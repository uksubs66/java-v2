/***************************************************************************************
 *   Proshop_report_course_rounds: This servlet will ouput the course statistics report
 *
 *
 *   Called by:     called by main menu options and by its own outputed html
 *
 *
 *   Created:       03/23/2005 by Paul
 *
 *
 *   History:
 *
 *                   5/24/2010   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *                  08/11/2009 - Replaced object arrays with arraylist so it scales better. No longer using g_MAX_ARRAY_SIZE
 *                               updated: displayGuestTypeQuery, displayMemberTypeQuery, displayMembershipTypeQuery
 *                  08/11/2009 - Changed the text in the database error messages so we can more easily identify the 
 *                               method reporting the error.
 *                  04/28/2009 - Fixed no-show calculation (labeled as Member no shows, but was displaying total no shows)
 *                               Also added 9/18/combined breakdown to the member no shows
 *                  04/17/2009 - Adjust Congressional custom to allow for temp Hills Course during course renovation.
 *                  04/01/2009 - Updated report to use new teepast2 fields mtype#, mship#, gtype#, grev#
 *                               no more joining to guest5 & member2b for past tee times (still for today reports)
 *                               also added links to the old reports since they will no longer be available from the menus
 *                  07/18/2008 - Added limited access proshop users checks
 *                  05/27/2008 - Increase g_MAX_ARRAY_SIZE size from 160 to 200
 *                  01/04/2008 - Custom for Congressional reports (course labeling)
 *                  06/28/2007 - Fixed last month date fix
 *                  02/07/2007 - Added 9/18 breakdown to the tmode type
 *                  12/18/2006 - Changed member type and membership type arrays from 20 to 40 to allow
 *                               for 24 types plus space for old/unkown types.
 *                  12/11/2006 - Updated displayCourseSummary to handle query returns with no member rounds
 *                  03/02/2006 - Updated displayTransportationQuery method with new
 *                               SQL query and removed the arrays.  Requires MySQL >= 4.1.12
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
//import javax.naming.*;

// foretees imports
//import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Proshop_report_course_rounds extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;
    //int g_MAX_ARRAY_SIZE = 200;
 
 //****************************************************
 // Process the get method on this page as a post call
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                   // call doPost processing
    
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

    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
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

    String courseName = "";        // course names
    String error = "None";
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses
    
    

    parmClub parm = new parmClub(0, con); // golf only report
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

        if (curr_time < 1200) {           // if AM, then we rolled ahead 1 day

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

    String start_date = year + "-" + month + "-" + day;
    String end_date = year + "-" + month + "-" + day;
    
    //
    // Check for multiple courses
    //
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club
       
       try {

          course = Utilities.getCourseNames(con);     // get all the course names
       
          count = course.size();                      // number of courses

       }
       catch (Exception exc) {
          displayDatabaseErrMsg("Error loading course names.", "", out);
          return;
       }
    }

    // start report output
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td align=\"center\" colspan=\"2\">");
    out.println("<font size=\"3\">");
    out.println("<p><b>Course Statistics for Today<br>");
    out.println(month + "/" + day + "/" + year);
    out.println("</b></font><font size=\"2\"><br><b>Note:</b> Percentages are rounded down to whole number.<br><p>");

    out.println(buildDisplayDateTime());
    out.println("</font></p>");

    out.println("</td></tr>");

    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<tr><td align=\"center\">");
        out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"today\" value=\"\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></td>");

        out.println("<td align=\"center\">");
/*
        if (accessClassicReports(con)) {
            out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_reports\">");
            out.println("<input type=\"hidden\" name=\"today\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\" View Classic Report \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
        }
        out.println("</td></tr>");
*/
    }

    out.println("<tr><td colspan=\"2\">");
    
    courseName = "";            // init as not multi

    // start the main tables that holds each course table
    //out.println("<table cellspacing=10 align=\"center\">");

    //
    // execute searches and display for each course
    //
    for (index=0; index<count; index++) {

       if (multi != 0) {

          courseName = course.get(index); 
       }

        //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");

        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" align=\"center\">");
            bgrndcolor = "#FFFFFF";      // white for excel
            fontcolor = "#000000";      // black for excel
        } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");
        }
        
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        
        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"3\">");
            out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
            out.println("<p align=\"center\"><b>" + courseName + " Summary</b></p>");
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
        out.println("<p align=\"center\"><b>Today</b> (thus far)</p>");
        out.println("</font></td>");
        
        displayCourseSummary(courseName, edate, curr_time, 1, excel, con, out, false);

        if (req.getParameter("excel") == null) {
            out.println("</tr><tr><td colspan=\"3\" align=center>");
            out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
            out.println("<input type=\"hidden\" name=\"today\">");
            out.println("<input type=\"hidden\" name=\"detail\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
            out.println("<input type=\"submit\" value=\" " + courseName + " Detail  \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
            out.println("<input type=\"hidden\" name=\"trans\">");
            out.println("<input type=\"hidden\" name=\"detail\">");
            out.println("<input type=\"hidden\" name=\"today\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
            out.println("<input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

            out.println("</font></td>");
        }
        
        out.println("</font></tr></table><br>");

    }       // end of while Courses - do all courses

    if (multi != 0) {
        
        //
        // if multi course then display grand total of combinded courses
        //
        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\" align=\"center\">");
        } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");
        }
        //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td colspan=\"3\">");
        out.println("<font color=\"" + fontcolor + "\" size=\"3\">");
        out.println("<p align=\"center\"><b>Combind Course Grand Totals</b></p>");
        out.println("</font></td></tr>");

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" + bgrndcolor + "\">");
        out.println("<td>");
        out.println("<font color=\"" + fontcolor + "\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");

        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" + fontcolor + "\" size=\"2\">");
        out.println("<p align=\"center\"><b>Today</b> (thus far)</p>");
        out.println("</font></td>");

        displayCourseSummary("-ALL-", edate, curr_time, 1, excel, con, out, false);
        
        if (req.getParameter("excel") == null) {
            out.println("</tr><tr><td colspan=\"3\" align=center>");
            out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
            out.println("<input type=\"hidden\" name=\"today\">");
            out.println("<input type=\"hidden\" name=\"detail\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"-ALL-\">");
            out.println("<input type=\"submit\" value=\" Combined Detail  \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
            out.println("<input type=\"hidden\" name=\"trans\">");
            out.println("<input type=\"hidden\" name=\"detail\">");
            out.println("<input type=\"hidden\" name=\"today\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"-ALL-\">");
            out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
            out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
            out.println("<input type=\"submit\" value=\" Trans Detail \" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");

            out.println("</font></td>");
        }
        
        out.println("</font></tr></table><br>");
        
    }

    out.println("</td></tr></table>");                // end of main page table & column

    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<table align=center cellspacing=7><tr><td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\" Home \" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
        out.println("</td><td><!--");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\">");
        out.println("<input type=\"hidden\" name=\"today\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        out.println("<input type=\"submit\" value=\" Detail \" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>--></td></tr></table></font>");
    }
    
    out.println("</center></font>");

 }  // end of doTodaySummary
 
 
 private void doTodayDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {

    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    
    String club = (String)sess.getAttribute("club");      // get club name
    String templott = (String)sess.getAttribute("lottery");        // get lottery support indicator
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    String sqlQuery = "";
    
    parmClub parm = new parmClub(0, con); // golf only report
    
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
    String courseName = "";
    
    ArrayList<String> course = new ArrayList<String>();      // unlimited courses
    

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

      if (curr_time < 1200) {           // if AM, then we rolled ahead 1 day

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

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

       try {
          
          course = Utilities.getCourseNames(con);     // get all the course names
       
          count = course.size();                      // number of courses

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

    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course.get(0);  // if multiple courses supported for this club get first course name    

    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | curr_time=" + curr_time + " | courseName=" + courseName + " -->");
    
    // if there is a course name being passed here in the querystring 
    // then just display that course not all of them
    if (req.getParameter("course") != null) {
        courseName = req.getParameter("course");
        count = 1;
    }
        
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | curr_time=" + curr_time + " | courseName=" + courseName + " -->");
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"today\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        if (!courseName.equals("")) out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></p>");
    }
    
    String courseClause = "courseName = ? AND";
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; count = 1; }
    
    // start the main tables that holds each course table
    out.println("<table cellspacing=10>");

    //
    // loop each course and display stats
    //
    for (index=0; index<count; index++) {
       
        if (index > 0) courseName = course.get(index);        // get next course if this is not first time through here

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>"); // posible browser bug - NN

        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
            bgrndcolor = "#FFFFFF";      // white for excel
            fontcolor = "#000000";      // black for excel
        } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        }
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td colspan=\"3\">");
            out.println("<font color=\"" + fontcolor + "\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + "</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td>");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
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
            
            sqlQuery = "" +
            "SELECT SUM(p_9), SUM(p_18) FROM (" +        
            "SELECT p1cw AS cw, " +
                    "p91 AS p_9, IF(p91=0,1,0) AS p_18 " +
                    "FROM teecurr2 ";
                    sqlQuery += buildWhereClause(1, courseName);
                    sqlQuery += "AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
                    //"WHERE " + courseClause + " date = ? AND time <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw AS cw,  " +
                    "p92 AS p_9, IF(p92=0,1,0) AS p_18 " +
                    "FROM teecurr2 ";
                    sqlQuery += buildWhereClause(1, courseName);
                    sqlQuery += "AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw AS cw, " +
                    "p93 AS p_9, IF(p93=0,1,0) AS p_18 " +
                    "FROM teecurr2 ";
                    sqlQuery += buildWhereClause(1, courseName);
                    sqlQuery += "AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw AS cw, " +
                    "p94 AS p_9, IF(p94=0,1,0) AS p_18 " +
                    "FROM teecurr2 ";
                    sqlQuery += buildWhereClause(1, courseName);
                    sqlQuery += "AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw AS cw,  " +
                    "p95 AS p_9, IF(p95=0,1,0) AS p_18 " +
                    "FROM teecurr2 ";
                    sqlQuery += buildWhereClause(1, courseName);
                    sqlQuery += "AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
            ") AS d_table;";
            
            PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
            pstmt1.clearParameters();
            if (courseName.equalsIgnoreCase("-ALL-")) {
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, curr_time); // edate
                pstmt1.setLong(3, sdate);
                pstmt1.setLong(4, curr_time);
                pstmt1.setLong(5, sdate);
                pstmt1.setLong(6, curr_time);
                pstmt1.setLong(7, sdate);
                pstmt1.setLong(8, curr_time);
                pstmt1.setLong(9, sdate);
                pstmt1.setLong(10, curr_time);
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
        out.println("<td align=\"center\"><font size=\"2\"><br><b>");
        out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0, excel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 18
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">18 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18, excel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 9
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">9 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18, excel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // blank row for divider
        out.println("<td colspan=\"3\">&nbsp;</td>");
        
        
        // show member type detail
        displayMemberTypeQuery(courseName, sdate, curr_time, 11, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, curr_time, 11, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, curr_time, 11, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        out.println("</font></tr></table><br>");

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back to Summary\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
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

    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }
    String club = (String)session.getAttribute("club");
    
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
    
    if (club.equals("congressional")) {
        out.println("<p align=\"center\"><input type=\"submit\" value=\"Blue / Gold\" name=\"virtuals\"></p>");
    }
    
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
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0") : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1") : "";
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel") : "";
    String bgrndcolor = "#336633";     // default
    String fontcolor = "#FFFFFF";      // default
    boolean virtuals = (req.getParameter("virtuals") != null) ? true : false;
    
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
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    Calendar cal1 = new GregorianCalendar(start_year, start_month - 1, start_day);
    Calendar cal2 = new GregorianCalendar(end_year, end_month - 1, end_day);
    
    
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only report
   
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
   String courseName = "";        // course names
   String error = "None";
   
   ArrayList<String> course = new ArrayList<String>();      // unlimited courses
   

   multi = parm.multi;
   count = 1;                  // init to 1 course

   //
   //   Check for multiple courses
   //
   if (multi != 0) {           // if multiple courses supported for this club

      try {

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         count = course.size();                      // number of courses

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
    out.println("<tr><td align=\"center\" colspan=\"2\">");

    out.println("<font size=\"3\">");
    out.println("<p><b>Course Statistics - Summary</b><br></font><font size=\"2\">");
    out.println("<b>Note:</b> If applicable, today's counts are not included. Percentages are rounded down to whole number.<br>");
    //out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    //out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    
    out.println(buildDisplayDateTime());
    out.println("</font>");

    out.println("</td></tr>");
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<tr><td align=\"center\">");
        out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"custom\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"cal_box_0\" value=\"" + start_date + "\">");
        out.println("<input type=\"hidden\" name=\"cal_box_1\" value=\"" + end_date + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></td>");

        out.println("<td align=\"center\">");
/*
      if (accessClassicReports(con)) {
        out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_reports\">");
        out.println("<input type=\"hidden\" name=\"custom2\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"smonth\" value=\"" + start_month + "\">");
        out.println("<input type=\"hidden\" name=\"sday\" value=\"" + start_day + "\">");
        out.println("<input type=\"hidden\" name=\"syear\" value=\"" + start_year + "\">");
        out.println("<input type=\"hidden\" name=\"emonth\" value=\"" + end_month + "\">");
        out.println("<input type=\"hidden\" name=\"eday\" value=\"" + end_day + "\">");
        out.println("<input type=\"hidden\" name=\"eyear\" value=\"" + end_year + "\">");
        out.println("<input type=\"submit\" value=\" View Classic Report \" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form>");
      }
*/
        out.println("</td></tr>");
        
    }

    out.println("<tr><td colspan=\"2\" align=\"center\">");
      
    courseName = "";            // init as not multi

    //
    // execute searches and display for each course
    //
    for (index=0; index<count; index++) {
       
        if (multi != 0) courseName = course.get(index);        // get course name if multi

        //
        //  Build a table for each course
        //
        //out.println("<br><table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        
        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
            bgrndcolor = "#FFFFFF";      // white for excel
            fontcolor = "#000000";      // black for excel
        } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        }
        
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"3\">");
            out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
            if (club.equals("congressional") && virtuals && !courseName.equals("Hills Course")) {
                if (courseName.equals("Open Course")) {
                    out.println("<p align=\"center\"><b>Blue Course</b>");
                } else {
                    out.println("<p align=\"center\"><b>Gold Course</b>");
                }
            } else {
                out.println("<p align=\"center\"><b>" + courseName + "</b>");
            }
            out.println("</p></font></td></tr>");
        }

        out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td>");
            out.println("<font size=\"2\" color=\"" +fontcolor+ "\">");
            out.println("<p align=\"left\"><b>Stat</b></p>");
            out.println("</font></td>");
            out.println("<td colspan=\"2\"><font size=\"2\" color=\"" +fontcolor+ "\">");
            out.println("<p align=\"center\"><b>From " + start_month + "/" + start_day + "/" + start_year + " to");
            out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
            out.println("</font></td>");
         
         displayCourseSummary(courseName, sdate, edate, 2, excel, con, out, virtuals);
         
         if (req.getParameter("excel") == null && !virtuals) {
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
      
   }       // end of while Courses - do all courses

   
    // display grand total of all courses (if multi)
    if (multi != 0 && !virtuals) {     
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

        displayCourseSummary("-ALL-", sdate, edate, 2, excel, con, out, false);

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
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    
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
    parmClub parm = new parmClub(0, con); // golf only report
    //parmCourse parmc = new parmCourse();          // allocate a parm block
    int lottery = Integer.parseInt(templott);
    int year = 0;
    int month = 0;
    int day = 0;
    int i = 0;
    int rs_rows1 = 0;
    int tmp_index = 0;
    int total_course_rounds_9 = 0;
    int total_course_rounds_18 = 0;
    boolean new_row = false;
    String tmp_error = "";
    String courseName = (req.getParameter("course") != null) ? req.getParameter("course")  : "";
    

    //
    //   Get multi option, member types, and guest types
    //
    try {

      getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }

    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | courseName=" + courseName + " -->");

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

    //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");

    if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
        out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
        bgrndcolor = "#FFFFFF";      // white for excel
        fontcolor = "#000000";      // black for excel
    } else {
        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
    }
    
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

    //
    // add course name header if multi
    //
    if (!courseName.equals( "" )) {

        out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"3\">");
        out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
        out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
        out.println("</font></td></tr>");
    }

    //
    //  Header row
    //
    out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td>");
    out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
    out.println("<p align=\"left\"><b>Stat</b></p>");
    out.println("</font></td>");
    out.println("<td colspan=\"2\">");
    out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
    out.println("<p align=\"center\"><b>From " + start_month + "/" + start_day + "/" + start_year + " to");
    out.println(" " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
    out.println("</font></td>");
        
    String courseClause = "courseName = ? AND ";
    
    if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; }
    
    String sql = "";
    
    //
    // use the dates provided to search the tee times tables
    //
    try {
        
        total_course_rounds_9 = 0; // reset
        total_course_rounds_18 = 0; // reset

        tmp_error = "SQL1 - Build SQL"; //debug
        
        sql = "" +
        "SELECT SUM(p_9), SUM(p_18) FROM (" +
        "SELECT p1cw AS cw, " +
                "p91 AS p_9, IF(p91=0,1,0) AS p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p2cw AS cw,  " +
                "p92 AS p_9, IF(p92=0,1,0) AS p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p3cw AS cw, " +
                "p93 AS p_9, IF(p93=0,1,0) AS p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p4cw AS cw, " +
                "p94 AS p_9, IF(p94=0,1,0) AS p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
        "UNION ALL " +
        "SELECT p5cw AS cw,  " +
                "p95 AS p_9, IF(p95=0,1,0) AS p_18 " +
                "FROM teepast2 " +
                "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
        ") AS d_table;";

        if (g_debug) out.println("<!-- " + sql + " -->");

        PreparedStatement pstmt1 = con.prepareStatement (sql);
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
        out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0, excel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 18
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">18 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18, excel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // grand total 9
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">9 Hole Rounds");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\"><b>");
        out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18, excel));
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
    displayMemberTypeQuery(courseName, sdate, edate, 12, total_course_rounds_18, total_course_rounds_9, excel, con, out);

    // member type breakdown (by membership)
    displayMemberShipTypeQuery(courseName, sdate, edate, 12, total_course_rounds_18, total_course_rounds_9, excel, con, out);

    // display guest type detail
    displayGuestTypeQuery(courseName, sdate, edate, 12, total_course_rounds_18, total_course_rounds_9, excel, con, out);

    out.println("</font></tr></table><br>");

    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back to Summary\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
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
   parmClub parm = new parmClub(0, con); // golf only report
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

   ArrayList<String> course = new ArrayList<String>();      // unlimited courses

   String courseName = "";        // course names
   String error = "None";
   String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";

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
   lm_end_date = year + "-" + month + "-31";
   
   //
   // Check for multiple courses
   //
   multi = parm.multi;
   count = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      try {

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names

         count = course.size();                      // number of courses

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

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" colspan=\"2\">");
      
      out.println("<font size=\"3\">");
      out.println("<p><b>Course Statistics</b><br></font><font size=\"2\">");
      out.println("<b>Note:</b> Percentages are rounded down to whole number.</p>");

      out.println(buildDisplayDateTime());
      out.println("</font>");

      out.println("</td></tr>");
      
   if (req.getParameter("excel") == null) {     // if normal request
      out.println("<tr><td align=\"center\">");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"round\" value=\"all\">");
      out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</td>");

      out.println("<td align=\"center\">");
