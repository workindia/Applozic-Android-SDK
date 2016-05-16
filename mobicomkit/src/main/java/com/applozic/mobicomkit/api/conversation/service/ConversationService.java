package com.applozic.mobicomkit.api.conversation.service;

import android.content.Context;
import android.util.Log;

import com.applozic.mobicomkit.api.conversation.database.ConversationDatabaseService;
import com.applozic.mobicomkit.feed.ChannelFeed;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.List;

/**
 * Created by sunil on 18/2/16.
 */
public class ConversationService {

    private static ConversationService conversationService;
    private Context context;
    private ConversationDatabaseService conversationDatabaseService;
    private ConversationClientService conversationClientService;

    private ConversationService(Context context) {
        this.context = context;
        conversationDatabaseService = ConversationDatabaseService.getInstance(context);
        conversationClientService = ConversationClientService.getInstance(context);

    }

    public synchronized static ConversationService getInstance(Context context) {
        if (conversationService == null) {
            conversationService = new ConversationService(context);
        }
        return conversationService;
    }

    public synchronized void processConversationArray(Conversation[] conversations, Channel channel, Contact contact) {

        if (conversations != null && conversations.length > 0) {
            for (Conversation conversation : conversations) {
                if (channel != null) {
                    conversation.setGroupId(channel.getKey());
                } else if (contact != null) {
                    conversation.setUserId(contact.getUserId());
                    conversation.setGroupId(0);
                }
                if (conversationDatabaseService.isConversationPresent(conversation.getId())) {
                    conversationDatabaseService.updateConversation(conversation);
                } else {
                    conversationDatabaseService.addConversation(conversation);
                }
            }
        }
    }

    public synchronized Conversation getConversationByConversationId(Integer conversationId) {
        return conversationDatabaseService.getConversationByConversationId(conversationId);
    }


    public synchronized List<Conversation> getConversationList(Channel channel, Contact contact) {
        return conversationDatabaseService.getConversationList(channel, contact);
    }


    public synchronized void addConversation(Conversation conversation) {
        if (conversation != null) {
            if (conversationDatabaseService.isConversationPresent(conversation.getId())) {
                conversationDatabaseService.updateConversation(conversation);
            } else {
                conversationDatabaseService.addConversation(conversation);
            }
        }
    }

    public synchronized ChannelFeed createConversation(Conversation conversation) {
        return conversationClientService.createConversation(conversation);
    }

    public synchronized void getConversation(Integer conversationId) {
        if (!conversationDatabaseService.isConversationPresent(conversationId)) {
            Conversation conversation = conversationClientService.getConversation(conversationId);
            if (conversation != null) {
                conversationDatabaseService.addConversation(conversation);
            }
        }
        return;
    }

    public synchronized void deleteConversation(String userId) {
        conversationDatabaseService.deleteConversation(userId);
    }

}
