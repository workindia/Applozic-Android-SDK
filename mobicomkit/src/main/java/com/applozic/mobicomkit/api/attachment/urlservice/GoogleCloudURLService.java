package com.applozic.mobicomkit.api.attachment.urlservice;

import android.content.Context;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.applozic.mobicomkit.api.attachment.FileClientService.CUSTOM_STORAGE_SERVICE_END_POINT;

public class GoogleCloudURLService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private HttpRequestUtils httpRequestUtils;
    private static final String GET_SIGNED_URL = "/files/url?key=";


    public GoogleCloudURLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
        httpRequestUtils = new HttpRequestUtils(context);
    }


    @Override
    public HttpURLConnection getAttachmentConnection(Message message) throws IOException {
        return mobiComKitClientService.openHttpConnection(message.getFileMetas().getUrl());
    }

    @Override
    public String getThumbnailURL(Message message) throws IOException {
        return httpRequestUtils.getResponse(mobiComKitClientService.getBaseUrl() + GET_SIGNED_URL + message.getFileMetas().getThumbnailBlobKey(), "application/json", "application/json");
    }


    @Override
    public String getFileUploadUrl() {
        return mobiComKitClientService.getBaseUrl() + CUSTOM_STORAGE_SERVICE_END_POINT;
    }

    @Override
    public String getImageUrl(Message message) {
        return message.getFileMetas().getUrl();
    }
}
