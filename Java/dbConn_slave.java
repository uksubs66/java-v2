/***************************************************************************************
 *   dbConn_slave:  Make a connection to a specific slave database.
 *
 *   Called by:  stats servlet
 *               
 *
 *   Created:  6/25/2007
 *
 *
 *   Notes:  just used by stats now for making specific direct connections
 *
 *                  **** SEE PAUL BEFORE CHANGING THIS FILE!!! ****
 *
 *
 ***************************************************************************************
 */

import java.sql.*;


public class dbConn_slave {

/*
 static String [] save = new String [3];

 slave[1] = "jdbc:mysql://216.243.184.87";
 //slave[2] = "jdbc:mysql://216.243.184.119";
*/

 //************************************************************************
 //
 //  Get the connection to the slave database server
 //
 //************************************************************************

 public static Connection Connect(String club)
        throws Exception {

     return Connect(club, 1);

 }


 public static Connection Connect(String club, int slave_num)
        throws Exception {



   //String user = SystemUtils.db_user;
   //String password = SystemUtils.db_password;
   
   //String url = "jdbc:mysql://10.0.0.11/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true"; // PRIVATE INTERFACE TO SLAVE DB
   //String url = "jdbc:mysql://216.243.184.87/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true"; // PUBLIC INTERFACE TO SLAVE DB
   //String url = "jdbc:mysql://216.243.184.119/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true"; // PUBLIC INTERFACE TO SLAVE DB
   String [] slave = new String [3];

   slave[1] = "jdbc:mysql://216.243.184.87";
   slave[2] = "jdbc:mysql://216.243.184.119";

   String url = slave[slave_num] + "/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true";
   
   Connection con = null;
   
   try {
       
      Class.forName("com.mysql.jdbc.Driver").newInstance();
      
      con = DriverManager.getConnection(url, "copycat", "dupd0g");
      
   }
   catch (ClassNotFoundException e) {
   
      throw new Exception("ClassNotFound Error Getting Con: " + e.getMessage());
   }
   catch (SQLException e1) {
   
      throw new Exception("dbConn_slave Error Getting Con: " + e1.getMessage());
   }
   
   return(con);
   
 }

}  // end of dbConn_slave class
