package nl.amis.rest.common.workflow;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import nl.amis.soa.workflow.tasks.entities.User;
import nl.amis.soa.workflow.tasks.services.HumanTaskClient;

import oracle.adf.share.logging.ADFLogger;


@Path("/identity")
public class Identity {

    private static ADFLogger logger = ADFLogger.createADFLogger(Identity.class);
    private static HumanTaskClient wfClient = new HumanTaskClient();
    public static final String MESSAGE = "This is the SOA Identity Rest service";

    public Identity() {
    }

    @GET
    @Path("/users")
    @Produces({MediaType.TEXT_HTML})
    public String getText(@PathParam("user") String user) {
        logger.finest("[GET] " +MESSAGE);
        return MESSAGE + " for user "+ user;
    }


    @GET
    @Path("/users/{user}")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response getUser(@PathParam("user") String user,
                            @Context HttpHeaders hh,
                            @Context SecurityContext sc){
          logger.finest("[GET] ");
          MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
        try {
          User bpmUser = wfClient.getUser(user); 
          return  Response.ok(new GenericEntity<User>(bpmUser){}).build();
        }  catch (Exception t) {
          return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }
    }


}
