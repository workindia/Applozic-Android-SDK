package com.applozic.mobicomkit.api.people;

import com.applozic.mobicommons.json.JsonMarker;
import com.applozic.mobicommons.people.channel.Channel;

import java.util.List;

/**
 * Created by sunil on 29/1/16.
 */
public class ChannelInfo extends JsonMarker{

    private String clientGroupId;
    private String groupName;
    private List<String> groupMemberList;
    private String imageUrl;
    private int type = Channel.GroupType.PUBLIC.getValue().intValue();

    public ChannelInfo(String groupName, List<String> groupMemberList) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
    }

    public ChannelInfo(String groupName, List<String> groupMemberList, String imageLink) {
        this.groupName = groupName;
        this.groupMemberList = groupMemberList;
        this.imageUrl = imageLink;
    }

    public String getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(String clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<String> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "clientGroupId='" + clientGroupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", groupMemberList=" + groupMemberList +
                ", imageUrl='" + imageUrl + '\'' +
                ", type=" + type +
                '}';
    }
}
