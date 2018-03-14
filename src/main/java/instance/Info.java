package instance;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "info")
public class Info {

    private String dataset;
    private String name;


    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
