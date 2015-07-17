/***************************************************************************************
 *   Proshop_diary: This servlet will implement the diary functionality
 *
 *
 *   Called by:     Proshop_sheet (doPost)
 *                  Proshop_oldsheet (doPost)
 *
 *
 *   Created:       2/15/2005 by Paul
 *
 *
 *   Last Updated:  
 *
 *                  
 *                  todo: 
 *                  add bold to selected radio options
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.zip.*;
import java.sql.*;
import java.lang.Math;
import java.text.DateFormat;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.client.action.ActionHelper;

public class Proshop_diary extends HttpServlet {

    static int iRadioRow = 0;
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
 
    private PrintWriter out;
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 }
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    
    Locale.setDefault( new Locale("en", "US"));
    
    iRadioRow = 0;
    
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    
    //PrintWriter out = resp.getWriter();                           // normal output stream
    out = resp.getWriter();
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }

    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        displayDatabaseErrMsg("Can not connect.");
        return;
    
    }
    
    // 
    //  Declare our local variables
    //
    int year = 0;
    int month = 0;
    int day = 0;
    int index = 0;    
    int multi = 0;                                                  // multiple course support flag
    int courseCount = 0;
    
    // vairables for incoming form elements
    String f_course = "";    
    Statement stmt = null;
    PreparedStatement pstmtc = null;
    ResultSet rs = null;
   
    //
    //  Get name of club for this user
    //
    String club = (String)session.getAttribute("club");             // get club name
    String user = (String)session.getAttribute("user");             // get user
        
    //
    //  Array to hold the course names
    //
    String courseName = "";
    String [] course = new String [20];                             // max of 20 courses per club

    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");
        if (rs.next()) {
            multi = rs.getInt(1);
        } else {
            displayDatabaseErrMsg("Club setup not complete.");
            return;
        }
        
        stmt.close();

        if (multi != 0) {                                           // if multiple courses supported for this club

            while (index< 20) {
                course[index] = "";                                 // init the course array
                index++;
            }

            index = 0;

            //
            //  Get the names of all courses for this club
            //
            
            stmt = con.createStatement();                           // create a statement
            rs = stmt.executeQuery("SELECT courseName FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < 20) {
                courseName = rs.getString(1);
                course[index] = courseName;                         // add course name to array
                index++;
            }
            
            courseCount = index;                                    // remember the number of courses
            
            stmt.close();

            //
            //  Add an 'ALL' option at the end of the list
            //
            if (index < 20) { course[index] = "-ALL-"; }
            
        }
    }
    catch (Exception e) {
        displayDatabaseErrMsg(e.getMessage());
        return;
    }
    
    int weather_am = 0;
    int weather_mid = 0;
    int weather_pm = 0;
    int am_course = 0;
    int mid_course = 0;
    int pm_course = 0;
    int diary_id = 0;  // if zero then we are adding, if > 0 then we've loaded and are updating
    int loadcc = 0;
    String notes = "";
    String msg = "";
    String todo = "";
    String tmp_cc = "";
    
    //out.println("<p>" + Integer.parseInt(req.getParameter("course_count")) + "</p>");
    
    if (req.getParameter("todo") != null) { todo = req.getParameter("todo"); }
    if (!todo.equals("add") && !todo.equals("update")) todo = "";               // strict enfore allowable values
    
    try {
        if (req.getParameter("year") != null) { year = Integer.parseInt(req.getParameter("year")); } else { year = 1900; }
        if (req.getParameter("month") != null) { month = Integer.parseInt(req.getParameter("month")); } else { month = 1; }
        if (req.getParameter("day") != null) { day = Integer.parseInt(req.getParameter("day")); } else { day = 0; }
    } catch (Exception e) {
        month = 1;
        year = 1900;
        day = 0;
    }
    
    GregorianCalendar cal = new GregorianCalendar(year, month - 1, day);
    
    String short_year = Integer.toString(year).substring(2, 4);
    String diary_compare_date = month + "/" + day + "/" + short_year;           // date in a format to compare against
    
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.FULL);
    DateFormat df_short = DateFormat.getDateInstance(DateFormat.SHORT);
    
    //out.println("<br>df_short=" + df_short.format(cal.getTime()));
    //out.println("diary_compare_date=" + diary_compare_date + "<br>");
    
    if (!diary_compare_date.equals(df_short.format(cal.getTime()))) {
        // they don't match
        msg = "Invalid date detected!  Defaulting to today.";
        cal = new GregorianCalendar();                                          // todays date
        month = cal.get(cal.MONTH) + 1;
        day = cal.get(cal.DAY_OF_MONTH);
        year = cal.get(cal.YEAR);
    }
    
    String diary_date = year + "-" + month + "-" + day;                         // date in mysql format
    out.println("diary_date=" + diary_date + "<br>");
    
    //
    // see what we are here to do a particular action
    //
    if (todo.equals("add")) {
        
        // add diary entry
        try {
            pstmtc = con.prepareStatement ("INSERT INTO diary VALUES (NULL, ?, ?, ?, ?, ?)");
            pstmtc.clearParameters();
            pstmtc.setString(1, diary_date);
            pstmtc.setInt(2, Integer.parseInt(enforceOptionDefault(req, "am_weather")));
            pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "midday_weather")));
            pstmtc.setInt(4, Integer.parseInt(enforceOptionDefault(req, "pm_weather")));
            pstmtc.setString(5, req.getParameter("notes"));
            //out.println("<br>>");
            //out.println(pstmtc.toString());
            rs = pstmtc.executeQuery();
            
            int i = 0;
            
            //while (req.getParameter("am_course_" + i) != null) {
            for (i=0; i < courseCount; i++) {
                
                pstmtc = con.prepareStatement ("INSERT INTO diarycc VALUES (NULL, ?, ?, ?, ?, ?)");
                pstmtc.clearParameters();
                pstmtc.setString(1, diary_date);
                pstmtc.setString(2, course[i]);
                pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "am_course_" + i)));
                pstmtc.setInt(4, Integer.parseInt(enforceOptionDefault(req, "midday_course_" + i)));
                pstmtc.setInt(5, Integer.parseInt(enforceOptionDefault(req, "pm_course_" + i)));
                //out.println("<br>"+i+">");
                //out.println(pstmtc.toString());
                rs = pstmtc.executeQuery();
                
                //i++;
            
            }
            
            pstmtc.close();
            msg = "Diary entry saved successfully.";
        }
        catch (Exception e) {
            displayDatabaseErrMsg("[INSERT ERROR] " + e.getMessage());
            return;
        }
        
    } else if (todo.equals("update")) {
    
        // update an existing diary entry
        try {
            pstmtc = con.prepareStatement ("UPDATE diary SET weather_am = ?, weather_mid = ?, weather_pm = ?, notes = ? WHERE diary_entry_id = ?");
            pstmtc.clearParameters();
            pstmtc.setInt(1, Integer.parseInt(enforceOptionDefault(req, "am_weather")));
            pstmtc.setInt(2, Integer.parseInt(enforceOptionDefault(req, "midday_weather")));
            pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "pm_weather")));
            pstmtc.setString(4, req.getParameter("notes"));
            pstmtc.setInt(5, Integer.parseInt(req.getParameter("diary_id")));
            rs = pstmtc.executeQuery();
            
            int i = 0;
            
            //while (req.getParameter("am_course_" + i) != null) {
            for (i=0; i < courseCount; i++) {
                pstmtc = con.prepareStatement ("UPDATE diarycc SET am_condition = ?, mid_condition = ?, pm_condition = ? WHERE diary_date = ? AND course_name = ?");
                pstmtc.clearParameters();
                
                pstmtc.setInt(1, Integer.parseInt(enforceOptionDefault(req, "am_course_" + i)));
                pstmtc.setInt(2, Integer.parseInt(enforceOptionDefault(req, "midday_course_" + i)));
                pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "pm_course_" + i)));
                pstmtc.setString(4, diary_date);
                pstmtc.setString(5, course[i]);
                //out.println("<br>"+i+">");
                //out.println(pstmtc.toString());
                rs = pstmtc.executeQuery();
                //i++;
            }
            
            pstmtc.close();
            msg = "Diary entry updated successfully.";
        }
        catch (Exception e) {
            displayDatabaseErrMsg("[UPDATING]" + e.getMessage());
            return;
        }
        
    } 
    
        
    // no todo action here so lets load the diary info from the specified day if any exists
    try {
        pstmtc = con.prepareStatement("SELECT * FROM diary WHERE diary_date = ?"); 
        pstmtc.clearParameters();
        pstmtc.setString(1, diary_date);
        rs = pstmtc.executeQuery();

        while (rs.next()) {

            weather_am = rs.getInt("weather_am");
            weather_mid = rs.getInt("weather_mid");
            weather_pm = rs.getInt("weather_pm");
            notes = rs.getString("notes");
            diary_id = rs.getInt("diary_entry_id");
            loadcc = 1;
        }

        pstmtc.close();
    }
    catch (Exception e) {
        displayDatabaseErrMsg(e.getMessage());
        return;
    }
    
    
    //
    //
    // end of preprocessing / start page output
    
    //out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts.js\"></script>");
    
    //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/styles.css\">");
    
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Diary Entry</title>");
    
    out.println("<style>");
    out.println(".textA {font-family: verdana; font-size: 12px; font-weight: normal; color: black}");
    out.println(".textB {font-family: verdana; font-size: 14px; font-weight: bold; color: white}");
    out.println("</style>");
    
    out.println("<script type=\"text/javascript\">");
    out.println("var iCourseCount = " + courseCount + ";");
    
    out.println("function cancelEntry() {");
    out.println(" if (confirm('Are you sure you want to close this window without saving.') == true ) window.close();");
    out.println("}");
        
    // may need to add another check to see if there are notes here AND invalid radio selections, 
    // if there are then just set all in that block to unchecked so that they're not sent back here for processing
    out.println("function submitEntry() {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
    /*
    out.println(" if (f.notes.value == '' ) {");
    out.println("  if (isOptionChecked('allday_course') == false) {");
    out.println("   if (isOptionChecked('am_course') == false || isOptionChecked('midday_course') == false || isOptionChecked('pm_course') == false) {");
    out.println("    alert('You must choose a course condition for either each time of day or all day.\\n\\nIf you are adding a journal entry this part will not be required.');");
    out.println("    return;");
    out.println("   }");
    out.println("  }");
    out.println("  if (isOptionChecked('allday_weather') == false) {");
    out.println("   if (isOptionChecked('am_weather') == false || isOptionChecked('midday_weather') == false || isOptionChecked('pm_weather') == false) {");
    out.println("    alert('You must choose a weather condition for either each time of day or all day.\\n\\nIf you are adding a journal entry this part will not be required.');");
    out.println("    return;");
    out.println("   }");
    out.println("  }");
    out.println(" }");
    */
    //out.println(" f.todo.value = 'add';");
    out.println(" f.submit();");
    out.println("}");
    
    out.println("function isOptionChecked(pElem) {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  if (eval('f.' + pElem + '[' + i + '].checked')) return true;");
    out.println(" }");
    out.println(" return false;");
    out.println("}");
    
    out.println("function setCourseCond(el) {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
    out.println(" var a = getSelectedIndex(el);");
    out.println(" // select same for each of the other courses");
    out.println(" for (cc=1; cc <= iCourseCount-1; cc++) {");
    out.println("  e = el.substring(0, el.length - 1) + cc;");
    out.println("  // before setting, check to make sure nothing is already selected");
    out.println("  if (getSelectedIndex(e) == -1) eval('f.' + e + '[' + a + '].checked = true');");
    out.println(" }");
    out.println("}");
    
    out.println("function getSelectedIndex(pElem) {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
    out.println(" var a = -1; // return -1 if non where checked");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  if ((eval('f.' + pElem + '[' + i + '].checked == true')) == true) { a = i; break }");
    out.println(" }");
    out.println(" return a;");
    out.println("}");
    
    out.println("</script>");
            
    out.println("</head>");
    out.println("<body bgcolor=white>");
    
    
    
    // this is the form that gets submitted when the user completes a diary entry
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_diary\" method=\"post\" name=\"frmDiaryEntry\" id=\"frmDiaryEntry\">");
    out.println("<input type=hidden name=todo value='" + ((diary_id == 0) ? "add" : "update") + "'>");
    out.println("<input type=hidden name=diary_id value='" + diary_id + "'>");
    out.println("<input type=hidden name=month value='" + month + "'>");
    out.println("<input type=hidden name=day value='" + day + "'>");
    out.println("<input type=hidden name=year value='" + year + "'>");
    //out.println("<input type=hidden name=course_count value='" + courseCount + "'>");
    
    out.println("<center><font class=textB style=\"color: black\">Journal Entry for " + df_full.format(cal.getTime()) + "</font>" +
                ((!msg.equals("")) ? "<br><br><font class=textA style=\"color: red\"><b>" + msg + "</b></font>" : "") +
                "</center>");
    
    out.println("<br><br>");
    
    
    //
    //  start displaying conditions
    //
    
    out.println("<table cellspacing=0 cellpadding=5><tr valign=top><td>");
    
    out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");
    
    out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">Weather Conditions</td></tr><tr><td>");
    
    startBorderBox();
     startBox("AM");
      drawRadioTableRow("Good", "am_weather", "1", "", ((weather_am == 1) ? 1 : 0));
      drawRadioTableRow("Fair", "am_weather", "2", "", ((weather_am == 2) ? 1 : 0));
      drawRadioTableRow("Poor", "am_weather", "3", "", ((weather_am == 3) ? 1 : 0));
     endBox();
    endBorderBox();
    
    out.println("</td><td>");
    
    startBorderBox();
     startBox("&nbsp;Mid-day&nbsp;");
      drawRadioTableRow("Good", "midday_weather", "1", "", ((weather_mid == 1) ? 1 : 0));
      drawRadioTableRow("Fair", "midday_weather", "2", "", ((weather_mid == 2) ? 1 : 0));
      drawRadioTableRow("Poor", "midday_weather", "3", "", ((weather_mid == 3) ? 1 : 0));
     endBox();
    endBorderBox();
    
    out.println("</td><td>");
    
    startBorderBox();
     startBox("PM");
      drawRadioTableRow("Good", "pm_weather", "1", "", ((weather_pm == 1) ? 1 : 0));
      drawRadioTableRow("Fair", "pm_weather", "2", "", ((weather_pm == 2) ? 1 : 0));
      drawRadioTableRow("Poor", "pm_weather", "3", "", ((weather_pm == 3) ? 1 : 0));
     endBox();
    endBorderBox();
    
    out.println("</td></tr></table>");
   
    
    out.println("</td><td rowspan=3 class=textB style=\"color: black\">");
    
    out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");
    
    out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">Daily Notes</td></tr><tr><td>");
    out.println("<textarea name=notes cols=32 rows=14 class=textA style=\"width: 275px; height: 250px\">" + notes + "</textarea>");
    
    out.println("</td></tr></table>");
    
    out.println("<br><br><center>");
    out.println("<input type=button name=btnCancel value=\"Close\" onclick=\"cancelEntry()\" style=\"width: 120px\">");
    out.println("&nbsp; &nbsp;");
    out.println("<input type=button name=btnSubmitEntry value=\"Submit Entry\" onclick=\"submitEntry()\" style=\"width: 120px\">");
    out.println("</center>");
    
    
    out.println("</td></tr><tr><td>");
    
    
    index = 0;
    courseName = course[index];
    String sTemp = "";

    while ((!courseName.equals( "" )) && (index < 20) && (!courseName.equals( "-ALL-" ))) {

        if (loadcc == 1) {
            
            try {
                pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date = ? AND course_name = ?"); 
                pstmtc.clearParameters();
                pstmtc.setString(1, diary_date);
                pstmtc.setString(2, courseName);
                rs = pstmtc.executeQuery();

                while (rs.next()) {
                    am_course = rs.getInt("am_condition");
                    mid_course = rs.getInt("mid_condition");
                    pm_course = rs.getInt("pm_condition");
                }

                pstmtc.close();
            }
            catch (Exception e) {
                displayDatabaseErrMsg(e.getMessage());
                return;
            }
        
        }
        
        
        out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");

        out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">" + courseName + "<br>Course Conditions</td></tr><tr><td>");

        sTemp = (index == 0) ? "setCourseCond(this.name)" : "";
        startBorderBox();
         startBox("AM");
          drawRadioTableRow("Good", "am_course_" + index, "1", sTemp, ((am_course == 1) ? 1 : 0));
          drawRadioTableRow("Fair", "am_course_" + index, "2", sTemp, ((am_course == 2) ? 1 : 0));
          drawRadioTableRow("Poor", "am_course_" + index, "3", sTemp, ((am_course == 3) ? 1 : 0));
         endBox();
        endBorderBox();

        out.println("</td><td>");

        startBorderBox();
         startBox("&nbsp;Mid-day&nbsp;");
          drawRadioTableRow("Good", "midday_course_" + index, "1", sTemp, (mid_course == 1) ? 1 : 0);
          drawRadioTableRow("Fair", "midday_course_" + index, "2", sTemp, (mid_course == 2) ? 1 : 0);
          drawRadioTableRow("Poor", "midday_course_" + index, "3", sTemp, (mid_course == 3) ? 1 : 0);
         endBox();
        endBorderBox();

        out.println("</td><td>");

        startBorderBox();
         startBox("PM");
          drawRadioTableRow("Good", "pm_course_" + index, "1", sTemp, (pm_course == 1) ? 1 : 0);
          drawRadioTableRow("Fair", "pm_course_" + index, "2", sTemp, (pm_course == 2) ? 1 : 0);
          drawRadioTableRow("Poor", "pm_course_" + index, "3", sTemp, (pm_course == 3) ? 1 : 0);
         endBox();
        endBorderBox();

        out.println("</td></tr></table>");
        
        out.println("</td></tr><tr><td>");

        index++;
        courseName = course[index];      // get course name from array
    }
        
    out.println("</td></tr></table>");

    out.println("</form>");
    
    out.println("</body>");
    out.println("</html>");
    
    out.close();
    
 }
  
