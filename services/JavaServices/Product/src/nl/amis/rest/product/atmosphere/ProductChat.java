package nl.amis.rest.product.atmosphere;


import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.config.service.AtmosphereService;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceEventListenerAdapter;
import org.atmosphere.jersey.JerseyBroadcaster;


import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import nl.amis.rest.product.entities.Message;
import nl.amis.rest.product.entities.Response;

/**
 * Simple chat resource demonstrating the power of Atmosphere. This resource supports transport like WebSocket, Streaming, JSONP and Long-Polling.
 *
 * @author Jeanfrancois Arcand
 */
@Path("/")
public class ProductChat {
    /**
     * Suspend the response without writing anything back to the client.
     * @return a white space
     */
    @Suspend(contentType = "application/json")
    @GET
    public String suspend() {
        return "";
    }

    /**
     * Broadcast the received message object to all suspended response. Do not write back the message to the calling connection.
     * @param message a {@link Message}
     * @return a {@link Response}
     */
    @Broadcast(writeEntity = false)
    @POST
    @Produces("application/json")
    public Response broadcast(Message message) {
        return new Response(message.author, message.message);
    }
}