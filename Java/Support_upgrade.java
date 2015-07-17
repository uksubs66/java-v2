/***************************************************************************************
 *   Support_upgrade:  This servlet will process the upgrade request from Support's Upgrade page.
 *
 *
 *       Adds a column to a db table, or a new table, for ALL clubs
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_upgrade extends HttpServlet {


 // Process the form request from support_upgrade.htm.....

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   Connection con2 = null;                  // init DB objects
   PreparedStatement pstmt = null;
   Statement stmt = null;
   Statement stmt2 = null;
   Statement stmt3 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String support = "support";             // valid username

   HttpSession session = null;


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String userName = (String)session.getAttribute("user");   // get username

   if (!userName.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   // Get the club names from the 'clubs' table
   //
   //  Process each club in the table
   //
   try {

      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         con = dbConn.Connect(club);                   // get a connection to this club's db

         //
         //  insert new fields in lessonbook5
         //
         stmt = con.createStatement();           // create a statement

   /*
         stmt.executeUpdate("" +
            "CREATE TABLE pos_hist ( " + 
              "pos_hist_id int(11) NOT NULL auto_increment, " +
              "date bigint NOT NULL DEFAULT '0', " +
              "time int NOT NULL DEFAULT '0', " +
              "course varchar(30) NOT NULL default ''," + 
              "fb smallint NOT NULL DEFAULT '0', " +
              "member_id varchar(15) NOT NULL default '', " +
              "player varchar(45) NOT NULL default '', " +
              "item_num varchar(30) NOT NULL default '', " +
              "item_name varchar(45) NOT NULL default '', " +
              "price varchar(8) NOT NULL default '', " +
              "PRIMARY KEY (pos_hist_id), " +
              "KEY date (date) " +
            ") ENGINE=MyISAM;");

        stmt.close();
   */


         stmt.executeUpdate("ALTER TABLE club5 ADD pos_paynow smallint NOT NULL DEFAULT '0'");

         stmt.close();

   /*
         //
         //  Set field to zero in all records
         //
         stmt = con.createStatement();           // create a statement

         stmt.executeUpdate("UPDATE club5 SET logins = 0");

         stmt.close();
   */
       
         con.close();                           // close the connection to the club db
      }                                         // do all clubs
      
      stmt2.close();                  // done - close out v5 club stmt & con
      con2.close();
      
   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Upgrade Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Upgrade Complete</H3>");
   out.println("<BR><BR>The upgrade is complete for all clubs.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/v5/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"/v5/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
