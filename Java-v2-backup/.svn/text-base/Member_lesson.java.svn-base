/***************************************************************************************
 *   Member_lesson:  This servlet will process all lesson requests from the menu.
 *
 *
 *   created: 11/23/2004   Bob P.
 *
 *   last updated:
 *
 *      4/29/14  Restore custom for Kopplin and Kuebler - it was lost in the mobile updates.
 *     12/13/13  Hiwan GC - (hiwan) - Allow members to access the lesson book 365 days in advance (case 2335).
 *     12/12/13  Desert Forest GC (desertforestgolfclub) - Allow members to access only 6 days out on the FlxRez lesson book calendar (case 2330).
 *     10/31/13  The Lakes G&CC (lakesclub) - Commented out custom days in advance booking, so members will be back to the standard 30 days out.
 *     10/24/13  Ridge CC (ridgecc) - Allow members to book lessons 120 days out for pro_id 1 only.
 *      9/10/13  Fixed issues that were preventing Clinic Series group lessons from updating when accessed from My Reservations Calendar.
 *      7/11/13  Rolling Hills CC - CO (rhillscc) - Allow members to access only 7 days out on the FlxRez lesson book calendar (case 2279).
 *      6/20/13  Columbine CC (columbine) - Added a custom to display a special message instead of the lesson book for a particular lesson pro.
 *      6/05/13  Forest Highlands GC (foresthighlands) - Members will now be allowed to book lessons up to 90 days in advance.
 *      5/02/13  Belle Meade CC (bellemeadecc) - Allow members to access only 7 days out on the FlxRez lesson book calendar.
 *      1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *      1/08/13  Change call to lesson pro bio page to utilize the new Common_lesson method for outputting them
 *     12/13/12  Fixed issue where autoblock_times was not getting checked properly when first selecting a lesson time.
 *     12/04/12  Pinehurst CC (pinehurstcountryclub) - Added custom to filter the list of pro names and display only certain pros depending on if the member is looking at individual or group lessons.
 *     11/15/12  Olympic Club (olyclub) - Added custom to only allow members to book lessons up to 14 days in advance (case 2199).
 *     11/12/12  Pinehurst CC (pinehurstcountryclub) - Added custom to make the individual lesson portion of the lesson book view-only (case 2198).
 *      9/06/12  Updated outputTopNav calls to also pass the HttpServletRequest object.
 *      8/23/12  Lesson booking process will no longer try to book court times for lessons if the autoblock_times setting for that lesson type is set to No (0).
 *      7/25/12  Ballantyne CC (ballantyne) - Changed lesson book days in advance back to 3 since the bug seems to have been fixed (case 2073).
 *      6/20/12  Belle Meade CC (bellemeadecc) - Updated so that members are only blocked from the lesson book on the golf side of the system.
 *      5/16/12  Fort Collins CC (fortcollins) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage and only allow members to access 7 days in advance.
 *      5/16/12  Sierra View CC (sierraviewcc) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage and only allow members to access 7 days in advance.
 *      5/10/12  The Lakes G&CC (lakesclub) - Allow members to access 60 days out on the Lesson Book calendar.
 *      4/09/12  Ballantyne CC (ballantyne) - Reverted lesson book days in advance custom to max = 4 so it displays properly (case 2073).
 *      4/03/12  Ballantyne CC (ballantyne) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage (case ?).
 *      4/03/12  Updated lesson book to use SystemLingo entries for Lesson verbiage (for custom verbiage such as "Ball Machine").
 *      4/03/12  Ballantyne CC (ballantyne) - Updated lesson book days in advance custom to display 3 days in advance instead of 4 (case 2073).
 *      3/20/12  Fixed lesson bio path on new skin - also now only pro's with bio pages created will appear in list
 *      3/16/12  Fort Collins CC (fortcollins) - Updated custom messages displayed to members when selecting the lesson feature (case 2121).
 *      3/16/12  Fort Collins CC (fortcollins) - Do not allow Tennis members to access the lesson system (case 2121).
 *      3/15/12  Avon Oaks CC (avonoakscc) - Do not allow members to access the lesson system (case 2137).
 *      2/07/12  Fixed a few tee time references from displaying on the FlxRez side.
 *      2/06/12  New skin - update three custom changes: promptProId() - clubs do not use lesson book, promptType() call pro for time, and reqTime2() - FlxRes.
 *      2/03/12  New Skin - put out pop up window on individual lesson when date selected but no type set. Add close form tag to fix return.
 *      1/26/12  New Skin - add jquery date-picker calendar to Individual Lesson
 *      1/23/12  New SKin - use common methods in Common_skin for bread crumbs and logo.
 *      1/20/12  Remove rounded corners on lesson_req_add_chg_form.
 *      1/19/12  Use standard_list_table for displaying individual and group lessons. Use standard_button. Update available times table to have square look.
 *     12/29/11  Add jquery date picker to grpSelectDate method
 *     12/29/11  Add new skin changes for cancel lesson function. Add new_skin for database errors.
 *     12/23/11  Release new skin changes for individual lessons, group lessons, and lesson pro bio.
 *     12/23/11  Fixed issue when looking up dates for non-clinic group lessons that was causing errors. CURDATE was being compared to a date value without 
 *               being modified via DATE_FORMAT to match the format of the other date.
 *     12/12/11  Westmoor CC (westmoor) - Allow members to access only 7 days out on the Lesson Book calendar (case 2092).
 *     12/08/11  Ballantyne CC (ballantyne) - For FlxRez lessons, perform a check to make sure the member doesn't have a conflicting tennis reservation (the same check we do for tennis reservations) (case 2087).
 *     11/15/11  Ballantyne CC (ballantyne) - Allow Tennis members to access only 3 days out on the lesson book since it's only used for the ball machine (case 2073).
 *     11/06/11  Change paths for lesson bio pages - now using NFS and discrete club folders
 *     11/01/11  Charlotte CC (charlottecc) - Updated 30 min buffer custom to only apply it on the golf side (case 2017).
 *      8/31/11  Updated lesson history entry to include the Lesson Time name as the courseName value for new history entries.
 *      8/31/11  Charlotte CC (charlottecc) - Add a 30 minute buffer following all lesson bookings, and factor this 30 minutes into calculations when determining available times (case 2017).
 *      8/10/11  Charlotte CC (charlottecc) - Only allow members to access 14 days out on Lesson Book calendar.
 *      5/27/11  The Country Club - Brookline (tcclub) - Display interface link to open the Appointment Plus site and log the member in (and create their account on the fly if needed).
 *      3/24/11  The Minikahda Club (minikahda) - Sort individual lesson types by length first, instead of just description.
 *      2/24/11  The Point Lake & GC (pointlake) - Do not allow members to book lessons on the Tennis side.
 *      2/24/11  Golf Academy of America Campuses (gaa*, non-classroom sites) - Only display group lessons up to 10 days in advance.
 *      2/24/11  Changed displayGroup to look a variable number of days out for group lessons, defaulted to 180 days.
 *      2/14/11  Golf Academy of America Campuses (gaa*, non-classroom sites) - Allow members to access only 7 days out on the Lesson Book calendar (case 1939).
 *      1/07/11  Round Hill CC (roundhill) - Allow members to access 45 days out on Lesson Book calendar.
 *     11/10/10  Quaker Ridge GC (quakerridgegc) - Allow members to access only 6 days out on the Lesson Book calendar (case 1908).
 *     10/20/10  Populate new parmEmail fields
 *     10/11/10  Changed pro drop-down lists to order by the new 'sort_by' field in lessonpro5.
 *      8/11/10  Filter some instances of the word 'Golf' for FlxRez activities.  
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

import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*;


// foretees imports
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.common.parmSlot;
import com.foretees.common.parmActivity;
import com.foretees.common.verifyActSlot;
import com.foretees.common.verifyLesson;
import com.foretees.client.SystemLingo;
import com.foretees.common.ProcessConstants;
import com.foretees.common.htmlTags;
import com.foretees.common.Connect;
import com.foretees.common.reqUtil;
import com.foretees.common.timeUtil;
import org.joda.time.DateTimeZone;

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

   // Check if we will only be return json data
   //boolean json_mode = (req.getParameter("json_mode")) != null;
     
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
   
   // Set the proper type for the content we'll be sending
   //if(json_mode){
   //    resp.setContentType("application/json");
   //}else{
       resp.setContentType("text/html");  
   //}
   PrintWriter out = resp.getWriter();

   //String omit = "";

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   Connection con = Connect.getCon(req);            // get DB connection
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   if (con == null) {
       errorPageTop(out, session, req, con, "DB Connection Error");
       out.println("  <br /><br /><h3 class=\"lesson_ctr\">Database Connection Error</h3>");
       out.println("  <p class=\"lesson_ctr\"><br />Unable to connect to the Database.");
       out.println("    <br />Please try again later.");
       out.println("    <br /><br />If problem persists, contact customer support.");
       out.println("    <br /><br />");
       out.println("  <br /></p>");
       Common_skin.outputPageEnd(club, sess_activity_id, out, req);
       out.close();
       return;
   }
   
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   
   sysLingo.setLingo("Lesson Book", club, sess_activity_id);


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

      promptType(req, resp, out, session, con, proid, sysLingo);   // no
      return;
   }

   //
   //  Process according to the type of request
   //
   if (req.getParameter("reqtime") != null) {           // request a lesson time from lesson book?

      reqTime(req, resp, out, session, con, proid, sysLingo);
      if (req.getParameter("cancel") == null || reqUtil.getParameterInteger(req, "index", 0) == 999) {
          return;
      }
      
   }

   if (req.getParameter("reqtime2") != null) {         // update a lesson time from lesson book (submit)?

      reqTime2(req, resp, out, session, con, proid, sysLingo);
      return;
   }

                                                       // Fall through if none of the above !!!!!!!!!!!!


   //**************************************************************************************
   // We fall through to the 'Display Available Times' (based on lesson type and pro)
   //**************************************************************************************
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   //PreparedStatement pstmt2 = null;
   //ResultSet rs2 = null;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //
   //  Num of days in each month
   //
   //int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

   //
   //  Num of days in Feb indexed by year starting with 2000 - 2040
   //
   //int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
   //                         28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

   String calDate = "";
   String temp = "";
   String dayName = "";
   String proName = "";
   String monthName = "";
   //String stime = "";

   String [] lgroupA = new String [10];       // Group Lessons - max 10 group lessons per day
   String [] lgcolorA = new String [10];
   //int [] stimeA = new int [10];
   //int [] etimeA = new int [10];
   //int [] lgmaxA = new int [10];
   //int [] lgnumA = new int [10];
   //int [] lgdoneA = new int [10];

   //String ltname = "";
   String ltype = "";
   String lgname = "";
   //String lbname = "";
   //String lbetime = "";
   //String frags = "";
   //String color = "";
   //String bgcolor = "";
   //String lgcolor = "";
   //String lbcolor = "";
   String memname = "";
   //String memid = "";
   //String phone1 = "";
   //String phone2 = "";
   //String notes = "";
   String ampm = "";
   //String submit = "";
   //String activity_name = "";

   char flag = 'b';

   int id = 0;
   int i = 0;
   int i2 = 0;
   int count = 0;
   int availCount = 0;
   //int skip = 0;
   //int fragment = 0;
   //int smonth = 0;                // init integer date/time values
   //int sday = 0;
   //int syear = 0;
   //int emonth = 0;
   //int eday = 0;
   //int eyear = 0;
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
   //int sbtime = 0;
   //int ebtime = 0;
   int block = 0;
   int num = 0;
   int length = 0;
   int last_length = 0;
   //int lgmax = 0;
   //int lgnum = 0;
   int in_use = 0;
   int advhrs = 0;
   int index = 0;
   int endTime = 0;
   //int lesson_id = 0;
   //int sheet_activity_id = 0;

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
   Integer idate = reqUtil.getParameterInteger(req, "date", null);
   if(idate != null){
       calDate = timeUtil.getStringDateMDYYYY(idate);
       int[] dateParts = timeUtil.parseIntDate(idate);
       year = dateParts[timeUtil.YEAR];
       month = dateParts[timeUtil.MONTH];
       day = dateParts[timeUtil.DAY];
       day_num = timeUtil.getDayNumOfWeek(idate);        // day of week (01 - 07)
 
   } else if (req.getParameter("calDate") != null) {       // if a return

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

          errorPageTop(out, session, req, con, "Procedure Error");
          out.println("  <br /><br /><h3 class=\"lesson_ctr\">Access Procedure Error</h3>");
          out.println("  <p class=\"lesson_ctr\"><br />Required Parameter is Missing - Member_sheet.");
          out.println("    <br />Please exit and try again.");
          out.println("    <br /><br />If problem persists, report this error to your " + (sess_activity_id > 0 ? "pro" : "golf") + " shop staff.");
          out.println("  <br /></p>");
          Common_skin.outputPageEnd(club, sess_activity_id, out, req);

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

         StringBuilder pro_name = new StringBuilder(rs.getString("fname"));  // get first name

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

   } catch (Exception ignore) {

   } finally {

       try { rs.close(); }
       catch (SQLException ignored) {}

       try { pstmt.close(); }
       catch (SQLException ignored) {}
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

   } catch (Exception exc) {

      dbError(req, out, exc, caller);
      return;
      
   } finally {

       try { rs.close(); }
       catch (SQLException ignored) {}

       try { pstmt.close(); }
       catch (SQLException ignored) {}
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

   } catch (Exception exc) {

      dbError(req, out, exc, caller);
      return;

   } finally {

       try { rs.close(); }
       catch (SQLException ignored) {}

       try { pstmt.close(); }
       catch (SQLException ignored) {}
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

      } catch (Exception exc) {

         dbError(req, out, exc, caller);
         return;

      } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}
      }

      // If Charlotte CC, add 30 minutes to the lesson length since they want us to block a 30 minute buffer time after each lesson.
      // Adding 30 mins will prevent members from booking a lesson that butts right up to an existing lesson later in the day.
      if (club.equals("charlottecc") && sess_activity_id == 0) {
          etime += 30;
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

     //  Build the page to display the available times
     Common_skin.outputHeader(club, sess_activity_id, "Member " + sysLingo.TEXT_Lesson + " Book", true, out, req);
     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);

     //**********************************************************
     //  Continue with instructions and tee sheet
     //**********************************************************
     //
     if (availCount > 0) {      // if any available times

         Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Available " + sysLingo.TEXT_Lesson + " Times", req);
         Common_skin.outputLogo(club, sess_activity_id, out, req);

         out.println("  <div class=\"main_instructions\">");

         out.println("    <b>Instructions:</b>");
         out.println("    &nbsp;&nbsp;&nbsp;&nbsp;Select one of the following available times. ");
         out.println("  </div>");

         if (club.equals("pinehurstcountryclub") && sess_activity_id == 1) {
             out.println("  <div class=\"main_instructions\" style=\"background-color:#FBF8E8;\">");

             out.println("    <b>Notice:</b>");
             out.println("    &nbsp;&nbsp;&nbsp;&nbsp;Please call the tennis shop to book a private lesson: (303) 985-3240. "
                     + "<br>To sign up for Adult Tennis Drills and/or Junior Clinics, select the \"Group Lesson\" option.");
             out.println("  </div>");
         }

         if (club.equals("interlachenspa")) {
             out.println("  <p class=\"lesson_big_ctr\">Spa Service:&nbsp;&nbsp;<b>" + ltype + "</b>");
             out.println("    <br /><br />");
             out.println("    Available Times For&nbsp;&nbsp;<b>" + proName + "</b>");
             out.println("    <span class=\"rwdHide\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class=\"ft-textGroup\">Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></span></p>");
         } else {
             out.println("  <p class=\"lesson_big_ctr\">" + sysLingo.TEXT_Lesson + " Type:&nbsp;&nbsp;<b>" + ltype + "</b>&nbsp;&nbsp;&nbsp;&nbsp;Length (minutes):&nbsp;&nbsp;<b>" + length + "</b>");
             out.println("    <br /><br />");
             out.println("    Available " + sysLingo.TEXT_Individual_Lesson + " Times For&nbsp;&nbsp;<b>" + proName + "</b>");
             out.println("    <span class=\"rwdHide\">&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;</span><span class=\"ft-textGroup\"><b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></span></p>");
         }

         out.println("  <table class=\"lesson_time\">");
         out.println("    <tbody>");

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

                     out.println("    <tr>");
                     out.println("      <td class=\"hour\">" + hr + " " + ampm + "</td>");
                     out.println("    </tr>");
                     oldhr = hr;
                 }

                 out.println("    <tr><td>");

                 if (club.equals("pinehurstcountryclub") && sess_activity_id == 1) {
                     out.println("      " + hr + ":" + Utilities.ensureDoubleDigit(min) + ampm);
                 } else {
                     out.println("      <a class=\"lesson_choice\" href=\"Member_lesson?proid=" + id + "&calDate=" + calDate + "&date=" + date + "&time=" + time + "&ltype=" + ltype + "&day=" + dayName + "&lesson_id=" + lesson_idA[i] + "&reqtime=yes\">");
                     out.println(hr + ":" + Utilities.ensureDoubleDigit(min) + ampm + "</a>");
                 }

                 out.println("      </td>");
                 out.println("    </tr>");
             }
         } // for (lesson Times)

         //
         //  End of Lesson Book table
         //
         out.println("    </tbody>");
         out.println("  </table>    <!-- lesson_time -->");     // end of table

     } else {        // none avail

         Common_skin.outputBreadCrumb(club, sess_activity_id, out, "No Available " + sysLingo.TEXT_Lesson + " Times", req);
         Common_skin.outputLogo(club, sess_activity_id, out, req);

         out.println("  <br />");

         if (club.equals("interlachenspa")) {

             out.println("  <p class=\"lesson_big_ctr\">Available Times For&nbsp;&nbsp;<b>" + proName + "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
             out.println("    <br /><br />");
             out.println("    Sorry, there are no times available for this date for the service you selected.<br />");
             if (earlyTimes == false) {
                 out.println("    Please try another date.</font><br><br></p>");
             } else {
                 out.println("    However, there are some times available that cannot be scheduled online at this time.<br />");
                 out.println("    Please contact the Spa Scheduler if you wish to schedule a service for this date.</font><br /><br /></p>");
             }

         } else {

             out.println("  <p class=\"lesson_big_ctr\">Available " + sysLingo.TEXT_Lesson + " Times For&nbsp;&nbsp;<b>" + proName + "</b>&nbsp;&nbsp;&nbsp;&nbsp;Date:&nbsp;&nbsp;<b>" + dayName + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");
             out.println("    <br /><br />");
             if (earlyTimes == false) {
                 out.println("    Sorry, there are no times available for this date for the " + sysLingo.TEXT_lesson + " type you selected.<br>");
                 out.println("    Please try another " + sysLingo.TEXT_Lesson_Pro + " or another date.<br /><br ></p>");
             } else {
                 // out.println("However, there are some times available that cannot be scheduled online at this time.<br>");
                 if (club.equals("deserthighlands")) {
                     out.println("    Please contact the Golf Shop if you wish to schedule a " + sysLingo.TEXT_lesson + " for this date (480.585.8521).<br /><br /></p>");
                 } else {
                     String lingoAn = "a";
                     if (sysLingo.TEXT_lesson.startsWith("a") || sysLingo.TEXT_lesson.startsWith("e") || sysLingo.TEXT_lesson.startsWith("i")
                             || sysLingo.TEXT_lesson.startsWith("o") || sysLingo.TEXT_lesson.startsWith("u")) {
                         lingoAn = "an";
                     }
                     if (sess_activity_id > 0) {
                         out.println("    Please contact the Pro Shop if you wish to schedule " + lingoAn + " " + sysLingo.TEXT_lesson + " for this date.<br /><br /></p>");
                     } else {
                         out.println("    Please contact the Golf Shop if you wish to schedule " + lingoAn + " " + sysLingo.TEXT_lesson + " for this date.<br /><br /></p>");
                     }
                 }
             }
         }
     }

     //
     //  End of HTML page
     //
     out.println("  <div class=\"lesson_return_2nd\">");
     out.println("    <a class=\"standard_button\" href=\"Member_lesson?proid=" + id + "\">Go Back</a>");
     out.println("  </div> ");
     Common_skin.outputPageEnd(club, sess_activity_id, out, req);

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
   String user = (String)session.getAttribute("user");               // get username
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   String proid = "";
   String proName = "";
   //String lname = "";
   //String fname = "";
   String mi = "";
   String suffix = "";
   //String temp = "";
   int id = 0;
   int count = 0;
   boolean found = false;

   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   
   sysLingo.setLingo("Lesson Book", club, sess_activity_id);


   //
   //  If club does not want members to use the lesson book - inform the user
   //
   // if (club.equals("championhills") || club.equals("wellesley")) {      // allow Wellesley for the summer
   if (club.equals("championhills") || club.equals("avonoakscc") 
           || (sess_activity_id == 0 && (club.equals("bellemeadecc"))) 
           || (sess_activity_id == 1 && (club.equals("cranecreek") || club.equals("pointlake") || club.equals("hillcrestid")))) {

      String custMsg = "";

      if (club.equals("championhills")) {
          custMsg = "Please call the Golf Shop to schedule a lesson with the Professional Staff. (828) 693-3600.";
      } else if (club.equals("wellesley")) {
          custMsg = "Winter indoor golf lessons with Jeff Phillips, Sherry Makerney, Todd Anzlovar and Mike Bowers can be scheduled by emailing them directly.";
      } else if (club.equals("cranecreek")) {
          custMsg = "Please call the Tennis Shop at 514-4361 to schedule a lesson or to reserve the Ball Machine.";
      } else if (club.equals("pointlake")) {
          custMsg = "Please call the Tennis Shop at 704-499-7300 ext. 5 to book a lesson.";
      } else if (club.equals("avonoakscc")) {
          custMsg = "Please call the Avon Oaks Golf Shop at 440-871-4638 to schedule a Golf Lesson with your Instructor of Choice!!";
      } else if (club.equals("hillcrestid")) {
          custMsg = "Please call the Tennis Shop at 208-345-9373 to schedule a lesson.";
      } else if (sess_activity_id == 1) {
          custMsg = "Please call the Tennis Shop to schedule a lesson with the Professional Staff.";
      } else {
          custMsg = "Please call the Golf Shop to schedule a lesson with the Professional Staff.";
      }

      Common_skin.outputHeader(club, sess_activity_id, "Lessons Unavailable", true, out, req);
      Common_skin.outputBody(club, sess_activity_id, out, req);
      Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
      Common_skin.outputBanner(club, sess_activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
      Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
      Common_skin.outputPageStart(club, sess_activity_id, out, req);
      Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Lessons Unavailable", req);
      Common_skin.outputLogo(club, sess_activity_id, out, req);

      out.println("  <br /><h3 class=\"lesson_ctr\">Online Scheduling Currently Unavailable</h3>");
      out.println("  <p class=\"lesson_ctr\"><br />Sorry, the online lesson scheduler is not available for your club at this time.");
      
      out.println("    <br /><br />" + custMsg);
      out.println("  </p>");
      Common_skin.outputPageEnd(club, sess_activity_id, out, req);
      
      out.close();

      proid = "";
      return(proid);            // return none
   } // end custom for clubs that don't want members to see the lesson book

   // Display a link to Appointment Plus for The Country Club - Brookline
   if (club.equals("tcclub")) {

      String mem_username = "";
      String mem_password = "";
      String mem_fname = "";
      String mem_lname = "";
      String mem_email = "";

      try {

          pstmt = con.prepareStatement("SELECT username, password, name_first, name_last, email FROM member2b WHERE username = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user);

          rs = pstmt.executeQuery();

          if (rs.next()) {
              
              mem_username = rs.getString("username");
              mem_password = rs.getString("password");
              mem_fname = rs.getString("name_first");
              mem_lname = rs.getString("name_last");
              mem_email = rs.getString("email");

                Common_skin.outputHeader(club, sess_activity_id, "Member - Appointment Plus Access", true, out, req);
                Common_skin.outputBody(club, sess_activity_id, out, req);
                Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
                Common_skin.outputBanner(club, sess_activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
                Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
                Common_skin.outputPageStart(club, sess_activity_id, out, req);
                Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Appointment Plus", req);
                Common_skin.outputLogo(club, sess_activity_id, out, req);
 
                out.println("  <br /><br /><h3 class=\"lesson_ctr\">Appointment Plus Accesss</h3>");
                out.println("  <p class=\"lesson_ctr\"><br />");
                out.println("  Online lesson scheduling for your club is handled via Appointment Plus.  Please click the link below to open Appointment Plus in a new window.");
                out.println("  <br /></p> <br />");
                out.println("  <div class=\"lesson_ctr\" style=\"margin-left:auto; margin-right:auto; text-align:center;\">");
                out.println("  <form method=\"post\" action=\"https://booknow.appointment-plus.com/43m0g63c/\" target=\"_blank\">");
                out.println("    <input type=\"hidden\" name=\"first_name\" value=\"" + mem_fname + "\">");
                out.println("    <input type=\"hidden\" name=\"last_name\" value=\"" + mem_lname + "\">");
                out.println("    <input type=\"hidden\" name=\"loginname\" value=\"" + mem_username + "\">");
                out.println("    <input type=\"hidden\" name=\"password\" value=\"" + mem_password + "\">");
                out.println("    <input type=\"hidden\" name=\"email\" value=\"" + mem_email + "\">");
                out.println("    <input type=\"hidden\" name=\"action\" value=\"log_in\">");
                out.println("    <input type=\"hidden\" name=\"cof\" value=\"yes\">");
                out.println("    <input type=\"submit\" value=\"Open Appointment Plus\">");
                out.println("  </form></div>");
                out.println("  <p class=\"lesson_ctr\"><br />");
                out.println("    Should you encounter any difficulties while using the Appointment Plus system, please contact the " + (sess_activity_id > 0 ? "pro" : "golf") + " shop staff for assistance.");
                out.println("  </p>");                 
                Common_skin.outputPageEnd(club, sess_activity_id, out, req);

          } // end if rs

      } catch (Exception exc) {

          // Pros do not exist yet - inform user to add some
          Common_skin.outputHeader(club, sess_activity_id, "Member - Appointment Plus Error", true, out, req);
          Common_skin.outputBody(club, sess_activity_id, out, req);
          Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
          Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
          Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
          Common_skin.outputPageStart(club, sess_activity_id, out, req);
          Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Appointment Plus", req);
          Common_skin.outputLogo(club, sess_activity_id, out, req);

          out.println("  <br /><br /><h3 class=\"lesson_ctr\">Database Access Error</h3>");
          out.println("  <p class=\"lesson_ctr\"><br />Unable to access the Database.");
          out.println("    <br />Please try again later.");
          out.println("    <br /><br />If problem persists, please contact customer support.");
          Common_skin.outputPageEnd(club, sess_activity_id, out, req);
              
      } finally {

          try { rs.close(); }
          catch (Exception ignore) { }

          try { pstmt.close(); }
          catch (Exception ignore) { }

          out.close();
      }

      proid = "";
      return(proid);            // return none
   } // end if tcclub


   //
   //  Get the existing pro ids and prompt for a selection
   //
   try {
       
       pstmt = con.prepareStatement (
               "SELECT id FROM lessonpro5 WHERE activity_id = ?");
       
       pstmt.clearParameters();
       pstmt.setInt(1, sess_activity_id);
       rs = pstmt.executeQuery();
       
       while (rs.next()) {
           
           id = rs.getInt("id");          // get the proid
           count++;                       // count how many exist
           found = true;                  // found some
       }
       
   } catch (Exception exc) {
       
       found = false;      // not found
       
   } finally {
       Connect.close(rs, pstmt);
   }

   // 
   // If we only found one pro for this activity then just jump to them without prompting
   //
   if (count == 1) return("" + id);
  
   
   //
   //  Process according to the number of ids found
   //
   if (found == false) {        // if none yet

       // Pros do not exist yet - inform user to add some
       errorPageTop(out, session, req, con, "Sequence Error");
       out.println("  <br /><br /><h3 class=\"lesson_ctr\">Online Scheduling Currently Unavailable</h3>");
       out.println("  <p class=\"lesson_ctr\"><br />Sorry, the online scheduler is not available for your club at this time.");
       out.println("    <br /><br />Please contact your " + (sess_activity_id > 0 ? "Pro" : "Golf") + " Shop Staff for assistance.");
       out.println("  <br /></p>");
       Common_skin.outputPageEnd(club, sess_activity_id, out, req);

       out.close();

       proid = "";
       return (proid);            // return none
   }

     //  Build the html page to request the lesson info
     Common_skin.outputHeader(club, sess_activity_id, "Member - Select Pro", true, out, req);
     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);

     if (club.equals("interlachenspa")) {
         Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Spa", req);
     } else {
         if (req.getParameter("group") == null) {
             Common_skin.outputBreadCrumb(club, sess_activity_id, out, sysLingo.TEXT_Individual_Lessons, req);
         } else {
             Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
         }
     }

     Common_skin.outputLogo(club, sess_activity_id, out, req);

     out.println("  <div class=\"main_instructions\">");

     if (club.equals("interlachenspa")) {
         out.println("    <b>Spa Service Selection</b><br />");
         out.println("    <br />To schedule a service, click on the desired Spa Service from the list below.");
     } else {
         out.println("    <b>" + sysLingo.TEXT_Lesson_Pro_Select + "</b><br />");
         String lingoAn = "a";
         if (sysLingo.TEXT_lesson_reservation.startsWith("a") || sysLingo.TEXT_lesson_reservation.startsWith("e") || sysLingo.TEXT_lesson_reservation.startsWith("i")
                 || sysLingo.TEXT_lesson_reservation.startsWith("o") || sysLingo.TEXT_lesson_reservation.startsWith("u")) {
             lingoAn = "an";
         }
         String golfPro = "Golf Professional";
         if (club.equals("kopplinandkuebler")) {
             golfPro = "Search Executive";
         }

         if (sess_activity_id != 0) {
             if (req.getParameter("group") == null) {
                 out.println("    <br />To schedule " + lingoAn + " " + sysLingo.TEXT_lesson_reservation + ", click on the desired Professional from the list below.");
             } else {
                 out.println("    <br />To view available Group Lessons and Clinics, click on the desired Professional from the list below.");
             }
         } else {
             if (req.getParameter("group") == null) {
                 out.println("    <br />To schedule " + lingoAn + " " + sysLingo.TEXT_lesson_reservation + ", click on the desired " + golfPro + " from the list below.");
             } else {
                 out.println("    <br />To view available Group Lessons, click on the desired Golf Professional from the list below.");
             }
         } // else, if (activity_id != 0)
     }
     out.println("  </div>");    // main instructions.

     out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"pform\">");

     if (req.getParameter("group") != null) {

         out.println("  <input type=\"hidden\" name=\"group\" value=\"yes\" />");
     }

     out.println("  <div class=\"lesson_select\">");

     if (club.equals("interlachenspa")) {
         out.println("    <b>Spa Service:</b>&nbsp;&nbsp;");
     } else {
         out.println("    <b>" + sysLingo.TEXT_Lesson_Pro + ":</b>&nbsp;&nbsp;");
     }
     out.println("    <select size=\"1\" name=\"proid\" onChange=\"document.pform.submit()\">");
     out.println("    <option value=\"0\">Select One</option>");

   try {

      if (club.equals( "interlachen" )) {      // if Interlachen, list Kevin first

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY fname DESC");

      } else if (club.equals("virginiacc")) {
         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY fname");
      } else {
         pstmt = con.prepareStatement (
                 "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY sort_by");
      }

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, sess_activity_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         StringBuilder pro_name = new StringBuilder(rs.getString("fname"));  // get first name

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
         
         // Pinehurst CC (pinehurstcountryclub) - Hide specific lesson pros when viewing individual or group lessons
         if (club.equals("pinehurstcountryclub") && sess_activity_id == 1 && 
                 ((req.getParameter("group") == null && (proName.equalsIgnoreCase("Adult Drill_Classes") || proName.equalsIgnoreCase("Junior Clinics"))) || 
                 (req.getParameter("group") != null && !proName.equalsIgnoreCase("Adult Drill_Classes") && !proName.equalsIgnoreCase("Junior Clinics")))) {
             
             continue;
         } else if (club.equals("interlachen") && id == 2) {    // Don't display Kevin Lucken in list
             continue;
         }
         
         out.println("    <option value=\"" + id + "\">" + proName + "</option>");
      }

       out.println("    </select>");
       out.println("  </div>    <!-- lesson_select  -->");
       out.println("  </form>");

   } catch (Exception exc) {
       out.println("  </form>");
       out.println("  <br /><br />Unable to access the Database.");
       out.println("  <br />Please try again later.");
       out.println("  <br /><br />" + exc.getMessage());
   } finally {

      try { rs.close(); }
      catch (Exception ignore) { }

      try { pstmt.close(); }
      catch (Exception ignore) { }

   }

     Common_skin.outputPageEnd(club, sess_activity_id, out, req);

     out.close();

     proid = "";           // return none so we know user was prompted

     return (proid);
 }


 // ********************************************************************
 //  Prompt user for the Lesson Type and Date
 // ********************************************************************

 private void promptType(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                         Connection con, String proid, SystemLingo sysLingo)
              throws ServletException, IOException {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String caller = (String)session.getAttribute("caller");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   htmlTags tags = new htmlTags(rwd);
   
   String proName = "";
   String ltname = "";
   //String fname = "";
   String mi = "";
   String suffix = "";
   String cost = "";
   String desc = "";
   //String temp = "";
   String order_by = "";
   
   String record_key = "";

   int id = 0;
   int i = 0;
   int length = 0;
   int count = 0;
   int col = 0;                       // init column counter
   int max = 30;                      // # of days to display in calendar

   long date = 0;
   
   long start_date = 0;
   long end_date = 0;

   //boolean found = false;
      
   Map<String, Map> date_map = new LinkedHashMap<String, Map>();
   Map<String, String> callback_field_map = new LinkedHashMap<String, String>();
   Map<String, Object> calendar_map = new LinkedHashMap<String, Object>();
   
   Gson gson_obj = new Gson();

   try {
      id = Integer.parseInt(proid);              // id of pro
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   //
   //  If Cordillera allow 90 days calendars
   //
   if (club.equals( "cordillera" ) || club.equals( "minneapolis" ) || club.equals( "congressional" ) || club.equals( "mnvalleycc" ) || club.equals("foresthighlands")) {

      max = 90;
   }

   if (club.equals( "minikahda" ) || club.equals( "ncrcc" ) || club.equals( "tcclub" ) || club.equals("paloaltohills")/* || club.equals("lakesclub")*/ || club.equals("thelegendclubs")) {

      max = 60;
   }

   if (club.equals( "cecc" ) || (club.equals("ridgecc") && id == 1)) {     // Columbia-Edgewater CC

      max = 120;
   }

   if (club.equals( "colletonriverclub" )) {     // Colleton River

      max = 180;
   }

   if (club.equals( "snoqualmieridge" )) {  

      max = 3;      // they only use lesson book to reserve a golf simulator/ball machine
   }

   if (club.equals("ballantyne") && sess_activity_id == 1) {
       
      max = 3;
   }
   
   if ((club.equals("sierraviewcc") || club.equals("fortcollins") || club.equals("bellemeadecc") || club.equals("rhillscc")) && sess_activity_id == 1) {
       
      max = 7;
   }
   
   if (club.equals("quakerridgegc") || club.equals("desertforestgolfclub")) {

      max = 6;
   }
   
   if (club.equalsIgnoreCase("richlandcc")) {
       max = 7;
   }
   
   if (club.equalsIgnoreCase("dmgcc")) {
       max = 150;
   }
   
   if (club.equals("westmoor")) {
      max = 8;
   }
   
   if (club.equals("olyclub") || (club.equals("sciotocc") && id == 3) || club.equalsIgnoreCase("uppermontclaircc") || club.equalsIgnoreCase("wilmington")) {
      max = 14;
   }
   
   if (club.equals("hiwan") || (club.equals("mirabel") && sess_activity_id == 0)) {
      max = 365;
   }

   //
   //  If Cherry Hills allow 14 days calendars
   //
   if (club.equals( "cherryhills" ) || club.equals( "royaloakscc" ) || club.equals("charlottecc") || club.equals("waynesborough")) {

      max = 14;
   }

   if (club.equals("roundhill")) {
       max = 45;
   }

   if (club.startsWith("gaa") && !club.endsWith("class")) {
       max = 7;
   }
   
   if (club.startsWith("meadowbrook") && id == 4 ) {
       max = 3;
   }
   
   if (club.equals("foxchapelgolfclub")) {
       max = 1;
   }
   if (club.equals("dellwood")) {
       max = 21;
   }
   if (club.equals("edina") && (sess_activity_id == 1 || sess_activity_id == 12)) {
       max = 60;
   }
   if (club.equals("martiscamp") && sess_activity_id == 10 ) {
       max = 90;
   }
   
          
   //
   //  Array to hold the indicator for each of the next 30 days
   //
   int [] daysA = new int [max];

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };

   //String [] oday_table = { "o", "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th", "9th", "10th", "11th",
   //                        "12th", "13th", "14th", "15th", "16th", "17th", "18th", "19th", "20th",
   //                        "21st", "22nd", "23rd", "24th", "25th", "26th", "27th", "28th", "29th", "30th" };

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

         StringBuilder pro_name = new StringBuilder(rs.getString("fname"));  // get first name

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

   } catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;

   } finally {

      try { rs.close(); }
      catch (Exception ignore) { }

      try { pstmt.close(); }
      catch (Exception ignore) { }

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

     //
     //  Start with today and check for available lesson times with this pro
     //
     // Get starting and ending dates we will be searching for/displaying
     start_date = Utilities.getDate(con);
     end_date = Utilities.getDate(con, max);

     // Store formatted dates for later use
     calendar_map.put("start_date", ("" + start_date).replaceAll("^([0-9]{4})([0-9]{2})([0-9]{2})$", "$2/$3/$1"));
     calendar_map.put("end_date", ("" + end_date).replaceAll("^([0-9]{4})([0-9]{2})([0-9]{2})$", "$2/$3/$1"));
     calendar_map.put("default_status", 0); // default day status for dates missing in map built below (0=not avail, 1=avail) 
     calendar_map.put("callback_url", "Member_lesson");
     calendar_map.put("callback_method", "post");
     calendar_map.put("callback_form", "pform"); // Optional form to collect data from when calling back
     callback_field_map.put("calDate", "[selected_date]"); // Fields to pull from our objected and submit
     calendar_map.put("callback_field_map", callback_field_map);
     calendar_map.put("tooltip_object", "[options.lesson.toolTips]"); // pointer to the tooltip object in javascript
     calendar_map.put("disabled_status_array", new int[]{0});  // List of status codes that will disable the day.
     if (req.getAttribute("calDate") != null) {
         calendar_map.put("selected_date", req.getAttribute("calDate"));
     } else {
         calendar_map.put("selected_date", (String) calendar_map.get("start_date"));
     }

     try {

         // Get dates that have available lessons
         pstmt = con.prepareStatement(
                 "SELECT `date`, (1) as status FROM lessonbook5 "
                 + "WHERE proid = ? "
                 + "AND (`date` BETWEEN ? AND ?) "
                 + "AND block = 0 "
                 + "AND in_use = 0 "
                 + "AND lgname = '' "
                 + "AND memname = '' "
                 + "AND ltype = ''"
                 + "GROUP BY `date`");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, id);
         pstmt.setLong(2, start_date);
         pstmt.setLong(3, end_date);
         rs = pstmt.executeQuery();      // execute the prepared pstmt



         // Build a map of the dates to send to javascript
         while (rs.next()) {

             // Reformat date from YYYYMMDD to MM/DD/YYYY for use as key
             record_key = ("" + rs.getInt("date")).replaceAll("^([0-9]{4})([0-9]{2})([0-9]{2})$", "$2/$3/$1");

             Map<String, Object> dateItem = new LinkedHashMap<String, Object>();
             dateItem.put("status", rs.getInt("status"));
             //dateItem.put("date", record_key);
             //dateItem.put("tooltip", "This is a tooltip for "+record_key );
             //dateItem.put("proid", id);
             date_map.put(record_key, dateItem);


         }

         //
         //  Get next day
         //
         cal.add(Calendar.DATE, 1);
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) + 1;
         day = cal.get(Calendar.DAY_OF_MONTH);

         date = (year * 10000) + (month * 100) + day;

     } catch (Exception e1) {

         //throw new RuntimeException(e1);
         dbError(req, out, e1, caller);
         return;

     } finally {

         try {
             rs.close();
         } catch (Exception ignore) {
         }

         try {
             pstmt.close();
         } catch (Exception ignore) {
         }

     }
     // Store the day map for later use
     calendar_map.put("date_map", date_map);

     boolean showDescription = (!club.equals("ballantyne") || sess_activity_id != 1);

     
   String lingoAn = "a";
   if (sysLingo.TEXT_lesson.startsWith("a") || sysLingo.TEXT_lesson.startsWith("e") || sysLingo.TEXT_lesson.startsWith("i") || 
         sysLingo.TEXT_lesson.startsWith("o") || sysLingo.TEXT_lesson.startsWith("u")) {
      lingoAn = "an";
   }
     
     //
     //  Build the html page to request the lesson type and date
     //
     Common_skin.outputHeader(club, sess_activity_id, "Member - Select " + sysLingo.TEXT_Lesson + " Type", false, out, req);
     out.println("<script type=\"text/javascript\">");            // Check for lesson selected.
     out.println("<!--");
     out.println("function lessonSelected() {");
     out.println("  f = document.forms[\"pform\"];");
     out.println("  if (f.lsnselected.value == 0) {");
     out.println("    alert(\"Please select " + lingoAn + " " + sysLingo.TEXT_lesson + " type before clicking date.\");");
     out.println("  }");
     out.println("}");

     out.println("function setLessonSel() {");
     out.println("  f = document.forms[\"pform\"];");
     out.println("  f.lsnselected.value = 1;");
     out.println("}");

     out.println("// -->");
     out.println("</script>");          // End of script
     out.println("</head>");

     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);

   if (club.equals("cherryhills") && (proName.equalsIgnoreCase("John Ogden") || proName.equalsIgnoreCase("Jason Rauzi") ||
       proName.equalsIgnoreCase("Matt Stewart") || proName.equalsIgnoreCase("Jeff Brummett"))) {

       Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Call Pro Shop", req);
       Common_skin.outputLogo(club, sess_activity_id, out, req);
       out.println("  <br /><h3 class=\"lesson_ctr\">Teaching Pro Not Available Online</h3>");
       out.println("  <br /><p class=\"lesson_ctr\">Please call the Golf Shop at 303-350-5220 to schedule a lesson with " + proName + ".</p>");

       out.println("  <p class=\"lesson_ctr\"><br />");
       out.println("    <form class=\"lesson_form\" action=\"Member_lesson\" method=\"post\">");
       out.println("      <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" >");
       out.println("  </form></p>");

       Common_skin.outputPageEnd(club, sess_activity_id, out, req);
       
       out.close();
       return;
   }

     if (club.equals("interlachenspa")) {
         Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Spa Service Selection", req);
     } else {
         Common_skin.outputBreadCrumb(club, sess_activity_id, out, sysLingo.TEXT_Individual_Lessons, req);
     }

     Common_skin.outputLogo(club, sess_activity_id, out, req);

     if (club.equals("columbine") && id == 7) {

         out.println("  <p class=\"lesson_ctr\">To book a lesson with Bryan, please call (303) 794-6333 or email Bryan at <a href=\"mailto:bheim@columbinecountryclub.org\" style=\"color:blue; text-decoration:underline;\">bheim@columbinecountryclub.org</a>.</p>");

         out.println("  <div class=\"lesson_return_top\">");
         out.println("    <form action=\"Member_lesson\" method=\"post\">");
         out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" />");
         out.println("    </form>");
         out.println("  </div>    <!-- lesson_return_top  -->");

         Common_skin.outputPageEnd(club, sess_activity_id, out, req);
         return;

     } else {

         lingoAn = "a";
         if (sysLingo.TEXT_Lesson.startsWith("A") || sysLingo.TEXT_Lesson.startsWith("E") || sysLingo.TEXT_Lesson.startsWith("I")
                 || sysLingo.TEXT_Lesson.startsWith("O") || sysLingo.TEXT_Lesson.startsWith("U")) {
             lingoAn = "an";
         }

         out.println("  <div class=\"main_instructions pageHelp\" data-fthelptitle=\"Instructions\">");

         if (club.equals("interlachenspa")) {
             out.println("    <b>Spa Service Selection</b><br />");
             out.println("    <br />Select a Spa Service from the list below and then click on the day desired.");
         } else {
             out.println("    <b>" + sysLingo.TEXT_Individual_Lesson + " Type Selection</b><br />");
             out.println("    <br />Select " + lingoAn + " " + sysLingo.TEXT_Lesson + " Type from the list below and then click on the day desired.");
         }
         out.println("  </div>");
         if (club.equals("interlachenspa")) {
             out.println("  <p class=\"lesson_ctr\">Spa Services and Dates available for:&nbsp;&nbsp;<b>" + proName + "</b></p>");
         } else {
             out.println("  <p class=\"lesson_ctr\">" + sysLingo.TEXT_Individual_Lesson + " Types and Dates available for:&nbsp;&nbsp;<b>" + proName + "</b></p>");
         }

         out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"pform\">");
         out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\" />");
         out.println("    <input type=\"hidden\" name=lsnselected value=\"0\" />");
     }

   try {

      if (!club.equals( "interlachen" )) {

         if (club.equals("minikahda")) {
             order_by = "length, descript";
         } else {
             order_by = "descript";
         }

          out.println("    <" + tags.table + " class=\"rwdTable standard_list_table rwdCompactible rwdWideData\">");
          out.println("      <" + tags.thead + " class=\"rwdThead\">");//<!--<tr>");
          //out.println("        <th colspan=\"4\">" + sysLingo.TEXT_Individual_Lesson + " Types  (Select One)</th>");
          //out.println("      </tr>-->");
          out.println("      <" + tags.tr + " class=\"rwdTr\">");
          out.println("        <" + tags.td + " class=\"rwdTh individual_lesson_type\">" + sysLingo.TEXT_Individual_Lesson + " Type</" + tags.td + ">");
          if (showDescription && !rwd) {
              out.println("        <" + tags.td + " class=\"rwdTh individual_lesson_desc\">Description</" + tags.td + ">");
          }
          out.println("        <" + tags.td + " class=\"rwdTh\">Minutes</" + tags.td + ">");
          out.println("        <" + tags.td + " class=\"rwdTh\">Price</" + tags.td + ">");
          if (showDescription && rwd) {
              out.println("        <" + tags.td + " class=\"rwdTh individual_lesson_desc\">Description</" + tags.td + ">");
          }
          out.println("      </" + tags.tr + "></" + tags.thead + ">");
          out.println("      <" + tags.tbody + " class=\"rwdTbody\">");

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontype5 WHERE proid = ? ORDER BY " + order_by);

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

            ltname = rs.getString("ltname");
            length = rs.getInt("length");
            cost = rs.getString("cost");
            desc = rs.getString("descript");
             if ((club.equalsIgnoreCase("sciotocc") && id == 3 && (ltname.equalsIgnoreCase("Non Member 1 Hour") || ltname.equalsIgnoreCase("Non Member 1/2 Hour")))
                     || (club.equals("westchester") && ((id == 5 && ltname.equalsIgnoreCase("Iron Fitting Kelly") 
                     || (id == 6 && (ltname.equalsIgnoreCase("Iron Fitting Barry") || ltname.equalsIgnoreCase("Iron Fitting"))) 
                     || (id == 8 && (ltname.equalsIgnoreCase("Wedge Fit") || ltname.equalsIgnoreCase("Wood Fitting"))))))) {

                 continue;
             }

             out.println("      <" + tags.tr + " class=\"rwdTr\">");
             out.println("        <" + tags.td + " class=\"rwdTd individual_lesson_type sT sRd\">");
             out.println("<label class=\"time_slot lesson_button\"><input type=\"radio\" name=\"ltype\" value=\"" + ltname + "\" onclick=\"setLessonSel()\"  /> " + ltname + "</label></" + tags.td + ">");
             if (showDescription && !rwd) {
                 out.println("        <" + tags.td + " class=\"rwdTd individual_lesson_desc\">" + desc + "</" + tags.td + ">");
             }
             out.println("        <" + tags.td + " class=\"rwdTd sLn\">" + length + "</" + tags.td + ">");
             out.println("        <" + tags.td + " class=\"rwdTd sCst\">" + cost + "</" + tags.td + ">");
             if (showDescription && rwd) {
                 out.println("        <" + tags.td + " class=\"rwdTd sDcr\">" + desc + "</" + tags.td + ">");
             }
             out.println("      </" + tags.tr + ">");

         }

      } else {             // Interlachen - do not display description, minutes or price
           
          out.println("    <" + tags.table + " class=\"rwdTable standard_list_table\">");
          out.println("      <" + tags.thead + " class=\"rwdThead\"><" + tags.tr + " class=\"rwdTr\">");
          out.println("        <" + tags.th + " class=\"rwdTh\">Individual Lesson Types  (Select One)</" + tags.tr + ">");
          out.println("      </" + tags.tr + "><" + tags.tr + " class=\"rwdTr\">");
          out.println("        <" + tags.th + " class=\"rwdTh individual_lesson_type\">Individual Lesson Type</" + tags.tr + ">");
          out.println("      </" + tags.tr + "></" + tags.thead + ">");
          out.println("      <" + tags.tbody + " class=\"rwdTbody\">");

         pstmt = con.prepareStatement (
                 "SELECT * FROM lessontype5 WHERE proid = ? ORDER BY descript");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         while (rs.next()) {

             ltname = rs.getString("ltname");

             out.println("      <" + tags.tr + " class=\"rwdTr\">");
             out.println("        <" + tags.td + " class=\"rwdTd individual_lesson_type\">");
             out.println("        <input type=\"radio\" name=\"ltype\" value=\"" + ltname + "\" /> " + ltname);
             out.println("        </" + tags.td + "></" + tags.tr + ">");   
         }

      }                  // end of IF Interlachen

       out.println("      </" + tags.tbody + ">");
       out.println("    </" + tags.table + ">    <!-- standard_list_table -->");
       out.println("    <br />");

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
      
       //
       //  build an element to hold calendar
       //
       out.println("<div class=\"lesson_calendar_container\">");

       out.println("<div id=\"member_lesson_calendar\" class=\"calendar individual_lesson\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(calendar_map)) + "\"></div>");

       out.println("</div>");
       out.println("</form>");
         

   } catch (Exception exc) {

       out.println("  </form>");
       out.println("  <br /><br />Unable to access the Database.");
       out.println("  <br />Please try again later.");
       out.println("  <br /><br />" + exc.getMessage());

   } finally {

      try { rs.close(); }
      catch (Exception ignore) { }

      try { pstmt.close(); }
      catch (Exception ignore) { }

   }

     out.println("  <div class=\"lesson_return_top\">");
     out.println("    <form action=\"Member_lesson\" method=\"post\">");
     out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" />");
     out.println("    </form>");
     out.println("  </div>    <!-- lesson_return_top  -->");

     Common_skin.outputPageEnd(club, sess_activity_id, out, req);
     
     out.close();
 }
 
 // ********************************************************************
 //  Display calendar top: month name, year, and days.
 // ********************************************************************
 private void displayCalendarTop(PrintWriter out, String mm, int year) {  

   out.println("    <div id=\"sm_calendar2\">");  
   out.println("      <table>");  
   out.println("      <thead><th colspan=\"7\"><b>" +mm+ "&nbsp;&nbsp;" +year+ "</b></th>");  
   out.println("      </thead>");  
   out.println("      <tr>");  
   out.println("        <td>S</td>");  
   out.println("        <td>M</td>");  
   out.println("        <td>T</td>");  
   out.println("        <td>W</td>");  
   out.println("        <td>T</td>");  
   out.println("        <td>F</td>");  
   out.println("        <td>S</td>");  
   out.println("      </tr>");  
 
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
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   htmlTags tags = new htmlTags(rwd);
   
   String proName = "";
   String dayName = "";
   //String monthName = "";
   String lgname = "";
   //String fname = "";
   String mi = "";
   String suffix = "";
   //String temp = "";
   String desc = "";
   String cost = "";
   String ampms = "";
   String ampme = "";
   String days = "";
   String eo_week = "";
   String date_start_disp = "";
   String date_end_disp = "";

   int id = 0;
   //int i = 0;
   int hr = 0;
   int min = 0;
   int max = 0;
   //int time = 0;
   int stime = 0;
   int etime = 0;
   //int length = 0;
   int count = 0;
   int count2 = 0;
   //int col = 0;                       // init column counter
   int lesson_id = 0;
   int clinic = 0;
   int advDays = 180;

   long date = 0;
   long sdate = 0;
   long edate = 0;
   long date_start = 0;
   long date_end = 0;
   long yy = 0;
   long mm = 0;
   long dd = 0;

   //boolean found = false;

   try {
      id = Integer.parseInt(proid);              // id of pro
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   String [] day_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thur", "Fri", "Sat" };

   //  Custom for Golf Academy of America sites to only show group lessons 10 days out.
   if (club.startsWith("gaa") && !club.endsWith("class")) {
       advDays = 10;
   }


   //
   //  Get the Pro's name and lesson types available
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT * FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1,id);
      rs = pstmt.executeQuery();

      if (rs.next()) {

          // build the name
         StringBuilder pro_name = new StringBuilder(rs.getString("fname"));

         mi = rs.getString("mi");
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));

         suffix = rs.getString("suffix");
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();

      }

   } catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;

   } finally {

      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}

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
   cal.add(Calendar.DATE, advDays);                  // roll ahead 180 days (limit for members)
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);

   month++;                                            // month starts at zero

   edate = (year * 10000) + (month * 100) + day;      // end date

     //
     //  Build the html page to request the Group Lesson
     //
     Common_skin.outputHeader(club, sess_activity_id, "Member - Select Pro", true, out, req);
     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);
     Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
     Common_skin.outputLogo(club, sess_activity_id, out, req);

     out.print("<div class=\"main_instructions pageHelp\" data-fthelptitle=\"Instructions\">");

     if (rwd) {
         out.print("<h2 class=\"altTitle\">Instructions:</h2>");
         out.print("<p>Select a Group Lesson from the list below. If the lesson isn't selectable, the lesson is full.</p>");
     } else {
         out.println("    <b>Group Lesson Selection</b><br />");
         out.println("    <br />Select a Group Lesson from the list below.");
     }
     out.print("</div>");
     if (!rwd) {
         out.println("  <p class=\"lesson_ctr\"><br />Group Lessons available for:&nbsp;&nbsp;<b>" + proName + "</b>");
     }
     out.println("  <" + tags.table + " class=\"rwdTable standard_list_table rwdCompactible rwdWideData\">");
     if (rwd) {
         out.print("<" + tags.caption + " class=\"rwdCaption ft-smallCaption\"><span><span>Group Lessons from:</span> <b>" + proName + "</b></span></" + tags.caption + ">");
         out.print("<" + tags.thead + " class=\"rwdThead\">");
     } else {
         out.println("    <thead><tr>");
         out.println("      <th colspan=\"8\">Group Lessons</th>");
         out.println("    </tr>");
     }
     out.println("<" + tags.tr + " class=\"rwdTr\">");
     out.println("      <" + tags.th + " class=\"rwdTh group_lesson_name\">Lesson Name</" + tags.th + ">");
     if (sess_activity_id != 0) {
         out.println("      <" + tags.th + " class=\"rwdTh group_lesson_date\">Dates</" + tags.th + ">");
     } else {
         out.println("      <" + tags.th + " class=\"rwdTh group_lesson_date\">Date</" + tags.th + ">");
     }
     if (rwd) {
         out.println("      <" + tags.th + " class=\"rwdTh group_lesson_time\">Time</" + tags.th + ">");
     } else {
         out.println("      <th class=\"rwdTh group_lesson_start_end\">Start</th>");
         out.println("      <th class=\"rwdTh group_lesson_start_end\">End</th>");
     }
     out.println("      <" + tags.th + " class=\"rwdTh\">Group Size</" + tags.th + ">");
     out.println("      <" + tags.th + " class=\"rwdTh\">Price</" + tags.th + ">");
     out.println("      <" + tags.th + " class=\"rwdTh group_lesson_desc\">Description</" + tags.th + ">");
     if (!rwd) {
         out.println("      <th></th>"); // Status
     }
     out.println("    </" + tags.tr + ">");
     out.println("    </" + tags.thead + ">");
     out.println("    <" + tags.tbody + " class=\"rwdTbody\">");
   
   //
   //  Get any Group Lessons in the next 180 days for this pro
   //
   try {

      if (sess_activity_id != 0) {

          // For non-golf activities, grab any clinics for which today's date falls between their start and end dates
          pstmt = con.prepareStatement (
                  "SELECT *, DATE_FORMAT(date,'%c/%e/%Y') as date_start_disp, DATE_FORMAT(edate,'%c/%e/%Y') as date_end_disp FROM lessongrp5 " +
                  "WHERE proid = ? AND ((date >= ? AND date <= ?) OR (edate >= ? AND edate <= ?) OR (date <= ? AND edate >= ?)) " +
                  "ORDER BY lname");
          pstmt.clearParameters();
          pstmt.setInt(1, id);
          pstmt.setLong(2, sdate);
          pstmt.setLong(3, edate);
          pstmt.setLong(4, sdate);
          pstmt.setLong(5, edate);
          pstmt.setLong(6, sdate);
          pstmt.setLong(7, edate);
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
      String daysBreak = null;
      if(rwd){
          daysBreak = "";    
      } else {
          daysBreak = "<br>";
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
             
             if (rs.getInt("sunday") == 1) days += daysBreak + eo_week + "Sunday";
             if (rs.getInt("monday") == 1) days += daysBreak + eo_week + "Monday";
             if (rs.getInt("tuesday") == 1) days += daysBreak + eo_week + "Tuesday";
             if (rs.getInt("wednesday") == 1) days += daysBreak + eo_week + "Wednesday";
             if (rs.getInt("thursday") == 1) days += daysBreak + eo_week + "Thursday";
             if (rs.getInt("friday") == 1) days += daysBreak + eo_week + "Friday";
             if (rs.getInt("saturday") == 1) days += daysBreak + eo_week + "Saturday";
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
         ampms = hr + ":" + Utilities.ensureDoubleDigit(min) + ampms;

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

         ampme = hr + ":" + Utilities.ensureDoubleDigit(min) + ampme;

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
         }
         
         StringBuilder form = new StringBuilder();
            form.append("    <form action=\"Member_lesson\" method=\"post\" name=\"pform\">");
            form.append("      <input type=\"hidden\" name=\"proid\" value=\"" +id+ "\" />");
            form.append("      <input type=\"hidden\" name=\"lgname\" value=\"" +lgname+ "\" />");
            form.append("      <input type=\"hidden\" name=\"date\" value=\"" +date+ "\" />");
            form.append("      <input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\" />");
            form.append("      <input type=\"hidden\" name=\"groupLesson\" value=\"yes\" />");  // get to lesson signup
            if (sess_activity_id == 0 || date_start == date_end || clinic == 1) {
                // ??
            } else {
                form.append("      <input type=\"hidden\" name=\"selectDate\" value=\"true\" />");
            }
         

          //
          //  Display the group lesson row
          //
          out.println("    <" + tags.tr + " class=\"rwdTr\">");

          if (rwd) {
              // If responsive, make lesson selection more like tee times on Member_sheet and Member_events.
              if ((sess_activity_id == 0 || date_start == date_end || clinic == 1) && count2 >= max) {
                  // Lesson is full
                  out.print("<" + tags.td + " class=\"rwdTd group_lesson_name sT\"><div class=\"time_slot\">" + lgname + "</div></" + tags.td + ">");
              } else {
                  // Not full, or uses date select.  Show button
                  out.print("<" + tags.td + " class=\"rwdTd group_lesson_name sT\">");
                  out.print(form);
                  out.print("<a class=\"standard_button submitForm\" href=\"#\">" + lgname + "</a>");
                  out.print("</form>");
                  out.print("</" + tags.td + ">");
              }

          } else {
              out.println("      <td class=\"group_lesson_name sT\">" + lgname + "</td>");
          }
          out.print("      <" + tags.td + " class=\"rwdTd group_lesson_date sDt\">");
          if (sess_activity_id == 0 || date_start == date_end) {
              out.print(dayName + " " + month + "/" + day + "/" + year);
          } else {
              if (rwd) {
                  out.print("<span class=\"dStart\">" + date_start_disp + "<span> <span>-</span> <span class=\"dEnd\">" + date_end_disp + "<span> <span class=\"ftDays\">" + days + "</span>");
              } else {
                  out.print(date_start_disp + " - " + date_end_disp + days);
              }
          }
          out.print("</" + tags.td + ">");
          if (rwd) {
              out.print("<" + tags.td + " class=\"rwdTd sTm\"><span>" + ampms + "</span> <span> to </span> <span>" + ampme + "</span></" + tags.td + ">");
          } else {
              out.print("<td class=\"group_lesson_start_end\">" + ampms + "</td>");
              out.print("<td class=\"group_lesson_start_end\">" + ampme + "</td>");
          }
          out.print("<" + tags.td + " class=\"rwdTd sGs\">" + max + "</" + tags.td + ">");
          out.print("<" + tags.td + " class=\"rwdTd sCst\">" + cost + "</" + tags.td + ">");
          out.print("<" + tags.td + " class=\"rwdTd group_lesson_desc sP sDcr\">" + desc + "</" + tags.td + ">");
          if (!rwd) {
              out.println("<td>");
              out.print(form);
              if (sess_activity_id == 0 || date_start == date_end || clinic == 1) {
                  if (count2 < max) {                                            // if room for more
                      out.println("      <input class=\"standard_button\" type=\"submit\" value=\"Sign Up\" />");
                  } else {
                      out.println("      Full");
                  }
              } else {
                  out.println("      <input class=\"standard_button\" type=\"submit\" name=\"selectDate\" value=\"Select Date\" />");
              }
              out.print("</form>");
              out.println("      </td>");

          }

          out.println("</" + tags.tr + ">");

          count++;
      }

       out.print("</" + tags.tbody + ">");
       out.println("  </" + tags.table + ">    <!-- standard_list_table -->");
       if (count == 0) {       // if none found
           if (!rwd) {
               out.println("  <br />");
           }
           out.println("  <p class=\"lesson_ctr\">Sorry, no Group Lessons were found for this pro during the next " + advDays + " days.</p>");
       }

   } catch (Exception exc) {

       out.println("  <br /><br />Unable to access the Database.");
       out.println("  <br />Please try again later.");
       out.println("  <br /><br />" + exc.getMessage());   

   } finally {

      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}

      if(rs2 != null){
        try { rs2.close(); }
        catch (SQLException ignored) {}
      }
      
      if (pstmt2 != null) {
        try { pstmt2.close(); }
        catch (SQLException ignored) {}
      }

   }

     out.println("  <div class=\"lesson_return_top\">");
     out.println("    <form action=\"Member_lesson\" method=\"post\">");
     out.println("      <input type=\"hidden\" name=\"group\" value=\"yes\" />");   // go prompt for lesson pro
     out.println("      <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" />");
     out.println("    </form>");
     out.println("  </div>");
     Common_skin.outputPageEnd(club, sess_activity_id, out, req);

     out.close();
 }


 // ***************************************************************************
 //  Process the 'Request Lesson Time' from the Lesson Book or My Tee Times
 // ***************************************************************************

 private void reqTime(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid, SystemLingo sysLingo) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   ResultSet rs = null;


   // Define parms - set defaults
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get username
   String caller = (String)session.getAttribute("caller");
   String memName = (String)session.getAttribute("name");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);

   String proName = "";
   String phone1 = "";
   String phone2 = "";
   String ph1 = "";
   String ph2 = "";
   String notes = "";
   String index = "";
   String ampm = "AM";
   String activity_name = "";

   int id = 0;
   int in_use = 0;
   int hr = 0;
   int min = 0;
   int hr2 = 0;
   int min2 = 0;
   int time = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int month2 = 0;
   int day2 = 0;
   int year2 = 0;
   int cancel_hrs = 0;
   int cancel_time = 0;
   int lesson_id = 0;
   int activity_id = 0;
   int sheet_activity_id = 0;
   int autoblock_times = 0;

   long date = 0;
   long cancel_date = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean editLesson = false;

   List<Integer> sheet_ids = new ArrayList<Integer>();
   Map<Integer, List<Integer>> sheet_id_map = new LinkedHashMap<Integer, List<Integer>>();

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
   String times = req.getParameter("time");           // time requested
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
   
   String dayName = timeUtil.getDayOfWeek((int)date);
   String calDate = timeUtil.getStringDateMDYYYY((int)date);
   
   long cur_date = Utilities.getDate(con);
   int cur_time = Utilities.getTime(con);
   
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
       if(index.equals("999")){
           cancel(id, time, date, calDate, ltype, out, session, req, caller, con);       // process cancel request
       } else {
           cancel(id, time, date, calDate, ltype, null, session, req, caller, con);       // process cancel request
       }
      
      return;
   }


   //******************************************************************
   //  Request is to edit a specific Lesson Time
   //******************************************************************
   
   if (req.getParameter("returnToSlot") == null) {
       //   Check if this day is in use, if not, set it
       try {

          in_use = verifyLesson.checkInUse(club, date, time, id, lesson_id, activity_id, ltype, user, con);

       } catch (Exception e1) {

           errorPageTop(out, session, req, con, "DB Error");
           out.println("  <br /><br /><h3 class=\"lesson_ctr\">Database Access Error</h3>");
           out.println("  <p class=\"lesson_ctr\"><br />Unable to access the Database.");
           out.println("    <br />Please try again later.");
           out.println("    <br /><br />If problem persists, please contact customer support.");
           out.println("    <br /><br />" + e1.getMessage());
           out.println("  <br /></p>");

           errorPageForm(id, calDate, ltype, out, session, req);
           Common_skin.outputPageEnd(club, sess_activity_id, out, req);

           out.close();
           return;
       }

       if (in_use != 0) {              // if time slot already in use

          errorPageTop(out, session, req, con, "DB Record In Use Error");
          out.println("  <br /><br /><h3 class=\"lesson_ctr\">Time Slot Busy</h3>");
          out.println("  <p class=\"lesson_ctr\"><br />Sorry, but this time slot is currently busy.");
          out.println("    <br />Please select another time or try again later.");
          out.println("  <br /></p>");

          errorPageForm(id, calDate, ltype, out, session, req);
          Common_skin.outputPageEnd(club, sess_activity_id, out, req);    

          out.close();
          return;
       }
   }

   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname, fname, mi, suffix, canlimit FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         StringBuilder pro_name = new StringBuilder(rs.getString("fname"));

         String mi = rs.getString("mi");
         if (!mi.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(mi);
         }
         pro_name.append(" " + rs.getString("lname"));

         String suffix = rs.getString("suffix");
         if (!suffix.equals( "" )) {
            pro_name.append(" ");
            pro_name.append(suffix);
         }

         proName = pro_name.toString();

         cancel_hrs = rs.getInt("canlimit");                    // get hours in advance for cancel res
      }

   } catch (Exception ignore) {

   } finally {

      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}
   }

   //
   //  Get today's date to see if this is today
   //
   Calendar cal2 = new GregorianCalendar();
   
   cal2.set((int)yy, ((int)mm-1), (int)dd, hr, min);    // Set calendar object to the date and time of this lesson
   cal2.add(Calendar.HOUR_OF_DAY, (cancel_hrs * -1));    // Roll back calendar a number of hours, equal to the cancelation limit.
   year2 = cal2.get(Calendar.YEAR);
   month2 = cal2.get(Calendar.MONTH) + 1;
   day2 = cal2.get(Calendar.DAY_OF_MONTH);
   hr2 = cal2.get(Calendar.HOUR_OF_DAY);
   min2 = cal2.get(Calendar.MINUTE);

   cancel_date = (year2 * 10000) + (month2 * 100) + day2;    // Date which equals the latest day they can cancel this lesson
   cancel_time = (hr2 * 100) + min2;     // Time which equals the time after which they can no longer cancel on cancel_date
   

   //
   //  Get the current lesson book info for the time requested
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT memname, phone1, phone2, notes, sheet_activity_id FROM lessonbook5 WHERE recid = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         phone1 = rs.getString("phone1");
         phone2 = rs.getString("phone2");
         notes = rs.getString("notes");
         sheet_activity_id = rs.getInt("sheet_activity_id");
         
         if (!rs.getString("memname").equals("")) {
             editLesson = true;
         }

      } else {
         
          nfError_ns(req, out, caller, con, sess_activity_id, club);            // LT not found

          return;
      }
      
      if (req.getParameter("returnToSlot") != null) {
          phone1 = req.getParameter("phone1");
          phone2 = req.getParameter("phone2");
          notes = req.getParameter("notes");
      }

      if (sheet_activity_id != 0) {
          activity_name = getActivity.getActivityName(sheet_activity_id, con);
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

      if (phone1.equals( "" ) && !ph1.equals( "" )) {

         phone1 = ph1;
      }

      if (phone2.equals( "" ) && !ph2.equals( "" )) {

         phone2 = ph2;
      }

   } catch (Exception exc) {

      dbError(req, out, exc, caller);
      return;

   } finally {

      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}
   }

   //  If in an activity other than golf, Check to see if there is available time on the timesheets to accommodate this lesson booking
   if (activity_id != 0) {

       int ltlength = 0;
       String locations_csv = "";

       // Pull up info regarding the lesson type needed so we can check to see if there are available times
       try {

           pstmt = con.prepareStatement(
                   "SELECT length, locations, autoblock_times FROM lessontype5 " +
                   "WHERE proid = ? AND activity_id = ? AND ltname = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, id);
           pstmt.setInt(2, activity_id);
           pstmt.setString(3, ltype);
           rs = pstmt.executeQuery();

           if (rs.next()) {
               ltlength = rs.getInt("length");
               locations_csv = rs.getString("locations");
               autoblock_times = rs.getInt("autoblock_times");
           }

       } catch (Exception exc) {

           dbError(req, out, exc, caller);
           return;

       } finally {

          try { rs.close(); }
          catch (SQLException ignored) {}

          try { pstmt.close(); }
          catch (SQLException ignored) {}
       }
       
       if (autoblock_times == 1 && !editLesson) {

           int time_start = time;
           int time_end = getEndTime(time, ltlength);

           boolean bookAllSheets = false;
           boolean autoSelectLocation = true;

           sheet_id_map = verifyLesson.checkActivityTimes(lesson_id, activity_id, locations_csv, date, date, new int[1], time_start, time_end, bookAllSheets, autoSelectLocation, con, out);

           if (sheet_id_map.get(0) != null) {
               sheet_ids = sheet_id_map.get(0);
           }

           // If no time slots returned, send the user back to the lesson book
           if (sheet_ids.size() == 0) {
               
               errorPageTop(out, session, req, con, "DB Record In Use Error");
               out.println("  <br /><br /><h3 class=\"lesson_ctr\">No Times Available</h3>");
               out.println("  <p class=\"lesson_ctr\"><br />Sorry, no available time slots were found for this " + sysLingo.TEXT_lesson + ".");
               out.println("    <br /><br />Please select a different time from the " + sysLingo.TEXT_lesson + " book.");
               out.println("  <br /></p>");
               out.println("  <div class=\"lesson_return_top\">");
               out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"can\">");
               out.println("    <input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
               out.println("    <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\">");
               out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
               out.println("    <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
               out.println("    <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
               out.println("    <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
               out.println("    <input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("    <input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("    <input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("    <input type=\"hidden\" name=\"day\" value=\"" + dayName + "\">");
               out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" name=\"cancel\">");
               out.println("  </form>");
               out.println("  </div>");
               Common_skin.outputPageEnd(club, sess_activity_id, out, req);

               out.close();
               return;
           }
       }
   }

     //  Output a page to prompt for lesson time info
     Common_skin.outputHeader(club, sess_activity_id, "Member " + sysLingo.TEXT_Lesson + " Book", false, out, req);

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

     //out.println("</head>");   
     //out.println("<body onLoad=\"movenotes()\" >");
     //out.println("<div id=\"top\"></div><div id=\"wrapper\">");
     Common_skin.outputBody(club, sess_activity_id, out, req, " onload=\"movenotes()\"");
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);

     if (club.equals("interlachenspa")) {
         Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Member Spa Service Request", req);
     } else {
         Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Member " + sysLingo.TEXT_Lesson + " Request", req);
     }
     Common_skin.outputLogo(club, sess_activity_id, out, req);

     out.println("  <div class=\"main_instructions\">");
     //out.println("    <b>Warning</b>:<br />");
     out.println("    <p>Complete the reservation below, then select \"Submit\" to complete the request.<br>"
             + "Time remaining: <b class=\"slot_timer\">00:00</b> <a class=\"helpButton\" href=\"#\" data-fthelp=\"time_remaining\" title=\"What is this timer for?\"><span>Help</span></a></p>");
     out.println("  </div>");

     out.println("  <p class=\"lesson_ctr\"><span class=\"ft-textGroup\">Date:&nbsp;&nbsp;<b>" + dayName);
     out.println("    &nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></span>");

     if (club.equals("interlachenspa")) {
         out.println("      <span class=\"rwdHide\">&nbsp;&nbsp;&nbsp;&nbsp</span><span class=\"ft-textGroup\">Time:");
     } else {
         out.println("      <span class=\"rwdHide\">&nbsp;&nbsp;&nbsp;&nbsp</span><span class=\"ft-textGroup\">" + sysLingo.TEXT_Lesson + " Time:");
     }
     out.println("      &nbsp;&nbsp;<b>" + hr + ":" + Utilities.ensureDoubleDigit(min) + " " + ampm + "</b></span></p>");

     out.println("  <div class=\"lesson_req_main\">");
     out.println("    <div class=\"lesson_req_goback\">");

     out.println("      <form action=\"Member_lesson\" method=\"post\" name=\"can\">");
     out.println("      <input type=\"hidden\" name=\"reqtime\" value=\"yes\" />");
     out.println("      <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\" />");
     out.println("      <input type=\"hidden\" name=\"proid\" value=\"" + id + "\" />");
     out.println("      <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\" />");
     out.println("      <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\" />");
     out.println("      <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\" />");
     out.println("      <input type=\"hidden\" name=\"date\" value=\"" + date + "\" />");
     out.println("      <input type=\"hidden\" name=\"index\" value=\"" + index + "\" />");
     out.println("      <input type=\"hidden\" name=\"time\" value=\"" + time + "\" />");
     out.println("      <input type=\"hidden\" name=\"day\" value=\"" + dayName + "\" />");
     out.println("      <input type=\"hidden\" name=\"cancel\" value=\"cancel\" />");
     out.println("      <span>Go Back<br>w/o Changes:<br /></span>");
     out.println("      <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" name=\"cancel\" />");
     out.println("    </form></div>    <!-- lesson_req_goback -->");   // Go Back return

     out.println("    <form action=\"Member_lesson\" method=\"post\" name=\"playerform\">");
     out.println("      <input type=\"hidden\" name=\"reqtime2\" value=\"yes\" />");
     out.println("      <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\" />");
     out.println("      <input type=\"hidden\" name=\"proid\" value=\"" + id + "\" />");
     out.println("      <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\" />");
     out.println("      <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\" />");
     out.println("      <input type=\"hidden\" name=\"date\" value=\"" + date + "\" />");
     out.println("      <input type=\"hidden\" name=\"time\" value=\"" + time + "\" />");
     out.println("      <input type=\"hidden\" name=\"day\" value=\"" + dayName + "\" />");
     out.println("      <input type=\"hidden\" name=\"memname\" value=\"" + memName + "\" />");
     out.println("      <input type=\"hidden\" name=\"index\" value=\"" + index + "\" />");
     out.println("      <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\" />");
     if (editLesson) out.println("<input type=\"hidden\" name=\"editLesson\">");
     out.println("      <div class=\"lesson_req_add_chg_form\">");

     out.println("      <div class=\"lesson_req_add_chg_form_hdr\">");
     if (club.equals("interlachenspa")) {
         out.println("        <b>Add or Change Member Service</b>");
     } else {
         out.println("        <b>Add or Change Member " + sysLingo.TEXT_Lesson_Reservation + "</b>");
     }

     out.println("      </div>");
     out.println("");
     out.println("      <div class=\"lesson_req_add_chg_form_body\">");
     out.println("      <p class=\"lesson_left\">Member:&nbsp;&nbsp;&nbsp;&nbsp;" + memName);

     if (club.equals("interlachenspa")) {
         out.println("      </p><p class=\"lesson_left\">&nbsp;&nbsp;Spa Service:&nbsp;&nbsp;" + proName);
         out.println("      </p><p class=\"lesson_left\">&nbsp;&nbsp;Service Type:&nbsp;&nbsp;" + ltype);
     } else {
         if (!club.equals("ballantyne") || sess_activity_id != 1) {
             out.println("      </p><p class=\"lesson_left\">&nbsp;&nbsp;" + sysLingo.TEXT_Lesson_Pro + ":&nbsp;&nbsp;" + proName);
         }
         out.println("      </p><p class=\"lesson_left\">&nbsp;&nbsp;" + sysLingo.TEXT_Lesson + " Type:&nbsp;&nbsp;" + ltype);
     }
     
     if (sess_activity_id != 0 && !activity_name.equals("")) {
         out.println("      <p class=\"lesson_left\">&nbsp;&nbsp;Location:&nbsp;&nbsp;&nbsp;&nbsp;" + activity_name);
     }
     
     out.println("      </p>");

     out.println("      <p class=\"lesson_left\">&nbsp;&nbsp;Phone 1:&nbsp;&nbsp;");
     out.println("      <input type=\"text\" name=\"phone1\" value=\"" + phone1 + "\" size=\"20\" maxlength=\"24\" />");
     out.println("      &nbsp;&nbsp;<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('phone1')\" style=\"cursor:hand\" />");
     out.println("      </p>");

     out.println("      <p class=\"lesson_left\">&nbsp;&nbsp;Phone 2:&nbsp;&nbsp;");
     out.println("      <input type=\"text\" name=\"phone2\" value=\"" + phone2 + "\" size=\"20\" maxlength=\"24\" />");
     out.println("      &nbsp;&nbsp;<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('phone2')\" style=\"cursor:hand\" />");
     out.println("      </p>");

     //
     //   Notes
     //
     //   Script will put any existing notes in the textarea (value= doesn't work)
     //
     out.println("      <p class=\"lesson_left\"><input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\" />"); // hold notes for script

     out.println("      &nbsp;&nbsp;Notes:&nbsp;&nbsp;");
     out.println("      <textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"24\" rows=\"3\">");
     out.println("      </textarea>");
     out.println("      &nbsp;&nbsp;<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\" />");
     out.println("      </p><br />");
     out.print("      <p class=\"lesson_ctr2\">");
     if (rwd) {
         out.print("<a href=\"#\" class=\"standard_button submitForm\" target=\"form[name=can]\">Go Back</a> ");
     }
     out.print("<input class=\"standard_button\" type=submit value=\"Submit\" name=\"submit\" />");
     out.println("</p>");
     out.println("      </div>    <!-- lesson_req_add_chg_form_body -->");
     out.println("      </div>    <!-- lesson_req_add_chg_form -->");

     if (!index.equals("")) {          // if an edit
         //
         //  If today and within the cancel limit set by pro, then do not allow member to cancel
         //
         out.println("      <div class=\"lesson_cancel\">");
                  
         if (cur_date > cancel_date || (cur_date == cancel_date && cur_time >= cancel_time)) {
             if (club.equals("interlachenspa")) {
                 out.println("      <p>It is too late to cancel this reservation.  Please call the Spa for assistance.</p>");
             } else {   // ok to cancel
                 if (activity_id > 0) {
                     out.println("      <p>It is too late to cancel this " + sysLingo.TEXT_lesson + ".  Please call the Pro Shop for assistance.</p>");
                 } else {
                     out.println("      <p>It is too late to cancel this " + sysLingo.TEXT_lesson + ".  Please call " + sysLingo.TEXT_Golf_Shop + " for assistance.</p>");
                 }
             }
         } else {   // ok to cancel
             if (club.equals("interlachenspa")) {
                 out.println("      <input type=submit class=\"standard_button\" value=\"Cancel Service\" name=\"remove\" />");
             } else {
                 out.println("      <input type=submit class=\"standard_button\" value=\"Cancel " + sysLingo.TEXT_Lesson + "\" name=\"remove\" />");
             }
         }
         out.println("      </div>");
     }

     out.println("    </form>");
     out.println("  </div>    <!-- lesson_req_main -->");

     //
     //  End of HTML page
     //
     Common_skin.outputPageEnd(club, sess_activity_id, out, req);
     out.close();
 }


 // ********************************************************************
 //  Process the 'Update Lesson Time' from reqTime above (submit)
 // ********************************************************************

 private void reqTime2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid, SystemLingo sysLingo) throws ServletException, IOException {

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
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;

   String memid = user;               // get name of user

   String memname2 = "";
   String memnameSave = "";
   String ampm = "AM";
   String locations_csv = "";

   String oldmemname = "";       // old lesson time values
   String oldltype = "";
   String oldphone1 = "";
   String oldphone2 = "";
   String oldnotes = "";
   String proName = "";
   String ltimename = "";
   String activity_name = "";

   int id = 0;
   int ltlength = 0;
   int oldlength = 0;
   int num = 0;
   int billed = 0;
   int in_use = 0;
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
   int autoblock_times = 1;

   long date = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   boolean b = false;
   boolean editLesson = false;
   boolean lessonBooked = false;

   List<Integer> sheet_ids = new ArrayList<Integer>();
   Map<Integer, List<Integer>> sheet_id_map = new LinkedHashMap<Integer, List<Integer>>();

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
   //String calDate = req.getParameter("calDate");  // date of lesson book in String mm/dd/yyyy (for returns)
   String times = req.getParameter("time");       // time requested
   //String dayName = req.getParameter("day");          // name of the day in this book
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
   
   if (req.getParameter("editLesson") != null) editLesson = true;
   
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
   String dayName = timeUtil.getDayOfWeek((int)date);
   String calDate = timeUtil.getStringDateMDYYYY((int)date);

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
   //   Check if still in use (should be)
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT * FROM lessonbook5 WHERE recid = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1, lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         oldmemname = rs.getString("memname");
         oldlength = rs.getInt("length");
         in_use = rs.getInt("in_use");
         oldphone1 = rs.getString("phone1");
         oldphone2 = rs.getString("phone2");
         oldnotes = rs.getString("notes");
         ltimename = rs.getString("ltname");
         sheet_activity_id = rs.getInt("sheet_activity_id");
      }

   } catch (Exception e1) {

       errorPageTop(out, session, req, con, "DB Error");
       out.println("  <br /><br /><h3 class=\"lesson_ctr\">Database Access Error</h3>");
       out.println("  <p class=\"lesson_ctr\"><br />Unable to access the Database.");
       out.println("    <br />Please try again later.");
       out.println("    <br /><br />If problem persists, please contact customer support.");
       out.println("  <br /><br />" + e1.getMessage());
       out.println("  <br /></p>");

       errorPageForm(id, calDate, ltype, out, session, req);
       Common_skin.outputPageEnd(club, sess_activity_id, out, req);

       out.close();
       return;

   } finally {
       
      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}
      
   }

   if (in_use == 0) {              // if slot no longer in use

       errorPageTop(out, session, req, con, "DB Record In Use Error");
       out.println("  <br /><br /><h3 class=\"lesson_ctr\">Reservation Timer Expired</h3>");
       out.println("  <p class=\"lesson_ctr\"><br />Sorry, but this time slot has been returned to the system.");
       out.println("    <br /><br />The system timed out and released the time.");
       out.println("    <br /><br />");
       out.println("    id=" + id + ", date=" + date + ", time=" + time);
       out.println("  <br /></p>");

       errorPageForm(id, calDate, ltype, out, session, req);
       Common_skin.outputPageEnd(club, sess_activity_id, out, req);

       out.close();
       return;
   }

   //
   //  Get the name of this pro
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT lname, fname, mi, suffix FROM lessonpro5 WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, id);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         StringBuilder pro_name = new StringBuilder(rs.getString("fname"));

         if (rs.getString("mi") != null && !rs.getString("mi").equals( "" )) {
            pro_name.append(" ");
            pro_name.append(rs.getString("mi"));
         }
         pro_name.append(" ");
         pro_name.append(rs.getString("lname"));
         
         if (rs.getString("suffix") != null && !rs.getString("suffix").equals( "" )) {
            pro_name.append(" ");
            pro_name.append(rs.getString("suffix"));
         }

         proName = pro_name.toString();

      }

   } catch (Exception exc) {
       
   } finally {
       
      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}

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
          verifyLesson.clearLessonFromActSheets(lesson_id, req);
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
                 "SELECT length, locations, autoblock_times FROM lessontype5 WHERE proid = ? AND ltname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1,id);
         pstmt.setString(2,ltype);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            ltlength = rs.getInt("length");
            locations_csv = rs.getString("locations");
            autoblock_times = rs.getInt("autoblock_times");
         }
         
      } catch (Exception exc) {

         dbError(req, out, exc, caller);
         return;

      } finally {

         try { rs.close(); }
         catch (SQLException ignored) {}

         try { pstmt.close(); }
         catch (SQLException ignored) {}

      }

      // If in an activity other than golf, check activity sheets for an empty chunk of slots to cover the lesson time
      if (activity_id != 0 && autoblock_times == 1 && !editLesson) {

          int time_start = time;
          int time_end = getEndTime(time, ltlength);

          boolean bookAllSheets = false;
          boolean autoSelectLocation = true;

          sheet_id_map = verifyLesson.checkActivityTimes(lesson_id, activity_id, locations_csv, date, date, new int[1], time_start, time_end, bookAllSheets, autoSelectLocation, con, out);
          
          if (sheet_id_map.get(0) != null) {
              sheet_ids = sheet_id_map.get(0);
          }

          // If no time slots returned, send the user back to the lesson book
          if (sheet_ids.size() == 0) {

              errorPageTop(out, session, req, con, "DB Record In Use Error");
              out.println("  <br /><br /><h3 class=\"lesson_ctr\">No Times Available</h3>");
              out.println("  <p class=\"lesson_ctr\"><br />Sorry, no available time slots were found for this " + sysLingo.TEXT_lesson + ".");
              out.println("    <br /><br />Please select a different time from the " + sysLingo.TEXT_lesson + " book.");
              out.println("  <br /></p>");
              out.println("  <div class=\"lesson_return_top\">");
              out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"can\">");
              out.println("    <input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
              out.println("    <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\">");
              out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
              out.println("    <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
              out.println("    <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
              out.println("    <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
              out.println("    <input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
              out.println("    <input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
              out.println("    <input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
              out.println("    <input type=\"hidden\" name=\"day\" value=\"" + dayName + "\">");
              out.println("    <input type=\"submit\" value=\"Go Back\" name=\"cancel\">");
              out.println("  </form>");
              out.println("  </div>");
              Common_skin.outputPageEnd(club, sess_activity_id, out, req);

              out.close();
              return;

          } else if (club.equals("ballantyne") && sess_activity_id == 1 && sheet_ids.get(0) != -99) {
            
               try {
             
                   // Populate a slotParms instance to pass to checkSched
                   parmSlot slotParms = new parmSlot();

                   slotParms.club = club;
                   slotParms.user1 = memid;
                   slotParms.user2 = "";
                   slotParms.user3 = "";
                   slotParms.user4 = "";
                   slotParms.player1 = memname;
                   slotParms.player2 = "";
                   slotParms.player3 = "";
                   slotParms.player4 = "";
                   slotParms.oldPlayer1 = "";
                   slotParms.oldPlayer2 = "";
                   slotParms.oldPlayer3 = "";
                   slotParms.oldPlayer4 = "";
                   slotParms.date = date;
                   slotParms.activity_id = getActivity.getActivityIdFromSlotId(sheet_ids.get(0), con);
                   slotParms.root_activity_id = sess_activity_id;
                   
                   slotParms.sheet_ids = (ArrayList<Integer>) sheet_ids;
               
                   verifyActSlot.checkSched(slotParms, con);
               
                   if (slotParms.hit || slotParms.hit2 || slotParms.hit3) {

                       //
                       // Populate our parmAct block
                       //
                       parmActivity parmAct = new parmActivity();              // allocate a parm block
                       
                       parmAct.slot_id = sheet_ids.get(0);                              // pass in the slot id so we can determin which activity to load parms for
                       
                       try {
                           
                           getActivity.getParms(con, parmAct);                  // get the activity config
                           
                       } catch (Exception e1) {
                           Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error getting activity_parameters for custom - " + e1.getMessage());
                       }
                      
                       Common_skin.outputHeader(club, sess_activity_id, "Member Scheduling Conflict", true, out, req);
                       Common_skin.outputBody(club, sess_activity_id, out, req);
                       Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
                       Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
                       Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
                       Common_skin.outputPageStart(club, sess_activity_id, out, req);
                       Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
                       Common_skin.outputLogo(club, sess_activity_id, out, req);
                       out.println("  <br /><h3 class=\"lesson_ctr\">Member Already Playing</h3>");
                       out.println("  <p class=\"lesson_ctr\">");

                       if (slotParms.hit == true) {

                           if (parmAct.rndsperday > 1) {

                               out.println("  <br />Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.<br /><br />");
                               out.println("  A player can only be scheduled " + parmAct.rndsperday + " times per day.<br /><br />");

                           } else {

                               out.println("  <br />Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" + SystemUtils.getSimpleTime(slotParms.time2) + "</b>.<br /><br />");
                               out.println("  A player can only be scheduled once per day.<br /><br />");
                           }

                       } else if (slotParms.hit2 == true) {

                           out.println("  <br />Sorry, <b>" + slotParms.player + "</b> is scheduled to play during the time this reservation would cover.<br /><br />");
                           out.println("  A player cannot have multiple reservations covering the same times.<br /><br />");
                           out.println("  <br /><br />");

                       } else if (slotParms.hit3 == true) {

                           out.println("  <br />Sorry, <b>" + slotParms.player + "</b> is scheduled to play another set within " + parmAct.minutesbtwn + " minutes of the time this reservation would cover.<br /><br />");
                           out.println("  " + slotParms.player + " is already scheduled to play on this date at <b>" + SystemUtils.getSimpleTime(slotParms.time2) + "</b>.<br /><br />");
                           out.println("  <br /><br />");
                       }

                       out.println("  <form class=\"lesson_form\" action=\"Member_lesson\" method=\"post\" name=\"can\">");
                       out.println("    <input type=\"hidden\" name=\"reqtime\" value=\"yes\" />");
                       out.println("    <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\" />");
                       out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\" />");
                       out.println("    <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\" />");
                       out.println("    <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\" />");
                       out.println("    <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\" />");
                       out.println("    <input type=\"hidden\" name=\"date\" value=\"" + date + "\" />");
                       out.println("    <input type=\"hidden\" name=\"index\" value=\"" + index + "\" />");
                       out.println("    <input type=\"hidden\" name=\"time\" value=\"" + time + "\" />");
                       out.println("    <input type=\"hidden\" name=\"day\" value=\"" + dayName + "\" />");
                       out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" name=\"cancel\" />");
                       out.println("  </form>");
                       out.println("  </p>");

                       Common_skin.outputPageEnd(club, sess_activity_id, out, req);

                       out.close();
                       return;
                   }
                   
               } catch (Exception exc) {
                   
                   dbError(req, out, exc, caller);
                   return;
                   
               } finally {

                  try { rs.close(); }
                  catch (SQLException ignored) {}

                  try { pstmt.close(); }
                  catch (SQLException ignored) {}

               }
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

    boolean clear_lesson_times = false;
   
    if (sess_activity_id != 0 && sheet_ids.size() > 0 && sheet_ids.get(0) != -99 && req.getParameter("remove") == null && !editLesson) {
        
        // Lock up all time slots and make sure they're not occupied
        lock_up_loop:
        for (int sheet_id : sheet_ids) {
            
            try {
                
                pstmt = con.prepareStatement(
                        "UPDATE activity_sheets "
                        + "SET in_use_by = ?, in_use_at = now() "
                        + "WHERE sheet_id = ? AND (SELECT count(*) FROM activity_sheets_players WHERE activity_sheet_id = ?) = 0 AND "
                        + "( in_use_by = '' || (UNIX_TIMESTAMP(in_use_at) + (6 * 60)) < UNIX_TIMESTAMP())");
                
                pstmt.clearParameters();
                pstmt.setString(1, user);
                pstmt.setInt(2, sheet_id);
                pstmt.setInt(3, sheet_id);
                
                // if we couldn't set the time in use, this means some portion of our court selection has become occupied, and can no longer be used. Free up any times set in use.
                if (pstmt.executeUpdate() == 0) {
                    clear_lesson_times = true;
                    break lock_up_loop;
                }
                
            } catch (Exception e) {
                Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error locking up activity_sheets - ERR: " + e.toString());
            } finally {
                Connect.close(pstmt);
            }
        }
    }
   
     if (!clear_lesson_times) {
         
         //  Update the lesson time
         try {

             pstmt = con.prepareStatement(
                     "UPDATE lessonbook5 "
                     + "SET memname = ?, memid = ?, num = ?, length = ?, billed = ?, in_use = 0, ltype = ?, "
                     + "phone1 = ?, phone2 = ?, notes = ?, sheet_activity_id = ? "
                     + "WHERE recid = ?");

             pstmt.clearParameters();
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

             pstmt.executeUpdate();
             
             lessonBooked = true;

         } catch (Exception e1) {

             errorPageTop(out, session, req, con, "DB Error");
             out.println("  <br /><br /><h3 class=\"lesson_ctr\">Database Access Error</h3>");
             out.println("  <p class=\"lesson_ctr\"><br />Unable to access the Database.");
             out.println("    <br />Please try again later.");
             out.println("    <br /><br />If problem persists, contact customer support.");
             out.println("    <br /><br />" + e1.getMessage());
             out.println("  <br /></p>");

             errorPageForm(id, calDate, ltype, out, session, req);
             Common_skin.outputPageEnd(club, sess_activity_id, out, req);

             out.close();
             return;
         } finally {
             Connect.close(pstmt);
         }
     }

   //
   //  If on an activity other than golf, block any necessary slots
   // if index 0 was -99, that means no time slots in activity_sheets are needed for this lesson, skip this portion
   //
   if (activity_id != 0 && sheet_ids.size() > 0 && autoblock_times == 1 && !editLesson) {

       String inString = "";
       int count = 0;

       for (int k = 0; k < sheet_ids.size(); k++) {
           inString += sheet_ids.get(k) + ",";
       }

       inString = inString.substring(0, inString.length() - 1);
       
       if (sheet_ids.get(0) != -99) {
           
           // If no issue encountered locking up time slots
           if (!clear_lesson_times) {

               try {
                   pstmt = con.prepareStatement("UPDATE activity_sheets SET lesson_id = ? WHERE sheet_id IN (" + inString + ")");
                   pstmt.clearParameters();
                   pstmt.setInt(1, lesson_id);

                   count = pstmt.executeUpdate();

                   pstmt.close();

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

                       try {
                           pstmt2 = con.prepareStatement("UPDATE lessonbook5 SET sheet_activity_id = ? WHERE recid = ?");
                           pstmt2.clearParameters();
                           pstmt2.setInt(1, sheet_activity_id);
                           pstmt2.setInt(2, lesson_id);

                           pstmt2.executeUpdate();
                           
                       } catch (Exception e) {
                           clear_lesson_times = true;
                           Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error updating sheet_id in lesson book slot - ERR: " + e.toString());
                       } finally {
                           Connect.close(pstmt2);
                       }
                   }

               } catch (Exception e) {
                   clear_lesson_times = true;
                   Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error locking up activity_sheets - ERR: " + e.toString());
               } finally {
                   Connect.close(rs, pstmt);
               }

               // Clear in-use flags on locked up activity times
               try {

                   pstmt = con.prepareStatement(
                           "UPDATE activity_sheets "
                           + "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' "
                           + "WHERE in_use_by = ? AND sheet_id IN (" + inString + ")");
                   pstmt.clearParameters();
                   pstmt.setString(1, user);

                   pstmt.executeUpdate();

               } catch (Exception e) {
                   Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error freeing up activity sheets after booking - ERR: " + e.toString());
               } finally {
                   Connect.close(pstmt);
               }
           }

       } else {
           activity_name = "Check with Pro";
       }
       
       if (clear_lesson_times) {
           
           // If the lesson was booked already, and we're not editing an existing lesson, back out the changes.
           if (!editLesson && lessonBooked) {
               try {
                   pstmt = con.prepareStatement(
                           "UPDATE lessonbook5 "
                           + "SET memname = '', memid = '', num = 0, length = 0, billed = 0, ltype = '', "
                           + "phone1 = '', phone2 = '', notes = '', sheet_activity_id = 0, set_id = 0 "
                           + "WHERE recid = ?");

                   pstmt.setInt(1, lesson_id);

                   pstmt.executeUpdate();      // execute the prepared stmt

               } catch (Exception e) {
                   Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error un-booking lesson time - ERR: " + e.toString());
               } finally {
                   Connect.close(pstmt);
               }
           }
           
           // Clear in_use status for all time sheets that were locked up
           try {

               pstmt = con.prepareStatement(
                       "UPDATE activity_sheets "
                       + "SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' "
                       + "WHERE in_use_by = ? AND sheet_id IN (" + inString + ")");
               pstmt.clearParameters();
               pstmt.setString(1, user);

               pstmt.executeUpdate();

           } catch (Exception e) {
               Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error clearing in-use activity sheets - ERR: " + e.toString());
           } finally {
               Connect.close(pstmt);
           }
           
           errorPageTop(out, session, req, con, "DB Record In Use Error");
           out.println("  <br /><br /><h3 class=\"lesson_ctr\">No Times Available</h3>");
           out.println("  <p class=\"lesson_ctr\"><br />Sorry, no available time slots were found for this " + sysLingo.TEXT_lesson + ".");
           out.println("    <br /><br />Please select a different time from the " + sysLingo.TEXT_lesson + " book.");
           out.println("  <br /></p>");
           out.println("  <div class=\"lesson_return_top\">");
           out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"can\">");
           out.println("    <input type=\"hidden\" name=\"reqtime\" value=\"yes\">");
           out.println("    <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\">");
           out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
           out.println("    <input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
           out.println("    <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
           out.println("    <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
           out.println("    <input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
           out.println("    <input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
           out.println("    <input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
           out.println("    <input type=\"hidden\" name=\"day\" value=\"" + dayName + "\">");
           out.println("    <input type=\"submit\" value=\"Go Back\" name=\"cancel\">");
           out.println("  </form>");
           out.println("  </div>");
           Common_skin.outputPageEnd(club, sess_activity_id, out, req);
              
           out.close();
           return;
       }
       
   } else if (activity_id != 0 && sheet_activity_id != 0 && editLesson) {

       // If editing a lesson and the lesson type hasn't changed, look up activity name since we don't already have it
       try {

           pstmt = con.prepareStatement(
                   "SELECT activity_name FROM activities WHERE activity_id = ?");
           pstmt.clearParameters();
           pstmt.setInt(1, sheet_activity_id);

           rs = pstmt.executeQuery();

           if (rs.next()) {
               activity_name = rs.getString("activity_name");
           }
       } catch (Exception e) {
           Utilities.logError("Proshop_lesson.reqTime2 - " + club + " - Error looking up activity name - Err: " + e.toString());
       } finally {
           Connect.close(rs, pstmt);
       }
   }


   //
   //  Make an entry in teehist so we can track these
   //
   if (oldmemname.equals( "" )) {

      //  new lesson
      SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, memname, ltype, "", "",
                             user, fullName, 0, con);

   } else {

       if (req.getParameter("remove") == null) {

           //  update lesson
           SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, memname, ltype, "", "",
                   user, fullName, 1, con);

       } else {

           //  cancel lesson
           SystemUtils.updateHist(date, dayName, time, 99, ltimename, proName, memname, ltype, "", "",
                   user, fullName, 2, con);
       }
   }


   //  Return to user
   Common_skin.outputHeader(club, sess_activity_id, "" + sysLingo.TEXT_Lesson + " Book - Return", true, out, req);
   Common_skin.outputBody(club, sess_activity_id, out, req);
   Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
   Common_skin.outputBanner(club, sess_activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
   Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
   Common_skin.outputPageStart(club, sess_activity_id, out, req);
   Common_skin.outputBreadCrumb(club, sess_activity_id, out, sysLingo.TEXT_Individual_Lesson, req);
   Common_skin.outputLogo(club, sess_activity_id, out, req);


   if (club.equals( "interlachenspa" )) {

      if (!index.equals( "" )) {       // if from My Tee Times Calendar (update)

         out.println("  <br /><br /><h3 class=\"lesson_ctr\">Spa Request Has Been Updated</h3>");
         out.println("  <p class=\"lesson_ctr\"><br /><br />Thank you, your service request has been updated.");

      } else {

         out.println("  <br /><br /><h3 class=\"lesson_ctr\">Spa Request Has Been Booked</h3>");
         out.println("  <p class=\"lesson_ctr\"><br /><br />Thank you, your Spa Service has been reserved.");
      }
      out.println("    <br /><br />Go to 'My Calendar' to view or change this request.");

   } else {

      if (!index.equals( "" )) {       // if from My Tee Times Calendar (update)

         out.println("  <br /><br /><h3 class=\"lesson_ctr\">" + sysLingo.TEXT_Lesson_Reservation + " Has Been Updated</h3>");
         out.println("  <p class=\"lesson_ctr\"><br /><br />Thank you, your " + sysLingo.TEXT_lesson_reservation + " has been updated.");
         if (sess_activity_id != 0 && req.getParameter("remove") == null) {
             if (!activity_name.equals("") && !activity_name.equals("Check with Pro")) {
                 out.println("    This " + sysLingo.TEXT_lesson_reservation + " will take place on " + activity_name + ".");
             } else {
                 out.println("    Please contact the " + sysLingo.TEXT_Lesson_Pro_Contact + " for the location of this " + sysLingo.TEXT_lesson + ".");
             }
         }

      } else {

         out.println("  <br /><br /><h3 class=\"lesson_ctr\">" + sysLingo.TEXT_Lesson + " Has Been Booked</h3>");
         out.println("  <p class=\"lesson_ctr\"><br /><br />Thank you, your " + sysLingo.TEXT_lesson + " has been reserved.");



         if (sess_activity_id != 0 && req.getParameter("remove") == null) {
             if (!activity_name.equals("") && !activity_name.equals("Check with Pro")) {
                 out.println("    This " + sysLingo.TEXT_lesson_reservation + " will take place on " + activity_name + ".");
             } else {
                 out.println("    Please contact the " + sysLingo.TEXT_Lesson_Pro_Contact + " for the location of this lesson.");
             }
         }
      }
      if (IS_TLT) {
         out.println("    <br /><br />Go to <b>Notifications - My Activities</b> to view or change this " + sysLingo.TEXT_lesson + " request.");
      } else if (sess_activity_id != 0) {
         out.println("    <br /><br />Go to <b>Reservations - My Reservations</b> to view or change this " + sysLingo.TEXT_lesson + " request.");
      } else {
         out.println("    <br /><br />Go to <b>Tee Times - My Tee Times Calendar</b> and click on the " + sysLingo.TEXT_lesson + " booking to view or cancel this " + sysLingo.TEXT_lesson + " request.");
      }
   }

   out.println("  <br /></p>");

   //
   //  Print dining request prompt if system is active and properly configured
   //
   if (Utilities.checkDiningLink("mem_lesson", con) && req.getParameter("remove") == null) {
       String msgDate = yy + "-" + mm + "-" + dd;
       out.println("  <p class=\"lesson_ctr\">");
       Utilities.printDiningPrompt(out, con, msgDate, dayName, user, 1, "lesson", "&proid=" + id + "&index=" + index, false);
       out.println("  </p>");
   }

   out.println("  <div class=\"lesson_return_top\">");
   out.println("    <a class=\"standard_button follow_return_path\" href=\"#\" title=\"Continue\">Continue</a>");
   //out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"pform\">");
   //out.println("    <input type=\"hidden\" name=\"proid\" value=\"" +id+ "\" />");
   //out.println("    <input type=\"hidden\" name=\"index\" value=\"" +index+ "\" />");
   //out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Return\" />");
   //out.println("  </form>");
   out.println("  </div>");

   Common_skin.outputPageEnd(club, sess_activity_id, out, req);   
   
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

      // If Charlotte CC, block any lesson slots that fall within the 30 minutes following the main lesson.
      if (club.equals("charlottecc") && sess_activity_id == 0) {

          int tempStime = etime;
          int tempEtime = getEndTime(etime, 30);

          try {

              pstmt = con.prepareStatement("UPDATE lessonbook5 SET block = 1, in_use = 0 WHERE proid = ? AND activity_id = ? AND date = ? AND time >= ? AND time < ?");
              pstmt.clearParameters();
              pstmt.setInt(1, id);
              pstmt.setInt(2, activity_id);
              pstmt.setLong(3, date);
              pstmt.setInt(4, tempStime);
              pstmt.setInt(5, tempEtime);

              pstmt.executeUpdate();

          } catch (Exception exc) {

              Utilities.logError("Member_lesson.reqTime2 - " + club + " - Error adding custom buffer time after lesson - Err: " + exc.toString());
              
          } finally {

              try { rs.close(); }
              catch (Exception ignore) { }

              try { pstmt.close(); }
              catch (Exception ignore) { }
          }
      }

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

         }
      }

   } catch (Exception e1) {
       // TODO add error msg notification
   } finally {

      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}

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
      parme.activity_id = sess_activity_id;
      parme.club = club;
      parme.guests = 0;
      parme.type = "lesson";                        // type = lesson time
      parme.date = date;
      parme.time = time;
      parme.to_time = etime;
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
       
      if (club.equalsIgnoreCase("colletonriverclub")) {
           
          parme.notes = notes;                          // lesson notes
          parme.hideNotes = 0;                          //don't hide notes
      }
      
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
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);

   //String ltype = "";
   String cost = "";
   String descript = "";
   //String error = "";
   String phone1 = "";
   String phone2 = "";
   String ph1 = "";
   String ph2 = "";
   String notes = "";
   String index = "";
   String dates = "";
   String date_string = "";
   String day_string = "";

   int id = 0;
   int max = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int stime = 0;
   int etime = 0;
   int lesson_id = 0;
   int clinic = 0;

   long date = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

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
   //Calendar cal = new GregorianCalendar();           // get today's date
   //int thisYear = cal.get(Calendar.YEAR);            // get the year

   //
   //  Get the current group lesson info
   //
   try {

      pstmt = con.prepareStatement (
              "SELECT *, CONCAT(DATE_FORMAT(DATE, '%c/%d/%Y'), ' - ', DATE_FORMAT(edate, '%c/%d/%Y')) AS 'date_string' FROM lessongrp5 WHERE lesson_id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setInt(1,lesson_id);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         max = rs.getInt("max");
         cost = rs.getString("cost");
         descript = rs.getString("descript");
         clinic = rs.getInt("clinic");
         if (clinic == 1 && sess_activity_id != 0) {
             date = rs.getLong("date");
             date_string = rs.getString("date_string");
             
             if (rs.getInt("eo_week") == 1) {
                 day_string = "Every Other ";
             } else {
                 day_string = "Every ";
             }
             
             if (rs.getInt("sunday") == 1) {
                 day_string += "Sunday, ";
             }
             if (rs.getInt("monday") == 1) {
                 day_string += "Monday, ";
             }
             if (rs.getInt("tuesday") == 1) {
                 day_string += "Tuesday, ";
             }
             if (rs.getInt("wednesday") == 1) {
                 day_string += "Wednesday, ";
             }
             if (rs.getInt("thursday") == 1) {
                 day_string += "Thursday, ";
             }
             if (rs.getInt("friday") == 1) {
                 day_string += "Friday, ";
             }
             if (rs.getInt("saturday") == 1) {
                 day_string += "Saturday, ";
             }
             if (day_string.endsWith(", ")) {
                 day_string = day_string.substring(0, day_string.length() - 2);
             }
         }

      } else {

          nfError_ns(req, out, caller, con, sess_activity_id, club);            // LT not found
          return;
      }

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

      if (hit == true && index.equals( "" )) {       // if exists and this is a new request - reject

          Common_skin.outputHeader(club, sess_activity_id, "Lesson Book - Return", true, out, req);
          Common_skin.outputBody(club, sess_activity_id, out, req);
          Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
          Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
          Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
          Common_skin.outputPageStart(club, sess_activity_id, out, req);
          Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
          Common_skin.outputLogo(club, sess_activity_id, out, req);

          out.println("  <br /><br /><h3 class=\"lesson_ctr\">Unable to Sign Up for Group Lesson</h3>");
          out.println("  <p class=\"lesson_ctr\"><br /><br />Sorry, you are already registered for this Group Lesson.");
          if (sess_activity_id != 0) {
              out.println("  <br /><br />Go to <b>My Reservations - Calendar</b> to view or change this lesson request.");
          } else {
              if (IS_TLT) {
                  out.println("  <br /><br />Go to <b>Notifications - My Activities</b> to view or change this lesson request.");
              } else {
                  out.println("  <br /><br />Go to <b>Tee Times - My Tee Times</b> to view or change this lesson request.");
              }
          }
          out.println("  <br /></p>");
          out.println("  <div class=\"lesson_return_top\">");
          out.println("  <form action=\"Member_lesson\" method=\"post\">");
          out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" />");
          out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\" />");
          out.println("    <input type=\"hidden\" name=\"group\" value=\"yes\" />");
          out.println("  </form></div>");
          Common_skin.outputPageEnd(club, sess_activity_id, out, req);
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

      if (phone1.equals( "" ) && !ph1.equals( "" )) {

         phone1 = ph1;
      }

      if (phone2.equals( "" ) && !ph2.equals( "" )) {

         phone2 = ph2;
      }

   } catch (Exception exc) {

      dbError(req, out, exc, caller);
      return;

   } finally {

      try { rs.close(); }
      catch (SQLException ignored) {}

      try { pstmt.close(); }
      catch (SQLException ignored) {}

   }

     //
     //  Output a page to display the group lesson info
     //
     Common_skin.outputHeader(club, sess_activity_id, "Member Lesson Book", false, out, req);

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
  out.println("</head>");   
   //out.println("<body onLoad=\"movenotes()\" >");
   //out.println("<div id=\"top\"></div><div id=\"wrapper\">");
   Common_skin.outputBody(club, sess_activity_id, out, req, " onload=\"movenotes()\"");
   Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
   Common_skin.outputBanner(club, sess_activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
   Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
   Common_skin.outputPageStart(club, sess_activity_id, out, req);
   Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
   Common_skin.outputLogo(club, sess_activity_id, out, req);

   out.println("  <div class=\"main_instructions\">"); 
   out.println("    <b>Group Lesson:</b>&nbsp;&nbsp;&nbsp;&nbsp;" +lgname);
   out.println("    <p class=\"lesson_left3\"><br />Make the desired additions or changes below and ");
   out.println("      click on Submit when done.</p>");
   out.println("  </div>");

   if (clinic == 1 && sess_activity_id != 0) {
       out.println("  <p class=\"lesson_ctr\">Dates:&nbsp;&nbsp;<b>" + date_string + "&nbsp;&nbsp;&nbsp;&nbsp;" + day_string + "</b></p>");
       out.println("  <p class=\"lesson_ctr\">Time:&nbsp;&nbsp;<b>" + Utilities.getSimpleTime(stime) + " &ndash; " + Utilities.getSimpleTime(etime) + "</b></p>");
   } else {
       out.println("  <p class=\"lesson_ctr\"><span class=\"ft-textGroup\">Date:&nbsp;&nbsp;<b>" + month + "/" + day + "/" + year + "</b></span> <span class=\"rwdHide\">&nbsp;&nbsp;&nbsp;&nbsp;</span><span class=\"ft-textGroup\">Time:&nbsp;&nbsp;<b>" + Utilities.getSimpleTime(stime) + " &ndash; " + Utilities.getSimpleTime(etime) + "</b></span></p>");
   }
   out.println("  <p class=\"lesson_ctr\">Cost:&nbsp;&nbsp;<b>" + cost + "</b>&nbsp;&nbsp;&nbsp;&nbsp;Max Group Size:&nbsp;&nbsp;<b>" + max + "</b></p>");
   out.println("  <div class=\"lesson_desc3\">");
   out.println("    <p class=\"lesson_ctr\"><b>Description:</b> " + descript + "</p>");
   out.println("  </div>");

   out.println("  <div class=\"lesson_req_main\">");
   out.println("    <div class=\"lesson_req_goback\">");

   if (index.equals( "" )) {
       out.println("    <form action=\"Member_lesson\" method=\"post\" name=\"can\">");
       out.println("      <input type=\"hidden\" name=\"group\" value=\"yes\" />");
       out.println("      <input type=\"hidden\" name=\"proid\" value=\"" +id+ "\" />");
       out.println("      <input type=\"hidden\" name=\"cancel\" value=\"cancel\" />");
   } else {
       out.println("    <form action=\"Member_teelist\" method=\"get\" name=\"can\">");
   }
   out.println("      <soan>Go Back<br />w/o Changes:<br /></span>");
   out.println("      <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" />");

   out.println("    </form></div>");   // end of 1st col

   out.println("    <form action=\"Member_lesson\" method=\"post\" name=\"playerform\">");
   out.println("      <input type=\"hidden\" name=\"groupLesson2\" value=\"" +lgname+ "\" />");
   out.println("      <input type=\"hidden\" name=\"lesson_id\" value=\"" +lesson_id+ "\" />");
   out.println("      <input type=\"hidden\" name=\"proid\" value=\"" +id+ "\" />");
   out.println("      <input type=\"hidden\" name=\"date\" value=\"" +date+ "\" />");
   out.println("      <input type=\"hidden\" name=\"time\" value=\"" +stime+ "\" />");
   out.println("      <input type=\"hidden\" name=\"index\" value=\"" +index+ "\" />");
   out.println("      <input type=\"hidden\" name=\"max\" value=\"" +max+ "\" />");

   out.println("      <div class=\"lesson_req_add_chg_form\">");
   out.println("      <div class=\"lesson_req_add_chg_form_hdr\">");
   out.println("        <b>Add or Change Member Lesson</b>");
   out.println("      </div>");

   out.println("");
   out.println("      <div class=\"lesson_req_add_chg_form_body\">");
   out.println("      <p class=\"lesson_left\">Member:&nbsp;&nbsp;&nbsp;&nbsp;" +memname);
   out.println("      </p>");

   out.println("      <p class=\"lesson_left\">Phone 1:&nbsp;&nbsp;");
   out.println("      <input type=\"text\" name=\"phone1\" value=\"" +phone1+ "\" size=\"20\" maxlength=\"24\" />");
   out.println("      &nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone1')\" style=\"cursor:hand\" />");
   out.println("      </p>");

   out.println("      <p class=\"lesson_left\">Phone 2:&nbsp;&nbsp;");
   out.println("      <input type=\"text\" name=\"phone2\" value=\"" +phone2+ "\" size=\"20\" maxlength=\"24\" />");
   out.println("      &nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('phone2')\" style=\"cursor:hand\" />");
   out.println("      </p>");

   //
   //   Notes
   //
   //   Script will put any existing notes in the textarea (value= doesn't work)
   //
   out.println("      <input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\" />"); // hold notes for script

   out.println("      <p class=\"lesson_left\">&nbsp;&nbsp;Notes:&nbsp;&nbsp;");
   out.println("      <textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"24\" rows=\"3\">");
   out.println("      </textarea>");
   out.println("      &nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\" />");
   out.println("      </p><br />");

   out.print("      <p class=\"lesson_ctr2\">");
    if(rwd){
        out.print("<a href=\"#\" class=\"standard_button submitForm\" target=\"form[name=can]\">Go Back</a> ");
    }
    out.print("<input class=\"standard_button\" type=submit value=\"Submit\" name=\"submit\" />");
    out.println("</p>");
   //out.println("      <p class=\"lesson_ctr2\"><input class=\"standard_button\" type=submit value=\"Submit\" name=\"submit\" /></p>");
   out.println("      </div>    <!-- lesson_req_add_chg_form_body -->");
   out.println("      </div>    <!-- lesson_req_add_chg_form -->");

   if (!index.equals( "" )) {          // if an edit

       out.println("      <div class=\"lesson_cancel\">");
       out.println("      <input class=\"standard_button\" type=submit value=\"Cancel Lesson\" name=\"remove\" />"); 
       out.println("      </div>");        
   }

   out.println("    </form>");
   out.println("  </div>    <!-- lesson_req_main -->");

   //
   //  End of HTML page
   //
   Common_skin.outputPageEnd(club, sess_activity_id, out, req);   
   
   out.close();

 }


 // ********************************************************************
 //  Process the 'Update Group Lesson Time' from grpLesson above (submit)
 // ********************************************************************

 private void grpLesson2(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, HttpSession session,
                       Connection con, String proid) throws ServletException, IOException {

   PreparedStatement pstmt = null;
   //PreparedStatement pstmt2 = null;
   ResultSet rs = null;


   //
   // Define parms - set defaults
   //
   String club = (String)session.getAttribute("club");               // get name of club
   String user = (String)session.getAttribute("user");               // get name of user
   String caller = (String)session.getAttribute("caller");
   String memname = (String)session.getAttribute("name");
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);

   //String temps = "";
   String memid = user;
   String memName = memname;
   String tempMM = "";
   String tempDD = "";
   String tempYY = "";
   String msgDate = "";
   String day_name = "";
   String params = "";

   int id = 0;
   //int i = 0;
   //int i2 = 0;
   //int i3 = 0;
   //int j = 0;
   int max = 0;
   //int num = 0;
   int month = 0;
   int day = 0;
   int year = 0;
   int time = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   //int emailCan = 0;
   int dayOfWeek = 0;
   int msgPlayerCount = 0;

   long date = 0;
   //long sdate = 0;
   //long edate = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;

   //boolean b = false;
   //boolean hit = false;

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
      sendemail = 1;
      emailNew = 0;
      emailMod = 0;
      emailCan = 1;

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

         pstmt.executeUpdate();

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

      }

   } catch (Exception e1) {

      dbError(req, out, e1, caller);
      return;

   } finally {

      try { pstmt.close(); }
      catch (Exception ignored) {}

   }

   Calendar cal = new GregorianCalendar();       // get todays date
   cal.set(year, month - 1, day);

   dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

   if (dayOfWeek == 1) { day_name = "Sunday"; }
   if (dayOfWeek == 2) { day_name = "Monday"; }
   if (dayOfWeek == 3) { day_name = "Tuesday"; }
   if (dayOfWeek == 4) { day_name = "Wednesday"; }
   if (dayOfWeek == 5) { day_name = "Thursday"; }
   if (dayOfWeek == 6) { day_name = "Friday"; }
   if (dayOfWeek == 7) { day_name = "Saturday"; }

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
     Common_skin.outputHeader(club, sess_activity_id, "Lesson Book - Return", true, out, req);
     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);
     Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
     Common_skin.outputLogo(club, sess_activity_id, out, req);

     out.println("  <br /><br /><h3 class=\"lesson_ctr\">Group Lesson Signup Complete</h3>");
     if (index.equals("")) {       // if new entry
         out.println("  <p class=\"lesson_ctr\"><br /><br />You have been added to the Group Lesson.");
     } else {
         out.println("  <p class=\"lesson_ctr\"><br /><br />Thank you, your entry in the Group Lesson has been updated.");
     }

     if (IS_TLT) {
         out.println("    <br /><br />Go to <b>Notifications - My Activities</b> to view or change this lesson request.");
     } else {
         out.println("    <br /><br />Go to <b>" + (sess_activity_id == 0 ? "Tee Times - My Tee Times" : "Reservations - My Reservations") + "</b> to view or change this lesson request.");
     }

     out.println("  <br /></p>");

     //
     //  Print dining request prompt
     //
     if (Utilities.checkDiningLink("mem_lesson", con) && req.getParameter("remove") == null) {
         out.println("  <p class=\"lesson_ctr\">");
         Utilities.printDiningPrompt(out, con, msgDate, day_name.toLowerCase(), user, msgPlayerCount, "lesson", params, false);
         out.println("  </p>");
     }


     //if (index.equals( "" )) {       // if new entry
     //   out.println("  <div class=\"lesson_return_top\">");
     //   out.println("  <form action=\"Member_lesson\" method=\"post\">");
     //   out.println("    <input type=\"hidden\" name=\"proid\" value=\"" +id+ "\" />");
     //   out.println("    <input type=\"hidden\" name=\"group\" value=\"yes\" />");
     //   out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Return\" />");
     //   out.println("  </form></div>");
     //} else {
     out.println("  <div class=\"lesson_return_top\">");
     out.println("    <a class=\"standard_button follow_return_path\" href=\"#\" title=\"Continue\">Continue</a>");
     //out.println("  <form action=\"Member_teelist\" method=\"get\">");
     //out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Return\" />");
     //out.println("  </form>");
     out.println("  </div>");
     //}      
     Common_skin.outputPageEnd(club, sess_activity_id, out, req);
   
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
      parme.emailCan = emailCan;
      parme.day = day_name;

      parme.type = "lessongrp";             // type = group lesson time
      parme.user = user;                    // from user
      parme.user1 = user;                   // username of member
      parme.player1 = memname;              // name of member
      parme.course = lgname;                // put group lesson name in course field
      parme.activity_name = getActivity.getActivityName(sess_activity_id, con);          // root activity name

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common
   }

 }

 private void grpSelectDate(HttpServletRequest req, HttpSession session, PrintWriter out, Connection con) {

        String club = (String) session.getAttribute("club");               // get name of club
        //String user = (String) session.getAttribute("user");               // get name of user
        String caller = (String) session.getAttribute("caller");
        int sess_activity_id = (Integer) session.getAttribute("activity_id");
        String clubName = SystemUtils.getClubName(con);            // get the full name of this club

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
        //int jump = 0;
        int diff = 0;
        
        boolean dateMatch = false;

        //if (req.getParameter("jump") != null) {
        //    jump = Integer.parseInt(req.getParameter("jump"));
        //}

        lesson_id = Integer.parseInt(req.getParameter("lesson_id"));
        id = Integer.parseInt(req.getParameter("proid"));

        try {

            // Get the name of this lesson group from lessongrp5
            pstmt = con.prepareStatement("SELECT lname, date, edate, max, IF(DATEDIFF(edate, CURDATE()) > 120, 120, DATEDIFF(edate, CURDATE())) AS diff FROM lessongrp5 WHERE lesson_id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, lesson_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                lname = rs.getString("lname");
                sdate = rs.getInt("date");
                edate = rs.getInt("edate");
                maxPlayers = rs.getInt("max");
                diff = rs.getInt("diff") + 1;
            }

            // Get the current date values
            Calendar cal_date = new GregorianCalendar();

            //
            //    Adjust the time based on the club's time zone (we are Central)
            //
            int cal_hourDay = cal_date.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
            int cal_min = cal_date.get(Calendar.MINUTE);
            int cal_time = (cal_hourDay * 100) + cal_min;
            cal_time = SystemUtils.adjustTime(con, cal_time);           // adjust to club local time

            if (cal_time < 0) {          // if negative, then we went back or ahead one day

                cal_time = 0 - cal_time;        // convert back to positive value

                if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi time)

                    //
                    // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                    //
                    cal_date.add(Calendar.DATE, 1);                     // get next day's date

                } else {                        // we rolled back 1 day

                    //
                    // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                    //
                    cal_date.add(Calendar.DATE, -1);                     // get yesterday's date

                }
            }



            int cal_year = cal_date.get(Calendar.YEAR);
            int cal_month = cal_date.get(Calendar.MONTH) + 1;
            int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
            int curr_date = (cal_year * 10000) + (cal_month * 100) + cal_day;

            Map<String, Object> date_map = new LinkedHashMap<String, Object>();
            date_map.put("year", cal_year);
            date_map.put("month", cal_month - 1);
            date_map.put("dayOfMonth", cal_day);

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

                for (int i = 0; i < diff; i++) {
                    daysArray[i] = 0;
                }

                // Set the calendar object to the start date of the group lesson
                //cal_date.set(s_yy, s_mm - 1, s_dd);

                int thisyear = cal_date.get(Calendar.YEAR);
                int thismonth = cal_date.get(Calendar.MONTH) + 1;
                int thisday = cal_date.get(Calendar.DAY_OF_MONTH);

                int thisdate = (thisyear * 10000) + (thismonth * 100) + thisday;

                if (!lname.equals("")) {

                    pstmt = con.prepareStatement(
                            "SELECT date, (SELECT COUNT(*) FROM lgrpsignup5 WHERE lname = ? AND date = lessonbook5.date) AS playerCount "
                            + "FROM lessonbook5 "
                            + "WHERE lgname = ? AND date >= DATE_FORMAT(CURDATE(), '%Y%m%d') "
                            + "GROUP BY date "
                            + "ORDER BY date");
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
                        while (i < diff) {

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
                            cal_date.add(Calendar.DATE, 1);
                            thisyear = cal_date.get(Calendar.YEAR);
                            thismonth = cal_date.get(Calendar.MONTH) + 1;
                            thisday = cal_date.get(Calendar.DAY_OF_MONTH);

                            thisdate = (thisyear * 10000) + (thismonth * 100) + thisday;

                            i++;

                            // If we matched on this date, then break out of the inner loop and move to next date
                            if (dateMatch) {
                                break;
                            }
                        }
                    }

                }

                // Start output of HTML page
                Common_skin.outputHeader(club, sess_activity_id, "Member - Edit Group Lesson Time", true, out, req);
                Common_skin.outputBody(club, sess_activity_id, out, req);
                Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
                Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
                Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
                Common_skin.outputPageStart(club, sess_activity_id, out, req);
                Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Group Lessons", req);
                Common_skin.outputLogo(club, sess_activity_id, out, req);

                out.println("  <div class=\"main_instructions\">");
                out.println("    <b>Group Lesson:</b>&nbsp;&nbsp;&nbsp;&nbsp;" + lname);
                out.println("    <p class=\"lesson_left2\"><br />Use the calendar below to select the desired date to attend this lesson.");
                out.println("      Available dates are displayed in <b><u>green</u></b>.");
                out.println("      Dates in <b><u>red</u></b> indicate that all slots are full.");
                out.println("    </p>");
                out.println("  </div>");

                //  Form for building the Calendars
                out.println("    <form action=\"Member_lesson\" method=\"post\" name=\"frmLoadDay\">");
                out.println("      <input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");
                out.println("      <input type=\"hidden\" name=\"calDate\" value=\"\">");
                out.println("      <input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
                out.println("      <input type=\"hidden\" name=\"lgname\" value=\"" + lname + "\">");
                out.println("      <input type=\"hidden\" name=\"lesson_id\" value=\"" + lesson_id + "\">");
                out.println("      <input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");  // get to lesson signup

                out.println("    </form>");

                // Create Json response for later use
                Gson gson_obj = new Gson();

                Map<String, Object> calendar_map = new LinkedHashMap<String, Object>();

                // Make our daysarray object look like the one in Member_sheet
                Map<String, Object> days_array_map = new LinkedHashMap<String, Object>();
                days_array_map.put("days", daysArray);

                // Group the data we want to send to javascript in a hash map
                calendar_map.put("daysArray", days_array_map);
                calendar_map.put("cal", date_map);
                calendar_map.put("max", diff);
                calendar_map.put("IS_TLT", false);

                // Ecode the data with json
                String jsonHashMap = gson_obj.toJson(calendar_map);

                out.println("<div class=\"single_lesson_cal_holder\">");
                out.println("<div id=\"lesson_calendar\" class=\"calendar lesson\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(jsonHashMap) + "\"></div>");
                out.println("</div>");

                out.println("<div class=\"lesson_return_2nd\">");

                out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"can\">");
                out.println("    <input type=\"hidden\" name=\"group\" value=\"yes\">");
                out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
                out.println("    <input type=\"submit\" value=\"Go Back\" class=\"go_back_button\">");
                out.println("  </form>");
                out.println("</div>");

                Common_skin.outputPageEnd(club, sess_activity_id, out, req);
            }

        } catch (Exception exc) {
            out.println(exc.getMessage());
        } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}

        }
 }

 
 // *********************************************************
 //  Cancel a request to edit the lesson book
 // *********************************************************

 private void cancel(int id, int time, long date, String calDate, String ltype, PrintWriter out, HttpSession session,
         HttpServletRequest req, String caller, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int length = 0;
   int etime = 0;

   String club = (String)session.getAttribute("club");               // get name of club
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
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

     //  Return to lesson book
     if (out == null) {
         return;
     }
     Common_skin.outputHeader(club, sess_activity_id, "Lesson Book - Cancel", true, out, req);
     //out.println("<meta http-equiv=\"Refresh\" content=\"5; url=Member_lesson?lesson=yes&proid=" +id+ "&calDate=" +calDate+ "&ltype=" +ltype+ "&index=" +index+ "\">");
     //out.println("</head>");
     Common_skin.outputBody(club, sess_activity_id, out, req);
     Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
     Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
     Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
     Common_skin.outputPageStart(club, sess_activity_id, out, req);
     Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Go Back", req);
     Common_skin.outputLogo(club, sess_activity_id, out, req);

     out.println("  <br /><br /><h3 class=\"lesson_ctr\">Go Back/Cancel Requested</h3>");
     out.println("  <p class=\"lesson_ctr\"><br /><br />Thank you, the reservation request has been returned to the system without changes.");
     out.println("  <br /></p>");

     out.println("  <div class=\"lesson_return_top\">");
     out.println("    <a class=\"standard_button follow_return_path auto_click_in_5\" href=\"#\" title=\"Continue\">Continue</a>");
     //out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"pform\">");
     // out.println("    <input type=\"hidden\" name=\"proid\" value=\"" +id+ "\">");
     //out.println("    <input type=\"hidden\" name=\"ltype\" value=\"" +ltype+ "\">");
     //out.println("    <input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
     //out.println("    <input type=\"hidden\" name=\"calDate\" value=\"" +calDate+ "\">");
     //out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Continue\" />");
     //out.println("  </form>");

     out.println("  </div>");
     Common_skin.outputPageEnd(club, sess_activity_id, out, req);
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
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   String proName = "";
   //String lname = "";
   //String fname = "";
   String mi = "";
   String suffix = "";
   //String temp = "";
   int id = 0;
   int count = 0;
   boolean found = false;

   try {
      id = Integer.parseInt(proid);              // id of pro for this lesson book
   }
   catch (NumberFormatException e) {
      id = 0;
   }

   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   
   sysLingo.setLingo("Lesson Book", club, sess_activity_id);


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

           errorPageTop(out, session, req, con, "Lesson Not Supported");
           out.println("  <br /><br /><h3 class=\"lesson_ctr\">Online Scheduling Currently Unavailable</h3>");
           out.println("  <p class=\"lesson_ctr\"><br />Sorry, the online lesson scheduler is not available for your club at this time.");
           out.println("    <br /><br />" + custMsg);
           out.println("  <br /></p>");
           Common_skin.outputPageEnd(club, sess_activity_id, out, req);
           out.close();

           return;            // return none
       }

      //
      //  Get the existing pro ids and prompt for a selection
      //
      if (!club.equals("bellemeadecc") || sess_activity_id != 0) {        // Belle Meade - no access for members

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

         } catch (Exception exc) {

            found = false;      // not found

         } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}

         }
      }

      //
      //  Process according to the number of ids found
      //
      if (found == false) {        // if none yet

          // Pros do not exist yet - inform user to add some
          errorPageTop(out, session, req, con, "Sequence Error");
          out.println("  <br /><br /><h3 class=\"lesson_ctr\">Setup Sequence Error</h3>");
          out.println("  <p class=\"lesson_ctr\"><br />Sorry, we are unable to process your request at this time.");
          if (club.equals("interlachenspa")) {
              out.println("    <br />There are no Spa Services defined in the system yet.");
              out.println("    <br /><br />Please contact your Club Staff for assistance.");
          } else {
              out.println("    <br />There are no Lesson Pros in the system yet.");
              out.println("    <br /><br />Please contact your " + (sess_activity_id > 0 ? "Pro" : "Golf") + " Shop Staff for assistance.");
          }
          out.println("  <br /></p>");
          Common_skin.outputPageEnd(club, sess_activity_id, out, req);
          out.close();
          return;            // return none
      }

       //  Build the html page to request the lesson info
       Common_skin.outputHeader(club, sess_activity_id, "Member - Select Pro", true, out, req);
       Common_skin.outputBody(club, sess_activity_id, out, req);
       Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
       Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
       Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
       Common_skin.outputPageStart(club, sess_activity_id, out, req);

       if (club.equals("interlachenspa")) {
           Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Spa Service", req);
       } else {
           Common_skin.outputBreadCrumb(club, sess_activity_id, out, sysLingo.TEXT_Lesson_Pro + " Bio", req);
       }
       Common_skin.outputLogo(club, sess_activity_id, out, req);
       out.println("  <div class=\"main_instructions\">");

       if (club.equals("interlachenspa")) {
           out.println("    <b>View a Spa Service Description</b><br />");
           out.println("    <br />Select a Spa Service from the list below.<br />");
       } else {
           out.println("    <b>View a " + sysLingo.TEXT_Lesson_Pro + "'s Bio</b><br />");
           if (sess_activity_id > 0) {
               out.println("    <br />Select a Professional from the list below.<br />");
           } else {
               out.println("    <br />Select a " + sysLingo.TEXT_Lesson_Pro + " from the list below.<br />");
           }
       }
       out.println("  </div>");

       out.println("  <div class=\"lesson_select\">");
       //out.println("    <form action=\"Member_lesson\" method=\"post\" name=\"pform\" target=\"_blank\">");
       //out.println("      <input type=\"hidden\" name=\"bio\" value=\"yes\" />");

       if (club.equals("interlachenspa")) {
           out.println("<b>Spa Service:</b>&nbsp;&nbsp;<br /><br />");
       } else {
           out.println("<b>Select an Available " + sysLingo.TEXT_Lesson_Pro + ":</b>&nbsp;&nbsp;<br /><br />");
       }

         try {

             File f = null;

            pstmt = con.prepareStatement (
                    "SELECT * FROM lessonpro5 WHERE activity_id = ? ORDER BY sort_by");

            pstmt.clearParameters();
            pstmt.setInt(1, sess_activity_id);
            rs = pstmt.executeQuery();

            while (rs.next()) {

               id = rs.getInt("id");                   // get the proid

               f = new File(req.getRealPath("") + "/announce/" +club+ "/" +club+ "_bio" +id+ ".htm");

               if (!f.isFile()) {
                  
                  // do nothing - pro hasn't created a bio page yet
                  out.println("<!-- couldn't file bio page file: " + req.getRealPath("") + "/announce/" +club+ "/" +club+ "_bio" +id+ ".htm -->");

               } else {

                   StringBuilder pro_name = new StringBuilder(rs.getString("fname"));  // get first name

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

                   out.println("<a href=\"/#\" class=\"helpTopic standard_button\" data-fthelptitle=\""+pro_name.toString()+"\" data-fthelplink=\"" + "Common_lesson?bioview&modal=true&club=" +club+ "&proid=" +id+ "\">" + pro_name.toString() + "</a><br /><br />");
                   
               } // end bio page found

            } // end pro loop
            
            out.println("  </div>    <!-- lesson_select -->");

         } catch (Exception exc) {
            out.println("  </form>");
            out.println("  <br /><br />Unable to access the Database.");
            out.println("  <br />Please try again later.");
            out.println("  <br /><br />" + exc.getMessage());  
         } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { pstmt.close(); }
            catch (SQLException ignored) {}

         }

      out.println("<p class=\"lesson_ctr\"><b>Note:</b> Simply click the 'X' in the upper right corner of the bio window to close it.</p>");
      Common_skin.outputPageEnd(club, sess_activity_id, out, req);
          
      out.close();

   } else {

       //  Proid provided - display his bio
       Common_skin.outputHeader(club, sess_activity_id, "Display Pro Bio", true, out, req, 5, "/" + rev + "/announce/" + club + "/" + club + "_bio" + id + ".htm\"");
       //out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/announce/" +club+ "/" +club+ "_bio" +id+ ".htm\">");
       //out.println("</head>");
       Common_skin.outputBody(club, sess_activity_id, out, req);
       Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
       Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
       Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
       Common_skin.outputPageStart(club, sess_activity_id, out, req);
       Common_skin.outputBreadCrumb(club, sess_activity_id, out, sysLingo.TEXT_Lesson_Pro + " Bio", req);
       Common_skin.outputLogo(club, sess_activity_id, out, req);

       out.println("  <div class=\"main_instructions\">");

       out.println("    <b>" + sysLingo.TEXT_Lesson_Pro + " Bio Requested</b><br />");
       out.println("    <br />If this page does not automatically refresh, then click Continue.<br />");
       out.println("  </div>");

       out.println("  <p class=\"lesson_ctr\"><a href=\"/" + rev + "/announce/" + club + "/" + club + "_bio" + id + ".htm\" class=\"tu_iframe_720x500\">Continue</a></p>");
       Common_skin.outputPageEnd(club, sess_activity_id, out, req);

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
   out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 private void nfError_ns(HttpServletRequest req, PrintWriter out, String caller,
         Connection con, int sess_activity_id, String club) {

   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   Common_skin.outputHeader(club, sess_activity_id, "Database Error", true, out, req);
   Common_skin.outputBody(club, sess_activity_id, out, req);
   Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
   Common_skin.outputBanner(club, sess_activity_id, clubName, "", out, req);
   Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
   Common_skin.outputPageStart(club, sess_activity_id, out, req);
   Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Error", req);
   Common_skin.outputLogo(club, sess_activity_id, out, req);

   out.println("  <br /><br /><h3 class=\"lesson_ctr\">Record Not Found Error</h3>");
   out.println("  <p class=\"lesson_ctr\">");
   out.println("    <br /><br />Sorry, we are unable to locate the database record at this time.");
   out.println("    <br />Please try again later.");
   out.println("    <br /><br />If problem persists, contact your club.");
   out.println("  </p>");   
   Common_skin.outputPageEnd(club, sess_activity_id, out, req);   
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
   out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }
 
 private void dbError_ns(HttpServletRequest req, PrintWriter out, Exception exc, String caller,
         Connection con, int sess_activity_id, String club) {
 
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   Common_skin.outputHeader(club, sess_activity_id, "Database Error", true, out, req);
   Common_skin.outputBody(club, sess_activity_id, out, req);
   Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
   Common_skin.outputBanner(club, sess_activity_id, clubName, "", out, req);
   Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
   Common_skin.outputPageStart(club, sess_activity_id, out, req);
   Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Error", req);
   Common_skin.outputLogo(club, sess_activity_id, out, req);

   out.println("  <br /><br /><h3 class=\"lesson_ctr\">Database Access Error</h3>");
   out.println("  <p class=\"lesson_ctr\">");
   out.println("    <br /><br />Sorry, we are unable to access the database at this time.");
   out.println("    <br />Please try again later.");
   out.println("    <br /><br />If problem persists, contact your club.");
   out.println("    <br /><br />" + exc.getMessage());
   out.println("  </p>");

   Common_skin.outputPageEnd(club, sess_activity_id, out, req);   
   out.close();
 }

 // *********************************************************
 //  Common functions for database errors
 // *********************************************************
 
private void errorPageTop(PrintWriter out, HttpSession session, HttpServletRequest req, Connection con, String titleStr) {

    String club = (String) session.getAttribute("club");               // get name of club
    int sess_activity_id = (Integer) session.getAttribute("activity_id");
    String clubName = SystemUtils.getClubName(con);            // get the full name of this club
    //String index = req.getParameter("index");               // get return indicator

    Common_skin.outputHeader(club, sess_activity_id, titleStr, true, out, req);
    Common_skin.outputBody(club, sess_activity_id, out, req);
    Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
    Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
    Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
    Common_skin.outputPageStart(club, sess_activity_id, out, req);
    Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Error", req);
    Common_skin.outputLogo(club, sess_activity_id, out, req);
 }


 private void errorPageForm(int id, String calDate, String ltype, PrintWriter out, HttpSession session,
         HttpServletRequest req) {

     String index = req.getParameter("index");               // get return indicator

     out.println("  <div class=\"lesson_return_top\">");
     out.println("  <form action=\"Member_lesson\" method=\"post\" name=\"pform\">");
     out.println("    <input type=\"hidden\" name=\"proid\" value=\"" + id + "\">");
     out.println("    <input type=\"hidden\" name=\"ltype\" value=\"" + ltype + "\">");
     out.println("    <input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
     out.println("    <input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
     out.println("    <input class=\"standard_button\" type=\"submit\" value=\"Go Back\" />");
     out.println("  </form>");
     out.println("  </div>");

 }
 
}

