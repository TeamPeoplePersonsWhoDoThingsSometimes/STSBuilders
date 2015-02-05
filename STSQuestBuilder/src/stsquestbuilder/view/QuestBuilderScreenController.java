package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.layout.Pane;
import javafx.scene.control.TitledPane;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

import stsquestbuilder.protocolbuffers.QuestProtobuf.ActionType;
import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.*;

/**
 * FXML Controller class
 *
 * @author William
 */
public class QuestBuilderScreenController implements Initializable {
    
    /**
     * Create a new quest screen for the given quest
     * @param quest quest to open a screen for
     */
    public static void openQuestBuilderScreenForQuest(Quest quest, STSQuestBuilder mainApp) {
        FXMLLoader loader = new FXMLLoader(QuestBuilderScreenController.class.getResource("/stsquestbuilder/view/QuestBuilderScreen.fxml"));
        Parent parent;
        try {
            parent = (Parent) loader.load();
        } catch(IOException excep) {
            System.err.println("Couldn't load Quest screen");
            excep.printStackTrace();
            return;
        }
        
        QuestBuilderScreenController controller = loader.getController();
        controller.setupScreenWithQuest(quest);
        controller.setApp(mainApp);
        
        StackPane root = new StackPane();
        
        root.getChildren().add(parent);
        
        Scene scene = new Scene(root, 800, 800);
        
        Stage questStage = new Stage();
        if(quest != null) {
            questStage.setTitle(quest.getName());
        } else {
            questStage.setTitle("New Quest");
        }
        questStage.setScene(scene);
        questStage.show();
        questStage.setOnShown(event -> controller.justifyAllStepNames());
        controller.setStage(questStage);
    }
    
    private static double ACTION_OFFSET_X = 300;
    private static double ACTION_OFFSET_Y = 10;
    
    @FXML
    private Accordion StepAccordion;
    
    @FXML
    private TextField NewQuestNameTextBox;
    
    @FXML
    private Button NewQuestNameSaveButton;
    
    @FXML
    private Button ChangeQuestNameButton;
    
    private ObservableList<ActionType> actionTypes;//an observable list version of model data
    private ObservableList<EnemyType> enemies;//an observable list version of the enemies stored in the model
    
    //a list holding the nodes of the titled pane for each step
    private ObservableList<TitledPane> steps;
    private HashMap<TitledPane, ActionComponentController> controllerMap;
    
    private Quest questForScreen;
    
    private Action currentAction;
    
    private STSQuestBuilder app;
    
    private Stage window;
    
    /**
     * Initializes the controller class with any viable generic information,
     * called by javaFX
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        controllerMap = new HashMap<>();
        populateModelLists();
        
        steps = StepAccordion.getPanes();
    }
    
    /**
     * Sets up the screen with information specific to the given quest
     * @param quest the quest to set the screen up with
     */
    public void setupScreenWithQuest(Quest quest) {
        questForScreen = quest;
        
        if(questForScreen.getLength() != 0) {
            for(Step s : questForScreen.getSteps()) {
                TitledPane stepPane = addStep(null);
                
                //populate the step pane
                ObservableList<Action> observableActions = FXCollections.observableArrayList();
                observableActions.addAll(s.getActions());

                getTableViewForTitledPane(stepPane).setItems(observableActions);
                getStepNameForTitledPane(stepPane).setText(s.getStepName());
                getStepDescriptionForTitledPane(stepPane).setText(s.getStepDescription());
                justifyStepName(stepPane);
            }
        } else {
            addStep(null);
        }
    }
    
    /**
     * Sets the backend app that this quest builder screen should interface with
     */
    public void setApp(STSQuestBuilder mainApp) {
        app = mainApp;
    }
    
    /**
     * Set a stage to associate with this controller
     * @param stage stage to associate
     */
    public void setStage(Stage stage) {
        window = stage;
    }
    
    /*
     Event Handlers
    */
    
