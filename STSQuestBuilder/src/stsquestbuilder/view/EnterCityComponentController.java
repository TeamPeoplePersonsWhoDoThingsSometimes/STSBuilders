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
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;

import stsquestbuilder.model.DirectObject;

/**
 * Direct object is used to model the city, not an area.
 * The reason for this is that the only information we need, knowing that
 * we have a city, is the size of the city and whether we are using a minimum
 * or a maximum.
 * 
 * Thus, a direct object provides
 * type- minimum, or maximum, as a string
 * id- size
 *
 * @author William
 */
public class EnterCityComponentController implements Initializable {

    public static EnterCityComponentController openCityBoundsComponentForObject(DirectObject obj) {
        Parent parent;
        EnterCityComponentController controller;
        FXMLLoader loader = new FXMLLoader(EnterCityComponentController.class.getResource("/stsquestbuilder/view/EnemyComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(EnterCityComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setCity(obj);
        controller.setRoot(parent);
        controller.postSetupOp();
        
        return controller;
    }
    
    private DirectObject city; 
    private Parent root;
    
    @FXML
    private ChoiceBox<String> sizeSpecificationBox;
            
    @FXML
    private TextField sizeField;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sizeSpecificationBox.setItems(FXCollections.observableArrayList("Minimum","Maximum"));
    }
    
    public void postSetupOp() {
        sizeField.textProperty().addListener(event -> {
            city.setIdentifier(sizeField.getText());
        });
        
        sizeSpecificationBox.valueProperty().addListener(event -> {
            city.setTypeId(sizeSpecificationBox.getValue());
        });
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }

    public DirectObject getCity() {
        return city;
    }

    public void setCity(DirectObject city) {
        this.city = city;
        
        if (city != null) {
            sizeField.setText(city.getIdentifier());
            sizeSpecificationBox.setValue(city.getTypeIdentifier());
        }
    }
    
    
}
