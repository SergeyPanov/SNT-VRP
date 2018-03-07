package vrpassembler;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.Service;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.vehicle.Vehicle;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import model.Instance;
import model.Node;
import model.Request;
import model.VehicleProfile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Assembler {

    private Instance instance;
    private List<Vehicle> fleet = new ArrayList<Vehicle>();
    private List<Service> services = new ArrayList<Service>();

    public List<Service> getServices() {
        return services;
    }

    public List<Vehicle> getFleet() {
        return fleet;
    }

    public Assembler(String path) throws JAXBException {
        File file = new File(path);
        JAXBContext jaxbContext = JAXBContext.newInstance(Instance.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        instance = (Instance) jaxbUnmarshaller.unmarshal(file);

        assembleFleet();
        assembleServices();
    }

   private void assembleFleet() {

       for (VehicleProfile vp:
            instance.getFleet().getProfiles()) {


           for (int i = 0; i < vp.getNumber(); i++) {

               VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder
                       .newInstance(vp.getType())
                       .addCapacityDimension(0, (int) vp.getCapacity());

               VehicleType vehicleType = vehicleTypeBuilder.build();

               Optional<Node> vehicle = instance.getNetwork().getNodes().getNode().stream()
                       .filter(nd -> nd.getType().equals(vp.getType())).findFirst();

               if (!vehicle.isPresent()){
                   System.out.println("Vehicle: " + vp.getType() + " was not found");
                   continue;
               }


               VehicleImpl.Builder vehicleBuilder = VehicleImpl.Builder
                       .newInstance("vehicle " +
                               vehicle.get().getType()
                               + " id: " + vehicle.get().getId() + "_" + i);
               vehicleBuilder.setStartLocation(Location.newInstance(vehicle.get().getCx(), vehicle.get().getCy()));
               vehicleBuilder.setLatestArrival(vp.getMaxTravelTime());
               vehicleBuilder.setType(vehicleType);


               fleet.add(vehicleBuilder.build());
           }

       }

   }


   private void assembleServices(){

       for (Request rec:
            instance.getRequests().getRequest()) {

            Service.Builder sBuilder = Service.Builder.newInstance(String.valueOf(rec.getId()))
                                        .addSizeDimension(0, (int) rec.getQuantity());

           Optional<Node> nd = instance.getNetwork().getNodes().getNode().stream()
                   .filter(sr -> sr.getId() == rec.getId()).findFirst();

           if (!nd.isPresent()){
               System.out.println("Service: " + rec.getId() + " was not found");
               continue;
           }



           sBuilder.setLocation(Location.newInstance(nd.get().getCx(), nd.get().getCy()));
           sBuilder.setServiceTime(rec.getServiceTime());

           sBuilder.setTimeWindow(TimeWindow.newInstance(rec.getTw().getStart(), rec.getTw().getEnd()));

           services.add(sBuilder.build());
       }

   }


}
