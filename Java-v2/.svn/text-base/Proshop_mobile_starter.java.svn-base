/***************************************************************************************     
 *   Proshop_mobile_starter:   
 *
 *
 *
 *   called by:  Proshop menu (to doGet)
 *               self         (to doPost & doGet)
 *
 *   created: 8/18/2006
 *
 *   last updated: 
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

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Connect;

public class Proshop_mobile_starter extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
    
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    int multi = 0;
    
    parmClub parm = new parmClub(0, con);
    
    try {

        getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }
    
    multi = parm.multi;
    
    String showEmptyTimes = (req.getParameter("showEmptyTimes") != null) ? req.getParameter("showEmptyTimes") : "";
    String showExpiredTimes = (req.getParameter("showExpiredTimes") != null) ? req.getParameter("showExpiredTimes") : "";
    String showPlayerNames = (req.getParameter("showPlayerNames") != null) ? req.getParameter("showPlayerNames") : "";
    String course = (req.getParameter("course") != null) ? req.getParameter("course") : "";
    String tmp = (req.getParameter("switch") != null) ? req.getParameter("switch") : "";
    
    if (tmp.equals("Switch Courses")) course = "";
    
    boolean showEmpty = (showEmptyTimes.equals("yes")) ? true : false;
    boolean showExpired = (showExpiredTimes.equals("yes")) ? true : false;
    boolean showNames = (showPlayerNames.equals("yes")) ? true : false;
    
    Calendar cal = new GregorianCalendar();
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    int date = (year * 10000) + (month * 100) + day;
    int hr = cal.get(Calendar.HOUR_OF_DAY); 
    int min = cal.get(Calendar.MINUTE);
    
    // adjust time to clubs timezone
    int ltime = hr * 100 + min;
    ltime = SystemUtils.adjustTime(con, ltime);

    hr = ltime / 100; // get time back into seperate variables
    min = ltime - (hr * 100);
    
    String ampm = " AM";
    if (hr == 12) ampm = " PM";
    if (hr == 0) hr = 12;
    if (hr > 12) {

       hr =- 12;
       ampm = " PM";
    }
    
    String time = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;
    
    out.println(SystemUtils.HeadTitle("Starter Screen"));
    
    out.println("<script>");
    out.println("function viewTime(pTeeCurrID) {");
    out.println(" var f = document.forms['frmTeeEdit'];");
    out.println(" f.tid.value = pTeeCurrID;");
    out.println(" f.submit();");
    out.println("}");
    out.println("</script>");
    
    out.println("<form method=get name=frmSheetView>");
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"100%\">");
    
    // start conditional TD
    
    // if this is multi course and no course yet selected just display the top header
    // and return before displaying tee sheet
    boolean forceCourseSelect = false;
    if (multi != 0) {
        
        if (course.equals("")) {           // if multiple courses supported for this club
        
            forceCourseSelect = true;

            out.println("<tr>");
            out.println("<td>");
            out.println("Choose course:&nbsp;");
            out.println("<select name=course size=1 onclick=\"\">");

            try {

                //
                //  Get the names of all courses for this club
                //
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE first_hr != 0");

                while (rs.next()) {

                    out.println("<option" + (course.equals(rs.getString(1)) ? " selected" : "") + ">" + rs.getString(1) + "</option>");
                }
                stmt.close();
            }
            catch (Exception exc) {
                SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, false);
                return;
            }

            out.println("</select>");
            out.println("</td>");
            out.println("</tr>");
        } else {
            
            // we're multi but we already have a course specificed so we just
            // need to make a form elem to pass it along
            out.println("<input type=hidden name=course value=\"" + course + "\">");
        }
    } // end if multiple courses for this club
    
    // end conditional TR
    
    
    out.println("<tr>");
    out.println("<td>");
    out.println("<input type=checkbox name=showExpiredTimes value=yes" + ((showExpired) ? " checked" : "") + "> Show Expired");
    out.println("<br>");
    out.println("<input type=checkbox name=showEmptyTimes value=yes" + ((showEmpty) ? " checked" : "") + "> Show Empty");
    out.println("<br>");
    out.println("<input type=checkbox name=showPlayerNames value=yes" + ((showNames) ? " checked" : "") + "> Show Names");
    
    out.println("</td>");
    
    if (forceCourseSelect) {
        
        out.println("</tr>");
        out.println("<tr>");
        out.println("<td align=center colspan=2><input type=submit value=\"  Display  \"></td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</form>");
        return;
    }
    
    out.println("</tr>");
    out.println("<tr>");
    out.println("<td colspan=" + ((multi == 0) ? "3" : "4") + " align=center><input type=submit value=\"  Refresh  \">" +
            ((multi == 0) ? "" : "&nbsp; &nbsp; &nbsp; <input type=submit name=switch value=\"Switch Courses\">") +
            "</td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</form>");
    
    if (multi != 0) {
        
        out.println("<center><b>"+ course + "</b></center>");
    }
    
    
    int i = 0;
    int teecurr_id = 0;
    int ts_hr = 0;
    int ts_min = 0;
    int ts_time = 0;
    int fb = 0;
    int pace_status_id = 0;
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    String sfb = "";
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
    String courseName = "";
    boolean alt = false;
    boolean found = false;
    String row_color_1 = "#DDDDBE";
    String row_color_2 = "#B3B392";
    
    out.println("<form method=post name=frmTeeEdit>");
    out.println("<input type=hidden name=tid value=\"\">");
    out.println("<input type=hidden name=showEmptyTimes value=\"" + showEmptyTimes + "\">");
    out.println("<input type=hidden name=showExpiredTimes value=\"" + showExpiredTimes + "\">");
    out.println("<input type=hidden name=showPlayerNames value=\"" + showPlayerNames + "\">");
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"100%\">");
    out.println("<tr bgcolor=\"#336633\">");

    out.println("<td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<u><b>Time</b></u>");
    out.println("</font></td>");

    out.println("<td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<u><b>F/B</b></u>");
    out.println("</font></td>");

    /*
    if (multi != 0 && course.equals("-ALL-")) {
        out.println("<td align=\"center\">");
        out.println("<font color=\"#FFFFFF\" size=\"2\">");
        out.println("<u><b>Course</b></u>");
        out.println("</font></td>");
    }
    */
    
    out.println("<td align=\"center\">");
    out.println("<font color=\"#FFFFFF\" size=\"2\">");
    out.println("<u><b>Players</b></u>");
    out.println("</font></td>");
        
    try {
        
        pstmt = con.prepareStatement("" +
                "SELECT * " +
                "FROM teecurr2 " +
                "WHERE restriction = \"\" " +
                    "AND date = ? " +
                    ((course.equals("") || course.equals("-ALL-")) ? "" : "AND courseName = ? ") + 
                "ORDER BY time, courseName, fb");
        pstmt.clearParameters();
        pstmt.setInt(1, date);
        if (!course.equals("") && !course.equals("-ALL-")) pstmt.setString(2, course);
        rs = pstmt.executeQuery();

        while (rs.next()) {

            teecurr_id = rs.getInt("teecurr_id");
            ts_hr = rs.getInt("hr");
            ts_min = rs.getInt("min");
            ts_time = rs.getInt("time");
            fb = rs.getInt("fb");
            pace_status_id = rs.getInt("pace_status_id");
            courseName = rs.getString("courseName");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            player5 = rs.getString("player5");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            
            if (p91 == 1) p1cw += "9";
            if (p92 == 1) p2cw += "9";
            if (p93 == 1) p3cw += "9";
            if (p94 == 1) p4cw += "9";
            if (p95 == 1) p5cw += "9";
            
            ampm = " AM";
            if (ts_hr == 12) ampm = " PM";
            if (ts_hr == 0) ts_hr = 12;
            if (ts_hr > 12) {

               ts_hr -= 12;
               ampm = " PM";
            }
            
            sfb = (fb == 0) ? "F" : "B";
            time = ts_hr + ":" + SystemUtils.ensureDoubleDigit(ts_min) + ampm;
            
            // #F5F5DC
            // #DDDDBE
            // #B3B392
            
                           // "<td>" + time + "</td>" + 
            // if it's an upcoming tee time or if showExpired is set then show this tee time
            if (ltime <= ts_time || showExpired) { 
                
                // if player1 is not empty or if showEmpty is set, then show this tee time
                if ((!player1.equals("") && !player1.equalsIgnoreCase("x")) || showEmpty) { 
                    
                    found = true;
                    out.println("<tr bgcolor=\"" + ((alt) ? row_color_1 : row_color_2) + "\">");
                    alt = (alt==false);
                    
                        out.println("<td align=center><input type=\"submit\" value=\"" + time + "\" onclick=\"viewTime('" + teecurr_id + "')\"></td>");
                        out.println("<td align=center><font size=\"2\">" + sfb + "</font></td>");

                        /*
                        if (multi != 0 && course.equals("-ALL-")) {

                            out.print("<td align=center><font size=\"2\">" + courseName + "</font></td>");
                        }
                        */
                            
                        if (showNames) {
                            
                            out.print("<td>");
                            if (!player1.equals("") && !player1.equalsIgnoreCase("x")) out.print("<font size=2>" + player1 + " <font size=1>(" + p1cw + ")</font>");
                            if (!player2.equals("") && !player2.equalsIgnoreCase("x")) out.print(", " + player2 + " <font size=1>(" + p2cw + ")</font>");
                            if (!player3.equals("") && !player3.equalsIgnoreCase("x")) out.print(", " + player3 + " <font size=1>(" + p3cw + ")</font>");
                            if (!player4.equals("") && !player4.equalsIgnoreCase("x")) out.print(", " + player4 + " <font size=1>(" + p4cw + ")</font>");
                            if (!player5.equals("") && !player5.equalsIgnoreCase("x")) out.print(", " + player5 + " <font size=1>(" + p5cw + ")</font>");
                            out.print("</font>");
                            out.println("</td>");
                        } else {
                            
                            i = 0;
                            out.print("<td align=center>");
                            if (!player1.equals("") && !player1.equalsIgnoreCase("x")) i++;
                            if (!player2.equals("") && !player2.equalsIgnoreCase("x")) i++;
                            if (!player3.equals("") && !player3.equalsIgnoreCase("x")) i++;
                            if (!player4.equals("") && !player4.equalsIgnoreCase("x")) i++;
                            if (!player5.equals("") && !player5.equalsIgnoreCase("x")) i++;
                            
                            if (i > 0) {
                                
                                out.print("<font size=2>" + player1);
                                i--;
                                if (i > 0) out.print(" + " + i + "</font>");
                            } else {
                                
                                out.print("<font size=2>" + i + "</font>");
                            }
                            out.println("</td>");
                        } // end show names check
                        
                  
                        out.println("</tr>");
                        
                } // end time check
            }
            
        }

        if (!found) {

            out.println("<tr><td colspan=3 align=center><i>No Tee Time Reservations Remaining Today</i></td></tr>");
        }
        
        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    out.println("</table>");
    out.println("</form>");
    
 } // end doGet
 
 
