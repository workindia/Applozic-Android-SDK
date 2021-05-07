package com.applozic.mobicomkit.uiwidgets.conversation.mentions;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.applozic.mobicomkit.channel.database.ChannelDatabaseService;
import com.applozic.mobicomkit.contact.AppContactService;
import com.applozic.mobicommons.people.channel.ChannelUserMapper;
import com.applozic.mobicommons.people.contact.Contact;

import java.util.ArrayList;
import java.util.List;

public final class MentionHelper {
    public static @NonNull List<Mention> getMentionsListForChannel(Context context, Integer channelKey) {
        ChannelDatabaseService channelDatabaseService = ChannelDatabaseService.getInstance(context);
        List<ChannelUserMapper> channelUserMapperList = channelDatabaseService.getChannelUserList(channelKey);
        if (channelUserMapperList == null) {
            return new ArrayList<>();
        }
        List<Mention> mentionUsersList = new ArrayList<>();
        AppContactService appContactService = new AppContactService(context);
        for (ChannelUserMapper channelUserMapper : channelUserMapperList) {
            Contact contact = appContactService.getContactById(channelUserMapper.getUserKey());
            if (contact != null && !TextUtils.isEmpty(contact.getUserId())) {
                mentionUsersList.add(new Mention(contact.getUserId(), contact.getDisplayName(), !TextUtils.isEmpty(contact.getLocalImageUrl()) ? contact.getLocalImageUrl() : contact.getImageURL()));
            } else {
                mentionUsersList.add(new Mention(channelUserMapper.getUserKey()));
            }
        }
        return mentionUsersList;
    }
}
