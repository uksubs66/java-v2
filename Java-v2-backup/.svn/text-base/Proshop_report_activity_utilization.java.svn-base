/***************************************************************************************
 *   Proshop_report_activity_utilization: This servlet will ouput the activity utilization report
 *                                        (by hour of day and day of week)
 *
 *
 *   Called by:     called by main menu options and by its own outputed html
 *
 *
 *   Created:       9/23/2010
 *
 *
 *   Last Updated:
 *
 *    7/01/13   Added an option to count all restricted time slots as utilized slots, based off the rest_id field and regardless of suspensions.
 *    6/28/13   Added labels to all radio and checkbox options to make them easier to select.
 *    3/12/13   Updated verbiage to say "Total Time Slots" instead of "Time Slots Available", and "Time Slots Used" instead of "Time Slots Reserved" to improve clarity.
 *   10/18/12   Added a Total Reservations column to make the # of different player counts make more sense.
 *   10/17/12   Fixed outstanding issues with how the # of different number player reservations and the member/guest counts were reporting, as they were highly inaccurate.
 *    8/03/12   FlxRez event slots will now always be counted as used, since they do not move players to the tee sheet (they were all getting marked as unused instead).
 *    2/03/12   Fixed issue where the month and day numbers were getting swapped when determining the date of the oldest time sheet.
 *    3/08/11   Finished the Each Time Slot option code, and updated the Each Hour of Day and Each Time Slot options to display a "total" line for each day
 *   12/21/10   Added excel output handling as well as a Home button on web page report display page.
 *   12/20/10   Fixed player count and gender counts not getting updated for the first player in a time slot.
 *
 *
 *
 *
 ***************************************************************************************
 */

// standard java imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.*;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_report_activity_utilization extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;


 //****************************************************
 // Process the initial call from menu
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


   //
   //  Prevent caching of this page
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
      return;
   }

   Connection con = Connect.getCon(req);                   // get DB connection
   if (con == null) {
       SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
       return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   // our oldest date variables (how far back calendars go)
   int oldest_mm = 0;
   int oldest_dd = 0;
   int oldest_yy = 0;

   //
   // lookup oldest date in activity_sheets
   //
   try {
       stmt = con.createStatement();
       rs = stmt.executeQuery("" +
               "SELECT " +
                   "DATE_FORMAT(date_time, '%c') AS mm, " +
                   "DATE_FORMAT(date_time, '%e') AS dd, " +
                   "DATE_FORMAT(date_time, '%Y') AS yy " +
               "FROM activity_sheets " +
               "ORDER BY date_time ASC " +
               "LIMIT 1");

       if (rs.next()) {
           oldest_mm = rs.getInt(1);
           oldest_dd = rs.getInt(2);
           oldest_yy = rs.getInt(3);
       }

   } catch (Exception e) {
       SystemUtils.buildDatabaseErrMsg("Error looking up oldest teetime.", e.getMessage(), out, true);
       return;
   }


   // start ouput
   out.println(SystemUtils.HeadTitle("Proshop - Rounds Played Report"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   SystemUtils.getProshopSubMenu(req, out, lottery);                // required to allow submenus on this page

   // set calendar vars
   Calendar cal_date = new GregorianCalendar();
   cal_date.add(Calendar.DAY_OF_MONTH, -1);
   int cal_year = cal_date.get(Calendar.YEAR);
   int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
   int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   // start main table for this page
   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");
   out.println("<font size=\"2\">");

   // output instructions
   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\"><b>Activity Utilization Report</b></font><br>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<br>This report will display the utilization for each <br>day of the week and selected time periods.<br>");
   out.println("<br>Select the report options below, then click");
   out.println("<br>on <b>Build Report</b> to generate the report.</font></td></tr>");
   out.println("</table><br>");

   out.println("<br><b>1. Select a date range for the report period:</b><br><br>");

   // start submission form
   out.println("<form action=\"Proshop_report_activity_utilization\" method=\"post\">");

   // output table that hold calendars and their related text boxes
   out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println(" <br><input type=text name=cal_box_0 id=cal_box_0>");
    out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
    out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println(" <br><input type=text name=cal_box_1 id=cal_box_1>");
   out.println("</td>\n</tr></table>\n");

   // location checkboxes
   out.println("<p align=\"left\"><b>2. Locations to Include:</b>&nbsp;&nbsp;&nbsp;");
   Common_Config.displayActivitySheetSelect("", sess_activity_id, true, con, out);
   out.println("</p>");


   // report Days of Week options
   out.println("<p align=\"left\"><b>3. Days of Week to Report:</b>&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"mon\" id=\"mon\" value=\"1\" checked><label for=\"mon\">&nbsp;&nbsp;Monday</label>");
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"tue\" id=\"tue\" value=\"1\" checked><label for=\"tue\">&nbsp;&nbsp;Tuesday</label>");
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"wed\" id=\"wed\" value=\"1\" checked><label for=\"wed\">&nbsp;&nbsp;Wednesday</label>");
     out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"thu\" id=\"thu\" value=\"1\" checked><label for=\"thu\">&nbsp;&nbsp;Thursday</label>");
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"fri\" id=\"fri\" value=\"1\" checked><label for=\"fri\">&nbsp;&nbsp;Friday</label>");
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"sat\" id=\"sat\" value=\"1\" checked><label for=\"sat\">&nbsp;&nbsp;Saturday</label>");
     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input type=\"checkbox\" name=\"sun\" id=\"sun\" value=\"1\" checked><label for=\"sun\">&nbsp;&nbsp;Sunday</label>");
   out.println("</p>");


   // report time interval options
   out.println("<p align=\"left\"><b>4. Display Counts For:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input checked type=\"radio\" name=\"report_interval\" id=\"report_interval_day\" value=\"day\"><label for=\"report_interval_day\">Entire Day</label>");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_interval\" id=\"report_interval_hour\" value=\"hour\"><label for=\"report_interval_hour\">Each Hour of Day</label>");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_interval\" id=\"report_interval_teetimes\" value=\"teetimes\"><label for=\"report_interval_teetimes\">Each Time Slot</label>");
   out.println("</p>");


   // event time options
   out.println("<p align=\"left\"><b>5. Include Event Times and Lesson Times?:</b>&nbsp;&nbsp;");
     out.println("<input checked type=\"radio\" name=\"report_events\" id=\"report_events_no\" value=\"no\"><label for=\"report_events_no\">No</label>");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_events\" id=\"report_events_yes\" value=\"yes\"><label for=\"report_events_yes\">Yes</label>");
   out.println("</p>");

   // option on how to report restricted time slots
   out.println("<p align=\"left\"><b>6. Count restricted time slots as utilized?:</b>&nbsp;&nbsp;");
     out.println("<input checked type=\"radio\" name=\"report_restricted\" id=\"report_restricted_no\" value=\"no\"><label for=\"report_restricted_no\">No</label>");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_restricted\" id=\"report_restricted_yes\" value=\"yes\"><label for=\"report_restricted_yes\">Yes</label>");
   out.println("</p>");

