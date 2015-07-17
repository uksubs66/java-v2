/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.foretees.common;


import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author sindep
 */
public class AESencrypt {

public static String IV = "7676kjh6575dd2325";
public static String encryptionKey = "ft4e9ls21srtiop3a5";


public static byte[] getMD5(String text) {

    byte byteData[] = null;

    try {

        MessageDigest md = MessageDigest.getInstance("MD5");
        byteData = md.digest(text.getBytes("UTF-8"));

    } catch (Exception exc) {
        exc.printStackTrace();
    }

    return byteData;

}


public static byte[] encrypt(String plainText, String encryptionKey, byte[] iv) throws Exception {

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    SecretKeySpec key = new SecretKeySpec(getMD5(encryptionKey), "AES");
    cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv));

    return cipher.doFinal(plainText.getBytes("UTF-8"));

}

public static String decrypt(byte[] cipherBytes, String encryptionKey, byte[] iv) throws Exception{

    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // AES/CFB/NoPadding
    //SecretKeySpec key = new SecretKeySpec(BasicSHA256.SHA256byte(encryptionKey), "AES");
    SecretKeySpec key = new SecretKeySpec(getMD5(encryptionKey), "AES");
    cipher.init(Cipher.DECRYPT_MODE, key,new IvParameterSpec(iv));

    return new String(cipher.doFinal(cipherBytes),"UTF-8");
}

}