public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    int hr = 0;
    int min = 0;
    
    String [] time_part = new String [1];
    String [] time_ampm = new String [2];
    String sql_time = "";
    String start_time = "";
    String actual_start = "";
    
    String course = (req.getParameter("course") != null) ? req.getParameter("course") : "";
    String showEmptyTimes = (req.getParameter("showEmptyTimes") != null) ? req.getParameter("showEmptyTimes") : "";
    String showExpiredTimes = (req.getParameter("showExpiredTimes") != null) ? req.getParameter("showExpiredTimes") : "";
    String showPlayerNames = (req.getParameter("showPlayerNames") != null) ? req.getParameter("showPlayerNames") : "";
    
    int tid = 0;
    String tmp = "";
    if (req.getParameter("tid") != null) tmp = req.getParameter("tid");
    
    try {
        tid = Integer.parseInt(tmp);
    }
    catch (NumberFormatException e) {
        out.println("<h3>Invalid or missing tee time identifier received.</h3>");
        return;
    }
    
    //out.println("tid="+tid);
    
    // if we are here to update the starting time the call out and display form
    if (req.getParameter("btnUpdateStart") != null) {
        
        displayUpdateStartTime(req, tid, con, out);
        return;
    } else {
        
        // otherwise see if have a new starting time to save
        if (req.getParameter("updateTime") != null) {
            
            String hole_time = req.getParameter("start_time");
            hole_time = hole_time.trim(); 
            
            if (!hole_time.equals("")) {
            
                try {
                    
                    // see if they included a space between the minutes and the ampm
                    if (hole_time.indexOf(" ") == -1) {

                        // no space - split is manually by its expected format
                        time_ampm[0] = hole_time.substring(0, hole_time.length() - 2);
                        time_ampm[1] = hole_time.substring(hole_time.length() - 2, hole_time.length());
                    } else {

                        // space found - use string split
                        time_ampm = hole_time.split(" "); // split it into the first array
                    }

                    time_part = time_ampm[0].split(":"); // explode the first part into seperate time segments

                    hr = Integer.parseInt(time_part[0]);
                    min = Integer.parseInt(time_part[1]);

                    if ( hr > 12 || hr < 0 || 
                         min > 59 || min < 0 || 
                         ((!time_ampm[1].equalsIgnoreCase("pm")) && (!time_ampm[1].equalsIgnoreCase("am")))) {

                        // use the error message in catch
                        throw new Exception("");
                    }

                    if (time_ampm[1].equalsIgnoreCase("pm") && hr != 12) hr += 12;
                    if (time_ampm[1].equalsIgnoreCase("am") && hr == 12) hr = 0;

                    sql_time = hr + ":" + min + ":00";
                    
                }
                catch (Exception e) {

                    out.println(SystemUtils.HeadTitle("Invalid Starting Time"));
                    out.println("<BODY><CENTER><BR>");
                    out.println("<H3>Invalid Starting Time</H3>");
                    out.println("<BR><BR>You have entered a time that can not be read.");
                    out.println("<BR>The proper format is hh:mm am/pm.  Example: 7:15 AM");
                    out.println("<BR>Please correct and try again.");
                    out.println("<BR><BR>If problem persists, contact customer support for assistance.");
                    out.println("<BR><BR>");
                    out.println("<A HREF=\"javascript:history.back(1)\">Back</a>");
                    out.println("</CENTER></BODY></HTML>");

                    out.close();
                    return;
                }
    
                try {

                    String sql = "INSERT INTO pace_entries (teecurr_id, hole_number, hole_timestamp) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE hole_timestamp = ?";
                    pstmt = con.prepareStatement(sql);
                    pstmt.clearParameters();
                    pstmt.setInt(1, tid);
                    pstmt.setInt(2, 0);
                    pstmt.setString(3, sql_time);
                    pstmt.setString(4, sql_time);
                    pstmt.executeUpdate();
                }
                catch (Exception e1) {

                    SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
                    return;
                }
                
            } // end if hole_time empty
            
        } // end if update starting time
    
    } // end else if
    
    
    // get multi parameter
    int multi = 0;
    parmClub parm = new parmClub(0, con);
    try {

        getClub.getParms(con, parm);        // get the club parms
    } catch (Exception ignore) { }
    multi = parm.multi;
    
    
    int playerPos = 0;
    int new_show = -1;
    String tmp2 = "";
    tmp = "";
    
    if (req.getParameter("show") != null) tmp = req.getParameter("show");
    if (req.getParameter("pos") != null) tmp2 = req.getParameter("pos");
    
    try {
        new_show = Integer.parseInt(tmp);
        playerPos = Integer.parseInt(tmp2);
    }
    catch (Exception ignore) {
    }
    
    // update show values if nessesary
    // if playerPos is positive AND there is a show value here, then we have a player to set their show value
    if (new_show > -1 && playerPos > 0) {
        
        String field = "show" + playerPos;
        
        try {
            pstmt = con.prepareStatement("UPDATE teecurr2 SET " + field + " = ? WHERE teecurr_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, new_show);
            pstmt.setInt(2, tid);
            pstmt.executeUpdate();
        }
        catch (Exception e1) {

            SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
            return;
        }
        
    } else {
        
        // check to see if check in all was selected
        if (req.getParameter("btnChkInAll") != null) {
         
            try {
                pstmt = con.prepareStatement("UPDATE teecurr2 SET show1=1, show2=1, show3=1, show4=1, show5=1 WHERE teecurr_id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, tid);
                pstmt.executeUpdate();
            }
            catch (Exception e1) {

                SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
                return;
            }
            
        } // end if chkInAll
        
    } // end setting show values
    
    
    // get their actual starting time from pace entries if one exists
    try {
        
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(hole_timestamp, '%H') AS as_hr, DATE_FORMAT(hole_timestamp, '%i') AS as_min FROM pace_entries WHERE teecurr_id = ? and hole_number = 0");
        pstmt.clearParameters();
        pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            actual_start = SystemUtils.getSimpleTime(rs.getInt("as_hr"), rs.getInt("as_min"));
        }
        
        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }

    // start html output
    out.println(SystemUtils.HeadTitle("Starter Screen"));
    
    out.println("<script>");
    out.println("function backToSheet() {");
    out.println(" document.forms['frmActions'].method = 'get';");
    out.println(" document.forms['frmActions'].submit();");
    out.println("}");
    out.println("</script>");
    
    // get tee time in RS and display data
    int i = 0;
    int ts_hr = 0;
    int ts_min = 0;
    int ts_time = 0;
    int fb = 0;
    int pace_status_id = 0;
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    int show1 = 0;
    int show2 = 0;
    int show3 = 0;
    int show4 = 0;
    int show5 = 0;
    String sfb = "";
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
    String courseName = "";
    String time = "";
    String ampm = "";
    
    try {
        
        pstmt = con.prepareStatement("" +
                "SELECT * " +
                "FROM teecurr2 " +
                "WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            ts_hr = rs.getInt("hr");
            ts_min = rs.getInt("min");
            ts_time = rs.getInt("time");
            fb = rs.getInt("fb");
            pace_status_id = rs.getInt("pace_status_id");
            courseName = rs.getString("courseName");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            player5 = rs.getString("player5");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            show1 = rs.getInt("show1");
            show2 = rs.getInt("show2");
            show3 = rs.getInt("show3");
            show4 = rs.getInt("show4");
            show5 = rs.getInt("show5");
            
            if (p91 == 1) p1cw += "9";
            if (p92 == 1) p2cw += "9";
            if (p93 == 1) p3cw += "9";
            if (p94 == 1) p4cw += "9";
            if (p95 == 1) p5cw += "9";
            
            ampm = " AM";
            if (ts_hr == 12) ampm = " PM";
            if (ts_hr == 0) ts_hr = 12;
            if (ts_hr > 12) {

               ts_hr -= 12;
               ampm = " PM";
            }
            
            sfb = (fb == 0) ? "F" : "B";
            time = ts_hr + ":" + SystemUtils.ensureDoubleDigit(ts_min) + ampm;
            
            boolean showChkAllBtn = false;
            if (!player1.equals("") && !player1.equalsIgnoreCase("x") && show1 != 1) showChkAllBtn = true;
            if (!player2.equals("") && !player2.equalsIgnoreCase("x") && show2 != 1) showChkAllBtn = true;
            if (!player3.equals("") && !player3.equalsIgnoreCase("x") && show3 != 1) showChkAllBtn = true;
            if (!player4.equals("") && !player4.equalsIgnoreCase("x") && show4 != 1) showChkAllBtn = true;
            if (!player5.equals("") && !player5.equalsIgnoreCase("x") && show5 != 1) showChkAllBtn = true;
            
            // #F5F5DC
            // #DDDDBE
            // #B3B392
            
            out.println("<table cellpadding=5 border=1 bgcolor=\"#F5F5DC\">");
            
            /*out.println("<tr>" +
                            "<td>&nbsp;</td>" +
                            "<td width=\"100%\">&nbsp;</td>" +
                            "<td>&nbsp;</td>" +
                        "</tr>");*/
            
            out.println("<tr><td><b>" + ((!actual_start.equals("")) ? "Orig. " : "") + "Time:</b></td><td>" + time + "</td></tr>");
            if (!actual_start.equals("")) out.println("<tr><td><b>Staring Time:</b></td><td>" + actual_start + "</td></tr>");
            out.println("<tr><td><b>F/B:</b></td><td>" + sfb + "</td></tr>");
            //if (multi != 0) out.println("<tr><td><b><u>Course</u>:</b></td><td colspan=2>" + courseName + "</td></tr>");
            
            out.println("<tr><td colspan=2><b>Players:</b></td></tr>");
            
            displayPlayer(player1, p1cw, show1, 1, tid, out);
            displayPlayer(player2, p2cw, show2, 2, tid, out);
            displayPlayer(player3, p3cw, show3, 3, tid, out);
            displayPlayer(player4, p4cw, show4, 4, tid, out);
            displayPlayer(player5, p5cw, show5, 5, tid, out);
            
            out.println("<form method=post name=frmActions id=frmActions>");
            out.println("<input type=hidden name=course value=\"" + course + "\">");
            out.println("<input type=hidden name=showEmptyTimes value=\"" + showEmptyTimes + "\">");
            out.println("<input type=hidden name=showExpiredTimes value=\"" + showExpiredTimes + "\">");
            out.println("<input type=hidden name=showPlayerNames value=\"" + showPlayerNames + "\">");
            out.println("<input type=hidden name=tid value=\"" + tid + "\">");
            
            out.println("<tr><td colspan=2 align=center>");
            
            out.println("<table>");
            if (showChkAllBtn) out.println("<tr><td><input type=submit name=btnChkInAll value=\"Check In All Players\" style=\"background-color:#8B8970;width:150px\"></td></tr>");
            out.println("<tr><td><input type=submit name=btnUpdateStart value=\" Update Start Time \" style=\"background-color:#8B8970;width:150px\"></td></tr>");
            out.println("<tr><td><input type=button name=btnBack value=\" Back to Tee Sheet \" onclick=\"backToSheet()\" style=\"background-color:#8B8970;width:150px\"></td></tr>");
            out.println("</table>");
            
            out.println("</form>");
            
            out.println("</td></tr></table>");

        } // end if rs found
        
        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
} // end doPost


private void displayUpdateStartTime(HttpServletRequest req, int pTeeCurrID, Connection con, PrintWriter out) {
    
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String time = "";
    String ampm = "";
    String actual_start = "";
    int ts_hr = 0;
    int ts_min = 0;

    // start html output
    out.println(SystemUtils.HeadTitle("Starter Screen"));
    
    // include timeBox jscripts
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/timeBox-scripts.js\"></script>");
   
    // start form to post user action
    out.println("<form method=post name=frmUpdateTime id=frmUpdateTime>");
    out.println("<input type=hidden name=updateTime>");
    out.println("<input type=hidden name=tid value=" + pTeeCurrID + ">");
    out.println("<input type=hidden name=hole_num value=0>");
    
    
    // get their actual starting time from pace entries if one exists
    try {
        
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(hole_timestamp, '%H') AS as_hr, DATE_FORMAT(hole_timestamp, '%i') AS as_min FROM pace_entries WHERE teecurr_id = ? and hole_number = 0");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            actual_start = SystemUtils.getSimpleTime(rs.getInt("as_hr"), rs.getInt("as_min"));
        }
        
        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    
    // get and display the info for this tee time
    try {
        
        pstmt = con.prepareStatement("" +
                "SELECT hr, min " +
                "FROM teecurr2 " +
                "WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            
            ts_hr = rs.getInt("hr");
            ts_min = rs.getInt("min");
            
            ampm = " AM";
            if (ts_hr == 12) ampm = " PM";
            if (ts_hr == 0) ts_hr = 12;
            if (ts_hr > 12) {

               ts_hr -= 12;
               ampm = " PM";
            }
            time = ts_hr + ":" + SystemUtils.ensureDoubleDigit(ts_min) + ampm;
            
            out.println("<table border=0 cellpadding=6>");
            
            
            out.println("<tr><td><b><u>" + ((!actual_start.equals("")) ? "Original " : "") + "Time:</u></b></td><td>" + time + "</td></tr>");
            if (!actual_start.equals("")) out.println("<tr><td><b><u>Staring Time:</u></b></td><td>" + actual_start + "</td></tr>");
            
            
            //out.println("<tr><td><b><u>Time:</u></b></td><td>" + time + "</td></tr>");
            out.println("<tr><td><b><u>New Start Time:</u></b></td><td>");
            
                out.println("<table cellpadding=2 cellspacing=0><tr><td>");
                out.println("<input type=text name=start_time value=\"" + time + "\"onclick=\"TB_setCaretPos(this)\" style=\"height: 22px\" size=9></td>");
                out.println("<td><image src=/" + rev + "/images/up.gif onclick=\"TB_adjustTime(1, TB_box_1)\" width=17 height=8><br>");
                out.println("<image src=/" + rev + "/images/shim.gif height=2 width=1><br>");
                out.println("<image src=/" + rev + "/images/down.gif onclick=\"TB_adjustTime(-1, TB_box_1)\" width=17 height=8></td>");
                out.println("</tr></table>");
    
            out.println("</td></tr>");
            
            out.println("<tr><td colspan=2 align=center><input type=submit value=\" Update Time \" style=\"background-color: #8B8970\"></td></tr>");
            out.println("<tr><td colspan=2 align=center><input type=button value=\"  Cancel  \" onclick=\"backToSheet()\" style=\"background-color: #8B8970\"></td></tr>");
            
            out.println("</table>");
        } // end if rs
        
        pstmt.close();
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    out.println("</form>");
    
    out.println("<script language=\"javascript\">");
    out.println("var is_gecko = /gecko/i.test(navigator.userAgent);");
    out.println("var is_ie    = /MSIE/.test(navigator.userAgent);");
    out.println("var TB_box_1 = document.forms['frmUpdateTime'].start_time;");
    out.println("var TB_caret_pos = 0;");
    out.println("function backToSheet() {");
    out.println(" window.location.href='Proshop_mobile_starter';");
    out.println("}");
    out.println("</script>");
    
} // end of displayUpdateStartTime


private void displayPlayer(String pPlayerName, String pTmode, int pShow, int pPlayerSlot, int pTeeCurrID, PrintWriter out) {
    
    int tmp_value = 0;
    String tmp_title = "";
    String tmp_image = "";
    
    if (!pPlayerName.equals("") && !pPlayerName.equalsIgnoreCase("x")) {
     
        switch (pShow) {
            case 1:
                tmp_title = "Click here to set as a no-show (blank).";
                tmp_image = "xbox.gif";
                tmp_value = 0;
                break;
            default:
                tmp_title = "Click here to check player in (x).";
                tmp_image = "mtbox.gif";
                tmp_value = 1;
                break;
        }
            
        out.println("<tr valign=middle>" +
                "<form method=post>" +
                "<input type=hidden name=tid value=\"" + pTeeCurrID + "\">" +
                "<input type=hidden name=pos value=\"" + pPlayerSlot + "\">" +
                "<input type=hidden name=show value=\"" + tmp_value + "\">" +
                    "<td colspan=2>" +
                    "&nbsp;<input type=image src=\"/" +rev+ "/images/" + tmp_image + "\" border=1 alt=\"" + tmp_title + "\">" + 
                    "&nbsp; " + pPlayerSlot + ". " + pPlayerName + " &nbsp; <font size=1>(" + pTmode + ")</font></td>" +
                "</form>" +
                "</tr>");
        
        /*out.println("<tr valign=middle>" +
                "<form method=post>" +
                "<input type=hidden name=tid value=\"" + pTeeCurrID + "\">" +
                "<input type=hidden name=pos value=\"" + pPlayerSlot + "\">" +
                "<input type=hidden name=show value=\"" + tmp_value + "\">" +
                    "<td colspan=2>" +
                    "&nbsp;<input type=image src=\"/" +rev+ "/images/" + tmp_image + "\" border=1 alt=\"" + tmp_title + "\">" + 
                    "&nbsp; " + pPlayerSlot + ". " + pPlayerName + "</td><td><font size=1>(" + pTmode + ")</font></td>" +
                "</form>" +
                "</tr>");*/
    }    
}

} // end class