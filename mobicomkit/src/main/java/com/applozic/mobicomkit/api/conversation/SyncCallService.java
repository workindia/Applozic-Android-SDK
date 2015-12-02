package com.applozic.mobicomkit.api.conversation;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by applozic on 12/2/15.
 */
public class SyncCallService {

    private static final String TAG = "SyncCall";

    private static SyncCallService syncCallService;
    private MobiComMessageService mobiComMessageService;

    private SyncCallService(Context context) {
        this.mobiComMessageService = new MobiComMessageService(context, MessageIntentService.class);
    }

    public synchronized static SyncCallService getInstance(Context context) {
        if (syncCallService == null) {
            syncCallService = new SyncCallService(context);
        }
        return syncCallService;
    }


    public synchronized void updateDeliveryStatus(String key) {
        mobiComMessageService.updateDeliveryStatus(key);
    }

    public synchronized void syncMessages(String key) {
        if (!TextUtils.isEmpty(key) && mobiComMessageService.isMessagePresent(key)) {
            Log.d(TAG, "Message is already present, MQTT reached before GCM.");
        } else {
            mobiComMessageService.syncMessages();
        }
    }
}
