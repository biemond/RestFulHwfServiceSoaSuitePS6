package nl.amis.soa.workflow.tasks.utility;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import nl.amis.soa.workflow.tasks.exceptions.InvalidTaskStateException;
import nl.amis.soa.workflow.tasks.exceptions.TaskNotAssignedException;

import oracle.adf.share.logging.ADFLogger;

import oracle.bpel.services.common.exception.ServicesException;
import oracle.bpel.services.workflow.IWorkflowConstants;
import oracle.bpel.services.workflow.StaleObjectException;
import oracle.bpel.services.workflow.WorkflowException;
import oracle.bpel.services.workflow.client.IWorkflowServiceClient;
import oracle.bpel.services.workflow.client.IWorkflowServiceClientConstants;
import oracle.bpel.services.workflow.client.WorkflowServiceClientFactory;
import oracle.bpel.services.workflow.client.config.RemoteClientType;
import oracle.bpel.services.workflow.client.config.ServerType;
import oracle.bpel.services.workflow.client.config.WorkflowServicesClientConfigurationType;
import oracle.bpel.services.workflow.query.ITaskQueryService;
import oracle.bpel.services.workflow.repos.Ordering;
import oracle.bpel.services.workflow.repos.Predicate;
import oracle.bpel.services.workflow.repos.TableConstants;
import oracle.bpel.services.workflow.task.ITaskAssignee;
import oracle.bpel.services.workflow.task.ITaskService;
import oracle.bpel.services.workflow.task.impl.TaskAssignee;
import oracle.bpel.services.workflow.task.model.Task;
import oracle.bpel.services.workflow.verification.IWorkflowContext;

import oracle.tip.pc.services.identity.AttributeNames;
import oracle.tip.pc.services.identity.BPMGroup;
import oracle.tip.pc.services.identity.BPMIdentityException;
import oracle.tip.pc.services.identity.BPMIdentityNotFoundException;
import oracle.tip.pc.services.identity.BPMIdentityService;
import oracle.tip.pc.services.identity.BPMUnsupportedAttributeException;
import oracle.tip.pc.services.identity.BPMUser;


public class BPELWorkflowServices {

    /**
     * Logger.
     */
    private static ADFLogger logger = ADFLogger.createADFLogger(BPELWorkflowServices.class);
    private static Logger logger_old2 = Logger.getLogger(BPELWorkflowServices.class.getName());

    private String wlsserver = "HumanWorkFlow";
    private static String soaserver = null;
    private static String wsurl = null;
    private static String t3url = null;
    private String contextFactory = "weblogic.jndi.WLInitialContextFactory";

    private String identityDomain = "jazn.com";
    private static String identityUsername = null;
    private static String identityPassword = null;
    private IWorkflowContext context = null;

    /**
     *
     */
    private static IWorkflowServiceClient workflowServiceClient;
    private static BPMIdentityService bpmClient;

    /**
     * Constructor.
     */
    public BPELWorkflowServices() {

        // Retrieve Workflow service client
        logger.finest("[START] BPELWorkflowServices");

        if ( soaserver == null ) {
          Properties defaultProps = new Properties();
          InputStream in;
          try {
              logger.finest("load soa.properties");
              in = this.getClass().getResourceAsStream ("/soa.properties");
              defaultProps.load(in);
              in.close();
          } catch (IOException e) {
              e.printStackTrace();
          }
  
          if ( defaultProps.containsKey("soaserver") ){
            logger.finest("found soaserver: " + defaultProps.getProperty("soaserver"));
            soaserver = defaultProps.getProperty("soaserver");              
          } else {
            logger.finest("try to find -Dhumantask.url");
            soaserver = System.getProperty("humantask.url");
          }
  
          wsurl = "http://" + soaserver;
          t3url = "t3://" + soaserver;
          logger.finest("ws:  "+wsurl);
  
          if ( defaultProps.containsKey("humantask.user") ){
            logger.finest("found user: " + defaultProps.getProperty("humantask.user"));
            identityUsername = defaultProps.getProperty("humantask.user");              
          } else {
            identityUsername = System.getProperty("humantask.user");
          }
  
          if ( defaultProps.containsKey("humantask.password") ){
            logger.finest("found password: " + defaultProps.getProperty("humantask.password"));
            identityPassword = defaultProps.getProperty("humantask.password");              
          } else {
            identityPassword = System.getProperty("humantask.password");
          }

        }
        if ( workflowServiceClient == null ) {
          try {
              WorkflowServicesClientConfigurationType wscct =
                  new WorkflowServicesClientConfigurationType();
  
              List<ServerType> servers = wscct.getServer();
              ServerType server = new ServerType();
              server.setDefault(true);
              server.setName(wlsserver);
              servers.add(server);
  
              RemoteClientType rct = new RemoteClientType();
              rct.setServerURL(t3url);
  
              rct.setInitialContextFactory(contextFactory);
              rct.setParticipateInClientTransaction(false);
  
              server.setRemoteClient(rct);
  
              workflowServiceClient =
                      WorkflowServiceClientFactory.getWorkflowServiceClient(WorkflowServiceClientFactory.REMOTE_CLIENT,
                                                                            wscct,
                                                                            logger_old2);
  
              Map<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, java.lang.String> properties =
                  new HashMap<IWorkflowServiceClientConstants.CONNECTION_PROPERTY, java.lang.String>();
  
              properties.put(IWorkflowServiceClientConstants.CONNECTION_PROPERTY.SOAP_END_POINT_ROOT,
                             wsurl);
  
              bpmClient =
                      WorkflowServiceClientFactory.getSOAPIdentityServiceClient(identityDomain,
                                                                                properties,
                                                                                logger_old2);
  
  
          } catch (Exception e) {
              //Handle any exceptions raised here...
              logger.severe("Caught workflow exception: " + e.getMessage());
          }
        }
        logger.finest("[EINDE] BPELWorkflowServices");
    }


