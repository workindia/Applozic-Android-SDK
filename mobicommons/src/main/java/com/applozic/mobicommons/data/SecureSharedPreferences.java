package com.applozic.mobicommons.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.applozic.mobicommons.encryption.SecurityUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * a security wrapper over {@link SharedPreferences} implementing encryption and decryption of the key-value pairs.
 * uses {@link SecurityUtils} as the utility class with the cryptography related code
 *
 * @author shubhamtewari
 * 1st February, 2020
 */
public class SecureSharedPreferences implements SharedPreferences {

    private SharedPreferences sharedPreferences; //shared preference object being used
    private SecurityUtils securityUtils; //with the encrypt, decrypt functions

    public SecureSharedPreferences(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        securityUtils = new SecurityUtils(context);
    }

    /**
     * NOTE: the values returned will be encrypted, you must decrypt them manually using {@link SecurityUtils}
     *
     * @return map with (String, ?) as the key, value
     */
    @Override
    public Map<String, ?> getAll() {
        return sharedPreferences.getAll();
    }

    @Nullable
    @Override
    public String getString(String key, @Nullable String defValue) {
        try {
            return securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, key), defValue));
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValue) {
        Set<String> encryptSet = sharedPreferences.getStringSet(securityUtils.encrypt(SecurityUtils.AES, key), defValue);
        Set<String> decryptSet = new HashSet<>();
        if (encryptSet == null) {
            return defValue;
        }
        for (String string : encryptSet) {
            decryptSet.add(securityUtils.decrypt(SecurityUtils.AES, string));
        }
        return decryptSet;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            return Integer.parseInt(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, key), String.valueOf(defValue))));
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return defValue;
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            return Long.parseLong(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, key), String.valueOf(defValue))));
        } catch (NullPointerException exception) {
            exception.printStackTrace();
            return defValue;
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            return Float.parseFloat(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, key), String.valueOf(defValue))));
        } catch (NullPointerException exceptionNull) {
            exceptionNull.printStackTrace();
            return defValue;
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return Boolean.parseBoolean(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, key), String.valueOf(defValue))));
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public boolean contains(String key) {
        try {
            return sharedPreferences.contains(securityUtils.encrypt(SecurityUtils.AES, key));
        } catch (Exception exception) {
            exception.printStackTrace();
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

    /**
     * wrapper over {@link android.content.SharedPreferences.Editor} to implement encryption
     *
     * @author shubhamtewari
     * 1st February, 2020
     */
    public class SecureEditor implements SharedPreferences.Editor {

        Editor editor;

        SecureEditor(Editor editor) {
            this.editor = editor;
        }

        @Override
        public SecureEditor putString(String key, @Nullable String value) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, key), securityUtils.encrypt(SecurityUtils.AES, value));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putStringSet(String key, @Nullable Set<String> values) {
            try {
                Set<String> encryptedStringSet = new HashSet<>();
                if (values == null) {
                    return this;
                }
                for (String string : values) {
                    encryptedStringSet.add(securityUtils.encrypt(SecurityUtils.AES, string));
                }
                editor.putStringSet(securityUtils.encrypt(SecurityUtils.AES, key), encryptedStringSet);
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putInt(String key, int value) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, key), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(value)));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putLong(String key, long value) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, key), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(value)));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putFloat(String key, float value) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, key), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(value)));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putBoolean(String key, boolean value) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, key), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(value)));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor remove(String key) {
            try {
                editor.remove(securityUtils.encrypt(SecurityUtils.AES, key));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
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
