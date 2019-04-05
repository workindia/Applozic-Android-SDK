package com.applozic.mobicomkit.api.people;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.contact.database.ContactDatabase;
import com.applozic.mobicomkit.listners.AlChannelListener;
import com.applozic.mobicomkit.listners.AlContactListener;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;

import java.lang.ref.WeakReference;

public class AlGetPeopleTask extends AsyncTask<Void, Object, Object> {
    private String userId;
    private String clientChannelKey;
    private Integer groupId;
    private AlChannelListener channelListener;
    private AlContactListener contactListener;
    private ContactDatabase contactDatabase;
    private ChannelDatabaseService channelDatabaseService;

    public AlGetPeopleTask(Context context, String userId, String clientChannelKey, Integer channelKey, AlChannelListener channelListener, AlContactListener contactListener, ContactDatabase contactDatabase, ChannelDatabaseService channelDatabaseService) {
        this.userId = userId;
        this.clientChannelKey = clientChannelKey;
        this.groupId = channelKey;
        this.channelListener = channelListener;
        this.contactListener = contactListener;

        if (contactDatabase == null) {
            this.contactDatabase = new ContactDatabase(context);
        } else {
            this.contactDatabase = contactDatabase;
        }

        if (channelDatabaseService == null) {
            this.channelDatabaseService = ChannelDatabaseService.getInstance(context);
        } else {
            this.channelDatabaseService = channelDatabaseService;
        }
    }


    @Override
    protected Object doInBackground(Void... voids) {
        try {
            if (!TextUtils.isEmpty(userId)) {
                return contactDatabase.getContactById(userId);
            }

            if (!TextUtils.isEmpty(clientChannelKey)) {
                return channelDatabaseService.getChannelByClientGroupId(clientChannelKey);
            }

            if (groupId != null && groupId > 0) {
                return channelDatabaseService.getChannelByChannelKey(groupId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (o instanceof Contact && contactListener != null) {
            contactListener.onGetContact((Contact) o);
        }
        if (o instanceof Channel && channelListener != null) {
            channelListener.onGetChannel((Channel) o);
        }
    }
}
