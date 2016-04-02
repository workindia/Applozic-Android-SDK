package com.applozic.mobicomkit.api.conversation;

import com.applozic.mobicomkit.api.JsonMarker;
import com.applozic.mobicomkit.feed.ApiResponse;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by devashish on 28/03/16.
 */
public class MessageInfoResponse extends ApiResponse {

    @SerializedName("response")
    List<MessageInfo> messageInfoList;

    public List<MessageInfo> getMessageInfoList() {
        return messageInfoList;
    }

    public void setMessageInfoList(List<MessageInfo> messageInfoList) {
        this.messageInfoList = messageInfoList;
    }
}
