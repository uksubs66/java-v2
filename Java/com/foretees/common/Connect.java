/***************************************************************************************
 *   Connect:  This servlet will provide connection objects to various db servers
 *
 *       called by:  dbConn, verifySlot
 *
 *
 *   created:  3/07/2006   Bob P.
 *
 *
 *   last updated:
 *
 *        3/15/10  Rewrote
 *        8/15/08  Change from synchronized to not.
 *
 *
 *   notes: As of 5/4/09 we were using url =
 *          "jdbc:mysql://10.0.0.10/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true";
 *
 *
 ***************************************************************************************
 */


package com.foretees.common;

import java.sql.*;


public class Connect {
   
   // CONNECTION SETTINGS
   private final static String DB_URL_READ = "jdbc:mysql://10.0.0.11/";
   private final static String DB_URL_READ_CRITICAL = "jdbc:mysql://10.0.0.10/";
   private final static String DB_URL_WRITE = "jdbc:mysql://10.0.0.10/";

   private final static String DB_PARMS = "?jdbcCompliantTruncation=false&autoReconnect=true";
   private final static String DB_USER = "bobp";
   private final static String DB_PASS = "rdp0805";


   // USED TO DETERMIN WHICH DB SERVER TO MAKE THE CONNECTION TO
   private final static int READ = 1;
   private final static int READ_CRITICAL = 2;
   private final static int WRITE = 3;





/**
 //**************************************
 //
 //  Return a connection to the database
 //
 //**************************************
 **/
 
 public static Connection getCon(String club, int queryType) {


   Connection con = null;

   try {

      Class.forName("com.mysql.jdbc.Driver").newInstance();

      if (queryType == WRITE || queryType == READ_CRITICAL) {

        con = DriverManager.getConnection(DB_URL_WRITE + club + DB_PARMS, DB_USER, DB_PASS);

      } else {

        con = DriverManager.getConnection(DB_URL_READ + club + DB_PARMS, DB_USER, DB_PASS);

      }

   } catch (Exception exc) {

      // BAD! RECURSIVE LOOP!!
      // Utilities.logError("Connect: club=" + club + ", err=" + exc.toString());
      Utilities.logErrorTxt("Connect: club=" + club + ", err=" + exc.toString(), ProcessConstants.REV);

   }

   return con;

 }


 public static Connection getCon(String club) {

     return getCon(club, WRITE);
 }

}  // end of Connect class

