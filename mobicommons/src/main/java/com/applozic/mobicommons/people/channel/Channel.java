package com.applozic.mobicommons.people.channel;

import com.applozic.mobicommons.people.contact.Contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by devashish on 5/9/14.
 */
public class Channel implements Serializable {

    private Integer key;
    private String name;
    private String adminKey;
    private Short type;
    private int userCount;
    private Conversation conversationPxy;
    private List<Contact> contacts = new ArrayList<Contact>();

    public Channel() {

    }

    public Channel(Integer key, String name, String adminKey, Short type) {
        this.key = key;
        this.name = name;
        this.adminKey = adminKey;
        this.type = type;
    }

    public Channel(Integer key) {
        this.key = key;
    }

    public Channel(Integer key, String name) {
        this.key = key;
        this.name = name;

    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminKey() {
        return adminKey;
    }

    public void setAdminKey(String adminKey) {
        this.adminKey = adminKey;
    }

    public Short getType() {
        return type;
    }

    public void setType(Short type) {
        this.type = type;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public List<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contact> contacts) {
        this.contacts = contacts;
    }

    public Conversation getConversationPxy() {
        return conversationPxy;
    }

    public void setConversationPxy(Conversation conversationPxy) {
        this.conversationPxy = conversationPxy;
    }

    @Override
    public String toString() {
        return "Channel{" +
                ", key=" + key +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", userCount=" + userCount +
                ", contacts=" + contacts +
                '}';
    }

    public enum GroupType {

        VIRTUAL(0),
        PRIVATE(1),
        PUBLIC(2),
        SELLER(3),
        SELF(4);

        private Integer value;

        GroupType(Integer value) {
            this.value = value;
        }

        public Short getValue() {
            return value.shortValue();
        }
    }

}
