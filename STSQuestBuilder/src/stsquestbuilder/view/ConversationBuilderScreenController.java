package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.shape.Line;
import javafx.scene.layout.Pane;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.Conversation;
import stsquestbuilder.model.ConversationNode;
import stsquestbuilder.model.StatusCheckable;
import stsquestbuilder.model.StatusReference;
import stsquestbuilder.model.StatusBlock;
import stsquestbuilder.model.SpawnCommand;
import stsquestbuilder.model.StatusCheckableFactory;

/**
 * FXML Controller class
 *
 * NOTE: as convention, "requirement" refers to statuses to be met in order to take an alternative,
 * whereas "status" refers to statuses to be met in order to execute a node command
 * 
 * @author William
 */
public class ConversationBuilderScreenController implements Initializable {
    
    public static double SCREEN_HEIGHT = 456;
    public static double SCREEN_WIDTH = 800;
    
    public static void openConversationBuilderScreenForConversation(Conversation convo, STSQuestBuilder app) {
        Parent parent;
        FXMLLoader loader = new FXMLLoader(ConversationBuilderScreenController.class.getResource("/stsquestbuilder/view/ConversationBuilderScreen.fxml"));
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
        Scene scene = new Scene(parent, SCREEN_WIDTH, SCREEN_HEIGHT);
        
        Stage convoStage = new Stage();
        convoStage.setTitle("STS Conversation Builder");
        convoStage.setScene(scene);
        convoStage.show();
        
        controller.postSetupOp();
    }
    
    private STSQuestBuilder app;
    
    private static double DEFAULT_SPACING = 200.0;//the default spacing between the top left of a node and the next one below it
    private static double HSPACING = 200.0;//the default spacing between the top left of a node and the next one next to it

    private ConversationNode.Alternative activeAlternative;
    private ConversationNodeController activeConversationNode;
    private ConversationNodeController targetConversationNode;
    private StatusBlock selectedStatusBlock;
    private SpawnCommand selectedCommand;
    private StatusReference selectedStatus;
    private ObservableList<StatusReference> selectedRequirementBlock;
    private StatusReference selectedRequirement;
    
    @FXML
    private Pane builderRoot;
    
    @FXML
    private ScrollPane scrollPanel;
    
    @FXML
    private Pane bottomBar;
    
    @FXML
    private TextField ConversationNameBox;
    
    @FXML
    private Pane nodePanel;
    
    @FXML
    private TextField nodeID;
    
    @FXML
    private Label idErrorMessage;
    
    @FXML
    private TextArea nodeMessage;
    
    @FXML
    private ListView<StatusBlock> nodeStatusBlocks;
    
    @FXML
    private ListView<StatusReference> nodeStatusBlock;
    
    @FXML
    private ListView<SpawnCommand> nodeCommands;
    
    @FXML
    private Pane alternativePanel;
    
    @FXML
    private TextArea alternativeText;
    
    @FXML
    private ListView<ObservableList<StatusReference>> alternativeOptions;
    
    @FXML
    private ListView<StatusReference> alternativeSet;
    
    @FXML
    private TextField priorityField;
    
    private Conversation conversation;
    
