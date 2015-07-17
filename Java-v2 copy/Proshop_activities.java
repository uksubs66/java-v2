/***************************************************************************************
 *   Proshop_activities: This servlet will ...
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       12/05/2008 by Paul
 *
 *
 *   Last Updated:  
 *
 *   10/30/09   Give full activity access to ForeTees proshop users
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
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.parmPOS;
import com.foretees.common.getClub;
import com.foretees.client.action.ActionHelper;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_activities extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    doPost(req, resp);                                              // call doPost processing

 } // end of doGet routine
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    String club = (String)session.getAttribute("club");   // get club name
    String user = (String)session.getAttribute("user");
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);

    int foretees_mode = 0;
    
    //
    //  Call is to display the announcement page.
    //
    //  Display a page to provide a link to the club's announcement page
    //
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees Proshop Announcement Page\"</title>");
  //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\"></link>");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("<style>");
    out.println(".linkA {color:#336633; font-size:14pt; font-weight:bold}");
    out.println("</style>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    SystemUtils.getProshopSubMenu(req, out, lottery);

    Connection con = SystemUtils.getCon(session);
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
/*    
    Connection con = null;
    try {
        con = dbConn.Connect("demopaul");
    } catch (Exception ignore) {}
*/
    out.println("<p align=center><b><font size=6 color=#336633>Available Activities</font></b></p>");

    out.println("<table align=center>");

    try {

        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT foretees_mode FROM club5 WHERE clubName <> '';");
        
        if (rs.next()) {
            foretees_mode = rs.getInt(1);
        }
        
        // if they have foretees and this pro has access, then give a link in to the golf system
        if (foretees_mode != 0) {

           if (Utilities.isFTProshopUser(user)) {   // if proshop4tea
              
              out.println("<tr><td><a href=\"Proshop_jump?switch&activity_id=0\" class=linkA target=_top>ForeTees Golf</a></td></tr>");
              
           } else {
              
              //   see if this user has access to Golf
              PreparedStatement pstmt = con.prepareStatement("" +
                        "SELECT default_entry FROM login2 WHERE username = ? AND activity_id = 0");
              pstmt.clearParameters();
              pstmt.setString(1, user);
              rs2 = pstmt.executeQuery();

              if (rs2.next()) {               // if ok

                 out.println("<tr><td><a href=\"Proshop_jump?switch&activity_id=0\" class=linkA target=_top>ForeTees Golf</a></td></tr>");
              }
              pstmt.close();
           }
        }
        
        // build a link to any activities they have access to
        if (Utilities.isFTProshopUser(user)) {
            rs = stmt.executeQuery("SELECT * FROM activities " +
                                   "WHERE parent_id = 0 " +
                                   "ORDER BY activity_name");
        } else {
            rs = stmt.executeQuery("SELECT * FROM activities " +
                                   "WHERE parent_id = 0 AND " +
                                   "activity_id IN (SELECT activity_id FROM login2 WHERE username = '" + user + "')" +
                                   "ORDER BY activity_name");
        }

        while (rs.next()) {

            out.println("<tr><td><a href=\"Proshop_jump?switch&activity_id=" + rs.getInt("activity_id") + "\" class=linkA target=_top>" + rs.getString("activity_name") + "</a></td></tr>");
            //out.println("<tr><td><a href=\"Proshop_gensheets?parent_id=" + rs.getInt("activity_id") + "\" class=linkA>" + rs.getString("activity_name") + "</a></td></tr>");

        }
        
        stmt.close();

    } catch (Exception exc) {

        out.println("<p>ERROR:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("</table>");

    
    
    int activity_id = (Integer)session.getAttribute("activity_id");
    
/*    
    
    // TESTING CODE
    out.println("<a href=\"Proshop_activity_config\">conf</a>");


    out.println("<form>");
    out.println("<input type=text size=3 name=actId>");
    out.println("<input type=submit name=todo value=Children>");
    out.println("<input type=submit name=todo value=Parents>");
    out.println("</form>");
    
    ArrayList<Integer> result = new ArrayList<Integer>();
    int actId = 0;
    String sid = req.getParameter("actId");
    String todo = req.getParameter("todo");
    if (todo == null) todo = "";
    
    try {
        actId = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    if (actId > 0) {
        if (todo.equals("Children")) {
            out.println("Getting Children for " + actId);
            try { result = getActivity.getAllChildrenForActivity(actId, con); }
            catch (Exception exc) { out.println("ERR=" + exc.toString()); }
        } else if (todo.equals("Parents")) {
            out.println("Getting Parents for " + actId);
            try { result = getActivity.getAllParentsForActivity(actId, con); }
            catch (Exception exc) { out.println("ERR=" + exc.toString()); }
        } else {
            out.println("<br>Nothing todo.");
        }
    }
    
    for (int i = 0; i < result.size(); i++) {

        out.println("<br>" + result.get(i));
    }

    out.println("<p>Found " + result.size() + " activities.</p>");
    
*/    
    
    
    

    //String todo = req.getParameter("todo");
    //if (todo == null) todo = "";
    //if (todo.equals("go")) rebuildTimeSheets(1, con);
    
/*
    int interval = 15; // minutes between times
    int alt_interval = 10;
    int first_time = 700;
    int last_time = 1900;
    int hr = 0;
    int min = 0;
    //int this_interval = 0;
    
    String mysql_datetime = "";
    
    boolean alt = false;
    
    Calendar cal = null;

    int this_time = first_time;
    
    hr = this_time / 100;
    min = this_time - (hr * 100);

    while (this_time < last_time) {
        
        out.println("<br>" + Utilities.get_mysql_timestamp(20090930, this_time));
        
        cal = new GregorianCalendar();
        //cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
        
        cal.add(Calendar.MINUTE, (!alt) ? interval : alt_interval);
        
        hr = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        
        this_time = hr * 100 + min;

        alt = alt == false;
        
    }
*/

/*
    int minutesbtwn = 90;

    int mins = minutesbtwn % 60;
    int hrs = (minutesbtwn - mins) / 60;

    int startTime = 0;
    int endTime = 0;

    startTime = 1100 - (hrs * 100) - mins;
    endTime = 1100 + (hrs * 100) + mins;


    out.println("<br>hrs=" + hrs);
    out.println("<br>mins=" + mins);

    out.println("<br>startTime=" + startTime);  //  930
    out.println("<br>endTime=" + endTime);      //  1230
*/
/*
    Enumeration headerNames = req.getHeaderNames();
    while(headerNames.hasMoreElements()) {
      String headerName = (String)headerNames.nextElement();
      out.print("<BR>" + headerName);
      out.println(" = " + req.getHeader(headerName));
    }
*/



    out.println("</body></html>");

    /*

    SELECT activity_id, date_time
    FROM activity_sheets t1
    LEFT OUTER JOIN activity_sheets_players t2 ON t1.sheet_id = t2.activity_sheet_id
    WHERE
        activity_id IN (17,9,1) AND
        t2.username = "6700" AND
        DATE_FORMAT(date_time, '%Y%m%d') = DATE_FORMAT(20090828, '%Y%m%d') AND
        DATE_FORMAT(date_time, '%k%i') >= DATE_FORMAT(DATE_ADD(date_time, INTERVAL 90 MINUTE), '%k%i') AND
        DATE_FORMAT(date_time, '%k%i') >= DATE_FORMAT(DATE_SUB(date_time, INTERVAL 90 MINUTE), '%k%i')

     */

 } // end of doPost routine
 
 
 private static void buildTimeSheets(int activity_id, Connection con) {
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
     
    int date = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int skip = 0;
    int i = 0;
    
    //int day_num = 0;
    //int sheet_id = 0;
    //int this_week = 0;
    //int eo_week = 0;
    //boolean doCustom = false;
    
    String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    String in = getActivity.buildInString(activity_id, 1, con); // get all the children of this activity id

    String club = SystemUtils.getClubName(con);
    
    ArrayList<Integer> array = new ArrayList<Integer>();
    
    Calendar cal = null;
    
    try { array = getActivity.getAllActivitiesWithSheets(in, con); } 
    catch (Exception exc) {}

    for (int x = 0; x < array.size(); x++) {

        cal = new GregorianCalendar();          // get todays date
        cal.add(Calendar.DATE, 365);            // roll ahead one year

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        //day_num = cal.get(Calendar.DAY_OF_WEEK);

        date = (year * 10000) + (month * 100) + day;

        //
        //  check all 365 days
        //
        i = 0;
        loopb1:
        while (i < 366) {        // go 1 extra for today

            skip = 0;
            //sheet_id = 0;
            //this_week = 0;
            //eo_week = 0;
            //doCustom = false;

            //
            //  See if tee sheet already exists for this activity and day
            //
            try {
              
                pstmt = con.prepareStatement (
                        "SELECT sheet_id FROM activity_sheets WHERE activity_id = ? AND DATE_FORMAT(date_time, '%Y%m%d') = ?");

                pstmt.clearParameters();
                pstmt.setInt(1, array.get(x));       // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                if ( rs.next() ) {                   // if times exist

                    skip = 1;                        // found one - we're done checking
                }
  
            } catch (Exception exc) {
                
                Utilities.logError("buildTimeSheets: Error=" + exc.getMessage());
    
            } finally {

                i++; // bump the counter here so errors will not cause infinte loops
                
                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }
            
            if (skip == 0) {

                // we didn't find any - let's build this day's time sheet

                buildTimeSheet(array.get(x), date, club, con);

            } else {

                // if running on dev server then don't break - scan full year
                if (Common_Server.SERVER_ID != 4) break loopb1;

            }

            cal.add(Calendar.DATE, -1);                 // roll back one day
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH) +1;
            year = cal.get(Calendar.YEAR);
            //day_num = cal.get(Calendar.DAY_OF_WEEK);

            date = (year * 10000) + (month * 100) + day;
            
        } // end year while loop
           
    } // end for loop of avtivities are are building time sheets for
    
 }
 
 
 // 
 // This method will build one time sheet for the specified activity
 // Make this method accept the local ints as parms (interval, start time, etc.)
 //
 private static void buildTimeSheet(int activity_id, int date, String club, Connection con) {

    int interval = 90;      // minutes between times
    int alt_interval = 90;
    int first_time = 830;
    int last_time = 1900; // 1730;
    int this_time = first_time;
    int hr = this_time / 100;
    int min = this_time - (hr * 100);
    
    boolean alt = false;
    //boolean is_wednesday = false;
    
    Calendar cal = null;
    
    //PreparedStatement pstmt = null;
    //ResultSet rs = null;

/*

    // START CUSTOM FOR ADMIRALSCOVE

    if (activity_id > 4 && activity_id < 9) {

        try {

            pstmt = con.prepareStatement (
                    "SELECT DATE_FORMAT(?, '%W') AS dayName");

            pstmt.clearParameters();
            pstmt.setInt(1, date);
            rs = pstmt.executeQuery();

            is_wednesday = ( rs.next() && rs.getString(1).equals("Wednesday") );

        } catch (Exception exc) {

            Utilities.logError("buildTimeSheets: is_wednesday error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        last_time = (is_wednesday) ? 1900 : 1730;

    } else {

        last_time = 1730;

    }

    // END CUSTOM FOR ADMIRALSCOVE
*/

    while (this_time <= last_time) {
        
        buildTimeSlot(date, this_time, activity_id, con);
        
        cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
        
        cal.add(Calendar.MINUTE, (!alt) ? interval : alt_interval);
        
        hr = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        
        this_time = hr * 100 + min;

        alt = alt == false;
        
    }
     
 }


 private static void buildTimeSlot(int date, int time, int activity_id, Connection con) {

    PreparedStatement pstmt = null;

    String date_time = Utilities.get_mysql_timestamp(date, time);

    try {

        pstmt = con.prepareStatement( "" +
                "INSERT INTO activity_sheets " +
                    "(activity_id, date_time)" +
                "VALUES " +
                    "(?, ?)" );

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        pstmt.setString(2, date_time);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        Utilities.logError("buildTimeSlot: date_time=" + date_time + ", Error=" + exc.getMessage());

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }



 private static void rebuildTimeSheets(int activity_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int date = 0;
    int year = 0;
    int month = 0;
    int day = 0;
    int found = 0;
    int i = 0;

    String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    String in = getActivity.buildInString(activity_id, 1, con); // get all the children of this activity id

    String club = SystemUtils.getClubName(con);

    ArrayList<Integer> array = new ArrayList<Integer>();

    Calendar cal = null;

    try { array = getActivity.getAllActivitiesWithSheets(in, con); }
    catch (Exception exc) {}

    for (int x = 0; x < array.size(); x++) {

        cal = new GregorianCalendar();          // get todays date
        cal.add(Calendar.DATE, 365);            // roll ahead one year

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);

        date = (year * 10000) + (month * 100) + day;

        //
        //  check all 365 days
        //
        i = 0;
        loopb1:
        while (i < 366) {        // go 1 extra for today

            found = 0;

            //
            //  See if tee sheet already exists for this activity and day
            //
            try {

                pstmt = con.prepareStatement (
                        "SELECT sheet_id FROM activity_sheets WHERE activity_id = ? AND DATE_FORMAT(date_time, '%Y%m%d') = ?");

                pstmt.clearParameters();
                pstmt.setInt(1, array.get(x));       // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                if ( rs.next() ) {                   // if times exist

                    found = 1;                        // found one - we're done checking
                }

            } catch (Exception exc) {

                Utilities.logError("buildTimeSheets: Error=" + exc.getMessage());

            } finally {

                i++; // bump the counter here so errors will not cause infinte loops

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

            if (found == 0) {

                // we didn't find any times for this day - let's build them all
                buildTimeSheet(array.get(x), date, club, con);

            } else {

                // we found some times, let's make any nessesary changes
                rebuildTimeSheet(array.get(x), date, club, con);

            }

            cal.add(Calendar.DATE, -1);                 // roll back one day
            day = cal.get(Calendar.DAY_OF_MONTH);
            month = cal.get(Calendar.MONTH) +1;
            year = cal.get(Calendar.YEAR);
            //day_num = cal.get(Calendar.DAY_OF_WEEK);

            date = (year * 10000) + (month * 100) + day;

        } // end year while loop

    } // end for loop of avtivities are are building time sheets for

 }


 private static void rebuildTimeSheet(int activity_id, int date, String club, Connection con) {

    int interval = 90;      // minutes between times
    int alt_interval = 90;
    int first_time = 830;
    int last_time = 1900;
    int this_time = first_time;
    int hr = this_time / 100;
    int min = this_time - (hr * 100);

    boolean alt = false;

    Calendar cal = null;

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    // CHECK EACH TIME TO SEE IF IT EXISTS

    while (this_time <= last_time) {

        try {

            pstmt = con.prepareStatement (
                    "SELECT sheet_id " +
                    "FROM activity_sheets " +
                    "WHERE activity_id = ? AND " +
                        "DATE_FORMAT(date_time, '%Y%m%d') = ? AND " +
                        "DATE_FORMAT(date_time, '%H%i') = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, activity_id);
            pstmt.setInt(2, date);
            pstmt.setInt(3, this_time);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                // do nothing - time already exists

            } else {

                // time not found - let's add it
                buildTimeSlot(date, this_time, activity_id, con);

            }

        } catch (Exception exc) {

            Utilities.logError("rebuildTimeSheet: error=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        // roll ahead one interval

        cal = new GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);

        cal.add(Calendar.MINUTE, (!alt) ? interval : alt_interval);

        hr = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);

        this_time = hr * 100 + min;

        alt = alt == false;

    }

 }

} // end servlet public class