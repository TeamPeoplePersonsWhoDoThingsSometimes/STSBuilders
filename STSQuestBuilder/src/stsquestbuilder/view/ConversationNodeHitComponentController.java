package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;

import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.Conversation;
import stsquestbuilder.model.ConversationNode;

/**
 * FXML Controller class
 *
 * @author William
 */
public class ConversationNodeHitComponentController implements Initializable {

    public static ConversationNodeHitComponentController openCityBoundsComponentForProperty(ObjectProperty<ConversationNode> prop) {
        Parent parent;
        ConversationNodeHitComponentController controller;
        FXMLLoader loader = new FXMLLoader(ConversationNodeHitComponentController.class.getResource("/stsquestbuilder/view/ConversationNodeHitComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConversationNodeHitComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();

        controller.setRoot(parent);
        controller.setConversationProperty(prop);
        controller.postSetupOp();
        
        return controller;
    }
    
    private ObjectProperty<ConversationNode> conversationProperty;
    private Parent root;
    
    @FXML
    private ChoiceBox<Conversation> conversationDropdown;
    
    @FXML
    private ChoiceBox<ConversationNode> nodeDropdown;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        conversationDropdown.setItems(STSQuestBuilder.instance.getConversations());
        
        conversationDropdown.valueProperty().addListener(event -> {
            setNodeDropdown();
        });
        
        nodeDropdown.valueProperty().addListener(event -> {
            conversationProperty.set(nodeDropdown.getValue());
        });
    }
    
    public void postSetupOp() {
        if (conversationProperty != null) {
            conversationDropdown.setValue(conversationProperty.get().getConversation());
            nodeDropdown.setValue(conversationProperty.get());
        }
    }

    /**
     * Sets the node dropdown to contain the nodes associated with the selected
     * conversation
     */
    private void setNodeDropdown() {
        nodeDropdown.setItems(FXCollections.observableArrayList(conversationDropdown.getValue().getNodeList()));
    }
    
    public ObjectProperty<ConversationNode> getConversationProperty() {
        return conversationProperty;
    }

    public void setConversationProperty(ObjectProperty<ConversationNode> conversationProperty) {
        this.conversationProperty = conversationProperty;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }
    
}
