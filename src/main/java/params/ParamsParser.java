package params;

import com.beust.jcommander.Parameter;

public class ParamsParser {

    @Parameter(names = {"-p", "--path"}, description = "Path to input xml file.", required = true)
    private String path;

    @Parameter(names = "-v", arity = 0, description = "In case of set visualization will be show.")
    private boolean visualize;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public boolean isVisualize() {
        return visualize;
    }

    public void setVisualize(boolean visualize) {
        this.visualize = visualize;
    }
}
