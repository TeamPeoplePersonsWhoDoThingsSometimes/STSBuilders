package stsquestbuilder.model;

import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Objects;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import stsquestbuilder.protocolbuffers.ConversationProtobuf;
import stsquestbuilder.protocolbuffers.QuestProtobuf;
import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusBlockProtocol;


/**
 *
 * @author William
 */
public class ConversationNode {
    
    public class Alternative {
        String text;
        private ConversationNode target;
        private long uid;//the target uid, needed during instantiation when target is not yet built
        ObservableList<ObservableList<StatusReference>> requirements;
        
        public Alternative(Long uid) {
            requirements = FXCollections.observableArrayList();
            this.uid = uid;
            text = uid.toString();
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String message) {
            text = message;
        }
        
        public ConversationNode getNode() {
            return target;
        }
        
        public long getUID() {
            return uid;
        }
        
        public void setTarget(ConversationNode node) {
            uid = node.uid;
            target = node;
        }
        
        public ObservableList<ObservableList<StatusReference>> getRequirements() {
            return requirements;
        }
        
        public void addRequirementBlock() {
            requirements.add(FXCollections.observableArrayList());
        }
        
        public void removeRequirementBlock(ObservableList<StatusReference> block) {
            requirements.remove(block);
        }
        
        @Override
        public boolean equals(Object other) {
            if (other instanceof Alternative) {
                Alternative alt = (Alternative)other;
                return alt.text.equals(text);
            } else if (other instanceof String) {
                String str = (String)other;
                return str.equals(text);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 53 * hash + Objects.hashCode(this.text);
            return hash;
        }
    }
    
    private static long currentUID = 0;
    private static HashMap<Long, ConversationNode> idMap = new HashMap<>();
    public static HashMap<String, ConversationNode> strIdMap = new HashMap<>();
    
    private static long getNextUID() {
        return currentUID++;
    }
    
    public static ConversationNode getNodeByID(long id) {
        return idMap.get(id);
    }
    
    public static ConversationNode getNodeByStringID(String id) {
        return strIdMap.get(id);
    }
    
    public static Set<String> getAllStringIDS() {
        return strIdMap.keySet();
    }
    
    private ObservableList<StatusBlock> blocks; 
    private HashMap<Long, Alternative> alternatives;
    private final long uid;
    private final StringProperty ID;//non-proto field, used for ui-sub for uid
    private final StringProperty text;
    private int X, Y;//for ui moveable node saving
    
    
    public ConversationNode() {
        text = new SimpleStringProperty();
        ID = new SimpleStringProperty();
        alternatives = new HashMap<>();
        uid = getNextUID();
        ID.set("" + uid);
        blocks = FXCollections.observableArrayList();
    }
    
    public ConversationNode(String message) {
        alternatives = new HashMap<>();
        ID = new SimpleStringProperty();
        text = new SimpleStringProperty();
        text.set(message);
        uid = getNextUID();
        ID.set("" + uid);
        blocks = FXCollections.observableArrayList();
    }
    
    public ConversationNode(ConversationProtobuf.ConversationNode proto) {
        text = new SimpleStringProperty();
        ID = new SimpleStringProperty();
        text.set(proto.getText());
        alternatives = new HashMap<>();
        blocks = FXCollections.observableArrayList();
        uid = proto.getUid();
        X = proto.getX();
        Y = proto.getY();
        
        for(ConversationProtobuf.Connection c : proto.getConnectionsList()) {
            Alternative alt = new Alternative(c.getNodeId());
            alt.text = c.getText();
            alternatives.put(alt.getUID(), alt);
            ObservableList<ObservableList<StatusReference>> requirementSets = alt.getRequirements();
            for (ConversationProtobuf.RequirementSet set : c.getRequirementSetsList()) {
                ObservableList<StatusReference> requirements = FXCollections.observableArrayList();
                for (QuestProtobuf.StatusCheckableProtocol status : set.getRequirementsList()) {
                    requirements.add(new StatusReference(StatusCheckableFactory.getStatusFromProtobuf(status)));
                }
                requirementSets.add(requirements);
            }
        }
        
        for(StatusBlockProtocol s : proto.getBlocksList()) {
            blocks.add(new StatusBlock(s));
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
    
    public StringProperty IDProperty() {
        return ID;
    }
    
    public String getID() {
        return ID.get();
    }
    
    /**
     * Checks to see if the id exists yet, and sets the id for this node if it does not
     * @param id id to set
     * @return true on successful set
     */
    public boolean setID(String id) {
        if (strIdMap.containsKey(id) && !ID.get().equals(id))
            return false;
        ID.set(id);
        return true;
    }
    
    public long getUID() {
        return uid;
    }
    
    public int getX() {
        return X;
    }
    
    public void setX(int newX) {
        X = newX;
    }
    
    public int getY() {
        return Y;
    }
    
    public void setY(int newY) {
        Y = newY;
    }
    
    public void newStatusBlock() {
        blocks.add(new StatusBlock("Block " + (blocks.size() + 1)));
    }
    
    public void newStatusBlock(String blockName) {
        blocks.add(new StatusBlock(blockName));
    }
    
    public void removeStatusBlock(StatusBlock block) {
        blocks.remove(block);
    }
    
    public ObservableList<StatusBlock> getBlocks() {
        return blocks;
    }
    
    public Alternative newAlternative(ConversationNode target) {
        Alternative alt = new Alternative(target.getUID());
        alt.setTarget(target);
        alternatives.put(alt.getUID(), alt);
        return alt;
    }
    
    /**
     * Add an alternative option to this node such that the string choice
     * maps to the Conversation node target
     * @param choice the option that selects this alternative
     * @param target the destination node
     */
    public void addAlternative(Alternative alt) {
        alternatives.put(alt.uid, alt);
    }
    
    /**
     * Gets all the nodes that may be reached by alternatives from this node
     * @return all of this node's alternative targets
     */
    public HashMap<Long, Alternative> getAlternatives() {
        return alternatives;
    }
    
    /**
     * Remove the alternative associated with the given choice
     * @param choice the choice associated with the alternative to remove
     */
    public void removeAlternative(long uid) {
        if(uid < 0) {
            alternatives.clear();
            return;
        }
        
        if(alternatives.containsKey(uid)) {
            alternatives.remove(uid);
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
            builder.setText("");
        for(Long i : alternatives.keySet()) {
            Alternative a = alternatives.get(i);
            ConversationProtobuf.Connection.Builder cBuilder = ConversationProtobuf.Connection.newBuilder();
            cBuilder.setText(a.text);
            cBuilder.setNodeId(a.target.uid);//break law of demeter due to encapsulated class
            
            for (ObservableList<StatusReference> block : a.getRequirements()) {
                ConversationProtobuf.RequirementSet.Builder sBuilder = ConversationProtobuf.RequirementSet.newBuilder();
                for (StatusReference ref : block) {
                    sBuilder.addRequirements(ref.getStatus().getStatusCheckableAsProtobuf());
                }
                cBuilder.addRequirementSets(sBuilder.build());
            }
            
            builder.addConnections(cBuilder.build());
        }
        
        builder.setUid(uid);
        builder.setX(X);
        builder.setY(Y);
        
        for (StatusBlock s : blocks) {
            builder.addBlocks(s.getStatusAsProtobuf());
        }
        
        return builder.build();
    }
    
}
