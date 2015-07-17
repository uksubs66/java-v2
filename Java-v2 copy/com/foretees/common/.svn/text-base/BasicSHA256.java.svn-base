/***************************************************************************************
 *   Purpose: This servlet will take an input string and return a SHA-256 hash of it.
 *
 *
 *   Called by:     called by Support_port_dining
 *
 *
 *   Created:       2/2/2011
 *
 *
 *   Notes: We'll use this class to generate passwords for the ForeTees Dining System.
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class BasicSHA256 {

    private static String convertToHex(byte[] data) {

        StringBuffer buf = new StringBuffer();

        for (int i = 0; i < data.length; i++) {

            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;

            do {

                if ((0 <= halfbyte) && (halfbyte <= 9)) {
                    buf.append((char) ('0' + halfbyte));
                } else {
                    buf.append((char) ('a' + (halfbyte - 10)));
                }

                halfbyte = data[i] & 0x0F;

            } while(two_halfs++ < 1);

        }

        return buf.toString();
    }

    public static String SHA256(String text)

    throws NoSuchAlgorithmException, UnsupportedEncodingException  {

        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        byte[] hash = new byte[40];
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        hash = md.digest();

        return convertToHex(hash);
    }


    public static byte[] SHA256byte(String text)

        throws NoSuchAlgorithmException, UnsupportedEncodingException  {

        MessageDigest md;
        md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());

        return md.digest();
        
    }


    //
    // Returns a randomly generated string to be used as a salt
    // The string returned will only use ascii chars 40-122
    //
    public static String getSalt(int length) {

        SecureRandom r = new SecureRandom();
        StringBuffer salt_key = new StringBuffer();

        do {

            salt_key.append((char)(40 + r.nextInt(82)));

        } while (salt_key.length() < length);

        return salt_key.toString();

     }
}
