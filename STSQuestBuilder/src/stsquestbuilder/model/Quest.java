package stsquestbuilder.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.StringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

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
    private final ObjectProperty<QuestProtobuf.Biome> biome;
    
    private ArrayList<Step> steps;
    
    public Quest() {
        this("");
    }
    
    public Quest(String name) {
        questName = new SimpleStringProperty(name);
        length = new SimpleIntegerProperty(0);
        creator = new SimpleStringProperty(STSQuestBuilder.UserName);
        steps = new ArrayList<>();
        biome = new SimpleObjectProperty<>();
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
        biome = new SimpleObjectProperty<>();
        
        if (quest.hasBiome()) {
            biome.set(quest.getBiome());
        } else {
            biome.set(QuestProtobuf.Biome.C);
        }
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
    
    public void setBiome(QuestProtobuf.Biome b) {
        biome.set(b);
    }
    
    public ObjectProperty<QuestProtobuf.Biome> getBiomeProperty() {
        return biome;
    }
    
    public QuestProtobuf.Biome getBiome() {
        return biome.get();
    }
    
    public QuestProtobuf.QuestProtocol getQuestAsProtobuf() {
        QuestProtobuf.QuestProtocol.Builder builder = QuestProtobuf.QuestProtocol.newBuilder();
        builder.setName(this.getName());
        
        builder.setBiome(biome.get());
        
        if (this.getCreator() != null) {
            builder.setCreator(this.getCreator());
        }
        
        //create step protobufs
        for (Step s : steps) {
            builder.addSteps(s.getProtobufForStep());
        }
        
        return builder.build();
    }
    
}
