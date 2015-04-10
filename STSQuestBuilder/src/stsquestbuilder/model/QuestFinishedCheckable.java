package stsquestbuilder.model;

import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.QuestFinishedProtocol;

/**
 *
 * @author William
 */
public class QuestFinishedCheckable implements StatusCheckable {
    
    private StringProperty name;//used in ui
    private int amount;
    private StringProperty quest;
    private boolean empty;
    private boolean not;
    
    private void init(String q) {
        name = new SimpleStringProperty();
        quest = new SimpleStringProperty();
        quest.set(q);
        empty = true;
        amount = -1;
        not = false;
    }
    
    public QuestFinishedCheckable() {
        init("");
    }
    
    public QuestFinishedCheckable(String quest) {
        init(quest);
    }
    
    public QuestFinishedCheckable(StatusCheckableProtocol proto) {
        init(proto.getQuest().getName());
        if (proto.hasNot()) {
            not = proto.getNot();
        }
    }
    
    public void setQuestProperty(StringProperty prop) {
        quest = prop;
    }
    
    public StringProperty getQuestProperty() {
        return quest;
    }
    
    public void setQuest(String qName) {
        quest.set(qName);
    }
    
    public String getQuest() {
        return quest.get();
    }
    
    @Override
    public StringProperty getNameProperty() {
        return name;
    }
    
    @Override
    public void setNotEmpty() {
        empty = false;
    }
    
    @Override
    public String toString() {
        if (empty) 
            return "Empty Check";
        return "Quest Finished Check: " + quest;
    }

    @Override
    public StatusCheckableProtocol getStatusCheckableAsProtobuf() {
        StatusCheckableProtocol.Builder builder = StatusCheckableProtocol.newBuilder();
        
        QuestFinishedProtocol.Builder qBuilder = QuestFinishedProtocol.newBuilder();
        qBuilder.setName(quest.get());
        
        builder.setQuest(qBuilder.build());
        builder.setNot(not);
        
        if (amount > 0) {
            builder.setAmount(amount);
        }
        
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
        return empty;
    }
}
