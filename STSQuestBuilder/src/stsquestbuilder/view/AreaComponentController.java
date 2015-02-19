package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import stsquestbuilder.model.Area;
import stsquestbuilder.protocolbuffers.QuestProtobuf.MapType;

/**
 * FXML Controller class
 *
 * @author William
 */
public class AreaComponentController implements Initializable {

    public static AreaComponentController openAreaComponentController(Area area) {
            
        Parent parent;
        FXMLLoader loader = new FXMLLoader(ConversationNodeController.class.getResource("/stsquestbuilder/view/AreaComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ConversationBuilderScreenController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        
        AreaComponentController controller = loader.getController();
        
        controller.setArea(area);
        controller.postSetupOp();
        
        return controller;
    }
    
    private Area area;
    
    @FXML
    private Pane backPane;
    
    @FXML
    private ChoiceBox<MapType> areaType;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private CheckBox generateCheckBox;
    
    @FXML
    private TextField rangeField;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        areaType.setItems(FXCollections.observableArrayList(MapType.values()));
    }
    
    public void postSetupOp() {
        
        //Wire Non-standard events
        areaType.valueProperty().addListener(event -> {
            area.setType(areaType.getValue());
        });
        
        nameField.textProperty().addListener(event -> {
            if(Area.uiIdExists(nameField.getText())) {
                nameField.setStyle("-fx-border-color:#F00");
            } else {
                nameField.setStyle("");
            }
            nameField.applyCss();

            area.setName(nameField.getText());
        });
        
        generateCheckBox.selectedProperty().addListener(event -> {
            area.setGenerateIfNeeded(generateCheckBox.selectedProperty().get());
        });
        
        rangeField.textProperty().addListener(event -> {
            area.setRange(Double.valueOf(rangeField.getText()));
        });
    }
    
    public Area getArea() {
        return area;
    }
    
    private void setArea(Area are) {
        area = are;
        
        areaType.setValue(area.getType());
        generateCheckBox.setSelected(area.isGenerateIfNeeded());
        rangeField.setText("" + area.getRange());
        nameField.setText(area.getName());
    }
    
    public Pane getRoot() {
        return backPane;
    }
}
