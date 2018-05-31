package com.applozic.mobicomkit.api.attachment;

import android.content.Context;

import com.applozic.mobicomkit.api.conversation.Message;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface URLService {
    HttpURLConnection getAttachmentURL(Context context, Message message) throws IOException;
}
