/***************************************************************************************
 *   Support_report_custom: 
 *
 *
 *     Use this to run a custom report.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;



public class Support_report_custom extends HttpServlet {


 //
 //   Get control from the Support menu
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    doPost(req, resp);    // future - change to prompt for date range!!!
 }
    
 
 //
 //  Generate the report
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt4 = null;


   String support = "support";             // valid username

   HttpSession session = null;
   
   int rsync = 0;


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
   String club = (String)session.getAttribute("club");   // get club name

   try {

      con = dbConn.Connect(club);
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
   

   int lotteries = 0;
   int teetimes = 0;
   int total = 0;
   int time = 0;
   int fb = 0;
   
   long date = 0;
   

   out.println("<HTML><HEAD><TITLE>Custom Report</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Tee Times vs Lottery Requests</H3>");

   //
   //   This report will determine how many of the tee times dusring a specified date and time range
   //   were originated as tee times versus how many were originated as lottery requests.
   //
   //   Get tee times for the date and time ranges
   //
   try {

      pstmt1 = con.prepareStatement (
            "SELECT date, time, fb " +
            "FROM teepast2 WHERE date > 20080431 AND date < 20081001 AND time > 929 AND time < 1201 AND day = 'Sunday' AND player1 != ''");

      pstmt1.clearParameters();        // clear the parms
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong(1);
         time = rs.getInt(2);
         fb = rs.getInt(3);

         //
         //  Check to see if there is a new history entry for this tee time
         //
         pstmt4 = con.prepareStatement (
            "SELECT day FROM teehist WHERE " +
            "date = ? AND time = ? AND fb = ? AND type = 0");

         pstmt4.clearParameters();
         pstmt4.setLong(1, date);
         pstmt4.setInt(2, time);
         pstmt4.setInt(3, fb);
         rs2 = pstmt4.executeQuery();

         if (rs2.next()) {

            teetimes++;    // if exists, then it was a tee time
            
         } else {
            
            lotteries++;   // if not, it was a lottery request
         }
         pstmt4.close();
                     
      }         
      pstmt1.close();
                     
      total = teetimes + lotteries;

      out.println("<br><br>May 1, 2008 - Sept 30, 2008 Sunday Morning Tee Times");               
      out.println("<br><br>Tee Times: " +teetimes+ ", Lottery Requests: " +lotteries+ ", Total: " +total);               
      
      
      //
      //  do the next season
      //
      teetimes = 0;
      lotteries = 0;
      total = 0;
   
      pstmt1 = con.prepareStatement (
            "SELECT date, time, fb " +
            "FROM teepast2 WHERE date > 20090431 AND date < 20091001 AND time > 929 AND time < 1201 AND day = 'Sunday' AND player1 != ''");

      pstmt1.clearParameters();        // clear the parms
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong(1);
         time = rs.getInt(2);
         fb = rs.getInt(3);

         //
         //  Check to see if there is a new history entry for this tee time
         //
         pstmt4 = con.prepareStatement (
            "SELECT day FROM teehist WHERE " +
            "date = ? AND time = ? AND fb = ? AND type = 0");

         pstmt4.clearParameters();
         pstmt4.setLong(1, date);
         pstmt4.setInt(2, time);
         pstmt4.setInt(3, fb);
         rs2 = pstmt4.executeQuery();

         if (rs2.next()) {

            teetimes++;    // if exists, then it was a tee time
            
         } else {
            
            lotteries++;   // if not, it was a lottery request
         }
         pstmt4.close();
      }         

      pstmt1.close();
                        
      total = teetimes + lotteries;

      out.println("<br><br>May 1, 2009 - Sept 30, 2009 Sunday Morning Tee Times");               
      out.println("<br><br>Tee Times: " +teetimes+ ", Lottery Requests: " +lotteries+ ", Total: " +total);               
                     
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      
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
