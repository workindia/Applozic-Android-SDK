package com.applozic.mobicomkit;

import android.content.Context;
import android.content.SharedPreferences;

import com.applozic.mobicomkit.api.MobiComKitClientService;

/**
 * Created by devashish on 8/21/2015.
 */
public class ApplozicClient {

    private Context context;
    public SharedPreferences sharedPreferences;
    private static final String HANDLE_DISPLAY_NAME = "CLIENT_HANDLE_DISPLAY_NAME";

    public static ApplozicClient applozicClient;

    private ApplozicClient(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(MobiComKitClientService.getApplicationKey(context), context.MODE_PRIVATE);
    }

    public static ApplozicClient getInstance(Context context) {
        if (applozicClient == null) {
            applozicClient = new ApplozicClient(context);
        }

        return applozicClient;
    }


    public boolean isHandleDisplayName() {
        return sharedPreferences.getBoolean(HANDLE_DISPLAY_NAME, false);
    }

    public ApplozicClient setHandleDisplayName(String label) {
        sharedPreferences.edit().putString(HANDLE_DISPLAY_NAME, label).commit();
        return this;
    }

}
