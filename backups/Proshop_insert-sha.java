/***************************************************************************************
 *   Proshop_insert:  This servlet will process the 'Edit Tee Time' request from
 *                    the Proshop's Tee Sheet page (Control Panel).
 *
 *
 *   called by:  Proshop_sheet (link - doGet)
 *               Proshop_insert (self - doPost)
 *
 *
 *   parms passed (on doGet):  
 *                  'index'       = the date index (0 = today, 1 = tomorrow, etc.)
 *                  'course'      = name of the course for this sheet
 *                  'insert=yes'  = if user selected 'Insert Tee Time' from edit menu
 *                  'delete=yes'  = if user selected 'Delete Tee Time' from edit menu
 *                  'setBlockers' = if present the setBlockers method is run
 *                  'blockers'    = if present the doBlockers method is run
 *
 *      secondary calls (from self to doPost):
 *                   (refer to dopost method)
 *
 *
 *   created: 4/30/2002   Bob P.
 *
 *   last updated:
 *
 *        5/09/06   Added menu option to Contol Panel 
 *        4/26/06   In doBlockers put quotes around course hidden parm values to include the entire course name.
 *        3/29/06   In moveWhole and moveSingle do not rely on verifySlot.checkInUse to get tee time data.
 *        3/15/06   Added menu option to Control Panel for managing blockers. (doBlockers, setBlockers methods)
 *        1/05/05   Add email=yes/no option to send or not to send emails.
 *        9/18/04   V5 - Change getClub from SystemUtils to common.
 *        8/20/04   Add fivesAll option to indicate if 5-somes are supported on any course (course=ALL).
 *        6/11/04   RDP Add an 'ALL' option for multiple course facilities.
 *        2/17/04   Add email notifications.
 *        2/10/04   Add support for configurable transportation modes.
 *        1/22/04   Add resp.getHeader controls to doGet processing to prevent 
 *                  blank page results when clicking on 'insert' or 'delete'.
 *       12/22/03   Enhancements for Version 4 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
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
import com.foretees.common.parmSlot;
import com.foretees.common.parmClub;
import com.foretees.common.verifySlot;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;


public class Proshop_insert extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   static String host = SystemUtils.HOST;

   static String efrom = SystemUtils.EFROM;

   static String header = SystemUtils.HEADER;

   static String trailer = SystemUtils.TRAILER;

   String delim = "_";

   
 //****************************************************************************
 // Process the request from Proshop_sheet or below after a move,
 // change, insert or delete - display the tee sheet
 //
 //   This doGet method will build the tee sheet and process the
 //   insert and delete requests from that sheet.
 //
 //   parms:  index  - used to calculate the date (0=today, 1=tomorrow, etc.)
 //           course - name of course
 //           day    - name of day
 //           jump   - optional for refreshing the sheet
 //
 //****************************************************************************
 //

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setHeader("Pragma", "no-cache");      // these 3 added to fix 'blank screen' problem
   resp.setHeader("Cache-Control", "no-cache");
   resp.setDateHeader("Expires", 0);
   resp.setContentType("text/html");
   PrintWriter out;

   PreparedStatement pstmtc = null;
   Statement stmt = null;
   Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  use GZip (compression) if supported by browser
   //
   String encodings = req.getHeader("Accept-Encoding");               // browser encodings

   if ((encodings != null) && (encodings.indexOf("gzip") != -1)) {    // if browser supports gzip

      OutputStream out1 = resp.getOutputStream();
      out = new PrintWriter(new GZIPOutputStream(out1), false);       // use compressed output stream
      resp.setHeader("Content-Encoding", "gzip");                     // indicate gzip

   } else {

      out = resp.getWriter();                                         // normal output stream
   }

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      out.println(SystemUtils.HeadTitle("Access Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>System Access Error</H3>");
      out.println("<BR><BR>You have entered this site incorrectly or have lost your session cookie.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   Connection con = SystemUtils.getCon(session);                     // get DB connection

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
   //  Check for 'Insert' or 'Delete' request
   //
   if (req.getParameter("insert") != null) {

      doInsert(req, out, con, session);          // process insert request
      return;
   }
   if (req.getParameter("delete") != null) {

      doDelete(req, out, con, session);        // process delete request
      return;
   }

   if (req.getParameter("setBlockers") != null) {

      setBlockers(req, out, con, session);        // process block request
   }
   
   if (req.getParameter("blockers") != null) {

      doBlockers(req, out, con, session);        // display blockers tee sheet
      out.close();
      return;
   }
   
   
   if (req.getParameter("ssha") != null) {

      setHoleAssignments(req, out, con, session);        // process hole assignments
      out.close();
      return;
   }
   
   if (req.getParameter("sha") != null) {

      doHoleAssignments(req, out, con, session);        // display hole assignment tee sheet
      out.close();
      return;
   }
   
   
   
   //*******************************************************************
   //
   //   Call is to build or refresh the tee sheet
   //
   //*******************************************************************
   //
   //   get name of club for this user
   //
   String club = (String)session.getAttribute("club");      // get club name

   int count = 0;
   int p = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;
   int in_use = 0;                         // event type
   int shotgun = 1;                      // event type = shotgun
   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;
   short hideNotes = 0;
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String rest_recurr = "";
   String rest5 = "";
   String bgcolor5 = "";
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
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
   String ampm = "";
   String event_rest = "";
   String bgcolor = "";
   String stime = "";
   String sshow = "";
   String sfb = "";
   String submit = "";
   String num = "";
   String jumps = "";

   String event1 = "";       // for legend - max 2 events, 4 rest's, 2 lotteries
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
   String blocker = "";
   String bag = "";
   String conf = "";
   String orig_by = "";
   String orig_name = "";
   String errMsg = "";

   //
   //  Lottery information storage area
   //
   //    lottery calculations done only once so we don't have to check each time while building sheet
   //
   String lottery = "";
   String lottery_color = "";
   String lottery_recurr = "";

   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int slots = 0;
   int curr_time = 0;
   int lskip = 0;
   int lstate = 0;           // lottery state
   int templstate = 0;       // temp lottery state

   String lott1 = "";        // name
   String lcolor1 = "";      // color
   int sdays1 = 0;           // days in advance to start taking requests
   int sdtime1 = 0;          // time of day to start taking requests
   int edays1 = 0;           // days in advance to stop taking requests
   int edtime1 = 0;          // time of day to stop taking requests
   int pdays1 = 0;           // days in advance to process the lottery
   int ptime1 = 0;           // time of day to process the lottery
   int slots1 = 0;           // # of consecutive groups allowed
   int lskip1 = 0;           // skip tee time displays
   int lstate1 = 0;          // lottery state
                               //    1 = before time to take requests (too early for requests)
                               //    2 = after start time, before stop time (ok to take requests)
                               //    3 = after stop time, before process time (late, but still ok for pro)
                               //    4 = requests have been processed but not approved (no new tee times now)
                               //    5 = requests have been processed & approved (ok for all tee times now)
                               //

   String lott2 = "";        // ditto for 2nd lottery on this day
   String lcolor2 = "";
   int sdays2 = 0;
   int sdtime2 = 0;
   int edays2 = 0;
   int edtime2 = 0;
   int pdays2 = 0;
   int ptime2 = 0;
   int slots2 = 0;
   int lskip2 = 0;
   int lstate2 = 0;

   String lott3 = "";        // ditto for 3rd lottery on this day (max of 3 for now)!!!!!!
   String lcolor3 = "";
   int sdays3 = 0;
   int sdtime3 = 0;
   int edays3 = 0;
   int edtime3 = 0;
   int pdays3 = 0;
   int ptime3 = 0;
   int slots3 = 0;
   int lskip3 = 0;
   int lstate3 = 0;

   int lyear = 0;
   int lmonth = 0;
   int lday = 0;
   int advance_days = 0;       // copy of 'index' = # of days between today and the day of this sheet


   // **************** end of lottery save area ***********

   int j = 0;
   int jump = 0;

   int index = 0;
   int wa = 0;
   int pc = 0;
   int ca = 0;
   int mc = 0;
   int i = 0;
   int fives = 0;
   int fivesALL = 0;
   int g1 = 0;                  // guest indicators (1 per player)
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int st2 = 2;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //  Array to hold the course names
   //
   String [] courseA = new String [20];             // max of 20 courses per club
   String courseName = "";
   String courseT = "";

   int [] fivesA = new int [20];                  // array to hold 5-some option for each course

   //
   //  Get the golf course name requested and the name of the day passed
   //
   String course = req.getParameter("course");
     
   if (course == null) {
     
      course = "";    // change to null string
   }

   String emailOpt = req.getParameter("email");

   //
   //  get the jump parm if provided (location on page to jump to)
   //
   if (req.getParameter("jump") != null) {

      jumps = req.getParameter("jump");         //  jump index value for where to jump to on the page
      jump = Integer.parseInt(jumps);
   }

   //
   //   Adjust jump so we jump to the selected line minus 14 so its not on top of page
   //
   if (jump > 14) {

      jump = jump - 14;

   } else {

      jump = 0;         // jump to top of page
   }

   //
   //    'index' contains an index value representing the date selected
   //    (0 = today, 1 = tomorrow, etc.)
   //
   num = req.getParameter("index");         // get the index value of the day selected

   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(num);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  save the index value for lottery computations
   //
   advance_days = index;

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   try {
      
      errMsg = "Get course names.";
        
      //
      // Get the Guest Types from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

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
      //   Get course names if multi-course facility so we can determine if any support 5-somes
      //
      i = 0;
      if (parm.multi != 0) {           // if multiple courses supported for this club

         while (i < 20) {

            courseA[i] = "";       // init the course array
            i++;
         }
         i = 0;

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2");

         while (rs.next() && i < 20) {

            courseName = rs.getString(1);

            courseA[i] = courseName;      // add course name to array
            i++;
         }
         stmt.close();
      }

      //
      //  Get the walk/cart options available and 5-some support
      //
      i = 0;
      if (course.equals( "-ALL-" )) {

         //
         //  Check all courses for 5-some support
         //
         loopc:
         while (i < 20) {

            courseName = courseA[i];       // get a course name

            if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

               if (courseName.equals( "" )) {      // done if null
                  break loopc;
               }
               getParms.getCourse(con, parmc, courseName);
            
               fivesA[i] = parmc.fives;      // get fivesome option
               if (fivesA[i] == 1) {
                  fives = 1;
               }
            }
            i++;
         }

      } else {       // single course requested

         getParms.getCourse(con, parmc, course);

         fives = parmc.fives;      // get fivesome option
      }
        
      fivesALL = fives;            // save 5-somes option for table display below

      i = 0;

      //
      //   Statements to find any restrictions, events or lotteries for today
      //
      String string7b = "";
      String string7c = "";
      String string7d = "";

      if (course.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes'";
      } else {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes'";
      }

      if (course.equals( "-ALL-" )) {
         string7c = "SELECT name, color FROM events2b WHERE date = ?";
      } else {
         string7c = "SELECT name, color FROM events2b WHERE date = ? " +
                    "AND (courseName = ? OR courseName = '-ALL-')";
      }

      if (course.equals( "-ALL-" )) {
         string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                    "FROM lottery3 WHERE sdate <= ? AND edate >= ?";
      } else {
         string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                    "FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-')";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);
      PreparedStatement pstmt7d = con.prepareStatement (string7d);

      errMsg = "Scan Restrictions.";

      //
      //  Scan the events, restrictions and lotteries to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7b.setString(3, course);
      }

      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);

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
      }                  // end of while
      pstmt7b.close();

      errMsg = "Scan Events.";

      pstmt7c.clearParameters();          // clear the parms
      pstmt7c.setLong(1, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7c.setString(2, course);
      }

      rs = pstmt7c.executeQuery();      // find all matching events, if any

      while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);

         if ((!event.equals( event1 )) && (event1.equals( "" ))) {

            event1 = event;
            ecolor1 = ecolor;

            if (ecolor.equalsIgnoreCase( "default" )) {

               ecolor1 = "#F5F5DC";
            }

          } else {

            if ((!event.equals( event1 )) && (!event.equals( event2 )) && (event2.equals( "" ))) {

               event2 = event;
               ecolor2 = ecolor;

               if (ecolor.equalsIgnoreCase( "default" )) {

                  ecolor2 = "#F5F5DC";
               }
            }
         }

      }                  // end of while
      pstmt7c.close();

      errMsg = "Scan for Lotteries.";

      //
      //  check for lotteries
      //
      pstmt7d.clearParameters();          // clear the parms
      pstmt7d.setLong(1, date);
      pstmt7d.setLong(2, date);
      if (!course.equals( "-ALL-" )) {
         pstmt7d.setString(3, course);
      }

      rs = pstmt7d.executeQuery();      // find all matching lotteries, if any

      while (rs.next()) {

         lottery = rs.getString(1);
         lottery_recurr = rs.getString(2);
         lottery_color = rs.getString(3);
         sdays = rs.getInt(4);
         sdtime = rs.getInt(5);
         edays = rs.getInt(6);
         edtime = rs.getInt(7);
         pdays = rs.getInt(8);
         ptime = rs.getInt(9);
         slots = rs.getInt(10);

         //
         //  We must check the recurrence for this day (Monday, etc.)
         //
         if ((lottery_recurr.equals( "Every " + day_name )) ||          // if this day
             (lottery_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
             ((lottery_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
               (!day_name.equalsIgnoreCase( "saturday" )) &&
               (!day_name.equalsIgnoreCase( "sunday" ))) ||
             ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
              (day_name.equalsIgnoreCase( "saturday" ))) ||
             ((lottery_recurr.equalsIgnoreCase( "all weekends" )) &&
              (day_name.equalsIgnoreCase( "sunday" )))) {


            if ((!lottery.equals( lott1 )) && (lott1.equals( "" ))) {

               lott1 = lottery;
               lcolor1 = lottery_color;
               sdays1 = sdays;
               sdtime1 = sdtime;
               edays1 = edays;
               edtime1 = edtime;
               pdays1 = pdays;
               ptime1 = ptime;
               slots1 = slots;

               if (lottery_color.equalsIgnoreCase( "default" )) {

                  lcolor1 = "#F5F5DC";
               }

            } else {

               if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (lott2.equals( "" ))) {

                  lott2 = lottery;
                  lcolor2 = lottery_color;
                  sdays2 = sdays;
                  sdtime2 = sdtime;
                  edays2 = edays;
                  edtime2 = edtime;
                  pdays2 = pdays;
                  ptime2 = ptime;
                  slots2 = slots;

                  if (lottery_color.equalsIgnoreCase( "default" )) {

                     lcolor2 = "#F5F5DC";
                  }
               } else {

                  if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (!lottery.equals( lott3 )) && (lott3.equals( "" ))) {

                     lott3 = lottery;
                     lcolor3 = lottery_color;
                     sdays3 = sdays;
                     sdtime3 = sdtime;
                     edays3 = edays;
                     edtime3 = edtime;
                     pdays3 = pdays;
                     ptime3 = ptime;
                     slots3 = slots;

                     if (lottery_color.equalsIgnoreCase( "default" )) {

                        lcolor3 = "#F5F5DC";
                     }
                  }
               }
            }
         }
      }                  // end of while
      pstmt7d.close();

      //
      //  Process the lotteries if there are any for this day
      //
      //    Determine which state we are in (before req's, during req's, before process, after process)
      //
      String string12 = "";

      errMsg = "Process the Lotteries.";

      if (course.equals( "-ALL-" )) {

         string12 = "SELECT state FROM lreqs3 " +
                    "WHERE name = ? AND date = ?";
      } else {
         string12 = "SELECT state FROM lreqs3 " +
                    "WHERE name = ? AND date = ? AND courseName = ?";
      }

      if (!lott1.equals( "" )) {

         //
         //  Get the current time
         //
         Calendar cal3 = new GregorianCalendar();    // get todays date
         int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time)
         int cal_min = cal3.get(Calendar.MINUTE);

         curr_time = (cal_hour * 100) + cal_min;

         curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

         if (curr_time < 0) {          // if negative, then we went back or ahead one day

            curr_time = 0 - curr_time;        // convert back to positive value
         }

         //
         //  now check the day and time values
         //
         if (advance_days > sdays1) {       // if we haven't reached the start day yet

            lstate1 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays1) {   // if this is the start day

               if (curr_time >= sdtime) {   // have we reached the start time?

                  lstate1 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate1 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate1 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays1) {   // if this is the stop day

               if (curr_time >= edtime) {   // have we reached the stop time?

                  lstate1 = 3;              // after stop time, before process time
               }
            }

            if (advance_days < edays1) {   // if we are past the stop day

               lstate1 = 3;                // after stop time, before process time
            }
         }

         if (lstate1 == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays1) {   // if this is the process day

               if (curr_time >= ptime) {    // have we reached the process time?

                  lstate1 = 4;              // after process time
               }
            }

            if (advance_days < pdays1) {   // if we are past the process day

               lstate1 = 4;                // after process time
            }
         }

         if (lstate1 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement(string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott1);
            pstmt12.setLong(2, date);
            if (!course.equals( "-ALL-" )) {
               pstmt12.setString(3, course);
            }
            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate1 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate1 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott1

      if (!lott2.equals( "" )) {

         //
         //  check the day and time values
         //
         if (advance_days > sdays2) {       // if we haven't reached the start day yet

            lstate2 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays2) {   // if this is the start day

               if (curr_time >= sdtime) {   // have we reached the start time?

                  lstate2 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate2 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate2 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays2) {   // if this is the stop day

               if (curr_time >= edtime) {   // have we reached the stop time?

                  lstate2 = 3;              // after stop time, before process time
               }
            }

            if (advance_days < edays2) {   // if we are past the stop day

               lstate2 = 3;                // after stop time, before process time
            }
         }

         if (lstate2 == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays2) {   // if this is the process day

               if (curr_time >= ptime) {    // have we reached the process time?

                  lstate2 = 4;              // after process time
               }
            }

            if (advance_days < pdays2) {   // if we are past the process day

               lstate2 = 4;                // after process time
            }
         }

         if (lstate2 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement(string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott2);
            pstmt12.setLong(2, date);
            if (!course.equals( "-ALL-" )) {
               pstmt12.setString(3, course);
            }
            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate2 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate2 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott2

      if (!lott3.equals( "" )) {

         //
         //  check the day and time values
         //
         if (advance_days > sdays3) {       // if we haven't reached the start day yet

            lstate3 = 1;                    // before time to take requests

         } else {

            if (advance_days == sdays3) {   // if this is the start day

               if (curr_time >= sdtime) {   // have we reached the start time?

                  lstate3 = 2;              // after start time, before stop time to take requests

               } else {

                  lstate3 = 1;              // before time to take requests
               }
            } else {                        // we are past the start day

               lstate3 = 2;                 // after start time, before stop time to take requests
            }

            if (advance_days == edays3) {   // if this is the stop day

               if (curr_time >= edtime) {   // have we reached the stop time?

                  lstate3 = 3;              // after stop time, before process time
               }
            }

            if (advance_days < edays3) {   // if we are past the stop day

               lstate3 = 3;                // after stop time, before process time
            }
         }

         if (lstate3 == 3) {                // if we are now in state 3, check for state 4

            if (advance_days == pdays3) {   // if this is the process day

               if (curr_time >= ptime) {    // have we reached the process time?

                  lstate3 = 4;              // after process time
               }
            }

            if (advance_days < pdays3) {   // if we are past the process day

               lstate3 = 4;                // after process time
            }
         }

         if (lstate3 == 4) {                // if we are now in state 4, check for pending approval

            PreparedStatement pstmt12 = con.prepareStatement(string12);

            pstmt12.clearParameters();        // clear the parms
            pstmt12.setString(1, lott3);
            pstmt12.setLong(2, date);
            if (!course.equals( "-ALL-" )) {
               pstmt12.setString(3, course);
            }
            rs = pstmt12.executeQuery();

            if (!rs.next()) {             // if none waiting approval

               lstate3 = 5;              // state 5 - after process & approval time

            } else {                     // still some reqs waiting

               templstate = rs.getInt(1);  // get its state

               if (templstate == 5) {      // if we processed already (some not assigned)

                  lstate3 = 5;
               }
            }
            pstmt12.close();

         }
      }   // end of if lott3


      //****************************************************
      // Define tee sheet size and build it                        Paul......
      //****************************************************

      // define our two arrays that describe the column sizes
      // index = column number, value = size in pixels
      int [] col_width = new int [15];
      int col_start[] = new int[15];                    // total width = 962 px (in dts-styles.css)
        
      if (course.equals( "-ALL-" )) {
         col_width[0] = 0;        // unused
         col_width[1] = 40;       // +/-
         col_width[2] = 71;       // time
         col_width[3] = 69;       // course name col
         col_width[4] = 31;       // f/b
         col_width[5] = 111;        // player 1
         col_width[6] = 39;       // player 1 trans opt
         col_width[7] = 111;        // player 2
         col_width[8] = 39;       // player 2 trans opt
         col_width[9] = 111;        // player 3
         col_width[10] = 39;      // player 3 trans opt
         col_width[11] = 111;       // player 4
         col_width[12] = 39;      // player 4 trans opt
         col_width[13] = 111;       // player 5
         col_width[14] = 39;      // player 5 trans opt
      } else {
         col_width[0] = 0;
         col_width[1] = 40;
         col_width[2] = 80;
         col_width[3] = 0;              // empty if no course name
         col_width[4] = 40;
         col_width[5] = 120;
         col_width[6] = 40;
         col_width[7] = 120;
         col_width[8] = 40;
         col_width[9] = 120;
         col_width[10] = 40;
         col_width[11] = 120;
         col_width[12] = 40;
         col_width[13] = 120;
         col_width[14] = 40;
      }
      col_start[1] = 0;
      col_start[2] = col_start[1] + col_width[1];
      col_start[3] = col_start[2] + col_width[2];
      col_start[4] = col_start[3] + col_width[3];
      col_start[5] = col_start[4] + col_width[4];
      col_start[6] = col_start[5] + col_width[5];
      col_start[7] = col_start[6] + col_width[6];
      col_start[8] = col_start[7] + col_width[7];
      col_start[9] = col_start[8] + col_width[8];
      col_start[10] = col_start[9] + col_width[9];
      col_start[11] = col_start[10] + col_width[10];
      col_start[12] = col_start[11] + col_width[11];
      col_start[13] = col_start[12] + col_width[12];
      col_start[14] = col_start[13] + col_width[13];


      // temp variable
      String dts_tmp = "";

      //
      //  Build the HTML page
      //
      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
      out.println("<html>\n<!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) ");
      out.println("is the proprietary property of ForeTees, LLC or its suppliers and its use, modification and distribution are protected ");
      out.println("and limited by United States copyright laws and international treaty provisions and all other applicable national laws. ");
      out.println("\nReproduction is strictly prohibited.-->");
      out.println("<head>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");

      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/dts-styles.css\">");

      out.println("<title>Proshop Edit Tee Sheet Page </title>");

      out.println("<script language=\"javascript\">");          // Jump script
      out.println("<!--");
      out.println("function jumpToHref(anchorstr) {");
      out.println("if (location.href.indexOf(anchorstr)<0) {");
      out.println(" location.href=anchorstr; }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");                               // End of script

      // include dts javascript source file
      out.println("<script language=\"javascript\" src=\"/" +rev+ "/dts-scripts.js\"></script>");

      out.println("</head>");

      out.println("<body onLoad='jumpToHref(\"#jump" + jump + "\");' bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // table for cmd tbl & instructions
      out.println("<tr><td align=\"left\" valign=\"middle\">");

         //
         //  Build Control Panel
         //
         out.println("<table border=\"1\" width=\"130\" cellspacing=\"3\" cellpadding=\"3\" bgcolor=\"8B8970\" align=\"left\">");
         out.println("<tr>");
         out.println("<td align=\"center\"><font size=\"3\"><b>Control Panel</b><br>");
         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +index+ "&course=" +course+ "&email=" +emailOpt+ "\" title=\"Refresh This Page\" alt=\"Refresh\">");
         out.println("Refresh This Sheet</a><br>");

         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +index+ "&course=" +course+ "&blockers&email=" +emailOpt+ "\" title=\"Block or Unblock Tee Times\" alt=\"Blockers\">");
         out.println("Block or Unblock Tee Times</a><br>");
         
         // add code here to conditionally show this menu option (only if there is a shotgun event scheduled for this day)
         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +index+ "&course=" +course+ "&sha&email=" +emailOpt+ "\" title=\"Shotgun Hole Assignments\" alt=\"Shotgun\">");
         out.println("Shotgun Hole Assignments</a><br>");

         out.println("</font></td></tr><tr><td align=\"center\"><font size=\"2\">");
         out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\" title=\"Return to Tee Sheet\" alt=\"Return\">");
         out.println("<nobr>Return to Tee Sheet</nobr></a><br>");
         
         out.println("</font></td></tr></table>");

      out.println("</td>");                                 // end of column for control panel
      out.println("<td align=\"center\" width=\"20\">&nbsp;");     // empty column for spacer

      out.println("</td>");
      out.println("<td align=\"left\" valign=\"top\">");     // column for calendars & course selector

         //**********************************************************
         //  Continue with instructions and tee sheet
         //**********************************************************

         out.println("<table cellpadding=\"3\" cellspacing=\"3\" width=\"80%\">");
         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"center\">");
         out.println("<p align=\"center\"><font size=\"4\">Golf Shop Edit Tee Sheet</font></p>");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>insert</b> a new tee time, ");
         out.println("click on the red plus sign <b>'+'</b> in the tee time you wish to insert <b>after</b>. ");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>delete</b> a tee time, click on the red minus sign <b>'-'</b> in the tee time you wish to delete. ");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>move an entire tee time</b>, click on the 'Time' value and drag the tee time to the new position.");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>move an individual player</b>, click on the Player and drag the name to the new position.");
         out.println("</td></tr>");

         out.println("<tr><td bgcolor=\"#CCCCAA\" align=\"left\" nowrap>");
         out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("To <b>change</b> the F/B value or the C/W value, just click on it and make the selection.");
         out.println("</font></td></tr></table>");

      out.println("</font></td></tr></table>");

      out.println("<font size=\"2\">");
      out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
      if (!course.equals( "" )) {
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
      }
      out.println("</font><font size=\"2\"><br><br>");
      out.println("<b>Tee Sheet Legend</b>");
      out.println("</font><br><font size=\"1\">");

      if (!event1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + ecolor1 + "\">" + event1 + "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!event2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         }
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
      }

      if (!lott1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + lcolor1 + "\">Lottery Times</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!lott2.equals( "" )) {

            out.println("<button type=\"button\" style=\"background:" + lcolor2 + "\">Lottery Times</button>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

            if (!lott3.equals( "" )) {

               out.println("<button type=\"button\" style=\"background:" + lcolor3 + "\">Lottery Times</button>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
            }
         }
      }

      if (!event1.equals( "" ) || !rest1.equals( "" ) || !lott1.equals( "" )) {
         out.println("<br>");
      }
      out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;");
      out.println("O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event");
      out.println("</font>");

    // *** these two lines came up from after tee sheet
      out.println("</td></tr>");
      out.println("</table>");                            // end of main page table
      out.println("</center>");

    //****************************************************************************
    //
    // start html for tee sheet
    //
    //****************************************************************************
    //
    //  To change the position of the tee sheet (static position from top):
    //
    //   Edit file 'dts-styles.css'
    //                     "top" property for elHContainer (header for the main container)
    //                     "top" property for elContainer (main container that holds the tee sheet elements)
    //                     Increment both numbers equally!!!!!!!!!!!!
    //
    //****************************************************************************
  
    String tmpCW = "C/W";
    out.println("<br>");
    out.println("\n<!-- START OF TEE SHEET HEADER -->");
    out.println("<div id=\"elHContainer\" align=center>");
    out.println("<span class=header style=\"left: " +col_start[1]+ "px; width: " +col_width[1]+ "px\">+/-</span><span");
    out.println(" class=header style=\"left: " +col_start[2]+ "px; width: " +col_width[2]+ "px\">Time</span><span");
    if (course.equals( "-ALL-" )) {
       out.println(" class=header style=\"left: " +col_start[3]+ "px; width: " +col_width[3]+ "px\">Course</span><span ");
       //tmpCW = "CW";
    }
    out.println(" class=header style=\"left: " +col_start[4]+ "px; width: " +col_width[4]+ "px\">F/B</span><span ");
    out.println(" class=header style=\"left: " +col_start[5]+ "px; width: " +col_width[5]+ "px\">Player 1</span><span ");
    out.println(" class=header style=\"left: " +col_start[6]+ "px; width: " +col_width[6]+ "px\">" +tmpCW+ "</span><span ");
    out.println(" class=header style=\"left: " +col_start[7]+ "px; width: " +col_width[7]+ "px\">Player 2</span><span ");
    out.println(" class=header style=\"left: " +col_start[8]+ "px; width: " +col_width[8]+ "px\">" +tmpCW+ "</span><span ");
    out.println(" class=header style=\"left: " +col_start[9]+ "px; width: " +col_width[9]+ "px\">Player 3</span><span ");
    out.println(" class=header style=\"left: " +col_start[10]+ "px; width: " +col_width[10]+ "px\">" +tmpCW+ "</span><span ");
    out.println(" class=header style=\"left: " +col_start[11]+ "px; width: " +col_width[11]+ "px\">Player 4</span><span ");
    out.print(" class=header style=\"left: " +col_start[12]+ "px; width: " +col_width[12]+ "px\"");
  
    if (fivesALL == 0)
    {
       out.print(" id=widthMarker>" +tmpCW+"</span>");
    } else {
       out.print(">" +tmpCW+ "</span><span \n class=header style=\"left: " +col_start[13]+ "px; width: " +col_width[13]+ "px\">Player 5</span><span ");
       out.print(" \n class=header style=\"left: " +col_start[14]+ "px; width: " +col_width[14]+ "px\" id=widthMarker>" +tmpCW+ "</span>");
    }

    out.print("</div>\n");
    out.println("<!-- END OF TEE SHEET HEADER -->\n");

    int dts_slot_index = 0; // slot index number
    String first = "yes";

      errMsg = "Get tee times.";

      //
      //  Get the tee sheet for this date
      //
      String stringTee = "";

      if (course.equals( "-ALL-" )) {

         stringTee = "SELECT " +
                     "hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, event_type, " +
                     "fb, player5, p5cw, lottery, courseName, " +
                     "blocker, rest5, rest5_color, lottery_color, conf, p91, p92, p93, p94, p95 " +
                     "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
      } else {
         stringTee = "SELECT " +
                     "hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, event_type, " +
                     "fb, player5, p5cw, lottery, courseName, " +
                     "blocker, rest5, rest5_color, lottery_color, conf, p91, p92, p93, p94, p95 " +
                     "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";
      }
      PreparedStatement pstmt = con.prepareStatement (stringTee);

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      if (!course.equals( "-ALL-" )) {
         pstmt.setString(2, course);
      } 
      rs = pstmt.executeQuery();      // execute the prepared stmt

      out.println("\n<!-- START OF TEE SHEET BODY -->");
      out.println("<div id=\"elContainer\">\n");

      // loop thru each of the tee times

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
         p1cw = rs.getString(12);
         p2cw = rs.getString(13);
         p3cw = rs.getString(14);
         p4cw = rs.getString(15);
         in_use = rs.getInt(16);
         type = rs.getInt(17);
         fb = rs.getShort(18);
         player5 = rs.getString(19);
         p5cw = rs.getString(20);
         lottery = rs.getString(21);
         courseT = rs.getString(22);
         blocker = rs.getString(23);
         rest5 = rs.getString(24);
         bgcolor5 = rs.getString(25);
         lottery_color = rs.getString(26);
         conf = rs.getString(27);
         p91 = rs.getInt(28);
         p92 = rs.getInt(29);
         p93 = rs.getInt(30);
         p94 = rs.getInt(31);
         p95 = rs.getInt(32);

         //
         //  If course=ALL requested, then set 'fives' option according to this course
         //
         if (course.equals( "-ALL-" )) {
            i = 0;
            loopall:
            while (i < 20) {
               if (courseT.equals( courseA[i] )) {
                  fives = fivesA[i];          // get the 5-some option for this course
                  break loopall;              // exit loop
               }
               i++;
            }
         }

         lskip = 0;                      // init skip switch

         //
         //  if not event, then check for lottery time (events override lotteries)
         //
         //  determine if we should skip this slot - display only one slot per lottery before its processed
         //
         if (event.equals("") && !lottery.equals("")) {

            if (lottery.equals( lott1 )) {

               if (lstate1 < 5) {             // if lottery has not been processed (times allotted)

                  if (lskip1 != 0) {          // if we were here already

                     lskip = 1;               // skip this slot
                  }
                  lskip1 = 1;                 // make sure its set now
               }
            } else {

               if (lottery.equals( lott2 )) {

                  if (lstate2 < 5) {

                     if (lskip2 != 0) {          // if we were here already

                        lskip = 1;               // skip this slot
                     }
                     lskip2 = 1;                 // make sure its set now
                  }
               } else {

                  if (lottery.equals( lott3 )) {

                     if (lstate3 < 5) {

                        if (lskip3 != 0) {          // if we were here already

                           lskip = 1;               // skip this slot
                        }
                        lskip3 = 1;                 // make sure its set now
                     }
                  }
               }
            }
         }          // end of IF lottery

         if (blocker.equals( "" ) && lskip == 0) {    // continue if tee time not blocked & not lottery - else skip

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            bgcolor = "#FFFFFF";               //default

            if (!event.equals("")) {
               bgcolor = ecolor;
            } else {

               if (!rest.equals("")) {
                  bgcolor = rcolor;
               } else {

                  if (!lottery_color.equals("")) {
                     bgcolor = lottery_color;
                  }
               }
            }

            if (bgcolor.equals("Default")) {
               bgcolor = "#FFFFFF";              //default
            }

            if (bgcolor5.equals("")) {
               bgcolor5 = bgcolor;               // player5 bgcolor = others if 5-somes not restricted
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

            g1 = 0;     // init guest indicators
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

            if (fb == 1) {

               sfb = "B";
            }

            if (fb == 9) {

               sfb = "O";
            }

            if (type == shotgun) {

               sfb = "S";            // there's an event and its type is 'shotgun'
            }

            // set default color for first three columns
            String dts_defaultF3Color = "#FFFFFF"; // default
            if (in_use != 0) dts_defaultF3Color = "";
      
            //
            //**********************************
            //  Build the tee time rows
            //**********************************
            //

            if (min < 10) {
               dts_tmp = hr + ":0" + min + ampm;
            } else {
               dts_tmp = hr + ":" + min + ampm;
            }

            out.print("<div id=time_slot_"+ dts_slot_index +" time=\"" + time + "\" course=\"" + courseT + "\" startX=0 startY=0 ");
            if (in_use == 0) {
              // not in use
              out.println("class=timeSlot drag=true style=\"background-color: "+ bgcolor +"\" bgc=\""+ bgcolor +"\">");
            } else {
              // in use
              out.println("class=timeSlotInUse>");
            }


            // col for 'insert' and 'delete' requests
            out.print(" <span id=time_slot_" + dts_slot_index + "_A class=cellDataB style=\"cursor: default; left: " + col_start[1] + "px; width: " + col_width[1] + "px; background-color: #FFFFFF\">");
            j++;                                           // increment the jump label index (where to jump on page)
            out.print("<a name=\"jump" + j + "\"></a>");     // create a jump label for returns
            out.print("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&first=" +first+ "&insert=yes\" title=\"Insert a time slot after this one\" alt=\"Insert a tee time\">");
            out.print("<img src=/" +rev+ "/images/dts_insert.gif width=13 height=13 border=0></a>");

            if (in_use == 0) {
              // not in use
              out.print("<img src=/" +rev+ "/images/shim.gif width=5 height=1 border=0>");
              out.print("<a href=\"/" +rev+ "/servlet/Proshop_insert?index=" +index+ "&course=" +courseT+ "&returnCourse=" +course+ "&time=" +time+ "&fb=" +fb+ "&jump=" +j+ "&email=" +emailOpt+ "&delete=yes\" title=\"Delete this time slot from the database\" alt=\"Remove this tee time\">");
              out.print("<img src=/" +rev+ "/images/dts_delete.gif width=13 height=13 border=0></a>");
            } else {
              // in use
              out.print("<img src=/" +rev+ "/images/shim.gif width=18 height=1 border=0>");
            }
            out.println("</span>");

            // time column
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_time class=cellData hollow=true style=\"left: " + col_start[2] + "px; width: " + col_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_time class=cellData style=\"cursor: default; left: " + col_start[2] + "px; width: " + col_width[2] + "px; background-color: " +dts_defaultF3Color+ "\">");
            }
              if (min < 10) {
                 out.print(hr + ":0" + min + ampm);
              } else {
                 out.print(hr + ":" + min + ampm);
              }
            out.println("</span>");

            //
            //  Name of Course
            //
            if (course.equals( "-ALL-" )) { // only display this col if this tee sheet is showing more than one course
               out.print(" <span id=time_slot_" + dts_slot_index + "_course class=cellDataC style=\"cursor: default; left: " + col_start[3] + "px; width: " + col_width[3] + "px\">");
               if (!courseT.equals("")) { out.print(fitName(courseT)); }
               out.println("</span>");
            }

            //
            //  Front/Back Indicator
            //
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_FB class=cellData onclick=\"showFBO(this)\" value=\""+sfb+"\" style=\"cursor: hand; left: " + col_start[4] + "px; width: " + col_width[4] + "px\">"); //  background-color: " +dts_defaultF3Color+ "
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_FB class=cellDataB value=\""+sfb+"\" style=\"cursor: default; left: " + col_start[4] + "px; width: " + col_width[4] + "px\">");
            }
            out.print(sfb);
            out.println("</span>");


            //
            //  Add Player 1
            //
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1 class=cellData drag=true startX="+col_start[5]+" playerSlot=1 style=\"left: " + col_start[5] + "px; width: " + col_width[5] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1 class=cellData startX="+col_start[5]+" playerSlot=1 style=\"cursor: default; left: " + col_start[5] + "px; width: " + col_width[5] + "px\">");
            }
            if (!player1.equals("")) { out.print(fitName(player1)); }
            out.println("</span>");

            // Player 1 CW
            if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
               dts_tmp = p1cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[6] + "px; width: " + col_width[6] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_1_CW class=cellDataB style=\"cursor: default; left: " + col_start[6] + "px; width: " + col_width[6] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");


            //
            //  Add Player 2
            //
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2 class=cellData drag=true startX="+col_start[7]+" playerSlot=2 style=\"left: " + col_start[7] + "px; width: " + col_width[7] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2 class=cellData startX="+col_start[7]+" playerSlot=2 style=\"cursor: default; left: " + col_start[7] + "px; width: " + col_width[7] + "px\">");
            }
            if (!player2.equals("")) { out.print(fitName(player2)); }
            out.println(" </span>");

            // Player 2 CW
            if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
               dts_tmp = p2cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[8] + "px; width: " + col_width[8] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_2_CW class=cellDataB style=\"cursor: default; left: " + col_start[8] + "px; width: " + col_width[8] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");


            //
            //  Add Player 3
            //
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3 class=cellData drag=true startX="+col_start[9]+" playerSlot=3 style=\"left: " + col_start[9] + "px; width: " + col_width[9] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3 class=cellData startX="+col_start[9]+" playerSlot=3 style=\"cursor: default; left: " + col_start[9] + "px; width: " + col_width[9] + "px\">");
            }
            if (!player3.equals("")) { out.print(fitName(player3)); }
            out.println("</span>");

            // Player 3 CW
            if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
               dts_tmp = p3cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[10] + "px; width: " + col_width[10] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_3_CW class=cellDataB style=\"cursor: default; left: " + col_start[10] + "px; width: " + col_width[10] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");

            //
            //  Add Player 4
            //
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4 class=cellData drag=true startX="+col_start[11]+" playerSlot=4 style=\"left: " + col_start[11] + "px; width: " + col_width[11] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4 class=cellData startX="+col_start[11]+" playerSlot=4 style=\"cursor: default; left: " + col_start[11] + "px; width: " + col_width[11] + "px\">");
            }
            if (!player4.equals("")) { out.print(fitName(player4)); }
            out.println("</span>");

            // Player 4 CW
            if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
               dts_tmp = p4cw;
            } else {
               dts_tmp = "";
            }
            if (in_use == 0) {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[12] + "px; width: " + col_width[12] + "px\">");
            } else {
              out.print(" <span id=time_slot_" + dts_slot_index + "_player_4_CW class=cellDataB style=\"cursor: default; left: " + col_start[12] + "px; width: " + col_width[12] + "px\">");
            }
            out.print(dts_tmp);
            out.println("</span>");

            //
            //  Add Player 5 if supported
            //
            if (fivesALL != 0) {        // if 5-somes on any course        (Paul - this is a new flag!!!!)
               if (fives != 0) {        // if 5-somes on this course
                 if (in_use == 0) {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5 class=cellData drag=true startX="+col_start[13]+" playerSlot=5 style=\"left: " + col_start[13] + "px; width: " + col_width[13] + "px; background-color: " +bgcolor5+ "\">");
                 } else {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5 class=cellData startX="+col_start[13]+" playerSlot=5 style=\"cursor: default; left: " + col_start[13] + "px; width: " + col_width[13] + "px\">");
                 }
                 if (!player5.equals("")) { out.print(fitName(player5)); }
                 out.println("</span>");

                 // Player 5 CW
                 if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                    dts_tmp = p5cw;
                 } else {
                    dts_tmp = "";
                 }
                 if (in_use == 0) {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB onclick=\"showTOPopup(this)\" value=\"" + dts_tmp + "\" style=\"left: " + col_start[14] + "px; width: " + col_width[14] + "px\">");
                 } else {
                   out.print(" <span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB style=\"cursor: default; left: " + col_start[14] + "px; width: " + col_width[14] + "px\">");
                 }
                 out.print(dts_tmp);
                 out.println("</span>");

               } else {       // 5-somes on at least 1 course, but not this one

                 out.print(" <span id=time_slot_" + dts_slot_index + "_player_5 class=cellData startX="+col_start[13]+" playerSlot=5 style=\"cursor: default; left: " + col_start[13] + "px; width: " + col_width[13] + "px; background-color: black\">");
                 out.println("</span>");

                 // Player 5 CW
                 dts_tmp = "";
                 out.print(" <span id=time_slot_" + dts_slot_index + "_player_5_CW class=cellDataB style=\"cursor: default; left: " + col_start[14] + "px; width: " + col_width[14] + "px; background-color: black\">");
                 out.print(dts_tmp);
                 out.println("</span>");

               } // end if fives
            } // end if fivesALL

            out.println("</div>"); // end timeslot container div

            dts_slot_index++;    // increment timeslot index counter
            first = "no";        // no longer first time displayed

         }  // end of IF Blocker that escapes building and displaying a particular tee time slot in the sheet

      }  // end of while

      out.println("<br>"); // spacer at bottom of tee sheet
      out.println("\n</div>"); // end main container div holding entire tee sheet
      out.println("<!-- END OF TEE SHEET BODY -->\n");
      out.println("<br><br>\n");

      pstmt.close();

    // write out form for posting tee sheet actions to the server for processing
    out.println("<form name=frmSendAction method=POST action=/" +rev+ "/servlet/Proshop_insert>");
    out.println("<input type=hidden name=index value=\"" + index + "\">");
    out.println("<input type=hidden name=returnCourse value=\"" + course + "\">");
    out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
      
    out.println("<input type=hidden name=from_course value=\"\">");
    out.println("<input type=hidden name=to_course value=\"\">");
  
    out.println("<input type=hidden name=jump value=\"\">");                  // needs to be set in ....js !!!!!
    out.println("<input type=hidden name=from_time value=\"\">");
    out.println("<input type=hidden name=from_fb value=\"\">");
    out.println("<input type=hidden name=to_time value=\"\">");
    out.println("<input type=hidden name=to_fb value=\"\">");
    out.println("<input type=hidden name=from_player value=\"\">");
    out.println("<input type=hidden name=to_player value=\"\">");
    out.println("<input type=hidden name=to_from value=\"\">");
    out.println("<input type=hidden name=to_to value=\"\">");
    out.println("<input type=hidden name=changeAll value=\"\">");
    out.println("<input type=hidden name=ninehole value=\"\">");
    out.println("</form>");

    // START OF FBO POPUP WINDOW //
    out.println("<div id=elFBOPopup defaultValue=\"\" style=\"visibility: hidden\" jump=\"\">");
    out.println("<table width=100% height=100% border=0 cellpadding=0 cellspacing=2>");
    out.println("<form name=frmFBO>");
    out.println("<input type=hidden name=jump value=\"\">");
    out.println("<tr><td align=center class=smtext><b><u>Make Selection</u></b></td></tr>");
    out.println("<tr><td class=smtext><input type=radio value=F name=FBO id=FBO_1><label for=\"FBO_1\">Front</label></td></tr>");
    out.println("<tr><td class=smtext><input type=radio value=B name=FBO id=FBO_2><label for=\"FBO_2\">Back</label></td></tr>");
    out.println("<tr><td class=smtext><input type=radio value=O name=FBO id=FBO_3><label for=\"FBO_3\">Crossover</label></td></tr>");
    out.println("<tr><td align=right><a href=\"javascript: cancelFBOPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveFBOPopup()\" class=smtext>save</a>&nbsp;</td></tr>");
    out.println("</form>");
    out.println("</table>");
    out.println("</div>");


    // START OF TRANSPORTATION POPUP WINDOW //
    //
    //  Note:  There can now be up to 16 dynamic Modes of Transportation (proshop can config).
    //         Both the Full Name/Description and the Acronym are specified by the pro.
    //         These names and acronyms will not contain the '9' to indicate 9 holes.
    //         These values can be found in:
    //
    //               parmc.tmode[i]   =  full name description   
    //               parmc.tmodea[i]  =  1 to 3 character acronym  (i = index of 0 - 15)
    //
    //
    out.println("<div id=elTOPopup defaultValue=\"\" fb=\"\" nh=\"\" jump=\"\">");
    out.println("<table width=100% height=100% border=0 cellpadding=0 cellspacing=2>");
    out.println("<form name=frmTransOpt>");
    out.println("<input type=hidden name=jump value=\"\">");
    // loop thru the array and write out a table row for each option
  
    // set tmp_cols to the # of cols this table will have
    // if the # of trans opts is less then 4 then that's the #, otherwise the max is 4
    //   tmode_limit = max number of tmodes available
    //   tmode_count = actual number of tmodes specified for this course
    int tmp_cols = 0;
    if (parmc.tmode_count < 4) {
       tmp_cols = parmc.tmode_count;
    } else {
       tmp_cols = 4;
    }
    int tmp_count = 0;

    out.println("<tr><td align=center class=smtext colspan="+tmp_cols+"><b><u>Make Selection</u></b></td></tr>");

    out.println("<tr>");
    for (int tmp_loop = 0; tmp_loop < parmc.tmode_limit; tmp_loop++) {
      if (!parmc.tmodea[tmp_loop].equals( "" ) && !parmc.tmodea[tmp_loop].equals( null )) {
        out.println("<td nowrap class=smtext><input type=radio value="+parmc.tmodea[tmp_loop]+" name=to id=to_"+tmp_loop+"><label for=\"to_"+tmp_loop+"\">"+parmc.tmode[tmp_loop]+"</label></td>");
        if (tmp_count == 3 || tmp_count == 7 || tmp_count == 11) {
          out.println("</tr><tr>");         // new row
        }
        tmp_count++;
      }
    }
    out.println("</tr>");
  
    out.println("<tr><td bgcolor=black colspan="+tmp_cols+"><img src=/" +rev+ "/images/shim.gif width=100 height=1 border=0></td></tr>");
    out.println("<tr><td class=smtext colspan="+tmp_cols+"><input type=checkbox value=yes name=9hole id=nh><label for=\"nh\">9 Hole</label></td></tr>");
    out.println("<tr><td bgcolor=black colspan="+tmp_cols+"><img src=/" +rev+ "/images/shim.gif width=100 height=1 border=0></td></tr>");
    // "CHANGE ALL" DEFAULT OPTION COULD BE SET HERE
    out.println("<tr><td class=smtext colspan="+tmp_cols+"><input type=checkbox value=yes name=changeAll id=ca><label for=\"ca\">Change All</label></td></tr>");
    out.println("<tr><td align=right colspan="+tmp_cols+"><a href=\"javascript: cancelTOPopup()\" class=smtext>cancel</a>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; <a href=\"javascript: saveTOPopup()\" class=smtext>save</a>&nbsp;</td></tr>");
    out.println("</form>");
    out.println("</table>");
    out.println("</div>");

    // FINAL JAVASCRIPT FOR THE PAGE, SET VARIABLES THAT WE DIDN'T KNOW TILL AFTER PROCESSING
    out.println("<script type=\"text/javascript\">");
    out.println("var slotHeight = document.getElementById(\"time_slot_0\").offsetHeight;");
    out.println("var totalTimeSlots = " + (dts_slot_index) + ";");
    out.println("var g_transOptTotal = "+tmp_count+";");
    out.println("var g_pslot1s = " + col_start[5] + ";");
    out.println("var g_pslot1e = " + col_start[6] + ";");
    out.println("var g_pslot2s = " + col_start[7] + ";");
    out.println("var g_pslot2e = " + col_start[8] + ";");
    out.println("var g_pslot3s = " + col_start[9] + ";");
    out.println("var g_pslot3e = " + col_start[10] + ";");
    out.println("var g_pslot4s = " + col_start[11] + ";");
    out.println("var g_pslot4e = " + col_start[12] + ";");
    out.println("var g_pslot5s = " + col_start[13] + ";");
    out.println("var g_pslot5e = " + col_start[14] + ";");
  
  // SIZE UP THE CONTAINER ELEMENTS AND THE TIME SLOTS
  out.println("var e = document.getElementById('widthMarker');");
  out.println("var g_slotWidth = e.offsetLeft + e.offsetWidth;");
  out.println("document.styleSheets[0].rules(0).style.width = (g_slotWidth + 2) + 'px';");
  out.println("document.styleSheets[0].rules(1).style.width = (g_slotWidth + 2) + 'px';");
  out.println("document.styleSheets[0].rules(7).style.width = g_slotWidth + 'px';");
  out.println("document.styleSheets[0].rules(8).style.width = g_slotWidth + 'px';");
     
    // IF THERE IS NO 5th PLAYER COLUMN ON THIS TEE SHEET THEN NUDGE THE TEE SHEET OVER A BIT TO CENTER BETTER
    if (fivesALL == 0) {
       out.println("document.styleSheets[0].rules(0).style.left = \"80px\";");
       out.println("document.styleSheets[0].rules(1).style.left = \"80px\";");
    }
  
    // CALL THE POSITIONING CODE FOR EACH OF THE TIME SLOTS WE CREATED
    out.println("for(x=0;x<=totalTimeSlots-1;x++) eval(\"positionElem('time_slot_\" + x + \"', \"+ x +\")\");");
    out.println("document.getElementById(\"elContainer\").style.height=\""+ (dts_slot_index * 20) +"px\";");
  
    out.println("</script>");
  // END OF OUT FINAL CLIENT SIDE SCRIPT WRITING
  
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>Error = " +errMsg);
      out.println("<BR><BR>Exception = " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</center></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  End of HTML page
   //
   out.println("</body>\n</html>");
   out.close();

 }   // end of doGet


 //************************************************************************************
 //
 //   **** doPost **** 
 //
 //     Refer to the 'doGet' method above to see how the tee sheet is built.
 //     This method needs the following parameters based on the type of drag-n-drop performed.
 //
 //
 // Process drag-n-drop requests from doGet above
 //
 //   Parms passed on every call:
 //
 //            index        = index value for date (0 = today, 1 = tomorrow, etc.)
 //            jump         = jump index for return to tee sheet
 //            returnCourse = name of course for return (could be -ALL-)
 //
 //      Change F/B:  
 //
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_fb        = front/back value of tee time player moved to (F, B, O)
 //
 //      Change C/W:
 //
 //            from_player  = player position that is changing (1-5)
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_from      = current transporation option for player - (WA, MC, etc.)
 //            to_to        = new transporation option for player(s) - (WA, MC, etc.)
 //            ninehole     = 9 Hole option
 //            changeAll    = change all players to this option (true or false)
 //
 //      Single player move:
 //
 //            from_player  = current player position - moved from (1-5)
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_player    = new player position - moved to (1-5)
 //            to_time      = time value of tee time (hhmm) player moved to
 //            to_fb        = front/back value of tee time player moved to (F, B, O)
 //            to_course    = the course name this player moved to
 //
 //      Entire tee time move:
 //
 //            from_time    = time value of tee time (hhmm) player moved from
 //            from_fb      = front/back value of tee time player moved from (F, B, O)
 //            from_course  = name of course for this tee time
 //            to_time      = time value of tee time (hhmm) player moved to
 //            to_fb        = front/back value of tee time player moved to (F, B, O)
 //            to_course    = the course name this tee time moved to
 //            prompt       = provided if user has been prompted to continue (2nd entry)
 //                           'return' if user wants to return w/o changes
 //                           'continue' if user wants to contnue with changes
 //            skip         = provided if user has been prompted, indicates verification
 //                           process to skip (1, 2, 3, etc.)
 //
 //************************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   if (req.getParameter("setBlockers") != null) {
       doGet(req, resp);
   }    
     
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                    // get DB connection

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
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   String changeAll = "";
   String ninehole = "";
   String dts_tmp = "";
   String prompt = "";
     
   int skip = 0;

   long date = 0;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  Get this session's username (to be saved in teecurr)
   //
   slotParms.user = (String)session.getAttribute("user");
   slotParms.club = (String)session.getAttribute("club");


   //
   //  Get the parms passed
   //
   slotParms.jump = req.getParameter("jump");           // anchor link for page loading
   String indexs = req.getParameter("index");           // # of days ahead of current day
     
   slotParms.ind = Integer.parseInt(indexs);            // save index value in parm block

   //
   //  Get the optional parms
   //
   if (req.getParameter("email") != null) {

      slotParms.sendEmail = req.getParameter("email");
   } else {
      slotParms.sendEmail = "yes";
   }

   if (req.getParameter("returnCourse") != null) {

      slotParms.returnCourse = req.getParameter("returnCourse");
   } else {
      slotParms.returnCourse = "";
   }

   if (req.getParameter("from_course") != null) {

      slotParms.from_course = req.getParameter("from_course");
   } else {
      slotParms.from_course = "";
   }

   if (req.getParameter("to_course") != null) {

      slotParms.to_course = req.getParameter("to_course");
   } else {
      slotParms.to_course = "";
   }

   if (req.getParameter("from_player") != null) {
     
      dts_tmp = req.getParameter("from_player");
        
      if (!dts_tmp.equals( "" )) {
         slotParms.from_player = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_player") != null) {

      dts_tmp = req.getParameter("to_player");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_player = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("from_time") != null) {

      dts_tmp = req.getParameter("from_time");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_time = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_time") != null) {

      dts_tmp = req.getParameter("to_time");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_time = Integer.parseInt(dts_tmp);
      }
   }
   if (req.getParameter("to_from") != null) {

      slotParms.to_from = req.getParameter("to_from");
   }
   if (req.getParameter("to_to") != null) {

      slotParms.to_to = req.getParameter("to_to");
   }
   if (req.getParameter("changeAll") != null) {

      changeAll = req.getParameter("changeAll");
   }
   if (req.getParameter("ninehole") != null) {

      ninehole = req.getParameter("ninehole");
   }
     
   if (req.getParameter("prompt") != null) {        // if 2nd entry (return from prompt)

      prompt = req.getParameter("prompt");
        
      dts_tmp = req.getParameter("date");

      if (!dts_tmp.equals( "" )) {
         date = Integer.parseInt(dts_tmp);
      }
        
      dts_tmp = req.getParameter("to_fb");

      if (!dts_tmp.equals( "" )) {
         slotParms.to_fb = Integer.parseInt(dts_tmp);
      }

      dts_tmp = req.getParameter("from_fb");

      if (!dts_tmp.equals( "" )) {
         slotParms.from_fb = Integer.parseInt(dts_tmp);
      }

      if (req.getParameter("skip") != null) {        // if 2nd entry and skip returned

         dts_tmp = req.getParameter("skip");

         if (!dts_tmp.equals( "" )) {
            skip = Integer.parseInt(dts_tmp);
         }
      }

   } else {
     
      if (req.getParameter("from_fb") != null) {

         dts_tmp = req.getParameter("from_fb");

//  ***************TEMP************
//      System.out.println("from_fb = " +dts_tmp);
//  ***************TEMP************

         slotParms.from_fb = 0;

         if (dts_tmp.equals( "B" )) {

            slotParms.from_fb = 1;
         }
         if (dts_tmp.equals( "O" )) {

            slotParms.from_fb = 9;
         }
      }
      if (req.getParameter("to_fb") != null) {

         dts_tmp = req.getParameter("to_fb");

//  ***************TEMP************
//      System.out.println("to_fb = " +dts_tmp);
//  ***************TEMP************

         slotParms.to_fb = 0;

         if (dts_tmp.equals( "B" )) {

            slotParms.to_fb = 1;
         }
         if (dts_tmp.equals( "O" )) {

            slotParms.to_fb = 9;
         }
      }
   }

   if (date == 0) {
      //
      //  Get today's date and then use the value passed to locate the requested date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      if (slotParms.ind > 0) {
         cal.add(Calendar.DATE,slotParms.ind);         // roll ahead 'index' days
      }

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

      month = month + 1;                            // month starts at zero

      slotParms.dd = day;         
      slotParms.mm = month;
      slotParms.yy = year;
      slotParms.day = day_table[day_num];                // get name for day

      date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
        
   } else {

      if (req.getParameter("day") != null) {

         slotParms.day = req.getParameter("day");
      }

      long lyy = date / 10000;                               // get year
      long lmm = (date - (lyy * 10000)) / 100;               // get month
      long ldd = (date - (lyy * 10000)) - (lmm * 100);       // get day

      slotParms.dd = (int)ldd;
      slotParms.mm = (int)lmm;
      slotParms.yy = (int)lyy;
   }

   //
   //  determine the type of call: Change F/B, Change C/W, Single Player Move, Whole Tee Time Move
   //
   if (!slotParms.to_from.equals( "" ) && !slotParms.to_to.equals( "" )) {

      changeCW(slotParms, changeAll, ninehole, date, req, out, con, resp);     // process Change C/W
      return;
   }

   if (slotParms.to_time == 0) {  // if not C/W and no 'to_time' specified

      changeFB(slotParms, date, req, out, con, resp);              // process Change F/B
      return;
   }

   if ((slotParms.to_player == 0) && (slotParms.from_player == 0)) {

      moveWhole(slotParms, date, prompt, skip, req, out, con, resp);  // process Move Whole Tee Time
      return;
   }

   if (slotParms.from_player > 0 && slotParms.to_player > 0) {

      moveSingle(slotParms, date, prompt, skip, req, out, con, resp);  // process Move Single Tee Time
      return;
   }
  
   //
   //  If we get here, there is an error
   //
   out.println(SystemUtils.HeadTitle("Error in Proshop_insert"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><BR><BR><H2>Error While Editing Tee Sheet</H2>");
   out.println("<BR><BR>An error has occurred that prevents the system from completing the task.<BR>");
   out.println("<BR>Please try again.  If problem continues, contact ForeTees.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +slotParms.ind+ "&course=" +slotParms.returnCourse+ "\">");
   out.println("Return to Tee Sheet</a></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }  // end of doPost



 // *********************************************************
 //  Process insert request from above
 //
 //  parms:  index         = index value for date
 //          course        = name of course
 //          returnCourse  = name of course for return to sheet
 //          jump          = jump index for return
 //          time          = time of tee time
 //          fb            = f/b indicator
 //          insertSubmit  = if from self
 //          first         = if first tee time
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
   int event_type = 0;

   String event = "";
   String event_color = "";
   String rest = "";
   String rest2 = "";
   String rest_color = "";
   String rest_color2 = "";
   String rest_recurr = "";
   String rest5 = "";                      // default values
   String rest52 = "";
   String rest5_color = "";
   String rest5_color2 = "";
   String rest5_recurr = "";
   String lott = "";                      // lottery name
   String lott2 = "";                      // lottery name
   String lott_color = "";
   String lott_color2 = "";
   String lott_recurr = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //    The 'index' paramter contains an index value
   //    (0 = today, 1 = tomorrow, etc.)
   //
   String indexs = req.getParameter("index");         //  index value of the day
   String course = req.getParameter("course");        //  get the course name for this insert
   String returnCourse = req.getParameter("returnCourse");     //  get the course name for this sheet
   String jump = req.getParameter("jump");            //  get the jump index
   String sfb = req.getParameter("fb");
   String times = req.getParameter("time");            //  get the tee time selected (hhmm)
   String first = req.getParameter("first");           //  get the first tee time indicator (yes or no)
   String emailOpt = req.getParameter("email");        //  get the email option from _sheet

   if (course == null) {

      course = "";    // change to null string
   }

   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(indexs);
      fb = Integer.parseInt(sfb);
      time = Integer.parseInt(times);
   }
   catch (NumberFormatException e) {
      // ignore error
   }


   //
   //  isolate hr and min values
   //
   hr = time / 100;
   min = time - (hr * 100);

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)


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
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\">");

      out.println("<font size=\"5\">");
      out.println("<p align=\"center\"><b>Insert Tee Sheet</b></p></font>");
      out.println("<font size=\"2\">");

      out.println("<table cellpadding=\"5\" align=\"center\" width=\"450\">");
      out.println("<tr><td colspan=\"4\" bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Instructions:</b>  To insert a tee time for the date shown below, select the time");
      out.println(" and the 'front/back' values.  Select 'Insert' to add the new tee time.");
      out.println("</font></td></tr></table><br>");


      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\"></input>");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\"></input>");
      out.println("<input type=\"hidden\" name=\"insert\" value=\"yes\"></input>");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\"></input>");

      out.println("<tr><td width=\"450\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">");
         out.println("<b>Note:</b> &nbsp;This tee time must be unique from all others on the sheet. &nbsp;");
         out.println("Therefore, at least one of these values must be different than other tee times.");
      out.println("<p align=\"center\">Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></p>");
      out.println("Time:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"time\">");
      //
      //  Define some variables for this processing
      //
      PreparedStatement pstmt1b = null;
      String dampm = " AM";
      int dhr = hr;
      int i = 0;
      int i2 = 0;
      int mint = min;
      int hrt = hr;
      int last = 0;
      int start = 0;
      int maxtimes = 20;
        
      //
      //  Determine time values to be used for selection 
      //
      loopt:
      while (i < maxtimes) {

         mint++;               // next minute
         if (mint > 59) {

            mint = 0;          // rotate the hour
            hrt++;
         }
         if (hrt > 23) {

            hrt = 23;
            mint = 59;
            break loopt;      // done
         }
         if (i == 0) {                        // if first time
            start = (hrt * 100) + mint;       // save first time for select
         }
         i++;
      }
      last = (hrt * 100) + mint;       // last time for select

      try {

         //
         //   Find the next time - after the time selected - use as the limit for selection list
         //
         pstmt1b = con.prepareStatement (
            "SELECT time FROM teecurr2 " +
            "WHERE date = ? AND time > ? AND time < ? AND fb = ? AND courseName = ? " +
            "ORDER BY time");

         pstmt1b.clearParameters();        // clear the parms
         pstmt1b.setLong(1, date);
         pstmt1b.setInt(2, time);
         pstmt1b.setInt(3, last);
         pstmt1b.setInt(4, fb);
         pstmt1b.setString(5, course);

         rs = pstmt1b.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            last = rs.getInt(1);          // get the first time found - use as upper limit for display
         }
         pstmt1b.close();

      }
      catch (Exception e) {
      }

      i = 0;
        
      //
      //  If first tee time on sheet, then allow 20 tee times prior to this time.
      //
      if (first.equalsIgnoreCase( "yes" )) {

         mint = min;                // get original time
         hrt = hr;

         while (i < maxtimes) {           // determine the first time

            if (mint > 0) {
               mint--;
            } else {               // assume not midnight
               hrt--;
               mint = 59;
            }
            i++;
         }
         start = (hrt * 100) + mint;       // save first time for select
         maxtimes = 40;           // new max for this request
      }

      //
      //  Start with the time selected in case they want a tee time with same time, different f/b
      //
      if (dhr > 11) {

         dampm = " PM";
      }
      if (dhr > 12) {

         dhr = dhr - 12;
      }
      if (min < 10) {
         out.println("<option value=\"" +time+ "\">" +dhr+ ":0" +min+ " " +dampm+ "</option>");
      } else {
         out.println("<option value=\"" +time+ "\">" +dhr+ ":" +min+ " " +dampm+ "</option>");
      }

      //
      //  list tee times that follow the one selected, but less than 'last'
      //
      otime = time;           // save original time value
      i = 0;
      hr = start / 100;             // get values for start time (first in select list)
      min = start - (hr * 100);

      loop1:
      while (i < maxtimes) {

         dhr = hr;            // init as same
         dampm = " AM";

         if (hr == 0) {

            dhr = 12;
         }
         if (hr > 12) {

            dampm = " PM";
            dhr = hr - 12;
         }
         if (hr == 12) {

            dampm = " PM";
         }

         time = (hr * 100) + min;      // set time value

         if (time >= last) {           // if we reached the end

            break loop1;              // done
         }

         if (time != otime) {        // if not same as original time
            if (min < 10) {
               out.println("<option value=\"" +time+ "\">" +dhr+ ":0" +min+ " " +dampm+ "</option>");
            } else {
               out.println("<option value=\"" +time+ "\">" +dhr+ ":" +min+ " " +dampm+ "</option>");
            }
         }
           
         min++;               // next minute
           
         if (min > 59) {

            min = 0;          // rotate the hour
            hr++;
         }
         if (hr > 23) {

            break loop1;      // done
         }

         i++;
      }           // end of while

      out.println("</select>");

      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("Front/Back:&nbsp;&nbsp;");
      out.println("<select size=\"1\" name=\"fb\">");
        out.println("<option value=\"00\">Front</option>");
        out.println("<option value=\"01\">Back</option>");
        out.println("<option value=\"09\">Crossover</option>");
      out.println("</select>");
      out.println("<br><br></p>");
      out.println("<p align=\"center\">");
        out.println("<input type=\"submit\" value=\"Insert\" name=\"insertSubmit\"></input></p>");
      out.println("</font></td></tr></form></table>");
      out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\"></input>");
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
      //                   returnCourse = course name for return

      //
      //  Check to make sure a slot like this doesn't already exist
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

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
            return;

         }    // ok if we get here - not matching time slot

         pstmt1.close();   // close the stmt

      }
      catch (Exception ignore) {   // this is good if no match found
      }

      //
      //  This slot is unique - now check for events or restrictions for this date and time
      //

      try {

         SystemUtils.insertTee(date, time, fb, course, day_name, con);     // insert new tee time

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>Error in Proshop_insert: " + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</center></BODY></HTML>");
         out.close();
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
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_insert?index=" +indexs+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center><BR><BR><H3>Insert Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been added.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      if (min < 10) {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":0" + min + " " + sampm + "</b>");
      } else {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + min + " " + sampm + "</b>");
      }
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\"></input>");
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      out.close();
   }

 }      // end of doInsert


 // *********************************************************
 //  Process delete request from above
 //
 //  parms:  index         = index value for date
 //          course        = name of course
 //          returnCourse  = name of course for return to sheet
 //          jump          = jump index for return
 //          time          = time of tee time
 //          fb            = f/b indicator
 //
 // *********************************************************

 private void doDelete(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {


   ResultSet rs = null;

   //
   //  variables for this class
   //
   int index = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int fb = 0;
   int hr = 0;
   int min = 0;
   int time = 0;

   String sampm = "AM";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };


   //
   //    The 'index' paramter contains an index value
   //    (0 = today, 1 = tomorrow, etc.)
   //
   String indexs = req.getParameter("index");        //  index value of the day
   String course = req.getParameter("course");       //  get the course name for this delete request
   String returnCourse = req.getParameter("returnCourse");   //  get the course name for this sheet
   String jump = req.getParameter("jump");           //  get the jump index
   String stime = req.getParameter("time");          //  get the time of tee time
   String sfb = req.getParameter("fb");              //  get the fb indicator
   String emailOpt = req.getParameter("email");      //  get the email indicator

   if (course == null) {

      course = "";    // change to null string
   }

   //
   //  Convert the index value from string to int
   //
   try {
      index = Integer.parseInt(indexs);
      fb = Integer.parseInt(sfb);
      time = Integer.parseInt(stime);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  isolate hr and min values
   //
   hr = time / 100;
   min = time - (hr * 100);

   //
   //  Get today's date and then use the value passed to locate the requested date
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

   month = month + 1;                            // month starts at zero

   String day_name = day_table[day_num];         // get name for day

   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   if (req.getParameter("deleteSubmit") == null) {      // if this is the first request

      //
      //   Call is from 'edit' processing above to start a delete request
      //
      //
      //  Build the HTML page to prompt user for a confirmation
      //
      out.println(SystemUtils.HeadTitle("Proshop Delete Tee Time Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<table border=\"0\" align=\"center\" width=\"100%\">");   // main page
      out.println("<tr><td align=\"center\">");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\" color=\"#000000\"><br><br>");

         out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"8\" align=\"center\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + "></input>");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\"></input>");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\"></input>");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\"></input>");
         out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\"></input>");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\"></input>");
         out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\"></input>");
         out.println("<input type=\"hidden\" name=\"delete\" value=\"yes\"></input>");

         out.println("<tr><td width=\"450\">");
         out.println("<font size=\"3\">");
         out.println("<p align=\"center\"><b>Delete Tee Time Confirmation</b></p></font>");
         out.println("<br><font size=\"2\">");

            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">");
            //
            //  Check to see if any players are in this tee time
            //
            try {

               PreparedStatement pstmt1d = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, player5 " +
                  "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1d.clearParameters();        // clear the parms
               pstmt1d.setLong(1, date);
               pstmt1d.setInt(2, time);
               pstmt1d.setInt(3, fb);
               pstmt1d.setString(4, course);

               rs = pstmt1d.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  player1 = rs.getString(1);
                  player2 = rs.getString(2);
                  player3 = rs.getString(3);
                  player4 = rs.getString(4);
                  player5 = rs.getString(5);

               } 

               pstmt1d.close();   // close the stmt

            }
            catch (Exception ignore) {   // this is good if no match found
            }
              
            if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) || !player5.equals( "" )) { 
              
               out.println("<b>Warning:</b> &nbsp;You are about to permanently remove a tee time ");
               out.println("which contains the following player(s):<br>");
                 
               if (!player1.equals( "" )) {
         
                  out.println("<br>" +player1);
               }
               if (!player2.equals( "" )) {

                  out.println("<br>" +player2);
               }
               if (!player3.equals( "" )) {

                  out.println("<br>" +player3);
               }
               if (!player4.equals( "" )) {

                  out.println("<br>" +player4);
               }
               if (!player5.equals( "" )) {

                  out.println("<br>" +player5);
               }
               out.println("<br><br>This will remove the entire tee time slot from the database. ");
               out.println(" If you wish to only remove the players, then return to the tee sheet and select the tee time to update it.");
               out.println("</p>");
            } else {
               out.println("<b>Warning:</b> &nbsp;You are about to permanently remove the following tee time.<br><br>");
               out.println("This will remove the entire tee time slot from the database. ");
               out.println(" If you wish to only remove the players, then return to the tee sheet and select the tee time to update it.");
               out.println("</p>");
            }
            //
            //  build the time string
            //
            sampm = "AM";
            if (hr > 11) {        // if PM

               sampm = "PM";
            }
            if (hr > 12) {
               hr = hr - 12;        // convert back to conventional time
            }
            out.println("<p align=\"center\">");
            if (min < 10) {
               out.println("Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":0" + min + " " + sampm + "</b>");
            } else {
               out.println("Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + min + " " + sampm + "</b>");
            }
              
         out.println("<BR><BR>Are you sure you want to delete this tee time?</p>");

            out.println("<p align=\"center\">");
              out.println("<input type=\"submit\" value=\"Yes - Delete It\" name=\"deleteSubmit\"></input></p>");
            out.println("</font></td></tr></form></table>");
            out.println("<br><br>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + "></input>");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\"></input>");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\"></input>");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
      out.println("<input type=\"submit\" value=\"No - Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"No - Return to Tee Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      //
      //  End of HTML page
      //
      out.println("</td></tr></table>");                           // end of main page
      out.println("</td></tr></table>");                           // end of whole page
      out.println("</center></body></html>");
      out.close();
      
   } else { 

      //
      //   Call is from self to process a delete request (final - this is the confirmation)
      //
      //  Check to make sure a slot like this already exists
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         pstmt1.setInt(2, time);
         pstmt1.setInt(3, fb);
         pstmt1.setString(4, course);

         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (!rs.next()) {

            out.println(SystemUtils.HeadTitle("DB Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center><BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>A tee time with these date, time and F/B values does not exist.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></BODY></HTML>");
            out.close();
            return;

         }    // ok if we get here - matching time slot found

         pstmt1.close();   // close the stmt

      }
      catch (Exception ignore) {   // this is good if no match found
      }

      //
      //  This slot was found - delete it from the database
      //

      try {

         PreparedStatement pstmt2 = con.prepareStatement (
            "DELETE FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, date);
         pstmt2.setInt(2, time);
         pstmt2.setInt(3, fb);
         pstmt2.setString(4, course);

         int count = pstmt2.executeUpdate();      // execute the prepared stmt

         pstmt2.close();   // close the stmt

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center><BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Unable to access the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>" + e1.getMessage());
         out.println("<BR><BR>");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"submit\" value=\"Return to Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</center></BODY></HTML>");
         out.close();
         return;
      }
      //
      //  Delete complete - inform user
      //
      sampm = "AM";
      if (hr > 11) {        // if PM

         sampm = "PM";
      }
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }

      out.println("<HTML><HEAD><Title>Proshop Delete Confirmation</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_insert?index=" +indexs+ "&course=" +returnCourse+ "&jump=" +jump+ "&email=" +emailOpt+ "&jump=" +jump+ "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
      out.println("><BR><BR><H3>Delete Tee Time Confirmation</H3>");
      out.println("<BR><BR>Thank you, the following tee time has been removed.");
      if (hr > 12) {
         hr = hr - 12;        // convert back to conventional time
      }
      if (min < 10) {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":0" + min + " " + sampm + "</b>");
      } else {
         out.println("<BR><BR>Date & Time:  <b>" + day_name + " " + month + "/" + day + "/" + year + " " + hr + ":" + min + " " + sampm + "</b>");
      }
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + indexs + "></input>");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\"></input>");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" +emailOpt+ "\"></input>");
      out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</center></BODY></HTML>");
      out.close();
   }
 }      // end of doDelete


 // *********************************************************
 //  changeCW - change the C/W option for 1 or all players in tee time.  
 //
 //  parms:
 //          jump        = jump index for return
 //          from_player = player position being changed (1-5) 
 //          from_time   = tee time being changed
 //          from_fb     = f/b of tee time
 //          from_course = name of course
 //          to_from     = current C/W option 
 //          to_to       = new C/W option
 //          changeAll   = change all players in slot (true or false)
 //          ninehole    = use 9 Hole options (true or false)
 //
 // *********************************************************

 private void changeCW(parmSlot slotParms, String changeAll, String ninehole, long date, HttpServletRequest req, PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int in_use = 0;
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
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String newcw = "";


   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_insert.changeCW - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use
      //
      try {

         in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in changeCW. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms);
      return;
   }

   //
   //  Ok - get current player info from the parm block (set by checkInUse)
   //
   player1 = slotParms.player1;
   player2 = slotParms.player2;
   player3 = slotParms.player3;
   player4 = slotParms.player4;
   player5 = slotParms.player5;
   p1cw = slotParms.p1cw;
   p2cw = slotParms.p2cw;
   p3cw = slotParms.p3cw;
   p4cw = slotParms.p4cw;
   p5cw = slotParms.p5cw;
   p91 = slotParms.p91;
   p92 = slotParms.p92;
   p93 = slotParms.p93;
   p94 = slotParms.p94;
   p95 = slotParms.p95;
     
   //
   //  If '9 Hole' option selected, then change new C/W to 9 hole type
   //
   newcw = slotParms.to_to;            // get selected C/W option
     
   //
   //  Set the new C/W value for each player requested
   //
   if (!player1.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 1)) {   // change this one?

         p1cw = newcw;
         if (ninehole.equals( "true" )) {
            p91 = 1;             // make it a 9 hole type
         } else {
            p91 = 0;             // make it 18 hole 
         }
      }
   }
   if (!player2.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 2)) {   // change this one?

         p2cw = newcw;
         if (ninehole.equals( "true" )) {
            p92 = 1;             // make it a 9 hole type
         } else {
            p92 = 0;             // make it 18 hole
         }
      }
   }
   if (!player3.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 3)) {   // change this one?

         p3cw = newcw;
         if (ninehole.equals( "true" )) {
            p93 = 1;             // make it a 9 hole type
         } else {
            p93 = 0;             // make it 18 hole
         }
      }
   }
   if (!player4.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 4)) {   // change this one?

         p4cw = newcw;
         if (ninehole.equals( "true" )) {
            p94 = 1;             // make it a 9 hole type
         } else {
            p94 = 0;             // make it 18 hole
         }
      }
   }
   if (!player5.equals( "" )) {

      if ((changeAll.equals( "true" )) || (slotParms.from_player == 5)) {   // change this one?

         p5cw = newcw;
         if (ninehole.equals( "true" )) {
            p95 = 1;             // make it a 9 hole type
         } else {
            p95 = 0;             // make it 18 hole
         }
      }
   }
     
   //
   //  Update the tee time and set it no longer in use
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET " +
         "p1cw=?, p2cw=?, p3cw=?, p4cw=?, in_use=0, p5cw=?, p91=?, p92=?, p93=?, p94=?, p95=? " +
         "WHERE date=? AND time=? AND fb=? AND courseName=?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setString(1, p1cw);      
      pstmt1.setString(2, p2cw);
      pstmt1.setString(3, p3cw);
      pstmt1.setString(4, p4cw);
      pstmt1.setString(5, p5cw);
      pstmt1.setInt(6, p91);
      pstmt1.setInt(7, p92);
      pstmt1.setInt(8, p93);
      pstmt1.setInt(9, p94);
      pstmt1.setInt(10, p95);
      pstmt1.setLong(11, date);
      pstmt1.setInt(12, slotParms.from_time);
      pstmt1.setInt(13, slotParms.from_fb);
      pstmt1.setString(14, slotParms.from_course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
   }
   catch (Exception e1) {

      String eMsg = "Error 2 in changeCW. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return 
   //
   editDone(out, slotParms, resp);

 }      // end of changeCW


 // *********************************************************
 //  changeFB - change the F/B option for the tee time specified.
 //
 //  parms: 
 //          jump        = jump index for return
 //          from_time   = tee time being changed
 //          from_fb     = current f/b of tee time
 //          from_course = name of course
 //          to_fb       = new f/b of tee time
 //
 // *********************************************************

 private void changeFB(parmSlot slotParms, long date, HttpServletRequest req, PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int in_use = 0;


   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_insert.changeFB - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use
      //
      try {

         in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in changeFB. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms);
      return;
   }

   //
   //  Ok, tee time not busy - change the F/B
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 0, fb = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt1.clearParameters();          // clear the parms
      pstmt1.setInt(1, slotParms.to_fb);
      pstmt1.setLong(2, date);
      pstmt1.setInt(3, slotParms.from_time);
      pstmt1.setInt(4, slotParms.from_fb);
      pstmt1.setString(5, slotParms.from_course);
      pstmt1.executeUpdate();            // execute the prepared stmt

      pstmt1.close();
   }
   catch (Exception e1) {

      String eMsg = "Error 2 in changeFB. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp);

 }      // end of changeFB


 // *********************************************************
 //  moveWhole - move an entire tee time
 //
 //  parms:
 //          jump        = jump index for return
 //          from_time   = tee time being moved
 //          from_fb     = f/b of tee time being moved
 //          from_course = name of course of tee time being moved
 //          to_time     = tee time to move to
 //          to_fb       = f/b of tee time to move to
 //          to_course   = name of course of tee time to move to
 //
 //          prompt      = null if first call here
 //                      = 'return' if user wants to return w/o changes
 //                      = 'continue' if user wants to continue with changes
 //          skip        = verification process to skip if 2nd return
 //
 // *********************************************************

 private void moveWhole(parmSlot slotParms, long date, String prompt, int skip, HttpServletRequest req, 
                        PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;
     
   int in_use = 0;

   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
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
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String orig_by = "";
   String conf = "";
   String notes = "";

   short pos1 = 0;
   short pos2 = 0;
   short pos3 = 0;
   short pos4 = 0;
   short pos5 = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
     
   int hide = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int fives = 0;
   int sendemail = 0;

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   boolean error = false;
     

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_insert.moveWhole - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use (the FROM tee time)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, then tee time is already busy

            in_use = 0;
              
            getTeeTimeData(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {                 // if time slot already in use

      teeBusy(out, slotParms);       // reject as busy
      return;
   }

   //
   //  Ok - get current 'FROM' player info from the parm block (set by checkInUse) and save it
   //
   player1 = slotParms.player1;
   player2 = slotParms.player2;
   player3 = slotParms.player3;
   player4 = slotParms.player4;
   player5 = slotParms.player5;
   p1cw = slotParms.p1cw;
   p2cw = slotParms.p2cw;
   p3cw = slotParms.p3cw;
   p4cw = slotParms.p4cw;
   p5cw = slotParms.p5cw;
   user1 = slotParms.user1;
   user2 = slotParms.user2;
   user3 = slotParms.user3;
   user4 = slotParms.user4;
   user5 = slotParms.user5;
   hndcp1 = slotParms.hndcp1;
   hndcp2 = slotParms.hndcp2;
   hndcp3 = slotParms.hndcp3;
   hndcp4 = slotParms.hndcp4;
   hndcp5 = slotParms.hndcp5;
   show1 = slotParms.show1;
   show2 = slotParms.show2;
   show3 = slotParms.show3;
   show4 = slotParms.show4;
   show5 = slotParms.show5;
   pos1 = slotParms.pos1;
   pos2 = slotParms.pos2;
   pos3 = slotParms.pos3;
   pos4 = slotParms.pos4;
   pos5 = slotParms.pos5;
   mNum1 = slotParms.mNum1;
   mNum2 = slotParms.mNum2;
   mNum3 = slotParms.mNum3;
   mNum4 = slotParms.mNum4;
   mNum5 = slotParms.mNum5;
   userg1 = slotParms.userg1;
   userg2 = slotParms.userg2;
   userg3 = slotParms.userg3;
   userg4 = slotParms.userg4;
   userg5 = slotParms.userg5;
   notes = slotParms.notes;
   hide = slotParms.hide;
   orig_by = slotParms.orig_by;
   conf = slotParms.conf;
   p91 = slotParms.p91;
   p92 = slotParms.p92;
   p93 = slotParms.p93;
   p94 = slotParms.p94;
   p95 = slotParms.p95;

   slotParms.player1 = "";       // init parmSlot player fields (verifySlot will fill) 
   slotParms.player2 = "";
   slotParms.player3 = "";
   slotParms.player4 = "";
   slotParms.player5 = "";

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.to_time == 0 || slotParms.to_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_insert.moveWhole2 - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.to_time+ ", course= " +slotParms.to_course+ ", fb= " +slotParms.to_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Now check if the 'TO' tee time is currently in use (this will put its info in slotParms)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 2 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }


   //
   //  If 'TO' tee time is in use 
   //
   if (in_use != 0) {   

      //
      //  Error - We must free up the 'FROM' tee time
      //
      in_use = 0;

      try {

         PreparedStatement pstmt4 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setInt(1, in_use);
         pstmt4.setLong(2, date);
         pstmt4.setInt(3, slotParms.from_time);
         pstmt4.setInt(4, slotParms.from_fb);
         pstmt4.setString(5, slotParms.from_course);
         pstmt4.executeUpdate();      // execute the prepared stmt
         pstmt4.close();

      }
      catch (Exception ignore) {
      }

      teeBusy(out, slotParms);
      return;
   }

   //
   //  If user was prompted and opted to return w/o changes, then we must clear the 'in_use' flags
   //  before returning to the tee sheet.
   //
   if (prompt.equals( "return" )) {        // if prompt specified a return

      in_use = 0;

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.from_time);
         pstmt1.setInt(4, slotParms.from_fb);
         pstmt1.setString(5, slotParms.from_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

         pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.to_time);
         pstmt1.setInt(4, slotParms.to_fb);
         pstmt1.setString(5, slotParms.to_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception ignore) {
      }

      // return to Proshop_insert

      out.println("<HTML><HEAD><Title>Proshop Edit Tee Sheet Complete</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_insert?index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&email=" + slotParms.sendEmail + "&jump=" + slotParms.jump + "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Return Accepted</H2>");
      out.println("<BR><BR>Thank you, click Return' below if this does not automatically return.<BR>");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {    // not a 'return' response from prompt 

      //
      //  This is either the first time here, or a 'Continue' reply to a prompt
      //
      //
      p1 = slotParms.player1;      // get players' names for easier reference
      p2 = slotParms.player2;
      p3 = slotParms.player3;
      p4 = slotParms.player4;
      p5 = slotParms.player5;


      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if 'TO' tee time is empty
         //
         if (!p1.equals( "" ) || !p2.equals( "" ) || !p3.equals( "" ) || !p4.equals( "" ) || !p5.equals( "" )) {

            //
            //  Tee time is occupied - inform user and ask to continue or cancel
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Tee Time is Occupied</H3><BR>");
            out.println("<BR>WARNING: The tee time you are trying to move TO is already occupied.");
            out.println("<BR><BR>If you continue, this tee time will effectively be cancelled.");
            out.println("<BR><BR>Would you like to continue and overwrite this tee time?");
            out.println("<BR><BR>");

            out.println("<BR><BR>Course = " +slotParms.to_course+ ", p1= " +p1+ ", p2= " +p2+ ", p3= " +p3+ ", p4= " +p4+ ", p5= " +p5+ ".");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }
        
      //
      //  check if we are to skip this test
      //
      if (skip < 2) {
        
         //
         // *******************************************************************************
         //  Check member restrictions in 'TO' tee time, but 'FROM' players
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //

         //
         //  allocate and setup new parm block to hold the tee time parms for this process
         //
         parmSlot slotParms2 = new parmSlot();          // allocate a parm block

         slotParms2.date = date;                 // get 'TO' info
         slotParms2.time = slotParms.to_time;
         slotParms2.course = slotParms.to_course;
         slotParms2.fb = slotParms.to_fb;
         slotParms2.day = slotParms.day;
            
         slotParms2.player1 = player1;          // get 'FROM' info      
         slotParms2.player2 = player2;
         slotParms2.player3 = player3;
         slotParms2.player4 = player4;
         slotParms2.player5 = player5;

         try {

            verifySlot.parseGuests(slotParms2, con);     // check for guests and set guest types

            error = verifySlot.parseNames(slotParms2, "pro");   // get the names (lname, fname, mi)

            verifySlot.getUsers(slotParms2, con);        // get the mship and mtype info (needs lname, fname, mi)

            error = false;                               // init error indicator

            error = verifySlot.checkMemRests(slotParms2, con);      // check restrictions  

         }
         catch (Exception ignore) {
         }                           

         if (error == true) {          // if we hit on a restriction
           
            //
            //  Prompt user to see if he wants to override this violation 
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotParms2.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms2.rest_name + "</b><br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 3) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions - use 'FROM' player5 and 'TO' tee time slot
         //
         //   If 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((!player5.equals( "" )) && (!slotParms.rest5.equals( "" ))) { // if 5-somes restricted prompt user to skip test

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 4) {

         //
         // *******************************************************************************
         //  Check 5-somes allowed on 'to course' and from-player5 specified
         // *******************************************************************************
         //
         if (!player5.equals( "" )) {      // if player5 exists in 'from' slot

            fives = 0;

            try {

               PreparedStatement pstmtc = con.prepareStatement (
                  "SELECT fives " +
                  "FROM clubparm2 WHERE courseName = ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, slotParms.to_course);
               rs = pstmtc.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  fives = rs.getInt("fives");
               }
               pstmtc.close();
            }
            catch (Exception e) {
            }
           
            if (fives == 0) {      // if 5-somes not allowed on to_course

               //
               //  Prompt user to see if he wants to override this violation
               //
               out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>5-Somes Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>5-somes</b> are not allowed on the course player5 is being moved to.");
               out.println("<BR>Player5 will be lost if you continue.<br><br>");
               out.println("<BR><BR>Would you like to move this tee time without player5?");
               out.println("<BR><BR>");

               //
               //  Return to _insert as directed
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"4\">");
               out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }
      }

   }     // end of IF 'return' reply from prompt

   //
   //  If we get here, then the  move is OK 
   //
   //   - move 'FROM' tee time info into this one (TO)
   //
   if (skip == 4) {      // if player5 being moved to course that does not allow 5-somes
     
      player5 = "";
      user5 = "";
      userg5 = "";
   }        
  
   in_use = 0;
     

   //
   //  Make sure we have the players and other info (this has failed before!!!)
   //
   if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) || !player5.equals( "" )) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
            "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
            "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = ?, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
            "hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " +
            "p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, hideNotes = ?, " +
            "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
            "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
            "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setString(1, player1);
         pstmt6.setString(2, player2);
         pstmt6.setString(3, player3);
         pstmt6.setString(4, player4);
         pstmt6.setString(5, user1);
         pstmt6.setString(6, user2);
         pstmt6.setString(7, user3);
         pstmt6.setString(8, user4);
         pstmt6.setString(9, p1cw);
         pstmt6.setString(10, p2cw);
         pstmt6.setString(11, p3cw);
         pstmt6.setString(12, p4cw);
         pstmt6.setInt(13, in_use);            // set in_use to NOT
         pstmt6.setFloat(14, hndcp1);
         pstmt6.setFloat(15, hndcp2);
         pstmt6.setFloat(16, hndcp3);
         pstmt6.setFloat(17, hndcp4);
         pstmt6.setShort(18, show1);
         pstmt6.setShort(19, show2);
         pstmt6.setShort(20, show3);
         pstmt6.setShort(21, show4);
         pstmt6.setString(22, player5);
         pstmt6.setString(23, user5);
         pstmt6.setString(24, p5cw);
         pstmt6.setFloat(25, hndcp5);
         pstmt6.setShort(26, show5);
         pstmt6.setString(27, notes);
         pstmt6.setInt(28, hide);
         pstmt6.setString(29, mNum1);
         pstmt6.setString(30, mNum2);
         pstmt6.setString(31, mNum3);
         pstmt6.setString(32, mNum4);
         pstmt6.setString(33, mNum5);
         pstmt6.setString(34, userg1);
         pstmt6.setString(35, userg2);
         pstmt6.setString(36, userg3);
         pstmt6.setString(37, userg4);
         pstmt6.setString(38, userg5);
         pstmt6.setString(39, orig_by);
         pstmt6.setString(40, conf);
         pstmt6.setInt(41, p91);
         pstmt6.setInt(42, p92);
         pstmt6.setInt(43, p93);
         pstmt6.setInt(44, p94);
         pstmt6.setInt(45, p95);
         pstmt6.setShort(46, pos1);
         pstmt6.setShort(47, pos2);
         pstmt6.setShort(48, pos3);
         pstmt6.setShort(49, pos4);
         pstmt6.setShort(50, pos5);

         pstmt6.setLong(51, date);
         pstmt6.setInt(52, slotParms.to_time);
         pstmt6.setInt(53, slotParms.to_fb);
         pstmt6.setString(54, slotParms.to_course);

         pstmt6.executeUpdate();      // execute the prepared stmt

         pstmt6.close();

      }
      catch (Exception e1) {

         String eMsg = "Error 3 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String fullName = "Proshop Edit Tsheet";

      //  new tee time
      SystemUtils.updateHist(date, slotParms.day, slotParms.to_time, slotParms.to_fb, slotParms.to_course, player1, player2, player3,
                             player4, player5, slotParms.user, fullName, 0, con);


      //
      //  Finally, set the 'FROM' tee time to NOT in use and clear out the players
      //
      try {

         PreparedStatement pstmt5 = con.prepareStatement (
            "UPDATE teecurr2 SET player1 = '', player2 = '', player3 = '', player4 = '', " +
            "username1 = '', username2 = '', username3 = '', username4 = '', " +
            "in_use = 0, show1 = 0, show2 = 0, show3 = 0, show4 = 0, " +
            "player5 = '', username5 = '', show5 = 0, " +
            "notes = '', " +
            "mNum1 = '', mNum2 = '', mNum3 = '', mNum4 = '', mNum5 = '', " +
            "userg1 = '', userg2 = '', userg3 = '', userg4 = '', userg5 = '', orig_by = '', conf = '', " +
            "pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0, pos5 = 0 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt5.clearParameters();        // clear the parms
         pstmt5.setLong(1, date);
         pstmt5.setInt(2, slotParms.from_time);
         pstmt5.setInt(3, slotParms.from_fb);
         pstmt5.setString(4, slotParms.from_course);

         pstmt5.executeUpdate();      // execute the prepared stmt

         pstmt5.close();

         if (slotParms.sendEmail.equalsIgnoreCase( "yes" )) {        // if ok to send emails

            sendemail = 1;          // tee time moved - send email notification
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 4 in moveWhole. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String empty = "";

      SystemUtils.updateHist(date, slotParms.day, slotParms.from_time, slotParms.from_fb, slotParms.from_course, empty, empty, empty,
                             empty, empty, slotParms.user, fullName, 1, con);

   } else {
     
      //
      //  save message in error log
      //
      String msg = "Error in Proshop_insert.moveWhole - Player names lost " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      teeBusy(out, slotParms);        // pretend its busy
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp);

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "moveWhole";         // type = Move Whole tee time
      parme.date = date;
      parme.time = 0;
      parme.to_time = slotParms.to_time;
      parme.from_time = slotParms.from_time;
      parme.fb = 0;
      parme.to_fb = slotParms.to_fb;
      parme.from_fb = slotParms.from_fb;
      parme.to_course = slotParms.to_course;
      parme.from_course = slotParms.from_course;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = slotParms.user;
      parme.emailNew = 0;
      parme.emailMod = 0;
      parme.emailCan = 0;

      parme.p91 = p91;
      parme.p92 = p92;
      parme.p93 = p93;
      parme.p94 = p94;
      parme.p95 = p95;

      parme.day = slotParms.day;

      parme.player1 = player1;
      parme.player2 = player2;
      parme.player3 = player3;
      parme.player4 = player4;
      parme.player5 = player5;

      parme.user1 = user1;
      parme.user2 = user2;
      parme.user3 = user3;
      parme.user4 = user4;
      parme.user5 = user5;

      parme.pcw1 = p1cw;
      parme.pcw2 = p2cw;
      parme.pcw3 = p3cw;
      parme.pcw4 = p4cw;
      parme.pcw5 = p5cw;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
 }      // end of moveWhole


 // *********************************************************
 //  moveSingle - move a single player
 //
 //  parms:
 //          jump        = jump index for return
 //          from_player = player position being moved (1-5)
 //          from_time   = tee time being moved
 //          from_fb     = f/b of tee time being moved
 //          from_course = name of course
 //          to_player   = player position to move to (1-5)
 //          to_time     = tee time to move to
 //          to_fb       = f/b of tee time to move to
 //          to_course   = name of course
 //
 //          prompt      = null if first call here
 //                      = 'return' if user wants to return w/o changes
 //                      = 'continue' if user wants to continue with changes
 //          skip        = verification process to skip if 2nd return
 //
 // *********************************************************

 private void moveSingle(parmSlot slotParms, long date, String prompt, int skip, HttpServletRequest req, 
                         PrintWriter out, Connection con, HttpServletResponse resp) {


   ResultSet rs = null;

   int fives = 0;
   int in_use = 0;
   int from = slotParms.from_player;     // get the player positions (1-5)
   int fr = from;                        // save original value 
   int to = slotParms.to_player;
   int sendemail = 0;

   //
   //  arrays to hold the player info (FROM tee time)
   //
   String [] p = new String [5];
   String [] player = new String [5];
   String [] pcw = new String [5];
   String [] user = new String [5];
   String [] mNum = new String [5];
   String [] userg = new String [5];
   short [] show = new short [5];
   short [] pos = new short [5];
   float [] hndcp = new float [5];
   int [] p9 = new int [5];
     
   boolean error = false;


   //
   //  adjust the player positions so they can be used for array indexes
   //
   if (to > 0 && to < 6) {
     
      to--;
        
   } else {
     
      to = 1;    // prevent big problem
   }
   if (from > 0 && from < 6) {

      from--;

   } else {

      from = 1;    // prevent big problem
   }

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.from_time == 0 || slotParms.from_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_insert.moveSingle - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.from_time+ ", course= " +slotParms.from_course+ ", fb= " +slotParms.from_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Check if the requested tee time is currently in use (the FROM tee time)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.from_time, slotParms.from_fb, slotParms.from_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 1 in moveSingle. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   if (in_use != 0) {              // if time slot already in use

      teeBusy(out, slotParms);       // first call here - reject as busy
      return;
   }

   //
   //  Ok - get current 'FROM' player info from the parm block (set by checkInUse) and save it
   //
   player[0] = slotParms.player1;
   player[1] = slotParms.player2;
   player[2] = slotParms.player3;
   player[3] = slotParms.player4;
   player[4] = slotParms.player5;
   pcw[0] = slotParms.p1cw;
   pcw[1] = slotParms.p2cw;
   pcw[2] = slotParms.p3cw;
   pcw[3] = slotParms.p4cw;
   pcw[4] = slotParms.p5cw;
   user[0] = slotParms.user1;
   user[1] = slotParms.user2;
   user[2] = slotParms.user3;
   user[3] = slotParms.user4;
   user[4] = slotParms.user5;
   hndcp[0] = slotParms.hndcp1;
   hndcp[1] = slotParms.hndcp2;
   hndcp[2] = slotParms.hndcp3;
   hndcp[3] = slotParms.hndcp4;
   hndcp[4] = slotParms.hndcp5;
   show[0] = slotParms.show1;
   show[1] = slotParms.show2;
   show[2] = slotParms.show3;
   show[3] = slotParms.show4;
   show[4] = slotParms.show5;
   mNum[0] = slotParms.mNum1;
   mNum[1] = slotParms.mNum2;
   mNum[2] = slotParms.mNum3;
   mNum[3] = slotParms.mNum4;
   mNum[4] = slotParms.mNum5;
   userg[0] = slotParms.userg1;
   userg[1] = slotParms.userg2;
   userg[2] = slotParms.userg3;
   userg[3] = slotParms.userg4;
   userg[4] = slotParms.userg5;
   p9[0] = slotParms.p91;
   p9[1] = slotParms.p92;
   p9[2] = slotParms.p93;
   p9[3] = slotParms.p94;
   p9[4] = slotParms.p95;
   pos[0] = slotParms.pos1;
   pos[1] = slotParms.pos2;
   pos[2] = slotParms.pos3;
   pos[3] = slotParms.pos4;
   pos[4] = slotParms.pos5;


   slotParms.player1 = "";       // init parmSlot player fields (verifySlot will fill)
   slotParms.player2 = "";
   slotParms.player3 = "";
   slotParms.player4 = "";
   slotParms.player5 = "";

   //
   //  Verify the required parms exist
   //
   if (date == 0 || slotParms.to_time == 0 || slotParms.to_course == null || slotParms.user.equals( "" ) || slotParms.user == null) {

      //
      //  save message in /" +rev+ "/error.txt
      //
      String msg = "Error in Proshop_insert.moveSingle2 - checkInUse Parms - for user " +slotParms.user+ " at " +slotParms.club+ ".  Date= " +date+ ", time= " +slotParms.to_time+ ", course= " +slotParms.to_course+ ", fb= " +slotParms.to_fb;   // build msg
      SystemUtils.logError(msg);                                   // log it

      in_use = 1;          // make like the time is busy

   } else {               // continue if parms ok

      //
      //  Now check if the 'TO' tee time is currently in use (this will put its info in slotParms)
      //
      try {

         //
         //  If we got here by returning from a prompt below, then tee time is already busy
         //
         if (!prompt.equals( "" )) {        // if return, tee time already busy

            in_use = 0;

            getTeeTimeData(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms, con);

         } else {

            in_use = verifySlot.checkInUse(date, slotParms.to_time, slotParms.to_fb, slotParms.to_course, slotParms.user, slotParms, con);
         }

      }
      catch (Exception e1) {

         String eMsg = "Error 2 in moveSingle. ";
         dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
         return;
      }
   }

   //
   //  If 'TO' tee time is in use 
   //
   if (in_use != 0) { 

      //
      //  Error - We must free up the 'FROM' tee time
      //
      try {

         PreparedStatement pstmt4 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 0 " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt4.clearParameters();        // clear the parms
         pstmt4.setLong(1, date);
         pstmt4.setInt(2, slotParms.from_time);
         pstmt4.setInt(3, slotParms.from_fb);
         pstmt4.setString(4, slotParms.from_course);
         pstmt4.executeUpdate();      // execute the prepared stmt
         pstmt4.close();

      }
      catch (Exception ignore) {
      }

      teeBusy(out, slotParms);
      return;
   }

   //
   //  If user was prompted and opted to return w/o changes, then we must clear the 'in_use' flags
   //  before returning to the tee sheet.
   //
   if (prompt.equals( "return" )) {        // if prompt specified a return

      in_use = 0;

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.from_time);
         pstmt1.setInt(4, slotParms.from_fb);
         pstmt1.setString(5, slotParms.from_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

         pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = ? " +
            "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setInt(1, in_use);
         pstmt1.setLong(2, date);
         pstmt1.setInt(3, slotParms.to_time);
         pstmt1.setInt(4, slotParms.to_fb);
         pstmt1.setString(5, slotParms.to_course);

         pstmt1.executeUpdate();      // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception ignore) {
      }

      // return to Proshop_insert

      out.println("<HTML><HEAD><Title>Proshop Edit Tee Sheet Complete</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_insert?index=" + slotParms.ind + "&course=" + slotParms.returnCourse + "&email=" + slotParms.sendEmail + "&jump=" + slotParms.jump + "\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H2>Return Accepted</H2>");
      out.println("<BR><BR>Thank you, click Return' below if this does not automatically return.<BR>");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\"></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } else {    // not a 'return' response from prompt

      //
      //  This is either the first time here, or a 'Continue' reply to a prompt
      //
      p[0] = slotParms.player1;    // save 'TO' player names
      p[1] = slotParms.player2;
      p[2] = slotParms.player3;
      p[3] = slotParms.player4;
      p[4] = slotParms.player5;


      //
      //  Make sure there are no duplicate names
      //
      if (!user[from].equals( "" )) {         // if player is a member
        
         if ((player[from].equalsIgnoreCase( p[0] )) || (player[from].equalsIgnoreCase( p[1] )) ||
             (player[from].equalsIgnoreCase( p[2] )) || (player[from].equalsIgnoreCase( p[3] )) ||
             (player[from].equalsIgnoreCase( p[4] ))) {

            //
            //  Error - name already exists
            //
            in_use = 0;

            try {

               PreparedStatement pstmt1 = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, in_use);
               pstmt1.setLong(2, date);
               pstmt1.setInt(3, slotParms.from_time);
               pstmt1.setInt(4, slotParms.from_fb);
               pstmt1.setString(5, slotParms.from_course);

               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();

               pstmt1 = con.prepareStatement (
                  "UPDATE teecurr2 SET in_use = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, in_use);
               pstmt1.setLong(2, date);
               pstmt1.setInt(3, slotParms.to_time);
               pstmt1.setInt(4, slotParms.to_fb);
               pstmt1.setString(5, slotParms.to_course);

               pstmt1.executeUpdate();      // execute the prepared stmt

               pstmt1.close();

            }
            catch (Exception ignore) {
            }

            out.println(SystemUtils.HeadTitle("Player Move Error"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Player Move Error</H3>");
            out.println("<BR><BR>Sorry, but the selected player is already scheduled at the time you are moving to.");
            out.println("<BR><BR>");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         //
         //  Check if 'TO' tee time position is empty
         //
         if (!p[to].equals( "" )) {

            //
            //  Tee time is occupied - inform user and ask to continue or cancel
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Tee Time Position is Occupied</H3><BR>");
            out.println("<BR>WARNING: The tee time position you are trying to move TO is already occupied.");
            out.println("<BR><BR>If you continue, the current player in this position (" +p[to]+ ") will be replaced.");
            out.println("<BR><BR>Would you like to continue and replace this player?");
            out.println("<BR><BR>");
               
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
           
         //
         // *******************************************************************************
         //  Check 5-somes allowed on 'to course' if moving to player5 slot
         // *******************************************************************************
         //
         if (to == 4) {      // if player being moved to player5 slot

            fives = 0;

            try {

               PreparedStatement pstmtc = con.prepareStatement (
                  "SELECT fives " +
                  "FROM clubparm2 WHERE courseName = ?");

               pstmtc.clearParameters();        // clear the parms
               pstmtc.setString(1, slotParms.to_course);
               rs = pstmtc.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  fives = rs.getInt("fives");
               }
               pstmtc.close();
            }
            catch (Exception e) {
            }

            if (fives == 0) {      // if 5-somes not allowed on to_course

               out.println(SystemUtils.HeadTitle("Player Move Error"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player Move Error</H3>");
               out.println("<BR><BR>Sorry, but the course you are moving the player to does not support 5-somes.");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
               out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
               out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
               out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
               out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
               out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
               out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
               out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
               out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }

      }
        
      //
      //  check if we are to skip this test
      //
      if (skip < 2) {

         //
         // *******************************************************************************
         //  Check member restrictions in 'TO' tee time, but 'FROM' players
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //

         //
         //  allocate and setup new parm block to hold the tee time parms for this process
         //
         parmSlot slotParms2 = new parmSlot();          // allocate a parm block

         slotParms2.date = date;                 // get 'TO' info
         slotParms2.time = slotParms.to_time;
         slotParms2.course = slotParms.to_course;
         slotParms2.fb = slotParms.to_fb;
         slotParms2.day = slotParms.day;

         slotParms2.player1 = player[from];          // get 'FROM' player (only check this player)

         try {

            verifySlot.parseGuests(slotParms2, con);     // check for guest and set guest type

            error = verifySlot.parseNames(slotParms2, "pro");   // get the name (lname, fname, mi)

            verifySlot.getUsers(slotParms2, con);        // get the mship and mtype info (needs lname, fname, mi)

            error = false;                               // init error indicator

            error = verifySlot.checkMemRests(slotParms2, con);      // check restrictions

         }
         catch (Exception ignore) {
         }

         if (error == true) {          // if we hit on a restriction

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" +player[from]+ "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms2.rest_name + "</b><br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      //
      //  check if we are to skip this test
      //
      if (skip < 3) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions - use 'FROM' player5 and 'TO' tee time slot
         //
         //   If moving to position 5 & 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((to == 4) && (!slotParms.rest5.equals( "" ))) { // if 5-somes restricted prompt user to skip test

            //
            //  Prompt user to see if he wants to override this violation
            //
            out.println(SystemUtils.HeadTitle("Edit Tee Sheet - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            //
            //  Return to _insert as directed
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"return\">");
            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

            out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
            out.println("<input type=\"hidden\" name=\"to_player\" value=\"" + slotParms.to_player + "\">");
            out.println("<input type=\"hidden\" name=\"from_player\" value=\"" + slotParms.from_player + "\">");
            out.println("<input type=\"hidden\" name=\"to_time\" value=\"" + slotParms.to_time + "\">");
            out.println("<input type=\"hidden\" name=\"from_time\" value=\"" + slotParms.from_time + "\">");
            out.println("<input type=\"hidden\" name=\"to_fb\" value=\"" + slotParms.to_fb + "\">");
            out.println("<input type=\"hidden\" name=\"from_fb\" value=\"" + slotParms.from_fb + "\">");
            out.println("<input type=\"hidden\" name=\"to_course\" value=\"" + slotParms.to_course + "\">");
            out.println("<input type=\"hidden\" name=\"from_course\" value=\"" + slotParms.from_course + "\">");
            out.println("<input type=\"hidden\" name=\"prompt\" value=\"continue\">");
            out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submit\"></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

   }     // end of IF 'return' reply from prompt


   //
   //  OK to move player - move 'FROM' player info into this tee time (TO position)
   //
   to++;      // change index back to position value

   String moveP1 = "UPDATE teecurr2 SET player" +to+ " = ?, username" +to+ " = ?, p" +to+ "cw = ?, in_use = 0, hndcp" +to+ " = ?, show" +to+ " = ?, mNum" +to+ " = ?, userg" +to+ " = ?, p9" +to+ " = ?, pos" +to+ " = ? WHERE date = ? AND time = ? AND fb = ? AND courseName=?";
     
   try {

      PreparedStatement pstmt6 = con.prepareStatement (moveP1);  

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, player[from]);
      pstmt6.setString(2, user[from]);
      pstmt6.setString(3, pcw[from]);
      pstmt6.setFloat(4, hndcp[from]);
      pstmt6.setShort(5, show[from]);
      pstmt6.setString(6, mNum[from]);
      pstmt6.setString(7, userg[from]);
      pstmt6.setInt(8, p9[from]);
      pstmt6.setShort(9, pos[from]);

      pstmt6.setLong(10, date);
      pstmt6.setInt(11, slotParms.to_time);
      pstmt6.setInt(12, slotParms.to_fb);
      pstmt6.setString(13, slotParms.to_course);

      pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();

      if (slotParms.sendEmail.equalsIgnoreCase( "yes" )) {        // if ok to send emails

         sendemail = 1;       // send email notification
      }

   }
   catch (Exception e1) {

      String eMsg = "Error 3 in moveSingle. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }


   //
   //  Finally, set the 'FROM' tee time to NOT in use and clear out the player info
   //
   String moveP2 = "UPDATE teecurr2 SET player" +fr+ " = '', username" +fr+ " = '', p" +fr+ "cw = '', in_use = 0, show" +fr+ " = 0, mNum" +fr+ " = '', userg" +fr+ " = '', pos" +fr+ " = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName=?";

   try {

      PreparedStatement pstmt5 = con.prepareStatement (moveP2);

      pstmt5.clearParameters();        // clear the parms
      pstmt5.setLong(1, date);
      pstmt5.setInt(2, slotParms.from_time);
      pstmt5.setInt(3, slotParms.from_fb);
      pstmt5.setString(4, slotParms.from_course);

      pstmt5.executeUpdate();      // execute the prepared stmt

      pstmt5.close();

   }
   catch (Exception e1) {

      String eMsg = "Error 4 in moveSingle. ";
      dbError(out, e1, slotParms.ind, slotParms.returnCourse, eMsg);
      return;
   }

   //
   //  Done - return
   //
   editDone(out, slotParms, resp);

   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      try {                 // get the new 'to' tee time values

         PreparedStatement pstmt5b = con.prepareStatement (
         "SELECT player1, player2, player3, player4, username1, username2, username3, " +
         "username4, p1cw, p2cw, p3cw, p4cw, " +
         "player5, username5, p5cw, p91, p92, p93, p94, p95 " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt5b.clearParameters();        // clear the parms
         pstmt5b.setLong(1, date);
         pstmt5b.setInt(2, slotParms.to_time);
         pstmt5b.setInt(3, slotParms.to_fb);
         pstmt5b.setString(4, slotParms.to_course);
         rs = pstmt5b.executeQuery();      

         if (rs.next()) {

            player[0] = rs.getString(1);
            player[1] = rs.getString(2);
            player[2] = rs.getString(3);
            player[3] = rs.getString(4);
            user[0] = rs.getString(5);
            user[1] = rs.getString(6);
            user[2] = rs.getString(7);
            user[3] = rs.getString(8);
            pcw[0] = rs.getString(9);
            pcw[1] = rs.getString(10);
            pcw[2] = rs.getString(11);
            pcw[3] = rs.getString(12);
            player[4] = rs.getString(13);
            user[4] = rs.getString(14);
            pcw[4] = rs.getString(15);
            p9[0] = rs.getInt(16);
            p9[1] = rs.getInt(17);
            p9[2] = rs.getInt(18);
            p9[3] = rs.getInt(19);
            p9[4] = rs.getInt(20);
         }
         pstmt5b.close();

      }
      catch (Exception ignoree) {
      }

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "tee";         // type = Move Single tee time (use tee and emailMod) 
      parme.date = date;
      parme.time = slotParms.to_time;
      parme.to_time = 0;
      parme.from_time = 0;
      parme.fb = slotParms.to_fb;
      parme.to_fb = 0;
      parme.from_fb = 0;
      parme.to_course = slotParms.to_course;
      parme.from_course = slotParms.from_course;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = slotParms.user;
      parme.emailNew = 0;
      parme.emailMod = 1;
      parme.emailCan = 0;

      parme.p91 = p9[0];
      parme.p92 = p9[1];
      parme.p93 = p9[2];
      parme.p94 = p9[3];
      parme.p95 = p9[4];

      parme.day = slotParms.day;

      parme.player1 = player[0];
      parme.player2 = player[1];
      parme.player3 = player[2];
      parme.player4 = player[3];
      parme.player5 = player[4];

      parme.oldplayer1 = slotParms.player1;
      parme.oldplayer2 = slotParms.player2;
      parme.oldplayer3 = slotParms.player3;
      parme.oldplayer4 = slotParms.player4;
      parme.oldplayer5 = slotParms.player5;

      parme.user1 = user[0];
      parme.user2 = user[1];
      parme.user3 = user[2];
      parme.user4 = user[3];
      parme.user5 = user[4];

      parme.olduser1 = slotParms.user1;
      parme.olduser2 = slotParms.user2;
      parme.olduser3 = slotParms.user3;
      parme.olduser4 = slotParms.user4;
      parme.olduser5 = slotParms.user5;

      parme.pcw1 = pcw[0];
      parme.pcw2 = pcw[1];
      parme.pcw3 = pcw[2];
      parme.pcw4 = pcw[3];
      parme.pcw5 = pcw[4];

      parme.oldpcw1 = slotParms.p1cw;
      parme.oldpcw2 = slotParms.p2cw;
      parme.oldpcw3 = slotParms.p3cw;
      parme.oldpcw4 = slotParms.p4cw;
      parme.oldpcw5 = slotParms.p5cw;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
 }      // end of moveSingle


/**
 //************************************************************************
 //
 //   Get tee time data
 //
 //************************************************************************
 **/

 private void getTeeTimeData(long date, int time, int fb, String course, parmSlot slotParms, Connection con)
         throws Exception {


   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;


   try {

      pstmt = con.prepareStatement (
         "SELECT * " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         slotParms.player1 = rs.getString( "player1" );
         slotParms.player2 = rs.getString( "player2" );
         slotParms.player3 = rs.getString( "player3" );
         slotParms.player4 = rs.getString( "player4" );
         slotParms.user1 = rs.getString( "username1" );
         slotParms.user2 = rs.getString( "username2" );
         slotParms.user3 = rs.getString( "username3" );
         slotParms.user4 = rs.getString( "username4" );
         slotParms.p1cw = rs.getString( "p1cw" );
         slotParms.p2cw = rs.getString( "p2cw" );
         slotParms.p3cw = rs.getString( "p3cw" );
         slotParms.p4cw = rs.getString( "p4cw" );
         slotParms.last_user = rs.getString( "in_use_by" );
         slotParms.hndcp1 = rs.getFloat( "hndcp1" );
         slotParms.hndcp2 = rs.getFloat( "hndcp2" );
         slotParms.hndcp3 = rs.getFloat( "hndcp3" );
         slotParms.hndcp4 = rs.getFloat( "hndcp4" );
         slotParms.show1 = rs.getShort( "show1" );
         slotParms.show2 = rs.getShort( "show2" );
         slotParms.show3 = rs.getShort( "show3" );
         slotParms.show4 = rs.getShort( "show4" );
         slotParms.player5 = rs.getString( "player5" );
         slotParms.user5 = rs.getString( "username5" );
         slotParms.p5cw = rs.getString( "p5cw" );
         slotParms.hndcp5 = rs.getFloat( "hndcp5" );
         slotParms.show5 = rs.getShort( "show5" );
         slotParms.notes = rs.getString( "notes" );
         slotParms.hide = rs.getInt( "hideNotes" );
         slotParms.rest5 = rs.getString( "rest5" );
         slotParms.mNum1 = rs.getString( "mNum1" );
         slotParms.mNum2 = rs.getString( "mNum2" );
         slotParms.mNum3 = rs.getString( "mNum3" );
         slotParms.mNum4 = rs.getString( "mNum4" );
         slotParms.mNum5 = rs.getString( "mNum5" );
         slotParms.userg1 = rs.getString( "userg1" );
         slotParms.userg2 = rs.getString( "userg2" );
         slotParms.userg3 = rs.getString( "userg3" );
         slotParms.userg4 = rs.getString( "userg4" );
         slotParms.userg5 = rs.getString( "userg5" );
         slotParms.orig_by = rs.getString( "orig_by" );
         slotParms.conf = rs.getString( "conf" );
         slotParms.p91 = rs.getInt( "p91" );
         slotParms.p92 = rs.getInt( "p92" );
         slotParms.p93 = rs.getInt( "p93" );
         slotParms.p94 = rs.getInt( "p94" );
         slotParms.p95 = rs.getInt( "p95" );
         slotParms.pos1 = rs.getShort( "pos1" );
         slotParms.pos2 = rs.getShort( "pos2" );
         slotParms.pos3 = rs.getShort( "pos3" );
         slotParms.pos4 = rs.getShort( "pos4" );
         slotParms.pos5 = rs.getShort( "pos5" );
      }

      pstmt.close();

   }
   catch (Exception e) {

      throw new Exception("Error getting tee time data - Proshop_insert.getTeeTimeData - Exception: " + e.getMessage());
   }

 }


 // *********************************************************
 //  Done
 // *********************************************************

 private void editDone(PrintWriter out, parmSlot slotParms, HttpServletResponse resp) {
   
   try {

       String url="/" +rev+ "/servlet/Proshop_insert?index=" +slotParms.ind+ "&course=" + slotParms.returnCourse + "&jump=" + slotParms.jump + "&email=" + slotParms.sendEmail;
       resp.sendRedirect(url);

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>System Error</H3>");
      out.println("<BR><BR>A system error occurred while trying to return to the edit tee sheet.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +slotParms.ind+ "&course=" +slotParms.returnCourse+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // *********************************************************
 //  Tee Time Busy Error
 // *********************************************************

 private void teeBusy(PrintWriter out, parmSlot slotParms) {

      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
      out.println("<BR><BR>Sorry, but this tee time slot is currently busy.");
      out.println("<BR><BR>If you are attempting to move a player to another position within the same tee time,");
      out.println("<BR>you will have to Return to the Tee Sheet and select that tee time to update it.");
      out.println("<BR><BR>Otherwise, please select another time or try again later.");
      out.println("<BR><BR>");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_insert\" method=\"get\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + slotParms.ind + "></input>");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\"></input>");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
         out.println("<input type=\"hidden\" name=\"email\" value=\"" + slotParms.sendEmail + "\">");
      out.println("<input type=\"submit\" value=\"Back to Edit\" style=\"text-decoration:underline; background:#8B8970\"></form>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.returnCourse + "\">");
         out.println("<input type=\"submit\" value=\"Return to Tee Sheet\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, int index, String course, String eMsg) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>Error in dbError in Proshop_insert:");
      out.println("<BR><BR>" + eMsg + " Exc= " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_jump?index=" +index+ "&course=" +course+ "\">");
      out.println("Return to Tee Sheet</a></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }

 //
 // returns the player name but enforces a max length for staying in the width allowed
 // change the two positive values to control the output
 //
 private static String fitName(String pName) {
   return (pName.length() > 13) ? pName.substring(0, 12) + "..." : pName;
 }

 
 private void doBlockers(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
         
    // BEGIN VARIABLE DEFINITIONS
    ResultSet rs = null; 
    Statement stmt = null;
    PreparedStatement pstmtc = null;

    int cMax = 21;  // max # of courses + 1 for -ALL-
    int i = 0;
    int tmp_i = 0; // counter for course[], shading of course field
    int index = 0;
    int day = 0;
    int month = 0;
    int year = 0;
    int courseCount = 0;

    long date = 0;

    String emailOpt = req.getParameter("email");
    String course = req.getParameter("course");
    String sindex = req.getParameter("index");

    //  array to hold the course names and colors
    String [] courseA = new String [cMax];             // max of 20 courses per club
    String [] course_color = new String [cMax];
    String courseName = "";

    int fives = 0;
    int fivesALL = 0;
    int [] fivesA = new int [cMax]; 
   
    //
    // END OF VARIABLES DEFINITIONS

   

    // BEGIN VARIABLE ASSIGNMENT
    //
    
    // set default course colors  
    /*
     *  NOTE: CHANES TO THIS ARRAY NEED TO BE DUPLICATED IN Proshop_sheet:doPost()
     */
    course_color[0] = "#F5F5DC";   // beige shades
    course_color[1] = "#DDDDBE";
    course_color[2] = "#B3B392";
    course_color[3] = "#8B8970";
    course_color[4] = "#E7F0E7";   // greens shades
    course_color[5] = "#C6D3C6";
    course_color[6] = "#648A64";
    course_color[7] = "#407340";
    course_color[8] = "#FFE4C4";   // bisque
    course_color[9] = "#95B795";
    course_color[10] = "#66CDAA";  // medium aquamarine
    course_color[11] = "#20B2AA";  // light seagreen
    course_color[12] = "#3CB371";  // medium seagreen
    course_color[13] = "#F5DEB3";  // wheat
    course_color[14] = "#D2B48C";  // tan
    course_color[15] = "#999900";  //
    course_color[16] = "#FF9900";  // red-orange??
    course_color[17] = "#33FF66";  //
    course_color[18] = "#7FFFD4";  // aquamarine
    course_color[19] = "#33FFFF";  //
    course_color[20] = "#FFFFFF";  // white
    
    if (course == null) course = ""; // ensure course isn't null

    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) 
     { index = 0; } else { index = Integer.parseInt(sindex); }
   
    //
    // get today's date and add the index to get the requested tee-sheet date
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                 // roll ahead 'index' days
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    month = month + 1;                            // month starts at zero
    date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
    
    
    // all I really should have to do is check to see if we are displaying 1 course or -ALL- courses.
    // if 1 then see if it supports 5 somes
    // if -ALL- then see if any supports 5 somes
    // query and display


    parmClub parm = new parmClub();          // allocate a parm block
    parmCourse parmc = new parmCourse();          // allocate a parm block
    
    try {
        
        getClub.getParms(con, parm);        // get the club parms
    }
    catch (Exception exp) {
        displayDatabaseErrMsg("Fatal error allocating parm blocks.", exp.getMessage(), out);        
    }
        


    
    try {

        //
        //   Get course names if multi-course facility so we can determine if any support 5-somes
        //
        i = 0;
        if (parm.multi != 0) {           // if multiple courses supported for this club

            while (i < 20) {

                courseA[i] = "";       // init the course array
                i++;
            }
            i = 0;

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName FROM clubparm2");

            while (rs.next() && i < 20) {

                courseName = rs.getString(1);
                courseA[i] = courseName;      // add course name to array
                i++;
            }

            stmt.close();

            if (i > cMax) {               // make sure we didn't go past max

                courseCount = cMax;

            } else {

                courseCount = i;              // save number of courses
            }

            if (i > 1 && i < cMax) {
                courseA[i] = "-ALL-";        // add '-ALL-' option
            }
           
        }

    }
    catch (Exception exp) {
        displayDatabaseErrMsg(exp.toString(), exp.getMessage(), out);        
    }
    
    //
    //  Get the walk/cart options available and 5-some support
    //
    i = 0;
    if (course.equals( "-ALL-" )) {

        try {
            
            //
            //  Check all courses for 5-some support
            //
            loopc:
            while (i < 20) {

                courseName = courseA[i];       // get a course name

                if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

                    if (courseName.equals( "" )) break loopc;

                    getParms.getCourse(con, parmc, courseName);
                    fivesA[i] = parmc.fives;      // get fivesome option

                    if (fivesA[i] == 1) fives = 1;

                } // end if courseName = -ALL-

                i++;
            } // end while loop

        }
        catch(Exception exp)
        {
            displayDatabaseErrMsg("Fatal error trying to get course parms. (-ALL-)", exp.getMessage(), out);   
        }
        
    } else {       // single course requested

        try {
            getParms.getCourse(con, parmc, course);
        }
        catch (Exception exp)
        {
            displayDatabaseErrMsg("Fatal error trying to get course parms.", exp.getMessage(), out); 
        }
        fives = parmc.fives;      // get fivesome option
    }

    fivesALL = fives;            // fives will be 1 if any course allowed 5-somes

    i = 0;

    // done pre-processing
    
    
    // start tee sheet output
    
    out.println(SystemUtils.HeadTitle("Tee Sheet Blockers"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");
    out.println("<center><p><font size=5>Tee Sheet Blockers</font></p>");
    out.println("<b>Date:</b>&nbsp;&nbsp;" + month + "/" + day + "/" + year);
    
    if (!course.equals( "" )) out.println("&nbsp;&nbsp;&nbsp;<b>Course:</b>&nbsp;&nbsp;" + course);
    out.println("</center><br>");

             
    // build tee sheet header
    
        out.println("<table width=\"80%\" align=\"center\"><tr valign=\"top\">");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_insert\" name=frmRefresh>");
         out.println("<input type=submit value=\" Refresh Sheet \">");
         out.println("<input type=hidden name=blockers value=true>");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_jump\" name=frmBack>");
         out.println("<input type=submit value=\" Back To Tee Sheet \">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_insert\" name=frmBackEdit>");
         out.println("<input type=submit value=\"Back To Edit Tee Sheet\" style=\"width: 190px\">");
         out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<form method=\"POST\" action=\"/" + rev + "/servlet/Proshop_insert\" name=frmBlockers>");
        out.println("<td align=\"center\">");
          out.println("<input type=submit value=\" Update Blockers \"></td>");
          out.println("<input type=hidden name=setBlockers value=true>");
          out.println("<input type=hidden name=blockers value=true>");
          out.println("<input type=hidden name=index value=\"" + index + "\">");
          out.println("<input type=hidden name=course value=\"" + course + "\">");
          
        out.println("</tr></table>");
    
        out.println("<center><font size=2>");
        out.println("<b>Tee Sheet Legend</b></font><font size=1><br>");
        out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;");
        out.println("O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event</font></center>");
      
         out.println("<br>");
         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"90%\">");

         out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

            if (course.equals( "-ALL-" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Course</b></u>");
                  out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>F/B</b></u>");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Block</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Blocker</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 1</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
    
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 2</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 3</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 4</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            
            if (fivesALL == 1) {
                out.println("<td align=\"center\">");
                   out.println("<font color=\"#FFFFFF\" size=\"2\">");
                   out.println("&nbsp;<u><b>Player 5</b></u>");
                   out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                   out.println("&nbsp;");
                   out.println("</font></td>");
            }
    
               
    //
    //  Get the tee sheet for this date
    //
    String stringTee = "";

    if (course.equals( "-ALL-" )) {

    //if (club.equals( "cordillera" )) {         // do not show the Short course if Cordillera

       //stringTee = "SELECT * " +
                   //"FROM teecurr2 WHERE date = ? AND courseName != 'Short' ORDER BY time, courseName, fb";

    //} else {

       // select all tee times for all courses
       stringTee = "SELECT * " +
                   "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
    //} // end if block - Cordillera customization

    } else {

    // select all tee times for a particular course
    stringTee = "SELECT * " +
                "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";

    } // end if all or 1 course

    // define variables to hold rs field data
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String blocker = "";
    String sfb = "";
    String ampm = "";
    String time = "";
    String checked = "";
    String teetime_id = "";
    
    int hr = 0;
    int min = 0;
    int event_type = 0;
    int shotgun = 1;
    int fb = 0;
    
    
    try {
    
        pstmtc = con.prepareStatement ( stringTee );

        pstmtc.clearParameters();
        pstmtc.setLong(1, date);
        if (!course.equals( "-ALL-" )) pstmtc.setString(2, course);
        rs = pstmtc.executeQuery();
        
        //out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");
        
        // loop over the returned records that comprise the tee-sheet
        while (rs.next()) {
        
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            courseName = rs.getString("courseName");
            fb = rs.getInt("fb");
            blocker = rs.getString("blocker");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            event_type = rs.getInt("event_type");
            
            teetime_id = fb + delim + courseName + delim + hr + delim + min;
            
            ampm = " AM";
            if (hr == 12) ampm = " PM";
            if (hr > 12) {
                ampm = " PM";
                hr = hr - 12;    // convert to conventional time
            }
            if (min < 10) {
                time = hr + ":" + "0" + min + ampm;
            } else {
                time = hr + ":" + min + ampm;
            }
            
            if (player1.equals("")) player1 = "&nbsp;";
            if (player2.equals("")) player2 = "&nbsp;";
            if (player3.equals("")) player3 = "&nbsp;";
            if (player4.equals("")) player4 = "&nbsp;";
            if (player5.equals("")) player5 = "&nbsp;";
            if (blocker.equals("")) blocker = "&nbsp;";
            
            //
            //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            //
            sfb = (fb == 1) ? "B" : (fb == 9) ? "0" : (event_type == shotgun) ? "S" : "F";
                  
            //
            //  If course=ALL requested, then set 'fives' option according to this course
            //
            if (course.equals( "-ALL-" )) {
              
                  i = 0;
                  loopall:
                  while (i < 20) {
                     if (courseName.equals( courseA[i] )) {
                        fives = fivesA[i];          // get the 5-some option for this course
                        break loopall;              // exit loop
                     }
                     i++;
                  }
                  
            }
            
            // start new row
            out.println("<tr>");
            
            //
            //  Time
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\"><b>");
            out.println(time);
            out.println("</b></font></td>");
            
            
            // course name (if not single course)
            if (course.equals( "-ALL-" )) {

                //
                //  Course Name
                //
                // set tmp_i equal to course index #
                //
                for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                    if (courseName.equals(courseA[tmp_i])) break;                      
                }

                out.println("<td bgcolor=\"" + course_color[tmp_i] + "\" align=\"center\">");
                out.println("<font size=\"2\">");
                out.println(courseName);
                out.println("</font></td>");
            }

            //
            //  Front/Back Indicator
            //
            out.println("<td bgcolor=\"white\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sfb);
            out.println("</font></td>");
            
            //
            //  Blocker Check Box
            //
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            checked = (blocker.equals("") || blocker.equals("&nbsp;")) ? "" : " checked";
            if (player1.equals("") || player1.equals("&nbsp;")) {
                out.println("<input type=checkbox name=\"" + teetime_id + "\" value=\"1\" " + checked + ">");
                out.println("<input type=hidden name=\"ID_" + teetime_id + "\" value=\"1\">");
            } else {
                out.println("<input type=checkbox name=\"" + teetime_id + "\" value=\"1\" disabled>");
            }
            out.println("</font></td>");
            
            //
            //  Blocker Name
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(blocker);
            out.println("</font></td>");
            
            //
            //  Player1
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player1);
            out.println("</font></td>");
            
            //
            //  Player2
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player2);
            out.println("</font></td>");
            
            //
            //  Player3
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player3);
            out.println("</font></td>");
            
            //
            //  Player4
            //
            out.println("<td align=\"center\" nowrap>");
            out.println("<font size=\"2\">");
            out.println(player4);
            out.println("</font></td>");
            
            if (fivesALL == 1) {
                //
                //  Player5
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player5);
                out.println("</font></td>");
            }
            
            
            // end row
            out.println("</tr>");
            
        } // end teesheet rs while loop
        
    }
    catch (Exception exp) {
        displayDatabaseErrMsg(exp.toString(), exp.getMessage(), out);
    }
    
    out.println("</form>");
    out.println("</body></html>");
    
 }
 
 private void setHoleAssignments(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
    //
    //
    
 }
 
 private void doHoleAssignments(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
         
    // BEGIN VARIABLE DEFINITIONS
    ResultSet rs = null; 
    Statement stmt = null;
    PreparedStatement pstmtc = null;

    int cMax = 21;  // max # of courses + 1 for -ALL-
    int i = 0;
    int tmp_i = 0; // counter for course[], shading of course field
    int index = 0;
    int day = 0;
    int month = 0;
    int year = 0;
    int courseCount = 0;

    long date = 0;

    String emailOpt = req.getParameter("email");
    String course = req.getParameter("course");
    String sindex = req.getParameter("index");

    //  array to hold the course names and colors
    String [] courseA = new String [cMax];             // max of 20 courses per club
    String [] course_color = new String [cMax];
    String courseName = "";

    int fives = 0;
    int fivesALL = 0;
    int [] fivesA = new int [cMax]; 
   
    String event1 = "";       // for legend - max 4 events, 4 rest's, 2 lotteries
    String ecolor1 = "";
    String event2 = "";
    String ecolor2 = "";
    String event3 = "";
    String ecolor3 = "";
    String event4 = "";
    String ecolor4 = "";
    String sql;
    String event = "";
    String ecolor = "";
   
    //
    // END OF VARIABLES DEFINITIONS

   

    // BEGIN VARIABLE ASSIGNMENT
    //
    
    // set default course colors  
    /*
     *  NOTE: CHANES TO THIS ARRAY NEED TO BE DUPLICATED IN Proshop_sheet:doPost()
     */
    course_color[0] = "#F5F5DC";   // beige shades
    course_color[1] = "#DDDDBE";
    course_color[2] = "#B3B392";
    course_color[3] = "#8B8970";
    course_color[4] = "#E7F0E7";   // greens shades
    course_color[5] = "#C6D3C6";
    course_color[6] = "#648A64";
    course_color[7] = "#407340";
    course_color[8] = "#FFE4C4";   // bisque
    course_color[9] = "#95B795";
    course_color[10] = "#66CDAA";  // medium aquamarine
    course_color[11] = "#20B2AA";  // light seagreen
    course_color[12] = "#3CB371";  // medium seagreen
    course_color[13] = "#F5DEB3";  // wheat
    course_color[14] = "#D2B48C";  // tan
    course_color[15] = "#999900";  //
    course_color[16] = "#FF9900";  // red-orange??
    course_color[17] = "#33FF66";  //
    course_color[18] = "#7FFFD4";  // aquamarine
    course_color[19] = "#33FFFF";  //
    course_color[20] = "#FFFFFF";  // white
    
    if (course == null) course = ""; // ensure course isn't null

    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) 
     { index = 0; } else { index = Integer.parseInt(sindex); }
   
    //
    // get today's date and add the index to get the requested tee-sheet date
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                 // roll ahead 'index' days
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    month = month + 1;                            // month starts at zero
    date = (year * 10000) + (month * 100) + day;  // create a date field of yyyymmdd
    
    
    // all I really should have to do is check to see if we are displaying 1 course or -ALL- courses.
    // if 1 then see if it supports 5 somes
    // if -ALL- then see if any supports 5 somes
    // query and display


    parmClub parm = new parmClub();          // allocate a parm block
    parmCourse parmc = new parmCourse();          // allocate a parm block
    
    try {
        
        getClub.getParms(con, parm);        // get the club parms
    }
    catch (Exception exp) {
        displayDatabaseErrMsg("Fatal error allocating parm blocks.", exp.getMessage(), out);        
    }
        
    // lookup shotgun events for today
    if (course.equals( "-ALL-" )) {
     sql = "SELECT name, color FROM events2b WHERE date = ? ORDER BY stime";
    } else {
     sql = "SELECT name, color FROM events2b WHERE date = ? " +
                "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
    }
    
    try {
        PreparedStatement pstmt = con.prepareStatement (sql);
        pstmt.clearParameters();          // clear the parms
        pstmt.setLong(1, date);
        if (!course.equals( "-ALL-" )) pstmt.setString(2, course);
        rs = pstmt.executeQuery();      // find all matching events, if any
    
        while (rs.next()) {

         event = rs.getString(1);
         ecolor = rs.getString(2);

         if (!event.equals( event1 ) && event1.equals( "" )) {

            event1 = event;
            ecolor1 = ecolor;

            if (ecolor.equalsIgnoreCase( "default" )) {

               ecolor1 = "#F5F5DC";
            }

          } else {

            if (!event.equals( event1 ) && !event.equals( event2 ) && event2.equals( "" )) {

               event2 = event;
               ecolor2 = ecolor;

               if (ecolor.equalsIgnoreCase( "default" )) {

                  ecolor2 = "#F5F5DC";
               }

             } else {

               if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) && 
                   event3.equals( "" )) {

                  event3 = event;
                  ecolor3 = ecolor;

                  if (ecolor.equalsIgnoreCase( "default" )) {

                     ecolor3 = "#F5F5DC";
                  }

                } else {

                  if (!event.equals( event1 ) && !event.equals( event2 ) && !event.equals( event3 ) &&
                      !event.equals( event4 ) && event4.equals( "" )) {

                     event4 = event;
                     ecolor4 = ecolor;

                     if (ecolor.equalsIgnoreCase( "default" )) {

                        ecolor4 = "#F5F5DC";
                     }
                  }
               }
            }
         }
        }                  // end of while
        pstmt.close();
    }
    catch (Exception exp) {
        displayDatabaseErrMsg("Fatal error loading events.", exp.getMessage(), out);        
    }
      
    
    try {

        //
        //   Get course names if multi-course facility so we can determine if any support 5-somes
        //
        i = 0;
        if (parm.multi != 0) {           // if multiple courses supported for this club

            while (i < 20) {

                courseA[i] = "";       // init the course array
                i++;
            }
            i = 0;

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName FROM clubparm2");

            while (rs.next() && i < 20) {

                courseName = rs.getString(1);
                courseA[i] = courseName;      // add course name to array
                i++;
            }

            stmt.close();

            if (i > cMax) {               // make sure we didn't go past max

                courseCount = cMax;

            } else {

                courseCount = i;              // save number of courses
            }

            if (i > 1 && i < cMax) {
                courseA[i] = "-ALL-";        // add '-ALL-' option
            }
           
        }

    }
    catch (Exception exp) {
        displayDatabaseErrMsg(exp.toString(), exp.getMessage(), out);        
    }
    
    //
    //  Get the walk/cart options available and 5-some support
    //
    i = 0;
    if (course.equals( "-ALL-" )) {

        try {
            
            //
            //  Check all courses for 5-some support
            //
            loopc:
            while (i < 20) {

                courseName = courseA[i];       // get a course name

                if (!courseName.equals( "-ALL-" )) {   // skip if -ALL-

                    if (courseName.equals( "" )) break loopc;

                    getParms.getCourse(con, parmc, courseName);
                    fivesA[i] = parmc.fives;      // get fivesome option

                    if (fivesA[i] == 1) fives = 1;

                } // end if courseName = -ALL-

                i++;
            } // end while loop

        }
        catch(Exception exp)
        {
            displayDatabaseErrMsg("Fatal error trying to get course parms. (-ALL-)", exp.getMessage(), out);   
        }
        
    } else {       // single course requested

        try {
            getParms.getCourse(con, parmc, course);
        }
        catch (Exception exp)
        {
            displayDatabaseErrMsg("Fatal error trying to get course parms.", exp.getMessage(), out); 
        }
        fives = parmc.fives;      // get fivesome option
    }

    fivesALL = fives;            // fives will be 1 if any course allowed 5-somes

    i = 0;

    // done pre-processing
    
    
    // start tee sheet output
    
    out.println(SystemUtils.HeadTitle("Shotgun Hole Assignments"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("");
    out.println("<center><p><font size=5>Shotgun Hole Assignments</font></p>");
    out.println("<font size=3><b>Date:</b>&nbsp;&nbsp;" + month + "/" + day + "/" + year);
    
    if (!course.equals( "" )) out.println("&nbsp;&nbsp;&nbsp;<b>Course:</b>&nbsp;&nbsp;" + course);
    
    out.println("</font></center><br>");

             
    // build tee sheet header
    
        out.println("<table width=\"80%\" align=\"center\"><tr valign=top>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_insert\" name=frmRefresh>");
         out.println("<input type=submit value=\" Refresh Sheet \">");
         out.println("<input type=hidden name=sha value=true>");
         out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_jump\" name=frmBack>");
         out.println("<input type=submit value=\" Back To Tee Sheet \">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<td align=\"center\">");
        out.println("<form method=\"GET\" action=\"/" + rev + "/servlet/Proshop_insert\" name=frmBackEdit>");
         out.println("<input type=submit value=\"Back To Edit Tee Sheet\" style=\"width: 190px\">"); // using style tag to hold size back
         out.println("<input type=hidden name=email value=\"" + emailOpt + "\">");
         out.println("<input type=hidden name=index value=\"" + index + "\">");
         out.println("<input type=hidden name=course value=\"" + course + "\">");
        out.println("</form></td>");
        
        out.println("<form method=\"POST\" action=\"/" + rev + "/servlet/Proshop_insert\" name=frmHoleAssign>");
        out.println("<td align=\"center\">");
          out.println("<input type=submit value=\"Update Assignments\">");
          out.println("<input type=hidden name=ssha value=true>");
          out.println("<input type=hidden name=sha value=true>");
          out.println("<input type=hidden name=index value=\"" + index + "\">");
          out.println("<input type=hidden name=course value=\"" + course + "\">");
          out.println("<input type=hidden name=email value=\"" + emailOpt + "\"></td>");
          out.println("<input type=hidden name=jump value=\"0\">");
          
        out.println("</tr></table>");
    
        
        
         //
         // If there is an event, restriction or lottery then show the applicable legend
         //
         if (!event1.equals( "" )) {

            // legend title
            out.println("<center><font size=2>");
            out.println("<b>Tee Sheet Legend</b> (click on buttons to view info)<br></font>");


            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event1+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
            out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor1 + "\">" + event1 + "</button></a>");
            out.println("&nbsp;&nbsp;&nbsp;");

            if (!event2.equals( "" )) {

                out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event2+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor2 + "\">" + event2 + "</button></a>");
                out.println("&nbsp;&nbsp;&nbsp;");

                if (!event3.equals( "" )) {

                    out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event3+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                    out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor3 + "\">" + event3 + "</button></a>");
                    out.println("&nbsp;&nbsp;&nbsp;");

                    if (!event4.equals( "" )) {

                        out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Proshop_sheet?event=" +event4+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">");
                        out.println("<button type=\"button\" style=\"text-decoration:none; background:" + ecolor4 + "\">" + event4 + "</button></a>");
                        out.println("&nbsp;&nbsp;&nbsp;");
                    }
                }
            }
            out.println("</center>");
         }
         
         //
         // START HEADER ROW
         //
        
         out.println("<br>");
         out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"90%\">");

         out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

            if (course.equals( "-ALL-" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Course</b></u>");
                  out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"1\">");
               out.println("<u><b>F/B</b></u>");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>Hole Assignment</b></u>");
               out.println("</font></td>");
            /*
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Blocker</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            */   
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 1</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
    
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 2</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 3</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
               
            out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("&nbsp;<u><b>Player 4</b></u>");
               out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
               out.println("&nbsp;");
               out.println("</font></td>");
            
            if (fivesALL == 1) {
                out.println("<td align=\"center\">");
                   out.println("<font color=\"#FFFFFF\" size=\"2\">");
                   out.println("&nbsp;<u><b>Player 5</b></u>");
                   out.println("</font><font color=\"#FFFFFF\" size=\"1\">");
                   out.println("&nbsp;");
                   out.println("</font></td>");
            }
    
               
    //
    //  Get the tee sheet for this date
    //
    String stringTee = "";

    if (course.equals( "-ALL-" )) {

    //if (club.equals( "cordillera" )) {         // do not show the Short course if Cordillera

       //stringTee = "SELECT * " +
                   //"FROM teecurr2 WHERE date = ? AND courseName != 'Short' ORDER BY time, courseName, fb";

    //} else {

       // select all tee times for all courses
       stringTee = "SELECT * " +
                   "FROM teecurr2 WHERE date = ? AND (event <> NULL OR event <> '') AND event_type = 1 ORDER BY time, courseName, fb";
    //} // end if block - Cordillera customization

    } else {

    // select all tee times for a particular course
    stringTee = "SELECT * " +
                "FROM teecurr2 WHERE date = ? AND courseName = ? AND (event <> NULL OR event <> '') AND event_type = 1 ORDER BY time, fb";

    } // end if all or 1 course

    // define variables to hold rs field data
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String hole = "";
    String sfb = "";
    String ampm = "";
    String time = "";
    String checked = "";
    String teetime_id = "";
    
    int hr = 0;
    int min = 0;
    int event_type = 0;
    int shotgun = 1;
    int fb = 0;
    
    
    try {
    
        pstmtc = con.prepareStatement ( stringTee );

        pstmtc.clearParameters();
        pstmtc.setLong(1, date);
        if (!course.equals( "-ALL-" )) pstmtc.setString(2, course);
        rs = pstmtc.executeQuery();
        
        //out.println("<table align=center border=1 cellspacing=5 cellpadding=3>");
        
        // loop over the returned records that comprise the tee-sheet
        while (rs.next()) {
        
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            
            // can i not just check player1 here?
            if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {
                courseName = rs.getString("courseName");
                fb = rs.getInt("fb");
                hole = rs.getString("hole");
                hr = rs.getInt("hr");
                min = rs.getInt("min");
                event_type = rs.getInt("event_type");

                teetime_id = fb + delim + courseName + delim + hr + delim + min;

                ampm = " AM";
                if (hr == 12) ampm = " PM";
                if (hr > 12) {
                    ampm = " PM";
                    hr = hr - 12;    // convert to conventional time
                }
                if (min < 10) {
                    time = hr + ":" + "0" + min + ampm;
                } else {
                    time = hr + ":" + min + ampm;
                }

                if (player1.equals("")) player1 = "&nbsp;";
                if (player2.equals("")) player2 = "&nbsp;";
                if (player3.equals("")) player3 = "&nbsp;";
                if (player4.equals("")) player4 = "&nbsp;";
                if (player5.equals("")) player5 = "&nbsp;";

                //
                //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                //
                sfb = (fb == 1) ? "B" : (fb == 9) ? "0" : (event_type == shotgun) ? "S" : "F";

                //
                //  If course=ALL requested, then set 'fives' option according to this course
                //
                if (course.equals( "-ALL-" )) {

                      i = 0;
                      loopall:
                      while (i < 20) {
                         if (courseName.equals( courseA[i] )) {
                            fives = fivesA[i];          // get the 5-some option for this course
                            break loopall;              // exit loop
                         }
                         i++;
                      }

                }

                // start new row
                out.println("<tr>");

                //
                //  Time
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\"><b>");
                out.println(time);
                out.println("</b></font></td>");


                // course name (if not single course)
                if (course.equals( "-ALL-" )) {

                    //
                    //  Course Name
                    //
                    // set tmp_i equal to course index #
                    //
                    for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                        if (courseName.equals(courseA[tmp_i])) break;                      
                    }

                    out.println("<td bgcolor=\"" + course_color[tmp_i] + "\" align=\"center\">");
                    out.println("<font size=\"2\">");
                    out.println(courseName);
                    out.println("</font></td>");
                }

                //
                //  Front/Back Indicator
                //
                out.println("<td bgcolor=\"white\" align=\"center\">");
                out.println("<font size=\"2\">");
                out.println(sfb);
                out.println("</font></td>");

                //
                //  Hole Assignment Text Box
                //
                out.println("<td align=\"center\">");
                out.println("<font size=\"2\">");
                out.println("<input type=text name=\"" + teetime_id + "\" value=\"" + hole + "\" size=6 maxlength=4>");
                out.println("</font></td>");
                /*
                //
                //  Blocker Name
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(blocker);
                out.println("</font></td>");
                */
                //
                //  Player1
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player1);
                out.println("</font></td>");

                //
                //  Player2
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player2);
                out.println("</font></td>");

                //
                //  Player3
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player3);
                out.println("</font></td>");

                //
                //  Player4
                //
                out.println("<td align=\"center\" nowrap>");
                out.println("<font size=\"2\">");
                out.println(player4);
                out.println("</font></td>");

                if (fivesALL == 1) {
                    //
                    //  Player5
                    //
                    out.println("<td align=\"center\" nowrap>");
                    out.println("<font size=\"2\">");
                    out.println(player5);
                    out.println("</font></td>");
                }


                // end row
                out.println("</tr>");

            } // end teesheet rs while loop
        
        } // end if there is at least 1 player signed up for the event
        
    }
    catch (Exception exp) {
        displayDatabaseErrMsg(exp.toString(), exp.getMessage(), out);
    }
    
    out.println("</form>");
    out.println("</body></html>");
    
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
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</CENTER></BODY></HTML>");
 }

 
 private void setBlockers(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session) {
        
    // the form should be here with each tee-time and its new 'block' setting
    // loop thru each element and locate the respective tee-time and update the blocker field

    //out.println("processing...");

    Enumeration elems  = req.getParameterNames();
    
    String name = "";
    String value = "";
    String tmp = "";
    String sindex = req.getParameter("index");
    int index = 0;
    
    if (sindex == null || sindex.equals( "" ) || sindex.equalsIgnoreCase( "null" )) {

        index = 0;

    } else {

        index = Integer.parseInt(sindex);

    }
    
    Calendar cal = new GregorianCalendar();       // get todays date
    cal.add(Calendar.DATE,index);                  // roll ahead 'index' days
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DAY_OF_MONTH);
    long date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd
    
    String sqlBlock = "UPDATE teecurr2 SET blocker = \"Auto-Blocker\" WHERE blocker = \"\" AND date = ? AND CONCAT_WS(\"_\", fb, courseName, hr, min) IN ("; 
    String sqlUnblock = "UPDATE teecurr2 SET blocker = \"\" WHERE player1 = \"\" AND date = ? AND CONCAT_WS(\"_\", fb, courseName, hr, min) IN (";
    
    boolean skipBlocks = true;
    boolean skipUnblocks = true;
    
    while (elems.hasMoreElements()) {

        name = (String)elems.nextElement();
        value = req.getParameter(name);

        if(name.startsWith("ID_") == true) {

            tmp = name.substring(3);

            if (value.equals(req.getParameter(tmp))) {

                sqlBlock += "\"" + tmp + "\",";
                skipBlocks = false;
                
            } else {

                sqlUnblock += "\"" + tmp + "\",";
                skipUnblocks = false;
            }

        }

    }
     
    sqlBlock = sqlBlock.substring(0, sqlBlock.length() - 1);
    sqlUnblock = sqlUnblock.substring(0, sqlUnblock.length() - 1);

    sqlBlock += ")";
    sqlUnblock += ")";

    //out.println("<p>" + date + " (" + index + ")</p>");
    //out.println("<p>" + sqlBlock + "</p>");
    //out.println("<p>" + sqlUnblock + "</p>");
    //out.println("<p>" + skipBlocks + " | " + skipUnblocks + "</p>");
     
    try {

        PreparedStatement pstmt = null;
            
        if (skipUnblocks != true) {
            pstmt = con.prepareStatement (sqlUnblock);
            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.executeUpdate();
             //out.println("<p>" + sqlUnblock + "</p>");
        }
        
        if (skipBlocks != true) {
            pstmt = con.prepareStatement (sqlBlock);
            pstmt.clearParameters();
            pstmt.setLong(1, date);
            pstmt.executeUpdate();
             //out.println("<p>" + sqlBlock + "</p>");
        }
        
        if (pstmt != null) pstmt.close();

    }
    catch (Exception exp) {
        displayDatabaseErrMsg("Fatal error updating tee time blockers.", exp.toString(), out);
    }
     
     
    return;
     
 }  // end setBlockers

} // end servlet
