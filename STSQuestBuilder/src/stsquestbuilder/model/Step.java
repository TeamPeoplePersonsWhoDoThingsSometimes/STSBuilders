package stsquestbuilder.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

import stsquestbuilder.protocolbuffers.QuestProtobuf;

/**
 * This class encompasses an entire quest step, complete with actions, a step
 * name, and descriptive text to give the quest writer more influence over the
 * user's experience
 * 
 * @author William
 */
public class Step {
    private String stepName;
    private String stepDescription;
    private ObservableList<StatusReference> statuses;
    private ObservableList<SpawnCommand> commands;
    
    public Step(String name, String description, ObservableList<StatusReference> parts, ObservableList<SpawnCommand> comm) {
        stepName = name;
        stepDescription = description;
        statuses = parts;
        commands = comm;
    }

    /**
     * Constructor provides for simple conversion from Protobuf Status Step Protocol
     * into Step objects
     * @param step the protobuf to convert from
     */
    public Step(QuestProtobuf.StatusStepProtocol step) {
        stepName = step.getName();
        stepDescription = step.getDescription();
        
        //to read the statuses, load all the protobufs, then loop through,
        //add the conversion to the actions arraylist if it is not already in it
        //else add 1 to the occurrence of the action already present
        List<QuestProtobuf.StatusCheckableProtocol> statusProtobufs = step.getStatusesInStepList();
        statuses = FXCollections.observableArrayList();
        for(QuestProtobuf.StatusCheckableProtocol s : statusProtobufs) {
            statuses.add(new StatusReference(StatusCheckableFactory.getStatusFromProtobuf(s)));
        }
        
        List<QuestProtobuf.SpawnCommandProtocol> spawnProtobufs = step.getCommandsList();
        commands = FXCollections.observableArrayList();
        for (QuestProtobuf.SpawnCommandProtocol s : spawnProtobufs) {
            commands.add(new SpawnCommand(s));
        }
    }
    
    /*
     Getters and setters
    */
    
    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStepDescription() {
        return stepDescription;
    }

    public void setStepDescription(String stepDescription) {
        this.stepDescription = stepDescription;
    }

    public ObservableList<StatusReference> getActions() {
        return statuses;
    }

    public void setActions(ObservableList<StatusReference> statuses) {
        this.statuses = statuses;
    }
    
    public ObservableList<SpawnCommand> getCommands() {
        return commands;
    }
    
    public void setCommands(ObservableList<SpawnCommand> comm) {
        commands = comm;
    }
    
    /**
     * Builds this object as a StatusStep Protobuf
     * @return a protobuf with the information from this object
     */
    public QuestProtobuf.StatusStepProtocol getProtobufForStep() {
        QuestProtobuf.StatusStepProtocol.Builder builder = QuestProtobuf.StatusStepProtocol.newBuilder();
        builder.setName(this.getStepName());
        builder.setDescription(this.getStepDescription());
        
        //get status checkable protobufs
        for (StatusReference ref : statuses) {
            builder.addStatusesInStep(ref.getStatus().getStatusCheckableAsProtobuf());
        }
        
        for (SpawnCommand comm : commands) {
            builder.addCommands(comm.getSpawnCommandAsProto());
        }
        
        return builder.build();    
    }
    
}
