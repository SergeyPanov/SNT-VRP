package instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "nodes")
public class Nodes {

    private List<Node> node;

    @XmlElement(name = "node")
    public List<Node> getNode() {
        return node;
    }

    public void setNode(List<Node> nodes) {
        this.node = nodes;
    }
}
