package tabu;

import java.util.*;

public class Solution {
    private final int DEPO = 0;

    private double cost;


    private Environment environment;
    private TabuList tabu;


    private double bestcost;


    public Solution(Environment environment) {

        this.environment = environment;
        this.cost = 0;

        tabu = new TabuList();

    }


    private void firstIteration(Vertex[] vertices, double[][] costMatrix) {
        double candidateCost, finalCost;

        int vechicleIndex = 0;

        while (Arrays.stream(environment.getVertices()).filter(v -> !v.isDepot()).anyMatch(v -> !v.isRouted())) {

            int serviceIndex = 0;
            Vertex candidateVertex = null;
            double mincost = Double.MAX_VALUE;

            if (environment.getFleet().size() == vechicleIndex){
                environment.getFleet().add(new Vehicle(environment.getFleet().size(), environment.getCapacity()));
            }

            if (environment.getFleet().get(vechicleIndex).getRoute().isEmpty()){
                environment.getFleet().get(vechicleIndex).addVertex(vertices[DEPO]);
            }

            for (int i = 1; i <= environment.getNumbOfCustomers(); i++) {

                if (!environment.getVertices()[i].isRouted()
                        
                        && environment.getFleet().get(vechicleIndex)
                        .isFit(environment.getVertices()[i].getDemand())) {

                    candidateCost = environment.getCostMatrix()[environment.getFleet().get(vechicleIndex).getCurLocation()][i];

                    if (mincost > candidateCost) {
                        mincost = candidateCost;
                        serviceIndex = i;
                        candidateVertex = vertices[i];
                    }
                }
            }


            if (candidateVertex == null) {
                //Not a single Customer Fits
                //We have more fleet to assign

                if (environment.getFleet().get(vechicleIndex).getCurLocation() != 0) { //End this route

                    finalCost = environment.getCostMatrix()[environment.getFleet().get(vechicleIndex).getCurLocation()][0];


                    environment.getFleet().get(vechicleIndex).addVertex(vertices[DEPO]);

                    this.cost += finalCost;
                }

                vechicleIndex = vechicleIndex + 1; //Go to next Vehicle

            } else {
                environment.getFleet().get(vechicleIndex).addVertex(candidateVertex); //If a fitting Customer is Found
                environment.getVertices()[serviceIndex].setRouted(true);
                this.cost += mincost;
            }

        }
        finalCost = costMatrix[environment.getFleet().get(vechicleIndex).getCurLocation()][0];
        environment.getFleet().get(vechicleIndex).addVertex(vertices[DEPO]);
        this.cost += finalCost;
    }


