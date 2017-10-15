package com.applozic.mobicommons.encryption;

import android.text.TextUtils;
import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


/**
 * Created by sunil on 26/8/16.
 */
public class EncryptionUtils {

    private static final String TAG = "EncryptionUtils";
    private static final String ALGORITHM = "AES/ECB/NoPadding";

    // Performs Encryption
    public static String encrypt(String ketString, String plainText) throws Exception {
        // generate key
        if(TextUtils.isEmpty(plainText)){
            return null;
        }
        Key key =  generateKey(ketString);
        while (plainText.length() % 16 != 0) {
            plainText = plainText.concat(" ");
        }

        Cipher chiper = Cipher.getInstance(ALGORITHM);
        chiper.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = chiper.doFinal(plainText.getBytes());
        String encryptedValue = Base64.encodeToString(encVal,Base64.DEFAULT);
        return TextUtils.isEmpty(encryptedValue)?null:encryptedValue.trim();
    }

    // Performs decryption
    public static String decrypt(String ketString, String encryptedText) throws Exception {
        // generate key
        Key key =  generateKey(ketString);
        Cipher chiper= Cipher.getInstance(ALGORITHM);
        chiper.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = Base64.decode(encryptedText,Base64.DEFAULT);
        byte[] decValue = chiper.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return TextUtils.isEmpty(decryptedValue)?null:decryptedValue.trim();
    }

    //generateKey() is used to generate a secret key for AES algorithm
    private static Key generateKey(String ketString) throws Exception {
        Key key = new SecretKeySpec(ketString.getBytes(), ALGORITHM);
        return key;
    }

}