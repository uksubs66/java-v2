/***************************************************************************************     
 *   Proshop_customsheets:  This servlet will implement the custom sheets functionality
 *
 *
 *
 *   called by:  proshop menu (doGet)
 *
 *   created: 01/10/2008   Paul S.
 *
 *
 *   last updated:
 *
 *       11/13/13   Added 30 and 60 minute options to the drop-down for number of minutes between tee times.
 *        8/23/13   Added processing in implementCustomSheet to check the dates configured in the custom tee sheet.  If the start date is earlier
 *                  than today, then use today's date for the start date.  Also, check the end date to make sure there are some days to process.
 *                  Problem occurred when we tried to change a custom tee sheet that had a start date that was more than 365 days previous to today.
 *        9/22/11   Indian Ridge CC (indianridgecc) - Added custom to order custom tee sheets by course and then by day of the week.
 *        6/23/09   Increase # of max minutes between times from 15 to 20
 *        8/19/08   Fixed implementCustomSheet so that it only affects today + 365 days
 *        8/11/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        7/16/08   Add hour defaulting and anchor jumping to make it quicker to use
 *        7/15/08   Fixed bug in creating new custom tee sheets
 *        3/24/08   Hide course seletion if not applicable
 *        3/03/08   Re-enable Customize button when coping/editing CTS  
 *        2/27/08   Fix diplay of AM/PM for 12PM & 12AM times in customizeSheet()
 *        2/20/08   Add support for 'Every other week' parameter for custom tee sheets
 *
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.util.*;
import java.sql.*;

import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Proshop_customsheets extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //**********************************************************
 //
 // Process the initial request from Proshop menu
 //
 //   parms passed:  none
 //
 //**********************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }
   
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
        SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
    }
     
    // Define parms
    //String course = "";         // course names
    int multi = 0;              // multiple course support indicator

    String club = (String)session.getAttribute("club");      // get club name
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    out.println(SystemUtils.HeadTitle(""));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
    //
    // Make sure the club is setup and there is at least one course configured
    //
    try {
      
        boolean tmp_test = false;
        int tmp_count = 0;

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi, (SELECT COUNT(*) FROM clubparm2 AS c) FROM club5 WHERE clubName != ''");

        if (rs.next()) {

            multi = rs.getInt(1);
            tmp_count = rs.getInt(2);

        } else {

            tmp_test = true;

        }

        if (tmp_count == 0) tmp_test = true;

        stmt.close();

        if (tmp_test) {

            // not setup yet - inform user to start with club setup 

            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
            out.println("<BR>The club setup has not been completed.");
            out.println("<BR>Please return to Configuration and select Club Setup.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
        }

    } catch (Exception exc) {

        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
        out.println("<BR>Please try again later.");
        out.println("<BR><br>Exception: " + exc.getMessage());
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;

    }

    // done with setup - start page output
    
    
    String todo = "edit";
    
    if (req.getParameter("new") != null) {
        todo = "new" ;
    } else if (req.getParameter("edit") != null) {
        todo = "edit"; 
    } else if (req.getParameter("copy") != null) {
        todo = "copy";
    } else if (req.getParameter("customize") != null) {
        todo = "customize";
    } else if (req.getParameter("implement") != null) {
        todo = "implement";
    } else if (req.getParameter("delete") != null) {
        todo = "delete";
    } else if (req.getParameter("insert") != null) {
        todo = "insert";
    } else if (req.getParameter("insert2") != null) {
        todo = "insert2";
    } else if (req.getParameter("changeFB") != null) {
        todo = "changefb";
    }

    
    if (todo.equals("menu")) {
        
        out.println("<h3 align=center>Custom Tee Sheets</h3>");
        out.println("<br>");
        out.println("<table align=center width=640>");
        out.println("" +
        "<tr>" +
        "<td align=center><a href=\"?new\">Build New</a></td>" +
        "<td align=center><a href=\"?edit\">Edit Existing</a></td>" +
        "<td align=center><a href=\"?copy\">Copy Existing</a></td>" +
        "</tr>");
        out.println("</table>");
        
        return;
    } // end if menu
    
   
    if (todo.equals("copy")) {
        
        int jump = 0;
        int sheet_id = 0;
        int tmp_id = 0;
        String tmp_name = "";
        String sheet_name = "";
        String tmp = "";
        
        if (req.getParameter("sheet_name") != null) {
            sheet_name = req.getParameter("sheet_name").trim();
            if (sheet_name.equals("")) jump = 1; // if passed but empty set jump to 1
        }
        if (req.getParameter("source") != null) {
            tmp = req.getParameter("source").trim();
            try {
                sheet_id = Integer.parseInt(tmp);
            } catch (Exception ignore) {}
            if (sheet_id == 0) jump = 3;
        }
        
        if (!sheet_name.equals("") && sheet_id != 0) {

            try {

                PreparedStatement pstmt = con.prepareStatement (
                  "SELECT custom_sheet_id FROM custom_sheets WHERE name = ?");

                pstmt.clearParameters();
                pstmt.setString(1, sheet_name);         // desired name of new sheet
                rs = pstmt.executeQuery();
                if (rs.next()) jump = 2;                // name found go select new name
                pstmt.close();
                
            } catch (Exception exc) {
                
                SystemUtils.buildDatabaseErrMsg("Error checking sheet name.", exc.toString(), out, false);
            }
            
            if (jump != 2) {
            
                if (doCopy(sheet_id, sheet_name, con, out)) {

                    int tid = getSheetId(sheet_name, con, out);
                    
                    out.println("<br>");
                    out.println("<p align=center style=\"size:16px\"><b>Custom Tee Sheet Has Been Copied</b></p>");
                    out.println("<table width=550 align=center><tr><td align=center>" +
                            "Although the configuration of the custom tee sheet has been copied, " +
                            "it has NOT been applied to your tee sheets yet.  After clicking 'Continue' you'll be taken " +
                            "to the Edit Custom Tee Sheet page where you can change the configuration as needed.  Once you've " +
                            "completed the changes make sure you either click 'Rebuild' or 'Customize' so the custom tee sheet " +
                            "can be applied to your current tee sheets.</td></tr></table>");

                    out.println("<br>");
                    out.println("<center>");
                    out.println("<form>");
                    out.println("<input type=hidden name=edit>");
                    out.println("<input type=hidden name=sheet_id value=\"" + tid + "\">");
                    out.println("<input type=submit value=\"Continue\">");
                    out.println("</form>");
                    out.println("</center>");
                    
                } else {

                    out.println("<br>");
                    out.println("<p align=center style=\"size:16px\"><b><font color=red>Custom Tee Sheet Copy Failed</font></b></p>");

                    out.println("<center>");
                    out.println("<form>");
                    out.println("<input type=hidden name=edit>");
                    out.println("<input type=submit value=\"Return\">");
                    out.println("</form>");
                    out.println("</center>");
                    
                }

                return;
            
            }
        }
        
        
        out.println("<br>");
        out.println("<h3 align=center>Copy Custom Tee Sheets</h3>");
        out.println("<br>");
        out.println("<table align=center border=0>");

        if (jump == 3) {
            out.println("<td colspan=3 align=center><font color=red><i><b>You must select a custom tee sheet to copy.</b></i></font></td>");
        } else if (jump == 2) {
            out.println("<td colspan=3 align=center><font color=red><i><b>The new name you provided already exists.</b></i></font></td>");
        } else if (jump == 1) {
            out.println("<td colspan=3 align=center><font color=red><i><b>You must provide a unique name for the duplicate sheet.</b></i></font></td>");
        }

        out.println("<form>");
        out.println("<tr>");
        out.println("<td>");
        out.println("<input type=hidden name=copy>");
        out.println("Select sheet to copy:<br>");
        out.println("<select name=source>");
        out.println("<option value=\"\">Choose...");

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT custom_sheet_id, name FROM custom_sheets ORDER BY name ASC");

            while (rs.next()) {

                tmp_id = rs.getInt(1);
                tmp_name = rs.getString(2);
                out.println("<option value=\"" + tmp_id + "\"" + ((tmp_id == sheet_id) ? "selected" : "") + ">" + tmp_name);
            }

            stmt.close();

        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error loading existing custom sheet names.", exc.toString(), out, false);
        }


        out.println("</select>");
        out.println("</td>");
        out.println("<td>&nbsp;</td>");
        out.println("<td>");
        out.println("Enter new name:<br>");
        out.println("<input type=text name=sheet_name value=\"" + sheet_name + "\"" + 
                    ((jump == 2) ? " style=\"background-color: yellow\"" : "") +
                    ">");
        out.println("</td>");

        out.println("</tr><tr>");

        out.println("<td colspan=3 align=center><input type=submit value=\" Copy \"></td>");
        out.println("</form>");

        out.println("</tr><tr>");

        out.println("<td colspan=3 align=center><br>");
        out.println("<form>");
        out.println("<input type=hidden name=menu>");
        out.println("<input type=submit value=\"Cancel\">");
        out.println("</form>");
        out.println("</td></tr>");
        out.println("</table>");

        return;
        
    } else if (todo.equals("edit-old")) {
        
        int sheet_id = 0;
        String tmp = "";
        int tmp_id = 0;
        String tmp_name = "";
        
        if (req.getParameter("sheet_id") != null) {
            tmp = req.getParameter("sheet_id").trim();
            try {
                sheet_id = Integer.parseInt(tmp);
            } catch (Exception ignore) {}
            
            if (sheet_id > 0) {
                
                buildForm(sheet_id, "", req, con, out);
                return;
                
            } else {
                
                out.println("<p align=center><font color=red><i><b>You must select the custom tee sheet you wish to edit.</b></i></font></p>");

                out.println("<center>");
                out.println("<form>");
                out.println("<input type=hidden name=menu>");
                out.println("<input type=submit value=\"Return\">");
                out.println("</form>");
                out.println("</center>");
            }
            
        } // end if sheet_id was passed
        
        out.println("<h3 align=center>Edit Custom Tee Sheets</h3>");
        out.println("<br><center>");
                
        out.println("<form>");
        out.println("<input type=hidden name=edit>");
        
        out.println("<select name=sheet_id>");
        out.println("<option value=\"\">Choose...");

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT custom_sheet_id, name FROM custom_sheets ORDER BY name ASC");

            while (rs.next()) {

                tmp_id = rs.getInt(1);
                tmp_name = rs.getString(2);
                out.println("<option value=\"" + tmp_id + "\"" + ((tmp_id == sheet_id) ? "selected" : "") + ">" + tmp_name);
            }

            stmt.close();

        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error loading existing custom sheet names.", exc.toString(), out, false);
        }


        out.println("</select>");
        
        out.println("&nbsp; &nbsp;<input type=submit value=\" Edit \">");
        out.println("</form>");
        
        out.println("<br><form>");
        out.println("<input type=hidden name=menu>");
        out.println("<input type=submit value=\"Return\">");
        out.println("</form>");
        out.println("</center>");
        
        
    } else if (todo.equals("edit")) {
        
        int sheet_id = 0;
        String tmp = "";
        int tmp_id = 0;
        String tmp_name = "";
        String tmp_course = "";
        String tmp_sdate = "";
        String tmp_edate = "";
        String tmp_recurrence = "Every ";
        
        if (req.getParameter("sheet_id") != null) {
            tmp = req.getParameter("sheet_id").trim();
            try {
                sheet_id = Integer.parseInt(tmp);
            } catch (Exception ignore) {}
            
            if (sheet_id > 0) {
                
                buildForm(sheet_id, "", req, con, out);
                return;
            }
            
        } // end if sheet_id was passed
        
        out.println("<br>");
        out.println("<h3 align=center>Edit Custom Tee Sheets</h3>");
        out.println("<br><center>");
        
        out.println("<table align=center border=1 bgcolor=\"#F5F5DC\" width=800>");
        
        out.println("<tr bgcolor=\"#8B8970\" align=center style=\"color: black; font-weight: bold\">" +
                    "<td height=35>Name</td>" +
                    ((multi == 0) ? "" : "<td>Course</td>") +
                    "<td>&nbsp;Start Date&nbsp;</td><td>&nbsp;End Date&nbsp;</td><td>Recurrence</td><td>&nbsp;</td></tr>");

        try {

            String orderBy = "name ASC";

            if (club.equals("indianridgecc")) {
                orderBy = "course, sunday DESC, monday DESC, tuesday DESC, wednesday DESC, thursday DESC, friday DESC, saturday DESC";
            }

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT *, " +
                        "DATE_FORMAT(start_date, '%c/%e/%Y') AS sdate, " +
                        "DATE_FORMAT(end_date, '%c/%e/%Y') AS edate " +
                    "FROM custom_sheets " +
                    "ORDER BY " + orderBy);

            while (rs.next()) {
                
                tmp_recurrence = "";
                tmp_id = rs.getInt("custom_sheet_id");
                tmp_name = rs.getString("name");
                tmp_course = rs.getString("course");
                tmp_sdate = rs.getString("sdate");
                tmp_edate = rs.getString("edate");
                if (rs.getInt("sunday") == 1) tmp_recurrence += "Sun, ";
                if (rs.getInt("monday") == 1) tmp_recurrence += "Mon, ";
                if (rs.getInt("tuesday") == 1) tmp_recurrence += "Tue, ";
                if (rs.getInt("wednesday") == 1) tmp_recurrence += "Wed, ";
                if (rs.getInt("thursday") == 1) tmp_recurrence += "Thu, ";
                if (rs.getInt("friday") == 1) tmp_recurrence += "Fri, ";
                if (rs.getInt("saturday") == 1) tmp_recurrence += "Sat, ";
                tmp_recurrence = tmp_recurrence.substring(0, tmp_recurrence.length() - 2);
                
                if (rs.getInt("eo_week") == 1) {
                    
                    tmp_recurrence += " <nobr>Every Other Week</nobr>";
                }
                
                out.println("<tr align=center height=50><form>" +
                            "<td>" + tmp_name + "</td>" +
                            ((multi == 0) ? "" : "<td>" + tmp_course + "</td>") +
                            "<td>" + tmp_sdate + "</td>" +
                            "<td>" + tmp_edate + "</td><td>" + tmp_recurrence + "</td>" +
                            "<td><input type=hidden name=edit><input type=hidden name=sheet_id value=\"" + tmp_id + "\">" + 
                            "<input type=submit value=\" Select \"></td></form></tr>");
            }
            
            stmt.close();
        
        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error loading existing custom sheet names.", exc.toString(), out, false);
        }


        out.println("</table>");
                
        out.println("<br><form method=\"GET\" action=\"Proshop_announce\">");
        //out.println("<br><form>");
        //out.println("<input type=hidden name=menu>");
        //out.println("<input type=submit value=\"Return\">");
        out.println("<input type=submit value=\"Home\">");
        out.println("</form>");
        out.println("</center>");
        
        
    } else if (todo.equals("new")) {
        
        buildForm(0, "", req, con, out);
        
    } else if (todo.equals("delete")) {
                
        int tid = 0;
        String tmp = req.getParameter("tid");
        String name = req.getParameter("name");
        String msg = "";
        
        if (tmp != null) {

            try {

                tid = Integer.parseInt(tmp);

                PreparedStatement pstmt2 = con.prepareStatement (
                    "DELETE FROM custom_tee_times WHERE custom_tee_time_id = ?");

                pstmt2.clearParameters();
                pstmt2.setLong(1, tid);
                int count = pstmt2.executeUpdate();
                pstmt2.close();

                if (count == 0) {
                    msg = "Delete Failed";
                } else {
                    msg = "Delete Successful";
                }

            } catch (Exception exc) {

                SystemUtils.buildDatabaseErrMsg("Error deleting custom tee time. (" + tid + ")", exc.toString(), out, false);
            }

            customizeSheet(name, msg, false, con, out, req);
            return;
        
        } else {
            
            
            try {

                tid = getSheetId(name, con, out);

                PreparedStatement pstmt2 = con.prepareStatement (
                    "DELETE FROM custom_sheets WHERE custom_sheet_id = ?");

                pstmt2.clearParameters();
                pstmt2.setLong(1, tid);
                int count = pstmt2.executeUpdate();
                pstmt2.close();

                if (count == 0) {
                    
                    msg = "Delete Failed";
                    
                } else {
                    
                    msg = "Delete Successful";
                    
                    pstmt2 = con.prepareStatement (
                        "DELETE FROM custom_tee_times WHERE custom_sheet_id = ?");

                    pstmt2.clearParameters();
                    pstmt2.setLong(1, tid);
                    count = pstmt2.executeUpdate();
                    pstmt2.close();
                    if (count == 0) {

                        msg = "Delete did NOT fully succeed.";

                    }
                }
                
                out.println("<br>");
                out.println("<h3 align=center>Edit Custom Tee Sheets</h3>");
                out.println("<p align=center>" + msg + "</p>");
                out.println("<center><form><input type=hidden name=edit>");
                out.println("<input type=submit value=\"Continue\"></form></center>");
                
                return;
                
            } catch (Exception exc) {

                SystemUtils.buildDatabaseErrMsg("Error deleting custom tee sheet. (" + tid + ")", exc.toString(), out, false);
            }
            
        }
        
    } else if (todo.equals("insert2")) {
        
        String shr = req.getParameter("hr");
        String smin = req.getParameter("min");
        String ampm = req.getParameter("ampm");
        String sfb = req.getParameter("fb");
        String name = req.getParameter("name");
        String msg = "";
        
        int sheet_id = getSheetId(name, con, out);
        int hr = 0;
        int min = 0;
        int time = 0;
        int fb = 0;
        
        if (sfb.equals("B")) {
            fb = 1;
        } else if (sfb.equals("O")) {
            fb = 9;
        }
        
        try {

            hr = Integer.parseInt(shr);
            min = Integer.parseInt(smin);
        } catch (Exception exc) {}

        if (min > 59 || min < 0) {
            
            msg = "Minutes out of range.";
            customizeSheet(name, msg, true, con, out, req);
            return;
        }
        
        // adjust for military times
        if (ampm.equalsIgnoreCase("PM") && hr != 12) hr += 12;
        if (ampm.equalsIgnoreCase("AM") && hr == 12) hr = 0;
        
        time = (hr * 100) + min;
        
        // perform insert
        try {
                        
            PreparedStatement pstmt2 = con.prepareStatement (
                "INSERT INTO custom_tee_times VALUES (NULL, ?, ?, ?)");

            pstmt2.clearParameters();
            pstmt2.setLong(1, sheet_id);
            pstmt2.setLong(2, time);
            pstmt2.setLong(3, fb);
            int count = pstmt2.executeUpdate();
            pstmt2.close();
            
        } catch (SQLException sql) {
        
            if (sql.getMessage().startsWith("Duplicate entry")) {
                msg = "Tee Time Already Exists";
            } else {
                msg = sql.getMessage();
            }
            
        } catch (Exception exc) {
        
            SystemUtils.buildDatabaseErrMsg("Error inserting custom tee time. (" + exc.getMessage() + ")", exc.toString(), out, false);
        }
        
        customizeSheet(name, msg, false, con, out, req);
        
        
    } else if (todo.equals("customize") || todo.equals("insert")) {
        
        String name = req.getParameter("name");
        customizeSheet(name, "", todo.equals("insert"), con, out, req);
        
    } else if (todo.equals("changefb")) {
        
        // change fb
        
        String sfb = req.getParameter("fb");
        String name = req.getParameter("name");
        String stid = req.getParameter("tid");
        int fb = 0;
        int tid = 0;
        
        try {

            tid = Integer.parseInt(stid);
        } catch (Exception exc) {}
        
        if (sfb.equals("B")) {
            fb = 1;
        } else if (sfb.equals("O")) {
            fb = 9;
        }
        
        // perform update
        try {
                        
            PreparedStatement pstmt2 = con.prepareStatement (
                "UPDATE custom_tee_times SET fb = ? WHERE custom_tee_time_id = ?");

            pstmt2.clearParameters();
            pstmt2.setInt(1, fb);
            pstmt2.setInt(2, tid);
            int count = pstmt2.executeUpdate();
            pstmt2.close();
            
        } catch (Exception exc) {
        
            SystemUtils.buildDatabaseErrMsg("Error updating FB for custom tee time " + tid, exc.toString(), out, false);
        }
        
        customizeSheet(name, "F/B Changed", false, con, out, req);
        
    } else if (todo.equals("implement")) {
        
        String name = req.getParameter("name");
        
        //if (req.getParameter("skip_rebuild") == null) populateCustomTeeTable(name, con, out);
        
        implementCustomSheet(name, con, out);
        
    }
   
 } // end doGet
 
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    out.println(SystemUtils.HeadTitle(""));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }
   
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_TEESHEETS", con, out)) {
        SystemUtils.restrictProshop("SYSCONFIG_TEESHEETS", out);
    }
     
    //
    // define variables
    //
    
    String tmp = "";
    String sql = "";
    
    int startYear = 0;
    int startMonth = 0;
    int startDay = 0;
    int endYear = 0;
    int endMonth = 0;
    int endDay = 0;
    
    int start_date = 0;
    int end_date = 0;
    int first_hr = 0;
    int first_min = 0;
    int last_hr = 0;
    int last_min = 0;
    int mins = 0;
    int alt_mins = 0;
    int stime = 0;
    int etime = 0;
    int sheet_id = 0;
    
    int sun = (req.getParameter("sun") != null) ? 1 : 0;
    int mon = (req.getParameter("mon") != null) ? 1 : 0;
    int tue = (req.getParameter("tue") != null) ? 1 : 0;
    int wed = (req.getParameter("wed") != null) ? 1 : 0;
    int thu = (req.getParameter("thu") != null) ? 1 : 0;
    int fri = (req.getParameter("fri") != null) ? 1 : 0;
    int sat = (req.getParameter("sat") != null) ? 1 : 0;
    int eo_week = (req.getParameter("eo_week") != null) ? 1 : 0;
    
    String syear = req.getParameter("syear");
    String smonth = req.getParameter("smonth");
    String sday = req.getParameter("sday");
    String eyear = req.getParameter("eyear");
    String emonth = req.getParameter("emonth");
    String eday = req.getParameter("eday");
    
    String sfirst_hr = req.getParameter("first_hr");
    String sfirst_min = req.getParameter("first_min").trim();
    String slast_hr = req.getParameter("last_hr");
    String slast_min = req.getParameter("last_min").trim();
    String name = req.getParameter("name").trim();
    String course = req.getParameter("course").trim();
    
    try {
        
        first_hr = Integer.parseInt(sfirst_hr);
        first_min = Integer.parseInt(sfirst_min);
        last_hr = Integer.parseInt(slast_hr);
        last_min = Integer.parseInt(slast_min);
        
    } catch (Exception ignore) {}
    
    stime = (first_hr * 100) + first_min;
    etime = (last_hr * 100) + last_min;
    
    try {
        
        startYear = Integer.parseInt(syear);
        startMonth = Integer.parseInt(smonth);
        startDay = Integer.parseInt(sday);
        endYear = Integer.parseInt(eyear);
        endMonth = Integer.parseInt(emonth);
        endDay = Integer.parseInt(eday);
        
    } catch (Exception ignore) {}
    
    start_date = (startYear * 10000) + (startMonth * 100) + startDay;
    end_date = (endYear * 10000) + (endMonth * 100) + endDay;
    
    if (req.getParameter("sheet_id") != null) {
        tmp = req.getParameter("sheet_id").trim();
        try {
            sheet_id = Integer.parseInt(tmp);
        } catch (Exception ignore) {}
    }
    
    tmp = req.getParameter("mins");
    try {
        mins = Integer.parseInt(tmp);
    } catch (Exception ignore) {}
    
    tmp = req.getParameter("alt_mins");
    try {
        alt_mins = Integer.parseInt(tmp);
    } catch (Exception ignore) {}
    
    
   
    // START VERIFICATION
    
    
    
    //
    // Make sure there is a name 
    //
    if (name.equals("")) {
        
        buildForm(sheet_id, "Must specify a name for the custom tee sheet.", req, con, out);
        return;
    }
    
    //
    // Make sure the name is unique
    //
    try {
        
        if (sheet_id == 0) {
            // see if this name is being used an any custom sheets
            sql = "SELECT * FROM custom_sheets WHERE name = ?";
        } else {
            // see if this name is being used on a DIFFERENT custom sheet
            sql = "SELECT * FROM custom_sheets WHERE name = ? AND custom_sheet_id <> ?";
        }
        
        PreparedStatement pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        pstmt.setString(1, name);
        if (sheet_id != 0) pstmt.setInt(2, sheet_id);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            
            // if any rows returned, the name passed here is already in use
            buildForm(sheet_id, "Name is already in use.", req, con, out);
            return;
        }
        
    } catch(Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Failed to validate name.", exc.toString(), out, false);
        return;
    }
    
    //
    // Make sure the ending date is after the starting date
    //
    if (start_date > end_date) {
        
        buildForm(sheet_id, "Starting date must be before the ending date.", req, con, out);
        return;
    }
    
    //
    // Make sure there is at least one day selected for recurrence
    //
    if (mon + tue + wed + thu + fri + sat + sun == 0) {
        
        buildForm(sheet_id, "Must select at least one day for recurrence.", req, con, out);
        return;
    }
    
    //
    // Check to see if this CTS conflicts with an existing CTS
    //
    try {
        
        sql = "SELECT custom_sheet_id, name, eo_week, " +
                "MOD(DATE_FORMAT(start_date, '%U'), 2) AS odd, " +
                "MOD(DATE_FORMAT(?, '%U'), 2) AS new_odd " + 
              "FROM custom_sheets " + 
              "WHERE course = ? AND (" +
              ((sun == 1) ? "sunday = 1 OR " : "") +
              ((mon == 1) ? "monday = 1 OR " : "") +
              ((tue == 1) ? "tuesday = 1 OR " : "") +
              ((wed == 1) ? "wednesday = 1 OR " : "") +
              ((thu == 1) ? "thursday = 1 OR " : "") +
              ((fri == 1) ? "friday = 1 OR " : "") +
              ((sat == 1) ? "saturday = 1 OR " : "") +
              "1 = 2) AND start_date <= ? AND end_date >= ? " + // 1=2 is so we don't have to trim the last OR
              ((sheet_id != 0) ? " AND custom_sheet_id <> ?" : ""); 
        
        //out.println("<!-- " + sql + " -->");
        
        PreparedStatement pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        pstmt.setLong(1, start_date);
        pstmt.setString(2, course);
        pstmt.setLong(3, start_date);
        pstmt.setLong(4, end_date);
        if (sheet_id != 0) pstmt.setInt(5, sheet_id);
        ResultSet rs = pstmt.executeQuery();
        
        //loop1:
        while (rs.next()) {
            
            // if any rows returned, then there is potentially a conflict with an existing CTS
            // need to check the eo_week values next to know for sure
            
            if (eo_week == 0 || (eo_week == rs.getInt("eo_week") && rs.getInt("odd") == rs.getInt("new_odd")) ) {
                
                buildForm(sheet_id, "Parameters conflict with an exising custom tee sheet " + rs.getString("name") + ".", req, con, out);
                //break loop1;
                return;
            }
        }
        
    } catch(Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Failed conflict testing.", exc.toString(), out, false);
        return;
    }
    
    
    // END VERIFICATION
    
    
    if ( sheet_id == 0 ) {
        
        // save new custom tee sheet
        sql = "INSERT INTO custom_sheets " +
                   "(name, course, start_date, end_date, sunday, monday, tuesday, wednesday, thursday, friday, saturday, stime, etime, alt, betwn, eo_week) " +
               "VALUES " +
                   "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
    } else {
        
        // update existing custom tee sheet
        sql = "UPDATE custom_sheets " +
              "SET name = ?, course = ?, start_date = ?, end_date = ?, sunday = ?, monday = ?, tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ?, stime = ?, etime = ?, alt = ?, betwn = ?, eo_week = ? " +
              "WHERE custom_sheet_id = ?";
        
    }
    
    try {
    
        PreparedStatement pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.setString(2, course);
        pstmt.setInt(3, start_date);
        pstmt.setInt(4, end_date);
        pstmt.setInt(5, sun);
        pstmt.setInt(6, mon);
        pstmt.setInt(7, tue);
        pstmt.setInt(8, wed);
        pstmt.setInt(9, thu);
        pstmt.setInt(10, fri);
        pstmt.setInt(11, sat);
        pstmt.setInt(12, stime);
        pstmt.setInt(13, etime);
        pstmt.setInt(14, alt_mins);
        pstmt.setInt(15, mins);
        pstmt.setInt(16, eo_week);
        if (sheet_id != 0) pstmt.setInt(17, sheet_id);
        pstmt.executeUpdate();
        pstmt.close();
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Failed to save data.", exc.toString(), out, false);
        return;
    }
    
    
    // populate the custom_tee_times table
    if (sheet_id == 0) {
        populateCustomTeeTable(name, con, out);
    } else if (req.getParameter("btnSubmit") != null && req.getParameter("btnSubmit").equals("Rebuild")) {
        populateCustomTeeTable(name, con, out);
    } else if (req.getParameter("btnSubmit") != null && req.getParameter("btnSubmit").equals("Customize")) {
        customizeSheet(name, "", false, con, out, req);        
        return;
    }
    
/*    
    if (req.getParameter("customize") != null) {
        
        customizeSheet(name, "", false, con, out);        
        return;
    }
*/    
    
    
    out.println("<br>");
    
    if (sheet_id == 0) {
        out.println("<h3 align=center>New Custom Tee Sheet Created</h3>");
    } else {
        out.println("<h3 align=center>Custom Tee Sheet Updated</h3>");
    }
    
    out.println("<p align=center>Do you wish to further customize your tee sheets?</p>");

    out.println("<table align=center><tr>");
    out.println("<form><td>");
    out.println("<input type=hidden name=implement>");
    out.println("<input type=hidden name=name value=\"" + name + "\">");
    out.println("<input type=submit value=\"No\">");
    out.println("</td></form>");
    out.println("<td>&nbsp; &nbsp;</td>");
    out.println("<form><td>");
    out.println("<input type=hidden name=customize>");
    out.println("<input type=hidden name=name value=\"" + name + "\">");
    out.println("<input type=submit value=\"Yes\">");
    out.println("</td></form>");
    out.println("</tr></table>");
    
 } // end doPost
 
 
 //
 // Applies a custom tee sheet to the teecurr2 table
 //
 // This method loops thru all days in the date ranage for said custom tee sheet and if
 // there are no existing tee times on a day it calls SystemUtils.buildCustomTees() 
 // to apply the custom tee sheet for specific days
 //
 private static void implementCustomSheet(String name, Connection con, PrintWriter out) {
     
     
    out.println("<center>");
    out.println("<h3 align=center>Applying Custom Template to Tee Sheets</h3>");
        
    int mon = 0;
    int tue = 0;
    int wed = 0;
    int thu = 0;
    int fri = 0;
    int sat = 0;
    int sun = 0;
    int eo_week = 0;
    int weekday = 0;
    int start_date = 0;
    int end_date = 0;
    int sheet_id = 0;
    int add_days = 0;
    int count = 0;
    int start_week = 0;
    int this_week = 0;
    
    long thisdate = 0;

    String course = "";

    boolean skip = false;
    boolean bookedTimes = false;

    // load values for CTS
    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT *, " +
                "DATE_FORMAT(start_date, '%Y') AS syear, " +
                "DATE_FORMAT(start_date, '%c') AS smonth, " +
                "DATE_FORMAT(start_date, '%e') AS sday, " +
                "DATE_FORMAT(end_date, '%Y') AS eyear, " +
                "DATE_FORMAT(end_date, '%c') AS emonth, " +
                "DATE_FORMAT(end_date, '%e') AS eday, " +
                "MOD(DATE_FORMAT(start_date, '%U'), 2) AS start_week " +
            "FROM custom_sheets " +
            "WHERE name = ?");

        pstmt.clearParameters();
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {

            sheet_id = rs.getInt("custom_sheet_id");
            course = rs.getString("course");
            start_date = rs.getInt("start_date");
            end_date = rs.getInt("end_date");
            sun = rs.getInt("sunday");
            mon = rs.getInt("monday");
            tue = rs.getInt("tuesday");
            wed = rs.getInt("wednesday");
            thu = rs.getInt("thursday");
            fri = rs.getInt("friday");
            sat = rs.getInt("saturday");
            eo_week = rs.getInt("eo_week");
            start_week = rs.getInt("start_week");

        }
        
        pstmt.close();

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading custom tee sheet.", exc.toString(), out, false);
        return;
    }
    
    
    //
    //  Since we only process 365 days we must make sure that the start date is not too early.  Use today's date if the start date is older than today.
    //
    long today_date = Utilities.getDate(con);     // get today's date adjusted for time zone
    
    if (start_date < today_date) start_date = (int)today_date;  
    
    if (start_date <= end_date) {       // now make sure we have some days to process
    
      try {

         while (thisdate <= end_date && add_days < 365) { // added add_days check to make sure we don't try to process days that do not exist yet

               skip = true; // default to skipping this date unless it hits on a recurrence check

               // get thisdate set to the date we are working on
               PreparedStatement pstmt2 = con.prepareStatement("" +
                     "SELECT DATE_FORMAT(DATE_ADD(?, INTERVAL " + add_days + " DAY), '%Y%m%d') AS d1");
               pstmt2.clearParameters();
               pstmt2.setInt(1, start_date);
               ResultSet rs = pstmt2.executeQuery();
               if (rs.next()) thisdate = rs.getInt(1);
               pstmt2.close();

               // get the day of week for this working date (0-6)
               pstmt2 = con.prepareStatement("SELECT DATE_FORMAT(?, '%w') AS d2, MOD(DATE_FORMAT(?, '%U'), 2) AS this_week");
               pstmt2.clearParameters();
               pstmt2.setLong(1, thisdate);
               pstmt2.setLong(2, thisdate);
               rs = pstmt2.executeQuery();
               if (rs.next()) {
                  weekday = rs.getInt(1);
                  this_week = rs.getInt(2);
               }
               pstmt2.close();


               if ((weekday == 0 && sun == 1) ||
                  (weekday == 1 && mon == 1) ||
                  (weekday == 2 && tue == 1) ||
                  (weekday == 3 && wed == 1) ||
                  (weekday == 4 && thu == 1) ||
                  (weekday == 5 && fri == 1) ||
                  (weekday == 6 && sat == 1)) { skip = false; }

               // if start_week is 0 the the starting week for this CTS was an even week
               // and if this_week is 0 then this week is an even week
               // so if this CTS is for every other week and start_week != this_week then skip this day
               if (!skip) {
                  if (eo_week == 1 && (start_week != this_week)) skip = true;
               }

               if (thisdate > end_date) skip = true;

               if (!skip) {

                  // check to see if there are already tee times for this day
                  pstmt2 = con.prepareStatement(
                           "SELECT COUNT(*) FROM teecurr2 WHERE date = ? AND courseName = ? AND player1 <> ''");
                  pstmt2.clearParameters();
                  pstmt2.setLong(1, thisdate);
                  pstmt2.setString(2, course);
                  rs = pstmt2.executeQuery();
                  if (rs.next()) count = rs.getInt(1);

                  if (count == 0) {

                     // only empty tee times found - purge and rebuild
                     pstmt2 = con.prepareStatement(
                           "DELETE FROM teecurr2 WHERE date = ? AND courseName = ?");
                     pstmt2.clearParameters();
                     pstmt2.setLong(1, thisdate);
                     pstmt2.setString(2, course);
                     pstmt2.executeUpdate();

                     SystemUtils.buildCustomTees(sheet_id, thisdate, con);

                  } else {

                     // we found tee times that are booked - notify user
                     bookedTimes = true;
                     out.println("<br>Skipping " + thisdate + " due to " + count + " existing tee times!");

                  }

                  pstmt2.close();

               } // end if skip

               add_days++;

         } // end while loop for dates

      } catch (Exception exc) {

         SystemUtils.buildDatabaseErrMsg("Error implementing custom tee sheet (" + sheet_id + ") for " + thisdate, exc.toString(), out, false);
         return;
      }

      // apply blockers, events, restrictions & lotteries to new the modified tee sheets
      SystemUtils.updateTeecurr(con);

      if (bookedTimes) {

         out.println("" +
                  "<p>We were unable to fully implement your custom tee sheet template due to existing tee times assigned to players.  " +
                  "Please contact support and provide the list of dates (yyyymmdd) from above and we will manually update your tee sheets.</p>" +
                  "<p>You could also change the starting and ending dates of the template so as to skip the effected days.</p>");
      } else {

         out.println("<p>We have applied your custom tee sheet template to your existing tee sheets.</p>");
      }
      
    } else {
       
       //  end_date is earlier than today's date - error
       
         out.println("<p><strong>Error:</strong> There is a problem with the start and/or end date configured.<BR>Please correct the dates and try again.</p>");       
    }

    
    out.println("<form>");
    out.println("<td align=center>");
    out.println("<input type=hidden name=menu>");
    out.println("<input type=submit value=\"Continue\" " + ((bookedTimes) ? "onclick=\"return confirm('Have you recorded each of the dates (yyyymmdd) listed on the page?')\"" : "") + "></td>");
    out.println("</form>");
    out.println("</center>");
     
 }
 
 
 public static void buildForm(int sheet_id, String msg, HttpServletRequest req, Connection con, PrintWriter out) {

    long startYear = 0;
    long startMonth = 0;
    long startDay = 0;
    long endYear = 0;
    long endMonth = 0;
    long endDay = 0;
    
    //int startDate = 0;
    //int endDate = 0;

    int mon = 0;
    int tue = 0;
    int wed = 0;
    int thu = 0;
    int fri = 0;
    int sat = 0;
    int sun = 0;
    int eo_week = 0;

    int first_hr = 0;
    int first_min = 0;
    int last_hr = 0;
    int last_min = 0;
    int mins = 0;
    int alt_mins = 0;
    //int stime = 0;
    //int etime = 0;
    int i = 0;
    int multi = 0;
    
    String name = "";
    String course = "";
    String syear = "";
    String smonth = "";
    String sday = "";
    String eyear = "";
    String emonth = "";
    String eday = "";
    String sfirst_hr = "";
    String sfirst_min = "";
    String slast_hr = "";
    String slast_min = "";
    
    parmClub parm = new parmClub(0, con); // this is for golf only
    
    //
    // Get the club parms
    //
    try {

        getClub.getParms(con, parm);        // get the club parms
        multi = parm.multi;
        
    } catch (Exception ignore) { }
    
    
    if ( sheet_id != 0 ) {

        // load values for form
        try {

            PreparedStatement pstmt = con.prepareStatement (
                "SELECT *, " +
                    "DATE_FORMAT(start_date, '%Y') AS syear, " +
                    "DATE_FORMAT(start_date, '%c') AS smonth, " +
                    "DATE_FORMAT(start_date, '%e') AS sday, " +
                    "DATE_FORMAT(end_date, '%Y') AS eyear, " +
                    "DATE_FORMAT(end_date, '%c') AS emonth, " +
                    "DATE_FORMAT(end_date, '%e') AS eday " +
                "FROM custom_sheets " +
                "WHERE custom_sheet_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, sheet_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                
                sun = rs.getInt("sunday");
                mon = rs.getInt("monday");
                tue = rs.getInt("tuesday");
                wed = rs.getInt("wednesday");
                thu = rs.getInt("thursday");
                fri = rs.getInt("friday");
                sat = rs.getInt("saturday");
                eo_week = rs.getInt("eo_week");
                
                name = rs.getString("name");
                course = rs.getString("course");
                
                mins = rs.getInt("betwn");
                alt_mins = rs.getInt("alt");
                
                startYear = rs.getInt("syear");
                startMonth = rs.getInt("smonth");
                startDay = rs.getInt("sday");
                endYear = rs.getInt("eyear");
                endMonth = rs.getInt("emonth");
                endDay = rs.getInt("eday");
                
                i = rs.getInt("stime");
                first_hr = i / 100;
                first_min = i - (first_hr * 100);
                
                i = rs.getInt("etime");
                last_hr = (i / 100);
                last_min = i - (last_hr * 100);
                last_hr = last_hr - 12; // will always be PM
                
            }
            pstmt.close();

        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error loading custom tee sheet.", exc.toString(), out, false);
            return;
        }
        
    } else if (!msg.equals("")) {
        
        name = req.getParameter("name").trim();
        course = req.getParameter("course").trim();
        
        sun = (req.getParameter("sun") != null) ? 1 : 0;
        mon = (req.getParameter("mon") != null) ? 1 : 0;
        tue = (req.getParameter("tue") != null) ? 1 : 0;
        wed = (req.getParameter("wed") != null) ? 1 : 0;
        thu = (req.getParameter("thu") != null) ? 1 : 0;
        fri = (req.getParameter("fri") != null) ? 1 : 0;
        sat = (req.getParameter("sat") != null) ? 1 : 0;
        eo_week = (req.getParameter("eo_week") != null) ? 1 : 0;

        syear = req.getParameter("syear");
        smonth = req.getParameter("smonth");
        sday = req.getParameter("sday");
        eyear = req.getParameter("eyear");
        emonth = req.getParameter("emonth");
        eday = req.getParameter("eday");

        sfirst_hr = req.getParameter("first_hr");
        sfirst_min = req.getParameter("first_min").trim();
        slast_hr = req.getParameter("last_hr");
        slast_min = req.getParameter("last_min").trim();
        
        try {

            first_hr = Integer.parseInt(sfirst_hr);
            first_min = Integer.parseInt(sfirst_min);
            last_hr = Integer.parseInt(slast_hr);
            last_min = Integer.parseInt(slast_min);

            startYear = Integer.parseInt(syear);
            startMonth = Integer.parseInt(smonth);
            startDay = Integer.parseInt(sday);
            endYear = Integer.parseInt(eyear);
            endMonth = Integer.parseInt(emonth);
            endDay = Integer.parseInt(eday);

        } catch (Exception ignore) {}
        
    }
    
    out.println("<br>");
    
    out.println("<table width=550 cellpadding=15 bgcolor=#F5F5DC border=1 align=center><tr><td align=center>");
    if (sheet_id == 0) {
        
        out.println("<font size=3><b>New Custom Tee Sheet</b></font>");
        out.println("<p>" +
                    "Please be careful when making custom tee sheets.  The changes it will make to your tee sheets are " +
                    "not easily undone.  If you haven't used this feature before, please contact support so that we can " +
                    "assist you.</p>" +
                    "<p>Please be aware that we cannot apply custom tee sheets to days that already have " +
                    "tee times scheduled.</p>");
    } else {
        
        out.println("<font size=3><b>Edit Custom Tee Sheet</b></font>");
        out.println("<p>" +
                    "Please be aware that while we allow you to edit your existing custom tee sheets,<br>" +
                    "changes that have already been made to your tee sheets cannot be reversed!<br>" +
                    "Please contact our Support staff for additional assistance.</p>");
    }
    out.println("</td></tr></table>");
    
    if (!msg.equals("")) {
        out.println("<p align=center style=\"color:red;font-weight:bold\"><i>Error: " + msg + "</i></p>");
    } else {
        out.println("<br>");
    }
    
    out.println("<table width=550 cellpadding=15 bgcolor=#F5F5DC border=1 align=center><tr><td>");
    
    out.println("<form method=POST>");
    
    out.println("<input type=hidden name=sheet_id value=" + sheet_id + ">");

    out.println("&nbsp;&nbsp;Custom Sheet Name: &nbsp;<input type=text name=\"name\" value=\"" + name + "\"> *&nbsp; <font size=1>(must be unique)</font><br><br>");
    
    if (multi != 0) {
        Common_Config.displayCourseSelection(course, false, con, out);
    } else {
        out.println("<input type=\"hidden\" name=\"course\">");
    }
    
    Common_Config.displayStartDate(startMonth, startDay, startYear, out);

    Common_Config.displayEndDate(endMonth, endDay, endYear, out);

    Common_Config.displayRecurr(mon, tue, wed, thu, fri, sat, sun, out);
    
    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    if (eo_week == 1) {
        out.println("<input type=\"checkbox\" name=\"eo_week\" checked value=\"1\">&nbsp;&nbsp;Every Other Week");
    } else {
        out.println("<input type=\"checkbox\" name=\"eo_week\" value=\"1\">&nbsp;&nbsp;Every Other Week");
    }
    out.println("<br><br>");
    
    out.println("Time of day for first tee time: &nbsp;&nbsp; hr &nbsp;");
    out.println("<select size=\"1\" name=\"first_hr\">");
    for (i=5;i<=9;i++) {
        Common_Config.buildOption(i, i, first_hr, out);
    }
    out.println("</select>");
    
    out.println("&nbsp; min &nbsp;");
    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"" + SystemUtils.ensureDoubleDigit(first_min) + "\" name=\"first_min\">");

    out.println("&nbsp;&nbsp;AM");
        
    out.println("<br><br>");
    
    out.println("Time of day for last tee time: &nbsp;&nbsp; hr &nbsp;");
    out.println("<select size=\"1\" name=\"last_hr\">");
    for (i=5;i<=9;i++) {
        Common_Config.buildOption(i + 12, i, last_hr + 12, out);
    }
    out.println("</select>");
    
    out.println("&nbsp; min &nbsp;");
    out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"" + SystemUtils.ensureDoubleDigit(last_min) + "\" name=\"last_min\">");

    out.println("&nbsp;&nbsp;PM");
    
    out.println("<br><br>");
    
    out.println("Number of minutes between tee times: &nbsp;&nbsp;");
    out.println("&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"mins\">");
    for (i=3;i<=20;i++) {
        
        Common_Config.buildOption(i, i, mins, out);
    }
    Common_Config.buildOption(30, 30, mins, out);
    Common_Config.buildOption(60, 60, mins, out);
    
    out.println("</select>");
    
    out.println("<br><br>");
    
    out.println("Alternate minutes between tee times: &nbsp;&nbsp;");
    out.println("&nbsp;&nbsp;");
    out.println("<select size=\"1\" name=\"alt_mins\">");
    out.println("<option value=0>No</option>");
    for (i=3;i<=15;i++) {
        
        Common_Config.buildOption(i, i, alt_mins, out);
    }
    out.println("</select>");
    out.println("&nbsp;&nbsp;Zero (0) for 'NO'");
               
    
    out.println("<br><br>");
    
    out.println("<table align=center border=0><tr><td align=center>");

    if ((sheet_id == 0)) {
        out.println("<input type=submit value=\"Create\"></td>");
    } else {
        out.println("<input type=submit name=btnSubmit value=\"Rebuild\" onclick=\"return confirm('If you continue, any existing customization will be lost and the custom tee sheet will be rebuilt using the new parameters.')\">");
        out.println(" &nbsp; ");
        out.println("<input type=submit name=btnSubmit value=\"Customize\" onclick=\"return confirm('If you continue, any changes you made to the starting and ending tee times as well as alternating minutes will be ignored.')\"></td>");
    }

    out.println("</form>");

    out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td>");

    out.println("<form>");
    out.println("<td align=center>");
    out.println("<input type=hidden name=menu>");
    out.println("<input type=submit value=\"Cancel\"></td>");
    out.println("</form>");

    out.println("</tr>");
    
    
    // if editing then allow a way to jump to customization (so we don't delete & rebuild)
    if (sheet_id != 0) {
        
        out.println("<tr><td>&nbsp;</td></tr>"); // spacer row
/*        
        out.println("<tr><td align=center><form>");
        out.println("<input type=hidden name=customize>");
        out.println("<input type=hidden name=name value=\"" + name + "\">");
        out.println("<input type=submit value=\"Customize\"></td></form>");
        out.println("<td></td>");
*/
        out.println("<tr><td colspan=3 align=center><form>");
        //out.println("<input type=hidden name=delete>");
        //out.println("<input type=hidden name=name value=\"" + name + "\">");
        //  return confirm('Are you sure you want to delete this custom tee sheet.  Note that deleting this will NOT undo the changes it made to your tee sheets!')
        out.println("<input type=button value=\"Disable\" onclick=\"alert('If you wish to stop this custom tee sheet from processing, please change the ending date to today.  If you need to undo the changes it has made to your tee sheets, please contact Support.')\"></td>");
        out.println("</form>");
        out.println("</tr>");
    }
    
    out.println("</table></td></tr></table>");
    
    out.println("<br>");
 }
 
 
 private static boolean doCopy(int sheet_id, String new_name, Connection con, PrintWriter out) {
    
    
    int count = 0;
    int new_sheet_id = 0;
    
    try {
                        
        PreparedStatement pstmt2 = con.prepareStatement (
            "INSERT INTO custom_sheets " +
                "(name, course, start_date, end_date, sunday, monday, tuesday, wednesday, thursday, friday, saturday, stime, etime, alt, betwn, eo_week) " +
                "(SELECT ?, course, start_date, end_date, sunday, monday, tuesday, wednesday, thursday, friday, saturday, stime, etime, alt, betwn, eo_week " +
                "FROM custom_sheets WHERE custom_sheet_id = ?)");

        pstmt2.clearParameters();
        pstmt2.setString(1, new_name);
        pstmt2.setLong(2, sheet_id);
        count = pstmt2.executeUpdate();
        pstmt2.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error1 copying sheet id " + sheet_id + " to sheet " + new_name, exc.toString(), out, false);
    }
    
    new_sheet_id = getSheetId(new_name, con, out);
    
    if (count == 1 && new_sheet_id != 0) {
        try {

            PreparedStatement pstmt2 = con.prepareStatement (
                "INSERT INTO custom_tee_times " +
                    "(custom_sheet_id, time, fb) " +
                    "(SELECT " + new_sheet_id + ", time, fb " +
                    "FROM custom_tee_times WHERE custom_sheet_id = ?)");

            pstmt2.clearParameters();
            pstmt2.setLong(1, sheet_id);
            pstmt2.executeUpdate();
            pstmt2.close();

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error2 copying sheet id " + sheet_id + " to sheet " + new_name, exc.toString(), out, false);
        }
    }
        
    return (count == 1);
 }
 
 
 //
 // This method will build a days worth of tee times for a custom sheet.  These times can then be
 // manipulated via the customizeSheet method and then applied to individual days in teecurr2
 //
 private static void populateCustomTeeTable(String sheet_name, Connection con, PrintWriter out) {
    
    int start_date = 0;
    int end_date = 0;
    //int thisdate = 0;

    int mon = 0;
    int tue = 0;
    int wed = 0;
    int thu = 0;
    int fri = 0;
    int sat = 0;
    int sun = 0;

    //int hr = 0;
    //int min = 0;
    //int last_hr = 0;
    //int last_min = 0;
    int betwn = 0;
    int alt_betwn = 0;
    int stime = 0;
    int etime = 0;
    int count = 0;
    //int i = 0;
    int hour = 0;
    int min = 0;
    int thistime = 0;
    int lasttime = 0;
    int alt = 0;
    int fb = 0;
    int sheet_id = 0;
    //int weekday = 0;
    
    //String course = "";
    
    //boolean skip = false;
    
    // delete any existing entries in the custom_tee_times table for this custom sheet
    try {
        
        PreparedStatement pstmt = con.prepareStatement( "" +
                "DELETE FROM custom_tee_times WHERE custom_sheet_id = " +
                    "(SELECT custom_sheet_id FROM custom_sheets WHERE name = ?)" );

        pstmt.clearParameters();
        pstmt.setString(1, sheet_name);
        pstmt.executeUpdate();
        pstmt.close();
    
    } catch (Exception exc) {
     
        SystemUtils.buildDatabaseErrMsg("Error purging existing custom tee times.", exc.toString(), out, false);
        return;
    }
    
    // load values for form
    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT *, " +
                "DATE_FORMAT(start_date, '%Y') AS syear, " +
                "DATE_FORMAT(start_date, '%c') AS smonth, " +
                "DATE_FORMAT(start_date, '%e') AS sday, " +
                "DATE_FORMAT(end_date, '%Y') AS eyear, " +
                "DATE_FORMAT(end_date, '%c') AS emonth, " +
                "DATE_FORMAT(end_date, '%e') AS eday " +
            "FROM custom_sheets " +
            "WHERE name = ?");

        pstmt.clearParameters();
        pstmt.setString(1, sheet_name);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {

            sun = rs.getInt("sunday");
            mon = rs.getInt("monday");
            tue = rs.getInt("tuesday");
            wed = rs.getInt("wednesday");
            thu = rs.getInt("thursday");
            fri = rs.getInt("friday");
            sat = rs.getInt("saturday");

            sheet_id = rs.getInt("custom_sheet_id");

            betwn = rs.getInt("betwn");
            alt_betwn = rs.getInt("alt");

            start_date = rs.getInt("start_date");
            end_date = rs.getInt("end_date");
            
            stime = rs.getInt("stime");
            hour = stime / 100;
            min = stime - (hour * 100);

            etime = rs.getInt("etime");
            //last_hr = (etime / 100);
            //last_min = etime - (last_hr * 100);

        }
        
        pstmt.close();

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading custom tee sheet.", exc.toString(), out, false);
        return;
    }
    
    thistime = stime;
    lasttime = etime;
    
    //out.println("<br>Building times from " + thistime + " to " + lasttime);
    
    try {
/*
        while (thisdate <= end_date) {
        
            skip = true; // default to skipping this date unless it hits on a recurrence check
            
            // get thisdate set to the date we are working on
            PreparedStatement pstmt2 = con.prepareStatement("" +
                    "SELECT DATE_FORMAT(DATE_ADD(?, INTERVAL " + count + " DAY), '%Y%m%d') AS d1");
            pstmt2.clearParameters();
            pstmt2.setInt(1, start_date);
            ResultSet rs = pstmt2.executeQuery();
            if (rs.next()) thisdate = rs.getInt(1);
            pstmt2.close();
            
            // get the day of week for this working date
            pstmt2 = con.prepareStatement("" +
                    "SELECT DATE_FORMAT(?, '%w') AS d2");
            pstmt2.clearParameters();
            pstmt2.setInt(1, thisdate);
            rs = pstmt2.executeQuery();
            if (rs.next()) weekday = rs.getInt(1);
            pstmt2.close();
            
            
            if (
                (weekday == 0 && sun == 1) ||
                (weekday == 1 && mon == 1) ||
                (weekday == 2 && tue == 1) ||
                (weekday == 3 && wed == 1) ||
                (weekday == 4 && thu == 1) ||
                (weekday == 5 && fri == 1) ||
                (weekday == 6 && sat == 1)) { skip = false; }
*/
            //
            // Build all the tee times for this date
            //
//            if (!skip) {

                PreparedStatement pstmt = con.prepareStatement( "" +
                        "INSERT INTO custom_tee_times VALUES (NULL, ?, ?, ?)" );

                while (thistime <= lasttime) {

                    pstmt.clearParameters();
                    pstmt.setInt(1, sheet_id);
                    pstmt.setInt(2, thistime);
                    pstmt.setInt(3, fb);
                    pstmt.executeUpdate();

                    //
                    //   Bump time to next interval
                    //
                    if (alt_betwn > 0) {        // if alternate minutes specified in parms

                        if (alt == 1) {          //  and this is an alternate time (every other)

                            min = min + alt_betwn;     // bump minutes by alt minutes between times
                            alt = 0;                   // toggle the alt switch

                        } else {

                            min = min + betwn;         // bump minutes by minutes between times
                            alt = 1;                   // toggle the alt switch
                        }

                    } else {

                        min = min + betwn;     // no alt minutes - bump minutes by minutes between times
                    }

                    if (min > 59) {

                       min = min - 60;     // adjust past hour count
                       hour = hour + 1;
                    }

                    thistime = (hour * 100) + min;       // recalc thistime (hhmm)
                    
                    count++;

                } // end while loop for time
            
//            } // end if skip
            
//        } // end while loop for date
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error populating custom tee sheet.", exc.toString(), out, false);
        return;
    }
    
    //out.println("<br>Added " + count + " to custom tee sheets.");
    
 }
 
 
 private void customizeSheet(String name, String msg, boolean insert, Connection con, PrintWriter out, HttpServletRequest req) {
     
    int time = 0;
    int hr = 0;
    int min = 0;
    int fb = 0;
    int tid = 0;
    int i = 0;
    int defHr = 0;                 // hour to default to
    int count = 0;
    
    String sfb = "F";
    String jump = req.getParameter("jump") == null ? "" : req.getParameter("jump");
    String shr = req.getParameter("hr") == null ? "0" : req.getParameter("hr");
    
    try {
        
        defHr = Integer.parseInt(shr);
    } catch (Exception ignore) {}
    
    boolean tmp_switch = false;
    
    out.println("<br><h3 align=center>Customize Custom Tee Sheet</h3>");
    
    out.println("<p align=center><font size=3><b>\"" + name + "\"</b></font></p>");
    
    if (!msg.equals("")) {
        
        out.println("<p align=center><font size=3><i>" + msg + "</i></font></p>");
    }
    
    if (insert) {
    
        out.println("<table align=center bgcolor=#F5F5DC border=1 cellpadding=5><tr><td><nobr>");
        out.println("<form>");
        out.println("<input type=hidden name=jump value=\"" + jump + "\">");
        out.println("<input type=hidden name=insert2>");
        out.println("<input type=hidden name=name value=\"" + name + "\">");

        out.println("Time:&nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;&nbsp;");

        out.println("<select size=\"1\" name=\"hr\">");

        for (i=1;i<=12;i++) {
            Common_Config.buildOption(i, i, defHr, out);
        }

        out.println("</select>");

        out.println("&nbsp;&nbsp; min &nbsp;&nbsp;");

        out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" name=\"min\">");
        out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");

        out.println("<select size=\"1\" name=\"ampm\">");
        out.println("<option>AM</option>");
        out.println("<option>PM</option>");
        out.println("</select>");

        out.println("&nbsp;&nbsp;&nbsp;");

        out.println("Front/Back:&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"fb\">");
          out.println("<option value=\"F\">Front</option>");
          out.println("<option value=\"B\">Back</option>");
          out.println("<option value=\"O\">Crossover</option>");
        out.println("</select>");

        out.println("</nobr><br>");
        out.println("<center><input type=submit value=\" Insert \"></center>");
        out.println("</form>");
        out.println("</td></tr></table><br>");
    
    } // end displaying insert form
    
    out.println("<table align=center><tr valign=top><td align=center>");
    
    out.println("<font size=3><b>AM Times</b></font>");
    out.println("<table border=1 bgcolor=#F5F5DC>");
    out.println("<tr style=\"background-color: #336633; font-weight: bold; color: white\" align=center><td>+ / -</td><td>Time</td><td>&nbsp;F/B&nbsp;</td></tr>");
            
    // load existing tee times for this custom sheet
    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT * FROM custom_tee_times WHERE custom_sheet_id = " +
                "(SELECT custom_sheet_id FROM custom_sheets WHERE name = ?) ORDER BY time ASC, fb");

        pstmt.clearParameters();
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {

            tid = rs.getInt("custom_tee_time_id");
            fb = rs.getInt("fb");
            sfb = "F";
            if (fb == 1) {
                sfb = "B";
            } else if (fb == 9) {
                sfb = "O";
            }
            time = rs.getInt("time");
            hr = time / 100;
            min = time - (hr * 100);
            
            if (time > 1159 && !tmp_switch) {
                
                tmp_switch = true;
                
                out.println("</table></td><td>&nbsp; &nbsp;</td><td align=center>");
                out.println("<font size=3><b>PM Times</b></font>");
                out.println("<table border=1 bgcolor=#F5F5DC>");
                out.println("<tr style=\"background-color: #336633; font-weight: bold; color: white\" align=center><td>+ / -</td><td>Time</td><td>&nbsp;F/B&nbsp;</td></tr>");
     
            }
            
            out.println("<tr><a name=\"jump"+count+"\" id=\"jump" + count + "\"></a>");
            out.print("<td>&nbsp;");
            out.print("<a href=\"Proshop_customsheets?insert&tid=" +tid+ "&name=" + name + "&hr=" + hr + "&jump=jump" + count + "\" title=\"Insert tee time\" alt=\"Insert tee time\">");
            out.print("<img src=/" +rev+ "/images/dts_insert.gif width=13 height=13 border=0></a>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<a href=\"Proshop_customsheets?delete&tid=" +tid+ "&name=" + name + "&jump=jump" + count + "\" title=\"Delete tee time\" alt=\"Delete tee time\">");
            out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");
            out.println("&nbsp;</td>");
            out.println("<td align=right>&nbsp;" + ((hr > 12) ? hr - 12 : ((hr == 0) ? 12 : hr)) + ":" + SystemUtils.ensureDoubleDigit(min) + ((time > 1159) ? " PM" : " AM") + "&nbsp;</td>");
            
            out.println("<form name=frmFB>");
            out.println("<input type=hidden name=changeFB>");
            out.println("<input type=hidden name=name value=\"" + name + "\">");
            out.println("<input type=hidden name=tid value=\"" + tid + "\">");
            out.print("<td align=center>");
            //out.print("<a href=\"Proshop_customsheets?fb&tid=" +tid+ "&name=" + name + "\">" + sfb + "</a>");
            out.println("<select size=1 name=fb onchange=\"this.form.submit()\">");
            Common_Config.buildOption("F", "F", sfb, out);
            Common_Config.buildOption("B", "B", sfb, out);
            Common_Config.buildOption("O", "O", sfb, out);
            out.println("</select>");
            out.println("</td>");
            out.println("</form>");
            out.println("</tr>");
            
            
            /*
            // Display one long column
            out.println("<tr>");
            out.print("<td>&nbsp;");
            out.print("<a href=\"Proshop_customsheets?customize&comtomize2&tid=" +tid+ "&insert=yes\" title=\"Insert tee time\" alt=\"Insert tee time\">");
            out.print("<img src=/" +rev+ "/images/dts_insert.gif width=13 height=13 border=0></a>");
            out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
            out.print("<a href=\"Proshop_customsheets?customize&comtomize2&tid=" +tid+ "&delete=yes\" title=\"Delete tee time\" alt=\"Delete tee time\" onclick=\"return confirm('Are you sure you want to permanently delete this tee time?');\">");
            out.print("<img src=/" +rev+ "/images/dts_trash.gif width=13 height=13 border=0></a>");
            out.println("&nbsp;</td>");
            out.println("<td>&nbsp;" + ((hr > 12) ? hr - 12 : hr) + ":" + SystemUtils.ensureDoubleDigit(min) + ((time > 1200) ? " PM" : " AM") + "&nbsp;</td>");
            out.println("<td align=center>" + sfb + "</td>");
            out.println("</tr>");
            */
            
            count++;
        }
        
        pstmt.close();

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading custom tee sheet.", exc.toString(), out, false);
        return;
    }
        
    out.println("</table>");
    
    out.println("</td></tr>");
    
    out.println("<tr><td align=center colspan=3>");
    out.println("<br>");
    out.println("<form>");
    out.println("<input type=hidden name=implement>");
    out.println("<input type=hidden name=skip_rebuild>");
    out.println("<input type=hidden name=name value=\"" + name + "\">");
    out.println("<input type=submit value=\"Save & Return\">");
    out.println("</form>");
    
    out.println("</td></tr></table>");
    
    if (!jump.equals("") && !insert) {
        out.println("<script>"); //  type=\"text/javascript\"
        //out.println("if (location.href.indexOf(\"" + jump + "\") < 0) {"); 
        out.println(" location.href=\"#" + jump + "\";"); 
        //out.println("}");
        out.println("</script>");
    }
    
 }
 
 
 private static int getSheetId(String name, Connection con, PrintWriter out) {
     
    int id = 0;
    
    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT custom_sheet_id FROM custom_sheets WHERE name = ?");

        pstmt.clearParameters();
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) id = rs.getInt(1);
        pstmt.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error checking sheet name.", exc.toString(), out, false);
    }
    
    return id;
    
 }
 
} // end class file