package tabu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Solution {
    private int fleetSize;
    private int noOfCustomers;
    private Vehicle[] fleet;
    private double cost;

//    private int TABU_Matrix[][];

    private TabuList tabu;

    //Tabu Variables
    private Vehicle[] fleetForBestSolution;
    private double bestcost;


    public Solution(int CustNum, int VechNum, int VechCap) {
        this.fleetSize = VechNum;

        this.noOfCustomers = CustNum;

        this.cost = 0;

        fleet = new Vehicle[fleetSize];

        fleetForBestSolution = new Vehicle[fleetSize];

        tabu = new TabuList();

        for (int i = 0; i < fleetSize; i++) {
            fleet[i] = new Vehicle(i + 1, VechCap);
            fleetForBestSolution[i] = new Vehicle(i + 1, VechCap);
        }
    }


    private void firstIteration(Vertex[] vertices, double[][] costMatrix) {
        double Candcost, finalCost;

        int vechicleIndex = 0;

        while (Arrays.stream(vertices).filter(v -> !v.isDepot()).anyMatch(v -> !v.isRouted())) {

            int serviceIndex = 0;
            Vertex candidateVertex = null;
            double mincost = Double.MAX_VALUE;

            if (fleet[vechicleIndex].getRoute().isEmpty()) {

                fleet[vechicleIndex].addVertex(vertices[0]);

            }
            for (int i = 1; i <= noOfCustomers; i++) {

                if (!vertices[i].isRouted()
                        && fleet[vechicleIndex]
                        .isFit(vertices[i].getDemand())) {

                    Candcost = costMatrix[fleet[vechicleIndex].getCurLocation()][i];

                    if (mincost > Candcost) {
                        mincost = Candcost;
                        serviceIndex = i;
                        candidateVertex = vertices[i];
                    }
                }
            }
            if (candidateVertex == null) {
                //Not a single Customer Fits
                //We have more fleet to assign

                if (vechicleIndex + 1 < fleet.length) {
                    if (fleet[vechicleIndex].getCurLocation() != 0) { //End this route

                        finalCost = costMatrix[fleet[vechicleIndex].getCurLocation()][0];

                        fleet[vechicleIndex].addVertex(vertices[0]);

                        this.cost += finalCost;
                    }

                    vechicleIndex = vechicleIndex + 1; //Go to next Vehicle
                } else {//We DO NOT have any more vehicle to assign. The problem is unsolved under these parameters
                    System.err.println("It is impossible to resolve this problem with such conditions.");
                    System.exit(0);
                }
            } else {
                fleet[vechicleIndex].addVertex(candidateVertex); //If a fitting Customer is Found
                
                vertices[serviceIndex].setRouted(true);
                
                this.cost += mincost;
            }
        }
        finalCost = costMatrix[fleet[vechicleIndex].getCurLocation()][0];
        fleet[vechicleIndex].addVertex(vertices[0]);
        this.cost += finalCost;
    }


    public void tabuSearch(Vertex[] vertices, int TABU_Horizon, double[][] costMatrix, int numberOfIters) {

        firstIteration(vertices, costMatrix);

        //We use 1-0 exchange move
        ArrayList<Vertex> routeFrom;

        ArrayList<Vertex> routeTo;

        int mvNdDemand = 0;

        int vechicleIndexFrom, vechicleIndexTo;
        double bestCost, neightCost;

        int swapA = -1, swapB = -1, swapRtFrom = -1, swapRteTo = -1;

        int dimension = costMatrix[1].length;

        tabu.setTabuList(new int[dimension + 1][dimension + 1]);

        bestcost = this.cost; //Initial Solution cost

        for (int iteration = 0; iteration < numberOfIters; ++iteration) {

            bestCost = Double.MAX_VALUE;

            for (vechicleIndexFrom = 0; vechicleIndexFrom < this.fleet.length; vechicleIndexFrom++) {
                
                routeFrom = this.fleet[vechicleIndexFrom].getRoute();

                for (int i = 1; i < routeFrom.size() - 1; i++) { //Not possible to move depot!

                    for (vechicleIndexTo = 0; vechicleIndexTo < this.fleet.length; vechicleIndexTo++) {

                        routeTo = this.fleet[vechicleIndexTo].getRoute();

                        for (int j = 0;
                             j < routeTo.size() - 1; j++) { //Not possible to move after last Depot!

                            mvNdDemand = routeFrom.get(i).getDemand();

                            if ((vechicleIndexFrom == vechicleIndexTo) 
                                    || this.fleet[vechicleIndexTo].isFit(mvNdDemand)) {
                                //If we assign to a different route check capacity constrains
                                //if in the new route is the same no need to check for capacity

                                // Not a move that Changes solution cost
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
                                            costMatrix[routeFromStart][routeFromStartNeigh]
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

            routeFrom = this.fleet[swapRtFrom].getRoute();
            routeTo = this.fleet[swapRteTo].getRoute();
            this.fleet[swapRtFrom].setRoute(null);
            this.fleet[swapRteTo].setRoute(null);

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


            this.fleet[swapRtFrom]
                    .setRoute(routeFrom);
            
            this.fleet[swapRtFrom]
                    .setLoad(this.fleet[swapRtFrom].getLoad() - mvNdDemand);

            this.fleet[swapRteTo]
                    .setRoute(routeTo);
            
            
            this.fleet[swapRteTo]
                    .setLoad(this.fleet[swapRteTo].getLoad() + mvNdDemand);


            this.cost += bestCost;

            if (this.cost < bestcost) {
                saveBestSolution();
            }

        }

        this.fleet = fleetForBestSolution;
        this.cost = bestcost;

    }

    private void saveBestSolution() {
        bestcost = cost;
        for (int j = 0; j < fleetSize; j++) {

            fleetForBestSolution[j].getRoute().clear();

            if (!fleet[j].getRoute().isEmpty()) {

                int RoutSize = fleet[j].getRoute().size();

                for (int k = 0; k < RoutSize; k++) {

                    Vertex n = fleet[j].getRoute().get(k);

                    fleetForBestSolution[j].getRoute().add(n);
                }
            }
        }
    }

    public void print() {
        for (int j = 0; j < fleetSize; j++) {

            if (!fleet[j].getRoute().isEmpty()) {

                System.out.print("Vehicle id: " + fleet[j].getId() + ":");

                int RoutSize = fleet[j].getRoute().size();

                for (int k = 0; k < RoutSize; k++) {

                    if (k == RoutSize - 1) {

                        System.out.print(fleet[j].getRoute().get(k).getId());

                    } else {

                        System.out.print(fleet[j].getRoute().get(k).getId() + "->");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("\nTotal cost " + this.cost + "\n");
    }
}