/*
      if (accessClassicReports(con)) {
          out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_reports\">");
          out.println("<input type=\"hidden\" name=\"round\" value=\"all\">");
          out.println("<input type=\"submit\" value=\" View Classic Report \" style=\"text-decoration:underline; background:#8B8970\">");
          out.println("</form>");
      }
*/
      out.println("</td></tr>");
   }

      
      out.println("<tr><td colspan=\"2\" align=\"center\">");

   courseName = "";            // init as not multi

   //
   // execute searches and display for each course
   //
   for (index=0; index<count; index++) {

      if (multi != 0) {           // if multiple courses supported for this club

         courseName = course.get(index);      // get course name 
      }

      //
      // use the dates provided to search the stats table
      //
       
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
            
        displayCourseSummaryBig(courseName, excel, con, out);            

        if (req.getParameter("excel") == null) {
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td><td></td><td></td>");

            // button links to detail reports
            out.println("</tr><tr valign=bottom>");
            out.println("<td>&nbsp;</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=lm><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=\"submit\" value=\"LM Detail\" style=\"text-decoration:underline; background:#8B8970; width:110px\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=hidden name=cal_box_0 value=\"" + lm_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + lm_end_date + "\"><input type=\"submit\" value=\"Trans Detail\" style=\"text-decoration:underline; background:#8B8970; width:110px\"></form>");
            out.println("</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=mtd><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=\"submit\" value=\"MTD Detail\" style=\"text-decoration:underline; background:#8B8970; width:110px\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=hidden name=cal_box_0 value=\"" + mtd_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + mtd_end_date + "\"><input type=\"submit\" value=\"Trans Detail\" style=\"text-decoration:underline; background:#8B8970; width:110px\"></form>");
            out.println("</td>");

            out.println("<td align=\"center\" colspan=\"2\">");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=ytd><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=\"submit\" value=\"YTD Detail\" style=\"text-decoration:underline; background:#8B8970; width:110px\"></form>");
            out.println("<form method=get action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\"><input type=hidden name=trans><input type=hidden name=detail><input type=hidden name=course value=\"" + courseName + "\"><input type=hidden name=cal_box_0 value=\"" + ytd_start_date + "\"><input type=hidden name=cal_box_1 value=\"" + ytd_end_date + "\"><input type=\"submit\" value=\"Trans Detail\" style=\"text-decoration:underline; background:#8B8970; width:110px\"></form>");
            out.println("</td>");
        
        } // end if not excel

        out.println("</font></tr><tr><td colspan=\"7\"></td></tr></table><br>");

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

        displayCourseSummaryBig("-ALL-", excel, con, out);
        
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
   out.println("</center></font><br></body></html>");
     

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
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String tmp_error = "";
    String courseName = "";                         // course names
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    String tmp_sql = "";
    
    ArrayList<String> course = new ArrayList<String>();      // unlimited courses

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

    //
    //   Get multi option
    //
    parmClub parm = new parmClub(0, con); // golf only report
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
      
    //
    //  If current month is January, then we must change the date to display to December of the prev year
    //
    if (month == 0) {
       
       month = 12;
       year--;
    }
  
    sdate = (year * 10000) + (month * 100);         // create a edate field of yyyymmdd for last month
    edate = sdate;
    sdate += 1;
    edate += 31;
   
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        try {

            //
            //  Get the names of all courses for this club
            //
            course = Utilities.getCourseNames(con);     // get all the course names

            count = course.size();                      // number of courses

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

    if (multi != 0) courseName = course.get(index);  // if multiple courses supported for this club get first course name

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
    for (index=0; index<count; index++) {

       if (index > 0) courseName = course.get(index);  // get course name if not first time here

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr><td>" : "</td><td>");

        //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        
        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
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

            out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"3\">");
            out.println("<font color=\"" +fontcolor+ "\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td>");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
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
            
            tmp_sql =  "" +
                    "SELECT SUM(p_9), SUM(p_18) FROM (" +        
                        "SELECT p1cw AS cw, " +
                                "p91 AS p_9, IF(p91=0,1,0) AS p_18 " +
                                "FROM teepast2 " +
                                "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
                        "UNION ALL " +
                        "SELECT p2cw AS cw, " +
                                "p92 AS p_9, IF(p92=0,1,0) AS p_18 " +
                                "FROM teepast2 " +
                                "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
                        "UNION ALL " +
                        "SELECT p3cw AS cw, " +
                                "p93 AS p_9, IF(p93=0,1,0) AS p_18 " +
                                "FROM teepast2 " +
                                "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
                        "UNION ALL " +
                        "SELECT p4cw AS cw, " +
                                "p94 AS p_9, IF(p94=0,1,0) AS p_18 " +
                                "FROM teepast2 " +
                                "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
                        "UNION ALL " +
                        "SELECT p5cw AS cw, " +
                                "p95 AS p_9, IF(p95=0,1,0) AS p_18 " +
                                "FROM teepast2 " +
                                "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
                    ") AS d_table;";
            
            if (g_debug) out.println("<!-- " + tmp_sql + " -->");
                    
            PreparedStatement pstmt1 = con.prepareStatement ( tmp_sql );
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
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 18
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 9
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18, excel));
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
        displayMemberTypeQuery(courseName, sdate, edate, 14, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, edate, 14, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, edate, 14, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        out.println("</font></tr></table><br>");

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back to Summary\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
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
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    
    parmClub parm = new parmClub(0, con); // golf only report
    
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
    String courseName = "";                         // course names

    ArrayList<String> course = new ArrayList<String>();      // unlimited courses

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
   
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        try {

            //
            //  Get the names of all courses for this club
            //
            course = Utilities.getCourseNames(con);     // get all the course names

            count = course.size();                      // number of courses

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

    if (multi != 0) courseName = course.get(index);  // if multiple courses supported for this club get first course name

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
    for (index=0; index<count; index++) {

        if (index > 0) courseName = course.get(index);  // get course name if not first time here

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr><td>" : "</td><td>");

        //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        
        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
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

            out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"3\">");
            out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td>");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p align=\"left\"><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
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
            "SELECT SUM(p_9), SUM(p_18) FROM (" +        
            "SELECT p1cw AS cw, " +
                    "p91 AS p_9, IF(p91=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw AS cw,  " +
                    "p92 AS p_9, IF(p92=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw AS cw, " +
                    "p93 AS p_9, IF(p93=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw AS cw, " +
                    "p94 AS p_9, IF(p94=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw AS cw,  " +
                    "p95 AS p_9, IF(p95=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
            ") AS d_table;");
            
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
            out.println("<td align=\"center\"><font size=\"2\"><br><b>");
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 18
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 9
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18, excel));
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
        displayMemberTypeQuery(courseName, sdate, edate, 15, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, edate, 15, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, edate, 15, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        out.println("</font></tr></table><br>");

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back to Summary\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
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
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    
    parmClub parm = new parmClub(0, con); // golf only report
    
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
    String courseName = "";                         // course names

    ArrayList<String> course = new ArrayList<String>();      // unlimited courses
    
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
   
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");

    //
    // Check for multiple courses
    //
    multi = parm.multi;
    count = 1;                  // init to 1 course

    if (multi != 0) {           // if multiple courses supported for this club

        try {

            //
            //  Get the names of all courses for this club
            //
            course = Utilities.getCourseNames(con);     // get all the course names

            count = course.size();                      // number of courses

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

    if (multi != 0) courseName = course.get(index);  // if multiple courses supported for this club get first course name

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
    for (index=0; index<count; index++) {

       if (index > 0) courseName = course.get(index);  // get next course name if not first time here

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>");

        //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        
        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
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

            out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td colspan=\"3\">");
            out.println("<font size=\"3\" color=\"" +fontcolor+ "\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" +bgrndcolor+ "\"><td>");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
        out.println("<p><b>Stat</b></p>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\">");
        out.println("<font color=\"" +fontcolor+ "\" size=\"2\">");
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
            "SELECT SUM(p_9), SUM(p_18) FROM (" +        
            "SELECT p1cw AS cw, " +
                    "p91 AS p_9, IF(p91=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw AS cw,  " +
                    "p92 AS p_9, IF(p92=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw AS cw, " +
                    "p93 AS p_9, IF(p93=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw AS cw, " +
                    "p94 AS p_9, IF(p94=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw AS cw,  " +
                    "p95 AS p_9, IF(p95=0,1,0) AS p_18 " +
                    "FROM teepast2 " +
                    "WHERE " + courseClause + " date >= ? AND date <= ? AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
            ") AS d_table;");
            
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
            out.println("<font size=\"2\" color=><br>");
            out.println("<b><u>Total Rounds Played:</u></b>");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><br><b>");
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 18
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // grand total 9
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18, excel));
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
        displayMemberTypeQuery(courseName, sdate, edate, 16, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // member type breakdown (by membership)
        displayMemberShipTypeQuery(courseName, sdate, edate, 16, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        // display guest type detail
        displayGuestTypeQuery(courseName, sdate, edate, 16, total_course_rounds_18, total_course_rounds_9, excel, con, out);

        out.println("</font></tr></table><br>");

    }       // end of while Courses - do all courses
   
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back to Summary\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
    }

    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");

 }  // end of doThisYearDetail
 
  
 private void doTransDetail(HttpServletRequest req, PrintWriter out, Connection con, HttpSession sess) {
    
    // first update the tmodes table for this club
    SystemUtils.doTableUpdate_tmodes(con);
    
    // 
    //  Declare our local variables
    //
    long sdate = 0;
    long edate = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int index = 0;
    int count = 1; // default is 1 course
    int multi = 0;
    int start_year;
    int start_month;
    int start_day;
    int end_year;
    int end_month;
    int end_day;
    
    if (g_debug == true) out.println("<!-- doTransDetail -->");
    int report_type = (req.getParameter("today") != null) ? 1 : 13;
    
    boolean new_row = false;
    
    String start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
    String end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    String error_text = "";
    String courseName = "";
    String bgrndcolor = "#336633";      // default
    String fontcolor = "#FFFFFF";      // default
    String sqlQuery = "";
    String tableName = (report_type == 1) ? "teecurr2" : "teepast2";
    
    ArrayList<String> course = new ArrayList<String>();      // unlimited courses
    
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
    //  Get today's date and current time and calculate date & time values
    //
    Calendar cal = new GregorianCalendar();       // get todays date

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    int cal_hour = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
    int cal_min = cal.get(Calendar.MINUTE);
    int curr_time = (cal_hour * 100) + cal_min;    // get time in hhmm format
    curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

    if (curr_time < 0) {          // if negative, then we went back or ahead one day

        curr_time = 0 - curr_time;        // convert back to positive value

        if (curr_time < 1200) {           // if AM, then we rolled ahead 1 day

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
   
    // if this is a 'today' report then subsitute the time for the ending date
    if (report_type == 1) edate = curr_time;
       
    Statement stmt = null;
    ResultSet rs = null; 
    
    String club = (String)sess.getAttribute("club");      // get club name

    //
    //   Get multi setting
    //
    parmClub parm = new parmClub(0, con); // golf only report
    try {
        getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }
    multi = parm.multi;
    
    if (multi != 0) {           // if multiple courses supported for this club

        try {

            //
            //  Get the names of all courses for this club
            //
            course = Utilities.getCourseNames(con);     // get all the course names

            count = course.size();                      // number of courses

        }
        catch (Exception exc) {
            displayDatabaseErrMsg("Can not establish connection.", "", out);
            return;
        } // end try
        
    } // end if multiple courses for this club

    
    courseName = "";            // init as not multi
    index = 0;

    if (multi != 0) courseName = course.get(index);  // if multiple courses supported for this club get first course name

    // if there is a course name being passed here in the querystring 
    // then just display that course not all of them
    if (req.getParameter("course") != null) {
        courseName = req.getParameter("course");
        count = 1;
    }
    
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | courseName=" + courseName + " | report_type=" + report_type + " -->");
    
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
    //out.println("Percentages for Member Types and Membership Types are of 'Rounds by Members'.<br>");
    //out.println("Percentages for Guest Types are of 'Rounds by Guests'.  Others are of 'Total Rounds'.</p>");
    out.println(buildDisplayDateTime());
    out.println("</font>");
    
    if (req.getParameter("excel") == null) {     // if normal request
        out.println("<p><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_course_rounds\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"trans\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"detail\" value=\"\">");
        if (report_type == 1) { out.println("<input type=\"hidden\" name=\"today\" value=\"\">"); }
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
    for (index=0; index<count; index++) {

       if (index > 0) courseName = course.get(index);  // get next course name if not first time here

        new_row = (new_row == false);
        out.println((new_row == true) ? "</td></tr><tr valign=top><td>" : "</td><td>");

        if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format
            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" cellpadding=\"5\">");
            bgrndcolor = "#FFFFFF";      // white for excel
            fontcolor = "#000000";      // black for excel
        } else {
            out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        }

        //out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

        //
        // add course name header if multi
        //
        if (!courseName.equals( "" )) {

            out.println("<tr bgcolor=\"" + bgrndcolor + "\"><td colspan=\"4\">");
            out.println("<font color=\"" + fontcolor + "\" size=\"3\">");
            out.println("<p align=\"center\"><b>" + courseName + " Detail<br>");
            out.println("</b></p>");
            //out.println(start_month + "/" + start_day + "/" + start_year + " - " + end_month + "/" + end_day + "/" + end_year + "</b></p>");
            out.println("</font></td></tr>");
        }

        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" + bgrndcolor + "\">");
        out.println("<td colspan=\"2\"><font color=\"" + fontcolor + "\" size=\"2\" nowrap>");
        out.println("<b>Stat</b>");
        out.println("</font></td>");
        out.println("<td colspan=\"2\" align=\"center\"><font color=\"" + fontcolor + "\" size=\"2\">");
        //out.println("<b>Count</b>");
        if (report_type == 1) {
            out.println("<b>Today (thus far)</b>");
        } else {
            out.println("<b>" + start_month + "/" + start_day + "/" + start_year + " - " + end_month + "/" + end_day + "/" + end_year + "</b>");
        }
        //out.println("<b>" + start_month + "/" + start_day + "/" + start_year + " - " + end_month + "/" + end_day + "/" + end_year + "</b>");
        out.println("</font></td></tr>");

        // display the total counts for this period/course
        //String courseClause = "courseName = ? AND ";
        String tmp_error = "";
        int total_course_rounds_9;
        int total_course_rounds_18;
        
        //if (courseName.equalsIgnoreCase("-ALL-")) { courseClause = ""; }

        //
        // use the dates provided to search the tee times tables
        //
        try {

            total_course_rounds_9 = 0; // reset
            total_course_rounds_18 = 0; // reset

            tmp_error = "SQL1 - Build SQL"; //debug
// date >= ? AND date <= ? AND 
            sqlQuery = "" + 
            "SELECT SUM(p_9), SUM(p_18) FROM (" +        
            "SELECT p1cw AS cw, " +
                    "p91 AS p_9, IF(p91=0,1,0) AS p_18 " +
                    "FROM " + tableName + " ";
            sqlQuery += buildWhereClause(report_type, courseName);
            sqlQuery += "AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
                    //"WHERE " + courseClause + " show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p2cw AS cw,  " +
                    "p92 AS p_9, IF(p92=0,1,0) AS p_18 " +
                    "FROM " + tableName + " ";
            sqlQuery += buildWhereClause(report_type, courseName);
            sqlQuery += "AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p3cw AS cw, " +
                    "p93 AS p_9, IF(p93=0,1,0) AS p_18 " +
                    "FROM " + tableName + " ";
            sqlQuery += buildWhereClause(report_type, courseName);
            sqlQuery += "AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p4cw AS cw, " +
                    "p94 AS p_9, IF(p94=0,1,0) AS p_18 " +
                    "FROM " + tableName + " ";
            sqlQuery += buildWhereClause(report_type, courseName);
            sqlQuery += "AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT p5cw AS cw,  " +
                    "p95 AS p_9, IF(p95=0,1,0) AS p_18 " +
                    "FROM " + tableName + " ";
            sqlQuery += buildWhereClause(report_type, courseName);
            sqlQuery += "AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
            ") AS d_table;";

            if (g_debug == true) out.println("<!-- Summary Query for Trans Report -->");
            if (g_debug == true) out.println("<!-- " + sqlQuery + " -->");
            
            PreparedStatement pstmt1 = con.prepareStatement (sqlQuery);
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
            rs = pstmt1.executeQuery();

            tmp_error = "SQL1 - RS Loop"; //debug

            while ( rs.next() ) {

                total_course_rounds_9 = rs.getInt(1);
                total_course_rounds_18 = rs.getInt(2);

            }

            pstmt1.close();            

            tmp_error = "SQL1 - RS Closed"; //debug

            out.println("</tr><tr>");                       // Grand totals
            out.println("<td align=\"left\" colspan=\"2\">");
            out.println("<font size=\"2\"><br>");
            out.println("<b><u>Total Rounds Played:</u></b>");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><br><b>");
            out.println(buildDisplayValue(total_course_rounds_9, total_course_rounds_18, 0, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr><td></td>");                          // grand total 18
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">18 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_18, 0, total_course_rounds_9 + total_course_rounds_18, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr><td></td>");                          // grand total 9
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">9 Hole Rounds");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\"><b>");
            out.println(buildDisplayValue(total_course_rounds_9, 0, total_course_rounds_9 + total_course_rounds_18, excel));
            out.println("</b></font></td>");

            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"4\">&nbsp;</td>");
/*
            out.println("<tr>");
            out.println("<td colspan=\"4\"><hr></td>");
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"4\">&nbsp;</td>");
*/                    
            // end of course grand totals

        }
        catch (Exception exc) {

            displayDatabaseErrMsg("DEBUG: " + tmp_error + "<br>" + exc.getMessage(), exc.toString(), out);
            return;

        } // end of try/catch 
        
        //
        //  Header row
        //
        out.println("<tr bgcolor=\"" + bgrndcolor + "\">");
        out.println("<td><font color=\"" + fontcolor + "\" size=\"2\" nowrap>");
        out.println("<b>Transportation Mode</b>");
        out.println("</font></td><td><font color=\"" + fontcolor + "\" size=\"2\">");
        out.println("<b>Member Type / Membership Type</b>");
        out.println("</font></td><td colspan=\"2\" align=\"center\"><font color=\"" + fontcolor + "\" size=\"2\">");
        //out.println("<b>Count</b>");
        if (report_type == 1) {
            out.println("<b>Today (thus far)</b>");
        } else {
            out.println("<b>" + start_month + "/" + start_day + "/" + start_year + " - " + end_month + "/" + end_day + "/" + end_year + "</b>");
        }
        out.println("</font></td></tr>");
        
        displayTransportationQuery(courseName, sdate, edate, report_type, excel, con, out);
        
        out.println("</font></tr></table><br>");
        
    } // end while do all courses
    
    out.println("</table>");

    out.println("</td></tr></table>");                // end of main page table & column

    if (req.getParameter("excel") == null) {
        out.println("<center><form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"button\" value=\"Back to Summary\" onclick=\"window.history.go(-1);\" style=\"text-decoration:underline; background:#8B8970\">&nbsp; &nbsp; ");
        out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\"> &nbsp; &nbsp; ");
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
 private void displayTransportationQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, String pExcel, Connection con, PrintWriter out) {

    if (g_debug) out.println("<!-- displayTransportationQuery -->");
    if (g_debug) out.println("<!-- pStartDate=" + pStartDate + " | pEndDate=" + pEndDate + " | pCourseName=" + pCourseName + " | pReportType=" + pReportType + "-->");

    Statement stmt = null;
    ResultSet rs = null;
    int tot9rounds = 0;
    int tot18rounds = 0;
    int totRounds = 0;
    int trans_mship9Total = 0;
    int trans_mship18Total = 0;
    int trans_type9Total = 0;
    int trans_type18Total = 0;
    int tmp_trans_type_total = 0;

    String error_text = "";
    
    //String tableName = (pReportType == 1) ? "teecurr2" : "teepast2";
    String sqlQuery = "";
    
    if (pReportType == 1) {

        sqlQuery = "" +
             "SELECT * FROM (" +
             "SELECT tmode, cw, IFNULL(m_ship,IF(null_user=1,'ZZZGuest','Removed')) AS m_ship, " +
             "IFNULL(m_type, IFNULL(guest_type,IF(null_user=1,'Unknown','Removed from Member Database'))) AS m_or_g_type, SUM(p_9), SUM(p_18) " +
                "FROM ( " +
                "SELECT courseName as cn, p1cw AS cw, " +
                    "p91 AS p_9, IF(p91=0,1,0) AS p_18, " +
                    "m_type, m_ship, guest AS guest_type, " +
                    "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                    "FROM teecurr2 " +
                    "LEFT OUTER JOIN member2b ON username1 = username " +
                    "LEFT OUTER JOIN guest5 ON guest = LEFT(player1,LENGTH(guest)) ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p2cw AS cw,  " +
                    "p92 AS p_9, IF(p92=0,1,0) AS p_18, " +
                    "m_type, m_ship, guest AS guest_type, " +
                    "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                    "FROM teecurr2 " +
                    "LEFT OUTER JOIN member2b ON username2 = username " +
                    "LEFT OUTER JOIN guest5 ON guest = LEFT(player2,LENGTH(guest)) ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p3cw AS cw, " +
                    "p93 AS p_9, IF(p93=0,1,0) AS p_18, " +
                    "m_type, m_ship, guest AS guest_type, " +
                    "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                    "FROM teecurr2 " +
                    "LEFT OUTER JOIN member2b ON username3 = username " +
                    "LEFT OUTER JOIN guest5 ON guest = LEFT(player3,LENGTH(guest)) ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p4cw AS cw, " +
                    "p94 AS p_9, IF(p94=0,1,0) AS p_18, " +
                    "m_type, m_ship, guest AS guest_type, " +
                    "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                    "FROM teecurr2 " +
                    "LEFT OUTER JOIN member2b ON username4 = username " +
                    "LEFT OUTER JOIN guest5 ON guest = LEFT(player4,LENGTH(guest)) ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p5cw AS cw, " +
                    "p95 AS p_9, IF(p95=0,1,0) AS p_18, " +
                    "m_type, m_ship, guest AS guest_type, " +
                    "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                    "FROM teecurr2 " +
                    "LEFT OUTER JOIN member2b ON username5 = username " +
                    "LEFT OUTER JOIN guest5 ON guest = LEFT(player5,LENGTH(guest)) ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
            "UNION ALL " +
            "SELECT tmodes.courseName as cn, tmodea AS cw, p_9, p_18, m_type, m_ship, guest_type, null_user FROM ( " +
                    "SELECT (0) AS p_9, (0) AS p_18, " +
                            "null AS m_type, null AS m_ship, guest AS guest_type, " +
                            "(1) AS null_user " +
                            "FROM guest5 " +
                    "UNION ALL " +
                    "SELECT (0) AS p_9, (0) AS p_18, m2b.m_type AS m_type, m5.mship AS m_ship, null AS guest_type, (1) AS null_user " +
                            "FROM member2b m2b, mship5 m5 " +
                            "WHERE m2b.m_type IS NOT null " +
                            "GROUP BY m5.mship, m2b.m_type " +
            ") AS null_entries, tmodes";
        sqlQuery += ") AS d_table LEFT OUTER JOIN tmodes ON (tmodea = cw AND cn = tmodes.courseName";
        sqlQuery += ") GROUP BY cw, m_ship, m_or_g_type WITH ROLLUP) AS c_table GROUP BY cw, m_ship, m_or_g_type ORDER BY cw, m_ship, m_or_g_type;";

    } else {

        sqlQuery = "SELECT * FROM (" +
             "SELECT tmode, cw, IF(m_ship='',IF(null_user=1,'ZZZGuest','Removed'), m_ship) AS m_ship, " +
             "IF(m_type='',IF(guest_type='',IF(null_user=1,'Unknown','Removed from Member Database'), guest_type), m_type) AS m_or_g_type, SUM(p_9), SUM(p_18) " +
                "FROM ( " +
                "SELECT courseName as cn, p1cw AS cw, " +
                    "p91 AS p_9, IF(p91=0,1,0) AS p_18, " +
                    "mtype1 AS m_type, mship1 AS m_ship, gtype1 AS guest_type, " +
                    "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                    "FROM teepast2 ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show1 = 1 AND ((username1 <> '' AND username1 IS NOT null) OR (player1 <> '' AND player1 <> 'x' AND player1 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p2cw AS cw,  " +
                    "p92 AS p_9, IF(p92=0,1,0) AS p_18, " +
                    "mtype2 AS m_type, mship2 AS m_ship, gtype2 AS guest_type, " +
                    "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                    "FROM teepast2 ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show2 = 1 AND ((username2 <> '' AND username2 IS NOT null) OR (player2 <> '' AND player2 <> 'x' AND player2 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p3cw AS cw, " +
                    "p93 AS p_9, IF(p93=0,1,0) AS p_18, " +
                    "mtype3 AS m_type, mship3 AS m_ship, gtype3 AS guest_type, " +
                    "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                    "FROM teepast2 ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show3 = 1 AND ((username3 <> '' AND username3 IS NOT null) OR (player3 <> '' AND player3 <> 'x' AND player3 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p4cw AS cw, " +
                    "p94 AS p_9, IF(p94=0,1,0) AS p_18, " +
                    "mtype4 AS m_type, mship4 AS m_ship, gtype4 AS guest_type, " +
                    "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                    "FROM teepast2 ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show4 = 1 AND ((username4 <> '' AND username4 IS NOT null) OR (player4 <> '' AND player4 <> 'x' AND player4 IS NOT null)) " +
            "UNION ALL " +
            "SELECT courseName as cn, p5cw AS cw, " +
                    "p95 AS p_9, IF(p95=0,1,0) AS p_18, " +
                    "mtype5 AS m_type, mship5 AS m_ship, gtype5 AS guest_type, " +
                    "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                    "FROM teepast2 ";
        sqlQuery += buildWhereClause(pReportType, pCourseName);
        sqlQuery += "AND show5 = 1 AND ((username5 <> '' AND username5 IS NOT null) OR (player5 <> '' AND player5 <> 'x' AND player5 IS NOT null)) " +
            "UNION ALL " +
            "SELECT tmodes.courseName as cn, tmodea AS cw, p_9, p_18, m_type, m_ship, guest_type, null_user FROM ( " +
                    "SELECT (0) AS p_9, (0) AS p_18, " +
                            "'' AS m_type, '' AS m_ship, guest AS guest_type, " +
                            "(1) AS null_user " +
                            "FROM guest5 " +
                    "UNION ALL " +
                    "SELECT (0) AS p_9, (0) AS p_18, m2b.m_type AS m_type, m5.mship AS m_ship, '' AS guest_type, (1) AS null_user " +
                            "FROM member2b m2b, mship5 m5 " +
                            "WHERE m2b.m_type IS NOT null " +
                            "GROUP BY m5.mship, m2b.m_type " +
            ") AS null_entries, tmodes";
        sqlQuery += ") AS d_table LEFT OUTER JOIN tmodes ON (tmodea = cw AND cn = tmodes.courseName";
        sqlQuery += ") GROUP BY cw, m_ship, m_or_g_type WITH ROLLUP) AS c_table GROUP BY cw, m_ship, m_or_g_type ORDER BY cw, m_ship, m_or_g_type;";

    }

    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
    
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
            //pstmt1.setString(16, pCourseName);
            //pstmt1.setString(17, pCourseName);
        }
                
        rs = pstmt1.executeQuery();

        while ( rs.next() ) { 
          
            // check to see if this row contains the overall totals
            if (rs.getString(2) == null && rs.getString(3) == null && rs.getString(4) == null) {

                // grand total of all rounds gathered for this report
                // these values have already been displayed in doTransDetail but we
                // need them for displaying percentages
                tot9rounds = rs.getInt(5);
                tot18rounds = rs.getInt(6);
                totRounds = tot9rounds + tot18rounds;

                if (g_debug) out.println("<!-- 9hole=" + tot9rounds + ", 18hole=" + tot18rounds + ", total=" + totRounds + " -->");
                
                
            } else {

                // check to see if this row contains the totals for a tmode
                if (rs.getString(3) == null && rs.getString(4) == null) {

                    // total 9 & 18 rounds for a particular trans mode
                    trans_type9Total = rs.getInt(5);
                    trans_type18Total = rs.getInt(6);

                    out.println("<tr>"); // spacer row
                    out.println("<td>&nbsp;</td><td width=200>&nbsp;</td><td colspan=\"2\">&nbsp;</td>");
                    out.println("</tr>");

                    out.println("<tr>");
                    out.println("<td nowrap>");
                    out.println("<font size=\"3\">");
                    out.println("<b><u>" + rs.getString(1) + "</u>:</b>"); //trans_mode_name
                    out.println("</font></td><td></td>");
                    out.println("<td align=\"center\" nowrap>");
                    out.println("<font size=\"2\"><b>");
                    out.println(buildDisplayValue(trans_type9Total, trans_type18Total, totRounds, pExcel));
                    out.println("</b></font></td></tr>");

                    // ADDED 9/18 BREAKDOWN TO TMODE TYPE 2-7-07
                    if (trans_type18Total > 0) {
                        out.println("<tr>");
                        out.println("<td></td><td align=\"right\">");
                        out.println("<font size=\"2\">18 Hole Rounds:");
                        out.println("</font></td>");
                        out.println("<td align=\"center\" nowrap><font size=\"2\">");
                        out.println(buildDisplayValue(trans_type18Total, 0, totRounds, pExcel));
                        out.println("</font></td></tr>");
                    }

                    if (trans_type9Total > 0) {
                        out.println("<tr>");
                        out.println("<td></td><td align=\"right\">");
                        out.println("<font size=\"2\">9 Hole Rounds:");
                        out.println("</font></td>");
                        out.println("<td align=\"center\" nowrap><font size=\"2\">");
                        out.println(buildDisplayValue(trans_type9Total, 0, totRounds, pExcel));
                        out.println("</font></td></tr>");
                    }
                    
                    
                    
                    out.println("<tr>"); // spacer row
                    out.println("<td></td><td width=\"200\"></td><td colspan=\"2\"></td>");
                    out.println("</tr>");

                    tmp_trans_type_total = trans_type9Total + trans_type18Total;

                } else {

                    if (rs.getString(3) != null && rs.getString(4) == null) {

                        // total rounds for particular membership type
                        trans_mship9Total = rs.getInt(5);
                        trans_mship18Total = rs.getInt(6);

                        out.println("<tr><td colspan=\"4\"></td></tr>");
                        out.println("<tr>");
                        out.print("<td></td><td nowrap><font size=\"2\"><b><u>");

                        if (rs.getString(3).equals("ZZZGuest")) {

                            out.print(" By Guest Type:&nbsp;");

                        } else {

                            out.print("Membership Type:&nbsp; " + rs.getString(3)); //tmp_mship_type

                        }
                        out.println("</u></b>");

                        out.println("</font></td>");
                        out.println("<td align=\"center\" nowrap><font size=\"2\"><b>");
                        out.println(buildDisplayValue(trans_mship9Total, trans_mship18Total, tmp_trans_type_total, pExcel));
                        out.println("</b></font></td>");
                        out.println("</tr>");
                        out.println("<tr><td colspan=\"4\"></td></tr>");
                      
                    } else {
                        
                        // this row contains 9 & 18 hole totals for a particular m_or_g_type
                        // most detailed level of breakdown
/*
                        // leaving this here for now as reference
                        fld_tmode[rs_rows] = rs.getString(1);
                        fld_cw[rs_rows] = rs.getString(2); 
                        fld_mship[rs_rows] = rs.getString(3); 
                        fld_mtype[rs_rows] = rs.getString(4); 
                        fld_sum9[rs_rows] = rs.getInt(5);
                        fld_sum18[rs_rows] = rs.getInt(6);
  */                    

                        if (rs.getInt(5) + rs.getInt(6) > 0) {

                            out.println("<tr>");
                            out.println("<td></td><td align=\"right\">"); //was center
                            out.println("<font size=\"2\"><b>");
                            out.println(rs.getString(4)); // mtype
                            out.println("</b></font></td>");
                            out.println("<td align=\"center\" nowrap>");
                            out.println("<font size=\"2\"><b>");
                            out.println(buildDisplayValue(rs.getInt(5), rs.getInt(6), tmp_trans_type_total, pExcel));
                            out.println("</b></font></td>");

                            if (rs.getInt(6) > 0) {
                                out.println("</tr><tr>");
                                out.println("<td></td><td align=\"right\">");
                                out.println("<font size=\"2\">18 Hole Rounds:");
                                out.println("</font></td>");
                                out.println("<td align=\"center\" nowrap><font size=\"2\">");
                                out.println(buildDisplayValue(rs.getInt(6), 0, tmp_trans_type_total, pExcel));
                                out.println("</font></td>");
                            }

                            if (rs.getInt(5) > 0) {
                                out.println("</tr><tr>");
                                out.println("<td></td><td align=\"right\">");
                                out.println("<font size=\"2\">9 Hole Rounds:");
                                out.println("</font></td>");
                                out.println("<td align=\"center\" nowrap><font size=\"2\">");
                                out.println(buildDisplayValue(rs.getInt(5), 0, tmp_trans_type_total, pExcel));
                                out.println("</font></td>");
                            }

                            out.println("</tr>");

                        } // end if block (whether or not to show this data - if 0 count then don't show to cut down report length)
  
                    } // end else if block (checking to see if this row was a membership type subtotal)

                }

            }

        } // end rs while loop

        pstmt1.close();
        

    } catch (Exception exc) {

        displayDatabaseErrMsg(error_text, exc.toString(), out);
        return;

    } // end of recordset retrieval and processing

        
 }
 
 
 private void displayMemberTypeQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, int pGrandTotal_18, int pGrandTotal_9, String pExcel, Connection con, PrintWriter out) {


    if (g_debug) {
        out.println("<!-- STARTING displayMemberTypeQuery -->");
        out.println("<!-- pCourseName=" + pCourseName + " -->");
        out.println("<!-- pGrandTotal_18=" + pGrandTotal_18 + " -->");
        out.println("<!-- pGrandTotal_9=" + pGrandTotal_9 + " -->");
    }


    // 
    // start by member type only
    //
    Statement stmt = null;
    ResultSet rs = null;

    //int tmp_mem_types = 0;
    int totalMemberRounds = 0;
    int tot9rounds = 0;
    int tot18rounds = 0;
    
    ArrayList<Integer> mem_type9 = new ArrayList<Integer>(24);
    ArrayList<Integer> mem_type18 = new ArrayList<Integer>(24);
    ArrayList<Integer> mem_type9Total = new ArrayList<Integer>(24);
    ArrayList<Integer> mem_type18Total = new ArrayList<Integer>(24);
    ArrayList<String> mem_type = new ArrayList<String>(24);
    
    //int [] mem_type9 = new int [g_MAX_ARRAY_SIZE];
    //int [] mem_type18 = new int [g_MAX_ARRAY_SIZE];
    //int [] mem_type9Total = new int [40];          // allow for 24 plus old/unknown types
    //int [] mem_type18Total = new int [40];
    
    //String [] mem_type = new String [g_MAX_ARRAY_SIZE];
    
    //String tableName = (pReportType == 11) ? "teecurr2" : "teepast2";
    String sqlQuery;

    // build sql statement

    if (pReportType == 11) {

        sqlQuery = "" +
        "SELECT IFNULL(m_type, IF(null_user=1,'Unknown','Removed from Member Database')) AS m_type, SUM(p_9), SUM(p_18) FROM ( " +
        "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, " +
            "m_type, " +
            "IF(IFNULL(username1,'')='',1,0) AS null_user " +
            "FROM teecurr2 " +
            "LEFT OUTER JOIN member2b ON username1 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, " +
            "m_type, " +
            "IF(IFNULL(username2,'')='',1,0) AS null_user " +
            "FROM teecurr2 " +
            "LEFT OUTER JOIN member2b ON username2 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, " +
            "m_type, " +
            "IF(IFNULL(username3,'')='',1,0) AS null_user " +
            "FROM teecurr2 " +
            "LEFT OUTER JOIN member2b ON username3 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, " +
            "m_type, " +
            "IF(IFNULL(username4,'')='',1,0) AS null_user " +
            "FROM teecurr2 " +
            "LEFT OUTER JOIN member2b ON username4 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, " +
            "m_type, " +
            "IF(IFNULL(username5,'')='',1,0) AS null_user " +
            "FROM teecurr2 " +
            "LEFT OUTER JOIN member2b ON username5 = username ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT (0) AS p_9, (0) AS p_18, " +
            "m2b.m_type AS m_type, " +
            "(1) AS null_user " +
            "FROM member2b m2b " +
            "WHERE m2b.m_type IS NOT NULL " +
            "GROUP BY m2b.m_type " +
        ") AS d_table GROUP BY m_type WITH ROLLUP;";

    } else {
        
        sqlQuery = "" + 
        "SELECT IF(m_type='', IF(null_user=1,'Unknown','Removed from Member Database'), m_type) AS m_type, SUM(p_9), SUM(p_18) FROM ( " +      
        "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, mtype1 AS m_type, " +
            "IF(IFNULL(username1,'')='',1,0) AS null_user " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, mtype2 AS m_type, " +
            "IF(IFNULL(username2,'')='',1,0) AS null_user " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, mtype3 AS m_type, " +
            "IF(IFNULL(username3,'')='',1,0) AS null_user " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, mtype4 AS m_type, " +
            "IF(IFNULL(username4,'')='',1,0) AS null_user " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, mtype5 AS m_type, " +
            "IF(IFNULL(username5,'')='',1,0) AS null_user " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(pReportType, pCourseName);
            sqlQuery += "AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT (0) AS p_9, (0) AS p_18, " +
            "m2b.m_type AS m_type, " +
            "(1) AS null_user " +
            "FROM member2b m2b " +
            "WHERE m2b.m_type IS NOT NULL " +
            "GROUP BY m2b.m_type " +
        ") AS d_table GROUP BY m_type WITH ROLLUP;";
        
    }


    if (g_debug) out.println("<!-- " + sqlQuery + " -->");

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

                mem_type.add(rs.getString(1));
                mem_type9Total.add(rs.getInt(2));
                mem_type18Total.add(rs.getInt(3));
                //tmp_mem_types++;

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
        out.println(buildDisplayValue(totalMemberRounds, 0, pGrandTotal_9 + pGrandTotal_18, pExcel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                          // member total 18
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><b>" + buildDisplayValue(tot18rounds, 0, totalMemberRounds, pExcel) + "</b>");
        out.println("</font></td>");

        out.println("</tr><tr>");                          // member total 9
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><b>" + buildDisplayValue(tot9rounds, 0, totalMemberRounds, pExcel) + "</b>");
        out.println("</font></td>");

        out.println("</tr><tr>");                          // blank row for divider
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">&nbsp;");
        out.println("</font></td>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">&nbsp;");
        out.println("</font></td>");


        int tmp_index = 0;
        while ( tmp_index < mem_type.size() ) {

            out.println("<tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">"); // show member type name
            out.println("<b>" + mem_type.get(tmp_index) + ":&nbsp;&nbsp;</b>");
            out.println("</font></td>");

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">"); // show total rounds for this member type
            out.println(buildDisplayValue(mem_type9Total.get(tmp_index), mem_type18Total.get(tmp_index), totalMemberRounds, pExcel));
            out.println("</font></td>");

            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">18 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">"); 
            out.println(buildDisplayValue(mem_type18Total.get(tmp_index), 0, totalMemberRounds, pExcel));
            out.println("</font></td>");

            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">9 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println(buildDisplayValue(mem_type9Total.get(tmp_index), 0, totalMemberRounds, pExcel));
            out.println("</font></td>");
            
            tmp_index++;

        } // end while loop of processed data
    } 
    catch(Exception exc) {

      //  displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        displayDatabaseErrMsg("Exception getting member type data.", exc.toString() , out);
        return;
            
    }

    // end of by member type
            
 }
 
 
 private void displayMemberShipTypeQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, int pGrandTotal_18, int pGrandTotal_9, String pExcel, Connection con, PrintWriter out) {

    
    if (g_debug) {
        out.println("<!-- STARTING displayMemberShipTypeQuery -->");
        out.println("<!-- pCourseName=" + pCourseName + " -->");
        out.println("<!-- pGrandTotal_18=" + pGrandTotal_18 + " -->");
        out.println("<!-- pGrandTotal_9=" + pGrandTotal_9 + " -->");
    }

    
    ResultSet rs = null;
    //String tableName = (pReportType == 11) ? "teecurr2" : "teepast2";
    String sqlQuery;

    int tot9rounds = 0;
    int tot18rounds = 0;

    //int [] memship_type9 = new int [40];       // allow for 24 plus old/unknown types
    //int [] memship_type18 = new int [40];
    //int [] mem_type9 = new int [g_MAX_ARRAY_SIZE];
    //int [] mem_type18 = new int [g_MAX_ARRAY_SIZE];
    //String [] memship_type = new String [g_MAX_ARRAY_SIZE];
    //String [] mem_type = new String [g_MAX_ARRAY_SIZE];

    
    ArrayList<Integer> memship_type9 = new ArrayList<Integer>(24);
    ArrayList<Integer> memship_type18 = new ArrayList<Integer>(24);
    ArrayList<Integer> mem_type9 = new ArrayList<Integer>(24);
    ArrayList<Integer> mem_type18 = new ArrayList<Integer>(24);
    ArrayList<String> memship_type = new ArrayList<String>(24);
    ArrayList<String> mem_type = new ArrayList<String>(24);
    
    int totalMemberRounds = 0;
    int rs_rows = 0;
    int rs_rows1 = 0;

    if (pReportType == 11) {

        // build sql statement
        sqlQuery = "" +
        "SELECT IFNULL(m_ship,IF(null_user=1,'Guest','Removed')) AS m_ship, IFNULL(m_type, IF(null_user=1,'Unknown','Removed from Member Database')) AS m_type, SUM(p_9), SUM(p_18) FROM ( " +
        "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, " +
                "m_type, m_ship, " +
                "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, " +
                "m_type, m_ship, " +
                "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, " +
                "m_type, m_ship, " +
                "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, " +
                "m_type, m_ship, " +
                "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, " +
                "m_type, m_ship, " +
                "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT (0) AS p_9, (0) AS p_18, " +
                "m2b.m_type AS m_type, " +
                "m5.mship AS m_ship, " +
                "(1) AS null_user " +
                "FROM member2b m2b, mship5 m5 " + 
                "WHERE m2b.m_type IS NOT NULL " + 
                "GROUP BY m5.mship, m2b.m_type " + 
        ") AS d_table GROUP BY m_ship, m_type WITH ROLLUP;";

 } else {
  
        // build sql statement
        sqlQuery = "" + 
        "SELECT IF(m_ship='', IF(null_user=1,'Guest','Removed'), m_ship) AS m_ship, IF(m_type='', IF(null_user=1,'Unknown','Removed from Member Database'), m_type) AS m_type, SUM(p_9), SUM(p_18) FROM ( " + 
        "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, " + 
                "mtype1 AS m_type, mship1 AS m_ship, " + 
                "IF(IFNULL(username1,'')='',1,0) AS null_user " + 
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show1 = 1 AND (username1 <> '' AND username1 IS NOT NULL) " + 
        "UNION ALL " + 
        "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, " + 
                "mtype2 AS m_type, mship2 AS m_ship, " + 
                "IF(IFNULL(username2,'')='',1,0) AS null_user " + 
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show2 = 1 AND (username2 <> '' AND username2 IS NOT NULL) " + 
        "UNION ALL " + 
        "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, " + 
                "mtype3 AS m_type, mship3 AS m_ship, " + 
                "IF(IFNULL(username3,'')='',1,0) AS null_user " + 
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show3 = 1 AND (username3 <> '' AND username3 IS NOT NULL) " + 
        "UNION ALL " + 
        "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, " +
                "mtype4 AS m_type, mship4 AS m_ship, " + 
                "IF(IFNULL(username4,'')='',1,0) AS null_user " + 
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show4 = 1 AND (username4 <> '' AND username4 IS NOT NULL) " + 
        "UNION ALL " + 
        "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, " + 
                "mtype5 AS m_type, mship5 AS m_ship, " + 
                "IF(IFNULL(username5,'')='',1,0) AS null_user " + 
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show5 = 1 AND (username5 <> '' AND username5 IS NOT NULL) " + 
        "UNION ALL " + 
        "SELECT (0) AS p_9, (0) AS p_18, " + 
                "m2b.m_type AS m_type, " + 
                "m5.mship AS m_ship, " + 
                "(1) AS null_user " + 
                "FROM member2b m2b, mship5 m5 " + 
                "WHERE m2b.m_type IS NOT NULL " + 
                "GROUP BY m5.mship, m2b.m_type " + 
        ") AS d_table GROUP BY m_ship, m_type WITH ROLLUP;";
     
 }


    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
    
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
                    memship_type9.add(rs.getInt(3));
                    memship_type18.add(rs.getInt(4));
                    //memship_type9[rs_rows] = rs.getInt(3);
                    //memship_type18[rs_rows] = rs.getInt(4);

                    rs_rows++;
                    
                } else {

                    // member totals
                    memship_type.add(rs.getString(1));
                    mem_type.add(rs.getString(2));
                    mem_type9.add(rs.getInt(3));
                    mem_type18.add(rs.getInt(4));
                    //memship_type[rs_rows1] = rs.getString(1);
                    //mem_type[rs_rows1] = rs.getString(2);
                    //mem_type9[rs_rows1] = rs.getInt(3);
                    //mem_type18[rs_rows1] = rs.getInt(4);

                    rs_rows1++;

                }

            }

        }

        pstmt1.close();
        
    } catch (Exception exc) {
           // displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
            displayDatabaseErrMsg("Exception getting membership data.", exc.toString() , out);
    }
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"2\">&nbsp;</td>");

    out.println("</tr><tr>");                     // Rounds by Member Type
    out.println("<td align=\"left\" nowrap>");
    out.println("<font size=\"2\">");
    out.println("<u><b>Rounds By Membership / Member Type</b></u>");
    out.println("</font></td><td></td></tr>");

    int tmp_index = 0;
    int tmp_index1 = 0;
    String tmp_memship_type = "";
    
    while ( tmp_index < memship_type.size() ) { //rs_rows1

            //out.println("<!-- tmp_index=" + tmp_index + " -->");

        if ( !memship_type.get(tmp_index).equalsIgnoreCase(tmp_memship_type) ) {
        //if ( !memship_type[tmp_index].equals(tmp_memship_type) ) {
            
            tmp_memship_type = memship_type.get(tmp_index);
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td></tr>");
            
            out.println("<tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("<b><u>Membership Type " + memship_type.get(tmp_index) + " Total:</u></b>");
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\"><b>"); //<!-- tmp_index1=" + tmp_index1 + " -->
            try { out.println(buildDisplayValue(memship_type9.get(tmp_index1) + memship_type18.get(tmp_index1), 0, totalMemberRounds, pExcel));
            } catch (Exception exc) { out.println("Error=" + exc.toString()); }
            out.println("</b></font></td>");
            
            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">18 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println(buildDisplayValue(memship_type18.get(tmp_index1), 0, totalMemberRounds, pExcel));
            out.println("</font></td>");
            out.println("</tr><tr>");
            out.println("<td align=\"right\">");
            out.println("<font size=\"2\">9 Hole Rounds:");
            out.println("</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">");
            out.println(buildDisplayValue(memship_type9.get(tmp_index1), 0, totalMemberRounds, pExcel));
            out.println("</font></td>");
            
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td colspan=\"2\">&nbsp;</td>");
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td align=\"right\"><font size=\"2\"><b>" + memship_type.get(tmp_index) + " Breakdown</b></font></td><td></td></tr>");
            out.println("</tr><tr>");                          // blank row for divider
            out.println("<td></td><td></td></tr>");
    
            tmp_index1++;
        
        }
        
        out.println("<tr>");
        out.println("<td align=\"right\" nowrap>");
        out.println("<font size=\"2\">");
        //out.println("<b>" + memship_type[tmp_index] + " &nbsp;/&nbsp; " + mem_type[tmp_index] + ":</b>");
        out.println("<b>" + mem_type.get(tmp_index) + ":</b>");
        out.println("</font></td>");

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println(buildDisplayValue(mem_type9.get(tmp_index) + mem_type18.get(tmp_index), 0, totalMemberRounds, pExcel));
        out.println("</font></td>");

        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(mem_type18.get(tmp_index), 0, totalMemberRounds, pExcel));
        out.println("</font></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(mem_type9.get(tmp_index), 0, totalMemberRounds, pExcel));
        out.println("</font></td>");

        tmp_index++;

    } // end while loop of processed data
    
    // end of by membership/member type
    
 }
 
 
 private void displayGuestTypeQuery(String pCourseName, long pStartDate, long pEndDate, int pReportType, int pGrandTotal_18, int pGrandTotal_9, String pExcel, Connection con, PrintWriter out) {


    if (g_debug) {
        out.println("<!-- STARTING displayGuestTypeQuery -->");
        out.println("<!-- pCourseName=" + pCourseName + " -->");
        out.println("<!-- pGrandTotal_18=" + pGrandTotal_18 + " -->");
        out.println("<!-- pGrandTotal_9=" + pGrandTotal_9 + " -->");
    }


    //String tableName = "teepast2";
    //if (pReportType == 11) tableName = "teecurr2";
    String sqlQuery;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs1 = null;
    //int rs_rows1 = 0;
    int tot9rounds = 0;
    int tot18rounds = 0;
    int totalGuestRounds = 0;
    
    int grand_totalGuestRounds = 0;
    int grand_tot9rounds = 0;
    int grand_tot18rounds = 0;
    int rev_totalGuestRounds = 0;
    int rev_tot9rounds = 0;
    int rev_tot18rounds = 0;
    
    int tmp_index = 0;
    
    ArrayList<Integer> gst_type9 = new ArrayList<Integer>(24);
    ArrayList<Integer> gst_type18 = new ArrayList<Integer>(24);
    ArrayList<String> gst_type = new ArrayList<String>(24);
    
    //int [] gst_type9 = new int [g_MAX_ARRAY_SIZE];
    //int [] gst_type18 = new int [g_MAX_ARRAY_SIZE];
    //String [] gst_type = new String [g_MAX_ARRAY_SIZE];
    
    boolean mix_revenue = false;

    //
    // NOTE:  The problem with this method of determining if we are going to do a breakdown by revenue/non-revenue is
    //        what happens if the club HAD a revenue type guest configured but then changed it back to non-rev, then
    //        that would cause this report to not do the breakdown when it probably should
    //        A better method for auto detecting would be to first look for any revenue guests that played during the search window....  ugly?
    //
    try {
        
      int revenue = 0;
      int non_revenue = 0;
      stmt = con.createStatement();
      rs = stmt.executeQuery("" +
                "SELECT * FROM (" +
                    "SELECT COUNT(*) AS r0 FROM guest5 WHERE revenue = 0 " +
                    "UNION ALL " +
                    "SELECT COUNT(*) AS r1 FROM guest5 WHERE revenue = 1 " +
                ") AS t" );
      
      if ( rs.next() ) revenue = rs.getInt(1);
      if ( rs.next() ) non_revenue = rs.getInt(1);
      
      if (revenue == 0 || non_revenue == 0) {
          // if either was zero then they can't be mixed
          mix_revenue = false;
      } else { // if (revenue > 0 && non-revenue > 0) 
          // one of them was NOT zero so they must be mixed
          mix_revenue = true;
      }
      
    } catch (Exception exc) {
        
       // displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        displayDatabaseErrMsg("Exception getting revenue counts.", exc.toString() , out);
        
    } finally {
        
        if (rs != null) {

            try { rs.close(); }
            catch (Exception e) {}
            rs = null;
        }

        if (stmt != null) {

            try { stmt.close(); } 
            catch (Exception e) {}
            stmt = null;
        }
        
    }

    boolean tmp_loop = true;
    String tmp_rev = "";
    int i = 1;
    
  while ( tmp_loop ) {
        
    if (!mix_revenue) {

        // only go thru this once - do not loop
        tmp_loop = false;
        tmp_rev = ""; 
        
    } else if ( i == 1 ) {
        
        tmp_rev = "revenue = 1 ";
        
    } else if ( i == 0 ) {
        
        tmp_rev = "";
        
    }

    if (pReportType == 11)  {

        // build sql statement for today
        sqlQuery = "" +
        "SELECT IF(null_user=1,'Guest','Removed') AS m_ship, IFNULL(guest_type,IF(null_user=1,'Unknown','Removed from Member Database')) AS m_or_g_type, SUM(p_9), SUM(p_18), revenue FROM ( " +
        "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, guest AS guest_type, revenue, " +
                "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player1,LENGTH(guest)) " + ((!tmp_rev.equals("")) ? "AND " + tmp_rev : "");
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show1 = 1 AND ((username1 = '' OR username1 IS NULL) AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, guest AS guest_type, revenue, " +
                "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username2 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player2,LENGTH(guest)) " + ((!tmp_rev.equals("")) ? "AND " + tmp_rev : "");
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show2 = 1 AND ((username2 = '' OR username2 IS NULL) AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, guest AS guest_type, revenue, " +
                "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username3 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player3,LENGTH(guest)) " + ((!tmp_rev.equals("")) ? "AND " + tmp_rev : "");
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show3 = 1 AND ((username3 = '' OR username3 IS NULL) AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, guest AS guest_type, revenue, " +
                "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username4 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player4,LENGTH(guest)) " + ((!tmp_rev.equals("")) ? "AND " + tmp_rev : "");
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show4 = 1 AND ((username4 = '' OR username4 IS NULL) AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, guest AS guest_type, revenue, " +
                "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username5 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player5,LENGTH(guest)) " + ((!tmp_rev.equals("")) ? "AND " + tmp_rev : "");
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show5 = 1 AND ((username5 = '' OR username5 IS NULL) AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT (0) AS p_9, (0) AS p_18, guest AS guest_type, revenue, (1) AS null_user FROM guest5) AS d_table ";
        if (mix_revenue) sqlQuery += "WHERE " + ((!tmp_rev.equals("")) ? tmp_rev : "revenue = 0 OR revenue IS NULL") + " ";
        sqlQuery += "GROUP BY m_ship, m_or_g_type WITH ROLLUP;";

    } else {

        // build sql statement for past rounds
        sqlQuery = "" +
        "SELECT IF(null_user=1,'Guest','Removed') AS m_ship, IF(guest_type='',IF(null_user=1,'Unknown','Removed from Member Database'), guest_type) AS m_or_g_type, SUM(p_9), SUM(p_18), revenue FROM ( " +
        "SELECT p91 AS p_9, IF(p91=0,1,0) AS p_18, gtype1 AS guest_type, grev1 AS revenue, " +
                "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show1 = 1 AND " + ((!tmp_rev.equals("")) ? "grev1 = 1" : "grev1 = 0") + " AND ((username1 = '' OR username1 IS NULL) AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p92 AS p_9, IF(p92=0,1,0) AS p_18, gtype2 AS guest_type, grev2 AS revenue, " +
                "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show2 = 1 AND " + ((!tmp_rev.equals("")) ? "grev2 = 1" : "grev2 = 0") + " AND ((username2 = '' OR username2 IS NULL) AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p93 AS p_9, IF(p93=0,1,0) AS p_18, gtype3 AS guest_type, grev3 AS revenue, " +
                "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show3 = 1 AND " + ((!tmp_rev.equals("")) ? "grev3 = 1" : "grev3 = 0") + " AND ((username3 = '' OR username3 IS NULL) AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p94 AS p_9, IF(p94=0,1,0) AS p_18, gtype4 AS guest_type, grev4 AS revenue, " +
                "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show4 = 1 AND " + ((!tmp_rev.equals("")) ? "grev4 = 1" : "grev4 = 0") + " AND ((username4 = '' OR username4 IS NULL) AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT p95 AS p_9, IF(p95=0,1,0) AS p_18, gtype5 AS guest_type, grev5 AS revenue, " +
                "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(pReportType, pCourseName);
                sqlQuery += "AND show5 = 1 AND " + ((!tmp_rev.equals("")) ? "grev5 = 1" : "grev5 = 0") + " AND ((username5 = '' OR username5 IS NULL) AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL)) " +
        "UNION ALL " +
        "SELECT (0) AS p_9, (0) AS p_18, guest AS guest_type, revenue, (1) AS null_user FROM guest5) AS d_table ";
        if (mix_revenue) sqlQuery += "WHERE " + ((!tmp_rev.equals("")) ? tmp_rev : "revenue = 0 OR revenue IS NULL") + " ";
        sqlQuery += "GROUP BY m_ship, m_or_g_type WITH ROLLUP;";

    }

/*
    sqlQuery = "" + 
    "SELECT IFNULL(m_ship,IF(null_user=1,'Guest','Removed')) AS m_ship, IFNULL(m_type, IFNULL(guest_type,IF(null_user=1,'Unknown','Removed from Member Database'))) AS m_or_g_type, SUM(p_9), SUM(p_18), revenue FROM ( " + 
    "SELECT p1cw AS cw, " + 
            "p91 AS p_9, IF(p91=0,1,0) AS p_18, " + 
            "m_type, m_ship, guest AS guest_type, revenue, " + 
            "IF(IFNULL(username1,'')='',1,0) AS null_user " + 
            "FROM " + tableName + " " +
            "LEFT OUTER JOIN member2b ON username1 = username " + 
            "LEFT OUTER JOIN guest5 ON guest = LEFT(player1,LENGTH(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName) + "AND " + tmp_rev;
            sqlQuery += "AND show1 = 1 AND ((username1 = '' OR username1 IS NULL) AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL)) " + 
    "UNION ALL " + 
    "SELECT p2cw AS cw, " + 
            "p92 AS p_9, IF(p92=0,1,0) AS p_18, " + 
            "m_type, m_ship, guest AS guest_type, revenue, " + 
            "IF(IFNULL(username2,'')='',1,0) AS null_user " + 
            "FROM " + tableName + " " +
            "LEFT OUTER JOIN member2b ON username2 = username " + 
            "LEFT OUTER JOIN guest5 ON guest = LEFT(player2,LENGTH(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName) + "AND " + tmp_rev;
            sqlQuery += "AND show2 = 1 AND ((username2 = '' OR username2 IS NULL) AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL)) " + 
    "UNION ALL " + 
    "SELECT p3cw AS cw, " + 
            "p93 AS p_9, IF(p93=0,1,0) AS p_18, " + 
            "m_type, m_ship, guest AS guest_type, revenue, " + 
            "IF(IFNULL(username3,'')='',1,0) AS null_user " + 
            "FROM " + tableName + " " +
            "LEFT OUTER JOIN member2b ON username3 = username " + 
            "LEFT OUTER JOIN guest5 ON guest = LEFT(player3,LENGTH(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName) + "AND " + tmp_rev;
            sqlQuery += "AND show3 = 1 AND ((username3 = '' OR username3 IS NULL) AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL)) " + 
    "UNION ALL " + 
    "SELECT p4cw AS cw, " + 
            "p94 AS p_9, IF(p94=0,1,0) AS p_18, " + 
            "m_type, m_ship, guest AS guest_type, revenue, " + 
            "IF(IFNULL(username4,'')='',1,0) AS null_user " + 
            "FROM " + tableName + " " +
            "LEFT OUTER JOIN member2b ON username4 = username " + 
            "LEFT OUTER JOIN guest5 ON guest = LEFT(player4,LENGTH(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName) + "AND " + tmp_rev;
            sqlQuery += "AND show4 = 1 AND ((username4 = '' OR username4 IS NULL) AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL)) " + 
    "UNION ALL " + 
    "SELECT p5cw AS cw, " + 
            "p95 AS p_9, IF(p95=0,1,0) AS p_18, " + 
            "m_type, m_ship, guest AS guest_type, revenue, " + 
            "IF(IFNULL(username5,'')='',1,0) AS null_user " + 
            "FROM " + tableName + " " +
            "LEFT OUTER JOIN member2b ON username5 = username " + 
            "LEFT OUTER JOIN guest5 ON guest = LEFT(player5,LENGTH(guest)) ";
            sqlQuery += buildWhereClause(pReportType, pCourseName) + "AND " + tmp_rev;
            sqlQuery += "AND show5 = 1 AND ((username5 = '' OR username5 IS NULL) AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL)) " + 
    "UNION ALL " + 
    "SELECT null AS cw, (0) AS p_9, (0) AS p_18, " + 
            "null AS m_type, null AS m_ship, guest AS guest_type, revenue, " + 
            "(1) AS null_user " + 
            "FROM guest5 " + 
    ") AS d_table WHERE " + tmp_rev + "GROUP BY m_ship, m_or_g_type WITH ROLLUP;";
*/
    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
    
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

        //rs_rows1 = 0;
        
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

                    gst_type.add(rs1.getString(2));
                    gst_type9.add(rs1.getInt(3));
                    gst_type18.add(rs1.getInt(4));
                    
                    //rs_rows1++;

                }

            }

        }

        pstmt1.close();
    
    } catch (Exception exc) {
       // displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        displayDatabaseErrMsg("Exception getting guest data.", exc.toString() , out);
    }
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"2\">&nbsp;</td>");

    out.println("</tr><tr>");                     // Total Rounds for Guests
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Guests" + ((mix_revenue) ? ((i==1) ? " (Revenue)" : " (Non-Revenue)") : "") +":</u></b></p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGuestRounds, 0, pGrandTotal_9 + pGrandTotal_18, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // guest total 18
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18rounds, 0, totalGuestRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // guest total 9
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9rounds, 0, totalGuestRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"2\">&nbsp;</td>");

    tmp_index = 0;
    //int gst9hole = 0;
    //int gst18hole = 0;
    //String guestType = "";

    while ( tmp_index < gst_type.size() ) {
        
        out.println("<tr>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<p align=\"right\"><b>" + gst_type.get(tmp_index) + ":</b></p>");
        out.println("</font></td>");
        
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println(buildDisplayValue(gst_type9.get(tmp_index) + gst_type18.get(tmp_index), 0, totalGuestRounds, pExcel));
        out.println("</font></td>");
        
        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(gst_type18.get(tmp_index), 0, totalGuestRounds, pExcel));
        out.println("</font></td>");
        out.println("</tr><tr>");
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(gst_type9.get(tmp_index), 0, totalGuestRounds, pExcel));
        out.println("</font></td>");
        
        tmp_index++;
    
    } // end while loop of processed data

    // if mixed revenue and this is the second loop then display the combined totals for guests
    if (i == 0) {
        
        grand_totalGuestRounds = totalGuestRounds + rev_totalGuestRounds;
        grand_tot9rounds = tot9rounds + rev_tot9rounds;
        grand_tot18rounds = tot18rounds + rev_tot18rounds;
        
        out.println("</tr><tr>");                           // blank row for divider
        out.println("<td colspan=\"2\">&nbsp;</td>");

        out.println("</tr><tr>");                           // Total Rounds for Guests
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<p align=\"left\"><b><u>Total Rounds by Guests:</u></b></p>");
        out.println("</font></td>");

        out.println("<td align=\"center\">");
        out.println("<font size=\"2\"><b>");
        out.println(buildDisplayValue(grand_totalGuestRounds, 0, pGrandTotal_9 + pGrandTotal_18, pExcel));
        out.println("</b></font></td>");

        out.println("</tr><tr>");                           // guest total 18
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">18 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(grand_tot18rounds, 0, grand_totalGuestRounds, pExcel));
        out.println("</font></td>");

        out.println("</tr><tr>");                           // guest total 9
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">9 Hole Rounds:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(grand_tot9rounds, 0, grand_totalGuestRounds, pExcel));
        out.println("</font></td>");
        
        out.println("</tr><tr>");                           // revenue total
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">Revenue:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(rev_totalGuestRounds, 0, grand_totalGuestRounds, pExcel));
        out.println("</font></td>");

        out.println("</tr><tr>");                           // non-revenue total
        out.println("<td align=\"right\">");
        out.println("<font size=\"2\">Non-Revenue:");
        out.println("</font></td>");
        out.println("<td align=\"center\"><font size=\"2\">");
        out.println(buildDisplayValue(grand_totalGuestRounds - rev_totalGuestRounds, 0, grand_totalGuestRounds, pExcel));
        out.println("</font></td>");
        
        tmp_loop = false; // break while loop
        
    } else if ( i == 1 ) {
        
        // first time through - save these values as revenue values
        rev_totalGuestRounds = totalGuestRounds;
        rev_tot9rounds = tot9rounds;
        rev_tot18rounds = tot18rounds;
        
        i = 0; // next pass is final

        // clear array lists
        gst_type.clear();
        gst_type9.clear();
        gst_type18.clear();

    }
    
  } // end revenue while loop
    
    // end of by guest type
            
 }
 
 //
 // Provides a summary of rounds played and shows breakdown of member and guests rounds
 // is called by menus when selecing Today report OR a custom date range
 //
 private void displayCourseSummary(String pCourseName, long pStartDate, long pEndDate, int pReportType, String pExcel, Connection con, PrintWriter out, boolean pVirtuals){

    if (g_debug) {
        out.println("<!-- STARTING displayCourseSummary -->");
        out.println("<!-- pCourseName=" + pCourseName + " -->");
        out.println("<!-- pReportType=" + pReportType + " -->");
        out.println("<!-- pVirtuals=" + pVirtuals + " -->");
    }

    String tableName = "teepast2";
    if (pReportType == 0 || pReportType == 1) tableName = "teecurr2";
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
    
    String whereClause = "";
    
    boolean ccwc = false; // flag for custom congressional where clause
    
    //if (pCourseName.equals("Open Course") || pCourseName.equals("Club Course") && pReportType != 1 && pReportType != 11 && pVirtuals) {
    if (pVirtuals && !pCourseName.equals("Hills Course")) {
        
        whereClause = buildCongressionalWhereClause(pReportType, pCourseName);
        ccwc = true;
    
    } else {
        
        whereClause = buildWhereClause(pReportType, pCourseName);
    
    }
    
    
    if (pReportType == 0 || pReportType == 1) {

        // build sql statement
        sqlQuery = "" +
        "SELECT IF(IFNULL(m_ship,IF(null_user=1,'Guest','Member'))='Guest','Rounds By Guests','Rounds By Members') AS m_ship, " +
                "SUM(p_9), SUM(p_18), SUM(p_9ns), SUM(p_18ns) FROM ( " +
        "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns, " +
                "p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18, " +
                "m_ship, guest AS guest_type, " +
                "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username1 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player1,LENGTH(guest)) ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns, " +
                "p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18, " +
                "m_ship, guest AS guest_type, " +
                "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username2 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player2,LENGTH(guest)) ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns, " +
                "p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18, " +
                "m_ship, guest AS guest_type, " +
                "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username3 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player3,LENGTH(guest)) ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns, " +
                "p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18, " +
                "m_ship, guest AS guest_type, " +
                "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username4 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player4,LENGTH(guest)) ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns, " +
                "p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18, " +
                "m_ship, guest AS guest_type, " +
                "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teecurr2 " +
                "LEFT OUTER JOIN member2b ON username5 = username " +
                "LEFT OUTER JOIN guest5 ON guest = LEFT(player5,LENGTH(guest)) ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) " +
        ") " +
        "AS d_table GROUP BY m_ship DESC;";

    } else {

        // build sql statement
        sqlQuery = "" +
        "SELECT IF(IF(m_ship='',IF(null_user=1,'Guest','Member'),m_ship)='Guest','Rounds By Guests','Rounds By Members') AS m_ship, " +
                "SUM(p_9), SUM(p_18), SUM(p_9ns), SUM(p_18ns) FROM ( " +
        "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns, " +
                "p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18, " +
                "mship1 AS m_ship, gtype1 AS guest_type, " +
                "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns, " +
                "p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18, " +
                "mship2 AS m_ship, gtype2 AS guest_type, " +
                "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns, " +
                "p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18, " +
                "mship3 AS m_ship, gtype3 AS guest_type, " +
                "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns, " +
                "p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18, " +
                "mship4 AS m_ship, gtype4 AS guest_type, " +
                "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns, " +
                "p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18, " +
                "mship5 AS m_ship, gtype5 AS guest_type, " +
                "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += whereClause;
                sqlQuery += "AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) " +
        ") " +
        "AS d_table GROUP BY m_ship DESC;";

    }

    if (g_debug) out.println("<!-- pStartDate=" + pStartDate + " | pEndDate=" + pEndDate + " | pCourseName=" + pCourseName + " -->");
    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
    
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
            
            if (ccwc) { 
                
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
                pstmt1.setLong(11, pStartDate);
                pstmt1.setLong(12, pEndDate);
                pstmt1.setLong(13, pStartDate);
                pstmt1.setLong(14, pEndDate);
                pstmt1.setLong(15, pStartDate);
                pstmt1.setLong(16, pEndDate);
                pstmt1.setLong(17, pStartDate);
                pstmt1.setLong(18, pEndDate);
                pstmt1.setLong(19, pStartDate);
                pstmt1.setLong(20, pEndDate);
                
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
        }
        
        rs1 = pstmt1.executeQuery();

        // NOTE:  This query may not return two rows, if there where NO member rounds found (1st row)
        // then the 1st row will contain the guest rows.
        if (rs1.next()) {

            if (rs1.getString(1).equalsIgnoreCase("Rounds By Members")) {
                
                tot9memrounds = rs1.getInt(2);
                tot18memrounds = rs1.getInt(3);
                totns9memrounds = rs1.getInt(4);
                totns18memrounds = rs1.getInt(5);

                totalMemRounds = tot9memrounds + tot18memrounds; // total member rounds
                if (tot9memrounds != 0) totalMemCRounds = tot18memrounds + (tot9memrounds / 2); // total member combinded rounds
            
            } else {
                
                tot9gstrounds = rs1.getInt(2);
                tot18gstrounds = rs1.getInt(3);
                totns9gstrounds = rs1.getInt(4);
                totns18gstrounds = rs1.getInt(5);
                
            }
            
        }
        
        if (rs1.next()) {
        
            tot9gstrounds = rs1.getInt(2);
            tot18gstrounds = rs1.getInt(3);
            totns9gstrounds = rs1.getInt(4);
            totns18gstrounds = rs1.getInt(5);
        
        }

        totalGstRounds = tot9gstrounds + tot18gstrounds; // total guest rounds
        if (tot9gstrounds != 0) totalGstCRounds = tot18gstrounds + (tot9gstrounds / 2); // total guest combinded rounds

        totalRounds = totalMemRounds + totalGstRounds;
        total9Rounds = tot9memrounds + tot9gstrounds;
        total18Rounds = tot18memrounds + tot18gstrounds;
        if (total9Rounds != 0) totalCRounds = total18Rounds + (total9Rounds / 2);

        totalNs9rounds = totns9memrounds + totns9gstrounds;
        totalNs18rounds = totns18memrounds + totns18gstrounds;
        totalNsRounds = totalNs9rounds + totalNs18rounds;
            
        pstmt1.close();
    
    } catch (Exception exc) {
       // displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        displayDatabaseErrMsg("Exception getting course summary data.", exc.toString() , out);
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
    out.println(buildDisplayValue(totalRounds, 0, 0, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds, 0, totalRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds, 0, totalRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds, 0, 0, pExcel));
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
    out.println(buildDisplayValue(totalMemRounds, 0, totalRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds, 0, totalMemRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds, 0, totalMemRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds, 0, 0, pExcel));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");
    
    
    // 
    // GUEST ROUNDS
    //
    out.println("</tr><tr>");                     // Total Guest Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Guests:</u></b> (non-members)</p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds, 0, totalRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds, 0, totalGstRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds, 0, totalGstRounds, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds, 0, 0, pExcel));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");


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
    out.println(buildDisplayValue(totns9memrounds + totns18memrounds, 0, totalMemRounds + totns9memrounds + totns18memrounds, pExcel)); // totalNsRounds
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total No-Show 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds, 0, totns9memrounds + totns18memrounds, pExcel)); // tot18gstrounds
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total No-Show 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns9memrounds, 0, totns9memrounds + totns18memrounds, pExcel)); // tot9gstrounds
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined No-Show Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds + (totns9memrounds/2), 0, 0, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"3\">&nbsp;</td>");
            
 } // end displayCourseSummary
 
 
 private void displayCourseSummaryBig(String pCourseName, String pExcel, Connection con, PrintWriter out){
    
    //String tableName = "teepast2";
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
    int totalNsMemRounds1 = 0;
    
    
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
    int totalNsMemRounds2 = 0;
    
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
    int totalNsMemRounds3 = 0;
    
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

    lmedate = lmsdate + 31;                      // create a Last Month End date
    
    //lm_start_date = year + "-" + month + "-01";
    //lm_end_date = year + "-" + month + "-30";
    
    
    // build sql statement to be used for LM, MTD, YTD queries
    sqlQuery = "" +
        "SELECT IF(IF(m_ship='',IF(null_user=1,'Guest','Member'),m_ship)='Guest','Rounds By Guests','Rounds By Members') AS m_ship, " +
                "SUM(p_9), SUM(p_18), SUM(p_9ns), SUM(p_18ns) FROM ( " +
        "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns, " +
                "p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18, " +
                "mship1 AS m_ship, gtype1 AS guest_type, " +
                "IF(IFNULL(username1,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(0, pCourseName);
                sqlQuery += "AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns, " +
                "p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18, " +
                "mship2 AS m_ship, gtype2 AS guest_type, " +
                "IF(IFNULL(username2,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(0, pCourseName);
                sqlQuery += "AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns, " +
                "p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18, " +
                "mship3 AS m_ship, gtype3 AS guest_type, " +
                "IF(IFNULL(username3,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(0, pCourseName);
                sqlQuery += "AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns, " +
                "p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18, " +
                "mship4 AS m_ship, gtype4 AS guest_type, " +
                "IF(IFNULL(username4,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(0, pCourseName);
                sqlQuery += "AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +
        "UNION ALL " +
        "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns, " +
                "p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18, " +
                "mship5 AS m_ship, gtype5 AS guest_type, " +
                "IF(IFNULL(username5,'')='',1,0) AS null_user " +
                "FROM teepast2 ";
                sqlQuery += buildWhereClause(0, pCourseName);
                sqlQuery += "AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) " +
        ") " +
        "AS d_table GROUP BY m_ship DESC;";

/*
    sqlQuery = "" + 
    "SELECT IF(m_ship='','Rounds By Guests','Rounds By Members') AS m_ship, " +
            "SUM(p_9), SUM(p_18), SUM(p_9ns), SUM(p_18ns) FROM ( " +
    "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns, " + 
            "p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18, " + 
            "mship1 AS m_ship, gtype1 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +
    "UNION ALL " + 
    "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns, " + 
            "p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18, " + 
            "mship2 AS m_ship, gtype2 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +
    "UNION ALL " + 
    "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns, " + 
            "p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18, " + 
            "mship3 AS m_ship, gtype3 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +
    "UNION ALL " + 
    "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns, " + 
            "p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18, " + 
            "mship4 AS m_ship, gtype4 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +
    "UNION ALL " + 
    "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns, " + 
            "p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18, " + 
            "mship5 AS m_ship, gtype5 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) " +
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";
*/

    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
    
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

        if (rs1.next()) {

            tot9memrounds1 = rs1.getInt(2);
            tot18memrounds1 = rs1.getInt(3);
            totns9memrounds1 = rs1.getInt(4);
            totns18memrounds1 = rs1.getInt(5);
        
        }
        
        totalMemRounds1 = tot9memrounds1 + tot18memrounds1; // total member rounds
        if (tot9memrounds1 != 0) totalMemCRounds1 = tot18memrounds1 + (tot9memrounds1 / 2); // total member combinded rounds
        
        if (rs1.next()) {
        
            tot9gstrounds1 = rs1.getInt(2);
            tot18gstrounds1 = rs1.getInt(3);
            totns9gstrounds1 = rs1.getInt(4);
            totns18gstrounds1 = rs1.getInt(5);
        
        }
            
        totalGstRounds1 = tot9gstrounds1 + tot18gstrounds1; // total guest rounds
        if (tot9gstrounds1 != 0) totalGstCRounds1 = tot18gstrounds1 + (tot9gstrounds1 / 2); // total guest combinded rounds

        totalRounds1 = totalMemRounds1 + totalGstRounds1;
        total9Rounds1 = tot9memrounds1 + tot9gstrounds1;
        total18Rounds1 = tot18memrounds1 + tot18gstrounds1;
        if (total9Rounds1 != 0) totalCRounds1 = total18Rounds1 + (total9Rounds1 / 2);

        totalNs9rounds1 = totns9memrounds1 + totns9gstrounds1;
        totalNs18rounds1 = totns18memrounds1 + totns18gstrounds1;
        totalNsRounds1 = totalNs9rounds1 + totalNs18rounds1;
        totalNsMemRounds1 = totns9memrounds1 + totns18memrounds1;

        pstmt1.close();
    
    } catch (Exception exc) {
       // displayDatabaseErrMsg(exc.getMessage(), exc.toString(), out);
        displayDatabaseErrMsg("Exception getting last month summary data.", exc.toString(), out);
    }
    
    // END OF LAST MONTH
    
    
