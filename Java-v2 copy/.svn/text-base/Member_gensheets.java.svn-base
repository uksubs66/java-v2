/*******************************************************************************
 *
 *   Member_gensheets:  This servlet will display the time sheets for an activity
 *
 *
 *   called by:  various servlets and menus
 *
 *
 *   created: 1/21/2009   Paul
 *
 *
 *   last updated:
 *
 *         3/07/14  Denver CC (denvercc) - Updated day-off cutoff custom to not apply to the new Golf Simulator activity (activity_id 16) (case 2183).
 *         2/04/14  Winged Foot GC (wingedfoot) - Updated detailed view to make the time selection drop-downs have different options based on the day of the week (case 2363).
 *        11/01/13  Lakewood Ranch CC (lakewoodranch) - Only display time slots every 1.5 hours from 8:30am through 1:00pm (case 2318).
 *        10/22/13  Quechee Club Tennis (quecheeclubtennis) - Updated day-of restriction custom to only apply to the Tennis activity (case 2010).
 *         8/06/13  Added additional processing to showRest for FlxRez which takes locations into account to properly display legend items when sub-activities are used.
 *         3/20/13  Updated getRestData() so that it will continue to search for an applicable, non-default color to display even if it's found a restriction that blocks the member.
 *         2/04/13  Added a 2nd calendar so members can easily access 2 months (like golf).
 *         1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        12/03/12  Governors Club (governorsclub) - Added custom to hide specific time slots from the summary and detailed views for members only.
 *        10/31/12  If no children were found for this activity, display a graceful message instead of attempting to load time sheets that don't exist.
 *         9/06/12  Updated outputTopNav calls to also pass the HttpServletRequest object.
 *         8/31/12  Denver CC (denvercc) - Cut off member access to the time sheets at 12:00PM club time on the day of (case 2183).
 *         6/19/12  Belle Meade CC (bellemeadecc) - Updated custom to hide a different set of times, and to have a different set of times on Sundays as well (case ?).
 *         6/07/12  New summary view now has fixed width columns. Header column will span vertically to display full court (activity) name.
 *         6/06/12  Belle Meade CC (bellemeadecc) - Added custom to hide specific time slots from the new summary view (case ?).
 *         6/06/12  Commented out the defaulting of layout_mode to 3 if layout_mode is 0. Was causing issues for clubs using sub-activities since we need the layout_mode to be 0 until they select a sub-activity.
 *         5/16/12  Fort Collins CC (fortcollins) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *         5/16/12  Sierra View CC (sierraviewcc) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage.
 *         5/04/12  Hovering over the time slot for a clinic on the summary view will now display the lesson pro and name of the clinic.
 *         5/04/12  Fixed issue where the bgcolor for summary view time slots wasn't getting reset in non-accessible time slots and colors were getting applied oddly.
 *         4/18/12  The Peninsula Club (peninsula) - Removed custom to cut off member access on the day of.
 *         4/11/12  Fixed issue that was preventing FlxRez clubs with multiple sub-activities from being able to access the time sheets.
 *         4/03/12  Updated member notice display box so that it is centered on the page.
 *         4/03/12  Ballantyne CC (ballantyne) - Added custom so "Ball Machine" lingo will be used in place of Lesson verbiage (case ?).
 *         4/03/12  Updated FlxRez time sheets to use SystemLingo entries for Lesson verbiage (for custom verbiage such as "Ball Machine").
 *         2/22/12  Set no-cache, use "please wait" dialog on new skin when linking to slot page.
 *         1/19/12  Updates for new skin
 *        12/22/11  Updated the summary view so that the black color for blocked times has a higher display priority than the white unavailable times.
 *        12/04/11  Updated the summary view to display a second table of activity_ids if they have over a certain number of activity sheets (cols_per_row in activities table).
 *        11/04/11  Updated the summary view to properly block out members from accessing times ahead of their days/time in advance settings.
 *         9/21/11  Added new summary view (labeled 'summary view') and renamed old summary view to 'Old Summary View'
 *         8/10/11  Quechee Club Tennis (quecheeclubtennis) - Custom to restrict member access to the time sheets on the day of (case 2010).
 *         5/10/11  Changed how sub-activities are selected and navigated between to be less cumbersome. No visible change for clubs with no sub-activities.
 *         3/17/11  Applied numerous changes to how restriction suspensions were being gathered and displayed so they now function properly.
 *         3/09/11  Pull allowable view modes from mem_allowable_views field in activities table instead of the allowable_views field.
 *         2/23/11  Layout modes 2 (old summary view) will now use the first number of the consec_pro_csv string for the consec value, if populated.
 *         2/22/11  Switched disallow_joins processing to look at the values for each individual sub-activity instead of the disallow_joins value of the parent activity.
 *         2/18/11  Updated noJoins custom, replaced noJoins with disallow_joins boolean, and replaced specific club names with a database query of the activities table to see if disallow_joins = 1 instead
 *         2/18/11  The Peninsula Club (peninsula) - custom to prevent members from joining an existing reservation that they aren't a part of.
 *         2/17/11  Converted all references to disallow_joins over to the new force_singles field.
 *         2/10/11  Governors Club (governorsclub) - custom to prevent members from joining an existing reservation that they aren't a part of.
 *         1/25/11  Elmcrest CC (elmcrestcc) - custom to prevent members from joining an existing reservation that they aren't a part of.
 *        12/23/10  Edina CC (edina) - custom to prevent members from joining an existing reservation that they aren't a part of.
 *        10/06/10  Lakewood CC (lakewoodcc) - Do not allow members to enter/join existing reservations unless they are already a part of the time.
 *         8/13/10  Peninsula - default summary view to 4 consec times
 *         8/11/10  The Quechee Club - custom to prevent members from joining an existing reservation that they are not part of (case 1875).
 *         8/10/10  The Peninsula Club (peninsula) - Restrict members from accessing time slots on the day of **remove later and replace with full fledged option for cutoff date/time**
 *         7/12/10  Changed how consecutive options are read from database and displayed.  Now defined by a custom csv of ints instead of a max consec value.
 *         4/06/10  Added sort_by field to the order by clause of all database queries pulling activity names
 *         3/29/10  Added Tabbed View functionality.
 *         3/27/10  Numberous fixes regarding displaying restrictions, events, lessons in each view
 *         3/11/10  Added custom for pattersonclub - overide the configured consec options if here for paddle sports (REMOVED 3-27-10)
 *                  the default is to pass consec=2 for singles and consec=3 for doubles
 *         1/14/10  Added support for booking consecutive times
 *        12/21/09  Implemented new activity selection process - no longer using parent_id
 *        12/09/09  When looking for events only check those that are active.
 *        11/04/09  Use DaysAdv for all the days in advance info - set by SystemUtils.daysInAdv.
 *        10/22/09  Added row highlighting to the time sheet
 *
 *
 *   notes:
 *
 *      DEFINITIONS:
 *
 *      Activity        - Very generic - could refer to anything from a root level activity down to a 'court' level activity.
 *      Root Activity   - The top most level for an activity - this value is stored in the session (sess_activity_id) and indicates which type of sport they are viewing and how foretees will function. (Logins, permissions, events, lesson books, etc are all based off of the root id.
 *      Sub Activity    - Essentially the same thing as a Parent Activity.  Specifically it is an activity beneath a Root Activity that has children associated to it.  I use this term when referring "down" the tree to another Parent Activity that has more of a child relationship. Could call this Child Activity too...
 *      Parent Activity - Every activity will have a parent, and only one parent.  A parent of zero indicates the activity is a Root Activity.
 *      Barren Activity - An activity that a time sheet built for it.  These are the only activities that can have a time sheet built for it.  A Barren Activity will not have any Sub Activities child activities associated to it.
 *
 *
 *
 *      group_id - the id we need for displaying time sheets.  This could be any activity id from root down to last one before the time sheets.  This will be an activity that has no Sub Activites associated with it, only Barren Activites (activities with time sheets).
 *
 *      each time the page loads look at the incoming activity_id, if it isn't there then set it to the sess_activity_id
 *      display the activity_id name and each of it's parents (if any)  Tennis (root) -> Indoor  > Court 1  (make a new getActivity method for this that will display the same text but make them clickable links so you can drill back)
 *      check to see if the activity_id has any children, if so create a selectbox with the children and call it activity_id
 *      since an activity with children can't have timesheets, the user will drill down until they come to a 'barren' activity.
 *      Once the user has selected an activity that has no sub activites, we're done - we found the time sheets.  Set group_id = activity_id.
 *
 *
 ******************************************************************************/



import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*;

import com.foretees.common.DaysAdv;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyLesson;
//import com.foretees.common.verifyCustom;
import com.foretees.common.getClub;
import com.foretees.common.parmClub;
import com.foretees.common.Utilities;
import com.foretees.common.BigDate;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.getActivity;
import com.foretees.client.SystemLingo;


public class Member_gensheets extends HttpServlet {

 final String rev = SystemUtils.REVLEVEL;
 final String [] dayShort_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
 final String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
 final boolean g_debug = false;
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so all buttons are properly displayed, etc.
    //
    resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    //PreparedStatement pstmt3 = null;
    PreparedStatement pstmt7b = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    //ResultSet rs3 = null;

    HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

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

    String club = (String)session.getAttribute("club");             // get club name
    String user = (String)session.getAttribute("user");             // get user name
    String caller = (String)session.getAttribute("caller");
    String mship = (String)session.getAttribute("mship");           // get member's mship type
    String mtype = (String)session.getAttribute("mtype");           // get member's mtype

    boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    String clubName = Utilities.getClubName(con, true);             // get the full name of this club
    
    //
    //  First, check for Event call - user clicked on an event in the Legend
    //
    if (req.getParameter("event") != null) {

        String eventName = req.getParameter("event");

        displayEvent(eventName, club, out, con);             // display the information
        return;
    }


    boolean sandbox = true; // ( Common_Server.SERVER_ID == 4 ); // && (club.startsWith("demo") || club.equals("admiralscove2"))


    int max = 0;                            // max days member can view sheets
  //int days = 0;                           // max days member can edit times for this day of the week
    int index = 0;                          // # of days from today (for date selected)
    int group_id = 0;                       // will contain the activity_id of the parent for the time sheets we are displaying
    int activity_id = 0;
    int last_tab = 0;                                               // passed back form slot
    int last_tab_index = 0;
    int layout_mode = 0;

