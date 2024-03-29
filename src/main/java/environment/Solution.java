package environment;

import algorithms.GreedySearch;
import algorithms.ModifiedNovelSearch;
import org.knowm.xchart.*;


import java.io.IOException;
import java.util.*;

public class Solution {
    private Environment environment;

    public Solution(Environment environment) {
        this.environment = environment;
    }

    /**
     * Execute greedy search
     */
    public void greedySearch(){
        GreedySearch greedySearch = new GreedySearch();
        environment = greedySearch.execute(environment);
    }

    /**
     * Execute ModifiedNovelSearch
     */
    public void tabuSearch(int numberOfIters, int horizon, int distance) {
        ModifiedNovelSearch modifiedNovelSearch = new ModifiedNovelSearch( numberOfIters, horizon, distance);
        environment = modifiedNovelSearch.execute(environment);

    }

    /**
     * Display tha best solution.
     */
    public void printBestRoute() {
        environment.getFleet()
                .sort(Comparator.comparing(Vehicle::getId));

        int totalDemand = Arrays.stream(environment.getVertices()).mapToInt(Vertex::getDemand).sum();
        System.out.println("Total demand: " + totalDemand );

        environment.getFleet().forEach(v -> {
            if (v.getRoute().size() > 2){
                int sum = v.getRoute().stream().mapToInt(Vertex::getDemand).sum();
                System.out.print("Vehicle: " + v.getId() + " vehicle capacity: " + v.getCapacity() + " load:" + sum + " route: ");

                System.out.print("(id: " + v.getRoute().get(0).getId()
                        + " oX: " + v.getRoute().get(0).getoX() + " oY: "
                        + v.getRoute().get(0).getoY() + ")->");

                v.getRoute().subList(1, v.getRoute().size() - 1).forEach(place ->
                        System.out.print("(id: " + place.getId() + " oX: " + place.getoX() + " oY: " + place.getoY() + ")->"));

                System.out.println("(id: " + v.getRoute().get(v.getRoute().size() - 1).getId()
                        + " oX: " + v.getRoute().get(v.getRoute().size() - 1).getoX() + " oY: "
                        + v.getRoute().get(v.getRoute().size() - 1).getoY() + ")");
            }

        });

        System.out.println("\nTotal cost: " + environment.getCost());
    }

    /**
     * Plot graph of routes.
     */
    public void plot(String algorithmName) throws IOException {

        List<Double> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();

        environment.getFleet()
                .sort(Comparator.comparing(Vehicle::getId));


        XYChart chart = new XYChartBuilder().width(600).height(400).title("Total cost: " + environment.getCost()).xAxisTitle("X").yAxisTitle("Y").build();


        environment.getFleet().forEach(v -> {
            if (v.getRoute().size() > 2){
                for (int i = 0; i < v.getRoute().size(); i++) {
                     xData.add((double) v.getRoute().get(i).getoX());
                     yData.add((double) v.getRoute().get(i).getoY());
                }

                chart.addSeries(String.valueOf(v.getId()), xData, yData);

                xData.clear();
                yData.clear();

            }

        });


        BitmapEncoder.saveBitmapWithDPI(chart, environment.getName() + "_" + algorithmName + "_" + environment.getCost(), BitmapEncoder.BitmapFormat.PNG, 300);


    }

}
