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

    private Strategy strategy = Strategy.NONE;

    private int distance;

    public TabuSearch(int numberOfIters, int horizon, int distance) {
        this.tabu = new TabuList();
        this.numberOfIters = numberOfIters;
        this.horizon = horizon;
        this.distance = distance;
    }

    /**
     * Check if tabu list does not reject relocate vertex i from "routeFrom" to "routeTo"
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
     * Calculate delta cost of the solution after placing place "i" between "j" and "j+1".
     * If value is negative -> improve the solution
     * If value is positive -> make current solution worse
     */
    private  double getRelocateNeighCost(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int i, int j){

        int routeFromPrev = routeFrom.get(i - 1).getId();
        int routeFromCurr = routeFrom.get(i).getId();
        int routeFromNext = routeFrom.get(i + 1).getId();

        int routeToCurr = routeTo.get(j).getId();
        int routeToNext = routeTo.get(j + 1).getId();

        return environment.getCostMatrix()[routeFromPrev][routeFromNext]
                + environment.getCostMatrix()[routeToCurr][routeFromCurr]
                + environment.getCostMatrix()[routeFromCurr][routeToNext]

                - environment.getCostMatrix()[routeFromPrev][routeFromCurr]
                - environment.getCostMatrix()[routeFromCurr][routeFromNext]
                - environment.getCostMatrix()[routeToCurr][routeToNext];
    }

    /**
     * Get delta after swap vertex i and vertex j
     */
    private double getSwapNeighCost(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int i, int j){

        int idPrevI = routeFrom.get(i - 1).getId();
        int idNextI = routeFrom.get(i + 1).getId();
        int idI = routeFrom.get(i).getId();

        int idPrevJ = routeTo.get(j - 1).getId();
        int idNextJ = routeTo.get(j + 1).getId();
        int idJ = routeTo.get(j).getId();

        return environment.getCostMatrix()[idPrevI][idJ]
                + environment.getCostMatrix()[idJ][idNextI]
                - environment.getCostMatrix()[idPrevI][idI]
                - environment.getCostMatrix()[idI][idNextI]
                + environment.getCostMatrix()[idPrevJ][idI]
                + environment.getCostMatrix()[idI][idNextJ]
                - environment.getCostMatrix()[idPrevJ][idJ]
                - environment.getCostMatrix()[idJ][idNextJ];
    }


    /**
     * Reorganize routes based on replaced vertex
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

    private boolean canBeSwapped(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int i, int j){

        int costFromAfterExchange = routeFrom.stream().mapToInt(Vertex::getDemand).sum();
        costFromAfterExchange += routeTo.get(j).getDemand() - routeFrom.get(i).getDemand();

        int costToAfterExchange = routeTo.stream().mapToInt(Vertex::getDemand).sum();
        costToAfterExchange += routeFrom.get(i).getDemand() - routeTo.get(j).getDemand();


        return (  (costFromAfterExchange <= environment.getCapacity()) &&  (costToAfterExchange <= environment.getCapacity())  );

    }

    /**
     * Search new best location for vertex "i" between vertexes "j" and "j+1"
     */
    private double innerIteration(ArrayList<Vertex> routeFrom,
                                int i,
                                int vechicleIndexFrom,
                                double bestCostOfIteration
                                ){
        for (int vechicleIndexTo = 0; vechicleIndexTo < this.environment.getFleet().size(); vechicleIndexTo++) {

            ArrayList<Vertex> routeTo = this.environment.getFleet().get(vechicleIndexTo).getRoute();
            for (int j = 0;
                 j < routeTo.size() - 1; j++) { //Not possible to move after last Depot!

                mvNdDemand = routeFrom.get(i).getDemand();

                //If we assign to a different route check capacity constrains
                //if in the new route is the same no need to check for capacity
                if ((vechicleIndexFrom == vechicleIndexTo)
                        || this.environment.getFleet().get(vechicleIndexTo).isFit(mvNdDemand)) {

                    if (!((vechicleIndexFrom == vechicleIndexTo) && ((j == i) || (j == i - 1)))) {

                        if (checkTabu(routeFrom, routeTo, i, j)){   // Check if relocation is not rejected by the tabu-list
                            break;
                        }

                        double neighCost = getRelocateNeighCost(routeFrom, routeTo, i, j); // Get cost of the new solution

                        if (neighCost < bestCostOfIteration) { // IF better solution was found apply it
                            bestCostOfIteration = neighCost;
                            strategy = Strategy.RELOCATE;
                            // Vertex "swapA" will be placed between "swapB" and "swapB+1"
                            swapA = i;
                            swapB = j;
                            swapRtFrom = vechicleIndexFrom; // Route contains vertex "swapA"
                            swapRtTo = vechicleIndexTo; // Route contains vertex "swapB"
                        }
                    }
                }


                // Try to execute swap operation. Depot cant be swapped.
                if (vechicleIndexFrom != vechicleIndexTo && i != j && routeFrom.get(i).getId() != environment.getDEPO() && routeTo.get(j).getId() != environment.getDEPO() && canBeSwapped(routeFrom, routeTo, i, j)

                        ){

                    double swapBestCost = getSwapNeighCost(routeFrom, routeTo, i, j);

                    if (swapBestCost < bestCostOfIteration){

                        bestCostOfIteration = swapBestCost;

                        strategy = Strategy.SWAP;

                        swapA = i;
                        swapB = j;

                        swapRtFrom = vechicleIndexFrom; // Route contains vertex "swapA"
                        swapRtTo = vechicleIndexTo; // Route contains vertex "swapB"
                    }

                }

            }
        }
        return bestCostOfIteration;
    }

    /**
     * For each route find the best place
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
     * Relocate vertex on the same route
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

            double bestCostOfIteration = this.singleIteration(Double.MAX_VALUE);    // Execute single iteration of algorithm. Get delta.

            tabu.decreaseTabu();    // Each iteration decrease value in tabu-list

            routeFrom = this.environment.getFleet().get(swapRtFrom).getRoute(); // Get route with vertex swapA
            routeTo = this.environment.getFleet().get(swapRtTo).getRoute(); // Get route with vertex swapB

            this.environment.getFleet().get(swapRtFrom).setRoute(null);
            this.environment.getFleet().get(swapRtTo).setRoute(null);

            Vertex swapVertex = routeFrom.get(swapA);


            // If relocation was the best decision
            if (strategy == Strategy.RELOCATE){
                tabu.setupRelocationDelays(routeFrom, routeTo, swapA, swapB, horizon, distance);    // Add edges into Tabu-list. They will be placed in the tabu-list [horizon..horizon+distance) iterations
                routeFrom.remove(swapA);    // Remove vertex from the road

                // Place vertex swapA on the better position
                if (swapRtFrom == swapRtTo) {
                    swapVertexes(routeTo, swapVertex);
                } else {
                    routeTo.add(swapB + 1, swapVertex);
                }
                changeRoutes(routeFrom, routeTo, swapRtFrom, swapRtTo, mvNdDemand); // Reconstruct routes based on relocated vertex
            }

            // If swap was the best decision
            if (strategy == Strategy.SWAP){
                tabu.setupSwapDelays(routeFrom, routeTo, swapA, swapB, horizon, distance);
                Vertex swapVertexB = routeTo.get(swapB);
                routeTo.set(swapB, swapVertex);
                routeFrom.set(swapA, swapVertexB);

                environment.getFleet().get(swapRtFrom).setRoute(routeFrom);
                environment.getFleet().get(swapRtTo).setRoute(routeTo);
            }

            this.environment.setCost(this.environment.getCost() + bestCostOfIteration); // Set new cost


            this.environment.calculateCost();

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
