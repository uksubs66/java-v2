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
 *       11/16/12  Point connection objects to new master db server
 *       11/11/11  Added closeCon method
 *        4/27/11  Added getCaddieCon method
 *        4/14/11  Added testConnection and ensureConnection methods
 *        4/13/11  Added tcpKeepAlive=true option to the JDBC URL parameter list (requires Connector/J 5.0.7+)
 *       12/17/10  Added getDiningCon method for getting connections to the PostgeSQL server
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

import java.sql.*;          // mysql
import javax.sql.*;         // postgres
import javax.naming.*;


public class Connect {
   
   // CONNECTION SETTINGS
   private final static String DB_URL_READ = "jdbc:mysql://10.0.5.1/";
   private final static String DB_URL_READ_CRITICAL = "jdbc:mysql://10.0.5.1/";
   private final static String DB_URL_WRITE = "jdbc:mysql://10.0.5.1/";

   private final static String DB_PARMS = "?jdbcCompliantTruncation=false"; // &autoReconnect=true&tcpKeepAlive=true
   private final static String DB_USER = "bobp";
   private final static String DB_PASS = "rdp0805";


   // USED TO DETERMIN WHICH DB SERVER TO MAKE THE CONNECTION TO
   public final static int READ = 1;
   public final static int READ_CRITICAL = 2;
   public final static int WRITE = 3;





 /**
  * getCon - Returns a default connection to the MySQL database
  *
  * @param club Name of club (and database) to connect to
  * @param queryType Used to specify which database server the connection will be for
  *
  * @return Connection - Returns a connection object for the requested club (database)
  */
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

      Utilities.logErrorTxt("Connect: club=" + club + ", err=" + exc.toString(), ProcessConstants.REV);

   }

   return con;

 }


 /**
  * getCon - Returns the default (writable) connection to the MySQL database
  *
  * @param club Name of club (and database) to connect to
  *
  * @return Connection - Returns a connection object for the requested club (database)
  */
 public static Connection getCon(String club) {

     return getCon(club, WRITE);
 }



 /**
  * getDiningCon - Returns a connection to the PostgreSQL dining database
  *
  * @return Connection - Returns a connection object for the dining database server (PostgreSQL)
  */
 public static Connection getDiningCon() {
 
    Connection con_d = null;
    
    try {
        
        Context ctx = new InitialContext();
        if(ctx == null )
            throw new Exception("Boom - No Context");

        javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup("java:comp/env/jdbc/postgres");

        if (ds != null) con_d = ds.getConnection();

    } catch (Exception exc) {

        Utilities.logError("Connect.getDiningCon(): err=" + exc.toString());
        
    } finally { }

    return con_d;
    
 }


 /**
  * getCaddieCon - Returns a default connection to the Caddie MySQL database
  *
  * @param club Name of club (and database) to connect to
  * @param queryType Used to specify which database server the connection will be for
  *
  * @return Connection - Returns a connection object for the requested club (database)
  */
 public static Connection getCaddieCon(String club) {

   Connection c_con = null;

   try {

      Class.forName("com.mysql.jdbc.Driver").newInstance();

      // the user/pass needs to be the same as in /srv/www/vhosts/caddie.foretees.com/1/host_club_information.php
      c_con = DriverManager.getConnection("jdbc:mysql://10.0.0.40/" + club + "_caddie" + DB_PARMS, "caddyapp", "caddypass");

   } catch (Exception exc) {

      Utilities.logErrorTxt("Connect.getCaddieCon(): club=" + club + ", err=" + exc.toString(), ProcessConstants.REV);

   }

   return c_con;

 }


 /**
  * ensureConnection - Tests the current connection and returns true if connection is good
  *
  * @param con Existing connection to database to test
  *
  * @return Boolean - Returns true of ok false if bad
  */
 public static boolean testConnection(Connection con) {


   boolean result = false;

   Statement stmt = null;
   ResultSet rs = null;

   try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT DATABASE()");

      if (rs.next()) result = true;

   } catch (Exception exc) {

       //Utilities.logError("FATAL: testConnection - " + Utilities.getStackTraceAsString(exc));

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }

   return( result );

 }


 /**
  * ensureConnection - Tests the current connection and returns it or a new one if needed
  *
  * @param club Name of club (and database) to connect to
  * @param con Existing connection to database to test
  *
  * @return Connection - Returns a connection object for the requested club (database)
  */
 public static Connection ensureConnection(String club, Connection con) {


    // test conneciton and if it fails attempt to reconnect
    if (testConnection(con) == false) {

        // get new con for club
        con = getCon(club, WRITE);

        // test it again
        if (testConnection(con) == false) {

            try { con.close(); }
            catch (Exception ignore) {}

            return null;

        }

    }

    return con;

 }


 /**
  * closeCon - Closes the passed connection object
  *
  * @param con The existing connection to database to close
  *
  */
 public static void closeCon(Connection con) {

    if (con != null) {
        /*
        try { con.close(); }
        catch (Exception ignore) {}
        */
    }

 }
 
}  // end of Connect class

