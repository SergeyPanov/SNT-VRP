import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parser.XMLparser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        XMLparser xmLparser = new XMLparser();
        xmLparser.setPath("/Users/sergeypanov/git/SNT-VRP/src/main/resources/solomon-1987-r1/R101_025.xml");
        xmLparser.parse();

        NodeList list = xmLparser.getElementaByTagName("node");



        Node vehicle = null;
        for (int i = 0; i < list.getLength(); i++) {

            if (xmLparser.getElementaByTagName("node")
                    .item(i)
                    .getAttributes()
                    .getNamedItem("id")
                    .getNodeValue().equals("0")){

                vehicle = xmLparser.getElementaByTagName("node")
                        .item(i);
                break;
            }
        }

        assert vehicle != null;

        NodeList vehicleProfiles = xmLparser.getElementaByTagName("vehicle_profile");

        Node vehicleProfile = null;
        for (int i = 0; i < vehicleProfiles.getLength(); i++) {

            String vType =  xmLparser.getElementaByTagName("vehicle_profile")
                    .item(i)
                    .getAttributes()
                    .getNamedItem("type")
                    .getNodeValue();

            if (vehicle.getAttributes().getNamedItem("type").getNodeValue().equals(vType)){
                vehicleProfile = xmLparser.getElementaByTagName("vehicle_profile").item(i);
            }
        }

        assert vehicleProfile != null;

        String capacity = xmLparser.getChildValue(vehicleProfile, "capacity");

        assert capacity != null;


        VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder
                .newInstance(vehicle.getAttributes().getNamedItem("type").getNodeValue())
                .addCapacityDimension(0, (int) Double.valueOf(capacity).doubleValue());

        VehicleType vehicleType = vehicleTypeBuilder.build();

        VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicle.getNodeName() +
                                                                            vehicle.getAttributes().getNamedItem("id"));


        String x = xmLparser.getChildValue(vehicle, "cx");
        String y = xmLparser.getChildValue(vehicle, "cy");

        assert x != null;
        assert y != null;

        String latestArrival = xmLparser.getChildValue(vehicleProfile, "max_travel_time");

        assert latestArrival != null;


        vehicleBuilder.setStartLocation(Location.newInstance(Double.valueOf(x), Double.valueOf(y)));
        vehicleBuilder.setLatestArrival(Double.valueOf(latestArrival));
        vehicleBuilder.setType(vehicleType);

        Vehicle completeVehicle = vehicleBuilder.build();


        // TODO: Add service

    }
}
