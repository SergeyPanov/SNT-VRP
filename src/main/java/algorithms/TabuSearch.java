package algorithms;

import environment.Environment;
import environment.Vehicle;
import environment.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Tabu search implementation.
 */
public class TabuSearch implements Algorithm {
    private TabuList tabu;
    private int numberOfIters;
    private int horizon;

    private Environment environment;
    private double totalBestCost;

    // Class variables to simplify some methods
    private int mvNdDemand = 0;
    private int swapA = -1, swapB = -1, swapRtFrom = -1, swapRtTo = -1;


    public TabuSearch(int numberOfIters, int horizon) {
        this.tabu = new TabuList();
        this.numberOfIters = numberOfIters;
        this.horizon = horizon;

    }

    /**
     * Check if tabu list does not reject to swap vertexes
     */
    private boolean checkTabu(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int i, int j){
        int routeFromStart = routeFrom.get(i - 1).getId();
        int routeFromEnd = routeFrom.get(i).getId();
        int routeFromStartNeigh = routeFrom.get(i + 1).getId();
        int routeToStart = routeTo.get(j).getId();
        int routeToEnd = routeTo.get(j + 1).getId();


        return  tabu.isInTabu(routeFromStart, routeFromStartNeigh) ||
                tabu.isInTabu(routeToStart, routeFromEnd) ||
                tabu.isInTabu(routeFromEnd, routeToEnd);
    }

    /**
     * Get cost of neighbour solution.
     */
    private  double getNeighbourCost(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int i, int j){

        int routeFromStart = routeFrom.get(i - 1).getId();
        int routeFromEnd = routeFrom.get(i).getId();
        int routeFromStartNeigh = routeFrom.get(i + 1).getId();
        int routeToStart = routeTo.get(j).getId();
        int routeToEnd = routeTo.get(j + 1).getId();

        return environment.getCostMatrix()[routeFromStart][routeFromStartNeigh]
                + environment.getCostMatrix()[routeToStart][routeFromEnd]
                + environment.getCostMatrix()[routeFromEnd][routeToEnd]
                - environment.getCostMatrix()[routeFromStart][routeFromEnd]
                - environment.getCostMatrix()[routeFromEnd][routeFromStartNeigh]
                - environment.getCostMatrix()[routeToStart][routeToEnd];
    }


    /**
     * Reorganize routes based on swapped places.
     */
    private void changeRoutes(ArrayList<Vertex> routeFrom,
                              ArrayList<Vertex> routeTo,
                              int swapRtFrom,
                              int swapRtTo,
                              int mvNdDemand
                              ){

        this.environment.getFleet().get(swapRtFrom)
                .setRoute(routeFrom);

        this.environment.getFleet().get(swapRtFrom)
                .setLoad(this.environment.getFleet().get(swapRtFrom).getLoad() - mvNdDemand);

        this.environment.getFleet().get(swapRtTo)
                .setRoute(routeTo);

        this.environment.getFleet().get(swapRtTo)
                .setLoad(this.environment.getFleet().get(swapRtTo).getLoad() + mvNdDemand);
    }


    /**
     * Takes route and based on neighborhood solutions choose the best.
     */
    private double innerIteration(ArrayList<Vertex> routeFrom,
                                int i,
                                int vechicleIndexFrom,
                                double bestCostOfIteration
                                ){
        for (int vechicleIndexTo = 0; vechicleIndexTo < this.environment.getFleet().size(); vechicleIndexTo++) {    // Look for the best vertex for relocation

            ArrayList<Vertex> routeTo = this.environment.getFleet().get(vechicleIndexTo).getRoute();
            for (int j = 0;
                 j < routeTo.size() - 1; j++) { //Not possible to move after last Depot!

                mvNdDemand = routeFrom.get(i).getDemand();

                if ((vechicleIndexFrom == vechicleIndexTo)
                        || this.environment.getFleet().get(vechicleIndexTo).isFit(mvNdDemand)) {    // Check if it possible to add vertex into the rout


                    if (!((vechicleIndexFrom == vechicleIndexTo) && ((j == i) || (j == i - 1)))) {

                        if (checkTabu(routeFrom, routeTo, i, j)){   // Check if relocation is not rejected by the tabu-list
                            break;
                        }

                        double neightCost = getNeighbourCost(routeFrom, routeTo, i, j); // Get cost of the new solution

                        if (neightCost < bestCostOfIteration) { // IF better solution was found apply it
                            bestCostOfIteration = neightCost;
                            swapA = i;
                            swapB = j;
                            swapRtFrom = vechicleIndexFrom;
                            swapRtTo = vechicleIndexTo;
                        }
                    }
                }
            }
        }
        return bestCostOfIteration;
    }

