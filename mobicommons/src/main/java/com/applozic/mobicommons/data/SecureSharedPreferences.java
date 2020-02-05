package com.applozic.mobicommons.data;

import android.content.Context;
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

    public SecureSharedPreferences(SharedPreferences sharedPreferences, Context context) {
        this.sharedPreferences = sharedPreferences;
        securityUtils = new SecurityUtils(context);
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
                String decryptedKey = securityUtils.decrypt(SecurityUtils.AES, entry.getKey());
                decryptedMap.put(decryptedKey, securityUtils.decrypt(SecurityUtils.AES, String.valueOf(prefMap.get(decryptedKey))));
            }
        }
        return decryptedMap;
    }

    @Nullable
    @Override
    public String getString(String s, @Nullable String s1) {
        try {
            return securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, s), s1));
        } catch (Exception e) {
            e.printStackTrace();
            return s1;
        }
    }

    @Nullable
    @Override
    public Set<String> getStringSet(String s, @Nullable Set<String> set) {
        Set<String> encryptSet = sharedPreferences.getStringSet(securityUtils.encrypt(SecurityUtils.AES, s), set);
        Set<String> decryptSet = new HashSet<>();
        if (encryptSet == null) {
            return set;
        }
        for (String string : encryptSet) {
            decryptSet.add(securityUtils.decrypt(SecurityUtils.AES, string));
        }
        return decryptSet;
    }

    @Override
    public int getInt(String s, int i) {
        try {
            return Integer.parseInt(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, s), String.valueOf(i))));
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
            return Long.parseLong(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, s), String.valueOf(l))));
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
            return Float.parseFloat(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, s), String.valueOf(v))));
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
            return Boolean.parseBoolean(securityUtils.decrypt(SecurityUtils.AES, sharedPreferences.getString(securityUtils.encrypt(SecurityUtils.AES, s), String.valueOf(b))));
        } catch (Exception e) {
            e.printStackTrace();
            return b;
        }
    }

    @Override
    public boolean contains(String s) {
        try {
            return sharedPreferences.contains(securityUtils.encrypt(SecurityUtils.AES, s));
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
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, s), securityUtils.encrypt(SecurityUtils.AES, s1));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putStringSet(String s, @Nullable Set<String> set) {
            try {
                Set<String> encryptedStringSet = new HashSet<>();
                if(set == null) {
                    return this;
                }
                for (String string : set) {
                    encryptedStringSet.add(securityUtils.encrypt(SecurityUtils.AES, string));
                }
                editor.putStringSet(securityUtils.encrypt(SecurityUtils.AES, s), encryptedStringSet);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putInt(String s, int i) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, s), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(i)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putLong(String s, long l) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, s), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(l)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putFloat(String s, float v) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, s), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(v)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor putBoolean(String s, boolean b) {
            try {
                editor.putString(securityUtils.encrypt(SecurityUtils.AES, s), securityUtils.encrypt(SecurityUtils.AES, String.valueOf(b)));
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public SecureEditor remove(String s) {
            try {
                editor.remove(securityUtils.encrypt(SecurityUtils.AES, s));
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
