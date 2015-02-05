/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder;

import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
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
import stsquestbuilder.protocolbuffers.ConversationProtobuf;
import stsquestbuilder.protocolbuffers.QuestProtobuf;
import stsquestbuilder.view.PrimaryScreenController;


/**
 *
 * @author William
 */
public class STSQuestBuilder extends Application {
    
    public static final String UserName = System.getenv("USERNAME");
    public static final String Storage_File = "./out.quest";    
    public static final String Storage_File_Conversations = "./out.conv";
    public static final String Action_File = "./builder.data";

    
    private ObservableList<Quest> quests;
    private ObservableList<Conversation> conversations;
    
    @Override
    public void start(Stage primaryStage) {
        conversations = FXCollections.observableArrayList();
        quests = FXCollections.observableArrayList();
        loadTypes();
        loadQuests();
        loadConversations();
        
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
        
        Scene scene = new Scene(root, 800, 800);
        
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
        ConversationProtobuf.ConversationPackage.Builder builder = ConversationProtobuf.ConversationPackage.newBuilder();
        ConversationProtobuf.ConversationPackage cPack;
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
        FileInputStream inStream;
        QuestProtobuf.BuilderPackage pack;
        try {
            inStream = new FileInputStream(Action_File);
            pack = QuestProtobuf.BuilderPackage.parseFrom(inStream);
            inStream.close();
        } catch(IOException excep) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, excep);
            return;
        }
        
        String[] types = {"Basic", "Badass"};
        EnemyType.enemyTypes = new ArrayList<>();
        for(QuestProtobuf.DirectObjectProtocol DirObj : pack.getActionsList()) {
            EnemyType.enemyTypes.add(new EnemyType(DirObj.getName(), types));
        }
    }
    
    /**
     * Load the data from the out.convo file which contains previously built
     * conversations
     */
    public void loadConversations() {
        //first load the package        
        FileInputStream inStream;
        ConversationProtobuf.ConversationPackage pack;
        try {
            inStream = new FileInputStream(Storage_File_Conversations);
            pack = ConversationProtobuf.ConversationPackage.parseFrom(inStream);
            inStream.close();
        } catch(IOException excep) {
            Logger.getLogger(STSQuestBuilder.class.getName()).log(Level.SEVERE, null, excep);
            return;
        }
        
        for(ConversationProtobuf.Conversation c : pack.getConversationsList()) {
            conversations.add(new Conversation(c));
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
