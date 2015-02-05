package stsquestbuilder.model;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;

/**
 *
 * @author William
 */
public class StatusCheckableFactory {
    
    public enum StatusType {
        ActionCheckable,
        TEST;
    }
    
    public static StatusType getStatusTypeOfCheck(StatusCheckable status) {
        if(status instanceof ActionCheckable) {
            return StatusType.ActionCheckable;
        }
        
        return StatusType.ActionCheckable;
    }
    
    public static StatusCheckable getStatusFromProtobuf(StatusCheckableProtocol proto) {
        if(proto.hasAction()) {
            return new ActionCheckable(proto);
        }
        return null;
    }
    
    /**
     * Gets an empty, undefined status
     * @return 
     */
    public static StatusCheckable getEmptyStatus() {
        return new ActionCheckable();
    }
    
    /**
     * Gets an ActionCheckable
     * @return 
     */
    public static StatusCheckable getActionStatus() {
        return new ActionCheckable();
    }
}
