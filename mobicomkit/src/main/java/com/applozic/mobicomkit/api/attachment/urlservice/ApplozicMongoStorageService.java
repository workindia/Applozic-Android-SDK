package com.applozic.mobicomkit.api.attachment.urlservice;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.attachment.FileClientService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicommons.commons.core.utils.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ApplozicMongoStorageService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private Context context;

    private static final String UPLOAD_URL ="/files/v2/upload";
    private static final String DOWNLOAD_URL ="/files/get/";


    public ApplozicMongoStorageService(Context context) {

        mobiComKitClientService = new MobiComKitClientService(context);
        this.context = context;
    }

    @Override
    public HttpURLConnection getAttachmentConnection(Message message) throws IOException {

        return mobiComKitClientService.openHttpConnection( mobiComKitClientService.getFileBaseUrl()
                + DOWNLOAD_URL
                + message.getFileMetas().getBlobKeyString());

    }

    @Override
    public String getThumbnailURL(Message message) throws IOException {
        return message.getFileMetas().getThumbnailUrl();
    }

    @Override
    public String getFileUploadUrl() {
        return mobiComKitClientService.getFileBaseUrl() + UPLOAD_URL;
    }

    @Override
    public String getImageUrl(Message message) {
        return message.getFileMetas().getBlobKeyString();
    }
}
