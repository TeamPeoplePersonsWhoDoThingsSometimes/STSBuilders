package stsquestbuilder.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import stsquestbuilder.protocolbuffers.ConversationProtobuf;

/**
 *
 * @author William
 */
public class Conversation {
    
    private ArrayList<ConversationNode> nodeList;
    private final IntegerProperty nodes;
    private final StringProperty name;
    private final StringProperty creator;
    
    public Conversation() {
        nodes = new SimpleIntegerProperty();
        name = new SimpleStringProperty();
        creator = new SimpleStringProperty();
        nodeList = new ArrayList<>();
    }
    
    public Conversation(String creatorName) {
        nodes = new SimpleIntegerProperty();
        name = new SimpleStringProperty();
        creator = new SimpleStringProperty();
        nodeList = new ArrayList<>();
        creator.set(creatorName);
        name.set("New Conversation");
        nodes.set(0);
    }
    
    public Conversation(ConversationProtobuf.Conversation proto) {
        nodes = new SimpleIntegerProperty();
        name = new SimpleStringProperty();
        creator = new SimpleStringProperty();
        nodeList = new ArrayList<>();
        if(proto.hasName())
            name.set(proto.getName());
        if(proto.hasCreator())
            creator.set(proto.getCreator());
        
        for(ConversationProtobuf.ConversationNode c : proto.getAllNodesList()) {
            addNode(new ConversationNode(c));
        }
    }
    
    public ArrayList<ConversationNode> getNodeList() {
        return nodeList;
    }
    
    /**
     * Add a conversation node to this conversation and connects it to nodes
     * based on uids if a connection doesn't already exist
     * @param node the node to add to the conversation
     */
    public void addNode(ConversationNode node) {
        for(ConversationNode c : nodeList) {
            for(Long uid : c.getAlternatives().keySet()) {
                ConversationNode.Alternative a = c.getAlternatives().get(uid);
                if(a.getUID() == node.getUID()) {
                    a.setTarget(node);
                }
            }
            
            for(Long uid : node.getAlternatives().keySet()) {
                ConversationNode.Alternative a = node.getAlternatives().get(uid);
                if(a.getUID() == c.getUID()) {
                    a.setTarget(c);
                }
            }
        }
        
        nodeList.add(node);
        nodes.set(nodes.get() + 1);
    }
    
    public void removeNode(ConversationNode node) {
        nodeList.remove(node);
        nodes.set(nodes.get() - 1);
    }
    
    public IntegerProperty getNodesProperty() {
        return nodes;
    }
    
    public StringProperty getNameProperty() {
        return name;
    }
    
    public StringProperty getCreatorProperty() {
        return creator;
    }
    
    public int getAmountOfNodes() {
        return nodes.get();
    }
    
    public String getName() {
        return name.get();
    }
    
    public String getCreator() {
        return creator.get();
    }
    
    public void setName(String n) {
        name.set(n);
    }
    
    /**
     * Creates and returns a protobuf with the information from this conversation
     * @return 
     */
    public ConversationProtobuf.Conversation getConversationAsProtobuf() {
        ConversationProtobuf.Conversation.Builder builder = ConversationProtobuf.Conversation.newBuilder();
        builder.setName(name.get());
        if(creator.get() != null)
            builder.setCreator(creator.get());
        
        for(ConversationNode n : nodeList) {
            builder.addAllNodes(n.getConversationNodeAsProtobuf());
        }
        
        return builder.build();
    }
}
