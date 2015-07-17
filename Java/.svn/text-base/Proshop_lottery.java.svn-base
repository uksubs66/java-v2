/***************************************************************************************     
 *   Proshop_lottery:  This servlet will process the 'Lottery Setup' request from
 *                    the Proshop's Config page.
 *
 *
 *   called by:  Proshop menu (to doGet) and Proshop_lottery (to doPost from HTML built here)
 *
 *   created: 7/22/2003   Bob P.
 *
 *   last updated:
 *
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        8/15/08   Add more years to the start and end dates until we come up with a better method.
 *        8/11/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        4/22/08   Add mins-before and mins-after options (case #1459).
 *        2/16/08   Add help links for weighted lottery type definitions.
 *        6/22/06   Comment out the option to give preference to full groups (we don't do this).
 *        6/09/06   Added javascript confirmation to Delete button
 *        6/06/05   Add 'Weighted By Proximity' Lottery Type.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        5/12/04   Add sheet= parm for call from Proshop_sheet.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.dateOpts;
import com.foretees.common.Utilities;


public class Proshop_lottery extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //**************************************************
 // Process the initial request from Proshop menu
 //**************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }
     
    // Define some parms to use in the html
    //
   String name = "";       // name of lottery
   int s_hour = 0;         // start time hr
   int s_min = 0;          // start time min
   int e_hour = 0;         // end time hr
   int e_min = 0;          // end time min
   int s_year = 0;         // start year
   int s_month = 0;        // start month
   int s_day = 0;          // start day
   int e_year = 0;         // end year
   int e_month = 0;        // end month
   int e_day = 0;          // end day
   int lottery = 0;
   int multi = 0;             // day
     
   String recurr = "";     // recurrence
   String color = "";      // color for lottery displays
   String courseName = ""; // name of course
   String fb = "";         // Front/back Indicator
   String type = "";       // lottery type (random, weighted, proshop)
       
   String s_ampm = "";
   String e_ampm = "";

   boolean b = false;

   //
   //  First, see if lotteries are supported for this club
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, lottery " +
                             "FROM club5");

      if (rs.next()) {

         multi = rs.getInt(1);
         lottery = rs.getInt(2);

         if (lottery != 0) {

            b = true;          // lottery supported
         }
      }
      stmt.close();
   }
   catch (Exception ignore) {
   }


   if (b == false) {            // if not supported
     
      out.println(SystemUtils.HeadTitle("Procedure Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Procedure Error</H3>");
      out.println("<BR><BR>Lotteries are not currently supported for your club.");
      out.println("<BR>In order to configure Lotteries, you must first ");
      out.println("<BR>select lotteries in the Club Setup.<BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
     
   b = false;

   //
   //  Build the HTML page to display the existing lotteries
   //
   out.println(SystemUtils.HeadTitle("Proshop Lottery Setup Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
        
      out.println("<table cellpadding=\"8\" cellspacing=\"8\" bgcolor=\"#336633\" border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"3\">");
      out.println("<b>Lotteries</b><br>");
      out.println("</font>");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<br>To change or remove a lottery, click on the Select button within the lottery.");
      out.println("<br>");
      out.println("</td></tr></table>");
      out.println("<br><br>");

      out.println("<table border=\"2\" cellpadding=\"5\"><tr bgcolor=\"#F5F5DC\">");
      if (multi != 0) {           // if multiple courses supported for this club
         out.println("<td colspan=\"11\" align=\"center\">");
      } else {
         out.println("<td colspan=\"10\" align=\"center\">");
      }
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><b>Active Lotteries</b></p>");
      out.println("</font></td></tr>");
      out.println("<tr bgcolor=\"#F5F5DC\"><td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Lottery Name</b></p>");
      out.println("</font></td>");
      if (multi != 0) {
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p><b>Course</b></p>");
         out.println("</font></td>");
      }
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Tees</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Date</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Start Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>End Time</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Recurrence</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Type</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p><b>Color</b></p>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><p>&nbsp;</p>");      // empty for select button
      out.println("</font></td></tr>");

   //
   //  Get and display the existing lotteries (one table row per lottery)
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM lottery3 ORDER BY name");

      while (rs.next()) {

         b = true;                     // indicate lotteries exist

         name = rs.getString("name");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         s_hour = rs.getInt("start_hr");
         s_min = rs.getInt("start_min");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         e_hour = rs.getInt("end_hr");
         e_min = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         color = rs.getString("color");
         courseName = rs.getString("courseName");
         fb = rs.getString("fb");
         type = rs.getString("type");

         //
         //  some values must be converted for display
         //
         s_ampm = " AM";         // default to AM
         if (s_hour > 12) {
            s_ampm = " PM";
            s_hour = s_hour - 12;                // convert to 12 hr clock value
         }

         if (s_hour == 12) {
            s_ampm = " PM";
         }

         if (s_hour == 0) {
            s_ampm = " AM";
            s_hour = 12;         // 12 AM
         }

         e_ampm = " AM";         // default to AM
         if (e_hour > 12) {
            e_ampm = " PM";
            e_hour = e_hour - 12;                  // convert to 12 hr clock value
         }

         if (e_hour == 12) {
            e_ampm = " PM";
         }

         if (e_hour == 0) {
            e_ampm = " AM";
            e_hour = 12;         // 12 AM
         }


         if (color.equals( "Default" )) {

            out.println("<tr bgcolor=\"#F5F5DC\">");
         } else {
            out.println("<tr bgcolor=" + color + ">");
         }
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_lottery\" target=\"bot\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +name+ "</p>");
         out.println("</font></td>");
         if (multi != 0) {
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><p>" +courseName+ "</b></p>");
            out.println("</font></td>");
         }
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +fb+ "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +s_month+ "/" + s_day + "/" + s_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +e_month+ "/" + e_day + "/" + e_year + "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (s_min < 10) {
            out.println("<font size=\"2\"><p>" + s_hour + ":0" + s_min + "  " + s_ampm + "</p>");
         } else {
            out.println("<font size=\"2\"><p>" + s_hour + ":" + s_min + "  " + s_ampm + "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         if (e_min < 10) {
            out.println("<font size=\"2\"><p>" + e_hour + ":0" + e_min + "  " + e_ampm + "</b></p>");
         } else {
            out.println("<font size=\"2\"><p>" + e_hour + ":" + e_min + "  " + e_ampm + "</b></p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +recurr+ "</p>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (type.startsWith( "Weighted" )) {
            if (type.equals( "WeightedBR" )) {
               out.println("<p>Weighted<br>By Rounds</p>");
            } else {
               out.println("<p>Weighted<br>By Proximity</p>");
            }
         } else {
            out.println("<p>" +type+ "</p>");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><p>" +color+ "</p>");
         out.println("</font></td>");

         out.println("<input type=\"hidden\" maxlength=\"30\" name=\"name\" value=\"" + name + "\">");    // must pass whole name!!!!
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
         out.println("<td align=\"center\">");
         out.println("<p>");
         out.println("<input type=\"submit\" value=\"Select\">");
         out.println("</td></form></tr>");

      }  // end of while loop

      stmt.close();

      if (!b) {
        
         out.println("</font><font size=\"2\"><p>No Lotteries Currently Exist</p>");
      }
   }
   catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");

   }  // end of try

   //
   //  End of HTML page
   //
   out.println("</table></font>");                   // end of lottery table
   out.println("</td></tr></table>");                // end of main page table
        
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");

   out.println("</center></font></body></html>");



 }  // end of doGet

 //
 //****************************************************************
 // Process the form request from Proshop_lottery page displayed above.
 //
 // Use the name provided to locate the lottery record and then display
 // the record to the user and prompt for edit or delete action. 
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
     
   String [] month_table = { "inv", "JAN", "FEB", "MAR", "APR", "MAY", "JUN",
                             "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_LOTTERY", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_LOTTERY", out);
   }
     
   long s_date = 0;
   long e_date = 0;
   int s_year = 0;
   int s_month = 0;
   int s_day = 0;
   int e_year = 0;
   int e_month = 0;
   int e_day = 0;
   int shr = 0;
   int smin = 0;
   int ehr = 0;
   int emin = 0;
   int multi = 0;        // multiple course support option
   int index = 0;
   int sdays = 0;
   int sdtime = 0;
   int sd_hr = 0;
   int sd_min = 0;
   int edays = 0;
   int edtime = 0;
   int ed_hr = 0;
   int ed_min = 0;
   int pdays = 0;
   int ptime = 0;
   int p_hr = 0;
   int p_min = 0;
   int adays = 0;
   int wdpts = 0;
   int wepts = 0;
   int evpts = 0;
   int gpts = 0;
   int nopts = 0;
   int selection = 0;
   int slots = 0;
   int guest = 0;
   int players = 0;
   int members = 0;
   int oldpdays = 0;
   int oldptime = 0;
   int mins_before = 0;
   int mins_after = 0;
   int allow_mins = 0;

   String color = "";
   String recur = "";
   String fb = "";
   String type = "";
   String pref = "";
   String approve = "";
   String courseName2 = "";
   String sheet = "no";                 // default to NOT a call from Proshop_sheet

   //
   // Get the name parameter from the hidden input field
   //
   String name = req.getParameter("name");
   String courseName = req.getParameter("course");

   if (req.getParameter("sheet") != null) {

      sheet = req.getParameter("sheet");
   }

   String oldCourse = courseName;    // save course name in case it changes
   String oldName = name;                // save original event name

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();


   //
   // Get the lottery from the lottery table
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT * FROM lottery3 WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in stmt
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         //
         //  Found the lottery record - get it
         //
         s_date = rs.getLong("sdate");
         s_month = rs.getInt("start_mm");
         s_day = rs.getInt("start_dd");
         s_year = rs.getInt("start_yy");
         shr = rs.getInt("start_hr");
         smin = rs.getInt("start_min");
         e_date = rs.getLong("edate");
         e_month = rs.getInt("end_mm");
         e_day = rs.getInt("end_dd");
         e_year = rs.getInt("end_yy");
         ehr = rs.getInt("end_hr");
         emin = rs.getInt("end_min");
         recur = rs.getString("recurr");
         color = rs.getString("color");
         fb = rs.getString("fb");
         sdays = rs.getInt("sdays");            // days in advance to start taking requests
         sdtime = rs.getInt("sdtime");          // time of day to start
         sd_hr = rs.getInt("sd_hr");
         sd_min = rs.getInt("sd_min");
         edays = rs.getInt("edays");            // days in advance to stop taking requests
         edtime = rs.getInt("edtime");          // time of day to stop
         ed_hr = rs.getInt("ed_hr");
         ed_min = rs.getInt("ed_min");
         pdays = rs.getInt("pdays");            // days in advance to process requests
         ptime = rs.getInt("ptime");            // time of day to process lottery
         p_hr = rs.getInt("p_hr");
         p_min = rs.getInt("p_min");
         type = rs.getString("type");           // lottery type (random, weighted or proshop)
         adays = rs.getInt("adays");            // # of days to accumulate member's rounds/points
         wdpts = rs.getInt("wdpts");            // points to count for a weekday round
         wepts = rs.getInt("wepts");            // weekend points
         evpts = rs.getInt("evpts");            // event points
         gpts = rs.getInt("gpts");              // guest points
         nopts = rs.getInt("nopts");            // no-show  points
         selection = rs.getInt("selection");    // selection based on ?
         guest = rs.getInt("guest");            // guests count as
         slots = rs.getInt("slots");            // # of consecutive tee times member can request
         pref = rs.getString("pref");           // give preference to full groups (yes/no)
         approve = rs.getString("approve");     // review/approve tee times first (yes/no)
         members = rs.getInt("members");        // min # of members per request
         players = rs.getInt("players");        // min # of players per request
         mins_before = rs.getInt("minsbefore");   // minutes before for member requests
         mins_after = rs.getInt("minsafter");     // minutes after for member requests
         allow_mins = rs.getInt("allowmins");     // allow members to specify mins before & after

      } else {      // not found - try filtering the name

         name = SystemUtils.filter(name);
         oldName = name;                 // save original name

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);       // put the parm in stmt
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            //
            //  Found the lottery record - get it
            //
            s_date = rs.getLong("sdate");
            s_month = rs.getInt("start_mm");
            s_day = rs.getInt("start_dd");
            s_year = rs.getInt("start_yy");
            shr = rs.getInt("start_hr");
            smin = rs.getInt("start_min");
            e_date = rs.getLong("edate");
            e_month = rs.getInt("end_mm");
            e_day = rs.getInt("end_dd");
            e_year = rs.getInt("end_yy");
            ehr = rs.getInt("end_hr");
            emin = rs.getInt("end_min");
            recur = rs.getString("recurr");
            color = rs.getString("color");
            fb = rs.getString("fb");
            sdays = rs.getInt("sdays");            // days in advance to start taking requests
            sdtime = rs.getInt("sdtime");          // time of day to start
            sd_hr = rs.getInt("sd_hr");
            sd_min = rs.getInt("sd_min");
            edays = rs.getInt("edays");            // days in advance to stop taking requests
            edtime = rs.getInt("edtime");          // time of day to stop
            ed_hr = rs.getInt("ed_hr");
            ed_min = rs.getInt("ed_min");
            pdays = rs.getInt("pdays");            // days in advance to process requests
            ptime = rs.getInt("ptime");            // time of day to process lottery
            p_hr = rs.getInt("p_hr");
            p_min = rs.getInt("p_min");
            type = rs.getString("type");           // lottery type (random, weighted or proshop)
            adays = rs.getInt("adays");            // # of days to accumulate member's rounds/points
            wdpts = rs.getInt("wdpts");            // points to count for a weekday round
            wepts = rs.getInt("wepts");            // weekend points
            evpts = rs.getInt("evpts");            // event points
            gpts = rs.getInt("gpts");              // guest points
            nopts = rs.getInt("nopts");            // no-show  points
            selection = rs.getInt("selection");    // selection based on ?
            guest = rs.getInt("guest");            // guests count as
            slots = rs.getInt("slots");            // # of consecutive tee times member can request
            pref = rs.getString("pref");           // give preference to full groups (yes/no)
            approve = rs.getString("approve");     // review/approve tee times first (yes/no)
            members = rs.getInt("members");        // min # of members per request
            players = rs.getInt("players");        // min # of players per request
            mins_before = rs.getInt("minsbefore");   // minutes before for member requests
            mins_after = rs.getInt("minsafter");     // minutes after for member requests
            allow_mins = rs.getInt("allowmins");     // allow members to specify mins before & after
         }
      }
      pstmt.close();              // close the stmt

      //
      //  Save the current processing days and time for edit 
      //
      oldpdays = pdays;
      oldptime = ptime;

      //
      //  check if multi-couse is yes
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }

      stmt.close();

      if (multi != 0) {
         
         course = Utilities.getCourseNames(con);     // get all the course names
      }                                         
   }
   catch (Exception exc) {

      dbError(out);
      return;
   }

   int sampm = 0;
   int eampm = 0;
   int sdampm = 0;
   int edampm = 0;
   int pampm = 0;

   if (shr > 12) {
      
      shr = shr - 12;        // convert to 12 hour value
      sampm = 12;
   }

   if (shr == 12) {
      sampm = 12;
   }

   if (ehr > 12) {

      ehr = ehr - 12;        // convert to 12 hour value
      eampm = 12;
   }

   if (ehr == 12) {
      eampm = 12;
   }

   if (sd_hr > 12) {

      sd_hr = sd_hr - 12;        // convert to 12 hour value
      sdampm = 12;
   }

   if (sd_hr == 12) {
      sdampm = 12;
   }

   if (ed_hr > 12) {

      ed_hr = ed_hr - 12;        // convert to 12 hour value
      edampm = 12;
   }

   if (ed_hr == 12) {
      edampm = 12;
   }

   if (p_hr > 12) {

      p_hr = p_hr - 12;        // convert to 12 hour value
      pampm = 12;
   }

   if (p_hr == 12) {
      pampm = 12;
   }

   String alphaSmonth = month_table[s_month];  // get name for start month
   String alphaEmonth = month_table[e_month];  // get name for end month

   //
   // Database record found - output an edit page
   //
   out.println(SystemUtils.HeadTitle("Proshop Edit Lottery"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Edit Lottery</b><br>");
      out.println("<br>Change the desired information for the lottery below.<br>");
      out.println("Click on <b>Update</b> to submit the changes.");
      out.println("<br>Click on <b>Remove</b> to delete the lottery.");
      out.println("</font></td></tr></table><br>");

      out.println("<table border=\"1\" cellpadding=\"8\" cellspacing=\"8\" bgcolor=\"#F5F5DC\">");
         if (sheet.equals( "yes" )) {
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_editlottery\" method=\"post\">");
         } else {
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_editlottery\" method=\"post\" target=\"bot\">");
         }
            out.println("<tr><td width=\"580\">");
               out.println("<font size=\"2\">");
               out.println("<p align=\"left\">&nbsp;&nbsp;Lottery name:&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"lottery_name\" value=\"" +name+ "\" size=\"30\" maxlength=\"30\">");
               out.println("</input><br>&nbsp;&nbsp;&nbsp;* Must be unique");
               out.println("<br><br>");

            if (multi != 0) {

               out.println("&nbsp;&nbsp;Select a Course:&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"course\">");

               if (courseName.equals( "-ALL-" )) {             // if same as existing name

                  out.println("<option selected value=\"-ALL-\">ALL</option>");
               } else {
                  out.println("<option value=\"-ALL-\">ALL</option>");
               }

               for (index=0; index < course.size(); index++) {

                  if (course.get(index).equals( courseName )) {             // if same as existing name

                     out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                  } else {
                     out.println("<option value=\"" + course.get(index) + "\">" + course.get(index) + "</option>");
                  }
               }
               out.println("</select>");
               out.println("<br><br>");

            } else {

               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
            }
              out.println("&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"fb\">");
                if (fb.equals( "Both" )) {
                   out.println("<option selected value=\"Both\">Both</option>");
                } else {
                   out.println("<option value=\"Both\">Both</option>");
                }
                if (fb.equals( "Front" )) {
                   out.println("<option selected value=\"Front\">Front</option>");
                } else {
                   out.println("<option value=\"Front\">Front</option>");
                }
                if (fb.equals( "Back" )) {
                   out.println("<option selected value=\"Back\">Back</option>");
                } else {
                   out.println("<option value=\"Back\">Back</option>");
                }
              out.println("</select><br><br>");
                
               out.println("&nbsp;&nbsp;Start Date:&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"smonth\">");
                   if (s_month == 1) {
                      out.println("<option selected value=\"01\">JAN</option>");
                   } else {
                      out.println("<option value=\"01\">JAN</option>");
                   }
                   if (s_month == 2) {
                      out.println("<option selected value=\"02\">FEB</option>");
                   } else {
                      out.println("<option value=\"02\">FEB</option>");
                   }
                   if (s_month == 3) {
                      out.println("<option selected value=\"03\">MAR</option>");
                   } else {
                      out.println("<option value=\"03\">MAR</option>");
                   }
                   if (s_month == 4) {
                      out.println("<option selected value=\"04\">APR</option>");
                   } else {
                      out.println("<option value=\"04\">APR</option>");
                   }
                   if (s_month == 5) {
                      out.println("<option selected value=\"05\">MAY</option>");
                   } else {
                      out.println("<option value=\"05\">MAY</option>");
                   }
                   if (s_month == 6) {
                      out.println("<option selected value=\"06\">JUN</option>");
                   } else {
                      out.println("<option value=\"06\">JUN</option>");
                   }
                   if (s_month == 7) {
                      out.println("<option selected value=\"07\">JUL</option>");
                   } else {
                      out.println("<option value=\"07\">JUL</option>");
                   }
                   if (s_month == 8) {
                      out.println("<option selected value=\"08\">AUG</option>");
                   } else {
                      out.println("<option value=\"08\">AUG</option>");
                   }
                   if (s_month == 9) {
                      out.println("<option selected value=\"09\">SEP</option>");
                   } else {
                      out.println("<option value=\"09\">SEP</option>");
                   }
                   if (s_month == 10) {
                      out.println("<option selected value=\"10\">OCT</option>");
                   } else {
                      out.println("<option value=\"10\">OCT</option>");
                   }
                   if (s_month == 11) {
                      out.println("<option selected value=\"11\">NOV</option>");
                   } else {
                      out.println("<option value=\"11\">NOV</option>");
                   }
                   if (s_month == 12) {
                      out.println("<option selected value=\"12\">DEC</option>");
                   } else {
                      out.println("<option value=\"12\">DEC</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"sday\">");
                   if (s_day == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (s_day == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (s_day == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (s_day == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (s_day == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (s_day == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (s_day == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (s_day == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (s_day == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (s_day == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (s_day == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (s_day == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (s_day == 13) {
                      out.println("<option selected value=\"13\">13</option>");
                   } else {
                      out.println("<option value=\"13\">13</option>");
                   }
                   if (s_day == 14) {
                      out.println("<option selected value=\"14\">14</option>");
                   } else {
                      out.println("<option value=\"14\">14</option>");
                   }
                   if (s_day == 15) {
                      out.println("<option selected value=\"15\">15</option>");
                   } else {
                      out.println("<option value=\"15\">15</option>");
                   }
                   if (s_day == 16) {
                      out.println("<option selected value=\"16\">16</option>");
                   } else {
                      out.println("<option value=\"16\">16</option>");
                   }
                   if (s_day == 17) {
                      out.println("<option selected value=\"17\">17</option>");
                   } else {
                      out.println("<option value=\"17\">17</option>");
                   }
                   if (s_day == 18) {
                      out.println("<option selected value=\"18\">18</option>");
                   } else {
                      out.println("<option value=\"18\">18</option>");
                   }
                   if (s_day == 19) {
                      out.println("<option selected value=\"19\">19</option>");
                   } else {
                      out.println("<option value=\"19\">19</option>");
                   }
                   if (s_day == 20) {
                      out.println("<option selected value=\"20\">20</option>");
                   } else {
                      out.println("<option value=\"20\">20</option>");
                   }
                   if (s_day == 21) {
                      out.println("<option selected value=\"21\">21</option>");
                   } else {
                      out.println("<option value=\"21\">21</option>");
                   }
                   if (s_day == 22) {
                      out.println("<option selected value=\"22\">22</option>");
                   } else {
                      out.println("<option value=\"22\">22</option>");
                   }
                   if (s_day == 23) {
                      out.println("<option selected value=\"23\">23</option>");
                   } else {
                      out.println("<option value=\"23\">23</option>");
                   }
                   if (s_day == 24) {
                      out.println("<option selected value=\"24\">24</option>");
                   } else {
                      out.println("<option value=\"24\">24</option>");
                   }
                   if (s_day == 25) {
                      out.println("<option selected value=\"25\">25</option>");
                   } else {
                      out.println("<option value=\"25\">25</option>");
                   }
                   if (s_day == 26) {
                      out.println("<option selected value=\"26\">26</option>");
                   } else {
                      out.println("<option value=\"26\">26</option>");
                   }
                   if (s_day == 27) {
                      out.println("<option selected value=\"27\">27</option>");
                   } else {
                      out.println("<option value=\"27\">27</option>");
                   }
                   if (s_day == 28) {
                      out.println("<option selected value=\"28\">28</option>");
                   } else {
                      out.println("<option value=\"28\">28</option>");
                   }
                   if (s_day == 29) {
                      out.println("<option selected value=\"29\">29</option>");
                   } else {
                      out.println("<option value=\"29\">29</option>");
                   }
                   if (s_day == 30) {
                      out.println("<option selected value=\"30\">30</option>");
                   } else {
                      out.println("<option value=\"30\">30</option>");
                   }
                   if (s_day == 31) {
                      out.println("<option selected value=\"31\">31</option>");
                   } else {
                      out.println("<option value=\"31\">31</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"syear\">");
                   if (s_year == 2003) {
                      out.println("<option selected value=\"2003\">2003</option>");
                   } else {
                      out.println("<option value=\"2003\">2003</option>");
                   }
                   if (s_year == 2004) {
                      out.println("<option selected value=\"2004\">2004</option>");
                   } else {
                      out.println("<option value=\"2004\">2004</option>");
                   }
                   if (s_year == 2005) {
                      out.println("<option selected value=\"2005\">2005</option>");
                   } else {
                      out.println("<option value=\"2005\">2005</option>");
                   }
                   if (s_year == 2006) {
                      out.println("<option selected value=\"2006\">2006</option>");
                   } else {
                      out.println("<option value=\"2006\">2006</option>");
                   }
                   if (s_year == 2007) {
                      out.println("<option selected value=\"2007\">2007</option>");
                   } else {
                      out.println("<option value=\"2007\">2007</option>");
                   }
                   if (s_year == 2008) {
                      out.println("<option selected value=\"2008\">2008</option>");
                   } else {
                      out.println("<option value=\"2008\">2008</option>");
                   }
                   if (s_year == 2009) {
                      out.println("<option selected value=\"2009\">2009</option>");
                   } else {
                      out.println("<option value=\"2009\">2009</option>");
                   }
                   if (s_year == 2010) {
                      out.println("<option selected value=\"2010\">2010</option>");
                   } else {
                      out.println("<option value=\"2010\">2010</option>");
                   }
                   if (s_year == 2011) {
                      out.println("<option selected value=\"2011\">2011</option>");
                   } else {
                      out.println("<option value=\"2011\">2011</option>");
                   }
                   if (s_year == 2012) {
                      out.println("<option selected value=\"2012\">2012</option>");
                   } else {
                      out.println("<option value=\"2012\">2012</option>");
                   }
                   if (s_year == 2013) {
                      out.println("<option selected value=\"2013\">2013</option>");
                   } else {
                      out.println("<option value=\"2013\">2013</option>");
                   }
                   if (s_year == 2014) {
                      out.println("<option selected value=\"2014\">2014</option>");
                   } else {
                      out.println("<option value=\"2014\">2014</option>");
                   }
                 out.println("</select><br><br>");
               out.println("&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;&nbsp;");
                 out.println("Month:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"emonth\">");
                   if (e_month == 1) {
                      out.println("<option selected value=\"01\">JAN</option>");
                   } else {
                      out.println("<option value=\"01\">JAN</option>");
                   }
                   if (e_month == 2) {
                      out.println("<option selected value=\"02\">FEB</option>");
                   } else {
                      out.println("<option value=\"02\">FEB</option>");
                   }
                   if (e_month == 3) {
                      out.println("<option selected value=\"03\">MAR</option>");
                   } else {
                      out.println("<option value=\"03\">MAR</option>");
                   }
                   if (e_month == 4) {
                      out.println("<option selected value=\"04\">APR</option>");
                   } else {
                      out.println("<option value=\"04\">APR</option>");
                   }
                   if (e_month == 5) {
                      out.println("<option selected value=\"05\">MAY</option>");
                   } else {
                      out.println("<option value=\"05\">MAY</option>");
                   }
                   if (e_month == 6) {
                      out.println("<option selected value=\"06\">JUN</option>");
                   } else {
                      out.println("<option value=\"06\">JUN</option>");
                   }
                   if (e_month == 7) {
                      out.println("<option selected value=\"07\">JUL</option>");
                   } else {
                      out.println("<option value=\"07\">JUL</option>");
                   }
                   if (e_month == 8) {
                      out.println("<option selected value=\"08\">AUG</option>");
                   } else {
                      out.println("<option value=\"08\">AUG</option>");
                   }
                   if (e_month == 9) {
                      out.println("<option selected value=\"09\">SEP</option>");
                   } else {
                      out.println("<option value=\"09\">SEP</option>");
                   }
                   if (e_month == 10) {
                      out.println("<option selected value=\"10\">OCT</option>");
                   } else {
                      out.println("<option value=\"10\">OCT</option>");
                   }
                   if (e_month == 11) {
                      out.println("<option selected value=\"11\">NOV</option>");
                   } else {
                      out.println("<option value=\"11\">NOV</option>");
                   }
                   if (e_month == 12) {
                      out.println("<option selected value=\"12\">DEC</option>");
                   } else {
                      out.println("<option value=\"12\">DEC</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eday\">");
                   if (e_day == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (e_day == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (e_day == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (e_day == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (e_day == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (e_day == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (e_day == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (e_day == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (e_day == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (e_day == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (e_day == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (e_day == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (e_day == 13) {
                      out.println("<option selected value=\"13\">13</option>");
                   } else {
                      out.println("<option value=\"13\">13</option>");
                   }
                   if (e_day == 14) {
                      out.println("<option selected value=\"14\">14</option>");
                   } else {
                      out.println("<option value=\"14\">14</option>");
                   }
                   if (e_day == 15) {
                      out.println("<option selected value=\"15\">15</option>");
                   } else {
                      out.println("<option value=\"15\">15</option>");
                   }
                   if (e_day == 16) {
                      out.println("<option selected value=\"16\">16</option>");
                   } else {
                      out.println("<option value=\"16\">16</option>");
                   }
                   if (e_day == 17) {
                      out.println("<option selected value=\"17\">17</option>");
                   } else {
                      out.println("<option value=\"17\">17</option>");
                   }
                   if (e_day == 18) {
                      out.println("<option selected value=\"18\">18</option>");
                   } else {
                      out.println("<option value=\"18\">18</option>");
                   }
                   if (e_day == 19) {
                      out.println("<option selected value=\"19\">19</option>");
                   } else {
                      out.println("<option value=\"19\">19</option>");
                   }
                   if (e_day == 20) {
                      out.println("<option selected value=\"20\">20</option>");
                   } else {
                      out.println("<option value=\"20\">20</option>");
                   }
                   if (e_day == 21) {
                      out.println("<option selected value=\"21\">21</option>");
                   } else {
                      out.println("<option value=\"21\">21</option>");
                   }
                   if (e_day == 22) {
                      out.println("<option selected value=\"22\">22</option>");
                   } else {
                      out.println("<option value=\"22\">22</option>");
                   }
                   if (e_day == 23) {
                      out.println("<option selected value=\"23\">23</option>");
                   } else {
                      out.println("<option value=\"23\">23</option>");
                   }
                   if (e_day == 24) {
                      out.println("<option selected value=\"24\">24</option>");
                   } else {
                      out.println("<option value=\"24\">24</option>");
                   }
                   if (e_day == 25) {
                      out.println("<option selected value=\"25\">25</option>");
                   } else {
                      out.println("<option value=\"25\">25</option>");
                   }
                   if (e_day == 26) {
                      out.println("<option selected value=\"26\">26</option>");
                   } else {
                      out.println("<option value=\"26\">26</option>");
                   }
                   if (e_day == 27) {
                      out.println("<option selected value=\"27\">27</option>");
                   } else {
                      out.println("<option value=\"27\">27</option>");
                   }
                   if (e_day == 28) {
                      out.println("<option selected value=\"28\">28</option>");
                   } else {
                      out.println("<option value=\"28\">28</option>");
                   }
                   if (e_day == 29) {
                      out.println("<option selected value=\"29\">29</option>");
                   } else {
                      out.println("<option value=\"29\">29</option>");
                   }
                   if (e_day == 30) {
                      out.println("<option selected value=\"30\">30</option>");
                   } else {
                      out.println("<option value=\"30\">30</option>");
                   }
                   if (e_day == 31) {
                      out.println("<option selected value=\"31\">31</option>");
                   } else {
                      out.println("<option value=\"31\">31</option>");
                   }
                 out.println("</select>");

                 out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"eyear\">");
                   if (e_year == 2003) {
                      out.println("<option selected value=\"2003\">2003</option>");
                   } else {
                      out.println("<option value=\"2003\">2003</option>");
                   }
                   if (e_year == 2004) {
                      out.println("<option selected value=\"2004\">2004</option>");
                   } else {
                      out.println("<option value=\"2004\">2004</option>");
                   }
                   if (e_year == 2005) {
                      out.println("<option selected value=\"2005\">2005</option>");
                   } else {
                      out.println("<option value=\"2005\">2005</option>");
                   }
                   if (e_year == 2006) {
                      out.println("<option selected value=\"2006\">2006</option>");
                   } else {
                      out.println("<option value=\"2006\">2006</option>");
                   }
                   if (e_year == 2007) {
                      out.println("<option selected value=\"2007\">2007</option>");
                   } else {
                      out.println("<option value=\"2007\">2007</option>");
                   }
                   if (e_year == 2008) {
                      out.println("<option selected value=\"2008\">2008</option>");
                   } else {
                      out.println("<option value=\"2008\">2008</option>");
                   }
                   if (e_year == 2009) {
                      out.println("<option selected value=\"2009\">2009</option>");
                   } else {
                      out.println("<option value=\"2009\">2009</option>");
                   }
                   if (e_year == 2010) {
                      out.println("<option selected value=\"2010\">2010</option>");
                   } else {
                      out.println("<option value=\"2010\">2010</option>");
                   }
                   if (e_year == 2011) {
                      out.println("<option selected value=\"2011\">2011</option>");
                   } else {
                      out.println("<option value=\"2011\">2011</option>");
                   }
                   if (e_year == 2012) {
                      out.println("<option selected value=\"2012\">2012</option>");
                   } else {
                      out.println("<option value=\"2012\">2012</option>");
                   }
                   if (e_year == 2013) {
                      out.println("<option selected value=\"2013\">2013</option>");
                   } else {
                      out.println("<option value=\"2013\">2013</option>");
                   }
                   if (e_year == 2014) {
                      out.println("<option selected value=\"2014\">2014</option>");
                   } else {
                      out.println("<option value=\"2014\">2014</option>");
                   }
                 out.println("</select><br><br>");
               out.println("&nbsp;&nbsp;Start Time:");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"start_hr\">");
                   if (shr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (shr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (shr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (shr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (shr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (shr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (shr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (shr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (shr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (shr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (shr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (shr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (shr == 0) {
                      out.println("<option selected value=\"0\">12</option>");
                   } else {
                      out.println("<option value=\"0\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (smin < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + smin + " name=\"start_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + smin + " name=\"start_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"start_ampm\">");
                   if (sampm == 00) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (sampm == 12) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select><br><br>");
               out.println("&nbsp;&nbsp;End Time:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"end_hr\">");
                   if (ehr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (ehr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (ehr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (ehr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (ehr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (ehr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (ehr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (ehr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (ehr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (ehr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (ehr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (ehr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (ehr == 0) {
                      out.println("<option selected value=\"0\">12</option>");
                   } else {
                      out.println("<option value=\"0\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (emin < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + emin + " name=\"end_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + emin + " name=\"end_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"end_ampm\">");
                   if (eampm == 00) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (eampm == 12) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select><br><br>");
                 out.println("&nbsp;&nbsp;Recurrence:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"recurr\">");
                if (recur.equalsIgnoreCase( "every day" )) {
                   out.println("<option selected value=\"Every Day\">Every Day</option>");
                } else {
                   out.println("<option value=\"Every Day\">Every Day</option>");
                }
                if (recur.equalsIgnoreCase( "every sunday" )) {
                   out.println("<option selected value=\"Every Sunday\">Every Sunday</option>");
                } else {
                   out.println("<option value=\"Every Sunday\">Every Sunday</option>");
                }
                if (recur.equalsIgnoreCase( "every monday" )) {
                   out.println("<option selected value=\"Every Monday\">Every Monday</option>");
                } else {
                   out.println("<option value=\"Every Monday\">Every Monday</option>");
                }
                if (recur.equalsIgnoreCase( "every tuesday" )) {
                   out.println("<option selected value=\"Every Tuesday\">Every Tuesday</option>");
                } else {
                   out.println("<option value=\"Every Tuesday\">Every Tuesday</option>");
                }
                if (recur.equalsIgnoreCase( "every wednesday" )) {
                   out.println("<option selected value=\"Every Wednesday\">Every Wednesday</option>");
                } else {
                   out.println("<option value=\"Every Wednesday\">Every Wednesday</option>");
                }
                if (recur.equalsIgnoreCase( "every thursday" )) {
                   out.println("<option selected value=\"Every Thursday\">Every Thursday</option>");
                } else {
                   out.println("<option value=\"Every Thursday\">Every Thursday</option>");
                }
                if (recur.equalsIgnoreCase( "every friday" )) {
                   out.println("<option selected value=\"Every Friday\">Every Friday</option>");
                } else {
                   out.println("<option value=\"Every Friday\">Every Friday</option>");
                }
                if (recur.equalsIgnoreCase( "every saturday" )) {
                   out.println("<option selected value=\"Every Saturday\">Every Saturday</option>");
                } else {
                   out.println("<option value=\"Every Saturday\">Every Saturday</option>");
                }
                if (recur.equalsIgnoreCase( "all weekdays" )) {
                   out.println("<option selected value=\"All Weekdays\">All Weekdays</option>");
                } else {
                   out.println("<option value=\"All Weekdays\">All Weekdays</option>");
                }
                if (recur.equalsIgnoreCase( "all weekends" )) {
                   out.println("<option selected value=\"All Weekends\">All Weekends</option>");
                } else {
                   out.println("<option value=\"All Weekends\">All Weekends</option>");
                }
                 out.println("</select>");
                 out.println("<br><br>");

                out.println("&nbsp;&nbsp;Color to make the lottery times on the tee sheet:&nbsp;&nbsp;");

                 Common_Config.displayColorsAll(color, out);       // output the color options

                 out.println("<br>&nbsp;&nbsp;Click here to see the available colors:&nbsp;");
                 out.println("<a href=\"/" +rev+ "/proshop_color.htm\" target=\"_blank\">View Colors</a>");
                 out.println("<br><br>");
                   
                  out.println("Days in advance to start taking requests:&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"sdays\">");
                 if (sdays == 2) {
                    out.println("<option selected value=\"2\">2</option>");
                 } else {
                    out.println("<option value=\"2\">2</option>");
                 }
                 if (sdays == 3) {
                    out.println("<option selected value=\"3\">3</option>");
                 } else {
                    out.println("<option value=\"3\">3</option>");
                 }
                 if (sdays == 4) {
                    out.println("<option selected value=\"4\">4</option>");
                 } else {
                    out.println("<option value=\"4\">4</option>");
                 }
                 if (sdays == 5) {
                    out.println("<option selected value=\"5\">5</option>");
                 } else {
                    out.println("<option value=\"5\">5</option>");
                 }
                 if (sdays == 6) {
                    out.println("<option selected value=\"6\">6</option>");
                 } else {
                    out.println("<option value=\"6\">6</option>");
                 }
                 if (sdays == 7) {
                    out.println("<option selected value=\"7\">7</option>");
                 } else {
                    out.println("<option value=\"7\">7</option>");
                 }
                 if (sdays == 8) {
                    out.println("<option selected value=\"8\">8</option>");
                 } else {
                    out.println("<option value=\"8\">8</option>");
                 }
                 if (sdays == 9) {
                    out.println("<option selected value=\"9\">9</option>");
                 } else {
                    out.println("<option value=\"9\">9</option>");
                 }
                 if (sdays == 10) {
                    out.println("<option selected value=\"10\">10</option>");
                 } else {
                    out.println("<option value=\"10\">10</option>");
                 }
                 if (sdays == 11) {
                    out.println("<option selected value=\"11\">11</option>");
                 } else {
                    out.println("<option value=\"11\">11</option>");
                 }
                 if (sdays == 12) {
                    out.println("<option selected value=\"12\">12</option>");
                 } else {
                    out.println("<option value=\"12\">12</option>");
                 }
                 if (sdays == 13) {
                    out.println("<option selected value=\"13\">13</option>");
                 } else {
                    out.println("<option value=\"13\">13</option>");
                 }
                 if (sdays == 14) {
                    out.println("<option selected value=\"14\">14</option>");
                 } else {
                    out.println("<option value=\"14\">14</option>");
                 }
                 if (sdays == 15) {
                    out.println("<option selected value=\"15\">15</option>");
                 } else {
                    out.println("<option value=\"15\">15</option>");
                 }
                 if (sdays == 16) {
                    out.println("<option selected value=\"16\">16</option>");
                 } else {
                    out.println("<option value=\"16\">16</option>");
                 }
                 if (sdays == 17) {
                    out.println("<option selected value=\"17\">17</option>");
                 } else {
                    out.println("<option value=\"17\">17</option>");
                 }
                 if (sdays == 18) {
                    out.println("<option selected value=\"18\">18</option>");
                 } else {
                    out.println("<option value=\"18\">18</option>");
                 }
                 if (sdays == 19) {
                    out.println("<option selected value=\"19\">19</option>");
                 } else {
                    out.println("<option value=\"19\">19</option>");
                 }
                 if (sdays == 20) {
                    out.println("<option selected value=\"20\">20</option>");
                 } else {
                    out.println("<option value=\"20\">20</option>");
                 }
                 if (sdays == 21) {
                    out.println("<option selected value=\"21\">21</option>");
                 } else {
                    out.println("<option value=\"21\">21</option>");
                 }
                 if (sdays == 22) {
                    out.println("<option selected value=\"22\">22</option>");
                 } else {
                    out.println("<option value=\"22\">22</option>");
                 }
                 if (sdays == 23) {
                    out.println("<option selected value=\"23\">23</option>");
                 } else {
                    out.println("<option value=\"23\">23</option>");
                 }
                 if (sdays == 24) {
                    out.println("<option selected value=\"24\">24</option>");
                 } else {
                    out.println("<option value=\"24\">24</option>");
                 }
                 if (sdays == 25) {
                    out.println("<option selected value=\"25\">25</option>");
                 } else {
                    out.println("<option value=\"25\">25</option>");
                 }
                 if (sdays == 26) {
                    out.println("<option selected value=\"26\">26</option>");
                 } else {
                    out.println("<option value=\"26\">26</option>");
                 }
                 if (sdays == 27) {
                    out.println("<option selected value=\"27\">27</option>");
                 } else {
                    out.println("<option value=\"27\">27</option>");
                 }
                 if (sdays == 28) {
                    out.println("<option selected value=\"28\">28</option>");
                 } else {
                    out.println("<option value=\"28\">28</option>");
                 }
                 if (sdays == 29) {
                    out.println("<option selected value=\"29\">29</option>");
                 } else {
                    out.println("<option value=\"29\">29</option>");
                 }
                 if (sdays == 30) {
                    out.println("<option selected value=\"30\">30</option>");
                 } else {
                    out.println("<option value=\"30\">30</option>");
                 }
                  out.println("</select>");
                  out.println("<br><br>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:");
               out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                 out.println("<select size=\"1\" name=\"sd_hr\">");
                   if (sd_hr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (sd_hr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (sd_hr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (sd_hr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (sd_hr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (sd_hr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (sd_hr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (sd_hr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (sd_hr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (sd_hr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (sd_hr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (sd_hr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (sd_hr == 0) {
                      out.println("<option selected value=\"0\">12</option>");
                   } else {
                      out.println("<option value=\"0\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (sd_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + sd_min + " name=\"sd_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + sd_min + " name=\"sd_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"sdampm\">");
                   if (sdampm == 00) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (sdampm == 12) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

                  out.println("Days in advance to stop taking requests (must be less than above):&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"edays\">");
                 if (edays == 1) {
                    out.println("<option selected value=\"1\">1</option>");
                 } else {
                    out.println("<option value=\"1\">1</option>");
                 }
                 if (edays == 2) {
                    out.println("<option selected value=\"2\">2</option>");
                 } else {
                    out.println("<option value=\"2\">2</option>");
                 }
                 if (edays == 3) {
                    out.println("<option selected value=\"3\">3</option>");
                 } else {
                    out.println("<option value=\"3\">3</option>");
                 }
                 if (edays == 4) {
                    out.println("<option selected value=\"4\">4</option>");
                 } else {
                    out.println("<option value=\"4\">4</option>");
                 }
                 if (edays == 5) {
                    out.println("<option selected value=\"5\">5</option>");
                 } else {
                    out.println("<option value=\"5\">5</option>");
                 }
                 if (edays == 6) {
                    out.println("<option selected value=\"6\">6</option>");
                 } else {
                    out.println("<option value=\"6\">6</option>");
                 }
                 if (edays == 7) {
                    out.println("<option selected value=\"7\">7</option>");
                 } else {
                    out.println("<option value=\"7\">7</option>");
                 }
                 if (edays == 8) {
                    out.println("<option selected value=\"8\">8</option>");
                 } else {
                    out.println("<option value=\"8\">8</option>");
                 }
                 if (edays == 9) {
                    out.println("<option selected value=\"9\">9</option>");
                 } else {
                    out.println("<option value=\"9\">9</option>");
                 }
                 if (edays == 10) {
                    out.println("<option selected value=\"10\">10</option>");
                 } else {
                    out.println("<option value=\"10\">10</option>");
                 }
                 if (edays == 11) {
                    out.println("<option selected value=\"11\">11</option>");
                 } else {
                    out.println("<option value=\"11\">11</option>");
                 }
                 if (edays == 12) {
                    out.println("<option selected value=\"12\">12</option>");
                 } else {
                    out.println("<option value=\"12\">12</option>");
                 }
                 if (edays == 13) {
                    out.println("<option selected value=\"13\">13</option>");
                 } else {
                    out.println("<option value=\"13\">13</option>");
                 }
                 if (edays == 14) {
                    out.println("<option selected value=\"14\">14</option>");
                 } else {
                    out.println("<option value=\"14\">14</option>");
                 }
                 if (edays == 15) {
                    out.println("<option selected value=\"15\">15</option>");
                 } else {
                    out.println("<option value=\"15\">15</option>");
                 }
                 if (edays == 16) {
                    out.println("<option selected value=\"16\">16</option>");
                 } else {
                    out.println("<option value=\"16\">16</option>");
                 }
                 if (edays == 17) {
                    out.println("<option selected value=\"17\">17</option>");
                 } else {
                    out.println("<option value=\"17\">17</option>");
                 }
                 if (edays == 18) {
                    out.println("<option selected value=\"18\">18</option>");
                 } else {
                    out.println("<option value=\"18\">18</option>");
                 }
                 if (edays == 19) {
                    out.println("<option selected value=\"19\">19</option>");
                 } else {
                    out.println("<option value=\"19\">19</option>");
                 }
                 if (edays == 20) {
                    out.println("<option selected value=\"20\">20</option>");
                 } else {
                    out.println("<option value=\"20\">20</option>");
                 }
                 if (edays == 21) {
                    out.println("<option selected value=\"21\">21</option>");
                 } else {
                    out.println("<option value=\"21\">21</option>");
                 }
                 if (edays == 22) {
                    out.println("<option selected value=\"22\">22</option>");
                 } else {
                    out.println("<option value=\"22\">22</option>");
                 }
                 if (edays == 23) {
                    out.println("<option selected value=\"23\">23</option>");
                 } else {
                    out.println("<option value=\"23\">23</option>");
                 }
                 if (edays == 24) {
                    out.println("<option selected value=\"24\">24</option>");
                 } else {
                    out.println("<option value=\"24\">24</option>");
                 }
                 if (edays == 25) {
                    out.println("<option selected value=\"25\">25</option>");
                 } else {
                    out.println("<option value=\"25\">25</option>");
                 }
                 if (edays == 26) {
                    out.println("<option selected value=\"26\">26</option>");
                 } else {
                    out.println("<option value=\"26\">26</option>");
                 }
                 if (edays == 27) {
                    out.println("<option selected value=\"27\">27</option>");
                 } else {
                    out.println("<option value=\"27\">27</option>");
                 }
                 if (edays == 28) {
                    out.println("<option selected value=\"28\">28</option>");
                 } else {
                    out.println("<option value=\"28\">28</option>");
                 }
                 if (edays == 29) {
                    out.println("<option selected value=\"29\">29</option>");
                 } else {
                    out.println("<option value=\"29\">29</option>");
                 }
                 if (edays == 30) {
                    out.println("<option selected value=\"30\">30</option>");
                 } else {
                    out.println("<option value=\"30\">30</option>");
                 }
                  out.println("</select>");
               out.println("<br><br>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:");
               out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                 out.println("<select size=\"1\" name=\"ed_hr\">");
                   if (ed_hr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (ed_hr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (ed_hr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (ed_hr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (ed_hr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (ed_hr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (ed_hr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (ed_hr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (ed_hr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (ed_hr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (ed_hr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (ed_hr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (ed_hr == 0) {
                      out.println("<option selected value=\"0\">12</option>");
                   } else {
                      out.println("<option value=\"0\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (ed_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + ed_min + " name=\"ed_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + ed_min + " name=\"ed_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"edampm\">");
                   if (edampm == 00) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (edampm == 12) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

                  out.println("Days in advance to process the requests (must be less than or equal to above):&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"pdays\">");
                 if (pdays == 1) {
                    out.println("<option selected value=\"1\">1</option>");
                 } else {
                    out.println("<option value=\"1\">1</option>");
                 }
                 if (pdays == 2) {
                    out.println("<option selected value=\"2\">2</option>");
                 } else {
                    out.println("<option value=\"2\">2</option>");
                 }
                 if (pdays == 3) {
                    out.println("<option selected value=\"3\">3</option>");
                 } else {
                    out.println("<option value=\"3\">3</option>");
                 }
                 if (pdays == 4) {
                    out.println("<option selected value=\"4\">4</option>");
                 } else {
                    out.println("<option value=\"4\">4</option>");
                 }
                 if (pdays == 5) {
                    out.println("<option selected value=\"5\">5</option>");
                 } else {
                    out.println("<option value=\"5\">5</option>");
                 }
                 if (pdays == 6) {
                    out.println("<option selected value=\"6\">6</option>");
                 } else {
                    out.println("<option value=\"6\">6</option>");
                 }
                 if (pdays == 7) {
                    out.println("<option selected value=\"7\">7</option>");
                 } else {
                    out.println("<option value=\"7\">7</option>");
                 }
                 if (pdays == 8) {
                    out.println("<option selected value=\"8\">8</option>");
                 } else {
                    out.println("<option value=\"8\">8</option>");
                 }
                 if (pdays == 9) {
                    out.println("<option selected value=\"9\">9</option>");
                 } else {
                    out.println("<option value=\"9\">9</option>");
                 }
                 if (pdays == 10) {
                    out.println("<option selected value=\"10\">10</option>");
                 } else {
                    out.println("<option value=\"10\">10</option>");
                 }
                 if (pdays == 11) {
                    out.println("<option selected value=\"11\">11</option>");
                 } else {
                    out.println("<option value=\"11\">11</option>");
                 }
                 if (pdays == 12) {
                    out.println("<option selected value=\"12\">12</option>");
                 } else {
                    out.println("<option value=\"12\">12</option>");
                 }
                 if (pdays == 13) {
                    out.println("<option selected value=\"13\">13</option>");
                 } else {
                    out.println("<option value=\"13\">13</option>");
                 }
                 if (pdays == 14) {
                    out.println("<option selected value=\"14\">14</option>");
                 } else {
                    out.println("<option value=\"14\">14</option>");
                 }
                 if (pdays == 15) {
                    out.println("<option selected value=\"15\">15</option>");
                 } else {
                    out.println("<option value=\"15\">15</option>");
                 }
                 if (pdays == 16) {
                    out.println("<option selected value=\"16\">16</option>");
                 } else {
                    out.println("<option value=\"16\">16</option>");
                 }
                 if (pdays == 17) {
                    out.println("<option selected value=\"17\">17</option>");
                 } else {
                    out.println("<option value=\"17\">17</option>");
                 }
                 if (pdays == 18) {
                    out.println("<option selected value=\"18\">18</option>");
                 } else {
                    out.println("<option value=\"18\">18</option>");
                 }
                 if (pdays == 19) {
                    out.println("<option selected value=\"19\">19</option>");
                 } else {
                    out.println("<option value=\"19\">19</option>");
                 }
                 if (pdays == 20) {
                    out.println("<option selected value=\"20\">20</option>");
                 } else {
                    out.println("<option value=\"20\">20</option>");
                 }
                 if (pdays == 21) {
                    out.println("<option selected value=\"21\">21</option>");
                 } else {
                    out.println("<option value=\"21\">21</option>");
                 }
                 if (pdays == 22) {
                    out.println("<option selected value=\"22\">22</option>");
                 } else {
                    out.println("<option value=\"22\">22</option>");
                 }
                 if (pdays == 23) {
                    out.println("<option selected value=\"23\">23</option>");
                 } else {
                    out.println("<option value=\"23\">23</option>");
                 }
                 if (pdays == 24) {
                    out.println("<option selected value=\"24\">24</option>");
                 } else {
                    out.println("<option value=\"24\">24</option>");
                 }
                 if (pdays == 25) {
                    out.println("<option selected value=\"25\">25</option>");
                 } else {
                    out.println("<option value=\"25\">25</option>");
                 }
                 if (pdays == 26) {
                    out.println("<option selected value=\"26\">26</option>");
                 } else {
                    out.println("<option value=\"26\">26</option>");
                 }
                 if (pdays == 27) {
                    out.println("<option selected value=\"27\">27</option>");
                 } else {
                    out.println("<option value=\"27\">27</option>");
                 }
                 if (pdays == 28) {
                    out.println("<option selected value=\"28\">28</option>");
                 } else {
                    out.println("<option value=\"28\">28</option>");
                 }
                 if (pdays == 29) {
                    out.println("<option selected value=\"29\">29</option>");
                 } else {
                    out.println("<option value=\"29\">29</option>");
                 }
                 if (pdays == 30) {
                    out.println("<option selected value=\"30\">30</option>");
                 } else {
                    out.println("<option value=\"30\">30</option>");
                 }
                  out.println("</select>");
               out.println("<br><br>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;at:");
               out.println("&nbsp;&nbsp;&nbsp; hr &nbsp;");
                 out.println("<select size=\"1\" name=\"p_hr\">");
                   if (p_hr == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (p_hr == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (p_hr == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (p_hr == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (p_hr == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                   if (p_hr == 6) {
                      out.println("<option selected value=\"06\">6</option>");
                   } else {
                      out.println("<option value=\"06\">6</option>");
                   }
                   if (p_hr == 7) {
                      out.println("<option selected value=\"07\">7</option>");
                   } else {
                      out.println("<option value=\"07\">7</option>");
                   }
                   if (p_hr == 8) {
                      out.println("<option selected value=\"08\">8</option>");
                   } else {
                      out.println("<option value=\"08\">8</option>");
                   }
                   if (p_hr == 9) {
                      out.println("<option selected value=\"09\">9</option>");
                   } else {
                      out.println("<option value=\"09\">9</option>");
                   }
                   if (p_hr == 10) {
                      out.println("<option selected value=\"10\">10</option>");
                   } else {
                      out.println("<option value=\"10\">10</option>");
                   }
                   if (p_hr == 11) {
                      out.println("<option selected value=\"11\">11</option>");
                   } else {
                      out.println("<option value=\"11\">11</option>");
                   }
                   if (p_hr == 12) {
                      out.println("<option selected value=\"12\">12</option>");
                   } else {
                      out.println("<option value=\"12\">12</option>");
                   }
                   if (p_hr == 0) {
                      out.println("<option selected value=\"0\">12</option>");
                   } else {
                      out.println("<option value=\"0\">12</option>");
                   }
                 out.println("</select>");
                 out.println("&nbsp;&nbsp;&nbsp; min &nbsp;&nbsp;");
                    if (p_min < 10) {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + p_min + " name=\"p_min\">");
                    } else {
                       out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + p_min + " name=\"p_min\">");
                    }
                    out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"pampm\">");
                   if (pampm == 00) {
                      out.println("<option selected value=\"00\">AM</option>");
                   } else {
                      out.println("<option value=\"00\">AM</option>");
                   }
                   if (pampm == 12) {
                      out.println("<option selected value=\"12\">PM</option>");
                   } else {
                      out.println("<option value=\"12\">PM</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Number of consecutive tee times a member can request:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"slots\">");
                   if (slots == 01) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (slots == 02) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (slots == 03) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (slots == 04) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (slots == 05) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Minimum number of <b>Players</b> per request (including guests):&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"players\">");
                   if (players == 0) {
                      out.println("<option selected selected value=\"0\">0</option>");
                   } else {
                      out.println("<option value=\"0\">0</option>");
                   }
                   if (players == 01) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (players == 02) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (players == 03) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (players == 04) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (players == 05) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Minimum number of <b>members</b> per request (not counting guests):&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"members\">");
                   if (members == 0) {
                      out.println("<option selected selected value=\"0\">0</option>");
                   } else {
                      out.println("<option value=\"0\">0</option>");
                   }
                   if (members == 01) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (members == 02) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (members == 03) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (members == 04) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (members == 05) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");
                 
/*
               out.println("Give preference to full groups?&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"pref\">");
                   if (pref.equals ("Yes")) {
                      out.println("<option selected value=\"Yes\">Yes</option>");
                   } else {
                      out.println("<option value=\"Yes\">Yes</option>");
                   }
                   if (pref.equals ("No")) {
                      out.println("<option selected value=\"No\">No</option>");
                   } else {
                      out.println("<option value=\"No\">No</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");
*/

             out.println("Do you want to allow members to specify acceptable time<br>range values when they request a starting time (minutes before and after)?&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"allowmins\">");
                   if (allow_mins == 0) {
                      out.println("<option selected value=\"Yes\">Yes</option>");
                   } else {
                      out.println("<option value=\"Yes\">Yes</option>");
                   }
                   if (allow_mins == 1) {
                      out.println("<option selected value=\"No\">No</option>");     // 1 = NO
                   } else {
                      out.println("<option value=\"No\">No</option>");
                   }
               out.println("</select>");
             out.println("<br><br>");


             out.println("Default value for Minutes Before for member starting time requests:&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"minsbefore\">");
               if (mins_before == 0) {
                  out.println("<option selected value=\"0\">None</option>");
               } else {
                  out.println("<option value=\"0\">None</option>");
               }
               if (mins_before == 10) {
                  out.println("<option selected value=\"10\">10 mins</option>");
               } else {
                  out.println("<option value=\"10\">10 mins</option>");
               }
               if (mins_before == 20) {
                  out.println("<option selected value=\"20\">20 mins</option>");
               } else {
                  out.println("<option value=\"20\">20 mins</option>");
               }
               if (mins_before == 30) {
                  out.println("<option selected value=\"30\">30 mins</option>");
               } else {
                  out.println("<option value=\"30\">30 mins</option>");
               }
               if (mins_before == 40) {
                  out.println("<option selected value=\"40\">40 mins</option>");
               } else {
                  out.println("<option value=\"40\">40 mins</option>");
               }
               if (mins_before == 50) {
                  out.println("<option selected value=\"50\">50 mins</option>");
               } else {
                  out.println("<option value=\"50\">50 mins</option>");
               }
               if (mins_before == 60) {
                  out.println("<option selected value=\"60\">1 hr</option>");
               } else {
                  out.println("<option value=\"60\">1 hr</option>");
               }
               if (mins_before == 75) {
                  out.println("<option selected value=\"75\">1 hr 15 mins</option>");
               } else {
                  out.println("<option value=\"75\">1 hr 15 mins</option>");
               }
               if (mins_before == 90) {
                  out.println("<option selected value=\"90\">1 hr 30 mins</option>");
               } else {
                  out.println("<option value=\"90\">1 hr 30 mins</option>");
               }
               if (mins_before == 120) {
                  out.println("<option selected value=\"120\">2 hrs</option>");
               } else {
                  out.println("<option value=\"120\">2 hrs</option>");
               }
               if (mins_before == 150) {
                  out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"150\">2 hrs 30 mins</option>");
               }
               if (mins_before == 180) {
                  out.println("<option selected value=\"180\">3 hrs</option>");
               } else {
                  out.println("<option value=\"180\">3 hrs</option>");
               }
               if (mins_before == 210) {
                  out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"210\">3 hrs 30 mins</option>");
               }
               if (mins_before == 240) {
                  out.println("<option selected value=\"240\">4 hrs</option>");
               } else {
                  out.println("<option value=\"240\">4 hrs</option>");
               }
               if (mins_before == 300) {
                  out.println("<option selected value=\"300\">5 hrs</option>");
               } else {
                  out.println("<option value=\"300\">5 hrs</option>");
               }
               if (mins_before == 360) {
                  out.println("<option selected value=\"360\">6 hrs</option>");
               } else {
                  out.println("<option value=\"360\">6 hrs</option>");
               }
               out.println("</select>");
             out.println("<br><br>");
          
          
             out.println("Default value for Minutes After for member starting time requests:&nbsp;&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"minsafter\">");
               if (mins_after == 0) {
                  out.println("<option selected value=\"0\">None</option>");
               } else {
                  out.println("<option value=\"0\">None</option>");
               }
               if (mins_after == 10) {
                  out.println("<option selected value=\"10\">10 mins</option>");
               } else {
                  out.println("<option value=\"10\">10 mins</option>");
               }
               if (mins_after == 20) {
                  out.println("<option selected value=\"20\">20 mins</option>");
               } else {
                  out.println("<option value=\"20\">20 mins</option>");
               }
               if (mins_after == 30) {
                  out.println("<option selected value=\"30\">30 mins</option>");
               } else {
                  out.println("<option value=\"30\">30 mins</option>");
               }
               if (mins_after == 40) {
                  out.println("<option selected value=\"40\">40 mins</option>");
               } else {
                  out.println("<option value=\"40\">40 mins</option>");
               }
               if (mins_after == 50) {
                  out.println("<option selected value=\"50\">50 mins</option>");
               } else {
                  out.println("<option value=\"50\">50 mins</option>");
               }
               if (mins_after == 60) {
                  out.println("<option selected value=\"60\">1 hr</option>");
               } else {
                  out.println("<option value=\"60\">1 hr</option>");
               }
               if (mins_after == 75) {
                  out.println("<option selected value=\"75\">1 hr 15 mins</option>");
               } else {
                  out.println("<option value=\"75\">1 hr 15 mins</option>");
               }
               if (mins_after == 90) {
                  out.println("<option selected value=\"90\">1 hr 30 mins</option>");
               } else {
                  out.println("<option value=\"90\">1 hr 30 mins</option>");
               }
               if (mins_after == 120) {
                  out.println("<option selected value=\"120\">2 hrs</option>");
               } else {
                  out.println("<option value=\"120\">2 hrs</option>");
               }
               if (mins_after == 150) {
                  out.println("<option selected value=\"150\">2 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"150\">2 hrs 30 mins</option>");
               }
               if (mins_after == 180) {
                  out.println("<option selected value=\"180\">3 hrs</option>");
               } else {
                  out.println("<option value=\"180\">3 hrs</option>");
               }
               if (mins_after == 210) {
                  out.println("<option selected value=\"210\">3 hrs 30 mins</option>");
               } else {
                  out.println("<option value=\"210\">3 hrs 30 mins</option>");
               }
               if (mins_after == 240) {
                  out.println("<option selected value=\"240\">4 hrs</option>");
               } else {
                  out.println("<option value=\"240\">4 hrs</option>");
               }
               if (mins_after == 300) {
                  out.println("<option selected value=\"300\">5 hrs</option>");
               } else {
                  out.println("<option value=\"300\">5 hrs</option>");
               }
               if (mins_after == 360) {
                  out.println("<option selected value=\"360\">6 hrs</option>");
               } else {
                  out.println("<option value=\"360\">6 hrs</option>");
               }
               out.println("</select>");
             out.println("<br><br>");
          
          
               out.println("Review & approve tee times prior to posting?&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"approve\">");
                   if (approve.equals ("Yes")) {
                      out.println("<option selected value=\"Yes\">Yes</option>");
                   } else {
                      out.println("<option value=\"Yes\">Yes</option>");
                   }
                   if (approve.equals ("No")) {
                      out.println("<option selected value=\"No\">No</option>");
                   } else {
                      out.println("<option value=\"No\">No</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

                  out.println("Lottery Type:&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"type\">");
                 if (type.equals ( "Random" )) {
                    out.println("<option selected value=\"Random\">Random</option>");
                 } else {
                    out.println("<option value=\"Random\">Random</option>");
                 }
                 if (type.equals ( "Proshop" )) {
                    out.println("<option selected value=\"Proshop\">Proshop</option>");
                 } else {
                    out.println("<option value=\"Proshop\">Proshop</option>");
                 }
                 if (type.equals ( "WeightedBR" )) {
                    out.println("<option selected value=\"WeightedBR\">Weighted By Rounds</option>");
                 } else {
                    out.println("<option value=\"WeightedBR\">Weighted By Rounds</option>");
                 }
                 if (type.equals ( "WeightedBP" )) {
                    out.println("<option selected value=\"WeightedBP\">Weighted By Proximity</option>");
                 } else {
                    out.println("<option value=\"WeightedBP\">Weighted By Proximity</option>");
                 }
                 out.println("</select>");
               out.println("<br><br>");

            out.println("</font></td></tr>");
            out.println("<tr>");
            out.println("<td width=\"580\">");
            out.println("<font size=\"2\">");
               out.println("<b>You MUST complete the following if you selected a Weighted Lottery Type:</b>");
               out.println("<br>(Not necessary for Random or Proshop types)<br><br>");

               out.println("Number of days to accumulate members' lottery points:&nbsp;&nbsp;&nbsp;");
                 if (adays < 10) {
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + adays + " name=\"adays\">");
                 } else {
                    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + adays + " name=\"adays\">");
                 }
                 out.println("&nbsp;(enter 00 - 99)");
               out.println("<br><br>");

                  out.println("Lottery selection based on:&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"selection\">");
                 if (selection == 1 ) {
                    out.println("<option selected value=\"1\">Total Points of Group</option>");
                 } else {
                    out.println("<option value=\"1\">Total Points of Group</option>");
                 }
                 if (selection == 2 ) {
                    out.println("<option selected value=\"2\">Average Points of Group</option>");
                 } else {
                    out.println("<option value=\"2\">Average Points of Group</option>");
                 }
                 if (selection == 3 ) {
                    out.println("<option selected value=\"3\">Points of Highest Player in Group</option>");
                 } else {
                    out.println("<option value=\"3\">Points of Highest Player in Group</option>");
                 }
                 if (selection == 4 ) {
                    out.println("<option selected value=\"4\">Points of Lowest Player in Group</option>");
                 } else {
                    out.println("<option value=\"4\">Points of Lowest Player in Group</option>");
                 }
                 out.println("</select>");
               out.println("<br><br>");

                  out.println("Each guest in a request will count as:&nbsp;&nbsp;&nbsp;");
                  out.println("<select size=\"1\" name=\"guest\">");
                 if (guest == 0) {
                    out.println("<option selected value=\"0\">None - do not count</option>");
                 } else {
                    out.println("<option value=\"0\">None - do not count</option>");
                 }
                 if (guest == 1) {
                    out.println("<option selected value=\"1\">Same as the highest member</option>");
                 } else {
                    out.println("<option value=\"1\">Same as the highest member</option>");
                 }
                 if (guest == 2 ) {
                    out.println("<option selected value=\"2\">Same as the lowest member</option>");
                 } else {
                    out.println("<option value=\"2\">Same as the lowest member</option>");
                 }
                 out.println("</select>");
               out.println("<br><br>");
                out.println("Click here for more information:&nbsp;&nbsp;");
                out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_lottery-ByRounds.htm', 'newwindow', 'Height=460, width=650, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
                out.println("Weighted By Rounds</a>");
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_lottery-ByProximity.htm', 'newwindow', 'Height=570, width=650, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
                out.println("Weighted By Proximity</a>");
                out.println("<br><br>");

            out.println("</font></td></tr>");
            out.println("<tr>");
            out.println("<td width=\"580\">");
            out.println("<font size=\"2\">");
             out.println("<b>You MUST complete the following ONLY if you selected 'Weighted By Rounds' Lottery Type:</b>");
             out.println("<br>(Not necessary for any other types)<br><br>");

               out.println("Number of lottery points to assess for a weekday round:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"wdpts\">");
                   if (wdpts == 0) {
                      out.println("<option selected selected value=\"00\">0</option>");
                   } else {
                      out.println("<option value=\"00\">0</option>");
                   }
                   if (wdpts == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (wdpts == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (wdpts == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (wdpts == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (wdpts == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Number of lottery points to assess for a weekend round:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"wepts\">");
                   if (wepts == 0) {
                      out.println("<option selected selected value=\"00\">0</option>");
                   } else {
                      out.println("<option value=\"00\">0</option>");
                   }
                   if (wepts == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (wepts == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (wepts == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (wepts == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (wepts == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Number of lottery points to assess for an event round:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"evpts\">");
                   if (evpts == 0) {
                      out.println("<option selected selected value=\"00\">0</option>");
                   } else {
                      out.println("<option value=\"00\">0</option>");
                   }
                   if (evpts == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (evpts == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (evpts == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (evpts == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (evpts == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Number of lottery points to assess for each guest round:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"gpts\">");
                   if (gpts == 0) {
                      out.println("<option selected selected value=\"00\">0</option>");
                   } else {
                      out.println("<option value=\"00\">0</option>");
                   }
                   if (gpts == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (gpts == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (gpts == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (gpts == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (gpts == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("Number of lottery points to assess for each no-show by a member:&nbsp;&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"nopts\">");
                   if (nopts == 0) {
                      out.println("<option selected selected value=\"00\">0</option>");
                   } else {
                      out.println("<option value=\"00\">0</option>");
                   }
                   if (nopts == 1) {
                      out.println("<option selected selected value=\"01\">1</option>");
                   } else {
                      out.println("<option value=\"01\">1</option>");
                   }
                   if (nopts == 2) {
                      out.println("<option selected value=\"02\">2</option>");
                   } else {
                      out.println("<option value=\"02\">2</option>");
                   }
                   if (nopts == 3) {
                      out.println("<option selected value=\"03\">3</option>");
                   } else {
                      out.println("<option value=\"03\">3</option>");
                   }
                   if (nopts == 4) {
                      out.println("<option selected value=\"04\">4</option>");
                   } else {
                      out.println("<option value=\"04\">4</option>");
                   }
                   if (nopts == 5) {
                      out.println("<option selected value=\"05\">5</option>");
                   } else {
                      out.println("<option value=\"05\">5</option>");
                   }
                 out.println("</select>");
               out.println("<br><br>");

               out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + oldName + "\"></input>");
               out.println("<input type=\"hidden\" name=\"oldCourse\" value=\"" + oldCourse + "\"></input>");
               out.println("<input type=\"hidden\" name=\"oldpdays\" value=\"" + oldpdays + "\"></input>");
               out.println("<input type=\"hidden\" name=\"oldptime\" value=\"" + oldptime + "\"></input>");
               out.println("<input type=\"hidden\" name=\"sheet\" value=\"" + sheet + "\">");

               out.println("<p align=\"center\">");
         out.println("<input type=\"submit\" name=\"Update\" value=\"Update\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<input type=\"submit\" name=\"Delete\" value=\"Delete\" onclick=\"return confirm('Are you sure you want to delete this lottery?')\">");
           
      out.println("</p></font></td></tr></form></table>");
      out.println("</td></tr></table>");                       // end of main page table
        
   out.println("<font size=\"2\">");
   if (!sheet.equals( "yes" )) {                      // if not call from Proshop_sheet
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_lottery\">");
      out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form>");
   } else {
      out.println("<form><input type=\"button\" value=\"Cancel\" onClick='self.close();'>");
      out.println("</input></form>");
   }
   out.println("</font>");
   out.println("</center></font></body></html>");

 }   // end of doPost   


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
