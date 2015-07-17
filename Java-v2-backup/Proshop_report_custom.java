/***************************************************************************************
 *   Proshop_report_custom: This servlet to be used for custom reports.  Create a separate method for each particular report.
 *
 *
 *
 *   Created:       11/01/10
 *
 *
 *   Last Updated:
 *
 *      5/06/14   Oak Hill CC (oakhillcc) - Add custom report for Advance guest Times (case 2361).
 *      6/06/13   Desert Mountain (desertmountain) - Added desertMountainExport custom report to pull a set of data they have requested.
 *      6/06/13   Report created
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
import com.foretees.common.Connect;

public class Proshop_report_custom extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;

    int mpccpb_maxIntervals = 67;       //
    int mpccpb_interval = 10;
    int mpccpb_firstTime = 700;     // Running 7am - 6pm on 10min intervals (should be 67 times)
    int mpccpb_firstHr = 7;
    int mpccpb_firstMin = 0;
    int mpccpb_lastTime = 1800;


    
 
 //****************************************************
 // Process the get - initial call from menu
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

     doPost(req, resp);

 } // end of doGet routine
 
 
 //****************************************************
 // Process the post - call from get processing
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();                             // normal output stream

    String excel = (req.getParameter("excel") != null) ? "yes" : "";

    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    String club = (String)session.getAttribute("club");
        
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        }
    }
    catch (Exception exc) {
    }

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

    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";
    
    String report_title = "Data Export";
    
    if (club.equals("oakhillcc") || club.equals("demov4")) report_title = "Advance Guest Time History Report";

    //
    //  Use the custom date range provided to generate the report
    //
    if (req.getParameter("excel") != null) {
        out.println("<html><head><title>" +report_title+ "</title></head>");
    } else {
        out.println(SystemUtils.HeadTitle2(report_title));
        SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }

    // check to see if the date is here, and if not then jump to display calendar routine
    if (start_date.equals("")) {
        getCustomDate(req, club, out, con);
        return;
    } else if (club.equals("desertmountain")) {
        desertMountainExport(req, excel, out, con);
    } else if (club.equals("oakhillcc") || club.equals("demov4")) {
        oakhillAdvGstReport(req, excel, out, con);
    }
    
 } // end doPost
 
 private void desertMountainExport(HttpServletRequest req, String excel, PrintWriter out, Connection con) {
     
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";

    int index = 1;
    int index_time = 0;
    int start_year = 0;
    int start_month = 0;
    int start_day = 0;
    int end_year = 0;
    int end_month = 0;
    int end_day = 0;
    int cur_year = 0;
    int cur_month = 0;
    int cur_day = 0;
    int cur_time = 0;
    int cur_hr = 0;
    int cur_min = 0;
    int cur_day_of_week = 0;

    long edate = 0;
    long sdate = 0;
    long cur_date = 0;

    boolean incTime = false;
     

    // if no ending date then default to starting date for 1 day report
    if (end_date.equals("")) {
        end_date = start_date;
    }

    // if the date passed are in our long format already (from being passed around)
    //  then we can skip this first round of validation
    try {
        sdate = Long.parseLong(start_date);
        edate = Long.parseLong(end_date);
    } catch (Exception e) {

        // make sure the dates here are valid, if not redisplay the calendars
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

        } catch (Exception e2) {
            // invalid dates here, bailout and call form again
            getCustomDate(req, "desertmountain", out, con);
            return;
        }

        // build our date variables for use in query
        sdate = start_year * 10000;                    // create a date field of yyyymmdd
        sdate = sdate + (start_month * 100);
        sdate = sdate + start_day;

        edate = end_year * 10000;                      // create a date field of yyyymmdd
        edate = edate + end_month * 100;
        edate = edate + end_day;
    }

    if (sdate > edate) {
       // start date is after the end date, jump out and call form again
       getCustomDate(req, "desertmountain", out, con);
       return;
    }

    // Data has been gathered, output data using selected method.

    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default

    if (excel.equals("yes")) {     // if user requested Excel Spreadsheet Format

        bgrndcolor = "#FFFFFF";      // white for excel
        fontcolor = "#000000";      // black for excel
    }

    if (!excel.equals("yes")) {
        out.println("<br><br>");
        out.println("<table align=\"center\" border=\"0\"><tr><td align=\"center\">");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"background:#8B8970\">");
        out.println("</form>");
        out.println("</td></tr></table>");
        out.println("<br>");
    }

    if (excel.equals("yes")) {     // if user requested Excel Spreadsheet Format
        out.println("<table align=\"center\" border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" cols=\"8\">");
    } else {
        out.println("<table align=\"center\" border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" cols=\"8\">");
    }
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<tr bgcolor=\"" +bgrndcolor+ "\">");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Date</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Time</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Day</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Front/Back</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Course name</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Player</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Username</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Guest Accomp</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Mem Last Name</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Mem First Name</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Mem MI</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Account Name</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Mem Gender</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Guest Gender</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Membership Type</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Member Type</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Birth Date</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Holes</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Mode of Trans</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">GHIN</td>");
    out.println("</tr>");
    
    try {
        
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(DATE, '%m/%d/%y') AS 'Date', TIME, DAY, IF(fb = 0, 'Front', 'Back') AS 'Front/Back', courseName, player1 AS player, "
            + "username1 AS username, userg1 AS userg, m.name_last AS 'Mem Last Name', m.name_first AS 'Mem First Name', m.name_mi AS 'Mem MI', "
            + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS 'Account Name', m.gender AS 'Mem Gender', "
            + "gdb.gender AS 'Guest Gender', m_ship, m_type, DATE_FORMAT(birth, '%m/%d/%y') AS 'Birth DATE', IF(p91 = 1, 9, 18) AS holes, p1cw AS 'MODE of Trans', ghin "
            + "FROM teepast2 t "
            + "LEFT OUTER JOIN member2b m ON IF(t.userg1 <> '', t.userg1 = m.username, t.username1 = m.username) "
            + "LEFT OUTER JOIN guestdb_data gdb ON t.guest_id1 = gdb.guest_id "
            + "WHERE player1 <> '' AND player1 <> 'X' AND date >= ? AND date <= ? "  
            + "UNION ALL "
            + "SELECT DATE_FORMAT(DATE, '%m/%d/%y') AS 'DATE', TIME, DAY, IF(fb = 0, 'Front', 'Back') AS 'Front/Back', courseName, player2 AS player, "
            + "username2 AS username, userg2 AS userg, m.name_last AS 'Mem LAST NAME', m.name_first AS 'Mem FIRST NAME', m.name_mi AS 'Mem MI', "
            + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS 'Account NAME', m.gender AS 'Mem Gender', "
            + "gdb.gender AS 'Guest Gender', m_ship, m_type, DATE_FORMAT(birth, '%m/%d/%y') AS 'Birth DATE', IF(p92 = 1, 9, 18) AS holes, p2cw AS 'MODE of Trans', ghin "
            + "FROM teepast2 t "
            + "LEFT OUTER JOIN member2b m ON IF(t.userg2 <> '', t.userg2 = m.username, t.username2 = m.username) "
            + "LEFT OUTER JOIN guestdb_data gdb ON t.guest_id2 = gdb.guest_id "
            + "WHERE player2 <> '' AND player2 <> 'X' AND date >= ? AND date <= ? "
            + "UNION ALL "
            + "SELECT DATE_FORMAT(DATE, '%m/%d/%y') AS 'DATE', TIME, DAY, IF(fb = 0, 'Front', 'Back') AS 'Front/Back', courseName, player3 AS player, "
            + "username3 AS username, userg3 AS userg, m.name_last AS 'Mem LAST NAME', m.name_first AS 'Mem FIRST NAME', m.name_mi AS 'Mem MI', "
            + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS 'Account NAME', m.gender AS 'Mem Gender', "
            + "gdb.gender AS 'Guest Gender', m_ship, m_type, DATE_FORMAT(birth, '%m/%d/%y') AS 'Birth DATE', IF(p93 = 1, 9, 18) AS holes, p3cw AS 'MODE of Trans', ghin "
            + "FROM teepast2 t "
            + "LEFT OUTER JOIN member2b m ON IF(t.userg3 <> '', t.userg3 = m.username, t.username3 = m.username) "
            + "LEFT OUTER JOIN guestdb_data gdb ON t.guest_id3 = gdb.guest_id "
            + "WHERE player3 <> '' AND player3 <> 'X' AND date >= ? AND date <= ? " 
            + "UNION ALL "
            + "SELECT DATE_FORMAT(DATE, '%m/%d/%y') AS 'DATE', TIME, DAY, IF(fb = 0, 'Front', 'Back') AS 'Front/Back', courseName, player4 AS player, "
            + "username4 AS username, userg4 AS userg, m.name_last AS 'Mem LAST NAME', m.name_first AS 'Mem FIRST NAME', m.name_mi AS 'Mem MI', "
            + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS 'Account NAME', m.gender AS 'Mem Gender', "
            + "gdb.gender AS 'Guest Gender', m_ship, m_type, DATE_FORMAT(birth, '%m/%d/%y') AS 'Birth DATE', IF(p94 = 1, 9, 18) AS holes, p4cw AS 'MODE of Trans', ghin "
            + "FROM teepast2 t "
            + "LEFT OUTER JOIN member2b m ON IF(t.userg4 <> '', t.userg4 = m.username, t.username4 = m.username) "
            + "LEFT OUTER JOIN guestdb_data gdb ON t.guest_id4 = gdb.guest_id "
            + "WHERE player4 <> '' AND player4 <> 'X' AND date >= ? AND date <= ? "
            + "UNION ALL "
            + "SELECT DATE_FORMAT(DATE, '%m/%d/%y') AS 'DATE', TIME, DAY, IF(fb = 0, 'Front', 'Back') AS 'Front/Back', courseName, player5 AS player, "
            + "username5 AS username, userg5 AS userg, m.name_last AS 'Mem LAST NAME', m.name_first AS 'Mem FIRST NAME', m.name_mi AS 'Mem MI', "
            + "CONCAT(m.name_last, ', ', m.name_first, IF(m.name_mi <> '', CONCAT(' ', m.name_mi), '')) AS 'Account NAME', m.gender AS 'Mem Gender', "
            + "gdb.gender AS 'Guest Gender', m_ship, m_type, DATE_FORMAT(birth, '%m/%d/%y') AS 'Birth DATE', IF(p95 = 1, 9, 18) AS holes, p5cw AS 'MODE of Trans', ghin "
            + "FROM teepast2 t "
            + "LEFT OUTER JOIN member2b m ON IF(t.userg5 <> '', t.userg5 = m.username, t.username5 = m.username) "
            + "LEFT OUTER JOIN guestdb_data gdb ON t.guest_id5 = gdb.guest_id "
            + "WHERE player5 <> '' AND player5 <> 'X' AND date >= ? AND date <= ? "
            + "ORDER BY DATE, courseName, TIME, 'Mem LAST NAME'");
        
        pstmt.clearParameters();
        pstmt.setLong(1, sdate);
        pstmt.setLong(2, edate);
        pstmt.setLong(3, sdate);
        pstmt.setLong(4, edate);
        pstmt.setLong(5, sdate);
        pstmt.setLong(6, edate);
        pstmt.setLong(7, sdate);
        pstmt.setLong(8, edate);
        pstmt.setLong(9, sdate);
        pstmt.setLong(10, edate);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            out.println("<tr>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("Date") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getInt("Time") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("Day") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("Front/Back") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("courseName") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("player") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("username") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("userg") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("Mem Last Name") != null ? rs.getString("Mem Last Name") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("Mem First Name") != null ? rs.getString("Mem First Name") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("Mem MI") != null ? rs.getString("Mem Mi") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("Account Name") != null ? rs.getString("Account Name") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("Mem Gender") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("Guest Gender") != null ? rs.getString("Guest Gender") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("m_ship") != null ? rs.getString("m_ship") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("m_type") != null ? rs.getString("m_type") : "&nbsp;")+ "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("Birth Date") != null ? rs.getString("Birth Date") : "&nbsp;") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("holes") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + rs.getString("Mode of Trans") + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + (rs.getString("ghin") != null ? rs.getString("ghin") : "&nbsp;") + "</td>");
            out.println("</tr>");
        }        
        
    } catch (Exception exc) {
        Utilities.logError("Proshop_report_custom - desertmountain - Error looking up tee time data for members - ERR: " + exc.toString());
    } finally { 
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
    }

    out.println("</table>");

    if (!excel.equals("yes")) {
        out.println("<br><br>");
        out.println("<table align=\"center\" border=\"0\"><tr><td align=\"center\">");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"background:#8B8970\">");
        out.println("</form>");
        out.println("</td></tr></table>");
    }
 }


 private int incrementTime (int curr_hr, int curr_min, int interval, int num) {

     int time = 0;
     int hr = curr_hr;
     int min = curr_min;

     for (int i=0; i<num; i++) {

         min += interval;

         // If minutes go over 60, subtract 60 minutes from min value and add 1 to hr value.
         if (min >= 60) {
             hr++;
             min -= 60;
         }

         // If hour goes over 24, subtract 24 hrs from hr value
         if (hr >= 24) {
             hr -= 24;
         }
     }

     // Determine time value to return
     time = (hr * 100) + min;

     return time;
 }
 
 
 
 //*****************************************************************************
 //
 //   Oak Hill CC - custom report to list the history of any Advance Guest Times (case 2361)
 //
 //*****************************************************************************
 //
 private void oakhillAdvGstReport(HttpServletRequest req, String excel, PrintWriter out, Connection con) {
     
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";

    int start_year = 0;
    int start_month = 0;
    int start_day = 0;
    int end_year = 0;
    int end_month = 0;
    int end_day = 0;
    int time = 0;
    int fb = 0;
    int last_time = 0;
    int last_fb = 0;
    int type = 0;
    int mm = 0;
    int dd = 0;
    int yy = 0;
    int hr = 0;
    int min = 0;

    long edate = 0;
    long sdate = 0;
    long date = 0;
    long last_date = 0;
    long mdate = 0;
    
    String last_course = "";
    String course = "";
    String day = "";
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String user = "";
    String mname = "";
    String Sdate = "";
    String notes = "";
    String sfb = "";
    String stype = "";
    String ampm = "";

    boolean incTime = false;
     

    // if no ending date then default to starting date for 1 day report
    if (end_date.equals("")) {
        end_date = start_date;
    }

    // if the date passed are in our long format already (from being passed around)
    //  then we can skip this first round of validation
    try {
        sdate = Long.parseLong(start_date);
        edate = Long.parseLong(end_date);
    } catch (Exception e) {

        // make sure the dates here are valid, if not redisplay the calendars
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

        } catch (Exception e2) {
            // invalid dates here, bailout and call form again
            getCustomDate(req, "oakhillcc", out, con);
            return;
        }

        // build our date variables for use in query
        sdate = start_year * 10000;                    // create a date field of yyyymmdd
        sdate = sdate + (start_month * 100);
        sdate = sdate + start_day;

        edate = end_year * 10000;                      // create a date field of yyyymmdd
        edate = edate + end_month * 100;
        edate = edate + end_day;
    }

    if (sdate > edate) {
       // start date is after the end date, jump out and call form again
       getCustomDate(req, "oakhillcc", out, con);
       return;
    }

    // Data has been gathered, output data using selected method.

    String tablecolor = "#F5F5DC";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default

    if (excel.equals("yes")) {     // if user requested Excel Spreadsheet Format

        tablecolor = "#FFFFFF";
        bgrndcolor = "#FFFFFF";      // white for excel
        fontcolor = "#000000";      // black for excel
    }
               
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");    
    out.println("<table align=\"center\"><tr><td align=\"center\">");

    out.println("<br><p align=\"center\"><H2>Advance Guest Time Report</H2></p>");
    
    if (!excel.equals("yes")) {
       
        out.println("<p align=\"center\">");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"background:#8B8970\">");
        out.println("</form></p>");
    }
    
    try {
        
        pstmt = con.prepareStatement("SELECT * FROM custom_hist "
            + "WHERE date >= ? AND date <= ? "
            + "ORDER BY date, time, fb, courseName, mdate, sdate");
        
        pstmt.clearParameters();
        pstmt.setLong(1, sdate);
        pstmt.setLong(2, edate);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
           
            date = rs.getLong("date");
            day = rs.getString("day");
            time = rs.getInt("time");
            fb = rs.getInt("fb");
            course = rs.getString("courseName");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            user = rs.getString("user");
            mname = rs.getString("mname");
            mdate = rs.getLong("mdate");
            Sdate = rs.getString("sdate");
            type = rs.getInt("type");
            notes = rs.getString("notes");
            
            sfb = "F";
            
            if (fb > 0) sfb = "B";
            
            stype = "New";
            
            if (type == 2) {
                
               stype = "Update";
               
            } else if (type == 3) {
               
               stype = "Cancel";
            }

            //
            //  New table for each tee time
            //
            if (date != last_date || time != last_time || fb != last_fb || !course.equals(last_course)) {
               
               if (last_date > 0) {    // if NOT the first time
                  
                   out.println("</table><br><br>");
               }
               
               yy = (int)date/10000;
               mm = (int)(date - (yy * 10000))/100;
               dd = (int)date - ((yy * 10000) + (mm * 100));
               
               hr = time/100;
               min = time - (hr * 100); 
               
               ampm = "AM";
               
               if (hr > 11) ampm = "PM";
               
               if (hr > 12) hr = hr - 12;
                      
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<p align=\"center\"><strong>Tee Time:</strong>&nbsp; " +day+ " " +mm+ "/" +dd+ "/" +yy+ " " +hr+ ":" +min+ " " +ampm+ " " +sfb+ " " +course);
               out.println("</p></font>");
           
               out.println("<table align=\"center\" border=\"1\" bgcolor=\"" +tablecolor+ "\" cellpadding=\"5\" cols=\"9\">");
               out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<tr bgcolor=\"" +bgrndcolor+ "\">");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Date/Time</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Name</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">User</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Type</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Player 1</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Player 2</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Player 3</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Player 4</td>");
               out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Notes</td>");
               out.println("</tr>");
            }
            
            last_date = date;
            last_time = time;
            last_fb = fb;
            last_course = course;
           
            out.println("<tr>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + Sdate + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + mname + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + user + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + stype + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + player1 + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + player2 + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + player3 + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\">" + player4 + "</td>");
            out.println("<td style=\"margin-left:auto; margin-right:auto;\" width=\"150\">" + notes + "</td>");
            out.println("</tr>");
        }        
        
    } catch (Exception exc) {
        Utilities.logError("Proshop_report_custom - oakhillcc - Error looking up tee time data for members - ERR: " + exc.toString());
    } finally { 
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
    }


    if (last_date == 0) {    // if we didn't find any

       out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<br><br><p align=\"center\">There were no Advance Guest Times found in the specified date range.");
       out.println("</p></font><br>");
       
    } else {
           
       out.println("</table>");
    }
    
    if (!excel.equals("yes")) {
    
        out.println("<p align=\"center\">");
        out.println("<form method=\"get\" action=\"Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"background:#8B8970\">");
        out.println("</form></p>");
    }
    
    out.println("</td></tr></table></font>");    // end the page table
    
 }      // end of oakhillAdvGstReport


 
 

 //**************************************************
 // Used by the custom date range reports this routine
 // presents the user with date selection options
 // and posts the date back to request the report
 //**************************************************
 //
 private void getCustomDate(HttpServletRequest req, String club, PrintWriter out, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;


    String report_title = "Custom Data Export";
    
    if (club.equals("oakhillcc") || club.equals("demov4")) report_title = "Advance Guest Time History Report";

    // our oldest date variables (how far back calendars go)
    long old_date = 0;
    int oldest_date = 0;
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;
    
    
    if (!club.equals("oakhillcc") && !club.equals("demov4")) {

       // lookup oldest date in teepast2 that has a pace status
       try {
          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT MIN(date) FROM teepast2");

          if (rs.next()) {

               old_date = rs.getLong(1);
          }

       } catch (Exception e) {
          SystemUtils.buildDatabaseErrMsg("Proshop_report_custom - Error looking up oldest tee time.", e.getMessage(), out, false);
          return;
       }


       //
       //  Determine oldest date values - month, day, year
       //
       oldest_date = (int)old_date;
       oldest_yy = oldest_date / 10000;
       oldest_mm = (oldest_date - (oldest_yy * 10000)) / 100;
       oldest_dd = oldest_date - ((oldest_yy * 10000) + (oldest_mm * 100));

    } else {
       
       // Oak Hill CC - start with 4/01/14

       oldest_yy  = 2014;
       oldest_mm  = 4;
       oldest_dd  = 1;
    }
       
       

    // set calendar vars
    Calendar cal_date = new GregorianCalendar();
    if (!club.equals("oakhillcc") && !club.equals("demov4")) {
       cal_date.add(Calendar.DATE,-1);                     // get yesterday's date (no teepast entries for today)
    }
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
    int start_year = cal_year;
    int start_month = cal_month;
    

    if (club.equals("oakhillcc") || club.equals("demov4")) {
       
       cal_date.add(Calendar.DATE,365);           // Oak Hill - allow them to go ahead one year       
       cal_year = cal_date.get(Calendar.YEAR);
       cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
       cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
    }

    
    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

    //out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

    // start main table for this page
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td>");

    // output instructions
    out.println("<br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
    out.println("<font size=\"3\"><b>" +report_title+ "</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"Proshop_report_custom\" method=\"post\">");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <br><input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <br><input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");

    out.println("<p align=\"center\"><b>Run Report (select an output type below):</b>");

    // Print report format buttons
    out.println("<br><br><input type=\"submit\" name=\"webpage\" value=\" Web Page \" style=\"background:#8B8970\">&nbsp;&nbsp;"
              + "<input type=\"submit\" name=\"excel\" value=\" Excel \" style=\"background:#8B8970\"></p>");

    // end submission form
    out.println("</form>");

    // output back button form
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
     out.println("<p align=\"center\"><input type=\"submit\" value=\" Cancel \" style=\"background:#8B8970\"></p>");
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
     out.println("g_cal_month[0] = " + start_month + ";");
     out.println("g_cal_year[0] = " + start_year + ";");
     out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = " + cal_day + ";");
     out.println("g_cal_ending_year[0] = " + cal_year + ";");

     out.println("g_cal_month[1] = " + start_month + ";");
     out.println("g_cal_year[1] = " + start_year + ";");
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

 }


//
//   Error Message
//
 private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Database Access Error</H2>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }

 
} // end servlet
