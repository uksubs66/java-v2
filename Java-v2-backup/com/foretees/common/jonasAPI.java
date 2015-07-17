/***************************************************************************************
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 ***************************************************************************************/



package com.foretees.common;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.sql.Connection;

import org.apache.commons.lang.*;


public class jonasAPI {

    
    final static private String vendorcode = "1669042831"; // Vendor code – a unique code provided by CHO E3 Support
    
    
    public static String getRequestURL (String club, String user, String serviceURL, String pageName) {

        
        StringBuilder redirectString = new StringBuilder();
            
        try {
            
            // Assign Time – Number of milliseconds since Jan 1st, 1970
            String timeStamp = Long.toString(System.currentTimeMillis());
            //String vendorcode = "1669042831";
            // Assign member number used to define a user - this should be pulled from your local data 
            String userid = "0061"; 
            // Assign Page – (optional) the URL to redirect after successful log in
            String pageurl = "/Statements.aspx";
            // Load your private key - do NOT store the private key file in a web-accessible place
            InputStream inStream = new FileInputStream("/root/myrsakey.pkcs8"); 
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            int count;
            byte[] inputbuf = new byte[1024]; 
            while (true)
            {
             count = inStream.read(inputbuf); 
             if (count <= 0)
              {
                break;
              }
              outStream.write(inputbuf, 0, count); 
            }
            byte[] encodedKey = outStream.toByteArray(); 
            inStream.close();

            String value = "";

            try {

                KeySpec ks = new PKCS8EncodedKeySpec(encodedKey);
                PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(ks);
                StringBuilder dataForSignature = new StringBuilder(); 
                dataForSignature.append(timeStamp); 
                dataForSignature.append("|"); 
                dataForSignature.append(vendorcode); 
                dataForSignature.append("|"); 
                dataForSignature.append(user); 
                dataForSignature.append("|"); 
                dataForSignature.append(pageName);

                // or SHA1withRSA if you generated an RSA key
                Signature signWithRSA = Signature.getInstance("SHA1withRSA"); 
                signWithRSA.initSign(privateKey); 
                signWithRSA.update(dataForSignature.toString().getBytes("UTF-16LE"));

                byte[] signedData = signWithRSA.sign();
                value = new sun.misc.BASE64Encoder().encode(signedData);

            } catch (Exception exc) {
                Utilities.logError("jonasAPI.getRequestURL(): Error=" + exc.getMessage());
            }

            // build url to return
            redirectString.append(serviceURL); 
            redirectString.append("?time="); 
            redirectString.append(timeStamp);
            redirectString.append("&vendor=");
            redirectString.append(vendorcode);
            redirectString.append("&userid="); 
            redirectString.append(user); 
            redirectString.append("&page="); 
            redirectString.append(pageName); 
            redirectString.append("&value="); 
            redirectString.append(URLEncoder.encode(value,"UTF-8"));

        } catch (Exception exc) {

            Utilities.logError("jonasAPI.getRequestURL(): Error=" + exc.getMessage());
        }

        return redirectString.toString();
            
    } // end getRequestURL
    
    
    public static String scrubUser (String club, String user) {
        
        if (!StringUtils.isNumeric(user.substring(user.length() - 1))) {
            
            user = user.substring(0, user.length() - 1);
            
        }
        
        while (user.length() < 4) {
            
            user = "0" + user;
        }
        
        return user;
    }
    
    
    public static boolean allowAccountAccess (String club, String user, Connection con) {
        
        
        // try to stop dependants from accessing 
        // TODO: use con to look up the membership type and make determination from that
        if (!StringUtils.isNumeric(user.substring(user.length() - 1)) || user.substring(user.length() - 1).equalsIgnoreCase("a")) {
            
            return true;
            
        } else {
            
            return false;
        
        }
    }
    
}