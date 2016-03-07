package com.applozic.mobicomkit.api.conversation;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.applozic.mobicomkit.channel.service.ChannelClientService;

/**
 * Created by sunil on 10/2/16.
 */
public class ApplozicChannelIntentService extends IntentService {


    public static final String REMOVE_USER_ID_FROM_CHANNEL = "removeUserId";
    public static final String CHANGE_CHANNEL_NAME = "channelName";
    public static final String ADD_USER_TO_CHANNEL = "addUserId";
    public static final String CHANNEL_KEY = "channelKey";
    private static final String TAG ="ApplozicChannelIntentService";

    public ApplozicChannelIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Integer channelKey = intent.getIntExtra(CHANNEL_KEY, 0);
        String addUserId = intent.getStringExtra(ADD_USER_TO_CHANNEL);
        String newChannelName = intent.getStringExtra(CHANGE_CHANNEL_NAME);
        String removeUserId = intent.getStringExtra(REMOVE_USER_ID_FROM_CHANNEL);

        try {
            if (channelKey != null && channelKey != 0) {
                if (!TextUtils.isEmpty(addUserId)) {
                    ChannelClientService.getInstance(getApplicationContext()).addMemberToChannel(channelKey, addUserId);
                }
                if (!TextUtils.isEmpty(removeUserId)) {
                    ChannelClientService.getInstance(getApplicationContext()).removeMemberFromChannel(channelKey, removeUserId);
                }
                if (!TextUtils.isEmpty(newChannelName)) {
                    ChannelClientService.getInstance(getApplicationContext()).updateChannelName(channelKey, newChannelName);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