    String sid = req.getParameter("group_id");
    try {
        group_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("activity_id");
    try {
        activity_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("last_tab");
    try {
        last_tab = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("last_tab_index");
    try {
        last_tab_index = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("layout_mode");
    try {
        layout_mode = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    if (activity_id == 0) activity_id = sess_activity_id;

    // debug
    if (g_debug) out.println("<!-- last_tab=" + last_tab + ", group_id=" + group_id + ", activity_id=" + activity_id + ", sess_activity_id=" + sess_activity_id + " -->"); // , parent_id=" + parent_id + "
   
    // setup our custom sytem text veriables
    SystemLingo sysLingo = new SystemLingo();
    
    if (club.equals("ballantyne") || club.equals("sierraviewcc") || club.equals("fortcollins")) {
        sysLingo.setLessonBookLingo("Ball Machine");
    } else {
        sysLingo.setLessonBookLingo("default");
    }

    String calDate = (req.getParameter("calDate") != null) ? req.getParameter("calDate") : "";
    String dayShort_name = "";
    String day_name = "";
    String num = "";
    int month = 0;
    int day = 0;
    int year = 0;
    int date = 0;
    int day_num = 0;
    int view_count = 0;

    int [] advdays = new int [7];
    int [] advtimes = new int [7];

    boolean is_today = false;
    boolean allow = true;
    boolean restrictAll = false;
    boolean tooEarly = false;

    String allowable_views = "";

    boolean allow_tabbed = false;
    boolean allow_summary = false;
    boolean allow_old_summary = false;
    boolean allow_detail = false;

    //
    //  parm block to hold the member restrictions for this date and member
    //
    parmRest parmr = new parmRest();          // allocate a parm block for restrictions

    //
    // Get the club parms
    //
    parmClub parm = new parmClub(sess_activity_id, con);

    try {

        getClub.getParms(con, parm, sess_activity_id);        // get the club parms

    } catch (Exception exc) {

        out.println(SystemUtils.HeadTitle("System Error"));
        out.println("<BODY bgcolor=\"ccccaa\"><CENTER>");
        out.println("<BR><BR><H2>System Error</H2>");
        out.println("<BR><BR>Sorry, we encountered a system problem.");
        out.println("<BR>Please try again later.");
        out.println("<BR><br>Exception (Member_gensheets #1): " + exc.getMessage());
        out.println("<BR><BR>If problem persists, please contact your club and provide this message.");
        out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;

    }

    int curr_time = Utilities.getTime(con);

    //
    //  Get today's date
    //
    int today = (int)Utilities.getDate(con);     // get today's date adjusted for time zone

    int yy = today / 10000;
    int mm = (today - (yy * 10000)) / 100;
    int dd = today - ((yy * 10000) + (mm * 100));

    Map<String, Object> date_map = new LinkedHashMap<String, Object>();
    date_map.put("year", yy);
    date_map.put("month", mm - 1);
    date_map.put("dayOfMonth", dd);

    Map<String, Object> date_map2 = new LinkedHashMap<String, Object>();   // date map for 2nd calendar
    if (mm < 12) {
      date_map2.put("year", yy);
      date_map2.put("month", mm);   // next month
    } else {        
      date_map2.put("year", yy + 1);
      date_map2.put("month", 0);     // Jan of next year
    }
    date_map2.put("dayOfMonth", 1);

    Calendar cal = new GregorianCalendar();       // get a Calendar for calDate processing

    if (!calDate.equals("") && calDate.indexOf("/") > 0) {

        //
        //  Convert the calDate value from string (mm/dd/yyyy) to ints (month, day, year)
        //
        try {

            StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

            num = tok.nextToken();                    // get the mm value
            month = Integer.parseInt(num);
            num = tok.nextToken();                    // get the dd value
            day = Integer.parseInt(num);
            num = tok.nextToken();                    // get the yyyy value
            year = Integer.parseInt(num);

        } catch (Exception exc) { out.println("ERROR: " + exc.toString() + " calDate="+calDate); }

        //
        //  set the requested date to get the day name, etc.
        //
        cal.set(Calendar.YEAR, year);                 // change to requested date
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        day_num = cal.get(Calendar.DAY_OF_WEEK);        // get day of week (01 - 07)

        dayShort_name = dayShort_table[day_num];        // get short name for day
        day_name = day_table[day_num];                  // get full name for day
        date = (year * 10000) + (month * 100) + day;

        //out.println("<!-- date=" + date + " (parsed from calDate of " + calDate + ")-->");

    } else {

        calDate = (req.getParameter("date") != null) ? req.getParameter("date") : "";

        if ( calDate.equals("") ) {

            date = today;
            is_today = true;

        } else {

            try {
                date = Integer.parseInt(calDate);
            } catch (NumberFormatException ignore) {}

        }
        year = date / 10000;
        int temp = year * 10000;
        month = date - temp;
        temp = month / 100;
        temp = temp * 100;
        day = month - temp;
        month = month / 100;

        cal.set(Calendar.YEAR, year);                 // change to requested date
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        day_num = cal.get(Calendar.DAY_OF_WEEK);        // get day of week (01 - 07)

        dayShort_name = dayShort_table[day_num];        // get short name for day
        day_name = day_table[day_num];                  // get full name for day

        //out.println("<!-- date=" + date + " (retrieved from calDate of " + calDate + ")-->");

    }

    if (!is_today) is_today = (date == today);


    //
    // Calculate the number of days between today and the date requested (=> ind)
    //
    BigDate todaydate = new BigDate(yy, mm, dd);            // get today's date
    BigDate thisdate = new BigDate(year, month, day);       // get requested date

    index = (thisdate.getOrdinal() - todaydate.getOrdinal());   // number of days between


    //
    // Setup the daysArray
    //
    //   This will contain an indicator for each day fo the next 365 days as to whether this user can
    //   access the sheet for this date.  These are adjusted for the time of the day and the club's time zone!
    //
    DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
    daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, user, sess_activity_id, con);

    max = daysArray.maxview;                     // get days in advance this member can view sheets


    //  use the member's mship type to determine which days & time in advance
    verifySlot.getDaysInAdv(con, parm, mship, sess_activity_id);

    advdays[0] = parm.advdays1;
    advdays[1] = parm.advdays2;
    advdays[2] = parm.advdays3;
    advdays[3] = parm.advdays4;
    advdays[4] = parm.advdays5;
    advdays[5] = parm.advdays6;
    advdays[6] = parm.advdays7;

    advtimes[0] = parm.advtime1;
    advtimes[1] = parm.advtime2;
    advtimes[2] = parm.advtime3;
    advtimes[3] = parm.advtime4;
    advtimes[4] = parm.advtime5;
    advtimes[5] = parm.advtime6;
    advtimes[6] = parm.advtime7;

    //
    //   Determine if member can access times on this day
    //
    if (daysArray.days[index] != 1) {      // if this day is beyond the days in advance for this member (set in SystemUtils.daysInAdv)

       restrictAll = true;

    }

    if (advdays[day_num - 1] == index) {
        if (curr_time < advtimes[day_num - 1]) {

            restrictAll = true;         // indicate no member access
            tooEarly = true;
        }
    }

    if (((club.equals("quecheeclubtennis") && activity_id == 1) || (club.equals("denvercc") && activity_id != 16 && curr_time >= 1200))&& index == 0) {
        restrictAll = true;
    }

    //out.println("<br>curr_time=" + curr_time);
    //out.println("<br>advtimes[" + (day_num - 1) + "]=" + advtimes[day_num - 1]);
    //out.println("<br>advdays[" + (day_num - 1) + "]=" + advdays[day_num - 1]);

    //
    //  Get all restrictions for this day and user (for use when checking each time below)
    //
    parmr.user = user;
    parmr.mship = mship;
    parmr.mtype = mtype;
    parmr.date = date;
    parmr.day = day_name;
    parmr.course = "";
    parmr.activity_id = sess_activity_id;     // use Root id for now

    try {

       getRests.getAll(con, parmr);              // get the restrictions

    } catch (Exception exc) {

        Utilities.logError("Member_gensheets: getRests failed. user=" + user + ", mship=" + mship + ", mtype=" + mtype + ", date=" + date + ", day_name=" + day_name + ", activity_id=" + sess_activity_id + ", err=" + exc.toString());

    }

    int rcount = 0;
    int ind = 0;
    while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {

        rcount++;
        ind++;
    }

    
    
    if (!new_skin) {
    
        out.println(SystemUtils.HeadTitle("Member Time Sheets"));

        // include files for dynamic calendars
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");

        // include files for tabber
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/tabber.css\">");
        out.println("<script language=\"javascript\" src=\"/" +rev+ "/tabber.js\"></script>");

        out.println("<style>");
        out.println("body { text-align: center; }");
        //out.println(".playerTD {width:125px}");
        out.println("</style>");

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        
        out.println("function gotoTimeSlot(slot_id, group_id, date, joins, layout, consec) {");
        out.println(" top.document.location.href=\"Member_activity_slot?slot_id=\"+slot_id+\"&group_id=\"+group_id+\"&date=\"+date+\"&layout_mode=\"+layout+\"&force_singles=\"+joins+\"&consec=\"+consec+\"\"");
        out.println("}");    

        out.println("function hiLite(i) {");
        out.println(" document.getElementById('row'+i).className='timesheetTD2over';");
        out.println("}");

        out.println("function hiLiteOff(i) {");
        out.println(" document.getElementById('row'+i).className='timesheetTD2';");
        out.println("}");

        out.println("function showInfo() {");
        out.println(" var w = window.open('/v5/member_newclock.htm','clockPopup','width=550,height=370,scollbars=0,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
        out.println(" w.creator = self;");
        out.println("}");

        out.println("function reloadPageActId(activity_id) {");
        out.println(" var f = document.forms['frmLoadDay'];");
        out.println(" f.activity_id.value = activity_id;");
        out.println(" f.submit();");
        out.println("}");

        out.println("// -->");
        out.println("</script>");

        out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#336633\" vlink=\"#8B8970\" alink=\"#8B8970\">");

        SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
    
    } else {

        //
        //  New skin
        //
        Common_skin.outputHeader(club, sess_activity_id, "Member Time Sheet", false, out, req);
        // include files for tabber
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/tabber.css\">");
        out.println("<script language=\"javascript\" src=\"/" +rev+ "/tabber.js\"></script>");
        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function gotoTimeSlot(slot_id, group_id, date, joins, layout, consec) {");
        out.println(" $('body').foreTeesModal('pleaseWait',\"open\",true);");
        out.println(" document.location.href=\"Member_activity_slot?slot_id=\"+slot_id+\"&group_id=\"+group_id+\"&date=\"+date+\"&layout_mode=\"+layout+\"&force_singles=\"+joins+\"&consec=\"+consec+\"\"");
        out.println("}");

        out.println("function reloadPageActId(activity_id) {");
        out.println(" var f = document.forms['frmLoadDay'];");
        out.println(" f.activity_id.value = activity_id;");
        out.println(" f.submit();");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("</head>");
        Common_skin.outputBody(club, sess_activity_id, out, req);
        Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
        Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
        Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
        Common_skin.outputPageStart(club, sess_activity_id, out, req);
        Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Select a " + getActivity.getActivityCommonName(sess_activity_id, con) + " Time", req);
        Common_skin.outputLogo(club, sess_activity_id, out, req);


        // Start Calendar / Instructions container
        out.println("<div class=\"member_sheet tabular_container\">");
        out.println("<div class=\"tabular_row\">");
        

        // Group the data we want to send to javascript in a hash map
        Map<String, Object> calendar_map = new LinkedHashMap<String, Object>();
        calendar_map.put("daysArray", daysArray);
        calendar_map.put("cal", date_map);
        calendar_map.put("max", max);
        calendar_map.put("IS_TLT", false);

        // Ecode the data with json
        Gson gson_obj = new Gson();
        String jsonHashMap = gson_obj.toJson(calendar_map);
        
        
        //  add another calendar  

        calendar_map.put("cal", date_map2);

        // Ecode the data with json
        String jsonHashMap2 = gson_obj.toJson(calendar_map);
        
        
        out.println("<div class=\"tabular_cell member_sheet_left\">");
        
        out.println("<div id=\"member_sheet_calendar\" class=\"calendar flxsheet primary\" " +
                "data-ftjson=\"" + StringEscapeUtils.escapeHtml(jsonHashMap) + "\"></div>");
            //    "data-ftdefaultdate=\"" + month + "/" + day + "/" + year + "\"></div>");
        out.println("</div>");
              
       out.println("<div class=\"tabular_cell\">");
         
        out.println("<div id=\"member_sheet_calendar\" class=\"calendar flxsheet secondary\" " +
                "data-ftjson=\"" + StringEscapeUtils.escapeHtml(jsonHashMap) + "\"></div>");
             //   "data-ftdefaultdate=\"3/01/2013\"></div>");
      
        out.println("</div>");

        //**********************************************************
        //  Continue with instructions and tee sheet
        //**********************************************************


        // Start Instructions div
        out.println("<div class=\"tabular_cell member_sheet_right\">");
        out.println("<div class=\"sub_instructions\">");

        out.println("<h3>Instructions:</h3>");

            out.println("<p>Use the time sheet below to select the time and location you'd like to reserve.</p>");
            out.println("<p>To display a different day's time sheet, select the date from the calendar to the left.</p>");
            out.println("<p>Special Events and Restrictions, if any are colored arrording to the legend below.</p>");


        //  End Instructions div
        out.println("</div>");
        out.println("</div>");
        // Float fix
        
        // End Calendar/ Instructions container
        out.println("</div>");
        out.println("</div>");
        
        // Start Date / Course
        out.println("<div class=\"main_instructions date_course\">");
        out.println(" <span>Displaying <b>" + getActivity.getActivityName(activity_id, con) + "</b> Time Sheets for <b>" + day_name + " " + month + "/" + day + "/" + year + "</b></span>");
        out.println("</div>");
        // End date / Course

        // Start Tee Sheet Legend
        /*
        out.println("<div class=\"tee_sheet_legend main_instructions\">");

        out.println("<h2>Tee Sheet Legend:</h2>");

        out.println("</div>");
         * 
         */
        // End Tee Sheet Legend
        
    }
    
    
    
    
    
    if (g_debug) out.println("<!-- FOUND " + rcount + " RESTRICTIONS FOR user=" + user + ", mship=" + mship + ", mtype=" + mtype + ", date=" + date + ", day_name=" + day_name + ", activity_id=" + sess_activity_id + " -->");



    //
    // BEGIN OLD LEGACY CODE
    //

    /*
    int child_count = 0;
    int has_parent = 0;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT parent_id " +
                "FROM activities " +
                "WHERE activity_id = ? AND parent_id <> 0");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) has_parent = 1;

    } catch (Exception exc) {

            out.println("<p>Error getting parent_count:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    child_count = getActivity.getSubActivityCount(activity_id, con);

    if (child_count > 0 || has_parent > 0) {

        String result = "";

        ArrayList<Integer> array = new ArrayList<Integer>();

        try { array = getActivity.getAllParentsForActivity(activity_id, con); }
        catch (Exception exc) { Utilities.logError("Error in Member_gensheets building path for activity id#" + activity_id + " ERR=" + exc.toString()); }

//        if (array.size() == 1) {

//            out.println("<!-- ONLY ONE CHILD ACTIVITY FOUND FOR "  + getActivity.getActivityName(activity_id, con) + " -->");

//            out.println("<br><b><font size=6 color=#336633>" + getActivity.getActivityName(activity_id, con) + "</font></b>");

//        } else {

            for (int i = 0; i < array.size(); i++) {
                //if (i == array.size() - 1) {
                    //result = getActivity.getActivityName(array.get(i), con) + result;
                //} else {
                    result = "<a href=\"javascript:void(" + array.get(i) + ")\" onclick=\"document.forms['frmLoadDay'].activity_id.value='" + array.get(i) + "';document.forms['frmLoadDay'].last_tab_index.value='';document.forms['frmLoadDay'].last_tab.value='';document.forms['frmLoadDay'].submit()\">" + getActivity.getActivityName(array.get(i), con) + "</a> > " + result;
                //}
            }

            if (result.endsWith(" > ") && activity_id != sess_activity_id) result = result.substring(0, result.length() - 3);

            out.println("<br>");
            out.println(result);
//        }

    }


    // If there are no 'sub activites' defined for this parent activity then set group_id = parent_id
    if ( child_count == 0 ) {

        group_id = activity_id;

        out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

        out.println("<!-- NO CHILD ACTIVITY FOUND FOR "  + getActivity.getActivityName(activity_id, con) + " -->");

        out.println("<br><b><font size=6 color=#336633>" + getActivity.getActivityName(activity_id, con) + "</font></b>");

    } else {


        //
        // DISPLAY CHILD ACTIVITES
        //
        out.println("<select name=activity_id onchange=\"document.forms['frmLoadDay'].last_tab_index.value='';document.forms['frmLoadDay'].last_tab.value='';document.forms['frmLoadDay'].submit()\">");

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                                    "SELECT activity_id, activity_name " +
                                    "FROM activities " +
                                    "WHERE parent_id = '" + activity_id + "' AND activity_id IN (SELECT parent_id FROM activities)");

            out.println("<option value=\"0\">CHOOSE...</option>");

            while (rs.next()) {

                Common_Config.buildOption(rs.getInt("activity_id"), rs.getString("activity_name"), -1, out);

            }

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        out.println("</select>");



        //
        // NOW LET'S CHECK TO SEE IF THIS ACTIVITY HAS ANY BARREN CHILDERN ACTIVITES ASSOCIATED WITH IT
        //
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT activity_id, activity_name " +
                    "FROM activities " +
                    "WHERE parent_id = '" + activity_id + "' AND activity_id NOT IN (SELECT parent_id FROM activities)");

            if (rs.next()) {

                group_id = activity_id;

                out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

                out.println("<!-- FOUND AT LEAST ONE BARREN ACTIVITY FOUND FOR "  + getActivity.getActivityName(activity_id, con) + " -->");

                out.println("<br><b><font size=6 color=#336633>" + getActivity.getActivityName(activity_id, con) + "</font></b>");

            }

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }


    } // end if no sub activites
    */
    
    //
    // END OLD LEGACY CODE
    //




    //
    // Display Full Activity Tree (only if subactivities found)
    //
    if (getActivity.getSubActivityCount(sess_activity_id, con) > 0) {

        getActivity.buildActivityTree(activity_id, 0, out, con);
    }


    // If the current activity_id has has no child sub activities (only barren children), set group_id = activity_id so time sheets are shown
    if (getActivity.getSubActivityCount(activity_id, con) == 0) {

        group_id = activity_id;
    }

    out.println("<br><br>");


    //
    // SEE WHICH VIEWS ARE ALLOWED BY THE CONFIGURATION FOR THIS ACTIVITY
    //
    try {

        pstmt = con.prepareStatement ("" +
                "SELECT mem_allowable_views " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, group_id); // view are based off of the 'parent' activity, not the 'court' level
        rs = pstmt.executeQuery();

        if ( rs.next() ) allowable_views = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("Error in Proshop_gensheets looking up allowable views for activity_id " + group_id + ". Exc=" + exc.getMessage() );

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

    if (g_debug) out.println("<!-- ALLOWABLE VIEWS FOR activity_id " + group_id + " ARE " + allowable_views + " -->");

    if ( allowable_views.indexOf("1") != -1 ) {
        allow_tabbed = true;
        view_count++;       // Tally available views so we know later if more than one is available.
    }
    if ( allowable_views.indexOf("2") != -1 ) {
        allow_summary = true;
        allow_old_summary = true;
        view_count += 2;
    }
    if ( allowable_views.indexOf("3") != -1 ) {
        allow_detail = true;
        view_count++;
    }
    if ( allowable_views.indexOf("4") != -1 ) {
        allow_summary = true;
        view_count++;
    }

    //
    // SET THE DEFAULT LAYOUT MODE FOR THIS ACTIVITY
    //
    if (layout_mode == 0) {

        //layout_mode = 3; // defaut if integer conversion fails  **** This was causing issues for clubs with sub-activities, since the layout_mode needs to be 0 until they select a sub-activity. ****
        try { layout_mode = Integer.parseInt(allowable_views.substring(0, 1)); }
        catch (Exception ignore) {}
       
        if (g_debug) out.println("<!-- USING DEFAULT VIEW OF " + layout_mode + " -->");

    }



    //
    // START THE FORM
    //

    // this is the form that gets submitted when the user selects a day from the calendar
    out.println("<form action=\"Member_gensheets\" method=\"get\" name=\"frmLoadDay\" " + ((!new_skin) ? " target=\"bot\"" : "") + ">");

    out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");
    out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
    out.println("<input type=\"hidden\" name=\"last_tab\" value=\"" + last_tab + "\">");
    out.println("<input type=\"hidden\" name=\"last_tab_index\" value=\"" + last_tab_index + "\">");
    out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + layout_mode + "\">"); // default

    // CLOSE THE FORM
    out.println("</form>");




    if (!new_skin) {

        //
        // Display Calendars
        //
        out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

        out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td>");

        out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

        out.println("</td>\n<tr>\n</table>");

        Calendar cal_date = new GregorianCalendar(yy, mm-1, dd);     // get today's date adjusted for time zone (above)
        int cal_year = cal_date.get(Calendar.YEAR);
        int cal_month = cal_date.get(Calendar.MONTH) + 1;     // month is zero based
        int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

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
        cal_year = cal_date.get(Calendar.YEAR);
        cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based

        out.println("g_cal_month[1] = " + cal_month + ";");
        out.println("g_cal_year[1] = " + cal_year + ";");
        out.println("g_cal_beginning_month[1] = " + cal_month + ";");
        out.println("g_cal_beginning_year[1] = " + cal_year + ";");
        out.println("g_cal_beginning_day[1] = 0;");

       // cal_date = new GregorianCalendar(year, month-1, day);
        cal_date = new GregorianCalendar(yy, mm-1, dd);            // reset calendar to today (adjusted for time zone)

        cal_date.add(Calendar.DAY_OF_MONTH, max);                  // add the days in advance
        cal_year = cal_date.get(Calendar.YEAR);
        cal_month = cal_date.get(Calendar.MONTH) + 1;             // month is zero based
        cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

        out.println("g_cal_ending_month[1] = " + cal_month + ";");
        out.println("g_cal_ending_day[1] = " + cal_day + ";");
        out.println("g_cal_ending_year[1] = " + cal_year + ";");

        out.print("var daysArray = new Array(");
        int js_index = 0;
        for (js_index = 0; js_index <= max; js_index++) {
           out.print(daysArray.days[js_index]);
           if (js_index != max) out.print(",");
        }
        out.println(");");

        out.println("var max = " + max + ";");

        out.println("</script>");

        out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
        out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");

    }



    if (!getActivity.isChildlessActivity(sess_activity_id, con)) {

        //
        // ONLY DISPLAY THE TIME SHEETS IF THE GROUP ID IS SET
        //
        if (group_id > 0) {


            //
            // DISPLAY THE DATE OF THE TIME SHEET
            //
            if (date == 0) {

                out.println("<p align=center><b>Select a date.</b></p>");

            } else {

                if (!new_skin) out.println("<p align=center><b>" + dayShort_name + " " + month + "/" + day + "/" + year + " Time Sheet</b></p>");

            }

            //
            // IF SHEET IS LOCKED BECAUSE WE'RE HERE TOO EARLY THEN DISPLAY THE CLOCK
            //
            if (restrictAll && tooEarly) {

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

                //  add iframe to display the server clock
                out.println("The time sheet will be unlocked at " + SystemUtils.getSimpleTime(advtimes[day_num - 1]) + ".&nbsp;&nbsp;");
                out.println("The Server Time is:&nbsp;");
                out.println("<iframe src=\"clock?club=" + club + "&bold\" id=ifClock style=\"width:80px;height:16px;position:relative;top:" + offset + "px\" scrolling=no frameborder=no></iframe>");

                //  help link to describe the server clock
                out.print("<font size=2>&nbsp;");
                out.print("<a href=\"javascript:void(0)\" onclick=\"document.forms['frmLoadDay'].submit()\" title=\"Refresh\" alt=\"Refresh Calendars\">Refresh Page</a>");
                out.print(" &nbsp;&nbsp; ");
                out.print("(<a href=\"javascript:void(0);\" onclick=\"showInfo(); return false;\">How is this clock used?</a>)");
                out.println("</font>");

            }


            //
            // DISPLAY ANY EVENTS FOUND FOR THIS DAY
            //
            try {

                boolean found = false;

                pstmt = con.prepareStatement("" +
                        "SELECT event_id, name, color " +
                        "FROM events2b " +
                        "WHERE " +
                            "activity_id = ? AND date = ? AND inactive = 0 " +
                        "ORDER BY act_hr, act_min, name");

                pstmt.clearParameters();
                pstmt.setInt(1, sess_activity_id);
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();


                rs.last();

                if (rs.getRow() > 0) {
                    found = true;
                    out.println("<p align=center><b>Today's Events:</b><br>");
                }

                rs.beforeFirst();

                while ( rs.next() ) {

                    //out.println("&nbsp;<button style=\"background-color:" + rs.getString("color") + "\" onclick=\"displayEvent('" + rs.getString("name") + "')\">" + rs.getString("name") + "</button>&nbsp;");

                    out.println("&nbsp;" +
                            "<a href=\"javascript: void(0)\" " +
                            "onclick=\"window.open('Member_gensheets?event=" + rs.getString("name") + "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no');return false;\">" +
                            "<button style=\"background-color:" + rs.getString("color") + "\">" + rs.getString("name") + "" +
                            "</button></a>" +
                            "&nbsp;");

                }

                if (found) out.println("</p>");


                //
                // NOW CHECK FOR RESTRICTIONS
                //
                found = false;

                pstmt7b = con.prepareStatement (
                         "SELECT name, recurr, color, id, stime, etime, locations " +
                         "FROM restriction2 " +
                         "WHERE sdate <= ? AND edate >= ? AND showit = 'Yes' AND activity_id = '" + sess_activity_id + "' " +
                         "ORDER BY stime");

                //
                //  Scan the events, restrictions and lotteries to build the legend
                //
                pstmt7b.clearParameters();          // clear the parms
                pstmt7b.setLong(1, date);
                pstmt7b.setLong(2, date);

                rs = pstmt7b.executeQuery();      // find all matching restrictions, if any

                while (rs.next()) {

                    String rest_recurr = rs.getString("recurr");

                    boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, "", rs.getString("locations"), sess_activity_id, activity_id, con);

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


                            if (found == false) {      // if first one

                                found = true;
                                out.println("<p align=center><b>Today's Restrictions:</b><br>");
                            }

                            out.println("&nbsp;" +
                                        "<button style=\"background-color:" + rs.getString("color") + "\">" + rs.getString("name") + "" +
                                        "</button>" +
                                        "&nbsp;");
                        }

                    } // end if showRest

                } // end of while

                if (found) out.println("</p>");

                //   endof Restrictions !!!!!!!


            } catch (Exception exc) {

                out.println("<p>ERROR CHECKING FOR ACTIVITY EVENTS:" + exc.toString() + "</p>");

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

                try { pstmt7b.close(); }
                catch (Exception ignore) {}

            }



            //
            //**********************************************
            //   Check for Member Notice from Pro
            //**********************************************
            //
            String memNoticeMsg = verifySlot.checkMemNotice(date, 0, 0, 0, "", day_name, "teesheet", true, sess_activity_id, con);

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
                    pstmt = con.prepareStatement(
                          "SELECT mon, tue, wed, thu, fri, sat, sun, message, bgColor " +
                          "FROM mem_notice " +
                          "WHERE sdate <= ? AND edate >= ? AND activity_id = ? AND teesheet=1");

                    pstmt.clearParameters();
                    pstmt.setLong(1, date);
                    pstmt.setLong(2, date);
                    pstmt.setInt(3, sess_activity_id);

                    rs = pstmt.executeQuery();

                    out.println("<br><div style=\"text-align:center;width:705px;margin:auto;\"><table align=\"center\" border=\"2\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"0\" cellspacing=\"0\">");
                    out.println("<tr><td><table border=\"0\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
                    out.println("<tr><td align=\"center\" valign=\"center\"><font size=\"4\"><b>*** Important Notice ***</b></font></td></tr>");

                    while (rs.next()) {

                        notice_mon = rs.getInt("mon");
                        notice_tue = rs.getInt("tue");
                        notice_wed = rs.getInt("wed");
                        notice_thu = rs.getInt("thu");
                        notice_fri = rs.getInt("fri");
                        notice_sat = rs.getInt("sat");
                        notice_sun = rs.getInt("sun");
                        notice_msg = rs.getString("message");
                        notice_bgColor = rs.getString("bgColor");

                        if ((notice_mon == 1 && day_name.equals( "Monday" )) || (notice_tue == 1 && day_name.equals( "Tuesday" )) || (notice_wed == 1 && day_name.equals( "Wednesday" )) ||
                            (notice_thu == 1 && day_name.equals( "Thursday" )) || (notice_fri == 1 && day_name.equals( "Friday" )) || (notice_sat == 1 && day_name.equals( "Saturday" )) ||
                            (notice_sun == 1 && day_name.equals( "Sunday" ))) {

                            out.println("<tr>");
                            if (!notice_bgColor.equals("")) {
                                out.println("<td width=\"700\" bgColor=\"" + notice_bgColor + "\" align=\"center\">");
                            } else {
                                out.println("<td width=\"700\" align=\"center\">");
                            }
                            out.println("<font size=\"2\">" + notice_msg + "</font></td></tr>");
                        }

                    }  // end WHILE loop

                    out.println("</table></td></tr></table></div><br>");

                } catch (Exception e1) {

                    out.println("<p>Error: Unable to display member notice.</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}

                }

            } // end if notice found



            //
            // BLOCK OLD SUMMARY VIEW ON NEW SKIN
            //
            if (new_skin) {

                allow_old_summary = false;
                if (layout_mode == 2) layout_mode = 4;
            }



            //
            // ONLY DISPLAY THE VIEW SELECTION LINKS IF THERE ARE MULTIPLE VIEWS AVAILABLE FOR THIS ACTIVITY
            //
            if (view_count > 1) {

                String view_bar = "";

                out.println("<p align=center><b>");

                if (allow_tabbed)  view_bar += (layout_mode == 1) ? "Tabbed | " : "<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='1';document.forms['frmLoadDay'].submit()\">Tabbed</a> | ";
                if (allow_summary) view_bar += (layout_mode == 4) ? "Summary | " : "<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='4';document.forms['frmLoadDay'].submit()\">Summary</a> | ";
                if (allow_old_summary) view_bar += (layout_mode == 2) ? "Old Summary | " : "<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='2';document.forms['frmLoadDay'].submit()\">Old Summary</a> | ";
                if (allow_detail)  view_bar += (layout_mode == 3) ? "Detailed | " : "<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='3';document.forms['frmLoadDay'].submit()\">Detailed</a>";

                if (view_bar.endsWith(" | ")) view_bar = view_bar.substring(0, view_bar.length() - 3);

                out.println( view_bar );

                out.println("</b></p>");

                if (new_skin) out.println("<br><br>");

            }





            //out.println("<div style=\"width:100%;margin: 0px auto; padding: 0px 5px; border:2px solid black\">");


            //
            // START ACTUAL OUTPUT OF THE TIME SHEETS
            //

            if ( layout_mode == 1 ) {


                //
                // TABBED VIEW
                //

                out.println("<table align=center border=0 style=\"margin: auto\">");        // table to center the tabbed sheets
                out.println("<tr valign=top><td>");

               // out.println("<div align=center style=\"align:center; margin:0px auto;\" id=tabberHolder>"); // width:640px
               //  out.println("<div align=center class=\"tabber\">");

                out.println("<div style=\"align:center; margin:0px auto;\" id=tabberHolder>"); // width:640px
                out.println("<div class=\"tabber\">");

                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT *, activity_name, max_players, t2.disallow_joins, consec_mem, consec_mem_csv, e.name, e.color, r.color AS rcolor, r.name AS rname, " +
                                "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                            "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                            "ORDER BY sort_by, activity_name, date_time");
                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    pstmt.setInt(2, date);
                    rs = pstmt.executeQuery();

                    int players_found = 0;
                    int last_activity_id = 0;
                    boolean tmp_first = true;
                    String tabbertabdefault = "";
                    int tab_index = 0;
                    String result = "";
                    int interval = 0;
                    String color = "";
                    String rname = "";
                    String lname = "";
                    String consec_csv = "";

                    ArrayList<Integer> consec = new ArrayList<Integer>();

                    boolean selectable = false;
                    boolean disallow_joins = false;

                    while (rs.next()) {

                        players_found = 0; // reset
                        selectable = true;
                        disallow_joins = false;
                        rname = "";

                        color = (rs.getString("color") == null) ? "" : rs.getString("color");

                        if (rs.getInt("t2.disallow_joins") == 1) {     // if 1, do not allow members to access existing times that they aren't a part of
                            disallow_joins = true;
                        }

                        allow = true;     // default

                        if (restrictAll == true) allow = false;     // do not allow access to this time if all day is off limits


                        // see if we are switching activities (new tab)
                        if (last_activity_id != rs.getInt("activity_id")) {

                            last_activity_id = rs.getInt("activity_id");
                            if (!tmp_first) {
                                tab_index++;
                                out.println("</table></div>");
                            }
                            if (tmp_first) tmp_first = false;

                            tabbertabdefault = "";
                            if (last_tab == rs.getInt("activity_id") || last_tab_index == tab_index) tabbertabdefault = " tabbertabdefault";
                            out.println("<div class=\"tabbertab" + tabbertabdefault + "\" title=\"" + rs.getString("activity_name") + "\">");

                            out.println("<table class=\"timesheet\">");

                            // header row
                            out.println("<tr class=timesheetTH>");
                            out.println("<td class=headerTD>Time</td>");
                            if (rs.getInt("consec_mem") > 1) out.println("<td class=headerTD>Min.</td>");
                            for (int i = 1; i <= rs.getInt("max_players"); i++) {

                                out.print("<td class=headerTD>Player " + i + "</td>");
                            }

                            out.println("</tr>");

                        } // end if switching activities and starting new tab


                        // hide blocked times
                        if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                            // determine which color we are going to shade this row
                            if (rs.getInt("event_id") != 0 && rs.getString("color") != null) {

                                if (g_debug) out.println("<!-- EVENT FOUND -->");
                                color = rs.getString("color");

                            } else if (rs.getInt("lesson_id") != 0) {

                                if (g_debug) out.println("<!-- LESSON FOUND -->");
                                result = getLessonName(rs.getInt("lesson_id"), con);

                                StringTokenizer tok = new StringTokenizer( result, "|" );

                                lname = tok.nextToken();      // first token is the name
                                color = tok.nextToken();      // second is the color

                            } else if (rs.getInt("rest_id") != 0) {   // if no Event or Blocker

                                try {

                                    // check all restrinctions to see if this time is affected
                                    String restData = getRestData(parmr, rs.getInt("intTime"), rs.getInt("activity_id"), rs.getInt("rest_id"));

                                    if (!restData.equals("")) {      // if something found

                                        if (g_debug) out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                        // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                        StringTokenizer tok = new StringTokenizer( restData, ":" );

                                        color = tok.nextToken();               // get the color for this time
                                        String rest_ind = tok.nextToken();     // get the restricted indicator
                                        rname = tok.nextToken();            // get the name of this restriction

                                        if (color.equals("none")) {            // if no color specified or found

                                            color = "";                         // use default color
                                        }

                                        if (rest_ind.equals("block")) {        // if member restricted

                                            allow = false;                      // do not allow member to access this time
                                        }

                                    } else {       // no restrinctions found

                                        color = "";             // use default color
                                    }

                                } catch (Exception exc) {

                                    out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");
                                }

                            } else {
                                if (g_debug) out.println("<!-- NO EVENT/LESSON/RESTRICTION -->");
                            }

                            // if today and the time has past, then make it not selectable
                            if ( is_today && rs.getInt("intTime") < curr_time ) {

                                selectable = false;
                            }

                            // if this day is beyond days in advance for member, then make it not selectable
                            if ( allow == false ) {

                                selectable = false;
                            }

                            // if in use by someone other than this user
                            if ( selectable && (!rs.getString("in_use_by").equals("") && !rs.getString("in_use_by").equals( user )) ) {

                                selectable = false;
                            }

                            if ( rs.getInt("lesson_id") != 0) {

                                selectable = false;
                            }
        /*
                            // make not selectable if owner specified no else can join AND user is not part of this time slot
                            if ( selectable && (rs.getInt("force_singles") == 1 && rs.getInt("part_of") != 1) ) {

                                selectable = false;
                            }
        */
                            // if this is an event time then make it not selectable
                            if ( selectable && rs.getInt("event_id") != 0 ) {

                                selectable = false;
                            }

                            boolean started_row = false;

                            consec_csv = rs.getString("consec_mem_csv").trim();

                            consec.clear();

                            if (!consec_csv.equals("")) consec = Utilities.buildActivityConsecList(consec_csv);

                            String TR_selectable = "<tr class=\"timesheetTR\"><td align=center><button class=\"btnSlot\" onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + rs.getInt("force_singles") + "," + layout_mode + ", " + (consec.size() > 0 ? consec.get(0) : "0") + ")\">" + rs.getString("time") + "</button></td>";
                            String TR_nonselectable = "<tr class=\"timesheetTR\"" + ((color.equals("")) ? "" : " style=\"background-color:" + color + "\"") + "><td align=center>" + rs.getString("time") + "</td>";

                            if ( !restrictAll && consec.size() > 0) {

                                TR_selectable += "" +
                                        "<td>" +
                                        "<select name=consec size=1 " +
                                        " onchange=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + rs.getInt("force_singles") + "," + layout_mode + ", this.options[this.selectedIndex].value)\">";

                                if (rs.getInt("interval") == rs.getInt("alt_interval") || rs.getInt("alt_interval") == 0) {
                                    interval = rs.getInt("interval");
                                } else {
                                    interval = 1;
                                }

                                /* if (club.equals("pattersonclub") && sess_activity_id == 9) {

                                    // pattersonclub AND paddle courts then display Single & Doubles as 60 & 90 minutes
                                    TR_selectable += "<option value=\"2\">Singles<option value=\"3\">Doubles";

                                } else { */

                                for (int i=0; i < consec.size(); i++) {
                                    TR_selectable += "<option value=\"" + consec.get(i) + "\">" + (consec.get(i) * interval);
                                }
                                /*
                                    // display the minutes using the interval value
                                    for (int i = 1; i <= rs.getInt("consec_mem"); i++) {
                                        TR_selectable += "<option value=\"" + i + "\">" + (i * interval);
                                    }
    */
                                //}

                                TR_selectable += "</select></td>";

                                TR_nonselectable += "<td>&nbsp;</td>";

                            }


                            // print the player columns
                            try {

                                pstmt2 = con.prepareStatement("" +
                                        "SELECT *, (" +
                                            "SELECT COUNT(*) AS players " +
                                            "FROM activity_sheets_players " +
                                            "WHERE activity_sheet_id = ? AND username = ?) AS part_of " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? " +
                                        "ORDER BY pos");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rs.getInt("sheet_id"));
                                pstmt2.setString(2, user);
                                pstmt2.setInt(3, rs.getInt("sheet_id"));
                                rs2 = pstmt2.executeQuery();

                                started_row = false;

                                while ( rs2.next() ) {

                                    if ( !started_row ) {

                                        started_row = true;

                                        // make not selectable if owner specified no else can join AND user is not part of this time slot
                                        if ( selectable && (rs.getInt("force_singles") == 1 && rs2.getInt("part_of") != 1) ) {

                                            selectable = false;
                                        }

                                        // CUSTOM - if club requests this - make slot NOT selectable if user is not part of it
                                        if ( selectable && disallow_joins && rs2.getInt("part_of") != 1 ) {

                                            selectable = false;
                                        }

                                        out.print( ( selectable) ? TR_selectable : TR_nonselectable );

                                    }

                                    players_found++;

                                    out.print("<td class=timesheetTD nowrap" + (!color.equals("") ? " style=\"background-color:" + color + "\"" : "") + ">");
                                    out.print("&nbsp;" + rs2.getString("player_name") + "</td>");

                                } // end player loop

                                pstmt2.close();

                            } catch (Exception exc) {

                                out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                            } finally {

                                try { rs2.close(); }
                                catch (Exception ignore) {}

                                try { pstmt2.close(); }
                                catch (Exception ignore) {}

                            }

                            // if this time slot is empty then the row hasn't been started - do it now before we fill in the empty player slots
                            if ( !started_row ) {

                                out.print( ( selectable) ? TR_selectable : TR_nonselectable );

                            }


                            // if no players then check to see if it was an event or lesson
                            // and if not then check for a restriction and coloring the rows
                            // accordingly. the player slots will all be full if any of these match
                            if (players_found == 0) { //emptySlot

                                if (rs.getInt("event_id") != 0) {

                                    // time is covered by an event
                                    out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD " +
                                                "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\" " +
                                                "nowrap><i>" + rs.getString("name") + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                } else if (rs.getInt("lesson_id") != 0) {

                                    result = getLessonName(rs.getInt("lesson_id"), con);

                                    StringTokenizer tok = new StringTokenizer( result, "|" );

                                    lname = tok.nextToken();       // get the name of this lesson
                                    String lcolor = tok.nextToken();      // get the color for this lesson

                                    out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD nowrap " +
                                                "style=\"background-color:" + lcolor + "\"><i>" + lname + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                } else if (!rname.equals("")) {

                                    // if no players are here and there is a restriction covering this time - display its name
                                    out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                                "" + color + "\"><i>" + rname + "</i></td>");

                                    players_found = rs.getInt("max_players");
                                }

                            }