    /**
     * Adds a step the the quest
     * @param event the event that triggered this handler
     * @return the titled pane containing the new step
     */
    public TitledPane addStep(MouseEvent event) {
        //load the fxml step component
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/stsquestbuilder/view/StepComponent.fxml"));
        Parent parent;
        try {
            parent = (Parent) loader.load();
        } catch(IOException excep) {
            System.err.println("Couldn't Step Component");
            excep.printStackTrace();
            return null;
        }

        //attach the component to a titled pane
        TitledPane stepPane = new TitledPane();
        stepPane.setContent(parent);
        
        Pane pane = (Pane) parent;
        
        ActionComponentController controller = ActionComponentController.openComponentForAction(currentAction);
        controllerMap.put(stepPane, controller);

        pane.getChildren().add(controller.getRoot());
        controller.getRoot().setLayoutX(ACTION_OFFSET_X);
        controller.getRoot().setLayoutY(ACTION_OFFSET_Y);
        
        setupStepPane(stepPane);

        //add the TitledPane to the step accordion
        StepAccordion.getPanes().add(stepPane);
        return stepPane;
    }
    
    /**
     * A wrapper to allow this method to plug into fxml components
     * @param event the event that triggered this handler
     */
    public void addStepFXML(MouseEvent event) {
        addStep(event);
    }
    
    /**
     * Remove the currently selected step
     * @param event the event that triggered this handler
     */
    public void removeStep(MouseEvent event) {
        TitledPane currentStep = StepAccordion.getExpandedPane();
        if(currentStep != null) {
            StepAccordion.getPanes().remove(currentStep);
            justifyAllStepNames();
        }
    }
    
    /**
     * Handler for the user clicking on the change quest name button
     * @param event the event that triggered this handler
     */
    public void changeQuestNameButtonClicked(MouseEvent event) {
        NewQuestNameTextBox.setText(questForScreen.getName());
        ChangeQuestNameButton.setVisible(false);
        NewQuestNameTextBox.setVisible(true);
        NewQuestNameSaveButton.setVisible(true);
    }
    
    /**
     * Handler for the user clicking on the save quest name button
     * @param event the event that triggered this handler
     */
    public void saveQuestNameButtonClicked(MouseEvent event) {
        String newTitle = NewQuestNameTextBox.getText();
        questForScreen.setName(newTitle);
        
        //if no stage is associated, then don't set it
        if(window != null) {
            window.setTitle(newTitle);
        }
        
        ChangeQuestNameButton.setVisible(true);
        NewQuestNameTextBox.setVisible(false);
        NewQuestNameSaveButton.setVisible(false);
    }
    
    /**
     * Handles the user pressing the new action button
     * @param event the event that triggered this action
     */
    public void newActionButtonPressed(MouseEvent event) {
        Node button = (Node)event.getSource();
        TableView actionTable = (TableView)button.parentProperty().getValue().lookup(".actionTable");

        currentAction = new Action();
        actionTable.getItems().add(currentAction);
    }
    
    /**
     * Save the action per the user's request
     * @param event event that triggered this handler
     */
    /*public void saveAction(MouseEvent event) {
        Node button = (Node)event.getSource();
        TableView actionTable = (TableView)button.parentProperty().getValue().lookup(".actionTable");
        
        //build an action from the action ui
        Parent pane = button.getParent();
        ActionType actionType = (ActionType)((ChoiceBox)pane.lookup(".actionTypeDropdown")).getValue();
        Pane killPane = (Pane)pane.lookup(".killPane");
        EnemyType enemy = (EnemyType)getEnemySelectionForKillPane(killPane).getValue();        
        String type = (String)getEnemyTypeForKillPane(killPane).getValue();        
        String amount = getEnemyAmountForKillPane(killPane).getText();
        Action actionFromUI = new Action(actionType, new DirectObject(enemy.getName(), type), Integer.parseInt(amount));
        
        currentAction.setAction(actionFromUI);
    }*/
    
