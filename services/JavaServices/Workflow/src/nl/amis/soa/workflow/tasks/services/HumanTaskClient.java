package nl.amis.soa.workflow.tasks.services;

import java.util.ArrayList;
import java.util.List;

import nl.amis.soa.workflow.tasks.entities.PurchaseTask;
import nl.amis.soa.workflow.tasks.entities.Group;
import nl.amis.soa.workflow.tasks.entities.RepairTask;
import nl.amis.soa.workflow.tasks.entities.User;
import nl.amis.soa.workflow.tasks.utility.BPELWorkflowServices;
import nl.amis.soa.workflow.tasks.utility.TaskConvertor;

import oracle.adf.share.logging.ADFLogger;

import oracle.bpel.services.workflow.WorkflowException;
import oracle.bpel.services.workflow.task.model.Task;
import oracle.bpel.services.workflow.verification.IWorkflowContext;

import oracle.tip.pc.services.identity.BPMGroup;
import oracle.tip.pc.services.identity.BPMUser;


public class HumanTaskClient {

    private static ADFLogger logger =
        ADFLogger.createADFLogger(HumanTaskClient.class);

    private static final int MAXIMUM_NUMBER_OF_TASKS = 500;
    public static final String purchaseAction = "purchase";
    public static final String repairAction = "repair";


    private BPELWorkflowServices workflowServices;
    private TaskConvertor taskConvertor;

    

    /**
     * Constructor.
     */
    public HumanTaskClient() {

        logger.finest("[START] HumanTaskServicesBean()");

        workflowServices = new BPELWorkflowServices();
        taskConvertor = new TaskConvertor();
        logger.finest("[END] HumanTaskServicesBean()");
    }

    public User getUser(String wfUser)  {
        try {
          BPMUser bpmUser = workflowServices.getUser(wfUser);
          logger.finest("return user ");        
          if ( bpmUser != null ) {  
             logger.finest("begin conversion user " + bpmUser.getName());      
             User user = new User();

             if ( bpmUser.getName() != null ) { 
               user.setName(bpmUser.getName());
             }
             if ( bpmUser.getDisplayName() != null ) { 
               user.setDisplayName(bpmUser.getDisplayName());
             }
             if ( bpmUser.getEmail() != null ) { 
               user.setEmail(bpmUser.getEmail());
             }
             if ( bpmUser.getFirstName() != null ) { 
               user.setFirstName(bpmUser.getFirstName());
             }
             if ( bpmUser.getLastName() != null ) { 
               user.setLastName(bpmUser.getLastName());
             }
             List<BPMGroup> bpmGroups =bpmUser.getGroups(false);
             List<Group> groups = new ArrayList();
             for ( BPMGroup group : bpmGroups  ) {
                  groups.add(new Group(group.getName()));
                 logger.finest("found group  "+group.getName());   
             }
             user.setGroups(groups); 
             logger.finest("after conversie user ");     
             return user;
          } else {
                return null;
          }
        } catch (Exception t) {
            logger.severe("Fout in getUser(): ");
            t.printStackTrace();
            throw new RuntimeException("Fout in getUser()");
        }
    }

