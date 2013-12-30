package nl.amis.soa.workflow.tasks.test;

import java.util.List;

import nl.amis.soa.workflow.tasks.entities.PurchaseTask;
import nl.amis.soa.workflow.tasks.entities.RepairTask;
import nl.amis.soa.workflow.tasks.entities.Task;
import nl.amis.soa.workflow.tasks.services.HumanTaskClient;

import oracle.bpel.services.workflow.WorkflowException;

public class QueryRepair {
    public QueryRepair() {
        super();
    }

    public static void main(String[] args) throws WorkflowException {
        QueryRepair queryRepair = new QueryRepair();
        queryRepair.testQuery();
    }

    public void testQuery() throws WorkflowException {
        HumanTaskClient client = new HumanTaskClient();

        List<RepairTask> tasks = client.getHumanTasksRepair("user2", null, null, null, 500, "DATE_ASC")  ;      

        for (int i = 0; i < tasks.size(); i++) {
                   Task task = tasks.get(i);
                   
                   System.out.println(" task id " + task.getTaskId() +
                                      " acquired " + task.getAcquiredBy()+
                                      " task " + task.getTaskNumber() + 
                                      " title " + task.getTitle() + 
                                      " created " + task.getCreated().getTime().toString() + 
                                      " assignee "+task.getAssignedToStr() +
                                      " state "+task.getState() +
                                      " outcome " + task.getOutcome() );

               }
    }



}
