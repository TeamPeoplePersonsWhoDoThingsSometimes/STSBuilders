package stsquestbuilder.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;

/**
 *
 * @author William
 */
public class ActionCheckable implements StatusCheckable {
    
    private StringProperty name;//used in ui
    private int amount;
    private Action action;
    private boolean empty;
    
    private void init(Action act) {
        name = new SimpleStringProperty();
        action = act;
        empty = true;
        amount = -1;
        bindName();
    }
    
    public ActionCheckable() {
        init(new Action());
    }
    
    public ActionCheckable(Action act) {
        init(act);
    }
    
    public ActionCheckable(StatusCheckableProtocol proto) {
        init(new Action(proto.getAction()));
    }
    
    public void setAction(Action act) {
        action = act;
        if (act != null) {
            bindName();
        } else {
            name.unbind();
            name.set("Empty");
        }
    }
    
    public Action getAction() {
        return action;
    }
    
    @Override
    public StringProperty getNameProperty() {
        return name;
    }
    
    @Override
    public void setNotEmpty() {
        empty = false;
    }
    
    @Override
    public String toString() {
        if (empty) 
            return "Empty Check";
        return "Action Check: " + action.getActionType();
    }

    @Override
    public StatusCheckableProtocol getStatusCheckableAsProtobuf() {
        StatusCheckableProtocol.Builder builder = StatusCheckableProtocol.newBuilder();
        builder.setAction(action.getActionAsProtobuf());
        
        if (amount > 0) {
            builder.setAmount(amount);
        }
        
        return builder.build();
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public int getAmount() {
        return amount;
    }
    
    /**
     * Bind the action checkable name property to the action properties
     */
    public void bindName() {
        name.bind(action.getDescriptorProperty());
    }

    @Override
    public boolean getEmpty() {
        return empty;
    }
}
