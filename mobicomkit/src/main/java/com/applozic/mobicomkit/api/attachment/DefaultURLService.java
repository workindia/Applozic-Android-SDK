package com.applozic.mobicomkit.api.attachment;

import android.content.Context;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public class DefaultURLService implements URLService {

    private MobiComKitClientService mobiComKitClientService;

    public DefaultURLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
    }

    @Override
    public HttpURLConnection getAttachmentConnection(Context context, Message message) throws IOException {
        return mobiComKitClientService.openHttpConnection(new MobiComKitClientService(context).getFileUrl() + message.getFileMetas().getBlobKeyString());
    }

    @Override
    public String getThumbnailURL(Context context, Message message) throws IOException {
        return message.getFileMetas().getThumbnailUrl();
    }
}
