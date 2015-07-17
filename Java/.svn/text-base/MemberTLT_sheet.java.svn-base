/***************************************************************************************
 *   MemberTLT_sheet:  This servlet will process the 'View Tee Sheet' request from
 *                    the Member's Select page.
 *
 *
 *   called by:  Member_select (doPost)
 *               Member_slot (via Member_jump on a cancel)
 *         Member_jump
 *
 *   created: 9/12/2006   Paul S.
 *
 *   last updated: 
 *
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *       12/09/09   When looking for events only check those that are active.
 *        8/05/09   Default time selection to the set start time for when members are allowed to submit notifications instead of always 7:00am
 *        7/21/09   Fixed change that was missing in MemberTLT_sheet for old calendar changes (4/30 on Member_jump/Member_sheet)
 *        8/07/07   Changed wording on page 
 *        5/09/07   DaysAdv array no longer stored in session block - Using call to SystemUtils.daysInAdv
 *        4/10/07   Changed 'tee sheet' portion to display all tee times not just todays already played rounds
 *
 *
 *   notes:  pull all consecutive tee times code - not needed at the notify level
 *
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

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.parmClub;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.verifySlot;
import com.foretees.common.Utilities;


public class MemberTLT_sheet extends HttpServlet {
                

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 } // end doPost


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out;

   //PreparedStatement pstmt1 = null;
   Statement stmt = null;
   //Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   //ResultSet rs3 = null;
   //ResultSet rs4 = null;

   ByteArrayOutputStream buf = null;
   String encodings = "";               // browser encodings
    
   boolean Gzip = false;        // default = no gzip

   if (req.getParameter("event") != null || req.getParameter("rest") != null) {

      out = resp.getWriter();                                         // normal output stream

   } else {
       
      //
      //  use GZip (compression) if supported by browser
      //
      encodings = req.getHeader("Accept-Encoding");               // browser encodings

      if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {    // if browser supports gzip

         Gzip = true;
         resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

         buf = new ByteArrayOutputStream();

         GZIPOutputStream gzipOut = new GZIPOutputStream(buf);
         OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOut, "UTF-8");
         out = new PrintWriter(outputStreamWriter);

      } else {

         out = resp.getWriter();                                         // normal output stream
      }
   }

   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

   if (session == null) return;

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
      return;
   }

   //
   //  Get this user's full name & username
   //
   String full_name = (String)session.getAttribute("name");
   String user = (String)session.getAttribute("user");
   String caller = (String)session.getAttribute("caller");
   String club = (String)session.getAttribute("club");              // get club name
   String mship = (String)session.getAttribute("mship");            // get member's mship type
   String mtype = (String)session.getAttribute("mtype");            // get member type
   //DaysAdv daysArray = (DaysAdv)session.getAttribute("daysArray");  // get array object for 'days in adv' from Login

   // Setup the daysArray
   DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
   daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, user, con);

   //
   //  First, check for Event call - user clicked on an event in the Legend
   //
   if (req.getParameter("event") != null) {

      String eventName = req.getParameter("event");

      displayEvent(eventName, out, con);             // display the information
      return;
   }

   //
   //  First, check for Restriction call - user clicked on a rest in the Legend
   //
   if (req.getParameter("rest") != null) {

      String restName = req.getParameter("rest");

      displayRest(restName, out, con);             // display the information
      return;
   }

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   int hr = 0;
   int min = 0;
   int tee_time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;                       // event type
   int shotgun = 1;                    // event type = shotgun
   int in_use;
   int mrest_id = 0;
   short fb = 0;

   String name = "";
   String courseNameT = "";
   String submit = "";
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String rest5 = "";
   String bgcolor5 = "";
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
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String orig_by = "";
   String ampm = "";
   //String event_rest = "";
   String bgcolor = "";
   String sfb = "";
   //String msubtype = "";
   //String mNum = "";
   String hole = ""; // hole assignment

   String event1 = "";       // for legend - max 4 events, 4 rest's
   String ecolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String event3 = "";
   String ecolor3 = "";
   String event4 = "";
   String ecolor4 = "";
   String rest1 = "";
   String rcolor1 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String blocker = "";
/*
   String mem1 = "";
   String mem2 = "";
   String mem3 = "";
   String mem4 = "";
   String mem5 = "";
   String mem6 = "";
   String mem7 = "";
   String mem8 = "";
   String mship1 = "";
   String mship2 = "";
   String mship3 = "";
   String mship4 = "";
   String mship5 = "";
   String mship6 = "";
   String mship7 = "";
   String mship8 = "";
*/
   String rest_recurr = "";
   //String rest_name = "";
   String sfb2 = "";
   //String rest_color = "";
   //String rest_fb = "";
   String jumps = "";
   String num = "";


   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int advance_days = 0;
   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //         one per day of week (Sun - Sat)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   //int oldDays3 = 0;
   //int oldDays6 = 0;
   int daysT = 0;
   //int hndcp = 0;
   int index = 0;
   int index2 = 0;
   int days = 0;
   int orig_days = 0;
   int multi = 0;               // multiple course support
   int i = 0;
   int fives = 0;               // support 5-somes for individual course
   int fivesALL = 0;            // support 5-somes for page display below
   int g1 = 0;                  // guest indicators
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int ind = 0;
   int j = 0;
   int jump = 0;
   int hideNames = 0;
   int hideN = 0;
   int hideSubmit = 0;

   int stop_time = 0;           // time to stop allowing tee times
   int cal_time = 0;            // calendar time for compares
   int cal_hour = 0;
   int cal_min = 0;

   boolean useDynamicCalendars = true;
   boolean allow = true;
   boolean disp_hndcp = true;
   boolean restrictAll = false;
   //boolean suspend = false;

   //
   //  2-some indicator used for some custom requests
   //
   boolean twoSomeOnly = false;

  //
   //  parm block to hold the Course Colors
   //
   parmCourseColors colors = new parmCourseColors();          // allocate a parm block

   int colorMax = colors.colorMax;             // max number of colors defined
   
   //
   //  Arrays to hold the course names, member types and membership types
   //
   int cMax = 0; 
   String courseName = "";
   
   ArrayList<String> course = new ArrayList<String>();
   
   ArrayList<Integer> fivesA = new ArrayList<Integer> ();        // array list to hold 5-some option for each course
   
   int tmp_i = 0; // counter for course[], shading of course field
   int courseCount = 0; // total courses for this club
   

   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat
   int [] advtimes = new int [7];                       // 0=Sun, 6=Sat
   int [] origdays = new int [7];                       // 0=Sun, 6=Sat

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();          // allocate a parm block

   //
   //  Check for the 'index' parm.  If not, get the index from the submit button (from Member_select).
   //
   if (req.getParameter("index") != null) {

      num = req.getParameter("index");
        
   } else {

      //
      //    The name of the submit button is an index value preceeded by the letter 'i' (must start with alpha)
      //    (0 = today, 1 = tomorrow, etc.)
      //
      //    Other parms passed:  course - name of course
      //
      name = "";                                      // init

      Enumeration enum1 = req.getParameterNames();     // get the parm names passed

      loop1:
      while (enum1.hasMoreElements()) {

         name = (String) enum1.nextElement();          // get name of parm

         if (name.startsWith( "i" )) {

            break loop1;                              // done - exit while loop
         }
      }

      //
      //  make sure we have the index value
      //
      if (!name.startsWith( "i" )) {

         out.println(SystemUtils.HeadTitle("Procedure Error"));
         out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
         out.println("<BR><BR><H3>Access Procedure Error</H3>");
         out.println("<BR><BR>Required Parameter is Missing - MemberTLT_sheet.");
         out.println("<BR>Please exit and try again.");
         out.println("<BR><BR>If problem persists, report this error to your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         if (Gzip == true) {
            resp.setContentLength(buf.size());                 // set output length
            resp.getOutputStream().write(buf.toByteArray());
         }
         return;
      }

      //
      //  Convert the index value from string to int
      //
      StringTokenizer tok = new StringTokenizer( name, "i" );     // space is the default token - use 'i'

      num = tok.nextToken();                // get just the index number (name= parm must start with alpha)
   }

   if (num.equals( "0" )) {           // for some reason zero is very slow
     
      index = 0;
        
   } else { 
     
      try {
         index = Integer.parseInt(num);
      }
      catch (NumberFormatException e) {
         // ignore error
      }
   }

   index2 = index;     // save for later (number of days from today)

   //
   //  save the index value for lottery computations
   //
   advance_days = index;

   //
   //  Get the golf course name requested
   //
   String courseName1 = "";
     
   if (req.getParameter("course") != null) {

      courseName1 = req.getParameter("course");
   } else {
       
      // Custom to default to -ALL- courses
      //if ( (club.equals("???") ) courseName1 = "-ALL-";
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
         // ignore error
         jump = 0;
      }
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
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
   cal_hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)
   cal_min = cal.get(Calendar.MINUTE);

   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   cal_time = (cal_hour * 100) + cal_min;     // get time in hhmm format

   cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value

      if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
      }
   }

   //
   //   Adjust the calendar to get the slected date
   //
   if (index > 0) {
     
      cal.add(Calendar.DATE,index);                  // roll ahead 'index' days
   }

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   String day_name = day_table[day_num];         // get name for day

   month++;                                      // month starts at zero

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   long dateShort = (month * 100) + day;         // create date of mmdd for customs
   
   String date_mysql = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);
   String time_mysql = (cal_time / 100) + ":" + SystemUtils.ensureDoubleDigit(cal_time % 100) + ":00";
   
   //out.println("time_mysql = "+time_mysql);

    int start_time = 0;
    int end_time = 0;
    int start_hr = 0;
    int start_min = 0;
    String start_ampm = "";
    String fstart_time = "";
    String fend_time = "";
    int tmp_interval = 5;
   
    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT notify_interval, " +
                "DATE_FORMAT(nwindow_starttime, '%H%i') AS start_time, " +
                "DATE_FORMAT(nwindow_endtime, '%H%i') AS end_time, " +
                "DATE_FORMAT(nwindow_starttime, '%l:%i %p') AS fstart_time, " +
                "DATE_FORMAT(nwindow_endtime, '%l:%i %p') AS fend_time, " +
                "DATE_FORMAT(nwindow_starttime, '%l') AS start_hr, " +
                "DATE_FORMAT(nwindow_starttime, '%i') AS start_min, " +
                "DATE_FORMAT(nwindow_starttime, '%p') AS start_ampm " +
            "FROM club5 " +
            "WHERE ISNULL(nwindow_starttime) = false AND ISNULL(nwindow_endtime) = false");

        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        if (rs.next()) {

            tmp_interval = rs.getInt(1);
            start_time = rs.getInt(2);
            end_time = rs.getInt(3);
            fstart_time = rs.getString(4);
            fend_time = rs.getString(5);
            start_hr = rs.getInt(6);
            start_min = rs.getInt(7);
            start_ampm = rs.getString(8);
        }

        pstmt.close();

    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<BR><BR><H2>Database Access Error</H2>");
        out.println("<BR><BR>Exception Received Getting Notification Window Times.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>" + e1.toString());
        out.println("<BR><BR>");
        out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        if (Gzip == true) {
            resp.setContentLength(buf.size());                 // set output length
            resp.getOutputStream().write(buf.toByteArray());
        }
        
    }
    
    //out.println("<!-- start_time=" + start_time + " | end_time=" + end_time + " -->");
    //out.println("<!-- fstart_time=" + fstart_time + " | fend_time=" + fend_time + " -->");
    
    
    try {

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm);        // get the club parms
      multi = parm.multi;
      hideNames = parm.hiden;

      //
      //  Determine if this club wants to display handicaps for the members
      //
      if (parm.hndcpMemSheet == 0) disp_hndcp = false;

      //
      //  use the member's mship type to determine which 'days in advance' parms to use
      //
      verifySlot.getDaysInAdv(con, parm, mship);        // get the days in adv data for this member
      
      // get days in adv for this type
      days1 = parm.advdays1;     
      days2 = parm.advdays2;
      days3 = parm.advdays3;
      days4 = parm.advdays4;
      days5 = parm.advdays5;
      days6 = parm.advdays6;
      days7 = parm.advdays7;

      // 
      //  Save the original days in adv
      //
      origdays[0] = days1;
      origdays[1] = days2;
      origdays[2] = days3;
      origdays[3] = days4;
      origdays[4] = days5;
      origdays[5] = days6;
      origdays[6] = days7;

      if (multi != 0) {           // if multiple courses supported for this club

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names
          
         courseCount = course.size();
         
         if (courseCount > 1) {                // if more than 1 course, add -ALL- option

            course.add ("-ALL-");
         }
         
         //
         //  Make sure we have a course name (in case we came directly from the menu for today's tee sheet)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = course.get(0);     // grab the first one
         }
      }
      
      //
      //  Get the System Parameters for this Course
      //
      if (courseName1.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         i = 0;
         loopc:
         while (i < courseCount) {

            courseName = course.get(i);       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

               if (courseName.equals( "" )) {      // done if null
                  break loopc;
               }
               getParms.getCourse(con, parmc, courseName);
               
               fivesA.add (parmc.fives);      // get fivesome option
               
               if (parmc.fives == 1) {
                  fives = 1;
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, courseName1);

         fives = parmc.fives;      // get fivesome option
      }

      fivesALL = fives;            // save 5-somes option for table display below

      //
      //   Remove any guest types that are null - for tests below
      //
      i = 0;
      while (i < parm.MAX_Guests) {

         if (parm.guest[i].equals( "" )) {

            parm.guest[i] = "$@#!^&*";      // make so it won't match player name
         }
         i++;
      }         // end of while loop

      //
      //  Get all restrictions for this day and user (for use when checking each tee time below)
      //
      parmr.user = user;
      parmr.mship = mship;
      parmr.mtype = mtype;
      parmr.date = date;
      parmr.day = day_name;
      parmr.course = courseName1;

      getRests.getAll(con, parmr);       // get the restrictions


      //
      //   Statements to find any restrictions or events for today
      //
      String string7b = "";
      String string7c = "";
        
      if (courseName1.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes' ORDER BY stime";
      } else {
         string7b = "SELECT name, recurr, color, id, stime, etime FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7c = "SELECT name, color, act_hr, act_min FROM events2b WHERE date = ? AND inactive = 0 ORDER BY stime";
      } else {
         string7c = "SELECT name, color, act_hr, act_min FROM events2b WHERE date = ? AND inactive = 0 " +
                    "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);

      //
      //  Scan the events and restrictions to build the legend
      //
      pstmt7b.clearParameters();
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
        
      if (!courseName1.equals( "-ALL-" )) {
         pstmt7b.setString(3, courseName1);
      }

      rs = pstmt7b.executeQuery();

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);
         mrest_id = rs.getInt("id");
         
         int stimeSusp = 0;
         int etimeSusp = 0;
         int stimeRest = rs.getInt("stime");
         int etimeRest = rs.getInt("etime");
         
         boolean showRest = true;
         
         ResultSet rsSusp = null;
         PreparedStatement pstmtSusp = con.prepareStatement(
                 "SELECT stime, etime FROM rest_suspend WHERE mrest_id = ? AND " + day_name.toLowerCase() + "=1 " +
                 "AND sdate <= ? AND edate >= ? " +
                 "AND (eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)))");
         pstmtSusp.clearParameters();
         pstmtSusp.setInt(1, mrest_id);
         pstmtSusp.setInt(2, (int)date);
         pstmtSusp.setInt(3, (int)date);
         pstmtSusp.setInt(4, (int)date);
         rsSusp = pstmtSusp.executeQuery();
         
         while (rsSusp.next()) {        // If restriction is suspended for the entire day, do not display it on the legend
             stimeSusp = rsSusp.getInt("stime");
             etimeSusp = rsSusp.getInt("etime");
             
             if (stimeSusp <= stimeRest && etimeSusp >= etimeRest) {      // If suspension covers entire restriction period
                 showRest = false;
             }
         }
         
         pstmtSusp.close();
         
         if (showRest) {    // Only display on legend if not suspended for entire day

             //
             //  We must check the recurrence for this day (Monday, etc.)
             //
             if ((rest_recurr.equals( "Every " + day_name )) ||          // if this day
                 (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                 ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                   (!day_name.equalsIgnoreCase( "saturday" )) &&
                   (!day_name.equalsIgnoreCase( "sunday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                  (day_name.equalsIgnoreCase( "saturday" ))) ||
                 ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                  (day_name.equalsIgnoreCase( "sunday" )))) {


                if ((!rest.equals( rest1 )) && (rest1.equals( "" ))) {

                   rest1 = rest;
                   rcolor1 = rcolor;

                   if (rcolor.equalsIgnoreCase( "default" )) {

                      rcolor1 = "#F5F5DC";
                   }

                } else {

                   if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (rest2.equals( "" ))) {

                      rest2 = rest;
                      rcolor2 = rcolor;

                      if (rcolor.equalsIgnoreCase( "default" )) {

                         rcolor2 = "#F5F5DC";
                      }

                   } else {

                      if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) && (rest3.equals( "" ))) {

                         rest3 = rest;
                         rcolor3 = rcolor;

                         if (rcolor.equalsIgnoreCase( "default" )) {

                            rcolor3 = "#F5F5DC";
                         }

                      } else {

                         if ((!rest.equals( rest1 )) && (!rest.equals( rest2 )) && (!rest.equals( rest3 )) &&
                             (!rest.equals( rest4 )) && (rest4.equals( "" ))) {

                            rest4 = rest;
                            rcolor4 = rcolor;

                            if (rcolor.equalsIgnoreCase( "default" )) {

                               rcolor4 = "#F5F5DC";
                            }
                         }
                      }
                   }
                }
             }
         }
      }                  // end of while
      pstmt7b.close();

      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);
        
      if (!courseName1.equals( "-ALL-" )) {
         pstmt7c.setString(2, courseName1);
      }

      rs = pstmt7c.executeQuery();      // find all matching events, if any

      int act_hr = 0;
      int act_min = 0;
      int event1_start_hr = 0;
      int event1_start_min = 0;
      int event2_start_hr = 0;
      int event2_start_min = 0;
      int event3_start_hr = 0;
      int event3_start_min = 0;
      int event4_start_hr = 0;
      int event4_start_min = 0;
      
      // loop thru all events
      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);
         act_hr = rs.getInt(3);
         act_min = rs.getInt(4);

         if (!event.equals( event1 ) && event1.equals( "" )) {

            event1 = event;
            ecolor1 = ecolor;
            event1_start_hr = act_hr;
            event1_start_min = act_min;
            
            if (ecolor.equalsIgnoreCase( "default" )) ecolor1 = "#F5F5DC";

          } else {

            if (!event.equals( event1 ) && !event.equals( event2 ) && event2.equals( "" )) {

               event2 = event;
               ecolor2 = ecolor;
               event2_start_hr = act_hr;
               event2_start_min = act_min;

               if (ecolor.equalsIgnoreCase( "default" )) ecolor2 = "#F5F5DC";

             } else {

               if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) &&
                   event3.equals( "" )) {

                  event3 = event;
                  ecolor3 = ecolor;
                  event3_start_hr = act_hr;
                  event3_start_min = act_min;

                  if (ecolor.equalsIgnoreCase( "default" )) ecolor3 = "#F5F5DC";

                } else {

                  if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) &&
                      !event.equals( event4 ) && event4.equals( "" )) {

                     event4 = event;
                     ecolor4 = ecolor;
                     event4_start_hr = act_hr;
                     event4_start_min = act_min;

                     if (ecolor.equalsIgnoreCase( "default" )) ecolor4 = "#F5F5DC";
                     
                  }
               }
            }
         }
      }                  // end of while
      pstmt7c.close();

      //
      //  Count the number of tee sheets displayed since last tomcat bounce
      //
      Calendar calCount = new GregorianCalendar();       // get todays date
      int hourCount = calCount.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)
      SystemUtils.sheetCountsMem[hourCount]++;

      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println(SystemUtils.HeadTitle2("Member Tee Sheet"));
        
      //
      // Include the dynamic calendars is required
      //
      if ( useDynamicCalendars == true ) {

         // include files for dynamic calendars
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
         out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
      }
      
      out.println("</HEAD>");
      out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");

      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"></font><center>");
      out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)
      out.println("<table border=\"0\" align=\"center\" width=\"95%\">");         // table for main page
      out.println("<tr><td valign=\"top\" align=\"center\">");

      //**********************************************************
      //  Build calendar for selecting a new day
      //**********************************************************
      int count = 0;                    // init day counter
      int col = 0;                       // init column counter
      int d = 0;                        // 'days in advance' value for current day of week
      int max = daysArray.MAXDAYS;      // max # of days to display

      //
      //  Get today's date and setup parms to use when building the calendar
      //
      Calendar cal2 = new GregorianCalendar();        // get todays date
      int year2 = cal2.get(Calendar.YEAR);
      int month2 = cal2.get(Calendar.MONTH);
      int day2 = cal2.get(Calendar.DAY_OF_MONTH);
      int day_num2 = cal2.get(Calendar.DAY_OF_WEEK);  // day of week (01 - 07)
      cal_hour = cal2.get(Calendar.HOUR_OF_DAY);
      cal_min = cal2.get(Calendar.MINUTE);
      int cal_sec = cal2.get(Calendar.SECOND);

      cal_time = (cal_hour * 100) + cal_min;

      cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

      if (cal_time < 0) {          // if negative, then we went back or ahead one day

         cal_time = 0 - cal_time;        // convert back to positive value

         if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day

            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal2.add(Calendar.DATE, 1);                     // get next day's date

            year2 = cal2.get(Calendar.YEAR);
            month2 = cal2.get(Calendar.MONTH);
            day2 = cal2.get(Calendar.DAY_OF_MONTH);
            day_num2 = cal2.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

         } else {                        // we rolled back 1 day

            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal2.add(Calendar.DATE, -1);                     // get yesterday's date

            year2 = cal2.get(Calendar.YEAR);
            month2 = cal2.get(Calendar.MONTH);
            day2 = cal2.get(Calendar.DAY_OF_MONTH);
            day_num2 = cal2.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
         }
      }

      int today2 = day2;                              // save today's number

      month2 = month2 + 1;                            // month starts at zero

      String mm = mm_table[month2];                   // month name

      int numDays = numDays_table[month2];            // number of days in month

      if (numDays == 0) {                             // if Feb

        int leapYear = year2 - 2000;
        numDays = feb_table[leapYear];               // get days in Feb
      }

      long thisDate2 = (year2 * 10000) + (month2 * 100) + day2;         // get adjusted date for today

      //
      //  Create a current time value for display on the tee sheet page
      //
      String s_time = "";

      cal_hour = cal_time / 100;                // get adjusted hour
      cal_min = cal_time - (cal_hour * 100);    // get minute value
      int cal_am_pm = 0;                            // preset to AM

      if (cal_hour > 11) {
         cal_am_pm = 1;                // PM
         cal_hour = cal_hour - 12;     // set to 12 hr clock
      }
      if (cal_hour == 0) {
         cal_hour = 12;
      }

      if (cal_min < 10) {
         s_time = cal_hour + ":0" + cal_min;
      } else {
         s_time = cal_hour + ":" + cal_min;
      }

      if (cal_sec < 10) {
         s_time = s_time + ":0" + cal_sec;
      } else {
         s_time = s_time + ":" + cal_sec;
      }
      if (cal_am_pm == 0) {
         s_time = s_time + " AM";
      } else {
         s_time = s_time + " PM";
      }

      //
      //  if its earlier than the time specified for days in advance, do not allow the last day_in_advance
      //
      if (parm.advtime1 > cal_time) {

         if (days1 > 0) {

            days1--;
         }
      }

      if (parm.advtime2 > cal_time) {

         if (days2 > 0) {

            days2--;
         }
      }

      if (parm.advtime3 > cal_time) {

         if (days3 > 0) {

            days3--;
         }
      }

      if (parm.advtime4 > cal_time) {

         if (days4 > 0) {

            days4--;
         }
      }

      if (parm.advtime5 > cal_time) {

         if (days5 > 0) {

            days5--;
         }
      }

      if (parm.advtime6 > cal_time) {

         if (days6 > 0) {

            days6--;
         }
      }

      if (parm.advtime7 > cal_time) {

         if (days7 > 0) {

            days7--;
         }
      }

      //
      //    adv time values have been set based on the mship type
      //    calendar time (cal_time) has been adjusted already for the time zone specified
      //
      advdays[0] = days1;     // put 'days in adv' values in array
      advdays[1] = days2;
      advdays[2] = days3;
      advdays[3] = days4;
      advdays[4] = days5;
      advdays[5] = days6;
      advdays[6] = days7;

      advtimes[0] = parm.advtime1;
      advtimes[1] = parm.advtime2;
      advtimes[2] = parm.advtime3;
      advtimes[3] = parm.advtime4;
      advtimes[4] = parm.advtime5;
      advtimes[5] = parm.advtime6;
      advtimes[6] = parm.advtime7;

      //
      //  Adjust days array values if necessary (in case the time has now reached the set value)
      //
      int day_numT = day_num2;                     // get today's day of the week (1 - 7)
      day_numT--;                                  // convert to index (0 - 6)


      for (i = 0; i < daysArray.MAXDAYS; i++) {

        daysT = advdays[day_numT];             // get days in advance for day of the week

        day_numT++;                           // bump to next day of week

        if (day_numT > 6) {                   // if wrapped past end of week

           day_numT = 0;
        }

        //
        // check if this day can be accessed by members (initially set in Login)
        //
        //    0 = No, 1 = Yes, 2 = Yes for Lottery only (set in Login only)
        //
        if (daysT >= i) {                 // if ok for this day

           daysArray.days[i] = 1;        // set ok in array
        }
      }

      //
      // determine days in advance for this day (day of sheet)
      //
      day_numT = day_num;                   // get current tee sheet's day of the week (1 - 7)

      day_numT--;                           // convert day_num to index (0 - 6)
      days = advdays[day_numT];             // get days in advance
      orig_days = origdays[day_numT];       // get original days in advance (before adjustments)

      //
      //  If original 'days in adv' for this day is 0 and we are before the adv time, then do not allow access to any tee times
      //
      if (orig_days == 0 && cal_time < advtimes[day_numT]) {

         restrictAll = true;         // indicate no member access
      }

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         //
         //  use 2 forms so you can switch by clicking either a course or a date
         //
         out.println("<form action=\"/" +rev+ "/servlet/MemberTLT_sheet\" method=\"post\" name=\"cform\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"i" + index2 + "\" value=\"\">");   // use current date

         out.println("<div id=\"awmobject1\">");        // allow menus to show over this box

         out.println("<b>Course:</b>&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"course\" onChange=\"document.cform.submit()\">");

         for (i=0; i < course.size(); i++) {

            courseName = course.get(i);      // get first course name from array

            if (courseName.equals( courseName1 )) {
               out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
            } else {
               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
         }
         out.println("</select></div>");
         out.println("</form>");
      }

      //
      //  build one large table to hold one table for each month required
      //
      out.println("<table border=\"0\" cellpadding=\"5\"><tr><td align=\"center\" valign=\"top\"><font size=\"2\">");

      //
      // if we are using dynamic calendars
      //
      if ( useDynamicCalendars == true ) {

         out.println("<font size=\"2\">");

         // this is the form that gets submitted when the user selects a day from the calendar
         out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
         out.println("<input type=\"hidden\" name=\"thisDate\" value=\"" +thisDate2+ "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName1+ "\">");

         out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
         out.println("</form>");

        // table for calendars built by js
        out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");    // was 190 !!!

        out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

        out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n<tr>\n</table>");

         Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
         int cal_year = cal_date.get(Calendar.YEAR);
         int cal_month = cal_date.get(Calendar.MONTH) + 1;
         int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
         int cal_year2 = cal_year;
         int cal_month2 = cal_month;

        out.println("<script type=\"text/javascript\">");

        out.println("var g_cal_bg_color = '#F5F5DC';");
        out.println("var g_cal_header_color = '#8B8970';");
        out.println("var g_cal_border_color = '#8B8970';");

        out.println("var g_cal_count = 2;"); // number of calendars on this page
        out.println("var g_cal_year = new Array(g_cal_count - 1);");
        out.println("var g_cal_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
        out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
        out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

        // set calendar date parts
        out.println("g_cal_month[0] = " + cal_month + ";");
        out.println("g_cal_year[0] = " + cal_year + ";");
        out.println("g_cal_beginning_month[0] = " + cal_month + ";");
        out.println("g_cal_beginning_year[0] = " + cal_year + ";");
        out.println("g_cal_beginning_day[0] = " + cal_day + ";");
        out.println("g_cal_ending_month[0] = " + cal_month + ";");
        out.println("g_cal_ending_day[0] = 31;");
        out.println("g_cal_ending_year[0] = " + cal_year + ";");

        cal_date.add(Calendar.MONTH, 1); // add a month
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        cal_year = cal_date.get(Calendar.YEAR);
        out.println("g_cal_beginning_month[1] = " + cal_month + ";");
        out.println("g_cal_beginning_year[1] = " + cal_year + ";");
        out.println("g_cal_beginning_day[1] = 0;");
        cal_date.add(Calendar.MONTH, -1); // subtract a month

        cal_date.add(Calendar.YEAR, 1); // add a year
        cal_year = cal_date.get(Calendar.YEAR);
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        out.println("g_cal_ending_month[1] = " + cal_month + ";");
        out.println("g_cal_ending_day[1] = " + cal_day + ";");
        out.println("g_cal_ending_year[1] = " + cal_year + ";");
        cal_date.add(Calendar.YEAR, -1); // subtract a year

        cal_date.add(Calendar.DAY_OF_MONTH, index); // add the # of days ahead of today this tee sheet is for
        if (cal_date.get(Calendar.MONTH) + 1 == cal_month2 && cal_date.get(Calendar.YEAR) == cal_year2) cal_date.add(Calendar.MONTH, 1);
        cal_year = cal_date.get(Calendar.YEAR);
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

        out.println("g_cal_month[1] = " + cal_month + ";");
        out.println("g_cal_year[1] = " + cal_year + ";");

        out.println("</script>");

        out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
        out.println("<script type=\"text/javascript\">\ndoCalendar('1');\n</script>");


         //
         // end of calendar row
         //
         out.println("</td></tr></table>");
         //out.println("</form>");

      } else {      // club is NOT using dynamic calendars

         //
         //  start a new form for the dates so you can switch by clicking either a course or a date
         //
         out.println("<form action=\"/" +rev+ "/servlet/MemberTLT_sheet\" method=\"post\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName1+ "\">");

         //
         //  table for first month
         //
         out.println("<table border=\"1\" width=\"200\" bgcolor=\"#F5F5DC\">");
            out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + mm + "&nbsp;&nbsp;" + year2 + "</b></font>");
            out.println("</td></tr><tr>");
               out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">M</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">W</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">F</font></td>");
               out.println("<td align=\"center\"><font size=\"2\">S</font></td>");

            out.println("</tr><tr>");        // first row of days

            for (i = 1; i < day_num2; i++) {    // skip to the first day
               out.println("<td><br></td>");
               col++;
            }

            while (day2 < today2) {
               out.println("<td align=\"center\"><font size=\"2\">" + day2 + "</font></td>");    // put in day of month
               col++;
               day2++;

               if (col == 7) {
                  col = 0;                             // start new week
                  out.println("</tr><tr>");
               }
            }

            while (count < max) {                 // start with today, go to end of month or 'max' days

               if (day2 <= numDays ) {

                  d = daysArray.days[count];      // get 'days in advance' value for this day (set by Login)

                  if (d == 1) {                  // color the buttons for 'days in advance' number of days

                     out.println("<td align=\"center\"><font size=\"2\"><b>");                      // limegreen
                     out.println("<input type=\"submit\" value=" + day2 + " name=\"i" + count + "\" style=\"background:#32CD32\"></b></font></td>");

                  } else {
                      
                     out.println("<td align=\"center\"><font size=\"2\"><b>");             // lightgrey
                     out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#D3D3D3\"></b></font></td>");
                  }       // end of IF days in advance
                  col++;
                  day2++;
                  count++;

                  if (col == 7) {
                     col = 0;                             // start new week
                     out.println("</tr><tr>");
                  }

               } else {

                  day2 = 1;                               // start a new month
                  month2 = month2 + 1;
                  if (month2 > 12) {
                     month2 = 1;                          // end of year - use Jan
                     year2 = year2 + 1;                    // new year
                  }
                  numDays = numDays_table[month2];        // number of days in month
                  mm = mm_table[month2];                  // month name

                  if (numDays == 0) {                           // if Feb

                     int leapYear = year2 - 2000;
                     numDays = feb_table[leapYear];             // get days in Feb
                  }
                  out.println("</tr></table></td><td align=\"center\" valign=\"top\"><font size=\"2\">");
                  out.println("<table border=\"1\" width=\"200\" bgcolor=\"#F5F5DC\">");
                  out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + mm + "&nbsp;&nbsp;" + year2 + "</b></font>");
                  out.println("</td></tr><tr>");
                     out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">M</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">W</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">F</font></td>");
                     out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
                  out.println("</tr><tr>");        // first row of days

                  for (i = 0; i < col; i++) {          // skip to where we left off
                     out.println("<td><br></td>");
                  }
               }
            }          // end of while count < 30

            out.println("</tr>");

         //
         // end of calendar row
         //
         out.println("</table>");
         out.println("</font></td></tr></table></form>");

      }

      //**********************************************************
      //  Continue with instructions and tee sheet
      //**********************************************************

      // check max allowed days in advance for members
      //if (index2 <= days) {

         out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"680\">");
         out.println("<tr><td align=\"left\"><font color=\"#FFFFFF\" size=\"2\">");

         out.println("<b>Instructions:</b>  " +
                 "To send the club a notification of your intent to play on this day, " +
                 "just click on the 'New Notification' button. " +
                 "Special Events and Restrictions, if any, are colored (see legend below). " +
                 "To display a different day's tee sheet, select the date from the calendar above. ");
         
         if (parm.constimesm > 1) {        // if consecutive tee times supported
            //out.println("<br>To make multiple consecutive tee times, select the number of tee times next to the ");
            //out.println("earliest time desired.  Then select that time.  The following time(s) must be available.");
         }
