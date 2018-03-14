package tabu;

import instance.Instance;
import instance.Node;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class Environment {
    private Vehicle[] fleet;
    private Vehicle[] bestFleet;
    private Vertex[] vertices;
    private double[][] distances;

    private Instance instance;


    private int fleetSize;
    private int numbOfCustomers;

    public Environment(Instance instance) {

        this.instance = instance;

        this.fleetSize = instance.getFleet().getProfiles().stream()
                .filter(fl -> fl.getType().equals("0"))
                .findFirst()
                .get()
                .getNumber();

        this.numbOfCustomers = instance.getNetwork().getNodes().getNode().size() - 1;

        setupEnvironment();
    }

    private void setupEnvironment(){

        vertices = new Vertex[ instance.getNetwork().getNodes().getNode().size()];
        distances = new double[numbOfCustomers + 1][numbOfCustomers + 1];

        setupVertexes();
        setupDistances();

    }

    private void setupDistances(){



        for (int i = 0; i <= numbOfCustomers; i++) {
            //The table is summetric to the first diagonal
            for (int j = i + 1; j <= numbOfCustomers; j++) { //Use this to compute distances in O(n/2)

                double distance = Math.sqrt(
                        (Math.pow((vertices[i].getoX() - vertices[j].getoY()), 2))
                        + (Math.pow((vertices[i].getoY() - vertices[j].getoY()), 2)));

                distance = Math.round(distance); //Distance is Casted in Integer
                //distance = Math.round(distance*100.0)/100.0; //Distance in double

                distances[i][j] = distance;
                distances[j][i] = distance;
            }
        }

    }

    private void setupVertexes(){

        Optional<Node> depot = instance.getNetwork().getNodes().getNode().stream()
                .filter(nd -> nd.getType().equals("0")).findFirst();

        assert depot.orElse(null) != null;
        vertices[0] = new Vertex((int)depot.orElse(null).getCx(), (int)depot.orElse(null).getCy());

        // Setup customer's Vertexes
        IntStream.range(1, numbOfCustomers + 1).forEach(
                i -> vertices[i] = new Vertex(i,
                        (int) instance.getNetwork().getNodes().getNode().get(i).getCx(),
                        (int) instance.getNetwork().getNodes().getNode().get(i).getCy(),
                        (int) Objects.requireNonNull(instance.getRequests().getRequest().stream()
                                .filter(request -> request.getId() == i)
                                .findFirst()
                                .orElse(null))
                                .getQuantity() //Random Demand
                ));

    }


    public int getNumbOfCustomers() {
        return numbOfCustomers;
    }

    public Vehicle[] getFleet() {
        return fleet;
    }

    public void setFleet(Vehicle[] fleet) {
        this.fleet = fleet;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public double[][] getDistances() {
        return distances;
    }

    public void setDistances(double[][] distances) {
        this.distances = distances;
    }

    public int getFleetSize() {
        return fleetSize;
    }

    public void setFleetSize(int fleetSize) {
        this.fleetSize = fleetSize;
    }
}
