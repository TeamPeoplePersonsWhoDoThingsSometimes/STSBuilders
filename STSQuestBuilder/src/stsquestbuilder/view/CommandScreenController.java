package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;


import stsquestbuilder.model.SpawnCommand;
import stsquestbuilder.model.Area;
import stsquestbuilder.model.Item;
import stsquestbuilder.model.Enemy;
import stsquestbuilder.model.DirectObject;
import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.DirectObjectFactory;
import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 * FXML Controller class
 *
 * @author William
 */
public class CommandScreenController implements Initializable {

    public static CommandScreenController openCommandScreenControllerForCommand(SpawnCommand command) {
        Parent parent;
        CommandScreenController controller;
        FXMLLoader loader = new FXMLLoader(CommandScreenController.class.getResource("/stsquestbuilder/view/CommandScreen.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(CommandScreenController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setCommand(command);
        
        controller.postSetupOp();
        
        Scene scene = new Scene(parent, STSQuestBuilder.WINDOW_WIDTH, STSQuestBuilder.WINDOW_HEIGHT);
        
        Stage stage = new Stage();
        stage.setTitle("Spawn Command Builder");

        stage.setScene(scene);
        stage.show();
        
        return controller;
    }
    
    //Implementation Note: Spawn in dropdown will specify either a range within the
    //current area or another area altogether
    
    private static double SUB_PANEL_Y_OFFSET = 50.0;
    
    private SpawnCommand command;
    private Parent subRoot;
    private Parent root;
    
    @FXML
    private Pane backPane;
    
    @FXML
    private ChoiceBox<DirectObjectFactory.ObjectType> commandTypeDropdown;
    
    @FXML
    private CheckBox localSpawnCheck;
    
    /*@FXML
    private ChoiceBox<SpawnAreaTypeSpecification> spawnAreaTypeDropdown;
    
    @FXML
    private TextField rangeField;*/
            
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commandTypeDropdown.setItems(FXCollections.observableArrayList(DirectObjectFactory.ObjectType.getSpawnables()));
        commandTypeDropdown.setValue(DirectObjectFactory.ObjectType.AREA);
        /*spawnAreaTypeDropdown.setItems(FXCollections.observableArrayList(SpawnAreaTypeSpecification.values()));
        spawnAreaTypeDropdown.setValue(SpawnAreaTypeSpecification.LOCAL);*/
        
        commandTypeDropdown.valueProperty().addListener(event -> {
            switchCommandType(null);
        });
        
        localSpawnCheck.selectedProperty().addListener(event -> {
            setCommandLocalSpawn();
        });
    }    
    
    public void postSetupOp() {
        commandTypeDropdown.setValue(command.commandType());
        localSpawnCheck.setSelected(command.getSpecification().equals(QuestProtobuf.SpawnAreaTypeSpecification.LOCAL));
        //spawnAreaTypeDropdown.setValue(command.getSpecification());
        
        //rangeField.setText("" + command.getRange());
        
        /*rangeField.textProperty().addListener(event -> {
            command.setRange(Integer.valueOf(rangeField.getText()));
        });
        
        spawnAreaTypeDropdown.valueProperty().addListener(event -> {
            command.setSpecification(spawnAreaTypeDropdown.getValue());
        });*/
        
        switchCommandType(null);
    }
    
    /**
     * Updates the model with the info from the local spawn checkbox
     */
    private void setCommandLocalSpawn() {
        command.setSpecification((localSpawnCheck.isSelected() ? QuestProtobuf.SpawnAreaTypeSpecification.LOCAL: QuestProtobuf.SpawnAreaTypeSpecification.DISTANCE));
    }
    
    /**
     * Switch the command type being represented based on the commandTypeDropdown
     * @param event 
     */
    private void switchCommandType(MouseEvent event) {
        if(subRoot != null) {
            backPane.getChildren().remove(subRoot);
        }
        
        switch(commandTypeDropdown.getValue()) {
            /*case AREA:
                if (command.getAreaToSpawn() == null) {
                    command.setAreaToSpawn(new Area());
                }
                subRoot = AreaComponentController.openAreaComponentController(command.getAreaToSpawn()).getRoot();
                break;*/
            case ITEM:
                if (command.getItemToSpawn() == null) {
                    command.setItemToSpawn(new Item());
                }
                subRoot = ItemComponentController.openComponentForItem(command.getItemToSpawn()).getRoot();
                break;
            /*case NPC:
                
                break;*/
            case EMPTY:
            case ENEMY:
                if (command.getEnemyToSpawn() == null) {
                    command.setEnemyToSpawn(new Enemy());
                }
                subRoot = EnemyComponentController.openComponentForEnemy(command.getEnemyToSpawn()).getRoot();
                break;
        }
        
        backPane.getChildren().add(subRoot);
        subRoot.setLayoutY(SUB_PANEL_Y_OFFSET);
    }
    
    private void setCommand(SpawnCommand com) {
        this.command = com;
    }
    
    private void setRoot(Parent r) {
        root = r;
    }
    
    public Parent getRoot() {
        return root;
    }
}
