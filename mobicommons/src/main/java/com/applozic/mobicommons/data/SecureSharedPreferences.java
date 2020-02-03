package com.applozic.mobicommons.data;

import android.content.SharedPreferences;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.applozic.mobicommons.encryption.EncryptionUtils;

import java.util.Map;
import java.util.Set;

public class SecureSharedPreferences implements SharedPreferences {

    SharedPreferences sharedPreferences;
    EncryptionUtils encryptionUtils;

    private static final String KEY = "!@#$%^&*()_APPLOZIC";

    public SecureSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        encryptionUtils = new EncryptionUtils();
    }

    @Override
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    @Nullable
    @Override
    public String getString(String s, @Nullable String s1) {
        try {
            return EncryptionUtils.decrypt(KEY, sharedPreferences.getString(s, s1));
        } catch (Exception e) {
            e.printStackTrace();
            return s1;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        return sharedPreferences.getStringSet(s, set);
    }

    @Override
    public int getInt(String s, int i) {
        try {
            return Integer.parseInt(EncryptionUtils.decrypt(KEY, String.valueOf(sharedPreferences.getString(s, String.valueOf(i)))));
        } catch (NullPointerException exceptionNull) {
            exceptionNull.printStackTrace();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    @Override
    public long getLong(String s, long l) {
        try {
            return Long.parseLong(EncryptionUtils.decrypt(KEY, String.valueOf(sharedPreferences.getString(s, String.valueOf(l)))));
        } catch (NullPointerException exceptionNull) {
            exceptionNull.printStackTrace();
            return l;
        } catch (Exception e) {
            e.printStackTrace();
            return l;
        }
    }

    @Override
    public float getFloat(String s, float v) {
        try {
            return Float.parseFloat(EncryptionUtils.decrypt(KEY, String.valueOf(sharedPreferences.getString(s, String.valueOf(v)))));
        } catch (NullPointerException exceptionNull) {
            exceptionNull.printStackTrace();
            return v;
        } catch (Exception e) {
            e.printStackTrace();
            return v;
        }
    }

    @Override
    public boolean getBoolean(String s, boolean b) {
        try {
            return Boolean.parseBoolean(EncryptionUtils.decrypt(KEY, String.valueOf(sharedPreferences.getString(s, String.valueOf(b)))));
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }
    }

    @Override
    public boolean contains(String s) {
        try {
            return sharedPreferences.contains(EncryptionUtils.encrypt(KEY, s));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SecureSharedPreferences.SecureEditor edit() {
        return new SecureEditor(sharedPreferences.edit());
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener onSharedPreferenceChangeListener) {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
    }

    public class SecureEditor implements SharedPreferences.Editor {

        Editor editor;

        SecureEditor(Editor editor) {
            this.editor = editor;
        }

        @Override
        public SecureEditor putString(String s, @Nullable String s1) {
            try {
                editor.putString(EncryptionUtils.encrypt(KEY, s), EncryptionUtils.encrypt(KEY, s1));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putStringSet(String s, @Nullable Set<String> set) {
            try {
                editor.putStringSet(EncryptionUtils.encrypt(KEY, s), set);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putInt(String s, int i) {
            try {
                editor.putString(EncryptionUtils.encrypt(KEY, s), EncryptionUtils.encrypt(KEY, String.valueOf(i)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putLong(String s, long l) {
            try {
                editor.putString(EncryptionUtils.encrypt(KEY, s), EncryptionUtils.encrypt(KEY, String.valueOf(l)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putFloat(String s, float v) {
            try {
                editor.putString(EncryptionUtils.encrypt(KEY, s), EncryptionUtils.encrypt(KEY, String.valueOf(v)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putBoolean(String s, boolean b) {
            try {
                editor.putString(EncryptionUtils.encrypt(KEY, s), EncryptionUtils.encrypt(KEY, String.valueOf(b)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor remove(String s) {
            try {
                editor.remove(EncryptionUtils.encrypt(KEY, s));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor clear() {
            editor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return editor.commit();
        }

        @Override
        public void apply() {
            editor.apply();
        }
    }
}
