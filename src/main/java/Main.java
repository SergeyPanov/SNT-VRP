import com.beust.jcommander.JCommander;
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
import params.ParamsParser;
import vrpassembler.Assembler;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) throws JAXBException {


        ParamsParser paramsParser = new ParamsParser();
        JCommander jCommander = new JCommander(paramsParser, args);
        jCommander.parse();

        List<String> aux = Arrays.asList(paramsParser.getPath().split("/"));
        String outputFile = aux.get(aux.size() - 1);

        File dir = new File("output");

        if (!dir.exists()){
            System.out.println("Creating directory ./output");
            boolean result = dir.mkdir();
            if(result) System.out.println("./output created");
        }

        Assembler assembler = new Assembler(paramsParser.getPath());

        VehicleRoutingProblem.Builder vrpBuilder = VehicleRoutingProblem.Builder.newInstance();

        vrpBuilder.setFleetSize(FleetSize.FINITE);

        vrpBuilder.addAllVehicles(assembler.getFleet());

        vrpBuilder.addAllJobs(assembler.getServices());

        VehicleRoutingProblem problem = vrpBuilder.build();

        VehicleRoutingAlgorithm algorithm = Jsprit.createAlgorithm(problem);

        Collection<VehicleRoutingProblemSolution> solutions = algorithm.searchSolutions();

        VehicleRoutingProblemSolution bestSolution = Solutions.bestOf(solutions);

        new VrpXMLWriter(problem, solutions).write("output/"+ "result-" + outputFile);

        SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);

        /*
         * plot
         */
        new Plotter(problem,bestSolution).plot("output/" + outputFile +".png","Solution");


        if (paramsParser.isVisualize()){
                    /*
        render problem and solution with GraphStream
         */
            new GraphStreamViewer(problem, bestSolution).labelWith(GraphStreamViewer.Label.ID).setRenderDelay(200).display();
        }

    }
}
