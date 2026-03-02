package com.example.biometricnoteapp.services;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import java.security.KeyStore;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class EncryptionManager {

    private static final String KEY_ALIAS = "Biometrics Note App";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final int KEY_SIZE = 256;
    private static final int TAG_LENGTH = 128;

    private final KeyStore ks;
    public EncryptionManager() throws Exception {
        this.ks = KeyStore.getInstance(KEYSTORE_PROVIDER);
        ks.load(null);  // initializes keystore
    }

    private SecretKey getOrCreateKey() throws Exception {
        KeyStore.Entry entry = ks.getEntry(KEY_ALIAS, null);

        // if key already exists return it
        if (entry instanceof KeyStore.SecretKeyEntry) {
            return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
        }

        // else create key
        KeyGenerator keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                KEYSTORE_PROVIDER
        );

        keyGenerator.init(
                new KeyGenParameterSpec.Builder(
                        KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
            )
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE) // GCM handles padding on its own
                        .setKeySize(KEY_SIZE)
                        .build()
        );
        return keyGenerator.generateKey();
    }
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        SecretKey key = getOrCreateKey();

        // extract data
        byte[] iv = new byte[12];
        byte[] cipherText = new byte[encryptedData.length-12];
        System.arraycopy(encryptedData, 0, iv, 0, 12);
        System.arraycopy(encryptedData, 12, cipherText, 0, cipherText.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        return cipher.doFinal(cipherText);
    }

    public byte[] encrypt(byte[] plainText) throws Exception {
        SecretKey key = getOrCreateKey();

        Cipher cipher = Cipher.getInstance((TRANSFORMATION));
        cipher.init(Cipher.ENCRYPT_MODE, key); // generates an iv here

        byte[] iv = cipher.getIV(); // get the iv so we can store it
        byte[] cipherText = cipher.doFinal(plainText);

        // result is in the form [iv(12 bytes) - ciphertext + auth tag]
        // auth tag is done automatically by gcm
        byte[] result = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(cipherText, 0, result, iv.length, cipherText.length);

        return result;
    }
}
