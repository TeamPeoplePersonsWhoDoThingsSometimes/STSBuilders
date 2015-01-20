package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.Conversation;
import stsquestbuilder.model.ConversationNode;

/**
 * FXML Controller class
 *
 * @author William
 */
public class ConversationBuilderScreenController implements Initializable {
    
    public static void openConversationBuilderScreenForConversation(Conversation convo, STSQuestBuilder app) {
        Parent parent;
        FXMLLoader loader = new FXMLLoader(ConversationNodeController.class.getResource("/stsquestbuilder/view/ConversationBuilderScreen.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConversationBuilderScreenController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        ConversationBuilderScreenController controller = loader.getController();
        controller.setConversation(convo);
        controller.setApp(app);
        
        //actually create the new window
        Scene scene = new Scene(parent, 800, 800);
        
        Stage convoStage = new Stage();
        convoStage.setTitle("STS Conversation Builder");
        convoStage.setScene(scene);
        convoStage.show();
        
        controller.postSetupOp();
    }
    
    private STSQuestBuilder app;
    
    private static double DEFAULT_SPACING = 200.0;//the default spacing between the top left of a node and the next one below it
    private static double HSPACING = 200.0;//the default spacing between the top left of a node and the next one next to it

    
    private ConversationNodeController activeConversation;
    
    @FXML
    private Pane builderRoot;
    
    @FXML
    private TextField ConversationNameBox;
    
    private Conversation conversation;
    
    private boolean isDraggingForConnection;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isDraggingForConnection = false;
    }
    
    /**
     * Runs setup operations that need to occur internally after static
     * configuration
     */
    public void postSetupOp() {
        //traverse the conversation and to add all the conversation nodes to the builder
        ConversationNode curr;
        ArrayDeque<ConversationNode> bfsQueue = new ArrayDeque();
        ArrayList<ConversationNode> traversed = new ArrayList<>();
        HashMap<ConversationNode, ConversationNodeController> nodeRoots = new HashMap<>();//used to draw child nodes at offsets from parent
        
        //if we have an already initialized convo, then draw what we have,
        //otherwise, do some basic setup for the root node
        if(conversation.getRoot() != null) {
            ConversationNodeController control = ConversationNodeController.openConversationNodeComponentForConversationNode(conversation.getRoot(), this);
            activeConversation = control;
            builderRoot.getChildren().add(control.getBase());
            Parent currRoot = control.getBase();
            nodeRoots.put(conversation.getRoot(), control);
            bfsQueue.push(conversation.getRoot());
            while(!bfsQueue.isEmpty()) {
                curr = bfsQueue.removeFirst();
                HashMap<String, ConversationNode> targets = curr.getAlternatives();
                currRoot = nodeRoots.get(curr).getBase();
                //compute root offset
                double x, y;
                if(currRoot == null) {
                    x = builderRoot.getLayoutX();
                    y = builderRoot.getLayoutY();
                } else {
                    x = currRoot.getLayoutX();
                    y = currRoot.getLayoutY() + DEFAULT_SPACING;
                }

                double xOffset = 0.0d;
                //draw each of the targets here
                for(String s : targets.keySet()) {
                    ConversationNode node = targets.get(s);
                    ConversationNodeController nodeController = ConversationNodeController.openConversationNodeComponentForConversationNode(node, this);
                    Parent root = (Parent)nodeController.getBase();
                    nodeRoots.put(node, nodeController);
                    root.setLayoutX(x + xOffset);
                    root.setLayoutY(y);
                    builderRoot.getChildren().add(root);
                    nodeController.drawConnection(nodeRoots.get(curr)).setText(s);
                    bfsQueue.addLast(node);
                    xOffset += HSPACING;
                }

                traversed.add(curr);
            }
        } else {
            ConversationNode node = new ConversationNode();
            ConversationNodeController controller = ConversationNodeController.openConversationNodeComponentForConversationNode(node, this);
            conversation.addNode(node);
            Parent root = controller.getBase();
            builderRoot.getChildren().add(root);
            activeConversation = controller;
        }
        
        ConversationNameBox.setText(conversation.getName());
    }
    
    /**
     * Set the root conversation node controller
     * @param conversation the root conversation node controller
     */
    public void setCurrentConversation(ConversationNodeController conversation) {
        activeConversation = conversation;
    }
    
    /**
     * Sets the conversation represented by this screen
     * @param convo the conversation
     */
    public void setConversation(Conversation convo) {
        conversation = convo;
    }
    
    public ConversationNodeController getCurrentConversation() {
        return activeConversation;
    }
    
    /**
     * Place the given line and text field on the builder root, particularly catered
     * toward placing connections between conversation nodes
     * @param line the line to add
     * @param field the field to add
     */
    public void addConnectionLine(Line line, Line lf, Line lr, TextField field) {
        builderRoot.getChildren().add(line);
        builderRoot.getChildren().add(lf);
        builderRoot.getChildren().add(lr);
        builderRoot.getChildren().add(field);
    }
    
    /**
     * Remove the given line and text field from the builder root
     * @param line the line to remove
     * @param field the field to remove
     */
    public void removeConnectionLine(Line line, TextField field) {
        builderRoot.getChildren().remove(line);
        builderRoot.getChildren().remove(field);
    }
    
    /**
     * Add a conversation node to the builder
     * @param event the event that triggered this handler
     */
    public void addConversationNode(MouseEvent event) {
        ConversationNodeController controller = ConversationNodeController.openConversationNodeComponentForConversationNode(new ConversationNode(), this);
        builderRoot.getChildren().add(controller.getBase());
        controller.setApp(this);

    }
    
    /**
     * Save the conversation
     * @param event the event that triggered this handler
     */
    public void saveConversation(MouseEvent event) {
        ConversationNode newRoot = activeConversation.getConversationNode();
        conversation.setName(ConversationNameBox.getText());
        conversation.setRoot(newRoot);
        app.save();
    }
    
    /**
     * When the user clicks in the builder pane, clear all current selections in the builder pane
     * @param event the event that triggered this handler
     */
    public void clearSelections(MouseEvent event) {
        Line line = (Line)builderRoot.lookup(".selected");
        if(line != null) {
            line.setStroke(Color.BLACK);
        }
    }
    
    public void setDraggingForConnection(boolean dragging) {
        isDraggingForConnection = dragging;
    }
    
    public boolean getDraggingForConnection() {
        return isDraggingForConnection;
    }
    
    /**
     * Expand the builder anchor to encompass the given point
     * @param x the x coordinate 
     * @param y the y coordinate
     */
    public void expand(double x, double y) {
        if(builderRoot.prefWidthProperty().get() < x)
            builderRoot.prefWidthProperty().set(x);
        if(builderRoot.prefHeightProperty().get() < y)
            builderRoot.prefHeightProperty().set(y);
    }
    
    public void setApp(STSQuestBuilder app) {
        this.app = app;
    }
}
