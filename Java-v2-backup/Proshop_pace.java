/***************************************************************************************     
 *   Proshop_pace:  This servlet will update pace of play for a tee time.  It add/modifies
 *                  records in the pace_entries table.
 *
 *
 *   called by:  Proshop menu (to doGet)
 *               self         (to doPost & doGet)
 *
 *   created: 6/18/2006
 *
 *   last updated: 
 *
 *              7/18/08 Added limited access proshop users checks
 *              2/27/06 Changed onfocus events to this.blur() instead of assigning focus elsewhere
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

import com.foretees.common.Connect;

public class Proshop_pace extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 //**************************************************
 // Display pace entry form
 //**************************************************
 //
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
    
    int tid = 0;
    String tmp = "";
    if (req.getParameter("tid") != null) tmp = req.getParameter("tid");
    
    try {
        tid = Integer.parseInt(tmp);
    }
    catch (NumberFormatException e) {
        return;
    }
    
    int pace_status_id = 0;
    if (req.getParameter("pace_status_id") != null) {
        
        try {
            pace_status_id = Integer.parseInt(req.getParameter("pace_status_id"));
        }
        catch (NumberFormatException e) {
            return;
        }
        if (pace_status_id != 0) doUpdatePaceStatus(tid, pace_status_id, out, con);
    }
    
    boolean details = false;
    if (req.getParameter("details") != null) details = true;
    
    resp.setDateHeader("Expires",0);
    resp.setHeader("Pragma","no-cache");
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
    resp.setContentType("text/html");

    if (details) {
        doPaceDetails(req, tid, out, con);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "TS_PACE_UPDATE", con, out)) {
        SystemUtils.restrictProshop("TS_PACE_UPDATE", out);
    }
    if (!SystemUtils.verifyProAccess(req, "TS_PACE_VIEW", con, out)) {
        SystemUtils.restrictProshop("TS_PACE_VIEW", out);
    }
    // get clubs current local time in hhmm format
    Calendar cal = new GregorianCalendar();
    int hr = cal.get(Calendar.HOUR_OF_DAY);           // 24 hr clock (0 - 23)
    int min = cal.get(Calendar.MINUTE);
    int sec = cal.get(Calendar.SECOND);
    if (sec > 30 && min != 59) min++;
    
    // adjust time to clubs timezone
    int ltime = hr * 100 + min;
    ltime = SystemUtils.adjustTime(con, ltime);

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
    
    out.println(SystemUtils.HeadTitle("Pace of Play"));
    
   out.println("<script language=\"javascript\" src=\"/" +rev+ "/timeBox-scripts.js\"></script>");
   
    out.println("<script>");
    out.println("function openPaceDetails(pID) {");
    out.println("document.location.href='Proshop_pace?tid=' +pID+'&details';");
    out.println("}");
    out.println("function openHelpWindow() {");
    out.println(" var z = window.open ('/" +rev+ "/proshop_help_paceofplay.htm','paceHelpPopup','width=640,height=360,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
    out.println("}");
    out.println("function showStartTimeForm() {");
    out.println(" document.getElementById(\"frmLayerA\").style.visibility='visible';");
    out.println(" document.getElementById(\"frmLayerB\").style.visibility='hidden';");
    out.println("}");
    out.println("function hideStartTimeForm() {");
    out.println(" document.getElementById(\"frmLayerA\").style.visibility='hidden';");
    out.println(" document.getElementById(\"frmLayerB\").style.visibility='visible';");
    out.println("}");
    out.println("function setHole(pID, pTime) {");
    out.println(" if (pTime == '') {");
    out.println("  var d = new Date();");
    out.println("  var h = d.getHours();");
    out.println("  var m = d.getMinutes();");
    out.println("  var ampm = ' AM';");
    out.println("  if (h>=12) {");
    out.println("   h -= 12;");
    out.println("   ampm=' PM';");
    out.println("  }");
    out.println("  if (h==0) h=12;");
    out.println("  if (m==0) {");
    out.println("   m = '00';");
    out.println("  } else {");
    out.println("   if (m<10) m='0'+m;");
    out.println("  }");
    out.println("  pTime=h+':'+m+ampm;");
    out.println(" }");
    out.println(" document.frmHoleTime.hole_num.value = pID;");
    out.println(" document.frmHoleTime.hole_time.value = pTime;");
    out.println("}");
    out.println("function submitForm() {");
    out.println(" if (document.frmHoleTime.hole_num.value != '') { ");
    out.println("  document.frmHoleTime.submit();");
    out.println("  return;");
    out.println(" }");
    out.println(" alert(\"You must select a tee to enter a time for.\");");
    out.println("}");
    out.println("</script>");
    
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    // identify tee time
    // maybe we could add a check to see if the players have been checked in yet

    int ts_hr = 0;
    int ts_min = 0;
    int fb = 0;
    int pace_status_id_2 = 0;
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String course = "";

    try {
        
        pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            ts_hr = rs.getInt("hr");
            ts_min = rs.getInt("min");
            fb = rs.getInt("fb");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            course = rs.getString("courseName");
            pace_status_id_2 = rs.getInt("pace_status_id");
        }

        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    // if any of the players are only doing 9
    boolean all9hole = true;
    if (!player1.equals("")) if (p91 == 0) all9hole = false;
    if (!player2.equals("")) if (p92 == 0) all9hole = false;
    if (!player3.equals("")) if (p93 == 0) all9hole = false;
    if (!player4.equals("")) if (p94 == 0) all9hole = false;
    if (!player5.equals("")) if (p95 == 0) all9hole = false;
    
    String tee_time = SystemUtils.getSimpleTime(ts_hr, ts_min);
    
    // see if there is an actual start time for the tee time (pace entry for hole 0)
    String actual_start = "";
    try {
        
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(hole_timestamp, '%H') AS as_hr, DATE_FORMAT(hole_timestamp, '%i') AS as_min FROM pace_entries WHERE teecurr_id = ? and hole_number = 0");
        pstmt.clearParameters();
        pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            actual_start = SystemUtils.getSimpleTime(rs.getInt("as_hr"), rs.getInt("as_min"));
        } else {
            
            // there was not a 0 hole so lets add it and default it to the starting time from teecurr2
            String sql_time = ts_hr + ":" + ts_min + ":00";
            String sql = "INSERT INTO pace_entries (teecurr_id, hole_number, hole_timestamp) VALUES (?, 0, ?) ON DUPLICATE KEY UPDATE hole_timestamp = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            pstmt.setInt(1, tid);
            pstmt.setString(2, sql_time);
            pstmt.setString(3, sql_time);
            pstmt.executeUpdate();
        
        }

        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    out.println("<table cellpadding=0 cellspacing=0 style=\"border: 1px solid #336633\" align=center width=\"90%\" bgcolor=\"#F5F5DC\"><tr><td>");
    
    out.println("<table width=\"100%\" border=0>");
    out.println("<tr style=\"color: #336633\"><td>&nbsp;</td><td nowrap><b><u>Start Time</b></td><td nowrap>&nbsp; &nbsp;</td><td><b><u>Course</b></td>");
    out.println("<td nowrap>&nbsp; &nbsp;</td><td><b><u>F/B</b></td><td nowrap>&nbsp; &nbsp;</td><td><b><u>Players</b></td>");
    out.println("<td width=\"100%\">&nbsp; &nbsp;</td>");
    out.println("</tr><tr valign=top>");
    out.println("<td></td>");
    out.println("<td nowrap><font size=2><a href=\"javascript:void(0);\" onclick=\"showStartTimeForm()\" alt=\"Click to change starting time\" style=\"color: blue\">");
    out.println(tee_time + (((!actual_start.equals("")) && (!actual_start.equalsIgnoreCase(tee_time))) ? "<br>" + actual_start + "*" : "") + "</a></td>");
    out.println("<td></td>");
    out.println("<td nowrap><font size=2>" + course + "</td>");
    out.println("<td></td>");
    out.println("<td nowrap><font size=2>" + ((fb == 0) ? "Front" : "Back") + "</td>");
    out.println("<td></td>");
    out.println("<td colspan=2><font size=2>");
    out.print(player1);
    if (!player2.equals("")) out.print(", <nobr>" + player2 + "</nobr>");
    if (!player3.equals("")) out.print(", <nobr>" + player3 + "</nobr>");
    if (!player4.equals("")) out.print(", <nobr>" + player4 + "</nobr>");
    if (!player5.equals("")) out.print(", <nobr>" + player5 + "</nobr>");
    out.println("</td>");
    out.println("</tr></table>");
    
    out.println("</td></tr></table>");
    
    out.println("<br>");
    
    //
    // display start icon and 18 flags
    //
    out.println("<div id=\"frmLayerB\" style=\"visibility: visible; position: absolute; top: 90px; left: 70px\">");
    
    //
    // display instructions
    //
    out.println("<table width=\"85%\" border=0 align=center><tr>");
    out.println("<td><font size=2><b>Instructions:</b> &nbsp;");
    out.println("Click on a hole to select it for the pace entry.&nbsp; The current time will default for you.&nbsp; ");
    out.println("If you wish to specify a different starting time for calculating pace, click the Start Time link above in the tee time summary box.");
    out.println("Holes with pace entries have a dark outline.");
    out.println("</font></td></tr></table>");
    
    out.println("<br>");
    
    out.println("<table align=\"center\" cellpadding=5 cellspacing=5><tr height=\"30\">");
    
    if (fb == 0) {
    
        buildFrontNine(tid, out, con);
        
        // if 9hole then don't show
        if (!all9hole) {
            
            out.println("</tr><tr height=\"30\">");
            buildBackNine(tid, out, con);
        }
    } else {
    
        buildBackNine(tid, out, con);
        
        // if 9hole then don't show
        if (!all9hole) {
            
            out.println("</tr><tr height=\"30\">");
            buildFrontNine(tid, out, con);
        }
    }
    
    out.println("</tr></table>");
    
 
    
    out.println("<table align=\"center\"><tr>");
    out.println("<form method=post name=frmHoleTime id=frmHoleTime>");
    out.println("<input type=hidden name=tid value=\"" + tid + "\">");
    out.println("<td align=\"center\"><nobr>");
    
    
     
    
    out.println("<table cellpadding=2 cellspacing=0><tr><td>");
   
    out.println("<b>Hole Selected:</b> &nbsp;");
    out.println("<input type=text name=hole_num value=\"None\" size=3 onkeydown=\"return false\" onfocus=\"this.blur()\" style=\"color: black; border: none; color:#336633; font-weight:bold\">");  // document.frmHoleTime.hole_time.focus() 
   
    
    out.println("<input type=text name=hole_time onclick=\"TB_setCaretPos(this)\" style=\"height: 22px\" size=9></td>");
    out.println("<td><image src=/" + rev + "/images/up.gif onclick=\"TB_adjustTime(1, TB_box_1)\" width=17 height=8><br>");
    out.println("<image src=/" + rev + "/images/shim.gif height=2 width=1><br>");
    out.println("<image src=/" + rev + "/images/down.gif onclick=\"TB_adjustTime(-1, TB_box_1)\" width=17 height=8></td>");
    out.println("<td>&nbsp; &nbsp;<input type=button value=\" Set Time \" onclick=\"submitForm()\" style=\"background-color: #8B8970\"></td>");
    out.println("</tr><table>");
    out.println("</nobr></td></tr></form><tr><td>");
    
    out.println("<br><br>");
    
    
    int calc_pace_status = 0;
    calc_pace_status = getPaceStatus(tid, con, out);
    String btn_text = (pace_status_id_2 == 0) ? "Set " : "Update ";
    
    // current is already here (pace_status_id)
    out.println("<form method=get name=frmStatus id=frmStatus>");
    out.println("<input type=hidden name=tid value=\"" + tid + "\">");
    //out.println("<input type=hidden name=details value=\"\">");
        
    out.println("<table align=center>");
    out.println("<tr><td align=center><b>Current Pace Status: </b>&nbsp;");

    out.println("<br><input type=submit value=\"" + btn_text + " Pace\" onclick=\"\" style=\"background-color: #8B8970\">");    
    
    out.println("</td><td>");
    
    int tmp_id = 0;
    String tmp_color = "white";
    String tmp_name = "";
    String tmp_name_calc = "";
        
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");
                
        while (rs.next()) {
            
            tmp_id = rs.getInt("pace_status_id");
            tmp_name = rs.getString("pace_status_name");
            if (tmp_id == calc_pace_status) {
                tmp_name_calc = tmp_name;
                tmp_color = rs.getString("pace_status_color");
            }
            
            out.print("<input type=radio name=pace_status_id value=\"" + tmp_id + "\"");
            if (tmp_id == pace_status_id_2) out.print(" checked");
            out.print(">" + tmp_name + "<br>");
        
        } // end while
        
    } catch (Exception e) {
        
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
    }
        
    
    
    out.println("</table>");
        
    if (tmp_name_calc.equals("")) tmp_name_calc = "N/A";
    
    out.println("<div style=\"position: relative; top:-95px; left: 36px\"><b>Computed Pace Status: </b>&nbsp; &nbsp;<input type=text value=\" " + tmp_name_calc + "\" style=\"background-color:" + tmp_color + "; border: 1px solid black\" onfocus=\"this.blur()\"></div>");  // document.frmStatus.pace_status_id.focus()
    
    out.println("</td></tr><tr>");
    
    out.println("<form>");
    out.println("<td align=center>");
    out.println("<input type=button value=\"  Help  \" onclick=\"openHelpWindow()\" style=\"background-color: #8B8970\">");
    out.println("&nbsp; &nbsp; &nbsp;");
    out.println("<input type=button value=\" Show Pace Details \" onclick=\"openPaceDetails(" + tid + ")\" style=\"background-color: #8B8970\">");
    out.println("&nbsp; &nbsp; &nbsp;");
    out.println("<input type=button value=\" Close Window \" onclick=\"window.close()\" style=\"background-color: #8B8970\">");
    out.println("</td></form>");
    out.println("</tr></table>");
    
    out.println("</div>");
    
    out.println("<div id=\"frmLayerA\" style=\"visibility: hidden; position: absolute; top: 0px; left: 0px\">");
    
        out.println("<table width=500 border=0 align=center><tr>");
        out.println("<td><font size=2><b>Instructions:</b> &nbsp;");
        out.println("Enter a new starting time and click the 'Update Starting Time' button.&nbsp; ");
        out.println("Use the 'Cancel' button to return to the previous screen.");
        out.println("</font></td></tr></table>");
    
        out.println("<br>");
    
        out.println("<form method=post name=frmStartTime id=frmStartTime>");
        out.println("<input type=hidden name=tid value=\"" + tid + "\">");
        out.println("<input type=hidden name=hole_num value=\"0\">");
        
        out.println("<table cellpadding=2 cellspacing=0><tr><td>");
   
        out.println("<input type=text name=hole_time onclick=\"TB_setCaretPos(this)\" style=\"height: 22px\" size=9 value=\"" + ((!actual_start.equals("")) ? actual_start  : tee_time) + "\"></td>");
        out.println("<td><image src=/" + rev + "/images/up.gif onclick=\"TB_adjustTime(1, TB_box_2)\" width=17 height=8><br>");
        out.println("<image src=/" + rev + "/images/shim.gif height=2 width=1><br>");
        out.println("<image src=/" + rev + "/images/down.gif onclick=\"TB_adjustTime(-1, TB_box_2)\" width=17 height=8></td>");
        out.println("<td nowrap>&nbsp; &nbsp;<input type=button value=\"Update Starting Time\" onclick=\"document.frmStartTime.submit()\" style=\"background-color: #8B8970\">");
        out.println("</td></tr><tr><td align=center colspan=3>");
        out.println("<br><br><input type=button value=\"   Cancel   \" onclick=\"hideStartTimeForm();\" style=\"background-color: #8B8970\">");
        
        out.println("</td></tr><table>");
        
        out.println("</form>");
        
    out.println("</div>");
    
    out.println("</body>");
    out.println("</html>");
    
    out.println("<script>");
    out.println("var is_gecko = /gecko/i.test(navigator.userAgent);");
    out.println("var is_ie    = /MSIE/.test(navigator.userAgent);");
    out.println("var TB_box_1 = document.frmHoleTime.hole_time;");
    out.println("var TB_box_2 = document.frmStartTime.hole_time;");
    out.println("var TB_caret_pos = 0;");
    out.println("</script>");
    
 }  // end of doGet

 
 //****************************************************************
 // Add Pace of Play entries for a specific tee time
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setDateHeader("Expires",0);
    resp.setHeader("Pragma","no-cache");
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
    resp.setContentType("text/html");

    PrintWriter out = resp.getWriter();

    out.println(SystemUtils.HeadTitle("Pace of Play"));
    
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int tid = 0;
    int hole_num = 0;
    int hr = 0;
    int min = 0;
    int fb = 0;
    int invert = 0;
    
    String hole_time = "";
    String tmp1 = "";
    String tmp2 = "";
    String [] time_part = new String [1];
    String [] time_ampm = new String [2];
    String sql_time = "";
    
    HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

    if (session == null) return;

    Connection con = Connect.getCon(req);            // get DB connection

    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the Database.", "", out, true);
        return;
    }

    // get the hole number, teecurr_id and time for this pace entry
    if (req.getParameter("tid") != null) tmp1 = req.getParameter("tid");
    if (req.getParameter("hole_num") != null) tmp2 = req.getParameter("hole_num");
    if (req.getParameter("hole_time") != null) hole_time = req.getParameter("hole_time");    
    
    try { 
        
        tid = Integer.parseInt(tmp1);
        hole_num = Integer.parseInt(tmp2);
        hole_time = hole_time.trim(); // trim the time string
        
        // first lets see if the hole_time is empty, if so then we'll erase or clear this pace entry
        if (!hole_time.equals("")) {
            
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

        } // end if hole_time empty
        
    }
    catch (Exception e) {

        out.println(SystemUtils.HeadTitle("Invalid Time For Pace of Play Entry"));
        out.println("<BODY><CENTER><BR>");
        out.println("<H3>Invalid Time For Pace of Play Entry</H3>");
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
    
    
    // see if this tee time started on the front or back (no need to if doing a delete)
    if (!hole_time.equals("")) {
        
        try {

            pstmt = con.prepareStatement("SELECT fb FROM teecurr2 WHERE teecurr_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, tid);
            rs = pstmt.executeQuery();
            if (rs.next()) fb = rs.getInt("fb");
            rs.close();
            pstmt.close();

        }
        catch (Exception e1) {

            SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
            return;
        }

    } // end if hole_time empty

    invert = hole_num;
    if (fb != 0) invert = (hole_num < 10) ? hole_num + 9 : hole_num - 9;
    
    /*
    out.println("<br>[fb]="+fb);
    out.println("<br>[hole_num]="+hole_num);
    out.println("<br>[invert]="+invert);
    */
    
    // add update pace entry
    try {
        
        if (hole_time.equals("")) {
            
            String sql = "DELETE FROM pace_entries WHERE teecurr_id = ? AND hole_number = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            pstmt.setInt(1, tid);
            pstmt.setInt(2, hole_num);
            pstmt.executeUpdate();
            
        } else {
        
            String sql = "INSERT INTO pace_entries (teecurr_id, hole_number, hole_timestamp, invert) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE hole_timestamp = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            pstmt.setInt(1, tid);
            pstmt.setInt(2, hole_num);
            pstmt.setString(3, sql_time);
            pstmt.setInt(4, invert);
            pstmt.setString(5, sql_time);
            pstmt.executeUpdate();
            
        } // end if delete or update 
        
    } catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, true);
        return;
    }
    
    
    // verify times are consecutive
    
    boolean found_error = false;
    int tmp_hole1 = 0;
    int tmp_hole2 = 0;
    int tmp_id = tid;
    int bad_pace_id = 0;
    
    try {
        
        pstmt = con.prepareStatement("SELECT * FROM pace_entries WHERE teecurr_id = ? ORDER BY invert");
         pstmt.clearParameters();
         pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();
        
        pstmt = con.prepareStatement("SELECT * FROM pace_entries WHERE teecurr_id = ? ORDER BY hole_timestamp");
         pstmt.clearParameters();
         pstmt.setInt(1, tid);
        rs2 = pstmt.executeQuery();
        
        while ( rs.next() && rs2.next() ) {
            
            if (rs.getInt("pace_entry_id") != rs2.getInt("pace_entry_id")) {
                
                found_error = true;
                tmp_hole1 = rs.getInt("hole_number");
                tmp_hole2 = rs2.getInt("hole_number");
                tmp_id = rs.getInt("teecurr_id");
                if (tmp_hole1 != 0) {
                    
                    bad_pace_id = rs.getInt("pace_entry_id");
                } else {
                    
                    bad_pace_id = rs2.getInt("pace_entry_id");
                }
                break;
            }
            
        }
        
        rs.close();
        rs2.close();
        
        out.println("<center>");
            
        if (found_error) {
            
            if (tmp_hole1 == 0 || tmp_hole2 == 0) {
                
                out.println("<h3>Conflict with starting time.</h3>");
                out.println("The pace entry you just attempted to add seems to conflict with the starting time."+bad_pace_id+"</p>");
                out.println("<form method=get>");
                out.println("<input type=hidden name=tid value=\"" + tmp_id + "\">");
                out.println("<input type=submit value=\" Go Back \" style=\"background-color: #8B8970\">");
                out.println("</form>");
                out.println("</center>");

                out.println("");
                return;                
                
            } else {
            
                // see if this invalid entry we just found was the one we just inserted (this should always be the case)
                if (tmp_hole1 == hole_num || tmp_hole2 == hole_num) {

                    // delete this invalid pace entry
                    pstmt = con.prepareStatement("DELETE FROM pace_entries WHERE pace_entry_id = ?");
                    pstmt.clearParameters();
                    pstmt.setInt(1, bad_pace_id);
                    pstmt.executeUpdate();

                    out.println("<h3>Hole " + tmp_hole1 + " or " + tmp_hole2 + " contains an invalid time.</h3>");
                    out.println("<p>The pace entry you just attempted has not been saved.  Please check your times to ensure they are consecutive.  A hole of zero indicates the starting time.</p>");

                } else {

                    out.println("<h3>Hole " + tmp_hole1 + " or " + tmp_hole2 + " contains an invalid time.</h3>");
                    out.println("<p>You will need to check your pace entry times to make sure they are all consecutive.</p>");
                }

                out.println("<form method=get>");
                out.println("<input type=hidden name=tid value=\"" + tmp_id + "\">");
                out.println("<input type=submit value=\" Go Back \" style=\"background-color: #8B8970\">");
                out.println("</form>");

                out.println("</center>");

                out.println("");
                return;
                
            } // end if error was related to the starting time or not
            
        }// end if error found
        
    } catch (Exception e) {
        
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, true);
        return;
    } // end look for invalid times try/catch
    
    
    // if updating start time, return to main page instead of going to detail page
    if (hole_num == 0) {
        doGet(req, resp); 
        return;
    }
    
    try {
        
        // get the highest hole number we have a pace entry for
        pstmt = con.prepareStatement("SELECT hole_timestamp, hole_number, invert FROM pace_entries WHERE teecurr_id = ? ORDER BY invert DESC LIMIT 1");
        pstmt.clearParameters();
        pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();

        int highest_hole_num = 0;

        if (rs.next()) {

            highest_hole_num = rs.getInt("hole_number");
            //invert = rs.getInt("invert");
            //hole_time = rs.getString("hole_timestamp");
        }

        rs.close();  

        if (hole_num == highest_hole_num) {

            // get the current computed pace
            int calc_pace_status = 0;
            calc_pace_status = getPaceStatus(tid, con, out);
            doUpdatePaceStatus(tid, calc_pace_status, out, con);
        }
    
    } catch (Exception e) {
        
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, true);
        return;
    }
    
    doPaceDetails(req, tid, out, con);
    return;
    
    //
    // Data has been saved and verified
    //
    /*
    out.println("<script>");
    out.println("function openPaceDetails(pID) {");
    out.println(" var z = window.open ('Proshop_pace?tid=' +pID+'&details','paceDetailsPopup','width=820,height=400,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
    out.println("}");
    out.println("</script>");
    
    // tell them pace entry saved ok
    out.println("<h3>Your Pace of Play update has been saved.</h3><br>");
    
    // get the current computed pace
    int calc_pace_status = 0;
    calc_pace_status = getPaceStatus(tid, con, out);
    
    // show current pace status indicator, if not set tell them
    
    
    
    // instructions / verbiage to explain possible actions regarding setting pace and buttons
    
    
    // buttons
    out.println("<form method=get>");
    out.println("<input type=hidden name=tid value=\"" + tmp_id + "\">");
    out.println("<input type=button value=\"Close Window\" onclick=\"window.close()\">");
    out.println("&nbsp; &nbsp; &nbsp; ");
    out.println("<input type=submit value=\"Return to Pace Entry\">");
    out.println("&nbsp; &nbsp; &nbsp; ");
    out.println("<input type=button value=\" Show Pace Details \" onclick=\"openPaceDetails(" + tmp_id + ");\">");
    out.println("</form>");
    out.println("</center>");
    return;

    */
    
 }   // end of doPost   

 
 //****************************************************************
 // Display pace of play detail for specified tee time
 // details include expected and actual times for each hole
 //****************************************************************
 //
 private void doPaceDetails(HttpServletRequest req, int pTeeCurrID, PrintWriter out, Connection con) {
     
    // show a table with each hole (in order of progression) with the
    // expected time and their actual time (if recorded)

    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    String hole_time = "";
    String hole_pace = "";

    int clubparm_id = 0;
    int i = 0;
    int i2 = 0;
    
    int bpace_min = 0;
    int bpace_sec = 0;

    // display tee time summary
    out.println(SystemUtils.HeadTitle("Pace of Play Details"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<center><h2>Pace of Play Details</h2></center>");
    
    int ts_hr = 0;
    int ts_min = 0;
    int fb = 0;
    int pace_status_id = 0;
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String course = "";

    try {
        
        pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            ts_hr = rs.getInt("hr");
            ts_min = rs.getInt("min");
            fb = rs.getInt("fb");
            pace_status_id = rs.getInt("pace_status_id");
            course = rs.getString("courseName");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");

        }

        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }

    // if any of the players are only doing 9
    boolean all9hole = true;
    if (!player1.equals("")) if (p91 == 0) all9hole = false;
    if (!player2.equals("")) if (p92 == 0) all9hole = false;
    if (!player3.equals("")) if (p93 == 0) all9hole = false;
    if (!player4.equals("")) if (p94 == 0) all9hole = false;
    if (!player5.equals("")) if (p95 == 0) all9hole = false;
    
    String tee_time = SystemUtils.getSimpleTime(ts_hr, ts_min);
    
    // see if there is an actual start time for the tee time (pace entry for hole 0)
    String actual_start = "";
    try {
        
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(hole_timestamp, '%H') AS as_hr, DATE_FORMAT(hole_timestamp, '%i') AS as_min FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            ts_hr = rs.getInt("as_hr");
            ts_min = rs.getInt("as_min");
            actual_start = SystemUtils.getSimpleTime(rs.getInt("as_hr"), rs.getInt("as_min"));
        }

        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    out.println("<table cellpadding=0 cellspacing=0 style=\"border: 1px solid #336633\" align=center bgcolor=\"#F5F5DC\"><tr><td>");
    
    out.println("<table border=0>");
    out.println("<tr style=\"color: #336633\"><td>&nbsp;</td><td nowrap><b><u>Start Time</b></td><td nowrap>&nbsp; &nbsp;</td><td><b><u>Course</b></td>");
    out.println("<td nowrap>&nbsp; &nbsp;</td><td><b><u>F/B</b></td><td nowrap>&nbsp; &nbsp;</td><td><b><u>Players</b></td>");
    out.println("<td nowrap>&nbsp; &nbsp;</td>");
    out.println("</tr><tr valign=top>");
    out.println("<td></td>");
    out.println("<td nowrap><font size=2>"); // + ((!actual_start.equals("")) ? actual_start + "*" : tee_time) + "</a></td>");
    out.println(tee_time + (((!actual_start.equals("")) && (!actual_start.equalsIgnoreCase(tee_time))) ? "<br>" + actual_start + "*" : "") + "</a></td>");
    out.println("<td></td>");
    out.println("<td nowrap><font size=2>" + course + "</td>");
    out.println("<td></td>");
    out.println("<td nowrap><font size=2>" + ((fb == 0) ? "Front" : "Back") + "</td>");
    out.println("<td></td>");
    out.println("<td colspan=2><font size=2>");
    out.print(player1);
    if (!player2.equals("")) out.print(", <nobr>" + player2 + "</nobr>");
    if (!player3.equals("")) out.print(", <nobr>" + player3 + "</nobr>");
    if (!player4.equals("")) out.print(", <nobr>" + player4 + "</nobr>");
    if (!player5.equals("")) out.print(", <nobr>" + player5 + "</nobr>");
    out.println("</td>");
    out.println("</tr></table>");
    //out.println("<br>");
    // end tee time summary
    
    //out.println("</td></tr><tr><td>");
    
    int exp_hr = 0;
    int exp_min = 0;
    int exp_sec = 0;
    int finish_hole = 19;
    
    try {

        clubparm_id = SystemUtils.getClubParmIdFromTeeCurrID(pTeeCurrID, con);
        
        out.println("<table style=\"font-size:10pt\" border=1>");
        
        // first row is header, row title, start, holes 1-18
        out.println("<tr align=center>");
                
        if (fb == 0) {
        
            if (all9hole) finish_hole = 10;
            for (i=0;i<finish_hole;i++) {

               if (i == 0) {

                    out.println("<td></td>");
                } else {

                    out.println("<td nowrap><b>" +i+ "</b></td>");
                }
            }
        } else {
        
            finish_hole = (all9hole) ? 19 : 9;
            for (i=10;i!=finish_hole;i++) {

               if (i == 10) {

                    out.println("<td></td>");
               }
               
               out.println("<td nowrap><b>" +i+ "</b></td>");
               
               if (i == 18 && !all9hole) i = 0;
            }
            if (!all9hole) out.println("<td nowrap><b>9</b></td>");
        }
        out.println("</tr>");
        // end header row
        
        
        // second row is benchmark times
        // get the pace benchmarks
                //((all9hole) ? "AND" : "") + 
        pstmt = con.prepareStatement("" +
                "SELECT hole_pace " +
                "FROM pace_benchmarks " +
                "WHERE clubparm_id = ? " +
                "ORDER BY " + ((fb == 0) ? "hole_number" : "invert"));
        pstmt.clearParameters();
        pstmt.setInt(1, clubparm_id);
        rs = pstmt.executeQuery();
        
        out.println("<tr align=center>");
        out.println("<td nowrap>Benchmark</td>");
        String pace_time = "";
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.HOUR, ts_hr);
        cal.set(Calendar.MINUTE, ts_min);
        cal.set(Calendar.SECOND, 0);
        
        i = 0;
        
        while (rs.next()) {

            bpace_min = rs.getInt("hole_pace");
            cal.add(Calendar.MINUTE, bpace_min);
            
            exp_hr = cal.get(Calendar.HOUR);
            exp_min = cal.get(Calendar.MINUTE);
            
            if (exp_hr == 0) exp_hr = 12;
            
            out.println("<td nowrap>" + exp_hr + ":" + SystemUtils.ensureDoubleDigit(exp_min) + "<!--:" + SystemUtils.ensureDoubleDigit(exp_sec) + "--></td>");
            i++;
            if (all9hole && i == 9) break;
        }
        
        out.println("</tr>");
        rs.close();
        // end benchmark times
        
        
        
        // third row are their pace entries
        // get their pace entries
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(hole_timestamp, '%l:%i') AS pretty_time, hole_number, invert FROM pace_entries WHERE teecurr_id = ? ORDER BY invert");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();
        
        out.println("<tr valign=center>");
        out.println("<td nowrap><i>Their Pace</i></td>");
        
        i = 1;
        
        while (rs.next()) {
            
            if (i != rs.getInt("invert")) {
                
                for (i=i;i<rs.getInt("invert");i++) {
                    
                    out.println("<td nowrap>&nbsp;</td>");
                }
            }
            
            if (rs.getInt("hole_number") != 0) {
                
                hole_time = rs.getString("pretty_time");
                out.println("<td nowrap><i>" + hole_time + "</i></td>");
                i++;
            }
            
            if (all9hole && i > 9) break;
            //if (fb != 0 && i > 18) i = 1;
        }
        // done with their pace settings, incase they didn't end get a pace on 18 make sure
        // any remainig hole are displayed as N/A
        
        finish_hole = (all9hole) ? 10 : 19;
        if (i != finish_hole) {
            
            for (i=i;i<finish_hole;i++) {

                out.println("<td nowrap>&nbsp;</td>");
            }
        }
        out.println("</tr>");
            
        rs.close();
        
        
        out.println("</table>");
     
    } catch (Exception el) {
        SystemUtils.buildDatabaseErrMsg(el.getMessage(), el.toString(), out, false);
    }
    
    out.println("</td></tr></table>");
    
    // done with detailed table
    
    
    /*
     * HIDE STATUS STUFF
    
    
    
    
    // show current pace and select box to change it
    
    out.println("<br><br><center>");
    
    int calc_pace_status = 0;
    calc_pace_status = getPaceStatus(pTeeCurrID, con, out);
    String btn_text = (pace_status_id == 0) ? "Set " : "Update ";
    
    // current is already here (pace_status_id)
    out.println("<form method=get name=frmStatus id=frmStatus>");
    out.println("<input type=hidden name=tid value=\"" + pTeeCurrID + "\">");
    out.println("<input type=hidden name=details value=\"\">");
    
    /*
    // start select box with the status options
    out.println("<b>Current Pace Status: </b>&nbsp;<select name=pace_status_id size=1>");
    out.print("<option value=0");
    if (pace_status_id == 0) out.print(" selected");
    out.print(">Not Set");
    
    int tmp_id = 0;
    String tmp_color = "";
    String tmp_name = "";
    String tmp_name_calc = "";
        
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");
                
        while (rs.next()) {
            
            tmp_id = rs.getInt("pace_status_id");
            tmp_name = rs.getString("pace_status_name");
            if (tmp_id == calc_pace_status) {
                tmp_name_calc = tmp_name;
                tmp_color = rs.getString("pace_status_color");
            }
            
            out.print("<option value=" + tmp_id);
            if (tmp_id == pace_status_id) out.print(" selected");
            out.print(">" + tmp_name);
        }
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
    }
    
    out.println("</select>");
    */
    /*
     * HIDE STATUS STUFF
    out.println("<table align=center>");
    out.println("<tr><td align=center><b>Current Pace Status: </b>&nbsp;");

    out.println("<br><input type=submit value=\"" + btn_text + " Pace\" onclick=\"\" style=\"background-color: #8B8970\">");    
    
    out.println("</td><td>");
    
    //out.print("<option value=0");
    //if (pace_status_id == 0) out.print(" selected");
    //out.print(">Not Set");
    
    //out.println("<input type=radio name=pace_status_id value=\"\">");
    
    int tmp_id = 0;
    String tmp_color = "";
    String tmp_name = "";
    String tmp_name_calc = "";
        
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");
                
        while (rs.next()) {
            
            tmp_id = rs.getInt("pace_status_id");
            tmp_name = rs.getString("pace_status_name");
            if (tmp_id == calc_pace_status) {
                tmp_name_calc = tmp_name;
                tmp_color = rs.getString("pace_status_color");
            }
            
            out.print("<input type=radio name=pace_status_id value=\"" + tmp_id + "\"");
            
            if (tmp_id == pace_status_id) out.print(" checked");
            out.print(">" + tmp_name + "<br>");
        }
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
    }
        
    
    
    out.println("</table>");
    */
    out.println("<center><br>");
    
    // Check Feature Access Rights for current proshop user
    if (SystemUtils.verifyProAccess(req, "TS_PACE_UPDATE", con, out)) {
        out.println("<input type=button value=\"Back to Pace Entry\" onclick=\"document.location.href='Proshop_pace?tid=" + pTeeCurrID + "'\" style=\"background-color: #8B8970\">");
        out.println("&nbsp; &nbsp; &nbsp;");
    }
    out.println("<input type=button value=\" Close Window \" onclick=\"window.close()\" style=\"background-color: #8B8970\">");
    out.println("</form></center>");
    /*
    if (tmp_name_calc.equals("")) tmp_name_calc = "N/A";
    out.println("<div style=\"position: relative; top:-157px; left: 12px\"><b>Computed Pace Status: </b>&nbsp; &nbsp;<input type=text value=\" " + tmp_name_calc + "\" style=\"background-color:" + tmp_color + "; border: 1px solid black\" onfocus=\"document.frmStatus.pace_status_id.focus()\"></div>");
    
    out.println("</center>");
    */
 }
 
 
 //****************************************************************
 // Accepts a teecurr_id and returns the pace_status_id for it
 //****************************************************************
 //
 private static int getPaceStatus(int pTeeCurrID, Connection con, PrintWriter out) {

    int retVal = 0;
    int hole_num = 0;
    int pace_minutes = 0;
    int their_minutes = 0;
    int course_id = 0;
    int diff = 0;
    int fb = 0;
    int invert = 0;
    
    boolean on_pace = false;
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    String hole_time = "";
    String tmp_stime = "";

    course_id = SystemUtils.getClubParmIdFromTeeCurrID(pTeeCurrID, con);
    
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
 
        
        // there is not enough data to compute pace - return a 0
        if (hole_num < 1) return 0;
        
        
        // get the benchmark pace for this hole
        pstmt = con.prepareStatement("SELECT SUM(hole_pace) AS pace_time FROM pace_benchmarks WHERE clubparm_id = ? AND " + ((fb == 0) ? "hole_number" : "invert") + " <= ?");
        pstmt.clearParameters();
        pstmt.setInt(1, course_id);
        pstmt.setInt(2, invert);
        rs = pstmt.executeQuery();
        
        if (rs.next()) pace_minutes = rs.getInt("pace_time");
        rs.close();
        
        
        // get the their starting time  // DATE_FORMAT(hole_timestamp, '%H') AS ts_hr, DATE_FORMAT(hole_timestamp, '%i') AS ts_min, 
        pstmt = con.prepareStatement("SELECT hole_timestamp FROM pace_entries WHERE teecurr_id = ? AND hole_number = 0");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();
        
        if (rs.next()) tmp_stime = rs.getString("hole_timestamp");
        rs.close();
        
        
        // we now have our benchmark times in workable formats
        
        // hole_time = mysql timestamp of most recent pace entry (24 hr format hh:mm)
        // pace_minutes = # of minutes it should take to reach this hole
                
        // get the difference between their times  (pace - actual)
        // mysql has a perfect internal function to compute this
        Statement stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT HOUR(SUBTIME(\"" + tmp_stime + "\", \"" + hole_time + "\")) AS hr, MINUTE(SUBTIME(\"" + tmp_stime + "\", \"" + hole_time + "\")) AS min");
        
        if (rs.next()) their_minutes = (rs.getInt(1) * 60) + rs.getInt(2);
        diff = (their_minutes - pace_minutes);
        
        
        out.println("<!-- ");
        out.println("tmp_stime=" + tmp_stime);
        out.println("hole_time=" + hole_time);
        out.println("their_minutes=" + their_minutes);
        out.println("pace_minutes=" + pace_minutes);
        out.println("diff_minutes=" + diff);
        out.println("invert=" + invert);
        out.println("hole_num=" + hole_num);
        out.println("-->");
        
        
        
        // if the difference is zero or greater then they are on pace, no need to hit db
        if (diff <= 0) {

            // behind pace
            on_pace = true;
            return 1;
        }
        
        // let see how far behind they are (they could still be considered 'on pace')
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");
        
        double allowable_leeway;
        retVal = 3; // default to slowest since the rs loop may not hit if they are slower then the slowest range
        
        while (rs.next()) {
            
            allowable_leeway = (rs.getDouble("pace_leeway") * pace_minutes);
            
            if (diff < allowable_leeway) {
                
                retVal = rs.getInt("pace_status_id");
                break;
            }
            
        } // status loop
        
        rs.close();
        
    }
    catch (Exception e) {
        
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
        return retVal;
    }

    return retVal;
 }
 
 
 //****************************************************************
 // Common method for updating a pace time entry to the db table
 //****************************************************************
 //
 private void doUpdatePaceStatus(int pTeeCurrID, int pPaceStatusID, PrintWriter out, Connection con) {
     
    try {
        
        PreparedStatement pstmt = con.prepareStatement("UPDATE teecurr2 SET pace_status_id = ? WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pPaceStatusID);
        pstmt.setInt(2, pTeeCurrID);
        pstmt.executeUpdate();
        
    }
    catch (Exception e) {
        
        SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
    }
 }
 
 
 //****************************************************************
 // Common method for displaying the front nine holes for selection
 //****************************************************************
 //
 private void buildFrontNine(int pTeeCurrID, PrintWriter out, Connection con) {
          
    out.println("<td><b>Front 9</b></td>");
    
    buildFlagTD("/images/flag_1.gif", "Hole 1", 1, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_2.gif", "Hole 2", 2, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_3.gif", "Hole 3", 3, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_4.gif", "Hole 4", 4, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_5.gif", "Hole 5", 5, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_6.gif", "Hole 6", 6, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_7.gif", "Hole 7", 7, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_8.gif", "Hole 8", 8, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_9.gif", "Hole 9", 9, pTeeCurrID, out, con);
    
 }
 
 
 //****************************************************************
 // Common method for displaying the back nine holes for selection
 //****************************************************************
 //
 private void buildBackNine(int pTeeCurrID, PrintWriter out, Connection con) {
     
    out.println("<td><b>Back 9</b></td>");
    
    buildFlagTD("/images/flag_10.gif", "Hole 10", 10, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_11.gif", "Hole 11", 11, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_12.gif", "Hole 12", 12, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_13.gif", "Hole 13", 13, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_14.gif", "Hole 14", 14, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_15.gif", "Hole 15", 15, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_16.gif", "Hole 16", 16, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_17.gif", "Hole 17", 17, pTeeCurrID, out, con);
    buildFlagTD("/images/flag_18.gif", "Hole 18", 18, pTeeCurrID, out, con);
     
 }
 
 
 //****************************************************************
 // Common method for building the individual holes for f/b nine
 //****************************************************************
 //
 private void buildFlagTD(String pImgSrc, String pAltText, int pHoleNumber, int pTeeCurrID, PrintWriter out, Connection con) {
    
    String html_onmover = "this.style.border='2px solid #8B8970';";
    String html_onmout = "this.style.border='2px solid white';";
    String html_defstyle = "border: 2px solid white";
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        
        pstmt = con.prepareStatement("SELECT DATE_FORMAT(hole_timestamp, '%H') AS hr, DATE_FORMAT(hole_timestamp, '%i') AS min FROM pace_entries WHERE teecurr_id = ? AND hole_number = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        pstmt.setInt(2, pHoleNumber);
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            
            int hr = rs.getInt("hr");
            int min = rs.getInt("min");
            
            pAltText = SystemUtils.getSimpleTime(hr, min);
            
            html_onmover = "this.style.border='2px solid #8B8970';";
            html_onmout = "this.style.border='2px solid black';";
            html_defstyle = "border: 2px solid black";
            
        }
        
    } catch (Exception ignore) {
        
    }
    
    String timeText = pAltText;
    if (pAltText.startsWith("Hole")) timeText = "";
    
    out.println("<td><img src=\"/" +rev+ "/" + pImgSrc + "\" width=\"24\" height=\"24\" border=\"0\" alt=\"" + pAltText + "\" onclick=\"setHole(" + pHoleNumber + ",'" + timeText + "')\" onmouseover=\"" + html_onmover + "\" onmouseout=\"" + html_onmout + "\" style=\"" + html_defstyle + "\"></td>");
    
    return;     
 }
 
}
