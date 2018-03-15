package algorithms;

import solution.Environment;
import solution.TabuList;
import solution.Vehicle;
import solution.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class TabuSearch implements Algorithm {
    private TabuList tabu;
    private int numberOfIters;
    private int horizon;

    private double bestcost;

    public TabuSearch(TabuList tabu, int numberOfIters, int horizon) {
        this.tabu = tabu;
        this.numberOfIters = numberOfIters;
        this.horizon = horizon;
    }

    @Override
    public Environment execute(Environment environment) {
        ArrayList<Vertex> routeFrom;

        ArrayList<Vertex> routeTo;

        int mvNdDemand = 0;

        int vechicleIndexFrom, vechicleIndexTo;
        double bestCost, neightCost;

        int swapA = -1, swapB = -1, swapRtFrom = -1, swapRteTo = -1;

        int dimension = environment.getCostMatrix()[1].length;

        tabu.setTabuList(new int[dimension + 1][dimension + 1]);

        bestcost = environment.getCost(); //Initial Solution cost

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
                                                    + environment.getCostMatrix()[routeToStart][routeFromEnd]
                                                    + environment.getCostMatrix()[routeFromEnd][routeToEnd]
                                                    - environment.getCostMatrix()[routeFromStart][routeFromEnd]
                                                    - environment.getCostMatrix()[routeFromEnd][routeFromStartNeigh]
                                                    - environment.getCostMatrix()[routeToStart][routeToEnd];


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

            tabu.getTabuList()[ndIdBefore][swapVertex.getId()] = horizon + delay1;
            tabu.getTabuList()[swapVertex.getId()][ndIdAfter] = horizon + delay2;
            tabu.getTabuList()[ndIdF][ndIdG] = horizon + delay3;

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

            environment.setCost(environment.getCost() + bestCost);

            if (environment.getCost() < bestcost) {
                saveBestSolution(environment);
            }
        }
        environment.setFleet(environment.getBestFleet());

        environment.setCost(bestcost);

        return environment;
    }

    private void saveBestSolution(Environment environment) {

        Set<Vehicle> bestFleet = new HashSet<>();
        bestcost = environment.getCost();
        for (int i = 0; i < environment.getFleet().size(); i++) {

            if (!environment.getFleet().get(i).getRoute().isEmpty()) {
                bestFleet.add(environment.getFleet().get(i));
            }
        }
        environment.setBestFleet(new ArrayList<>());
        environment.getBestFleet().addAll(bestFleet);
    }
}
