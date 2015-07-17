/***************************************************************************************
 *   Support_upgrade:  This servlet will process the upgrade request from Support's Upgrade page.
 *
 *   *********** refer to Support_updatedb for database updates !!! ********
 *   
 *    Use this file for miscellaneous queries of all clubs.
 * 
 * 
 *   ******  SEE ALSO SUPPORT_MISC *************
 * 
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

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
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


   boolean excel = true;                    // ************************** TOGGLE THIS AS DESIRED *************************
   
   
   
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
   String clubName = "";

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

   try{
      if (excel == true) {    

         resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
      }
   }
   catch (Exception exc) {
   }

   out.println("<HTML><HEAD><TITLE>Club Query</TITLE></HEAD>");
   out.println("<BODY>");
   if (excel == false) {
       out.println("<CENTER><H3>Query All Clubs For New Skin Date</H3>");
       out.println("<BR><BR>The following clubs are NOT using the New Skin:<BR><BR>");
   }
   out.println("<table>");
   out.println("<tr><td align=\"left\"><b>Club Name</b></td>");
   out.println("<td align=\"left\"><b>club</b></td>");
   out.println("<td align=\"left\"><b>New Skin Date</b></td>");
   out.println("</tr>");
    
   
   
   Calendar cal_date = new GregorianCalendar();             // get current date 
   int year = cal_date.get(Calendar.YEAR);
   int month = cal_date.get(Calendar.MONTH) + 1;
   int day = cal_date.get(Calendar.DAY_OF_MONTH);

   int today = (year * 10000) + (month * 100) + day;
   
   
   // today = 20120601;                           // ************************* TEMP ****************************
       
           
   //
   // Get the club names from the 'clubs' table
   //
   //  Process each club in the table
   //
   try {

      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname, fullname FROM clubs WHERE inactive = 0 ORDER BY fullname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name
         clubName = rs2.getString(2);        
         
         if (!club.startsWith("demo") && !club.startsWith("notify")) {

             con = dbConn.Connect(club);                   // get a connection to this club's db

             //
             //  insert new fields in lessonbook5
             //
             stmt = con.createStatement();           // create a statement

             rs = stmt.executeQuery("SELECT new_skin_date FROM club5");

             while (rs.next()) {

                 int new_skin_date = rs.getInt(1);      

                 if (new_skin_date > today && !club.startsWith("demo")) {

                   out.println("<tr><td align=\"left\">" +clubName+ "</td>");
                   out.println("<td align=\"left\">" +club+ "</td>");
                   out.println("<td align=\"left\">" +new_skin_date+ "</td></tr>");
                 }
             }
             stmt.close();

             con.close();                           // close the connection to the club db
         }
      }                                         // do all clubs
      
      stmt2.close();                  // done - close out v5 club stmt & con
      con2.close();
      
   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<BR><BR>Unable to connect to the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("</table>");
   
   if (excel == false) {    

       out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
   }
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
   out.println("<BR><BR>Please <A HREF=\"Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
