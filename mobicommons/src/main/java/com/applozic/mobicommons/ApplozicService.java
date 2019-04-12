package com.applozic.mobicommons;

import android.app.Application;
import android.content.Context;

public class ApplozicService {
    private static Application application;

    public static Application getAppContext() {
        return application;
    }

    public static void initApp(Application application) {
        ApplozicService.application = application;
    }

    public static Context getContext(Context context) {
        if (application != null) {
            return application;
        }
        if (context != null) {
            return context instanceof Application ? context : context.getApplicationContext();
        }
        return getAppContext();
    }
}