/*
    // build sql statement for MONTH TO DATE (mtddate & edate)
    sqlQuery = "" +
    "SELECT IF(m_ship='','Rounds By Guests','Rounds By Members') AS m_ship, " +
            "SUM(p_9), SUM(p_18), SUM(p_9ns), SUM(p_18ns) FROM ( " + 
    "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns, " + 
            "p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18, " + 
            "mship1 AS m_ship, gtype1 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns, " + 
            "p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18, " + 
            "mship2 AS m_ship, gtype2 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns, " + 
            "p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18, " + 
            "mship3 AS m_ship, gtype3 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns, " + 
            "p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18, " + 
            "mship4 AS m_ship, gtype4 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns, " + 
            "p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18, " + 
            "mship5 AS m_ship, gtype5 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) " +  
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";

    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
*/
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

        if (rs1.next()) {

            tot9memrounds2 = rs1.getInt(2);
            tot18memrounds2 = rs1.getInt(3);
            totns9memrounds2 = rs1.getInt(4);
            totns18memrounds2 = rs1.getInt(5);
        
        }
        
        totalMemRounds2 = tot9memrounds2 + tot18memrounds2; // total member rounds
        if (tot9memrounds2 != 0) totalMemCRounds2 = tot18memrounds2 + (tot9memrounds2 / 2); // total member combinded rounds
        
        if (rs1.next()) {
        
            tot9gstrounds2 = rs1.getInt(2);
            tot18gstrounds2 = rs1.getInt(3);
            totns9gstrounds2 = rs1.getInt(4);
            totns18gstrounds2 = rs1.getInt(5);
        
        }
        
        totalGstRounds2 = tot9gstrounds2 + tot18gstrounds2; // total guest rounds
        if (tot9gstrounds2 != 0) totalGstCRounds2 = tot18gstrounds2 + (tot9gstrounds2 / 2); // total guest combinded rounds

        totalRounds2 = totalMemRounds2 + totalGstRounds2;
        total9Rounds2 = tot9memrounds2 + tot9gstrounds2;
        total18Rounds2 = tot18memrounds2 + tot18gstrounds2;
        if (total9Rounds2 != 0) totalCRounds2 = total18Rounds2 + (total9Rounds2 / 2);

        totalNs9rounds2 = totns9memrounds2 + totns9gstrounds2;
        totalNs18rounds2 = totns18memrounds2 + totns18gstrounds2;
        totalNsRounds2 = totalNs9rounds2 + totalNs18rounds2;
        totalNsMemRounds2 = totns9memrounds2 + totns18memrounds2;
        
        pstmt1.close();
    
    } catch (Exception exc) {
      //  displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        displayDatabaseErrMsg("Exception getting month to date summary data.", exc.toString() , out);
    }
    
    // END OF MONTH TO DATE
    
    
    
    
