package algorithms;

import solution.Environment;
import solution.TabuList;
import solution.Vehicle;
import solution.Vertex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class NovelAlgorithm implements Algorithm{

    private Environment environment;

    private double totalFitness;
    private double bestCost;

    private TabuList tabu = new TabuList();

    public static Object deepClone(Object object) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    // Calculate fitness function for vehicle v
    private double getFitnessForVehicle(Vehicle v){
        double vehicleFitness = 0;
        for (int i = 0; i < v.getRoute().size() - 1; i++) {
            int fromId = v.getRoute().get(i).getId();
            for (int j = 0; j < v.getRoute().size() - 1; j++) {
                if (i == j) continue;
                int toId = v.getRoute().get(j).getId();
                vehicleFitness += environment.getCostMatrix()[fromId][toId];
            }
        }
        return vehicleFitness;
    }

    // Calculate total fitness function
    private void calculateTotalFitness(){
        environment.getFleet().forEach(v -> totalFitness += getFitnessForVehicle(v));
        
    }

    private Vertex placeChooser(Vehicle v){
        double value = (new Random().nextDouble()) * getFitnessForVehicle(v);

        for (int i = 0; i < v.getRoute().size() - 1; i++) {
            int fromId = v.getRoute().get(i).getId();
            for (int j = 0; j < v.getRoute().size() - 1; j++) {
                if (i == j) continue;
                int toId = v.getRoute().get(j).getId();
                value -= environment.getCostMatrix()[fromId][toId];
                if (value < 0) return v.getRoute().get(i);
            }
        }
        return v.getRoute().get(environment.getDEPO());
    }

    private Optional<Vertex> findSecondClosest(Vertex v){
        double min1 = Double.MAX_VALUE;
        double min2 = Double.MAX_VALUE;

        int minIndex1 = v.getId();
        int minIndex2 = v.getId();

        for (int i = 0; i < environment.getCostMatrix()[v.getId()].length; i++) {


            if (i != v.getId()){
                if (environment.getCostMatrix()[v.getId()][i] < min1){
                    min2 = min1;
                    minIndex2 = minIndex1;

                    min1 = environment.getCostMatrix()[v.getId()][i];
                    minIndex1 = i;
                }else if (environment.getCostMatrix()[v.getId()][i] < min2){
                    min2 = environment.getCostMatrix()[v.getId()][i];
                    minIndex2 = i;
                }
            }

        }
        int finalMinIndex = minIndex2;
        return Arrays.stream(environment.getVertices()).filter(vertex -> vertex.getId() == finalMinIndex).findFirst();
    }
    
    private boolean isOnTheRoad(Vehicle vehicle, Vertex vertex){
        return vehicle.getRoute().stream().anyMatch(v -> v.getId() == vertex.getId());
    }

    private void swapVertexes(Vertex chosen, Vertex closest){

        Optional<Vehicle> v1 = environment.getFleet().stream().filter(v -> v.getRoute().contains(chosen)).findFirst();
        Optional<Vehicle> v2 = environment.getFleet().stream().filter(v -> v.getRoute().contains(closest)).findFirst();

        int vertexIndexA = v1.get().getRoute().indexOf(chosen);
        int vertexIndexB = v2.get().getRoute().indexOf(closest);

        if (v1.get().isFit(closest.getDemand() - chosen.getDemand() )
                && v2.get().isFit(chosen.getDemand() - closest.getDemand() )
                && !chosen.isDepot()
                && !closest.isDepot()){
//        if (!chosen.isDepot()
//                && !closest.isDepot()){
//

            v1.get().setLoad(v1.get().getLoad() + (closest.getDemand() - chosen.getDemand()));
            v1.get().getRoute().set(vertexIndexA, closest);

            v2.get().setLoad(v2.get().getLoad() + (chosen.getDemand() - closest.getDemand()));
            v2.get().getRoute().set(vertexIndexB, chosen);
        }
    }


    @Override
    public Environment execute(Environment environment) {
        this.environment = environment;
//        calculateTotalFitness();

        tabu.setTabuList(new int[this.environment.getCostMatrix()[1].length + 1][this.environment.getCostMatrix()[1].length + 1]);
        saveBestSolution();
        bestCost = environment.calculateRouteCost(environment.getFleet());
        for (int i = 0; i < 1000; i++) {

            // Stage 1. Select one of each point i from each routes Rk* by roulette wheel principle
            ArrayList<Vertex> chosenVertexes = new ArrayList<>();
            for (Vehicle vehicle:
                 environment.getFleet()) {
                chosenVertexes.add(placeChooser(vehicle));
            }

            // Stage 2. Determine the second closest points to each selected points according to the Distance matrix
            ArrayList<Vertex> closestVertexes = new ArrayList<>();
            for (Vertex v:
                 chosenVertexes) {
                closestVertexes.add(findSecondClosest(v).get());
            }

            // Stage 3. Relocate the selected point with its second closest point.
            // If the selected point and its second point are in the same route, do not relocate them.
            // So we can prevent to enhance the solution space too much.
            for (Vehicle vehicle:
                 environment.getFleet()) {
                for (int j = 0; j < chosenVertexes.size(); j++) {
//                    if (isOnTheRoad(vehicle, chosenVertexes.get(j)) && !isOnTheRoad(vehicle, closestVertexes.get(j))){
                        swapVertexes(chosenVertexes.get(j), closestVertexes.get(j));
//                    }
                }
            }
            if (bestCost > this.environment.calculateRouteCost(this.environment.getFleet())){
                saveBestSolution();
            }
        }
        this.environment.setCost(this.environment.calculateRouteCost(this.environment.getBestFleet()));
        return this.environment;
    }

    private void saveBestSolution() {

        ArrayList<Vehicle> bestFleet = new ArrayList<>();
        bestCost = this.environment.calculateRouteCost(this.environment.getFleet());


        for (int i = 0; i < environment.getFleet().size(); i++) {
            ArrayList<Vertex> aux = new ArrayList<>(environment.getFleet().get(i).getRoute());

            if (!environment.getFleet().get(i).getRoute().isEmpty()) {

                Vehicle v = (Vehicle) deepClone(environment.getFleet().get(i));
                v.setRoute(new ArrayList<>());
                v.getRoute().addAll(aux);
                bestFleet.add(v);
            }
        }
        environment.setBestFleet(bestFleet);
    }
}