    private boolean isDraggingForConnection;
    private boolean isNodeEditorOpened;
    private boolean isAlternativeEditorOpened;
    private boolean setupData;//used in order to prevent editor listeners firing on data initialzation for the editor 

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isDraggingForConnection = false;
        isNodeEditorOpened = false;
        isAlternativeEditorOpened = false;
        setupData = false;
    }
    
    /**
     * Runs setup operations that need to occur internally after static
     * configuration
     */
    public void postSetupOp() {
        //traverse the conversation and to add all the conversation nodes to the builder
        ConversationNode curr;
        ArrayDeque<ConversationNode> bfsQueue = new ArrayDeque();
        ArrayList<ConversationNode> remaining = new ArrayList<>();
        HashMap<ConversationNode, ConversationNodeController> nodeRoots = new HashMap<>();//used to draw child nodes at offsets from parent
        
        //if we have an already initialized convo, then draw what we have,
        //otherwise, do some basic setup for the root node
        if(conversation.getAmountOfNodes() != 0) {
            remaining.addAll(conversation.getNodeList());
            
            while(!remaining.isEmpty()) {
                ConversationNodeController control = ConversationNodeController.openConversationNodeComponentForConversationNode(remaining.get(0), this);
                builderRoot.getChildren().add(control.getBase());
                Parent currRoot = control.getBase();
                nodeRoots.put(remaining.get(0), control);
                bfsQueue.push(remaining.get(0));
                remaining.remove(remaining.get(0));
                while(!bfsQueue.isEmpty()) {
                    curr = bfsQueue.removeFirst();
                    HashMap<Long, ConversationNode.Alternative> targets = curr.getAlternatives();
                    currRoot = nodeRoots.get(curr).getBase();
                    
                    //draw each of the targets here
                    for(Long uid : targets.keySet()) {
                        ConversationNode.Alternative a = targets.get(uid);
                        ConversationNode node = a.getNode();
                        ConversationNodeController nodeController;
                        if(remaining.contains(node)) {//need to draw the node
                            //build component
                            nodeController = ConversationNodeController.openConversationNodeComponentForConversationNode(node, this);
                            
                            //add component to screen
                            Parent root = (Parent)nodeController.getBase();
                            nodeRoots.put(node, nodeController);
                            builderRoot.getChildren().add(root);

                            //no longer remaining to be drawn
                            remaining.remove(a.getNode());
                            bfsQueue.addLast(node);
                        } else {//its already drawn
                            nodeController = nodeRoots.get(a.getNode());
                        }
                        
                        //draw line connections
                        nodeRoots.get(curr).addAlternative(nodeController, nodeController.drawConnection(nodeRoots.get(curr), a));
                    }

                }
            }
        } else {
            ConversationNode node = new ConversationNode();
            ConversationNodeController controller = ConversationNodeController.openConversationNodeComponentForConversationNode(node, this);
            conversation.addNode(node);
            Parent root = controller.getBase();
            builderRoot.getChildren().add(root);
            activeConversationNode = controller;
        }
        
        /*
        
        wire up model to changes
        
        */
        nodeID.textProperty().addListener(event -> {
            if(setupData){
                setupData = false; //do not act on setup changes
                return;
            }
            double height = idErrorMessage.getPrefHeight();
            if (!activeConversationNode.getConversationNode().setID(nodeID.getText())) {
                //display error message
                nodeID.setStyle("-fx-text-box-border: red ; -fx-focus-color: red ;");
            } else {
                nodeID.setStyle("");
            }
        });
        
        nodeMessage.textProperty().addListener(event -> {
            if(setupData) return;
            activeConversationNode.getConversationNode().setText(nodeMessage.getText());
        });
        
        nodeStatusBlocks.setCellFactory(list -> {
            //I don't know why, but the default text setup functionality is removed
            //when a factory is provided, so this is neccesary
            ListCell<StatusBlock> cell = new ListCell<StatusBlock>() {
                @Override
                protected void updateItem(StatusBlock item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        this.setText(item.toString());
                    }
                }
            };
            
            cell.setOnMouseClicked(event -> {
                selectedStatusBlock = cell.getItem();
                nodeStatusBlock.setItems(selectedStatusBlock.getStatuses());
                nodeCommands.setItems(selectedStatusBlock.getCommands());
            });
            return cell;
        });
        
        nodeStatusBlock.setOnMouseClicked(event -> {
            selectedStatus = nodeStatusBlock.getSelectionModel().getSelectedItem();
            if(event.getClickCount() >= 2) {
                StatusCheckableScreenController.openScreenForStatusCheck(selectedStatus);
            }
        });
        
        nodeCommands.setOnMouseClicked(event -> {
            selectedCommand = nodeCommands.getSelectionModel().getSelectedItem();
            if(event.getClickCount() >= 2) {
                CommandScreenController.openCommandScreenControllerForCommand(selectedCommand);
            }
        });
        
        
        /*
        
        wire model to changes
        
        */
        alternativeText.textProperty().addListener(event -> {
            activeAlternative.setText(alternativeText.getText());
        });
        
        priorityField.textProperty().addListener(event -> {
            try {
                activeAlternative.setPriority(Integer.parseInt(priorityField.getText()));
            } catch (NumberFormatException excep) {
                System.err.println("User cleared priority field, last valid field value will be used");
            }
        });
                
        alternativeOptions.setCellFactory(list -> {
            //Note- copied from display conversation method because I don't want to create an entirely new class for this...
            //I don't know why, but the default text setup functionality is removed
            //when a factory is provided, so this is neccesary
            ListCell<ObservableList<StatusReference>> cell = new ListCell<ObservableList<StatusReference>>() {
                @Override
                protected void updateItem(ObservableList<StatusReference> item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        this.setText("Set: " + list.getItems().indexOf(item));
                    }
                }
            };
            
            cell.setOnMouseClicked(event -> {
                selectedRequirementBlock = cell.getItem();
                alternativeSet.setItems(selectedRequirementBlock);
            });
            return cell;
        });
        
        alternativeSet.setCellFactory(list -> {
            //Note- copied from display conversation method because I don't want to create an entirely new class for this...
            //I don't know why, but the default text setup functionality is removed
            //when a factory is provided, so this is neccesary
            ListCell<StatusReference> cell = new ListCell<StatusReference>() {
                @Override
                protected void updateItem(StatusReference item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        this.setText(item.toString());
                    }
                }
            };
            
            cell.setOnMouseClicked(event -> {
                selectedRequirement = cell.getItem();
                if(event.getClickCount() >= 2) {
                    StatusCheckableScreenController.openScreenForStatusCheck(selectedRequirement);
                }
            });
            return cell;
        });
        
        ConversationNameBox.setText(conversation.getName());
    }
    
    /**
     * Since active alternatives are only used for the editors, automatically opens
     * an editor as well
     * @param alternative 
     */
    public void setActiveAlternative(ConversationNode.Alternative alternative) {
        activeAlternative = alternative;
        displayAlternativeEditor();
    }
    
    /**
     * Set the root conversation node controller
     * @param conversation the root conversation node controller
     */
    public void setCurrentConversationNode(ConversationNodeController conversation) {
        activeConversationNode = conversation;
    }
    
    public void setTargetConversationNode(ConversationNodeController node) {
        targetConversationNode = node;
    }
    
    /**
     * Sets the conversation represented by this screen
     * @param convo the conversation
     */
    public void setConversation(Conversation convo) {
        conversation = convo;
    }
    
    public ConversationNodeController getCurrentConversationNode() {
        return activeConversationNode;
    }
    
    public ConversationNodeController getTargetConversationNode() {
        return targetConversationNode;
    }
    
    /**
     * Place the given line and text field on the builder root, particularly catered
     * toward placing connections between conversation nodes
     * @param line the line to add
     * @param field the field to add
     */
    public void addConnectionLine(Line line, Line lf, Line lr) {
        builderRoot.getChildren().add(line);
        builderRoot.getChildren().add(lf);
        builderRoot.getChildren().add(lr);
    }
    
    /**
     * Remove the given line and text field from the builder root
     * @param line the line to remove
     * @param field the field to remove
     */
    public void removeConnectionLine(Line line, Line left, Line right) {
        builderRoot.getChildren().remove(line);
        builderRoot.getChildren().remove(left);
        builderRoot.getChildren().remove(right);
    }
    
    /**
     * Add a conversation node to the builder
     * @param event the event that triggered this handler
     */
    public void addConversationNode(MouseEvent event) {
        ConversationNodeController controller = ConversationNodeController.openConversationNodeComponentForConversationNode(new ConversationNode(), this);
        conversation.addNode(controller.getConversationNode());
        builderRoot.getChildren().add(controller.getBase());
        controller.setApp(this);
    }
    
    /**
     * Add a status block to the currently selected conversation node
     * @param event 
     */
    public void addStatusBlock(MouseEvent event) {
        if (activeConversationNode != null) {
             activeConversationNode.getConversationNode().newStatusBlock();
        }
    }
    
    public void removeStatusBlock(MouseEvent event) {
        if(activeConversationNode != null && selectedStatusBlock != null) {
            activeConversationNode.getConversationNode().removeStatusBlock(selectedStatusBlock);
            nodeStatusBlock.setItems(null);
            nodeCommands.setItems(null);
        }
    }
    
    /**
     * Add a status to the currently selected block
     * @param event 
     */
    public void addStatus(MouseEvent event) {
        if (selectedStatusBlock != null) {
            selectedStatusBlock.newStatus();
        }
    }
    
    public void addCommand(MouseEvent event) {
        if(selectedStatusBlock != null) {
            selectedStatusBlock.newCommand();
        }
    }
    
    public void removeCommand(MouseEvent event) {
        if(selectedStatusBlock != null && selectedCommand != null) {
            selectedStatusBlock.removeCommand(selectedCommand);
        }
    }
    
    public void removeStatus(MouseEvent event) {
        if(selectedStatusBlock != null && selectedStatus != null) {
            selectedStatusBlock.removeStatus(selectedStatus);
        }
    }
    
            
    public void addRequirementBlock(MouseEvent event) {
        if (activeAlternative != null) {
            activeAlternative.addRequirementBlock();
        }
    }
    
    public void removeRequirementBlock(MouseEvent event) {
        if (activeAlternative != null) {
            activeAlternative.removeRequirementBlock(selectedRequirementBlock);
        }
        
    }
    
    public void addRequirement(MouseEvent event) {
        selectedRequirementBlock.add(new StatusReference(StatusCheckableFactory.getEmptyStatus()));
    }
    
    public void removeRequirement(MouseEvent event) {
        selectedRequirementBlock.remove(selectedRequirement);
    }
    
    /**
     * Save the conversation
     * @param event the event that triggered this handler
     */
    public void saveConversation(MouseEvent event) {
        conversation.setName(ConversationNameBox.getText());
        app.save();
    }
    
    /**
     * Just clear selection colors
     */
    public void clearSelectionColors() {
        Set<Node> lines = builderRoot.lookupAll(".selected");
        for(Node n : lines) {
            Line line = (Line)n;
            if(line != null) {
                line.setStroke(Color.BLACK);
            }
        }
    }
    
    /**
     * When the user clicks in the builder pane, clear all current selections in the builder pane
     * @param event the event that triggered this handler
     */
    public void clearSelections(MouseEvent event) {
        if(event != null && !event.getTarget().equals(builderRoot))
            return;
        
        clearSelectionColors();
        
        closeConversationNodeEditor();
        closeAlternativeEditor();
    }
    
    public void setDraggingForConnection(boolean dragging) {
        isDraggingForConnection = dragging;
    }
    
    public boolean getDraggingForConnection() {
        return isDraggingForConnection;
    }
    
    /**
     * Opens the conversation node editor for the active conversation, also wires
     * events for the list views
     */
    public void displayConversationNodeEditor() {
        if (isAlternativeEditorOpened)
            closeAlternativeEditor();
        
        clearSelectionColors();//this method assumes a selected conversation node, so we shouldn't color alternatives
        
        ConversationNode node = activeConversationNode.getConversationNode();
        nodeID.setText(node.getID());
        nodeMessage.setText(node.getText());
        
        nodeStatusBlocks.setItems(node.getBlocks());
        nodeStatusBlock.setItems(null);
        nodeCommands.setItems(null);
        
        if (!isNodeEditorOpened) {
            double height = nodePanel.getHeight();
            scrollPanel.setMaxHeight(scrollPanel.getPrefHeight() - height - 3);
            bottomBar.setTranslateY(-height - 3);
            nodePanel.setVisible(true);
            isNodeEditorOpened = true;
        }
    }
    
    /**
     * Opens the alternative editor for the active alternative
     */
    public void displayAlternativeEditor() {
        if(isNodeEditorOpened)
            closeConversationNodeEditor();
        
        alternativeText.setText(activeAlternative.getText());
        alternativeOptions.setItems(activeAlternative.getRequirements());
        priorityField.setText("" + activeAlternative.getPriority());
        
        if(!isAlternativeEditorOpened) {
            double height = alternativePanel.getHeight();
            scrollPanel.setMaxHeight(scrollPanel.getPrefHeight() - height - 3);
            bottomBar.setTranslateY(-height - 3);
            alternativePanel.setVisible(true);
            isAlternativeEditorOpened = true;
        }
    }
    
    /**
     * Closes the conversation node editor
     */
    public void closeConversationNodeEditor() {
        if(isNodeEditorOpened) {
            double height = nodePanel.getHeight();
            scrollPanel.setMaxHeight(scrollPanel.getHeight() + height + 3);
            bottomBar.setTranslateY(0);
            nodePanel.setVisible(false);
            isNodeEditorOpened = false;
        }
        
        selectedStatusBlock = null;
        setupData = true;
    }
    
    /**
     * Closes the alternative editor
     */
    public void closeAlternativeEditor() {
        if(isAlternativeEditorOpened) {
            double height = nodePanel.getHeight();
            scrollPanel.setMaxHeight(scrollPanel.getHeight() + height + 3);
            bottomBar.setTranslateY(0);
            alternativePanel.setVisible(false);
            isAlternativeEditorOpened = false;
        }
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
