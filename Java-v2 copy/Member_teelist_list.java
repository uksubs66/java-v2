/***************************************************************************************
 *   Member_teelist_list:  This servlet will search teecurr for this member and display
 *                         a list of current tee times.  Also, search evntsup for any
 *                         events and lreqs for any lottery requests.
 *
 *
 *   called by:  memu item & member_teemain2.htm
 *
 *   created: 1/10/2002   Bob P.
 *
 *   last updated:
 *
 *        3/06/14    La Grange CC (lagrangecc) - Do not display the 5th player slot for members (case 2382).
 *        3/05/14    Correct the link to event signups for FlxRez - user new skin link rather than old anchor link.
 *        3/04/14    Edina CC - custom to allow adults to access their dependents' event registrations - like Denver CC (case 2378).
 *        2/20/14    Chattanooga G & CC (chattanoogagcc) - Do not display the 5th player slot for members (case 2372).
 *       11/29/13    Check rest5 in teecurr2 to determine if 5-somes are allowed.
 *       11/13/13    Season long event signups will now be displayed until it's beyond the cutoff time on the event's cutoff date (they were bugged and not showing if the "date" value, which they can't change, had passed).
 *       10/21/13    Fixed issue where a player name in the 4th group of a lottery signup was being displayed twice.
 *        5/30/13    Exclude FlxRez reservations when view Dining
 *        5/29/13    Pass the club name to Utilities.checkRests for customs.
 *        2/28/13    Denver CC - change custom to only display dependent events for the activity they are on.
 *        2/11/13    Do not show the other activities if FLEXWEBFT (Connect Premier).
 *        2/07/13    CC of York (ccyork) - Do not display the 5th player slot for members (case 2224).
 *        2/06/13    Remove refrences to top. in js and _top in target for Connect Premier
 *        2/05/13    Rehoboth Beach CC (rehobothbeachcc) - Do not display the 5th player slot for members (case 2222).
 *        1/17/13    Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *       10/30/12    Fix dining reservation display time
 *       10/16/12    Denver CC - custom to allow adults to access their dependents' event registrations.
 *        9/18/12    Adjust the top of the page for Dining to allow space for the logo.
 *        9/18/12    Use the event color on the event buttons when a member is registered for an event.
 *        9/06/12    Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        8/03/12    Events will now be listed in chronological order by date and then the actual start time of the event.
 *        5/17/12    Fixed bug that was causing the first 4 players to be displayed twice for lottery requests not allowing 5-somes.
 *        5/10/12    Fixed notification bug causing bad links to slot page
 *        2/13/12    Fixed a typo that was causing the rs ResultSet to get closed instead of the rsev ResultSet when a shotgun event was present.
 *        1/20/12    Update new skin wait list links to use json/jquery 
 *        1/19/12    Minor fixes and general db code cleanup
 *        1/17/12    Ensure dining db con gets closed.  Add return statements after outputting fatal error messages
 *        1/16/12    Forced no-cache in headers
 *        1/16/12    Updated lottery links for new skin
 *        1/12/12    Minor new skin changes
 *        1/10/12    Add new skin for DB errors, Flex Res, replace form with hashmap. Correct td for shotgun event.
 *        1/6/12     Add new_skin capability to servlet.
 *        7/12/11    Columbine, Estancia, Lakes, RTJ and Colorado - add check for 5-somes allowed.
 *        7/11/11    Wollaston GC (wollastongc) - Removed custom to restrict members from accessing tee sheets for any day on Mondays (case 1819).
 *        7/05/11    Forest Highlands GC (foresthighlands) - Added custom so tee times entered from this page hide the 5th player position during the appropriate date range (case 1613).
 *        3/22/11    Updated FlxRez processing to display only one entry per reservation when a reservation contains multiple time slots. Length of booking is now included as well.
 *        3/03/11    Updated calendar to include lottery requests that a member originated, but is not a part of.
 *        1/20/11    Added dining events & reservations from ForeTees Dining
 *        1/19/11    Members will now be appropriately blocked from accessing restricted times they are a part of.
 *       10/15/10    The Estancia Club (estanciaclub) - Added a message to display a member's count of unused Advance Guest Times (case 1884).
 *        9/16/10    Edina CC 2010 (edina2010) - Do not allow members to access tee times via the tee list
 *        9/10/10    Do not display event and lottery times that have been drug to the tee sheet but not approved
 *        8/10/10    Changes to support passing encrypted tee time info to _slot page
 *        5/18/10    Fix noAccessAfterLottery custom processing to check for lottery_color in teecurr to determine if lottery processed (case 1827).
 *        4/26/10    Brae Burn - do not allow members to access tee times after the lottery has been processed (case 1827).
 *        4/20/10    Bonnie Briar - do not allow members to access tee times after the lottery has been processed (case 1822).
 *        4/16/10    Wollaston GC - do not allow members to access tee times on Monday - all day (case 1819).
 *       12/09/09    When looking for events only check those that are active.
 *        9/22/09    Include scheduled activities - also we now hide things that are empty (no tee times, lotteries, etc)
 *        9/02/09    Do not include lottery tee times that have been pre-booked before lottery has been approved (case 1703).
 *        7/24/09    Add check for mobile user and route to Member_teelist_mobile if so.
 *        4/30/09    Added Status field to event signup display to show if the member is Registered or on the Wait List (case 1587).
 *       10/23/08    Added Wait List signups to list
 *       10/03/08    Check for replacement text for the word "Lottery" when email is for a lottery request.
 *        5/28/08    Do not allow member to access a tee time from calendar if cutoff date/time reached.
 *        3/26/08    Do not allow member to select a tee time that has already passed (case 1431).
 *        7/17/07    Include tee times that were originated by the user in the tee time list.
 *        1/05/07    Changes for TLT system - display notifications
 *       11/20/06    Do not use p5rest parm, only p5 for tee times and lottery requests.
 *        7/11/06    If tee time is during a shotgun event, do not allow link and show as shotgun time.
 *        5/25/05    Recreated from V4 Member_teelist to allow members to list their tee
 *                   times, etc. the old way.
 *
 ***************************************************************************************
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
//import com.foretees.common.getRests;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.common.verifyCustom;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;


public class Member_teelist_list extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

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

        Statement stmt = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmte = null;
        PreparedStatement pstmte2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rsev = null;

        HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

        if (session == null) {

            return;
        }

        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        int mobile = 0;
        boolean dining = false;

        //
        //  See if Mobile user
        //
        try {
            mobile = (Integer) session.getAttribute("mobile");
        } catch (Exception ignore) {
            mobile = 0;
        }

        //
        //   Check for Mobile user and route to proper servlet if so
        //
        if (mobile > 0) {        // if mobile user

            Member_teelist_mobile teelist_mobile = new Member_teelist_mobile();      // create an instance of Member_teelist_mobile so we can call it (static vs non-static)

            teelist_mobile.doGet(req, resp);             // call 'doGet' method in Memebr_teelist_mobile
            return;                                      // exit  
        }


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

        if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {     // if member on the Dining system
            //crumb = "Dining Calendar";
            crumb = "Dining Activity List";
            new_skin = true; // force new skin for dining
            dining = true;
        }
        //
        // See if activities are configured for this club
        //
        //boolean activities_enabled = getActivity.isConfigured(con);

        String user = (String) session.getAttribute("user");      // get username
        String club = (String) session.getAttribute("club");      // get club name
        String caller = (String) session.getAttribute("caller");      // get caller
        String mship = (String) session.getAttribute("mship");             // get member's mship type
        String mtype = (String) session.getAttribute("mtype");             // get member's mtype


        Connection con = SystemUtils.getCon(session);            // get DB connection
        String clubName = SystemUtils.getClubName(con);            // get the full name of this club

        if (con == null) {

            errorPageTop(out, session, req, con, "DB Connection Error");
            out.println("  <br /><br /><h3 class=\"tee_list_ctr\">Database Connection Error</h3>");
            out.println("  <p class=\"tee_list_ctr\"><br />Unable to connect to the Database.");
            out.println("    <br />Please try again later.");
            out.println("    <br /><br />If problem persists, contact your club manager.");
            out.println("    <br /><br />");
            out.println("  <br /></p>");
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);       
            out.close();
            return;
        }


        boolean found = false;

        //String omit = "";
        String ampm = "";
        String teeTimeStr = "";      // used to hold hour and minute time for processing.
        String day = "";
        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";
        String player5 = "";
        String player6 = "";
        String player7 = "";
        String player8 = "";
        String player9 = "";
        String player10 = "";
        String player11 = "";
        String player12 = "";
        String player13 = "";
        String player14 = "";
        String player15 = "";
        String player16 = "";
        String player17 = "";
        String player18 = "";
        String player19 = "";
        String player20 = "";
        String player21 = "";
        String player22 = "";
        String player23 = "";
        String player24 = "";
        String player25 = "";
        String sfb = "";
        String submit = "";
        String name = "";
        String course = "";
        String rest5 = "";
        String rest5_color = "";
        String zone = "";
        String lname = "";
        String rest = "";
        String ename = "";
        String ecolor = "";
        String stime2 = "";
        String lotteryText = "";
        String lotteryName = "";
        String lottery_color = "";

        long date = 0;
        long edate = 0;
        long cdate = 0;
        long c_date = 0;
        long lottid = 0;

        int c_time = 0;
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int hr = 0;
        int min = 0;
        int time = 0;
        int ptime = 0;
        int ctime = 0;
        int count = 0;
        int multi = 0;
        int lottery = 0;
        int lstate = 0;
        int sdays = 0;
        int sdtime = 0;
        int edays = 0;
        int edtime = 0;
        int pdays = 0;
        //int pdtime = 0;
        int slots = 0;
        int advance_days = 0;
        int fives = 0;
        int fivesomes = 0;
        int signUp = 0;
        int fb = 0;
        int etype = 0;
        int wait = 0;
        int person_id = 0;
        int season = 0;

        int tmp_tlt = (Integer) session.getAttribute("tlt");
        boolean IS_TLT = (tmp_tlt == 1) ? true : false;

        boolean events = false;
        boolean restricted = false;
        boolean cutoff = false;
        boolean restrictAllTees = false;         // restrict all tee times
        boolean check5somes = false;

        Gson gson_obj = new Gson(); // Create Json response for later use
        Map<String, Object> hashMap = new LinkedHashMap<String, Object>(); // Create hashmap response for later use


        //
        //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
        //
        //  NOTE:  see same flag in Member_teelist and Member_sheet and Member_teelist_mobile !!!!!!!!!!!!!!!!!!
        //
        boolean noAccessAfterLottery = false;

        if (club.equals("bonniebriar") || club.equals("braeburncc")) {   // add other clubs here!!

            noAccessAfterLottery = true;      // no member access after lottery processed
        }

        if (organization_id != 0) {

            person_id = Utilities.getPersonId(user, con);

        }

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(sess_activity_id, con);


        //
        //   Get options for this club
        //
        try {

            //
            // Get the days in advance and time for advance from the club db
            //
            getClub.getParms(con, parm, sess_activity_id);        // get the club parms

            multi = parm.multi;
            lottery = parm.lottery;
            zone = parm.adv_zone;


            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT SUM(fives) FROM clubparm2");           // check all courses for 5-somes

            if (rs.next()) {

                if (rs.getInt(1) > 0) {
                    fivesomes = 1;
                }

            }

        } catch (Exception exc) {

            errorPageTop(out, session, req, con, "Member My Tee Times - Error");
            dbAccessError(out, club, sess_activity_id, exc, req);
            out.close();
            return;

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt.close();
            } catch (Exception ignore) {
            }

        }

        //
        //  get today's date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
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

        cdate = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd for today's date

        //String mysql_date = yy + "-" + SystemUtils.ensureDoubleDigit(mm) + "-" + SystemUtils.ensureDoubleDigit(dd);

        //
        //  Get tomorrow's date for cutoff test
        //
        cal.add(Calendar.DATE, 1);                     // get next day's date
        yy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH) + 1;
        dd = cal.get(Calendar.DAY_OF_MONTH);

        long tomorrowDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

        yy = 0;
        mm = 0;
        dd = 0;


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
        Common_skin.outputHeader(club, sess_activity_id, "", true, out, req);
        Common_skin.outputBody(club, sess_activity_id, out, req);
        Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
        Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
        Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
        Common_skin.outputPageStart(club, sess_activity_id, out, req);

        Common_skin.outputBreadCrumb(club, sess_activity_id, out, crumb, req);
        Common_skin.outputLogo(club, sess_activity_id, out, req);

        //out.println("<div style=\"clear:both;\"></div>"); // clear the float
        out.println("  <script type=\"text/javascript\">");
        out.println("  function editNotify(pId, pTime, pDate) {");
        out.println("    var f = document.forms['frmEditNotify'];");
        out.println("    f.notifyId.value = pId;");
        out.println("    f.stime.value = pTime;");
        out.println("    f.sdate.value = pDate;");
        out.println("    f.submit();");
        out.println("  }");
        out.println("  </script>");
        out.println("  <form method=post action=MemberTLT_slot name=frmEditNotify id=frmEditNotify>"); //  target=_top
        out.println("    <input type=hidden name=notifyId value=\"\">");
        out.println("    <input type=hidden name=stime value=\"\">");
        out.println("    <input type=hidden name=sdate value=\"\">");
        out.println("    <input type=hidden name=index value=\"995\">");
        out.println("  </form>");

         //out.println("  <div id=\"breadcrumb\"><a href=\"Member_announce\">Home</a> / Tee Times</div>");
         //out.println("  <div id=\"main_ftlogo\"><img src=\"/v5/assets/images/foretees_logo.png\"></div>");

         if (dining) {

               //out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Reservation List</div>");                
               //Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Dining Activity List", req);
               //Common_skin.outputLogo(club, sess_activity_id, out, req);
               //out.println("  <BR><BR>");
            }


        //***********************************************************
        //  Check for Golf events if in that mode
        //***********************************************************  

        //
        // First display either notifications or tee times
        //
        if (parm.foretees_mode != 0 && sess_activity_id == 0) {    // if Golf defined and this user is under Golf

            if (IS_TLT) {


                try {

                    pstmt = con.prepareStatement(
                            "SELECT n.notification_id, "
                            + "DATE_FORMAT(n.req_datetime, '%W, %b. %D') AS pretty_date, "
                            + "DATE_FORMAT(n.req_datetime, '%Y%d%m') AS our_date, "
                            + "DATE_FORMAT(n.req_datetime, '%l:%i %p') AS pretty_time "
                            + "FROM notifications n, notifications_players np "
                            + "WHERE n.notification_id = np.notification_id "
                            + "AND np.username = ? "
                            + "AND DATE(n.req_datetime) >= DATE(now()) "
                            + "ORDER BY n.req_datetime");

                    pstmt.clearParameters();        // clear the parms
                    pstmt.setString(1, user);
                    rs = pstmt.executeQuery();

                    // if we found any then output the header row.
                    rs.last();
                    if (rs.getRow() > 0) {

                        found = true;

                        out.println("<font size=\"3\">");
                        out.println("<b>Your current notifications</b></font><br>");
                        out.println("<font size=\"2\"><br>");

                    }

                    rs.beforeFirst();

                    while (rs.next()) {

                        out.println("<a href=\"javascript:editNotify(" + rs.getInt("notification_id") + ", '" + rs.getString("pretty_time") + "', '" + rs.getString("our_date") + "')\"><font color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
                    }

                } catch (Exception exc) {

                    errorPageTop(out, session, req, con, "Database Error");
                    dbAccessError(out, club, sess_activity_id, exc, req);
                    out.close();
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

            } else {

                //
                // Not a notification club
                //

                if (club.equals("estanciaclub")) {

                    int advTimeCount = 0;

                    advTimeCount = verifyCustom.checkEstanciaAdvTimes(user, con);

                    out.println("<p align=\"center\"><b>You currently have " + ((4 - advTimeCount > 0) ? (4 - advTimeCount) : 0) + " out of 4 Advance Guest Times remaining.</b></p>");
                }

                PreparedStatement pstmt1 = null;

                try {

                    //
                    // search for this user's tee times
                    //
                    pstmt1 = con.prepareStatement(
                            "SELECT date, mm, dd, yy, day, hr, min, time, event, player1, player2, player3, player4, event_type, fb, "
                            + "player5, lottery, courseName, rest5, rest5_color, lottery_color "
                            + "FROM teecurr2 "
                            + "WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                            + "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ?) "
                            + "AND lottery_email = 0 AND player1 <> '' "
                            + "ORDER BY date, time");

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
                    rs = pstmt1.executeQuery();      // execute the prepared stmt

                    rs.last();      // if we found any then output the header row.
                    
                    if (rs.getRow() > 0) {

                        found = true;

                        out.println("  <h3 class=\"tee_list_ctr2\">Your current Tee Times</h3>");
                        out.println("  <p class=\"tee_list_ctr2\"><b>To select a tee time</b>:  Just click on the box containing the time (2nd column).</p>");
                        out.println("  <p class=\"tee_list_small_ctr\">F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("    F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other</p>");
                        out.println("  <table class=\"standard_list_table\">");
                        addTeelistTblTop(out, multi, fivesomes);
                        out.println("    <tbody>");
                    }

                    rs.beforeFirst();


                    //
                    //  Get each additional record and display it
                    //
                    while (rs.next()) {

                        date = rs.getLong(1);
                        mm = rs.getInt(2);
                        dd = rs.getInt(3);
                        yy = rs.getInt(4);
                        day = rs.getString(5);
                        hr = rs.getInt(6);
                        min = rs.getInt(7);
                        time = rs.getInt(8);
                        ename = rs.getString(9);
                        player1 = rs.getString(10);
                        player2 = rs.getString(11);
                        player3 = rs.getString(12);
                        player4 = rs.getString(13);
                        etype = rs.getInt(14);
                        fb = rs.getInt(15);
                        player5 = rs.getString(16);
                        lotteryName = rs.getString(17);
                        course = rs.getString(18);
                        rest5 = rs.getString(19);
                        rest5_color = rs.getString(20);
                        lottery_color = rs.getString(21);

                        //
                        //  Check if a member restriction has been set up to block ALL mem types or mship types for this date & time
                        //
                        restricted = Utilities.checkRests(date, time, fb, course, day, mship, mtype, club, con);

                        ampm = " AM";
                        if (hr == 12) {
                            ampm = " PM";
                        }
                        if (hr > 12) {
                            ampm = " PM";
                            hr = hr - 12;    // convert to conventional time
                        }

                        //
                        //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                        //
                        sfb = "O";       // default Other

                        if (fb == 1) {

                            sfb = "B";
                        }

                        if (fb == 0) {

                            sfb = "F";
                        }

                        submit = "time:" + fb;       // create a name for the submit button

                        //
                        //  Build the HTML for each record found
                        //
                        teeTimeStr = hr + ":" + Utilities.ensureDoubleDigit(min) + ampm;

                        fives = 0;
                        check5somes = true;

                        long month_day = (mm * 100) + dd;     // get adjusted date

                        // 
                        //  Customs 5-some restrictions for members (do not allow access to 5th player)
                        //
                        if (club.equals("rtjgc") || club.equals("coloradogc") || club.equals("rehobothbeachcc") || club.equals("ccyork") || club.equals("chattanoogagcc") || club.equals("lagrangecc")) {

                            check5somes = false;

                        } else if (club.equals("foresthighlands") && (month_day > 424 && month_day < 1001)) {

                            check5somes = false;

                        } else if (club.equals("columbine") && (month_day > 331 && month_day < 1001)) {

                            check5somes = false;

                        } else if (club.equals("estanciaclub") && (month_day <= 515 || month_day >= 1015)) {

                            check5somes = false;

                        } else if (club.equals("lakes") && (mm < 6 || mm > 10)) {

                            check5somes = false;
                        }


                        //
                        //  check if 5-somes allowed on this course
                        //
                        if (check5somes == true && rest5.equals("")) {

                            PreparedStatement pstmt3 = null;

                            try {

                                pstmt3 = con.prepareStatement(
                                        "SELECT fives FROM clubparm2 WHERE courseName = ?");

                                pstmt3.clearParameters();        // clear the parms
                                pstmt3.setString(1, course);
                                rs2 = pstmt3.executeQuery();      // execute the prepared pstmt3

                                if (rs2.next()) {

                                    fives = rs2.getInt(1);
                                }

                            } catch (Exception exc) {
                                
                            } finally {

                               try { rs2.close(); }
                               catch (SQLException ignored) {}

                               try { pstmt3.close(); }
                               catch (SQLException ignored) {}

                            }
                        }

                        out.println("    <tr>");
                        hashMap.clear();
                        hashMap.put("type", "Member_slot");
                        hashMap.put("ttdata", Utilities.encryptTTdata(teeTimeStr + "|" + fb + "|" + user));
                        addTeelistTblHashmapB(hashMap, date, day, course, fives, rest);

                        out.print("      <td>");
                        out.println(day + "&nbsp;" + mm + "/" + dd + "/" + yy + "</td>");

                        // Start tag for Time column, check for shotgun event before close.
                        out.print("      <td>");

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

                               try { rsev.close(); }
                               catch (SQLException ignored) {}

                               try { pstmtev.close(); }
                               catch (SQLException ignored) {}

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

                            out.print("<div class=\"time_slot\">Shotgun at " + stime2 + "</div>");

                        } else {

                            cutoff = false;

                            //
                            //  Check for Member Cutoff specified in Club Options
                            //
                            if (parm.cutoffdays < 99) {        // if option specified

                                if (parm.cutoffdays == 0 && date == cdate && ctime > parm.cutofftime) {  // if cutoff day of and we are doing today and current time is later than cutoff time

                                    cutoff = true;         // indicate no member access

                                } else {

                                    if (parm.cutoffdays == 1 && (date == cdate || (date == tomorrowDate && ctime > parm.cutofftime))) {    // if cutoff day is the day before

                                        cutoff = true;         // indicate no member access
                                    }
                                }
                            }

                            //
                            //  Check for lottery time
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


                            //
                            //  Display time button or just the time
                            //
                            if (!restricted && !cutoff && !(date == cdate && time <= ctime)) {     // if mem can edit tee time
                               out.println("<a class=\"teetime_button standard_button\" href=\"#\" ");
                               out.print("      data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + teeTimeStr + "</a>");
                            } else {
                               out.print("<div class=\"time_slot\">" + teeTimeStr + "</div>");
                            }


                        }       // else (not a shotgun event)

                        // Close tag for Time column.
                        out.println("</td>");
                        if (multi != 0) {

                           out.println("      <td>" + course + "</td>");
                        }

                        out.println("      <td>" + sfb + "</td>");

                        if (player1.equals("")) {

                           player1 = " ";         // need to have space for table display (omits border)
                        }
                        if (player2.equals("")) {

                           player2 = " ";
                        }

                        if (player3.equals("")) {

                           player3 = " ";
                        }
                        if (player4.equals("")) {

                           player4 = " ";
                        }
                        if (player5.equals("")) {

                           player5 = " ";
                        }

                        addTeelistTblPlayers(out, player1, player2, player3, player4, player5, fivesomes, rest5_color);
                        out.println("    </tr>");

                    }    // end of while

                    out.println("    </tbody>");
                    out.println("  </table>    <!-- standard_list_table -->");

                } catch (Exception exc) {

                    errorPageTop(out, session, req, con, "Database Error");
                    dbAccessError(out, club, sess_activity_id, exc, req);          
                    out.close();
                    return;

                } finally {

                    try { rs.close(); }
                    catch (SQLException ignored) {}

                    try { pstmt1.close(); }
                    catch (SQLException ignored) {}

                }

            } // end if notification or tee times


            //
            //*****************************************************************
            // now check for any Golf events that member is signed up for
            //*****************************************************************
            //
            try {

                pstmte2 = con.prepareStatement(
                        "SELECT name, courseName "
                        + "FROM events2b WHERE (date >= ? OR (season = 1 AND (c_date > ? OR (c_date = ? AND c_time > ?)))) AND activity_id = 0 AND inactive = 0");       // look for Golf events

                pstmte2.clearParameters();
                pstmte2.setLong(1, cdate);
                pstmte2.setLong(2, cdate);
                pstmte2.setLong(3, cdate);
                pstmte2.setInt(4, ctime);
                rs2 = pstmte2.executeQuery();

                loop1:
                while (rs2.next()) {

                    name = rs2.getString(1);
                    course = rs2.getString(2);

                    // check for signups
                    pstmte = con.prepareStatement(
                            "SELECT name "
                            + "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                            + "OR username5 = ?) AND name = ? AND courseName = ? AND inactive = 0");

                    pstmte.clearParameters();        // clear the parms
                    pstmte.setString(1, user);
                    pstmte.setString(2, user);
                    pstmte.setString(3, user);
                    pstmte.setString(4, user);
                    pstmte.setString(5, user);
                    pstmte.setString(6, name);
                    pstmte.setString(7, course);
                    rs = pstmte.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        events = true;
                    }
                    pstmte.close();

                    if (events == true) {

                        break loop1;
                    }
                }        // end of WHILE evntsup
                pstmte2.close();

                //
                //  if any events found above, then display them here (search over again)
                //
                if (events == true) {

                    found = true;

                     out.println("");
                     out.println("  <br />");
                     out.println("  <h3 class=\"tee_list_ctr2\">Events for which you are currently registered:</h3>");

                     //
                     //  Build html to display any events
                     //
                     out.println("  <p class=\"tee_list_ctr2\"><b>To select the event</b>:  Just click on the box containing the event name.</p>");
                     out.println("  <p class=\"tee_list_ctr3\">If you cannot select the event, then it is currently past the sign-up date.</p>");
                     out.println("  <table class=\"standard_list_table\">");
                     out.println("    <thead>");
                     out.println("    <tr>");
                     out.println("      <th>Event Name</th>");
                     out.println("      <th>Date & Time</th>");

                     if (multi != 0) {

                           out.println("      <th>Course Name</th>");
                     }

                     out.println("      <th>Player 1</th>");
                     out.println("      <th>Player 2</th>");
                     out.println("      <th>Player 3</th>");
                     out.println("      <th>Player 4</th>");
                     out.println("      <th>Player 5</th>");
                     out.println("      <th>Status</th>");
                     out.println("    </tr></thead>");
                     out.println("    <tbody>");

 
                    //
                    // now list the Golf events that member is signed up for
                    //
                    pstmte2 = con.prepareStatement(
                            "SELECT name, courseName, date, year, month, day, act_hr, act_min, signUp, c_date, c_time, color, season "
                            + "FROM events2b WHERE (date >= ? OR (season = 1 AND (c_date > ? OR (c_date = ? AND c_time > ?)))) AND activity_id = 0 AND inactive = 0 "
                            + "ORDER BY date, act_hr, act_min");       // look for Golf events

                    pstmte2.clearParameters();        // clear the parms
                    pstmte2.setLong(1, cdate);
                    pstmte2.setLong(2, cdate);
                    pstmte2.setLong(3, cdate);
                    pstmte2.setInt(4, ctime);
                    rs2 = pstmte2.executeQuery();      // execute the prepared stmt
               
                    while (rs2.next()) {

                        name = rs2.getString("name");
                        course = rs2.getString("courseName");
                        edate = rs2.getLong("date");
                        yy = rs2.getInt("year");
                        mm = rs2.getInt("month");
                        dd = rs2.getInt("day");
                        hr = rs2.getInt("act_hr");
                        min = rs2.getInt("act_min");
                        signUp = rs2.getInt("signUp");
                        c_date = rs2.getLong("c_date");
                        c_time = rs2.getInt("c_time");
                        ecolor = rs2.getString("color");
                        season = rs2.getInt("season");


                        //
                        //  Now look for signups in this event
                        //
                        pstmte = con.prepareStatement(
                                "SELECT player1, player2, player3, player4, player5, wait "
                                + "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                + "OR username5 = ?) AND name = ? AND courseName = ? AND inactive = 0 ORDER BY c_date, c_time");

                        pstmte.clearParameters();        // clear the parms
                        pstmte.setString(1, user);
                        pstmte.setString(2, user);
                        pstmte.setString(3, user);
                        pstmte.setString(4, user);
                        pstmte.setString(5, user);
                        pstmte.setString(6, name);
                        pstmte.setString(7, course);
                        rs = pstmte.executeQuery();      // execute the prepared stmt

                        while (rs.next()) {                        

                            player1 = rs.getString(1);
                            player2 = rs.getString(2);
                            player3 = rs.getString(3);
                            player4 = rs.getString(4);
                            player5 = rs.getString(5);
                            wait = rs.getInt(6);

                            if (edate >= cdate || season == 1) {          // if event is today or later - we found one

                                //
                                //  Create time values
                                //
                                ampm = "AM";

                                if (hr == 0) {

                                    hr = 12;                 // change to 12 AM (midnight)

                                } else {

                                    if (hr == 12) {

                                        ampm = "PM";         // change to Noon
                                    }
                                }
                                if (hr > 12) {

                                    hr = hr - 12;
                                    ampm = "PM";             // change to 12 hr clock
                                }

                                if (player1.equals("")) {

                                    player1 = " - ";
                                }
                                if (player2.equals("")) {

                                    player2 = " - ";
                                }
                                if (player3.equals("")) {

                                    player3 = " - ";
                                }
                                if (player4.equals("")) {

                                    player4 = " - ";
                                }
                                if (player5.equals("")) {

                                    player5 = " - ";
                                }

                                 teeTimeStr = hr + ":" + Utilities.ensureDoubleDigit(min) + ampm;
                                 out.println("    <tr>");
                                 out.print("      <td>");
                                 hashMap.clear();
                                 hashMap.put("type", "Member_events2");

                                 if (signUp != 0 && c_date >= cdate) {     // if members can sign up for this event

                                       if ((c_date > cdate) || (c_time > ctime)) {

                                          hashMap.put("name", name);
                                          hashMap.put("course", course);
                                          hashMap.put("index", 995);
                                          out.println("<a class=\"event_button standard_button\" href=\"#\"");
                                          out.print("      data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\" style=\"background-color:" + ecolor + ";\">" + name + "</a>");

                                       } else {

                                          out.print("<div class=\"time_slot\">" + name + "</div>");
                                       }

                                 } else {

                                       out.print("<div class=\"time_slot\">" + name + "</div>");
                                 }
                                 out.println("</td>");

                                 out.println("      <td nowrap>" + mm + "/" + dd + "/" + yy + "<br />" + teeTimeStr + "</td>");

                                 if (multi != 0) {

                                       out.println("      <td>" + course + "</td>");
                                 }

                                 out.println("      <td>" + player1 + "</td>");
                                 out.println("      <td>" + player2 + "</td>");
                                 out.println("      <td>" + player3 + "</td>");
                                 out.println("      <td>" + player4 + "</td>");
                                 out.println("      <td>" + player5 + "</td>");

                                 out.print("      <td>");

                                 if (wait == 0) {
                                       out.print("Registered");
                                 } else {
                                       out.print("Wait List");
                                 }
                                 out.println("</td>");
                                 out.println("    </tr>");

                            }     // end of IF event date is current

                        }     // end of WHILE signups
                        pstmte.close();

                    }        // end of WHILE events

                    pstmte2.close();

                    if (!new_skin) {
                        out.println("</font></table>");        // done with events table               
                    } else {
                        out.println("    </tbody>");
                        out.println("  </table>    <!-- standard_list_table -->");        // done with events table
                    }

                }    // end of IF any events


                //
                // ****************************************************************
                // use the name to search for lottery requests (if supported)
                // ****************************************************************
                //
                if (lottery > 0) {

                    //
                    //  Get the replacement text for the word "Lottery" if club requested one - added to club5 by Support Club Options
                    //
                    lotteryText = getClub.getLotteryText(con);         // get replacement text for "Lottery" if provided  


                    PreparedStatement pstmtl = con.prepareStatement(
                            "SELECT name, date, mm, dd, yy, day, hr, min, time, player1, player2, player3, player4, "
                            + "player5, player6, player7, player8, player9, player10, player11, player12, player13, player14, "
                            + "player15, player16, player17, player18, player19, player20, player21, player22, player23, "
                            + "player24, player25, fb, courseName, id "
                            + "FROM lreqs3 "
                            + "WHERE user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR "
                            + "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR "
                            + "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR "
                            + "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR "
                            + "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? OR "
                            + "orig_by LIKE ?"
                            + "ORDER BY date, time");

                    pstmtl.clearParameters();        // clear the parms
                    pstmtl.setString(1, user);
                    pstmtl.setString(2, user);
                    pstmtl.setString(3, user);
                    pstmtl.setString(4, user);
                    pstmtl.setString(5, user);
                    pstmtl.setString(6, user);
                    pstmtl.setString(7, user);
                    pstmtl.setString(8, user);
                    pstmtl.setString(9, user);
                    pstmtl.setString(10, user);
                    pstmtl.setString(11, user);
                    pstmtl.setString(12, user);
                    pstmtl.setString(13, user);
                    pstmtl.setString(14, user);
                    pstmtl.setString(15, user);
                    pstmtl.setString(16, user);
                    pstmtl.setString(17, user);
                    pstmtl.setString(18, user);
                    pstmtl.setString(19, user);
                    pstmtl.setString(20, user);
                    pstmtl.setString(21, user);
                    pstmtl.setString(22, user);
                    pstmtl.setString(23, user);
                    pstmtl.setString(24, user);
                    pstmtl.setString(25, user);
                    pstmtl.setString(26, user);
                    rs = pstmtl.executeQuery();      // execute the prepared stmt

                    // if we found any then output the header row.
                    rs.last();
                    if (rs.getRow() > 0) {

                        found = true;

                        //
                        //   build the table for the display
                        //
                        if (!new_skin) {
                            out.println("</td></tr>");                                 // terminate previous col/row
                            out.println("<tr><td align=\"center\" valign=\"top\">");

                            out.println("<font size=\"3\"><br><br>");
                            if (club.equals("oldoaks")) {
                                out.println("<b>Current Tee Time Requests</b><br>");
                                out.println("</font><font size=\"2\">");
                                out.println("<b>To select a request</b>:  Just click on the box containing the time (if allowed).");
                            } else if (!lotteryText.equals("")) {
                                out.println("<b>Current " + lotteryText + "s</b><br>");
                                out.println("</font><font size=\"2\">");
                                out.println("<b>To select a " + lotteryText + "</b>:  Just click on the box containing the time (if allowed).");
                            } else {
                                out.println("<b>Current Lottery Requests</b><br>");
                                out.println("</font><font size=\"2\">");
                                out.println("<b>To select a lottery request</b>:  Just click on the box containing the time (if allowed).");
                            }
                            out.println("</font><font size=\"2\">");
                            out.println("<br>F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other");
                            out.println("</font><font size=\"2\">");

                            out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\">");
                            out.println("<tr bgcolor=\"#336633\"><td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>Date</b></u>");
                            out.println("</font></td>");

                            out.println("<td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>Time</b></u>");
                            out.println("</font></td>");

                            if (multi != 0) {
                                out.println("<td align=center>");
                                out.println("<font color=\"#ffffff\" size=\"2\">");
                                out.println("<u><b>Course</b></u>");
                                out.println("</font></td>");
                            }

                            out.println("<td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>F/B</b></u>");
                            out.println("</font></td>");

                            out.println("<td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>Player 1</b></u>");
                            out.println("</font></td>");

                            out.println("<td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>Player 2</b></u>");
                            out.println("</font></td>");

                            out.println("<td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>Player 3</b></u>");
                            out.println("</font></td>");

                            out.println("<td align=center>");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<u><b>Player 4</b></u>");
                            out.println("</font></td>");

                            if (fivesomes != 0) {
                                out.println("<td align=center>");
                                out.println("<font color=\"#ffffff\" size=\"2\">");
                                out.println("<u><b>Player 5</b></u>");
                                out.println("</font></td>");
                            }

                            out.println("</tr>");
                        } // if (!new_skin)
                        else {
                            out.println("");
                            out.println("  <br />");
                            if (club.equals("oldoaks")) {
                                out.println("  <h3 class=\"tee_list_ctr2\">Current Tee Time Requests</h3>");
                                out.println("  <p class=\"tee_list_ctr2\"><b>To select a request</b>:  Just click on the box containing the time (if allowed).</p>");
                            } else if (!lotteryText.equals("")) {
                                out.println("  <h3 class=\"tee_list_ctr2\">Current " + lotteryText + "s</h3>");
                                out.println("  <p class=\"tee_list_ctr2\"><b>To select a " + lotteryText + "</b>:  Just click on the box containing the time (if allowed).</p>");
                            } else {
                                out.println("  <h3 class=\"tee_list_ctr2\">Current Lottery Requests</h3>");
                                out.println("  <p class=\"tee_list_ctr2\"><b>To select a lottery request</b>:  Just click on the box containing the time (if allowed).</p>");
                            }
                            out.println("  <p class=\"tee_list_small_ctr\">F/B Legend:&nbsp;&nbsp;&nbsp;&nbsp;");
                            out.println("    F = Front 9, &nbsp;&nbsp;B = Back 9, &nbsp;&nbsp;O = Other</p>");
                            out.println("  <table class=\"standard_list_table\">");
                            addTeelistTblTop(out, multi, fivesomes);
                            out.println("    <tbody>");

                        } // else (if !new_skin)
                    }

                    rs.beforeFirst();

                    //
                    //  Get each record and display it
                    //
                    count = 0;             // number of records found

                    while (rs.next()) {

                        lname = rs.getString(1);
                        date = rs.getLong(2);
                        mm = rs.getInt(3);
                        dd = rs.getInt(4);
                        yy = rs.getInt(5);
                        day = rs.getString(6);
                        hr = rs.getInt(7);
                        min = rs.getInt(8);
                        time = rs.getInt(9);
                        player1 = rs.getString(10);
                        player2 = rs.getString(11);
                        player3 = rs.getString(12);
                        player4 = rs.getString(13);
                        player5 = rs.getString(14);
                        player6 = rs.getString(15);
                        player7 = rs.getString(16);
                        player8 = rs.getString(17);
                        player9 = rs.getString(18);
                        player10 = rs.getString(19);
                        player11 = rs.getString(20);
                        player12 = rs.getString(21);
                        player13 = rs.getString(22);
                        player14 = rs.getString(23);
                        player15 = rs.getString(24);
                        player16 = rs.getString(25);
                        player17 = rs.getString(26);
                        player18 = rs.getString(27);
                        player19 = rs.getString(28);
                        player20 = rs.getString(29);
                        player21 = rs.getString(30);
                        player22 = rs.getString(31);
                        player23 = rs.getString(32);
                        player24 = rs.getString(33);
                        player25 = rs.getString(34);
                        fb = rs.getInt(35);
                        course = rs.getString(36);
                        lottid = rs.getLong(37);

                        count++;

                        ampm = " AM";
                        if (hr == 12) {
                            ampm = " PM";
                        }
                        if (hr > 12) {
                            ampm = " PM";
                            hr = hr - 12;    // convert to conventional time
                        }

                        if (player1.equals("")) {

                            player1 = " ";       // make it a space for table display
                        }
                        if (player2.equals("")) {

                            player2 = " ";       // make it a space for table display
                        }
                        if (player3.equals("")) {

                            player3 = " ";       // make it a space for table display
                        }
                        if (player4.equals("")) {

                            player4 = " ";       // make it a space for table display
                        }
                        if (player5.equals("")) {

                            player5 = " ";       // make it a space for table display
                        }
                        if (player6.equals("")) {

                            player6 = " ";       // make it a space for table display
                        }
                        if (player7.equals("")) {

                            player7 = " ";       // make it a space for table display
                        }
                        if (player8.equals("")) {

                            player8 = " ";       // make it a space for table display
                        }
                        if (player9.equals("")) {

                            player9 = " ";       // make it a space for table display
                        }
                        if (player10.equals("")) {

                            player10 = " ";       // make it a space for table display
                        }
                        if (player11.equals("")) {

                            player11 = " ";       // make it a space for table display
                        }
                        if (player12.equals("")) {

                            player12 = " ";       // make it a space for table display
                        }
                        if (player13.equals("")) {

                            player13 = " ";       // make it a space for table display
                        }
                        if (player14.equals("")) {

                            player14 = " ";       // make it a space for table display
                        }
                        if (player15.equals("")) {

                            player15 = " ";       // make it a space for table display
                        }
                        if (player16.equals("")) {

                            player16 = " ";       // make it a space for table display
                        }
                        if (player17.equals("")) {

                            player17 = " ";       // make it a space for table display
                        }
                        if (player18.equals("")) {

                            player18 = " ";       // make it a space for table display
                        }
                        if (player19.equals("")) {

                            player19 = " ";       // make it a space for table display
                        }
                        if (player20.equals("")) {

                            player20 = " ";       // make it a space for table display
                        }
                        if (player21.equals("")) {

                            player21 = " ";       // make it a space for table display
                        }
                        if (player22.equals("")) {

                            player22 = " ";       // make it a space for table display
                        }
                        if (player23.equals("")) {

                            player23 = " ";       // make it a space for table display
                        }
                        if (player24.equals("")) {

                            player24 = " ";       // make it a space for table display
                        }
                        if (player25.equals("")) {

                            player25 = " ";       // make it a space for table display
                        }

                        //
                        //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                        //
                        sfb = "O";       // default Other

                        if (fb == 1) {

                            sfb = "B";
                        }

                        if (fb == 0) {

                            sfb = "F";
                        }

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
                        if (fivesomes != 0) {                      // if 5-somes supported for any courses 

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

                        //
                        //  get the slots value and determine the current state for this lottery
                        //
                        PreparedStatement pstmt7d = con.prepareStatement(
                                "SELECT sdays, sdtime, edays, edtime, pdays, ptime, slots "
                                + "FROM lottery3 WHERE name = ?");

                        pstmt7d.clearParameters();          // clear the parms
                        pstmt7d.setString(1, lname);

                        rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                        if (rs2.next()) {

                            sdays = rs2.getInt(1);         // days in advance to start taking requests
                            sdtime = rs2.getInt(2);
                            edays = rs2.getInt(3);         // ...stop taking reqs
                            edtime = rs2.getInt(4);
                            pdays = rs2.getInt(5);         // ....to process reqs
                            ptime = rs2.getInt(6);
                            slots = rs2.getInt(7);

                        }                  // end of while
                        pstmt7d.close();

                        //
                        //    Determine which state we are in (before req's, during req's, before process, after process)
                        //
                        //  Get the current time
                        //
                        Calendar cal3 = new GregorianCalendar();    // get the current time of the day

                        if (zone.equals("Eastern")) {         // Eastern Time = +1 hr

                            cal3.add(Calendar.HOUR_OF_DAY, 1);         // roll ahead 1 hour (rest should adjust)
                        }

                        if (zone.equals("Mountain")) {        // Mountain Time = -1 hr

                            cal3.add(Calendar.HOUR_OF_DAY, -1);        // roll back 1 hour (rest should adjust)
                        }

                        if (zone.equals("Pacific")) {         // Pacific Time = -2 hrs

                            cal3.add(Calendar.HOUR_OF_DAY, -2);        // roll back 2 hours (rest should adjust)
                        }

                        int cal_hour = cal3.get(Calendar.HOUR_OF_DAY);  // 00 - 23 (military time - adjusted for time zone)
                        cal_min = cal3.get(Calendar.MINUTE);
                        int curr_time = cal_hour * 100;
                        curr_time = curr_time + cal_min;                // create military time

                        //
                        //  determine the number of days in advance of the req'd tee time we currently are
                        //
                        int cal_yy = cal3.get(Calendar.YEAR);
                        int cal_mm = cal3.get(Calendar.MONTH);
                        int cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

                        cal_mm++;                            // month starts at zero
                        advance_days = 0;

                        while (cal_mm != mm || cal_dd != dd || cal_yy != yy) {

                            cal3.add(Calendar.DATE, 1);                // roll ahead 1 day untill a match found

                            cal_yy = cal3.get(Calendar.YEAR);
                            cal_mm = cal3.get(Calendar.MONTH);
                            cal_dd = cal3.get(Calendar.DAY_OF_MONTH);

                            cal_mm++;                            // month starts at zero
                            advance_days++;
                        }

                        //
                        //  now check the day and time values
                        //
                        if (advance_days > sdays) {       // if we haven't reached the start day yet

                            lstate = 1;                    // before time to take requests

                        } else {

                            if (advance_days == sdays) {   // if this is the start day

                                if (curr_time >= sdtime) {   // have we reached the start time?

                                    lstate = 2;              // after start time, before stop time to take requests

                                } else {

                                    lstate = 1;              // before time to take requests
                                }
                            } else {                        // we are past the start day

                                lstate = 2;                 // after start time, before stop time to take requests
                            }

                            if (advance_days == edays) {   // if this is the stop day

                                if (curr_time >= edtime) {   // have we reached the stop time?

                                    lstate = 3;              // after start time, before stop time to take requests
                                }
                            }

                            if (advance_days < edays) {   // if we are past the stop day

                                lstate = 3;                // after start time, before stop time to take requests
                            }
                        }

                        if (lstate == 3) {                // if we are now in state 3, check for state 4

                            if (advance_days == pdays) {   // if this is the process day

                                if (curr_time >= ptime) {    // have we reached the process time?

                                    lstate = 4;              // after process time
                                }
                            }

                            if (advance_days < pdays) {   // if we are past the process day

                                lstate = 4;                // after process time
                            }
                        }

                        if (lstate == 4) {                // if we are now in state 4, check for state 5

                            PreparedStatement pstmt12 = con.prepareStatement(
                                    "SELECT mm FROM lreqs3 "
                                    + "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

                            pstmt12.clearParameters();        // clear the parms
                            pstmt12.setString(1, lname);
                            pstmt12.setLong(2, date);
                            pstmt12.setString(3, course);
                            rs2 = pstmt12.executeQuery();

                            if (!rs2.next()) {             // if none waiting approval

                                lstate = 5;                // state 5 - after process & approval time
                            }
                            pstmt12.close();
                        }

                        submit = "time:" + fb;       // create a name for the submit button

                        //
                        //  Build the HTML for each record found
                        //
                        teeTimeStr = hr + ":" + Utilities.ensureDoubleDigit(min) + ampm;
                        out.println("    <tr>");

                        if (lstate == 2) {       // if still ok to process lottery requests

                           hashMap.clear();
                           hashMap.put("type", "Member_lott");
                           addTeelistTblHashmapB(hashMap, date, day, course, fives, rest);
                           addTeelistTblHashmapL(hashMap, lname, lottid, slots, lstate);
                           hashMap.put("stime", hr + ":" + String.format("%02d", min) + ampm);
                           hashMap.put("fb", fb);

                           out.println("      <td>" + day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</td>");

                           out.println("      <td><a class=\"lottery_button standard_button\" href=\"#\" ");
                           out.print("      data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + teeTimeStr + "</a>");

                           out.println("</td>");

                        } else {

                           if (lstate == 5) {       // if lottery has already been processed

                              hashMap.clear();
                              hashMap.put("type", "Member_slot");
                              hashMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + String.format("%02d", min) + ampm + "|" + fb + "|" + user));
                              addTeelistTblHashmapB(hashMap, date, day, course, fives, rest);

                              out.println("      <td>" + day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</td>");

                              out.println("      <td>");
                              out.println("    <a class=\"teetime_button standard_button\" href=\"#\"");
                              out.print("    data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + teeTimeStr + "</a>");
                              out.println("</td>");

                           } else {

                              out.println("      <td>" + day + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</td>");

                              out.println("      <td><div class=\"time_slot\">" + teeTimeStr + "</div></td>");
                           }
                        }

                        if (multi != 0) {
                           out.println("      <td>" + course + "</td>");
                        }

                        out.println("      <td>" + sfb + "</td>");
                        addTeelistTblPlayers(out, player1, player2, player3, player4, player5, fives, "");

                        out.println("    </tr>");

                        if (fives != 0) {

                           //
                           //  check if there are more than 5 players registered
                           //
                           if (!player6.equals(" ") || !player7.equals(" ") || !player8.equals(" ") || !player9.equals(" ") || !player10.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player6, player7, player8, player9, player10, fives, "");
                              out.println("    </tr>");
                           }

                           if (!player11.equals(" ") || !player12.equals(" ") || !player13.equals(" ") || !player14.equals(" ") || !player15.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player11, player12, player13, player14, player15, fivesomes, "");
                              out.println("    </tr>");
                           }

                           if (!player16.equals(" ") || !player17.equals(" ") || !player18.equals(" ") || !player19.equals(" ") || !player20.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player16, player17, player18, player19, player20, fivesomes, "");
                              out.println("    </tr>");
                           }

                           if (!player21.equals(" ") || !player22.equals(" ") || !player23.equals(" ") || !player24.equals(" ") || !player25.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player21, player22, player23, player24, player25, fivesomes, "");
                              out.println("    </tr>");
                           }

                        } else {   // no 5-somes on this course

                           //
                           //  check if there are more than 4 players registered
                           //
                           if (!player5.equals(" ") || !player6.equals(" ") || !player7.equals(" ") || !player8.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player5, player6, player7, player8, " ", fivesomes, "");
                              out.println("    </tr>");
                           }

                           if (!player9.equals(" ") || !player10.equals(" ") || !player11.equals(" ") || !player12.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player9, player10, player11, player12, " ", fivesomes, "");
                              out.println("    </tr>");
                           }

                           if (!player13.equals(" ") || !player14.equals(" ") || !player15.equals(" ") || !player16.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player13, player14, player15, player16, " ", fivesomes, "");
                              out.println("    </tr>");
                           }

                           if (!player17.equals(" ") || !player18.equals(" ") || !player19.equals(" ") || !player20.equals(" ")) {

                              out.println("    <tr>");
                              addTeelistTblEmpty(out, multi);
                              addTeelistTblPlayers(out, player17, player18, player19, player20, " ", fivesomes, "");
                              out.println("    </tr>");
                           }
                        }     // end of IF 5-somes      

                    }    // end of while

                    pstmtl.close();

                    if (!new_skin) {
                        out.println("</font></table>");
                    } else {
                        if (count > 0) {       // entries found close  table.
                            out.println("    </tbody>");
                            out.println("  </table>    <!-- standard_list_table  -->");
                        }
                    }
                    /*
                    if (count == 0) {
                    out.println("<font size=\"2\">");
                    if (club.equals( "oldoaks" )) {
                    out.println("<p align=\"center\">You are currently not included in any Tee Time Requests at this time.</p>");
                    } else if (!lotteryText.equals("")) {
                    out.println("<p align=\"center\">You are currently not included in any " +lotteryText+ "s at this time.</p>");
                    } else {
                    out.println("<p align=\"center\">You are currently not included in any Lottery Requests at this time.</p>");
                    }
                    out.println("</font>");
                    }
                     */

                } // end if lottery supported by club




                boolean foundWait = false;
                date = SystemUtils.getDate(con);

                pstmt = con.prepareStatement(
                        "SELECT wl.wait_list_id, wl.course, wls.date, wls.ok_stime, wls.ok_etime, wls.wait_list_signup_id, wl.color, "
                        + "DATE_FORMAT(wls.date, '%c/%d/%Y') AS date2, DATE_FORMAT(wls.date, '%W') AS day_name, DATE_FORMAT(wls.date, '%Y%m%d') AS dateymd "
                        + "FROM wait_list_signups wls "
                        + "LEFT OUTER JOIN wait_list wl ON wls.wait_list_id = wl.wait_list_id "
                        + "LEFT OUTER JOIN wait_list_signups_players wlp ON wlp.wait_list_signup_id = wls.wait_list_signup_id "
                        + "WHERE wls.date >= ? AND wlp.username = ? AND converted = 0 "
                        + "ORDER BY wls.date");

                pstmt.clearParameters();
                pstmt.setLong(1, date);
                pstmt.setString(2, user);

                rs = pstmt.executeQuery();


                // if we found any then output the header row.
                rs.last();
                if (rs.getRow() > 0) {

                    foundWait = true; // flag to indicate we need to close the header table
                    found = true;

                    //
                    // Check for any wait list signups
                    //
                     out.println("  <br />");
                     out.println("  <h3 class=\"tee_list_ctr2\">Current Wait List Sign-ups</h3>");
                     out.println("");

                     out.println("  <p class=\"tee_list_ctr3\"><b>To select the wait list</b>:  Just click on the box containing the date.</p>");
                     out.println("");
                     out.println("  <table class=\"standard_list_table\">");
                     out.println("    <thead>");
                     out.println("    <tr>");
                     out.println("      <th>Date</th>");
                     out.println("      <th>Time</th>");
                     if (multi != 0) {
                           out.println("      <th>Course Name</th>");
                     }
                     out.println("      <th>Player 1</th>");
                     out.println("      <th>Player 2</th>");
                     out.println("      <th>Player 3</th>");
                     out.println("      <th>Player 4</th>");
                     out.println("      <th>Player 5</th>");
                     out.println("    </tr></thead>");

                     out.println("    <tbody>");
                }

                rs.beforeFirst();

                while (rs.next()) {

                    player1 = "&nbsp;";
                    player2 = "&nbsp;";
                    player3 = "&nbsp;";
                    player4 = "&nbsp;";
                    player5 = "&nbsp;";

                    PreparedStatement pstmtp = con.prepareStatement(
                            "SELECT player_name "
                            + "FROM wait_list_signups_players "
                            + "WHERE wait_list_signup_id = ? "
                            + "ORDER BY pos");

                    pstmtp.clearParameters();
                    pstmtp.setInt(1, rs.getInt("wait_list_signup_id"));
                    ResultSet rsp = pstmtp.executeQuery();

                    if (rsp.next()) {
                        player1 = rsp.getString(1);
                    }
                    if (rsp.next()) {
                        player2 = rsp.getString(1);
                    }
                    if (rsp.next()) {
                        player3 = rsp.getString(1);
                    }
                    if (rsp.next()) {
                        player4 = rsp.getString(1);
                    }
                    if (rsp.next()) {
                        player5 = rsp.getString(1);
                    }

                    pstmtp.close();

                    if (!new_skin) {
                        out.println("<tr>");
                        out.print("<td align=\"center\"><font size=\"2\">");      // date col
                        out.print(rs.getString("date2"));
                        //out.println("<button onclick=\"top.location.href='Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=0&day_name="+rs.getString("day_name")+"&course=" +rs.getString("course")+"&returnCourse=" +rs.getString("course")+"'\">" + rs.getString("date2") + "</button>");
                        out.println("</font></td>");

                        out.print("<form onsubmit='return false;'><td align=\"center\"><font size=\"2\">");      // time col
                        out.print("<button onclick=\"top.location.href='Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=995&day=" + rs.getString("day_name") + "&course=" + rs.getString("course") + "&returnCourse=" + rs.getString("course") + "'\">");
                        out.print(SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
                        out.print("</button>");
                        out.println("</font></td></form>");

                        if (multi != 0) {
                            out.print("<td align=\"center\"><font size=\"2\">");   // course
                            out.print(rs.getString("course"));
                            out.println("</font></td>");
                        }

                        out.print("<td align=\"center\"><font size=\"2\">");      // player 1
                        out.print(player1);
                        out.println("</font></td>");

                        out.print("<td align=\"center\"><font size=\"2\">");      // player 2
                        out.print(player2);
                        out.println("</font></td>");

                        out.print("<td align=\"center\"><font size=\"2\">");      // player 3
                        out.print(player3);
                        out.println("</font></td>");

                        out.print("<td align=\"center\"><font size=\"2\">");      // player 4
                        out.print(player4);
                        out.println("</font></td>");

                        out.print("<td align=\"center\"><font size=\"2\">");      // player 5
                        out.print(player5);
                        out.println("</font></td>");

                        out.println("</tr>");
                    } // if (!new_skin)
                    else {
                        hashMap.clear();
                        hashMap.put("type", "Member_waitlist");
                        hashMap.put("waitListId", rs.getInt("wait_list_id"));
                        hashMap.put("course", rs.getString("course"));
                        hashMap.put("returnCourse", rs.getString("course"));
                        hashMap.put("index", 995);
                        hashMap.put("date", rs.getInt("dateymd"));
                        
                        out.println("    <tr>");
                        out.print("      <td>");      // date col
                        out.print(rs.getString("day_name") + " " + rs.getString("date2"));
                        //out.println("<button onclick=\"top.location.href='Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=0&day_name="+rs.getString("day_name")+"&course=" +rs.getString("course")+"&returnCourse=" +rs.getString("course")+"'\">" + rs.getString("date2") + "</button>");
                        out.println("</td>");

                        out.print("<td><a style=\"background-color:"+rs.getString("color")+";\" class=\"waitlist_button standard_button\" href=\"#\" ");
                        out.print("data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")) + "</a></td>");

                        //out.println("      <form onsubmit='return false;'><td>");      // time col
                        //out.println("      <button onclick=\"top.location.href='Member_waitlist?waitListId=" + rs.getInt("wait_list_id") + "&date=" + rs.getInt("dateymd") + "&index=995&day="+rs.getString("day_name")+"&course=" +rs.getString("course")+"&returnCourse=" +rs.getString("course")+"'\">");
                        //out.print("      " + SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
                        //out.println("</button>");
                        //out.println("      </td></form>");

                        if (multi != 0) {
                            out.println("      <td>" + rs.getString("course") + "</td>");   // course
                        }

                        addTeelistTblPlayers(out, player1, player2, player3, player4, player5, 1, "");
                        out.println("    </tr>");
                    } // else (if !new_skin)

                } // end loop of tee times rs

                pstmt.close();


                if (!new_skin) {
                    if (foundWait) {
                        out.println("</table>");
                    }
                } else {
                    if (foundWait) {
                        out.println("    </tbody>");
                        out.println("  </table>    <!-- standard_list_table -->");
                    }
                }


            } catch (Exception exc) {

                if (!new_skin) {
                    out.println(SystemUtils.HeadTitle("Database Error"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
                    out.println("<BR><BR><H3>Database Access Error</H3>");
                    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                    out.println("<BR>Error:" + exc.getMessage());
                    out.println("<BR>Error:" + exc.toString());
                    out.println("<BR><BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, contact your club manager.");
                    out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
                    out.println("</CENTER></BODY></HTML>");
                } else {
                    errorPageTop(out, session, req, con, "Database Error");
                    out.println("  <br /><br /><h3 class=\"tee_list_ctr\">Database Access Error</h3>");
                    out.println("  <p class=\"tee_list_ctr\"><br />Sorry, we are unable to access the database at this time.");
                    out.println("    <br />Error:" + exc.getMessage());
                    out.println("    <br />Error:" + exc.toString());
                    out.println("    <br /><br />Please try again later.");
                    out.println("    <br /><br />If problem persists, contact your club manager.");
                    out.println("    <br /><br />");
                    out.println("  </p>");
                    Common_skin.outputPageEnd(club, sess_activity_id, out, req);
                } // else (if !new_skin)             

                out.close();
                return;

            }

        }    // end of IF Golf     


        //**********************************
        // END OF GOLF
        //**********************************



        //**********************************
        // Check Activities if in that mode
        //**********************************


        if (parm.genrez_mode != 0 && sess_activity_id > 0 && sess_activity_id != ProcessConstants.DINING_ACTIVITY_ID) {    // if activities defined and this user is under Activities

            //
            // Check for any activities
            //
            try {

                pstmt = con.prepareStatement(
                        "SELECT * FROM ("
                        + "SELECT a.sheet_id, a.activity_id, ac.interval, "
                        + "IF(a.related_ids <> '', a.related_ids, a.sheet_id) as related_ids, "
                        + "DATE_FORMAT(a.date_time, '%W, %b. %D') AS pretty_date, "
                        + "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, "
                        + "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time "
                        + "FROM activity_sheets a, activity_sheets_players ap, activities ac "
                        + "WHERE a.sheet_id = ap.activity_sheet_id "
                        + "AND ac.activity_id = a.activity_id "
                        + "AND ap.username = ? "
                        + "AND DATE(a.date_time) >= DATE(now()) "
                        + "ORDER BY a.date_time"
                        + ") as act_times "
                        + "GROUP BY related_ids ORDER BY dateymd");

                pstmt.clearParameters();        // clear the parms
                pstmt.setString(1, user);
                rs = pstmt.executeQuery();

                // if we found any then output the header row.
                rs.last();
                if (rs.getRow() > 0) {

                    found = true;

                    if (!new_skin) {
                        out.println("<font size=\"3\">");
                        out.println("<br><b>Your Scheduled Activities</b></font><br>");
                        out.println("<font size=\"2\"><br>");
                        out.println("<table border=\"0\">");
                    } else {
                        out.println("  <h3 class=\"tee_list_ctr\">Your Scheduled Activities</h3>");
                        out.println("  <table class=\"tee_list_flex_res_events\">");
                    }

                }

                rs.beforeFirst();

                int slotCount = 0;
                int interval = 0;
                int lastDate = 0;

                while (rs.next()) {

                    StringTokenizer tok = new StringTokenizer(rs.getString("related_ids"), ",");

                    slotCount = tok.countTokens();

                    interval = rs.getInt("interval");

                    if (!new_skin) {
                        if (lastDate != rs.getInt("dateymd")) {
                            if (lastDate != 0) {
                                out.println("<tr><td>&nbsp;</td></tr>");
                            }
                            lastDate = rs.getInt("dateymd");
                        }

                        out.println("<tr><td align=\"left\"><font size=2>");
                        out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));

                        out.println("</font></td><td>&nbsp;</td><td align=\"left\">");
                        out.print("<a href=\"javascript:void(0)\" onclick=\"top.location.href='Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=998'\"><font size=2 color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><font size=2> for " + (interval * slotCount) + " minutes.</font><br>");
                        //out.println("&nbsp; &nbsp; <a href=\"javascript:editActSignup(" + rs.getInt("sheet_id") + ", '" + rs.getString("pretty_time") + "')\"><font color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
                        out.println("</td></tr>");
                    } else {
                        if (lastDate != rs.getInt("dateymd")) {
                            if (lastDate != 0) {
                                out.println("    <tr><td>&nbsp;</td></tr>");
                            }
                            lastDate = rs.getInt("dateymd");
                        }

                        out.println("    <tr>");
                        out.print("      <td>");
                        out.println(getActivity.getFullActivityName(rs.getInt("activity_id"), con));

                        out.println("      </td><td>");
                        out.println("      <a href=\"javascript:void(0)\" onclick=\"location.href='Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=998'\">");
                        out.println("      <span style=\"color: darkGreen;\">" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</span></a> for " + (interval * slotCount) + " minutes.");
                        //out.println("&nbsp; &nbsp; <a href=\"javascript:editActSignup(" + rs.getInt("sheet_id") + ", '" + rs.getString("pretty_time") + "')\"><font color=darkGreen>" + rs.getString("pretty_date") + " at " + rs.getString("pretty_time") + "</font></a><br>");
                        out.println("      </td></tr>");
                    } // else (if !new_skin)

                }
                if (!new_skin) {
                    out.println("</table>");
                } else {
                    out.println("  </table>    <!-- tee_list_flex_res_events -->");
                }


                //
                //*****************************************************************
                // now check for any Events for this Activity that member is signed up for
                //*****************************************************************
                //
                pstmte2 = con.prepareStatement(
                        "SELECT name "
                        + "FROM events2b WHERE date >= ? AND activity_id = ? AND inactive = 0");       // look for Activity events

                pstmte2.clearParameters();
                pstmte2.setLong(1, cdate);
                pstmte2.setInt(2, sess_activity_id);
                rs2 = pstmte2.executeQuery();

                loop1e:
                while (rs2.next()) {

                    name = rs2.getString(1);

                    // check for signups
                    pstmte = con.prepareStatement(
                            "SELECT name "
                            + "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                            + "OR username5 = ?) AND name = ? AND inactive = 0");

                    pstmte.clearParameters();        // clear the parms
                    pstmte.setString(1, user);
                    pstmte.setString(2, user);
                    pstmte.setString(3, user);
                    pstmte.setString(4, user);
                    pstmte.setString(5, user);
                    pstmte.setString(6, name);
                    rs = pstmte.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        events = true;
                    }
                    pstmte.close();

                    if (events == true) {

                        break loop1e;
                    }
                }        // end of WHILE evntsup
                pstmte2.close();

                //
                //  if any events found above, then display them here (search over again)
                //
                if (events == true) {

                    found = true;

                    if (!new_skin) {
                        out.println("<font size=\"3\">");
                        out.println("<br><b>Your Scheduled Events</b></font><br>");
                        out.println("<font size=\"2\"><br>");
                        out.println("<table border=\"0\">");
                    } else {
                        out.println("");
                        out.println("  <h3 class=\"tee_list_ctr\">Your Scheduled Events</h3>");
                        out.println("  <table class=\"tee_list_flex_res_events\">");
                    }

                    //
                    // now list the events that member is signed up for
                    //
                    pstmte2 = con.prepareStatement(
                            "SELECT name, date, year, month, day, act_hr, act_min, signUp, c_date, c_time "
                            + "FROM events2b WHERE date >= ? AND activity_id = ? AND inactive = 0");       // look for Golf events

                    pstmte2.clearParameters();        // clear the parms
                    pstmte2.setLong(1, cdate);
                    pstmte2.setInt(2, sess_activity_id);
                    rs2 = pstmte2.executeQuery();

                    while (rs2.next()) {

                        name = rs2.getString(1);
                        edate = rs2.getLong(2);
                        yy = rs2.getInt(3);
                        mm = rs2.getInt(4);
                        dd = rs2.getInt(5);
                        hr = rs2.getInt(6);
                        min = rs2.getInt(7);
                        signUp = rs2.getInt(8);
                        c_date = rs2.getLong(9);
                        c_time = rs2.getInt(10);


                        //
                        //  Now look for signups in this event
                        //
                        pstmte = con.prepareStatement(
                                "SELECT player1, player2, player3, player4, player5, wait "
                                + "FROM evntsup2b WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                + "OR username5 = ?) AND name = ? AND inactive = 0 ORDER BY c_date, c_time");

                        pstmte.clearParameters();        // clear the parms
                        pstmte.setString(1, user);
                        pstmte.setString(2, user);
                        pstmte.setString(3, user);
                        pstmte.setString(4, user);
                        pstmte.setString(5, user);
                        pstmte.setString(6, name);
                        rs = pstmte.executeQuery();      // execute the prepared stmt

                        while (rs.next()) {

                            player1 = rs.getString(1);
                            player2 = rs.getString(2);
                            player3 = rs.getString(3);
                            player4 = rs.getString(4);
                            player5 = rs.getString(5);
                            wait = rs.getInt(6);

                            if (edate >= cdate) {          // if event is today or later - we found one

                                //
                                //  Create time values
                                //
                                ampm = "AM";

                                if (hr == 0) {

                                    hr = 12;                 // change to 12 AM (midnight)

                                } else {

                                    if (hr == 12) {

                                        ampm = "PM";         // change to Noon
                                    }
                                }
                                if (hr > 12) {

                                    hr = hr - 12;
                                    ampm = "PM";             // change to 12 hr clock
                                }

                                if (signUp != 0 && c_date >= cdate) {     // if members can sign up for this event

                                    if ((c_date > cdate) || (c_time > ctime)) {

                                        signUp = 1;;

                                    } else {

                                        signUp = 0;;
                                    }

                                } else {

                                    signUp = 0;;
                                }

                                String timeS = mm + "/" + dd + "/" + yy + " at " + hr + ":" + Utilities.ensureDoubleDigit(min) + " " + ampm;

                                 out.println("    <tr>");
                                 out.println("      <td>" + name + "</td>");                 // display name of event

                                 out.println("      <td>");
                                 if (signUp == 1) {
                                    
                                    hashMap.clear();
                                    hashMap.put("type", "Member_events2");
                                    
                                    hashMap.put("name", name);
                                    hashMap.put("course", "");
                                    hashMap.put("index", 995);
                                    out.println("<a class=\"event_button standard_button\" href=\"#\"");
                                    out.print("      data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\" style=\"background-color:" + ecolor + ";\">" + name + " on " + timeS + "</a>");
                                    
                                    //out.println("  <a href=\"Member_events2?name=" + name + "&index=995\"><span style=\"color:darkGreen;\">" + name + " on " + timeS + "</span></a>");
                                    
                                 } else {
                                       out.println("      " + name + " at " + timeS);
                                 }
                                 out.println("    </td></tr>");
                                 
                            }     // end of IF event date is current

                        }     // end of WHILE signups
                        pstmte.close();

                    }        // end of WHILE events

                    pstmte2.close();

                    if (!new_skin) {
                        out.println("</font></table>");        // done with events table
                    } else {
                        out.println("  </table>    <!-- tee_list_flex_res_events -->");    // done with events table
                    }

                }    // end of IF any events
                
                
            } catch (Exception exc) {

                if (!new_skin) {
                    out.println(SystemUtils.HeadTitle("Database Error"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
                    out.println("<BR><BR><H3>Database Access Error</H3>");
                    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                    out.println("<BR>Error:" + exc.getMessage());
                    out.println("<BR><BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, contact your club manager.");
                    out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
                    out.println("</CENTER></BODY></HTML>");
                } else {
                    errorPageTop(out, session, req, con, "Database Error");
                    dbAccessError(out, club, sess_activity_id, exc, req);
                } // else (if !new_skin)             

                out.close();
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

        } // end if activities supported




        //
        // Display any Dining reservations or events
        //
        boolean showDining = false;
        
        if (organization_id != 0 && new_skin) {
            
            showDining = true;
            
            if (caller.equals("FLEXWEBFT") && dining == false) {    // if Connect Premier and user not on Dining system
                
                showDining = false;         // only show the activity they are on
            }
        }

        if (showDining) {

            Connection con_d = null;

            try {

                events = false; // reuse
                date = SystemUtils.getDate(con);

                //Utilities.logErrorTxt("1: Getting con_d", club);

                con_d = Connect.getDiningCon();
                
                //Utilities.logErrorTxt("2: con_d == null equals " + (con_d == null), club);

                int dtime = 0;
                String state = "";
                String mNum = "";

                if (user != null && !user.equals("")) {

                    mNum = Utilities.getmNum(user, con);      // get this mem's member number
                }

                if (con_d != null) {

                    // lookup this member in the reservations table to see if they are already signed up for anything dining related
                    pstmt = con_d.prepareStatement(""
                            + "SELECT r.id, r.state, r.date, r.category, r.event_id, e.name, e.members_can_make_reservations, to_char(r.time, 'HH24MI') AS stime, r.date "
                            + "FROM reservations r "
                            + "LEFT OUTER JOIN events e ON r.event_id = e.id "
                            + "WHERE "
                                + "r.organization_id = ? AND "
                                + "r.person_id = ? AND "
                                + "to_char(r.date, 'YYYYMMDD')::int >= ? AND "
                                + "r.state <> 'cancelled' "
                            + "ORDER BY r.date, r.time");

                    pstmt.setInt(1, organization_id);
                    pstmt.setInt(2, person_id);
                    pstmt.setInt(3, (int) date);

                    rs = pstmt.executeQuery();

                    while (rs.next()) {

                        //state = rs.getString("state");
                        /*
                        if (!new_skin) {
                            if (!events) {

                                events = true;

                                out.println("<font size=\"3\">");
                                out.println("<br><b>Your Upcoming Dining Reservations</b></font><br>");
                                out.println("<font size=\"2\"><br>");
                                out.println("<table border=\"0\">");

                            }

                            out.println("<tr><td align=\"left\"><font size=2>");

                            if (rs.getInt("event_id") != 0) {

                                // found a dining event reservation

                                // if member can signup for the dining event AND they are not already signed-up then make it a link
                                // and show their reservation status, otherwise just display the name of the event
                                if (false) { // rs.getBoolean("members_can_make_reservations") == true

                                    dtime = Integer.parseInt(rs.getString("stime"));
                                    out.print("<a href=\"Dining_slot?\">");
                                    out.print(rs.getString("name"));
                                    out.println("</a> at " + Utilities.getSimpleTime(dtime) + " on " + rs.getString("date"));

                                } else {

                                    out.print("<span>" + rs.getString("name") + " at " + Utilities.getSimpleTime(dtime) + " on " + rs.getString("date"));
                                    if (!state.equals("")) {
                                        out.print("&nbsp; &nbsp; (" + state + ")");
                                    }
                                    out.print("</span>");

                                }
                                //if (!state.equals("")) out.println("<br><span>(" + state + ")</span>");

                            } else {

                                // found a simple dining reservation
                                dtime = Integer.parseInt(rs.getString("stime"));
                                out.print("<span>Dining Reservation at " + Utilities.getSimpleTime(dtime) + " on " + rs.getString("date"));
                                if (!state.equals("")) {
                                    out.print("&nbsp; &nbsp; (" + state + ")");
                                }
                                out.print("</span>");

                            }

                            out.println("</font></td></tr>");
                        } // if (!new_skin)
                        else {
                        */
                            if (!events) {

                                events = true;

                                out.println("  <h3 class=\"tee_list_ctr\">Your Upcoming Dining Reservations</h3>");
                                out.println("  <table class=\"tee_list_flex_res_events\">");

                            }

                            out.println("    <tr><td>");

                            if (rs.getInt("event_id") != 0) {

                                // found a dining event reservation
                                found = true;

                                // if member can signup for the dining event AND they are not already signed-up then make it a link
                                // and show their reservation status, otherwise just display the name of the event
                                if (false) { // rs.getBoolean("members_can_make_reservations") == true

                                    dtime = Integer.parseInt(rs.getString("stime"));
                                    out.print("      <a href=\"http://216.243.184.69/self_service/reservations/member_login?landing=event&event_id=" + rs.getInt("event_id") + "&username=" + mNum + "&organization_id=" + organization_id + "\" target=\"_dining\">");
                                    out.print(rs.getString("name"));
                                    out.println("</a> at " + Utilities.getSimpleTime(dtime) + " on " + rs.getString("date"));

                                } else {

                                    dtime = Integer.parseInt(rs.getString("stime"));
                                    out.print("      <span>" + rs.getString("name") + " at " + Utilities.getSimpleTime(dtime) + " on " + rs.getString("date"));
                                    if (!state.equals("")) {
                                        out.print("&nbsp; &nbsp; (" + state + ")");
                                    }
                                    out.println("</span>");

                                }
                                //if (!state.equals("")) out.println("<br><span>(" + state + ")</span>");

                            } else {

                                // found a simple dining reservation
                                found = true;
                                dtime = Integer.parseInt(rs.getString("stime"));
                                out.println("<a class=\"day\" href=\""+Utilities.getBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club)+"Dining_slot?action=edit&amp;orig=calendar&amp;reservation_id=" + rs.getInt("id") + "\">");
                                // href=\"/" + rev + "/servlet/Dining_slot?action=edit&amp;orig=calendar&amp;reservation_id=" + rs.getInt("id") + 
                                out.print("    <span>Dining Reservation at " + Utilities.getSimpleTime(dtime) + " on " + rs.getString("date"));
                                if (!state.equals("")) {
                                    out.print("&nbsp; &nbsp; (" + state + ")");
                                }
                                out.println("</span>");
                                out.println("</a>");

                            }

                            out.println("      </td>");
                            out.println("    </tr>");

                        //} // else (if !new_skin)

                    } // while rs loop

                    /*if (!new_skin) {
                        out.println("</table>");        // done with dining table
                    } else {*/
                        out.println("  </table>    <!-- tee_list_flex_res_events -->");        // done with dining table
                    //}

                }

            } catch (Exception exc) {

                if (!new_skin) {
                    out.println(SystemUtils.HeadTitle("Database Error"));
                    out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
                    out.println("<BR><BR><H3>Database Access Error</H3>");
                    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                    out.println("<BR>Error:" + exc.getMessage());
                    out.println("<BR><BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, contact your club manager.");
                    out.println("<BR><BR><a href=\"Member_announce\">Home</a>");
                    out.println("</CENTER></BODY></HTML>");
                } else {
                    errorPageTop(out, session, req, con, "Database Error");
                    dbAccessError(out, club, sess_activity_id, exc, req);

                } // else (if !new_skin)             

                out.close();
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

                try {
                    con_d.close();
                } catch (Exception ignore) {
                }

            }

        }


        //
        //  Custom - check for Dependents' Event Registrations
        //
        if ((club.equals("denvercc") && !mtype.equalsIgnoreCase("Dependent")) ||
            (club.equals("edina") && (mtype.startsWith("Adult") || mtype.startsWith("Pre-Leg")))) {  // Custom to check for Dependents registered

            //  Get each dependent and check if they are registered

            boolean foundDep = false;
            String mNumD = "";
            PreparedStatement pstmtDenver = null;
            PreparedStatement pstmtDenver2 = null;
            PreparedStatement pstmtDenver3 = null;
            ResultSet rsDenver = null;
            ResultSet rsDenver2 = null;
            ResultSet rsDenver3 = null;

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

                    pstmtDenver3 = con.prepareStatement(
                            "SELECT name, date, year, month, day, act_hr, act_min, signUp, c_date, c_time, courseName "
                            + "FROM events2b WHERE date >= ? AND activity_id = ? AND inactive = 0");          // look for any events for this activity

                    pstmtDenver3.clearParameters();        // clear the parms
                    pstmtDenver3.setLong(1, cdate);
                    pstmtDenver3.setInt(2, sess_activity_id);
                    rsDenver3 = pstmtDenver3.executeQuery();

                    while (rsDenver3.next()) {

                        name = rsDenver3.getString(1);
                        edate = rsDenver3.getLong(2);
                        yy = rsDenver3.getInt(3);
                        mm = rsDenver3.getInt(4);
                        dd = rsDenver3.getInt(5);
                        hr = rsDenver3.getInt(6);
                        min = rsDenver3.getInt(7);
                        signUp = rsDenver3.getInt(8);
                        c_date = rsDenver3.getLong(9);
                        c_time = rsDenver3.getInt(10);
                        course = rsDenver3.getString("courseName");
                        
                        String courseS = course;

                        if (multi != 0 && course.equals("")) {

                            courseS = " ";  
                        }

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
                                    "SELECT player1, player2, player3, player4, player5, wait "
                                    + "FROM evntsup2b WHERE name = ? AND inactive = 0 "
                                    + "AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                    + "OR username5 = ?) ORDER BY c_date, c_time");

                            pstmtDenver2.clearParameters();       
                            pstmtDenver2.setString(1, name);
                            pstmtDenver2.setString(2, userD);
                            pstmtDenver2.setString(3, userD);
                            pstmtDenver2.setString(4, userD);
                            pstmtDenver2.setString(5, userD);
                            pstmtDenver2.setString(6, userD);
                            rsDenver2 = pstmtDenver2.executeQuery();     

                            if (rsDenver2.next()) {

                                if (foundDep == false) {          // if this is the first one found

                                    out.println("  <BR><h3 class=\"tee_list_ctr\">Your Dependents' Scheduled Events</h3>");
                                    out.println("  <p class=\"tee_list_ctr2\"><b>To select the event</b>:  Just click on the box containing the event name.</p>");
                                    out.println("  <p class=\"tee_list_ctr3\">If you cannot select the event, then it is currently past the sign-up date.</p>");
                                    out.println("  <table class=\"standard_list_table\">");
                                    out.println("    <thead>");
                                    out.println("    <tr>");
                                    out.println("      <th>Event Name</th>");
                                    out.println("      <th>Date & Time</th>");

                                    if (!courseS.equals("")) {

                                        out.println("      <th>Course Name</th>");
                                    }

                                    out.println("      <th>Player 1</th>");
                                    out.println("      <th>Player 2</th>");
                                    out.println("      <th>Player 3</th>");
                                    out.println("      <th>Player 4</th>");
                                    out.println("      <th>Player 5</th>");
                                    out.println("      <th>Status</th>");
                                    out.println("    </tr></thead>");
                                    out.println("    <tbody>");
                                }         

                                foundDep = true;                               // set dependent sign up found
                                player1 = rsDenver2.getString("player1");
                                player2 = rsDenver2.getString("player2");
                                player3 = rsDenver2.getString("player3");
                                player4 = rsDenver2.getString("player4");
                                player5 = rsDenver2.getString("player5");
                                wait = rsDenver2.getInt("wait");  // not used

                                //
                                //  Create time values
                                //
                                ampm = "AM";

                                if (hr == 0) {

                                    hr = 12;                 // change to 12 AM (midnight)

                                } else {

                                    if (hr == 12) {

                                        ampm = "PM";         // change to Noon
                                    }
                                }
                                if (hr > 12) {

                                    hr = hr - 12;
                                    ampm = "PM";             // change to 12 hr clock
                                }

                                if (signUp != 0 && c_date >= cdate) {     // if members can sign up for this event

                                    if ((c_date > cdate) || (c_time > ctime)) {

                                        signUp = 1;;

                                    } else {

                                        signUp = 0;;
                                    }

                                } else {

                                    signUp = 0;;
                                }

                                String timeS = mm + "/" + dd + "/" + yy + " at " + hr + ":" + Utilities.ensureDoubleDigit(min) + " " + ampm;

                                //
                                //  Display the event name as a clickable link (see form & js above)
                                //
                                teeTimeStr = hr + ":" + Utilities.ensureDoubleDigit(min) + ampm;
                                out.println("    <tr>");
                                out.print("      <td>");
                                hashMap.clear();
                                hashMap.put("type", "Member_events2");

                                if (signUp != 0 && c_date >= cdate) {     // if members can sign up for this event

                                    if ((c_date > cdate) || (c_time > ctime)) {

                                        hashMap.put("name", name);
                                        hashMap.put("course", course);
                                        hashMap.put("index", 995);
                                        out.println("<a class=\"event_button standard_button\" href=\"#\"");
                                        out.print("      data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\" style=\"background-color:" + ecolor + ";\">" + name + "</a>");

                                    } else {

                                        out.print("<div class=\"time_slot\">" + name + "</div>");
                                    }

                                } else {

                                    out.print("<div class=\"time_slot\">" + name + "</div>");
                                }
                                out.println("</td>");

                                out.println("      <td nowrap>" + mm + "/" + dd + "/" + yy + "<br />" + teeTimeStr + "</td>");

                                if (!courseS.equals("")) {

                                    out.println("      <td>" + courseS + "</td>");
                                }

                                out.println("      <td>" + player1 + "</td>");
                                out.println("      <td>" + player2 + "</td>");
                                out.println("      <td>" + player3 + "</td>");
                                out.println("      <td>" + player4 + "</td>");
                                out.println("      <td>" + player5 + "</td>");

                                out.print("      <td>");

                                if (wait == 0) {
                                    out.print("Registered");
                                } else {
                                    out.print("Wait List");
                                }
                                out.println("</td>");
                                out.println("    </tr>");
                                
                                break loopDenver;       // don't need to check any other dependents
                            }
                            pstmtDenver2.close();

                        }            // check all dependents
                        pstmtDenver.close();

                    }    // end of WHILE events
                    pstmtDenver3.close();
                    
                    if (foundDep) {
                        
                        out.println("    </tbody>");
                        out.println("  </table>    <!-- standard_list_table -->");        // done with dependents events table
                    }                                        

                }    // end of IF mNumD (if member number found)

            } catch (Exception e9) {

                Utilities.logError("Member_teelist_list: Error processing events (custom) for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e9.getMessage());

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
                    if (rsDenver3 != null) rsDenver3.close();
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
                    if (pstmtDenver3 != null) pstmtDenver3.close();
                } catch (SQLException ignored) {
                }
            }

        }   // end of Custom for dependents



        // Finish the page

        if (!new_skin) {
            if (!found) {

                out.println("<p>You do not currently have any scheduled activities at this time.</p>");

            }

            out.println("</font></td>");
            out.println("</tr>");
            out.println("</table>");                   // end of table for main page

            out.println("<br><br>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"Member_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");

            //
            //  End of HTML page
            //
            out.println("</center></font></body></html>");
        } // if (!new_skin)
        else {
            if (!found) {

                out.println("  <p class=\"tee_list_ctr\">You do not currently have any scheduled activities at this time.</p>");

            }

            out.println("  <br />");

            //
            //  End of HTML page
            //
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);
        }
        out.close();

    }   // end of doGet

    //
    // Common functions for the teelist table.
    // Add top row to table.
    //
    private void addTeelistTblTop(PrintWriter out, int multi, int fivesomes) {

        out.println("    <thead>");
        out.println("    <tr>");
        out.println("      <th>Date</th>");
        out.println("      <th>Time</th>");
        if (multi != 0) {
            out.println("      <th>Course Name</th>");
        }
        out.println("      <th>F/B</th>");
        out.println("      <th>Player 1</th>");
        out.println("      <th>Player 2</th>");
        out.println("      <th>Player 3</th>");

        if (fivesomes != 0) {
            out.println("      <th>Player 4</th>");
            out.println("      <th>Player 5</th>");
        } else {
            out.println("      <th>Player 4</th>");
        }
        out.println("    </tr></thead>");

    }

    //
    // Add entries for hashmap base fields.
    //
    @SuppressWarnings("unchecked")
    private void addTeelistTblHashmapB(Map hashMap, long date, String day, String course, int fives, String rest) {

        hashMap.put("date", date);
        hashMap.put("day", day);
        hashMap.put("course", course);
        hashMap.put("index", 995);

        if ((fives != 0) && (rest.equals(""))) {                // if 5-somes and not restricted
            hashMap.put("p5", "Yes");
        } else {
            hashMap.put("p5", "No");
        }

    }

    //
    // Add entries for hashmap lottery fields.
    //
    @SuppressWarnings("unchecked")
    private void addTeelistTblHashmapL(Map hashMap, String lname, long lottid, int slots, int lstate) {

        // add lottery fields
        hashMap.put("lname", lname);
        hashMap.put("lottid", lottid);
        hashMap.put("slots", slots);
        hashMap.put("lstate", lstate);

    }

    //
    // Add empty entries for date, time, course, and f/b.
    //
    private void addTeelistTblEmpty(PrintWriter out, int multi) {

        out.println("      <td>&nbsp;</td>");
        out.println("      <td>&nbsp;</td>");
        if (multi != 0) {
            out.println("      <td>&nbsp;</td>");
        }
        out.println("      <td>&nbsp;</td>");

    }

    //
    // Add player entries.
    //
    private void addTeelistTblPlayers(PrintWriter out, String player1, String player2, String player3,
            String player4, String player5, int fivesomes, String rest5_color) {

        out.println("      <td>" + player1 + "</td>");
        out.println("      <td>" + player2 + "</td>");
        out.println("      <td>" + player3 + "</td>");
        out.println("      <td>" + player4 + "</td>");
        if (fivesomes != 0) {

            if (rest5_color.equals("")) {

                out.print("      <td>");
            } else {
                out.print("      <td style=\"background-color: " + rest5_color + ";\">");
            }

            out.println(player5 + "</td>");
        }

    }
    // *********************************************************
    //  Common functions for database errors
    // *********************************************************

    private void errorPageTop(PrintWriter out, HttpSession session, HttpServletRequest req, Connection con, String titleStr) {

        String club = (String) session.getAttribute("club");               // get name of club
        int sess_activity_id = (Integer) session.getAttribute("activity_id");
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        String clubName = SystemUtils.getClubName(con);            // get the full name of this club
        String index = req.getParameter("index");               // get return indicator

        if (new_skin) {
            Common_skin.outputHeader(club, sess_activity_id, titleStr, true, out, req);
            Common_skin.outputBody(club, sess_activity_id, out, req);
            Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
            Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);
            Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
            Common_skin.outputPageStart(club, sess_activity_id, out, req);
            out.println("  <div id=\"breadcrumb\"><a href=\"Member_announce\">Home</a> / Database Error</div>");
            out.println("  <div class=\"tee_list_clr\">");
            out.println("    <p class=\"tee_list_ctr\">");
            out.println("      <img src=\"/" + rev + "/images/foretees.gif\" /><br />");
            out.println("      <hr class=\"tee_list\" />");
            out.println("    </p></div>");
        }
    }

    private void dbAccessError(PrintWriter out, String club, int sess_activity_id, Exception exc, HttpServletRequest req) {

        out.println("  <br /><br /><h3 class=\"tee_list_ctr\">Database Access Error</h3>");
        out.println("  <p class=\"tee_list_ctr\"><br />Sorry, we are unable to access the database at this time.");
        out.println("    <br />Error:" + exc.getMessage());
        out.println("    <br /><br />Please try again later.");
        out.println("    <br /><br />If problem persists, contact your club manager.");
        out.println("    <br /><br />");
        out.println("  </p>");
        Common_skin.outputPageEnd(club, sess_activity_id, out, req);

    }
}
