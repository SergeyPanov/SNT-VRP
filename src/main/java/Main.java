import com.graphhopper.jsprit.analysis.toolbox.GraphStreamViewer;
import com.graphhopper.jsprit.analysis.toolbox.Plotter;
import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.reporting.SolutionPrinter;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.io.problem.VrpXMLWriter;
import vrpassembler.Assembler;

import javax.xml.bind.JAXBException;
import java.util.Collection;

public class Main {

    public static void main(String[] args) throws JAXBException {


        Assembler assembler = new Assembler("/Users/sergeypanov/git/SNT-VRP/src/main/resources/solomon-1987-r1/R101_025.xml");


        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        vrpBuilder.setFleetSize(FleetSize.FINITE);

        vrpBuilder.addAllVehicles(assembler.getFleet());

        vrpBuilder.addAllJobs(assembler.getServices());

        VehicleRoutingProblem problem = vrpBuilder.build();

        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

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
