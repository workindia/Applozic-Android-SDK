package com.applozic.mobicomkit.channel.service;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.people.ChannelCreate;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.feed.ChannelFeed;
import com.applozic.mobicomkit.feed.ChannelFeedApiResponse;
import com.applozic.mobicomkit.feed.ChannelName;
import com.applozic.mobicomkit.sync.SyncChannelFeed;
import com.applozic.mobicommons.json.GsonUtils;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;

import java.net.URLEncoder;

/**
 * Created by sunil on 29/12/15.
 */
public class ChannelClientService extends MobiComKitClientService {
    private static final String CHANNEL_INFO_URL = "/rest/ws/group/info";
    private static final String CHANNEL_SYNC_URL = "/rest/ws/group/list";
    private static final String CREATE_CHANNEL_URL = "/rest/ws/group/create";
    private static final String ADD_MEMBER_TO_CHANNEL_URL = "/rest/ws/group/add/member";
    private static final String REMOVE_MEMBER_FROM_CHANNEL_URL = "/rest/ws/group/remove/member";
    private static final String CHANNEL_NAME_CHANGE_URL = "/rest/ws/group/change/name";
    private static final String CHANNEL_LEFT_URL = "/rest/ws/group/left";

    private static final String UPDATED_AT = "updatedAt";
    private static final String USER_ID = "userId";
    private static final String NEW_CHANNEL_NAME = "newName";
    private static final String GROUP_ID = "groupId";
    private static final String TAG = "ChannelClientService";
    private static ChannelClientService channelClientService;
    private Context context;
    private ChannelDatabaseService channelDatabaseService;
    private HttpRequestUtils httpRequestUtils;


    private ChannelClientService(Context context) {
        super(context);
        this.context = context;
        this.channelDatabaseService = ChannelDatabaseService.getInstance(context);
        this.httpRequestUtils = new HttpRequestUtils(context);
    }


    public static ChannelClientService getInstance(Context context) {
        if (channelClientService == null) {
            channelClientService = new ChannelClientService(context);
        }
        return channelClientService;
    }

    public String getChannelInfoUrl() {
        return getBaseUrl() + CHANNEL_INFO_URL;
    }

    public String getChannelSyncUrl() {
        return getBaseUrl() + CHANNEL_SYNC_URL;
    }

    public String getCreateChannelUrl() {
        return getBaseUrl() + CREATE_CHANNEL_URL;
    }

    public String getAddMemberToGroup() {
        return getBaseUrl() + ADD_MEMBER_TO_CHANNEL_URL;
    }

    public String getRemoveMemberUrl() {
        return getBaseUrl() + REMOVE_MEMBER_FROM_CHANNEL_URL;
    }

    public String getUpdateNewChannelNameUrl() {
        return getBaseUrl() + CHANNEL_NAME_CHANGE_URL;
    }

    public String getChannelLeftUrl() {
        return getBaseUrl() + CHANNEL_LEFT_URL;
    }

