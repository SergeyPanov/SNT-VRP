package instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "network")
public class Network {


    private Nodes nodes;

    private String euclidean;

    private int decimal;

    @XmlElement
    public Nodes getNodes() {
        return nodes;
    }

    public void setNodes(Nodes nodes) {
        this.nodes = nodes;
    }

    public String getEuclidean() {
        return euclidean;
    }

    public void setEuclidean(String euclidean) {
        this.euclidean = euclidean;
    }

    public int getDecimal() {
        return decimal;
    }

    public void setDecimal(int decimal) {
        this.decimal = decimal;
    }
}
