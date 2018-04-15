package instance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "request")
public class Request {
    private int id;

    private int node;


    private double quantity;

    private double serviceTime;

    @XmlAttribute
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlAttribute
    public int getNode() {
        return node;
    }

    public void setNode(int node) {
        this.node = node;
    }

    @XmlElement
    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @XmlElement(name = "service_time")
    public double getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }
}
