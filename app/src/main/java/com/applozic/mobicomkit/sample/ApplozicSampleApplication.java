package com.applozic.mobicomkit.sample;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.applozic.mobicomkit.api.conversation.Message;
import com.applozic.mobicomkit.exception.ApplozicException;
import com.applozic.mobicomkit.listners.MediaUploadProgressHandler;
import com.applozic.mobicomkit.uiwidgets.conversation.richmessaging.ALRichMessageListener;
import com.applozic.mobicomkit.uiwidgets.uilistener.ALProfileClickListener;
import com.applozic.mobicommons.commons.core.utils.Utils;
import com.applozic.mobicommons.people.ALContactProcessor;
import com.applozic.mobicommons.people.channel.Channel;
import com.applozic.mobicommons.people.contact.Contact;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by sunil on 21/3/16.
 */
public class ApplozicSampleApplication extends MultiDexApplication implements MediaUploadProgressHandler, ALProfileClickListener, ALRichMessageListener {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onUploadStarted(ApplozicException e) {

    }

    @Override
    public void onProgressUpdate(int percentage, ApplozicException e) {

    }

    @Override
    public void onCancelled(ApplozicException e) {

    }

    @Override
    public void onCompleted(ApplozicException e) {

    }

    @Override
    public void onSent(Message message) {
        Utils.printLog(this, "UiTest", message.toString());
    }

    @Override
    public void onClick(Context context, Contact contact, Channel channel, boolean isToolbar) {
        Utils.printLog(context, "CallTest", "Contact : " + (contact != null ? contact.getUserId() : null) + " , Channel : " + (channel != null ? channel.getName() : null) + ", isToolbar : " + isToolbar);
    }

    @Override
    public void onAction(Context context, String action, Message message, Object object) {

    }
}
