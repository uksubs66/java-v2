/***************************************************************************************
 *   dbConn:  Make a connection to the database.
 *
 *   called by:  Login
 *               SystemUtils
 *
 *
 *   created:  2/14/2005
 *
 *
 *   last updated:
 *
 *                  **** SEE PAUL BEFORE CHANGING THIS FILE!!! ****
 *
 *     3/15/10  Rewrote - now is just a shell that calls common/Connect
 *     2/20/05  Add exception processing to catch connection failures.
 *
 ***************************************************************************************
 */


import java.sql.*;

import com.foretees.common.Connect;


public class dbConn {


/**
 * Calls com.foretees.common.Connect and will return a connection to the 
 * database.  This method is now depreciated in favor of calling
 * com.foretees.common.Connect directly.
 *
 * @param club - Database name for the club
 */

 public static Connection Connect(String club)
        throws Exception {

     
     Connection con = null;
     
     con = Connect.getCon(club);
     
     return con;
     
/*
   String user = SystemUtils.db_user;
   String password = SystemUtils.db_password;      

   //
   // ***********************************************************************
   //  Change this url based on which server it is. !!!!!!!!!!!!!!!!!!!!!!!
   //
   //      see also com/foretees/common/Connect.java !!!!!!!!!!!!!
   //
   // ***********************************************************************
   //
   
   
   //
   // PRIVATE INTERFACE TO DB
   //
   // As of 5/4/09 we're using url = "jdbc:mysql://10.0.0.10/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true";
   // 
   // Others:
   // url = "jdbc:mysql://216.243.184.85/" + club; // PUBLIC INTERFACE TO DB
   // url = "jdbc:mysql://10.0.0.10/" + club + "?autoReconnect=true"; // used before jdbc changes
   // url = "jdbc:mysql://10.0.0.10/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true";
   // url = "jdbc:mysql://192.168.0.48/" + club;           // use this on the development cluster/workstation
   // url = "jdbc:mysql://localhost/" + club;        // use this on the DB Server and Local PC
   //
   String url = "jdbc:mysql://10.0.0.10/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true"; 

   Connection con = null;

   try {

      // Load the JDBC Driver

      // The newinstance() call is a work around for some Java implementations.

      //Class.forName("org.gjt.mm.mysql.Driver").newInstance();
      Class.forName("com.mysql.jdbc.Driver").newInstance();

      // Connect to the DB.........
      con = DriverManager.getConnection(url, user, password);
        
   }
   catch (ClassNotFoundException e) {

      throw new Exception("ClassNotFound Error Getting Con: " + e.getMessage());
   }
   catch (SQLException e1) {

      throw new Exception("dbConn Error Getting Con: " + e1.getMessage());
   }

   return(con);
*/

 }

}  // end of dbConn class

