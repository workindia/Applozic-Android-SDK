package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public class S3URLService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private HttpRequestUtils httpRequestUtils;

    public S3URLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
        httpRequestUtils = new HttpRequestUtils(context);
    }

    @Override
    public HttpURLConnection getAttachmentURL(Context context, Message message) throws IOException {
        String response = httpRequestUtils.getResponse(mobiComKitClientService.getFileAuthBaseUrl(message.getFileMetas().getBlobKeyString()), "application/json", "application/json");
        if (TextUtils.isEmpty(response)) {
            return null;
        } else {
            return mobiComKitClientService.openHttpConnection(response);
        }
    }
}