    private ITaskQueryService getTaskQueryService() {

        return workflowServiceClient.getTaskQueryService();
    }


    private ITaskService getTaskService() {
        return workflowServiceClient.getTaskService();
    }


    public IWorkflowContext authenticate(String onBehalfOfUser) {

        logger.finest("authenticate ");

        IWorkflowContext contextBehalf = null;

        if (onBehalfOfUser == null)
            onBehalfOfUser = identityUsername;

        try {

            long startTime2 = System.currentTimeMillis();

            ITaskQueryService taskQueryService = getTaskQueryService();
            long stopTime2 = System.currentTimeMillis();
            long elapsedTime2 = stopTime2 - startTime2;
             logger.finest("\t" +"getTaskQueryService " + elapsedTime2);


            if (context == null) {
                logger.finest("HumanWorkflow " + 
                              identityUsername + " " + 
                      //        identityPassword.toCharArray() + " " + 
                              identityDomain +
                                   " context created");


                long startTime3 = System.currentTimeMillis();

                context =
                        taskQueryService.authenticate(identityUsername, 
                                                      identityPassword.toCharArray(),
                                                      identityDomain);
                long stopTime3 = System.currentTimeMillis();
                long elapsedTime3 = stopTime3 - startTime3;
                 logger.finest("\t" +"authenticate " + elapsedTime3);

              logger.finest("context authenticate finished ");

            }

            long startTime4 = System.currentTimeMillis();

            contextBehalf =
                    taskQueryService.authenticateOnBehalfOf(context, onBehalfOfUser);
            
            long stopTime4 = System.currentTimeMillis();
            long elapsedTime4 = stopTime4 - startTime4;
             logger.finest("\t" +"authenticateOnBehalfOf " + elapsedTime4);


        } catch (WorkflowException wfe) {

            logger.severe(wfe.getMessage());

            throw new RuntimeException(wfe);
        }

        logger.finest("[EINDE] authenticate()");

        return contextBehalf;
    }


    public void authenticateGroup(String group) {

        try {
            bpmClient.lookupGroup(group);
        } catch (Throwable t) {

            t.printStackTrace();

            logger.severe("Fout in authenticateUser() voor group: " + group +
                         " fout: " + t.getClass().getName() + " " +
                         t.getMessage(), t);

            throw new IllegalArgumentException("Groep: " + group +
                                               " onbekend");
        }
    }


    public void authenticateUser(String user) {

        try {

            bpmClient.lookupGroup(user);
        } catch (Throwable t) {

            logger.severe("Fout in authenticateUser() voor user: " + user +
                         " fout: " + t.getClass().getName() + " " +
                         t.getMessage(), t);

            throw new IllegalArgumentException("Persoon: " + user +
                                               " is onbekend");
        }
    }

