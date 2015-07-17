/***************************************************************************************
 *   Member_select:  This servlet will process the 'View Tee Sheet' request from
 *                    the Member's main page.
 *
 *
 *   called by:  member_main.htm (doGet)
 *
 *   created: 1/13/2002   Bob P.
 *
 *   last updated:
 * 
 *        9/17/13   Get member sub_type from the session instead of member2b.
 *        8/30/13   Brooklawn CC (brooklawn) - Updated 18 Holer ladies day to apply to Thursday for 9/5/13 only (case 1493).
 *        7/23/13   Boulder CC (boulder) - Added custom to hide the days in advance info from member view (case 2288).
 *        7/17/13   A server time clock will now be displayed on the mobile version of the page.
 *        7/08/13   Mira Vista CC (miravista) - Allow "18 Holer" mship access to Thursday morning times 3 weeks in advance (case 2278).
 *        6/12/13   Philadelphia Cricket Club (philcricket) - Updated recip button to have a yellow background to be more visible.
 *        5/30/13   Philadelphia Cricket Club (philcricket) - add seamless link to their Recip site.
 *        5/28/13   CC of Fairfax (ccfairfax) - Fixed issue that was causing 18 Holer subtypes to have 14 day access to Tuesdays instead of Wednesdays (case 1960).
 *        5/22/13   Overlake G&CC (overlakegcc) - Added custom to allow "18 Holer" msubtypes to access Tuesday and Friday tee times 7 days in advance (case 2259).
 *        5/06/13   Birmingham CC (bhamcc) - Added custom to set days in advance to 6 and make the tee sheet open up at 5:30pm on Thursdays for Adult Female members (case 2261).
 *        5/06/13   Brooklawn CC (brooklawn) - Updated customs for early event access with 2013 dates (case 1985)(case 2151)(case 2265).
 *        5/03/13   Brookside CC (brooksidecountryclub) - Added custom to allow "League" msubtype to access Tuesday, Wednesday, and Thursday tee sheets up to 14 days in advance (standard = 7) (case 2251).
 *        5/01/13   Interlachen - changed the guest quota name from Preferred Guests to Advanced Guests.
 *        4/24/13   Add custom for Lakewood Ranch (possible new feature) to allow member to select the consecutive times before going to the tee sheet.
 *                  This will make it faster and easier to get multiple times (when they are racing for the popular times).
 *        4/04/13   Remove warning message for the 'adjust tee sheet' checkbox option as it now functions better in Member_sheet.
 *        3/29/13   Add warning message for the 'adjust tee sheet' checkbox option to inform members that it will cause the tee sheet to jump after loading.
 *        3/07/13   Add guest quota display for Interlachen.
 *        3/05/13   Add member notices display in case pro wants to display any messages on this page (new member notice option).
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       10/17/12   Riviera CC (rivieracc) - Updated custom to allow "RWGA" member subtype access to certain Thursday morning times 14 day in advance (case 2152).
 *        9/06/12   Tweak for implementing the select_jump feature when going to tee sheet
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        5/30/12   Sankaty Head GC (sankatyheadgc) - Added custom to allow Primary/Spouse Female members access to 8-10 am times on 6/12, 7/17, 8/7, and 9/18 7 days in advance (case 2149).
 *        5/23/12   Brooklawn CC (brooklawn) - Updated early custom for early access with two additional dates (similar to case 1985).
 *        3/29/12   Riviera CC (rivieracc) - Added custom to allow all 'Adult Female' members access to Tuesdays 7 days in advance (case 2000).
 *        5/10/12   Riviera CC (rivieracc) - Added custom to allow women's "RWGA" member subtype access to Tues morning times 14 days in adv (case 2152).
 *        5/02/12   Brooklawn CC (brooklawn) - Allow access to tee times on 6/16/11 for Male members from 8:30 to 13:59 for tee time event (case 1985).
 *        4/26/12   Change new skin bread crumb so it does not say 'tee time' if Notification system.
 *        4/25/12   Scioto CC (sciotocc) - Updated custom processing for spouse access with different days in advance.
 *        3/30/12   Add a check box option for user to select if they want to show more tee times on the tee sheet.
 *        3/29/12   Riviera CC (rivieracc) - Added custom to allow all 'Adult Female' members access to Tuesdays 7 days in advance (case 2000).
 *        3/09/12   Mobile - add a couple of blank lines at bottom of page to allow for the IOS Nav Bar on iPhones.
 *        2/13/12   Claremont CC (claremontcc) - Commented out custom for giving Adult Females early access to Tuesday times (case 1361).
 *       11/02/11   Rolling Hills CC - CO (rhillscc) - Updated Ladies custom to allow them access to specific times during the off-season as well (case 1866).
 *       10/05/11   Capital City Club (capitalcityclub) - Removed Crabapple course weekend access custom (case 1955).
 *        9/23/11   Los Altos G&CC (lagcc) - Updated ladies time custom to also allow 14 day access to Tuesday 8:00am-9:30am times (case 1934).
 *        9/07/11   Naperville CC (napervillecc) - Color days from 8-30 days in advance yellow on the calendar signifying advance guest times only on those days (case 2009).
 *        8/11/11   Merion GC (merion) - Updated custom to allow "Non-Resident" members 365 days advance booking on weekends so the existing custom functions properly (case 1017).
 *        6/13/11   Brooklawn CC (brooklawn) - Allow access to tee times on 7/16/11 for members from 7:00 to 13:00 for tee time event.
 *        6/09/11   Olypmpic Club - do not show the Cliffs course (members cannot book in advance).
 *        5/24/11   Scioto CC (sciotocc) - Adjusted days in advance that spouses have access to Friday tee times.
 *        5/12/11   Brooklawn CC (brooklawn) - Adjusted custom to also allow dependents (case 1985).
 *        5/10/11   Brooklawn CC (brooklawn) - Allow access to tee times on 6/18/11 for Male members from 9:00 to 13:59 for tee time event (case 1985).
 *        5/10/11   Royal Oaks WA (royaloaks) - Removed Wednesday from Ladies time custom (case 1684).
 *        4/25/11   Algonquin GC (algonquin) - Added custom to allow women's "9 Holer" member subtype access to Wed morning times 14 days in adv (case 1967).
 *        4/06/11   CC of Fairfax (ccfairfax) - Added custom to allow women's "18 Holer" and "9 Holer" member subtypes access to Wed morning times 14 days in adv (case 1960).
 *        3/24/11   Capital City Club (capitalcityclub) - Custom to allow members access to the Crabapple course only at 7:30am, 3 days in advance for Fri/Sat/Sun (case 1955).
 *        3/18/11   Scioto Reserve/Kinsale (sciotoreserve) - Added a custom to set the default course based on the member type of the member.
 *        2/03/11   Indian Hills CC (indianhills) - Allow "18 Holer" female members to access Tuesday morning times 14 days in advance (case 1899).
 *        2/03/11   Tualatin CC (tualatincc) - Allow Primary/Spouse Female members to access Tuesday morning times 7 days in advance (case 1928).
 *        2/02/11   Los Altos G&CC (lagcc) - Allow female members to access Thursdays 14 days in advance (case 1934).
 *       12/02/10   Mobile - add Mid-Day option and make 'Entire Day' the default.
 *       10/29/10   Royal Oaks WA (royaloaks) - Added date range to Ladies' times custom (case 1684).
 *       10/22/10   Rolling Hills CC - CO (rhillscc) - Added date range to Ladies sub-type custom
 *       10/15/10   The Estancia Club (estanciaclub) - Added a message to display a member's count of unused Advance Guest Times (case 1884).
 *       10/15/10   The Estancia Club (estanciaclub) - Color days greater than 30 days in advance yellow on the calendar (case 1884).
 *        8/02/10   Fort Collins CC (fortcollins) - Updated customs to include Fox Hill CC
 *        7/21/10   Rolling Hills CC - CO (rhillscc) - added support for 'Ladies' msubtype access to Tuesday and Thursday times 14 days in advance (case 1866).
 *        4/13/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        3/31/10   Palo Alto Hills - allow women 18 holers to book Thurs 30 days in adv (case 1785).
 *       12/04/09   Seacliff CC - add women ladies-day - more days in adv for Tues. (case 1750).
 *       12/01/09   Allow for Saudi time zone (if rolled ahead one day)
 *        7/15/09   Add support for Mobile devices.
 *        7/15/09   For clubs with viewable days = bookable days in advance (same days in adv for ALL days of the week), allow members to click
 *                  a day on the calendar even if they aren't able to book on that day yet, but do NOT display ANY of the tee times when the tee sheet loads!!
 *                  This allows members to select this day as the time rolls over without reloading the page, while still keeping
 *                  the teesheet hidden from view until the designated time.
 *        6/30/09   Brooklawn - allow for subtype of "9/18 Holer".
 *        6/15/09   Brooklawn - allow Male members to access 7/18/09 in advance for tee time event.
 *        6/02/09   Royal Oaks WA - allow women to book more days in adv for Tues mornings. (case 1684).
 *        5/15/09   Timarron CC - do not change the days to view tee times (see change directly below dated 5/06/09).
 *        5/06/09   If the days in advance equals the days to view, use the time value to determine if member can view the sheet.
 *        5/05/09   Medinah CC - remove custom days in advance (case 1673).
 *        4/29/09   Pass the adjusted date for today to Member_jump so the correct index is calculated.  Other time zones
 *                  were getting the wrong tee sheets around midnight.
 *        4/21/09   Brooklawn - add women 9 holers - more days in adv for Wed. (case 1637).
 *        3/31/09   Fixed another bug with calendars affecting max days
 *                  (if you jump ahead 1 month, then jump back 1 month you may not be on the same day!)
 *        3/04/09   Fixed bug with calendars (was using CST date not adjusted time/date)
 *        2/10/09   Fixed bug with calendars (max days not always correct)
 *        1/22/09   Green Hills CC - Adjusted access time for the case 1574 custom.
 *        1/07/09   Timarron CC - back out the custom days in advance (case 1595).
 *        1/07/09   Restore the Refresh button in case members still want to use it (was removed when new clock added).
 *        1/02/09   Timarron CC - add custom days in advance to view tee sheets (case 1595).
 *       12/31/08   Green Hills CC - add women ladies-day - more days in adv for Thurs. (case 1574).
 *       10/08/08   Added new javascript clock to display server time
 *        8/18/08   Sharon Heights - add women 18 holers - more days in adv for Tues. (case 1533).
 *        8/11/08   Stonebridge Ranch - limit the course options based on mship type (case 1529).
 *        7/07/08   Admirals Cove - Added custom to default to -ALL- courses. This now uses new fields in the club5
 *                  table for determining the default course.  Still a custom though as no config available yet.  (case 1513).
 *        7/01/08   Black Diamond Ranch - Added custom to default to -ALL- courses.
 *        6/02/08   Brooklawn - add women 18 holers - more days in adv for Tues. (case 1493).
 *        4/22/08   Change to show lottery days on calendar
 *        4/18/08   Claremont CC - change days in adv parms for Adult Females on Tues (case 1361).
 *        4/02/08   Oak Hill CC - set default course to East Course (case 1433).
 *        2/08/08   Valley Country Club - Remove custom to allow ladies to access Fridays 2 weeks in advance.  (Case #1388)
 *        1/29/08   CC of Jackson - Set default course - Case #1373
 *        1/09/08   Merion - only allow members to access the East course.
 *        1/07/08   Jonathans Landing - Removed days in advance custom #1328
 *       12/17/07   Mediterra - Added custom to default to -ALL- courses (Case #1343)
 *       12/05/07   Jonathan's Landing - Custom days in advance for certain member types - Case# 1328
 *       12/04/07   Jonathans Landing - Added custom to default to -ALL- courses (Case #1338)
 *       11/09/07   Imperial GC - Added custom to default to -ALL- courses (Case #1306)
 *        9/19/07   Winged Foot - Added button to guestReport for showing guest rounds as counted for quota (Case #1250)
 *        8/13/07   Los Coyotes - change the days in adv for Secondary Members from to 3 days  (Case #1191)
 *        8/12/07   Medinah CC - if Monday (today) and days in adv = 30, change to 29  (Case #1225)
 *        8/04/07   Valley Country Club - Allow ladies to access Fridays 2 weeks in advance.  (Case #1160)
 *        7/24/07   Medinah - remove customs that are no longer needed (ARR members and days in adv).
 *        6/08/07   Los Coyotes - Added custom to default to -ALL- courses (case #1187).
 *        5/24/07   Catamount Ranch - remove 365 day calendars for Founder members.
 *        5/23/07   Milwaukee - make some adjustments to calendar custom..
 *        5/22/07   Remove custom 'max' days values to view tee sheets - use new config parms.
 *        5/22/07   Edison - default to 'ALL' courses.
 *        5/09/07   DaysAdv array no longer stored in session block - Using call to SystemUtils.daysInAdv
 *        4/30/07   Upgraded calendars to version 4.0 - now all clubs use dynamic calenders
 *        4/26/07   Northland CC - use 365 day calendars.
 *        4/25/07   Minikahda - Add a note about available guest times (case #1027).
 *        4/11/07   Congressional - Add a note about available guest times (case #1075).
 *        4/05/07   The International - Added custom to default to -ALL- courses.
 *        4/02/07   Pinery - Added custom to default to -ALL- courses.
 *        3/31/07   Inverness Club - use 365 day calendars.
 *        2/14/07   Mission Viejo - custom, only allow 10 days in advance for members to view tee sheets.
 *        2/07/07   Fort Collins/Greeley - do not allow the -ALL- course option, and
 *                  list the appropriate course first based on which club the user is from.
 *        1/24/07   Peninsula Club - use 365 day calendars for members.
 *        1/23/07   Changes for TLT - added use365 boolean to control calendars (no longer needed - see max)
 *        1/11/07   El Niguel - change days in adv parms for Adult Females on Tues.
 *        1/04/07   Royal Oaks Houston - use 14 day calendars.
 *       11/14/06   Base changes for TLT System
 *       11/13/06   Disabled Santa Ana Womens retriction - per Larry
 *       11/08/06   Columbia-Edgewater - custom days in adv for Spouse member types.
 *       10/18/06   Westchester - use 90 day calendars.
 *       10/11/05   Blackstone - custom, only allow 14 days in advance for members to view tee sheets.
 *        8/21/06   Added custom to default to -ALL- courses for Fairbanks Ranch
 *        8/14/06   Added custom to default to -ALL- courses for Lakewood Ranch and Pelican's Nest
 *        6/28/06   Desert Highlands - custom, only allow 7 days in advance for members to view tee sheets.
 *        6/14/06   Scioto - custom days in adv for Spouse member types.
 *        5/02/06   North Hills - make sure that the days after 7 are in red.
 *        4/25/06   Forest Highlands - change instructions.
 *        4/18/06   Nakoma - use 90 day calendars.
 *        4/17/06   Medinah - remove ARR processing per Medinah's instructions.
 *        4/13/06   Inverness - use 90 day calendars.
 *        4/11/06   Catamount Ranch - use 365 day calendars for Founder members.
 *        3/15/06   CC of the Rockies - use 365 day calendars.
 *        3/14/06   Oakland Hills - use 365 day calendars.
 *        3/01/06   North Hills - allow 30 days in advance for guest times.
 *        2/23/06   Merion - use 365 day calendars.
 *       11/18/05   Ritz-Carlton - use 365 day calendars.
 *       10/24/05   Allow for 20 courses and -ALL- for multi course clubs.
 *        7/20/05   Medinah - change ARR members from 30 days adv to 1 month adv.
 *        7/20/05   Forest Highlands - custom, only allow 5 days in advance for members to view tee sheets.
 *        5/21/05   Custom for Medinah CC - If ARR member, display link to show Advanced Reservation Rights (ARR).
 *        4/26/05   Custom for Santa Ana Women - Increase days in advance for Tues and Fri.
 *        3/25/05   RDP Custom for Oakmont and Saucon Valley - use 365 day calendars.
 *        3/15/05   RDP Add a Refresh button to refresh the server time - quicker.
 *        1/20/05   RDP Correct the way the days in advance is adjusted for the current time.
 *        1/05/05   RDP Correct the way the days in advance is calculated.
 *       11/29/04   RDP Add special processing for Milwaukee - allow 90 days in advance for guest times.
 *       10/06/04   Ver 5 - add sub-menu support.
 *        9/22/04   RDP Add special processing for Oakmont - allow 90 days in advance for guest times.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        5/03/04   RDP Add an 'ALL' option for multiple course facilities.
 *        1/21/04   RDP Allow for 'Days in Adv' parms to be based on membership type.
 *        1/11/04   JAG Modification to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *       12/18/02   Enhancements for Version 2 of the software.
 *                   Add multiple course support.
 *                   Add processing for 'days in advance/time' features.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import org.apache.commons.lang.*;
//import org.apache.commons.lang.StringEscapeUtils.*

        
//import org.json.simple.JSONObject;
import com.google.gson.*;

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.medinahCustom;
import com.foretees.common.Utilities;
import com.foretees.client.SystemLingo;
import com.foretees.common.verifyCustom;


public class Member_select extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the initial request from member_main.htm
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    // Check if we will only be return json data
    boolean json_mode = (req.getParameter("json_mode")) != null;
     
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

   //Statement stmt = null;
   ResultSet rs = null;
   //ResultSet rs2 = null;
   //ResultSet rs3 = null;
   //ResultSet rs4 = null;


    // Define parms
   String courseName = "";        // course names
   String adv_zone = "";
   //String adv_ampm = "";
   //String lottery1 = "";
   //String lottery2 = "";
   String buttonColor = "";

   boolean skipALL = false;      // do not include the -ALL- option for course selection if true

   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //         one per day of week (Mon - Sun)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int days = 0;
   int multi = 0;               // multiple course support
   int lottery = 0;             // lottery support
   int index = 0;
   //int found = 0;
   //int sdays = 0;
   int max = 0;                 // max # of days to display  (was daysArray.MAXDAYS)
   int mobile = 0;              // Mobile user
   int tee_sheet_jump = 0;      // tee sheet jump option for user

   int cal_time = 0;            // calendar time for compares
   //int adv_time = 0;            // 'days in advance' time for compares
   //int adv_hr = 0;
   //int adv_min = 0;

   int mccMax = 7;              // days in advance for customs !!!!!!

   //long date = 0;

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                          "September", "October", "November", "December" };
/*
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
*/
   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat
   int [] advtime = new int [7];                        // adv time for each

   //
   //  Array to hold the course names
   //
   int cMax = 0;
   //String [] course = new String [cMax];
   ArrayList<String> course = new ArrayList<String>();



   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   String club = (String)session.getAttribute("club");               // get club name
   String user = (String)session.getAttribute("user");
   String caller = (String)session.getAttribute("caller");
   String mship = (String)session.getAttribute("mship");             // get member's mship type
   String mtype = (String)session.getAttribute("mtype");             // get member's mtype
   String msubtype = (String)session.getAttribute("msubtype");       // get member's sub_type
   boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
   int activity_id = (Integer)session.getAttribute("activity_id");

   boolean skipCourse = false;
   
   

   //
   //  See if Mobile user
   //
   try {
      mobile = (Integer)session.getAttribute("mobile");
   }
   catch (Exception ignore) {
      mobile = 0;
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
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }
   
   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // this page is not being used by FlxRez so we'll hard code a zero in here for the activity_id

   // Setup the daysArray
   DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
   daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, user, con);

   //
   //  See if we are in the timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;

   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);

   //
   // Get the club parms
   //
   try {

      getClub.getParms(con, parm);        // get the club parms

      multi = parm.multi;
      lottery = parm.lottery;
      adv_zone = parm.adv_zone;

      //
      //  use the member's mship type to determine which 'days in advance' and 'time' values to use
      //
      verifySlot.getDaysInAdv(con, parm, mship);        // get the days in adv & time data for this member

      days1 = parm.advdays1;     // get days in adv for this type (Sun - Sat)
      days2 = parm.advdays2;
      days3 = parm.advdays3;
      days4 = parm.advdays4;
      days5 = parm.advdays5;
      days6 = parm.advdays6;
      days7 = parm.advdays7;

      advtime[0] = parm.advtime1;   // get time values
      advtime[1] = parm.advtime2;
      advtime[2] = parm.advtime3;
      advtime[3] = parm.advtime4;
      advtime[4] = parm.advtime5;
      advtime[5] = parm.advtime6;
      advtime[6] = parm.advtime7;

      max = parm.memviewdays;        // days this member can view tee sheets

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("System Error"));
      out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
      out.println("<BR><BR><H2>System Error</H2>");
      out.println("<BR><BR>Sorry, we encountered a system problem.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception (Member_slect #1): " + exc.getMessage());
      out.println("<BR><BR>If problem persists, please contact your golf shop (provide this message).");
      out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get the course names if more than one
   //
   try {

      if (multi != 0) {           // if multiple courses supported for this club

         //
         //   If Stonebridge Ranch CC - course selection based on mship type
         //
         if (club.equals( "stonebridgeranchcc" ) && !mship.equals("Dual")) {

            if (mship.equals("Dye")) {       // if DYE mship

               course.add( "Dye" );            // Only Dye course

            } else {                         // Hills mship gets all but Dye

               course.add( "Chisholm" );
               course.add( "Cimmarron" );
               course.add( "Saddleback" );
            }

         } else {              // all others

            course = Utilities.getCourseNames(con);     // get all the course names
         }


         if (mobile > 0 || club.equals( "fortcollins" ) || (club.equals( "stonebridgeranchcc" ) && mship.equals("Dye"))) {

            skipALL = true;       // do not include the -ALL- option
         }


         if (skipALL == false && course.size() > 1) {   // if ok and more than 1 course, add -ALL- option

            course.add( "-ALL-" );
         }




         /*
         while (index < cMax) {

            course[index] = "";       // init the course array
            index++;
         }

         index = 0;


         //
         //   If Stonebridge Ranch CC - course selection based on mship type
         //
         if (club.equals( "stonebridgeranchcc" ) && !mship.equals("Dual")) {

            if (mship.equals("Dye")) {       // if DYE mship

               course[0] = "Dye";            // Only Dye course

            } else {                         // Hills mship gets all but Dye

               course[0] = "Chisholm";
               course[1] = "Cimmarron";
               course[2] = "Saddleback";
               index = 3;
            }

         } else {              // all others

            //
            //  Get the names of all courses for this club
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT courseName " +
                                    "FROM clubparm2 WHERE first_hr != 0");

            while (rs.next() && index < cMax) {

               courseName = rs.getString(1);

               course[index] = courseName;      // add course name to array
               index++;
            }
            stmt.close();
         }


         if (mobile > 0 || club.equals( "fortcollins" ) || (club.equals( "stonebridgeranchcc" ) && mship.equals("Dye"))) {

            skipALL = true;       // do not include the -ALL- option
         }


         if (skipALL == false && index > 1 && index < cMax) {   // if ok and more than 1 course, add -ALL- option

            course[index] = "-ALL-";
         }
          */


      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("System Error"));
      out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
      out.println("<BR><BR><H2>System Error</H2>");
      out.println("<BR><BR>Sorry, we encountered a system problem.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception (Member_slect #2): " + exc.getMessage());
      out.println("<BR><BR>If problem persists, please contact your golf shop (provide this message).");
      out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Parms for calendar
   //
   int i = 0;
   //int count = 0;                    // init day counter
   //int col = 0;                      // init column counter
   //int d = 0;                        // 'days in advance' value for current day of week

   boolean day90 = false;

   if (club.equals( "milwaukee" ) || club.equals( "inverness" ) || club.equals( "nakoma" ) ||
       club.equals( "westchester" )) {

      day90 = true;              // special 90 day guest times - directions
   }

/*
   if (club.equals( "foresthighlands" )) {        // per Pro's request

      max = 6;           // 5 days in advance (+ today)
   }

   if (club.equals( "blackstone" ) || club.equals( "royaloakscc" )) {   // per Pro's request

      max = 15;           // 14 days in advance (+ today)
   }

   if (club.equals( "missionviejo" )) {   // per Pro's request

      max = 11;           // 10 days in advance (+ today)
   }

   if (club.equals( "deserthighlands" )) {        // per Pro's request

      max = 8;           // 7 days in advance (+ today)
   }
*/


   //
   //  Get current date/time and setup parms to use when building the calendar
   //
   Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
   int cal_min = cal.get(Calendar.MINUTE);
   int cal_sec = cal.get(Calendar.SECOND);

   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format

   cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value

      if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi time)

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

   month++;                                      // month starts at zero

   int cal_hour = cal_time / 100;                // get adjusted hour
   cal_min = cal_time - (cal_hour * 100);        // get minute value

   if (cal_hour > 11) cal_hour = cal_hour - 12;  // set to 12 hr clock

   if (cal_hour == 0) cal_hour = 12;

   String day_name = day_table[day_num];         // get name for day
   String mm_name = mm_table[month];             // get name for month

   long thisDate = (year * 10000) + (month * 100) + day;         // get adjusted date for today


   //
   //  Custom 1595 for Timarron - days to view must match the days in advance so members cannot view any sooner than they can book.
   //
   /*    // backed out per pro's request
   if (club.equals( "timarroncc" )) {   // per Pro's request

      max = 4;                // normally 4 days in advance

      if (day_num == 2 || cal_time < 700) {     // if Monday (any time), then do not allow access to Friday, OR if before 7:00 AM

         max = 3;
      }
   }
    */


