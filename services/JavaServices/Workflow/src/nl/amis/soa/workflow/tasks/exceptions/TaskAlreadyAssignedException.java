package nl.amis.soa.workflow.tasks.exceptions;


public class TaskAlreadyAssignedException extends Exception {
    
    /**
     * Constructor.
     * 
     * @param message
     */
    public TaskAlreadyAssignedException(String message) {
    
        super(message);
    }
}