    public Integer countTasks(IWorkflowContext context,String action) 
      throws WorkflowException  {

        int tasks = 0; 

        // Correct state
        List<String> correctStates = new ArrayList<String>();

        correctStates.add(IWorkflowConstants.TASK_STATE_ALERTED);
        correctStates.add(IWorkflowConstants.TASK_STATE_ASSIGNED);
        correctStates.add(IWorkflowConstants.TASK_SUBSTATE_ASSIGNED);
        correctStates.add(IWorkflowConstants.TASK_STATE_INFO_REQUESTED);
        correctStates.add(IWorkflowConstants.TASK_STATE_OUTCOME_UPDATED);

        // only these active states 
        Predicate predicateBasic = new Predicate(TableConstants.WFTASK_STATE_COLUMN, 
                         Predicate.OP_IN,
                         correctStates);
        // not stale tasks
        predicateBasic.addClause(Predicate.AND,
                             TableConstants.WFTASK_STATE_COLUMN,
                             Predicate.OP_NEQ,
                             IWorkflowConstants.TASK_STATE_STALE);
        if ( action != null  )   {
        predicateBasic.addClause(Predicate.AND,
                          TableConstants.WFTASK_TEXTATTRIBUTE1_COLUMN,
                          Predicate.OP_EQ,
                          action);
        }


        tasks = getTaskQueryService().countTasks(context, 
                                                 ITaskQueryService.AssignmentFilter.MY_AND_GROUP_ALL, 
                                                 null,
                                                 predicateBasic);

        logger.finest("[END] queryTasks()");
        getTaskQueryService().destroyWorkflowContext(context);
        return tasks;
    }



    public List<Task> queryTasks(IWorkflowContext context,
                                 String action,
                                 int noOfRecords,
                                 String orderBy,
                                 String searchString,
                                 String product,
                                 String shipper) 
                                throws WorkflowException {

        logger.finest("[START] queryTasks()");

        List<String> queryColumns = new ArrayList<String>();


        queryColumns.add(TableConstants.WFTASK_TITLE_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_PRIORITY_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_STATE_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_SUBSTATE_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_ACQUIREDBY_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TASKID_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TASKNUMBER_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_PROCESSNAME_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_PROCESSID_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_INSTANCEID_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_PROCESSVERSION_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_IDENTIFICATIONKEY_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_ASSIGNEEGROUPS_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_ASSIGNEEUSERS_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_EXPIRATIONDATE_COLUMN.getName());

        queryColumns.add(TableConstants.WFTASK_UPDATEDBY_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_UPDATEDDATE_COLUMN.getName());

        // specific "text string" columns
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE1_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE2_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE3_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE4_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE5_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE6_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_TEXTATTRIBUTE7_COLUMN.getName());

        queryColumns.add(TableConstants.WFTASK_URLATTRIBUTE1_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_URLATTRIBUTE2_COLUMN.getName());

        queryColumns.add(TableConstants.WFTASK_DATEATTRIBUTE1_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_DATEATTRIBUTE2_COLUMN.getName());

        queryColumns.add(TableConstants.WFTASK_NUMBERATTRIBUTE1_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_NUMBERATTRIBUTE2_COLUMN.getName());
        queryColumns.add(TableConstants.WFTASK_NUMBERATTRIBUTE3_COLUMN.getName());



        // Correct state
        List<String> correctStates = new ArrayList<String>();

        correctStates.add(IWorkflowConstants.TASK_STATE_ALERTED);
        correctStates.add(IWorkflowConstants.TASK_STATE_ASSIGNED);
        correctStates.add(IWorkflowConstants.TASK_SUBSTATE_ASSIGNED);
        correctStates.add(IWorkflowConstants.TASK_STATE_INFO_REQUESTED);
        correctStates.add(IWorkflowConstants.TASK_STATE_OUTCOME_UPDATED);

        // All Correct state
        List<String> correctStatesAll = new ArrayList<String>();

        correctStatesAll.add(IWorkflowConstants.TASK_STATE_ALERTED);
        correctStatesAll.add(IWorkflowConstants.TASK_STATE_ASSIGNED);
        correctStatesAll.add(IWorkflowConstants.TASK_SUBSTATE_ASSIGNED);
        correctStatesAll.add(IWorkflowConstants.TASK_STATE_INFO_REQUESTED);
        correctStatesAll.add(IWorkflowConstants.TASK_STATE_OUTCOME_UPDATED);
        correctStatesAll.add(IWorkflowConstants.TASK_STATE_EXPIRED);
        correctStatesAll.add(IWorkflowConstants.TASK_STATE_COMPLETED);

        Predicate predicateBasic = null;


