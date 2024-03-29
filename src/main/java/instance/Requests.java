package instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * List of the requests.
 */
@XmlRootElement(name = "requests")
public class Requests {

    private List<Request> request;

    @XmlElement
    public List<Request> getRequest() {
        return request;
    }

    public void setRequest(List<Request> request) {
        this.request = request;
    }
}
