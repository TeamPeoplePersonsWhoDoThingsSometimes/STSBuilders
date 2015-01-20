package stsquestbuilder.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import stsquestbuilder.protocolbuffers.QuestProtobuf;


/**
 * Action class based on the unity and protobuf implementation with slight variation-
 * the action stores an occurrence int to simplify interfacing between this action class
 * and the editor, but should be converted to a list of that many actions before
 * being stored as a protobuf message
 * 
 * @author William
 */
public class Action {
    private final StringProperty actionDescriptor;
    
    private ActionType actionType;
    
    private DirectObject directObject;
    
    private int occurrence;
    
    public Action() {
        actionType = null;
        directObject = null;
        occurrence = -1;
        
        actionDescriptor = new SimpleStringProperty("New Action");
    }
    
    public Action(ActionType type, DirectObject object, int times) {
        actionType = type;
        directObject = object;
        occurrence = times;
        
        actionDescriptor = new SimpleStringProperty();
        updateDescriptor();
    }
    
    /**
     * Constructor provides for simple conversion of ActionProtocol protobufs into
     * Action objhects
     * @param action the protobuf to build from
     */
    public Action(QuestProtobuf.ActionProtocol action, int times) {
        actionType = ActionType.valueOf(action.getType().toString());
        directObject = new DirectObject(action.getTarget());
        occurrence = times;
        actionDescriptor = new SimpleStringProperty();
        updateDescriptor();
    }

    /**
     * Setup this action with all the information from the given action.
     * Potentially useful in data sources and also useful for instantiating new Actions
     * over again without running into exceptions due to other fields being null
     * as the descriptor update occurs after the fields are all set
     * 
     * @param action the action to update with
     */
    public void setAction(Action action) {
        this.actionType = action.actionType;
        this.directObject = action.directObject;
        this.occurrence = action.occurrence;
        updateDescriptor();
    }
    
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
        updateDescriptor();
    }

    public DirectObject getDirectObject() {
        return directObject;
    }

    public void setDirectObject(DirectObject directObject) {
        this.directObject = directObject;
        updateDescriptor();
    }
 
    public int getOccurrence() {
        return occurrence;
    }  
    
    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
        updateDescriptor();
    }
    
    public StringProperty getDescriptorProperty() {
        return actionDescriptor;
    }
    
    public String getDescriptor() {
        return actionDescriptor.getValue();
    }
    
    private void updateDescriptor() {
        actionDescriptor.set(actionType.toString() + directObject.getIdentifier() + ":" + occurrence);
    }
    
    /**
     * Builds a status checkable protobuf from this action
     * @return a protobuf with the information from this object
     */
    public QuestProtobuf.StatusCheckableProtocol getStatusCheckableAsProtobuf() {
        QuestProtobuf.StatusCheckableProtocol.Builder statusBuilder = QuestProtobuf.StatusCheckableProtocol.newBuilder();
        QuestProtobuf.ActionProtocol.Builder actionBuilder = QuestProtobuf.ActionProtocol.newBuilder();
        actionBuilder.setType(QuestProtobuf.ActionType.valueOf(this.getActionType().toString()));
        actionBuilder.setTarget(directObject.getDirectObjectAsProtobuf());
        statusBuilder.setAction(actionBuilder.build());
        statusBuilder.setAmount(occurrence);
        ArrayList<QuestProtobuf.StatusCheckableProtocol> protocols = new ArrayList<>();
        QuestProtobuf.StatusCheckableProtocol protocol = statusBuilder.build();
        return protocol;
    }
    
    /*
     * Object Overrides 
     */
    
    @Override
    public int hashCode() {
        return actionDescriptor.get().hashCode();
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof Action) {
            Action oAct = (Action)other;
            if(oAct.getDirectObject() == null || directObject == null) {
                return oAct.getActionType() == this.getActionType() && oAct.getDirectObject() == directObject;
            }
            return oAct.getActionType() == this.getActionType() && oAct.getDirectObject().equals(directObject);
        }
        return false;
    }
    
}
