/***************************************************************************************
 *   Hotel_oldsheets:  This servlet will process the 'View Old Tee Sheets' request from
 *                       the Hotel's navigation bar.
 *
 *
 *   called by:  Hotel_maintop (doGet)
 *               Hotel_oldsheets (doPost)
 *
 *
 *   created: 9/22/2005   Bob P. (per request from Cordillera)
 *
 *   last updated:
 *
 *        5/20/10   Changes for unlimited guest types, sql obj cleanup & comment out unused variables
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
//import java.lang.Math;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.parmSlot;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.BigDate;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Hotel_oldsheets extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the call from the menu
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   Statement stmt = null;
   ResultSet rs = null;
     
   int multi = 0;               // multiple course support
   int index= 0;
     
   long date = 0;                 // date returned - use for calendar
   long yy = 0;                   // year
   long mm = 0;                   // month
   long dd = 0;                   // day
   long old_date = 0;             // oldest date that tee sheets exist
   long old_mm = 0;               // oldest month
   long old_dd = 0;               // oldest day
   long old_yy = 0;               // oldest year
   //long new_date = 0;             // newest date that tee sheets exist (yesterday)
     
   String courseName = "";        // course names

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();


   if (req.getParameter("post") != null) {      // if call is for doPost (auto return from below)

      doPost(req, resp);      // call doPost processing
      return;
   }


   HttpSession session = SystemUtils.verifyHotel(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);                      // get DB connection

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
      return;
   }

   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");               // get user name

   //
   //  If date passed, then this is a return - use date for calendar
   //
   if (req.getParameter("date") != null) {

      String dates = req.getParameter("date");

      date = Long.parseLong(dates);

      yy = date / 10000; 
      mm = (date - (yy * 10000)) / 100;
      dd = date - ((yy * 10000) + (mm * 100)); 
   }

   //
   // Get the 'Multiple Course' option from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmt.close();

      if (multi != 0) {           // if multiple courses supported for this club

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names
      }
           
      //
      //  Get the oldest date with tee sheets for this club
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT MIN(date) " +
                             "FROM teepast2");

      if (rs.next()) {

         old_date = rs.getLong(1);
      }
      stmt.close();

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Hotel_select\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;

   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { stmt.close(); }
      catch (Exception ignore) {}

   }

   //
   //  Determine oldest date values - month, day, year
   //
   old_yy = old_date / 10000;
   old_mm = (old_date - (old_yy * 10000)) / 100;
   old_dd = old_date - ((old_yy * 10000) + (old_mm * 100));

   //
   //  Output a page to prompt for a date
   //
   out.println(SystemUtils.HeadTitle2("Hotel View Past Tee Sheets Page"));
     
   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/cal-scripts-old.js\"></script>");

   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
   out.println("<tr><td align=\"center\" valign=\"top\">");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

   out.println("<font size=\"3\">");
   out.println("<p align=\"center\"><b>View Past Tee Sheets</b></p></font>");
   out.println("<br><font size=\"2\">");

   out.println("<table cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
     
   if (multi != 0) {           // if multiple courses supported for this club

      out.println("<b>Instructions:</b>  To view a past tee sheet, select the course and date of the tee sheet you desire.");
      
   } else {
     
      out.println("<b>Instructions:</b>  To view a past tee sheet, select the date of the tee sheet you desire.");
   }
   out.println("</font></td></tr></table><br>");


      out.println("<form action=\"Hotel_oldsheets\" target=\"bot\" method=\"post\" name=\"frmLoadDay\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");              // value set by script
         out.println("<input type=\"hidden\" name=\"old_mm\" value=\"" +old_mm+ "\">");   // oldest month (for js)
         out.println("<input type=\"hidden\" name=\"old_dd\" value=\"" +old_dd+ "\">");   // oldest day
         out.println("<input type=\"hidden\" name=\"old_yy\" value=\"" +old_yy+ "\">");   // oldest year

         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">");
         out.println("<b>Note:</b> &nbsp;The most recent date you can enter is yesterday.&nbsp;&nbsp;Empty (unused) tee times will not be displayed.<br><br>");

         //
         //  If multiple courses, then add a drop-down box for course names
         //
         if (multi != 0) {           // if multiple courses supported for this club

            String firstCourse = course.get(0);    // default to first course

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");

            for (index=0; index < course.size(); index++) {

               courseName = course.get(index);      // get course name from array

               if (courseName.equals( firstCourse )) {
                  out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
               } else {
                  out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
               }
            }
            out.println("</select>");
            out.println("<br><br>");

         } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
         }

         out.println("</font></form></p>");

         out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

         out.println(" <div id=calendar0 style=\"width: 180px\"></div>");

         out.println("</td></tr>\n</table>");

         Calendar cal_date = new GregorianCalendar();         // Calendar.getInstance();
         int cal_year = cal_date.get(Calendar.YEAR);
         int cal_month = cal_date.get(Calendar.MONTH) + 1;
         int cal_day = cal_date.get(Calendar.DAY_OF_MONTH) - 1;

         out.println("<script type=\"text/javascript\">");
         out.println("var iEndingDay = " + cal_day + ";");
         out.println("var iEndingYear = " + cal_year + ";");
         out.println("var iEndingMonth = " + cal_month + ";");
         out.println("var iStartingMonth = " + old_mm + ";");
         out.println("var iStartingDay = " + old_dd + ";");
         out.println("var iStartingYear = " + old_yy + ";");
         if (cal_day == 0) {
             cal_month--;
             if (cal_month == 0) { cal_month = 12; cal_year--; }
         }
         if (date > 0) {                                              // if date provided on return - use it
            out.println("doCalendar('" + mm + "', '" + yy + "');");
         } else {
            out.println("doCalendar('" + cal_month + "', '" + cal_year + "');");
         }
         out.println("</script>");
           
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">");
         out.println("<form method=\"get\" action=\"Hotel_select\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></p>");
         out.println("</font>");

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                           // end of main page
   out.println("</td></tr></table>");                           // end of whole page
   out.println("</center></body></html>");


 }  // end of doGet processing


 //*****************************************************
 //  doPost
 //*****************************************************
 //
 //   Parms passed:  calDate - mm/dd/yyyy 
 //                  course - course name
 //                  time - time of tee time
 //                  fb - front/back indicator
 //                  jump - row to jump to
 //                  edit - if clicked on a tee time to edit
 //                  noshow - if checking a player in or out
 //                  notes - if call to display the notes
 //
 //*****************************************************
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   PreparedStatement pstmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyHotel(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);                      // get DB connection

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
      return;
   }

   String user = (String)session.getAttribute("user");               // get user name
  
   //int count = 0;
   int p = 0;
   int i = 0;
   int fives = 0;
   //int jump = 0;
   //int index = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   //int day_num = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int g1 = 0;                  // guest indicators
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
     
   long date = 0;
     
   //short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
     
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   //String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   //String p1 = "";
   //String p2 = "";
   //String p3 = "";
   //String p4 = "";
   //String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String ampm = "";
   //String event_rest = "";
   String bgcolor = "";
   //String stime = "";
   //String sshow = "";
   //String name = "";
   String sfb = "";
   //String submit = "";
   String num = "";
   //String jumps = "";
   String course = "";

   String event1 = "";       // for legend - max 2 events, 4 rest's
   String ecolor1 = "";
   String rest1 = "";
   String rcolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   //String notes = "";
   String calDate = "";

   //
   //  Array to hold the Guest types
   //
   //String [] xguest = new String [36];            // max of 36 guest types per hotel user
   ArrayList<String> xguest = new ArrayList<String>();

   //
   //  Get the parms passed and build a date field to search for (yyyymmdd)
   //
   course = req.getParameter("course");
     
   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");

      //
      //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      num = tok.nextToken();                    // get the mm value

      month = Integer.parseInt(num);

      num = tok.nextToken();                    // get the dd value

      day = Integer.parseInt(num);

      num = tok.nextToken();                    // get the yyyy value

      year = Integer.parseInt(num);

      date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + day;                            // date = yyyymmdd (for comparisons)
   }

   try {

       // now look up the guest types for this restriction
       pstmt = con.prepareStatement (
                 "SELECT guest_type FROM hotel3_gtypes WHERE username = ?");

       pstmt.clearParameters();
       pstmt.setString(1, user);

       rs = pstmt.executeQuery();

       while ( rs.next() ) {

           xguest.add(rs.getString("guest_type"));

       }
       pstmt.close();

      //
      //  Get the 5-some option
      //
      pstmt = con.prepareStatement (
         "SELECT fives " +
         "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         fives = rs.getInt(1);          // 5-somes
      }
      pstmt.close();

      //
      //  Get the tee sheet for this date
      //
      pstmt = con.prepareStatement (
         "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
         "player3, player4, p1cw, p2cw, p3cw, p4cw, " +
         "show1, show2, show3, show4, fb, player5, p5cw, show5, " +
         "notes, p91, p92, p93, p94, p95 " +
         "FROM teepast2 WHERE date = ? AND courseName = ? ORDER BY time, fb");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setString(2, course);       
      rs = pstmt.executeQuery();      // execute the prepared stmt

      //
      //  Scan the tee sheet for events and restrictions to build the legend
      //

      while (rs.next()) {

         event = rs.getString(4);
         ecolor = rs.getString(5);
         rest = rs.getString(6);
         rcolor = rs.getString(7);

         if ((!event.equals( event1 )) && (!ecolor.equals( "Default" )) && (event1.equals( "" ))) {

            event1 = event;
            ecolor1 = ecolor;

          } else {

            if ((!event.equals( event1 )) && (!event.equals( event2 )) && (!ecolor.equals( "Default" )) && (event2.equals( "" ))) {

               event2 = event;
               ecolor2 = ecolor;
            }
         }

         if ((!rest.equals( rest1 )) && (!rcolor.equals( "Default" )) && (rest1.equals( "" ))) {

            rest1 = rest;
            rcolor1 = rcolor;

          } else {

            if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rcolor.equals( "Default" )) && (rest2.equals( "" ))) {

               rest2 = rest;
               rcolor2 = rcolor;

            } else {

               if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rcolor.equals( "Default" )) && (rest3.equals( "" ))) {

                  rest3 = rest;
                  rcolor3 = rcolor;

               } else {

                  if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rest.equals( rest4 )) && 
                       (!rcolor.equals( "Default" )) && (rest4.equals( "" ))) {

                     rest4 = rest;
                     rcolor4 = rcolor;
                  }
               }
            }
         }
      }                  // end of while
      pstmt.close();

      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println(SystemUtils.HeadTitle2("Hotel - View Past Sheets"));
      out.println("</HEAD>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#336633\" vlink=\"#336633\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      //
      //************************************************************************
      //  Build page to display or print the old sheet
      //************************************************************************
      //
      out.println("<table border=\"0\" width=\"70%\" align=\"center\"><tr valign=\"top\">");
      
      out.println("<td align=\"center\">");
      out.println("<form method=\"link\" action=\"javascript:self.print()\">");
      out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Sheet</button>");
      out.println("</form></td>");

      out.println("<td align=\"center\">");
      out.println("<form action=\"Hotel_oldsheets\" method=\"get\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></td></tr></table>");

      out.println("<font size=\"2\"><br><br>");
      out.println("Date:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b>");
          
      if (!course.equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");

      }
      out.println("<br><br>");
      out.println("<b>Tee Sheet Legend</b>");
      out.println("</font><font size=\"1\">");

      if (!event1.equals( "" )) {

         out.println("<br><button type=\"button\" style=\"background:" + ecolor1 + "\">" + event1 + "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!event2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         }
      } else {

         out.println("<br>");
      }

      if (!rest1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + rcolor1 + "\">" + rest1 + "</button>");

         if (!rest2.equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<button type=\"button\" style=\"background:" + rcolor2 + "\">" + rest2 + "</button>");

            if (!rest3.equals( "" )) {

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" + rcolor3 + "\">" + rest3 + "</button>");

               if ((!rest4.equals( "" )) && (event1.equals( "" ))) {   // do 4 rest's if no events

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<button type=\"button\" style=\"background:" + rcolor4 + "\">" + rest4 + "</button>");
               }
            }
         }

         out.println("<br>");

      } else {

         if (!event1.equals( "" )) {     // if event but no rest, need br

            out.println("<br>");
         }
      }
   
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"85%\">");
      out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Time</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>F/B</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 1</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 2</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 3</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 4</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         if (fives != 0) {
           
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Player 5</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");
          }

          out.println("</tr>");


      //
      //  Get the tee sheet for this date
      //
      pstmt = con.prepareStatement (
         "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
         "player3, player4, p1cw, p2cw, p3cw, p4cw, " +
         "show1, show2, show3, show4, fb, player5, p5cw, show5, " +
         "p91, p92, p93, p94, p95 " +
         "FROM teepast2 WHERE date = ? AND courseName = ? ORDER BY time, fb");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         hr = rs.getInt(1);
         min = rs.getInt(2);
         time = rs.getInt(3);
         event = rs.getString(4);
         ecolor = rs.getString(5);
         rest = rs.getString(6);
         rcolor = rs.getString(7);
         player1 = rs.getString(8);
         player2 = rs.getString(9);
         player3 = rs.getString(10);
         player4 = rs.getString(11);
         p1cw = rs.getString(12);
         p2cw = rs.getString(13);
         p3cw = rs.getString(14);
         p4cw = rs.getString(15);
         show1 = rs.getShort(16);
         show2 = rs.getShort(17);
         show3 = rs.getShort(18);
         show4 = rs.getShort(19);
         fb = rs.getShort(20);
         player5 = rs.getString(21);
         p5cw = rs.getString(22);
         show5 = rs.getShort(23);
         p91 = rs.getInt(24);
         p92 = rs.getInt(25);
         p93 = rs.getInt(26);
         p94 = rs.getInt(27);
         p95 = rs.getInt(28);

         ampm = " AM";
         if (hr == 12) {
            ampm = " PM";
         }
         if (hr > 12) {
            ampm = " PM";
            hr = hr - 12;    // convert to conventional time
         }

         bgcolor = "#F5F5DC";               //default

         if (!event.equals("")) {
            bgcolor = ecolor;
         } else {

            if (!rest.equals("")) {
               bgcolor = rcolor;
            }
         }

         if (bgcolor.equals("Default")) {
            bgcolor = "#F5F5DC";              //default
         }

         if (player1.equals("")) {
            p1cw = "";
         } else {
            if (p91 == 1) {
               p1cw = p1cw + "9";      // 9 hole round
            }
         }
         if (player2.equals("")) {
            p2cw = "";
         } else {
            if (p92 == 1) {
               p2cw = p2cw + "9";      // 9 hole round
            }
         }
         if (player3.equals("")) {
            p3cw = "";
         } else {
            if (p93 == 1) {
               p3cw = p3cw + "9";      // 9 hole round
            }
         }
         if (player4.equals("")) {
            p4cw = "";
         } else {
            if (p94 == 1) {
               p4cw = p4cw + "9";      // 9 hole round
            }
         }
         if (player5.equals("")) {
            p5cw = "";
         } else {
            if (p95 == 1) {
               p5cw = p5cw + "9";      // 9 hole round
            }          
         }

         g1 = 0;               // init guest indicators
         g2 = 0;
         g3 = 0;
         g4 = 0;
         g5 = 0;

         //
         //  Check if any player names are hotel guests for this hotel user.
         //  Change all other names to 'member' and don't allow access to these times.
         //
         if (!player1.equals( "" )) {

            i = 0;
            gloop1:
            while (i < xguest.size()) {

               if ((!xguest.get(i).equals( "" )) && (player1.startsWith( xguest.get(i) ))) {

                  g1 = 1;       // indicate player1 is a hotel guest
                  break gloop1;
               }
               i++;
            }
            if (g1 == 0) {

               player1 = "Member";       // change name
            }
         }
         if (!player2.equals( "" )) {

            i = 0;
            gloop2:
            while (i < xguest.size()) {

               if ((!xguest.get(i).equals( "" )) && (player2.startsWith( xguest.get(i) ))) {

                  g2 = 1;       // indicate player2 is a hotel guest
                  break gloop2;
               }
               i++;
            }
            if (g2 == 0) {

               player2 = "Member";       // change name
            }
         }
         if (!player3.equals( "" )) {

            i = 0;
            gloop3:
            while (i < xguest.size()) {

               if ((!xguest.get(i).equals( "" )) && (player3.startsWith( xguest.get(i) ))) {

                  g3 = 1;       // indicate player3 is a hotel guest
                  break gloop3;
               }
               i++;
            }
            if (g3 == 0) {

               player3 = "Member";       // change name
            }
         }
         if (!player4.equals( "" )) {

            i = 0;
            gloop4:
            while (i < xguest.size()) {

               if ((!xguest.get(i).equals( "" )) && (player4.startsWith( xguest.get(i) ))) {

                  g4 = 1;       // indicate player4 is a hotel guest
                  break gloop4;
               }
               i++;
            }
            if (g4 == 0) {

               player4 = "Member";       // change name
            }
         }
         if (!player5.equals( "" )) {

            i = 0;
            gloop5:
            while (i < xguest.size()) {

               if ((!xguest.get(i).equals( "" )) && (player5.startsWith( xguest.get(i) ))) {

                  g5 = 1;       // indicate player5 is a hotel guest
                  break gloop5;
               }
               i++;
            }
            if (g5 == 0) {

               player5 = "Member";       // change name
            }
         }

         //
         //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
         //
         sfb = "F";       // default Front 9

         if (fb == 1) {

            sfb = "B";
         }

         if (fb == 9) {

            sfb = "O";
         }

         out.println("<tr>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         if (min < 10) {                                 // if min value is only 1 digit
            out.println(hr + ":0" + min + ampm);
         } else {                                        // min value is 2 digits
            out.println(hr + ":" + min + ampm);
         }
         out.println("</font></td></form>");

         out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(sfb);
            out.println("</font></td>");

         if (!player1.equals("")) {

            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(player1);
            out.println("</font></td>");

         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p1cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (!player2.equals("")) {

            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(player2);
            out.println("</font></td>");

         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p2cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (!player3.equals("")) {

            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(player3);
            out.println("</font></td>");

         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p3cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (!player4.equals("")) {

            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(player4);
            out.println("</font></td>");

         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p4cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (fives != 0) {

            if (!player5.equals("")) {

               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(player5);
               out.println("</font></td>");

            } else {
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            }

            if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(p5cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");
         }

         out.println("</tr>");

      }  // end of while

      pstmt.close();

   } catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      return;
   
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                           // end of whole page
   out.println("</center></body></html>");

 }  // end of doPost


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(HttpServletRequest req, PrintWriter out, Exception exc, int lottery) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>" + exc.getMessage());
   out.println("<BR><BR><a href=\"Hotel_select\">Return</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }

}