/*
   // report data options
   out.println("<p align=\"left\"><b>5. Report Data:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input checked type=\"radio\" name=\"report_data\" value=\"general\">General Utilization");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_data\" value=\"mtype\">By Member Type");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_data\" value=\"mship\">By Membership Type");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_data\" value=\"guest\">By Guest Type");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_data\" value=\"modes\">By Mode of Transportation");
   out.println("</p>");
*/

   // report format options
   out.println("<p align=\"left\"><b>7. Report Format:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
     out.println("<input checked type=\"radio\" name=\"report_format\" id=\"report_format_web\" value=\"web\"><label for=\"report_format_web\">Web Page</label>");
     out.println("&nbsp; &nbsp;&nbsp; &nbsp;");
     out.println("<input type=\"radio\" name=\"report_format\" id=\"report_format_excel\" value=\"excel\"><label for=\"report_format_excel\">Excel</label>");
   out.println("</p>");

/*
   if (multi != 0) {           // if multiple courses supported for this club

      // report course options
      out.println("<p align=\"left\"><b>7. Course to Report:</b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"report_course\">");

        for (i=0; i<course.size(); i++) {

           out.println("<option value=\"" + course.get(i) + "\">" + course.get(i) + "</option>");
        }
        out.println("</select>");
      out.println("</p>");

   } else {

      out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +courseName+ "\">");
   }
*/

   // report button (go)
   out.println("<p align=\"center\"><input type=\"submit\" value=\" Build Report \"></p>");

   // end date submission form
   out.println("</form>");

   // output back button form
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
    out.println("<p align=\"center\"><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\"></p>");
   out.println("</form>");

   // end of main page table
   out.println("</td></tr></table>");

   // start calendar javascript setup code
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


    // set calendar date parts in js
    out.println("g_cal_month[0] = " + cal_month + ";");
    out.println("g_cal_year[0] = " + cal_year + ";");
    out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
    out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
    out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
    out.println("g_cal_ending_month[0] = " + cal_month + ";");
    out.println("g_cal_ending_day[0] = " + cal_day + ";");
    out.println("g_cal_ending_year[0] = " + cal_year + ";");

    out.println("g_cal_month[1] = " + cal_month + ";");
    out.println("g_cal_year[1] = " + cal_year + ";");
    out.println("g_cal_beginning_month[1] = " + oldest_mm + ";");
    out.println("g_cal_beginning_year[1] = " + oldest_yy + ";");
    out.println("g_cal_beginning_day[1] = " + oldest_dd + ";");
    out.println("g_cal_ending_month[1] = " + cal_month + ";");
    out.println("g_cal_ending_day[1] = " + cal_day + ";");
    out.println("g_cal_ending_year[1] = " + cal_year + ";");

    out.println("function sd(pCal, pMonth, pDay, pYear) {");
    out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
    out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
    out.println("}");

   out.println("</script>");

   out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>");

   out.println("</center></font>");
   out.println("</body></html>");
   out.close();                            // wait for 'Go'

 } // end doGet


 //****************************************************
 // Process the post - call from get processing
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


   //
   //  Prevent caching of this page
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
   resp.setContentType("text/html");

   PrintWriter out = resp.getWriter();                             // normal output stream

   HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
   if (session == null) { return; }

   Connection con = Connect.getCon(req);                   // get DB connection
   if (con == null) {
       displayDatabaseErrMsg("Can not establish connection.", "", out);
       return;
   }

   String club = (String)session.getAttribute("club");
   String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean error = false;

   String temp = "";

   int i = 0;
   //int count = 0;
   int mon = 0;
   int tue = 0;
   int wed = 0;
   int thu = 0;
   int fri = 0;
   int sat = 0;
   int sun = 0;
   int start_year;
   int start_month;
   int start_day;
   int end_year;
   int end_month;
   int end_day;


   //
   //  Get the input parms
   //
   String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
   String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";

   String format = (req.getParameter("report_format") != null) ? req.getParameter("report_format")  : "";

   String events = (req.getParameter("report_events") != null) ? req.getParameter("report_events")  : "";

   String interval = (req.getParameter("report_interval") != null) ? req.getParameter("report_interval")  : "";

   String data = (req.getParameter("report_data") != null) ? req.getParameter("report_data")  : "";

   String report_events = (req.getParameter("report_events") != null) ? req.getParameter("report_events")  : "no";
   
   String report_restricted = (req.getParameter("report_restricted") != null) ? req.getParameter("report_restricted") : "no";

   if (req.getParameter("mon") != null) {
      temp = req.getParameter("mon");
      mon = Integer.parseInt(temp);
   }
   if (req.getParameter("tue") != null) {
      temp = req.getParameter("tue");
      tue = Integer.parseInt(temp);
   }
   if (req.getParameter("wed") != null) {
      temp = req.getParameter("wed");
      wed = Integer.parseInt(temp);
   }
   if (req.getParameter("thu") != null) {
      temp = req.getParameter("thu");
      thu = Integer.parseInt(temp);
   }
   if (req.getParameter("fri") != null) {
      temp = req.getParameter("fri");
      fri = Integer.parseInt(temp);
   }
   if (req.getParameter("sat") != null) {
      temp = req.getParameter("sat");
      sat = Integer.parseInt(temp);
   }
   if (req.getParameter("sun") != null) {
      temp = req.getParameter("sun");
      sun = Integer.parseInt(temp);
   }


   // set response content type based on format requested (excel or web page)
   try{
       if (format.equals("excel")) {                           // if user requested Excel Spreadsheet Format
           resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
           resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
       }
   }
   catch (Exception exc) {
   }


   //
   //  Verify the input parms
   //
   // make sure the dates here are valid
   try {

       int dash1 = start_date.indexOf("-");
       int dash2 = start_date.indexOf("-", dash1 + 1);
       start_year = Integer.parseInt(start_date.substring(0, 4));
       start_month = Integer.parseInt(start_date.substring(dash1 + 1, dash2));
       start_day = Integer.parseInt(start_date.substring(dash2 + 1));

       dash1 = end_date.indexOf("-");
       dash2 = end_date.indexOf("-", dash1 + 1);
       end_year = Integer.parseInt(end_date.substring(0, 4));
       end_month = Integer.parseInt(end_date.substring(dash1 + 1, dash2));
       end_day = Integer.parseInt(end_date.substring(dash2 + 1));

   } catch (Exception e) {
       // invalid dates here, bailout and call form again
       displayInputErrMsg("Error processing the specified date range.", out);
       return;
   }

   // build our date variables for use in query
   long sdate = start_year * 10000;                    // create a date field of yyyymmdd
   sdate = sdate + (start_month * 100);
   sdate = sdate + start_day;

   long edate = end_year * 10000;                      // create a date field of yyyymmdd
   edate = edate + end_month * 100;
   edate = edate + end_day;

   if (sdate > edate) {
       // start date is after the end date, jump out and call form again
       displayInputErrMsg("Invalid date range. The start date must be earlier than the end date.", out);
       return;
   }

   //  verify that at least one day was requested
   if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {
       displayInputErrMsg("You must select at least one day of the week.", out);
       return;
   }

   //  verify that the interval was requested
   if (interval.equals( "" )) {
       displayInputErrMsg("You must select an interval (Display counts for:).", out);
       return;
   }

   //  verify that the format was requested
   if (format.equals( "" )) {
       displayInputErrMsg("You must select a Report Format.", out);
       return;
   }
   


   //******************************************************************
   //  Build the report
   //******************************************************************

   //
   //  parm block to hold counts, etc.
   //
   parmUtilization parm = new parmUtilization();          // allocate a parm block


   //
   //  Input parms ok - save them in the parm block
   //
   parm.sdate = sdate;
   parm.edate = edate;
   parm.start_year = start_year;
   parm.start_month = start_month;
   parm.start_day = start_day;
   parm.end_year = end_year;
   parm.end_month = end_month;
   parm.end_day = end_day;
   parm.mon = mon;
   parm.tue = tue;
   parm.wed = wed;
   parm.thu = thu;
   parm.fri = fri;
   parm.sat = sat;
   parm.sun = sun;
   parm.club = club;
   parm.format = format;
   parm.data = data;
   parm.events = events;
   parm.interval = interval;
   parm.colCount = 1;           // start with 1 for the time column
   parm.locations_csv = Common_Config.buildLocationsString(req);

   parm.t1player = true;
   parm.t2players = true;
   parm.t3players = true;
   parm.t4players = true;
   parm.tTimesAvail = true;
   parm.tTimesUsed = true;
   parm.tTimesUnused = true;
   parm.memRounds = true;
   parm.gstRounds = true;
   parm.inc_gender = true;
   parm.colCount = 6;
   parm.inc_events = (report_events.equals("yes"));
   parm.count_restricted = (report_restricted.equals("yes"));
   
   if (parm.inc_events) parm.colCount = parm.colCount + 3;
   
   if (parm.inc_gender) parm.colCount = parm.colCount + 2;

   if (parm.t1player) parm.colCount++;
   if (parm.t2players) parm.colCount++;
   if (parm.t3players) parm.colCount++;
   if (parm.t4players) parm.colCount++;
   if (parm.t4players || parm.t3players || parm.t2players || parm.t1player) parm.colCount++;


   //debug
   out.println("<!-- DEBUG -->");
   out.println("<!-- parm.sun=" + parm.sun + " -->");
   out.println("<!-- parm.mon=" + parm.mon + " -->");
   out.println("<!-- parm.tue=" + parm.tue + " -->");
   out.println("<!-- parm.wed=" + parm.wed + " -->");
   out.println("<!-- parm.thu=" + parm.thu + " -->");
   out.println("<!-- parm.fri=" + parm.fri + " -->");
   out.println("<!-- parm.sat=" + parm.sat + " -->");


   //
   //  build the report
   //
   if (!format.equals("excel")) { out.println(SystemUtils.HeadTitle("Proshop - Utilization Report")); }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!format.equals("excel")) { SystemUtils.getProshopSubMenu(req, out, lottery); }               // required to allow submenus on this page

   // start report output
   out.println("<table border=\"0\" align=\"center\">");    // table for whole page
   out.println("<tr>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");

   out.println("<table border=\"1\" cellpadding=\"5\" align=\"center\" bgcolor=\"#F5F5DC\">");   // heading table
   out.println("<tr>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"3\">");

   out.println("<p><b>Activity Utilization Report (General Data)<br>");

   out.println("for " + start_month + "/" + start_day + "/" + start_year + " to");
   out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b>");

   out.println("</font><font size=\"2\"><br><br><b>Notes:</b> Percentages are rounded down to whole number.<br>");

   out.println("Times and Slots Available excludes those that are blocked.<br>");

   if (parm.interval.equals("hour") || parm.interval.equals("teetimes")) {
       out.println("When viewing by hour or each time slot, Reservation and Player counts are only listed for the start time <br>of the reservation, even if the reservation covers multiple time slots.<br>");
   }
   
   out.println("Gender counts are only as accurate as your member roster, so ensure your roster specifies their gender.<br>");

   // go thru each day of the week and gather its stats and output the results
   if (sun > 0) {       // if Sunday selected

      parm.day = "Sunday";
      parm.day_num = 0;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   
   if (mon > 0) {       // if Monday selected

      parm.day = "Monday";
      parm.day_num = 1;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }


   if (tue > 0) {       // if Tuesday selected

      parm.day = "Tuesday";
      parm.day_num = 2;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (wed > 0) {       // if Wednesday selected

      parm.day = "Wednesday";
      parm.day_num = 3;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (thu > 0) {       // if Thursday selected

      parm.day = "Thursday";
      parm.day_num = 4;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (fri > 0) {       // if Friday selected

      parm.day = "Friday";
      parm.day_num = 5;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (sat > 0) {       // if Saturday selected

      parm.day = "Saturday";
      parm.day_num = 6;

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) return;

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   out.println("</table>");
   
   // output back button form
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
    out.println("<p align=\"center\"><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\"></p>");
   out.println("</form>");

 } // end doPost


 private boolean gatherStats(parmUtilization parm, PrintWriter out, Connection con) {

    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int i = 0;
    int last_id = 0;
    int players = 0;

    // variables used for tallying data prior to storage in parm object
    int timeSlot = -1;
    int guests = 0;
    int members = 0;
    int males = 0;
    int females = 0;
    int singles = 0;
    int doubles = 0;
    int triples = 0;
    int quads = 0;
    int slots_avail = 0;
    int slots_used = 0;
    int slots_unused = 0;
    int event_slots = 0;
    int event_slots_used = 0;
    int event_slots_unused = 0;

    int last_timeSlot = -1;


    boolean sametime = false;

    String last_player_name = "";
    String last_related_ids = "";
    String order_by = "";
    
    List<String> previous_related_ids = new ArrayList<String>();
    List<Integer> excluded_rests = new ArrayList<Integer>();

    for (i=0; i<24; i++) {
        parm.hour[i] = -1;
    }

    i = 0;

    if (parm.interval.equals("hour") || parm.interval.equals("teetimes")) {

        order_by = "t1.date_time ASC, t1.sheet_id, t2.pos";

    } else {

        order_by = "timeSlot, t1.sheet_id, t2.pos";
    }
    
    if (parm.count_restricted) {
        
        try {

            // Gather list of rest_ids to exclude from the "count restricted times as utilized" option. Is activity-agnostic, but we don't have a root act_id here, and this won't harm anything.
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT id FROM restriction2 WHERE utilization_exclude = 1");

            while (rs.next()) {
                excluded_rests.add(rs.getInt("id"));
            }

        } catch (Exception e) {
            Utilities.logError("Proshop_report_activity_utilization.gatherStats - " + parm.club + " - Failed looking up excluded restrictions - Error = " + e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
    }
    
    try {

        pstmt = con.prepareStatement (
            "SELECT t1.sheet_id, t1.event_id, t1.rest_id, t1.lesson_id, t1.related_ids, t2.username, t2.player_name, " +
                "DATE_FORMAT(date_time,'%k') AS hr, CAST(DATE_FORMAT(date_time, '%k%i') AS UNSIGNED) AS timeSlot, IFNULL(m.gender, '') AS gender " +
            "FROM activity_sheets t1 " +
            "LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id " +
            "LEFT OUTER JOIN member2b m ON t2.username = m.username " +
            "WHERE " +
                "DATE_FORMAT(date_time,'%Y%m%d') >= ? AND DATE_FORMAT(date_time,'%Y%m%d') <= ? AND " +
                "DATE_FORMAT(date_time,'%w') = ? AND t1.blocker_id = 0 AND " +
                "t1.activity_id IN (" + parm.locations_csv + ") " +
                ((!parm.inc_events) ? "AND event_id = 0 AND lesson_id = 0 " : "") +            
            "ORDER BY " + order_by + ";");

        pstmt.clearParameters();
        pstmt.setLong(1, parm.sdate);
        pstmt.setLong(2, parm.edate);
        pstmt.setInt(3, parm.day_num);           // # for day of week 0-6

        rs = pstmt.executeQuery();
/*
        // if we found any then output the header row.
        rs.last();
        if (rs.getRow() > 0) {

            out.println(rs.getRow() + " found.");

        }

        rs.beforeFirst();
*/    
        while (rs.next()) {
            
            if (rs.getInt("sheet_id") == last_id) {

                // this is the same time slot as last time thru this loop
                // so this row only contains an additional player for this time slot
                // note: should be a player and not null
                
                if (rs.getString("related_ids").equals("") || !previous_related_ids.contains(rs.getString("related_ids"))) {

                    players++;
                    sametime = true;
                    
                    // determine if this is a guest or member
                    if (rs.getString("username").equals("") && !rs.getString("player_name").equals("")) {

                        if (parm.interval.equals("teetimes")) {
                            guests++;
                        } else {
                            parm.guests[i]++;
                        }

                        parm.total_guests++;

                    } else {

                        if (parm.interval.equals("teetimes")) {
                            members++;
                        } else {
                            parm.members[i]++;
                        }

                        parm.total_members++;
                    }

                    if (rs.getString("gender").equalsIgnoreCase("m")) {

                        if (parm.interval.equals("teetimes")) {
                            males++;
                        } else {
                            parm.males[i]++;
                        }

                        parm.total_males++;

                    } else if (rs.getString("gender").equalsIgnoreCase("f")) {

                        if (parm.interval.equals("teetimes")) {
                            females++;
                        } else {
                            parm.females[i]++;
                        }

                        parm.total_females++;
                    }
                }

            } else {

                // this is a different time slot than the last one
                // first lets see if we just finished processing players from the same time slot
                if (last_related_ids.equals("") || !previous_related_ids.contains(last_related_ids)) {
                    
                    if (sametime) {

                        if (players == 2) {

                            if (parm.interval.equals("teetimes")) {
                                doubles++;
                            } else {
                                parm.doubles[i]++;
                            }

                            parm.total_doubles++;

                        } else if (players == 3) {

                            if (parm.interval.equals("teetimes")) {
                                triples++;
                            } else {
                                parm.triples[i]++;
                            }

                            parm.total_triples++;

                        } else if (players == 4) {

                            if (parm.interval.equals("teetimes")) {
                                quads++;
                            } else {
                                parm.quads[i]++;
                            }

                            parm.total_quads++;

                        } else {
                            SystemUtils.logError("Players=" + players);
                        }
                        sametime = false;
                        players = 0;

                    } else {

                        // since sametime was not true and we're on a different time slot then
                        // the last one we know that the previous time only contained one player
                        // (or do we? - maybe there was no asp data)
                        if (last_player_name != null && !last_player_name.equals("")) {

                            if (parm.interval.equals("teetimes")) {
                                singles++;
                            } else {
                                parm.singles[i]++;
                            }

                            parm.total_singles++;
                        }
                        players = 0;
                    }
                }


                // if in interval = teetimes mode, see if the time slot has changed, if so, add current values to parm ArrayLists
                if (parm.interval.equals("teetimes")) {

                    timeSlot = rs.getInt("timeSlot");

                    if (timeSlot != last_timeSlot) {

                        if (last_timeSlot == -1) {      // if first time through

                            last_timeSlot = timeSlot;

                        } else {
            
                            parm.timeSlotL.add(last_timeSlot);
                            parm.guestsL.add(guests);
                            parm.membersL.add(members);
                            parm.malesL.add(males);
                            parm.femalesL.add(females);
                            parm.singlesL.add(singles);
                            parm.doublesL.add(doubles);
                            parm.triplesL.add(triples);
                            parm.quadsL.add(quads);
                            parm.slots_availL.add(slots_avail);
                            parm.slots_usedL.add(slots_used);
                            parm.slots_unusedL.add(slots_unused);
                            parm.event_slotsL.add(event_slots);
                            parm.event_slots_usedL.add(event_slots_used);
                            parm.event_slots_unusedL.add(event_slots_unused);

                            last_timeSlot = timeSlot;
                            guests = 0;
                            members = 0;
                            males = 0;
                            females = 0;
                            singles = 0;
                            doubles = 0;
                            triples = 0;
                            quads = 0;
                            slots_avail = 0;
                            slots_used = 0;
                            slots_unused = 0;
                            event_slots = 0;
                            event_slots_used = 0;
                            event_slots_unused = 0;
                        }
                    }
                }

                // if interval is hour then we want to set i equal to the new hour
                if (parm.interval.equals( "hour" )) {
                    
                    i = rs.getInt("hr");
                    parm.hour[i] = rs.getInt("hr");
                }
                
                /*{

                    if (this_hour != parm.hour[i]) {             // if new hour value

                        i++;                                // bump to next position
                        parm.hour[i] = this_hour;                // save the hour value
                    }

                } // else interval is day and we don't need to ever bump i
                */
                
                // If related_ids hasn't changed, this time slot is part of the same reservation, so we don't want to count the players this time
                if (!last_related_ids.equals("") && !previous_related_ids.contains(last_related_ids)) {
                    previous_related_ids.add(last_related_ids);                        
                }
                
                last_related_ids = rs.getString("related_ids"); 
                
                // let's bump the slots avail counter
                if (parm.interval.equals("teetimes")) {
                    slots_avail++;
                } else {
                    parm.slots_avail[i]++;
                }

                parm.total_slots_avail++;

                // let's see if this time slot is covered by an event and if so bump that count
                if (rs.getInt("event_id") != 0 || rs.getInt("lesson_id") != 0) {
                    
                    if (parm.interval.equals("teetimes")) {
                        event_slots++;
                    } else {
                        parm.event_slots[i]++;
                    }

                    parm.total_event_slots++;
                }

                if (rs.getString("player_name") == null) {

                    // For blank player slots in FlxRez, we need to always treat event slots as USED since they cannot plug in the players. Lessons are included in this as well.
                    if (rs.getInt("event_id") != 0 || rs.getInt("lesson_id") != 0) {
                        
                        if (parm.interval.equals("teetimes")) {
                            slots_used++;
                            event_slots_used++;
                        } else {
                            parm.slots_used[i]++;
                            parm.event_slots_used[i]++;
                        }

                        parm.total_slots_used++;
                        parm.total_event_slots_used++;
                        
                    } else if (parm.count_restricted && rs.getInt("rest_id") != 0 && !excluded_rests.contains(rs.getInt("rest_id"))) {
                        
                        if (parm.interval.equals("teetimes")) {
                            slots_used++;
                        } else {
                            parm.slots_used[i]++;
                        }

                        parm.total_slots_used++;
                        
                    } else {
                        
                        if (parm.interval.equals("teetimes")) {
                            slots_unused++;
                        } else {
                            parm.slots_unused[i]++;
                        }

                        parm.total_slots_unused++;
                    }

                } else {

                    if (parm.interval.equals("teetimes")) {
                        slots_used++;
                    } else {
                        parm.slots_used[i]++;
                    }

                    parm.total_slots_used++;
                    
                    if (rs.getInt("event_id") != 0 || rs.getInt("lesson_id") != 0) {
                        
                        if (parm.interval.equals("teetimes")) {
                            event_slots_used++;
                        } else {
                            parm.event_slots_used[i]++;
                        }

                        parm.total_event_slots_used++;
                    }
                    
                    if (rs.getString("related_ids").equals("") || !previous_related_ids.contains(rs.getString("related_ids"))) {
                    
                        players++;
                
                        // determin if this is a guest or member
                        if (rs.getString("username").equals("") && !rs.getString("player_name").equals("")) {

                            if (parm.interval.equals("teetimes")) {
                                guests++;
                            } else {
                                parm.guests[i]++;
                            }

                            parm.total_guests++;

                        } else {

                            if (parm.interval.equals("teetimes")) {
                                members++;
                            } else {
                                parm.members[i]++;
                            }

                            parm.total_members++;
                        }

                        if (rs.getString("gender").equalsIgnoreCase("m")) {

                            if (parm.interval.equals("teetimes")) {
                                males++;
                            } else {
                                parm.males[i]++;
                            }

                            parm.total_males++;

                        } else if (rs.getString("gender").equalsIgnoreCase("f")) {

                            if (parm.interval.equals("teetimes")) {
                                females++;
                            } else {
                                parm.females[i]++;
                            }

                            parm.total_females++;
                        }
                    }
                }

            } // end if new time slot id

            last_id = rs.getInt("sheet_id");
            last_player_name = rs.getString("player_name");
        }

        if (last_related_ids.equals("") && previous_related_ids.contains(last_related_ids)) {
            
            // Run through player count tally one final time
            if (sametime) {

                if (players == 2) {

                    if (parm.interval.equals("teetimes")) {
                        doubles++;
                    } else {
                        parm.doubles[i]++;
                    }

                    parm.total_doubles++;

                } else if (players == 3) {

                    if (parm.interval.equals("teetimes")) {
                        triples++;
                    } else {
                        parm.triples[i]++;
                    }

                    parm.total_triples++;

                } else if (players == 4) {

                    if (parm.interval.equals("teetimes")) {
                        quads++;
                    } else {
                        parm.quads[i]++;
                    }

                    parm.total_quads++;

                } else {
                    SystemUtils.logError("Players=" + players);
                }

            } else {

                // since sametime was not true and we're on a different time slot then
                // the last one we know that the previous time only contained one player
                // (or do we? - maybe there was no asp data)
                if (last_player_name != null && !last_player_name.equals("")) {

                    if (parm.interval.equals("teetimes")) {
                        singles++;
                    } else {
                        parm.singles[i]++;
                    }

                    parm.total_singles++;
                }
            }
        }


        // Run through adding tallies to parm ArrayLists one final time for the last time encountered.
        if (parm.interval.equals("teetimes") && last_timeSlot != -1) {

            parm.timeSlotL.add(last_timeSlot);
            parm.guestsL.add(guests);
            parm.membersL.add(members);
            parm.malesL.add(males);
            parm.femalesL.add(females);
            parm.singlesL.add(singles);
            parm.doublesL.add(doubles);
            parm.triplesL.add(triples);
            parm.quadsL.add(quads);
            parm.slots_availL.add(slots_avail);
            parm.slots_usedL.add(slots_used);
            parm.slots_unusedL.add(slots_unused);
            parm.event_slotsL.add(event_slots);
            parm.event_slots_usedL.add(event_slots_used);
            parm.event_slots_unusedL.add(event_slots_unused);
        }


    } catch (SQLException exc) {

        out.println("<p>ERROR: " + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return false;

 }



 //***************************************************************************
 // Common Method build and display one day for the report in Web Page Format
 //***************************************************************************
 //
 private void buildDayTableWeb(parmUtilization parm, PrintWriter out, Connection con) {

/*
    boolean error = false;
    boolean includeEvents = false;

    if (parm.events.equals( "yes" )) {          // if we are to include event times

        includeEvents = true;
    }
*/
    String ampm = "";
    String bgcolor = "#F5F5DC";
    String bgcolor1 = "#F5F5DC";
    String bgcolor2 = "#CDCDB4";

    int i = 0;
    int hr = 0;
    int min = 0;
    int time = 0;
    int time2 = 0;
    int slots = 0;
    int slotsu = 0;
    int avail = 0;
    int timeSlotCount = 0;
    int loopLimit = 0;

    out.println("<table border=\"1\" cellpadding=\"5\" align=\"center\" bgcolor=\"#CDCDB4\">");

    out.println("<tr bgcolor=\"#336633\"><td colspan=\"" +parm.colCount+ "\" align=\"center\">");
    out.println("<font size=\"2\" color=\"#FFFFFF\">");
    out.println("<b>" +parm.day+ "</b>");
    out.println("</font></td></tr>");

    out.println("<tr bgcolor=\"#8B8970\"><td align=\"center\">");     // heading row
    out.println("<font size=\"2\">");
    out.println("<b>Time</b>");
    out.println("</font></td>");

    if (parm.tTimesAvail == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Total Time<br>Slots</b>");
        out.println("</font></td>");
    }

    if (parm.tTimesUsed == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Time Slots<br>Used</b>");
        out.println("</font></td>");
    }

    if (parm.tTimesUnused == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Time Slots<br>Unused</b>");
        out.println("</font></td>");
    }
/*
    if (parm.tTimesFull == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Full<br>Times</b>");
        out.println("</font></td>");
    }
*/
    if (parm.t4players || parm.t3players || parm.t2players || parm.t1player) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Total<br>Reservations</b>");
        out.println("</font></td>");
    }
    
    if (parm.t4players == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>4<br>Players</b>");
        out.println("</font></td>");
    }

    if (parm.t3players == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>3<br>Players</b>");
        out.println("</font></td>");
    }

    if (parm.t2players == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>2<br>Players</b>");
        out.println("</font></td>");
    }

    if (parm.t1player == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>1<br>Player</b>");
        out.println("</font></td>");
    }

    if (parm.inc_events == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Event/Lesson Times<br>Found</b>");
        out.println("</font></td>");
    }

    if (parm.inc_events == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Event/Lesson Times<br>Utilized</b>");
        out.println("</font></td>");
    }

    if (parm.inc_events == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Event Times<br>Unused</b>");
        out.println("</font></td>");
    }

    if (parm.memRounds == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Total<br>Members</b>");
        out.println("</font></td>");
    }

    if (parm.gstRounds == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Total<br>Guests</b>");
        out.println("</font></td>");
    }

    if (parm.inc_gender == true) {          // if column to be displayed

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Total<br>Male</b>");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<b>Total<br>Female</b>");
        out.println("</font></td>");
    }

    out.println("</tr>");




   if (parm.interval.equals("teetimes")) {

       loopLimit = parm.timeSlotL.size();

   } else {

       loopLimit = 24;
   }

   //
   //  output one row for each tee time, or each hour, or one row if interval=day
   //
   loop1:
   for (i=0; i<loopLimit; i++) {
       
      //
      //  get the time value and convert to string
      //
      if (parm.interval.equals( "day" )) {       // no time values for 'day'

         //avail = parm.slots_avail[0];                 // get total available tee times

         time2 = 0;                              // indicate 'day' display for later

      } else {

         if (parm.times[i] == 0) {

            //break loop1;           // exit if done
         }

         //avail = parm.timesU[i] + parm.eTimesA[i];      // get total available tee times (teepast used + all empty)
                                                        // NOTE: parm.eTimesA contains all empty times and no-shows!! (empty/unused from teepast2 and teepastempty)

         if (parm.interval.equals( "hour" )) {

            hr = parm.hour[i];                          // only hour value in time slot

            time2 = hr;                                 // save for later

         } else {

            time = parm.timeSlotL.get(i);

            hr = time / 100;           // get hour value

            min = time - (hr * 100);   // get minutes

            time2 = time;              // save for later

         }

         ampm = " AM";

         if (hr == 12) {

            ampm = " PM";

         } else {

            if (hr > 12) {

               hr = hr - 12;

               ampm = " PM";
            }
         }
      }

      //slots = (avail * 4);                             // total Slots available

      //slotsu = (slots - parm.slotsU[i]);               // unused slots

      // Calculate total number of time slots
      if (parm.interval.equals("teetimes")) {
          timeSlotCount = parm.singlesL.get(i) + parm.doublesL.get(i) + parm.triplesL.get(i) + parm.quadsL.get(i);
      } else {
          timeSlotCount = parm.singles[i] + parm.doubles[i] + parm.triples[i] + parm.quads[i];
      }
      

    // output results

    if (hr != -1) {

    out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");     // data row(s)
    out.println("<font size=\"2\">");
    if (parm.interval.equals( "day" )) {
        out.println("All");
    } else {
        if (parm.interval.equals( "hour" )) {
            out.println(hr + ":xx" + ampm);
        } else {
            out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
        }
    }
    out.println("</font></td>");


      if (parm.tTimesAvail == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             out.println(parm.slots_availL.get(i));
         } else {
             out.println(parm.slots_avail[i]);                            // total available tee times
         }
         out.println("</font></td>");
      }

      if (parm.tTimesUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (parm.slots_usedL.get(i) > 0) {
                out.println(parm.slots_usedL.get(i) + " (" +((parm.slots_usedL.get(i) * 100) / parm.slots_availL.get(i))+ "%)");     // tee times utilized
             } else {
                out.println("0");
             }
         } else {
             if (parm.slots_used[i] > 0) {
                out.println(parm.slots_used[i]+ " (" +((parm.slots_used[i] * 100) / parm.slots_avail[i])+ "%)");     // tee times utilized
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.tTimesUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (parm.slots_unusedL.get(i) > 0) {
                out.println(parm.slots_unusedL.get(i)+ " (" +((parm.slots_unusedL.get(i) * 100) / parm.slots_availL.get(i))+ "%)");   // unused tee times (empty)
             } else {
                out.println("0");
             }
         } else {
             if (parm.slots_unused[i] > 0) {
                out.println(parm.slots_unused[i]+ " (" +((parm.slots_unused[i] * 100) / parm.slots_avail[i])+ "%)");   // unused tee times (empty)
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }
/*
      if (parm.tTimesFull == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.fulls[i] > 0) {
            out.println(parm.fulls[i]+ " (" +((parm.fulls[i] * 100) / parm.slots_avail[i])+ "%)");         // Full Tee Times
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }
*/
      
      if (parm.t4players || parm.t3players || parm.t2players || parm.t1player) {
          
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (timeSlotCount > 0) {
             out.println(timeSlotCount);
         } else {
             out.println("0");
         }
         out.println("</font></td>");
      }
      
      if (parm.t4players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (timeSlotCount > 0 && parm.quadsL.get(i) > 0) {
                out.println(parm.quadsL.get(i)+ " (" +((parm.quadsL.get(i) * 100) / timeSlotCount)+ "%)");      // 4-some times
             } else {
                out.println("0");
             }
         } else {
             if (timeSlotCount > 0 && parm.quads[i] > 0) {
                out.println(parm.quads[i]+ " (" +((parm.quads[i] * 100) / timeSlotCount)+ "%)");      // 4-some times
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.t3players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (timeSlotCount > 0 && parm.triplesL.get(i) > 0) {
                out.println(parm.triplesL.get(i)+ " (" +((parm.triplesL.get(i) * 100) / timeSlotCount)+ "%)");      // 3-some times
             } else {
                out.println("0");
             }
         } else {
             if (timeSlotCount > 0 && parm.triples[i] > 0) {
                out.println(parm.triples[i]+ " (" +((parm.triples[i] * 100) / timeSlotCount)+ "%)");      // 3-some times
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.t2players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (timeSlotCount > 0 && parm.doublesL.get(i) > 0) {
                out.println(parm.doublesL.get(i)+ " (" +((parm.doublesL.get(i) * 100) / timeSlotCount)+ "%)");       // 2-some times
             } else {
                out.println("0");
             }
         } else {
             if (timeSlotCount > 0 && parm.doubles[i] > 0) {
                out.println(parm.doubles[i]+ " (" +((parm.doubles[i] * 100) / timeSlotCount)+ "%)");       // 2-some times
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.t1player == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (timeSlotCount > 0 && parm.singlesL.get(i) > 0) {
                out.println(parm.singlesL.get(i)+ " (" +((parm.singlesL.get(i) * 100) / timeSlotCount)+ "%)");      // 1-some times
             } else {
                out.println("0");
             }
         } else {
             if (timeSlotCount > 0 && parm.singles[i] > 0) {
                out.println(parm.singles[i]+ " (" +((parm.singles[i] * 100) / timeSlotCount)+ "%)");      // 1-some times
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.inc_events == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             out.println(parm.event_slotsL.get(i));
         } else {
             out.println(parm.event_slots[i]);                                                                             // Event times found
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (parm.event_slots_usedL.get(i) > 0) {
                out.println(parm.event_slots_usedL.get(i)+ " (" +((parm.event_slots_usedL.get(i) * 100) / parm.event_slotsL.get(i))+ "%)");     // Event times utilized
             } else {
                out.println("0");
             }
         } else {
             if (parm.event_slots_used[i] > 0) {
                out.println(parm.event_slots_used[i]+ " (" +((parm.event_slots_used[i] * 100) / parm.event_slots[i])+ "%)");     // Event times utilized
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (parm.event_slots_unusedL.get(i) > 0) {
                out.println(parm.event_slots_unusedL.get(i)+ " (" +((parm.event_slots_unusedL.get(i) * 100) / parm.event_slotsL.get(i))+ "%)");        // Unused Event times (empty)
             } else {
                out.println("0");
             }
         } else {
             if (parm.event_slots_unused[i] > 0) {
                out.println(parm.event_slots_unused[i]+ " (" +((parm.event_slots_unused[i] * 100) / parm.event_slots[i])+ "%)");        // Unused Event times (empty)
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.memRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (parm.membersL.get(i) > 0) {
                out.println(parm.membersL.get(i)); //+ " (" +((parm.membersL.get(i) * 100) / parm.slots_usedL.get(i))+ "%)");     // member rounds
             } else {
                out.println("0");
             }
         } else {
             if (parm.members[i] > 0) {
                out.println(parm.members[i]); //+ " (" +((parm.members[i] * 100) / parm.slots_used[i])+ "%)");     // member rounds
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.gstRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             if (parm.guestsL.get(i) > 0) {
                out.println(parm.guestsL.get(i)); //+ " (" +((parm.guestsL.get(i) * 100) / parm.slots_usedL.get(i))+ "%)");      // guest rounds
             } else {
                out.println("0");
             }
         } else {
             if (parm.guests[i] > 0) {
                out.println(parm.guests[i]); //+ " (" +((parm.guests[i] * 100) / parm.slots_used[i])+ "%)");      // guest rounds
             } else {
                out.println("0");
             }
         }
         out.println("</font></td>");
      }

      if (parm.inc_gender == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             out.println(parm.malesL.get(i));
         } else {
             out.println(parm.males[i]);
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.interval.equals("teetimes")) {
             out.println(parm.femalesL.get(i));
         } else {
             out.println(parm.females[i]);
         }
         out.println("</font></td>");
      }

      if (parm.interval.equals( "day" )) {      // if day report

         break loop1;                           // done
      }

      if (bgcolor.equals( bgcolor1 )) {

         bgcolor = bgcolor2;

      } else {

         bgcolor = bgcolor1;
      }
    } // end empty hr
   } // end loop of each row


   // If not running in full day mode, print a final row to display all the totals
   if (!parm.interval.equals("day")) {

        out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");     // data row(s)
        out.println("<font size=\"2\"><b>");
        out.println("Total");
        out.println("</b></font></td>");


      if (parm.tTimesAvail == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         out.println(parm.total_slots_avail);
         out.println("</b></font></td>");
      }

      if (parm.tTimesUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (parm.total_slots_used > 0) {
            out.println(parm.total_slots_used + " (" +((parm.total_slots_used * 100) / parm.total_slots_avail)+ "%)");     // tee times utilized
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }

      if (parm.tTimesUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (parm.total_slots_unused > 0) {
            out.println(parm.total_slots_unused + " (" +((parm.total_slots_unused * 100) / parm.total_slots_avail)+ "%)");   // unused tee times (empty)
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }
    /*
      if (parm.tTimesFull == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.fulls[i] > 0) {
            out.println(parm.fulls[i]+ " (" +((parm.fulls[i] * 100) / parm.slots_avail[i])+ "%)");         // Full Tee Times
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }
    */
      
      timeSlotCount = parm.total_singles + parm.total_doubles + parm.total_triples + parm.total_quads;
      
      if (parm.t4players || parm.t3players || parm.t2players || parm.t1player) {
          
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (timeSlotCount > 0) {
             out.println(timeSlotCount);
         } else {
             out.println("0");
         }
         out.println("</b></font></td>");
      }
      
      if (parm.t4players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (timeSlotCount > 0 && parm.total_quads > 0) {
            out.println(parm.total_quads + " (" +((parm.total_quads * 100) / timeSlotCount)+ "%)");      // 4-some times
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }

      if (parm.t3players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (timeSlotCount > 0 && parm.total_triples > 0) {
            out.println(parm.total_triples + " (" +((parm.total_triples * 100) / timeSlotCount)+ "%)");      // 3-some times
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }

      if (parm.t2players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (timeSlotCount > 0 && parm.total_doubles > 0) {
            out.println(parm.total_doubles + " (" +((parm.total_doubles * 100) / timeSlotCount)+ "%)");       // 2-some times
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }

      if (parm.t1player == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (timeSlotCount > 0 && parm.total_singles > 0) {
            out.println(parm.total_singles+ " (" +((parm.total_singles * 100) / timeSlotCount)+ "%)");      // 1-some times
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }

      if (parm.inc_events == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         out.println(parm.total_event_slots);
         out.println("</b></font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.total_event_slots_used > 0) {
            out.println(parm.total_event_slots_used + " (" +((parm.total_event_slots_used * 100) / parm.total_event_slots)+ "%)");     // Event times utilized
         } else {
            out.println("0");
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         if (parm.total_event_slots_unused > 0) {
            out.println(parm.total_event_slots_unused + " (" +((parm.total_event_slots_unused * 100) / parm.total_event_slots)+ "%)");        // Unused Event times (empty)
         } else {
            out.println("0");
         }
         out.println("</b></font></td>");
      }

      if (parm.memRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         out.println(parm.total_members);
         out.println("</b></font></td>");
      }

      if (parm.gstRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         out.println(parm.total_guests);
         out.println("</b></font></td>");
      }

      if (parm.inc_gender == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         out.println(parm.total_males);
         out.println("</b></font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\"><b>");
         out.println(parm.total_females);
         out.println("</b></font></td>");
      }

      out.println("</tr>");
   }

   out.println("</table>");
   
 } // end buildDayTableWeb


 //**************************************************
 // Common Method to reset the stats
 //**************************************************
 //
 private void resetStats(parmUtilization parm) {


   for (int i=0; i<parm.max; i++) {

      if (i < 24) {
          
          parm.slots_used[i] = 0;
          parm.slots_unused[i] = 0;
          parm.slots_avail[i] = 0;
          parm.hour[i] = 0;
          parm.event_slots[i] = 0;
          parm.event_slots_used[i] = 0;
          parm.event_slots_unused[i] = 0;
          parm.quads[i] = 0;
          parm.males[i] = 0;
          parm.females[i] = 0;
      }

      parm.times[i] = 0;
      parm.timesA[i] = 0;
      parm.timesU[i] = 0;
      parm.slotsU[i] = 0;
      parm.eventsU[i] = 0;
      parm.singles[i] = 0;
      parm.doubles[i] = 0;
      parm.triples[i] = 0;
      parm.fulls[i] = 0;
      parm.members[i] = 0;
      parm.guests[i] = 0;
      parm.eTimesA[i] = 0;
      parm.eEvents[i] = 0;
   }

   parm.total_guests = 0;
   parm.total_members = 0;
   parm.total_males = 0;
   parm.total_females = 0;
   parm.total_singles = 0;
   parm.total_doubles = 0;
   parm.total_triples = 0;
   parm.total_quads = 0;
   parm.total_slots_avail = 0;
   parm.total_slots_used = 0;
   parm.total_slots_unused = 0;
   parm.total_event_slots = 0;
   parm.total_event_slots_used = 0;
   parm.total_event_slots_unused = 0;

   if (parm.interval.equals("teetimes")) {

       parm.timeSlotL.clear();
       parm.guestsL.clear();
       parm.membersL.clear();
       parm.malesL.clear();
       parm.femalesL.clear();
       parm.singlesL.clear();
       parm.doublesL.clear();
       parm.triplesL.clear();
       parm.quadsL.clear();
       parm.slots_availL.clear();
       parm.slots_usedL.clear();
       parm.slots_unusedL.clear();
       parm.event_slotsL.clear();
       parm.event_slots_usedL.clear();
       parm.event_slots_unusedL.clear();
   }

 }


 //**************************************************
 // Common Method for Displaying Input Errors
 //**************************************************
 //
 private void displayInputErrMsg(String pMessage, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Input Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Invalid Request</H2>");
    out.println("<BR><BR>Sorry, we are unable to process the report.");
    out.println("<BR><br>" + pMessage);
    out.println("<BR>Please try again.");
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<font size=\"2\">");
    out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form></font>");
    out.println("</CENTER></BODY></HTML>");
 }


 //**************************************************
 // Common Method for Displaying Database Errors
 //**************************************************
 //
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }
 
}
