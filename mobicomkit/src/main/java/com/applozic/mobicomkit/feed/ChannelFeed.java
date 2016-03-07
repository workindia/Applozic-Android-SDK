package com.applozic.mobicomkit.feed;

import com.applozic.mobicomkit.api.JsonMarker;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.channel.Conversation;

import java.util.Set;

/**
 * Created by sunil on 28/12/15.
 */
public class ChannelFeed extends JsonMarker {

    private Integer id;
    private String name;
    private String adminName;
    private int unreadCount;
    private int userCount;
    private short type;
    private Set<String> membersName;
    private Conversation conversationPxy;


    public ChannelFeed(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ChannelFeed(Channel group) {
        this.id = group.getKey();
        this.name = group.getName();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public short getType() {
        return type;
    }

    public void setType(short type) {
        this.type = type;
    }

    public Set<String> getMembersName() {
        return membersName;
    }

    public void setMembersName(Set<String> membersName) {
        this.membersName = membersName;
    }

    public Conversation getConversationPxy() {
        return conversationPxy;
    }

    public void setConversationPxy(Conversation conversationPxy) {
        this.conversationPxy = conversationPxy;
    }


    @Override
    public String toString() {
        return "ChannelFeed{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", adminName='" + adminName + '\'' +
                ", unreadCount=" + unreadCount +
                ", userCount=" + userCount +
                ", type=" + type +
                ", membersName=" + membersName +
                ", conversationPxy=" + conversationPxy +
                '}';
    }
}
