package com.applozic.mobicomkit.uiwidgets;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;

/**
 * Created by devashish on 28/4/14.
 */
public class ApplozicApplication extends Application {

    public static final String TITLE = "My Chats";

    @Override
    public void onCreate() {
        // workaround for http://code.google.com/p/android/issues/detail?id=20915
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                registerActivityLifecycleCallbacks(new ActivityLifecycleHandler());
            }
            Class.forName("android.os.AsyncTask");
        } catch (ClassNotFoundException e) {
        }
        super.onCreate();
    }

}