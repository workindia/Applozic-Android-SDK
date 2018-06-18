package com.applozic.mobicomkit.api.attachment.urlservice;

import android.content.Context;

import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public class URLServiceProvider {

    private Context context;
    private static URLService urlService;

    public URLServiceProvider(Context context) {
        this.context = context;
    }

    private static URLService getUrlService(Context context) {

        if (urlService != null) {
            return urlService;
        }

        ApplozicClient appClient = ApplozicClient.getInstance(context);

        if (appClient.isS3StorageServiceEnabled()) {
            urlService = new S3URLService(context);
        } else if (appClient.isGoogleCloudServiceEnabled()) {
            urlService = new GoogleCloudURLService(context);
        } else if (appClient.isStorageServiceEnabled()) {
            urlService = new ApplozicMongoStorageService(context);
        } else {
            urlService = new DefaultURLService(context);
        }

        return urlService;
    }


    public HttpURLConnection getDownloadConnection(Message message) throws IOException {
        HttpURLConnection connection;

        try {
            connection = getUrlService(context).getAttachmentConnection(message);
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
        return connection;
    }

    public String getThumbnailURL(Message message) throws IOException {
        try {
            return getUrlService(context).getThumbnailURL(message);
        } catch (Exception e) {
            throw new IOException("Error connecting");
        }
    }

    public String getFileUploadUrl() {
        return getUrlService(context).getFileUploadUrl();
    }

    public String getImageURL(Message message) {
        return getUrlService(context).getImageUrl(message);
    }

}