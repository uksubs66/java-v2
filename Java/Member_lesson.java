/***************************************************************************************     
 *   Member_lesson:  This servlet will process all lesson requests from the menu.
 *
 *
 *   created: 11/23/2004   Bob P.
 *
 *   last updated:
 *
 *      7/22/10  Palo Alto Hills (paloaltohills) - Allow members to access 60 days out on Lesson Book calendar
 *      5/11/10  Cherry Hills - add custom message for pros that do not use the lesson book.
 *      5/03/10  Remove single quotes from confirmation messages as they often are displayed as ? in browser.
 *      4/06/10  Fix for when advance booking limit is set, was allowing same-day bookings when advance time
 *               pushed date to next day.
 *      3/29/10  Virginia CC (virginiacc) - Order pro list by first name alphabetically
 *      3/26/10  Updated golf side message for successfully booking a lesson to be more helpful
 *      3/25/10  Crane Creek CC (cranecreek) - Do not allow members to book lessons on the Tennis side (case 1801).
 *      3/25/10  Wellesley - allow members to use the lesson book (case 1764).
 *     12/29/09  Wellesley - do not allow members to use the lesson book (case 1764).
 *     12/02/09  Champion Hills - do not allow members to use the lesson book (case 1752).
 *     11/17/09  Desert Highlands - Fix for 3/24/09 change (case 1608).
 *     11/09/09  Display lesson location on confirmation screen and in email notification
 *     11/08/09  Store the activity_id of the activity sheet this lesson was booked on
 *     11/06/09  Change to accomodate booking lesson times that fall completely outside the time-sheets for non-golf activities
 *     11/03/09  Added prompt to reject members from entering a lesson time if no corresponding open times could be found on the time sheets (non-golf)
 *     11/02/09  Display location selection checkboxes when configuring lesson types and group lessons to allow for precise court choices
 *     10/29/09  Allow group lessons to be created as Clinic (shared signup list across all recurrences) or Non-Clinic (unique signup list for each recurrence)
 *     10/27/09  Various changes to allow access to individual and group lessons from Member_teelist
 *     10/26/09  Changes to group lesson/clinic signup to accommodate recurring lessons
 *     10/21/09  Changes to check activity timesheets for open spots when booking a lesson for a non-golf activity
 *      5/07/09  Change the message when lesson time is not available.
 *      4/14/09  The CC of Brookline - change to 60 day calendars (case 1645).
 *      4/06/09  NCR CC - change to 60 day calendars (case 1643).
 *      3/24/09  Desert Highlands - cutoff member access to the lesson book starting at 8pm the day before (also day-of) (case 1608).
 *      3/02/09  Added dining request processing for group lesson bookings
 *      2/13/09  Tweaks to Dining Request prompt display
 *      2/13/09  Belle Meade - do not allow members to access the lesson book.  They use this for their practice range (case 1612).
 *      1/07/09  TPC at Snoqualmie Ridge - change max calendar days to 3 for their simulator reservations (case 1598).
 *      7/29/08  Colleton River - change to 180 day calendars (case 1526).
 *      4/11/08  Minikahda Club - change to 60 day calendars (case 1446).
 *      4/02/08  MN Valley - change to 90 day calendars (case 1438).
 *      2/28/08  Columbia-Edgewater - change to 120 day calendars.
 *      2/27/08  Update getEndTime method to check for more than one hour when calculating end time.
 *      2/20/08  Royal Oaks (Houston) - limit members to 2 weeks in advance for lessons (case 1360).
 *      6/26/07  Cherry Hills - Add custom message for Matt & Ty asking member to contact golf shop for lesson info (case #1199).
 *      6/20/07  Add entry to tee time history table (teehist) for lessons
 *      6/06/07  Cherry Hills - Add custom message for Clayton asking member to contact golf shop for lesson info
 *      4/20/07  Congressional - change to 90 day calendars.
 *      3/27/07  Change Group Lesson selection list to include all group lessons within 180 days instead of 90.
 *      2/20/07  Minneapolis - change to 90 day calendars.
 *      1/24/07  Interlachen Spa - allow lesson book to be used to schedule Spa Services.
 *     01/21/07  Verbage changes for TLT system
 *     10/23/06  Desert Highlands - provide phone number of golf shop in message described below.  
 *     10/23/06  Inform member if no times available for date selected if some are prior to hrs in advance. 
 *      7/11/06  Set the response header controls to not allow caching.  Some members were
 *               able to get into lesson times that were blocked.
 *      5/01/06  Correct processing for available lesson times so the last time is included.
 *      3/02/06  Cherry Hills - change to 14 day calendars.
 *      4/16/05  Cordillera - change to 90 day calendars.
 *      4/12/05  Check for end of lesson book when checking available times for a lesson.
 *      4/10/05  Custom change for Interlachen - list the lesson pros by first name.
 *      3/09/05  Custom change for Interlachen - do not list the lesson description,
 *               length or price when a member schedules a lesson. 
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
import java.lang.Math;

// foretees imports
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.verifyLesson;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;


public class Member_lesson extends HttpServlet {
 
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the doGet call (go to doPost)
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }   // end of doGet


 //****************************************************
 // Process all requests here (doPost)
 //****************************************************
 //
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

   String omit = "";

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");
   String caller = (String)session.getAttribute("caller");
   String mship = (String)session.getAttribute("mship");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
     
   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY>");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   String proid = "";     // init proid
     
   //
   //  If the pro id is not passed, then go prompt for it (required on all except 'Add Pro')
   //
   if (req.getParameter("proid") == null) {     

      if (req.getParameter("bio") != null) {              // request to view a pro's bio?

         proid = "0";
           
         displayBio(req, resp, out, session, con, proid);    // prompt for pro (unique for this)
         return;
      }

      proid = promptProId(req, resp, out, session, con);
        
      if (proid.equals( "" )) {       // return if user prompted for pro
         return;
      }
   } else {

      proid = req.getParameter("proid");           //  lesson pro's id
   }

   if (req.getParameter("bio") != null) {              // request to view a pro's bio?

      displayBio(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("group") != null) {              // request to view a pro's Group Lessons?

      displayGroup(req, resp, out, session, con, proid);
      return;
   }

   //
   //  Group Lesson Requests
   //
   if (req.getParameter("groupLesson") != null) {      // request a Group Lesson list?

      if (req.getParameter("selectDate") != null) {
          grpSelectDate(req, session, out, con);
      } else {
          grpLesson(req, resp, out, session, con, proid);
      }
      return;
   }

   if (req.getParameter("groupLesson2") != null) {      // update a Group Lesson (submit)?

      grpLesson2(req, resp, out, session, con, proid);
      return;
   }

   //
   //  Individual Lesson Requests
   //
   //  If lesson type not provided, then prompt for it
   //
   if (req.getParameter("ltype") == null) {              // lesson type provided?

      promptType(req, resp, out, session, con, proid);   // no
      return;
   }

   //
   //  Process according to the type of request
   //
   if (req.getParameter("reqtime") != null) {           // request a lesson time from lesson book?

      reqTime(req, resp, out, session, con, proid);
      return;
   }

   if (req.getParameter("reqtime2") != null) {         // update a lesson time from lesson book (submit)?

      reqTime2(req, resp, out, session, con, proid);
      return;
   }

                                                       // Fall through if none of the above !!!!!!!!!!!!


   //**************************************************************************************
   // We fall through to the 'Display Available Times' (based on lesson type and pro)
   //**************************************************************************************
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs2 = null;

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

   String calDate = "";
   String temp = "";
   String dayName = "";
   String proName = "";
   String monthName = "";
   String stime = "";
     
   String [] lgroupA = new String [10];       // Group Lessons - max 10 group lessons per day
   String [] lgcolorA = new String [10];    
   int [] stimeA = new int [10];             
   int [] etimeA = new int [10];
   int [] lgmaxA = new int [10];
   int [] lgnumA = new int [10];
   int [] lgdoneA = new int [10];

   String ltname = "";
   String ltype = "";
   String lgname = "";
   String lbname = "";
   String lbetime = "";
   String frags = "";
   String color = "";
   String bgcolor = "";
   String lgcolor = "";
   String lbcolor = "";
   String memname = "";
   String memid = "";
   String phone1 = "";
   String phone2 = "";
   String notes = "";
   String ampm = "";
   String submit = "";
   String activity_name = "";

   char flag = 'b';

   int id = 0;
   int i = 0;
   int i2 = 0;
   int count = 0;
   int availCount = 0;
   int skip = 0;
   int fragment = 0;
   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int hr = 0;
   int min = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int day_num = 0;
   int time = 0;
   int advtime = 0;
   int etime = 0;
   int sbtime = 0;
   int ebtime = 0;
   int block = 0;
   int num = 0;
   int length = 0;
   int last_length = 0;
   int lgmax = 0;
   int lgnum = 0;
   int in_use = 0;
   int advhrs = 0;
   int index = 0;
   int endTime = 0;
   int lesson_id = 0;
   int sheet_activity_id = 0;

   long date = 0;
   long todayDate = 0;

   boolean earlyTimes = false;


   try {
      id = Integer.parseInt(proid);           // get and convert the proid
   }
   catch (NumberFormatException e) {
      id = 0;
   }


   //
   //  init the group lesson arrays
   //
   for (i=0; i<10; i++) {      
      lgroupA[i] = "";
      lgcolorA[i] = "";
   }

   if (req.getParameter("ltype") != null) {       // if lesson type requested

      ltype = req.getParameter("ltype");       //  get it
   }

   //
   //  If we came from the calendar then use the submit button value determine the date, else use calDate
   //
   if (req.getParameter("calDate") != null) {       // if a return

      calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)

      //
      //  Convert the date from string (mm/dd/yyyy) to ints (month, day, year)
      //
      StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

      temp = tok.nextToken();                    // get the mm value
      month = Integer.parseInt(temp);
      temp = tok.nextToken();                    // get the dd value
      day = Integer.parseInt(temp);
      temp = tok.nextToken();                    // get the yyyy value
      year = Integer.parseInt(temp);

      //
      //  Set the requested date to get the day name, etc.
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      cal.set(Calendar.YEAR, year);                 // change to requested date
      cal.set(Calendar.MONTH, month-1);
      cal.set(Calendar.DAY_OF_MONTH, day);

      day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   } else {

      //
      //    The name of the submit button is an index value preceeded by the letter 'i' (must start with alpha)
      //    (0 = today, 1 = tomorrow, etc.)
      //
      temp = "";                                      // init

      Enumeration enum1 = req.getParameterNames();     // get the parm names passed

      loop1:
      while (enum1.hasMoreElements()) {

         temp = (String) enum1.nextElement();          // get name of parm

         if (temp.startsWith( "i" )) {

            break loop1;                              // done - exit while loop
         }
      }

      //
      //  make sure we have the index value
      //
      if (!temp.startsWith( "i" )) {

         out.println(SystemUtils.HeadTitle("Procedure Error"));
         out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
         out.println("<BR><BR><H3>Access Procedure Error</H3>");
         out.println("<BR><BR>Required Parameter is Missing - Member_sheet.");
         out.println("<BR>Please exit and try again.");
         out.println("<BR><BR>If problem persists, report this error to your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Convert the index value from string to int
      //
      StringTokenizer tok = new StringTokenizer( temp, "i" );     // space is the default token - use 'i'

      String tempNum = tok.nextToken();        // get just the index number (name= parm must start with alpha)

      try {
         index = Integer.parseInt(tempNum);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  Get today's date and use the value passed to locate the requested date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      cal.add(Calendar.DATE,index);                  // roll ahead 'index' days

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
       
      month++;
      calDate = month+ "/" +day+ "/" +year;         // set caldate for returns
   }

   dayName = day_table[day_num];                   // get name for day
   monthName = mm_table[month];                    // get name for month

   date = (year * 10000) + (month * 100) + day;    // create a date field of yyyymmdd

   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         String mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         String suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

         advhrs = rs.getInt("advlimit");                            // get hours in advance must make res
      }
      pstmt.close();

   }
   catch (Exception exc) {
   }

   //
   //  Get today's date to see if this is today
   //
   Calendar cal2 = new GregorianCalendar();        // get todays date
   if (advhrs > 0) {
      cal2.add(Calendar.HOUR_OF_DAY,advhrs);          // roll ahead 'adv limit' hours
   }
   year2 = cal2.get(Calendar.YEAR);
   month2 = cal2.get(Calendar.MONTH);
   day2 = cal2.get(Calendar.DAY_OF_MONTH);
   hr = cal2.get(Calendar.HOUR_OF_DAY);           // 24 hr clock (0 - 23)
   min = cal2.get(Calendar.MINUTE);

   month2++;
   todayDate = (year2 * 10000) + (month2 * 100) + day2;    // create a date field of yyyymmdd
   advtime = (hr * 100) + min;                             // advance time (earliest time to allow for today)


   //
   //  Determine the number of times in this pro's lesson book - then build an array for these times
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT COUNT(*) FROM lessonbook5 " +
              "WHERE proid = ? AND DATE = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setLong(2, date);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         count = rs.getInt(1);       // get number of entries for this date
      }

      pstmt.close();

   }
   catch (Exception exc) {
      dbError(req, out, exc, caller);
      return;
   }

   //
   //  Create an array to hold an indicator representing each entry in the lesson book
   //
   int [] timeA = new int [count];
   int [] lesson_idA = new int [count];
   char [] bookA = new char [count];

   //
   //  init the entries so they are all busy
   //
   for (i = 0; i < count; i++) {

      bookA[i] = 'B';     // busy
   }

   try {

      //
      //  Now see which time slots are open
      //
      i = 0;
        
      pstmt = con.prepareStatement (
              "SELECT * FROM lessonbook5 " +
              "WHERE proid = ? AND DATE = ? ORDER BY time");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setLong(2, date);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next() && i < count) {

         lesson_idA[i] = rs.getInt("recid");
         lgname = rs.getString("lgname");
         timeA[i] = rs.getInt("time");        // save the time value
         block = rs.getInt("block");
         memname = rs.getString("memname");
         last_length = rs.getInt("length");
         in_use = rs.getInt("in_use");
           
         if (lgname.equals( "" ) && memname.equals( "" ) && block == 0 && in_use == 0) {
           
            if (date < todayDate || (date == todayDate && timeA[i] < advtime)) {

               bookA[i] = 'B';         // NOT open (too early)
                 
               earlyTimes = true;      // indicate times found that are too early (before adv hrs)   
             
            } else {    // ok

               bookA[i] = 'O';     // open
               availCount++;       // keep count
            }
         }  
  
         i++;
      }           // end of WHILE

      pstmt.close();

   }
   catch (Exception exc) {
      dbError(req, out, exc, caller);
      return;
   }

   //
   //  Use the lesson length and arrays just built to determine which times are actually available for the 
   //  requested lesson type.
   //
   if (availCount > 0) {      // if any are open
     
      availCount = 0;         // init count
        
      try {

         pstmt = con.prepareStatement (
                 "SELECT length FROM lessontype5 WHERE proid = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.setString(2,ltype);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            length = rs.getInt(1);
         }
         pstmt.close();

      }
      catch (Exception exc) {
         dbError(req, out, exc, caller);
         return;
      }

      //
      //  Get the last time in the book
      //
      endTime = timeA[count-1];

      endTime = getEndTime(endTime, last_length);  // determine the true end of the book - for compares

      //
      //  determine which entries are available
      //
      for (i = 0; i < count; i++) {            // process all times in lesson book
        
         if (bookA[i] == 'O') {                // if slot is open (not busy)

            time = timeA[i];                   // get the time of this slot

            etime = getEndTime(time, length);   // get end of lesson time
              
            if (etime <= endTime) {             // if we haven't reached the end of the lesson book

               flag = 'A';                          // init as Available

               if (i < count-1) {                     // if more to check

                  i2 = i+1;                            // save index (i)

                  time = timeA[i2];                    // get time of next slot

                  bloop1:
                  while (i2 < count && time < etime) {      // check next slots

                     if (bookA[i2] != 'O') {                 // if slot is busy

                        flag = 'B';                           // flag as Busy
                        break bloop1;                         // exit loop
                     }
                     i2++;
                     if (i2 < count) {
                        time = timeA[i2];                   // get time of next slot
                     }
                  }
               }
              
               bookA[i] = flag;                       // set slot as Busy or Available

               if (flag == 'A') {                     // if it was available

                  availCount++;                       // count it
               }
                 
            } else {                     // end of book
              
               bookA[i] = 'B';           // set slot as Busy
            }
         }
      }
   }

   //
   //  Build the page to display the available times
   //
   out.println(SystemUtils.HeadTitle("Member Lesson Book"));

   // include files for dynamic calendars
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/cal-styles.css\">");
   out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/cal-scripts.js\"></script>");

   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#336633\" vlink=\"#8B8970\" alink=\"#8B8970\">");
   SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"></font><center>");

   out.println("<table border=\"0\" align=\"center\" width=\"100%\">");        // whole page
   out.println("<tr><td align=\"center\">");
   //**********************************************************
   //  Continue with instructions and tee sheet
   //**********************************************************
   //
   if (availCount > 0) {      // if any available times

      out.println("<table cellpadding=\"3\" align=\"center\">");
      out.println("<tr><td bgcolor=\"#336633\"><font color=\"#FFFFFF\" size=\"2\">");
      out.println("&nbsp;&nbsp;<b>Instructions:</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;Select one of the following available times.&nbsp;&nbsp; ");
      out.println("</font></td></tr></table>");

      out.println("<font size=\"1\"><br></font><font size=\"3\">");
      if (club.equals( "interlachenspa" )) {
         out.println("Spa Service:&nbsp;&nbsp;<b>" +ltype+ "</b>");
         out.println("<br><br>");
         out.println("Available Times For&nbsp;&nbsp;<b>" +proName+ "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
      } else {
         out.println("Lesson Type:&nbsp;&nbsp;<b>" +ltype+ "</b>&nbsp;&nbsp;&nbsp;&nbsp;Length (minutes):&nbsp;&nbsp;<b>" +length+ "</b>");
         out.println("<br><br>");
         out.println("Available Individual Lesson Times For&nbsp;&nbsp;<b>" +proName+ "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
      }
      out.println("</font><font size=\"2\"><br><br>");
      out.println("<table cellpadding=\"3\" align=\"center\" border=\"1\" bgcolor=\"#F5F5DC\" width=\"200\">");

      int oldhr = 0;
        
      //
      //  Get the lesson times for this date and pro
      //
      for (i = 0; i < count; i++) {           // process all times of book

         if (bookA[i] == 'A') {               // if slot is Available (not busy)

            time = timeA[i];                   // get the time of this slot

            hr = time / 100;           // determine time values
            min = time - (hr * 100);

            ampm = " AM";
            if (hr == 12) {
               ampm = " PM";
            }
            if (hr > 12) {
               ampm = " PM";
               hr = hr - 12;    // convert to conventional time
            }

            if (hr != oldhr) {              // if new hour value
              
               out.println("<tr bgcolor=\"#8B8970\">");
               out.println("<td align=\"left\"><font size=\"1\">");
               out.println(hr+ " " +ampm);
               out.println("</font></td>");
               out.println("</tr>");
               oldhr = hr;
            }

            out.println("<tr>");
            out.println("<td align=\"center\">");     
            out.println("<font size=\"1\">");

            //
            //   submit button (time)
            //
            String subName = "";
            if (min < 10) {                                 // if min value is only 1 digit
               subName = hr + ":0" + min + ampm;
            } else {                                        // min value is 2 digits
               subName = hr + ":" + min + ampm;
            }
            out.println("<a href=\"/" +rev+ "/servlet/Member_lesson?proid=" +id+ "&calDate=" +calDate+ "&date=" +date+ "&time=" +time+ "&ltype=" +ltype+ "&day=" +dayName+ "&lesson_id=" +lesson_idA[i]+ "&reqtime=yes\" target=\"_top\">");
            out.println(subName+ "</a>");

            out.println("</font></td>");
            out.println("</form>");
            out.println("</tr>");
         }              
      }

      //
      //  End of Lesson Book table
      //
      out.println("</table>");                         // end of table

   } else {        // none avail

      out.println("<font size=\"3\">");
        
      if (club.equals( "interlachenspa" )) {

         out.println("Available Times For&nbsp;&nbsp;<b>" +proName+ "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
         out.println("</font><font size=\"2\"><br><br>");
         out.println("Sorry, there are no times available for this date for the service you selected.<br>");
         if (earlyTimes == false) {
            out.println("Please try another date.</font><br><br>");
         } else {
            out.println("However, there are some times available that cannot be scheduled online at this time.<br>");
            out.println("Please contact the Spa Scheduler if you wish to schedule a service for this date.</font><br><br>");
         }

      } else {

         out.println("<br><br>Available Lesson Times For&nbsp;&nbsp;<b>" +proName+ "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
         out.println("<br><br>");
         if (earlyTimes == false) {
            out.println("Sorry, there are no times available for this date for the lesson type you selected.<br>");
            out.println("Please try another Lesson Pro or another date.</font><br><br>");
         } else {
            // out.println("However, there are some times available that cannot be scheduled online at this time.<br>");
            if (club.equals( "deserthighlands" )) {
               out.println("Please contact the Golf Shop if you wish to schedule a lesson for this date (480.585.8521).</font><br><br>");
            } else {
               out.println("Please contact the Golf Shop if you wish to schedule a lesson for this date.</font><br><br>");
            }
         }
      }
   }

   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table><br>");                            // end of main page table
   out.println("<a href=\"/" +rev+ "/servlet/Member_lesson?proid=" +id+ "\" style=\"text-decoration:underline; color:black\">Return</a>");
   out.println("</center></body></html>");
   out.close();

 }   // end of doPost   


 // ********************************************************************
 //  Process the 'Prompt Pro Id' request
 // ********************************************************************

 private String promptProId(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {


   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String proid = "";
   String proName = "";
   String lname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String temp = "";
   int id = 0;
   int count = 0;
   boolean found = false;

   //
   //  If club does not want members to use the lesson book - inform the user
   //
   // if (club.equals("championhills") || club.equals("wellesley")) {      // allow Wellesley for the summer
   if (club.equals("championhills") || (club.equals("cranecreek") && sess_activity_id == 1)) {

      String custMsg = "";

      if (club.equals("championhills")) {
          custMsg = "Please call the Golf Shop to schedule a lesson with the Professional Staff. (828) 693-3600.";
      } else if (club.equals("wellesley")) {
          custMsg = "Winter indoor golf lessons with Jeff Phillips, Sherry Makerney, Todd Anzlovar and Mike Bowers can be scheduled by emailing them directly.";
      } else if (club.equals("cranecreek")) {
          custMsg = "Please call the Tennis Shop at 514-4361 to schedule a lesson or to reserve the Ball Machine.";
      } else {
          custMsg = "Please call the Golf Shop to schedule a lesson with the Professional Staff.";
      }
      
      out.println(SystemUtils.HeadTitle("Lesson Not Supported"));
      out.println("<BODY>");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<CENTER>");
      out.println("<BR><BR><H3>Online Scheduling Currently Unavailable</H3>");
      out.println("<BR><BR>Sorry, the online lesson scheduler is not available for your club at this time.");
      out.println("<BR><BR>" + custMsg);
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
  
      proid = "";
      return(proid);            // return none
   }


   //
   //  Get the existing pro ids and prompt for a selection
   //
   if (!club.equals("bellemeadecc")) {        // Belle Meade - no access for members
   
      try {

         pstmt = con.prepareStatement (
                 "SELECT id FROM lessonpro5 WHERE activity_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, sess_activity_id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            id = rs.getInt("id");          // get the proid
            count++;                       // count how many exist
            found = true;                  // found some
         }
         pstmt.close();
      }
      catch (Exception exc) {

         found = false;      // not found
      }
   }

   //
   //  Process according to the number of ids found
   //
   if (found == false) {        // if none yet
      
      // Pros do not exist yet - inform user to add some

      out.println(SystemUtils.HeadTitle("Sequence Error"));
      out.println("<BODY>");
      SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
      out.println("<CENTER>");
          out.println("<BR><BR><H3>Online Scheduling Currently Unavailable</H3>");
          out.println("<BR><BR>Sorry, the online scheduler is not available for your club at this time.");
          out.println("<BR><BR>Please contact your Golf Shop Staff for assistance.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
  
      proid = "";
      return(proid);            // return none
   }

   //
   //  Build the html page to request the lesson info
   //
   out.println(SystemUtils.HeadTitle("Member - Select Pro"));
   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");
   SystemUtils.getMemberSubMenu(req, out, caller);            // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   if (club.equals( "interlachenspa" )) {
      out.println("<b>Spa Service Selection</b><br>");
      out.println("<br>To schedule a service, click on the desired Spa Service from the list below.");
   } else {
      out.println("<b>Lesson Pro Selection</b><br>");
      if (sess_activity_id != 0) {
          if (req.getParameter("group") == null) {
             out.println("<br>To schedule a lesson, click on the desired Professional from the list below.");
          } else {
             out.println("<br>To view available Group Lessons and Clinics, click on the desired Professional from the list below.");
          }
      } else {
          if (req.getParameter("group") == null) {
             out.println("<br>To schedule a lesson, click on the desired Golf Professional from the list below.");
          } else {
             out.println("<br>To view available Group Lessons, click on the desired Golf Professional from the list below.");
          }
      }
   }
   out.println("</font></td></tr></table><br><br><br>");

   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"pform\">");

   if (req.getParameter("group") != null) {

      out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
   }

   if (club.equals( "interlachenspa" )) {
      out.println("<b>Spa Service:</b>&nbsp;&nbsp;");
   } else {
      out.println("<b>Lesson Pro:</b>&nbsp;&nbsp;");
   }
   out.println("<select size=\"1\" name=\"proid\" onChange=\"document.pform.submit()\">");
   out.println("<option value=\"0\">Select One</option>");

   try {

      if (club.equals( "interlachen" )) {      // if Interlachen, list Kevin first
        
         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY fname DESC");

      } else if (club.equals("virginiacc")) {
         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY fname");
      } else {
         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonpro5 WHERE activity_id = ?");
      }

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

         id = rs.getInt("id");                   // get the proid

         out.println("<option value=\"" +id+ "\">" +proName+ "</option>");
      }
      pstmt.close();

      out.println("</select>");
      out.println("</form>");

   }
   catch (Exception exc) {

      out.println("</form>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>" + exc.getMessage());
   }

   out.println("<br><br>");
   out.println("<font size=\"2\"><br><br>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
     
   proid = "";           // return none so we know user was prompted
     
   return(proid);    
 }


 // ********************************************************************
 //  Prompt user for the Lesson Type and Date
 // ********************************************************************

 private void promptType(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session, 
                         Connection con, String proid)
              throws ServletException, IOException {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String caller = (String)session.getAttribute("caller");      

   String proName = "";
   String ltname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String cost = "";
   String desc = "";
   String temp = "";
     
   int id = 0;
   int i = 0;
   int length = 0;
   int count = 0;
   int col = 0;                       // init column counter
   int max = 30;                      // # of days to display in calendar
    
   long date = 0;

   boolean found = false;

   try {
      id = Integer.parseInt(proid);              // id of pro 
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  If Cordillera allow 90 days calendars
   //
   if (club.equals( "cordillera" ) || club.equals( "minneapolis" ) || club.equals( "congressional" ) || club.equals( "mnvalleycc" )) {
     
      max = 90;
   }

   if (club.equals( "minikahda" ) || club.equals( "ncrcc" ) || club.equals( "tcclub" ) || club.equals("paloaltohills")) {
     
      max = 60;
   }

   if (club.equals( "cecc" )) {     // Columbia-Edgewater CC
     
      max = 120;
   }

   if (club.equals( "colletonriverclub" )) {     // Colleton River
     
      max = 180;
   }

   if (club.equals( "snoqualmieridge" )) {     // TPC at Snoqualmie Ridge
     
      max = 3;      // they only use lesson book to reserve a golf simulator 
   }

   //
   //  If Cherry Hills allow 14 days calendars
   //
   if (club.equals( "cherryhills" ) || club.equals( "royaloakscc" )) {

      max = 14;
   }

   //
   //  Array to hold the indicator for each of the next 30 days
   //
   int [] daysA = new int [max];

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   String [] oday_table = { "o", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th",
                           "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th",
                           "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th" };

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
   //  Get the Pro's name and lesson types available
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

      }
      pstmt.close();
   }
   catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;
   }

   //
   //  Determine which of the next 30 days are avialable for this pro
   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   month++;                                            // month starts at zero

   date = (year * 10000) + (month * 100) + day;      // get today

   //
   //  Start with today and check for available lesson times with this pro
   //
   try {

      for (i=0; i<max; i++) {

         daysA[i] = 0;          // init this day to not available

         pstmt = con.prepareStatement (
                 "SELECT time FROM lessonbook5 " +
                 "WHERE proid = ? AND date = ? AND block = 0 AND in_use = 0 AND lgname = '' AND memname = '' AND ltype = ''");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setLong(2,date);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            daysA[i] = 1;          // set this day available
         }
           
         pstmt.close();

         //
         //  Get next day
         //
         cal.add(Calendar.DATE,1);
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) +1;
         day = cal.get(Calendar.DAY_OF_MONTH);

         date = (year * 10000) + (month * 100) + day;
      }
           
   }
   catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;
   }

   //
   //  Build the html page to request the lesson type and date
   //
   out.println(SystemUtils.HeadTitle("Member - Select Lesson Type"));
   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");
   SystemUtils.getMemberSubMenu(req, out, caller);            // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   if (club.equals("cherryhills") && (proName.equalsIgnoreCase("John Ogden") || proName.equalsIgnoreCase("Jason Rauzi") || 
       proName.equalsIgnoreCase("Matt Stewart") || proName.equalsIgnoreCase("Jeff Brummett"))) {
       
       out.println("<br><b>Teaching Pro Not Available Online</b><br>");
       out.println("<br><br><p align=center><b><i>Please call the Golf Shop at 303-350-5220 to schedule a lesson with " +proName+ ".</b></i></p><br>");

       out.println("<font size=\"2\"><br>");
       out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\">");
       out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</form>");
       out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
       out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
       out.println("</input></form></font>");
       out.println("</center>");
       out.println("</body>");
       out.println("</html>");
       out.close();
       return;
   } 
   
   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   if (club.equals( "interlachenspa" )) {
      out.println("<b>Spa Service Selection</b><br>");
      out.println("<br>Select a Spa Service from the list below and then click on the day desired.<br>");
   } else {
      out.println("<b>Individual Lesson Type Selection</b><br>");
      out.println("<br>Select a Lesson Type from the list below and then click on the day desired.<br>");
   }
   out.println("</font></td></tr></table>");

   out.println("<font size=\"2\"><br>");
   if (club.equals( "interlachenspa" )) {
      out.println("Spa Services and Dates available for:&nbsp;&nbsp;<b>" +proName+ "</b>");
   } else {
      out.println("Individual Lesson Types and Dates available for:&nbsp;&nbsp;<b>" +proName+ "</b>");
   }

   out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"pform\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");

   try {

      if (!club.equals( "interlachen" )) {

         out.println("<table border=\"1\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" width=\"80%\">");
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"4\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         if (club.equals( "interlachenspa" )) {
            out.println("<b>Spa Services (Select One)</b>");
         } else {
            out.println("<b>Individual Lesson Types  (Select One)</b>");
         }
         out.println("</font></td></tr>");

         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\">");
         if (club.equals( "interlachenspa" )) {
            out.println("<u><b>Service Type</b></u>");
         } else {
            out.println("<u><b>Individual Lesson Type</b></u>");
         }
         out.println("</font></td>");
         out.println("<td align=\"left\" width=\"320\">");
         out.println("<font size=\"2\">");
         out.println("<b><u>Description</u></b>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b><u>Minutes</u></b>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">");
         out.println("<b><u>Price</u></b>");
         out.println("</font></td></tr>");

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontype5 WHERE proid = ? ORDER BY descript");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            ltname = rs.getString("ltname");
            length = rs.getInt("length");
            cost = rs.getString("cost");
            desc = rs.getString("descript");

            out.println("<tr><td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<input type=\"radio\" name=\"ltype\" value=\"" +ltname+ "\"> " +ltname);
            out.println("</font></td>");
            out.println("<td align=\"left\" width=\"320\">");
            out.println("<font size=\"2\">");
            out.println(desc);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(length);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(cost);
            out.println("</font></td></tr>");

         }
         pstmt.close();

      } else {             // Interlachen - do not display description, minutes or price

         out.println("<table border=\"1\" cellpadding=\"3\" bgcolor=\"#F5F5DC\">");
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Individual Lesson Types  (Select One)</b>");
         out.println("</font></td></tr>");

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontype5 WHERE proid = ? ORDER BY descript");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            ltname = rs.getString("ltname");

            out.println("<tr><td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println("<input type=\"radio\" name=\"ltype\" value=\"" +ltname+ "\"> " +ltname);
            out.println("</font></td></tr>");
         }
         pstmt.close();

      }                  // end of IF Interlachen

      out.println("</table>");
      out.println("<br>");

      //
      //  Get today's date
      //
      cal = new GregorianCalendar();                 // get current date & time (Central Time)

      int today = cal.get(Calendar.DAY_OF_MONTH);  // save today's number

      // Restrict member booking on day of and, if after 8pm, tomorrow as well.
      if (club.equals("deserthighlands")) {
          int daysToAdd = 1;
          int tempHr = cal.get(Calendar.HOUR_OF_DAY);
          tempHr = SystemUtils.adjustTime(con, tempHr * 100);
          if (tempHr < 0) { tempHr = tempHr * -1; }
          if (tempHr >= 2000) {
              daysToAdd = 2;
              count++;
          }
          
          cal.add(Calendar.DATE, daysToAdd);
          count++;
      }
      
      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);
      day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      month++;                                            // month starts at zero

      String mm = mm_table[month];                  // month name

      int numDays = numDays_table[month];           // number of days in month

      if (numDays == 0) {                           // if Feb

         int leapYear = year - 2000;
         numDays = feb_table[leapYear];             // get days in Feb
      }

      //
      //  Now display a calendar for the next 30 days - only allow user to select days when pro has times avail
      //
      //  build one large table to hold one table for each month required
      //
      out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\">");

      //
      //  table for first month
      //
      out.println("<table border=\"1\" width=\"200\" bgcolor=\"#f5f5dc\">");
         out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + mm + "&nbsp;&nbsp;" + year + "</b></font>");
         out.println("</td></tr><tr>");
            out.println("<td align=\"center\"><font size=\"2\">S</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">M</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">W</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">T</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">F</font></td>");
            out.println("<td align=\"center\"><font size=\"2\">S</font></td>");

         out.println("</tr><tr>");        // first row of days

         for (i = 1; i < day_num; i++) {    // skip to the first day
            out.println("<td><br></td>");
            col++;
         }

         while (day < today) {
            out.println("<td align=\"center\"><font size=\"2\">" + day + "</font></td>");    // put in day of month
            col++;
            day++;

            if (col == 7) {
               col = 0;                             // start new week
               out.println("</tr><tr>");
            }
         }

         while (count < max) {                 // start with today, go to end of month or 'max' days 

            if (day <= numDays ) {

               if (daysA[count] == 1) {            // color the buttons for 'days available'

                  out.println("<td align=\"center\"><font size=\"2\"><b>");          // limegreen
                  out.println("<input type=\"submit\" value=\"" + day + "\" name=\"i" + count + "\" style=\"background:#32CD32\"></b></font></td>");

               } else {
                 
                  out.println("<td align=\"center\"><font size=\"2\"><b>" +day);   // not available
                  out.println("</b></font></td>");
               }
               col++;
               day++;
               count++;

               if (col == 7) {
                  col = 0;                             // start new week
                  out.println("</tr><tr>");
               }

            } else {

               day = 1;                               // start a new month
               month = month + 1;
               if (month > 12) {
                  month = 1;                          // end of year - use Jan
                  year = year + 1;                    // new year
               }
               numDays = numDays_table[month];        // number of days in month
               mm = mm_table[month];                  // month name

               if (numDays == 0) {                           // if Feb

                  int leapYear = year - 2000;
                  numDays = feb_table[leapYear];             // get days in Feb
               }
               out.println("</tr></table></td><td align=\"center\" valign=\"top\">");
               out.println("<table border=\"1\" width=\"200\" bgcolor=\"#f5f5dc\">");
                  out.println("<tr><td colspan=\"7\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\"><b>" + mm + "&nbsp;&nbsp;" + year + "</b></font>");
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
         }                        // end of while count < 30

         //
         // finish the current month
         //
         while (day <= numDays ) {

            out.println("<td align=\"center\"><font size=\"2\">" + day + "</font></td>");    // put in day of month
            col++;
            day++;
            count++;

            if (col == 7) {
               col = 0;                             // start new week
               out.println("</tr><tr>");
            }

         }
         out.println("</tr>");

      //
      // end of calendar row
      //
      out.println("</table>");
      out.println("</td></tr></table>");
      out.println("</form>");

   }
   catch (Exception exc) {

      out.println("</form>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>" + exc.getMessage());
   }

   out.println("<font size=\"2\"><br>");
   out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ********************************************************************
 //  Display List of Available Group Lessons for Pro
 // ********************************************************************

 private void displayGroup(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                         Connection con, String proid)
              throws ServletException, IOException {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String proName = "";
   String dayName = "";
   String monthName = "";
   String lgname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String temp = "";
   String desc = "";
   String cost = "";
   String ampms = "";
   String ampme = "";
   String days = "";
   String eo_week = "";
   String date_start_disp = "";
   String date_end_disp = "";

   int id = 0;
   int i = 0;
   int hr = 0;
   int min = 0;
   int max = 0;
   int time = 0;
   int stime = 0;
   int etime = 0;
   int length = 0;
   int count = 0;
   int count2 = 0;
   int col = 0;                       // init column counter
   int lesson_id = 0;
   int clinic = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long date_start = 0;
   long date_end = 0;
   long yy = 0;
   long mm = 0;
   long dd = 0;

   boolean found = false;

   try {
      id = Integer.parseInt(proid);              // id of pro
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   String [] day_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };


   //
   //  Get the Pro's name and lesson types available
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

      }
      pstmt.close();
   }
   catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;
   }

   //
   //  Determine which of the next 180 days are avialable for this pro
   //
   //  Get today's date
   //
   Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   month++;                                            // month starts at zero

   sdate = (year * 10000) + (month * 100) + day;      // get today

   //
   //  Get date 180 days from now
   //
   cal.add(Calendar.DATE,180);                  // roll ahead 180 days (limit for members)
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);

   month++;                                            // month starts at zero

   edate = (year * 10000) + (month * 100) + day;      // end date

   //
   //  Build the html page to request the Group Lesson
   //
   out.println(SystemUtils.HeadTitle("Member - Select Group Lesson"));
   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");
   SystemUtils.getMemberSubMenu(req, out, caller);            // required to allow submenus on this page
   out.println("<font face=\"Verdana, Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<b>Group Lesson Selection</b><br>");
      out.println("<br>Select a Group Lesson from the list below.<br>");
   out.println("</font></td></tr></table><br><br>");

   out.println("<font size=\"2\">");
   out.println("Group Lessons available for:&nbsp;&nbsp;<b>" +proName+ "</b>");

   out.println("<br><br>");
   out.println("</font>");

   out.println("<table border=\"1\" cellpadding=\"3\" bgcolor=\"#F5F5DC\" width=\"90%\">");
   out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"8\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Group Lessons</b>");
   out.println("</font></td></tr>");

   out.println("<tr><td align=\"left\">");
   out.println("<font size=\"2\">");
   out.println("<u><b>Lesson Name</b></u>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   if (sess_activity_id != 0) {
       out.println("<b><u>Dates</u></b>");
   } else {
       out.println("<b><u>Date</u></b>");
   }
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b><u>Start</u></b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b><u>End</u></b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b><u>Group Size</u></b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<b><u>Price</u></b>");
   out.println("</font></td>");
   out.println("<td align=\"left\" width=\"240\">");
   out.println("<font size=\"2\">");
   out.println("<b><u>Description</u></b>");
   out.println("</font></td>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("&nbsp;");
   out.println("</font></td>");
   out.println("</tr>");

   //
   //  Get any Group Lessons in the next 180 days for this pro
   //
   try {

      if (sess_activity_id != 0) {

          // For non-golf activities, grab any clinics for which today's date falls between their start and end dates
          pstmt = con.prepareStatement (
                  "SELECT *, DATE_FORMAT(date,'%c/%e/%Y') as date_start_disp, DATE_FORMAT(edate,'%c/%e/%Y') as date_end_disp FROM lessongrp5 " +
                  "WHERE proid = ? AND ((date >= ? AND date <= ?) OR (edate >= ? AND edate <= ?)) " +
                  "ORDER BY lname");
          pstmt.clearParameters();
          pstmt.setInt(1, id);
          pstmt.setLong(2, sdate);
          pstmt.setLong(3, edate);
          pstmt.setLong(4, sdate);
          pstmt.setLong(5, edate);
          rs = pstmt.executeQuery();
          
      } else {
          
          pstmt = con.prepareStatement (
                  "SELECT * FROM lessongrp5 " +
                  "WHERE proid = ? AND date >= ? AND date <= ? ORDER BY date");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1,id);
          pstmt.setLong(2,sdate);
          pstmt.setLong(3,edate);
          rs = pstmt.executeQuery();      // execute the prepared pstmt
      }

      while (rs.next()) {

         lesson_id = rs.getInt("lesson_id");
         lgname = rs.getString("lname");
         date = rs.getLong("date");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         max = rs.getInt("max");
         cost = rs.getString("cost");
         desc = rs.getString("descript");
         clinic = rs.getInt("clinic");

         if (sess_activity_id != 0) {

             date_start = rs.getLong("date");
             date_end = rs.getLong("edate");
             date_start_disp = rs.getString("date_start_disp");
             date_end_disp = rs.getString("date_end_disp");

             eo_week = "Every ";
             if (rs.getInt("eo_week") == 1) eo_week = "Every Other ";

             days = "";
             if (rs.getInt("sunday") == 1) days += "<br>" + eo_week + "Sunday";
             if (rs.getInt("monday") == 1) days += "<br>" + eo_week + "Monday";
             if (rs.getInt("tuesday") == 1) days += "<br>" + eo_week + "Tuesday";
             if (rs.getInt("wednesday") == 1) days += "<br>" + eo_week + "Wednesday";
             if (rs.getInt("thursday") == 1) days += "<br>" + eo_week + "Thursday";
             if (rs.getInt("friday") == 1) days += "<br>" + eo_week + "Friday";
             if (rs.getInt("saturday") == 1) days += "<br>" + eo_week + "Saturday";
         }

         //
         //  use date to determine date and day name
         //
         yy = date / 10000;                             // get year
         mm = (date - (yy * 10000)) / 100;              // get month
         dd = (date - (yy * 10000)) - (mm * 100);       // get day

         month = (int)mm;
         day = (int)dd;
         year = (int)yy;

         //
         //  Get this date
         //
         cal = new GregorianCalendar();                 // get current date & time (Central Time)
         cal.set(Calendar.YEAR, year);                 // change to requested date
         cal.set(Calendar.MONTH, month-1);
         cal.set(Calendar.DAY_OF_MONTH, day);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

         dayName = day_table[day_num];                   // get name for day

         //
         //  Get time strings
         //
         hr = stime / 100;           // determine start time values
         min = stime - (hr * 100);

         ampms = " AM";
         if (hr == 12) {
            ampms = " PM";
         }
         if (hr > 12) {
            ampms = " PM";
            hr = hr - 12;   
         }
         if (min < 10) {
            ampms = hr + ":0" + min + ampms;
         } else {
            ampms = hr + ":" + min + ampms;
         }

         hr = etime / 100;           // determine end time values
         min = etime - (hr * 100);

         ampme = " AM";
         if (hr == 12) {
            ampme = " PM";
         }
         if (hr > 12) {
            ampme = " PM";
            hr = hr - 12;   
         }
         if (min < 10) {
            ampme = hr + ":0" + min + ampme;
         } else {
            ampme = hr + ":" + min + ampme;
         }

         count2 = 0;        // init counter
           
         //
         //  See how many are already singed up for this lesson.  Only grab players for golf group lessons, or clinics set for only a single date
         //
         if (sess_activity_id == 0 || date_start == date_end || clinic == 1) {

             if (clinic == 1 && sess_activity_id != 0) {

                 pstmt2 = con.prepareStatement (
                         "SELECT memname FROM lgrpsignup5 " +
                         "WHERE lesson_id = ? AND memname != ''");

                 pstmt2.clearParameters();        // clear the parms
                 pstmt2.setInt(1,lesson_id);
                 rs2 = pstmt2.executeQuery();      // execute the prepared pstmt
                 
             } else {

                 pstmt2 = con.prepareStatement (
                         "SELECT memname FROM lgrpsignup5 " +
                         "WHERE proid = ? AND lname = ? AND date = ? AND memname != ''");

                 pstmt2.clearParameters();        // clear the parms
                 pstmt2.setInt(1,id);
                 pstmt2.setString(2,lgname);
                 pstmt2.setLong(3,date);
                 rs2 = pstmt2.executeQuery();      // execute the prepared pstmt
             }

             while (rs2.next()) {

                count2++;
             }
             pstmt2.close();
         }

         //
         //  Display the group lesson row
         //
         out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"pform\">");
         out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
         out.println("<input type=\"hidden\" name=\"lgname\" value=\"" +lgname+ "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
         out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
         out.println("<input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");  // get to lesson signup

         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"1\">");
         out.println(lgname);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         if (sess_activity_id == 0 || date_start == date_end) {
             out.println(dayName+ " " +month+ "/" +day+ "/" +year);
         } else {
             out.println(date_start_disp + " - " + date_end_disp + days);
         }
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         out.println(ampms);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         out.println(ampme);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         out.println(max);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         out.println(cost);
         out.println("</font></td>");
         out.println("<td align=\"left\" width=\"240\">");
         out.println("<font size=\"1\">");
         out.println(desc);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"1\">");
         if (sess_activity_id == 0 || date_start == date_end || clinic == 1) {
             if (count2 < max) {                                            // if room for more
                out.println("<input type=\"submit\" value=\"Sign Up\">");
             } else {
                out.println("Full");
             }
         } else {
             out.println("<input type=\"submit\" name=\"selectDate\" value=\"Select Date\">");
         }
         out.println("</font></td>");
           
         out.println("</tr></form>");
           
         count++;
      }
      pstmt.close();

      out.println("</table>");
      out.println("<font size=\"2\"><br>");
      if (count == 0) {       // if none found
         out.println("<p>Sorry, no Group Lessons were found for this pro during the next 180 days.</p>");
         out.println("<br>");
      }

   }
   catch (Exception exc) {

      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>" + exc.getMessage());
   }

   out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\">");
   out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");   // go prompt for lesson pro
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();
 }


 // ***************************************************************************
 //  Process the 'Request Lesson Time' from the Lesson Book or My Tee Times
 // ***************************************************************************

 private void reqTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get username
   String caller = (String)session.getAttribute("caller");          
   String memName = (String)session.getAttribute("name");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String ltname = "";
   String proName = "";
   String memname = "";
   String memid = "";
   String phone1 = "";
   String phone2 = "";
   String ph1 = "";
   String ph2 = "";
   String notes = "";
   String color = "";
   String index = "";
   String error = "";
   String ampm = "AM";
   String s_ampm = "AM";
   String e_ampm = "AM";

   int id = 0;
   int i = 0;
   int block = 0;
   int num = 0;
   int length = 0;
   int billed = 0;
   int in_use = 0;
   int fragment = 0;
   int smonth = 0;               
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int hr = 0;
   int min = 0;
   int hr2 = 0;
   int min2 = 0;
   int time = 0;
   int stime = 0;
   int etime = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int month2 = 0;
   int day2 = 0;
   int year2 = 0;
   int canhrs = 0;
   int cantime = 0;
   int lesson_id = 0;
   int activity_id = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long todayDate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;

   ArrayList<Integer> sheet_ids = new ArrayList<Integer>();

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   // 
   //  Get the parms
   //
   String dates = req.getParameter("date");           // date of lesson book
   String calDate = req.getParameter("calDate");      // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");           // time requested
   String dayName = req.getParameter("day");          // name of the day in this book
   String ltype = req.getParameter("ltype");          // lesson type requested

   if (req.getParameter("index") != null) {           // index provided (came from Member_teelist)?

      index = req.getParameter("index");     
   }

   // See if we came from teelist, if so, use the passed activity_id instead of the session-based one
   if (index.equals("999") && req.getParameter("activity_id") != null) {
       activity_id = Integer.parseInt(req.getParameter("activity_id"));
   } else {
       activity_id = sess_activity_id;
   }

   // Get passed lesson_id
   lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      time = Integer.parseInt(times);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

   //
   //  Check if request is a Cancel - return without changes
   //
   if (req.getParameter("cancel") != null) {

      cancel(id, time, date, calDate, ltype, out, req, caller, con);       // process cancel request
      return;
   }


   //******************************************************************
   //  Request is to edit a specific Lesson Time
   //******************************************************************
   //
   //   Check if this day is in use, if not, set it 
   //
   try {

      in_use = verifyLesson.checkInUse(date, time, id, lesson_id, activity_id, ltype, user, con);

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (in_use != 0) {              // if time slot already in use

      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H2>Time Slot Busy</H2>");
      out.println("<BR><BR>Sorry, but this time slot is currently busy.<BR>");
      out.println("<BR>Please select another time or try again later.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
     
   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname, fname, mi, suffix FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         String mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         String suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

         canhrs = rs.getInt("canlimit");                            // get hours in advance for cancel res
      }
      pstmt.close();

   }
   catch (Exception exc) {
   }

   //
   //  Get today's date to see if this is today
   //
   Calendar cal2 = new GregorianCalendar();        // get todays date
   cal2.add(Calendar.HOUR_OF_DAY,canhrs);          // roll ahead 'adv cancel limit' hours
   year2 = cal2.get(Calendar.YEAR);
   month2 = cal2.get(Calendar.MONTH+1);
   day2 = cal2.get(Calendar.DAY_OF_MONTH);
   hr2 = cal2.get(Calendar.HOUR_OF_DAY);           // 24 hr clock (0 - 23)
   min2 = cal2.get(Calendar.MINUTE);

   todayDate = (year2 * 10000) + (month2 * 100) + day2;    // create a date field of yyyymmdd
   cantime = (hr2 * 100) + min2;          // advance cancel time (earliest time that can be cancelled today)


   //
   //  Get the current lesson book info for the time requested
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT phone1, phone2, notes FROM lessonbook5 WHERE recid = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         phone1 = rs.getString("phone1");
         phone2 = rs.getString("phone2");
         notes = rs.getString("notes");

      } else {

         nfError(req, out, caller);            // LT not found
         pstmt.close();
         return;
      }
      pstmt.close();

      //
      //  See if this user has any phone numbers
      //
      pstmt = con.prepareStatement (
               "SELECT phone1, phone2 FROM member2b " +
               "WHERE username = ?");

      pstmt.clearParameters();               // clear the parms
      pstmt.setString(1, user);
      rs = pstmt.executeQuery();             // execute the prepared stmt

      if (rs.next()) {

         ph1 = rs.getString(1);
         ph2 = rs.getString(2);
      }
      pstmt.close();
        
      if (phone1.equals( "" ) && !ph1.equals( "" )) {
        
         phone1 = ph1;
      }

      if (phone2.equals( "" ) && !ph2.equals( "" )) {

         phone2 = ph2;
      }

   }
   catch (Exception exc) {

      dbError(req, out, exc, caller);
      return;
   }

   //  If in an activity other than golf, Check to see if there is available time on the timesheets to accommodate this lesson booking
   if (activity_id != 0) {

       int ltlength = 0;
       String locations_csv = "";

       // Pull up info regarding the lesson type needed so we can check to see if there are available times
       try {

           pstmt = con.prepareStatement(
                   "SELECT length, locations FROM lessontype5 " +
                   "WHERE proid = ? AND activity_id = ? AND ltname = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, id);
           pstmt.setInt(2, activity_id);
           pstmt.setString(3, ltype);
           rs = pstmt.executeQuery();

           if (rs.next()) {
               ltlength = rs.getInt("length");
               locations_csv = rs.getString("locations");
           }

       } catch (Exception exc) {

           dbError(req, out, exc, caller);
           return;
       }

       int time_start = time;
       int time_end = getEndTime(time, ltlength);

       boolean bookAllSheets = false;

       sheet_ids = verifyLesson.checkActivityTimes(lesson_id, activity_id, locations_csv, date, date, new int[1], time_start, time_end, bookAllSheets, con, out);

       // If no time slots returned, send the user back to the lesson book
       if (sheet_ids.size() == 0) {

           out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
           out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
           out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
           out.println("<CENTER><BR><BR><H3>No Times Available</H3>");
           out.println("<BR><BR>Sorry, no available time slots were found for this lesson.<BR>");
           out.println("<BR>Please select a different time from the lesson book.");
           out.println("<BR><BR>");
           out.println("<font size=\"2\">");
           out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"can\">");
           out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
           out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
           out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
           out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" +activity_id+ "\">");
           out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
           out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
           out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
           out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
           out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
           out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
           out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\">");
           out.println("</form></font>");
           out.println("</CENTER></BODY></HTML>");
           out.close();
           return;
       }
   }
   
   //
   //  Output a page to prompt for lesson time info
   //
   out.println(SystemUtils.HeadTitle2("Member - Edit Lesson Time"));

      //
      //*******************************************************************
      //  Erase player name and phone (erase button selected next to player's name)
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Erase name script
      out.println("<!--");

      out.println("function erasename(pos1) {");

      out.println("document.playerform[pos1].value = '';");           // clear the player field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("<script type=\"text/javascript\">");             // Move Notes into textarea
      out.println("<!--");
      out.println("function movenotes() {");
      out.println("var oldnotes = document.playerform.oldnotes.value;");
      out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script


   out.println("</HEAD>");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     if (club.equals( "interlachenspa" )) {
        out.println("<font size=\"5\">Member Spa Service Request</font>");
     } else {
        out.println("<font size=\"5\">Member Lesson Request</font>");
     }
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"630\" align=\"left\">");
         out.println("<font size=\"2\">");
            out.println("<b>Warning</b>:<br>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;You have approximately <b>6 minutes</b> to complete this task.");
            out.println("&nbsp;&nbsp;If you want to return without making any");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;changes, <b>do not ");
            out.println("use your browser's BACK</b> button/option. ");
            out.println("Instead select the <b>Go Back</b> option below.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\"><br>");
      out.println("Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
      if (club.equals( "interlachenspa" )) {
         out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Time:");
      } else {
         out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Lesson Time:");
      }
      if (min < 10) {
         out.println("&nbsp;&nbsp;<b>" +hr+ ":0" +min+ " " +ampm+ "</b>");
      } else {
         out.println("&nbsp;&nbsp;<b>" +hr+ ":" +min+ " " +ampm+ "</b>");
      }
      out.println("</font>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 3 tables below
         out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");         // col for Go Back button
           out.println("<font size=\"2\">");
           out.println("<br><br><br><br><br><br><br>");

           out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"can\">");
              out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
              out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
              out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
              out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" +activity_id+ "\">");
              out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
              out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
              out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
              out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
              out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
              out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
           out.println("Return<br>w/o Changes:<br>");
           out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

         out.println("</font></td>");   // end of 1st col

            out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"playerform\">");
            out.println("<input type=\"hidden\" name=\"reqtime2\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
            out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" +activity_id+ "\">");
            out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
            out.println("<input type=\"hidden\" name=\"memname\" value=\"" +memName+ "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
            out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");

         out.println("<td align=\"center\" valign=\"top\">");    // col for main box

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            if (club.equals( "interlachenspa" )) {
               out.println("<b>Add or Change Member Service</b>");
            } else {
               out.println("<b>Add or Change Member Lesson</b>");
            }
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">&nbsp;&nbsp;Member:&nbsp;&nbsp;&nbsp;&nbsp;" +memName);

            if (club.equals( "interlachenspa" )) {
               out.println("</p><p align=\"left\">&nbsp;&nbsp;Spa Service:&nbsp;&nbsp;" +proName);
               out.println("</p><p align=\"left\">&nbsp;&nbsp;Service Type:&nbsp;&nbsp;" +ltype);
            } else {
               out.println("</p><p align=\"left\">&nbsp;&nbsp;Lesson Pro:&nbsp;&nbsp;" +proName);
               out.println("</p><p align=\"left\">&nbsp;&nbsp;Lesson Type:&nbsp;&nbsp;" +ltype);
            }
            out.println("</p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Phone 1:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"phone1\" value=\"" +phone1+ "\" size=\"20\" maxlength=\"24\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone1')\" style=\"cursor:hand\">");
            out.println("</p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Phone 2:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"phone2\" value=\"" +phone2+ "\" size=\"20\" maxlength=\"24\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone2')\" style=\"cursor:hand\">");
            out.println("</p>");

            //
            //   Notes
            //
            //   Script will put any existing notes in the textarea (value= doesn't work)
            //
            out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

            out.println("&nbsp;&nbsp;Notes:&nbsp;&nbsp;");
            out.println("<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"24\" rows=\"3\">");
            out.println("</textarea>");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
            out.println("<br>");

            out.println("<input type=submit value=\"Submit\" name=\"submit\">");
            out.println("</font></td></tr>");
            out.println("</table>");
            if (!index.equals( "" )) {          // if an edit              
               //
               //  If today and within the cancel limit set by pro, then do not allow member to cancel
               //
               if (date == todayDate && time < cantime) {
                  if (club.equals( "interlachenspa" )) {
                     out.println("<p>It is too late to cancel this reservation.  Please call the Spa for assistance.</p>");
                  } else {   // ok to cancel
                     if (activity_id > 0) {
                         out.println("<p>It is too late to cancel this lesson.  Please call the Pro Shop for assistance.</p>");
                     } else {
                         out.println("<p>It is too late to cancel this lesson.  Please call the Golf Shop for assistance.</p>");
                     }
                  }
               } else {   // ok to cancel
                  if (club.equals( "interlachenspa" )) {
                     out.println("<p align=\"center\"><input type=submit value=\"Cancel Service\" name=\"remove\"></p>");
                  } else {
                     out.println("<p align=\"center\"><input type=submit value=\"Cancel Lesson\" name=\"remove\"></p>");
                  }
               }
            }
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

 }


 // ********************************************************************
 //  Process the 'Update Lesson Time' from reqTime above (submit)
 // ********************************************************************

 private void reqTime2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get username of user
   String fullName = (String)session.getAttribute("name");
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   String memid = user;               // get name of user

   String ltname = "";
   String memname2 = "";
   String memnameSave = "";
   String color = "";
   String error = "";
   String ampm = "AM";
   String s_ampm = "AM";
   String e_ampm = "AM";
   String locations_csv = "";

   String oldmemname = "";       // old lesson time values
   String oldmemid = "";
   String oldltype = "";
   String oldphone1 = "";
   String oldphone2 = "";
   String oldnotes = "";
   String proName = "";
   String activity_name = "";

   int id = 0;
   int i = 0;
   int j = 0;
   int ltlength = 0;
   int oldlength = 0;
   int num = 0;
   int length = 0;
   int billed = 0;
   int in_use = 0;
   int fragment = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int time2 = 0;
   int etime = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int lesson_id = 0;
   int activity_id = 0;
   int sheet_activity_id = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;

   ArrayList<Integer> sheet_ids = new ArrayList<Integer>();

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String dates = req.getParameter("date");       // date of lesson book
   String calDate = req.getParameter("calDate");  // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");       // time requested
   String dayName = req.getParameter("day");          // name of the day in this book
   String memname = req.getParameter("memname");
   String ltype = req.getParameter("ltype");
   String phone1 = req.getParameter("phone1");
   String phone2 = req.getParameter("phone2");  
   String notes = req.getParameter("notes");
   String index = req.getParameter("index");
   lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

   // See if we came from teelist, if so, use the passed activity_id instead of the session-based one
   if (index.equals("999") && req.getParameter("activity_id") != null) {
       activity_id = Integer.parseInt(req.getParameter("activity_id"));
   } else {
       activity_id = sess_activity_id;
   }

   oldltype = ltype;
   memnameSave = memname;         // save for email

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      time = Integer.parseInt(times);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   hr = time / 100;
   min = time - (hr * 100);

   if (hr == 12) {
      ampm = "PM";
   } else {
      if (hr > 12) {
         hr = hr - 12;          // adjust
         ampm = "PM";
      }
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;

   if (!notes.equals( "" )) {

      notesL = notes.length();       // get length of notes
   }

   //
   //   Check if still in use (should be)
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT memname, length, in_use, phone1, phone2, notes, recid " +
         "FROM lessonbook5 WHERE recid = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         oldmemname = rs.getString(1);
         oldlength = rs.getInt(2);
         in_use = rs.getInt(3);
         oldphone1 = rs.getString(4);
         oldphone2 = rs.getString(5);
         oldnotes = rs.getString(6);
      }
      pstmt.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   if (in_use == 0) {              // if slot no longer in use

      out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Reservation Timer Expired</H3>");
      out.println("<BR><BR>Sorry, but this time slot has been returned to the system.<BR>");
      out.println("<BR>The system timed out and released the time.");
      out.println("<BR><BR>");
      out.println("id=" +id+ ", date=" +date+ ", time=" +time);
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname, fname, mi, suffix FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

         String mi = rs.getString("mi");                                          // middle initial
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));                   // last name

         String suffix = rs.getString("suffix");                                // suffix
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();                             // convert to one string

      }
      pstmt.close();

   }
   catch (Exception exc) {
   }


   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   if (req.getParameter("remove") != null) {

      memname = "";
      memid = "";
      ltype = "";
      phone1 = "";
      phone2 = "";
      notes = "";
      num = 0;                
      ltlength = 0;
      billed = 0;
      sheet_activity_id = 0;

      emailCan = 1;      // send email notification for Cancel Request
      sendemail = 1;

      // If an activity other than golf, remove lesson_id value from time sheets for this lesson
      if (activity_id != 0) {
          try {
              pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = 0 WHERE lesson_id = ?");
              pstmt.clearParameters();
              pstmt.setInt(1, lesson_id);
              pstmt.executeUpdate();

              pstmt.close();

          } catch (Exception exc) {
              dbError(req, out, exc, caller);
          }
      }

   } else {   // not a cancel request  

      //
      //  Process normal res request
      //
      //  Get the lesson type info for the lesson type requested
      //
      try {

         //
         //  Get the lesson types for this pro
         //
         pstmt = con.prepareStatement (
                 "SELECT length, locations FROM lessontype5 WHERE proid = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setString(2,ltype);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            ltlength = rs.getInt("length");
            locations_csv = rs.getString("locations");
         }
         pstmt.close();

      }
      catch (Exception exc) {

         dbError(req, out, exc, caller);
         return;
      }

      // If in an activity other than golf, check activity sheets for an empty chunk of slots to cover the lesson time
      if (activity_id != 0) {

          int time_start = time;
          int time_end = getEndTime(time, ltlength);

          boolean bookAllSheets = false;

          sheet_ids = verifyLesson.checkActivityTimes(lesson_id, activity_id, locations_csv, date, date, new int[1], time_start, time_end, bookAllSheets, con, out);

          // If no time slots returned, send the user back to the lesson book
          if (sheet_ids.size() == 0) {

              out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
              out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
              out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
              out.println("<CENTER><BR><BR><H3>No Times Available</H3>");
              out.println("<BR><BR>Sorry, no available time slots were found for this lesson.<BR>");
              out.println("<BR>Please select a different time from the lesson book.");
              out.println("<BR><BR>");
              out.println("<font size=\"2\">");
              out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"can\">");
              out.println("<input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
              out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
              out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
              out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" +activity_id+ "\">");
              out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
              out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
              out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
              out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
              out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
              out.println("<input type=\"hidden\" name=\"day\" value=\"" +dayName+ "\">");
              out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\">");
              out.println("</form></font>");
              out.println("</CENTER></BODY></HTML>");
              out.close();
              return;
          }
      }
      
      num = 1;                // set one member for lesson

      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      sendemail = 1;         // init email flags
      emailNew = 0;
      emailMod = 0;

      //
      //  Determine if emails should be sent
      //
//      if (!index.equals( "" )) {       // if from My Tee Times Calendar (update)
        
//         emailMod = 1;       // set modified lesson
                 
//      } else {
              
         emailNew = 1;       // set new lesson
//      }

   }  // end of IF 'cancel time' request

   //
   //  Update the lesson time
   //
   try {

      pstmt = con.prepareStatement (
         "UPDATE lessonbook5 " +
         "SET memname = ?, memid = ?, num = ?, length = ?, billed = ?, in_use = 0, ltype = ?, " +
         "phone1 = ?, phone2 = ?, notes = ?, sheet_activity_id = ? " +
         "WHERE recid = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, memname);
      pstmt.setString(2, memid);
      pstmt.setInt(3, num);
      pstmt.setInt(4, ltlength);
      pstmt.setInt(5, billed);
      pstmt.setString(6, ltype);
      pstmt.setString(7, phone1);
      pstmt.setString(8, phone2);
      pstmt.setString(9, notes);
      pstmt.setInt(10, sheet_activity_id);

      pstmt.setInt(11, lesson_id);

      pstmt.executeUpdate();      // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
      out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  If on an activity other than golf, block any necessary slots
   // if index 0 was -99, that means no time slots in activity_sheets are needed for this lesson, skip this portion
   //
   if (activity_id != 0 && sheet_ids.size() > 0) {

       if (sheet_ids.get(0) != -99) {
           String inString = "";
           int count = 0;

           for (int k=0; k<sheet_ids.size(); k++) {
               inString += sheet_ids.get(k) + ",";
           }

           inString = inString.substring(0, inString.length() - 1);

           try {
               pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = ? WHERE sheet_id IN (" + inString + ")");
               pstmt.clearParameters();
               pstmt.setInt(1, lesson_id);

               count = pstmt.executeUpdate();

               pstmt.close();

               if (count < inString.length()) {
                   out.println("<!-- Not enough slots blocked!!! -->");
               }

               // Figure out what activity sheet they're on and store this value.
               pstmt = con.prepareStatement(
                       "SELECT acs.activity_id, ac.activity_name FROM activity_sheets acs " +
                       "LEFT OUTER JOIN activities ac ON acs.activity_id = ac.activity_id " +
                       "WHERE acs.sheet_id = ?");
               pstmt.clearParameters();
               pstmt.setInt(1, sheet_ids.get(0));

               rs = pstmt.executeQuery();

               if (rs.next()) {

                   sheet_activity_id = rs.getInt("acs.activity_id");
                   activity_name = rs.getString("ac.activity_name");

                   pstmt2 = con.prepareStatement("UPDATE lessonbook5 SET sheet_activity_id = ? WHERE recid = ?");
                   pstmt2.clearParameters();
                   pstmt2.setInt(1, sheet_activity_id);
                   pstmt2.setInt(2, lesson_id);

                   count = pstmt2.executeUpdate();

                   pstmt2.close();
               }

               pstmt.close();

           } catch (Exception exc) {
               out.println("<!-- Error encountered: " + exc.getMessage() + " -->");
           }
       } else {
           activity_name = "Check with Pro";
       }
   }


   //
   //  Make an entry in teehist so we can track these
   //
   if (oldmemname.equals( "" )) {

      //  new lesson
      SystemUtils.updateHist(date, dayName, time, 99, "Lesson Time", proName, memname, ltype, "", "",
                             user, fullName, 0, con);

   } else {

      //  update lesson
      SystemUtils.updateHist(date, dayName, time, 99, "Lesson Time", proName, memname, ltype, "", "",
                             user, fullName, 1, con);
   }


   //
   //  Return to user
   //
   out.println(SystemUtils.HeadTitle2("Lesson Book - Return"));
   out.println("</HEAD>");
   out.println("<BODY>");
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
     
   if (club.equals( "interlachenspa" )) {

      if (!index.equals( "" )) {       // if from My Tee Times Calendar (update)

         out.println("<BR><BR><H3>Spa Request Has Been Updated</H3>");
         out.println("<BR><BR>Thank you, your service request has been updated.");

      } else {

         out.println("<BR><BR><H3>Spa Request Has Been Booked</H3>");
         out.println("<BR><BR>Thank you, your Spa Service has been reserved.");
      }
      out.println("<BR><BR>Go to 'My Calendar' to view or change this request.");

   } else {

      if (!index.equals( "" )) {       // if from My Tee Times Calendar (update)

         out.println("<BR><BR><H3>Lesson Has Been Updated</H3>");
         out.println("<BR><BR>Thank you, your lesson has been updated.");
         if (sess_activity_id != 0 && req.getParameter("remove") == null) {
             if (!activity_name.equals("") && !activity_name.equals("Check with Pro")) {
                 out.println("  This lesson will take place on " + activity_name + ".");
             } else {
                 out.println("  Please contact the Lesson Pro for the location of this lesson.");
             }
         }

      } else {

         out.println("<BR><BR><H3>Lesson Has Been Booked</H3>");
         out.println("<BR><BR>Thank you, your lesson has been reserved.");
         if (sess_activity_id != 0 && req.getParameter("remove") == null) {
             if (!activity_name.equals("") && !activity_name.equals("Check with Pro")) {
                 out.println("  This lesson will take place on " + activity_name + ".");
             } else {
                 out.println("  Please contact the Lesson Pro for the location of this lesson.");
             }
         }
      }
      if (IS_TLT) {
         out.println("<BR><BR>Go to <b>Notifications - My Activities</b> to view or change this lesson request.");
      } else if (sess_activity_id != 0) {
         out.println("<BR><BR>Go to <b>Reservations - My Reservations</b> to view or change this lesson request.");
      } else {
         out.println("<BR><BR>Go to <b>Tee Times - My Tee Times Calendar</b> and click on the lesson booking to view or cancel this lesson request.");
      }
   }
   
   out.println("<BR><BR>");
   
   //
   //  Print dining request prompt if system is active and properly configured
   //
   if (Utilities.checkDiningLink("mem_lesson", con) && req.getParameter("remove") == null) {
       String msgDate = yy + "-" + mm + "-" + dd;
       Utilities.printDiningPrompt(out, con, msgDate, dayName, user, 1, "lesson", "&proid=" + id + "&index=" + index, false);
   }
   

   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   //************************************************************************************
   //  Now check if more lesson times need to be updated based on the lesson length
   //************************************************************************************
   //
   if (ltlength == 0) {      // if cancel request
     
      ltlength = oldlength;    // restore old length to update all time slots
   }
     
   etime = getEndTime(time, ltlength);

   //
   //  Get the lesson times that follow this time and within the lesson length
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT time, memname FROM lessonbook5 " +
              "WHERE proid = ? AND activity_id = ? AND DATE = ? AND time > ? AND time < ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setInt(2, activity_id);
      pstmt.setLong(3, date);
      pstmt.setInt(4, time);
      pstmt.setInt(5, etime);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      loop1:
      while (rs.next()) {

         time2 = rs.getInt("time");
         memname2 = rs.getString("memname");
           
         //
         //  set this time as a subsequent time for the primary time being processed
         //
         pstmt2 = con.prepareStatement (
            "UPDATE lessonbook5 " +
            "SET memname = ?, memid = ?, num = 0, length = 0, billed = 0, in_use = 0, ltype = '', " +
            "phone1 = '', phone2 = '', notes = '' " +
            "WHERE proid = ? AND activity_id = ? AND date = ? AND time = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setString(1, memname);
         pstmt2.setString(2, memid);

         pstmt2.setInt(3, id);
         pstmt2.setInt(4, activity_id);
         pstmt2.setLong(5, date);
         pstmt2.setInt(6, time2);

         pstmt2.executeUpdate();      // execute the prepared stmt

         pstmt2.close();
      }

      pstmt.close();

      //
      //  Also, see if any phone numbers were entered.  If so, then see if we should update the member's record
      //
      if (!phone1.equals( "" ) && !memid.equals( "" )) {      // if phone # entered

         String ph1 = "";
         String ph2 = "";

         boolean changePhone = false;

         pstmt = con.prepareStatement (
                  "SELECT phone1, phone2 FROM member2b " +
                  "WHERE username = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setString(1, memid);
         rs = pstmt.executeQuery();             // execute the prepared stmt

         if (rs.next()) {

            ph1 = rs.getString(1);
            ph2 = rs.getString(2);
         }
         pstmt.close();

         if (ph1.equals( "" )) {        // if member has no phone #
           
            ph1 = phone1;               // he/she does now
            changePhone = true;         // update the record
         }
         if (ph2.equals( "" ) && !phone2.equals( "" )) {   // if member has no phone2, but 1 entered here

            ph2 = phone2;               // he/she does now
            changePhone = true;         // update the record
         }

         //
         //  update the member record if phone # to change
         //
         if (changePhone == true) {
           
            pstmt = con.prepareStatement (
               "UPDATE member2b " +
               "SET phone1 = ?, phone2 = ? " +
               "WHERE memid = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, phone1);
            pstmt.setString(2, phone2);
            pstmt.setString(3, memid);

            pstmt.executeUpdate();      // execute the prepared stmt

            pstmt.close();
         }
      }

   }
   catch (Exception e1) {
   }

   //
   //***********************************************
   //  Now send any emails if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();            // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "lesson";                        // type = lesson time
      parme.date = date;
      parme.time = time;
      parme.fb = id;                                // pro id
      parme.mm = month;
      parme.dd = day;
      parme.yy = year;
      parme.emailNew = emailNew;
      parme.emailMod = emailMod;                    // not used
      parme.emailCan = emailCan;
      parme.day = dayName;
      parme.user = user;                            // from this user
      parme.user1 = user;                           // username of member
      parme.player1 = memnameSave;                  // name of member
      parme.actual_activity_name = activity_name;   // this is the 'court' level name
      parme.activity_name = getActivity.getActivityName(sess_activity_id, con);          // root activity name

      if (emailCan == 1) {                          // if Cancel Request

         parme.course = oldltype;                   // put lesson type in course field

      } else {

         parme.course = ltype;                      // put lesson type in course field
      }

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);                 // in common
   }

 }


 // ********************************************************************
 //  Process the 'Request Group Lesson Time' 
 // ********************************************************************

 private void grpLesson(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String caller = (String)session.getAttribute("caller");       
   String memname = (String)session.getAttribute("name");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   String ltype = "";
   String cost = "";
   String descript = "";
   String error = "";
   String phone1 = "";
   String phone2 = "";
   String ph1 = "";
   String ph2 = "";
   String notes = "";
   String index = "";
   String dates = "";

   int id = 0;
   int max = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int stime = 0;
   int lesson_id = 0;
   int clinic = 0;

   long date = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String lgname = req.getParameter("lgname");    // name of group lesson

   if (req.getParameter("calDate") != null) {
       dates = req.getParameter("calDate");
       
       StringTokenizer dateTok = new StringTokenizer(dates, "/");

       String tempMM = dateTok.nextToken();
       if (tempMM.length() == 1) tempMM = "0" + tempMM;
       String tempDD = dateTok.nextToken();
       if (tempDD.length() == 1) tempDD = "0" + tempDD;
       String tempYY = dateTok.nextToken();

       dates = tempYY + tempMM + tempDD;
       
   } else {
       dates = req.getParameter("date");       // date of lesson book
   }
   
   if (req.getParameter("index") != null) {           // index provided (came from Member_teelist)?

      index = req.getParameter("index");
   }

   if (req.getParameter("lesson_id") != null) lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   //
   //  Get this year
   //
   Calendar cal = new GregorianCalendar();           // get today's date
   int thisYear = cal.get(Calendar.YEAR);            // get the year

   //
   //  Get the current group lesson info
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT stime, max, cost, descript, clinic FROM lessongrp5 WHERE lesson_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         stime = rs.getInt("stime");
         max = rs.getInt("max");
         cost = rs.getString("cost");
         descript = rs.getString("descript");
         clinic = rs.getInt("clinic");

      } else {

         nfError(req, out, caller);            // LT not found
         pstmt.close();
         return;
      }
      pstmt.close();

      //
      //  See if user is already scheduled for this lesson
      //
      if (clinic == 1 && sess_activity_id != 0) {

          pstmt = con.prepareStatement (
                  "SELECT phone1, phone2, notes " +
                  "FROM lgrpsignup5 WHERE lesson_id = ? AND memid = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1,lesson_id);
          pstmt.setString(2,user);
          rs = pstmt.executeQuery();      // execute the prepared pstmt

      } else {

          pstmt = con.prepareStatement (
                  "SELECT phone1, phone2, notes " +
                  "FROM lgrpsignup5 WHERE proid = ? AND lname = ? AND date = ? AND memid = ?");

          pstmt.clearParameters();        // clear the parms
          pstmt.setInt(1,id);
          pstmt.setString(2,lgname);
          pstmt.setLong(3,date);
          pstmt.setString(4,user);
          rs = pstmt.executeQuery();      // execute the prepared pstmt
      }

      if (rs.next()) {

         phone1 = rs.getString("phone1");              // if already signed up
         phone2 = rs.getString("phone2");
         notes = rs.getString("notes");
           
         hit = true;
      }
      pstmt.close();

      if (hit == true && index.equals( "" )) {       // if exists and this is a new request - reject   

         out.println(SystemUtils.HeadTitle2("Group Lesson Return"));
         out.println("</HEAD>");
         out.println("<BODY>");
         SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
         out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Unable to Sign Up for Group Lesson</H3>");
         out.println("<BR><BR>Sorry, you are already registered for this Group Lesson.");
         if (sess_activity_id != 0) {
             out.println("<BR><BR>Go to <b>My Reservations - Calendar</b> to view or change this lesson request.");
         } else {
             if (IS_TLT) {
                out.println("<BR><BR>Go to <b>Notifications - My Activities</b> to view or change this lesson request.");
             } else {
                out.println("<BR><BR>Go to <b>Tee Times - My Tee Times</b> to view or change this lesson request.");
             }
         }
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
         out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  See if this user has any phone numbers
      //
      pstmt = con.prepareStatement (
               "SELECT phone1, phone2 FROM member2b " +
               "WHERE username = ?");

      pstmt.clearParameters();               // clear the parms
      pstmt.setString(1, user);
      rs = pstmt.executeQuery();             // execute the prepared stmt

      if (rs.next()) {

         ph1 = rs.getString(1);
         ph2 = rs.getString(2);
      }
      pstmt.close();

      if (phone1.equals( "" ) && !ph1.equals( "" )) {

         phone1 = ph1;
      }

      if (phone2.equals( "" ) && !ph2.equals( "" )) {

         phone2 = ph2;
      }

   }
   catch (Exception exc) {

      dbError(req, out, exc, caller);
      return;
   }

   //
   //  Output a page to display the group lesson info
   //
   out.println(SystemUtils.HeadTitle2("Member - Edit Group Lesson Time"));

      //
      //*******************************************************************
      //  Erase phone
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Erase name script
      out.println("<!--");

      out.println("function erasename(pos1) {");

      out.println("document.playerform[pos1].value = '';");           // clear the player field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script type=\"text/javascript\">");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //  movenotes
      out.println("<script type=\"text/javascript\">");             // Move Notes into textarea
      out.println("<!--");
      out.println("function movenotes() {");
      out.println("var oldnotes = document.playerform.oldnotes.value;");
      out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script
      //
      //*******************************************************************
      //  End of Scripts
      //*******************************************************************

   out.println("</HEAD>");
   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#336633\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"630\" align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Group Lesson:</b>&nbsp;&nbsp;&nbsp;&nbsp;" +lgname);
            out.println("<br>Make the desired additions or changes below and click on Submit when done.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" align=\"center\" width=\"630\">"); 
      out.println("<tr>");
      out.println("<td align=\"center\">");      
      out.println("<font size=\"2\">");
      out.println("Date:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b>");
        out.println("<br><br><b>Cost:</b> " +cost+ "&nbsp;&nbsp;&nbsp;&nbsp;<b>Max Group Size:</b> " +max);
        out.println("<br><br><b>Description:</b> " +descript);
      out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 3 tables below
         out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");         // col for Go Back button
         out.println("<font size=\"2\">");
         out.println("<br><br><br><br><br><br><br>");

         if (index.equals( "" )) {
            out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
         } else {
            out.println("<form action=\"/" +rev+ "/servlet/Member_teelist\" method=\"get\">");
         }
         out.println("Return<br>w/o Changes:<br>");
         out.println("<input type=\"submit\" value=\"Go Back\"></form>");

         out.println("</font></td>");   // end of 1st col

            out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"playerform\">");
            out.println("<input type=\"hidden\" name=\"groupLesson2\" value=\"" +lgname+ "\">");
            out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
            out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
            out.println("<input type=\"hidden\" name=\"time\" value=\"" +stime+ "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
            out.println("<input type=\"hidden\" name=\"max\" value=\"" +max+ "\">");

         out.println("<td align=\"center\" valign=\"top\">");    // col for main box

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" cellpadding=\"5\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Add or Change Member Lesson</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<p align=\"left\">&nbsp;&nbsp;Member:&nbsp;&nbsp;&nbsp;&nbsp;" +memname);
            out.println("</p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Phone 1:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"phone1\" value=\"" +phone1+ "\" size=\"20\" maxlength=\"24\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone1')\" style=\"cursor:hand\">");
            out.println("</p>");

            out.println("<p align=\"left\">&nbsp;&nbsp;Phone 2:&nbsp;&nbsp;");
            out.println("<input type=\"text\" name=\"phone2\" value=\"" +phone2+ "\" size=\"20\" maxlength=\"24\">");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone2')\" style=\"cursor:hand\">");
            out.println("</p>");

            //
            //   Notes
            //
            //   Script will put any existing notes in the textarea (value= doesn't work)
            //
            out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

            out.println("&nbsp;&nbsp;Notes:&nbsp;&nbsp;");
            out.println("<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"24\" rows=\"3\">");
            out.println("</textarea>");
            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
            out.println("<br>");

            out.println("<input type=submit value=\"Submit\" name=\"submit\">");
            out.println("</font></td></tr>");
            out.println("</table>");
            if (!index.equals( "" )) {          // if an edit
               out.println("<p align=\"center\"><input type=submit value=\"Cancel Lesson\" name=\"remove\"></p>");
            }
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

 }


 // ********************************************************************
 //  Process the 'Update Group Lesson Time' from grpLesson above (submit)
 // ********************************************************************

 private void grpLesson2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String caller = (String)session.getAttribute("caller");           
   String memname = (String)session.getAttribute("name");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   String temps = "";
   String memid = user;
   String memName = memname;
   String tempMM = "";
   String tempDD = "";
   String tempYY = "";
   String msgDate = "";
   String day_name = "";
   String params = "";

   int id = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int j = 0;
   int max = 0;
   int num = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int time = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int dayOfWeek = 0;
   int msgPlayerCount = 0;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean hit = false;

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  Get the parms
   //
   String lgname = req.getParameter("groupLesson2");    // name of group lesson
   String dates = req.getParameter("date");             // date of lesson book
   String stime = req.getParameter("time");             // start time of group lesson
   String maxs = req.getParameter("max");               // max number of members allowed in group lesson
   String phone1 = req.getParameter("phone1");          // phone #
   String phone2 = req.getParameter("phone2");          // phone #
   String notes = req.getParameter("notes");            // notes
   String index = req.getParameter("index");            // index (if came from My Tee Times)
   int lesson_id = Integer.parseInt(req.getParameter("lesson_id"));

   //
   //  Convert to ints
   //
   try {
      date = Long.parseLong(dates);
      max = Integer.parseInt(maxs);
      time = Integer.parseInt(stime);
   }
   catch (NumberFormatException e) {
   }

   //
   //  break down the date and time
   //
   yy = date / 10000;                             // get year
   mm = (date - (yy * 10000)) / 100;              // get month
   dd = (date - (yy * 10000)) - (mm * 100);       // get day

   month = (int)mm;
   day = (int)dd;
   year = (int)yy;

   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   if (req.getParameter("remove") != null) {

      memid = "";
      memName = "";
      phone1 = "";
      phone2 = "";
      notes = "";

//      emailCan = 1;      // send email notification for Cancel Request
//      sendemail = 1;

   } else {   // not a cancel request

      //
      //  Determine if emails should be sent
      //
      sendemail = 1;         // init email flags
      emailNew = 0;
      emailMod = 0;

      if (!index.equals( "" )) {       // if from My Tee Times (an update)

//         emailMod = 1;
         emailNew = 1;      // just use new for now

      } else {

         emailNew = 1;
      }
   }

   //
   //  Add or Update the group lesson signup list
   //
   try {

      if (index.equals( "" ) && req.getParameter("remove") == null) {       // if new entry

         pstmt = con.prepareStatement (
           "INSERT INTO lgrpsignup5 (lesson_id, proid, lname, date, memname, " +
           "memid, billed, phone1, phone2, notes) " +
           "VALUES (?,?,?,?,?,?,0,?,?,?)");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, lesson_id);
         pstmt.setInt(2, id);
         pstmt.setString(3, lgname);
         pstmt.setLong(4, date);
         pstmt.setString(5, memname);
         pstmt.setString(6, user);
         pstmt.setString(7, phone1);
         pstmt.setString(8, phone2);
         pstmt.setString(9, notes);

         pstmt.executeUpdate();      // execute the prepared stmt

         pstmt.close();

      } else {

         pstmt = con.prepareStatement (
            "UPDATE lgrpsignup5 " +
            "SET lesson_id = ?, memname = ?, memid = ?, phone1 = ?, phone2 = ?, notes = ? " +
            "WHERE proid = ? AND lname = ? AND date = ? AND memid = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, lesson_id);
         pstmt.setString(2, memName);
         pstmt.setString(3, memid);
         pstmt.setString(4, phone1);
         pstmt.setString(5, phone2);
         pstmt.setString(6, notes);

         pstmt.setInt(7, id);
         pstmt.setString(8, lgname);
         pstmt.setLong(9, date);
         pstmt.setString(10, user);

         pstmt.executeUpdate();      // execute the prepared stmt

         pstmt.close();
      }

   }
   catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;
   }
   
   Calendar cal = new GregorianCalendar();       // get todays date
   cal.set(year, month - 1, day);
   
   dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
   
   if (dayOfWeek == 1) { day_name = "sunday"; }
   if (dayOfWeek == 2) { day_name = "monday"; }
   if (dayOfWeek == 3) { day_name = "tuesday"; }
   if (dayOfWeek == 4) { day_name = "wednesday"; }
   if (dayOfWeek == 5) { day_name = "thursday"; }
   if (dayOfWeek == 6) { day_name = "friday"; }
   if (dayOfWeek == 7) { day_name = "saturday"; }
         
   tempYY = String.valueOf(yy);
   tempMM = String.valueOf(mm);
   tempDD = String.valueOf(dd);
   if (tempMM.length() < 2) {
       tempMM = "0" + tempMM;
   }
   if (tempDD.length() < 2) {
       tempDD = "0" + tempDD;
   }
   msgDate = tempYY + "-" + tempMM + "-" + tempDD;
   msgPlayerCount = 1;
   
   if (index.equals("")) {
       params = "&proid=" + id + "&group=yes";
   } else {
       params = "&group=yes";
   }
   //
   //  Return to user
   //
   out.println(SystemUtils.HeadTitle2("Lesson Book - Return"));
   out.println("</HEAD>");
   out.println("<BODY>");
   SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Group Lesson Signup Complete</H3>");
   if (index.equals( "" )) {       // if new entry
      out.println("<BR><BR>You have been added to the Group Lesson.");
   } else {
      out.println("<BR><BR>Thank you, your entry in the Group Lesson has been updated.");
   }
   
   if (IS_TLT) {
      out.println("<BR><BR>Go to <b>Notifications - My Activities</b> to view or change this lesson request.");
   } else {
      out.println("<BR><BR>Go to <b>Tee Times - My Tee Times</b> to view or change this lesson request.");
   }
   
   out.println("<BR><BR>");
   
   //
   //  Print dining request prompt
   //
   if (Utilities.checkDiningLink("mem_lesson", con) && req.getParameter("remove") == null) {
       Utilities.printDiningPrompt(out, con, msgDate, day_name, user, msgPlayerCount, "lesson", params, false);
   }
   

   out.println("<font size=\"2\">");
   if (index.equals( "" )) {       // if new entry
      out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
      out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   } else {
      out.println("<form action=\"/" +rev+ "/servlet/Member_teelist\" method=\"get\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   }
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   //***********************************************
   //  Now send any emails if necessary
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
      parme.date = date;
      parme.time = time;
      parme.fb = id;                        // pro id
      parme.mm = month;
      parme.dd = day;
      parme.yy = year;
      parme.emailNew = emailNew;
      parme.emailMod = 0;            // not used yet???
      parme.emailCan = 0;
      parme.day = " ";

      parme.type = "lessongrp";             // type = group lesson time
      parme.user = user;                    // from user
      parme.user1 = user;                   // username of member
      parme.player1 = memname;              // name of member
      parme.course = lgname;                // put group lesson name in course field

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common
   }

 }

 private void grpSelectDate(HttpServletRequest req, HttpSession session, PrintWriter out, Connection con) {

     String club = (String)session.getAttribute("club");               // get name of club
     String user = (String)session.getAttribute("user");               // get name of user
     String caller = (String)session.getAttribute("caller");
     int sess_activity_id = (Integer)session.getAttribute("activity_id");

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String lname = "";

     int id = 0;
     int lesson_id = 0;
     int lesson_date = 0;
     int playerCount = 0;
     int maxPlayers = 0;
     int sdate = 0;
     int edate = 0;
     int jump = 0;
     int diff = 0;

     boolean dateMatch = false;

     if (req.getParameter("jump") != null) jump = Integer.parseInt(req.getParameter("jump"));

     lesson_id = Integer.parseInt(req.getParameter("lesson_id"));
     id = Integer.parseInt(req.getParameter("proid"));

     try {

         // Get the name of this lesson group from lessongrp5
         pstmt = con.prepareStatement("SELECT lname, date, edate, max, DATEDIFF(edate, CURDATE()) as diff FROM lessongrp5 WHERE lesson_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, lesson_id);
         rs = pstmt.executeQuery();

         if (rs.next()) {
             lname = rs.getString("lname");
             sdate = rs.getInt("date");
             edate = rs.getInt("edate");
             maxPlayers = rs.getInt("max");
             diff = rs.getInt("diff");
         }

         pstmt.close();

         // Get the current date values
         Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
         int cal_year = cal_date.get(Calendar.YEAR);
         int cal_month = cal_date.get(Calendar.MONTH) + 1;
         int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
         int curr_date = (cal_year * 10000) + (cal_month * 100) + cal_day;

         //  Determine oldest date values - month, day, year
         int s_yy = sdate / 10000;
         int s_mm = (sdate - (s_yy * 10000)) / 100;
         int s_dd = sdate - ((s_yy * 10000) + (s_mm * 100));

         int e_yy = edate / 10000;
         int e_mm = (edate - (e_yy * 10000)) / 100;
         int e_dd = edate - ((e_yy * 10000) + (e_mm * 100));

         int default_mm = cal_month;
         int default_yy = cal_year;

         if (curr_date < sdate) {
             default_mm = s_mm;
             default_yy = s_yy;
         } else if (curr_date > edate) {
             default_mm = e_mm;
             default_yy = e_yy;
         }

         if (diff > 0) {

             // Create and initialize an array to hold the day colorings
             int[] daysArray = new int[diff];

             for (int i=0; i<diff; i++) {
                 daysArray[i] = 0;
             }

             // Set the calendar object to the start date of the group lesson
             //cal_date.set(s_yy, s_mm - 1, s_dd);

             int thisyear = cal_date.get(Calendar.YEAR);
             int thismonth = cal_date.get(Calendar.MONTH) +1;
             int thisday = cal_date.get(Calendar.DAY_OF_MONTH);

             int thisdate = (thisyear * 10000) + (thismonth * 100) + thisday;

             if (!lname.equals("")) {
                 pstmt = con.prepareStatement(
                         "SELECT date, (SELECT COUNT(*) FROM lgrpsignup5 WHERE lname = ? AND date = lessonbook5.date) AS playerCount " +
                         "FROM lessonbook5 " +
                         "WHERE lgname = ? AND date >= CURDATE() " +
                         "GROUP BY date " +
                         "ORDER BY date");
                 pstmt.clearParameters();
                 pstmt.setString(1, lname);
                 pstmt.setString(2, lname);
                 rs = pstmt.executeQuery();

                 int i = 0;

                 // Loop through all the dates this clinic takes place on
                 while (rs.next()) {

                     lesson_date = rs.getInt("date");
                     playerCount = rs.getInt("playerCount");
                     
                     // Loop through dates between the start and end date of the clinic
                     while (i<diff) {

                         dateMatch = false;

                         if (thisdate == lesson_date) {

                             // If player slots left and date hasn't already passed, display date in green
                             if (playerCount < maxPlayers) {
                                 daysArray[i] = 1;  // Space left (green)
                             } else {   // Otherwise, display in red
                                 daysArray[i] = 2;  // No Space (sienna)
                             }

                             dateMatch = true;
                         }

                         //  Get next day
                         cal_date.add(Calendar.DATE,1);
                         thisyear = cal_date.get(Calendar.YEAR);
                         thismonth = cal_date.get(Calendar.MONTH) +1;
                         thisday = cal_date.get(Calendar.DAY_OF_MONTH);

                         thisdate = (thisyear * 10000) + (thismonth * 100) + thisday;

                         i++;

                         // If we matched on this date, then break out of the inner loop and move to next date
                         if (dateMatch) break;
                     }
                 }

                 pstmt.close();
             }

             // Start output of HTML page
             out.println(SystemUtils.HeadTitle2("Member - Edit Group Lesson Time"));

             out.println("</HEAD>");

             // include files for dynamic calendars
             out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
             out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");      // use new cal script

             out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
             SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
             out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

             out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
             out.println("<tr><td valign=\"top\" align=\"center\">");

             out.println("<table border=\"1\" cols=\"1\" align=\"center\" bgcolor=\"#336633\" cellpadding=\"3\">");
             out.println("<tr>");
             out.println("<td width=\"630\" align=\"center\">");
             out.println("<font color=\"#FFFFFF\" size=\"2\">");
             out.println("<b>Group Lesson:</b>&nbsp;&nbsp;&nbsp;&nbsp;" +lname);
             out.println("<br><br>Select the desired date to attend this lesson.");
             out.println("<br>Available dates are displayed in <b><u>green</u></b>.");
             out.println("<br>Dates in <b><u>red</u></b> indicate that all slots are full.");
             out.println("</font></td></tr>");
             out.println("</table>");

              //
              //  Form for building the Calendars
              //
              out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" target=\"bot\" name=\"frmLoadDay\">");
              out.println("<input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");
              out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
              out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
              out.println("<input type=\"hidden\" name=\"lgname\" value=\"" +lname+ "\">");
              out.println("<input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\">");
              out.println("<input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");  // get to lesson signup

              out.println("</form>");

              // table for calendars built by js

              out.println("<table align=center border=0 height=150>\n<tr valign=top>\n<td>");

              out.println("<div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");

              out.println("</td>\n</tr>\n</table>");

              out.println("<script type=\"text/javascript\">");

              out.println("var g_cal_bg_color = '#F5F5DC';\n");
              out.println("var g_cal_header_color = '#8B8970';\n");
              out.println("var g_cal_border_color = '#8B8970';\n");

              out.println("var g_cal_count = 1;"); // num of calendars
              out.println("var g_cal_year = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_month = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_ending_month = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_ending_day = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);\n");
              out.println("var g_cal_ending_year = new Array(g_cal_count - 1);\n");
              
              // set calendar date parts
              out.println("g_cal_month[0] = " + default_mm + ";\n"); // starting month for calendar (from caller or today)
              out.println("g_cal_year[0] = " + default_yy + ";\n"); // starting year for calendar
              out.println("g_cal_beginning_month[0] = " + s_mm + ";\n"); // oldest month calendar can navigate to
              out.println("g_cal_beginning_year[0] = " + s_yy + ";\n"); // oldest year
              out.println("g_cal_beginning_day[0] = " + s_dd + ";\n"); // oldest day
              out.println("g_cal_ending_month[0] = " + e_mm + ";\n"); // future month calendar can navigate to
              out.println("g_cal_ending_day[0] = " + e_dd + ";\n"); // future day
              out.println("g_cal_ending_year[0] = " + e_yy + ";\n"); // future year

                 out.print("var daysArray = new Array(");
                 for (int j=0; j < diff; j++) {
                    out.print(daysArray[j]);
                    if (j+1 != diff) out.print(",");
                 }
                 out.println(");");

                 out.println("var max = " + diff + ";");

                 out.println("function buildCalendar(pCal, pMonthStartPos, pDaysInPrevMonth, pDaysInMonth, d) {");
                     out.println("var bEndRow = new Boolean(false);");
                     out.println("var iPrintDay = 1;");
                     out.println("var iNextMonthDates = 1;");
                     out.println("var s = calTop(pCal);");
                     out.println("var index = 0;");

                     out.println("while (bEndRow == false) {");
                         out.println("s += '<tr align=right valign=bottom>';");
                         out.println("for (i=1; i<=7; i++) {");
                             out.println("if (pMonthStartPos > 1) {");
                                 out.println("s += buildTD(pDaysInPrevMonth, 'non');");
                                 out.println("pDaysInPrevMonth++; ");
                                 out.println("pMonthStartPos--;");
                             out.println("} else {");
                                 out.println("if (iPrintDay > pDaysInMonth) {");
                                     out.println("s += buildTD(iNextMonthDates, 'non');");
                                     out.println("iNextMonthDates++;");
                                     out.println("bEndRow = true;");
                                 out.println("} else {");
                                     out.println("bEndRow = (iPrintDay == pDaysInMonth);");
                                     out.println("d = new Date(g_cal_year[pCal], g_cal_month[pCal] - 1, iPrintDay);");
                                     out.println("index = determin_index(d);");
                                     out.println("if (");
                                     out.println("(g_cal_month[pCal] <= g_cal_beginning_month[pCal] && g_cal_year[pCal] <= g_cal_beginning_year[pCal] && g_cal_beginning_day[pCal] > iPrintDay) ||");
                                     out.println("(g_cal_month[pCal] >= g_cal_ending_month[pCal] && g_cal_year[pCal] >= g_cal_ending_year[pCal] && g_cal_ending_day[pCal] < iPrintDay) || ");
                                     out.println("index > max || index < 0) {");

                                         out.println("if (index < 0 && select_old_days == true) {");
                                             out.println("s += '<td><a class=day0 href=\"javascript:void(0)\" onclick=\"sd(' + pCal + ',' + g_cal_month[pCal] + ',' + iPrintDay + ',' + g_cal_year[pCal] + ');return false\">' + iPrintDay + '</a></td>';");
                                         out.println("} else {");
                                             out.println("s += buildTD(iPrintDay, 'non');");
                                         out.println("}");

                                     out.println("} else {");

                                         out.println("if (daysArray[index] > 0)  {");
                                             out.println("s += '<td><a class=day' + daysArray[index] + ' href=\"javascript:void(0)\" onclick=\"sd(' + pCal + ',' + g_cal_month[pCal] + ',' + iPrintDay + ',' + g_cal_year[pCal] + ');return false\">' + iPrintDay + '</a></td>';");
                                         out.println("} else {");
                                             out.println("s += buildTD(iPrintDay, 'non');");
                                         out.println("}");
                                     out.println("}");
                                 out.println("}");
                                 out.println("iPrintDay++;");
                             out.println("}");
                         out.println("}");
                         out.println("s += '</tr>';");
                     out.println("}");
                     out.println("s += '</table>';");
                     out.println("return s;");
                 out.println("}");

                 out.println("doCalendar('0');"); // display calendar

              out.println("</script>");

              out.println("</td></tr>");
              out.println("<tr><td align=\"center\">");

              out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"can\">");
              out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
              out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");

              out.println("<input type=\"submit\" value=\"Go Back\"></form>");

              out.println("</td></tr></table>");   // end of table for control panel and cals
         }
     } catch (Exception exc) {
         out.println(exc.getMessage());
     }
 }

 // *********************************************************
 //  Cancel a request to edit the lesson book
 // *********************************************************

 private void cancel(int id, int time, long date, String calDate, String ltype, PrintWriter out, HttpServletRequest req, String caller, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int length = 0;
   int etime = 0;

   String index = req.getParameter("index");               // get return indicator

   //
   //  Clear the 'in_use' flag for all times for this lesson
   //
   try {

      //
      //  First we need to determine how many time slots to check/set (based on lesson type)
      //
      pstmt = con.prepareStatement (
              "SELECT length FROM lessontype5 WHERE proid = ? AND ltname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setString(2, ltype);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         length = rs.getInt(1);
      }
      pstmt.close();

      //
      //  Now determine end time
      //
      etime = getEndTime(time, length);   // get end of lesson time


      pstmt = con.prepareStatement (
         "UPDATE lessonbook5 SET in_use = 0 WHERE proid = ? AND date = ? AND time >= ? AND time < ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, id);
      pstmt.setLong(2, date);
      pstmt.setInt(3, time);
      pstmt.setInt(4, etime);
      pstmt.executeUpdate();

      pstmt.close();

   }
   catch (Exception ignore) {
   }

   //
   //  Return to lesson book
   //
   out.println(SystemUtils.HeadTitle2("Lesson Book - Cancel"));
     
   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_jump?lesson=yes&proid=" +id+ "&calDate=" +calDate+ "&ltype=" +ltype+ "&index=" +index+ "\">");
   out.println("</HEAD>");
   out.println("<BODY>");
   out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
   out.println("<BR><BR>Thank you, the reservation request has been returned to the system without changes.");
   out.println("<BR><BR>");

   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
   out.println("<input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
   out.println("<input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
   out.println("<input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the Request to Display a Pro's Bio
 // ********************************************************************

 private void displayBio(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String proName = "";
   String lname = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String temp = "";
   int id = 0;
   int count = 0;
   boolean found = false;

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  If proid provided, then display his bio, else prompt for pro
   //
   if (id == 0) {

       //
       //  If club does not want members to use the lesson book - inform the user
       //
       // if (club.equals("championhills") || club.equals("wellesley")) {      // allow Wellesley for the summer
       if ((club.equals("cranecreek") && sess_activity_id == 1)) {

          String custMsg = "";

          if (club.equals("cranecreek")) {
              custMsg = "Please call the Tennis Shop at 514-4361 to schedule a lesson or to reserve the Ball Machine.";
          } else {
              custMsg = "Please call the Golf Shop to schedule a lesson with the Professional Staff.";
          }

          out.println(SystemUtils.HeadTitle("Lesson Not Supported"));
          out.println("<BODY>");
          SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
          out.println("<CENTER>");
          out.println("<BR><BR><H3>Online Scheduling Currently Unavailable</H3>");
          out.println("<BR><BR>Sorry, the online lesson scheduler is not available for your club at this time.");
          out.println("<BR><BR>" + custMsg);
          out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
          out.println("</CENTER></BODY></HTML>");
          out.close();

          return;            // return none
       }

      //
      //  Get the existing pro ids and prompt for a selection
      //
      if (!club.equals("bellemeadecc")) {        // Belle Meade - no access for members
   
         try {

            pstmt = con.prepareStatement (
                    "SELECT id FROM lessonpro5");

            pstmt.clearParameters();        // clear the parms
            rs = pstmt.executeQuery();      // execute the prepared pstmt

            while (rs.next()) {

               id = rs.getInt("id");          // get the proid
               count++;                       // count how many exist
               found = true;                  // found some
            }
            pstmt.close();
         }
         catch (Exception exc) {

            found = false;      // not found
         }
      }

      //
      //  Process according to the number of ids found
      //
      if (found == false) {        // if none yet

         // Pros do not exist yet - inform user to add some

         out.println(SystemUtils.HeadTitle("Sequence Error"));
         out.println("<BODY>");
         SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
         out.println("<CENTER>");
         out.println("<BR><BR><H3>Setup Sequence Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
         if (club.equals( "interlachenspa" )) {
            out.println("<BR>There are no Spa Services defined in the system yet.");
            out.println("<BR><BR>Please contact your Club Staff for assistance.");
         } else {
            out.println("<BR>There are no Lesson Pros in the system yet.");
            out.println("<BR><BR>Please contact your Golf Shop Staff for assistance.");
         }
         out.println("<BR><BR>");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;            // return none
      }

      //
      //  Build the html page to request the lesson info
      //
      out.println(SystemUtils.HeadTitle("Member - Select Pro"));
      out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");
      SystemUtils.getMemberSubMenu(req, out, caller);            // required to allow submenus on this page
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");

      out.println("<br><br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
      out.println("<tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      if (club.equals( "interlachenspa" )) {
         out.println("<b>View a Spa Service Description</b><br>");
         out.println("<br>Select a Spa Service from the list below.<br>");
      } else {
         out.println("<b>View a Lesson Pro's Bio</b><br>");
         if (sess_activity_id > 0) {
             out.println("<br>Select a Professional from the list below.<br>");
         } else {
             out.println("<br>Select a Golf Professional from the list below.<br>");
         }
      }
      out.println("</font></td></tr></table><br><br><br>");

      out.println("<form action=\"/" +rev+ "/servlet/Member_lesson\" method=\"post\" name=\"pform\" target=\"_blank\">");
         out.println("<input type=\"hidden\" name=\"bio\" value=\"yes\">");

         out.println("<font size=\"2\">");
         if (club.equals( "interlachenspa" )) {
            out.println("<b>Spa Service:</b>&nbsp;&nbsp;");
         } else {
            out.println("<b>Lesson Pro:</b>&nbsp;&nbsp;");
         }
         out.println("<select size=\"1\" name=\"proid\" onChange=\"if(this.selectedIndex!=0) {document.pform.submit()}\">");
         out.println("<option value=\"0\">Select One</option>");

         try {

            pstmt = con.prepareStatement (
                    "SELECT * FROM lessonpro5 WHERE activity_id = ?");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, sess_activity_id);
            rs = pstmt.executeQuery();      // execute the prepared pstmt

            while (rs.next()) {

               StringBuffer pro_name = new StringBuffer(rs.getString("fname"));  // get first name

               mi = rs.getString("mi");                                          // middle initial
               if (!mi.equals( "" )) {
                  pro_name.append(" ");
                  pro_name.append(mi);
               }
               pro_name.append(" " + rs.getString("lname"));                   // last name

               suffix = rs.getString("suffix");                                // suffix
               if (!suffix.equals( "" )) {
                  pro_name.append(" ");
                  pro_name.append(suffix);
               }

               proName = pro_name.toString();                             // convert to one string

               id = rs.getInt("id");                   // get the proid

               out.println("<option value=\"" +id+ "\">" +proName+ "</option>");
            }
            pstmt.close();

            out.println("</select>");
            out.println("</form>");

         }
         catch (Exception exc) {

            out.println("</form>");
            out.println("<BR><BR>Unable to access the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>" + exc.getMessage());
         }

      out.println("<BR><BR><b>Note:</b> Just click the 'X' in the upper right corner of the new bio window to close it.");

      out.println("<BR><BR>");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</center>");
      out.println("</body>");
      out.println("</html>");
      out.close();

   } else {
     
      //
      //  Proid provided - display his bio
      //
      out.println(SystemUtils.HeadTitle2("Display Pro Bio"));

      out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/announce/" +club+ "_bio" +id+ ".htm\">");
      out.println("</HEAD>");
      out.println("<BODY>");
      out.println("<BR><BR><H3>Pro Bio Requested</H3>");
      out.println("<BR><BR>If this page does not automatically refresh, then click Continue.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<a href=\"/" +rev+ "/announce/" +club+ "_bio" +id+ ".htm\">Continue</a>");
      out.println("</font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
   }
 }


 // *********************************************************
 //  determine time value for lesson length
 // *********************************************************

 private int getEndTime(int time, int ltlength) {


   int hr = 0;
   int min = 0;

   //
   //  add the length field to the time field
   //
   hr = time / 100;            // get hr
   min = time - (hr * 100);    // get minute

   min += ltlength;            // add length to minutes

   while (min > 59 && hr < 23) {     // if exceeded next hour

      hr++;                    // get next hour (most likely won't exceed midnight)
      min -= 60;               // adjust minute
   }

   time = (hr * 100) + min;    // get new time

   return(time);

 }   // end of getEndTime


 // *********************************************************
 // Record Not Found Error
 // *********************************************************

 private void nfError(HttpServletRequest req, PrintWriter out, String caller) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Record Not Found Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to locate the database record at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact your club.");
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(HttpServletRequest req, PrintWriter out, Exception exc, String caller) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY>");
   SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact your club.");
   out.println("<BR><BR>" + exc.getMessage());
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Member_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
