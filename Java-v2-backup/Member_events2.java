
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
import com.foretees.common.Common_skin;
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
import com.foretees.common.Connect;
import com.foretees.common.reservationUtil;
import com.foretees.common.timeUtil;
import com.foretees.common.reservationPrompt;

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

        HttpSession session = null;

        boolean json_mode = Utilities.getParameterString(req,"jsonMode",Utilities.getParameterString(req,"json_mode",null)) != null; // Will we output JSON or HTML

        boolean ext_login = false;            // not external login (from email link)

        if (Utilities.getSessionString(req, "ext-user", null) != null) {   // if from Login or Member_evntSignUp for an external login user

            ext_login = true;        // member came from link in email message  (via Login.ProcessExtLogin)
            session = SystemUtils.verifyMem(req, out, true);       // validate external login 

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
        //String caller = (String) session.getAttribute("caller");  // get caller (web site)
        //String mtype = (String) session.getAttribute("mtype");    // member's mtype 
        //String mship = (String) session.getAttribute("mship");    // member's mship type

        int activity_id = (Integer) session.getAttribute("activity_id");

        Connection con = Connect.getCon(req);            // get DB connection

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

        int event_id = Utilities.getParameterInteger(req, "id", Utilities.getParameterInteger(req, "event_id", 0));

        // if not json mode and not an external user than this is either old skin or a flexscape user
        // coming from the intergrated calendar
        // external users should be able to use this as well so they have the new skin look
        if (!json_mode && !ext_login) {


            Common_skin.outputHeader(club, activity_id, "Event Detail Page", false, out, req);

            out.println("<script type=\"text/javascript\">");
            out.println("$(document).ready(function() {");
            out.println(" setTimeout(function(){$(\"#fakeClick\").click();},100);");
            out.println("});");
            out.println("</script>");
            out.println("</head>");

            Common_skin.outputBody(club, activity_id, out, req);
            //Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
            Common_skin.outputBanner(club, activity_id, Utilities.getClubName(con, true), (String)session.getAttribute("zipcode"), out, req);
            Common_skin.outputSubNav(club, activity_id, out, con, req);
            Common_skin.outputPageStart(club, activity_id, out, req);
            Common_skin.outputBreadCrumb (club, activity_id, out, "Event Details", req);
            Common_skin.outputLogo(club, activity_id, out, req);
            

            // Output a link that will be clicked immediatly
            out.println("<a id=\"fakeClick\" class=\"event_button\" href=\"#\" data-ftjson=\""+reservationUtil.linkJsonEsc(event_id, activity_id)+"\"></a><br><br>");
            
            Common_skin.outputPageEnd(club, activity_id, out, req);

            return;
        }
        
        
        reservationPrompt event = null;
        if(Utilities.getParameterString(req, "type","").equalsIgnoreCase("dining_event")){
            // Dining event
            event = reservationUtil.getDiningEventPrompt(req, event_id, user);
        } else if(Utilities.getParameterString(req, "type","").equalsIgnoreCase("dining_reservation_list")) { 
            // Request to get list of all current dining reservations (event or otherwise) for the date specified
            int date = Utilities.getParameterInteger(req, "date", 0);
            event = reservationUtil.getDiningSignupsByDate(req, date, user);
        } else {
            // Golf or flxrez event
            event = reservationUtil.getEventPrompt(req, event_id, user);
        }
        if(event != null){
            Gson gson_obj = new Gson();
            out.print(gson_obj.toJson(event));
        } else {
            resp.setStatus(500);
            out.print("<html><head><title>Error connecting to database</title></head><body>Error connecting to database.</body></html>");
            return;
        }

    }   // end of doPost
}
