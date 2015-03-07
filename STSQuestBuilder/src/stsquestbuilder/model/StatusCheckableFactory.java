package stsquestbuilder.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;

/**
 *
 * @author William
 */
public class StatusCheckableFactory {
    
    public enum StatusType {
        ActionCheckable("Action Checkable"),
        TierCheckable("Tier Checkable"),
        LevelCheckable("Level Checkable"),
        EMPTY("Empty Check");
        
        private StringProperty name;
        
        StatusType(String n) {
            name = new SimpleStringProperty();
            name.setValue(n);
        }
        
        public StringProperty getNameProperty() {
            return name;
        }
        
    }
    
    public static StatusType getStatusTypeOfCheck(StatusCheckable status) {
        if (status.getEmpty()) {
            return StatusType.EMPTY;
        }
        
        if (status instanceof ActionCheckable) {
            return StatusType.ActionCheckable;
        } else if (status instanceof TierCheckable) {
            return StatusType.TierCheckable;
        } if (status instanceof LevelCheckable) {
            return StatusType.LevelCheckable;
        }
        
        return StatusType.ActionCheckable;
    }
    
    public static StatusCheckable getStatusFromProtobuf(StatusCheckableProtocol proto) {
        StatusCheckable status = null;
        if (proto.hasAction()) {
            status = new ActionCheckable(proto);
        } else if (proto.hasTier()) {
            status = new TierCheckable(proto);
        } else if (proto.hasLevel()) {
            status = new LevelCheckable(proto);
        }
        
        if (status == null)
            return null;
         
        if (proto.hasAmount()) {
            status.setAmount(proto.getAmount());
        }
        
        status.setNotEmpty();
        return status;
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
        StatusCheckable check = new ActionCheckable();
        check.setNotEmpty();
        return check;
    }
    
    /**
     * Gets a TierCheckable
     * @return 
     */
    public static StatusCheckable getTierStatus() {
        StatusCheckable check = new TierCheckable();
        return check;
    }
    
    /**
     * Gets a LevelCheckable
     * @return 
     */
    public static StatusCheckable getLevelStatus() {
        StatusCheckable check = new LevelCheckable();
        return check;
    }
    
}
