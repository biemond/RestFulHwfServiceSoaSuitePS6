package nl.amis.rest.product.entities;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class FileDetail {
    public FileDetail() {
        super();
    }

    private String   fileName;
    private String   fullPath;
    private String   batchId;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getBatchId() {
        return batchId;
    }
}