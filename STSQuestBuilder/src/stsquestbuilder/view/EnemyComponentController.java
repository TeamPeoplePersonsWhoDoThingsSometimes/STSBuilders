package stsquestbuilder.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import stsquestbuilder.model.EnemyType;
import stsquestbuilder.model.Enemy;

/**
 * FXML Controller class-
 * this component has a method to provide for easy access to the enemy defined
 * it also provides for simple plugin to a centralized enemy storage
 *
 * @author William
 */
public class EnemyComponentController implements Initializable {

    public static EnemyComponentController openComponentForEnemy(Enemy enemy) {
        Parent parent;
        EnemyComponentController controller;
        FXMLLoader loader = new FXMLLoader(EnemyComponentController.class.getResource("/stsquestbuilder/view/EnemyComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(EnemyComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setEnemy(enemy);
        controller.setRoot(parent);
        controller.postSetupOp();
        
        return controller;
    }
    
    private Enemy enemy;
    
    private Parent root;
    
    @FXML
    private ChoiceBox<EnemyType> enemyDropdown;
    
    @FXML
    private ChoiceBox<String> typeDropdown;
    
    @FXML
    private TextField quantityField;
    
    public Parent getRoot() {
        return root;
    }
    
    private void setRoot(Parent r) {
        root = r;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //TODO
    }
    
    /**
     * Post static init operations
     */
    public void postSetupOp() {
        enemyDropdown.setItems(FXCollections.observableArrayList(EnemyType.enemyTypes));
        enemyDropdown.setValue(enemy.getGeneralType());
        updateTypeDropdown();
        typeDropdown.setValue(enemy.getParticularType());
        quantityField.setText("" + enemy.getAmount());
        
        //Setup non-standard event handlers
        enemyDropdown.valueProperty().addListener(event -> {
            EnemyType newType = enemyDropdown.getValue();
            enemy.setEnemyType(newType);
            updateTypeDropdown();
        });
        
        typeDropdown.valueProperty().addListener(event -> {
            enemy.setParticularType(typeDropdown.getValue());
        });
        
        quantityField.textProperty().addListener(event -> {
            try {
                enemy.setAmount(Integer.valueOf(quantityField.getText()));
            } catch(NumberFormatException excep) {
                //TODO User error alert
            }
        });
    }
    
    public Enemy getEnemy() {
        return enemy;
    }
    
    private void setEnemy(Enemy obj) {
        enemy = obj;
    }
    
    /**
     * Set the type drowdown to match the set available to the selected enemy
     */
    private void updateTypeDropdown() {
        typeDropdown.setItems(FXCollections.observableArrayList(enemy.getGeneralType().getTypeStrings()));
    }
    
}
