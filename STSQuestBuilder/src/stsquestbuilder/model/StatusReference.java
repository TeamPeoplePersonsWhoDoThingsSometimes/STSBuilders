package stsquestbuilder.model;

/**
 * This class is a hack.
 * It is used to maintain continuity across screens when a status checkable model
 * changes from one implementation to another.  There almost certainly is a better
 * way to do this...
 *
 * @author William
 */
public class StatusReference {
    private StatusCheckable status;
    
    public StatusReference() {
        status = null;
    }
    
    public StatusReference(StatusCheckable check) {
        status = check;
    }
    
    public StatusCheckable getStatus() {
        return status;
    }
    
    public void setStatus(StatusCheckable check) {
        status = check;
    }
    
    @Override
    public String toString() {
        return status.toString();
    }
}
