package com.applozic.mobicommons.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.applozic.mobicommons.ApplozicService;
import com.applozic.mobicommons.encryption.SecurityUtils;

import java.security.KeyPair;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.crypto.SecretKey;

/**
 * a security wrapper over {@link SharedPreferences} implementing encryption and decryption of the key-value pairs.
 * uses {@link SecurityUtils} as the utility class with the cryptography related code
 *
 * @author shubhamtewari
 * 1st February, 2020
 */
public class SecureSharedPreferences implements SharedPreferences {

    private SharedPreferences sharedPreferences; //shared preference object being used
    private SecretKey secretKeyAES;
    private byte[] initializationVector;

    public SecureSharedPreferences(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        Context applicationContext = ApplozicService.getContext(context);
        KeyPair keyPairRSA = SecurityUtils.getRSAKeyPair(applicationContext);
        if (keyPairRSA != null) {
            secretKeyAES = SecurityUtils.getAESKey(applicationContext, keyPairRSA);
        }
        initializationVector = new byte[16];
    }

    /**
     * return the plain value for the given plain key, from the encrypted shared pref key/value pairs
     *
     * @param key      the plain key string
     * @param defValue the default value
     * @param <T>      for the default value type
     * @return the plain value for the given key
     */
    private <T> String getDecryptedString(String key, T defValue) {
        return SecurityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(SecurityUtils.encrypt(SecurityUtils.AES, key, secretKeyAES, initializationVector), String.valueOf(defValue)), secretKeyAES, initializationVector);
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
            return getDecryptedString(key, defValue);
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String key, @Nullable Set<String> defValue) {
        Set encryptSet = sharedPreferences.getStringSet(SecurityUtils.encrypt(SecurityUtils.AES, key, secretKeyAES, initializationVector), defValue);
        Set<String> decryptSet = new HashSet<>();
        if (encryptSet == null) {
            return defValue;
        }
        for (Object string : encryptSet) {
            decryptSet.add(SecurityUtils.decrypt(SecurityUtils.AES, (String) string, secretKeyAES, initializationVector));
        }
        return decryptSet;
    }

    @Override
    public int getInt(String key, int defValue) {
        try {
            return Integer.parseInt(getDecryptedString(key, defValue));
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public long getLong(String key, long defValue) {
        try {
            return Long.parseLong(getDecryptedString(key, defValue));
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public float getFloat(String key, float defValue) {
        try {
            return Float.parseFloat(getDecryptedString(key, defValue));
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        try {
            return Boolean.parseBoolean(getDecryptedString(key, defValue));
        } catch (Exception exception) {
            exception.printStackTrace();
            return defValue;
        }
    }

    @Override
    public boolean contains(String key) {
        try {
            return sharedPreferences.contains(SecurityUtils.encrypt(SecurityUtils.AES, key, secretKeyAES, initializationVector));
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

        /**
         * add the given value to the {@link SharedPreferences} as a string (for encryption and decryption)
         *
         * @param key   the plain key
         * @param value the plain value
         * @param <T>   depending of the type of putX function this method is being used in
         * @return the {@link SecureEditor}
         */
        private <T> SecureEditor putAsString(String key, T value) {
            try {
                editor.putString(SecurityUtils.encrypt(SecurityUtils.AES, key, secretKeyAES, initializationVector), SecurityUtils.encrypt(SecurityUtils.AES, String.valueOf(value), secretKeyAES, initializationVector));
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putString(String key, @Nullable String value) {
            return putAsString(key, value);
        }

        @Override
        public SecureEditor putStringSet(String key, @Nullable Set<String> values) {
            try {
                Set<String> encryptedStringSet = new HashSet<>();
                if (values == null) {
                    return this;
                }
                for (String string : values) {
                    encryptedStringSet.add(SecurityUtils.encrypt(SecurityUtils.AES, string, secretKeyAES, initializationVector));
                }
                editor.putStringSet(SecurityUtils.encrypt(SecurityUtils.AES, key, secretKeyAES, initializationVector), encryptedStringSet);
                return this;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putInt(String key, int value) {
            return putAsString(key, value);
        }

        @Override
        public SecureEditor putLong(String key, long value) {
            return putAsString(key, value);
        }

        @Override
        public SecureEditor putFloat(String key, float value) {
            return putAsString(key, value);
        }

        @Override
        public SecureEditor putBoolean(String key, boolean value) {
            return putAsString(key, value);
        }

        @Override
        public SecureEditor remove(String key) {
            try {
                editor.remove(SecurityUtils.encrypt(SecurityUtils.AES, key, secretKeyAES, initializationVector));
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
