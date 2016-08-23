package com.applozic.mobicomkit.api.conversation;

import android.app.IntentService;
import android.content.Intent;

import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.conversation.database.MessageDatabaseService;
import com.applozic.mobicomkit.channel.service.ChannelService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

/**
 * Created by sunil on 23/7/16.
 */
public class ConversationReadService extends IntentService {

    public static final String CONTACT = "contact";
    public static final String CHANNEL = "channel";
    private static final String TAG = "ConversationReadService";

    public ConversationReadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Contact contact = (Contact) intent.getSerializableExtra(CONTACT);
        Channel channel = (Channel) intent.getSerializableExtra(CHANNEL);
        MessageClientService messageClientService = new MessageClientService(getApplicationContext());
        MessageDatabaseService messageDatabaseService = new MessageDatabaseService(getApplicationContext());

        Integer unreadCount = null;
        if (contact != null) {
            Contact newContact = new AppContactService(getApplicationContext()).getContactById(contact.getContactIds());
            unreadCount = newContact.getUnreadCount();
            messageDatabaseService.updateReadStatusForContact(contact.getContactIds());
        } else if (channel != null) {
            Channel newChannel = ChannelService.getInstance(getApplicationContext()).getChannelByChannelKey(channel.getKey());
            unreadCount = newChannel.getUnreadCount();
            messageDatabaseService.updateReadStatusForChannel(String.valueOf(newChannel.getKey()));
        }
        if (unreadCount != null && unreadCount != 0) {
            messageClientService.updateReadStatus(contact, channel);
        } else {
            UserService.getInstance(getApplicationContext()).processUserReadConversation();
        }
    }

}
