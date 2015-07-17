
/***************************************************************************************
 *   Member_teelist:  This servlet will search teecurr for this member and display
 *                    a list of current tee times.  Also, search evntsup for any
 *                    events and lreqs for any lottery requests.
 *
 *
 *   called by:  member_main.htm
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *        3/31/14   Columbine CC (columbine) - Updated custom to hide 5th player position with new dates for 2014 (case 1640).
 *        3/06/14   La Grange CC (lagrangecc) - Do not display the 5th player slot for members (case 2382).
 *        3/05/14   Added some sysLingo for Kopplin & Kuebler (Greg DeRosa) for their Meeting Scheduling site.
 *        3/03/14   Edina CC - custom to allow adults to access their dependents' event registrations - like Denver CC (case 2378).
 *        2/20/14   Chattanooga G & CC (chattanoogagcc) - Do not display the 5th player slot for members (case 2372).
 *        9/10/13   Clinic Series group lessons (FlxRez only at the moment) will now properly display on dates where the clinic takes place if the member is signed up.
 *        7/02/13   Fixed issue where FlxRez reservations weren't showing up when "ALL" was selected.
 *        5/29/13   Pass the club name to Utilities.checkRests for customs.
 *        3/15/13   Estero CC (esterocc) - Display 12 months of calendars.
 *        3/07/13   St Cloud CC (stcloudcc) - Display 12 months of calendars.
 *        3/07/13   St Cloud CC (stcloudcc) - Always default activity selection to "ALL" when first entering page.
 *        2/26/13   Denver CC - custom to allow adults to access their dependents' group lessons (NOT USED YET).
 *        2/20/13   Denver CC (denvercc) - Always default activity selection to "ALL" when first entering page.
 *        2/11/13   Do not show the activity selection list if FLEXWEBFT (Connect Premier).
 *        2/07/13   CC of York (ccyork) - Do not display the 5th player slot for members (case 2224).
 *        2/05/13   Rehoboth Beach CC (rehobothbeachcc) - Do not display the 5th player slot for members (case 2222).
 *        1/30/13   Excluded dining events that have been cancelled
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       12/05/12   When "ALL" is selected from the activity selection drop-down, the name of the activity an event belongs to will be included in it's name on the calendar.
 *       12/04/12   Mirabel (mirabel) - Always default activity selection to "ALL" when first entering page.
 *       12/04/12   FlxRez events will now display the start time of the event, as is done for Shotgun events for Golf.
 *       11/29/12   Changed the events links to pass the baseurl for switching activities - all events should now be clickable links regardless of activity
 *       11/21/12   An activity_id value will now be passed for event links to ensure the modal window renders itself according to the activity_id of the event.
 *       11/19/12   Mirabel (mirabel) - Display 12 months of calendars.
 *       11/05/12   Prevented calendar legend items from being displayed more than once when viewing All activities, and other general code cleanup.
 *       11/05/12   Will now try to detect if an activity has no children activities, and if so, will display the My Calendar menu instead of the reservations menu.
 *       10/30/12   Dining events now display the reservation time instead of the event start time
 *       10/16/12   Denver CC - custom to allow adults to access their dependents' event registrations.
 *        9/18/12   Adjust the top of the page for Dining to allow space for the logo.
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        5/16/12   Fort Collins CC (fortcollins) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *        5/16/12   Sierra View CC (sierraviewcc) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *        4/24/12   Updated calendar display of events so that no time is shown for teetime events, and the correct actual start time of the event is shown for shotgun events.
 *        4/03/12   Ballantyne CC (ballantyne) - Added custom to hide Event-related items from the legend (case ?).
 *        4/03/12   Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *        4/03/12   Updated calendar to use SystemLingo entries for Lesson verbiage (for custom verbiage such as "Ball Machine").
 *        1/24/12   Change to allow use by Dining
 *        1/19/12   Minor fixes and general db code cleanup
 *        1/18/12   Add new skin changes to servlet.
 *        8/24/11   The Country Club - Brookline (tcclub) - Updated custom to display Guest Only/Outside Events on the member calendar so that it applies to the FlxRez-specific code as well.
 *        7/12/11   Columbine, Estancia, Lakes, RTJ and Colorado - add check for 5-somes allowed.
 *        7/11/11   Wollaston GC (wollastongc) - Removed custom to restrict members from accessing tee sheets for any day on Mondays (case 1819).
 *        7/05/11   Forest Highlands GC (foresthighlands) - Added custom so tee times entered from this page hide the 5th player position during the appropriate date range (case 1613).
 *        5/17/11   Activity selection will be hidden on the golf side if FlxRez Staging mode is turned on.
 *        3/22/11   Added a couple additional fixes to FlxRez processing to fix a bug with multiple single-slot reservations on a given day being combined into one
 *        3/21/11   Updated to pull Activity names from activities table instead of hard-coded Tennis values. Also fixed display of FlxRez reservations
 *                  to display the proper time and be capabel of displaying more than one per day
 *        3/03/11   Updated calendar to include lottery requests that a member originated, but is not a part of.
 *        2/04/11   Fixed calendar not displaying tee times originated by the current member that they or their guests are not a part of.
 *        1/20/11   Added dining events & reservations from ForeTees Dining to the member calendar
 *        1/19/11   Members will now be appropriately blocked from accessing restricted times they are a part of.
 *       10/15/10   The Estancia Club (estanciaclub) - Added a message to display a member's count of unused Advance Guest Times (case 1884).
 *        9/16/10   Edina CC 2010 (edina2010) - Do not allow members to access tee times via the calendar
 *        9/10/10   Do not display event and lottery times that have been drug to the tee sheet but not approved
 *        5/18/10   Fix noAccessAfterLottery custom processing to check for lottery_color in teecurr to determine if lottery processed (case 1827).
 *        4/26/10   Black Rock CC (blackrockcountryclub) - allow 6 months to be displayed.
 *        4/26/10   Brae Burn - do not allow members to access tee times after the lottery has been processed (case 1827).
 *        4/20/10   Bonnie Briar - do not allow members to access tee times after the lottery has been processed (case 1822).
 *        4/16/10   Wollaston GC - do not allow members to access tee times on Monday - all day (case 1819).
 *       12/09/09   When looking for events only check those that are active.
 *       10/30/09   Don't display links for calendar items when logged in under one activity id and viewing another
 *       10/27/09   Changes to allow access to Individual and Group lessons from the Tee Time Calendar
 *       10/07/09   If more than one activity in system, allow members to select the activity.
 *       10/01/09   Check for FlexRez (aka genres) defined for club before checking for activities.
 *        9/02/09   Do not include lottery tee times that have been pre-booked before lottery has been approved (case 1703).
 *        5/06/09   If the days in advance equals the days to view, use the time value to determine if member can view the sheet.
 *        4/30/09   Added status to event signup display to show if the member is Registered or on the Wait List (case 1587).
 *        1/02/09   Timarron CC - add custom days in advance to view tee sheets (case 1595).
 *       10/23/08   Added Wait List signups to list
 *       10/03/08   Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        5/27/08   Do not allow member to access a tee time from calendar if cutoff date/time reached.
 *        5/03/08   Patterson Club - display 6 months of calendars (case 1471).
 *        3/26/08   Do not allow member to select a tee time that has already passed (case 1431).
 *        2/10/08   Robert Trent Jones - display 6 months of calendars.
 *        7/14/07   Desert Highlands - display 12 months of calendars.
 *        6/20/07   Get all lesson times for the day but filter any with num=0 (subsequent times).
 *        5/22/07   Remove custom days to view tee sheets.
 *        5/09/07   DaysAdv array no longer stored in session block - Using call to SystemUtils.daysInAdv
 *        4/12/07   The CC - custom, show all events, including guest only (case #1103).
 *        2/14/07   Mission Viejo - custom, only allow 10 days in advance for members to view tee sheets.
 *       02/05/07   Fix verbiage for TLT system.
 *       01/24/07   Changes for Interlachen Spa.
 *       01/05/07   Changes for TLT system - display notifications on calanders / redirect to MemberTLT_sheet instead
 *       10/18/06   Westchester - allow access to tee times 90 days in adv.
 *        7/11/06   If tee time is during a shotgun event, do not allow link and show as shotgun time.
 *        7/20/05   Forest Highlands - custom, only allow 5 days in advance for members to view tee sheets.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/10/05   RDP Add check for member restriction with ALL member types specified.
 *       11/18/04   Ver 5 - Change to a calendar format.
 *       10/06/04   Ver 5 - add sub-menu support.
 *        2/25/04   RDP Check for Unaccompanied Guest tee times tied to the member.
 *        1/13/04   JAG Modified to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery processing.
 *        2/24/03   Add event sign up processing.
 *        9/18/02   Enhancements for Version 2 of the software.
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
import java.sql.*;          // mysql
//import javax.sql.*;         // postgres
//import javax.naming.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.DaysAdv;
//import com.foretees.common.getRests;
import com.foretees.common.parmClub;
import com.foretees.common.parmDining;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.client.SystemLingo;
import com.foretees.common.Connect;
import com.foretees.common.reqUtil;
import com.foretees.common.reservationUtil;
import com.foretees.common.timeUtil;
import com.foretees.common.htmlTags;
import com.foretees.common.httpConnect;


public class Member_teelist extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
    int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;
    int all_activities = ProcessConstants.ALL_ACTIVITIES;
    
    //  Holidays
    private static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
    private static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
    private static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
    private static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
    private static long Hdate7 = ProcessConstants.tgDay;   // Thanksgiving

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //
        //  Prevent caching so sessions are not mangled
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        //Statement stmt1 = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rsev = null;

        HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

        if (session == null) {

            return;
        }

        String user = (String) session.getAttribute("user");      // get username
        String club = (String) session.getAttribute("club");      // get club name
        String caller = (String) session.getAttribute("caller");
        String mship = (String) session.getAttribute("mship");             // get member's mship type
        String mtype = (String) session.getAttribute("mtype");             // get member's mtype
        String msubtype = (String)session.getAttribute("msubtype");       // get member's sub_type

        Connection con = Connect.getCon(req);            // get DB connection
        String clubName = SystemUtils.getClubName(con);            // get the full name of this club


        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
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

        //String omit = "";
        String ampm = "";
        //String player1 = "";
        //String sfb = "";
        //String submit = "";
        String monthName = "";
        //String name = "";
        String course = "";
        String zone = "";
        String lname = "";
        String lgname = "";
        String ename = "";
        String dayname = "";
        String rest = "";
        String rest5 = "";
        String p5 = "";
        String stime = "";
        String stime2 = "";
        String ltype = "";
        //String url = "";
        String lotteryName = "";
        String lottery_color = "";
        String lotteryText = "";         // replacement text for "Lottery"
        String activity_name = "";
        String selected_act_name = "";
        String selected_act_children = "";
        String item_name = "";

        int root_id = 0;
        long date = 0;
        long edate = 0;
        long sdate = 0;
        long ldate = 0;
        long lottid = 0;
        //long temp = 0;
        long todayDate = 0;
        long tomorrowDate = 0;

        int mm = 0;
        int dd = 0;
        int yy = 0;
        int month = 0;
        int months = 0;
        int day = 0;
        int numDays = 0;
        int today = 0;
        boolean todayFlag = true;
        int day_num = 0;
        int year = 0;
        int hr = 0;
        int min = 0;
        int time = 0;
        //int ptime = 0;
        int ctime = 0;
        int index = 0;
        int i = 0;
        int i2 = 0;
        int max = 30;    // default
        int col = 0;
        int multi = 0;
        int lottery = 0;
        int lstate = 0;
        //int sdays = 0;
        //int sdtime = 0;
        //int edays = 0;
        //int edtime = 0;
        //int pdays = 0;
        //int pdtime = 0;
        int slots = 0;
        //int advance_days = 0;
        int fives = 0;
        //int fivesomes = 0;
        int signUp = 0;
        int fb = 0;
        int proid = 0;
        int etype = 0;
        int wait = 0;
        int lesson_id = 0;
        int person_id = 0;
        int selected_act_id = 0;         // selected activity id (which activities to include in calendar)
        int returned_activity_id = 0;         // activity id returned from queries (used to filter which items to display as links when displaying ALL)
        int custom_int = 0;

        int tmp_tlt = (Integer) session.getAttribute("tlt");
        boolean IS_TLT = (tmp_tlt == 1) ? true : false;
        
        boolean showGuestOnlyEvents = false;    // Used to determine whether guest only events are displayed on the calendar.
        boolean showLessons = true;            // Used to hide page elements related to the Lesson Book if the Lesson Book is not configured.
        boolean flxRezAct_selected = false;     // Signifies that the currently selected activity is a FlxRez activity. Used to simplify conditionals where this needs to be determined
        boolean flxRezAct_sess = false;         // Signifies that the sess_activity_id is a FlxRez activity. Used to simplify conditionals where this needs to be determined
        boolean childlessAct_selected = false;  // Signifies that the currently selected activity has no children activities (thus no time sheets). 
        boolean childlessAct_sess = false;      // Signifies that the sess_activity_id has no children activities (thus no time sheets).
        boolean legendPrinted_scheduledEvents = false;    // Signifies that the legend item for "Your Scheduled Events" has been printed
        boolean legendPrinted_joinableEvents = false;     // Signifies that the legend item for "Events You May Join" has been printed
        boolean legendPrinted_otherEvents = false;        // Signifies that the legend item for "Other Events" has been printed
        boolean legendPrinted_scheduledLessons = false;   // Signifies that the legend item for "Your Scheduled Lessons" has been printed

        Gson gson_obj = new Gson(); // Create Json response for later use
        Map<String, Object> hashMap = new LinkedHashMap<String, Object>(); // Create hashmap response for later use

        //
        // See what activity mode we are in
        //
        int sess_activity_id = 0;

        try {
            sess_activity_id = (Integer) session.getAttribute("activity_id");
        } catch (Exception ignore) {
        }

        int organization_id = 0;

        try {
            organization_id = (Integer) session.getAttribute("organization_id");
        } catch (Exception ignore) {
        }

        String crumb = "Calendar";

        if (sess_activity_id == dining_activity_id) {
            crumb = "Dining Calendar";
        }
        
        String dining_base = "";
        String golf_base = "";
        String flxrez_base = "";
        
        dining_base = Utilities.getBaseUrl(req, dining_activity_id, club);
        golf_base = Utilities.getBaseUrl(req, 0, club);
        flxrez_base = Utilities.getBaseUrl(req, sess_activity_id, club);

        // Setup the daysArray
        DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
        daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, user, con);

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(sess_activity_id, con);

        //boolean events = false;
        boolean didone = false;
        boolean restricted = false;
        boolean allAct = false;
        boolean restrictAllTees = false;         // restrict all tee times
        boolean check5somes = false;
        boolean isOlyClubWGN = false;
        
        boolean rwd = reqUtil.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        
        htmlTags tags = new htmlTags(rwd);
        
        long now = timeUtil.getCurrentUnixTime();
        
        // setup our custom sytem text veriables
        SystemLingo sysLingo = new SystemLingo();
   
        sysLingo.setLingo("Lesson Book", club, sess_activity_id);
        
        //
        //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
        //
        //  NOTE:  see same flag in Member_sheet and Member_teelist_list and Member_teelist_mobile !!!!!!!!!!!!!!!!!!
        //
        boolean noAccessAfterLottery = false;

        if (club.equals("bonniebriar") || club.equals("braeburncc")) {   // add other clubs here!!

            noAccessAfterLottery = true;      // no member access after lottery processed
        }

        if (club.equals("tcclub") || club.equals("bearcreekgc")) {
            
            showGuestOnlyEvents = true;
        }

        if (club.equals("olyclub")) {
            isOlyClubWGN = verifyCustom.isOlyClubWGN(user, req);
        }

        String[] mm_table = {"inv", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

        String[] day_table = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        //
        //  Num of days in each month
        //
        int[] numDays_table = {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        //
        //  Num of days in Feb indexed by year starting with 2000 - 2040
        //
        int[] feb_table = {29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, +28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29};

        //
        //  Arrays to hold the event indicators - one entry per day (is there one of the events on this day)
        //
        int[] eventA = new int[32];          //  events (32 entries so we can index by day #)
        int[] teetimeA = new int[32];        //  tee times
        int[] lessonA = new int[32];         //  lesson times
        int[] lessongrpA = new int[32];      //  lesson group times
        int[] lotteryA = new int[32];        //  lotteries
        int[] waitlistA = new int[32];       //  wait list signups
        int[] activitiesA = new int[32];     //  activity signups
        int[] diningA = new int[32];         //  dining events
        int[] diningR = new int[32];         //  dining reservations
        List<List<List<Object>>> lessongrpseriesA = new ArrayList<List<List<Object>>>(32);   //  lesson group clinic series

        if (organization_id != 0) {

            person_id = Utilities.getPersonId(user, con);

        }
        
        for (int j = 0; j < 32; j++) {
            lessongrpseriesA.add(j, new ArrayList<List<Object>>());
        }

        // 
        //  Get activity id if user changed his selection
        //
        if (req.getParameter("activity_id") != null) {

            String tempId = req.getParameter("activity_id");

            try {
                selected_act_id = Integer.parseInt(tempId);
            } catch (NumberFormatException e) {
                selected_act_id = sess_activity_id;      // default to current activity
            }

        } else {

            if (club.equals("mirabel") || club.equals("denvercc") || club.equals("stcloudcc") 
                    || sess_activity_id >= 8000 && sess_activity_id <= 8003) {
                selected_act_id = all_activities;
            } else {
                selected_act_id = sess_activity_id;      // default to current activity
            }
        }
        
        if (sess_activity_id != 0 && sess_activity_id != dining_activity_id) {
            
            flxRezAct_sess = true;
            
            if (getActivity.isChildlessActivity(sess_activity_id, con)) {
                childlessAct_sess = true;
            }
        }

        // Set a boolean for easy reference later
        if (selected_act_id == all_activities) {
            allAct = true;
        } else {
            allAct = false;
        }

        if (selected_act_id != 0 && selected_act_id != dining_activity_id && selected_act_id != all_activities) {

            flxRezAct_selected = true;
            selected_act_name = getActivity.getActivityName(selected_act_id, con);       // Get name for this activity
            selected_act_children = getActivity.buildInString(selected_act_id, 1, con);  // Get csv string of all children of this activity
            
            if (getActivity.isChildlessActivity(selected_act_id, con)) {
                childlessAct_selected = true;
            } else {
                childlessAct_selected = false;
            }
        }
        
        if (selected_act_id != all_activities) {
            showLessons = Utilities.isLessonBookConfigured(selected_act_id, con);
        }

        try {

            //
            // Get the days in advance and time for advance from the club db
            //
            getClub.getParms(con, parm, sess_activity_id);        // get the club parms

            multi = parm.multi;
            lottery = parm.lottery;
            zone = parm.adv_zone;

            //
            //  use the member's mship type to determine which 'days in advance' parms to use
            //
            verifySlot.getDaysInAdv(con, parm, mship, sess_activity_id);        // get the days in adv data for this member

            max = parm.memviewdays + 1;        // days this member can view tee sheets

        } catch (Exception exc) {
        }


        //
        //  get today's date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);     // get current time
        int cal_min = cal.get(Calendar.MINUTE);

        //
        //    Adjust the time based on the club's time zone (we are Central)
        //
        ctime = (cal_hourDay * 100) + cal_min;        // get time in hhmm format

        ctime = SystemUtils.adjustTime(con, ctime);   // adjust the time

        if (ctime < 0) {                // if negative, then we went back or ahead one day

            ctime = 0 - ctime;           // convert back to positive value

            if (ctime < 1200) {           // if AM, then we rolled ahead 1 day

                //
                // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                //
                cal.add(Calendar.DATE, 1);                     // get next day's date

            } else {                        // we rolled back 1 day

                //
                // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                //
                cal.add(Calendar.DATE, -1);                     // get yesterday's date
            }
        }

        cal_hourDay = ctime / 100;                      // get adjusted hour
        cal_min = ctime - (cal_hourDay * 100);          // get minute value

        yy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH) + 1;
        dd = cal.get(Calendar.DAY_OF_MONTH);
        day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

        todayDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

        year = yy;
        month = mm;
        day = dd;

        today = day;                                  // save today's number


        //
        //  Get tomorrow's date for cutoff test
        //
        cal.add(Calendar.DATE, 1);                     // get next day's date
        yy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH) + 1;
        dd = cal.get(Calendar.DAY_OF_MONTH);

        tomorrowDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

        yy = 0;       // reset
        mm = 0;
        dd = 0;


        //
        //  Check if its earlier than the time specified for days in advance
        //
        if (parm.advtime1 > ctime) {

            //
            //  If this club set the max days to view equal to the days in advance, and all days in advance are the same, and
            //  all times of the days are the same, then adjust the days to view.  This is the only way we can do this!!
            //
            if (parm.memviewdays > 0 && parm.memviewdays == parm.advdays1 && parm.memviewdays == parm.advdays2 && parm.memviewdays == parm.advdays3
                    && parm.memviewdays == parm.advdays4 && parm.memviewdays == parm.advdays5 && parm.memviewdays == parm.advdays6 && parm.memviewdays == parm.advdays7
                    && parm.advtime1 == parm.advtime2 && parm.advtime1 == parm.advtime3 && parm.advtime1 == parm.advtime4 && parm.advtime1 == parm.advtime5
                    && parm.advtime1 == parm.advtime6 && parm.advtime1 == parm.advtime7) {

                max--;
            }
        }


        //
        //  Custom 1595 for Timarron - days to view must match the days in advance so members cannot view any sooner than they can book.
        //
        if (club.equals("timarroncc") && sess_activity_id == 0) {   // per Pro's request

            max = 5;                               // normally 4 days in advance (this method requires max+1 value)

            if (day_num == 2 || ctime < 700) {     // if Monday (any time), then do not allow access to Friday, OR if before 7:00 AM

                max = 4;
            }
        }


        //
        //  Wollaston GC - if today is Monday, then do not allow access to any tee times, no matter what day the tee sheet is for (case 1819). 
        //
   /*
        if (club.equals( "wollastongc" )) {
        
        restrictAllTees = verifyCustom.checkWollastonMon();         // restrict all day if today is Monday
        }
         */

        //
        //   build the HTML page for the display
        //
        Common_skin.outputHeader(club, sess_activity_id, "My Reservation Calendar", false, out, req);
        //
        //*******************************************************************
        //  Scripts to complete and submit the forms
        //*******************************************************************
        //

        // New skin still using old-skin method for lessons
        out.println("<script type=\"text/javascript\">");                     // Lesson Times
        out.println("<!--");
        out.println("function exeLtimeForm(proid, calDate, date, time, day, ltype, lesson_id, activity_id) {");
        out.println("document.forms['LtimeForm'].proid.value = proid;");
        out.println("document.forms['LtimeForm'].calDate.value = calDate;");
        out.println("document.forms['LtimeForm'].date.value = date;");
        out.println("document.forms['LtimeForm'].time.value = time;");
        out.println("document.forms['LtimeForm'].day.value = day;");
        out.println("document.forms['LtimeForm'].ltype.value = ltype;");
        out.println("document.forms['LtimeForm'].lesson_id.value = lesson_id;");
        out.println("document.forms['LtimeForm'].activity_id.value = activity_id;");
        out.println("document.forms['LtimeForm'].submit();");        // submit the form
        out.println("}");                  // end of script function
        out.println("// -->");
        out.println("</script>");          // End of script

        out.println("<script type=\"text/javascript\">");                     // Lesson Groups
        out.println("<!--");
        out.println("function exeLgroupForm(proid, date, lgname, lesson_id, activity_id) {");
        out.println("document.forms['LgroupForm'].proid.value = proid;");
        out.println("document.forms['LgroupForm'].date.value = date;");
        out.println("document.forms['LgroupForm'].lgname.value = lgname;");
        out.println("document.forms['LgroupForm'].lesson_id.value = lesson_id;");
        out.println("document.forms['LgroupForm'].activity_id.value = activity_id;");
        out.println("document.forms['LgroupForm'].submit();");        // submit the form
        out.println("}");                  // end of script function
        out.println("// -->");
        out.println("</script>");          // End of script

        String title = "";
        String activity_text = "Activities";

        if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == all_activities)) {  // if Golf in system and Golf or ALL selected

            title = "Your Current " + ((IS_TLT) ? "Notifications" : "Tee Times") + " and Other Activities";

        } else if (selected_act_id == all_activities) {       // was ALL Activities selected

            title = "Your Current Scheduled Activities";

        } else {

            title = "Your Current " + selected_act_name + " Activities";
            activity_text = selected_act_name + " Reservations";
        }
        
        out.println("</head>\n");
        Common_skin.outputBody(club, sess_activity_id, out, req);
        Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
        Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
        Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
        Common_skin.outputPageStart(club, sess_activity_id, out, req);
        Common_skin.outputBreadCrumb(club, sess_activity_id, out, crumb, req);
        Common_skin.outputLogo(club, sess_activity_id, out, req);
        //out.println("  <div id=\"breadcrumb\"><a href=\"Member_announce\">Home</a> / Calendar</div>");
        //out.println("  <div id=\"main_ftlogo\"><img src=\"/v5/assets/images/foretees_logo.png\"></div>");

        //if (sess_activity_id == dining_activity_id) out.println("<BR><BR>");  // move down to start the H3
        // PLEASE DO NOT USE BR's to fix layout issues.  CSS, and/or proper changes to the markup, can handle this
        // without creating future layout issues

        out.println("<h3>" + title + "</h3>");
        out.println("<div class=\"main_instructions pageHelp\" data-fthelptitle=\"Legend\">");

        out.println("<fieldset class=\"standard_legend_list\">"); // legend
        out.println("<legend>Legend</legend><div>");

        if (club.equals("interlachenspa")) {

            out.print("<div class=\"tee_list_color_5\"><p>Your Scheduled Spa Services</p></div>");
            out.print("<div class=\"tee_list_color_3\"><p>Other Events</p></div>");

        } else {

            if (selected_act_id == 0 || selected_act_id == all_activities) {

                if (!legendPrinted_scheduledEvents) {
                    legendPrinted_scheduledEvents = true;
                    out.print("<div class=\"tee_list_color_1\"><p>Your Scheduled Events</p></div>");
                }
                if (!legendPrinted_joinableEvents) {
                    legendPrinted_joinableEvents = true;
                    out.print("<div class=\"tee_list_color_2\"><p>Events You May Join</p></div>");
                }
                if (!legendPrinted_joinableEvents) {
                    legendPrinted_joinableEvents = true;
                    out.print("<div class=\"tee_list_color_3\"><p>Other Events</p></div>");
                }

                if (parm.foretees_mode == 1) {

                    out.print("<div class=\"tee_list_color_4\"><p>Your " + ((IS_TLT) ? "Submitted Notifications" : "Scheduled Tee Times") + "</p></div>");
                }

                if (!legendPrinted_scheduledLessons) {
                    legendPrinted_scheduledLessons = true;
                    out.print("<div class=\"tee_list_color_5\"><p>Your Scheduled " + sysLingo.TEXT_Lesson_Reservations + "</p></div>");
                }

                if (lottery > 0 && parm.foretees_mode == 1) {

                    //
                    //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
                    //
                    lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  

                    if (club.equals("oldoaks")) {
                        out.print("<div class=\"tee_list_color_6\"><p>Your Tee Time Requests</p></div>");
                    } else if (!lotteryText.equals("")) {
                        out.print("<div class=\"tee_list_color_6\"><p>Your " + lotteryText + "s</p></div>");
                    } else {
                        out.print("<div class=\"tee_list_color_6\"><p>Your Lottery Requests</p></div>");
                    }
                }

                out.print("<div class=\"tee_list_color_7\"><p>Your Wait List Signups</div>");
            }
            if (parm.genrez_mode != 0 && (flxRezAct_selected || selected_act_id == all_activities)) {

                if (!childlessAct_selected) {
                    out.print("<div class=\"tee_list_color_6\"><p>Your Scheduled " + activity_text + "</p></div>");
                }

                if (!club.equals("ballantyne") || sess_activity_id != 1) {

                    if (!legendPrinted_scheduledEvents) {
                        legendPrinted_scheduledEvents = true;
                        out.print("<div class=\"tee_list_color_1\"><p>Your Scheduled Events</p></div>");
                    }
                    if (!legendPrinted_joinableEvents) {
                        legendPrinted_joinableEvents = true;
                        out.print("<div class=\"tee_list_color_2\"><p>Events You May Join</p></div>");
                    }
                    if (!legendPrinted_otherEvents) {
                        legendPrinted_otherEvents = true;
                        out.print("<div class=\"tee_list_color_3\"><p>Other Events</p></div>");
                    }
                }

                if (!legendPrinted_scheduledLessons) {
                    legendPrinted_scheduledLessons = true;
                    out.print("<div class=\"tee_list_color_5\"><p>Your Scheduled " + sysLingo.TEXT_Lesson_Reservations + "</p></div>");
                }

            }

            if (organization_id != 0 && (selected_act_id == dining_activity_id || selected_act_id == all_activities)) {

                out.print("<div class=\"tee_list_color_9\"><p>Your Dining Reservations</p></div>");
                out.print("<div class=\"tee_list_color_10\"><p>Dining Events You May Join</p></div>");

            }

        }
        out.println("</div></fieldset>");

        out.println("</div>");

        StringBuilder activitySelect = new StringBuilder();
        List<String> asClasses = new ArrayList<String>();
        asClasses.add("sub_instructions");
        asClasses.add("activitySelect");
