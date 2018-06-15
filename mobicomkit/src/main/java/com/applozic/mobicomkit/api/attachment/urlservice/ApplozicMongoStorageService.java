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

    public ApplozicMongoStorageService(Context context) {

        mobiComKitClientService = new MobiComKitClientService(context);
        this.context = context;
    }

    @Override
    public HttpURLConnection getAttachmentConnection(Message message) throws IOException {

        return mobiComKitClientService.openHttpConnection(new MobiComKitClientService(context).getFileUrl() + message.getFileMetas().getBlobKeyString());
    }

    @Override
    public String getThumbnailURL(Message message) throws IOException {
        return mobiComKitClientService.getFileBaseUrl() +
                FileClientService.THUMBNAIL_URL + message.getFileMetas().getThumbnailUrl();
    }

    @Override
    public String getFileUploadUrl() {

        String fileUploadUrl = Utils.getMetaDataValue(context.getApplicationContext(), MobiComKitClientService.FILE_UPLOAD_METADATA_KEY);

        if (!TextUtils.isEmpty(fileUploadUrl)) {
            return mobiComKitClientService.getFileBaseUrl() + fileUploadUrl;
        }
        return null;
    }

    @Override
    public String getImageUrl(Message message) {
        return message.getFileMetas().getBlobKeyString();
    }
}