/*
      } else {

         out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"600\">");
         out.println("<tr><td align=\"left\"><font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Note:</b>&nbsp;&nbsp;This date is not yet available for members to make notifications.");
         out.println(" You are allowed to view this for planning purposes only.");
         
      }
*/
      out.println("</font></td></tr></table>");

      out.println("<font size=\"4\">");
      out.println("<p>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");

      if (!courseName1.equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
      }

    // show todays events, restrictions and blockers  
    out.println("<p>");
    if (!event1.equals( "" )) {
        
        out.println("<font size=2>(click on Event button to view info)</font>");
    }
    out.println("</font><font size=\"1\"><br>");
    
    if (!event1.equals( "" )) {
    
        out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?event=" +event1+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
        out.println("<button type=\"button\" style=\"background:" + ecolor1 + "\">" + event1 + "</button></a>");
        out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
        
        if (!event2.equals( "" )) {

            out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?event=" +event2+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button></a>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            
            if (!event3.equals( "" )) {
            
                out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?event=" +event3+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
                out.println("<button type=\"button\" style=\"background:" + ecolor3 + "\">" + event3 + "</button></a>");
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                
                if (!event4.equals( "" )) {
                
                    out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?event=" +event4+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
                    out.println("<button type=\"button\" style=\"background:" + ecolor4 + "\">" + event4 + "</button></a>");
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                }
            }
        }
    }
    
    if (!rest1.equals( "" )) {

        out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?rest=" +rest1+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
        out.println("<button type=\"button\" style=\"background:" + rcolor1 + "\">" + rest1 + "</button></a>");
        out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

        if (!rest2.equals( "" )) {

            out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?rest=" +rest2+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
            out.println("<button type=\"button\" style=\"background:" + rcolor2 + "\">" + rest2 + "</button></a>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

            if (!rest3.equals( "" )) {

                out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?rest=" +rest3+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
                out.println("<button type=\"button\" style=\"background:" + rcolor3 + "\">" + rest3 + "</button></a>");
                out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

                if (!rest4.equals( "" )) {

                    out.println("<a href=\"javascript:void(0)\" onclick=\"window.open('/" +rev+ "/servlet/MemberTLT_sheet?rest=" +rest4+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no, directories=no, status=no');return false;\">");
                    out.println("<button type=\"button\" style=\"background:" + rcolor4 + "\">" + rest4 + "</button></a>");
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                }
            }
        }
    }
    
    if ( !event1.equals( "" ) || !rest1.equals( "" ) ) {

        out.println("<br>");
    }
     // end display todays restrictions, events, blockers 
      
      out.println("</p></font><font size=\"2\">");

      //
      //  Display a note if members are not allowed to access tee times today
      //
      if (restrictAll == true) {

         out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the Golf Shop for any notifications today.</button><br>");
      }

      //int tmp_x;
      //
      // Display form for making new reservation
      //
      out.println("<table align=center>");
      
        out.println("<form action=\"/" +rev+ "/servlet/MemberTLT_slot\" method=\"post\" target=\"_top\" name=\"frmNewNotify\" id=\"frmNewNotify\">");
        
        out.println("<tr><td align=right>What time would you like the first tee?</td>");
        
        //out.println("<td><input type=text name=stime></td>");
        
        out.println("<td nowrap>");
        out.println("<input type=hidden name=stime value=''>");

        out.println("<select name=hr>");
        for (int tmp_x = 1; tmp_x <= 12; tmp_x++) {
            out.println("<option value=\"" + tmp_x + "\"" +
                    ( (tmp_x == start_hr) ? " selected" : "") +
                    ">" + tmp_x + "</option>");
        }
        out.println("</select>");
        
        out.println("<select name=min>");
        for (int tmp_x = 0; tmp_x < 60; tmp_x += tmp_interval) {
            out.println("<option value=\"" + SystemUtils.ensureDoubleDigit(tmp_x) + "\"" + ( (tmp_x == start_min) ? " selected" : "") + ">" + SystemUtils.ensureDoubleDigit(tmp_x) + "</option>");
        }
        out.println("</select>");
        
        out.println("<select name=ampm>");
        out.println("<option value=\"AM\"" + ( (start_ampm.equalsIgnoreCase("am")) ? " selected" : "") + ">AM");
        out.println("<option value=\"PM\"" + ( (start_ampm.equalsIgnoreCase("pm")) ? " selected" : "") + ">PM");
        out.println("</select>");
        
        out.println("</td>");

        if (courseName1.equals("-ALL-")) {
       
            out.println("<td>&nbsp; &nbsp;<select size=\"1\" name=\"course\">");

            for (i=0; i < course.size(); i++) {

                courseName = course.get(i);      // get first course name from array

                if (!courseName.equals( "-ALL-" )) {

                   out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
               }
            }
            out.println("</select></td>");
        } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
        }
        out.println("<td><input type=button value=\"Make New Request\" onclick=\"makeNotify()\"></td></tr>");
        
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        
        
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + ((fives != 0 && rest5.equals( "" )) ? "Yes" : "No") + "\">");  // tell _slot to do 5's
        
        out.println("</form>");
        
        
        out.println("<tr><td colspan=" + ((courseName1.equals("-ALL-")) ? "4" : "3") + " align=center><font size=2>" +
                "(Keep in mind that this is not a tee time, rather it is an approximate time that you would<br> " +
                "like to start your round of golf.&nbsp; The golf operations staff will do everything in<br> " +
                "their power to get your group as close to, or on, your requested time.)" +
                "<br>Notifications are accepted between the hours of " + fstart_time + " and " + fend_time + "." +
                "</font></td></tr>");
      
      out.println("</table>");
      
      out.println("<script type=\"text/javascript\">");
      out.println("function makeNotify() {");
      out.println(" var f = document.forms['frmNewNotify'];");
      out.println(" var t1 = f.hr.options[f.hr.selectedIndex].value;");
      out.println(" var t2 = f.min.options[f.min.selectedIndex].value;");
      out.println(" var t3 = f.ampm.options[f.ampm.selectedIndex].value;");
      out.println(" var t = '' + t1 + ':' + t2 + ' ' + t3;");
      out.println(" f.stime.value = t;");
      
      out.println(" var st = " + start_time + ";");
      out.println(" var et = " + end_time + ";");
      //out.println(" var time = ((((t1 < 12) ? t1 : (t1 + 12)) * 100) + t2);");
      
      out.println(" var time = 0;");
      out.println(" time = t1 - 0;");
      out.println(" if (t3 == 'PM' && t1 != 12) time = time + 12;");
      out.println(" if (t3 == 'AM' && t1 == 12) time = time - 12;");
      out.println(" time = time * 100;");
      out.println(" time = time + (t2 - 0);");
      out.println(" if ((time < st || time > et) && (st != 0 && et != 0)) { ");
      out.println("  alert(\"Notifications are not being accepting at this time, available hours are between " + fstart_time + " and " + fend_time + ".\\nYour requested time was \" + t + \".\");");
      out.println("  return;");
      out.println(" }");
      out.println(" f.submit();");
      out.println("}");
      out.println("function editNotify(pNotifyId, pTime) {");
      out.println(" var f = document.forms['frmEditNotify'];");
      out.println(" f.notifyId.value = pNotifyId;");
      out.println(" f.stime.value = pTime;");
      out.println(" f.submit();");
      out.println("}");
      out.println("</script>");
      
      out.println("<form method=\"post\" action=/" + rev + "/servlet/MemberTLT_slot name=\"frmEditNotify\" id=\"frmEditNotify\" target=\"_top\">");
      
        out.println("<input type=\"hidden\" name=\"stime\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
        out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        //out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + ((fives != 0 && rest5.equals( "" )) ? "Yes" : "No") + "\">");  // tell _slot to do 5's
        out.println("<input type=\"hidden\" name=\"notifyId\" value=\"\">");
        
      out.println("</form>");
      
      
    //
    // Display a legend for the notifications table
    //
    out.println("<p align=center style=\"font-size:18px;font-weight:bold\">" + ((index == 0) ? "Today's " : day_name + "'s ") + " Upcoming Notifications</p>");
/*
    out.println("<b>Legend</b>");
    out.println("<font size=\"1\">");
    out.println("<br><b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine" +
                "<br><b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");
    for (int ic=0; ic<16; ic++) {

        if (!parmc.tmodea[ic].equals( "" )) {
            out.print(parmc.tmodea[ic]+ " = " +parmc.tmode[ic]+ "&nbsp;&nbsp;&nbsp;");
        }
    }
    out.println("</font>");
*/

    //
    // Display existing notifcations from the notifications table
    //
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"95%\">"); 
        out.println("<tr bgcolor=\"#336633\">");
         out.println("<td align=\"center\" width=100>");
          out.println("<font color=\"#FFFFFF\" size=\"2\">");
          out.println("<u><b>Estimated Arrival</b></u>");
         out.println("</font></td>");

    if (courseName1.equals( "-ALL-" )) {
        out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<u><b>Course</b></u>");
        out.println("</font></td>");
    }
        /*out.println("<td align=\"center\" width=35>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<u><b>F/B</b></u>");
        out.println("</font></td>");*/
        
        out.println("<td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<u><b>Members</b></u>");
        out.println("</font></td>");
        
        out.println("<td align=\"center\" width=40>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<u><b>Holes</b></u>");
        out.println("</font></td>");
        
        out.println("<td align=\"center\" width=50>");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<u><b>Players</b></u>");
        out.println("</font></td>");
        
        
        /*
            SELECT n.*, c.courseName, DATE_FORMAT(n.req_datetime, "%l:%i %p") AS t, (
            SELECT COUNT(*) AS m 
            FROM notifications_players np
            WHERE np.notification_id = 1
            ) AS m, 
            (SELECT SUM(friends) AS f 
            FROM notifications_players np
            WHERE np.notification_id = 1
            ) AS f  
            FROM notifications n, clubparm2 c 
            LEFT OUTER JOIN notifications ON c.clubparm_id = n.course_id  
            WHERE DATE(n.req_datetime) = "2006-10-23" 
            ORDER BY n.req_datetime, c.courseName, n.fb;
        */
    
    
    String sql = "SELECT n.*, c.courseName, DATE_FORMAT(n.req_datetime, '%l:%i %p') AS req_time, " +
                 "(SELECT username FROM notifications_players np WHERE username = ? AND np.notification_id = n.notification_id LIMIT 1) AS allow " + 
                 "FROM notifications n, clubparm2 c " +
                 "WHERE DATE(n.req_datetime) = ? " +
                 ((index!=0) ? "" : "AND TIME(n.req_datetime) > TIME(?) ") +
                 ((!courseName1.equals( "-ALL-" )) ? " AND courseName = ? " : "") +
                 "AND c.clubparm_id = n.course_id " +
                 //"AND n.converted = 0 " + 
                 "ORDER BY n.req_datetime, c.courseName";
    
    /*
    String sql2 = "SELECT np.cw, np.9hole, CONCAT(m.name_first, ' ', m.name_last) AS full_name " +
                  "FROM notifications_players np " +
                  "LEFT JOIN member2b m ON np.username = m.username " +
                  "WHERE notification_id = ? " +
                  "ORDER BY pos";
    */
    
    String sql2 = "SELECT * " +
                  "FROM notifications_players " +
                  "WHERE notification_id = ? " +
                  "ORDER BY pos";
    
    PreparedStatement pstmt = con.prepareStatement (sql);
    
    pstmt.clearParameters();
    pstmt.setString(1, user);
    pstmt.setString(2, date_mysql);
    int x = 3;
    if (index == 0) { 
        pstmt.setString(x, time_mysql);
        x++; 
    }
    if (!courseName1.equals( "-ALL-" )) pstmt.setString(x, courseName1);
    
    rs = pstmt.executeQuery();

    boolean tmp_found = false;
    boolean tmp_found2 = false;
    int notification_id = 0;
    int nineHole = 0;
    int eighteenHole = 0;
    int friends = 0;
    int sum_players = 0;
    String req_time = "";
    String fullName = "";
    String cw = "";
    String holes = "";
    String belongs = "";

    while (rs.next()) {

        // reset variables
        allow = true;
        tmp_found = true;
        sum_players = 0;
        nineHole = 0;
        eighteenHole = 0;
        
        notification_id = rs.getInt("notification_id");
        courseName = rs.getString("courseName");
        req_time = rs.getString("req_time");
        belongs = rs.getString("allow"); // if this user is part of this notification, then belongs will contain their username
                
        out.println("<tr>");
        
        // if this notification has already been converted then disallow access to it
        if (rs.getInt("converted") == 1) allow = false;
            
        //  disallow access if this user is not part of this tee time
        if (belongs == null || !belongs.equals(user)) allow = false;
        
        // display either a button for selecting (editing) this notifcation, or just display the time
        if (allow) {
        
            out.println("<td align=center><font size=2><input type=button onclick=\"editNotify(" + notification_id + ", '" + req_time + "')\" value=\"" + req_time + "\"></font></td>");
        } else {
            
            if (rs.getInt("converted") == 1) {
                out.println("<td align=center><font size=2><b>" + req_time + "*</b></font></td>");
            } else {
                out.println("<td align=center><font size=2>" + req_time + "</font></td>");
            }
        }
        
        if (courseName1.equals( "-ALL-" )) out.println("<td><font size=2>" + courseName + "</font></td>");
        
        //out.println("<td align=center bgcolor=white><font size=2>" + sfb + "</font></td>");
        
        out.println("<td><font size=2>");
        
        PreparedStatement pstmt2 = con.prepareStatement (sql2);
        pstmt2.clearParameters();
        pstmt2.setInt(1, notification_id);
        rs2 = pstmt2.executeQuery();      // execute the prepared stmt
        tmp_found2 = false;
        
        while (rs2.next()) {
        
            fullName = rs2.getString("player_name");
            cw = rs2.getString("cw");
            
            // see if we need to hide player names
            // we hide names when hideName=1 and when allow if false (allow will be true if user belongs to this notifications)
            if (hideNames == 1 && !allow) {
                
                i=0;
                gloop:
                while (i < parm.MAX_Guests) {
                    if (fullName.startsWith( parm.guest[i] )) {

                        fullName = "Guest";
                        break gloop;
                    }
                    i++;
                }
                
                // if the player was not changed to guest then change to member
                if (!fullName.equals("Guest")) fullName = "Member";
                
            }
            
            if (rs2.getInt("9hole") == 1) nineHole = 1;
            if (rs2.getInt("9hole") == 0) eighteenHole = 1;
            
            if (tmp_found2) out.print(",&nbsp; "); 
            else out.print("&nbsp;");
            
            out.print(fullName + " <font size=1>(" + cw + ((rs2.getInt("9hole") == 1) ? "9" : "") + ")</font>");
            
            sum_players++;
            tmp_found2 = true;
        }
        
        if (nineHole == 1 && eighteenHole == 1) {
            holes = "mixed";
        } else if (nineHole == 1) {
            holes = "9";
        } else if (eighteenHole == 1) {
            holes = "18";
        }
        
        out.println("</font></td>");
        
        out.println("<td align=center bgcolor=white><font size=2>" + holes + "</font></td>");
        
        out.println("<td align=center bgcolor=white><font size=2>" + sum_players + "</font></td>");
        
        out.println("</tr>");
    }
    
    out.println("</table>");
    
    out.println("<p>&nbsp;</p>");
    //
    // End display notifications
    //
    
    if (true) { // index == 0  // always show tee sheet
    //
    // Display legend for the tee sheet
    //
    out.println("<p align=center style=\"font-size: 18px; font-weight:bold\">");
    if (index == 0) {
        out.println("Today's Scheduled Rounds</p>");
    } else {
        out.println(day_name + "'s Scheduled Rounds</p>");
    }
    out.println("<b>Legend</b>");
    out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;" +
                "O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event<br>" +
                "<b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");

     for (int ic=0; ic<16; ic++) {

        if (!parmc.tmodea[ic].equals( "" )) {
           out.print(parmc.tmodea[ic]+ " = " +parmc.tmode[ic]+ "&nbsp;&nbsp;&nbsp;");
        }
     }
     out.println("(__9 = 9 holes)");
     
     
     // 
     // Display the Tee Sheet w/ data from teecurr2
     //
     out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"95%\">");    // tee sheet table
        out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
              out.println("<font color=\"#FFFFFF\" size=\"2\">");
              out.println("<u><b>Time</b></u>");
              out.println("</font></td>");

           if (parm.constimesm > 1) {      // if Consecutive Tee Times allowed

              out.println("<td align=\"center\">");
                 out.println("<font color=\"#FFFFFF\" size=\"2\">");
                 out.println("<u><b>#</b></u>");
              out.println("</font></td>");
           }

           if (courseName1.equals( "-ALL-" )) {

              out.println("<td align=\"center\">");
                 out.println("<font color=\"#FFFFFF\" size=\"2\">");
                 out.println("<u><b>Course</b></u>");
              out.println("</font></td>");
           }

           out.println("<td align=\"center\">");
              out.println("<font color=\"#FFFFFF\" size=\"1\">");
              out.println("<u><b>F/B</b></u>");
              out.println("</font></td>");

           out.println("<td align=\"center\" nowrap>");
              out.println("<font color=\"#FFFFFF\" size=\"2\">");
              out.println("<u><b>Player 1</b></u> ");
              if (disp_hndcp == false) {
                 out.println("&nbsp;");
              } else {
                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
              }
              out.println("</font></td>");

           out.println("<td align=\"center\">");
              out.println("<font color=\"#FFFFFF\" size=\"1\">");
              out.println("<u><b>C/W</b></u>");
              out.println("</font></td>");

           out.println("<td align=\"center\" nowrap>");
              out.println("<font color=\"#FFFFFF\" size=\"2\">");
              out.println("<u><b>Player 2</b></u> ");
              if (disp_hndcp == false) {
                 out.println("&nbsp;");
              } else {
                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
              }
              out.println("</font></td>");

           out.println("<td align=\"center\">");
              out.println("<font color=\"#FFFFFF\" size=\"1\">");
              out.println("<u><b>C/W</b></u>");
              out.println("</font></td>");

           out.println("<td align=\"center\" nowrap>");
              out.println("<font color=\"#FFFFFF\" size=\"2\">");
              out.println("<u><b>Player 3</b></u> ");
              if (disp_hndcp == false) {
                 out.println("&nbsp;");
              } else {
                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
              }
              out.println("</font></td>");

           out.println("<td align=\"center\">");
              out.println("<font color=\"#FFFFFF\" size=\"1\">");
              out.println("<u><b>C/W</b></u>");
              out.println("</font></td>");

           out.println("<td align=\"center\" nowrap>");
              out.println("<font color=\"#FFFFFF\" size=\"2\">");
              out.println("<u><b>Player 4</b></u> ");
              if (disp_hndcp == false) {
                 out.println("&nbsp;");
              } else {
                 out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
              }
              out.println("</font></td>");

           out.println("<td align=\"center\">");
              out.println("<font color=\"#FFFFFF\" size=\"1\">");
              out.println("<u><b>C/W</b></u>");
              out.println("</font></td>");

           if (fivesALL != 0 ) {

              out.println("<td align=\"center\" nowrap>");
                 out.println("<font color=\"#FFFFFF\" size=\"2\">");
                 out.println("<u><b>Player 5</b></u> ");
                 if (disp_hndcp == false) {
                    out.println("&nbsp;");
                 } else {
                    out.println("</font><font color=\"#FFFFFF\" size=\"1\"><u>hndcp</u>");
                 }
                 out.println("</font></td>");

              out.println("<td align=\"center\">");
                 out.println("<font color=\"#FFFFFF\" size=\"1\">");
                 out.println("<u><b>C/W</b></u>");
                 out.println("</font></td>");
           }
           out.println("</tr>");

     //
     //  Get the tee sheet for this date and course
     //
     String stringTee = "";

     if (courseName1.equals( "-ALL-" )) {

        stringTee = "SELECT * " +
                    "FROM teecurr2 WHERE date = ? AND player1 <> '' ORDER BY time, courseName, fb"; // AND time < ? 
     } else {
        stringTee = "SELECT * " +
                    "FROM teecurr2 WHERE date = ? AND courseName = ? AND player1 <> '' ORDER BY time, fb"; // AND time < ? 
     }
     out.println("<!-- " + stringTee + " -->");
     out.println("<!-- " + date + " | " + cal_time + " -->");
     pstmt = con.prepareStatement (stringTee);

     pstmt.clearParameters();
     pstmt.setLong(1, date);
     //pstmt.setLong(2, cal_time);

     if (!courseName1.equals( "-ALL-" )) {
         
        pstmt.setString(2, courseName1);
     }

     rs = pstmt.executeQuery();      // execute the prepared stmt

     loop1:
     while (rs.next()) {

        hr = rs.getInt("hr");
        min = rs.getInt("min");
        tee_time = rs.getInt("time");
        event = rs.getString("event");
        ecolor = rs.getString("event_color");
        rest = rs.getString("restriction");
        rcolor = rs.getString("rest_color");
        player1 = rs.getString("player1");
        player2 = rs.getString("player2");
        player3 = rs.getString("player3");
        player4 = rs.getString("player4");
        user1 = rs.getString("username1");
        user2 = rs.getString("username2");
        user3 = rs.getString("username3");
        user4 = rs.getString("username4");
        p1cw = rs.getString("p1cw");
        p2cw = rs.getString("p2cw");
        p3cw = rs.getString("p3cw");
        p4cw = rs.getString("p4cw");
        in_use = rs.getInt("in_use");
        type = rs.getInt("event_type");
        hndcp1 = rs.getFloat("hndcp1");
        hndcp2 = rs.getFloat("hndcp2");
        hndcp3 = rs.getFloat("hndcp3");
        hndcp4 = rs.getFloat("hndcp4");
        fb = rs.getShort("fb");
        player5 = rs.getString("player5");
        user5 = rs.getString("username5");
        p5cw = rs.getString("p5cw");
        hndcp5 = rs.getFloat("hndcp5");
        courseNameT = rs.getString("courseName");
        blocker = rs.getString("blocker");
        rest5 = rs.getString("rest5");
        bgcolor5 = rs.getString("rest5_color");
        userg1 = rs.getString("userg1");
        userg2 = rs.getString("userg2");
        userg3 = rs.getString("userg3");
        userg4 = rs.getString("userg4");
        userg5 = rs.getString("userg5");
        orig_by = rs.getString("orig_by");
        p91 = rs.getInt("p91");
        p92 = rs.getInt("p92");
        p93 = rs.getInt("p93");
        p94 = rs.getInt("p94");
        p95 = rs.getInt("p95");
        hole = rs.getString("hole");

        //
        //  If course=ALL requested, then set 'fives' option according to this course
        //
        if (courseName1.equals( "-ALL-" )) {
           i = 0;
           loopall:
           while (i < course.size()) {
              if (courseNameT.equals( course.get(i) )) {
                 fives = fivesA.get(i);          // get the 5-some option for this course
                 break loopall;              // exit loop
              }
              i++;
           }
        }

        //*************************************************************
        //  Check for 2-some only times
        //*************************************************************
        //
        twoSomeOnly = false;             // init the flag

        
        //
        //****************************************************************************************
        //  Hide Names Feature - if club opts to hide the member names, then hide all names
        //                       except for any group that this user is part of.
        //****************************************************************************************
        //
        hideN = 0;           // default to 'do not hide names'
        hideSubmit = 0;      // default to 'do not hide the submit button'

        if (hideNames > 0) {

          hideN = 1;        // hide names in this group

          if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) ||
              user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

             hideN = 0;                // do not hide this group

          } else {    // user not part of this tee time

             //
             //  Check if any members exist in this tee time.  If so, then do not allow this member access
             //
             if (!user1.equals("") || !user2.equals("") || !user3.equals("") ||
                 !user4.equals("") || !user5.equals("")) {

                hideSubmit = 1;                // do not allow access to tee time
             }
             
          }
          
        } // end if hideNames

        //
        //  determine if we should skip this slot
        //
        if (blocker.equals( "" )) {    // continue if tee time not blocked & not lottery - else skip

           ampm = " AM";
           if (hr == 12) {
              ampm = " PM";
           }
           if (hr > 12) {
              ampm = " PM";
              hr = hr - 12;    // convert to conventional time
           }

           bgcolor = "#F5F5DC";                //default

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

           if (bgcolor5.equals( "" )) {
              bgcolor5 = bgcolor;              //same as others if not specified
           }

           if (p91 == 1) {          // if 9 hole round
              p1cw = p1cw + "9";
           }
           if (p92 == 1) {
              p2cw = p2cw + "9";
           }
           if (p93 == 1) {
              p3cw = p3cw + "9";
           }
           if (p94 == 1) {
              p4cw = p4cw + "9";
           }
           if (p95 == 1) {
              p5cw = p5cw + "9";
           }

           if (player1.equals("")) {
              p1cw = "";
           }
           if (player2.equals("")) {
              p2cw = "";
           }
           if (player3.equals("")) {
              p3cw = "";
           }
           if (player4.equals("")) {
              p4cw = "";
           }
           if (player5.equals("")) {
              p5cw = "";
           }

           g1 = 0;          // init guest indicators
           g2 = 0;
           g3 = 0;
           g4 = 0;
           g5 = 0;

           //
           //  Check if any player names are guest names
           //
           if (!player1.equals( "" )) {

              i = 0;
              ploop1:
              while (i < parm.MAX_Guests) {
                 if (player1.startsWith( parm.guest[i] )) {

                    g1 = 1;       // indicate player1 is a guest name
                    break ploop1;
                 }
                 i++;
              }
           }
           if (!player2.equals( "" )) {

              i = 0;
              ploop2:
              while (i < parm.MAX_Guests) {
                 if (player2.startsWith( parm.guest[i] )) {

                    g2 = 1;       // indicate player2 is a guest name
                    break ploop2;
                 }
                 i++;
              }
           }
           if (!player3.equals( "" )) {

              i = 0;
              ploop3:
              while (i < parm.MAX_Guests) {
                 if (player3.startsWith( parm.guest[i] )) {

                    g3 = 1;       // indicate player3 is a guest name
                    break ploop3;
                 }
                 i++;
              }
           }
           if (!player4.equals( "" )) {

              i = 0;
              ploop4:
              while (i < parm.MAX_Guests) {
                 if (player4.startsWith( parm.guest[i] )) {

                    g4 = 1;       // indicate player4 is a guest name
                    break ploop4;
                 }
                 i++;
              }
           }
           if (!player5.equals( "" )) {

              i = 0;
              ploop5:
              while (i < parm.MAX_Guests) {
                 if (player5.startsWith( parm.guest[i] )) {

                    g5 = 1;       // indicate player5 is a guest name
                    break ploop5;
                 }
                 i++;
              }
           }

           //
           //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
           //
           sfb = "F";       // default Front 9
           sfb2 = "Front";       // default Front 9

           if (fb == 1) {

              sfb = "B";
              sfb2 = "Back";
           }

           if (fb == 9) {

              sfb = "O";
              sfb2 = "O";
           }

           if (type == shotgun) {

              //sfb = "S";            // there's an event and its type is 'shotgun'
              sfb = (!hole.equals("")) ? hole : "";            // there's an event and its type is 'shotgun'
           }

           //
           // check if we should allow user to select this slot
           //
           // check max allowed days in advance (normal time and lottery time), special event & cross-over time
           //
           //  allow 90 days in adv for Milwaukee !!!!!
           //
           allow = true;

           //
           //  Save the Days in Advance value
           //
           int tempDays = days;        // days in adv for test

           //
           //  Check if members are not allowed to access tee times today
           //
           if (restrictAll == true) {

              allow = false;            // not today!
           }

           if (allow == true) {     // if still ok, check more

              //
              // if restriction for this slot, check restriction for this member
              //
              if ( !rest.equals("") ) {

                 ind = 0;                                            // init fields

                 while (ind < parmr.MAX && allow == true) {                 // check all possible restrictions

                    if (parmr.stime[ind] <= tee_time && parmr.etime[ind] >= tee_time) {                // matching time ?

                       if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( courseNameT ))) {  // course ?

                          if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb2 ))) {    // matching f/b ?

                             //
                             //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                             //
                             if (event.equals("")) {           // change color if no event

                                if (!parmr.color[ind].equals("Default")) {     // if not default

                                   bgcolor = parmr.color[ind];

                                   if (bgcolor5.equals( "" )) {
                                      bgcolor5 = bgcolor;         // same as others if not specified
                                   }
                                }
                             }
                             allow = false;                    // match found
                          }
                       }
                    }
                    ind++;
                 }               // end of while (loop2)
              }                  // end of if rest exists in teecurr
           }

           if (allow == true) {     // if still ok, check more

              if (!event.equals("") || fb == 9) {    // event or cross-over ?

                 allow = false;
              }
           }

           if (allow == true) {         // if still ok

              //
              //  if all spots taken and this player is not one of them or assigned to guest,
              //  or did not originate the request, do not allow select
              //
              if (twoSomeOnly == true) {      // if 2-some ONLY time

                 if (!player1.equals( "" ) && !player2.equals( "" )) {

                    if (!player1.equals( full_name ) && !player2.equals( full_name )) {

                       if (!userg1.equals( user ) && !userg2.equals( user )) {

                          allow = false;     // all spots taken and this player not one of them
                       }
                    }
                 }

              } else {         // normal tee time

                 if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

                    if ((!player1.equals( "" )) && (!player2.equals( "" )) &&
                        (!player3.equals( "" )) && (!player4.equals( "" )) && (!player5.equals( "" ))) {

                       if ((!player1.equals( full_name )) && (!player2.equals( full_name )) &&
                           (!player3.equals( full_name )) && (!player4.equals( full_name )) && (!player5.equals( full_name ))) {

                          if (!userg1.equals( user ) && !userg2.equals( user ) && !userg3.equals( user ) &&
                              !userg4.equals( user ) && !userg5.equals( user ) && !orig_by.equals( user )) {

                             allow = false;     // all spots taken and this player not one of them
                          }
                       }
                    }
                 } else {        // 4-some time

                    if ((!player1.equals( "" )) && (!player2.equals( "" )) &&
                        (!player3.equals( "" )) && (!player4.equals( "" ))) {

                       if ((!player1.equals( full_name )) && (!player2.equals( full_name )) &&
                           (!player3.equals( full_name )) && (!player4.equals( full_name ))) {

                          if (!userg1.equals( user ) && !userg2.equals( user ) && !userg3.equals( user ) &&
                              !userg4.equals( user ) && !orig_by.equals( user )) {

                             allow = false;     // all spots taken and this player not one of them
                          }
                       }
                    }
                 }
              }

              //
              //  if today's sheet and the tee time is less than the current time do not allow select
              //
              if ((index2 == 0) && (tee_time <= cal_time)) {

                 allow = false;     // do not allow select
              }

              //
              //  if today's sheet and the tee time is less than the stop time do not allow select (custom)
              //
              if (allow == true && index2 == 0 && stop_time > 0) {

                 if (tee_time <= stop_time) {

                    allow = false;     // do not allow select
                 }
              }

           }

           submit = "time:" + fb;       // create a name for the submit button

           out.println("<tr>");         // start of tee slot (row)

           j++;                                       // increment the jump label index (where to jump on page)
           out.println("<a name=\"jump" + j + "\"></a>"); // create a jump label for 'noshow' returns

           if ((allow == true) && (in_use == 0)) {         // can user select this slot and not in use?
              
               out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
           }

           if ((allow == true) && (in_use == 0) && (hideSubmit == 0)) {         // if still ok

              out.println("<td align=\"center\" nowrap>");
              out.println("<font size=\"2\">");
              out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
              out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
              out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
              out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">");
              out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");
              out.println("<input type=\"hidden\" name=\"wasP1\" value=\"" +player1+ "\">");
              out.println("<input type=\"hidden\" name=\"wasP2\" value=\"" +player2+ "\">");
              out.println("<input type=\"hidden\" name=\"wasP3\" value=\"" +player3+ "\">");
              out.println("<input type=\"hidden\" name=\"wasP4\" value=\"" +player4+ "\">");
              out.println("<input type=\"hidden\" name=\"wasP5\" value=\"" +player5+ "\">");
              out.println("<input type=\"hidden\" name=\"p5\" value=\"" + ((fives != 0 && rest5.equals( "" )) ? "Yes" : "No") + "\">");
              out.println("<input type=\"submit\" name=\"" + submit + "\" id=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
              out.println("</font></td>");

              //
              //  if Consecutive Tee Times allowed, no event, and tee time is empty,
              //   and F or B tee, allow member to select more than one tee time
              //
              if (parm.constimesm > 1) {

                 out.println("<td align=\"center\">");

                 if (allow == true && player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) &&
                     player4.equals( "" ) && player5.equals( "" ) &&
                     event.equals("") && (fb == 0 || fb == 1)) {

                    out.println("<select size=\"1\" name=\"contimes\">");
                    out.println("<option value=\"01\">01</option>");
                    out.println("<option value=\"02\">02</option>");
                    if (parm.constimesm > 2) {
                       out.println("<option value=\"03\">03</option>");
                    }
                    if (parm.constimesm > 3) {
                       out.println("<option value=\"04\">04</option>");
                    }
                    if (parm.constimesm > 4) {
                       out.println("<option value=\"05\">05</option>");
                    }
                    out.println("</select>");

                 } else {

                    out.println("&nbsp;");
                 }
                 out.println("</td>");
              }
              out.println("</form>");

           } else {
               
              out.println("<td align=\"center\" nowrap>");
              out.println("<font size=\"2\">");
              if (type == shotgun) {

                if (event.equals(event1)) {
                    out.print(SystemUtils.getSimpleTime(event1_start_hr, event1_start_min));

                } else {

                    if (event.equals(event2)) {
                        out.print(SystemUtils.getSimpleTime(event2_start_hr, event2_start_min));

                    } else {

                        if (event.equals(event3)) {
                            out.print(SystemUtils.getSimpleTime(event3_start_hr, event3_start_min));

                        } else {

                            if (event.equals(event4)) {
                                out.print(SystemUtils.getSimpleTime(event4_start_hr, event4_start_min));

                            }

                        }

                    }

                }

                out.println(" Shotgun");

              } else {

                  out.print(hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm);

              }
              out.println("</font></td>");

              if (parm.constimesm > 1) {
                 out.println("<td align=\"center\">");
                 out.println("&nbsp;");
                 out.println("</td>");
              }
           }

           //
           //  Course Name
           //
           if (courseName1.equals( "-ALL-" )) {

              // set tmp_i equal to course index #
              tmp_i = course.indexOf(courseNameT);     // get the index value of this course
                      
              if (tmp_i >= colorMax) tmp_i = (colorMax - 1);      // default to White if exceeds # of colors

              out.println("<td bgcolor=\"" + colors.course_color[tmp_i] + "\" align=\"center\">");
              out.println("<font size=\"2\">");
              out.println(courseNameT);
              out.println("</font></td>");
           }

           //
           //  Front/Back indicator
           //
           out.println("<td bgcolor=\"white\" align=\"center\">");
              out.println("<font size=\"2\">");
              out.println(sfb);
              out.println("</font></td>");

           //
           //  Add Players
           //
           buildPlayerName(player1, bgcolor, disp_hndcp, hndcp1, g1, hideN, p1cw, out);
           buildPlayerName(player2, bgcolor, disp_hndcp, hndcp2, g2, hideN, p2cw, out);
           buildPlayerName(player3, bgcolor, disp_hndcp, hndcp3, g3, hideN, p3cw, out);
           buildPlayerName(player4, bgcolor, disp_hndcp, hndcp4, g4, hideN, p4cw, out);
           
           if (fivesALL != 0) {        // if 5-somes supported on any course

               if (fives != 0) {        // if 5-somes on this course 

                   if (!rest5.equals( "" )) {       // if 5-somes are restricted
                       
                       out.println("<td bgcolor=\"" + bgcolor5 + "\" align=\"center\">");
                       out.println("&nbsp;");
                   } else { 
                       
                       buildPlayerName(player5, bgcolor, disp_hndcp, hndcp5, g5, hideN, p5cw, out);
                   }
               } else {        

                 // 5-somes supported on at least 1 course, but not this one (if course=ALL)
                 out.println("<td bgcolor=\"black\">&nbsp;</td>");
                 out.println("<td bgcolor=\"black\">&nbsp;</td>");
               }
           }
           
           out.println("</tr>"); 

        }  // end of IF blocker

     }  // end of while loop1

     pstmt.close();

     out.println("</table>");                   // end of tee sheet table

     } // end if index == 0
    
     out.println("</td></tr>");
     out.println("</table>");                   // end of main page table
     //
     //  End of HTML page
     //
     out.println("</center><p>&nbsp;</p></body></html>");
     out.close();
     if (Gzip == true) {
        resp.setContentLength(buf.size());                 // set output length
        resp.getOutputStream().write(buf.toByteArray());
     }
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      if (Gzip == true) {
         resp.setContentLength(buf.size());                 // set output length
         resp.getOutputStream().write(buf.toByteArray());
      }
   }
 }  // end of doPost

 
 // *********************************************************
 //  Display event information in new pop-up window
 // *********************************************************

 public void displayEvent(String name, PrintWriter out, Connection con) {

   ResultSet rs = null;
     
   int year = 0;
   int month = 0;
   int day = 0;
   int act_hr = 0;
   int act_min = 0;
   int signUp = 0;
   int type = 0;
   int holes = 0;
   int max = 0;
   int size = 0;
   int guests = 0;
   int teams = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;
     
   String course = "";
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String c_ampm = "";
   String act_ampm = "";
   String fb = "";
     
   //
   //  Locate the event and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM events2b " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         type = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         course = rs.getString("courseName");
         signUp = rs.getInt("signUp");
         format = rs.getString("format");
         pairings = rs.getString("pairings");
         size = rs.getInt("size");
         max = rs.getInt("max");
         guests = rs.getInt("guests");
         memcost = rs.getString("memcost");
         gstcost = rs.getString("gstcost");
         c_month = rs.getInt("c_month");
         c_day = rs.getInt("c_day");
         c_year = rs.getInt("c_year");
         c_time = rs.getInt("c_time");
         itin = rs.getString("itin");
         holes = rs.getInt("holes");
         fb = rs.getString("fb");

      } else {           // name not found - try filtering it

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
            type = rs.getInt("type");
            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");
            course = rs.getString("courseName");
            signUp = rs.getInt("signUp");
            format = rs.getString("format");
            pairings = rs.getString("pairings");
            size = rs.getInt("size");
            max = rs.getInt("max");
            guests = rs.getInt("guests");
            memcost = rs.getString("memcost");
            gstcost = rs.getString("gstcost");
            c_month = rs.getInt("c_month");
            c_day = rs.getInt("c_day");
            c_year = rs.getInt("c_year");
            c_time = rs.getInt("c_time");
            itin = rs.getString("itin");
            holes = rs.getInt("holes");
            fb = rs.getString("fb");
         }
      }
      stmt.close();

      //
      //  Create time values
      //
      act_ampm = "AM";

      if (act_hr == 0) {

         act_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (act_hr == 12) {

            act_ampm = "PM";         // change to Noon
         }
      }
      if (act_hr > 12) {

         act_hr = act_hr - 12;
         act_ampm = "PM";             // change to 12 hr clock
      }

      c_hr = c_time / 100;
      c_min = c_time - (c_hr * 100);

      c_ampm = "AM";

      if (c_hr == 0) {

         c_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (c_hr == 12) {

            c_ampm = "PM";         // change to Noon
         }
      }
      if (c_hr > 12) {

         c_hr = c_hr - 12;
         c_ampm = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Member Event Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"3\">");
      out.println("Event: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("<b>Date:</b>&nbsp;&nbsp; " + month + "/" + day + "/" + year);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (act_min < 10) {
         out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":0" + act_min + " " + act_ampm);
      } else {
         out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + act_min + " " + act_ampm);
      }
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (type != 0) {
         out.println("<b>Type:</b>&nbsp;&nbsp; Shotgun<br><br>");
      } else {
         out.println("<b>Type:</b>&nbsp;&nbsp; Tee Times<br><br>");
      }

      if (!course.equals( "" )) {

         out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

      out.println("<b>Format:</b>&nbsp;&nbsp; " + format + "<br><br>");
      out.println("<b>Pairings by:</b>&nbsp;&nbsp; " + pairings);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes + "<br><br>");
      out.println("<b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "<br><br>");
           
      if (signUp != 0) {       // if members can sign up

         if (c_min < 10) {
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":0" + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         } else {
            out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         }
         out.println("<br><br>");
         out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");

         out.println("<br><br>");
         out.println("To register for this event click on the <b>Events</b> tab after closing this window.<br>");
        
      } else {
  
           
         out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
         out.println("<br><br>");
         out.println("Online sign up was not selected for this event.");
      }
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");
      //
      //  End of HTML page
      //
      out.println("<p align=\"center\"><br><form>");
      out.println("<input type=\"button\" value=\"CLOSE\" onClick='self.close();'>");
      out.println("</form></p>");
      out.println("</font></td>");
      out.println("</tr></table>");
      out.println("</center></font></body></html>");
      out.close();
        
   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }

 
 // *********************************************************
 //  Display restriction information in new pop-up window
 // *********************************************************

 private void displayRest(String name, PrintWriter out, Connection con) {
     
   ResultSet rs = null;

   int year1 = 0;
   int month1 = 0;
   int day1 = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int i = 0;

   String course = "";
   String recurr = "";
   String fb = "";
   String ampm1 = "AM";
   String ampm2 = "AM";

   String [] mtype = new String [8];                     // member types
   String [] mship = new String [8];                     // membership types

   //
   //  Locate the event and display the content
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM restriction2 " +
         "WHERE name = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);
      rs = stmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month1 = rs.getInt("start_mm");
         day1 = rs.getInt("start_dd");
         year1 = rs.getInt("start_yy");
         hr1 = rs.getInt("start_hr");
         min1 = rs.getInt("start_min");
         month2 = rs.getInt("end_mm");
         day2 = rs.getInt("end_dd");
         year2 = rs.getInt("end_yy");
         hr2 = rs.getInt("end_hr");
         min2 = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         mtype[0] = rs.getString("mem1");
         mtype[1] = rs.getString("mem2");
         mtype[2] = rs.getString("mem3");
         mtype[3] = rs.getString("mem4");
         mtype[4] = rs.getString("mem5");
         mtype[5] = rs.getString("mem6");
         mtype[6] = rs.getString("mem7");
         mtype[7] = rs.getString("mem8");
         mship[0] = rs.getString("mship1");
         mship[1] = rs.getString("mship2");
         mship[2] = rs.getString("mship3");
         mship[3] = rs.getString("mship4");
         mship[4] = rs.getString("mship5");
         mship[5] = rs.getString("mship6");
         mship[6] = rs.getString("mship7");
         mship[7] = rs.getString("mship8");
         course = rs.getString("courseName");
         fb = rs.getString("fb");

      } else {         // not found - try filtering the name

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            month1 = rs.getInt("start_mm");
            day1 = rs.getInt("start_dd");
            year1 = rs.getInt("start_yy");
            hr1 = rs.getInt("start_hr");
            min1 = rs.getInt("start_min");
            month2 = rs.getInt("end_mm");
            day2 = rs.getInt("end_dd");
            year2 = rs.getInt("end_yy");
            hr2 = rs.getInt("end_hr");
            min2 = rs.getInt("end_min");
            recurr = rs.getString("recurr");
            mtype[0] = rs.getString("mem1");
            mtype[1] = rs.getString("mem2");
            mtype[2] = rs.getString("mem3");
            mtype[3] = rs.getString("mem4");
            mtype[4] = rs.getString("mem5");
            mtype[5] = rs.getString("mem6");
            mtype[6] = rs.getString("mem7");
            mtype[7] = rs.getString("mem8");
            mship[0] = rs.getString("mship1");
            mship[1] = rs.getString("mship2");
            mship[2] = rs.getString("mship3");
            mship[3] = rs.getString("mship4");
            mship[4] = rs.getString("mship5");
            mship[5] = rs.getString("mship6");
            mship[6] = rs.getString("mship7");
            mship[7] = rs.getString("mship8");
            course = rs.getString("courseName");
            fb = rs.getString("fb");
         }
      }
      stmt.close();

      //
      //  Create time values
      //
      if (hr1 == 0) {
         hr1 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr1 == 12) {
            ampm1 = "PM";         // change to Noon
         }
      }
      if (hr1 > 12) {
         hr1 = hr1 - 12;
         ampm1 = "PM";             // change to 12 hr clock
      }

      if (hr2 == 0) {
         hr2 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr2 == 12) {
            ampm2 = "PM";         // change to Noon
         }
      }
      if (hr2 > 12) {
         hr2 = hr2 - 12;
         ampm2 = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Member Restriction Information"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
        
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\" valign=\"top\">");
      out.println("<font size=\"3\">");
      out.println("Restriction: <b>" + name + "</b>");
      out.println("</font></td></tr>");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\"><br><br>");
      out.println("<b>Start Date of Restriction:</b>&nbsp;&nbsp; " + month1 + "/" + day1 + "/" + year1);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min1 < 10) {
         out.println("<b>Start Time:</b>&nbsp;&nbsp; " + hr1 + ":0" + min1 + " " + ampm1);
      } else {
         out.println("<b>Start Time:</b>&nbsp;&nbsp; " + hr1 + ":" + min1 + " " + ampm1);
      }
      out.println("<br><br><b>End Date of Restriction:</b>&nbsp;&nbsp; " + month2 + "/" + day2 + "/" + year2);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      if (min2 < 10) {
         out.println("<b>End Time:</b>&nbsp;&nbsp; " + hr2 + ":0" + min2 + " " + ampm2);
      } else {
         out.println("<b>End Time:</b>&nbsp;&nbsp; " + hr2 + ":" + min2 + " " + ampm2);
      }

      out.println("<br><br><b>Recurrence:</b>&nbsp;&nbsp; " +recurr+ "<br><br>");

      if (!course.equals( "" )) {

         out.println("<b>Course:</b>&nbsp;&nbsp; " + course);
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      }

      out.println("<b>Front/Back Tees:</b>&nbsp;&nbsp; " + fb + "<br><br>");

      // if any member types specified
      if (!mtype[0].equals("") || !mtype[1].equals("") || !mtype[2].equals("") || !mtype[3].equals("") ||
          !mtype[4].equals("") || !mtype[5].equals("") || !mtype[6].equals("") || !mtype[7].equals("")) {

         out.println("<b>Member Types Restricted:</b>");
         if (!mtype[0].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[0]);
         }
         if (!mtype[1].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[1]);
         }
         if (!mtype[2].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[2]);
         }
         if (!mtype[3].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[3]);
         }
         if (!mtype[4].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[4]);
         }
         if (!mtype[5].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[5]);
         }
         if (!mtype[6].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[6]);
         }
         if (!mtype[7].equals("")) {
            out.println("&nbsp;&nbsp; " +mtype[7]);
         }
         out.println("<br><br>");
      }

      // if any membership types specified
      if (!mship[0].equals("") || !mship[1].equals("") || !mship[2].equals("") || !mship[3].equals("") ||
          !mship[4].equals("") || !mship[5].equals("") || !mship[6].equals("") || !mship[7].equals("")) {

         out.println("<b>Membership Types Restricted:</b>");
         if (!mship[0].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[0]);
         }
         if (!mship[1].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[1]);
         }
         if (!mship[2].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[2]);
         }
         if (!mship[3].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[3]);
         }
         if (!mship[4].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[4]);
         }
         if (!mship[5].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[5]);
         }
         if (!mship[6].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[6]);
         }
         if (!mship[7].equals("")) {
            out.println("&nbsp;&nbsp; " +mship[7]);
         }
         out.println("<br><br>");
      }

      //
      //  End of HTML page
      //
      out.println("</font></td></tr></table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }
 
 
 private static void buildPlayerName(String playerName, String bgcolor, boolean disp_hndcp, float hndcp, int guest, int hideN, String trans, PrintWriter out) {
     
    out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
    out.println("<font size=\"2\">");

    if (!playerName.equals("")) {

        if (hideN == 0) {             // if ok to display names

            if (playerName.equalsIgnoreCase("x") || guest != 0) {   // if 'x' or guest

                out.println(playerName);

            } else {       // not 'x' or guest

                out.print(playerName);
                
                if (disp_hndcp) {
                    
                    if ((hndcp == 99) || (hndcp == -99)) {
                        
                        out.print("  NH");
                    } else {
                        
                        if (hndcp <= 0) { hndcp = 0 - hndcp; }
                        int tmp = Math.round(hndcp);
                        out.print("  " + tmp);
                    }
                }
            }

        } else {                        
        
            // do not display member names
            if (playerName.equalsIgnoreCase("x")) {

                out.println("X");

            } else {

                if (guest != 0) { out.println("Guest"); } 
                else { out.println("Member"); }

            }

        }

    } else {     // player is empty

        out.println("&nbsp;");
    }
    out.println("</font></td>");
    
    // diplay their transportation mode
    out.println("<td bgcolor=\"white\" align=\"center\">");

    if ((!playerName.equals("")) && (!playerName.equalsIgnoreCase( "x" ))) {
      
      out.println("<font size=\"2\">");
      out.println(trans);
      out.println("</font>");
    
    } else {
        
      out.println("&nbsp;");
    
    }
    
    out.println("</td>");
     
 }
 
} // end servlet
