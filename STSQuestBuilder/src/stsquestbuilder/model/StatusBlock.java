package stsquestbuilder.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusBlockProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.StatusCheckableProtocol;
import stsquestbuilder.protocolbuffers.QuestProtobuf.SpawnCommandProtocol;


/**
 *
 * @author William
 */
public class StatusBlock {
    private String name;
    private ObservableList<StatusReference> statusesToBeMet;
    private ObservableList<SpawnCommand> blockCommands;
    
    private void init(String name) {
        this.name = name;
        statusesToBeMet = FXCollections.observableArrayList();
        blockCommands = FXCollections.observableArrayList();
    }
    
    public StatusBlock() {
        init("");
    }
    
    public StatusBlock(String name) {
        init(name);
    }
    
    public StatusBlock(StatusBlockProtocol proto) {
        init(proto.getName());
        
        for(StatusCheckableProtocol s : proto.getStatusesList()) {
            statusesToBeMet.add(new StatusReference(StatusCheckableFactory.getStatusFromProtobuf(s)));
        }
        
        for(SpawnCommandProtocol s : proto.getCommandsList()) {
            blockCommands.add(new SpawnCommand(s));
        }
    }
    
    public ObservableList<StatusReference> getStatuses() {
        return statusesToBeMet;
    }
    
    public void newStatus() {
        StatusReference ref = new StatusReference(StatusCheckableFactory.getEmptyStatus());
        statusesToBeMet.add(ref);
    }
    
    public void removeStatus(StatusReference status) {
        statusesToBeMet.remove(status);
    }
    
    public void newCommand() {
        blockCommands.add(new SpawnCommand());
    }
    
    public void removeCommand(SpawnCommand command) {
        blockCommands.remove(command);
    }
    
    public ObservableList<SpawnCommand> getCommands() {
        return blockCommands;
    }
    
    public StatusBlockProtocol getStatusAsProtobuf() {
        StatusBlockProtocol.Builder builder = StatusBlockProtocol.newBuilder();
        
        builder.setName(name);
        
        for(SpawnCommand c : blockCommands) {
            builder.addCommands(c.getSpawnCommandAsProto());
        }
           
        for(StatusReference r : statusesToBeMet) {
            builder.addStatuses(r.getStatus().getStatusCheckableAsProtobuf());
        }
        
        return builder.build();
    }
    
    @Override
    public String toString() {
        return name;
    }
}
