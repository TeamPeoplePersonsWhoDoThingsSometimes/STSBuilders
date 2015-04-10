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
public class NumAreasCheckable implements StatusCheckable {

    private StringProperty name;//used in ui
    private int amount;
    private IntegerProperty areas;
    private boolean not;
    
    private void init(int t) {
        name = new SimpleStringProperty();
        areas = new SimpleIntegerProperty();
        areas.set(t);
        amount = 1;
        not = false;
        bindName();
    }
    
    public NumAreasCheckable() {
        init(1);
    }
    
    public NumAreasCheckable(int numAreas) {
        init(numAreas);
    }
    
    public NumAreasCheckable(QuestProtobuf.StatusCheckableProtocol proto) {
        init(proto.getNumAreas().getNumAreas());
        if (proto.hasNot()) {
            not = proto.getNot();
        }
    }
    
    public void setAreas(IntegerProperty t) {
        areas = t;
        bindName();
    }
    
    public IntegerProperty getAreasProperty() {
        return areas;
    }
    
    public int getAreas() {
        return areas.get();
    }
    
    @Override
    public StringProperty getNameProperty() {
        return name;
    }
    
    public void bindName() {
        name.set("" + areas.get());
    }
    
    @Override
    public String toString() {
        return "Num Areas Check: " + areas.get();
    }

    @Override
    public QuestProtobuf.StatusCheckableProtocol getStatusCheckableAsProtobuf() {
        QuestProtobuf.StatusCheckableProtocol.Builder builder = QuestProtobuf.StatusCheckableProtocol.newBuilder();
        QuestProtobuf.NumAreasProtocol.Builder lBuilder = QuestProtobuf.NumAreasProtocol.newBuilder();
        lBuilder.setNumAreas(areas.get());
        builder.setNumAreas(lBuilder);
        builder.setAmount(1);
        builder.setNot(not);
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
    public boolean isNot() {
        return not;
    }

    @Override
    public void setNot(boolean not) {
        this.not = not;
    }
    
    @Override
    public boolean getEmpty() {
        return false;
    }

    @Override
    public void setNotEmpty() {
    }

}
