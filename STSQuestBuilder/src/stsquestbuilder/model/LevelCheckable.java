/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 *
 * @author William
 */
public class LevelCheckable implements StatusCheckable {

    private StringProperty name;//used in ui
    private int amount;
    private IntegerProperty level;
    
    private void init(int t) {
        name = new SimpleStringProperty();
        level = new SimpleIntegerProperty();
        level.set(t);
        amount = 1;
        bindName();
    }
    
    public LevelCheckable() {
        init(1);
    }
    
    public LevelCheckable(int level) {
        init(level);
    }
    
    public LevelCheckable(QuestProtobuf.StatusCheckableProtocol proto) {
        init(proto.getTier().getTier());
    }
    
    public void setLevel(IntegerProperty t) {
        level = t;
        bindName();
    }
    
    public IntegerProperty getLevelProperty() {
        return level;
    }
    
    public int getLevel() {
        return level.get();
    }
    
    @Override
    public StringProperty getNameProperty() {
        return name;
    }
    
    public void bindName() {
        name.set("" + level.get());
    }
    
    @Override
    public String toString() {
        return "Level Check: " + level.get();
    }

    @Override
    public QuestProtobuf.StatusCheckableProtocol getStatusCheckableAsProtobuf() {
        QuestProtobuf.StatusCheckableProtocol.Builder builder = QuestProtobuf.StatusCheckableProtocol.newBuilder();
        QuestProtobuf.LevelProtocol.Builder lBuilder = QuestProtobuf.LevelProtocol.newBuilder();
        lBuilder.setLevel(level.get());
        builder.setLevel(lBuilder);
        builder.setAmount(1);
        return builder.build();
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public int getAmount() {
        return amount;
    }
    
    @Override
    public boolean getEmpty() {
        return false;
    }

    @Override
    public void setNotEmpty() {
    }

}
