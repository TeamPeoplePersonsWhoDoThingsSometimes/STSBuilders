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
import javafx.scene.layout.Pane;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import stsquestbuilder.model.Item;
import stsquestbuilder.model.ItemType;

import stsquestbuilder.protocolbuffers.QuestProtobuf.LevelSpecification;

/**
 * FXML Controller class
 *
 * @author William
 */
public class ItemComponentController implements Initializable {

    public static ItemComponentController openComponentForItem(Item item) {
        Parent parent;
        ItemComponentController controller;
        FXMLLoader loader = new FXMLLoader(ItemComponentController.class.getResource("/stsquestbuilder/view/ItemComponent.fxml"));
        try {
            parent = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ItemComponentController.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        controller = loader.getController();
        
        controller.setRoot(parent);
        controller.setItem(item);
        controller.postSetupOp();
        
        return controller;
    }
    
    private Item item;
    private Parent root;
    
    private double amountOffset;
    
    @FXML
    private Pane weaponPane;
    
    @FXML
    private ChoiceBox<ItemType> typeDropdown;
    
    @FXML
    private TextField nameField;
    
    @FXML 
    private ChoiceBox<LevelSpecification> levelDropdown;
    
    @FXML
    private TextField levelField;
    
    @FXML
    private Label amountLabel;
    
    @FXML
    private TextField amountField;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        levelDropdown.setItems(FXCollections.observableArrayList(LevelSpecification.values()));
        typeDropdown.setItems(FXCollections.observableArrayList(ItemType.types));
        amountOffset = amountField.getLayoutY() - levelDropdown.getLayoutY();
    }
    
    /**
     * Handles all the initialization that needs to occur after initial setup
     */
    public void postSetupOp() {
        typeDropdown.setValue(item.getType());
        levelDropdown.setValue(item.getLevelSpec());
        nameField.setText(item.getName());
        levelField.setText("" + item.getVersion());
        amountField.setText("" + item.getAmount());
        
        //listen on dropdown change, but we also need initial setup
        setToCurrentGeneralItemType();
        
        typeDropdown.valueProperty().addListener(event -> {
            item.setType(typeDropdown.getValue());
            setToCurrentGeneralItemType();
        });
        
        levelDropdown.valueProperty().addListener(event -> {
            item.setLevelSpec(levelDropdown.getValue());
        });
        
        nameField.textProperty().addListener(event -> {
            item.setName(nameField.getText());
        });
        
        levelField.textProperty().addListener(event -> {
            if(!levelField.getText().equals("")) {
                item.setVersion(Integer.parseInt(levelField.getText()));
            }
        });
        
        amountField.textProperty().addListener(event -> {
            if(!levelField.getText().equals("")) {
                item.setAmount(Integer.parseInt(amountField.getText()));
            }
        });
    }

    /**
     * Set the pane to handle for the current General Item type
     */
    public void setToCurrentGeneralItemType() {
        if (item.getType().getGeneralType().equals(ItemType.GeneralType.HACK)) {
            setItemToHack();
        } else {
            setItemToWeapon();
        }
    }
    
    public void setItemToHack() {
        if (weaponPane.isVisible()) {
            weaponPane.setVisible(false);
            amountField.setLayoutY(amountField.getLayoutY() - amountOffset);
            amountLabel.setLayoutY(amountLabel.getLayoutY() - amountOffset);
        }
    }
    
    public void setItemToWeapon() {
        if (!weaponPane.isVisible()) {
            weaponPane.setVisible(true);
            amountField.setLayoutY(amountField.getLayoutY() + amountOffset);
            amountLabel.setLayoutY(amountLabel.getLayoutY() + amountOffset);
        }
    }
    
    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(Parent root) {
        this.root = root;
    }
    
}