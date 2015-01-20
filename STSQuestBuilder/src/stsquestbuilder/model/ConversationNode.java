package stsquestbuilder.model;

import java.util.HashMap;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import stsquestbuilder.protocolbuffers.ConversationProtobuf;

/**
 *
 * @author William
 */
public class ConversationNode {
    
    private HashMap<String, ConversationNode> alternatives;
    private final StringProperty text;
    
    public ConversationNode() {
        text = new SimpleStringProperty();
        alternatives = new HashMap<>();
    }
    
    public ConversationNode(String message) {
        alternatives = new HashMap<>();
        text = new SimpleStringProperty();
        text.set(message);
    }
    
    public ConversationNode(ConversationProtobuf.ConversationNode proto) {
        text = new SimpleStringProperty();
        text.set(proto.getText());
        alternatives = new HashMap<>();
        
        for(ConversationProtobuf.Connection c : proto.getConnectionsList()) {
            alternatives.put(c.getText(), new ConversationNode(c.getNode()));
        }
    }
    
    public StringProperty textProperty() {
        return text;
    }
    
    public String getText() {
        return text.get();
    }
    
    public void setText(String t) {
        text.set(t);
    }
    
    /**
     * Add an alternative option to this node such that the string choice
     * maps to the Conversation node target
     * @param choice the option that selects this alternative
     * @param target the destination node
     */
    public void addAlternative(String choice, ConversationNode target) {
        if(!alternatives.containsKey(choice)) {
            alternatives.put(choice, target);
        }
    }
    
    /**
     * Gets all the nodes that may be reached by alternatives from this node
     * @return all of this node's alternative targets
     */
    public HashMap<String, ConversationNode> getAlternatives() {
        return alternatives;
    }
    
    /**
     * Remove the alternative associated with the given choice
     * @param choice the choice associated with the alternative to remove
     */
    public void removeAlternative(String choice) {
        if(alternatives.containsKey(choice)) {
            alternatives.remove(choice);
        }
    }
    
    /**
     * Creates and returns a protobuf with the information from this conversation node
     * @return a protobuf with the information from this conversation node
     */
    public ConversationProtobuf.ConversationNode getConversationNodeAsProtobuf() {
        ConversationProtobuf.ConversationNode.Builder builder = ConversationProtobuf.ConversationNode.newBuilder();
        if(text.get() != null)
            builder.setText(text.get());
        else
            builder.setText("New Conversation");
        for(String s : alternatives.keySet()) {
            ConversationProtobuf.Connection.Builder cBuilder = ConversationProtobuf.Connection.newBuilder();
            cBuilder.setText(s);
            cBuilder.setNode(alternatives.get(s).getConversationNodeAsProtobuf());
            builder.addConnections(cBuilder.build());
        }
        
        return builder.build();
    }
    
}
