package stsquestbuilder;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Parent;

import stsquestbuilder.model.*;
import stsquestbuilder.protocolbuffers.QuestProtobuf;
import stsquestbuilder.view.PrimaryScreenController;


/**
 *
 * @author William
 */
public class STSQuestBuilder extends Application {
    
    public static STSQuestBuilder instance;
    public static final String UserName = System.getenv("USERNAME");
    public static final String Storage_File = "./out.quest";    
    public static final String Storage_File_Conversations = "./out.conv";
    public static final String Action_File = "/builder.data";
    
    public static final double WINDOW_WIDTH = 600;
    public static final double WINDOW_HEIGHT = 400;

    private ObservableList<Quest> quests;
    private ObservableList<Conversation> conversations;
    
    public ObservableList<String> getQuestNames() {
        ObservableList<String> questNames = FXCollections.observableArrayList();
        
        for (Quest q : quests) {
            questNames.add(q.getName());
        }
        
        return questNames;
    }
    
    @Override
    public void start(Stage primaryStage) {
        instance = this;
        conversations = FXCollections.observableArrayList();
        quests = FXCollections.observableArrayList();
        
        //this ordering is critical, types are needed for conversations and quests
        //and conversations are needed for quests
        loadTypes();
        loadConversations();
        loadQuests();
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/stsquestbuilder/view/PrimaryScreen.fxml"));
        Parent parent;
        try {
            parent = (Parent) loader.load();
        } catch(IOException excep) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, excep);
            System.exit(1);
            return;
        }
        
        PrimaryScreenController prime = (PrimaryScreenController) loader.getController();
        prime.setQuests(quests);
        prime.setConversations(conversations);
        prime.setApp(this);
        
        StackPane root = new StackPane();
        
        root.getChildren().add(parent);
        
        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
        
        primaryStage.setTitle("STS Content Builder");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Store all the current quests in a file
     */
    public void save() {
        //convert quests to protobufs and store in a quest package protobuf
        QuestProtobuf.QuestPackage.Builder packBuilder = QuestProtobuf.QuestPackage.newBuilder();
        QuestProtobuf.QuestPackage pack;
        for(Quest q : quests) {
            packBuilder.addQuests(q.getQuestAsProtobuf());
        }
        
        pack = packBuilder.build();
        
        FileOutputStream oStream;
        try {
            //now write out the bites to a file
            oStream = new FileOutputStream(Storage_File);
            pack.writeTo(oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException ex) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        //convert conversations to protobufs and store in a conversation package protobuf
        QuestProtobuf.ConversationPackage.Builder builder = QuestProtobuf.ConversationPackage.newBuilder();
        QuestProtobuf.ConversationPackage cPack;
        for(Conversation c : conversations) {
            builder.addConversations(c.getConversationAsProtobuf());
        }
        
        cPack = builder.build();
        
        try {
            //now write out the bites to a file
            oStream = new FileOutputStream(Storage_File_Conversations);
            cPack.writeTo(oStream);
            oStream.flush();
            oStream.close();
        } catch (IOException ex) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
    }
    
    /**
     * Load the quests from the storage file
     */
    public void loadQuests() {
        //first load the package        
        FileInputStream inStream;
        QuestProtobuf.QuestPackage pack;
        try {
            inStream = new FileInputStream(Storage_File);
            pack = QuestProtobuf.QuestPackage.parseFrom(inStream);
            inStream.close();
        } catch(IOException excep) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, excep);
            return;
        }
        
        //next unwrap the quests from the package
        List<QuestProtobuf.QuestProtocol> questProtocols = pack.getQuestsList();
        for(QuestProtobuf.QuestProtocol q : questProtocols) {
            quests.add(new Quest(q));
        }
        
    }
    
    /**
     * Load the data from the builder.data file which contains potential direct
     * objects
     */
    public void loadTypes() {
        //first load the package        
        InputStream inStream;
        QuestProtobuf.BuilderPackage pack;
        try {
            inStream = getClass().getResourceAsStream(Action_File);
            pack = QuestProtobuf.BuilderPackage.parseFrom(inStream);
            inStream.close();
        } catch(IOException excep) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, excep);
            return;
        }
        
        String[] types = {"Basic", "Badass"};
        EnemyType.enemyTypes = new ArrayList<>();
        for(QuestProtobuf.DirectObjectProtocol DirObj : pack.getEnemiesList()) {
            EnemyType.enemyTypes.add(new EnemyType(DirObj.getName(), types));
        }
        
        for(QuestProtobuf.DirectObjectProtocol DirObj : pack.getHacksList()) {
            new ItemType(DirObj.getName(), ItemType.GeneralType.HACK);
        }
        
        for (QuestProtobuf.DirectObjectProtocol DirObj : pack.getWeaponsList()) {
            new ItemType(DirObj.getName(), ItemType.GeneralType.WEAPON);
        }
    }
    
    /**
     * Load the data from the out.convo file which contains previously built
     * conversations
     */
    public void loadConversations() {
        //first load the package        
        FileInputStream inStream;
        QuestProtobuf.ConversationPackage pack;
        try {
            inStream = new FileInputStream(Storage_File_Conversations);
            pack = QuestProtobuf.ConversationPackage.parseFrom(inStream);
            inStream.close();
        } catch(IOException excep) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, excep);
            return;
        }
        
        //initialize uid map
        for (QuestProtobuf.Conversation c : pack.getConversationsList()) {
            for (QuestProtobuf.ConversationNode n : c.getAllNodesList()) {
                new ConversationNode(n.getUid());
            }
        }
        
        for(QuestProtobuf.Conversation c : pack.getConversationsList()) {
            conversations.add(new Conversation(c));
        }
    }
    
    public ObservableList<Conversation> getConversations() {
        return conversations;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
