package solution;

/**
 * Class represents Customer(Vertex)
 */
public class Vertex {

    private int id;
    private int oX;
    private int oY;
    private int demand;
    private boolean isRouted;
    private boolean isDepot;

    Vertex(int dOX, int dOY) {
        this.id = 0;
        this.oX = dOX;
        this.oY = dOY;
        this.isDepot= true;
    }

    Vertex(int id, int x, int y, int demand) {
        this.id = id;
        this.oX = x;
        this.oY = y;
        this.demand = demand;
        this.isRouted = false;
        this.isDepot= false;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getoX() {
        return oX;
    }

    public void setoX(int oX) {
        this.oX = oX;
    }

    public int getoY() {
        return oY;
    }

    public void setoY(int oY) {
        this.oY = oY;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public boolean isNotRouted() {
        return !isRouted;
    }

    public void setRouted(boolean routed) {
        isRouted = routed;
    }

    public boolean isDepot() {
        return isDepot;
    }

    public void setDepot(boolean depot) {
        isDepot = depot;
    }
}
