/**
 **************************************************************************
 *   getSMTP:  This class will populate the parmSMTP paramter block object
 *
 *
 *   called by:  several servlets
 *
 *   created: 03/05/2008   Paul S.
 *
 *   last updated:
 * 
 *
 *
 **************************************************************************
 **/


package com.foretees.common;


import java.sql.*;


public class getSMTP {
    
  
/**
 **************************************************************************
 *
 *  Get the club parms
 *
 **************************************************************************
 **/
 
 public static void getParms(Connection con, parmSMTP parm)
         throws Exception {
 
    // the paramter block already contains the defaults for our mail server so
    // if the club has not fully defined their own external mail server then
    // it will default to our settings - query only returns something is fully defined
    
    try {
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("" +
                "SELECT " +
                    "smtp_addr, smtp_port, smtp_auth, smtp_user, smtp_pass, " +
                    "email_from, email_from_pro, email_from_mem " +
                "FROM club5 " +
                "WHERE " +
                    "smtp_addr <> '' AND smtp_port <> '' AND smtp_user <> '' AND smtp_pass <> '' AND " +
                    "email_from <> '' AND email_from_pro <> '' AND email_from_mem <> ''");

        if (rs.next()) {

            parm.SMTP_ADDR = rs.getString("smtp_addr");
            parm.SMTP_PORT = rs.getString("smtp_port");
            parm.SMTP_AUTH = (rs.getInt("smtp_auth") == 1);
            parm.SMTP_USER = rs.getString("smtp_user");
            parm.SMTP_PASS = rs.getString("smtp_pass");
            parm.EMAIL_FROM = rs.getString("email_from");
            parm.EMAIL_FROM_PRO = rs.getString("email_from_pro");
            parm.EMAIL_FROM_MEM = rs.getString("email_from_mem");

        }

        stmt.close();
    
   }
   catch (Exception e) {

      throw new Exception("Error loading email configuration - getSMTP.getParms, Error=" + e.getMessage());
   }
   
 }
    
}