private void drawRadioTableRow(String pDisplayText, String pRadioName, String pRadioValue, String pJsCall, int pChecked) {
    //out.println("<tr><td><input type=\"radio\" name=\"" + pRadioName + "\" id=\"radio_" + iRadioRow + "\" value=\"good\" onclick=\"" + pJsCall + "\"></td><td class=textA><label for=radio_" + iRadioRow + ">" + pDisplayText + "</label></td></tr>");
    out.println("<tr><td class=textA>&nbsp;<label for=radio_" + iRadioRow + "><input type=\"radio\" name=\"" + pRadioName + "\" id=\"radio_" + iRadioRow + "\" value=\"" + pRadioValue + "\" onclick=\"" + pJsCall + "\""+ ((pChecked == 1) ? " checked" : "") + ">&nbsp; " + pDisplayText + " &nbsp;</label></td></tr>");
    iRadioRow++;
}
 
private void startBorderBox() {
    out.println("<table cellspacing=0 cellpadding=0 style=\"border: 1px solid black\"><tr><td>");
}

private void endBorderBox() {
    out.println("</td></tr></table>");
}
private void startBox(String pBoxTitle) {
    out.println("<table width=85 border=0 cellpadding=2 cellspacing=0><tr bgcolor=#336633><td colspan=2 align=center class=\"textB\" nowrap>" + pBoxTitle + "</td></tr>");
    //out.println("");
}

private void endBox() {
    out.println("</table>");
}

private void displayDatabaseErrMsg(String pMessage) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Exception: " + pMessage);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
}

private String enforceOptionDefault(HttpServletRequest req, String pElemName) {
    return (req.getParameter(pElemName) == null) ? "0" : req.getParameter(pElemName);
}

}
