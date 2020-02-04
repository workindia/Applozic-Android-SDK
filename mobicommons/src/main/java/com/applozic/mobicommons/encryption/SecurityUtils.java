package com.applozic.mobicommons.encryption;

import android.text.TextUtils;
import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Security utility functions, such as encryption and decryption of strings
 * Improvement on {@link EncryptionUtils}
 *
 * @author shubhamtewari
 * 3rd February, 2020
 */
public class SecurityUtils {

    public static final String AES = "AES";
    private static final String CIPHER_AES = "AES/CBC/PKCS5PADDING";

    private String cryptAlgorithm = AES;
    private SecretKey secretKey;
    private byte[] iv;

    /**
     * get secret key, generate if not yet created
     *
     * @return {@link SecretKey} the secret key
     */
    private SecretKey getSecretKey() {
        KeyGenerator keygen = null;
        try {
            keygen = KeyGenerator.getInstance(cryptAlgorithm);
            keygen.init(256);
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get secret key, or generate one from salt if not found
     *
     * @param salt the salt used to derive the key
     * @return {@link SecretKey} the secret key
     */
    private SecretKey getSecretKey(String password, String salt) {
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt.getBytes(),131072,128);
            SecretKey s = secretKeyFactory.generateSecret(keySpec);
            return new SecretKeySpec(s.getEncoded(),"AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * initialize the object and get the secret key, based on the algorithm
     *
     * @param cryptAlgorithm the algorithm to use
     */
    public SecurityUtils(String cryptAlgorithm) {
        this.cryptAlgorithm = cryptAlgorithm;
        secretKey = getSecretKey("Kommunicate", "Applozic"); //TODO: use key stored in keystore
        iv = new byte[16];
    }

    public String encrypt(String plainText) {
        if(TextUtils.isEmpty(plainText)) {
            return null;
        }
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_AES);
            cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), cryptAlgorithm), new IvParameterSpec(iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String cipherText) {
        try {
            Cipher cipher = Cipher.getInstance(CIPHER_AES);
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKey.getEncoded(), cryptAlgorithm), new IvParameterSpec(iv));
            byte[] cipherArray = Base64.decode(cipherText, Base64.DEFAULT);
            byte[] plainText = cipher.doFinal(cipherArray);
            return new String(plainText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
            return null;
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        } catch (BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
