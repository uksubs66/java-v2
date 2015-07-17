/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.security.MessageDigest;
import java.security.SecureRandom;
//import java.util.Arrays;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author sindep
 */
public class AESencrypt {

    public static String IV = "7676kjh6575dd2325";
    public static String encryptionKey = "ft4e9ls21srtiop3a5";

    public static String encodedIV() {

        return Base64.encodeBase64String(generateIV());

    }

    public static byte[] decodeIV(String encodedIV) {
        
        return Base64.decodeBase64(encodedIV);

    }

    public static byte[] generateIV() {

        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[16];
        random.nextBytes(iv);
        return iv;

    }

    public static byte[] getMD5(String text) {

        byte byteData[] = null;

        try {

            MessageDigest md = MessageDigest.getInstance("MD5");
            byteData = md.digest(text.getBytes("UTF-8"));

        } catch (Exception e) {
            Connect.logError("AESencrypt.getMD5 Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        }

        return byteData;

    }

    public static String encodeBase64(byte[] data) {
        
        return Base64.encodeBase64String(data);
    }

    public static byte[] decodeBase64(String data) {

        return Base64.decodeBase64(data);

    }
    
    public static byte[] fromHexString(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }
    
    public static String encryptToHexString(String plainText, String encryptionKey, byte[] iv) throws Exception {
        return javax.xml.bind.DatatypeConverter.printHexBinary(encrypt(plainText, encryptionKey, iv));
    }

    public static String encryptBase64(String plainText, String encryptionKey, byte[] iv) throws Exception {
        return Base64.encodeBase64String(encrypt(plainText, encryptionKey, iv));
        //return javax.xml.bind.DatatypeConverter.printHexBinary(encrypt(plainText, encryptionKey, iv));
    }

    public static byte[] encrypt(String plainText, String encryptionKey, byte[] iv) throws Exception {

        byte[] keybytes = getMD5(encryptionKey);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(keybytes, "AES");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(iv));

        byte[] result = cipher.doFinal(plainText.getBytes("UTF-8"));
        //Connect.logError("AESencrypt.encrypt Debug=\nLENGTH:" + result.length 
        //        + "\nDATA:" + javax.xml.bind.DatatypeConverter.printHexBinary(result)
        //        + "\nKEY:" + javax.xml.bind.DatatypeConverter.printHexBinary(keybytes) 
        //        + "\nIV:" + javax.xml.bind.DatatypeConverter.printHexBinary(iv));
        return result;

    }
    
    public static String decryptFromHexString(String cipherBytes, String encryptionKey, byte[] iv) throws Exception {
        return decrypt(fromHexString(cipherBytes), encryptionKey, iv);
    }

    public static String decryptBase64(String cipherBytes, String encryptionKey, byte[] iv) throws Exception {

        return decrypt(Base64.decodeBase64(cipherBytes), encryptionKey, iv);
        //return decrypt(fromHexString(cipherBytes), encryptionKey, iv);
    }

    public static String decrypt(byte[] cipherBytes, String encryptionKey, byte[] iv) throws Exception {

        byte[] keybytes = getMD5(encryptionKey);
        //Connect.logError("AESencrypt.decrypt Debug=\nLENGTH:" + cipherBytes.length 
        //        + "\nDATA:" + javax.xml.bind.DatatypeConverter.printHexBinary(cipherBytes)
        //        + "\nKEY:" + javax.xml.bind.DatatypeConverter.printHexBinary(keybytes) 
        //        + "\nIV:" + javax.xml.bind.DatatypeConverter.printHexBinary(iv));
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  // AES/CFB/NoPadding  -- AES/CBC/PKCS5Padding
        //SecretKeySpec key = new SecretKeySpec(BasicSHA256.SHA256byte(encryptionKey), "AES");
        SecretKeySpec key = new SecretKeySpec(keybytes, "AES");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

        return new String(cipher.doFinal(cipherBytes), "UTF-8");
    }
}