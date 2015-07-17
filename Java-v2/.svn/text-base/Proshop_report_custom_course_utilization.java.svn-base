/***************************************************************************************
 *   Proshop_report_course_utilization: This servlet will ouput the custom course utilization reports
 *                                      (by hour of day and day of week)
 *
 *
 *
 *   Created:       11/01/10
 *
 *
 *   Last Updated:
 *
 *      10/15/12   Fixed issue with bolded times on the report results starting and ending one time too early.
 *      10/08/12   Fixed issue with incorrect times being included in the peak times counts.
 *       8/16/12   Updated report to not include event times in the counts.
 *       8/02/12   Removed some irrelevant info from being printed when in excel mode, as it was causing errors upon opening the file.
 *      12/02/10   Added a checkbox option to include raw data values alongside the percentages.
 *      11/23/10   Added 'Home' buttons to the Web Page results display page
 *      11/22/10   Monterey Peninsula CC (mpccpb) - Custom Walk In Availability report added (case 1883).
 *      11/22/10   Report created
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

public class Proshop_report_custom_course_utilization extends HttpServlet {
    
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

    boolean zeros = true;   // Since it's always on now, default to true, set to false here if desired for custom reports


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
    int temp_time = 0;
    int peak_total = 0;
    int peak_meets_criteria = 0;

    long edate = 0;
    long sdate = 0;
    long cur_date = 0;

    boolean incTime = false;
    boolean isPeakTime = false;
    boolean rawData = false;

    int [][][] utilData = new int [7][mpccpb_maxIntervals][2];   // 3-Dimensional Array to hold all gathered findings.  [day_of_week][tee_time][total/eligible]

    //
    //  Use the custom date range provided to generate the report
    //
    if (req.getParameter("excel") != null) {
        out.println("<html><head><title>Walk In Availability Report</title></head>");
    } else {
        out.println(SystemUtils.HeadTitle2("Walk In Availability"));
        SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }

    // check to see if the date is here, and if not then jump to display calendar routine
    if (start_date.equals("")) {
        getCustomDate(req, out, con);
        return;
    }

    // if no ending date then default to starting date for 1 day report
    if (end_date.equals("")) {
        end_date = start_date;
    }

    if (req.getParameter("rawData") != null) {
        rawData = true;
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

    // Build main query string.
    String query_main = "SELECT * FROM (" +
            "SELECT tp.time AS time1, IF((tp.player1='' AND tp.player2='' AND tp.player3='' AND tp.player4='' AND tp.player5='') OR tp.custom_int=1, 1, 0) AS eligible, tp.event as event " +
            "FROM teepast2 tp " +
            "WHERE tp.date = ? " +
            "UNION ALL " +
            "SELECT te.time AS time1, '1' AS eligible, te.event as event " +
            "FROM teepastempty te " +
            "WHERE te.date = ?" +
            ") AS tmp ORDER BY time1;";

    // Dates should be properly set now.  Generate Calendar object and set to the sdate.
    Calendar cal = new GregorianCalendar();
    cal.set(Calendar.YEAR,start_year);                    // set year in cal
    cal.set(Calendar.MONTH,start_month - 1);                     // set month in cal
    cal.set(Calendar.DAY_OF_MONTH,start_day);            // set day in cal

    cur_year = cal.get(Calendar.YEAR);
    cur_month = cal.get(Calendar.MONTH) + 1; // month is zero based
    cur_day = cal.get(Calendar.DAY_OF_MONTH);
    cur_day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;  // day_of_week is one-based, decremented to use as array index

    cur_date = (cur_year * 10000) + (cur_month * 100) + cur_day;                      // create a date field of yyyymmdd

    loop1:
    while (cur_date <= edate) {

        cur_time = mpccpb_firstTime;        // set cur_time to the first tee time of the day
        cur_hr = mpccpb_firstHr;
        cur_min = mpccpb_firstMin;

        // re-initialize indicies
        index = 1;
        index_time = 0;

        // Grab the tee times for the current date
        try {
            pstmt = con.prepareStatement(query_main);
            pstmt.clearParameters();
            pstmt.setLong(1, cur_date);
            pstmt.setLong(2, cur_date);

            rs = pstmt.executeQuery();

            // Loop over all possible tee time slots for this day
            loop2:
            while (cur_time <= mpccpb_lastTime && index_time < mpccpb_maxIntervals) {

                incTime = false;
                isPeakTime = false;

                if (cur_day_of_week >= 4 && cur_day_of_week <=6 && cur_time >= 800 && cur_time <= 1350) {
                    isPeakTime = true;
                }

                // Loop through times until we find one that matches a time in the result set
                try {
                    rs.absolute(index);
                    
                    temp_time = rs.getInt("time1");

                    // See if time matches the time we're currently looking at
                    if (temp_time == cur_time && rs.getString("event").equals("")) {    // time found!

                        // Increment result array to count this slot, if during peak time, increment the peak counter as well
                        utilData[cur_day_of_week][index_time][0]++;
                        if (isPeakTime) peak_total++;

                        // Loop through 5 time slots (this included) to determine if any meet critera
                        loop3:
                        for (int i=0; i<5; i++) {

                            temp_time = incrementTime(cur_hr, cur_min, mpccpb_interval, i);

                            // check all slots at this time
                            loop4:
                            while (rs.getInt("time1") == temp_time) {

                                if (rs.getInt("eligible") == 1 && rs.getString("event").equals("")) {

                                    // Eligible tee time slot found, increment meets criteria count for this slot and break out to continue with next time slot
                                    utilData[cur_day_of_week][index_time][1]++;
                                    if (isPeakTime) peak_meets_criteria++;

                                    break loop3;

                                } else {

                                    // This time was not eligible, move to next time
                                    try {
                                        rs.next();
                                    } catch (Exception exc3) {

                                        break loop3;
                                    }
                                }
                            }   // end loop4
                        }   // end loop3

                        incTime = true;
                        index++;

                    } else if (temp_time < cur_time) {      // cur_time is later than the time from rs, move rs index forward

                        index++;

                    } else {        // cur_time is earlier than the time pulled from rs

                        incTime = true;
                    }

                    if (incTime) {

                        // Increment cur_time and continue loop
                        cur_min += mpccpb_interval;

                        // If minutes go over 60, subtract 60 minutes from min value and add 1 to hr value.
                        if (cur_min >= 60) {
                            cur_hr++;
                            cur_min -= 60;
                        }

                        // If hour goes over 24, subtract 24 hrs from hr value
                        if (cur_hr >= 24) {
                            cur_hr -= 24;
                        }

                        cur_time = (cur_hr * 100) + cur_min;

                        index_time++;
                    }

                } catch (Exception exc2) {

                    break loop2;
                }
            }   // end loop2

            pstmt.close();
            
        } catch (Exception exc) {
            Utilities.logError("error encountered: " + exc.getMessage());
        }
        // Increment day by one
        cal.add(Calendar.DATE, 1);
        
        cur_year = cal.get(Calendar.YEAR);
        cur_month = cal.get(Calendar.MONTH) + 1; // month is zero based
        cur_day = cal.get(Calendar.DAY_OF_MONTH);
        cur_day_of_week = cal.get(Calendar.DAY_OF_WEEK) - 1;  // day_of_week is one-based, decremented to use as array index

        cur_date = (cur_year * 10000) + (cur_month * 100) + cur_day;                      // create a date field of yyyymmdd

    }  // end of loop1

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
    
    out.println("<tr><td align=\"center\" colspan=\"8\"><b>Peak Times: " + (peak_total == 0 ? "0" : ((peak_meets_criteria * 100) / peak_total)) + "% (" + peak_meets_criteria + "/" + peak_total + ")</b></td></tr>");

    out.println("<tr bgcolor=\"" +bgrndcolor+ "\">");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Time</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Sunday</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Monday</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Tuesday</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Wednesday</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Thursday</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Friday</td>");
    out.println("<td align=\"center\" style=\"color:" + fontcolor + ";\">Saturday</td>");
    out.println("</tr>");

    cur_hr = mpccpb_firstHr;
    cur_min = mpccpb_firstMin;
    cur_time = mpccpb_firstTime;
    isPeakTime = false;
    
    String cur_time_s = "7:00";
    String disp_hr = "";
    boolean pm = false;

    // Print out report results
    for (int i=0; i<67; i++) {

        out.println("<tr>");

        for (int j=0; j<8; j++) {

            if (j==0) {
                
                out.println("<td bgcolor=\"" + bgrndcolor + "\" align=\"center\" style=\"color:" + fontcolor + ";\">" + cur_time_s + " " + (!pm ? "am" : "pm") + "</td>");

                // Increment cur_time and continue loop
                cur_min += mpccpb_interval;

                // If minutes go over 60, subtract 60 minutes from min value and add 1 to hr value.
                if (cur_min >= 60) {
                    cur_hr++;
                    cur_min -= 60;
                }

                // If hour goes over 24, subtract 24 hrs from hr value
                if (cur_hr >= 24) {
                    cur_hr -= 24;
                }

                pm = false;

                if (cur_hr == 0) {
                    disp_hr = "12";
                } else if (cur_hr > 12) {
                    disp_hr = String.valueOf(cur_hr - 12);
                    pm = true;
                } else {
                    disp_hr = String.valueOf(cur_hr);
                }

                cur_time_s = disp_hr + ":" + (cur_min < 10 ? ("0" + cur_min ) : cur_min);
                cur_time = (cur_hr * 100) + cur_min;
                
            } else {

                if (j >= 5 && j <= 7 && cur_time >= 810 && cur_time <= 1400) { // start and stop 10 minutes late since we've already adjusted the time for the next time at this point, e.g. 810 actually corresponds to 800.
                    isPeakTime = true;
                } else {
                    isPeakTime = false;
                }
                
                out.println("<td align=\"center\">" + (isPeakTime ? "<b>" : "") + (utilData[j-1][i][0] == 0 ? "0" : ((utilData[j-1][i][1] * 100) / utilData[j-1][i][0])) + "%" + (rawData ? " (" + utilData[j-1][i][1] + "/" + utilData[j-1][i][0] + ")" : "") + (isPeakTime ? "</b>" : "") + "</td>");
            }
        }

        out.println("</tr>");
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

 } // end doPost


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
 

 //**************************************************
 // Used by the custom date range reports this routine
 // presents the user with date selection options
 // and posts the date back to request the report
 //**************************************************
 //
 private void getCustomDate(HttpServletRequest req, PrintWriter out, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;


    // our oldest date variables (how far back calendars go)
    long old_date = 0;
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;

    // lookup oldest date in teepast2 that has a pace status
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT MIN(date) FROM teepast2");

        if (rs.next()) {

            old_date = rs.getLong(1);
        }

    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Proshop_report_custom_course_utilization Error looking up oldest tee time.", e.getMessage(), out, false);
        return;
    }


   //
   //  Determine oldest date values - month, day, year
   //
   int oldest_date = (int)old_date;
   oldest_yy = oldest_date / 10000;
   oldest_mm = (oldest_date - (oldest_yy * 10000)) / 100;
   oldest_dd = oldest_date - ((oldest_yy * 10000) + (oldest_mm * 100));



    // set calendar vars
    Calendar cal_date = new GregorianCalendar();
    cal_date.add(Calendar.DATE,-1);                     // get yesterday's date (no teepast entries for today)
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
    out.println("<font size=\"3\"><b>Custom Course Utilization Report</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"Proshop_report_custom_course_utilization\" method=\"post\">");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <br><input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <br><input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");

    out.println("<p align=\"center\"><b>Run Report (select an output type below):</b>");

    // Include a checkbox for whether or not raw data should be included along with percentage values
    out.println("<br><br><input type=\"checkbox\" name=\"rawData\" value=\"1\">&nbsp;&nbsp;Include raw data?");

    // Print report format buttons
    out.println("<br><br><input type=\"submit\" name=\"webpage\" value=\" Web Page \" style=\"background:#8B8970\">&nbsp;&nbsp;<input type=\"submit\" name=\"excel\" value=\" Excel \" style=\"background:#8B8970\"></p>");

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
