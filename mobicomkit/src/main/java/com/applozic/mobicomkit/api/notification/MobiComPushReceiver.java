package com.applozic.mobicomkit.api.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.applozic.mobicomkit.api.account.user.MobiComUserPreference;
import com.applozic.mobicomkit.api.conversation.MessageClientService;
import com.applozic.mobicomkit.api.conversation.MobiComConversationService;
import com.applozic.mobicomkit.api.conversation.SyncCallService;
import com.applozic.mobicomkit.api.people.ContactContent;
import com.applozic.mobicomkit.broadcast.BroadcastService;
import com.applozic.mobicomkit.contact.ContactService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MobiComPushReceiver {

    public static final String MTCOM_PREFIX = "MT_";
    public static final List<String> notificationKeyList = new ArrayList<String>();
    private static final String TAG = "MobiComPushReceiver";

    static {
        notificationKeyList.add("MT_SYNC"); // 0
        notificationKeyList.add("MT_MARK_ALL_MESSAGE_AS_READ"); //1
        notificationKeyList.add("MT_DELIVERED"); //2
        notificationKeyList.add("MT_SYNC_PENDING"); //3
        notificationKeyList.add("MT_DELETE_MESSAGE"); //4
        notificationKeyList.add("MT_DELETE_MULTIPLE_MESSAGE"); //5
        notificationKeyList.add("MT_DELETE_MESSAGE_CONTACT");// 6
        notificationKeyList.add("MTEXTER_USER");//7
        notificationKeyList.add("MT_CONTACT_VERIFIED"); //8
        notificationKeyList.add("MT_CONTACT_UPDATED"); //9
        notificationKeyList.add("MT_DEVICE_CONTACT_SYNC");//10
        notificationKeyList.add("MT_EMAIL_VERIFIED");//11
        notificationKeyList.add("MT_DEVICE_CONTACT_MESSAGE");//12
        notificationKeyList.add("MT_CANCEL_CALL");//13
        notificationKeyList.add("MT_MESSAGE");//14
        notificationKeyList.add("MT_USER_CONNECTED");//15
        notificationKeyList.add("MT_USER_DISCONNECTED");//16
        notificationKeyList.add("MT_MESSAGE_DELIVERED_READ"); //17 MESSAGE_DELIVERED_READ
        notificationKeyList.add("MT_CONVERSATION_READ"); //18
    }

    public static boolean isMobiComPushNotification(Intent intent) {
        Log.i(TAG, "checking for Applozic notification.");
        return isMobiComPushNotification(intent.getExtras());
    }

    public static boolean isMobiComPushNotification(Bundle bundle) {
        //This is to identify collapse key sent in notification..
        String payLoad = bundle.getString("collapse_key");
        Log.i(TAG, "Received notification: " + payLoad);

        if (payLoad != null && payLoad.contains(MTCOM_PREFIX) || notificationKeyList.contains(payLoad)) {
            return true;
        } else {
            for (String key : notificationKeyList) {
                payLoad = bundle.getString(key);
                if (payLoad != null) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void processMessage(Context context, Bundle bundle) {
        // Bundle extras = intent.getExtras();
        if (bundle != null) {
            // ToDo: do something for invalidkey ;
            // && extras.get("InvalidKey") != null
            String message = bundle.getString("collapse_key");
            String deleteConversationForContact = bundle.getString(notificationKeyList.get(6));
            String deleteSms = bundle.getString(notificationKeyList.get(4));
            String multipleMessageDelete = bundle.getString(notificationKeyList.get(5));
            String mtexterUser = bundle.getString(notificationKeyList.get(7));
            String payloadForDelivered = bundle.getString(notificationKeyList.get(2));
            if (TextUtils.isEmpty(payloadForDelivered)) {
                payloadForDelivered = bundle.getString(notificationKeyList.get(17));
            }
            String userConnected = bundle.getString(notificationKeyList.get(15));
            String userDisconnected = bundle.getString(notificationKeyList.get(16));
            processMessage(context, bundle, message, deleteConversationForContact, deleteSms, multipleMessageDelete, mtexterUser, payloadForDelivered, userConnected, userDisconnected);
        }
    }

    public static void processMessage(final Context context, Bundle bundle, String message, String deleteConversationForContact, String deleteSms, String multipleMessageDelete, String mtexterUser, String payloadForDelivered, String userConnected, String userDisconnected) {
        SyncCallService syncCallService = SyncCallService.getInstance(context);
        final MessageClientService messageClientService = new MessageClientService(context);

        if (!TextUtils.isEmpty(payloadForDelivered)) {
            syncCallService.updateDeliveryStatus(payloadForDelivered);
        }
        if (!TextUtils.isEmpty(deleteConversationForContact)) {
            MobiComConversationService conversationService = new MobiComConversationService(context);
            conversationService.deleteConversationFromDevice(deleteConversationForContact);
            BroadcastService.sendConversationDeleteBroadcast(context, BroadcastService.INTENT_ACTIONS.DELETE_CONVERSATION.toString(), deleteConversationForContact, "success");
        }

        if (!TextUtils.isEmpty(mtexterUser)) {
            Log.i(TAG, "Received GCM message MTEXTER_USER: " + mtexterUser);
            if (mtexterUser.contains("{")) {
                Gson gson = new Gson();
                ContactContent contactContent = gson.fromJson(mtexterUser, ContactContent.class);
                ContactService.addUsersToContact(context, contactContent.getContactNumber(), contactContent.getAppVersion(), true);
            } else {
                String[] details = mtexterUser.split(",");
                ContactService.addUsersToContact(context, details[0], Short.parseShort(details[1]), true);
            }
        }
        if (!TextUtils.isEmpty(userConnected)) {
            syncCallService.updateConnectedStatus(userConnected, new Date(), true);
            /*final String userId = userConnected;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserDetail[] userDetail = messageClientService.getUserDetails(userId);
                    if (userDetail != null) {
                        for (UserDetail userDetails : userDetail) {
                            if (userDetails != null && userDetails.isConnected()) {
                                new ContactDatabase(context).updateConnectedOrDisconnectedStatus(userId, userDetails.isConnected());
                            }
                        }
                    }
                }
            }).start();*/
        }

        if (!TextUtils.isEmpty(userDisconnected)) {
            String[] parts = userDisconnected.split(",");
            String userId = parts[0];
            Date lastSeenAt = new Date();
            if (parts.length >= 2) {
                lastSeenAt = new Date(Long.valueOf(parts[1]));
            }
            syncCallService.updateConnectedStatus(userId, lastSeenAt, false);
            /*final String userId = userDisconnected;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserDetail[] userDetail = messageClientService.getUserDetails(userId);
                    if (userDetail != null) {
                        for (UserDetail userDetails : userDetail) {
                            if (userDetails != null && userDetails.getLastSeenAtTime() != null) {
                                ContactDatabase contactDatabase = new ContactDatabase(context);
                                contactDatabase.updateConnectedOrDisconnectedStatus(userId, userDetails.isConnected());
                                contactDatabase.updateLastSeenTimeAt(userId, userDetails.getLastSeenAtTime());
                            }
                        }
                    }
                }
            }).start();*/
        }

        if (!TextUtils.isEmpty(multipleMessageDelete)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            MessageDeleteContent messageDeleteContent = gson.fromJson(multipleMessageDelete, MessageDeleteContent.class);

            for (String deletedSmsKeyString : messageDeleteContent.getDeleteKeyStrings()) {
                processDeleteSingleMessageRequest(context, deletedSmsKeyString, messageDeleteContent.getContactNumber());
            }
        }

        if (!TextUtils.isEmpty(deleteSms)) {
            String contactNumbers = deleteSms.split(",").length > 1 ? deleteSms.split(",")[1] : null;
            processDeleteSingleMessageRequest(context, deleteSms.split(",")[0], contactNumbers);
        }

        String messageKey = bundle.getString(notificationKeyList.get(0));
        if (notificationKeyList.get(1).equalsIgnoreCase(message)) {

        } else if (!TextUtils.isEmpty(messageKey)) {
            Log.i(TAG, "MT sync for key: " + messageKey);
            syncCallService.syncMessages(messageKey);
        } else if (notificationKeyList.get(0).equalsIgnoreCase(message)) {
            syncCallService.syncMessages(null);
        } else if (notificationKeyList.get(3).equalsIgnoreCase(message)) {
            //  MessageStatUtil.sendMessageStatsToServer(context);
        } else if (notificationKeyList.get(18).equals(message)) {
            String contactId = bundle.getString(notificationKeyList.get(18));
            Log.i(TAG, "Got conversation read for contactId: " + contactId);
            syncCallService.updateDeliveryStatusForContact(contactId);
        }
    }

    private static void processDeleteSingleMessageRequest(Context context, String deletedSmsKeyString, String contactNumber) {
        MobiComConversationService conversationService = new MobiComConversationService(context);
        contactNumber = conversationService.deleteMessageFromDevice(deletedSmsKeyString, contactNumber);
        BroadcastService.sendMessageDeleteBroadcast(context, BroadcastService.INTENT_ACTIONS.DELETE_MESSAGE.toString(), deletedSmsKeyString, contactNumber);
    }

    public static void processMessageAsync(final Context context, final Bundle bundle) {
        if (MobiComUserPreference.getInstance(context).isLoggedIn()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    processMessage(context, bundle);
                }
            }).start();
        }
    }

    public static void processMessageAsync(final Context context, final Intent intent) {
        processMessageAsync(context, intent.getExtras());
    }

}
