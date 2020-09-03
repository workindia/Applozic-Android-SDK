package com.applozic.mobicommons.data;

import android.content.Context;
import android.text.TextUtils;

import com.applozic.mobicommons.ApplozicService;

public class AlPrefSettings {

    public static final String AL_PREF_SETTING_KEY = "al_secret_key_pref";
    private static final String APPLICATION_KEY = "APPLICATION_KEY";
    public static final String GOOGLE_API_KEY_META_DATA = "com.google.android.geo.API_KEY";
    private SecureSharedPreferences sharedPreferences;
    private static AlPrefSettings alPrefSettings;
    private static String decodedAppKey;
    private static String decodedGeoApiKey;

    private AlPrefSettings(Context context) {
        this.sharedPreferences = new SecureSharedPreferences(AL_PREF_SETTING_KEY, ApplozicService.getContext(context));
    }

    public static AlPrefSettings getInstance(Context context) {
        if (alPrefSettings == null) {
            alPrefSettings = new AlPrefSettings(ApplozicService.getContext(context));
        }
        return alPrefSettings;
    }

    public String getApplicationKey() {
        if (TextUtils.isEmpty(decodedAppKey)) {
            decodedAppKey = sharedPreferences.getString(APPLICATION_KEY, null);
        }
        return decodedAppKey;
    }

    public String getGeoApiKey() {
        if (TextUtils.isEmpty(decodedGeoApiKey)) {
            decodedGeoApiKey = sharedPreferences.getString(GOOGLE_API_KEY_META_DATA, null);
        }
        return decodedGeoApiKey;
    }

    public AlPrefSettings setApplicationKey(String applicationKey) {
        decodedAppKey = applicationKey;
        sharedPreferences.edit().putString(APPLICATION_KEY, applicationKey).commit();
        return this;
    }

    public AlPrefSettings setGeoApiKey(String geoApiKey) {
        decodedGeoApiKey = geoApiKey;
        sharedPreferences.edit().putString(GOOGLE_API_KEY_META_DATA, geoApiKey).commit();
        return this;
    }
}
