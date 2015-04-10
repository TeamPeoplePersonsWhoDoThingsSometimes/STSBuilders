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
import javafx.beans.property.StringProperty;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import javafx.scene.control.ChoiceBox;
import stsquestbuilder.STSQuestBuilder;

/**
 * FXML Controller class
 *
 * @author William
 */
public class QuestFinishedComponentController implements Initializable {

    public static QuestFinishedComponentController openComponentForQuest(StringProperty prop) {
        Parent parent;
        QuestFinishedComponentController controller;
        FXMLLoader loader = new FXMLLoader(QuestFinishedComponentController.class.getResource("/stsquestbuilder/view/QuestFinishedComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(QuestFinishedComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setQuestProperty(prop);
        
        return controller;
    }
    
    private StringProperty quest;
    
    private Parent root;
    
    @FXML
    private ChoiceBox<String> questDropdown;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        questDropdown.setItems(STSQuestBuilder.instance.getQuestNames());
    }
    
    public void setRoot(Parent r) {
        root = r;
    }
    
    public Parent getRoot() {
        return root;
    }
    
    public void setQuestProperty(StringProperty prop) {
        quest = prop;
        questDropdown.setValue(quest.get());
        rigQuest();
    }
    
    private void rigQuest() {
        questDropdown.valueProperty().addListener(event -> {
            quest.set(questDropdown.getValue());
        });
    }
    
}
