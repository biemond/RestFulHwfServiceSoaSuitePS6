package nl.amis.rest.common.workflow;


import com.sun.jersey.api.core.InjectParam;

import java.net.URI;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import javax.ws.rs.core.UriBuilder;

import nl.amis.rest.common.requests.RepairRequest;
import nl.amis.soa.workflow.tasks.entities.PurchaseTask;
import nl.amis.soa.workflow.tasks.entities.RepairTask;
import nl.amis.soa.workflow.tasks.entities.Result;
import nl.amis.soa.workflow.tasks.services.HumanTaskClient;

import oracle.adf.share.logging.ADFLogger;

@Path("/workflow/users/{user}/repair")
public class Repair {
    public Repair() {
    }

    private static ADFLogger logger = ADFLogger.createADFLogger(Purchase.class);
    private static HumanTaskClient wfClient = new HumanTaskClient();


    private String username;

    public String getUsername() {
        return username;
    }


    @GET
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})
    public Response getHumanTasksRepair(@PathParam("user") String user, 
                                          @InjectParam final RepairRequest request,
                                          @Context SecurityContext sc
                                         ) {
        logger.finest("[GET] ");
        if (sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
              List<RepairTask> tasks = wfClient.getHumanTasksRepair(user, 
                                                                    request.getSearch(),   
                                                                    request.getProduct(),
                                                                    request.getSerialnumber(),                                                                                     
                                                                    request.getNoOfRecords(),
                                                                    request.getOrderBy()); 
    
              return  Response.ok( tasks.toArray(new RepairTask[tasks.size()])).build();
            }  catch (Throwable t) {
                t.printStackTrace();
              return Response.status(Response.Status.NOT_ACCEPTABLE).build();   
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }


    @GET
    @Path("/total")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})    
    public Response getRepairTotals( @PathParam("user") String user,
                                     @QueryParam("status") String procesStatus,
                                     @Context SecurityContext sc){
        logger.finest("[GET] for user " +user);
        if ( sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
              Integer count = wfClient.getHumanTasksPurchaseTotals(user);
              Result result = new Result("total",count);
              return Response.ok(result).build();
            }  catch (Throwable t) {
              return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }



    @POST
    @Path("/{taskId}/assign")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})    
    public Response repairAssignTask(@PathParam("taskId")      String taskId,
                                  @PathParam("user")        String user,
                                  @QueryParam("futureUser") String futureUser, 
                                  @DefaultValue("false") @QueryParam("isGroup")    boolean isGroup,
                                  @Context HttpHeaders hh,
                                  @Context SecurityContext sc) {
        logger.finest("[POST] for taskId " +taskId);
        if (sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
              wfClient.assignTask(taskId, user, futureUser, isGroup);
            }  catch (Throwable t) {
               return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }

    @GET
    @Path("/{taskId}")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})    
    public Response repairTask(@PathParam("taskId")  String taskId,
                                   @PathParam("user")    String user,
                                   @DefaultValue("true") @QueryParam("isAllowedToBeAcquiredByUser") boolean isAllowedToBeAcquiredByUser,
                                   @Context SecurityContext sc) {
        logger.finest("[GET] for taskId " +taskId);
        if (sc.isUserInRole("AppUser") || sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
              RepairTask repairTask = wfClient.getRepairTask(taskId, user);
              return Response.ok(repairTask).build();
            }  catch (Throwable t) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }

    @POST
    @Path("/{taskId}/acquire")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})    
    public Response repairAcquireTask(@PathParam("taskId")  String taskId,
                                   @PathParam("user")    String user,
                                   @DefaultValue("true") @QueryParam("isAllowedToBeAcquiredByUser") boolean isAllowedToBeAcquiredByUser,
                                   @Context SecurityContext sc) {
        logger.finest("[POST] for taskId " +taskId);
        if (sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
               wfClient.acquireTask( taskId, user, isAllowedToBeAcquiredByUser);
               return Response.seeOther(getTaskURI(user, taskId)).build();  
            }  catch (Throwable t) {
                return Response.status(Response.Status.NOT_ACCEPTABLE).build();
    
            }
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }

    private URI getTaskURI(String user, String taskId) {
           return UriBuilder.fromResource(Repair.class).path("/{taskId}").build(user, taskId);
    }

    @POST
    @Path("/{taskId}/release")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})    
    public Response repairReleaseTask(@PathParam("taskId") String taskId,
                                   @PathParam("user")   String user,
                                   @Context SecurityContext sc) {
        logger.finest("[POST] for taskId " +taskId);
        if ( sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
              wfClient.releaseTask(taskId, user);
            }  catch (Throwable t) {
                return Response.status(Response.Status.NOT_MODIFIED).build();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }

    @POST
    @Path("/{taskId}/complete")
    @Produces({MediaType.APPLICATION_JSON,MediaType.APPLICATION_XML})    
    public Response repairCompleteTask(@PathParam("taskId") String taskId,
                                    @PathParam("user")      String user,
                                    @QueryParam("outcome")  String outcome,
                                    @QueryParam("comment")  String comment,
                                    @Context SecurityContext sc) {
        logger.finest("[POST] for taskId " +taskId);
        if ( sc.isUserInRole("AppAdmin") ) {
            this.username = user;
            try {
              wfClient.completeTask(taskId, outcome, user, comment);        
            }  catch (Throwable t) {
              return Response.status(Response.Status.NOT_ACCEPTABLE).build();
            }
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.FORBIDDEN).build();   
        }
    }

}