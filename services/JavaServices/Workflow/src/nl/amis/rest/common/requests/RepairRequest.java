package nl.amis.rest.common.requests;

import javax.ws.rs.QueryParam;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RepairRequest extends Request{
    public RepairRequest() {
        super();
    }

    @QueryParam("product")
    private String product;

    @QueryParam("serialnumber")
    private String serialnumber;


    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public String getSerialnumber() {
        return serialnumber;
    }
}
