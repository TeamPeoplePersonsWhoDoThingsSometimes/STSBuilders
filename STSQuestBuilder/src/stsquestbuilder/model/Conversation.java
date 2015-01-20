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
    
    private ConversationNode root;
    private ArrayList<ConversationNode> nodeList;//sortof useless, but used in tracking newly added nodes...
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
        root = new ConversationNode(proto.getRoot());
        justifyNodeList();
    }
    
    /**
     * gets the root node of this conversation, if one has been set up yet
     */
    public ConversationNode getRoot() {
        return root;
    }
    
    /**
     * Set a new root of this conversation, used by the conversation builder to simplify
     * saving
     * @param newRoot the new root of this conversation
     */
    public void setRoot(ConversationNode newRoot) {
        root = newRoot;
        justifyNodeList();
    }
    
    /**
     * Add a conversation node to this conversation.  If no nodes are in the conversation,
     * then node is set as the conversation root as well
     * @param node the node to add to the conversation
     */
    public void addNode(ConversationNode node) {
        if(nodeList.size() == 0) {
            root = node;
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
        builder.setRoot(root.getConversationNodeAsProtobuf());
        return builder.build();
    }
    
    /**
     * Traverse all conversation nodes to add all nodes to the node list and get a
     * good node count
     */
    private void justifyNodeList() {
        //count the number of nodes in this conversation
        ArrayDeque<ConversationNode> bfsQueue = new ArrayDeque();
        ArrayList<ConversationNode> traversed = new ArrayList<>();
        ConversationNode curr;
        int count = 0;
        bfsQueue.add(root);
        while(!bfsQueue.isEmpty()) {
            curr = bfsQueue.removeFirst();
            count++;
            traversed.add(curr);
            bfsQueue.addAll(curr.getAlternatives().values());
        }
        nodes.set(count);
    }
}
