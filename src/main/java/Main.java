import instance.Instance;
import params.Parameters;
import solution.Environment;
import solution.Solution;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

public class Main {

    public static void main(String[] args) throws JAXBException {

        Parameters params = new Parameters();
        params.parse(args);


        File file = new File(params.getPath());
        JAXBContext jaxbContext = JAXBContext.newInstance(Instance.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        Instance instance = (Instance) jaxbUnmarshaller.unmarshal(file);

        Environment environment = new Environment(instance);

        if (params.isCompare()){
            Solution solution = new Solution(environment);
            solution.greedySearch();
            solution.printBestRoute();
        }

        Solution solution = new Solution(environment);
        solution.tabuSearch(  params.getIterations());
        solution.printBestRoute();

    }
}