        // only these active states 
        predicateBasic =
               new Predicate(TableConstants.WFTASK_STATE_COLUMN, 
                             Predicate.OP_IN,
                             correctStates);

        
        // not stale tasks
        predicateBasic.addClause(Predicate.AND,
                                 TableConstants.WFTASK_STATE_COLUMN,
                                 Predicate.OP_NEQ,
                                 IWorkflowConstants.TASK_STATE_STALE);
      
        if ( action != null  )   {
          predicateBasic.addClause(Predicate.AND,
                              TableConstants.WFTASK_TEXTATTRIBUTE1_COLUMN,
                              Predicate.OP_EQ,
                              action);

        }
        
 
      
        Predicate predicateSearch = null;
        
        // oude search waar er maar 1 zoekstring is
        if ( searchString != null && !"".equals(searchString) ) {
            
            
           predicateSearch = new Predicate(TableConstants.WFTASK_TEXTATTRIBUTE1_COLUMN,
                                           Predicate.OP_CONTAINS,
                                           searchString);
           predicateSearch.addClause(Predicate.OR,
                               TableConstants.WFTASK_TEXTATTRIBUTE2_COLUMN,
                               Predicate.OP_CONTAINS,
                               searchString);
           predicateSearch.addClause(Predicate.OR,
                               TableConstants.WFTASK_TEXTATTRIBUTE3_COLUMN,
                               Predicate.OP_CONTAINS,
                               searchString);
           predicateSearch.addClause(Predicate.OR,
                               TableConstants.WFTASK_TEXTATTRIBUTE4_COLUMN,
                               Predicate.OP_CONTAINS,
                               searchString);
           predicateSearch.addClause(Predicate.OR,
                               TableConstants.WFTASK_TEXTATTRIBUTE5_COLUMN,
                               Predicate.OP_CONTAINS,
                               searchString);

           if ( isParsableToInt(searchString) ){  
              predicateSearch.addClause(Predicate.OR,
                                        TableConstants.WFTASK_TASKNUMBER_COLUMN,
                                        Predicate.OP_EQ,
                                        searchString);
             predicateSearch.addClause(Predicate.OR,
                                       TableConstants.WFTASK_NUMBERATTRIBUTE1_COLUMN,
                                       Predicate.OP_EQ,
                                       searchString);
             predicateSearch.addClause(Predicate.OR,
                                       TableConstants.WFTASK_NUMBERATTRIBUTE2_COLUMN,
                                       Predicate.OP_EQ,
                                       searchString);
             predicateSearch.addClause(Predicate.OR,
                                       TableConstants.WFTASK_NUMBERATTRIBUTE3_COLUMN,
                                       Predicate.OP_EQ,
                                       searchString);

           }
  
        } else {
            if (product != null && !"".equalsIgnoreCase(product)) {
                if (predicateSearch == null) {
                    predicateSearch =
                            new Predicate(TableConstants.WFTASK_TEXTATTRIBUTE2_COLUMN,
                                          Predicate.OP_CONTAINS, product,true);
                } else {
                    predicateSearch.addClause(Predicate.AND,
                                              TableConstants.WFTASK_TEXTATTRIBUTE2_COLUMN,
                                              Predicate.OP_CONTAINS, product,true);
                }
            }
            if (shipper != null && !"".equalsIgnoreCase(shipper)) {
                if (predicateSearch == null) {
                    predicateSearch =
                            new Predicate(TableConstants.WFTASK_TEXTATTRIBUTE3_COLUMN,
                                          Predicate.OP_CONTAINS, shipper,true);
                } else {
                    predicateSearch.addClause(Predicate.AND,
                                              TableConstants.WFTASK_TEXTATTRIBUTE3_COLUMN,
                                              Predicate.OP_CONTAINS, shipper,true);
                }
            }

        }
        
