package instance;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vehicle_profile")
public class VehicleProfile {

    private String type;

    private int number;

    private int departureNode;

    private int arrivalNode;

    private double capacity;



    @XmlAttribute
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlAttribute
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @XmlElement(name = "departure_node")
    public int getDepartureNode() {
        return departureNode;
    }

    public void setDepartureNode(int departureNode) {
        this.departureNode = departureNode;
    }

    @XmlElement(name = "arrival_node")
    public int getArrivalNode() {
        return arrivalNode;
    }

    public void setArrivalNode(int arrivalNode) {
        this.arrivalNode = arrivalNode;
    }

    @XmlElement
    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
}
