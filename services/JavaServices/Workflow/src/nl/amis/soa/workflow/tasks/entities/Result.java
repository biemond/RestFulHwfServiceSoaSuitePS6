package nl.amis.soa.workflow.tasks.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Result {


    public Result() {
    }

    private String key;
    private Integer count;

    public Result(String key, Integer count) {
        super();
        this.key = key;
        this.count = count;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCount() {
        return count;
    }
}