    public void tabuSearch(Vertex[] vertices, int TABU_Horizon, double[][] costMatrix, int numberOfIters) {

        firstIteration(vertices, costMatrix);

        ArrayList<Vertex> routeFrom;

        ArrayList<Vertex> routeTo;

        int mvNdDemand = 0;

        int vechicleIndexFrom, vechicleIndexTo;
        double bestCost, neightCost;

        int swapA = -1, swapB = -1, swapRtFrom = -1, swapRteTo = -1;

        int dimension = environment.getCostMatrix()[1].length;

        tabu.setTabuList(new int[dimension + 1][dimension + 1]);

        bestcost = this.cost; //Initial Solution cost

        for (int iteration = 0; iteration < numberOfIters; ++iteration) {

            bestCost = Double.MAX_VALUE;

            for (vechicleIndexFrom = 0; vechicleIndexFrom < environment.getFleet().size(); vechicleIndexFrom++) {


                routeFrom = environment.getFleet().get(vechicleIndexFrom).getRoute();


                for (int i = 1; i < routeFrom.size() - 1; i++) { //Not possible to move depot!

                    for (vechicleIndexTo = 0; vechicleIndexTo < environment.getFleet().size(); vechicleIndexTo++) {


                        routeTo = environment.getFleet().get(vechicleIndexTo).getRoute();


                        for (int j = 0;
                             j < routeTo.size() - 1; j++) { //Not possible to move after last Depot!

                            mvNdDemand = routeFrom.get(i).getDemand();

                            if ((vechicleIndexFrom == vechicleIndexTo)
                                    || environment.getFleet().get(vechicleIndexTo).isFit(mvNdDemand)) {



                                if (!((vechicleIndexFrom == vechicleIndexTo) && ((j == i) || (j == i - 1)))) {

                                    int routeFromStart = routeFrom.get(i - 1).getId();
                                    int routeFromEnd = routeFrom.get(i).getId();
                                    int routeFromStartNeigh = routeFrom.get(i + 1).getId();
                                    int routeToStart = routeTo.get(j).getId();
                                    int routeToEnd = routeTo.get(j + 1).getId();

                                    if (
                                            tabu.isInTabu(routeFromStart, routeFromStartNeigh) ||
                                                    tabu.isInTabu(routeToStart, routeFromEnd) ||
                                                    tabu.isInTabu(routeFromEnd, routeToEnd)
                                            ){
                                        break;
                                    }

                                    neightCost =
                                            environment.getCostMatrix()[routeFromStart][routeFromStartNeigh]
                                                    + costMatrix[routeToStart][routeFromEnd]
                                                    + costMatrix[routeFromEnd][routeToEnd]
                                                    - costMatrix[routeFromStart][routeFromEnd]
                                                    - costMatrix[routeFromEnd][routeFromStartNeigh]
                                                    - costMatrix[routeToStart][routeToEnd];


                                    if (neightCost < bestCost) {
                                        bestCost = neightCost;
                                        swapA = i;
                                        swapB = j;
                                        swapRtFrom = vechicleIndexFrom;
                                        swapRteTo = vechicleIndexTo;
                                    }
                                }
                            }
                        }
                    }
                }

            }

            tabu.decreaseTabu();

            routeFrom = environment.getFleet().get(swapRtFrom).getRoute();
            routeTo = environment.getFleet().get(swapRteTo).getRoute();
            environment.getFleet().get(swapRtFrom).setRoute(null);
            environment.getFleet().get(swapRteTo).setRoute(null);


            Vertex swapVertex = routeFrom.get(swapA);

            int ndIdBefore = routeFrom.get(swapA - 1).getId();
            int ndIdAfter = routeFrom.get(swapA + 1).getId();
            int ndIdF = routeTo.get(swapB).getId();
            int ndIdG = routeTo.get(swapB + 1).getId();

            Random tbRandomChanger = new Random();
            int delay1 = tbRandomChanger.nextInt(20);
            int delay2 = tbRandomChanger.nextInt(20);
            int delay3 = tbRandomChanger.nextInt(20);

            tabu.getTabuList()[ndIdBefore][swapVertex.getId()] = TABU_Horizon + delay1;
            tabu.getTabuList()[swapVertex.getId()][ndIdAfter] = TABU_Horizon + delay2;
            tabu.getTabuList()[ndIdF][ndIdG] = TABU_Horizon + delay3;

            routeFrom.remove(swapA);

            if (swapRtFrom == swapRteTo) {
                if (swapA < swapB) {
                    routeTo.add(swapB, swapVertex);
                } else {
                    routeTo.add(swapB + 1, swapVertex);
                }
            } else {
                routeTo.add(swapB + 1, swapVertex);
            }


            environment.getFleet().get(swapRtFrom)
                    .setRoute(routeFrom);

            environment.getFleet().get(swapRtFrom)
                    .setLoad(environment.getFleet().get(swapRtFrom).getLoad() - mvNdDemand);

            environment.getFleet().get(swapRteTo)
                    .setRoute(routeTo);


            environment.getFleet().get(swapRteTo)
                    .setLoad(environment.getFleet().get(swapRteTo).getLoad() + mvNdDemand);


            this.cost += bestCost;

            if (this.cost < bestcost) {
                saveBestSolution();
            }

        }

        environment.setFleet(environment.getBestFleet());

        this.cost = bestcost;

    }
    private void saveBestSolution() {

        Set<Vehicle> bestFleet = new HashSet<>();
        bestcost = cost;
        for (int i = 0; i < environment.getFleet().size(); i++) {

            if (!environment.getFleet().get(i).getRoute().isEmpty()) {
                bestFleet.add(environment.getFleet().get(i));
            }
        }
        environment.setBestFleet(new ArrayList<>());
        environment.getBestFleet().addAll(bestFleet);
    }


    public void print() {


        environment.getBestFleet()
                .sort(Comparator.comparing(Vehicle::getId));

        environment.getBestFleet().forEach(v -> {
            System.out.print("Vehicle: " + v.getId() + " route: ");

            System.out.print("(id: " + v.getRoute().get(0).getId()
                    + " oX: " + v.getRoute().get(0).getoX() + " oY: "
                    + v.getRoute().get(0).getoY() + ")->");

            v.getRoute().subList(1, v.getRoute().size() - 1).forEach(place -> {
                System.out.print("(id: " + place.getId() + " oX: " + place.getoX() + " oY: " + place.getoY() + ")->");
            });

            System.out.println("(id: " + v.getRoute().get(v.getRoute().size() - 1).getId()
                    + " oX: " + v.getRoute().get(v.getRoute().size() - 1).getoX() + " oY: "
                    + v.getRoute().get(v.getRoute().size() - 1).getoY() + ")");
        });

        System.out.println("\nTotal cost: " + cost);

    }

}
