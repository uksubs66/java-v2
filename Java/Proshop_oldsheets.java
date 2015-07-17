/***************************************************************************************
 *   Proshop_oldsheets:  This servlet will process the 'View Old Tee Sheets' request from
 *                       the Proshop's navigation bar.
 *
 *
 *   called by:  Proshop_maintop (doGet)
 *               Proshop_oldsheets (doPost)
 *
 *
 *   created: 5/20/2002   Bob P.
 *
 *   last updated:
 *
 *        5/24/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        4/29/10   Tee Sheet notes section only appears if there are notes to display
 *                  - Added Minnetoka CC to the disp_mnum setting
 *                  - Fixed the extra pace button appearing next to the control panel
 *        4/08/10   Added guest tracking processing
 *        3/08/10   Don't display Cancel button on editTime page for TPC Craig Ranch.
 *        2/19/10   Added Tee Sheet Notes display above the tee sheet
 *        1/25/10   Updated moveguest Javascript function to handle the new use_guestdb value being passed to it.
 *        1/05/10   Move calendar and course selection to doPost method so everything is on one page (much easier for pros).
 *        9/16/09   Added support for the REST_OVERRIDE limited access proshop user field to block users without access from overriding restrictions.
 *        9/15/09   Change Limited Access Proshop User calls for TS_VIEW and TS_UPDATE to the "past"-specific versions: TS_PAST_VIEW, TS_PAST_UPDATE
 *        9/08/09   Remove stats5 db table processing as we don't use this any longer.
 *        6/30/09   Display the new check-in image when checked in and pos charges sent.
 *        3/26/09   Change verify to populate new teepast2 fields mtype, mship, grev, gtype values when adding/changing tee times
 *       10/22/08   Add PoP Report to row of buttons at top of page (case 1392).
 *        9/14/08   Removed restrictions on new features
 *        8/26/08   Add View Wait List option to days that had a wait list with signups on it
 *        8/07/08   Added additional limited access proshop user restrictions
 *        7/29/08   Los Coyotes - display the GHIN number (SCGA#) in place of the gender - next to the mNum.
 *        7/22/08   Add POS Report button to Old Tee Sheet display (case 1429).
 *        7/18/08   Added limited access proshop users checks
 *        3/31/08   Add Excel export functionality Case# 1260
 *        3/24/08   Remove the link to update the stats and perform this function any time the old tee
 *                  sheet is updated.  This way the stats will always be updated and pro won't have to
 *                  remember to do it.  (Temp solution for case #1182)
 *        2/07/07   Modified Tee Time History calling code
 *        8/03/06   Fixed show boxes to only display x if show =! 1 (was if == 0)
 *        3/28/06   Added "Tee Time History" option to the tee sheet
 *        3/20/06   Added "Check All In" as a Control Panel option - only available if not using ProShopKeeper POS
 *        8/21/05   Move displayNotes to SystemUtils to share the process.
 *        7/07/05   Custom for Forest Highlands - default course depends on login id.
 *        3/11/05   Ver 5 - add Insert processing to insert a new tee time.
 *        1/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *        9/23/04   V5 - revamp so pro can edit the tee times.  Use calendar instead of date selection.
 *        9/13/04   Add 'Notes' to the old tee sheets.
 *        3/21/04   Enhancements for Version 4 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/03/03   Enhancements for Version 2 of the software.
 *                  Add multiple course support.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
//import java.lang.Math;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.parmSlot;
import com.foretees.common.parmPOS;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.BigDate;
import com.foretees.common.parmItem;
import com.foretees.common.getItem;
import com.foretees.common.getWaitList;
import com.foretees.common.Utilities;


public class Proshop_oldsheets extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the call from Proshop Navigation (no parms passed)
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    
      doPost(req, resp);      // call doPost processing
      return;
    
      
   // NOTE:  the calendar and course selection has been moved to doPost below so it can all reside on one page!!!!!!!!!!!
    
   /*
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   Statement stmt = null;
   ResultSet rs = null;
     
   int multi = 0;               // multiple course support
   int index= 0;
     
   long date = 0;                 // date returned - use for calendar
   long yy = 0;                   // year
   long mm = 0;                   // month
   long dd = 0;                   // day
   long old_date = 0;             // oldest date that tee sheets exist
   long old_mm = 0;               // oldest month
   long old_dd = 0;               // oldest day
   long old_yy = 0;               // oldest year
   long new_date = 0;             // newest date that tee sheets exist (yesterday)
     
   String courseName = "";        // course names

   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club


   if (req.getParameter("post") != null) {      // if call is for doPost (auto return from below)

      doPost(req, resp);      // call doPost processing
      return;
   }


   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

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

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TS_VIEW", con, out)) {
       SystemUtils.restrictProshop("TS_VIEW", out);
   }
   
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");               // get user name
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  If date passed, then this is a return - use date for calendar
   //
   if (req.getParameter("date") != null) {

      String dates = req.getParameter("date");

      date = Long.parseLong(dates);

      yy = date / 10000; 
      mm = (date - (yy * 10000)) / 100;
      dd = date - ((yy * 10000) + (mm * 100)); 
   }

   //
   // Get the 'Multiple Course' option from the club db
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
      }
      stmt.close();

      if (multi != 0) {           // if multiple courses supported for this club

         while (index< 20) {

            course[index] = "";       // init the course array
            index++;
         }

         index = 0;

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
      }
           
      //
      //  Get the oldest date with tee sheets for this club
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT MIN(date) " +
                             "FROM teepast2");

      if (rs.next()) {

         old_date = rs.getLong(1);
      }
      stmt.close();

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Determine oldest date values - month, day, year
   //
   old_yy = old_date / 10000;
   old_mm = (old_date - (old_yy * 10000)) / 100;
   old_dd = old_date - ((old_yy * 10000) + (old_mm * 100));

   //
   //  Output a page to prompt for a date
   //
   out.println(SystemUtils.HeadTitle2("Proshop View Past Tee Sheets Page"));
     
   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
   out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts-old.js\"></script>");

   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
   out.println("<tr><td align=\"center\" valign=\"top\">");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
   out.println("<tr><td align=\"center\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

   out.println("<font size=\"3\">");
   out.println("<p align=\"center\"><b>View Past Tee Sheets</b></p></font>");
   out.println("<br><font size=\"2\">");

   out.println("<table cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
     
   if (multi != 0) {           // if multiple courses supported for this club

      out.println("<b>Instructions:</b>  To view a past tee sheet, select the course and date of the tee sheet you desire.");
      
   } else {
     
      out.println("<b>Instructions:</b>  To view a past tee sheet, select the date of the tee sheet you desire.");
   }
   out.println("</font></td></tr></table><br>");


      out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" target=\"bot\" method=\"post\" name=\"frmLoadDay\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");              // value set by script
         out.println("<input type=\"hidden\" name=\"old_mm\" value=\"" +old_mm+ "\">");   // oldest month (for js)
         out.println("<input type=\"hidden\" name=\"old_dd\" value=\"" +old_dd+ "\">");   // oldest day
         out.println("<input type=\"hidden\" name=\"old_yy\" value=\"" +old_yy+ "\">");   // oldest year

         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">");
         out.println("<b>Note:</b> &nbsp;The most recent date you can enter is yesterday.&nbsp;&nbsp;Empty (unused) tee times will not be displayed.<br><br>");

         //
         //  If multiple courses, then add a drop-down box for course names
         //
         if (multi != 0) {           // if multiple courses supported for this club

            String firstCourse = course[0];    // default to first course

            if (club.equals( "foresthighlands" )) {

               if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

                  firstCourse = "Meadow";      // setup default course (top of the list)

               } else {

                  firstCourse = "Canyon";
               }
            }

            index = 0;
            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");

            courseName = course[index];      // get course name from array

            while ((!courseName.equals( "" )) && (index < 20)) {

               if (courseName.equals( firstCourse )) {
                  out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
               } else {
                  out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
               }
               index++;
               if (index < 20) {
                  courseName = course[index];      // get course name from array
               }
            }
            out.println("</select>");
            out.println("<br><br>");

         } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
         }

         out.println("</font></form></p>");

         out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

         out.println(" <div id=calendar0 style=\"width: 180px\"></div>");

         out.println("</td></tr>\n</table>");

         Calendar cal_date = new GregorianCalendar();         // Calendar.getInstance();
         int cal_year = cal_date.get(Calendar.YEAR);
         int cal_month = cal_date.get(Calendar.MONTH) + 1;
         int cal_day = cal_date.get(Calendar.DAY_OF_MONTH) - 1;

         out.println("<script type=\"text/javascript\">");
         out.println("var iEndingDay = " + cal_day + ";");
         out.println("var iEndingYear = " + cal_year + ";");
         out.println("var iEndingMonth = " + cal_month + ";");
         out.println("var iStartingMonth = " + old_mm + ";");
         out.println("var iStartingDay = " + old_dd + ";");
         out.println("var iStartingYear = " + old_yy + ";");
         if (cal_day == 0) {
             cal_month--;
             if (cal_month == 0) { cal_month = 12; cal_year--; }
         }
         if (date > 0) {                                              // if date provided on return - use it
            out.println("doCalendar('" + mm + "', '" + yy + "');");
         } else {
            out.println("doCalendar('" + cal_month + "', '" + cal_year + "');");
         }
         out.println("</script>");
           
         out.println("<font size=\"2\">");
         out.println("<p align=\"center\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Exit\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></p>");
         out.println("</font>");

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                           // end of main page
   out.println("</td></tr></table>");                           // end of whole page
   out.println("</center></body></html>");
   out.close();
    */


 }  // end of doGet processing


 //*****************************************************
 //  doPost
 //*****************************************************
 //
 //   Parms passed:  calDate - mm/dd/yyyy 
 //                  course - course name
 //                  time - time of tee time
 //                  fb - front/back indicator
 //                  jump - row to jump to
 //                  edit - if clicked on a tee time to edit
 //                  noshow - if checking a player in or out
 //                  notes - if call to display the notes
 //
 //*****************************************************
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
   
   // set response content type
   try{
       if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
           resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       } else {
           resp.setContentType("text/html");
       }
   } catch (Exception exc) { }
   
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
     

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) return;
   
   Connection con = SystemUtils.getCon(session);                      // get DB connection

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
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TS_PAST_VIEW", con, out)) {
       SystemUtils.restrictProshop("TS_PAST_VIEW", out);
   }

   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");               // get user name
   
   //
   //  Check for 'Insert' or 'Delete' request
   //
   if (req.getParameter("insert") != null) {

      doInsert(req, out, con, session);          // process insert request
      return;
   }
     
   //
   //  See if we are in the timeless tees mode
   //
   boolean IS_TLT = ((Integer)session.getAttribute("tlt") == 1) ? true : false;
  
   int count = 0;
   int p = 0;
   int fives = 0;
   int jump = 0;
   int j = 0;
   int index = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int teepast_id = 0;
   int paceofplay = 0;  // hold indicator of wether of not PoP is enabled
     
   long date = 0;
     
   long yy = 0;                   // year
   long mm = 0;                   // month
   long dd = 0;                   // day
   long old_date = 0;             // oldest date that tee sheets exist
   long old_mm = 0;               // oldest month
   long old_dd = 0;               // oldest day
   long old_yy = 0;               // oldest year
   long new_date = 0;             // newest date that tee sheets exist (yesterday)
     
   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
     
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String mnum1 = "";
   String mnum2 = "";
   String mnum3 = "";
   String mnum4 = "";
   String mnum5 = "";
      
   String ampm = "";
   String event_rest = "";
   String bgcolor = "";
   String stime = "";
   String sshow = "";
   String name = "";
   String sfb = "";
   String submit = "";
   String num = "";
   String jumps = "";
   String course = "";

   String event1 = "";       // for legend - max 2 events, 4 rest's
   String ecolor1 = "";
   String rest1 = "";
   String rcolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String notes = "";
   String calDate = "";
   String day_name = "";
   String showbox = "";
   
   String oldest_date = "";
   String newest_date = "";
   String dates = "";

   String courseName = "";        // course names

   //
   //  Array to hold the course names
   //
   ArrayList<String> courseA = new ArrayList<String>();      // unlimited courses


   boolean noShow = false;
   boolean disp_mnum = false;
   boolean updateAccess = SystemUtils.verifyProAccess(req, "TS_PAST_UPDATE", con, out);
   boolean checkinAccess = SystemUtils.verifyProAccess(req, "TS_CHECKIN", con, out);
   boolean printAccess = SystemUtils.verifyProAccess(req, "TS_PRINT", con, out);
   boolean posAccess = SystemUtils.verifyProAccess(req, "TS_POS", con, out);
   boolean reportAccess = SystemUtils.verifyProAccess(req, "REPORTS", con, out);
   boolean waitListAccess = SystemUtils.verifyProAccess(req, "WAITLIST_VIEW", con, out);
   boolean popAccess = SystemUtils.verifyProAccess(req, "TS_PACE_VIEW", con, out);

   String updShow = "";

   String updShow1 = "UPDATE teepast2 SET show1 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow2 = "UPDATE teepast2 SET show2 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow3 = "UPDATE teepast2 SET show3 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow4 = "UPDATE teepast2 SET show4 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
   String updShow5 = "UPDATE teepast2 SET show5 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";

   String updShowAll = "UPDATE teepast2 SET show1=1, show2=1, show3=1, show4=1, show5=1 WHERE date=? AND time=? AND fb=? AND courseName=?";

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   
   if (req.getParameter("course") != null) {

      course = req.getParameter("course");
   }
   
   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms
   parmClub parm = new parmClub(0, con);  // golf only feature

   try {

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception ignore) {
   }   
      
   paceofplay = parm.paceofplay;
   
   
   Calendar cal_date = new GregorianCalendar();         // get today's date
   int thisyear = cal_date.get(Calendar.YEAR);

   
   //
   //  Get the parms passed and build a date field to search for (yyyymmdd)
   //
   //  NOTE: some callers pass 'date' and some 'calDate' or 'gotodate' and the menu comes with neither!!!
   //  
   if (req.getParameter("gotodate") != null) {     // if user typed in the date in the "GoTo" box

      dates = req.getParameter("gotodate");

      StringTokenizer tok = new StringTokenizer( dates, "/" );     // space is the default token - use '/'
      
      month = 0;      // init so we can detect an invalid date

      if ( tok.countTokens() == 3 ) {
         
         num = tok.nextToken();                    // get the mm value

         month = Integer.parseInt(num);

         num = tok.nextToken();                    // get the dd value

         day = Integer.parseInt(num);

         num = tok.nextToken();                    // get the yyyy value

         year = Integer.parseInt(num);

         date = (year * 10000) + (month * 100) + day;         // create a date field of yyyymmdd

         calDate = month + "/" + day + "/" + year;            // create calDate         
      } 
      
      if (month < 1 || month > 12 || day < 1 || day > 31 || year < 2002 || year > thisyear) {
         
         //  invalid date
         
         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H2>Data Entry Error</H2>");
         out.println("<BR><BR>Sorry, the date you entered is invalid.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_oldsheets\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
      
   } else if (req.getParameter("date") != null) {

      dates = req.getParameter("date");

      date = Long.parseLong(dates);

      yy = date / 10000; 
      mm = (date - (yy * 10000)) / 100;
      dd = date - ((yy * 10000) + (mm * 100)); 
      
      year = (int)yy; 
      month = (int)mm;
      day = (int)dd; 

      calDate = month + "/" + day + "/" + year;            // create calDate 
      
   } else if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");

      //
      //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      num = tok.nextToken();                    // get the mm value

      month = Integer.parseInt(num);

      num = tok.nextToken();                    // get the dd value

      day = Integer.parseInt(num);

      num = tok.nextToken();                    // get the yyyy value

      year = Integer.parseInt(num);

      date = (year * 10000) + (month * 100) + day;         // create a date field of yyyymmdd
      
   } else {
      
      // use yesterday as default
      
      cal_date = new GregorianCalendar();         // get today's date
      cal_date.add(Calendar.DATE,-1);                      // get yesterday's date
      year = cal_date.get(Calendar.YEAR);
      month = cal_date.get(Calendar.MONTH) + 1;
      day = cal_date.get(Calendar.DAY_OF_MONTH);    

      date = (year * 10000) + (month * 100) + day;         // create a date field of yyyymmdd
      
      calDate = month + "/" + day + "/" + year;            // create calDate 
   }
   
   
   //
   //  use date for calendar
   //
   yy = year;
   mm = month;
   dd = day;

   
   try {

      if (parm.multi != 0) {           // if multiple courses supported for this club

         courseA = Utilities.getCourseNames(con);     // get all the course names
         
         if (course.equals("")) {
            
            course = courseA.get(0);     // grab first course
         }
      }
           
      //
      //  Get the oldest date with tee sheets for this club
      //
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT MIN(date) " +
                             "FROM teepast2");

      if (rs.next()) {

         old_date = rs.getLong(1);
      }
      stmt.close();

      //
      //  Determine oldest date values - month, day, year
      //
      old_yy = old_date / 10000;
      old_mm = (old_date - (old_yy * 10000)) / 100;
      old_dd = old_date - ((old_yy * 10000) + (old_mm * 100));
      
      oldest_date = old_mm + "/" + old_dd + "/" + old_yy;    // create string for 'go to date' option
      
      cal_date = new GregorianCalendar();         // get today's date
      cal_date.add(Calendar.DATE,-1);                      // get yesterday's date

      newest_date = (cal_date.get(Calendar.MONTH) + 1) + "/" + cal_date.get(Calendar.DAY_OF_MONTH) + "/" + cal_date.get(Calendar.YEAR);    // create string for 'go to date' option
      
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   
      
   //
   // Let check to see if the checkallin parameter is present
   //
   if (req.getParameter("checkallin") != null && checkinAccess) {
      
      // run sql statement to set show# values to 1
      String tmpWhereClause = "";
      String tmpSQL = "";
      long today = Long.parseLong(req.getParameter("date"));
      
      try {
     
         PreparedStatement pstmt3 = null;
         
         // if course is -ALL- then we'll won't be including the course name in the clause
         tmpWhereClause = (course.equals("-ALL-")) ? ";" : " AND courseName = ?;";

         // shouldn't have to worry about supporting 5somes, since the update should
         // will only affect rows the the player# field is already set
         for (int i7=1; i7<6; i7++) {
             tmpSQL = "UPDATE teepast2 SET show" + i7 + " = 1 WHERE player" + i7 + " <> '' AND date = ?" + tmpWhereClause;
             pstmt3 = con.prepareStatement (tmpSQL);
             pstmt3.clearParameters();        // clear the parms
             pstmt3.setLong(1, today);
             if (!course.equals("-ALL-")) pstmt3.setString(2, course);
             pstmt3.executeUpdate();      // execute the prepared stmt
             pstmt3.close();
         }
    
      }
      catch (Exception exp) {
        displayDatabaseErrMsg("Error checking in all players for this previous tee-sheet.", exp.getMessage(), out);
      }
      
   } 

   //
   //  get the jump parm if provided (location on page to jump to)
   //
   if (req.getParameter("jump") != null) {

      jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page

      try {
         jump = Integer.parseInt(jumps);
      }
      catch (NumberFormatException e) {
         jump = 0;
      }
   }

   //
   //   if call was to edit a tee time - go do it
   //
   if (req.getParameter("edit") != null && updateAccess) {

      editTime(date, course, jump, req, out, session, con);     // go edit the tee time
      return;
   }
   
   //
   //   if call was to view an old wait list - go do it
   //
   if (req.getParameter("viewWL") != null && waitListAccess) {

      viewSignups(req, out, con);
      return;
   }

   //
   //   Adjust jump so we jump to the selected line minus 3 so its not on top of page
   //
   if (jump > 3) {

      jump = jump - 3;

   } else {

      jump = 0;         // jump to top of page
   }

   //
   //   if call was for Show Notes then get the notes and display a new page
   //
   if (req.getParameter("displayNotes") != null) {

      stime = req.getParameter("time");         //  time of the slot
      sfb = req.getParameter("fb");             //  front/back indicator

      SystemUtils.displayOldNotes(stime, sfb, date, course, out, con);             // display the information
      return;
   }

   if (((req.getParameter("noShow") != null) || (req.getParameter("checkAll") != null)) && checkinAccess) {

      //
      //  proshop clicked on the box next to player's name to toggle the 'show' parm (checked-in or no-show)
      //  or proshop clicked on the 'check all' box to check in all players in slot
      //
      stime = req.getParameter("time");         //  time of the slot
      sfb = req.getParameter("fb");             //  front/back indicator

      if (req.getParameter("noShow") != null) {

         noShow = true;
         player = req.getParameter("noShow");      //  number of the player selected (1-4)
         sshow = req.getParameter("show");         //  current show value for player selected

         p = Integer.parseInt(player);
         show = Short.parseShort(sshow);
      }

      //
      //  Convert the common string values to int's
      //
      time = Integer.parseInt(stime);
      fb = Short.parseShort(sfb);
        
      //
      //   if call was for Check All, then check-in all players in the specified slot
      //
      if (req.getParameter("checkAll") != null) {

         try {

            PreparedStatement pstmt2 = con.prepareStatement (updShowAll);

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, date);
            pstmt2.setInt(2, time);
            pstmt2.setShort(3, fb);
            pstmt2.setString(4, course);
            count = pstmt2.executeUpdate();      // execute the prepared stmt

            pstmt2.close();

         }
         catch (Exception ignore) {
         }
      }

      //
      //   if call was for noShow, then toggle the show indicator in the specified slot for the specified player
      //
      if (noShow == true) {

         if (show == 0) {

            show = 1;              // toggle the show value (0 = no-show, 1 = show)

         } else {

            show = 0;
         }

         if (p == 1) {              // get sql string based on player #
            updShow = updShow1;
         }
         if (p == 2) {              
            updShow = updShow2;
         }
         if (p == 3) {              
            updShow = updShow3;     
         }
         if (p == 4) {              
            updShow = updShow4;
         }
         if (p == 5) {        
            updShow = updShow5;
         }

         //
         //  update teepast to reflect the new 'show' setting for this player
         //
         try {

            PreparedStatement pstmt1 = con.prepareStatement (updShow);  // use selected stmt

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setShort(1, show);       // put the parm in pstmt1
            pstmt1.setLong(2, date);
            pstmt1.setInt(3, time);
            pstmt1.setShort(4, fb);
            pstmt1.setString(5, course);
            count = pstmt1.executeUpdate();      // execute the prepared stmt

            pstmt1.close();

         }
         catch (Exception e2) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Unable to access the Database at this time (check-in/out player).");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR>" + e2.getMessage());
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</BODY></HTML>");
            out.close();
            return;
         }
      }             
   }             // end of show unique processing
 
   
   
   //
   //  Customs to display the mNum after name on tee sheet
   //
   if (club.equals( "loscoyotes" ) || club.equals( "minnetonkacc" )) {
      
      disp_mnum = true;
   }

   try {

      //
      //  Get the POS System Parameters for this Club & Course
      //
      getClub.getPOS(con, parmp, course);
   
   
      //
      //  Get the 5-some option & name of day we're viewing
      //
      PreparedStatement pstmtc = con.prepareStatement (
         "SELECT fives, DATE_FORMAT(?, '%W') AS day_name " +
         "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

      pstmtc.clearParameters();
      pstmtc.setLong(1, date);
      pstmtc.setString(2, course);
      rs = pstmtc.executeQuery();

      if (rs.next()) {

         fives = rs.getInt(1);              // 5-somes
         day_name = rs.getString(2);        // day name
      }
      pstmtc.close();

      //
      //  Get the tee sheet for this date
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT event, event_color, restriction, rest_color " +
         "FROM teepast2 " +
         "WHERE date = ? AND courseName = ? " +
         "ORDER BY time, fb");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      //
      //  Scan the tee sheet for events and restrictions to build the legend
      //

      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);
         rest = rs.getString(3);
         rcolor = rs.getString(4);

         if ((!event.equals( event1 )) && (!ecolor.equals( "Default" )) && (event1.equals( "" ))) {

            event1 = event;
            ecolor1 = ecolor;

          } else {

            if ((!event.equals( event1 )) && (!event.equals( event2 )) && (!ecolor.equals( "Default" )) && (event2.equals( "" ))) {

               event2 = event;
               ecolor2 = ecolor;
            }
         }

         if ((!rest.equals( rest1 )) && (!rcolor.equals( "Default" )) && (rest1.equals( "" ))) {

            rest1 = rest;
            rcolor1 = rcolor;

          } else {

            if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rcolor.equals( "Default" )) && (rest2.equals( "" ))) {

               rest2 = rest;
               rcolor2 = rcolor;

            } else {

               if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rcolor.equals( "Default" )) && (rest3.equals( "" ))) {

                  rest3 = rest;
                  rcolor3 = rcolor;

               } else {

                  if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (!rest.equals( rest4 )) && 
                       (!rcolor.equals( "Default" )) && (rest4.equals( "" ))) {

                     rest4 = rest;
                     rcolor4 = rcolor;
                  }
               }
            }
         }
      }                  // end of while
      pstmt.close();

      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      if (!excel.equals("yes")) {
         out.println(SystemUtils.HeadTitle2("Proshop - View Past Sheets"));
         out.println("</HEAD>");
     
         // include files for dynamic calendars
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
         out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts-old.js\"></script>");

         out.println("<script language='JavaScript'>");          // Jump script
         out.println("<!--");
         out.println("function jumpToHref(anchorstr) {");
         out.println(" if (location.href.indexOf(anchorstr)<0) {");
         out.println("  location.href=anchorstr; }");
         out.println("}");
         out.println("// -->");
         out.println("</script>");                               // End of script

         out.println("<script language='javascript'>");
         out.println("function openDiaryWindow() {");
         out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_diary?scroll=1&index=" +num+ "&course=" +course+ "&year=" +year+ "&month=" +month+ "&day=" +day+ "','clientIdPopup','width=640,height=475,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=0,status=0,toolbar=0');");
         out.println(" w.creator = self;");
         out.println("}");
         out.println("function openHistoryWindow(date, course, time, fb, tid) {");
         out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_teetime_history?calDate=' +date+ '&course=' +course+ '&time=' +time+ '&fb=' +fb+ '&tpid=' +tid+ '&history=yes','historyPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
         out.println(" w.creator = self;");
         out.println("}");
         out.println("</script>");
      }
    
      out.println("<body onLoad='jumpToHref(\"#jump" + jump + "\");' bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      if (!excel.equals("yes")) SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

    if (!excel.equals("yes")) {

       
      out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

      out.println("<font size=\"4\">");
      out.println("<p align=\"center\"><BR><b>View Past Tee Sheets</b></p></font>");

      out.println("<font size=\"2\">");
      out.println("<p align=\"center\">");
      out.println("<b>Note:</b> &nbsp;The most recent date you can access is yesterday.&nbsp;&nbsp;Empty (unused) tee times will not be displayed.<br></p>");

      //
      //  Table to hold Control Panel and Calendar
      //
      out.println("<table align=center border=0 width=\"85%\"><tr valign=top>");   

      out.println("<td align=left width=\"30%\">");         // column for Control Panel
            
         //
         //************************************************************************
         //  Table for the Control Panel
         //************************************************************************
         //
         out.println("<table border=\"1\" width=\"150\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"left\">");
         out.println("<tr>");
         out.println("<td align=\"center\"><font color=\"#000000\" size=\"3\"><b>Control Panel</b><br>");
         out.println("</font></td></tr>");

         // only display the "Check All In" feature if not using Pro-ShopKeeper POS
         //if (!parmp.posType.equals( "Pro-ShopKeeper" )) {

         // only display if proshop user has check-in access
         if (checkinAccess) {

             out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_oldsheets?checkallin=yes&course=" +course+ "&date=" +date+ "&calDate=" +calDate+ "\" title=\"Check In All Players\" alt=\"Check All In\" onclick=\"return confirm('Are you sure you want to check in all players on this tee-sheet?');\">");
             out.println("Check All In</a>");
             out.println("</td></tr>");
             /*
             out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" target=\"bot\">");
             out.println("<tr><td align=\"left\">");
             out.println("<input type=\"hidden\" name=\"checkallin\" value=\"yes\">");
             out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
             out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
             out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
             out.println("<input type=\"submit\" value=\"Check All In\" onclick=\"return confirm('Are you sure you want to check in all players on this tee-sheet?');\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</td></tr></form>");
              */
         }   
         //}

         // only display if user has update access
         if (updateAccess) {
            
             out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
             out.println("<a href=\"javascript:void(0)\" onclick=\"openDiaryWindow(); return false;\"  title=\"Diary Entry\" alt=\"Diary Entry\">");
             out.println("Diary Entry</a>");
             out.println("</td></tr>");
            
             out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_oldsheets?insert=yes&course=" +course+ "&date=" +date+ "&calDate=" +calDate+ "\" title=\"Insert New Tee Time\" alt=\"Insert New Tee Time\">");
             out.println("Insert New Tee Time</a>");
             out.println("</td></tr>");
               
             /*
             out.println("<tr><td align=\"left\">");
             out.println("<input type=\"button\" value=\"Diary Entry\" onclick=\"openDiaryWindow();\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</td></tr>");

             out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" target=\"bot\">");
             out.println("<tr><td align=\"left\">");
             out.println("<input type=\"hidden\" name=\"insert\" value=\"yes\">");
             out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
             out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
             out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
             out.println("<input type=\"submit\" value=\"Insert New Tee Time\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</td></tr></form>");
              */
         }

         if (!parmp.posType.equals( "" ) && !parmp.posType.equals( "None" )) {  // any POS type 

            // only display if user has pos access
            if (posAccess) {
               
                out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
                out.println("<a href=\"/" +rev+ "/servlet/Proshop_report_pos?oldSheet=yes&course=" +course+ "&date=" +date+ "\" title=\"POS Report\" alt=\"POS Report\" target=\"_blank\">");
                out.println("POS Report</a>");
                out.println("</td></tr>");              
                /*
                out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_pos\" target=\"_blank\">");
                out.println("<tr><td align=\"left\">");
                out.println("<input type=\"hidden\" name=\"oldSheet\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
                out.println("<input type=\"submit\" value=\"POS Report\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</td></tr></form>");
                 */
            }
         }

         // only display if user has reports access
         if (reportAccess) {
            
             out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_report_handicap?todo=view2&course=" +course+ "&date=" +date+ "\" title=\"Posted Scores\" alt=\"Posted Scores\">");
             out.println("Posted Scores</a>");
             out.println("</td></tr>");            
             /*
             out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_handicap\">");
             out.println("<tr><td align=\"left\">");
             out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
             out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
             out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
             out.println("<input type=\"submit\" value=\"Posted Scores\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</td></tr></form>");
              */
         }

         // only display if user has wait list view access
         if (waitListAccess) {

             parmItem parmWaitLists = new parmItem();          // allocate a parm block

             try {

                 getItem.getWaitLists(date, course, day_name, parmWaitLists, con);

             } catch (Exception e) {
                 SystemUtils.buildDatabaseErrMsg("Error loading wait list data.", e.getMessage(), out, true);
             }

             for (int z = 0; z < parmWaitLists.count; z++) {

                if ( parmWaitLists.signups[z] > 0 ) {

                   out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
                   out.println("<a href=\"/" +rev+ "/servlet/Proshop_oldsheets?viewWL=''&course=" +course+ "&date=" +date+ "&waitListId=" +parmWaitLists.id[z]+ "&start_time=" +parmWaitLists.stime[z]+ "&end_time=" +parmWaitLists.etime[z]+ "&name=" +parmWaitLists.name[z]+ "\" title=\"View Wait List\" alt=\"View Wait List\">");
                   out.println("View Wait List" + ((z>1) ? " " + z : "") + "</a>");
                   out.println("</td></tr>");
                    /*
                    out.println("<form method=\"POST\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                    out.println("</tr><td align=\"left\">");
                    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" +parmWaitLists.id[z]+ "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" +date+ "\">");
                    out.println("<input type=\"hidden\" name=\"start_time\" value=\"" +parmWaitLists.stime[z]+ "\">");
                    out.println("<input type=\"hidden\" name=\"end_time\" value=\"" +parmWaitLists.etime[z]+ "\">");
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + parmWaitLists.name[z] + "\">");
                    out.println("<input type=\"hidden\" name=\"viewWL\" value=\"\">");
                    out.println("<input type=\"submit\" value=\"View Wait List" + ((z>1) ? " " + z : "") + "\" style=\"text-decoration:underline; background:#8B8970\">");
                    out.println("</td></tr></form>");
                     */
                }
             }
         }

         // only display if user has print access
         if (printAccess) {
            
             out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
             out.println("<a href=\"javascript:void(0)\" onclick=\"javascript:self.print(); return false;\"  title=\"Print Sheet\" alt=\"Print Sheet\">");
             out.println("Print Sheet</a>");
             out.println("</td></tr>");
            
             out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
             out.println("<a href=\"/" +rev+ "/servlet/Proshop_oldsheets?excel=yes&course=" +course+ "&calDate=" +calDate+ "\" title=\"Excel\" alt=\"Excel\" target=\"_blank\">");
             out.println("Excel</a>");
             out.println("</td></tr>");             
             /*
             out.println("<form method=\"link\" action=\"javascript:self.print()\">");
             out.println("<tr><td align=\"left\">");
             out.println("<input type=\"submit\" value=\"Print Sheet\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</td></tr></form>");

             out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" target=\"_blank\">");
             out.println("<tr><td align=\"left\">");
             out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
             out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
             out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
             out.println("<input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</td></tr></form>");
              */
         }

         if (paceofplay != 0 && popAccess == true) {

             //
             // Decide if we are going to show the pace report link
             //
             int pace_count = 0;
             try {

                 PreparedStatement pstmt1 = con.prepareStatement("" +
                         "SELECT COUNT(*) FROM teepast2 " +
                         "WHERE date = ? AND pace_status_id != 0 " +
                         ((course.equals("-ALL-")) ? "" : "AND courseName = ?") +
                         "");

                 pstmt1.clearParameters();
                 pstmt1.setLong(1, date);
                 if (!course.equals( "-ALL-" )) pstmt1.setString(2, course);
                 rs = pstmt1.executeQuery();

                 if (rs.next()) {

                     pace_count = rs.getInt(1);
                 }

             } catch (Exception e) {

                 SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
             }
             if (pace_count > 0) {
                
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_pace\" method=\"POST\" name=\"paceform\">");
                out.println("<input type=hidden name=todo value=\"oldtee\">");
                out.println("<input type=hidden name=cal_box_0 value=\"" + date + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
                //out.println("<input type=\"submit\" value=\"Today's Pace (" + pace_count + ")\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form>");
                
                out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
                out.println("<a href=\"javascript:void(0)\" onclick=\"paceform.submit(); return false;\"  title=\"This Day's Pace\" alt=\"This Day's Pace\">");
                out.println("This Day's Pace (" + pace_count + ")</a>");
                out.println("</td></tr>");             
               
             }
         }


         out.println("</tr>");

         if (IS_TLT) {

           int tmp_index = 0;
           Calendar cal = new GregorianCalendar();
           int tmp_year = cal.get(Calendar.YEAR);
           int tmp_month = cal.get(Calendar.MONTH);
           int tmp_day = cal.get(Calendar.DAY_OF_MONTH);
           int cal_hour = cal.get(Calendar.HOUR_OF_DAY);
           int cal_min = cal.get(Calendar.MINUTE);
           int curr_time = SystemUtils.adjustTime(con, (cal_hour * 100) + cal_min);   // adjust the time

           if (curr_time < 0) {          // if negative, then we went back or ahead one day

               curr_time = 0 - curr_time;        // convert back to positive value

               if (curr_time < 1200) {           // if AM, then we rolled ahead 1 day

                   //
                   // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                   //
                   cal.add(Calendar.DATE,1);                     // get next day's date

                   tmp_year = cal.get(Calendar.YEAR);
                   tmp_month = cal.get(Calendar.MONTH);
                   tmp_day = cal.get(Calendar.DAY_OF_MONTH);

               } else {                        // we rolled back 1 day

                   //
                   // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                   //
                   cal.add(Calendar.DATE,-1);                     // get yesterday's date

                   tmp_year = cal.get(Calendar.YEAR);
                   tmp_month = cal.get(Calendar.MONTH);
                   tmp_day = cal.get(Calendar.DAY_OF_MONTH);
               }
           }

           tmp_month++;                           // month starts at zero

           String tmp_date1 = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day); // date sheet is for
           String tmp_date2 = tmp_year + "-" + SystemUtils.ensureDoubleDigit(tmp_month) + "-" + SystemUtils.ensureDoubleDigit(tmp_day); // today
           //out.println("<!-- tmp_date1=" + tmp_date1 + " | tmp_date2=" + tmp_date2 + " -->");

           //
           // Decide if we are going to show the Manage Notifications link in the control panel
           //
           int notifications_count = 0;

           try {

                PreparedStatement pstmt1 = con.prepareStatement("" +
                       "SELECT COUNT(*), DATEDIFF('" +tmp_date1+ "', '" +tmp_date2+ "') AS i " + 
                       "FROM notifications " +
                       "WHERE DATE(req_datetime) = ? AND converted = 0");
                pstmt1.clearParameters();
                pstmt1.setString(1, tmp_date1);
                //if (!course.equals( "-ALL-" )) pstmt1.setString(2, course);
                rs = pstmt1.executeQuery();
                if (rs.next()) {
                    notifications_count = rs.getInt(1);
                    tmp_index = rs.getInt(2);
                }

           } catch (Exception e) {

               SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
           }

           if (notifications_count > 0) {
   /*
               Calendar cal = new GregorianCalendar();
               int tmp_year = cal.get(Calendar.YEAR);
               int tmp_month = cal.get(Calendar.MONTH);
               int tmp_day = cal.get(Calendar.DAY_OF_MONTH);
               int tmp_date = tmp_year * 10000;                     // create a date field of yyyymmdd for today
               tmp_date = tmp_date + (tmp_month * 100);
               tmp_date = tmp_date + tmp_day;
               int tmp_index = (int)(tmp_date - date);
      */
               //out.println("<!-- tmp_date=" + tmp_date + " | date=" + date + " -->");

              out.println("<tr><td align=\"center\"><font color=\"#FFFFFF\" size=\"2\">");
              out.println("<a href=\"/" +rev+ "/servlet/Proshop_dsheet?email=no&course=" +course+ "&index=" +tmp_index+ "\" title=\"Manage Notifications\" alt=\"Manage Notifications\" target=\"_top\">");
              out.println("Manage Notifications (" + notifications_count + ")</a>");
              out.println("</td></tr>");             
              /*
              // out.println("<tr><td align=\"center\" colspan=5>");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
               out.println("<tr><td align=\"left\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" +tmp_index+ "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
               //out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"no\">");
               out.println("<input type=\"submit\" value=\"Manage Notifications (" + notifications_count + ")\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</td></tr></form>");
               */
           }

         } // end if IS_TLT

         out.println("</table>");
         //
         //  end of Control Panel
         //
      

      out.println("</td><td align=center width=\"40%\">");         // column for Course Selection and Calendar
      
      //  form for calendar
      
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" target=\"bot\" method=\"post\" name=\"frmLoadDay\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");  // value set by script - will override current date if changed
      out.println("<input type=\"hidden\" name=\"old_mm\" value=\"" +old_mm+ "\">");   // oldest month (for js)
      out.println("<input type=\"hidden\" name=\"old_dd\" value=\"" +old_dd+ "\">");   // oldest day
      out.println("<input type=\"hidden\" name=\"old_yy\" value=\"" +old_yy+ "\">");   // oldest year

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (parm.multi != 0) {           // if multiple courses supported for this club

         //String firstCourse = courseA[0];    // default to first course
         String firstCourse = course;          // default to course we are displaying now

         if (club.equals( "foresthighlands" )) {

            if (user.equalsIgnoreCase( "proshop3" ) || user.equalsIgnoreCase( "proshop4" )) {

               firstCourse = "Meadow";      // setup default course (top of the list)

            } else {

               firstCourse = "Canyon";
            }
         }

         out.println("<b>Course:</b>&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"course\" onChange=\"document.frmLoadDay.submit()\">");
         
         for (index=0; index < courseA.size(); index++) {
         
            courseName = courseA.get(index);      // get course name from array

            if (courseName.equals( firstCourse )) {
               out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
            } else {
               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
         }
         out.println("</select><br>");

      } else {
         out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
      }

      out.println("</font></form></p>");

      out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   

      out.println(" <div id=calendar0 style=\"width: 180px\"></div>");

      out.println("</td></tr>\n</table>");

      cal_date = new GregorianCalendar();         // Calendar.getInstance();
      int cal_year = cal_date.get(Calendar.YEAR);
      int cal_month = cal_date.get(Calendar.MONTH) + 1;
      int cal_day = cal_date.get(Calendar.DAY_OF_MONTH) - 1;

      out.println("<script type=\"text/javascript\">");
      out.println("var iEndingDay = " + cal_day + ";");
      out.println("var iEndingYear = " + cal_year + ";");
      out.println("var iEndingMonth = " + cal_month + ";");
      out.println("var iStartingMonth = " + old_mm + ";");
      out.println("var iStartingDay = " + old_dd + ";");
      out.println("var iStartingYear = " + old_yy + ";");
      if (cal_day == 0) {
          cal_month--;
          if (cal_month == 0) { cal_month = 12; cal_year--; }
      }
      if (date > 0) {                                              // if date provided on return - use it
         out.println("doCalendar('" + mm + "', '" + yy + "');");
      } else {
         out.println("doCalendar('" + cal_month + "', '" + cal_year + "');");
      }
      out.println("</script>");
       
       
      out.println("</td><td align=left width=\"30%\">");         // column for Go To Date form
      
         if (parm.multi != 0) {           // if multiple courses supported for this club

            out.println("<BR><BR>");      // move the box down more
         }
         out.println("<BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" target=\"bot\" method=\"post\">");
         out.println("<table align=left border=1 width=240 bgcolor=\"#F5F5DC\"><tr><td align=center>");   
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
         out.println("<font size=2>Select a date using the calendar - OR -<BR>Enter the date below in the format shown.<BR>");
         out.println("Date must be in the range of<BR>" +oldest_date+ " to " +newest_date+ ".<BR>");
         out.println("<input type=\"text\" name=\"gotodate\" value=\"" +mm+ "/" +dd+ "/" +yy+ "\" size=\"10\" maxlength=\"10\">");
         out.println("&nbsp;&nbsp;<input type=\"submit\" value=\"GO\" style=\"text-decoration:underline; background:#8B8970\">");        
         out.println("</td></tr></table></form>");

      
      out.println("</td></tr></table>");                   // end of table for Control Panel and Calendar
          
             
    } // end if excel output
      



    //
    //  Add Tee Sheet Notes
    //
    try {

        // try to load notes - if exist then display and allow editing - if none exists allow editing (which will makes new)
        pstmt = con.prepareStatement ("SELECT notes FROM activity_sheet_notes WHERE activity_id = 0 AND DATE_FORMAT(date, '%Y%m%d') = ?");

        pstmt.clearParameters();
        pstmt.setLong(1, date);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            notes = rs.getString("notes");
        }

        pstmt.close();

    } catch (Exception exc) {
            out.println("LOAD ERROR: " + exc.toString());
    } finally {

    }

    // only show the notes if there are notes
    if (!notes.equals("")) {

        out.println("<table cellpadding=0 cellspacing=0 style=\"border: 2px solid darkGreen\">");
        out.println("<tr><td align=\"center\" bgcolor=\"darkGreen\">");
        out.println("<font size=\"4\" color=\"white\">");
        out.println("<b>Tee Sheet Notes</b>");
        out.println("</font></td></tr>");
        out.println("<tr><td style=\"padding: 5px\">");
        out.println(notes);
        out.println("</td></tr>");
        out.println("</table><br>");
    }


    out.println("<font size=\"3\">");

    out.println("Date:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b>");

    if (!course.equals( "" )) {

        out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");

    }
    out.println("</font><font size=\"2\">");
    out.println("<br><br>");
    out.println("<b>Tee Sheet Legend</b>");
    out.println("</font><font size=\"1\">");

      if (!event1.equals( "" )) {

         out.println("<br><button type=\"button\" style=\"background:" + ecolor1 + "\">" + event1 + "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!event2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         }
      } else {

         out.println("<br>");
      }

      if (!rest1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + rcolor1 + "\">" + rest1 + "</button>");

         if (!rest2.equals( "" )) {

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("<button type=\"button\" style=\"background:" + rcolor2 + "\">" + rest2 + "</button>");

            if (!rest3.equals( "" )) {

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<button type=\"button\" style=\"background:" + rcolor3 + "\">" + rest3 + "</button>");

               if ((!rest4.equals( "" )) && (event1.equals( "" ))) {   // do 4 rest's if no events

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<button type=\"button\" style=\"background:" + rcolor4 + "\">" + rest4 + "</button>");
               }
            }
         }

         out.println("<br>");

      } else {

         if (!event1.equals( "" )) {     // if event but no rest, need br

            out.println("<br>");
         }
      }
   
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"85%\">");
      out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Time</b></u>");
            out.println("</font></td>");
         
         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>F/B</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 1</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 2</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 3</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>Player 4</b></u> ");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>C/W</b></u>");
            out.println("</font></td>");

         if (fives != 0) {
           
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>Player 5</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");
          }
      if (!excel.equals("yes")) {
         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>X</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>N</b></u>");
            out.println("</font></td>");

         out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"1\">");
            out.println("<u><b>H</b></u>");
            out.println("</font></td>");
      }
      out.println("</tr>");
      
      int pos_value = 3;      // new POS value to indicate that charges were sent

      if (date < 20090626) {

         pos_value = 1;       // use old value (pos sent indicator)
      }
   

      //
      //  Get the tee sheet for this date
      //
      pstmt = con.prepareStatement (
         "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
         "player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
         "show1, show2, show3, show4, fb, player5, username5, p5cw, show5, " +
         "mNum1, mNum2, mNum3, mNum4, mNum5, notes, p91, p92, p93, p94, p95, teepast_id," +
         "pos1, pos2, pos3, pos4, pos5 " +
         "FROM teepast2 " +
         "WHERE date = ? AND courseName = ? " + 
         "ORDER BY time, courseName, fb");

      pstmt.clearParameters();
      pstmt.setLong(1, date);
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();

      while (rs.next()) {

         hr = rs.getInt(1);
         min = rs.getInt(2);
         time = rs.getInt(3);
         event = rs.getString(4);
         ecolor = rs.getString(5);
         rest = rs.getString(6);
         rcolor = rs.getString(7);
         player1 = rs.getString(8);
         player2 = rs.getString(9);
         player3 = rs.getString(10);
         player4 = rs.getString(11);
         user1 = rs.getString(12);
         user2 = rs.getString(13);
         user3 = rs.getString(14);
         user4 = rs.getString(15);
         p1cw = rs.getString(16);
         p2cw = rs.getString(17);
         p3cw = rs.getString(18);
         p4cw = rs.getString(19);
         show1 = rs.getShort(20);
         show2 = rs.getShort(21);
         show3 = rs.getShort(22);
         show4 = rs.getShort(23);
         fb = rs.getShort(24);
         player5 = rs.getString(25);
         user5 = rs.getString(26);
         p5cw = rs.getString(27);
         show5 = rs.getShort(28);
         mnum1 = rs.getString(29);
         mnum2 = rs.getString(30);
         mnum3 = rs.getString(31);
         mnum4 = rs.getString(32);
         mnum5 = rs.getString(33);
         notes = rs.getString(34);
         p91 = rs.getInt(35);
         p92 = rs.getInt(36);
         p93 = rs.getInt(37);
         p94 = rs.getInt(38);
         p95 = rs.getInt(39);
         teepast_id = rs.getInt(40);
         pos1 = rs.getInt(41);
         pos2 = rs.getInt(42);
         pos3 = rs.getInt(43);
         pos4 = rs.getInt(44);
         pos5 = rs.getInt(45);

         ampm = " AM";
         if (hr == 12) {
            ampm = " PM";
         }
         if (hr > 12) {
            ampm = " PM";
            hr = hr - 12;    // convert to conventional time
         }

         bgcolor = "#F5F5DC";               //default

         if (!event.equals("")) {
            bgcolor = ecolor;
         } else {

            if (!rest.equals("")) {
               bgcolor = rcolor;
            }
         }

         if (bgcolor.equals("Default")) {
            bgcolor = "#F5F5DC";              //default
         }

         if (player1.equals("")) {
            p1cw = "";
         } else {
            if (p91 == 1) {
               p1cw = p1cw + "9";      // 9 hole round
            }
         }
         if (player2.equals("")) {
            p2cw = "";
         } else {
            if (p92 == 1) {
               p2cw = p2cw + "9";      // 9 hole round
            }
         }
         if (player3.equals("")) {
            p3cw = "";
         } else {
            if (p93 == 1) {
               p3cw = p3cw + "9";      // 9 hole round
            }
         }
         if (player4.equals("")) {
            p4cw = "";
         } else {
            if (p94 == 1) {
               p4cw = p4cw + "9";      // 9 hole round
            }
         }
         if (player5.equals("")) {
            p5cw = "";
         } else {
            if (p95 == 1) {
               p5cw = p5cw + "9";      // 9 hole round
            }          
         }

         
         //
         //  Custom for Los Coyotes - display the Gender with the Member Number
         //
         if (club.equals( "loscoyotes" ) && disp_mnum == true) {

            if (!user1.equals( "" )) {

           //    mnum1 = verifyCustom.getLCGender(user1, mnum1, con);    // get Gender and save in mnum for display
               mnum1 = getHndcp(user1, mnum1, con);    // get Handicap # and save in mnum for display
            }
            if (!user2.equals( "" )) {

               mnum2 = getHndcp(user2, mnum2, con);
            }
            if (!user3.equals( "" )) {

               mnum3 = getHndcp(user3, mnum3, con);
            }
            if (!user4.equals( "" )) {

               mnum4 = getHndcp(user4, mnum4, con);
            }
            if (!user5.equals( "" )) {

               mnum5 = getHndcp(user5, mnum5, con);
            }
         }

         
         //
         //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
         //
         sfb = "F";       // default Front 9

         if (fb == 1) {

            sfb = "B";
         }

         if (fb == 9) {

            sfb = "O";
         }

         submit = "time:" + fb;       // create a name for the submit button to include 'time:' & fb

         out.println("<tr>");
         j++;                                       // increment the jump label index (where to jump on page)
         
      if (!excel.equals("yes")) {
         out.println("<a name=\"jump" + j + "\"></a>"); // create a jump label for 'noshow' returns

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");

         out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                 
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");

         if (fives != 0) {                                 // if 5-somes supported
            out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");
         } else {
            out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
         }
         
         if (updateAccess) {
             out.println("<input type=\"submit\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
         } else {
             out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
         }
         
         out.println("</font></td></form>");
      } else {
         out.println("<td align=\"center\"><font size=\"1\">");
         out.println(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);
         out.println("</font></td>");
      }
         
         out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(sfb);
            out.println("</font></td>");

         if (!player1.equals("")) {
            
            showbox = "mtbox.gif";            // not checked in 
            
            if (show1 == 1) {                 // if player has been checked in
               
               if (pos1 == pos_value) {       // if checked in and POS charges sent
                  
                  showbox = "xboxsent.gif";   // checked in and pos sent

               } else {
                  
                  showbox = "xbox.gif";       // just checked in
               }
            }

            if (excel.equals("yes")) {
                
                out.println("<td bgcolor=\"" + bgcolor + "\">");
                out.print("<font size=\"1\">&nbsp;");
                out.print("<img src=\"/" +rev+ "/images/" + showbox + "\" border=1>&nbsp; &nbsp; &nbsp; ");
                out.print(player1);
                out.print("</font></td>");
                
            } else {
                
                if (player1.equalsIgnoreCase("x")) {

                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println(player1);
                   out.println("</font></td>");

                } else {
                   out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println("<input type=\"hidden\" name=\"noShow\" value=\"1\">");  // player #
                   out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                   out.println("<input type=\"hidden\" name=\"show\" value=\"" + show1 + "\">");
                   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                   p1 = player1;                                       // copy name only
                   
                   if (!mnum1.equals( "" ) && disp_mnum == true) {

                      p1 = p1 + " " + mnum1;     // tack on mNum value
                   }
                   
                   if (checkinAccess) {
                       if (show1 != 1) {                                   // if player has not checked in yet
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to check player in (x).\" alt=\"submit\">");
                          out.println("&nbsp;" + p1);
                       } else {
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to set as a no-show (blank).\" alt=\"submit\">");
                          out.println("&nbsp;" + p1);
                       }
                   } else {
                          out.println("&nbsp;" + p1);
                   }
                   out.println("</font></td></form>");
                }
                
            } // end if excel
            
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p1cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
            out.println("</font></td>");

         if (!player2.equals("")) {

            showbox = "mtbox.gif";            // not checked in 
            
            if (show2 == 1) {                 // if player has been checked in
               
               if (pos2 == pos_value) {       // if checked in and POS charges sent
                  
                  showbox = "xboxsent.gif";   // checked in and pos sent

               } else {
                  
                  showbox = "xbox.gif";       // just checked in
               }
            }

            if (excel.equals("yes")) {
                
                out.println("<td bgcolor=\"" + bgcolor + "\">");
                out.print("<font size=\"1\">&nbsp;");
                out.print("<img src=\"/" +rev+ "/images/" + showbox + "\" border=1>&nbsp; &nbsp; &nbsp; ");
                out.print(player2);
                out.print("</font></td>");
                
            } else {

                if (player2.equalsIgnoreCase("x")) {

                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println(player2);
                   out.println("</font></td>");

                } else {

                   out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println("<input type=\"hidden\" name=\"noShow\" value=\"2\">");  // player #
                   out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                   out.println("<input type=\"hidden\" name=\"show\" value=\"" + show2 + "\">");
                   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                   p2 = player2;                                       // copy name only
                   
                   if (!mnum2.equals( "" ) && disp_mnum == true) {

                      p2 = p2 + " " + mnum2;     // tack on mNum value
                   }
                   
                   if (checkinAccess) {
                       if (show2 != 1) {                                   // if player has not checked in yet
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to check player in (x).\" alt=\"submit\">");
                          out.println("&nbsp;" + p2);
                       } else {
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to set as a no-show (blank).\" alt=\"submit\">");
                          out.println("&nbsp;" + p2);
                       }
                   } else {
                          out.println("&nbsp;" + p2);
                   }
                   out.println("</font></td></form>");
                }
            } // end if excel
            
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p2cw);
         } else {
            out.println("<td bgcolor=\"white\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
            out.println("</font></td>");

         if (!player3.equals("")) {

            showbox = "mtbox.gif";            // not checked in 
            
            if (show3 == 1) {                 // if player has been checked in
               
               if (pos3 == pos_value) {       // if checked in and POS charges sent
                  
                  showbox = "xboxsent.gif";   // checked in and pos sent

               } else {
                  
                  showbox = "xbox.gif";       // just checked in
               }
            }

            if (excel.equals("yes")) {
                
                out.println("<td bgcolor=\"" + bgcolor + "\">");
                out.print("<font size=\"1\">&nbsp;");
                out.print("<img src=\"/" +rev+ "/images/" + showbox + "\" border=1>&nbsp; &nbsp; &nbsp; ");
                out.print(player3);
                out.print("</font></td>");
                
            } else {

                if (player3.equalsIgnoreCase("x")) {

                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println(player3);
                   out.println("</font></td>");

                } else {

                   out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println("<input type=\"hidden\" name=\"noShow\" value=\"3\">");  // player #
                   out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                   out.println("<input type=\"hidden\" name=\"show\" value=\"" + show3 + "\">");
                   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                   p3 = player3;                                       // copy name only
                   
                   if (!mnum3.equals( "" ) && disp_mnum == true) {

                      p3 = p3 + " " + mnum3;     // tack on mNum value
                   }
                   
                   if (checkinAccess) {
                       if (show3 != 1) {                                   // if player has not checked in yet
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to check player in (x).\" alt=\"submit\">");
                          out.println("&nbsp;" + p3);
                       } else {
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to set as a no-show (blank).\" alt=\"submit\">");
                          out.println("&nbsp;" + p3);
                       }
                   } else {
                          out.println("&nbsp;" + p3);
                   }
                   out.println("</font></td></form>");
                }
                
            } // end if excel

         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p3cw);
         } else {
            out.println("<td bgcolor=\"white\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
            out.println("</font></td>");

         if (!player4.equals("")) {

            showbox = "mtbox.gif";            // not checked in 
            
            if (show4 == 1) {                 // if player has been checked in
               
               if (pos4 == pos_value) {       // if checked in and POS charges sent
                  
                  showbox = "xboxsent.gif";   // checked in and pos sent

               } else {
                  
                  showbox = "xbox.gif";       // just checked in
               }
            }

            if (excel.equals("yes")) {
                
                out.println("<td bgcolor=\"" + bgcolor + "\">");
                out.print("<font size=\"1\">&nbsp;");
                out.print("<img src=\"/" +rev+ "/images/" + showbox + "\" border=1>&nbsp; &nbsp; &nbsp; ");
                out.print(player4);
                out.print("</font></td>");
                
            } else {

                if (player4.equalsIgnoreCase("x")) {

                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println(player4);
                   out.println("</font></td>");

                } else {

                   out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                   out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                   out.println("<font size=\"1\">");
                   out.println("<input type=\"hidden\" name=\"noShow\" value=\"4\">");  // player #
                   out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                   out.println("<input type=\"hidden\" name=\"show\" value=\"" + show4 + "\">");
                   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                   p4 = player4;                                       // copy name only
                   
                   if (!mnum4.equals( "" ) && disp_mnum == true) {

                      p4 = p4 + " " + mnum4;     // tack on mNum value
                   }
                   
                   if (checkinAccess) {
                       if (show4 != 1) {                                   // if player has not checked in yet
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to check player in (x).\" alt=\"submit\">");
                          out.println("&nbsp;" + p4);
                       } else {
                          out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to set as a no-show (blank).\" alt=\"submit\">");
                          out.println("&nbsp;" + p4);
                       }
                   } else {
                          out.println("&nbsp;" + p4);
                   }
                   out.println("</font></td></form>");
                }
                
            } // end if excel
            
         } else {
            out.println("<td bgcolor=\"" + bgcolor + "\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
            out.println("</font></td>");
         }

         if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println(p4cw);
         } else {
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"1\">");
            out.println("&nbsp;");
         }
         out.println("</font></td>");

         if (fives != 0) {

            if (!player5.equals("")) {

               showbox = "mtbox.gif";            // not checked in 

               if (show5 == 1) {                 // if player has been checked in

                  if (pos5 == pos_value) {       // if checked in and POS charges sent

                     showbox = "xboxsent.gif";   // checked in and pos sent

                  } else {

                     showbox = "xbox.gif";       // just checked in
                  }
               }

                if (excel.equals("yes")) {

                    out.println("<td bgcolor=\"" + bgcolor + "\">");
                    out.print("<font size=\"1\">&nbsp;");
                    out.print("<img src=\"/" +rev+ "/images/" + showbox + "\" border=1>&nbsp; &nbsp; &nbsp; ");
                    out.print(player5);
                    out.print("</font></td>");

                } else {

                   if (player5.equalsIgnoreCase("x")) {

                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                      out.println("<font size=\"1\">");
                      out.println(player5);
                      out.println("</font></td>");

                   } else {

                      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                      out.println("<font size=\"1\">");
                      out.println("<input type=\"hidden\" name=\"noShow\" value=\"5\">");  // player #
                      out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                      out.println("<input type=\"hidden\" name=\"show\" value=\"" + show5 + "\">");
                      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                      p5 = player5;                                       // copy name only
                   
                      if (!mnum5.equals( "" ) && disp_mnum == true) {

                         p5 = p5 + " " + mnum5;     // tack on mNum value
                      }
                      
                      if (checkinAccess) {
                          if (show5 != 1) {                                   // if player has not checked in yet
                             out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to check player in (x).\" alt=\"submit\">");
                             out.println("&nbsp;" + p5);
                          } else {
                             out.println("&nbsp;<input type=\"image\" src=\"/" +rev+ "/images/" + showbox + "\" border=\"1\" name=\"sub\" value=\"" + p1 + "\" title=\"Click here to set as a no-show (blank).\" alt=\"submit\">");
                             out.println("&nbsp;" + p5);
                          }
                      } else {
                          out.println("&nbsp;" + p5);
                      }
                      out.println("</font></td></form>");
                   }
                   
                } // end excel
                
            } else {
               out.println("<td bgcolor=\"" + bgcolor + "\">");
               out.println("<font size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            }

            if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(p5cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");
            
         } // end if fives

         //
         //  Next column for 'check-in all' box (add box if any players in slot)
         //
         if (!excel.equals("yes")) {
             
             if (checkinAccess) {
                 out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\">");
                 out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                 out.println("<font size=\"1\">");
                 if (((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) ||
                     ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) ||
                     ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) ||
                     ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) ||
                     ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" )))) {

                    out.println("<input type=\"hidden\" name=\"checkAll\" value=\"yes\">");
                    out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                    out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                    out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
                    out.println("<input type=\"image\" src=\"/" +rev+ "/images/checkall.gif\" border=\"1\" name=\"subAll\" title=\"Click here to check all players in.\" alt=\"check all\">");
                 }
                 out.println("</font></td></form>");
             }

             //  col for notes
             out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_oldsheets\" target=\"_blank\">");
             out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
             out.println("<font size=\"2\">");
             if (!notes.equals("")) {

                out.println("<input type=\"hidden\" name=\"displayNotes\" value=\"yes\">");
                out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                out.println("<input type=\"image\" src=\"/" +rev+ "/images/notes.jpg\" border=\"0\" name=\"showNotes\" title=\"Click here to view notes.\">");
             }
             out.println("</font></td></form>");         // end of Notes col

             //
             //  Last column for 'TeeTime History' box
             //
             out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
             out.println("<a href=\"javascript: void(0)\" onclick=\"openHistoryWindow('" + calDate + "','" + course + "', " + time + "," + fb + "," + teepast_id + ");return false;\"><img src=\"/" +rev+ "/images/history.gif\" width=\"12\" height=\"13\" border=\"0\" title=\"Click here to view tee time history.\"></a>");
             out.println("</td></tr>");

             /*
             out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_teetime_history\" target=\"_tthistory\">");
             out.println("<font size=\"2\">");

             out.println("<input type=\"hidden\" name=\"history\" value=\"yes\">");
             out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
             out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
             out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
             out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
             out.println("<input type=\"image\" src=\"/" +rev+ "/images/history.gif\" width=\"12\" height=\"13\" border=\"0\" name=\"showHistory\" title=\"Click here to view tee time history.\">");
             out.println("</font></td></form>");         // end of the teetime history col
             */   
             
         } else {
             
             out.println("</tr>");
         
         }
        
      }  // end of while

      pstmt.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</BODY></HTML>");
      out.close();
      return;
   }

   //
   //  End of HTML page
   //
   out.println("</td></tr></table>");                           // end of whole page
   out.println("</center></body></html>");
   out.close();

 }  // end of doPost


 // *********************************************************
 //  Edit a tee time
 // *********************************************************

 private void editTime(long date, String course, int jump, HttpServletRequest req, PrintWriter out, HttpSession session, Connection con) {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;

   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb' - a request from Proshop_oldsheets above (edit a tee time)
   //      'cancel' - a 'go back' request
   //      'submit' - a tee time change request
   //      'remove' - a 'cancel tee time' request
   //      'letter' - a request to list member names
   //      'return' - a return to the old tee sheet from verify (from a skip)
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, con);       // process cancel request
      return;

   }

   if ((req.getParameter("submit") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session);                 // process reservation requests
      return;
   }

   //int count = 0;
   //int in_use = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int fb = 0;
   //int visits = 0;
   int x = 0;
   int xCount = 0;
   int i = 0;
   //int nowc = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int assign = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;

   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
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
   String mem1 = "";          // Name of Member associated with a guest player
   String mem2 = "";
   String mem3 = "";
   String mem4 = "";
   String mem5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   String sdate = "";
   String stime = "";
   String ampm = "";
   String sfb = "";
   String notes = "";
   String orig_by = "";
   String orig_name = "";
   String day_name = "";
   String conf = "";

   String club = (String)session.getAttribute("club");

   //
   //  parm blocks to hold the club & course parameters
   //
   parmClub parm = new parmClub(0, con);
   parmCourse parmc = new parmCourse();       

   //
   // Get all the parameters entered
   //
   String calDate = req.getParameter("calDate");
   String p5 = req.getParameter("p5");                //  5-somes supported

   stime = req.getParameter("time");
   sfb = req.getParameter("fb");
        
   if (req.getParameter("assign") != null) {     // if this is to assign members to guests

      assign = 1;                                // indicate 'assign members to guest' (Unaccompanied Guests)
   }

   //
   //  Convert the values from string to int
   //
   try {
      fb = Integer.parseInt(sfb);
      time = Integer.parseInt(stime);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  isolate yy, mm, dd
   //
   yy = date / 10000;
   temp = yy * 10000;
   mm = date - temp;
   temp = mm / 100;
   temp = temp * 100;
   dd = mm - temp;
   mm = mm / 100;

   //
   //  create a time string for display
   //
   hr = time / 100;
   min = time - (hr * 100);

   ampm = " AM";

   if (hr > 11) {

      ampm = " PM";

      if (hr > 12) {

         hr = hr - 12;
      }
   }

   if (min < 10) {
      stime = hr + ":0" + min + ampm;
   } else {
      stime = hr + ":" + min + ampm;
   }

   if ((req.getParameter("letter") != null) || (req.getParameter("return") != null)) { // if user clicked on a name letter

      player1 = req.getParameter("player1");     // get the player info from the player table
      player2 = req.getParameter("player2");
      player3 = req.getParameter("player3");
      player4 = req.getParameter("player4");
      player5 = req.getParameter("player5");
      p1cw = req.getParameter("p1cw");
      p2cw = req.getParameter("p2cw");
      p3cw = req.getParameter("p3cw");
      p4cw = req.getParameter("p4cw");
      p5cw = req.getParameter("p5cw");
      notes = req.getParameter("notes");
      orig_by = req.getParameter("orig_by");
      conf = req.getParameter("conf");
      guest_id1 = Integer.parseInt(req.getParameter("guest_id1"));
      guest_id2 = Integer.parseInt(req.getParameter("guest_id2"));
      guest_id3 = Integer.parseInt(req.getParameter("guest_id3"));
      guest_id4 = Integer.parseInt(req.getParameter("guest_id4"));
      guest_id5 = Integer.parseInt(req.getParameter("guest_id5"));

      String p9s = "";

      if (req.getParameter("p91") != null) {
         p9s = req.getParameter("p91");
         p91 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p92") != null) {
         p9s = req.getParameter("p92");
         p92 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p93") != null) {
         p9s = req.getParameter("p93");
         p93 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p94") != null) {
         p9s = req.getParameter("p94");
         p94 = Integer.parseInt(p9s);
      }
      if (req.getParameter("p95") != null) {
         p9s = req.getParameter("p95");
         p95 = Integer.parseInt(p9s);
      }

      if (req.getParameter("mem1") != null) {
         mem1 = req.getParameter("mem1");
      }
      if (req.getParameter("mem2") != null) {
         mem2 = req.getParameter("mem2");
      }
      if (req.getParameter("mem3") != null) {
         mem3 = req.getParameter("mem3");
      }
      if (req.getParameter("mem4") != null) {
         mem4 = req.getParameter("mem4");
      }
      if (req.getParameter("mem5") != null) {
         mem5 = req.getParameter("mem5");
      }

   } else {

      try {

         PreparedStatement pstmt2s = con.prepareStatement (
            "SELECT * " +
            "FROM teepast2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

         pstmt2s.clearParameters();        // clear the parms
         pstmt2s.setLong(1, date);
         pstmt2s.setInt(2, time);
         pstmt2s.setInt(3, fb);
         pstmt2s.setString(4, course);

         rs = pstmt2s.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            day_name = rs.getString("day");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            userg1 = rs.getString("userg1");
            userg2 = rs.getString("userg2");
            userg3 = rs.getString("userg3");
            userg4 = rs.getString("userg4");
            userg5 = rs.getString("userg5");
            orig_by = rs.getString("orig_by");
            conf = rs.getString("conf");
            notes = rs.getString("notes");
            p91 = rs.getInt("p91");
            p92 = rs.getInt("p92");
            p93 = rs.getInt("p93");
            p94 = rs.getInt("p94");
            p95 = rs.getInt("p95");
            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
         }
         pstmt2s.close();

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, please contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

   }              // end of 'letter' or 'return' if

   //
   //  Ensure that there are no null player fields
   //
   if (player1 == null ) {
      player1 = "";
   }
   if (player2 == null ) {
      player2 = "";
   }
   if (player3 == null ) {
      player3 = "";
   }
   if (player4 == null ) {
      player4 = "";
   }
   if (player5 == null ) {
      player5 = "";
   }
   if (p1cw == null ) {
      p1cw = "";
   }
   if (p2cw == null ) {
      p2cw = "";
   }
   if (p3cw == null ) {
      p3cw = "";
   }
   if (p4cw == null ) {
      p4cw = "";
   }
   if (p5cw == null ) {
      p5cw = "";
   }
   if (notes == null ) {
      notes = "";
   }
   if (orig_by == null ) {
      orig_by = "";
   }
   if (conf == null ) {
      conf = "";
   }

   //
   //  Get the walk/cart options available and find the originators name
   //
   try {

      getParms.getTmodes(con, parmc, course);

      if (!orig_by.equals( "" )) {         // if originator exists (username of person originating tee time)

         if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            orig_name = orig_by;        // if proshop, just use the username

         } else {

            //
            //  Check member table and hotel table for match
            //
            orig_name = "";        // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, orig_by);

            rs = pstmtc.executeQuery();

            if (rs.next()) {

               // Get the member's full name.......

               StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

               String mi = rs.getString(3);                                // middle initial
               if (!mi.equals( "" )) {
                  mem_name.append(" ");
                  mem_name.append(mi);
               }
               mem_name.append(" " + rs.getString(1));                     // last name

               orig_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            if (orig_name.equals( "" )) {       // if match not found - check hotel user table

               pstmtc = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username= ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, orig_by);

               rs = pstmtc.executeQuery();

               if (rs.next()) {

                  // Get the member's full name.......

                  StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                  String mi = rs.getString(3);                                // middle initial
                  if (!mi.equals( "" )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  mem_name.append(" " + rs.getString(1));                     // last name

                  orig_name = mem_name.toString();                          // convert to one string
               }
               pstmtc.close();
            }
         }
      }
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Build the HTML page to prompt user for names
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Tee Slot Page"));

   if (assign == 0) {   // if normal tee time
       
       //  Add script code to allow modal windows to be used
       out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->" +
               "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>" +
               "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>" +
               "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function resizeIFrame(divHeight, iframeName) {");
       out.println("document.getElementById(iframeName).style.height = divHeight;");
       out.println("}");
       out.println("// -->");
       out.println("</script>");

      //
      //*******************************************************************
      //  Erase player name (erase button selected next to player's name)
      //
      //    Remove the player's name and shift any other names up starting at player1
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erasename(pos1) {");

      out.println("document.playerform[pos1].value = '';");           // clear the player field

      out.println("var pos2 = pos1.replace('player', 'guest_id');");
      out.println("document.playerform[pos2].value = '0';");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("<script language='JavaScript'>");             // Move Notes into textarea
      out.println("<!--");
      out.println("function movenotes() {");
      out.println("var oldnotes = document.playerform.oldnotes.value;");
      out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                               // deliminator is a colon
      out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");
      out.println("var wc = array[1];");
      out.println("skip = 0;");

      out.println("var f = document.forms['playerform'];");

      out.println("var player1 = f.player1.value;");
      out.println("var player2 = f.player2.value;");
      out.println("var player3 = f.player3.value;");
      out.println("var player4 = f.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = f.player5.value;");
      }

      out.println("if (( name != 'x') && ( name != 'X')) {");


      if (p5.equals( "Yes" )) {
         out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {");
      } else {
         out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4)) {");
      }
            out.println("skip = 1;");
         out.println("}");
      out.println("}");

      out.println("if (skip == 0) {");

         out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("f.player1.value = name;");
            out.println("f.guest_id1.value = '0';");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("f.p1cw.value = wc;");
            out.println("}");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
            out.println("f.player2.value = name;");
            out.println("f.guest_id2.value = '0';");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("f.p2cw.value = wc;");
            out.println("}");
         out.println("} else {");

         out.println("if (player3 == '') {");                    // if player3 is empty
            out.println("f.player3.value = name;");
            out.println("f.guest_id3.value = '0';");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("f.p3cw.value = wc;");
            out.println("}");
         out.println("} else {");

         out.println("if (player4 == '') {");                    // if player4 is empty
            out.println("f.player4.value = name;");
            out.println("f.guest_id4.value = '0';");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("f.p4cw.value = wc;");
            out.println("}");

      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
            out.println("f.player5.value = name;");
            out.println("f.guest_id5.value = '0';");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("f.p5cw.value = wc;");
            out.println("}");
         out.println("}");
       }

         out.println("}");
         out.println("}");
         out.println("}");
         out.println("}");

      out.println("}");                  // end of dup name chack

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script


      //
      //*******************************************************************
      //  Move a Guest Name or 'X' into the tee slot
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move Guest Name script
      out.println("<!--");

      // Global vars used for guest tracking
      out.println("var guestid_slot;");
      out.println("var player_slot;");

      out.println("function moveguest(namewc) {");

      //out.println("var name = namewc;");

      out.println("var f = document.forms['playerform'];");

      out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
      out.println("var name = array[0];");
      out.println("var use_guestdb = array[1]");

      out.println("var player1 = f.player1.value;");
      out.println("var player2 = f.player2.value;");
      out.println("var player3 = f.player3.value;");
      out.println("var player4 = f.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = f.player5.value;");
      }

      //  set spc to ' ' if name to move isn't an 'X'
      out.println("var spc = '';");
      out.println("if (name != 'X' && name != 'x') {");
      out.println("   spc = ' ';");
      out.println("}");
      
      // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
      out.println("if (use_guestdb == 1 && (player1 == '' || player2 == '' || player3 == '' || player4 == ''" + (p5.equals("Yes") ? " || player5 == ''" : "") + ")) {");
      out.println("  loadmodal(0);");
      out.println("}");

         out.println("if (player1 == '') {");                    // if player1 is empty
             out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player1;");
                out.println("guestid_slot = f.guest_id1;");
                out.println("f.player1.value = name + spc;");
             out.println("} else {");
                out.println("f.player1.value = name;");
             out.println("}");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
             out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player2;");
                out.println("guestid_slot = f.guest_id2;");
                out.println("f.player2.value = name + spc;");
             out.println("} else {");
                out.println("f.player2.value = name;");
             out.println("}");
         out.println("} else {");

         out.println("if (player3 == '') {");                    // if player3 is empty
             out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player3;");
                out.println("guestid_slot = f.guest_id3;");
                out.println("f.player3.value = name + spc;");
             out.println("} else {");
                out.println("f.player3.value = name;");
             out.println("}");
         out.println("} else {");

         out.println("if (player4 == '') {");                    // if player4 is empty
             out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player4;");
                out.println("guestid_slot = f.guest_id4;");
                out.println("f.player4.value = name + spc;");
             out.println("} else {");
                out.println("f.player4.value = name;");
             out.println("}");

      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
             out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player5;");
                out.println("guestid_slot = f.guest_id5;");
                out.println("f.player5.value = name + spc;");
             out.println("} else {");
                out.println("f.player5.value = name;");
             out.println("}");
         out.println("}");
       }

         out.println("}");
         out.println("}");
         out.println("}");
         out.println("}");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script

   } else {   // this is a prompt to assign a member to an Unaccompanied Guest

      //
      //*******************************************************************
      //  Erase Associated Member name (for Unaccompanied Guests Prompt)
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
      out.println("<!--");

      out.println("function erasemem(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the member name field      
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the Associated Member slot for Unaccomp. Guests
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("var f = document.forms['playerform'];");

      out.println("del = ':';");                                // deliminator is a colon
      out.println("array = namewc.split(del);");                // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");                      // just get the name

      out.println("var mem1 = f.mem1.value;");
      out.println("var mem2 = f.mem2.value;");
      out.println("var mem3 = f.mem3.value;");
      out.println("var mem4 = f.mem4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var mem5 = f.mem5.value;");
      }

      out.println("if (( name != 'x') && ( name != 'X')) {");

         out.println("if (mem1 == '') {");                    // if mem1 is empty
            out.println("f.mem1.value = name;");
         out.println("} else {");

         out.println("if (mem2 == '') {");                    // if mem2 is empty
            out.println("f.mem2.value = name;");
         out.println("} else {");

         out.println("if (mem3 == '') {");                    // if mem3 is empty
            out.println("f.mem3.value = name;");
         out.println("} else {");

         out.println("if (mem4 == '') {");                    // if mem4 is empty
            out.println("f.mem4.value = name;");

         if (p5.equals( "Yes" )) {
            out.println("} else {");
            out.println("if (mem5 == '') {");                    // if mem5 is empty
               out.println("f.mem5.value = name;");
            out.println("}");
          }
         out.println("}");
         out.println("}");
         out.println("}");
         out.println("}");
      out.println("}");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script
   }

   out.println("</HEAD>");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"2\">");
         if (assign == 0) {
            out.println("Make any desired changes and click on Submit to process them.");
         } else {
            out.println("<b>Assign a Member for each Unaccompanied Guest.<br>");
            out.println("You can specify a member more than once.</b>");
         }
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\"><br><br>");
      out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Tee Time:&nbsp;&nbsp;<b>" + stime + "</b>");
        if (!course.equals( "" )) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
        }
        if (!orig_name.equals( "" )) {         // if tee time already exists
           out.println("<br><br>");
           out.println("Tee Time originated by: <b>" + orig_name + "</b>");
        }
        out.println("</font>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below
         out.println("<tr>");
         out.println("<td align=\"center\">");         // col for Instructions and Go Back button

         out.println("<font size=\"1\">");
         if (assign == 0) {      // if normal tee time
            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_instruct.htm', 'newwindow', config='Height=460, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         } else {
            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_unacomp.htm', 'newwindow', config='Height=380, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         }
         out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
         out.println("<br>Click for Help</a>");

         out.println("</font><font size=\"2\">");
         out.println("<br><br><br>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" name=\"can\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=" + calDate + ">");
         out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
         out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
         out.println("Return<br>w/o Changes:<br>");
         out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
         out.println("</font></td>");

         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" name=\"playerform\">");
         out.println("<td align=\"center\" valign=\"top\">");

         // Print hidden guest_id inputs
         out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

            if (assign == 0) {      // if normal tee time

               out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"370\">");  // table for player selection
               out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<b>Add or Remove Players</b>");
               out.println("</font></td></tr>");

               out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");

               out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1')\" style=\"cursor:hand\">");
               out.println("1:&nbsp;&nbsp;<input type=\"text\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\">");
                 if (!p1cw.equals( "" )) {
                    out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p91 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p91\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p91\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2')\" style=\"cursor:hand\">");
               out.println("2:&nbsp;&nbsp;<input type=\"text\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\">");
                 if (!p2cw.equals( "" )) {
                    out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p92 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p92\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p92\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player3')\" style=\"cursor:hand\">");
               out.println("3:&nbsp;&nbsp;<input type=\"text\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\">");
                 if (!p3cw.equals( "" )) {
                    out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p93 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p93\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p93\" value=\"1\">");
                 }

               out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player4')\" style=\"cursor:hand\">");
               out.println("4:&nbsp;&nbsp;<input type=\"text\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\">");
                 if (!p4cw.equals( "" )) {
                    out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p94 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p94\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p94\" value=\"1\">");
                 }

               if (p5.equals( "Yes" )) {

                 out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player5')\" style=\"cursor:hand\">");
                 out.println("5:&nbsp;&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
                 if (!p5cw.equals( "" )) {
                    out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                 }
                 out.println("</select>");
                 if (p95 == 1) {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p95\" value=\"1\">");
                 } else {
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p95\" value=\"1\">");
                 }
               } else {

                 out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                 out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
               }

               //
               //   Notes
               //
               //   Script will put any existing notes in the textarea (value= doesn't work)
               //
               out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

               out.println("<br><br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
               out.println("Notes:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"28\" rows=\"2\">");
               out.println("</textarea>");

            } else {     // assign = 1


             // Print hidden guest_id inputs
             out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
             out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
             out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
             out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
             out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

               out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");  // table for member selection
               out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<b>Select a Member for Each Guest</b>");
               out.println("</font></td></tr>");
               out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");

               //
               //  Prompt the user for member names to assign to Unaccompanied Guests
               //
               out.println("<p align=\"left\">");
               out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Associated Member");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("Guests</b><br>");

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem1')\" style=\"cursor:hand\">");
               out.println("1:&nbsp;&nbsp;<input type=\"text\" name=\"mem1\" value=\"" + mem1 + "\" size=\"20\" maxlength=\"30\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player1 + "&nbsp;&nbsp;&nbsp;<br>");

               if (!player2.equals( "" )) {

                  out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem2')\" style=\"cursor:hand\">");
                  out.println("2:&nbsp;&nbsp;<input type=\"text\" name=\"mem2\" value=\"" + mem2 + "\" size=\"20\" maxlength=\"30\">");
                  out.println("&nbsp;&nbsp;&nbsp;" + player2 + "&nbsp;&nbsp;&nbsp;<br>");

               } else {

                 out.println("<input type=\"hidden\" name=\"mem2\" value=\"\">");
               }
               if (!player3.equals( "" )) {

                  out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem3')\" style=\"cursor:hand\">");
                  out.println("3:&nbsp;&nbsp;<input type=\"text\" name=\"mem3\" value=\"" + mem3 + "\" size=\"20\" maxlength=\"30\">");
                  out.println("&nbsp;&nbsp;&nbsp;" + player3 + "&nbsp;&nbsp;&nbsp;<br>");

               } else {

                 out.println("<input type=\"hidden\" name=\"mem3\" value=\"\">");
               }
               if (!player4.equals( "" )) {

                  out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem4')\" style=\"cursor:hand\">");
                  out.println("4:&nbsp;&nbsp;<input type=\"text\" name=\"mem4\" value=\"" + mem4 + "\" size=\"20\" maxlength=\"30\">");
                  out.println("&nbsp;&nbsp;&nbsp;" + player4 + "&nbsp;&nbsp;&nbsp;<br>");

               } else {

                 out.println("<input type=\"hidden\" name=\"mem4\" value=\"\">");
               }
               if (!player5.equals( "" )) {

                  out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem5')\" style=\"cursor:hand\">");
                  out.println("5:&nbsp;&nbsp;<input type=\"text\" name=\"mem5\" value=\"" + mem5 + "\" size=\"20\" maxlength=\"30\">");
                  out.println("&nbsp;&nbsp;&nbsp;" + player5 + "&nbsp;&nbsp;&nbsp;<br>");

               } else {

                 out.println("<input type=\"hidden\" name=\"mem5\" value=\"\">");
               }
               out.println("</p>");
            }    // end of IF assign

            out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + "></input>");
            out.println("<input type=\"hidden\" name=\"sdate\" value=" + sdate + "></input>");
            out.println("<input type=\"hidden\" name=\"calDate\" value=" + calDate + "></input>");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + "></input>");
            out.println("<input type=\"hidden\" name=\"time\" value=" + time + "></input>");
            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + "></input>");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\"></input>");
            out.println("<input type=\"hidden\" name=\"mm\" value=" + mm + "></input>");
            out.println("<input type=\"hidden\" name=\"yy\" value=" + yy + "></input>");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=" + p5 + ">");
            out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
            if (assign == 0) {
               out.println("<br><br><font size=\"1\">");
               for (i=0; i<16; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("</font><br>");

            } else {
               out.println("<input type=\"hidden\" name=\"skip\" value=\"10\">");      // skip right to assign
               out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            }
            out.println("<input type=submit value=\"Submit\" name=\"submit\"></input><br>");
            if (assign == 0 && !club.equals("tpccraigranch")) {
               out.println("<input type=submit value=\"Cancel Tee Time\" name=\"remove\"></input>&nbsp;&nbsp;&nbsp;");
            }
            out.println("</font></td></tr>");
            out.println("</table>");
         out.println("</td>");
         out.println("<td valign=\"top\">");

   if (assign == 0 || req.getParameter("letter") != null) {  // if normal tee time OR user clicked on a name letter

         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\" valign=\"top\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

      // ********************************************************************************
      //   If we got control from user clicking on a letter in the Member List,
      //   then we must build the name list.
      // ********************************************************************************
      if (req.getParameter("letter") != null) {     // if user clicked on a name letter

         String letter = req.getParameter("letter");

         if (letter.equals( "List All" )) {
            letter = "%";
         } else {
            letter = letter + "%";
         }

         String first = "";
         String mid = "";
         String last = "";
         String name = "";
         String wname = "";
         String dname = "";
         String wc = "";

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi, wc FROM member2b " +
                     "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"8\" name=\"bname\" onClick=\"movename(this.form.bname.value)\" style=\"cursor:hand\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);
               wc = rs.getString(4);           // walk/cart preference

               i = 0;
               loopi3:
               while (i < 16) {             // make sure wc is supported

                  if (parmc.tmodea[i].equals( wc )) {

                     break loopi3;
                  }
                  i++;
               }
               if (i > 15) {       // if we went all the way without a match

                  wc = parmc.tmodea[0];    // use default option
               }
               i = 0;

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               wname = name + ":" + wc;              // combine name:wc for script

               out.println("<option value=\"" + wname + "\">" + dname + "</option>");
            }

            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }

      }        // end of IF Letter

      if (assign == 0) {          // if normal tee time

         //
         //  add a table row for 'guest' and 'x'
         //
         //     Check the club db table for X and Guest parms specified by proshop
         //
         try {
            getClub.getParms(con, parm);        // get the club parms

            x = parm.x;

            //
            //  first we must count how many fields there will be
            //
            xCount = 0;
            if (x != 0) {

               xCount = 1;
            }
            for (i = 0; i < parm.MAX_Guests; i++) {

               if (!parm.guest[i].equals( "" )) {

                  xCount++;
               }
            }
            i = 0;
            if (xCount != 0) {

               if (xCount < 2) {

                  xCount = 2;             // set size to at least 2
               }
               if (xCount > 10) {

                  xCount = 10;             // set size to no more than 10 showing at once (it will scroll)
               }
               out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
               out.println("<b>**</b> Add guests immediately <b>after</b> host member.<br>");
               out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

               out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\" style=\"cursor:hand\">");
               if (x != 0) {
                  out.println("<option value=\"X\">X</option>");
               }
               for (i = 0; i < parm.MAX_Guests; i++) {

                  if (!parm.guest[i].equals( "" )) {   // if guest name

                     out.println("<option value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\">" + parm.guest[i] + "</option>");
                  }
               }
               out.println("</select>");
               out.println("</font></td></tr></table>");      // end of this table and column
               out.println("</td>");

            } else {

               out.println("</table></td>");      // end the table and column if none specified
            }

         }
         catch (Exception exc) {             // SQL Error - ignore guest and x

            out.println("</table></td>");
         }

      } else {  // assign = yes

         out.println("</table></td>");
      }      // end of IF assign

       out.println("</td>");                                      // end of this column
       out.println("<td valign=\"top\">");                        // add column for member list table

   }  // end of IF assign=0 OR letter

      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
         out.println("<tr bgcolor=\"#336633\">");
            out.println("<td colspan=\"6\" align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Member List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
            out.println("<td colspan=\"6\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Last name begins with:");
               out.println("</font></td>");
         out.println("</tr><tr>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
         out.println("</tr><tr>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
         out.println("</tr><tr>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
           out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
         out.println("</tr><tr>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
         out.println("</tr><tr>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
            out.println("<td align=\"center\">");
               out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
            out.println("<td align=\"center\" colspan=\"4\">");
               out.println("<input type=\"submit\" value=\"List All\" name=\"letter\"></td>");
         out.println("</tr>");

         out.println("</table>");
      out.println("</td>");
      out.println("</form>");
      out.println("</tr>");
    out.println("</table>");      // end of large table containg 3 smaller tables

   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table

   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></center></body></html>");
   out.close();

 }                   // end of editTime


 // *********************************************************
 //  Process cancel request - 'Go Back'
 // *********************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, Connection con) {

   int count = 0;
   int time  = 0;
   int fb  = 0;
   long date  = 0;


   //
   // Get all the parameters entered
   //
   String calDate = req.getParameter("calDate");           //  date of tee time requested (mm/dd/yyyy)
   String course = req.getParameter("course");             //  name of course
   String jump = req.getParameter("jump");                 //  jump index

   //
   //  This return will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Proshop Tee Slot Page</Title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_oldsheets?calDate=" + calDate + "&course=" + course + "&post=yes\">");
   out.println("</HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
   out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
   out.println("<BR><BR>");

   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=" + calDate + ">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }                   // end of cancel


 // *********************************************************
 //  Process reservation request from Proshop_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;


   //
   //  Get this session's attributes
   //
   String user = "";
   String club = "";
   String posType = "";
   user = (String)session.getAttribute("user");
   club = (String)session.getAttribute("club");
   posType = (String)session.getAttribute("posType");

   int count = 0;
   //int reject = 0;
   //int time2 = 0;
   //int fb2 = 0;
   //int t_fb = 0;
   //int xhrs = 0;
   //int xError = 0;
   //int xUsed = 0;
   //int i = 0;
   int mm = 0;
   int yy = 0;
   int dd = 0;
   int fb = 0;
   int time = 0;
   //int mtimes = 0;
   //int year = 0;
   //int month = 0;
   //int dayNum = 0;
   //int ind = 0;
   int temp = 0;
   int gi = 0;
   int proMod = 0;
   int skip = 0;

   long date = 0;
   //long dateStart = 0;
   //long dateEnd = 0;

   String player = "";
   String err_name = "";
   //String sfb2 = "";
   //String notes2 = "";
   //String period = "";
   //String mperiod = "";
   //String course2 = "";
   String memberName = "";
   //String mship = "";
   //String mtype = "";
   String skips = "";
   String p9s = "";
   //String gplayer = "";

   String sponsored = "Spons";

   //boolean hit = false;
   //boolean hit2 = false;
   //boolean check = false;
   //boolean guestError = false;
   boolean error = false;

   boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

   //int [] mtimesA = new int [8];          // array to hold the mship max # of rounds value
   //String [] periodA = new String [8];    // array to hold the mship periods (week, month, year)

   //
   //  Arrays to hold member & guest names to tie guests to members
   //
   String [] memA = new String [5];     // members
   String [] usergA = new String [5];   // guests' associated member (username)

   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String calDate = req.getParameter("calDate");      //  date of tee time requested (mm/dd/yyyy)
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time

   slotParms.p5 = req.getParameter("p5");                //  5-somes supported for this slot
   slotParms.course = req.getParameter("course");        //  name of course
   slotParms.player1 = req.getParameter("player1");
   slotParms.player2 = req.getParameter("player2");
   slotParms.player3 = req.getParameter("player3");
   slotParms.player4 = req.getParameter("player4");
   slotParms.player5 = req.getParameter("player5");
   slotParms.p1cw = req.getParameter("p1cw");
   slotParms.p2cw = req.getParameter("p2cw");
   slotParms.p3cw = req.getParameter("p3cw");
   slotParms.p4cw = req.getParameter("p4cw");
   slotParms.p5cw = req.getParameter("p5cw");
   slotParms.guest_id1 = Integer.parseInt(req.getParameter("guest_id1"));
   slotParms.guest_id2 = Integer.parseInt(req.getParameter("guest_id2"));
   slotParms.guest_id3 = Integer.parseInt(req.getParameter("guest_id3"));
   slotParms.guest_id4 = Integer.parseInt(req.getParameter("guest_id4"));
   slotParms.guest_id5 = Integer.parseInt(req.getParameter("guest_id5"));
   slotParms.day = req.getParameter("day");                      // name of day
   slotParms.sfb = req.getParameter("fb");                       // Front/Back indicator
   slotParms.notes = req.getParameter("notes");                  // Proshop Notes
   slotParms.jump = req.getParameter("jump");             // jump index for _sheet
   slotParms.conf = req.getParameter("conf");             // confirmation # (or Id) for Hotels

   //
   //  set 9-hole options
   //
   slotParms.p91 = 0;                       // init to 18 holes
   slotParms.p92 = 0;
   slotParms.p93 = 0;
   slotParms.p94 = 0;
   slotParms.p95 = 0;

   if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      slotParms.p91 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      slotParms.p92 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      slotParms.p93 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      slotParms.p94 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      slotParms.p95 = Integer.parseInt(p9s);
   }

   //
   //  Get member names for Unaccompanied Guests, if provided
   //
   if (req.getParameter("mem1") != null) {
      slotParms.mem1 = req.getParameter("mem1");
   }
   if (req.getParameter("mem2") != null) {
      slotParms.mem2 = req.getParameter("mem2");
   }
   if (req.getParameter("mem3") != null) {
      slotParms.mem3 = req.getParameter("mem3");
   }
   if (req.getParameter("mem4") != null) {
      slotParms.mem4 = req.getParameter("mem4");
   }
   if (req.getParameter("mem5") != null) {
      slotParms.mem5 = req.getParameter("mem5");
   }

   //
   //  Get skip parm if provided
   //
   if (req.getParameter("skip") != null) {

      skips = req.getParameter("skip");
      skip = Integer.parseInt(skips);
   }

   //
   //  Ensure that there are no null player fields
   //
   if (slotParms.player1 == null ) {
      slotParms.player1 = "";
   }
   if (slotParms.player2 == null ) {
      slotParms.player2 = "";
   }
   if (slotParms.player3 == null ) {
      slotParms.player3 = "";
   }
   if (slotParms.player4 == null ) {
      slotParms.player4 = "";
   }
   if (slotParms.player5 == null ) {
      slotParms.player5 = "";
   }
   if (slotParms.p1cw == null ) {
      slotParms.p1cw = "";
   }
   if (slotParms.p2cw == null ) {
      slotParms.p2cw = "";
   }
   if (slotParms.p3cw == null ) {
      slotParms.p3cw = "";
   }
   if (slotParms.p4cw == null ) {
      slotParms.p4cw = "";
   }
   if (slotParms.p5cw == null ) {
      slotParms.p5cw = "";
   }

   //
   //  Convert date & time from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(slotParms.sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;

   if (!slotParms.notes.equals( "" )) {

      notesL = slotParms.notes.length();       // get length of notes
   }

   //
   //   use yy and mm and date to determine dd (from tee time's date)
   //
   temp = yy * 10000;
   temp = temp + (mm * 100);
   dd = (int) date - temp;            // get day of month from date

   //
   //  put parms in Parameter Object for portability
   //
   slotParms.date = date;
   slotParms.time = time;
   slotParms.mm = mm;
   slotParms.yy = yy;
   slotParms.dd = dd;
   slotParms.fb = fb;
   slotParms.club = club;    // name of club

   //
   //  get old info
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, username1, username2, username3, " +
         "username4, p1cw, p2cw, p3cw, p4cw, " +
         "show1, show2, show3, show4, player5, username5, p5cw, show5, proMod, " +
         "userg1, userg2, userg3, userg4, userg5, orig_by," +
         "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
         "FROM teepast2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, slotParms.course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         slotParms.oldPlayer1 = rs.getString(1);
         slotParms.oldPlayer2 = rs.getString(2);
         slotParms.oldPlayer3 = rs.getString(3);
         slotParms.oldPlayer4 = rs.getString(4);
         slotParms.oldUser1 = rs.getString(5);
         slotParms.oldUser2 = rs.getString(6);
         slotParms.oldUser3 = rs.getString(7);
         slotParms.oldUser4 = rs.getString(8);
         slotParms.oldp1cw = rs.getString(9);
         slotParms.oldp2cw = rs.getString(10);
         slotParms.oldp3cw = rs.getString(11);
         slotParms.oldp4cw = rs.getString(12);
         slotParms.show1 = rs.getShort(13);
         slotParms.show2 = rs.getShort(14);
         slotParms.show3 = rs.getShort(15);
         slotParms.show4 = rs.getShort(16);
         slotParms.oldPlayer5 = rs.getString(17);
         slotParms.oldUser5 = rs.getString(18);
         slotParms.oldp5cw = rs.getString(19);
         slotParms.show5 = rs.getShort(20);
         proMod = rs.getInt(21);
         slotParms.userg1 = rs.getString(22);
         slotParms.userg2 = rs.getString(23);
         slotParms.userg3 = rs.getString(24);
         slotParms.userg4 = rs.getString(25);
         slotParms.userg5 = rs.getString(26);
         slotParms.orig_by = rs.getString(27);
         slotParms.oldguest_id1 = rs.getInt("guest_id1");
         slotParms.oldguest_id2 = rs.getInt("guest_id2");
         slotParms.oldguest_id3 = rs.getInt("guest_id3");
         slotParms.oldguest_id4 = rs.getInt("guest_id4");
         slotParms.oldguest_id5 = rs.getInt("guest_id5");
      }
      pstmt.close();

      if (slotParms.orig_by.equals( "" )) {    // if originator field still empty

         slotParms.orig_by = user;             // set this user as the originator
      }

   }
   catch (Exception ignore) {
   }

   //
   //  If request is to 'Cancel This Res', then delete this slot (we don't save empty slots in teepast)
   //
   if (req.getParameter("remove") != null) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
            "DELETE FROM teepast2 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setLong(1, slotParms.date);
         pstmt6.setInt(2, slotParms.time);
         pstmt6.setInt(3, slotParms.fb);
         pstmt6.setString(4, slotParms.course);

         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

      }
      catch (Exception e1) {
      }

      //
      //  Update the stats for this day and course
      //
      // doStats(slotParms.date, club, slotParms.course, con);
         

   } else {

      //
      //  Process normal res request
      //
      //  Make sure at least 1 player contains a name
      //
      if ((slotParms.player1.equals( "" )) && (slotParms.player2.equals( "" )) && (slotParms.player3.equals( "" )) && (slotParms.player4.equals( "" )) && (slotParms.player5.equals( "" ))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
         out.println("<BR>If you wish to remove all names from this slot, use the 'Cancel Tee Time' button.");
         out.println("<BR><BR>");

            //
            //  Return to _oldsheets to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }

      //
      //  At least 1 Player field is present - Make sure a C/W was specified for all players
      //
      if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.p1cw.equals( "" ))) ||
          ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.p2cw.equals( "" ))) ||
          ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.p3cw.equals( "" ))) ||
          ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.p4cw.equals( "" ))) ||
          ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.p5cw.equals( "" )))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
         out.println("<BR><BR>");
            //
            //  Return to _oldsheets to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Shift players up if any empty spots (start with Player1 position)
      //
      verifySlot.shiftUp(slotParms);

      //
      //  Check if any player names are guest names (set userg1-5 if necessary)
      //
      try {

         verifySlot.parseGuests(slotParms, con);

      }
      catch (Exception e1) {
      }

      //
      //  Make sure there are no duplicate names
      //
      player = "";

      if ((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) {

         if ((slotParms.player1.equalsIgnoreCase( slotParms.player2 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player3 )) ||
             (slotParms.player1.equalsIgnoreCase( slotParms.player4 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player5 ))) {

            player = slotParms.player1;
         }
      }

      if ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) {

         if ((slotParms.player2.equalsIgnoreCase( slotParms.player3 )) || (slotParms.player2.equalsIgnoreCase( slotParms.player4 )) ||
             (slotParms.player2.equalsIgnoreCase( slotParms.player5 ))) {

            player = slotParms.player2;
         }
      }

      if ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) {

         if ((slotParms.player3.equalsIgnoreCase( slotParms.player4 )) ||
             (slotParms.player3.equalsIgnoreCase( slotParms.player5 ))) {

            player = slotParms.player3;
         }
      }

      if ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) {

         if (slotParms.player4.equalsIgnoreCase( slotParms.player5 )) {

            player = slotParms.player4;
         }
      }

      if (!player.equals( "" )) {          // if dup name found

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
         out.println("<BR><BR>Please correct this and try again.");
         out.println("<BR><BR>");
         //
         //  Return to _oldsheets to change the player order
         //
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
         out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
         out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
         out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
         out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
         out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
         out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
         out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
         out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
         out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
         out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
         out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
         out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
         out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
         out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
         out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
         out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
         out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
         out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
         out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Parse the names to separate first, last & mi
      //
      try {

         error = verifySlot.parseNames(slotParms, "pro");

      }
      catch (Exception ignore) {
      }

      if ( error == true ) {          // if problem

         invData(out);                        // reject
         return;
      }


      //
      //  Get the usernames, membership types for players if matching name found
      //
      try {

         verifySlot.getUsers(slotParms, con);

      }
      catch (Exception ignore) {
      }


      //
      //  Save the members' usernames for guest association
      //
      memA[0] = slotParms.user1;
      memA[1] = slotParms.user2;
      memA[2] = slotParms.user3;
      memA[3] = slotParms.user4;
      memA[4] = slotParms.user5;

      //
      //  Check if proshop user requested that we skip the following name test.
      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if any of the names are invalid.  If so, ask proshop if they want to ignore the error.
         //
         if (slotParms.inval5 != 0) {

            err_name = slotParms.player5;
         }

         if (slotParms.inval4 != 0) {

            err_name = slotParms.player4;
         }

         if (slotParms.inval3 != 0) {

            err_name = slotParms.player3;
         }

         if (slotParms.inval2 != 0) {

            err_name = slotParms.player2;
         }

         if (slotParms.inval1 != 0) {

            err_name = slotParms.player1;
         }

         if (!err_name.equals( "" )) {      // invalid name received

            out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
            out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");

            if (overrideAccess) {
                out.println("<BR><BR>Would you like to override this check and allow this reservation?");
            }

            out.println("<BR><BR>");
            
            //
            //  Return to _oldsheets to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");

            if (!overrideAccess) {
                out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</form></font>");
            } else {
                out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                
                out.println("</form></font>");

                out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
                out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
                out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
                out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
                out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
                out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
                out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
                out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
                out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
                out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
                out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
                out.println("<input type=\"submit\" value=\"YES\" name=\"submit\"></form>");
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }

          //
          //  Reject if any player is a guest type that uses the guest tracking system, but the guest_id is blank or doesn't match the guest name entered
          //
          if (!slotParms.gplayer.equals( "" ) && slotParms.hit4 == true) {

              out.println(SystemUtils.HeadTitle("Data Entry Error"));
              out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
              out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
              out.println("<center>");
              out.println("<BR><BR><H3>Data Entry Error</H3>");
              out.println("<BR><BR><b>" + slotParms.gplayer + "</b> appears to have been manually enetered or " +
                      "<br>modified after selecting a different guest from the Guest Selection window.");
              out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
              out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
              out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.");

              returnToSlot(out, slotParms, calDate, overrideAccess, 1);
              return;
          }
      }       // end of skip1

      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 9) {     // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

         //
         //***********************************************************************************************
         //
         //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
         //
         //***********************************************************************************************
         //
         if (slotParms.guests != 0 && slotParms.members != 0) {      // if both guests and members were included

            if (slotParms.g1.equals( "" )) {              // if slot 1 is not a guest

               //
               //  Both guests and members specified - determine guest owners by order
               //
               gi = 0;
               memberName = "";
               while (gi < 5) {                  // cycle thru arrays and find guests/members

                  if (!slotParms.gstA[gi].equals( "" )) {

                     usergA[gi] = memberName;       // get last players username
                  } else {
                     usergA[gi] = "";               // init field
                  }
                  if (!memA[gi].equals( "" )) {

                     memberName = memA[gi];        // get players username
                  }
                  gi++;
               }
               slotParms.userg1 = usergA[0];        // max of 4 guests since 1 player must be a member to get here
               slotParms.userg2 = usergA[1];
               slotParms.userg3 = usergA[2];
               slotParms.userg4 = usergA[3];
               slotParms.userg5 = usergA[4];
            }

            if (!slotParms.g1.equals( "" ) || slotParms.members > 1) {  // if slot 1 is a guest OR more than 1 member

               //
               //  At least one guest and one member have been specified.
               //  Prompt user to verify the order.
               //
               //  Only require positioning if a POS system was specified for this club (saved in Login)
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

               if (!slotParms.g1.equals( "" ) && !posType.equals( "" )) {   // if slot 1 is not a guest & POS

                  out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("You cannot have a guest in the first player position when one or more members are also specified.");
                  out.println("<BR><BR>");
               } else {
                  out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("Please verify that the following order is correct:");
                  out.println("<BR><BR>");
                  out.println(slotParms.player1 + " <BR>");
                  out.println(slotParms.player2 + " <BR>");
                  if (!slotParms.player3.equals( "" )) {
                     out.println(slotParms.player3 + " <BR>");
                  }
                  if (!slotParms.player4.equals( "" )) {
                     out.println(slotParms.player4 + " <BR>");
                  }
                  if (!slotParms.player5.equals( "" )) {
                     out.println(slotParms.player5 + " <BR>");
                  }
                  out.println("<BR>Would you like to process the request as is?");
               }

               //
               //  Return to _oldsheets to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");

               if (!slotParms.g1.equals( "" ) && !posType.equals( "" )) {      // if slot 1 is a guest & POS

                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

               } else {
                  out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

                  //
                  //  Return to process the players as they are
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
                  out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
                  out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
                  out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
                  out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
                  out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form></font>");
               }
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }

         } else {

            //
            //  Either all members or all guests - check for all guests (Unaccompanied Guests)
            //
            if (slotParms.guests != 0) {      // if all guests

               //
               //  At least one guest and no member has been specified.
               //  Get associated member names if already assigned.
               //
               try {

                  if (!slotParms.userg1.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg1);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem1 = mem_name.toString();                      // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg2.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg2);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem2 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg3.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg3);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem3 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg4.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg4);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem4 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg5.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg5);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem5 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
               }
               catch (Exception ignore) {
               }

               //
               //  Prompt user to specify associated member(s) or skip.
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

               // if Hazeltine National and sponsored guests
               if ((club.equals( "hazeltine" )) &&
                   (slotParms.player1.startsWith(sponsored) || slotParms.player2.startsWith(sponsored) ||
                    slotParms.player3.startsWith(sponsored) || slotParms.player4.startsWith(sponsored) ||
                    slotParms.player5.startsWith(sponsored))) {

                  out.println("You are requesting a tee time for a Sponsored Group.<br>");
                  out.println("Sponsored Groups must be associated with a member.<br><br>");
                  out.println("<BR>Would you like to assign a member to the Sponsored Group?");

               } else {

                  if (slotParms.guests == 1) {      // if one guest
                     out.println("You are requesting a tee time for an unaccompanied guest.<br>");
                     out.println("The guest should be associated with a member.<br><br>");
                     out.println("<BR>Would you like to assign a member to the guest, or change the assignment?");
                  } else {
                     out.println("You are requesting a tee time for unaccompanied guests.<br>");
                     out.println("Guests should be associated with a member.<br><br>");
                     out.println("<BR>Would you like to assign a member to the guests, or change the assignments?");
                  }
               }

               //
               //  Return to _oldsheets (doPost) to assign members
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"mem1\" value=\"" + slotParms.mem1 + "\">");
               out.println("<input type=\"hidden\" name=\"mem2\" value=\"" + slotParms.mem2 + "\">");
               out.println("<input type=\"hidden\" name=\"mem3\" value=\"" + slotParms.mem3 + "\">");
               out.println("<input type=\"hidden\" name=\"mem4\" value=\"" + slotParms.mem4 + "\">");
               out.println("<input type=\"hidden\" name=\"mem5\" value=\"" + slotParms.mem5 + "\">");

               out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");  // assign member to guests

               out.println("<input type=\"submit\" value=\"Yes - Assign Member\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               //
               //  Return to process the players as they are
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
               out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
               out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
               out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
               out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
               out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
               out.println("<input type=\"submit\" value=\"No - Continue\" name=\"submit\"></form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;

            }
         }      // end of IF any guests specified

      } else {   // skip 9 requested?

         if (skip == 9) {   // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

            //
            //  User has responded to the guest association prompt - process tee time request in specified order
            //
            slotParms.userg1 = req.getParameter("userg1");
            slotParms.userg2 = req.getParameter("userg2");
            slotParms.userg3 = req.getParameter("userg3");
            slotParms.userg4 = req.getParameter("userg4");
            slotParms.userg5 = req.getParameter("userg5");
         }
      }         // end of IF skip9


      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      //
      //  Make sure there is a member in the tee time slot
      //    If not, no statistic counted
      //
      if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) ||
          ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) ||
          ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) ||
          ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) ||
          ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.g5.equals( "" )))) {

         //
         //  If players changed, then init the no-show flag, else use the old no-show value
         //
         if (!slotParms.player1.equals( slotParms.oldPlayer1 )) {

            slotParms.show1 = 0;        // init no-show flag
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 )) {

            slotParms.show2 = 0;        // init no-show flag
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 )) {

            slotParms.show3 = 0;        // init no-show flag
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 )) {

            slotParms.show4 = 0;        // init no-show flag
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 )) {

            slotParms.show5 = 0;        // init no-show flag
         }

         //
         //  Verification complete -
         //
         if ((!slotParms.oldPlayer1.equals( "" )) || (!slotParms.oldPlayer2.equals( "" )) || (!slotParms.oldPlayer3.equals( "" )) ||
             (!slotParms.oldPlayer4.equals( "" )) || (!slotParms.oldPlayer5.equals( "" ))) {

            proMod++;      // increment number of mods
         }
      }

      //
      // Get the extra data for the mtype1-5, mship1-5, gtype1-5 & grev1-5 fields
      // (this portion of code is basically copied from SystemUtils.moveTee)

      // load up and store all the guest types in an array
      String [] gtypes = new String[35];
      int [] grevs = new int[35];

      String mship1 = "";
      String mship2 = "";
      String mship3 = "";
      String mship4 = "";
      String mship5 = "";
      String mtype1 = "";
      String mtype2 = "";
      String mtype3 = "";
      String mtype4 = "";
      String mtype5 = "";
      String gtype1 = "";
      String gtype2 = "";
      String gtype3 = "";
      String gtype4 = "";
      String gtype5 = "";
      int grev1 = 0;
      int grev2 = 0;
      int grev3 = 0;
      int grev4 = 0;
      int grev5 = 0;

      PreparedStatement pstmt2;
      ResultSet rs2 = null;

      int total_guests = 0;
      int x = 0;

      Statement stmt = null;

      try {

          stmt = con.createStatement();
          rs = stmt.executeQuery("SELECT guest, revenue FROM guest5");

          while (rs.next()) {

             gtypes[x] = rs.getString(1);
             grevs[x] = rs.getInt(2);

             x++;
          }

          stmt.close();

      } catch (Exception exc) {
      }

      if (x > 0) total_guests = x - 1;

        // get mship1, mtype1, gtype1, grev1
        if (!slotParms.user1.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, slotParms.user1);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mship1 = rs2.getString(1);
                    mtype1 = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!slotParms.player1.equals("")) {

            // check to see which type of guest this player is
            loop1:
            for (x=0; x <= total_guests; x++) {

                try {
                if (slotParms.player1.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                    gtype1 = gtypes[x];
                    grev1 = grevs[x];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

        // get mship2, mtype2, gtype2, grev2
        if (!slotParms.user2.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, slotParms.user2);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mship2 = rs2.getString(1);
                    mtype2 = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!slotParms.player2.equals("")) {

            // check to see which type of guest this player is
            loop12:
            for (x=0; x <= total_guests; x++) {

                try {
                if (slotParms.player2.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                    gtype2 = gtypes[x];
                    grev2 = grevs[x];
                    break;
                }
                } catch (IndexOutOfBoundsException exc) {}
            }

        }

        // get mship3, mtype3, gtype3, grev3
        if (!slotParms.user3.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, slotParms.user3);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mship3 = rs2.getString(1);
                    mtype3 = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!slotParms.player3.equals("")) {

            // check to see which type of guest this player is
            loop3:
            for (x=0; x <= total_guests; x++) {

                try {
                if (slotParms.player3.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                    gtype3 = gtypes[x];
                    grev3 = grevs[x];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

        // get mship4, mtype4, gtype4, grev4
        if (!slotParms.user4.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, slotParms.user4);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mship4 = rs2.getString(1);
                    mtype4 = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!slotParms.player4.equals("")) {

            // check to see which type of guest this player is
            loop4:
            for (x=0; x <= total_guests; x++) {

                try {
                if (slotParms.player4.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                    gtype4 = gtypes[x];
                    grev4 = grevs[x];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }

        // get mship5, mtype5, gtype5, grev5
        if (!slotParms.user5.equals("")) {

            try {

                pstmt2 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE username = ?");

                pstmt2.clearParameters();
                pstmt2.setString(1, slotParms.user5);
                rs2 = pstmt2.executeQuery();

                if (rs2.next()) {

                    mship5 = rs2.getString(1);
                    mtype5 = rs2.getString(2);
                }

                pstmt2.close();

            } catch (Exception ignore) { }

        } else if (!slotParms.player5.equals("")) {

            // check to see which type of guest this player is
            loop5:
            for (x=0; x <= total_guests; x++) {

                try {
                if (slotParms.player5.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                    gtype5 = gtypes[x];
                    grev5 = grevs[x];
                    break;
                }
                } catch (IndexOutOfBoundsException ignore) {}
            }

        }




      //
      //  Update the tee slot in teepast
      //
      try {

         PreparedStatement pstmt6 = con.prepareStatement (
            "UPDATE teepast2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  " +
            "show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, show5 = ?, notes = ?, proMod = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? ," +
            "mship1 = ?, mship2 = ?, mship3 = ?, mship4 = ?, mship5 = ?, " +
            "mtype1 = ?, mtype2 = ?, mtype3 = ?, mtype4 = ?, mtype5 = ?, " +
            "gtype1 = ?, gtype2 = ?, gtype3 = ?, gtype4 = ?, gtype5 = ?, " +
            "grev1 = ?, grev2 = ?, grev3 = ?, grev4 = ?, grev5 = ?, " +
            "guest_id1 = ?, guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, slotParms.player1);
         pstmt6.setString(2, slotParms.player2);
         pstmt6.setString(3, slotParms.player3);
         pstmt6.setString(4, slotParms.player4);
         pstmt6.setString(5, slotParms.user1);
         pstmt6.setString(6, slotParms.user2);
         pstmt6.setString(7, slotParms.user3);
         pstmt6.setString(8, slotParms.user4);
         pstmt6.setString(9, slotParms.p1cw);
         pstmt6.setString(10, slotParms.p2cw);
         pstmt6.setString(11, slotParms.p3cw);
         pstmt6.setString(12, slotParms.p4cw);
         pstmt6.setShort(13, slotParms.show1);
         pstmt6.setShort(14, slotParms.show2);
         pstmt6.setShort(15, slotParms.show3);
         pstmt6.setShort(16, slotParms.show4);
         pstmt6.setString(17, slotParms.player5);
         pstmt6.setString(18, slotParms.user5);
         pstmt6.setString(19, slotParms.p5cw);
         pstmt6.setShort(20, slotParms.show5);
         pstmt6.setString(21, slotParms.notes);
         pstmt6.setInt(22, proMod);
         pstmt6.setString(23, slotParms.mNum1);
         pstmt6.setString(24, slotParms.mNum2);
         pstmt6.setString(25, slotParms.mNum3);
         pstmt6.setString(26, slotParms.mNum4);
         pstmt6.setString(27, slotParms.mNum5);
         pstmt6.setString(28, slotParms.userg1);
         pstmt6.setString(29, slotParms.userg2);
         pstmt6.setString(30, slotParms.userg3);
         pstmt6.setString(31, slotParms.userg4);
         pstmt6.setString(32, slotParms.userg5);
         pstmt6.setString(33, slotParms.orig_by);
         pstmt6.setString(34, slotParms.conf);
         pstmt6.setInt(35, slotParms.p91);
         pstmt6.setInt(36, slotParms.p92);
         pstmt6.setInt(37, slotParms.p93);
         pstmt6.setInt(38, slotParms.p94);
         pstmt6.setInt(39, slotParms.p95);

         pstmt6.setString(40, mship1);
         pstmt6.setString(41, mship2);
         pstmt6.setString(42, mship3);
         pstmt6.setString(43, mship4);
         pstmt6.setString(44, mship5);

         pstmt6.setString(45, mtype1);
         pstmt6.setString(46, mtype2);
         pstmt6.setString(47, mtype3);
         pstmt6.setString(48, mtype4);
         pstmt6.setString(49, mtype5);

         pstmt6.setString(50, gtype1);
         pstmt6.setString(51, gtype2);
         pstmt6.setString(52, gtype3);
         pstmt6.setString(53, gtype4);
         pstmt6.setString(54, gtype5);

         pstmt6.setInt(55, grev1);
         pstmt6.setInt(56, grev2);
         pstmt6.setInt(57, grev3);
         pstmt6.setInt(58, grev4);
         pstmt6.setInt(59, grev5);

         pstmt6.setInt(60, slotParms.guest_id1);
         pstmt6.setInt(61, slotParms.guest_id2);
         pstmt6.setInt(62, slotParms.guest_id3);
         pstmt6.setInt(63, slotParms.guest_id4);
         pstmt6.setInt(64, slotParms.guest_id5);

         pstmt6.setLong(65, slotParms.date);
         pstmt6.setInt(66, slotParms.time);
         pstmt6.setInt(67, slotParms.fb);
         pstmt6.setString(68, slotParms.course);

         count = pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

         //
         //  Update the stats for this day and course
         //
         // doStats(slotParms.date, club, slotParms.course, con);
         
      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H2>Database Access Error</H2>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         //
         //  Return to _oldsheets to change the player order
         //
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
         out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
         out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
         out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
         out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
         out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
         out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
         out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
         out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
         out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
         out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
         out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
         out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
         out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
         out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
         out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
         out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
         out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
         out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
         out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
         out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

   }  // end of 'cancel this res' if

   //  Attempt to add hosts for any accompanied tracked guests
   if (slotParms.guest_id1 > 0 && !slotParms.userg1.equals("")) Common_guestdb.addHost(slotParms.guest_id1, slotParms.userg1, con);
   if (slotParms.guest_id2 > 0 && !slotParms.userg2.equals("")) Common_guestdb.addHost(slotParms.guest_id2, slotParms.userg2, con);
   if (slotParms.guest_id3 > 0 && !slotParms.userg3.equals("")) Common_guestdb.addHost(slotParms.guest_id3, slotParms.userg3, con);
   if (slotParms.guest_id4 > 0 && !slotParms.userg4.equals("")) Common_guestdb.addHost(slotParms.guest_id4, slotParms.userg4, con);
   if (slotParms.guest_id5 > 0 && !slotParms.userg5.equals("")) Common_guestdb.addHost(slotParms.guest_id5, slotParms.userg5, con);
   
   //
   //  Build the HTML page to confirm reservation for user
   //
   //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Proshop Tee Slot Page</Title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_oldsheets?calDate=" +calDate+ "&course=" + slotParms.course + "&jump=" + slotParms.jump + "&post=yes\">");
   out.println("</HEAD>");
   out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

      if (notesL > 254) {

      out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
      }
   }
   out.println("<p>&nbsp;</p></font>");

   out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#8B8970\" cellpadding=\"8\">");
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=" + calDate + ">");
   out.println("<input type=\"hidden\" name=\"jump\" value=" + slotParms.jump + ">");
   out.println("<tr><td><font size=\"2\">");
   out.println("<input type=\"submit\" value=\"Return\">");
   out.println("</font></td></tr></form></table>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

 }       // end of Verify


/*
 // ********************************************************************
 //  Process the 'Update Stats' request (when pro is done updating sheet)
 // ********************************************************************

 private void doStats(long date, String club, String course, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String ind = "oldsheets";


   //
   //  Delete the stats record for this date
   //
   try {
      pstmt = con.prepareStatement (
               "Delete FROM stats5 WHERE date = ? AND course = ?");

      pstmt.clearParameters();               // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, course);
      pstmt.executeUpdate();         // execute the prepared pstmt

      pstmt.close();

      //
      //  Go rebuild the stats record for this date and course
      //
      SystemUtils.moveStatsCom(date, ind, course, con, club);
        
   }
   catch (Exception exc) {
   }
 }
 */ 


 // *********************************************************
 //  Process insert request from above
 //
 //  parms:  date          = date of old tee sheet
 //          course        = name of course
 //         
 //          time          = time of tee time (on 2nd call)
 //          fb            = f/b indicator    (on 2nd call)
 //
 // *********************************************************

 private void doInsert(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;

   //
   //  variables for this method
   //
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int hr = 0;
   int min = 0;
   int ampm = 0;
   int fb = 0;
   int time = 0;
   int otime = 0;
   int index = 0;
   int byy = 0;
   int bmm = 0;
   int bdd = 0;

   long date = 0;
   long yy = 0;
   long mm = 0;
   long dd = 0;

   String temp = "";

   String [] day_table = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  get the parms
   //
   String dates = req.getParameter("date");       
   String course = req.getParameter("course");         //  get the course name for this insert
   String calDate = req.getParameter("calDate");

     
   if (course == null) {

      course = "";    // change to null string
   }

   try {
      date = Long.parseLong(dates);
   }
   catch (NumberFormatException e) {
   }

   if (req.getParameter("fb") != null) {            // if 2nd call

      temp = req.getParameter("fb");
      fb = Integer.parseInt(temp);

      temp = req.getParameter("hour");
      hr = Integer.parseInt(temp);

      temp = req.getParameter("min");
      min = Integer.parseInt(temp);

      temp = req.getParameter("ampm");
      ampm = Integer.parseInt(temp);
        
      if (hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)

         hr = hr + ampm;            // convert to military time (12 is always Noon, or PM)
      }

      time = (hr * 100) + min;
   }

   //
   //  Get the name of the day for the date requested
   //
   yy = date / 10000;
   mm = (date - (yy * 10000)) / 100;
   dd = date - ((yy * 10000) + (mm * 100));
     
   byy = (int)yy;
   bmm = (int)mm;
   bdd = (int)dd;

   BigDate thisdate = new BigDate(byy, bmm, bdd);     // get requested date

   int dayNum = thisdate.getDayOfWeek();           // day of the week (0 = Sun, 6 = Sat)

   String day_name = day_table[dayNum];           // get name for day


   if (req.getParameter("insertSubmit") == null) {      // if not an insert 'submit' request (from self)

      //
      //  Process the initial call to Insert a New Tee Time
      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println(SystemUtils.HeadTitle("Proshop Insert Tee Time Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

      out.println("<font size=\"5\">");
      out.println("<p align=\"center\"><b>Insert Tee Time</b></p></font>");
      out.println("<font size=\"2\">");

      out.println("<table cellpadding=\"5\" align=\"center\" width=\"560\">");
      out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Instructions:</b>  To insert a tee time for the date shown below, select the time");
      out.println(" and the 'front/back' values.  Select 'Insert' to add the new tee time.");
      out.println("</font></td></tr></table><br>");


      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\"></input>");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\"></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\"></input>");
      out.println("<input type=\"hidden\" name=\"insert\" value=\"yes\"></input>");

      out.println("<tr><td width=\"560\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">");
         out.println("<b>Note:</b> &nbsp;This tee time must be unique from all others on the sheet. &nbsp;");
         out.println("Therefore, at least one of these values must be different than other tee times.");
      out.println("<p align=\"center\">Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" +mm+ "/" +dd+ "/" +yy+ "</b></p>");
      out.println("Time:&nbsp;&nbsp;");

      out.println("Hour:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"hour\">");
            out.println("<option value=\"01\">01</option>");
            out.println("<option value=\"02\">02</option>");
            out.println("<option value=\"03\">03</option>");
            out.println("<option value=\"04\">04</option>");
            out.println("<option value=\"05\">05</option>");
            out.println("<option value=\"06\">06</option>");
            out.println("<option value=\"07\">07</option>");
            out.println("<option value=\"08\">08</option>");
            out.println("<option value=\"09\">09</option>");
            out.println("<option value=\"10\">10</option>");
            out.println("<option value=\"11\">11</option>");
            out.println("<option value=\"12\">12</option>");
      out.println("</select>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("Min:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"min\">");
         for (int i=0; i<60; i++) {
            if (i < 10) {
               out.println("<option value=\"" +i+ "\">0" +i+ "</option>");
            } else {
               out.println("<option value=\"" +i+ "\">" +i+ "</option>");
            }
         }
      out.println("</select>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("AM/PM:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"ampm\">");
            out.println("<option value=\"00\">AM</option>");
            out.println("<option value=\"12\">PM</option>");
            out.println("</select>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");

      out.println("Front/Back:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"fb\">");
        out.println("<option value=\"00\">Front</option>");
        out.println("<option value=\"01\">Back</option>");
      out.println("</select>");
      out.println("<br><br></p>");
      out.println("<p align=\"center\">");
        out.println("<input type=\"submit\" value=\"Insert\" name=\"insertSubmit\"></input></p>");
      out.println("</font></td></tr></form></table>");
      out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\"></input>");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\"></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" +course + "\"></input>");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      //
      //  End of HTML page
      //
      out.println("</td></tr></table>");                           // end of main page
      out.println("</td></tr></table>");                           // end of whole page
      out.println("</center></body></html>");
      out.close();

   } else {     // end of Insert Submit processing

      //
      //   Call is from self to process an insert request (submit)
      //
      //   Parms passed:   time = time of the tee time to be inserted
      //                   date = date of the tee sheet
      //                   fb   = the front/back value (see above)
      //                   course = course name

      //
      //  Check to make sure a slot like this doesn't already exist
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm FROM teepast2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         pstmt1.setInt(2, time);
         pstmt1.setInt(3, fb);
         pstmt1.setString(4, course);

         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>A tee time with these date, time and F/B values already exists.");
            out.println("<BR>One of these values must change so the tee time is unique.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></BODY></HTML>");
            out.close();
            return;

         }    // ok if we get here - not matching time slot

         pstmt1.close();   // close the stmt

      }
      catch (Exception exc) {   // this is good if no match found
        
         dbError(req, out, exc, lottery);
         return;
      }

      //
      //  This slot is unique - insert it
      //
      try {

         //
         //  Prep'd statement to insert tee time in teecurr
         //
         PreparedStatement pstmt4 = con.prepareStatement (
            "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
            "show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, show5, courseName, " +
            "proNew, proMod, memNew, memMod, " +
            "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, " +
            "userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', " +
            "'', '', '', '', '', '', '', '', '', '', 0, 0, 0, 0, ?, '', '', '', 0, ?, " +
            "0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', " +
            "0, 0, '', '', '', 0, 0, 0, 0, 0)");

         //
         //   Add this time slot to teepast
         //
         pstmt4.clearParameters();        // clear the parms
         pstmt4.setLong(1, date);
         pstmt4.setInt(2, bmm);
         pstmt4.setInt(3, bdd);
         pstmt4.setInt(4, byy);
         pstmt4.setString(5, day_name);
         pstmt4.setInt(6, hr);
         pstmt4.setInt(7, min);
         pstmt4.setInt(8, time);
         pstmt4.setInt(9, fb);
         pstmt4.setString(10, course);

         pstmt4.executeUpdate();        // execute the prepared stmt - insert the tee time slot

         pstmt4.close();   // close the stmt

      }
      catch (Exception e1) {

         dbError(req, out, e1, lottery);
         return;
      }

      //
      //  Insert complete - inform user
      //
      String sampm = "AM";
      if (hr > 11) {        // if PM

         sampm = "PM";
      }

      out.println("<HTML><HEAD><Title>Proshop Insert Confirmation</Title>");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H3>Insert Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been added.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      if (min < 10) {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + mm + "/" + dd + "/" + yy + " " + hr + ":0" + min + " " + sampm + "</b>");
      } else {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + mm + "/" + dd + "/" + yy + " " + hr + ":" + min + " " + sampm + "</b>");
      }
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\" target=\"bot\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=" +calDate+ "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" +course + "\"></input>");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      out.close();
   }

 }      // end of doInsert


 // ******************************************************************************
 //  Get a member's Handicap Number and display in place with the mNum
 // ******************************************************************************

 private String getHndcp(String user, String mNum, Connection con) {

   String hdcp = "";

   try {

      if (!user.equals( "" )) {

         PreparedStatement pstmte1 = con.prepareStatement (
                  "SELECT ghin FROM member2b WHERE username = ?");

         pstmte1.clearParameters();        // clear the parms
         pstmte1.setString(1, user);
         ResultSet rs = pstmte1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            hdcp = rs.getString(1);        // user's handicap number
         }

         pstmte1.close();                  // close the stmt
         
         if (!hdcp.equals("")) {
             
            mNum = mNum + " " + hdcp;     // for display
         } 
         
      } // end if user empty

   }
   catch (Exception ignore) {
   }

   return(mNum);

 }                   // end of getHndcp


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(HttpServletRequest req, PrintWriter out, Exception exc, int lottery) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   out.println("<BR><BR>" + exc.getMessage());
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }

 // *********************************************************
 // Process
 // *********************************************************
 
