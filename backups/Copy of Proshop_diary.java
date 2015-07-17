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
    
    iRadioRow = 0;
    
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               // prevents caching at the proxy server
    
    //PrintWriter out = resp.getWriter();                                         // normal output stream
    out = resp.getWriter();
    
    HttpSession session = SystemUtils.verifyPro(req, out);          // check for intruder
    if (session == null) { return; }

    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
    
        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><BR>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
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
    //String 
    
    Statement stmt = null;
    ResultSet rs = null;
   
    //
    //  Get name of club for this user
    //
    String club = (String)session.getAttribute("club");             // get club name
    String user = (String)session.getAttribute("user");             // get user
    
    //
    //   Get today's date and then use the value passed to locate the requested date
    //
    Calendar cal = new GregorianCalendar();                         // get todays date

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    
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
            // Parms do not exist yet
            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H2>Database Access Error</H2>");
            out.println("<BR><BR>The Club Setup has not been completed.");
            out.println("<BR>Please go to 'System Config' and select 'Club Setup'.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
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
            
            courseCount = index;                                     // remember the number of courses
            
            stmt.close();

            //
            //  Add an 'ALL' option at the end of the list
            //
            if (index < 20) { course[index] = "-ALL-"; }
            
        }
    }
    catch (Exception exc) {
        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H1>Database Access Error</H1>");
        out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
        out.println("<BR>Please try again later.");
        out.println("<BR><br>Exception: " + exc.getMessage());
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
    }
    
    //
    // see if we're are here to add an entry
    //
    String todo = "";
    if (req.getParameter("todo") != null) {
     todo = req.getParameter("todo");   
    }
    
    if (todo == "add") {
        


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
    out.println("function clearWeatherAll() {");
    out.println("var f = document.forms['frmDiaryEntry'];");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  f.allday_weather[i].checked = false;");
    out.println(" }");
    out.println("}");
    
    out.println("function clearWeatherTime() {");
    out.println("var f = document.forms['frmDiaryEntry'];");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  f.am_weather[i].checked = false;");
    out.println("  f.midday_weather[i].checked = false;");
    out.println("  f.pm_weather[i].checked = false;");
    out.println(" }");
    out.println("}");
    
    out.println("function clearCourseAll() {");
    out.println("var f = document.forms['frmDiaryEntry'];");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  f.allday_course[i].checked = false;");
    out.println(" }");
    out.println("}");
    
    out.println("function clearCourseTime() {");
    out.println("var f = document.forms['frmDiaryEntry'];");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  f.am_course[i].checked = false;");
    out.println("  f.midday_course[i].checked = false;");
    out.println("  f.pm_course[i].checked = false;");
    out.println(" }");
    out.println("}");
    
    // may need to add another check to see if there are notes here AND invalid radio selections, 
    // if there are then just set all in that block to unchecked so that they're not sent back here for processing
    out.println("function submitEntry() {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
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
    out.println(" f.todo = 'add';");
    out.println(" f.submit();");
    out.println("}");
    
    out.println("function isOptionChecked(pElem) {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
    out.println(" for (i=0; i<3; i++) {");
    out.println("  if (eval('f.' + pElem + '[' + i + '].checked')) return true;");
    out.println(" }");
    out.println(" return false;");
    out.println("}");
    
    out.println("function setCourseCond() {");
    out.println(" var f = document.forms['frmDiaryEntry'];");
    out.println(" for (cc=0; cc < iCourseCount-1; cc++) {");
    out.println("  e = 'am_course' + cc;"); // 
    out.println("  for (i=0; i<3; i++) {");
    out.println("   if (eval('f.' + pElem + '[' + i + '].checked')) return true;");
    out.println("  }");
    out.println(" }");
    out.println("}");
    
    //out.println("");
    //out.println("");
    out.println("</script>");
            
    out.println("</head>");
    out.println("<body bgcolor=white>");
    
    
    
    // this is the form that gets submitted when the user completes a diary entry
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_diary\" method=\"post\" name=\"frmDiaryEntry\" id=\"frmDiaryEntry\">");
    /*
    out.println("<table width=550 align=center><tr><td>");
    //
    //  If multiple courses, then add a drop-down box for course names
    //
    if (multi != 0) {           // if multiple courses supported for this club

        index = 0;
        courseName = course[index];      // get first course name from array

        out.println("<font class=textB style=\"color: black\">Course:</font>&nbsp;&nbsp;");
        out.println("<select size=\"1\" name=\"course\">");
        out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");

        index++;
        courseName = course[index];      // get course name from array

        while ((!courseName.equals( "" )) && (index < 20)) {
            out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            index++;
            courseName = course[index];      // get course name from array
        }
        out.println("</select>");

    } else {
     out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
    }
    out.println("</td><td align=right class=textB style=\"color: black\">Thrusday, Feburary 17th, 2005 &nbsp;</td></tr></table>");
    */
    
    out.println("<center><font class=textB style=\"color: black\">Journal Entry for Thrusday, Feburary 17th, 2005</font></center>");
    
    out.println("<br><br>");
    
    
    
    //
    //  start displaying conditions
    //
    
    out.println("<table cellspacing=0 cellpadding=5><tr valign=top><td>");
    
    out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");
    
    out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">Weather Conditions</td></tr><tr><td>");
    
    startBorderBox();
     startBox("AM");
      drawRadioTableRow("Good", "am_weather", "good", "");
      drawRadioTableRow("Fair", "am_weather", "fair", "");
      drawRadioTableRow("Poor", "am_weather", "poor", "");
     endBox();
    endBorderBox();
    
    out.println("</td><td>");
    
    startBorderBox();
     startBox("&nbsp;Mid-day&nbsp;");
      drawRadioTableRow("Good", "midday_weather", "good", "");
      drawRadioTableRow("Fair", "midday_weather", "fair", "");
      drawRadioTableRow("Poor", "midday_weather", "poor", "");
     endBox();
    endBorderBox();
    
    out.println("</td><td>");
    
    startBorderBox();
     startBox("PM");
      drawRadioTableRow("Good", "pm_weather", "good", "");
      drawRadioTableRow("Fair", "pm_weather", "fair", "");
      drawRadioTableRow("Poor", "pm_weather", "poor", "");
     endBox();
    endBorderBox();
    
    out.println("</td></tr></table>");
   
    
    out.println("</td><td rowspan=3 class=textB style=\"color: black\">Notes:<br>");
    out.println("<textarea name=notes cols=32 rows=14 class=textA style=\"width: 275px; height: 250px\"></textarea>");
    out.println("<br><br><input type=button name=btnSubmitEntry value=\"Submit Entry\" onclick=\"submitEntry()\" style=\"width: 150px\">");
    out.println("</td></tr><tr><td>");
    
    
    index = 0;
    courseName = course[index];      // get course name from array
    String sTemp = "";

    while ((!courseName.equals( "" )) && (index < 20) && (!courseName.equals( "-ALL-" ))) {

        out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");

        out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">" + courseName + "<br>Course Conditions</td></tr><tr><td>");

        sTemp = (index == 0) ? "setCourseCond()" : "";
        startBorderBox();
         startBox("AM");
          drawRadioTableRow("Good", "am_course" + index, "good", sTemp);
          drawRadioTableRow("Fair", "am_course" + index, "fair", sTemp);
          drawRadioTableRow("Poor", "am_course" + index, "poor", sTemp);
         endBox();
        endBorderBox();

        out.println("</td><td>");

        startBorderBox();
         startBox("&nbsp;Mid-day&nbsp;");
          drawRadioTableRow("Good", "midday_course" + index, "good", "");
          drawRadioTableRow("Fair", "midday_course" + index, "fair", "");
          drawRadioTableRow("Poor", "midday_course" + index, "poor", "");
         endBox();
        endBorderBox();

        out.println("</td><td>");

        startBorderBox();
         startBox("PM");
          drawRadioTableRow("Good", "pm_course" + index, "good", "");
          drawRadioTableRow("Fair", "pm_course" + index, "fair", "");
          drawRadioTableRow("Poor", "pm_course" + index, "poor", "");
         endBox();
        endBorderBox();

        out.println("</td></tr></table>");
        
        out.println("</td></tr><tr><td>");

        index++;
        courseName = course[index];      // get course name from array
    }
        
    out.println("</td></tr><tr><td><br><br><input type=button name=btnSubmitEntry value=\"Submit Entry\" onclick=\"submitEntry()\" style=\"width: 150px\">");

    out.println("</td></tr></table>");

    out.println("</form>");
    
    out.println("</body>");
    out.println("</html>");
    
    out.close();
    
 }
  
private void drawRadioTableRow(String pDisplayText, String pRadioName, String pRadioValue, String pJsCall) {
    //out.println("<tr><td><input type=\"radio\" name=\"" + pRadioName + "\" id=\"radio_" + iRadioRow + "\" value=\"good\" onclick=\"" + pJsCall + "\"></td><td class=textA><label for=radio_" + iRadioRow + ">" + pDisplayText + "</label></td></tr>");
    out.println("<tr><td class=textA>&nbsp;<label for=radio_" + iRadioRow + "><input type=\"radio\" name=\"" + pRadioName + "\" id=\"radio_" + iRadioRow + "\" value=\"good\" onclick=\"" + pJsCall + "\">&nbsp; " + pDisplayText + " &nbsp;</label></td></tr>");
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

}