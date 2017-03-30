package com.domain.common.utils;

import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class ThirdPartyAES {

    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static final String ALGORITHM = "AES";

    private static AlgorithmParameterSpec getIV() {

        byte[] iv = {10, 1, 11, 5, 12, 4, 15, 7, 13, 9, 23, 3, 2, 14, 8, 12};
        return new IvParameterSpec(iv);
    }

    public static byte[] encrypt(byte[] plain, String passwd) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), "AES");
        cipher.init(1, key, getIV());
        return cipher.doFinal(plain);
    }

    public static String encrypt(String data, String passwd) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), "AES");
        cipher.init(1, key, getIV());
        byte[] bt = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeBase64String(bt);
    }

    public static String decrypt(String data, String passwd) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), "AES");
        cipher.init(2, key, getIV());
        return new String(cipher.doFinal(Base64.decodeBase64(data)), "UTF-8");
    }
}