/***************************************************************************************
 *   Proshop_diary: This servlet will implement the diary functionality
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       2/19/2005 by Paul
 *
 *
 *   Last Updated:  
 *
 *          5/19/10  Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *          7/23/08  Removed limitation of 31 day reporting window (now limited to 1 year)
 *          7/18/08  Added limited access proshop users checks
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.Math;
import java.text.DateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_diary_report extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }

    Connection con = Connect.getCon(req);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
   
    // see what we are here to do, or try to do
    
    // if there is a start date, an end date and a valid courseName then display report
    // othewise display the report request form
    // scrub dates, start start must be before end date, etc...
    
    startPageOutput(out);
    SystemUtils.getProshopSubMenu(req, out, lottery);               // required to allow submenus on this page
    
    String detail = (req.getParameter("detail") != null) ? req.getParameter("detail")  : "";
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";    
    String calBox0 = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String calBox1 = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    
    if (req.getParameter("cal_box_0") == null) {
    
        // initial page load since cal_box is not here yet
        displayReportForm("", con, out);
        
    } else if (calBox0.equals("") || calBox1.equals("") || req.getParameter("course") == null) {
        
        // not initial load so missing info
        displayReportForm("Missing Required Information.", con, out);
        
    } else if (detail.equals("")) { //|| excel.equals("")
        
        displaySummaryReport(con, req, resp, out);
        
    } else {
        
        displayDetailReport(con, req, resp, out);
        
    }
    
    endPageOutput(out);
    
    
 } // end of doPost routine
 
 private void displaySummaryReport(Connection con, HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
    
    // 
    //  Declare our local variables
    //
    Statement stmt = null;
    PreparedStatement pstmtc = null;
    ResultSet rs = null;
    int i = 0;
    int weather_am = 0;
    int weather_mid = 0;
    int weather_pm = 0;
    int course_am = 0;
    int course_mid = 0;
    int course_pm = 0;
    int courseCount = 0;
    String selectedCourse = req.getParameter("course");
    String courseName = "";
    
    String start_date = req.getParameter("cal_box_0");
    String end_date = req.getParameter("cal_box_1");
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    ArrayList<String> course = new ArrayList<String>();
    
    int [] ary_am_weather_total = new int [4];
    int [] ary_mid_weather_total = new int [4];
    int [] ary_pm_weather_total = new int [4];
    
    int [] ary_weather_totals = new int [4];
    int [] ary_course_totals = new int [4];
    String [] ary_conditions = new String [4];
    ary_conditions[0] = "N/A";
    ary_conditions[1] = "Good";
    ary_conditions[2] = "Fair";
    ary_conditions[3] = "Poor";
    
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;

    // make sure we have valid date parts here, if not redisplay report form
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
        displayReportForm("Invalid dates for this report.", con, out);
        return;
    }
    
    Calendar cal1 = new GregorianCalendar(start_year, start_month - 1, start_day);
    Calendar cal2 = new GregorianCalendar(end_year, end_month - 1, end_day);
    
    long fromDate = cal1.getTimeInMillis();
    long toDate  = cal2.getTimeInMillis();
    long diffInMillis = toDate - fromDate;
    long diffInDays = diffInMillis / (1000 * 60 * 60 * 24) + 1;
    
    if (diffInDays > 366) {
        
        displayReportForm("This report is limited to one year of results at a time.", con, out);
        return;
    }
    
    int reportedDays = 0;
    
    //DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\"displaySummaryReport.xls\"");
        }
    }
    catch (Exception exc) {
    }
    
    out.println("<style>");
    out.println(".reportHeader{font-family: arial; font-size: 12pt; color: white; background-color: #336633; font-weight: bold; font-style: normal}");
    out.println(".reportField{font-family: arial; font-size: 11pt; color: black; background-color: #F5F5DC; font-weight: bold; font-style: normal}");
    out.println(".reportData{font-family: arial; font-size: 11pt; color: #336633; background-color: #F5F5DC; font-weight: bold; font-style: italic}");
    out.println("</style>");
    
    
    try {

        course = Utilities.getCourseNames(con);     // get all the course names
       
        courseCount = course.size();                                        // remember the number of courses
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage());
        displayDatabaseErrMsg("Error loading course information for report.", e.getMessage(), out);
        return;        
    }
    
    i = 0; // reset counter
    int [] ary_reported_days = new int [366];
    
    // get a recordset of the weather conditions
    try {
        pstmtc = con.prepareStatement("SELECT * FROM diary WHERE diary_date >= ? AND diary_date <= ?"); 
        pstmtc.clearParameters();
        pstmtc.setString(1, start_date);
        pstmtc.setString(2, end_date);
        rs = pstmtc.executeQuery();
        
        while (rs.next()) {

            weather_am = rs.getInt("weather_am");
            weather_mid = rs.getInt("weather_mid");
            weather_pm = rs.getInt("weather_pm");
            ary_am_weather_total[weather_am]++;
            ary_mid_weather_total[weather_mid]++;
            ary_pm_weather_total[weather_pm]++;
            ary_weather_totals[weather_am]++;
            ary_weather_totals[weather_mid]++;
            ary_weather_totals[weather_pm]++;
            
            if (weather_am + weather_mid + weather_pm != 0) {
                ary_reported_days[i]++;
            } else if (!rs.getString("notes").equals("")) {
                ary_reported_days[i]++;
            }
            
            i++;
            
        }
        pstmtc.close();
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage(), out);
        displayDatabaseErrMsg("Error loading weather info for summary report: " + e.toString(), e.getMessage(), out);
        return;
    }
    
    
    
    //
    // START SUMMARY REPORT OUTPUT
    //
    out.println("<center><font face=\"Arial, Helvetica, Sans-serif\"><center><b>Diary Summary Report" +
                "<br><br>" + df_full.format(cal1.getTime()) + "&nbsp; thru &nbsp;" + df_full.format(cal2.getTime()) + "</b></font></center><br><br>"); 

    out.println("<table border=1 align=center style=\"border: 1px solid #336633\" cellpadding=0 cellspacing=0><tr><td>" + 
                "<table border=0 cellpadding=5 cellspacing=0>" +
                "<tr class=reportField>" +
                    "<td></td>" +
                    "<td colspan=3 align=center>AM</td>" +
                    "<td></td>" +
                    "<td colspan=3 align=center>Mid</td>" +
                    "<td></td>" +
                    "<td colspan=3 align=center>PM</td>" +
                "</tr>");
    out.println("<tr class=reportField>" +
                    "<td></td>" +
                    "<td>Good</td>" +
                    "<td>Fair</td>" +
                    "<td>Poor</td>" +
                    "<td></td>" +
                    "<td>Good</td>" +
                    "<td>Fair</td>" +
                    "<td>Poor</td>" +
                    "<td></td>" +
                    "<td>Good</td>" +
                    "<td>Fair</td>" +
                    "<td>Poor</td>" +
                "</tr>");
    out.println("<tr class=reportData>" +
                    "<td class=reportField nowrap>Weather Conditions:</td>" +
                    "<td width=30 align=center>" + ary_am_weather_total[1] + "</td>" +
                    "<td width=30 align=center>" + ary_am_weather_total[2] + "</td>" +
                    "<td width=30 align=center>" + ary_am_weather_total[3] + "</td>" +
                    "<td></td>" +
                    "<td width=30 align=center>" + ary_mid_weather_total[1] + "</td>" +
                    "<td width=30 align=center>" + ary_mid_weather_total[2] + "</td>" +
                    "<td width=30 align=center>" + ary_mid_weather_total[3] + "</td>" +
                    "</td><td>" +
                    "<td width=30 align=center>" + ary_pm_weather_total[1] + "</td>" +
                    "<td width=30 align=center>" + ary_pm_weather_total[2] + "</td>" +
                    "<td width=30 align=center>" + ary_pm_weather_total[3] + "</td>" +
                "</tr>");
    
    // see if we are either displaying ALL course conditions or just one course
    if (selectedCourse.equals("-ALL-")) {

        for (i = 0; i < courseCount; i++) {

            int [] ary_am_course_totals = new int [4];
            int [] ary_mid_course_totals = new int [4];
            int [] ary_pm_course_totals = new int [4];
            
            try {
                pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date >= ? AND diary_date <= ? AND course_name = ?"); 
                pstmtc.clearParameters();
                pstmtc.setString(1, start_date);
                pstmtc.setString(2, end_date);
                pstmtc.setString(3, course.get(i));
                rs = pstmtc.executeQuery();
                int z = 0; // reset counter
                while (rs.next()) {
                    course_am = rs.getInt("am_condition");
                    course_mid = rs.getInt("mid_condition");
                    course_pm = rs.getInt("pm_condition");
                    ary_am_course_totals[course_am]++;
                    ary_mid_course_totals[course_mid]++;
                    ary_pm_course_totals[course_pm]++;
                    
                    if (course_am + course_mid + course_pm != 0) ary_reported_days[z]++;
                    
                    z++;
                    
                }
                pstmtc.close();
            }
            catch (Exception e) {
                displayDatabaseErrMsg("Error loading course conditions info for summary report", e.getMessage(), out);
                return;
            }
            
            out.println("<tr class=reportData><td class=reportField>" + course.get(i) + " Conditions:</td>" +
                        "<td width=30 align=center>" + ary_am_course_totals[1] + "</td>" +
                        "<td width=30 align=center>" + ary_am_course_totals[2] + "</td>" +
                        "<td width=30 align=center>" + ary_am_course_totals[3] + "</td>" +
                        "<td></td>" +
                        "<td width=30 align=center>" + ary_mid_course_totals[1] + "</td>" +
                        "<td width=30 align=center>" + ary_mid_course_totals[2] + "</td>" +
                        "<td width=30 align=center>" + ary_mid_course_totals[3] + "</td>" +
                        "</td><td>" +
                        "<td width=30 align=center>" + ary_pm_course_totals[1] + "</td>" +
                        "<td width=30 align=center>" + ary_pm_course_totals[2] + "</td>" +
                        "<td width=30 align=center>" + ary_pm_course_totals[3] + "</td></tr>");
        
        } // end courseCount for loop
    
    } else {
        
        int [] ary_am_course_totals = new int [4];
        int [] ary_mid_course_totals = new int [4];
        int [] ary_pm_course_totals = new int [4];
            
        // display single course
        try {
          
           if (courseCount > 1) {

               pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date >= ? AND diary_date <= ? AND course_name = ?");
               pstmtc.clearParameters();
               pstmtc.setString(1, start_date);
               pstmtc.setString(2, end_date);
               pstmtc.setString(3, selectedCourse);
              
            } else {
              
               pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date >= ? AND diary_date <= ?");
               pstmtc.clearParameters();
               pstmtc.setString(1, start_date);
               pstmtc.setString(2, end_date);
            }

            rs = pstmtc.executeQuery();
            i = 0; // reset counter
            while (rs.next()) {
                course_am = rs.getInt("am_condition");
                course_mid = rs.getInt("mid_condition");
                course_pm = rs.getInt("pm_condition");
                ary_am_course_totals[course_am]++;
                ary_mid_course_totals[course_mid]++;
                ary_pm_course_totals[course_pm]++;
                if (course_am + course_mid + course_pm != 0) reportedDays++; // ary_reported_days[i]++;
                i++;
            }
            pstmtc.close();
        }
        catch (Exception e) {
            displayDatabaseErrMsg("Error loading course conditions info for summary report", e.getMessage(), out);
            return;
        }
        out.println("<tr class=reportData>");
        if (courseCount > 1) {
           out.println("<td class=reportField>" + selectedCourse + " Conditions:</td>");
        } else {
           out.println("<td class=reportField>Course Conditions:</td>");
        }
        out.println("<td width=30 align=center>" + ary_am_course_totals[1] + "</td>" +
                    "<td width=30 align=center>" + ary_am_course_totals[2] + "</td>" +
                    "<td width=30 align=center>" + ary_am_course_totals[3] + "</td>" +
                    "<td></td>" +
                    "<td width=30 align=center>" + ary_mid_course_totals[1] + "</td>" +
                    "<td width=30 align=center>" + ary_mid_course_totals[2] + "</td>" +
                    "<td width=30 align=center>" + ary_mid_course_totals[3] + "</td>" +
                    "</td><td>" +
                    "<td width=30 align=center>" + ary_pm_course_totals[1] + "</td>" +
                    "<td width=30 align=center>" + ary_pm_course_totals[2] + "</td>" +
                    "<td width=30 align=center>" + ary_pm_course_totals[3] + "</td></tr>");
    }
/*    
    // count the reported days
    for (i=0; i < 31; i++) {
        if (ary_reported_days[i] != 0) reportedDays++;
    }
*/    
    out.println("<tr valign=bottom>" +
                "<td class=reportField><br>Total Days in Period:</td><td class=reportData>" + diffInDays + "</td>" +
                "<td colspan=10 class=reportData></td></tr>");
    
    out.println("<tr valign=bottom>" +
                "<td class=reportField>Total Days Reported:</td><td class=reportData>" + reportedDays + "</td>" +
                "<td colspan=10 class=reportData></td></tr>");
    
    out.println("</table>\n</td></tr></table>");            // end report table
    
    
    if (excel.equals("")) {
        out.println("<br><br><center><form method=\"post\" action=\"Proshop_diary_report\" target=\"_self\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + selectedCourse + "\">");
        out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
        out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print()\" style=\"text-decoration:underline; background:#8B8970; width:75px\">&nbsp; &nbsp;");
        out.println("<input type=\"button\" value=\"Excel\" onclick=\"viewExcel()\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
        out.println("<input type=\"button\" value=\"Detailed Report\" onclick=\"switchReports()\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
        out.println("<input type=\"button\" value=\"Back\" onclick=\"document.location.href='Proshop_diary_report'\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp;");
        out.println("<input type=\"button\" value=\"Home\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center><br><br>");
        
        out.println("<script type=\"text/javascript\">");
        out.println("function switchReports() {");
        out.println(" document.forms[1].target = '_self';");
        out.println(" document.forms[1].detail.value = 'yes';");
        out.println(" document.forms[1].excel.value = '';");
        out.println(" document.forms[1].submit();");
        out.println("}");
        out.println("function viewExcel() {");
        out.println(" document.forms[1].target = '_blank';");
        out.println(" document.forms[1].excel.value = 'yes';");
        out.println(" document.forms[1].detail.value = '';");
        out.println(" document.forms[1].submit();");
        out.println("}");
        out.println("</script>");
    }
    
 }
 private void displayDetailReport(Connection con, HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {

    // 
    //  Declare our local variables
    //
    Statement stmt = null;
    PreparedStatement pstmtc = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    int i = 0;
    int weather_am = 0;
    int weather_mid = 0;
    int weather_pm = 0;
    int course_am = 0;
    int course_mid = 0;
    int course_pm = 0;
    int [] ary_condition_totals = new int [4];
    int courseCount = 0;
    
    String notes = "";
    String diary_date = "";
    String courseName = "";
    String start_date = req.getParameter("cal_box_0");
    String end_date = req.getParameter("cal_box_1");
    String selectedCourse = req.getParameter("course");
    
    ArrayList<String> course = new ArrayList<String>();
    String [] ary_conditions = new String [4];
    
    ary_conditions[0] = "N/A";
    ary_conditions[1] = "Good";
    ary_conditions[2] = "Fair";
    ary_conditions[3] = "Poor";
    
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;

    // make sure we have valid date parts here, if not redisplay report form
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
        displayReportForm("Invalid dates for this report.", con, out);
        return;
    }
    
    Calendar cal1 = new GregorianCalendar(start_year, start_month - 1, start_day);
    Calendar cal2 = new GregorianCalendar(end_year, end_month - 1, end_day);
    
    
    //
    // Start page output
    //
    
    out.println("<style>");
    out.println(".reportHeader{font-family: arial; font-size: 12pt; color: white; background-color: #336633; font-weight: bold};");
    out.println(".reportField{font-family: arial; font-size: 11pt; color: black; background-color: #F5F5DC; font-weight: bold};");
    out.println(".reportData{font-family: arial; font-size: 11pt; color: #336633; background-color: #F5F5DC; font-weight: bold; font-style: italic");
    out.println("</style>");
    
    try {

        course = Utilities.getCourseNames(con);     // get all the course names
            
        courseCount = course.size();                // remember the number of courses
       
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage());
        displayDatabaseErrMsg("Error loading course information for report.", e.getMessage(), out);
        return;
        
    }
    
    // get a recordset of the report data
    try {
        pstmtc = con.prepareStatement("SELECT * FROM diary WHERE diary_date >= ? AND diary_date <= ? ORDER BY diary_date"); 
        pstmtc.clearParameters();
        pstmtc.setString(1, start_date);
        pstmtc.setString(2, end_date);
        rs = pstmtc.executeQuery();
        
        if (courseCount > 1) {

           out.println("<center><font face=\"Arial, Helvetica, Sans-serif\"><b>Detailed Diary Report" +
                       ((selectedCourse.equals("-ALL-")) ? "<br>All Courses" : "<br>" + selectedCourse + " Course") +
                       "<br><br>" + df_full.format(cal1.getTime()) + "&nbsp; thru &nbsp;" + df_full.format(cal2.getTime()) + "</b></font></center><br><br>");
        
        } else {

           out.println("<center><font face=\"Arial, Helvetica, Sans-serif\"><b>Detailed Diary Report" +
                       "<br><br>" + df_full.format(cal1.getTime()) + "&nbsp; thru &nbsp;" + df_full.format(cal2.getTime()) + "</b></font></center><br><br>");
        }

        out.println("<table border=1 align=center style=\"border: 1px solid #336633\" cellpadding=0 cellspacing=0>");
        while (rs.next()) {

            weather_am = rs.getInt("weather_am");
            weather_mid = rs.getInt("weather_mid");
            weather_pm = rs.getInt("weather_pm");
            notes = rs.getString("notes");
            diary_date = rs.getString("diary_date");
            
            out.println("<tr><td>");
            
            out.println("<table width=570 border=0 cellpadding=5 cellspacing=0>"); // 525 width + 45 padding
            out.println("<tr><td width=90 class=reportField nowrap>" + diary_date + "</td>" +
                        "<td width=160 class=reportField nowrap>Weather Conditions:</td>" + "<td width=5 class=reportField>&nbsp;</td>" + 
                        "<td width=35 class=reportField>AM:</td><td width=55 class=reportData>" + ary_conditions[weather_am] + "</td>" + 
                        "<td width=35 class=reportField>Mid:</td><td width=55 class=reportData>" + ary_conditions[weather_mid] + "</td>" + 
                        "<td width=35 class=reportField>PM:</td><td width=55 class=reportData>" + ary_conditions[weather_pm] + "</td></tr>");
            
            // show either the selected course or all courses condition data for this date
            
            if (selectedCourse.equals("-ALL-")) {
            
                for (i = 0; i < courseCount; i++) {
            
                    try {
                        pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date = ? AND course_name = ?"); 
                        pstmtc.clearParameters();
                        pstmtc.setString(1, diary_date);
                        pstmtc.setString(2, course.get(i));
                        rs2 = pstmtc.executeQuery();
                        while (rs2.next()) {
                            course_am = rs2.getInt("am_condition");
                            course_mid = rs2.getInt("mid_condition");
                            course_pm = rs2.getInt("pm_condition");

                            out.println("<tr><td class=reportField></td>" +
                                        "<td width=160 class=reportField nowrap>" + course.get(i) + " Conditions:</td>" + "<td width=5 class=reportField>&nbsp;</td>" + 
                                        "<td width=35 class=reportField>AM:</td><td width=55 class=reportData>" + ary_conditions[course_am] + "</td>" + 
                                        "<td width=35 class=reportField>Mid:</td><td width=55 class=reportData>" + ary_conditions[course_mid] + "</td>" + 
                                        "<td width=35 class=reportField>PM:</td><td width=55 class=reportData>" + ary_conditions[course_pm] + "</td></tr>");
                        }
                        pstmtc.close();
                    }
                    catch (Exception e) {
                        displayDatabaseErrMsg("Error loading course conditions info for detail report", e.getMessage(), out);
                        return;
                    }

                }   
            } else {
                
                try {
                  
                   if (courseCount > 1) {

                       pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date = ? AND course_name = ?");
                       pstmtc.clearParameters();
                       pstmtc.setString(1, diary_date);
                       pstmtc.setString(2, selectedCourse);
                       rs2 = pstmtc.executeQuery();
                       while (rs2.next()) {
                           course_am = rs2.getInt("am_condition");
                           course_mid = rs2.getInt("mid_condition");
                           course_pm = rs2.getInt("pm_condition");

                           out.println("<tr><td class=reportField></td>" +
                                       "<td width=160 class=reportField nowrap>" + selectedCourse + " Conditions:</td>" + "<td width=5 class=reportField>&nbsp;</td>" +
                                       "<td width=35 class=reportField>AM:</td><td width=55 class=reportData>" + ary_conditions[course_am] + "</td>" +
                                       "<td width=35 class=reportField>Mid:</td><td width=55 class=reportData>" + ary_conditions[course_mid] + "</td>" +
                                       "<td width=35 class=reportField>PM:</td><td width=55 class=reportData>" + ary_conditions[course_pm] + "</td></tr>");
                       }
                       pstmtc.close();
                         
                    } else {
                      
                       pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date = ?");
                       pstmtc.clearParameters();
                       pstmtc.setString(1, diary_date);
                       rs2 = pstmtc.executeQuery();
                       while (rs2.next()) {
                           course_am = rs2.getInt("am_condition");
                           course_mid = rs2.getInt("mid_condition");
                           course_pm = rs2.getInt("pm_condition");

                           out.println("<tr><td class=reportField></td>" +
                                       "<td width=160 class=reportField nowrap>Course Conditions:</td>" + "<td width=5 class=reportField>&nbsp;</td>" +
                                       "<td width=35 class=reportField>AM:</td><td width=55 class=reportData>" + ary_conditions[course_am] + "</td>" +
                                       "<td width=35 class=reportField>Mid:</td><td width=55 class=reportData>" + ary_conditions[course_mid] + "</td>" +
                                       "<td width=35 class=reportField>PM:</td><td width=55 class=reportData>" + ary_conditions[course_pm] + "</td></tr>");
                       }
                       pstmtc.close();
                    }
                }
                catch (Exception e) {
                    displayDatabaseErrMsg("Error loading course conditions info for detail report", e.getMessage(), out);
                    return;
                }
            
            }
            
            out.println("<tr valign=top><td colspan=9 class=reportField>Notes: &nbsp;<font class=reportData>" + ((notes.equals("")) ? "N/A" : notes) + "</font></td></tr>");
            out.println("</table>");
            
            out.println("</td></tr>");
            
            out.println("<tr><td><img src=\"/" + rev + "/images/shim.gif\" width=1 height=5></td></tr>");
            
            ary_condition_totals[weather_am]++;
            ary_condition_totals[weather_mid]++;
            ary_condition_totals[weather_pm]++;
            
        }
        out.println("</table><br><br>");

        
        if (req.getParameter("excel") == null || req.getParameter("excel") != "yes") {
            out.println("<center><form method=\"post\" action=\"Proshop_diary_report\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + selectedCourse + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
            out.println("<input type=\"button\" value=\"Print\" onclick=\"window.print()\" style=\"text-decoration:underline; background:#8B8970; width:75px\">&nbsp; &nbsp;");
            out.println("<input type=\"submit\" value=\"Summary\" style=\"text-decoration:underline; background:#8B8970; width:100px\">&nbsp; &nbsp;");
            out.println("<input type=\"button\" value=\"Home\" onclick=\"document.location.href='Proshop_announce'\" style=\"text-decoration:underline; background:#8B8970; width:75px\">");
            out.println("</form></center><br><br>");
        }
        
        pstmtc.close();
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage());
        displayDatabaseErrMsg("Error loading diary for report", e.getMessage(), out);
        return;
    }
    
    
 }
 
 private void displayReportForm(String msg, Connection con, PrintWriter out) {
     
    // 
    //  Declare our local variables
    //
    int start_year = 0;
    int start_month = 0;
    int start_day = 0;
    int end_year = 0;
    int end_month = 0;
    int end_day = 0;
    int i = 0;
    int multi = 0;
    int courseCount = 0;

    Statement stmt = null;
    PreparedStatement pstmtc = null;
    ResultSet rs = null;
    
    //
    //  Array to hold the course names
    //
    String courseName = "";

    ArrayList<String> course = new ArrayList<String>();
    

    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");
        if (rs.next()) {
            multi = rs.getInt(1);
        } else {
            //SystemUtils.displayDatabaseErrMsg("Club setup not complete.", "");
            displayDatabaseErrMsg("Club setup not complete.", "", out);
            return;
        }

        stmt.close();

        if (multi != 0) {                                           // if multiple courses supported for this club

            course = Utilities.getCourseNames(con);     // get all the course names
            
            courseCount = course.size();                // remember the number of courses
            
            //
            //  Add an 'ALL' option at the end of the list
            //
            course.add ("-ALL-");            
        }
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading course information.", e.getMessage());
        displayDatabaseErrMsg("Error loading course information.", e.getMessage(), out);
        return;
    }
    
    
    //
    //  Get today's date and current time and calculate date & time values 
    //
    Calendar cal = new GregorianCalendar();       // get todays date

    // default starting date to the 1st of this month
    start_year = cal.get(Calendar.YEAR);
    start_month = cal.get(Calendar.MONTH);
    start_day = 1;
    
    // default ending date to current date
    end_year = cal.get(Calendar.YEAR);
    end_month = cal.get(Calendar.MONTH) + 1;
    end_day = cal.get(Calendar.DAY_OF_MONTH);


    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    
    // javascript variables for dynamic calendars
    out.println("<script type=\"text/javascript\">");
    // globals
    out.println("var g_cal_bg_color = '#F5F5DC';");
    out.println("var g_cal_header_color = '#8B8970';");
    out.println("var g_cal_border_color = '#8B8970';");

    out.println("var g_cal_count = 2;");
    out.println("var g_cal_year = new Array(g_cal_count - 1);");
    out.println("var g_cal_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

    // set calendar date parts
    out.println("g_cal_month[0] = " + start_month + ";");
    out.println("g_cal_year[0] = " + start_year + ";");
    out.println("g_cal_beginning_month[0] = 1;");
    out.println("g_cal_beginning_year[0] = 2000;");
    out.println("g_cal_beginning_day[0] = 0;");
    out.println("g_cal_ending_month[0] = " + end_month + ";");
    out.println("g_cal_ending_day[0] = " + end_day + ";");
    out.println("g_cal_ending_year[0] = " + end_year + ";");
    
    out.println("g_cal_month[1] = " + end_month + ";");
    out.println("g_cal_year[1] = " + end_year + ";");
    out.println("g_cal_beginning_month[1] = 1;");
    out.println("g_cal_beginning_year[1] = 2000;");
    out.println("g_cal_beginning_day[1] = 0;");
    out.println("g_cal_ending_month[1] = " + end_month + ";");
    out.println("g_cal_ending_day[1] = " + end_day + ";");
    out.println("g_cal_ending_year[1] = " + end_year + ";");
    
    out.println("</script>\n");
    
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">\n");
    
    out.println("<center><font size=5><b>Diary Reports</b></font><br><br></center>");
    
    out.println("<table width=\"380\" border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">\n");
    out.println("<b>Diary Report</b><br>");
    out.println("<br>Use the calendars below to select a date range for the report.<br>");
    //out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
    out.println("<br>Click on <b>Go</b> to generate the report.");
    out.println("</td></tr></table><br>");
    
    // display error message if present
    if (!msg.equals("")) {
        out.println("<p align=center><font color=red><i>Error: " + msg + "</i></font></p>");
    }
    
    // this is the form that gets posted back here for viewing a report
    out.println("<form action=\"Proshop_diary_report\" method=\"post\" name=\"frmDiaryReport\">");
    out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
    
    out.println("<table width=500 align=center>");
    
    //out.println("<tr><td colspan=2 align=center><font size=4><b>Diary Reports</b></font><br></td></tr>");
    
    if (multi != 0) {
       
        out.println("<tr><td colspan=2 align=center>");
        out.println("<b>Course:</b>&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"course\" style=\"width: 200px\">");
        
        for (i = 0; i < course.size(); i++) {
           
            courseName = course.get(i);      // get first course name from array
           
            out.print("<option value=\"" + courseName + "\"");
            out.print ((courseName.equals("-ALL-")) ? " selected" : "");
            out.print (">" + courseName + "</option>");
        }
        out.println("</select><br><br>");
        out.println("</td></tr>");
    } else {
        out.println("<tr><td align=center><input type=\"hidden\" name=\"course\" value=\"" + courseName + "\"</td></tr>");
    }
    
    out.println("<tr><td align=center><b>Starting Date</td><td align=center><b>Ending Date</td></tr>");
    out.println("<tr><td align=center>");
    out.println("<div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println("</td><td align=center>");
    out.println("<div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println("</td></tr><tr>");
    out.println("<td align=center><input type=text name=cal_box_0 id=cal_box_0></td>");
    out.println("<td align=center><input type=text name=cal_box_1 id=cal_box_1></td>");
    out.println("</tr>");
    
    out.println("<tr><td colspan=2 align=center><br><input type=button onclick=\"submitForm()\" value=\"  Go  \" style=\"width: 75px\">");
    out.println("</td></tr>");
    
    out.println("<tr><td colspan=2 align=center>");
    out.println("<br><center><input type=\"button\" onclick=\"document.location.href='Proshop_announce'\" value=\"  Home  \" style=\"text-decoration:underline; background:#8B8970; width: 60px\"></center>");
    out.println("</td></tr>");
    
    out.println("</table>");
    
      
    out.println("</form>");
    
    // start calanedar
    out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>\n");
    
    out.println("<script type=\"text/javascript\">");
    out.println("function sd(pCal, pMonth, pDay, pYear) {");
    out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
    out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
    out.println("}");
    out.println("function submitForm() {");
    out.println(" var f = document.forms['frmDiaryReport'];");
    out.println(" f.submit();");
    out.println("}");
    out.println("</script>");
 }
 
 private void startPageOutput(PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Diary Reports"));
    //out.println("<html><head><title>Diary Reports</title></head><body bgcolor=white>");
 }
 
 private void endPageOutput(PrintWriter out) {
    out.println("</body></html>");
 }
 
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

} // end servlet public class
