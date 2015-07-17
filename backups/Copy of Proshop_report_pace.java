/***************************************************************************************
 *   Proshop_diary: This servlet will ouput the member rounds report
 *
 *
 *   Called by:     called by main menu options
 *
 *
 *   Created:       06/14/2006
 *
 *
 *   Last Updated:  
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
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;


public class Proshop_report_pace extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    NumberFormat nf = NumberFormat.getNumberInstance();


 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    Statement stmt = null;
    ResultSet rs = null;
    
    int count = 0;
    int multi = 0;
     
    resp.setContentType("text/html"); 
    PrintWriter out = resp.getWriter(); 
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
        return;
    }
    
    parmClub parm = new parmClub(); 
    
    try {

        getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }
    
    multi = parm.multi;
    count = 1;                  // init to 1 course
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
   
    out.println(SystemUtils.HeadTitle2("Pace of Play Reports"));
   
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#336633\" vlink=\"#336633\" alink=\"#000000\">");
    
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   
    //out.println("<br><center><h2>Choose Pace of Play Report</h2><br>");
    
    /*
    out.println("<form method=post name=frmWhichReport id=frmWhichReport>");
    out.println("<input type=hidden name=todo value=\"\">");
    out.println("<input type=button name=btnMember value=\"By Member\" onclick=\"document.frmWhichReport.todo.value='member';document.frmWhichReport.submit()\">");
    out.println(" &nbsp; &nbsp; ");
    out.println("<input type=button name=btnCourse value=\"By Course\" onclick=\"document.frmWhichReport.todo.value='course';document.frmWhichReport.submit()\">");
    out.println(" &nbsp; &nbsp; ");
    out.println("<input type=button name=btnUsage value=\"  Usage  \" onclick=\"document.frmWhichReport.todo.value='usage';document.frmWhichReport.submit()\">");
    out.println("</form>");
    */
    
    out.println("<script>");
    out.println("function reqReport(pTodo, pGetDate) {");
    out.println(" var f = document.forms['frmWhichReport'];");
    out.println(" f.todo.value = pTodo;");
    out.println(" f.get_date.value = pGetDate;");
    out.println(" f.submit();");
    out.println("}");
    out.println("</script>");
    
    out.println("<form method=post action=\"/"+rev+"/servlet/Proshop_report_pace\" name=frmWhichReport id=frmWhichReport>");
    out.println("<input type=hidden name=todo value=\"\">");
    out.println("<input type=hidden name=get_date value=\"0\">");
    
    out.println("<table cellspacing=25 align=center>");
    out.println(" <tr><td colspan=3 align=center><h2>Choose a Pace of Play Report</h2></td></tr>");
    
    out.println(" <tr valign=top>");
    out.println(" <td>");
    out.println("<p><h3>By Member</h3></p>");
    out.println("<a href=\"javascript:reqReport('mem-30', 0)\">Last 30 days</a><br>");
    out.println("<a href=\"javascript:reqReport('mem-tm', 0)\">This month</a><br>");
    out.println("<a href=\"javascript:reqReport('mem-lm', 0)\">Last month</a><br>");
    out.println("<a href=\"javascript:reqReport('mem-cd', 1)\">Custom date range</a><br>");
    out.println("<a href=\"javascript:reqReport('mem-ad', 0)\">All availabe data</a><br>");
    
    String user = "";
    String lname = "";
    String fname = "";
    String mname = "";

    out.println("<p>Choose Member:<br>");
    out.println("<select size=\"1\" name=\"username\">");

    try {

        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT username, name_last, name_first, name_mi FROM member2b " +
                               "ORDER BY name_last, name_first, name_mi");

        while (rs.next()) {

            user = rs.getString(1);
            lname = rs.getString(2);
            fname = rs.getString(3);
            mname = rs.getString(4);

            out.println("<option value=" + user + ">" + lname + ", " + fname + " " + mname + "</option>");
        }

        stmt.close();
    }
    catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error getting member list.", exc.getMessage(), out, false);
        return;
    }

    out.println("</select></p>");
    
    if (user.equals("")) out.println("There are no members in the database at this time.<br><br>");
    
    out.println(" </td>");
    out.println(" <td>");
    out.println("<p><h3>By Course</h3></p>");
    out.println("<a href=\"javascript:reqReport('cor-tm', 0)\">This month</a><br>");
    out.println("<a href=\"javascript:reqReport('cor-lm', 0)\">Last month</a><br>");
    out.println("<a href=\"javascript:reqReport('cor-cd', 1)\">Custom date range</a><br>");
    out.println("<a href=\"javascript:reqReport('cor-ad', 0)\">All availabe data</a><br>");
    // if multi - display select box with options
    
    
    if (multi != 0) {           // if multiple courses supported for this club
        String courseName = "";
        
        out.println("<p>Choose course:<br>");
        out.println("<select name=course size=1>");
        out.println("<option value=\"-ALL-\">-ALL-</option>");
                
        try {

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubparm_id, courseName FROM clubparm2 WHERE first_hr != 0");

            while (rs.next()) {

                courseName = rs.getString(1);
                //out.println("<option value=\"" + rs.getInt(1) + "\">" + rs.getString(2) + "</option>");
                out.println("<option>" + rs.getString(2) + "</option>");
            }
            stmt.close();
        }
        catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, false);
            return;
        }
        
        out.println("</select></p>");
        
    } // end if multiple courses for this club
    
    out.println(" </td>");
    out.println(" <td>");
    out.println("<p><h3>Usage</h3></p>");
    out.println("<a href=\"javascript:reqReport('use-tm', 0)\">This month</a><br>");
    out.println("<a href=\"javascript:reqReport('use-lm', 0)\">Last month</a><br>");
    out.println("<a href=\"javascript:reqReport('use-cd', 1)\">Custom date range</a><br>");
    out.println("<a href=\"javascript:reqReport('use-ad', 0)\">All availabe data</a><br>");
    out.println(" </td>");
    out.println(" </tr>");
    
    //out.println(" <tr><td colspan=3 align=center><p align=\"center\"><input type=\"submit\" value=\"   Get Report   \"></p></td></tr>");
    
    out.println("</table>");
        
    out.println("</form>");
    
    
    out.println("</center>");
    
 } // end of doGet routine


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    long edate = 0;
    long sdate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int start_year = 0;
    int start_month = 0;
    int start_day = 0;
    int end_year = 0;
    int end_month = 0;
    int end_day = 0;
        
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
        return;
    }
    
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String todo = (req.getParameter("todo") != null) ? req.getParameter("todo")  : "";
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    String tmp_get_date = (req.getParameter("get_date") != null) ? req.getParameter("get_date")  : "";
    boolean get_date = (tmp_get_date.equals("1")) ? true : false;
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        }
    }
    catch (Exception exc) {
    }

    out.println(SystemUtils.HeadTitle2("Pace of Play Reports"));
   
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
    
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
    //
    //  If we need to get a custom date range lets display the calendars
    //  If not, then let check to see if we're display a report that needs them
    //  And if so, then make sure they are here and valid.
    //
    if (get_date) {
        
        // get custom date range
        getCustomDate(req, out, con);
        return;
    } else {

        // check to see if the report we are building is a custom date range report
        if (todo.endsWith("-cd")) {

            // check to see if the date is here, and if not then jump to display calendar routine
            if (start_date.equals("")) {
                getCustomDate(req, out, con); 
                return;
            }
            
            // if no ending date then default to starting date for 1 day report
            if (end_date.equals("")) end_date = start_date;
            
            // if the date passed are in our long format already (from being passed around)
            // then we can skip this first round of validation
            try {
                sdate = Integer.parseInt(start_date);
                edate = Integer.parseInt(end_date);
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
                    getCustomDate(req, out, con);
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
                getCustomDate(req, out, con);
                return;
            }
            
            // end of custom date range
            
        } else {
            
            // get a calendar object to compute either this month or last months dates
            Calendar cal = new GregorianCalendar();             // get todays date
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) + 1;                // month starts at zero
            day = cal.get(Calendar.DAY_OF_MONTH);

            if (todo.equals("mem-30")) {

                // if this is a member report then instead of last month do last 30 days
                edate = year * 10000;                       // create a date field of yyyymmdd
                edate += month * 100;
                edate += day;

                cal.add(Calendar.DATE,-30);                 // roll back 30 days
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH) + 1;        // month starts at zero
                day = cal.get(Calendar.DAY_OF_MONTH);
                sdate = year * 10000;                       // create a date field of yyyymmdd
                sdate += month * 100;
                sdate += day;

            } else {
            
                // check to see if this is a last month report
                if (todo.endsWith("-lm")) {

                    month--;
                    if (month == 0) {

                       month = 12;
                       year--;
                    }

                    sdate = (year * 10000) + (month * 100);     // create a edate field of yyyymmdd for last month
                    edate = sdate;
                    sdate += 1;
                    edate += 31; // this is ok for query but not suitable for display purposes!

                    // end of last month

                } else {

                    // check to see if this is a this month report
                    if (todo.endsWith("-tm")) {

                        sdate = (year * 10000) + (month * 100);     // create a edate field of yyyymmdd
                        edate = sdate;
                        sdate += 1;
                        edate += day; // today (was 31)

                    } // end this month

                } // end if else last month
                
            } // end if mem last 30
            
        } // end if not custom date range
        
    } // end if we need to get custom dates
    
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    // 
    // DATES ARE SET - IF ALL DATA REPORT THEN NO DATES NEEDED
    //
    if (todo.startsWith("use-")) doUsageReport(req, sdate, edate, excel, out, con);
    if (todo.startsWith("mem-")) doMemberReport(req, sdate, edate, excel, out, con);
    if (todo.startsWith("cor-")) doCourseReport(req, sdate, edate, excel, out, con);
    
    out.println("&nbsp;<br><p align=center><a href=\"/"+rev+"/servlet/Proshop_report_pace\" style=\"color:#336633\">Return to Pace of Play Report Menu</a></p>");
    
    out.println("<p>&nbsp;</p>");
    out.println("</body>");
    out.println("</html>");
    
    return;
 }

  
 private void doUsageReport(HttpServletRequest req, long pStartDate, long pEndDate, String pExcel, PrintWriter out, Connection con) {
     
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    String range_begin = "";
    String range_end = "";
    String course = "";
    String header = "";
    
    int oldest_pdate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int edate = 0;
    int multi = 0;
    
    parmClub parm = new parmClub();
    
    try {

        getClub.getParms(con, parm);
        multi = parm.multi;
    } catch (Exception ignore) { }
    
    // if it's all available data request then subsitute the 
    // dates with the oldest tee time with pace data and todays date
    if (pStartDate == 0 && pEndDate == 0) {
        
        // lookup oldest date in teepast2 that has a pace status
        boolean found = false;
        try {
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT date FROM teepast2 WHERE pace_status_id <> 0 ORDER BY date ASC LIMIT 1");

            while (rs.next()) {
                pStartDate = rs.getInt(1);
            }

        } catch (Exception e) {
            SystemUtils.buildDatabaseErrMsg("Error looking up oldest tee time with pace data.", e.getMessage(), out, false);
            return;
        }
        
        // adjust pEndDate to today
        Calendar cal_date = new GregorianCalendar();
        year = cal_date.get(Calendar.YEAR);
        month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        day = cal_date.get(Calendar.DAY_OF_MONTH);
        pEndDate = (year * 10000) + (month * 100) + day;
        if (!found) pStartDate = (year * 10000) + (1 * 100) + 1;
        
    } // end changeing pStartDate, pEndDate
    
    if (g_debug) out.println("<!-- pStartDate=" + pStartDate + " | pEndDate=" + pEndDate + " -->");
    
    // get pretty dates for the report
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DATE_FORMAT('" + pStartDate + "', '%W %M %D, %Y') AS range_begin, DATE_FORMAT('" + pEndDate + "', '%W %M %D, %Y') AS range_end");
        if (rs.next()) {
            range_begin = rs.getString("range_begin");
            range_end = rs.getString("range_end");
        }
        rs.close();
        stmt.close();
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting formatted dates.", e.getMessage(), out, false);
        return;
    }

    // setup our report header
    header = "<center><font size=+2>Pace of play usage report " + ((multi == 1) ? "by course " : "") + "from<br></font>" + 
            "<font size=+1><nobr>" + range_begin + "</nobr> thru <nobr>" + range_end + "</nobr></font></center>";
   
    // build and display the report data
    try {
        
        boolean tmp_header = false;
        int wpace = 0;      // total rounds w/ pace
        int wopace = 0;     // total rounds w/o pace
        int rounds = 0;     // total rounds
        int pw = 0;         // percent w/ pace
        int pwo = 0;        // percent w/o pace
        String overall_tag = "";
        
        pstmt = con.prepareStatement("" +
            "SELECT *, ROUND(wpace / total * 100) AS pw, ROUND(wopace / total * 100) AS pwo FROM (" +
                "SELECT " +
                    "courseName, " +
                    "COUNT(*) AS total, " +
                    "SUM((pace_status_id = 0)) AS wopace, " +
                    "SUM((pace_status_id > 0)) AS wpace " +
                "FROM teepast2 " +
                "WHERE date >= ? AND date <= ? " +
                "GROUP BY courseName " +
                ((multi != 0) ? "WITH ROLLUP" : "") +
                ") AS z");
        pstmt.clearParameters();
        pstmt.setLong(1, pStartDate);
        pstmt.setLong(2, pEndDate);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            if (!tmp_header) {
                
                out.println(header);
                
                out.println("&nbsp;<br><br><table bgcolor=\"#F5F5DC\" align=center border=1 cellpadding=7 "+((multi==0) ? "" : "style=\"border: 2px solid #336633\"") + ">");
                out.println("<tr bgcolor=\"#336633\">" +
                                ((multi==1) ? "<td><font color=white><b>Course</b></font></td>" : "") +
                                "<td><font color=white><b>Rounds</b></font></td>" +
                                "<td><font color=white><b>With Pace</b></font></td>" +
                                "<td><font color=white><b>Without Pace</b></font></td>" +
                            "</tr>");
                tmp_header = true;
            }
            
            course = rs.getString("courseName");
            rounds = rs.getInt("total");
            wpace = rs.getInt("wpace");
            wopace = rs.getInt("wopace");
            pw = rs.getInt("pw");
            pwo = rs.getInt("pwo");
            
            overall_tag = "";
            
            if (multi == 1 && course == null) {
                course = "Overall Totals";
                out.println("<tr bgcolor=\"#336633\"><td colspan=4></td></tr>");
                overall_tag = "<b>";
            } else {
                out.println("<tr>");
            }
            
            if (multi==1) out.println("<td>" + nf.format(overall_tag + course) + "</td>");
            out.println("<td>" + overall_tag + nf.format(rounds) + "</td>");
            out.println("<td>" + overall_tag + nf.format(wpace) + ((wpace == 0) ? "" : "&nbsp;(" + pw + "%)&nbsp;") + "</td>");
            out.println("<td>" + overall_tag + nf.format(wopace) + ((wopace == 0) ? "" : "&nbsp;(" + pwo + "%)&nbsp;") + "</td>");
            
            out.println("</tr>");
        } // end while loop
        
        
        // if nothing was returned from the query then notify
        if (!tmp_header) {
            
            out.println(header);
                
            out.println("<p align=center><i>No tee times were found during the requested time<br>frame that contain pace of play data.</i></p>");
            //out.println("The time frame searched was from " + range_begin + " to " + range_end + ".</p>");
        } else {
            
            out.println("</table>");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg(exc.toString(), "", out, false);
        return;
    }
     
 }
 
 
 private void doMemberReport(HttpServletRequest req, long pStartDate, long pEndDate, String pExcel, PrintWriter out, Connection con) {
                    
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    String range_begin = "";
    String range_end = "";
    String courseName = "";
    String header = "";
    String name = "";
    String username = (req.getParameter("username") != null) ? req.getParameter("username")  : "";
    String todo = (req.getParameter("todo") != null) ? req.getParameter("todo")  : "";
    
    boolean found = false;
    //boolean details = (req.getParameter("details") != null && req.getParameter("details").equals("y")) ? true : false;
    String tmp = (req.getParameter("details") != null) ? req.getParameter("details") : "";
    boolean details = (tmp.equals("y")) ? true : false;
    
    int year = 0;
    int month = 0;
    int day = 0;
    int edate = 0;
    int multi = 0;
    
    long first_date = 0;
    
    parmClub parm = new parmClub();
    
    try {

        getClub.getParms(con, parm);
        multi = parm.multi;
    } catch (Exception ignore) { }
    
    //
    // Before processing the report lets make sure this members has at least one
    // round in teh database with pace of play recorded for it
    //
    
    // lookup oldest date in teepast2 that has a pace status for this member
    try {
        pstmt = con.prepareStatement("" +
                "SELECT date " +
                "FROM teepast2 " +
                "WHERE pace_status_id <> 0 " +
                    "AND " +
                    "(" +
                        "username1 = ? OR " +
                        "username2 = ? OR " +
                        "username3 = ? OR " +
                        "username4 = ? OR " +
                        "username5 = ?" +
                    ") " +
                "ORDER BY date ASC LIMIT 1");

        pstmt.clearParameters();
        pstmt.setString(1, username);
        pstmt.setString(2, username);
        pstmt.setString(3, username);
        pstmt.setString(4, username);
        pstmt.setString(5, username);

        rs = pstmt.executeQuery();

        while (rs.next()) {
            // if all data report then subsitute the start date of the report with this date
            first_date = rs.getInt(1);
            found = true;
        }

    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error looking up oldest tee time with pace data.", e.getMessage(), out, false);
        return;
    }
    
    
    // bail out if no rounds with pace data were found for this member
    if (!found) {

        // this member has no rounds with pace of play data collected
        out.println("<p align=center><i>No tee times were found for this member during the requested time<br>frame that contain pace of play data.</i></p>");
        return;
    } // end if no records where found
    
    
    // just because they have pace data doesn't mean they have pace data
    // within the search range of this report
    found = false;  // reset to resue
    
    // if it's all available data request then subsitute the end date with todays date
    if (pStartDate == 0 && pEndDate == 0) {
        
        // adjust pEndDate to today
        Calendar cal_date = new GregorianCalendar();
        year = cal_date.get(Calendar.YEAR);
        month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        day = cal_date.get(Calendar.DAY_OF_MONTH);
        pEndDate = (year * 10000) + (month * 100) + day;
        pStartDate = first_date;
        
    } // end changeing pStartDate, pEndDate
    
    
    // debug output
    if (g_debug) out.println("<!-- username=" + username + " | pStartDate=" + pStartDate + " | pEndDate=" + pEndDate + " | details=" + ((details) ? "true" : "false") + " -->");
    
    
    // member's display name from their username
    try {
        
        pstmt = con.prepareStatement("SELECT name_last, name_mi, name_first FROM member2b WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            name = rs.getString("name_first") + " " + rs.getString("name_last");
        }
        rs.close();
        pstmt.close();
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting members display name.", e.getMessage(), out, false);
        return;
    }
    
    // get pretty dates for the report
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DATE_FORMAT('" + pStartDate + "', '%W %M %D, %Y') AS range_begin, DATE_FORMAT('" + pEndDate + "', '%W %M %D, %Y') AS range_end");
        if (rs.next()) {
            range_begin = rs.getString("range_begin");
            range_end = rs.getString("range_end");
        }
        rs.close();
        stmt.close();
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting formatted dates.", e.getMessage(), out, false);
        return;
    }

    
    // call out to details report 
    if (details) {
        doMemberDetailsReport(req, pStartDate, pEndDate, pExcel, multi, username, todo, out, con);
        return;
    }
    
    // setup our report header
    header = "<center><font size=+2>Member pace of play report " + ((multi == 1) ? "by course " : "") + "from<br></font>" + 
            "<font size=+1><nobr>" + range_begin + "</nobr> thru <nobr>" + range_end + "</nobr><br>for<br>" + name + 
            "</font></center>";
    out.println(header);                
    out.println("&nbsp;<br><table bgcolor=\"#F5F5DC\" align=center border=1 cellpadding=7 "+((multi==0) ? "" : "style=\"border: 2px solid #336633\"") + ">");
                
    // build and display the report data
    try {
        
        boolean tmp_first = true;
        int pace_status_id = 0;     // pace id
        int rounds = 0;     // total rounds
        String pace_status_name = "";
        String tmp_last = "";
        
        pstmt = con.prepareStatement("" +
                "SELECT t.courseName, COUNT(*) AS rounds, t.pace_status_id, p.pace_status_name " +
                "FROM teepast2 t, pace_status p " +
                "WHERE date >= ? AND date <= ? " +
                    "AND t.pace_status_id = p.pace_status_id " +
                    "AND (" +
                        "t.username1 = ? OR " +
                        "t.username2 = ? OR " +
                        "t.username3 = ? OR " +
                        "t.username4 = ? OR " +
                        "t.username5 = ?" +
                    ") " +
                "GROUP BY " + ((multi != 0) ? "t.courseName, " : "") + 
                    "t.pace_status_id WITH ROLLUP");
        
        pstmt.clearParameters();
        pstmt.setLong(1, pStartDate);
        pstmt.setLong(2, pEndDate);
        pstmt.setString(3, username);
        pstmt.setString(4, username);
        pstmt.setString(5, username);
        pstmt.setString(6, username);
        pstmt.setString(7, username);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            found = true;
            courseName = rs.getString("courseName");
            rounds = rs.getInt("rounds");
            pace_status_id = rs.getInt("pace_status_id");
            pace_status_name = rs.getString("pace_status_name");
            
            if (multi == 1 && !tmp_last.equals(courseName) && courseName != null) {
                
                // don't draw a line above the first course
                if (!tmp_first) {
                    out.println("<tr bgcolor=\"#336633\"><td colspan=4></td></tr>");
                    tmp_first = false;
                }
                out.println("<td colspan=2 align=center><b><u>" + courseName + "</u></b></td></tr><tr>");
                tmp_last = courseName;
                out.println("<tr>");
                out.println("<td>" + nf.format(rounds) + " rounds</td>");
                out.println("<td>" + pace_status_name + "</td>");
                out.println("</tr>");
                
            } else {
            
                if (multi == 1 && courseName == null) {

                    out.println("<tr>");
                    out.println("<td colspan=2 align=center><b>Total Rounds w/ Pace " + nf.format(rounds) + "</td>");
                    out.println("</tr>");

                } else {

                    if (pace_status_id == 0) {

                        // total for course
                        out.println("<tr>");
                        out.println("<td colspan=2 align=center><i>" + nf.format(rounds) + " tracked rounds</i></td>");
                        out.println("</tr>");
                        if (multi != 0) out.println("<tr bgcolor=\"#336633\"><td colspan=4></td></tr>");

                    } else {

                        out.println("<tr>");
                        out.println("<td>" + nf.format(rounds) + " rounds</td>");
                        out.println("<td>" + pace_status_name + "</td>");
                        out.println("</tr>");

                    }

                }
                
            }
            
            tmp_last = courseName;
            
        } // end while loop
        
        
        // if nothing was returned from the query then notify
        if (!found) {
            
            out.println("<p align=center><i>No tee times were found during the requested time<br>frame that contain pace of play data.</i></p>");
        } else {
        
            out.println("</table>");
            out.println("&nbsp;<br>");
            
            // display a link to the detail report
            out.println("<table align=center border=0><tr><td>");
            out.println("<form method=post action=\"/"+rev+"/servlet/Proshop_report_pace\" name=frmWhichReport id=frmWhichReport>");
            out.println("<input type=hidden name=todo value=\"" + todo + "\">");
            out.println("<input type=hidden name=cal_box_0 value=\"" + pStartDate + "\">");
            out.println("<input type=hidden name=cal_box_1 value=\"" + pEndDate + "\">");
            out.println("<input type=hidden name=username value=\"" + username + "\">");
            out.println("<input type=hidden name=details value=\"y\">");
            out.println("<input type=submit name=btnSubmit value=\"  Detailed Report  \" style=\"background:#8B8970\">");
            out.println("</form>");
            out.println("</td></tr></table>");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg(exc.toString(), "", out, false);
        return;
    }  
    
 }
 
 
 private void doMemberDetailsReport(HttpServletRequest req, long pStartDate, long pEndDate, String pExcel, int pMulti, String pUsername, String pTodo, PrintWriter out, Connection con) {
     
     
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    String range_begin = "";
    String range_end = "";
    String name = "";
    String sort = (req.getParameter("sort") != null) ? req.getParameter("sort") : "";
    
    out.println("<!-- *** DETAIL DATA *** sort=" + sort + " -->");
        
    // member's display name from their username
    try {
        
        pstmt = con.prepareStatement("SELECT name_last, name_mi, name_first FROM member2b WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setString(1, pUsername);
        rs = pstmt.executeQuery();
        if (rs.next()) {
            name = rs.getString("name_first") + " " + rs.getString("name_last");
        }
        rs.close();
        pstmt.close();
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting members display name.", e.getMessage(), out, false);
        return;
    }
    
    // get pretty dates for the report
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DATE_FORMAT('" + pStartDate + "', '%W %M %D, %Y') AS range_begin, DATE_FORMAT('" + pEndDate + "', '%W %M %D, %Y') AS range_end");
        if (rs.next()) {
            range_begin = rs.getString("range_begin");
            range_end = rs.getString("range_end");
        }
        rs.close();
        stmt.close();
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting formatted dates.", e.getMessage(), out, false);
        return;
    }
    
    
    out.println("<center><font size=+2>Member pace of play detail report " + ((pMulti == 1) ? "by course " : "") + "from<br></font>" + 
                "<font size=+1><nobr>" + range_begin + "</nobr> thru <nobr>" + range_end + "</nobr><br>for<br>" + name + 
                "</font></center>");                
    out.println("&nbsp;<br><table border=\"1\" bgcolor=\"#F5F5DC\" align=center border=0 cellpadding=6 "+((pMulti==0) ? "" : "style=\"border: 2px solid #336633\"") + ">");
    out.println(
                "<tr bgcolor=\"#336633\">" +
                    "<td><font color=white><b><a href=\"javascript:fetchSorted('date')\">Date</a></b></font></td>" +
                    "<td><font color=white><b>Time</b></font></td>" +
                    ((pMulti == 1) ? "<td><font color=white><b>Course</b></font></td>" : "") +
                    "<td><font color=white><b>Pace Time</font></td>" +
                    "<td><font color=white><b><a href=\"javascript:fetchSorted('pace')\">Pace</a></b></font></td>" +
                "</tr>");
                
    // build and display the report data
    try {
        
        boolean tmp_first = true;
        int fb = 0;
        int hr = 0;
        int min = 0;
        int pace_status_id = 0;     // pace id
        int rounds = 0;     // total rounds
        int teecurr_id = 0;
        String pace_status_name = "";
        String tmp_last = "";
        String courseName = "";
        String pretty_date = "";
        String ampm = " AM";
        String time = "";
        String pace_time = "";
        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";
        String player5 = "";
        String p1cw = "";
        String p2cw = "";
        String p3cw = "";
        String p4cw = "";
        String p5cw = "";
        
        String sql = "" +
                "SELECT DATE_FORMAT(date, '%a. %b %D, %Y') AS pretty_date, t.*, p.pace_status_name " +
                "FROM teepast2 t, pace_status p " +
                "WHERE t.date >= ? AND t.date <= ? " +
                    "AND t.pace_status_id = p.pace_status_id " +
                    "AND (" +
                        "t.username1 = ? OR " +
                        "t.username2 = ? OR " +
                        "t.username3 = ? OR " +
                        "t.username4 = ? OR " +
                        "t.username5 = ?" +
                    ") " +
                "ORDER BY " + 
                ((sort.equals("pace")) ? "t.pace_status_id, " : "") + 
                "t.date DESC, t.time";
                
        //out.println("<!-- " + sql + " -->");
        
        pstmt = con.prepareStatement(sql);
        
        pstmt.clearParameters();
        pstmt.setLong(1, pStartDate);
        pstmt.setLong(2, pEndDate);
        pstmt.setString(3, pUsername);
        pstmt.setString(4, pUsername);
        pstmt.setString(5, pUsername);
        pstmt.setString(6, pUsername);
        pstmt.setString(7, pUsername);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            teecurr_id = rs.getInt("teecurr_id");
            courseName = rs.getString("courseName");
            pretty_date = rs.getString("pretty_date");
            pace_status_id = rs.getInt("pace_status_id");
            pace_status_name = rs.getString("pace_status_name");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            fb = rs.getInt("fb");

            if (hr == 12) ampm = " PM";
            if (hr == 0) hr = 12;
            if (hr > 12) {

               hr = hr - 12;
               ampm = " PM";
            }

            time = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;
            pace_time = getPaceTimeByTeeCurrID(teecurr_id, out, con);
            if (pace_time.equals("")) pace_time = "N/A";
            
            out.println("<tr>");
            out.println("<td>" + pretty_date + "</td>");
            out.println("<td>" + time + "</td>");
            if (pMulti == 1) out.println("<td>" + courseName + "</td>");
            out.println("<td>" + pace_time + ((fb == 1) ? "" : "<font size=1>(B)</font>") + "</td>");
            out.println("<td>" + pace_status_name + "</td>");
            out.println("</tr>");
            out.println("<tr>");
            out.print("<td colspan=" + ((pMulti == 1) ? "5" : "4") + "><font size=2>");
            out.print(player1 + " <font size=1>(" + p1cw + ")</font>");
            if (!player2.equals("") && !player2.equalsIgnoreCase("x")) out.print(", " + player2 + " <font size=1>(" + p2cw + ")</font>");
            if (!player3.equals("") && !player3.equalsIgnoreCase("x")) out.print(", " + player3 + " <font size=1>(" + p3cw + ")</font>");
            if (!player4.equals("") && !player4.equalsIgnoreCase("x")) out.print(", " + player4 + " <font size=1>(" + p4cw + ")</font>");
            if (!player5.equals("") && !player5.equalsIgnoreCase("x")) out.print(", " + player5 + " <font size=1>(" + p5cw + ")</font>");
            out.print("</font></td>");
            out.println("</tr>");
            
            out.println("<tr><td colspan=" + ((pMulti == 1) ? "5" : "4") + " bgcolor=\"#8B8970\"><img src=\"/" + rev + "/images/shim.gif\" width=1 height=1></td></tr>");
                        
        } // end while loop
        
        out.println("</table>");
        out.println("<br>&nbsp;");
        
        out.println("<table align=center border=0><tr><td>");
        out.println("<form method=post action=\"/"+rev+"/servlet/Proshop_report_pace\" name=frmWhichReport id=frmWhichReport>");
        out.println("<input type=hidden name=todo value=\"" + pTodo + "\">");
        out.println("<input type=hidden name=cal_box_0 value=\"" + pStartDate + "\">");
        out.println("<input type=hidden name=cal_box_1 value=\"" + pEndDate + "\">");
        out.println("<input type=hidden name=username value=\"" + pUsername + "\">");
        out.println("<input type=hidden name=details value=\"\">");
        out.println("<input type=hidden name=sort value=\"\">");
        out.println("<input type=submit name=btnSubmit value=\"Back to Summary Report\" style=\"background:#8B8970\">");
        out.println("</form>");
        out.println("</td></tr></table>");
        
        out.println("<script>");
        out.println("function fetchSorted(pBy) {");
        out.println(" f = document.forms['frmWhichReport'];");
        out.println(" f.sort.value = pBy;");
        out.println(" f.details.value = 'y';");
        out.println(" f.submit();");
        out.println("}");
        out.println("</script>");
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg(exc.toString(), "", out, false);
        return;
    }  
     
 }
 
 
 private void doCourseReport(HttpServletRequest req, long pStartDate, long pEndDate, String pExcel, PrintWriter out, Connection con) {

    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    int i = 0;
    String sql = "";
    String courseName = (req.getParameter("course") != null) ? req.getParameter("course")  : "";
    String range_begin = "";
    String range_end = "";
    
    int multi = 0;
    
    parmClub parm = new parmClub();
    
    try {

        getClub.getParms(con, parm);
        multi = parm.multi;
    } catch (Exception ignore) { }
    
    try {
        
        if (pStartDate != 0 && pEndDate != 0) {
            
            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                "SELECT " +
                "DATE_FORMAT('" + pStartDate + "', '%W %M %D, %Y') AS b, " +
                "DATE_FORMAT('" + pEndDate + "', '%W %M %D, %Y') AS e");
            
            if (rs.next()) {
                range_begin = rs.getString("b");
                range_end = rs.getString("e");
            }
            rs.close();
            stmt.close();
        }
        
        pstmt = con.prepareStatement("" +
            "SELECT courseName, pace_status_id, pace_status_name, SUM(totals) AS overall " + 
            "FROM (" +
                "SELECT t.courseName, t.pace_status_id, COUNT(*) AS totals, p.pace_status_name " +
                "FROM teepast2 t, pace_status p " +
                "WHERE t.pace_status_id > 0 " +
                    ((pStartDate == 0 && pEndDate == 0) ? "" : "AND t.date >= ? AND t.date <= ? ") +
                    "AND t.pace_status_id = p.pace_status_id " +
                    ((courseName.equals("-ALL-")) ? "" : "AND t.courseName = ? ") +
                "GROUP BY t.courseName, t.pace_status_id" +
            ") AS z " +
            "GROUP BY courseName, pace_status_id WITH ROLLUP;");
        
        pstmt.clearParameters();
        if (pStartDate == 0 && pEndDate == 0) {
            if (!courseName.equals("-ALL-")) {
                
                pstmt.setString(1, courseName);
            }
        } else {
            pstmt.setLong(1, pStartDate);
            pstmt.setLong(2, pEndDate);
            if (!courseName.equals("-ALL-")) {
                
                pstmt.setString(3, courseName);
            }
        }
        
        rs = pstmt.executeQuery();

        String tmp_last = "";
        String course = "";
        String pace_status_name = "";
        int pace_status_id = 0;
        int overall = 0;
        boolean tmp_header = false;
        
        while (rs.next()) {

            if (!tmp_header) {
                out.print("<center><font size=+2>Course report ");
                if (!courseName.equals("")) out.print("for " + ((courseName.equals("-ALL-")) ? "each course" : courseName));
                if (pStartDate == 0 && pEndDate == 0) {
                    out.print("</font><br><font size=+1>using all available data</font></center>");
                } else {
                    out.print(" from</font><br><font size=+1><nobr>" + range_begin + "</nobr> thru <nobr>" + range_end + "</nobr></font></center>");
                }
                out.println("&nbsp;<br><br><table bgcolor=\"#F5F5DC\" align=center border=1 cellpadding=7 "+((multi==0) ? "" : "style=\"border: 2px solid #336633\"") + ">");
                tmp_header = true;
            }
            
            course = rs.getString("courseName");
            pace_status_name = rs.getString("pace_status_name");
            pace_status_id = rs.getInt("pace_status_id");
            overall = rs.getInt("overall");
            
            out.println("<tr>");
            
            if (course == null && pace_status_id == 0) {
                
                //out.println("<td colspan=2 align=center>&nbsp;<br><b><i>Total rounds with pace of play data: " + overall + "</i></b></td>"); // total rounds with pace data
                out.println("<td><b>" + nf.format(overall) + "</b></td><td><b>Rounds with pace of play data</b></td>"); // total rounds with pace data
                
            } else {
                
                if (pace_status_id == 0 && multi == 1) {
                 
                    //out.println("<td colspan=2 align=center>" + course + " has " + overall + " rounds with pace of play data."); // total pace rounds for course
                    out.println("<td colspan=2 align=center>" + nf.format(overall) + " rounds have pace of play data"); // total pace rounds for course
                } else {
                    
                    if (!tmp_last.equals(course))
                        out.println("<td colspan=2 align=center>&nbsp;<br><b><u>" + course + "</u></b></td></tr><tr>");
                    
                    //out.println("<td></td>");
                    
                    if (pace_status_id != 0) {
                        out.println("<td align=right>" + nf.format(overall) + "</td>");
                        out.println("<td>" + pace_status_name + "</td>");
                    }
                    
                    /*
                    out.println("<td cospan=3 align=center>" +
                            "<table><tr>" +
                            "<td align=right>" + overall + "</td>" +
                            "<td>" + pace_status_name + "</td>" +
                            "</tr></table>" +
                            "</td>");
                    */
                    tmp_last = course;
                }
            
            }
            
            out.println("</tr>");
        } // end while loop
        
        // if nothing was returned from the query then notify
        if (!tmp_header) {
            
            out.println("<tr><td colspan=3><p>No tee times were found during the requested time frame that contain pace of play data.");
            out.println("The time frame searched was from " + range_begin + " to " + range_end + ".</p></td></tr>");
        }
        
        out.println("</table>");
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg(exc.toString(), "", out, false);
        return;
    }
 } // end doCourseReport

 
 private void getCustomDate(HttpServletRequest req, PrintWriter out, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    String fname = "";
    String lname = "";
    String mname = "";
    String user = "";

    String todo = (req.getParameter("todo") != null) ? req.getParameter("todo")  : "";
    String course = (req.getParameter("course") != null) ? req.getParameter("course")  : "";
    String username = (req.getParameter("username") != null) ? req.getParameter("username")  : "";
    
    // our oldest date variables (how far back calendars go)
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;
    
    // lookup oldest date in teepast2 that has a pace status
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT mm,dd,yy,pace_status_id FROM teepast2 WHERE pace_status_id <> 0 ORDER BY date ASC LIMIT 1");

        while (rs.next()) {
            oldest_mm = rs.getInt(1);
            oldest_dd = rs.getInt(2);
            oldest_yy = rs.getInt(3);
        }
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error looking up oldest tee time with pace data.", e.getMessage(), out, false);
        return;
    }
    
    // set calendar vars
    Calendar cal_date = new GregorianCalendar();
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
    
    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

    //out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

    // start main table for this page
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td>");
    
    // output instructions
    out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
    out.println("<font size=\"3\"><b>Pace of Play Report</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
    out.println("Click on <b>Generate Report</b> to build the report.</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_pace\" method=\"post\">");
     out.println("<input type=hidden name=todo value=\""+ todo +"\">");
     out.println("<input type=hidden name=username value=\""+ username +"\">");
     out.println("<input type=hidden name=course value=\""+ course +"\">");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");   

    // report button (go)
    out.println("<p align=\"center\"><input type=\"submit\" value=\"  Generate Report  \" style=\"background:#8B8970\"></p>");
    
    // end date submission form
    out.println("</form>");
    
    // output back button form
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_pace\">");
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

 }
 
 
 private static String getPaceTimeByTeeCurrID(int pTeeCurrID, PrintWriter out, Connection con) {
     
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    String hole_time = "";
    String tmp_stime = "";
    String retVal = "";
    
    int hole_num = 0;
    int diff = 0;
    int fb = 0;
    int invert = 0;

    int course_id = SystemUtils.getClubParmIdFromTeeCurrID(pTeeCurrID, con);
    
    
    try {
        
        // get the front/back indicator for this tee time
        pstmt = con.prepareStatement("SELECT fb FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();
        
        if (rs.next()) fb = rs.getInt("fb");
        rs.close();
        
        
        // get the highest hole number we have a pace entry for
        pstmt = con.prepareStatement("SELECT hole_timestamp, hole_number, invert FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            
            hole_num = rs.getInt("hole_number");
            invert = rs.getInt("invert");
            hole_time = rs.getString("hole_timestamp");
        }
        
        rs.close();        
 
        
        // there is not enough data - return default
        if (hole_num < 1) return retVal;
        
        //if (!(invert == 18 || invert == 9)) return retVal;
        
        // get the their starting time
        pstmt = con.prepareStatement("SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();
        
        if (rs.next()) tmp_stime = rs.getString("hole_timestamp");
        rs.close();
                
        Statement stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT HOUR(SUBTIME(\"" + tmp_stime + "\", \"" + hole_time + "\")) AS hr, MINUTE(SUBTIME(\"" + tmp_stime + "\", \"" + hole_time + "\")) AS min");
        
        if (rs.next()) retVal = rs.getInt("hr") + ":" + SystemUtils.ensureDoubleDigit(rs.getInt("min"));
                
        if (invert != 18) retVal += " <font size=1>(thru " + hole_num + ")</font>";
        
    } catch (Exception e) {
        
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
        return retVal;
    }
    
    return retVal;
 }
}  // end class