    public ChannelFeed getChannelInfo(Integer channelKey) {
        String response = "";
        try {
            response = httpRequestUtils.getResponse(getCredentials(), getChannelInfoUrl() + "?groupId=" + channelKey, "application/json", "application/json");
            ChannelFeedApiResponse channelFeedApiResponse = (ChannelFeedApiResponse) GsonUtils.getObjectFromJson(response, ChannelFeedApiResponse.class);
            Log.i(TAG, "Channel info response  is :" + response);

            if (channelFeedApiResponse.isSuccess()) {
                ChannelFeed channelFeed = channelFeedApiResponse.getResponse();
                return channelFeed;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public SyncChannelFeed getChannelFeed(String lastChannelSyncTime) {
        String url = getChannelSyncUrl() + "?" +
                UPDATED_AT
                + "=" + lastChannelSyncTime;
        try {
            String response = httpRequestUtils.getResponse(getCredentials(), url, "application/json", "application/json");
            Log.i(TAG, "Channel sync call response: " + response);
            return (SyncChannelFeed) GsonUtils.getObjectFromJson(response, SyncChannelFeed.class);
        } catch (Exception e) {
            return null;
        }
    }


    public ChannelFeed createChannel(ChannelCreate channelCreate) {
        ChannelFeed channelFeed = null;
        try {
            String jsonFromObject = GsonUtils.getJsonFromObject(channelCreate, channelCreate.getClass());
            String createChannelResponse = httpRequestUtils.postData(getCredentials(), getCreateChannelUrl(), "application/json", "application/json", jsonFromObject);
            Log.i(TAG, "Create channel Response :" + createChannelResponse);
            ChannelFeedApiResponse channelFeedApiResponse = (ChannelFeedApiResponse) GsonUtils.getObjectFromJson(createChannelResponse, ChannelFeedApiResponse.class);

            if (channelFeedApiResponse.isSuccess()) {
                channelFeed = channelFeedApiResponse.getResponse();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return channelFeed;
    }

    public synchronized ApiResponse addMemberToChannel(Channel channel, String userId) {
        ApiResponse  apiResponse = null;
        try {
            if (channel != null && channel.getKey() != null && !TextUtils.isEmpty(userId) ) {
                String url = getAddMemberToGroup() + "?" +
                        GROUP_ID
                        + "=" + URLEncoder.encode(String.valueOf(channel.getKey()), "UTF-8") + "&" + USER_ID + "=" + URLEncoder.encode(userId, "UTF-8");
                String response = httpRequestUtils.getResponse(getCredentials(), url, "application/json", "application/json");
                apiResponse = (ApiResponse) GsonUtils.getObjectFromJson(response, ApiResponse.class);
                Log.i(TAG, "Channel add member call response: " + apiResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

    public synchronized ApiResponse removeMemberFromChannel(Channel channel, ChannelUserMapper channelUserMapper) {
        ApiResponse apiResponse = null;
        try {
            if (channel != null && channel.getKey() != null &&  channelUserMapper != null) {
                String url = getRemoveMemberUrl() + "?" +
                        GROUP_ID
                        + "=" + URLEncoder.encode(String.valueOf(channel.getKey()), "UTF-8") + "&" + USER_ID + "=" + URLEncoder.encode(channelUserMapper.getUserKey(), "UTF-8");
                String response = httpRequestUtils.getResponse(getCredentials(), url, "application/json", "application/json");
                 apiResponse = (ApiResponse) GsonUtils.getObjectFromJson(response, ApiResponse.class);
                Log.i(TAG, "Channel remove member response: " + apiResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

    public synchronized ApiResponse updateChannelName(ChannelName channelName) {
        ApiResponse apiResponse = null;
        try {
            if (channelName != null && channelName.getGroupId() != null && !TextUtils.isEmpty(channelName.getNewName())) {
                String channelNameUpdateJson = GsonUtils.getJsonFromObject(channelName,ChannelName.class);
                String response = httpRequestUtils.postData(getCredentials(), getUpdateNewChannelNameUrl() , "application/json", "application/json", channelNameUpdateJson);
                apiResponse = (ApiResponse) GsonUtils.getObjectFromJson(response, ApiResponse.class);
                Log.i(TAG, "Update Channel name response: " + apiResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

    public synchronized ApiResponse leaveFromChannel(Channel channel) {
        ApiResponse  apiResponse = null;
        try {
            if (channel != null && channel.getKey() != null ) {
                String url = getChannelLeftUrl() + "?" +
                        GROUP_ID
                        + "=" + URLEncoder.encode(String.valueOf(channel.getKey()), "UTF-8");
                String response = httpRequestUtils.getResponse(getCredentials(), url, "application/json", "application/json");
                apiResponse = (ApiResponse) GsonUtils.getObjectFromJson(response, ApiResponse.class);
                Log.i(TAG, "Channel leave member call response: " + apiResponse.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return apiResponse;
    }

}
