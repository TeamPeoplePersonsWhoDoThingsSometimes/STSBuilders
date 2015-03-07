/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


/**
 * FXML Controller class
 *
 * @author William
 */
public class LevelComponentController implements Initializable {

    public static LevelComponentController openComponentForAction(IntegerProperty prop) {
        Parent parent;
        LevelComponentController controller;
        FXMLLoader loader = new FXMLLoader(LevelComponentController.class.getResource("/stsquestbuilder/view/LevelComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(LevelComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setLevelProperty(prop);
        
        return controller;
    }
    
    private IntegerProperty level;
    
    private Parent root;
    
    @FXML
    private TextField levelField;
    
    @FXML
    private Label componentLabel;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        level = new SimpleIntegerProperty();
        rigLevel();
    }
    
    public void setRoot(Parent r) {
        root = r;
    }
    
    public Parent getRoot() {
        return root;
    }
    
    public void setLevelProperty(IntegerProperty prop) {
        level = prop;
        levelField.setText("" + level.get());
        rigLevel();
    }
    
    /**
     * This method allows for the tier component to be used for anyplace that needs to only track an integer value
     * i.e. levels
     * @param t text to set the label to
     */
    public void setLabelText(String t) {
        componentLabel.setText(t);
    }
    
    private void rigLevel() {
        levelField.textProperty().addListener(event -> {
            level.set(Integer.parseInt(levelField.getText()));
        });
    }
    
}
