package nl.amis.soa.workflow.tasks.test;

import java.util.List;

import nl.amis.soa.workflow.tasks.entities.PurchaseTask;
import nl.amis.soa.workflow.tasks.entities.Task;
import nl.amis.soa.workflow.tasks.services.HumanTaskClient;

import oracle.bpel.services.workflow.WorkflowException;

public class QueryPurchase {
    public QueryPurchase() {
        super();
    }

    public static void main(String[] args) {
        QueryPurchase query = new QueryPurchase();
        try {
            query.testQuery();
        } catch (WorkflowException e) {
           e.printStackTrace(); 
        }
    }

    public void testQuery() throws WorkflowException {
        HumanTaskClient client = new HumanTaskClient();

        List<PurchaseTask> tasks = client.getHumanTasksPurchase("user1", null, null, null, 500, "DATE_ASC")  ;      

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
