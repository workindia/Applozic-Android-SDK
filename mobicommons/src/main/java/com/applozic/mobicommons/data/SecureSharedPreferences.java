package com.applozic.mobicommons.data;

import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.applozic.mobicommons.encryption.SecurityUtils;

import java.util.HashMap;
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

    public SecureSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        securityUtils = new SecurityUtils(SecurityUtils.AES); //AES algorithm used for encryption
    }

    /**
     * returns a (String, String) map
     * NOTE: this differs from the java {@link SharedPreferences} implementation which returns (String, ?) map
     *
     * @return map with (String, String) as the key, value
     */
    @Override
    public Map<String, String> getAll() {
        Map<String, ?> prefMap = sharedPreferences.getAll();
        Map<String, String> decryptedMap = new HashMap<>();

        if(!prefMap.isEmpty()) {
            for (Map.Entry<String, ?> entry : prefMap.entrySet()) {
                String decryptedKey = securityUtils.decrypt(entry.getKey());
                decryptedMap.put(decryptedKey, securityUtils.decrypt(String.valueOf(prefMap.get(decryptedKey))));
            }
        }
        return decryptedMap;
    }

    @Nullable
    @Override
    public String getString(String s, @Nullable String s1) {
        try {
            return securityUtils.decrypt(sharedPreferences.getString(securityUtils.encrypt(s), s1));
        } catch (Exception e) {
            e.printStackTrace();
            return s1;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        Set<String> encryptSet = sharedPreferences.getStringSet(securityUtils.decrypt(s), set);
        Set<String> decryptSet = new HashSet<>();
        if (encryptSet == null) {
            return set;
        }
        for (String string : encryptSet) {
            decryptSet.add(securityUtils.decrypt(string));
        }
        return decryptSet;
    }

    @Override
    public int getInt(String s, int i) {
        try {
            return Integer.parseInt(securityUtils.decrypt(sharedPreferences.getString(securityUtils.encrypt(s), String.valueOf(i))));
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
            return Long.parseLong(securityUtils.decrypt(sharedPreferences.getString(securityUtils.encrypt(s), String.valueOf(l))));
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
            return Float.parseFloat(securityUtils.decrypt(sharedPreferences.getString(securityUtils.encrypt(s), String.valueOf(v))));
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
            return Boolean.parseBoolean(securityUtils.decrypt(sharedPreferences.getString(securityUtils.encrypt(s), String.valueOf(b))));
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }
    }

    @Override
    public boolean contains(String s) {
        try {
            return sharedPreferences.contains(securityUtils.encrypt(s));
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
        public SecureEditor putString(String s, @Nullable String s1) {
            try {
                editor.putString(securityUtils.encrypt(s), securityUtils.encrypt(s1));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putStringSet(String s, @Nullable Set<String> set) {
            try {
                editor.putStringSet(securityUtils.encrypt(s), set);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putInt(String s, int i) {
            try {
                editor.putString(securityUtils.encrypt(s), securityUtils.encrypt(String.valueOf(i)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putLong(String s, long l) {
            try {
                editor.putString(securityUtils.encrypt(s), securityUtils.encrypt(String.valueOf(l)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putFloat(String s, float v) {
            try {
                editor.putString(securityUtils.encrypt(s), securityUtils.encrypt(String.valueOf(v)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putBoolean(String s, boolean b) {
            try {
                editor.putString(securityUtils.encrypt(s), securityUtils.encrypt(String.valueOf(b)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor remove(String s) {
            try {
                editor.remove(securityUtils.encrypt(s));
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
