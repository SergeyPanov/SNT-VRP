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

        File file = new File(paramsParser.getPath());
        JAXBContext jaxbContext = JAXBContext.newInstance(Instance.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Instance instance = (Instance) jaxbUnmarshaller.unmarshal(file);

        Environment environment = new Environment(instance);

        Solution solution = new Solution(environment);
        solution.tabuSearch( 0, 300);
//        solution.printBestRoute();

    }
}
