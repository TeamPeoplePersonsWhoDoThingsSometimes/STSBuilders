/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stsquestbuilder.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableColumn;
import javafx.scene.input.MouseEvent;

import stsquestbuilder.STSQuestBuilder;
import stsquestbuilder.model.Quest;
import stsquestbuilder.model.Conversation;

/**
 * FXML Controller class
 *
 * @author William
 */
public class PrimaryScreenController implements Initializable {

    @FXML
    private TableView<Quest> QuestTable;
    
    @FXML
    private TableColumn<Quest, String> QuestName;
    
    @FXML
    private TableColumn<Quest, Number> QuestLength;
    
    @FXML
    private TableColumn<Quest, String> QuestCreator;
    
    @FXML
    private TitledPane questRemovalPopup;
    
    @FXML
    private TitledPane conversationRemovalPopup;
    
    @FXML
    private TableView<Conversation> ConversationTable;
    
    @FXML
    private TableColumn<Conversation, String> ConversationName;
    
    @FXML
    private TableColumn<Conversation, Number> ConversationLength;
    
    @FXML
    private TableColumn<Conversation, String> ConversationCreator;
    
    private STSQuestBuilder app;
    
    private Quest selectedQuest;

    private Conversation selectedConversation;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //rig up columns to model data
        QuestName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        QuestLength.setCellValueFactory(cellData -> cellData.getValue().getLengthProperty());
        QuestCreator.setCellValueFactory(cellData -> cellData.getValue().getCreatorProperty());
        ConversationName.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());
        ConversationLength.setCellValueFactory(cellData -> cellData.getValue().getNodesProperty());
        ConversationCreator.setCellValueFactory(cellData -> cellData.getValue().getCreatorProperty());
        
        
        //setup the row listeners by changing the factory callback
        //since the API gives us no other direct row access
        QuestTable.setRowFactory(table -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                onQuestRowSelect(event, row);
            });
            return row;
        });
        
        ConversationTable.setRowFactory(table -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                onConversationRowSelect(event, row);
            });
            return row;
        });
    }
    
    /**
     * Handle a quest row click
     * 
     * @param event the event that triggered this handler
     * @param row the table row that was clicked on
     */
    public void onQuestRowSelect(MouseEvent event, TableRow row) {
        //only open on double click, else select
        if(event.getClickCount() == 2) {
            Quest quest = (Quest) row.getItem();
            QuestBuilderScreenController.openQuestBuilderScreenForQuest(quest, app);
        } else {
            selectedQuest = (Quest)row.getItem();
        }
    }
    
    /**
     * Handle a conversation row click
     * @param event the event that triggered this handler
     * @param row the table row that was clicked on
     */
    public void onConversationRowSelect(MouseEvent event, TableRow row) {
        //only open on double click, else select
        if(event.getClickCount() == 2) {
            Conversation convo = (Conversation) row.getItem();
            ConversationBuilderScreenController.openConversationBuilderScreenForConversation(convo, app);
        } else {
            selectedConversation = (Conversation)row.getItem();
        }
    }
        
    /**
     * Handle new Quest button clicked
     * @param event mouse event that triggered this handler 
     */
    public void newQuestButtonClicked(MouseEvent event) {
        Quest newQuest = new Quest("New Quest");
        QuestTable.getItems().add(newQuest);
        QuestBuilderScreenController.openQuestBuilderScreenForQuest(newQuest, app);
    }
    
    /**
     * Handle new conversation button clicked
     * @param event the event that triggered this handler
     */
    public void newConversationButtonClicked(MouseEvent event) {
        Conversation newConversation = new Conversation(STSQuestBuilder.UserName);
        ConversationTable.getItems().add(newConversation);
        ConversationBuilderScreenController.openConversationBuilderScreenForConversation(newConversation, app);
    }
    
    /**
     * Handle remove quest button clicked
     * @param event the event that triggered this handler
     */
    public void removeQuestButtonClicked(MouseEvent event) {
        questRemovalPopup.setVisible(true);
    }
    
    /**
     * Confirm the removal of a quest via mouse click
     * @param event the event that triggered this handler
     */
    public void confirmQuestRemoval(MouseEvent event) {
        QuestTable.getItems().remove(selectedQuest);
        app.save();
        questRemovalPopup.setVisible(false);
    }
    
    /**
     * decline the removal of a quest
     * @param event the event that triggered this handler
     */
    public void declineQuestRemoval(MouseEvent event) {
        questRemovalPopup.setVisible(false);
    }
    
    /**
     * Handle remove conversation button clicked
     * @param event the event that triggered this handler
     */
    public void removeConversationButtonClicked(MouseEvent event) {
        conversationRemovalPopup.setVisible(true);
    }
    
    /**
     * Confirm the removal of a conversation via mouse click
     * @param event the event that triggered this handler
     */
    public void confirmConversationRemoval(MouseEvent event) {
        ConversationTable.getItems().remove(selectedConversation);
        app.save();
        conversationRemovalPopup.setVisible(false);
    }
    
    /**
     * decline the removal of a conversation
     * @param event the event that triggered this handler
     */
    public void declineConversationRemoval(MouseEvent event) {
        conversationRemovalPopup.setVisible(false);
    }
    
    public void setApp(STSQuestBuilder mainApp) {
        app = mainApp;
    }
    
    public void setQuests(ObservableList<Quest> quests) {
        QuestTable.setItems(quests);
    }
    
    public void setConversations(ObservableList<Conversation> conversations) {
        ConversationTable.setItems(conversations);
    }
    
}
