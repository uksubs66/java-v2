/***************************************************************************************
 *   Hotel_sheet:  This servlet will process the 'View Tee Sheet' request from
 *                    the Hotel's Select page.
 *
 *
 *   called by:  Hotel_select (doPost)
 *               Hotel_slot (via Hotel_jump on a cancel)
 *
 *
 *   created: 11/18/2003   Bob P.
 *
 *   last updated:        ******* keep this accurate *******
 *
 *        5/04/11   Cordillera - do not allow access to times prior to 11:00 AM (4/15 - 10/31).
 *        4/29/11   Remove Cordillera custom lodge restrictions for now.
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        4/27/10   Cordillera - restore custom for lodge times - new owners changed their minds. (case 1830)
 *        4/19/10   Changes for unlimited guest types
 *        2/04/10   Cordillera - remove custom for lodge times (new owners don't want it).
 *       12/14/09   Bay Hill - removed custom that blocked hotel users from tee times < 8 days in advance (case 1559).
 *       12/09/09   When looking for events only check those that are active.
 *       12/02/09   Allow for Saudi Arabia time - adjustTime.
 *       12/09/08   Added guest restriction suspension handling for legend and tee sheet display
 *       12/01/08   Bay Hill - display member notices (case 1563).
 *       11/26/08   Added restriction suspension display processing
 *       10/15/08   Inverness - do not change the player names - allow hotel user to see names and guest types (case 1376).
 *       10/07/08   Cordillera - Add Forecaddie column/support to Hotel teesheet (case 1500).
 *       10/07/08   Bay Hill - do not allow hotel users access to tee times if within 7 days in advance (case 1559).
 *        3/06/08   Cordillera - change custom lodge time check to change bgcolor even when allow=false already set.
 *       11/16/07   Add -ALL- option for course selection  (Case #1111)
 *        1/18/07   Cordillera - change the date checks for custom rest's.
 *        4/20/06   Cordillera - do not allow members to access tee times on the day of (today).
 *        3/08/05   Updated calendars to use new calv30 version - the dynamic calendar is now sticky
 *        4/03/05   Cordillera - add some custom hotel restrictions.
 *       12/20/04   Sawgrass - do not allow more than 16 tee times per day for hotel.
 *                             Also, do not allow hotel to change a tee time within 24 hrs of time.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *                     - remove adv_hr and adv_min as not needed for hotel user.
 *        3/10/04   RDP Change calendars to js cals for 365 day support.
 *        2/06/04   Add support for configurable transportation modes.
 *       12/15/02   Bob P.   Do not show member restriction in legend if showit=no.
 *       01/07/04   JAG  Modified to match new color scheme
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
//import java.lang.Math;

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.BigDate;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.cordilleraCustom;
import com.foretees.common.verifySlot;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;


public class Hotel_sheet extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process the return from Hotel_jump
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }


 //*****************************************************
 // Process the request from Hotel_select
 //*****************************************************
 //

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out;


   PreparedStatement pstmt = null;
   PreparedStatement pstmt5 = null;
   Statement stmt = null;
   //Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   //ResultSet rs4 = null;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
/*
   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };
*/
   //
   //  Num of days in each month
   //
   int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };


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

   HttpSession session = SystemUtils.verifyHotel(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/motel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   int hr = 0;
   int min = 0;
   int tee_time = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int type = 0;                       // event type
   int shotgun = 1;                    // event type = shotgun
   int in_use = 0;
   //int sawgrassCount = 0;
     
   short fb = 0;

   String name = "";
   String num = "";
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
   String ampm = "";
   //String event_rest = "";
   String bgcolor = "";
   String sfb = "";

   String event1 = "";       // for legend - max 2 events, 4 rest's, 4 guest rest's
   String ecolor1 = "";
   String event2 = "";
   String ecolor2 = "";
   String rest1 = "";
   String rcolor1 = "";
   String rest2 = "";
   String rcolor2 = "";
   String rest3 = "";
   String rcolor3 = "";
   String rest4 = "";
   String rcolor4 = "";
   String grest1 = "";
   String grest2 = "";
   String grest3 = "";
   String grest4 = "";
   String gcolor = "";
   String gcolor2 = "";
   String guest_uid1 = "";
   String guest_uid2 = "";
   String guest_uid3 = "";
   String guest_uid4 = "";
   String guest_uid5 = "";
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;
   
   String blocker = "";
   //String adv_ampm = "";

   //String mem1 = "";
   //String mem2 = "";
   //String mem3 = "";
   //String mem4 = "";
   //String mem5 = "";
   //String mem6 = "";
   //String mem7 = "";
   //String mem8 = "";

   String rcourse = "";
   String grest_recurr = "";
   String grest_color = "";

   String courseNameT2 = "";
   String rest_recurr = "";
   //String rest_name = "";
   String sfb2 = "";
   //String rest_color = "";
   String rest_fb = "";
   String jumps = "";
   String lottery = "";

   String calDate = "";
   String stime = "";
   String err = "";

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int days1 = 0;               // days in advance that Hotels can make tee times
   int days2 = 0;               //         one per day of week (Sun - Sat)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   //int hndcp = 0;
   int index = 0;
   int index2 = 0;
   int days = 0;
   int multi = 0;               // multiple course support
   int i = 0;
   int i2 = 0;
   int fives = 0;               // support 5-somes
   int g1 = 0;                  // guest indicators
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91;
   int p92;
   int p93;
   int p94;
   int p95;
   //int ind = 0;
   int j = 0;
   int k = 0;                   // form id counter
   int jump = 0;
   //int lott = 0;
   int numCaddies = 0;             // Cordillera - forecaddie indicator
   int courseCount = 0;

   int cal_time = 0;            // calendar time for compares

   int tmp_i = 0;               // counter for course[], shading of course field

   boolean allow = true;
   boolean block = false;
   boolean suspend = false;            // Member restriction suspension
   boolean restrictAll = false;

   //
   //  parm block to hold the Course Colors
   //
   parmCourseColors colors = new parmCourseColors();          // allocate a parm block

   int colorMax = colors.colorMax;             // max number of colors defined
   
   //
   //  Array to hold the course names
   //
   int cMax = 0;                               
   int fivesALL = 0;
   String courseName = "";
   
   ArrayList<String> course = new ArrayList<String>();
  
   ArrayList<Integer> fivesA = new ArrayList<Integer> ();        // array list to hold 5-some option for each course
   
   
   
   //
   //  Array to hold the Guest Restriction's guest types
   //
   ArrayList<String> rguest = new ArrayList<String>();  // retricted guests (loaded for each matching restriction)
   ArrayList<String> hguest = new ArrayList<String>();  // available guests this hotel user can access (was xguests[])
   
   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();

   //
   //  parm block to hold the member restrictions for this date and member
   //
   parmRest parmr = new parmRest();
   
   //
   //  Get this user's username and club name
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");      // get club name

   //
   //  Check if we came from Hotel_select - If so, calculate the index value passed
   //
   if (req.getParameter("calDate") != null) {

      //
      //************************************************************
      //  From Hotel_select
      //
      //   Convert the date received from mm/dd/yyyy to 'index' value
      //
      //   (index no longer passed from Hotel_select as of V4)
      //
      //************************************************************
      //
      //
      //  make sure we have the date value
      //
      calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)

      //
      //  Convert the index value from string to int
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      num = tok.nextToken();                    // get the mm value

      month = Integer.parseInt(num);

      num = tok.nextToken();                    // get the dd value

      day = Integer.parseInt(num);

      num = tok.nextToken();                    // get the yyyy value

      year = Integer.parseInt(num);

      //
      //  Get today's date and then set the requested date to get the day name, etc.
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      cal.set(Calendar.YEAR, year);                 // change to requested date
      cal.set(Calendar.MONTH, month-1);
      cal.set(Calendar.DAY_OF_MONTH, day);

      day_num = cal.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)

      //
      // Calculate the number of days between today and the date requested (=> ind)
      //
      BigDate today = BigDate.localToday();                 // get today's date
      BigDate thisdate = new BigDate(year, month, day);     // get requested date

      index = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between
        
      name = "i" + index;               // create index parm to pass to _slot 

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
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H3>Access Procdure Error</H3>");
         out.println("<BR><BR>Required Parameter is Missing - Hotel_sheet.");
         out.println("<BR>Please exit and try again.");
         out.println("<BR><BR>If problem persists, report this error to your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Convert the index value from string to int
      //
      StringTokenizer tok = new StringTokenizer( name, "i" );     // space is the default token - use 'i'

      num = tok.nextToken();                // get just the index number (name= parm must start with alpha)

      try {
         index = Integer.parseInt(num);
      }
      catch (NumberFormatException e) {
         // ignore error
      }
   }

   index2 = index;     // save for later (number of days from today)

   //
   //  Get the golf course name requested
   //
   String courseName1 = req.getParameter("course");

   if (courseName1 == null || courseName1.equals( "null" )) {

      courseName1 = "";     // change to other null
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
   Calendar cal = new GregorianCalendar();          // get todays date

   cal.add(Calendar.DATE,index);                    // roll ahead 'index' days

   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_num = cal.get(Calendar.DAY_OF_WEEK);         // day of week (01 - 07)

   month++;                                         // month starts at zero

   String day_name = day_table[day_num];            // get name for day

   long date = year * 10000;                        // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                               // date = yyyymmdd (for comparisons)

   long dateShort = (month * 100) + day;          // create date of mmdd for customs
  
   //
   //   if Cordillera and call was to update the forecaddie indicator - go process
   //
   if (club.equals( "cordillera" ) && req.getParameter("forecaddy") != null) {

      String forecaddy = req.getParameter("forecaddy");      //  get forecaddie indicator
      stime = req.getParameter("time");                      //  time of the slot
      sfb = req.getParameter("fb");                          //  front/back indicator
      courseNameT2 = req.getParameter("courseT");             //  course name for tee time

      updateForeCaddie(stime, sfb, date, forecaddy, courseNameT2, out, con);   // update the record & continue to display the sheet
   }
   
   
   try {

      //
      // Get the Multiple Course Option, guest types and time for advance from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;
      
      if (multi != 0) {           // if multiple courses supported for this club

         //
         //  Get the names of all courses for this club
         //
         course = Utilities.getCourseNames(con);     // get all the course names
          
         courseCount = course.size();
         
         if (courseCount > 1) {                // if more than 1 course, add -ALL- option

            course.add ("-ALL-");
         }
      }
  
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>Error loading course array: " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   out.println("<!-- courseCount="+courseCount+" -->");
   
   try {
          
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
      
   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>Error determining five-some support for course " + courseName + ": " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   // main try catch for this page
   try {
/*
      //
      //  Get the walk/cart options available, and the 5-some option
      //
      getParms.getCourse(con, parmc, courseName1);

      fives = parmc.fives;      // get fivesome option
*/

      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT * " +
         "FROM hotel3 WHERE username = ?");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         days1 = rs.getInt("days1");
         days2 = rs.getInt("days2");
         days3 = rs.getInt("days3");
         days4 = rs.getInt("days4");
         days5 = rs.getInt("days5");
         days6 = rs.getInt("days6");
         days7 = rs.getInt("days7");

         // now look up the guest types for this restriction
         PreparedStatement pstmt3 = con.prepareStatement (
                 "SELECT guest_type FROM hotel3_gtypes WHERE username = ?");

         pstmt3.clearParameters();
         pstmt3.setString(1, user);

         rs3 = pstmt3.executeQuery();

         hguest.clear();

         while ( rs3.next() ) {

             hguest.add(rs3.getString("guest_type"));

         }
         pstmt3.close();

      }
      pstmt2.close();


      //
      //  Check for any guest restrictions for this hotel's guests
      //
      pstmt5 = con.prepareStatement (
         "SELECT * " +
         "FROM guestres2 WHERE sdate <= ? AND edate >= ? AND activity_id = 0");

      pstmt5.clearParameters();
      pstmt5.setLong(1, date);
      pstmt5.setLong(2, date);
      rs2 = pstmt5.executeQuery();

      while (rs2.next()) {

         grest_recurr = rs2.getString("recurr");
         rcourse = rs2.getString("courseName");
         rest_fb = rs2.getString("fb");
         gcolor2 = rs2.getString("color");

         // now look up the guest types for this restriction
         PreparedStatement pstmt3 = con.prepareStatement (
                 "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

         pstmt3.clearParameters();
         pstmt3.setInt(1, rs2.getInt("id"));

         rs3 = pstmt3.executeQuery();

         rguest.clear(); // reset

         while ( rs3.next() ) {

            rguest.add(rs3.getString("guest_type"));

         }

         pstmt3.close();

         //
         //  Check if course matches that specified in restriction
         //
         if (rcourse.equals( "-ALL-" ) || rcourse.equals( courseName1 ) || courseName1.equals( "-ALL-" )) {

            //
            //  We must check the recurrence for this day (Monday, etc.) and guest types
            //
            //     guestx = guest types specified for this hotel user
            //     rguestx = guest types from restriction gotten above
            //
            if ((grest_recurr.equalsIgnoreCase( "every " + day_name )) ||
                (grest_recurr.equalsIgnoreCase( "every day" )) ||
                ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                 (!day_name.equalsIgnoreCase( "saturday" )) &&
                 (!day_name.equalsIgnoreCase( "sunday" ))) ||
                ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                 (day_name.equalsIgnoreCase( "saturday" ))) ||
                ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                 (day_name.equalsIgnoreCase( "sunday" )))) {

               boolean showRest = getRests.showRest(-99, rs2.getInt("id"), rs2.getInt("stime"), rs2.getInt("etime"), date, day_name, courseName1, con);

               if (showRest) {    // Only display on legend if not suspended for entire day
               
                   i = 0;
                   while (i < hguest.size() ) {     // check all guest types for this hotel user (parm.MAX_Guests)

                      //if (!parm.guest[i].equals( "" )) {  // if guest type specified for user (must be at least 1)

                         i2 = 0;
                         ploop1:
                         while (i2 < rguest.size()) {     // check all guest types for this restriction

                            if ( rguest.get(i2).equals( hguest.get(i) )) {

                               if ((!hguest.get(i).equals( grest1 )) && (grest1.equals( "" ))) {

                                  grest1 = hguest.get(i);   // indicate guest restriction for this user today
                                  gcolor = gcolor2;         // set color 

                               } else {

                                  if ((!hguest.get(i).equals( grest1 )) && (!hguest.get(i).equals( grest2 )) && (grest2.equals( "" ))) {

                                     grest2 = hguest.get(i);  // indicate guest restriction for this user today

                                     if (!gcolor2.equals( "" )) {
                                        gcolor = gcolor2;         // set color
                                     }

                                  } else {

                                     if ((!hguest.get(i).equals( grest1 )) && (!hguest.get(i).equals( grest2 )) && (!hguest.get(i).equals( grest3 )) &&
                                         (grest3.equals( "" ))) {

                                        grest3 = hguest.get(i);   // indicate guest restriction for this user today

                                        if (!gcolor2.equals( "" )) {
                                           gcolor = gcolor2;         // set color
                                        }

                                     } else {

                                        if ((!hguest.get(i).equals( grest1 )) && (!hguest.get(i).equals( grest2 )) && (!hguest.get(i).equals( grest3 )) &&
                                            (!hguest.get(i).equals( grest4 )) && (grest4.equals( "" ))) {

                                           grest4 = hguest.get(i); // indicate guest restriction for this user today

                                           if (!gcolor2.equals( "" )) {
                                              gcolor = gcolor2;         // set color
                                           }
                                        }
                                     }
                                  }
                               }
                               break ploop1;
                            }
                            i2++;
                         }
                      //}
                      i++;
                   } // end of WHILE hotel user guest types
               }
            }
         } // end of IF course matches
      } // end of loop3 while loop (while guest restrictions exist)

      pstmt5.close();

      //
      //  set color if not specified
      //
      if (gcolor.equals( "" )) {

         gcolor = "F5F5DC";
      }


      //
      //  Get all restrictions for this day and user (for use when checking each tee time below)
      //
      parmr.user = user;
      parmr.mship = "";
      parmr.mtype = "";
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
         string7b = "SELECT name, recurr, color, id, stime, etime " +
                    "FROM restriction2 " +
                    "WHERE sdate <= ? AND edate >= ? AND showit = 'Yes' AND activity_id = 0 " +
                    "ORDER BY stime";
      } else {
         string7b = "SELECT name, recurr, color, id, stime, etime " +
                    "FROM restriction2 " +
                    "WHERE sdate <= ? AND edate >= ? AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' AND activity_id = 0 " +
                    "ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7c = "SELECT name, color, act_hr, act_min " +
                    "FROM events2b WHERE date = ? AND inactive = 0 " +
                    "ORDER BY stime";
      } else {
         string7c = "SELECT name, color, act_hr, act_min " +
                    "FROM events2b WHERE date = ? AND inactive = 0 AND (courseName = ? OR courseName = '-ALL-') AND activity_id = 0 " +
                    "ORDER BY stime";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);

      //
      //  Scan the events, restrictions to build the legend
      //
      pstmt7b.clearParameters();          // clear the parms
      pstmt7b.setLong(1, date);
      pstmt7b.setLong(2, date);

      if (!courseName1.equals( "-ALL-" )) {
         pstmt7b.setString(3, courseName1);
      }
           
      rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

      while (rs.next()) {

         rest = rs.getString(1);
         rest_recurr = rs.getString(2);
         rcolor = rs.getString(3);
         
         boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, courseName1, con);
         
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


      //
      //  Build the HTML page to prompt user for a specific time slot
      //
      out.println("<HTML><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) ");
      out.println("is the proprietary property of ForeTees, LLC or its suppliers and its use, modification and distribution are protected ");
      out.println("and limited by United States copyright laws and international treaty provisions and all other applicable national laws. ");
      out.println("Reproduction is prohibited except for backup purposes.-->");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>ForeTees Hotel Tee Sheet Page </TITLE>");
      out.println("<script type=\"text/javascript\">");          // Jump script
      out.println("<!--");

      out.println("function jumpToHref(anchorstr) {");

      out.println("if (location.href.indexOf(anchorstr)<0) {");

      out.println("location.href=anchorstr; }");
      out.println("}");
      out.println("// -->");
      out.println("</script>");                               // End of script

      out.println("</HEAD>");

      // include files for dynamic calendars
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
      out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

