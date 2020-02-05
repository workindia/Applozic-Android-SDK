package com.applozic.mobicommons.encryption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.text.TextUtils;
import android.util.Base64;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Calendar;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

/**
 * Security utility functions, such as encryption and decryption of strings
 * Improvement on {@link EncryptionUtils}
 *
 * @author shubhamtewari
 * 3rd February, 2020
 */
public class SecurityUtils {

    public static final String AES = "AES";
    public static final String RSA = "RSA";

    private static final String CIPHER_AES = "AES/CBC/PKCS5PADDING";
    private static final String CIPHER_RSA = "RSA/ECB/PKCS1Padding";
    private static final String RSA_KEY_ALIAS = "ApplozicRSAKey";
    private static final String RSA_PROVIDER = "AndroidKeyStore";
    private static final String CRYPTO_SHARED_PREF = "cryptosharedpreferences";
    private static final String AES_ENCRYPTION_KEY = "aesencryptionkey";

    private SecretKey secretKeyAES;
    private KeyPair keyPairRSA;
    private byte[] iv;
    private Context context;

    /**
     * initialize the object and get the secret key, based on the algorithm
     *
     * @param context the context
     */
    public SecurityUtils(Context context) {
        this.context = context;
        iv = new byte[16];
        keyPairRSA = getRSAKeyPair();
        if (keyPairRSA != null) {
            secretKeyAES = getAESKey();
        }
    }

    /**
     * generate a public-private RSA key pair using {@link KeyPairGenerator} and using AndroidKeystore as provider.
     * the key-pair is stored using {@link KeyStore}
     *
     * @return the {@link KeyPair} public/private RSA key pair
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void generateRSAKeyPair() {
        try {
            Calendar start = Calendar.getInstance();
            Calendar end = Calendar.getInstance();
            end.add(Calendar.YEAR, 25); //key certificate will be valid for 25 years
            KeyPairGeneratorSpec keyPairGeneratorSpec = new KeyPairGeneratorSpec.Builder(context.getApplicationContext())
                    .setAlias(RSA_KEY_ALIAS)
                    .setSubject(new X500Principal("CN=" + RSA_KEY_ALIAS + ", O=ApplozicInc"))
                    .setSerialNumber(BigInteger.valueOf(123456))
                    .setStartDate(start.getTime())
                    .setEndDate(end.getTime())
                    .build();
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA, RSA_PROVIDER);
            keyPairGenerator.initialize(keyPairGeneratorSpec);
            keyPairGenerator.genKeyPair();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * get keys from {@link KeyStore} or generate them using the generateRSAKeyPair method.
     *
     * @return RSA key-pair {@link KeyPair}
     */
    private KeyPair getRSAKeyPair() {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            //generate or retrieve the public and private keys to encrypt/decrypt the AES key
            if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
                generateRSAKeyPair();
            }
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
            PublicKey publicKey = keyEntry.getCertificate().getPublicKey();
            PrivateKey privateKey = keyEntry.getPrivateKey();
            return new KeyPair(publicKey, privateKey);
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * generate a 256-bit AES symmetric key, using {@link KeyGenerator}
     *
     * @return {@link SecretKey} the secret key
     */
    private SecretKey generateAESKey() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance(AES);
            keygen.init(256);
            return keygen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get secret key for shared pref, or generate one if not found
     * the secret key is encrypted using RSA and stored in shared preferences
     *
     * @return the AES key {@link SecretKey}
     */
    private SecretKey getAESKey() {
        try {
            SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(CRYPTO_SHARED_PREF, Context.MODE_PRIVATE);
            if(sharedPreferences.contains(AES_ENCRYPTION_KEY)) { //get key from shared pref file, decrypt it and return
                String cipherKey = sharedPreferences.getString(AES_ENCRYPTION_KEY, null);
                String plainKey = decrypt(RSA, cipherKey);
                byte[] decodedKey = Base64.decode(plainKey, Base64.DEFAULT);
                return new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
            } else { //generate AES key, encrypt it, store it to shared pref and return the un-encrypted version
                SecretKey secretKey = generateAESKey();
                String plainKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
                String cipherKey = encrypt(RSA, plainKey);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(AES_ENCRYPTION_KEY, cipherKey);
                editor.apply();
                return secretKey;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * encrypt string plain text to string cipher text based on the encryption algorithm name passed.
     * NOTE: when passing RSA as the encryption algorithm, note than the plain text size must be less than 256 bits
     *
     * @param cryptAlgorithm the name of the algorithm to use
     * @param plainText the plain text
     * @return the cipher text
     */
    public String encrypt(String cryptAlgorithm, String plainText) {
        if(TextUtils.isEmpty(plainText) || TextUtils.isEmpty(cryptAlgorithm)) {
            return null;
        }
        try {
            Cipher cipher;
            if(cryptAlgorithm.equals(AES)) {
                cipher = Cipher.getInstance(CIPHER_AES);
                cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(secretKeyAES.getEncoded(), cryptAlgorithm), new IvParameterSpec(iv));
            } else if(cryptAlgorithm.equals(RSA)) {
                cipher = Cipher.getInstance(CIPHER_RSA);
                cipher.init(Cipher.ENCRYPT_MODE, keyPairRSA.getPublic());
            } else {
                throw new NoSuchAlgorithmException("The parameter that is passed to the encrypt method must either be AES or RSA.");
            }
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

    /**
     * decrypt string cipher text to string plain text based on the encryption algorithm name passed.
     *
     * @param cryptAlgorithm the name of the algorithm to use
     * @param cipherText the plain text
     * @return the plain text
     */
    public String decrypt(String cryptAlgorithm, String cipherText) {
        try {
            Cipher cipher;
            if(cryptAlgorithm.equals(AES)) {
                cipher = Cipher.getInstance(CIPHER_AES);
                cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(secretKeyAES.getEncoded(), cryptAlgorithm), new IvParameterSpec(iv));
            } else if(cryptAlgorithm.equals(RSA)) {
                cipher = Cipher.getInstance(CIPHER_RSA);
                cipher.init(Cipher.DECRYPT_MODE, keyPairRSA.getPrivate());
            } else {
                throw new NoSuchAlgorithmException("The parameter that is passed to the decrypt method must either be AES or RSA.");
            }

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
