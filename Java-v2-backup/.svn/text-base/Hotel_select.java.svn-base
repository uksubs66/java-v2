/***************************************************************************************
 *   Hotel_select:  This servlet will process the 'View Tee Sheet' request from
 *                    the Hotel's main page.
 *
 *
 *   called by:  hotel_main.htm (doGet)
 *
 *   created: 11/18/2003   Bob P.
 *
 *   last updated:
 *
 *       5/19/10  Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *      12/02/09  Allow for Saudi Arabia time - adjustTime.
 *      10/04/09  Updated calendars to v3.0   
 *      11/16/07  Add -ALL- option for course selection  (Case #1111)
 *       9/22/05  Add link to view old tee sheets - per Cordillera's request.
 *       1/24/05  Ver 5 - change club2 to club5.
 *       1/15/04  RDP  Change calendar to use js to allow for 365 days. 
 *       1/07/04  JAG  Modified to match new color scheme
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
import com.foretees.common.Connect;

public class Hotel_select extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the initial request from hotel_main.htm
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   ResultSet rs4 = null;


    // Define parms
   String courseName = "";        // course names
   String adv_ampm = "";
   String adv_zone = "";

   int days1 = 0;               // days in advance that Hotels can make tee times
   int days2 = 0;               //         one per day of week (Mon - Sun)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int adv_hr = 0;
   int adv_min = 0;
   int multi = 0;               // multiple course support
   int lottery = 0;             // lottery support
   int index = 0;
   int found = 0;
   int sdays = 0;

   int cal_time = 0;            // calendar time for compares
   int adv_time = 0;            // 'days in advance' time for compares

   long date = 0;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   String [] oday_table = { "o", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th",
                           "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th",
                           "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th" };

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


   HttpSession session = SystemUtils.verifyHotel(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get user name

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   // Get the 'Days In Advance' info from the club db and hotel db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, adv_zone " +
                              "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         adv_zone = rs.getString(2);

      } else {
        // Parms do not exist yet
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club manager.");
         out.println("<BR><BR>");
         out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
      stmt.close();

      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT days1, days2, days3, days4, days5, days6, days7 " +
         "FROM hotel3 WHERE username = ?");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         days1 = rs.getInt(1);
         days2 = rs.getInt(2);
         days3 = rs.getInt(3);
         days4 = rs.getInt(4);
         days5 = rs.getInt(5);
         days6 = rs.getInt(6);
         days7 = rs.getInt(7);
      }
      pstmt2.close();

      //
      //  There are no time parms (days in adv) for hotel users, so just use 1:00 AM
      //
      adv_hr = 1;
      adv_min = 0;
      adv_ampm = "AM";

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
         
         if (course.size() > 1) {                // if more than 1 course, add -ALL- option

            course.add ("-ALL-");
         }
      }
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Get current date/time and setup parms to use when building the calendar
   //
   Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
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
   cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format

   cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value

      if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi Arabia time)

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

   month = month + 1;                            // month starts at zero

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

   int today = day;                              // save today's number

   String day_name = day_table[day_num];         // get name for day

   String mm = mm_table[month];                  // month name

   int numDays = numDays_table[month];           // number of days in month

   if (numDays == 0) {                           // if Feb

      int leapYear = year - 2000;
      numDays = feb_table[leapYear];             // get days in Feb
   }

   //
   //  setup time for display
   //
   String s_time = "";

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


   //
   //  Build the HTML page to prompt user for a specific date
   //

   out.println(SystemUtils.HeadTitle("Hotel Select Date Page"));
     
   // include files for dynamic calendars
   //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
   //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/cal-scripts.js\"></script>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
  
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<center><font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td><br><br><br>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"500\">");
      out.println("<tr><td><font color=\"#FFFFFF\" size=\"2\">");

      if (multi != 0) {           // if multiple courses supported for this club

         out.println("<p align=\"center\">To view a day's Tee Sheet, select the course and then click on the date below.");
      } else {

         out.println("<p align=\"center\">To view a day's Tee Sheet, select the date below.");
      }

      out.println("<br>Use the arrows to change the month displayed in the calendar on the right.</p>");
      out.println("</td></tr></font></table>");

   out.println("<p align=\"center\"><font size=\"2\">");
   out.println("Today's date is:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</b>");

   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The Server Time is:&nbsp;&nbsp;<b>" + s_time);

   if (cal_am_pm == 0) {
      out.println(" AM");
   } else {
      out.println(" PM");
   }
   out.println(" " + adv_zone + "</b></font></p>");


   //
   // start of calendar row
   //
   out.println("</td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<form action=\"Hotel_sheet\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         String firstCourse = course.get(0);    // default to first course

         out.println("<br><b>Course:</b>&nbsp;&nbsp;");
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
         out.println("<br>");

      } else {
         out.println("<br><input type=\"hidden\" name=\"course\" value=\"\">");
      }

      //
      //  Calendars
      //
      out.println("</form>");
      
      out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");    // was 190 !!!

      out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

      out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

      out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

      out.println("</td>\n<tr>\n</table>");

      Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
      int cal_year = cal_date.get(Calendar.YEAR);
      int cal_month = cal_date.get(Calendar.MONTH) + 1;
      int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
      int cal_year2 = cal_year;
      int cal_month2 = cal_month;

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
      cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
      cal_year = cal_date.get(Calendar.YEAR);
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
      cal_date.add(Calendar.YEAR, -1); // subtract a year

      cal_date.add(Calendar.DAY_OF_MONTH, index); // add the # of days ahead of today this tee sheet is for
      if (cal_date.get(Calendar.MONTH) + 1 == cal_month2 && cal_date.get(Calendar.YEAR) == cal_year2) cal_date.add(Calendar.MONTH, 1);
      cal_year = cal_date.get(Calendar.YEAR);
      cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

      out.println("g_cal_month[1] = " + cal_month + ";");
      out.println("g_cal_year[1] = " + cal_year + ";");

      out.println("</script>");

      out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
      out.println("<script type=\"text/javascript\">\ndoCalendar('1');\n</script>");

      //
      // end of calendar row
      //
      out.println("</td></tr>");
        
   out.println("</table>");

   //
   //   Add link to view old tee sheets
   //
   out.println("<form action=\"Hotel_oldsheets\" method=\"get\" target=\"bot\">");
   out.println("<input type=\"submit\" value=\"Click Here To View Past Tee Sheets\" style=\"background:#8B8970\">");

   //
   //  End of HTML page
   //
   out.println("</center></body></html>");

 }  // end of doGet

}
