package params;

import org.apache.commons.cli.*;

/**
 * Pars input parameters
 */
public class Parameters {
    private  Options options = new Options();


    private String path;

    private boolean isCompare;

    private int iterations = 300;

    private boolean isGraph;

    private int horizon = 10;
    private int distance = 5;

    public Parameters(){
        Option optionPath = new Option("p", true, "Path to the file.");
        Option optionCmp = new Option("c", false, "In case of set, the result of Greedy search will be printed.");
        Option optionIters = new Option("i", true, "Set amount of iterations for Tabu Search");
        Option optionGraph = new Option("g", false, "Plot graph if needed.");
        Option optionHorizon = new Option("h", true, "Set initial horizon.");
        Option optionDistance = new Option("d", true, "Right bound of horizon will be horizon + distance.");

        options.addOption(optionGraph);
        options.addOption(optionPath);
        options.addOption(optionCmp);
        options.addOption(optionIters);
        options.addOption(optionHorizon);
        options.addOption(optionDistance);
    }

    public void parse(String[] args){

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;

        try{
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert cmd != null;
        path = cmd.getOptionValue("p");
        isCompare = cmd.hasOption("c");
        isGraph = cmd.hasOption("g");
        iterations = cmd.getOptionValue("i") == null ? 300 : Integer.parseInt(cmd.getOptionValue("i"));
        horizon = cmd.getOptionValue("h") == null ? 10 : Integer.parseInt(cmd.getOptionValue("h"));
        distance = cmd.getOptionValue("d") == null ? 5 : Integer.parseInt(cmd.getOptionValue("d"));

    }

    public int getIterations() {
        return iterations;
    }


    public boolean isCompare() {
        return isCompare;
    }


    public String getPath() {
        return path;
    }

    public boolean isGraph() {
        return isGraph;
    }

    public int getHorizon() {
        return horizon;
    }

    public int getDistance() {
        return distance;
    }
}
