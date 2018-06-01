package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicommons.commons.core.utils.Utils;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.applozic.mobicomkit.api.attachment.FileClientService.FILE_UPLOAD_URL;

public class DefaultURLService extends MobiComKitClientService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private Context context;

    public DefaultURLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
        this.context = context;
    }

    @Override
    public HttpURLConnection getAttachmentConnection(Context context, Message message) throws IOException {
        return mobiComKitClientService.openHttpConnection(new MobiComKitClientService(context).getFileUrl() + message.getFileMetas().getBlobKeyString());
    }

    @Override
    public String getThumbnailURL(Context context, Message message) throws IOException {
        return message.getFileMetas().getThumbnailUrl();
    }

    @Override
    public String getFileUploadUrl() {
        String fileUploadUrl = Utils.getMetaDataValue(context.getApplicationContext(), FILE_UPLOAD_METADATA_KEY);
        if (!TextUtils.isEmpty(fileUploadUrl)) {
            return getFileBaseUrl() + fileUploadUrl;
        }
        return getFileBaseUrl() + FILE_UPLOAD_URL;
    }

}