                            // see if we need to fill in any remaining player positions for this time slot based up the activity
                            while (players_found < rs.getInt("max_players")) {

                                out.print("<td class=\"timesheetTD\"" + (( !color.equals("") ) ? " style=\"background-color:" + color + "\"" : "") + ">&nbsp;</td>");
                                players_found++;
                            }

                            out.println("</tr>");

                        } // end if blocked

                    } // end time slot rs loop

                    pstmt.close();

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                }

                out.println("</table>");

                out.println("</div>");

                out.println("</div>");

                // end the main table
                out.println("</td><tr></table>");

                //buildRestHolder(restArray, con, out);

            } else if ( layout_mode == 2 ) {

                //
                //*******************************************************************************************************
                // Layout Mode 2 (Summary) - Display Time Sheet (Member View - multiple activities - horizontal display)
                //*******************************************************************************************************
                //

                // this array will hold the activities and the order in which they are displayed
                ArrayList<Integer> order = new ArrayList<Integer>();
                ArrayList<Integer> consec = new ArrayList<Integer>();

                String consec_csv = "";

                out.println("<table align=center border=0 style=\"margin: auto\">");
                out.println("<tr valign=top><td>");

                // The first column contains all the activity names (ie. court names)
                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT t2.activity_name, t2.activity_id " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                            "GROUP BY t2.activity_name " +
                            "ORDER BY t2.sort_by, t2.activity_name");

                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    pstmt.setInt(2, date);
                    rs = pstmt.executeQuery();

                    out.println("<table class=timesheet>");
                    out.println("<tr>");
                    out.println("<td class=headerTD nowrap><b>Time:</b></td>");             // Time Heading for horizontal row of times
                    out.println("</tr>");

                    int i = 0;

                    while ( rs.next() ) {

                        order.add(rs.getInt("activity_id"));

                        out.println("<tr>"); // class=timesheetTR id=\"row" + i + "\"
                        out.println("<td class=timesheetTD2 nowrap id=\"row" + i + "\"><b>" + rs.getString(1) + "</b></td>");    // List Each Activity in this column
                        out.println("</tr>");
                        i++;

                    }
                    //out.println("</table>");

                } catch (Exception exc) {

                    out.println("<p>ERROR LOADING ACTIVITY ROWS:" + exc.toString() + "</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}

                }

                //out.println("</td><td>");
                //out.println("<table class=timesheet>");


                //
                //   one column per time - cells contain the players
                //
                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT *, activity_name, t2.disallow_joins, t2.consec_mem_csv, e.name, e.color, " +
                                "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                            "ORDER BY date_time, sort_by, activity_name");
                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    pstmt.setInt(2, date);
                    rs = pstmt.executeQuery();

                    String last_time = "";
                    String onclick = "";
                    String td_style = "";
                    String color = "";
                    int i = 0;
                    int order_pos = 0;
                    boolean disallow_joins = false;

                    while ( rs.next() ) {

                        allow = true;     // default
                        disallow_joins = false;

                        if (restrictAll == true) allow = false;     // do not allow access to this time if all day is off limits

                        if (rs.getInt("t2.disallow_joins") == 1) {     // if 1, do not allow members to access existing times that they aren't a part of
                            disallow_joins = true;
                        }

                        // see if we are switching times (start new col)
                        if ( !last_time.equals(rs.getString("time")) ) {

                            last_time = rs.getString("time");
                            out.println("</table></td><td>");           // end current column and start new one
                            out.println("<table class=timesheet>");     // start new table inside this column
                            out.println("<tr><td class=headerTD nowrap align=center><b>" + last_time + "</b></td></tr>");
                            i=0;
                            order_pos = 0;
                        }

                        consec_csv = rs.getString("t2.consec_mem_csv").trim();

                        consec.clear();

                        if (!consec_csv.equals("")) consec = Utilities.buildActivityConsecList(consec_csv);

                        // order of checks
                        //
                        // blocked?
                        // restrict all? (due to days in advance for member)
                        // past time?
                        // in use?
                        // is member part of?
                        //

                        color = (rs.getString("color") == null) ? "" : rs.getString("color");   // get Event color if present

                        if (color.equals("")) {            // if no Event

                           if (rs.getInt("rest_id") != 0) {   // if Restriction

                              // check all restrinctions to see if this time is affected
                              String restData = getRestData(parmr, rs.getInt("intTime"), rs.getInt("activity_id"), rs.getInt("rest_id"));

                              if (!restData.equals("")) {      // if something found

                                 if (g_debug) out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                 // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                 StringTokenizer tok = new StringTokenizer( restData, ":" );

                                 color = tok.nextToken();                // get the color for this time
                                 String rest_ind = tok.nextToken();      // get the restricted indicator

                                 if (color.equals("none")) {       // if no color specified or found

                                    color = "#E9F4BB";             // use default color
                                 }

                                 if (rest_ind.equals("block")) {   // if member restricted

                                    allow = false;               // do not allow member to access this time
                                 }

                              } else {       // no restrinctions found

                                 color = "#E9F4BB";             // use default color
                              }

                           } else {

                              color = "#E9F4BB";             // use default color
                           }
                        }

                        // if not is use and not blocked
                        if (!rs.getString("in_use_by").equals("")) {

                            // time is in-use
                            onclick = "";
                            td_style = "style=\"background: yellow;\"";

                        // } else if (!color.equals("")) {
                        } else if (rs.getInt("event_id") != 0) {            // if Event

                            // this is an event time - not selectable and color this td
                            onclick = "";
                            td_style = "style=\"background: " + color + ";\"";

                        } else if (rs.getInt("lesson_id") != 0) {

                            // time is taken by a lesson booking
                            onclick = "";
                            td_style = "style=\"background: #E9F4BB;\"";

                        } else if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("lesson_id") == 0 && allow == true &&
                                   rs.getInt("auto_blocked") == 0 && (!is_today || (is_today && rs.getInt("intTime") > curr_time))) {

                            onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + rs.getInt("force_singles") + "," + layout_mode + (consec.size() > 0 ? "," + consec.get(0) : "") + ")\"";
                            td_style = "style=\"background: " + color + "; cursor:pointer\"";

                        } else if ((is_today && rs.getInt("intTime") < curr_time) || (allow == false && rs.getInt("rest_id") == 0)) {

                            // time is already past
                            onclick = "";
                            td_style = "style=\"background: white;\"";

                        } else if (allow == false && rs.getInt("rest_id") != 0) {

                            // Restriction is blocking this time
                            onclick = "";
                            td_style = "style=\"background: " + color + ";\"";   // use its color

                        } else if (rs.getInt("blocker_id") != 0 || rs.getInt("auto_blocked") != 0) {

                            // time is blocked
                            onclick = "";
                            td_style = "style=\"background: black;\"";

                        }

                        // see if we've skipped over some times
                        while (order.get(order_pos) != rs.getInt("activity_id")) {
                            out.println("<tr class=timesheetTR><td class=timesheetTD2>&nbsp;</td></tr>");
                            i++;
                            order_pos++;
                        }

                        out.println("<tr class=timesheetTR onmouseover=\"hiLite(" + i + ")\" onmouseout=\"hiLiteOff(" + i + ")\">");        // note: added the class on 10/08/09
                        i++;
                        order_pos++;

                        // output the players (if any)
                        try {

                            pstmt2 = con.prepareStatement("" +
                                    "SELECT COUNT(*) AS players, (" +
                                        "SELECT COUNT(*) AS players " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? AND username = ?) AS part_of " +
                                    "FROM activity_sheets_players " +
                                    "WHERE activity_sheet_id = ? " +
                                    "ORDER BY pos");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, rs.getInt("sheet_id"));
                            pstmt2.setString(2, user);
                            pstmt2.setInt(3, rs.getInt("sheet_id"));
                            rs2 = pstmt2.executeQuery();

                            if ( rs2.next() ) {

                                if (rs2.getInt("players") > 0) {

                                    // EXISTING PLAYERS

                                    out.print("<td class=timesheetTD2 align=left nowrap ");


                                    // CUSTOM - if club requests this - make slot NOT selectable if user is not part of it
                                    if ( disallow_joins && rs2.getInt("part_of") != 1 ) {

                                         allow = false;
                                         onclick = "";
                                    }


                                    // if there is room to join and existing players have not said no to joining then make clickable
                                    if ( rs2.getInt("part_of") == 1 || (rs.getInt("force_singles") == 0 && rs2.getInt(1) < rs.getInt("max_players")) ) {

                                        // time slot is not full and others can join (or maybe it is full but user is part of it)
                                        out.print(onclick + " " + td_style);

                                    } else {

                                        // time slot is full - change bgcolor to indicate it's not clickable,
                                        out.print("style=\"background: white;\"");

                                    }
                                    out.print(">");

                                    // if time slot can be joined display the green bar
                                    if (allow && rs.getInt("force_singles") == 0 && rs2.getInt(1) < rs.getInt("max_players") && (!is_today || (is_today && rs.getInt("intTime") > curr_time))) out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                    out.print("<nobr>&nbsp;Member (" + rs2.getInt(1) + ")</nobr>");

                                } else {

                                    // EMPTY TIME SLOT

                                    out.print("<td class=timesheetTD2 " + onclick + " " + td_style + " nowrap>");

                                    if ( rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && rs.getInt("blocker_id") == 0) {

                                        // NOT IN USE
                                        if (onclick.equals("")) {
                                           out.println("&nbsp;");      // if today and past time or not allowed
                                        } else {
                                           out.print("<span style=\"background-color:darkGreen\">" +
                                               "<!--<a href=\"javascript: void(0)\"><img src='/" + rev + "/images/shim.gif' width=10 height=25 border=0></a>-->" +
                                               "&nbsp;</span>&nbsp;");
                                        }

                                    } else {

                                        // IN USE
                                        if (rs.getInt("lesson_id") != 0) {

                                            out.println("<center><i>" + ((rs.getInt("lesson_id") > 0) ? sysLingo.TEXT_Lesson : "Clinic") + "</i></center>");

                                        } else if (rs.getInt("event_id") > 0) {

                                            out.println("<center><i>Event</i></center>");
                                            //out.println("<center><i>" + rs.getString("name") + "</i></center>");

                                        } else if (rs.getInt("blocker_id") > 0) {

                                            out.println("<center><i><font color=white>Blocked</font></i></center>");

                                        } else {

                                            out.println("&nbsp;<i>in use</i>&nbsp;");

                                        }
                                    }

                                }

                            } // end rs2

                            pstmt2.close();

                        } catch (Exception exc) {

                            out.println("<p>ERROR LOADING PLAYERS:" + exc.toString() + "</p>");

                        }

                        // end player time slot td/tr
                        out.println("</td></tr>");

                    } // end while loop for all the time slots

                    pstmt.close();

                    out.println("</table>");    // terminate the last table

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}

                    try { rs2.close(); }
                    catch (Exception ignore) {}

                    try { pstmt2.close(); }
                    catch (Exception ignore) {}

                }

                // end the main table
                out.println("</td><tr></table>");

            } else if (layout_mode == 3) {

                //
                //*******************************************************************************************************
                // Layout Mode 3 (Detailed) - Display Time Sheet (Member View - multiple activities - vertical display)
                //*******************************************************************************************************
                //


                // FIND OUT THE MAX # OF PLAYER POSITIONS & CONSECUTIVE TIMES FOR GROUP OF ACTIVITIES
                int max_players = 0;
                int max_consec_mem = 0;

                String consec_csv ="";

                ArrayList<Integer> consec = new ArrayList<Integer>();

                try {

                    stmt = con.createStatement();
                    rs = stmt.executeQuery("" +
                            "SELECT MAX(max_players), MAX(consec_mem) " +
                            "FROM activities " +
                            "WHERE activity_id IN (SELECT activity_id FROM activities WHERE parent_id = " + group_id + ")");

                    if (rs.next()) {
                        max_players = rs.getInt(1);
                        max_consec_mem = rs.getInt(2);
                    }

                } catch (Exception exc) {

                    out.println("<p>ERROR1:" + exc.toString() + "</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { stmt.close(); }
                    catch (Exception ignore) {}

                }

                out.println("<table align=center border=0 style=\"margin: auto\">");
                out.println("<tr valign=top><td>");

                out.println("<table class=\"timesheet\" align=center style=\"align:center;\">");

                // header row
                out.println("<tr class=timesheetTH>");
                out.println("<td class=headerTD>Time</td>");
                if (!restrictAll && max_consec_mem > 1) out.println("<td class=headerTD>Min.</td>");
                out.println("<td class=headerTD width=100>" + getActivity.getActivityCommonName(activity_id, con) + "</td>");
                for (int i = 1; i <= max_players; i++) {

                    out.print("<td class=headerTD>Player " + i + "</td>");
                }
                out.println("</tr>");

                String color = "";

                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT t1.*, t2.max_players, t2.activity_name, t2.disallow_joins, t2.consec_mem, t2.consec_mem_csv, t2.interval, t2.alt_interval, e.name, e.color, " +
                                "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                            "ORDER BY date_time, sort_by, activity_name");

                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id);
                    pstmt.setInt(2, date);

                    rs = pstmt.executeQuery();

                    int interval = 0;
                    int players_found = 0;
                    int curr_activity_id = 0;
                    boolean alt = true; // #5D8733
                    boolean selectable = true;
                    boolean disallow_joins = false;
                    String last_time = "";

                    while ( rs.next() ) {

                        players_found = 0; // reset
                        selectable = true;
                        disallow_joins = false;
                        String rname = "";

                        color = (rs.getString("color") == null) ? "" : rs.getString("color");
                        curr_activity_id = rs.getInt("activity_id");

                        if (rs.getInt("t2.disallow_joins") == 1) {     // if 1, do not allow members to access existing times that they aren't a part of
                            disallow_joins = true;
                        }

                        allow = true;     // default

                        if (restrictAll == true) allow = false;     // do not allow access to this time if all day is off limits
                        
                        // If Governors Club and Courts 1-6 (activity_ids 2-7), only display times every 90 minutes. Skip the rest!
                        if (club.equals("governorsclub") && curr_activity_id >= 2 && curr_activity_id <= 7) {

                            int tempTime = rs.getInt("intTime");

                            if (tempTime != 800 && tempTime != 930 && tempTime != 1100 && tempTime != 1230
                                    && tempTime != 1400 && tempTime != 1530 && tempTime != 1700 && tempTime != 1830
                                    && tempTime != 2000) {

                                continue;
                            }
                        } else if (club.equals("lakewoodranch")) {
                            
                            // Prior to 1pm, only display times every 1.5 hrs.
                            int tempTime = rs.getInt("intTime");
                            
                            if (tempTime < 1300  && tempTime != 830 && tempTime != 1000 && tempTime != 1130) {
                                continue;
                            }
                        }

                        if (color.equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {   // if no Event or Blocker

                           if (rs.getInt("rest_id") != 0) {   // if Restriction

                              try {

                                  // check all restrinctions to see if this time is affected
                                  String restData = getRestData(parmr, rs.getInt("intTime"), rs.getInt("activity_id"), rs.getInt("rest_id"));

                                  if (!restData.equals("")) {

                                      if (g_debug) out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                      // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                      StringTokenizer tok = new StringTokenizer( restData, ":" );

                                      color = tok.nextToken();               // get the color for this time
                                      String rest_ind = tok.nextToken();     // get the restricted indicator
                                      rname = tok.nextToken();            // get the name of this restriction

                                      if (color.equals("none")) {            // if no color specified or found

                                          color = "";                         // use default color
                                      }

                                      if (rest_ind.equals("block")) {

                                          allow = false;
                                      }                      // do not allow member to access this time
                                  }

                              } catch (Exception exc) {

                                  out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");

                              }

                           } // end if restriction found
                        }

                        // hide blocked times
                        if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                            if ( !last_time.equals(rs.getString("time")) ) {

                                last_time = rs.getString("time");
                                alt = (alt == false); // toggle row shading
                            }

                            // if today and the time has past, then make it not selectable
                            if ( is_today && rs.getInt("intTime") < curr_time ) {

                                selectable = false;
                            }

                            // if this day is beyond days in advance for member, then make it not selectable
                            if ( allow == false ) {

                                selectable = false;
                            }

                            // if in use by someone other than this user
                            if ( selectable && (!rs.getString("in_use_by").equals("") && !rs.getString("in_use_by").equals( user )) ) {

                                selectable = false;
                            }

                            if ( rs.getInt("lesson_id") != 0) {

                                selectable = false;
                            }
        /*
                            // make not selectable if owner specified no else can join AND user is not part of this time slot
                            if ( selectable && (rs.getInt("force_singles") == 1 && rs.getInt("part_of") != 1) ) {

                                selectable = false;
                            }
        */
                            // if this is an event time then make it not selectable
                            if ( selectable && rs.getInt("event_id") != 0 ) {

                                selectable = false;
                            }

                            boolean started_row = false;

                            if (club.equals("wingedfoot")) {
                                if (day_name.equals("Saturday") || day_name.equals("Sunday")) {
                                    consec_csv = "2,3";
                                } else {
                                    consec_csv = "2,1";
                                }
                            } else {
                                consec_csv = rs.getString("t2.consec_mem_csv").trim();
                            }

                            consec.clear();

                            if (!consec_csv.equals("")) consec = Utilities.buildActivityConsecList(consec_csv);

                            String TR_selectable = "<tr class=\"timesheetTR\"><td align=center><button class=\"btnSlot\" onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + rs.getInt("force_singles") + "," + layout_mode + ", " + (consec.size() > 0 ? consec.get(0) : "0") + ")\">" + rs.getString("time") + "</button></td>";
                            String TR_nonselectable = "<tr class=\"timesheetTR\"" + ((color.equals("")) ? "" : " style=\"background-color:" + color + "\"") + "><td align=center>" + rs.getString("time") + "</td>";

                            if (!restrictAll && consec.size() > 0) {

                                TR_selectable += "" +
                                        "<td>" +
                                        "<select name=consec size=1 " +
                                        " onchange=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + rs.getInt("force_singles") + "," + layout_mode + ", this.options[this.selectedIndex].value)\">";

                                if (rs.getInt("interval") == rs.getInt("alt_interval") || rs.getInt("alt_interval") == 0) {
                                    interval = rs.getInt("interval");
                                } else {
                                    interval = 1;
                                }

                                /* if (club.equals("pattersonclub") && sess_activity_id == 9) {

                                    // pattersonclub AND paddle courts then display Single & Doubles as 60 & 90 minutes
                                    TR_selectable += "<option value=\"2\">Singles<option value=\"3\">Doubles";

                                } else { */

                                for (int i=0; i < consec.size(); i++) {
                                    TR_selectable += "<option value=\"" + consec.get(i) + "\">" + (consec.get(i) * interval);
                                }
                                /*
                                    // display the minutes using the interval value
                                    for (int i = 1; i <= rs.getInt("consec_mem"); i++) {
                                        TR_selectable += "<option value=\"" + i + "\">" + (i * interval);
                                    }

                                //}
                                */
                                TR_selectable += "</select></td>";

                                TR_nonselectable += "<td>&nbsp;</td>";

                            } else if (!restrictAll && max_consec_mem > 1) {

                                // other activities being displayed allow consecutive times but not this one

                                TR_selectable += "<td>&nbsp;</td>";
                                TR_nonselectable += "<td>&nbsp;</td>";

                            }

                            // only look up the players if time slot is not for a lesson or event
                            if (rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0) {

                                // print the player columns
                                try {

                                    pstmt2 = con.prepareStatement("" +
                                            "SELECT *, (" +
                                                "SELECT COUNT(*) AS players " +
                                                "FROM activity_sheets_players " +
                                                "WHERE activity_sheet_id = ? AND username = ?) AS part_of " +
                                            "FROM activity_sheets_players " +
                                            "WHERE activity_sheet_id = ? " +
                                            "ORDER BY pos");
                                    pstmt2.clearParameters();
                                    pstmt2.setInt(1, rs.getInt("sheet_id"));
                                    pstmt2.setString(2, user);
                                    pstmt2.setInt(3, rs.getInt("sheet_id"));
                                    rs2 = pstmt2.executeQuery();

                                    started_row = false;

                                    while ( rs2.next() ) {

                                        if ( !started_row ) {

                                            started_row = true;

                                            // make not selectable if owner specified no else can join AND user is not part of this time slot
                                            if ( selectable && (rs.getInt("force_singles") == 1 && rs2.getInt("part_of") != 1) ) {

                                                selectable = false;
                                            }

                                            // CUSTOM - if club requests this - make slot NOT selectable if user is not part of it
                                            if ( selectable && disallow_joins && rs2.getInt("part_of") != 1 ) {

                                                selectable = false;
                                            }

                                            out.print( ( selectable) ? TR_selectable : TR_nonselectable );

                                            out.print("<td class=" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + " nowrap align=center>" + rs.getString("activity_name") + "</td>");
                                        }

                                        players_found++;

                                        out.print("<td class=timesheetTD nowrap" + (!color.equals("") ? " style=\"background-color:" + color + "\"" : "") + ">");
                                        out.print("&nbsp;" + rs2.getString("player_name") + "</td>");

                                    } // end player loop

                                    pstmt2.close();

                                } catch (Exception exc) {

                                    out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                                } finally {

                                    try { rs2.close(); }
                                    catch (Exception ignore) {}

                                    try { pstmt2.close(); }
                                    catch (Exception ignore) {}

                                }

                                // if no players are here and there is a restriction covering this time - display its name
                                if (players_found == 0 && !rname.equals("")) { //emptySlot &&

                                    out.print( (selectable) ? TR_selectable : TR_nonselectable );
                                    out.print("<td class=" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + " nowrap align=center>" + rs.getString("activity_name") + "</td>");
                                    out.println("<td colspan='" + rs.getInt("max_players") + "' style=\"text-align:center;background-color:" + color + "\"><i>" + rname + "</i></td>"); // align=center class=timesheetTD nowrap
                                    players_found = rs.getInt("max_players");
                                    started_row = true;
                                }

                            } else {

                                // this time slot if for either an event or a lesson

                                out.print( TR_nonselectable );
                                out.print("<td class=" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + " nowrap align=center>" + rs.getString("activity_name") + "</td>");
                                started_row = true;

                                if (rs.getInt("event_id") != 0) {

                                    // time is covered by an event
                                    out.println("<td colspan='" + rs.getInt("max_players") + "' style=\"text-align:center;background-color: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\"><i>" + rs.getString("name") + "</i></td>"); // align=center class=timesheetTD
                                    players_found = rs.getInt("max_players");

                                } else if (rs.getInt("lesson_id") != 0) {

                                    out.println("<td colspan='" + rs.getInt("max_players") + "' style=\"text-align:center\"><i>" + ((rs.getInt("lesson_id") > 0) ? sysLingo.TEXT_Lesson : "Clinic") + "</i></td>"); // align=center class=timesheetTD
                                    players_found = rs.getInt("max_players");

                                }

                            }

                            // if this time slot is empty then the row hasn't been started - do it now before we fill in the empty player slots
                            if ( !started_row ) {

                                out.print( ( selectable) ? TR_selectable : TR_nonselectable );

                                out.print("<td class=" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + " nowrap align=center>" + rs.getString("activity_name") + "</td>");

                            }

                            // see if we need to fill in any remaining player positions for this time slot based up the activity
                            while (players_found < rs.getInt("max_players")) {

                                out.print("<td class=timesheetTD" + (!color.equals("") ? " style=\"background-color:" + color + "\"" : "") + ">&nbsp;</td>");
                                players_found++;
                            }

                            // now see if we need to fill in any remaining player positions for this time slot based upon the activity group
                            while (players_found < max_players) {

                                // if we're in here then this activity must support less players than others being displayed
                                // so lets build td that are grayed out
                                out.print("<td class=timesheetTD style=\"background-color: black\">&nbsp</td>");

                                players_found++;
                            }

                            out.println("</tr>");

                        } // end if blocked

                    } // end time slot rs loop

                    pstmt.close();

                } catch (Exception exc) {

                    Utilities.logError("Member_gensheets: detail view.  Err=" + exc.toString());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}

                }

                out.println("</table>");

                // end the main table
                out.println("</td><tr></table>");

            } else if (layout_mode == 4) {


                //
                // LAYOUT MODE 4 - NEW SUMMARY VIEW
                //

                // this array will hold the activities and the order in which they are displayed
                ArrayList<Integer> order = new ArrayList<Integer>();
                ArrayList<String> activity_names = new ArrayList<String>();

                String consec_csv = "";
                String curr_activity_instring = "";

                int activity_ids_per_row = getActivity.getColsPerRow(group_id, con);      // Determines how many activity ids are displayed in each row of the time sheet

                out.println("<script type=\"text/javascript\">");
                out.println("function hiLite(i) {");
                out.println(" document.getElementById('row'+i).className='headerTDover';");
                out.println("}");
                out.println("function hiLiteOff(i) {");
                out.println(" document.getElementById('row'+i).className='headerTD';");
                out.println("}");
                out.println("</script>");

                try {

                    // Load all the relevant activity_ids and activity_names into their respective ArrayLists
                    pstmt = con.prepareStatement("" +
                            "SELECT t2.activity_name, t2.activity_id " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                            "GROUP BY t2.activity_id " +
                            "ORDER BY t2.sort_by, t2.activity_name");

                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    pstmt.setInt(2, date);
                    rs = pstmt.executeQuery();

                    while ( rs.next() ) {

                        order.add(rs.getInt("activity_id"));
                        activity_names.add(rs.getString("activity_name"));
                    }

                } catch (Exception exc) {
                    Utilities.logError("Member_gensheets.doGet - Error while printing layout_mode 4 header row - Err: " + exc.toString());
                }


                // Loop and print n activity_ids at a time (this is determined by the "cols_per_row" field in the parent activity database entry)
                for (int i=0; i<=((order.size()-1)/activity_ids_per_row); i++) {

                    curr_activity_instring = "";

                    out.println("<table align=\"center\" border=\"1\" style=\"margin: auto\">");
                    out.println("<tr valign=\"top\"><td>");

                    out.println("<table class=\"timeSheet\">");
                    out.println("<tr>");
                    out.println("<td class=\"headerTD\" nowrap><b>Time</b></td>");

                    for (int j = (i*activity_ids_per_row); j < ((i+1)*activity_ids_per_row) && j < order.size(); j++) {

                        curr_activity_instring += (!curr_activity_instring.equals("") ? "," : "") + order.get(j);   // Add activity_ids to a csv string to be used when pulling activity_sheets below
                        out.println("<td class=\"headerTD\" style=\"width:90px\" id=\"col" + j + "\" align=\"center\"><b>" + activity_names.get(j) + "</b></td>"); // timesheetTD2
                    }

                    try {

                        pstmt = con.prepareStatement("" +
                                "SELECT t1.*, t2.activity_name, t2.max_players, t2.disallow_joins, t2.consec_mem_csv, t2.interval, t2.alt_interval, e.name, e.color, " +
                                    "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                    "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                    "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                                "FROM activity_sheets t1 " +
                                "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                                "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                                "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                                "WHERE " +
                                    "t1.activity_id IN (" + curr_activity_instring + ") AND " +
                                    "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                "ORDER BY date_time, sort_by, activity_name");
                        pstmt.clearParameters();
                        pstmt.setInt(1, date);
                        rs = pstmt.executeQuery();

                        String last_time = "";
                        String onclick = "";
                        String td_style = "";
                        int row = i*1000;
                        int order_pos = 0;
                        int part_of = 0;
                        int sheet_id = 0;
                        int consec = 0;
                        int players_found = 0;
                        int curr_activity_id = 0;
                        String username = "";
                        String last_name = "";
                        String color = "";
                        String rname = "";
                        boolean disallow_joins = false;

                        while ( rs.next() ) {

                            color = "";
                            rname = "";
                            username = "";
                            td_style = "";
                            part_of = 0;
                            players_found = 0; // reset
                            allow = true;
                            disallow_joins = false;

                            if (restrictAll == true) allow = false;     // do not allow access to this time if all day is off limits

                            sheet_id = rs.getInt("sheet_id");
                            curr_activity_id = rs.getInt("activity_id");

                            if (rs.getInt("t2.disallow_joins") == 1) {     // if 1, do not allow members to access existing times that they aren't a part of
                                disallow_joins = true;
                            }

                            // If Belle Meade CC, only display times every 90 minutes. Skip the rest!
                            if (club.equals("bellemeadecc")) {

                                int tempTime = rs.getInt("intTime");

                                if (day_name.equals("Sunday")) {    // Sunday drops a couple times

                                    if (tempTime != 1030 && tempTime != 1200 && tempTime != 1330 && tempTime != 1500 && tempTime != 1630 && tempTime != 1800) {

                                        continue;
                                    }

                                } else {    // All other days of the week

                                    if (tempTime != 900 && tempTime != 1030 && tempTime != 1200 && tempTime != 1330 
                                            && tempTime != 1500 && tempTime != 1630 && tempTime != 1800 && tempTime != 1930) {

                                        continue;
                                    }
                                }
                            }

                            // If Governors Club and Courts 1-6 (activity_ids 2-7), only display times every 90 minutes. Skip the rest!
                            if (club.equals("governorsclub") && curr_activity_id >= 2 && curr_activity_id <= 7) {

                                int tempTime = rs.getInt("intTime");

                                if (tempTime != 800 && tempTime != 930 && tempTime != 1100 && tempTime != 1230
                                        && tempTime != 1400 && tempTime != 1530 && tempTime != 1700 && tempTime != 1830 
                                        && tempTime != 2000) {

                                    continue;
                                }
                            } else if (club.equals("lakewoodranch")) {
                                
                                // Prior to 1pm, only display times every 1.5 hrs.
                                int tempTime = rs.getInt("intTime");
                                
                                if (tempTime < 1300  && tempTime != 830 && tempTime != 1000 && tempTime != 1130) {
                                    continue;
                                }
                            }


                            // see if we are switching times (new row)
                            if ( !last_time.equals(rs.getString("time")) ) {

                                last_time = rs.getString("time");
                                out.println("</tr>");
                                out.println("<tr onmouseover=\"hiLite(" + row + ")\" onmouseout=\"hiLiteOff(" + row + ")\"><td class=\"headerTD\" nowrap align=\"center\" id=\"row" + row + "\"><b>" + last_time + "</b></td>"); // </tr>
                                order_pos = i*activity_ids_per_row;
                                row++;
                            }

                            consec = getActivity.getMaxConsecTimes(user, curr_activity_id, con);

                            color = (rs.getString("color") == null) ? "" : rs.getString("color");   // get Event color if present

                            if (color.equals("")) {            // if no Event

                               if (rs.getInt("rest_id") != 0) {   // if Restriction

                                  // check all restrinctions to see if this time is affected
                                  String restData = getRestData(parmr, rs.getInt("intTime"), curr_activity_id, rs.getInt("rest_id"));

                                  if (!restData.equals("")) {      // if something found

                                     if (g_debug) out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                     // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                     StringTokenizer tok = new StringTokenizer( restData, ":" );

                                     color = tok.nextToken();                // get the color for this time
                                     String rest_ind = tok.nextToken();      // get the restricted indicator

                                     if (color.equals("none")) {       // if no color specified or found

                                        color = "#E9F4BB";             // use default color
                                     }

                                     if (rest_ind.equals("block")) {   // if member restricted

                                        allow = false;               // do not allow member to access this time
                                     }

                                  } else {       // no restrinctions found

                                     color = "#E9F4BB";             // use default color
                                  }

                               } else {

                                  color = "#E9F4BB";             // use default color
                               }
                            }


                            if (!rs.getString("in_use_by").equals("")) {

                                // time is in-use
                                onclick = "";
                                td_style = "style=\"background: yellow;\"";

                            } else if (rs.getInt("event_id") != 0) {

                                // time is covered by an event
                                onclick = "";
                                td_style = "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\"";


                            } else if (rs.getInt("lesson_id") != 0) {

                                // time is taken by a lesson booking
                                String result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);
                                StringTokenizer tok = new StringTokenizer( result, "|" );

                                String lname = tok.nextToken();       // get the name of this lesson
                                String lcolor = tok.nextToken();      // get the color for this lesson

                                onclick = "";
                                td_style = "style=\"background:" + lcolor + ";\"";

                            } else if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("lesson_id") == 0 && allow == true &&
                                       rs.getInt("auto_blocked") == 0 && ((!is_today && !restrictAll) || (is_today && rs.getInt("intTime") > curr_time))) {     // if not in use and not blocked & not a lesson & not an event

                                onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + rs.getInt("force_singles") + "," + layout_mode + (consec > 0 ? "," + consec : "") + ")\"";
                                td_style = "style=\"background: " + color + "; cursor:pointer\"";

                            } else if (rs.getInt("blocker_id") != 0 || rs.getInt("auto_blocked") != 0) {

                                // time is blocked
                                onclick = "";
                                td_style = "style=\"background: black;\"";

                            } else if ((is_today && rs.getInt("intTime") < curr_time) || ((!allow || restrictAll) && rs.getInt("rest_id") == 0)) {

                                // time is already past
                                onclick = "";
                                td_style = "style=\"background: white;\"";

                            } else if (allow == false && rs.getInt("rest_id") != 0) {

                                // Restriction is blocking this time
                                onclick = "";
                                td_style = "style=\"background: " + color + ";\"";   // use its color

                            }

                            // see if we've skipped over any courts
                            while (order.get(order_pos) != curr_activity_id) {
                                out.println("<td class=\"timesheetTD2\">&nbsp;</td>");
                                order_pos++;
                            }

                            order_pos++;

                            // output the players (if any)
                            try {

                                // for these clubs we will display the first members last name instead of the generic 'Member' tag
                                pstmt2 = con.prepareStatement("" +
                                        "SELECT COUNT(*) AS players_found, username, SUBSTRING_INDEX(player_name, ' ', -1) AS last_name, (" +
                                            "SELECT COUNT(*) AS players " +
                                            "FROM activity_sheets_players " +
                                            "WHERE activity_sheet_id = ? AND username = ?) AS part_of " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? " +
                                        "GROUP BY activity_sheet_id " +
                                        "ORDER BY pos");

                                pstmt2.clearParameters();
                                pstmt2.setInt(1, sheet_id);
                                pstmt2.setString(2, user);
                                pstmt2.setInt(3, sheet_id);
                                rs2 = pstmt2.executeQuery();

                                if ( rs2.next() ) {

                                    players_found = rs2.getInt("players_found");
                                    username = rs2.getString("username");
                                    last_name = rs2.getString("last_name");
                                    part_of = rs2.getInt("part_of");

                                } else {
                                    players_found = 0;
                                }

                                if (players_found > 0) {

                                    // EXISTING PLAYERS
                                    out.print("<td class=\"timesheetTD2\" align=\"left\" nowrap ");

                                    if ((disallow_joins && part_of != 1) || restrictAll) {

                                        allow = false;
                                        onclick = "";
                                    }


                                    // if there is room to join and existing players have not said no to joining then color as normal
                                    if (allow && (part_of == 1 || (rs.getInt("force_singles") == 0 && players_found < rs.getInt("max_players")))) {

                                        // time slot is not full and others can join
                                        out.print(onclick + " " + td_style);

                                    } else {

                                        // time slot is full - change bgcolor to indicate it's not clickable
                                        out.print("style=\"background: white;\"");
                                    }
                                    out.print(">");

                                    // if time slot can be joined display the green bar
                                    if (allow && (part_of == 1 || (rs.getInt("force_singles") == 0 && players_found < rs.getInt("max_players"))) && ((!is_today && !restrictAll) || (is_today && rs.getInt("intTime") > curr_time))) out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                    out.print("<nobr>&nbsp;" + ((username.equals("")) ? ((last_name.equalsIgnoreCase("x")) ? "X" : "Guest") : last_name) + " (" + players_found + ")<nobr>");


                                } else {

                                    // EMPTY TIME SLOT

                                    out.print("<td class=\"timesheetTD2\" " + onclick + " " + td_style + " nowrap>");

                                    if ( rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 &&
                                         rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                                        // NOT IN USE
                                        if (onclick.equals("")) {
                                            out.println("&nbsp;");      // if today and past time or not allowed
                                        } else {
                                            out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                            out.print("&nbsp;");
                                        }

                                    } else {

                                        // IN USE OR TAKEN BY LESSON
                                        if (rs.getInt("lesson_id") != 0) {

                                            if (rs.getInt("lesson_id") < 0) {
                                                String result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);
                                                StringTokenizer tok = new StringTokenizer( result, "|" );

                                                String lname = tok.nextToken();       // get the name of this lesson
                                                String lcolor = tok.nextToken();      // get the color for this lesson

                                                out.println("<span title=\"" + lname + "\"><center><i>Clinic</i></center></span>");

                                            } else {

                                                out.println("<center><i>" + sysLingo.TEXT_Lesson + "</i></center>");
                                            }

                                        } else if (rs.getInt("event_id") > 0) {

                                            out.println("<center><i>Event</i></center>");

                                        } else if (rs.getInt("blocker_id") > 0) {

                                            out.println("<center><i><font color=\"white\">Blocked</font></i></center>");

                                        } else {

                                            out.println("&nbsp;<i>in use</i>&nbsp;");

                                        }
                                    }

                                }

                                //}

                                pstmt2.close();

                            } catch (Exception exc) {

                                out.println("<p>ERROR LOADING PLAYERS:" + exc.toString() + "</p>");

                            }

                            // end player time slot td/tr
                            out.println("</td>");//</tr>

                        } // end while loop for all the time slots

                        pstmt.close();

                    } catch (Exception exc) {

                        out.println("<p>ERROR:" + exc.toString() + "</p>");
                        Utilities.logError("Member_gensheets.doGet - Error while printing layout_mode 4 - Err: " + exc.toString());
                    }

                    // end the main tables
                    out.println("<tr></table>");
                    out.println("</td><tr></table><br>");

                } // end loop

            } // end layout 4

            //out.println("</div>");

            //out.println("<form action=Member_events2 method=post name=frmDispEvnt target=_blank>");
            //out.println("<input type=hidden name=\"name\" value=\"\">");
            //out.println("</form>");

            //out.println("<script>");
            //out.println("function displayEvent(name) {");
            //out.println(" var f = document.forms['frmDispEvnt'];");
            //out.println(" f.name.value=name;");
            //out.println(" f.submit();");
            //out.println("}");
            //out.println("</script>");

        } // end if group_id > 0
    } else {
        out.println("<div style=\"text-align:center;\">No time sheets found for this activity.</div>");
    }

    if (!new_skin) {
    
        out.println("</body></html>");
        out.close();
        
    } else {
        
        //
        //  End of HTML page
        //
        Common_skin.outputPageEnd(club, sess_activity_id, out, req);
    
    }

 }


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
   int max = 0;
   int size = 0;
   int guests = 0;
   //int teams = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;

   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String c_ampm = "";
   String act_ampm = "";

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
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
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

      } else {           // name not found - try filtering it

         name = SystemUtils.filter(name);

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, name);
         rs = stmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");
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
      out.println("<br><br>");

      out.println("<b>Format:</b>&nbsp;&nbsp; " + format + "<br><br>");
      out.println("<b>Pairings by:</b>&nbsp;&nbsp; " + pairings);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
      out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size + "<br><br>");
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


 // *********************************************************
 //  Check for a member restriction for the time selected
 // *********************************************************

 public String getRestData(parmRest parmr, int time, int activity_id, int rest_id) {


    String color = "none";
    String allow = "allow";
    String name = "";
    String result = "";

    boolean suspend = false;
    boolean nameFound = false;    // Restriction that blocks the player has been found and stored, do not overwrite 'allow' & 'name' variable values
    boolean colorFound = false;    // A color has been found to display on the timesheets

    int ind = 0;

    // Check all restrinctions for this day to see if any affect this time
    mainLoop:
    while (ind < parmr.MAX && (allow.equals("allow") || !colorFound) && !parmr.restName[ind].equals("")) {           // loop over possible restrictions
       
        if (parmr.applies[ind] == 1 && parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

            // Check to make sure no suspensions apply
            suspend = false;

            suspendLoop:
            for (int k=0; k<parmr.MAX; k++) {

                if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {

                    k = parmr.MAX;   // don't bother checking any more

                } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    // time falls within a suspension

                    // check to see if this activity_id is in the locations csv for this suspension
                    StringTokenizer tok = new StringTokenizer( parmr.susp_locations[ind][k][0], "," );

                    while (tok.hasMoreTokens()) {

                        if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                            suspend = true;
                            break suspendLoop;     // don't bother checking any more

                        } // end if restriction applies to this activity_id (court level)

                    } // end while loop of the locations csv
                }
            }

            if (!suspend) {

                // check to see if this activity_id is in the locations csv for this restriction
                StringTokenizer tok = new StringTokenizer( parmr.locations[ind], "," );

                while (tok.hasMoreTokens()) {

                    if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                        // found a restriction that matches activity, date, time, day, mtype & mship of this member
                        if (!parmr.color[ind].equals("Default")) {     // if not default
                            color = parmr.color[ind];
                            colorFound = true;
                        }

                        if (!nameFound) {
                            allow = "block";
                            name = parmr.restName[ind];
                            nameFound = true;
                            continue mainLoop;
                        }

                    } // end if restriction applies to this activity_id (court level)

                } // end while loop of the locations csv

            } // end if not suspended

        } // end if time matches

       ind++;

    } // end of while loop of all restrictions


    // if restriction not found that blocks this member
    // then get the color of the restriction defined for this time
    if ((allow.equals("allow") || !colorFound) && color.equals("none")) {

        ind = 0;

        // First check the restriction that has its rest_id populated in this time slot.
        loop1:
        while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {           // loop over possible restrictions

            if (!parmr.color[ind].equals("") && !parmr.color[ind].equalsIgnoreCase("Default") &&
                 parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

                // Check to make sure no suspensions apply
                suspend = false;

                loop2:
                for (int k=0; k<parmr.MAX; k++) {

                    if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {

                        k = parmr.MAX;   // don't bother checking any more

                    } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    // time falls within a suspension


                        // check to see if this activity_id is in the locations csv for this suspension
                        StringTokenizer tok = new StringTokenizer( parmr.susp_locations[ind][k][0], "," );

                        while (tok.hasMoreTokens()) {

                            if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                                break loop1;     // don't bother checking any more

                            } // end if restriction applies to this activity_id (court level)

                        } // end while loop of the locations csv
                    }
                }

                if (!suspend) {

                    // check to see if this activity_id is in the locations csv for this restriction
                    StringTokenizer tok = new StringTokenizer( parmr.locations[ind], "," );

                    while (tok.hasMoreTokens()) {

                        if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                            // found a restriction that matches activity, date, time, day, mtype & mship of this member
                            color = parmr.color[ind];
                            
                            if (!nameFound) {
                                name = parmr.restName[ind];
                            }
                            break loop1;

                        } // end if restriction applies to this activity_id (court level)

                    } // end while loop of the locations csv

                } // end if not suspended

            } // end if color not blank/default and time matches

           ind++;

        } // end of while loop of all restrictions

        // If a color wasn't found above, look through the rest of the restrictions
        if (color.equals("none")) {

            ind = 0;

            loop1:
            while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {           // loop over possible restrictions

                if (!parmr.color[ind].equals("") && !parmr.color[ind].equalsIgnoreCase("Default") &&
                     parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

                    // Check to make sure no suspensions apply
                    suspend = false;

                    loop2:
                    for (int k=0; k<parmr.MAX; k++) {

                        if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {

                            k = parmr.MAX;   // don't bother checking any more

                        } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    // time falls within a suspension


                            // check to see if this activity_id is in the locations csv for this suspension
                            StringTokenizer tok = new StringTokenizer( parmr.susp_locations[ind][k][0], "," );

                            while (tok.hasMoreTokens()) {

                                if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                                    suspend = true;
                                    break loop2;     // don't bother checking any more

                                } // end if restriction applies to this activity_id (court level)

                            } // end while loop of the locations csv
                        }
                    }

                    if (!suspend) {

                        // check to see if this activity_id is in the locations csv for this restriction
                        StringTokenizer tok = new StringTokenizer( parmr.locations[ind], "," );

                        while (tok.hasMoreTokens()) {

                            if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                                // found a restriction that matches activity, date, time, day, mtype & mship of this member
                                color = parmr.color[ind];
                                
                                if (!nameFound) {
                                    name = parmr.restName[ind];
                                }
                                break loop1;

                            } // end if restriction applies to this activity_id (court level)

                        } // end while loop of the locations csv

                    } // end if not suspended

                } // end if color not blank/default and time matches

               ind++;

            } // end of while loop of all restrictions
        }
    }

    if (!allow.equals("allow") || !color.equals("none")) {
        result = color + ":" + allow + ":" + name;
    }

    return result;

 }


 private String getLessonName(int lesson_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        // if negative it's a lesson group, if positive then it's a individual lesson
        if (lesson_id < 0) {

            // lesson group (go to lessongrp5 - bind lesson_id to one another and get lname)
            pstmt = con.prepareStatement("" +
                    "SELECT lname, color " +
                    "FROM lessongrp5 " +
                    "WHERE lesson_id = ?");
            lesson_id = Math.abs(lesson_id);

        } else {

            // individual lesson (go to lessonbook5 - bind lesson_id to recid and get ltype)
            pstmt = con.prepareStatement("" +
                    "SELECT ltype, color " +
                    "FROM lessonbook5 " +
                    "WHERE recid = ?");
        }

        pstmt.clearParameters();
        pstmt.setInt(1, lesson_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            result = rs.getString(1) + "|" + rs.getString(2);

        }

    } catch (Exception exc) {

        Utilities.logError("<p>ERROR GETTING LESSON NAME:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }
}