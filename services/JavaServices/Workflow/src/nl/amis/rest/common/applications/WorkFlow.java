package nl.amis.rest.common.applications;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import nl.amis.rest.common.workflow.Purchase;
import nl.amis.rest.common.workflow.Identity;

import nl.amis.rest.common.workflow.Repair;

import oracle.adf.share.logging.ADFLogger;


public class WorkFlow extends Application {

    private static ADFLogger logger =
        ADFLogger.createADFLogger(WorkFlow.class);

    
    @Override
    public Set<Class<?>> getClasses() {
        logger.fine("Application getClasses");  
        
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register root resource
        classes.add(Purchase.class);
        classes.add(Repair.class);
        classes.add(Identity.class);
        return classes;
    }

}