//      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
//      out.println("<script language=\"javascript\" src=\"/" +rev+ "/cal-scripts.js\"></script>");


      out.println("<body onLoad='jumpToHref(\"#jump" + jump + "\");' bgcolor=\"#ccccaa\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"></font><center>");

      out.println("<a name=\"jump0\"></a>");     // create a default jump label (start of page)

      out.println("<table border=\"0\" align=\"center\" width=\"95%\">");         // table for main page

      out.println("<tr><td valign=\"top\" align=\"center\">");

      //**********************************************************
      //  Build calendar for selecting a new day
      //**********************************************************

      //
      //  Get today's date and setup parms to use when building the calendar
      //
      Calendar cal2 = new GregorianCalendar();          // get todays date
      int year2 = cal2.get(Calendar.YEAR);
      int month2 = cal2.get(Calendar.MONTH);
      int day2 = cal2.get(Calendar.DAY_OF_MONTH);
      int day_num2 = cal2.get(Calendar.DAY_OF_WEEK);    // day of week (01 - 07)
      int cal_am_pm = cal.get(Calendar.AM_PM);          // current time
      int cal_hour = cal.get(Calendar.HOUR);
      int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
      int cal_min = cal.get(Calendar.MINUTE);

      cal_time = (cal_hourDay * 100) + cal_min;

      cal_time = SystemUtils.adjustTime(con, cal_time); // adjust the time

      if (cal_time < 0) {                               // if negative, then we went back or ahead one day

         cal_time = 0 - cal_time;                       // convert back to positive value

         if (cal_time < 1200) {                         // if AM, then we rolled ahead 1 day (allow for Saudi time)

            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal.add(Calendar.DATE,1);                   // get next day's date

            year2 = cal.get(Calendar.YEAR);
            month2 = cal.get(Calendar.MONTH);
            day2 = cal.get(Calendar.DAY_OF_MONTH);
            day_num2 = cal.get(Calendar.DAY_OF_WEEK);   // day of week (01 - 07)

         } else {                                       // we rolled back 1 day

            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal.add(Calendar.DATE,-1);                  // get yesterday's date

            year2 = cal.get(Calendar.YEAR);
            month2 = cal.get(Calendar.MONTH);
            day2 = cal.get(Calendar.DAY_OF_MONTH);
            day_num2 = cal.get(Calendar.DAY_OF_WEEK);   // day of week (01 - 07)
         }
      }

      //int today2 = day2;                              // save today's number

      month2++;                                         // month starts at zero

      //String mm = mm_table[month2];                   // month name

      int numDays = numDays_table[month2];              // number of days in month

      if (numDays == 0) {                               // if Feb

         int leapYear = year2 - 2000;
         numDays = feb_table[leapYear];                 // get days in Feb
      }

      //
      //  put the 'days in advance' values in an array to be used when building the calendars below
      //
      advdays[0] = days1;
      advdays[1] = days2;
      advdays[2] = days3;
      advdays[3] = days4;
      advdays[4] = days5;
      advdays[5] = days6;
      advdays[6] = days7;

      //
      // determine days in advance for this day (day of sheet)
      //
      days = day_num;                   // get this day's number value (1 - 7)
      days--;                           // convert to index
      days = advdays[days];             // get days in advance

      //int count = 0;                    // init day counter
      //int col = 0;                       // init column counter
      //int d = 0;                         // 'days in advance' value for current day of week
      //int max = 90;                      // max # of days to display


      //
      //  If today, or the day before and after 5:00 PM MT, then do not allow hotel users to access any tee times.
      //
      if ((index == 0 || (index == 1 && cal_time > 1700)) &&
           club.equals( "cordillera" )) {

         restrictAll = true;         // indicate no hotel access
      }

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         String caldate = month + "/" + day + "/" + year;       // create date for _jump

         //
         //  use 2 forms so you can switch by clicking either a course or a date
         //
         if (courseCount < 5) {        // if < 5 courses, use buttons

            i = 0;
            courseName = course.get(i);      // get first course name from array

            out.println("<p><font size=\"3\">");
            out.println("<b>Select Course or Date:</b>&nbsp;&nbsp;");

            while (i < course.size()) {    // allow for -ALL-

               out.println("<a href=\"Hotel_sheet?jump=select&calDate=" +caldate+ "&course=" +course.get(i)+ "\" style=\"color:blue\" target=\"bot\" title=\"Switch to new course\" alt=\"" +course.get(i)+ "\">");
               out.println(course.get(i)+ "</a>");
               out.println("&nbsp;&nbsp;&nbsp;");

               i++;
            }
            out.println("</p>");

         } else {     // use drop-down menu

            out.println("<form action=\"Hotel_sheet\" method=\"post\" name=\"cform\" target=\"bot\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +caldate+ "\">");

            out.println("<b>Course:</b>&nbsp;&nbsp;");
            out.println("<div id=\"awmobject1\">");                      // allow menus to show over this box
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
      } // end if multi


      //
      //  start a new form for the dates so you can switch by clicking either a course or a date
      //
      out.println("<form action=\"Hotel_sheet\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName1+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");
      out.println("</form>");
  
      //
      //   Add calendars
      //
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



      //**********************************************************
      //  Continue with instructions and tee sheet
      //**********************************************************

      if (index2 <= days) {                      // check max allowed days in advance for Hotels

         out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"680\">");
         out.println("<tr><td align=\"left\"><font color=\"ffffff\" size=\"2\">");

         out.println("<b>Instructions:</b>  To select a tee time, just click on the button containing the time (1st column). ");
         out.println(" Special Events and Restrictions, if any, are colored (see legend below). ");
         out.println(" To display a different day's tee sheet, select the date from the calendar above.");

      } else {

         out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"600\">");
         out.println("<tr><td align=\"left\"><font color=\"ffffff\" size=\"2\">");

         out.println("<b>Note:</b>&nbsp;&nbsp;Since this date is more than " + days + " days from today's date, ");
         out.println("you cannot reserve any times.  You are allowed to view this sheet for planning purposes only.");
      }
      out.println("</font></td></tr></table>");

      out.println("<p>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");

      if (!courseName1.equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
      }

      out.println("</p><font size=\"2\">");

      //
      //  Display a note if Hotel not allowed to access tee times today
      //
      if (restrictAll == true) {

         out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the Golf Shop for tee times today.</button><br>");
      }


      out.println("<b>Tee Sheet Legend</b>");
      out.println("</font><font size=\"1\"><br>");

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

               if (!rest4.equals( "" )) {

                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
                  out.println("<button type=\"button\" style=\"background:" + rcolor4 + "\">" + rest4 + "</button>");
               }
            }
         }
      }

      if (!grest1.equals( "" )) {

         out.println("<button type=\"button\" style=\"background:" + gcolor + "\">Guest(s) Restricted: " + grest1 + "&nbsp; " + grest2 + "&nbsp; " + grest3 + "&nbsp; " + grest4 + "</button>");
      }

      if (!event1.equals( "" ) || !rest1.equals( "" ) || !grest1.equals( "" )) {

         out.println("<br>");
      }

      out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event<br>");

      out.println("<b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");

      for (int ic=0; ic<16; ic++) {

         if (!parmc.tmodea[ic].equals( "" )) {
            out.println(parmc.tmodea[ic]+ " = " +parmc.tmode[ic]+ "&nbsp;&nbsp;&nbsp;");
         }
      }
      out.println("(__9 = 9 holes)");
      
      if (club.equals( "cordillera" )) {                  // if Cordillera

         out.println("&nbsp;&nbsp;&nbsp;<b>FC:</b> Forecaddie Requested");
      }

      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      //
      String memNoticeMsg = verifySlot.checkMemNotice(date, 0, 0, courseName1, day_name, "teesheet", false, con);

      if (!memNoticeMsg.equals("")) {

          int notice_mon = 0;
          int notice_tue = 0;
          int notice_wed = 0;
          int notice_thu = 0;
          int notice_fri = 0;
          int notice_sat = 0;
          int notice_sun = 0;

          String notice_msg = "";
          String notice_bgColor = "";

          try {

              // Get relevent member notice data from database
              ResultSet notice_rs = null;
              PreparedStatement notice_pstmt = con.prepareStatement(
                      "SELECT mon, tue, wed, thu, fri, sat, sun, message, bgColor " +
                      "FROM mem_notice " +
                      "WHERE sdate <= ? AND edate >= ? AND " +
                      "(courseName = ? OR courseName = ?) AND teesheet=1");

              notice_pstmt.clearParameters();        // clear the parms and check player 1
              notice_pstmt.setLong(1, date);
              notice_pstmt.setLong(2, date);
              notice_pstmt.setString(3, courseName1);
              notice_pstmt.setString(4, "-ALL-");
              notice_rs = notice_pstmt.executeQuery();      // execute the prepared stmt

              out.println("<br><br><table border=\"2\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"0\" cellspacing=\"0\">");
              out.println("<tr><td><table border=\"0\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
              out.println("<tr><td align=\"center\" valign=\"center\"><font size=\"4\"><b>*** Important Notice ***</b></font></td></tr>");

              while (notice_rs.next()) {

                  notice_mon = notice_rs.getInt("mon");
                  notice_tue = notice_rs.getInt("tue");
                  notice_wed = notice_rs.getInt("wed");
                  notice_thu = notice_rs.getInt("thu");
                  notice_fri = notice_rs.getInt("fri");
                  notice_sat = notice_rs.getInt("sat");
                  notice_sun = notice_rs.getInt("sun");
                  notice_msg = notice_rs.getString("message");
                  notice_bgColor = notice_rs.getString("bgColor");


                  if ((notice_mon == 1 && day_name.equals( "Monday")) || (notice_tue == 1 && day_name.equals( "Tuesday")) || (notice_wed == 1 && day_name.equals( "Wednesday")) ||
                          (notice_thu == 1 && day_name.equals( "Thursday")) || (notice_fri == 1 && day_name.equals( "Friday")) || (notice_sat == 1 && day_name.equals( "Saturday")) ||
                          (notice_sun == 1 && day_name.equals( "Sunday"))) {

                      out.println("<tr>");
                      if (!notice_bgColor.equals("")) {
                          out.println("<td width=\"120\" bgColor=\"" + notice_bgColor + "\" align=\"center\">");
                      } else {
                          out.println("<td width=\"120\" align=\"center\">");
                      }
                      out.println("<font size=\"2\">");
                      out.println("<pre width=\"120\"><font size=\"2\" face=\"Times New Roman, Serif\">" + notice_msg + "</font></pre>");
                      out.println("</font></td></tr>");
                  }

              }  // end WHILE loop

              out.println("</table></td></tr></table><br><br>");

              notice_pstmt.close();

          } catch (Exception e1) {

              out.println(SystemUtils.HeadTitle("DB Error"));
              out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
              out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
              out.println("<BR><BR><H2>Database Access Error</H2>");
              out.println("<BR><BR>Unable to access the Database.");
              out.println("<BR>Please try again later.");
              out.println("<BR><BR>If problem persists, contact your club manager.");
              out.println("<BR><BR>" + e1.getMessage());
              out.println("<BR><BR>");
              out.println("<a href=\"Member_announce\">Return</a>");
              out.println("</CENTER></BODY></HTML>");
              out.close();
          }
      } // end if member notice
         
         
      out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"95%\">");    // tee sheet table
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"2\">");
               out.println("<u><b>Time</b></u>");
               out.println("</font></td>");

            if (courseName1.equals( "-ALL-" )) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"#FFFFFF\" size=\"2\">");
                  out.println("<u><b>Course</b></u>");
               out.println("</font></td>");
            }

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"1\">");
               out.println("<u><b>F/B</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"2\">");
               out.println("<u><b>Player 1</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"2\">");
               out.println("<u><b>Player 2</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"2\">");
               out.println("<u><b>Player 3</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"2\">");
               out.println("<u><b>Player 4</b></u> ");
               out.println("</font></td>");

            out.println("<td align=\"center\">");
               out.println("<font color=\"ffffff\" size=\"1\">");
               out.println("<u><b>C/W</b></u>");
               out.println("</font></td>");

            if (fivesALL != 0 ) {

               out.println("<td align=\"center\">");
                  out.println("<font color=\"ffffff\"  size=\"2\">");
                  out.println("<u><b>Player 5</b></u> ");
                  out.println("</font></td>");

               out.println("<td align=\"center\">");
                  out.println("<font color=\"ffffff\" size=\"1\">");
                  out.println("<u><b>C/W</b></u>");
                  out.println("</font></td>");
            }
            if (club.equals( "cordillera" )) {          // if Cordillera

               out.println("<td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<u><b>FC</b></u>");
               out.println("</font></td>");
            }
            out.println("</tr>");

      //
      //  Get the tee sheet for this date and course
      //
      String courseNameT = "";
      String stringTee = "";
      
      if (courseName1.equals("-ALL-")) {
          
          stringTee = "" +
                  "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, event_type, hndcp1, hndcp2, hndcp3, " +
                     "hndcp4, fb, player5, p5cw, hndcp5, lottery, blocker, rest5, rest5_color, " +
                     "p91, p92, p93, p94, p95, courseName, pos5, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
                 "FROM teecurr2 " +
                 "WHERE date = ? ORDER BY time, courseName, fb";
      } else {
      
          stringTee = "" +
                  "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, event_type, hndcp1, hndcp2, hndcp3, " +
                     "hndcp4, fb, player5, p5cw, hndcp5, lottery, blocker, rest5, rest5_color, " +
                     "p91, p92, p93, p94, p95, courseName, pos5, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
                 "FROM teecurr2 " +
                 "WHERE date = ? AND courseName = ? ORDER BY time, fb";
      }
      pstmt = con.prepareStatement ( stringTee );

      pstmt.clearParameters();
      pstmt.setLong(1, date);
      if (!courseName1.equals("-ALL-")) pstmt.setString(2, courseName1);
      rs = pstmt.executeQuery();

      loop2:
      while (rs.next()) {

         hr = rs.getInt(1);
         min = rs.getInt(2);
         tee_time = rs.getInt(3);
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
         hndcp1 = rs.getFloat(18);
         hndcp2 = rs.getFloat(19);
         hndcp3 = rs.getFloat(20);
         hndcp4 = rs.getFloat(21);
         fb = rs.getShort(22);
         player5 = rs.getString(23);
         p5cw = rs.getString(24);
         hndcp5 = rs.getFloat(25);
         lottery = rs.getString(26);
         blocker = rs.getString(27);
         rest5 = rs.getString(28);
         bgcolor5 = rs.getString(29);
         p91 = rs.getInt(30);
         p92 = rs.getInt(31);
         p93 = rs.getInt(32);
         p94 = rs.getInt(33);
         p95 = rs.getInt(34);
         courseNameT = rs.getString("courseName");
         
         if (club.equals( "cordillera" )) {          // If Cordillera - get forecaddie indicator (saved in pos5)
            numCaddies = rs.getInt("pos5");          // use this field since not used by Cordillera
         }
         guest_id1 = rs.getInt("guest_id1");
         guest_id2 = rs.getInt("guest_id2");
         guest_id3 = rs.getInt("guest_id3");
         guest_id4 = rs.getInt("guest_id4");
         guest_id5 = rs.getInt("guest_id5");

         //
         //  If course=ALL requested, then set 'fives' option according to this course
         //
         if (courseName1.equals( "-ALL-" )) {
               i = 0;
               loopall:
               while (i < course.size()) {
                  if (courseNameT.equals( course.get(i) )) {
                     
                     fives = fivesA.get(i);      // get the 5-some option for this course
                     break loopall;              // exit loop
                  }
                  i++;
               }
         }
         
         //
         //  Custom check for Cordillera Lodge Restrictions
         //
         if (club.equals( "cordillera" ) && !courseNameT.equals("Short")) {

            if (dateShort > 414 && dateShort < 1101 && tee_time < 1100) {         // if this is within the custom date range

               blocker = "temp";      // fake a blocker - no lodge access to these times
            }
         }
         
         
         if (blocker.equals( "" )) {    // continue if tee time not blocked - else skip

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

            g1 = 0;               // init guest indicators
            g2 = 0;
            g3 = 0;
            g4 = 0;
            g5 = 0;
            allow = true;         // init allow flag

            //
            //  Check if any player names are hotel guests for this hotel user.
            //  Change all other names to 'member' and don't allow access to these times.
            //
            if (!player1.equals( "" )) {

               i = 0;
               gloop1:
               while (i < hguest.size()) {

                  if (player1.startsWith( hguest.get(i) )) {

                     g1 = 1;       // indicate player1 is a hotel guest
                     guest_uid1 = Common_guestdb.buildUIDString(guest_id1, con);
                     break gloop1;
                  }
                  i++;
               }
               if (g1 == 0) {

                  if (!club.equals("inverness")) {
                     player1 = "Member";             // change name if NOT Inverness
                  }
                  allow = false;            // do not allow access to tee time
               }
            }
            if (!player2.equals( "" )) {

               i = 0;
               gloop2:
               while (i < hguest.size()) {

                  if (player2.startsWith( hguest.get(i) )) {

                     g2 = 1;       // indicate player2 is a hotel guest
                     guest_uid2 = Common_guestdb.buildUIDString(guest_id1, con);
                     break gloop2;
                  }
                  i++;
               }
               if (g2 == 0) {

                  if (!club.equals("inverness")) {
                     player2 = "Member";             // change name if NOT Inverness
                  }
                  allow = false;            // do not allow access to tee time
               }
            }
            if (!player3.equals( "" )) {

               i = 0;
               gloop3:
               while (i < hguest.size()) {

                  if (player3.startsWith( hguest.get(i) )) {

                     g3 = 1;       // indicate player3 is a hotel guest
                     guest_uid3 = Common_guestdb.buildUIDString(guest_id1, con);
                     break gloop3;
                  }
                  i++;
               }
               if (g3 == 0) {

                  if (!club.equals("inverness")) {
                     player3 = "Member";             // change name if NOT Inverness
                  }
                  allow = false;            // do not allow access to tee time
               }
            }
            if (!player4.equals( "" )) {

               i = 0;
               gloop4:
               while (i < hguest.size()) {

                  if (player4.startsWith( hguest.get(i) )) {

                     g4 = 1;       // indicate player4 is a hotel guest
                     guest_uid4 = Common_guestdb.buildUIDString(guest_id1, con);
                     break gloop4;
                  }
                  i++;
               }
               if (g4 == 0) {

                  if (!club.equals("inverness")) {
                     player4 = "Member";            // change name if NOT Inverness
                  }
                  allow = false;            // do not allow access to tee time
               }
            }
            if (!player5.equals( "" )) {

               i = 0;
               gloop5:
               while (i < hguest.size()) {

                  if (player5.startsWith( hguest.get(i) )) {

                     g5 = 1;       // indicate player5 is a hotel guest
                     guest_uid5 = Common_guestdb.buildUIDString(guest_id1, con);
                     break gloop5;
                  }
                  i++;
               }
               if (g5 == 0) {

                  if (!club.equals("inverness")) {
                     player5 = "Member";            // change name if NOT Inverness
                  }
                  allow = false;            // do not allow access to tee time
               }
            }

            //
            //  Check if Hotel not allowed to access tee times today
            //
            if (restrictAll == true) {

               allow = false;            // not today!
            }

            //
            // check if we should allow user to select this slot
            // check max allowed days in advance, special event, lottery or cross-over time
            //
            if ((index2 > days) || (!event.equals("")) || (fb == 9) || (!lottery.equals(""))) {

               allow = false;
            }

            /*      // removed 12/14/09 per Balke Terry's request
            //
            //   Bay Hill - do not allow access if within 7 days in advance (they must call golf shop)
            //
            if (club.equals("bayhill") && index2 < 8) {

               allow = false;
            }
             */

            
            //
            //  if today's sheet and the tee time is less than the current time do not allow select
            //
            if ((index2 == 0) && (tee_time <= cal_time)) {

               allow = false;     // do not allow select

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

               sfb = "S";            // there's an event and its type is 'shotgun'
            }

            //
            // if restriction for this slot and its not the first time for a lottery, check restriction for this user
            //
            if (!rest.equals("") && !rcolor.equals("")) {
                
                int indx = 0;
                while (indx < parmr.MAX && !parmr.restName[indx].equals("")) {

                    if (parmr.restName[indx].equals(rest)) {

                        // Check to make sure no suspensions apply
                        suspend = false;                        
                        for (int m=0; m<parmr.MAX; m++) {

                            if (parmr.susp[indx][m][0] == 0 && parmr.susp[indx][m][1] == 0) {
                                m = parmr.MAX;   // don't bother checking any more
                            } else if (parmr.susp[indx][m][0] <= tee_time && parmr.susp[indx][m][1] >= tee_time) {    // time falls within a suspension
                                suspend = true;                       
                                m = parmr.MAX;     // don't bother checking any more
                            }
                        }      // end of for loop

                        if (suspend) {

                            if ((parmr.courseName[indx].equals( "-ALL-" )) || (parmr.courseName[indx].equals( courseNameT ))) {  // course ?

                                if ((parmr.fb[indx].equals( "Both" )) || (parmr.fb[indx].equals( sfb2 ))) {    // matching f/b ?

                                    //
                                    //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                    //
                                    if (event.equals("") && lottery.equals("")) {           // change color back to default if no event

                                        // Search for the first non-suspended color to apply, or default if non found
                                        bgcolor = "#F5F5DC";   // default color

                                        int ind2 = 0;
                                        while (ind2 < parmr.MAX && !parmr.restName[ind2].equals("")) {

                                            // make sure it's not the default restriction/color, and has a non-blank, non-default color
                                            // and applies to this time
                                            if (!parmr.restName[ind2].equals(rest) && !parmr.color[ind2].equals("") && !parmr.color[ind2].equalsIgnoreCase("Default") && 
                                                    parmr.stime[ind2] <= tee_time && parmr.etime[ind2] >= tee_time) {      

                                                // Check to make sure no suspensions apply
                                                suspend = false;                        
                                                for (int m=0; m<parmr.MAX; m++) {

                                                    if (parmr.susp[ind2][m][0] == 0 && parmr.susp[ind2][m][1] == 0) {
                                                        m = parmr.MAX;   // don't bother checking any more
                                                    } else if (parmr.susp[ind2][m][0] <= tee_time && parmr.susp[ind2][m][1] >= tee_time) {    //time falls within a suspension
                                                        suspend = true;                       
                                                        m = parmr.MAX;     // don't bother checking any more
                                                    }
                                                }

                                                if (!suspend) {

                                                    if ((parmr.courseName[ind2].equals( "-ALL-" )) || (parmr.courseName[ind2].equals( courseNameT ))) {  // course ?

                                                        if ((parmr.fb[ind2].equals( "Both" )) || (parmr.fb[ind2].equals( sfb2 ))) {    // matching f/b ?

                                                            //
                                                            //  Found a restriction that matches date, time, day, F/B, mtype & mship of this member
                                                            //
                                                            if (event.equals("") && lottery.equals("")) {           // change color if no event

                                                                if (!parmr.color[ind2].equals("Default")) {     // if not default

                                                                    bgcolor = parmr.color[ind2];
                                                                    ind2 = parmr.MAX;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            ind2++;
                                        }

                                        if (bgcolor5.equals( "" )) {
                                            bgcolor5 = bgcolor;         // same as others if not specified
                                        }
                                    } // end of if event or lottery time
                                } // end if f/b matches
                             } // and if course matches
                         } // end if rest is suspended
                     }
                     indx++;
                 }      // end of while loop
            }     // end of if rest exists in teecurr

            
            //
            //   Check guest restrictions for this hotel's guest types if slot available.
            //   Only block tee time if ALL guest types for this user are restricted.
            //
            if ((allow == true) && (in_use == 0)) {         // still ok and not in use?

               pstmt5 = con.prepareStatement (
                  "SELECT * " +
                  "FROM guestres2 WHERE sdate <= ? AND edate >= ? AND " +
                  "stime <= ? AND etime >= ? AND activity_id = 0");

               pstmt5.clearParameters();
               pstmt5.setLong(1, date);
               pstmt5.setLong(2, date);
               pstmt5.setInt(3, tee_time);
               pstmt5.setInt(4, tee_time);
               rs2 = pstmt5.executeQuery();

               loop3:
               while (rs2.next()) {

                  grest_recurr = rs2.getString("recurr");
                  rcourse = rs2.getString("courseName");
                  rest_fb = rs2.getString("fb");
                  grest_color = rs2.getString("color");
                  
                  // now look up the guest types for this restriction
                  PreparedStatement pstmt3 = con.prepareStatement (
                         "SELECT guest_type FROM guestres2_gtypes WHERE guestres_id = ?");

                  pstmt3.clearParameters();
                  pstmt3.setInt(1, rs2.getInt("id"));

                  rs3 = pstmt3.executeQuery();

                  rguest.clear();

                  while ( rs3.next() ) {

                     rguest.add(rs3.getString("guest_type"));

                  }
                  pstmt3.close();

                  //
                  //  Check if course matches that specified in restriction
                  //
                  if ((rcourse.equals( "-ALL-" )) || (rcourse.equals( courseNameT ))) {

                     //
                     //  We must check the recurrence for this day (Monday, etc.) and guest types
                     //
                     //     guestx = guest types specified for this hotel user
                     //     rguestx = guest types from restriction gotten above
                     //
                     if ((grest_recurr.equalsIgnoreCase( "every " + day_name )) ||
                         (grest_recurr.equalsIgnoreCase( "every day" )) ||
                         ((grest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                          (!day_name.equalsIgnoreCase( "saturday" )) &&
                          (!day_name.equalsIgnoreCase( "sunday" ))) ||
                         ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                          (day_name.equalsIgnoreCase( "saturday" ))) ||
                         ((grest_recurr.equalsIgnoreCase( "all weekends" )) &&
                          (day_name.equalsIgnoreCase( "sunday" )))) {

                        //
                        //  Now check if F/B matches
                        //
                        if ((rest_fb.equals( "Both" )) || (rest_fb.equals( sfb2 ))) {

                            if (!verifySlot.checkRestSuspend(-99, rs2.getInt("id"), 0, (int)date, tee_time, day_name, courseNameT, con)) {        // check if this guest restriction is suspended for this time)
                            
                               block = false;                  // init to 'do not block' (allow)
                               i = 0;
                               int i3 = 0;
                               ploop1:
                               while (i < hguest.size()) {     // check all guest types for this hotel user

                                  //if (!hguest.get(i).equals( "" )) {  // if guest type specified for user (must be at least 1)

                                     i2 = 0;
                                     while (i2 < rguest.size()) {     // check all guest types for this restriction

                                        if (hguest.get(i).equals( rguest.get(i2) )) {
                                           out.println("<!-- MATCHING GUEST TYPE FOUND: Name=" + rs2.getString("name") + ", tee_time=" + tee_time + ", hotel_guest=" + hguest.get(i) + ", rest_guest=" + rguest.get(i2) + ", " + rguest.size() + " -->");
                                           //block = true;    // guest type in restriction - block this tee time
                                           i3++;
                                           //break ploop1;
                                        }
                                        i2++;
                                     }
                                  //}
                                  i++;
                               }

                               if (i3 == hguest.size()) {
                                   block = true;
                               } else if (i3 > 0) {
                                  bgcolor = grest_color;            // set color for this slot
                               }

                               if (block == true) {                 // block this slot ?

                                  allow = false;                    // indicate guests restricted for this user
                                  bgcolor = grest_color;            // set color for this slot
                                  break loop3;                      // exit restriction loop
                               }
                            }
                        }  // end of IF f/b matches

                     } // end if recurr matches

                  } // end of IF course matches

               } // end of loop3 while loop (while guest restrictions exist)

               pstmt5.close();

               //
               //  Custom check for Cordillera Canned Restrictions
               //
               /*
               if (club.equals( "cordillera" ) && allow == true) {

                  if (dateShort > 413 && dateShort < 1101) {         // if this is within the custom date range
                                    
                     boolean cordallow = cordilleraCustom.checkCordillera(date, tee_time, courseNameT, "hotel"); // go check if this time is restricted (changed from courseName1 to courseNameT)
                       
                     if (cordallow == false) {                  // if restricted to Hotel
                        bgcolor = gcolor;                       // set color for this slot
                        allow = false;                          // do allow access
                     }
                  }
               }
                */
               
            }       // end of IF allow = true

            submit = "time:" + fb;       // create a name for the submit button

            out.println("<tr>");         // start of tee slot (row)

            j++;                                       // increment the jump label index (where to jump on page)
            out.println("<a name=\"jump" + j + "\"></a>"); // create a jump label for 'noshow' returns

            if ((allow == true) && (in_use == 0)) {         // can user select this slot and not in use?

               out.println("<form action=\"Hotel_slot\" method=\"post\" target=\"_top\">");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + name + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseNameT + "\">"); //  (changed from courseName1 to courseNameT)
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

               if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

                  out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");  // tell _slot to do 5's
               } else {
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
               }

               if (min < 10) {
                  out.println("<input type=\"submit\" name=\"" + submit + "\" id=\"" + submit + "\" value=\"" + hr + ":0" + min + ampm + "\" alt=\"submit\">");
               } else {
                  out.println("<input type=\"submit\" name=\"" + submit + "\" id=\"" + submit + "\" value=\"" + hr + ":" + min + ampm + "\" alt=\"submit\">");
               }

               out.println("</font></td>");
               out.println("</form>");

            } else {
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               if (type == shotgun) {
                     out.println("shotgun");
               } else {
                  if (min < 10) {
                     out.println(hr + ":0" + min + ampm);
                  } else {
                     out.println(hr + ":" + min + ampm);
                  }
               }
               out.println("</font></td>");
            }

            //
            //  Course Name
            //
            if (courseName1.equals( "-ALL-" )) {
                   
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
            //  Add Player 1
            //
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");

            if (!player1.equals("")) {
                 // If guest and part of the guestdb, display name as link to guest edit page for this guest_id.
                 if (g1 == 1 && guest_id1 != 0) {
                     out.print("&nbsp;<a href=\"javascript: void(0)\" title=\"" + guest_uid1 + "\" " + (guest_id1 < 0 ? "style=\"color: red;\" " : "style=\"color: black; text-decoration: none\" ") + "onclick=\"window.open('Common_guestdb?caller=sheet&guest_id=" + (guest_id1 < 0 ? (guest_id1 * -1) : guest_id1) + "', 'guestEdit', 'height=500, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, directories=no, status=no')\"" + (guest_id1 > 0 ? " onmouseover=\"this.style.textDecoration='underline'\" onmouseout=\"this.style.textDecoration='none'\"" : "") + ">" + player1 + "</a></font>");
                 } else {
                     out.print("&nbsp;" + player1 + "</font>");
                 }
//               out.println(player1);

            } else {     // player is empty

               out.println("&nbsp;");
            }
            out.println("</font></td>");

            if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(p1cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");

            //
            //  Add Player 2
            //
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");

            if (!player2.equals("")) {
                 // If guest and part of the guestdb, display name as link to guest edit page for this guest_id.
                 if (g2 == 1 && guest_id2 != 0) {
                     out.print("&nbsp;<a href=\"javascript: void(0)\" title=\"" + guest_uid2 + "\" " + (guest_id2 < 0 ? "style=\"color: red;\" " : "style=\"color: black; text-decoration: none\" ") + "onclick=\"window.open('Common_guestdb?caller=sheet&guest_id=" + (guest_id2 < 0 ? (guest_id2 * -1) : guest_id2) + "', 'guestEdit', 'height=500, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, directories=no, status=no')\"" + (guest_id2 > 0 ? " onmouseover=\"this.style.textDecoration='underline'\" onmouseout=\"this.style.textDecoration='none'\"" : "") + ">" + player2 + "</a></font>");
                 } else {
                     out.print("&nbsp;" + player2 + "</font>");
                 }
//               out.println(player2);

            } else {     // player is empty

               out.println("&nbsp;");
            }
            out.println("</font></td>");

            if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(p2cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");

            //
            //  Add Player 3
            //
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");

            if (!player3.equals("")) {
                 // If guest and part of the guestdb, display name as link to guest edit page for this guest_id.
                 if (g3 == 1 && guest_id3 != 0) {
                     out.print("&nbsp;<a href=\"javascript: void(0)\" title=\"" + guest_uid3 + "\" " + (guest_id3 < 0 ? "style=\"color: red;\" " : "style=\"color: black; text-decoration: none\" ") + "onclick=\"window.open('Common_guestdb?caller=sheet&guest_id=" + (guest_id3 < 0 ? (guest_id3 * -1) : guest_id3) + "', 'guestEdit', 'height=500, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, directories=no, status=no')\"" + (guest_id3 > 0 ? " onmouseover=\"this.style.textDecoration='underline'\" onmouseout=\"this.style.textDecoration='none'\"" : "") + ">" + player3 + "</a></font>");
                 } else {
                     out.print("&nbsp;" + player3 + "</font>");
                 }
//               out.println(player3);

            } else {     // player is empty

               out.println("&nbsp;");
            }
            out.println("</font></td>");

            if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(p3cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");

            //
            //  Add Player 4
            //
            out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
            out.println("<font size=\"2\">");

            if (!player4.equals("")) {
                 // If guest and part of the guestdb, display name as link to guest edit page for this guest_id.
                 if (g4 == 1 && guest_id4 != 0) {
                     out.print("&nbsp;<a href=\"javascript: void(0)\" title=\"" + guest_uid4 + "\" " + (guest_id4 < 0 ? "style=\"color: red;\" " : "style=\"color: black; text-decoration: none\" ") + "onclick=\"window.open('Common_guestdb?caller=sheet&guest_id=" + (guest_id4 < 0 ? (guest_id4 * -1) : guest_id4) + "', 'guestEdit', 'height=500, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, directories=no, status=no')\"" + (guest_id4 > 0 ? " onmouseover=\"this.style.textDecoration='underline'\" onmouseout=\"this.style.textDecoration='none'\"" : "") + ">" + player4 + "</a></font>");
                 } else {
                     out.print("&nbsp;" + player4 + "</font>");
                 }
//               out.println(player4);

            } else {     // player is empty

               out.println("&nbsp;");
            }
            out.println("</font></td>");

            if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"1\">");
               out.println(p4cw);
            } else {
               out.println("<td bgcolor=\"white\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;");
            }
            out.println("</font></td>");

            //
            //  Add Player 5 if supported
            //
            if (fivesALL != 0) {        // if 5-somes supported on any course
                if (fives != 0) {

                   if (!rest5.equals( "" )) {       // if 5-somes are restricted

                      out.println("<td bgcolor=\"" + bgcolor5 + "\" align=\"center\">");
                      out.println("&nbsp;");

                   } else {

                      out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                      out.println("<font size=\"2\">");

                      if (!player5.equals("")) {
                           // If guest and part of the guestdb, display name as link to guest edit page for this guest_id.
                           if (g5 == 1 && guest_id5 != 0) {
                               out.print("&nbsp;<a href=\"javascript: void(0)\" title=\"" + guest_uid5 + "\" " + (guest_id5 < 0 ? "style=\"color: red;\" " : "style=\"color: black; text-decoration: none\" ") + "onclick=\"window.open('Common_guestdb?caller=sheet&guest_id=" + (guest_id5 < 0 ? (guest_id5 * -1) : guest_id5) + "', 'guestEdit', 'height=500, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no, directories=no, status=no')\"" + (guest_id5 > 0 ? " onmouseover=\"this.style.textDecoration='underline'\" onmouseout=\"this.style.textDecoration='none'\"" : "") + ">" + player5 + "</a></font>");
                           } else {
                               out.print("&nbsp;" + player5 + "</font>");
                           }
//                         out.println(player5);

                      } else {     // player is empty

                         out.println("&nbsp;");
                      }
                   }
                   out.println("</font></td>");

                   if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                      out.println("<td bgcolor=\"white\" align=\"center\">");
                      out.println("<font size=\"1\">");
                      out.println(p5cw);
                   } else {
                      out.println("<td bgcolor=\"white\" align=\"center\">");
                      out.println("<font size=\"2\">");
                      out.println("&nbsp;");
                   }
                   out.println("</font></td>");
                   
                } else {          // 5-somes supported on at least 1 course, but not this one (if course=ALL)

                     out.println("<td bgcolor=\"black\" align=\"center\">");   // no 5-somes
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                     out.println("<td bgcolor=\"black\" align=\"center\">");
                     out.println("<font size=\"2\">");
                     out.println("&nbsp;");
                     out.println("</font></td>");
                }
            }
            
            if (club.equals( "cordillera" )) {         // if Cordillera - add ForeCaddie Assigned col

               out.println("<form method=\"post\" action=\"Hotel_sheet\" name=\"caform" +k+ "\">");
               out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
               
               if (allow) {
                   out.println("<font size=\"2\">");

                   out.println("<select size=\"1\" name=\"forecaddy\" onChange=\"document.caform" +k+ ".submit()\">");
                   if (numCaddies == 0) {
                      out.println("<option selected value=\" \"> </option>");
                      out.println("<option value=\"Y\">Y</option>");
                   } else {
                      out.println("<option selected value=\"Y\">Y</option>");
                      out.println("<option value=\" \"> </option>");
                   }
                   out.println("</select>");

                   out.println("<input type=\"hidden\" name=\"time\" value=\"" + tee_time + "\">");
                   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                   out.println("<input type=\"hidden\" name=\"i" + index + "\" value=\"a\">");
                   out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
                   out.println("<input type=\"hidden\" name=\"courseT\" value=\"" + courseNameT + "\">");
                   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + j + "\">");

                   k++;                                     // increment form id counter
                   out.println("</font>");
               } else {
                   out.println("&nbsp");
               }
               out.println("</td></form>");
            }
            out.println("</tr>");       // end of this row

         }  // end of IF blocker

      }  // end of while loop2

      pstmt.close();

         out.println("</table>");                   // end of tee sheet table
         out.println("</td></tr>");
         out.println("</table>");                   // end of main page table
      //
      //  End of HTML page
      //
      out.println("</center></body></html>");
      out.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>Message:" + e1.getMessage());
      out.println("<BR><BR>Error:" + e1.toString());
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

 }  // end of doPost

 
 // *********************************************************
 //  Cordillera Custom - update the forecaddie indicator for group
 // *********************************************************

 private void updateForeCaddie(String stime, String sfb, long date, String foreCaddie, String course, 
                               PrintWriter out, Connection con) {


   //
   //  Convert the common string values to int's
   //
   int fc = 0;
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   if (foreCaddie.equals( "Y" )) {     // if ForeCaddies = Yes
     
      fc = 1;
   }

   try {

      PreparedStatement pstmt3 = con.prepareStatement (
           "UPDATE teecurr2 SET pos5 = ? " +
           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

       //
       //  execute the prepared statement to update the tee time slot
       //
       pstmt3.clearParameters();        // clear the parms
       pstmt3.setInt(1, fc);
       pstmt3.setLong(2, date);
       pstmt3.setInt(3, time);
       pstmt3.setInt(4, fb);
       pstmt3.setString(5, course);
       pstmt3.executeUpdate();

       pstmt3.close();

   }
   catch (Exception ignore) {
   }
     
 }      // end of updateForeCaddie

}