    /**
     * For each route find the best solution
     */
    private double singleIteration(double bestCostOfIteration){
        for (int vechicleIndexFrom = 0; vechicleIndexFrom < this.environment.getFleet().size(); vechicleIndexFrom++) {  // Take route

            ArrayList<Vertex> routeFrom = this.environment.getFleet().get(vechicleIndexFrom).getRoute();

            for (int i = 1; i < routeFrom.size() - 1; i++) { // Take vertex on the route

                bestCostOfIteration = innerIteration(routeFrom, i, vechicleIndexFrom, bestCostOfIteration); // Find best relocation

            }
        }
        return bestCostOfIteration;
    }

    /**
     * Swap vertexes on the route
     */
    private void swapVertexes(ArrayList<Vertex> routeTo, Vertex swapVertex){    // Add swapVertex to the route
        if (swapA < swapB) {
            routeTo.add(swapB, swapVertex);
        } else {
            routeTo.add(swapB + 1, swapVertex);
        }
    }

    /**
     * Starts execution.
     */
    @Override
    public Environment execute(Environment environment) {

        this.environment = environment;

        // Init Tabu list
        tabu.setTabuList(new int[this.environment.getCostMatrix()[1].length + 1][this.environment.getCostMatrix()[1].length + 1]);

        // Initial best cost is actual cost
        totalBestCost = this.environment.getCost();

        // Execute algorithm "numberOfIters" times
        for (int iteration = 0; iteration < numberOfIters; ++iteration) {

            ArrayList<Vertex> routeFrom;

            ArrayList<Vertex> routeTo;

            double bestCostOfIteration = this.singleIteration(Double.MAX_VALUE);    // Execute single iteration of algorithm

            tabu.decreaseTabu();    // Each iteration decrease value in tabu-list

            routeFrom = this.environment.getFleet().get(swapRtFrom).getRoute();
            routeTo = this.environment.getFleet().get(swapRtTo).getRoute();

            this.environment.getFleet().get(swapRtFrom).setRoute(null);
            this.environment.getFleet().get(swapRtTo).setRoute(null);

            Vertex swapVertex = routeFrom.get(swapA);

            tabu.setupDelays(routeFrom, routeTo, swapA, swapB, horizon);    // Setup delays of the tabu list with values [hoziron .. horizon + 5)
            routeFrom.remove(swapA);    // Remove vertex from the road

            if (swapRtFrom == swapRtTo) {
                swapVertexes(routeTo, swapVertex);
            } else {
                routeTo.add(swapB + 1, swapVertex);
            }
            changeRoutes(routeFrom, routeTo, swapRtFrom, swapRtTo, mvNdDemand); // Reconstruct routes based on swaped vertexes

            this.environment.setCost(this.environment.getCost() + bestCostOfIteration); // Set new cost

            if (this.environment.getCost() < totalBestCost) {   // If better solution was found save it
                saveBestSolution(this.environment);
            }

        }

        this.environment.setFleet(this.environment.getBestFleet());

        this.environment.setCost(totalBestCost);

        return this.environment;
    }

    /**
     * Save the best solution.
     */
    private void saveBestSolution(Environment environment) {

        Set<Vehicle> bestFleet = new HashSet<>();
        totalBestCost = environment.getCost();
        for (int i = 0; i < environment.getFleet().size(); i++) {

            if (!environment.getFleet().get(i).getRoute().isEmpty()) {
                bestFleet.add(environment.getFleet().get(i));
            }
        }
        environment.setBestFleet(new ArrayList<>());
        environment.getBestFleet().addAll(bestFleet);
    }
}
