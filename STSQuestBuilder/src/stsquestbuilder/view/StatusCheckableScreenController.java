package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import stsquestbuilder.model.Action;
import stsquestbuilder.model.ActionCheckable;
import stsquestbuilder.model.TierCheckable;
import stsquestbuilder.model.LevelCheckable;
import stsquestbuilder.model.QuestFinishedCheckable;
import stsquestbuilder.model.NumAreasCheckable;
import stsquestbuilder.model.StatusCheckable;
import stsquestbuilder.model.StatusReference;
import stsquestbuilder.model.StatusCheckableFactory;
import stsquestbuilder.STSQuestBuilder;

/**
 * FXML Controller class
 *
 * @author William
 */
public class StatusCheckableScreenController implements Initializable {
    
    public static StatusCheckableScreenController openScreenForStatusCheck(StatusReference reference) {
        Parent parent;
        StatusCheckableScreenController controller;
        FXMLLoader loader = new FXMLLoader(StatusCheckableScreenController.class.getResource("/stsquestbuilder/view/StatusCheckableScreen.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(StatusCheckableScreenController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setStatus(reference);
        
        controller.postSetupOp();
        
        Scene scene = new Scene(parent, STSQuestBuilder.WINDOW_WIDTH, STSQuestBuilder.WINDOW_HEIGHT);
        
        Stage stage = new Stage();
        stage.setTitle("Status Check Builder");

        stage.setScene(scene);
        stage.show();
        
        return controller;
    }
    
    private static double subComponentYOffset = 30.0d;
    
    private StatusReference status;
    private Parent subPanelRoot;
    private Parent root;
    
    @FXML
    private Pane backPane;
    
    @FXML
    private ChoiceBox<StatusCheckableFactory.StatusType> checkTypeDropdown;
    
    @FXML
    private CheckBox notCheckBox;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        checkTypeDropdown.setItems(FXCollections.observableArrayList(StatusCheckableFactory.StatusType.values()));
    }
    
    private void setRoot(Parent r) {
        root = r;
    }
    
    public Parent getRoot() {
        return root;
    }
    
    public void setStatus(StatusReference reference) {
        status = reference;
        status.getStatus().setNotEmpty();
        setupWithStatus();
    }
    
    public StatusReference getStatusReference() {
        return status;
    }
    
    public StatusCheckable getStatus() {
        return status.getStatus();
    }
    
    public void postSetupOp() {
        status.getStatus().setNotEmpty();
        
        //non-standard handlers
        checkTypeDropdown.valueProperty().addListener(event -> {
            changeToStatusType(checkTypeDropdown.getValue());
        });
        
        notCheckBox.selectedProperty().addListener(event -> {
            if (status.getStatus() != null) {
                status.getStatus().setNot(notCheckBox.isSelected());
            }
        });
    }
    
    public void clearSubComponents() {
        backPane.getChildren().remove(subPanelRoot);
    }
    
    public void changeToStatusType(StatusCheckableFactory.StatusType type) {
        Object o = null;
        switch(type) {
            case ActionCheckable:
                o = new Action();
                break;
            case LevelCheckable://cooincedantally the same as tier check
            case TierCheckable:
            case NumAreasCheckable:
                o = new SimpleIntegerProperty();
                break;
            case QuestFinishedCheckable:
                o = new SimpleStringProperty();
                break;
        }
        changeToStatusType(type, o);
    }
    
    public void changeToStatusType(StatusCheckableFactory.StatusType type, Object subObject) {
        clearSubComponents();
        
        if (type.equals(StatusCheckableFactory.StatusType.ActionCheckable)) {
            status.setStatus(StatusCheckableFactory.getActionStatus());
            ((ActionCheckable)status.getStatus()).setAction((Action)subObject);
            ActionComponentController controller = ActionComponentController.openComponentForAction((Action)subObject);
            subPanelRoot = controller.getRoot();
        } else if (type.equals(StatusCheckableFactory.StatusType.TierCheckable)) {
            status.setStatus(StatusCheckableFactory.getTierStatus());
            ((TierCheckable)status.getStatus()).setTier((IntegerProperty)subObject);
            TierComponentController controller = TierComponentController.openComponentForAction((IntegerProperty)subObject);
            subPanelRoot = controller.getRoot();
        } else if (type.equals(StatusCheckableFactory.StatusType.LevelCheckable)) {
            status.setStatus(StatusCheckableFactory.getLevelStatus());
            ((LevelCheckable)status.getStatus()).setLevel((IntegerProperty)subObject);
            LevelComponentController controller = LevelComponentController.openComponentForAction((IntegerProperty)subObject);
            subPanelRoot = controller.getRoot();
        } else if (type.equals(StatusCheckableFactory.StatusType.QuestFinishedCheckable)) {
            status.setStatus(StatusCheckableFactory.getQuestFinishedStatus());
            ((QuestFinishedCheckable)status.getStatus()).setQuestProperty((StringProperty)subObject);
            QuestFinishedComponentController controller = QuestFinishedComponentController.openComponentForQuest((StringProperty)subObject);
            subPanelRoot = controller.getRoot();
        } else if (type.equals(StatusCheckableFactory.StatusType.NumAreasCheckable)) {
            status.setStatus(StatusCheckableFactory.getNumAreasStatus());
            ((NumAreasCheckable)status.getStatus()).setAreas((IntegerProperty)subObject);
            TierComponentController controller = TierComponentController.openComponentForAction((IntegerProperty)subObject);
            controller.setLabelText("Areas: ");
            subPanelRoot = controller.getRoot();
        }
        
        status.getStatus().setNot(notCheckBox.isSelected());
        
        backPane.getChildren().add(subPanelRoot);
        subPanelRoot.setLayoutY(subComponentYOffset);
    }
    
    private void setupWithStatus() {
        notCheckBox.setSelected(status.getStatus().isNot());
        
        if(StatusCheckableFactory.getStatusTypeOfCheck(status.getStatus()).equals(StatusCheckableFactory.StatusType.ActionCheckable)) {
            checkTypeDropdown.setValue(StatusCheckableFactory.StatusType.ActionCheckable);
            changeToStatusType(StatusCheckableFactory.StatusType.ActionCheckable, ((ActionCheckable)status.getStatus()).getAction());
        } else if (StatusCheckableFactory.getStatusTypeOfCheck(status.getStatus()).equals(StatusCheckableFactory.StatusType.TierCheckable)) {
            checkTypeDropdown.setValue(StatusCheckableFactory.StatusType.TierCheckable);
            changeToStatusType(StatusCheckableFactory.StatusType.TierCheckable, ((TierCheckable)status.getStatus()).getTierProperty());
        } else if (StatusCheckableFactory.getStatusTypeOfCheck(status.getStatus()).equals(StatusCheckableFactory.StatusType.LevelCheckable)) {
            checkTypeDropdown.setValue(StatusCheckableFactory.StatusType.LevelCheckable);
            changeToStatusType(StatusCheckableFactory.StatusType.LevelCheckable, ((LevelCheckable)status.getStatus()).getLevelProperty());
        } else if (StatusCheckableFactory.getStatusTypeOfCheck(status.getStatus()).equals(StatusCheckableFactory.StatusType.QuestFinishedCheckable)) {
            checkTypeDropdown.setValue(StatusCheckableFactory.StatusType.QuestFinishedCheckable);
            changeToStatusType(StatusCheckableFactory.StatusType.QuestFinishedCheckable, ((QuestFinishedCheckable)status.getStatus()).getQuestProperty());
        } else if (StatusCheckableFactory.getStatusTypeOfCheck(status.getStatus()).equals(StatusCheckableFactory.StatusType.NumAreasCheckable)) {
            checkTypeDropdown.setValue(StatusCheckableFactory.StatusType.NumAreasCheckable);
            changeToStatusType(StatusCheckableFactory.StatusType.NumAreasCheckable, ((NumAreasCheckable)status.getStatus()).getAreasProperty());
        }
    }
    
}
