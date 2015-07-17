/***************************************************************************************
 *   Support_kick_timers:  This class will restart a system timer when it has failed.
 *                         Also, call scanTee to build tee sheets for club.
 *
 *   called by:  support_main2.htm
 *
 *   created: 6/22/2006   Bob P.
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
//import com.foretees.common.FeedBack;
//import com.foretees.member.Member;
//import com.foretees.member.MemberHelper;


public class Support_kick_timers extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   Connection con = null;
   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String support = "support";

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");   // get club name

   if (!user.equals( support ) || club.equals( "" ) || club == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   //  Check for call to restart the tee time scan timer 
   //
   if (req.getParameter("timer2") != null) {

      startTimer2(req, out);             // restart the timer
      return;
   }

   //
   //  Check for call to restart the tee time scan timer
   //
   if (req.getParameter("timer60") != null) {

      startTimer60(req, out);             // restart the timer
      return;
   }

   //
   //  Check for call to build new tee sheets fro ALL clubs (call scanTee for each club)
   //
   if (req.getParameter("buildalltees") != null) {

      buildAllTees(req, out);             // go build tee sheets for each club
      return;
   }

   //
   //  Check for call to build new tee sheets (call scanTee)
   //
   if (req.getParameter("buildtees") != null) {

      try {
         con = dbConn.Connect(club);       // connect to Local db

      }
      catch (Exception exc) {

         // Error connecting to db....

         out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the DB.");
         out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      buildTees(req, out, club, con);             // go build tee sheets (call scanTee)
      return;
   }

   //
   //  Check for call to build new Activity sheets (call scanSheets)
   //
   if (req.getParameter("buildactsheets") != null) {
      
      try {
         con = dbConn.Connect(club);       // connect to Local db

      }
      catch (Exception exc) {

         // Error connecting to db....

         out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the DB.");
         out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      buildActs(req, out, club, con);             // go build activity sheets 
      return;
   }

   //
   //   Call is to display the server's current date and time. (not really used here - just a default)
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   java.util.Date date = cal.getTime();

    out.println("<HTML><HEAD><TITLE>Support Display Time</TITLE></HEAD>");
    out.println("<BODY><CENTER><H3>Display Date & Time</H3>");
    out.println("<BR><BR>The current date and time are:");
    out.println("<BR>"+ date);
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
    out.println("</CENTER></BODY></HTML>");
    return;
 }


 // ********************************************************************
 //  Process the request to restart the 2 minute timer to scan tee times
 // ********************************************************************

 private void startTimer2(HttpServletRequest req, PrintWriter out) {


   SystemUtils.inactTimer();

   //
   out.println("<HTML><HEAD><TITLE>Support Restart Timer</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Timer Restarted</H3><BR>");
   out.println("<BR><BR>The 2 Minute Timer to Scan Tee Times has been Restarted.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // ********************************************************************
 //  Process the request to restart the 60 minute timer to scan for X's
 // ********************************************************************

 private void startTimer60(HttpServletRequest req, PrintWriter out) {


   SystemUtils.xTimer();

   //
   out.println("<HTML><HEAD><TITLE>Support Restart Timer</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Timer Restarted</H3><BR>");
   out.println("<BR><BR>The 60 Minute Timer to Scan for Xs has been Restarted.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // ********************************************************************
 //  Process the request to build tee times (call scanTee)
 // ********************************************************************

 private void buildTees(HttpServletRequest req, PrintWriter out, String club, Connection con) {


   try {

      boolean b = SystemUtils.scanTee(con, club);                // build new tee sheets for each club

   }
   catch (Exception exc) {
   }

   //
   out.println("<HTML><HEAD><TITLE>Support Build Tee Sheets</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Tee Sheets Built</H3><BR>");
   out.println("<BR><BR>The tee sheets for club " +club+ " have been built.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }
 
 
 
 private void buildActs(HttpServletRequest req, PrintWriter out, String club, Connection con) {


   try {

      SystemUtils.scanSheets(con, club);             // go build Activity Sheets

   }
   catch (Exception exc) {
   }

   //
   out.println("<HTML><HEAD><TITLE>Support Build Activity Sheets</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Activity Sheets Built</H3><BR>");
   out.println("<BR><BR>The activity sheets for club " +club+ " have been built.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 
 // ********************************************************************
 //  Process the request to build tee times (call scanTee)
 // ********************************************************************

 private void buildAllTees(HttpServletRequest req, PrintWriter out) {


   Connection con = null;
   Connection con2 = null;
   Statement stmt = null;
   ResultSet rs = null;
   PreparedStatement pstmt = null;

   boolean b = false;

   //
   //  Perform timer function for each club in the system database 'clubs' table
   //
   String club = rev;                       // get db name to use for 'clubs' table

   try {
      con = dbConn.Connect(club);                       // get a connection
   }
   catch (Exception e1) {

      String errorMsg1 = "Error1 in SystemUtils teeTimer: ";
      errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

      SystemUtils.logError(errorMsg1);                                       // log it
   }

   //
   //  Get the current day
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int day_name = cal.get(Calendar.DAY_OF_WEEK);
   boolean doOptimize = (day_name == 4);


   //
   //  Get the current date from 1 year ago
   //
   int year = cal.get(Calendar.YEAR) - 1;
   int month = cal.get(Calendar.MONTH) +1;
   int daynum = cal.get(Calendar.DAY_OF_MONTH);
   long odate = (year * 10000) + (month * 100) + daynum;

   if (con != null) {

      //
      // Get the club names from the 'clubs' table
      //
      //  Process each club in the table
      //
      try {

         stmt = con.createStatement();              // create a statement

         rs = stmt.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

         while (rs.next()) {

            club = rs.getString(1);                 // get a club name

            try {

                con2 = dbConn.Connect(club);            // get a connection to this club's db

                if (con2 != null) {

                   b = SystemUtils.scanTee(con2, club);      // build new tee sheets for each club

                   con2.close();                           // close the connection to the club db

                } else {

                   SystemUtils.logError("Error3 in SystemUtils teeTimer: Connection failed to " +club);
                }

           }
           catch (Exception e1) {

               SystemUtils.logError("Error in SystemUtils teeTimer: club=" + club + " Exception:" + e1.getMessage());
           }

         } // end while all clubs loop
           
         stmt.close();

      }
      catch (Exception e2) {

         String errorMsg2 = "Error2 in SystemUtils teeTimer: ";
         errorMsg2 = errorMsg2 + e2.getMessage();                                // build error msg

         SystemUtils.logError(errorMsg2);                                       // log it
      }

      try {
         con.close();                              // close the connection to the system db
      }
      catch (Exception ignored) {
      }
        
   } // end if con != null

   out.println("<HTML><HEAD><TITLE>Support Build Tee Sheets</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Tee Sheets Built</H3><BR>");
   out.println("<BR><BR>The tee sheets for all clubs have been built.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }

}
