/***************************************************************************************
 *   Proshop_activity_print_sheet:  This servlet will process the 'Print Sheet' request from
 *                                  the Proshop_gensheets.
 *
 *
 *   Called by:  Proshop_gensheets - Control Panel to print a report
 *
 *
 *   Created: 10/10/2009   Bob P.
 *
 *
 *   Last updated:
 *
 *      12/05/12  Updated summary view excel print option to include a border around table cells.
 *       9/28/11  Added summary view to print options and made various updates.  Previous summary option updated to use "Old Summary View" instead of Summary View.
 *       2/17/11  Converted all references to disallow_joins over to the new force_singles field.
 *       8/17/10  Updated print sheets to include pro ane mem names in lesson listings and rest name if no players in time
 *       8/05/10  Updated database queries to use the sort_by values in the activities table.
 *       7/28/10  Event, Lesson, and Restriction coloring/names will now be displayed on Activity sheet print options (case 1847).
 *      12/08/09  Removed parent_id references
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.lang.Math;
import java.text.*;

// foretees imports
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.ProcessConstants;
import com.foretees.common.verifySlot;
import com.foretees.common.Utilities;


public class Proshop_activity_print_sheet extends HttpServlet {


   static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   String [] dayShort_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


 //*****************************************************
 // Process the call from Proshop_gensheets - display the print options
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


   //
   //  If call from below (print option selected)
   //
   if (req.getParameter("prtoption") != null) {

      doPost(req, resp);   // go process in doPost
      return;
   }

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);

    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    //int sess_activity_id = (Integer)session.getAttribute("activity_id");

    String allowable_views = "";

    int group_id = 0;
    int month = 0;
    int day = 0;
    int year = 0;
    int date = 0;

    boolean allow_tabbed = false;
    boolean allow_summary = false;
    boolean allow_oldsummary = false;
    boolean allow_detail = false;


    //get the time sheet date
    String dateStr = req.getParameter("date");

    if (dateStr == null || dateStr.equals("")) {

      date = 0;

    } else {

       date = Integer.parseInt(dateStr);
    }

    String sid = req.getParameter("group_id");
    try {
        group_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    if (date > 0) {

        year = date / 10000;
        int temp = year * 10000;
        month = date - temp;
        temp = month / 100;
        temp = temp * 100;
        day = month - temp;
        month = month / 100;
    }

    //
    // SEE WHICH VIEWS ARE ALLOWED BY THE CONFIGURATION FOR THIS ACTIVITY
    //
    try {

        pstmt = con.prepareStatement ("" +
                "SELECT pro_allowable_views " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, group_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) allowable_views = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("Error in Proshop_gensheets looking up allowable views for activity_id " + group_id + ". Exc=" + exc.getMessage() );

    } finally {

        try { rs.close(); }
        catch (Exception ignored) {}

        try { pstmt.close(); }
        catch (Exception ignored) {}

    }

    out.println("<!-- ALLOWABLE VIEWS FOR activity_id " + group_id + " ARE " + allowable_views + " -->");

    if ( allowable_views.indexOf("1") != -1 ) allow_tabbed = true;
    if ( allowable_views.indexOf("2") != -1 ) allow_oldsummary = true;
    if ( allowable_views.indexOf("3") != -1 ) allow_detail = true;
    if ( allowable_views.indexOf("4") != -1 || allowable_views.indexOf("2") != -1 ) allow_summary = true;


    out.println(SystemUtils.HeadTitle2("Proshop Print Sheet Page"));

    out.println("<style>");
    out.println("body { text-align: center; }");
    out.println("</style>");

    out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    //
    // Display Print Option List
    //
    out.println("<center>");

    out.println("<p align=center><BR><BR><b>Print Sheets For " + month + "/" + day + "/" + year + "</b><BR></p>");

    if (allow_summary) {
        out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=summary\" class=\"ctrlPanelLink\" title=\"Print Summary\" alt=\"Print Summary\">");
        out.println("Summary View - Print</a></p>");

        out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=summary&excel=yes\" class=\"ctrlPanelLink\" title=\"Summary in Excel\" alt=\"Summary in Excel\">");
        out.println("Summary View - Excel</a></p>");
    }

    if (allow_detail) {
        out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=detail\" class=\"ctrlPanelLink\" title=\"Detailed Print\" alt=\"Print Detailed Report\">");
        out.println("Detailed View - Print</a></p>");

        out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=detail&excel=yes\" class=\"ctrlPanelLink\" title=\"Detailed Excel\" alt=\"Detailed in Excel\">");
        out.println("Detailed View - Excel</a></p>");
    }

    if (allow_oldsummary) {
        out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=oldsummary\" class=\"ctrlPanelLink\" title=\"Print Old Summary\" alt=\"Print Old Summary\">");
        out.println("Old Summary View - Print</a></p>");

        out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=oldsummary&excel=yes\" class=\"ctrlPanelLink\" title=\"Old Summary in Excel\" alt=\"Old Summary in Excel\">");
        out.println("Old Summary View - Excel</a></p>");
    }

    out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=individual\" class=\"ctrlPanelLink\" title=\"Individual Sheets Print\" alt=\"Print Individual Sheets\">");
    out.println("Individual Sheets - Print</a></p>");

    out.println("<p align=center><a href=\"Proshop_activity_print_sheet?group_id=" +group_id+ "&date=" + date + "&prtoption=individual&excel=yes\" class=\"ctrlPanelLink\" title=\"Individual Sheets Excel\" alt=\"Individual Sheets in Excel\">");
    out.println("Individual Sheets - Excel</a></p>");


   out.println("<br><br><form>");
   out.println("<input type=\"button\" value=\"Cancel\" onClick='self.close();'>");
   out.println("</form>");


    // debug
    out.println("<!-- group_id=" + group_id + " -->");

    out.println("</body></html>");

    out.close();

 } // end of doGet


 //*****************************************************
 // Process the call from doGet above - print the sheets
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    PreparedStatement pstmt3 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);


    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    int group_id = 0;
    int event_id = 0;
    int rest_id = 0;
    int lesson_id = 0;
    int month = 0;
    int day = 0;
    int year = 0;
    int date = 0;
    int i = 0;
    int cols = 0;

    boolean excel = false;

    String dayShort_name = "";
    String day_name = "";
    String bgcolor = "";
    String ltype = "";
    String memname = "";
    String proname = "";
    String rest_name = "";
    String event_name = "";

    //get the Print Option selected
    String prtOpt = req.getParameter("prtoption");

    //get the time sheet date
    String dateStr = req.getParameter("date");

    if (dateStr == null || dateStr.equals("")) {

      date = 0;

    } else {

       date = Integer.parseInt(dateStr);
    }

    String sid = req.getParameter("group_id");
    try {
        group_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    //
    //  Check if Excel option selected
    //
    if (req.getParameter("excel") != null) {

       excel = true;        // use excel output

       resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
    }


    if (date > 0) {

        year = date / 10000;
        int temp = year * 10000;
        month = date - temp;
        temp = month / 100;
        temp = temp * 100;
        day = month - temp;
        month = month / 100;
    }

     Calendar cal = new GregorianCalendar();       // get todays date

     cal.set(Calendar.YEAR, year);                 // change to requested date
     cal.set(Calendar.MONTH, month - 1);
     cal.set(Calendar.DAY_OF_MONTH, day);

     dayShort_name = dayShort_table[cal.get(Calendar.DAY_OF_WEEK)];   // get short name for day
     day_name = day_table[cal.get(Calendar.DAY_OF_WEEK)];   // get name for day

     parmRest parmr = new parmRest();          // allocate a parm block

     parmr.user = user;
     parmr.mship = "";
     parmr.mtype = "";
     parmr.date = date;
     parmr.day = day_name;
     parmr.course = "";
     parmr.activity_id = sess_activity_id;     // use Root id for now

     try {

         getRests.getAll(con, parmr);              // get the restrictions

     } catch (Exception exc) {

         Utilities.logError("Proshop_activity_print_sheet: getRests failed. user=" + user + ", date=" + date + ", activity_id=" + sess_activity_id + ", err=" + exc.toString());

     }

    if (excel == false) {

       out.println(SystemUtils.HeadTitle2("Proshop Print Sheet Page"));

       // include files for tabber
       out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/tabber.css\">");
       out.println("<script language=\"javascript\" src=\"/" +rev+ "/tabber.js\"></script>");

       out.println("<style>");
       out.println("body { text-align: center; }");
       out.println("</style>");

       out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");
       out.println("<center>");

      out.println("<p align=\"center\"><BR>");
      out.println("<form method=\"link\" action=\"javascript:self.print()\">");
      out.println("Click image to print this page. &nbsp;&nbsp;");
      out.println("<input type=\"image\" src=\"/" +rev+ "/images/print_sm.gif\" alt=\"Print\">");
      out.println("</form></p>");


    }    // end of IF excel

    //
    //  Output the sheet(s)
    //
    if (!prtOpt.equals("notes")) {    // if NOT Notes

       out.println("<p align=center><b>" + dayShort_name + " " + month + "/" + day + "/" + year + " Time Sheets</b></p>");
    }


    if (prtOpt.equals("oldsummary")) {    // if Old Summary Mode

        //
        // Display Time Sheet in Old Summary mode
        //
        out.println("<table align=center border=0>");
        out.println("<tr valign=top><td>");

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT t2.activity_name " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                    "GROUP BY t2.activity_name " +
                    "ORDER BY t2.sort_by, t2.activity_name");

            pstmt.clearParameters();
            pstmt.setInt(1, group_id);
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            out.println("<table class=timeSheet>");
            out.println("<tr>");
            out.println("<td class=headerTD nowrap><b>Time</b></td>");
            out.println("</tr>");

            while ( rs.next() ) {

                out.println("<tr>");
                out.println("<td class=timesheetTD2 nowrap><b>" + rs.getString(1) + "</b></td>");
                out.println("</tr>");
            }


            pstmt = con.prepareStatement("" +
                    "SELECT *, activity_name, " +
                        "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                    "ORDER BY date_time, t2.sort_by, activity_name");
            pstmt.clearParameters();
            pstmt.setInt(1, group_id);
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            String last_time = "";
            String td_style = "";

            while ( rs.next() ) {

                bgcolor = "";
                event_id = rs.getInt("event_id");
                lesson_id = rs.getInt("lesson_id");
                rest_id = rs.getInt("rest_id");

                // see if we are switching times (new col)
                if ( !last_time.equals(rs.getString("time")) ) {

                    last_time = rs.getString("time");
                    out.println("</table></td><td>");           // end current column and start new one
                    out.println("<table class=timeSheet>");     // start new table inside this column
                    out.println("<tr><td class=headerTD nowrap align=center><b>" + last_time + "</b></td></tr>");
                }

                // if not in use and not blocked
                if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                    if (rest_id > 0) {

                        try {
                            pstmt2 = con.prepareStatement("SELECT name, color FROM restriction2 WHERE id = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, rest_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                bgcolor = rs2.getString("color");
                            } else {
                                bgcolor = "";
                            }

                            pstmt2.close();

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING RESTRICTION:" + exc.toString() + "</p>");
                        }
                    }

                    td_style = "style=\"background: " + (!bgcolor.equals("") ? bgcolor : "#E9F4BB") + "; cursor:pointer\"";

                } else if (rs.getInt("blocker_id") != 0 || rs.getInt("auto_blocked") != 0) {

                    // time is blocked
                    td_style = "style=\"background: black;\"";
                }

                out.println("<tr>");

                if (event_id > 0) {
                    try {
                        pstmt2 = con.prepareStatement("SELECT color FROM events2b WHERE event_id = ?");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, event_id);

                        rs2 = pstmt2.executeQuery();

                        if (rs2.next()) {
                            bgcolor = rs2.getString("color");

                            out.println("<td class=timesheetTD2 nowrap style=\"text-align:center;background-color:" + bgcolor + ";\"><i>Event</i></td>");
                        } else {

                            out.println("<td class=timesheetTD2 nowrap style=\"text-align:center;\"><i>Event</i></td>");
                        }

                        pstmt2.close();

                    } catch (Exception exc) {
                        out.println("<p>ERROR LOADING EVENT:" + exc.toString() + "</p>");
                    }
                } else if (lesson_id > 0) {
                    try {
                        pstmt2 = con.prepareStatement("SELECT color FROM lessonbook5 WHERE recid = ?");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, lesson_id);

                        rs2 = pstmt2.executeQuery();

                        if (rs2.next()) {
                            bgcolor = rs2.getString("color");

                            out.println("<td class=timesheetTD2 nowrap style=\"text-align:center;;background-color:" + bgcolor + ";\"><i>Lesson</i></td>");
                        } else {

                            out.println("<td class=timesheetTD2 nowrap style=\"text-align:center;\"><i>Lesson</i></td>");
                        }

                        pstmt2.close();

                    } catch (Exception exc) {
                        out.println("<p>ERROR LOADING LESSON:" + exc.toString() + "</p>");
                    }
                } else {

                    // output the players (if any)
                    try {

                        pstmt2 = con.prepareStatement("" +
                                "SELECT COUNT(*) AS c " +
                                "FROM activity_sheets_players " +
                                "WHERE activity_sheet_id = ? " +
                                "ORDER BY pos");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, rs.getInt("sheet_id"));
                        rs2 = pstmt2.executeQuery();

                        if ( rs2.next() ) {

                            if (rs2.getInt(1) > 0) {

                                // EXISTING PLAYERS

                                out.print("<td class=timesheetTD2 align=left " +td_style+ ">");

                                // Show number of Members
                                if (rs.getInt("force_singles") > 0) {                                // if force_singles selected
                                   out.print("<nobr>&nbsp;Member (" + rs2.getInt(1) + ") S<nobr>");   // indicate Singles Match
                                } else {
                                   out.print("<nobr>&nbsp;Member (" + rs2.getInt(1) + ")<nobr>");
                                }

                            } else {

                                // EMPTY TIME SLOT

                                out.print("<td class=timesheetTD2 " + td_style + ">");

                                out.println("&nbsp;");
                            }
                        }

                        pstmt2.close();

                    } catch (Exception exc) {

                        out.println("<p>ERROR LOADING PLAYERS:" + exc.toString() + "</p>");

                    }
                }

                // end player time slot td/tr
                out.println("</td></tr>");

            } // end while loop for all the time slots

            pstmt.close();

           // end the table
           out.println("</table>");

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }

        // end the main table
        out.println("</td><tr></table>");     // end of Old Summary table


    } else if (prtOpt.equals("summary")) {
        
        String excel_style = "";

        if (excel) {
            excel_style = "border:1px solid black;";
        }
        
        out.println("<table align=center border=0>");     // one big table for whole sheet to align in center of page
        out.println("<tr valign=top><td>");

        // this array will hold the activities and the order in which they are displayed
        ArrayList<Integer> order = new ArrayList<Integer>();

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT t2.activity_name, t2.activity_id " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                    "GROUP BY t2.activity_name " +
                    "ORDER BY t2.sort_by, t2.activity_name");

            pstmt.clearParameters();
            pstmt.setInt(1, group_id); // activity_id
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            out.println("<table class=\"timeSheet\">");
            out.println("<tr>");
            out.println("<td class=\"headerTD\" nowrap style=\"" + excel_style + "\"><b>Time</b></td>");

            int j = 0;

            while ( rs.next() ) {

                order.add(rs.getInt("activity_id"));

                out.println("<td class=\"headerTD\" nowrap id=\"col" + j + "\" align=\"center\" style=\"" + excel_style + "\"><b>" + rs.getString(1) + "</b></td>"); // timesheetTD2
                j++;
            }

        } catch (Exception exc) {

        }

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT t1.*, t2.activity_name, t2.max_players, t2.consec_pro_csv, e.name, e.color, " +
                        "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                        "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                    "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                    "ORDER BY date_time, sort_by, activity_name");
            pstmt.clearParameters();
            pstmt.setInt(1, group_id); // activity_id
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            String last_time = "";
            String td_style = "";
            int j = 0;
            int row = 0;
            int order_pos = 0;
            int player_count = 0;
            int curr_activity_id = 0;
            String username = "";
            String last_name = "";
            String rest_color = "";
            String lesson_color = "";

            while ( rs.next() ) {

                rest_color = "";
                curr_activity_id = rs.getInt("activity_id");
                lesson_id = rs.getInt("lesson_id");

                // see if we are switching times (new row)
                if ( !last_time.equals(rs.getString("time")) ) {

                    last_time = rs.getString("time");
                    out.println("</tr>");
                    out.println("<tr><td class=\"headerTD\" style=\"" + excel_style + "\" nowrap align=\"center\" id=\"row" + row + "\"><b>" + last_time + "</b></td>"); // </tr>
                    j = 0;
                    order_pos = 0;
                    row++;
                }

                // if not in use and not blocked & not a lesson & not an event
                if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0 &&
                    rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && rs.getInt("rest_id") == 0) {

                    td_style = "style=\"background: #E9F4BB;" + excel_style + "\"";

                } else if (rs.getInt("blocker_id") != 0 || rs.getInt("auto_blocked") != 0) {

                    // time is blocked
                    td_style = "style=\"background: black;" + excel_style + "\"";

                } else if (lesson_id != 0) {

                    lesson_color = "#E9F4BB";

                    int temp_lesson_id = lesson_id;

                    if (temp_lesson_id < 0) temp_lesson_id = temp_lesson_id * -1;

                    try {
                        pstmt2 = con.prepareStatement("SELECT color FROM " + (lesson_id > 0 ? "lessonbook5 WHERE recid" : "lessongrp5 WHERE lesson_id") + " = ?");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, temp_lesson_id);

                        rs2 = pstmt2.executeQuery();

                        if (rs2.next()) {
                            lesson_color = rs2.getString("color");
                        } else {
                            lesson_color = "#E9F4BB";
                        }

                        pstmt2.close();

                    } catch (Exception exc) {
                        out.println("<p>ERROR LOADING LESSON:" + exc.toString() + "</p>");
                    }

                    // time is taken by a lesson booking
                    td_style = "style=\"background: " + lesson_color + ";" + excel_style + "\"";

                } else if (rs.getInt("event_id") != 0) {

                    // time is covered by an event
                    td_style = "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";" + excel_style + "\"";


                } else if (rs.getInt("rest_id") != 0) {

                    try {

                        // check all restrinctions to see if this time is affected
                        String restData = Proshop_gensheets.getRestData(parmr, rs.getInt("intTime"), curr_activity_id, rs.getInt("rest_id"));

                        if (!restData.equals("")) {      // if something found

                            out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                            // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                            StringTokenizer tok = new StringTokenizer( restData, ":" );

                            rest_color = tok.nextToken();               // get the color for this time
                        }

                    } catch (Exception exc) {

                        out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");
                    }

                    if (rest_color.equals("")) {
                        rest_color = "#E9F4BB";
                    }

                    // time is covered by a restriction
                    td_style = "style=\"background: " + rest_color + ";" + excel_style + "\"";

                }

                // see if we've skipped over any courts
                while (order.get(order_pos) != curr_activity_id) {
                    out.println("<td class=\"timesheetTD2\">&nbsp;</td>");
                    j++;
                    order_pos++;
                }

                j++;
                order_pos++;

                // output the players (if any)
                try {

                    // for these clubs we will display the first members last name instead of the generic 'Member' tag
                    pstmt2 = con.prepareStatement("" +
                            "SELECT COUNT(*) AS c, username, SUBSTRING_INDEX(player_name, ' ', -1) AS last_name " +
                            "FROM activity_sheets_players " +
                            "WHERE activity_sheet_id = ? " +
                            "GROUP BY activity_sheet_id " +
                            "ORDER BY pos");

                    pstmt2.clearParameters();
                    pstmt2.setInt(1, rs.getInt("sheet_id"));
                    rs2 = pstmt2.executeQuery();

                    if ( rs2.next() ) {

                        player_count = rs2.getInt("c");
                        username = rs2.getString("username");
                        last_name = rs2.getString("last_name");

                    } else {
                        player_count = 0;
                    }

                    if (player_count > 0) {

                        // EXISTING PLAYERS

                        out.print("<td class=\"timesheetTD2\" align=\"left\" nowrap " + td_style + ">");

                        out.print("<nobr>&nbsp;" + ((username.equals("")) ? ((last_name.equalsIgnoreCase("x")) ? "X" : "Guest") : last_name) + " (" + player_count + ")<nobr>");


                    } else {

                        // EMPTY TIME SLOT

                        out.print("<td class=\"timesheetTD2\" " + td_style + " nowrap>");

                        if ( rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 &&
                             rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                            // NOT IN USE
                            out.print("&nbsp;");

                        } else {

                            // IN USE OR TAKEN BY LESSON
                            if (rs.getInt("lesson_id") != 0) {

                                out.println("<center><i>" + ((rs.getInt("lesson_id") > 0) ? "Lesson" : "Clinic") + "</i></center>");
                                    
                            } else if (rs.getInt("event_id") > 0) {

                                out.println("<center><i>Event</i></center>");

                            } else if (rs.getInt("blocker_id") > 0) {

                                out.println("<center><i><font color=\"white\">Blocked</font></i></center>");

                            } else {

                                out.println("&nbsp;");

                            }
                        }

                    }

                    //}

                    pstmt2.close();

                } catch (Exception exc) {

                    out.println("<p>ERROR LOADING PLAYERS:" + exc.toString() + "</p>");

                }

                // end player time slot td/tr
                out.println("</td>");//</tr>

            } // end while loop for all the time slots

            pstmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");
        }

        // end the main tables
        out.println("<tr></table>");
        out.println("</td><tr></table>");      // end of DETAIL table

    } else if (prtOpt.equals("detail")) {


        // LAYOUT MODE = 2 - 'All View' (Pro Detailed View)

        out.println("<table align=center border=0>");     // one big table for whole sheet to align in center of page
        out.println("<tr valign=top><td>");

        out.println("<table border=1 class=\"timesheet\" align=center style=\"align:center;\">");

        // header row
        out.println("<tr class=timesheetTH>");                       // Header Row
        out.println("<td class=headerTD>Time</td>");
        out.println("<td class=headerTD width=100>Location</td>");      //  get configured name !!!!!!!!!!!!!!!!!!!!
        for (i = 1; i <= 4; i++) {                                   //rs.getInt("max_players")  !!!!!!!!!!!!!!!!!!!

            out.print("<td class=headerTD>Player " + i + "</td>");
        }
        out.println("</tr>");

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT *, activity_name, max_players, " +
                        "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                    "ORDER BY date_time, t2.sort_by, activity_name");
            pstmt.clearParameters();
            pstmt.setInt(1, group_id); // activity_id
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            int players_found = 0;
            int players_count = 0;
            boolean alt = true; // #5D8733
            String last_time = "";

            while (rs.next()) {

                if ( !last_time.equals(rs.getString("time")) ) {

                    last_time = rs.getString("time");
                    alt = (alt == false); // toggle row shading
                }

                // hide blocked times
                if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                    bgcolor = "";
                    event_id = rs.getInt("event_id");
                    lesson_id = rs.getInt("lesson_id");
                    rest_id = rs.getInt("rest_id");

                    players_found = 0; // reset
                    out.println("<tr class=\"timesheetTR\"><td align=center><font size=2>" + rs.getString("time") + "</font></td>");

                    out.print("<td class=" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + " nowrap>" + rs.getString("activity_name") + "</td>");

                    if (event_id > 0) {
                        try {
                            pstmt2 = con.prepareStatement("SELECT name, color FROM events2b WHERE event_id = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, event_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                event_name = rs2.getString("name");
                                bgcolor = rs2.getString("color");

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" + bgcolor + ";\"><i>" + event_name + "</i></td>");
                            } else {

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;\"><i>Event</i></td>");
                            }

                            pstmt2.close();

                            players_found = rs.getInt("max_players");

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING EVENT:" + exc.toString() + "</p>");
                        }
                    } else if (lesson_id > 0) {     // single member lesson
                        try {
                            pstmt2 = con.prepareStatement("" +
                                    "SELECT lb.ltype, lb.memname, lb.color, CONCAT(lp.fname, ' ', IF(lp.mi<>'', CONCAT(lp.mi, ' '), ''), lp.lname) AS proname " +
                                    "FROM lessonbook5 lb " +
                                    "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lb.proid " +
                                    "WHERE lb.recid = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, lesson_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                ltype = rs2.getString("lb.ltype");
                                bgcolor = rs2.getString("lb.color");
                                memname = rs2.getString("lb.memname");
                                proname = rs2.getString("proname");

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;;background-color:" + bgcolor + ";\"><i>" + proname + " - " + ltype + " - " + memname + "</i></td>");
                            } else {

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;\"><i>Lesson</i></td>");
                            }

                            pstmt2.close();

                            players_found = rs.getInt("max_players");

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING LESSON:" + exc.toString() + "</p>");
                        }
                    } else if (lesson_id < 0) {     // group lesson
                        try {
                            pstmt2 = con.prepareStatement("" +
                                    "SELECT lg.lname, lg.color, CONCAT(lp.fname, ' ', IF(lp.mi<>'', CONCAT(lp.mi, ' '), ''), lp.lname) AS proname " +
                                    "FROM lessongrp5 lg " +
                                    "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lg.proid " +
                                    "WHERE lg.lesson_id = ?");
                            lesson_id = Math.abs(lesson_id);
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, lesson_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                ltype = rs2.getString("lg.lname");
                                bgcolor = rs2.getString("lg.color");
                                proname = rs2.getString("proname");

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;;background-color:" + bgcolor + ";\"><i>" + proname + " - " + ltype + "</i></td>");
                            } else {

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;\"><i>Lesson</i></td>");
                            }

                            pstmt2.close();

                            players_found = rs.getInt("max_players");

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING LESSON:" + exc.toString() + "</p>");
                        }
                    } else {

                        if (rest_id > 0) {

                            try {
                                pstmt2 = con.prepareStatement("SELECT name, color FROM restriction2 WHERE id = ?");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rest_id);

                                rs2 = pstmt2.executeQuery();

                                if (rs2.next()) {
                                    bgcolor = rs2.getString("color");
                                    rest_name = rs2.getString("name");
                                } else {
                                    bgcolor = "";
                                    rest_name = "";
                                }

                                pstmt2.close();

                            } catch (Exception exc) {
                                out.println("<p>ERROR LOADING RESTRICTION:" + exc.toString() + "</p>");
                            }
                        }

                        try {

                            pstmt2 = con.prepareStatement("SELECT count(*) FROM activity_sheets_players WHERE activity_sheet_id = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, rs.getInt("sheet_id"));
                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                players_count = rs2.getInt(1);
                            }

                            pstmt2.close();

                            if (players_count > 0) {

                                pstmt2 = con.prepareStatement("" +
                                        "SELECT * " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? " +
                                        "ORDER BY pos");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rs.getInt("sheet_id"));
                                rs2 = pstmt2.executeQuery();

                                while ( rs2.next() ) {

                                    players_found++;

                                    out.print("<td class=timesheetTD nowrap" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + "\"" : "") + ">");

                                     out.print("&nbsp;<img src=\"/" +rev+ "/images/");

                                     switch (rs2.getInt("show")) {
                                     case 1:
                                         out.print("xbox.gif");
                                         break;
                                     case 2:
                                         out.print("rmtbox.gif");
                                         break;
                                     default:
                                         out.print("mtbox.gif");
                                         break;
                                     }

                                     // check-in image and player name
                                     out.print("\" border=\"1\" name=\"noShow\">");
                                     out.print("&nbsp;" + rs2.getString("player_name") + "</td>");

                                } // end player loop

                                pstmt2.close();
                                
                            } else if (rest_id > 0) {
                                out.println("<td class=timesheetTD colspan=\"" + rs.getInt("max_players") + "\" nowrap" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + ";text-align:center\"" : "") + "><i>" + (!rest_name.equals("") ? rest_name : "") + "</i></td>");
                                players_found = rs.getInt("max_players");
                            }

                        } catch (Exception exc) {

                            out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                        }
                    }

                    // see if we need to fill in any remaining player positions for this time slot
                    while (players_found < rs.getInt("max_players")) {

                        if (rs.getInt("force_singles") > 0) {                // if force_singles selected
                           out.print("<td class=timesheetTD" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + "\"" : "") + ">&nbsp;N/A</td>");       // Indicate slot not available
                        } else {
                           out.print("<td class=timesheetTD" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + "\"" : "") + ">&nbsp;</td>");
                        }
                        players_found++;
                    }
                    out.println("</tr>");

                } // end if blocked

            } // end time slot rs loop

            pstmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }

        out.println("</table>");
        out.println("</div>");
        out.println("</div>");

        // end the main table
        out.println("</td><tr></table>");      // end of DETAIL table


    } else if ( prtOpt.equals("individual")) {


        // LAYOUT MODE = 3 - Individual Sheets in Detail Mode

        String lastName = "";
        String actName = "";

        out.println("<table align=center border=0>");     // one big table for whole sheet to align in center of page
        out.println("<tr valign=top><td>");

        try {
                                                      // Get Activlity Sheets in order by name, then time
            pstmt = con.prepareStatement("" +
                    "SELECT *, activity_name, max_players, " +
                        "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                    "ORDER BY t2.sort_by, activity_name, date_time");
            pstmt.clearParameters();
            pstmt.setInt(1, group_id); // activity_id
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            int players_found = 0;
            int players_count = 0;
            boolean alt = true;
            String last_time = "";

            while (rs.next()) {

               actName = rs.getString("activity_name");       // get the name of this activity
               cols = rs.getInt("max_players") +1;            // # of cols in table

               if (!actName.equals( lastName )) {           // if new Sheet

                  if (!lastName.equals("")) {               // if not the first sheet

                     out.println("</table><BR><BR>");
                  }

                  out.println("<table border=1 class=\"timesheet\" align=center style=\"align:center;" + (!lastName.equals("") ? "page-break-before:always;" : "") + "\"\">");

                  lastName = actName;

                  // header row
                  out.println("<tr class=timesheetTH>");                       // Header Row
                  out.println("<td class=headerTD colspan=" +cols+ " align=center><strong>" +actName+ "</strong></td></tr>");    // name of Sheet
                  out.println("<tr class=timesheetTH>");                       // Header Row 2
                  out.println("<td class=headerTD>Time</td>");
                  for (i = 1; i < cols; i++) {

                     out.print("<td class=headerTD>Player " + i + "</td>");
                  }
                  out.println("</tr>");
               }

               // output each time

                if ( !last_time.equals(rs.getString("time")) ) {

                    last_time = rs.getString("time");
                    alt = (alt == false); // toggle row shading
                }

                // hide blocked times
                if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                    bgcolor = "";
                    event_id = rs.getInt("event_id");
                    lesson_id = rs.getInt("lesson_id");
                    rest_id = rs.getInt("rest_id");

                    players_found = 0; // reset
                    out.println("<tr class=\"timesheetTR\"><td align=center><font size=2>" + rs.getString("time") + "</font></td>");


                    if (event_id > 0) {
                        try {
                            pstmt2 = con.prepareStatement("SELECT name, color FROM events2b WHERE event_id = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, event_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                event_name = rs2.getString("name");
                                bgcolor = rs2.getString("color");

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" + bgcolor + ";\"><i>" + event_name + "</i></td>");
                            } else {

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;\"><i>Event</i></td>");
                            }

                            pstmt2.close();

                            players_found = rs.getInt("max_players");

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING EVENT:" + exc.toString() + "</p>");
                        }
                    } else if (lesson_id > 0) {
                        try {
                            pstmt2 = con.prepareStatement("" +
                                    "SELECT lb.ltype, lb.memname, lb.color, CONCAT(lp.fname, ' ', IF(lp.mi<>'', CONCAT(lp.mi, ' '), ''), lp.lname) AS proname " +
                                    "FROM lessonbook5 lb " +
                                    "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lb.proid " +
                                    "WHERE lb.recid = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, lesson_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                ltype = rs2.getString("lb.ltype");
                                bgcolor = rs2.getString("lb.color");
                                memname = rs2.getString("lb.memname");
                                proname = rs2.getString("proname");

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;;background-color:" + bgcolor + ";\"><i>" + proname + " - " + ltype + " - " + memname + "</i></td>");
                            } else {

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;\"><i>Lesson</i></td>");
                            }

                            pstmt2.close();

                            players_found = rs.getInt("max_players");

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING LESSON:" + exc.toString() + "</p>");
                        }
                    } else if (lesson_id < 0) {     // group lesson
                        try {
                            pstmt2 = con.prepareStatement("" +
                                    "SELECT lg.lname, lg.color, CONCAT(lp.fname, ' ', IF(lp.mi<>'', CONCAT(lp.mi, ' '), ''), lp.lname) AS proname " +
                                    "FROM lessongrp5 lg " +
                                    "LEFT OUTER JOIN lessonpro5 lp ON lp.id = lg.proid " +
                                    "WHERE lg.lesson_id = ?");
                            lesson_id = Math.abs(lesson_id);
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, lesson_id);

                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                ltype = rs2.getString("lg.lname");
                                bgcolor = rs2.getString("lg.color");
                                proname = rs2.getString("proname");

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;;background-color:" + bgcolor + ";\"><i>" + proname + " - " + ltype + "</i></td>");
                            } else {

                                out.println("<td class=timesheetTD nowrap colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;\"><i>Lesson</i></td>");
                            }

                            pstmt2.close();

                            players_found = rs.getInt("max_players");

                        } catch (Exception exc) {
                            out.println("<p>ERROR LOADING LESSON:" + exc.toString() + "</p>");
                        }
                    } else {

                        if (rest_id > 0) {

                            try {
                                pstmt2 = con.prepareStatement("SELECT name, color FROM restriction2 WHERE id = ?");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rest_id);

                                rs2 = pstmt2.executeQuery();

                                if (rs2.next()) {
                                    bgcolor = rs2.getString("color");
                                    rest_name = rs2.getString("name");
                                } else {
                                    bgcolor = "";
                                    rest_name = "";
                                }

                                pstmt2.close();

                            } catch (Exception exc) {
                                out.println("<p>ERROR LOADING RESTRICTION:" + exc.toString() + "</p>");
                            }
                        }

                        try {

                            pstmt2 = con.prepareStatement("SELECT count(*) FROM activity_sheets_players WHERE activity_sheet_id = ?");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, rs.getInt("sheet_id"));
                            rs2 = pstmt2.executeQuery();

                            if (rs2.next()) {
                                players_count = rs2.getInt(1);
                            }

                            pstmt2.close();

                            if (players_count > 0) {

                                pstmt2 = con.prepareStatement("" +
                                        "SELECT * " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? " +
                                        "ORDER BY pos");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rs.getInt("sheet_id"));
                                rs2 = pstmt2.executeQuery();

                                while ( rs2.next() ) {

                                    players_found++;

                                    out.print("<td class=timesheetTD nowrap" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + "\"" : "") + ">");

                                     out.print("&nbsp;<img src=\"/" +rev+ "/images/");

                                     switch (rs2.getInt("show")) {
                                     case 1:
                                         out.print("xbox.gif");
                                         break;
                                     case 2:
                                         out.print("rmtbox.gif");
                                         break;
                                     default:
                                         out.print("mtbox.gif");
                                         break;
                                     }

                                     // check-in image and player name
                                     out.print("\" border=\"1\" name=\"noShow\">");
                                     out.print("&nbsp;" + rs2.getString("player_name") + "</td>");

                                } // end player loop

                                pstmt2.close();

                            } else if (rest_id > 0) {
                                out.println("<td class=timesheetTD colspan=\"" + rs.getInt("max_players") + "\" nowrap" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + ";text-align:center\"" : "") + "><i>" + (!rest_name.equals("") ? rest_name : "") + "</i></td>");
                                players_found = rs.getInt("max_players");
                            }

                        } catch (Exception exc) {

                            out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                        }
                    }

                    // see if we need to fill in any remaining player positions for this time slot
                    while (players_found < rs.getInt("max_players")) {

                        if (rs.getInt("force_singles") > 0) {                // if force_singles selected
                           out.print("<td class=timesheetTD" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + "\"" : "") + ">&nbsp;N/A</td>");       // Indicate slot not available
                        } else {
                           out.print("<td class=timesheetTD" + (!bgcolor.equals("") ? " style=\"background-color:" + bgcolor + "\"" : "") + ">&nbsp;</td>");
                        }
                        players_found++;
                    }
                    out.println("</tr>");

                } // end if blocked

            } // end time slot rs loop

            pstmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }

        out.println("</table>");
        out.println("</div>");
        out.println("</div>");

        // end the main table
        out.println("</td><tr></table>");     // end of INDIVIDUAL table


    } else if ( prtOpt.equals("notes") ) {    // if Notes


        // Print NOTES

       out.println("<p align=center><b>All Notes for " + dayShort_name + " " + month + "/" + day + "/" + year + "</b></p>");

        out.println("<table align=center border=0>");     // one big table for whole sheet to align in center of page
        out.println("<tr valign=top><td>");

        out.println("<table border=1 class=\"timesheet\" align=center style=\"align:center;\">");

        // header row
        out.println("<tr class=timesheetTH>");                       // Header Row
        out.println("<td class=headerTD>Time</td>");
        out.println("<td class=headerTD width=100>Sheet</td>");
        out.print("<td class=headerTD>Players</td>");
        out.print("<td class=headerTD>Notes</td>");
        out.println("</tr>");

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT *, activity_name, max_players, " +
                        "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                    "FROM activity_sheets t1 " +
                    "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                    "WHERE " +
                        "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                        "DATE_FORMAT(date_time, \"%Y%m%d\") = ? AND notes != '' " +
                    "ORDER BY date_time, t2.sort_by, activity_name");
            pstmt.clearParameters();
            pstmt.setInt(1, group_id); // activity_id
            pstmt.setInt(2, date);
            rs = pstmt.executeQuery();

            String players = "";
            boolean alt = true;
            String last_time = "";

            while (rs.next()) {

                players = "";

                if ( !last_time.equals(rs.getString("time")) ) {

                    last_time = rs.getString("time");
                    alt = (alt == false); // toggle row shading
                }

                // hide blocked times
                if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                    out.println("<tr class=\"timesheetTR\"><td class=timesheetTD nowrap>&nbsp;" + rs.getString("time") + "&nbsp;</td>");

                    out.print("<td class=timesheetTD nowrap>" + rs.getString("activity_name") + "</td>");

                    try {

                        pstmt2 = con.prepareStatement("" +
                                "SELECT * " +
                                "FROM activity_sheets_players " +
                                "WHERE activity_sheet_id = ? " +
                                "ORDER BY pos");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, rs.getInt("sheet_id"));
                        rs2 = pstmt2.executeQuery();

                        while ( rs2.next() ) {

                             players = players + rs2.getString("player_name") + "<br>";

                        } // end player loop

                        pstmt2.close();

                        out.print("<td class=timesheetTD nowrap>" + players + "</td>");

                    } catch (Exception exc) {
                        out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");
                    }

                    out.print("<td class=timesheetTD>" + rs.getString("notes") + "</td>");

                    out.println("</tr>");

                } // end if blocked

            } // end time slot rs loop

            pstmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }

        out.println("</table>");
        out.println("</div>");
        out.println("</div>");

        // end the main table
        out.println("</td><tr></table>");      // end of NOTES table


    } else {

       out.println("<p align=center><b>Error - Unknown print Option Specified</b></p>");
    }

    if (excel == false) {

      out.println("<p align=\"center\"><form>");
      out.println("<input type=\"button\" value=\"Done\" onClick='self.close();'>");
      out.println("</form></p>");
   }


 } // end of doPost



 // *********************************************************
 //  Output the image based on the check-in value
 // *********************************************************

 private static String getShowImageForPrint(short pShow) {

    String tmp;

    switch (pShow) {
    case 1:
        tmp = "<img src=\"/" +rev+ "/images/xboxsm.gif\" border=\"2\">";
        break;
    case 2:
        tmp = "<img src=\"/" +rev+ "/images/ymtboxsm.gif\" border=\"2\">";
        break;
    default: // if not 1 or 2 then assume 0 and return the small empty image box
        tmp = "<img src=\"/" +rev+ "/images/mtboxsm.gif\" border=\"2\">";
    }

    return tmp;
 }


 // *********************************************************
 //  Strip the '9' from end of string
 // *********************************************************

 private static final String strip9( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      }

      return new String (ca2);

 } // end strip9



 // *********************************************************
 //  Strip 1 char from the start of a string
 // *********************************************************

 private static final String stripOne( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i+1];
         ca2[i] = oldLetter;
      }

      return new String (ca2);

 } // end stripOne

 private static void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
     out.println(SystemUtils.HeadTitle("Database Error"));
     out.println("<BODY><CENTER>");
     out.println("<BR><BR><H1>Database Access Error</H1>");
     out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
     out.println("<BR>Please try again later.");
     out.println("<BR><br>Fatal Error: " + pMessage);
     out.println("<BR><br>Exception: " + pException);
     out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }

}
