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
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;


/**
 * FXML Controller class
 *
 * @author William
 */
public class TierComponentController implements Initializable {

    public static TierComponentController openComponentForAction(IntegerProperty prop) {
        Parent parent;
        TierComponentController controller;
        FXMLLoader loader = new FXMLLoader(TierComponentController.class.getResource("/stsquestbuilder/view/TierComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(TierComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setTierProperty(prop);
        
        return controller;
    }
    
    private IntegerProperty tier;
    
    private Parent root;
    
    @FXML
    private TextField tierField;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        tier = new SimpleIntegerProperty();
        rigTier();
    }
    
    public void setRoot(Parent r) {
        root = r;
    }
    
    public Parent getRoot() {
        return root;
    }
    
    public void setTierProperty(IntegerProperty prop) {
        tier = prop;
        tierField.setText("" + tier.get());
        rigTier();
    }
    
    private void rigTier() {
        tierField.textProperty().addListener(event -> {
            tier.set(Integer.parseInt(tierField.getText()));
        });
    }
    
}
