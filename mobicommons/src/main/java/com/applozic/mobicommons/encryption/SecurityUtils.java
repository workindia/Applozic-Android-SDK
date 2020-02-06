package com.applozic.mobicommons.encryption;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.text.TextUtils;
import android.util.Base64;

import com.applozic.mobicommons.commons.core.utils.Utils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
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

    public static final String TAG = "SecurityUtils";

    public static final String AES = "AES";
    public static final String RSA = "RSA";

    private static final String CIPHER_AES = "AES/CBC/PKCS5PADDING";
    private static final String CIPHER_RSA = "RSA/ECB/PKCS1Padding";
    private static final String RSA_KEY_ALIAS = "ApplozicRSAKey";
    private static final String RSA_PROVIDER = "AndroidKeyStore";
    private static final String CRYPTO_SHARED_PREF = "cryptosharedpreferences"; //name for the shared pref storing the AES encryption key
    private static final String AES_ENCRYPTION_KEY = "aesencryptionkey"; //key for the AES encryption key entry

    private SecretKey secretKeyAES;
    private KeyPair keyPairRSA;
    private byte[] initializationVector;
    private Context context;

    /**
     * initialize the object and get the secret key, based on the algorithm
     *
     * @param context the context
     */
    public SecurityUtils(Context context) {
        this.context = context;
        initializationVector = new byte[16];
        keyPairRSA = getRSAKeyPair();
        if (keyPairRSA != null) {
            secretKeyAES = getAESKey();
        }
    }

    /**
     * generate a public-private RSA key pair using {@link KeyPairGenerator} and using AndroidKeystore as provider.
     * the key-pair is stored using {@link KeyStore}
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
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException exception) {
            exception.printStackTrace();
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

            //generate the public and private keys to encrypt/decrypt the AES key
            if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
                generateRSAKeyPair();
            }
            //retrieve keys from keystore
            KeyStore.PrivateKeyEntry keyEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);
            PublicKey publicKey = keyEntry.getCertificate().getPublicKey();
            PrivateKey privateKey = keyEntry.getPrivateKey();
            return new KeyPair(publicKey, privateKey);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException | UnrecoverableEntryException exception) {
            exception.printStackTrace();
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
        } catch (NoSuchAlgorithmException exception) {
            exception.printStackTrace();
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
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(CRYPTO_SHARED_PREF, Context.MODE_PRIVATE);
        if (sharedPreferences.contains(AES_ENCRYPTION_KEY)) { //get key from shared pref file, decrypt it and return
            String cipherKey = sharedPreferences.getString(AES_ENCRYPTION_KEY, null);
            String plainKey = decrypt(RSA, cipherKey);
            byte[] decodedKey = Base64.decode(plainKey, Base64.DEFAULT);
            return new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
        } else { //generate AES key, encrypt it, store it to shared pref and return the un-encrypted version
            SecretKey secretKey = generateAESKey();
            if (secretKey == null) {
                Utils.printLog(context, TAG, "SecretKey is null. There are problems occurring with it's generation at runtime.");
                return null;
            }
            String plainKey = Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
            String cipherKey = encrypt(RSA, plainKey);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(AES_ENCRYPTION_KEY, cipherKey);
            editor.apply();
            return secretKey;
        }
    }

    /**
     * return the {@link Cipher} object based on the cipher mode(encryption or decryption) and the algorithm
     *
     * @param cryptAlgorithm the algorithm to use: AES or RSA
     * @param cryptMode      the mode: encryption ot decryption, passed as an int constant
     * @return the cipher object
     * @throws NoSuchPaddingException             if the padding type doesn't exist
     * @throws NoSuchAlgorithmException           if the algo doesn't exist
     * @throws InvalidAlgorithmParameterException if the algorithm parameters are null or not-compatible
     * @throws InvalidKeyException                if the key is not compatible
     */
    private Cipher returnCipher(String cryptAlgorithm, int cryptMode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher;
        Key keyRSA = cryptMode == Cipher.DECRYPT_MODE ? keyPairRSA.getPrivate() : keyPairRSA.getPublic();
        if (AES.equals(cryptAlgorithm)) {
            cipher = Cipher.getInstance(CIPHER_AES);
            cipher.init(cryptMode, new SecretKeySpec(secretKeyAES.getEncoded(), cryptAlgorithm), new IvParameterSpec(initializationVector));
        } else if (RSA.equals(cryptAlgorithm)) {
            cipher = Cipher.getInstance(CIPHER_RSA);
            if (keyRSA == null) {
                throw new InvalidAlgorithmParameterException("Please provide RSA public or private key when passing cryptAlgorithm == \"RSA\".");
            }
            cipher.init(cryptMode, keyRSA);
        } else {
            throw new NoSuchAlgorithmException("The algorithm parameter that is passed to the method must either be \"AES\" or \"RSA\".");
        }
        return cipher;
    }

    /**
     * encrypt string plain text to string cipher text based on the encryption algorithm name passed.
     * NOTE: when passing RSA as the encryption algorithm, note than the plain text size must be less than 256 bits
     *
     * @param cryptAlgorithm the name of the algorithm to use
     * @param plainText      the plain text
     * @return the cipher text
     */
    public String encrypt(String cryptAlgorithm, String plainText) {
        if (TextUtils.isEmpty(plainText) || TextUtils.isEmpty(cryptAlgorithm)) {
            return null;
        }
        try {
            Cipher cipher = returnCipher(cryptAlgorithm, Cipher.ENCRYPT_MODE);
            byte[] cipherText = cipher.doFinal(plainText.getBytes());
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        } catch (BadPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    /**
     * decrypt string cipher text to string plain text based on the encryption algorithm name passed.
     *
     * @param cryptAlgorithm the name of the algorithm to use
     * @param cipherText     the plain text
     * @return the plain text
     */
    public String decrypt(String cryptAlgorithm, String cipherText) {
        try {
            Cipher cipher = returnCipher(cryptAlgorithm, Cipher.DECRYPT_MODE);
            byte[] cipherArray = Base64.decode(cipherText, Base64.DEFAULT);
            byte[] plainText = cipher.doFinal(cipherArray);
            return new String(plainText);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException exception) {
            exception.printStackTrace();
            return null;
        }
    }
}
