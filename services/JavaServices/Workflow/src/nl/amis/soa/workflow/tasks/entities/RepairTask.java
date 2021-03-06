package nl.amis.soa.workflow.tasks.entities;

import com.sun.jersey.server.linking.Binding;
import com.sun.jersey.server.linking.Link;
import com.sun.jersey.server.linking.Links;
import com.sun.jersey.server.linking.Ref;
import com.sun.jersey.server.linking.Ref.Style;

import java.net.URI;

import javax.xml.bind.annotation.XmlRootElement;

import nl.amis.rest.common.workflow.Purchase;
import nl.amis.rest.common.workflow.Repair;

@XmlRootElement
@Links({
    @Link(
      value= @Ref(value="/product-context-root/jersey/products/{product}" ,
                  style=Style.RELATIVE_PATH),
      rel="product"
    ),
    @Link(
      value= @Ref(resource = Purchase.class, 
                  method="purchaseAcquireTask" ,
                  bindings={  @Binding(name="user",
                                       value="${resource.username}")}, 
                  style=Style.ABSOLUTE_PATH),
      rel="self"
    )
})
public class RepairTask extends Task {
    public RepairTask() {
    }

    @Ref(resource = Repair.class, 
         method="repairTask" ,
         bindings={  @Binding(name="user",
                              value="${resource.username}")}, 
         style=Style.ABSOLUTE_PATH)
    private URI self;

    public URI getSelf() {
        return self;
    }
    
    private String   country;
    private String   city;
    private String   serialNumber;

    @Ref(value="/product-context-root/jersey/products/{product}",
         style=Style.RELATIVE_PATH)
    private URI productURI;

    public URI getProductURI() {
        return productURI;
    }
    
    private String product;


    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getProduct() {
        return product;
    }
}
