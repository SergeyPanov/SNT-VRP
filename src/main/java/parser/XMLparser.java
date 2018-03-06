package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLparser {
    private String path;
    private Document document;


    public void parse() throws ParserConfigurationException, IOException, SAXException {
        File inputFile = new File(path);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        document = dBuilder.parse(inputFile);
        document.getDocumentElement().normalize();
    }

    public String getChildValue(Node parent, String childName){
        parent.normalize();

        NodeList children = parent.getChildNodes();

        for (int i = 0; i < children.getLength(); i++) {

            if (children.item(i).getNodeName().equals(childName)){
                return children.item(i).getTextContent();
            }
        }

        return null;
    }

    public NodeList getElementaByTagName(String tagName){
        return document.getElementsByTagName(tagName);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
