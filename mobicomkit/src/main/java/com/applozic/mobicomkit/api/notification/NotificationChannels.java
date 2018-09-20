package com.applozic.mobicomkit.api.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.applozic.mobicomkit.Applozic;
import com.applozic.mobicomkit.ApplozicClient;
import com.applozic.mobicomkit.api.MobiComKitConstants;
import com.applozic.mobicommons.commons.core.utils.Utils;

/**
 * Please remember to increment the NOTIFICATION_CHANNEL_VERSION if any change is made in this class.
 * It is mandatory to increment the version or the update in the Notification channels will fail.
 */

public class NotificationChannels {

    //increment this version if changes in notification channel is made
    public static int NOTIFICATION_CHANNEL_VERSION = 1;

    private Context context;
    private NotificationManager mNotificationManager;
    private String TAG = getClass().getSimpleName();

    public NotificationChannels(Context context) {
        this.context = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void prepareNotificationChannels() {
        if (Applozic.getInstance(context).getNotificationChannelVersion() < NOTIFICATION_CHANNEL_VERSION) {
            if (isNotificationChannelCreated()) {
                deleteNotificationChannel();
            }
            if (isSilentChannelCreated()) {
                deleteSilentNotificationChannel();
            }
            if (isAppChannelCreated()) {
                Applozic.getInstance(context).setCustomNotificationSound(null);
                deleteAppNotificationChannel();
            }
            if (TextUtils.isEmpty(Applozic.getInstance(context).getCustomNotificationSound())) {
                createNotificationChannel();
            } else {
                createAppNotificationChannel();
            }
            createSilentNotificationChannel();

            Applozic.getInstance(context).setNotificationChannelVersion(NOTIFICATION_CHANNEL_VERSION);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void deleteAllChannels() {
        if (isSilentChannelCreated()) {
            deleteSilentNotificationChannel();
        }

        if (isNotificationChannelCreated()) {
            deleteNotificationChannel();
        }

        if (isAppChannelCreated()) {
            deleteAppNotificationChannel();
        }
    }

    public String getDefaultChannelId(boolean mute) {
        if (mute) {
            return MobiComKitConstants.AL_SILENT_NOTIFICATION;
        } else {
            if (TextUtils.isEmpty(Applozic.getInstance(context).getCustomNotificationSound())) {
                return MobiComKitConstants.AL_PUSH_NOTIFICATION;
            }
        }
        return MobiComKitConstants.AL_APP_NOTIFICATION;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void createNotificationChannel() {
        CharSequence name = MobiComKitConstants.PUSH_NOTIFICATION_NAME;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(MobiComKitConstants.AL_PUSH_NOTIFICATION) == null) {
            NotificationChannel mChannel = new NotificationChannel(MobiComKitConstants.AL_PUSH_NOTIFICATION, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.setShowBadge(ApplozicClient.getInstance(context).isUnreadCountBadgeEnabled());

            if (ApplozicClient.getInstance(context).getVibrationOnNotification()) {
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            }

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();
            mChannel.setSound(TextUtils.isEmpty(Applozic.getInstance(context).getCustomNotificationSound()) ? RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION) : Uri.parse(Applozic.getInstance(context).getCustomNotificationSound()), audioAttributes);
            Utils.printLog(context, TAG, mChannel.getSound().getPath());
            mNotificationManager.createNotificationChannel(mChannel);
            Utils.printLog(context, TAG, "Created notification channel");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void createAppNotificationChannel() {
        CharSequence name = MobiComKitConstants.APP_NOTIFICATION_NAME;
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(MobiComKitConstants.AL_APP_NOTIFICATION) == null) {
            NotificationChannel mChannel = new NotificationChannel(MobiComKitConstants.AL_APP_NOTIFICATION, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.setShowBadge(ApplozicClient.getInstance(context).isUnreadCountBadgeEnabled());

            if (ApplozicClient.getInstance(context).getVibrationOnNotification()) {
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            }

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION).build();

            mChannel.setSound(Uri.parse(Applozic.getInstance(context).getCustomNotificationSound()), audioAttributes);
            Utils.printLog(context, TAG, mChannel.getSound().getPath());
            mNotificationManager.createNotificationChannel(mChannel);
            Utils.printLog(context, TAG, "Created app notification channel");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void createSilentNotificationChannel() {
        CharSequence name = MobiComKitConstants.SILENT_PUSH_NOTIFICATION;
        int importance = NotificationManager.IMPORTANCE_LOW;
        if (mNotificationManager != null && mNotificationManager.getNotificationChannel(MobiComKitConstants.AL_SILENT_NOTIFICATION) == null) {
            NotificationChannel mChannel = new NotificationChannel(MobiComKitConstants.AL_SILENT_NOTIFICATION, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            if (ApplozicClient.getInstance(context).isUnreadCountBadgeEnabled()) {
                mChannel.setShowBadge(true);
            } else {
                mChannel.setShowBadge(false);
            }

            mNotificationManager.createNotificationChannel(mChannel);
            Utils.printLog(context, TAG, "Created silent notification channel");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void deleteNotificationChannel() {
        if (mNotificationManager != null) {
            mNotificationManager.deleteNotificationChannel(MobiComKitConstants.AL_PUSH_NOTIFICATION);
            Utils.printLog(context, TAG, "Deleted notification channel");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void deleteSilentNotificationChannel() {
        if (mNotificationManager != null) {
            mNotificationManager.deleteNotificationChannel(MobiComKitConstants.AL_SILENT_NOTIFICATION);
            Utils.printLog(context, TAG, "Deleted silent notification channel");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private synchronized void deleteAppNotificationChannel() {
        if (mNotificationManager != null) {
            mNotificationManager.deleteNotificationChannel(MobiComKitConstants.AL_APP_NOTIFICATION);
            Utils.printLog(context, TAG, "Deleted app notification channel");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isNotificationChannelCreated() {
        return mNotificationManager != null && mNotificationManager.getNotificationChannel(MobiComKitConstants.AL_PUSH_NOTIFICATION) != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isAppChannelCreated() {
        return mNotificationManager != null && mNotificationManager.getNotificationChannel(MobiComKitConstants.AL_APP_NOTIFICATION) != null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isSilentChannelCreated() {
        return mNotificationManager != null && mNotificationManager.getNotificationChannel(MobiComKitConstants.AL_SILENT_NOTIFICATION) != null;
    }
}
