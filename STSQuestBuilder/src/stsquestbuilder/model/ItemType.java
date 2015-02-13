package stsquestbuilder.model;

import java.util.ArrayList;

/**
 * Item types as exported from the unity application
 * 
 * @author William
 */
public class ItemType {
    
    public enum GeneralType {
        HACK,
        WEAPON
    }
    
    public static ArrayList<ItemType> types = new ArrayList<>();
    
    public static ItemType parse(String typeName) {
        for(ItemType i : types) {
            if (i.name.equals(typeName)) {
                return i;
            }
        }
        return null;
    }
    
    private void init(String n, GeneralType t) {
        name = n;
        genType = t;
        types.add(this);
    }
    
    public ItemType() {
        init("", GeneralType.WEAPON);
    }
    
    public ItemType(String name, GeneralType type) {
        init(name, type);
    }
    
    private String name;
    private GeneralType genType;

    public String getName() {
        return name;
    }
    
    public GeneralType getGeneralType() {
        return genType;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
