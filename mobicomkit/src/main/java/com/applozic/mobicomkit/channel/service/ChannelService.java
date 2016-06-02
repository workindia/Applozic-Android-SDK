package com.applozic.mobicomkit.channel.service;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.account.user.UserService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.service.ConversationService;
import com.applozic.mobicomkit.api.people.ChannelCreate;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicomkit.contact.BaseContactService;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.applozic.mobicomkit.feed.ChannelFeed;
import com.applozic.mobicomkit.feed.ChannelName;
import com.applozic.mobicomkit.sync.SyncChannelFeed;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sunil on 1/1/16.
 */
public class ChannelService {

    public static boolean isUpdateTitle = false;
    private static ChannelService channelService;
    public Context context;
    private ChannelDatabaseService channelDatabaseService;
    private ChannelClientService channelClientService;
    private BaseContactService baseContactService;
    private UserService userService;

    private ChannelService(Context context) {
        this.context = context;
        channelClientService = ChannelClientService.getInstance(context);
        channelDatabaseService = ChannelDatabaseService.getInstance(context);
        userService = UserService.getInstance(context);
        baseContactService = new AppContactService(context);
    }

    public synchronized static ChannelService getInstance(Context context) {
        if (channelService == null) {
            channelService = new ChannelService(context);
        }
        return channelService;
    }

    public Channel getChannelInfoFromLocalDb(Integer key) {
        return channelDatabaseService.getChannelByChannelKey(key);
    }

    public Channel getChannelInfo(Integer key) {
        if (key == null) {
            return null;
        }
        Channel channel = channelDatabaseService.getChannelByChannelKey(key);
        if (channel == null) {
            ChannelFeed channelFeed = channelClientService.getChannelInfo(key);
            if (channelFeed != null) {
                ChannelFeed[] channelFeeds = new ChannelFeed[1];
                channelFeeds[0] = channelFeed;
                processChannelFeedList(channelFeeds, false);
                BroadcastService.sendUpdateForName(context, channelFeed.getId(), BroadcastService.INTENT_ACTIONS.UPDATE_NAME.toString());
                channel = new Channel(channelFeed.getId(), channelFeed.getName(), channelFeed.getAdminName(), channelFeed.getType(), channelFeed.getUnreadCount());
                return channel;
            }
        }
        return channel;
    }

