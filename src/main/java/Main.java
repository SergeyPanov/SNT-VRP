import com.beust.jcommander.JCommander;
import instance.Instance;
import params.ParamsParser;
import solution.Environment;
import solution.Solution;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws JAXBException {



        ParamsParser paramsParser = new ParamsParser();
        JCommander.newBuilder().addObject(paramsParser).build().parse(args);

        File file = new File(paramsParser.getPath());
        JAXBContext jaxbContext = JAXBContext.newInstance(Instance.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Instance instance = (Instance) jaxbUnmarshaller.unmarshal(file);

        Environment environment = new Environment(instance);

        if (paramsParser.isCompare()){
            Solution solution = new Solution(environment);
            solution.greedySearch();
            solution.printBestRoute();
        }

        Solution solution = new Solution(environment);
        solution.tabuSearch(  paramsParser.getIterations());
        solution.printBestRoute();

    }
}