    public Integer getHumanTasksPurchaseTotals(String user) {
        
        IWorkflowContext context = workflowServices.authenticate(user);
        try {
            return workflowServices.countTasks(context, purchaseAction );
        } catch (WorkflowException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<PurchaseTask> getHumanTasksPurchase(String user, 
                                                    String searchString, 
                                                    String product,
                                                    String shipper,
                                                    int noOfRecords,
                                                    String orderBy) {

        long startTime = System.currentTimeMillis();
        noOfRecords =
                (noOfRecords == 0 ? MAXIMUM_NUMBER_OF_TASKS : noOfRecords);

        if (orderBy == null) {
            orderBy = "DATE_ASC";
        }


        logger.finest("[START] getHumanTasks()");

        logger.finest("\t" + "User: " + user);
        logger.finest("\t" + "Number of records: " + noOfRecords);
        logger.finest("Number of records: " + noOfRecords);

        validateUser(user, false);

        List<PurchaseTask> resultSet = null;

        try {

            IWorkflowContext context = workflowServices.authenticate(user);

            List<Task> tasks =
                workflowServices.queryTasks(context, purchaseAction, noOfRecords,
                                            orderBy, searchString, product,shipper);
 

 
            resultSet = new ArrayList<PurchaseTask>(tasks.size());

            for (Task task : tasks) {
                resultSet.add(taskConvertor.convertPurchaseTask(task));
            }


        } catch (Throwable t) {
            logger.severe(t.getMessage(), t);
            throw new RuntimeException(t.getMessage());
        }

        logger.finest("\t" + "total tasks: " + resultSet.size());
        logger.finest("[END] getHumanTasks()");

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[END] getHumanTasks() in " + elapsedTime);

        return resultSet;
    }

    public Integer getHumanTasksRepairTotals(String user) {
        
        IWorkflowContext context = workflowServices.authenticate(user);
        try {
            return workflowServices.countTasks(context, repairAction );
        } catch (WorkflowException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<RepairTask> getHumanTasksRepair(String user, 
                                                    String searchString, 
                                                    String product,
                                                    String serialNumber,
                                                    int noOfRecords,
                                                    String orderBy) {

        long startTime = System.currentTimeMillis();
        noOfRecords =
                (noOfRecords == 0 ? MAXIMUM_NUMBER_OF_TASKS : noOfRecords);

        if (orderBy == null) {
            orderBy = "DATE_ASC";
        }


        logger.finest("[START] getHumanTasks()");

        logger.finest("\t" + "User: " + user);
        logger.finest("\t" + "Number of records: " + noOfRecords);
        logger.finest("Number of records: " + noOfRecords);

        validateUser(user, false);

        List<RepairTask> resultSet = null;

        try {

            IWorkflowContext context = workflowServices.authenticate(user);

            List<Task> tasks =
                workflowServices.queryTasks(context, repairAction, noOfRecords,
                                            orderBy, searchString, product,serialNumber);
    

    
            resultSet = new ArrayList<RepairTask>(tasks.size());

            for (Task task : tasks) {
                resultSet.add(taskConvertor.convertRepairTask(task));
            }


        } catch (Throwable t) {
            logger.severe(t.getMessage(), t);
            throw new RuntimeException(t.getMessage());
        }

        logger.finest("\t" + "total tasks: " + resultSet.size());
        logger.finest("[END] getHumanTasks()");

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[END] getHumanTasks() in " + elapsedTime);

        return resultSet;
    }



    public void assignTask(String taskId, String currentUser,
                           String futureUser, boolean isGroup) {
        long startTime = System.currentTimeMillis();

        logger.finest("[START] assignTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "Is group: " + isGroup);

        validateUser(currentUser, false);
        validateUser(futureUser, isGroup);

        try {

            IWorkflowContext context =
                workflowServices.authenticate(currentUser);

            List<String> taskIds = new ArrayList<String>();

            taskIds.add(taskId);

            workflowServices.validateTaskStates(context, taskIds);
            workflowServices.validateTasksAreAcquired(context, taskIds);

            context = workflowServices.authenticate(null);

            // Assign aan group via reassign
            if (isGroup) {

                workflowServices.assignTask(context, taskId, futureUser,
                                            isGroup);
            }
            // Assign aan persoon via release en acquire
            else {

                releaseTask(taskId, currentUser);

                acquireTask(taskId, futureUser, true);
            }
        } catch (Throwable t) {

            logger.severe(t.getMessage(), t);
            throw new RuntimeException(t.getMessage());
        }

        logger.finest("[END] assignTask()");

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[END] assignTask() in " + elapsedTime);

    }

    public void acquireTask(String taskId, String user,
                            boolean isAllowedToBeAcquiredByUser) {
        long startTime = System.currentTimeMillis();

        logger.finest("[START] acquireTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "User: " + user);
        logger.finest("\t" + "is allowed to be acquired by user: " +
                    isAllowedToBeAcquiredByUser);

        long startTime2 = System.currentTimeMillis();
        validateUser(user, false); 

        long stopTime2 = System.currentTimeMillis();
        long elapsedTime2 = stopTime2 - startTime2;
        logger.finest("\t" +"acquireTask() validateUser in " + elapsedTime2);

        try {
            long startTime3 = System.currentTimeMillis();
            IWorkflowContext context = workflowServices.authenticate(user);
            long stopTime3 = System.currentTimeMillis();
            long elapsedTime3 = stopTime3 - startTime3;
             logger.finest("\t" +"acquireTask() authenticate in " + elapsedTime3);


            List<String> taskIds = new ArrayList<String>();

            taskIds.add(taskId);

            long startTime4 = System.currentTimeMillis();
            workflowServices.validateTaskStates(context, taskIds);
            long stopTime4 = System.currentTimeMillis();
            long elapsedTime4 = stopTime4 - startTime4;
            logger.finest("\t" +"acquireTask() validateTaskStates in " + elapsedTime4);


            long startTime5 = System.currentTimeMillis();
            workflowServices.acquireTask(context, taskId,
                                         isAllowedToBeAcquiredByUser);
            long stopTime5 = System.currentTimeMillis();
            long elapsedTime5 = stopTime5 - startTime5;
            logger.finest("\t" +"acquireTask() acquireTask in " + elapsedTime5);



        } catch (Throwable t) {

            logger.severe(t.getMessage(), t);
            throw new RuntimeException("this task is already acquired");
        }

        logger.finest("[END] acquireTask()");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[END] acquireTask() in " + elapsedTime);
    }

    public PurchaseTask acquirePurchaseTask(String taskId, 
                                          String user,
                                          boolean isAllowedToBeAcquiredByUser) {
        long startTime = System.currentTimeMillis();

        logger.finest("[START] acquireTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "User: " + user);
        logger.finest("\t" + "is allowed to be acquired by user: " +
                    isAllowedToBeAcquiredByUser);

        long startTime2 = System.currentTimeMillis();
        validateUser(user, false); 

        long stopTime2 = System.currentTimeMillis();
        long elapsedTime2 = stopTime2 - startTime2;
        logger.finest("\t" +"acquireTask() validateUser in " + elapsedTime2);
        PurchaseTask purchase = null;
        try {
            long startTime3 = System.currentTimeMillis();
            IWorkflowContext context = workflowServices.authenticate(user);
            long stopTime3 = System.currentTimeMillis();
            long elapsedTime3 = stopTime3 - startTime3;
             logger.finest("\t" +"acquireTask() authenticate in " + elapsedTime3);


            List<String> taskIds = new ArrayList<String>();

            taskIds.add(taskId);

            long startTime4 = System.currentTimeMillis();
            workflowServices.validateTaskStates(context, taskIds);
            long stopTime4 = System.currentTimeMillis();
            long elapsedTime4 = stopTime4 - startTime4;
            logger.finest("\t" +"acquireTask() validateTaskStates in " + elapsedTime4);


            long startTime5 = System.currentTimeMillis();

            Task task = workflowServices.acquireTask2(context, taskId,
                                         isAllowedToBeAcquiredByUser);
            purchase = taskConvertor.convertPurchaseTask(task);  
            long stopTime5 = System.currentTimeMillis();
            long elapsedTime5 = stopTime5 - startTime5;
            logger.finest("\t" +"acquireTask() acquireTask in " + elapsedTime5);
        } catch (Throwable t) {

            logger.severe(t.getMessage(), t);
            throw new RuntimeException("this task is already acquired");
        }

        logger.finest("[END] acquireTask()");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[END] acquireTask() in " + elapsedTime);
        return purchase; 
    }

    public RepairTask acquireRepairTask(String taskId, 
                                          String user,
                                          boolean isAllowedToBeAcquiredByUser) {
        long startTime = System.currentTimeMillis();

        logger.finest("[START] acquireTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "User: " + user);
        logger.finest("\t" + "is allowed to be acquired by user: " +
                    isAllowedToBeAcquiredByUser);

        long startTime2 = System.currentTimeMillis();
        validateUser(user, false); 

        long stopTime2 = System.currentTimeMillis();
        long elapsedTime2 = stopTime2 - startTime2;
        logger.finest("\t" +"acquireTask() validateUser in " + elapsedTime2);
        RepairTask repair = null;
        try {
            long startTime3 = System.currentTimeMillis();
            IWorkflowContext context = workflowServices.authenticate(user);
            long stopTime3 = System.currentTimeMillis();
            long elapsedTime3 = stopTime3 - startTime3;
             logger.finest("\t" +"acquireTask() authenticate in " + elapsedTime3);


            List<String> taskIds = new ArrayList<String>();

            taskIds.add(taskId);

            long startTime4 = System.currentTimeMillis();
            workflowServices.validateTaskStates(context, taskIds);
            long stopTime4 = System.currentTimeMillis();
            long elapsedTime4 = stopTime4 - startTime4;
            logger.finest("\t" +"acquireTask() validateTaskStates in " + elapsedTime4);


            long startTime5 = System.currentTimeMillis();

            Task task = workflowServices.acquireTask2(context, taskId,
                                         isAllowedToBeAcquiredByUser);
            repair = taskConvertor.convertRepairTask(task);  
            long stopTime5 = System.currentTimeMillis();
            long elapsedTime5 = stopTime5 - startTime5;
            logger.finest("\t" +"acquireTask() acquireTask in " + elapsedTime5);
        } catch (Throwable t) {

            logger.severe(t.getMessage(), t);
            throw new RuntimeException("this task is already acquired");
        }

        logger.finest("[END] acquireTask()");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[END] acquireTask() in " + elapsedTime);
        return repair; 
    }


    public void releaseTask(String taskId, String user) {
        long startTime = System.currentTimeMillis();

        logger.finest("[START] releaseTask()");
        logger.finest("\t" + "Task id: " + taskId);

        validateUser(user, false);

        try {

            IWorkflowContext context = workflowServices.authenticate(user);

            List<String> taskIds = new ArrayList<String>();

            taskIds.add(taskId);

            workflowServices.validateTaskStates(context, taskIds);
            workflowServices.validateTasksAreAcquired(context, taskIds);

            workflowServices.releaseTask(context, taskId);
        } catch (Throwable t) {

            logger.severe(t.getMessage(), t);
            throw new RuntimeException(t.getMessage());
        }

        logger.finest("[EINDE] releaseTask()");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[EINDE] releaseTask() in " + elapsedTime);


    }


    public void completeTask(String taskId, String outcome, String user,
                             String comment) {
        long startTime = System.currentTimeMillis();


        logger.finest("[START] completeTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "Task outcome: " + outcome);

        validateUser(user, false);

        try {

            // Validate task and outcome
            if (taskId == null || outcome == null) {

                throw new IllegalArgumentException("Ongeldige waarden voor taskId: " +
                                                   taskId + " en outcome: " +
                                                   outcome);
            }

            IWorkflowContext context = workflowServices.authenticate(user);

            // Check whether tasks are assigned
            List<String> taskIds = new ArrayList<String>();
            taskIds.add(taskId);

            workflowServices.validateTaskStates(context, taskIds);
            workflowServices.validateTasksAreAcquired(context, taskIds);

            // Complete tasks
            workflowServices.completeTask(context, taskId,
                                          outcome.toUpperCase(), comment);
        } catch (Throwable t) {

            logger.severe(t.getMessage(), t);
            throw new RuntimeException(t.getMessage());
        }

        logger.finest("[EINDE] completeTask()");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        logger.finest("[EINDE] completeTask() in " + elapsedTime);
    }


    private void validateUser(String name, boolean isGroup) {

        logger.finest("[START] validateUser()");

        logger.finest("\t" + "Meegegeven gebruiker/groep: " + name);


        if (name == null || name.trim().length() == 0) {

            throw new IllegalArgumentException("Gebruiker of groep is null");
        }


        logger.finest("[EINDE] validateUser()");
    }


    public void setErrorCodeTask(String errorCode, String taskId,
                                 String user) {

        logger.finest("[START] setErrorCodeTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "Error code: " + errorCode);
        logger.finest("\t" + "User: " + user);

        setTextAttributeOnTask("ERROR_CODE", errorCode, taskId, user);

        logger.finest("[EINDE] setErrorCodeTask()");
    }


    public void setTextAttributeOnTask(String attribute, String value,
                                       String taskId, String user) {

        logger.finest("[START] setTextAttributeOnTask()");

        logger.finest("\t" + "Task id: " + taskId);
        logger.finest("\t" + "Attribute: " + attribute);
        logger.finest("\t" + "Value: " + value);
        logger.finest("\t" + "User: " + user);

        validateUser(user, false);

        try {

            IWorkflowContext context = workflowServices.authenticate(user);

            List<String> taskIds = new ArrayList<String>();

            taskIds.add(taskId);

            workflowServices.validateTaskStates(context, taskIds);
            workflowServices.validateTasksAreAcquired(context, taskIds);

            Task task = workflowServices.getTaskDetail(context, taskId);


            workflowServices.updateTask(context, task);
        } catch (Throwable t) {

            logger.severe("Fout in setErrorCodeTask(): " + t.getMessage(), t);
            throw new RuntimeException("Fout in setErrorCodeTask()");
        }

        logger.finest("[EINDE] setTextAttributeOnTask()");
    }



}
