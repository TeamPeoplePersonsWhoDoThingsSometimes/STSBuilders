package stsquestbuilder.view;

import com.sun.prism.paint.Paint;
import java.net.URL;
import java.awt.Point;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.BorderStroke;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import stsquestbuilder.model.ConversationNode;
/**
 * FXML Controller class
 *
 * @author William
 */
public class ConversationNodeController implements Initializable {

    public static ConversationNodeController openConversationNodeComponentForConversationNode(ConversationNode node, ConversationBuilderScreenController root) {
        Parent parent;
        ConversationNodeController controller;
        FXMLLoader loader = new FXMLLoader(ConversationNodeController.class.getResource("/stsquestbuilder/view/ConversationNode.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConversationNodeController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.conversationText.setText(node.getText());
        controller.conversationText.textProperty().addListener(event -> {
            node.setText(controller.conversationText.getText());
        });
        controller.setApp(root);
        controller.node = node;
        
        controller.baseNode = parent;
        controller.postOpSetup();
        
        return controller;
    }
    
    public static double ARROW_HEAD_LENGTH = 5;
    public static double LINE_WIDTH = 7;
    
    private ConversationNode node;//model
    
    @FXML
    private TextArea conversationText;
    
    @FXML
    private AnchorPane backPane;//back of node, used for internal fxml
    
    private Parent baseNode;//base of fxml, used for external setup wiring
    
    private ConversationBuilderScreenController root;
    
    private HashMap<ConversationNodeController, Connection> alternatives;
    private ArrayList<Connection> connectionsToHere;
    
    //draging variables
    private boolean isBeingDragged;
    private double initialXOffset;
    private double initialYOffset;
    
    private class Connection {
        
        Connection(Line l, Line lf, Line lr, ConversationNode.Alternative alt) {
            line = l;
            alternative = alt;
            left = lf;
            right = lr;
        } 
        
        Line line;
        Line left;
        Line right;
        ConversationNode.Alternative alternative;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        alternatives = new HashMap<>();
        connectionsToHere = new ArrayList<>();
        isBeingDragged = false;
    }
    
    /**
     * Takes care of any instance-specific setup operations following
     * static setup- in particular, handles node positioning
     */
    private void postOpSetup() {
        //setup layout with backend model
        baseNode.setLayoutX(node.getX());
        baseNode.setLayoutY(node.getY());
        
        encompass();
        
        baseNode.layoutXProperty().addListener(event -> {
            node.setX((int)baseNode.getLayoutX());
        });
        baseNode.layoutYProperty().addListener(event -> {
            node.setY((int)baseNode.getLayoutY());
        });
    }
    
    /**
     * Checks to see if this controller has a connection to the given node
     * @param node the node to check for a connection to
     * @return true if a connection is present
     */
    private boolean hasConnectionToNode(ConversationNodeController node) {
        return alternatives.containsKey(node);
    }
    
    /**
     * Removes any existing connection and lines to the given node
     * @param node the node to delete connections to
     */
    private void removeConnection(ConversationNodeController node) {
        if(alternatives.containsKey(node)) {
            Connection connection = alternatives.get(node);
            root.removeConnectionLine(connection.line, connection.left, connection.right);
            alternatives.remove(node);
            this.node.removeAlternative(node.getConversationNode().getUID());
        }
    }
    
    /**
     * Add the alternative to the alternatives list, difference b/w this and setup is
     * that this does not modify the model, thus this may be used to pull informatio
     * from the model into the controller
     * 
     * @param target
     * @param connector 
     */
    public void addAlternative(ConversationNodeController target, Connection connector) {
        alternatives.put(target, connector);
    }
    
    /**
     * Sets up the target as an alternative for this conversation node if it is not already
     * connected
     * @param target the target to add
     */
    private ConversationNode.Alternative setupAlternative(ConversationNodeController target) {     
        return node.newAlternative(target.getConversationNode());
    }
    
    /**
     * Returns a point for the connection from the given point
     * 
     * Note: relies on preferred widths for calculation due to the neccesity of this
     * method to operate before the node is rendered in some cases
     * 
     * @param other the point to find a good connection for
     * @return a point for the connection from the given point
     */
    public Point getPointForConnection(Point other) {
        double x = backPane.getLayoutX();
        double y = backPane.getLayoutY();
        if(other.y < y) {
            x = x + backPane.getPrefWidth()/ 2;
        } else if(other.y > y + backPane.getPrefHeight()) {
            y = y + backPane.getPrefHeight();
            x = x + backPane.getPrefWidth() / 2;
        } else if(other.x < x) {
            y = y + backPane.getPrefHeight() / 2;
        } else {
            x = x + backPane.getPrefWidth();
            y = y + backPane.getPrefHeight() / 2;
        }
        
        return new Point((int)x,(int)y);
    }
    
    /**
     * Set the current app for this conversation controller
     * @param app the app to set for this conversation
     */
    public void setApp(ConversationBuilderScreenController app) {
        root = app;
    }
    
    /**
     * This handler is neccesary to trace a users click on this controller to cancel
     * any previous drags, this will prevent apparent jumpiness during attempted
     * connections
     * @param event the event that triggered this handler
     */
    public void cancelDragOnClick(MouseEvent event) {
        isBeingDragged = false;
    }
    
    /**
     * Handler the initial drag event on a conversation node to register the
     * node with the root
     * @param event the event that triggered this handler
     */
    public void handleConversationInitialDrag(MouseEvent event) {
        if(event.getClickCount() >= 2) {
            isBeingDragged = true;
            initialXOffset = event.getX();
            initialYOffset = event.getY();
        } else {
            root.setCurrentConversationNode(this);
            root.setDraggingForConnection(true);
            backPane.startFullDrag();
        }
    }
    
    /**
     * Display the editor on mouse up
     * @param event 
     */
    public void handleDisplayConversation(MouseEvent event) {
        root.setCurrentConversationNode(this);//should potentially have this somewhere else?
        root.displayConversationNodeEditor();
    }
    
    /**
     * Handle the user trying to drag the node in the builder
     * @param event the event that triggered this handler
     */
    public void handleConversationDrag(MouseEvent event) {
        if(isBeingDragged) {
            double x = baseNode.getLayoutX() + event.getX() - initialXOffset;
            double y = baseNode.getLayoutY() + event.getY() - initialYOffset;
            baseNode.setLayoutX(x);
            baseNode.setLayoutY(y);
            encompass();
            
            for(Connection c : connectionsToHere) {
                Point lineEnd = getPointForConnection(new Point((int)c.line.getStartX(), (int)c.line.getStartY()));
                c.line.setEndX(lineEnd.x);
                c.line.setEndY(lineEnd.y);
                
                //TODO last second changes! Copy copy copy, remove this later
                
                //rotate outer sections of the marks
                //transform
                Point adjustedEnd = new Point((int)c.line.getStartX() - (int)c.line.getEndX(), (int)c.line.getStartY() - (int)c.line.getEndY());

                //scale
                double dist = Math.sqrt(Math.pow(c.line.getStartX() - c.line.getEndX(), 2) + Math.pow(c.line.getStartY() - c.line.getEndY(), 2));
                double adjx = adjustedEnd.x/dist;
                double adjy = adjustedEnd.y/dist;

                //rotate
                double lefts = Math.sin(Math.PI/4);
                double leftc = Math.cos(Math.PI/4);
                double rights = Math.sin(-Math.PI/4);
                double rightc = Math.cos(-Math.PI/4);
                double leftEndx = leftc*adjx - lefts*adjy;
                double leftEndY = lefts*adjx + leftc*adjy;
                double rightEndx = rightc*adjx - rights*adjy;
                double rightEndy = rights*adjx + rightc*adjy;

                //scale
                leftEndx *= ARROW_HEAD_LENGTH;
                leftEndY *= ARROW_HEAD_LENGTH;
                rightEndx *= ARROW_HEAD_LENGTH;
                rightEndy *= ARROW_HEAD_LENGTH;

                //transform
                leftEndx += c.line.getEndX();
                leftEndY += c.line.getEndY();
                rightEndx += c.line.getEndX();
                rightEndy += c.line.getEndY();

                c.left.setEndX(leftEndx);
                c.left.setEndY(leftEndY);
                c.left.setStartX(lineEnd.x);
                c.left.setStartY(lineEnd.y);
                c.right.setEndX(rightEndx);
                c.right.setEndY(rightEndy);
                c.right.setStartX(lineEnd.x);
                c.right.setStartY(lineEnd.y);
            }
            
            for(Connection c : alternatives.values()) {
                Point lineStart = getPointForConnection(new Point((int)c.line.getEndX(), (int)c.line.getEndY()));
                c.line.setStartX(lineStart.x);
                c.line.setStartY(lineStart.y);
                
                //TODO last second changes! Copy copy copy, remove this later
                //rotate outer sections of the marks
                //transform
                Point adjustedEnd = new Point((int)c.line.getStartX() - (int)c.line.getEndX(), (int)c.line.getStartY() - (int)c.line.getEndY());

                //scale
                double dist = Math.sqrt(Math.pow(c.line.getStartX() - c.line.getEndX(), 2) + Math.pow(c.line.getStartY() - c.line.getEndY(), 2));
                double adjx = adjustedEnd.x/dist;
                double adjy = adjustedEnd.y/dist;

                //rotate
                double lefts = Math.sin(Math.PI/4);
                double leftc = Math.cos(Math.PI/4);
                double rights = Math.sin(-Math.PI/4);
                double rightc = Math.cos(-Math.PI/4);
                double leftEndx = leftc*adjx - lefts*adjy;
                double leftEndY = lefts*adjx + leftc*adjy;
                double rightEndx = rightc*adjx - rights*adjy;
                double rightEndy = rights*adjx + rightc*adjy;

                //scale
                leftEndx *= ARROW_HEAD_LENGTH;
                leftEndY *= ARROW_HEAD_LENGTH;
                rightEndx *= ARROW_HEAD_LENGTH;
                rightEndy *= ARROW_HEAD_LENGTH;

                //transform
                leftEndx += c.line.getEndX();
                leftEndY += c.line.getEndY();
                rightEndx += c.line.getEndX();
                rightEndy += c.line.getEndY();

                c.left.setEndX(leftEndx);
                c.left.setEndY(leftEndY);
                c.right.setEndX(rightEndx);
                c.right.setEndY(rightEndy);
            }
        }
    }
    
    /**
     * Handle a drag release on this conversation to connect this conversation
     * to the current active conversation of the app
     * @param event the event that triggered this handler
     */
    public void handleConversationConnectionDrag(MouseDragEvent event) {
        //don't connect a conversation to itself, instead, open an editor
        if(this.equals(root.getCurrentConversationNode())) {
            //root.displayConversationNodeEditor();
            return;
        }
        
        if(!root.getDraggingForConnection())
            return;


        ConversationNode.Alternative alt = root.getCurrentConversationNode().setupAlternative(this);
        Connection data = drawConnection(root.getCurrentConversationNode(), alt);
        root.getCurrentConversationNode().addAlternative(this, data);
        root.setDraggingForConnection(false);
    }
    
    /**
     * Draw a connecting line and text box from the given source to this node
     * @param source the node to connect from 
     * @return the text field on the connection to provide for external setup
     */
    public Connection drawConnection(ConversationNodeController source, ConversationNode.Alternative alternative) {
        //set up connecting line
        Line connection = new Line();
        Line leftMark = new Line();
        Line rightMark = new Line();
        connection.strokeWidthProperty().set(LINE_WIDTH);
        leftMark.strokeWidthProperty().set(LINE_WIDTH);
        rightMark.strokeWidthProperty().set(LINE_WIDTH);
        Point hookToThis = new Point((int)backPane.getLayoutX(), (int)backPane.getLayoutY());
        Point hook = source.getPointForConnection(hookToThis);
        hookToThis = getPointForConnection(hook);
        connection.setStartX(hook.x);
        connection.setStartY(hook.y);
        connection.setEndX(hookToThis.x);
        connection.setEndY(hookToThis.y);
        leftMark.setStartX(hookToThis.x);
        leftMark.setStartY(hookToThis.y);
        rightMark.setStartX(hookToThis.x);
        rightMark.setStartY(hookToThis.y);
        
        //rotate outer sections of the marks
        //transform
        Point adjustedEnd = new Point(hookToThis.x - hook.x, hookToThis.y - hook.y);

        //scale
        double dist = hook.distance(hookToThis);
        double adjx = adjustedEnd.x/dist;
        double adjy = adjustedEnd.y/dist;
        
        //rotate
        double lefts = Math.sin(5*Math.PI/4);
        double leftc = Math.cos(5*Math.PI/4);
        double rights = Math.sin(-5*Math.PI/4);
        double rightc = Math.cos(-5*Math.PI/4);
        double leftEndx = leftc*adjx - lefts*adjy;
        double leftEndY = lefts*adjx + leftc*adjy;
        double rightEndx = rightc*adjx - rights*adjy;
        double rightEndy = rights*adjx + rightc*adjy;
        
        //scale
        leftEndx *= ARROW_HEAD_LENGTH;
        leftEndY *= ARROW_HEAD_LENGTH;
        rightEndx *= ARROW_HEAD_LENGTH;
        rightEndy *= ARROW_HEAD_LENGTH;
        
        //transform
        leftEndx += hookToThis.x;
        leftEndY += hookToThis.y;
        rightEndx += hookToThis.x;
        rightEndy += hookToThis.y;
        
        leftMark.setEndX(leftEndx);
        leftMark.setEndY(leftEndY);
        rightMark.setEndX(rightEndx);
        rightMark.setEndY(rightEndy);
        
        //place text field on line
        double x = (hookToThis.x + hook.x) / 2;
        double y = (hookToThis.y + hook.y) / 2;
        
        //actually place the components in the application window and make the connection
        root.addConnectionLine(connection, leftMark, rightMark);
        Connection data = new Connection(connection, leftMark, rightMark, alternative);
        connectionsToHere.add(data);
        
        //TODO hook up line and text events
        connection.setOnMouseClicked(event -> {
            root.clearSelectionColors();
            root.setActiveAlternative(alternative);
            connection.getStyleClass().add("selected");
            leftMark.getStyleClass().add("selected");
            rightMark.getStyleClass().add("selected");
            connection.setStroke(Color.RED);
            leftMark.setStroke(Color.RED);
            rightMark.setStroke(Color.RED);
            connection.setFocusTraversable(true);
            connection.requestFocus();
            event.consume();
        });
        
        connection.setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.DELETE) && connection.isFocused()) {
                root.removeConnectionLine(connection, leftMark, rightMark);
                connectionsToHere.remove(data);
                source.removeConnection(this);
            }
        });
        
        return data;
    }
    
    public ConversationNode getConversationNode() {
        return node;
    }
    
    /**
     * Expand the root to encompass this node
     */
    public void encompass() {
        root.expand(baseNode.getLayoutX() + backPane.getPrefWidth(), baseNode.getLayoutY() + backPane.getPrefHeight());
    }
    
    /****************
     * Accessors (only one, its very lonely :(  )
     ****************/
    
    public Parent getBase() {
        return baseNode;
    }
}
