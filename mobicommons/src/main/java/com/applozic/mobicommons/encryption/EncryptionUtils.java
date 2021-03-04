package com.applozic.mobicommons.encryption;

import android.text.TextUtils;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by sunil on 26/8/16.
 */
public class EncryptionUtils {

    private static final String TAG = "EncryptionUtils";
    private static final String ALGORITHM = "AES/CBC/PKCS5PADDING";
    private static final String ALGORITHM_AES = "AES";

    // Performs Encryption
    public static String encrypt(String keyString, byte[] iv, String plainText) throws Exception {
        if (TextUtils.isEmpty(plainText)) {
            return null;
        }
        SecretKeySpec secretKeySpec = generateKey(keyString);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec,
                new IvParameterSpec(iv));
        byte[] encrypted = cipher.doFinal(plainText.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    // Performs decryption
    public static String decrypt(String keyString, byte[] iv, String encryptedText) throws Exception {
        SecretKeySpec secretKeySpec = generateKey(keyString);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec,
                new IvParameterSpec(iv));
        byte[] decodedValue = Base64.decode(encryptedText, Base64.DEFAULT);
        byte[] original = cipher.doFinal(decodedValue);
        return new String(original);
    }

    //generateKey() is used to generate a secret key for AES algorithm
    private static SecretKeySpec generateKey(String keyString) throws Exception {
        return new SecretKeySpec(keyString.getBytes("UTF-8"), ALGORITHM_AES);
    }
}