/***************************************************************************************
 *   Proshop_diary: This servlet will implement the diary functionality
 *
 *
 *   Called by:     Proshop_sheet (doPost)
 *                  Proshop_oldsheet (doPost)
 *
 *   Parameters:    year, month, day - invalid/missing date parts and it defaults to today
 *                  todo - action to perform, will be either add or update defaults to ""
 *
 *                  note: form for adding/editing diary entry is posted here as well 
 *
 *
 *   Created:       2/15/2005 by Paul
 *
 *
 *   Last Updated:  
 *
 *          6/13/13   Fixed issue where single course facilities were unable to update diary entries.
 *          6/04/10   Fix for how coursename was being handled for single-course clubs when adding and updating diary entries
 *          5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *          4/11/05   Change executeQuery to executeUpdate when updating the diary table.                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
//import java.util.zip.*;
import java.sql.*;
//import java.lang.Math;
import java.text.DateFormat;

// foretees imports      (are these needed to this page?)
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.Utilities;


public class Proshop_diary extends HttpServlet {

    static int iRadioRow = 0;
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
 
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
    
    PrintWriter out;
    //Locale.setDefault( new Locale("en", "US"));
    
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
        //SystemUtils.displayDatabaseErrMsg("Can not connect.", "");
        displayDatabaseErrMsg("Can not connect.", "", out);
        return;
    }
    
    parmClub parm = new parmClub(0, con); // golf only feature

    try {
     getClub.getParms(con, parm);        // get the club parms
    }
    catch (Exception e) {
        out.println("can't allocate parm block. " + e.getMessage());
    }
    //out.println("parm.precheckin=" + parm.precheckin);
    
    
    
    // 
    //  Declare our local variables
    //
    int year = 0;
    int month = 0;
    int day = 0;
    int last_year = 0;
    int last_month = 0;
    int last_day = 0;
    int next_year = 0;
    int next_month = 0;
    int next_day = 0;
    int index = 0;    
    int multi = 0;                                                  // multiple course support flag
    int courseCount = 1;       // default = 1 course
    int weather_am = 0;
    int weather_mid = 0;
    int weather_pm = 0;
    int am_course = 0;
    int mid_course = 0;
    int pm_course = 0;
    int diary_id = 0;  // if zero then we are adding, if > 0 then we've loaded and are updating
    int loadcc = 0;
    int i = 0;
    int scroll = 0;
    
    String notes = "";
    String msg = "";
    String todo = "";
    //String tmp_cc = "";
    
    Statement stmt = null;
    PreparedStatement pstmtc = null;
    ResultSet rs = null;
   
    //
    //  Get name of club for this user
    //
    //String club = (String)session.getAttribute("club");             // get club name
    //String user = (String)session.getAttribute("user");             // get user
        
    //
    //  Array to hold the course names
    //
    String courseName = "";
    ArrayList<String> course = new ArrayList<String>();

    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");
        if (rs.next()) {
            multi = rs.getInt(1);
        } else {
            //SystemUtils.displayDatabaseErrMsg("Club setup not complete.", "");
            displayDatabaseErrMsg("Club setup not complete.", "", out);
            return;
        }
        
        stmt.close();

        if (multi != 0) {                                           // if multiple courses supported for this club

            course = Utilities.getCourseNames(con);     // get all the course names
            
            courseCount = course.size();                // remember the number of courses
            
            //
            //  Add an 'ALL' option at the end of the list
            //
            course.add ("-ALL-");            
        }
    }
    catch (Exception e) {
        //SystemUtils.displayDatabaseErrMsg("Error loading course information.", e.getMessage());
        displayDatabaseErrMsg("Error loading course information.", e.getMessage(), out);
        return;
    }
    
    // scrub our vars
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
    
    if (req.getParameter("scroll") != null) { 
        try {
            scroll = Integer.parseInt(req.getParameter("scroll"));
        } catch (Exception e) {
            scroll = 0;
        }
    }
    
    // verify date integrity
    GregorianCalendar cal = new GregorianCalendar(year, month - 1, day);
    
    String short_year = Integer.toString(year).substring(2, 4);
    String diary_compare_date = month + "/" + day + "/" + short_year;           // date in a format to compare against
    
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.FULL);
    DateFormat df_short = DateFormat.getDateInstance(DateFormat.SHORT);
    
    //out.println("<br>df_short=" + df_short.format(cal.getTime()));
    //out.println("diary_compare_date=" + diary_compare_date + "<br>");
    
    if (!diary_compare_date.equals(df_short.format(cal.getTime()))) {
        // they don't match - this shouldn't happend under normal circumstances
        msg = "Invalid or missing date encountered!  Defaulting to today.";
        cal = new GregorianCalendar();                                          // todays date
        month = cal.get(cal.MONTH) + 1;
        day = cal.get(cal.DAY_OF_MONTH);
        year = cal.get(cal.YEAR);
    }
    
    String full_display_date = df_full.format(cal.getTime());
    String diary_date = year + "-" + month + "-" + day;                         // date in mysql format
    
    //out.println("debug: diary_date=" + diary_date + "<br>");
    //out.println("debug: scroll=" + scroll + "<br>");
    
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;
    boolean no_last = false;
    boolean no_next = false;
    
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
    
    // if scroll is here (meaning this was called from oldsheets) then populate last_ & next_ date parts
    if (scroll == 1) {
        cal.add(cal.DAY_OF_MONTH, 1);
        next_month = cal.get(cal.MONTH) + 1;
        next_day = cal.get(cal.DAY_OF_MONTH);
        next_year = cal.get(cal.YEAR);
        cal.add(cal.DAY_OF_MONTH, -2);
        last_month = cal.get(cal.MONTH) + 1;
        last_day = cal.get(cal.DAY_OF_MONTH);
        last_year = cal.get(cal.YEAR);
        
        GregorianCalendar oldest_cal = new GregorianCalendar(oldest_yy, oldest_mm - 1, oldest_dd);
        GregorianCalendar last_cal = new GregorianCalendar(last_year, last_month - 1, last_day);
        GregorianCalendar next_cal = new GregorianCalendar(next_year, next_month - 1, next_day);
        GregorianCalendar today_cal = new GregorianCalendar();
        
        no_last = (last_cal.before(oldest_cal));
        no_next = (next_cal.after(today_cal));
        
    }
    
    //
    // see if we are here to do a particular action
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
            pstmtc.executeUpdate();
              
            pstmtc.close();
            
            for (i=0; i < courseCount; i++) {
                
                pstmtc = con.prepareStatement ("INSERT INTO diarycc VALUES (NULL, ?, ?, ?, ?, ?)");
                pstmtc.clearParameters();
                pstmtc.setString(1, diary_date);
                if (multi != 0) {
                    pstmtc.setString(2, course.get(i));
                } else {
                    pstmtc.setNull(2, Types.VARCHAR);
                }
                pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "am_course_" + i)));
                pstmtc.setInt(4, Integer.parseInt(enforceOptionDefault(req, "midday_course_" + i)));
                pstmtc.setInt(5, Integer.parseInt(enforceOptionDefault(req, "pm_course_" + i)));
                pstmtc.executeUpdate();
            
                pstmtc.close();
            }
            
            msg = "Diary entry saved successfully.";
        }
        catch (Exception e) {
            //SystemUtils.displayDatabaseErrMsg("Error inserting diary entry.", e.getMessage());
            displayDatabaseErrMsg("Error inserting diary entry.", e.getMessage(), out);
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
            pstmtc.executeUpdate();

            pstmtc.close();
            
            if (multi != 0) {
                for (i=0; i < courseCount; i++) {
                    pstmtc = con.prepareStatement ("UPDATE diarycc SET am_condition = ?, mid_condition = ?, pm_condition = ? WHERE diary_date = ? AND course_name = ?");
                    pstmtc.clearParameters();
                    pstmtc.setInt(1, Integer.parseInt(enforceOptionDefault(req, "am_course_" + i)));
                    pstmtc.setInt(2, Integer.parseInt(enforceOptionDefault(req, "midday_course_" + i)));
                    pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "pm_course_" + i)));
                    pstmtc.setString(4, diary_date);
                    pstmtc.setString(5, course.get(i));
                    pstmtc.executeUpdate();

                    pstmtc.close();
                }
            } else {
                pstmtc = con.prepareStatement ("UPDATE diarycc SET am_condition = ?, mid_condition = ?, pm_condition = ? WHERE diary_date = ?");
                pstmtc.clearParameters();
                pstmtc.setInt(1, Integer.parseInt(enforceOptionDefault(req, "am_course_" + i)));
                pstmtc.setInt(2, Integer.parseInt(enforceOptionDefault(req, "midday_course_" + i)));
                pstmtc.setInt(3, Integer.parseInt(enforceOptionDefault(req, "pm_course_" + i)));
                pstmtc.setString(4, diary_date);
                pstmtc.executeUpdate();

                pstmtc.close();
            }
            
            msg = "Diary entry updated successfully.";       
        }
        catch (Exception e) {
            //SystemUtils.displayDatabaseErrMsg("Error updating diary entry.", e.getMessage());
            displayDatabaseErrMsg("Error updating diary entry.", e.getMessage(), out);
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
        //SystemUtils.displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage());
        displayDatabaseErrMsg("Error loading diary for " + diary_date, e.getMessage(), out);
        return;
    }
    
    
    //
    //
    // end of preprocessing / start page output
    
    out.println("<html>");
    out.println("<head>");
    out.println("<title>Diary Entry</title>");
    
    out.println("<style>");
    out.println(".textA {font-family: verdana; font-size: 12px; font-weight: normal; color: black}");
    out.println(".textB {font-family: verdana; font-size: 14px; font-weight: bold; color: white}");
    out.println(".scrollLink {font-family: verdana; font-size: 12px; font-weight: bold; color: darkBlue; text-decoration: underline}");
    out.println("</style>");
    
    out.println("<script type=\"text/javascript\">");
    out.println("var iCourseCount = " + courseCount + ";");
    
    out.println("function cancelEntry() {");
    out.println(" if (confirm('Are you sure you want to close this window.') == true ) window.close();");
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
    out.println("<form action=\"Proshop_diary\" method=\"post\" name=\"frmDiaryEntry\" id=\"frmDiaryEntry\">");
    out.println("<input type=hidden name=todo value='" + ((diary_id == 0) ? "add" : "update") + "'>");
    out.println("<input type=hidden name=diary_id value='" + diary_id + "'>");
    out.println("<input type=hidden name=month value='" + month + "'>");
    out.println("<input type=hidden name=day value='" + day + "'>");
    out.println("<input type=hidden name=year value='" + year + "'>");
    out.println("<input type=hidden name=scroll value='" + scroll + "'>");
    //out.println("<input type=hidden name=course_count value='" + courseCount + "'>");
    
    //out.println("<center><font class=textB style=\"color: black\">Journal Entry for " + full_display_date + "</font>" +
    //            ((!msg.equals("")) ? "<br><br><font class=textA style=\"color: red\"><b>" + msg + "</b></font>" : "") +
    //            "</center>");
    
    out.println("<table align=center><tr>");
    out.println("<tr><td colspan=2 align=center nowrap><font class=textB style=\"color: black\">Journal Entry for " + full_display_date + "</font></td></tr>");
    out.println((!msg.equals("")) ? "<tr><td colspan=2 align=center><br><font class=textA style=\"color: red\">" + msg + "</font></td></tr>" : "");
    
    if (scroll == 1) {
        out.println("<tr><td align=left>");
        out.println((no_last == false) ? "<a href=\"Proshop_diary?scroll=1&course=" +courseName+ "&year=" +last_year+ "&month=" +last_month+ "&day=" +last_day+ "\" class=scrollLink>Prev Day</a>" : "");
        out.println ("</td>");
        out.println((no_next == false) ? "<td align=right><a href=\"Proshop_diary?scroll=1&course=" +courseName+ "&year=" +next_year+ "&month=" +next_month+ "&day=" +next_day+ "\" class=scrollLink>Next Day</a>" : "");
        out.println ("</td>");
        out.println("</tr>");
    }                
                
    out.println("</table><br>");
    
    
    //
    //  start displaying conditions
    //
    
    out.println("<table cellspacing=0 cellpadding=5><tr valign=top><td>");
    
    out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");
    
    out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">Weather Conditions</td></tr><tr><td>");
    
    startBorderBox(out);
     startBox("AM", out);
      drawRadioTableRow("Good", "am_weather", "1", "", ((weather_am == 1) ? 1 : 0), out);
      drawRadioTableRow("Fair", "am_weather", "2", "", ((weather_am == 2) ? 1 : 0), out);
      drawRadioTableRow("Poor", "am_weather", "3", "", ((weather_am == 3) ? 1 : 0), out);
     endBox(out);
    endBorderBox(out);
    
    out.println("</td><td>");
    
    startBorderBox(out);
     startBox("&nbsp;Mid-day&nbsp;", out);
      drawRadioTableRow("Good", "midday_weather", "1", "", ((weather_mid == 1) ? 1 : 0), out);
      drawRadioTableRow("Fair", "midday_weather", "2", "", ((weather_mid == 2) ? 1 : 0), out);
      drawRadioTableRow("Poor", "midday_weather", "3", "", ((weather_mid == 3) ? 1 : 0), out);
     endBox(out);
    endBorderBox(out);
    
    out.println("</td><td>");
    
    startBorderBox(out);
     startBox("PM", out);
      drawRadioTableRow("Good", "pm_weather", "1", "", ((weather_pm == 1) ? 1 : 0), out);
      drawRadioTableRow("Fair", "pm_weather", "2", "", ((weather_pm == 2) ? 1 : 0), out);
      drawRadioTableRow("Poor", "pm_weather", "3", "", ((weather_pm == 3) ? 1 : 0), out);
     endBox(out);
    endBorderBox(out);
    
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
    String sTemp = "";
    courseName = "";
      
    if (multi != 0) {                  // if multiple courses supported for this club
       
       for (index=0; index < course.size(); index++) {

          courseName = course.get(index);

          if (!courseName.equals( "-ALL-" )) {

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
                      //SystemUtils.displayDatabaseErrMsg("Error loading course conditions for diary.", e.getMessage());
                      displayDatabaseErrMsg("Error loading course conditions for diary.", e.getMessage(), out);
                      return;
                  }
              }

              out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");

              out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">" + courseName + "<br>Course Conditions</td></tr><tr><td>");

              sTemp = (index == 0) ? "setCourseCond(this.name)" : "";
              startBorderBox(out);
               startBox("AM", out);
                drawRadioTableRow("Good", "am_course_" + index, "1", sTemp, ((am_course == 1) ? 1 : 0), out);
                drawRadioTableRow("Fair", "am_course_" + index, "2", sTemp, ((am_course == 2) ? 1 : 0), out);
                drawRadioTableRow("Poor", "am_course_" + index, "3", sTemp, ((am_course == 3) ? 1 : 0), out);
               endBox(out);
              endBorderBox(out);

              out.println("</td><td>");

              startBorderBox(out);
               startBox("&nbsp;Mid-day&nbsp;", out);
                drawRadioTableRow("Good", "midday_course_" + index, "1", sTemp, (mid_course == 1) ? 1 : 0, out);
                drawRadioTableRow("Fair", "midday_course_" + index, "2", sTemp, (mid_course == 2) ? 1 : 0, out);
                drawRadioTableRow("Poor", "midday_course_" + index, "3", sTemp, (mid_course == 3) ? 1 : 0, out);
               endBox(out);
              endBorderBox(out);

              out.println("</td><td>");

              startBorderBox(out);
               startBox("PM", out);
                drawRadioTableRow("Good", "pm_course_" + index, "1", sTemp, (pm_course == 1) ? 1 : 0, out);
                drawRadioTableRow("Fair", "pm_course_" + index, "2", sTemp, (pm_course == 2) ? 1 : 0, out);
                drawRadioTableRow("Poor", "pm_course_" + index, "3", sTemp, (pm_course == 3) ? 1 : 0, out);
               endBox(out);
              endBorderBox(out);

              out.println("</td></tr></table>");

              out.println("</td></tr><tr><td>");
          }
       }
       
       out.println("</td></tr>");
        
    } else {   // single course

        if (loadcc == 1) {

            try {
                pstmtc = con.prepareStatement("SELECT * FROM diarycc WHERE diary_date = ?");
                pstmtc.clearParameters();
                pstmtc.setString(1, diary_date);
                rs = pstmtc.executeQuery();

                while (rs.next()) {
                    am_course = rs.getInt("am_condition");
                    mid_course = rs.getInt("mid_condition");
                    pm_course = rs.getInt("pm_condition");
                }
                pstmtc.close();
            }
            catch (Exception e) {
                //SystemUtils.displayDatabaseErrMsg("Error loading course conditions for diary.", e.getMessage());
                displayDatabaseErrMsg("Error loading course conditions for diary.", e.getMessage(), out);
                return;
            }
        }


        out.println("<table bgcolor=#F5F5DC cellspacing=0 cellpadding=4 style=\"border: 2px solid black\">");

        out.println("<tr><td align=center colspan=5 class=textB style=\"color: black\">Course Conditions</td></tr><tr><td>");

        sTemp = (index == 0) ? "setCourseCond(this.name)" : "";
        startBorderBox(out);
         startBox("AM", out);
          drawRadioTableRow("Good", "am_course_" + index, "1", sTemp, ((am_course == 1) ? 1 : 0), out);
          drawRadioTableRow("Fair", "am_course_" + index, "2", sTemp, ((am_course == 2) ? 1 : 0), out);
          drawRadioTableRow("Poor", "am_course_" + index, "3", sTemp, ((am_course == 3) ? 1 : 0), out);
         endBox(out);
        endBorderBox(out);

        out.println("</td><td>");

        startBorderBox(out);
         startBox("&nbsp;Mid-day&nbsp;", out);
          drawRadioTableRow("Good", "midday_course_" + index, "1", sTemp, (mid_course == 1) ? 1 : 0, out);
          drawRadioTableRow("Fair", "midday_course_" + index, "2", sTemp, (mid_course == 2) ? 1 : 0, out);
          drawRadioTableRow("Poor", "midday_course_" + index, "3", sTemp, (mid_course == 3) ? 1 : 0, out);
         endBox(out);
        endBorderBox(out);

        out.println("</td><td>");

        startBorderBox(out);
         startBox("PM", out);
          drawRadioTableRow("Good", "pm_course_" + index, "1", sTemp, (pm_course == 1) ? 1 : 0, out);
          drawRadioTableRow("Fair", "pm_course_" + index, "2", sTemp, (pm_course == 2) ? 1 : 0, out);
          drawRadioTableRow("Poor", "pm_course_" + index, "3", sTemp, (pm_course == 3) ? 1 : 0, out);
         endBox(out);
        endBorderBox(out);

        out.println("</td></tr></table>");

        out.println("</td></tr>");
    }

    out.println("</table>");

    out.println("</form>");
    
    out.println("</body>");
    out.println("</html>");
    
    out.close();
    
 }
  
private void drawRadioTableRow(String pDisplayText, String pRadioName, String pRadioValue, String pJsCall, int pChecked, PrintWriter out) {
    //out.println("<tr><td><input type=\"radio\" name=\"" + pRadioName + "\" id=\"radio_" + iRadioRow + "\" value=\"good\" onclick=\"" + pJsCall + "\"></td><td class=textA><label for=radio_" + iRadioRow + ">" + pDisplayText + "</label></td></tr>");
    out.println("<tr><td class=textA>&nbsp;<label for=radio_" + iRadioRow + "><input type=\"radio\" name=\"" + pRadioName + "\" id=\"radio_" + iRadioRow + "\" value=\"" + pRadioValue + "\" onclick=\"" + pJsCall + "\""+ ((pChecked == 1) ? " checked" : "") + ">&nbsp; " + pDisplayText + " &nbsp;</label></td></tr>");
    iRadioRow++;
}
 
private void startBorderBox(PrintWriter out) {
    out.println("<table cellspacing=0 cellpadding=0 style=\"border: 1px solid black\"><tr><td>");
}

private void endBorderBox(PrintWriter out) {
    out.println("</td></tr></table>");
}
private void startBox(String pBoxTitle, PrintWriter out) {
    out.println("<table width=85 border=0 cellpadding=2 cellspacing=0><tr bgcolor=#336633><td colspan=2 align=center class=\"textB\" nowrap>" + pBoxTitle + "</td></tr>");
    //out.println("");
}

private void endBox(PrintWriter out) {
    out.println("</table>");
}

private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
}

private String enforceOptionDefault(HttpServletRequest req, String pElemName) {
    return (req.getParameter(pElemName) == null) ? "0" : req.getParameter(pElemName);
}

}
