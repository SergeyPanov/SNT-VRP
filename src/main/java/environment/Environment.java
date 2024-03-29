package environment;

import instance.Instance;
import instance.Node;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

/**
 * Class holds whole environment.
 */
public class Environment {
    private List<Vehicle> fleet = new ArrayList<>();
    private List<Vehicle> bestFleet = new ArrayList<>();
    private Vertex[] vertices;
    private double[][] costMatrix;
    private int capacity;

    private String name;
    private final int DEPO = 0;

    private double cost = 0;

    private Instance instance;

    private int allowedVehicleNumber = 0;


    private int numbOfCustomers;

    // Fill environment based on input instance
    public Environment(Instance instance) {

        this.instance = instance;

        this.allowedVehicleNumber = instance.getFleet().getProfiles().get(0).getNumber();

        name = instance.getInfo().getName();

        this.capacity = (int) instance.getFleet().getProfiles().stream()
                .filter(vehicleProfile -> vehicleProfile.getType().equals("0")).findFirst().get().getCapacity();

        this.numbOfCustomers = instance.getNetwork().getNodes().getNode().size() - 1;

        setupEnvironment();
    }


    public double calculateCost(){

        double cost = 0;

        for (Vehicle v:
             fleet) {

            for (int i = 0; i < v.getRoute().size() - 1; i++) {
                cost += costMatrix[i][i + 1];
            }

        }
        return cost;
    }

    /**
     * Setup environment.
     */
    private void setupEnvironment(){

        vertices = new Vertex[ instance.getNetwork().getNodes().getNode().size()];
        costMatrix = new double[numbOfCustomers + 1][numbOfCustomers + 1];

        setupVertexes();
        setupDistances();

    }

    /**
     * Fill matrix of distances.
     */
    private void setupDistances(){

        for (int i = 0; i <= numbOfCustomers; i++) {
            for (int j = i + 1; j <= numbOfCustomers; j++) {

                double distance = Math.sqrt(
                        (Math.pow((vertices[i].getoX() - vertices[j].getoX()), 2))
                        + (Math.pow((vertices[i].getoY() - vertices[j].getoY()), 2)));

                distance = Math.round(distance);

                costMatrix[i][j] = distance;
                costMatrix[j][i] = distance;
            }
        }

    }

    /**
     * Based on instance create Vertexes.
     */
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
                                .filter(request -> (request.getNode() == i) || (request.getId() == i))
                                .findFirst()
                                .orElse(null))
                                .getQuantity()
                ));
    }


    public double[][] getCostMatrix() {
        return costMatrix;
    }

    public int getNumbOfCustomers() {
        return numbOfCustomers;
    }



    public Vertex[] getVertices() {
        return vertices;
    }

    public void setVertices(Vertex[] vertices) {
        this.vertices = vertices;
    }

    public List<Vehicle> getFleet() {
        return fleet;
    }

    public void setFleet(List<Vehicle> fleet) {
        this.fleet = fleet;
    }

    public List<Vehicle> getBestFleet() {
        return bestFleet;
    }

    public void setBestFleet(List<Vehicle> bestFleet) {
        this.bestFleet = bestFleet;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getDEPO() {
        return DEPO;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getAllowedVehicleNumber() {
        return allowedVehicleNumber;
    }

    public void setAllowedVehicleNumber(int allowedVehicleNumber) {
        this.allowedVehicleNumber = allowedVehicleNumber;
    }

    public String getName() {
        return name;
    }
}
