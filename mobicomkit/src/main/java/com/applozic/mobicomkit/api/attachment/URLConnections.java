package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitClientService;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public class URLConnections {

    private HttpURLConnection connection;

    public HttpURLConnection getDownloadConnection(Context context, Message message) throws IOException {
        try {
            if (ApplozicClient.getInstance(context).isCustomStorageServiceEnabled() && !TextUtils.isEmpty(message.getFileMetas().getUrl())) {
                connection = new GoogleCloudURLService(context).getAttachmentURL(context, message);
            } else if (ApplozicClient.getInstance(context).isS3SignedURLsEnabled() && !TextUtils.isEmpty(message.getFileMetas().getBlobKeyString())) {
                connection = new S3URLService(context).getAttachmentURL(context, message);
            } else {
                connection = new DefaultURLService(context).getAttachmentURL(context, message);
            }
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
        return connection;
    }

}