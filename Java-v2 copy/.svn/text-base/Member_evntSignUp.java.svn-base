
/***************************************************************************************
 *   Member_evntSignUp:  This servlet will process a request to register a member for
 *                       an event.
 *
 *
 *
 *   called by:  Member_events
 *
 *
 *   created: 2/11/2003   Bob P.
 *
 *   last updated:
 *
 *       3/03/12   Edina CC - custom to allow adults to access their dependents' event registrations - like Denver CC (case 2378).
 *       2/27/14   Merion GC (merion) - Added custom to restrict members to only signing up to a certain number of "Mens Stag Day" and "Mens Member Guest" events at a given time, based on the current date (case 2369).
 *       1/31/14   Updated email/phone/other extra info fields for event signups to allow for more characters.
 *      11/08/13   Lomas Santa Fe CC (lomassantafecc) - Display "GHIN #" instead of "HDCP #" on the event signup page (case 2323).
 *       9/06/13   BallenIsles CC (ballenisles) - Added custom to prevent the automated move-up of groups off of the event wait list (case 2296).
 *       8/22/13   Bald Peak (baldpeak) - Added custom to only allow the originator of a tee time to make or change the Notes (case 2293).
 *       6/05/13   The Country Club (tcclub) - Updated enforce guest type custom to only prepend "Tournament" on the front if the player name doesn't already start with "Tournament" (case 1458).
 *       5/22/13   The Country Club (tcclub) - Updated enforce guest type custom to plug in a guest type of "Tournament" if a manually typed player is not recognized (case 1458).
 *       4/04/13   Fixed issue where userg values were not getting populated into the email parameter block, which was causing tracked guest email notifications to not send out.
 *       3/21/13   TPC Snoqualmie Ridge (snoqualmieridge) - Updated custom to only block player2 in season long events if it's already populated with a player (case 1599).
 *       3/19/13   Fixed typo in link to event help document.
 *       2/28/13   Denver CC - Do not allow anyone other than the orriginator to remove players if activity is Juniors or Fitness.
 *       2/07/13   Discovery Bay CC (discoverybay) - Added custom to prevent Single Golf members from being associated with a Junior guest type in events (case 2219).
 *       2/05/13   Fixed issue where .equals was being used instead of .equalsIgnoreCase when checking if a username was part of a tee time they're trying to cancel.
 *      11/27/12   Tweak iframe resize code
 *      10/16/12   Denver CC - custom to allow adults to access/cancel their dependents' event registrations.
 *       9/18/12   Player names and MoTs will now be included in the email notification indicating a team has been moved off of the wait list for an event.
 *       9/12/12   Updated invData() error message for when an invalid player is entered so that it doesn't print out empty quotes for any blank player slots.
 *       9/12/12   The Country Club (tcclub) - Reinstated custom to automatically plug in the gtype "Guest" before an unrecognized, manually typed in player name (case 1458).
 *       7/30/12   Added loop to remove any modes of transportation that aren't allowed for this event. This was being done in the old skin code, but not for the new skin.
 *       5/17/12   Updates to correct external login with new skin.
 *       1/18/12   Updated for new skin
 *      12/05/11   Olympic Club (olyclub) - Added custom to change the success message that displays after an event signup has been booked (case 2089).
 *      11/30/11   Olympic Club (olyclub) - Removed custom to restrict members from booking additional players since they decided they didn't need it (case 2068).
 *      11/30/11   Olympic Club (olyclub) - Only display one guest type for members: "Event Guest" (case 2083).
 *      11/29/11   Olympic Club (olyclub) - Members can only book themselves into an event team unless the proshop created their team (case 2068).
 *      11/28/11   Olympic Club (olyclub) - Display "GHIN #" instead of "HDCP #" on the event signup page (case 2074).
 *      11/17/11   Updated checkMemNotice() call to the updated call that includes sess_activity_id to prevent golf messages appearing for FlxRez events, and vice-versa.
 *       9/21/11   Updated extraInfoForm() method so userg values are passed through it, since they were getting set to null previously if any question were asked.
 *       9/20/11   Rolling Hills GC - SA (rollinghillsgc) - Do not allow members to make any changes to the HDCP # field for any player.
 *       9/13/11   Monterey Peninsula CC (mpccpb) - Display "GHIN #" instead of "HDCP #" on the event signup page.
 *       7/11/11   Added processing to accomodate members coming from the tee sheet for event signup, and close the window on exit instead of returning to another page.
 *       3/12/11   Added calls to utilize the event signup log
 *       2/15/11   Add processing to handle member coming in from Login via link in an email message.
 *      10/20/10   Populate new parmEmail fields
 *       6/08/10   Added code to guest tracking verification to allow for the guest "TBA" option
 *       5/26/10   Shift the players up when one or more players are removed from request and other players follow the empty slot(s).
 *                 There must be a player in slot1 if any exist in the request - required by drag-n-drop.
 *       5/20/10   Portland GC - back out the wiatlist custom.
 *       5/13/10   Fixed issue where event signup id was getting passed in place of the event id for verifySlot.checkEventRests() method
 *       5/06/10   Display name list options for single signup events.
 *       4/23/10   Portland GC - No automated wait list move ups (Case# 1811).
 *       4/23/10   Correct the wait list processing so entries are taken off the wait list when a group cancels.
 *                 There was an invalid field (activity_id) in the WHERE clause of a query for evntsup2.
 *       4/06/10   Added guest tracking processing
 *       3/23/10   Central Washington Chapter PGA (cwcpga) - Display ghin numbers in name list
 *       1/29/10   Include event signups for CC of Naples custom membership type quota restriction (case 1704).
 *       1/12/10   Remove check to see if member is already registered for the event.  This was preventing group leaders from
 *                 signing up other members of the group (requested by The CC).
 *       1/08/09   When setting user's name as first open player respect the max team size parameter from the event config
 *      12/29/09   Only look at event signups that aren't inactive when determining how many teams are currently signed up for an event.
 *      12/09/09   When looking for event signups only check those that are active.
 *      12/03/09   Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *      10/04/09   Added activity isolation to the buddy list
 *       9/30/09   Add support for Activities
 *       8/12/09   Changed how Mode of Trans select boxes are built so that member default MoT are not added to the select box when not allowed for this event
 *       7/30/09   Add focus setting for moveguest js method to improve efficiency
 *       5/15/09   Add maxlength to text boxes in extraInfoForm (also increased size of db fields for clubname & address)
 *       5/14/09   Do not verify tmodes or pro only tmodes for season long events
 *       5/13/09   Minor fixes for season long events (hdcp numbers/formatting/tmodes)
 *       4/17/09   Change the wording of the Guest note below the player box.
 *       3/02/09   Add hdcp number and gender defaulting for current user & partners
 *       2/11/09   Custom for Hazeltine - do not allow members to signup for Invitational unless they
 *                 have a sub-type of Invite Priority (case 1585).
 *       1/07/09   Snoqualmie Ridge - do not allow members to cancel a request or remove players (case 1599).
 *      12/04/08   Fixed guest association prompt bug when requiring gender & ghin numbers
 *      11/26/08   Changed required color for Blackstone
 *      11/26/08   Fixed defaulting of gender/ghin for members with no default tmode set
 *      11/10/08   Added changes for additional signup information
 *      11/10/08   Add code to default the tmode to the users preference
 *      10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *       8/19/08   Remove synchronized mechanisms and change evntsup2b id field to a unique auto-incrementing field.
 *       7/29/08   Remove custom for Gallery (email pro all notifications) - they will use the new config option.
 *       5/09/08   Medinah CC - No automated wait list move ups for Medinah (Case# 1475)
 *       5/05/08   Gather additional guest information for events configured for export
 *       4/22/08   The CC - remove custom for case #1458 - they changed their minds.
 *       4/22/08   The CC - Enforce a default guest type if player name not member name for tcclub (case #1458)
 *       4/16/08   Make wait list notice more prominent
 *       4/02/08   Suppress the c/w option for season long events
 *       3/27/08   Added support for new event fields, season, email1, email2 - Case 1372 & 1408
 *       3/07/08   Piedmont & Gallery - add pro emails to all notifications (cases 1395 & 1398).
 *      12/04/07   Berkeley Hall - Remove the trans mode of 'REC' and 'CMP' (Case 1341)
 *      11/30/07   Fixed guest associations when only 1 member included in signup
 *      10/15/07   Pinnacle Peak CC - Remove the trans mode of 'NC' (Case 1288)
 *       9/25/07   Add enforcement of new minimum sign-up size option
 *       8/20/07   Modified call to verifySlot.checkMemNotice
 *       6/26/07   When pulling a handicap from member2b get it from the g_hancap field instead of c_hancap
 *       4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *       4/06/07   Do not include members that are inactive (new inact flag in member2b).
 *       3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *       2/27/07   Add new 'Member Notice' processing - display pro-defined message if found.
 *       2/15/07   Set the clubname and course name for getClub.getParms.
 *       9/11/06   Wellesley - add custom to force 'Tourney Guest' as the only guest type.
 *       3/17/06   Stanwich Club - add custom message in event signup confirmation.
 *       9/15/05   Medinah - do not show guest types that are not for events.
 *       5/13/05   Add processing for restrictions on events (member may not have access to event).
 *       4/01/05   Put the 'X' option by itself to avoid confusion with guest types.
 *       2/07/05   Replace 'back(1)' javascripts with a form to return to events2.
 *      12/09/04   Ver 5 - Change Member Name Alphabit table to common table.
 *      11/20/04   Ver 5 - allow for return to Member_teelist (add index=).
 *       9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *       7/25/04   Make sure there is room in event before changing wait to registered after
 *                 a group is cancelled.
 *       2/16/04   Add support for configurable transportation modes.
 *       1/13/04   JAG  Modifications to match new color scheme
 *       7/18/03   Enhancements for Version 3 of the software.
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
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmSlot;
import com.foretees.common.parmCourse;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.alphaTable;
import com.foretees.common.congressionalCustom;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.parmSlotPage;
import com.foretees.common.formUtil;

public class Member_evntSignUp extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

    //
    //******************************************************************************
    //  doPost processing
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

        ResultSet rs = null;

        HttpSession session = null;

        boolean ext_login = false;        // from external login

      
        //  This will print all parms for testing purposes !!!!!!!!!  
        /*
        //if (req.getParameter("skip8") != null) {   // only do last entry
        if (ext_login == false) {                    // do any entry
            
              out = resp.getWriter();     

              Enumeration enum9 = req.getParameterNames();

              out.println(SystemUtils.HeadTitle("Database Error"));
              out.println("<BODY><CENTER>");
              out.println("<BR><BR><H2>Parameters Received</H2>");

              out.println("<BR><BR>Query String: ");
              out.println(req.getQueryString());
              out.println();

              out.println("<BR><BR>Request Parms: ");

              while (enum9.hasMoreElements()) {

                 String name = (String) enum9.nextElement();
                 String values[] = req.getParameterValues(name);
                 if (values != null) {
                    for (int i=0; i<values.length; i++) {

                       out.println("<BR>" +name+ " (" +i+ "): " +values[i]);
                    }
                 }
              }

              out.println("<BR><BR>");
              out.println("<BR><BR><a href=\"Logout\">Exit</a>");
              out.println("</CENTER></BODY></HTML>");
              out.close();
              return;
        }
        */
       
         
        
        if (req.getParameter("ext-login") != null || req.getParameter("ext-dReq") != null) {    // make sure we check for both (we only need one, but somehow ended up using 2 parms for this)

            ext_login = true;           // indicate external login (from event link in email)
            
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

        Connection con = SystemUtils.getCon(session);             // get DB connection
        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#CCCCAA\">");
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</center></body></html>");
            out.close();
            return;
        }

        //
        //  Get this session's username and full name
        //
        String user = "";
        if (ext_login == false) {
            user = (String) session.getAttribute("user");
        } else {
            user = (String) session.getAttribute("ext-user");       // get this user's username - external login (different location for security)
        }
        String club = (String) session.getAttribute("club");
        //String caller = (String)session.getAttribute("caller");
        String userName = (String) session.getAttribute("name");   // get users full name
        String pcw = (String) session.getAttribute("wc");          // get users walk/cart preference
        int sess_activity_id = (Integer) session.getAttribute("activity_id");
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        
        boolean json_mode = (req.getParameter("json_mode") != null);

        //
        // Process request according to which 'submit' button was selected
        //
        //      'id'      - a request from Member_events
        //      'cancel'  - a cancel request from self (return with no changes)
        //      'letter'  - a request to list member names (from self)
        //      'buddy'   - a request to display the Partner List (from self)
        //      'submitForm'  - a reservation request (from self)
        //      'remove'  - a 'cancel reservation' request (from self - Cancel Tee Time)
        //      'return'  - a return from verify
        //
        if (req.getParameter("cancel") != null) {

            cancelReq(req, out, con);                      // process cancel request
            return;
        }


        //
        //  if user submitting request to update or cancel the entry - go process
        //
        if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

            verify(req, out, con, session, resp);            // process reservation requests request
            return;
        }

        // Create fill slot page parameters we already know, fill in the rest later
        parmSlotPage slotPageParms = new parmSlotPage();

        slotPageParms.club = club;
        slotPageParms.club_name = clubName;
        slotPageParms.slot_url = "Member_evntSignUp";
        //slotPageParms.notice_message = "";
        slotPageParms.slot_help_url = "../member_help_evnt_instruct.htm";
        slotPageParms.activity_id = sess_activity_id;
        //slotPageParms.member_tbd_text = "Player";
        slotPageParms.show_fb = true;
        slotPageParms.show_transport = true;
        slotPageParms.user = user;
        slotPageParms.mship = (String) session.getAttribute("mship");
        slotPageParms.mtype = (String) session.getAttribute("mtype");
        slotPageParms.zip_code = (String) session.getAttribute("zipcode");

        // Store request parameters in our slotPageParms, in case we need them for call-back later
        // This will trigger a "uses unchecked or unsafe operations" warning while compiling.  
        // Perhaps there is a better way to do this, but for now it works.
        List<String> reqNames = (ArrayList<String>) Collections.list((Enumeration<String>) req.getParameterNames());
        for (String reqName : reqNames) {
            slotPageParms.callback_map.put(reqName, req.getParameter(reqName));
        }
        slotPageParms.callback_map.put("json_mode", "true");
        boolean proccess_slot = true;

        //
        //   Request is from Member_events -or- its a letter or buddy request from self
        //
        String name = "";
        String pairings = "";
        String course = "";
        String act_ampm = "";
        String hdcp_num = "";
        String gender = "";
        String course_disp = "";
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
        String pname = "";
        String sid = "";
        String sfb = "";
        String notes = "";
        String hides = "";
        String index = "";
        String season_tmode = "";
        String gender1 = "";
        String gender2 = "";
        String gender3 = "";
        String gender4 = "";
        String gender5 = "";
        String ghin1 = "";
        String ghin2 = "";
        String ghin3 = "";
        String ghin4 = "";
        String ghin5 = "";
        String homeclub1 = "";
        String homeclub2 = "";
        String homeclub3 = "";
        String homeclub4 = "";
        String homeclub5 = "";
        String phone1 = "";
        String phone2 = "";
        String phone3 = "";
        String phone4 = "";
        String phone5 = "";
        String address1 = "";
        String address2 = "";
        String address3 = "";
        String address4 = "";
        String address5 = "";
        String email1 = "";
        String email2 = "";
        String email3 = "";
        String email4 = "";
        String email5 = "";
        String shirt1 = "";
        String shirt2 = "";
        String shirt3 = "";
        String shirt4 = "";
        String shirt5 = "";
        String shoe1 = "";
        String shoe2 = "";
        String shoe3 = "";
        String shoe4 = "";
        String shoe5 = "";
        String other1A1 = "";
        String other1A2 = "";
        String other1A3 = "";
        String other2A1 = "";
        String other2A2 = "";
        String other2A3 = "";
        String other3A1 = "";
        String other3A2 = "";
        String other3A3 = "";
        String other4A1 = "";
        String other4A2 = "";
        String other4A3 = "";
        String other5A1 = "";
        String other5A2 = "";
        String other5A3 = "";

        int event_id = 0;
        int month = 0;
        int day = 0;
        int year = 0;
        int act_hr = 0;
        int act_min = 0;
        int size = 0;
        int max = 0;
        int guests = 0;
        //int count = 0;
        int count2 = 0;
        //int in_use = 0;
        int id = 0;
        int fb = 0;
        int hide = 0;
        int xCount = 0;
        int x = 0;
        int i = 0;
        int nowc = 0;
        int time = 0;
        int c_time = 0;
        int gstOnly = 0;
        int season = 0;
        int export_type = 0;
        int ask_gender = 0;
        int ask_hdcp = 0;
        int ask_more = 0;
        int guest_id1 = 0;
        int guest_id2 = 0;
        int guest_id3 = 0;
        int guest_id4 = 0;
        int guest_id5 = 0;

        int[] tmode = new int[16];      // supported transportation modes for event

        long date = 0;
        long c_date = 0;
        //float hndcp1 = 0;
        //float hndcp2 = 0;
        //float hndcp3 = 0;
        //float hndcp4 = 0;
        //float hndcp5 = 0;

        String[] day_table = {"inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        
        ArrayList<String> availableTmodes = new ArrayList<String>();

        //boolean guestError = false;            // init error flag
        boolean newRequest = false;
        boolean blockP1 = false;
        boolean blockP2 = false;
        boolean blockP3 = false;
        boolean blockP4 = false;
        boolean blockP5 = false;
        boolean user_is_owner = true;           // used for custom below
        //boolean allowCancel = true;
        
        //
        //   Get the parms received
        //
        name = req.getParameter("name");
        course = req.getParameter("course");

        if (req.getParameter("index") != null) {         // if from Member_teelist or teelist_list

            index = req.getParameter("index");
        }
        
        if (course == null) course = "";

        
        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(sess_activity_id, con);

        //
        //     Check the club db table for guests
        //
        try {
            parm.club = club;                   // set club name
            parm.course = course;               // and course name

            getClub.getParms(con, parm, sess_activity_id);        // get the club parms

        } catch (Exception exc) {             // SQL Error - ignore guest and x
        }

        //
        //  parm block to hold the course parameters
        //
        parmCourse parmc = new parmCourse();          // allocate a parm block


        // Start configure block.  We will break out of this if we encounter an issue.  
        configure_slot:
        {

            try {

                //
                //   get the event requested
                //
                PreparedStatement stmt = con.prepareStatement(
                        "SELECT * FROM events2b "
                        + "WHERE name = ?");

                stmt.clearParameters();       
                stmt.setString(1, name);
                //stmt.setString(2, course);   // we really don't need to use the couurse name
                rs = stmt.executeQuery();      

                if (rs.next()) {

                    event_id = rs.getInt("event_id");
                    date = rs.getLong("date");          // event date
                    year = rs.getInt("year");
                    month = rs.getInt("month");
                    day = rs.getInt("day");
                    act_hr = rs.getInt("act_hr");
                    act_min = rs.getInt("act_min");
                    pairings = rs.getString("pairings");
                    size = rs.getInt("size");
                    max = rs.getInt("max");
                    guests = rs.getInt("guests");
                    c_date = rs.getLong("c_date");         // cut-off date
                    c_time = rs.getInt("c_time");
                    gstOnly = rs.getInt("gstOnly");
                    sfb = rs.getString("fb");
                    season = rs.getInt("season");
                    export_type = rs.getInt("export_type");
                    ask_gender = rs.getInt("ask_gender");
                    ask_hdcp = rs.getInt("ask_hdcp");
                    tmode[0] = rs.getInt("tmode1");
                    tmode[1] = rs.getInt("tmode2");
                    tmode[2] = rs.getInt("tmode3");
                    tmode[3] = rs.getInt("tmode4");
                    tmode[4] = rs.getInt("tmode5");
                    tmode[5] = rs.getInt("tmode6");
                    tmode[6] = rs.getInt("tmode7");
                    tmode[7] = rs.getInt("tmode8");
                    tmode[8] = rs.getInt("tmode9");
                    tmode[9] = rs.getInt("tmode10");
                    tmode[10] = rs.getInt("tmode11");
                    tmode[11] = rs.getInt("tmode12");
                    tmode[12] = rs.getInt("tmode13");
                    tmode[13] = rs.getInt("tmode14");
                    tmode[14] = rs.getInt("tmode15");
                    tmode[15] = rs.getInt("tmode16");

                } else {

                    if (new_skin) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "[options.notify.dbAccessErrorTitle]";
                        slotPageParms.page_start_notifications.add("[options.notify.dbAccessErrorNotice]");
                        break configure_slot;

                    } else {

                        out.println(SystemUtils.HeadTitle("Database Error"));
                        out.println("<BODY bgcolor=\"#CCCCAA\">");
                        out.println("<CENTER>");
                        out.println("<BR><BR><H3>Database Access Error</H3>");
                        out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
                        out.println("<BR><BR>Please try again later.");
                        out.println("<BR><BR>If problem persists, contact your golf shop.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");
                        if (ext_login == true) {     // if came from external login (no frames)
                            out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
                            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                        } else if (!index.equals("") && index.equals("0")) {
                            out.println("<form action=\"Member_events2\" method=\"post\">");
                        } else {
                            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        }
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                        out.println("</center></body></html>");
                        out.close();
                        return;

                    }
                }  // end of IF event found
                stmt.close();

                //
                //  Get the Modes of Trans for this course
                //
                getParms.getCourse(con, parmc, course);

                //
                //  If Pinnacle Peak remove the trans mode of 'NC' (Case 1288)
                //
                if (club.equals("pinnaclepeak")) {

                    for (i = 0; i < 16; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("NC")) {
                            parmc.tmodea[i] = "";
                        }
                    }
                }

                //
                //  If Berkeley Hall remove the trans mode of 'REC' and 'CMP' (Case 1341)
                //
                if (club.equals("berkeleyhall")) {

                    for (i = 0; i < 16; i++) {

                        if (parmc.tmodea[i].equalsIgnoreCase("REC") || parmc.tmodea[i].equalsIgnoreCase("CMP")) {
                            parmc.tmodea[i] = "";
                        }
                    }
                }
                
                // Remove modes of transportation that aren't being used for this event
                for (i = 0; i < 16; i++) {
                    if (tmode[i] == 0 && !parmc.tmodea[i].equals("")) {
                        parmc.tmodea[i] = "";
                    }
                }
                
                //
                // Get the first valid tmode option
                //
                tmode_loop:
                for (i = 0; i < 16; i++) {
                    if (tmode[i] == 1) {
                        if (!parmc.tmodea[i].equals("")) {
                            season_tmode = parmc.tmodea[i];
                            break tmode_loop;
                        }
                    }
                }
                


                //
                //  Create time values
                //
                time = (act_hr * 100) + act_min;       // actual time of event

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

                //
                //  Override team size if proshop pairings (just in case)
                //
                if (!pairings.equalsIgnoreCase("Member")) {

                    size = 1;       // set size to one for proshop pairings (size is # per team)
                }

                if (req.getParameter("new") != null) {     // if 'new' request first page display

                    newRequest = true;

                    /*    // REMOVED 1/12/10 per request from The CC - blocks group leader from registering other members of group!!
                    //
                    //  see if user is already signed up
                    //
                    PreparedStatement pstmtu = con.prepareStatement (
                    "SELECT name FROM evntsup2b " +
                    "WHERE name = ? AND courseName = ? AND inactive = 0 AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?) ");
                    
                    pstmtu.clearParameters();        // clear the parms
                    pstmtu.setString(1, name);
                    pstmtu.setString(2, course);
                    pstmtu.setString(3, user);
                    pstmtu.setString(4, user);
                    pstmtu.setString(5, user);
                    pstmtu.setString(6, user);
                    pstmtu.setString(7, user);
                    rs = pstmtu.executeQuery();      // execute the prepared pstmt
                    
                    if (rs.next()) {
                    
                    out.println(SystemUtils.HeadTitle("Event Error"));
                    out.println("<BODY bgcolor=\"#CCCCAA\">");
                    out.println("<CENTER>");
                    out.println("<BR><BR><H3>Event Sign Up Error</H3>");
                    out.println("<BR><BR>Sorry, you are already registered for this event.");
                    out.println("<BR><BR>Please check with the Golf Shop if you have any questions.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                    out.println("</form></font>");
                    out.println("</center></body></html>");
                    out.close();
                    pstmtu.close();
                    return;
                    }
                    
                    pstmtu.close();
                     */

                    //
                    //  Insert a new entry to evntsup2b table and return the uid
                    //
                    id = Proshop_evntSignUp.getNewId(con, name, course, c_date, c_time);      // allocate a new entry

                    if (id == 0) {

                        if (new_skin) {

                            slotPageParms.page_start_button_go_back = true;
                            slotPageParms.page_start_title = "[options.notify.eventSignupErrorTitle]";
                            slotPageParms.page_start_notifications.add("[options.notify.eventSignupErrorNotice]");
                            break configure_slot;

                        } else {

                            out.println(SystemUtils.HeadTitle("Event Error"));
                            out.println("<BODY bgcolor=\"#CCCCAA\">");
                            out.println("<CENTER>");
                            out.println("<BR><BR><H3>Event Sign Up Error</H3>");
                            out.println("<BR><BR>Sorry, we were unable to allocate a new entry for this event.");
                            out.println("<BR><BR>Please try again later.");
                            out.println("<BR><BR>If problem persists, notify the Golf Shop.");
                            out.println("<BR><BR>");
                            out.println("<font size=\"2\">");
                            if (ext_login == true) {     // if came from external login (no frames)
                                out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
                                out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                            } else if (!index.equals("") && index.equals("0")) {
                                out.println("<form action=\"Member_events2\" method=\"post\">");
                            } else {
                                out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                            }
                            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                            out.println("</form></font>");
                            out.println("</center></body></html>");
                            out.close();
                            return;
                        }
                    }

                } else {   // not 'new' request (actually, not first time here for a new request)

                    if ((req.getParameter("letter") != null) || (req.getParameter("buddy") != null)
                            || (req.getParameter("return") != null) || (req.getParameter("memNotice") != null)) {   // if user clicked on a name letter, or its a return from verify

                        sid = req.getParameter("id");

                    } else {

                        //
                        //    The name of the submit button contains the 'id' of the entry in the event sign up table
                        //
                        Enumeration enum1 = req.getParameterNames();     // get the parm name passed

                        while (enum1.hasMoreElements()) {

                            pname = (String) enum1.nextElement();             // get parm name

                            if (pname.startsWith("id")) {

                                StringTokenizer tok = new StringTokenizer(pname, ":");     // separate name around the colon

                                sid = tok.nextToken();                        // skip past 'id: '
                                sid = tok.nextToken();                        // skip past 'id: '
                            }
                        }
                    }                             // end of IF letter or buddy

                    id = Integer.parseInt(sid);                   // convert id from string

                }   // end of IF 'new' request

            } catch (Exception e1) {

                if (new_skin) {

                    slotPageParms.page_start_button_go_back = true;
                    slotPageParms.page_start_title = "[options.notify.dbAccessErrorTitle]";
                    slotPageParms.page_start_notifications.add("[options.notify.dbAccessErrorNotice]");
                    break configure_slot;

                } else {

                    out.println(SystemUtils.HeadTitle("DB Error"));
                    out.println("<BODY bgcolor=\"#CCCCAA\">");
                    out.println("<CENTER>");
                    out.println("<BR><BR><H3>Database Access Error</H3>");
                    out.println("<BR><BR>Unable to access the Database.");
                    out.println("<BR>Please try again later.");
                    out.println("<BR><BR>If problem persists, contact your club manager.");
                    out.println("<BR><BR> id = " + id + "   sid = " + sid + "   pname = " + pname);
                    out.println("<BR><BR>Exception: " + e1.getMessage());
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    if (ext_login == true) {     // if came from external login (no frames)
                        out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                    } else if (!index.equals("") && index.equals("0")) {
                        out.println("<form action=\"Member_events2\" method=\"post\">");
                    } else {
                        out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                    }
                    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                    out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                    out.println("</form></font>");
                    out.println("</center></body></html>");
                    out.close();
                    return;
                }
            }

            //
            // Note:  At this point we have an id for either a new or an existing signup.
            //


            //
            //   Process both new and update requests (new entry has already been added)
            //
            //      id = id of entry to update
            //      name = name of event
            //      course = name of course
            //      user = username of this member
            //      userName = full name of this member
            //
            //   First, set this entry in_use if not already
            //
            if ((req.getParameter("letter") != null) || (req.getParameter("buddy") != null)
                    || (req.getParameter("return") != null) || (req.getParameter("memNotice") != null)) {   // if user clicked on a name letter, or return

                player1 = req.getParameter("player1");     // get the player info from the parms passed
                player2 = req.getParameter("player2");
                player3 = req.getParameter("player3");
                player4 = req.getParameter("player4");
                player5 = req.getParameter("player5");
                p1cw = req.getParameter("p1cw");
                p2cw = req.getParameter("p2cw");
                p3cw = req.getParameter("p3cw");
                p4cw = req.getParameter("p4cw");
                p5cw = req.getParameter("p5cw");
                ghin1 = req.getParameter("ghin1");
                ghin2 = req.getParameter("ghin2");
                ghin3 = req.getParameter("ghin3");
                ghin4 = req.getParameter("ghin4");
                ghin5 = req.getParameter("ghin5");
                gender1 = req.getParameter("gender1");
                gender2 = req.getParameter("gender2");
                gender3 = req.getParameter("gender3");
                gender4 = req.getParameter("gender4");
                gender5 = req.getParameter("gender5");
                notes = req.getParameter("notes");
                hides = req.getParameter("hide");
                homeclub1 = req.getParameter("homeclub1");
                homeclub2 = req.getParameter("homeclub2");
                homeclub3 = req.getParameter("homeclub3");
                homeclub4 = req.getParameter("homeclub4");
                homeclub5 = req.getParameter("homeclub5");
                phone1 = req.getParameter("phone1");
                phone2 = req.getParameter("phone2");
                phone3 = req.getParameter("phone3");
                phone4 = req.getParameter("phone4");
                phone5 = req.getParameter("phone5");
                address1 = req.getParameter("address1");
                address2 = req.getParameter("address2");
                address3 = req.getParameter("address3");
                address4 = req.getParameter("address4");
                address5 = req.getParameter("address5");
                email1 = req.getParameter("email1");
                email2 = req.getParameter("email2");
                email3 = req.getParameter("email3");
                email4 = req.getParameter("email4");
                email5 = req.getParameter("email5");
                shirt1 = req.getParameter("shirt1");
                shirt2 = req.getParameter("shirt2");
                shirt3 = req.getParameter("shirt3");
                shirt4 = req.getParameter("shirt4");
                shirt5 = req.getParameter("shirt5");
                shoe1 = req.getParameter("shoe1");
                shoe2 = req.getParameter("shoe2");
                shoe3 = req.getParameter("shoe3");
                shoe4 = req.getParameter("shoe4");
                shoe5 = req.getParameter("shoe5");
                other1A1 = req.getParameter("other1A1");
                other1A2 = req.getParameter("other1A2");
                other1A3 = req.getParameter("other1A3");
                other2A1 = req.getParameter("other2A1");
                other2A2 = req.getParameter("other2A2");
                other2A3 = req.getParameter("other2A3");
                other3A1 = req.getParameter("other3A1");
                other3A2 = req.getParameter("other3A2");
                other3A3 = req.getParameter("other3A3");
                other4A1 = req.getParameter("other4A1");
                other4A2 = req.getParameter("other4A2");
                other4A3 = req.getParameter("other4A3");
                other5A1 = req.getParameter("other5A1");
                other5A2 = req.getParameter("other5A2");
                other5A3 = req.getParameter("other5A3");
                guest_id1 = Integer.parseInt(req.getParameter("guest_id1"));
                guest_id2 = Integer.parseInt(req.getParameter("guest_id2"));
                guest_id3 = Integer.parseInt(req.getParameter("guest_id3"));
                guest_id4 = Integer.parseInt(req.getParameter("guest_id4"));
                guest_id5 = Integer.parseInt(req.getParameter("guest_id5"));

                //
                //  Convert hide from string to int
                //
                hide = 0;                       // init to No
                if (!hides.equals("0")) {     // if not zero
                    hide = 1;
                }

            } else {

                //
                //  Set the entry in use to see if it is already in use
                //
                try {

                    PreparedStatement pstmt1 = con.prepareStatement(
                            "UPDATE evntsup2b "
                            + "SET in_use = 1, in_use_by = ? "
                            + "WHERE id = ? AND (in_use = 0 OR in_use_by = ?)");

                    pstmt1.clearParameters();
                    pstmt1.setString(1, user);
                    pstmt1.setInt(2, id);
                    pstmt1.setString(3, user);

                    count2 = pstmt1.executeUpdate();

                    pstmt1.close();

                } catch (Exception e2) {

                    dbError(out, e2);
                    return;
                }


                if (count2 == 0) {                    // if event slot already in use

                    if (new_skin) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_title = "[options.notify.eventBusyTitle]";
                        slotPageParms.page_start_notifications.add("[options.notify.eventBusyNotice]");
                        break configure_slot;

                    } else {

                        out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                        out.println("<BODY bgcolor=\"#CCCCAA\">");
                        out.println("<CENTER><BR><BR><H3>Event Entry Busy</H3>");
                        out.println("<BR><BR>Sorry, but this entry is currently busy.<BR>");
                        out.println("<BR>Please select another entry or try again later.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");
                        if (ext_login == true) {     // if came from external login (no frames)
                            out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
                            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                        } else if (!index.equals("") && index.equals("0")) {
                            out.println("<form action=\"Member_events2\" method=\"post\">");
                        } else {
                            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                        }
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                        out.println("</center></body></html>");
                        out.close();
                        return;

                    }
                } else {
                    // We now are holding this slot in use
                    slotPageParms.time_remaining = verifySlot.getSlotHoldTime(session);
                }


                //
                //  get the existing entry and its contents
                //
                try {

                    PreparedStatement pstmt3 = con.prepareStatement(
                            "SELECT * FROM evntsup2b "
                            + "WHERE id = ?");

                    pstmt3.clearParameters();
                    pstmt3.setInt(1, id);
                    rs = pstmt3.executeQuery();

                    while (rs.next()) {

                        player1 = rs.getString("player1");
                        player2 = rs.getString("player2");
                        player3 = rs.getString("player3");
                        player4 = rs.getString("player4");
                        player5 = rs.getString("player5");
                        p1cw = rs.getString("p1cw");
                        p2cw = rs.getString("p2cw");
                        p3cw = rs.getString("p3cw");
                        p4cw = rs.getString("p4cw");
                        p5cw = rs.getString("p5cw");
                        ghin1 = rs.getString("ghin1");
                        ghin2 = rs.getString("ghin2");
                        ghin3 = rs.getString("ghin3");
                        ghin4 = rs.getString("ghin4");
                        ghin5 = rs.getString("ghin5");
                        gender1 = rs.getString("gender1");
                        gender2 = rs.getString("gender2");
                        gender3 = rs.getString("gender3");
                        gender4 = rs.getString("gender4");
                        gender5 = rs.getString("gender5");
                        notes = rs.getString("notes");
                        hide = rs.getInt("hideNotes");
                        homeclub1 = rs.getString("homeclub1");
                        homeclub2 = rs.getString("homeclub2");
                        homeclub3 = rs.getString("homeclub3");
                        homeclub4 = rs.getString("homeclub4");
                        homeclub5 = rs.getString("homeclub5");
                        phone1 = rs.getString("phone1");
                        phone2 = rs.getString("phone2");
                        phone3 = rs.getString("phone3");
                        phone4 = rs.getString("phone4");
                        phone5 = rs.getString("phone5");
                        address1 = rs.getString("address1");
                        address2 = rs.getString("address2");
                        address3 = rs.getString("address3");
                        address4 = rs.getString("address4");
                        address5 = rs.getString("address5");
                        email1 = rs.getString("email1");
                        email2 = rs.getString("email2");
                        email3 = rs.getString("email3");
                        email4 = rs.getString("email4");
                        email5 = rs.getString("email5");
                        shirt1 = rs.getString("shirtsize1");
                        shirt2 = rs.getString("shirtsize2");
                        shirt3 = rs.getString("shirtsize3");
                        shirt4 = rs.getString("shirtsize4");
                        shirt5 = rs.getString("shirtsize5");
                        shoe1 = rs.getString("shoesize1");
                        shoe2 = rs.getString("shoesize2");
                        shoe3 = rs.getString("shoesize3");
                        shoe4 = rs.getString("shoesize4");
                        shoe5 = rs.getString("shoesize5");
                        other1A1 = rs.getString("other1A1");
                        other1A2 = rs.getString("other1A2");
                        other1A3 = rs.getString("other1A3");
                        other2A1 = rs.getString("other2A1");
                        other2A2 = rs.getString("other2A2");
                        other2A3 = rs.getString("other2A3");
                        other3A1 = rs.getString("other3A1");
                        other3A2 = rs.getString("other3A2");
                        other3A3 = rs.getString("other3A3");
                        other4A1 = rs.getString("other4A1");
                        other4A2 = rs.getString("other4A2");
                        other4A3 = rs.getString("other4A3");
                        other5A1 = rs.getString("other5A1");
                        other5A2 = rs.getString("other5A2");
                        other5A3 = rs.getString("other5A3");
                        guest_id1 = rs.getInt("guest_id1");
                        guest_id2 = rs.getInt("guest_id2");
                        guest_id3 = rs.getInt("guest_id3");
                        guest_id4 = rs.getInt("guest_id4");
                        guest_id5 = rs.getInt("guest_id5");

                    }

                    pstmt3.close();

                } catch (Exception e2) {

                    dbError(out, e2);
                    return;
                }


                //
                //**********************************************
                //   Check for Member Notice from Pro
                //**********************************************
                //
                if (sfb.equals("Back")) {      // if Event only on Back Tees

                    fb = 1;
                }

                //
                //  Determine day of week from the event date
                //
                Calendar cal = new GregorianCalendar();       // get todays date

                cal.set(Calendar.YEAR, year);                 // change to requested date
                cal.set(Calendar.MONTH, month - 1);
                cal.set(Calendar.DAY_OF_MONTH, day);

                int day_num = cal.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)

                String day_name = day_table[day_num];            // get name for day

                //        
                //  Check for a Member Notice message from Pro
                //
                String memNotice = verifySlot.checkMemNotice(date, time, time, fb, course, day_name, "event", false, sess_activity_id, con);  // use date of event and actual start time

                if (!memNotice.equals("") && req.getParameter("skip_member_notice") == null) {      // if message to display

                    //
                    //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
                    //
                    if (new_skin) {

                        slotPageParms.page_start_button_go_back = true;
                        slotPageParms.page_start_button_accept = true;
                        slotPageParms.page_start_title = "[options.notify.noticeFromGolfShopTitle]";
                        slotPageParms.page_start_notifications.add(memNotice);
                        slotPageParms.page_start_notifications.add("[options.notify.continueWithRequestPrompt]");
                        slotPageParms.callback_map.put("skip_member_notice", "yes");
                        break configure_slot;

                    } else {
                        out.println("<HTML><HEAD>");
                        out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
                        out.println("<Title>Member Notice For Event Signup Request</Title>");
                        out.println("</HEAD>");

                        out.println("<BODY bgcolor=\"#CCCCAA\"><CENTER>");
                        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

                        out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
                        out.println("<tr><td valign=\"top\" align=\"center\">");
                        out.println("<p>&nbsp;&nbsp;</p>");
                        out.println("<p>&nbsp;&nbsp;</p>");
                        out.println("<font size=\"3\">");
                        out.println("<b>NOTICE FROM YOUR GOLF SHOP</b><br><br><br></font>");

                        out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
                        out.println("<tr>");
                        out.println("<td width=\"580\" align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<br>" + memNotice);
                        out.println("</font></td></tr>");
                        out.println("</table><br>");

                        out.println("</font><font size=\"2\">");
                        out.println("<br>Would you like to continue with this request?<br>");
                        out.println("<br><b>Please select from the following. DO NOT use you browser's BACK button!</b><br><br>");

                        out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
                        out.println("<tr><td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_evntSignUp\" method=\"post\" name=\"can\">");
                        out.println("<input type=\"hidden\" name=\"id\" value=" + id + ">");
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        if (ext_login == true) {     // if came from external login (no frames)
                            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                        }
                        out.println("<input type=\"submit\" value=\"No - Return\" name=\"cancel\"></form>");

                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"Member_evntSignUp\" method=\"post\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                        out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
                        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                        out.println("<input type=\"hidden\" name=\"ghin1\" value=\"" + ghin1 + "\">");
                        out.println("<input type=\"hidden\" name=\"ghin2\" value=\"" + ghin2 + "\">");
                        out.println("<input type=\"hidden\" name=\"ghin3\" value=\"" + ghin3 + "\">");
                        out.println("<input type=\"hidden\" name=\"ghin4\" value=\"" + ghin4 + "\">");
                        out.println("<input type=\"hidden\" name=\"ghin5\" value=\"" + ghin5 + "\">");
                        out.println("<input type=\"hidden\" name=\"gender1\" value=\"" + gender1 + "\">");
                        out.println("<input type=\"hidden\" name=\"gender2\" value=\"" + gender2 + "\">");
                        out.println("<input type=\"hidden\" name=\"gender3\" value=\"" + gender3 + "\">");
                        out.println("<input type=\"hidden\" name=\"gender4\" value=\"" + gender4 + "\">");
                        out.println("<input type=\"hidden\" name=\"gender5\" value=\"" + gender5 + "\">");
                        out.println("<input type=\"hidden\" name=\"homeclub1\" value=\"" + homeclub1 + "\">");
                        out.println("<input type=\"hidden\" name=\"homeclub2\" value=\"" + homeclub2 + "\">");
                        out.println("<input type=\"hidden\" name=\"homeclub3\" value=\"" + homeclub3 + "\">");
                        out.println("<input type=\"hidden\" name=\"homeclub4\" value=\"" + homeclub4 + "\">");
                        out.println("<input type=\"hidden\" name=\"homeclub5\" value=\"" + homeclub5 + "\">");
                        out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
                        out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
                        out.println("<input type=\"hidden\" name=\"phone3\" value=\"" + phone3 + "\">");
                        out.println("<input type=\"hidden\" name=\"phone4\" value=\"" + phone4 + "\">");
                        out.println("<input type=\"hidden\" name=\"phone5\" value=\"" + phone5 + "\">");
                        out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
                        out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
                        out.println("<input type=\"hidden\" name=\"address3\" value=\"" + address3 + "\">");
                        out.println("<input type=\"hidden\" name=\"address4\" value=\"" + address4 + "\">");
                        out.println("<input type=\"hidden\" name=\"address5\" value=\"" + address5 + "\">");
                        out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
                        out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
                        out.println("<input type=\"hidden\" name=\"email3\" value=\"" + email3 + "\">");
                        out.println("<input type=\"hidden\" name=\"email4\" value=\"" + email4 + "\">");
                        out.println("<input type=\"hidden\" name=\"email5\" value=\"" + email5 + "\">");
                        out.println("<input type=\"hidden\" name=\"shirt1\" value=\"" + shirt1 + "\">");
                        out.println("<input type=\"hidden\" name=\"shirt2\" value=\"" + shirt2 + "\">");
                        out.println("<input type=\"hidden\" name=\"shirt3\" value=\"" + shirt3 + "\">");
                        out.println("<input type=\"hidden\" name=\"shirt4\" value=\"" + shirt4 + "\">");
                        out.println("<input type=\"hidden\" name=\"shirt5\" value=\"" + shirt5 + "\">");
                        out.println("<input type=\"hidden\" name=\"shoe1\" value=\"" + shoe1 + "\">");
                        out.println("<input type=\"hidden\" name=\"shoe2\" value=\"" + shoe2 + "\">");
                        out.println("<input type=\"hidden\" name=\"shoe3\" value=\"" + shoe3 + "\">");
                        out.println("<input type=\"hidden\" name=\"shoe4\" value=\"" + shoe4 + "\">");
                        out.println("<input type=\"hidden\" name=\"shoe5\" value=\"" + shoe5 + "\">");
                        out.println("<input type=\"hidden\" name=\"other1A1\" value=\"" + other1A1 + "\">");
                        out.println("<input type=\"hidden\" name=\"other1A2\" value=\"" + other1A2 + "\">");
                        out.println("<input type=\"hidden\" name=\"other1A3\" value=\"" + other1A3 + "\">");
                        out.println("<input type=\"hidden\" name=\"other2A1\" value=\"" + other2A1 + "\">");
                        out.println("<input type=\"hidden\" name=\"other2A2\" value=\"" + other2A2 + "\">");
                        out.println("<input type=\"hidden\" name=\"other2A3\" value=\"" + other2A3 + "\">");
                        out.println("<input type=\"hidden\" name=\"other3A1\" value=\"" + other3A1 + "\">");
                        out.println("<input type=\"hidden\" name=\"other3A2\" value=\"" + other3A2 + "\">");
                        out.println("<input type=\"hidden\" name=\"other3A3\" value=\"" + other3A3 + "\">");
                        out.println("<input type=\"hidden\" name=\"other4A1\" value=\"" + other4A1 + "\">");
                        out.println("<input type=\"hidden\" name=\"other4A2\" value=\"" + other4A2 + "\">");
                        out.println("<input type=\"hidden\" name=\"other4A3\" value=\"" + other4A3 + "\">");
                        out.println("<input type=\"hidden\" name=\"other5A1\" value=\"" + other5A1 + "\">");
                        out.println("<input type=\"hidden\" name=\"other5A2\" value=\"" + other5A2 + "\">");
                        out.println("<input type=\"hidden\" name=\"other5A3\" value=\"" + other5A3 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
                        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
                        out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
                        if (ext_login == true) {     // if came from external login (no frames)
                            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                        }
                        out.println("<input type=\"submit\" value=\"YES - Continue\"></form>");
                        out.println("</font></td></tr>");
                        out.println("</table>");

                        out.println("</td>");
                        out.println("</tr>");
                        out.println("</table>");
                        out.println("</font></center></body></html>");
                        out.close();
                        return;
                    }

                } // end if memberNotice to display

            } // end of IF buddy or letter request


            //
            //  Ensure that there are no null player fields
            //
            if (player1 == null) {
                player1 = "";
            }
            if (player2 == null) {
                player2 = "";
            }
            if (player3 == null) {
                player3 = "";
            }
            if (player4 == null) {
                player4 = "";
            }
            if (player5 == null) {
                player5 = "";
            }
            if (p1cw == null) {
                p1cw = "";
            }
            if (p2cw == null) {
                p2cw = "";
            }
            if (p3cw == null) {
                p3cw = "";
            }
            if (p4cw == null) {
                p4cw = "";
            }
            if (p5cw == null) {
                p5cw = "";
            }

            //
            //   Get the 'X' parms for this event
            //
            try {

                PreparedStatement pstmtev = con.prepareStatement(
                        "SELECT x, (ask_homeclub + ask_phone + ask_address + ask_email + ask_shirtsize + ask_shoesize) AS q "
                        + "FROM events2b "
                        + "WHERE name = ? AND courseName = ? AND activity_id = ?");

                pstmtev.clearParameters();
                pstmtev.setString(1, name);
                pstmtev.setString(2, course);
                pstmtev.setInt(3, sess_activity_id);
                rs = pstmtev.executeQuery();

                if (rs.next()) {

                    x = rs.getInt(1);
                    ask_more = (rs.getInt(2) == 0) ? 0 : 1;
                }
                pstmtev.close();

            } catch (Exception ignore) {
            }


            //
            // If this event is asking for either ghin or gender then lets look them up
            //
            if (ask_hdcp + ask_gender > 0 || export_type != 0) {

                try {

                    PreparedStatement pstmt = con.prepareStatement("SELECT ghin, gender FROM member2b WHERE username = ?");
                    pstmt.clearParameters();
                    pstmt.setString(1, user);
                    rs = pstmt.executeQuery();
                    if (rs.next()) {

                        hdcp_num = rs.getString(1);
                        gender = rs.getString(2);
                    }

                    pstmt.close();

                    if (hdcp_num.length() == 5) {
                        hdcp_num = "00" + hdcp_num;
                    } else if (hdcp_num.length() == 6) {
                        hdcp_num = "0" + hdcp_num;
                    }

                } catch (Exception ignore) {
                }

            }


            //
            //  Denver CC - if Juniors or Fitness activities, determine who owns this team (player1 or parent) - if not a new team
            //
            if (club.equals("denvercc") && !player1.equals("") && (sess_activity_id == 2 || sess_activity_id == 3)) {
                
                String username1 = "";      
                String mNumD = "";

                try {

                    PreparedStatement pstmt3 = con.prepareStatement(
                            "SELECT username1 FROM evntsup2b "
                            + "WHERE id = ?");

                    pstmt3.clearParameters();
                    pstmt3.setInt(1, id);
                    rs = pstmt3.executeQuery();

                    if (rs.next()) {

                        username1 = rs.getString("username1");
                    }
                    
                    if (user.equals(username1)) {       // if this user owns the team
                        
                        user_is_owner = true;
                        
                    } else {
                        
                        //  check if user is a family member
                        
                        pstmt3 = con.prepareStatement(
                                "SELECT memNum "
                                + "FROM member2b WHERE username = ?");      // get this member's mNum

                        pstmt3.clearParameters();       
                        pstmt3.setString(1, user);
                        rs = pstmt3.executeQuery();    

                        if (rs.next()) {

                            mNumD = rs.getString("memNum");                                            
                        }

                        if (!mNumD.equals("")) {        // if member number found for this member
                            
                            pstmt3 = con.prepareStatement(
                                    "SELECT memNum "
                                    + "FROM member2b WHERE username = ?");      // get player1's mNum

                            pstmt3.clearParameters();       
                            pstmt3.setString(1, username1);
                            rs = pstmt3.executeQuery();    

                            if (rs.next()) {

                                if (mNumD.equals(rs.getString("memNum"))) {       // if this user is a family member of the owner

                                    user_is_owner = true;    
                                }
                            }
                        }
                    }
                    pstmt3.close();

                } catch (Exception ignore) {
                }
                
                //   If user is not the owner, then do not allow him/her to remove any existing players
                
                if (user_is_owner == false) {
                    
                    if (!player1.equals("")) blockP1 = true;
                    if (!player2.equals("")) blockP2 = true;
                    if (!player3.equals("")) blockP3 = true;
                    if (!player4.equals("")) blockP4 = true;
                    if (!player5.equals("")) blockP5 = true;       
                }
            }            // end of Denver CC 
                        


            //
            //  Set user's Name as first open player to be placed in name slot for them
            //
            if (!player1.equals(userName) && !player2.equals(userName) && !player3.equals(userName) && !player4.equals(userName) && !player5.equals(userName)) {

                if (player1.equals("")) {

                    player1 = userName;
                    p1cw = pcw;
                    gender1 = gender;
                    ghin1 = hdcp_num;
                    guest_id1 = 0;

                } else if (player2.equals("") && size > 1) {

                    player2 = userName;
                    p2cw = pcw;
                    gender2 = gender;
                    ghin2 = hdcp_num;
                    guest_id2 = 0;

                } else if (player3.equals("") && size > 2) {

                    player3 = userName;
                    p3cw = pcw;
                    gender3 = gender;
                    ghin3 = hdcp_num;
                    guest_id3 = 0;

                } else if (player4.equals("") && size > 3) {

                    player4 = userName;
                    p4cw = pcw;
                    gender4 = gender;
                    ghin4 = hdcp_num;
                    guest_id4 = 0;

                } else if (player5.equals("") && size > 4) {

                    player5 = userName;
                    p5cw = pcw;
                    gender5 = gender;
                    ghin5 = hdcp_num;
                    guest_id5 = 0;

                }
            }
            
        } // end configure block
        
        
        //
        //  Custom - do not allow members to remove themselves or others from Season Long Events
        //
        if (club.equals("snoqualmieridge") && season == 1) {

            blockP1 = true;     // do not allow members to remove players        
            
            if (!player2.equals("")) {
                blockP2 = true;
            }
        }


        // Set players per group
        //players_per_group = (p5.equals("Yes") ? 5 : 4);

        // Set default course name
        if (club.equals("congressional")) {
            //course_disp = congressionalCustom.getFullCourseName(date, (int) dd, course);
            course_disp = course;
        } else {
            course_disp = course;
        }

        // Complete filling parameters for slot page

        //slotPageParms.time_remaining = verifySlot.getInUseTimeRemaining(date, time, fb, course, session);

        slotPageParms.slot_type = "Event";
        slotPageParms.signup_type = "Sign Up";
        slotPageParms.bread_crumb = "Event Sign Up";
        slotPageParms.page_title = "Member Event Sign Up Page";

        slotPageParms.hide_notes = hide;
        slotPageParms.show_member_tbd = (x != 0);
        slotPageParms.show_fb = false;
        slotPageParms.show_gender = (export_type != 0 || ask_gender == 1);
        slotPageParms.show_ghin = (export_type != 0 || ask_hdcp == 1);
        slotPageParms.lock_ghin = club.equals("rollinghillsgc");
        slotPageParms.show_transport = (sess_activity_id == 0 && season == 0);
        slotPageParms.ghin_text = ((sess_activity_id == 0) ? (club.equals("mpccpb") || club.equals("olyclub") || club.equals("lomassantafecc") ? "GHIN #" : "Hdcp #") : "USTA #");
        
        slotPageParms.show_member_select = true;
        slotPageParms.show_guest_types = true;
        slotPageParms.show_ghin_in_list = club.equals("cwcpga");
        slotPageParms.use_default_member_tmode = true;
        slotPageParms.default_member_wc = season_tmode;
        if (!(sess_activity_id == 0 && season == 0)) {
            slotPageParms.default_member_wc = season_tmode;
            slotPageParms.use_default_member_tmode = true;
            slotPageParms.default_member_wc_override = season_tmode;
        }
        slotPageParms.allowed_tmodes_list = Arrays.asList(parmc.tmodea);  // Tmode that will be allowed when set as defaults
        slotPageParms.verify_member_tmode = true; 

        slotPageParms.player_count = 5;
        slotPageParms.players_per_group = 5;
        slotPageParms.visible_players_per_group = size;
        slotPageParms.index = index;
        slotPageParms.id = id;
        slotPageParms.season_long = (season != 0);
        slotPageParms.event_id = event_id;

        slotPageParms.fb = fb;
        slotPageParms.slots = 1;

        slotPageParms.yy = (int) year;
        slotPageParms.mm = (int) month;
        slotPageParms.dd = (int) day;

        slotPageParms.course = course;
        slotPageParms.stime = act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm;
        slotPageParms.course_disp = course_disp;
        slotPageParms.time = time;
        slotPageParms.notes = notes;
        slotPageParms.name = name;
        slotPageParms.show_name = true;
        
        //slotPageParms.allow_cancel = (lottid > 0 && user.equalsIgnoreCase(orig_by));
        slotPageParms.ask_more = (ask_more > 0);
        slotPageParms.edit_mode = (!newRequest);
        slotPageParms.allow_cancel = (!newRequest);
        if(slotPageParms.allow_cancel && (sess_activity_id == 0 && club.equals("snoqualmieridge") && season == 1)){
            slotPageParms.allow_cancel = false;
            slotPageParms.show_contact_to_cancel = true;
        }
        
        //
        //  Denver CC - if Juniors or Fitness activities, do not allow others to cancel the team or remove players
        //
        if (club.equals("denvercc") && user_is_owner == false && (sess_activity_id == 2 || sess_activity_id == 3)) {

            slotPageParms.allow_cancel = false;
        }
                        
        
        //
        //  Custom to only allow the tee time originator to add/change/remove any existing notes (case 2293).
        //
        if (club.equals("baldpeak")) {

           if (!notes.equals("") && newRequest == false && !userName.equalsIgnoreCase(player1)) {    // if existing notes, and existing tee time that this user did not originate

              slotPageParms.protect_notes = true;      // protect the notes - do not allow user to add/change/remove the notes
           }
        }

        
        slotPageParms.course_parms = parmc;

        slotPageParms.pcw = pcw; // User's default PCW

        slotPageParms.guest_id_a = new int[]{guest_id1, guest_id2, guest_id3, guest_id4, guest_id5};
        slotPageParms.player_a = new String[]{player1, player2, player3, player4, player5};
        slotPageParms.pcw_a = new String[]{p1cw, p2cw, p3cw, p4cw, p5cw};
        slotPageParms.gender_a = new String[]{gender1, gender2, gender3, gender4, gender5};
        slotPageParms.ghin_a = new String[]{ghin1, ghin2, ghin3, ghin4, ghin5};
        slotPageParms.homeclub_a = new String[]{homeclub1, homeclub2, homeclub3, homeclub4, homeclub5};
        slotPageParms.phone_a = new String[]{phone1, phone2, phone3, phone4, phone5};
        slotPageParms.address_a = new String[]{address1, address2, address3, address4, address5};
        slotPageParms.email_a = new String[]{email1, email2, email3, email4, email5};
        slotPageParms.shirt_a = new String[]{shirt1, shirt2, shirt3, shirt4, shirt5};
        slotPageParms.shoe_a = new String[]{shoe1, shoe2, shoe3, shoe4, shoe5};
        slotPageParms.otherA1_a = new String[]{other1A1, other2A1, other3A1, other4A1, other5A1};
        slotPageParms.otherA2_a = new String[]{other1A2, other2A2, other3A2, other4A2, other5A2};
        slotPageParms.otherA3_a = new String[]{other1A3, other2A3, other3A3, other4A3, other5A3};

        // Set players that cannot be editied on form
        slotPageParms.lock_player_a = new boolean[]{blockP1, blockP2, blockP3, blockP4, blockP5};

        // Set tranport types
        Common_slot.setDefaultTransportTypes(slotPageParms);
        // Set transport legend
        Common_slot.setTransportLegend(slotPageParms, parmc, new_skin);
        // Set transport modes
        Common_slot.setTransportModes(slotPageParms, parmc);
        // Set guest types
        Common_slot.setGuestTypes(con, slotPageParms, parm);

        // Define the fields we will include when submitting the form
        slotPageParms.slot_submit_map.put("time", "time");
        slotPageParms.slot_submit_map.put("index", "index");
        slotPageParms.slot_submit_map.put("course", "course");
        slotPageParms.slot_submit_map.put("id", "id");
        slotPageParms.slot_submit_map.put("event_id", "event_id");
        slotPageParms.slot_submit_map.put("name", "name");
        slotPageParms.slot_submit_map.put("slots", "slots");
        slotPageParms.slot_submit_map.put("hide", "hide_notes");
        slotPageParms.slot_submit_map.put("notes", "notes");
        slotPageParms.slot_submit_map.put("player%", "player_a");
        slotPageParms.slot_submit_map.put("p9%", "p9_a");
        slotPageParms.slot_submit_map.put("p%cw", "pcw_a");
        slotPageParms.slot_submit_map.put("guest_id%", "guest_id_a");
        slotPageParms.slot_submit_map.put("gender%", "gender_a");
        slotPageParms.slot_submit_map.put("ghin%", "ghin_a");
        slotPageParms.slot_submit_map.put("homeclub%", "homeclub_a");
        slotPageParms.slot_submit_map.put("phone%", "phone_a");
        slotPageParms.slot_submit_map.put("address%", "address_a");
        slotPageParms.slot_submit_map.put("email%", "email_a");
        slotPageParms.slot_submit_map.put("shirt%", "shirt_a");
        slotPageParms.slot_submit_map.put("shoe%", "shoe_a");
        slotPageParms.slot_submit_map.put("other%A1", "otherA1_a");
        slotPageParms.slot_submit_map.put("other%A2", "otherA2_a");
        slotPageParms.slot_submit_map.put("other%A3", "otherA3_a");
     
        if (ext_login == true) {     // if came from external login (no frames)
            slotPageParms.slot_submit_map.put("ext-login", "time");    // use the 'time' value as this process REQUIRES an existing label be specified in the 2nd location !!!!!
        }
       

        if (new_skin) {

            /**************************************
             * New Skin Output
             **************************************/
            if (json_mode) {
                out.print(Common_slot.slotJson(slotPageParms));
            } else {
                Common_slot.displaySlotPage(out, slotPageParms, req, con);
            }

        } else { // Old Skin
            //
            //  Build the HTML page to prompt user for names
            //
            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
            out.println("<title>Member Event Sign Up Page</title>");

            //  Add script code to allow modal windows to be used
            out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->"
                    + "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>"
                    + "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>"
                    + "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

            out.println("<script type=\"text/javascript\">");
            out.println("<!--");
            out.println("function resizeIFrame(divHeight, iframeName) {");
            out.println("document.getElementById(iframeName).height = divHeight;");
            out.println("}");
            out.println("// -->");
            out.println("</script>");

            //
            //*******************************************************************
            //  User clicked on a letter - submit the form for the letter
            //*******************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Submit the form when clicking on a letter
            out.println("<!--");
            out.println("function subletter(x) {");

//      out.println("alert(x);");
            out.println("document.forms['playerform'].letter.value = x;");       // put the letter in the parm
            out.println("document.forms['playerform'].submit();");               // submit the form
            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");          // End of script

            //
            //*********************************************************************************
            //  Erase player name (erase button selected next to player's name)
            //
            //    Remove the player's name and shift any other names up starting at player1
            //*********************************************************************************
            //
            out.println("<script type=\"text/javascript\">");            // Erase name script
            out.println("<!--");

            // Global vars used for guest tracking
            out.println("var guestid_slot;");
            out.println("var player_slot;");

            out.println("function erasename(pos) {");
            out.println(" var f = document.forms['playerform'];");
            out.println(" eval('f.player'+pos+'.value = \"\"');");
            if (export_type != 0 || ask_hdcp == 1) {
                out.println(" eval('f.ghin'+pos+'.value = \"\"');");
            }
            if (export_type != 0 || ask_gender == 1) {
                out.println(" eval('f.gender'+pos+'.selectedIndex = -1');");
            }

            out.println("eval('f.guest_id'+pos+'.value = \"0\"');");

            out.println("}");

            out.println("function erasetext(pos1) {");
            out.println(" document.playerform[pos1].value = '';");
            out.println("}");

            out.println("function movename(namewc) {");
            out.println("del = ':';");                               // deliminator is a colon
            out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
            out.println("var name = array[0];");
            out.println("var wc = array[1];");
            out.println("var gender = array[2];");
            out.println("var ghin = array[3];");
            out.println("var skip = 0;");
            out.println("var f = document.forms['playerform'];");

            out.println("var player1 = f.player1.value;");

            if (size > 1) {
                out.println("var player2 = f.player2.value;");
            }
            if (size > 2) {
                out.println("var player3 = f.player3.value;");
            }
            if (size > 3) {
                out.println("var player4 = f.player4.value;");
            }
            if (size > 4) {
                out.println("var player5 = f.player5.value;");
            }

            out.println("if (( name != 'x') && ( name != 'X')) {");

            if (size == 5) {
                out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {");
            }
            if (size == 4) {
                out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4)) {");
            }
            if (size == 3) {
                out.println("if (( name == player1) || ( name == player2) || ( name == player3)) {");
            }
            if (size == 2) {
                out.println("if (( name == player1) || ( name == player2)) {");
            }
            if (size == 1) {
                out.println("if (( name == player1)) {");
            }
            out.println("skip = 1;");
            out.println("}");
            out.println("}");

            out.println("if (skip == 0) {");

            out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("f.player1.value = name;");
            out.println("f.guest_id1.value = '0';");
            //out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("f.p1cw.value = wc;");
            if (export_type != 0 || ask_gender == 1) {
                out.println("f.gender1.value = gender;");
            }
            if (export_type != 0 || ask_hdcp == 1) {
                out.println("f.ghin1.value = ghin;");
            }
            //out.println("f.gender1.value = gender;");
            //out.println("f.ghin1.value = ghin;");
            //out.println("}");
            if (size > 1) {
                out.println("} else {");

                out.println("if (player2 == '') {");                    // if player2 is empty
                out.println("f.player2.value = name;");
                out.println("f.guest_id2.value = '0';");
                //out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
                out.println("f.p2cw.value = wc;");
                if (export_type != 0 || ask_gender == 1) {
                    out.println("f.gender2.value = gender;");
                }
                if (export_type != 0 || ask_hdcp == 1) {
                    out.println("f.ghin2.value = ghin;");
                }
                //out.println("f.gender2.value = gender;");
                //out.println("f.ghin2.value = ghin;");
                //out.println("}");

                if (size > 2) {
                    out.println("} else {");
                    out.println("if (player3 == '') {");                    // if player3 is empty
                    out.println("f.player3.value = name;");
                    out.println("f.guest_id3.value = '0';");
                    //out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
                    out.println("f.p3cw.value = wc;");
                    if (export_type != 0 || ask_gender == 1) {
                        out.println("f.gender3.value = gender;");
                    }
                    if (export_type != 0 || ask_hdcp == 1) {
                        out.println("f.ghin3.value = ghin;");
                    }
                    //out.println("f.gender3.value = gender;");
                    //out.println("f.ghin3.value = ghin;");
                    //out.println("}");

                    if (size > 3) {
                        out.println("} else {");
                        out.println("if (player4 == '') {");                    // if player4 is empty
                        out.println("f.player4.value = name;");
                        out.println("f.guest_id4.value = '0';");
                        //out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
                        out.println("f.p4cw.value = wc;");
                        if (export_type != 0 || ask_gender == 1) {
                            out.println("f.gender4.value = gender;");
                        }
                        if (export_type != 0 || ask_hdcp == 1) {
                            out.println("f.ghin4.value = ghin;");
                        }
                        //out.println("f.gender4.value = gender;");
                        //out.println("f.ghin4.value = ghin;");
                        //out.println("}");

                        if (size > 4) {
                            out.println("} else {");
                            out.println("if (player5 == '') {");                    // if player5 is empty
                            out.println("f.player5.value = name;");
                            out.println("f.guest_id5.value = '0';");
                            //out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
                            out.println("f.p5cw.value = wc;");
                            if (export_type != 0 || ask_gender == 1) {
                                out.println("f.gender5.value = gender;");
                            }
                            if (export_type != 0 || ask_hdcp == 1) {
                                out.println("f.ghin5.value = ghin;");
                            }
                            //out.println("f.gender5.value = gender;");
                            //out.println("f.ghin5.value = ghin;");
                            //out.println("}");
                            out.println("}");
                        }
                        out.println("}");
                    }
                    out.println("}");
                }
                out.println("}");
            }
            out.println("}");

            out.println("}");                  // end of dup name chack
            out.println("}");

            out.println("function moveguest(namewc) {");
            //out.println("var name = namewc;");
            out.println("var f = document.forms['playerform'];");

            out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
            out.println("var name = array[0];");
            out.println("var use_guestdb = array[1]");

            out.println("var player1 = f.player1.value;");
            if (size > 1) {
                out.println("var player2 = f.player2.value;");
            }
            if (size > 2) {
                out.println("var player3 = f.player3.value;");
            }
            if (size > 3) {
                out.println("var player4 = f.player4.value;");
            }
            if (size > 4) {
                out.println("var player5 = f.player5.value;");
            }

            // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
            out.println("if (use_guestdb == 1 && (player1 == ''"
                    + (size > 1 ? " || player2 == ''" : "")
                    + (size > 2 ? " || player3 == ''" : "")
                    + (size > 3 ? " || player4 == ''" : "")
                    + (size > 4 ? " || player5 == ''" : "") + ")) {");
            out.println("  loadmodal(0);");
            out.println("}");


            //  set spc to ' ' if name to move isn't an 'X'
            out.println("var spc = '';");
            out.println("if (name != 'X' && name != 'x') {");
            out.println("   spc = ' ';");
            out.println("}");

            out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player1;");
            out.println("guestid_slot = f.guest_id1;");
            out.println("f.player1.value = name + spc;");
            out.println("} else {");
            out.println("f.player1.focus();");                   // here for IE compat
            out.println("f.player1.value = name + spc;");
            out.println("f.player1.focus();");
            out.println("}");

            if (size > 1) {
                out.println("} else {");
                out.println("if (player2 == '') {");                    // if player2 is empty
                out.println("if (use_guestdb == 1) {");
                out.println("player_slot = f.player2;");
                out.println("guestid_slot = f.guest_id2;");
                out.println("f.player2.value = name + spc;");
                out.println("} else {");
                out.println("f.player2.focus();");
                out.println("f.player2.value = name + spc;");
                out.println("f.player2.focus();");
                out.println("}");

                if (size > 2) {
                    out.println("} else {");
                    out.println("if (player3 == '') {");                    // if player3 is empty
                    out.println("if (use_guestdb == 1) {");
                    out.println("player_slot = f.player3;");
                    out.println("guestid_slot = f.guest_id3;");
                    out.println("f.player3.value = name + spc;");
                    out.println("} else {");
                    out.println("f.player3.focus();");
                    out.println("f.player3.value = name + spc;");
                    out.println("f.player3.focus();");
                    out.println("}");

                    if (size > 3) {
                        out.println("} else {");
                        out.println("if (player4 == '') {");                    // if player4 is empty
                        out.println("if (use_guestdb == 1) {");
                        out.println("player_slot = f.player4;");
                        out.println("guestid_slot = f.guest_id4;");
                        out.println("f.player4.value = name + spc;");
                        out.println("} else {");
                        out.println("f.player4.focus();");
                        out.println("f.player4.value = name + spc;");
                        out.println("f.player4.focus();");
                        out.println("}");

                        if (size > 4) {
                            out.println("} else {");
                            out.println("if (player5 == '') {");                    // if player5 is empty
                            out.println("if (use_guestdb == 1) {");
                            out.println("player_slot = f.player5;");
                            out.println("guestid_slot = f.guest_id5;");
                            out.println("f.player5.value = name + spc;");
                            out.println("} else {");
                            out.println("f.player5.focus();");
                            out.println("f.player5.value = name + spc;");
                            out.println("f.player5.focus();");
                            out.println("}");
                            out.println("}");
                        }
                        out.println("}");
                    }
                    out.println("}");
                }
                out.println("}");
            }
            out.println("}");

            out.println("}");                  // end of script function
            out.println("// -->");
            out.println("</script>");                               // End of script

            //*******************************************************************

            out.println("</head>");
            out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<table border=\"0\" width=\"100%\" valign=\"top\">");  // large table for whole page
            out.println("<tr><td valign=\"top\">");

            out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"336633\" align=\"center\" valign=\"top\">");
            out.println("<tr><td align=\"left\" width=\"300\">");
            out.println("<img src=\"/" + rev + "/images/foretees.gif\" border=0>");
            out.println("</td>");

            out.println("<td align=\"center\">");
            out.println("<font color=\"#ffffff\" size=\"5\">Member Event Sign Up</font>");
            out.println("</font></td>");

            out.println("<td align=\"center\" width=\"300\">");
            out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
            out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
            out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " + SystemUtils.CURRENT_YEAR + " All rights reserved.");
            out.println("</font></td>");
            out.println("</tr></table>");

            out.println("<table border=\"0\" align=\"center\">");                           // table for main page
            out.println("<tr><td align=\"center\"><br>");

            out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
            out.println("<tr>");
            out.println("<td width=\"620\" align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this event registration.");
            out.println("&nbsp; If you want to return without changes, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("option below.");
            out.println("</font></td></tr>");
            out.println("</table>");

            out.println("<font size=\"2\">");
            out.println("<br>Event:&nbsp;&nbsp;<b>" + name + "</b>&nbsp;&nbsp;");
            out.println("Date:&nbsp;&nbsp;<b>" + ((season == 0) ? month + "/" + day + "/" + year : "Season Long") + "</b>&nbsp;&nbsp;");
            if (season == 0) {
                out.println("Time:&nbsp;&nbsp;<b>" + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm + "</b>");
            }
            if (!course.equals("")) {

                if (club.equals("congressional")) {
                    out.println(" &nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + congressionalCustom.getFullCourseName(date, day, course) + "</b>");
                } else {
                    out.println(" &nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
                }

            }
            out.println("<br></font>");

            out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 4 tables below

            out.println("<tr>");
            out.println("<td align=\"center\" valign=\"top\">");     // col for Instructions and Go Back button

            out.println("<font size=\"2\"><br><br><br>");
            out.println("<a href=\"#\" onclick=\"window.open('/" + rev + "/member_help_evnt_instruct.htm', 'newwindow', config='Height=580, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" + rev + "/images/instructions.gif\" border=0></a><br><br><br><br>");

            // this is the cancel form
            out.println("<form action=\"Member_evntSignUp\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"id\" value=" + id + ">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            if (ext_login == true) {     // if came from external login (no frames)
                out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
            }
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
            out.println("</font></td>");

            // this is the form for submitting the request
            out.println("<form action=\"Member_evntSignUp\" method=\"post\" name=\"playerform\" id=\"playerform\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"season\" value=\"" + season + "\">");
            out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");

            if (ext_login == true) {     // if came from external login (no frames)
                out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
            }
            out.println("<td align=\"center\" valign=\"top\" width=\"390\">");

            out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\">");  // table for player selection   width=\"390\"
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Add or Remove Players</b>&nbsp;&nbsp; Note: Click on Names --><br>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\"><font size=\"2\">");

            out.println("<table cellpadding=0 cellspacing=0 border=0>");

            //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            //out.println("<b>Players&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            //out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            //out.println("Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            //out.println("Hdcp Num&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Gender</b>");

            //out.println("<br>");

            out.println("<tr style=\"font-size:10pt;font-weight:bold\">"
                    + "<td></td>"
                    + "<td>&nbsp;&nbsp;&nbsp;&nbsp; Players</td>");

            if (sess_activity_id == 0 && season == 0) {
                out.println("<td>&nbsp;</td>"
                        + "<td>&nbsp;Trans</td>");
            }

            if (export_type != 0 || ask_hdcp == 1) {
                out.println("<td>&nbsp;</td>"
                        + "<td nowrap>&nbsp;" + ((sess_activity_id == 0) ? (club.equals("mpccpb") || club.equals("olyclub") ? "GHIN #" : "Hdcp #") : "USTA #") + "</td>");
            }
            if (export_type != 0 || ask_gender == 1) {
                out.println("<td>&nbsp;</td>"
                        + "<td>Gender&nbsp;</td>");
            }

            out.println("</tr>");

            // Print hidden guest_id inputs
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

            //  add player boxes
            out.println("<tr><td>");
            if (!player1.equals("") && blockP1 == true) {    // IF option to not allow mems to change player1
                out.println("&nbsp;&nbsp;</td>");
                out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">1:&nbsp;<input disabled type=\"text\" id=\"player1\" name=\"player1\" value=\"" + player1 + "\" size=\"26\" maxlength=\"60\"></td>");
                out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            } else {     // all others             
                out.println("<img src=\"/" + rev + "/images/erase.gif\" onclick=\"erasename(1)\" style=\"cursor:hand\"></td>");
                out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\"" + player1 + "\" size=\"26\" maxlength=\"60\"></td>");
            }

            if (sess_activity_id == 0 && season == 0) {
                out.println("<td></td>");
                out.println("<td><select size=\"1\" name=\"p1cw\" id=\"p1cw\">");
                for (i = 0; i < 16; i++) {        // get all c/w options
                    if (tmode[i] == 1 && !parmc.tmodea[i].equals("")) {
                        Common_Config.buildOption(parmc.tmodea[i], parmc.tmodea[i], p1cw, out);
                    }
                }
                out.println("</select></td>");
            } else {
                out.println("<input type=hidden name=p1cw value=\"" + season_tmode + "\">");
            }
            if (export_type != 0 || ask_hdcp == 1) {
                out.println("<td></td>");
                out.println("<td><input type=text name=ghin1 value=\"" + ghin1 + "\" size=8 maxlength=16" + (club.equals("rollinghillsgc") ? " onfocus=\"this.blur()\" ondblclick=\"alert('This field cannot be modified.');\"" : "") + "></td>");
            }
            if (export_type != 0 || ask_gender == 1) {
                out.println("<td></td>");
                out.println("<td><select size=\"1\" name=\"gender1\" id=\"gender1\">");
                Common_Config.buildOption("M", "M", gender1, out);
                Common_Config.buildOption("F", "F", gender1, out);
                out.println("</select></td>");
            }
            out.println("</tr>");

            if (size > 1) {

                out.println("<tr><td>");
                if (!player2.equals("") && blockP2 == true) {    // IF option to not allow mems to change player
                    out.println("&nbsp;&nbsp;</td>");
                    out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">2:&nbsp;<input disabled type=\"text\" id=\"player2\" name=\"player2\" value=\"" + player2 + "\" size=\"26\" maxlength=\"60\"></td>");
                    out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                } else {     // all others             
                    out.println("<img src=\"/" + rev + "/images/erase.gif\" onclick=\"erasename(2)\" style=\"cursor:hand\"></td>");
                    out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\"" + player2 + "\" size=\"26\" maxlength=\"60\"></td>");
                }

                if (sess_activity_id == 0 && season == 0) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"p2cw\" id=\"p2cw\">");
                    for (i = 0; i < 16; i++) {
                        if (tmode[i] == 1 && !parmc.tmodea[i].equals("")) {
                            Common_Config.buildOption(parmc.tmodea[i], parmc.tmodea[i], p2cw, out);
                        }
                    }
                    out.println("</select></td>");
                } else {
                    out.println("<input type=hidden name=p2cw value=\"" + season_tmode + "\">");
                }
                if (export_type != 0 || ask_hdcp == 1) {
                    out.println("<td></td>");
                    out.println("<td><input type=text name=ghin2 value=\"" + ghin2 + "\" size=8 maxlength=16" + (club.equals("rollinghillsgc") ? " onfocus=\"this.blur()\" ondblclick=\"alert('This field cannot be modified.');\"" : "") + "></td>");
                }
                if (export_type != 0 || ask_gender == 1) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"gender2\" id=\"gender2\">");
                    Common_Config.buildOption("M", "M", gender2, out);
                    Common_Config.buildOption("F", "F", gender2, out);
                    out.println("</select></td>");
                }
                out.println("</tr>");

            } else {

                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                out.println("<input type=\"hidden\" name=\"ghin2\" value=\"" + ghin2 + "\">");
                out.println("<input type=\"hidden\" name=\"gender2\" value=\"" + gender2 + "\">");
            }
            if (size > 2) {

                out.println("<tr><td><img src=\"/" + rev + "/images/erase.gif\" onclick=\"erasename(3)\" style=\"cursor:hand\"></td>");
                out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\"" + player3 + "\" size=\"26\" maxlength=\"60\"></td>");

                if (sess_activity_id == 0 && season == 0) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"p3cw\" id=\"p3cw\">");
                    for (i = 0; i < 16; i++) {
                        if (tmode[i] == 1 && !parmc.tmodea[i].equals("")) {       // if specified for event and not empty
                            Common_Config.buildOption(parmc.tmodea[i], parmc.tmodea[i], p3cw, out);
                        }
                    }
                    out.println("</select></td>");
                } else {
                    out.println("<input type=hidden name=p3cw value=\"" + season_tmode + "\">");
                }
                if (export_type != 0 || ask_hdcp == 1) {
                    out.println("<td></td>");
                    out.println("<td><input type=text name=ghin3 value=\"" + ghin3 + "\" size=8 maxlength=16" + (club.equals("rollinghillsgc") ? " onfocus=\"this.blur()\" ondblclick=\"alert('This field cannot be modified.');\"" : "") + "></td>");
                }
                if (export_type != 0 || ask_gender == 1) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"gender3\" id=\"gender3\">");
                    Common_Config.buildOption("M", "M", gender3, out);
                    Common_Config.buildOption("F", "F", gender3, out);
                    out.println("</select></td>");
                }
                out.println("</tr>");

            } else {

                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                out.println("<input type=\"hidden\" name=\"ghin3\" value=\"" + ghin3 + "\">");
                out.println("<input type=\"hidden\" name=\"gender3\" value=\"" + gender3 + "\">");
            }
            if (size > 3) {

                out.println("<tr><td><img src=\"/" + rev + "/images/erase.gif\" onclick=\"erasename(4)\" style=\"cursor:hand\"></td>");
                out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\"" + player4 + "\" size=\"26\" maxlength=\"60\"></td>");

                if (sess_activity_id == 0 && season == 0) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"p4cw\" id=\"p4cw\">");
                    for (i = 0; i < 16; i++) {
                        if (tmode[i] == 1 && !parmc.tmodea[i].equals("")) {
                            Common_Config.buildOption(parmc.tmodea[i], parmc.tmodea[i], p4cw, out);
                        }
                    }
                    out.println("</select></td>");
                } else {
                    out.println("<input type=hidden name=p4cw value=\"" + season_tmode + "\">");
                }
                if (export_type != 0 || ask_hdcp == 1) {
                    out.println("<td></td>");
                    out.println("<td><input type=text name=ghin4 value=\"" + ghin4 + "\" size=8 maxlength=16" + (club.equals("rollinghillsgc") ? " onfocus=\"this.blur()\" ondblclick=\"alert('This field cannot be modified.');\"" : "") + "></td>");
                }
                if (export_type != 0 || ask_gender == 1) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"gender4\" id=\"gender4\">");
                    Common_Config.buildOption("M", "M", gender4, out);
                    Common_Config.buildOption("F", "F", gender4, out);
                    out.println("</select></td>");
                }
                out.println("</tr>");

            } else {

                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                out.println("<input type=\"hidden\" name=\"ghin4\" value=\"" + ghin4 + "\">");
                out.println("<input type=\"hidden\" name=\"gender4\" value=\"" + gender4 + "\">");
            }
            if (size > 4) {

                out.println("<tr><td><img src=\"/" + rev + "/images/erase.gif\" onclick=\"erasename(5)\" style=\"cursor:hand\"></td>");
                out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"26\" maxlength=\"60\"></td>");

                if (sess_activity_id == 0 && season == 0) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
                    for (i = 0; i < 16; i++) {
                        if (tmode[i] == 1 && !parmc.tmodea[i].equals("")) {
                            Common_Config.buildOption(parmc.tmodea[i], parmc.tmodea[i], p5cw, out);
                        }
                    }
                    out.println("</select></td>");
                } else {
                    out.println("<input type=hidden name=p5cw value=\"" + season_tmode + "\">");
                }
                if (export_type != 0 || ask_hdcp == 1) {
                    out.println("<td></td>");
                    out.println("<td><input type=text name=ghin5 value=\"" + ghin5 + "\" size=8 maxlength=16" + (club.equals("rollinghillsgc") ? " onfocus=\"this.blur()\" ondblclick=\"alert('This field cannot be modified.');\"" : "") + "></td>");
                }
                if (export_type != 0 || ask_gender == 1) {
                    out.println("<td></td>");
                    out.println("<td><select size=\"1\" name=\"gender5\" id=\"gender5\">");
                    Common_Config.buildOption("M", "M", gender5, out);
                    Common_Config.buildOption("F", "F", gender5, out);
                    out.println("</select></td>");
                }
                out.println("</tr>");
            } else {

                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                out.println("<input type=\"hidden\" name=\"ghin5\" value=\"" + ghin5 + "\">");
                out.println("<input type=\"hidden\" name=\"gender5\" value=\"" + gender5 + "\">");
            }


            //
            //   Notes
            //
            if (hide != 0) {      // if proshop wants to hide the notes, do not display the text box or notes

                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes

            } else {

                int cols = 3;
                if (export_type == 1 || ask_hdcp + ask_gender == 2) {
                    cols = 7;
                } else if (ask_hdcp + ask_gender == 1) {
                    cols = 5;
                }
                // ((export_type == 0) ? "3" : "7")
                out.println("<tr><td><br></td></tr>");
                out.println("<tr><td valign=top><br><img src=\"/" + rev + "/images/erase.gif\" onclick=\"erasetext('notes')\" style=\"cursor:hand\"></td>");
                out.println("<td colspan=" + cols + " style=\"font-size:10pt;font-weight:bold\" align=center>Notes to Pro:<br>");
                out.println("<textarea name=\"notes\" id=\"notes\" cols=\"42\" rows=\"4\">" + notes + "</textarea>");
                out.println("</td></tr>");
            }

            out.println("</table>");

            // pass these values forward to the verify method when form submitted
            out.println("<input type=\"hidden\" name=\"homeclub1\" value=\"" + homeclub1 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub2\" value=\"" + homeclub2 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub3\" value=\"" + homeclub3 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub4\" value=\"" + homeclub4 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub5\" value=\"" + homeclub5 + "\">");
            out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
            out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
            out.println("<input type=\"hidden\" name=\"phone3\" value=\"" + phone3 + "\">");
            out.println("<input type=\"hidden\" name=\"phone4\" value=\"" + phone4 + "\">");
            out.println("<input type=\"hidden\" name=\"phone5\" value=\"" + phone5 + "\">");
            out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
            out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
            out.println("<input type=\"hidden\" name=\"address3\" value=\"" + address3 + "\">");
            out.println("<input type=\"hidden\" name=\"address4\" value=\"" + address4 + "\">");
            out.println("<input type=\"hidden\" name=\"address5\" value=\"" + address5 + "\">");
            out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
            out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
            out.println("<input type=\"hidden\" name=\"email3\" value=\"" + email3 + "\">");
            out.println("<input type=\"hidden\" name=\"email4\" value=\"" + email4 + "\">");
            out.println("<input type=\"hidden\" name=\"email5\" value=\"" + email5 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt1\" value=\"" + shirt1 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt2\" value=\"" + shirt2 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt3\" value=\"" + shirt3 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt4\" value=\"" + shirt4 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt5\" value=\"" + shirt5 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe1\" value=\"" + shoe1 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe2\" value=\"" + shoe2 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe3\" value=\"" + shoe3 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe4\" value=\"" + shoe4 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe5\" value=\"" + shoe5 + "\">");
            out.println("<input type=\"hidden\" name=\"other1A1\" value=\"" + other1A1 + "\">");
            out.println("<input type=\"hidden\" name=\"other1A2\" value=\"" + other1A2 + "\">");
            out.println("<input type=\"hidden\" name=\"other1A3\" value=\"" + other1A3 + "\">");
            out.println("<input type=\"hidden\" name=\"other2A1\" value=\"" + other2A1 + "\">");
            out.println("<input type=\"hidden\" name=\"other2A2\" value=\"" + other2A2 + "\">");
            out.println("<input type=\"hidden\" name=\"other2A3\" value=\"" + other2A3 + "\">");
            out.println("<input type=\"hidden\" name=\"other3A1\" value=\"" + other3A1 + "\">");
            out.println("<input type=\"hidden\" name=\"other3A2\" value=\"" + other3A2 + "\">");
            out.println("<input type=\"hidden\" name=\"other3A3\" value=\"" + other3A3 + "\">");
            out.println("<input type=\"hidden\" name=\"other4A1\" value=\"" + other4A1 + "\">");
            out.println("<input type=\"hidden\" name=\"other4A2\" value=\"" + other4A2 + "\">");
            out.println("<input type=\"hidden\" name=\"other4A3\" value=\"" + other4A3 + "\">");
            out.println("<input type=\"hidden\" name=\"other5A1\" value=\"" + other5A1 + "\">");
            out.println("<input type=\"hidden\" name=\"other5A2\" value=\"" + other5A2 + "\">");
            out.println("<input type=\"hidden\" name=\"other5A3\" value=\"" + other5A3 + "\">");

            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"id\" value=" + id + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=" + hide + ">");

            out.println("<br><font size=\"1\">");
            for (i = 0; i < 16; i++) {
                if (tmode[i] == 1) {       // if specified for event
                    if (!parmc.tmodea[i].equals("")) {
                        out.println(parmc.tmodea[i] + " = " + parmc.tmode[i] + "&nbsp;&nbsp;");
                    }
                }
            }
            out.println("</font><br>");

            if (!newRequest) {
                if (sess_activity_id == 0 && club.equals("snoqualmieridge") && season == 1) {
                    out.println("<b>Contact Golf Shop To Cancel</b>&nbsp;&nbsp;");
                } else {
                    out.println("<input type=submit value=\"Cancel Entry\" name=\"remove\" onclick=\"return confirm('Are you sure you want to cancel your request?')\">&nbsp;&nbsp;&nbsp;");
                }
            }

            out.println("<input type=submit value=\"" + ((ask_more == 0) ? "Submit" : "Continue") + "\" name=\"submitForm\">");
            out.println("</font></td></tr>");
            out.println("</table>");

            //if (size > 1) {

            out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" width=\"390\" cellpadding=5 cellspacing=5 align=\"center\">");
            out.println("<tr><td align=\"left\">");
            out.println("<font color=\"red\" size=\"2\">");
            out.println("<b>Note:</b>  If adding a guest, click on the proper guest indicator, then click in the ");
            out.println("player position immediately after the guest indicator ");
            out.println("and enter the name of the guest and their home club, if appropriate.");
            out.println("</font></td></tr></table>");
            /*
            } else {
            
            out.println("<p align=\"left\"><font size=\"2\">");
            out.println("<b>Note:</b>  The format of this event indicates that you may only register yourself. ");
            out.println("</font></p>");
            }
             */
            out.println("</td>");                                // end of table and column

            //if (size > 1) {    // do not display names, etc. if member is only one that can sign up

            out.println("<td align=\"center\" valign=\"top\">");

            // ********************************************************************************
            //   If we got control from user clicking on a letter in the Member List,
            //   then we must build the name list.
            // ********************************************************************************
            String letter = "";

            if (req.getParameter("letter") != null) {     // if user clicked on a name letter

                letter = req.getParameter("letter");

                if (!letter.equals("Partner List")) {      // if not Partner List request

                    letter = letter + "%";

                    String first = "";
                    String mid = "";
                    String last = "";
                    String bname = "";
                    String wname = "";
                    String dname = "";
                    String wc = "";
                    String ghin = "";
                    //String gender = "";

                    out.println("<table border=\"1\" width=\"140\" bgcolor=\"#f5f5dc\" valign=\"top\">");      // name list
                    out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
                    out.println("<font color=\"#ffffff\" size=\"2\">");
                    out.println("<b>Name List</b>");
                    out.println("</font></td>");
                    out.println("</tr><tr>");
                    out.println("<td align=\"center\">");
                    out.println("<font size=\"2\">");
                    out.println("Click on name to add");
                    out.println("</font></td></tr>");

                    try {

                        PreparedStatement stmt2 = con.prepareStatement(
                                "SELECT name_last, name_first, name_mi, wc, ghin, gender "
                                + "FROM member2b "
                                + "WHERE name_last LIKE ? AND inact = 0 "
                                + "ORDER BY name_last, name_first, name_mi");

                        stmt2.clearParameters();               // clear the parms
                        stmt2.setString(1, letter);            // put the parm in stmt
                        rs = stmt2.executeQuery();             // execute the prepared stmt //form.bname.

                        out.println("<tr><td align=\"left\"><font size=\"2\">");
                        out.println("<select size=\"20\" name=\"bname\" onclick=\"movename(this.value)\" style=\"cursor:hand\">");

                        while (rs.next()) {

                            last = rs.getString(1);
                            first = rs.getString(2);
                            mid = rs.getString(3);
                            wc = rs.getString(4);           // walk/cart preference
                            ghin = rs.getString(5);
                            gender = rs.getString(6);

                            nowc = 1;    // default to 'not found'

                            i = 0;
                            loopwc1:
                            while (i < 16) {
                                if (tmode[i] == 1) {       // if specified for event
                                    if (parmc.tmodea[i].equals(wc)) {
                                        nowc = 0;
                                        break loopwc1;
                                    }
                                }
                                i++;
                            }

                            if (nowc != 0) {      // if wc not supported for this event

                                i = 0;
                                loopwc2:
                                while (i < 16) {
                                    if (tmode[i] == 1) {       // if specified for event
                                        if (!parmc.tmodea[i].equals("")) {
                                            wc = parmc.tmodea[i];                // change wc option for this event
                                            break loopwc2;
                                        }
                                    }
                                    i++;
                                }
                            }

                            if (mid.equals("")) {

                                bname = first + " " + last;
                                dname = last + ", " + first;
                            } else {

                                bname = first + " " + mid + " " + last;
                                dname = last + ", " + first + " " + mid;
                            }

                            wname = bname + ":" + wc + ":" + gender + ":" + ghin;              // combine name:wc for script

                            if (club.equals("cwcpga") && !ghin.equals("")) {
                                out.println("<option value=\"" + wname + "\">" + dname + " " + ghin + "</option>");
                            } else {
                                out.println("<option value=\"" + wname + "\">" + dname + "</option>");
                            }
                        }

                        out.println("</select>");
                        out.println("</font></td></tr>");

                        stmt2.close();
                    } catch (Exception ignore) {
                    }

                    out.println("</table>");

                }        // end of IF Partner List or Letter
            }           // end of IF letter parm

            if (letter.equals("") || letter.equals("Partner List")) {  // if no letter or Partner List request

                alphaTable.displayPartnerList(user, sess_activity_id, 1, con, out);

            }        // end of if letter display

            out.println("</td>");

            out.println("</td>");                                      // end of this column
            out.println("<td width=\"200\" valign=\"top\">");

            //
            //   Output the Alphabit Table for Members' Last Names
            //
            alphaTable.getTable(out, user);

            if (x != 0) {                    // x value from event 

                //
                //  add a table for 'x'
                //
                out.println("<font size=\"1\"><br></font>");
                out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");
                out.println("<tr bgcolor=\"#336633\">");
                out.println("<td align=\"center\">");
                out.println("<font color=\"#FFFFFF\" size=\"2\">");
                out.println("<b>Member TBD</b>");
                out.println("</font></td>");
                out.println("</tr>");
                out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
                out.println("Use 'X' to reserve a position for a Member.<br>");
                out.println("</font></td></tr>");
                out.println("<tr><td align=\"left\" bgcolor=\"#FFFFFF\">");
                out.println("<font size=\"2\">");
                out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onClick=\"moveguest('X')\">X</a>");
                out.println("</font></td></tr></table>");      // end of this table
            }

            //
            //  add a table for the Guest Types
            //
            out.println("<font size=\"1\"><br></font>");
            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\">");
            out.println("<tr bgcolor=\"#336633\">");
            out.println("<td align=\"center\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("<b>Guest Types</b>");
            out.println("</font></td>");
            out.println("</tr>");

            //
            //  first we must count how many fields there will be
            //
            xCount = 0;
            for (i = 0; i < parm.MAX_Guests; i++) {

                if (!parm.guest[i].equals("") && parm.gOpt[i] == 0) {   // count the X and guest names

                    xCount++;
                }
            }
            i = 0;
            if (xCount != 0) {                       // if guest names, display them in list

                if (club.equals("medinahcc") || club.equals("wellesley")) {     // Medinah & Wellesley only use 1 guest type for events

                    xCount = 2;             // set size to at least 2

                } else {

                    if (xCount < 2) {

                        xCount = 2;             // set size to at least 2
                    }
                    if (xCount > 8) {

                        xCount = 8;             // set size to no more than 8 showing at once (it will scroll)
                    }
                }

                out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
                out.println("<b>**</b> Add guests immediately<br><b>after</b> host member.<br>");
                out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\">");

                //
                //  If Medinah, then only display guest type for events
                //
                if (club.equals("medinahcc") || club.equals("olyclub")) {

                    out.println("<option value=\"Event Guest\">Event Guest</option>");

                } else {

                    if (club.equals("wellesley")) {

                        out.println("<option value=\"Tourney Guest\">Tourney Guest</option>");

                    } else {

                        for (i = 0; i < parm.MAX_Guests; i++) {

                            if (!parm.guest[i].equals("") && parm.gOpt[i] == 0) {   // if guest name is open for members

                                out.println("<option value=\"" + parm.guest[i] + "|" + parm.gDb[i] + "\">" + parm.guest[i] + "</option>");
                            }
                        }
                    }
                }
                out.println("</select>");
                out.println("</font></td></tr></table>");      // end of this table

            } else {

                out.println("</table>");      // end the table if none specified
            }

            out.println("</td>");

            //}  // end of IF size > 1

            out.println("</tr>");
            out.println("</form>");     // end of playerform
            out.println("</table>");      // end of large table containing 4 smaller tables

            out.println("</font></td></tr>");
            out.println("</table>");                      // end of main page table
            //
            //  End of HTML page
            //
            out.println("</td></tr>");
            out.println("</table>");                      // end of whole page table
            out.println("</font></body></html>");
            out.close();
        } // end old skin

    }   // end of doPost

    // *********************************************************
    //  Get a new id and set a new entry in the Event Sign Up db table
    // *********************************************************
