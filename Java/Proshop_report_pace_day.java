/***************************************************************************************
 *   Proshop_report_pace_day:  This servlet will process the 'Pace of Play Report - By Day'
 *                             request from the Proshop's navigation bar.
 *
 *
 *   called by:  Proshop menu (doGet)
 *
 *
 *   created: 6/20/2007  
 *
 *   last updated:
 *
 *        5/25/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        7/18/08   Added limited access proshop users checks
 *       12/03/07   Minor display fixes
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.Math;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.parmSlot;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.BigDate;
import com.foretees.common.Utilities;


public class Proshop_report_pace_day extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the call from Proshop_select (no parms passed)
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
   long new_date = 0;             // newest date that tee sheets exist (yesterday)
     
   String courseName = "";        // course names

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses


   if (req.getParameter("post") != null) {      // if call is for doPost (auto return from below)

      doPost(req, resp);      // call doPost processing
      return;
   }


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
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }
   
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");               // get user name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

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

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
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
   out.println(SystemUtils.HeadTitle2("Proshop Pace of Play Report Date Selection Page"));
     
   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
   out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts-old.js\"></script>");

   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
   out.println("<tr><td align=\"center\" valign=\"top\">");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

   out.println("<font size=\"3\">");
   out.println("<p align=\"center\"><b>Pace of Play Report - By Day</b></p></font>");
   out.println("<br><font size=\"2\">");

   out.println("<table cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
     
   if (multi != 0) {           // if multiple courses supported for this club

      out.println("<b>Instructions:</b>  Select the course and date you wish to view.");
      
   } else {
     
      out.println("<b>Instructions:</b>  Select the date you wish to view.");
   }
   out.println("</font></td></tr></table><br>");


      out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_pace_day\" target=\"bot\" method=\"post\" name=\"frmLoadDay\">");
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

            if (club.equals( "foresthighlands" )) {

               if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

                  firstCourse = "Meadow";      // setup default course (top of the list)

               } else {

                  firstCourse = "Canyon";
               }
            }

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");

            for (index = 0; index < course.size(); index++) {

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
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Exit\" style=\"text-decoration:underline; background:#8B8970\">");
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
 //
 //*****************************************************
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   PreparedStatement pstmt2 = null;
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
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }
   
   //
   //  See if we are in the timeless tees mode
   //
   boolean IS_TLT = ((Integer)session.getAttribute("tlt") == 1) ? true : false;
  
   int count = 0;
   int p = 0;
   int fives = 0;
   int index = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int teepast_id = 0;
   int pace_status_id = 0;
   int teecurr_id = 0;
   int invert = 0;
   int hole_num = 0;
   
   long date = 0;
     
   short show = 0;
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
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String ampm = "";
   String event_rest = "";
   String bgcolor = "";
   String stime = "";
   String sshow = "";
   String name = "";
   String sfb = "";
   String submit = "";
   String num = "";
   String jumps = "";
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
   String notes = "";
   String calDate = "";
   String pace_time = "";

   boolean noShow = false;


   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";



   course = req.getParameter("course");
   
   //
   //  Get the parms passed and build a date field to search for (yyyymmdd)
   //  
   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");

      //
      //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      num = tok.nextToken();                   // get the mm value

      month = Integer.parseInt(num);

      num = tok.nextToken();                   // get the dd value

      day = Integer.parseInt(num);

      num = tok.nextToken();                   // get the yyyy value

      year = Integer.parseInt(num);

      date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + day;                       // date = yyyymmdd (for comparisons)
   }
   

   //
   // Load PoP status colors into an array for quick access
   //

   String tmp_color = "";
   String [] aryPopStatusColors = new String [5];
   int tmp_id = 0;

   try {

      Statement stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");

      String tmp_name = "";

      while (rs.next()) {

          tmp_id = rs.getInt("pace_status_id");
          tmp_color = rs.getString("pace_status_color");
          aryPopStatusColors[tmp_id] = tmp_color;
      }

   } catch (Exception e) {

      SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
   }


   // handle excel output
   try{
       if (excel.equals("yes")) {                              // if user requested Excel Spreadsheet Format
           resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       }
   }
   catch (Exception exc) {
   }


   try {

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
      //  Build the Pace of Play Report for the selected day
      //
      out.println(SystemUtils.HeadTitle2("Proshop - Pace of Play Report"));
      out.println("</HEAD>");
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#336633\" vlink=\"#336633\" alink=\"#FF0000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      //
      //************************************************************************
      //  Build page to display or print the old sheet
      //************************************************************************
      //
      if (!excel.equals("yes")) {                              // if NOT Excel Format
        
         out.println("<BR><table border=\"0\" align=\"center\"><tr valign=\"top\">");

         out.println("<td align=\"center\">");
         out.println("<form method=\"link\" action=\"javascript:self.print()\">");
         out.println("<button type=\"submit\" style=\"text-decoration:underline; background:#8B8970\">Print Report</button>");
         out.println("</form></td>");

         out.println("<td align=\"center\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_pace_day\" method=\"post\" target=\"_blank\">");
         out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
         out.println("<input type=\"submit\" value=\"Excel Format\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></td>");

         out.println("<td align=\"center\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_pace_day\" method=\"get\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></td></tr>");
         out.println("</table>");
      }
      
      out.println("<H3>Pace of Play Report</H3>");
      out.println("Date:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b>");
          
      if (!course.equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");

      }
      out.println("<br><br>");
   
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"85%\">");
      out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Time</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>F/B</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 1</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 2</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 3</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>Player 4</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         if (fives != 0) {
           
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Player 5</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");
          }

         out.println("<td align=\"center\" nowrap>");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<u><b>Pace</b></u>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("</font></td></tr>");

      //
      //  Get the tee sheet for this date
      //
      pstmt = con.prepareStatement (
         "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
         "player3, player4, p1cw, p2cw, p3cw, p4cw, " +
         "show1, show2, show3, show4, fb, player5, p5cw, show5, " +
         "notes, p91, p92, p93, p94, p95, teepast_id, pace_status_id, teecurr_id " +
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
         notes = rs.getString(24);
         p91 = rs.getInt(25);
         p92 = rs.getInt(26);
         p93 = rs.getInt(27);
         p94 = rs.getInt(28);
         p95 = rs.getInt(29);
         teepast_id = rs.getInt(30);
         pace_status_id = rs.getInt("pace_status_id");
         teecurr_id = rs.getInt("teecurr_id");

  
         //
         //  get the pace time for this tee time if it exists
         //
         pace_time = "N/A";        // init
         
         pstmt2 = con.prepareStatement (
             "SELECT " +
                 "HOUR(SUBTIME((SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0), (SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1))) AS hr, " +
                 "MINUTE(SUBTIME((SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0), (SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1))) AS min, " +
                 "invert, hole_number FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1;");

         pstmt2.clearParameters();
         pstmt2.setInt(1, teecurr_id);
         pstmt2.setInt(2, teecurr_id);
         pstmt2.setInt(3, teecurr_id);
         pstmt2.setInt(4, teecurr_id);
         pstmt2.setInt(5, teecurr_id);
         rs2 = pstmt2.executeQuery();

         while (rs2.next()) {
             if (!(rs2.getInt("hr") == 0 && rs2.getInt("min") == 0)) {
                pace_time = rs2.getInt("hr") + "h " + rs2.getInt("min") + "min";
                hole_num = rs2.getInt("hole_number");
                invert = rs2.getInt("invert");
                if (invert != 18) pace_time += "<br><font size=1>(thru " + hole_num + ")</font>";
             }               
         }

         rs2.close();
         pstmt2 = null;


         ampm = " AM";
         if (hr == 12) {
            ampm = " PM";
         }
         if (hr > 12) {
            ampm = " PM";
            hr = hr - 12;    // convert to conventional time
         }

         bgcolor = "#F5F5DC";               //default

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
         out.println("<td align=\"center\" nowrap>");
         out.println("<font size=\"2\">");
         if (min < 10) {                                 // if min value is only 1 digit
            out.println(hr + ":0" + min + ampm);
         } else {                                  
            out.println(hr + ":" + min + ampm);
         }
         out.println("</font></td></form>");

         out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sfb);
            out.println("</font></td>");

         if (!player1.equals("")) {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(player1);
            out.println("</font></td>");
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(p1cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (!player2.equals("")) {

            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(player2);
            out.println("</font></td>");
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(p2cw);
         } else {
            out.println("<td bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (!player3.equals("")) {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(player3);
            out.println("</font></td>");
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(p3cw);
         } else {
            out.println("<td bgcolor=\"white\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (!player4.equals("")) {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(player4);
            out.println("</font></td>");
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(p4cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (fives != 0) {

            if (!player5.equals("")) {
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(player5);
               out.println("</font></td>");
            } else {
               out.println("<td bgcolor=\"" + bgcolor + "\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            }

            if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(p5cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");
         }

         //
         //  Next column for Pace of Play time value for this group
         //
         tmp_color = bgcolor;
         if (pace_status_id != 0) {

            tmp_color = aryPopStatusColors[pace_status_id];             // get PoP status color for this tee time
         }
         out.println("<td bgcolor=\"" + tmp_color + "\" align=\"center\" nowrap>");
         out.println("<font size=\"2\">");

         if (!pace_time.equals( "" )) {        // if pace time exists for this tee time
           
               out.println(pace_time);         // show it
         } else {
               out.println("&nbsp;");
         }
         out.println("</font></td>");
         out.println("</tr>");
         
      }  // end of while

      pstmt.close();

   }
   catch (Exception e1) {

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
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>" + exc.getMessage());
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
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

 // *********************************************************
 // Process
 // *********************************************************
 
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }
 
}
