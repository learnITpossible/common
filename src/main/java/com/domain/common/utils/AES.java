package com.domain.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.AlgorithmParameterSpec;

/**
 * JDK不支持PKCS7Padding,这里使用PKCS5Padding(实现上和PKCS7Padding一致), 这里手工加入iOS支持的PKCS7Padding模式
 * <p>
 * 1. 在Android中可直接使用当前类，调用方式参考SecurityTest.aesTest().
 * 2. 在iOS端，使用  PKCS7Padding
 * @author Frank
 */
public class AES {

    public final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public final static String ALGORITHM = "AES";

    private static AlgorithmParameterSpec getIV() {

        byte[] iv = {0xA, 1, 0xB, 5, 0xC, 4, 0xF, 7, 0xD, 9, 0x17, 3, 2, 0xE, 8, 12};
        return new IvParameterSpec(iv);
    }

    public static byte[] encrypt(byte[] plain, String passwd) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, getIV());
        return cipher.doFinal(plain);
    }

    public static String encrypt(String data, String passwd) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key, getIV());
        byte[] bt = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeBase64String(bt);
    }

    public static String decrypt(String data, String passwd) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        SecretKeySpec key = new SecretKeySpec(passwd.getBytes(), ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key, getIV());
        return new String(cipher.doFinal(Base64.decodeBase64(data)), "UTF-8");
    }

}