/*
   //
   //  If Jonathan's Landing and an certain member types - change days in advance to 6 - Case# 1328
   //
   if (club.equals( "jonathanslanding" ) &&
           (mship.equals( "Golf" ) || mship.equals( "Golf Asc" ) || mship.equals( "Golf Sr" )) ) {

      days1 = 6;                    // change to 6 days
      days2 = 6;
      days3 = 6;
      days4 = 6;
      days5 = 6;
      days6 = 6;
      days7 = 6;
   }
*/
/*
   //
   //  If Valley Country Club and a lady change the days in adv to 14 for Fridays.  Removed 2/08/08 (case #1388).
   //
   if (club.equals( "valleycc" ) && mtype.endsWith( "Female" ) ) {
         days6 = 14;
         parm.advdays6 = 14;
   }
*/

   //
   //   Scioto Custom - change the days in adv for Spouses - Sun, Mon, Thur, Fri, Sat = 2, Tue, Wed = 3
   //
   if (club.equals( "sciotocc" ) && mtype.startsWith( "Spouse" )) {

      days1 = 3;          // Sun = 3 days in advance (starting at 7:30 AM)
      days2 = 3;          // Mon = 3 days in advance (starting at 7:30 AM)
      days3 = 4;          // Tue = 4 days in advance (starting at 7:30 AM)
      days4 = 4;          // Wed = 4 days in advance (starting at 7:30 AM)
      days5 = 4;          // Thu = 4 days in advance (starting at 7:30 AM)
      days6 = 3;          // Fri = 3 days in advance (starting at 7:30 AM)
      days7 = 3;          // Sat = 3 days in advance (starting at 7:30 AM)
      parm.advdays1 = 3;
      parm.advdays2 = 3;
      parm.advdays3 = 4;
      parm.advdays4 = 4;
      parm.advdays5 = 4;
      parm.advdays6 = 3;
      parm.advdays7 = 3;

      //advtime[5] = 1200;  // Changed back to 7:30 4/25/12
   }

   //
   //  If El Niguel and an Adult Female - change the Tuesday Days and Time
   //
   if (club.equals( "elniguelcc" ) && mtype.equals( "Adult Female" )) {

      days3 = 4;               // Tues = 4 (normally is 2)

      advtime[2] = 1300;       // at 1:00 PM
   }


   //
   //  If Claremont and an Adult Female - change the Tuesday days to 30 (normally 3)
   //
   /*
   if (club.equals( "claremontcc" ) && mtype.equals( "Adult Female" )) {

      days3 = 30;               // Tues = 30 (normally is 3)
      parm.advdays3 = 30;
   }
   */

   /******** ON HOLD
   //
   //  If Los Coyotes and Secondary mtype - change the Tuesday days to 8 (normally 3 at 6:00 AM)
   //
   if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" )) {

      days3 = 8;                // Tues = 8
      parm.advdays3 = 8;
   }
   */


   try {

      //
      //  Get the tee sheet option and member sub-type for this user
      //
      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT msub_type, tee_sheet_jump FROM member2b WHERE username = ?");

        pstmt1.clearParameters();        // clear the parms
        pstmt1.setString(1, user);
        rs = pstmt1.executeQuery();      // execute the prepared stmt

        if (rs.next()) {

           //msubtype = rs.getString(1);     // now its in the session
           tee_sheet_jump = rs.getInt("tee_sheet_jump");
        }

      pstmt1.close();
   }
   catch (Exception exc) {
   }
   

   //
   //  If Brooklawn and a Ladies 18-Holer (sub-type) - change the Tuesday days to 7 (normally 2 at 7:30 AM)
   //  If Sharon Heights and a Ladies 18-Holer (sub-type) - change the Tuesday days to 14 at 7 AM
   //
   if ((club.equals( "brooklawn" ) || club.equals( "sharonheights" ) || club.equals( "greenhills" ) || club.equals( "seacliffcc" ) ||
        club.equals( "paloaltohills" ) || club.equals( "rhillscc" ) || club.equals("indianhillscc") ||
        club.equals("algonquin") || club.equals("rivieracc")) && mtype.endsWith( "Female" )) {

      //
      //  Process by club and subtype
      //
      if (club.equals( "brooklawn" )) {

         if (msubtype.equals( "18 Holer" ) || msubtype.equals( "9/18 Holer" )) {

            days3 = 7;                // Tues = 7
            advtime[2] = 1500;        // at 3:00 PM
            parm.advdays3 = 7;
            
            if (thisDate < 20130905) {
                days5 = 7;
                advtime[4] = 1500;
                parm.advdays5 = 7;
            }
         }

         if (msubtype.equals("9 Holer") || msubtype.equals( "9/18 Holer" )) {

           days4 = 7;               // Wed = 7 days in adv
           advtime[3] = 730;        // at 7:30 AM
           parm.advdays4 = 7;
         }

      } else if (msubtype.equals( "18 Holer" )) {   // all 18 Holers

         if (club.equals("seacliffcc")) {   // Seacliff

            days3 = 30;                // Tues = 30
            advtime[2] = 600;          // at 6:00 AM
            parm.advdays3 = 30;
            max = 30;                  // allow female 18 holers to view ahead 30 days (vs 7)

         } else if (club.equals("paloaltohills")) {

            days5 = 30;                // Thurs = 30
            max = 30;                  // allow female 18 holers to view ahead 30 days (vs 7)

         } else {                       // other clubs that use 18 Holer sub-type

            days3 = 14;                // Tues = 14
            advtime[2] = 700;          // at 7:00 AM
            parm.advdays3 = 14;
         }

      } else if (msubtype.equals("Ladies") && club.equals("greenhills")) {  // Green Hills

           days5 = 7;               // Green Hills - Thurs = 7
           advtime[4] = 1530;        // at 1:30 PM
           parm.advdays5 = 7;

      } else if (msubtype.equals("Ladies") && club.equals("rhillscc")) {

              days3 = 14;
              advtime[2] = 14;
              parm.advdays3 = 14;

              days5 = 14;
              advtime[4] = 14;
              parm.advdays5 = 14;
              
      } else if (club.equals("indianhillscc") && msubtype.equals("18 Holer")) {

          int month_day = (month * 100) + day;

          if (month_day >= 401 && month_day <= 1015) {
              
              days3 = 14;
              advtime[2] = 14;
              parm.advdays3 = 14;
          }
      } else if (club.equals("algonquin") && msubtype.equals("9 Holer")) {

          int month_day = (month * 100) + day;

          if (month_day >= 401 && month_day <= 1031) {

              days4 = 14;
              advtime[3] = 14;
              parm.advdays4 = 14;
          }
      } else if (club.equals("rivieracc") && msubtype.equals("RWGA")) {

          days3 = 14;
          days5 = 14;
          advtime[2] = 14;
          advtime[4] = 14;
          parm.advdays3 = 14;
          parm.advdays5 = 14;
      }
   }


   //
   //  Roayl Oaks WA - adult females can book early on Tues mornings
   //
   if (club.equals( "royaloaks" ) && mtype.equals( "Adult Female" )) {

      int month_day = (month * 100) + day;

      if (month_day >= 301 && month_day <= 1031) {
          
          days3 = 7;                // Tues = 7
          parm.advdays3 = 7;
          advtime[2] = 730;         // at 7:30 AM

          days6 = 7;                // Fri = 7
          parm.advdays6 = 7;
          advtime[5] = 730;         // at 7:30 AM
      }
      
   } else if (club.equals("lagcc") && mtype.endsWith("Female")) {

       days3 = 14;
       days5 = 14;
       advtime[2] = 14;
       advtime[4] = 14;
       parm.advdays3 = 14;
       parm.advdays5 = 14;
       
   } else if (club.equals("tualatincc") && (mtype.equals("Primary Female") || mtype.equals("Spouse Female"))) {

       days3 = 7;
       advtime[2] = 7;
       parm.advdays3 = 7;
       
   } else if (club.equals("brooksidecountryclub") && msubtype.equals("League")) {
       
       if (mtype.endsWith("Male")) {
           days3 = 14;
           advtime[2] = 14;
           parm.advdays3 = 14;
       }
       
       if (mtype.endsWith("Female")) {
           days4 = 14;
           advtime[3] = 14;
           parm.advdays4 = 14;
       }
       
   } else if (club.equals("bhamcc") && mtype.equals("Adult Female")) {
       
       days5 = 6;
       parm.advdays5 = 6;
       advtime[4] = 1730;
       
   } else if (club.equals("overlakegcc") && msubtype.equals("18 Holer")) {
       
       days3 = 7;                // Tues = 7
       parm.advdays3 = 7;

       days6 = 7;                // Fri = 7
       parm.advdays6 = 7;
       
   } else if (club.equals("ccfairfax") && (msubtype.equals("18 Holer") || msubtype.equals("9 Holer"))) {     // If ccfairfax and 18 Holer/9 holer, allow access to Wed 14 days in adv.
       
       int month_day = (month * 100) + day;
       
       if (month_day >= 401 && month_day <= 1031) {
           
           days4 = 14;
           advtime[3] = 14;
           parm.advdays4 = 14;
       }
   } else if (club.equals("miravista") && msubtype.equals("18 Holer")) {
       
       days5 = 21;
       parm.advdays5 = 21;
   }
   

       /*    // removed per Mike Scully's request 5/05/09
   //
   //   Medinah Custom - if Monday (today) and days in adv = 30, change to 29 (proshop closed on Mondays)
   //
   if ( club.equals( "medinahcc" ) && ( day_num == 2 || ( day_num == 3 && cal_time < 600 ) ) ) {     // if Medinah and today is Monday or Tues b4 6:00am

          if (days1 == 30) {
             days1 = 29;
             parm.advdays1 = 29;
          }
          if (days2 == 30) {
             days2 = 29;
             parm.advdays2 = 29;
          }
          if (days3 == 30) {
             days3 = 29;
             parm.advdays3 = 29;
          }
          if (days4 == 30) {
             days4 = 29;
             parm.advdays4 = 29;
          }
          if (days5 == 30) {
             days5 = 29;
             parm.advdays5 = 29;
          }
          if (days6 == 30) {
             days6 = 29;
             parm.advdays6 = 29;
          }
          if (days7 == 30) {
             days7 = 29;
             parm.advdays7 = 29;
          }
   }
        */


   //
   //  If Merion's change the days in adv to 365 (East course is the only one that can book times on)
   //
   if (club.equals( "merion" )) {

       days1 = 7;
       days2 = 365;
       days3 = 365;
       days4 = 365;
       days5 = 365;
       days6 = 365;
       days7 = 7;

       if (mship.equals("Non-Resident")) {
           days1 = 365;
           days7 = 365;
       }

   }
   
   //
   //   Los Coyotes Custom - change the days in adv for Secondary Members from to 3 days  (Case #1191)
   //
   if (club.equals( "loscoyotes" ) && mtype.startsWith( "Secondary" )) {

      days1 = 3;
      days2 = 3;
      days3 = 3;
      days4 = 3;
      days5 = 3;
      days6 = 3;
      days7 = 3;
      parm.advdays1 = 3;
      parm.advdays2 = 3;
      parm.advdays3 = 3;
      parm.advdays4 = 3;
      parm.advdays5 = 3;
      parm.advdays6 = 3;
      parm.advdays7 = 3;
   }

   //
   // If Capital City Club and Crabapple course, set advance time to 7:30am for Fri/Sat/Sun
   //
   /*
   if (club.equals("capitalcityclub") && course.equals("Crabapple")) {

       days1 = 3;               // Sunday - 3 days
       advtime[0] = 730;        // Sunday - 7:30am

       days6 = 3;               // Friday - 3 days
       advtime[5] = 730;        // Friday - 7:30am

       days7 = 3;               // Saturday - 3 days
       advtime[6] = 730;        // Saturday - 7:30am
   }
    */


   //
   //   Columbia-Edgewater Custom - change the days in adv for Spouses
   //
   if (club.equals( "cecc" ) && mtype.startsWith( "Spouse" )) {

      days3 = 7;          // Tues = 7 days in advance (starting at 8:00 AM)
      days6 = 7;          // Fri = 7 days in advance (starting at 8:00 AM)
      parm.advdays3 = 7;
      parm.advdays6 = 7;

      if (cal_time < 800) {       // if before 8 AM

         days3 = 6;
         days6 = 6;
      }

      if (advtime[0] > cal_time) {    // adjust the others if necessary
         days1--;
      }
      if (advtime[1] > cal_time) {
         days2--;
      }
      if (advtime[3] > cal_time) {
         days4--;
      }
      if (advtime[4] > cal_time) {
         days5--;
      }
      if (advtime[6] > cal_time) {
         days7--;
      }

   } else {

      //
      //    adv time values have been set based on the mship type
      //    calendar time (cal_time) has been adjusted already for the time zone specified
      //
      //  if its earlier than the time specified for days in advance, do not allow the last day_in_advance
      //
      if (advtime[0] > cal_time) {

/*  Moved to Member_sheet instead so day is still clickable on calendar on the day-of
         //
         //  If this club set the max days to view equal to the days in advance, and all days in advance are the same, and
         //  all times of the days are the same, then adjust the days to view.  This is the only way we can do this!!
         //
         if (!club.equals( "timarroncc" )) {   // per Pro's request - do not do for Timarron

            if (max > 0 && max == days1 && max == days2 && max == days3 && max == days4 && max == days5 && max == days6 && max == days7 &&
                advtime[0] == advtime[1] && advtime[0] == advtime[2] && advtime[0] == advtime[3] && advtime[0] == advtime[4] && advtime[0] == advtime[5] &&
                advtime[0] == advtime[6]) {

               max--;
            }
         }
 */

         mccMax--;                     // adjust custom days in advance
         days1--;
      }

      if (advtime[1] > cal_time) {

         days2--;
      }

      if (advtime[2] > cal_time) {

         days3--;
      }

      if (advtime[3] > cal_time) {

         days4--;
      }

      if (advtime[4] > cal_time) {

         days5--;
      }

      if (advtime[5] > cal_time) {

         days6--;
      }

      if (advtime[6] > cal_time) {

         days7--;
      }
   }

   //
   //  put the 'days in advance' values in an array to be used below
   //
   advdays[0] = days1;       // Sun - Sat
   advdays[1] = days2;
   advdays[2] = days3;
   advdays[3] = days4;
   advdays[4] = days5;
   advdays[5] = days6;
   advdays[6] = days7;

   //
   //  Adjust days array values if necessary (in case the time has now reached the set value)
   //
   day_num--;                           // convert today's day_num to index (0 - 6)

   for (index = 0; index < daysArray.MAXDAYS; index++) {

      days = advdays[day_num];              // get days in advance for day of the week
      day_num++;                            // bump to next day of week
      if (day_num > 6) day_num = 0;         // if wrapped past end of week

      //
      // check if this day can be accessed by members
      //
      //    0 = No, 1 = Yes, 2 = Yes for Lottery only (set in SystemUtils.daysInAdv called above)
      //
      if (club.equals( "milwaukee" )) {

         if (days >= index) {                 // if ok for this day

            // use default colors
            daysArray.days[index] = 1;        // set ok in array

         } else {

            if (day_num == 3 || day_num == 4 || day_num == 5 || day_num == 6) {  // if Tues - Fri (day_num already adjusted for next day!)

               daysArray.days[index] = 101;        // mark this one in red (sienna)
            }
         }

      } else if (club.equals("estanciaclub")) {

          if (index >= 30) {
              daysArray.days[index] = 103;    // mark days more than 30 out in yellow
          }

      } else if (club.equals("napervillecc")) {

          if (index > 7 && index <= 30) {
              daysArray.days[index] = 103;    // mark days more than 7 out in yellow
          }

      } else {

         if (days >= index) {                 // if ok for this day

            // use default colors
            daysArray.days[index] = 1;        // set ok in array
         } else if (daysArray.days[index] != 2) {
            daysArray.days[index] = 0;        // set to no access in array if no lottery found
         }

         if (club.equals( "brooklawn" )) {

             if (thisDate <= 20130615 && (mtype.endsWith( "Male" ) || mtype.equalsIgnoreCase("Cert Dependent over 16") ||
                 mtype.endsWith("Dependent"))) {    // Brooklawn Custom for 7/18/09

                if ((thisDate > 20130509 || (thisDate == 20130509 && cal_time >= 900)) && index > 0) {

                   //
                   //  Allow member access on 7/18/09 for a tee time event
                   //
                   Calendar calB = new GregorianCalendar();             // get current date
                   calB.add(Calendar.DATE,index);                       // get this day's date
                   int yearB = calB.get(Calendar.YEAR);
                   int monthB = calB.get(Calendar.MONTH) +1;
                   int dayB = calB.get(Calendar.DAY_OF_MONTH);

                   long DateB = (yearB * 10000) + (monthB * 100) + dayB;         // get adjusted date

                   if (DateB == 20130615) {         // if day of event

                      daysArray.days[index] = 1;        // set ok in array

                      if (max < index) max = index;     // bump max days to view if necessary
                   }
                }
             }

             if (thisDate <= 20130619) {    // Brooklawn Custom for 7/16/11

                if ((thisDate > 20130515 || (thisDate == 20130515 && cal_time >= 900)) && index > 0) {

                   //
                   //  Allow member access to 6/19/13 on 5/15/13 for a tee time event
                   //
                   Calendar calB = new GregorianCalendar();             // get current date
                   calB.add(Calendar.DATE,index);                       // get this day's date
                   int yearB = calB.get(Calendar.YEAR);
                   int monthB = calB.get(Calendar.MONTH) +1;
                   int dayB = calB.get(Calendar.DAY_OF_MONTH);

                   long DateB = (yearB * 10000) + (monthB * 100) + dayB;         // get adjusted date

                   if (DateB == 20130619) {         // if day of event

                      daysArray.days[index] = 1;        // set ok in array

                      if (max < index) max = index;     // bump max days to view if necessary
                   }
                }
             }

             if (thisDate <= 20130713) {    // Brooklawn Custom for 7/16/11

                if ((thisDate > 20130526 || (thisDate == 20130526 && cal_time >= 700)) && index > 0) {

                   //
                   //  Allow member access on 7/16/11 for a tee time event
                   //
                   Calendar calB = new GregorianCalendar();             // get current date
                   calB.add(Calendar.DATE,index);                       // get this day's date
                   int yearB = calB.get(Calendar.YEAR);
                   int monthB = calB.get(Calendar.MONTH) +1;
                   int dayB = calB.get(Calendar.DAY_OF_MONTH);

                   long DateB = (yearB * 10000) + (monthB * 100) + dayB;         // get adjusted date

                   if (DateB == 20130713) {         // if day of event

                      daysArray.days[index] = 1;        // set ok in array

                      if (max < index) max = index;     // bump max days to view if necessary
                   }
                }
             }
             
         } else if (club.equals("sankatyheadgc")) {

            if (mtype.equalsIgnoreCase("Primary Female") || mtype.equalsIgnoreCase("Spouse Female")) {
              
               // Allow 7 days early access to specific days
               Calendar calB = new GregorianCalendar();             // get current date
               calB.add(Calendar.DATE,index);                       // get this day's date
               int yearB = calB.get(Calendar.YEAR);
               int monthB = calB.get(Calendar.MONTH) +1;
               int dayB = calB.get(Calendar.DAY_OF_MONTH);

               long DateB = (yearB * 10000) + (monthB * 100) + dayB;         // get adjusted date

               if ((index < 7 || (index == 7 && cal_time >= 700)) && (DateB == 20120612 || DateB == 20120717 || DateB == 20120807 || DateB == 20120918)) {         // if day of event

                  daysArray.days[index] = 1;        // set ok in array

                  if (max < index) max = index;     // bump max days to view if necessary
               }
            }
         }
      }
   }


   //
   //  setup time for display
   //
   String s_time = "";

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

   // flag for determining if we are using 365 day calendars  (NOTE:  we can get rid of this now, but must set config for each club!!!!)
   //boolean use365 = false; // ritzcarlton, oaklandhills, ccrockies, peninsula*(bad), northland, invernessclub**
   if (IS_TLT || club.equals( "oakmont" ) || club.equals( "sauconvalleycc" ) || club.equals( "ritzcarlton" ) || club.equals( "merion" ) ||
       club.equals( "oaklandhills" ) || club.equals( "ccrockies" ) || club.equals( "peninsula" ) || club.equals( "invernessclub" ) ||
       club.equals( "northland" )) {

       //use365 = true;
       max = 365; // temp
   }

  
   // Create Json response for later use
    Gson gson_obj = new Gson();

    Map<String, Object> hashMap = new HashMap<String, Object>();

    // Group the data we want to send to javascript in a hash map
    hashMap.put("daysArray", daysArray);
    hashMap.put("cal", cal);
    hashMap.put("max", max);
    hashMap.put("IS_TLT", IS_TLT);

    // Ecode the data with json
    String jsonHashMap = gson_obj.toJson(hashMap);
               
   
   // If in Json mode, output json string.
   if (json_mode) {
       
       out.print(jsonHashMap);
   
    //  If not Json, and not mobile user, do standard output   
   } else if ((mobile == 0) && (!json_mode)) {

      //
      //  Non-Mobile Browser - Build the HTML page to prompt user for a specific date
      //
      if (!new_skin) { 
          out.println(SystemUtils.HeadTitle2("Member Select Date Page"));
          // include files for dynamic calendars
          out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
          out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");
      } else {
          Common_skin.outputHeader(club, activity_id, "Member Select Date Page", false, out, req);
          //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
          //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");

         // out.println("<script type=\"text/javascript\">");
          //out.println("<!-- ");
          //out.println("function sd(pCal, pMonth, pDay, pYear) {");
          //out.println(" f = document.forms[\"frmLoadDay\"];");
          //out.println(" f.calDate.value = pMonth + '/' + pDay + '/' + pYear;");
          //out.println(" f.submit();");
          //out.println("}");

          //out.println("// -->");
          //out.println("</script>");

      }
      out.println("<script type=\"text/javascript\">");
      out.println("<!-- ");
      out.println("function showInfo() {");
      out.println(" var w = window.open('/v5/member_newclock.htm','clockPopup','width=550,height=370,scollbars=0,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
      out.println(" w.creator = self;");
      out.println("}");
      out.println("// -->");
      out.println("</script>");
      out.println("</head>");

      if (!new_skin) {
            out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");
            SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
            out.println("<center><font face=\"Verdana, Arial, Helvetica, Sans-serif\"></font>");

            out.println("<table border=\"0\" align=\"center\">");
            out.println("<tr><td>");

            // Start of instruction table
            out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
            out.println("<tr><td align=\"left\">");
            out.println("<font color=\"#FFFFFF\" size=\"3\"><p align=\"center\">");

      } else {
          
            String breadCrumb = "Make a Tee Time";
            
            if (IS_TLT) breadCrumb = "Select a Date";
          
            Common_skin.outputBody(club, activity_id, out, req);
            Common_skin.outputTopNav(req, club, activity_id, out, con);
            Common_skin.outputBanner(club, activity_id, clubName, (String)session.getAttribute("zipcode"), out, req);
            Common_skin.outputSubNav(club, activity_id, out, con, req);
            Common_skin.outputPageStart(club, activity_id, out, req);
            Common_skin.outputBreadCrumb (club, activity_id, out, breadCrumb, req);
            Common_skin.outputLogo(club, activity_id, out, req);
            
            
            //
            //**********************************************
            //   Check for Member Notice from Pro
            //**********************************************
            //
            String memNoticeMsg = verifySlot.checkMemNotice(thisDate, 0, 0, 0, "", day_name, "teetime_cal", false, 0, con); 

            if (!memNoticeMsg.equals("")) {

                int notice_mon = 0;
                int notice_tue = 0;
                int notice_wed = 0;
                int notice_thu = 0;
                int notice_fri = 0;
                int notice_sat = 0;
                int notice_sun = 0;

                String notice_msg = "";

                try {

                    // Get relevent member notice data from database
                    ResultSet notice_rs = null;
                    PreparedStatement notice_pstmt = con.prepareStatement(
                            "SELECT mon, tue, wed, thu, fri, sat, sun, message "
                            + "FROM mem_notice "
                            + "WHERE sdate <= ? AND edate >= ? AND "
                            + "teetime_cal = 1 AND activity_id = 0");     

                    notice_pstmt.clearParameters();      
                    notice_pstmt.setLong(1, thisDate);
                    notice_pstmt.setLong(2, thisDate);
                    
                    notice_rs = notice_pstmt.executeQuery();    

                   // out.println("<div class=\"sub_instructions\"><h3>Important Notice:</h3>");
                    out.println("<div class=\"sub_instructions noticeBox\">");                // Let the pros add their own heading

                    while (notice_rs.next()) {

                        notice_mon = notice_rs.getInt("mon");
                        notice_tue = notice_rs.getInt("tue");
                        notice_wed = notice_rs.getInt("wed");
                        notice_thu = notice_rs.getInt("thu");
                        notice_fri = notice_rs.getInt("fri");
                        notice_sat = notice_rs.getInt("sat");
                        notice_sun = notice_rs.getInt("sun");
                        notice_msg = notice_rs.getString("message");

                        if ((notice_mon == 1 && day_name.equals("Monday")) || (notice_tue == 1 && day_name.equals("Tuesday")) || (notice_wed == 1 && day_name.equals("Wednesday"))
                                || (notice_thu == 1 && day_name.equals("Thursday")) || (notice_fri == 1 && day_name.equals("Friday")) || (notice_sat == 1 && day_name.equals("Saturday"))
                                || (notice_sun == 1 && day_name.equals("Sunday"))) {
                            out.println("<p>" + notice_msg + "</p>");
                        }

                    }  // end WHILE loop

                    out.println("</div>");

                    notice_pstmt.close();

                } catch (Exception e1) {

                    out.println(SystemUtils.HeadTitle("DB Error"));
                    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<BR><BR><H2>System Error</H2>");
                    out.println("<BR><BR>Unable to access the Database.");
                    out.println("<BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, contact your club staff.");
                    out.println("<BR><BR>" + e1.getMessage());
                    out.println("<BR><BR>");
                    out.println("<a href=\"Member_announce\">Return</a>");
                    out.println("</BODY></HTML>");
                    out.close();
                }

            } // end of member notice
            
            
            // Start Instructions div
            out.println("<div class=\"main_instructions pageHelp\" data-ftHelpTitle=\"Using the calendar\">");
            out.println("<p>");
            
      }    // end of new skin check
     
        

         
         if (IS_TLT) {

             out.println("<b>Select a date by using the calendars below.</b></p>");

         } else {
          
            out.print("<b>To view a day's Tee Sheet, click on the date below.</b></p>");
            out.print("<p class=\"noteIndent\"><b>Note:</b> You are allowed to view tee sheets for the next " + max + " days, however you");
            out.print(" can only make normal tee times on dates established by the golf shop (in green).");

            //
            //  Custom directions
            //
            if (day90 == true || club.equals( "northhills" )) {     // if custom required

               out.print(" In addition, you may make ");
               if (day90 == true) {
                  out.print("guest " + sysLingo.TEXT_reservations + " 90 days in ");
                  if (club.equals( "milwaukee" )) {
                     out.print("advance on Tuesday through Friday afternoons for groups consisting of 2 or 3 guests starting on the Back 9 only (days in red).<br>");
                  } else {
                     out.print("advance for groups consisting of 2 or 3 guests starting on the Back 9 only.<br>");
                  }
               } else {
                  out.print("guest " + sysLingo.TEXT_reservations + " 30 days in advance for groups consisting of 2 or 3 guests.");
               }

            } else {

               out.print(" Any other colors represent days that some specific tee time requests are allowed.");
            }
            
            if (club.equals("estanciaclub")) {
                out.println("<br>Dates displayed in yellow are available for Advance Guest Time booking only.");
            }
            
            out.println("</p>"); // end noteIndent

            if (club.equals( "congressional" )) {        // Congressional - inform user about guest times

               out.println("<p class=\"noteIndent\"><b>Additional Note:</b> There are some guest times available up to 29 days in advance on the Open Course.</p>");
            }

            
            
            if (!new_skin) out.println("</font>");
         }

         if (club.equals("boulder")) {
             
             out.println("</div>");
             
         } else {
             
            if (!new_skin) {

                 out.println("</td></tr>");
                 out.println("<tr bgcolor=\"#F5F5DC\"><td align=\"left\"><font size=\"2\">");

            } else {

                // End Instructions Div
                out.println("</div>");
                // start tt1_right
                out.println("<div id=\"tt1_right\">");
                out.println("<div class=\"sub_instructions pageHelp\" data-ftHelpTitle=\"About calendar dates and time\">");

            }

             if (!IS_TLT) {

                 out.println("Each day of the week has its own 'days in advance' and 'time of day' values to determine when they become available.  They are:");

                 if (new_skin) {
                    out.println("<ul class=\"days_in_advance_list\">");
                    out.println("<li><b>Sun:</b> " +parm.advdays1+ " days at " +parm.advhr1+ ":" +Utilities.ensureDoubleDigit(parm.advmin1)+ " " +parm.advam1 + "</li>");
                    out.println("<li><b>Mon:</b> " +parm.advdays2+ " days at " +parm.advhr2+ ":" +Utilities.ensureDoubleDigit(parm.advmin2)+ " " +parm.advam2 + "</li>");
                    out.println("<li><b>Tue:</b> " +parm.advdays3+ " days at " +parm.advhr3+ ":" +Utilities.ensureDoubleDigit(parm.advmin3)+ " " +parm.advam3 + "</li>");
                    out.println("<li><b>Wed:</b> " +parm.advdays4+ " days at " +parm.advhr4+ ":" +Utilities.ensureDoubleDigit(parm.advmin4)+ " " +parm.advam4 + "</li>");
                    out.println("<li><b>Thu:</b> " +parm.advdays5+ " days at " +parm.advhr5+ ":" +Utilities.ensureDoubleDigit(parm.advmin5)+ " " +parm.advam5 + "</li>");
                    out.println("<li><b>Fri:</b> " +parm.advdays6+ " days at " +parm.advhr6+ ":" +Utilities.ensureDoubleDigit(parm.advmin6)+ " " +parm.advam6 + "</li>");
                    out.println("<li><b>Sat:</b> " +parm.advdays7+ " days at " +parm.advhr7+ ":" +Utilities.ensureDoubleDigit(parm.advmin7)+ " " +parm.advam7 + "</li>");
                    out.println("</ul>");

                 } else {

                     out.println("<br>");

                     if (parm.advmin1 < 10) {
                        out.println("&nbsp;&nbsp;&nbsp;<b>Sun</b> " +parm.advdays1+ ", " +parm.advhr1+ ":0" +parm.advmin1+ " " +parm.advam1);
                     } else {
                        out.println("<b>Sun</b> " +parm.advdays1+ ", " +parm.advhr1+ ":" +parm.advmin1+ " " +parm.advam1);
                     }
                     if (parm.advmin2 < 10) {
                        out.println(" &nbsp;&nbsp; <b>Mon</b> " +parm.advdays2+ ", " +parm.advhr2+ ":0" +parm.advmin2+ " " +parm.advam2);
                     } else {
                        out.println(" &nbsp;&nbsp; <b>Mon</b> " +parm.advdays2+ ", " +parm.advhr2+ ":" +parm.advmin2+ " " +parm.advam2);
                     }
                     if (parm.advmin3 < 10) {
                        out.println(" &nbsp;&nbsp; <b>Tue</b> " +parm.advdays3+ ", " +parm.advhr3+ ":0" +parm.advmin3+ " " +parm.advam3);
                     } else {
                        out.println(" &nbsp;&nbsp; <b>Tue</b> " +parm.advdays3+ ", " +parm.advhr3+ ":" +parm.advmin3+ " " +parm.advam3);
                     }
                     if (parm.advmin4 < 10) {
                        out.println(" &nbsp;&nbsp; <b>Wed</b> " +parm.advdays4+ ", " +parm.advhr4+ ":0" +parm.advmin4+ " " +parm.advam4);
                     } else {
                        out.println(" &nbsp;&nbsp; <b>Wed</b> " +parm.advdays4+ ", " +parm.advhr4+ ":" +parm.advmin4+ " " +parm.advam4);
                     }
                     if (parm.advmin5 < 10) {
                        out.println(" &nbsp;&nbsp; <b>Thu</b> " +parm.advdays5+ ", " +parm.advhr5+ ":0" +parm.advmin5+ " " +parm.advam5);
                     } else {
                        out.println(" &nbsp;&nbsp; <b>Thu</b> " +parm.advdays5+ ", " +parm.advhr5+ ":" +parm.advmin5+ " " +parm.advam5);
                     }
                     if (parm.advmin6 < 10) {
                        out.println(" &nbsp;&nbsp; <b>Fri</b> " +parm.advdays6+ ", " +parm.advhr6+ ":0" +parm.advmin6+ " " +parm.advam6);
                     } else {
                        out.println(" &nbsp;&nbsp; <b>Fri</b> " +parm.advdays6+ ", " +parm.advhr6+ ":" +parm.advmin6+ " " +parm.advam6);
                     }
                     if (parm.advmin7 < 10) {
                        out.println(" &nbsp;&nbsp; <b>Sat</b> " +parm.advdays7+ ", " +parm.advhr7+ ":0" +parm.advmin7+ " " +parm.advam7);
                     } else {
                        out.println(" &nbsp;&nbsp; <b>Sat</b> " +parm.advdays7+ ", " +parm.advhr7+ ":" +parm.advmin7+ " " +parm.advam7);
                     }
                       out.println("<br>");
                 }

             } else {
               out.println("<b>Note:</b> ");
             }
             out.println("The times are based on the ForeTees server time shown in the clock displayed next to today's date.");
             if (club.equals("wingedfoot")) out.println("<br>You can view a report detailing your current quota usage for Guest Retrictions by clicking the 'Guest Quota Report' button. &nbsp;");

         }         
         
         if (!new_skin) {

            out.println("</font></td></tr></table>");

            out.println("<p align=\"center\"><font size=\"2\" style=\"font-size:14px\">");
            out.println("Today's date is:&nbsp;&nbsp;<b>" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</b>");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The Server Time is:&nbsp;&nbsp;");

              int offset = 2; // default
              String userAgent = req.getHeader("User-Agent").toLowerCase();
              if (userAgent.indexOf("mac") != -1) {
                  offset = 3;
                  if (userAgent.indexOf("firefox") != -1) {
                      offset = 5;
                  }
              } else if (userAgent.indexOf("windows") != -1) {
                  if (userAgent.indexOf("firefox") != -1) {
                      offset = 1;
                  }
              }

               //out.println("<!-- UA=" + userAgent + " -->");

            //  add iframe to display the server clock
            out.println("<iframe src=\"clock?club=" + club + "&new_skin="+((String)session.getAttribute("new_skin"))+"&bold&bgcolor=inherit\" id=ifClock style=\"width:80px;height:16px;position:relative;top:" + offset + "px\" scrolling=no frameborder=no></iframe>");


            out.println("<font size=2>&nbsp;&nbsp;");
            out.println("(<a href=\"javascript:void(0);\" onclick=\"showInfo(); return false;\">How is this clock used?</a>)");
            out.println("</font></font>");
            out.println("</p>");

            
        } else {
             
             if (!club.equals("boulder")) {
                // end sub_instructions
                out.println("</div>");

                //  End tt1_right
                out.println("</div>");
             }
                
            // start left_container
            out.println("<div class=\"left_container\">");
            // start tt1_left
            out.println("<div id=\"tt1_left\">");

            out.println("<p class=\"server_time\">");
            out.println("Today's date is: <b>" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</b>");
            out.println("The Server Time is: <b class=\"jquery_server_clock\" data-ftclub=\"" + club + "\"></b>");
            //  help link to describe the server clock
            out.println("<a class=\"tip_text\" href=\"javascript:void(0);\" onclick=\"showInfo(); return false;\">How is this clock used?</a>");
            out.println("</p>");
   
        }
 
      /*
      if (cal_am_pm == 0) {      // remove old clock and refresh link
         out.println(" AM");
      } else {
         out.println(" PM");
      }
      out.println(" " + adv_zone + "</b>");
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<a href=\"Member_select\" title=\"Refresh\" alt=\"Refresh Time\">");
      out.println("Refresh Time</a>");
      */

      

      
      
      if (new_skin) {
         
            //  End tt1_left
            out.println("</div>");
            // start refresh
            out.println("<div id=\"refresh\">");
            
      }

      //  add refresh link in case they still need/want it
      if (!new_skin) out.println("<p align=\"center\"><font size=2>");
      out.println("<a class=\"standard_button\" href=\"Member_select\" title=\"Refresh\">"); // alt=\"Refresh Calendars\"
      out.println("Refresh Calendar<span class=\"plural\">s</span></a>");
      if (!new_skin) out.println("</font></p>");
       
      if (club.equals("estanciaclub")) {

          int advTimeCount = 0;

          advTimeCount = verifyCustom.checkEstanciaAdvTimes(user, con);

          out.println("<p align=\"center\"><b>You currently have " + ((4 - advTimeCount > 0) ? (4 - advTimeCount) : 0) + " out of 4 Advance Guest Times remaining.</b></p>");
      }

   /*
      // add course selection and calendars
      cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
      day = 1;
   */
      if (!new_skin) {
          
        out.println("</td></tr>");
        out.println("<tr>");
        out.println("<td align=\"center\">");
        out.println("<font size=\"2\">");
        out.println("<br><br>");
      

        // this is the form that gets submitted when the user selects a day from the calendar
        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\" name=\"frmLoadDay\">");
        out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"thisDate\" value=\"" +thisDate+ "\">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"select\">");

      } else {

          //  End refresh
          out.println("</div>");
          // start lg_calendar
          //out.println("<div id=\"sm_calendar\">");
          // start course select
          out.println("<div class=\"select\">");

           // this is the form that gets submitted when the user selects a day from the calendar
          out.println("<form action=\""+((IS_TLT) ? "MemberTLT_sheet" : "Member_sheet")+"\" method=\"post\" name=\"frmLoadDay\">");
          out.println("<input type=\"hidden\" name=\"calDate\" value=\"\">");
          
      }



         //
         //  If multiple courses, then add a drop-down box for course names
         //
         if (multi != 0) {           // if multiple courses supported for this club

            if (club.equals("merion")) {      // Merion members can only view East Course

               out.println("<input type=\"hidden\" name=\"course\" value=\"East\">");

            } else {      // not Merion

               /*
               index = 0;
               courseName = course[index];      // get first course name from array

               String firstCourse = course[0];    // default to first course
                  */

               courseName = course.get(0);            // get first course name from array

               String firstCourse = courseName;       // default to first course


               if (!parm.default_course_mem.equals( "" )) {

                  firstCourse = parm.default_course_mem;            // get default course from club5 !!!!

               } else {

                  //
                  //    NOTE:  Replace these customs by manually putting the default value in club5!!!!!!!!!!!!!!
                  //

                  //
                  //   List -ALL- option first for these clubs
                  //
                  if (club.equals("lakewoodranch") || club.equals("pelicansnest") || club.equals("fairbanksranch") ||
                        club.equals( "lakes" ) || club.equals( "edisonclub" ) || club.equals("imperialgc") ||
                        club.equals( "jonathanslanding" ) || club.equals("mediterra") || club.equals("blackdiamondranch") ||
                        club.equals("pinery") || club.equals("international") || club.equals( "loscoyotes" )) {

                     firstCourse = "-ALL-";
                  }

                  if (club.equals("fortcollins")) {              // if Fort Collins (& Greeley)

                     if (mtype.endsWith( "Greeley" )) {          // if Greeley member
                        firstCourse = "Greeley CC";
                     } else if (mtype.endsWith("Fox Hill")) {
                        firstCourse = "Fox Hill CC";
                     } else {
                        firstCourse = "Fort Collins CC";
                     }
                  }

                  if (club.equals("sciotoreserve")) {
                        if (mtype.startsWith("Kinsale")) {
                           firstCourse = "Kinsale";
                        } else {   // Scioto Reserve member
                           firstCourse = "Scioto Reserve";
                        }
                  }

                  // Set default course - Case #1433
                  if (club.equals("oakhillcc")) {

                        firstCourse = "East Course";
                  }
               }

               index = 0;
               cMax = course.size();     // number of courses

               out.println("<b>Course:</b>&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"course\">");

               while (!courseName.equals( "" ) && index < cMax) {

                  skipCourse = false;

                  if (club.equals("olyclub") && courseName.equals( "Cliffs" )) skipCourse = true;       

                  if (skipCourse == false) {

                     if (courseName.equals( firstCourse )) {
                           out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                     } else {
                           out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                     }
                  }
                  index++;
                  if (index < cMax) {
                     courseName = course.get(index);      // get course name from array
                  }
               }
               out.println("</select>");

               if (!new_skin) {
                  out.println("<br>");
               }

            }

         } else {
            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
         }


         //
         //   NOTE:  this form is rebuilt in jquery.foreTeesMemberCalendar.js !!!!!!!!!!!!
         //


         //
         //  CUSTOM - add consecutive tee time selection to speed process
         //
         //if (!IS_TLT && parm.constimesm > 1 && !club.equals("plantationpv")) {    // USE THIS IF WE MAKE THIS STANDARD !!! (plantationpv forces default to 0)
         if (!IS_TLT && parm.constimesm > 1 && (club.equals("lakewoodranch") || club.equals("demotom"))) {      // if consecutive tee times supported

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label for=\"select_times\"><b># of Tee Times to Request:</b></label>&nbsp;&nbsp;<select size=\"1\" name=\"select_times\" id=\"select_times\" style=\"width:50px\">");
            out.println("<option selected value=\"1\">1</option>");
            for (int k = 2; k <= parm.constimesm; k++) {
               out.println("<option value=\"" + k + "\">" + k + "</option>");
            }
            out.println("</select>");

         } else {

            out.println("<input type=\"hidden\" name=\"select_times\" value=\"1\">");  // parm need for jquery.foreTeesMemberCalendar.js

            //
            //  Custom for Philadelphia Cricket Club - add seamless link to their Recip site
            //
            if (club.equals("philcricket")) {         

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<a class=\"standard_button\" href=\"http://web.foretees.com/v5/servlet/Login?clubname=philcricketrecip&caller=PDG4735&user_name=" +user+ "\" "
                       + "target=\"_blank\" style=\"background-color: yellow;\" title=\"Link to Recip Site\">"); 
               out.println("Go to RECIP Site</a>");
            }
         }





         if(new_skin){

            //  End course select
            out.println("</div>");
            // Start calendar container

            out.println("<div id=\"member_select_calendars\">");
            out.println("<div id=\"member_select_calendar1\" class=\"calendar member primary\" data-ftjson=\""+StringEscapeUtils.escapeHtml(jsonHashMap)+"\"></div>");
            out.println("<div id=\"member_select_calendar2\" class=\"calendar member secondary\" data-ftjson=\""+StringEscapeUtils.escapeHtml(jsonHashMap)+"\"></div>");

         } else {

            out.println("</form>");

            // use 365 day calendar
            out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

            out.println("<div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

            out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

            out.println("<div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

            out.println("</td>\n<tr>\n</table>");

            //Calendar cal_date = new GregorianCalendar(); //Calendar.getInstance();
            //int cal_year = cal_date.get(Calendar.YEAR);
            //int cal_month = cal_date.get(Calendar.MONTH) + 1;
            //int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

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
               out.println("g_cal_month[0] = " + month + ";");
               out.println("g_cal_year[0] = " + year + ";");
               out.println("g_cal_beginning_month[0] = " + month + ";");
               out.println("g_cal_beginning_year[0] = " + year + ";");
               out.println("g_cal_beginning_day[0] = " + day + ";");
               out.println("g_cal_ending_month[0] = " + month + ";");
               out.println("g_cal_ending_day[0] = 31;");
               out.println("g_cal_ending_year[0] = " + year + ";");

               cal.add(Calendar.MONTH, 1); // add a month
               int cal_year = cal.get(Calendar.YEAR);
               int cal_month = cal.get(Calendar.MONTH) + 1; // month is zero based

               out.println("g_cal_month[1] = " + cal_month + ";");
               out.println("g_cal_year[1] = " + cal_year + ";");
               out.println("g_cal_beginning_month[1] = " + cal_month + ";");
               out.println("g_cal_beginning_year[1] = " + cal_year + ";");
               out.println("g_cal_beginning_day[1] = 0;");

               Calendar cal_date = new GregorianCalendar(year, month - 1, day);

               cal_date.add(Calendar.DAY_OF_MONTH, max); // add the days in advance
               cal_year = cal_date.get(Calendar.YEAR);
               cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
               int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
               out.println("g_cal_ending_month[1] = " + cal_month + ";");
               out.println("g_cal_ending_day[1] = " + cal_day + ";");
               out.println("g_cal_ending_year[1] = " + cal_year + ";");

               out.print("var daysArray = new Array(");
               int js_index = 0;
               for (js_index = 0; js_index <= max; js_index++) {
                  if (IS_TLT) out.print("0"); else out.print(daysArray.days[js_index]);
                  if (js_index != max) out.print(",");
               }
               out.println(");");

               out.println("var max = " + max + ";");

            out.println("</script>");

            out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
            out.println("<script type=\"text/javascript\">\ndoCalendar('1');\n</script>");

            //
            // end of calendar row
            //
            out.println("</td></tr></table>");
            out.println("</form>");


            out.println("<form method=\"get\" action=\"Member_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
            out.println("</form>");

      }

      if (!new_skin) {

         out.println("</td></tr></table>");
         //
         //  End of HTML page
         //
         out.println("</center></body></html>");
        
      } else {
          
         // End calendar container
         out.println("</div>");
         out.println("<div class=\"clearFix\"></div>"); // clear the float
        
        
         if (club.equals("interlachen")) {

             guestReport(thisDate, user, club, out, con);     // display the member's guest quota counts
 
         }      // end of Interlachen Guest Quotas

                  
         
         if (!IS_TLT) {

            //  add check box for option to show more tee times on tee sheet page (jump past calendar and instructions)
            out.print("<p id=\"hide_calendar_select\">"
                    + "<input type=\"checkbox\" name=\"select_jump\" id=\"select_jump\" value=\"1\"" + ((tee_sheet_jump == 1) ? " checked" : "") + ">");

            out.print("<label for=\"select_jump\">Position tee sheet to show more tee times (skips the calendar and instructions).</label>"
                    + "<span>Note: Use the Settings from menu above to set a default for this.</span>"
                    + "</p>");
         }
        
         out.println("</form>");     
         
                
         if (club.equals("wingedfoot")) {

             out.println("<form method=\"get\" action=\"Member_searchpast\">");
             out.println("<input type=hidden name=subtee value=gquota>");
             out.println("<input type=submit value=\" Guest Quota Report \">");
             out.println("</form>");
         }

        
         // End Left container
         out.println("</div>");
         //
         //  End of HTML page
         //
         Common_skin.outputPageEnd(club, activity_id, out, req);
        
      }       

   } else if(!json_mode) {

      //
      //  Mobile User1200
      //
      out.println(SystemUtils.HeadTitleMobile("ForeTees Member Date Selection"));
      out.println(SystemUtils.BannerMobile());

      out.println("<div class=\"smheadertext\">" + day_name + "&nbsp;" + month + "/" + day + "/" + year + "</div>");
      out.println("<div class=\"headertext\">Make a Tee Time </div>");
      out.println("<div class=\"smheadertext\">The Server Time is:");
      out.println("<iframe src=\"clock?club=" + club + "&bold&bgcolor=inherit\" id=ifClock style=\"width:100px;height:16px;position:relative;top:2px\" scrolling=no frameborder=no></iframe>");
      out.println("</div>");

      // this is the form that gets submitted when the user selects a day from the list below
      out.println("<form action=\"Member_sheet\" method=\"post\">");

      //
      //  If multiple courses, then add a drop-down box for course names
      //
      if (multi != 0) {           // if multiple courses supported for this club

         if (club.equals("merion")) {      // Merion members can only view East Course

            out.println("<input type=\"hidden\" name=\"course\" value=\"East\">");

         } else {      // not Merion

            courseName = course.get(0);            // get first course name from array

            String firstCourse = courseName;       // default to first course


            if (!parm.default_course_mem.equals( "" )) {

               firstCourse = parm.default_course_mem;            // get default course from club5 !!!!

            } else {

                if (club.equals("fortcollins")) {              // if Fort Collins (& Greeley)

                    if (mtype.endsWith( "Greeley" )) {          // if Greeley member
                        firstCourse = "Greeley CC";
                    } else if (mtype.endsWith("Fox Hill")) {
                        firstCourse = "Fox Hill CC";
                    } else {
                        firstCourse = "Fort Collins CC";
                    }
                }

               // Set default course - Case #1433
               if (club.equals("oakhillcc")) {

                   firstCourse = "East Course";
               }
            }

            index = 0;
            cMax = course.size();     // number of courses

            out.println("<div class=\"smheadertext\">Select a Course:");
            out.println("<select size=\"1\" name=\"course\">");

            while ((!courseName.equals( "" )) && (index < cMax)) {
               
               skipCourse = false;
               
               if (club.equals("olyclub") && courseName.equals( "Cliffs" )) skipCourse = true;       

               if (skipCourse == false) {

                  if (courseName.equals( firstCourse )) {
                      out.println("<option selected value=\"" + courseName + "\">" + courseName + "</option>");
                  } else {
                      out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
                  }
               }
               
               index++;
               if (index < cMax) {
                  courseName = course.get(index);      // get course name from array
               }
            }
            out.println("</select>");
            out.println("<br>");
            out.println("</div>");
         }

      } else {
         out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
      }


      out.println("<div class=\"smheadertext\">Tee Sheet - Display:");
         out.println("<select size=\"1\" name=\"displayOpt\">");
         out.println("<option selected value=\"All\">Entire Day</option>");    // default - until we add to settings!
         out.println("<option value=\"Morning\">Morning</option>");
         out.println("<option value=\"Mid-Day\">Mid-Day</option>");
         out.println("<option value=\"Afternoon\">Afternoon</option>");
         out.println("<option value=\"Available\">Open Times Only</option>");
         out.println("</select>");
      out.println("</div>");


      //
      //   Only display the days that this member can access, or 30 days, whichever is less!!
      //
      out.println("<div>Available Dates</div>");

      out.println("<div class=\"content\"><ul>");

      //
      //  Display a list of dates that this member can access
      //
      if (max > 30) max = 30;                  // if member can view more than 30 days, then liimit them to 30 (too keep list shorter)

      for (i=0; i<(max+1); i++) {              // check today and the next 30 days to see which the member has access to

         buttonColor = "lightgrey";            // default to View Only color

         if (daysArray.days[i] == 1) {         // if days in advance for this day of the week is within this range

            buttonColor = "lightgreen";        // indicate user can access tee times for this day

         } else if (daysArray.days[i] == 2) {  // lottery on this day ?

            buttonColor = "sienna";            // indicate user can access lottery for this day
         }

         out.println("<li><input type=submit value=\" " +day_name+ ", " +mm_name+ " " +day+ " \" name=\"i" +i+ "\" style=\"background-color:" +buttonColor+ "\"></li>");

         //
         //  Get next day
         //
         cal.add(Calendar.DATE,1);                     // get next day's date
         month = cal.get(Calendar.MONTH)+1;
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);     // day of week (01 - 07)

         day_name = day_table[day_num];               // get name for day
         mm_name = mm_table[month];                   // get name for month
      }

      out.println("</ul></div>");
      out.println("<div><p>&nbsp;</p><p>&nbsp;</p></div>");    //  add a couple of blank lines at the bottom to allow for the IOS Nav Bar (iPhones)
      out.println("</form></body>");
      out.println("</html>");

   }    // end of IF Mobile or Standard user

   out.close();

 }  // end of doGet
 
 
 
 private void guestReport(long date, String user, String club, PrintWriter out, Connection con) {
   

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    int id = 0;
    int num_guests = 0;
    
    String name = "";
    String per = "";
     
     
    //
    // Display the member's current guest quota counts
    //
    //    note:  thisDate = today 
    //
    int quotaCount = verifySlot.getGuestQuotaCount(date, 0, con);      // get number of guest quotas for golf effective today (end date > today)

    if (quotaCount > 0) {        // if any

        // Use the Instructions div
        out.println("<BR><div class=\"main_instructions\">");
        out.println("<p><strong>Your Membership's Current Guest Quota Counts:</strong></p>");

        try {

            // Get guest quotas for golf

            pstmt = con.prepareStatement(
                    "SELECT id, name, num_guests, per "
                    + "FROM guestqta4 "
                    + "WHERE edate >= ? AND activity_id = 0");     

            pstmt.clearParameters();      
            pstmt.setLong(1, date);

            rs = pstmt.executeQuery();    

            while (rs.next()) {

                id = rs.getInt("id");
                name = rs.getString("name");                 // Quota name
                num_guests = rs.getInt("num_guests");        // Max guests allowed
                per = rs.getString("per");                   // Per Member or Family

                if (club.equals("interlachen")) {

                    if (name.equals("Low Rate Guests")) {

                        quotaCount = verifySlot.getGuestQuotaCountMem(user, club, id, 0, con);    // get number of guests used for this member and this guest quota

                        out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;You have currently booked " +quotaCount+ " of your " +num_guests+ " allowed <strong>Low Rate Guests</strong>.</p>");
                               
                    } else if (name.equals("Advanced Guests")) {

                        quotaCount = verifySlot.getGuestQuotaCountMem(user, club, id, 0, con);    // get number of guests used for this member and this guest quota
                     
                        out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;You have currently booked " +quotaCount+ " of your " +num_guests+ " allowed <strong>Advanced Tee Times</strong>.</p>");       
                    }
                }    // end of Interlachen

            }  // end WHILE loop

        } catch (Exception e1) {

            Utilities.logError("Member_select.guestReport: err=" + e1.toString());
            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}
            try { pstmt.close(); }
            catch (Exception ignore) {}
        }

        out.println("</div>");    // end the display div
    }
     
 }    // end of guestReport

}
