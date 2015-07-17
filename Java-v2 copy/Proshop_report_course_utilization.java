/***************************************************************************************
 *   Proshop_report_course_utilization: This servlet will ouput the course utilization report
 *                                      (by hour of day and day of week)
 *
 *
 *   Called by:     called by main menu options and by its own outputed html
 *
 *
 *   Created:       9/28/2006
 *
 *
 *   Last Updated:
 *
 *                 4/07/11  Updated report to fix errors when running report by mship, mtype, or gtype for future dates. Member mship/mtype data will now be queried on
 *                          the spot from member2b instead of from mship1-5 and mtype1-5, and the guest type will be determined from the player name instead of gtype1-5
 *                 3/09/11  Add abilty to generate reports for current tee times / Added labels to form elements
 *                 2/04/11  Return all the selected options so user can run report again without having to set the options every time.
 *                11/22/10  Monterey Peninsula CC (mpccpb) - Added button to custom Walk In Availability report (case 1883).
 *                 5/24/10  Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *                 4/15/09  Get mtype and mships from teepast instead of member2b (report upgrade).
 *                 1/14/09  Add Modes of Trans option to include counts for this.
 *                 7/18/08  Added limited access proshop users checks
 *                 6/19/08  Corrected the 'avail' count for excel display - was using timesA instead of timesU.
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
//import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Proshop_report_course_utilization extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;

    final String spacer_row = "<tr><td><img src=\""+rev+"/images/shim.gif\" width=\"1\" height=\"5\" border=\"0\"></td></tr>";
    final String spacer_item = "&nbsp; &nbsp;&nbsp; &nbsp;";
    final String spacer_label = "&nbsp;";
 
 //****************************************************
 // Process the get - initial call from menu
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

   Connection con = SystemUtils.getCon(session);                   // get DB connection
   if (con == null) {
       displayDatabaseErrMsg("Can not establish connection.", "", out);
       return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }

   String club = (String)session.getAttribute("club");      // get club name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   String format = "";
   String data = "";
   String events = "";
   String interval = "";
   String courseR = "";
   String mon = "";
   String tue = "";
   String wed = "";
   String thu = "";
   String fri = "";
   String sat = "";
   String sun = "";
   String sdate = "";
   String edate = "";
   String temp = "";

   // our oldest date variables (how far back calendars go)
   int oldest_mm = 0;
   int oldest_dd = 0;
   int oldest_yy = 0;
   int smonth = 0;
   int syear = 0;
   int emonth = 0;
   int eyear = 0;

   int curr_day = 0;
   int curr_month = 0;
   int curr_year = 0;

   int multi = 0;                                       // multiple course support
   int i = 0;

   //
   //  Array to hold the course names
   //
   String courseName = "";
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses
    

   //
   //  Get the last used parm settings in case we are returning here after just running the report
   //
   sdate = (req.getParameter("sdate") != null) ? req.getParameter("sdate")  : " ";

   edate = (req.getParameter("edate") != null) ? req.getParameter("edate")  : " ";

   format = (req.getParameter("report_format") != null) ? req.getParameter("report_format")  : "";

   events = (req.getParameter("report_events") != null) ? req.getParameter("report_events")  : "";

   interval = (req.getParameter("report_interval") != null) ? req.getParameter("report_interval")  : "";

   data = (req.getParameter("report_data") != null) ? req.getParameter("report_data")  : "";

   courseR = (req.getParameter("report_course") != null) ? req.getParameter("report_course")  : "";

   mon = (req.getParameter("mon") != null) ? req.getParameter("mon")  : "";
   tue = (req.getParameter("tue") != null) ? req.getParameter("tue")  : "";
   wed = (req.getParameter("wed") != null) ? req.getParameter("wed")  : "";
   thu = (req.getParameter("thu") != null) ? req.getParameter("thu")  : "";
   fri = (req.getParameter("fri") != null) ? req.getParameter("fri")  : "";
   sat = (req.getParameter("sat") != null) ? req.getParameter("sat")  : "";
   sun = (req.getParameter("sun") != null) ? req.getParameter("sun")  : "";
   
   if (req.getParameter("smonth") != null) {
      temp = req.getParameter("smonth");
      smonth = Integer.parseInt(temp);
   }
   if (req.getParameter("emonth") != null) {
      temp = req.getParameter("emonth");
      emonth = Integer.parseInt(temp);
   }
   if (req.getParameter("syear") != null) {
      temp = req.getParameter("syear");
      syear = Integer.parseInt(temp);
   }
   if (req.getParameter("eyear") != null) {
      temp = req.getParameter("eyear");
      eyear = Integer.parseInt(temp);
   }
   
   
   //
   // get the course names, if multi
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club5");

      if (rs.next()) {

         multi = rs.getInt("multi");
      }
      stmt.close();

      //
      //   Get course names if multi-course facility
      //
      if (multi != 0) {           // if multiple courses supported for this club

          course = Utilities.getCourseNames(con);     // get all the course names
      }

   } catch (Exception e) {
       displayDatabaseErrMsg("Error looking up course names.", e.getMessage(), out);
       return;
   }

   //
   // lookup oldest date in teepast
   //
   try {
       stmt = con.createStatement();
       rs = stmt.executeQuery("SELECT mm,dd,yy FROM teepast2 ORDER BY date ASC LIMIT 1");

       if (rs.next()) {
           oldest_mm = rs.getInt(1);
           oldest_dd = rs.getInt(2);
           oldest_yy = rs.getInt(3);
       }

   } catch (Exception e) {
       displayDatabaseErrMsg("Error looking up oldest teetime.", e.getMessage(), out);
       return;
   }


   // start ouput
   out.println(SystemUtils.HeadTitle("Proshop - Course Utilization Report"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
     
   SystemUtils.getProshopSubMenu(req, out, lottery);                // required to allow submenus on this page

   // set calendar vars
   Calendar cal_date = new GregorianCalendar();
   curr_year = cal_date.get(Calendar.YEAR);
   curr_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
   curr_day = cal_date.get(Calendar.DAY_OF_MONTH);
   
   //  Use today for both calendars if the last used dates were not returned
   if (syear == 0) {
    
      syear = curr_year;
      eyear = curr_year;
      smonth = curr_month;
      emonth = curr_month;
   }

   cal_date.add(Calendar.YEAR, 1); // add a year
   int furthest_year = cal_date.get(Calendar.YEAR);
   int furthest_month = cal_date.get(Calendar.MONTH) + 1;
   int furthest_day = cal_date.get(Calendar.DAY_OF_MONTH);
   
   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   // start main table for this page
   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");

   // output instructions
   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\"><b>Course Utilization Report</b></font><br>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<br>This report will display the utilization of a course for each day of the week and selected time periods.<br>");
   out.println("<br>Select the report options below, then click on <b>Go</b> to generate the report. ");
   out.println("<br>Dates choosen must both be old (before today) or both be current (today or after). " +
               "<br>You cannot have the starting date before today with an ending date of today or later.</font></td></tr>");
   out.println("</table><br>");

   if (club.equals("mpccpb")) {
       out.println("<br><table align=\"center\" border=\"0\"><tr><td align=\"center\"><button class=\"btnNorm\" onclick=\"location.href='Proshop_report_custom_course_utilization'\">Walk In Availability Report</button></td></tr></table>");
   }

   out.println("<br><b>1. Select a date range for the report period:</b><br><br>");

   // start submission form
   out.println("<form action=\"Proshop_report_course_utilization\" method=\"post\">");

   // output table that hold calendars and their related text boxes
   out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println(" <input type=text name=cal_box_0 id=cal_box_0 value=\"" +sdate+ "\">");
    out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
    out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println(" <input type=text name=cal_box_1 id=cal_box_1 value=\"" +edate+ "\">");
   out.println("</td>\n</tr></table>\n");



   out.println("<table border=0>");

   out.println(spacer_row);
   out.println(spacer_row);

   // report Days of Week options
   out.println("<tr valign=\"top\"><td nowrap><b>2. Days of Week to Report:</b></td>");
   out.println("<td>&nbsp;&nbsp;&nbsp;</td>");
   
   out.println("<td>");
   out.println("<table border=0 cellpadding=0 cellspacing=0><tr>");
   
   out.println("<td><input type=\"checkbox\" " + ((mon.equals("1")) ? "checked" : "") + " name=\"mon\" value=\"1\" id=\"mon\"><label for=\"mon\">" + spacer_label + "Monday</label></td>");
     out.println("<td>" + spacer_item + "</td>");
   out.println("<td><input type=\"checkbox\" " + ((tue.equals("1")) ? "checked" : "") + " name=\"tue\" value=\"1\" id=\"tue\"><label for=\"tue\">" + spacer_label + "Tuesday</label></td>");
     out.println("<td>" + spacer_item + "</td>");
   out.println("<td><input type=\"checkbox\" " + ((wed.equals("1")) ? "checked" : "") + " name=\"wed\" value=\"1\" id=\"wed\"><label for=\"wed\">" + spacer_label + "Wednesday</label></td></tr>");
     out.println("<tr><td><img src=\""+rev+"/images/shim.gif\" width=\"1\" height=\"3\" border=\"0\"></td></tr>");
   out.println("<tr><td><input type=\"checkbox\" " + ((thu.equals("1")) ? "checked" : "") + " name=\"thu\" value=\"1\" id=\"thu\"><label for=\"thu\">" + spacer_label + "Thursday</label></td>");
     out.println("<td>" + spacer_item + "</td>");
   out.println("<td><input type=\"checkbox\" " + ((fri.equals("1")) ? "checked" : "") + " name=\"fri\" value=\"1\" id=\"fri\"><label for=\"fri\">" + spacer_label + "Friday</label></td>");
     out.println("<td>" + spacer_item + "</td>");
   out.println("<td><input type=\"checkbox\" " + ((sat.equals("1")) ? "checked" : "") + " name=\"sat\" value=\"1\" id=\"sat\"><label for=\"sat\">" + spacer_label + "Saturday</label></td>");
     out.println("<td>" + spacer_item + "</td>");
   out.println("<td><input type=\"checkbox\" " + ((sun.equals("1")) ? "checked" : "") + " name=\"sun\" value=\"1\" id=\"sun\"><label for=\"sun\">" + spacer_label + "Sunday</label></td>");
   out.println("</tr></table>");

   out.println("</td></tr>");

   out.println(spacer_row);



   // report time interval options
   out.println("<tr>");
   out.println("<td><b>3. Display Counts For:</b></td><td></td>");
   out.println("<td>");

     out.println("<input " + ((interval.equals("") || interval.equals("day")) ? "checked" : "") + " " +
             "type=\"radio\" name=\"report_interval\" value=\"day\" id=\"day\"><label for=\"day\">" + spacer_label + "Entire Day</label>");
     out.println(spacer_item);
     out.println("<input " + ((interval.equals("hour")) ? "checked" : "") + " " +
             "type=\"radio\" name=\"report_interval\" value=\"hour\" id=\"hour\"><label for=\"hour\">" + spacer_label + "Each Hour of Day</label>");
     out.println(spacer_item);
     out.println("<input " + ((interval.equals("teetimes")) ? "checked" : "") + " " +
             "type=\"radio\" name=\"report_interval\" value=\"teetimes\" id=\"teetimes\"><label for=\"teetimes\">" + spacer_label + "Each Tee Time</label>");
     
   out.println("</td></tr>");

   out.println(spacer_row);


   // event time option
   out.println("<tr>");
   out.println("<td><b>4. Include Event Times:</b></td><td></td>");
   out.println("<td>");
    out.println("<input " + ((events.equals( "" ) || events.equals( "no" )) ? "checked" : "") + " type=\"radio\" name=\"report_events\" value=\"no\" id=\"events_no\"><label for=\"events_no\">" + spacer_label + "No</label>");
    out.println(spacer_item);
    out.println("<input " + ((events.equals( "yes" )) ? "checked" : "") + " type=\"radio\" name=\"report_events\" value=\"yes\" id=\"events_yes\"><label for=\"events_yes\">" + spacer_label + "Yes</label>");
   out.println("</td>");
   out.println("</tr>");

   out.println(spacer_row);


   // report data options
   out.println("<tr>");
   out.println("<td><b>5. Report Data:</b></td><td></td>");
   out.println("<td>");
    out.println("<input " + ((data.equals( "" ) || data.equals( "general" )) ? "checked" : "") + " type=\"radio\" name=\"report_data\" value=\"general\" id=\"rd_general\"><label for=\"rd_general\">" + spacer_label + "General Utilization</label>");
    out.println(spacer_item);
    out.println("<input " + ((data.equals( "mtype" )) ? "checked" : "") + " type=\"radio\" name=\"report_data\" value=\"mtype\" id=\"rd_mtype\"><label for=\"rd_mtype\">" + spacer_label + "By Member Type</label>");
   
    out.println(spacer_item);
    out.println("<input " + ((data.equals( "mship" )) ? "checked" : "") + " type=\"radio\" name=\"report_data\" value=\"mship\" id=\"rd_mship\"><label for=\"rd_mship\">" + spacer_label + "By Membership Type</label>");
   
    out.println(spacer_item);
    out.println("<input " + ((data.equals( "guest" )) ? "checked" : "") + " type=\"radio\" name=\"report_data\" value=\"guest\" id=\"rd_guest\"><label for=\"rd_guest\">" + spacer_label + "By Guest Type</label>");
   
    out.println(spacer_item);
    out.println("<input " + ((data.equals( "modes" )) ? "checked" : "") + " type=\"radio\" name=\"report_data\" value=\"modes\" id=\"rd_modes\"><label for=\"rd_modes\">" + spacer_label + "By Mode of Transportation</label>");
    
    out.println("</td>");
   out.println("</tr>");

   out.println(spacer_row);


   // report format options
   out.println("<tr>");
   out.println("<td><b>6. Report Format:</b></td><td></td>");
   out.println("<td>");
    out.println("<input " + ((format.equals( "" ) || format.equals( "web" )) ? "checked" : "") + " type=\"radio\" name=\"report_format\" value=\"web\" id=\"rf_web\"><label for=\"rf_web\">" + spacer_label + "Web Page</label>");
    out.println(spacer_item);
    out.println("<input " + ((events.equals( "yes" )) ? "checked" : "") + " type=\"radio\" name=\"report_format\" value=\"excelOpt\" id=\"rf_xcel\"><label for=\"rf_xcel\">" + spacer_label + "Excel File</label>");
   out.println("</td>");
   out.println("</tr>");

   out.println(spacer_row);


   if (multi != 0) {           // if multiple courses supported for this club

      // report course options
      out.println("<tr>");
      out.println("<td><b>7. Course to Report:</b></td><td></td>");
      out.println("<td>");
        out.println("<select size=\"1\" name=\"report_course\">");

        for (i=0; i<course.size(); i++) {

           out.println("<option " + ((courseR.equals( course.get(i) )) ? "checked" : "") + " value=\"" + course.get(i) + "\">" + course.get(i) + "</option>");

        }
        out.println("</select>");
      out.println("</td>");
      out.println("</tr>");

      out.println(spacer_row);
      
   } else {

      out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +courseName+ "\">");
   }


   out.println("</table>");


   //
   //  Include the last used parms for the next page if they were returned on the 'Return' from the last report
   //
   if (req.getParameter("tTimesAvail") != null) {
      out.println("<input type=\"hidden\" name=\"tTimesAvail\" value=\"yes\">");
   }
   if (req.getParameter("tTimesUsed") != null) {
      out.println("<input type=\"hidden\" name=\"tTimesUsed\" value=\"yes\">");
   }
   if (req.getParameter("tTimesUnused") != null) {
      out.println("<input type=\"hidden\" name=\"tTimesUnused\" value=\"yes\">");
   }
   if (req.getParameter("tTimesFull") != null) {
      out.println("<input type=\"hidden\" name=\"tTimesFull\" value=\"yes\">");
   }
   if (req.getParameter("t3players") != null) {
      out.println("<input type=\"hidden\" name=\"t3players\" value=\"yes\">");
   }
   if (req.getParameter("t2players") != null) {
      out.println("<input type=\"hidden\" name=\"t2players\" value=\"yes\">");
   }
   if (req.getParameter("t1player") != null) {
      out.println("<input type=\"hidden\" name=\"t1player\" value=\"yes\">");
   }
   if (req.getParameter("eTimesUsed") != null) {
      out.println("<input type=\"hidden\" name=\"eTimesUsed\" value=\"yes\">");
   }
   if (req.getParameter("eTimesUnused") != null) {
      out.println("<input type=\"hidden\" name=\"eTimesUnused\" value=\"yes\">");
   }
   if (req.getParameter("slotsAvail") != null) {
      out.println("<input type=\"hidden\" name=\"slotsAvail\" value=\"yes\">");
   }
   if (req.getParameter("slotsUsed") != null) {
      out.println("<input type=\"hidden\" name=\"slotsUsed\" value=\"yes\">");
   }
   if (req.getParameter("slotsUnused") != null) {
      out.println("<input type=\"hidden\" name=\"slotsUnused\" value=\"yes\">");
   }
   if (req.getParameter("memRounds") != null) {
      out.println("<input type=\"hidden\" name=\"memRounds\" value=\"yes\">");
   }
   if (req.getParameter("gstRounds") != null) {
      out.println("<input type=\"hidden\" name=\"gstRounds\" value=\"yes\">");
   }
      
   

   // report button (go)
   out.println("<p align=\"center\"><input type=\"submit\" value=\"  Go  \" onclick=\"return checkDates();\"></p>");

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

    out.println("var sdate = 0;");
    out.println("var edate = 0;");
    out.println("var today = " + Utilities.getDate(con) + ";");

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

    out.println("var g_hilite_curr_day = new Boolean(true);");
    out.println("g_current_day = " + curr_day + ";");
    out.println("g_current_month = " + curr_month + ";");
    out.println("g_current_year = " + curr_year + ";");

    // set calendar date parts in js
    out.println("g_cal_month[0] = " + smonth + ";");
    out.println("g_cal_year[0] = " + syear + ";");
    out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
    out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
    out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
    out.println("g_cal_ending_month[0] = " + furthest_month + ";");
    out.println("g_cal_ending_day[0] = " + furthest_day + ";");
    out.println("g_cal_ending_year[0] = " + furthest_year + ";");

    out.println("g_cal_month[1] = " + emonth + ";");
    out.println("g_cal_year[1] = " + eyear + ";");
    out.println("g_cal_beginning_month[1] = " + oldest_mm + ";");
    out.println("g_cal_beginning_year[1] = " + oldest_yy + ";");
    out.println("g_cal_beginning_day[1] = " + oldest_dd + ";");
    out.println("g_cal_ending_month[1] = " + furthest_month + ";");
    out.println("g_cal_ending_day[1] = " + furthest_day + ";");
    out.println("g_cal_ending_year[1] = " + furthest_year + ";");

    out.println("function sd(pCal, pMonth, pDay, pYear) {");
    out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
    out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
    out.println(" if (pCal == 0) {");
    out.println("  sdate=(pYear * 10000) + (pMonth * 100) + (pDay * 1);");
    out.println(" } else {");
    out.println("  edate=(pYear * 10000) + (pMonth * 100) + (pDay * 1);");
    out.println(" }");
    out.println("}");

    out.println("function checkDates() {");

    out.println(" if (sdate == 0) {");
    out.println("  dp=document.getElementById('cal_box_0').value.split('-', 3);");
    out.println("  sdate = parseInt(dp[0] * 10000) + parseInt(dp[1] * 100) + parseInt(dp[2]);");
    out.println(" }");
    
    out.println(" if (edate == 0) {");
    out.println("  dp=document.getElementById('cal_box_1').value.split('-', 3);");
    out.println("  edate = parseInt(dp[0] * 10000) + parseInt(dp[1] * 100) + parseInt(dp[2]);");
    out.println(" }");

    out.println(" if ( (sdate < today && edate < today) || (sdate >= today && edate >= today) ) {");
    out.println("  if ( sdate <= edate ) {");
    out.println("   return true;");
    out.println("  } else {");
    out.println("   alert('Invalid Dates\\n\\nYour starting date is after your end date.');");
    out.println("   return false;");
    out.println("  }");
    out.println(" } else {");
    out.println("  alert('Invalid Dates\\n\\nDates choosen must both be old (before today) or both be current (today or after).');");
    out.println("  return false;");
    out.println(" }");
    out.println("}");


   out.println("</script>");

   out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>");

   out.println("</center></font>");
   out.println("</body></html>");
   out.close();                            // wait for 'Go'

 } // end of doGet routine
 
 
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

   Connection con = SystemUtils.getCon(session);                   // get DB connection
   if (con == null) {
       displayDatabaseErrMsg("Can not establish connection.", "", out);
       return;
   }

   String club = (String)session.getAttribute("club");   
   String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean error = false; 
   boolean parmsReturned = false; 
     
   String format = "";
   String data = "";
   String events = "no";
   String interval = "";
   String course = "";
   String temp = "";
   String errorMsg = "";

   int i = 0;
   int count = 0;
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
     
   format = (req.getParameter("report_format") != null) ? req.getParameter("report_format")  : "";

   events = (req.getParameter("report_events") != null) ? req.getParameter("report_events")  : "";

   interval = (req.getParameter("report_interval") != null) ? req.getParameter("report_interval")  : "";

   data = (req.getParameter("report_data") != null) ? req.getParameter("report_data")  : "";

   course = (req.getParameter("report_course") != null) ? req.getParameter("report_course")  : "";

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
       displayInputErrMsg(req, "Error processing the specified date range.", out);
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
       displayInputErrMsg(req, "Invalid date range. The start date must be earlier than the end date.", out);
       return;
   }

   //  verify that at least one day was requested
   if (mon == 0 && tue == 0 && wed == 0 && thu == 0 && fri == 0 && sat == 0 && sun == 0) {
       displayInputErrMsg(req, "You must select at least one day of the week.", out);
       return;
   }

   //  verify that the interval was requested
   if (interval.equals( "" )) {
       displayInputErrMsg(req, "You must select an interval (Display counts for:).", out);
       return;
   }

   //  verify that the format was requested
   if (format.equals( "" )) {
       displayInputErrMsg(req, "You must select a Report Format.", out);
       return;
   }


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
   parm.course = course;
   parm.colCount = 1;           // start with 1 for the time column


   //
   //  get the Column options selected (columns to be displayed)
   //
   if (req.getParameter("tTimesAvail") != null) {
      parm.tTimesAvail = true;
      parm.colCount++;                    // count the columns required
      parmsReturned = true; 
   }
   if (req.getParameter("tTimesUsed") != null) {
      parm.tTimesUsed = true;
      parm.colCount++;
      if (format.equals("excel")) { 
         parm.colCount++;                 // seperate % col in excel
      }
      parmsReturned = true; 
   }
   if (req.getParameter("tTimesUnused") != null) {
      parm.tTimesUnused = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("tTimesFull") != null) {
      parm.tTimesFull = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("t3players") != null) {
      parm.t3players = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("t2players") != null) {
      parm.t2players = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("t1player") != null) {
      parm.t1player = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("eTimesUsed") != null) {
      parm.eTimesUsed = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("eTimesUnused") != null) {
      parm.eTimesUnused = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("slotsAvail") != null) {
      parm.slotsAvail = true;
      parm.colCount++;
      parmsReturned = true; 
   }
   if (req.getParameter("slotsUsed") != null) {
      parm.slotsUsed = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("slotsUnused") != null) {
      parm.slotsUnused = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("memRounds") != null) {
      parm.memRounds = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }
   if (req.getParameter("gstRounds") != null) {
      parm.gstRounds = true;
      parm.colCount++;
      if (format.equals("excel")) {
         parm.colCount++;
      }
      parmsReturned = true; 
   }


   //
   //   First, check if column display options selected - if not, open a new window
   //
   if (req.getParameter("continue") == null && !format.equals("excel")) {       // if first time here & not excel (2nd req)

      // start ouput
      out.println(SystemUtils.HeadTitle("Proshop - Rounds Played Report"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

      SystemUtils.getProshopSubMenu(req, out, lottery);                // required to allow submenus on this page

      out.println("<CENTER><BR>");
      // output instructions
      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"3\"><b>Course Utilization Report - Column Options<br>");
      if (data.equals( "general" )) {
         out.println("(General Utilization)");
      } else {
         if (data.equals( "mtype" )) {
            out.println("(By Member Type)");
         } else {
            if (data.equals( "mship" )) {
               out.println("(By Membership Type)");
            } else {
               if (data.equals( "modes" )) {
                  out.println("(By Mode of Transportation)");
               } else {
                  out.println("(By Guest Type)");
               }
            }
         }
      }
      out.println("</b></font><font size=\"2\">");
      out.println("<BR><BR>Please check or uncheck the desired report columns to determine the data to be displayed.");
      out.println("<BR>Some columns have been pre-checked based on your previous options selected.");
      if (data.equals( "mtype" )) {
         out.println("<BR><BR>In addition to those selected below, there will be <b>one column for each Member Type</b>.");
      } else {
         if (data.equals( "mship" )) {
            out.println("<BR><BR>In addition to those selected below, there will be <b>one column for each Membership Type</b>.");
         } else {
            if (data.equals( "modes" )) {
               out.println("<BR><BR>In addition to those selected below, there will be <b>one column for each Mode of Transportation</b>.");
            } else {
               if (data.equals( "guest" )) {
                  out.println("<BR><BR>In addition to those selected below, there will be <b>one column for each Guest Type</b>.");
               }
            }
         }
      }
      out.println("<BR><BR>Select Continue to display the report or Return to go back.");
      out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<BR><BR><b>Columns To Display</b><BR>");

      //out.println("<font size=\"2\">");

      out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
      out.println("<tr>");
      if (format.equals("excelOpt")) {     // if user requested Excel Option - new page
         out.println("<form action=\"Proshop_report_course_utilization\" method=\"post\" target=\"_blank\">");
      } else {
         out.println("<form action=\"Proshop_report_course_utilization\" method=\"post\">");
      }
      out.println("<td align=\"left\">");
        out.println("<input " + ((parmsReturned == false || parm.tTimesAvail == true) ? "checked" : "") + " type=\"checkbox\" name=\"tTimesAvail\" value=\"1\" " +
                    "id=\"tTimesAvail\">" + spacer_label + "<label for=\"tTimesAvail\">Tee Times Available</label>");
      out.println("</td>");
      
      out.println("<td align=\"left\">");
        out.println("<input " + ((parmsReturned == false || parm.tTimesUsed == true) ? "checked" : "") + " type=\"checkbox\" name=\"tTimesUsed\" value=\"1\" " +
                    "id=\"tTimesUsed\">" + spacer_label + "<label for=\"tTimesUsed\">Tee Times Utilized</label>");
      out.println("</td>");
      
      out.println("<td align=\"left\">"); 
         out.println("<input " + ((parmsReturned == false || parm.tTimesUnused == true) ? "checked" : "") + " type=\"checkbox\" name=\"tTimesUnused\" value=\"1\" " +
                     "id=\"tTimesUnused\">" + spacer_label + "<label for=\"tTimesUnused\">Tee Times Unused</label>");
      out.println("</td>");
      
      out.println("<td align=\"left\">&nbsp;</td>");
      
      out.println("</tr>");

      out.println("<tr><td align=\"left\">");
         out.println("<input " + ((parm.tTimesFull == true) ? "checked" : "") + " type=\"checkbox\" name=\"tTimesFull\" value=\"1\" " +
                     "id=\"tTimesFull\">" + spacer_label + "<label for=\"tTimesFull\">Full Tee Times</label>");
      out.println("</td>");
      
      out.println("<td align=\"left\">");
         out.println("<input " + ((parm.t3players == true) ? "checked" : "") + " type=\"checkbox\" name=\"t3players\" value=\"1\" " +
                     "id=\"t3players\">" + spacer_label + "<label for=\"t3players\">Times With 3 Players</label>");
      out.println("</td>");
      
      out.println("<td align=\"left\">");
         out.println("<input " + ((parm.t2players == true) ? "checked" : "") + " type=\"checkbox\" name=\"t2players\" value=\"1\" " +
                     "id=\"t2players\">" + spacer_label + "<label for=\"t2players\">Times With 2 Players</label>");
      out.println("</td>");
      
      out.println("<td align=\"left\">");
         out.println("<input " + ((parm.t1player == true) ? "checked" : "") + " type=\"checkbox\" name=\"t1players\" value=\"1\" " +
                     "id=\"t1players\">" + spacer_label + "<label for=\"t1players\">Times With 1 Players</label>");
      out.println("</td>");
      
      out.println("</tr>");

      if (events.equals( "yes" )) {          // if event times to be included
        
         out.println("<tr><td align=\"left\">");
            out.println("<input " + ((parmsReturned == false || parm.eTimesUsed == true) ? "checked" : "") + " type=\"checkbox\" name=\"eTimesUsed\" value=\"1\" " +
                        "id=\"eTimesUsed\">" + spacer_label + "<label for=\"eTimesUsed\">Event Times Utilized</label>");
         out.println("</td>");
         
         out.println("<td align=\"left\">");
            out.println("<input " + ((parmsReturned == false || parm.eTimesUnused == true) ? "checked" : "") + " type=\"checkbox\" name=\"eTimesUnused\" value=\"1\" " +
                        "id=\"eTimesUnused\">" + spacer_label + "<label for=\"eTimesUnused\">Event Times Unused</label>");
         out.println("</td>");
         
         out.println("<td align=\"left\">&nbsp;</td>");
         out.println("<td align=\"left\">&nbsp;</td>");
        out.println("</tr>");

      }

      out.println("<tr><td align=\"left\">");
            out.println("<input " + ((parmsReturned == false || parm.slotsAvail == true) ? "checked" : "") + " type=\"checkbox\" name=\"slotsAvail\" value=\"1\" " +
                        "id=\"slotsAvail\">" + spacer_label + "<label for=\"slotsAvail\">Player Slots Available&nbsp</label>");
      out.println("</td>");

      out.println("<td align=\"left\">");
            out.println("<input " + ((parmsReturned == false || parm.slotsUsed == true) ? "checked" : "") + " type=\"checkbox\" name=\"slotsUsed\" value=\"1\" " +
                        "id=\"slotsUsed\">" + spacer_label + "<label for=\"slotsUsed\">Player Slots Utilized</label>");
      out.println("</td>");

      out.println("<td align=\"left\">");
            out.println("<input " + ((parmsReturned == false || parm.slotsUnused == true) ? "checked" : "") + " type=\"checkbox\" name=\"slotsUnused\" value=\"1\" " +
                        "id=\"slotsUnused\">" + spacer_label + "<label for=\"slotsUnused\">Player Slots Unused</label>");
      out.println("</td>");

      out.println("<td align=\"left\">&nbsp;</td>");

      out.println("</tr>");

      out.println("<tr><td align=\"left\">");
      if (parmsReturned == false) {

          out.println("<input " + ((data.equals( "general" ) || data.equals( "guest" )) ? "checked" : "") + " type=\"checkbox\" name=\"memRounds\" value=\"1\" " +
                      "id=\"memRounds\">" + spacer_label + "<label for=\"memRounds\">Number of Member Rounds</label>");
      } else {

          out.println("<input " + ((parm.memRounds == true) ? "checked" : "") + " type=\"checkbox\" name=\"memRounds\" value=\"1\" " +
                      "id=\"memRounds\">" + spacer_label + "<label for=\"memRounds\">Number of Member Rounds</label>");
      }
      out.println("</td>");

      out.println("<td align=\"left\">");
      if (parmsReturned == false) {

          out.println("<input " + ((data.equals( "guest" ) || data.equals( "modes" )) ? "checked" : "") + " type=\"checkbox\" name=\"gstRounds\" value=\"1\" " +
                      "id=\"gstRounds\">" + spacer_label + "<label for=\"gstRounds\">Number of Guest Rounds</label>");
      } else {

          out.println("<input " + ((parm.gstRounds == true) ? "checked" : "") + " type=\"checkbox\" name=\"gstRounds\" value=\"1\" " +
                      "id=\"gstRounds\">" + spacer_label + "<label for=\"gstRounds\">Number of Guest Rounds</label>");
      }
      out.println("</td>");

      out.println("<td align=\"left\">&nbsp;</td>");
      out.println("<td align=\"left\">&nbsp;</td>");

      if (format.equals("excelOpt")) {     // if user requested Excel Option
         out.println("<input type=\"hidden\" name=\"report_format\" value=\"excel\">");
      } else {
         out.println("<input type=\"hidden\" name=\"report_format\" value=\"web\">");
      }
      out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" +start_date+ "\">");
      out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" +end_date+ "\">");
      out.println("<input type=\"hidden\" name=\"report_interval\" value=\"" +interval+ "\">");
      out.println("<input type=\"hidden\" name=\"report_data\" value=\"" +data+ "\">");
      out.println("<input type=\"hidden\" name=\"report_events\" value=\"" +events+ "\">");
      out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"mon\" value=\"" +mon+ "\">");
      out.println("<input type=\"hidden\" name=\"tue\" value=\"" +tue+ "\">");
      out.println("<input type=\"hidden\" name=\"wed\" value=\"" +wed+ "\">");
      out.println("<input type=\"hidden\" name=\"thu\" value=\"" +thu+ "\">");
      out.println("<input type=\"hidden\" name=\"fri\" value=\"" +fri+ "\">");
      out.println("<input type=\"hidden\" name=\"sat\" value=\"" +sat+ "\">");
      out.println("<input type=\"hidden\" name=\"sun\" value=\"" +sun+ "\">");
        
      out.println("</tr></table>");
      out.println("<BR><BR>");  
      out.println("<input type=\"submit\" value=\"Continue\" name=\"continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      
      out.println("<BR><BR><form action=\"Proshop_report_course_utilization\" method=\"get\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" +start_date+ "\">");
      out.println("<input type=\"hidden\" name=\"edate\" value=\"" +end_date+ "\">");
      out.println("<input type=\"hidden\" name=\"syear\" value=\"" +start_year+ "\">");
      out.println("<input type=\"hidden\" name=\"eyear\" value=\"" +end_year+ "\">");
      out.println("<input type=\"hidden\" name=\"smonth\" value=\"" +start_month+ "\">");
      out.println("<input type=\"hidden\" name=\"emonth\" value=\"" +end_month+ "\">");
      out.println("<input type=\"hidden\" name=\"report_interval\" value=\"" +interval+ "\">");
      out.println("<input type=\"hidden\" name=\"report_format\" value=\"" +format+ "\">");
      out.println("<input type=\"hidden\" name=\"report_data\" value=\"" +data+ "\">");
      out.println("<input type=\"hidden\" name=\"report_events\" value=\"" +events+ "\">");
      out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"mon\" value=\"" +mon+ "\">");
      out.println("<input type=\"hidden\" name=\"tue\" value=\"" +tue+ "\">");
      out.println("<input type=\"hidden\" name=\"wed\" value=\"" +wed+ "\">");
      out.println("<input type=\"hidden\" name=\"thu\" value=\"" +thu+ "\">");
      out.println("<input type=\"hidden\" name=\"fri\" value=\"" +fri+ "\">");
      out.println("<input type=\"hidden\" name=\"sat\" value=\"" +sat+ "\">");
      out.println("<input type=\"hidden\" name=\"sun\" value=\"" +sun+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\"></form>");      
      
      out.println("</font></CENTER></BODY></HTML>");
      out.close();
      return;

   } // end second page of report options



   //******************************************************************
   //  Build the report
   //******************************************************************

   //
   //  Finish counting the columns required for this report
   //
   parmClub parmc = new parmClub(0, con);           // golf only report
   parmCourse parmcs = new parmCourse();            // allocate a parm block for Course Parms

   //
   //   Get the mtypes, mships and guest types for this club
   //
   if (!parm.data.equals( "general" )) {          // if we need the club info

      try {

         getClub.getParms(con, parmc);        // get the club parms
      }
      catch (Exception ignore) {
      }

      //
      //   get the number of configured types based on the type of display requested
      //
      if (parm.data.equals( "modes" )) {          // if data type = by modes of trans

         try {

            getParms.getTmodes(con, parmcs, course);        // get the course parms (modes of trans)
         }
         catch (Exception ignore) {
         }
         
         tmloop1:
         for (i=0; i<parmcs.tmode_limit; i++) {         // check all modes

            if (!parmcs.tmodea[i].equals( "" )) {

               parm.colCount++;
               if (format.equals("excel")) {
                  parm.colCount++;
               }
            } else {
               break tmloop1;                       // done
            }
         }         
      }   
         
      if (parm.data.equals( "mtype" )) {          // if data type = by member type

         tloop1:
         for (i=0; i<parmc.MAX_Mems; i++) {         // check all member types

            if (!parmc.mem[i].equals( "" )) {

               parm.colCount++;
               if (format.equals("excel")) {
                  parm.colCount++;
               }
            } else {
               break tloop1;                       // done
            }
         }
      }

      if (parm.data.equals( "mship" )) {          // if data type = by membership type

         tloop2:
         for (i=0; i<parmc.MAX_Mships; i++) {         // check all member types

            if (!parmc.mship[i].equals( "" )) {

               parm.colCount++;
               if (format.equals("excel")) {
                  parm.colCount++;
               }
            } else {
               break tloop2;                       // done
            }
         }
      }

      if (parm.data.equals( "guest" )) {          // if data type = by guest type

         tloop3:
         for (i=0; i<parmc.MAX_Guests; i++) {         // check all guest types

            if (!parmc.guest[i].equals( "" )) {

               parm.colCount++;
               if (format.equals("excel")) {
                  parm.colCount++;
               }
            } else {
               break tloop3;                       // done
            }
         }
         
         parm.colCount++;                  // add 1 more col for Other guest types (old guest types)
      }
   }


   //
   //  build the report
   //
   if (!format.equals("excel")) { out.println(SystemUtils.HeadTitle("Proshop - Course Utilization Report")); }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!format.equals("excel")) { SystemUtils.getProshopSubMenu(req, out, lottery); }               // required to allow submenus on this page

   // start report output
   out.println("<table border=\"0\" align=\"center\">");    // table for whole page
   out.println("<tr>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
     
   out.println("<table border=\"1\" cellpadding=\"5\" align=\"center\" bgcolor=\"#F5F5DC\">");   // heading table
   out.println("<tr>");
   if (format.equals("excel")) {
      out.println("<td colspan=\"" +parm.colCount+ "\" align=\"center\">");
   } else {
      out.println("<td align=\"center\">");
   }
   out.println("<font size=\"3\">");
     
   if (data.equals("general")) {
      out.println("<p><b>Course Utilization Report (General Data)<br>");
   } else {
      if (data.equals("mtype")) {
         out.println("<p><b>Course Utilization Report (by Member Type)<br>");
      } else {
         if (data.equals("mship")) {
            out.println("<p><b>Course Utilization Report (by Membership Type)<br>");
         } else {
            if (data.equals("modes")) {
               out.println("<p><b>Course Utilization Report (by Mode of Transportation)<br>");
            } else {
               out.println("<p><b>Course Utilization Report (by Guest Type)<br>");
            }
         }
      }
   }

   out.println("for " + start_month + "/" + start_day + "/" + start_year + " to");
   out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b>");
   if (!course.equals( "" )) {
      out.println("<br><br>For Course: <b>" +course+ "</b>");
   }
   out.println("</font><font size=\"2\"><br><br><b>Notes:</b> Percentages are rounded down to whole number.<br>");
   out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Counts and percentages are based on 4 players per group (5-somes not considered).<br>");
     
   if (data.equals("general")) {
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Times and Slots Available include those that are not blocked and are not cross-overs.<br>");
   } else {
      if (data.equals("guest")) {
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Guest Type percentages are based on the number of Guest Rounds.<br>");
      } else {
         if (data.equals("mship")) {
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Membership Type percentages are based on the number of Member Rounds.<br>");
         } else {
            if (data.equals("modes")) {
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Mode of Trans percentages are based on the number of All Rounds.<br>");
            } else {
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Member Type percentages are based on the number of Member Rounds.<br>");
            }
         }
      }
   }
   out.println("<br>" +buildDisplayDateTime());
   out.println("</font></p></td>");
   if (format.equals("excel")) {            // force table across the page
      out.println("</tr><tr bgcolor=\"#FFFFFF\">");
      for (i=0; i<parm.colCount; i++) {
         out.println("<td>&nbsp;</td>");
      }
   }
   out.println("</tr></table");

   out.println("</td></tr>");
   out.println("<tr><td align=\"center\">");

   if (!format.equals("excel")) { 
      
      out.println("<BR><form action=\"Proshop_report_course_utilization\" method=\"get\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" +start_date+ "\">");
      out.println("<input type=\"hidden\" name=\"edate\" value=\"" +end_date+ "\">");
      out.println("<input type=\"hidden\" name=\"syear\" value=\"" +start_year+ "\">");
      out.println("<input type=\"hidden\" name=\"eyear\" value=\"" +end_year+ "\">");
      out.println("<input type=\"hidden\" name=\"smonth\" value=\"" +start_month+ "\">");
      out.println("<input type=\"hidden\" name=\"emonth\" value=\"" +end_month+ "\">");
      out.println("<input type=\"hidden\" name=\"report_interval\" value=\"" +interval+ "\">");
      out.println("<input type=\"hidden\" name=\"report_format\" value=\"" +format+ "\">");
      out.println("<input type=\"hidden\" name=\"report_data\" value=\"" +data+ "\">");
      out.println("<input type=\"hidden\" name=\"report_events\" value=\"" +events+ "\">");
      out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"mon\" value=\"" +mon+ "\">");
      out.println("<input type=\"hidden\" name=\"tue\" value=\"" +tue+ "\">");
      out.println("<input type=\"hidden\" name=\"wed\" value=\"" +wed+ "\">");
      out.println("<input type=\"hidden\" name=\"thu\" value=\"" +thu+ "\">");
      out.println("<input type=\"hidden\" name=\"fri\" value=\"" +fri+ "\">");
      out.println("<input type=\"hidden\" name=\"sat\" value=\"" +sat+ "\">");
      out.println("<input type=\"hidden\" name=\"sun\" value=\"" +sun+ "\">");
      if (parm.tTimesAvail == true) {
         out.println("<input type=\"hidden\" name=\"tTimesAvail\" value=\"yes\">");
      }
      if (parm.tTimesUsed == true) {
         out.println("<input type=\"hidden\" name=\"tTimesUsed\" value=\"yes\">");
      }
      if (parm.tTimesUnused == true) {
         out.println("<input type=\"hidden\" name=\"tTimesUnused\" value=\"yes\">");
      }
      if (parm.tTimesFull == true) {
         out.println("<input type=\"hidden\" name=\"tTimesFull\" value=\"yes\">");
      }
      if (parm.t3players == true) {
         out.println("<input type=\"hidden\" name=\"t3players\" value=\"yes\">");
      }
      if (parm.t2players == true) {
         out.println("<input type=\"hidden\" name=\"t2players\" value=\"yes\">");
      }
      if (parm.t1player == true) {
         out.println("<input type=\"hidden\" name=\"t1player\" value=\"yes\">");
      }
      if (parm.eTimesUsed == true) {
         out.println("<input type=\"hidden\" name=\"eTimesUsed\" value=\"yes\">");
      }
      if (parm.eTimesUnused == true) {
         out.println("<input type=\"hidden\" name=\"eTimesUnused\" value=\"yes\">");
      }
      if (parm.slotsAvail == true) {
         out.println("<input type=\"hidden\" name=\"slotsAvail\" value=\"yes\">");
      }
      if (parm.slotsUsed == true) {
         out.println("<input type=\"hidden\" name=\"slotsUsed\" value=\"yes\">");
      }
      if (parm.slotsUnused == true) {
         out.println("<input type=\"hidden\" name=\"slotsUnused\" value=\"yes\">");
      }
      if (parm.memRounds == true) {
         out.println("<input type=\"hidden\" name=\"memRounds\" value=\"yes\">");
      }
      if (parm.gstRounds == true) {
         out.println("<input type=\"hidden\" name=\"gstRounds\" value=\"yes\">");
      }
      
      out.println("<p align=\"center\"><input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");      
      
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<a href=\"Proshop_announce\">Home</a></p></form>");
   }

   if (mon > 0) {       // if Monday selected

      parm.day = "Monday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day
        
      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (tue > 0) {       // if Tuesday selected

      parm.day = "Tuesday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (wed > 0) {       // if Wednesday selected

      parm.day = "Wednesday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (thu > 0) {       // if Thursday selected

      parm.day = "Thursday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (fri > 0) {       // if Friday selected

      parm.day = "Friday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (sat > 0) {       // if Saturday selected

      parm.day = "Saturday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");

      resetStats(parm);                     // reset the counters
   }

   if (sun > 0) {       // if Sunday selected

      parm.day = "Sunday";

      //
      //  Gather the stats for this request
      //
      error = gatherStats(parm, out, con);        // gather all the counts and stats into parm

      if (error == true) {

         return;
      }

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");

      if (!format.equals("excel")) {

         buildDayTableWeb(parm, out, con);   // go build the table for this day

      } else {

         buildDayTableExcel(parm, out, con);   // go build the table for this day
      }

      out.println("<p>&nbsp;</p>");
      out.println("</td></tr>");
   }

   out.println("</td></tr></table>");         // end of main table

   if (!format.equals("excel")) {
      
      out.println("<BR><form action=\"Proshop_report_course_utilization\" method=\"get\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" +start_date+ "\">");
      out.println("<input type=\"hidden\" name=\"edate\" value=\"" +end_date+ "\">");
      out.println("<input type=\"hidden\" name=\"syear\" value=\"" +start_year+ "\">");
      out.println("<input type=\"hidden\" name=\"eyear\" value=\"" +end_year+ "\">");
      out.println("<input type=\"hidden\" name=\"smonth\" value=\"" +start_month+ "\">");
      out.println("<input type=\"hidden\" name=\"emonth\" value=\"" +end_month+ "\">");
      out.println("<input type=\"hidden\" name=\"report_interval\" value=\"" +interval+ "\">");
      out.println("<input type=\"hidden\" name=\"report_format\" value=\"" +format+ "\">");
      out.println("<input type=\"hidden\" name=\"report_data\" value=\"" +data+ "\">");
      out.println("<input type=\"hidden\" name=\"report_events\" value=\"" +events+ "\">");
      out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"mon\" value=\"" +mon+ "\">");
      out.println("<input type=\"hidden\" name=\"tue\" value=\"" +tue+ "\">");
      out.println("<input type=\"hidden\" name=\"wed\" value=\"" +wed+ "\">");
      out.println("<input type=\"hidden\" name=\"thu\" value=\"" +thu+ "\">");
      out.println("<input type=\"hidden\" name=\"fri\" value=\"" +fri+ "\">");
      out.println("<input type=\"hidden\" name=\"sat\" value=\"" +sat+ "\">");
      out.println("<input type=\"hidden\" name=\"sun\" value=\"" +sun+ "\">");
      if (parm.tTimesAvail == true) {
         out.println("<input type=\"hidden\" name=\"tTimesAvail\" value=\"yes\">");
      }
      if (parm.tTimesUsed == true) {
         out.println("<input type=\"hidden\" name=\"tTimesUsed\" value=\"yes\">");
      }
      if (parm.tTimesUnused == true) {
         out.println("<input type=\"hidden\" name=\"tTimesUnused\" value=\"yes\">");
      }
      if (parm.tTimesFull == true) {
         out.println("<input type=\"hidden\" name=\"tTimesFull\" value=\"yes\">");
      }
      if (parm.t3players == true) {
         out.println("<input type=\"hidden\" name=\"t3players\" value=\"yes\">");
      }
      if (parm.t2players == true) {
         out.println("<input type=\"hidden\" name=\"t2players\" value=\"yes\">");
      }
      if (parm.t1player == true) {
         out.println("<input type=\"hidden\" name=\"t1player\" value=\"yes\">");
      }
      if (parm.eTimesUsed == true) {
         out.println("<input type=\"hidden\" name=\"eTimesUsed\" value=\"yes\">");
      }
      if (parm.eTimesUnused == true) {
         out.println("<input type=\"hidden\" name=\"eTimesUnused\" value=\"yes\">");
      }
      if (parm.slotsAvail == true) {
         out.println("<input type=\"hidden\" name=\"slotsAvail\" value=\"yes\">");
      }
      if (parm.slotsUsed == true) {
         out.println("<input type=\"hidden\" name=\"slotsUsed\" value=\"yes\">");
      }
      if (parm.slotsUnused == true) {
         out.println("<input type=\"hidden\" name=\"slotsUnused\" value=\"yes\">");
      }
      if (parm.memRounds == true) {
         out.println("<input type=\"hidden\" name=\"memRounds\" value=\"yes\">");
      }
      if (parm.gstRounds == true) {
         out.println("<input type=\"hidden\" name=\"gstRounds\" value=\"yes\">");
      }
      
      out.println("<p align=\"center\"><input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");      
      
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<a href=\"Proshop_announce\">Home</a></p></form>");

      out.println("</center></font>");
      out.println("</body></html>");
   }
   out.close();                            // wait for 'Go'
    
 } // end doPost
 
 
 //**************************************************
 // Common Method to reset the stats 
 //**************************************************
 //
 private void resetStats(parmUtilization parm) {


   for (int i=0; i<parm.max; i++) {
     
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
     
 }


 //**************************************************
 // Common Method to gather stats for each day
 //**************************************************
 //
 private boolean gatherStats(parmUtilization parm, PrintWriter out, Connection con) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   String day = parm.day;
   String course = parm.course;
   String interval = parm.interval;
   String event = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";

   int today = (int)Utilities.getDate(con);

   String show_clause = showClauseByDate(parm.sdate, today);
   String table = tableByDate(parm.sdate, today);
   boolean teepast = (parm.sdate < today);

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int count = 0;
   int loopCount = 0;
   int time = 0;
   int hour = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int max = parm.max;

   boolean error = false;
   boolean includeEvents = false;
     
   if (parm.events.equals( "yes" )) {          // if we are to include event times
     
      includeEvents = true;
   }

   //
   //  temp arrays to hold the data before moving to parm
   //
   int [] timesTeePast = new int [max];       // tee times and counts from teepast2
   int [] timesA = new int [max];                 // tee times available counts
   int [] timesU = new int [max];                 // tee times utilized counts
   int [] slotsU = new int [max];                 // slots utilized counts
   int [] eventsU = new int [max];                // event times utilized
   int [] singles = new int [max];                // times with only one player
   int [] doubles = new int [max];                //          2 players
   int [] triples = new int [max];                //          3 players
   int [] fulls = new int [max];                  //          at least 4 players
   int [] members = new int [max];                // # of members
   int [] guests = new int [max];                 // # of guests
   int [] eTimes = new int [max];                 // # of empty times (no shows)
   int [] eEtimes = new int [max];                 // # of empty event times (no shows)

   int [] timesEmpty = new int [max];         // tee times and counts from teepastempty
   int [] eTimesA = new int [max];                 // # of empty tee times available
   int [] eEvents = new int [max];                 // # of empty event times


   //
   //   First we must get all the tee times for this day (empty and used) and place them in order from earliest to latest
   //
   try {

      //
      //  Get all the empty times for this day, course and date range
      //
      if (includeEvents == true) {
        
         pstmt1 = con.prepareStatement (
            "SELECT hr, time, event FROM teepastempty " +
            "WHERE date >= ? AND date <= ? AND day = ? AND courseName = ? " +
            "ORDER BY time");

      } else {       // do not include event times

         pstmt1 = con.prepareStatement (
            "SELECT hr, time, event FROM teepastempty " +
            "WHERE date >= ? AND date <= ? AND day = ? AND event = '' AND courseName = ? " +
            "ORDER BY time");
      }

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, parm.sdate);
      pstmt1.setLong(2, parm.edate);
      pstmt1.setString(3, day);
      pstmt1.setString(4, course);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         hour = rs.getInt("hr");
         time = rs.getInt("time");
         event = rs.getString("event");

         if (!interval.equals( "day" )) {              // if tee times or hour (day saves all counts in first entry only)

            if (interval.equals( "teetimes" )) {       // if tee times

               if (i == 0 && timesEmpty[i] == 0) {     // if first time here

                  timesEmpty[i] = time;                // save the time value

               } else {                                

                  if (time != timesEmpty[i]) {         // if new time value

                     i++;                              // bump to next position
                     timesEmpty[i] = time;             // save the time value
                  }
               }
                    
            } else {                                   // by hour
  
               if (i == 0 && timesEmpty[i] == 0) {     // if first time here

                  timesEmpty[i] = hour;                // save the hour value

               } else {

                  if (hour != timesEmpty[i]) {         // if new hour value

                     i++;                              // bump to next position
                     timesEmpty[i] = hour;             // save the hour value
                  }
               }
            }
         }

         //
         //  Bump the counters based on tee time data
         //
         eTimesA[i]++;                 // increment # of empty available tee times

         if (!event.equals( "" )) {

            eEvents[i]++;             // increment # of empty Event tee times
         }

         // safety check
         if (i >= max) {              // make sure we don't blow the arrays

            SystemUtils.logError("Proshop_report_course_utilization - length of arrays exceeded for teepastempty. Club = " +parm.club);
            break loop1;
         }

      }
      pstmt1.close();

   } catch (Exception e) {
       displayDatabaseErrMsg("Error gathering empty tee times.", e.getMessage(), out);
       error = true;
       return(error);
   }

   i = 0;            // reset the index

   try {

      //
      //  Get all the non-empty times for this day, course and date range
      //
      if (includeEvents == true) {

         pstmt1 = con.prepareStatement (
            "SELECT hr, time, event, player1, player2, player3, player4, " +
            "username1, username2, username3, username4, show1, show2, show3, show4 " +
            "FROM " + table + " " +
            "WHERE date >= ? AND date <= ? AND day = ? AND courseName = ? " +
            "ORDER BY time");

      } else {              // do not include event times

         pstmt1 = con.prepareStatement (
            "SELECT hr, time, event, player1, player2, player3, player4, " +
            "username1, username2, username3, username4, show1, show2, show3, show4 " +
            "FROM " + table + " " +
            "WHERE date >= ? AND date <= ? AND day = ? AND event = '' AND courseName = ? " +
            "ORDER BY time");
      }
        
      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, parm.sdate);
      pstmt1.setLong(2, parm.edate);
      pstmt1.setString(3, day);
      pstmt1.setString(4, course);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      loop2:
      while (rs.next()) {

         hour = rs.getInt("hr");
         time = rs.getInt("time");
         event = rs.getString("event");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         show1 = (!teepast) ? 1 : rs.getInt("show1"); // force shows to 1 if querying teecurr
         show2 = (!teepast) ? 1 : rs.getInt("show2");
         show3 = (!teepast) ? 1 : rs.getInt("show3");
         show4 = (!teepast) ? 1 : rs.getInt("show4");

         if (!interval.equals( "day" )) {              // if tee times or hour (day saves all counts in first entry only)

            if (interval.equals( "teetimes" )) {       // if tee times

               if (i == 0 && timesTeePast[i] == 0) {   // if first time here

                  timesTeePast[i] = time;              // save the time value

               } else {

                  if (time != timesTeePast[i]) {       // if new time value

                     i++;                              // bump to next position
                     timesTeePast[i] = time;           // save the time value
                  }
               }

            } else {                                   // by hour

               if (i == 0 && timesTeePast[i] == 0) {   // if first time here

                  timesTeePast[i] = hour;              // save the hour value

               } else {

                  if (hour != timesTeePast[i]) {       // if new hour value

                     i++;                              // bump to next position
                     timesTeePast[i] = hour;           // save the hour value
                  }
               }
            }

         }

         //
         //  Bump the counters based on tee time data
         //
         timesA[i]++;                                 // increment # of available tee times

         if ( (!teepast && !player1.equals( "" )) ||
              (teepast && (show1 == 1 || show2 == 1 || show3 == 1 || show4 == 1))
            ) {      // if any shows and teepast OR at least one player and teecurr

            timesU[i]++;                              // increment # of tee times utilized

            if (!event.equals( "" )) {

               eventsU[i]++;                          // increment # of Event tee times utilized
            }

            count = 0;

            if (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" ) && show1 == 1) {        // if a player and played

               slotsU[i]++;                                // increment # of Slots Utilitized
               count++;                                    // track # of players in this tee time

               if (!user1.equals( "" )) {                  // if a member

                  members[i]++;                            // increment # of members

               } else {

                  guests[i]++;                             // increment # of guests
               }
            }

            if (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" ) && show2 == 1) {        // if a player and played

               slotsU[i]++;                                // increment # of Slots Utilitized
               count++;                                    // track # of players in this tee time

               if (!user2.equals( "" )) {                  // if a member

                  members[i]++;                            // increment # of members

               } else {

                  guests[i]++;                            // increment # of guests
               }
            }

            if (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" ) && show3 == 1) {        // if a player and played
                                                       
               slotsU[i]++;                                // increment # of Slots Utilitized
               count++;                                    // track # of players in this tee time

               if (!user3.equals( "" )) {                  // if a member

                  members[i]++;                            // increment # of members

               } else {

                  guests[i]++;                             // increment # of guests
               }
            }

            if (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" ) && show4 == 1) {        // if a player and played

               slotsU[i]++;                                // increment # of Slots Utilitized
               count++;                                    // track # of players in this tee time

               if (!user4.equals( "" )) {                  // if a member

                  members[i]++;                            // increment # of members

               } else {

                  guests[i]++;                             // increment # of guests
               }
            }

            //
            //  determine size of group
            //
            if (count == 1) {

               singles[i]++;           // increment # of 1-somes

            } else {

               if (count == 2) {

                  doubles[i]++;           // increment # of 2-somes

               } else {

                  if (count == 3) {

                     triples[i]++;           // increment # of 3-somes

                  } else {

                     if (count == 4) {

                        fulls[i]++;           // increment # of full groups

                     }
                  }
               }
            }

         } else {              // all no shows - treat as an empty time!!

            eTimes[i]++;             // increment # of empty tee times

            if (!event.equals( "" )) {

               eEtimes[i]++;          // increment # of empty Event tee times
            }
         }

         // safety check
         if (i >= max) {              // make sure we don't blow the arrays

            Utilities.logError("Proshop_report_course_utilization - length of arrays exceeded for " + table + ". Club = " +parm.club);
            break loop2;
         }

      }
      pstmt1.close();

   } catch (Exception e) {
       displayDatabaseErrMsg("Error gathering past tee times.", e.getMessage(), out);
       error = true;
       return(error);
   }


   // TEMP *************************
//   SystemUtils.logError("Proshop_report_course_utilization - eTimesA=" +eTimesA[0]+ ", eTimes=" +eTimes[0]+ ", timesA=" +timesA[0]+ ", timesTeePast=" +timesTeePast[0]);



   //
   //  Now combine and move the stats from local arrays to parm
   //
   if (interval.equals( "day" )) {              // if stats for entire day (uses one entry)

      parm.times[0] = 0;
      parm.timesA[0] = (timesA[0] + eTimesA[0]);    // teepast times plus empty times
      parm.timesU[0] = timesU[0];
      parm.slotsU[0] = slotsU[0];
      parm.eventsU[0] = eventsU[0];
      parm.singles[0] = singles[0];
      parm.doubles[0] = doubles[0];
      parm.triples[0] = triples[0];
      parm.fulls[0] = fulls[0];
      parm.members[0] = members[0];
      parm.guests[0] = guests[0];
      parm.eTimesA[0] = (eTimesA[0] + eTimes[0]);   // empty times plus empty teepast times
      parm.eEvents[0] = (eEvents[0] + eEtimes[0]);  // empty event times plus empty teepast event times
       
   } else {             // hour or tee time interval
     
      i = 0;            // index for teepast times
      i2 = 0;           // index for empty times
      i3 = 0;           // index for parm arrays
        
      loop3:
      while (i < max || i2 < max) {

         if (timesTeePast[i] > 0 && (timesTeePast[i] < timesEmpty[i2] || timesEmpty[i2] == 0)) {     // use teepast time?
     
            if (timesTeePast[i] != parm.times[i3]) {       // new time?

               if (parm.times[i3] > 0) {                   // if not first time in parm

                  i3++;                                       // next time slot in parm

                  // safety check
                  if (i3 >= max) {              // make sure we don't blow the arrays

                     SystemUtils.logError("Proshop_report_course_utilization - length of arrays exceeded for move teepast data. Club = " +parm.club);
                     break loop3;
                  }
               }
               parm.times[i3] = timesTeePast[i];           // set new time
            }              
  
            parm.timesA[i3] += timesA[i];                  // move teepast data to parm
            parm.timesU[i3] += timesU[i];
            parm.slotsU[i3] += slotsU[i];
            parm.eventsU[i3] += eventsU[i];
            parm.singles[i3] += singles[i];
            parm.doubles[i3] += doubles[i];
            parm.triples[i3] += triples[i];
            parm.fulls[i3] += fulls[i];
            parm.members[i3] += members[i];
            parm.guests[i3] += guests[i];
            parm.eTimesA[i3] += eTimes[i];
            parm.eEvents[i3] += eEtimes[i];

            i++;                                          // bump teepast index

         } else {        // check empty times     

            if ((timesEmpty[i2] > 0 && timesEmpty[i2] < timesTeePast[i]) ||
                (timesEmpty[i2] == timesTeePast[i])) {                        // use empty time?

               if (timesEmpty[i2] != parm.times[i3]) {        // new time?

                  if (parm.times[i3] > 0) {                   // if not first time in parm

                     i3++;                                       // next time slot in parm

                     // safety check
                     if (i3 >= max) {              // make sure we don't blow the arrays

                        SystemUtils.logError("Proshop_report_course_utilization - length of arrays exceeded for move empty data. Club = " +parm.club);
                        break loop3;
                     }
                  }
                  parm.times[i3] = timesEmpty[i2];           // set new time
               }

               parm.eTimesA[i3] += eTimesA[i2];               // move empty data
               parm.eEvents[i3] += eEvents[i2];

               i2++;                                          // bump empty index
                 
            } else {         // neither Past nor Empty is next (reached end?)

               if (timesTeePast[i] == 0) {       // reached the end of Past times?

                  i2++;                          // bump Empty index to check next entry

               } else {

                  if (timesEmpty[i2] == 0) {       // reached the end of Empty times?

                     i++;                          // bump Past index to check next entry
                  }
               }
            }
         }        // end of IF (which time to use)
         
         //            
         //  see if we are done
         //
         if (timesTeePast[i] == 0 && timesEmpty[i2] == 0) {       // reached the end of both times?

            break loop3;              // then exit loop
         }

         //
         //  prevent an infinite loop
         //
         loopCount++;
           
         if (loopCount > 500) {       // reached the limit

            SystemUtils.logError("Proshop_report_course_utilization - loop counter exceeded. Club=" +parm.club+ ", i=" +i+ ", i2=" +i2+ ", i3=" +i3+ ", past=" +timesTeePast[i]+ ", empty=" +timesEmpty[i2]+ ", parm=" +parm.times[i3]);
            break loop3;                          // then exit loop - something is broken
         }

      }           // end of WHILE
   }              // end of IF interval

   return(error);

 }


 //***************************************************************************
 // Common Method build and display one day for the report in Web Page Format
 //***************************************************************************
 //
 private void buildDayTableWeb(parmUtilization parm, PrintWriter out, Connection con) {


   //boolean emptyTime = false;
   boolean error = false;
   boolean includeEvents = false;

   if (parm.events.equals( "yes" )) {          // if we are to include event times

      includeEvents = true;
   }


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
   int mshipc = 0;      // # of mship types
   int mtypec = 0;      // # of mtypes
   int guestc = 0;      // # of guest types
   int modesc = 0;      // # of modes of trans

   int today = (int)Utilities.getDate(con);
   boolean teepast = (parm.sdate < today);

   //
   //  parm block to hold the club parameters
   //
   parmClub parmc = new parmClub(0, con); // golf only report
   parmCourse parmcs = new parmCourse();          // allocate a parm block for Course parms

   //
   //   Get the mtypes, mships and guest types for this club
   //
   if (!parm.data.equals( "general" )) {          // if we need the club info     
  
      try {

         getClub.getParms(con, parmc);        // get the club parms
      }
      catch (Exception ignore) {
      }
        
      //
      //   get the number of configured types based on the type of display requested
      //
      if (parm.data.equals( "modes" )) {          // if data type = by mode of trans

         try {

            getParms.getTmodes(con, parmcs, parm.course);        // get the course parms (tmodes)
         }
         catch (Exception ignore) {
         }
        
         tmloop1:
         while (modesc < parmcs.tmode_limit) {         // check all mode of trans
           
            if (!parmcs.tmodea[modesc].equals( "" )) {
              
               modesc++;                           // count # of modes
                 
            } else {
              
               break tmloop1;                       // done
            }
         }
      }

      if (parm.data.equals( "mtype" )) {          // if data type = by member type

         tloop1:
         while (mtypec < parmc.MAX_Mems) {         // check all member types
           
            if (!parmc.mem[mtypec].equals( "" )) {
              
               mtypec++;                           // count # of mtypes
                 
            } else {
              
               break tloop1;                       // done
            }
         }
      }

      if (parm.data.equals( "mship" )) {          // if data type = by membership type

         tloop2:
         while (mshipc < parmc.MAX_Mships) {         // check all member types

            if (!parmc.mship[mshipc].equals( "" )) {

               mshipc++;                           // count # of mships

            } else {

               break tloop2;                       // done
            }
         }
      }

      if (parm.data.equals( "guest" )) {          // if data type = by guest type

         tloop3:
         while (guestc < parmc.MAX_Guests) {         // check all member types

            if (!parmc.guest[guestc].equals( "" )) {

               guestc++;                           // count # of guests

            } else {

               break tloop3;                       // done
            }
         }
      }
   }


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
      out.println("<b>Tee Times<br>Available</b>");
      out.println("</font></td>");
   }

   if (parm.tTimesUsed == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Tee Times<br>Utilized</b>");
      out.println("</font></td>");
   }
     
   if (parm.tTimesUnused == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Tee Times<br>Unused</b>");
      out.println("</font></td>");
   }

   if (parm.tTimesFull == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Full<br>Times</b>");
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

   if (parm.eTimesUsed == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Event Times<br>Utilized</b>");
      out.println("</font></td>");
   }

   if (parm.eTimesUnused == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Event Times<br>Unused</b>");
      out.println("</font></td>");
   }

   if (parm.slotsAvail == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Slots<br>Available</b>");
      out.println("</font></td>");
   }

   if (parm.slotsUsed == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Slots<br>Utilized</b>");
      out.println("</font></td>");
   }

   if (parm.slotsUnused == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Slots<br>Unused</b>");
      out.println("</font></td>");
   }

   if (parm.memRounds == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Member<br>Rounds</b>");
      out.println("</font></td>");
   }

   if (parm.gstRounds == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Guest<br>Rounds</b>");
      out.println("</font></td>");
   }     

   if (parm.data.equals( "mtype" )) {          // if data type = by member type

      for (i=0; i<mtypec; i++) {               // 1 column per mtype

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b>" +parmc.mem[i]+ "<br>Rounds</b>");
         out.println("</font></td>");
      }

   } else {

      if (parm.data.equals( "mship" )) {          // if data type = by membership type

         for (i=0; i<mshipc; i++) {               // 1 column per mship type

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>" +parmc.mship[i]+ "<br>Rounds</b>");
            out.println("</font></td>");
         }

      } else {

         if (parm.data.equals( "modes" )) {          // if data type = by mode of trans

            for (i=0; i<modesc; i++) {               // 1 column per mode

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<b>" +parmcs.tmodea[i]+ "<br>Rounds</b>");
               out.println("</font></td>");
            }

         } else {

            if (parm.data.equals( "guest" )) {          // if data type = by guest type

               for (i=0; i<guestc; i++) {               // 1 column per guest type

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<b>" +parmc.guest[i]+ "<br>Rounds</b>");
                  out.println("</font></td>");
               }

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<b>Other<br>" + (teepast ? "Guest" : "Unknown") + "<br>Rounds</b>");
               out.println("</font></td>");
            }
         }
      }

   }  
   out.println("</tr>");

   //
   //  output one row for each tee time, or each hour, or one row if interval=day
   //
   loop1:
   for (i=0; i<parm.max; i++) {

      //
      //  get the time value and convert to string
      //
      if (parm.interval.equals( "day" )) {       // no time values for 'day'

         avail = parm.timesA[i];                 // get total available tee times

         time2 = 0;                              // indicate 'day' display for later
           
      } else {

         if (parm.times[i] == 0) {

            break loop1;           // exit if done
         }

         avail = parm.timesU[i] + parm.eTimesA[i];      // get total available tee times (teepast used + all empty)
                                                        // NOTE: parm.eTimesA contains all empty times and no-shows!! (empty/unused from teepast2 and teepastempty)

         if (parm.interval.equals( "hour" )) {

            hr = parm.times[i];                   // only hour value in time slot
              
            time2 = hr;                           // save for later

         } else {

            time = parm.times[i];

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

      slots = (avail * 4);                             // total Slots available

      slotsu = (slots - parm.slotsU[i]);               // unused slots


      out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");     // data row(s)
      out.println("<font size=\"2\">");
      if (parm.interval.equals( "day" )) {
         out.println("All");
      } else {
         if (parm.interval.equals( "hour" )) {
            out.println(hr + ":xx" + ampm);
         } else {
            if (min < 10) {
               out.println(hr + ":0" + min + ampm);
            } else {
               out.println(hr + ":" + min + ampm);
            }
         }
      }
      out.println("</font></td>");
        
      if (parm.tTimesAvail == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(avail);                            // total available tee times
         out.println("</font></td>");
      }

      if (parm.tTimesUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.timesU[i] > 0) {
            out.println(parm.timesU[i]+ " (" +((parm.timesU[i] * 100) / avail)+ "%)");     // tee times utilized
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.tTimesUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.eTimesA[i] > 0) {
            out.println(parm.eTimesA[i]+ " (" +((parm.eTimesA[i] * 100) / avail)+ "%)");   // unused tee times (empty)
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.tTimesFull == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.fulls[i] > 0) {
            out.println(parm.fulls[i]+ " (" +((parm.fulls[i] * 100) / avail)+ "%)");         // Full Tee Times
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }
    
      if (parm.t3players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.triples[i] > 0) {
            out.println(parm.triples[i]+ " (" +((parm.triples[i] * 100) / avail)+ "%)");      // 3-some times
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.t2players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.doubles[i] > 0) {
            out.println(parm.doubles[i]+ " (" +((parm.doubles[i] * 100) / avail)+ "%)");       // 2-some times
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.t1player == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.singles[i] > 0) {
            out.println(parm.singles[i]+ " (" +((parm.singles[i] * 100) / avail)+ "%)");      // 1-some times
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.eTimesUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.eventsU[i] > 0) {
            out.println(parm.eventsU[i]+ " (" +((parm.eventsU[i] * 100) / avail)+ "%)");     // Event times utilized
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.eTimesUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.eEvents[i] > 0) {
            out.println(parm.eEvents[i]+ " (" +((parm.eEvents[i] * 100) / avail)+ "%)");        // Unused Events (empty)
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.slotsAvail == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(slots);                                                         // total Slots available
         out.println("</font></td>");
      }

      if (parm.slotsUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.slotsU[i] > 0) {
            out.println(parm.slotsU[i]+ " (" +((parm.slotsU[i] * 100) / slots)+ "%)");    // slots utilized
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.slotsUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (slotsu > 0) {
            out.println(slotsu+ " (" +((slotsu * 100) / slots)+ "%)");                      // unused slots
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.memRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.members[i] > 0) {
            out.println(parm.members[i]+ " (" +((parm.members[i] * 100) / parm.slotsU[i])+ "%)");     // member rounds
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.gstRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.guests[i] > 0) {
            out.println(parm.guests[i]+ " (" +((parm.guests[i] * 100) / parm.slotsU[i])+ "%)");      // guest rounds
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }        
  
      if (parm.data.equals( "mtype" )) {          // if data type = by member type

         error = doMtypeCols(parm, parmc, time2, mtypec, parm.members[i], out, con);  // display 1 col for each type

         if (error == true) {

            break loop1;
         }

      } else {

         if (parm.data.equals( "mship" )) {          // if data type = by membership type

            error = doMshipCols(parm, parmc, time2, mshipc, parm.members[i], out, con);  // display 1 col for each type

            if (error == true) {

               break loop1;
            }

         } else {

            if (parm.data.equals( "guest" )) {          // if data type = by guest type

               error = doGuestCols(parm, parmc, time2, guestc, parm.guests[i], out, con);  // display 1 col for each type

               if (error == true) {

                  break loop1;
               }
            
            } else {

               if (parm.data.equals( "modes" )) {          // if data type = by modes of trans

                  error = doModeCols(parm, parmcs, time2, modesc, parm.members[i] + parm.guests[i], out, con);  // display 1 col for each type

                  if (error == true) {

                     break loop1;
                  }
               }
            }
         }  
      }  
      out.println("</tr>");


      if (parm.interval.equals( "day" )) {      // if day report

         break loop1;                           // done
      }

      if (bgcolor.equals( bgcolor1 )) {

         bgcolor = bgcolor2;

      } else {

         bgcolor = bgcolor1;
      }

   }          // do all the rows

   out.println("</table");
    
 }
 
 
 //***************************************************************************
 // Common Method build and display one day for the report in Excel Format
 //***************************************************************************
 //
 private void buildDayTableExcel(parmUtilization parm, PrintWriter out, Connection con) {


   //boolean emptyTime = false;
   boolean error = false;
   boolean includeEvents = false;

   if (parm.events.equals( "yes" )) {          // if we are to include event times

      includeEvents = true;
   }


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
   int mshipc = 0;      // # of mship types
   int mtypec = 0;      // # of mtypes
   int guestc = 0;      // # of guest types
   int modesc = 0;      // # of modes of trans

   int today = (int)Utilities.getDate(con);
   boolean teepast = (parm.sdate < today);


   //
   //  parm block to hold the club parameters
   //
   parmClub parmc = new parmClub(0, con); // golf only report
   parmCourse parmcs = new parmCourse();          // allocate a parm block for Course parms

   //
   //   Get the mtypes, mships and guest types for this club
   //
   if (!parm.data.equals( "general" )) {          // if we need the club info

      try {

         getClub.getParms(con, parmc);        // get the club parms
      }
      catch (Exception ignore) {
      }

      //
      //   get the number of configured types based on the type of display requested
      //
      if (parm.data.equals( "modes" )) {          // if data type = by mode of trans

         try {

            getParms.getTmodes(con, parmcs, parm.course);        // get the course parms (tmodes)
         }
         catch (Exception ignore) {
         }
        
         tmloop1:
         while (modesc < parmcs.tmode_limit) {         // check all mode of trans
           
            if (!parmcs.tmodea[modesc].equals( "" )) {
              
               modesc++;                           // count # of modes
                 
            } else {
              
               break tmloop1;                       // done
            }
         }
      }

      if (parm.data.equals( "mtype" )) {          // if data type = by member type

         tloop1:
         while (mtypec < parmc.MAX_Mems) {         // check all member types

            if (!parmc.mem[mtypec].equals( "" )) {

               mtypec++;                           // count # of mtypes

            } else {

               break tloop1;                       // done
            }
         }
      }

      if (parm.data.equals( "mship" )) {          // if data type = by membership type

         tloop2:
         while (mshipc < parmc.MAX_Mships) {         // check all member types

            if (!parmc.mship[mshipc].equals( "" )) {

               mshipc++;                           // count # of mships

            } else {

               break tloop2;                       // done
            }
         }
      }

      if (parm.data.equals( "guest" )) {          // if data type = by guest type

         tloop3:
         while (guestc < parmc.MAX_Guests) {         // check all member types

            if (!parmc.guest[guestc].equals( "" )) {

               guestc++;                           // count # of guests

            } else {

               break tloop3;                       // done
            }
         }
      }
   }


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
      out.println("<b>Tee Times<br>Available</b>");
      out.println("</font></td>");
   }

   if (parm.tTimesUsed == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Tee Times<br>Utilized</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.tTimesUnused == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Tee Times<br>Unused</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.tTimesFull == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Full<br>Times</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.t3players == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>3<br>Players</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.t2players == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>2<br>Players</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.t1player == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>1<br>Player</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.eTimesUsed == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Event Times<br>Utilized</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.eTimesUnused == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Event Times<br>Unused</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.slotsAvail == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Slots<br>Available</b>");
      out.println("</font></td>");
   }

   if (parm.slotsUsed == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Slots<br>Utilized</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.slotsUnused == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Slots<br>Unused</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.memRounds == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Member<br>Rounds</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }

   if (parm.gstRounds == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Guest<br>Rounds</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>%</b>");
      out.println("</font></td>");
   }
        
   if (parm.data.equals( "mtype" )) {          // if data type = by member type

      for (i=0; i<mtypec; i++) {               // 1 column per mtype

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b>" +parmc.mem[i]+ "<br>Rounds</b>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b>%</b>");
         out.println("</font></td>");
      }

   } else {

      if (parm.data.equals( "mship" )) {          // if data type = by membership type

         for (i=0; i<mshipc; i++) {               // 1 column per mship type

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>" +parmc.mship[i]+ "<br>Rounds</b>");
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>%</b>");
            out.println("</font></td>");
         }

      } else {

         if (parm.data.equals( "guest" )) {          // if data type = by guest type

            for (i=0; i<guestc; i++) {               // 1 column per guest type

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<b>" +parmc.guest[i]+ "<br>Rounds</b>");
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<b>%</b>");
               out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>Other<br>" + (teepast ? "Guest" : "Unknown") + "<br>Rounds</b>");
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>%</b>");
            out.println("</font></td>");

         } else {

            if (parm.data.equals( "modes" )) {          // if data type = by mode of trans

               for (i=0; i<modesc; i++) {               // 1 column per mode

                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<b>" +parmcs.tmodea[i]+ "<br>Rounds</b>");
                  out.println("</font></td>");
                  out.println("<td align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<b>%</b>");
                  out.println("</font></td>");
               }
            }
         }
      }
   }
  
   out.println("</tr>");

   //
   //  output one row for each tee time, or each hour, or one row if interval=day
   //
   loop1:
   for (i=0; i<parm.max; i++) {

      //
      //  get the time value and convert to string
      //
      if (parm.interval.equals( "day" )) {       // no time values for 'day'

         avail = parm.timesA[i];                 // get total available tee times

         time2 = 0;                              // indicate 'day' display for later

      } else {

         if (parm.times[i] == 0) {

            break loop1;           // exit if done
         }

         avail = parm.timesU[i] + parm.eTimesA[i];      // get total available tee times (teepast used + all empty)
                                                        // NOTE: parm.eTimesA contains all empty times and no-shows!! (empty/unused from teepast2 and teepastempty)

         if (parm.interval.equals( "hour" )) {

            hr = parm.times[i];                   // only hour value in time slot

            time2 = hr;                           // save for later

         } else {

            time = parm.times[i];

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

      slots = (avail * 4);                             // total Slots available

      slotsu = (slots - parm.slotsU[i]);               // unused slots


      out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");     // data row(s)
      out.println("<font size=\"2\">");
      if (parm.interval.equals( "day" )) {
         out.println("All");
      } else {
         if (parm.interval.equals( "hour" )) {
            out.println(hr + ":xx" + ampm);
         } else {
            if (min < 10) {
               out.println(hr + ":0" + min + ampm);
            } else {
               out.println(hr + ":" + min + ampm);
            }
         }
      }
      out.println("</font></td>");
        
      if (parm.tTimesAvail == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(avail);                            // total available tee times
         out.println("</font></td>");
      }

      if (parm.tTimesUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.timesU[i]);                            // tee times utilized
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.timesU[i] > 0) {
            out.println(((parm.timesU[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.tTimesUnused == true) {          // if column to be displayed
  
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.eTimesA[i]);                         // unused tee times (empty)
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.eTimesA[i] > 0) {
            out.println(((parm.eTimesA[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.tTimesFull == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.fulls[i]);                           // Full Tee Times
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.fulls[i] > 0) {
            out.println(((parm.fulls[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.t3players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.triples[i]);                        // 3-some times
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.triples[i] > 0) {
            out.println(((parm.triples[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.t2players == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.doubles[i]);                         // 2-some times
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.doubles[i] > 0) {
            out.println(((parm.doubles[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.t1player == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.singles[i]);                            // 1-some times
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.singles[i] > 0) {
            out.println(((parm.singles[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.eTimesUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.eventsU[i]);                           // Event times utilized
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.eventsU[i] > 0) {
            out.println(((parm.eventsU[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.eTimesUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.eEvents[i]);                           // Unused Events (empty)
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.eEvents[i] > 0) {
            out.println(((parm.eEvents[i] * 100) / avail)+ "%");     // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.slotsAvail == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(slots);                                   // total Slots available
         out.println("</font></td>");
      }

      if (parm.slotsUsed == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.slotsU[i]);                           // slots utilized
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.slotsU[i] > 0) {
            out.println(((parm.slotsU[i] * 100) / slots)+ "%");              // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.slotsUnused == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(slotsu);                                   // unused slots
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (slotsu > 0) {
            out.println(((slotsu * 100) / slots)+ "%");                      // %
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.memRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.members[i]);                             // member rounds
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.members[i] > 0) {
            out.println(((parm.members[i] * 100) / parm.slotsU[i])+ "%");            // %   (of utilized slots)
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }

      if (parm.gstRounds == true) {          // if column to be displayed

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(parm.guests[i]);                              // guest rounds
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (parm.guests[i] > 0) {
            out.println(((parm.guests[i] * 100) / parm.slotsU[i])+ "%");            // %   (of utilized slots)
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }     

  
      if (parm.data.equals( "mtype" )) {          // if data type = by member type

         error = doMtypeCols(parm, parmc, time2, mtypec, parm.members[i], out, con);  // display 1 col for each type

         if (error == true) {

            break loop1;
         }

      } else {

         if (parm.data.equals( "mship" )) {          // if data type = by membership type

            error = doMshipCols(parm, parmc, time2, mshipc, parm.members[i], out, con);  // display 1 col for each type

            if (error == true) {

               break loop1;
            }

         } else {

            if (parm.data.equals( "guest" )) {          // if data type = by guest type

               error = doGuestCols(parm, parmc, time2, guestc, parm.guests[i], out, con);  // display 1 col for each type

               if (error == true) {

                  break loop1;
               }
            
            } else {

               if (parm.data.equals( "modes" )) {          // if data type = by modes of trans

                  error = doModeCols(parm, parmcs, time2, modesc, parm.members[i] + parm.guests[i], out, con);  // display 1 col for each type

                  if (error == true) {

                     break loop1;
                  }
               }
            }
         }
      }
  
      out.println("</tr>");      // end of row


      if (parm.interval.equals( "day" )) {      // if day report

         break loop1;                           // done
      }

      if (bgcolor.equals( bgcolor1 )) {

         bgcolor = bgcolor2;

      } else {

         bgcolor = bgcolor1;
      }

   }          // do all the rows

   out.println("</table");

 }


 //**************************************************
 // Common Method for Displaying Mtype Columns
 //
 //  Display one column for each mtype configured.
 //**************************************************
 //
 private boolean doMtypeCols(parmUtilization parm, parmClub parmc, int time, int mtypec, int rounds, PrintWriter out, Connection con) {


   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   boolean error = false;
   boolean includeEvents = false;

   if (parm.events.equals( "yes" )) {          // if we are to include event times

      includeEvents = true;
   }
     
   String mtype1 = "";
   String mtype2 = "";
   String mtype3 = "";
   String mtype4 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";

   int today = (int)Utilities.getDate(con);

   String show_clause = showClauseByDate(parm.sdate, today);
   String table = tableByDate(parm.sdate, today);
   String teepastOnlyFields = "";
   boolean teepast = (parm.sdate < today);

   int i = 0;
   int stime = 0;
   int etime = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;

   if (teepast) teepastOnlyFields = ", mtype1, mtype2, mtype3, mtype4";

   int [] mtypeC = new int [mtypec];     // counts for each type


   try {

      if (rounds > 0) {            // if there are any member rounds to check

         //
         //  process based on the interval requested
         //
         if (parm.interval.equals( "day" )) {       // all day (one column)

            //
            //  gather counts for the entire day
            //
            if (includeEvents == true) {

               pstmt1 = con.prepareStatement (
                  "SELECT username1, username2, username3, username4, " +
                  "show1, show2, show3, show4" + teepastOnlyFields + " " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND courseName = ? AND " +
                  "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);

            } else {            // do not include event times

               pstmt1 = con.prepareStatement (
                  "SELECT username1, username2, username3, username4, " +
                  "show1, show2, show3, show4" + teepastOnlyFields + " " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND event = '' AND courseName = ? AND " +
                  "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);
            }

            //
            //  Count the number of tee times for each member type
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setLong(1, parm.sdate);
            pstmt1.setLong(2, parm.edate);
            pstmt1.setString(3, parm.day);
            pstmt1.setString(4, parm.course);

         } else {

            if (parm.interval.equals( "hour" )) {    // one row per hour

               //
               //  gather stats for the specified hour
               //
               stime = (time * 100);                  // convert to military time
               etime = (stime + 60);                  // create end time to capture entire hour

               if (includeEvents == true) {

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);

               } else {            // do not include event times

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND event = '' AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);
               }

               //
               //  Count the number of tee times for each member type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, stime);
               pstmt1.setInt(5, etime);
               pstmt1.setString(6, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt


            } else {     // one row per tee time (time = tee time value)

               //
               //  gather stats for the specified tee time
               //
               if (includeEvents == true) {

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);

               } else {            // do not include event times

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND event = '' AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);
               }

               //
               //  Count the number of tee times for each member type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, time);
               pstmt1.setString(5, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

            }
         }


         //
         //   Run teepast2 query selected above
         //
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            show1 = (!teepast) ? 1 : rs.getInt("show1");  // force shows to 1 if querying teecurr
            show2 = (!teepast) ? 1 : rs.getInt("show2");
            show3 = (!teepast) ? 1 : rs.getInt("show3");
            show4 = (!teepast) ? 1 : rs.getInt("show4");

            //
            // check each player if member and they played
            //
            if (!user1.equals( "" ) && show1 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mtype data from original query

                   mtype1 = rs.getString("mtype1");

               } else {     // Report is for future dates, look up mtype data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_type FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user1);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mtype1 = rs2.getString("m_type");
                       }

                   } catch (Exception exc) {

                       mtype1 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mtype1.equals( "" )) {

                  loop1:
                  for (i=0; i<mtypec; i++) {               // 1 column per mtype

                     if (mtype1.equals(parmc.mem[i])) {     // find matching type

                        mtypeC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user1

            if (!user2.equals( "" ) && show2 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mtype data from original query

                   mtype2 = rs.getString("mtype2");

               } else {     // Report is for future dates, look up mtype data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_type FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user2);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mtype2 = rs2.getString("m_type");
                       }

                   } catch (Exception exc) {

                       mtype2 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mtype2.equals( "" )) {

                  loop1:
                  for (i=0; i<mtypec; i++) {               // 1 column per mtype

                     if (mtype2.equals(parmc.mem[i])) {     // find matching type

                        mtypeC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user2

            if (!user3.equals( "" ) && show3 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mtype data from original query

                   mtype3 = rs.getString("mtype3");

               } else {     // Report is for future dates, look up mtype data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_type FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user3);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mtype3 = rs2.getString("m_type");
                       }

                   } catch (Exception exc) {

                       mtype3 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mtype3.equals( "" )) {

                  loop1:
                  for (i=0; i<mtypec; i++) {               // 1 column per mtype

                     if (mtype3.equals(parmc.mem[i])) {     // find matching type

                        mtypeC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user3

            if (!user4.equals( "" ) && show4 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mtype data from original query

                   mtype4 = rs.getString("mtype4");

               } else {     // Report is for future dates, look up mtype data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_type FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user4);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mtype4 = rs2.getString("m_type");
                       }

                   } catch (Exception exc) {

                       mtype4 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mtype4.equals( "" )) {

                  loop1:
                  for (i=0; i<mtypec; i++) {               // 1 column per mtype

                     if (mtype4.equals(parmc.mem[i])) {     // find matching type

                        mtypeC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user4

         }    // end of WHILE

         pstmt1.close();
      }

      //
      //  Display the count for this type
      //
      for (i=0; i<mtypec; i++) {               // 1 column per type

         if (!parm.format.equals( "excel" )) {
           
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (mtypeC[i] > 0 && rounds > 0) {
               out.println(mtypeC[i]+ " (" +((mtypeC[i] * 100) / rounds)+ "%)");
            } else {
               out.println("0");
            }
            out.println("</font></td>");

         } else {        // excel - display in 2 columns

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(mtypeC[i]);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (mtypeC[i] > 0 && rounds > 0) {
               out.println(((mtypeC[i] * 100) / rounds)+ "%");
            } else {
               out.println("0");
            }
            out.println("</font></td>");
         }
      }

   } catch (Exception e) {
       displayDatabaseErrMsg("Error gather member type counts.", e.getMessage(), out);
       error = true;
       return(error);
   }
  
   return(error);

 }


 //**************************************************
 // Common Method for Displaying Mship Columns
 //
 //  Display one column for each mship configured.
 //**************************************************
 //
 private boolean doMshipCols(parmUtilization parm, parmClub parmc, int time, int mshipc, int rounds, PrintWriter out, Connection con) {


   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;


   boolean error = false;
   boolean includeEvents = false;

   if (parm.events.equals( "yes" )) {          // if we are to include event times

      includeEvents = true;
   }

   String mship1 = "";
   String mship2 = "";
   String mship3 = "";
   String mship4 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";

   int today = (int)Utilities.getDate(con);

   String show_clause = showClauseByDate(parm.sdate, today);
   String table = tableByDate(parm.sdate, today);
   String teepastOnlyFields = "";
   boolean teepast = (parm.sdate < today);

   int i = 0;
   int stime = 0;
   int etime = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;

   int [] mshipC = new int [mshipc];     // counts for each type

   if (teepast) teepastOnlyFields = ", mship1, mship2, mship3, mship4";

   try {

      if (rounds > 0) {            // if there are any member rounds to check

         //
         //  process based on the interval requested
         //
         if (parm.interval.equals( "day" )) {       // all day (one column)

            //
            //  gather counts for the entire day
            //
            if (includeEvents == true) {

               pstmt1 = con.prepareStatement (
                  "SELECT username1, username2, username3, username4, " +
                  "show1, show2, show3, show4" + teepastOnlyFields + " " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND courseName = ? AND " +
                  "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);

            } else {       // do not include event times

               pstmt1 = con.prepareStatement (
                  "SELECT username1, username2, username3, username4, " +
                  "show1, show2, show3, show4" + teepastOnlyFields + " " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND event = '' AND courseName = ? AND " +
                  "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);
            }

            //
            //  Count the number of tee times for each member ship
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setLong(1, parm.sdate);
            pstmt1.setLong(2, parm.edate);
            pstmt1.setString(3, parm.day);
            pstmt1.setString(4, parm.course);

         } else {

            if (parm.interval.equals( "hour" )) {    // one row per hour

               //
               //  gather stats for the specified hour
               //
               stime = (time * 100);                  // convert to military time
               etime = (stime + 60);                  // create end time to capture entire hour

               if (includeEvents == true) {

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);

               } else {       // do not include event times

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND event = '' AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);
               }

               //
               //  Count the number of tee times for each member type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, stime);
               pstmt1.setInt(5, etime);
               pstmt1.setString(6, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt


            } else {     // one row per tee time (time = tee time value)

               //
               //  gather stats for the specified tee time
               //
               if (includeEvents == true) {

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);

               } else {       // do not include event times

                  pstmt1 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, " +
                     "show1, show2, show3, show4" + teepastOnlyFields + " " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND event = '' AND courseName = ? AND " +
                     "(username1 != '' OR username2 != '' OR username3 != '' OR username4 != '') " + show_clause);
               }

               //
               //  Count the number of tee times for each member type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, time);
               pstmt1.setString(5, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

            }
         }


         //
         //   Run teepast2 query selected above
         //
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            user1 = rs.getString("username1");
            user2 = rs.getString("username2");
            user3 = rs.getString("username3");
            user4 = rs.getString("username4");
            show1 = (!teepast) ? 1 : rs.getInt("show1"); // force shows to 1 if querying teecurr
            show2 = (!teepast) ? 1 : rs.getInt("show2");
            show3 = (!teepast) ? 1 : rs.getInt("show3");
            show4 = (!teepast) ? 1 : rs.getInt("show4");

            //
            // check each player if member and they played
            //
            if (!user1.equals( "" ) && show1 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mship data from original query

                   mship1 = rs.getString("mship1");

               } else {     // Report is for future dates, look up mship data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user1);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mship1 = rs2.getString("m_ship");
                       }

                   } catch (Exception exc) {

                       mship1 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mship1.equals( "" )) {

                  loop1:
                  for (i=0; i<mshipc; i++) {               // 1 column per mship

                     if (mship1.equals(parmc.mship[i])) {     // find matching type

                        mshipC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user1

            if (!user2.equals( "" ) && show2 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mship data from original query

                   mship2 = rs.getString("mship2");

               } else {     // Report is for future dates, look up mship data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user2);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mship2 = rs2.getString("m_ship");
                       }

                   } catch (Exception exc) {

                       mship2 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mship2.equals( "" )) {

                  loop1:
                  for (i=0; i<mshipc; i++) {               // 1 column per mship

                     if (mship2.equals(parmc.mship[i])) {     // find matching type

                        mshipC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user2

            if (!user3.equals( "" ) && show3 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mship data from original query

                   mship3 = rs.getString("mship3");
                   
               } else {     // Report is for future dates, look up mship data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user3);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mship3 = rs2.getString("m_ship");
                       }

                   } catch (Exception exc) {

                       mship3 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mship3.equals( "" )) {

                  loop1:
                  for (i=0; i<mshipc; i++) {               // 1 column per mship

                     if (mship3.equals(parmc.mship[i])) {     // find matching type

                        mshipC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user3

            if (!user4.equals( "" ) && show4 == 1) {    // should we check this user?

               if (teepast) {     // Report is for past dates, grab mship data from original query

                   mship4 = rs.getString("mship4");

               } else {     // Report is for future dates, look up mship data for this member from member2b

                   try {
                       pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                       pstmt2.clearParameters();
                       pstmt2.setString(1, user3);

                       rs2 = pstmt2.executeQuery();

                       if (rs2.next()) {
                           mship4 = rs2.getString("m_ship");
                       }

                   } catch (Exception exc) {

                       mship4 = "";

                   } finally {

                       try { rs2.close(); }
                       catch (Exception ignore) { }

                       try { pstmt2.close(); }
                       catch (Exception ignore) { }
                   }
               }

               if (!mship4.equals( "" )) {

                  loop1:
                  for (i=0; i<mshipc; i++) {               // 1 column per mship

                     if (mship4.equals(parmc.mship[i])) {     // find matching type

                        mshipC[i]++;
                        break loop1;
                     }
                  }
               }
            }       // end of IF user4

         }    // end of WHILE

         pstmt1.close();
      }

      //
      //  Display the count for this type
      //
      for (i=0; i<mshipc; i++) {               // 1 column per type

         if (!parm.format.equals( "excel" )) {

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (mshipC[i] > 0 && rounds > 0) {
               out.println(mshipC[i]+ " (" +((mshipC[i] * 100) / rounds)+ "%)");
            } else {
               out.println("0");
            }
            out.println("</font></td>");

         } else {        // excel - display in 2 columns

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(mshipC[i]);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (mshipC[i] > 0 && rounds > 0) {
               out.println(((mshipC[i] * 100) / rounds)+ "%");
            } else {
               out.println("0");
            }
            out.println("</font></td>");
         }
      }

   } catch (Exception e) {
       displayDatabaseErrMsg("Error gather member ship counts.", e.getMessage(), out);
       error = true;
       return(error);
   }

   return(error);

 }


 //**************************************************
 // Common Method for Displaying Guest Columns
 //
 //  Display one column for each guest configured.
 //**************************************************
 //
 private boolean doGuestCols(parmUtilization parm, parmClub parmc, int time, int guestc, int rounds, PrintWriter out, Connection con) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;

   boolean error = false;
   boolean includeEvents = false;
   boolean found = false;

   if (parm.events.equals( "yes" )) {          // if we are to include event times

      includeEvents = true;
   }

   String gtype1 = "";
   String gtype2 = "";
   String gtype3 = "";
   String gtype4 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";

   int today = (int)Utilities.getDate(con);

   String show_clause = showClauseByDate(parm.sdate, today);
   String table = tableByDate(parm.sdate, today);
   String teepastOnlyFields = "";
   boolean teepast = (parm.sdate < today);

   int i = 0;
   int stime = 0;
   int etime = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;

   int [] guestC = new int [guestc];     // counts for each type
   int guesto = 0;                       // counts for Other guest types (no longer defined)

   if (teepast) teepastOnlyFields = ", gtype1, gtype2, gtype3, gtype4";

   try {

      if (rounds > 0) {            // if there are any guest rounds to check

         //
         //  process based on the interval requested
         //
         if (parm.interval.equals( "day" )) {       // all day (one column)

            //
            //  gather counts for the entire day
            //
            pstmt1 = con.prepareStatement (
               "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
               "show1, show2, show3, show4" + teepastOnlyFields + " " +
               "FROM " + table + " " +
               "WHERE date >= ? AND date <= ? AND day = ? AND courseName = ? AND " +
               "(player1 != '' OR player2 != '' OR player3 != '' OR player4 != '') " + show_clause);


            //
            //  Count the number of tee times for each guest type
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setLong(1, parm.sdate);
            pstmt1.setLong(2, parm.edate);
            pstmt1.setString(3, parm.day);
            pstmt1.setString(4, parm.course);

         } else {

            if (parm.interval.equals( "hour" )) {    // one row per hour

               //
               //  gather stats for the specified hour
               //
               stime = (time * 100);                  // convert to military time
               etime = (stime + 60);                  // create end time to capture entire hour

               pstmt1 = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
                  "show1, show2, show3, show4" + teepastOnlyFields + " " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND courseName = ? AND " +
                  "(player1 != '' OR player2 != '' OR player3 != '' OR player4 != '') " + show_clause);

               //
               //  Count the number of tee times for each guest type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, stime);
               pstmt1.setInt(5, etime);
               pstmt1.setString(6, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt


            } else {     // one row per tee time (time = tee time value)

               //
               //  gather stats for the specified tee time
               //
               pstmt1 = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
                  "show1, show2, show3, show4" + teepastOnlyFields + " " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND courseName = ? AND " +
                  "(player1 != '' OR player2 != '' OR player3 != '' OR player4 != '') " + show_clause);

               //
               //  Count the number of tee times for each guest type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, time);
               pstmt1.setString(5, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

            }
         }

         //
         //   Run teepast2 query selected above
         //
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            show1 = (!teepast) ? 1 : rs.getInt("show1"); // force shows to 1 if querying teecurr
            show2 = (!teepast) ? 1 : rs.getInt("show2");
            show3 = (!teepast) ? 1 : rs.getInt("show3");
            show4 = (!teepast) ? 1 : rs.getInt("show4");

            // If past report, grab gtypes from original query
            if (teepast) {

                gtype1 = rs.getString("gtype1");
                gtype2 = rs.getString("gtype2");
                gtype3 = rs.getString("gtype3");
                gtype4 = rs.getString("gtype4");

                //
                // check each player if guest and they played
                //
                if (!gtype1.equals( "" ) && show1 == 1) {    // should we check this player?

                   found = false;                            // init

                   loop1:
                   for (i=0; i<guestc; i++) {                // 1 column per guest

                      if (gtype1.equalsIgnoreCase(parmc.guest[i])) {     // find matching type

                         guestC[i]++;
                         found = true;
                         break loop1;
                      }
                   }

                   if (found == false) {

                      guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                   }
                }       // end of IF player1

                if (!gtype2.equals( "" ) && show2 == 1) {    // should we check this player?

                   found = false;                            // init

                   loop2:
                   for (i=0; i<guestc; i++) {                // 1 column per guest

                      if (gtype2.equalsIgnoreCase(parmc.guest[i])) {     // find matching type

                         guestC[i]++;
                         found = true;
                         break loop2;
                      }
                   }

                   if (found == false) {

                      guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                   }
                }       // end of IF player2

                if (!gtype3.equals( "" ) && show3 == 1) {    // should we check this player?

                   found = false;                            // init

                   loop3:
                   for (i=0; i<guestc; i++) {                // 1 column per guest

                      if (gtype3.equalsIgnoreCase(parmc.guest[i])) {     // find matching type

                         guestC[i]++;
                         found = true;
                         break loop3;
                      }
                   }

                   if (found == false) {

                      guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                   }
                }       // end of IF player3

                if (!gtype4.equals( "" ) && show4 == 1) {    // should we check this player?

                   found = false;                            // init

                   loop4:
                   for (i=0; i<guestc; i++) {                // 1 column per guest

                      if (gtype4.equalsIgnoreCase(parmc.guest[i])) {     // find matching type

                         guestC[i]++;
                         found = true;
                         break loop4;
                      }
                   }

                   if (found == false) {

                      guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                   }
                }       // end of IF player4

            } else {        // If future report, check player names for any that start with a guest type for any player with no username specified

                username1 = rs.getString("username1");
                username2 = rs.getString("username2");
                username3 = rs.getString("username3");
                username4 = rs.getString("username4");

                if (username1.equals("")) {

                    found = false;

                    for (int j=0; j<guestc; j++) {

                        if (player1.startsWith(parmc.guest[j])) {

                            guestC[j]++;
                            found = true;
                            break;
                        }
                    }

                    if (found == false) {
                        
                        guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                    }
                }

                if (username2.equals("")) {

                    found = false;

                    for (int j=0; j<guestc; j++) {

                        if (player2.startsWith(parmc.guest[j])) {

                            guestC[j]++;
                            found = true;
                            break;
                        }
                    }

                    if (found == false) {

                        guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                    }
                }

                if (username3.equals("")) {

                    found = false;

                    for (int j=0; j<guestc; j++) {

                        if (player3.startsWith(parmc.guest[j])) {

                            guestC[j]++;
                            found = true;
                            break;
                        }
                    }

                    if (found == false) {

                        guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                    }
                }

                if (username4.equals("")) {

                    found = false;

                    for (int j=0; j<guestc; j++) {

                        if (player4.startsWith(parmc.guest[j])) {

                            guestC[j]++;
                            found = true;
                            break;
                        }
                    }

                    if (found == false) {

                        guesto++;              // bump # of Other guest type rounds (guest type no longer defined)
                    }
                }
            }
         }    // end of WHILE

         pstmt1.close();
     
      }

      //
      //  Display the count for this type
      //
      for (i=0; i<guestc; i++) {               // 1 column per type

         if (!parm.format.equals( "excel" )) {

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (guestC[i] > 0 && rounds > 0) {
               out.println(guestC[i]+ " (" +((guestC[i] * 100) / rounds)+ "%)");
            } else {
               out.println("0");
            }
            out.println("</font></td>");

         } else {        // excel - display in 2 columns

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(guestC[i]);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (guestC[i] > 0 && rounds > 0) {
               out.println(((guestC[i] * 100) / rounds)+ "%");
            } else {
               out.println("0");
            }
            out.println("</font></td>");
         }
      }
      
      //
      //  Do "Other Guest" col - for guest types that no longer exist
      //
      if (!parm.format.equals( "excel" )) {

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (guesto > 0 && rounds > 0) {
            out.println(guesto+ " (" +((guesto * 100) / rounds)+ "%)");
         } else {
            out.println("0");
         }
         out.println("</font></td>");

      } else {        // excel - display in 2 columns

         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println(guesto);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         if (guesto > 0 && rounds > 0) {
            out.println(((guesto * 100) / rounds)+ "%");
         } else {
            out.println("0");
         }
         out.println("</font></td>");
      }
       

   } catch (Exception e) {
       displayDatabaseErrMsg("Error gather guest type counts.", e.getMessage(), out);
       error = true;
       return(error);
   }

   return(error);

 }


 //**************************************************
 // Common Method for Displaying Mode of Trans Columns
 //
 //  Display one column for each mode configured.
 //**************************************************
 //
 private boolean doModeCols(parmUtilization parm, parmCourse parmc, int time, int modec, int rounds, PrintWriter out, Connection con) {


   PreparedStatement pstmt1 = null;
   ResultSet rs = null;


   boolean error = false;
   boolean includeEvents = false;

   if (parm.events.equals( "yes" )) {          // if we are to include event times

      includeEvents = true;
   }
     
   String mode = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";

   int today = (int)Utilities.getDate(con);

   String show_clause = showClauseByDate(parm.sdate, today);
   String table = tableByDate(parm.sdate, today);
   boolean teepast = (parm.sdate < today);

   // these queries don't filter by checking to see if player names are populated - they use show values only
   // so instead if show_clause is empty then add a check to see if player1 is not empty - we shift names up so player1
   // should always be populated before any others.  this will greatly reduce the # of rows we have so loop over!
   if (show_clause.equals("")) show_clause = " AND player1 <> ''";

   int i = 0;
   int stime = 0;
   int etime = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;

   int [] modeC = new int [modec];     // counts for each type


   try {

      if (rounds > 0) {            // if there are any rounds to check

         //
         //  process based on the interval requested
         //
         if (parm.interval.equals( "day" )) {       // all day (one column)

            //
            //  gather counts for the entire day
            //
            if (includeEvents == true) {

               pstmt1 = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                  "show1, show2, show3, show4 " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND courseName = ? " + show_clause);

            } else {            // do not include event times

               pstmt1 = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                  "show1, show2, show3, show4 " +
                  "FROM " + table + " " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND event = '' AND courseName = ? " + show_clause);
            }

            //
            //  Count the number of tee times for each member type
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setLong(1, parm.sdate);
            pstmt1.setLong(2, parm.edate);
            pstmt1.setString(3, parm.day);
            pstmt1.setString(4, parm.course);

         } else {

            if (parm.interval.equals( "hour" )) {    // one row per hour

               //
               //  gather stats for the specified hour
               //
               stime = (time * 100);                  // convert to military time
               etime = (stime + 60);                  // create end time to capture entire hour

               if (includeEvents == true) {

                  pstmt1 = con.prepareStatement (
                     "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                     "show1, show2, show3, show4 " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND courseName = ? " + show_clause);

               } else {            // do not include event times

                  pstmt1 = con.prepareStatement (
                     "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                     "show1, show2, show3, show4 " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time >= ? AND time <= ? AND event = '' AND courseName = ? " + show_clause);
               }

               //
               //  Count the number of tee times for each member type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, stime);
               pstmt1.setInt(5, etime);
               pstmt1.setString(6, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt


            } else {     // one row per tee time (time = tee time value)

               //
               //  gather stats for the specified tee time
               //
               if (includeEvents == true) {

                  pstmt1 = con.prepareStatement (
                     "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                     "show1, show2, show3, show4 " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND courseName = ? " + show_clause);

               } else {            // do not include event times

                  pstmt1 = con.prepareStatement (
                     "SELECT player1, player2, player3, player4, p1cw, p2cw, p3cw, p4cw, " +
                     "show1, show2, show3, show4 " +
                     "FROM " + table + " " +
                     "WHERE date >= ? AND date <= ? AND day = ? AND time = ? AND event = '' AND courseName = ? " + show_clause);
               }

               //
               //  Count the number of tee times for each member type
               //
               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, parm.sdate);
               pstmt1.setLong(2, parm.edate);
               pstmt1.setString(3, parm.day);
               pstmt1.setInt(4, time);
               pstmt1.setString(5, parm.course);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

            }
         }

         //
         //   Run teepast2 query selected above
         //
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            show1 = (!teepast) ? 1 : rs.getInt("show1"); // force shows to 1 if querying teecurr
            show2 = (!teepast) ? 1 : rs.getInt("show2");
            show3 = (!teepast) ? 1 : rs.getInt("show3");
            show4 = (!teepast) ? 1 : rs.getInt("show4");

            //
            // check each mode of trans
            //
            if (!player1.equals( "" ) && !p1cw.equals( "" ) && show1 == 1) {    // should we check this mode?

               loop1:
               for (i=0; i<modec; i++) {               // 1 column per mode of trans type

                  if (p1cw.equals(parmc.tmodea[i])) {     // find matching type

                     modeC[i]++;
                     break loop1;
                  }
               }
            }       // end of IF mode

            if (!player2.equals( "" ) && !p2cw.equals( "" ) && show2 == 1) {    // should we check this mode?

               loop2:
               for (i=0; i<modec; i++) {               // 1 column per mode of trans type

                  if (p2cw.equals(parmc.tmodea[i])) {     // find matching type

                     modeC[i]++;
                     break loop2;
                  }
               }
            }       // end of IF mode

            if (!player3.equals( "" ) && !p3cw.equals( "" ) && show3 == 1) {    // should we check this mode?

               loop3:
               for (i=0; i<modec; i++) {               // 1 column per mode of trans type

                  if (p3cw.equals(parmc.tmodea[i])) {     // find matching type

                     modeC[i]++;
                     break loop3;
                  }
               }
            }       // end of IF mode

            if (!player4.equals( "" ) && !p4cw.equals( "" ) && show4 == 1) {    // should we check this mode?

               loop4:
               for (i=0; i<modec; i++) {               // 1 column per mode of trans type

                  if (p4cw.equals(parmc.tmodea[i])) {     // find matching type

                     modeC[i]++;
                     break loop4;
                  }
               }
            }       // end of IF mode


         }    // end of WHILE

         pstmt1.close();
      }

      //
      //  Display the count for this type
      //
      for (i=0; i<modec; i++) {               // 1 column per type

         if (!parm.format.equals( "excel" )) {
           
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (modeC[i] > 0 && rounds > 0) {
               out.println(modeC[i]+ " (" +((modeC[i] * 100) / rounds)+ "%)");
            } else {
               out.println("0");
            }
            out.println("</font></td>");

         } else {        // excel - display in 2 columns

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(modeC[i]);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (modeC[i] > 0 && rounds > 0) {
               out.println(((modeC[i] * 100) / rounds)+ "%");
            } else {
               out.println("0");
            }
            out.println("</font></td>");
         }
      }

   } catch (Exception e) {
       displayDatabaseErrMsg("Error gather mode of trans counts.", e.getMessage(), out);
       error = true;
       return(error);
   }
  
   return(error);

 }


 //**************************************************
 // Common Method for Displaying Date/Time of Report
 //**************************************************
 //
 private String buildDisplayDateTime() {

    GregorianCalendar cal = new GregorianCalendar();
    DateFormat df_full = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    return "<i>This report was generated on " + df_full.format(cal.getTime()) + "</i>";

 }


 private String tableByDate(long sdate, int today) {

     return (sdate < today) ? "teepast2" : "teecurr2";

 }


 private String showClauseByDate(long sdate, int today) {

     return (sdate < today) ? " AND (show1 = 1 OR show2 = 1 OR show3 = 1 OR show4 = 1) " : "";

 }


 //**************************************************
 // Common Method for Displaying Input Errors
 //**************************************************
 //
 private void displayInputErrMsg(HttpServletRequest req, String pMessage, PrintWriter out) {


    String temp = "";

    int mon = 0;
    int tue = 0;
    int wed = 0;
    int thu = 0;
    int fri = 0;
    int sat = 0;
    int sun = 0;
    int start_year = 0;
    int start_month = 0;
    int end_year = 0;
    int end_month = 0;

    //
    //  Get the input parms
    //
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";

    String format = (req.getParameter("report_format") != null) ? req.getParameter("report_format")  : "";

    String events = (req.getParameter("report_events") != null) ? req.getParameter("report_events")  : "";

    String interval = (req.getParameter("report_interval") != null) ? req.getParameter("report_interval")  : "";

    String data = (req.getParameter("report_data") != null) ? req.getParameter("report_data")  : "";

    String course = (req.getParameter("report_course") != null) ? req.getParameter("report_course")  : "";

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

   // make sure the dates here are valid
   try {

       int dash1 = start_date.indexOf("-");
       int dash2 = start_date.indexOf("-", dash1 + 1);
       start_year = Integer.parseInt(start_date.substring(0, 4));
       start_month = Integer.parseInt(start_date.substring(dash1 + 1, dash2));

       dash1 = end_date.indexOf("-");
       dash2 = end_date.indexOf("-", dash1 + 1);
       end_year = Integer.parseInt(end_date.substring(0, 4));
       end_month = Integer.parseInt(end_date.substring(dash1 + 1, dash2));

   } catch (Exception ignore) {
       // set to zero so calendars default to current month/year
       start_year = 0;
       start_month = 0;
       end_year = 0;
       end_month = 0;
   }

    out.println(SystemUtils.HeadTitle("Input Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Invalid Report Request</H2>");
    out.println("<BR><BR>Sorry, we are unable to process the report.");
    out.println("<BR><p><b>" + pMessage + "</b></p>");
    out.println("<BR>Please correct and try again.");
    out.println("<font size=\"2\">");

    out.println("<BR><BR><form action=\"Proshop_report_course_utilization\" method=\"get\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" +start_date+ "\">");
    out.println("<input type=\"hidden\" name=\"edate\" value=\"" +end_date+ "\">");
    out.println("<input type=\"hidden\" name=\"syear\" value=\"" +start_year+ "\">");
    out.println("<input type=\"hidden\" name=\"eyear\" value=\"" +end_year+ "\">");
    out.println("<input type=\"hidden\" name=\"smonth\" value=\"" +start_month+ "\">");
    out.println("<input type=\"hidden\" name=\"emonth\" value=\"" +end_month+ "\">");
    out.println("<input type=\"hidden\" name=\"report_interval\" value=\"" +interval+ "\">");
    out.println("<input type=\"hidden\" name=\"report_format\" value=\"" +format+ "\">");
    out.println("<input type=\"hidden\" name=\"report_data\" value=\"" +data+ "\">");
    out.println("<input type=\"hidden\" name=\"report_events\" value=\"" +events+ "\">");
    out.println("<input type=\"hidden\" name=\"report_course\" value=\"" +course+ "\">");
    out.println("<input type=\"hidden\" name=\"mon\" value=\"" +mon+ "\">");
    out.println("<input type=\"hidden\" name=\"tue\" value=\"" +tue+ "\">");
    out.println("<input type=\"hidden\" name=\"wed\" value=\"" +wed+ "\">");
    out.println("<input type=\"hidden\" name=\"thu\" value=\"" +thu+ "\">");
    out.println("<input type=\"hidden\" name=\"fri\" value=\"" +fri+ "\">");
    out.println("<input type=\"hidden\" name=\"sat\" value=\"" +sat+ "\">");
    out.println("<input type=\"hidden\" name=\"sun\" value=\"" +sun+ "\">");
    out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\"></form>");

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
 
} // end servlet
