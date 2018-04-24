package com.applozic.mobicomkit;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ashish on 24/04/18.
 * Do not touch settings from this file, unless asked to do so by Applozic
 * This may result in break down of an update to already existing application.
 */

public class ALSpecificSettings {
    private static final String MY_PREFERENCE = "applozic_internal_preference_key";
    private SharedPreferences sharedPreferences;
    public static ALSpecificSettings applozicSettings;
    private static final String DATABASE_NAME = "DATABASE_NAME";

    private ALSpecificSettings(Context context) {
        this.sharedPreferences = context.getSharedPreferences(MY_PREFERENCE, context.MODE_PRIVATE);
    }

    public static ALSpecificSettings getInstance(Context context) {
        if (applozicSettings == null) {
            applozicSettings = new ALSpecificSettings(context.getApplicationContext());
        }
        return applozicSettings;
    }

    public ALSpecificSettings setDatabaseName(String dbName) {
        sharedPreferences.edit().putString(DATABASE_NAME, dbName).apply();
        return this;
    }

    public String getDatabaseName() {
        return sharedPreferences.getString(DATABASE_NAME, null);
    }

    public boolean clearAll() {
        if (sharedPreferences != null) {
            return sharedPreferences.edit().clear().commit();
        }

        return false;
    }
}
