package stsquestbuilder.model;

import javafx.beans.property.StringProperty;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;

/**
 * @author William
 */
public interface StatusCheckable {
    
    public boolean getEmpty();
    
    public void setNotEmpty();
    
    public StringProperty getNameProperty();
    
    public StatusCheckableProtocol getStatusCheckableAsProtobuf();
    
    /**
     * Used by the factory to guarantee that the status checkable
     * stores amount information
     * @param amount 
     */
    public void setAmount(int amount);
}
