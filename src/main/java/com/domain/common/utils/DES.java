package com.domain.common.utils;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;

/**
 * DES加解密
 */
public class DES {

    public final static String TRANSFORMATION = "DES";

    /**
     * 加密
     * @param source
     * @param password
     * @return
     * @throws Exception
     */
    public static String encrypt(String source, String password) throws Exception {

        SecureRandom random = new SecureRandom();
        DESKeySpec desKey = new DESKeySpec(password.getBytes());

        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRANSFORMATION);
        SecretKey securekey = keyFactory.generateSecret(desKey);

        // Cipher对象实际完成加密操作
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.ENCRYPT_MODE, securekey, random);

        return Hex.encodeHexString(cipher.doFinal(source.getBytes())).toUpperCase();

    }

    /**
     * 解密
     * @param src
     * @param password
     * @return
     * @throws Exception
     */
    public static String decrypt(String src, String password) throws Exception {

        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();

        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());

        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(TRANSFORMATION);

        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);

        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);

        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);

        byte[] byte0 = Hex.decodeHex(src.toCharArray());

        // 真正开始解密操作
        return new String(cipher.doFinal(byte0), "utf-8");

    }

}
