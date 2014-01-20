package nl.amis.rest.product.entities;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UploadRequest {
    public UploadRequest() {
    }

    @XmlElement(name = "type")
    private String type;

    @XmlElement(name = "files")
    private List<FileDetail> files;

    public String getType() {
        return type;
    }

    public void setFiles(List<FileDetail> files) {
        this.files = files;
    }

    public List<FileDetail> getFiles() {
        return files;
    }
}
