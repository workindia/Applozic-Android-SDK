package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public class URLConnections {

    private HttpURLConnection connection;

    public HttpURLConnection getDownloadConnection(Context context, Message message) throws IOException {
        try {
            if (ApplozicClient.getInstance(context).isCustomStorageServiceEnabled() && !TextUtils.isEmpty(message.getFileMetas().getUrl())) {
                connection = new GoogleCloudURLService(context).getAttachmentConnection(context, message);
            } else if (ApplozicClient.getInstance(context).isS3SignedURLsEnabled() && !TextUtils.isEmpty(message.getFileMetas().getBlobKeyString())) {
                connection = new S3URLService(context).getAttachmentConnection(context, message);
            } else {
                connection = new DefaultURLService(context).getAttachmentConnection(context, message);
            }
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
        return connection;
    }

    public String getThumbnailURL(Context context, Message message) throws IOException {
        try {
            if (ApplozicClient.getInstance(context).isCustomStorageServiceEnabled() && !TextUtils.isEmpty(message.getFileMetas().getUrl())) {
                return new GoogleCloudURLService(context).getThumbnailURL(context, message);
            } else if (ApplozicClient.getInstance(context).isS3SignedURLsEnabled() && !TextUtils.isEmpty(message.getFileMetas().getBlobKeyString())) {
                return new S3URLService(context).getThumbnailURL(context, message);
            } else {
                return new DefaultURLService(context).getThumbnailURL(context, message);
            }
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
    }

}