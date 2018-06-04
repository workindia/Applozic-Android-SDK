package com.applozic.mobicomkit.api.attachment;

import android.content.Context;
import android.os.Handler;

import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface URLService {
    HttpURLConnection getAttachmentConnection(Context context, Message message) throws IOException;

    String getThumbnailURL(Context context, Message message) throws IOException;

    ApplozicMultipartUtility getMultipartFile(String path, Handler handler);

    String getFileUploadUrl();
}