        Predicate predicate = null;
        if ( predicateSearch == null ) {
          predicate = predicateBasic; 
        } else {
          predicate = new Predicate (predicateBasic,Predicate.AND,predicateSearch);
        }
          
        
        // Ordering
        Ordering taskOrdering = null;
        logger.finest("orderBy: " + orderBy);
        if ( "PRIO".equalsIgnoreCase(orderBy) ) {
           taskOrdering = new Ordering(TableConstants.WFTASK_PRIORITY_COLUMN, true,false); 
           taskOrdering.addClause(TableConstants.WFTASK_TASKNUMBER_COLUMN, true,false);
        } else if  ("ID".equalsIgnoreCase(orderBy) ) { 
           taskOrdering = new Ordering(TableConstants.WFTASK_TASKNUMBER_COLUMN, false,true);
        } else if ("ESC_DESC".equalsIgnoreCase(orderBy)  ) {
            taskOrdering = new Ordering(TableConstants.WFTASK_EXPIRATIONDATE_COLUMN, false, false);
        }  else if ("ESC_ASC".equalsIgnoreCase(orderBy) ) {
            taskOrdering = new Ordering(TableConstants.WFTASK_EXPIRATIONDATE_COLUMN, true, false);
        } else if ("CRE_DESC".equalsIgnoreCase(orderBy)  ) {
              taskOrdering = new Ordering(TableConstants.WFTASK_CREATEDDATE_COLUMN, false, false);
        }  else if ("CRE_ASC".equalsIgnoreCase(orderBy) ) {
              taskOrdering = new Ordering(TableConstants.WFTASK_CREATEDDATE_COLUMN, true, false);
        }  else if ("DATE_ASC".equalsIgnoreCase(orderBy) ) {
              taskOrdering = new Ordering(TableConstants.WFTASK_DATEATTRIBUTE1_COLUMN, true, false);
        }

        List<ITaskQueryService.OptionalInfo> optionalfinest = new ArrayList<ITaskQueryService.OptionalInfo>();
        optionalfinest.add(ITaskQueryService.OptionalInfo.CUSTOM_ACTIONS);
        optionalfinest.add(ITaskQueryService.OptionalInfo.COMMENTS);
        optionalfinest.add(ITaskQueryService.OptionalInfo.PAYLOAD);
        optionalfinest.add(ITaskQueryService.OptionalInfo.DISPLAY_INFO);

        List<Task> tasks =
            getTaskQueryService().queryTasks(context, 
                                             queryColumns,
                                             optionalfinest,
                                             ITaskQueryService.AssignmentFilter.MY_AND_GROUP_ALL,
                                             null, 
                                             predicate, 
                                             taskOrdering,
                                             0,
                                             noOfRecords);

        logger.finest("[END] queryTasks()");

        getTaskQueryService().destroyWorkflowContext(context);

