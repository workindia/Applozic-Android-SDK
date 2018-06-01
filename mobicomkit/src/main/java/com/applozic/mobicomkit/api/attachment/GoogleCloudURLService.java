package com.applozic.mobicomkit.api.attachment;

import android.content.Context;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.applozic.mobicomkit.api.attachment.FileClientService.CUSTOM_STORAGE_SERVICE_END_POINT;

public class GoogleCloudURLService extends MobiComKitClientService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private HttpRequestUtils httpRequestUtils;

    public GoogleCloudURLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
        httpRequestUtils = new HttpRequestUtils(context);
    }


    @Override
    public HttpURLConnection getAttachmentConnection(Context context, Message message) throws IOException {
        return mobiComKitClientService.openHttpConnection(message.getFileMetas().getUrl());
    }

    @Override
    public String getThumbnailURL(Context context, Message message) throws IOException {
        return httpRequestUtils.getResponse(mobiComKitClientService.getFileAuthBaseUrl(message.getFileMetas().getThumbnailBlobKey()), "application/json", "application/json");
    }

    @Override
    public String getFileUploadUrl() {
        return getBaseUrl() + CUSTOM_STORAGE_SERVICE_END_POINT;
    }
}