//        if (club.equals("estanciaclub")) {
//
//            int advTimeCount = 0;
//
//            advTimeCount = verifyCustom.checkEstanciaAdvTimes(user, con);
//
//            activitySelect.append("    <p><b>You currently have " + ((4 - advTimeCount > 0) ? (4 - advTimeCount) : 0) + " out of 4 Advance Guest Times remaining.</b></p><br>");
//            asClasses.add("hasMessage");
//        }
        
        if (club.equals("kiawahislandclub") && verifyCustom.checkKiawahAdvanceMship(mship)) {

            int advTimeCount1 = 0;
            int advTimeCount2 = 0;
            int quarter1 = 0;
            int quarter2 = 0;
            int year1 = year;
            int year2 = year;
            int year_short1 = year - 2000;
            int year_short2 = year - 2000;
            int advTimeLimit = 2;
        
            String quarter1_str = "";
            String quarter2_str = "";

            if (month <= 3) {
                quarter1 = 1;
                quarter2 = 2;
                quarter1_str = "1/1/" + year_short1 + " - 3/31/" + year_short1;
                quarter2_str = "4/1/" + year_short1 + " - 6/30/" + year_short1;
            } else if (month <= 6) {
                quarter1 = 2;
                quarter2 = 3;
                quarter1_str = "4/1/" + year_short1 + " - 6/30/" + year_short1;
                quarter2_str = "7/1/" + year_short1 + " - 9/30/" + year_short1;
            } else if (month <= 9) {
                quarter1 = 3;
                quarter2 = 4;
                quarter1_str = "7/1/" + year_short1 + " - 9/30/" + year_short1;
                quarter2_str = "10/1/" + year_short1 + " - 12/31/" + year_short1;
            } else {
                quarter1 = 4;
                quarter2 = 1;
                year2++;
                year_short2++;
                quarter1_str = "10/1/" + year_short1 + " - 12/31/" + year_short1;
                quarter2_str = "1/1/" + year_short2 + " - 3/31/" + year_short2;
            }

            advTimeCount1 = verifyCustom.checkKiawahAdvanceTimes(user, mship, year1, quarter1, con);
            advTimeCount2 = verifyCustom.checkKiawahAdvanceTimes(user, mship, year2, quarter2, con);

            int advTimeDisp1 = advTimeLimit - advTimeCount1;
            int advTimeDisp2 = advTimeLimit - advTimeCount2;

            if (advTimeDisp1 < 0) {
                advTimeDisp1 = 0;
            } else if (advTimeDisp1 > advTimeLimit) {
                advTimeDisp1 = advTimeLimit;
            }

            if (advTimeDisp2 < 0) {
                advTimeDisp2 = 0;
            } else if (advTimeDisp2 > advTimeLimit) {
                advTimeDisp2 = advTimeLimit;
            }

            out.println("<p align=\"center\"><b>You currently have " + advTimeDisp1 + " out of 2 Advance Times remaining for the " + quarter1_str + " quarter.</b></p>");
            out.println("<p align=\"center\"><b>You currently have " + advTimeDisp2 + " out of 2 Advance Times remaining for the " + quarter2_str + " quarter.</b></p>");
            out.println("<br><br>");
        }

        if (!caller.equals("FLEXWEBFT") && ((parm.genrez_mode != 0 && (!getActivity.isStagingMode(con) || sess_activity_id != 0)) || organization_id != 0)) {       // if any activities defined in system - add a drop down selection list for activities

            activitySelect.append("    <form action=\"Member_teelist\" method=\"get\" name=\"cform\">");
            activitySelect.append("    <p>");

            activitySelect.append("      <b>Select the Activity to Display:</b>&nbsp;&nbsp;");
            getActivity.buildActivitySelect(1, selected_act_id, "onChange=\"document.cform.submit()\"", true, con, activitySelect);
            activitySelect.append("    </p><br></form>");
            asClasses.add("hasActivities");
        }

        activitySelect.append("    <a href=\"#\" class=\"standard_button print_button\"><b>Print this page</b></a>");

        out.print("<div class=\"" + Utilities.implode(asClasses, " ") + "\">");
        out.print(activitySelect);
        out.print("</div>");


        //
        //  Display 3 tables - 1 for each of the next 3 months
        //
        months = 3;                                      // default = 3 months

        if ((club.equals("deserthighlands") && sess_activity_id == 0) || club.equals("mirabel") || club.equals("stcloudcc") || 
             club.equals("esterocc") || club.equals("edina") || club.equals("superstitionmountain") || club.equals("silvercreekcountryclub")) {

            months = 12;                               // they want 12 months
        }

        if ((club.equals("rtjgc") || club.equals("pattersonclub") || club.equals("blackrockcountryclub")) && sess_activity_id == 0) {     // Robert Trent Jones GC

            months = 6;                               // they want 6 months
        }
        
        if (club.equals("blackhawk") && selected_act_id == 1) {
            months = 2;
        }


        for (i2 = 0; i2 < months; i2++) {                 // do each month

            monthName = mm_table[month];                  // month name

            numDays = numDays_table[month];               // number of days in month

            if (numDays == 0) {                           // if Feb

                int leapYear = year - 2000;
                numDays = feb_table[leapYear];             // get days in Feb
            }

            //
            //  Adjust values to start at the beginning of the month
            //
            cal.set(Calendar.YEAR, year);                 // set year in case it changed below
            cal.set(Calendar.MONTH, month - 1);             // set the current month value
            cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
            day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
            day = 1;
            col = 0;

            //
            //  init the indicator arrays to start new month
            //
            for (i = 0; i < 32; i++) {
                eventA[i] = 0;
                teetimeA[i] = 0;
                lessonA[i] = 0;
                lotteryA[i] = 0;
                waitlistA[i] = 0;
                activitiesA[i] = 0;
                diningA[i] = 0;
                diningR[i] = 0;
                lessongrpA[i] = 0;
                lessongrpseriesA.get(i).clear();
            }

            //
            //  Locate all the Tee Times for this member & month and set the array indicators for each day
            //
            sdate = (year * 10000) + (month * 100) + 0;       // start of the month (for searches)
            edate = (year * 10000) + (month * 100) + 32;      // end of the month

            if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == all_activities)) {

                try {

                    //
                    // search for this user's tee times for this month
                    //
                    pstmt1 = con.prepareStatement(
                            "SELECT dd "
                            + "FROM teecurr2 "
                            + "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                            + "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) "
                            + "AND (date > ? AND date < ?) AND lottery_email = 0 "
                            + "ORDER BY date");

                    pstmt1.clearParameters();        // clear the parms
                    pstmt1.setString(1, user);
                    pstmt1.setString(2, user);
                    pstmt1.setString(3, user);
                    pstmt1.setString(4, user);
                    pstmt1.setString(5, user);
                    pstmt1.setString(6, user);
                    pstmt1.setString(7, user);
                    pstmt1.setString(8, user);
                    pstmt1.setString(9, user);
                    pstmt1.setString(10, user);
                    pstmt1.setLong(11, sdate);
                    pstmt1.setLong(12, edate);
                    rs = pstmt1.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        dd = rs.getInt(1);
                        teetimeA[dd] = 1;       // set indicator for this day (tee time exists)

                    }

                } catch (Exception e1) {

                    Utilities.logError("Member_teelist: Error getting tee times for " + club + ", User: " + user + ", Error: " + e1.getMessage());

                } finally {

                    try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }

                    try {
                        pstmt1.close();
                    } catch (SQLException ignored) {
                    }

                }

                //
                //  Check for any lottery requests, if supported
                //
                if (lottery > 0) {

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT dd "
                                + "FROM lreqs3 "
                                + "WHERE (user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR "
                                + "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR "
                                + "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR "
                                + "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR "
                                + "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? OR "
                                + "orig_by LIKE ?) "
                                + "AND (date > ? AND date < ?) "
                                + "ORDER BY date");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, user);
                        pstmt1.setString(2, user);
                        pstmt1.setString(3, user);
                        pstmt1.setString(4, user);
                        pstmt1.setString(5, user);
                        pstmt1.setString(6, user);
                        pstmt1.setString(7, user);
                        pstmt1.setString(8, user);
                        pstmt1.setString(9, user);
                        pstmt1.setString(10, user);
                        pstmt1.setString(11, user);
                        pstmt1.setString(12, user);
                        pstmt1.setString(13, user);
                        pstmt1.setString(14, user);
                        pstmt1.setString(15, user);
                        pstmt1.setString(16, user);
                        pstmt1.setString(17, user);
                        pstmt1.setString(18, user);
                        pstmt1.setString(19, user);
                        pstmt1.setString(20, user);
                        pstmt1.setString(21, user);
                        pstmt1.setString(22, user);
                        pstmt1.setString(23, user);
                        pstmt1.setString(24, user);
                        pstmt1.setString(25, user);
                        pstmt1.setString(26, user);
                        pstmt1.setLong(27, sdate);
                        pstmt1.setLong(28, edate);
                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            dd = rs.getInt(1);

                            lotteryA[dd] = 1;       // set indicator for this day (lottery req exists)
                        }

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting lottery reqs for " + club + ", User: " + user + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }

                    }
                }         // end of IF lottery
            }

            //
            //  Get all lesson times for this user this month 
            //
            try {

                pstmt1 = con.prepareStatement(
                        "SELECT date "
                        + "FROM lessonbook5 "
                        + "WHERE date > ? AND date < ? AND memid = ? "
                        + (!allAct ? "AND activity_id = ? " : "")
                        + "ORDER BY date");

                pstmt1.clearParameters();
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, edate);
                pstmt1.setString(3, user);
                if (!allAct) {
                    pstmt1.setInt(4, selected_act_id);
                }
                rs = pstmt1.executeQuery();

                while (rs.next()) {

                    ldate = rs.getLong(1);

                    ldate = ldate - ((ldate / 100) * 100);     // get day
                    dd = (int) ldate;

                    lessonA[dd] = 1;       // set indicator for this day (lesson time exists)

                }
                pstmt1.close();

                // Get all lesson groups (non-clinic only for FlzRez)
                pstmt1 = con.prepareStatement(
                        "SELECT lgs.date "
                        + "FROM lgrpsignup5 lgs "
                        + "LEFT OUTER JOIN lessongrp5 lg ON lg.lesson_id = lgs.lesson_id "
                        + "WHERE lgs.date > ? AND lgs.date < ? AND memid = ? AND clinic = 0 "
                        + "ORDER BY lgs.date");

                pstmt1.clearParameters();
                pstmt1.setLong(1, sdate);
                pstmt1.setLong(2, edate);
                pstmt1.setString(3, user);
                rs = pstmt1.executeQuery();

                while (rs.next()) {

                    ldate = rs.getLong(1);

                    ldate = ldate - ((ldate / 100) * 100);     // get day
                    dd = (int) ldate;

                    lessongrpA[dd] = 1;       // set indicator for this day (lesson time exists)
                }
                      
                
                // If viewing a FlxRez activity, look up clinic-series lesson groups differently to determine their active dates
                if (flxRezAct_selected || selected_act_id == all_activities) {
                         
                    pstmt1.close();

                    // Get all lesson groups (non-clinic only for FlzRez)
                    pstmt1 = con.prepareStatement(
                            "SELECT lgs.lname, lgs.proid "
                            + "FROM lgrpsignup5 lgs "
                            + "LEFT OUTER JOIN lessongrp5 lg ON lg.lesson_id = lgs.lesson_id "
                            + "WHERE lg.date < ? AND edate > ? AND memid = ? AND clinic = 1 "
                            + "ORDER BY lgs.date");

                    pstmt1.clearParameters();
                    pstmt1.setLong(1, edate);
                    pstmt1.setLong(2, sdate);
                    pstmt1.setString(3, user);
                    rs = pstmt1.executeQuery();

                    while (rs.next()) {
                      
                        try {
                            
                            pstmt2 = con.prepareStatement("SELECT DATE_FORMAT(date, '%e') FROM lessonbook5 WHERE lgname = ? AND proid = ? AND date > ? AND date < ? GROUP BY date ORDER BY date");
                            
                            pstmt2.clearParameters();
                            pstmt2.setString(1, rs.getString("lname"));
                            pstmt2.setInt(2, rs.getInt("proid"));
                            pstmt2.setLong(3, sdate);
                            pstmt2.setLong(4, edate);
                            
                            rs2 = pstmt2.executeQuery();
                            
                            while (rs2.next()) {
                             
                                dd = rs2.getInt(1);
                                
                                List<Object> grpLessonInfo = new ArrayList<Object>();
                                
                                grpLessonInfo.add(0, rs.getString("lname"));
                                grpLessonInfo.add(1, rs.getInt("proid"));
                                
                                lessongrpseriesA.get(dd).add(grpLessonInfo);    // Store the lesson group name and proid so we don't have to go through the logic to find it again when printing the calendar.
                                                           
                            }
                            
                        } catch (Exception exc) {
                            Utilities.logError("Member_teelist: Error getting lesson group (clinic series) times for " + club + ", User: " + user + ", Error: " + exc.toString());
                        } finally {
                            try { rs2.close(); }
                            catch (Exception ignore) {}
                            
                            try { pstmt2.close(); }
                            catch (Exception ignore) {}
                        }
                    }
                }

            } catch (Exception e1) {

                Utilities.logError("Member_teelist: Error getting lesson times for " + club + ", User: " + user + ", Error: " + e1.getMessage());                           // log it

            } finally {

                try {
                    rs.close();
                } catch (SQLException ignored) {
                }

                try {
                    pstmt1.close();
                } catch (SQLException ignored) {
                }

            }
            
            
            /*      
            //  Custom to check for dependents' group lessons
            if ((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent")) {     // Custom to check for Dependents registered
    
                //  Get each dependent and check if they are registered
                String mNumD = "";
                PreparedStatement pstmtDenver = null;
                PreparedStatement pstmtDenver2 = null;
                ResultSet rsDenver = null;
                ResultSet rsDenver2 = null;

                try {
                    
                    pstmtDenver = con.prepareStatement(
                            "SELECT memNum "
                            + "FROM member2b WHERE username = ?");      // get this member's mNum

                    pstmtDenver.clearParameters();       
                    pstmtDenver.setString(1, user);
                    rsDenver = pstmtDenver.executeQuery();    

                    if (rsDenver.next()) {

                        mNumD = rsDenver.getString("memNum");                                            
                    }
                    pstmtDenver.close();

                    if (!mNumD.equals("")) {        // if member number found for this member

                        //  Locate any Dependents for this member and search their event signups

                        pstmtDenver = con.prepareStatement(
                                "SELECT username "
                                + "FROM member2b WHERE memNum = ? AND m_type = 'Dependent'");

                        pstmtDenver.clearParameters();      
                        pstmtDenver.setString(1, mNumD);
                        rsDenver = pstmtDenver.executeQuery();    

                        loopDenver:
                        while (rsDenver.next()) {

                            String userD = rsDenver.getString("username");   // get the dependent's username and look for any event signups for this child

                            pstmtDenver2 = con.prepareStatement(
                                    "SELECT date "
                                    + "FROM lgrpsignup5 "
                                    + "WHERE date > ? AND date < ? AND memid = ? "
                                    + "ORDER BY date");

                            pstmtDenver2.clearParameters();
                            pstmtDenver2.setLong(1, sdate);
                            pstmtDenver2.setLong(2, edate);
                            pstmtDenver2.setString(3, userD);
                            rsDenver2 = pstmtDenver2.executeQuery();

                            while (rsDenver2.next()) {

                                ldate = rsDenver2.getLong(1);

                                ldate = ldate - ((ldate / 100) * 100);     // get day
                                dd = (int) ldate;

                                lessongrpA[dd] = 1;       // set indicator for this day (lesson time exists)
                            }
                        }
                    }   

                } catch (Exception e1) {

                    Utilities.logError("Member_teelist: Error getting Dependents group lessons for " + club + ", User: " + user + ", Error: " + e1.getMessage());                           // log it

                } finally {

                    try {
                        if (rsDenver != null) rsDenver.close();
                    } catch (SQLException ignored) {
                    }
                    try {
                        if (rsDenver2 != null) rsDenver2.close();
                    } catch (SQLException ignored) {
                    }

                    try {
                        if (pstmtDenver != null) pstmtDenver.close();
                    } catch (SQLException ignored) {
                    }
                    try {
                        if (pstmtDenver2 != null) pstmtDenver2.close();
                    } catch (SQLException ignored) {
                    }
                    
                }

            }       // end of IF denvercc
            * 
            */
            
            

            //
            //  Get all events for this month
            //
            try {

                if (/*parm.genrez_mode == 0 || */selected_act_id == all_activities) {     // if no Activities or ALL selected

                    pstmt1 = con.prepareStatement(
                            "SELECT day "
                            + "FROM events2b WHERE date > ? AND date < ? " + (showGuestOnlyEvents ? "" : "AND gstOnly = 0 ") + "AND signup_on_teesheet = 0 AND inactive = 0");

                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);

                } else {

                    pstmt1 = con.prepareStatement(
                            "SELECT day "
                            + "FROM events2b WHERE date > ? AND date < ? " + (showGuestOnlyEvents ? "" : "AND gstOnly = 0 ") + "AND signup_on_teesheet = 0 AND activity_id = ? AND inactive = 0");
                    

                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);
                    pstmt1.setInt(3, selected_act_id);
                }

                rs = pstmt1.executeQuery();

                while (rs.next()) {

                    dd = rs.getInt(1);

                    eventA[dd] = 1;       // set indicator for this day (event exists)
                }

            } catch (Exception e1) {

                Utilities.logError("Member_teelist: Error getting events for " + club + ", Error: " + e1.getMessage());

            } finally {

                try {
                    rs.close();
                } catch (SQLException ignored) {
                }

                try {
                    pstmt1.close();
                } catch (SQLException ignored) {
                }

            }


            //
            //  Get all dining events & ala carte reservations for this month
            //
            if (organization_id != 0) { // only query for them if dining is enabled

                Connection con_d = null;

                try {

                    con_d = Connect.getDiningCon();

                    if (con_d != null) {

                        // quick fix - set the correct max value for dining
                        max = parmDining.getMaxDays(organization_id, con_d);

                        // get all dining event reservations
                        pstmt1 = con_d.prepareStatement(""
                                + "SELECT EXTRACT(DAY FROM date) "
                                + "FROM events "
                                + "WHERE organization_id = ? AND cancelled = false AND "
                                + "to_char(date, 'YYYYMMDD')::int >= ? AND "
                                + "to_char(date, 'YYYYMMDD')::int <= ?");

                        pstmt1.setInt(1, organization_id);
                        pstmt1.setLong(2, sdate);
                        pstmt1.setLong(3, edate);

                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            dd = rs.getInt(1);
                            diningA[dd] = 1;       // set indicator for this day (dining event exists)

                        }

                        //  get all dining reservations for this month and member
                        pstmt1 = con_d.prepareStatement(""
                                + "SELECT EXTRACT(DAY FROM date) "
                                + "FROM reservations "
                                + "WHERE "
                                + "category = 'dining' AND state <> 'cancelled' AND "
                                + "organization_id = ? AND "
                                + "person_id = ? AND "
                                + "to_char(date, 'YYYYMMDD')::int >= ? AND "
                                + "to_char(date, 'YYYYMMDD')::int <= ?");

                        pstmt1.setInt(1, organization_id);
                        pstmt1.setInt(2, person_id);
                        pstmt1.setLong(3, sdate);
                        pstmt1.setLong(4, edate);

                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            dd = rs.getInt(1);
                            diningR[dd] = 1;       // set indicator for this day (dining reservation exists)

                        }

                    }

                } catch (Exception e1) {

                    Utilities.logError("Member_teelist: Error getting dinning events for " + club + ", Error: " + e1.getMessage());

                } finally {

                    try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }

                    try {
                        pstmt1.close();
                    } catch (SQLException ignored) {
                    }

                    try {
                        con_d.close();
                    } catch (SQLException ignored) {
                    }

                }

            } // end if dining club


            if (parm.foretees_mode == 1 && (selected_act_id == 0 || selected_act_id == all_activities)) {

                try {

                    //
                    // search for this user's unconverted wait list signups for this month
                    //
                    pstmt1 = con.prepareStatement(
                            "SELECT DATE_FORMAT(wls.date, '%d') AS dd, wls.wait_list_signup_id "
                            + "FROM wait_list_signups wls "
                            + "LEFT OUTER JOIN wait_list_signups_players wlp ON wlp.wait_list_signup_id = wls.wait_list_signup_id "
                            + "WHERE DATE_FORMAT(wls.date, '%Y%m%d') >= ? AND DATE_FORMAT(wls.date, '%Y%m%d') <= ? AND wlp.username = ? AND converted = 0 "
                            + "ORDER BY wls.date");

                    pstmt1.clearParameters();
                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);
                    pstmt1.setString(3, user);

                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        waitlistA[rs.getInt(1)] = rs.getInt(2);       // set indicator for this day (wait list signup exists)

                    }

                } catch (Exception e1) {

                    Utilities.logError("Member_teelist: Error getting wait list signups for " + club + ", User: " + user + ", Error: " + e1.getMessage());

                } finally {

                    try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }

                    try {
                        pstmt1.close();
                    } catch (SQLException ignored) {
                    }

                }
            }


            if (parm.genrez_mode != 0 && (flxRezAct_selected || allAct)) {      // if any activites defined for this club

                try {

                    //
                    // search for this user's activity signups for this month
                    //
                    pstmt1 = con.prepareStatement(
                            "SELECT DATE_FORMAT(a.date_time, '%d') AS dd, a.sheet_id "
                            + "FROM activity_sheets a "
                            + "LEFT OUTER JOIN activity_sheets_players ap ON ap.activity_sheet_id = a.sheet_id "
                            + "WHERE"
                            + " a.date_time BETWEEN ? AND ? "
                            //+ " DATE_FORMAT(a.date_time, '%Y%m%d') >= ? AND DATE_FORMAT(a.date_time, '%Y%m%d') <= ? "
                            + " AND (ap.username = ? OR a.orig_by = ?) " + (selected_act_id != all_activities ? "AND a.activity_id IN (" + selected_act_children + ") " : "")
                            + "GROUP BY dd "
                            + "ORDER BY a.date_time");

                    pstmt1.clearParameters();
                    pstmt1.setString(1, Utilities.get_mysql_timestamp((int)sdate, 0));
                    pstmt1.setString(2, Utilities.get_mysql_timestamp((int)edate, 2359));
                    //pstmt1.setLong(1, sdate);
                    //pstmt1.setLong(2, edate);
                    pstmt1.setString(3, user);
                    pstmt1.setString(4, user);

                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        activitiesA[rs.getInt(1)] = 1;       // set indicator for this day (activity signup exists)

                    }

                } catch (Exception e1) {

                    Utilities.logError("Member_teelist: Error getting activity signups for " + club + ", User: " + user + ", Error: " + e1.getMessage());

                } finally {

                    try {
                        rs.close();
                    } catch (SQLException ignored) {
                    }

                    try {
                        pstmt1.close();
                    } catch (SQLException ignored) {
                    }

                }
            }


            //
            //  BEGIN CALENDAR OUTPUT
            //
            out.println("<!-- max=" + max + " -->");

            out.println("<div class=\"list_calendar\"><table class=\"list_calendar\" data-enhance=\"false\">");
            out.println("<caption><b>" + monthName + "&nbsp;&nbsp;" + year + "</b></caption>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th><b><span>Sunday</span><span>Sun</span></b></th>");
            out.println("<th><b><span>Monday</span><span>Mon</span></b></th>");
            out.println("<th><b><span>Tuesday</span><span>Tue</span></b></th>");
            out.println("<th><b><span>Wednesday</span><span>Wed</span></b></th>");
            out.println("<th><b><span>Thursday</span><span>Thu</span></b></th>");
            out.println("<th><b><span>Friday</span><span>Fri</span></b></th>");
            out.println("<th><b><span>Saturday</span><span>Sat</span></b></th>");
            out.println("</tr></thead>");
            out.println("<tbody>");        // first row of days

            col = 0;

            for (i = 1; i < day_num; i++) {    // skip to the first day
                if (col == 0) {
                    out.print("<tr>");
                }
                out.println("<td class=\"empty\">&nbsp;</td>");
                col++;
                if (col == 0) {
                    col = 0;
                    out.print("</tr>");
                }
            }

            while (day < today) {
                if (col == 0) {
                    out.print("<tr>");
                }
                out.println("<td class=\"old\"><div class=\"day_wrapper_empty\"><div class=\"day_container\"><div class=\"day\">" + day + "</div></div></div></td>");  // put in day of month
                col++;
                day++;

                if (col == 7) {
                    col = 0;                             // start new week
                    out.println("</tr>");
                }
            }

            //
            // start with today, or 1st day of month, and go to end of month
            //
            while (day <= numDays) {

                //
                //  create a date field for queries
                //
                date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd
                String mysql_date = year + "-" + Utilities.ensureDoubleDigit(month) + "-" + Utilities.ensureDoubleDigit(day);
                didone = false;        // init 'did one' flag

                if (col == 0) {      // if new row
                    out.print("<tr>");
                }

                //
                // day of month, only set today format once. 
                //
                if ((day == today) && todayFlag) {

                    out.print("<td class=\"today\">");
                    todayFlag = false;
                } else {
                    out.print("<td>");
                }
                out.print("<div class=\"day_wrapper\"><div class=\"day_container\">");

                if (max > 0 && (sess_activity_id == 0 || sess_activity_id == dining_activity_id || !getActivity.isChildlessActivity(sess_activity_id, con))) {            // if member can view tee sheet for this date

                    //
                    //  Add link to tee sheets, time sheets, or dining slot page (do not specify a course, it will default to 1st one)
                    //
                    if (sess_activity_id == 0) {          // if Golf
                        out.print("<a class=\"day\" href=\""+golf_base+"Member" + ((IS_TLT) ? "TLT" : "") + "_sheet?index=" + index + "\" title=\"Select a Tee Time for this day\">");
                    } else if (sess_activity_id == dining_activity_id) {
                        out.println("<a class=\"day ftCsLink\" href=\"#\" data-fthref=\""+dining_base+"Dining_slot?action=new&amp;date=" + date + "\" title=\"Create a reservation for this day\">");
                    } else {
                        out.print("<a class=\"day\" href=\""+flxrez_base+"Member_gensheets?date=" + date + "\">");
                    }
                    out.println(day + "</a>");

                    index++;              // next day
                    max--;

                } else {

                    out.println("<div class=\"day\">" + day + "</div>");         // just put in day of month
                }


                if (IS_TLT) {


                    // check to see if there are any notifications for this day

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT n.notification_id, DATE_FORMAT(n.req_datetime, '%l:%i %p') AS pretty_time, req_datetime "
                                + "FROM notifications n, notifications_players np "
                                + "WHERE n.notification_id = np.notification_id "
                                + "AND np.username = ? "
                                + "AND DATE(n.req_datetime) = ? ");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, user);
                        pstmt1.setString(2, mysql_date);
                        rs = pstmt1.executeQuery();

                        while (rs.next()) {
                            
                            long reqUnixTime = timeUtil.getClubUnixTimeFromDb(req, rs.getString("req_datetime"));

                            Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                            data_map.put("url", Utilities.getBaseUrl(req, 0, club)+"MemberTLT_slot");
                            Map<String, Object> form_data_map = new LinkedHashMap<String, Object>();
                            form_data_map.put("stime", rs.getString("pretty_time"));
                            form_data_map.put("notifyId", rs.getInt("notification_id"));
                            form_data_map.put("date", date);
                            form_data_map.put("day", day);
                            form_data_map.put("index", 999);
                            data_map.put("data", form_data_map);
                            out.print("<div class=\"item_container\">");
                            if (reqUnixTime > now) {
                                out.println(
                                        "<a class=\"tee_list_tlt_time post_button\" href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(data_map)) + "\">"
                                        + buildItemDescription("Golf", rs.getString("pretty_time"), rwd) + "</a>");
                            } else {
                                out.println(
                                        "<div class=\"tee_list_tlt_time\">" + buildItemDescription("Golf", rs.getString("pretty_time"), rwd) + "</div>");
                            }
                            out.print("</div>");
                        }

                    } catch (Exception exp) {
                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }

                    }

                } else {

                    //*******************************************************************************
                    //  Check for any tee times for this day
                    //*******************************************************************************
                    //
                    if (teetimeA[day] == 1) {        // if any tee times exist for this day

                        try {

                            pstmt1 = con.prepareStatement(
                                    "SELECT teecurr_id, mm, dd, yy, day, hr, min, time, event, event_type, fb, "
                                    + "lottery, courseName, rest5, lottery_color, custom_int "
                                    + "FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                    + "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ?) "
                                    + "AND date = ? ORDER BY time");

                            pstmt1.clearParameters();        // clear the parms
                            pstmt1.setString(1, user);
                            pstmt1.setString(2, user);
                            pstmt1.setString(3, user);
                            pstmt1.setString(4, user);
                            pstmt1.setString(5, user);
                            pstmt1.setString(6, user);
                            pstmt1.setString(7, user);
                            pstmt1.setString(8, user);
                            pstmt1.setString(9, user);
                            pstmt1.setString(10, user);
                            pstmt1.setString(11, user);
                            pstmt1.setLong(12, date);
                            rs = pstmt1.executeQuery();      // execute the prepared stmt

                            while (rs.next()) {

                                mm = rs.getInt("mm");
                                dd = rs.getInt("dd");
                                yy = rs.getInt("yy");
                                dayname = rs.getString("day");
                                hr = rs.getInt("hr");
                                min = rs.getInt("min");
                                time = rs.getInt("time");
                                ename = rs.getString("event");
                                etype = rs.getInt("event_type");
                                fb = rs.getInt("fb");
                                lotteryName = rs.getString("lottery");
                                course = rs.getString("courseName");
                                rest5 = rs.getString("rest5");
                                lottery_color = rs.getString("lottery_color");
                                custom_int = rs.getInt("custom_int");

                                //
                                //  Check if a member restriction has been set up to block ALL mem types or mship types for this date & time
                                //
                                restricted = Utilities.checkRests(date, time, fb, course, dayname, mship, mtype, club, con);

                                ampm = " AM";
                                if (hr == 12) {
                                    ampm = " PM";
                                }
                                if (hr > 12) {
                                    ampm = " PM";
                                    hr = hr - 12;    // convert to conventional time
                                }

                                stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                                stime = stime2 + ampm;        // create a value for time parm

                                p5 = "No";                   // default = no 5-somes
                                fives = 0;
                                check5somes = true;

                                long month_day = (mm * 100) + dd;     // get adjusted date

                                // 
                                //  Customs 5-some restrictions for members (do not allow access to 5th player)
                                //
                                if (club.equals("rtjgc") || club.equals("coloradogc") || club.equals("rehobothbeachcc") || club.equals("ccyork") || club.equals("chattanoogagcc") 
                                        || club.equals("lagrangecc") || club.equals("lincolnshirefieldscc")) {

                                    check5somes = false;

//                                } else if (club.equals("foresthighlands") && (month_day > 424 && month_day < 1001)) {
//
//                                    check5somes = false;

                                } else if (club.equals("columbine") && !course.equalsIgnoreCase("Par 3") && (month_day >= 415 && month_day <= 930)) {

                                    check5somes = false;

                                } else if (club.equals("estanciaclub") && (month_day <= 515 || month_day >= 1015)) {

                                    check5somes = false;

                                } else if (club.equals("lakes") && (mm < 6 || mm > 10)) {

                                    check5somes = false;
                                    
                                } else if (club.equals("dmgcc") && (month_day >= 401 && date <= Hdate3)) {
                                    
                                    check5somes = false;
                                }


                                //
                                //  check if 5-somes allowed on this course
                                //
                                if (check5somes == true) {

                                    PreparedStatement pstmt3 = null;

                                    try {

                                        pstmt3 = con.prepareStatement(
                                                "SELECT fives FROM clubparm2 WHERE courseName = ?");

                                        pstmt3.clearParameters();        // clear the parms
                                        pstmt3.setString(1, course);
                                        rs2 = pstmt3.executeQuery();      // execute the prepared pstmt3

                                        if (rs2.next()) {

                                            fives = rs2.getInt(1);

                                            if ((fives != 0) && (rest5.equals(""))) {   // if 5-somes and not restricted

                                                p5 = "Yes";
                                            }
                                        }

                                    } catch (Exception e) {
                                    } finally {

                                        try {
                                            rs2.close();
                                        } catch (SQLException ignored) {
                                        }

                                        try {
                                            pstmt3.close();
                                        } catch (SQLException ignored) {
                                        }

                                    }
                                }

                                //
                                // Check for a shotgun event during this time
                                //
                                if (!ename.equals("") && etype == 1) {  // tee time during a shotgun event

                                    PreparedStatement pstmtev = null;

                                    try {

                                        //
                                        //   Get the parms for this event
                                        //
                                        pstmtev = con.prepareStatement(
                                                "SELECT act_hr, act_min FROM events2b "
                                                + "WHERE name = ?");

                                        pstmtev.clearParameters();        // clear the parms
                                        pstmtev.setString(1, ename);
                                        rsev = pstmtev.executeQuery();      // execute the prepared stmt

                                        if (rsev.next()) {

                                            hr = rsev.getInt("act_hr");
                                            min = rsev.getInt("act_min");
                                        }

                                    } catch (Exception e) {
                                    } finally {

                                        try {
                                            rsev.close();
                                        } catch (SQLException ignored) {
                                        }

                                        try {
                                            pstmtev.close();
                                        } catch (SQLException ignored) {
                                        }

                                    }

                                    //
                                    //  Create time value for email msg
                                    //
                                    ampm = " AM";

                                    if (hr == 0) {

                                        hr = 12;                 // change to 12 AM (midnight)

                                    } else {

                                        if (hr == 12) {

                                            ampm = " PM";         // change to Noon
                                        }
                                    }
                                    if (hr > 12) {

                                        hr = hr - 12;
                                        ampm = " PM";             // change to 12 hr clock
                                    }

                                    //
                                    //  convert time to hour and minutes for email msg
                                    //
                                    stime2 = hr + ":" + Utilities.ensureDoubleDigit(min) + ampm;

                                    out.print("<div class=\"item_container\">");
                                    out.println("<div class=\"shotgun\">" + buildItemDescription("Shotgun Start", stime2, rwd) + "</div>");
                                    out.print("</div>");

                                } else {

                                    boolean cutoff = false;

                                    //
                                    //  Check for Member Cutoff specified in Club Options
                                    //
                                    if (parm.cutoffdays < 99) {        // if option specified

                                        if (parm.cutoffdays == 0 && date == todayDate && ctime > parm.cutofftime) {  // if cutoff day of and we are doing today and current time is later than cutoff time             

                                            cutoff = true;         // indicate no member access

                                        } else {

                                            if (parm.cutoffdays == 1 && (date == todayDate || (date == tomorrowDate && ctime > parm.cutofftime))) {    // if cutoff day is the day before           

                                                cutoff = true;         // indicate no member access
                                            }
                                        }
                                    }

                                    //
                                    //  Check for lottery time that has already been processed
                                    //
                                    if (!lotteryName.equals("")) {

                                        //
                                        //  Get the current state of this lottery on the day of this tee time
                                        //
                                        lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lotteryName, course, con);

                                        if (lstate < 5 || noAccessAfterLottery) {    // if lottery not approved OR access not allowed after approval

                                            cutoff = true;        // do not allow access to this tee time (pre-booked lottery time)
                                        }

                                    } else {

                                        if (!lottery_color.equals("") && noAccessAfterLottery) {   // if it was a lottery time and access not allowed after processed

                                            cutoff = true;                    // do not allow access to this tee time
                                        }
                                    }


                                    if (restrictAllTees == true) {    // if all tee times are restricted

                                        cutoff = true;        // do not allow access to this tee time
                                    }
                                    
                                    if (club.equals("kiawahislandclub") && custom_int > 0) {
                                        cutoff = true;
                                    }


                                    if (!restricted && !cutoff && !(date == todayDate && time <= ctime)) {
                                    //if (!restricted && !cutoff && !(date == todayDate && time <= ctime) && sess_activity_id == 0) {     // if mem can edit tee time

                                        //
                                        //  Display the tee time as a clickable link (see form & js above)
                                        //
                                        //     refer to 'web utilities/foretees2.css' for style info
                                        //
                                        hashMap.clear();
                                        hashMap.put("type", "Member_slot");
                                        hashMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + String.format("%02d", min) + ampm + "|" + fb + "|" + user));
                                        hashMap.put("date", date);
                                        hashMap.put("course", course);
                                        hashMap.put("fb", fb);
                                        hashMap.put("p5", p5);
                                        hashMap.put("index", 999);
                                        hashMap.put("stime", stime);
                                        hashMap.put("base_url", Utilities.getBaseUrl(req, 0, club));

                                        out.print("<div class=\"item_container\">");
                                        out.print(iCalLink(rs.getInt("teecurr_id"), "tee"));
                                        out.print("<a class=\"teetime_button tee_list_color_4\" href=\"#\" "
                                                + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + buildItemDescription("Tee Time", stime, rwd) + "</a>");

                                        //out.println("      <a href=\"javascript: exeTtimeForm('" + date + "','" + dayname + "','" + p5 + "','" + course + "','" + fb + "','" + stime + "')\" class=mteetime>");
                                        //out.println("      Tee Time at " + stime2 + "</a>");
                                        out.print("</div>");


                                    } else {

                                        out.print("<div class=\"item_container\">");
                                        out.print("<div class=\"teetime\"" + buildItemDescription("Tee Time", stime, rwd) + "</div>");
                                        out.print("</div>");
                                    }
                                }

                                didone = true;        // set 'did one' flag

                            }                    // end of WHILE

                        } catch (Exception e1) {

                            Utilities.logError("Member_teelist: Error getting tee times for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e1.getMessage());                           // log it

                        } finally {

                            try {
                                rs.close();
                            } catch (SQLException ignored) {
                            }

                            try {
                                pstmt1.close();
                            } catch (SQLException ignored) {
                            }

                        }
                    }                       // end of IF tee times

                } // end if tlt

                //*******************************************************************************
                //  Check for any lotteries for this day (all will be zero if not supported)
                //*******************************************************************************
                //
                if (lotteryA[day] == 1) {        // if any lotteries  exist for this day

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT name, mm, dd, yy, day, hr, min, time, "
                                + "fb, courseName, id "
                                + "FROM lreqs3 "
                                + "WHERE (user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR "
                                + "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR "
                                + "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR "
                                + "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR "
                                + "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? OR "
                                + "orig_by LIKE ?) "
                                + "AND date = ? ORDER BY time");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, user);
                        pstmt1.setString(2, user);
                        pstmt1.setString(3, user);
                        pstmt1.setString(4, user);
                        pstmt1.setString(5, user);
                        pstmt1.setString(6, user);
                        pstmt1.setString(7, user);
                        pstmt1.setString(8, user);
                        pstmt1.setString(9, user);
                        pstmt1.setString(10, user);
                        pstmt1.setString(11, user);
                        pstmt1.setString(12, user);
                        pstmt1.setString(13, user);
                        pstmt1.setString(14, user);
                        pstmt1.setString(15, user);
                        pstmt1.setString(16, user);
                        pstmt1.setString(17, user);
                        pstmt1.setString(18, user);
                        pstmt1.setString(19, user);
                        pstmt1.setString(20, user);
                        pstmt1.setString(21, user);
                        pstmt1.setString(22, user);
                        pstmt1.setString(23, user);
                        pstmt1.setString(24, user);
                        pstmt1.setString(25, user);
                        pstmt1.setString(26, user);
                        pstmt1.setLong(27, date);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        while (rs.next()) {

                            lname = rs.getString(1);
                            mm = rs.getInt(2);
                            dd = rs.getInt(3);
                            yy = rs.getInt(4);
                            dayname = rs.getString(5);
                            hr = rs.getInt(6);
                            min = rs.getInt(7);
                            time = rs.getInt(8);
                            fb = rs.getInt(9);
                            course = rs.getString(10);
                            lottid = rs.getLong(11);

                            ampm = " AM";
                            if (hr == 12) {
                                ampm = " PM";
                            }
                            if (hr > 12) {
                                ampm = " PM";
                                hr = hr - 12;    // convert to conventional time
                            }

                            stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            stime = stime2 + ampm;        // create a value for time parm

                            //
                            //  Check if 5-somes supported for this course
                            //
                            fives = 0;        // init

                            PreparedStatement pstmtc = con.prepareStatement(
                                    "SELECT fives "
                                    + "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

                            pstmtc.clearParameters();        // clear the parms
                            pstmtc.setString(1, course);
                            rs2 = pstmtc.executeQuery();      // execute the prepared stmt

                            if (rs2.next()) {

                                fives = rs2.getInt(1);          // 5-somes
                            }
                            pstmtc.close();

                            //
                            //  check if 5-somes restricted for this time
                            //
                            rest = "";     // no rest5

                            if (fives != 0) {

                                PreparedStatement pstmtr = con.prepareStatement(
                                        "SELECT rest5 "
                                        + "FROM teecurr2 WHERE date = ? AND time =? AND fb = ? AND courseName = ?");

                                pstmtr.clearParameters();        // clear the parms
                                pstmtr.setLong(1, date);
                                pstmtr.setInt(2, time);
                                pstmtr.setInt(3, fb);
                                pstmtr.setString(4, course);
                                rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                                if (rs2.next()) {

                                    rest = rs2.getString(1);
                                }
                                pstmtr.close();
                            }

                            p5 = "No";                   // default = no 5-somes

                            if (fives != 0 && rest.equals("")) {     // if 5-somes are supported & not restricted

                                p5 = "Yes";                   // 5-somes ok
                            }


                            //
                            //  get the slots value and determine the current state for this lottery
                            //
                            PreparedStatement pstmt7d = con.prepareStatement(
                                    "SELECT slots "
                                    + "FROM lottery3 WHERE name = ?");

                            pstmt7d.clearParameters();          // clear the parms
                            pstmt7d.setString(1, lname);

                            rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                            if (rs2.next()) {

                                slots = rs2.getInt(1);
                            }
                            pstmt7d.close();



                            //
                            //  Get the current state of this lottery on the day of this tee time
                            //
                            lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lname, course, con);


                            //
                            //  Form depends on the state
                            //
                            hashMap.clear();
                            hashMap.put("day", dayname);
                            hashMap.put("date", date);
                            hashMap.put("course", course);
                            hashMap.put("p5", p5);
                            hashMap.put("index", 999);

                            if (club.equals("oldoaks")) {
                                item_name = buildItemDescription("Tee Time Request", stime, rwd);
                            } else if (!lotteryText.equals("")) {
                                item_name = buildItemDescription(lotteryText, stime, rwd);
                            } else {
                                item_name = buildItemDescription("Lottery Request", stime, rwd);
                            }

                            //String lotteryInfoStr = "'" + date + "','" + dayname + "','" + p5 + "','" + course + "','" + fb + "','" + stime + "','" + lname + "','" + lottid + "','";
                            if (lstate == 2) {       // if still ok to process lottery requests

                                hashMap.put("fb", fb);
                                hashMap.put("stime", stime);
                                hashMap.put("lname", lname);
                                hashMap.put("lottid", lottid);
                                hashMap.put("type", "Member_lott");
                                hashMap.put("lstate", lstate);
                                hashMap.put("slots", slots);
                                hashMap.put("base_url", Utilities.getBaseUrl(req, 0, club));

                                //
                                //  Display the lottery time as a clickable link (see form & js above)
                                //

                                out.print("<div class=\"item_container\">");
                                //if (sess_activity_id == 0) {
                                    //out.print("<a href=\"javascript: exeLott2Form(" + lotteryInfoStr + slots + "','" + lstate + "')\" class=mlottery>");
                                    out.println("<a class=\"lottery_button tee_list_color_6\" href=\"#\""
                                            + " data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">"
                                            + item_name + "</a>");
                                //} else {
                                //    out.println("<div class=\"tee_list_color_6\">" + item_name + "</div>");
                               // }
                                out.print("</div>");

                            } else {

                                if (lstate == 5) {       // if lottery has already been processed

                                    hashMap.put("type", "Member_slot");
                                    hashMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + String.format("%02d", min) + ampm + "|" + fb + "|" + user));

                                    //
                                    //  Display the lottery time as a clickable link (see form & js above)
                                    //
                                    out.print("<div class=\"item_container\">");
                                    out.println("<a class=\"teetime_button tee_list_color_6\" href=\"#\""
                                            + " data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + item_name + "</a>");
                                    //out.println("      <a href=\"javascript: exeLott5Form(" + lotteryInfoStr + lstate + "')\" class=mlottery>");
                                    out.print("</div>");

                                } else {

                                    out.print("<div class=\"item_container\">");
                                    out.print("<div class=\"tee_list_lott_time tee_list_color_6\">" + item_name + "</div>");
                                    out.print("</div>");

                                }
                            }

                            didone = true;        // set 'did one' flag

                        }                    // end of WHILE
                        pstmt1.close();

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting tee times for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }

                    }
                }    // end of IF lotteries
                
                
                //**********************************************************
                //  Check for any lessons for this day
                //**********************************************************
                //
                if (lessonA[day] == 1) {        // if any lessons  exist for this day

                    try {
                        lesson_id = 0;

                        pstmt1 = con.prepareStatement(
                                "SELECT proid, time, ltype, recid, activity_id "
                                + "FROM lessonbook5 "
                                + "WHERE memid = ? AND date = ? AND num > 0 "
                                + (!allAct ? "AND activity_id = ? " : "")
                                + "ORDER BY time");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, user);
                        pstmt1.setLong(2, date);
                        if (!allAct) {
                            pstmt1.setInt(3, selected_act_id);
                        }
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

//               if (rs.next()) {               // just get the first one as each lesson can have multiple entries
                        while (rs.next()) {              // get all lesson times that have a num greater than zero (not a subsequent time)

                            proid = rs.getInt(1);
                            time = rs.getInt(2);
                            ltype = rs.getString(3);
                            lesson_id = rs.getInt("recid");
                            returned_activity_id = rs.getInt("activity_id");

                            hr = time / 100;
                            min = time - (hr * 100);
                            
                            ampm = " AM";
                            if (hr == 12) {
                                ampm = " PM";
                            }
                            if (hr > 12) {
                                ampm = " PM";
                                hr = hr - 12;    // convert to conventional time
                            }

                            stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            dayname = day_table[col];                           // get the name of this day

                            String calDate = month + "/" + day + "/" + year;       // set parm for form
                            
                            long resUtime = timeUtil.getClubUnixTime(req, (int)date, time);

                            //
                            //  Display the lesson time as a clickable link (see form & js above)
                            //
                            //     refer to 'web utilities/foretees2.css' for style info
                            //
                            // Only display as selectable link if on current activity
                            if (club.equals("interlachenspa")) {
                                item_name = buildItemDescription("Spa Reservation", stime2+ampm, rwd);
                            } else {
                                item_name = buildItemDescription(sysLingo.TEXT_Lesson, stime2+ampm, rwd);
                            }
                            out.print("<div class=\"item_container\">");
                            if (resUtime > now) {
                                String url = "Member_lesson?" + httpConnect.getUri(new String[]{
                                    "proid", rs.getString("proid"),
                                    "date", Long.toString(date),
                                    "time", Integer.toString(time),
                                    "ltype", rs.getString("ltype"),
                                    "lesson_id", rs.getString("recid"),
                                    "activity_id", Integer.toString(returned_activity_id),
                                    "index", "999",
                                    "reqtime", "yes"
                                }, true);

                                out.println(tags.getFtLink(item_name, 
                                    Utilities.getBaseUrl(req, returned_activity_id, club) + url.replace("&", "&amp;"), 
                                    "tee_list_color_5"));
                            } else {
                                out.println("<div class=\"tee_list_color_5\">" + item_name + "</div>");
                            }
                            out.print("</div>");

                            didone = true;        // set 'did one' flag

                        }                    // end of WHILE

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting lesson times for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e1.getMessage());                           // log it

                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }

                    }
                }    // end of IF lessons


                //**********************************************************
                //  Check for any lessongrps (Clinic only) for this day
                //**********************************************************
                //
                if (lessongrpA[day] == 1) {        // if any lessongrps  exist for this day

                    try {
                        lesson_id = 0;

                        pstmt1 = con.prepareStatement(
                                "SELECT proid, lname "
                                + "FROM lgrpsignup5 "
                                + "WHERE memid = ? AND date = ?");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, user);
                        pstmt1.setLong(2, date);
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        while (rs.next()) {

                            proid = rs.getInt(1);
                            lgname = rs.getString(2);

                            //
                            //  Get the start time for this group lesson
                            //
                            PreparedStatement pstmt7d = con.prepareStatement(
                                    "SELECT stime, activity_id, lesson_id "
                                    + "FROM lessongrp5 WHERE proid = ? AND lname = ? "
                                    + (!allAct ? "AND activity_id = ?" : ""));

                            pstmt7d.clearParameters();          // clear the parms
                            pstmt7d.setInt(1, proid);
                            pstmt7d.setString(2, lgname);
                            if (!allAct) {
                                pstmt7d.setInt(3, selected_act_id);
                            }

                            rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                            if (rs2.next()) {

                                time = rs2.getInt("stime");
                                returned_activity_id = rs2.getInt("activity_id");
                                lesson_id = rs2.getInt("lesson_id");

                                hr = time / 100;
                                min = time - (hr * 100);

                                ampm = " AM";
                                if (hr == 12) {
                                    ampm = " PM";
                                }
                                if (hr > 12) {
                                    ampm = " PM";
                                    hr = hr - 12;    // convert to conventional time
                                }

                                stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            } else {                 // end of IF
                                continue;  // If no data found (means it was a different activity), skip over the rest of the loop for this iteration
                            }
                            pstmt7d.close();

                            //
                            //  Display the lesson time as a clickable link (see form & js above)
                            //
                            //     refer to 'web utilities/foretees2.css' for style info
                            //
                            out.print("<div class=\"item_container\">");
                            //item_name = buildItemDescription("Group Lesson", stime2+ampm);
                            item_name = buildItemDescription(lgname, stime2+ampm, rwd);
                            if (returned_activity_id == sess_activity_id) {
                                out.println("<a href=\"javascript: exeLgroupForm('" + proid + "','" + date + "','" + lgname + "','" + lesson_id + "','" + sess_activity_id + "')\" class=\"tee_list_color_5\">"
                                        + item_name + "</a>");
                            } else {
                                out.println("<div class=\"tee_list_color_5\">" + item_name + "</div>");
                            }
                            out.print("</div>");

                            didone = true;        // set 'did one' flag

                        }                    // end of WHILE

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting group lesson times (clinic/golf) for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }
                    }
                }


                //**********************************************************
                //  Check for any lessongrps (Clinic Series only) for this day
                //**********************************************************
                //        
                if (lessongrpseriesA.get(day).size() > 0) {        // if any lessongrps (Clinic Series) exist for this day
                 
                    try {
                        lesson_id = 0;
                        
                        for (int j = 0; j < lessongrpseriesA.get(day).size(); j++) {

                            lgname = (String) lessongrpseriesA.get(day).get(j).get(0);
                            proid = (Integer) lessongrpseriesA.get(day).get(j).get(1);

                            //
                            //  Get the start time for this group lesson
                            //
                            PreparedStatement pstmt7d = con.prepareStatement(
                                    "SELECT stime, activity_id, lesson_id "
                                    + "FROM lessongrp5 WHERE proid = ? AND lname = ? "
                                    + (!allAct ? "AND activity_id = ?" : ""));

                            pstmt7d.clearParameters();          // clear the parms
                            pstmt7d.setInt(1, proid);
                            pstmt7d.setString(2, lgname);
                            if (!allAct) {
                                pstmt7d.setInt(3, selected_act_id);
                            }

                            rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                            if (rs2.next()) {

                                time = rs2.getInt("stime");
                                returned_activity_id = rs2.getInt("activity_id");
                                lesson_id = rs2.getInt("lesson_id");

                                hr = time / 100;
                                min = time - (hr * 100);

                                ampm = " AM";
                                if (hr == 12) {
                                    ampm = " PM";
                                }
                                if (hr > 12) {
                                    ampm = " PM";
                                    hr = hr - 12;    // convert to conventional time
                                }

                                stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            } else {                 // end of IF
                                continue;  // If no data found (means it was a different activity), skip over the rest of the loop for this iteration
                            }
                            pstmt7d.close();

                            //
                            //  Display the lesson time as a clickable link (see form & js above)
                            //
                            //     refer to 'web utilities/foretees2.css' for style info
                            //
                            out.print("<div class=\"item_container\">");
                            item_name = buildItemDescription(lgname, stime2+ampm, rwd);
                            if (returned_activity_id == sess_activity_id) {
                                out.println("<a href=\"javascript: exeLgroupForm('" + proid + "','" + date + "','" + lgname + "','" + lesson_id + "','" + sess_activity_id + "')\" class=\"tee_list_color_5\">"
                                        + item_name + "</a>");
                            } else {
                                out.println("<div class=\"tee_list_color_5\">" + item_name + "</div>");
                            }
                            out.print("</div>");

                            didone = true;        // set 'did one' flag

                        }    // end of FOR

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting group lesson times (clinic series) for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }
                    }
                    
                    
                    /*
                    //  Custom to check for dependents' group lessons
                    if ((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent")) {     // Custom to check for Dependents registered

                        //  Get each dependent and check if they are registered
                        String mNumD = "";
                        PreparedStatement pstmtDenver = null;
                        PreparedStatement pstmtDenver2 = null;
                        PreparedStatement pstmt7d = null;
                        ResultSet rsDenver = null;
                        ResultSet rsDenver2 = null;

                        try {

                            pstmtDenver = con.prepareStatement(
                                    "SELECT memNum "
                                    + "FROM member2b WHERE username = ?");      // get this member's mNum

                            pstmtDenver.clearParameters();       
                            pstmtDenver.setString(1, user);
                            rsDenver = pstmtDenver.executeQuery();    

                            if (rsDenver.next()) {

                                mNumD = rsDenver.getString("memNum");                                            
                            }
                            pstmtDenver.close();

                            if (!mNumD.equals("")) {        // if member number found for this member

                                //  Locate any Dependents for this member and search their event signups

                                pstmtDenver = con.prepareStatement(
                                        "SELECT username "
                                        + "FROM member2b WHERE memNum = ? AND m_type = 'Dependent'");

                                pstmtDenver.clearParameters();      
                                pstmtDenver.setString(1, mNumD);
                                rsDenver = pstmtDenver.executeQuery();    

                                loopDenver:
                                while (rsDenver.next()) {

                                    String userD = rsDenver.getString("username");   // get the dependent's username and look for any group lessons for this child
                                    
                                    lesson_id = 0;

                                    pstmtDenver2 = con.prepareStatement(
                                            "SELECT proid, lname "
                                            + "FROM lgrpsignup5 "
                                            + "WHERE memid = ? AND date = ?");

                                    pstmtDenver2.clearParameters();        // clear the parms
                                    pstmtDenver2.setString(1, userD);
                                    pstmtDenver2.setLong(2, date);
                                    rsDenver2 = pstmtDenver2.executeQuery();      // execute the prepared stmt

                                    while (rsDenver2.next()) {

                                        proid = rsDenver2.getInt(1);
                                        lgname = rsDenver2.getString(2);

                                        //
                                        //  Get the start time for this group lesson
                                        //
                                        pstmt7d = con.prepareStatement(
                                                "SELECT stime, activity_id, lesson_id "
                                                + "FROM lessongrp5 WHERE proid = ? AND lname = ? "
                                                + (!allAct ? "AND activity_id = ?" : ""));

                                        pstmt7d.clearParameters();          // clear the parms
                                        pstmt7d.setInt(1, proid);
                                        pstmt7d.setString(2, lgname);
                                        if (!allAct) {
                                            pstmt7d.setInt(3, selected_act_id);
                                        }

                                        rs2 = pstmt7d.executeQuery();      // find matching group lesson

                                        if (rs2.next()) {

                                            time = rs2.getInt("stime");
                                            returned_activity_id = rs2.getInt("activity_id");
                                            lesson_id = rs2.getInt("lesson_id");

                                            hr = time / 100;
                                            min = time - (hr * 100);

                                            ampm = " AM";
                                            if (hr == 12) {
                                                ampm = " PM";
                                            }
                                            if (hr > 12) {
                                                ampm = " PM";
                                                hr = hr - 12;    // convert to conventional time
                                            }

                                            stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                                        } else {                 // end of IF
                                            continue;  // If no data found (means it was a different activity), skip over the rest of the loop for this iteration
                                        }
                                        pstmt7d.close();

                                        //
                                        //  Display the lesson time as a clickable link (see form & js above)
                                        //
                                        //     refer to 'web utilities/foretees2.css' for style info
                                        //
                                        out.print("<div class=\"item_container\">");
                                        item_name = buildItemDescription("Child Group Activity", stime2+ampm);
                                        
                                        if (returned_activity_id == sess_activity_id || returned_activity_id == selected_act_id || selected_act_id == all_activities) {
                                        //if (returned_activity_id == sess_activity_id) {
                                            out.println("<a href=\"javascript: exeLgroupForm('" + proid + "','" + date + "','" + lgname + "','" + lesson_id + "','" + sess_activity_id + "')\" class=\"tee_list_color_5\">"
                                                    + item_name + "</a>");
                                        } else {
                                            out.println("<div class=\"tee_list_color_5\">" + item_name + "</div>");
                                        }
                                        out.print("</div>");

                                        didone = true;        // set 'did one' flag

                                    }                    // end of WHILE group lessons
                                    
                                }       // end of WHILE Dependents
                            }   

                        } catch (Exception e1) {

                            Utilities.logError("Member_teelist: Error getting Dependents group lessons for " + club + ", User: " + user + ", Error: " + e1.getMessage());                           // log it

                        } finally {

                            try {
                                if (rsDenver != null) rsDenver.close();
                            } catch (SQLException ignored) {
                            }
                            try {
                                if (rsDenver2 != null) rsDenver2.close();
                            } catch (SQLException ignored) {
                            }
                            try {
                                if (rs2 != null) rs2.close();
                            } catch (SQLException ignored) {
                            }

                            try {
                                if (pstmtDenver != null) pstmtDenver.close();
                            } catch (SQLException ignored) {
                            }
                            try {
                                if (pstmtDenver2 != null) pstmtDenver2.close();
                            } catch (SQLException ignored) {
                            }
                            try {
                                if (pstmt7d != null) pstmt7d.close();
                            } catch (SQLException ignored) {
                            }

                        }

                    }       // end of IF denvercc
                    * 
                    */
            

                }    // end of IF lessongrps


                //**********************************************************
                //  Check for any events for this day
                //**********************************************************
                //
                if (eventA[day] == 1) {        // if any events  exist for this day

                    try {

                        int event_id = 0;
                        int event_type = 0;
                        int event_hr = 0;
                        int event_min = 0;
                        
                        String event_ampm = "";

                        pstmt1 = con.prepareStatement(
                                "SELECT name, coursename, signup, activity_id, event_id, act_hr, act_min, type "
                                + "FROM events2b WHERE date = ? " + (showGuestOnlyEvents ? "" : "AND gstOnly = 0 ") + "AND signup_on_teesheet = 0 AND inactive = 0 "
                                + (!allAct ? "AND activity_id = ? " : " ") 
                                + "ORDER BY act_hr, act_min");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setLong(1, date);
                        if (!allAct) {
                            pstmt1.setInt(2, selected_act_id);
                        }
                        rs = pstmt1.executeQuery();      // execute the prepared stmt

                        while (rs.next()) {

                            ename = rs.getString("name");
                            course = rs.getString("coursename");
                            signUp = rs.getInt("signup");
                            event_hr = rs.getInt("act_hr");
                            event_min = rs.getInt("act_min");
                            event_type = rs.getInt("type");
                            //stime = Utilities.getSimpleTime(rs.getInt("stime"));
                            returned_activity_id = rs.getInt("activity_id");
                            event_id = rs.getInt("event_id");

                            if (club.equals("desertmountain") && !msubtype.equalsIgnoreCase("League")) {

                                ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                                temp_category_ids.add(11);

                                if (Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con).equals("")) {
                                    continue;
                                }
                            } else if (club.equals("olyclub") && !isOlyClubWGN) {

                                ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                                temp_category_ids.add(1);

                                if (Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con).equals("")) {
                                    continue;
                                }
                            } else if (club.equals("oceanreef") && !msubtype.equalsIgnoreCase("Friday Boys")) {

                                ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                                temp_category_ids.add(1);

                                if (Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con).equals("")) {
                                    continue;
                                }
                            }
                            
                            try {
                                root_id = getActivity.getRootIdFromActivityId(returned_activity_id, con);
                            } catch (Exception ignore) {
                                root_id = returned_activity_id;
                            }

                            activity_name = getActivity.getActivityName(root_id, con);
                            
                            if (activity_name.equals("")) {
                                activity_name = "Golf";
                            }

                            hashMap.clear();
                            hashMap.put("type", "event");
                            hashMap.put("id", event_id);
                            //hashMap.put("course", course);
                            //hashMap.put("activity_id", returned_activity_id);
                            //hashMap.put("base_url", Utilities.getBaseUrl(req, returned_activity_id, club));
                            //hashMap.put("index", 999);       // was 995 (teelist_list) ???

                            //
                            //  Check if this member is signed up
                            //
                            boolean signedup = false;
                            int id = 0;

                            if (event_type == 1 || returned_activity_id != 0) {
                                
                                // Create time value for actual start time of event if it's a shotgun event
                                event_ampm = " AM";

                                if (event_hr == 0) {

                                    event_hr = 12;                 // change to 12 AM (midnight)

                                } else {

                                    if (event_hr == 12) {

                                        event_ampm = " PM";         // change to Noon
                                    }
                                }
                                if (event_hr > 12) {

                                    event_hr = event_hr - 12;
                                    event_ampm = " PM";             // change to 12 hr clock
                                }

                                //  convert time to hour and minutes
                                stime = event_hr + ":" + Utilities.ensureDoubleDigit(event_min) + event_ampm;
                                
                            } else {
                                
                                stime = "";
                            }
                            
                            if (signUp != 0) {           // if members can signup

                                PreparedStatement pstmte = con.prepareStatement(
                                        "SELECT id, wait "
                                        + "FROM evntsup2b WHERE name = ? AND inactive = 0 "
                                        + "AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                        + "OR username5 = ?)");

                                pstmte.clearParameters();        // clear the parms
                                pstmte.setString(1, ename);
                                pstmte.setString(2, user);
                                pstmte.setString(3, user);
                                pstmte.setString(4, user);
                                pstmte.setString(5, user);
                                pstmte.setString(6, user);
                                rs2 = pstmte.executeQuery();      // execute the prepared stmt

                                if (rs2.next()) {

                                    signedup = true;              // set member signed up
                                    wait = rs2.getInt("wait");
                                    id = rs2.getInt("id");
                                }
                                pstmte.close();

                                //
                                //  display a link to the signup
                                //
                                out.print("<div class=\"item_container\">");
                                if (signedup == true) {     // if member is registered

                                    //
                                    //  Display the event name as a clickable link (see form & js above)
                                    //
                                    if (returned_activity_id == sess_activity_id || returned_activity_id == selected_act_id || selected_act_id == all_activities) {

                                        out.println(iCalLink(id, "evntsup"));
                                        if (club.equalsIgnoreCase("tcclub") && wait != 0) {
                                            out.println("<a class=\"event_button tee_list_color_7\" href=\"#\" "
                                                    + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");
                                        } else {
                                            out.println("<a class=\"event_button tee_list_color_1\" href=\"#\" "
                                                    + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");
                                        }
                                        if (wait == 0) {
                                            out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Registered", stime, rwd));

                                        } else {
                                            out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Wait List", stime, rwd));
                                        }

                                        out.println("</a>");

                                    } else {
                                        out.println("");
                                        if (wait == 0) {

                                            out.println(iCalLink(id, "eventsup"));

                                            out.println("<div class=\"tee_list_color_1\">");
                                            out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Registered", stime, rwd));
                                            out.println("</div>");

                                        } else {
                                            if (club.equalsIgnoreCase("tcclub")) {
                                                out.println("<div class=\"tee_list_color_7\">");
                                            } else {
                                                out.println("<div class=\"tee_list_color_1\">");
                                            }
                                            out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Wait List", stime, rwd));
                                            out.println("</div>");
                                        }
                                    }

                                } else {     // member not registered 

                                    //  Custom to check for dependents registered for this event
                                    if (((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent")) ||
                                          (club.equals("edina") && (mtype.startsWith("Adult") || mtype.startsWith("Pre-Leg")))) {  // Custom to check for Dependents registered

                                        //  Get each dependent and check if they are registered
                                        String mNumD = "";
                                        PreparedStatement pstmtDenver = null;
                                        PreparedStatement pstmtDenver2 = null;
                                        ResultSet rsDenver = null;
                                        ResultSet rsDenver2 = null;

                                        String mtypeString = "m_type = 'Dependent'";      // for Denver

                                        if (club.equals("edina")) {

                                           mtypeString = "(m_type = 'Qualified Junior' OR m_type = 'Jr Male' OR m_type = 'Jr Female')";  // for Edina
                                        }

                                        try {

                                            pstmtDenver = con.prepareStatement(
                                                    "SELECT memNum "
                                                    + "FROM member2b WHERE username = ?");      // get this member's mNum

                                            pstmtDenver.clearParameters();       
                                            pstmtDenver.setString(1, user);
                                            rsDenver = pstmtDenver.executeQuery();    

                                            if (rsDenver.next()) {

                                                mNumD = rsDenver.getString("memNum");                                            
                                            }
                                            pstmtDenver.close();

                                            if (!mNumD.equals("")) {        // if member number found for this member

                                                //  Locate any Dependents for this member and search their event signups

                                                pstmtDenver = con.prepareStatement(
                                                        "SELECT username "
                                                        + "FROM member2b WHERE memNum = ? AND " +mtypeString);

                                                pstmtDenver.clearParameters();      
                                                pstmtDenver.setString(1, mNumD);
                                                rsDenver = pstmtDenver.executeQuery();    

                                                loopDenver:
                                                while (rsDenver.next()) {

                                                    String userD = rsDenver.getString("username");   // get the dependent's username and look for any event signups for this child

                                                    pstmtDenver2 = con.prepareStatement(
                                                            "SELECT id, wait "
                                                            + "FROM evntsup2b WHERE name = ? AND inactive = 0 "
                                                            + "AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                                            + "OR username5 = ?)");

                                                    pstmtDenver2.clearParameters();       
                                                    pstmtDenver2.setString(1, ename);
                                                    pstmtDenver2.setString(2, userD);
                                                    pstmtDenver2.setString(3, userD);
                                                    pstmtDenver2.setString(4, userD);
                                                    pstmtDenver2.setString(5, userD);
                                                    pstmtDenver2.setString(6, userD);
                                                    rsDenver2 = pstmtDenver2.executeQuery();     

                                                    if (rsDenver2.next()) {

                                                        signedup = true;              // set member/dependent signed up
                                                        wait = rsDenver2.getInt("wait");
                                                        id = rsDenver2.getInt("id");

                                                        //
                                                        //  Display the event name as a clickable link if event is for current activity, or the selected id, or selected id is ALL
                                                        //
                                                        if (returned_activity_id == sess_activity_id || returned_activity_id == selected_act_id || selected_act_id == all_activities) {

                                                            out.println(iCalLink(id, "evntsup"));
                                                            out.print("<a class=\"event_button tee_list_color_1\" href=\"#\" "
                                                                    + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");

                                                            if (wait == 0) {

                                                                out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Child Registered", stime, rwd));

                                                            } else {

                                                                out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Child on Wait List", stime, rwd));
                                                            }

                                                        } else {

                                                            if (wait == 0) {

                                                                out.println(iCalLink(id, "eventsup"));

                                                                out.println("<div class=\"tee_list_color_1\">");
                                                                out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Child Registered", stime, rwd));
                                                                out.println("</div>");

                                                            } else {

                                                                out.println("<div class=\"tee_list_color_1\">");
                                                                out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : "") + " - Child on Wait List", stime, rwd));
                                                                out.println("</div>");
                                                            }
                                                        }

                                                        break loopDenver;       // don't need to check any other dependents
                                                    }
                                                    pstmtDenver2.close();

                                                }            // check all dependents
                                                pstmtDenver.close();

                                            }    // end of IF mNumD (if member number found)

                                        } catch (Exception e9) {

                                            Utilities.logError("Member_teelist: Error processing events (custom) for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e9.getMessage());

                                        } finally {

                                            try {
                                                if (rsDenver != null) rsDenver.close();
                                            } catch (SQLException ignored) {
                                            }
                                            try {
                                                if (rsDenver2 != null) rsDenver2.close();
                                            } catch (SQLException ignored) {
                                            }

                                            try {
                                                if (pstmtDenver != null) pstmtDenver.close();
                                            } catch (SQLException ignored) {
                                            }
                                            try {
                                                if (pstmtDenver2 != null) pstmtDenver2.close();
                                            } catch (SQLException ignored) {
                                            }

                                        }
                                    }      // end of custom for dependents

                                    if (signedup == false) {     // if dependents not registered either - add link to event info

                                        out.println(iCalLink(event_id, "event"));

                                        if (returned_activity_id == sess_activity_id || returned_activity_id == selected_act_id || selected_act_id == all_activities) {
                                            out.print("<a class=\"event_button tee_list_color_2\" href=\"#\" "
                                                    + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");
                                            //out.println("      <a href=\"javascript: exeEventForm('" + ename + "','" + course + "')\" class=tee_list_color_2>");
                                            out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : ""), stime, rwd));
                                            out.println("</a>");
                                        } else {
                                            out.println("<div class=\"tee_list_color_2\">");
                                            out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : ""), stime, rwd));
                                            out.println("</div>");
                                        }
                                    }
                                }

                                out.print("</div>");

                            } else {     // no sign up available

                                //
                                //  Go to Member_sheet to display the event info
                                //
                                out.print("<div class=\"item_container\">");
                                out.println(iCalLink(event_id, "event"));
                                if (returned_activity_id == sess_activity_id) {
                                    out.print("<a class=\"event_button tee_list_color_3\" href=\"#\" "
                                            + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");
                                    //out.println("      <a href=\"javascript: exeEventForm2('" + ename + "')\" class=tee_list_color_3>");
                                    out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : ""), stime, rwd));
                                    out.println("</a>");
                                } else {
                                    out.println("<div class=\"tee_list_color_3\">");
                                    out.println(buildItemDescription(ename + (selected_act_id == all_activities ? " - (" + activity_name + ")" : ""), stime, rwd));
                                    out.println("</div>");
                                }
                                out.print("</div>");

                            }       // end of IF signup

                        }          // end of WHILE events

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error processing events for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }

                    }

                }    // end of IF events


                //
                // Check for any wait list signups for this day
                //
                if (waitlistA[day] != 0) {

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT wl.wait_list_id, wl.course, wls.date, wls.ok_stime, wls.ok_etime, wls.wait_list_signup_id, wl.color, "
                                + "DATE_FORMAT(wls.date, '%W') AS day_name, DATE_FORMAT(wls.date, '%Y%m%d') AS dateymd "
                                + "FROM wait_list_signups wls "
                                + "LEFT OUTER JOIN wait_list wl ON wls.wait_list_id = wl.wait_list_id "
                                + "WHERE wls.wait_list_signup_id = ?");

                        pstmt1.clearParameters();
                        pstmt1.setInt(1, waitlistA[day]);
                        rs = pstmt1.executeQuery();

                        if (rs.next()) {

                            out.print("<div class=\"item_container\">");
                            if (sess_activity_id == 0) {

                                hashMap.clear();
                                hashMap.put("type", "Member_waitlist");
                                hashMap.put("waitListId", rs.getInt("wait_list_id"));
                                hashMap.put("course", rs.getString("course"));
                                hashMap.put("returnCourse", rs.getString("course"));
                                hashMap.put("index", 995);
                                hashMap.put("date", rs.getInt("dateymd"));
                                //  style=\"background-color:"+rs.getString("color")+";\"
                                out.print("<a class=\"waitlist_button calendar_link tee_list_color_7\" href=\"#\" "
                                        + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");

                                //out.println("      <a class=\"mWLsignup\" href=\"javascript:void(0)\" "
                                //        + "&index=999&day=" + rs.getString("day_name") + "&course=" + rs.getString("course") + "&returnCourse=" + rs.getString("course") + "'\" >");
                                //out.print(SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
                                out.print("Wait List Sign-up");
                                out.println("</a>");
                            } else {
                                out.println("<div class=\"tee_list_color_7\">Wait List Sign-up</div>");
                            }
                            out.print("</div>");
                        }

                    } catch (Exception exp) {
                    } finally {

                        try {
                            rs.close();
                        } catch (SQLException ignored) {
                        }

                        try {
                            pstmt1.close();
                        } catch (SQLException ignored) {
                        }

                    }

                } // end IF wait list signup found


                if (parm.genrez_mode != 0) {
                    //
                    // Check for any activity signups for this day
                    //
                    if (activitiesA[day] != 0) {

                        boolean printbr = true;

                        try {

                            pstmt1 = con.prepareStatement(
                                    "SELECT * FROM ("
                                    + "SELECT a.activity_id, a.sheet_id, date_time, "
                                    + "IF(a.related_ids <> '', a.related_ids, a.sheet_id) as related_ids, "
                                    + "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, "
                                    + "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time "
                                    + "FROM activity_sheets a, activity_sheets_players ap "
                                    + "WHERE a.sheet_id = ap.activity_sheet_id "
                                    + (selected_act_id != all_activities ? "AND a.activity_id IN (" + selected_act_children + ") " : "")
                                    + " AND date_time BETWEEN ? AND ? "
                                    //+ "AND DATE_FORMAT(a.date_time, '%Y%m%d') = ? "
                                    + "AND (ap.username = ? OR a.orig_by = ?) "
                                    + "ORDER BY a.date_time"
                                    + ") AS act_times "
                                    + "GROUP BY related_ids "
                                    + "ORDER BY date_time");

                            pstmt1.clearParameters();
                            //pstmt1.setLong(1, date);
                            pstmt1.setString(1, Utilities.get_mysql_timestamp((int)date, 0));
                            pstmt1.setString(2, Utilities.get_mysql_timestamp((int)date, 2359));
                            pstmt1.setString(3, user);
                            pstmt1.setString(4, user);
                            rs = pstmt1.executeQuery();

                            while (rs.next()) {
                                
                                long restime = timeUtil.getClubUnixTimeFromDb(req, rs.getString("date_time"));

                                try {
                                    root_id = getActivity.getRootIdFromActivityId(rs.getInt("activity_id"), con);
                                } catch (Exception ignore) {
                                    root_id = rs.getInt("activity_id");
                                }

                                activity_name = getActivity.getActivityName(root_id, con);

                                item_name = buildItemDescription(activity_name, rs.getString("pretty_time"), rwd);
                                
                                out.print("<div class=\"item_container\">");
                                //if (root_id == sess_activity_id) {
                                if (restime >= now) {
                                    //hashMap.clear();
                                    //hashMap.put("type", "Member_activity_slot");
                                    //hashMap.put("slot_id", rs.getInt("sheet_id"));
                                    //hashMap.put("date", rs.getString("dateymd"));
                                    //hashMap.put("index", 999);

                                    out.print("<a class=\"ftCsLink calendar_link tee_list_color_6\" "
                                        + "href=\"#\" data-fthref=\"" + Utilities.getBaseUrl(req, rs.getInt("activity_id"), club)
                                        + "Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + timeUtil.getClubDate(req, restime) + "&_" + now + "\">"
                                        + item_name + "</a>");

                                    //out.print("<a class=\"activity_button calendar_link tee_list_color_6\" href=\"#\" "
                                     //       + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">"
                                     //       + item_name + "</a>");

                                    //out.println("      <a  class=\"tee_list_color_6\" href=\"javascript:void(0)\" ");
                                    //out.println("      onclick=\"top.location.href='Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=999'\">");
                                    //out.print(activity_name + " at " + rs.getString("pretty_time"));
                                    //out.println("</a>");
                                } else {
                                    out.println("<div class=\"tee_list_color_6\">" + item_name + "</div>");
                                }
                                out.print("</div>");

                            }

                        } catch (Exception exc) {

                            Utilities.logError("Member_teelist - " + club + " - Error loading activity times: " + exc.toString());

                        } finally {

                            try {
                                rs.close();
                            } catch (SQLException ignored) {
                            }

                            try {
                                pstmt1.close();
                            } catch (SQLException ignored) {
                            }

                        }

                    } // end IF wait list signup found
                }


                // add a check here to see if dining is enabled and if so loop the dining array
                if (organization_id != 0 && diningA[day] != 0 && (selected_act_id == all_activities || selected_act_id == dining_activity_id)) {

                    Connection con_d = null;

                    try {

                        String state = "";
                                int dtime = 0;

                        con_d = Connect.getDiningCon();

                        if (con_d != null) {

                            pstmt1 = con_d.prepareStatement(""
                                    + "SELECT id, name, members_can_make_reservations, to_char(start_time, 'HH24MI') AS stime, to_char(end_time, 'HH24MI') AS etime "
                                    + "FROM events "
                                    + "WHERE organization_id = ? AND to_char(date, 'YYYYMMDD')::int = ? AND cancelled = false "
                                    + "ORDER BY stime");

                            pstmt1.clearParameters();
                            pstmt1.setInt(1, organization_id);
                            pstmt1.setLong(2, date);

                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                state = ""; // reset
                                dtime = Integer.parseInt(rs.getString("stime")); // first set to the event start time

                                // lookup this member in the reservations table to see if they are already signed up for this dining event
                                pstmt2 = con_d.prepareStatement(""
                                        + "SELECT id, state, to_char(time, 'HH24MI') AS stime  "
                                        + "FROM reservations "
                                        + "WHERE "
                                        + "category = 'event' AND state <> 'cancelled' AND "
                                        + "organization_id = ? AND "
                                        + "event_id = ? AND "
                                        + "person_id = ?");

                                pstmt2.setInt(1, organization_id);
                                pstmt2.setInt(2, rs.getInt("id"));
                                pstmt2.setInt(3, person_id);

                                rs2 = pstmt2.executeQuery();

                                if (rs2.next()) {

                                    state = rs2.getString("state");
                                    dtime = Integer.parseInt(rs2.getString("stime")); // override dtime to time of actual reservation if found

                                }

                                String mNum = "";

                                if (user != null && !user.equals("")) {

                                    mNum = Utilities.getmNum(user, con);      // get this mem's member number
                                }


                                item_name = buildItemDescription(rs.getString("name"), Utilities.getSimpleTime(dtime), rwd);

                                // if member can signup for the dining event AND they are not already signed-up then make it a link
                                // and show their reservation status, otherwise just display the name of the event
              

                                    out.print("<div class=\"item_container\">");

                                    // only make it a link if they are able to sign up but have not yet signed up.
                                    if (rs.getBoolean("members_can_make_reservations") == false) {
                                        
                                        // event does not allow online signup - display non-link
                                        out.print("<div class=\"tee_list_color_10\">" + item_name + "</div>");
                                        
                                    } else if (state.equals("") || state.equals("cancelled")) {

                                        // event allows signups but user is not signed up or has a cancelled signup
                                        out.print("<a href=\"#\" data-ftjson=\""+reservationUtil.linkJsonEsc(rs.getInt("id"), dining_activity_id)+ "\" class=\"event_button tee_list_color_10\">");
                                        out.print(item_name);
                                        out.println("</a>");

                                    } else {

                                        // user is already signed up - display link to their reservation
                                        //out.print("<a href=\"#\" data-fthref=\""+dining_base+"Dining_slot?reservation_id=" + rs2.getInt("id") + "\" class=\"ftCsLink tee_list_color_9\">");
                                        out.print("<a href=\"#\" data-ftjson=\""+reservationUtil.linkJsonEsc(rs.getInt("id"), dining_activity_id)+ "\" class=\"event_button tee_list_color_10\">"); 
                                        out.print(item_name + " - Registered");
                                        out.println("</a>");

                                        //out.print("<span class=\"item_state\">(" + state + ")</span>");
                                        
                                        //out.println("</div>");
                                    }
                                    out.print("</div>");

                 

                            } // end while loop of events

                        }

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting specific dinning event info for " + club + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (Exception ignore) {
                        }

                        try {
                            pstmt1.close();
                        } catch (Exception ignore) {
                        }

                        try {
                            rs2.close();
                        } catch (Exception ignore) {
                        }

                        try {
                            pstmt2.close();
                        } catch (Exception ignore) {
                        }

                        try {
                            con_d.close();
                        } catch (Exception ignore) {
                        }

                    }

                } // end IF wait list signup found


                //
                // If a dining reservation was found for this day then output its details
                //
                if (organization_id != 0 && diningR[day] != 0 && (selected_act_id == all_activities || selected_act_id == dining_activity_id)) {

                    Connection con_d = null;

                    try {

                        int dtime = 0;

                        con_d = Connect.getDiningCon();

                        if (con_d != null) {

                            pstmt1 = con_d.prepareStatement(""
                                    + "SELECT id, to_char(time, 'HH24MI') AS stime "
                                    + "FROM reservations "
                                    + "WHERE "
                                    + " category = 'dining' AND state <> 'cancelled' AND "
                                    + " organization_id = ? AND "
                                    + " person_id = ? AND "
                                    + " to_char(date, 'YYYYMMDD')::int = ? "
                                    + "ORDER BY time");

                            pstmt1.clearParameters();
                            pstmt1.setInt(1, organization_id);
                            pstmt1.setInt(2, person_id);
                            pstmt1.setLong(3, date);

                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                dtime = Integer.parseInt(rs.getString("stime"));

                                item_name = buildItemDescription("Reservation", Utilities.getSimpleTime(dtime), rwd);

                                out.print("<div class=\"item_container\">");
                                out.println("<a class=\"tee_list_color_9 ftCsLink\" href=\"#\" "
                                        + "data-fthref=\""+dining_base+"Dining_slot?reservation_id=" + rs.getInt("id") + "\">"
                                        + item_name + "</a>");
                                out.print("</div>");

                            }

                        }

                    } catch (Exception e1) {

                        Utilities.logError("Member_teelist: Error getting specific dinning reservation info for " + club + ", Error: " + e1.getMessage());

                    } finally {

                        try {
                            rs.close();
                        } catch (Exception ignore) {
                        }

                        try {
                            pstmt1.close();
                        } catch (Exception ignore) {
                        }

                        try {
                            con_d.close();
                        } catch (Exception ignore) {
                        }

                    }

                } // end if dining rez found



                //
                //**********************************************************
                //  End of display for this day - get next day
                //**********************************************************
                //
                out.println("</div></div></td>");       // end of column (day)

                col++;
                day++;

                if (col == 7) {
                    col = 0;                             // start new week
                    out.println("    </tr>");
                }

            } // end if while loop for days in month

            // finish off the week if nessesary
            if (col != 0) {      // if not at the start

                while (col != 0 && col < 7) {      // finish off this row if not at the end

                    out.println("      <td class=\"empty\">&nbsp;</td>");
                    col++;
                }
                out.println("    </tr>");
            }

            //
            // end of calendar row
            //
            out.println("</tbody></table></div>");

            today = 1;       // ready for next month
            month++;

            if (month > 12) {     // if end of year
                year++;
                month = 1;
            }

        } // end for month loop

        //
        //  Lesson Time Form
        //
        out.println("  <form name=\"LtimeForm\" action=\"Member_lesson\" method=\"post\">"); // target=\"_top\"
        out.println("    <input type=\"hidden\" name=\"proid\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"calDate\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"date\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"time\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"day\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"ltype\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"lesson_id\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"activity_id\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"reqtime\" value=\"yes\">");       // indicate a request
        out.println("    <input type=\"hidden\" name=\"index\" value=999>");             // indicate from teelist
        out.println("  </form>");

        //
        //  Lesson Group Form
        //
        out.println("  <form name=\"LgroupForm\" action=\"Member_lesson\" method=\"post\">"); //  target=\"bot\"
        out.println("    <input type=\"hidden\" name=\"proid\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"date\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"lgname\" value=\"\">");       // name of group lesson
        out.println("    <input type=\"hidden\" name=\"lesson_id\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"activity_id\" value=\"\">");
        out.println("    <input type=\"hidden\" name=\"groupLesson\" value=\"yes\">");
        out.println("    <input type=\"hidden\" name=\"index\" value=999>");             // indicate from teelist
        out.println("  </form>");

        //
        //  End of HTML page
        //
        Common_skin.outputPageEnd(club, sess_activity_id, out, req);

        out.close();

    }   // end of doGet

    private String buildItemDescription(String name, String time, boolean rwd) {

        if (time.equals("")) {
            
            return (name);   
            
        } else {
                
            if (rwd) {

                return (name + " at <b>" + time + "</b>");    // put name before time on mobile devices (much narrower space)
            
            } else {

                return ("<b>" + time + "</b>" + ": " + name);
            }
        }

    }

    private String iCalLink(int id, String type) {

        return "<a class=\"ical_button\" href=\"data_loader?ical&id=" + id + "&id_type=" + type + "\" title=\"download iCal\"><b></b></a>";

    }
}