/*
    // build sql statement for YEAR TO DATE (ytddate & edate)
    sqlQuery = "" +
    "SELECT IF(m_ship='','Rounds By Guests','Rounds By Members') AS m_ship, " +
            "SUM(p_9), SUM(p_18), SUM(p_9ns), SUM(p_18ns) FROM ( " + 
    "SELECT p91*(show1<>1) AS p_9ns, IF(p91=0,1,0)*(show1<>1) AS p_18ns, " + 
            "p91*(show1=1) AS p_9, IF(p91=0,1,0)*(show1=1) AS p_18, " + 
            "mship1 AS m_ship, gtype1 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player1 <> '' AND player1 <> 'x' AND player1 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p92*(show2<>1) AS p_9ns, IF(p92=0,1,0)*(show2<>1) AS p_18ns, " + 
            "p92*(show2=1) AS p_9, IF(p92=0,1,0)*(show2=1) AS p_18, " + 
            "mship2 AS m_ship, gtype2 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player2 <> '' AND player2 <> 'x' AND player2 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p93*(show3<>1) AS p_9ns, IF(p93=0,1,0)*(show3<>1) AS p_18ns, " + 
            "p93*(show3=1) AS p_9, IF(p93=0,1,0)*(show3=1) AS p_18, " + 
            "mship3 AS m_ship, gtype3 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player3 <> '' AND player3 <> 'x' AND player3 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p94*(show4<>1) AS p_9ns, IF(p94=0,1,0)*(show4<>1) AS p_18ns, " + 
            "p94*(show4=1) AS p_9, IF(p94=0,1,0)*(show4=1) AS p_18, " + 
            "mship4 AS m_ship, gtype4 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player4 <> '' AND player4 <> 'x' AND player4 IS NOT NULL) " +  
    "UNION ALL " + 
    "SELECT p95*(show5<>1) AS p_9ns, IF(p95=0,1,0)*(show5<>1) AS p_18ns, " + 
            "p95*(show5=1) AS p_9, IF(p95=0,1,0)*(show5=1) AS p_18, " + 
            "mship5 AS m_ship, gtype5 AS guest_type " +
            "FROM teepast2 ";
            sqlQuery += buildWhereClause(0, pCourseName);
            sqlQuery += "AND (player5 <> '' AND player5 <> 'x' AND player5 IS NOT NULL) " +  
    ") " + 
    "AS d_table GROUP BY m_ship DESC;";

    if (g_debug) out.println("<!-- " + sqlQuery + " -->");
*/
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

        if (rs1.next()) {

            tot9memrounds3 = rs1.getInt(2);
            tot18memrounds3 = rs1.getInt(3);
            totns9memrounds3 = rs1.getInt(4);
            totns18memrounds3 = rs1.getInt(5);

        }

        totalMemRounds3 = tot9memrounds3 + tot18memrounds3; // total member rounds
        if (tot9memrounds3 != 0) totalMemCRounds3 = tot18memrounds3 + (tot9memrounds3 / 2); // total member combinded rounds
        
        if (rs1.next()) {
        
            tot9gstrounds3 = rs1.getInt(2);
            tot18gstrounds3 = rs1.getInt(3);
            totns9gstrounds3 = rs1.getInt(4);
            totns18gstrounds3 = rs1.getInt(5);
        
        }
        
        totalGstRounds3 = tot9gstrounds3 + tot18gstrounds3; // total guest rounds
        if (tot9gstrounds3 != 0) totalGstCRounds3 = tot18gstrounds3 + (tot9gstrounds3 / 2); // total guest combinded rounds

        totalRounds3 = totalMemRounds3 + totalGstRounds3;
        total9Rounds3 = tot9memrounds3 + tot9gstrounds3;
        total18Rounds3 = tot18memrounds3 + tot18gstrounds3;
        if (total9Rounds3 != 0) totalCRounds3 = total18Rounds3 + (total9Rounds3 / 2);

        totalNs9rounds3 = totns9memrounds3 + totns9gstrounds3;
        totalNs18rounds3 = totns18memrounds3 + totns18gstrounds3;
        totalNsRounds3 = totalNs9rounds3 + totalNs18rounds3;
        totalNsMemRounds3 = totns9memrounds3 + totns18memrounds3;
        
        pstmt1.close();
    
    } catch (Exception exc) {
       // displayDatabaseErrMsg(exc.getMessage(), exc.toString() , out);
        displayDatabaseErrMsg("Exception getting end of year summary data.", exc.toString() , out);
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
    out.println(buildDisplayValue(totalRounds1, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalRounds2, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalRounds3, 0, 0, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds1, 0, totalRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds2, 0, totalRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total18Rounds3, 0, totalRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds1, 0, totalRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds2, 0, totalRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(total9Rounds3, 0, totalRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds1, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds2, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalCRounds3, 0, 0, pExcel));
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
    out.println(buildDisplayValue(totalMemRounds1, 0, totalRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemRounds2, 0, totalRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemRounds3, 0, totalRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds1, 0, totalMemRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds2, 0, totalMemRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18memrounds3, 0, totalMemRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds1, 0, totalMemRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds2, 0, totalMemRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9memrounds3, 0, totalMemRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds1, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds2, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalMemCRounds3, 0, 0, pExcel));
    out.println("</b></font></td>");
    
    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"7\">&nbsp;</td>");
    
    
    // 
    // GUEST ROUNDS
    //
    out.println("</tr><tr>");                     // Total Guest Rounds Played
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">");
    out.println("<p align=\"left\"><b><u>Rounds by Guests:</u></b> (non-members)</p>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds1, 0, totalRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds2, 0, totalRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstRounds3, 0, totalRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds1, 0, totalGstRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds2, 0, totalGstRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot18gstrounds3, 0, totalGstRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds1, 0, totalGstRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds2, 0, totalGstRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(tot9gstrounds3, 0, totalGstRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds1, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds2, 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totalGstCRounds3, 0, 0, pExcel));
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
    out.println(buildDisplayValue(totalNsMemRounds1, 0, totalMemRounds1 + totalNsMemRounds1, pExcel)); // totalNsRounds1
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalNsMemRounds2, 0, totalMemRounds2 + totalNsMemRounds2, pExcel)); // totalNsRounds2
    out.println("</b></font></td>");
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\"><b>");
    out.println(buildDisplayValue(totalNsMemRounds3, 0, totalMemRounds3 + totalNsMemRounds3, pExcel)); // totalNsRounds3
    out.println("</b></font></td>");


    out.println("</tr><tr>");                          // Total No-Show 18 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">18 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds1, 0, totalNsMemRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds2, 0, totalNsMemRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds3, 0, totalNsMemRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total No-Show 9 Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">9 Hole Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns9memrounds1, 0, totalNsMemRounds1, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns9memrounds2, 0, totalNsMemRounds2, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns9memrounds3, 0, totalNsMemRounds3, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // Total Combined No-Show Rounds
    out.println("<td align=\"center\">");
    out.println("<font size=\"2\">Combined Rounds:");
    out.println("</font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds1 + (totns9memrounds1 / 2), 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds2 + (totns9memrounds2 / 2), 0, 0, pExcel));
    out.println("</b></font></td>");
    out.println("<td align=\"center\"><font size=\"2\"><b>");
    out.println(buildDisplayValue(totns18memrounds3 + (totns9memrounds3 / 2), 0, 0, pExcel));
    out.println("</b></font></td>");

    out.println("</tr><tr>");                          // blank row for divider
    out.println("<td colspan=\"7\">&nbsp;</td>");
            
 } // end displayCourseSummaryBig
 
 
 //**************************************************
 // Common Method for Displaying Values 
 //**************************************************
 //
 private String buildDisplayValue(int pSubTotal_9, int pSubTotal_18, int pGrandTotal, String pExcel) {

    try {

    NumberFormat nf;
    nf = NumberFormat.getNumberInstance();
    
    if (pSubTotal_9 + pSubTotal_18 < 1 || pGrandTotal < 1) {
        return "" + nf.format(pSubTotal_9 + pSubTotal_18) + "</td><td>";
    } else {
        double tmp = ((pSubTotal_9 + pSubTotal_18) * 100) / pGrandTotal;
        if (pExcel.equals("")) {
            return nf.format(pSubTotal_9 + pSubTotal_18) + "</td><td><font size=\"2\">(" + ((tmp < 1) ? "<1" : nf.format(tmp)) + "%)";
        } else {
            return nf.format(pSubTotal_9 + pSubTotal_18) + "</td><td><font size=\"2\">" + ((tmp < 1) ? "<1" : nf.format(tmp)) + "%";
        }
    }

    } catch (Exception exc) {

        return "Err <!-- " + exc.toString() + " -->";

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
 // Common Method for Building WHERE Clause for Congressional
 //**************************************************
 //
 private String buildCongressionalWhereClause(int pReportType, String pCourseName) {

    String tmp = "";    
    
    if (pCourseName.equals("Open Course")) {
        
        tmp = "WHERE " +
              "((courseName = 'Open Course' AND date >= ? AND date <= ? AND date % 2 =  0) OR " +
              " (courseName = 'Club Course' AND date >= ? AND date <= ? AND date % 2 <> 0)) ";
        
    } else {
        
        tmp = "WHERE " +
              "((courseName = 'Club Course' AND date >= ? AND date <= ? AND date % 2 =  0) OR " +
              " (courseName = 'Open Course' AND date >= ? AND date <= ? AND date % 2 <> 0)) ";
    }
    
    return tmp;
 }


 //
 // Check the clubs teepast2 table and if there are entires from before a
 // certain date then return true otherwise return false
 //
 private boolean accessClassicReports(Connection con) {

    boolean result = false; // default to no

    Statement stmt = null;
    ResultSet rs = null;

    int date = 0;

    try {

      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT MIN(date) FROM teepast2");

      if ( rs.next() ) date = rs.getInt(1);

      result = date < 20090401; // let use 4-1-2009 since the updated moveTee code has only been in place about a week

      stmt.close();

    } catch (Exception exc) {

        // for now lets just let it default to not
        // displaying the classic reports button

    } finally {

        if (rs != null) {

            try { rs.close(); }
            catch (Exception e) {}
            rs = null;
        }

        if (stmt != null) {

            try { stmt.close(); }
            catch (Exception e) {}
            stmt = null;
        }

    }

    return result;

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
