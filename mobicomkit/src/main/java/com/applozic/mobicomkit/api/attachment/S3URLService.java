package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

import static com.applozic.mobicomkit.api.attachment.FileClientService.S3_SIGNED_URL_END_POINT;

public class S3URLService extends MobiComKitClientService implements URLService {

    private MobiComKitClientService mobiComKitClientService;
    private HttpRequestUtils httpRequestUtils;

    S3URLService(Context context) {
        mobiComKitClientService = new MobiComKitClientService(context);
        httpRequestUtils = new HttpRequestUtils(context);
    }

    @Override
    public HttpURLConnection getAttachmentConnection(Context context, Message message) throws IOException {
        String response = httpRequestUtils.getResponse(mobiComKitClientService.getFileAuthBaseUrl(message.getFileMetas().getBlobKeyString()), "application/json", "application/json");
        if (TextUtils.isEmpty(response)) {
            return null;
        } else {
            return mobiComKitClientService.openHttpConnection(response);
        }
    }

    @Override
    public String getThumbnailURL(Context context, Message message) throws IOException {
        return httpRequestUtils.getResponse(mobiComKitClientService.getFileAuthBaseUrl(message.getFileMetas().getThumbnailBlobKey()), "application/json", "application/json");
    }

    @Override
    public String getFileUploadUrl() {
        return getBaseUrl() + S3_SIGNED_URL_END_POINT;
    }
}
