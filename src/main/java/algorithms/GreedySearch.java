package algorithms;

import solution.Environment;
import solution.Vehicle;
import solution.Vertex;

import java.util.Arrays;

public class GreedySearch implements Algorithm {
    @Override
    public Environment execute(Environment environment) {
        double candidateCost, finalCost;

        int vechicleIndex = 0;

        while (Arrays.stream(environment.getVertices()).filter(v -> !v.isDepot()).anyMatch(Vertex::isNotRouted)) {

            int serviceIndex = 0;
            Vertex candidateVertex = null;
            double mincost = Double.MAX_VALUE;

            if (environment.getFleet().size() == vechicleIndex){
                environment.getFleet().add(new Vehicle(environment.getFleet().size(), environment.getCapacity()));
            }

            if (environment.getFleet().get(vechicleIndex).getRoute().isEmpty()){
                environment.getFleet().get(vechicleIndex).addVertex(environment.getVertices()[environment.getDEPO()]);
            }

            for (int i = 1; i <= environment.getNumbOfCustomers(); i++) {

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

                if (environment.getFleet().get(vechicleIndex).getCurLocation() != 0) { //End this route
                    finalCost = environment.getCostMatrix()[environment.getFleet().get(vechicleIndex).getCurLocation()][environment.getDEPO()];
                    environment.getFleet().get(vechicleIndex).addVertex(environment.getVertices()[environment.getDEPO()]);
                    environment.setCost(environment.getCost() + finalCost);
                }
                vechicleIndex = vechicleIndex + 1; //Go to next Vehicle

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
