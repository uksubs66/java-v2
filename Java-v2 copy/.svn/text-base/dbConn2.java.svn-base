/***************************************************************************************
 *   dbConn2:  Make a connection to the database.
 *
 *       called by:  Login
 *                   SystemUtils
 *
 *
 *   created:  2/14/2005
 *
 *
 *   last updated:
 *
 *     2/20/05  Add exception processing to catch connection failures.
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class dbConn2 {


/**
 //************************************************************************
 //
 //  Get the connection
 //
 //************************************************************************
 **/

 public synchronized static Connection Connect(String club)
        throws Exception {

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
  
   //String url = "jdbc:mysql://localhost/" + club;           // use this on the DB Server and Local PC
   //String url = "jdbc:mysql://192.168.0.48/" + club;           // use this on the development cluster

   //String url = "jdbc:mysql://10.0.0.10/" + club;           // PRIVATE INTERFACE TO DB
   String url = "jdbc:mysql://216.243.184.85/" + club;    // PUBLIC INTERFACE TO DB

   // + "?jdbcCompliantTruncation=false"

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
 }

}  // end of dbConn class

