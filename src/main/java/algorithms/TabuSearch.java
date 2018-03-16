package algorithms;

import solution.Environment;
import solution.TabuList;
import solution.Vehicle;
import solution.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class TabuSearch implements Algorithm {
    private TabuList tabu;
    private int numberOfIters;
    private int horizon;

    private Environment environment;
    private double totalBestCost;

    // Class variables to simplify some methods
    private int mvNdDemand = 0;
    private int swapA = -1, swapB = -1, swapRtFrom = -1, swapRtTo = -1;


    public TabuSearch(TabuList tabu, int numberOfIters, int horizon) {
        this.tabu = tabu;
        this.numberOfIters = numberOfIters;
        this.horizon = horizon;
    }

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

                if ((vechicleIndexFrom == vechicleIndexTo)
                        || this.environment.getFleet().get(vechicleIndexTo).isFit(mvNdDemand)) {


                    if (!((vechicleIndexFrom == vechicleIndexTo) && ((j == i) || (j == i - 1)))) {

                        if (checkTabu(routeFrom, routeTo, i, j)){
                            break;
                        }

                        double neightCost = getNeighbourCost(routeFrom, routeTo, i, j);

                        if (neightCost < bestCostOfIteration) {
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

    private double singleIteration(double bestCostOfIteration){
        for (int vechicleIndexFrom = 0; vechicleIndexFrom < this.environment.getFleet().size(); vechicleIndexFrom++) {

            ArrayList<Vertex> routeFrom = this.environment.getFleet().get(vechicleIndexFrom).getRoute();

            for (int i = 1; i < routeFrom.size() - 1; i++) { //Not possible to move depot!

                bestCostOfIteration = innerIteration(routeFrom, i, vechicleIndexFrom, bestCostOfIteration);

            }
        }
        return bestCostOfIteration;
    }

    private void swapRoutes(ArrayList<Vertex> routeTo, Vertex swapVertex){
        if (swapA < swapB) {
            routeTo.add(swapB, swapVertex);
        } else {
            routeTo.add(swapB + 1, swapVertex);
        }
    }

    @Override
    public Environment execute(Environment environment) {

        this.environment = environment;

        tabu.setTabuList(new int[this.environment.getCostMatrix()[1].length + 1][this.environment.getCostMatrix()[1].length + 1]);

        totalBestCost = this.environment.getCost();

        // Certain amount of iterations
        for (int iteration = 0; iteration < numberOfIters; ++iteration) {

            ArrayList<Vertex> routeFrom;

            ArrayList<Vertex> routeTo;

            double bestCostOfIteration = this.singleIteration(Double.MAX_VALUE);    // Execute single iteration of algorithm

            tabu.decreaseTabu();    // Each iteration decrease value in tabu-list

            routeFrom = this.environment.getFleet().get(swapRtFrom).getRoute(); // Just alias for convenience
            routeTo = this.environment.getFleet().get(swapRtTo).getRoute(); // Just alias for convenience

            this.environment.getFleet().get(swapRtFrom).setRoute(null);
            this.environment.getFleet().get(swapRtTo).setRoute(null);

            Vertex swapVertex = routeFrom.get(swapA);

            tabu.setupDelays(routeFrom, routeTo, swapA, swapB, horizon);
            
            routeFrom.remove(swapA);

            if (swapRtFrom == swapRtTo) {
                swapRoutes(routeTo, swapVertex);
            } else {
                routeTo.add(swapB + 1, swapVertex);
            }

            changeRoutes(routeFrom, routeTo, swapRtFrom, swapRtTo, mvNdDemand);

            this.environment.setCost(this.environment.getCost() + bestCostOfIteration);

            if (this.environment.getCost() < totalBestCost) {
                saveBestSolution(this.environment);
            }
        }
        this.environment.setFleet(this.environment.getBestFleet());

        this.environment.setCost(totalBestCost);

        return this.environment;
    }

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
