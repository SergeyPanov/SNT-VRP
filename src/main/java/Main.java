import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import org.apache.xerces.dom.DeferredElementImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import parser.XMLparser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Main {


    private static Node getNodeWithId(NodeList nodes, String id){

        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getAttributes().getNamedItem("id").getNodeValue().equals(id)){
                return nodes.item(i);
            }
        }
        return null;
    }


    private static Node getChildNode(NodeList list, String name){
        for (int i = 0; i < list.getLength(); i++) {
            if (list.item(i).getNodeName().equals(name)){
                return list.item(i);
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException {

        File dir = new File("output");
// if the directory does not exist, create it
        if (!dir.exists()){
            System.out.println("creating directory ./output");
            boolean result = dir.mkdir();
            if(result) System.out.println("./output created");
        }

        XMLparser xmLparser = new XMLparser();
        xmLparser.setPath("/Users/sergeypanov/git/SNT-VRP/src/main/resources/solomon-1987-r1/R101_050.xml");
        xmLparser.parse();

        NodeList list = xmLparser.getElementaByTagName("node");



        Node vehicle = null;
        for (int i = 0; i < list.getLength(); i++) {

            if (xmLparser.getElementaByTagName("node")
                    .item(i)
                    .getAttributes()
                    .getNamedItem("type")
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


        String x = xmLparser.getChildValue(vehicle, "cx");
        String y = xmLparser.getChildValue(vehicle, "cy");

        assert x != null;
        assert y != null;

        String latestArrival = xmLparser.getChildValue(vehicleProfile, "max_travel_time");

        assert latestArrival != null;

        List<Vehicle> fleet = new ArrayList<Vehicle>();

        // Create fleet
        for (int i = 0; i < Double.valueOf(((DeferredElementImpl) vehicleProfile).getAttribute("number")); i++) {

            VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder
                    .newInstance(vehicle.getAttributes().getNamedItem("type").getNodeValue())
                    .setFixedCost(0)
                    .setMaxVelocity(13)
                    .addCapacityDimension(0, (int) Double.valueOf(capacity).doubleValue());

            VehicleType vehicleType = vehicleTypeBuilder.build();


            VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder.newInstance(vehicle.getNodeName() + " " +
                    vehicle.getAttributes().getNamedItem("type") + " num: " + i);

            vehicleBuilder.setStartLocation(Location.newInstance(Double.valueOf(x), Double.valueOf(y)));
            vehicleBuilder.setLatestArrival(Double.valueOf(latestArrival));
            vehicleBuilder.setType(vehicleType);

            fleet.add(vehicleBuilder.build());
        }

        NodeList requests = xmLparser.getElementaByTagName("request");

        List<Service> services = new ArrayList<Service>();

        // Create services
        for (int i = 0; i < requests.getLength(); i++) {

            Node request = requests.item(i);

            Service.Builder sBuilder = Service.Builder
                    .newInstance(request.getAttributes().getNamedItem("id").getNodeValue())
                    .addSizeDimension(0, (int) Double.valueOf(xmLparser.getChildValue(requests.item(i), "quantity")).doubleValue());

            Node node = getNodeWithId(list, String.valueOf(i));

            sBuilder.setLocation(Location.newInstance(
                    Double.valueOf(xmLparser.getChildValue(node, "cx")),
                    Double.valueOf(xmLparser.getChildValue(node, "cy")))
            );

            sBuilder.setServiceTime(Double.valueOf(xmLparser.getChildValue(request, "service_time")));

            Node tw = getChildNode(request.getChildNodes(), "tw");

            String start = xmLparser.getChildValue(tw, "start");
            String end = xmLparser.getChildValue(tw, "end");

            sBuilder.setTimeWindow(TimeWindow.newInstance(
                    Double.valueOf(start),
                    Double.valueOf(end)
            ));

            Service service = sBuilder.build();
            services.add(service);

        }

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        vrpBuilder.setFleetSize(FleetSize.FINITE);

        vrpBuilder.addAllVehicles(fleet);

        vrpBuilder.addAllJobs(services);

        VehicleRoutingProblem problem = vrpBuilder.build();

        VehicleRoutingAlgorithm algorithm = Jsprit.Builder.newInstance(problem)
                .setProperty(Jsprit.Parameter.FAST_REGRET, "true")
                .setProperty(Jsprit.Parameter.THREADS, "5")
                .setProperty(Jsprit.Parameter.FIXED_COST_PARAM, "1.") //Increase weight of the fixed cost to enable the force all vehicle workaround
                .buildAlgorithm();

        Collection solutions = algorithm.searchSolutions();

        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/problem-with-solution.xml");

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

        /*
         * plot
         */
        new Plotter(problem,bestSolution).plot("output/plot.png","simple example");

        /*
        render problem and solution with GraphStream
         */
        new GraphStreamViewer(problem, bestSolution).labelWith(GraphStreamViewer.Label.ID).setRenderDelay(200).display();
    }
}
