package nl.amis.soa.workflow.tasks.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Group {
    public Group() {
    }

    public Group(String name) {
        this.name = name;
    }
    

    private String name;



    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
