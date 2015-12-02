package com.applozic.mobicomkit.api.conversation;

import android.content.Context;

/**
 * Created by applozic on 12/2/15.
 */
public class SyncCallService {

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


    public synchronized void syncMessages() {
        mobiComMessageService.syncMessages();
    }

    public synchronized void updateDeliveryStatus(String key) {
        mobiComMessageService.updateDeliveryStatus(key);
    }
}
