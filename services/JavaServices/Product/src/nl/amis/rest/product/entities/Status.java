package nl.amis.rest.product.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Status {
    public Status() {
    }

    private String importStatus;
    private String message;
    private List<ImportProgress> importProgresses;

    public void setImportStatus(String importStatus) {
        this.importStatus = importStatus;
    }

    public String getImportStatus() {
        return importStatus;
    }


    public void setImportProgresses(List<ImportProgress> importProgresses) {
        this.importProgresses = importProgresses;
    }

    public List<ImportProgress> getImportProgresses() {
        return importProgresses;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
