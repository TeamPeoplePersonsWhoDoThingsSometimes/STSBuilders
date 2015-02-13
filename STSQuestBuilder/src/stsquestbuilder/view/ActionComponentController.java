package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ChoiceBox;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;

import stsquestbuilder.model.Action;
import stsquestbuilder.model.DirectObject;
import stsquestbuilder.model.Enemy;
import stsquestbuilder.model.Area;
import stsquestbuilder.model.DirectObjectFactory;
import stsquestbuilder.model.Item;
import stsquestbuilder.protocolbuffers.QuestProtobuf.ActionType;

/**
 *  
 * 
 * @author William
 */
public class ActionComponentController implements Initializable {
    
    public static ActionComponentController openComponentForAction(Action action) {
        Parent parent;
        ActionComponentController controller;
        FXMLLoader loader = new FXMLLoader(EnemyComponentController.class.getResource("/stsquestbuilder/view/ActionComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(EnemyComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setAction(action);
        
        return controller;
    }
    
    private static double subComponentYOffset = 40.0d;
    
    private Action action;
    
    @FXML
    private Pane backPane;
    
    @FXML
    private ChoiceBox<ActionType> typeSelector;
    
    private Parent subPanelRoot;
    private Parent root;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        typeSelector.setItems(FXCollections.observableArrayList(ActionType.values()));
        
        //register non-standard handlers
        typeSelector.valueProperty().addListener(event -> {
            switchToActionType(typeSelector.getValue());
        });
    }
    
    private void setRoot(Parent r) {
        root = r;
    }
    
    public Parent getRoot() {
        return root;
    }
    
    public Action getAction() {
        return action;
    }
    
    public void setAction(Action act) {
        action = act;
        
        if(act == null)
            return;
        
        ActionType type = action.getActionType();
        DirectObject DO = action.getDirectObject();
        Object sub = null;
        
        typeSelector.setValue(type);
        
        switch(type) {
            case KILL:
                sub = (Enemy)DO;
                break;
            case MOVE_AREA:
                sub = (Area)DO;
                break;
        }
        switchToActionType(type, DO);
    }
    
    public void destroySubcomponent() {
        backPane.getChildren().remove(subPanelRoot);
    }
    
    public void switchToActionType(ActionType type) {
        Object sub = null;
        
        sub = DirectObjectFactory.buildObjectByType(type);
        switchToActionType(type, sub);
    }
    
    public void switchToActionType(ActionType type, Object subObject) {
        destroySubcomponent();
        
        action.setActionType(type);
        
        DirectObjectFactory.ObjectType objType = DirectObjectFactory.getObjectTypeForActionType(type);
        
        if(objType.equals(DirectObjectFactory.ObjectType.ENEMY)) {
            EnemyComponentController controller = EnemyComponentController.openComponentForEnemy((Enemy)subObject);
            subPanelRoot = controller.getRoot();
            action.setDirectObject((Enemy)subObject);
        } else if(objType.equals(DirectObjectFactory.ObjectType.AREA)) {
            AreaComponentController controller = AreaComponentController.openAreaComponentController((Area)subObject);
            subPanelRoot = controller.getRoot();
            action.setDirectObject((Area)subObject);
        } else if(objType.equals(DirectObjectFactory.ObjectType.ITEM)) {
            ItemComponentController controller = ItemComponentController.openComponentForItem((Item) subObject);
            subPanelRoot = controller.getRoot();
            action.setDirectObject((Item)subObject);
        }
        
        if(subPanelRoot != null) {
            backPane.getChildren().add(subPanelRoot);
            subPanelRoot.setLayoutY(subComponentYOffset);
        }
    }
    
}
