package solution;

import java.io.Serializable;
import java.util.ArrayList;

public class Vehicle implements Serializable{
    private int id;
    private ArrayList<Vertex> route = new ArrayList<>();
    private int capacity;
    private int load = 0;
    private int curLocation = 0;
    private boolean isClosed = false;

    public Vehicle(int id, int cap) {
        this.id = id;
        this.capacity = cap;
    }

    public void addVertex(Vertex customer) {
        route.add(customer);
        this.load += customer.getDemand();
        this.curLocation = customer.getId();
    }

    public boolean isFit(int dem) {
        return (load + dem <= capacity);
    }

    public ArrayList<Vertex> getRoute() {
        return route;
    }

    public void setRoute(ArrayList<Vertex> route) {
        this.route = route;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getLoad() {
        return load;
    }

    public void setLoad(int load) {
        this.load = load;
    }

    public int getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(int curLocation) {
        this.curLocation = curLocation;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }
}