        return tasks;
    }

    public void acquireTask(IWorkflowContext context, String taskId,
                            boolean isAllowedToBeAcquiredByUser) throws StaleObjectException,
                                                                        WorkflowException {

        logger.finest("[START] acquireTask()");

        logger.finest("\t" + "Context: " + context);
        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "Allowed to be acquired by user: " +
                     isAllowedToBeAcquiredByUser);

        if (isAllowedToBeAcquiredByUser) {

            String user = context.getUser();
            Task task = getTaskDetail(context, taskId);
            String aquiredBy = task.getSystemAttributes().getAcquiredBy();
            List assignedUsers = task.getSystemAttributes().getAssigneeUsers();

            if (aquiredBy != null && aquiredBy.equals(user)) {
                return;
            }

            if (assignedUsers != null && assignedUsers.size() == 1 &&
                assignedUsers.get(0).equals(user)) {
                return;
            }
        }

        getTaskService().acquireTask(context, taskId);
        getTaskQueryService().destroyWorkflowContext(context);
        logger.finest("[END] acquireTask()");
    }


    public void releaseTask(IWorkflowContext context,
                            String taskId) throws StaleObjectException,
                                                  WorkflowException {

        getTaskService().releaseTask(context, taskId);
        getTaskQueryService().destroyWorkflowContext(context);

    }



    public void assignTask(IWorkflowContext context, String taskId,
                           String assignTo,
                           boolean isGroup) throws StaleObjectException,
                                                   WorkflowException {

        List assignees = new ArrayList();

        ITaskAssignee assigneeUser =
            new TaskAssignee(assignTo, isGroup ? IWorkflowConstants.IDENTITY_TYPE_GROUP :
                                       IWorkflowConstants.IDENTITY_TYPE_USER);
        assignees.add(assigneeUser);

        getTaskService().reassignTask(context, taskId, assignees);
        getTaskQueryService().destroyWorkflowContext(context);

    }



    public void completeTask(IWorkflowContext context, String taskId,
                             String outcome, String comment) throws StaleObjectException,
                                                    WorkflowException {

        if ( comment != null && !comment.equalsIgnoreCase("")  ) {
          getTaskService().addComment(context, taskId, comment);
        }
        getTaskService().updateTaskOutcome(context, taskId, outcome);
        getTaskQueryService().destroyWorkflowContext(context);

    }



    public Task getTaskDetail(IWorkflowContext context,
                              String taskId) throws WorkflowException {
        Task task = getTaskQueryService().getTaskDetailsById(context, taskId);
        getTaskQueryService().destroyWorkflowContext(context);
        return task;
    }


    public void validateTaskStates(IWorkflowContext context,
                                   List<String> taskIds) throws InvalidTaskStateException,
                                                                WorkflowException {

        logger.finest("[START] validateTaskStates()");

        if (taskIds == null || taskIds.size() == 0) {

            throw new IllegalArgumentException();
        }

        // Temporary objects for iteration
        Task currentTask = null;
        String currentTaskState = null;

        // Itereer over tasks
        for (String taskId : taskIds) {

            currentTask =
                    getTaskQueryService().getTaskDetailsById(context, taskId);

            currentTaskState = currentTask.getSystemAttributes().getState();

            if (IWorkflowConstants.TASK_STATE_STALE.equals(currentTaskState) ||
                IWorkflowConstants.TASK_STATE_COMPLETED.equals(currentTaskState) ||
                IWorkflowConstants.TASK_STATE_ERRORED.equals(currentTaskState) ||
                IWorkflowConstants.TASK_STATE_EXPIRED.equals(currentTaskState) ||
                IWorkflowConstants.TASK_STATE_SUSPENDED.equals(currentTaskState)) {
                getTaskQueryService().destroyWorkflowContext(context);
                throw new InvalidTaskStateException("Taak: " + taskId +
                                                    " heeft incorrecte status: " +
                                                    currentTaskState);
            }
        }
        getTaskQueryService().destroyWorkflowContext(context);

        logger.finest("[END] validateTaskStates()");
    }


    public void validateTasksAreAcquired(IWorkflowContext context,
                                         List<String> taskIds) throws TaskNotAssignedException,
                                                                      WorkflowException {

        logger.finest("[START] validateTasksAreAcquired()");

        if (taskIds == null || taskIds.size() == 0) {

            throw new IllegalArgumentException();
        }

        String user = context.getUser();

        // Temporary objects for iteration
        Task currentTask = null;
        String currentAcquiredBy = null;
        List<String> currentAssignedUsers = null;

        // Itereer over tasks
        for (String taskId : taskIds) {

            currentTask =
                    getTaskQueryService().getTaskDetailsById(context, taskId);

            currentAcquiredBy =
                    currentTask.getSystemAttributes().getAcquiredBy();

            if (user.equals(currentAcquiredBy)) {

                continue;
            }

            if (currentAssignedUsers == null ||
                !currentAssignedUsers.contains(user)) {
                getTaskQueryService().destroyWorkflowContext(context);
                throw new TaskNotAssignedException("Task: " + taskId +
                                                   " not assigned to: " +
                                                   user);
            }
        }
        getTaskQueryService().destroyWorkflowContext(context);
        logger.finest("[END] validateTasksAreAcquired()");
    }



    public Task updateTask(IWorkflowContext context,
                           Task task) throws StaleObjectException,
                                             WorkflowException {

        task = getTaskService().updateTask(context, task);
        getTaskQueryService().destroyWorkflowContext(context);
        return task;
    }



    public List<BPMUser> getAllBPELUsers(String groupName) throws BPMIdentityException,
                                                                  BPMIdentityNotFoundException,
                                                                  ServicesException {
        BPMGroup group = bpmClient.lookupGroup(groupName);
        return group.getParticipants(false);
    }

    public BPMUser getUser(String username) throws BPMIdentityException,
                                                    BPMIdentityNotFoundException,
                                                   ServicesException {
        BPMUser user =  bpmClient.lookupUser(username);
        if ( user != null ) {
          logger.finest("return user: "+user.getName());  
        } else {
          logger.finest("return an empty user ");   
        }
        return user;
    }


    public List<BPMUser> getAllBPELUsers() throws BPMIdentityException,
                                                  BPMIdentityNotFoundException,
                                                  ServicesException {
        return bpmClient.searchUsers(AttributeNames.NAME_ATTRNAME, "*");
    }


    public List<BPMGroup> getAllBPELGroups() throws BPMIdentityException,
                                                    BPMUnsupportedAttributeException,
                                                    ServicesException {
        List<BPMGroup> groups =
            bpmClient.searchGroups(AttributeNames.NAME_ATTRNAME, "*");
        if (groups == null) {
            return null;
        }

        return groups;
    }
    
    public boolean isParsableToInt(String i) {
     try {
        Integer.parseInt(i);
        return true;
     } catch(NumberFormatException nfe)  {
        return false;
     }
    }

}
