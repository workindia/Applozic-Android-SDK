package com.applozic.mobicomkit.feed;

import java.util.List;

/**
 * Created by sunil on 28/12/15.
 */
public class ChannelInfo {

    private String groupName;
    private List<String> members;

    public ChannelInfo() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    @Override
    public String toString() {
        return "ChannelInfo{" +
                "groupName='" + groupName + '\'' +
                ", members=" + members +
                '}';
    }
}
