package instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "fleet")
public class Fleet {

    private List<VehicleProfile> profiles;


    @XmlElement(name = "vehicle_profile")
    public List<VehicleProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<VehicleProfile> fleet) {
        this.profiles = fleet;
    }
}

