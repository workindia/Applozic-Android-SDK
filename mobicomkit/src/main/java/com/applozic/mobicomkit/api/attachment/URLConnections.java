package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.HttpRequestUtils;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;

public class URLConnections {

    private HttpRequestUtils httpRequestUtils;
    private Context context;

    URLConnections(Context context) {
        this.httpRequestUtils = new HttpRequestUtils(context);
        this.context = context;
    }

    public HttpURLConnection getDownloadConnection(Message message) throws IOException {
        HttpURLConnection connection;
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

    public String getThumbnailURL(Message message) throws IOException {
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

    public String getUploadKey() {
        if (ApplozicClient.getInstance(context).isStorageServiceEnabled() || ApplozicClient.getInstance(context).isCustomStorageServiceEnabled() || ApplozicClient.getInstance(context).isS3SignedURLsEnabled()) {
            return getFileUploadUrl();
        } else {
            return httpRequestUtils.getResponse(getFileUploadUrl()
                    + "?" + new Date().getTime(), "text/plain", "text/plain", true);
        }
    }

    private String getFileUploadUrl() {
        if (ApplozicClient.getInstance(context).isCustomStorageServiceEnabled()) {
            return new GoogleCloudURLService(context).getFileUploadUrl();
        }
        if (ApplozicClient.getInstance(context).isS3SignedURLsEnabled()) {
            return new S3URLService(context).getFileUploadUrl();
        }
        return new DefaultURLService(context).getFileUploadUrl();
    }
}