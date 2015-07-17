/***************************************************************************************
 *   Proshop_diary: This servlet will ouput the course statistics report
 *
 *
 *   Called by:     called by main menu options and by its own outputed html
 *
 *
 *   Created:       3/23/2005 by Paul
 *
 *
 *   Last Updated:  11/15/2005
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
import java.text.*;

// foretees imports
//import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;


public class Proshop_report_course_rounds extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = false;
    
    
 //****************************************************
 // Process the get method on this page as a post call
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine
 
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // set response content type
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not establish connection.", "", out);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    int report_type = 0;
    
    // start ouput
    if (!excel.equals("yes")) { out.println(SystemUtils.HeadTitle("Proshop - Rounds Played Report")); }
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    if (!excel.equals("yes")) { SystemUtils.getProshopSubMenu(req, out, lottery); }               // required to allow submenus on this page
    
    //if (req.getParameter("round") != null) { report_type = 0; }
    if (req.getParameter("today") != null) { report_type = 1; }
    if (req.getParameter("custom") != null) { report_type = 2; }
    if (req.getParameter("trans") != null) { report_type = 3; } // requires valid date range when called
    if (req.getParameter("lm") != null) { report_type = 4; } // currently this trigger will only be used w/ detail
    if (req.getParameter("mtd") != null) { report_type = 5; } // currently this trigger will only be used w/ detail
    if (req.getParameter("ytd") != null) { report_type = 6; } // currently this trigger will only be used w/ detail
    if (req.getParameter("detail") != null) { report_type += 10; }
    
    if (g_debug) out.println("<!-- report_type=" + report_type + " -->");
    
    switch(report_type) {
        
        case 1:
            doTodaySummary(req, out, con, session);                 // go process Number of Rounds Played Today report
            break;
        case 2:
            doCustomDate(req, out, con, session);                   // go process Number of Rounds Played Custom Date range report
            break;
        case 10:
            //doRoundDetail(req, resp, out, session, con);          // go process Number of Rounds Played Last Month, MTD, YTD Detailed report
            break;
        case 11:
            doTodayDetail(req, out, con, session);                  // go process Number of Rounds Played Today Detailed report
            break;
        case 12:
            doCustomDateDetail(req, out, con, session);           // go process Number of Rounds Played Custom Date Range Detailed report
            break;
        case 13:
            doTransDetail(req, out, con, session);                  // go process Transportation Options Used report
            break;
        case 14:
            doLastMonthDetail(req, out, con, session);                   // go process Number of Rounds Played Custom Date range report
            break;
        case 15:
            doThisMonthDetail(req, out, con, session);                   // go process Number of Rounds Played Custom Date range report
            break;
        case 16:
            doThisYearDetail(req, out, con, session);                   // go process Number of Rounds Played Custom Date range report
            break;
        default:
            doRound(req, resp, out, session, con);                  // go process Number of Rounds Played Last Month, MTD, YTD report
        
    }
    
    out.println("</body></html>");
    out.close();
    
 } // end doPost
 
 
 //**************************************
 // Today Reports
 //**************************************
 //
 
 private void doTodaySummary(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
   
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int multi = 0;                 // multiple course support
    int index = 0;
    int i = 0;
    int count = 0;                 // number of courses
    int edate = 0;

    String [] course = new String [20];
    String courseName = "";        // course names
    String error = "None";

    parmClub parm = new parmClub();          // allocate a parm block
    try {

        getClub.getParms(con, parm);        // get the club parms
    }
    catch (Exception ignore) {
    }

    //
    //  Get today's date and current time and calculate date & time values
    //
    Calendar cal = new GregorianCalendar();       // get todays date

    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
    int cal_min = cal.get(Calendar.MINUTE);
    int curr_time = (cal_hour * 100) + cal_min;    // get time in hhmm format
    curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

    if (curr_time < 0) {          // if negative, then we went back or ahead one day

        curr_time = 0 - curr_time;        // convert back to positive value

        if (curr_time < 100) {           // if hour is zero, then we rolled ahead 1 day

            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal.add(Calendar.DATE,1);                     // get next day's date

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);

        } else {                        // we rolled back 1 day

            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal.add(Calendar.DATE,-1);                     // get yesterday's date

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
        }
    }

    month = month + 1;                           // month starts at zero

    edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
    edate = edate + (month * 100);
    edate = edate + day;                         // date = yyyymmdd (for comparisons)

    multi = parm.multi;

    //
    // Check for multiple courses
    //
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        while (index< 20) {

            course[index] = "";       // init the course array
            index++;
        }

      index = 0;

      try {

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count = index;                      // number of courses

      }
      catch (Exception exc) {
         displayDatabaseErrMsg("Error loading course names.", "", out);
         return;
      }
    }

    // start report output
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font size=\"3\">");
    out.println("<p><b>Course Statistics for Today<br>");
    out.println(month + "/" + day + "/" + year);
    out.println("</b></font><font size=\"2\"><br><b>Note:</b> Percentages are rounded down to whole number.<br><p>");
    out.println(buildDisplayDateTime());
    out.println("</font></p><br>");

    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) { courseName = course[index]; }

    // start the main tables that holds each course table
    //out.println("<table cellspacing=10 align=\"center\">");

    //
    // execute searches and display for each course
    //
    while (count > 0) {

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Summary</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\">");
        out.println("<td>");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"center\"><b>Today</b> (thus far)</p>");
        out.println("</font></td>");

        
        displayCourseSummary(courseName, edate, edate, 0, con, out);
        
        out.println("</font></tr></table><br>");

        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more

    }       // end of while Courses - do all courses

    if (multi != 0) {
        
        //
        // if multi course then display grand total of combinded courses
        //
        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
        out.println("<font color=\"#FFFFFF\" size=\"3\">");
        out.println("<p align=\"center\"><b>Combind Course Grand Totals</b></p>");
        out.println("</font></td></tr>");

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\">");
        out.println("<td>");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"center\"><b>Today</b> (thus far)</p>");
        out.println("</font></td>");

        displayCourseSummary("-ALL-", edate, edate, 0, con, out);
        
        out.println("</font></tr></table><br>");
        
    }

    out.println("</td></tr></table>");                // end of main page table & column

    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<table align=center cellspacing=7><tr><td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</td><td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
        out.println("<input type=\"hidden\" name=\"today\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        out.println("<input type=\"submit\" value=\" Detail \" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></td></tr></table></font>");
    }
    
    out.println("</center></font>");

 }  // end of doTodaySummary
 
 
 private void doTodayDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {

    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String club = (String)sess.getAttribute("club");      // get club name
    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    parmClub parm = new parmClub();          // allocate a parm block
    //parmCourse parmc = new parmCourse();          // allocate a parm block
    long sdate = 0;
    long edate = 0;
    int lottery = Integer.parseInt(templott);
    int year = 0;
    int month = 0;
    int day = 0;
    int multi = 0;                 // multiple course support
    int index = 0;
    int i = 0;
    int count = 0;                 // number of courses
    int rs_rows1 = 0;
    int tmp_index = 0;
    int total_course_rounds_9 = 0;
    int total_course_rounds_18 = 0;
    boolean new_row = false;
    String tmp_error = "";
    String [] course = new String [20];             // max of 20 courses per club
    String courseName = "";                         // course names

    //
    //   Get multi option, member types, and guest types
    //
    try {

      getClub.getParms(con, parm);                      // get the club parms
    } catch (Exception ignore) { }

    //
    //  Get today's date and current time and calculate date & time values
    //
    Calendar cal = new GregorianCalendar();             // get todays date
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH) + 1;                // month starts at zero
    day = cal.get(Calendar.DAY_OF_MONTH);
    sdate = (year * 10000) + (month * 100);             // create a edate field of yyyymmdd
    sdate += day;
   
    int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
    int cal_min = cal.get(Calendar.MINUTE);
    long curr_time = (cal_hour * 100) + cal_min;        // get time in hhmm format
    curr_time = SystemUtils.adjustTime2(con, curr_time); // adjust the time

    if (curr_time < 0) {          // if negative, then we went back or ahead one day

      curr_time = 0 - curr_time;        // convert back to positive value

      if (curr_time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
      }
    }

    //out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | curr_time=" + curr_time + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        while (index< 20) {

            course[index] = "";       // init the course array
            index++;
        }

        index = 0;

        try {

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName " +
                            "FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < 20) {

                courseName = rs.getString(1);

                course[index] = courseName;      // add course name to array
                index++;
            }
            
            stmt.close();
            count = index;                      // number of courses

        }
        catch (Exception exc) {
            displayDatabaseErrMsg("Can not establish connection.", "", out);
            return;
        }
        
    } // end if multiple courses for this club

    //
    //  Start to build the HTML page to display stats
    //

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Detailed Course Statistics for Today<br>");
    out.println(month + "/" + day + "/" + year);
    out.println("</b></font><font size=\"2\"><br><b>Note:</b> Percentages are rounded down to whole number.<br>");
    out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");

    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"today\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course[index];  // if multiple courses supported for this club get first course name
    
    String courseClause = "courseName = ? AND";
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; count = 1; }
    
    // start the main tables that holds each course table
    out.println("<table cellspacing=10>");

    //
    // loop each course and display stats
    //
    while (count > 0) {

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>"); // posible browser bug - NN

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\"><td>");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"center\"><b>Today (thus far)</b></p>"); // maybe show time?
        out.println("</font></td>");
        
                     
        //
        // use the dates provided to search the tee times tables
        //
        try {

            //
            //  Get the System Parameters for this Course
            //
            //getParms.getCourse(con, parmc, courseName); // do i need this?
            
            //
            //  First lets get the total rounds played for this course
            //
            
            total_course_rounds_9 = 0; // reset
            total_course_rounds_18 = 0; // reset
            
            PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT sum(p_9), sum(p_18) from (" +        
            "SELECT p1cw as cw, " +
                    "p91 as p_9, IF(p91=0,1,0) as p_18 " +
                    "FROM teecurr2 " +
                    "WHERE " + courseClause + " date = ? AND time <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw as cw,  " +
                    "p92 as p_9, IF(p92=0,1,0) as p_18 " +
                    "FROM teecurr2 " +
                    "WHERE " + courseClause + " date = ? AND time <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw as cw, " +
                    "p93 as p_9, IF(p93=0,1,0) as p_18 " +
                    "FROM teecurr2 " +
                    "WHERE " + courseClause + " date = ? AND time <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw as cw, " +
                    "p94 as p_9, IF(p94=0,1,0) as p_18 " +
                    "FROM teecurr2 " +
                    "WHERE " + courseClause + " date = ? AND time <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw as cw,  " +
                    "p95 as p_9, IF(p95=0,1,0) as p_18 " +
                    "FROM teecurr2 " +
                    "WHERE " + courseClause + " date = ? AND time <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 IS NOT null)) " +
            ") as d_table;");
            
            pstmt1.clearParameters();
            if (courseName.equalsIgnoreCase("-ALL-")) {
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, edate);
                pstmt1.setLong(3, sdate);
                pstmt1.setLong(4, edate);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setLong(7, sdate);
                pstmt1.setLong(8, edate);
                pstmt1.setLong(9, sdate);
                pstmt1.setLong(10, edate);
            } else {
                pstmt1.setString(1, courseName);
                pstmt1.setLong(2, sdate);
                pstmt1.setLong(3, curr_time);
                pstmt1.setString(4, courseName);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, curr_time);
                pstmt1.setString(7, courseName);
                pstmt1.setLong(8, sdate);
                pstmt1.setLong(9, curr_time);
                pstmt1.setString(10, courseName);
                pstmt1.setLong(11, sdate);
                pstmt1.setLong(12, curr_time);
                pstmt1.setString(13, courseName);
                pstmt1.setLong(14, sdate);
                pstmt1.setLong(15, curr_time);
            }
            rs1 = pstmt1.executeQuery();
            
            rs1.next();
            
            total_course_rounds_9 = rs1.getInt(1);
            total_course_rounds_18 = rs1.getInt(2);
            
            pstmt1.close();            
            
            // end of course grand totals
           
        }
        catch (Exception exc) {
            
            displayDatabaseErrMsg(tmp_error, exc.toString() , out);
            return;
            
        } // end of try/catch for sql
        
        out.println("</tr><tr>");                       // Grand totals
        out.println("<td align=\"left\">");
        out.println("<font size=\"2\"><br>");
        out.println("<b><u>Total Rounds Played:</u></b>");
        out.println("</font></td>");

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><br><b>");
        out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 18
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">18 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 9
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">9 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // blank row for divider
        out.println("<td colspan=\"3\">&nbsp;</td>");
        
        
        // show member type detail
        displayMemberTypeQuery(courseName, sdate, curr_time, 11, total_course_rounds_18, total_course_rounds_9, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, curr_time, 11, total_course_rounds_18, total_course_rounds_9, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, curr_time, 11, total_course_rounds_18, total_course_rounds_9, con, out);

        out.println("</font></tr></table><br>");

        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\" Home \" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");

        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
        out.println("<input type=hidden name=today>");
        out.println("<input type=\"submit\" value=\"Back to Summary\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
    }
    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");

 }  // end of doTodayDetail
 
 
 //**************************************************
 // Custom Date Range Reports
 //**************************************************
 //
 private void getCustomDate(HttpServletRequest req, PrintWriter out, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    String fname = "";
    String lname = "";
    String mname = "";
    String user = "";

    // our oldest date variables (how far back calendars go)
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;
    
    // lookup oldest date in teepast2
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT mm,dd,yy FROM teepast2 ORDER BY date ASC LIMIT 1");

        while (rs.next()) {
            oldest_mm = rs.getInt(1);
            oldest_dd = rs.getInt(2);
            oldest_yy = rs.getInt(3);
        }
        
    } catch (Exception e) {
        displayDatabaseErrMsg("Error looking up oldest teetime.", e.getMessage(), out);
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
    out.println("<font size=\"3\"><b>Number of Rounds Played Report</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("<b>Note:</b>  Only rounds before today will be included in the counts.<br><br>");
    out.println("Click on <b>Go</b> to generate the report.</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" method=\"post\">");
     out.println("<input type=hidden name=custom value=1>");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");   

    // report button (go)
    out.println("<p align=\"center\"><input type=\"submit\" value=\"  Go  \"></p>");
    
    // end date submission form
    out.println("</form>");
    
    // output back button form
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
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

    //out.println("</center></font>");
 }
 
 
 private void doCustomDate(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
    
    // 
    //  Declare our local variables
    //
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    
    // check to see if the date is here, and if not then jump to display calendar routine
    if (start_date.equals("") || end_date.equals("")) {
        getCustomDate(req, out, con); 
        return;
    }
    
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
    
    } catch (Exception e) {
        // invalid dates here, bailout and call form again
        getCustomDate(req, out, con);
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
        getCustomDate(req, out, con);
        return;
    }
    
    //
    // IF WE ARE STILL HERE THEN WE HAVE A VALID DATE RANGE SUPPLIED BY THE USER
    //
        
    
    // 
    //  Declare more local variables
    //
    Statement stmt = null;
    //PreparedStatement pstmtc = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    Calendar cal1 = new GregorianCalendar(start_year, start_month - 1, start_day);
    Calendar cal2 = new GregorianCalendar(end_year, end_month - 1, end_day);
    
    
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   //parmCourse parmc = new parmCourse();          // allocate a parm block
   
   String club = (String)sess.getAttribute("club");      // get club name
   
   //
   //  get the club parameters
   //
   try {
      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception e) {
   }
   
   int multi = 0;                 // multiple course support
   int index = 0;
   int count = 0;                 // number of courses

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   String courseName = "";        // course names

   String error = "None";

   //
   //   Get multi option, member types, and guest types
   //
   multi = parm.multi;

   count = 1;                  // init to 1 course

   //
   //   Check for multiple courses
   //
   if (multi != 0) {           // if multiple courses supported for this club

      while (index< 20) {

         course[index] = "";       // init the course array
         index++;
      }

      index = 0;

      try {

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count = index;                      // number of courses

      }
      catch (Exception exc) {
         displayDatabaseErrMsg("Error loading course names.", exc.getMessage(), out);
         return;
      }
   }

   //
   //  Build the HTML page to display search results
   //
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td align=\"center\">");

      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics - Summary</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> If applicable, today's counts are not included. Percentages are rounded down to whole number.<br>");
      //out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
      //out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
      out.println(buildDisplayDateTime());
      out.println("</font>");

    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"custom\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
        out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
      
   courseName = "";            // init as not multi
   index = 0;

   if (multi != 0) {           // if multiple courses supported for this club

      courseName = course[index];      // get first course name
   }

   //
   // execute searches and display for each course
   //
   while (count > 0) {

      //
      //  Build a table for each course
      //
      out.println("<br><table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

         //
         // add course name header if multi
         //
         if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
            out.println("<font color=\"#FFFFFF\" face=\"verdana\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
            out.println("</font></td></tr>");
         }

        out.println("<tr bgcolor=\"#336633\"><td>");
            out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
            out.println("<p align=\"left\"><b>Stat</b></p>");
            out.println("</font></td>");

            out.println("<td colspan=\"2\">");
            out.println("<font size=\"2\" color=\"white\" face=\"verdana\">");
            out.println("<p align=\"center\"><b>From " + start_month + "/" + start_day + "/" + start_year + " to");
            out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
            out.println("</font></td>");
               
         displayCourseSummary(courseName, sdate, edate, 2, con, out);
         
         if (req.getParameter("excel") == null) {
             out.println("</tr><tr><td colspan=\"3\" align=center>");
             out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
               out.println("<input type=\"hidden\" name=\"custom\">");
               out.println("<input type=\"hidden\" name=\"detail\">");
               out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
               out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
               out.println("<input type=\"submit\" value=\" " + courseName + " Detail  \" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</form>");

             out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
               out.println("<input type=hidden name=trans>");
               out.println("<input type=hidden name=detail>");
               out.println("<input type=hidden name=course value=\"" + courseName + "\">");
               out.println("<input type=hidden name=cal_box_0 value=\"" + start_date + "\">");
               out.println("<input type=hidden name=cal_box_1 value=\"" + end_date + "\">");
               out.println("<input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</form>");

             out.println("</font></td>");
         }
      out.println("</tr></table><br>");

      count--;                         // decrement number of courses
      index++;
      courseName = course[index];      // get next course name, if more

   }       // end of while Courses - do all courses

   
    // display grand total of all courses (if multi)
    if (multi != 0) {     
        out.println("<br><table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //

        out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
        out.println("<font color=\"#FFFFFF\" face=\"verdana\" size=\"3\">");
        out.println("<p align=\"center\"><b>Combind Course Totals</b></p>");
        out.println("</font></td></tr>");

        out.println("<tr bgcolor=\"#336633\"><td>");
        out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font size=\"2\" color=\"white\" face=\"verdana\">");
        out.println("<p align=\"center\"><b>From " + start_month + "/" + start_day + "/" + start_year + " to");
        out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
        out.println("</font></td>");

        displayCourseSummary("-ALL-", sdate, edate, 2, con, out);

        if (req.getParameter("excel") == null) {
            out.println("</tr><tr><td colspan=\"3\" align=center>");
            out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
            out.println("<input type=\"hidden\" name=\"custom\">");
            out.println("<input type=\"hidden\" name=\"detail\">");
            out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"-ALL-\">");
            out.println("<input type=\"submit\" value=\" Combind Course Detail  \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
            out.println("<input type=\"hidden\" name=\"trans\">");
            out.println("<input type=\"hidden\" name=\"detail\">");
            out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"-ALL-\">");
            out.println("<input type=\"submit\" value=\" Combind Trans Detail \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

            out.println("</font></td>");
        }

        out.println("</tr></table><br>");
      
    } // end conditional display of all combind courses
   
   out.println("</td></tr></table>");                // end of main page table & column

   if (req.getParameter("excel") == null) {
       out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
       out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form></font>");
   }
   
 } // end of doCustomDate
 
 
 private void doCustomDateDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
    
    // 
    //  Declare our local variables
    //
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    
    // check to see if the date is here, and if not then jump to display calendar routine
    if (start_date.equals("") || end_date.equals("")) {
        getCustomDate(req, out, con); 
        return;
    }
    
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
    
    } catch (Exception e) {
        // invalid dates here, bailout and call form again
        getCustomDate(req, out, con);
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
        getCustomDate(req, out, con);
        return;
    }
    
    //
    // IF WE ARE STILL HERE THEN WE HAVE A VALID DATE RANGE SUPPLIED BY THE USER
    //
        
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String club = (String)sess.getAttribute("club");      // get club name
    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    parmClub parm = new parmClub();          // allocate a parm block
    //parmCourse parmc = new parmCourse();          // allocate a parm block
    int lottery = Integer.parseInt(templott);
    int year = 0;
    int month = 0;
    int day = 0;
    int multi = 0;                 // multiple course support
    int index = 0;
    int i = 0;
    int count = 0;                 // number of courses
    int rs_rows1 = 0;
    int tmp_index = 0;
    int total_course_rounds_9 = 0;
    int total_course_rounds_18 = 0;
    boolean new_row = false;
    String tmp_error = "";
    String [] course = new String [20];             // max of 20 courses per club
    String courseName = (req.getParameter("course") != null) ? req.getParameter("course")  : "";
    

    //
    //   Get multi option, member types, and guest types
    //
    try {

      getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }

    out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | courseName=" + courseName + " -->");

    //
    //  Start to build the HTML page to display stats
    //

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Detailed Course Statistics for Custom Date Range</b><br></font><font size=\"2\">");
    out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
    out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");

    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"custom\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
        out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    // start the main tables that holds each course table
    out.println("<table cellspacing=10>");

    new_row = (new_row == false);
    out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>");

    out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

    //
    // add course name header if multi
    //
    if (!courseName.equals( "" )) {

        out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
        out.println("<font color=\"#FFFFFF\" size=\"3\">");
        out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
        out.println("</font></td></tr>");
    }

    //
    //  Header row
    //
    out.println("<tr bgcolor=\"#336633\"><td>");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<p align=\"left\"><b>Stat</b></p>");
    out.println("</font></td>");
    out.println("<td colspan=\"2\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<p align=\"center\"><b>From " + start_month + "/" + start_day + "/" + start_year + " to");
    out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
    out.println("</font></td>");
        
    String courseClause = "courseName = ? AND ";
    
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; }
    
    //
    // use the dates provided to search the tee times tables
    //
    try {

        //
        //  Get the System Parameters for this Course
        //
        //getParms.getCourse(con, parmc, courseName); // do i need this?

        //
        //  First lets get the total rounds played for this course
        //

        total_course_rounds_9 = 0; // reset
        total_course_rounds_18 = 0; // reset

        tmp_error = "SQL1 - Build SQL"; //debug
        
        PreparedStatement pstmt1 = con.prepareStatement (
        "SELECT sum(p_9), sum(p_18) from (" +        
        "SELECT p1cw as cw, " +
                "p91 as p_9, IF(p91=0,1,0) as p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p2cw as cw,  " +
                "p92 as p_9, IF(p92=0,1,0) as p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p3cw as cw, " +
                "p93 as p_9, IF(p93=0,1,0) as p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p4cw as cw, " +
                "p94 as p_9, IF(p94=0,1,0) as p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p5cw as cw,  " +
                "p95 as p_9, IF(p95=0,1,0) as p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 IS NOT null)) " +
        ") as d_table;");

        pstmt1.clearParameters();
        tmp_error = "SQL1 - Set Parameters"; //debug
        if (courseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, sdate);
            pstmt1.setLong(2, edate);
            pstmt1.setLong(3, sdate);
            pstmt1.setLong(4, edate);
            pstmt1.setLong(5, sdate);
            pstmt1.setLong(6, edate);
            pstmt1.setLong(7, sdate);
            pstmt1.setLong(8, edate);
            pstmt1.setLong(9, sdate);
            pstmt1.setLong(10, edate);
        } else {
            pstmt1.setString(1, courseName);
            pstmt1.setLong(2, sdate);
            pstmt1.setLong(3, edate);
            pstmt1.setString(4, courseName);
            pstmt1.setLong(5, sdate);
            pstmt1.setLong(6, edate);
            pstmt1.setString(7, courseName);
            pstmt1.setLong(8, sdate);
            pstmt1.setLong(9, edate);
            pstmt1.setString(10, courseName);
            pstmt1.setLong(11, sdate);
            pstmt1.setLong(12, edate);
            pstmt1.setString(13, courseName);
            pstmt1.setLong(14, sdate);
            pstmt1.setLong(15, edate);
        }
        rs1 = pstmt1.executeQuery();

        tmp_error = "SQL1 - RS Loop"; //debug

        while ( rs1.next() ) {

            total_course_rounds_9 = rs1.getInt(1);
            total_course_rounds_18 = rs1.getInt(2);

        }

        pstmt1.close();            

        tmp_error = "SQL1 - RS Closed"; //debug
        
        out.println("</tr><tr>");                       // Grand totals
        out.println("<td align=\"left\">");
        out.println("<font size=\"2\"><br>");
        out.println("<b><u>Total Rounds Played:</u></b>");
        out.println("</font></td>");

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><br><b>");
        out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 18
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">18 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 9
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">9 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // blank row for divider
        out.println("<td colspan=\"3\">&nbsp;</td>");

        // end of course grand totals

    }
    catch (Exception exc) {

        displayDatabaseErrMsg("DEBUG: " + tmp_error + "<br>" + exc.getMessage(), exc.toString(), out);
        return;

    } // end of try/catch 

    // show member type detail
    displayMemberTypeQuery(courseName, sdate, edate, 12, total_course_rounds_18, total_course_rounds_9, con, out);

    // member type breakdown (by membership)
    displayMemberShipTypeQuery(courseName, sdate, edate, 12, total_course_rounds_18, total_course_rounds_9, con, out);

    // display guest type detail
    displayGuestTypeQuery(courseName, sdate, edate, 12, total_course_rounds_18, total_course_rounds_9, con, out);

    out.println("</font></tr></table><br>");

    count--;                         // decrement number of courses
    index++;
    courseName = course[index];      // get next course name, if more
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
    }
    
    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");
 } // end of doCustomDateDetail
 
 
 //*************************************************************************
 // Large Summary Report Containing Last Month, Month-to-Date, Year-to-Date
 //*************************************************************************
 //
 private void doRound(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession sess, Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block
   try {

      getClub.getParms(con, parm);        // get the club parms
   }
   catch (Exception ignore) {
   }

   long sdate = 0; 
   long edate = 0;                             // today's date
   long mtddate = 0;                           // MTD start date
   long ytddate = 0;                           // YTD start date
   long lmsdate = 0;                           // Last Month start date
   long lmedate = 0;                           // Last Month end date
   int year = 0;
   int month = 0;
   int day = 0;
     
   int multi = 0;                 // multiple course support
   int index = 0;
   int i = 0;
   int count = 0;                 // number of courses

   String [] course = new String [20];                     // max of 20 courses per club
   String courseName = "";        // course names
   String error = "None";

   String lm_start_date = "";
   String mtd_start_date = "";
   String ytd_start_date = "";
   String lm_end_date = "";
   String mtd_end_date = "";
   String ytd_end_date = "";
   
   String bgrndcolor = "#336633";      // default
   String fontcolor = "#FFFFFF";      // default

   //
   //  Get today's date and current time and calculate date & time values 
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_am_pm = cal.get(Calendar.AM_PM);        // current time
   int cal_hour = cal.get(Calendar.HOUR);
   int cal_min = cal.get(Calendar.MINUTE);

   int curr_time = cal_hour;
   if (cal_am_pm == 1) {                       // if PM

      curr_time = curr_time + 12;              // convert to military time
   }

   curr_time = curr_time * 100;                // create current time value for compare
   curr_time = curr_time + cal_min;

   month = month + 1;                           // month starts at zero

   mtd_start_date = year + "-" + month + "-01";
   mtd_end_date = year + "-" + month + "-" + day;
   ytd_start_date = year + "-01-01";
   ytd_end_date = year + "-" + month + "-" + day;
   
   edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
   edate = edate + (month * 100);
   edate = edate + day;                         // date = yyyymmdd (for comparisons)

   mtddate = year * 10000;                      // create a MTD date
   mtddate = mtddate + (month * 100);
   mtddate = mtddate + 01;

   ytddate = year * 10000;                      // create a YTD date
   ytddate = ytddate + 100;
   ytddate = ytddate + 01;

   month = month - 1;                           // last month

   if (month == 0) {
      
      month = 12;
      year = year - 1;
   }
   
   lmsdate = year * 10000;                      // create a Last Month Start date
   lmsdate = lmsdate + (month * 100);
   lmsdate = lmsdate + 01;
   
   lmedate = lmsdate + 30;                      // create a Last Month End date
   
   lm_start_date = year + "-" + month + "-01";
   lm_end_date = year + "-" + month + "-30";
   
   //
   // Check for multiple courses
   //
   multi = parm.multi;
   count = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      while (index< 20) {

         course[index] = "";       // init the course array
         index++;
      }

      index = 0;

      try {

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count = index;                      // number of courses

      }
      catch (Exception exc) {

         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   
   //
   //  Build the HTML page to display search results
   //
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   if (req.getParameter("excel") == null) {     // if normal request
      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\">");
      
      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> Percentages are rounded down to whole number.</p>");
      out.println(buildDisplayDateTime());
      out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"round\" value=\"all\">");
      out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></p>");
      out.println("</font>");
   }

   courseName = "";            // init as not multi
   index = 0;

   if (multi != 0) {           // if multiple courses supported for this club

      courseName = course[index];      // get first course name 
   }

   //
   // execute searches and display for each course
   //
   while (count > 0) {

      //
      // use the dates provided to search the stats table
      //
      try {


      }
      catch (Exception exc) {

         out.println("<BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Exception:" + exc.getMessage());
         out.println("<BR><BR>Error:" + error);
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

       
      if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
         out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" cols=\"7\">");
         bgrndcolor = "#FFFFFF";      // white for excel
         fontcolor = "#000000";      // black for excel
      } else {
         out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" cols=\"7\">");
      }
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

      //
      // add course name header if multi
      //
      if (!courseName.equals( "" )) {

         out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"7\">");
         out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
         out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
         out.println("</font></td></tr>");
      }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" +bgrndcolor+ "\">");
        out.println("<td>");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Last Month<br>(" + month + "/" + year + ")</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Month To Date</b><br>(excludes today)</p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Year To Date</b><br>(excludes today)</p>");
        out.println("</font></td>");
            
        displayCourseSummaryBig(courseName, con, out);            

        if (req.getParameter("excel") == null) {
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td><td></td><td></td>");

            // button links to detail reports
            out.println("</tr><tr valign=bottom>");
            out.println("<td>&nbsp;</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=lm><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=\"submit\" value=\" LM Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=hidden name=cal_box_0 value=\"" + lm_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + lm_end_date + "\"><input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=mtd><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=\"submit\" value=\" MTD Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=hidden name=cal_box_0 value=\"" + mtd_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + mtd_end_date + "\"><input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=ytd><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=\"submit\" value=\" YTD Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=hidden name=cal_box_0 value=\"" + ytd_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + ytd_end_date + "\"><input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</td>");
        
        } // end if not excel

        out.println("</font></tr><tr><td colspan=\"7\"></td></tr></table><br>");
     

        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more

    } // end of while Courses - do all courses
   

    // display the Grand Total of All Courses Combined
    if (multi != 0) {

        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" cols=\"7\">");
        } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" cols=\"7\">");
        }

        out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td colspan=\"7\">");
        out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
        out.println("<p align=\"center\"><b>Grand Total of all Courses</b></p>");
        out.println("</font></td></tr>");

        out.println("<tr bgcolor=\"" + bgrndcolor + "\">");
        out.println("<td>");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Last Month</b><br>(" + month + "/" + year + ")</p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Month To Date</b><br>(excludes today)</p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Year To Date</b><br>(excludes today)</p>");
        out.println("</font></td>");

        displayCourseSummaryBig("-ALL-", con, out);
        
        if (req.getParameter("excel") == null) {
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td><td></td><td></td>");

            // button links to detail reports
            out.println("</tr><tr valign=bottom>");
            out.println("<td>&nbsp;</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=lm><input type=hidden name=detail><input type=hidden name=course value=\"-ALL-\"><input type=\"submit\" value=\" LM Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"-ALL-\"><input type=hidden name=cal_box_0 value=\"" + lm_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + lm_end_date + "\"><input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=mtd><input type=hidden name=detail><input type=hidden name=course value=\"-ALL-\"><input type=\"submit\" value=\" MTD Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"-ALL-\"><input type=hidden name=cal_box_0 value=\"" + mtd_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + mtd_end_date + "\"><input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=ytd><input type=hidden name=detail><input type=hidden name=course value=\"-ALL-\"><input type=\"submit\" value=\" YTD Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"-ALL-\"><input type=hidden name=cal_box_0 value=\"" + ytd_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + ytd_end_date + "\"><input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</td>");
        
        } // end if not excel

        out.println("</tr></table>");
        
   } // end if display combind course totals
   
   
   out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {     // if normal request

        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
      
    }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
     

 } // end of doRound
 
 
 //**************************************************
 // Detailed Reports 
 //**************************************************
 //
 private void doLastMonthDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {


    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String club = (String)sess.getAttribute("club");      // get club name
    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    parmClub parm = new parmClub();          // allocate a parm block
    //parmCourse parmc = new parmCourse();          // allocate a parm block
    long sdate = 0;
    int lottery = Integer.parseInt(templott);    long edate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int multi = 0;                 // multiple course support
    int index = 0;
    int i = 0;
    int count = 0;                 // number of courses
    int rs_rows1 = 0;
    int tmp_index = 0;
    int total_course_rounds_9 = 0;
    int total_course_rounds_18 = 0;
    boolean new_row = false;
    String tmp_error = "";
    String [] course = new String [20];             // max of 20 courses per club
    String courseName = "";                         // course names

    //
    //   Get multi option, member types, and guest types
    //
    try {

      getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }

    //
    //  Get today's date and set our date vars to last month
    //
    Calendar cal = new GregorianCalendar();         // get todays date
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH); // + 1;            // month starts at zero
    day = cal.get(Calendar.DAY_OF_MONTH);
    sdate = (year * 10000) + (month * 100);         // create a edate field of yyyymmdd
    edate = sdate;
    sdate += 1;
    edate += 31;
   
    //out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        while (index< 20) {

            course[index] = "";       // init the course array
            index++;
        }

        index = 0;

        try {

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName " +
                            "FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
            }
            stmt.close();
            count = index;                      // number of courses

        }
        catch (Exception exc) {
            displayDatabaseErrMsg("Can not establish connection.", "", out);
            return;
        }
        
    } // end if multiple courses for this club

    //
    //  Start to build the HTML page to display stats
    //

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Course Statistics for Last Month<br>");
    out.println(month + "/" + year);
    out.println("</b><br></font><font size=\"2\"><b>Note:</b> Percentages are rounded down to whole number.<br>");
    out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");
    
    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course[index];  // if multiple courses supported for this club get first course name

    // if there is a course name being passed here in the querystring 
    // then just display that course not all of them
    if (req.getParameter("course") != null) {
        courseName = req.getParameter("course");
        count = 1;
    }
    
    String courseClause = "courseName = ? AND";
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; }
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"lm\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        if (count == 1) { out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">"); }
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    // start the main tables that holds each course table
    out.println("<table cellspacing=10>");

    //
    // loop each course and display stats
    //
    while (count > 0) {

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr><td>" : "</td><td>");

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\"><td>");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"center\"><b>Last Month</b></p>");
        out.println("</font></td>");
        
                     
        //
        // use the dates provided to search the tee times tables
        //
        try {

            //
            //  Get the System Parameters for this Course
            //
            //getParms.getCourse(con, parmc, courseName); // do i need this?
            
            //
            //  First lets get the total rounds played for this course
            //
            
            total_course_rounds_9 = 0; // reset
            total_course_rounds_18 = 0; // reset
            
            PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT sum(p_9), sum(p_18) from (" +        
            "SELECT p1cw as cw, " +
                    "p91 as p_9, IF(p91=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw as cw,  " +
                    "p92 as p_9, IF(p92=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw as cw, " +
                    "p93 as p_9, IF(p93=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw as cw, " +
                    "p94 as p_9, IF(p94=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw as cw,  " +
                    "p95 as p_9, IF(p95=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 IS NOT null)) " +
            ") as d_table;");
            
            pstmt1.clearParameters();
            if (courseName.equalsIgnoreCase("-ALL-")) {
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, edate);
                pstmt1.setLong(3, sdate);
                pstmt1.setLong(4, edate);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setLong(7, sdate);
                pstmt1.setLong(8, edate);
                pstmt1.setLong(9, sdate);
                pstmt1.setLong(10, edate);
            } else {
                pstmt1.setString(1, courseName);
                pstmt1.setLong(2, sdate);
                pstmt1.setLong(3, edate);
                pstmt1.setString(4, courseName);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setString(7, courseName);
                pstmt1.setLong(8, sdate);
                pstmt1.setLong(9, edate);
                pstmt1.setString(10, courseName);
                pstmt1.setLong(11, sdate);
                pstmt1.setLong(12, edate);
                pstmt1.setString(13, courseName);
                pstmt1.setLong(14, sdate);
                pstmt1.setLong(15, edate);
            }
            rs1 = pstmt1.executeQuery();
            
            tmp_error = "SQL1 - Overall Totals2"; //debug
            
            while ( rs1.next() ) {
            
                total_course_rounds_9 = rs1.getInt(1);
                total_course_rounds_18 = rs1.getInt(2);
                
            }
            
            pstmt1.close();            
            
            out.println("</tr><tr>");                       // Grand totals
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b><u>Total Rounds Played:</u></b>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 18
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 9
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"3\">&nbsp;</td>");
            
            // end of course grand totals
           
        }
        catch (Exception exc) {
            
            displayDatabaseErrMsg(tmp_error, exc.toString() , out);
            return;
            
        } // end of try/catch 
            
        // show member type detail
        displayMemberTypeQuery(courseName, sdate, edate, 14, total_course_rounds_18, total_course_rounds_9, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, edate, 14, total_course_rounds_18, total_course_rounds_9, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, edate, 14, total_course_rounds_18, total_course_rounds_9, con, out);

        out.println("</font></tr></table><br>");

        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
    }
    
    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");

 }  // end of doLastMonthDetail
 
 
 private void doThisMonthDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {


    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String club = (String)sess.getAttribute("club");      // get club name
    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    parmClub parm = new parmClub();          // allocate a parm block
    //parmCourse parmc = new parmCourse();          // allocate a parm block
    long sdate = 0;
    int lottery = Integer.parseInt(templott);    long edate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int multi = 0;                 // multiple course support
    int index = 0;
    int i = 0;
    int count = 0;                 // number of courses
    int rs_rows1 = 0;
    int tmp_index = 0;
    int total_course_rounds_9 = 0;
    int total_course_rounds_18 = 0;
    boolean new_row = false;
    String tmp_error = "";
    String [] course = new String [20];             // max of 20 courses per club
    String courseName = "";                         // course names

    //
    //   Get multi option, member types, and guest types
    //
    try {

      getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }

    //
    //  Get today's date and current time and calculate date & time values
    //
    Calendar cal = new GregorianCalendar();         // get todays date
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH) + 1;            // month starts at zero
    day = cal.get(Calendar.DAY_OF_MONTH);
    sdate = (year * 10000) + (month * 100);         // create a edate field of yyyymmdd
    edate = sdate;
    sdate += 1;
    edate += 31;
   
    //out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        while (index< 20) {

            course[index] = "";       // init the course array
            index++;
        }

        index = 0;

        try {

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName " +
                            "FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
            }
            stmt.close();
            count = index;                      // number of courses

        }
        catch (Exception exc) {
            displayDatabaseErrMsg("Can not establish connection.", "", out);
            return;
        }
        
    } // end if multiple courses for this club

    //
    //  Start to build the HTML page to display stats
    //

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Course Statistics for This Month<br>");
    out.println(month + "/" + year);
    out.println("</b><br></font><font size=\"2\"><b>Note:</b> Percentages are rounded down to whole number.<br>");
    out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");
    
    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course[index];  // if multiple courses supported for this club get first course name

    // if there is a course name being passed here in the querystring 
    // then just display that course not all of them
    if (req.getParameter("course") != null) {
        courseName = req.getParameter("course");
        count = 1;
    }
    
    String courseClause = "courseName = ? AND";
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; }
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"mtd\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        if (count == 1) { out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">"); }
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    // start the main tables that holds each course table
    out.println("<table cellspacing=10>");

    //
    // loop each course and display stats
    //
    while (count > 0) {

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr><td>" : "</td><td>");

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\"><td>");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"center\"><b>This Month</b></p>");
        out.println("</font></td>");
        
                     
        //
        // use the dates provided to search the tee times tables
        //
        try {

            //
            //  Get the System Parameters for this Course
            //
           
            //getParms.getCourse(con, parmc, courseName); // do i need this?  -- commented out 11-22
            
            //
            //  First lets get the total rounds played for this course
            //
            
            total_course_rounds_9 = 0; // reset
            total_course_rounds_18 = 0; // reset
            
            PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT sum(p_9), sum(p_18) from (" +        
            "SELECT p1cw as cw, " +
                    "p91 as p_9, IF(p91=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw as cw,  " +
                    "p92 as p_9, IF(p92=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw as cw, " +
                    "p93 as p_9, IF(p93=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw as cw, " +
                    "p94 as p_9, IF(p94=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw as cw,  " +
                    "p95 as p_9, IF(p95=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 IS NOT null)) " +
            ") as d_table;");
            
            pstmt1.clearParameters();
            if (courseName.equalsIgnoreCase("-ALL-")) {
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, edate);
                pstmt1.setLong(3, sdate);
                pstmt1.setLong(4, edate);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setLong(7, sdate);
                pstmt1.setLong(8, edate);
                pstmt1.setLong(9, sdate);
                pstmt1.setLong(10, edate);
            } else {
                pstmt1.setString(1, courseName);
                pstmt1.setLong(2, sdate);
                pstmt1.setLong(3, edate);
                pstmt1.setString(4, courseName);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setString(7, courseName);
                pstmt1.setLong(8, sdate);
                pstmt1.setLong(9, edate);
                pstmt1.setString(10, courseName);
                pstmt1.setLong(11, sdate);
                pstmt1.setLong(12, edate);
                pstmt1.setString(13, courseName);
                pstmt1.setLong(14, sdate);
                pstmt1.setLong(15, edate);
            }
            rs1 = pstmt1.executeQuery();
            
            tmp_error = "SQL1 - Overall Totals2"; //debug
            
            while ( rs1.next() ) {
            
                total_course_rounds_9 = rs1.getInt(1);
                total_course_rounds_18 = rs1.getInt(2);
                
            }
            
            pstmt1.close();            
            
            out.println("</tr><tr>");                       // Grand totals
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b><u>Total Rounds Played:</u></b>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 18
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 9
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"3\">&nbsp;</td>");
            
            // end of course grand totals
           
        }
        catch (Exception exc) {
            
            displayDatabaseErrMsg(tmp_error, exc.toString() , out);
            return;
            
        } // end of try/catch 
            
        // show member type detail
        displayMemberTypeQuery(courseName, sdate, edate, 15, total_course_rounds_18, total_course_rounds_9, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, edate, 15, total_course_rounds_18, total_course_rounds_9, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, edate, 15, total_course_rounds_18, total_course_rounds_9, con, out);

        out.println("</font></tr></table><br>");

        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
    }
    
    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");

 }  // end of doThisMonthDetail
 
 
 private void doThisYearDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {


    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    String club = (String)sess.getAttribute("club");      // get club name
    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    parmClub parm = new parmClub();          // allocate a parm block
    //parmCourse parmc = new parmCourse();          // allocate a parm block
    long sdate = 0;
    int lottery = Integer.parseInt(templott);
    long edate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int multi = 0;                 // multiple course support
    int index = 0;
    int i = 0;
    int count = 0;                 // number of courses
    int rs_rows1 = 0;
    int tmp_index = 0;
    int total_course_rounds_9 = 0;
    int total_course_rounds_18 = 0;
    boolean new_row = false;
    String tmp_error = "";
    String [] course = new String [20];             // max of 20 courses per club
    String courseName = "";                         // course names

    //
    //   Get multi option, member types, and guest types
    //
    try {

      getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }

    //
    //  Get today's date and current time and calculate date & time values
    //
    Calendar cal = new GregorianCalendar();         // get todays date
    year = cal.get(Calendar.YEAR);
    sdate = (year * 10000) + 100;         // create a edate field of yyyymmdd
    edate = sdate + 1100;
    sdate += 1;
    edate += 31;
   
    //out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        while (index< 20) {

            course[index] = "";       // init the course array
            index++;
        }

        index = 0;

        try {

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName " +
                            "FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < 20) {

                courseName = rs.getString(1);

                course[index] = courseName;      // add course name to array
                index++;
            }
            stmt.close();
            count = index;                      // number of courses

        }
        catch (Exception exc) {
            displayDatabaseErrMsg("Can not establish connection.", "", out);
            return;
        }
        
    } // end if multiple courses for this club

    //
    //  Start to build the HTML page to display stats
    //

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Course Statistics for This Year</b><br></font><font size=\"2\">");
    out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
    out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");

    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course[index];  // if multiple courses supported for this club get first course name

    // if there is a course name being passed here in the querystring 
    // then just display that course not all of them
    if (req.getParameter("course") != null) {
        courseName = req.getParameter("course");
        count = 1;
    }
    
    String courseClause = "courseName = ? AND";
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; }
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"ytd\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        if (count == 1) { out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">"); }
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    // start the main tables that holds each course table
    out.println("<table cellspacing=10>");

    //
    // loop each course and display stats
    //
    while (count > 0) {

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>");

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"3\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\"><td>");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<p align=\"center\"><b>This Year</b></p>");
        out.println("</font></td>");
        
                     
        //
        // use the dates provided to search the tee times tables
        //
        try {

            //
            //  Get the System Parameters for this Course
            //
            //getParms.getCourse(con, parmc, courseName); // do i need this?
            
            //
            //  First lets get the total rounds played for this course
            //
            
            total_course_rounds_9 = 0; // reset
            total_course_rounds_18 = 0; // reset
            
            PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT sum(p_9), sum(p_18) from (" +        
            "SELECT p1cw as cw, " +
                    "p91 as p_9, IF(p91=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw as cw,  " +
                    "p92 as p_9, IF(p92=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw as cw, " +
                    "p93 as p_9, IF(p93=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw as cw, " +
                    "p94 as p_9, IF(p94=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw as cw,  " +
                    "p95 as p_9, IF(p95=0,1,0) as p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 IS NOT null)) " +
            ") as d_table;");
            
            pstmt1.clearParameters();
            if (courseName.equalsIgnoreCase("-ALL-")) {
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, edate);
                pstmt1.setLong(3, sdate);
                pstmt1.setLong(4, edate);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setLong(7, sdate);
                pstmt1.setLong(8, edate);
                pstmt1.setLong(9, sdate);
                pstmt1.setLong(10, edate);
            } else {
                pstmt1.setString(1, courseName);
                pstmt1.setLong(2, sdate);
                pstmt1.setLong(3, edate);
                pstmt1.setString(4, courseName);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, edate);
                pstmt1.setString(7, courseName);
                pstmt1.setLong(8, sdate);
                pstmt1.setLong(9, edate);
                pstmt1.setString(10, courseName);
                pstmt1.setLong(11, sdate);
                pstmt1.setLong(12, edate);
                pstmt1.setString(13, courseName);
                pstmt1.setLong(14, sdate);
                pstmt1.setLong(15, edate);
            }
            rs1 = pstmt1.executeQuery();
            
            tmp_error = "SQL1 - Overall Totals2"; //debug
            
            while ( rs1.next() ) {
            
                total_course_rounds_9 = rs1.getInt(1);
                total_course_rounds_18 = rs1.getInt(2);
                
            }
            
            pstmt1.close();            
            
            out.println("</tr><tr>");                       // Grand totals
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b><u>Total Rounds Played:</u></b>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><br><b>");
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 18
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 9
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"3\">&nbsp;</td>");
            
            // end of course grand totals
           
        }
        catch (Exception exc) {
            
            displayDatabaseErrMsg(tmp_error, exc.toString() , out);
            return;
            
        } // end of try/catch 
            
        // show member type detail
        displayMemberTypeQuery(courseName, sdate, edate, 16, total_course_rounds_18, total_course_rounds_9, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, edate, 16, total_course_rounds_18, total_course_rounds_9, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, edate, 16, total_course_rounds_18, total_course_rounds_9, con, out);

        out.println("</font></tr></table><br>");

        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
    }

    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");

 }  // end of doThisYearDetail
 
  
 private void doTransDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
    
    // 
    //  Declare our local variables
    //
    long sdate = 0;
    long edate = 0;
    int curr_time = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    boolean new_row = false;
    int index = 0;
    int count = 1; // default is 1 course
    int multi = 0;
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    String error_text = "";
    String [] course = new String [20];
    String courseName = "";
    
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
    
    } catch (Exception e) {
        // invalid dates here, bailout and call form again
        //getCustomDate(req, out, con);
        displayDatabaseErrMsg("Can not get valid dates.", "", out);
        return;
    }
    
    //
    // IF WE ARE STILL HERE THEN WE HAVE A VALID DATE RANGE SUPPLIED BY THE USER
    //
    
    
    // build our date variables for use in query
    sdate = start_year * 10000;                    // create a date field of yyyymmdd
    sdate = sdate + (start_month * 100);
    sdate = sdate + start_day;

    edate = end_year * 10000;                      // create a date field of yyyymmdd
    edate = edate + end_month * 100;
    edate = edate + end_day;
   
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | courseName=" + courseName + " -->");
       
    Statement stmt = null;
    ResultSet rs = null; 
    
    String club = (String)sess.getAttribute("club");      // get club name

    //
    //   Get multi setting
    //
    parmClub parm = new parmClub();          // allocate a parm block
    try {
        getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }
    multi = parm.multi;
    
    if (multi != 0) {           // if multiple courses supported for this club

        while (index< 20) {
            course[index] = "";       // init the course array
            index++;
        }

        index = 0;

        try {

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement
            rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < 20) {

                courseName = rs.getString(1);
                course[index] = courseName;      // add course name to array
                index++;

            }
            stmt.close();
            count = index;                      // number of courses

        }
        catch (Exception exc) {
            displayDatabaseErrMsg("Can not establish connection.", "", out);
            return;
        } // end try
        
    } // end if multiple courses for this club

    
    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course[index];  // if multiple courses supported for this club get first course name

    // if there is a course name being passed here in the querystring 
    // then just display that course not all of them
    if (req.getParameter("course") != null) {
        courseName = req.getParameter("course");
        count = 1;
    }
    
    // start the main tables that holds each course table
    
    //
    //  Start to build the HTML page to display stats
    //

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Transportation Statistics</b><br></font><font size=\"2\">");
    out.println("<b>Note:</b> Percentages are rounded down to whole number.<br>");
    out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"trans\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
        out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
        if (count == 1) { out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">"); }
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    out.println("<table cellspacing=10>");

    //
    // loop each course and display stats
    //
    while (count > 0) {

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>");

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"#336633\"><td colspan=\"4\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail<br>");
            out.println(start_month + "/" + start_day + "/" + start_year + " - " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"#336633\">");
        out.println("<td><font color=\"#FFFFFF\" size=\"2\" nowrap>");
        out.println("<b>Transportaion Mode</b>");
        out.println("</font></td><td><font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Member Type / Membership Type</b>");
        out.println("</font></td><td colspan=\"2\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
        out.println("<b>Count</b>");
        out.println("</font></td></tr>");

        displayTransportationQuery(courseName, sdate, edate, 13, con, out);
        
        out.println("</font></tr></table><br>");
        
        
        count--;                         // decrement number of courses
        index++;
        courseName = course[index];      // get next course name, if more
        
        
    } // end while do all courses
    
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<center><form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\"> &nbsp; &nbsp; ");
        out.println("<input type=\"button\" value=\"Back\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("</form></font></center>");
    }
   
   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   
 } // end of doTransDetail
 

 //**************************************************
 // Common Methods for Generating Specific Reports 
 //**************************************************
 //
 private void displayTransportationQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, Connection con, PrintWriter out) {

    //out.println("<!-- pStartDate=" + pStartDate + " | pEndDate=" + pEndDate + " | pCourseName=" + pCourseName + "-->");

    Statement stmt = null;
    ResultSet rs = null;

    int tmp_trans_types = 0;
    int tmp_mem_types = 0;
    int tmp_mship_types = 0;
    int tot9rounds = 0;
    int tot18rounds = 0;
    int totRounds = 0;
    int rs_rows = 0;
    int tmp_index = 0;
    
    int [] trans_mship9Total = new int [120];
    int [] trans_mship18Total = new int [120];
    int [] trans_type9Total = new int [120];
    int [] trans_type18Total = new int [120];
    int [] fld_sum9 = new int [220];
    int [] fld_sum18 = new int [220];

    String error_text = "";
    
    String [] trans_type = new String [120];
    String [] transa_type = new String [120];
    String [] mship_type = new String [120];
    String [] mem_type = new String [120];

    String [] fld_tmode = new String [220];
    String [] fld_cw = new String [220];
    String [] fld_mship = new String [220];
    String [] fld_mtype = new String [220];
    
    String tableName = (pReportType == 1) ? "teecurr2" : "teepast2";
    String sqlQuery = "" + 
         "SELECT tmode, cw, ifnull(m_ship,if(null_user=1,'ZZZGuest','Removed')) AS m_ship, " + 
         "ifnull(m_type, ifnull(guest_type,if(null_user=1,'Unknown','Removed from Member Database'))) AS m_or_g_type, sum(p_9), sum(p_18) " + 
            "FROM ( " + 
            "SELECT p1cw AS cw, " +
                "p91 AS p_9, IF(p91=0,1,0) AS p_18, " +
                "m_type, m_ship, guest AS guest_type, " +
                "if(ifnull(username1,'')='',1,0) AS null_user " +
                "FROM " + tableName + " " +
                "LEFT OUTER JOIN member2b ON username1 = username " +
                "LEFT OUTER JOIN guest5 ON guest = left(player1,length(guest)) ";
    sqlQuery += buildWhereClause(pReportType, pCourseName);
    sqlQuery += "AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p2cw AS cw,  " +
                "p92 AS p_9, IF(p92=0,1,0) AS p_18, " +
                "m_type, m_ship, guest AS guest_type, " +
                "if(ifnull(username2,'')='',1,0) AS null_user " +
                "FROM " + tableName + " " +
                "LEFT OUTER JOIN member2b ON username2 = username " +
                "LEFT OUTER JOIN guest5 ON guest = left(player2,length(guest)) ";
    sqlQuery += buildWhereClause(pReportType, pCourseName);
    sqlQuery += "AND show2 = 1 AND ((username2 <> '' and username2 IS NOT null) OR (player2 <> '' AND player2 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p3cw AS cw, " +
                "p93 AS p_9, IF(p93=0,1,0) AS p_18, " +
                "m_type, m_ship, guest AS guest_type, " +
                "if(ifnull(username3,'')='',1,0) AS null_user " +
                "FROM " + tableName + " " +
                "LEFT OUTER JOIN member2b ON username3 = username " +
                "LEFT OUTER JOIN guest5 ON guest = left(player3,length(guest)) ";
    sqlQuery += buildWhereClause(pReportType, pCourseName);
    sqlQuery += "AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p4cw AS cw, " +
                "p94 AS p_9, IF(p94=0,1,0) AS p_18, " +
                "m_type, m_ship, guest AS guest_type, " +
                "if(ifnull(username4,'')='',1,0) AS null_user " +
                "FROM " + tableName + " " +
                "LEFT OUTER JOIN member2b ON username4 = username " +
                "LEFT OUTER JOIN guest5 ON guest = left(player4,length(guest)) ";
    sqlQuery += buildWhereClause(pReportType, pCourseName);
    sqlQuery += "AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p5cw AS cw, " +
                "p95 AS p_9, IF(p95=0,1,0) AS p_18, " +
                "m_type, m_ship, guest AS guest_type, " +
                "if(ifnull(username5,'')='',1,0) AS null_user " +
                "FROM " + tableName + " " +
                "LEFT OUTER JOIN member2b ON username5 = username " +
                "LEFT OUTER JOIN guest5 ON guest = left(player5,length(guest)) ";
    sqlQuery += buildWhereClause(pReportType, pCourseName);
    sqlQuery += "AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 IS NOT null)) " +
        "UNION ALL " +
        "SELECT tmodea AS cw, p_9, p_18, m_type, m_ship, guest_type, null_user FROM ( " +
                "SELECT (0) AS p_9, (0) AS p_18, " +
                        "null AS m_type, null AS m_ship, guest AS guest_type, " +
                        "(1) AS null_user " +
                        "FROM guest5 " +
                "UNION ALL " +
                "SELECT (0) AS p_9, (0) AS p_18, m2b.m_type AS m_type, m5.mship AS m_ship, null AS guest_type, (1) AS null_user " +
                        "FROM member2b m2b, mship5 m5 " +
                        "WHERE m2b.m_type IS NOT null " +
                        "GROUP BY m5.mship, m2b.m_type " +
        //") AS null_entries, tmodes WHERE courseName = ? " +
        ") AS null_entries, tmodes";
    sqlQuery += (pCourseName.equalsIgnoreCase("-ALL-")) ? "" : " WHERE courseName = ?";
    sqlQuery += ") AS d_table LEFT OUTER JOIN tmodes ON (tmodea = cw"; 
    sqlQuery += (pCourseName.equalsIgnoreCase("-ALL-")) ? "" : " AND courseName = ?";
    sqlQuery += ") GROUP BY cw, m_ship, m_or_g_type WITH ROLLUP;";

    out.println("<!-- " + sqlQuery + " -->");
    
    error_text = "SQL";
        
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();

        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, pStartDate);
            pstmt1.setLong(2, pEndDate);
            pstmt1.setLong(3, pStartDate);
            pstmt1.setLong(4, pEndDate);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setLong(7, pStartDate);
            pstmt1.setLong(8, pEndDate);
            pstmt1.setLong(9, pStartDate);
            pstmt1.setLong(10, pEndDate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, pStartDate);
            pstmt1.setLong(3, pEndDate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, pStartDate);
            pstmt1.setLong(9, pEndDate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, pStartDate);
            pstmt1.setLong(12, pEndDate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, pStartDate);
            pstmt1.setLong(15, pEndDate);
            pstmt1.setString(16, pCourseName);
            pstmt1.setString(17, pCourseName);
        }
                
        rs = pstmt1.executeQuery();

        error_text = "Loop1";

        while ( rs.next() ) {

            if (rs.getString(2) == null && rs.getString(3) == null && rs.getString(4) == null) {

                // grand total of all rounds gathered for this report
                tot9rounds = rs.getInt(5);
                tot18rounds = rs.getInt(6);
                totRounds = tot9rounds + tot18rounds;

            } else {

                if (rs.getString(3) == null && rs.getString(4) == null) {

                    // total 9/18 rounds for a particular trans mode
                    trans_type9Total[tmp_trans_types] = rs.getInt(5);
                    trans_type18Total[tmp_trans_types] = rs.getInt(6);

                    tmp_trans_types++;

                } else {

                    if (rs.getString(3) != null && rs.getString(4) == null) {

                        // total rounds for particular membership type
                        trans_mship9Total[tmp_mship_types] = rs.getInt(5);
                        trans_mship18Total[tmp_mship_types] = rs.getInt(6);

                        tmp_mship_types++;

                    } else {

                        error_text = "Loop1 - " + rs_rows;
                        fld_tmode[rs_rows] = rs.getString(1);
                        fld_cw[rs_rows] = rs.getString(2); 
                        fld_mship[rs_rows] = rs.getString(3); 
                        fld_mtype[rs_rows] = rs.getString(4); 
                        fld_sum9[rs_rows] = rs.getInt(5);
                        fld_sum18[rs_rows] = rs.getInt(6);

                        if (fld_mship[rs_rows].equals("ZZZGuest")) fld_mship[rs_rows] = "Guest";
                        
                        // hack
                        if (fld_tmode[rs_rows] == null) { out.println("<!-- NULL FOUND AT " + rs_rows + " -->"); fld_tmode[rs_rows] = ""; }
                        
                        rs_rows++;

                    }

                }

            }

        }

        pstmt1.close();
        
        String trans_mode_name = "";
        String tmp_mship_type = "";
        tmp_trans_types = 0;
        tmp_mship_types = 0;

        error_text = "Loop2";
        boolean firstrow = true;

        while (tmp_index < rs_rows) {

            //error_text = "Loop2 -- " + tmp_index + "/" + rs_rows;
            if (!trans_mode_name.equalsIgnoreCase(fld_tmode[tmp_index])) {
                error_text = "Loop2 -- here";
                trans_mode_name = fld_tmode[tmp_index];

            //error_text = "Loop2 - Q " + tmp_index + "/" + rs_rows;
                out.println("<tr>"); // spacer row
                out.println("<td>&nbsp;</td><td width=200>&nbsp;</td><td colspan=\"2\">&nbsp;</td>");
                out.println("</tr>");
                
                if (firstrow != true) {
                    out.println("<tr>");
                    out.println("<td colspan=4><hr></td>");
                    out.println("</tr>");
                    out.println("<tr>");  // spacer row
                    out.println("<td></td><td width=200></td><td></td>");
                    out.println("</tr>");
                } else { firstrow = false; }
                
                out.println("<tr>");
                out.println("<td nowrap>");
                out.println("<font size=\"3\">");
                out.println("<b><u>" + trans_mode_name + "</u>:</b>");
                out.println("</font></td><td></td>");
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\"><b>");
                out.println(buildDisplayValue(trans_type9Total[tmp_trans_types], trans_type18Total[tmp_trans_types], totRounds));
                out.println("</b></font></td></tr>");

                out.println("<tr>"); // spacer row
                out.println("<td></td><td width=\"200\"></td><td colspan=\"2\"></td>");
                out.println("</tr>");
                
                tmp_trans_types++;

            }

            error_text = "Loop2 A " + tmp_index + "/" + rs_rows;
            if (!tmp_mship_type.equals(fld_mship[tmp_index])) {
                tmp_mship_type = fld_mship[tmp_index];

                out.println("<tr><td colspan=\"4\"></td></tr>");
                out.println("<tr>");
                out.print("<td></td><td nowrap><font size=\"2\"><b><u>");
                
                //out.print(tmp_mship_type.equals("Guest") ? "Guest" : "Membership");
                //out.print(" Type:&nbsp; " + tmp_mship_type + "</u></b>");
                
                if (tmp_mship_type.equals("Guest")) {
                    
                    out.print(" By Guest Type:&nbsp;");
                    
                } else {
                    
                    out.print("Membership Type:&nbsp; " + tmp_mship_type);
                    
                }
                out.println("</u></b>");
                
                out.println("</font></td>");
                out.println("<td align=\"center\" nowrap><font size=\"2\"><b>");
                out.println(buildDisplayValue(trans_mship9Total[tmp_mship_types], trans_mship18Total[tmp_mship_types], totRounds));
                out.println("</b></font></td>");
                out.println("</tr>");
                out.println("<tr><td colspan=\"4\"></td></tr>");
                
                tmp_mship_types++;

            }

            error_text = "Loop2 B " + tmp_index + "/" + rs_rows;
            if (fld_sum9[tmp_index] + fld_sum18[tmp_index] > 0) {

                out.println("<tr>");
                out.println("<td></td><td align=\"right\">"); //was center
                out.println("<font size=\"2\"><b>");
                out.println(fld_mtype[tmp_index]);
                out.println("</b></font></td>");
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\"><b>");
                out.println(buildDisplayValue(fld_sum9[tmp_index], fld_sum18[tmp_index], totRounds));
                out.println("</b></font></td>");

            error_text = "Loop2 C " + tmp_index + "/" + rs_rows;
                if (fld_sum18[tmp_index] > 0) {
                    out.println("</tr><tr>");
                    out.println("<td></td><td align=\"right\">");
                    out.println("<font size=\"2\">18 Hole Rounds:");
                    out.println("</font></td>");
                    out.println("<td align=\"center\" nowrap><font size=\"2\">");
                    out.println(buildDisplayValue(fld_sum18[tmp_index], 0, totRounds));
                    out.println("</font></td>");
                }

            error_text = "Loop2 D " + tmp_index + "/" + rs_rows;
                if (fld_sum9[tmp_index] > 0) {
                    out.println("</tr><tr>");
                    out.println("<td></td><td align=\"right\">");
                    out.println("<font size=\"2\">9 Hole Rounds:");
                    out.println("</font></td>");
                    out.println("<td align=\"center\" nowrap><font size=\"2\">");
                    out.println(buildDisplayValue(fld_sum9[tmp_index], 0, totRounds));
                    out.println("</font></td>");
                }

                out.println("</tr>");

            } // end if block (whether or not to show this data - if 0 count then don't show to cut down report length)

            tmp_index++;

        } // end while loop for displaying processed data


    } catch (Exception exc) {

        displayDatabaseErrMsg(error_text, exc.toString(), out);
        return;

    } // end of recordset retrieval and processing

        
 }
 
 
 private void displayMemberTypeQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, int pGrandTotal_18, int pGrandTotal_9, Connection con, PrintWriter out) {
     
    // 
    // start by member type only
    //
    Statement stmt = null;
    ResultSet rs = null;

    int tmp_mem_types = 0;
    int totalMemberRounds = 0;
    int tot9rounds = 0;
    int tot18rounds = 0;
    int [] mem_type9 = new int [120];
    int [] mem_type18 = new int [120];
    int [] mem_type9Total = new int [20];
    int [] mem_type18Total = new int [20];
    
    String [] mem_type = new String [120];
    String tableName = (pReportType == 11) ? "teecurr2" : "teepast2";
    String sqlQuery;

    // build sql statement
    sqlQuery = "" + 
    "select ifnull(m_type, if(null_user=1,'Unknown','Removed from Member Database')) as m_type, sum(p_9), sum(p_18) from ( " +      
    "select p91 as p_9, IF(p91=0,1,0) as p_18, " +
        "m_type,  " +
        "if(ifnull(username1,'')='',1,0) as null_user " +
        "FROM " + tableName + " " +
        "left outer join member2b on username1 = username ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show1 = 1 AND (username1 <> '' and username1 is not null) " +
    "union all " +
    "select p92 as p_9, IF(p92=0,1,0) as p_18, " +
        "m_type, " +
        "if(ifnull(username2,'')='',1,0) as null_user " +
        "FROM " + tableName + " " +
        "left outer join member2b on username2 = username ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show2 = 1 AND (username2 <> '' and username2 is not null) " +
    "union all " +
    "select p93 as p_9, IF(p93=0,1,0) as p_18, " +
        "m_type,  " +
        "if(ifnull(username3,'')='',1,0) as null_user " +
        "FROM " + tableName + " " +
        "left outer join member2b on username3 = username ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show3 = 1 AND (username3 <> '' and username3 is not null) " +
    "union all " +
    "select p94 as p_9, IF(p94=0,1,0) as p_18, " +
        "m_type,  " +
        "if(ifnull(username4,'')='',1,0) as null_user " +
        "FROM " + tableName + " " +
        "left outer join member2b on username4 = username ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show4 = 1 AND (username4 <> '' and username4 is not null) " +
    "union all " +
    "select p95 as p_9, IF(p95=0,1,0) as p_18, " +
        "m_type,  " +
        "if(ifnull(username5,'')='',1,0) as null_user " +
        "FROM " + tableName + " " +
        "left outer join member2b on username5 = username ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show5 = 1 AND (username5 <> '' and username5 is not null) " +
    "union all " +
    "select (0) as p_9, (0) as p_18, " +
        "m2b.m_type as m_type, " +
        "(1) as null_user " +
        "from member2b m2b " +
        "where m2b.m_type is not null " +
        "group by m2b.m_type " +
    ") as d_table group by m_type WITH ROLLUP;";

    out.println("<!-- " + sqlQuery + " -->");

    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, pStartDate);
            pstmt1.setLong(2, pEndDate);
            pstmt1.setLong(3, pStartDate);
            pstmt1.setLong(4, pEndDate);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setLong(7, pStartDate);
            pstmt1.setLong(8, pEndDate);
            pstmt1.setLong(9, pStartDate);
            pstmt1.setLong(10, pEndDate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, pStartDate);
            pstmt1.setLong(3, pEndDate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, pStartDate);
            pstmt1.setLong(9, pEndDate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, pStartDate);
            pstmt1.setLong(12, pEndDate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, pStartDate);
            pstmt1.setLong(15, pEndDate);
        }
        rs = pstmt1.executeQuery();

        while ( rs.next() ) {

            if (rs.getString(1) == null) {

                tot9rounds = rs.getInt(2);
                tot18rounds = rs.getInt(3);
                totalMemberRounds = tot9rounds + tot18rounds;

            } else {

                mem_type[tmp_mem_types] = rs.getString(1);
                mem_type9Total[tmp_mem_types] = rs.getInt(2);
                mem_type18Total[tmp_mem_types] = rs.getInt(3);
                tmp_mem_types++;

            }
        }

        pstmt1.close();

        out.println("</tr><tr>");                     // Total Rounds for Members
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<p align=\"left\"><b><u>Rounds by Members:</u></b></p>");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><b>");
        out.println(buildDisplayValue(totalMemberRounds, 0, pGrandTotal_9 + pGrandTotal_18));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // member total 18
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><b>" + buildDisplayValue(tot18rounds, 0, totalMemberRounds) + "</b>");
        out.println("</font></td>");

        out.println("</tr><tr>");                          // member total 9
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><b>" + buildDisplayValue(tot9rounds, 0, totalMemberRounds) + "</b>");
        out.println("</font></td>");

        out.println("</tr><tr>");                          // blank row for divider
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">&nbsp;");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">&nbsp;");
        out.println("</font></td>");


        int tmp_index = 0;
        while (tmp_index < tmp_mem_types) {

            out.println("<tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">"); // show member type name
            out.println("<b>" + mem_type[tmp_index] + ":&nbsp;&nbsp;</b>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">"); // show total rounds for this member type
            out.println(buildDisplayValue(mem_type9Total[tmp_index], mem_type18Total[tmp_index], totalMemberRounds));
            out.println("</font></td>");

            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">18 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">"); 
            out.println(buildDisplayValue(mem_type18Total[tmp_index], 0, totalMemberRounds));
            out.println("</font></td>");

            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">9 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println(buildDisplayValue(mem_type9Total[tmp_index], 0, totalMemberRounds));
            out.println("</font></td>");
            
            tmp_index++;

        } // end while loop of processed data
    } 
    catch(Exception exc) {

        displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        return;
            
    }

    // end of by member type
            
 }
 
 
 private void displayMemberShipTypeQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, int pGrandTotal_18, int pGrandTotal_9, Connection con, PrintWriter out) {

    ResultSet rs = null;
    String tableName = (pReportType == 11) ? "teecurr2" : "teepast2";
    String sqlQuery;

    int tot9rounds = 0;
    int tot18rounds = 0;

    int [] memship_type9 = new int [20];
    int [] memship_type18 = new int [20];
    int [] mem_type9 = new int [120];
    int [] mem_type18 = new int [120];
    String [] memship_type = new String [120];
    String [] mem_type = new String [120];

    int totalMemberRounds = 0;
    int rs_rows = 0;
    int rs_rows1 = 0;
        
    // build sql statement
    sqlQuery = "" + 
    "select ifnull(m_ship,if(null_user=1,'Guest','Removed')) as m_ship, ifnull(m_type, if(null_user=1,'Unknown','Removed from Member Database')) as m_type, sum(p_9), sum(p_18) from ( " + 
    "select p91 as p_9, IF(p91=0,1,0) as p_18, " + 
            "m_type, m_ship, " + 
            "if(ifnull(username1,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username1 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show1 = 1 AND (username1 <> '' and username1 is not null) " + 
    "union all " + 
    "select p92 as p_9, IF(p92=0,1,0) as p_18, " + 
            "m_type, m_ship, " + 
            "if(ifnull(username2,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username2 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show2 = 1 AND (username2 <> '' and username2 is not null) " + 
    "union all " + 
    "select p93 as p_9, IF(p93=0,1,0) as p_18, " + 
            "m_type, m_ship, " + 
            "if(ifnull(username3,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username3 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show3 = 1 AND (username3 <> '' and username3 is not null) " + 
    "union all " + 
    "select p94 as p_9, IF(p94=0,1,0) as p_18, " + 
            "m_type, m_ship, " + 
            "if(ifnull(username4,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username4 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show4 = 1 AND (username4 <> '' and username4 is not null) " + 
    "union all " + 
    "select p95 as p_9, IF(p95=0,1,0) as p_18, " + 
            "m_type, m_ship, " + 
            "if(ifnull(username5,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username5 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show5 = 1 AND (username5 <> '' and username5 is not null) " + 
    "union all " + 
    "select (0) as p_9, (0) as p_18, " + 
            "m2b.m_type as m_type, " + 
            "m5.mship as m_ship, " + 
            "(1) as null_user " + 
            "from member2b m2b, mship5 m5 " + 
            "where m2b.m_type is not null " + 
            "group by m5.mship, m2b.m_type " + 
    ") as d_table group by m_ship, m_type WITH ROLLUP;";

    out.println("<!-- " + sqlQuery + " -->");
    
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, pStartDate);
            pstmt1.setLong(2, pEndDate);
            pstmt1.setLong(3, pStartDate);
            pstmt1.setLong(4, pEndDate);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setLong(7, pStartDate);
            pstmt1.setLong(8, pEndDate);
            pstmt1.setLong(9, pStartDate);
            pstmt1.setLong(10, pEndDate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, pStartDate);
            pstmt1.setLong(3, pEndDate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, pStartDate);
            pstmt1.setLong(9, pEndDate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, pStartDate);
            pstmt1.setLong(12, pEndDate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, pStartDate);
            pstmt1.setLong(15, pEndDate);
        }
        rs = pstmt1.executeQuery();
        
        while ( rs.next() ) {

            if (rs.getString(1) == null && rs.getString(2) == null) {

                tot9rounds = rs.getInt(3);
                tot18rounds = rs.getInt(4);
                totalMemberRounds = tot9rounds + tot18rounds;

            } else {

                if (rs.getString(1) != null && rs.getString(2) == null) {
                    
                    // membership type totals
                    memship_type9[rs_rows] = rs.getInt(3);
                    memship_type18[rs_rows] = rs.getInt(4);

                    rs_rows++;
                    
                } else {

                    // member totals
                    memship_type[rs_rows1] = rs.getString(1);
                    mem_type[rs_rows1] = rs.getString(2);
                    mem_type9[rs_rows1] = rs.getInt(3);
                    mem_type18[rs_rows1] = rs.getInt(4);

                    rs_rows1++;

                }

            }

        }

        pstmt1.close();
        
    } catch (Exception exc) {
            displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
    }
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"2\">&nbsp;</td>");

    out.println("</tr><tr>");                     // Rounds by Member Type
    out.println("<td align=\"left\">");
    out.println("<font size=\"2\">");
    out.println("<u><b>Rounds By Membership / Member Type</b></u>");
    out.println("</font></td><td></td></tr>");

    int tmp_index = 0;
    int tmp_index1 = 0;
    String tmp_memship_type = "";
    
    while (tmp_index < rs_rows1) {

        if (!memship_type[tmp_index].equals(tmp_memship_type)) {
            
            tmp_memship_type = memship_type[tmp_index];
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td></tr>");
            
            out.println("<tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("<b><u>Membership Type " + memship_type[tmp_index] + " Total:</u></b>");
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>");
            out.println(buildDisplayValue(memship_type9[tmp_index1] + memship_type18[tmp_index1], 0, totalMemberRounds));
            out.println("</b></font></td>");
            
            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">18 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println(buildDisplayValue(memship_type18[tmp_index1], 0, totalMemberRounds));
            out.println("</font></td>");
            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">9 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println(buildDisplayValue(memship_type9[tmp_index1], 0, totalMemberRounds));
            out.println("</font></td>");
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"2\">&nbsp;</td>");
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td align=\"right\"><font size=\"2\"><b>" + memship_type[tmp_index] + " Breakdown</b></font></td><td></td></tr>");
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td></tr>");
    
            tmp_index1++;
        
        }
        
        out.println("<tr>");
        out.println("<td align=\"right\" nowrap>");
        out.println("<font size=\"2\">");
        //out.println("<b>" + memship_type[tmp_index] + " &nbsp;/&nbsp; " + mem_type[tmp_index] + ":</b>");
        out.println("<b>" + mem_type[tmp_index] + ":</b>");
        out.println("</font></td>");

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println(buildDisplayValue(mem_type9[tmp_index] + mem_type18[tmp_index], 0, totalMemberRounds));
        out.println("</font></td>");

        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(mem_type18[tmp_index], 0, totalMemberRounds));
        out.println("</font></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(mem_type9[tmp_index], 0, totalMemberRounds));
        out.println("</font></td>");

        tmp_index++;

    } // end while loop of processed data

    
    // end of by membership/member type
    
 }
 
 
 private void displayGuestTypeQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, int pGrandTotal_18, int pGrandTotal_9, Connection con, PrintWriter out) {
     
    String tableName = "teepast2";
    if (pReportType == 11) tableName = "teecurr2";
    String sqlQuery;
    ResultSet rs1 = null;
    int rs_rows1 = 0;
    int tot9rounds = 0;
    int tot18rounds = 0;
    int totalGuestRounds = 0;
    int tmp_index = 0;
    int [] gst_type9 = new int [120];
    int [] gst_type18 = new int [120];
    String [] gst_type = new String [120];
    

    // build sql statement
    sqlQuery = "" + 
    "SELECT ifnull(m_ship,if(null_user=1,'Guest','Removed')) as m_ship, ifnull(m_type, ifnull(guest_type,if(null_user=1,'Unknown','Removed from Member Database'))) as m_or_g_type, sum(p_9), sum(p_18) FROM ( " + 
    "SELECT p1cw as cw, " + 
            "p91 as p_9, IF(p91=0,1,0) as p_18, " + 
            "m_type, m_ship, guest as guest_type, " + 
            "if(ifnull(username1,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username1 = username " + 
            "left outer join guest5 on guest = left(player1,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show1 = 1 AND ((username1 = '' or username1 is null) AND (player1 <> '' and player1 is not null)) " + 
    "union all " + 
    "SELECT p2cw as cw, " + 
            "p92 as p_9, IF(p92=0,1,0) as p_18, " + 
            "m_type, m_ship, guest as guest_type, " + 
            "if(ifnull(username2,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username2 = username " + 
            "left outer join guest5 on guest = left(player2,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show2 = 1 AND ((username2 = '' or username2 is null) AND (player2 <> '' and player2 is not null)) " + 
    "union all " + 
    "SELECT p3cw as cw, " + 
            "p93 as p_9, IF(p93=0,1,0) as p_18, " + 
            "m_type, m_ship, guest as guest_type, " + 
            "if(ifnull(username3,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username3 = username " + 
            "left outer join guest5 on guest = left(player3,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show3 = 1 AND ((username3 = '' or username3 is null) AND (player3 <> '' and player3 is not null)) " + 
    "union all " + 
    "SELECT p4cw as cw, " + 
            "p94 as p_9, IF(p94=0,1,0) as p_18, " + 
            "m_type, m_ship, guest as guest_type, " + 
            "if(ifnull(username4,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username4 = username " + 
            "left outer join guest5 on guest = left(player4,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show4 = 1 AND ((username4 = '' or username4 is null) AND (player4 <> '' and player4 is not null)) " + 
    "union all " + 
    "SELECT p5cw as cw, " + 
            "p95 as p_9, IF(p95=0,1,0) as p_18, " + 
            "m_type, m_ship, guest as guest_type, " + 
            "if(ifnull(username5,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username5 = username " + 
            "left outer join guest5 on guest = left(player5,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show5 = 1 AND ((username5 = '' or username5 is null) AND (player5 <> '' and player5 is not null)) " + 
    "union all " + 
    "SELECT null as cw, (0) as p_9, (0) as p_18, " + 
            "null as m_type, null as m_ship, guest as guest_type, " + 
            "(1) as null_user " + 
            "FROM guest5 " + 
    ") AS d_table group by m_ship, m_or_g_type WITH ROLLUP;";

    out.println("<!-- " + sqlQuery + " -->");
    
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, pStartDate);
            pstmt1.setLong(2, pEndDate);
            pstmt1.setLong(3, pStartDate);
            pstmt1.setLong(4, pEndDate);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setLong(7, pStartDate);
            pstmt1.setLong(8, pEndDate);
            pstmt1.setLong(9, pStartDate);
            pstmt1.setLong(10, pEndDate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, pStartDate);
            pstmt1.setLong(3, pEndDate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, pStartDate);
            pstmt1.setLong(9, pEndDate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, pStartDate);
            pstmt1.setLong(12, pEndDate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, pStartDate);
            pstmt1.setLong(15, pEndDate);
        }
        
        rs1 = pstmt1.executeQuery();

        while ( rs1.next() ) {

            if (rs1.getString(1) == null && rs1.getString(2) == null) {

                tot9rounds = rs1.getInt(3);
                tot18rounds = rs1.getInt(4);
                totalGuestRounds = tot9rounds + tot18rounds;

            } else {

                if (rs1.getString(1) != null && rs1.getString(2) == null) {

                    // do nothing since this value will be the same as
                    // above (both field 1&2 are null)

                } else {

                    gst_type[rs_rows1] = rs1.getString(2);
                    gst_type9[rs_rows1] = rs1.getInt(3);
                    gst_type18[rs_rows1] = rs1.getInt(4);

                    rs_rows1++;

                }

            }

        }

        pstmt1.close();
    
    } catch (Exception exc) {
        displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
    }
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"2\">&nbsp;</td>");

    out.println("</tr><tr>");                     // Total Rounds for Guests
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Guests:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGuestRounds, 0, pGrandTotal_9 + pGrandTotal_18));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // guest total 18
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18rounds, 0, totalGuestRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // guest total 9
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9rounds, 0, totalGuestRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"2\">&nbsp;</td>");

    tmp_index = 0;
    int gst9hole = 0;
    int gst18hole = 0;
    String guestType = "";

    while (tmp_index < rs_rows1) {
    
        out.println("<tr>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<p align=\"right\"><b>" + gst_type[tmp_index] + ":</b></p>");
        out.println("</font></td>");
        
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println(buildDisplayValue(gst_type9[tmp_index] + gst_type18[tmp_index], 0, totalGuestRounds));
        out.println("</font></td>");
        
        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(gst_type18[tmp_index], 0, totalGuestRounds));
        out.println("</font></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(gst_type9[tmp_index], 0, totalGuestRounds));
        out.println("</font></td>");
        
        tmp_index++;
    
    } // end while loop of processed data

    // end of by guest type
            
 }
 
 
 private void displayCourseSummary(String pCourseName, long pStartDate, long pEndDate, int pReportType, Connection con, PrintWriter out){
    
    String tableName = "teepast2";
    if (pReportType == 0) tableName = "teecurr2";
    String sqlQuery;
    ResultSet rs1 = null;
    int tmp_index = 0;

    int tot9memrounds = 0;
    int tot18memrounds = 0;
    int totns9memrounds = 0;
    int totns18memrounds = 0;
    int totalMemRounds = 0; // total member rounds
    int totalMemCRounds = 0; // total member combinded rounds

    int tot9gstrounds = 0;
    int tot18gstrounds = 0;
    int totns9gstrounds = 0;
    int totns18gstrounds = 0;
    int totalGstRounds = 0; // total guest rounds
    int totalGstCRounds = 0; // total guest combinded rounds

    int totalRounds = 0;
    int total9Rounds = 0;
    int total18Rounds = 0;
    int totalCRounds = 0;

    int totalNs9rounds = 0;
    int totalNs18rounds = 0;
    int totalNsRounds = 0;
    
    // build sql statement
    sqlQuery = "" + 
    "SELECT if(ifnull(m_ship,if(null_user=1,'Guest','Member'))='Guest','Rounds By Guests','Rounds By Members') as m_ship, " +
            "sum(p_9), sum(p_18), sum(p_9ns), sum(p_18ns) FROM ( " + 
    "SELECT p1cw as cw, " + 
            "p91*(show1<>1) as p_9ns, IF(p91=0,1,0)*(show1<>1) as p_18ns, " + 
            "p91*(show1=1) as p_9, IF(p91=0,1,0)*(show1=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username1,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username1 = username " + 
            "left outer join guest5 on guest = left(player1,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND ((username1 <> '' AND username1 is not null) AND (player1 <> '' AND player1 <> 'x' AND player1 is not null)) " + 
    "union all " + 
    "SELECT p2cw as cw, " + 
            "p92*(show2<>1) as p_9ns, IF(p92=0,1,0)*(show2<>1) as p_18ns, " + 
            "p92*(show2=1) as p_9, IF(p92=0,1,0)*(show2=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username2,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username2 = username " + 
            "left outer join guest5 on guest = left(player2,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND ((username2 <> '' AND username2 is not null) AND (player2 <> '' AND player2 <> 'x' AND player2 is not null)) " + 
    "union all " + 
    "SELECT p3cw as cw, " + 
            "p93*(show3<>1) as p_9ns, IF(p93=0,1,0)*(show3<>1) as p_18ns, " + 
            "p93*(show3=1) as p_9, IF(p93=0,1,0)*(show3=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username3,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username3 = username " + 
            "left outer join guest5 on guest = left(player3,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND ((username3 <> '' AND username3 is not null) AND (player3 <> '' AND player3 <> 'x' AND player3 is not null)) " + 
    "union all " + 
    "SELECT p4cw as cw, " + 
            "p94*(show4<>1) as p_9ns, IF(p94=0,1,0)*(show4<>1) as p_18ns, " + 
            "p94*(show4=1) as p_9, IF(p94=0,1,0)*(show4=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username4,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username4 = username " + 
            "left outer join guest5 on guest = left(player4,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND ((username4 <> '' AND username4 is not null) AND (player4 <> '' AND player4 <> 'x' AND player4 is not null)) " + 
    "union all " + 
    "SELECT p5cw as cw, " + 
            "p95*(show5<>1) as p_9ns, IF(p95=0,1,0)*(show5<>1) as p_18ns, " + 
            "p95*(show5=1) as p_9, IF(p95=0,1,0)*(show5=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username5,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username5 = username " + 
            "left outer join guest5 on guest = left(player5,length(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND ((username5 <> '' AND username5 is not null) AND (player5 <> '' AND player5 <> 'x' AND player5 is not null)) " + 
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";

    out.println("<!-- " + sqlQuery + " -->");
    
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, pStartDate);
            pstmt1.setLong(2, pEndDate);
            pstmt1.setLong(3, pStartDate);
            pstmt1.setLong(4, pEndDate);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setLong(7, pStartDate);
            pstmt1.setLong(8, pEndDate);
            pstmt1.setLong(9, pStartDate);
            pstmt1.setLong(10, pEndDate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, pStartDate);
            pstmt1.setLong(3, pEndDate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, pStartDate);
            pstmt1.setLong(6, pEndDate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, pStartDate);
            pstmt1.setLong(9, pEndDate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, pStartDate);
            pstmt1.setLong(12, pEndDate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, pStartDate);
            pstmt1.setLong(15, pEndDate);
        }
        
        rs1 = pstmt1.executeQuery();

        rs1.next();

        tot9memrounds = rs1.getInt(2);
        tot18memrounds = rs1.getInt(3);
        totns9memrounds = rs1.getInt(4);
        totns18memrounds = rs1.getInt(5);
        totalMemRounds = tot9memrounds + tot18memrounds; // total member rounds
        totalMemCRounds = tot18memrounds + (tot9memrounds / 2); // total member combinded rounds
        
        rs1.next();
        
        tot9gstrounds = rs1.getInt(2);
        tot18gstrounds = rs1.getInt(3);
        totns9gstrounds = rs1.getInt(4);
        totns18gstrounds = rs1.getInt(5);
        totalGstRounds = tot9gstrounds + tot18gstrounds; // total guest rounds
        totalGstCRounds = tot18gstrounds + (tot9gstrounds / 2); // total guest combinded rounds
        
        totalRounds = totalMemRounds + totalGstRounds;
        total9Rounds = tot9memrounds + tot9gstrounds;
        total18Rounds = tot18memrounds + tot18gstrounds;
        totalCRounds = total18Rounds + (total9Rounds / 2);
        
        totalNs9rounds = totns9memrounds + totns9gstrounds;
        totalNs18rounds = totns18memrounds + totns18gstrounds;
        totalNsRounds = totalNs9rounds + totalNs18rounds;
        
        pstmt1.close();
    
    } catch (Exception exc) {
        displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
    }
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");

    //
    // TOTAL ROUNDS
    //
    out.println("</tr><tr>");                     // Total Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Total Rounds Played:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalRounds, 0, 0));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds, 0, totalRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds, 0, totalRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");

    
    // 
    // MEMBER ROUNDS
    //
    out.println("</tr><tr>");                     // Total Member Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Members:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemRounds, 0, 0));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds, 0, totalMemRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds, 0, totalMemRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");
    
    
    // 
    // GUEST ROUNDS
    //
    out.println("</tr><tr>");                     // Total Guest Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Guest:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds, 0, 0));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds, 0, totalGstRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds, 0, totalGstRounds));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");


    //
    // NO SHOWS
    //
    out.println("</tr><tr>");                     // Total No Shows
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Number of Member No-Shows:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalNsRounds, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");
            
 }
 
 
 private void displayCourseSummaryBig(String pCourseName, Connection con, PrintWriter out){
    
    String tableName = "teepast2";
    String sqlQuery;
    ResultSet rs1 = null;
    int tmp_index = 0;

    int tot9memrounds1 = 0;
    int tot18memrounds1 = 0;
    int totns9memrounds1 = 0;
    int totns18memrounds1 = 0;
    int totalMemRounds1 = 0; // total member rounds
    int totalMemCRounds1 = 0; // total member combinded rounds

    int tot9gstrounds1 = 0;
    int tot18gstrounds1 = 0;
    int totns9gstrounds1 = 0;
    int totns18gstrounds1 = 0;
    int totalGstRounds1 = 0; // total guest rounds
    int totalGstCRounds1 = 0; // total guest combinded rounds

    int totalRounds1 = 0;
    int total9Rounds1 = 0;
    int total18Rounds1 = 0;
    int totalCRounds1 = 0;

    int totalNs9rounds1 = 0;
    int totalNs18rounds1 = 0;
    int totalNsRounds1 = 0;
    
    
    int tot9memrounds2 = 0;
    int tot18memrounds2 = 0;
    int totns9memrounds2 = 0;
    int totns18memrounds2 = 0;
    int totalMemRounds2 = 0; // total member rounds
    int totalMemCRounds2 = 0; // total member combinded rounds

    int tot9gstrounds2 = 0;
    int tot18gstrounds2 = 0;
    int totns9gstrounds2 = 0;
    int totns18gstrounds2 = 0;
    int totalGstRounds2 = 0; // total guest rounds
    int totalGstCRounds2 = 0; // total guest combinded rounds

    int totalRounds2 = 0;
    int total9Rounds2 = 0;
    int total18Rounds2 = 0;
    int totalCRounds2 = 0;

    int totalNs9rounds2 = 0;
    int totalNs18rounds2 = 0;
    int totalNsRounds2 = 0;
    
    
    int tot9memrounds3 = 0;
    int tot18memrounds3 = 0;
    int totns9memrounds3 = 0;
    int totns18memrounds3 = 0;
    int totalMemRounds3 = 0; // total member rounds
    int totalMemCRounds3 = 0; // total member combinded rounds

    int tot9gstrounds3 = 0;
    int tot18gstrounds3 = 0;
    int totns9gstrounds3 = 0;
    int totns18gstrounds3 = 0;
    int totalGstRounds3 = 0; // total guest rounds
    int totalGstCRounds3 = 0; // total guest combinded rounds

    int totalRounds3 = 0;
    int total9Rounds3 = 0;
    int total18Rounds3 = 0;
    int totalCRounds3 = 0;

    int totalNs9rounds3 = 0;
    int totalNs18rounds3 = 0;
    int totalNsRounds3 = 0;
    
    long sdate = 0; 
    long edate = 0;                             // today's date
    long mtddate = 0;                           // MTD start date
    long ytddate = 0;                           // YTD start date
    long lmsdate = 0;                           // Last Month start date
    long lmedate = 0;                           // Last Month end date
    int year = 0;
    int month = 0;
    int day = 0;
    
    //
    //  Get today's date and current time and calculate date & time values 
    //
    Calendar cal = new GregorianCalendar();       // get todays date

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    int cal_am_pm = cal.get(Calendar.AM_PM);        // current time
    int cal_hour = cal.get(Calendar.HOUR);
    int cal_min = cal.get(Calendar.MINUTE);

    int curr_time = cal_hour;
    if (cal_am_pm == 1) {                       // if PM

      curr_time = curr_time + 12;              // convert to military time
    }

    curr_time = curr_time * 100;                // create current time value for compare
    curr_time = curr_time + cal_min;

    month = month + 1;                           // month starts at zero

    //mtd_start_date = year + "-" + month + "-01";
    //mtd_end_date = year + "-" + month + "-" + day;
    //ytd_start_date = year + "-01-01";
    //ytd_end_date = year + "-" + month + "-" + day;

    edate = year * 10000;                        // create a edate field of yyyymmdd (for today)
    edate = edate + (month * 100);
    edate = edate + day;                         // date = yyyymmdd (for comparisons)

    mtddate = year * 10000;                      // create a MTD date
    mtddate = mtddate + (month * 100);
    mtddate = mtddate + 01;

    ytddate = year * 10000;                      // create a YTD date
    ytddate = ytddate + 100;
    ytddate = ytddate + 01;

    month = month - 1;                           // last month

    if (month == 0) {
        month = 12;
        year = year - 1;
    }

    lmsdate = year * 10000;                      // create a Last Month Start date
    lmsdate = lmsdate + (month * 100);
    lmsdate = lmsdate + 01;

    lmedate = lmsdate + 30;                      // create a Last Month End date

    //lm_start_date = year + "-" + month + "-01";
    //lm_end_date = year + "-" + month + "-30";   
    
    
    
    // build sql statement for LAST MONTH
    sqlQuery = "" + 
    "SELECT if(ifnull(m_ship,if(null_user=1,'Guest','Member'))='Guest','Rounds By Guests','Rounds By Members') as m_ship, " +
            "sum(p_9), sum(p_18), sum(p_9ns), sum(p_18ns) FROM ( " + 
    "SELECT p1cw as cw, " + 
            "p91*(show1<>1) as p_9ns, IF(p91=0,1,0)*(show1<>1) as p_18ns, " + 
            "p91*(show1=1) as p_9, IF(p91=0,1,0)*(show1=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username1,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username1 = username " + 
            "left outer join guest5 on guest = left(player1,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username1 <> '' AND username1 is not null) AND (player1 <> '' AND player1 <> 'x' AND player1 is not null)) " + 
    "union all " + 
    "SELECT p2cw as cw, " + 
            "p92*(show2<>1) as p_9ns, IF(p92=0,1,0)*(show2<>1) as p_18ns, " + 
            "p92*(show2=1) as p_9, IF(p92=0,1,0)*(show2=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username2,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username2 = username " + 
            "left outer join guest5 on guest = left(player2,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username2 <> '' AND username2 is not null) AND (player2 <> '' AND player2 <> 'x' AND player2 is not null)) " + 
    "union all " + 
    "SELECT p3cw as cw, " + 
            "p93*(show3<>1) as p_9ns, IF(p93=0,1,0)*(show3<>1) as p_18ns, " + 
            "p93*(show3=1) as p_9, IF(p93=0,1,0)*(show3=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username3,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username3 = username " + 
            "left outer join guest5 on guest = left(player3,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username3 <> '' AND username3 is not null) AND (player3 <> '' AND player3 <> 'x' AND player3 is not null)) " + 
    "union all " + 
    "SELECT p4cw as cw, " + 
            "p94*(show4<>1) as p_9ns, IF(p94=0,1,0)*(show4<>1) as p_18ns, " + 
            "p94*(show4=1) as p_9, IF(p94=0,1,0)*(show4=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username4,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username4 = username " + 
            "left outer join guest5 on guest = left(player4,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username4 <> '' AND username4 is not null) AND (player4 <> '' AND player4 <> 'x' AND player4 is not null)) " + 
    "union all " + 
    "SELECT p5cw as cw, " + 
            "p95*(show5<>1) as p_9ns, IF(p95=0,1,0)*(show5<>1) as p_18ns, " + 
            "p95*(show5=1) as p_9, IF(p95=0,1,0)*(show5=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username5,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username5 = username " + 
            "left outer join guest5 on guest = left(player5,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username5 <> '' AND username5 is not null) AND (player5 <> '' AND player5 <> 'x' AND player5 is not null)) " + 
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";

    out.println("<!-- " + sqlQuery + " -->");
    
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, lmsdate);
            pstmt1.setLong(2, lmedate);
            pstmt1.setLong(3, lmsdate);
            pstmt1.setLong(4, lmedate);
            pstmt1.setLong(5, lmsdate);
            pstmt1.setLong(6, lmedate);
            pstmt1.setLong(7, lmsdate);
            pstmt1.setLong(8, lmedate);
            pstmt1.setLong(9, lmsdate);
            pstmt1.setLong(10, lmedate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, lmsdate);
            pstmt1.setLong(3, lmedate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, lmsdate);
            pstmt1.setLong(6, lmedate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, lmsdate);
            pstmt1.setLong(9, lmedate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, lmsdate);
            pstmt1.setLong(12, lmedate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, lmsdate);
            pstmt1.setLong(15, lmedate);
        }
        
        rs1 = pstmt1.executeQuery();

        rs1.next();

        tot9memrounds1 = rs1.getInt(2);
        tot18memrounds1 = rs1.getInt(3);
        totns9memrounds1 = rs1.getInt(4);
        totns18memrounds1 = rs1.getInt(5);
        totalMemRounds1 = tot9memrounds1 + tot18memrounds1; // total member rounds
        totalMemCRounds1 = tot18memrounds1 + (tot9memrounds1 / 2); // total member combinded rounds
        
        rs1.next();
        
        tot9gstrounds1 = rs1.getInt(2);
        tot18gstrounds1 = rs1.getInt(3);
        totns9gstrounds1 = rs1.getInt(4);
        totns18gstrounds1 = rs1.getInt(5);
        totalGstRounds1 = tot9gstrounds1 + tot18gstrounds1; // total guest rounds
        totalGstCRounds1 = tot18gstrounds1 + (tot9gstrounds1 / 2); // total guest combinded rounds
        
        totalRounds1 = totalMemRounds1 + totalGstRounds1;
        total9Rounds1 = tot9memrounds1 + tot9gstrounds1;
        total18Rounds1 = tot18memrounds1 + tot18gstrounds1;
        totalCRounds1 = total18Rounds1 + (total9Rounds1 / 2);
        
        totalNs9rounds1 = totns9memrounds1 + totns9gstrounds1;
        totalNs18rounds1 = totns18memrounds1 + totns18gstrounds1;
        totalNsRounds1 = totalNs9rounds1 + totalNs18rounds1;
        
        pstmt1.close();
    
    } catch (Exception exc) {
        displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
    }
    
    // END OF LAST MONTH
    
    
    
    // build sql statement for MONTH TO DATE (mtddate & edate)
    sqlQuery = "" + 
    "SELECT if(ifnull(m_ship,if(null_user=1,'Guest','Member'))='Guest','Rounds By Guests','Rounds By Members') as m_ship, " +
            "sum(p_9), sum(p_18), sum(p_9ns), sum(p_18ns) FROM ( " + 
    "SELECT p1cw as cw, " + 
            "p91*(show1<>1) as p_9ns, IF(p91=0,1,0)*(show1<>1) as p_18ns, " + 
            "p91*(show1=1) as p_9, IF(p91=0,1,0)*(show1=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username1,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username1 = username " + 
            "left outer join guest5 on guest = left(player1,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username1 <> '' AND username1 is not null) AND (player1 <> '' AND player1 <> 'x' AND player1 is not null)) " + 
    "union all " + 
    "SELECT p2cw as cw, " + 
            "p92*(show2<>1) as p_9ns, IF(p92=0,1,0)*(show2<>1) as p_18ns, " + 
            "p92*(show2=1) as p_9, IF(p92=0,1,0)*(show2=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username2,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username2 = username " + 
            "left outer join guest5 on guest = left(player2,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username2 <> '' AND username2 is not null) AND (player2 <> '' AND player2 <> 'x' AND player2 is not null)) " + 
    "union all " + 
    "SELECT p3cw as cw, " + 
            "p93*(show3<>1) as p_9ns, IF(p93=0,1,0)*(show3<>1) as p_18ns, " + 
            "p93*(show3=1) as p_9, IF(p93=0,1,0)*(show3=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username3,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username3 = username " + 
            "left outer join guest5 on guest = left(player3,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username3 <> '' AND username3 is not null) AND (player3 <> '' AND player3 <> 'x' AND player3 is not null)) " + 
    "union all " + 
    "SELECT p4cw as cw, " + 
            "p94*(show4<>1) as p_9ns, IF(p94=0,1,0)*(show4<>1) as p_18ns, " + 
            "p94*(show4=1) as p_9, IF(p94=0,1,0)*(show4=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username4,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username4 = username " + 
            "left outer join guest5 on guest = left(player4,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username4 <> '' AND username4 is not null) AND (player4 <> '' AND player4 <> 'x' AND player4 is not null)) " + 
    "union all " + 
    "SELECT p5cw as cw, " + 
            "p95*(show5<>1) as p_9ns, IF(p95=0,1,0)*(show5<>1) as p_18ns, " + 
            "p95*(show5=1) as p_9, IF(p95=0,1,0)*(show5=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username5,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username5 = username " + 
            "left outer join guest5 on guest = left(player5,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username5 <> '' AND username5 is not null) AND (player5 <> '' AND player5 <> 'x' AND player5 is not null)) " + 
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";

    out.println("<!-- " + sqlQuery + " -->");
    
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, mtddate);
            pstmt1.setLong(2, edate);
            pstmt1.setLong(3, mtddate);
            pstmt1.setLong(4, edate);
            pstmt1.setLong(5, mtddate);
            pstmt1.setLong(6, edate);
            pstmt1.setLong(7, mtddate);
            pstmt1.setLong(8, edate);
            pstmt1.setLong(9, mtddate);
            pstmt1.setLong(10, edate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, mtddate);
            pstmt1.setLong(3, edate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, mtddate);
            pstmt1.setLong(6, edate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, mtddate);
            pstmt1.setLong(9, edate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, mtddate);
            pstmt1.setLong(12, edate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, mtddate);
            pstmt1.setLong(15, edate);
        }
        
        rs1 = pstmt1.executeQuery();

        rs1.next();

        tot9memrounds2 = rs1.getInt(2);
        tot18memrounds2 = rs1.getInt(3);
        totns9memrounds2 = rs1.getInt(4);
        totns18memrounds2 = rs1.getInt(5);
        totalMemRounds2 = tot9memrounds2 + tot18memrounds2; // total member rounds
        totalMemCRounds2 = tot18memrounds2 + (tot9memrounds2 / 2); // total member combinded rounds
        
        rs1.next();
        
        tot9gstrounds2 = rs1.getInt(2);
        tot18gstrounds2 = rs1.getInt(3);
        totns9gstrounds2 = rs1.getInt(4);
        totns18gstrounds2 = rs1.getInt(5);
        totalGstRounds2 = tot9gstrounds2 + tot18gstrounds2; // total guest rounds
        totalGstCRounds2 = tot18gstrounds2 + (tot9gstrounds2 / 2); // total guest combinded rounds
        
        totalRounds2 = totalMemRounds2 + totalGstRounds2;
        total9Rounds2 = tot9memrounds2 + tot9gstrounds2;
        total18Rounds2 = tot18memrounds2 + tot18gstrounds2;
        totalCRounds2 = total18Rounds2 + (total9Rounds2 / 2);
        
        totalNs9rounds2 = totns9memrounds2 + totns9gstrounds2;
        totalNs18rounds2 = totns18memrounds2 + totns18gstrounds2;
        totalNsRounds2 = totalNs9rounds2 + totalNs18rounds2;
        
        pstmt1.close();
    
    } catch (Exception exc) {
        displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
    }
    
    // END OF MONTH TO DATE
    
    
    
    
    
    // build sql statement for YEAR TO DATE (ytddate & edate)
    sqlQuery = "" + 
    "SELECT if(ifnull(m_ship,if(null_user=1,'Guest','Member'))='Guest','Rounds By Guests','Rounds By Members') as m_ship, " +
            "sum(p_9), sum(p_18), sum(p_9ns), sum(p_18ns) FROM ( " + 
    "SELECT p1cw as cw, " + 
            "p91*(show1<>1) as p_9ns, IF(p91=0,1,0)*(show1<>1) as p_18ns, " + 
            "p91*(show1=1) as p_9, IF(p91=0,1,0)*(show1=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username1,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username1 = username " + 
            "left outer join guest5 on guest = left(player1,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username1 <> '' AND username1 is not null) AND (player1 <> '' AND player1 <> 'x' AND player1 is not null)) " + 
    "union all " + 
    "SELECT p2cw as cw, " + 
            "p92*(show2<>1) as p_9ns, IF(p92=0,1,0)*(show2<>1) as p_18ns, " + 
            "p92*(show2=1) as p_9, IF(p92=0,1,0)*(show2=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username2,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username2 = username " + 
            "left outer join guest5 on guest = left(player2,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username2 <> '' AND username2 is not null) AND (player2 <> '' AND player2 <> 'x' AND player2 is not null)) " + 
    "union all " + 
    "SELECT p3cw as cw, " + 
            "p93*(show3<>1) as p_9ns, IF(p93=0,1,0)*(show3<>1) as p_18ns, " + 
            "p93*(show3=1) as p_9, IF(p93=0,1,0)*(show3=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username3,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username3 = username " + 
            "left outer join guest5 on guest = left(player3,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username3 <> '' AND username3 is not null) AND (player3 <> '' AND player3 <> 'x' AND player3 is not null)) " + 
    "union all " + 
    "SELECT p4cw as cw, " + 
            "p94*(show4<>1) as p_9ns, IF(p94=0,1,0)*(show4<>1) as p_18ns, " + 
            "p94*(show4=1) as p_9, IF(p94=0,1,0)*(show4=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username4,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username4 = username " + 
            "left outer join guest5 on guest = left(player4,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username4 <> '' AND username4 is not null) AND (player4 <> '' AND player4 <> 'x' AND player4 is not null)) " + 
    "union all " + 
    "SELECT p5cw as cw, " + 
            "p95*(show5<>1) as p_9ns, IF(p95=0,1,0)*(show5<>1) as p_18ns, " + 
            "p95*(show5=1) as p_9, IF(p95=0,1,0)*(show5=1) as p_18, " + 
            "m_ship, guest as guest_type, " + 
            "if(ifnull(username5,'')='',1,0) as null_user " + 
            "FROM " + tableName + " " +
            "left outer join member2b on username5 = username " + 
            "left outer join guest5 on guest = left(player5,length(guest)) ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND ((username5 <> '' AND username5 is not null) AND (player5 <> '' AND player5 <> 'x' AND player5 is not null)) " + 
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";

    out.println("<!-- " + sqlQuery + " -->");
    
    try {
        
        PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
        pstmt1.clearParameters();
        
        if (pCourseName.equalsIgnoreCase("-ALL-")) {
            pstmt1.setLong(1, ytddate);
            pstmt1.setLong(2, edate);
            pstmt1.setLong(3, ytddate);
            pstmt1.setLong(4, edate);
            pstmt1.setLong(5, ytddate);
            pstmt1.setLong(6, edate);
            pstmt1.setLong(7, ytddate);
            pstmt1.setLong(8, edate);
            pstmt1.setLong(9, ytddate);
            pstmt1.setLong(10, edate);
        } else {
            pstmt1.setString(1, pCourseName);
            pstmt1.setLong(2, ytddate);
            pstmt1.setLong(3, edate);
            pstmt1.setString(4, pCourseName);
            pstmt1.setLong(5, ytddate);
            pstmt1.setLong(6, edate);
            pstmt1.setString(7, pCourseName);
            pstmt1.setLong(8, ytddate);
            pstmt1.setLong(9, edate);
            pstmt1.setString(10, pCourseName);
            pstmt1.setLong(11, ytddate);
            pstmt1.setLong(12, edate);
            pstmt1.setString(13, pCourseName);
            pstmt1.setLong(14, ytddate);
            pstmt1.setLong(15, edate);
        }
        
        rs1 = pstmt1.executeQuery();

        rs1.next();

        tot9memrounds3 = rs1.getInt(2);
        tot18memrounds3 = rs1.getInt(3);
        totns9memrounds3 = rs1.getInt(4);
        totns18memrounds3 = rs1.getInt(5);
        totalMemRounds3 = tot9memrounds3 + tot18memrounds3; // total member rounds
        totalMemCRounds3 = tot18memrounds3 + (tot9memrounds3 / 2); // total member combinded rounds
        
        rs1.next();
        
        tot9gstrounds3 = rs1.getInt(2);
        tot18gstrounds3 = rs1.getInt(3);
        totns9gstrounds3 = rs1.getInt(4);
        totns18gstrounds3 = rs1.getInt(5);
        totalGstRounds3 = tot9gstrounds3 + tot18gstrounds3; // total guest rounds
        totalGstCRounds3 = tot18gstrounds3 + (tot9gstrounds3 / 2); // total guest combinded rounds
        
        totalRounds3 = totalMemRounds3 + totalGstRounds3;
        total9Rounds3 = tot9memrounds3 + tot9gstrounds3;
        total18Rounds3 = tot18memrounds3 + tot18gstrounds3;
        totalCRounds3 = total18Rounds3 + (total9Rounds3 / 2);
        
        totalNs9rounds3 = totns9memrounds3 + totns9gstrounds3;
        totalNs18rounds3 = totns18memrounds3 + totns18gstrounds3;
        totalNsRounds3 = totalNs9rounds3 + totalNs18rounds3;
        
        pstmt1.close();
    
    } catch (Exception exc) {
        displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
    }
    
    // END OF YEAR TO DATE
    
    
    
    
    
    //
    // TOTAL ROUNDS
    //
    out.println("</tr><tr>");                     // Total Rounds Played
    out.println("<td align=\"center\" nowrap>");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Total Rounds Played:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalRounds3, 0, 0));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds1, 0, totalRounds1));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds2, 0, totalRounds2));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds3, 0, totalRounds3));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds1, 0, totalRounds1));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds2, 0, totalRounds2));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds3, 0, totalRounds3));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds3, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"7\">&nbsp;</td>");

    
    // 
    // MEMBER ROUNDS
    //
    out.println("</tr><tr>");                     // Total Member Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Members:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemRounds3, 0, 0));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds1, 0, totalMemRounds1));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds2, 0, totalMemRounds2));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds3, 0, totalMemRounds3));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds1, 0, totalMemRounds1));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds2, 0, totalMemRounds2));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds3, 0, totalMemRounds3));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds3, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"7\">&nbsp;</td>");
    
    
    // 
    // GUEST ROUNDS
    //
    out.println("</tr><tr>");                     // Total Guest Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Guest:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds3, 0, 0));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds1, 0, totalGstRounds1));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds2, 0, totalGstRounds2));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds3, 0, totalGstRounds3));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds1, 0, totalGstRounds1));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds2, 0, totalGstRounds2));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds3, 0, totalGstRounds3));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds3, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"7\">&nbsp;</td>");


    //
    // NO SHOWS
    //
    out.println("</tr><tr>");                     // Total No Shows
    out.println("<td align=\"center\" nowrap>");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Number of Member No-Shows:</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalNsRounds1, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalNsRounds2, 0, 0));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalNsRounds3, 0, 0));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"7\">&nbsp;</td>");
            
 }
 
 
 //**************************************************
 // Common Method for Displaying Values 
 //**************************************************
 //
 private String buildDisplayValue(int subTotal_9, int subTotal_18, int grandTotal) {
    
    NumberFormat nf;
    nf = NumberFormat.getNumberInstance();
    
    if (subTotal_9 + subTotal_18 < 1 || grandTotal < 1) {
        return "" + nf.format(subTotal_9 + subTotal_18) + "</td><td>&nbsp;";
    } else {
        double tmp = ((subTotal_9 + subTotal_18) * 100) / grandTotal;
        return nf.format(subTotal_9 + subTotal_18) + "</td><td><font size=\"2\">(" + ((tmp < 1) ? "<1" : nf.format(tmp)) + "%)";
    }
    
 }
 
 
 //**************************************************
 // Common Method for Building WHERE Clause for SQL 
 //**************************************************
 //
 private String buildWhereClause(int pReportType, String pCourseName) {

    String courseClause = "courseName = ? AND";
    if (pCourseName.equals("-ALL-")) { courseClause = ""; }
    
    if (pReportType == 1 || pReportType == 11) {
        // today
        return "WHERE " + courseClause + " date = ? AND time <= ? ";
    } else {
        // date range
        return "WHERE " + courseClause + " date >= ? AND date <= ? ";
    }
     
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
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }
 
 
} // end servlet
