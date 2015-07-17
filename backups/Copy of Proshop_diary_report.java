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
import java.text.DateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.client.action.ActionHelper;


public class Proshop_diary_report extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    private PrintWriter out;
    
    
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
    
    //PrintWriter out = resp.getWriter();                           // normal output stream
    out = resp.getWriter();
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }

    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "");
        return;
    }
    
    // see what we are here to do, or try to do
    
    // if there is a start date, an end date and a valid courseName then display report
    // othewise display the report request form
    // scrub dates, start start must be before end date, etc...
    
    if (req.getParameter("cal_box_0") == null || 
        req.getParameter("cal_box_1") == null || 
        req.getParameter("course") == null) { 
        displayReportForm(con);
    } else {
        displayReport(con, req, resp);
    }
    
    
    
 } // end of doPost routine
 
 private void displayReport(Connection con, HttpServletRequest req, HttpServletResponse resp) {
     
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
    String notes = "";
    String diary_date = "";
    String courseName = req.getParameter("course");
    String start_date = req.getParameter("cal_box_0");
    String end_date = req.getParameter("cal_box_1");
    String [] ary_conditions = new String [4];
    int [] ary_condition_totals = new int [4];
    ary_conditions[0] = "";
    ary_conditions[1] = "Good";
    ary_conditions[2] = "Fair";
    ary_conditions[3] = "Poor";
    
    // get a recordset of the report data
    try {
        pstmtc = con.prepareStatement("SELECT * FROM diary WHERE diary_date >= ? AND diary_date <= ?"); 
        pstmtc.clearParameters();
        pstmtc.setString(1, start_date);
        pstmtc.setString(2, end_date);
        rs = pstmtc.executeQuery();

        out.println("<center>Raw Diary Report</center>");
        out.println("<table border=1 width=640 align=center>");
        while (rs.next()) {

            weather_am = rs.getInt("weather_am");
            weather_mid = rs.getInt("weather_mid");
            weather_pm = rs.getInt("weather_pm");
            notes = rs.getString("notes");
            diary_date = rs.getString("diary_date");
            
            out.println("<tr><td>");
            out.println("<table border=0 width=100%>");
            out.println("<tr valign=top><td width=150 nowrap>" + diary_date + "</td>" +
                        "<td width=150>AM Weather: " + ary_conditions[weather_am] + 
                        "<br>Mid Weather: " + ary_conditions[weather_mid] + 
                        "<br>PM Weather: " + ary_conditions[weather_pm] + 
                        "</td><td width=340>Notes: " + notes + "</td></tr>");
            //out.println("<tr><td colspan=3 align=center>-</td></tr>");
            out.println("</table>");
            out.println("</td></tr>");
            
            ary_condition_totals[weather_am]++;
            ary_condition_totals[weather_mid]++;
            ary_condition_totals[weather_pm]++;
            
        }
        out.println("</table>");

        out.println("<br><table align=center border=1><tr><td align=center>Summary</td></tr><tr><td><br>" +
                    "Total Goods: " + ary_condition_totals[1] + "<br>" +
                    "Total Fairs: " + ary_condition_totals[2] + "<br>" +
                    "Total Poors: " + ary_condition_totals[3] + "<br>" +
                    "</td></tr></table>");
        
        pstmtc.close();
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage());
        displayDatabaseErrMsg("Error loading diary for report", e.getMessage());
        return;
    }
    
    
 }
 
 private void displayReportForm(Connection con) {
     
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
    String [] course = new String [20];                             // max of 20 courses per club

    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");
        if (rs.next()) {
            multi = rs.getInt(1);
        } else {
            //SystemUtils.displayDatabaseErrMsg("Club setup not complete.", "");
            displayDatabaseErrMsg("Club setup not complete.", "");
            return;
        }

        stmt.close();

        if (multi != 0) {                                           // if multiple courses supported for this club

            while (i< 20) {
                course[i] = "";                                 // init the course array
                i++;
            }

            i = 0;

            //
            //  Get the names of all courses for this club
            //

            stmt = con.createStatement();                           // create a statement
            rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && i < 20) {
                courseName = rs.getString(1);
                course[i] = courseName;                         // add course name to array
                i++;
            }

            courseCount = i;                                    // remember the number of courses

            stmt.close();

            //
            //  Add an 'ALL' option at the end of the list
            //
            if (i < 20) { course[i] = "-ALL-"; }

        }
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading course information.", e.getMessage());
        displayDatabaseErrMsg("Error loading course information.", e.getMessage());
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
    end_month = cal.get(Calendar.MONTH);
    end_day = cal.get(Calendar.DAY_OF_MONTH);


    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    
    // javascript variables for dynamic calendars
    out.println("<script language=\"javascript\">\n");
    // globals
    out.println("var g_cal_bg_color = '#E7E8CA';\n");
    out.println("var g_cal_header_color = '#7D7272';\n");
    out.println("var g_cal_border_color = '#7D7272';\n");

    out.println("var g_cal_count = 2;");
    out.println("var g_cal_year = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_month = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_ending_month = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_ending_day = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);\n");
    out.println("var g_cal_ending_year = new Array(g_cal_count - 1);\n");

    // set calendar date parts
    out.println("g_cal_month[0] = " + start_month + ";\n");
    out.println("g_cal_year[0] = " + start_year + ";\n");
    out.println("g_cal_beginning_month[0] = 1;\n");
    out.println("g_cal_beginning_year[0] = 2000;\n");
    out.println("g_cal_beginning_day[0] = 0;\n");
    out.println("g_cal_ending_month[0] = " + end_month + ";\n");
    out.println("g_cal_ending_day[0] = " + end_day + ";\n");
    out.println("g_cal_ending_year[0] = " + end_year + ";\n");
    
    out.println("g_cal_month[1] = " + end_month + ";\n");
    out.println("g_cal_year[1] = " + end_year + ";\n");
    out.println("g_cal_beginning_month[1] = 1;\n");
    out.println("g_cal_beginning_year[1] = 2000;\n");
    out.println("g_cal_beginning_day[1] = 0;\n");
    out.println("g_cal_ending_month[1] = " + end_month + ";\n");
    out.println("g_cal_ending_day[1] = " + end_day + ";\n");
    out.println("g_cal_ending_year[1] = " + end_year + ";\n");
    
    out.println("</script>\n");
    
    // this is the form that gets posted back here for viewing a report
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_diary_report\" method=\"post\" name=\"frmDiaryReport\">");
    out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
    
    out.println("<br><br><table width=500 align=center>");
    
    out.println("<tr><td colspan=2 align=center><font size=4><b>Diary Reports</b></font><br></td></tr>");
    
    i = 0;
    courseName = course[i];      // get first course name from array
    out.println("<tr><td colspan=2 align=center>");
    out.println("<b>Course:</b>&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"course\" style=\"width: 200px\">");
    while ((!courseName.equals( "" )) && (i < 20)) {
        out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
        i++;
        courseName = course[i];
    }
    out.println("</select><br><br>");
    out.println("</td></tr>");
    
    out.println("<tr><td align=center><b>Starting Date</td><td align=center><b>Ending Date</td></tr>");
    out.println("<tr><td align=center>");
    out.println("<div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println("</td><td align=center>");
    out.println("<div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
    out.println("</td></tr><tr>");
    out.println("<td align=center>");
    out.println("<input type=text name=cal_box_0 id=cal_box_0>\n");
    out.println("</td><td align=center>");
    out.println("<input type=text name=cal_box_1 id=cal_box_1>\n");
    out.println("</td></tr>");
    
    out.println("<tr><td colspan=2 align=center><br><input type=button onclick=\"submitForm()\" value=\"  Get Report  \" style=\"width: 120px\">");
    out.println("</td></tr>");
    out.println("</table>");
    
    out.println("</form>");
    
    // start calanedar
    out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>\n");
    out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>\n");

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
 
 private void displayDatabaseErrMsg(String pMessage, String pException) {
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

} // end servlet public class