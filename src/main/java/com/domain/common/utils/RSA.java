package com.domain.common.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Frank: 在zhongsq SecurityKey.java基础上修改的RSA算法，仅做RSA加解密使用.
 * 不同JDK中的TRANSFORMATION可能不一样, SUN JDK默认是RSA/ECB/PKCS1Padding, Android 默认是RSA/None/NoPadding
 * <p>
 * 1. 在Android中可直接使用当前类，调用方式参考SecurityTest.java.
 * 2. iOS使用RSA/ECB/PKCS1Padding算法计算
 * @author Frank
 */
public class RSA {

    public final static String TRANSFORMATION = "RSA/ECB/PKCS1Padding";

    public final static String ALGORITHM = "RSA";

    private PrivateKey privateKey; // 私钥

    private PublicKey publicKey;   // 公钥

    private RSA() {

    }

    public static RSA instance(int keySize) throws NoSuchAlgorithmException {

        RSA rsa = new RSA();
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGen.initialize(keySize);
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        rsa.publicKey = (RSAPublicKey) keyPair.getPublic();
        rsa.privateKey = (RSAPrivateKey) keyPair.getPrivate();

        return rsa;
    }

    private PrivateKey getPrivateKey() {

        return privateKey;
    }

    private PublicKey getPublicKey() {

        return publicKey;
    }

    public String getPrivateKeyText() {

        return Base64.encodeBase64String(privateKey.getEncoded());
    }

    public String getPublicKeyText() {

        return Base64.encodeBase64String(publicKey.getEncoded());
    }

    public static Key getPrivateKey(String key) throws Exception {

        byte[] keyBytes = Base64.decodeBase64(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }

    public static Key getPublicKey(String key) throws Exception {

        byte[] keyBytes = Base64.decodeBase64(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }

    public static byte[] encrypt(byte[] plain, Key key) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(plain);
    }

    public static String encrypt(String data, Key key) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] bt = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeBase64String(bt);
    }

    public static String decrypt(String data, Key key) throws Exception {

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        return new String(cipher.doFinal(Base64.decodeBase64(data)), "UTF-8");
    }

}
