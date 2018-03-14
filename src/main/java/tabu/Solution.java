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
        double Candcost, Endcost;
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
                        Endcost = costMatrix[fleet[vechicleIndex].getCurLocation()][0];
                        fleet[vechicleIndex].addVertex(vertices[0]);
                        this.cost += Endcost;
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
        Endcost = costMatrix[fleet[vechicleIndex].getCurLocation()][0];
        fleet[vechicleIndex].addVertex(vertices[0]);
        this.cost += Endcost;
    }


    public void tabuSearch(Vertex[] vertices, int TABU_Horizon, double[][] costMatrix, int numberOfIters) {

        firstIteration(vertices, costMatrix);

        //We use 1-0 exchange move
        ArrayList<Vertex> RouteFrom;
        ArrayList<Vertex> RouteTo;

        int MovingNodeDemand = 0;

        int vechicleIndexFrom, vechicleIndexTo;
        double BestNcost, Neigthboorcost;

        int SwapIndexA = -1, SwapIndexB = -1, SwapRouteFrom = -1, SwapRouteTo = -1;

        int DimensionCustomer = costMatrix[1].length;

        tabu.setTabuList(new int[DimensionCustomer + 1][DimensionCustomer + 1]);

        bestcost = this.cost; //Initial Solution cost

        for (int iteration = 0; iteration < numberOfIters; ++iteration) {

            BestNcost = Double.MAX_VALUE;

            for (vechicleIndexFrom = 0; vechicleIndexFrom < this.fleet.length; vechicleIndexFrom++) {
                RouteFrom = this.fleet[vechicleIndexFrom].getRoute();

                for (int i = 1; i < RouteFrom.size() - 1; i++) { //Not possible to move depot!

                    for (vechicleIndexTo = 0; vechicleIndexTo < this.fleet.length; vechicleIndexTo++) {

                        RouteTo = this.fleet[vechicleIndexTo].getRoute();

                        for (int j = 0;
                             j < RouteTo.size() - 1; j++) { //Not possible to move after last Depot!

                            MovingNodeDemand = RouteFrom.get(i).getDemand();

                            if ((vechicleIndexFrom == vechicleIndexTo) || this.fleet[vechicleIndexTo].isFit(MovingNodeDemand)) {
                                //If we assign to a different route check capacity constrains
                                //if in the new route is the same no need to check for capacity

                                // Not a move that Changes solution cost
                                if (!((vechicleIndexFrom == vechicleIndexTo) && ((j == i) || (j == i - 1)))) {

                                    int routeFromStart = RouteFrom.get(i - 1).getId();
                                    int routeFromEnd = RouteFrom.get(i).getId();
                                    int routeFromStartNeigh = RouteFrom.get(i + 1).getId();
                                    int routeToStart = RouteTo.get(j).getId();
                                    int routeToEnd = RouteTo.get(j + 1).getId();

                                    if (
                                            tabu.isInTabu(routeFromStart, routeFromStartNeigh) ||
                                                    tabu.isInTabu(routeToStart, routeFromEnd) ||
                                                    tabu.isInTabu(routeFromEnd, routeToEnd)
                                            ){
                                        break;
                                    }

                                    Neigthboorcost =
                                            costMatrix[routeFromStart][routeFromStartNeigh]
                                                    + costMatrix[routeToStart][routeFromEnd]
                                                    + costMatrix[routeFromEnd][routeToEnd]
                                                    - costMatrix[routeFromStart][routeFromEnd]
                                                    - costMatrix[routeFromEnd][routeFromStartNeigh]
                                                    - costMatrix[routeToStart][routeToEnd];

                                    if (Neigthboorcost < BestNcost) {
                                        BestNcost = Neigthboorcost;
                                        SwapIndexA = i;
                                        SwapIndexB = j;
                                        SwapRouteFrom = vechicleIndexFrom;
                                        SwapRouteTo = vechicleIndexTo;
                                    }
                                }
                            }
                        }
                    }
                }

            }

            tabu.decreaseTabu();

            RouteFrom = this.fleet[SwapRouteFrom].getRoute();
            RouteTo = this.fleet[SwapRouteTo].getRoute();
            this.fleet[SwapRouteFrom].setRoute(null);
            this.fleet[SwapRouteTo].setRoute(null);

            Vertex swapVertex = RouteFrom.get(SwapIndexA);

            int NodeIDBefore = RouteFrom.get(SwapIndexA - 1).getId();
            int NodeIDAfter = RouteFrom.get(SwapIndexA + 1).getId();
            int NodeID_F = RouteTo.get(SwapIndexB).getId();
            int NodeID_G = RouteTo.get(SwapIndexB + 1).getId();

            Random TabuRan = new Random();
            int RendomDelay1 = TabuRan.nextInt(20);
            int RendomDelay2 = TabuRan.nextInt(20);
            int RendomDelay3 = TabuRan.nextInt(20);

            tabu.getTabuList()[NodeIDBefore][swapVertex.getId()] = TABU_Horizon + RendomDelay1;
            tabu.getTabuList()[swapVertex.getId()][NodeIDAfter] = TABU_Horizon + RendomDelay2;
            tabu.getTabuList()[NodeID_F][NodeID_G] = TABU_Horizon + RendomDelay3;

            RouteFrom.remove(SwapIndexA);

            if (SwapRouteFrom == SwapRouteTo) {
                if (SwapIndexA < SwapIndexB) {
                    RouteTo.add(SwapIndexB, swapVertex);
                } else {
                    RouteTo.add(SwapIndexB + 1, swapVertex);
                }
            } else {
                RouteTo.add(SwapIndexB + 1, swapVertex);
            }


            this.fleet[SwapRouteFrom].setRoute(RouteFrom);
            this.fleet[SwapRouteFrom].setLoad(this.fleet[SwapRouteFrom].getLoad() - MovingNodeDemand);

            this.fleet[SwapRouteTo].setRoute(RouteTo);
            this.fleet[SwapRouteTo].setLoad(this.fleet[SwapRouteTo].getLoad() + MovingNodeDemand);


            this.cost += BestNcost;

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
