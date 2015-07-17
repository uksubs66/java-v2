/***************************************************************************************
 *   Member_sheet:  This servlet will process the 'View Tee Sheet' request from
 *                    the Member's Select page.
 *
 *
 *   called by:  Member_select (doPost)
 *               Member_slot (via Member_jump on a cancel)
 *
 *
 *   created: 1/14/2002   Bob P.
 *
 *   last updated:        ******* keep this accurate *******
 *
 *        5/30/06   Added support for displaying shotgun hole assignments and event start time
 *        5/23/06   Westchester - do not allow members to access tee times starting at 6:00 PM the day before.
 *        5/20/06   Init the twosomeonly flag on each tee time.
 *        5/19/06   TPC-TC - allow Corporate mships to make 2 tee times at once.
 *        5/17/06   Wee Burn - do not allow members to access tee times on the day of (today).
 *        5/11/06   Forest Highlands - display 'Player' in place of X.
 *        4/26/06   Do not allow members to access tee times if they are 2-some only times and full.
 *        4/20/06   Cordillera - do not allow members to access tee times on the day of (today).
 *        4/19/06   Fix the tests for lottery state - use the correct time values.
 *        4/18/06   Nakoma - Use 90 day calendars.
 *        4/17/06   Medinah - change special times (member times, etc.) per Medinah's instructions.
 *        4/17/06   Medinah - remove ARR processing per Medinah's instructions.
 *        4/14/06   Change calls to Member_slot from doGet to doPost calls - security reasons.
 *        4/14/06   Bearpath - do not allow members to access tee times on the day of (today).
 *        4/13/06   Inverness - Use 90 day calendars.
 *        4/12/06   Oakmont CC - do not allow members to make/change times on weekends more than 14 days
 *                               in advance (they changed their minds on this again).
 *        4/11/06   Catamount Ranch - use 365 day calendars and allow members to book 180 days in advance.
 *        4/05/06   New Canaan & Des Moines - do not allow members to access tee times within 3 hours of the tee time (today).
 *        3/30/06   Forest Highlands - modify the hide names feature.
 *        3/28/06   North Hills - do not allow members to access tee times on the day of (today).
 *        3/15/06   CC of the Rockies - use 365 day calendars and allow members to book all year.
 *        3/15/06   Bearpath - color code the member-only times.
 *        3/14/06   Tamarack - do not allow members to access tee times on the day of (today).
 *        3/14/06   Oakland Hills - use 365 day calendars and allow members to book certain times beyond the 8 days in adv limit.
 *        3/02/06   Oakmont - change the special Wed. guest times for Sept.
 *        3/01/06   North Hills - allow 30 days in advance for guest times.
 *        2/28/06   Merion - do not show member and guest names whenever there is a guest in the tee time.
 *        2/23/06   Merion - use 365 day calendars.
 *        2/03/06   Cordillera - add custom member restrictions that correspond to the custom hotel restrictions (checkCordillera).
 *        2/01/06   Cordillera - remove custom to change days in advance based on course.
 *        1/30/06   Do not allow member to access a tee time if not part of it when names are not displayed (config option).
 *       11/18/05   Ritz-Carlton - use 365 day calendars.
 *       11/04/05   Cherry Hills - custom member restrictions.
 *       10/31/05   Potowomut - do not allow members to access tee times on the day of (today).
 *       10/26/05   Send lottery requests to Member_mlottery so member can select to view other request or start a new request.
 *       10/24/05   Allow for 20 courses and -ALL- for multi course clubs.
 *       10/16/05   Meadow Springs - do not allow multiple tee time requests on Fri, Sat or Sun.
 *       10/03/05   Add 'div' tag to form for drop-down course selector so main menus will show over the drop-down box.
 *        9/08/05   Mission Viejo - do not allow members to access tee times on the day of (today).
 *        9/08/05   If days in adv is zero and the current time is less than the adv time, do not allow access.
 *        9/06/05   Hartefeld National - do not allow members to access tee times on the day of (today).
 *        7/20/05   Medinah - change ARR members from 30 days adv to 1 month adv.
 *        7/20/05   Forest Highlands - custom, only allow 5 days in advance for members to view tee sheets.
 *        7/12/05   Oakmont CC - do not allow members to make/change times on weekends more than 7 days
 *                               in advance (they changed their minds on this).
 *        7/07/05   Forest Highlands & PFC - do not allow members to access tee times on the day of (today).
 *        6/27/05   Do not allow multiple tee time request if F/B is not front or back.
 *        6/08/05   Cordillera - check member number and course to determine days in advance.
 *        5/17/05   Medinah CC - add custom to check for 'Member Times' (walk-up times).
 *                             - members can book times 7 days in advance on Course #2.
 *                             - Some members use Advanced Res Rights (ARR) to book 30 days in adv on #1 & #3.
 *        5/04/05   Pass player values to slot so it can verify that tee time has not changed.
 *        5/01/05   Add counters for # of tee sheets - in SystemUtils.
 *        4/26/05   Custom for Santa Ana Women - Increase days in advance for Tues and Fri.
 *        4/20/05   RDP Add global Gzip counters to count number of tee sheets using gzip.
 *        4/18/05   RDP Inverness - do not display the Itinerary on events if signup = no.
 *        3/25/05   RDP Custom for Oakmont and Saucon Valley - use 365 day calendars.
 *        3/27/05   Change the Milwaukee custom code to color the guest times even if within 7 days.
 *        3/22/05   Ver 5 - add course column shading to tee sheet printing when displaying all courses
 *        2/18/05   Do not display lottery in legend or use color on sheet if state = 5 (processed).
 *        1/20/05   RDP Correct the way the days in advance is adjusted for the current time.
 *        1/18/05   Ver 5 - display up to 4 events in the legend.
 *        1/10/05   Oakmont CC - change some of the Wed guest times for April, Sept & Oct.
 *        1/05/05   Ver 5 - allow member to make up to 5 consecutive tee times at once.
 *       12/09/04   Ver 5 - Add new club option to hide players' names on the tee sheet.
 *       11/29/04   Milwaukee CC - add checks for special tee times that allow Multiple Guests per member.
 *       11/17/04   Oakmont CC - add checks for special tee times that allow Multiple Guests per member.
 *       10/06/04   Ver 5 - add sub-menu support.
 *        9/22/04   RDP Add special processing for Oakmont - allow 90 days in advance for guest times.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        8/20/04   Add fivesAll option to indicate if 5-somes are supported on any course (course=ALL).
 *        7/07/04   Get the events, restrictions & lotteries in order for the legend display.
 *        6/30/04   Custom change for Old Oaks.  If lottery for date requested and state is 2,
 *                  then only display the lottery button - no tee sheet.
 *        6/17/04   Change the way we use gzip - use byte buffer so the length of output is
 *                  set properly.  Fixes problem with Compuserve 7.0.
 *        5/27/04   Add support for 4 lotteries per tee sheet.
 *        5/24/04   Change call to Member_slot a Get instead of a Post to allow 'Be Patient' page.
 *        5/06/04   RDP Make legend items (events, restrictions, lotteries) buttons so user
 *                  can click to view pop-up window describing the item.
 *        5/03/04   RDP Add an 'ALL' option for multiple course facilities.
 *        4/22/04   RDP Add custom processing for Hazeltine Natl.  Allow women 14 days in adv.
 *        3/17/04   Remove the 'jump' javascript as it will not work on MACs with IE.
 *        2/25/04   Allow access to full tee time if member is associated with unaccompanied guests.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/21/04   RDP Allow for 'Days in Adv' parms to be based on membership type.
 *        1/11/04   JAG Modified to match new color scheme
 *       12/15/02   Bob P.   Do not show member restriction in legend if show=no.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add Lottery processing.
 *       12/20/02   Bob P.   V2 Changes.
 *                           Add support for 'courseName' parm - select tee times for specific course.
 *                           Add support for Tee Time 'Blockers'.
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
import com.foretees.common.medinahCustom;
import com.foretees.common.ProcessConstants;
import com.foretees.common.cordilleraCustom;


public class Member_sheet extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   //
   //  Holidays 
   //
   private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day


 //*****************************************************
 // Process the return from Member_slot
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing
 }


 //*****************************************************
 // Process the request from Member_select
 //*****************************************************
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
   PrintWriter out;


   PreparedStatement pstmt1 = null;
   Statement stmt = null;
   Statement stmtc = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   ResultSet rs4 = null;

   ByteArrayOutputStream buf = null;
   String encodings = "";               // browser encodings

   boolean Gzip = false;        // default = no gzip

   if (req.getParameter("event") != null) {

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

   if (session == null) {

      return;
   }

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
   DaysAdv daysArray = (DaysAdv)session.getAttribute("daysArray");  // get array object for 'days in adv' from Login


   //
   //  First, check for Event call - user clicked on an event in the Legend
   //
   if (req.getParameter("event") != null) {

      String eventName = req.getParameter("event");

      displayEvent(eventName, club, out, con);             // display the information
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
   int Max7 = 7;                // days in advance for Milwaukee & Medinah Course #2 !!!!!!
   int Max14 = 14;              // days in advance for Oakmont
   int piedmontStatus = 0;      // custom piedmont status
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
   String event_rest = "";
   String bgcolor = "";
   String sfb = "";
   String msubtype = "";
   String mNum = "";
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

   String rest_recurr = "";
   String rest_name = "";
   String sfb2 = "";
   String rest_color = "";
   String rest_fb = "";
   String jumps = "";
   String num = "";

   String cordRestColor = "burlywood";        // color for Cordillera's custom member restriction


   //***********************************************************************************
   //  Lottery information storage area
   //
   //    lottery calculations done only once so we don't have to check each time while building sheet
   //
   String lottery = "";
   String lottery1 = "";
   String lottery2 = "";
   String lottery_color = "";
   String lottery_recurr = "";

   long date2 = 0;

   int lott = 0;            // lottery supported indicator
   int ldays = 0;
   int sdays = 0;
   int sdtime = 0;
   int edays = 0;
   int edtime = 0;
   int pdays = 0;
   int ptime = 0;
   int slots = 0;
   int curr_time = 0;
   int lskip = 0;
   int firstLott = 0;
   int st2 = 2;
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

   String lott3 = "";        // ditto for 3rd lottery on this day 
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

   String lott4 = "";        // ditto for 4th lottery on this day (max of 4 for now)!!!!!!
   String lcolor4 = "";
   int sdays4 = 0;
   int sdtime4 = 0;
   int edays4 = 0;
   int edtime4 = 0;
   int pdays4 = 0;
   int ptime4 = 0;
   int slots4 = 0;
   int lskip4 = 0;
   int lstate4 = 0;

   int lyear = 0;
   int lmonth = 0;
   int lday = 0;
   int advance_days = 0;       // copy of 'index' = # of days between today and the day of this sheet


   // **************** end of lottery save area ***********


   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //         one per day of week (Sun - Sat)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int oldDays3 = 0;
   int oldDays6 = 0;
   int daysT = 0;
   int hndcp = 0;
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

   boolean allow = true;
   boolean disp_hndcp = true;
   boolean lotteryOnly = false;
   boolean restrictAll = false;
   boolean arrmem = false;      // Medinah ARR member indicator
   int arrMax = 0;               // days in advance that ARR members can make tee times

   //
   //  2-some indicator used for some custom requests
   //
   boolean twoSomeOnly = false;

   //
   //  Arrays to hold the course names, member types and membership types
   //
   int cMax = 21;                                       // max of 20 courses plus allow room for '-ALL-'
   String courseName = "";
   String [] course = new String [cMax];
   String [] course_color = new String [cMax];

   int tmp_i = 0; // counter for course[], shading of course field
   int courseCount = 0; // total courses for this club
   
   // set default course colors
   course_color[0] = "#F5F5DC"; // beige (4 shades)
   course_color[1] = "#DDDDBE";
   course_color[2] = "#B3B392";
   course_color[3] = "#8B8970";
   course_color[4] = "#E7F0E7"; // greens (5 shades)
   course_color[5] = "#C6D3C6";
   course_color[6] = "#95B795";
   course_color[7] = "#648A64";
   course_color[8] = "#407340";
   course_color[9] = "#FFFFFF"; // new colors needed
   course_color[10] = "#FFFFFF";
   course_color[11] = "#FFFFFF";
   course_color[12] = "#FFFFFF";
   course_color[13] = "#FFFFFF";
   course_color[14] = "#FFFFFF";
   course_color[15] = "#FFFFFF";
   course_color[16] = "#FFFFFF";
   course_color[17] = "#FFFFFF";
   course_color[18] = "#FFFFFF";
   course_color[19] = "#FFFFFF";

   int [] fivesA = new int [cMax];                  // array to hold 5-some option for each course

   String [] mtypeA = new String [8];             // array to hold the member types

   String [] mshipA = new String [8];             // array to hold the membership types

   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat
   int [] advtimes = new int [7];                       // 0=Sun, 6=Sat
   int [] origdays = new int [7];                       // 0=Sun, 6=Sat

   //**********************************************************************************
   //  Oakmont tee time arrays for Wednesday & Friday (special guest restrictions) see also verifySlot
   //**********************************************************************************
   //
   int wedcount = 17;    // 17 tee times
   int [] wedtimes = { 820, 830, 840, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150, 1440, 1450, 1500 };

   int wedcount2 = 15;    // 15 tee times on special Wednesdays
   int [] wedtimes2 = { 820, 830, 840, 900, 910, 920, 930, 1110, 1120, 1130, 1140, 1150, 1440, 1450, 1500 };

   int wedcount3 = 17;    // 21 tee times on special Wednesdays in Sept
   int [] wedtimes3 = { 820, 830, 840, 1010, 1020, 1030, 1040, 1050, 1100, 1110, 1120, 1130, 1140, 1150, 1440, 1450, 1500 };

   int fricount = 17;    // 20 tee times
   int [] fritimes = { 820, 830, 840, 910, 920, 940, 1010, 1020, 1040, 1110, 1120, 1140, 1150, 1310, 1320, 1410, 1420 };

   boolean oakshotgun = false;    // indicator for shotgun event this day

   //**********************************************************************************

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

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
         out.println("<BR><BR>Required Parameter is Missing - Member_sheet.");
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

      if (cal_time < 100) {           // if hour is zero, then we rolled ahead 1 day

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


   //
   //  If today, then do not allow members to access any tee times within 3 hours - for the following clubs:
   //      New Canaan, Des Moines
   //
   if (index == 0 && (club.equals( "newcanaan" ) || club.equals( "desmoines" ))) {

      stop_time = cal_time + 300;        // add 3 hours to current time (already adjusted for time zone)
   }

   //
   //  If today, then do not allow members to access any tee times - for the following clubs:
   //     Forest Highlands, Hartefeld Natl., PFC, Potowomut, Tamarack, North Hills
   //
   if (index == 0 && (club.equals( "foresthighlands" ) || club.equals( "pfc" ) || club.equals( "hartefeld" ) ||
       club.equals( "potowomut" ) || club.equals( "tamarack" ) || club.equals( "bearpath" ) || club.equals( "weeburn" ))) {
     
      restrictAll = true;         // indicate no member access
   }

   //
   //  If today and after 6:00 AM, then do not allow members to access any tee times.
   //     North Hills
   //
   if (index == 0 && cal_time > 600 && club.equals( "northhills" )) {

      restrictAll = true;         // indicate no member access
   }

   //
   //  If today, or the day before and after 6:00 PM (cal_time is adjusted), then do not allow members to access any tee times.
   //     Mission Viejo & Westchester
   //
   if ((index == 0 || (index == 1 && cal_time > 1800)) &&
        (club.equals( "missionviejo" ) || club.equals( "westchester" ))) {

      restrictAll = true;         // indicate no member access
   }

   //
   //  If today, or the day before and after 5:00 PM MT, then do not allow members to access any tee times.
   //
   if ((index == 0 || (index == 1 && cal_time > 1700)) &&
        club.equals( "cordillera" )) {

      restrictAll = true;         // indicate no member access
   }

   //
   //  If Oakmont, a weekend day and more than 14 days in advance - do not allow access to any times
   //
   if (index > 14 && club.equals( "oakmont" ) && (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ))) {

      restrictAll = true;         // indicate no member access
   }

   //
   //  If Merion, a weekend day or holiday and more than 7 days in advance - do not allow access to any times
   //
   if (club.equals( "merion" ) && (index > 7 || (index == 7 && cal_time < 600)) && 
       (date == Hdate1 || date == Hdate2 || date == Hdate2b || date == Hdate3 ||
        day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ))) {

      restrictAll = true;         // indicate no member access
   }


   try {

      if (club.equals( "hazeltine" )) {

         //
         //  Get the member sub-type for this user
         //
         pstmt1 = con.prepareStatement (
            "SELECT msub_type FROM member2b WHERE username = ?");

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, user);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               msubtype = rs.getString(1);
            }

         pstmt1.close();
      }

      //
      // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
      //
      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;
      lott = parm.lottery;
      hideNames = parm.hiden;

      //
      //  Determine if this club wants to display handicaps for the members
      //
      if (parm.hndcpMemSheet == 0) {

         disp_hndcp = false;
      }

      //
      //  use the member's mship type to determine which 'days in advance' parms to use
      //
      verifySlot.getDaysInAdv(con, parm, mship);        // get the days in adv data for this member

      days1 = parm.advdays1;     // get days in adv for this type
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

      //
      //  If Meadow Springs, do not allow multiple tee time requests on Fri, Sat or Sun
      //
      if (club.equals( "meadowsprings" )) {

         if (day_name.equals( "Friday" ) || day_name.equals( "Saturday" ) || day_name.equals( "Sunday" )) {

            parm.constimesm = 1;        // no consecutive tee times (only 1 time per request)
         }
      }

      //
      //
      //
      if (club.equals( "tpctc" ) && mship.equals( "Corporate" )) {

         parm.constimesm = 2;        // alloow 2 consecutive tee times 
      }

      if (multi != 0) {           // if multiple courses supported for this club

         for (i=0; i<cMax; i++) {
            course[i] = "";       // init the course array
         }
         i = 0;

         //
         //  If club = Lakes CC or Bellerive2006, add an 'ALL' option at that start of the list
         //  else add it at the end of the list
         //
         if (club.equals( "lakes" ) || club.equals( "bellerive2006" )) {

            course[0] = "-ALL-";
            i = 1;
         }

         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && i < cMax) {

            courseName = rs.getString(1);

            course[i] = courseName;      // add course name to array
            i++;
         }
         stmt.close();
         
         courseCount = i; // save the total # of courses for later (course shading)

         if (!club.equals( "lakes" ) && !club.equals( "bellerive2006" ) && i > 1 && i < cMax) { // if not Lakes CC and more than 1 course

            course[i] = "-ALL-";
         }
           
         //
         //  Make sure we have a course name (in case we came directly from the menu for today's tee sheet)
         //
         if (courseName1.equals( "" )) {
           
            courseName1 = course[0];     // grab the first one
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
         while (i < cMax) {

            courseName = course[i];       // get a course name

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
      //   Statements to find any restrictions, events or lotteries for today
      //
      String string7b = "";
      String string7c = "";
        
      if (courseName1.equals( "-ALL-" )) {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND showit = 'Yes' ORDER BY stime";
      } else {
         string7b = "SELECT name, recurr, color FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') AND showit = 'Yes' ORDER BY stime";
      }

      if (courseName1.equals( "-ALL-" )) {
         string7c = "SELECT name, color, act_hr, act_min FROM events2b WHERE date = ? ORDER BY stime";
      } else {
         string7c = "SELECT name, color, act_hr, act_min FROM events2b WHERE date = ? " +
                    "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
      }

      PreparedStatement pstmt7b = con.prepareStatement (string7b);
      PreparedStatement pstmt7c = con.prepareStatement (string7c);

      //
      //  Scan the events, restrictions and lotteries to build the legend
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
      //  check for lotteries
      //
      if (lott != 0) {                       // if supported for this club

         String string7d = "";
           
         if (courseName1.equals( "-ALL-" )) {
           
            string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                        "FROM lottery3 WHERE sdate <= ? AND edate >= ? ORDER BY stime";
         } else {

            string7d = "SELECT name, recurr, color, sdays, sdtime, edays, edtime, pdays, ptime, slots " +
                        "FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
                        "AND (courseName = ? OR courseName = '-ALL-') ORDER BY stime";
         }

         PreparedStatement pstmt7d = con.prepareStatement (string7d);

         pstmt7d.clearParameters();          // clear the parms
         pstmt7d.setLong(1, date);
         pstmt7d.setLong(2, date);
           
         if (!courseName1.equals( "-ALL-" )) {
            pstmt7d.setString(3, courseName1);
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
                     } else {

                        if ((!lottery.equals( lott1 )) && (!lottery.equals( lott2 )) && (!lottery.equals( lott3 )) && (!lottery.equals( lott4 )) && (lott4.equals( "" ))) {

                           lott4 = lottery;
                           lcolor4 = lottery_color;
                           sdays4 = sdays;
                           sdtime4 = sdtime;
                           edays4 = edays;
                           edtime4 = edtime;
                           pdays4 = pdays;
                           ptime4 = ptime;
                           slots4 = slots;

                           if (lottery_color.equalsIgnoreCase( "default" )) {

                              lcolor4 = "#F5F5DC";
                           }
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

         if (courseName1.equals( "-ALL-" )) {

            string12 = "SELECT state FROM lreqs3 " +
                       "WHERE name = ? AND date = ?";
         } else {
            string12 = "SELECT state FROM lreqs3 " +
                       "WHERE name = ? AND date = ? AND courseName = ?";
         }

         if (!lott1.equals( "" )) {

            //
            //  Get the current time (date is correct from _select)
            //
            Calendar cal3 = new GregorianCalendar();    // get todays date
            cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time)
            cal_min = cal3.get(Calendar.MINUTE);

            curr_time = (cal_hour * 100) + cal_min;

            curr_time = SystemUtils.adjustTime(con, curr_time);   // adjust the time

            if (curr_time < 0) {          // if negative, then we went back or ahead one day

               curr_time = 0 - curr_time;        // convert back to positive value
            }

            //
            //  now check the day and time values  (advance_days = index value)
            //
            if (advance_days > sdays1) {       // if we haven't reached the start day yet

               lstate1 = 1;                    // before time to take requests

            } else {

               if (advance_days == sdays1) {   // if this is the start day

                  if (curr_time >= sdtime1) {   // have we reached the start time?

                     lstate1 = 2;              // after start time, before stop time to take requests

                  } else {

                     lstate1 = 1;              // before time to take requests
                  }
               } else {                        // we are past the start day

                  lstate1 = 2;                 // after start time, before stop time to take requests
               }

               if (advance_days == edays1) {   // if this is the stop day

                  if (curr_time >= edtime1) {   // have we reached the stop time?

                     lstate1 = 3;              // after start time, before stop time to take requests
                  }
               }

               if (advance_days < edays1) {   // if we are past the stop day

                  lstate1 = 3;                // after start time, before stop time to take requests
               }
            }

            if (lstate1 == 3) {                // if we are now in state 3, check for state 4

               if (advance_days == pdays1) {   // if this is the process day

                  if (curr_time >= ptime1) {    // have we reached the process time?

                     lstate1 = 4;              // after process time
                  }
               }

               if (advance_days < pdays1) {   // if we are past the process day

                  lstate1 = 4;                // after process time
               }
            }

            if (lstate1 == 4) {                // if we are now in state 4, check for pending approval

               PreparedStatement pstmt12 = con.prepareStatement (string12);

               pstmt12.clearParameters();        // clear the parms
               pstmt12.setString(1, lott1);
               pstmt12.setLong(2, date);

               if (!courseName1.equals( "-ALL-" )) {
                  pstmt12.setString(3, courseName1);
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

            //
            //  Custom change for Old Oaks CC
            //    If lottery for this day, only display the lottery button - no table or tee times
            //
            if (club.equals( "oldoaks" ) && lstate1 == 2) {   // old oaks and members can make lottery req's

               lotteryOnly = true;
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

                  if (curr_time >= sdtime2) {   // have we reached the start time?

                     lstate2 = 2;              // after start time, before stop time to take requests

                  } else {

                     lstate2 = 1;              // before time to take requests
                  }
               } else {                        // we are past the start day

                  lstate2 = 2;                 // after start time, before stop time to take requests
               }

               if (advance_days == edays2) {   // if this is the stop day

                  if (curr_time >= edtime2) {   // have we reached the stop time?

                     lstate2 = 3;              // after start time, before stop time to take requests
                  }
               }

               if (advance_days < edays2) {   // if we are past the stop day

                  lstate2 = 3;                // after start time, before stop time to take requests
               }
            }

            if (lstate2 == 3) {                // if we are now in state 3, check for state 4

               if (advance_days == pdays2) {   // if this is the process day

                  if (curr_time >= ptime2) {    // have we reached the process time?

                     lstate2 = 4;              // after process time
                  }
               }

               if (advance_days < pdays2) {   // if we are past the process day

                  lstate2 = 4;                // after process time
               }
            }

            if (lstate2 == 4) {                // if we are now in state 4, check for pending approval

               PreparedStatement pstmt12 = con.prepareStatement (string12);

               pstmt12.clearParameters();        // clear the parms
               pstmt12.setString(1, lott2);
               pstmt12.setLong(2, date);

               if (!courseName1.equals( "-ALL-" )) {
                  pstmt12.setString(3, courseName1);
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

                  if (curr_time >= sdtime3) {   // have we reached the start time?

                     lstate3 = 2;              // after start time, before stop time to take requests

                  } else {

                     lstate3 = 1;              // before time to take requests
                  }
               } else {                        // we are past the start day

                  lstate3 = 2;                 // after start time, before stop time to take requests
               }

               if (advance_days == edays3) {   // if this is the stop day

                  if (curr_time >= edtime3) {   // have we reached the stop time?

                     lstate3 = 3;              // after start time, before stop time to take requests
                  }
               }

               if (advance_days < edays3) {   // if we are past the stop day

                  lstate3 = 3;                // after start time, before stop time to take requests
               }
            }

            if (lstate3 == 3) {                // if we are now in state 3, check for state 4

               if (advance_days == pdays3) {   // if this is the process day

                  if (curr_time >= ptime3) {    // have we reached the process time?

                     lstate3 = 4;              // after process time
                  }
               }

               if (advance_days < pdays3) {   // if we are past the process day

                  lstate3 = 4;                // after process time
               }
            }

            if (lstate3 == 4) {                // if we are now in state 4, check for pending approval

               PreparedStatement pstmt12 = con.prepareStatement (string12);

               pstmt12.clearParameters();        // clear the parms
               pstmt12.setString(1, lott3);
               pstmt12.setLong(2, date);

               if (!courseName1.equals( "-ALL-" )) {
                  pstmt12.setString(3, courseName1);
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
         }   // end of IF lott3
           
         if (!lott4.equals( "" )) {

            //
            //  check the day and time values
            //
            if (advance_days > sdays4) {       // if we haven't reached the start day yet

               lstate4 = 1;                    // before time to take requests

            } else {

               if (advance_days == sdays4) {   // if this is the start day

                  if (curr_time >= sdtime4) {   // have we reached the start time?

                     lstate4 = 2;              // after start time, before stop time to take requests

                  } else {

                     lstate4 = 1;              // before time to take requests
                  }
               } else {                        // we are past the start day

                  lstate4 = 2;                 // after start time, before stop time to take requests
               }

               if (advance_days == edays4) {   // if this is the stop day

                  if (curr_time >= edtime4) {   // have we reached the stop time?

                     lstate4 = 3;              // after start time, before stop time to take requests
                  }
               }

               if (advance_days < edays4) {   // if we are past the stop day

                  lstate4 = 3;                // after start time, before stop time to take requests
               }
            }

            if (lstate4 == 3) {                // if we are now in state 3, check for state 4

               if (advance_days == pdays4) {   // if this is the process day

                  if (curr_time >= ptime4) {    // have we reached the process time?

                     lstate4 = 4;              // after process time
                  }
               }

               if (advance_days < pdays4) {   // if we are past the process day

                  lstate4 = 4;                // after process time
               }
            }

            if (lstate4 == 4) {                // if we are now in state 4, check for pending approval

               PreparedStatement pstmt12 = con.prepareStatement (string12);

               pstmt12.clearParameters();        // clear the parms
               pstmt12.setString(1, lott4);
               pstmt12.setLong(2, date);

               if (!courseName1.equals( "-ALL-" )) {
                  pstmt12.setString(3, courseName1);
               }

               rs = pstmt12.executeQuery();

               if (!rs.next()) {             // if none waiting approval

                  lstate4 = 5;              // state 5 - after process & approval time

               } else {                     // still some reqs waiting

                  templstate = rs.getInt(1);  // get its state

                  if (templstate == 5) {      // if we processed already (some not assigned)

                     lstate4 = 5;
                  }
               }
               pstmt12.close();

            }
         }   // end of IF lott4
      }      // end of IF lottery supported

      //
      //  Special processing for Oakmont CC - check if there is a Shotgun Event for today (if Friday)
      //
      if (club.equals( "oakmont" ) && day_name.equals("Friday")) {

         PreparedStatement pstmtc = con.prepareStatement (
            "SELECT dd " +
            "FROM teecurr2 " +
            "WHERE date = ? AND event_type = ?");

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setLong(1, date);
         pstmtc.setInt(2, shotgun);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            oakshotgun = true;         // shotgun event today
         }
         pstmtc.close();
      }

      //
      //  Special processing for Milwaukee CC - check if there are back tees
      //                                        after noon, and it is a weekday (Tues - Fri)
      //
      boolean mccGuestDay = false;
        
      if (club.equals( "milwaukee" ) && (day_name.equals("Tuesday") || day_name.equals("Wednesday") ||
          day_name.equals("Thursday") || day_name.equals("Friday"))) {

         PreparedStatement pstmtc = con.prepareStatement (
            "SELECT dd " +
            "FROM teecurr2 " +
            "WHERE date = ? AND fb = 1 AND time > 1200 AND time < 1431");  // this day, back tee, noon to 2:30

         pstmtc.clearParameters();        // clear the parms
         pstmtc.setLong(1, date);
         rs = pstmtc.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mccGuestDay = true;         // Guest Times exist for this day
         }
         pstmtc.close();
      }

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
      // if Oakmont or Saucon Valley or Ritz-Carlton or Merion or Oakland Hills
      //
      if (club.equals( "oakmont" ) || club.equals( "sauconvalleycc" ) || club.equals( "ritzcarlton" ) ||
          club.equals( "ccrockies" ) ||
          (club.equals( "oaklandhills" ) && courseName1.equals( "South Course" )) ||
          (club.equals( "merion" ) && courseName1.equals( "East" )) || (club.equals( "catamount" ) && mship.equals( "Founder" ))) {

         // include files for dynamic calendars
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
         out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
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

      if (club.equals( "interlachen" ) || club.equals( "noaks" )) {

         max = 28;        // Interlachen and North Oaks only want 28 days
      }

      if (club.equals( "milwaukee" ) || club.equals( "inverness" ) || club.equals( "nakoma" )) {

         max = 90;        // allow 90 days for guest times
      }

      if (club.equals( "nakoma" )) {

         Max7 = 7;        // allow 7 days in advance
      }
        
      if (club.equals( "inverness" )) {

         Max7 = 5;        // allow 5 days in advance

         if (mship.startsWith( "Corporate" )) {

            Max7 = 4;        // allow 4 days in advance
         }
      }

      if (club.equals( "foresthighlands" )) {        // per Pro's request

         max = 6;              // 5 days in advance (+ today)
      }

      if (club.equals( "northhills" )) {    

         max = 30;        // North Hills - allow 30 days for guest times
      }

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

         if (cal_time < 100) {           // if hour is zero, then we rolled ahead 1 day

            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal2.add(Calendar.DATE,1);                     // get next day's date

            year2 = cal2.get(Calendar.YEAR);
            month2 = cal2.get(Calendar.MONTH);
            day2 = cal2.get(Calendar.DAY_OF_MONTH);
            day_num2 = cal2.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

         } else {                        // we rolled back 1 day

            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal2.add(Calendar.DATE,-1);                     // get yesterday's date

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

/*
      //
      //  If Medinah check for ARR member - if so, change days in adv to 1 month (normally 2 days)
      //
      if (club.equals( "medinahcc" )) {

         arrmem = medinahCustom.checkARRmem(user, con, mship, mtype);

         if (arrmem == true) {

            days1 = numDays;           // ARR mem - change to 1 month
            days2 = numDays;
            days3 = numDays;
            days4 = numDays;
            days5 = numDays;
            days6 = numDays;
            days7 = numDays;

            arrMax = numDays;            // save max for ARR members

            if (max < numDays+1) {

               max = numDays + 1;           // # of days to display on calendar
            }
         }
      }
*/

      //
      //  If Merion's East course change the days in adv to 365
      //
      if (club.equals( "merion" ) && courseName1.equals( "East" )) {

            days1 = 365;          
            days2 = 365;
            days3 = 365;
            days4 = 365;
            days5 = 365;
            days6 = 365;
            days7 = 365;
      }

      //
      //  If Oakland Hills or CC of the Rockies change the days in adv to 365
      //
      if ((club.equals( "oaklandhills" ) && courseName1.equals( "South Course" )) || club.equals( "ccrockies" )) {

            days1 = 365;
            days2 = 365;
            days3 = 365;
            days4 = 365;
            days5 = 365;
            days6 = 365;
            days7 = 365;
      }

      //
      //  If Catamount Ranch change the days in adv to 180
      //
      if (club.equals( "catamount" )) {

            days1 = 180;
            days2 = 180;
            days3 = 180;
            days4 = 180;
            days5 = 180;
            days6 = 180;
            days7 = 180;
      }

      //
      //  If North Hills change the days in adv to 30
      //
      if (club.equals( "northhills" )) {

            days1 = 30;
            days2 = 30;
            days3 = 30;
            days4 = 30;
            days5 = 30;
            days6 = 30;
            days7 = 30;
      }

      //
      //   Santa Ana Custom - increase the days in adv for Women - Tues = 4, Fri = 7
      //
      if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {

         oldDays3 = days3;     // save original days    
         oldDays6 = days6;
         days3 = 4;          // Tues = 4 days in advance (starting at 7:30 AM)
         days6 = 7;          // Fri = 7 days in advance (starting at 7:30 AM)
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

      if (!club.equals( "northhills" )) {

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
      }

      if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {

         advdays[2] = oldDays3;     // restore original days
         advdays[5] = oldDays6;
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
      //  If Hazeltine, check if days in adv should change
      //
      if (club.equals( "hazeltine" )) {
        
         if (day_num == 3) {     // if Tuesday
           
            //
            //  If a Female and sub-type is 'After Hours', '9 holer', or combo, then set Tuesdays to 14 days adv.
            //
            if ((mtype.equals("Adult Female")) && (msubtype.equals("After Hours") || msubtype.equals("9 Holer") ||
                msubtype.startsWith("AH-") || msubtype.equals("9/18 Holer"))) {

               days = 14;      // set 14 days in advance for Tuesdays (all 'After Hours' and 9-Holers)
            }

         } else {
           
            if (day_num == 5) {     // if Thursday

               if ((mtype.equals("Adult Female")) && (msubtype.equals("18 Holer") || msubtype.startsWith("AH-9/18") ||
                   msubtype.startsWith("AH-18") || msubtype.equals("9/18 Holer"))) {

                  days = 14;      // set 14 days in advance for Thursdays (all 18-Holers)
               }
            }  
         }
      }

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         //
         //  use 2 forms so you can switch by clicking either a course or a date
         //
         out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\" name=\"cform\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"i" + index2 + "\" value=\"\">");   // use current date

         out.println("<div id=\"awmobject1\">");        // allow menus to show over this box

         i = 0;
         courseName = course[i];      // get first course name from array

         out.println("<b>Course:</b>&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"course\" onChange=\"document.cform.submit()\">");

         while ((!courseName.equals( "" )) && (i < cMax)) {

            if (courseName.equals( courseName1 )) {
               out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
            } else {
               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
            i++;
            if (i < cMax) {
               courseName = course[i];      // get course name from array
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
      // if Oakmont or Saucon Valley or Ritz-Carlton or Merion or Oakland Hills use 365 day calendar
      //
      if (club.equals( "oakmont" ) || club.equals( "sauconvalleycc" ) || club.equals( "ritzcarlton" ) ||
          club.equals( "ccrockies" ) ||
          (club.equals( "oaklandhills" ) && courseName1.equals( "South Course" )) ||
          (club.equals( "merion" ) && courseName1.equals( "East" )) || (club.equals( "catamount" ) && mship.equals( "Founder" ))) {

         out.println("<font size=\"2\">");

         // this is the form that gets submitted when the user selects a day from the calendar
         out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
         out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
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

        out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
        out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");


         //
         // end of calendar row
         //
         out.println("</td></tr></table>");
         out.println("</form>");

      } else {          // club is NOT Oakmont or Saucon Valley or Ritz-Carlton or Merion

         //
         //  start a new form for the dates so you can switch by clicking either a course or a date
         //
         out.println("<form action=\"/" +rev+ "/servlet/Member_sheet\" method=\"post\" target=\"bot\">");
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

            while (count < max) {                 // start with today, go to end of month or 30 days

               if (day2 <= numDays ) {

                  //
                  //  If Milwaukee or Medinah Course #2, then 7 days in advance always
                  //
                  if (club.equals( "milwaukee" ) || club.equals( "inverness" ) || club.equals( "nakoma" ) || 
                      (club.equals( "medinahcc" ) && courseName1.equals( "No 2" ))) {
                     d = 0;
                     if (count <= Max7) {           // if within days in advance (all days are same for oakmont)
                        d = 1;
                     }
                  } else {
                        d = daysArray.days[count];      // get 'days in advance' value for this day (set by Login)
                  }

                  if (d == 1) {                  // color the buttons for 'days in advance' number of days

                     if (club.equals( "northhills" ) && count > 7) {   // if North Hills CC
                        out.println("<td align=\"center\"><font size=\"2\"><b>");          // sienna
                        out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#A0522D\"></b></font></td>");
                     } else {
                        out.println("<td align=\"center\"><font size=\"2\"><b>");                      // limegreen
                        out.println("<input type=\"submit\" value=" + day2 + " name=\"i" + count + "\" style=\"background:#32CD32\"></b></font></td>");
                     }

                  } else {
                     //
                     //  determine if a lottery is setup for this day, and if the signup is longer than 'd' days
                     //
                     if (lott != 0) {                 // if lottery supported by this club

                        if (d == 2) {                // if a lottery was found for this day

                           out.println("<td align=\"center\"><font size=\"2\"><b>");          // sienna
                           out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#A0522D\"></b></font></td>");
                        } else {
                           out.println("<td align=\"center\"><font size=\"2\"><b>");             // lightgrey
                           out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#D3D3D3\"></b></font></td>");
                        }
                     } else {       // lottery not supported

                        if (club.equals( "milwaukee" )) {   // if Milwaukee CC
                           if (col == 6 || col == 0 || col == 1) {    // if Sat, Sun or Mon (no access)
                              out.println("<td align=\"center\"><font size=\"2\"><b>");             // lightgrey
                              out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#D3D3D3\"></b></font></td>");
                           } else {
                              out.println("<td align=\"center\"><font size=\"2\"><b>");          // sienna
                              out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#A0522D\"></b></font></td>");
                           }
                        } else {    // not milwaukee
                           if (club.equals( "northhills" )) {   // if North Hills CC
                              out.println("<td align=\"center\"><font size=\"2\"><b>");          // sienna
                              out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#A0522D\"></b></font></td>");
                           } else {
                              out.println("<td align=\"center\"><font size=\"2\"><b>");             // lightgrey
                              out.println("<input type=\"submit\" value=\"" + day2 + "\" name=\"i" + count + "\" style=\"background:#D3D3D3\"></b></font></td>");
                           }
                        }
                     }
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
      if (index2 <= days || lstate1 == 2 || lstate2 == 2 || lstate3 == 2 || lstate4 == 2 || 
          club.equals( "oakmont" ) || mccGuestDay == true) {

         out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"680\">");
         out.println("<tr><td align=\"left\"><font color=\"#FFFFFF\" size=\"2\">");

         out.println("<b>Instructions:</b>  To select a tee time, just click on the button containing the time (1st column). ");
         out.println(" Special Events and Restrictions, if any, are colored (see legend below). ");
         out.println(" To display a different day's tee sheet, select the date from the calendar above.");
         if (club.equals( "oakmont" ) && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {
            out.println("<br>Times when Multiple Guests are allowed are indicated by the green time button.");
         }
         if (club.equals( "milwaukee" ) && mccGuestDay == true) {
            out.println("<br>Times when Multiple Guests are allowed are indicated by the green time button.");
         }
         if (club.equals( "bearpath" )) {
            out.println("<br>Member-only times are indicated by the green time button (if any on this date).");
         }
         if (parm.constimesm > 1) {        // if consecutive tee times supported
            out.println("<br>To make multiple consecutive tee times, select the number of tee times next to the ");
            out.println("earliest time desired.  Then select that time.  The following time(s) must be available.");
         }

      } else {

         out.println("<table cols=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" width=\"600\">");
         out.println("<tr><td align=\"left\"><font color=\"#FFFFFF\" size=\"2\">");
         out.println("<b>Note:</b>&nbsp;&nbsp;This date is not yet available for members to make tee times.");
         out.println(" You are allowed to view this sheet for planning purposes only.");
      }
      out.println("</font></td></tr></table>");

      out.println("<font size=\"4\">");
      out.println("<p>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b>");

      if (!courseName1.equals( "" )) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + courseName1 + "</b>");
      }

      out.println("</p></font><font size=\"2\">");

      //
      //  Display a note if members are not allowed to access tee times today
      //
      if (restrictAll == true) {

         if (club.equals( "oakmont" )) {    // if Oakmont (must be weekend more than 14 days in advance)

            out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the Golf Shop for tee times on this day.</button><br>");

         } else {

            out.println("<button type=\"button\" style=\"background:#F5F5DC\">Please contact the Golf Shop for tee times today.</button><br>");
         }
      }


      if (!event1.equals( "" )) {

         out.println("<b>Tee Sheet Legend</b> (click on Event button to view info)");
           
      } else {
        
         out.println("<b>Tee Sheet Legend</b>");
      }

      out.println("</font><font size=\"1\"><br>");

      if (!event1.equals( "" )) {

         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Member_sheet?event=" +event1+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no');return false;\">");
         out.println("<button type=\"button\" style=\"background:" + ecolor1 + "\">" + event1 + "</button></a>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

         if (!event2.equals( "" )) {

            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Member_sheet?event=" +event2+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no');return false;\">");
            out.println("<button type=\"button\" style=\"background:" + ecolor2 + "\">" + event2 + "</button></a>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

            if (!event3.equals( "" )) {

               out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Member_sheet?event=" +event3+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no');return false;\">");
               out.println("<button type=\"button\" style=\"background:" + ecolor3 + "\">" + event3 + "</button></a>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;");

               if (!event4.equals( "" )) {

                  out.println("<a href=\"javascript:void(0)\" onClick=\"window.open('/" +rev+ "/servlet/Member_sheet?event=" +event4+ "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no');return false;\">");
                  out.println("<button type=\"button\" style=\"background:" + ecolor4 + "\">" + event4 + "</button></a>");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
               }
            }
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

      //
      //  Custom check for Cordillera Canned Restrictions - add to legend
      //
      if (club.equals( "cordillera" ) && date > 20060413 && date < 20061030) {

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("<button type=\"button\" style=\"background:" +cordRestColor+ "\">Lodge Tee Times</button>");
      }

      boolean lottDisp = false;

      if (!lott1.equals( "" ) && lstate1 < 5) {

         out.println("<button type=\"button\" style=\"background:" + lcolor1 + "\">" +lott1+ "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         lottDisp = true;
      }

      if (!lott2.equals( "" ) && lstate2 < 5) {

         out.println("<button type=\"button\" style=\"background:" + lcolor2 + "\">" +lott2+ "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         lottDisp = true;
      }

      if (!lott3.equals( "" ) && lstate3 < 5) {

         out.println("<button type=\"button\" style=\"background:" + lcolor3 + "\">" +lott3+ "</button>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
         lottDisp = true;
      }

      if (!lott4.equals( "" ) && lstate4 < 5) {

         out.println("<button type=\"button\" style=\"background:" + lcolor4 + "\">" +lott4+ "</button>");
         lottDisp = true;
      }

      if (!event1.equals( "" ) || !rest1.equals( "" ) || lottDisp == true || (club.equals( "cordillera" ) && date > 20060413 && date < 20061030)) {

         out.println("<br>");
      }

      //
      //  If Old Oaks CC and this is a 'Lottery Only" Day, then only display the lottery button.
      //
      if (lotteryOnly == false) {
        
         out.println("<b>F/B:</b>&nbsp;&nbsp;&nbsp;&nbsp;F = Front Nine,&nbsp;&nbsp;&nbsp;B = Back Nine,&nbsp;&nbsp;&nbsp;O = Open (for cross-overs),&nbsp;&nbsp;&nbsp;S = Shotgun Event<br>");

         out.println("<b>C/W:</b>&nbsp;&nbsp;&nbsp;&nbsp;");

         for (int ic=0; ic<16; ic++) {

            if (!parmc.tmodea[ic].equals( "" )) {
               out.println(parmc.tmodea[ic]+ " = " +parmc.tmode[ic]+ "&nbsp;&nbsp;&nbsp;");
            }
         }
         out.println("(__9 = 9 holes)");

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

               out.println("<td align=\"center\">");
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

               out.println("<td align=\"center\">");
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

               out.println("<td align=\"center\">");
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

               out.println("<td align=\"center\">");
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

                  out.println("<td align=\"center\">");
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
                        "FROM teecurr2 WHERE date = ? ORDER BY time, courseName, fb";
         } else {
            stringTee = "SELECT * " +
                        "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb";
         }
         PreparedStatement pstmt = con.prepareStatement (stringTee);

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt

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
            lottery = rs.getString("lottery");
            courseNameT = rs.getString("courseName");
            blocker = rs.getString("blocker");
            rest5 = rs.getString("rest5");
            bgcolor5 = rs.getString("rest5_color");
            lottery_color = rs.getString("lottery_color");
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
               while (i < cMax) {
                  if (courseNameT.equals( course[i] )) {
                     fives = fivesA[i];          // get the 5-some option for this course
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
              
            if (club.equals( "piedmont" ) && tee_time < 1000 &&
                (day_name.equals( "Saturday" ) || day_name.equals( "Sunday" ))) {

               piedmontStatus = verifySlot.checkPiedmont(date, tee_time, day_name);     // check if special time

               if (piedmontStatus == 3) {

                  twoSomeOnly = true;             // Only allow 2-somes for this tee time
               }
            }

            //
            //  if Westchester CC, check if tee time is for 2-somes only on the South course
            //
            if (club.equals( "westchester" ) && courseNameT.equalsIgnoreCase( "south" )) {

               twoSomeOnly = verifySlot.checkWestchester(date, tee_time);     // check if special time
            }

            //
            //  if Woodway CC, check if tee time is for 2-somes only
            //
            if (club.equals( "woodway" )) {

               twoSomeOnly = verifySlot.checkWoodway(date, tee_time);     // check if special time
            }

            //
            //  if Hudson National, check if tee time is for 2-somes only
            //
            if (club.equals( "hudsonnatl" )) {

               twoSomeOnly = verifySlot.checkHudson(date, tee_time, day_name);     // check if special time
            }

            //
            //  if The Stanwich Club, check if tee time is for 2-somes only
            //
            if (club.equals( "stanwichclub" )) {

               twoSomeOnly = verifySlot.checkStanwich(date, tee_time, day_name);     // check if special time
            }

            //
            //  New Canaan, check if tee time is for 2-somes only
            //
            if (club.equals( "newcanaan" )) {

               twoSomeOnly = verifySlot.checkNewCanaan(date, tee_time, day_name);     // check if special time
            }

            //
            //  Apawamis, check if tee time is for 2-somes only
            //
            if (club.equals( "apawamis" )) {

               twoSomeOnly = verifySlot.checkApawamis(date, tee_time, day_name);     // check if special time
            }

            //
            //  Wee Burn, check if tee time is for 2-somes only
            //
            if (club.equals( "weeburn" )) {

               twoSomeOnly = verifySlot.checkWeeburn(date, tee_time, day_name);     // check if special time
            }


            //
            //****************************************************************************************
            //  Hide Names Feature - if club opts to hide the member names, then hide all names
            //                       except for any group that this user is part of.
            //****************************************************************************************
            //
            hideN = 0;           // default to 'do not hide names'
            hideSubmit = 0;      // default to 'do not hide the submit button'
              
            if (club.equals( "merion" )) {    

               //
               //  Merion - hide names whenever there is a guest in the tee time (and its not this member's time)
               //
               if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {
  
                  hideN = 1;        // hide names in this guest group

                  if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) ||
                      user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                     hideN = 0;     // do not hide this group
                  }
               }

            } else {
              
               if (hideNames > 0) {

                  hideN = 1;        // hide names in this group

                  if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) ||
                      user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {    // if user is in this group

                     hideN = 0;                // do not hide this group

                  } else {    // user not part of this tee time

                     if (club.equals( "foresthighlands" )) {
                       
                        //
                        //  Forest Highlands - do not hide names unless the tee time is full (4-somes only!) 
                        //
                        if (player1.equals( "" ) || player2.equals( "" ) || player3.equals( "" ) || player4.equals( "" )) { 
                          
                           hideN = 0;                // do not hide this group
                        }

                     } else {
                       
                        //
                        //  Check if any members exist in this tee time.  If so, then do not allow this member access
                        //
                        if (!user1.equals("") || !user2.equals("") || !user3.equals("") ||
                            !user4.equals("") || !user5.equals("")) {

                           hideSubmit = 1;                // do not allow access to tee time
                        }
                     }
                  }
               }
            }

            //
            //  if not event, then check for lottery time (events override lotteries)
            //
            //  determine if we should skip this slot - display only one slot per lottery before its processed
            //
            lskip = 0;                      // init skip switch
            ldays = 0;                      // init lottery days value
            firstLott = 0;                  // init first lottery flag

            if (blocker.equals( "" )) {     // check for lottery if tee time not blocked

               if (event.equals("") && !lottery.equals("")) {

                  if (lottery.equals( lott1 )) {

                     ldays = sdays1;                // save lottery's advance days

                     if (lstate1 < 5) {             // if lottery has not been processed (times allotted)

                        if (lskip1 != 0) {          // if we were here already

                           lskip = 1;               // skip this slot
                        }
                        lskip1 = 1;                 // make sure its set now
                        firstLott = 1;              // indicate 1st lottery time
                     } else {
                        lottery_color = "";         // already processed, do not use color
                     }
                  } else {

                     if (lottery.equals( lott2 )) {

                        ldays = sdays2;                // save lottery's advance days

                        if (lstate2 < 5) {

                           if (lskip2 != 0) {          // if we were here already

                              lskip = 1;               // skip this slot
                           }
                           lskip2 = 1;                 // make sure its set now
                           firstLott = 1;              // indicate 1st lottery time
                        } else {
                           lottery_color = "";         // already processed, do not use color
                        }
                     } else {

                        if (lottery.equals( lott3 )) {

                           ldays = sdays3;                // save lottery's advance days

                           if (lstate3 < 5) {

                              if (lskip3 != 0) {          // if we were here already

                                 lskip = 1;               // skip this slot
                              }
                              lskip3 = 1;                 // make sure its set now
                              firstLott = 1;              // indicate 1st lottery time
                           } else {
                              lottery_color = "";         // already processed, do not use color
                           }
                        } else {

                           if (lottery.equals( lott4 )) {

                              ldays = sdays4;                // save lottery's advance days

                              if (lstate4 < 5) {

                                 if (lskip4 != 0) {          // if we were here already

                                    lskip = 1;               // skip this slot
                                 }
                                 lskip4 = 1;                 // make sure its set now
                                 firstLott = 1;              // indicate 1st lottery time
                              } else {
                                 lottery_color = "";         // already processed, do not use color
                              }
                           }
                        }
                     }
                  }
               }          // end of IF lottery
            }          // end of IF blocker

            if (blocker.equals( "" ) && lskip == 0) {    // continue if tee time not blocked & not lottery - else skip

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

                  // an event wasn't specified, lets check for lotterys
                  if (!lottery.equals("") && !lottery_color.equals("")) {
                     bgcolor = lottery_color;
                  } else {
                     if (!rest.equals("")) {
                        bgcolor = rcolor;
                     }
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
                  sfb = (!hole.equals("")) ? hole : "S";            // there's an event and its type is 'shotgun'
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
               //  If Milwaukee CC, check for Special Guest Time 
               //
               boolean mcctime = false;

               if (club.equals("milwaukee") && mccGuestDay == true) {

                  if (fb == 1 && tee_time > 1200 && tee_time < 1431) {

                     mcctime = true;      // special guest time - make it a green button
                  }
               }
                 
               //
               //  If Santa Ana CC, check for Special Women's Time
               //
               boolean satime = false;

               if (club.equals( "santaana" ) && mtype.equals( "Adult Female" )) {

                  if (day_name.equals("Tuesday") && index2 > 2 && index2 < 5 &&
                      ((tee_time > 744 && tee_time < 946 && fb == 0) || 
                       (tee_time > 744 && tee_time < 831 && fb == 1))) {

                     satime = true;      // special time - allow it
                  }

                  if (day_name.equals("Friday") && index2 > 5 && index2 < 8 &&
                      ((tee_time > 736 && tee_time < 1001 && fb == 0) ||
                       (tee_time > 751 && tee_time < 816 && fb == 1))) {

                     satime = true;      // special time - allow it
                  }
               }

               //
               //  If Bearpath CC check for member-only times
               //
               boolean beartime = false;

               if (club.equals("bearpath")) {

                  beartime = verifySlot.checkBearpathGuests(day_name, date, tee_time, index2);
               }
                 
               //
               //  If Oakmont CC check for selected times on Wed and Fri that are available
               //
               boolean oaktime = false;

               if (club.equals("oakmont")) {

                  //
                  //  If Wed or Fri, and not a shotgun today, check if this time is one when multiple guests are allowed.
                  //  If so, make the submit button green and allow this tee time.
                  //
                  if (event.equals("") && oakshotgun == false && (day_name.equals("Wednesday") || day_name.equals("Friday"))) {

                     if (index2 > Max14) {                // Wed or Fri and more than 14 days in advance

                        allow = false;                     // do not allow this time unless ok below
                     }

                     //
                     //  Check the time of this tee time against those allowed for this day
                     //
                     if (day_name.equals( "Friday" )) {       // if Friday

                        oakloop1:
                        for (int oak = 0; oak < fricount; oak++) {

                           if (tee_time == fritimes[oak]) {

                              oaktime = true;       // tee time is ok
                              allow = true;
                              break oakloop1;
                           }
                        }

                     } else {    // Wednesday

                        if (month == 9) {

                           oakloop4:
                           for (int oak = 0; oak < wedcount3; oak++) {       // use special Wed times for Sept.

                              if (tee_time == wedtimes3[oak]) {

                                 oaktime = true;       // tee time is ok
                                 allow = true;
                                 break oakloop4;
                              }
                           }

                        } else {

                           if ((month == 4 && day == 20) || (month == 4 && day == 27) || month == 10) {

                              oakloop2:
                              for (int oak = 0; oak < wedcount2; oak++) {       // use special Wed times

                                 if (tee_time == wedtimes2[oak]) {

                                    oaktime = true;       // tee time is ok
                                    allow = true;
                                    break oakloop2;
                                 }
                              }

                           } else {

                              oakloop3:
                              for (int oak = 0; oak < wedcount; oak++) {      // check normal Wed times

                                 if (tee_time == wedtimes[oak]) {

                                    oaktime = true;       // tee time is ok
                                    allow = true;
                                    break oakloop3;
                                 }
                              }

                              if (allow == false) {

                                 // check for special day - Wed July 6 after 12:30 and back tee

                                 if (date == 20050706 && tee_time > 1229 && fb == 1) {

                                    oaktime = true;       // tee time is ok
                                    allow = true;
                                 }
                              }
                           }
                        }
                     }
                  }
               } 

               //
               //  Check if members are not allowed to access tee times today
               //
               if (restrictAll == true) {

                  allow = false;            // not today!
               }

               if (allow == true) {     // if still ok, check more

                  //
                  // if restriction for this slot and its not the first time for a lottery, check restriction for this member
                  //
                  if (!rest.equals("") && firstLott == 0) {

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

               //
               //  If Medinah CC check for Member Time (walk-up time) and days in adv for Course #2
               //
               if (allow == true && club.equals( "medinahcc" )) {

                  //
                  //  8:00 & 8:12 tee times on Course #3 on Tues are reserved for Spouses
                  //
                  if (courseNameT.equals( "No 3" ) && day_name.equals( "Tuesday" ) &&
                      (tee_time == 800 || tee_time == 812)) {

                     if (!mtype.equals( "Regular Spouse" ) && !mtype.equals( "Jr. Spouse" ) &&
                         !mtype.equals( "Reg Prob Spouse" )) {

                        allow = false;                     // 8:00 & 8:12 reserved for spouses

                     } else {       // spouse - these times available 6 days in adv starting at 7:00 AM

                        if (index2 > 6 || (index2 == 6 && cal_time < 700)) {

                           allow = false;
                             
                        } else {
                          
                           tempDays = 6;       //  force days in adv to 6 days for this time only
                        }
                     }

                  } else {

                     //
                     // Medinah, not #3 on Tues at 8:00 or 8:12
                     //
                     boolean medinahMemTime = medinahCustom.checkMemTime(courseNameT, day_name, tee_time, date);

                     if (medinahMemTime == true) {

                        allow = false;     // Member Time - do not allow

                     } else {

                        if (courseNameT.equals( "No 2" )) {       // if Course #2

                           if (index2 > Max7) {                  // more than 7 days in advance?

                              allow = false;                     // Max of 7 days in adv for Course #2
                           }

                        } else {   
                          
                           //
                           //  #1 or #3 and NOT a Member Time - do not allow spouses anytime other than those above
                           //
                           if (mtype.equals( "Regular Spouse" ) || mtype.equals( "Jr. Spouse" ) ||
                                mtype.equals( "Reg Prob Spouse" ) || mtype.equals( "NR Spouse" ) ||
                                mtype.equals( "Soc Spouse" )) {

                              allow = false;                     // no spouses can make times
                           }
                        }
                     }
                  }
                    
/*
                  //
                  //  if still ok, course #3 and ARR member - check for Social Reg - if so, only 2 days in adv allowed
                  //
                  if (allow == true && courseNameT.equals( "No 3" ) && arrmem == true) {  

                     if (mship.equals( "Social Regular" ) && (index2 > 2 || (index2 == 2 && cal_time < 700))) { 
                       
                        allow = false;  // no can do
                     }
                  }
*/                    

                  //
                  //  Allow 7 days in adv for Course #2 (all mships are config'd for 2 days)
                  //
                  if (courseNameT.equals( "No 2" )) {

                     tempDays = 7;         //  force medinah's Course #2 to 7 days
                  }

               }      // end of Medinah Custom


               if (allow == true) {     // if still ok, check more

                  //
                  //  Check if we are past the allowed Days in Advance
                  //
                  if (!club.equals( "oakmont" ) && (index2 > tempDays && index2 > ldays)) {   // if beyond days in adv

                     if (mcctime == false && satime == false) {      // allow if mcctime or SantaAna time

                        allow = false;
                     }
                  }

                  if (!event.equals("") || fb == 9) {    // event or cross-over ?

                     allow = false;
                  }
               }

               //
               //  Custom check for Cordillera Canned Restrictions
               //
               if (club.equals( "cordillera" ) && allow == true) {

                  if (date > 20060413 && date < 20061030) {         // if this is within the custom date range

                     allow = cordilleraCustom.checkCordillera(date, tee_time, courseNameT, "member"); // go check if this time is restricted

                     if (allow == false) {                      // if restricted
                        bgcolor = cordRestColor;                // set color for this slot
                     }
                  }
               }

               //
               //  Custom check for Oakland Hills (for days beyond the 8 day limit)
               //
               if (club.equals( "oaklandhills" ) && allow == true && index2 > 8 && courseNameT.equals( "South Course" )) {

                  allow = verifySlot.checkOaklandAdvTime(date, tee_time, day_name); // go check if this time is restricted
               }


               if (allow == true) {     // if still ok, check more

                  //
                  //  If Cherry Hills, check mtype and day
                  //
                  if (club.equals( "cherryhills" )) {  

                     //
                     //  Process according to the day of week (and holidays)
                     //
                     if (day_name.equals( "Tuesday" ) && index2 > 1 && tee_time < 1101 && mtype.endsWith( "ember" )) {     

                        allow = false;
                     }
                     if (day_name.equals( "Thursday" ) && index2 > 1 && tee_time < 1000 && mtype.endsWith( "ember" )) {

                        allow = false;
                     }
                     if (day_name.equals( "Saturday" ) && tee_time < 1101 && !mtype.endsWith( "ember" )) {

                        allow = false;
                     }
                     if (day_name.equals( "Sunday" ) && tee_time < 1001 && !mtype.endsWith( "ember" )) {

                        allow = false;
                     }
                     if ((date == Hdate1 || date == Hdate2 || date == Hdate3) && tee_time < 1101 && !mtype.endsWith( "ember" )) {

                        allow = false;
                     }
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
                  //
                  //  if not event, then check for lottery time (events override lotteries)
                  //
                  if (event.equals("") && !lottery.equals("")) {  // lotteries are not in use before processing date/time

                     if (lottery.equals( lott1 )) {

                        if (lstate1 == 2) {        // ok to make lottery request

                           out.println("<form action=\"/" +rev+ "/servlet/Member_mlottery\" method=\"get\">");
                           out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate1 + "\">");
                           out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott1 + "\">");
                           out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots1 + "\">");

                        } else {

                           if (lstate1 == 5) {         // lottery is done - normal request (lottery)

                              out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                              out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate1 + "\">");

                           } else {      // not valid for members

                              allow = false;
                           }
                        }
                        lstate = lstate1;

                     } else {

                        if (lottery.equals( lott2 )) {

                           if (lstate2 == 2) {        // ok to make lottery request

                              out.println("<form action=\"/" +rev+ "/servlet/Member_mlottery\" method=\"get\">");
                              out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate2 + "\">");
                              out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott2 + "\">");
                              out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots2 + "\">");

                           } else {

                              if (lstate2 == 5) {         // lottery is done - normal request

                                 out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                                 out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate2 + "\">");

                              } else {      // not valid for members

                                 allow = false;
                              }
                           }
                           lstate = lstate2;

                        } else {

                           if (lottery.equals( lott3 )) {

                              if (lstate3 == 2) {        // ok to make lottery request

                                 out.println("<form action=\"/" +rev+ "/servlet/Member_mlottery\" method=\"get\">");
                                 out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate3 + "\">");
                                 out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott3 + "\">");
                                 out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots3 + "\">");

                              } else {

                                 if (lstate3 == 5) {         // lottery is done - normal request

                                    out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                                    out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate3 + "\">");

                                 } else {      // not valid for members

                                    allow = false;
                                 }
                              }
                              lstate = lstate3;

                           } else {

                              if (lottery.equals( lott4 )) {

                                 if (lstate4 == 2) {        // ok to make lottery request

                                    out.println("<form action=\"/" +rev+ "/servlet/Member_mlottery\" method=\"get\">");
                                    out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott4 + "\">");
                                    out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots4 + "\">");

                                 } else {

                                    if (lstate4 == 5) {         // lottery is done - normal request

                                       out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                                       out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate4 + "\">");

                                    } else {      // not valid for members

                                       allow = false;
                                    }
                                 }
                                 lstate = lstate4;

                              } else {     // more than 4 lotteries in one day !!!

                                 //
                                 //  save error message in /" +rev+ "/error.txt
                                 //
                                 String errorMsg = "Member_sheet: More than 4 lotteries defined for one day at " + club;    // build error msg
                                 SystemUtils.logError(errorMsg);                           // log it

                                 out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                              }
                           }
                        }
                     }
                  } else {   // no lottery for this tee time

                     lstate = 0;
                     out.println("<form action=\"/" +rev+ "/servlet/Member_slot\" method=\"post\" target=\"_top\">");
                  }
               }

               //
               //  If Hazeltine and more than 7 days in advance
               //
               if (club.equals( "hazeltine" ) && index2 > 7) {

                  if ((allow == true) && (in_use == 0)) {         // if still ok
                     //
                     //  Custom processing for Hazeltine
                     //
                     //    Certain women are allowed to book times at specific times on Tuesdays and
                     //    Thursdays 14 days in advance (vs. 7 days normally).
                     //
                     //    If allow is still true, then they must have the right to edit tee times on this day.
                     //    We must limit access to the specified times for that day.
                     //
                     if (day_name.equals( "Thursday" )) {

                        if (tee_time < 724 || tee_time > 930) {         // must be between 7:24 and 9:30

                           allow = false;
                        }
                     }
                     if (day_name.equals( "Tuesday" )) {

                        allow = false;    // default to not allowed so both types can be checked

                        if (msubtype.equals("After Hours") || msubtype.startsWith("AH")) {   // if 'After Hours' type

                           if (tee_time > 1631 & tee_time < 1831) {    // must be between 4:32 and 6:30

                              allow = true;
                           }
                        }
                        if (msubtype.equals("9 Holer") || msubtype.equals("AH-9 Holer") ||
                            msubtype.equals("AH-9/18 Holer") || msubtype.equals("9/18 Holer")) {  // if '9 Holer' type

                           if (tee_time > 723 && tee_time < 921) {    // must be between 7:24 and 9:20

                              allow = true;
                           }
                        }
                     }
                  }
               }

               if ((allow == true) && (in_use == 0) && (hideSubmit == 0)) {         // if still ok

                  out.println("<td align=\"center\">");
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

                  if ((fives != 0 ) && (rest5.equals( "" ))) {   // if 5-somes and not restricted

                     out.println("<input type=\"hidden\" name=\"p5\" value=\"Yes\">");  // tell _slot to do 5's
                  } else {
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");
                  }

                  if (lstate == 2) {       // if ok for lottery request

                     out.println("<input type=\"submit\" value=\"Lottery\">");
                     out.println("<input type=\"hidden\" name=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");

                  } else {

                     //
                     //  If Oakmont CC and Wed or Fri, or Milwaukee, and not a shotgun today, and this is a time when
                     //  multiple guests are allowed, make the submit button green. 
                     //
                     if (oaktime == true || mcctime == true || beartime == true) {

                        out.println("<input type=\"submit\" name=\"" + submit + "\" id=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\" style=\"text-decoration:none; background:lightgreen\">");

                     } else {
                        
                        out.println("<input type=\"submit\" name=\"" + submit + "\" id=\"" + submit + "\" value=\"" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm + "\" alt=\"submit\">");
                        
                     }
                  }

                  out.println("</font></td>");

                  //
                  //  if Consecutive Tee Times allowed, no lottery or event, and tee time is empty,
                  //   and F or B tee, allow member to select more than one tee time
                  //
                  if (parm.constimesm > 1) {

                     out.println("<td align=\"center\">");

                     if (allow == true && player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) &&
                         player4.equals( "" ) && player5.equals( "" ) &&
                         event.equals("") && lottery.equals("") && (fb == 0 || fb == 1)) {

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
                  out.println("<td align=\"center\">");
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
                     
                      out.print(SystemUtils.getSimpleTime(hr, min));
                     
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
                  for (tmp_i = 0; tmp_i < courseCount; tmp_i++) {
                      if (courseNameT.equals(course[tmp_i])) break;                      
                  }
                  
                  out.println("<td bgcolor=\"" + course_color[tmp_i] + "\" align=\"center\">");
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

                  if (hideN == 0) {             // if ok to display names

                     if (player1.equalsIgnoreCase("x") || g1 != 0) {   // if 'x' or guest

                        out.println(player1);

                     } else {       // not 'x' or guest

                        if (disp_hndcp == false) {
                           out.println(player1);
                        } else {
                           if ((hndcp1 == 99) || (hndcp1 == -99)) {
                              out.println(player1 + "  NH");
                           } else {
                              if (hndcp1 <= 0) {
                                 hndcp1 = 0 - hndcp1;                       // convert to non-negative
                              }
                              hndcp = Math.round(hndcp1);                   // round it off
                              out.println(player1 + "  " + hndcp);
                           }
                        }
                     }

                  } else {                           // do not display member names
                    
                     if (club.equals( "foresthighlands" )) {

                        out.println("Player");

                     } else {

                        if (player1.equalsIgnoreCase("x")) {      // if 'x'

                           out.println("X");

                        } else {                        // not 'x' 

                           if (g1 != 0) {               // if guest

                              out.println("Guest");

                           } else {                     // must be a member

                              out.println("Member");
                           }
                        }
                     }
                  }
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

                  if (hideN == 0) {             // if ok to display names

                     if ((player2.equalsIgnoreCase("x")) || (g2 != 0)) {   // if 'x' or guest

                        out.println(player2);

                     } else {       // not 'x' or guest

                        if (disp_hndcp == false) {
                           out.println(player2);
                        } else {
                           if ((hndcp2 == 99) || (hndcp2 == -99)) {
                              out.println(player2 + "  NH");
                           } else {
                              if (hndcp2 <= 0) {
                                 hndcp2 = 0 - hndcp2;                       // convert to non-negative
                              }
                              hndcp = Math.round(hndcp2);                   // round it off
                              out.println(player2 + "  " + hndcp);
                           }
                        }
                     }

                  } else {                          // do not display member names

                     if (club.equals( "foresthighlands" )) {

                        out.println("Player");

                     } else {

                        if (player2.equalsIgnoreCase("x")) {      // if 'x'

                           out.println("X");

                        } else {                        // not 'x'

                           if (g2 != 0) {               // if guest

                              out.println("Guest");

                           } else {                    // must be a member

                              out.println("Member");
                           }
                        }
                     }
                  }
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

               if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                  out.println("X");            // 2-somes ONLY

               } else {                  // not Piedmont special time

                  if (!player3.equals("")) {

                     if (hideN == 0) {             // if ok to display names

                        if ((player3.equalsIgnoreCase("x")) || (g3 != 0)) {   // if 'x' or guest

                           out.println(player3);

                        } else {       // not 'x' or guest

                           if (disp_hndcp == false) {
                              out.println(player3);
                           } else {
                              if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                 out.println(player3 + "  NH");
                              } else {
                                 if (hndcp3 <= 0) {
                                    hndcp3 = 0 - hndcp3;                       // convert to non-negative
                                 }
                                 hndcp = Math.round(hndcp3);                   // round it off
                                 out.println(player3 + "  " + hndcp);
                              }
                           }
                        }

                     } else {          // do not display member names

                        if (club.equals( "foresthighlands" )) {

                           out.println("Player");

                        } else {                  

                           if (player3.equalsIgnoreCase("x")) {      // if 'x'

                              out.println("X");

                           } else {                  // not 'x'

                              if (g3 != 0) {         // if guest

                                 out.println("Guest");

                              } else {               // must be a member

                                 out.println("Member");
                              }
                           }
                        }
                     }
                  } else {     // player is empty

                     out.println("&nbsp;");
                  }
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

               if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                  out.println("X");            // 2-somes ONLY

               } else {                  // not Piedmont special time

                  if (!player4.equals("")) {

                     if (hideN == 0) {             // if ok to display names

                        if ((player4.equalsIgnoreCase("x")) || (g4 != 0)) {   // if 'x' or guest

                           out.println(player4);

                        } else {       // not 'x' or guest

                           if (disp_hndcp == false) {
                              out.println(player4);
                           } else {
                              if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                 out.println(player4 + "  NH");
                              } else {
                                 if (hndcp4 <= 0) {
                                    hndcp4 = 0 - hndcp4;                       // convert to non-negative
                                 }
                                 hndcp = Math.round(hndcp4);                   // round it off
                                 out.println(player4 + "  " + hndcp);
                              }
                           }
                        }

                     } else {                      // do not display member names

                        if (club.equals( "foresthighlands" )) {

                           out.println("Player");

                        } else {

                           if (player4.equalsIgnoreCase("x")) {      // if 'x'

                              out.println("X");

                           } else {                    // not 'x'

                              if (g4 != 0) {           // if guest

                                 out.println("Guest");

                              } else {                 // must be a member

                                 out.println("Member");
                              }
                           }
                        }
                     }
                  } else {     // player is empty

                     out.println("&nbsp;");
                  }
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

                  if (fives != 0) {        // if 5-somes on this course 

                     if (!rest5.equals( "" )) {       // if 5-somes are restricted

                        out.println("<td bgcolor=\"" + bgcolor5 + "\" align=\"center\">");
                        out.println("&nbsp;");

                     } else {

                        out.println("<td bgcolor=\"" + bgcolor + "\" align=\"center\">");
                        out.println("<font size=\"2\">");

                        if (piedmontStatus == 3) {      // if Sat or Sun special times (1st 4 tee times)

                           out.println("X");            // 2-somes ONLY

                        } else {                  // not Piedmont special time

                           if (!player5.equals("")) {

                              if (hideN == 0) {             // if ok to display names

                                 if ((player5.equalsIgnoreCase("x")) || (g5 != 0)) {   // if 'x' or guest

                                    out.println(player5);

                                 } else {       // not 'x' or guest

                                    if (disp_hndcp == false) {
                                       out.println(player5);
                                    } else {
                                       if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                          out.println(player5 + "  NH");
                                       } else {
                                          if (hndcp5 <= 0) {
                                             hndcp5 = 0 - hndcp5;                       // convert to non-negative
                                          }
                                          hndcp = Math.round(hndcp5);                   // round it off
                                          out.println(player5 + "  " + hndcp);
                                       }
                                    }
                                 }

                              } else {                      // do not display member names

                                 if (player5.equalsIgnoreCase("x")) {      // if 'x'

                                    out.println("X");

                                 } else {                    // not 'x'

                                    if (g5 != 0) {           // if guest

                                       out.println("Guest");

                                    } else {                 // must be a member

                                       out.println("Member");
                                    }
                                 }
                              }
                           } else {     // player is empty

                              out.println("&nbsp;");
                           }
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
                  
                  } else {         // 5-somes supported on at least 1 course, but not this one (if course=ALL)

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
               out.println("</tr>");       // end of this row

            }  // end of IF blocker or Lottery test

         }  // end of while loop1

         pstmt.close();

         out.println("</table>");                   // end of tee sheet table
           
      } else {
        
         //
         //  This is Old Oaks CC and there is only a lottery for this date.
         //  Just display a lottery request button.
         //
         out.println("<br><br><br>");
         out.println("<form action=\"/" +rev+ "/servlet/Member_mlottery\" method=\"get\">");
         out.println("<input type=\"hidden\" name=\"lstate\" value=\"" + lstate1 + "\">");
         out.println("<input type=\"hidden\" name=\"lname\" value=\"" + lott1 + "\">");
         out.println("<input type=\"hidden\" name=\"slots\" value=\"" + slots1 + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + courseName1 + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=\"No\">");  // they always restrict to 4
       
         out.println("<input type=\"submit\" value=\"Note: Click Here to Request a Tee Time\" style=\"background:" +lcolor1+ "\">");
      }
      out.println("</td></tr>");
      out.println("</table>");                   // end of main page table
      //
      //  End of HTML page
      //
      out.println("</center></body></html>");
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

 public void displayEvent(String name, String club, PrintWriter out, Connection con) {

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
  
         if (!club.equals( "inverness" )) {          // if NOT Inverness
           
            out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
            out.println("<br><br>");
         }
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

}
