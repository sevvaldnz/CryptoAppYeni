package com.example.cryptoapp;

import android.util.Base64;

import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {
    private static final String AES_MODE = "AES/CBC/PKCS7Padding";
    private static final String CHARSET_NAME = "UTF-8";

    public static String encryptMessage(String message) {
        try {
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(AES_MODE);
            byte[] iv = cipher.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] encryptedBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedBytesWithIv = concatenateByteArrays(iv, encryptedBytes);
            return Base64.encodeToString(encryptedBytesWithIv, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decryptMessage(String encryptedMessage) {
        try {
            byte[] encryptedBytesWithIv = Base64.decode(encryptedMessage, Base64.DEFAULT);
            byte[] iv = getIvFromEncryptedBytes(encryptedBytesWithIv);
            byte[] encryptedBytes = getEncryptedBytes(encryptedBytesWithIv);
            SecretKeySpec secretKey = getSecretKey();
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static SecretKeySpec getSecretKey() {
        byte[] keyBytes = "0123456789abcdef".getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static byte[] concatenateByteArrays(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static byte[] getIvFromEncryptedBytes(byte[] encryptedBytesWithIv) {
        byte[] iv = new byte[16];
        System.arraycopy(encryptedBytesWithIv, 0, iv, 0, iv.length);
        return iv;
    }

    private static byte[] getEncryptedBytes(byte[] encryptedBytesWithIv) {
        byte[] encryptedBytes = new byte[encryptedBytesWithIv.length - 16];
        System.arraycopy(encryptedBytesWithIv, 16, encryptedBytes, 0, encryptedBytes.length);
        return encryptedBytes;
    }
}