private void displayDatabaseErrMsg(String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H1>Database Access Error</H1>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
}
 

 private void viewSignups(HttpServletRequest req, PrintWriter out, Connection con) {

            
    int wait_list_id = 0;
    int wait_list_signup_id = 0;
    int sum_players = 0; 
    int date = 0;
    int pos = 1;
    int time = SystemUtils.getTime(con);
    int today_date = (int)SystemUtils.getDate(con);
    int start_time = 0;
    int end_time = 0;
    int count = 0;
    int index = 0;
    
    String id = req.getParameter("waitListId");                 //  uid of the wait list we are working with
    String course = (req.getParameter("course") == null) ? "" : req.getParameter("course");
    String sdate = (req.getParameter("sdate") == null) ? "" : req.getParameter("sdate");    
    String sstart_time = (req.getParameter("start_time") == null) ? "" : req.getParameter("start_time");
    String send_time = (req.getParameter("end_time") == null) ? "" : req.getParameter("end_time");
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
        
        String sindex = req.getParameter("index");                  //  index value of day (needed by Proshop_waitlist_slot when returning)
        String returnCourse = (req.getParameter("returnCourse") == null) ? "" : req.getParameter("returnCourse");
        String day_name = (req.getParameter("day_name") == null) ? "" : req.getParameter("day_name");
        String jump = req.getParameter("jump");
    
    String fullName = "";
    String cw = "";
    String notes = "";
    String nineHole = "";
    
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    
    boolean tmp_found = false;
    boolean tmp_found2 = false;
    boolean show_notes = (req.getParameter("show_notes") != null && req.getParameter("show_notes").equals("yes"));
    boolean alt_row = false;
    boolean tmp_converted = false;
    
    try {
        
        date = Integer.parseInt(sdate);
        wait_list_id = Integer.parseInt(id);
        start_time = Integer.parseInt(sstart_time);
        end_time = Integer.parseInt(send_time);
    }
    catch (NumberFormatException ignore) { }
    
    try {
        
        count = getWaitList.getListCount(wait_list_id, date, 1, 0, false, con);
        
    } catch (Exception exp) {
        out.println(exp.getMessage());
    }
    
    //
    //  isolate yy, mm, dd
    //
    int yy = date / 10000;
    int temp = yy * 10000;
    int mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    int dd = mm - temp;
    mm = mm / 100;
    
    out.println("<br>");
    out.println("<h3 align=center>Master Wait List Sign-up Sheet</h3>");
    
    out.println("<p align=center><font size=3><b><i>\"" + name + "\"</i></b></font></p>");
    
    out.println("<table border=0 align=center>");
    
    out.println("<tr><td><font size=\"2\">");
    out.println("Date:&nbsp;&nbsp;<b>" + mm + "/" + dd + "/" + yy + "</b></td>");
    out.println("<td>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</td><td>");
    if (!course.equals( "" )) {
        out.println("<font size=\"2\">Course:&nbsp;&nbsp;<b>" + course + "</b></font>");
    }
    out.println("</td></tr>");
    
    out.println("<tr><td><font size=\"2\">Time:&nbsp;&nbsp;<b>" + SystemUtils.getSimpleTime(start_time) + " to " + SystemUtils.getSimpleTime(end_time) + "</b></font></td>");
    
    out.println("<td></td>");
    
    out.println("<td><font size=\"2\">Signups:&nbsp;&nbsp;<b>" + count + "</b></font></td>");
    
    out.println("</table>");
    
    out.println("<br>");
    
    out.println("<table align=center border=1 bgcolor=\"#F5F5DC\">");
    
    out.println("<tr bgcolor=\"#8B8970\" align=center style=\"color: black; font-weight: bold\">" +
                    "<td height=35>&nbsp;Pos&nbsp;</td>" +
                    "<td>Sign-up Time</td>" + 
                    "<td>Members</td>" +
                    "<td>Desired Time</td>" +
                    "<td>&nbsp;Players&nbsp;</td>" +
                    "<td>&nbsp;On Sheet&nbsp;</td>" +
                    "<td>Converted At</td>" +
                    "<td>&nbsp;Converted By&nbsp;</td>" +
                    ((show_notes) ? "<td>&nbsp;Notes&nbsp;</td>" : "") +
                "</tr>");
    
    out.println("<!-- wait_list_id=" + wait_list_id + ", date=" + date + ", time=" + time + " -->");
    
    try {
        
        pstmt = con.prepareStatement ("" +
            "SELECT *, " +
                "DATE_FORMAT(created_datetime, '%c/%e/%y %r') AS created_time, " +
                "DATE_FORMAT(converted_at, '%c/%e/%y %r') AS converted_time " + // %l:%i %p
            "FROM wait_list_signups " +
            "WHERE wait_list_id = ? AND date = ? " +
            "ORDER BY created_datetime");
        
        pstmt.clearParameters();
        pstmt.setInt(1, wait_list_id);
        pstmt.setInt(2, date);
        
        ResultSet rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            wait_list_signup_id = rs.getInt("wait_list_signup_id");
            
            out.print("<tr align=center" + ((alt_row) ? " style=\"background-color:white\"" : "") + "><td>" + pos + "</td>");
            
            out.println("<td>&nbsp;" + rs.getString("created_time") + "&nbsp;</td>");
            
            //
            //  Display players in this signup
            //
            pstmt2 = con.prepareStatement ("" +
                  "SELECT * " +
                  "FROM wait_list_signups_players " +
                  "WHERE wait_list_signup_id = ? " +
                  "ORDER BY pos");
            
            pstmt2.clearParameters();
            pstmt2.setInt(1, wait_list_signup_id);
            
            ResultSet rs2 = pstmt2.executeQuery();

            out.print("<td align=left>");

            tmp_found2 = false;

            while (rs2.next()) {

                fullName = rs2.getString("player_name");
                cw = rs2.getString("cw");
                if (rs2.getInt("9hole") == 1) cw = cw + "9";

                if (tmp_found2) out.print(",&nbsp; "); else out.print("&nbsp;");
                out.print(fullName + " <font style=\"font-size:9px\">(" + cw + ")</font>");
                tmp_found2 = true;
                sum_players++;
                nineHole = "";   // reset
            }
            
            pstmt2.close();
            
            out.print("</td>");
            
            out.println("<td>&nbsp;" + SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")) + "&nbsp;</td>");
            
            out.println("<td>" + sum_players + "</td>");
                            
            tmp_converted = rs.getInt("converted") == 1;
            out.println("<td>" + ((tmp_converted) ? "Yes" : "No") + "</td>");
            out.println("<td>" + ((tmp_converted) ? rs.getString("converted_time") : "&nbsp;") + "</td>");
            out.println("<td>" + ((tmp_converted) ? rs.getString("converted_by") : "&nbsp;") + "</td>");
            
            if (show_notes) {
                
                notes = rs.getString("notes").trim();
                if (notes.equals("")) notes = "&nbsp;";
                out.println("<td>" + notes + "</td>");
            }
            
            out.print("</tr>");
            
            pos++;
            sum_players = 0;
            alt_row = alt_row == false;
        }

        pstmt.close();

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading wait list signups.", exc.toString(), out, false);
    }


    out.println("</table><br>");
    
    out.println("<table align=center><tr>");
        
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"POST\">");
    out.println("<input type=\"hidden\" name=\"viewWL\" value=\"\">");
    out.println("<input type=\"hidden\" name=\"show_notes\" value=\"" + ((show_notes) ? "no" : "yes") + "\">");
    out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
    out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    
    out.println("<input type=\"hidden\" name=\"start_time\" value=\"" + start_time + "\">");
    out.println("<input type=\"hidden\" name=\"end_time\" value=\"" + end_time + "\">");
    
    //out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    //out.println("<input type=\"hidden\" name=\"index\" value=\"" + sindex + "\">");
    //out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
    //out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    //out.println("<input type=\"hidden\" name=\"day_name\" value=\"" + day_name + "\">");
    
    out.println("<td><input type=\"submit\" value=\"" + ((show_notes) ? "Hide Notes" : "Show Notes") + "\"></td></form>");
    
    out.println("<td>&nbsp;&nbsp;</td>");
    
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"GET\">");
    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
    //out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    //out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
    
    out.println("<td><input type=\"submit\" value=\"Return\"></td></form>");
    
    out.println("</tr></table></form>");
    
    out.println("<br>");
 }

 private void returnToSlot(PrintWriter out, parmSlot slotParms, String calDate, boolean allowOverride, int skip) {

            out.println("<BR><BR>");

            //
            //  Return to _oldsheets to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_oldsheets\" method=\"post\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"edit\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
 }
 
}
