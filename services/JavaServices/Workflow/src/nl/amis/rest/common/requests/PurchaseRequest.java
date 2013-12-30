package nl.amis.rest.common.requests;

import javax.ws.rs.QueryParam;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class PurchaseRequest extends Request{
    public PurchaseRequest() {
    }

    @QueryParam("product")
    private String product;

    @QueryParam("shipper")
    private String shipper;


    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }

    public void setShipper(String shipper) {
        this.shipper = shipper;
    }

    public String getShipper() {
        return shipper;
    }
}
