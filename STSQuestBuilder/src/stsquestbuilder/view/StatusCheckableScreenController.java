package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import stsquestbuilder.model.Action;
import stsquestbuilder.model.ActionCheckable;
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
        }
        
        backPane.getChildren().add(subPanelRoot);
        subPanelRoot.setLayoutY(subComponentYOffset);
    }
    
    private void setupWithStatus() {
        if(StatusCheckableFactory.getStatusTypeOfCheck(status.getStatus()).equals(StatusCheckableFactory.StatusType.ActionCheckable)) {
            checkTypeDropdown.setValue(StatusCheckableFactory.StatusType.ActionCheckable);
            changeToStatusType(StatusCheckableFactory.StatusType.ActionCheckable, ((ActionCheckable)status.getStatus()).getAction());
        }
    }
    
}
