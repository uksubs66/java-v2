
/***************************************************************************************
 *   Member_events2:  This servlet will display event information and sign-up info
 *                    for the event selected in Member_events.
 *
 *
 *
 *   called by:  Member_events
 *               Member_jump (on return from Member_evntSignUp)
 *
 *
 *   created: 2/13/2003   Bob P.
 *
 *   last updated:
 *
 *        3/03/12   Edina CC - custom to allow adults to access their dependents' event registrations - like Denver CC (case 2378).
 *        2/27/14   Merion GC (merion) - Added custom to restrict members to only signing up to a certain number of "Mens Stag Day" and "Mens Member Guest" events at a given time, based on the current date (case 2369).
 *        2/14/14   Merion GC (merion) - Updated hide names custom with different event names for 2014 (case 2234).
 *       11/07/13   Change event Map creation to increase efficiency, readability, and remove warnings during build.
 *       11/06/13   Fixed an issue when displaying event info that was causing info on joining existing groups to appear even when team size equaled the minimum team size.
 *       10/15/13   Walpole CC (walpolecc) - Hide all signups that the current user isn't a part of, removed previous custom to hide names since those times won't even be visible.
 *        9/30/13   Walpole CC (walpolecc) - Hide names in the signups for a particular event.
 *        9/23/13   Fixed positive handicaps so they show with the proper "+" ahead of them.
 *        8/27/13   Do not allow member to edit a registration if the signups have been moved to the tee sheet.
 *        5/25/13   Merion GC (merion) - Hide names in the signup listing for 1 more event
 *        4/04/13   Update for Connect Premier calendar integration
 *        4/03/13   Baltusrol GC (baltusrolgc) - Added custom to display member names only for "BGC Ladies" events (case 2218).
 *        3/13/13   Beechmont CC (beechmontcc) - Updated custom to the correct event name (was changed due to size constraints) (case 2239).
 *        3/06/13   Merion GC (merion) - Hide names in the signup listing for 4 specific events (case 2234).
 *        3/06/13   Beechmont CC (beechmontcc) - Hide names in the signups for a particular event (case 2239).
 *        2/28/13   Denver CC - Do not allow others to join a team if activity is Juniors or Fitness.
 *        2/27/13   Denver CC (denvercc) - Changed the verbiage in the new event signup message to say it was "RECEIVED" instead of "RESERVED" (case 2233).
 *       11/21/12   Updated processing to try pulling an activity_id out of the request object.  If found, that activity_id will be used through the rest of the code. If not found, sess_activity_id will be used.
 *       10/25/12   Updated event info so that numerous details are not displayed for FlxRez events. Also updated verbiage to use "Sign Ups" in place of "Teams" and "Participants" in place of "Players" for FlxRez events.
 *       10/16/12   Denver CC - custom to allow adults to access their dependents' event registrations.
 *        7/23/12   Rolling Hills GC - SA (rollinghillsgc) - Updated event signup messages to direct members to contact 'tournaments@arabiangolf.com' instead of the golf shop.
 *        5/17/12   Updates to correct external login with new skin.
 *        3/06/12   Updated page display so the list of signups are sorted first by waitlist status, then by registration date and time.
 *        2/01/12   Fixed Inverness custom which was preventing the event itinerary from displaying for ALL clubs if members couldn't currently signup.
 *        1/26/12   Aliso Viejo CC (alisoviejo) - Added custom to display names on event signup even though the hide names feature is turned on (case 2110).
 *       12/08/11   Refinements to JSON output
 *       12/07/11   Moved customs before HTML generation, using LinkedHashMaps; Changed player1-5;user1-5;hndcp1-5 to arrays; Loop new arrays to generate Maps
 *       12/05/11   Add JSON output mode for use with new skin.
 *       11/30/11   Ramsey G&CC (ramseycountryclub) - Display the answers to the Custom Question #1 in an additional column to the right of each players for FlxRez events, but only when that question is used (case 2079).
 *       11/28/11   Olympic Club (olyclub) - Hide member names on the event signup page (case 2066).
 *       11/28/11   Olympic Club (olyclub) - Display "Received" instead of "Registered" in the status field of the event signup table (case 2069).
 *        8/26/11   Member handicap indexes will no longer be rounded (we stopped doing this on proshop side some time ago, but it looks like it was never put on the member side).
 *        7/11/11   Added processing to accomodate members coming from the tee sheet for event signup, and close the window on exit instead of returning to another page.
 *        2/15/11   Add processing to handle member coming in from Login via link in an email message.
 *       12/02/10   Rolling Hills GC - SA (rollinghillsgc) - Changed verbiage on custom message
 *       12/02/10   Rolling Hills GC - SA (rollinghillsgc) - Do not display Sign Up button if max teams has been reached, and hide all wait listed signups from member view.
 *       11/19/10   Blackstone CC (blackstone) - Do not hide member names in event signup (case 1914).
 *       10/12/10   Fixed a couple spots still using golf terminology
 *       10/06/10   Change some of the messages regarding the status of online sign-up for events to make them more meaningful (Hop Meadow request).
 *        4/14/10   CC of Virginia - do not display names in the event signup list for the "2010 Mens Member Guest" event (case 1799). 
 *       12/09/09   When looking for events only check those that are active.
 *       10/10/09   Do not use the course name when locating the event (Activities do not use a course).
 *        9/28/09   Added support for Activities
 *        3/19/09   Changed team/count calcuation to seperate registered vs. waitlist and changed related display text
 *        2/11/09   Custom for Hazeltine - do not allow members to signup for Invitational unless they
 *                  have a sub-type of Invite Priority (case 1585).
 *       12/10/08   Provide a more specific message about why user can't signup for event
 *        4/22/08   Do not show instructions on how to join a team if hidenames = yes.
 *        4/02/08   Removed C/W info in players listing for season long events
 *        3/27/08   Add gender and season information to event summary
 *        9/27/07   Display the new minimum sign-up size as part of the event details
 *        7/16/07   Allow members to view the event signup list up until the date of the event.
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        7/19/06   Desert Highlands - ignore the 'Hide Names' parm and always show names for events.
 *        1/30/06   Do not display the member names if option selected in config.
 *        6/16/05   If the entry in the event signup list is full, leave the button column empty (was "Full").
 *                  Some members thought this meant the event was full.
 *        5/13/05   Add processing for restrictions on events (member may not have access to event).
 *        4/15/05   Inverness - do not show the Itinerary if signup=no.
 *        3/09/05   Do not allow signup if singup=no or date too early.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       11/20/04   Ver 5 - allow for return to Member_teelist (add index=).
 *       10/06/04   Ver 5 - allow for sub-menus.
 *        1/13/04   JAG Modifications to match new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 *
 ***************************************************************************************
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
//import java.lang.Math;
import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Labels;
import com.foretees.common.verifyCustom;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;

public class Member_events2 extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

//********************************************************************************
//
//  doGet - call doPost processing (gets control from Member_jump)
//
//********************************************************************************
//
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        doPost(req, resp);                          // call doPost processing

    }   // end of doGet

    //
    //******************************************************************************
    //
    //  doPost processing - gets control from Member_events when user selects an event.
    //
    //  Get the event info and display a sign up sheet
    //
    //******************************************************************************
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
  
        //
        //  Prevent caching so sessions are not mangled
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        PreparedStatement stmt = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        HttpSession session = null;

        boolean json_mode = (req.getParameter("jsonMode")) != null; // Will we output JSON or HTML

        boolean ext_login = false;            // not external login (from email link)

        if (req.getParameter("ext-dReq") != null || req.getParameter("ext-login") != null) {   // if from Login or Member_evntSignUp for an external login user

            ext_login = true;        // member came from link in email message  (via Login.ProcessExtLogin)

            session = req.getSession(false);

            // if the user sits too long on the exernal welcome page their special session may of expired
            if (session == null || (String) session.getAttribute("ext-user") == null) {

                out.println("<HTML>");
                out.println("<HEAD>");
                out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
                out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
                out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
                out.println("<TITLE>Access Error</TITLE></HEAD>");
                out.println("<BODY><CENTER>");
                out.println("<H2>Access Error - Please Read</H2>");
                out.println("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
                out.println("<BR><BR>This site requires the use of Cookies for security purposes.");
                out.println("<BR><HR width=\"500\"><BR>");
                out.println("If you feel that you have received this message in error,");
                out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
                out.println("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
                out.println("<BR>Thank you.");
                out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
                out.println("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }

        } else {

            session = SystemUtils.verifyMem(req, out);       // check for intruder
        }

        if (session == null) {

            return;
        }

        String user = "";

        if (ext_login == true) {        // if from an external login (email link)

            user = (String) session.getAttribute("ext-user");       // get this user's username
           // session.setAttribute("user", user);                // save username as user for normal processing  *** Can't do this **** Security issue !!!
            
        } else {

            user = (String) session.getAttribute("user");             // get this user's username
        }
        
        String club = (String) session.getAttribute("club");      // get club name
        String caller = (String) session.getAttribute("caller");  // get caller (web site)
        String mtype = (String) session.getAttribute("mtype");    // member's mtype 
        String mship = (String) session.getAttribute("mship");    // member's mship type

        int sess_activity_id = (Integer) session.getAttribute("activity_id");

        Connection con = SystemUtils.getCon(session);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"ccccaa\">");
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            if (ext_login == false && req.getParameter("ext-login") == null) {
                out.println("<BR><BR>");
                out.println("<a href=\"Member_announce\">Return</a>");
            } else {
                out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }


        // if not json mode and not an external user than this is either old skin or a flexscape user
        // coming from the intergrated calendar
        // external users should be able to use this as well so they have the new skin look
        if (!json_mode && !ext_login) {


            Common_skin.outputHeader(club, sess_activity_id, "Event Detail Page", false, out, req);

            out.println("<script type=\"text/javascript\">");
            out.println("$(document).ready(function() {");
            out.println(" setTimeout(function(){$(\"#fakeClick\").click();},100);");
            out.println("});");
            out.println("</script>");
            out.println("</head>");

            Common_skin.outputBody(club, sess_activity_id, out, req);
            //Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
            Common_skin.outputBanner(club, sess_activity_id, Utilities.getClubName(con, true), (String)session.getAttribute("zipcode"), out, req);
            Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
            Common_skin.outputPageStart(club, sess_activity_id, out, req);
            Common_skin.outputBreadCrumb (club, sess_activity_id, out, "Event Details", req);
            Common_skin.outputLogo(club, sess_activity_id, out, req);

            // rebuild the ftjson data
            //out.println("<a onclick=\"window.close()\">close</a><br><br>");
            out.println("<a id=\"fakeClick\" class=\"event_button\" href=\"\" data-ftjson=\"{" +
                    "&quot;type&quot;:&quot;Member_events2&quot;," +
                    "&quot;name&quot;:&quot;" + req.getParameter("name") + "&quot;," +
                    "&quot;course&quot;:&quot;" + req.getParameter("course") + "&quot;," +
                    "&quot;activity_id&quot;:" + req.getParameter("activity_id") + "," +
                    "&quot;base_url&quot;:&quot;" + req.getParameter("base_url") + "&quot;," +
                    "&quot;iframe_action&quot;:&quot;close&quot;," +
                    "&quot;index&quot;:" + req.getParameter("index") +
                    "}\"></a><br><br>");
            out.println("");
            out.println("");
            out.println("");
            out.println("");

/*
            Enumeration en = req.getParameterNames();

            while (en.hasMoreElements()) {

                String paramName = (String) en.nextElement();
                out.println(paramName + " = " + req.getParameter(paramName) + "<br/>");

            }
*/
            
            Common_skin.outputPageEnd(club, sess_activity_id, out, req);

            return;
        }







        String name = "";
        String format = "";
        String pairings = "";
        String memcost = "";
        String gstcost = "";
        String itin = "";
        String course = "";
        String c_ampm = "";
        String act_ampm = "";
        String[] player = new String[5];
        String[] euser = new String[5];
        String[] cw = new String[5];
        String[] othera1 = new String[5];// Used for a custom that relies on answers to Other Question #1
        String submit = "";
        String fb = "";
        String index = "";
        String activity_name = "";
        String course_display = "";
        String signup_button_type = "";
        String temp_value = "";
        String temp_string1 = "";
        String temp_string2 = "";
        String value_style_open = "";
        String value_style_close = "";
        String cell_style = "";
        String cell_type = "";
        String team_player_status = "";

        int activity_id = -1;    // This will be set to a passed activity_id, if present, otherwise it will default to sess_activity_id
        int hndcpOpt = 0;
        int month = 0;
        int day = 0;
        int year = 0;
        int type = 0;
        int holes = 0;
        int act_hr = 0;
        int act_hr_24 = 0;
        int act_min = 0;
        int size = 0;
        int minsize = 0;
        int max = 0;
        int guests = 0;
        int c_month = 0;
        int c_day = 0;
        int c_year = 0;
        int c_time = 0;
        int c_hr = 0;
        int c_hr_24 = 0;
        int c_min = 0;
        int count_reg = 0;
        int count_wait = 0;
        int teams_reg = 0;
        int teams_wait = 0;
        int i = 0;
        int t = 0;
        int full = 0;
        int id = 0;
        //int skip = 0;
        int in_use = 0;
        int wait = 0;
        int signup = 0;
        int sutime = 0;
        int currtime = 0;
        int hideNames = 0;
        int hideN = 0;
        int[] guest = new int[5];
        int gender = 0;
        int season = 0;
        int event_id = 0;
        int ask_otherA1 = 0;
        int row_count = 0;
        int select_count = 0;
        int moved = 0;
        int custom_event_count_1 = 0;
        int custom_event_count_2 = 0;
        int custom_event_category_id_1 = 0;
        int custom_event_category_id_2 = 0;
        int custom_selected_event_id = 0;

        long today = 0;
        long date = 0;
        long c_date = 0;
        long sudate = 0;
        float hndcp = 0;
        float[] hndcpa = new float[5];

        boolean disp_hndcp = true;
        boolean viewList = false; // default to not being able to see the event signups
        boolean teesheet = false;
        boolean show_itinerary = true;
        boolean use_signup_button = true;
        boolean all_players_empty = true;
        boolean in_this_group = false;


        List<String> instructions = new ArrayList<String>();

        Gson gson_obj = new Gson();

        Map<String, Object> event_map = new LinkedHashMap<String, Object>();
        event_map.put("requests", new LinkedHashMap<String, Object>());
        Map<String, Map> column_map = new LinkedHashMap<String, Map>();
        Map<String, Map<String, Map>> row_map = new LinkedHashMap<String, Map<String, Map>>();
        Map<String, Map> dashboard_map = new LinkedHashMap<String, Map>();
        Map<String, Integer> user_to_event_map = new LinkedHashMap<String, Integer>();

        //
        //   Get the parms received
        //
        if (ext_login == false) {                 // if NOT from an external login

            name = req.getParameter("name");

            if (req.getParameter("course") != null) {         // if course name provided

                course = req.getParameter("course");
            }

            if (req.getParameter("index") != null) {         // if from Member_teelist or Member_teelist_list

                index = req.getParameter("index");
            }

            if (req.getParameter("teesheet") != null) {

                teesheet = true;
            }
            
            if (req.getParameter("activity_id") != null) {
                
                try {
                    activity_id = Integer.parseInt(req.getParameter("activity_id"));
                } catch (Exception exc) {
                    
                    activity_id = sess_activity_id;
                    Utilities.logError("Member_events2.doPost - " + club + " - Error parsing activity_id - ERR: " + exc.toString());
                    
                }
            }

        } else {     // external Login - from email link to signup for event (via Login.ProcessExtLogin)

            if (req.getParameter("event_id") != null && !req.getParameter("event_id").equals("")) {

                try {
                    event_id = Integer.parseInt(req.getParameter("event_id"));
                } catch (Exception ignore) {
                }

                try {

                    stmt = con.prepareStatement(
                            "SELECT name, courseName FROM events2b WHERE event_id = ? ");

                    stmt.clearParameters();        // clear the parms
                    stmt.setInt(1, event_id);
                    rs = stmt.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        name = rs.getString("name");
                        course = rs.getString("courseName");
                    }
                    stmt.close();

                } catch (Exception exc) {

                    out.println(SystemUtils.HeadTitle("Database Error"));
                    out.println("<BODY bgcolor=\"ccccaa\">");
                    out.println("<CENTER>");
                    out.println("<BR><BR><H3>Database Access Error</H3>");
                    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                    out.println("<BR>Error:" + exc.getMessage());
                    out.println("<BR><BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, contact your " + activity_name + " shop (provide this information).");
                    out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
                    out.println("</CENTER></BODY></HTML>");
                    out.close();
                }
                
            } else {     // look for the name and course parms from Login
                               
                name = req.getParameter("name");

                if (req.getParameter("course") != null) {         // if course name provided

                    course = req.getParameter("course");
                }
            }
            
        }     // end of IF external login
        
        // If activity_id still hasn't been set, set it to the sess_activity_id
        if (activity_id == -1) {
            activity_id = sess_activity_id;
        }

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(activity_id, con);

        String[] mshipA = new String[parm.MAX_Mems + 1];            // Mem Types
        String[] mtypeA = new String[parm.MAX_Mships + 1];          // Mship Types

        if (sess_activity_id == 0) {
            activity_name = "Golf";
        } else {
            activity_name = getActivity.getActivityName(activity_id, con);
        }

        //
        //   Get current date and time (adjusted for time zone)
        //
        today = SystemUtils.getDate(con);

        currtime = SystemUtils.getTime(con);

        try {

            //
            // Get the Multiple Course Option, guest types, days in advance and time for advance from the club db
            //
            getClub.getParms(con, parm, activity_id);        // get the club parms

            hndcpOpt = parm.hndcpMemEvent;

            if (!club.equals("deserthighlands") && !club.equals("blackstone") && !club.equals("alisoviejo")) {     // ignore setting if Desert Highlands (show names)

                hideNames = parm.hiden;
            }


            //
            //  Custom for CC of Virginia - hide the names on the 2010 Mens Member Guest event
            //
            if (club.equals("virginiacc") && name.equals("2010 Mens Member Guest")) {

                hideNames = 1;
            }

            if (club.equals("olyclub")) {

                hideNames = 1;
            }
            
            if (club.equals("merion") && (name.equalsIgnoreCase("Fall 2 Day Member Guest") || name.equalsIgnoreCase("Fall 3 Day Member Guest") || name.equalsIgnoreCase("2 Day Member Guest") 
                    || name.equalsIgnoreCase("3 Day Member Guest") || name.equalsIgnoreCase("Stag Day June") || name.equalsIgnoreCase("Stag Day July") || name.equalsIgnoreCase("Stag Day May") 
                    || name.equalsIgnoreCase("Stag Day Oct") || name.equalsIgnoreCase("Stag Day Sep") || name.equalsIgnoreCase("Super Stag Day Aug"))) {
                
                hideNames = 1;
            }
            
            if (club.equals("beechmontcc") && name.equalsIgnoreCase("Member Guest Invitational")) {
                
                hideNames = 1;
            }
            
            // Don't hide member names for "BGC Ladies" events for Baltusrol GC
            if (club.equals("baltusrolgc") && name.startsWith("BGC Ladies")) {
                
                hideNames = 0;
            }


            //
            //  Check if club wants to display handicaps
            //
            if (hndcpOpt == 0) {

                disp_hndcp = false;      // if NO
            }

            //
            //  count the number of players already signed up and compare against the max allowed value
            //
            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT player1, player2, player3, player4, player5, wait "
                    + "FROM evntsup2b "
                    + "WHERE name = ? AND courseName = ? AND inactive = 0");

            pstmt.clearParameters();        // clear the parms
            pstmt.setString(1, name);
            pstmt.setString(2, course);
            rs = pstmt.executeQuery();      // execute the prepared pstmt

            while (rs.next()) {

                player[0] = rs.getString(1);
                player[1] = rs.getString(2);
                player[2] = rs.getString(3);
                player[3] = rs.getString(4);
                player[4] = rs.getString(5);
                wait = rs.getInt(6);

                t = 0;

                // check each player position and bump the appropriate count  (NOTE: events signups do not call shift up!)
                for (int i2 = 0; i2 < player.length; i2++) {
                    if (!player[i2].equals("")) {

                        t = 1;
                        if (wait == 0) {
                            count_reg++;
                        } else {
                            count_wait++;
                        }
                    }
                }

                // if we found a team then bump the appropriate count
                if (t == 1) {

                    if (wait == 0) {
                        teams_reg++;
                    } else {
                        teams_wait++;
                    }
                }
            }
            pstmt.close();
            //
            //   get the event requested
            //
            stmt = con.prepareStatement(
                    "SELECT * FROM events2b WHERE name = ? AND courseName = ? ");

            stmt.clearParameters();        // clear the parms
            stmt.setString(1, name);
            stmt.setString(2, course);
            rs = stmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                date = rs.getLong("date");
                year = rs.getInt("year");
                month = rs.getInt("month");
                day = rs.getInt("day");
                type = rs.getInt("type");
                act_hr = rs.getInt("act_hr");
                act_min = rs.getInt("act_min");
                course = rs.getString("courseName");
                signup = rs.getInt("signUp");
                format = rs.getString("format");
                pairings = rs.getString("pairings");
                size = rs.getInt("size");
                minsize = rs.getInt("minsize");
                max = rs.getInt("max");
                guests = rs.getInt("guests");
                memcost = rs.getString("memcost");
                gstcost = rs.getString("gstcost");
                c_month = rs.getInt("c_month");
                c_day = rs.getInt("c_day");
                c_year = rs.getInt("c_year");
                c_date = rs.getLong("c_date");
                c_time = rs.getInt("c_time");
                itin = rs.getString("itin");
                holes = rs.getInt("holes");
                sudate = rs.getLong("su_date");
                sutime = rs.getInt("su_time");
                fb = rs.getString("fb");
                gender = rs.getInt("gender");
                season = rs.getInt("season");
                ask_otherA1 = rs.getInt("ask_otherA1");
                for (i = 1; i < parm.MAX_Mems + 1; i++) {
                    mtypeA[i] = rs.getString("mem" + i);
                }
                for (i = 1; i < parm.MAX_Mships + 1; i++) {
                    mshipA[i] = rs.getString("mship" + i);
                }

                //
                //  Create time values
                //
                c_hr = c_time / 100;
                c_min = c_time - (c_hr * 100);

                act_hr_24 = act_hr;
                c_hr_24 = c_hr;

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
                // Define message to display later
                //
                String reason_msg = "Sorry, you cannot register for this event at this time."; // default msg

                //
                //  Check if member can View the signup list for this event
                //
                if (signup == 0) {

                    if (club.equals("rollinghillsgc")) {
                        reason_msg = "Sorry, online sign-up is not available for this event.<br>Please contact tournaments@arabiangolf.com for assistance.";
                    } else {
                        reason_msg = "Sorry, online sign-up is not available for this event.<br>Please contact your " + activity_name + " shop for assistance.";
                    }

                } else if (today >= sudate && today <= date) { // if on or after singup date and before or on day of event

                    // allow members to view signups
                    viewList = true;

                }


                // reason_msg = "The sign-up cutoff date has already passed.  You can no longer sign-up for this event.";

                //
                //  Check if member can signup for this event
                //
                if (signup > 0) {

                    if (today > c_date || (today == c_date && currtime > c_time)) {           // if after signup date

                        // reason_msg = "Sorry, the sign-up cutoff date has already passed.<br>You can no longer sign-up for this event.";
                        if (club.equals("rollinghillsgc")) {   
                            reason_msg = "Sorry, the online registration deadline for this event has passed.<BR>Please contact tournaments@arabiangolf.com to check availability.";
                        } else {
                            reason_msg = "Sorry, the online registration deadline for this event has passed.<BR>Please contact the " + activity_name + " shop to check availability.";
                        }
                        signup = 0;

                    } else if (today < sudate || (today == sudate && currtime < sutime)) {    // if before signup dates

                        if (club.equals("rollinghillsgc")) {
                            reason_msg = "Sorry, online registration is not yet available for this event.<BR>Please contact tournaments@arabiangolf.com if you have questions.";
                        } else {
                            reason_msg = "Sorry, online registration is not yet available for this event.<BR>Please contact the " + activity_name + " shop if you have questions.";
                        }
                        signup = 0;

                    }

                    if (signup > 0) {            // if signup still ok, check mtype restrictions

                        i = 1;
                        loopr1:
                        while (i < parm.MAX_Mems + 1) {

                            if (mtype.equals(mtypeA[i])) {       // is this member restricted?

                                signup = 0;                         // no signup
                                reason_msg = "Sorry, your member type restricts you from playing in this event.";
                                break loopr1;                       // exit loop
                            }
                            i++;
                        }
                    }

                    if (signup > 0) {            // if signup still ok, check mship restrictions

                        i = 1;
                        loopr2:
                        while (i < parm.MAX_Mships + 1) {

                            if (mship.equals(mshipA[i])) {       // is this member restricted?

                                signup = 0;                         // no signup
                                reason_msg = "Sorry, your membership type restricts you from playing in this event.";
                                break loopr2;                       // exit loop
                            }
                            i++;
                        }
                    }

                    if (activity_id == 0 && signup > 0 && club.equals("hazeltine")) {     // if Hazeltine & signup still ok, check event name and member sub-type

                        if (name.equals("Mens Invitational")) {        // if Invitational, then only members that played last year can signup

                            String restPlayer = verifyCustom.checkHazeltineInvite(user, "", "", "", "", con);    // check if user is Invite Priority sub-type

                            if (!restPlayer.equals("")) {         // if NOT Invite Priority - no signup

                                signup = 0;
                                reason_msg = "Sorry, you are unable to register for this event.<BR>Please contact the " + activity_name + " shop if you have questions.";
                            }
                        }
                    }           // end of IF hazeltine

                }

                if (club.equals("foresthighlands")) {      // no reason_msg, if Forest Highlands
                    reason_msg = "";
                }

                //
                //  Override team size if proshop pairings (just in case)
                //
                if (!pairings.equalsIgnoreCase("Member")) {

                    size = 1;       // set size to one for proshop pairings (size is # per team)
                }

                // Override course name, if needed
                course_display = course;
                if (!course.equals("")) {
                    if (club.equals("congressional")) {
                        course_display = congressionalCustom.getFullCourseName(date, day, course);
                    }
                }

                // Should we show itinerary?
                if (club.equals("inverness") && signup == 0) { // do not display itin if Inverness and no Signup
                    show_itinerary = false;
                }

                //
                // Create column header map
                //
                // Select column header
                Map<String, String> mselect = new LinkedHashMap<String, String>();
                mselect.put("value", "Select");
                    column_map.put("select", mselect);
                // Player column headers
                for (int i2 = 0; i2 < size; i2++) {
                    Map<String, String> mplayer = new LinkedHashMap<String, String>();
                    mplayer.put("value", "Player " + (i2 + 1));
                    if (disp_hndcp == true) {
                        mplayer.put("value_small", "hndcp");
                    }
                    column_map.put("player_" + i2, mplayer);
                    // C/W column header
                    if (activity_id == 0 && season == 0) {
                        Map<String, String> mcw = new LinkedHashMap<String, String>();
                        mcw.put("class", "cw_head");
                        mcw.put("value", "C/W");
                        column_map.put("cw_" + i2, mcw);
                    }
                    // ramseycountryclub custom column header
                    if (club.equals("ramseycountryclub") && activity_id == 1 && ask_otherA1 == 1) {
                        Map<String, String> matime = new LinkedHashMap<String, String>();
                        matime.put("value", "Arrival Time");
                        column_map.put("arrival_time_" + i2, matime); 
                    }
                }
                // Status column header
                Map<String, String> mstatus = new LinkedHashMap<String, String>();
                mstatus.put("value", "Status");
                column_map.put("status", mstatus);
                
                if (club.equals("merion")) {
                    
                    custom_event_count_1 = 0;
                    custom_event_count_2 = 0;
                    custom_event_category_id_1 = 1;    // "Mens Stag Day Events"
                    custom_event_category_id_2 = 2;    // "Mens Member Guest Events"
                    custom_selected_event_id = Utilities.getEventIdFromName(name, con);
                    
                    
                    ArrayList<Integer> category_ids_1 = new ArrayList<Integer>();
                    ArrayList<Integer> category_ids_2 = new ArrayList<Integer>();
                    
                    category_ids_1.add(custom_event_category_id_1);
                    category_ids_2.add(custom_event_category_id_2);               
                    
                    // See if the event is part of the first event category for "Stag Day" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_1, con).equals("")) {
                    
                        custom_event_count_1 = verifyCustom.checkEventCategoryCounts(user, club, custom_event_category_id_1, con);
                    
                        if ((today >= 20140301 && today < 20140401 && custom_event_count_1 >= 2)
                                || (today >= 20140401 && today < 20140501 && custom_event_count_1 >= 3)) {
                            
                            use_signup_button = false;
                        }
                    }
                    
                    // See if the event is part of the first event category for "Member/Guest" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_2, con).equals("")) {
                        
                        custom_event_count_2 = verifyCustom.checkEventCategoryCounts(user, club, custom_event_category_id_2, con);
                    
                        if ((today >= 20140301 && today < 20140501 && custom_event_count_2 >= 1)
                                || (today >= 20140501 && custom_event_count_2 >= 2)) {
                            
                            use_signup_button = false;
                        }
                    }
                }

                // Create row map
                if (signup != 0 || viewList) {
                    //
                    //  Get all entries for this Event Sign Up Sheet
                    //
                    PreparedStatement pstmte = con.prepareStatement(
                            "SELECT * FROM evntsup2b "
                            + "WHERE name = ? AND courseName = ? AND inactive = 0 ORDER BY wait, r_date, r_time");

                    pstmte.clearParameters();        // clear the parms
                    pstmte.setString(1, name);
                    pstmte.setString(2, course);
                    rs2 = pstmte.executeQuery();      // execute the prepared pstmt

                    row_count = 0;
                    select_count = 0;

                    while (rs2.next()) {

                        player[0] = rs2.getString("player1");
                        player[1] = rs2.getString("player2");
                        player[2] = rs2.getString("player3");
                        player[3] = rs2.getString("player4");
                        player[4] = rs2.getString("player5");
                        euser[0] = rs2.getString("username1");
                        euser[1] = rs2.getString("username2");
                        euser[2] = rs2.getString("username3");
                        euser[3] = rs2.getString("username4");
                        euser[4] = rs2.getString("username5");
                        cw[0] = rs2.getString("p1cw");
                        cw[1] = rs2.getString("p2cw");
                        cw[2] = rs2.getString("p3cw");
                        cw[3] = rs2.getString("p4cw");
                        cw[4] = rs2.getString("p5cw");
                        in_use = rs2.getInt("in_use");
                        hndcpa[0] = rs2.getFloat("hndcp1");
                        hndcpa[1] = rs2.getFloat("hndcp2");
                        hndcpa[2] = rs2.getFloat("hndcp3");
                        hndcpa[3] = rs2.getFloat("hndcp4");
                        hndcpa[4] = rs2.getFloat("hndcp5");
                        id = rs2.getInt("id");
                        wait = rs2.getInt("wait");                 // wait list indicator
                        moved = rs2.getInt("moved");               // moved to tee sheet indicator

                        //if (ask_otherA1 == 1) {
                        othera1[0] = rs2.getString("other1a1");
                        othera1[1] = rs2.getString("other2a1");
                        othera1[2] = rs2.getString("other3a1");
                        othera1[3] = rs2.getString("other4a1");
                        othera1[4] = rs2.getString("other5a1");
                        //}
                        //
                        //  set up some fields needed for the table
                        //
                        //submit = "id:" + id;       // create a name for the submit button (to pass the id)
                        all_players_empty = true;
                        in_this_group = false;
                        hideN = 0;                   // default to 'do not hide names'
                        full = 0;
                        
                        // Check if current user is in this group, and init guest indicators
                        for (int i2 = 0; i2 < euser.length; i2++) {
                            
                            guest[i2] = 0;                                 // By default, players are not guests
                            
                            if((euser[i2].length() > 0) && (!user_to_event_map.containsKey(euser[i2]))) {
                                
                                user_to_event_map.put(euser[i2], id);       // Create a user to event_id reverse lookup map
                            }
                            
                            if (user.equalsIgnoreCase(euser[i2])) {
                                
                                in_this_group = true;                       // Current user is in this group
                            }
                        }
                        
                        // Skip all other signups if the current member isn't a part of them
                        if (club.equals("walpolecc") && name.equalsIgnoreCase("Mens October Member Guest") && !in_this_group) { 
                            continue;
                        }
                        
                        //
                        //  Custom to check if any dependents of this member are in the event signup - if so, allow member to access it
                        //
                        if (json_mode && in_this_group == false && ((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent")) ||
                             (club.equals("edina") && (mtype.startsWith("Adult") || mtype.startsWith("Pre-Leg")))) {  // Custom to check for Dependents registered

                            //  Get each dependent and check if they are registered

                            boolean foundDep = false;
                            String mNumD = "";
                            PreparedStatement pstmtDenver = null;
                            ResultSet rsDenver = null;

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

                                    //  Locate any Dependents for this member and check if they are part of this event registration

                                    pstmtDenver = con.prepareStatement(
                                            "SELECT username "
                                            + "FROM member2b WHERE memNum = ? AND " +mtypeString);

                                    pstmtDenver.clearParameters();      
                                    pstmtDenver.setString(1, mNumD);
                                    rsDenver = pstmtDenver.executeQuery();    

                                    loopDenver:
                                    while (rsDenver.next()) {

                                        String userD = rsDenver.getString("username");   // get the dependent's username

                                        // Check if current user is in this group, and init guest indicators
                                        for (int i2 = 0; i2 < euser.length; i2++) {
                                            
                                            if (userD.equalsIgnoreCase(euser[i2])) {
                                                
                                                in_this_group = true;  // Current user is in this group
                                                foundDep = true;       // dependent found
                                                break;
                                            }
                                        }
                        
                                        if (foundDep) break loopDenver;    // stop looking if already found
                        
                                    }   // end of WHILE dependents
                                    
                                    pstmtDenver.close();
                                    
                                }       // end of IF mNumD
                        
                            } catch (Exception e9) {

                                Utilities.logError("Member_events2: Error processing events (custom) for " + club + ", User: " + user + ", Error: " + e9.getMessage());

                            } finally {

                                try {
                                    if (rsDenver != null) rsDenver.close();
                                } catch (SQLException ignored) {
                                }

                                try {
                                    if (pstmtDenver != null) pstmtDenver.close();
                                } catch (SQLException ignored) {
                                }
                            }       // end of try - catch - finally
                        }           // end of custom
                        
                        
                        // Check players/guests
                        for (int i2 = 0; i2 < player.length; i2++) {
                            if (!(player[i2].equals(""))) {
                                all_players_empty = false;
                                //
                                // Hide Names Feature - if club opts to hide the member names, then hide all names
                                // except for any group that this user is part of.
                                //
                                if ((hideNames > 0) && (!in_this_group)) {
                                    hideN = 1;
                                    if ((!player[i2].equalsIgnoreCase("x")) && (euser[i2].equals(""))) {
                                        guest[i2] = 1;  // this player is a guest
                                    }
                                }
                                //
                                // determine if slot is full and this user is not on it
                                //
                                if (((i2 + 1) == size) && (!in_this_group)) {
                                    full = 1;
                                }
                            } else {
                                cw[i2] = "";
                            }
                        }
                        
                        //
                        //  Denver CC - if Juniors or Fitness activities, do not allow others to join a partial group
                        //
                        if (club.equals("denvercc") && full == 0 && in_this_group == false && (activity_id == 2 || activity_id == 3)) {
                            
                            full = 1;         // act like team is full
                        }
                        
                        
                        
                        //
                        //   skip this entry if all players are null (someone cancelled) or Rolling Hills - SA and wait listed
                        //
                        if ((!all_players_empty) && (!club.equals("rollinghillsgc") || wait == 0)) {

                            //
                            //  Create "select button" cell
                            //
                            Map<String, Map> mrc = new LinkedHashMap<String, Map>();
                            Map<String, String> mselectr = new LinkedHashMap<String, String>();

                            temp_value = "";
                            if (in_use == 0) {
                               if (moved == 0) {
                                  if (full == 0 && hideN == 0 && signup > 0 && (!club.equals("merion") || use_signup_button || in_this_group)) {
                                       mselectr.put("type", "select");
                                       temp_value = "" + id;
                                       select_count ++; // record how many groups we can select
                                  }
                               } else {                               
                                   temp_value = "Moved";
                               }
                            } else {
                                temp_value = "Busy";
                            }
                            mselectr.put("value", temp_value);
                            //
                            //  Add Players
                            //
                            for (int i2 = 0; i2 < size; i2++) {
                                // Add player
                                Map<String, String> mplayer = new LinkedHashMap<String, String>();
                                temp_value = player[i2];
                                if (player[i2].equalsIgnoreCase("x")) {   // if 'x'
                                    temp_value = "X";
                                } else if (!player[i2].equals("")) {
                                    if (hideN == 0) {             // if ok to display names
                                        if (disp_hndcp) {
                                            if ((hndcpa[i2] != 99) && (hndcpa[i2] != -99)) {
                                                if (hndcpa[i2] <= 0) {
                                                    hndcpa[i2] = 0 - hndcpa[i2]; // convert to non-negative
                                                    mplayer.put("value_small", " " + hndcpa[i2]);
                                                } else {
                                                    mplayer.put("value_small", " +" + hndcpa[i2]);
                                                }
                                                //hndcpa[i2] = Math.round(hndcp[i2]);    // round it off
                                                // Add handicap to player cell
                                            }
                                        }
                                    } else {                        // do not display member names
                                        if (guest[i2] != 0) {       // if guest
                                            temp_value = "Guest";
                                        } else {                    // must be a member
                                            temp_value = "Member";
                                        }
                                    }
                                    
                                }
                                // Add player name to player cell
                                mplayer.put("value", temp_value);
                                // Add C/W cell
                                if (activity_id == 0 && season == 0) {
                                    Map<String, String> mcw = new LinkedHashMap<String, String>();
                                    mcw.put("class", "cw_cell");
                                    mcw.put("value", cw[i2]);
                                    mrc.put("cw_" + i2, new LinkedHashMap());
                                }
                                // Add ramseycountryclub custom "Arrival Time" cell
                                if (club.equals("ramseycountryclub") && activity_id == 1 && ask_otherA1 == 1) {
                                    Map<String, String> matime = new LinkedHashMap<String, String>();
                                    matime.put("value", othera1[i2]);
                                    mrc.put("arrival_time_" + i2, matime);
                                }
                                mrc.put("player_" + i2, mplayer);
                            }
                            //
                            //  add status (on wait list?)
                            //
                            temp_value = "Wait List";
                            if (wait == 0) {
                                if (club.equals("olyclub") || (club.equals("denvercc") && sess_activity_id == 2)) {
                                    temp_value = "Received";
                                } else {
                                    temp_value = "Registered";
                                }
                            }
                            Map<String, String> mrstat = new LinkedHashMap<String, String>();
                            mrstat.put("value", temp_value);
                            mrc.put("status", mrstat);

                            mrc.put("select", mselectr);
                            row_map.put("row_" + row_count, mrc);
                            
                            row_count++;
                        }
                    }

                    pstmte.close();

                }
                
                use_signup_button = true;
                signup_button_type = "sign_up_button";
                instructions.clear();
                
                if (club.equals("merion")) {
                    
                    ArrayList<Integer> category_ids_1 = new ArrayList<Integer>();
                    ArrayList<Integer> category_ids_2 = new ArrayList<Integer>();
                    
                    category_ids_1.add(custom_event_category_id_1);
                    category_ids_2.add(custom_event_category_id_2);               
                    
                    // See if the event is part of the first event category for "Stag Day" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_1, con).equals("")) {
                    
                        if ((today >= 20140301 && today < 20140401 && custom_event_count_1 >= 2)
                                || (today >= 20140401 && today < 20140501 && custom_event_count_1 >= 3)) {
                            
                            if (today >= 20140301 && today < 20140401) {
                                instructions.add("You are already signed up for the maximum allowed Mens Stag Day events at this time (2).");
                                instructions.add("You will be able to sign up for an additional Mens Stag Day event on April 1st.");
                            } else if (today >= 20140401 && today < 20140501) {
                                instructions.add("You are already signed up for the maximum allowed Mens Stag Day events at this time (3).");
                                instructions.add("You will be able to sign up for all remaining Mens Stag Day events on May 1st.");
                            }
                            
                            use_signup_button = false;
                        }
                    }
                    
                    // See if the event is part of the first event category for "Member/Guest" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_2, con).equals("")) {
                    
                        if ((today >= 20140301 && today < 20140501 && custom_event_count_2 >= 1)
                                || (today >= 20140501 && custom_event_count_2 >= 2)) {
                            
                            if (today >= 20140301 && today < 20150401) {
                                instructions.add("You are already signed up for the maximum allowed Mens Member Guest events at this time (1).");
                                instructions.add("You will be able to sign up for one additional Mens Member Guest event on April 1st.");
                            } else if (today >= 20140501) {
                                instructions.add("You are already signed up for the maximum allowed Mens Member Guest events (2).");
                            }
                            
                            use_signup_button = false;
                        }
                    }
                }

                // Configure instructions and buttons
                // After old-skin has been abandond, this configuration should be moved into javascript,
                // allowing customs to be accomplished with club.js 

                if (teams_reg >= max) {         // if no room for more teams

                    // If Rolling Hills - SA, do not display sign up button if max teams has been reached.  Members must call Proshop to be placed on the wait list.
                    if (club.equals("rollinghillsgc")) {

                        instructions.add("This event is currently <b>full</b>.");
                        instructions.add("To be put on the waiting list for this event, send an email to tournaments@arabiangolf.com stating your Name, Member ID, and the event you wish to place on the waiting list for.");
                        use_signup_button = false;

                    } else {

                        if (pairings.equalsIgnoreCase("Proshop")) {

                            instructions.add("<b>Warning:</b> this event already has " + count_reg + " members registered.");
                            if (json_mode) {
                                instructions.add("To be added to the waiting list, click [signup_button]");
                            } else {
                                instructions.add("To be added to the waiting list, click here:");
                            }

                        } else {

                            instructions.add("<b>Warning:</b> this event already has " + teams_reg + " teams registered.");
                            if (json_mode) {
                                instructions.add("To add a team to the <b>waiting list</b>, click [signup_button]");
                                if (hideNames == 0  && (viewList) && (select_count > 0)) {  // if NOT hiding names, and there are teams to select
                                    instructions.add("To join an existing team, click [view_edit_button]");
                                }
                            } else {
                                if (hideNames == 0) {      // if NOT hiding names
                                    instructions.add("To join an existing team, click on the Select button in the table below.");
                                }
                                instructions.add("To add a team to the <b>waiting list</b>, click here:");
                            }
                        }
                    }

                } else {

                    if (size == 1) {      // only member can sign up (no guests or other members
                        instructions.add("Because of the format selected for this event, you can only register yourself.");
                        if (json_mode) {
                            instructions.add("To sign up for this event, click [signup_button]");
                        } else {
                            instructions.add("To sign up for this event, click here:");
                        }
                    } else {              // allow team sign up 
                        
                        if (activity_id == 0) {
                                if (use_signup_button) {
                                    if (hideNames == 0  && viewList && select_count > 0 && size > minsize) {      // if NOT hiding names, then members can join a team (only when team size is larger than minsize)
                                        instructions.add("The format of this event allows you to join an existing team or register a new team.");
                                        if (json_mode) {
                                            instructions.add("To join an existing team, click [view_edit_button]");
                                        } else {
                                            instructions.add("To join an existing team, click on the Select button in the table below.");
                                        }
                                        signup_button_type = "new_team_button";
                                    }
                                    if (json_mode) {
                                        instructions.add("To register a new team, click [signup_button]");
                                    } else {
                                        instructions.add("To register a new team, click here:");
                                    }
                                }
                            if (viewList == true && select_count > 0 && size > minsize) {          // if showing the event list
                                instructions.add("If 'Moved' is listed in the Select column, that team has been moved to the tee sheet.");
                            }
                        } else {
                            if (hideNames == 0  && viewList && select_count > 0 && size > minsize) {      // if NOT hiding names, then members can join a team
                                instructions.add("The format of this event allows you to join an existing sign up or register a new sign up.");
                                if (json_mode) {
                                    instructions.add("To join an existing sign up, click [view_edit_button]");
                                } else {
                                    instructions.add("To join an existing sign up, click on the Select button in the table below.");
                                }
                            }
                            if (json_mode) {
                                instructions.add("To register a new sign up, click [signup_button]");
                            } else {
                                instructions.add("To register a new sign up, click here:");
                            }
                        }
                    }
                }

                //
                // Create current players/teams status text
                //
                team_player_status = "";
                
                String players_term = "players";
                String teams_term = "teams";
                
                if (activity_id != 0) {
                    players_term = "participants";
                    teams_term = "sign ups";
                }
                
                if (count_reg == 0) {      // if no one signed up
                    team_player_status = "There are currently no " + players_term + " registered for this event";
                } else {
                    if (pairings.equals("Member")) {
                        team_player_status = "There are currently " + teams_reg + " " + teams_term + " (" + count_reg + " " + players_term + ") registered for this event";
                        if (teams_wait > 0) {
                            team_player_status += " and " + teams_wait + " " + teams_term + " (" + count_wait + " " + players_term + ") on the wait list";
                        }
                    } else {
                        team_player_status = "There are currently " + count_reg + " " + players_term + " registered for this event";
                        if (count_wait > 0) {
                            team_player_status += " and " + count_wait + " " + players_term + " on the wait list";
                        }
                    }
                }

                //
                // Create dashboard/itinerary
                //
                Map<String, String> mddate = new LinkedHashMap<String, String>();
                mddate.put("value", ((season == 0) ? month + "/" + day + "/" + year : "Season Long"));
                if (season == 0) {
                    Map<String, String> mdtime = new LinkedHashMap<String, String>();
                    mdtime.put("value", act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm);
                    dashboard_map.put("Time", mdtime);
                    if (activity_id == 0) {
                        Map<String, String> mdtype = new LinkedHashMap<String, String>();
                        mdtype.put("value", ((type != 0) ? "Shotgun" : "Tee Time"));
                        dashboard_map.put("Type", mdtype);
                    }
                }
                dashboard_map.put("Date", mddate);
                if (activity_id == 0) {
                    if (!course_display.equals("")) {
                        Map<String, String> mdcourse = new LinkedHashMap<String, String>();
                        mdcourse.put("value", course_display);
                        mdcourse.put("class", "double_width");
                        dashboard_map.put("Course", mdcourse);
                    }
                    Map<String, String> mdfb = new LinkedHashMap<String, String>();
                    mdfb.put("value", fb);
                    dashboard_map.put("Front/Back", mdfb);
                }
                Map<String, String> mdformat = new LinkedHashMap<String, String>();
                mdformat.put("value", format);
                mdformat.put("class", "double_width");
                dashboard_map.put("Format", mdformat);
                Map<String, String> mdgender = new LinkedHashMap<String, String>();
                mdgender.put("value", Labels.gender_opts[gender]);
                dashboard_map.put("Gender", mdgender);
                
                if (activity_id == 0) {
                    Map<String, String> mdteams = new LinkedHashMap<String, String>();
                    mdteams.put("value", pairings);
                    mdteams.put("class", "double_width");
                    dashboard_map.put("Teams Selected By", mdteams);
                }
                
                if (activity_id == 0) {
                    Map<String, Integer> mdmaxteams = new LinkedHashMap<String, Integer>();
                    mdmaxteams.put("value", max);
                    dashboard_map.put("Max # of Teams", mdmaxteams);
                } else {
                    Map<String, Integer> mdmaxsignups = new LinkedHashMap<String, Integer>();
                    mdmaxsignups.put("value", max);
                    dashboard_map.put("Max # of Sign Ups", mdmaxsignups);
                }
                
                if (activity_id == 0) {
                    Map<String, Integer> mdteamsize = new LinkedHashMap<String, Integer>();
                    mdteamsize.put("value", size);
                    dashboard_map.put("Team Size", mdteamsize);
                    Map<String, Integer> mdholes = new LinkedHashMap<String, Integer>();
                    mdholes.put("value", holes);
                    dashboard_map.put("Holes", mdholes);
                    Map<String, Integer> mdminsignup = new LinkedHashMap<String, Integer>();
                    mdminsignup.put("value", minsize);
                    dashboard_map.put("Min. Sign-Up Size", mdminsignup);
                }
                
                Map<String, Integer> mdguestspermem = new LinkedHashMap<String, Integer>();
                mdguestspermem.put("value", guests);
                dashboard_map.put("Guests per Member", mdguestspermem);
                
                Map<String, String> mdcostperguest = new LinkedHashMap<String, String>();
                mdcostperguest.put("value", gstcost);
                dashboard_map.put("Cost per Guest", mdcostperguest);
                
                Map<String, String> mdcostpermem = new LinkedHashMap<String, String>();
                mdcostpermem.put("value", memcost);
                dashboard_map.put("Cost per Member", mdcostpermem);
                
                Map<String, String> mdsignupby = new LinkedHashMap<String, String>();
                mdsignupby.put("value", c_hr + ":" + Utilities.ensureDoubleDigit(c_min) + " " + c_ampm + " on " + c_month + "/" + c_day + "/" + c_year);
                mdsignupby.put("class", "double_width");
                dashboard_map.put("Must Sign Up By", mdsignupby);
                
                if (show_itinerary && (itin.length() > 0)) {
                    Map<String, String> mditiner = new LinkedHashMap<String, String>();
                    mditiner.put("value", itin);
                    mditiner.put("class", "box_with_title");
                    dashboard_map.put("Itinerary", mditiner);
                }

                //
                // Output results
                //
                if (json_mode) {
                    // Json mode
                    Map<String, Integer> eventdate = new LinkedHashMap<String, Integer>();
                    eventdate.put("month", month);
                    eventdate.put("day", day);
                    eventdate.put("year", year);
                    eventdate.put("hour", act_hr_24);
                    eventdate.put("minute", act_min);
                    event_map.put("event_date", eventdate);
                    Map<String, Integer> signupdate = new LinkedHashMap<String, Integer>();
                    signupdate.put("month", c_month);
                    signupdate.put("day", c_day);
                    signupdate.put("year", c_year);
                    signupdate.put("hour", c_hr_24);
                    signupdate.put("minute", c_min);
                    event_map.put("sign_up_date", signupdate);
                    event_map.put("instructions", instructions);
                    event_map.put("team_player_status", team_player_status);
                    event_map.put("use_signup_button", use_signup_button);
                    event_map.put("signup_button_type", signup_button_type);
                    event_map.put("signup_count", signup);
                    event_map.put("view_list", ((viewList || signup > 0) && count_reg != 0));
                    event_map.put("select_count", select_count);
                    if (ext_login) {
                        event_map.put("ext-login", "yes");    // indicate user came from email link
                    }
                    event_map.put("reason_msg", reason_msg);
                    event_map.put("dashboard_map", dashboard_map);
                    event_map.put("column_map", column_map);
                    event_map.put("row_map", row_map);
                    event_map.put("user_to_event_map", user_to_event_map);

                    /*
                    event_map.put("format", format);
                    event_map.put("season", season);
                    event_map.put("course", course);
                    event_map.put("course_display", course_display);
                    event_map.put("sess_activity_id", sess_activity_id);
                    event_map.put("gender", Labels.gender_opts[gender]);
                    event_map.put("pairings", pairings);
                    event_map.put("teams_maximum", max);
                    event_map.put("teams_registered", teams_reg);
                    event_map.put("team_size", size);
                    event_map.put("holes", holes);
                    event_map.put("minimum_signup_size", minsize);
                    event_map.put("guests_per_member", guests);
                    event_map.put("cost_per_guest", gstcost);
                    event_map.put("show_itinerary", show_itinerary);
                    event_map.put("itinerary", itin);
                    event_map.put("hide_names", hideNames);
                    event_map.put("pairings", pairings);
                    event_map.put("teams_wait", teams_wait);
                    event_map.put("count_wait", count_wait);
                    event_map.put("count_reg", count_reg);
                     * */


                    // Output JSON
                    out.print(gson_obj.toJson(event_map));

                } else {  // Old skin
                    //
                    //   build the HTML page for the display
                    //
                    out.println(SystemUtils.HeadTitle("Member Event Sign Up Page"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
                    if (ext_login == false) {                 // if NOT from an external login
                        SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
                    }
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

                    if (ext_login == true) {                 // if from an external login - add header (no frames)

                        Utilities.getExtMainTop(req, out, session, con);
                    }

                    out.println("<table border=\"0\" valign=\"top\">");       // table for main page
                    out.println("<tr><td align=\"center\" valign=\"top\">");
                    out.println("<font size=\"2\">");

                    out.println("<form action=\"Member_evntSignUp\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");

                    if (ext_login == true) {                 // if from an external login

                        out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                    }

                    out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" width=\"530\" cellpadding=\"5\" cellspacing=\"3\" valign=\"top\">");
                    out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                    out.println("<font color=\"ffffff\" size=\"3\">");
                    out.println("<b>" + name + "</b>");
                    out.println("</font></td></tr>");
                    out.println("<tr><td align=\"left\">");
                    out.println("<font size=\"2\">");
                    out.println("<b>Date:</b>&nbsp;&nbsp; " + ((season == 0) ? month + "/" + day + "/" + year : "Season Long"));
                    if (season == 0) {
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("<b>Time:</b>&nbsp;&nbsp; " + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm);
                        if (activity_id == 0) {
                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                            if (type != 0) {
                                out.println("<b>Type:</b>&nbsp;&nbsp; Shotgun<br><br>");
                            } else {
                                out.println("<b>Type:</b>&nbsp;&nbsp; Tee Times<br><br>");
                            }
                        } else {
                            out.println("<br><br>");
                        }
                    } else {
                        out.println("<br><br>");
                    }
                    if (activity_id == 0) {
                        if (!course.equals("")) {
                            out.println("<b>Course:</b>&nbsp;&nbsp; " + course_display);
                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        }

                        out.println("<b>Front/Back:</b>&nbsp;&nbsp; " + fb + "<br><br>");
                    }
                    out.println("<b>Format:</b>&nbsp;&nbsp; " + format);
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<b>Gender:</b>&nbsp;&nbsp; " + Labels.gender_opts[gender] + "<br><br>");
                    out.println("<b>Teams Selected By:</b>&nbsp;&nbsp; " + pairings);
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<b># of Teams:</b>&nbsp;&nbsp; " + max);
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<b>Team Size:</b>&nbsp;&nbsp; " + size + "<br><br>");
                    if (activity_id == 0) {
                        out.println("<b>Holes:</b>&nbsp;&nbsp; " + holes);
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    }
                    out.println("<b>Min. Sign-Up Size:</b>&nbsp;&nbsp; " + minsize + "<br><br>");
                    out.println("<nobr><b>Guests per Member:</b>&nbsp;&nbsp;" + guests);
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<b>Cost per Guest:</b>&nbsp;&nbsp;" + gstcost);
                    out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    out.println("<b>Cost per Member:</b>&nbsp;&nbsp;" + memcost + "</nobr><br><br>");
                    out.println("<b>Must Sign Up By:</b>&nbsp;&nbsp; " + c_hr + ":" + Utilities.ensureDoubleDigit(c_min) + " " + c_ampm + " on " + c_month + "/" + c_day + "/" + c_year);
                    if (show_itinerary) {
                        out.println("<br><br>");
                        out.println("<b>Itinerary:</b>&nbsp;&nbsp; " + itin + "<br>");
                    }
                    out.println("</font></td></tr>");
                    out.println("<tr><td align=\"center\">");
                    out.println("<font size=\"2\">");



                    if (signup == 0) {         // if no signup at this time

                        if (reason_msg.length() > 0) {
                            out.println("<b>" + reason_msg + "</b>");
                        }

                    } else {
                        // Output instructions and new team/member sign-up button
                        for (int i2 = 0; i2 < instructions.size(); i2++) {
                            if (i2 > 0) {
                                out.println("<br>"); // Line break between each instruction
                            }
                            if (i2 == 1) {
                                out.println("<br>"); // Double line break after the first instruction
                            }
                            out.println(instructions.get(i2));
                        }
                        if (use_signup_button) {
                            out.println("<input type=\"hidden\" name=\"new\" value=\"new\">");
                            if (signup_button_type.equals("sign_up_button")) {
                                out.println(" <input type=\"submit\" value=\"Sign Up\" >");
                            } else {
                                out.println(" <input type=\"submit\" value=\"New Team\" >");
                            }
                        }

                        out.println("<br>");

                    }
                    out.println("</font></td>");
                    out.println("</tr></table></form>");             // end of header table

                    if (signup > 0 || viewList == true) {         // if member can signup or view the list at this time

                        out.println("<br>" + team_player_status + "<br><br>");

                        if (count_reg != 0) {      // if some players/teams are signed up

                            /*
                            if (pairings.equals( "Member" )) {
                            
                            out.println("<br>There are currently " + teams_reg + " teams and " + count_reg + " players registered for this event.<br>");
                            } else {
                            out.println("<br>There are currently " + count_reg + " players registered for this event.<br>");
                            }
                             */

                            if (teams_reg > 5) {

                                if (ext_login == false) {                 // if NOT from an external login

                                    if (!index.equals("") && index.equals("0")) {
                                        out.println("<button onClick=\"window.close();\">Close</button>");
                                    } else {
                                        out.println("<font size=\"2\">");
                                        if (!index.equals("")) {
                                            if (index.equals("999")) {
                                                out.println("<form method=\"get\" action=\"Member_teelist\">");
                                            } else {
                                                out.println("<form method=\"get\" action=\"Member_teelist_list\">");
                                            }
                                        } else {
                                            out.println("<form method=\"get\" action=\"Member_events\">");
                                        }
                                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                        out.println("</form></font>");
                                    }

                                } else {     // from external login - close the window

                                    out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
                                }
                            }
                            out.println("<b>Players currently registered for this event:</b>");

                            if (signup > 0) {         // if member can signup at this time

                                out.println("<form action=\"Member_evntSignUp\" method=\"post\" target=\"_top\">");
                                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                if (ext_login == true) {                 // if from an external login

                                    out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                                }
                            }



                            out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\" valign=\"top\">");
                            out.println("<tr bgcolor=\"#336633\" style=\"color: white\">");

                            // Output table column headers
                            for (Map.Entry<String, Map> entry : column_map.entrySet()) {
                                temp_string1 = "";
                                temp_string2 = "";
                                temp_value = "";
                                if (((Map) entry.getValue()).containsKey("class")) {
                                    temp_string1 = (String) ((Map) entry.getValue()).get("class");
                                }
                                if (((Map) entry.getValue()).containsKey("value")) {
                                    if (!((String) ((Map) entry.getValue()).get("value")).equals("")) {
                                        temp_value = (String) ((Map) entry.getValue()).get("value");
                                    }
                                }
                                if (((Map) entry.getValue()).containsKey("small_text")) {
                                    if (!((String) ((Map) entry.getValue()).get("small_text")).equals("")) {
                                        temp_string2 = (String) ((Map) entry.getValue()).get("small_text");
                                    }
                                }
                                if (temp_value.equals("") && temp_string2.equals("")) {
                                    temp_value = "&nbsp;"; // there is no content for this cell, set it to a non-breaking space (IE6 w/no css compat)
                                }
                                out.print("<td align=\"center\">");
                                if (!temp_value.equals("")) {
                                    if (temp_string1.equals("cw_head")) {
                                        out.print("<font size=\"1\"><u><b>");
                                    } else {
                                        out.print("<font size=\"2\"><u><b>");
                                    }
                                    out.print(temp_value);
                                    out.println("</b></u></font>");
                                }
                                if (!temp_string2.equals("")) {
                                    out.print("<font size=\"1\"><u>");
                                    out.print(temp_string2);
                                    out.println("</u></font>");
                                }
                                out.println("</td>");
                            }

                            out.println("</tr>");

                            //
                            //  Output all rows
                            //
                            for (Map.Entry<String, Map<String, Map>> row_set : row_map.entrySet()) {

                                out.println("<tr>");
                                // Output cells for a given row
                                for (Map.Entry<String, Map> entry : ((Map<String, Map>) row_set.getValue()).entrySet()) {

                                    value_style_open = "<font size=\"2\">";
                                    value_style_close = "</font>";
                                    cell_style = "align=\"center\"";
                                    temp_string1 = "";
                                    temp_string2 = "";
                                    temp_value = "";
                                    cell_type = "";
                                    if (((Map) entry.getValue()).containsKey("type")) {
                                        if (!((String) ((Map) entry.getValue()).get("type")).equals("")) {
                                            cell_type = (String) ((Map) entry.getValue()).get("type");
                                        }
                                    }
                                    if (((Map) entry.getValue()).containsKey("class")) {
                                        temp_string1 = (String) ((Map) entry.getValue()).get("class");
                                        if (temp_string1.equals("cw_cell")) {
                                            cell_style = "bgcolor=\"white\" align=\"center\"";
                                        }
                                    }
                                    if (((Map) entry.getValue()).containsKey("value")) {
                                        if (!((String) ((Map) entry.getValue()).get("value")).equals("")) {
                                            temp_value = (String) ((Map) entry.getValue()).get("value");
                                        }
                                    }
                                    if (((Map) entry.getValue()).containsKey("small_text")) {
                                        if (!((String) ((Map) entry.getValue()).get("small_text")).equals("")) {
                                            temp_string2 = (String) ((Map) entry.getValue()).get("small_text");
                                        }
                                    }
                                    if (temp_value.equals("") && temp_string2.equals("")) {
                                        temp_value = "&nbsp;"; // there is no content for this cell, set it to a non-breaking space (IE6 w/no css compat)
                                    }
                                    out.print("<td " + cell_style + ">");
                                    if (cell_type.equals("select")) {
                                        out.print("<input type=\"submit\" value=\"Select\" name=\"id:" + temp_value + "\" id=\"id:" + temp_value + "\">");
                                    } else {
                                        if (!temp_value.equals("")) {

                                            out.print(value_style_open);
                                            out.print(temp_value);
                                            out.print(value_style_close);

                                        }
                                        if (!temp_string2.equals("")) {
                                            //out.print("<font size=\"1\"><u>");
                                            out.print("  " + temp_string2);
                                            //out.println("</u></font>");
                                        }
                                    }
                                    out.println("</td>");
                                }

                                out.println("</tr>");

                            }                   // end of while


                            out.println("</table></form>");              // end of player table
                        }  // End Old Skin HTML output

                    }    // end of IF signup     

                    out.println("</font></td></tr>");
                    out.println("</table>");                   // end of main page table

                    if (ext_login == false) {                 // if NOT from an external login

                        if (!index.equals("") && index.equals("0")) {        // Came from tee sheet
                            out.println("<br><button onClick=\"window.close();\">Close</button>");
                        } else {
                            out.println("<br><font size=\"2\">");
                            if (!index.equals("")) {
                                if (index.equals("999")) {
                                    out.println("<form method=\"get\" action=\"Member_teelist\">");
                                } else {
                                    out.println("<form method=\"get\" action=\"Member_teelist_list\">");
                                }
                            } else {
                                out.println("<form method=\"get\" action=\"Member_events\">");
                            }
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");
                        }

                    } else {     // from external login - close the window

                        out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
                    }

                    //
                    //  End of HTML page
                    //
                    out.println("</center></font></body></html>");

                }             // end of IF New Skin or Old
                out.close();

            } else {    // event name not found

                out.println(SystemUtils.HeadTitle("Database Error"));
                out.println("<BODY bgcolor=\"ccccaa\">");
                out.println("<CENTER>");
                out.println("<BR><BR><H3>Procedure Error</H3>");
                out.println("<BR><BR>Sorry, we are unable to locate the selected event (" + name + ").");
                out.println("<BR><BR>Please try again later.");
                out.println("<BR><BR>If problem persists, contact your " + activity_name + " shop (provide this information).");
                if (ext_login == false) {                 // if NOT from an external login
                    out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
                } else {
                    out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
                }
                out.println("</CENTER></BODY></HTML>");
                out.close();

            }   // end of IF event

            stmt.close();

        } catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY bgcolor=\"ccccaa\">");
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Error:" + exc.getMessage());
            //out.println("<BR>Error:" + exc.toString());
            out.println("<BR><BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your " + activity_name + " shop (provide this information).");
            if (ext_login == false) {                 // if NOT from an external login
                out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
            } else {
                out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
        }

    }   // end of doPost
}
