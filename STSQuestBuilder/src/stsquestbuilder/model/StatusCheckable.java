package stsquestbuilder.model;

import javafx.beans.property.StringProperty;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;

/**
 * @author William
 */
public interface StatusCheckable {
    
    public void setNotEmpty();
    
    public StringProperty getNameProperty();
    
    public StatusCheckableProtocol getStatusCheckableAsProtobuf();
}
