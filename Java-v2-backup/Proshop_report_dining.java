/***************************************************************************************
 *   Proshop_report_dining:  This servlet will provide some basic reporting capabilities
 *                           for our Dining System.
 * 
 * 
 *   Called by:       called by self and start w/ direct call main menu option.
 *
 *
 *   Created:         10/28/2014 by Paul
 *
 *
 *   Last Updated:  
 * 
 *          
 * 
 * 
 * 
 ****************************************************************************************/


import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;


import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.Common_skin;
import com.foretees.common.parmDiningCosts;
import com.foretees.common.timeUtil;
import com.foretees.common.reqUtil;


public class Proshop_report_dining extends HttpServlet {

    final String rev = SystemUtils.REVLEVEL;
    final String DELIM = ",";
    final String QUOTE = "\"";
    final String NEW_LINE = "\r\n";
    final int OUTPUT_CSV = 1;
    final int OUTPUT_HTML = 2;
    final int SEPERATE_NAMES = 0;
    final int CONDENSE_NAMES = 1;

    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);

    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
     

    
    if (req.getParameter("todo") != null) {
        doReport(req, resp);
        return;
    }

    PrintWriter out = resp.getWriter();
    Connection con_d = Connect.getDiningCon();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    int organization_id = 74; // las campanas
    
    HttpSession session = null;
    
    String user = "";
    
    session = SystemUtils.verifyPro(req, out);
    
    if (session == null) return;
    
    Connection con = Connect.getCon(req);

    if (con == null) {

        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    String club = (String)session.getAttribute("club");
    
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    out.println(SystemUtils.HeadTitle2("Dining Reports"));
    out.println(Common_skin.getScripts(club, 0, session, req, true));
            
    out.println("</head>");
    
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
    
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
    // page title
    out.println("<br><p align=center><font size=5 color=white>Dining Reports</font><br><br><br>");
    
    out.println("<table align=center style=\"margin:auto\"><tr valign=top><td>");
    
    out.println("<table align=center style=\"margin:auto;border:2px black solid;padding:15px;background-color:white\">");
    
    out.println("<tr><td><b>Event Reports</b></td></tr>");
    
    out.println("<tr><td><form method=get>");
    
    try {

        pstmt = con_d.prepareStatement ("" +
                        "SELECT e.id, e.name, e.members_can_make_reservations, " +
                            "e.minimum_advance_days, e.maximum_advance_days, " +
                            "to_char(e.start_time, 'HH24:MI') AS time1, " +
                            "to_char(e.start_time, 'HH24MI') AS stime, " +
                            "to_char(e.end_time, 'HH24MI') AS etime, " +
                            "to_char(e.date, 'YYYYMMDD')::int AS our_date, " +
                            "to_char(e.date, 'MM/DD/YYYY') AS date1, " +
                            "e.costs, loc.name AS location_name, " +
                            "e.start_time, e.date, e.maximum_party_size " +
                        "FROM events e " +
                        "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id " +
                        "WHERE e.organization_id = ? AND e.cancelled = false AND " +
                            "to_char(e.date, 'YYYYMMDD')::int >= ? " +
                        "ORDER BY e.date, e.start_time");
        
        pstmt.clearParameters();
        pstmt.setInt(1, organization_id);
        pstmt.setLong(2, Utilities.getDate(con));

        rs = pstmt.executeQuery();

        out.println("<br>Choose Event: <select name=\"event_id\"><option value=\"0\">Choose Event</option>");
        while ( rs.next() ) {

            out.println("<option value=\"" + rs.getInt("id") + "\">" + rs.getString("name") + "</option>");

        }
        out.println("</select>");

    } catch (Exception exc) {

        Utilities.logError("Proshop_report_dining - eventListing: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    out.println("<br>Grouping: <select name=\"condense_names\" size=\"1\">");
    out.println("<option value=\"" + SEPERATE_NAMES + "\">One row per cover</option>");
    out.println("<option value=\"" + CONDENSE_NAMES + "\">One row per reservation</option>");
    out.println("</select>");
        
    out.println("<br>Output: <select name=\"output_mode\" size=\"1\">");
    out.println("<option value=\"" + OUTPUT_HTML + "\">Web page</option>");
    out.println("<option value=\"" + OUTPUT_CSV + "\">Comma seperated values</option>");
    out.println("</select>");
    
    out.println("<input type=hidden name=todo value=\"report\">");

    out.println("<br><br><input type=submit name=submit value=\"View Event Report\" style=\"width:150px;margin:auto\">");
    
    out.println("</form></td></tr></table>");
    
    out.println("</td><td>");

    out.println("<table align=center style=\"margin:auto;border:2px black solid;padding:15px;background-color:white\">");
    
    out.println("<tr><td><b>Ala Carte Reports</b></td></tr>");
    
    out.println("<tr><td><form method=get>");
    
    String tmp = timeUtil.getStringDateMMDDYYYY(timeUtil.getClubDate(req));
    out.println("<br>Choose Date: <input type=text name=date class=\"ft_date_picker_field\" "
            + "data-ftdefaultdate=\"" + tmp + "\" value=\"" + tmp + "\">");
    
    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT id, name " +
                "FROM locations " +
                "WHERE " +
                    "organization_id = ? " + 
                    "AND deactivated = false");

        pstmt.setInt(1, organization_id);

        rs = pstmt.executeQuery();

        out.println("<br>Choose Location: <select name=\"location_id\"><option value=\"0\">All Locations</option>");
        while ( rs.next() ) {
            out.println("<option value=\"" + rs.getInt("id") + "\">" + rs.getString("name") + "</option>");
        }
        out.println("</select>");

    } catch (Exception exc) {

        Utilities.logError("Proshop_report_dining - locationListing: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    
    out.println("<br>Grouping: <select name=\"condense_names\" size=\"1\">");
    out.println("<option value=\"" + SEPERATE_NAMES + "\">One row per cover</option>");
    out.println("<option value=\"" + CONDENSE_NAMES + "\">One row per reservation</option>");
    out.println("</select>");
        
    out.println("<br>Output: <select name=\"output_mode\" size=\"1\">");
    out.println("<option value=\"" + OUTPUT_HTML + "\">Web page</option>");
    out.println("<option value=\"" + OUTPUT_CSV + "\">Comma seperated values</option>");
    out.println("</select>");
    
    out.println("<input type=hidden name=todo value=\"report\">");
    out.println("<input type=hidden name=alacarte value=\"1\">");

    out.println("<br><br><input type=submit name=submit value=\"View Ala Carte Report\">");
    
    out.println("</form></td></tr></table>");
    
    
    out.println("</td></tr></table>");
    
    try { con_d.close(); }
    catch (Exception ignore) {}
    
 }
 
 
 public void doReport(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
     

    int nameDisplayType = reqUtil.getParameterInteger(req, "condense_names", SEPERATE_NAMES);
    int outputMode = reqUtil.getParameterInteger(req, "output_mode", OUTPUT_HTML);
    
    PrintWriter out = resp.getWriter();
    boolean excel = (req.getParameter("excel") != null);
    
    // set response content type
    try {
        if (excel) {
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\"DiningReport.xls\"");
        } else if (outputMode == OUTPUT_CSV) {
            resp.setContentType("text/csv");
            resp.setHeader("Content-Disposition", "attachment;filename=DiningReport.csv");
        } else {
            resp.setContentType("text/html");
        }
    } catch (Exception ignore) { }
    
    Connection con_d = Connect.getDiningCon();
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    int organization_id = 74; // las campanas
    int event_id = reqUtil.getParameterInteger(req, "event_id", 0);
    boolean event = event_id > 0;
    
    String sdate = reqUtil.getParameterString(req, "date", "");
    //int date = reqUtil.getParameterInteger(req, "date", timeUtil.getClubDate(req));
    int location_id = reqUtil.getParameterInteger(req, "location_id", 0);
    int date = 0;
    
    if (!sdate.isEmpty()) {

        StringTokenizer tok = new StringTokenizer(sdate, "/");
        String num1 = tok.nextToken();
        int month1 = Integer.parseInt(num1);
        num1 = tok.nextToken();
        int day1 = Integer.parseInt(num1);
        num1 = tok.nextToken();
        int year1 = Integer.parseInt(num1);

        date = timeUtil.buildIntDate(year1, month1, day1);
    }
    
    //out.println("<!-- sdate=" + sdate + ", date=" + date + ", month1=" + month1 + ", day1=" + day1 + ", year1=" + year1 + " -->");
    
    parmDiningCosts parmCosts = new parmDiningCosts();
            
    //ArrayList<String> columns = new ArrayList<String>();
    
    // if event then see how many different price categories there are configured
    if (event) {

        try {
            pstmt = con_d.prepareStatement ("" +
                            "SELECT costs " +
                            "FROM events " +
                            "WHERE organization_id = ? AND id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, organization_id);
            pstmt.setInt(2, event_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                // extract the cost for the event
                parmCosts.costs = rs.getString("costs");

                if ( !parmCosts.parseCosts() ) {

                    //out.println("<!--  FATAL ERROR: " + parmCosts.err_string + " -->");
                    //out.println("<!--  FATAL ERROR: " + parmCosts.err_message + " -->");

                } else {

                    //out.println("<!--  COSTS FOUND: " + parmCosts.costs_found + " -->");

                }

            }
            
        } catch (Exception exc) {

            Utilities.logError("Proshop_report_dining.doReport: Error looking up event costs. event_id = " + event_id + ", Err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    } // end if event
    
    
    List<String> header = new ArrayList<String>();
    List<List> rows = new ArrayList<List>();
    
    if (outputMode == OUTPUT_HTML) out.println("<table border=1>");
    
    try {

        String sql = "";
            
        if (nameDisplayType == SEPERATE_NAMES) {
            
            sql = "" + 
                "SELECT " + 
                    "r.date, to_char(r.date, 'YYYYMMDD')::int AS date_int, " + 
                    "r.time, to_char(r.time, 'HH24MI') AS time_int, " + 
                    "r.reservation_number, r.reservee_name, p.user_identity, " + 
                    "r.check_number, r.charges, " + 
                    ((event) ? "e.name AS event_name, " : "") + 
                    "m.name AS meal_period_name, l.name AS location_name, " + 
                    "r.special_requests, r.member_special_requests, " + 
                    "CASE WHEN r.parent_id IS NULL THEN true " + 
                    "ELSE false " + 
                    "END AS master " + 
                "FROM reservations r " +
                "LEFT OUTER JOIN people p ON p.id = r.person_id " +
                "LEFT OUTER JOIN locations l ON l.id = r.location_id " +
                "LEFT OUTER JOIN meal_periods m ON m.id = r.meal_period_id " +
                ((event) ? "LEFT OUTER JOIN events e ON e.id = r.event_id " : "") +
                "WHERE r.organization_id = ? AND r.state <> 'cancelled' ";

            if (event) {
                sql += "AND r.event_id = ? AND e.cancelled = false ";
            } else {
                sql += "AND r.event_id IS NULL AND to_char(date, 'YYYYMMDD')::int = ? ";
                if (location_id > 0) {
                    sql += "AND r.location_id = ? ";
                }
            }

            sql +=  "ORDER BY date, time, m.name, l.name, reservation_number, master DESC";

            //out.println("<!-- " + sql + " -->");

            pstmt = con_d.prepareStatement (sql);
            pstmt.setInt(1, organization_id);
            if (event) {
                pstmt.setInt(2, event_id);
            } else {
                pstmt.setInt(2, date);
                if (location_id > 0) {
                    pstmt.setInt(3, location_id);
                }
            }
            rs = pstmt.executeQuery();

            while ( rs.next() ) {

                List<String> row = new ArrayList<String>();

                row.add(Utilities.getDateFromYYYYMMDD(rs.getInt("date_int"), 1));
                row.add(Utilities.getSimpleTime(rs.getInt("time_int")));
                row.add(""+rs.getInt("reservation_number"));
                row.add(rs.getString("reservee_name"));
                if (rs.getString("user_identity") != null && !rs.getString("user_identity").isEmpty()) {
                    row.add(rs.getString("user_identity").substring(0, rs.getString("user_identity").indexOf(":")).trim());
                } else {
                    row.add("");
                }
                row.add("" + rs.getInt("check_number"));
                row.add(rs.getString("location_name"));

                rows.add(row);
            }

            header.add("Date");
            header.add("Time");
            header.add("Reservation #");
            header.add("Member Name");
            header.add("Member Number");
            header.add("Check #");
            header.add("Location");
    
        } else if (nameDisplayType == CONDENSE_NAMES) {
            
            sql = "" + 
                "SELECT " + 
                    "r.date, to_char(r.date, 'YYYYMMDD')::int AS date_int, " + 
                    "r.time, to_char(r.time, 'HH24MI') AS time_int, " + 
                    "r.reservation_number, r.reservee_name, p.user_identity, " + 
                    "r.check_number, r.charges, " + 
                    ((event) ? "e.name AS event_name, " : "") + 
                    "m.name AS meal_period_name, l.name AS location_name, " + 
                    "r.special_requests, r.member_special_requests, " + 
                    "CASE WHEN r.parent_id IS NULL THEN true " + 
                    "ELSE false " + 
                    "END AS master " + 
                "FROM reservations r " +
                "LEFT OUTER JOIN people p ON p.id = r.person_id " +
                "LEFT OUTER JOIN locations l ON l.id = r.location_id " +
                "LEFT OUTER JOIN meal_periods m ON m.id = r.meal_period_id " +
                ((event) ? "LEFT OUTER JOIN events e ON e.id = r.event_id " : "") +
                "WHERE r.organization_id = ? AND r.state <> 'cancelled' ";

            if (event) {
                sql += "AND r.event_id = ? AND e.cancelled = false ";
            } else {
                sql += "AND r.event_id IS NULL AND to_char(date, 'YYYYMMDD')::int = ? ";
                if (location_id > 0) {
                    sql += "AND r.location_id = ? ";
                }
            }

            sql +=  "ORDER BY date, time, m.name, l.name, reservation_number, master DESC";

            //out.println("<!-- " + sql + " -->");

            pstmt = con_d.prepareStatement (sql);
            pstmt.setInt(1, organization_id);
            if (event) {
                pstmt.setInt(2, event_id);
            } else {
                pstmt.setInt(2, date);
                if (location_id > 0) {
                    pstmt.setInt(3, location_id);
                }
            }
            rs = pstmt.executeQuery();

            while ( rs.next() ) {

                if (rs.getBoolean("master") == true) {
                    
                    List<String> row = new ArrayList<String>();

                    row.add(Utilities.getDateFromYYYYMMDD(rs.getInt("date_int"), 1));
                    row.add(Utilities.getSimpleTime(rs.getInt("time_int")));
                    row.add(""+rs.getInt("reservation_number"));
                    String tmp = getReservationNameData(rs.getInt("reservation_number"), organization_id, con_d);

                    int count = 0;
                    try {
                        count = Integer.parseInt(tmp.substring(tmp.lastIndexOf((":"), tmp.length()) + 1, tmp.length()));
                    } catch (NumberFormatException e) {
                        out.println("<!-- tmp=" + tmp + ", ERR:  " + e.toString() + " -->");
                    }
                    row.add(tmp.substring(0, tmp.indexOf(":")));
                    row.add(""+count);
                    row.add(rs.getString("location_name"));

                    rows.add(row);
                }
            }

            header.add("Date");
            header.add("Time");
            header.add("Reservation #");
            header.add("Member Names");
            header.add("Count");
            header.add("Location");
            
        }
        
    } catch (Exception exc) {

        out.println("<p>Proshop_report_dining.doReport: Err=" + exc.toString() + "</p><p>" + Utilities.getStackTraceAsString(exc) + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        try { con_d.close(); }
        catch (Exception ignore) {}

    }
    
    // output the header row
    out.println(buildRow(header, outputMode, true).toString());
    
    // output the data rows
    out.println(buildRows(rows, outputMode).toString());
    
    if (outputMode == OUTPUT_HTML) out.println("</table></body></html>");
    
    out.close();
    
 } // end of doReport routine
 
 
 private StringBuilder buildRow(List<String> row, int outputMode, boolean header) {
     
    
    StringBuilder h = new StringBuilder();
        
    switch (outputMode) {
        
        case OUTPUT_CSV:
            
            for (String column : row) {
                h.append(QUOTE);
                h.append(column);
                h.append(QUOTE);
                h.append(DELIM);
            }
            if (h.length() > 0) h.setLength(h.length() - 1);
            
            break;
            
        case OUTPUT_HTML:
        
            h.append("<tr>");
            for (String column : row) {
                h.append("<td>");
                if (header) h.append("<b>");
                h.append(column);
                if (header) h.append("</b>");
                h.append("</td>");
            }
            h.append("</tr>");
            break;
    }
    
    return h;
 }
 
 
 private StringBuilder buildRows(List<List> rows, int mode) {
     
     
    StringBuilder h = new StringBuilder();
    
    switch (mode) {
        
        case OUTPUT_CSV:
            
            for (List<String> row : (List<List>) rows){
                h.append(buildRow(row, mode, false));
                h.append(NEW_LINE);
            }
            
            break;
            
        case OUTPUT_HTML:
            
            //h.append("<tr>");
            for (List<String> row : (List<List>) rows){
                h.append(buildRow(row, mode, false));
            }
            //h.append("</tr>");
            
            break;
    }
    
    return h;
    
 }
 
 private String getReservationNameData(int reservation_number, int organization_id, Connection con_d) {
     
     
    StringBuilder r = new StringBuilder();
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        
        int count = 0;
        String sql = ""
                + "SELECT r.reservee_name, p.user_identity, CASE WHEN r.parent_id IS NULL THEN true ELSE false END AS master "
                + "FROM reservations r "
                + "LEFT OUTER JOIN people p ON p.id = r.person_id "
                + "WHERE r.organization_id = ? AND r.reservation_number = ? "
                + "ORDER BY master DESC, r.check_number, r.id ASC;";

        pstmt = con_d.prepareStatement (sql);
        pstmt.setInt(1, organization_id);
        pstmt.setInt(2, reservation_number);
        
        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            if (count > 0) r.append(", ");
            r.append(rs.getString("reservee_name"));
            if (rs.getString("user_identity") != null && !rs.getString("user_identity").isEmpty()) {
                r.append(" (");
                r.append(rs.getString("user_identity").substring(0, rs.getString("user_identity").indexOf(":")).trim());
                r.append(")");
            }
            count++;
        }
        
        r.append(":" + count);
        
    } catch (Exception exc) {

        Utilities.logError("Proshop_report_dining.getReservationNameData: Err=" + exc.toString() + "");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return r.toString();
    
 }
 
 private List<List> getReportData() {
     
     
    List<List> r = new ArrayList<List>();

    
    return r;
    
 }
 
 
 
 /*
 
SELECT r.organization_id, r.date, to_char(r.date, 'YYYYMMDD')::int AS date_int, r.time, to_char(r.time, 'HH24MI') AS time_int, r.reservation_number, r.reservee_name, p.user_identity, r.check_number, r.charges, e.name AS event_name, m.name AS meal_period_name, l.name AS location_name, r.special_requests, r.member_special_requests, CASE WHEN r.parent_id IS NULL THEN true ELSE false END AS master 
FROM reservations r 
LEFT OUTER JOIN people p ON p.id = r.person_id 
LEFT OUTER JOIN locations l ON l.id = r.location_id 
LEFT OUTER JOIN meal_periods m ON m.id = r.meal_period_id 
LEFT OUTER JOIN events e ON e.id = r.event_id 
WHERE r.state <> 'cancelled' AND r.event_id = 5031 AND e.cancelled = false  
ORDER BY date, time, m.name, l.name, reservation_number, master DESC;
 
 
SELECT r.id, r.reservation_number, r.reservee_name, p.user_identity, CASE WHEN r.parent_id IS NULL THEN true ELSE false END AS master 
FROM reservations r 
LEFT OUTER JOIN people p ON p.id = r.person_id 
WHERE r.organization_id = 74 AND reservation_number = 972
ORDER BY master DESC, r.id ASC;
 
 
 */
}