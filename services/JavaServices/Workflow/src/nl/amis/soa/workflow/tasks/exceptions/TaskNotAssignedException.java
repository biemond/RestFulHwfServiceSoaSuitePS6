package nl.amis.soa.workflow.tasks.exceptions;


public class TaskNotAssignedException extends Exception {
    
    /**
     * Constructor.
     * 
     * @param message
     */
    public TaskNotAssignedException(String message) {
    
        super(message);
    }
}
