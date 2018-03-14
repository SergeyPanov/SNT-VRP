package instance;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tw")
public class TimeWindow {
    private int start;
    private int end;

    @XmlElement
    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @XmlElement
    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }
}
