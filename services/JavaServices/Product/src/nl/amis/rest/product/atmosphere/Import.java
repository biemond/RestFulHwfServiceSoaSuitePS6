package nl.amis.rest.product.atmosphere;
import org.atmosphere.annotation.Broadcast;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.jersey.Broadcastable;


@Path("/import/{topic}")
public class Import {
    public Import() {
    }

    private @PathParam("topic") Broadcaster topic;

    @Suspend(contentType = "application/json" )
    @GET
    public Broadcastable suspend() {
        return new Broadcastable("",topic);
    }
}