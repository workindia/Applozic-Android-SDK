package com.applozic.mobicommons.people.channel;


import java.io.Serializable;
import java.util.List;

/**
 * Created by sunil on 5/1/16.
 */
public class Conversation implements Serializable {

    private Integer id;
    private String topicId;
    private String topicDetail;
    private String userId;
    private List<String> supportIds;
    private boolean created;
    private String senderUserName;
    private String applicationKey;
    private Integer groupId;

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getSupportIds() {
        return supportIds;
    }

    public void setSupportIds(List<String> supportIds) {
        this.supportIds = supportIds;
    }

    public boolean isCreated() {
        return created;
    }

    public void setCreated(boolean created) {
        this.created = created;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getApplicationKey() {
        return applicationKey;
    }

    public void setApplicationKey(String applicationKey) {
        this.applicationKey = applicationKey;
    }

    public String getTopicDetail() {
        return topicDetail;
    }

    public void setTopicDetail(String topicDetail) {
        this.topicDetail = topicDetail;
    }

    @Override
    public String toString() {
        return "Conversation{" +
                "id=" + id +
                ", topicId='" + topicId + '\'' +
                ", userId='" + userId + '\'' +
                ", supportIds=" + supportIds +
                ", created=" + created +
                ", senderUserName='" + senderUserName + '\'' +
                ", applicationKey='" + applicationKey + '\'' +
                ", groupId=" + groupId +
                '}';
    }
}
