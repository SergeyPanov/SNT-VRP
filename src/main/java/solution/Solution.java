package solution;

import algorithms.GreedySearch;
import algorithms.TabuSearch;

import java.util.*;

public class Solution {


    private Environment environment;
    private TabuList tabu;



    public Solution(Environment environment) {
        this.environment = environment;
        tabu = new TabuList();
    }

    /**
     * Execute greedy search
     */
    public void greedySearch(){
        GreedySearch greedySearch = new GreedySearch();
        environment = greedySearch.execute(environment);
    }

    /**
     * Execite TabuSearch
     * @param numberOfIters Stop condition
     */

    public void tabuSearch(int numberOfIters) {

        greedySearch();

        TabuSearch tabuSearch = new TabuSearch(tabu, numberOfIters, 10);
        environment = tabuSearch.execute(environment);

    }

    /**
     * Display tha best solution.
     */
    public void printBestRoute() {
        environment.getFleet()
                .sort(Comparator.comparing(Vehicle::getId));

        environment.getFleet().forEach(v -> {
            if (v.getRoute().size() > 2){
                System.out.print("Vehicle: " + v.getId() + " route: ");

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

}
