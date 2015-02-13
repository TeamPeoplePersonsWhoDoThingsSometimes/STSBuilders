package stsquestbuilder.model;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author William
 */
public class EnemyType {
    
    public static ArrayList<EnemyType> enemyTypes;
    
    public static EnemyType parse(String str) {
        for(EnemyType t : enemyTypes) {
            if(str.equals(t.getName())) {
                return t;
            }
        }
        return null;
    }
    
    private StringProperty name;
    private ObservableList<String> types;
    
    public EnemyType(String name, String[] typeArr) {
        this.name = new SimpleStringProperty(name);
        types = FXCollections.observableArrayList();
        for(String a : typeArr) {
            types.add(a);
        }
    }
    
    public StringProperty getNameProperty() {
        return name;
    }
    
    public String getName() {
        return name.get();
    }
    
    public ObservableList<String> getTypeStrings() {
        return types;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
