package params;

import com.beust.jcommander.Parameter;

public class ParamsParser {

    @Parameter(names = {"-p", "--path"}, description = "Path to input xml file.", required = true)
    private String path;

    @Parameter(names = "-c", arity = 0, description = "In case of set the result of greedy search will be printed either.")
    private boolean isCompare;

    @Parameter(names = "-i", arity = 1, description = "Set amount of iterations. Default value is 300.")
    private int iterations = 300;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public boolean isCompare() {
        return isCompare;
    }

    public void setCompare(boolean compare) {
        isCompare = compare;
    }

    public int getIterations() {
        return iterations;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }
}
