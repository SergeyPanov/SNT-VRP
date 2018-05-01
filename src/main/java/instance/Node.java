package instance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Node in the network. Used for creating Vertex.
 */
@XmlRootElement(name = "node")
public class Node {
    private int id;
    private String type;

    private double cx;
    private double cy;

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }


    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }
}
