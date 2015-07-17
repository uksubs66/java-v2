/***************************************************************************************     
 *   Support_report_allparms:  This servlet will process the 'Generate Reports' request from
 *                             Support's main page.  Report runs for ALL Clubs.
 *
 * 
 *     This will report the configuration parms for all clubs.  Always in EXCEL.
 * 
 *
 *   called by:  support_main.htm (calls doGet)
 *               self (calls doPost)
 *
 *   created: 4/07/2009   Bob P.
 *
 *   last updated:
 *
 *     5/05/10  Add access for sales users and option to list options for single club.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_report_allparms extends HttpServlet {
                
 String omit = "";

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 

 //****************************************************
 // Process the initial request - display a Report Menu
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession sess = verifyUser(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   String user = (String)sess.getAttribute("user");   // get username
   
   
   // 
   //  Display a menu of Reports for user to select from
   //
   out.println(SystemUtils.HeadTitle("Support Reports Page"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
   out.println("<font size=\"3\">");
   
   out.println("<br>ForeTees Configuration Report For All Clubs<br><br>");
   out.println("</font><font size=\"2\">");
   out.println("Select the desired report.<br><br>");

   out.println("<form action=\"Support_report_allparms\" method=\"post\" target=\"_blank\">");
   
   out.println("&nbsp;&nbsp;Display for this club only:&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"single\" value=\"1\"><br><br>");
   
   out.println("<input type=\"submit\" name=\"club\" value=\"Club Options Report\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br><br>");

   out.println("<input type=\"submit\" name=\"course\" value=\"Course Parameters Report\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br><br>");

   out.println("<input type=\"submit\" name=\"mship\" value=\"Membership Type Parameters Report\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br><br>");
   
   out.println("<input type=\"submit\" name=\"lottery\" value=\"Lottery Types Report\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br><br>");
   
   out.println("</form>");
   
   if (user.startsWith( "sales" )) {
      out.println("<A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
   } else {
      out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   }
   
   out.println("</center></body></html>");
   
 }



 //*******************************************************
 // Process the initial request from Support_main.htm page
 //
 //    also, doPost comes here!!!
 //*******************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession sess = verifyUser(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   Connection con = null;        
   Connection con2 = null;    
   
   int singleClub = 0;          // requested single club indicator

   
   String user = (String)sess.getAttribute("user");   // get username
   String club = (String)sess.getAttribute("club");

   if (req.getParameter("single") != null) singleClub = Integer.parseInt(req.getParameter("single"));  // request for single club?

   
   if (singleClub == 0) {              // if request for all clubs         

      try {

         con2 = dbConn.Connect(rev);          // get con to V5
      }
      catch (Exception exc) {
      }
   }

   try{

      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
    
   }
   catch (Exception exc) {
   }

   
   String repType = "Course";                     // default = course parms

   //
   // Get the parameters entered
   //
   if (req.getParameter("mship") != null) {              // Report for Mship Parms?           

      repType = "Membership";
   }

   if (req.getParameter("club") != null) {              // report for Club Parms              

      repType = "Club";
   }

   if (req.getParameter("course") != null) {              // report for Course Parms              

      repType = "Course";
   }

   if (req.getParameter("lottery") != null) {              // report for Lottery Types             

      repType = "Lottery";
   }

      
   if (repType.equals( "Course" )) {                  // if Course Parms Report

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" cellpadding=\"5\">");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Club Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Course Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>5-Somes</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>1st Tee Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Last Tee Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Interval Mins</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Alternate Mins</b></font></td>");
      out.println("</tr>");
      
   } else if (repType.equals( "Club" )) {                  // if Club Parms Report

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" cellpadding=\"5\">");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Club Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Rsync</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Multi</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Lottery</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hotel</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Dining</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Unaccomp</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Force Gst Names</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hide Mem Names</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Consec Mem</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Consec Pro</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b># Times Per Day</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Pre-Checkin</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>PoP</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hndcp System</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Post Scores</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hndcps - Pro Tee</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hndcps - Pro Evnt</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hndcps - Mem Tee</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hndcps - Mem Evnt</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>POS Type</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Pay Now</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b># of Xs</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>X Hrs</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Times Mem Can Play</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Hrs Betwn</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time Zone</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>PW Email</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Cutoff</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Cut Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Gst DB</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Notify</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Golf</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>FlxRez</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Web Caller</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Mobile</b></font></td>");    
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>BoxGroove</b></font></td>");    
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Evnt Signup</b></font></td>");    
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Lessons</b></font></td>");    
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Demo Club</b></font></td>");    
      out.println("</tr>");
      
   } else if (repType.equals( "Membership" )) {                  // if Membership (Dys in Adv) Parms Report

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" cellpadding=\"5\">");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Club Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Mship Type</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>T Flag</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Rounds Allowed</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Per</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Days To View</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Sun</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Mon</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Tue</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Wed</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Thu</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Fri</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Adv Sat</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Time</b></font></td>");
      out.println("</tr>");
      
   } else if (repType.equals( "Lottery" )) {                  // if Lottery Report

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"1\" cellpadding=\"5\">");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Club Name</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Proshop</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Random</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Weighted by Rounds</b></font></td>");
      out.println("<td align=\"center\"><font size=\"2\">");
      out.println("<b>Weighted by Proximity</b></font></td>");
      out.println("</tr>");
   } 
   

   try {

      if (singleClub == 0) {              // if request for all clubs         

         //
         //  Get each club in the system and get its parms
         //
         stmt = con2.createStatement();           

         rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive=0");     // get Active club names from V5 db

         while (rs.next()) {

            club = rs.getString(1);             // get a club name

            //  weed out the demo clubs, etc.

            boolean skip = false;

            if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
                 club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   

               skip = true;      // skip it
            }

            if (skip == false) {

               con = dbConn.Connect(club);          // get con to this club

               //
               //  get report data for this club based on report type requested
               //
               if (repType.equals( "Course" )) {                // if Course Parms Report

                  doCourseReport(club, out, con); 
                  
               } else if (repType.equals( "Club" )) {                  // if Course Parms Report

                  doClubReport(club, out, con); 
                  
               } else if (repType.equals( "Membership" )) {           // if Mship Parms Report

                  doMshipReport(club, out, con);      
                  
               } else if (repType.equals( "Lottery" )) {              // if Lottery Report

                  doLotteryReport(club, out, con);      
               }

               con.close();                           // close the connection to the club db
            }
         }                   // do all clubs

         stmt.close();
         con2.close();

      } else {      // request for this club only

         con = dbConn.Connect(club);          // get con to this club

         //
         //  get report data for this club based on report type requested
         //
         if (repType.equals( "Course" )) {                // if Course Parms Report

            doCourseReport(club, out, con);  
            
         } else if (repType.equals( "Club" )) {                  // if Course Parms Report

            doClubReport(club, out, con);  
            
         } else if (repType.equals( "Membership" )) {           // if Mship Parms Report

            doMshipReport(club, out, con);      
                  
         } else if (repType.equals( "Lottery" )) {              // if Lottery Report

            doLotteryReport(club, out, con);      
         }

         con.close();                           // close the connection to the club db
      }

   }
   catch (Exception exc) {

      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Error processing the clubs.");
      out.println("<BR><BR>Exception: " + exc.getMessage());
      
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR><A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
      } else {
         out.println("<BR><BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      }
      return;

   }   // end of member name list
      
   out.println("</table>");


 }  // end of doGet
 
 
 
 // *********************************************************
 //   Do course report
 // *********************************************************
 private void doCourseReport(String club, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   
   String course = "";
   int fives = 0;
   int fhr = 0;
   int fmin = 0;
   int lhr = 0;
   int lmin = 0;
   int betwn = 0;
   int alt = 0;
   

   //
   // use the con provided to gather the course parameters for this club and list them in the table
   //
   try {

      stmt = con.createStatement();    
      
      // get club5 parm values

      rs = stmt.executeQuery("SELECT courseName, first_hr, first_min, last_hr, last_min, betwn, alt, fives " +
                              "FROM clubparm2");         

      while (rs.next()) {

         course = rs.getString("courseName");         
         fhr = rs.getInt("first_hr");         
         fmin = rs.getInt("first_min");         
         lhr = rs.getInt("last_hr");         
         lmin = rs.getInt("last_min");         
         betwn = rs.getInt("betwn");         
         alt = rs.getInt("alt");         
         fives = rs.getInt("fives");         

         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println(club + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(course + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(fives + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(fhr + ":" + fmin + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(lhr + ":" + lmin + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(betwn + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(alt + "</font></td>");
         out.println("</tr>");         // end row for this club
      }
      
      stmt.close();

            
   }
   catch (Exception exc) {
   }

 }



 // *********************************************************
 //   Do club report
 // *********************************************************
 private void doClubReport(String club, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   
   //
   // use the con provided to gather the club parameters for this club and list them in the table
   //
   try {
    
      out.println("<tr>");         // start row for this club
      
      stmt = con.createStatement();           

      rs = stmt.executeQuery("SELECT * " +
                              "FROM club5");         

      if (rs.next()) {

         int multi = rs.getInt("multi");         
         int lottery = rs.getInt("lottery");         
         int x = rs.getInt("x");         
         int xhrs = rs.getInt("xhrs");         
         String zone = rs.getString("adv_zone");         
         int emailopt = rs.getInt("emailOpt");         
         int hotel = rs.getInt("hotel");         
         int userlock = rs.getInt("userlock");         
         int unagst = rs.getInt("unacompGuest");         
         int hndcpProSheet = rs.getInt("hndcpProSheet");         
         int hndcpProEvent = rs.getInt("hndcpProEvent");         
         int hndcpMemSheet = rs.getInt("hndcpMemSheet");         
         int hndcpMemEvent = rs.getInt("hndcpMemEvent");         
         String posType = rs.getString("posType");         
         int rndsperday = rs.getInt("rndsperday");         
         int hrsbtwn = rs.getInt("hrsbtwn");         
         int forcegnames = rs.getInt("forcegnames");         
         int hidenames = rs.getInt("hidenames");         
         int constimesm = rs.getInt("constimesm");         
         int constimesp = rs.getInt("constimesp");         
         int precheckin = rs.getInt("precheckin");         
         int pop = rs.getInt("paceofplay");         
         String hdcpSystem = rs.getString("hdcpSystem");         
         int allowMemPost = rs.getInt("allowMemPost");         
         int rsync = rs.getInt("rsync");         
         String emailPW = rs.getString("emailPass");         
         int max_originations = rs.getInt("max_originations");         
         int cutoffdays = rs.getInt("cutoffDays");         
         int cutofftime = rs.getInt("cutoffTime");         
         int pospaynow = rs.getInt("pos_paynow");         
         int dining = rs.getInt("dining");  
         
         int guestdb = rs.getInt("guestdb");  
         int nores = rs.getInt("no_reservations");         
         int golf = rs.getInt("foretees_mode");  
         int flxrez = rs.getInt("genrez_mode");  
         String seamless_caller = rs.getString("seamless_caller");  
         int mobile = rs.getInt("allow_mobile");  
         int boxgroove = rs.getInt("boxgroove");  
         
         if (!emailPW.equals( "" )) {        // if email PW provided
            
            emailPW = "Yes";                 // do not show it
            
         } else {
            
            emailPW = "No";
         }

         out.println("<td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println(club + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(rsync + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(multi + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(lottery + "</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hotel + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(dining + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(unagst + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(forcegnames + "</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hidenames + "</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(constimesm + "</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(constimesp + "</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(max_originations + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(precheckin + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(pop + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hdcpSystem + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(allowMemPost + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hndcpProSheet + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hndcpProEvent + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hndcpMemSheet + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hndcpMemEvent + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(posType + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(pospaynow + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(x + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(xhrs + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(rndsperday + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(hrsbtwn + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(zone + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(emailPW + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(cutoffdays + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(cutofftime + "</font></td>");       
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(guestdb + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(nores + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(golf + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(flxrez + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(seamless_caller + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(mobile + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(boxgroove + "</font></td>");
               
      }
      
      //  See if club uses Event Signup
      
      String yes_no = "No";
      
      rs = stmt.executeQuery("SELECT date " +
                              "FROM events2b WHERE signUp = 1");         

      if (rs.next()) {

         yes_no = "Yes";                
      }
      
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println(yes_no + "</font></td>");

      
      //  See if club uses Lesson Book
      
      yes_no = "No";
      int lcount = 0;
      
      rs = stmt.executeQuery("SELECT proid " +
                              "FROM lessonbook5 WHERE date > 0");         

      if (rs.next()) {

         yes_no = "Yes";                
      }
      
      if (yes_no.equals("Yes")) {
          
         rs = stmt.executeQuery("SELECT count(*) " +
                                "FROM lessonpro5 WHERE id > 0");    // get # of pros defined         

         if (rs.next()) {

             lcount = rs.getInt(1);
             
             yes_no = String.valueOf(lcount);  
         }   
      }
      
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println(yes_no + "</font></td>");

      
      //  See if club uses Demo Club Tracking
      
      yes_no = "No";
      
      rs = stmt.executeQuery("SELECT id " +
                              "FROM demo_clubs WHERE inact = 0");         

      if (rs.next()) {

         yes_no = "Yes";                
      }
      
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println(yes_no + "</font></td>");
      
      // DONE
      
      stmt.close();
    
      out.println("</tr>");        // end the row for this club
         
   }
   catch (Exception exc) {
   }

 }



 // *********************************************************
 //   Do mship report
 // *********************************************************
 private void doMshipReport(String club, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   
   //
   // use the con provided to gather the mship parameters
   //
   try {
    
      stmt = con.createStatement();           

      rs = stmt.executeQuery("SELECT * " +
                              "FROM mship5");         

      while (rs.next()) {

         String mship = rs.getString("mship");         
         int mtimes = rs.getInt("mtimes");         
         String period = rs.getString("period");         
         int days1 = rs.getInt("days1");         
         int days2 = rs.getInt("days2");         
         int days3 = rs.getInt("days3");         
         int days4 = rs.getInt("days4");         
         int days5 = rs.getInt("days5");         
         int days6 = rs.getInt("days6");         
         int days7 = rs.getInt("days7");         
         int advhr1 = rs.getInt("advhrd1");         
         int advmin1 = rs.getInt("advmind1");         
         String advam1 = rs.getString("advamd1");         
         int advhr2 = rs.getInt("advhrd2");         
         int advmin2 = rs.getInt("advmind2");         
         String advam2 = rs.getString("advamd2");         
         int advhr3 = rs.getInt("advhrd3");         
         int advmin3 = rs.getInt("advmind3");         
         String advam3 = rs.getString("advamd3");         
         int advhr4 = rs.getInt("advhrd4");         
         int advmin4 = rs.getInt("advmind4");         
         String advam4 = rs.getString("advamd4");         
         int advhr5 = rs.getInt("advhrd5");         
         int advmin5 = rs.getInt("advmind5");         
         String advam5 = rs.getString("advamd5");         
         int advhr6 = rs.getInt("advhrd6");         
         int advmin6 = rs.getInt("advmind6");         
         String advam6 = rs.getString("advamd6");         
         int advhr7 = rs.getInt("advhrd7");         
         int advmin7 = rs.getInt("advmind7");         
         String advam7 = rs.getString("advamd7");         
         int viewdays = rs.getInt("viewdays");         
         String tflag = rs.getString("tflag");         

         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println(club + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(mship + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(tflag + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(mtimes + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(period + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(viewdays + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days1 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr1 + ":" + advmin1 + " " + advam1 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days2 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr2 + ":" + advmin2 + " " + advam2 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days3 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr3 + ":" + advmin3 + " " + advam3 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days4 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr4 + ":" + advmin4 + " " + advam4 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days5 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr5 + ":" + advmin5 + " " + advam5 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days6 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr6 + ":" + advmin6 + " " + advam6 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(days7 + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(advhr7 + ":" + advmin7 + " " + advam7 + "</font></td>");
         out.println("</tr>");
         
      }
      stmt.close();
    
   }
   catch (Exception exc) {
   }

 }



 // *********************************************************
 //   Do mship report
 // *********************************************************
 private void doLotteryReport(String club, PrintWriter out, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   
   int lottery = 0;
   int proshop = 0;
   int random = 0;
   int wbr = 0;
   int wbp = 0;
   
  
   try {
    
      stmt = con.createStatement();           

      rs = stmt.executeQuery("SELECT lottery " +
                              "FROM club5");         

      if (rs.next()) {

         lottery = rs.getInt("lottery");         
      }
      
      if (lottery > 0) {           // if club uses the lottery
      
         rs = stmt.executeQuery("SELECT type " +
                                 "FROM lottery3");         

         while (rs.next()) {

            String ltype = rs.getString("type");    
            
            if (ltype.equalsIgnoreCase("Proshop")) {
               
               proshop++;
            
            } else if (ltype.equalsIgnoreCase("Random")) {
               
               random++;
            
            } else if (ltype.equalsIgnoreCase("WeightedBR")) {
               
               wbr++;
            
            } else if (ltype.equalsIgnoreCase("WeightedBP")) {
               
               wbp++;
            }            
         }

         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println(club + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(proshop + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(random + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(wbr + "</font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println(wbp + "</font></td>");
         out.println("</tr>");        
      }
      stmt.close();
    
   }
   catch (Exception exc) {
   }

 }



                               
                               
 // *********************************************************
 // Check for illegal access by user
 // *********************************************************

 private HttpSession verifyUser(HttpServletRequest verreq, PrintWriter out) {

   HttpSession session = null;

   String support = "support";

   //
   // Make sure user didn't enter illegally
   //
   session = verreq.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject

   } else {

      String user = (String)session.getAttribute("user");   // get username

      if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

         invalidUser(out);            // Intruder - reject
         session = null;
      }
   }
   return session;
 }
    
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("</CENTER></BODY></HTML>");

 }

}
