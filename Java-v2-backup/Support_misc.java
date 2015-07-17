/***************************************************************************************
 *   Support_misc: 
 *
 *    Use this file for miscellaneous queries of ALL clubs.
 * 
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_misc extends HttpServlet {


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   Connection con2 = null;                  // init DB objects
   PreparedStatement pstmt = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String support = "support";             // valid username

   HttpSession session = null;

   boolean found = false;
   boolean excel = false;                    // ************************** TOGGLE THIS AS DESIRED *************************
   
   
   
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
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
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
       out.println("<CENTER><H3>Query All Clubs For Missing Zip Code</H3>");
       out.println("<BR><BR>The following club is missing a zip code:<BR><BR>");
   }
   
   
   /*
   Calendar cal_date = new GregorianCalendar();             // get current date 
   int year = cal_date.get(Calendar.YEAR);
   int month = cal_date.get(Calendar.MONTH) + 1;
   int day = cal_date.get(Calendar.DAY_OF_MONTH);

   int today = (year * 10000) + (month * 100) + day;
    */
   
   
           
   //
   // Get the club names from the 'clubs' table
   //
   //  Process each club in the table
   //
   try {

      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname, fullname FROM clubs ORDER BY fullname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name
         clubName = rs2.getString(2);        
         
         con = dbConn.Connect(club);                   // get a connection to this club's db
         
         found = false;

         //
         //  insert new fields in lessonbook5
         //
         stmt = con.createStatement();           // create a statement

         rs = stmt.executeQuery("SELECT zipcode FROM club5 WHERE zipcode = ''");

         if (rs.next()) {

            out.println("Club = " +clubName+ ", Site = " +club+ "<br><br>");
            out.println("<form method=post><input type=hidden value=\"" +club+ "\" name=\"club\">");
            out.println("Enter the club's zip code here &nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"zipcode\" size=\"5\" maxlength=\"5\"><br>");
            out.println("<input type=submit value=\"Update\">");
            out.println("</form><br>");
            
            found = true;
         }
         stmt.close();
         con.close();                           // close the connection to the club db
         
         if (found == true) break;              // exit and wait if one found
         
      }                                         // check all clubs
      
      stmt2.close();                  // done - close out v5 club stmt & con
      con2.close();
      
   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<BR><BR>Unable to connect to the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (excel == false) {    

       out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();
   
 }   // end of doGet

 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;

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

   String club = req.getParameter("club").trim();         //  get parms passed
   String zipcode = req.getParameter("zipcode").trim();    
   
   if (!club.equals("") && !zipcode.equals("")) { 
       
       try {

          con = dbConn.Connect(club);                   // get a connection to this club's db
         
          //   Update the club5 table - add the zipcode
           
          pstmt = con.prepareStatement (
            "UPDATE club5 SET zipcode = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, zipcode);

          pstmt.executeUpdate();     // execute the prepared stmt

          pstmt.close();

          con.close();
       }
       catch (Exception e2) {

          // Error connecting to db....

          out.println("<BR><BR>Unable to connect to the DB - error2.");
          out.println("<BR>Exception: "+ e2.getMessage());
          out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
          out.println("</CENTER></BODY></HTML>");
          return;
       }
       
   } else {
       
       out.println("<BR><BR>Error - you must enter a zipcode.");
   }

   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_misc\">Check Another Club</A>");

   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return - Done</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   
 }    // end of doPost


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