/*
    private int getNewId(Connection con, String name, String course, long date, int time) {
    
    
    //Statement stmtm = null;
    ResultSet rs = null;
    
    
    int month = 0;
    int day = 0;
    int year = 0;
    int hr = 0;
    int min = 0;
    int id = 0;
    int r_time = 0;
    long r_date = 0;
    
    try {
    //
    //   Get current date and time
    //
    Calendar cal = new GregorianCalendar();        // get current date and time
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    hr = cal.get(Calendar.HOUR_OF_DAY);
    min = cal.get(Calendar.MINUTE);
    
    //
    //  Build the 'time' string for display
    //
    //    Adjust the time based on the club's time zone (we are Central)
    //
    r_time = (hr * 100) + min;
    
    r_time = SystemUtils.adjustTime(con, r_time);       // adjust for time zone
    
    if (r_time < 0) {                // if negative, then we went back or ahead one day
    
    r_time = 0 - r_time;          // convert back to positive value
    
    if (r_time < 1200) {           // if AM, then we rolled ahead 1 day
    
    //
    // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
    //
    cal.add(Calendar.DATE,1);                     // get next day's date
    
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    
    } else {                        // we rolled back 1 day
    
    //
    // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
    //
    cal.add(Calendar.DATE,-1);                     // get yesterday's date
    
    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH);
    day = cal.get(Calendar.DAY_OF_MONTH);
    }
    }
    
    hr = r_time / 100;                // get adjusted hour
    min = r_time - (hr * 100);          // get minute value
    
    month = month + 1;                            // month starts at zero
    r_date = (year * 10000) + (month * 100) + day;
    
    
    PreparedStatement pstmt = con.prepareStatement (
    "INSERT INTO evntsup2b " +
    "(name, courseName, player1, player2, player3, player4, player5, " +
    "username1, username2, username3, username4, username5, " +
    "p1cw, p2cw, p3cw, p4cw, p5cw, in_use, in_use_by, hndcp1, hndcp2, hndcp3, hndcp4, " +
    "hndcp5, notes, hideNotes, c_date, c_time, r_date, r_time, wait, " +
    "userg1, userg2, userg3, userg4, userg5, hole) " +
    "VALUES (?, ?, '', '', '', '', '', " +
    "'', '', '', '', '', " +
    "'', '', '', '', '', 0, '', 0, 0, 0, 0, " +
    "0, '', 0, ?, ?, ?, ?, 0, " +
    "'', '', '', '', '', '')");
    
    pstmt.clearParameters();        // clear the parms
    pstmt.setString(1, name);
    pstmt.setString(2, course);
    //      pstmt.setInt(3, id);
    pstmt.setLong(3, date);             // cut-off date
    pstmt.setInt(4, time);              // cut-off time
    pstmt.setLong(5, r_date);           // date registered
    pstmt.setInt(6, r_time);            // time registered
    
    pstmt.executeUpdate();
    
    pstmt.close();
    
    
    //
    //  now get the id of the entry we just added
    //
    pstmt = con.prepareStatement (
    "SELECT LAST_INSERT_ID()");      // returns the last auto-increment id field this con inserted
    
    pstmt.clearParameters();        // clear the parms
    rs = pstmt.executeQuery();      // execute the prepared stmt
    
    if (rs.next()) {
    
    id = rs.getInt(1);
    }
    pstmt.close();      
    
    }
    catch (Exception ignore) {
    
    id = 0;   // indicate failed
    }
    
    return id;
    }
     */
    // *********************************************************
    //  Process eventreservation request from self
    // *********************************************************
    private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


        ResultSet rs = null;
        ResultSet rs2 = null;

        boolean ext_login = false;

        if (req.getParameter("ext-login") != null || req.getParameter("ext-dReq") != null) {    // make sure we check for both (we only need one, but somehow ended up using 2 parms for this)

            ext_login = true;      // indicate external login (from email link)
        }

        //
        //  Get this session's club, username and full name
        //
        String user = "";
        
        if (ext_login == true) {
            
            user = (String) session.getAttribute("ext-user");
            
        } else {
            
            user = (String) session.getAttribute("user");
        }
        
        String club = (String) session.getAttribute("club");
        String mtype = (String) session.getAttribute("mtype");    // member's mtype 
        String mship = (String) session.getAttribute("mship");    // member's mship type
        //String caller = (String)session.getAttribute("caller");
        //String userName = (String)session.getAttribute("name");   // get users full name

        int sess_activity_id = (Integer) session.getAttribute("activity_id");

        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

        Map<String, Object> result_map = new LinkedHashMap<String, Object>();
        Map<String, Object> hidden_field_map = new LinkedHashMap<String, Object>();

        Gson gson_obj = new Gson();

        String in_use_by = "";

        List<String> messages = new ArrayList<String>();

        String name = "";
        String playerName = "";
        String course = "";
        String sid = "";
        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";
        String player5 = "";
        String g1 = "";
        String g2 = "";
        String g3 = "";
        String g4 = "";
        String g5 = "";
        String oldPlayer1 = "";
        String oldPlayer2 = "";
        String oldPlayer3 = "";
        String oldPlayer4 = "";
        String oldPlayer5 = "";
        String oldUser1 = "";
        String oldUser2 = "";
        String oldUser3 = "";
        String oldUser4 = "";
        String oldUser5 = "";
        String oldp1cw = "";
        String oldp2cw = "";
        String oldp3cw = "";
        String oldp4cw = "";
        String oldp5cw = "";
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
        String fname1 = "";
        String lname1 = "";
        String mi1 = "";
        String fname2 = "";
        String lname2 = "";
        String mi2 = "";
        String fname3 = "";
        String lname3 = "";
        String mi3 = "";
        String fname4 = "";
        String lname4 = "";
        String mi4 = "";
        String fname5 = "";
        String lname5 = "";
        String mi5 = "";
        String act_ampm = "";
        String act_time = "";
        String wplayer1 = "";
        String wplayer2 = "";
        String wplayer3 = "";
        String wplayer4 = "";
        String wplayer5 = "";
        String wuser1 = "";
        String wuser2 = "";
        String wuser3 = "";
        String wuser4 = "";
        String wuser5 = "";
        String wp1cw = "";
        String wp2cw = "";
        String wp3cw = "";
        String wp4cw = "";
        String wp5cw = "";
        String userg1 = "";
        String userg2 = "";
        String userg3 = "";
        String userg4 = "";
        String userg5 = "";
        String memberName = "";
        String index = "";
        String proemail1 = "";
        String proemail2 = "";
        String gender1 = "";
        String gender2 = "";
        String gender3 = "";
        String gender4 = "";
        String gender5 = "";
        String ghin1 = "";
        String ghin2 = "";
        String ghin3 = "";
        String ghin4 = "";
        String ghin5 = "";
        String homeclub1 = "";
        String homeclub2 = "";
        String homeclub3 = "";
        String homeclub4 = "";
        String homeclub5 = "";
        String phone1 = "";
        String phone2 = "";
        String phone3 = "";
        String phone4 = "";
        String phone5 = "";
        String address1 = "";
        String address2 = "";
        String address3 = "";
        String address4 = "";
        String address5 = "";
        String email1 = "";
        String email2 = "";
        String email3 = "";
        String email4 = "";
        String email5 = "";
        String shirt1 = "";
        String shirt2 = "";
        String shirt3 = "";
        String shirt4 = "";
        String shirt5 = "";
        String shoe1 = "";
        String shoe2 = "";
        String shoe3 = "";
        String shoe4 = "";
        String shoe5 = "";
        String other1A1 = "";
        String other1A2 = "";
        String other1A3 = "";
        String other2A1 = "";
        String other2A2 = "";
        String other2A3 = "";
        String other3A1 = "";
        String other3A2 = "";
        String other3A3 = "";
        String other4A1 = "";
        String other4A2 = "";
        String other4A3 = "";
        String other5A1 = "";
        String other5A2 = "";
        String other5A3 = "";
        String otherQ1 = "";
        String otherQ2 = "";
        String otherQ3 = "";
        String gplayer = "";

        int id = 0;
        int event_id = 0;
        int count = 0;
        int in_use = 0;
        int day = 0;
        int month = 0;
        int year = 0;
        int guests = 0;
        int eguests = 0;
        int x = 0;
        int xhrs = 0;
        int act_hr = 0;
        int act_min = 0;
        int i = 0;
        int etype = 0;
        int xcount = 0;
        int sendemail = 0;
        int emailNew = 0;
        int emailMod = 0;
        int emailCan = 0;
        int members = 0;
        int teams = 0;
        int t = 0;
        int max = 0;
        int minsize = 0;
        int wait = 0;
        int checkWait = 0;
        int gi = 0;
        int holes = 0;
        int season = 0;
        int export_type = 0;
        int guest_id1 = 0;
        int guest_id2 = 0;
        int guest_id3 = 0;
        int guest_id4 = 0;
        int guest_id5 = 0;
        int oldguest_id1 = 0;
        int oldguest_id2 = 0;
        int oldguest_id3 = 0;
        int oldguest_id4 = 0;
        int oldguest_id5 = 0;

        int ask_homeclub = 0;
        int ask_phone = 0;
        int ask_address = 0;
        int ask_hdcp = 0;
        int ask_email = 0;
        int ask_gender = 0;
        int ask_shirtsize = 0;
        int ask_shoesize = 0;
        int ask_otherA1 = 0;
        int ask_otherA2 = 0;
        int ask_otherA3 = 0;
        int req_guestname = 0;
        int req_homeclub = 0;
        int req_phone = 0;
        int req_address = 0;
        int req_hdcp = 0;
        int req_email = 0;
        int req_gender = 0;
        int req_shirtsize = 0;
        int req_shoesize = 0;
        int req_otherA1 = 0;
        int req_otherA2 = 0;
        int req_otherA3 = 0;
        int who_shirtsize = 0; // not used right now
        int who_shoesize = 0;
        int who_otherQ1 = 0;
        int who_otherQ2 = 0;
        int who_otherQ3 = 0;

        float hndcp1 = 99;
        float hndcp2 = 99;
        float hndcp3 = 99;
        float hndcp4 = 99;
        float hndcp5 = 99;

        boolean guestError = false;            // init error flag
        boolean invalidGuest = false;
        boolean hit = false;                   // init error flag
        boolean guestdbTbaAllowed = false;


        //
        //  Arrays to hold member & guest names to tie guests to members
        //
        String[] gstA = new String[5];     // guests name (prefixed w/ guest type)
        String[] memA = new String[5];     // members
        String[] usergA = new String[5];   // guests' associated member (username)
        String[] player = new String[5];
        String[] hdcp = new String[5];
        String[] gender = new String[5];
        String[] homeclub = new String[5];
        String[] phone = new String[5];
        String[] address = new String[5];
        String[] email = new String[5];
        String[] shirt = new String[5];
        String[] shoe = new String[5];
        String[] otherA1 = new String[5];   // other answers
        String[] otherA2 = new String[5];
        String[] otherA3 = new String[5];
        String[] oldUserA = new String[5];
        int[] guest_idA = new int[5];

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(sess_activity_id, con);

        //  If guest tracking is in use, determine whether names are optional or required
        if (Utilities.isGuestTrackingConfigured(sess_activity_id, con) && Utilities.isGuestTrackingTbaAllowed(sess_activity_id, false, con)) {
            guestdbTbaAllowed = true;
        }

        //
        // Get all the parameters passed from the form
        //
        course = req.getParameter("course");        //  name of course
        name = req.getParameter("name");            //  name of event
        index = req.getParameter("index");
        sid = req.getParameter("id");               //  id of event entry
        event_id = (req.getParameter("event_id") != null) ? Integer.parseInt(req.getParameter("event_id")) : 0;
        player1 = req.getParameter("player1");
        player2 = req.getParameter("player2");
        player3 = req.getParameter("player3");
        player4 = req.getParameter("player4");
        player5 = req.getParameter("player5");
        p1cw = req.getParameter("p1cw");
        p2cw = req.getParameter("p2cw");
        p3cw = req.getParameter("p3cw");
        p4cw = req.getParameter("p4cw");
        p5cw = req.getParameter("p5cw");
        gender1 = (req.getParameter("gender1") != null) ? req.getParameter("gender1") : "";
        gender2 = (req.getParameter("gender2") != null) ? req.getParameter("gender2") : "";
        gender3 = (req.getParameter("gender3") != null) ? req.getParameter("gender3") : "";
        gender4 = (req.getParameter("gender4") != null) ? req.getParameter("gender4") : "";
        gender5 = (req.getParameter("gender5") != null) ? req.getParameter("gender5") : "";
        ghin1 = (req.getParameter("ghin1") != null) ? req.getParameter("ghin1").trim() : "";
        ghin2 = (req.getParameter("ghin2") != null) ? req.getParameter("ghin2").trim() : "";
        ghin3 = (req.getParameter("ghin3") != null) ? req.getParameter("ghin3").trim() : "";
        ghin4 = (req.getParameter("ghin4") != null) ? req.getParameter("ghin4").trim() : "";
        ghin5 = (req.getParameter("ghin5") != null) ? req.getParameter("ghin5").trim() : "";
        homeclub1 = (req.getParameter("homeclub1") != null) ? req.getParameter("homeclub1").trim() : "";
        homeclub2 = (req.getParameter("homeclub2") != null) ? req.getParameter("homeclub2").trim() : "";
        homeclub3 = (req.getParameter("homeclub3") != null) ? req.getParameter("homeclub3").trim() : "";
        homeclub4 = (req.getParameter("homeclub4") != null) ? req.getParameter("homeclub4").trim() : "";
        homeclub5 = (req.getParameter("homeclub5") != null) ? req.getParameter("homeclub5").trim() : "";
        phone1 = (req.getParameter("phone1") != null) ? req.getParameter("phone1").trim() : "";
        phone2 = (req.getParameter("phone2") != null) ? req.getParameter("phone2").trim() : "";
        phone3 = (req.getParameter("phone3") != null) ? req.getParameter("phone3").trim() : "";
        phone4 = (req.getParameter("phone4") != null) ? req.getParameter("phone4").trim() : "";
        phone5 = (req.getParameter("phone5") != null) ? req.getParameter("phone5").trim() : "";
        address1 = (req.getParameter("address1") != null) ? req.getParameter("address1").trim() : "";
        address2 = (req.getParameter("address2") != null) ? req.getParameter("address2").trim() : "";
        address3 = (req.getParameter("address3") != null) ? req.getParameter("address3").trim() : "";
        address4 = (req.getParameter("address4") != null) ? req.getParameter("address4").trim() : "";
        address5 = (req.getParameter("address5") != null) ? req.getParameter("address5").trim() : "";
        email1 = (req.getParameter("email1") != null) ? req.getParameter("email1").trim() : "";
        email2 = (req.getParameter("email2") != null) ? req.getParameter("email2").trim() : "";
        email3 = (req.getParameter("email3") != null) ? req.getParameter("email3").trim() : "";
        email4 = (req.getParameter("email4") != null) ? req.getParameter("email4").trim() : "";
        email5 = (req.getParameter("email5") != null) ? req.getParameter("email5").trim() : "";
        shirt1 = (req.getParameter("shirt1") != null) ? req.getParameter("shirt1").trim() : "";
        shirt2 = (req.getParameter("shirt2") != null) ? req.getParameter("shirt2").trim() : "";
        shirt3 = (req.getParameter("shirt3") != null) ? req.getParameter("shirt3").trim() : "";
        shirt4 = (req.getParameter("shirt4") != null) ? req.getParameter("shirt4").trim() : "";
        shirt5 = (req.getParameter("shirt5") != null) ? req.getParameter("shirt5").trim() : "";
        shoe1 = (req.getParameter("shoe1") != null) ? req.getParameter("shoe1").trim() : "";
        shoe2 = (req.getParameter("shoe2") != null) ? req.getParameter("shoe2").trim() : "";
        shoe3 = (req.getParameter("shoe3") != null) ? req.getParameter("shoe3").trim() : "";
        shoe4 = (req.getParameter("shoe4") != null) ? req.getParameter("shoe4").trim() : "";
        shoe5 = (req.getParameter("shoe5") != null) ? req.getParameter("shoe5").trim() : "";
        other1A1 = (req.getParameter("other1A1") != null) ? req.getParameter("other1A1").trim() : "";
        other1A2 = (req.getParameter("other1A2") != null) ? req.getParameter("other1A2").trim() : "";
        other1A3 = (req.getParameter("other1A3") != null) ? req.getParameter("other1A3").trim() : "";
        other2A1 = (req.getParameter("other2A1") != null) ? req.getParameter("other2A1").trim() : "";
        other2A2 = (req.getParameter("other2A2") != null) ? req.getParameter("other2A2").trim() : "";
        other2A3 = (req.getParameter("other2A3") != null) ? req.getParameter("other2A3").trim() : "";
        other3A1 = (req.getParameter("other3A1") != null) ? req.getParameter("other3A1").trim() : "";
        other3A2 = (req.getParameter("other3A2") != null) ? req.getParameter("other3A2").trim() : "";
        other3A3 = (req.getParameter("other3A3") != null) ? req.getParameter("other3A3").trim() : "";
        other4A1 = (req.getParameter("other4A1") != null) ? req.getParameter("other4A1").trim() : "";
        other4A2 = (req.getParameter("other4A2") != null) ? req.getParameter("other4A2").trim() : "";
        other4A3 = (req.getParameter("other4A3") != null) ? req.getParameter("other4A3").trim() : "";
        other5A1 = (req.getParameter("other5A1") != null) ? req.getParameter("other5A1").trim() : "";
        other5A2 = (req.getParameter("other5A2") != null) ? req.getParameter("other5A2").trim() : "";
        other5A3 = (req.getParameter("other5A3") != null) ? req.getParameter("other5A3").trim() : "";
        guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
        guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
        guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
        guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
        guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;

        String notes = req.getParameter("notes").trim();           // Notes
        String hides = req.getParameter("hide");                   // hide Notes

        //
        //  Convert date & time from string to int
        //
        try {

            id = Integer.parseInt(sid);
        } catch (NumberFormatException e) {
        }

        //
        //  Check C/W's for null
        //
        if (p1cw == null) {
            p1cw = "";
        }
        if (p2cw == null) {
            p2cw = "";
        }
        if (p3cw == null) {
            p3cw = "";
        }
        if (p4cw == null) {
            p4cw = "";
        }
        if (p5cw == null) {
            p5cw = "";
        }
        if (player1 == null) {
            player1 = "";
        }
        if (player2 == null) {
            player2 = "";
        }
        if (player3 == null) {
            player3 = "";
        }
        if (player4 == null) {
            player4 = "";
        }
        if (player5 == null) {
            player5 = "";
        }

        //
        //  Check if this entry is still 'in use' and still in use by this user??
        //
        //  This is necessary because the user may have gone away while holding this slot.  If the
        //  slot timed out (system timer), the slot would be marked 'not in use' and another
        //  user could pick it up.  The original holder could be trying to use it now.
        //
        //  Note: The old variables are used to detect if a player changed and then passed to sendEmail via parmEmail
        //
        try {

            PreparedStatement pstmt = con.prepareStatement(
                    "SELECT * FROM evntsup2b WHERE id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                oldPlayer1 = rs.getString("player1");
                oldPlayer2 = rs.getString("player2");
                oldPlayer3 = rs.getString("player3");
                oldPlayer4 = rs.getString("player4");
                oldPlayer5 = rs.getString("player5");
                oldUser1 = rs.getString("username1");
                oldUser2 = rs.getString("username2");
                oldUser3 = rs.getString("username3");
                oldUser4 = rs.getString("username4");
                oldUser5 = rs.getString("username5");
                oldp1cw = rs.getString("p1cw");
                oldp2cw = rs.getString("p2cw");
                oldp3cw = rs.getString("p3cw");
                oldp4cw = rs.getString("p4cw");
                oldp5cw = rs.getString("p5cw");
                oldguest_id1 = rs.getInt("guest_id1");
                oldguest_id2 = rs.getInt("guest_id2");
                oldguest_id3 = rs.getInt("guest_id3");
                oldguest_id4 = rs.getInt("guest_id4");
                oldguest_id5 = rs.getInt("guest_id5");

                in_use = rs.getInt("in_use");
                in_use_by = rs.getString("in_use_by");
                wait = rs.getInt("wait");

            }

            pstmt.close();

            if ((in_use == 0) || (!in_use_by.equals(user))) {    // if entry not in use or not by this user

                out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                out.println("<BODY bgcolor=\"#CCCCAA\">");
                out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
                out.println("<BR><BR>Sorry, but this event entry has been returned to the system.<BR>");
                out.println("<BR>The system timed out and released it.");
                out.println("<font size=\"2\"><br><br>");
                if (ext_login == true) {     // if came from external login (no frames)
                    out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                } else if (!index.equals("") && index.equals("0")) {
                    out.println("<form action=\"Member_events2\" method=\"post\">");
                } else {
                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                }
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("</form></font>");
                out.println("</center></body></html>");
                out.close();
                return;
            }

            PreparedStatement pstmtev = con.prepareStatement(
                    "SELECT * "
                    + "FROM events2b "
                    + "WHERE name = ? AND courseName = ? AND activity_id = ?");

            pstmtev.clearParameters();
            pstmtev.setString(1, name);
            pstmtev.setString(2, course);
            pstmtev.setInt(3, sess_activity_id);
            rs = pstmtev.executeQuery();

            if (rs.next()) {

                year = rs.getInt("year");       // get date & time for email msgs
                month = rs.getInt("month");
                day = rs.getInt("day");
                etype = rs.getInt("type");
                act_hr = rs.getInt("act_hr");
                act_min = rs.getInt("act_min");
                max = rs.getInt("max");
                minsize = rs.getInt("minsize");
                x = rs.getInt("x");
                xhrs = rs.getInt("xhrs");
                holes = rs.getInt("holes");
                season = rs.getInt("season");
                proemail1 = rs.getString("email1");
                proemail2 = rs.getString("email2");
                export_type = rs.getInt("export_type");

                ask_homeclub = rs.getInt("ask_homeclub");
                ask_phone = rs.getInt("ask_phone");
                ask_address = rs.getInt("ask_address");
                ask_hdcp = rs.getInt("ask_hdcp");
                ask_email = rs.getInt("ask_email");
                ask_gender = rs.getInt("ask_gender");
                ask_shirtsize = rs.getInt("ask_shirtsize");
                ask_shoesize = rs.getInt("ask_shoesize");
                ask_otherA1 = rs.getInt("ask_otherA1");
                ask_otherA2 = rs.getInt("ask_otherA2");
                ask_otherA3 = rs.getInt("ask_otherA3");

                req_guestname = rs.getInt("req_guestname");
                req_homeclub = rs.getInt("req_homeclub");
                req_phone = rs.getInt("req_phone");
                req_address = rs.getInt("req_address");
                req_hdcp = rs.getInt("req_hdcp");
                req_email = rs.getInt("req_email");
                req_gender = rs.getInt("req_gender");
                req_shirtsize = rs.getInt("req_shirtsize");
                req_shoesize = rs.getInt("req_shoesize");
                req_otherA1 = rs.getInt("req_otherA1");
                req_otherA2 = rs.getInt("req_otherA2");
                req_otherA3 = rs.getInt("req_otherA3");

                otherQ1 = rs.getString("otherQ1");
                otherQ2 = rs.getString("otherQ2");
                otherQ3 = rs.getString("otherQ3");

            }

            pstmtev.close();

        } catch (Exception e) {

            dbError(out, e);
            return;
        }

        //
        //  Create time values for email msg below
        //
        act_ampm = " AM";

        if (act_hr == 0) {

            act_hr = 12;                 // change to 12 AM (midnight)

        } else if (act_hr == 12) {

            act_ampm = " PM";         // change to Noon

        }
        if (act_hr > 12) {

            act_hr = act_hr - 12;
            act_ampm = " PM";             // change to 12 hr clock
        }

        //
        //  convert time to hour and minutes for email msg
        //
        act_time = act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + act_ampm;


        //
        //  If request is to 'Cancel This Res', then clear all fields for this slot
        //
        //  First, make sure user is already in the entry
        //
        if (req.getParameter("remove") != null) {
            
            boolean userFound = false;

            try {

                PreparedStatement pstmt4 = con.prepareStatement(
                        "SELECT username1, username2, username3, username4, username5 "
                        + "FROM evntsup2b "
                        + "WHERE name = ? AND id = ?");

                pstmt4.clearParameters();
                pstmt4.setString(1, name);
                pstmt4.setInt(2, id);
                rs = pstmt4.executeQuery();

                if (rs.next()) {
                    
                    user1 = rs.getString("username1");
                    user2 = rs.getString("username2");
                    user3 = rs.getString("username3");
                    user4 = rs.getString("username4");
                    user5 = rs.getString("username5");
                }
                    
                pstmt4.close();

            } catch (Exception e4) {

                dbError(out, e4);
                return;
            }

            if (user.equalsIgnoreCase(user1) || user.equalsIgnoreCase(user2) || user.equalsIgnoreCase(user3) || user.equalsIgnoreCase(user4) || user.equalsIgnoreCase(user5)) {
                
                userFound = true;              // user is part of this event entry
                
            } else if (((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent")) ||
                             (club.equals("edina") && (mtype.startsWith("Adult") || mtype.startsWith("Pre-Leg")))) {
              
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
                                + "FROM member2b WHERE memNum = ? AND " + mtypeString);

                        pstmtDenver.clearParameters();      
                        pstmtDenver.setString(1, mNumD);
                        rsDenver = pstmtDenver.executeQuery();    

                        loopDenver:
                        while (rsDenver.next()) {

                            String userD = rsDenver.getString("username");   // get the dependent's username

                            if (userD.equalsIgnoreCase(user1) || userD.equalsIgnoreCase(user2) || userD.equalsIgnoreCase(user3) || userD.equalsIgnoreCase(user4) || userD.equalsIgnoreCase(user5)) {

                                userFound = true;              // user is part of this event entry
                                break loopDenver;              // done
                            }
                        }
                  
                        pstmtDenver.close();

                    }       // end of IF mNumD

                } catch (Exception e9) {

                    Utilities.logError("Member_evntSignUp: Error processing events (custom) for " + club + ", User: " + user + ", Error: " + e9.getMessage());

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
                
            }           // end of Custom

            if (userFound == true) {     
                
                player1 = "";                  // set reservation fields to null
                player2 = "";
                player3 = "";
                player4 = "";
                player5 = "";
                p1cw = "";
                p2cw = "";
                p3cw = "";
                p4cw = "";
                p5cw = "";
                user1 = "";
                user2 = "";
                user3 = "";
                user4 = "";
                user5 = "";
                guest_id1 = 0;
                guest_id2 = 0;
                guest_id3 = 0;
                guest_id4 = 0;
                guest_id5 = 0;
                notes = "";

            } else {

                out.println(SystemUtils.HeadTitle("Procedure Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Procedure Error</H3>");
                out.println("<BR><BR>You cannot cancel an event entry that you are not part of.");
                out.println("<BR><BR>You must be a member currently on the team in order to cancel it.");
                out.println("<font size=\"2\"><br><br>");
                if (ext_login == true) {     // if came from external login (no frames)
                    out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
                    out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                } else if (!index.equals("") && index.equals("0")) {
                    out.println("<form action=\"Member_events2\" method=\"post\">");
                } else {
                    out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                }
                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");
                out.println("</center></body></html>");
                out.close();
                return;
            }


            //
            //  if this team was not on the wait list, then set an idicator so we will process the wait list below.
            //
            if (wait == 0) {

                checkWait = 1;
            }
            wait = 0;          // init wait in cancelled entry

            emailCan = 1;      // send email notification for Cancel Request
            sendemail = 1;

        } else { // this code block spans all the way down past verification to where we update the db

            //
            //  Normal reservation request
            //
            //
            //   Get the guest names specified for this club
            //
            try {

                parm.club = club;                 // set club name
                parm.course = course;             // and course name

                getClub.getParms(con, parm, sess_activity_id);      // get the club parms

            } catch (Exception ignore) {
            }

            //
            //   Remove any guest types that are null or not allowed for members - for tests below
            //
            if (sess_activity_id == 0 && club.equals("wellesley")) {

                parm.guest[0] = "Tourney Guest";       // only 1 guest type

                for (i = 1; i < parm.MAX_Guests; i++) {

                    parm.guest[i] = "$@#!^&*";          // make so it won't match player field
                }

            } else {

                for (i = 0; i < parm.MAX_Guests; i++) {

                    if ((parm.guest[i].equals("")) || (parm.gOpt[i] != 0)) {

                        parm.guest[i] = "$@#!^&*";      // make so it won't match player field
                    }
                } // end of while loop
            }

            //
            //  init string arrays
            //
            for (i = 0; i < 5; i++) {

                gstA[i] = "";
                hdcp[i] = "";
            }

            //
            // prep our arrays
            //
            player[0] = player1;
            player[1] = player2;
            player[2] = player3;
            player[3] = player4;
            player[4] = player5;
            hdcp[0] = ghin1;
            hdcp[1] = ghin2;
            hdcp[2] = ghin3;
            hdcp[3] = ghin4;
            hdcp[4] = ghin5;
            gender[0] = gender1;
            gender[1] = gender2;
            gender[2] = gender3;
            gender[3] = gender4;
            gender[4] = gender5;
            homeclub[0] = homeclub1;
            homeclub[1] = homeclub2;
            homeclub[2] = homeclub3;
            homeclub[3] = homeclub4;
            homeclub[4] = homeclub5;
            phone[0] = phone1;
            phone[1] = phone2;
            phone[2] = phone3;
            phone[3] = phone4;
            phone[4] = phone5;
            address[0] = address1;
            address[1] = address2;
            address[2] = address3;
            address[3] = address4;
            address[4] = address5;
            email[0] = email1;
            email[1] = email2;
            email[2] = email3;
            email[3] = email4;
            email[4] = email5;
            shirt[0] = shirt1;
            shirt[1] = shirt2;
            shirt[2] = shirt3;
            shirt[3] = shirt4;
            shirt[4] = shirt5;
            shoe[0] = shoe1;
            shoe[1] = shoe2;
            shoe[2] = shoe3;
            shoe[3] = shoe4;
            shoe[4] = shoe5;
            otherA1[0] = other1A1;
            otherA1[1] = other2A1;
            otherA1[2] = other3A1;
            otherA1[3] = other4A1;
            otherA1[4] = other5A1;
            otherA2[0] = other1A2;
            otherA2[1] = other2A2;
            otherA2[2] = other3A2;
            otherA2[3] = other4A2;
            otherA2[4] = other5A2;
            otherA3[0] = other1A3;
            otherA3[1] = other2A3;
            otherA3[2] = other3A3;
            otherA3[3] = other4A3;
            otherA3[4] = other5A3;
            guest_idA[0] = guest_id1;
            guest_idA[1] = guest_id2;
            guest_idA[2] = guest_id3;
            guest_idA[3] = guest_id4;
            guest_idA[4] = guest_id5;
            oldUserA[0] = oldUser1;
            oldUserA[1] = oldUser2;
            oldUserA[2] = oldUser3;
            oldUserA[3] = oldUser4;
            oldUserA[4] = oldUser5;


            // remove any nulls
            for (i = 0; i < 5; i++) {

                if (player[i] == null) {
                    player[i] = "";
                }
                if (hdcp[i] == null) {
                    hdcp[i] = "";
                }
                if (gender[i] == null) {
                    gender[i] = "";
                }
                if (homeclub[i] == null) {
                    homeclub[i] = "";
                }
                if (phone[i] == null) {
                    phone[i] = "";
                }
                if (address[i] == null) {
                    address[i] = "";
                }
                if (email[i] == null) {
                    email[i] = "";
                }
                if (shirt[i] == null) {
                    shirt[i] = "";
                }
                if (shoe[i] == null) {
                    shoe[i] = "";
                }
                if (otherA1[i] == null) {
                    otherA1[i] = "";
                }
                if (otherA2[i] == null) {
                    otherA2[i] = "";
                }
                if (otherA3[i] == null) {
                    otherA3[i] = "";
                }
                if (guest_idA[i] < 0) {
                    guest_idA[i] = 0;
                }
            }

            /*
            g1 = "";
            g2 = "";
            g3 = "";
            g4 = "";
            g5 = "";
             */

            //
            //  Check if any player names are guest names
            //
            if (!player1.equals("")) {

                i = 0;
                loop1:
                while (i < parm.MAX_Guests) {

                    if (player1.startsWith(parm.guest[i])) {

                        g1 = parm.guest[i];      // indicate player is a guest name and save name
                        gstA[0] = player1;       // save guest value
                        guests++;                // increment number of guests this slot

                        if (parm.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || guest_id1 != 0 || !player1.equals(parm.guest[i] + " TBA")) {

                                if (guest_id1 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(player1, guest_id1, parm.guest[i], club, con);
                                }

                                if (invalidGuest) {
                                    gplayer = player1;    // indicate error
                                }
                            }
                        }

                        break loop1;
                    }
                    i++;
                }         // end of while loop
            }
            if (!player2.equals("")) {

                i = 0;
                loop2:
                while (i < parm.MAX_Guests) {

                    if (player2.startsWith(parm.guest[i])) {

                        g2 = parm.guest[i];      // indicate player is a guest name and save name
                        gstA[1] = player2;       // save guest value
                        guests++;                // increment number of guests this slot

                        if (parm.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || guest_id2 != 0 || !player2.equals(parm.guest[i] + " TBA")) {

                                if (guest_id2 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(player2, guest_id2, parm.guest[i], club, con);
                                }

                                if (invalidGuest) {
                                    gplayer = player2;    // indicate error
                                }
                            }
                        }

                        break loop2;
                    }
                    i++;
                }         // end of while loop
            }
            if (!player3.equals("")) {

                i = 0;
                loop3:
                while (i < parm.MAX_Guests) {

                    if (player3.startsWith(parm.guest[i])) {

                        g3 = parm.guest[i];      // indicate player is a guest name and save name
                        gstA[2] = player3;       // save guest value
                        guests++;                // increment number of guests this slot

                        if (parm.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || guest_id3 != 0 || !player3.equals(parm.guest[i] + " TBA")) {

                                if (guest_id3 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(player3, guest_id3, parm.guest[i], club, con);
                                }

                                if (invalidGuest) {
                                    gplayer = player3;    // indicate error
                                }
                            }
                        }

                        break loop3;
                    }
                    i++;
                }         // end of while loop
            }
            if (!player4.equals("")) {

                i = 0;
                loop4:
                while (i < parm.MAX_Guests) {

                    if (player4.startsWith(parm.guest[i])) {

                        g4 = parm.guest[i];      // indicate player is a guest name and save name
                        gstA[3] = player4;       // save guest value
                        guests++;                // increment number of guests this slot

                        if (parm.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || guest_id4 != 0 || !player4.equals(parm.guest[i] + " TBA")) {

                                if (guest_id4 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(player4, guest_id4, parm.guest[i], club, con);
                                }

                                if (invalidGuest) {
                                    gplayer = player4;    // indicate error
                                }
                            }
                        }

                        break loop4;
                    }
                    i++;
                }         // end of while loop
            }
            if (!player5.equals("")) {

                i = 0;
                loop5:
                while (i < parm.MAX_Guests) {

                    if (player5.startsWith(parm.guest[i])) {

                        g5 = parm.guest[i];      // indicate player is a guest name and save name
                        gstA[4] = player5;       // save guest value
                        guests++;                // increment number of guests this slot

                        if (parm.gDb[i] == 1) {

                            if (!guestdbTbaAllowed || guest_id5 != 0 || !player5.equals(parm.guest[i] + " TBA")) {

                                if (guest_id5 == 0) {
                                    invalidGuest = true;
                                } else {
                                    invalidGuest = verifySlot.checkTrackedGuestName(player5, guest_id5, parm.guest[i], club, con);
                                }

                                if (invalidGuest) {
                                    gplayer = player5;    // indicate error
                                }
                            }
                        }

                        break loop5;
                    }
                    i++;
                }         // end of while loop
            }

            //
            //  Make sure at least 1 player contains a member
            //
            if (((player1.equals("")) || (player1.equalsIgnoreCase("x")) || (!g1.equals("")))
                    && ((player2.equals("")) || (player2.equalsIgnoreCase("x")) || (!g2.equals("")))
                    && ((player3.equals("")) || (player3.equalsIgnoreCase("x")) || (!g3.equals("")))
                    && ((player4.equals("")) || (player4.equalsIgnoreCase("x")) || (!g4.equals("")))
                    && ((player5.equals("")) || (player5.equalsIgnoreCase("x")) || (!g5.equals("")))) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR>Required field has not been completed or is invalid.");
                out.println("<BR><BR>At least one player field must contain a member name.");
                out.println("<BR>If you want to cancel the event entry, use the 'Cancel Entry' button under the player fields.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                out.println("</center></body></html>");
                out.close();
                return;
            }

            //
            //  Check the number of X's against max specified by proshop
            //
            xcount = 0;

            if (player1.equalsIgnoreCase("x")) {
                xcount++;
            }
            if (player2.equalsIgnoreCase("x")) {
                xcount++;
            }
            if (player3.equalsIgnoreCase("x")) {
                xcount++;
            }
            if (player4.equalsIgnoreCase("x")) {
                xcount++;
            }
            if (player5.equalsIgnoreCase("x")) {
                xcount++;
            }

            if (xcount > x) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + x + ").");
                out.println("<BR>Please try again.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                out.println("</font></center></body></html>");
                out.close();
                return;
            }

            //
            //  Make sure this signup has enough players as specified in the event conf
            //
            int players = 0;

            if (!player1.equals("")) {
                players++;
            }
            if (!player2.equals("")) {
                players++;
            }
            if (!player3.equals("")) {
                players++;
            }
            if (!player4.equals("")) {
                players++;
            }
            if (!player5.equals("")) {
                players++;
            }

            if (players < minsize) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR>The number of players (" + players + ") does not meet the required amount (" + minsize + ").");
                // if they haven't used their alloted # of X's, tell them they can use up to xcount amount of them
                if (xcount < x) {
                    out.println("<BR><BR>You're allowed to use " + xcount + " X's to hold player positions, but you must intend on filling them.");
                }
                out.println("<BR>Please try again.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                out.println("</font></center></body></html>");
                out.close();
                return;
            }

            /*
            if (club.equals("olyclub") && players > 1) {
            
            // If more than 1 player included, the signup needs to have been originated by a proshop user
            if (!verifyCustom.checkOlyClubIsProshopEventSignup(id, con)) {
            
            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Only event signups created by the golf shop staff may include more than one player.");
            out.println("<BR><BR>Please return and remove any additional players.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            
            returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, 
            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
            notes, hides, index, course, name, rev, id, event_id, ext_login, out);
            
            out.println("</font></center></body></html>");
            out.close();
            return;
            }
            }
             */

            //
            //  Make sure a C/W was specified for all players
            //
            if (sess_activity_id == 0 && season == 0
                    && (((!player1.equals("")) && (!player1.equalsIgnoreCase("x")) && (p1cw.equals("")))
                    || ((!player2.equals("")) && (!player2.equalsIgnoreCase("x")) && (p2cw.equals("")))
                    || ((!player3.equals("")) && (!player3.equalsIgnoreCase("x")) && (p3cw.equals("")))
                    || ((!player4.equals("")) && (!player4.equalsIgnoreCase("x")) && (p4cw.equals("")))
                    || ((!player5.equals("")) && (!player5.equalsIgnoreCase("x")) && (p5cw.equals(""))))) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR>Required field has not been completed or is invalid.");
                out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                out.println("</font></center></body></html>");
                out.close();
                return;
            }

            //
            //  See if an invalid tracked guest entry was present
            //
            if (!gplayer.equals("")) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Data Entry Error</H3>");
                out.println("<BR><BR><b>" + gplayer + "</b> appears to have been manually entered or "
                        + "<br>modified after selecting a different guest from the Guest Selection window.");
                out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
                out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
                out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                out.println("</font></center></body></html>");
                out.close();
                return;
            }

            //
            //  Make sure there are no duplicate names
            //
            if ((!player1.equals("")) && (!player1.equalsIgnoreCase("x")) && (g1.equals(""))) {

                if ((player1.equalsIgnoreCase(player2)) || (player1.equalsIgnoreCase(player3)) || (player1.equalsIgnoreCase(player4)) || (player1.equalsIgnoreCase(player5))) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR><b>" + player1 + "</b> was specified more than once.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }

            if ((!player2.equals("")) && (!player2.equalsIgnoreCase("x")) && (g2.equals(""))) {

                if ((player2.equalsIgnoreCase(player3)) || (player2.equalsIgnoreCase(player4)) || (player2.equalsIgnoreCase(player5))) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR><b>" + player2 + "</b> was specified more than once.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }

            if ((!player3.equals("")) && (!player3.equalsIgnoreCase("x")) && (g3.equals(""))) {

                if ((player3.equalsIgnoreCase(player4)) || (player3.equalsIgnoreCase(player5))) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR><b>" + player3 + "</b> was specified more than once.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }

            if ((!player4.equals("")) && (!player4.equalsIgnoreCase("x")) && (g4.equals(""))) {

                if (player4.equalsIgnoreCase(player5)) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR><b>" + player4 + "</b> was specified more than once.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }

            //
            //  Parse the names to separate first, last & mi
            //  (Member does not verify single tokens - check for x or guest) !!!!!!!!!!!
            //
            if ((!player1.equals("")) && (g1.equals(""))) {                  // specified but not guest

                StringTokenizer tok = new StringTokenizer(player1);     // space is the default token

                if (tok.countTokens() > 3) {          // too many name fields

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if (tok.countTokens() == 2) {         // first name, last name

                    fname1 = tok.nextToken();
                    lname1 = tok.nextToken();
                }

                if (tok.countTokens() == 3) {         // first name, mi, last name

                    fname1 = tok.nextToken();
                    mi1 = tok.nextToken();
                    lname1 = tok.nextToken();
                }

                if (tok.countTokens() == 1) {         // X not valid for player1

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Data Entry Error</H3>");
                    out.println("<BR><BR><b>" + player1 + "</b> is not allowed in the first player position.");
                    out.println("<BR>Player 1 must be a valid member name.");
                    out.println("<BR><BR>Please correct this and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }

            if ((!player2.equals("")) && (g2.equals(""))) {                  // specified but not guest

                StringTokenizer tok = new StringTokenizer(player2);     // space is the default token

                if (tok.countTokens() > 3) {          // too many name fields

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if ((tok.countTokens() == 1) && (!player2.equalsIgnoreCase("X"))) {    // if not X

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if (tok.countTokens() == 2) {         // first name, last name

                    fname2 = tok.nextToken();
                    lname2 = tok.nextToken();
                }

                if (tok.countTokens() == 3) {         // first name, mi, last name

                    fname2 = tok.nextToken();
                    mi2 = tok.nextToken();
                    lname2 = tok.nextToken();
                }
            }

            if ((!player3.equals("")) && (g3.equals(""))) {                  // specified but not guest

                StringTokenizer tok = new StringTokenizer(player3);     // space is the default token

                if (tok.countTokens() > 3) {          // too many name fields

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if ((tok.countTokens() == 1) && (!player3.equalsIgnoreCase("X"))) {    // if not X

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if (tok.countTokens() == 2) {         // first name, last name

                    fname3 = tok.nextToken();
                    lname3 = tok.nextToken();
                }

                if (tok.countTokens() == 3) {         // first name, mi, last name

                    fname3 = tok.nextToken();
                    mi3 = tok.nextToken();
                    lname3 = tok.nextToken();
                }
            }

            if ((!player4.equals("")) && (g4.equals(""))) {                  // specified but not guest

                StringTokenizer tok = new StringTokenizer(player4);     // space is the default token

                if (tok.countTokens() > 3) {          // too many name fields

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if ((tok.countTokens() == 1) && (!player4.equalsIgnoreCase("X"))) {    // if not X

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if (tok.countTokens() == 2) {         // first name, last name

                    fname4 = tok.nextToken();
                    lname4 = tok.nextToken();
                }

                if (tok.countTokens() == 3) {         // first name, mi, last name

                    fname4 = tok.nextToken();
                    mi4 = tok.nextToken();
                    lname4 = tok.nextToken();
                }
            }

            if ((!player5.equals("")) && (g5.equals(""))) {                  // specified but not guest

                StringTokenizer tok = new StringTokenizer(player5);     // space is the default token

                if (tok.countTokens() > 3) {          // too many name fields

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if ((tok.countTokens() == 1) && (!player5.equalsIgnoreCase("X"))) {    // if not X

                    invData(out, player1, player2, player3, player4, player5);                        // reject
                    return;
                }

                if (tok.countTokens() == 2) {         // first name, last name

                    fname5 = tok.nextToken();
                    lname5 = tok.nextToken();
                }

                if (tok.countTokens() == 3) {         // first name, mi, last name

                    fname5 = tok.nextToken();
                    mi5 = tok.nextToken();
                    lname5 = tok.nextToken();
                }
            }

            members = 0;     // init number of members in res request

            //
            //  Get the usernames & hndcp's for players if matching name found
            //
            try {

                PreparedStatement pstmt1 = con.prepareStatement(
                        "SELECT username, g_hancap "
                        + "FROM member2b "
                        + "WHERE name_last = ? AND name_first = ? AND name_mi = ? AND inact = 0");

                if ((!fname1.equals("")) && (!lname1.equals(""))) {

                    pstmt1.clearParameters();
                    pstmt1.setString(1, lname1);
                    pstmt1.setString(2, fname1);
                    pstmt1.setString(3, mi1);
                    rs = pstmt1.executeQuery();

                    if (rs.next()) {

                        user1 = rs.getString(1);
                        hndcp1 = rs.getFloat(2);

                        members++;        // increment # of members in request

                    } else {

                        invData(out, player1, player2, player3, player4, player5);                        // reject
                        return;
                    }
                }

                if ((!fname2.equals("")) && (!lname2.equals(""))) {

                    pstmt1.clearParameters();        // clear the parms
                    pstmt1.setString(1, lname2);
                    pstmt1.setString(2, fname2);
                    pstmt1.setString(3, mi2);
                    rs = pstmt1.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        user2 = rs.getString(1);
                        hndcp2 = rs.getFloat(2);

                        members++;        // increment # of members in request

                    } else if (club.equals("tcclub")) {
                        
                            // enforce a default guest type for tcclub - this is repeated for each position (case #1458)
                            if (!player2.startsWith("Tournament")) {
                                    player2 = "Tournament " + player2;                // append 'Guest' to player name
                            }
                            g2 = "Tournament";                                // save name of guest type
                            gstA[0] = player2;                           // save full player name
                            guests++;                                    // increment number of guests in this slot
                            
                    } else {

                        invData(out, player1, player2, player3, player4, player5);                        // reject
                        return;
                    }
                }

                if ((!fname3.equals("")) && (!lname3.equals(""))) {

                    pstmt1.clearParameters();        // clear the parms
                    pstmt1.setString(1, lname3);
                    pstmt1.setString(2, fname3);
                    pstmt1.setString(3, mi3);
                    rs = pstmt1.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        user3 = rs.getString(1);
                        hndcp3 = rs.getFloat(2);

                        members++;        // increment # of members in request

                    } else if (club.equals("tcclub")) {
                        
                            // enforce a default guest type for tcclub - this is repeated for each position (case #1458)
                            if (!player3.startsWith("Tournament")) {
                                player3 = "Tournament " + player3;                // append 'Guest' to player name
                            }
                            g3 = "Tournament";                                // save name of guest type
                            gstA[0] = player3;                           // save full player name
                            guests++;                                    // increment number of guests in this slot
                            
                    } else {

                        invData(out, player1, player2, player3, player4, player5);                        // reject
                        return;
                    }
                }

                if ((!fname4.equals("")) && (!lname4.equals(""))) {

                    pstmt1.clearParameters();        // clear the parms
                    pstmt1.setString(1, lname4);
                    pstmt1.setString(2, fname4);
                    pstmt1.setString(3, mi4);
                    rs = pstmt1.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        user4 = rs.getString(1);
                        hndcp4 = rs.getFloat(2);

                        members++;        // increment # of members in request

                    } else if (club.equals("tcclub")) {
                        
                            // enforce a default guest type for tcclub - this is repeated for each position (case #1458)
                            if (!player4.startsWith("Tournament")) {
                                player4 = "Tournament " + player4;                // append 'Guest' to player name
                            }
                            g4 = "Tournament";                                // save name of guest type
                            gstA[0] = player4;                           // save full player name
                            guests++;                                    // increment number of guests in this slot
                            
                    } else {

                        invData(out, player1, player2, player3, player4, player5);                        // reject
                        return;
                    }
                }

                if ((!fname5.equals("")) && (!lname5.equals(""))) {

                    pstmt1.clearParameters();        // clear the parms
                    pstmt1.setString(1, lname5);
                    pstmt1.setString(2, fname5);
                    pstmt1.setString(3, mi5);
                    rs = pstmt1.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        user5 = rs.getString(1);
                        hndcp5 = rs.getFloat(2);

                        members++;        // increment # of members in request

                    } else if (club.equals("tcclub")) {
                        
                            // enforce a default guest type for tcclub - this is repeated for each position (case #1458)
                            if (!player5.startsWith("Tournament")) {
                                player5 = "Tournament " + player5;                // append 'Guest' to player name
                            }
                            g5 = "Tournament";                                // save name of guest type
                            gstA[0] = player5;                           // save full player name
                            guests++;                                    // increment number of guests in this slot
                            
                    } else {

                        invData(out, player1, player2, player3, player4, player5);                        // reject
                        return;
                    }
                }

                pstmt1.close();

            } catch (Exception e1) {

                dbError(out, e1);                        // reject
                return;
            }

            //
            //  Save the members' usernames for guest association
            //
            memA[0] = user1;
            memA[1] = user2;
            memA[2] = user3;
            memA[3] = user4;
            memA[4] = user5;

            // Allocate parm blocks for parmCourse and parmSlot and populated as needed for ProOnly Tmodes check      
            parmSlot parms = new parmSlot();          // allocate a slot parm block
            parmCourse parmc = new parmCourse();          // allocate a course parm block

            try {
                if (course.equals("-ALL-") || course.equals("")) {
                    getParms.getCourseTrans(con, parmc);
                } else {
                    getParms.getTmodes(con, parmc, course);
                }

                parms.activity_id = sess_activity_id;

                parms.player1 = player1;
                parms.player2 = player2;
                parms.player3 = player3;
                parms.player4 = player4;
                parms.player5 = player5;
                parms.user1 = user1;
                parms.user2 = user2;
                parms.user3 = user3;
                parms.user4 = user4;
                parms.user5 = user5;
                parms.p1cw = p1cw;
                parms.p2cw = p2cw;
                parms.p3cw = p3cw;
                parms.p4cw = p4cw;
                parms.p5cw = p5cw;
                parms.oldPlayer1 = oldPlayer1;
                parms.oldPlayer2 = oldPlayer2;
                parms.oldPlayer3 = oldPlayer3;
                parms.oldPlayer4 = oldPlayer4;
                parms.oldPlayer5 = oldPlayer5;
                parms.oldp1cw = oldp1cw;
                parms.oldp2cw = oldp2cw;
                parms.oldp3cw = oldp3cw;
                parms.oldp4cw = oldp4cw;
                parms.oldp5cw = oldp5cw;
                parms.guest_id1 = guest_id1;
                parms.guest_id2 = guest_id2;
                parms.guest_id3 = guest_id3;
                parms.guest_id4 = guest_id4;
                parms.guest_id5 = guest_id5;
                parms.oldguest_id1 = oldguest_id1;
                parms.oldguest_id2 = oldguest_id2;
                parms.oldguest_id3 = oldguest_id3;
                parms.oldguest_id4 = oldguest_id4;
                parms.oldguest_id5 = oldguest_id5;

                //
                //  No players are using Pro-Only transportation modes without authorization *DO NOT CHECK FOR SEASON LONG EVENTS!!*
                //
                if (sess_activity_id == 0 && season == 0 && parmc.hasProOnlyTmodes && !verifySlot.checkProOnlyMOT(parms, parmc, con)) {

                    out.println(SystemUtils.HeadTitle("Access Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Access Error</H3>");
                    out.println("<BR><BR><b>'" + parms.player + "'</b> is not authorized to use that mode of transportation.");
                    out.println("<BR><BR>Please select another mode of transportation.");
                    out.println("<BR>Contact your club if you require assistance with restricted modes of transportation.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            } catch (Exception exc) {
                dbError(out, exc);                        // reject
                return;
            }


            //
            // **************************************
            //  Check for Restrictions in the Event - check all members
            // **************************************
            //
            try {

                String restPlayer = verifySlot.checkEventRests(user1, user2, user3, user4, user5, event_id, con);

                if (!restPlayer.equals("")) {        // if member (username) restricted from this event

                    if (restPlayer.equals(user1)) {
                        restPlayer = player1;             // get player's name
                    } else {
                        if (restPlayer.equals(user2)) {
                            restPlayer = player2;
                        } else {
                            if (restPlayer.equals(user3)) {
                                restPlayer = player3;
                            } else {
                                if (restPlayer.equals(user4)) {
                                    restPlayer = player4;
                                } else {
                                    restPlayer = player5;
                                }
                            }
                        }
                    }

                    out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                    out.println("<BODY bgcolor=\"#CCCCAA\">");
                    out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><H3>Member Restricted For Event</H3><BR>");
                    out.println("<BR><BR>Sorry, member " + restPlayer + " is not allowed to participate in this event.");
                    out.println("<BR><BR>Please remove this member and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }

            } catch (Exception e1) {

                dbError(out, e1); // reject
                return;
            }

            //
            // **************************************
            //  Check for max # of guests exceeded (per member)
            // **************************************
            //
            if (guests != 0) {      // if any guests were included

                try {

                    PreparedStatement pstmtg = con.prepareStatement(
                            "SELECT guests FROM events2b "
                            + "WHERE name = ? AND courseName = ? AND activity_id = ?");

                    pstmtg.clearParameters();
                    pstmtg.setString(1, name);
                    pstmtg.setString(2, course);
                    pstmtg.setInt(3, sess_activity_id);
                    rs = pstmtg.executeQuery();

                    if (rs.next()) {

                        eguests = rs.getInt("guests");
                    }
                    pstmtg.close();

                    // if num of guests req'd (guests) > num allowed (eguests) per member
                    //
                    //       to get here guests is > 0
                    //       eguests is 0 - 3
                    //       members is 0 - 5
                    //
                    guestError = false;         // init error flag

                    if ((eguests == 0) || (members == 0)) {      // no guests allowed or no members

                        guestError = true;         // set error flag
                    }

                    if (members == 1) {

                        if (guests > eguests) {     // if 1 member and more guests than allowed

                            guestError = true;         // set error flag
                        }
                    }
                    if (members == 2) {

                        if (eguests == 1) {

                            if (guests > 2) {             // if 1 allowed and more than 1 each

                                guestError = true;         // set error flag
                            }
                        }
                    }

                    if (guestError == true) {      // if # of guests exceeded

                        out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
                        out.println("<BODY bgcolor=\"#CCCCAA\">");
                        out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
                        out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
                        out.println("event you are requesting is " + eguests + " per member.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");

                        returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                                guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                        out.println("</font></center></body></html>");
                        out.close();
                        return;
                    }

                } catch (Exception e5) {

                    dbError(out, e5);
                    return;
                }

                //
                // If guest names are required for this event OR if Hazeltine National
                // then make sure member entered a guest's name after each guest type
                //
                if (req_guestname == 1 || (sess_activity_id == 0 && club.equals("hazeltine"))) {

                    boolean error = false;

                    int count2 = 0;
                    count = 0;

                    String gstname = "";
                    String[] gName = new String[5];    // array to hold the Guest Names specified
                    String[] gType = new String[5];    // array to hold the Guest Types specified

                    //
                    //  init string arrays
                    //
                    for (i = 0; i < 5; i++) {

                        gName[i] = "";
                        gType[i] = "";
                    }

                    i = 0; // reset

                    //
                    //  Determine which player positions need to be tested.
                    //  Do not check players that have already been checked.
                    //
                    if (!player1.equals(oldPlayer1)) {     // if player name is new or has changed

                        if (!g1.equals("")) {

                            gType[i] = g1;        // get guest type
                            gName[i] = player1;   // get name entered
                            i++;
                        }
                    }
                    if (!player2.equals(oldPlayer2)) {     // if player name is new or has changed

                        if (!g2.equals("")) {

                            gType[i] = g2;        // get guest type
                            gName[i] = player2;   // get name entered
                            i++;
                        }
                    }
                    if (!player3.equals(oldPlayer3)) {     // if player name is new or has changed

                        if (!g3.equals("")) {

                            gType[i] = g3;        // get guest type
                            gName[i] = player3;   // get name entered
                            i++;
                        }
                    }
                    if (!player4.equals(oldPlayer4)) {     // if player name is new or has changed

                        if (!g4.equals("")) {

                            gType[i] = g4;        // get guest type
                            gName[i] = player4;   // get name entered
                            i++;
                        }
                    }
                    if (!player5.equals(oldPlayer5)) {     // if player name is new or has changed

                        if (!g5.equals("")) {

                            gType[i] = g5;        // get guest type
                            gName[i] = player5;   // get name entered
                            i++;
                        }
                    }

                    //
                    //  Verify that a name was provided
                    //
                    i = 0;
                    loopg1:
                    while (i < 5) {

                        if (!gType[i].equals("")) {          // if guest type specified

                            gstname = gName[i];                 // get player name specified

                            if (gstname.equals(gType[i])) {   // if matches then can't be name

                                error = true;
                                break loopg1;
                            }
                            //
                            //  Use tokens to determine the number of words in each string.
                            //  There must be at least 2 extra words in the player name.
                            //
                            StringTokenizer tok = new StringTokenizer(gstname, " ");          // delimiter is a space
                            count = tok.countTokens();                                       // number of words in player name

                            StringTokenizer tok2 = new StringTokenizer(gType[i], " ");     // guest type
                            count2 = tok2.countTokens();                                     // number of words in guest type

                            if (count > count2) {

                                count = count - count2;          // how many more words in player name than guest type

                                if (count < 2) {                 // must be at least 2

                                    error = true;
                                    break loopg1;
                                }

                            } else {           // error

                                error = true;
                                break loopg1;
                            }

                        } else {        // done when no guest type
                            break loopg1;
                        }
                        i++;
                    }                  // end of while

                    if (error == true) {

                        out.println(SystemUtils.HeadTitle("Guest Name Not Provided - Reject"));
                        out.println("<BODY bgcolor=\"CCCCAA\">");
                        out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Invalid Guest Request</H3><BR>");
                        out.println("<BR><BR>Sorry, you must provide the full name of your guest(s).");
                        out.println("<BR>Please enter a space followed by the guest's name immediately after the guest type");
                        out.println("<BR>in the player field.  Click your mouse in the player field, move the cursor");
                        out.println("<BR>to the end of the guest type, hit the space bar and then type the full name.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");

                        returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                                guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                        out.println("</font></center></body></html>");
                        out.close();
                        return;
                    }

                } // end if hazeltine custom

            } // end of if guests

            //
            //***********************************************************************************************
            //
            //    Now check if any of the players are already scheduled for this event
            //
            //***********************************************************************************************
            //
            try {

                hit = false;

                if ((!player1.equals("")) && (!player1.equalsIgnoreCase("x")) && (g1.equals(""))) {

                    PreparedStatement pstmt21 = con.prepareStatement(
                            "SELECT id FROM evntsup2b "
                            + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND "
                            + "name = ? AND courseName = ? AND id != ? AND inactive = 0");

                    pstmt21.clearParameters();        // clear the parms and check player 1
                    pstmt21.setString(1, player1);
                    pstmt21.setString(2, player1);
                    pstmt21.setString(3, player1);
                    pstmt21.setString(4, player1);
                    pstmt21.setString(5, player1);
                    pstmt21.setString(6, name);
                    pstmt21.setString(7, course);
                    pstmt21.setInt(8, id);
                    rs = pstmt21.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        hit = true;                    // player already scheduled for this event
                        playerName = player1;          // get player for message
                    }
                    pstmt21.close();
                }

                if (!hit && !player2.equals("") && !player2.equalsIgnoreCase("x") && g2.equals("")) {

                    PreparedStatement pstmt21 = con.prepareStatement(
                            "SELECT id FROM evntsup2b "
                            + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND "
                            + "name = ? AND courseName = ? AND id != ? AND inactive = 0");

                    pstmt21.clearParameters();        // clear the parms and check player 2
                    pstmt21.setString(1, player2);
                    pstmt21.setString(2, player2);
                    pstmt21.setString(3, player2);
                    pstmt21.setString(4, player2);
                    pstmt21.setString(5, player2);
                    pstmt21.setString(6, name);
                    pstmt21.setString(7, course);
                    pstmt21.setInt(8, id);
                    rs = pstmt21.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        hit = true;                    // player already scheduled for this event
                        playerName = player2;          // get player for message
                    }
                    pstmt21.close();
                }

                if (!hit && !player3.equals("") && !player3.equalsIgnoreCase("x") && g3.equals("")) {

                    PreparedStatement pstmt21 = con.prepareStatement(
                            "SELECT id FROM evntsup2b "
                            + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND "
                            + "name = ? AND courseName = ? AND id != ? AND inactive = 0");

                    pstmt21.clearParameters();        // clear the parms and check player 3
                    pstmt21.setString(1, player3);
                    pstmt21.setString(2, player3);
                    pstmt21.setString(3, player3);
                    pstmt21.setString(4, player3);
                    pstmt21.setString(5, player3);
                    pstmt21.setString(6, name);
                    pstmt21.setString(7, course);
                    pstmt21.setInt(8, id);
                    rs = pstmt21.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        hit = true;                    // player already scheduled for this event
                        playerName = player3;          // get player for message
                    }
                    pstmt21.close();
                }

                if (!hit && !player4.equals("") && !player4.equalsIgnoreCase("x") && g4.equals("")) {

                    PreparedStatement pstmt21 = con.prepareStatement(
                            "SELECT id FROM evntsup2b "
                            + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND "
                            + "name = ? AND courseName = ? AND id != ? AND inactive = 0");

                    pstmt21.clearParameters();        // clear the parms and check player 4
                    pstmt21.setString(1, player4);
                    pstmt21.setString(2, player4);
                    pstmt21.setString(3, player4);
                    pstmt21.setString(4, player4);
                    pstmt21.setString(5, player4);
                    pstmt21.setString(6, name);
                    pstmt21.setString(7, course);
                    pstmt21.setInt(8, id);
                    rs = pstmt21.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        hit = true;                    // player already scheduled for this event
                        playerName = player4;          // get player for message
                    }
                    pstmt21.close();
                }

                if (!hit && !player5.equals("") && !player5.equalsIgnoreCase("x") && g5.equals("")) {

                    PreparedStatement pstmt21 = con.prepareStatement(
                            "SELECT id FROM evntsup2b "
                            + "WHERE (player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?) AND "
                            + "name = ? AND courseName = ? AND id != ? AND inactive = 0");

                    pstmt21.clearParameters();        // clear the parms and check player 5
                    pstmt21.setString(1, player5);
                    pstmt21.setString(2, player5);
                    pstmt21.setString(3, player5);
                    pstmt21.setString(4, player5);
                    pstmt21.setString(5, player5);
                    pstmt21.setString(6, name);
                    pstmt21.setString(7, course);
                    pstmt21.setInt(8, id);
                    rs = pstmt21.executeQuery();      // execute the prepared stmt

                    while (rs.next()) {

                        hit = true;                    // player already scheduled for this event
                        playerName = player5;          // get player for message
                    }
                    pstmt21.close();
                }
            } catch (Exception e21) {

                dbError(out, e21);
                return;
            }

            if (hit == true) {          // if we hit on a duplicate res

                out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                out.println("<BODY bgcolor=\"#CCCCAA\">");
                out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Member Already Scheduled for Event</H3><BR>");
                out.println("<BR>Sorry, <b>" + playerName + "</b> is already scheduled to play in this event.<br><br>");
                out.println("Please remove this player and try again.<br>");
                out.println("Contact the proshop if you have any questions.<br>");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                out.println("</font></center></body></html>");
                out.close();
                return;
            }



            //
            // **************************************
            //  Check for any Customs
            // **************************************
            //
            if (sess_activity_id == 0 && club.equals("hazeltine") && name.equals("Mens Invitational")) {  // if Hazeltine & Invitational, check member sub-type

                String restPlayer = verifyCustom.checkHazeltineInvite(user1, user2, user3, user4, user5, con);

                if (!restPlayer.equals("")) {        // if member (username) restricted from this event

                    if (restPlayer.equals(user1)) {
                        restPlayer = player1;             // get player's name
                    } else {
                        if (restPlayer.equals(user2)) {
                            restPlayer = player2;
                        } else {
                            if (restPlayer.equals(user3)) {
                                restPlayer = player3;
                            } else {
                                if (restPlayer.equals(user4)) {
                                    restPlayer = player4;
                                } else {
                                    restPlayer = player5;
                                }
                            }
                        }
                    }

                    out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                    out.println("<BODY bgcolor=\"#CCCCAA\">");
                    out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><H3>Member Restricted For Event</H3><BR>");
                    out.println("<BR><BR>Sorry, member " + restPlayer + " is not allowed to participate in this event.");
                    out.println("<BR><BR>Please remove this member and try again.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }            // end of IF hazeltine

            // For Merion GC, members are limited on how many "Stag Day" or "Member/Guest" events they can join at once, based on the current date.
            if (club.equals("merion")) {

                int custom_event_count_1 = 0;
                int custom_event_count_2 = 0;
                int custom_event_category_id_1 = 1;    // "Mens Stag Day Events"
                int custom_event_category_id_2 = 2;    // "Mens Member Guest Events"
                int custom_selected_event_id = Utilities.getEventIdFromName(name, con);
                
                long curr_date = Utilities.getDate(con);
                
                String err_msg = "";

                ArrayList<Integer> category_ids_1 = new ArrayList<Integer>();
                ArrayList<Integer> category_ids_2 = new ArrayList<Integer>();

                category_ids_1.add(custom_event_category_id_1);
                category_ids_2.add(custom_event_category_id_2);   
                
                for (int j = 0; j < 5; j++) {
                    
                    err_msg = "";
                    custom_event_count_1 = 0;
                    custom_event_count_2 = 0;
                    
                    // Skip over this player if they're not a member, or if they were already in this reservation.
                    if (memA[j].equals("") || oldUserA[0].equals(memA[j]) || oldUserA[1].equals(memA[j]) || oldUserA[2].equals(memA[j]) 
                            || oldUserA[3].equals(memA[j]) || oldUserA[4].equals(memA[j])) {
                        continue;
                    }

                    // See if the event is part of the first event category for "Stag Day" events
                    if (Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_1, con).equals("")) {

                        custom_event_count_1 = verifyCustom.checkEventCategoryCounts(memA[j], club, custom_event_category_id_1, con);

                        if ((curr_date >= 20140301 && curr_date < 20140401 && custom_event_count_1 >= 2)
                                || (curr_date >= 20140401 && curr_date < 20140501 && custom_event_count_1 >= 3)) {

                            if (curr_date >= 20140301 && curr_date < 20140401) {
                                err_msg = "Sorry, but <span style=\"font-weight:bold;\">" + player[j] + "</span> is already signed up for the maximum allowed Mens Stag Day events at this time (2)."
                                        + "<br><br>They will be able to sign up for an additional Mens Stag Day event on April 1st.";
                            } else if (curr_date >= 20140401 && curr_date < 20140501) {
                                err_msg = "Sorry, but <span style=\"font-weight:bold;\">" + player[j] + "</span> is already signed up for the maximum allowed Mens Stag Day events at this time (3)."
                                        + "<br><br>They will be able to sign up for all remaining Mens Stag Day events on May 1st.";
                            }
                        }
                    }

                    // See if the event is part of the first event category for "Member/Guest" events
                    if (err_msg.equals("") && Utilities.checkEventCategoryBindings(custom_selected_event_id, category_ids_2, con).equals("")) {

                        custom_event_count_2 = verifyCustom.checkEventCategoryCounts(memA[j], club, custom_event_category_id_2, con);

                        if ((curr_date >= 20140301 && curr_date < 20140501 && custom_event_count_2 >= 1)
                                || (curr_date >= 20140501 && custom_event_count_2 >= 2)) {

                            if (curr_date >= 20140301 && curr_date < 20150401) {
                                err_msg = "Sorry, but <span style=\"font-weight:bold;\">" + player[j] + "</span> is already signed up for the maximum allowed Mens Member Guest events at this time (1)."
                                        + "<br><br>They will be able to sign up for one additional Mens Member Guest event on April 1st.";
                            } else if (curr_date >= 20140501) {
                                err_msg = "Sorry, but <span style=\"font-weight:bold;\">" + player[j] + "</span> is already signed up for the maximum allowed Mens Member Guest events (2).";
                            }
                        }
                    }

                    if (!err_msg.equals("")) {

                        out.println(SystemUtils.HeadTitle("Member Signup Limit Reached"));
                        out.println("<BODY bgcolor=\"#CCCCAA\">");
                        out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Member Signup Limit Reached</H3><BR>");
                        out.println("<BR><BR>" + err_msg);
                        out.println("<BR><BR>Please remove this member and try again.");
                        out.println("<BR><BR>");
                        out.println("<font size=\"2\">");

                        returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                                guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                        out.println("</font></center></body></html>");
                        out.close();
                        return;
                    }
                }
            }


            //
            //   CC of Naples - check Associate B mships (if golf event)
            //
            if (club.equals("ccnaples") && sess_activity_id == 0) {

                //boolean error = false;

                //String returnMsg = "";

                PreparedStatement pstmt = null;
                ResultSet rs3 = null;

                parms.signup_id = id;
                parms.teecurr_id = 0;

                // Get the date of this event
                try {
                    pstmt = con.prepareStatement("SELECT date FROM events2b WHERE name = ? AND courseName = ? AND activity_id = ?");
                    pstmt.clearParameters();
                    pstmt.setString(1, name);
                    pstmt.setString(2, course);
                    pstmt.setInt(3, sess_activity_id);

                    rs3 = pstmt.executeQuery();

                    if (rs3.next()) {
                        parms.date = rs3.getInt("date");
                    }

                    pstmt.close();

                } catch (Exception exc) {
                    //error = true;
                }

                // Get the mship and mnum for all 5 potential players
                try {
                    if (!parms.user1.equals("")) {
                        pstmt = con.prepareStatement("SELECT m_ship, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user1);

                        rs3 = pstmt.executeQuery();

                        if (rs3.next()) {
                            parms.mship1 = rs3.getString("m_ship");
                            parms.mNum1 = rs3.getString("memNum");
                        }

                        pstmt.close();
                    }

                    if (!parms.user2.equals("")) {
                        pstmt = con.prepareStatement("SELECT m_ship, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user2);

                        rs3 = pstmt.executeQuery();

                        if (rs3.next()) {
                            parms.mship2 = rs3.getString("m_ship");
                            parms.mNum2 = rs3.getString("memNum");
                        }

                        pstmt.close();
                    }

                    if (!parms.user3.equals("")) {
                        pstmt = con.prepareStatement("SELECT m_ship, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user3);

                        rs3 = pstmt.executeQuery();

                        if (rs3.next()) {
                            parms.mship3 = rs3.getString("m_ship");
                            parms.mNum3 = rs3.getString("memNum");
                        }

                        pstmt.close();
                    }

                    if (!parms.user4.equals("")) {
                        pstmt = con.prepareStatement("SELECT m_ship, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user4);

                        rs3 = pstmt.executeQuery();

                        if (rs3.next()) {
                            parms.mship4 = rs3.getString("m_ship");
                            parms.mNum4 = rs3.getString("memNum");
                        }

                        pstmt.close();
                    }

                    if (!parms.user5.equals("")) {
                        pstmt = con.prepareStatement("SELECT m_ship, memNum FROM member2b WHERE username = ?");
                        pstmt.clearParameters();
                        pstmt.setString(1, user5);

                        rs3 = pstmt.executeQuery();

                        if (rs3.next()) {
                            parms.mship5 = rs3.getString("m_ship");
                            parms.mNum5 = rs3.getString("memNum");
                        }

                        pstmt.close();
                    }
                } catch (Exception exc) {
                    //error = true;
                }

                //
                //  CC of Naples - check for Associate B mships - restricted in season (case 1704)
                //
                if (verifyCustom.checkNaplesAssocBQuota(parms, con)) {    // check max rounds for Assoc B mships

                    out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                    out.println("<BODY bgcolor=\"#CCCCAA\">");
                    out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<BR><BR><H3>Member Exceeded Max Rounds</H3>"
                            + "<BR>Sorry, " + parms.player + " has exceeded his/her max allowed rounds for this month."
                            + "<BR><BR>Please remove the player or return to the tee sheet.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }        // end of CC of Naples


            //
            //  Check if user has approved of the member/guest sequence (guest association)
            //
            //  If this skip is set, then we've already been through these tests.
            //
            if (req.getParameter("skip8") == null) {

                //
                //***********************************************************************************************
                //
                //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
                //
                //***********************************************************************************************
                //
                if (guests > 0) {

                    if (members > 0) {  // if at least one member (this should always be the case!)

                        //
                        //  Determine guest owners by order
                        //
                        gi = 0;
                        memberName = "";

                        while (gi < 5) {                  // cycle thru arrays and find guests/members

                            if (!gstA[gi].equals("")) {

                                usergA[gi] = memberName;       // get last players username
                            } else {
                                usergA[gi] = "";               // init array entry
                            }
                            if (!memA[gi].equals("")) {

                                memberName = memA[gi];        // get players username
                            }
                            gi++;
                        }
                        userg1 = usergA[0];        // set usernames for guests in teecurr
                        userg2 = usergA[1];
                        userg3 = usergA[2];
                        userg4 = usergA[3];
                        userg5 = usergA[4];
                    }

                    if (members > 1 || !g1.equals("")) {  // if multiple members OR slot 1 is a guest
                        //
                        //  At least one guest and two members have been specified.
                        //  Prompt user to verify the order.
                        //
                        if (new_skin) {

                            // Pull the arryas into local variable, incase we want to use them later
                            String[] player_a = new String[]{player1, player2, player3, player4, player5};
                            String[] pcw_a = new String[]{p1cw, p2cw, p3cw, p4cw, p5cw};
                            int[] guest_id_a = new int[]{guest_id1, guest_id2, guest_id3, guest_id4, guest_id5};
                            String[] userg_a = new String[]{userg1, userg2, userg3, userg4, userg5};

                            // Fill that field map with values that will be used when calling back
                            hidden_field_map.put("skip8", "yes");
                            hidden_field_map.put("player%", player_a);
                            hidden_field_map.put("p%cw", pcw_a);
                            hidden_field_map.put("guest_id%", guest_id_a);
                            hidden_field_map.put("userg%", userg_a);
                            hidden_field_map.put("gender%", new String[]{gender1, gender2, gender3, gender4, gender5});
                            hidden_field_map.put("ghin%", new String[]{ghin1, ghin2, ghin3, ghin4, ghin5});
                            hidden_field_map.put("homeclub%", new String[]{homeclub1, homeclub2, homeclub3, homeclub4, homeclub5});
                            hidden_field_map.put("phone%", new String[]{phone1, phone2, phone3, phone4, phone5});
                            hidden_field_map.put("address%", new String[]{address1, address2, address3, address4, address5});
                            hidden_field_map.put("email%", new String[]{email1, email2, email3, email4, email5});
                            hidden_field_map.put("shirt%", new String[]{shirt1, shirt2, shirt3, shirt4, shirt5});
                            hidden_field_map.put("shoe%", new String[]{shoe1, shoe2, shoe3, shoe4, shoe5});
                            hidden_field_map.put("other%A1", new String[]{other1A1, other2A1, other3A1, other4A1, other5A1});
                            hidden_field_map.put("other%A2", new String[]{other1A2, other2A2, other3A2, other4A2, other5A2});
                            hidden_field_map.put("other%A3", new String[]{other1A3, other2A3, other3A3, other4A3, other5A3});
                            hidden_field_map.put("index", index);
                            hidden_field_map.put("id", id);
                            hidden_field_map.put("event_id", event_id);
                            hidden_field_map.put("course", course);
                            hidden_field_map.put("notes", notes);
                            hidden_field_map.put("hide", hides);
                            hidden_field_map.put("name", name);
                            hidden_field_map.put("submitForm", "YES - continue");
                            if (ext_login == true) {
                                hidden_field_map.put("ext-login", "yes");   // do both to ensure it works
                                hidden_field_map.put("ext-dReq", "yes");   
                            }

                            // Build the player list
                            String player_list_html = "<ul class=\"indented_list\">";
                            for (int i3 = 0; i3 < player_a.length; i3++) {
                                if (!player_a[i3].equals("")) {
                                    player_list_html += "<li class=\"" + ((!userg_a[i3].equals("")) ? "guest_item" : "player_item") + "\">" + player_a[i3] + "</li>";
                                }
                            }
                            player_list_html += "</ul>";

                            // Fill the result map
                            result_map.put("title", "Player/Guest Association Prompt");
                            result_map.put("prompt_yes_no", true);
                            result_map.put("successful", false);
                            result_map.put("field_override_hidden_map", hidden_field_map);

                            result_map.put("message_array", new String[]{
                                        "Guests must be specified <b>immediately after</b> the member they belong to.",
                                        "<b>Please verify the following order:</b>",
                                        player_list_html,
                                        "Would you like to process the request as is?"});

                            // Send results as json string
                            out.print(gson_obj.toJson(result_map));

                            out.close();
                            return;

                        } else {

                            out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
                            out.println("<BODY bgcolor=\"#CCCCAA\">");
                            out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                            out.println("<hr width=\"40%\">");
                            out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");
                            out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");

                            if (!g1.equals("")) {    // if player1 is a guest

                                out.println("The first player position cannot contain a guest.  Please correct the order<br>");
                                out.println("of players.  This is what you requested:");

                            } else {

                                out.println("Please verify that the following order is correct:");
                            }
                            out.println("<BR><BR>");
                            out.println(player1 + " <BR>");
                            out.println(player2 + " <BR>");
                            if (!player3.equals("")) {
                                out.println(player3 + " <BR>");
                            }
                            if (!player4.equals("")) {
                                out.println(player4 + " <BR>");
                            }
                            if (!player5.equals("")) {
                                out.println(player5 + " <BR>");
                            }

                            if (g1.equals("")) {    // if player1 is not a guest

                                out.println("<BR>Would you like to process the request as is?");
                            }

                            //
                            //  Return to _evntSignUp to change the player order
                            //
                            out.println("<font size=\"2\">");
                            out.println("<form action=\"Member_evntSignUp\" method=\"post\" target=\"_top\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                            out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
                            out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
                            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                            out.println("<input type=\"hidden\" name=\"gender1\" value=\"" + gender1 + "\">");
                            out.println("<input type=\"hidden\" name=\"gender2\" value=\"" + gender2 + "\">");
                            out.println("<input type=\"hidden\" name=\"gender3\" value=\"" + gender3 + "\">");
                            out.println("<input type=\"hidden\" name=\"gender4\" value=\"" + gender4 + "\">");
                            out.println("<input type=\"hidden\" name=\"gender5\" value=\"" + gender5 + "\">");
                            out.println("<input type=\"hidden\" name=\"ghin1\" value=\"" + ghin1 + "\">");
                            out.println("<input type=\"hidden\" name=\"ghin2\" value=\"" + ghin2 + "\">");
                            out.println("<input type=\"hidden\" name=\"ghin3\" value=\"" + ghin3 + "\">");
                            out.println("<input type=\"hidden\" name=\"ghin4\" value=\"" + ghin4 + "\">");
                            out.println("<input type=\"hidden\" name=\"ghin5\" value=\"" + ghin5 + "\">");
                            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"hidden\" name=\"homeclub1\" value=\"" + homeclub1 + "\">");
                            out.println("<input type=\"hidden\" name=\"homeclub2\" value=\"" + homeclub2 + "\">");
                            out.println("<input type=\"hidden\" name=\"homeclub3\" value=\"" + homeclub3 + "\">");
                            out.println("<input type=\"hidden\" name=\"homeclub4\" value=\"" + homeclub4 + "\">");
                            out.println("<input type=\"hidden\" name=\"homeclub5\" value=\"" + homeclub5 + "\">");
                            out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
                            out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
                            out.println("<input type=\"hidden\" name=\"phone3\" value=\"" + phone3 + "\">");
                            out.println("<input type=\"hidden\" name=\"phone4\" value=\"" + phone4 + "\">");
                            out.println("<input type=\"hidden\" name=\"phone5\" value=\"" + phone5 + "\">");
                            out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
                            out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
                            out.println("<input type=\"hidden\" name=\"address3\" value=\"" + address3 + "\">");
                            out.println("<input type=\"hidden\" name=\"address4\" value=\"" + address4 + "\">");
                            out.println("<input type=\"hidden\" name=\"address5\" value=\"" + address5 + "\">");
                            out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
                            out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
                            out.println("<input type=\"hidden\" name=\"email3\" value=\"" + email3 + "\">");
                            out.println("<input type=\"hidden\" name=\"email4\" value=\"" + email4 + "\">");
                            out.println("<input type=\"hidden\" name=\"email5\" value=\"" + email5 + "\">");
                            out.println("<input type=\"hidden\" name=\"shirt1\" value=\"" + shirt1 + "\">");
                            out.println("<input type=\"hidden\" name=\"shirt2\" value=\"" + shirt2 + "\">");
                            out.println("<input type=\"hidden\" name=\"shirt3\" value=\"" + shirt3 + "\">");
                            out.println("<input type=\"hidden\" name=\"shirt4\" value=\"" + shirt4 + "\">");
                            out.println("<input type=\"hidden\" name=\"shirt5\" value=\"" + shirt5 + "\">");
                            out.println("<input type=\"hidden\" name=\"shoe1\" value=\"" + shoe1 + "\">");
                            out.println("<input type=\"hidden\" name=\"shoe2\" value=\"" + shoe2 + "\">");
                            out.println("<input type=\"hidden\" name=\"shoe3\" value=\"" + shoe3 + "\">");
                            out.println("<input type=\"hidden\" name=\"shoe4\" value=\"" + shoe4 + "\">");
                            out.println("<input type=\"hidden\" name=\"shoe5\" value=\"" + shoe5 + "\">");
                            out.println("<input type=\"hidden\" name=\"other1A1\" value=\"" + other1A1 + "\">");
                            out.println("<input type=\"hidden\" name=\"other1A2\" value=\"" + other1A2 + "\">");
                            out.println("<input type=\"hidden\" name=\"other1A3\" value=\"" + other1A3 + "\">");
                            out.println("<input type=\"hidden\" name=\"other2A1\" value=\"" + other2A1 + "\">");
                            out.println("<input type=\"hidden\" name=\"other2A2\" value=\"" + other2A2 + "\">");
                            out.println("<input type=\"hidden\" name=\"other2A3\" value=\"" + other2A3 + "\">");
                            out.println("<input type=\"hidden\" name=\"other3A1\" value=\"" + other3A1 + "\">");
                            out.println("<input type=\"hidden\" name=\"other3A2\" value=\"" + other3A2 + "\">");
                            out.println("<input type=\"hidden\" name=\"other3A3\" value=\"" + other3A3 + "\">");
                            out.println("<input type=\"hidden\" name=\"other4A1\" value=\"" + other4A1 + "\">");
                            out.println("<input type=\"hidden\" name=\"other4A2\" value=\"" + other4A2 + "\">");
                            out.println("<input type=\"hidden\" name=\"other4A3\" value=\"" + other4A3 + "\">");
                            out.println("<input type=\"hidden\" name=\"other5A1\" value=\"" + other5A1 + "\">");
                            out.println("<input type=\"hidden\" name=\"other5A2\" value=\"" + other5A2 + "\">");
                            out.println("<input type=\"hidden\" name=\"other5A3\" value=\"" + other5A3 + "\">");
                            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

                            if (ext_login == true) {     // if came from external login (no frames)
                                out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                            }

                            if (g1.equals("")) {    // if player1 is not a guest

                                out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; \">");

                            } else {
                                out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                            }
                            out.println("</form></font>");

                            if (g1.equals("")) {    // if player1 is not a guest

                                //
                                //  Return to process the players as they are
                                //
                                out.println("<font size=\"2\">");
                                out.println("<form action=\"Member_evntSignUp\" method=\"post\" target=\"_top\">");
                                out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
                                out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
                                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                                out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
                                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
                                out.println("<input type=\"hidden\" name=\"gender1\" value=\"" + gender1 + "\">");
                                out.println("<input type=\"hidden\" name=\"gender2\" value=\"" + gender2 + "\">");
                                out.println("<input type=\"hidden\" name=\"gender3\" value=\"" + gender3 + "\">");
                                out.println("<input type=\"hidden\" name=\"gender4\" value=\"" + gender4 + "\">");
                                out.println("<input type=\"hidden\" name=\"gender5\" value=\"" + gender5 + "\">");
                                out.println("<input type=\"hidden\" name=\"ghin1\" value=\"" + ghin1 + "\">");
                                out.println("<input type=\"hidden\" name=\"ghin2\" value=\"" + ghin2 + "\">");
                                out.println("<input type=\"hidden\" name=\"ghin3\" value=\"" + ghin3 + "\">");
                                out.println("<input type=\"hidden\" name=\"ghin4\" value=\"" + ghin4 + "\">");
                                out.println("<input type=\"hidden\" name=\"ghin5\" value=\"" + ghin5 + "\">");
                                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                                out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                                out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                                out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
                                out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
                                out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + userg1 + "\">");
                                out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + userg2 + "\">");
                                out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + userg3 + "\">");
                                out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + userg4 + "\">");
                                out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + userg5 + "\">");
                                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                                out.println("<input type=\"hidden\" name=\"homeclub1\" value=\"" + homeclub1 + "\">");
                                out.println("<input type=\"hidden\" name=\"homeclub2\" value=\"" + homeclub2 + "\">");
                                out.println("<input type=\"hidden\" name=\"homeclub3\" value=\"" + homeclub3 + "\">");
                                out.println("<input type=\"hidden\" name=\"homeclub4\" value=\"" + homeclub4 + "\">");
                                out.println("<input type=\"hidden\" name=\"homeclub5\" value=\"" + homeclub5 + "\">");
                                out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
                                out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
                                out.println("<input type=\"hidden\" name=\"phone3\" value=\"" + phone3 + "\">");
                                out.println("<input type=\"hidden\" name=\"phone4\" value=\"" + phone4 + "\">");
                                out.println("<input type=\"hidden\" name=\"phone5\" value=\"" + phone5 + "\">");
                                out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
                                out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
                                out.println("<input type=\"hidden\" name=\"address3\" value=\"" + address3 + "\">");
                                out.println("<input type=\"hidden\" name=\"address4\" value=\"" + address4 + "\">");
                                out.println("<input type=\"hidden\" name=\"address5\" value=\"" + address5 + "\">");
                                out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
                                out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
                                out.println("<input type=\"hidden\" name=\"email3\" value=\"" + email3 + "\">");
                                out.println("<input type=\"hidden\" name=\"email4\" value=\"" + email4 + "\">");
                                out.println("<input type=\"hidden\" name=\"email5\" value=\"" + email5 + "\">");
                                out.println("<input type=\"hidden\" name=\"shirt1\" value=\"" + shirt1 + "\">");
                                out.println("<input type=\"hidden\" name=\"shirt2\" value=\"" + shirt2 + "\">");
                                out.println("<input type=\"hidden\" name=\"shirt3\" value=\"" + shirt3 + "\">");
                                out.println("<input type=\"hidden\" name=\"shirt4\" value=\"" + shirt4 + "\">");
                                out.println("<input type=\"hidden\" name=\"shirt5\" value=\"" + shirt5 + "\">");
                                out.println("<input type=\"hidden\" name=\"shoe1\" value=\"" + shoe1 + "\">");
                                out.println("<input type=\"hidden\" name=\"shoe2\" value=\"" + shoe2 + "\">");
                                out.println("<input type=\"hidden\" name=\"shoe3\" value=\"" + shoe3 + "\">");
                                out.println("<input type=\"hidden\" name=\"shoe4\" value=\"" + shoe4 + "\">");
                                out.println("<input type=\"hidden\" name=\"shoe5\" value=\"" + shoe5 + "\">");
                                out.println("<input type=\"hidden\" name=\"other1A1\" value=\"" + other1A1 + "\">");
                                out.println("<input type=\"hidden\" name=\"other1A2\" value=\"" + other1A2 + "\">");
                                out.println("<input type=\"hidden\" name=\"other1A3\" value=\"" + other1A3 + "\">");
                                out.println("<input type=\"hidden\" name=\"other2A1\" value=\"" + other2A1 + "\">");
                                out.println("<input type=\"hidden\" name=\"other2A2\" value=\"" + other2A2 + "\">");
                                out.println("<input type=\"hidden\" name=\"other2A3\" value=\"" + other2A3 + "\">");
                                out.println("<input type=\"hidden\" name=\"other3A1\" value=\"" + other3A1 + "\">");
                                out.println("<input type=\"hidden\" name=\"other3A2\" value=\"" + other3A2 + "\">");
                                out.println("<input type=\"hidden\" name=\"other3A3\" value=\"" + other3A3 + "\">");
                                out.println("<input type=\"hidden\" name=\"other4A1\" value=\"" + other4A1 + "\">");
                                out.println("<input type=\"hidden\" name=\"other4A2\" value=\"" + other4A2 + "\">");
                                out.println("<input type=\"hidden\" name=\"other4A3\" value=\"" + other4A3 + "\">");
                                out.println("<input type=\"hidden\" name=\"other5A1\" value=\"" + other5A1 + "\">");
                                out.println("<input type=\"hidden\" name=\"other5A2\" value=\"" + other5A2 + "\">");
                                out.println("<input type=\"hidden\" name=\"other5A3\" value=\"" + other5A3 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
                                out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

                                if (ext_login == true) {     // if came from external login (no frames)
                                    out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
                                }

                                out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
                                out.println("</form></font>");
                            }
                            out.println("</center></body></html>");
                            out.close();
                            return;
                        }

                    }   // end of IF more than 1 member OR guest in #1 slot

                }      // end of IF any guests specified

            } else {   // skip 8 requested

                //
                //  User has responded to the guest association prompt - process tee time request in specified order
                //
                userg1 = req.getParameter("userg1");
                userg2 = req.getParameter("userg2");
                userg3 = req.getParameter("userg3");
                userg4 = req.getParameter("userg4");
                userg5 = req.getParameter("userg5");

            } // end of IF skip8

            if (club.equals("discoverybay")) {
                
                PreparedStatement pstmtx = null;
                ResultSet rsx = null;
                
                String[] userA = new String[5];
                String[] mshipA = new String[5];

                for (int j = 0; j < 5; j++) {
                    mshipA[j] = "";
                }

                userA[0] = user1;
                userA[1] = user2;
                userA[2] = user3;
                userA[3] = user4;
                userA[4] = user5;
                
                
                // Get the mship values for all 5 players
                for (int j = 0; j < 5; j++) {
                 
                    if (!userA[j].equals("")) {
                        
                        try {

                            pstmtx = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                            pstmtx.clearParameters();
                            pstmtx.setString(1, userA[j]);

                            rsx = pstmtx.executeQuery();

                            if (rsx.next()) {
                                mshipA[j] = rsx.getString(1);
                            }

                        } catch (Exception exc) {
                            Utilities.logError("Member_evntSignUp.verify - " + club + " - Error looking up mship values - ERR: " + exc.toString());
                        } finally {

                            try { rsx.close(); }
                            catch (Exception ignore) {}

                            try { pstmtx.close(); }
                            catch (Exception ignore) {}
                        }
                    }
                }         
                
                parms.mship1 = mshipA[0];
                parms.mship2 = mshipA[1];
                parms.mship3 = mshipA[2];
                parms.mship4 = mshipA[3];
                parms.mship5 = mshipA[4];
                
                parms.userg1 = userg1;
                parms.userg2 = userg2;
                parms.userg3 = userg3;
                parms.userg4 = userg4;
                parms.userg5 = userg5;
                
                if (verifyCustom.checkDiscoveryBayGuests(parms, con)) {    // check max rounds for Assoc B mships
           
                    out.println(SystemUtils.HeadTitle("Guest Type Not Allowed - Reject"));
                    out.println("<BODY bgcolor=\"#CCCCAA\">");
                    out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                    out.println("<hr width=\"40%\">");
                    out.println("<H3>Guest Type Not Allowed</H3>" +
                            "<BR>Sorry, but " + parms.player + " is not allowed to specify the Junior guest type." +
                            "<BR>Single Golf members are not permitted to bring a Junior guest at any time." +
                            "<BR><BR>Please select a different guest type for this member or return to the tee sheet.");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                            otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                }
            }


            //
            // Notes: Handciap # and gender are two new options that are included in the slot
            //      



            //
            // If handicap numbers are required for this event then test for them
            //
            if (req_hdcp == 1) {

                // Check to make sure any guests that are present have names included
                i = 0;
                loop1:
                while (i < 5) {

                    if (!player[i].equals("")) {    // if player present

                        if (hdcp[i].equals("")) {      // if hdcp # not specified

                            out.println(SystemUtils.HeadTitle("Data Entry Error"));
                            out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                            out.println("<center>");
                            out.println("<BR><BR><H3>Missing Required Data</H3>");
                            out.println("<BR><BR><b>" + player[i] + "</b> does not have a " + ((sess_activity_id == 0) ? "handicap" : "USTA") + " number specified.");
                            out.println("<BR><BR>" + ((sess_activity_id == 0) ? "Handicap" : "USTA") + " numbers are required for all players in this event.");
                            out.println("<BR><BR>Please correct this and try again.");
                            out.println("<BR><BR>");
                            out.println("<font size=\"2\">");

                            returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                            out.println("</font></center></body></html>");
                            out.close();
                            return;

                        } // end if hdcp empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if handicap numbers req



            //
            // If gender is required for this event then test for it
            //
            if (req_gender == 1) {

                // Check to make sure any guests that are present have a gender specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!gstA[i].equals("")) {      // if player present

                        if (gender[i].equals("")) {    // if gender not specified

                            out.println(SystemUtils.HeadTitle("Data Entry Error"));
                            out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                            out.println("<center>");
                            out.println("<BR><BR><H3>Missing Required Data</H3>");
                            out.println("<BR><BR><b>" + gstA[i] + "</b> does not have a gender specified.");
                            out.println("<BR><BR>All participants are required to specify a gender for this event.");
                            out.println("<BR><BR>Please correct this and try again.");
                            out.println("<BR><BR>");
                            out.println("<font size=\"2\">");

                            returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    otherA1, otherA2, otherA3, userg1, userg2, userg3, userg4, userg5,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, rev, id, event_id, ext_login, out);

                            out.println("</font></center></body></html>");
                            out.close();
                            return;

                        } // end if gender empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if gender req



            //
            // Note:  The additional options below are presented on a new page - not slot
            //      


            // If we are here from slot and there is additional data to get from user then
            // before doing any other checks, let call the second page.  
            // If we've already been to the second page then proceed with the checks.
            if (req.getParameter("sp") == null
                    && ((ask_homeclub + ask_phone + ask_address + ask_email > 0 && guests > 0)
                    || (ask_shirtsize + ask_shoesize + ask_otherA1 + ask_otherA2 + ask_otherA3 > 0))) {

                if (!new_skin) {
                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                }
                extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                        player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                        ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                        otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);
                if (!new_skin) {
                    out.println("</font></center></body></html>");
                }
                out.close();
                return;

            }

            //
            // If home clubs are required for this event then test for them
            //
            if (req_homeclub == 1) {

                // Check to make sure any guests that are present have home clubs specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!gstA[i].equals("")) {      // if guest present

                        if (homeclub[i].equals("")) {  // if home club not specified


                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + player[i] + "</b> does not have a home club specified.");
                                out.println("<BR><BR>Home clubs are required for all guests in this event.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + player[i] + "</b> does not have a home club specified.");
                                messages.add("Home clubs are required for all guests in this event.");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);
                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if home club empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if home clubs req



            //
            // If phone numbers are required for this event then test for them
            //
            if (req_phone == 1) {

                // Check to make sure any guests that are present have phone numbers included
                i = 0;
                loop1:
                while (i < 5) {

                    if (!gstA[i].equals("")) {      // if guest present

                        if (phone[i].equals("")) {     // if phone number not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + gstA[i] + "</b> does not have a phone number specified.");
                                out.println("<BR><BR>Phone numbers are required for all guests in this event.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + gstA[i] + "</b> does not have a phone number specified.");
                                messages.add("Phone numbers are required for all guests in this event.");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if phone empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if phone numbers req



            //
            // If addresses are required for this event then test for them
            //
            if (req_address == 1) {

                // Check to make sure any guests that are present have addresses included
                i = 0;
                loop1:
                while (i < 5) {

                    if (!gstA[i].equals("")) {      // if guest present

                        if (address[i].equals("")) {   // if address not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + gstA[i] + "</b> does not have an address specified.");
                                out.println("<BR><BR>Addresses are required for all guests in this event.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + gstA[i] + "</b> does not have an address specified.");
                                messages.add("Addresses are required for all guests in this event.");
                            }
                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if address empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if addresses req



            //
            // If emails are required for this event then test for them
            //
            if (req_email == 1) {

                // Check to make sure any guests that are present have email address included
                i = 0;
                loop1:
                while (i < 5) {

                    if (!gstA[i].equals("")) {      // if guest present

                        if (email[i].equals("")) {     // if email address not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + gstA[i] + "</b> does not have an email address specified.");
                                out.println("<BR><BR>Email addresses are required for all guests in this event.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + gstA[i] + "</b> does not have an email address specified.");
                                messages.add("Email addresses are required for all guests in this event.");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if email address empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if email addresses req



            //
            // This questions below are asked of all players
            //



            //
            // If shirt size is required for this event then test for it
            //
            if (req_shirtsize == 1) {

                // Check to make sure any guests that are present have a shirt size specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!player[i].equals("")) {      // if player present

                        if (shirt[i].equals("")) {     // if shirt size not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + player[i] + "</b> does not have a shirt size specified.");
                                out.println("<BR><BR>All participants are required to specify a shirt size for this event.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + player[i] + "</b> does not have a shirt size specified.");
                                messages.add("All participants are required to specify a shirt size for this event.");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if shirt size empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if shirt size req



            //
            // If shoe size is required for this event then test for it
            //
            if (req_shoesize == 1) {

                // Check to make sure any guests that are present have a shoe size specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!player[i].equals("")) {      // if player present

                        if (shoe[i].equals("")) {      // if shoe size not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + player[i] + "</b> does not have a shoe size specified.");
                                out.println("<BR><BR>All participants are required to specify a shoe size for this event.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + player[i] + "</b> does not have a shoe size specified.");
                                messages.add("All participants are required to specify a shoe size for this event.");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if shoe size empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if shoe size req



            //
            // If other A1 is required for this event then test for it
            //
            if (req_otherA1 == 1) {

                // Check to make sure any guests that are present have a shoe size specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!player[i].equals("")) {      // if player present

                        if (otherA1[i].equals("")) {   // if other answer not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + player[i] + "</b> is missing an answer to Question #1.");
                                //out.println("<BR><BR>All participants are required to specify an answer to Question #1.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + player[i] + "</b>[options.event.missingQuestion1]");
                                //messages.add("");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if other A1 empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if other A1



            //
            // If other A2 is required for this event then test for it
            //
            if (req_otherA2 == 1) {

                // Check to make sure any guests that are present have a shoe size specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!player[i].equals("")) {      // if player present

                        if (otherA2[i].equals("")) {   // if other answer not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + player[i] + "</b> is missing an answer to Question #2.");
                                //out.println("<BR><BR>All participants are required to specify an answer to Question #2.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + player[i] + "</b>[options.event.missingQuestion2]");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if other A1 empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if other A2



            //
            // If other A3 is required for this event then test for it
            //
            if (req_otherA3 == 1) {

                // Check to make sure any guests that are present have a shoe size specified
                i = 0;
                loop1:
                while (i < 5) {

                    if (!player[i].equals("")) {      // if player present

                        if (otherA3[i].equals("")) {   // if other answer not specified

                            if (!new_skin) {
                                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                                out.println("<center>");
                                out.println("<BR><H3>Additional Information for Sign-up</H3>");
                                out.println("<i><b><font color=red>Missing Required Information</font></b></i>");

                                out.println("<BR><BR><b>" + player[i] + "</b> is missing an answer to Question #3.");
                                //out.println("<BR><BR>All participants are required to specify an answer to Question #3.");
                                out.println("<BR><BR>Please correct this and try again.");
                                out.println("<BR><BR>");
                                out.println("<font size=\"2\">");
                            } else {
                                messages.add("<b>" + player[i] + "</b>[options.event.missingQuestion3]");
                            }

                            extraInfoForm(player1, player2, player3, player4, player5, userg1, userg2, userg3, userg4, userg5, p1cw, p2cw, p3cw, p4cw, p5cw,
                                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                                    player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3,
                                    ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3,
                                    otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests,
                                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                    notes, hides, index, course, name, club, rev, id, event_id, ext_login, out, new_skin, messages);

                            if (!new_skin) {
                                out.println("</font></center></body></html>");
                            }
                            out.close();
                            return;

                        } // end if other A3 empty

                    } // end if player present

                    i++;

                } // end of while

            } // end if other A3






            //**************************************************************
            //  Verification Complete !!!!!!!!
            //**************************************************************
            //
            //  See if entry should be on wait list
            //
            if (wait == 0) {     // if not already on wait list

                if (oldPlayer1.equals("") && oldPlayer2.equals("") && oldPlayer3.equals("")
                        && oldPlayer4.equals("") && oldPlayer5.equals("")) {      // and new entry

                    teams = 0;         // init # of teams registered

                    try {
                        //
                        //   see if event is full
                        //
                        PreparedStatement pstmtw = con.prepareStatement(
                                "SELECT player1, player2, player3, player4, player5 "
                                + "FROM evntsup2b "
                                + "WHERE name = ? AND wait = 0 AND inactive = 0");

                        pstmtw.clearParameters();
                        pstmtw.setString(1, name);
                        rs2 = pstmtw.executeQuery();

                        while (rs2.next()) {

                            wplayer1 = rs2.getString("player1");
                            wplayer2 = rs2.getString("player2");
                            wplayer3 = rs2.getString("player3");
                            wplayer4 = rs2.getString("player4");
                            wplayer5 = rs2.getString("player5");

                            if (!wplayer1.equals("") || !wplayer2.equals("") || !wplayer3.equals("")
                                    || !wplayer4.equals("") || !wplayer5.equals("")) {

                                teams++;
                            }

                        }
                        pstmtw.close();

                    } catch (Exception ignore) {
                    }

                    if (teams >= max) {           // if event already full

                        wait = 1;                  // put on wait list
                    }
                }
            }

            //
            //  process email requirements for this entry
            //
            sendemail = 0;         // init email flags

            //if (!proemail1.equals("") || !proemail2.equals("")) sendemail = 1;

            emailNew = 0;
            emailMod = 0;
            //
            //  If players changed, then init the no-show flag and send emails, else use the old no-show value
            //
            if (!player1.equals(oldPlayer1)) {

                sendemail = 1;    // player changed - send email notification to all
            }

            if (!player2.equals(oldPlayer2)) {

                sendemail = 1;    // player changed - send email notification to all
            }

            if (!player3.equals(oldPlayer3)) {

                sendemail = 1;    // player changed - send email notification to all
            }

            if (!player4.equals(oldPlayer4)) {

                sendemail = 1;    // player changed - send email notification to all
            }

            if (!player5.equals(oldPlayer5)) {

                sendemail = 1;    // player changed - send email notification to all
            }

            //
            //   Set email type based on new or update request (cancel set above)
            //
            if ((!oldPlayer1.equals("")) || (!oldPlayer2.equals("")) || (!oldPlayer3.equals(""))
                    || (!oldPlayer4.equals("")) || (!oldPlayer5.equals(""))) {

                emailMod = 1;  // tee time was modified

                //
                //  Shift players up in case one was removed from the request (player1 must be occupied if any players exist)
                //
                if (player1.equals("")) {    // if empty

                    if (!player2.equals("")) {    // if not empty

                        player1 = player2;
                        p1cw = p2cw;
                        guest_id1 = guest_id2;
                        user1 = user2;
                        hndcp1 = hndcp2;
                        userg1 = userg2;
                        gender1 = gender2;
                        ghin1 = ghin2;
                        homeclub1 = homeclub2;
                        phone1 = phone2;
                        address1 = address2;
                        email1 = email2;
                        shirt1 = shirt2;
                        shoe1 = shoe2;
                        other1A1 = other2A1;
                        other1A2 = other2A2;
                        other1A3 = other2A3;

                        player2 = "";
                        p2cw = "";
                        guest_id2 = 0;
                        user2 = "";
                        hndcp2 = 0;
                        userg2 = "";
                        gender2 = "";
                        ghin2 = "";
                        homeclub2 = "";
                        phone2 = "";
                        address2 = "";
                        email2 = "";
                        shirt2 = "";
                        shoe2 = "";
                        other2A1 = "";
                        other2A2 = "";
                        other2A3 = "";

                    } else {

                        if (!player3.equals("")) {    // if not empty

                            player1 = player3;
                            p1cw = p3cw;
                            guest_id1 = guest_id3;
                            user1 = user3;
                            hndcp1 = hndcp3;
                            userg1 = userg3;
                            gender1 = gender3;
                            ghin1 = ghin3;
                            homeclub1 = homeclub3;
                            phone1 = phone3;
                            address1 = address3;
                            email1 = email3;
                            shirt1 = shirt3;
                            shoe1 = shoe3;
                            other1A1 = other3A1;
                            other1A2 = other3A2;
                            other1A3 = other3A3;

                            player3 = "";
                            p3cw = "";
                            guest_id3 = 0;
                            user3 = "";
                            hndcp3 = 0;
                            userg3 = "";
                            gender3 = "";
                            ghin3 = "";
                            homeclub3 = "";
                            phone3 = "";
                            address3 = "";
                            email3 = "";
                            shirt3 = "";
                            shoe3 = "";
                            other3A1 = "";
                            other3A2 = "";
                            other3A3 = "";

                        } else {

                            if (!player4.equals("")) {    // if not empty

                                player1 = player4;
                                p1cw = p4cw;
                                guest_id1 = guest_id4;
                                user1 = user4;
                                hndcp1 = hndcp4;
                                userg1 = userg4;
                                gender1 = gender4;
                                ghin1 = ghin4;
                                homeclub1 = homeclub4;
                                phone1 = phone4;
                                address1 = address4;
                                email1 = email4;
                                shirt1 = shirt4;
                                shoe1 = shoe4;
                                other1A1 = other4A1;
                                other1A2 = other4A2;
                                other1A3 = other4A3;

                                player4 = "";
                                p4cw = "";
                                guest_id4 = 0;
                                user4 = "";
                                hndcp4 = 0;
                                userg4 = "";
                                gender4 = "";
                                ghin4 = "";
                                homeclub4 = "";
                                phone4 = "";
                                address4 = "";
                                email4 = "";
                                shirt4 = "";
                                shoe4 = "";
                                other4A1 = "";
                                other4A2 = "";
                                other4A3 = "";

                            } else {

                                if (!player5.equals("")) {    // if not empty

                                    player1 = player5;
                                    p1cw = p5cw;
                                    guest_id1 = guest_id5;
                                    user1 = user5;
                                    hndcp1 = hndcp5;
                                    userg1 = userg5;
                                    gender1 = gender5;
                                    ghin1 = ghin5;
                                    homeclub1 = homeclub5;
                                    phone1 = phone5;
                                    address1 = address5;
                                    email1 = email5;
                                    shirt1 = shirt5;
                                    shoe1 = shoe5;
                                    other1A1 = other5A1;
                                    other1A2 = other5A2;
                                    other1A3 = other5A3;

                                    player5 = "";
                                    p5cw = "";
                                    guest_id5 = 0;
                                    user5 = "";
                                    hndcp5 = 0;
                                    userg5 = "";
                                    gender5 = "";
                                    ghin5 = "";
                                    homeclub5 = "";
                                    phone5 = "";
                                    address5 = "";
                                    email5 = "";
                                    shirt5 = "";
                                    shoe5 = "";
                                    other5A1 = "";
                                    other5A2 = "";
                                    other5A3 = "";
                                }
                            }
                        }
                    }
                }
                if (player2.equals("")) {    // if empty

                    if (!player3.equals("")) {    // if not empty

                        player2 = player3;
                        p2cw = p3cw;
                        guest_id2 = guest_id3;
                        user2 = user3;
                        hndcp2 = hndcp3;
                        userg2 = userg3;
                        gender2 = gender3;
                        ghin2 = ghin3;
                        homeclub2 = homeclub3;
                        phone2 = phone3;
                        address2 = address3;
                        email2 = email3;
                        shirt2 = shirt3;
                        shoe2 = shoe3;
                        other2A1 = other3A1;
                        other2A2 = other3A2;
                        other2A3 = other3A3;

                        player3 = "";
                        p3cw = "";
                        guest_id3 = 0;
                        user3 = "";
                        hndcp3 = 0;
                        userg3 = "";
                        gender3 = "";
                        ghin3 = "";
                        homeclub3 = "";
                        phone3 = "";
                        address3 = "";
                        email3 = "";
                        shirt3 = "";
                        shoe3 = "";
                        other3A1 = "";
                        other3A2 = "";
                        other3A3 = "";

                    } else {

                        if (!player4.equals("")) {    // if not empty

                            player2 = player4;
                            p2cw = p4cw;
                            guest_id2 = guest_id4;
                            user2 = user4;
                            hndcp2 = hndcp4;
                            userg2 = userg4;
                            gender2 = gender4;
                            ghin2 = ghin4;
                            homeclub2 = homeclub4;
                            phone2 = phone4;
                            address2 = address4;
                            email2 = email4;
                            shirt2 = shirt4;
                            shoe2 = shoe4;
                            other2A1 = other4A1;
                            other2A2 = other4A2;
                            other2A3 = other4A3;

                            player4 = "";
                            p4cw = "";
                            guest_id4 = 0;
                            user4 = "";
                            hndcp4 = 0;
                            userg4 = "";
                            gender4 = "";
                            ghin4 = "";
                            homeclub4 = "";
                            phone4 = "";
                            address4 = "";
                            email4 = "";
                            shirt4 = "";
                            shoe4 = "";
                            other4A1 = "";
                            other4A2 = "";
                            other4A3 = "";

                        } else {

                            if (!player5.equals("")) {    // if not empty

                                player2 = player5;
                                p2cw = p5cw;
                                guest_id2 = guest_id5;
                                user2 = user5;
                                hndcp2 = hndcp5;
                                userg2 = userg5;
                                gender2 = gender5;
                                ghin2 = ghin5;
                                homeclub2 = homeclub5;
                                phone2 = phone5;
                                address2 = address5;
                                email2 = email5;
                                shirt2 = shirt5;
                                shoe2 = shoe5;
                                other2A1 = other5A1;
                                other2A2 = other5A2;
                                other2A3 = other5A3;

                                player5 = "";
                                p5cw = "";
                                guest_id5 = 0;
                                user5 = "";
                                hndcp5 = 0;
                                userg5 = "";
                                gender5 = "";
                                ghin5 = "";
                                homeclub5 = "";
                                phone5 = "";
                                address5 = "";
                                email5 = "";
                                shirt5 = "";
                                shoe5 = "";
                                other5A1 = "";
                                other5A2 = "";
                                other5A3 = "";
                            }
                        }
                    }
                }
                if (player3.equals("")) {    // if empty

                    if (!player4.equals("")) {    // if not empty

                        player3 = player4;
                        p3cw = p4cw;
                        guest_id3 = guest_id4;
                        user3 = user4;
                        hndcp3 = hndcp4;
                        userg3 = userg4;
                        gender3 = gender4;
                        ghin3 = ghin4;
                        homeclub3 = homeclub4;
                        phone3 = phone4;
                        address3 = address4;
                        email3 = email4;
                        shirt3 = shirt4;
                        shoe3 = shoe4;
                        other3A1 = other4A1;
                        other3A2 = other4A2;
                        other3A3 = other4A3;

                        player4 = "";
                        p4cw = "";
                        guest_id4 = 0;
                        user4 = "";
                        hndcp4 = 0;
                        userg4 = "";
                        gender4 = "";
                        ghin4 = "";
                        homeclub4 = "";
                        phone4 = "";
                        address4 = "";
                        email4 = "";
                        shirt4 = "";
                        shoe4 = "";
                        other4A1 = "";
                        other4A2 = "";
                        other4A3 = "";

                    } else {

                        if (!player5.equals("")) {    // if not empty

                            player3 = player5;
                            p3cw = p5cw;
                            guest_id3 = guest_id5;
                            user3 = user5;
                            hndcp3 = hndcp5;
                            userg3 = userg5;
                            gender3 = gender5;
                            ghin3 = ghin5;
                            homeclub3 = homeclub5;
                            phone3 = phone5;
                            address3 = address5;
                            email3 = email5;
                            shirt3 = shirt5;
                            shoe3 = shoe5;
                            other3A1 = other5A1;
                            other3A2 = other5A2;
                            other3A3 = other5A3;

                            player5 = "";
                            p5cw = "";
                            guest_id5 = 0;
                            user5 = "";
                            hndcp5 = 0;
                            userg5 = "";
                            gender5 = "";
                            ghin5 = "";
                            homeclub5 = "";
                            phone5 = "";
                            address5 = "";
                            email5 = "";
                            shirt5 = "";
                            shoe5 = "";
                            other5A1 = "";
                            other5A2 = "";
                            other5A3 = "";
                        }
                    }
                }
                if (player4.equals("")) {    // if empty

                    if (!player5.equals("")) {    // if not empty

                        player4 = player5;
                        p4cw = p5cw;
                        guest_id4 = guest_id5;
                        user4 = user5;
                        hndcp4 = hndcp5;
                        userg4 = userg5;
                        gender4 = gender5;
                        ghin4 = ghin5;
                        homeclub4 = homeclub5;
                        phone4 = phone5;
                        address4 = address5;
                        email4 = email5;
                        shirt4 = shirt5;
                        shoe4 = shoe5;
                        other4A1 = other5A1;
                        other4A2 = other5A2;
                        other4A3 = other5A3;

                        player5 = "";
                        p5cw = "";
                        guest_id5 = 0;
                        user5 = "";
                        hndcp5 = 0;
                        userg5 = "";
                        gender5 = "";
                        ghin5 = "";
                        homeclub5 = "";
                        phone5 = "";
                        address5 = "";
                        email5 = "";
                        shirt5 = "";
                        shoe5 = "";
                        other5A1 = "";
                        other5A2 = "";
                        other5A3 = "";
                    }
                }


            } else {

                emailNew = 1;  // tee time is new
            }

        }  // end of 'cancel this res' if - cancel will contain empty player fields

        //
        //  Update the entry in the event sign up table
        //
        try {

            PreparedStatement pstmt6 = con.prepareStatement(
                    "UPDATE evntsup2b "
                    + "SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, "
                    + "username1 = ?, username2 = ?, username3 = ?, username4 = ?, username5 = ?, p1cw = ?, "
                    + "p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, "
                    + "hndcp4 = ?, hndcp5 = ?, notes = ?, wait = ?, "
                    + "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, "
                    + "gender1 = ?, gender2 = ?, gender3 = ?, gender4 = ?, gender5 = ?, "
                    + "ghin1 = ?, ghin2 = ?, ghin3 = ?, ghin4 = ?, ghin5 = ?, "
                    + "homeclub1 = ?, homeclub2 = ?, homeclub3 = ?, homeclub4 = ?, homeclub5 = ?, "
                    + "phone1 = ?, phone2 = ?, phone3 = ?, phone4 = ?, phone5 = ?, "
                    + "address1 = ?, address2 = ?, address3 = ?, address4 = ?, address5 = ?, "
                    + "email1 = ?, email2 = ?, email3 = ?, email4 = ?, email5 = ?, "
                    + "shirtsize1 = ?, shirtsize2 = ?, shirtsize3 = ?, shirtsize4 = ?, shirtsize5 = ?, "
                    + "shoesize1 = ?, shoesize2 = ?, shoesize3 = ?, shoesize4 = ?, shoesize5 = ?,"
                    + "other1A1 = ?, other1A2 = ?, other1A3 = ?, other2A1 = ?, other2A2 = ?, other2A3 = ?, "
                    + "other3A1 = ?, other3A2 = ?, other3A3 = ?, other4A1 = ?, other4A2 = ?, other4A3 = ?, "
                    + "other5A1 = ?, other5A2 = ?, other5A3 = ?, guest_id1 = ?, guest_id2 = ?, "
                    + "guest_id3 = ?, guest_id4 = ?, guest_id5 = ? "
                    + "WHERE id = ?"); // name = ? AND courseName = ? AND 

            pstmt6.clearParameters();
            pstmt6.setString(1, player1);
            pstmt6.setString(2, player2);
            pstmt6.setString(3, player3);
            pstmt6.setString(4, player4);
            pstmt6.setString(5, player5);
            pstmt6.setString(6, user1);
            pstmt6.setString(7, user2);
            pstmt6.setString(8, user3);
            pstmt6.setString(9, user4);
            pstmt6.setString(10, user5);
            pstmt6.setString(11, p1cw);
            pstmt6.setString(12, p2cw);
            pstmt6.setString(13, p3cw);
            pstmt6.setString(14, p4cw);
            pstmt6.setString(15, p5cw);
            pstmt6.setFloat(16, hndcp1);
            pstmt6.setFloat(17, hndcp2);
            pstmt6.setFloat(18, hndcp3);
            pstmt6.setFloat(19, hndcp4);
            pstmt6.setFloat(20, hndcp5);
            pstmt6.setString(21, notes);
            pstmt6.setInt(22, wait);
            pstmt6.setString(23, userg1);
            pstmt6.setString(24, userg2);
            pstmt6.setString(25, userg3);
            pstmt6.setString(26, userg4);
            pstmt6.setString(27, userg5);
            pstmt6.setString(28, gender1);
            pstmt6.setString(29, gender2);
            pstmt6.setString(30, gender3);
            pstmt6.setString(31, gender4);
            pstmt6.setString(32, gender5);
            pstmt6.setString(33, ghin1);
            pstmt6.setString(34, ghin2);
            pstmt6.setString(35, ghin3);
            pstmt6.setString(36, ghin4);
            pstmt6.setString(37, ghin5);

            pstmt6.setString(38, homeclub1);
            pstmt6.setString(39, homeclub2);
            pstmt6.setString(40, homeclub3);
            pstmt6.setString(41, homeclub4);
            pstmt6.setString(42, homeclub5);

            pstmt6.setString(43, phone1);
            pstmt6.setString(44, phone2);
            pstmt6.setString(45, phone3);
            pstmt6.setString(46, phone4);
            pstmt6.setString(47, phone5);

            pstmt6.setString(48, address1);
            pstmt6.setString(49, address2);
            pstmt6.setString(50, address3);
            pstmt6.setString(51, address4);
            pstmt6.setString(52, address5);

            pstmt6.setString(53, email1);
            pstmt6.setString(54, email2);
            pstmt6.setString(55, email3);
            pstmt6.setString(56, email4);
            pstmt6.setString(57, email5);

            pstmt6.setString(58, shirt1);
            pstmt6.setString(59, shirt2);
            pstmt6.setString(60, shirt3);
            pstmt6.setString(61, shirt4);
            pstmt6.setString(62, shirt5);

            pstmt6.setString(63, shoe1);
            pstmt6.setString(64, shoe2);
            pstmt6.setString(65, shoe3);
            pstmt6.setString(66, shoe4);
            pstmt6.setString(67, shoe5);

            pstmt6.setString(68, other1A1);
            pstmt6.setString(69, other1A2);
            pstmt6.setString(70, other1A3);
            pstmt6.setString(71, other2A1);
            pstmt6.setString(72, other2A2);
            pstmt6.setString(73, other2A3);
            pstmt6.setString(74, other3A1);
            pstmt6.setString(75, other3A2);
            pstmt6.setString(76, other3A3);
            pstmt6.setString(77, other4A1);
            pstmt6.setString(78, other4A2);
            pstmt6.setString(79, other4A3);
            pstmt6.setString(80, other5A1);
            pstmt6.setString(81, other5A2);
            pstmt6.setString(82, other5A3);

            pstmt6.setInt(83, guest_id1);
            pstmt6.setInt(84, guest_id2);
            pstmt6.setInt(85, guest_id3);
            pstmt6.setInt(86, guest_id4);
            pstmt6.setInt(87, guest_id5);

            pstmt6.setInt(88, id);

            pstmt6.executeUpdate();      // execute the prepared stmt

            pstmt6.close();

        } catch (Exception e6) {

            dbError(out, e6);
            return;
        }

        String mode = "UPDATE"; // default mode
        String detail = Utilities.buildPlayerString(player1, player2, player3, player4, player5, ", ");

        // if not here to cancel the signup then add the log entry
        if (req.getParameter("remove") != null) {

            mode = "CANCEL";
            detail = "Canceled by member.";

        } else if (emailNew == 1) {

            mode = "CREATE";

        }

        // add entry to event log
        Utilities.createEventLogEntry(user, event_id, id, mode, detail, con);

        //  Attempt to add hosts for any accompanied tracked guests
        if (guest_id1 > 0 && !userg1.equals("")) {
            Common_guestdb.addHost(guest_id1, userg1, con);
        }
        if (guest_id2 > 0 && !userg2.equals("")) {
            Common_guestdb.addHost(guest_id2, userg2, con);
        }
        if (guest_id3 > 0 && !userg3.equals("")) {
            Common_guestdb.addHost(guest_id3, userg3, con);
        }
        if (guest_id4 > 0 && !userg4.equals("")) {
            Common_guestdb.addHost(guest_id4, userg4, con);
        }
        if (guest_id5 > 0 && !userg5.equals("")) {
            Common_guestdb.addHost(guest_id5, userg5, con);
        }

        //
        //  Build the HTML page to confirm event registration for user
        //

        out.println(SystemUtils.HeadTitle("Member Event Sign Up Complete"));
        out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

        out.println("<center><img src=\"/" + rev + "/images/foretees.gif\"><hr width=\"40%\">");
        out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

        if (req.getParameter("remove") != null) {

            out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The event entry has been cancelled.</p>");

        } else {

            if (club.equals("olyclub")) {
                out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your Event Registration has been received.</p>");
            } else {
                out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your Event Registration has been accepted and processed.</p>");
            }

            if (wait > 0) {            // if on wait list

                out.println("<br><br><font size=5 style=\"background-color: yellow\"><b>Note:</b>  Your team is currently on the wait list.</font>");
            }

            if (xcount > 0 && xhrs > 0) {            // if any X's were specified

                out.println("<p>&nbsp;</p>All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the tee time.");
                out.println("<br>If not, the system will automatically remove the X.<br>");
            }

            //
            //  Add custom message for The Stanwich Club
            //
            if (sess_activity_id == 0 && club.equals("stanwichclub")) {

                out.println("<br><br><b>*** IMPORTANT SENIORITY INFORMATION - PLEASE READ</b>");
                out.println("<br><br>Thank you for signing up for this tournament.<br>If your email is setup with ForeTees, you will receive a confirmation email.");
                out.println("<br><br><i>However, if the event is oversubscribed before the deadline date<br>(see tournament information page ");
                out.println("for deadline date of each event),<br>member seniority will determine a waitlist.</i>");
                out.println("<br><br>You will be contacted by the tournament coordinator<br>if you are placed on a waitlist due to seniority.");
            }

        }

        out.println("<p>&nbsp;</p></font>");
        out.println("<font size=\"2\">");
        if (ext_login == true) {     // if came from external login (no frames)
            out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
        } else if (!index.equals("") && index.equals("0")) {
            out.println("<form action=\"Member_events2\" method=\"post\">");
        } else {
            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
        }
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");

        //
        //  End of HTML page
        //
        out.println("</center></font></body></html>");

        try {

            resp.flushBuffer();      // force the repsonse to complete

        } catch (Exception ignore) {
        }

        out.close();


        //
        //***********************************************
        //  if entry was removed then check for a wait list
        //  and move a team up if possible
        //***********************************************
        //
        // if (checkWait != 0 && !club.equals("medinahcc") && !club.equals("portlandgc")) { // no automated wait list move ups for Medinah (Case# 1475)
        if (checkWait != 0 && !club.equals("medinahcc") && !club.equals("ballenisles")) { // no automated wait list move ups for Medinah (Case# 1475)

            teams = 0;
            count = 0;

            try {

                //
                //   see if event is full
                //
                PreparedStatement pstmtw = con.prepareStatement(
                        "SELECT player1, player2, player3, player4, player5 "
                        + "FROM evntsup2b "
                        + "WHERE name = ? AND wait = 0 AND inactive = 0");

                pstmtw.clearParameters();        // clear the parms
                pstmtw.setString(1, name);
                rs2 = pstmtw.executeQuery();      // execute the prepared pstmt

                while (rs2.next()) {

                    wplayer1 = rs2.getString("player1");
                    wplayer2 = rs2.getString("player2");
                    wplayer3 = rs2.getString("player3");
                    wplayer4 = rs2.getString("player4");
                    wplayer5 = rs2.getString("player5");

                    if (!wplayer1.equals("") || !wplayer2.equals("") || !wplayer3.equals("")
                            || !wplayer4.equals("") || !wplayer5.equals("")) {

                        teams++;               // bump number of teams if any players
                    }

                }
                pstmtw.close();

                if (teams < max) {

                    long wdate = 0;
                    int wtime = 0;

                    //
                    //   get the earliest registration date on wait list
                    //
                    PreparedStatement stmtw1 = con.prepareStatement(
                            "SELECT MIN(r_date) FROM evntsup2b "
                            + "WHERE name = ? AND wait != 0 AND inactive = 0");

                    stmtw1.clearParameters();        // clear the parms
                    stmtw1.setString(1, name);
                    rs = stmtw1.executeQuery();      // execute the prepared stmt

                    if (rs.next()) {

                        wdate = rs.getLong(1);
                    }
                    stmtw1.close();

                    if (wdate != 0) {

                        //
                        //   get the earliest time on this reg date on wait list
                        //
                        PreparedStatement stmtw2 = con.prepareStatement(
                                "SELECT MIN(r_time) FROM evntsup2b "
                                + "WHERE name = ? AND r_date = ? AND wait != 0 AND inactive = 0");

                        stmtw2.clearParameters();        // clear the parms
                        stmtw2.setString(1, name);
                        stmtw2.setLong(2, wdate);
                        rs = stmtw2.executeQuery();      // execute the prepared stmt

                        if (rs.next()) {

                            wtime = rs.getInt(1);
                        }
                        stmtw2.close();


                        if (wtime != 0) {

                            //
                            //   get the earliest time on this reg date on wait list
                            //
                            PreparedStatement stmtw4 = con.prepareStatement(
                                    "SELECT username1, username2, username3, username4, username5, player1, player2, player3, player4, player5, "
                                    + "p1cw, p2cw, p3cw, p4cw, p5cw, id "
                                    + "FROM evntsup2b "
                                    + "WHERE name = ? AND r_date = ? AND r_time = ? AND wait != 0 AND inactive = 0");

                            stmtw4.clearParameters();        // clear the parms
                            stmtw4.setString(1, name);
                            stmtw4.setLong(2, wdate);
                            stmtw4.setInt(3, wtime);
                            rs = stmtw4.executeQuery();      // execute the prepared stmt

                            if (rs.next()) {

                                wuser1 = rs.getString("username1");
                                wuser2 = rs.getString("username2");
                                wuser3 = rs.getString("username3");
                                wuser4 = rs.getString("username4");
                                wuser5 = rs.getString("username5");
                                wplayer1 = rs.getString("player1");
                                wplayer2 = rs.getString("player2");
                                wplayer3 = rs.getString("player3");
                                wplayer4 = rs.getString("player4");
                                wplayer5 = rs.getString("player5");
                                wp1cw = rs.getString("p1cw");
                                wp2cw = rs.getString("p2cw");
                                wp3cw = rs.getString("p3cw");
                                wp4cw = rs.getString("p4cw");
                                wp5cw = rs.getString("p5cw");
                                id = rs.getInt("id");
                            }
                            stmtw4.close();

                            PreparedStatement pstmtw3 = con.prepareStatement(
                                    "UPDATE evntsup2b "
                                    + "SET wait = 0 "
                                    + "WHERE name = ? AND id = ?");

                            pstmtw3.clearParameters();        // clear the parms
                            pstmtw3.setString(1, name);
                            pstmtw3.setInt(2, id);

                            count = pstmtw3.executeUpdate();      // execute the prepared stmt

                            pstmtw3.close();

                            if (count == 0) {

                                SystemUtils.logError("Process Wait List for Club " + club + ". Error removing wait list. Name = " + name + ", id = " + id);       // log the error
                            }
                        }
                    }
                }
            } catch (Exception e1) {

                SystemUtils.logError("Error in Member_evntSignUp.verify - Process Wait List for Club " + club + ". Exception = " + e1.getMessage());       // log it and continue
            }

        }     // end of IF checkWait


        //
        //***********************************************
        //  Send email notification if necessary
        //***********************************************
        //
        if (sendemail != 0) {

            long date = (year * 10000) + (month * 100) + day;    // create event date value  

            //
            //  allocate a parm block to hold the email parms
            //
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            String activity_name = "";
            try {
                activity_name = getActivity.getActivityName(sess_activity_id, con);
            } catch (Exception ignore) {
            }

            //
            //  Set the values in the email parm block
            //
            parme.activity_id = sess_activity_id;
            parme.club = club;
            parme.guests = guests;
            //parme.type = "activity";         // type = time slot
            parme.activity_name = activity_name;
            parme.actual_activity_name = activity_name;

            parme.type = "event";         // type = event
            parme.date = date;
            parme.time = 0;
            parme.fb = 0;
            parme.mm = month;
            parme.dd = day;
            parme.yy = year;
            parme.season = season;

            parme.wuser1 = wuser1;     // set Event-only fields
            parme.wuser2 = wuser2;
            parme.wuser3 = wuser3;
            parme.wuser4 = wuser4;
            parme.wuser5 = wuser5;
            parme.wplayer1 = wplayer1;
            parme.wplayer2 = wplayer2;
            parme.wplayer3 = wplayer3;
            parme.wplayer4 = wplayer4;
            parme.wplayer5 = wplayer5;
            parme.wp1cw = wp1cw;
            parme.wp2cw = wp2cw;
            parme.wp3cw = wp3cw;
            parme.wp4cw = wp4cw;
            parme.wp5cw = wp5cw;
            parme.name = name;
            parme.etype = etype;
            parme.act_time = act_time;
            parme.wait = wait;
            parme.checkWait = checkWait;

            parme.user = user;
            parme.emailNew = emailNew;
            parme.emailMod = emailMod;
            parme.emailCan = emailCan;

            parme.p91 = 0;     // doesn't matter for event
            parme.p92 = 0;
            parme.p93 = 0;
            parme.p94 = 0;
            parme.p95 = 0;

            parme.course = course;
            parme.day = "";

            parme.player1 = player1;
            parme.player2 = player2;
            parme.player3 = player3;
            parme.player4 = player4;
            parme.player5 = player5;

            parme.oldplayer1 = oldPlayer1;
            parme.oldplayer2 = oldPlayer2;
            parme.oldplayer3 = oldPlayer3;
            parme.oldplayer4 = oldPlayer4;
            parme.oldplayer5 = oldPlayer5;

            parme.user1 = user1;
            parme.user2 = user2;
            parme.user3 = user3;
            parme.user4 = user4;
            parme.user5 = user5;

            parme.olduser1 = oldUser1;
            parme.olduser2 = oldUser2;
            parme.olduser3 = oldUser3;
            parme.olduser4 = oldUser4;
            parme.olduser5 = oldUser5;

            parme.pcw1 = p1cw;
            parme.pcw2 = p2cw;
            parme.pcw3 = p3cw;
            parme.pcw4 = p4cw;
            parme.pcw5 = p5cw;

            parme.oldpcw1 = oldp1cw;
            parme.oldpcw2 = oldp2cw;
            parme.oldpcw3 = oldp3cw;
            parme.oldpcw4 = oldp4cw;
            parme.oldpcw5 = oldp5cw;

            parme.guest_id1 = guest_id1;
            parme.guest_id2 = guest_id2;
            parme.guest_id3 = guest_id3;
            parme.guest_id4 = guest_id4;
            parme.guest_id5 = guest_id5;

            parme.oldguest_id1 = oldguest_id1;
            parme.oldguest_id2 = oldguest_id2;
            parme.oldguest_id3 = oldguest_id3;
            parme.oldguest_id4 = oldguest_id4;
            parme.oldguest_id5 = oldguest_id5;

            parme.userg1 = userg1;
            parme.userg2 = userg2;
            parme.userg3 = userg3;
            parme.userg4 = userg4;
            parme.userg5 = userg5;

            //
            //  Customs - add pro email addresses for all event notifications
            //
            parme.emailpro1 = proemail1;
            parme.emailpro2 = proemail2;

            if (club.equals("piedmont")) {

                parme.emailpro1 = "rgraham@drivingclub.com";     // Robert Graham
            }

            /*   Removed 7/29/08 - they will use the new event config option
            if (club.equals("gallerygolf")) {
            
            parme.emailpro1 = "mikekarpe@pga.com";         // Mike Karpe
            }
             */


            //
            //  Send the email
            //
            sendEmail.sendIt(parme, con);      // in common

        }     // end of IF sendemail
    }       // end of verify

    // ************************************************************************
    //  Process cancel request (Return w/o changes) from self
    // ************************************************************************
    private void cancelReq(HttpServletRequest req, PrintWriter out, Connection con) {


        //int count = 0;
        int id = 0;

        //
        // Get all the parameters entered
        //
        String name = req.getParameter("name");           //  name of event
        String course = req.getParameter("course");        //  name of course
        String sid = req.getParameter("id");               //  id of entry in evntsup table
        String index = req.getParameter("index");

        boolean ext_login = false;        // from external login

        if (req.getParameter("ext-login") != null) {         // if from external login

            ext_login = true;
        }

        //
        //  Convert the values from string to int
        //
        try {
            id = Integer.parseInt(sid);
        } catch (NumberFormatException e) {
            // ignore error
        }

        //
        //  Clear the 'in_use' flag for this entry
        //
        try {

            PreparedStatement pstmt1 = con.prepareStatement(
                    "UPDATE evntsup2b SET in_use = 0 WHERE id = ?"); //name = ? AND courseName = ? AND

            pstmt1.clearParameters();
            //pstmt1.setString(1, name);
            //pstmt1.setString(2, course);
            pstmt1.setInt(1, id);
            pstmt1.executeUpdate();

            pstmt1.close();

        } catch (Exception ignore) {
        }

        //
        //  Prompt user to return to Member_events
        //
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
        out.println("<Title>Member Event Registration Page</Title>");

        if (!index.equals("") && index.equals("0")) {
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_events2?name=" + name + "&course=" + course + "&index=" + index + "\">");
        } else if (ext_login == false) {
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_jump?name=" + name + "&course=" + course + "&index=" + index + "\">");
        }
        out.println("</HEAD>");
        out.println("<BODY bgcolor=\"#CCCCAA\">");
        out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
        out.println("<BR><BR>Thank you, the event entry has been returned to the system without changes.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        if (ext_login == true) {     // if came from external login (no frames)
            out.println("<form action=\"Member_events2\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
        } else if (!index.equals("") && index.equals("0")) {
            out.println("<form action=\"Member_events2\" method=\"post\">");
        } else {
            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
        }
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");

        out.println("</center></body></html>");
        out.close();
    }

    // *********************************************************
    //  Database Error
    // *********************************************************
    private void dbError(PrintWriter out, Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact your club manager.");
        out.println("<BR><BR>Exception: " + e1.getMessage());
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</center></body></html>");
        out.close();
    }

    // *********************************************************
    // Invalid data received - reject request
    // *********************************************************
    private void invData(PrintWriter out, String p1, String p2, String p3, String p4, String p5) {


        if (p1.equals("")) {

            p1 = " ";             // use space instead of null
        }

        if (p2.equals("")) {

            p2 = " ";             // use space instead of null
        }

        if (p3.equals("")) {

            p3 = " ";             // use space instead of null
        }

        if (p4.equals("")) {

            p4 = " ";             // use space instead of null
        }

        if (p5.equals("")) {

            p5 = " ";             // use space instead of null
        }

        out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
        out.println("<BODY bgcolor=\"#CCCCAA\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><H3>Invalid Data Received</H3><BR>");
        out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
        out.println("<BR>You entered:" + (!p1.equals(" ") ? "&nbsp;&nbsp;&nbsp;'" + p1 : "") + (!p2.equals(" ") ? "',&nbsp;&nbsp;&nbsp;'" + p2 : "") + 
                (!p3.equals(" ") ? "',&nbsp;&nbsp;&nbsp;'" + p3 : "") + (!p4.equals(" ") ? "',&nbsp;&nbsp;&nbsp;'" + p4 : "") + (!p5.equals(" ") ? "',&nbsp;&nbsp;&nbsp;'" + p5 : "") + "'");
        out.println("<BR><BR>");
        out.println("Please check the names and try again.");
        out.println("<BR><BR>");
        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");
        out.println("</center></body></html>");
        out.close();
        return;
    }

    // *********************************************************
    // Invalid data received - reject request
    // *********************************************************
    private void returnForm(String player1, String player2, String player3, String player4, String player5,
            String p1cw, String p2cw, String p3cw, String p4cw, String p5cw,
            String gender1, String gender2, String gender3, String gender4, String gender5,
            String ghin1, String ghin2, String ghin3, String ghin4, String ghin5,
            String homeclub1, String homeclub2, String homeclub3, String homeclub4, String homeclub5,
            String phone1, String phone2, String phone3, String phone4, String phone5,
            String address1, String address2, String address3, String address4, String address5,
            String email1, String email2, String email3, String email4, String email5,
            String shirt1, String shirt2, String shirt3, String shirt4, String shirt5,
            String shoe1, String shoe2, String shoe3, String shoe4, String shoe5,
            String otherA1[], String otherA2[], String otherA3[],
            String userg1, String userg2, String userg3, String userg4, String userg5,
            int guest_id1, int guest_id2, int guest_id3, int guest_id4, int guest_id5,
            String notes, String hides, String index, String course, String name,
            String rev, int id, int event_id, boolean ext_login, PrintWriter out) {

        out.println("<form action=\"Member_evntSignUp\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
        out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
        out.println("<input type=\"hidden\" name=\"gender1\" value=\"" + gender1 + "\">");
        out.println("<input type=\"hidden\" name=\"gender2\" value=\"" + gender2 + "\">");
        out.println("<input type=\"hidden\" name=\"gender3\" value=\"" + gender3 + "\">");
        out.println("<input type=\"hidden\" name=\"gender4\" value=\"" + gender4 + "\">");
        out.println("<input type=\"hidden\" name=\"gender5\" value=\"" + gender5 + "\">");
        out.println("<input type=\"hidden\" name=\"ghin1\" value=\"" + ghin1 + "\">");
        out.println("<input type=\"hidden\" name=\"ghin2\" value=\"" + ghin2 + "\">");
        out.println("<input type=\"hidden\" name=\"ghin3\" value=\"" + ghin3 + "\">");
        out.println("<input type=\"hidden\" name=\"ghin4\" value=\"" + ghin4 + "\">");
        out.println("<input type=\"hidden\" name=\"ghin5\" value=\"" + ghin5 + "\">");
        out.println("<input type=\"hidden\" name=\"homeclub1\" value=\"" + homeclub1 + "\">");
        out.println("<input type=\"hidden\" name=\"homeclub2\" value=\"" + homeclub2 + "\">");
        out.println("<input type=\"hidden\" name=\"homeclub3\" value=\"" + homeclub3 + "\">");
        out.println("<input type=\"hidden\" name=\"homeclub4\" value=\"" + homeclub4 + "\">");
        out.println("<input type=\"hidden\" name=\"homeclub5\" value=\"" + homeclub5 + "\">");
        out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
        out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
        out.println("<input type=\"hidden\" name=\"phone3\" value=\"" + phone3 + "\">");
        out.println("<input type=\"hidden\" name=\"phone4\" value=\"" + phone4 + "\">");
        out.println("<input type=\"hidden\" name=\"phone5\" value=\"" + phone5 + "\">");
        out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
        out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
        out.println("<input type=\"hidden\" name=\"address3\" value=\"" + address3 + "\">");
        out.println("<input type=\"hidden\" name=\"address4\" value=\"" + address4 + "\">");
        out.println("<input type=\"hidden\" name=\"address5\" value=\"" + address5 + "\">");
        out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
        out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
        out.println("<input type=\"hidden\" name=\"email3\" value=\"" + email3 + "\">");
        out.println("<input type=\"hidden\" name=\"email4\" value=\"" + email4 + "\">");
        out.println("<input type=\"hidden\" name=\"email5\" value=\"" + email5 + "\">");
        out.println("<input type=\"hidden\" name=\"shirt1\" value=\"" + shirt1 + "\">");
        out.println("<input type=\"hidden\" name=\"shirt2\" value=\"" + shirt2 + "\">");
        out.println("<input type=\"hidden\" name=\"shirt3\" value=\"" + shirt3 + "\">");
        out.println("<input type=\"hidden\" name=\"shirt4\" value=\"" + shirt4 + "\">");
        out.println("<input type=\"hidden\" name=\"shirt5\" value=\"" + shirt5 + "\">");
        out.println("<input type=\"hidden\" name=\"shoe1\" value=\"" + shoe1 + "\">");
        out.println("<input type=\"hidden\" name=\"shoe2\" value=\"" + shoe2 + "\">");
        out.println("<input type=\"hidden\" name=\"shoe3\" value=\"" + shoe3 + "\">");
        out.println("<input type=\"hidden\" name=\"shoe4\" value=\"" + shoe4 + "\">");
        out.println("<input type=\"hidden\" name=\"shoe5\" value=\"" + shoe5 + "\">");
        out.println("<input type=\"hidden\" name=\"other1A1\" value=\"" + otherA1[0] + "\">");
        out.println("<input type=\"hidden\" name=\"other2A1\" value=\"" + otherA1[1] + "\">");
        out.println("<input type=\"hidden\" name=\"other3A1\" value=\"" + otherA1[2] + "\">");
        out.println("<input type=\"hidden\" name=\"other4A1\" value=\"" + otherA1[3] + "\">");
        out.println("<input type=\"hidden\" name=\"other5A1\" value=\"" + otherA1[4] + "\">");
        out.println("<input type=\"hidden\" name=\"other1A2\" value=\"" + otherA2[0] + "\">");
        out.println("<input type=\"hidden\" name=\"other2A2\" value=\"" + otherA2[1] + "\">");
        out.println("<input type=\"hidden\" name=\"other3A2\" value=\"" + otherA2[2] + "\">");
        out.println("<input type=\"hidden\" name=\"other4A2\" value=\"" + otherA2[3] + "\">");
        out.println("<input type=\"hidden\" name=\"other5A2\" value=\"" + otherA2[4] + "\">");
        out.println("<input type=\"hidden\" name=\"other1A3\" value=\"" + otherA3[0] + "\">");
        out.println("<input type=\"hidden\" name=\"other2A3\" value=\"" + otherA3[1] + "\">");
        out.println("<input type=\"hidden\" name=\"other3A3\" value=\"" + otherA3[2] + "\">");
        out.println("<input type=\"hidden\" name=\"other4A3\" value=\"" + otherA3[3] + "\">");
        out.println("<input type=\"hidden\" name=\"other5A3\" value=\"" + otherA3[4] + "\">");
        out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + userg1 + "\">");
        out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + userg2 + "\">");
        out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + userg3 + "\">");
        out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + userg4 + "\">");
        out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + userg5 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
        out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");

        if (ext_login == true) {     // if came from external login (no frames)
            out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
        }

        out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");

        out.println("</form>");

    }

    private static void extraInfoForm(
            String player1, String player2, String player3, String player4, String player5,
            String userg1, String userg2, String userg3, String userg4, String userg5,
            String p1cw, String p2cw, String p3cw, String p4cw, String p5cw,
            String gender1, String gender2, String gender3, String gender4, String gender5,
            String ghin1, String ghin2, String ghin3, String ghin4, String ghin5,
            String homeclub1, String homeclub2, String homeclub3, String homeclub4, String homeclub5,
            String phone1, String phone2, String phone3, String phone4, String phone5,
            String address1, String address2, String address3, String address4, String address5,
            String email1, String email2, String email3, String email4, String email5,
            String shirt1, String shirt2, String shirt3, String shirt4, String shirt5,
            String shoe1, String shoe2, String shoe3, String shoe4, String shoe5,
            String player[], String gstA[], String homeclub[], String phone[], String address[], String email[], String shirt[], String shoe[], String otherA1[], String otherA2[], String otherA3[],
            int ask_homeclub, int ask_phone, int ask_address, int ask_email, int ask_shirtsize, int ask_shoesize, int ask_otherA1, int ask_otherA2, int ask_otherA3,
            String otherQ1, String otherQ2, String otherQ3, int who_shirtsize, int who_shoesize, int who_otherQ1, int who_otherQ2, int who_otherQ3, int players, int guests,
            int guest_id1, int guest_id2, int guest_id3, int guest_id4, int guest_id5,
            String notes, String hides, String index, String course, String name,
            String club, String rev, int signup_id, int event_id, boolean ext_login, PrintWriter out, boolean new_skin, List<String> messages) {

        //                  int req_homeclub, int req_phone, int req_address, int req_email, int req_shirtsize, int req_shoesize, 


        /*
         * NOTE: player[] and gstA[] are filtered -- use player1,2,3 etc., for unmodified player/guest fields.
         */
        int i = 0;

        String tmp_color = "yellow";

        if (club.equals("demobrock") || club.equals("blackstone")) {

            tmp_color = "chartreuse";
        }

        String tmp_style = "style=\"background-color:" + tmp_color + "\"";

        if (new_skin) {

            Map<String, Object> result_map = new LinkedHashMap<String, Object>();
            Map<String, Object> hidden_field_map = new LinkedHashMap<String, Object>();
            //Map<String, Object> callback_form_map = new LinkedHashMap<String, Object>();

            Gson gson_obj = new Gson();

            // Pull the arryas into local variable, incase we want to use them later
            String[] player_a = new String[]{player1, player2, player3, player4, player5};
            String[] pcw_a = new String[]{p1cw, p2cw, p3cw, p4cw, p5cw};
            int[] guest_id_a = new int[]{guest_id1, guest_id2, guest_id3, guest_id4, guest_id5};
            String[] userg_a = new String[]{userg1, userg2, userg3, userg4, userg5};
            String[] homeclub_a = new String[]{homeclub1, homeclub2, homeclub3, homeclub4, homeclub5};
            String[] phone_a = new String[]{phone1, phone2, phone3, phone4, phone5};
            String[] address_a = new String[]{address1, address2, address3, address4, address5};
            String[] email_a = new String[]{email1, email2, email3, email4, email5};
            String[] shirt_a = new String[]{shirt1, shirt2, shirt3, shirt4, shirt5};
            String[] shoe_a = new String[]{shoe1, shoe2, shoe3, shoe4, shoe5};
            String[] other_questions_a = new String[]{otherQ1, otherQ2, otherQ3};

            // Fill that field map with values that will be used when calling back
            hidden_field_map.put("skip8", "yes");
            hidden_field_map.put("sp", "yes");
            hidden_field_map.put("player%", player_a);
            hidden_field_map.put("p%cw", pcw_a);
            hidden_field_map.put("guest_id%", guest_id_a);
            hidden_field_map.put("gender%", new String[]{gender1, gender2, gender3, gender4, gender5});
            hidden_field_map.put("ghin%", new String[]{ghin1, ghin2, ghin3, ghin4, ghin5});
            hidden_field_map.put("userg%", userg_a);
            hidden_field_map.put("homeclub%", homeclub_a);
            hidden_field_map.put("phone%", phone_a);
            hidden_field_map.put("address%", address_a);
            hidden_field_map.put("email%", email_a);
            hidden_field_map.put("shirt%", shirt_a);
            hidden_field_map.put("shoe%", shoe_a);
            hidden_field_map.put("other%A1", otherA1);
            hidden_field_map.put("other%A2", otherA2);
            hidden_field_map.put("other%A3", otherA3);
            hidden_field_map.put("index", index);
            hidden_field_map.put("id", signup_id);
            hidden_field_map.put("event_id", event_id);
            hidden_field_map.put("course", course);
            hidden_field_map.put("notes", notes);
            hidden_field_map.put("hide", hides);
            hidden_field_map.put("name", name);
            hidden_field_map.put("submitForm", "YES - continue");
            if (ext_login == true) {
                hidden_field_map.put("ext-login", "yes");
            }

            // Fill the result map
            result_map.put("title", "[options.event.moreInfoTitle]");
            result_map.put("prompt_close_continue", true);
            result_map.put("successful", false);
            result_map.put("modal_width", 900);
            result_map.put("other_questions_a", other_questions_a);
            result_map.put("field_override_hidden_map", hidden_field_map);
            result_map.put("required_field_color", tmp_color);

            if (messages.size() > 0) {
                messages.add("[options.event.pleaseCorrect]");
                result_map.put("warning_head", "[options.event.missingInfoPrompt]");
                result_map.put("warning_list", messages);
            }

            result_map.put("message_array", new String[]{"[options.event.requiredPrompt]"});

            String form_html = "";

            // if we need to ask any of the 'guest only' questions
            if (guests > 0 && (ask_homeclub + ask_phone + ask_address + ask_email > 0)) {

                // Build the guest field list

                form_html += "<div class=\"main_instructions\"><h3>[options.event.guestInfoTitle]</h3>";

                form_html += "<table class=\"standard_list_table\"><thead><tr><th>[options.event.guestPrompt]</th>";
                if (ask_homeclub > 0) {
                    form_html += "<th>[options.event.homeclubPrompt]</th>";
                }
                if (ask_phone > 0) {
                    form_html += "<th>[options.event.phonePrompt]</th>";
                }
                if (ask_address > 0) {
                    form_html += "<th>[options.event.addressPrompt]</th>";
                }
                if (ask_email > 0) {
                    form_html += "<th>[options.event.emailPrompt]</th>";
                }
                form_html += "</tr></thead><tbody>";

                for (i = 0; i < players; i++) {

                    form_html += "<tr>";

                    if (!gstA[i].equals("")) {

                        form_html += "<td>" + gstA[i] + "</td>";
                        if (ask_homeclub > 0) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=20 maxlength=50 name=homeclub" + (i + 1) + " value=\"" + homeclub[i] + "\" " + ((ask_homeclub == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_phone > 0) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=11 maxlength=50 name=phone" + (i + 1) + " value=\"" + phone[i] + "\" " + ((ask_phone == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_address > 0) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=24 maxlength=64 name=address" + (i + 1) + " value=\"" + address[i] + "\" " + ((ask_address == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_email > 0) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=16 maxlength=64 name=email" + (i + 1) + " value=\"" + email[i] + "\" " + ((ask_email == 2) ? tmp_style : "") + "></div></td>";
                        }
                    }

                    form_html += "</tr>";
                }

                form_html += "</tbody></table></div>";

            }


            // if we need to ask any of the 'all players' questions
            if (ask_shirtsize + ask_shoesize + ask_otherA1 + ask_otherA2 + ask_otherA3 > 0) {

                form_html += "<div class=\"main_instructions\"><h3>[options.event.playerInfoTitle]</h3>";

                // table to display questions
                form_html += "[options.event.playerQuestionsOpen]";
                if (ask_otherA1 > 0 && !otherQ1.equals("")) {
                    form_html += "[options.event.question1]";
                }
                if (ask_otherA2 > 0 && !otherQ2.equals("")) {
                    form_html += "[options.event.question2]";
                }
                if (ask_otherA3 > 0 && !otherQ3.equals("")) {
                    form_html += "[options.event.question3]";
                }
                form_html += "[options.event.playerQuestionsClose]";

                // header row
                form_html += "<table class=\"standard_list_table\"><thead><tr><th>[options.event.playerPrompt]</th>";
                if (ask_shirtsize > 0) {
                    form_html += "<th>[options.event.shirtPrompt]</th>";
                }
                if (ask_shoesize > 0) {
                    form_html += "<th>[options.event.shoePrompt]</th>";
                }
                if (ask_otherA1 > 0 && !otherQ1.equals("")) {
                    form_html += "<th class=\"question\">[options.event.q1Prompt]</th>";
                }
                if (ask_otherA2 > 0 && !otherQ2.equals("")) {
                    form_html += "<th class=\"question\">[options.event.q2Prompt]</th>";
                }
                if (ask_otherA3 > 0 && !otherQ3.equals("")) {
                    form_html += "<th class=\"question\">[options.event.q3Prompt]</th>";
                }
                form_html += "</thead></tr><tbody>";

                for (i = 0; i < players; i++) {

                    form_html += "<tr>";

                    if (!player[i].equals("")) {

                        form_html += "<td>" + player[i] + "</td>";
                        if (ask_shirtsize > 0) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=6 maxlength=8 name=shirt" + (i + 1) + " value=\"" + shirt[i] + "\" " + ((ask_shirtsize == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_shoesize > 0) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=6 maxlength=8 name=shoe" + (i + 1) + " value=\"" + shoe[i] + "\" " + ((ask_shoesize == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_otherA1 > 0 && !otherQ1.equals("")) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=20 maxlength=50 name=other" + (i + 1) + "A1 value=\"" + otherA1[i] + "\" " + ((ask_otherA1 == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_otherA2 > 0 && !otherQ2.equals("")) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=20 maxlength=50 name=other" + (i + 1) + "A2 value=\"" + otherA2[i] + "\" " + ((ask_otherA2 == 2) ? tmp_style : "") + "></div></td>";
                        }
                        if (ask_otherA3 > 0 && !otherQ3.equals("")) {
                            form_html += "<td><div class=\"input_container\"><input type=text size=20 maxlength=50 name=other" + (i + 1) + "A3 value=\"" + otherA3[i] + "\" " + ((ask_otherA3 == 2) ? tmp_style : "") + "></div></td>";
                        }
                    }

                    form_html += "</tr>";
                }

                form_html += "</tbody></table></div>";

            }

            result_map.put("form_html", form_html);

            // Send results as json string
            out.print(gson_obj.toJson(result_map));

            out.close();

        } else { // Old Skin

            /*
            out.println("<table border=1 cols=1 bgcolor=\"#F5F5DC\" cellpadding=3>");
            out.println("<tr><td width=620 align=center><font size=2>");
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this event registration.");
            out.println("&nbsp; If you want to return without changes, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Cancel Sign-up</b> option below.<br>");
            out.println("<b>Note</b>:&nbsp;&nbsp;The questions with <b>" + tmp_color + " boxes are required</b> and need to be completed in order to complete your sign-up.");
            out.println("</font></td></tr>");
            out.println("</table><br>");

            out.println("<form action=\"Member_evntSignUp\" method=\"post\" target=\"_top\">");

            // if we need to ask any of the 'guest only' questions
            if (guests > 0 && (ask_homeclub + ask_phone + ask_address + ask_email > 0)) {

                out.println("<table border=1 bgcolor=\"#F5F5DC\" align=center>");
                out.println("<tr bgcolor=\"#336633\"><td align=center>");
                out.println("<font color=white size=3><b>Additional Guest Only Information</b></font></td>");
                out.println("</tr><tr><td>");

                out.println("<table><tr style=\"font-size:10pt;font-weight:bold\"><td>Guests</td>");
                if (ask_homeclub > 0) {
                    out.println("<td>Home Club, State</td>");
                }
                if (ask_phone > 0) {
                    out.println("<td>Phone</td>");
                }
                if (ask_address > 0) {
                    out.println("<td>Mailing Address</td>");
                }
                if (ask_email > 0) {
                    out.println("<td>Email</td>");
                }
                out.println("</tr>");

                for (i = 0; i < players; i++) {

                    out.println("<tr style=\"font-size:10pt\">");

                    if (!gstA[i].equals("")) {

                        out.println("<td>" + gstA[i] + "&nbsp; &nbsp;</td>");
                        if (ask_homeclub > 0) {
                            out.println("<td><input type=text size=20 maxlength=50 name=homeclub" + (i + 1) + " value=\"" + homeclub[i] + "\" " + ((ask_homeclub == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_phone > 0) {
                            out.println("<td><input type=text size=11 maxlength=20 name=phone" + (i + 1) + " value=\"" + phone[i] + "\" " + ((ask_phone == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_address > 0) {
                            out.println("<td><input type=text size=24 maxlength=64 name=address" + (i + 1) + " value=\"" + address[i] + "\" " + ((ask_address == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_email > 0) {
                            out.println("<td><input type=text size=16 maxlength=50 name=email" + (i + 1) + " value=\"" + email[i] + "\" " + ((ask_email == 2) ? tmp_style : "") + "></td>");
                        }
                    }

                    out.println("</tr>");
                }

                out.println("</table>");
                out.println("</td></tr>");
                out.println("</table>");

                out.println("<br>");

            }

            // if we need to ask any of the 'all players' questions
            if (ask_shirtsize + ask_shoesize + ask_otherA1 + ask_otherA2 + ask_otherA3 > 0) {

                out.println("<table border=1 bgcolor=\"#F5F5DC\" align=center>");
                out.println("<tr bgcolor=\"#336633\"><td align=center>");
                out.println("<font color=white size=3><b>Additional Player Information</b></font></td>");
                out.println("</tr><tr><td>");

                // table to display questions
                out.println("<br><table align=center style=\"border: #929292 dashed 1px\"><tr><td align=center><b>Questions:</b></td></tr>");
                if (ask_otherA1 > 0 && !otherQ1.equals("")) {
                    out.println("<tr><td><font size=2>&nbsp;1. " + otherQ1 + "&nbsp;</font></td></tr>");
                }
                if (ask_otherA2 > 0 && !otherQ2.equals("")) {
                    out.println("<tr><td><font size=2>&nbsp;2. " + otherQ2 + "&nbsp;</font></td></tr>");
                }
                if (ask_otherA3 > 0 && !otherQ3.equals("")) {
                    out.println("<tr><td><font size=2>&nbsp;3. " + otherQ3 + "&nbsp;</font></td></tr>");
                }
                out.println("</table><br>");

                // header row
                out.println("<table><tr style=\"font-size:10pt;font-weight:bold\"><td>Players</td>");
                if (ask_shirtsize > 0) {
                    out.println("<td>Shirt Size</td>");
                }
                if (ask_shoesize > 0) {
                    out.println("<td>Shoe Size</td>");
                }
                if (ask_otherA1 > 0 && !otherQ1.equals("")) {
                    out.println("<td>Question #1</td>");
                }
                if (ask_otherA2 > 0 && !otherQ2.equals("")) {
                    out.println("<td>Question #2</td>");
                }
                if (ask_otherA3 > 0 && !otherQ3.equals("")) {
                    out.println("<td>Question #3</td>");
                }
                out.println("</tr>");

                for (i = 0; i < players; i++) {

                    out.println("<tr style=\"font-size:10pt\">");

                    if (!player[i].equals("")) {

                        out.println("<td>" + player[i] + "&nbsp; &nbsp;</td>");
                        if (ask_shirtsize > 0) {
                            out.println("<td><input type=text size=6 maxlength=8 name=shirt" + (i + 1) + " value=\"" + shirt[i] + "\" " + ((ask_shirtsize == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_shoesize > 0) {
                            out.println("<td><input type=text size=6 maxlength=8 name=shoe" + (i + 1) + " value=\"" + shoe[i] + "\" " + ((ask_shoesize == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_otherA1 > 0 && !otherQ1.equals("")) {
                            out.println("<td><input type=text size=20 maxlength=24 name=other" + (i + 1) + "A1 value=\"" + otherA1[i] + "\" " + ((ask_otherA1 == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_otherA2 > 0 && !otherQ2.equals("")) {
                            out.println("<td><input type=text size=20 maxlength=24 name=other" + (i + 1) + "A2 value=\"" + otherA2[i] + "\" " + ((ask_otherA2 == 2) ? tmp_style : "") + "></td>");
                        }
                        if (ask_otherA3 > 0 && !otherQ3.equals("")) {
                            out.println("<td><input type=text size=20 maxlength=24 name=other" + (i + 1) + "A3 value=\"" + otherA3[i] + "\" " + ((ask_otherA3 == 2) ? tmp_style : "") + "></td>");
                        }
                    }

                    out.println("</tr>");
                }

                out.println("</table>");

            }

            /*
            if (ask_otherA1 > 0 && !otherQ1.equals("")) {
            out.println("<table>");
            //out.println("<tr style=\"font-size:10pt;font-weight:bold\"><td></td><td>Custom Question #1</td></tr>");
            
            for (i = 0; i < players; i++) {
            
            out.println("<tr style=\"font-size:10pt\">");
            
            if (!gstA[i].equals("")) {
            
            out.println("<td>" + gstA[i] + "&nbsp; &nbsp;</td>");
            out.println("<td>" + otherQ1 + "&nbsp; &nbsp;</td>");
            out.println("<td><input type=text size=20 name=other" + (i + 1) + "A1 value=\"" + otherA1[i] + "\" " + ((ask_otherA1 == 2) ? tmp_style : "") + "></td>");
            }
            
            out.println("</tr>");
            }
            out.println("</table>");
            }
            
            if (ask_otherA2 > 0 && !otherQ2.equals("")) {
            out.println("<table>");
            //out.println("<tr style=\"font-size:10pt;font-weight:bold\"><td></td><td>Custom Question #2</td></tr>");
            
            for (i = 0; i < players; i++) {
            
            out.println("<tr style=\"font-size:10pt\">");
            
            if (!gstA[i].equals("")) {
            
            out.println("<td>" + gstA[i] + "&nbsp; &nbsp;</td>");
            out.println("<td>" + otherQ2 + "&nbsp; &nbsp;</td>");
            out.println("<td><input type=text size=20 name=other" + (i + 1) + "A2 value=\"" + otherA2[i] + "\" " + ((ask_otherA2 == 2) ? tmp_style : "") + "></td>");
            }
            
            out.println("</tr>");
            }
            out.println("</table>");
            }
            
            if (ask_otherA3 > 0 && !otherQ3.equals("")) {
            out.println("<table>");
            //out.println("<tr style=\"font-size:10pt;font-weight:bold\"><td></td><td>Custom Question #3</td></tr>");
            
            for (i = 0; i < players; i++) {
            
            out.println("<tr style=\"font-size:10pt\">");
            
            if (!gstA[i].equals("")) {
            
            out.println("<td>" + gstA[i] + "&nbsp; &nbsp;</td>");
            out.println("<td>" + otherQ3 + "&nbsp; &nbsp;</td>");
            out.println("<td><input type=text size=20 name=other" + (i + 1) + "A3 value=\"" + otherA3[i] + "\" " + ((ask_otherA3 == 2) ? tmp_style : "") + "></td>");
            }
            
            out.println("</tr>");
            }
            out.println("</table>");
            }

            out.println("</td></tr>");
            out.println("</table>");
            out.println("<br>");

            out.println("<input type=\"hidden\" name=\"sp\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">"); // to skip the guest association prompt

            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
            out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
            out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + userg1 + "\">");
            out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + userg2 + "\">");
            out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + userg3 + "\">");
            out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + userg4 + "\">");
            out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + userg5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"gender1\" value=\"" + gender1 + "\">");
            out.println("<input type=\"hidden\" name=\"gender2\" value=\"" + gender2 + "\">");
            out.println("<input type=\"hidden\" name=\"gender3\" value=\"" + gender3 + "\">");
            out.println("<input type=\"hidden\" name=\"gender4\" value=\"" + gender4 + "\">");
            out.println("<input type=\"hidden\" name=\"gender5\" value=\"" + gender5 + "\">");
            out.println("<input type=\"hidden\" name=\"ghin1\" value=\"" + ghin1 + "\">");
            out.println("<input type=\"hidden\" name=\"ghin2\" value=\"" + ghin2 + "\">");
            out.println("<input type=\"hidden\" name=\"ghin3\" value=\"" + ghin3 + "\">");
            out.println("<input type=\"hidden\" name=\"ghin4\" value=\"" + ghin4 + "\">");
            out.println("<input type=\"hidden\" name=\"ghin5\" value=\"" + ghin5 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

            if (ext_login == true) {     // if came from external login (no frames)
                out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
            }
            
            out.println("<input type=\"hidden\" name=\"homeclub1\" value=\"" + homeclub1 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub2\" value=\"" + homeclub2 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub3\" value=\"" + homeclub3 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub4\" value=\"" + homeclub4 + "\">");
            out.println("<input type=\"hidden\" name=\"homeclub5\" value=\"" + homeclub5 + "\">");
            out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
            out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
            out.println("<input type=\"hidden\" name=\"phone3\" value=\"" + phone3 + "\">");
            out.println("<input type=\"hidden\" name=\"phone4\" value=\"" + phone4 + "\">");
            out.println("<input type=\"hidden\" name=\"phone5\" value=\"" + phone5 + "\">");
            out.println("<input type=\"hidden\" name=\"address1\" value=\"" + address1 + "\">");
            out.println("<input type=\"hidden\" name=\"address2\" value=\"" + address2 + "\">");
            out.println("<input type=\"hidden\" name=\"address3\" value=\"" + address3 + "\">");
            out.println("<input type=\"hidden\" name=\"address4\" value=\"" + address4 + "\">");
            out.println("<input type=\"hidden\" name=\"address5\" value=\"" + address5 + "\">");
            out.println("<input type=\"hidden\" name=\"email1\" value=\"" + email1 + "\">");
            out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
            out.println("<input type=\"hidden\" name=\"email3\" value=\"" + email3 + "\">");
            out.println("<input type=\"hidden\" name=\"email4\" value=\"" + email4 + "\">");
            out.println("<input type=\"hidden\" name=\"email5\" value=\"" + email5 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt1\" value=\"" + shirt1 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt2\" value=\"" + shirt2 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt3\" value=\"" + shirt3 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt4\" value=\"" + shirt4 + "\">");
            out.println("<input type=\"hidden\" name=\"shirt5\" value=\"" + shirt5 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe1\" value=\"" + shoe1 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe2\" value=\"" + shoe2 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe3\" value=\"" + shoe3 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe4\" value=\"" + shoe4 + "\">");
            out.println("<input type=\"hidden\" name=\"shoe5\" value=\"" + shoe5 + "\">");
            

            out.println("<table align=center><tr><td align=center>");
            out.println("<input type=\"submit\" value=\"Complete Sign-Up\" name=\"submitForm\" style=\"text-decoration:underline;\">");
            out.println("</form></td>");
            out.println("</tr><tr>");
            out.println("<form action=\"Member_evntSignUp\" method=\"post\" name=\"can\"><td align=center><br>");
            //out.println("</td><td></td><td>");
            out.println("<input type=\"hidden\" name=\"id\" value=" + signup_id + ">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            if (ext_login == true) {     // if came from external login (no frames)
                out.println("<input type=\"hidden\" name=\"ext-login\" value=\"yes\">");
            }
            out.println("<input type=\"submit\" value=\"Cancel Changes\" name=\"cancel\"></form>");
            out.println("</td></tr></table>");
            */
        }

    }
} // end class file
