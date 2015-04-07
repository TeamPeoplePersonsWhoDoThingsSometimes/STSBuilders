/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 *
 * @author William
 */
public class TierCheckable implements StatusCheckable {

    private StringProperty name;//used in ui
    private int amount;
    private IntegerProperty tier;
    private boolean not;
    
    private void init(int t) {
        name = new SimpleStringProperty();
        tier = new SimpleIntegerProperty();
        tier.set(t);
        amount = 1;
        not = false;
        bindName();
    }
    
    public TierCheckable() {
        init(1);
    }
    
    public TierCheckable(int tier) {
        init(tier);
    }
    
    public TierCheckable(QuestProtobuf.StatusCheckableProtocol proto) {
        init(proto.getTier().getTier());
        if (proto.hasNot()) {
            not = proto.getNot();
        }
    }
    
    public void setTier(IntegerProperty t) {
        tier = t;
        bindName();
    }
    
    public IntegerProperty getTierProperty() {
        return tier;
    }
    
    public int getTier() {
        return tier.get();
    }
    
    @Override
    public StringProperty getNameProperty() {
        return name;
    }
    
    public void bindName() {
        name.set("" + tier.get());
    }
    
    @Override
    public String toString() {
        return "Tier Check: " + tier.get();
    }

    @Override
    public QuestProtobuf.StatusCheckableProtocol getStatusCheckableAsProtobuf() {
        QuestProtobuf.StatusCheckableProtocol.Builder builder = QuestProtobuf.StatusCheckableProtocol.newBuilder();
        QuestProtobuf.TierProtocol.Builder tBuilder = QuestProtobuf.TierProtocol.newBuilder();
        tBuilder.setTier(tier.get());
        builder.setTier(tBuilder);
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
    public boolean getEmpty() {
        return false;
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
    public void setNotEmpty() {
    }

}
