package com.applozic.mobicomkit.api.attachment;

import android.content.Context;

import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public class GoogleCloudURLService implements URLService {

    private MobiComKitClientService mobiComKitClientService;

    public GoogleCloudURLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
    }


    @Override
    public HttpURLConnection getAttachmentURL(Context context, Message message) throws IOException {
        return mobiComKitClientService.openHttpConnection(message.getFileMetas().getUrl());
    }
}
