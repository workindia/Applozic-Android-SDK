package com.applozic.mobicomkit.feed;

/**
 * Created by sunil on 28/11/15.
 */
public class MqttMessageResponse {

    private String type;
    private String message;
    private boolean notifyUser;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isNotifyUser() {
        return notifyUser;
    }

    public void setNotifyUser(boolean notifyUser) {
        this.notifyUser = notifyUser;
    }

    @Override
    public String toString() {
        return "MqttMessageResponse{" +
                "type='" + type + '\'' +
                ", message='" + message + '\'' +
                ", notifyUser=" + notifyUser +
                '}';
    }

}
