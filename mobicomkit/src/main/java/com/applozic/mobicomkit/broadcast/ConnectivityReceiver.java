package com.applozic.mobicomkit.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.MessageClientService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicommons.commons.core.utils.Utils;

/**
 * Created by devashish on 29/08/15.
 */
public class ConnectivityReceiver extends BroadcastReceiver {

    static final private String TAG = "ConnectivityReceiver";
    static final private String CONNECTIVITY_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";
    Context context;

    @Override
    public void onReceive(final Context context, Intent intent) {
        this.context = context;

        String action = intent.getAction();

        Log.i(TAG, action);

        if (action.equalsIgnoreCase(CONNECTIVITY_CHANGE)) {
            if (!Utils.isInternetAvailable(context)) {
                return;
            }
            if (!MobiComUserPreference.getInstance(context).isLoggedIn()) {
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SyncCallService.getInstance(context).syncMessages(null);
                    MessageClientService.syncPendingMessages(context);
                    MessageClientService.syncDeleteMessages(context);
                }
            }).start();
        }
    }

}

