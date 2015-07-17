/***************************************************************************************
 *   Proshop_notify: This servlet will allow the proshop to make notifications on behalf
 * 			   of members
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       11/20/2006 by Paul
 *
 *
 *   Last Updated:  
 *
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
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

import com.foretees.common.Utilities;



public class Proshop_notify extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
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


    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    ResultSet rs = null;
    Statement stmt = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, please contact customer support.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String smm = req.getParameter("mm");
    String syy = req.getParameter("yy");
    String sdd = req.getParameter("dd");
    int mm = 0;
    int yy = 0;
    int dd = 0;
    
    try {

        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        dd = Integer.parseInt(sdd);
    }
    catch (NumberFormatException e) { }

    int date = (dd == 0) ? 0 : (yy * 10000) + (mm * 100) + (dd * 1);
    
    //
    //  Array to hold the course names
    //
    ArrayList<String> course = new ArrayList<String>();      // unlimited courses
    
   // int cMax = 21;                                       // max of 20 courses plus allow room for '-ALL-'
   // String [] course = new String [cMax];
    
    
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

    // Define parms
    String courseName = "";
    String defaultCourse = "";
    String firstCourse = "";
    String adv_zone = "";
    int multi = 0;
    int index = 0;

    defaultCourse = req.getParameter("course");
    if (defaultCourse == null) defaultCourse = "";
    
    //
    // Get the 'Days In Advance' info from the club db
    //
    try {

        stmt = con.createStatement();        // create a statement

        rs = stmt.executeQuery("SELECT multi, adv_zone " +
                             "FROM club5 " +
                             "WHERE clubName != ''");

        if (rs.next()) {

            multi = rs.getInt(1);
            adv_zone = rs.getString(2);
            
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
            return;
            
        }
        stmt.close();

        if (multi != 0) {           // if multiple courses supported for this club

            course = Utilities.getCourseNames(con);     // get all the course names
        }
    }
    catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading courses.", exc.getMessage(), out, false);
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
    int today = day;                              // save today's number
    month = month + 1;                            // month starts at zero
    String day_name = day_table[day_num];         // get name for day
    int numDays = numDays_table[month];           // number of days in month

    if (numDays == 0) {                           // if Feb

        int leapYear = year - 2000;      
        numDays = feb_table[leapYear];             // get days in Feb
    }

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

    int count = 0;                     // init day counter
    int col = 0;                       // init column counter

    // get clubs current local time in hhmm format
    cal = new GregorianCalendar();
    int hr = cal.get(Calendar.HOUR_OF_DAY);           // 24 hr clock (0 - 23)
    int min = cal.get(Calendar.MINUTE);
    int sec = cal.get(Calendar.SECOND);
    if (sec > 30 && min != 59) min++;
    
    // adjust time to clubs timezone
    int ltime = (hr * 100) + min;
    ltime = SystemUtils.adjustTime(con, ltime);
    if (ltime < 0) ltime = 0 - ltime;

    hr = ltime / 100; // get time back into seperate variables
    min = ltime - (hr * 100);
    
    String ampm = " AM";
    if (hr == 12) ampm = " PM";
    if (hr == 0) hr = 12;
    if (hr > 12) {

       hr = hr - 12;
       ampm = " PM";
    }
        
    String time = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;
       
    out.println(SystemUtils.HeadTitle("Proshop - New Notification"));
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/timeBox-scripts.js\"></script>");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    SystemUtils.getProshopSubMenu(req, out, 0);        // required to allow submenus on this page
    
    out.println("<form method=post action=ProshopTLT_slot name=frmNotifyTime id=frmNotifyTime target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + ((dd == 0) ? "" : date) + "\" id=sdate>");
    out.println("<input type=\"hidden\" name=\"index\" value=\"" + ((dd == 0) ? "555" : "444") + "\">");
    out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
    
    out.println("<table cellpadding=2 cellspacing=0 align=center border=0>");
   
    out.println("<tr><td colspan=4 align=center><h2>Create Member Notification</h2>");
    
    out.println("<p align=\"center\">Today's date is:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</b>");

    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The Server Time is:&nbsp;&nbsp;<b>" + s_time);

    if (cal_am_pm == 0) {
        out.println(" AM");
    } else {
        out.println(" PM");
    }
    out.println(" " + adv_zone + "</b></p></td></tr>");
    
    out.println("</table><br>");

    out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

    out.println("</td>\n<tr>\n</table>");

    out.println("<table align=center>");
    
    //out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
    
    out.println("<tr><td><b>Selected Date:&nbsp;</b><input type=text value=\"" + ((dd == 0) ? "" : yy + "-" + mm + "-" + dd) + "\" name=cal_box_0 id=cal_box_0 size=10></td></tr>");
    
    out.println("</table>");
    out.println("<table align=center>");
    
    out.println("<tr><td><b>Arrival Time:&nbsp;</b><input type=text name=stime value=\"" + time + "\" onclick=\"TB_setCaretPos(this)\" style=\"height: 22px\" size=9></td>");
    
    out.println("<td><image src=/" + rev + "/images/up.gif onclick=\"TB_adjustTime(1, TB_box_1)\" width=17 height=8><br>");
    out.println("<image src=/" + rev + "/images/shim.gif height=2 width=1><br>");
    out.println("<image src=/" + rev + "/images/down.gif onclick=\"TB_adjustTime(-1, TB_box_1)\" width=17 height=8></td></tr>");
    
    out.println("</table>");
    out.println("<table align=center>");
    
    if (multi != 0) {

        firstCourse = (defaultCourse.equals("")) ? course.get(0) : defaultCourse;
        
        out.println("<tr><td align=center><b>Course:</b>&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"course\">");

        for (index=0; index < course.size(); index++) {

            courseName = course.get(index);      // get course name from array

            if (courseName.equals( firstCourse )) {
                out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
            } else {
                out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
        } // end while

        out.println("</select></td></tr>");
        
    } else {
        
        out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
        
    } // end if multi
    
    out.println("<tr><td align=center colspan=2><br>" +
            "<input type=button value=\"Create Notification\" onclick=\"submitForm()\" style=\"background-color: #8B8970\">" +
            "</form>" +
            "<form method=get action=\"Proshop_dsheet\" target=\"_top\">" + 
            "<input type=hidden name=index value=\"0\">" + 
            "<input type=hidden name=course value=\"" + firstCourse + "\">" + 
            "<input type=submit value=\"Manage Notifications\" style=\"background-color: #8B8970\">" +
            "</form></td>");
    out.println("</tr>");

    out.println("</table>");

    out.println("</body>");
    out.println("</html>");

    Calendar cal_date = new GregorianCalendar();
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

    out.println("<script type=\"text/javascript\">");

    out.println("var g_cal_bg_color = '#F5F5DC';");
    out.println("var g_cal_header_color = '#8B8970';");
    out.println("var g_cal_border_color = '#8B8970';");

    out.println("var g_cal_count = 1;"); // number of calendars on this page
    out.println("var g_cal_year = new Array(g_cal_count - 1);");
    out.println("var g_cal_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
    out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
    out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

    // set calendar date parts
    out.println("g_cal_month[0] = " + ( (mm==0) ? cal_month : mm ) + ";");
    out.println("g_cal_year[0] = " + ( (yy==0) ? cal_year : yy ) + ";");
    
    out.println("g_cal_beginning_month[0] = " + cal_month + ";");
    out.println("g_cal_beginning_year[0] = " + cal_year + ";");
    out.println("g_cal_beginning_day[0] = " + cal_day + ";");

    cal_date.add(Calendar.MONTH, -1); // subtract a month
    cal_date.add(Calendar.YEAR, 1); // add a year
    cal_year = cal_date.get(Calendar.YEAR);
    cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

    out.println("g_cal_ending_month[0] = " + cal_month + ";");
    out.println("g_cal_ending_day[0] = " + cal_day + ";");
    out.println("g_cal_ending_year[0] = " + cal_year + ";");

    out.println("</script>");

    out.println("<script type=\"text/javascript\">");
    out.println("var is_gecko = /gecko/i.test(navigator.userAgent);");
    out.println("var is_ie    = /MSIE/.test(navigator.userAgent);");
    out.println("var TB_box_1 = document.frmNotifyTime.stime;");
    out.println("var TB_caret_pos = 0;");
    out.println("function submitForm() {");
    out.println(" var f = document.getElementById(\"frmNotifyTime\");");
    out.println(" if (f.stime.value == '' || f.sdate.value == '') {");
    out.println("  alert('You must provide a date and time you wish to make the notification for.');");
    out.println("  return;");
    out.println(" }");
    out.println(" f.submit();");
    out.println("}");
    //out.println("function goManage() {");
    //out.println(" var f = document.getElementById(\"frmGoManage\");");
    //out.println(" f.submit();");
    //out.println(" document.location.href=\"Proshop_dsheet?index=0&course=" + defaultCourse + "\";");
    //out.println("}");
    out.println("function sd(pCal, pMonth, pDay, pYear) {");
    out.println("  f = document.getElementById(\"cal_box_\"+pCal);");
    out.println("  f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
    out.println("  var d = (pYear * 10000) + (pMonth * 100) + (pDay * 1);");
    out.println("  document.getElementById(\"sdate\").value = d;");
    out.println("}");
    out.println("</script>");
    
    out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
    
 } // end of doPost routine
 
} // end servlet public class