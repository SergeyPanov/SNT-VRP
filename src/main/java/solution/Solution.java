package solution;

import algorithms.GreedySearch;
import algorithms.NovelAlgorithm;
import algorithms.TabuSearch;

import java.util.*;

public class Solution {


    private Environment environment;
    private TabuList tabu;



    public Solution(Environment environment) {
        this.environment = environment;
        tabu = new TabuList();
    }


    public void tabuSearch(int TABU_Horizon, int numberOfIters) {
        GreedySearch greedySearch = new GreedySearch();
        environment = greedySearch.execute(environment);
//        printBestRoute();
        NovelAlgorithm novelAlgorithm = new NovelAlgorithm();
        environment = novelAlgorithm.execute(environment);
        printBestRoute();
//        TabuSearch tabuSearch = new TabuSearch(tabu, numberOfIters, TABU_Horizon);
//        environment = tabuSearch.execute(environment);

    }


    public void printBestRoute() {
        environment.getBestFleet()
                .sort(Comparator.comparing(Vehicle::getId));

        environment.getBestFleet().forEach(v -> {
            System.out.print("Vehicle: " + v.getId() + " route: " + " load: " +v.getLoad() );

            System.out.print("(id: " + v.getRoute().get(0).getId()
                    + " oX: " + v.getRoute().get(0).getoX() + " oY: "
                    + v.getRoute().get(0).getoY() + " )->");

            v.getRoute().subList(1, v.getRoute().size() - 1).forEach(place ->{
                        System.out.print("(id: " + place.getId() + " oX: " + place.getoX() + " oY: " + place.getoY() + ")->");
                    }
                    );
            System.out.println("(id: " + v.getRoute().get(v.getRoute().size() - 1).getId()
                    + " oX: " + v.getRoute().get(v.getRoute().size() - 1).getoX() + " oY: "
                    + v.getRoute().get(v.getRoute().size() - 1).getoY() + ")");
        });

        System.out.println("\nTotal cost: " + environment.getCost());

    }

}
