/***************************************************************************************
 *   Common_calendar:  This servlet will display a calendar on the announcement page.
 *                     The calendar will display the up coming events.
 * 
 *
 *   called by:    Member_announce
 *                 Proshop_announce
 *
 *   created: 10/28/2010   Bob P.
 *
 *   last updated:
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

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.getRests;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.Connect;

public class Common_calendar extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
         doGet(req, resp);
 }
 
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

   PreparedStatement pstmt1 = null;
   //Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rsev = null;

   HttpSession session = null;

   //
   // This servlet can be called by both Proshop and Member users - find out which
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      return;
   }

   String monthName = "";
   String course = "";
   String ename = "";

   long date = 0;
   long edate = 0;
   long sdate = 0;
   long todayDate = 0;
   long tomorrowDate = 0;

   int mm = 0;
   int dd = 0;
   int yy = 0;
   int month = 0;
   int months = 0;
   int day = 0;
   int numDays = 0;
   int today = 0;
   int day_num = 0;
   int year = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int ctime = 0;
   int index = 0;
   int i = 0;
   int i2 = 0;
   int max = 30;    // default
   int col = 0;
   int multi = 0;
   int etype = 0;
   int signUp = 0;
   int selected_act_id = 0;         // selected activity id (which activities to include in calendar)
   int returned_activity_id = 0;         // activity id returned from queries (used to filter which items to display as links when displaying ALL)

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;

   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   String user = (String)session.getAttribute("user");      // get username
   String club = (String)session.getAttribute("club");      // get club name
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   boolean didone = false;
   boolean allRest = false;
   boolean allAct = false;
   boolean restrictAllTees = false;         // restrict all tee times

   
   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   String [] day_table = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

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
   //  Arrays to hold the event indicators - one entry per day (is there one of the events on this day)
   //
   int [] eventA = new int [32];          //  events (32 entries so we can index by day #)
   
   
   //
   //  Display 2 months for now
   //
   months = 2;                                      // default = 3 months

   
   
   // 
   //  Get activity id if user changed his selection
   //
   if (req.getParameter("activity_id") != null) {

      String tempId = req.getParameter("activity_id");

      try {
         selected_act_id = Integer.parseInt(tempId);
      }
      catch (NumberFormatException e) {  
         selected_act_id = sess_activity_id;      // default to current activity
      }
      
   } else {
      
      selected_act_id = sess_activity_id;      // default to current activity
   }

   // Set a boolean for easy reference later
   if (selected_act_id == 999) {
       allAct = true;
   } else {
       allAct = false;
   }


   try {
      
      //
      // Get the days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm, sess_activity_id);        // get the club parms

      multi = parm.multi;
   }
   catch (Exception exc) {
   }


   //
   //  get today's date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);     // get current time
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   ctime = (cal_hourDay * 100) + cal_min;        // get time in hhmm format

   ctime = SystemUtils.adjustTime(con, ctime);   // adjust the time

   if (ctime < 0) {                // if negative, then we went back or ahead one day

      ctime = 0 - ctime;           // convert back to positive value

      if (ctime < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date
      }
   }

   cal_hourDay = ctime / 100;                      // get adjusted hour
   cal_min = ctime - (cal_hourDay * 100);          // get minute value

   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) +1;
   dd = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   todayDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

   year = yy;
   month = mm;
   day = dd;

   today = day;                                  // save today's number
   
   
   //
   //  Get tomorrow's date for cutoff test
   //
   cal.add(Calendar.DATE,1);                     // get next day's date
   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) +1;
   dd = cal.get(Calendar.DAY_OF_MONTH);

   tomorrowDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd
   
   yy = 0;       // reset
   mm = 0;
   dd = 0;
     
   

   //
   //   build the HTML page for the display
   //
   out.println(SystemUtils.HeadTitle2("Member My " + ((IS_TLT) ? "Activities" : "Tee Times")));
   //
   //*******************************************************************
   //  Scripts to complete and submit the forms
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");                     // Events
   out.println("<!--");
   out.println("function exeEventForm(name, course) {");
      out.println("document.forms['eventForm'].course.value = course;");
      out.println("document.forms['eventForm'].name.value = name;");
      out.println("document.forms['eventForm'].index.value = '999';");
      out.println("document.forms['eventForm'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script


   out.println("<script type=\"text/javascript\">");                     // Non-Signup Events
   out.println("<!--");
   out.println("function exeEventForm2(name) {");
      out.println("document.forms['eventForm2'].event.value = name;");
      out.println("document.forms['eventForm2'].submit();");        // submit the form
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   out.println("</head>");
   out.println("<body>");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\" valign=\"top\">");   // table for main page
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\">");


   for (i2 = 0; i2 < months; i2++) {                 // do each month

      monthName = mm_table[month];                  // month name

      numDays = numDays_table[month];               // number of days in month

      if (numDays == 0) {                           // if Feb

         int leapYear = year - 2000;
         numDays = feb_table[leapYear];             // get days in Feb
      }

      //
      //  Adjust values to start at the beginning of the month
      //
      cal.set(Calendar.YEAR, year);                 // set year in case it changed below
      cal.set(Calendar.MONTH, month-1);             // set the current month value
      cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
      day = 1;
      col = 0;

      //
      //  init the indicator arrays to start new month
      //
      for (i = 0; i < 32; i++) {   
         eventA[i] = 0;
      }

      //
      //  Locate all the Events for this member & month and set the array indicators for each day
      //
      sdate = (year * 10000) + (month * 100) + 0;       // start of the month (for searches)
      edate = (year * 10000) + (month * 100) + 32;      // end of the month


      //
      //  Get all events for this month
      //
      try {

         if (parm.genrez_mode == 0 || selected_act_id == 999) {     // if no Activities or ALL selected

            if (club.equals( "tcclub" )) {

               pstmt1 = con.prepareStatement (
                  "SELECT day " +
                  "FROM events2b WHERE date > ? AND date < ? AND inactive = 0");

            } else {

               pstmt1 = con.prepareStatement (
                  "SELECT day " +
                  "FROM events2b WHERE date > ? AND date < ? AND gstOnly = 0 AND inactive = 0");
            }

            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            
         } else {


            pstmt1 = con.prepareStatement (
                    "SELECT day " +
                    "FROM events2b WHERE date > ? AND date < ? AND gstOnly = 0 AND activity_id = ? AND inactive = 0");


            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            pstmt1.setInt(3, selected_act_id);
         }

         rs = pstmt1.executeQuery();

         while (rs.next()) {

            dd = rs.getInt(1);

            eventA[dd] = 1;       // set indicator for this day (event exists)
         }
         pstmt1.close();

      } catch (Exception e1) {
          
         SystemUtils.logError("Common_calendar: Error getting events for " +club+ ", Error: " +e1.getMessage());
      }
        
      
      //
      //  table for the month
      //
      out.println("<table border=\"1\" width=\"740\" bgcolor=\"#F5F5DC\">");
      out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#C0BA74\">");                  // same color as announcement page top panel
         out.println("<font color=\"#FFFFFF\" size=\"2\"><b>Club Events for " + monthName + "&nbsp;&nbsp;" + year + "</b></font>");
      out.println("</td></tr><tr>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Sunday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Monday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Tuesday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Wednesday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Thursday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Friday</b></font></td>");
         out.println("<td width=\"110\" align=\"center\"><font size=\"1\"><b>Saturday</b></font></td>");
      out.println("</tr>");
      out.println("<tr>");        // first row of days

      for (i = 1; i < day_num; i++) {    // skip to the first day
         out.println("<td>&nbsp;<br><br></td>");
         col++;
      }

      while (day < today) {
         out.println("<td align=\"left\" valign=\"top\"><font size=\"1\">" + day + "</font>");  // put in day of month
         out.println("<br><br></td>");  // put in day of month
         col++;
         day++;

         if (col == 7) {
            col = 0;                             // start new week
            out.println("</tr>");
         }
      }

      //
      // start with today, or 1st day of month, and go to end of month
      //
      while (day <= numDays) {

         //
         //  create a date field for queries
         //
         date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd
         String mysql_date = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);
         didone = false;        // init 'did one' flag

         if (col == 0) {      // if new row

            out.println("<tr>");
         }

         out.println("<td align=\"left\" valign=\"top\"><font size=\"1\">");   // day of month

         out.println(day);         // just put in day of month
         out.println("<br>");
         

         //**********************************************************
         //  Check for any events for this day
         //**********************************************************
         //
         if (eventA[day] == 1) {        // if any events  exist for this day

            try {

               if (club.equals( "tcclub" )) {
                 
                  pstmt1 = con.prepareStatement (
                     "SELECT name, coursename, signup, activity_id " +
                     "FROM events2b WHERE date = ? AND inactive = 0 " +
                     (!allAct ? "AND activity_id = ?" : ""));

               } else {

                  pstmt1 = con.prepareStatement (
                     "SELECT name, coursename, signup, activity_id " +
                     "FROM events2b WHERE date = ? AND gstOnly = 0 AND inactive = 0 " +
                     (!allAct ? "AND activity_id = ?" : ""));
               }

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, date);
               if (!allAct) pstmt1.setInt(2, selected_act_id);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  ename = rs.getString(1);
                  course = rs.getString(2);
                  signUp = rs.getInt(3);
                  returned_activity_id = rs.getInt("activity_id");

                  //
                  //  Add a link for this event
                  //
                  if (returned_activity_id == sess_activity_id) {
                      out.println("<a href=\"javascript: exeEventForm('" +ename+ "','" +course+ "')\" class=meventblue>");
                      out.println(ename+ "</a><br>");
                  } else {
                      out.println("<span class=meventblue>" +ename+ "</span><br>");
                  }

               }          // end of WHILE events
               pstmt1.close();

            }
            catch (Exception e1) {
               String errorMsg = "Common_calendar: Error processing events for " +club+ ", Day=" +day+ ", User: " +user+ ", Error: " +e1.getMessage(); // build error msg
               SystemUtils.logError(errorMsg);                           // log it
            }

         }    // end of IF events


         //
         //**********************************************************
         //  End of display for this day - get next day
         //**********************************************************
         //
         if (didone == true) {          // if we added something to the day
            out.println("</td>");       // end of column (day)
         } else {
            out.println("<br><br><br></td>");
         }
         col++;
         day++;

         if (col == 7) {
            col = 0;                             // start new week
            out.println("</tr>");
         }
      }

      if (col != 0) {      // if not at the start

         while (col != 0 && col < 7) {      // finish off this row if not at the end

            out.println("<td>&nbsp;</td>");
            col++;
         }
         out.println("</tr>");
      }

      //
      // end of calendar row
      //
      out.println("</table>");
      out.println("<br>");
        
      today = 1;       // ready for next month
      month++;
        
      if (month > 12) {     // if end of year
         year++;         
         month = 1;
      }
      
   } // end if month loop

   out.println("</font></td>");
   out.println("</tr>");
   out.println("</table>");                   // end of table for main page

   //
   //  Build forms for submitting requests in calendars
   //
   if (user.startsWith("proshop")) {
      out.println("<form name=\"eventForm\" action=\"Proshop_events2\" method=\"post\" target=\"bot\">");
   } else {
      out.println("<form name=\"eventForm\" action=\"Member_events2\" method=\"post\" target=\"bot\">");
   }
   out.println("<input type=\"hidden\" name=\"name\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"\">");  // indicate from teelist
   out.println("</form>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }   // end of doGet
  
}
