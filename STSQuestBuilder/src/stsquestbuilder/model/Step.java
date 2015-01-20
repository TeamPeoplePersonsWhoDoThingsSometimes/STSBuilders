package stsquestbuilder.model;

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
    private ArrayList<Action> actions;
    
    public Step(String name, String description, ArrayList<Action> parts) {
        stepName = name;
        stepDescription = description;
        actions = parts;
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
        actions = new ArrayList<>();
        for(QuestProtobuf.StatusCheckableProtocol s : statusProtobufs) {
            Action a = new Action(s.getAction(), (s.hasAmount() ? s.getAmount() : 1));
            actions.add(a);
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

    public ArrayList<Action> getActions() {
        return actions;
    }

    public void setActions(ArrayList<Action> actions) {
        this.actions = actions;
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
        for(Action a : actions) {
            builder.addStatusesInStep(a.getStatusCheckableAsProtobuf());
        }
        
        return builder.build();    
    }
    
}