    /**
     * Save the current steps
     * @param event the event that triggered this handler
     */
    public void saveSteps(MouseEvent event) {
        ArrayList<Step> questSteps = new ArrayList<>();
        for(TitledPane n : steps) {
            //read step texts
            String stepName = getStepNameForTitledPane(n).getText();
            String stepDescription = getStepDescriptionForTitledPane(n).getText();
            
            //get step actions
            TableView<Action> table = getTableViewForTitledPane(n);
            ArrayList<Action> actions = new ArrayList<>();
            actions.addAll(table.getItems());
            
            questSteps.add(new Step(stepName, stepDescription, actions));
        }
        
        questForScreen.setSteps(questSteps);
        app.save();
    }
    
    /*
     * General Utility
     */
    
    /**
     * Populated the observable lists needed for step panes from the model
     */
    private void populateModelLists() {
        //set up the action type list with the values from the ActionType enum
        //defined in the protocol buffer
        actionTypes = FXCollections.observableArrayList();
        
        for(ActionType actionType : ActionType.values()) {
            actionTypes.add(actionType);
        }
        
        enemies = FXCollections.observableArrayList();
        
        for(EnemyType a : EnemyType.enemyTypes) {
            enemies.add(a);
        }
    }
    
    private void justifyAllStepNames() {
        for(TitledPane pane : steps) {
            justifyStepName(pane);
        }
    }
    
    private void justifyStepName(TitledPane pane) {
        String name = getStepNameForTitledPane(pane).getText();
        int stepNum = steps.indexOf(pane) + 1;
        pane.setText(stepNum + ": " + name);
    }
    
    /**
     * Wires buttons and tables for the given step pane
     * @param pane 
     */
    private void setupStepPane(TitledPane pane) {
        //setup the action dropdown and table
        TableView<Action> table = getTableViewForTitledPane(pane);
        ((TableColumn<Action, String>)table.getColumns().get(0)).setCellValueFactory(cellData -> cellData.getValue().getDescriptorProperty());

        //load action component
        ActionComponentController controller = controllerMap.get(pane);
        
        //setup the row listeners by changing the factory callback
        //since the API gives us no other direct row access
        table.setRowFactory(tbl -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                //null-check load the selected action
                Action selectedAction = ((TableRow<Action>)event.getSource()).getItem();
                if(selectedAction == null) return;
                currentAction =  selectedAction;
                
                //load the information from the selected action into the ui
                controller.setAction(currentAction);
            });

            return row;
        });

        //setup the handlers for the action buttons and text boxesS
        //pane.getContent().lookup(".saveActionButton").setOnMouseClicked(event -> saveAction(event));            
        pane.getContent().lookup(".newActionButton").setOnMouseClicked(event -> newActionButtonPressed(event));            
        pane.getContent().lookup(".removeActionButton").setOnMouseClicked(event -> table.getItems().remove(currentAction));
        getStepNameForTitledPane(pane).setOnKeyPressed(event -> justifyStepName(pane));
    }
    
    /*
     * Utility Getters
     */
    
    public Pane getKillPane(TitledPane pane) {
        return (Pane)pane.getContent().lookup(".killPane");
    }
    
    public TableView<Action> getTableViewForTitledPane(TitledPane pane) {
        return (TableView<Action>)pane.getContent().lookup(".actionTable");
    }
    
    //public ChoiceBox getActionTypeForTitledPane(TitledPane pane) {
    //    return (ChoiceBox)pane.getContent().lookup(".actionTypeDropdown");
    //}
    
    public TextField getStepNameForTitledPane(TitledPane pane) {
        return (TextField)pane.getContent().lookup(".stepNameField");
    }
    
    public TextArea getStepDescriptionForTitledPane(TitledPane pane) {
        return (TextArea)pane.getContent().lookup(".stepDescriptionField");
    }
    
    /**public ChoiceBox getEnemySelectionForKillPane(Pane pane) {
        return (ChoiceBox)pane.lookup(".enemySelection");
    }
    
    public ChoiceBox getEnemyTypeForKillPane(Pane pane) {
        return (ChoiceBox)pane.lookup(".typeSelection");
    }    
    
    public TextField getEnemyAmountForKillPane(Pane pane) {
        return (TextField)pane.lookup(".quantity");
    }*/
}
