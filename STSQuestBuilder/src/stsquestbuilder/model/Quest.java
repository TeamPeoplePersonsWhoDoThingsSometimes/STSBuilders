package stsquestbuilder.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 *
 * @author William
 */
public class Quest {
    private final StringProperty questName;
    private final IntegerProperty length;
    private final StringProperty creator;
    
    private ArrayList<Step> steps;
    
    public Quest() {
        this("");
    }
    
    public Quest(String name) {
        questName = new SimpleStringProperty(name);
        length = new SimpleIntegerProperty(0);
        creator = new SimpleStringProperty(STSQuestBuilder.UserName);
        steps = new ArrayList<>();
    }
    
    /**
     * Constructor provides for simple conversion of QuestProtocol protobufs into
     * Quests
     * @param quest the quest protobuf to create from
     */
    public Quest(QuestProtobuf.QuestProtocol quest) {
        questName = new SimpleStringProperty(quest.getName());
        if(quest.hasCreator()) {
            creator = new SimpleStringProperty(quest.getCreator());
        } else {
            creator = new SimpleStringProperty();
        }
        
        //load step protocols
        List<QuestProtobuf.StatusStepProtocol> stepProtocols = quest.getStepsList();
        steps = new ArrayList<>();
        for(QuestProtobuf.StatusStepProtocol s : stepProtocols) {
            steps.add(new Step(s));
        }
        
        length = new SimpleIntegerProperty(steps.size());
    }
    
    public StringProperty getNameProperty() {
        return questName;
    }
    
    public String getName() {
        return questName.get();
    }
    
    public void setName(String name) {
        questName.set(name);
    }
    
    public IntegerProperty getLengthProperty() {
        return length;
    }
    
    public int getLength() {
        return length.get();
    }
    
    public ArrayList<Step> getSteps() {
        return steps;
    }
    
    public void setSteps(ArrayList<Step> questSteps) {
        steps = questSteps;
        length.set(steps.size());
    }
    
    public StringProperty getCreatorProperty() {
        return creator;
    }
    
    public String getCreator() {
        return creator.get();
    }
    
    public QuestProtobuf.QuestProtocol getQuestAsProtobuf() {
        QuestProtobuf.QuestProtocol.Builder builder = QuestProtobuf.QuestProtocol.newBuilder();
        builder.setName(this.getName());
        if(this.getCreator() != null) {
            builder.setCreator(this.getCreator());
        }
        
        //create step protobufs
        for(Step s : steps) {
            builder.addSteps(s.getProtobufForStep());
        }
        
        return builder.build();
    }
    
}
