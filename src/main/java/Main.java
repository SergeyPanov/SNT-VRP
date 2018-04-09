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

        Solution solution = new Solution(environment);
        solution.greedySearch();
        if (params.isCompare()){
            solution.printBestRoute();
        }

        solution.tabuSearch(  params.getIterations());
        solution.printBestRoute();

//        if (params.isGraph()){
//            solution.plot();
//        }

    }
}
