/***************************************************************************************
 *   Proshop_select:  This servlet will process the 'View Tee Sheet' request from
 *                    the Proshop's main page.
 *
 *
 *   called by:  Proshop menu
 *
 *   created: 1/03/2002   Bob P.
 *
 *   last updated:
 *
 *        3/05/13   Add member notices display in case pro wants to display any messages on this page (new member notice option).
 *       12/19/12   Desert Mountain - setup default courses based on proshop user.
 *        8/02/10   Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *        6/30/10   Cordillera - set default courses based on the user (case 1862).
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        2/07/10   Updated calendars to hilite today in green.
 *        1/25/10   Fix date & calendars for clubs ahead of Central time (Saudi Time especially)
 *        4/22/09   Beverly GC - Color the calendar days green to display what days members currently have access too for proshop ease of booking (case 1467).
 *        8/12/08   Stonebridge Ranch - limit the course options based on user (case 1530).
 *        7/31/08   Add Test Lottery button to bottom of page and doPost method - for testing only!!!!!!
 *        7/18/08   Added limited access proshop users checks
 *        7/07/08   Admirals Cove - Added custom to default to -ALL- courses. This now uses new fields in the club5
 *                  table for determining the default course.  Still a custom though as no config available yet.  (case 1513).
 *        7/01/08   Black Diamond Ranch - Added custom to default to -ALL- courses.
 *        6/07/08   Mayfield Sandridge - default course to Sand Ridge for proshop1 (case 1491).
 *        4/02/08   Oak Hill CC - Set default course - Case #1433.
 *        1/29/08   CC of Jackson - Set default course - Case #1373
 *       12/17/07   Mediterra - Added custom to default to -ALL- courses (Case #1343)
 *       11/09/07   Imperial GC - Added custom to default to -ALL- courses (Case #1306)
 *        5/24/07   Blackhawk (CA) - default course to Lakeside for proshop5.
 *        4/05/07   The International - Added custom to default to -ALL- courses.
 *        4/02/07   Pinery - Added custom to default to -ALL- courses.
 *        2/07/07   Fort Collins/Greeley - do not allow the -ALL- course option, and
 *                  list the appropriate course first based on which club the user is from.
 *        8/21/06   Added custom to default to -ALL- courses for Fairbanks Ranch
 *        8/14/06   Added custom to default to -ALL- courses for Lakewood Ranch and Pelican's Nest
 *       10/24/05   Allow for 20 courses and -ALL- for multi course clubs.
 *        7/07/05   Custom for Forest Highlands - default course depends on login id.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        6/09/04   RDP Add an 'ALL' option for multiple course facilities.
 *        1/15/04   Change calendar to use js to allow for 365 days. (by Paul S.)
 *        1/10/04   Enhancements for Version 4 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;
import com.foretees.common.verifySlot;


public class Proshop_select extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the initial request from Proshop menu
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;


   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
   //                       "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   //int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   //int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
   //                         28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   //
   //  Array to hold the course names
   //
   int cMax = 0;                                       // max of 20 courses plus allow room for '-ALL-'
   //String [] course = new String [cMax];

   ArrayList<String> course = new ArrayList<String>();


   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");               // get club name
   String user = (String)session.getAttribute("user");               // get user name
   Connection con = SystemUtils.getCon(session);                      // get DB connection
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean skipALL = false;      // do not include the -ALL- option for course selection if true


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
      out.close();
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TS_VIEW", con, out)) {
       SystemUtils.restrictProshop("TS_VIEW", out);
   }

    // Define parms
   String courseName = "";        // course names
   String default_course_pro;
   String adv_zone = "";
   int multi = 0;               // multiple course support
   int index= 0;

   //
   // Get the 'Days In Advance' info from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, " +
                             "adv_zone, default_course_pro " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         adv_zone = rs.getString(2);
         default_course_pro = rs.getString(3);

      } else {
        // Parms do not exist yet
         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H2>Database Access Error</H2>");
         out.println("<BR><BR>The Club Setup has not been completed.");
         out.println("<BR>Please go to 'System Config' and select 'Club Setup'.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
      stmt.close();

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names


         if (club.equals( "fortcollins" )) {

            skipALL = true;       // do not include the -ALL- option
         }


         if (club.equals( "stonebridgeranchcc" )) {

            if (user.equals("proshop1") || user.equals("proshop2")) {

               skipALL = true;               // do not include the -ALL- option

               default_course_pro = "Dye";   // set default course for these users
            }

            if (user.equals("proshop4") || user.equals("proshop5")) {

               default_course_pro = "-ALL-";   // set default course for these users
            }
         }


         //
         //  Add an 'ALL' option at the end of the list
         //
         if (skipALL == false && course.size() > 1) {

            course.add ("-ALL-");      // add '-ALL-' option
         }

      }
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get today's date and setup parms to use when building the calendar
   //
   Calendar cal = new GregorianCalendar();             // get todays date & time (Central Time)
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);
   int cal_sec = cal.get(Calendar.SECOND);

   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   int cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format

   cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value

      if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      }
   }
   //int today = day;                              // save today's number
   month = month + 1;                            // month starts at zero
   String day_name = day_table[day_num];         // get name for day
   //String mm = mm_table[month];                  // month name
   //int numDays = numDays_table[month];           // number of days in month

   //if (numDays == 0) {                           // if Feb

   //   int leapYear = year - 2000;
   //   numDays = feb_table[leapYear];             // get days in Feb
   //}

   int cal_hour = cal_time / 100;                // get adjusted hour
   cal_min = cal_time - (cal_hour * 100);        // get minute value
   int cal_am_pm = 0;                            // preset to AM

   if (cal_hour > 11) {

      cal_am_pm = 1;                // PM
      cal_hour = cal_hour - 12;     // set to 12 hr clock
   }
   if (cal_hour == 0) {

      cal_hour = 12;
   }

   String s_time = cal_hour + ":" + Utilities.ensureDoubleDigit(cal_min) + ":" + Utilities.ensureDoubleDigit(cal_sec);
   
   
   long thisDate = (year * 10000) + (month * 100) + day;         // get adjusted date for today
   
   
/*
   if (cal_min < 10) {
      s_time = cal_hour + ":0" + cal_min;
   } else {
      s_time = cal_hour + ":" + cal_min;
   }

   if (cal_sec < 10) {
      s_time = s_time + ":0" + cal_sec;
   } else {
      s_time = s_time + ":" + cal_sec;
   }
*/
   //int count = 0;                     // init day counter
   //int col = 0;                       // init column counter


   //
   //  Build the HTML page to prompt user for a specific date
   //

   out.println(SystemUtils.HeadTitle2("Proshop Select Date Page"));

   // include files for dynamic calendars
   if (club.equals("beverlygc")) {     // Different style sheets needed for color-coding days in adv for BeverlyGC
       out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
       out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");
   } else {
       out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
       out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
   }

   out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");


   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

   
   
      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      //
      String memNoticeMsg = verifySlot.checkMemNotice(thisDate, 0, 0, "", day_name, "teetime_cal", true, con);

      if (!memNoticeMsg.equals("")) {

          int notice_mon = 0;
          int notice_tue = 0;
          int notice_wed = 0;
          int notice_thu = 0;
          int notice_fri = 0;
          int notice_sat = 0;
          int notice_sun = 0;

          String notice_msg = "";

          try {

              // Get relevent member notice data from database
              ResultSet notice_rs = null;
              PreparedStatement notice_pstmt = con.prepareStatement(
                      "SELECT mon, tue, wed, thu, fri, sat, sun, message " +
                      "FROM mem_notice " +
                      "WHERE sdate <= ? AND edate >= ? AND " +
                      "teetime_cal = 1 AND proside=1 AND activity_id = 0");

              notice_pstmt.clearParameters();        // clear the parms and check player 1
              notice_pstmt.setLong(1, thisDate);
              notice_pstmt.setLong(2, thisDate);
              notice_rs = notice_pstmt.executeQuery();      // execute the prepared stmt

              out.println("<br><table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"7\" cellspacing=\"0\">");
              out.println("<tr><td>");

              while (notice_rs.next()) {

                  notice_mon = notice_rs.getInt("mon");
                  notice_tue = notice_rs.getInt("tue");
                  notice_wed = notice_rs.getInt("wed");
                  notice_thu = notice_rs.getInt("thu");
                  notice_fri = notice_rs.getInt("fri");
                  notice_sat = notice_rs.getInt("sat");
                  notice_sun = notice_rs.getInt("sun");
                  notice_msg = notice_rs.getString("message");

                  if ((notice_mon == 1 && day_name.equals( "Monday")) || (notice_tue == 1 && day_name.equals( "Tuesday")) || (notice_wed == 1 && day_name.equals( "Wednesday")) ||
                          (notice_thu == 1 && day_name.equals( "Thursday")) || (notice_fri == 1 && day_name.equals( "Friday")) || (notice_sat == 1 && day_name.equals( "Saturday")) ||
                          (notice_sun == 1 && day_name.equals( "Sunday"))) {

                      out.println( notice_msg );
                  }

              }  // end WHILE loop

              out.println("</td></tr></table><BR>");

              notice_pstmt.close();

         } catch (Exception e1) {

             out.println(SystemUtils.HeadTitle("DB Error"));
             out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
             out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
             out.println("<BR><BR><H2>System Error</H2>");
             out.println("<BR><BR>Unable to access the Database.");
             out.println("<BR>Please try again later.");
             out.println("<BR><BR>If problem persists, contact your club manager.");
             out.println("<BR><BR>" + e1.getMessage());
             out.println("<BR><BR>");
             out.println("<a href=\"Proshop_announce\">Return</a>");
             out.println("</CENTER></BODY></HTML>");
             out.close();
         }
     }


   
      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"500\">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");

      if (multi != 0) {           // if multiple courses supported for this club

         out.println("<p align=\"center\">To view a day's Tee Sheet, select the course and then click on the date below.</p>");
      } else {

         out.println("<p align=\"center\">To view a day's Tee Sheet, select the date below.</p>");
      }

      out.println("</td></tr></font></table>");
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\">Today's date is:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</b>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The Server Time is:&nbsp;&nbsp;<b>" + s_time);

      if (cal_am_pm == 0) {
         out.println(" AM");
      } else {
         out.println(" PM");
      }
      out.println(" " + adv_zone + "</b></p>");

      //
      // start of calendar row
      //
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\"><br>");

      // this is the form that gets submitted when the user selects a day from the calendar
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         String firstCourse = course.get(0);    // default to first course


         if (!default_course_pro.equals( "" )) {

            firstCourse = default_course_pro;            // get default course from club5 !!!!

         } else {

            //
            //    NOTE:  Replace these customs by manually putting the default value in club5!!!!!!!!!!!!!!
            //

            if (club.equals( "foresthighlands" )) {

               if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

                  firstCourse = "Meadow";      // setup default course (top of the list)

               } else {

                  firstCourse = "Canyon";
               }

            } else if (club.equals( "cordillera" )) {     // Cordillera - case 1862

               if (user.equalsIgnoreCase( "proshop1" )) {

                  firstCourse = "Valley";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase( "proshop2" )) {

                  firstCourse = "Mountain";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase( "proshop3" )) {

                  firstCourse = "Summit";      // setup default course (top of the list)

               } else if (user.equalsIgnoreCase( "proshop4" )) {

                  firstCourse = "Short";      // setup default course (top of the list)
               }

            } else if (club.equals( "blackhawk" ) && user.equalsIgnoreCase( "proshop5" )) {

               firstCourse = "Lakeside";

            } else if (club.equals( "mayfieldsr" ) && user.equalsIgnoreCase( "proshop1" )) {

               firstCourse = "Sand Ridge";

            } else if (club.equals("fortcollins")) {              // if Fort Collins (& Greeley)

               if (user.equalsIgnoreCase( "proshop4" ) || user.equalsIgnoreCase( "proshop5" )) {   // if Greeley pro

                  firstCourse = "Greeley CC";
               } else if (user.equalsIgnoreCase("proshopfox")) {
                  firstCourse = "Fox Hill CC";
               } else {
                  firstCourse = "Fort Collins CC";
               }

            } else if (club.equals("ccjackson")) {

               firstCourse = "Cypress to Cypress";         // Set default course - Case #1373

            } else if (club.equals("oakhillcc")) {

                firstCourse = "East Course";              // Set default course - Case #1433

            } else if (club.equals("lakewoodranch") || club.equals("pelicansnest") || club.equals("fairbanksranch") ||
                club.equals("pinery") || club.equals("international") || club.equals("blackdiamondranch") ||
                club.equals("imperialgc") || club.equals("mediterra")) {

               firstCourse = "-ALL-";
               
            } else if (club.equals("desertmountain")) {   // Desert Mountain
                
               if (user.startsWith( "proshopa" )) {  

                  firstCourse = "Apache";
                  
               } else if (user.startsWith("proshopch")) {
                   
                  firstCourse = "Chiricahua";
                
               } else if (user.startsWith("proshopcg")) {
                   
                  firstCourse = "Cochise";      // actually does Geronimo too
                
               } else if (user.startsWith("proshopo")) {
                   
                  firstCourse = "Outlaw";
                
               } else if (user.startsWith("proshopr")) {
                   
                  firstCourse = "Renegade";
                
               } else if (user.startsWith("proshoppc")) {
                   
                  firstCourse = "-ALL-";     // Performance Center users                
               }
            }
         }


         out.println("<b>Course:</b>&nbsp;&nbsp;");
         out.println("<div id=awmobject1>");
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
         out.println("</div>");
         out.println("<br>");

      } else {
         out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
      }

      out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
      out.println("</form>");

      out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

    out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

    out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

      out.println("</td>\n<tr>\n</table>");

    Calendar cal_date = new GregorianCalendar();
    cal_date.set(year, (month - 1), day);
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

    out.println("<script type=\"text/javascript\">");

     out.println("var g_cal_bg_color = '#F5F5DC';");
     out.println("var g_cal_header_color = '#8B8970';");
     out.println("var g_cal_border_color = '#8B8970';");

     out.println("var g_cal_count = 2;"); // number of calendars on this page
     out.println("var g_cal_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

     out.println("var g_cal_findToday = new Array(g_cal_count - 1);");
     out.println("g_cal_findToday[0] = true;");

     // set calendar date parts
     out.println("g_cal_month[0] = " + cal_month + ";");
     out.println("g_cal_year[0] = " + cal_year + ";");
     out.println("g_cal_beginning_month[0] = " + cal_month + ";");
     out.println("g_cal_beginning_year[0] = " + cal_year + ";");
     out.println("g_cal_beginning_day[0] = " + cal_day + ";");
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = 31;");
     out.println("g_cal_ending_year[0] = " + cal_year + ";");

     cal_date.add(Calendar.MONTH, 1); // add a month
     cal_year = cal_date.get(Calendar.YEAR);
     cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

     out.println("g_cal_month[1] = " + cal_month + ";");
     out.println("g_cal_year[1] = " + cal_year + ";");
     out.println("g_cal_beginning_month[1] = " + cal_month + ";");
     out.println("g_cal_beginning_year[1] = " + cal_year + ";");
     out.println("g_cal_beginning_day[1] = 0;");

     cal_date.add(Calendar.MONTH, -1); // subtract a month
     cal_date.add(Calendar.YEAR, 1); // add a year
     cal_year = cal_date.get(Calendar.YEAR);
     cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
     out.println("g_cal_ending_month[1] = " + cal_month + ";");
     out.println("g_cal_ending_day[1] = " + cal_day + ";");
     out.println("g_cal_ending_year[1] = " + cal_year + ";");

     if (club.equals("beverlygc")) {   //add color coding for Beverly GC

         out.print("var daysArray = new Array(");
         int js_index = 0;
         int max = 365;
         int[] days = new int[max+1];
         for (int i=0; i<max+1; i++) {
             if (i<=7) {
                 days[i] = 1;
             } else {
                 days[i] = 0;
             }
         }

         for (js_index = 0; js_index <= max; js_index++) {
             out.print(days[js_index]);
             if (js_index != max) out.print(",");
         }
         out.println(");");

         out.println("var max = " + max + ";");
     }

    out.println("</script>");

    out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
    out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");

      if (req.getParameter("source") == null) {

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr>");

         out.println("<form method=\"get\" action=\"Proshop_announce\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</p></font>");
         out.println("</td></form></tr></table");
      }

   out.println("</td></tr>");
   out.println("<tr><td align=\"center\"><br>");
      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" width=\"620\">");
      out.println("<tr><td><font size=\"2\">");
      out.println("<p><b>Note:</b>  Members will be able to select dates according to the 'days in advance' ");
      out.println("and 'time of day' values specified in the system configuration.");
      out.println("&nbsp;&nbsp;The 'time of day' is based on the Server Time displayed above.");
      out.println("&nbsp;&nbsp;Click on the <b>'Tee Sheets'</b> tab above to update the server clock.</p>");

   out.println("</td></tr></font></table>");

       /*
         //
         //  TEMP FOR TESTING !!!!  comment this out when not testing!!!!
         //
         //        Use this to force a lottery to be processed.
         //
         out.println("<form method=\"post\" action=\"Proshop_select\">");
         out.println("<input type=\"hidden\" name=\"testLott\" value=\"testLott\">");
         out.println("<p align=\"center\">");
         out.println("<input type=\"submit\" value=\"Process Lottery\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</p></form>");
         //  end of test code
       */


   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");
   out.println("</center></font></body></html>");
   out.close();

 }  // end of doGet



 //*****************************************************
 //   doPost - for Testing Lotteries ONLY !!!!!
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");               // get club name
   String user = (String)session.getAttribute("user");               // get user name
   Connection con = SystemUtils.getCon(session);                      // get DB connection


   if (req.getParameter("testLott") == null) {

      return;        // make sure we should be here
   }


   String lname = "Black Diamond Test 2";       // name of lottery to process
   long date = 20080801;                        // date of lottery to process


   SystemUtils.testLott(lname, date, club, con);      // go kick it off

   out.println(SystemUtils.HeadTitle("Test Complete"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Lottery Test Complete</H3>");
   out.println("<BR><BR>Lottery " +lname+ " has been processed.");
   out.println("<BR><BR>");
   out.println("<a href=\"Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }   // end of doPost (for Testing ONLY)

}
