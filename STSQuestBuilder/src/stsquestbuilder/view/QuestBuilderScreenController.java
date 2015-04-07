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
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.StackPane;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListCell;


import stsquestbuilder.protocolbuffers.QuestProtobuf.ActionType;
import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.*;
import stsquestbuilder.protocolbuffers.QuestProtobuf;

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
        
        Scene scene = new Scene(root, STSQuestBuilder.WINDOW_WIDTH, STSQuestBuilder.WINDOW_HEIGHT);
        
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
        
        controller.postSetupOp();
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
    
    @FXML
    private ChoiceBox<QuestProtobuf.Biome> BiomeChoiceBox;
    
    private ObservableList<ActionType> actionTypes;//an observable list version of model data
    private ObservableList<EnemyType> enemies;//an observable list version of the enemies stored in the model
    
    //a list holding the nodes of the titled pane for each step
    private ObservableList<TitledPane> steps;
    private HashMap<TitledPane, ActionComponentController> controllerMap;
    
    private Quest questForScreen;
    
    private StatusReference currentReference;
    
    private SpawnCommand currentCommand;
    
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
        
        BiomeChoiceBox.setValue(quest.getBiome());
        
        quest.getBiomeProperty().bind(BiomeChoiceBox.valueProperty());
        
        if(questForScreen.getLength() != 0) {
            for(Step s : questForScreen.getSteps()) {
                TitledPane stepPane = addStep(null);
                
                //populate the step pane
                ObservableList<StatusReference> observableActions = FXCollections.observableArrayList();
                observableActions.addAll(s.getActions());
                
                ObservableList<SpawnCommand> commands = FXCollections.observableArrayList();
                commands.addAll(s.getCommands());

                getTableViewForTitledPane(stepPane).setItems(observableActions);
                getListViewForTitledPane(stepPane).setItems(commands);
                getStepNameForTitledPane(stepPane).setText(s.getStepName());
                getStepDescriptionForTitledPane(stepPane).setText(s.getStepDescription());
                justifyStepName(stepPane);
            }
        } else {
            addStep(null);
        }
    }
    
    public void postSetupOp() {
        if (BiomeChoiceBox.getValue() == null)
            BiomeChoiceBox.setValue(QuestProtobuf.Biome.PYTHON);
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

        currentReference = new StatusReference(StatusCheckableFactory.getEmptyStatus());
        actionTable.getItems().add(currentReference);
    }
    
    /**
     * Handles the user pressing a new command button
     * @param event the event that triggered this action
     */
    public void newCommandButtonPressed(MouseEvent event) {
        Node button = (Node)event.getSource();
        ListView commandList = (ListView)button.parentProperty().getValue().lookup(".commandList");

        currentCommand = new SpawnCommand();
        commandList.getItems().add(currentCommand);
    }
    
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
            TableView<StatusReference> table = getTableViewForTitledPane(n);
            ListView<SpawnCommand> commandList = getListViewForTitledPane(n);
            
            questSteps.add(new Step(stepName, stepDescription, table.getItems(), commandList.getItems()));
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
        ObservableList<QuestProtobuf.Biome> biomes = FXCollections.observableArrayList();
        
        for (QuestProtobuf.Biome b : QuestProtobuf.Biome.values()) {
            biomes.add(b);
        }
        
        BiomeChoiceBox.setItems(biomes);
        
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
        TableView<StatusReference> table = getTableViewForTitledPane(pane);
        ((TableColumn<StatusReference, String>)table.getColumns().get(0)).setCellValueFactory(cellData -> StatusCheckableFactory.getStatusTypeOfCheck(cellData.getValue().getStatus()).getNameProperty());
        ((TableColumn<StatusReference, String>)table.getColumns().get(1)).setCellValueFactory(cellData -> cellData.getValue().getStatus().getNameProperty());
        
        ListView<SpawnCommand> list = getListViewForTitledPane(pane);
        
        //setup the row listeners by changing the factory callback
        //since the API gives us no other direct row access
        table.setRowFactory(tbl -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                //null-check load the selected action
                StatusReference reference = ((TableRow<StatusReference>)event.getSource()).getItem();
                if(reference == null) return;
                currentReference =  reference;
                
                if (event.getClickCount() >= 2) {
                    StatusCheckableScreenController controller = StatusCheckableScreenController.openScreenForStatusCheck(currentReference);
                }
            });

            return row;
        });
        
        list.setCellFactory(l -> {
            //I don't know why, but the default text setup functionality is removed
            //when a factory is provided, so this is neccesary
            ListCell<SpawnCommand> cell = new ListCell<SpawnCommand>() {
                @Override
                protected void updateItem(SpawnCommand item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item != null) {
                        this.setText(item.toString());
                    }
                }
            };
            
            cell.setOnMouseClicked(event -> {
                currentCommand = cell.getItem();
                if (event.getClickCount() >= 2) {
                    CommandScreenController.openCommandScreenControllerForCommand(currentCommand);
                }
            });
            return cell;
        });

        //setup the handlers for the action buttons and text boxesS
        //pane.getContent().lookup(".saveActionButton").setOnMouseClicked(event -> saveAction(event));            
        pane.getContent().lookup(".newActionButton").setOnMouseClicked(event -> newActionButtonPressed(event));            
        pane.getContent().lookup(".removeActionButton").setOnMouseClicked(event -> table.getItems().remove(currentReference));
        pane.getContent().lookup(".addCommand").setOnMouseClicked(event -> newCommandButtonPressed(event)); 
        pane.getContent().lookup(".removeCommand").setOnMouseClicked(event -> list.getItems().remove(currentCommand));       

        
        getStepNameForTitledPane(pane).setOnKeyPressed(event -> justifyStepName(pane));
    }
    
    /*
     * Utility Getters
     */
    
    public Pane getKillPane(TitledPane pane) {
        return (Pane)pane.getContent().lookup(".killPane");
    }
    
    public TableView<StatusReference> getTableViewForTitledPane(TitledPane pane) {
        return (TableView<StatusReference>)pane.getContent().lookup(".actionTable");
    }
    
    public ListView<SpawnCommand> getListViewForTitledPane(TitledPane pane) {
        return (ListView<SpawnCommand>)pane.getContent().lookup(".commandList");
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
