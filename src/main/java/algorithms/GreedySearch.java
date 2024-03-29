package algorithms;

import environment.Environment;
import environment.Vehicle;
import environment.Vertex;

import java.util.Arrays;

/**
 * Greedy search implementation.
 * User as initial environment.
 * Also used for comparison with Tabu Search
 */
public class GreedySearch implements Algorithm {
    @Override
    public Environment execute(Environment environment) {
        double candidateCost, finalCost;

        int vechicleIndex = 0;

        // While all vertexes wont be on any route
        while (Arrays.stream(environment.getVertices()).filter(v -> !v.isDepot()).anyMatch(Vertex::isNotRouted)) {

            int serviceIndex = 0;
            Vertex candidateVertex = null;
            double mincost = Double.MAX_VALUE;

            // All vehicles are full, add new one
            if (environment.getFleet().size() == vechicleIndex){
                environment.getFleet().add(new Vehicle(environment.getFleet().size(), environment.getCapacity()));
            }

            // Each vehicle starts from DEPO
            if (environment.getFleet().get(vechicleIndex).getRoute().isEmpty()){
                environment.getFleet().get(vechicleIndex).addVertex(environment.getVertices()[environment.getDEPO()]);
            }

            // Looking for a customer
            for (int i = 1; i <= environment.getNumbOfCustomers(); i++) {

                // If customer is not routed and can be placed on the route
                if (environment.getVertices()[i].isNotRouted()

                        && environment.getFleet().get(vechicleIndex)
                        .isFit(environment.getVertices()[i].getDemand())) {

                    candidateCost = environment.getCostMatrix()[environment.getFleet().get(vechicleIndex).getCurLocation()][i];

                    if (mincost > candidateCost) {
                        mincost = candidateCost;
                        serviceIndex = i;
                        candidateVertex = environment.getVertices()[i];
                    }
                }
            }

            if (candidateVertex == null) {
                if (vechicleIndex + 1 < environment.getAllowedVehicleNumber()){

                    if (environment.getFleet().get(vechicleIndex).getCurLocation() != 0) { //End this route

                        finalCost = environment.getCostMatrix()[environment.getFleet().get(vechicleIndex).getCurLocation()][environment.getDEPO()];

                        environment.getFleet().get(vechicleIndex).addVertex(environment.getVertices()[environment.getDEPO()]);

                        environment.setCost(environment.getCost() + finalCost);

                    }

                    vechicleIndex = vechicleIndex + 1; //Go to next Vehicle
                }else {
                    System.out.println("\nThe rest customers do not fit in any Vehicle\n" +
                            "The problem cannot be resolved under these constrains");
                    System.exit(0);
                }

            } else {
                environment.getFleet().get(vechicleIndex).addVertex(candidateVertex); //If a fitting Customer is Found
                environment.getVertices()[serviceIndex].setRouted(true);
                environment.setCost(environment.getCost() + mincost);

            }

        }
        finalCost = environment.getCostMatrix()[environment.getFleet().get(vechicleIndex).getCurLocation()][environment.getDEPO()];
        environment.getFleet().get(vechicleIndex).addVertex(environment.getVertices()[environment.getDEPO()]);
        environment.setCost(environment.getCost() + finalCost);

        return environment;
    }
}
