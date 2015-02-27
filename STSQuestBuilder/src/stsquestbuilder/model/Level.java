package stsquestbuilder.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import stsquestbuilder.protocolbuffers.QuestProtobuf.DirectObjectProtocol;

/**
 * This class is used to plug levels into the action system as a direct object
 * @author William
 */
public class Level extends DirectObject{
    
    private IntegerProperty level;
    
    private void init(int l) {
        level = new SimpleIntegerProperty();
        level.set(l);
        
        level.addListener(event -> {
            super.setTypeId("" + level.get());
        });
        
    }
    
    public Level() {
        super("Level Up", "" + 1);
        init(1);
    }
    
    public Level(int l) {
        super("Level Up", "" + l);
        init(l);
    }
    
    public Level(DirectObjectProtocol proto) {
        super(proto);
        init(Integer.parseInt(proto.getType()));
    }
    
    public IntegerProperty getLevelProperty() {
        return level;
    }

    public int getLevel() {
        return level.get();
    }
    
    public void setLevel(int l) {
        level.set(l);
    }
    
    @Override
    public String toString() {
        return super.getIdentifier() + ": " + level.get();
    }
}
