/***************************************************************************************
 *   Proshop_mlottery:  This servlet will process the 'Lottery Services' request from
 *                    the Proshop's main page.
 *
 *
 *   called by:  Proshop_main (doGet)
 *
 *   created: 9/17/2003   Bob P.
 *
 *   last updated:
 *
 * 
 *        9/17/13   Allow Ballenisles to view calendars for 365 days.
 *        2/26/13   Updated SystemUtils.moveReqs calls to pass a username value.
 *       11/07/12   Change calls to SystemUtils.getFirstTime and getLastTime to Common_Lott (methods moved).
 *        4/19/12   Denver CC (denvercc) - allow pro to see up to 60 days in advance (case 2145).
 *        5/18/11   Cherry Hills CC (cherrychills) - Allow pro to see up to 365 days in advance (case 1982).
 *        9/10/10   Black Diamond Ranch (blackdiamondranch) - Extend visible lottery days custom from 90days to 180days.
 *        9/02/10   Quaker Ridge GC (quakerridgegc) - Allow pro to see up to 120 days in advance.
 *        8/26/10   Boulder CC (boulder) - Allow pro to see up to 120 days in advance (case 1813).
 *        8/26/10   Troon CC (trooncc) - Allow pro to see up to 90 days in advance.
 *        7/29/10   No longer allow lottery requests to be edited after the lottery has been processed (lstate > 3)
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        5/05/10   Wayzata CC (wayzata) - allow pro to see up to 60 days in advance (case 1839).
 *        4/15/10   Inverness Club - allow pro to see up to 210 days in advance
 *        1/29/10   Added notes column to signup listing and made it clickable to open a notes window to displaying them
 *                  Also fixed the 5some rest issue so that the signup listing is formatted more accurately
 *        8/08/09   Make days past maxdays in calander appear silver in color so user can tell the
 *                  days are not available yet (Black Diamond Ranch) - also updated the instructions
 *       10/21/08   Black Diamond Ranch - allow pro to see up to 90 days in advance
 *        9/02/08   Modified limited access proshop user checks
 *        8/14/08   Added limited access proshop users checks
 *        6/11/07   Enabled new lottery management to all clubs
 *        6/04/07   Added call to _sheet for new lottery processing
 *        4/26/07   Updated calls to SystemUtils.moveReqs
 *        4/04/07   Inverness Club - allow pro to see up to 90 days in advance
 *        3/28/07   Process Lottery - process restrictions for all clubs when assigning a time for lottery request (case 1078, 1084).
 *       12/14/06   Correct the font color for headings.
 *       10/10/06   Allow for a unique f/b indicator in lreqs3 for all times when the times have been assigned.
 *       10/09/06   Check all error types when outputting error message when assigns fail.
 *        9/19/06   Set the time value before processing restrictions.
 *        9/13/06   Allow pro to view lottery requests for ALL courses at one time.
 *        8/31/06   Do not include tee times that are blocked or during an event when listing available times (approve2).
 *        7/14/06   Check restrictions after times have been assigned to verify new time (approve2).
 *        6/28/06   Use the group's fb indicator when checking if a tee time already exists (approve2).
 *        6/21/05   Add new lottery type - Weighted By Proximity.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        3/04/04   Fix the 5-some restriction tests when displaying the list of lottery requests.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;


public class Proshop_mlottery extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the initial request from Proshop_main
 //   and 2nd requests from self.
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   //
   //  Check if return from Proshop_lott (via Proshop_jump)
   //
   if (req.getParameter("jump") != null) {

      doPost(req, resp);      // call doPost processing
      return;
   }

   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();


   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (req.getParameter("approve") != null && !SystemUtils.verifyProAccess(req, "LOTT_APPROVE", con, out)) {
       SystemUtils.restrictProshop("LOTT_APPROVE", out);
       return;
   }

   if (req.getParameter("requests") != null && !SystemUtils.verifyProAccess(req, "LOTT_UPDATE", con, out)) {
       SystemUtils.restrictProshop("LOTT_UPDATE", out);
       return;
   }

    // Define parms
   String club = (String)session.getAttribute("club");
   String courseName = "";        // course names
   String reqName = "";
   int multi = 0;               // multiple course support
   int index= 0;
   long date = 0;

   boolean use_dlott = true;
   boolean reqFound = false;


   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("notes") != null) {

      String sid = req.getParameter("id");             //  entry id in evntsup
      int id = 0;
      try {
         id = Short.parseShort(sid);
      }
      catch (NumberFormatException e) {
         // ignore error
      }
      courseName = req.getParameter("course");
      String lname = req.getParameter("lname");
      String player1 = "";
      String notes = "";

      try {

         PreparedStatement pstmt2s = con.prepareStatement (
            "SELECT player1, notes " +
            "FROM lreqs3 WHERE name = ? AND courseName = ? AND id = ?");

         pstmt2s.clearParameters();        // clear the parms
         pstmt2s.setString(1, lname);
         pstmt2s.setString(2, courseName);
         pstmt2s.setInt(3, id);

         rs = pstmt2s.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            player1 = rs.getString(1);
            notes = rs.getString(2);
         }

         pstmt2s.close();

         out.println(SystemUtils.HeadTitle("Show Notes"));
         out.println("<BODY><CENTER><BR>");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<font size=\"3\"><b>Lottery Request Notes</b></font><br><BR>");
         out.println("<font size=\"2\">For Lottery <b>" + lname + "</b> ");
         if (!courseName.equals( "" )) {
            out.println("on course <b>" + courseName + "</b> ");
         }
         out.println("where Player 1 is <b>" + player1);
         out.println("</b><BR><p>");
         out.println( notes );
         out.println("<p>");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" value=\"Close\" onclick='self.close()' alt=\"Close\">");
         out.println("</form>");
         out.println("</font></CENTER></BODY></HTML>");

      } catch (Exception ignore) { }

      return;
   }


   //
   //  Check if course selected
   //
   if (req.getParameter("course") == null)  {

      //
      // Get info from the club db
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi " +
                                "FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);

         } else {
           // Parms do not exist yet
            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>The Club Setup has not been completed.");
            out.println("<BR>Please go to 'System Config' and select 'Club Setup'.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
         stmt.close();

         if (multi != 0) {           // if multiple courses supported for this club

            course = Utilities.getCourseNames(con);     // get all the course names

            if (req.getParameter("requests") != null && course.size() > 1)  {    // if processing a 'view lottery requests' and more than 1 course

               course.add ("-ALL-");                       // add the ALL option
            }

            //
            //  Build the HTML page to prompt user for a specific date
            //
            out.println(SystemUtils.HeadTitle("Proshop Select Course Page"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
            out.println("<table border=\"0\" align=\"center\">");
            out.println("<tr><td align=\"center\">");

            out.println("<br><p align=\"center\"><font size=\"4\">");
            out.println("Lottery Manager</p>");

            out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"560\">");
            out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\">Select the course you wish to process and then click on 'Continue' below.");
            out.println("</td></tr></font></table>");
            out.println("<font size=\"2\"><br><br>");

            out.println("<form method=\"get\" action=\"Proshop_mlottery\">");

            if (req.getParameter("requests") != null)  {       // if processing a 'view lottery requests'
               out.println("<input type=\"hidden\" name=\"requests\" value=\"\">");
            } else {
               out.println("<input type=\"hidden\" name=\"approve\" value=\"\">");
            }

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");

            courseName = course.get(0);      // get first course name from array

            out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");

            for (index=1; index < course.size(); index++) {

               courseName = course.get(index);      // get first course name from array

               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
            out.println("</select>");
            out.println("<br><br>");

            out.println("<table border=\"0\" align=\"center\">");
            out.println("<tr>");
            out.println("<td align=\"center\">");
            out.println("<p align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font></p>");
            out.println("<p align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font></p>");
            out.println("</td></tr></table");

            //
            //  End of HTML page
            //
            out.println("</td></tr>");
            out.println("</table>");
            out.println("</center></font></body></html>");

            return;                        // exit and wait for 3rd call

         } else {                          // multi courses not supported

            courseName = "";              // no courses to worry about - continue below
         }

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   } else {       // 3nd call - course was selected

      courseName = req.getParameter("course");      // get the course name passed
   }

   //
   //  Get today's date and setup parms to use when building the calendar
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);  // day of week (01 - 07)

   int today = day;                              // save today's number

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   String mm = mm_table[month];                  // month name

   int numDays = numDays_table[month];           // number of days in month

   if (numDays == 0) {                           // if Feb

      int leapYear = year - 2000;
      numDays = feb_table[leapYear];             // get days in Feb
   }

   int count = 0;                     // init day counter
   int col = 0;                       // init column counter
   int maxDays = 30;                  // number of days in calendar

   if (club.equals("trooncc")) {    // if custom calendars required

      maxDays = 90;           // allow 90 day calendars
   }

   if (club.equals("invernessclub")) {    // if custom calendars required

      maxDays = 210;           // allow 210 day calendars
   }

   if (club.equals("wayzata") || club.equals("denvercc")) {

      maxDays = 60;
   }

   if (club.equals("boulder") || club.equals("quakerridgegc")) {
       
      maxDays = 120;
   }

   if (club.equals("blackdiamondranch")) {
      maxDays = 180;
   }

   if (club.equals("cherryhills") || club.equals("ballenisles") || club.equals("demotom")) {
      maxDays = 365;
   }

   //
   //  Build the HTML page to prompt user for a specific date
   //
   out.println(SystemUtils.HeadTitle("Proshop Select Date Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");


   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<br><p align=\"center\"><font size=\"4\">");
      out.println("Lottery Manager</p>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"560\">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");

      if (req.getParameter("approve") != null) {

         out.println("<p align=\"center\">To view a day's Lottery Results, select the date below.");
         out.println("<br>These are Lottery Requests that have been assigned times but are awaiting approval.<br>");
         out.println("<br>Only the dates with Lottery Results waiting for Approval will be selectable.</p>");

      } else {

         out.println("<p align=\"center\">To view a day's Lottery Requests, select the date below.");
         out.println("<br>Only the dates with Active Lottery Requests within the next " + maxDays + " will be selectable.</p>");
      }

      out.println("</td></tr></font></table>");
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\">Today's date is:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</b></p>");


      cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
      day = 1;

      //
      // start of calendar row
      //
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"center\">");

      if (use_dlott && req.getParameter("approve") != null) {
        out.println("<form action=\"Proshop_jump\" method=\"get\" target=\"_top\">");
      } else {
        out.println("<form action=\"Proshop_mlottery\" method=\"post\">");
      }

      if (req.getParameter("approve") != null) {

         out.println("<input type=\"hidden\" name=\"approve\" value=\"\">");

      } else {

         out.println("<input type=\"hidden\" name=\"requests\" value=\"\">");
      }
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");

      int numMonths = 1;        // number of month calendars being built

      //
      //  build one large table to hold one table for each month required
      //
      out.println("<table border=\"0\" cellpadding=\"5\"><tr><td align=\"center\" valign=\"top\"><font size=\"2\">");

      //
      //  table for first month
      //
      out.println("<table border=\"1\" width=\"200\" bgcolor=\"#F5F5DC\">");
         out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + mm + "&nbsp;&nbsp;" + year + "</b></font>");
         out.println("</td></tr><tr>");
            out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">M</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">W</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">F</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">S</font></td>");

         out.println("</tr><tr>");        // first row of days

         for (int i = 1; i < day_num; i++) {    // skip to the first day
            out.println("<td><br></td>");
            col++;
         }

         while (day < today) {
            out.println("<td align=\"center\"><font size=\"2\">" + day + "</font></td>");    // put in day of month
            col++;
            day++;

            if (col == 7) {
               col = 0;                             // start new week
               out.println("</tr><tr>");
            }
         }

         while ( count < maxDays ) {            // start with today, go to end of month or 30 days (or more)

            if (day <= numDays ) {

               date = (year * 10000) + (month * 100) + day;

               try {
                  //
                  //  See if there are any lottery requests for this day
                  //
                  if (req.getParameter("approve") != null) {

                     pstmt = con.prepareStatement (
                        "SELECT name " +
                        "FROM lreqs3 " +
                        "WHERE date = ? AND courseName = ? AND state > 1 " +
                        "GROUP BY name");

                     pstmt.clearParameters();        // clear the parms
                     pstmt.setLong(1, date);
                     pstmt.setString(2, courseName);

                  } else {

                     if (courseName.equals( "-ALL-" )) {

                        pstmt = con.prepareStatement (
                           "SELECT name " +
                           "FROM lreqs3 " +
                           "WHERE date = ? " +
                           "GROUP BY name");

                        pstmt.clearParameters();        // clear the parms
                        pstmt.setLong(1, date);

                     } else {

                        pstmt = con.prepareStatement (
                           "SELECT name " +
                           "FROM lreqs3 " +
                           "WHERE date = ? AND courseName = ? " +
                           "GROUP BY name");

                        pstmt.clearParameters();        // clear the parms
                        pstmt.setLong(1, date);
                        pstmt.setString(2, courseName);
                     }
                  }

                  rs = pstmt.executeQuery();      // execute the prepared stmt

                  reqFound = false;

                  while (rs.next()) {

                     reqName = rs.getString("name");

                     if (use_dlott && req.getParameter("approve") != null) {

                        reqFound = true;
                        out.println("<td align=\"center\"><font size=\"2\"><b><input type=\"submit\" value=\"" + day + "\" name=\"index\" onclick=\"this.value='" + count + "'\"></b></font></td>");

                     } else if (SystemUtils.getLotteryState(date, month, day, year, reqName, courseName, con) < 4) {

                        reqFound = true;
                        out.println("<td align=\"center\"><font size=\"2\"><b><input type=\"submit\" value=\"" + day + "\" name=\"i" + count + "\"></b></font></td>");
                     }

                     if (reqFound) break;
                  }

                  if (!reqFound) {
                     out.println("<td align=\"center\"><font size=\"2\">" + day + "</font></td>");    // put in day of month
                  }

                  /*
                  if (rs.next()) {

                     if (use_dlott && req.getParameter("approve") != null) {
                        out.println("<td align=\"center\"><font size=\"2\"><b><input type=\"submit\" value=\"" + day + "\" name=\"index\" onclick=\"this.value='" + count + "'\"></b></font></td>");
                     } else {
                        out.println("<td align=\"center\"><font size=\"2\"><b><input type=\"submit\" value=\"" + day + "\" name=\"i" + count + "\"></b></font></td>");
                     }

                  } else {

                     out.println("<td align=\"center\"><font size=\"2\">" + day + "</font></td>");    // put in day of month

                  }
                  */

                  pstmt.close();
               }
               catch (Exception e) {
                  out.println("<td align=\"center\"><font size=\"2\"><b><input type=\"submit\" value=\"" + day + "\" name=\"i" + count + "\"></b></font></td>");
               }

               col++;
               day++;
               count++;

               if (col == 7) {
                  col = 0;                             // start new week
                  out.println("</tr><tr>");
               }

            } else {

               day = 1;                               // start a new month
               month = month +1;
               if (month > 12) {
                  month = 1;                          // end of year - use Jan
                  year = year + 1;                    // new year
               }
               numDays = numDays_table[month];        // number of days in month
               mm = mm_table[month];                  // month name

               if (numDays == 0) {                           // if Feb

                  int leapYear = year - 2000;
                  numDays = feb_table[leapYear];             // get days in Feb
               }

               numMonths++;        // bump # of calendars being built in this row

               out.println("</tr></table></td>");

               if (numMonths > 3) {         // if this is the 4th month in this row

                  out.println("</tr><tr>");   // start new row of calendars
                  numMonths = 1;              // reset counter
               }

               out.println("<td align=\"center\" valign=\"top\"><font size=\"2\">");

               out.println("<table border=\"1\" width=\"200\" bgcolor=\"#F5F5DC\">");
                  out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + mm + "&nbsp;&nbsp;" + year + "</b></font>");
                  out.println("</td></tr><tr>");
                     out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">M</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">W</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">F</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
                  out.println("</tr><tr>");        // first row of days

                  for (int i = 0; i < col; i++) {          // skip to where we left off
                     out.println("<td><br></td>");
                  }
            }
         }                        // end of while count < 30

         //
         // finish the current month and make the days silver in color so user can tell the days are not available yet
         //
         while (day <= numDays ) {

            out.println("<td align=\"center\"><font size=\"2\" color=silver>" + day + "</font></td>");    // put in day of month
            col++;
            day++;
            count++;

            if (col == 7) {
               col = 0;                             // start new week
               out.println("</tr><tr>");
            }

         }
         out.println("</tr>");

      //
      // end of calendar row
      //
      out.println("</table>");
      out.println("</font>");
      out.println("</td></tr></table>");
      out.println("</form>");

      if (req.getParameter("source") == null) {

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr>");
         out.println("<td align=\"center\">");
         out.println("<p align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_mlottery\">");
            if (req.getParameter("requests") != null)  {       // if processing a 'view lottery requests'
               out.println("<input type=\"hidden\" name=\"requests\" value=\"yes\">");
            } else {
               out.println("<input type=\"hidden\" name=\"approve\" value=\"yes\">");
            }
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font></p>");
         out.println("<p align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font></p>");
         out.println("</td></tr></table");
      }

   out.println("</td></tr>");

   //
   //  End of HTML page
   //
   out.println("</table>");
   out.println("</center></font></body></html>");

 }  // end of doGet


 //*****************************************************
 // Process the requests from doGet above
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   //Statement stmt = null;
   //Statement stmtc = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

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
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  check which action we are to process (requests or approval)
   //
   if (req.getParameter("approve") != null) {   // if call to view/approve assigned requests

      approve(req, out, con);          // go process the approval request
      return;
   }

   //
   //  check for approve2 - process an actual approval
   //
   if (req.getParameter("approve2") != null) {

      approve2(req, out, con);          // go process the approved requests
      return;
   }

   //
   //   get name of club for this user
   //
   //String club = (String)session.getAttribute("club");      // get club name

   String index2 = "";
   String name = "";
   String lname = "";
   String num = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";
   String ampm = "";
   String sfb = "";
   String submit = "";
   String rest = "";
   String time_zone = "";
   String day_name = "";
   String course = "";
   String courseName = "";

   long date = 0;
   long lottid = 0;

   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int count = 0;
   int fb = 0;
   int slots = 0;
   int lstate = 0;
   int advance_days = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int ind = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int day_num = 0;
   int fives = 0;
   int tfives = 0;

   boolean notes_exists = false;

   //
   //  request is from user selecting a date to list all lottery requests
   //
   course = req.getParameter("course");      // get the course name passed

   //
   //************************************************************
   //       The name of the submit button is an index value
   //       (0 = today, 1 = tomorrow, etc.)
   //************************************************************
   //
   Enumeration enum1 = req.getParameterNames();     // get the parm names passed

   loop1:
   while (enum1.hasMoreElements()) {

      name = (String) enum1.nextElement();          // get name of parm

      if (name.startsWith( "i" )) {

         index2 = name;                            // save for Proshop_lott and its return here
         break loop1;                              // done - exit while loop
      }
   }

   //
   //  make sure we have the index value
   //
   if (!name.startsWith( "i" )) {

      out.println(SystemUtils.HeadTitle("Procedure Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Access Procdure Error</H3>");
      out.println("<BR><BR>Required Parameter is Missing - Proshop_mlottery.");
      out.println("<BR>Please exit and try again.");
      out.println("<BR><BR>If problem persists, report this error to ForeTees support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Convert the index value from string to int
   //
   StringTokenizer tok = new StringTokenizer( name, "i" );     // space is the default token - use 'i'

   num = tok.nextToken();                        // get just the index number (name= parm must start with alpha)

   try {
      ind = Integer.parseInt(num);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   while (ind != 0) {                          // if not today

      cal.roll(Calendar.DATE,true);              // roll ahead one day

      day = cal.get(Calendar.DAY_OF_MONTH);      // get new day

      if (day == 1) {
         cal.roll(Calendar.MONTH,true);          // adjust month if starting a new one
      }

      month = cal.get(Calendar.MONTH);
      month = month + 1;                         // adjust our new month

      if ((month == 1) && (day == 1)) {
         cal.roll(Calendar.YEAR,true);           // adjust year if starting a new one
      }

      ind = ind - 1;

   }   // end of while

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   try {
      //
      //  Determine if 5-somes are supported on this course
      //
      if (course.equals( "-ALL-" )) {

         pstmt = con.prepareStatement (
            "SELECT fives FROM clubparm2 WHERE courseName != ''");

         pstmt.clearParameters();        // clear the parms
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            tfives = rs.getInt(1);      // 5-some support (0 = No)

            if (tfives > 0) {           // if supported on any course

               fives = 1;               // indicate so
            }
         }
         pstmt.close();

      } else {

         pstmt = con.prepareStatement (
            "SELECT fives FROM clubparm2 WHERE courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, course);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            fives = rs.getInt(1);      // 5-some support (0 = No)
         }
         pstmt.close();
      }

      //
      //  Get all the lottery requests for the selected date and course
      //
      if (course.equals( "-ALL-" )) {

         pstmt = con.prepareStatement (
            "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
            "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, " +
            "player15, player16, player17, player18, player19, player20, player21, player22, player23, " +
            "player24, player25, fb, courseName, id, notes " +
            "FROM lreqs3 " +
            "WHERE date = ? " +
            "ORDER BY date, time");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);

      } else {

         pstmt = con.prepareStatement (
            "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, " +
            "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, " +
            "player15, player16, player17, player18, player19, player20, player21, player22, player23, " +
            "player24, player25, fb, courseName, id, notes " +
            "FROM lreqs3 " +
            "WHERE date = ? AND courseName = ? " +
            "ORDER BY date, time");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         pstmt.setString(2, course);
      }

      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         mm = rs.getInt(3);
         dd = rs.getInt(4);
         yy = rs.getInt(5);
         day_name = rs.getString(6);
      }
      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Proshop Lottery Requests Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<font size=\"3\">");
      out.println("<b>Current Lottery Requests</b><br><br>");

      out.println("</font><font size=\"2\">");
      out.println("<b>For " + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + " </b>");
      if (!course.equals( "" ) && !course.equals( "-ALL-" )) {
         out.println("<b>On " + course + "</b>");
      }

      out.println("<form method=\"get\" action=\"Proshop_mlottery\">");
      out.println("<input type=\"hidden\" name=\"requests\" value=\"\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      out.println("<b>To select a tee time</b>:  Just click on the button containing the time (1st column).");
      out.println("</font><font size=\"1\">");
      out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
      out.println("</font>");

         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
            out.println("<tr bgcolor=\"#336633\" style=\"color:white\"><td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Requested Time</b></u></p>");
                  out.println("</font></td>");

      if (course.equals( "-ALL-" )) {

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Course</b></u></p>");
                  out.println("</font></td>");
      }

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>F/B</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
                  out.println("</font></td>");

            if (fives != 0) {
               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
                  out.println("</font></td>");
            }

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Notes</b></u></p>");
                  out.println("</font></td>");

               out.println("</tr>");

      //
      //  Get each record and display it
      //
      count = 0;             // number of records found

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      while (rs.next()) {

         count++;

         lname = rs.getString(1);
         date = rs.getLong(2);
         mm = rs.getInt(3);
         dd = rs.getInt(4);
         yy = rs.getInt(5);
         day_name = rs.getString(6);
         hr = rs.getInt(7);
         min = rs.getInt(8);
         time = rs.getInt(9);
         player1 = rs.getString(10);
         player2 = rs.getString(11);
         player3 = rs.getString(12);
         player4 = rs.getString(13);
         player5 = rs.getString(14);
         player6 = rs.getString(15);
         player7 = rs.getString(16);
         player8 = rs.getString(17);
         player9 = rs.getString(18);
         player10 = rs.getString(19);
         player11 = rs.getString(20);
         player12 = rs.getString(21);
         player13 = rs.getString(22);
         player14 = rs.getString(23);
         player15 = rs.getString(24);
         player16 = rs.getString(25);
         player17 = rs.getString(26);
         player18 = rs.getString(27);
         player19 = rs.getString(28);
         player20 = rs.getString(29);
         player21 = rs.getString(30);
         player22 = rs.getString(31);
         player23 = rs.getString(32);
         player24 = rs.getString(33);
         player25 = rs.getString(34);
         fb = rs.getInt(35);
         courseName = rs.getString(36);
         lottid = rs.getLong(37);

         // Only continue if this lottery has not been processed yet
         if (SystemUtils.getLotteryState(date, mm, dd, yy, lname, courseName, con) < 4) {

             notes_exists = rs.getString("notes").length() > 0;

             ampm = " AM";
             if (hr == 12) {
                ampm = " PM";
             }
             if (hr > 12) {
                ampm = " PM";
                hr = hr - 12;    // convert to conventional time
             }

             if (player1.equals( "" )) {

                player1 = " ";       // make it a space for table display
             }
             if (player2.equals( "" )) {

                player2 = " ";       // make it a space for table display
             }
             if (player3.equals( "" )) {

                player3 = " ";       // make it a space for table display
             }
             if (player4.equals( "" )) {

                player4 = " ";       // make it a space for table display
             }
             if (player5.equals( "" )) {

                player5 = " ";       // make it a space for table display
             }
             if (player6.equals( "" )) {

                player6 = " ";       // make it a space for table display
             }
             if (player7.equals( "" )) {

                player7 = " ";       // make it a space for table display
             }
             if (player8.equals( "" )) {

                player8 = " ";       // make it a space for table display
             }
             if (player9.equals( "" )) {

                player9 = " ";       // make it a space for table display
             }
             if (player10.equals( "" )) {

                player10 = " ";       // make it a space for table display
             }
             if (player11.equals( "" )) {

                player11 = " ";       // make it a space for table display
             }
             if (player12.equals( "" )) {

                player12 = " ";       // make it a space for table display
             }
             if (player13.equals( "" )) {

                player13 = " ";       // make it a space for table display
             }
             if (player14.equals( "" )) {

                player14 = " ";       // make it a space for table display
             }
             if (player15.equals( "" )) {

                player15 = " ";       // make it a space for table display
             }
             if (player16.equals( "" )) {

                player16 = " ";       // make it a space for table display
             }
             if (player17.equals( "" )) {

                player17 = " ";       // make it a space for table display
             }
             if (player18.equals( "" )) {

                player18 = " ";       // make it a space for table display
             }
             if (player19.equals( "" )) {

                player19 = " ";       // make it a space for table display
             }
             if (player20.equals( "" )) {

                player20 = " ";       // make it a space for table display
             }
             if (player21.equals( "" )) {

                player21 = " ";       // make it a space for table display
             }
             if (player22.equals( "" )) {

                player22 = " ";       // make it a space for table display
             }
             if (player23.equals( "" )) {

                player23 = " ";       // make it a space for table display
             }
             if (player24.equals( "" )) {

                player24 = " ";       // make it a space for table display
             }
             if (player25.equals( "" )) {

                player25 = " ";       // make it a space for table display
             }

             //
             //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
             //
             sfb = "O";       // default Other

             if (fb == 1) {

                sfb = "B";
             }

             if (fb == 0) {

                sfb = "F";
             }

             //
             // if fives are allowed on the course then
             // check for a 5-some restriction at this time
             //
             if (fives != 0) {

                PreparedStatement pstmtr = con.prepareStatement (
                   "SELECT rest5 " +
                   "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                pstmtr.clearParameters();        // clear the parms
                pstmtr.setLong(1, date);
                pstmtr.setInt(2, time);
                pstmtr.setInt(3, fb);
                pstmtr.setString(4, courseName);
                rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                if (rs2.next()) {

                   rest = rs2.getString(1);

                } else {

                   rest = "";
                }
                pstmtr.close();
             }

             //
             //  get the slots value and determine the current state for this lottery
             //
             PreparedStatement pstmt7d = con.prepareStatement (
                "SELECT sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                "FROM lottery3 WHERE name = ?");

             pstmt7d.clearParameters();          // clear the parms
             pstmt7d.setString(1, lname);

             rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

             if (rs2.next()) {

                sdays = rs2.getInt(1);         // days in advance to start taking requests
                sdtime = rs2.getInt(2);
                edays = rs2.getInt(3);         // ...stop taking reqs
                edtime = rs2.getInt(4);
                pdays = rs2.getInt(5);         // ....to process reqs
                ptime = rs2.getInt(6);
                slots = rs2.getInt(7);

             }                  // end of while
             pstmt7d.close();

             //
             //    Determine which state we are in (before req's, during req's, before process, after process)
             //
             //  Get the current time
             //
             Calendar cal3 = new GregorianCalendar();    // get the current time of the day

             if (time_zone.equals( "Eastern" )) {         // Eastern Time = +1 hr

                cal3.add(Calendar.HOUR_OF_DAY,1);         // roll ahead 1 hour (rest should adjust)
             }

             if (time_zone.equals( "Mountain" )) {        // Mountain Time = -1 hr

                cal3.add(Calendar.HOUR_OF_DAY,-1);        // roll back 1 hour (rest should adjust)
             }

             if (time_zone.equals( "Pacific" )) {         // Pacific Time = -2 hrs

                cal3.add(Calendar.HOUR_OF_DAY,-2);        // roll back 2 hours (rest should adjust)
             }

             int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time - adjusted for time zone)
             int cal_min = cal3.get(Calendar.MINUTE);
             int curr_time = cal_hour * 100;
             curr_time = curr_time + cal_min;                // create military time

             //
             //  determine the number of days in advance of the req'd tee time we currently are
             //
             int cal_yy = cal3.get(Calendar.YEAR);
             int cal_mm = cal3.get(Calendar.MONTH);
             int cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

             cal_mm++;                            // month starts at zero
             advance_days = 0;

             while (cal_mm != mm && cal_dd != dd && cal_yy != yy) {

                cal3.add(Calendar.DATE,1);                // roll ahead 1 day untill a match found

                cal_yy = cal3.get(Calendar.YEAR);
                cal_mm = cal3.get(Calendar.MONTH);
                cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

                cal_mm++;                            // month starts at zero
                advance_days++;
             }

             //
             //  now check the day and time values
             //
             if (advance_days > sdays) {       // if we haven't reached the start day yet

                lstate = 1;                    // before time to take requests

             } else {

                if (advance_days == sdays) {   // if this is the start day

                   if (curr_time >= sdtime) {   // have we reached the start time?

                      lstate = 2;              // after start time, before stop time to take requests

                   } else {

                      lstate = 1;              // before time to take requests
                   }
                } else {                        // we are past the start day

                   lstate = 2;                 // after start time, before stop time to take requests
                }

                if (advance_days == edays) {   // if this is the stop day

                   if (curr_time >= edtime) {   // have we reached the stop time?

                      lstate = 3;              // after start time, before stop time to take requests
                   }
                }

                if (advance_days < edays) {   // if we are past the stop day

                   lstate = 3;                // after start time, before stop time to take requests
                }
             }

             if (lstate == 3) {                // if we are now in state 3, check for state 4

                if (advance_days == pdays) {   // if this is the process day

                   if (curr_time >= ptime) {    // have we reached the process time?

                      lstate = 4;              // after process time
                   }
                }

                if (advance_days < pdays) {   // if we are past the process day

                   lstate = 4;                // after process time
                }
             }

             submit = "time:" + fb;       // create a name for the submit button

             //
             //  Build the HTML for each record found
             //
             out.println("<tr>");
             out.println("<form action=\"Proshop_lott\" method=\"post\" target=\"_top\">");
             out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
             out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
             out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
             out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + course + "\">");
             out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lname + "\">");
             out.println("<input type=\"hidden\" name=\"lottid\" value=\"" + lottid + "\">");
             out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots + "\">");
             out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate + "\">");
             out.println("<input type=\"hidden\" name=\"index2\" value=\"" + index2 + "\">");  // needed for return
             out.println("<input type=\"hidden\" name=\"index\" value=777>");                  // indicate from here

             if (fives != 0) {
                out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
                if (!rest.equals( "" )) {          // if 5-somes are restricted

                   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"Yes\">");  // tell _lott
                } else {
                   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
                }
             } else {
                out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                out.println("<input type=\"hidden\" name=\"p5rest\" value=\"No\">");
             }
             out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + Utilities.ensureDoubleDigit(min) + ampm + "\">");
                out.println("</font></td>");

             if (course.equals( "-ALL-" )) {

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println(courseName);
                out.println("</font></td>");
             }

                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println(sfb);
                out.println("</font></td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( player1 );
                out.println("</font></td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( player2 );
                out.println("</font></td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( player3 );
                out.println("</font></td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( player4 );
                out.println("</font></td>");

             if (fives != 0 && rest.equals( "" )) {

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( player5 );
                out.println("</font></td>");

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( (notes_exists) ? "<a href=\"Proshop_mlottery?notes&id=" + lottid + "&course=" + courseName + "&lname=" + lname + "\" target=\"_notes\"><img src=\"/v5/images/notes.jpg\" border=0 alt=\"Notes\" title=\"Click here to view notes.\"></a>" : "&nbsp;" );
                out.println("</font></td>");

                //
                //  check if there are more than 5 players registered
                //
                if (!player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " ) || !player9.equals( " " ) || !player10.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player6 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player7 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player8 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player9 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player10 );
                   out.println("</font></td>");
                }

                if (!player11.equals( " " ) || !player12.equals( " " ) || !player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player11 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player12 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player13 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player14 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player15 );
                   out.println("</font></td>");
                }

                if (!player16.equals( " " ) || !player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player16 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player17 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player18 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player19 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player20 );
                   out.println("</font></td>");
                }

                if (!player21.equals( " " ) || !player22.equals( " " ) || !player23.equals( " " ) || !player24.equals( " " ) || !player25.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player21 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player22 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player23 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player24 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player25 );
                   out.println("</font></td>");
                }

             } else {   // no 5-somes

                // fivesomes are either not allowed on this course OR are restricted at this time

                if (fives != 0) {

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">&nbsp;</font></td>");
                }

                out.println("<td align=\"center\" bgcolor=\"white\">");
                out.println("<font size=\"2\">");
                out.println( (notes_exists) ? "<a href=\"Proshop_mlottery?notes&id=" + lottid + "&course=" + courseName + "&lname=" + lname + "\" target=\"_notes\"><img src=\"/v5/images/notes.jpg\" border=0 alt=\"Notes\" title=\"Click here to view notes.\"></a>" : "&nbsp;" );
                out.println("</font></td>");

                //
                //  check if there are more than 4 players registered
                //
                if (!player5.equals( " " ) || !player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player5 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player6 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player7 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player8 );
                   out.println("</font></td>");

                   if (fives != 0) {

                      out.println("<td align=\"center\" bgcolor=\"white\">");
                      out.println("<font size=\"2\">&nbsp;</font></td>");
                   }
                }

                if (!player9.equals( " " ) || !player10.equals( " " ) || !player11.equals( " " ) || !player12.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player9 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player10 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player11 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player12 );
                   out.println("</font></td>");

                   if (fives != 0) {

                      out.println("<td align=\"center\" bgcolor=\"white\">");
                      out.println("<font size=\"2\">&nbsp;</font></td>");
                   }
                }

                if (!player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " ) || !player16.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player13 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player14 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player15 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player16 );
                   out.println("</font></td>");

                   if (fives != 0) {

                      out.println("<td align=\"center\" bgcolor=\"white\">");
                      out.println("<font size=\"2\">&nbsp;</font></td>");
                   }
                }

                if (!player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

                   out.println("</tr><tr>");

                   out.println("<td align=\"center\">");      // time col
                   out.println("&nbsp;</td>");

                   if (course.equals( "-ALL-" )) {

                      out.println("<td align=\"center\">");       // course name
                      out.println("&nbsp;</td>");
                   }

                   out.println("<td align=\"center\">");       // f/b
                   out.println("&nbsp;</td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player17 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player18 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player19 );
                   out.println("</font></td>");

                   out.println("<td align=\"center\" bgcolor=\"white\">");
                   out.println("<font size=\"2\">");
                   out.println( player20 );
                   out.println("</font></td>");

                   if (fives != 0) {

                      out.println("<td align=\"center\" bgcolor=\"white\">");
                      out.println("<font size=\"2\">&nbsp;</font></td>");
                   }
                }
             }     // end of IF 5-somes
             out.println("</form></tr>");

         }
      }    // end of while

      pstmt.close();

      out.println("</font></table>");

      if (count == 0) {

         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">No lottery requests found for the date selected.</p>");
         out.println("</font>");
      }

      out.println("</td></tr></table>");                // end of main page table

      out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_mlottery\">");
         out.println("<input type=\"hidden\" name=\"requests\" value=\"\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</td><td>");
         out.println("<font size=\"4\">");        // add a space betwwen the buttons
         out.println("&nbsp;</font>");
         out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font>");
         out.println("</td></tr></table>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"Proshop_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }     // end of search function

 }   // end of doPost


 // ***************************************************************************
 //  Process a request to View/Approve Lottery Requests for a specific Date
 // ***************************************************************************

 private void approve(HttpServletRequest req, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name

   String name = "";
   String lname = "";
   String num = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String user7 = "";
   String user8 = "";
   String user9 = "";
   String user10 = "";
   String user11 = "";
   String user12 = "";
   String user13 = "";
   String user14 = "";
   String user15 = "";
   String user16 = "";
   String user17 = "";
   String user18 = "";
   String user19 = "";
   String user20 = "";
   String user21 = "";
   String user22 = "";
   String user23 = "";
   String user24 = "";
   String user25 = "";
   String ampm = "";
   String sfb = "";
   String submit = "";
   String rest = "";
   String time_zone = "";
   String day_name = "";
   String course = "";
   String returnCourse = "";
   String p5 = "";
   String range = "";
   String tampm = "";
   String tsfb = "";
   String dates = "";
   String days = "";
   String months = "";
   String years = "";
   String pmonth = "";
   String tlottery = "";

   String rcourse = "";
   String rindex = "";
   String rjump = "";

   long date = 0;
   long lottid = 0;

   int hr = 0;
   int min = 0;
   int time = 0;
   int count = 0;
   int fb = 0;
   int slots = 0;
   int lstate = 0;
   int advance_days = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int ind = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int day_num = 0;
   int fives = 0;
   int before = 0;
   int after = 0;
   int groups = 0;
   int state = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int ltime = 0;
   int ftime = 0;
   int afb = 0;
   int tfb = 0;
   int thr = 0;
   int tmin = 0;
   int weight = 0;
   int players = 0;
   int checkothers = 0;

   boolean weighted = false;
   boolean restriction = false;

   //
   //  Get the name of the course requested
   //
   course = req.getParameter("course");      // get the course name passed

   if (req.getParameter("returnCourse") != null) {

      returnCourse = req.getParameter("returnCourse");      // get the course name for the return (if multi)
   }

   //
   //  Check if return from approve2 below
   //
   if (req.getParameter("date2") != null) {

      dates = req.getParameter("date2");
      day_name = req.getParameter("day_name");
      days = req.getParameter("day");
      months = req.getParameter("month");
      years = req.getParameter("year");

      if (req.getParameter("sheet") != null) {     // if originally from Proshop_sheet

         rcourse = course;                         // save parms for return to Proshop_sheet
         rindex = req.getParameter("index");
         rjump = req.getParameter("jump");
      }

      if (req.getParameter("restriction") != null) {     // if any restrictions were hit during approval

         restriction = true;                             // prompt user to override
      }

      try {
         date = Long.parseLong(dates);
         day = Integer.parseInt(days);
         month = Integer.parseInt(months);
         year = Integer.parseInt(years);
      }
      catch (NumberFormatException e) {
      }

   } else {

      //
      //  Check if call from Proshop_sheet
      //
      if (req.getParameter("date") != null) {

         rcourse = course;                     // save parms for return to Proshop_sheet
         rindex = req.getParameter("index");
         rjump = req.getParameter("jump");
         dates = req.getParameter("date");
         day_name = req.getParameter("day");
         days = req.getParameter("dd");
         months = req.getParameter("mm");
         years = req.getParameter("yy");

         try {
            date = Long.parseLong(dates);
            day = Integer.parseInt(days);
            month = Integer.parseInt(months);
            year = Integer.parseInt(years);
         }
         catch (NumberFormatException e) {
         }

         pmonth = mm_table[month];                  // month name

      } else {    // call from doGet above or approve2 below

         //
         //************************************************************
         //       The name of the submit button is an index value
         //       (0 = today, 1 = tomorrow, etc.)
         //************************************************************
         //
         Enumeration enum1 = req.getParameterNames();     // get the parm names passed

         loop1:
         while (enum1.hasMoreElements()) {

            name = (String) enum1.nextElement();          // get name of parm

            if (name.startsWith( "i" )) {

               break loop1;                              // done - exit while loop
            }
         }

         //
         //  make sure we have the index value
         //
         if (!name.startsWith( "i" )) {

            out.println(SystemUtils.HeadTitle("Procedure Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Access Procdure Error</H3>");
            out.println("<BR><BR>Required Parameter is Missing - Prohop_mlottery.");
            out.println("<BR>Please exit and try again.");
            out.println("<BR><BR>If problem persists, report this error to ForeTees support.");
            out.println("<BR><BR>");
            out.println("<a href=\"Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         //
         //  Convert the index value from string to int
         //
         StringTokenizer tok = new StringTokenizer( name, "i" );     // space is the default token - use 'i'

         num = tok.nextToken();                        // get just the index number (name= parm must start with alpha)

         try {
            ind = Integer.parseInt(num);
         }
         catch (NumberFormatException e) {
            // ignore error
         }

         //
         //  Get today's date and then use the value passed to locate the requested date
         //
         Calendar cal = new GregorianCalendar();       // get todays date

         while (ind != 0) {                            // if not today

            cal.roll(Calendar.DATE,true);              // roll ahead one day

            day = cal.get(Calendar.DAY_OF_MONTH);      // get new day

            if (day == 1) {
               cal.roll(Calendar.MONTH,true);          // adjust month if starting a new one
            }

            month = cal.get(Calendar.MONTH);
            month = month + 1;                         // adjust our new month

            if ((month == 1) && (day == 1)) {
               cal.roll(Calendar.YEAR,true);           // adjust year if starting a new one
            }

            ind = ind - 1;

         }   // end of while

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

         month = month + 1;                            // month starts at zero

         day_name = day_table[day_num];                // get name for day

         pmonth = mm_table[month];                     // month name

         date = year * 10000;                          // create a date field of yyyymmdd
         date = date + (month * 100);
         date = date + day;                            // date = yyyymmdd (for comparisons)

         //
         //  if we came from approve2 below and originally came from sheet
         //
         if (req.getParameter("sheet") != null) {

            rcourse = course;                     // save parms for return to Proshop_sheet
            rindex = req.getParameter("index");
            rjump = req.getParameter("jump");
         }
      }
   }         // end of IFs for who called this


   try {
      //
      //  Determine if 5-somes are supported on this course
      //
      pstmt = con.prepareStatement (
         "SELECT fives FROM clubparm2 WHERE courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, course);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         fives = rs.getInt(1);      // 5-some support (0 = No)
      }
      pstmt.close();

      //
      //  See if there are any 'weighted' lottery requests for this date
      //
      pstmt = con.prepareStatement (
         "SELECT weight " +
         "FROM lreqs3 " +
         "WHERE date = ? AND courseName = ? AND weight > 0");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         int tmpWght = rs.getInt(1);      // get the weight

         if (tmpWght < 9999) {            // if a real weight

            weighted = true;                // indicate yes
         }
      }
      pstmt.close();

      //
      //  Get all the lottery requests for the selected date and course
      //
      pstmt = con.prepareStatement (
         "SELECT name, time, minsbefore, minsafter, " +
         "player1, player2, player3, player4, player5, player6, player7, player8, " +
         "player9, player10, player11, player12, player13, player14, player15, player16, " +
         "player17, player18, player19, player20, player21, player22, player23, player24, player25, " +
         "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
         "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
         "user21, user22, user23, user24, user25, " +
         "fb, id, groups, state, atime1, atime2, atime3, atime4, atime5, afb, p5, players, weight, checkothers " +
         "FROM lreqs3 " +
         "WHERE date = ? AND courseName = ? AND state > 0 " +
         "ORDER BY atime1, afb, time, fb");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Proshop Lottery Results Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<font size=\"3\">");
      out.println("<b>Current Lottery Results</b><br><br>");
      out.println("</font>");

      out.println("<table border=\"0\" align=\"center\" bgcolor=\"#336633\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>To approve the lottery results</b>: Change any times if necessary and then click on 'Submit' below.");
         out.println("</font><font size=\"1\">");
         out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
         out.println("</font>");
      out.println("</td></tr></table>");

      out.println("<font size=\"2\">");

      if (course.equals( "" )) {
         out.println("<p align=\"center\">Lottery Results For:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + pmonth + " " + day + ", " + year + "</b></p>");
      } else {
         out.println("<p align=\"center\">Lottery Results For:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + pmonth + " " + day + ", " + year + "</b>");
         out.println("  on Course: <b>" + course + "</b></p>");
      }

      if (weighted == true) {          // if any weighted lottery requests

         out.println("<p align=\"center\"><b>Note:</b>&nbsp;&nbsp;If the lottery type is 'Weighted By Rounds', then a higher weight indicates a lower priority.<br>");
         out.println("If the lottery type is 'Weighted By Proximity', then a higher weight indicates a higher priority.</p>");
      }

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
      out.println("<tr bgcolor=\"#336633\"><td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Req'd Time/Tee</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Acceptable Range</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Assigned Time/Tee</b></u></p>");
         out.println("</font></td>");

      if (!course.equals( "" )) {          // if course name, then must be mutiple courses
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Other<br>Course<br>Ok?</b></u></p>");
            out.println("</font></td>");
      }

      if (restriction == true) {          // if restrictions were encountered in previous approval
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Override<br>Restrictions?</b></u></p>");
            out.println("</font></td>");
      }

      if (weighted == true) {          // if any weighted lottery requests
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Weight</b></u></p>");
            out.println("</font></td>");
      }

      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 1</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 2</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 3</b></u></p>");
         out.println("</font></td>");

      out.println("<td>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<p align=\"center\"><u><b>Player 4</b></u></p>");
         out.println("</font></td>");

      if (fives != 0) {
         out.println("<td>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"center\"><u><b>Player 5</b></u></p>");
            out.println("</font></td>");
      }
      out.println("</tr>");
      out.println("<form method=\"post\" action=\"Proshop_mlottery\">");
      out.println("<input type=\"hidden\" name=\"approve2\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
      out.println("<input type=\"hidden\" name=\"month\" value=\"" + month + "\">");
      out.println("<input type=\"hidden\" name=\"year\" value=\"" + year + "\">");

      if (req.getParameter("date") != null || req.getParameter("sheet") != null) { // if came from Proshop_sheet

         out.println("<input type=\"hidden\" name=\"sheet\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"rcourse\" value=\"" + rcourse + "\">");
         out.println("<input type=\"hidden\" name=\"rindex\" value=\"" + rindex + "\">");
         out.println("<input type=\"hidden\" name=\"rjump\" value=\"" + rjump + "\">");
      }

      //
      //  Get each record and display it
      //
      count = 0;             // number of records found

      while (rs.next()) {

         count++;                        // also used as index value for parms

         lname = rs.getString(1);
         time = rs.getInt(2);
         before = rs.getInt(3);
         after = rs.getInt(4);
         player1 = rs.getString(5);
         player2 = rs.getString(6);
         player3 = rs.getString(7);
         player4 = rs.getString(8);
         player5 = rs.getString(9);
         player6 = rs.getString(10);
         player7 = rs.getString(11);
         player8 = rs.getString(12);
         player9 = rs.getString(13);
         player10 = rs.getString(14);
         player11 = rs.getString(15);
         player12 = rs.getString(16);
         player13 = rs.getString(17);
         player14 = rs.getString(18);
         player15 = rs.getString(19);
         player16 = rs.getString(20);
         player17 = rs.getString(21);
         player18 = rs.getString(22);
         player19 = rs.getString(23);
         player20 = rs.getString(24);
         player21 = rs.getString(25);
         player22 = rs.getString(26);
         player23 = rs.getString(27);
         player24 = rs.getString(28);
         player25 = rs.getString(29);
         user1 = rs.getString(30);
         user2 = rs.getString(31);
         user3 = rs.getString(32);
         user4 = rs.getString(33);
         user5 = rs.getString(34);
         user6 = rs.getString(35);
         user7 = rs.getString(36);
         user8 = rs.getString(37);
         user9 = rs.getString(38);
         user10 = rs.getString(39);
         user11 = rs.getString(40);
         user12 = rs.getString(41);
         user13 = rs.getString(42);
         user14 = rs.getString(43);
         user15 = rs.getString(44);
         user16 = rs.getString(45);
         user17 = rs.getString(46);
         user18 = rs.getString(47);
         user19 = rs.getString(48);
         user20 = rs.getString(49);
         user21 = rs.getString(50);
         user22 = rs.getString(51);
         user23 = rs.getString(52);
         user24 = rs.getString(53);
         user25 = rs.getString(54);
         fb = rs.getInt(55);
         lottid = rs.getLong(56);
         groups = rs.getInt(57);
         state = rs.getInt(58);
         atime1 = rs.getInt(59);
         atime2 = rs.getInt(60);
         atime3 = rs.getInt(61);
         atime4 = rs.getInt(62);
         atime5 = rs.getInt(63);
         afb = rs.getInt(64);
         p5 = rs.getString(65);
         players = rs.getInt(66);
         weight = rs.getInt(67);
         checkothers = rs.getInt(68);

         if (player1.equals( "" )) {

            player1 = " ";       // make it a space for table display
         }
         if (player2.equals( "" )) {

            player2 = " ";       // make it a space for table display
         }
         if (player3.equals( "" )) {

            player3 = " ";       // make it a space for table display
         }
         if (player4.equals( "" )) {

            player4 = " ";       // make it a space for table display
         }
         if (player5.equals( "" )) {

            player5 = " ";       // make it a space for table display
         }
         if (player6.equals( "" )) {

            player6 = " ";       // make it a space for table display
         }
         if (player7.equals( "" )) {

            player7 = " ";       // make it a space for table display
         }
         if (player8.equals( "" )) {

            player8 = " ";       // make it a space for table display
         }
         if (player9.equals( "" )) {

            player9 = " ";       // make it a space for table display
         }
         if (player10.equals( "" )) {

            player10 = " ";       // make it a space for table display
         }
         if (player11.equals( "" )) {

            player11 = " ";       // make it a space for table display
         }
         if (player12.equals( "" )) {

            player12 = " ";       // make it a space for table display
         }
         if (player13.equals( "" )) {

            player13 = " ";       // make it a space for table display
         }
         if (player14.equals( "" )) {

            player14 = " ";       // make it a space for table display
         }
         if (player15.equals( "" )) {

            player15 = " ";       // make it a space for table display
         }
         if (player16.equals( "" )) {

            player16 = " ";       // make it a space for table display
         }
         if (player17.equals( "" )) {

            player17 = " ";       // make it a space for table display
         }
         if (player18.equals( "" )) {

            player18 = " ";       // make it a space for table display
         }
         if (player19.equals( "" )) {

            player19 = " ";       // make it a space for table display
         }
         if (player20.equals( "" )) {

            player20 = " ";       // make it a space for table display
         }
         if (player21.equals( "" )) {

            player21 = " ";       // make it a space for table display
         }
         if (player22.equals( "" )) {

            player22 = " ";       // make it a space for table display
         }
         if (player23.equals( "" )) {

            player23 = " ";       // make it a space for table display
         }
         if (player24.equals( "" )) {

            player24 = " ";       // make it a space for table display
         }
         if (player25.equals( "" )) {

            player25 = " ";       // make it a space for table display
         }

         hr = time / 100;
         min = time - (hr * 100);

         ampm = " AM";
         if (hr == 12) {
            ampm = " PM";
         }
         if (hr > 12) {
            ampm = " PM";
            hr = hr - 12;    // convert to conventional time
         }

         //
         //  Create the 'acceptable time range' string
         //
         ftime = time;   // init
         ltime = time;

         if (before > 0) {

            ftime = Common_Lott.getFirstTime(time, before);    // get earliest time for this request
         }

         if (after > 0) {

            ltime = Common_Lott.getLastTime(time, after);     // get latest time for this request
         }

         thr = ftime/100;
         tmin = ftime - (thr * 100);
         if (thr == 0) {
            if (tmin < 10) {
               range = "12:0" + tmin + " AM - ";
            } else {
               range = "12:" + tmin + " AM - ";
            }
         } else {
            if (thr == 12) {
               if (tmin < 10) {
                  range = "12:0" + tmin + " PM - ";
               } else {
                  range = "12:" + tmin + " PM - ";
               }
            } else {
               if (thr > 12) {
                  thr = thr - 12;
                  if (tmin < 10) {
                     range = thr + ":0" + tmin + " PM - ";
                  } else {
                     range = thr + ":" + tmin + " PM - ";
                  }
               } else {
                  if (tmin < 10) {
                     range = thr + ":0" + tmin + " AM - ";
                  } else {
                     range = thr + ":" + tmin + " AM - ";
                  }
               }
            }
         }
         thr = ltime/100;
         tmin = ltime - (thr * 100);
         if (thr == 0) {
            if (tmin < 10) {
               range = range + "12:0" + tmin + " AM";
            } else {
               range = range + "12:" + tmin + " AM";
            }
         } else {
            if (thr == 12) {
               if (tmin < 10) {
                  range = range + "12:0" + tmin + " PM";
               } else {
                  range = range + "12:" + tmin + " PM";
               }
            } else {
               if (thr > 12) {
                  thr = thr - 12;
                  if (tmin < 10) {
                     range = range + thr + ":0" + tmin + " PM";
                  } else {
                     range = range + thr + ":" + tmin + " PM";
                  }
               } else {
                  if (tmin < 10) {
                     range = range + thr + ":0" + tmin + " AM";
                  } else {
                     range = range + thr + ":" + tmin + " AM";
                  }
               }
            }
         }

         //
         //  Process the F/B parm    0 = Front 9, 1 = Back 9
         //
         sfb = "  F";       // default Other

         if (fb == 1) {

            sfb = "  B";
         }

         out.println("<input type=\"hidden\" name=\"lottid" + count + "\" value=\"" + lottid + "\">");
         out.println("<input type=\"hidden\" name=\"players" + count + "\" value=\"" + players + "\">");

         //
         //  Build the HTML for each record found
         //
         out.println("<tr>");
         out.println("<td align=\"center\">");                  // requested time/tee
            out.println("<font size=\"2\">");
         if (min < 10) {
            out.println(hr + ":0" + min + ampm + sfb);
         } else {
            out.println(hr + ":" + min + ampm + sfb);
         }
            out.println("</font></td>");

         out.println("<td align=\"center\">");                  // acceptable time range
            out.println("<font size=\"2\">");
            out.println(range);
            out.println("</font></td>");

         out.println("<td align=\"center\">");                 // assigned time
            out.println("<font size=\"2\">");
            out.println("<select size=\"1\" name=\"g1atime" + count + "\">");   // atime parm (time and f/b)

            if (atime1 == 0) {         // if not assigned

               hr = 0;
               min = 0;    // init for assigned time
               out.println("<option selected value=\"0\">Not Assigned</option>");

            } else {           // determine time values for assigned time

               hr = atime1/100;
               min = atime1 - (hr * 100);

               ampm = " AM";
               if (hr == 12) {
                  ampm = " PM";
               }
               if (hr > 12) {
                  ampm = " PM";
                  hr = hr - 12;    // convert to conventional time
               }
            }
            //
            //  Find all the matching available tee times for this lottery
            //
            PreparedStatement pstmtd1 = null;

            pstmtd1 = con.prepareStatement (
               "SELECT hr, min, fb, lottery " +
               "FROM teecurr2 " +
               "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND " +
               "player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
               "ORDER BY time, fb");

            pstmtd1.clearParameters();          // clear the parms
            pstmtd1.setLong(1, date);
            pstmtd1.setString(2, course);

            rs2 = pstmtd1.executeQuery();      // find all matching lottery times

            while (rs2.next()) {

              thr  = rs2.getInt(1);
              tmin  = rs2.getInt(2);
              tfb  = rs2.getInt(3);
              tlottery  = rs2.getString(4);

              if (tfb < 2) {          // not a cross-over time

                 if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                    tampm = " AM";
                    if (thr == 12) {
                       tampm = " PM";
                    }
                    if (thr > 12) {
                       thr = thr - 12;
                       tampm = " PM";
                    }
                    if (tfb == 0) {
                       tsfb = "  F";
                    } else {
                       tsfb = "  B";
                    }

                    if (tmin < 10) {
                       if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                          out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                       } else {
                          out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                       }
                    } else {
                       if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                          out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                       } else {
                          out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                       }
                    }
                 }
              }
            }                  // end of while
            pstmtd1.close();

            out.println("</select>");
            out.println("</font></td>");

            if (!course.equals( "" )) {                               // if multiple courses
               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               if (checkothers == 0) {
                  out.println("No");
               } else {
                  out.println("Yes");
               }
               out.println("</font></td>");
            }

            if (restriction == true) {          // if restrictions were encountered in previous approval

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println("<input type=\"checkbox\" name=\"oriderest" + count + "\"  value=\"1\">");
               out.println("</font></td>");
            }

            if (weighted == true) {          // if any weighted lottery requests
               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( weight );
               out.println("</font></td>");
            }

            out.println("<td align=\"center\" bgcolor=\"white\">");        // players
            out.println("<font size=\"2\">");
            out.println( player1 );
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println( player2 );
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println( player3 );
            out.println("</font></td>");

            out.println("<td align=\"center\" bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println( player4 );
            out.println("</font></td>");

         if (p5.equals( "Yes" )) {
            out.println("<td align=\"center\" bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println( player5 );
            out.println("</font></td>");

            //
            //  check if there are more than 5 players registered
            //
            if (!player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " ) || !player9.equals( " " ) || !player10.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");      // req time col
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // range col
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g2atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime2 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime2/100;
                     min = atime2 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player6 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player7 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player8 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player9 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player10 );
               out.println("</font></td>");
            }

            if (!player11.equals( " " ) || !player12.equals( " " ) || !player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g3atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime3 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime3/100;
                     min = atime3 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player11 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player12 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player13 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player14 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player15 );
               out.println("</font></td>");
            }

            if (!player16.equals( " " ) || !player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g4atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime4 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime4/100;
                     min = atime4 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player16 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player17 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player18 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player19 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player20 );
               out.println("</font></td>");
            }

            if (!player21.equals( " " ) || !player22.equals( " " ) || !player23.equals( " " ) || !player24.equals( " " ) || !player25.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g5atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime5 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime5/100;
                     min = atime5 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player21 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player22 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player23 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player24 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player25 );
               out.println("</font></td>");
            }

         } else {   // no 5-somes

            //
            //  check if there are more than 4 players registered
            //
            if (!player5.equals( " " ) || !player6.equals( " " ) || !player7.equals( " " ) || !player8.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g2atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime2 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime2/100;
                     min = atime2 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player5 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player6 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player7 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player8 );
               out.println("</font></td>");
            }

            if (!player9.equals( " " ) || !player10.equals( " " ) || !player11.equals( " " ) || !player12.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g3atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime3 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime3/100;
                     min = atime3 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player9 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player10 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player11 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player12 );
               out.println("</font></td>");
            }

            if (!player13.equals( " " ) || !player14.equals( " " ) || !player15.equals( " " ) || !player16.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g4atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime4 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime4/100;
                     min = atime4 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player13 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player14 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player15 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player16 );
               out.println("</font></td>");
            }

            if (!player17.equals( " " ) || !player18.equals( " " ) || !player19.equals( " " ) || !player20.equals( " " )) {

               out.println("</tr><tr>");
               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");
               out.println("&nbsp;</td>");

               out.println("<td align=\"center\">");      // assigned time
                  out.println("<font size=\"2\">");
                  out.println("<select size=\"1\" name=\"g5atime" + count + "\">");   // atime parm (time and f/b)

                  if (atime5 == 0) {         // if not assigned

                     hr = 0;
                     min = 0;    // init for assigned time
                     out.println("<option selected value=\"0\">Not Assigned</option>");

                  } else {           // determine time values for assigned time

                     hr = atime5/100;
                     min = atime5 - (hr * 100);

                     ampm = " AM";
                     if (hr == 12) {
                        ampm = " PM";
                     }
                     if (hr > 12) {
                        ampm = " PM";
                        hr = hr - 12;    // convert to conventional time
                     }
                  }
                  //
                  //  Find all the matching available tee times for this lottery
                  //
                  pstmtd1 = con.prepareStatement (
                     "SELECT hr, min, fb, lottery " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' " +
                     "AND in_use = 0 AND player5 = '' AND courseName = ? AND event = '' AND blocker = '' " +
                     "ORDER BY time, fb");

                  pstmtd1.clearParameters();          // clear the parms
                  pstmtd1.setLong(1, date);
                  pstmtd1.setString(2, course);

                  rs2 = pstmtd1.executeQuery();      // find all matching lottery times

                  while (rs2.next()) {

                    thr  = rs2.getInt(1);
                    tmin  = rs2.getInt(2);
                    tfb  = rs2.getInt(3);
                    tlottery  = rs2.getString(4);

                    if (tfb < 2) {          // not a cross-over time

                       if ((req.getParameter("all") != null) || (tlottery.equals( lname ))) {  // if display all or lottery name matches

                          tampm = " AM";
                          if (thr == 12) {
                             tampm = " PM";
                          }
                          if (thr > 12) {
                             thr = thr - 12;
                             tampm = " PM";
                          }
                          if (tfb == 0) {
                             tsfb = "  F";
                          } else {
                             tsfb = "  B";
                          }

                          if (tmin < 10) {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":0" + tmin + tampm + tsfb + "</option>");
                             }
                          } else {
                             if ((thr == hr) && (tmin == min) && (tfb == afb) && (tampm.equals( ampm ))) {
                                out.println("<option selected value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             } else {
                                out.println("<option value=\"" + thr + ":" + tmin + tampm + " " + tfb + "\">" + thr + ":" + tmin + tampm + tsfb + "</option>");
                             }
                          }
                       }
                    }
                  }                  // end of while
                  pstmtd1.close();
                  out.println("</select>");
               out.println("</font></td>");

               if (!course.equals( "" )) {                               // if multiple courses
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println(" ");
                  out.println("</font></td>");
               }

               if (restriction == true) {          // if restrictions were encountered in previous approval

                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println("&nbsp;");
                  out.println("</font></td>");
               }

               if (weighted == true) {          // if any weighted lottery requests
                  out.println("<td align=\"center\" bgcolor=\"white\">");
                  out.println("<font size=\"2\">");
                  out.println( " " );
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player17 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player18 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player19 );
               out.println("</font></td>");

               out.println("<td align=\"center\" bgcolor=\"white\">");
               out.println("<font size=\"2\">");
               out.println( player20 );
               out.println("</font></td>");
            }
         }     // end of IF 5-somes
         out.println("</tr>");

      }    // end of while

      pstmt.close();

      out.println("</font></table>");

      out.println("<input type=\"hidden\" name=\"count\" value=\"" + count + "\">");

      if (count == 0) {

         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">No lottery requests found for the date selected.</p>");
         out.println("</font>");
      }

      out.println("</td></tr></table>");                // end of main page table
      out.println("<br></font>");

      out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr><td>");
         out.println("<font size=\"2\">");
         out.println("<input type=\"submit\" value=\"Submit\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font>");
         out.println("</td><td>");
         out.println("<font size=\"4\">");        // add a space betwwen the buttons
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</font>");
         if (req.getParameter("date") != null) {  // if came from Proshop_sheet
            out.println("<form method=\"post\" action=\"Proshop_jump\" target=\"_top\">");
            if (!returnCourse.equals( "" )) {
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + rcourse + "\">");
            }
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + rindex + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + rjump + "\">");
         } else {
            out.println("<form method=\"get\" action=\"Proshop_mlottery\">");
            out.println("<input type=\"hidden\" name=\"approve\" value=\"\">");
         }
         out.println("</td><td>");
         out.println("<font size=\"2\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font>");
      out.println("</td></tr></table>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"Proshop_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }     // end of search function

 }  // end of approve


 // ***************************************************************************
 //  Process the Approved Lottery Requests for a specific Date
 //    Proshop has set the Assigned Times and hit submit.
 // ***************************************************************************

 private void approve2(HttpServletRequest req, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name

   String sfb = "";
   String ampm = "";
   String shr = "";
   String smin = "";
   String sid = "";
   String name1 = "";          // lottery names (max of 3 per day)
   String name2 = "";
   String name3 = "";
   String name = "";
   String day_name = "";
   String day = "";
   String month = "";
   String year = "";

   String rcourse = "";
   String returnCourse = "";
   String rindex = "";
   String rjump = "";

   String error_hdr = "";
   String error_msg = "";

   long date = 0;
   long lottid = 0;

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int count = 0;
   int count2 = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int oriderest = 0;

   byte fb = 0;
   byte fb1 = 0;
   byte fb2 = 0;
   byte fb3 = 0;
   byte fb4 = 0;
   byte fb5 = 0;

   StringTokenizer tok = null;

   boolean unass = false;
   boolean dup = false;
   boolean restricted = false;
   boolean restError = false;

   //
   //  Arrays to hold the requests (max = 100)
   //
   long [] idA = new long [100];          // request id (lottid)
   int [] orideA = new int [100];         // override restrictions option
//   int [] playersA = new int [100];     // # of players in the request (need if we are going to try to combine groups with dup times)
   int [] g1timeA = new int [100];        // assigned time group 1
   int [] g2timeA = new int [100];        // assigned time group 2
   int [] g3timeA = new int [100];        // assigned time group 3
   int [] g4timeA = new int [100];        // assigned time group 4
   int [] g5timeA = new int [100];        // assigned time group 5
   byte [] g1fbA = new byte [100];        // assigned fb (0=front, 1=back) group 1
   byte [] g2fbA = new byte [100];        // assigned fb group 2
   byte [] g3fbA = new byte [100];        // assigned fb group 3
   byte [] g4fbA = new byte [100];        // assigned fb group 4
   byte [] g5fbA = new byte [100];        // assigned fb group 5

   //
   //  get parms that were passed
   //
   String course = req.getParameter("course");
   String sdate = req.getParameter("date");      // date of these requests
   String scount = req.getParameter("count");    // get number of entries to process

   if (req.getParameter("returnCourse") != null) {

      returnCourse = req.getParameter("returnCourse");
   }

   //
   //  Check if call from Approve above
   //
   if (req.getParameter("day_name") != null) {

      day_name = req.getParameter("day_name");
      day = req.getParameter("day");
      month = req.getParameter("month");
      year = req.getParameter("year");
   }

   //
   //  Check if call from Proshop_sheet
   //
   if (req.getParameter("sheet") != null) {

      rcourse = req.getParameter("rcourse");
      rindex = req.getParameter("rindex");
      rjump = req.getParameter("rjump");
   }

   try {
      count = Integer.parseInt(scount);
      date = Long.parseLong(sdate);
   }
   catch (NumberFormatException e) {
   }

   //
   //  Get all the parms that were passed - 'count' determines the number of requests that are included
   //
   String si = "";
   String parm = "";
   String parm1 = "lottid";      // set parm names
   String parm2 = "g1atime";
   String parm3 = "g2atime";
   String parm4 = "g3atime";
   String parm5 = "g4atime";
   String parm6 = "g5atime";
   String parm7 = "oriderest";
   i = 0;                 // init index

   loop1:
   while (count > 0 && i < 100) {

      si = String.valueOf( i + 1 );     // create string index
      parm = parm1 + si;                // create parm name - lottid_

      if (req.getParameter( parm ) != null) {

         //
         //  Process lottery id
         //
         sid = req.getParameter( parm );       // get the lottid

         try {
           idA[i] = Long.parseLong(sid);     // put in array
         }
         catch (NumberFormatException e) {
         }

         //
         //     Process Group 1's Override Restrictions Option if exists
         //
         parm = parm7 + si;                       // create parm name - oriderest_

         if (req.getParameter( parm ) != null) {  // if it was included

            sid = req.getParameter( parm );       // get the option value

            try {
              orideA[i] = Integer.parseInt(sid);
            }
            catch (NumberFormatException e) {
            }
         }

         //
         //     Process Group 1's Time and Tee (hr:min AM/PM f/b)
         //
         parm = parm2 + si;                    // create parm name - g1atime_

         sid = req.getParameter( parm );       // get the assigned time for grp 1

         if (sid.equals( "0" )) {              // if zero = Not Assigned

            time = 0;
            fb = 0;

         } else {

            tok = new StringTokenizer( sid, ": " );       // use colon and space for separators

            shr = tok.nextToken();                        // get the hour value
            smin = tok.nextToken();                       // get the minute value
            ampm = tok.nextToken();                       // get the AM/PM value
            sfb = tok.nextToken();                        // get the front/back indicator from name of submit button

            try {
              hr = Integer.parseInt(shr);
              min = Integer.parseInt(smin);
              fb = new Byte(sfb).byteValue();
            }
            catch (NumberFormatException e) {
            }

            if (hr != 12 && ampm.equals( "PM" )) {

               hr = hr + 12;     // create PM time
            }

            time = (hr * 100) + min;
         }

         g1timeA[i] = time;                        // save time in array
         g1fbA[i] = fb;                            // save fb in array

         //
         //     Process Group 2's Time and Tee (hr:min AM/PM f/b)
         //
         parm = parm3 + si;                        // create parm name - g2atime_

         if (req.getParameter( parm ) != null) {

            sid = req.getParameter( parm );       // get the assigned time for grp 2

            if (sid.equals( "0" )) {              // if zero = Not Assigned

               time = 0;
               fb = 0;

            } else {

               tok = new StringTokenizer( sid, ": " );       // use colon and space for separators

               shr = tok.nextToken();                        // get the hour value
               smin = tok.nextToken();                       // get the minute value
               ampm = tok.nextToken();                       // get the AM/PM value
               sfb = tok.nextToken();                        // get the front/back indicator from name of submit button

               try {
                 hr = Integer.parseInt(shr);
                 min = Integer.parseInt(smin);
                 fb = new Byte(sfb).byteValue();
               }
               catch (NumberFormatException e) {
               }

               if (hr != 12 && ampm.equals( "PM" )) {

                  hr = hr + 12;     // create PM time
               }

               time = (hr * 100) + min;
            }

            g2timeA[i] = time;                        // save time in array
            g2fbA[i] = fb;                            // save fb in array

            //
            //     Process Group 3's Time and Tee (hr:min AM/PM f/b)
            //
            parm = parm4 + si;                       // create parm name - g3atime_

            if (req.getParameter( parm ) != null) {

               sid = req.getParameter( parm );       // get the assigned time for grp 3

               if (sid.equals( "0" )) {              // if zero = Not Assigned

                  time = 0;
                  fb = 0;

               } else {

                  tok = new StringTokenizer( sid, ": " );       // use colon and space for separators

                  shr = tok.nextToken();                        // get the hour value
                  smin = tok.nextToken();                       // get the minute value
                  ampm = tok.nextToken();                       // get the AM/PM value
                  sfb = tok.nextToken();                        // get the front/back indicator from name of submit button

                  try {
                    hr = Integer.parseInt(shr);
                    min = Integer.parseInt(smin);
                    fb = new Byte(sfb).byteValue();
                  }
                  catch (NumberFormatException e) {
                  }

                  if (hr != 12 && ampm.equals( "PM" )) {

                     hr = hr + 12;     // create PM time
                  }

                  time = (hr * 100) + min;
               }

               g3timeA[i] = time;                        // save time in array
               g3fbA[i] = fb;                            // save fb in array

               //
               //     Process Group 4's Time and Tee (hr:min AM/PM f/b)
               //
               parm = parm5 + si;                       // create parm name - g4atime_

               if (req.getParameter( parm ) != null) {

                  sid = req.getParameter( parm );       // get the assigned time for grp 4

                  if (sid.equals( "0" )) {              // if zero = Not Assigned

                     time = 0;
                     fb = 0;

                  } else {

                     tok = new StringTokenizer( sid, ": " );       // use colon and space for separators

                     shr = tok.nextToken();                        // get the hour value
                     smin = tok.nextToken();                       // get the minute value
                     ampm = tok.nextToken();                       // get the AM/PM value
                     sfb = tok.nextToken();                        // get the front/back indicator from name of submit button

                     try {
                       hr = Integer.parseInt(shr);
                       min = Integer.parseInt(smin);
                       fb = new Byte(sfb).byteValue();
                     }
                     catch (NumberFormatException e) {
                     }

                     if (hr != 12 && ampm.equals( "PM" )) {

                        hr = hr + 12;     // create PM time
                     }

                     time = (hr * 100) + min;
                  }

                  g4timeA[i] = time;                        // save time in array
                  g4fbA[i] = fb;                            // save fb in array

                  //
                  //     Process Group 5's Time and Tee (hr:min AM/PM f/b)
                  //
                  parm = parm6 + si;                       // create parm name - g5atime_

                  if (req.getParameter( parm ) != null) {

                     sid = req.getParameter( parm );       // get the assigned time for grp 5

                     if (sid.equals( "0" )) {              // if zero = Not Assigned

                        time = 0;
                        fb = 0;

                     } else {

                        tok = new StringTokenizer( sid, ": " );       // use colon and space for separators

                        shr = tok.nextToken();                        // get the hour value
                        smin = tok.nextToken();                       // get the minute value
                        ampm = tok.nextToken();                       // get the AM/PM value
                        sfb = tok.nextToken();                        // get the front/back indicator from name of submit button

                        try {
                          hr = Integer.parseInt(shr);
                          min = Integer.parseInt(smin);
                          fb = new Byte(sfb).byteValue();
                        }
                        catch (NumberFormatException e) {
                        }

                        if (hr != 12 && ampm.equals( "PM" )) {

                           hr = hr + 12;     // create PM time
                        }

                        time = (hr * 100) + min;
                     }

                     g5timeA[i] = time;                        // save time in array
                     g5fbA[i] = fb;                            // save fb in array
                  }
               }
            }
         }

      } else {

         break loop1;    // done
      }
      i++;                // bump index
      count--;
   }

   //
   //  Process the lottery requests passed - look for unassigned times
   //
   count = i + 1;         // reset count
   i = 0;                 // init index
   i3 = 0;                // init dup & unass counter

   loop2:
   while (count > 0 && i < 100) {

      lottid = idA[i];             // get lottery id, times and f/b's for each request

      if (lottid != 0) {           // if entry exist

         atime1 = g1timeA[i];
         atime2 = g2timeA[i];
         atime3 = g3timeA[i];
         atime4 = g4timeA[i];
         atime5 = g5timeA[i];

         //
         //  got a req - look for unassigned times (if 1 group is unassigned, then they all will be)
         //
         if (atime1 == 0) {

            g2timeA[i] = 0;
            g3timeA[i] = 0;
            g4timeA[i] = 0;
            g5timeA[i] = 0;
            unass = true;        // indicate at least one Unassigned request exists
            i3++;                // bump counter

         } else {               // group 1 is assigned - check the rest (these are optional)

            if (((atime2 == 0) && (atime3 != 0 || atime4 != 0 || atime5 != 0)) ||
                ((atime3 == 0) && (atime4 != 0 || atime5 != 0)) ||
                ((atime4 == 0) && (atime5 != 0))) {

               g1timeA[i] = 0;
               g2timeA[i] = 0;
               g3timeA[i] = 0;
               g4timeA[i] = 0;
               g5timeA[i] = 0;
               unass = true;        // indicate at least one Unassigned request exists
               i3++;                // bump counter
            }
         }

      } else {            // lottid is zero

         break loop2;     // exit - we're done
      }

      i++;                // bump index
      count--;
   }                      // end of WHILE lottery req's exist (loop2)

   //
   //  Now look for duplicate times
   //
   count = i + 1;         // reset count
   i = 0;                 // init index

   loop3:
   while (count > 0 && i < 100) {

      lottid = idA[i];             // get lottery id, times and f/b's for each request

      if (lottid != 0) {           // if entry exist

         atime1 = g1timeA[i];
         atime2 = g2timeA[i];
         atime3 = g3timeA[i];
         atime4 = g4timeA[i];
         atime5 = g5timeA[i];
         fb1 = g1fbA[i];
         fb2 = g2fbA[i];
         fb3 = g3fbA[i];
         fb4 = g4fbA[i];
         fb5 = g5fbA[i];

         if (atime1 != 0) {      // if assigned

            //
            //  Now look for duplicate times
            //
            i2 = i + 1;           // set new indexes to start with next request
            count2 = count - 1;

            loop4:
            while (count2 > 0 && i2 < 100) {

               if (idA[i2] != 0) {       // if next entry exist

                  if ((atime1 == g1timeA[i2] && fb1 == g1fbA[i2]) || (atime1 == g2timeA[i2] && fb1 == g2fbA[i2]) ||
                      (atime1 == g3timeA[i2] && fb1 == g3fbA[i2]) || (atime1 == g4timeA[i2] && fb1 == g4fbA[i2]) ||
                      (atime1 == g5timeA[i2] && fb1 == g5fbA[i2])) {

                     g1timeA[i] = 0;      // mark all duplicate times as Unassigned
                     g2timeA[i] = 0;
                     g3timeA[i] = 0;
                     g4timeA[i] = 0;
                     g5timeA[i] = 0;

                     g1timeA[i2] = 0;
                     g2timeA[i2] = 0;
                     g3timeA[i2] = 0;
                     g4timeA[i2] = 0;
                     g5timeA[i2] = 0;

                     dup = true;        // indicate at least one Duplicate request exists
                     i3 = i3 + 2;       // bump counter

                  } else {

                     if ((atime2 != 0) && ((atime2 == g1timeA[i2] && fb2 == g1fbA[i2]) ||
                         (atime2 == g2timeA[i2] && fb2 == g2fbA[i2]) || (atime2 == g3timeA[i2] && fb2 == g3fbA[i2]) ||
                         (atime2 == g4timeA[i2] && fb2 == g4fbA[i2]) || (atime2 == g5timeA[i2] && fb2 == g5fbA[i2]))) {

                        g1timeA[i] = 0;      // mark all duplicate times as Unassigned
                        g2timeA[i] = 0;
                        g3timeA[i] = 0;
                        g4timeA[i] = 0;
                        g5timeA[i] = 0;

                        g1timeA[i2] = 0;
                        g2timeA[i2] = 0;
                        g3timeA[i2] = 0;
                        g4timeA[i2] = 0;
                        g5timeA[i2] = 0;

                        dup = true;        // indicate at least one Duplicate request exists
                        i3 = i3 + 2;       // bump counter

                     } else {

                        if ((atime3 != 0) && ((atime3 == g1timeA[i2] && fb3 == g1fbA[i2]) ||
                            (atime3 == g2timeA[i2] && fb3 == g2fbA[i2]) || (atime3 == g3timeA[i2] && fb3 == g3fbA[i2]) ||
                            (atime3 == g4timeA[i2] && fb3 == g4fbA[i2]) || (atime3 == g5timeA[i2] && fb3 == g5fbA[i2]))) {

                           g1timeA[i] = 0;      // mark all duplicate times as Unassigned
                           g2timeA[i] = 0;
                           g3timeA[i] = 0;
                           g4timeA[i] = 0;
                           g5timeA[i] = 0;

                           g1timeA[i2] = 0;
                           g2timeA[i2] = 0;
                           g3timeA[i2] = 0;
                           g4timeA[i2] = 0;
                           g5timeA[i2] = 0;

                           dup = true;        // indicate at least one Duplicate request exists
                           i3 = i3 + 2;       // bump counter

                        } else {

                           if ((atime4 != 0) && ((atime4 == g1timeA[i2] && fb4 == g1fbA[i2]) ||
                               (atime4 == g2timeA[i2] && fb4 == g2fbA[i2]) || (atime4 == g3timeA[i2] && fb4 == g3fbA[i2]) ||
                               (atime4 == g4timeA[i2] && fb4 == g4fbA[i2]) || (atime4 == g5timeA[i2] && fb4 == g5fbA[i2]))) {

                              g1timeA[i] = 0;      // mark all duplicate times as Unassigned
                              g2timeA[i] = 0;
                              g3timeA[i] = 0;
                              g4timeA[i] = 0;
                              g5timeA[i] = 0;

                              g1timeA[i2] = 0;
                              g2timeA[i2] = 0;
                              g3timeA[i2] = 0;
                              g4timeA[i2] = 0;
                              g5timeA[i2] = 0;

                              dup = true;        // indicate at least one Duplicate request exists
                              i3 = i3 + 2;       // bump counter

                           } else {

                              if ((atime5 != 0) && ((atime5 == g1timeA[i2] && fb5 == g1fbA[i2]) ||
                                  (atime5 == g2timeA[i2] && fb5 == g2fbA[i2]) || (atime5 == g3timeA[i2] && fb5 == g3fbA[i2]) ||
                                  (atime5 == g4timeA[i2] && fb5 == g4fbA[i2]) || (atime5 == g5timeA[i2] && fb5 == g5fbA[i2]))) {

                                 g1timeA[i] = 0;      // mark all duplicate times as Unassigned
                                 g2timeA[i] = 0;
                                 g3timeA[i] = 0;
                                 g4timeA[i] = 0;
                                 g5timeA[i] = 0;

                                 g1timeA[i2] = 0;
                                 g2timeA[i2] = 0;
                                 g3timeA[i2] = 0;
                                 g4timeA[i2] = 0;
                                 g5timeA[i2] = 0;

                                 dup = true;        // indicate at least one Duplicate request exists
                                 i3 = i3 + 2;       // bump counter
                              }
                           }
                        }
                     }
                  }
               } else {

                  break loop4;     // exit - we're done
               }

               i2++;                // bump index
               count2--;
            }                       // end of WHILE to check for dups (loop3)

         }                // end of IF atime1 is not 0

      } else {            // lottid is zero

         break loop3;     // exit - we're done
      }

      i++;                // bump index
      count--;
   }                      // end of WHILE lottery req's exist (loop2)


   //
   //  Now process the restrictions for the assigned times to make sure the players are allowed during these times.
   //
   count = i + 1;         // reset count
   i = 0;                 // init index

   parmLott parmL = new parmLott();          // allocate a parm block

   loop7:
   while (count > 0 && i < 100) {

      lottid = idA[i];             // get lottery id, times and f/b's for each request

      if (lottid != 0) {           // if entry exist

         atime1 = g1timeA[i];
         fb1 = g1fbA[i];
         oriderest = orideA[i];

         if (atime1 != 0) {      // if assigned

            //
            //  Process restrictions for the group
            //
            restricted = false;                                    // init

            if (oriderest == 0) {                                  // if override not selected

               parmL.lottid = lottid;                    // set id

               //
               //  Now check if any members in this group are restricted during the assigned time
               //
               Common_Lott.getParmValues(parmL, con);    // go set the lottery parm values

               parmL.club = club;                        // set/override some other values
               parmL.course = course;
               parmL.fb = fb1;
               parmL.ind = 1;                            // index not pertinent for this - use 1 to get through restrictions
               parmL.time = atime1;                      // use time of first group

               restricted = Common_Lott.checkRests(0, parmL, con); // check restrictions (returns true if a member is restricted)

               if (restricted == true) {                           // if restriction hit

                  restError = true;                                // indicate this

                  g1timeA[i] = 0;                     // mark times as Unassigned
                  g2timeA[i] = 0;
                  g3timeA[i] = 0;
                  g4timeA[i] = 0;
                  g5timeA[i] = 0;

                  i3++;                               // bump # of unassigned times

                  error_hdr = parmL.error_hdr;        // save for response
                  error_msg = parmL.error_msg;
               }
            }
         }                // end of IF atime1 is not 0

      } else {            // lottid is zero

         break loop7;     // exit - we're done
      }

      i++;                // bump index
      count--;
   }                      // end of WHILE lottery req's exist (loop8)


   //
   //  Update the lottery req - set the times and fb's
   //
   count = i + 1;         // reset count
   i = 0;                 // init index
   i2 = 0;                // init request counter

   loop5:
   while (count > 0 && i < 100) {

      lottid = idA[i];             // get lottery id, times and f/b's for each request

      if (lottid != 0) {           // if entry exist

         atime1 = g1timeA[i];
         atime2 = g2timeA[i];
         atime3 = g3timeA[i];
         atime4 = g4timeA[i];
         atime5 = g5timeA[i];
         fb1 = g1fbA[i];
         fb2 = g2fbA[i];
         fb3 = g3fbA[i];
         fb4 = g4fbA[i];
         fb5 = g5fbA[i];

         try {
            //
            //  Set the new values in the lottery request
            //
            PreparedStatement pstmt9 = con.prepareStatement (
                "UPDATE lreqs3 SET state = 2, atime1 = ?, atime2 = ?, atime3 = ?, " +
                "atime4 = ?, atime5 = ?, afb = ?, afb2 = ?, afb3 = ?, afb4 = ?, afb5 = ? WHERE id = ?");

            pstmt9.clearParameters();        // clear the parms
            pstmt9.setInt(1, atime1);
            pstmt9.setInt(2, atime2);
            pstmt9.setInt(3, atime3);
            pstmt9.setInt(4, atime4);
            pstmt9.setInt(5, atime5);
            pstmt9.setInt(6, fb1);
            pstmt9.setInt(7, fb2);
            pstmt9.setInt(8, fb3);
            pstmt9.setInt(9, fb4);
            pstmt9.setInt(10, fb5);
            pstmt9.setLong(11, lottid);

            pstmt9.executeUpdate();

            pstmt9.close();

            i2++;                  // count number of req's updated

            //
            //  get the name of the lottery for this request
            //
            pstmt9 = con.prepareStatement (
               "SELECT name " +
               "FROM lreqs3 " +
               "WHERE id = ?");

            pstmt9.clearParameters();        // clear the parms
            pstmt9.setLong(1, lottid);
            rs = pstmt9.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               name = rs.getString(1);       // get the name of this lottery
            }
            pstmt9.close();

            if (name1.equals( "" )) {

               name1 = name;

            } else {

               if (name2.equals( "" ) && !name1.equals( name )) {

                  name2 = name;

               } else {

                  if (name3.equals( "" ) && !name1.equals( name ) && !name2.equals( name )) {

                     name3 = name;
                  }
               }
            }
         }
         catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER><BR>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Error:" + exc.getMessage());
            out.println("<BR><BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<br><br><a href=\"Proshop_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

      } else {            // lottid is zero

         break loop5;     // exit - we're done
      }

      i++;                // bump index
      count--;
   }                      // end of WHILE lottery req's exist (loop5)

   //
   //  if any req's were processed, then call sysutils to create the tee times
   //
   if (i2 > i3) {

      i2 = i2 - i3;      // reduce processed count by number of dups and unassigned reqs

   } else {

      i2 = 0;            // too many dups/unass
   }

   if (i2 > 0) {

      SystemUtils.moveReqs(name1, date, course, "lottprocess", true, con);         // do first lottery

      if (!name2.equals( "" )) {

         SystemUtils.moveReqs(name2, date, course, "lottprocess", true, con);      // do 2nd lottery if exists
      }

      if (!name3.equals( "" )) {

         SystemUtils.moveReqs(name3, date, course, "lottprocess", true, con);      // do 3rd lottery if exists
      }
   }

   //
   //  output page as response - either done or return to finish
   //
   out.println(SystemUtils.HeadTitle("Proshop Process Lottery Page"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, 1);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<br><p align=\"center\"><font size=\"4\">");
   out.println("Lottery Requests Processed</p><br><br>");

   out.println("</font><font size=\"3\">");
   out.println(i2 + " Lottery Requests were processed.");
   if (i2 > 0) {
      out.println("<br><br>");
      out.println("You can now access these tee times on the tee sheet.");
   }
   out.println("<br><br>");

   if (unass == true || dup == true || restError == true) {     // if more req's to assign

      if (unass == true) {                  // if more req's to assign

         out.println("There are still some unassigned requests to process.<br><br>");
      }

      if (dup == true) {                  // if dups found

         out.println("There were some requests with duplicate times assigned. These have been set to 'unassigned'.<br><br>");
      }

      if (restError == true) {           // if restriction hit

         out.println("There were one or more requests with a member that is restricted from the time assigned.<br>These have been set to 'unassigned'.<br>");
         out.println("<br><br>The following is the last error message received:<br>");
         out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
            out.println("<tr>");
            out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<H3>" +error_hdr+ "</H3>");
               out.println("<BR>" +error_msg+ "<BR>");
            out.println("</font></td></tr>");
         out.println("</table>");
      }
      out.println("You will now have access to all available tee times for this date when assigning times.<br>");

      out.println("<p align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<form method=\"post\" action=\"Proshop_mlottery\">");
      out.println("<input type=\"hidden\" name=\"approve\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"date2\" value=\"" + date + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"all\" value=\"yes\">");

      if (restError == true) {     // if a restriction was hit

         out.println("<input type=\"hidden\" name=\"restriction\" value=\"yes\">");
      }

      if (!day_name.equals( "" )) {      // parms required for Approve above

         out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
         out.println("<input type=\"hidden\" name=\"month\" value=\"" + month + "\">");
         out.println("<input type=\"hidden\" name=\"year\" value=\"" + year + "\">");
      }

      //
      //  Check if call from Proshop_sheet
      //
      if (req.getParameter("sheet") != null) {

         out.println("<input type=\"hidden\" name=\"sheet\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + rindex + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + rjump + "\">");
      }

      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; color:blue; background:#8B8970\">");
      out.println("</form>");
      out.println("</font></p>");

   } else {

      //
      //  Check if call from Proshop_sheet
      //
      if (req.getParameter("sheet") != null) {

         out.println("<p align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<form method=\"post\" action=\"Proshop_jump\" target=\"_top\">");
         if (!returnCourse.equals( "" )) {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         }
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + rindex + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + rjump + "\">");
         out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; color:blue; background:#8B8970\">");
         out.println("</form>");
         out.println("</font></p>");

      } else {

         out.println("<p align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"Proshop_mlottery\">");
         out.println("<input type=\"hidden\" name=\"approve\" value=\"\">");
         out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font></p>");
      }
   }

   out.println("</td></tr></table>");
   out.println("</center></font></body></html>");

   //
   //  Check each lottery (1, 2 & 3) to see if all requests have been processed.
   //  If so, set any remaining tee times as non-lottery to free them up.
   //
   try {

      PreparedStatement pstmt61 = null;
      PreparedStatement pstmt62 = null;

      pstmt61 = con.prepareStatement (
         "SELECT time " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ?");

      pstmt61.clearParameters();        // clear the parms
      pstmt61.setString(1, name1);
      pstmt61.setLong(2, date);
      pstmt61.setString(3, course);
      rs = pstmt61.executeQuery();

      if (!rs.next()) {            // if none

         //
         //*********************************************************************************
         //  Check for any empty slots remaining.  Set these as non-lottery so members
         //  can freely access them now.
         //*********************************************************************************
         //
         pstmt62 = con.prepareStatement (
            "UPDATE teecurr2 SET lottery = '' " +
            "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND in_use = 0 " +
            "AND player5 = '' AND lottery = ? AND courseName = ?");

         pstmt62.clearParameters();        // clear the parms
         pstmt62.setLong(1, date);
         pstmt62.setString(2, name1);
         pstmt62.setString(3, course);

         pstmt62.executeUpdate();      // execute the prepared stmt

         pstmt62.close();
      }
      pstmt61.close();

      //
      //  Do 2nd lottery for this date if it exists
      //
      if (!name2.equals( "" )) {

         pstmt61 = con.prepareStatement (
            "SELECT time " +
            "FROM lreqs3 " +
            "WHERE name = ? AND date = ? AND courseName = ?");

         pstmt61.clearParameters();        // clear the parms
         pstmt61.setString(1, name2);
         pstmt61.setLong(2, date);
         pstmt61.setString(3, course);
         rs = pstmt61.executeQuery();

         if (!rs.next()) {            // if none

            //
            //*********************************************************************************
            //  Check for any empty slots remaining.  Set these as non-lottery so members
            //  can freely access them now.
            //*********************************************************************************
            //
            pstmt62 = con.prepareStatement (
               "UPDATE teecurr2 SET lottery = '' " +
               "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND in_use = 0 " +
               "AND player5 = '' AND lottery = ? AND courseName = ?");

            pstmt62.clearParameters();        // clear the parms
            pstmt62.setLong(1, date);
            pstmt62.setString(2, name2);
            pstmt62.setString(3, course);

            pstmt62.executeUpdate();      // execute the prepared stmt

            pstmt62.close();
         }
         pstmt61.close();
      }

      //
      //  Do 3rd lottery for this date if it exists
      //
      if (!name3.equals( "" )) {

         pstmt61 = con.prepareStatement (
            "SELECT time " +
            "FROM lreqs3 " +
            "WHERE name = ? AND date = ? AND courseName = ?");

         pstmt61.clearParameters();        // clear the parms
         pstmt61.setString(1, name3);
         pstmt61.setLong(2, date);
         pstmt61.setString(3, course);
         rs = pstmt61.executeQuery();

         if (!rs.next()) {            // if none

            //
            //*********************************************************************************
            //  Check for any empty slots remaining.  Set these as non-lottery so members
            //  can freely access them now.
            //*********************************************************************************
            //
            pstmt62 = con.prepareStatement (
               "UPDATE teecurr2 SET lottery = '' " +
               "WHERE date = ? AND player1 = '' AND player2 = '' AND player3 = '' AND player4 = '' AND in_use = 0 " +
               "AND player5 = '' AND lottery = ? AND courseName = ?");

            pstmt62.clearParameters();        // clear the parms
            pstmt62.setLong(1, date);
            pstmt62.setString(2, name3);
            pstmt62.setString(3, course);

            pstmt62.executeUpdate();      // execute the prepared stmt

            pstmt62.close();
         }
         pstmt61.close();
      }
   }
   catch (Exception ignore) {
   }

 }  // end of approve2

}