    public void processChannelFeedList(ChannelFeed[] channelFeeds, boolean isUserDetails) {
        if (channelFeeds != null && channelFeeds.length > 0) {
            for (ChannelFeed channelFeed : channelFeeds) {
                Set<String> memberUserIds = channelFeed.getMembersName();
                Channel channel = new Channel(channelFeed.getId(), channelFeed.getName(), channelFeed.getAdminName(), channelFeed.getType(), channelFeed.getUnreadCount());
                if (channelDatabaseService.isChannelPresent(channel.getKey())) {
                    channelDatabaseService.updateChannel(channel);
                } else {
                    channelDatabaseService.addChannel(channel);
                }
                if(channelFeed.getConversationPxy() != null){
                    channelFeed.getConversationPxy().setGroupId(channelFeed.getId());
                    ConversationService.getInstance(context).addConversation(channelFeed.getConversationPxy());
                }
                if (memberUserIds != null && memberUserIds.size() > 0) {
                    for (String userId : memberUserIds) {
                        ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelFeed.getId(), userId);
                        if (channelDatabaseService.isChannelUserPresent(channelFeed.getId(), userId)) {
                            channelDatabaseService.updateChannel(channelUserMapper);
                        } else {
                            channelDatabaseService.addChannelUserMapper(channelUserMapper);
                        }
                    }
                    if (isUserDetails) {
                        userService.processUserDetail(channelFeed.getUsers());
                    }
                }
            }
        }
    }

    public synchronized Channel getChannelByChannelKey(Integer channelKey) {
        if (channelKey == null) {
            return null;
        }
        return channelDatabaseService.getChannelByChannelKey(channelKey);
    }


    public List<ChannelUserMapper> getListOfUsersFromChannelUserMapper(Integer channelKey) {
        return channelDatabaseService.getChannelUserList(channelKey);
    }

    public Channel getChannel(Integer channelKey) {
        Channel channel = channelDatabaseService.getChannelByChannelKey(channelKey);
        if (channel == null) {
            channel = new Channel(channelKey);
        }
        return channel;
    }

    public void updateChannel(Channel channel) {
        if (channelDatabaseService.getChannelByChannelKey(channel.getKey()) == null) {
            channelDatabaseService.addChannel(channel);
        } else {
            channelDatabaseService.updateChannel(channel);
        }
    }

    public List<Channel> getChannelList() {
        return channelDatabaseService.getAllChannels();
    }

    public synchronized void syncChannels() {
        final MobiComUserPreference userpref = MobiComUserPreference.getInstance(context);
        SyncChannelFeed syncChannelFeed = channelClientService.getChannelFeed(userpref.getChannelSyncTime());
        if (syncChannelFeed.isSuccess()) {
            processChannelList(syncChannelFeed.getResponse());
            BroadcastService.sendUpdateForChannelSync(context, BroadcastService.INTENT_ACTIONS.CHANNEL_SYNC.toString());
        }
        userpref.setChannelSyncTime(syncChannelFeed.getGeneratedAt());

    }

    public synchronized Channel createChannel(final ChannelCreate channelCreate) {
        Channel channel = null;
        ChannelFeed channelFeed = channelClientService.createChannel(channelCreate);
        if (channelFeed != null) {
            ChannelFeed[] channelFeeds = new ChannelFeed[1];
            channelFeeds[0] = channelFeed;
            processChannelFeedList(channelFeeds, true);
            channel = new Channel(channelFeed.getId(), channelFeed.getName(), channelFeed.getAdminName(), channelFeed.getType(), channelFeed.getUnreadCount());
        }
        return channel;
    }

    public String removeMemberFromChannelProcess(Integer channelKey, String userId) {
        if (channelKey == null && TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.removeMemberFromChannel(channelKey, userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.removeMemberFromChannel(channelKey, userId);
        }
        return apiResponse.getStatus();
    }

    public String addMemberToChannelProcess(Integer channelKey, String userId) {
        if (channelKey == null && !TextUtils.isEmpty(userId)) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.addMemberToChannel(channelKey, userId);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelKey, userId);
            channelDatabaseService.addChannelUserMapper(channelUserMapper);
        }
        return apiResponse.getStatus();
    }

    public String leaveMemberFromChannelProcess(Integer channelKey, String userId) {
        if (channelKey == null) {
            return "";
        }
        ApiResponse apiResponse = channelClientService.leaveMemberFromChannel(channelKey);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.leaveMemberFromChannel(channelKey, userId);
        }
        return apiResponse.getStatus();
    }

    public String updateNewChannelNameProcess(ChannelName channelName) {
        if (channelName == null) {
            return null;
        }
        ApiResponse apiResponse = channelClientService.updateChannelName(channelName);
        if (apiResponse == null) {
            return null;
        }
        if (apiResponse.isSuccess()) {
            channelDatabaseService.updateChannelName(channelName);
        }
        return apiResponse.getStatus();
    }


    public synchronized void processChannelList(List<ChannelFeed> channelFeedList) {
        if (channelFeedList != null && channelFeedList.size() > 0) {
            for (ChannelFeed channelFeed : channelFeedList) {
                Set<String> memberUserIds = channelFeed.getMembersName();
                Set<String> userIds = new HashSet<>();
                Channel channel = new Channel(channelFeed.getId(), channelFeed.getName(), channelFeed.getAdminName(), channelFeed.getType(), channelFeed.getUnreadCount());
                if (channelDatabaseService.isChannelPresent(channel.getKey())) {
                    channelDatabaseService.updateChannel(channel);
                    channelDatabaseService.deleteChannelUserMappers(channel.getKey());
                } else {
                    channelDatabaseService.addChannel(channel);
                }
                if (memberUserIds != null && memberUserIds.size() > 0) {
                    for (String userId : memberUserIds) {
                        ChannelUserMapper channelUserMapper = new ChannelUserMapper(channelFeed.getId(), userId);
                        channelDatabaseService.addChannelUserMapper(channelUserMapper);
                        Contact contact = baseContactService.getContactById(userId);
                        if(TextUtils.isEmpty(contact.getFullName()) || TextUtils.isEmpty(contact.getImageURL())){
                            userIds.add(userId);
                        }
                    }
                    if(userIds != null && userIds.size()>0){
                        userService.processUserDetails(userIds);
                    }
                }
            }
        }
    }

    public synchronized boolean processIsUserPresentInChannel(Integer channelKey) {
        return channelDatabaseService.isChannelUserPresent(channelKey, MobiComUserPreference.getInstance(context).getUserId());
    }

    public synchronized boolean isUserAlreadyPresentInChannel(Integer channelKey, String userId) {
        return channelDatabaseService.isChannelUserPresent(channelKey, userId);
    }

    public synchronized String processChannelDeleteConversation(Channel channel, Context context) {
           String response =  new MobiComConversationService(context).deleteSync(null,channel,null);
         if(!TextUtils.isEmpty(response) && "success".equals(response)){
            channelDatabaseService.deleteChannelUserMappers(channel.getKey());
            channelDatabaseService.deleteChannel(channel.getKey());
        }
        return response;

    }

}




