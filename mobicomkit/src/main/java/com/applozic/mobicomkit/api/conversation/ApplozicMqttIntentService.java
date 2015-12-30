package com.applozic.mobicomkit.api.conversation;

import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.ApplozicMqttService;
import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicommons.people.contact.Contact;

/**
 * Created by sunil on 30/12/15.
 */
public class ApplozicMqttIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final String TAG = "ApplozicMqttIntentService";

    public ApplozicMqttIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String subscribe = intent.getStringExtra("subscribe");
        if (!TextUtils.isEmpty(subscribe)) {
            ApplozicMqttService.getInstance(getApplicationContext()).subscribe();
        }
        String userKeyString = intent.getStringExtra("userKeyString");
        if (!TextUtils.isEmpty(userKeyString)) {
            ApplozicMqttService.getInstance(getApplicationContext()).disconnectPublish(userKeyString, "0");
        }

        boolean connectedStatus = intent.getBooleanExtra("connectedPublish", false);
        if (connectedStatus) {
            ApplozicMqttService.getInstance(getApplicationContext()).connectPublish(MobiComUserPreference.getInstance(getApplicationContext()).getSuUserKeyString(), "1");
        }
        Contact contact = (Contact) intent.getSerializableExtra("contact");
        if (contact != null) {
            boolean typing = intent.getBooleanExtra("typing", false);
            if (typing) {
                ApplozicMqttService.getInstance(getApplicationContext()).typingStarted(contact);
            } else {
                ApplozicMqttService.getInstance(getApplicationContext()).typingStopped(contact);
            }
        }
    }
}
