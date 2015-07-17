/***************************************************************************************
 *   Proshop_evntSignUp:  This servlet will process a request to register a member for
 *                        an event.
 *
 *
 *
 *   called by:  Proshop_events2
 *
 *
 *   created: 2/18/2003   Bob P.
 *
 *   last updated:
 
 *       2/27/14   Merion GC (merion) - Added custom to restrict members to only signing up to a certain number of "Mens Stag Day" and "Mens Member Guest" events at a given time, based on the current date (case 2369).
 *       1/31/14   Updated email/phone/other extra info fields for event signups to allow for more characters.
 *       9/06/13   BallenIsles CC (ballenisles) - Added custom to prevent the automated move-up of groups off of the event wait list (case 2296).
 *       7/17/13   Updated calls to create an event log entry to also pass an email_suppressed indicator to be displayed when viewing the event log.
 *       4/05/13   Fixed issue where userg values were not getting populated into the email parameter block, which was causing tracked guest email notifications to not send out.
 *       1/10/13   Undo the disabling of the guest db for iPad users
 *      11/27/12   Tweak iframe resize code
 *       9/18/12   Player names and MoTs will now be included in the email notification indicating a team has been moved off of the wait list for an event.
 *      11/28/11   Olympic Club (olyclub) - Display "GHIN #" instead of "HDCP #" on the event signup page (case 2074).
 *      11/17/11   Updated checkMemNotice() call to the updated call that includes sess_activity_id to prevent golf messages appearing for FlxRez events, and vice-versa.
 *      10/06/11   Modified alphaTable.nameList() calls to pass an additional parameter used for displaying inact members when modifying past tee times.
 *       4/21/11   Added processing to clear out any modes of transportation before updating an event signup for FlxRez events.
 *       3/12/11   Added calls to utilize the event signup log
 *       1/07/11   Fix to catch null fb values on FlxRez sites
 *      10/20/10   Populate new parmEmail fields
 *       6/24/10   Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *       6/08/10   Added code to guest tracking verification to allow for the guest "TBA" option
 *       6/03/10   For non-golf activity ids, look up the activity name to use in "____ Shop Event Sign Up" message
 *       5/26/10   Shift the players up when one or more players are removed from request and other players follow the empty slot(s).
 *                 There must be a player in slot1 if any exist in the request - required by drag-n-drop.
 *       5/20/10   Portland GC - back out the wiatlist custom.
 *       5/14/10   Fixed the fix from 5/13/10 so event_id is passed to the intermediate/conditional landing pages properly
 *       5/13/10   Fixed issue where event signup id was getting passed in place of the event id for verifySlot.checkEventRests() method
 *       4/23/10   Portland GC - No automated wait list move ups (Case# 1811).
 *       4/23/10   Correct the wait list processing so entries are taken off the wait list when a group cancels.
 *                 There was an invalid field (activity_id) in the WHERE clause of a query for evntsup2.
 *       4/13/10   Changes to verify method for FlxRez and unlimitied guest types
 *       4/07/10   Added guest tracking processing
 *       3/24/10   Added member_side parameter to alphaTable.guestList() call.  It was calling a method that no longer exists
 *       1/29/10   Include event signups for CC of Naples custom membership type quota restriction (case 1704).
 *      12/08/09   Do not delete events or event signups - mark them inactive instead so we can easily restore them.
 *      10/23/09   At the end of the movename script we now send focus back to the DYN_search text box
 *       9/30/09   Add support for Activities
 *       7/30/09   Add focus setting for moveguest js method to improve efficiency and disabled return key in player positions
 *       7/14/09   Desert Forest GC (desertforestgolfclub) - Default 'Suppress Emails' checkbox to checked/yes (case 1701).
 *       5/15/09   Add maxlength to text boxes in extraInfoForm (also increased size of db fields for clubname & address)
 *       5/14/09   Do not verify tmodes or pro only tmodes for season long events
 *       5/13/09   Shadow Ridge - default to hide notes and suppress emails.
 *       5/12/09   Do not copy over a member's default tmode if a season long event.
 *       2/17/09   Corrected Guest Name check and added message if hdcp or gender missing and export type selected
 *       2/11/09   Custom for Hazeltine - do not allow members to signup for Invitational unless they
 *                 have a sub-type of Invite Priority (case 1585).
 *      11/26/08   Changed required color for Blackstone
 *      11/26/08   Fixed defaulting of gender/ghin for members with no default tmode set
 *      11/10/08   Added changes for additional signup information
 *      10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *       9/22/08   Bay Hill - always set 'hidenotes' to Yes (case 1549).
 *       8/19/08   Remove synchronized mechanisms and change evntsup2b id field to a unique auto-incrementing field.
 *       7/29/08   Remove custom for Gallery (email pro all notifications) - they will use the new config option.
 *       5/09/08   Medinah CC - No automated wait list move ups for Medinah (Case# 1475)
 *       5/05/08   Gather additional guest information for events configured for export
 *       4/02/08   Suppress the c/w option for season long events
 *       3/27/08   Added support for new event fields, season, email1, email2 - Case 1372 & 1408
 *       3/07/08   Piedmont & Gallery - add pro emails to all notifications (cases 1395 & 1398).
 *       9/25/07   Add enforcement of new minimum sign-up size option
 *       8/20/07   Added Member Notice proshop side display as a configurable option (per notice basis)
 *       8/08/07   Muirfield & Inverness GC - Added Member Notice display to Proshop side
 *       7/27/07   Add option to suppress emails for the signup.
 *       6/28/07   Do not meta-refresh the reply when team is on wait list (let pro read the message).
 *       6/26/07   When pulling a handicap from member2b get it from the g_hancap field instead of c_hancap
 *       6/21/07   Do not send emails if event has already passed.
 *       4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *       4/09/07   Modified call to alphaTable.guestList to pass new boolean parameter
 *       3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *       2/15/07   Modified the call to alphaTable.nameList to include new boolean for ghin integration
 *       2/15/07   Set the clubname and course name for getClub.getParms.
 *       1/16/07   Royal Oaks Houston - always set 'hidenotes' to Yes.
 *       6/09/06   Added confirmation to cancel request
 *       3/07/06   Add ability to list names by mship and/or mtype groups.
 *       2/28/06   Merion - color 'House' members' names red in the name lists.
 *       9/15/05   Medinah - do not show guest types that are not for events.
 *       5/16/05   Add processing for restrictions on events (member may not have access to event).
 *      11/16/04   Ver 5 - Improve layout to provide quicker access to member names.
 *       9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *       7/25/04   Make sure there is room in event before changing wait to registered after
 *                 a group is cancelled.
 *       6/10/04   Add 'List All' option to member list to list all members for name selection.
 *       6/01/04   Remove error when pro enters a guest name without the guest type.
 *       2/16/04   Add support for configurable transportation modes.
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

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;
import com.foretees.common.congressionalCustom;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_evntSignUp extends HttpServlet {
                         

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
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   //
   //  Get this session's username 
   //
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   String mshipOpt = (String)session.getAttribute("mshipOpt");
   String mtypeOpt = (String)session.getAttribute("mtypeOpt");

   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   
   if (mshipOpt.equals( "" ) || mshipOpt == null) {

      mshipOpt = "ALL";
   }
   if (mtypeOpt.equals( "" ) || mtypeOpt == null) {

      mtypeOpt = "ALL";
   }



   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   // Process request according to which 'submit' button was selected
   //
   //      'id'      - a request from Proshop_events2
   //      'cancel'  - a cancel request from self (return with no changes)
   //      'letter'  - a request to list member names (from self)
   //      'submitForm'  - a reservation request (from self)
   //      'remove'  - a 'cancel reservation' request (from self - Cancel Tee Time)
   //      'return'  - a return from verify (from a skip)
   //
   if (req.getParameter("cancel") != null) {

      cancelReq(req, out, con);                      // process cancel request
      return;
   }


   //
   //  if user submitting request to update or cancel the entry - go process
   //
   if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session, resp);                 // process reservation requests request
      return;
   }

   //
   //   Request is from Proshop_events2 -or- its a letter or buddy request from self
   //
   String name = "";
   String pairings = "";
   String course = "";
   String act_ampm = "";
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
   String notes = "";
   String hides = "no";
   String sfb = "";
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
   int month  = 0;
   int day = 0;
   int year = 0;
   int act_hr = 0;
   int act_min = 0;
   int size = 0;
   int max = 0;
   int guests = 0;
   int count = 0;
   int count2 = 0;
   //int in_use = 0;
   int id = 0;
   int hide = 0;
   //int xCount = 0;
   int x = 0;
   int i = 0;
   //int nowc = 0;
   int time = 0;
   int c_time = 0;
   int fb = 0;
   int season = 0;
   int export_type = 0;
   int ask_more = 0;
   int ask_hdcp = 0;
   int ask_gender = 0;
   int guest_id1 = 0;
   int guest_id2 = 0;
   int guest_id3 = 0;
   int guest_id4 = 0;
   int guest_id5 = 0;

   int [] tmode = new int [16];      // supported transportation modes for event

   long date = 0;
   long c_date = 0;
   //float hndcp1 = 0;
   //float hndcp2 = 0;
   //float hndcp3 = 0;
   //float hndcp4 = 0;
   //float hndcp5 = 0;

   //boolean guestError = false;            // init error flag

   boolean enableAdvAssist = Utilities.enableAdvAssist(req);
   //boolean force_evnt_log_mode = false;
   
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block


   //
   //   Get the parms received
   //
   name = req.getParameter("name");
   course = req.getParameter("course");

   String suppressEmails = "no";
   if (req.getParameter("suppressEmails") != null) {

      suppressEmails = req.getParameter("suppressEmails");
      
   } else {
      
      //
      //    Custom - default to yes for suppress emails
      //
      if (club.equals( "shadowridgecc" ) || club.equals( "desertforestgolfclub")) {

         suppressEmails = "yes";
      }      
   }
   
   //
   //   Save club info in club parm table
   //
   parm.club = club;
   parm.course = course;


   try {

      //
      //  First, count the number of players already signed up 
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5 " +
         "FROM evntsup2b " +
         "WHERE name = ? AND courseName = ? AND inactive = 0");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      pstmt.setString(2, course);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      while (rs.next()) {

         player1 = rs.getString(1);
         player2 = rs.getString(2);
         player3 = rs.getString(3);
         player4 = rs.getString(4);
         player5 = rs.getString(5);

         if (!player1.equals( "" )) {

            count++;
         }
         if (!player2.equals( "" )) {

            count++;
         }
         if (!player3.equals( "" )) {

            count++;
         }
         if (!player4.equals( "" )) {

            count++;
         }
         if (!player5.equals( "" )) {

            count++;
         }
      }
      pstmt.close();
      
      //
      //   get the event requested
      //
      PreparedStatement stmt = con.prepareStatement (
         "SELECT * FROM events2b " +
         "WHERE name = ? AND courseName = ? AND activity_id = ?");

      stmt.clearParameters();
      stmt.setString(1, name);
      stmt.setString(2, course);
      stmt.setInt(3, sess_activity_id);
      rs = stmt.executeQuery();

      if (rs.next()) {

         event_id = rs.getInt("event_id");
         date = rs.getLong("date");
         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         pairings = rs.getString("pairings");
         size = rs.getInt("size");
         max = rs.getInt("max");
         guests = rs.getInt("guests");
         c_date = rs.getLong("c_date");
         c_time = rs.getInt("c_time");
         sfb = (rs.getString("fb") != null ? rs.getString("fb") : "");
         season = rs.getInt("season");
         ask_gender = rs.getInt("ask_gender");
         ask_hdcp = rs.getInt("ask_hdcp");
         export_type = rs.getInt("export_type");
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

         if (rs.getString("fb") != null) {
             sfb = rs.getString("fb");
         } else {

         }

      } else {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact support.");
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }   // end of IF event found

      stmt.close();

      //
      //  Get the Modes of Trans for this course
      //
      getParms.getCourse(con, parmc, course);

      //
      // Get the first valid tmode option
      //
      tmode_loop:
      for (i=0; i<16; i++) {
        if (tmode[i] == 1) {
           if (!parmc.tmodea[i].equals( "" )) {
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
      if (!pairings.equalsIgnoreCase( "Member" )) {

         size = 1;       // set size to one for proshop pairings (size is # per team)
      }

      if (req.getParameter("new") != null) {     // if 'new' request

         //
         //  Get the highest existing id from the sign up sheet and insert a new entry
         //
         id = getNewId(con, name, course, c_date, c_time);      // allocate a new entry

         if (id == 0) {

            out.println(SystemUtils.HeadTitle("Event Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Event Sign Up Error</H3>");
            out.println("<BR><BR>Sorry, we were unable to allocate a new entry for this event.");
            out.println("<BR><BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact support.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }

         //Utilities.createEventLogEntry(user, event_id, id, "CREATE", "New event signup created.", con);
         //force_evnt_log_mode = true;

      } else {   // not 'new' request

         if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) ||
             (req.getParameter("mtypeopt") != null) || (req.getParameter("memNotice") != null)) {                                        // if user clicked on a name letter

            sid = req.getParameter("id");

         } else {

            //
            //    The name of the submit button contains the 'id' of the entry in the event sign up table
            //
            Enumeration enum1 = req.getParameterNames();     // get the parm name passed

            while (enum1.hasMoreElements()) {

               pname = (String) enum1.nextElement();             // get parm name

               if (pname.startsWith( "id" )) {

                  StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon

                  sid = tok.nextToken();                        // skip past 'id: '
                  sid = tok.nextToken();                        // skip past 'id: '
               }
            }
         }                             // end of IF letter or buddy

         id = Integer.parseInt(sid);                   // convert id from string

      }   // end of IF 'new' request

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact support (provide the following).");
      out.println("<BR><BR> id = " + id + "   sid = " + sid + "   pname = " + pname);
      out.println("<BR><BR>Exception: " + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //   Process both new and update requests (new entry has already been added)
   //
   //      id = id of entry to update
   //      name = name of event
   //      course = name of course
   //      user = username of this Proshop
   //
   //   First, set this entry in_use if not already
   //
   if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) ||
       (req.getParameter("mtypeopt") != null) || (req.getParameter("memNotice") != null)) {   // if user clicked on a name letter or mtype

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
      notes = req.getParameter("notes");
      gender1 = (req.getParameter("gender1") != null) ? req.getParameter("gender1") : "";
      gender2 = (req.getParameter("gender2") != null) ? req.getParameter("gender2") : "";
      gender3 = (req.getParameter("gender3") != null) ? req.getParameter("gender3") : "";
      gender4 = (req.getParameter("gender4") != null) ? req.getParameter("gender4") : "";
      gender5 = (req.getParameter("gender5") != null) ? req.getParameter("gender5") : "";
      ghin1 = (req.getParameter("ghin1") != null) ? req.getParameter("ghin1") : "";
      ghin2 = (req.getParameter("ghin2") != null) ? req.getParameter("ghin2") : "";
      ghin3 = (req.getParameter("ghin3") != null) ? req.getParameter("ghin3") : "";
      ghin4 = (req.getParameter("ghin4") != null) ? req.getParameter("ghin4") : "";
      ghin5 = (req.getParameter("ghin5") != null) ? req.getParameter("ghin5") : "";
      homeclub1 = (req.getParameter("homeclub1") != null) ? req.getParameter("homeclub1") : "";
      homeclub2 = (req.getParameter("homeclub2") != null) ? req.getParameter("homeclub2") : "";
      homeclub3 = (req.getParameter("homeclub3") != null) ? req.getParameter("homeclub3") : "";
      homeclub4 = (req.getParameter("homeclub4") != null) ? req.getParameter("homeclub4") : "";
      homeclub5 = (req.getParameter("homeclub5") != null) ? req.getParameter("homeclub5") : "";
      phone1 = (req.getParameter("phone1") != null) ? req.getParameter("phone1") : "";
      phone2 = (req.getParameter("phone2") != null) ? req.getParameter("phone2") : "";
      phone3 = (req.getParameter("phone3") != null) ? req.getParameter("phone3") : "";
      phone4 = (req.getParameter("phone4") != null) ? req.getParameter("phone4") : "";
      phone5 = (req.getParameter("phone5") != null) ? req.getParameter("phone5") : "";
      address1 = (req.getParameter("address1") != null) ? req.getParameter("address1") : "";
      address2 = (req.getParameter("address2") != null) ? req.getParameter("address2") : "";
      address3 = (req.getParameter("address3") != null) ? req.getParameter("address3") : "";
      address4 = (req.getParameter("address4") != null) ? req.getParameter("address4") : "";
      address5 = (req.getParameter("address5") != null) ? req.getParameter("address5") : "";
      email1 = (req.getParameter("email1") != null) ? req.getParameter("email1") : "";
      email2 = (req.getParameter("email2") != null) ? req.getParameter("email2") : "";
      email3 = (req.getParameter("email3") != null) ? req.getParameter("email3") : "";
      email4 = (req.getParameter("email4") != null) ? req.getParameter("email4") : "";
      email5 = (req.getParameter("email5") != null) ? req.getParameter("email5") : "";
      shirt1 = (req.getParameter("shirt1") != null) ? req.getParameter("shirt1") : "";
      shirt2 = (req.getParameter("shirt2") != null) ? req.getParameter("shirt2") : "";
      shirt3 = (req.getParameter("shirt3") != null) ? req.getParameter("shirt3") : "";
      shirt4 = (req.getParameter("shirt4") != null) ? req.getParameter("shirt4") : "";
      shirt5 = (req.getParameter("shirt5") != null) ? req.getParameter("shirt5") : "";
      shoe1 = (req.getParameter("shoe1") != null) ? req.getParameter("shoe1") : "";
      shoe2 = (req.getParameter("shoe2") != null) ? req.getParameter("shoe2") : "";
      shoe3 = (req.getParameter("shoe3") != null) ? req.getParameter("shoe3") : "";
      shoe4 = (req.getParameter("shoe4") != null) ? req.getParameter("shoe4") : "";
      shoe5 = (req.getParameter("shoe5") != null) ? req.getParameter("shoe5") : "";
      other1A1 = (req.getParameter("other1A1") != null) ? req.getParameter("other1A1") : "";
      other1A2 = (req.getParameter("other1A2") != null) ? req.getParameter("other1A2") : "";
      other1A3 = (req.getParameter("other1A3") != null) ? req.getParameter("other1A3") : "";
      other2A1 = (req.getParameter("other2A1") != null) ? req.getParameter("other2A1") : "";
      other2A2 = (req.getParameter("other2A2") != null) ? req.getParameter("other2A2") : "";
      other2A3 = (req.getParameter("other2A3") != null) ? req.getParameter("other2A3") : "";
      other3A1 = (req.getParameter("other3A1") != null) ? req.getParameter("other3A1") : "";
      other3A2 = (req.getParameter("other3A2") != null) ? req.getParameter("other3A2") : "";
      other3A3 = (req.getParameter("other3A3") != null) ? req.getParameter("other3A3") : "";
      other4A1 = (req.getParameter("other4A1") != null) ? req.getParameter("other4A1") : "";
      other4A2 = (req.getParameter("other4A2") != null) ? req.getParameter("other4A2") : "";
      other4A3 = (req.getParameter("other4A3") != null) ? req.getParameter("other4A3") : "";
      other5A1 = (req.getParameter("other5A1") != null) ? req.getParameter("other5A1") : "";
      other5A2 = (req.getParameter("other5A2") != null) ? req.getParameter("other5A2") : "";
      other5A3 = (req.getParameter("other5A3") != null) ? req.getParameter("other5A3") : "";
      guest_id1 = (req.getParameter("guest_id1") != null) ? Integer.parseInt(req.getParameter("guest_id1")) : 0;
      guest_id2 = (req.getParameter("guest_id2") != null) ? Integer.parseInt(req.getParameter("guest_id2")) : 0;
      guest_id3 = (req.getParameter("guest_id3") != null) ? Integer.parseInt(req.getParameter("guest_id3")) : 0;
      guest_id4 = (req.getParameter("guest_id4") != null) ? Integer.parseInt(req.getParameter("guest_id4")) : 0;
      guest_id5 = (req.getParameter("guest_id5") != null) ? Integer.parseInt(req.getParameter("guest_id5")) : 0;
      //hides = req.getParameter("hide");

      if (req.getParameter("hide") != null) {

          hides = req.getParameter("hide");
      }
      
      //
      //  Convert hide from string to int
      //
      hide = 0;                       // init to No
      if (hides.equalsIgnoreCase( "Yes" )) {
         hide = 1;
      }

      if (req.getParameter("mtypeopt") != null) {

         mtypeOpt = req.getParameter("mtypeopt");
         session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
      }
      if (req.getParameter("mshipopt") != null) {
         mshipOpt = req.getParameter("mshipopt");
         session.setAttribute("mshipOpt", mshipOpt);
      }

   } else {
      
 
      //
      //  set entry in use to see if it is already in use
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE evntsup2b " +
            "SET in_use = 1, in_use_by = ? " +
            "WHERE id = ? AND in_use = 0");

         pstmt1.clearParameters();
         pstmt1.setString(1, user);
         pstmt1.setInt(2, id);

         count2 = pstmt1.executeUpdate();

         pstmt1.close();

      }
      catch (Exception e2) {

         dbError(out, e2);
         return;
      }


      if (count2 == 0) {              // if event slot already in use

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H3>Event Entry Busy</H3>");
         out.println("<BR><BR>Sorry, but this entry is currently busy.<BR>");
         out.println("<BR>Please select another entry or try again later.");
         out.println("<BR><BR>");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

         
      //
      //  get the existing entry and its contents
      //
      try {

         PreparedStatement pstmt3 = con.prepareStatement (
            "SELECT * FROM evntsup2b WHERE id = ?");

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
            notes = rs.getString("notes");
            hide = rs.getInt("hideNotes");
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
      //  if Royal Oaks Houston - always hide notes from members, but allow pro to override (do not force on returns)
      //
      if (club.equals( "royaloakscc" ) || club.equals( "bayhill" ) || club.equals( "shadowridgecc" )) {

         hide = 1;
      }
  
      
      //
      //  Determine day of week from the event date
      //
      Calendar cal2 = new GregorianCalendar();       // get todays date

      cal2.set(Calendar.YEAR, year);                 // change to requested date
      cal2.set(Calendar.MONTH, month-1);
      cal2.set(Calendar.DAY_OF_MONTH, day);

      int day_num = cal2.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)

      String day_name = day_table[day_num];            // get name for day
      
      if (sfb.equals( "Back" )) {      // if Event only on Back Tees
        
         fb = 1;
      }
      
      //        
      //  Check for a Member Notice message from Pro
      //
      String memNotice = verifySlot.checkMemNotice(date, time, time, fb, course, day_name, "event", true, sess_activity_id, con);  // use date of event and actual start time

      if (!memNotice.equals( "" )) {      // if message to display

         //
         //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
         //
         out.println("<HTML><HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<Title>Member Notice For Event Signup Request</Title>");
         out.println("</HEAD>");

         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
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
               out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" name=\"can\">");
               out.println("<input type=\"hidden\" name=\"id\" value=" + id + ">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"cancel\"></form>");

               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("</font></td>");

               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
                  out.println("<form action=\"Proshop_evntSignUp\" method=\"post\">");
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
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
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
                  out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
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
      
   }                // end of IF letter request

   //
   //  Ensure that there are no null player fields
   //
   if (player1 == null) player1 = "";
   if (player2 == null) player2 = "";
   if (player3 == null) player3 = "";
   if (player4 == null) player4 = "";
   if (player5 == null) player5 = "";
   if (p1cw == null) p1cw = "";
   if (p2cw == null) p2cw = "";
   if (p3cw == null) p3cw = "";
   if (p4cw == null) p4cw = "";
   if (p5cw == null) p5cw = "";

   //
   //   Get the 'X' parms for this event
   //
   try {

      PreparedStatement pstmtev = con.prepareStatement (
         "SELECT x, (ask_homeclub + ask_phone + ask_address + ask_email + ask_shirtsize + ask_shoesize) AS q " +
         "FROM events2b " +
         "WHERE name = ? AND courseName = ? AND activity_id = ?");

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

   } catch (Exception ignore) { }

   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
   out.println("<title>Proshop Event Sign Up Page</title>");

   //  Add script code to allow modal windows to be used
   out.println("<!-- ******** BEGIN LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->" +
           "<script type=\"text/javascript\">var lwmwLinkedBy=\"LiknoWebModalWindows [1]\",lwmwName=\"foretees-modal\",lwmwBN=\"128\";awmAltUrl=\"\";</script>" +
           "<script charset=\"UTF-8\" src=\"/" + rev + "/web%20utilities/foretees-modal.js\" type=\"text/javascript\"></script>" +
           "<!-- ******** END LIKNO WEB MODAL WINDOWS CODE FOR foretees-modal ******** -->");

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
   out.println("<script type=\"text/javascript\">");            // Erase name script
   out.println("<!--");
   out.println("function subletter(x) {");
   out.println(" var f = document.forms['playerform'];");
   out.println(" f.letter.value = x;");         // put the letter in the parm
   out.println(" f.submit();");        // submit the form
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
   out.println("function erasename(pos) {");
   out.println(" var f = document.forms['playerform'];");
   out.println(" eval('f.player'+pos+'.value = \"\"');");
   if (export_type != 0 || ask_hdcp == 1) out.println(" eval('f.ghin'+pos+'.value = \"\"');");
   if (export_type != 0 || ask_gender == 1) out.println(" eval('f.gender'+pos+'.selectedIndex = -1');");

   out.println(" eval('f.guest_id'+pos+'.value = \"0\"');");
   
   out.println("}");
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*********************************************************************************
   //  Move name script
   //*********************************************************************************
   //
   out.println("<script type=\"text/javascript\">");        // Move name script
   out.println("<!--");

   out.println("function movename(namewc) {");

   out.println("del = ':';");                               // deliminator is a colon
   out.println("array = namewc.split(del);");               // split string into 2 pieces (name, wc)
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
         out.println("if ( name == player1) {");
      }
            out.println("skip = 1;");
         out.println("}");
      out.println("}");

      out.println("if (skip == 0) {");

         out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("f.player1.value = name;");
            out.println("f.guest_id1.value = '0';");
            //out.println("if ((wc != null) && (wc != '')) {");    // if player is not 'X'
            out.println("f.p1cw.value = wc;");
            if (export_type != 0 || ask_gender == 1) out.println("f.gender1.value = gender;");
            if (export_type != 0 || ask_hdcp == 1) out.println("f.ghin1.value = ghin;");
            //out.println("}");

         if (size > 1) {
            out.println("} else {");
            out.println("if (player2 == '') {");                    // if player2 is empty
               out.println("f.player2.value = name;");
               out.println("f.guest_id2.value = '0';");
               //out.println("if ((wc != null) && (wc != '')) {");    // if player is not 'X'
               out.println("f.p2cw.value = wc;");
               if (export_type != 0 || ask_gender == 1) out.println("f.gender2.value = gender;");
               if (export_type != 0 || ask_hdcp == 1) out.println("f.ghin2.value = ghin;");
               //out.println("}");

            if (size > 2) {
               out.println("} else {");
               out.println("if (player3 == '') {");                    // if player3 is empty
                  out.println("f.player3.value = name;");
                  out.println("f.guest_id3.value = '0';");
                  //out.println("if ((wc != null) && (wc != '')) {");    // if player is not 'X'
                  out.println("f.p3cw.value = wc;");
                  if (export_type != 0 || ask_gender == 1) out.println("f.gender3.value = gender;");
                  if (export_type != 0 || ask_hdcp == 1) out.println("f.ghin3.value = ghin;");
                  //out.println("}");

               if (size > 3) {
                  out.println("} else {");
                  out.println("if (player4 == '') {");                    // if player4 is empty
                     out.println("f.player4.value = name;");
                     out.println("f.guest_id4.value = '0';");
                     //out.println("if ((wc != null) && (wc != '')) {");    // if player is not 'X'
                     out.println("f.p4cw.value = wc;");
                     if (export_type != 0 || ask_gender == 1) out.println("f.gender4.value = gender;");
                     if (export_type != 0 || ask_hdcp == 1) out.println("f.ghin4.value = ghin;");
                     //out.println("}");

                  if (size > 4) {
                     out.println("} else {");
                     out.println("if (player5 == '') {");                    // if player5 is empty
                        out.println("f.player5.value = name;");
                        out.println("f.guest_id5.value = '0';");
                        //out.println("if ((wc != null) && (wc != '')) {");    // if player is not 'X'
                        out.println("f.p5cw.value = wc;");
                        if (export_type != 0 || ask_gender == 1) out.println("f.gender5.value = gender;");
                        if (export_type != 0 || ask_hdcp == 1) out.println("f.ghin5.value = ghin;");
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

      out.println("}");               // end of dup name chack

      out.println("document.playerform.DYN_search.focus();");
      out.println("document.playerform.DYN_search.select();");
      
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*******************************************************************
   //  Move a Guest Name or 'X' into the tee slot
   //*******************************************************************
   //
   out.println("<script type=\"text/javascript\">");            // Move Guest Name script
   out.println("<!--");

   // Global vars used for guest tracking
   out.println("var guestid_slot;");
   out.println("var player_slot;");

   out.println("function moveguest(namewc) {");

   //out.println("var name = namewc;");
   out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
   out.println("var name = array[0];");

   //if (enableAdvAssist) {
       out.println("var use_guestdb = array[1];");
   //} else {
   //    out.println("var use_guestdb = 0; // force to off on iPad");
   //}

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

   // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
   out.println("if (use_guestdb == 1 && (player1 == ''" +
           (size > 1 ? " || player2 == ''" : "") +
           (size > 2 ? " || player3 == ''" : "") +
           (size > 3 ? " || player4 == ''" : "") +
           (size > 4 ? " || player5 == ''" : "") + ")) {");
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
                out.println("f.player2.focus();");                   // here for IE compat
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
                    out.println("f.player3.focus();");                   // here for IE compat
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
                      out.println("f.player4.focus();");                   // here for IE compat
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
                         out.println("f.player5.focus();");                   // here for IE compat
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

   out.println("</HEAD>");
   //out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#FF0000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<table border=\"0\" width=\"100%\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("&nbsp;&nbsp;&nbsp;<b>ForeTees</b>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     if (sess_activity_id == 0) {
         out.println("<font size=\"5\">Golf Shop Event Sign Up</font>");
     } else {

         String temp_act_name = "";

         try {

             PreparedStatement pstmt3 = con.prepareStatement("SELECT activity_name FROM activities WHERE activity_id = ?");
             pstmt3.clearParameters();
             pstmt3.setInt(1, sess_activity_id);

             ResultSet rs3 = pstmt3.executeQuery();

             if (rs3.next()) {
                 temp_act_name = rs3.getString("activity_name") + " Shop ";
             }

             pstmt3.close();

         } catch (Exception exc) {
             temp_act_name = "";
         }

         out.println("<font size=\"5\">" + temp_act_name + "Event Sign Up</font>");

     }
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " + SystemUtils.CURRENT_YEAR + " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this event registration.");
                               out.println("&nbsp; If you want to return without changes, <b>do not ");
                               out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
                               out.println("option below.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\">");         // col for Instructions and Go Back button

            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_evnt_instruct.htm', 'newwindow', config='Height=380, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0></a><br><br><br><br><br>");

            out.println("<font size=\"2\">");
            out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"id\" value=" + id + ">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
         out.println("</font></td>");

         out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" name=\"playerform\" id=\"playerform\">");
         out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");

         out.println("<td align=\"center\" valign=\"top\">");

            out.println("<font size=\"2\">");
            out.println("Event:&nbsp;&nbsp;<b>" + name + "</b><br>");
            out.println("Date:&nbsp;&nbsp;<b>" + ((season == 0) ? month + "/" + day + "/" + year : "Season Long") + "</b>&nbsp;&nbsp;");
            if (season == 0) out.println("Time:&nbsp;&nbsp;<b>" + act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + " " + act_ampm + "</b>");
            
            if (!course.equals( "" )) {
                if (club.equals("congressional")) {
                    out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + congressionalCustom.getFullCourseName(date, day, course) + "</b>");
                } else {
                    out.println("&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
                }
            }
           out.println("<br><br></font>");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"400\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Add or Remove Players</b><br>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\"><font size=\"2\"><br>");

            out.println("<table cellpadding=0 cellspacing=0 border=0>");
            
            out.println("<tr style=\"font-size:10pt;font-weight:bold\">" +
                            "<td></td>" +
                            "<td>&nbsp;&nbsp;&nbsp;&nbsp; Players</td>" +
                            ""); // +
            if (sess_activity_id == 0 && season == 0) out.println("<td>&nbsp;</td><td>&nbsp;Trans</td>");
            if (export_type != 0 || ask_hdcp == 1) {
                out.println("<td>&nbsp;</td>" +
                            "<td nowrap>&nbsp;" + ((sess_activity_id == 0) ? (club.equals("olyclub") ? "GHIN" : "Hdcp") : "USTA") + " #</td>");
            }
            if (export_type != 0 || ask_gender == 1) {
                out.println("<td>&nbsp;</td>" +
                            "<td>Gender&nbsp;</td>");
            }
            /*
            if (export_type != 0) {
                out.println("<td>&nbsp;</td>" +
                            "<td>&nbsp;Hdcp #</td>" +
                            "<td>&nbsp;</td>" +
                            "<td>Gender&nbsp;</td>");
            }
            */
            out.println("</tr>");

            // Print hidden guest_id inputs
            out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
            out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

               //  add player boxes
               out.println("<tr><td><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename(1)\" style=\"cursor:hand\"></td>");
               out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\"" + player1 + "\" size=\"26\" maxlength=\"60\" onkeypress=\"return DYN_disableEnterKey(event)\"></td>");
               if (sess_activity_id == 0 && season == 0) {
                 out.println("<td></td>");
                 out.println("<td><select size=\"1\" name=\"p1cw\" id=\"p1cw\">");
                 if (!p1cw.equals( "" )) {
                    out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
                 }
                 for (i=0; i<16; i++) {        // get all c/w options
                    if (tmode[i] == 1) {       // if specified for event
                       if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                          out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                       }
                    }
                 }
                 out.println("</select></td>");
               } else {
                 out.println("<input type=hidden name=p1cw value=\"" + season_tmode + "\">");
               }
               if (export_type != 0 || ask_hdcp == 1) {
                 out.println("<td></td>");
                 out.println("<td><input type=text name=ghin1 value=\"" + ghin1 + "\" size=8 maxlength=16></td>");
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

               out.println("<tr><td><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename(2)\" style=\"cursor:hand\"></td>");
               out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\"" + player2 + "\" size=\"26\" maxlength=\"60\" onkeypress=\"return DYN_disableEnterKey(event)\"></td>");
               if (sess_activity_id == 0 && season == 0) {
                 out.println("<td></td>");
                 out.println("<td><select size=\"1\" name=\"p2cw\" id=\"p2cw\">");
                 if (!p2cw.equals( "" )) {
                    out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
                 }
                 for (i=0; i<16; i++) {
                    if (tmode[i] == 1) {       // if specified for event
                       if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
                          out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                       }
                    }
                 }
                 out.println("</select></td>");
               } else {
                 out.println("<input type=hidden name=p2cw value=\"" + season_tmode + "\">");
               }
               if (export_type != 0 || ask_hdcp == 1) {
                 out.println("<td></td>");
                 out.println("<td><input type=text name=ghin2 value=\"" + ghin2 + "\" size=8 maxlength=16></td>");
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

               out.println("<tr><td><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename(3)\" style=\"cursor:hand\"></td>");
               out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\"" + player3 + "\" size=\"26\" maxlength=\"60\" onkeypress=\"return DYN_disableEnterKey(event)\"></td>");
               if (sess_activity_id == 0 && season == 0) {
                 out.println("<td></td>");
                 out.println("<td><select size=\"1\" name=\"p3cw\" id=\"p3cw\">");
                 for (i=0; i<16; i++) {
                    if (tmode[i] == 1 && !parmc.tmodea[i].equals( "" )) {       // if specified for event and not empty
                          Common_Config.buildOption(parmc.tmodea[i], parmc.tmodea[i], p3cw, out); 
                    }
                 }
                 out.println("</select></td>");
               } else {
                 out.println("<input type=hidden name=p3cw value=\"" + season_tmode + "\">");
               }
               if (export_type != 0 || ask_hdcp == 1) {
                 out.println("<td></td>");
                 out.println("<td><input type=text name=ghin3 value=\"" + ghin3 + "\" size=8 maxlength=16></td>");
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

               out.println("<tr><td><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename(4)\" style=\"cursor:hand\"></td>");
               out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\"" + player4 + "\" size=\"26\" maxlength=\"60\" onkeypress=\"return DYN_disableEnterKey(event)\"></td>");
               if (sess_activity_id == 0 && season == 0) {
                 out.println("<td></td>");
                 out.println("<td><select size=\"1\" name=\"p4cw\" id=\"p4cw\">");
                 if (!p4cw.equals( "" )) {
                    out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                 }
                 for (i=0; i<16; i++) {
                    if (tmode[i] == 1) {       // if specified for event
                       if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
                          out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                       }
                    }
                 }
                 out.println("</select></td>");
               } else {
                 out.println("<input type=hidden name=p4cw value=\"" + season_tmode + "\"><br>");
               }
               if (export_type != 0 || ask_hdcp == 1) {
                 out.println("<td></td>");
                 out.println("<td><input type=text name=ghin4 value=\"" + ghin4 + "\" size=8 maxlength=16></td>");
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

               out.println("<tr><td><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename(5)\" style=\"cursor:hand\"></td>");
               out.println("<td nowrap style=\"font-size:10pt;font-weight:bold\">5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"26\" maxlength=\"60\" onkeypress=\"return DYN_disableEnterKey(event)\"></td>");
               if (sess_activity_id == 0 && season == 0) {
                 out.println("<td></td>");
                 out.println("<td><select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
                 if (!p5cw.equals( "" )) {
                    out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                 }
                 for (i=0; i<16; i++) {
                    if (tmode[i] == 1) {       // if specified for event
                       if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
                          out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                       }
                    }
                 }
                 out.println("</select></td>");
               } else {
                 out.println("<input type=hidden name=p5cw value=\"" + season_tmode + "\">");
               }
               if (export_type != 0 || ask_hdcp == 1) {
                 out.println("<td></td>");
                 out.println("<td><input type=text name=ghin5 value=\"" + ghin5 + "\" size=8 maxlength=16></td>");
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
               
              
               int cols = 3;
               if (export_type == 1 || ask_hdcp + ask_gender == 2) {
                   cols = 7;
               } else if (ask_hdcp + ask_gender == 1) {
                   cols = 5;
               }
              
               //
               //   Notes
               //
               out.println("<tr><td><br></td></tr>");
               out.println("<tr><td valign=top><br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"document.forms['playerform'].notes.value='';\" style=\"cursor:hand\"></td>");
               out.println("<td colspan=" + cols + " style=\"font-size:10pt;font-weight:bold\" align=center>Notes to Pro:<br>");
               out.println("<textarea name=\"notes\" id=\"notes\" cols=\"42\" rows=\"4\">" + notes + "</textarea>");
              
               out.println("</td></tr>");
               out.println("</table>");
               
               out.println("<br>&nbsp;&nbsp;Hide Notes from Members?&nbsp;&nbsp; ");
               if (hide != 0) {
                   out.println("<input type=\"checkbox\" checked name=\"hide\" value=\"Yes\">");
               } else {
                   out.println("<input type=\"checkbox\" name=\"hide\" value=\"Yes\">");
               }
               out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");
/*            
               out.println("<br>&nbsp;&nbsp;Hide Notes from Members?:&nbsp;&nbsp; ");
               out.println("<select size=\"1\" name=\"hide\">");
               if (hide != 0) {
                 out.println("<option selected value=\"Yes\">Yes</option>");
                 out.println("<option value=\"No\">No</option>");
               } else {
                 out.println("<option selected value=\"No\">No</option>");
                 out.println("<option value=\"Yes\">Yes</option>");
               }
               out.println("</select>");
*/              
               out.println("<br>Suppress email notification?&nbsp;&nbsp; ");
               if (suppressEmails.equalsIgnoreCase( "yes" )) {
                   out.println("<input type=\"checkbox\" checked name=\"suppressEmails\" value=\"Yes\">");
               } else {
                   out.println("<input type=\"checkbox\" name=\"suppressEmails\" value=\"Yes\">");
               }
               out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");
               
               // display a tmode legend
               if (season == 0) {
                   out.println("<br><br><font size=\"1\">");
                   for (i=0; i<16; i++) {
                      if (tmode[i] == 1) {       // if specified for event
                         if (!parmc.tmodea[i].equals( "" )) {
                            out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                         }
                      }
                   }
                   out.println("<br>");
               }
               //out.println("</td></tr>"); // close that was after notes
               
               
           out.println("<input type=submit value=\"" + ((ask_more == 0) ? "Submit" : "Continue") + "\" name=\"submitForm\">");
           out.println("</font></td></tr>");
           out.println("</table>");
           
               //out.println("</font><br>");
               //out.println("<input type=submit value=\"Submit\" name=\"submitForm\"><br>");
               //out.println("</font></td></tr>");
               //out.println("</table>");
               out.println("<br><input type=submit value=\"Cancel Entry\" name=\"remove\" onclick=\"return confirm('Are you sure you want to remove ALL players from this event sign up?')\">");
         out.println("</td>");                                // end of table and column
           
         out.println("<td valign=\"top\">");
         
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
               
               
      // ********************************************************************************
      //   If we got control from user clicking on a letter in the Member List,
      //   then we must build the name list.
      // ********************************************************************************
        
      String letter = "%";         // default is 'List All'

      if (req.getParameter("letter") != null) {

         letter = req.getParameter("letter");

         if (letter.equals( "List All" )) {
            letter = "%";
         } else {
            letter = letter + "%";
         }
      }

   //
   //   Output the List of Names
   //
   alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, false, parmc, enableAdvAssist, false, out, con);


   out.println("</td>");                                      // end of this column
   out.println("<td valign=\"top\">");

   //
   //   Output the Alphabit Table for Members' Last Names
   //
   alphaTable.getTable(out, user);


   //
   //   Output the Mship and Mtype Options
   //
   alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);


   //
   //   Output the List of Guests
   //
   alphaTable.guestList(club, course, "", time, parm, false, false, sess_activity_id, enableAdvAssist, out, con);


   out.println("</td>");
   out.println("</tr>");
   out.println("</form>");     // end of playerform
   out.println("</table>");      // end of large table containing 4 smaller tables

   out.println("<font size=\"2\">");
   out.println("<b>Note:</b>  If adding a guest, click on the proper guest indicator, then click in the ");
   out.println("player position immediately<br>after the guest indicator ");
   out.println("and enter a space followed by the name of the guest and his/her club name.");
   out.println("</font>");


   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table
   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></body></html>");
   out.close();

 }   // end of doPost


 // *********************************************************
 //  Get a new id and set a new entry in the Event Sign Up db table
 // *********************************************************

// private synchronized int getNewId(Connection con, String name, String course, long date, int time) {
 public static int getNewId(Connection con, String name, String course, long date, int time) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int id = 0; // return value

    try {

        pstmt = con.prepareStatement (
                "INSERT INTO evntsup2b (" +
                    "name, courseName, c_date, c_time, r_date, r_time, " +
                    "player1, player2, player3, player4, player5, " +
                    "username1, username2, username3, username4, username5, " +
                    "userg1, userg2, userg3, userg4, userg5, " +
                    "p1cw, p2cw, p3cw, p4cw, p5cw, " +
                    "hndcp1, hndcp2, hndcp3, hndcp4, hndcp5, " +
                    "in_use, in_use_by, notes, hideNotes, wait, hole) " +
                "VALUES (" +
                    "?, ?, ?, ?, ?, ?, " +
                    "'', '', '', '', '', " +    // player
                    "'', '', '', '', '', " +    // username
                    "'', '', '', '', '', " +    // userg
                    "'', '', '', '', '', " +    // pcw
                    "0, 0, 0, 0, 0, " +         // hndcp
                    "0, '', '', 0, 0, '')");

        pstmt.clearParameters();
        pstmt.setString(1, name);
        pstmt.setString(2, course);
        pstmt.setLong(3, date);                               // cut-off date
        pstmt.setInt(4, time);                                // cut-off time
        pstmt.setLong(5, Utilities.getDate(con));             // date registered
        pstmt.setInt(6, Utilities.getTime(con));              // time registered


        // only get last insert id if the insert was successful
        if ( pstmt.executeUpdate() > 0 ) {

            //
            //  now get the id of the entry we just added
            pstmt = con.prepareStatement( "SELECT LAST_INSERT_ID()" );      // returns the last auto-increment id field this con inserted

            pstmt.clearParameters();
            rs = pstmt.executeQuery();

            if (rs.next()) id = rs.getInt(1);

        } else {

            Utilities.logError("Proshop_evntSignUp.getNewId - Fatal error creating new event signup!");

        }

    } catch (Exception exc) {

        Utilities.logError("Error in Proshop_evntSignUp.getNewId. Exception = " + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return id;

 }


 // *********************************************************
 //  Process reservation request from Proshop_evntSignUp (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   ResultSet rs = null;
   ResultSet rs2 = null;

   //
   //  Get this session's username
   //
   String user = (String)session.getAttribute("user");
   String club = (String)session.getAttribute("club");
   
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String in_use_by = "";

   String name = "";
   String course = "";
   //String sid = "";
   String notes = "";
   String playerName = "";
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
   String euser = "";
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
   
   int skip = 0;
   int signup_id = 0;
   //int reject = 0;
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
   //int adv_time = 0;
   //int adv_date = 0;
   int i = 0;
   int etype = 0;
   int hide = 0;
   //int ind = 0;
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
   int season = 0;
   int export_type = 0;
   
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
   int email_suppressed = 0;

   float hndcp1 = 99;
   float hndcp2 = 99;
   float hndcp3 = 99;
   float hndcp4 = 99;
   float hndcp5 = 99;

   //
   //  Arrays to hold member & guest names to tie guests to members
   //
   String [] gstA = new String [5];       // guests
   String [] memA = new String [5];       // members
   String [] usergA = new String [5];     // guests' associated member (username)
   String [] player = new String [5];
   String [] hdcp = new String [5];
   String [] gender = new String [5];
   String [] homeclub = new String [5];
   String [] phone = new String [5];
   String [] address = new String [5];
   String [] email = new String [5];
   String [] shirt = new String [5];
   String [] shoe = new String [5];
   String [] otherA1 = new String [5];    // other answers
   String [] otherA2 = new String [5];
   String [] otherA3 = new String [5];
   String [] oldUserA = new String [5];
   int [] guest_idA = new int [5];
   
   boolean guestError = false;            // init error flag
   boolean error = false;                 // init error flag
   //boolean hit = false;                   // init error flag
   //boolean check = false;
   boolean invalidGuest = false;
   boolean guestdbTbaAllowed = false;

   //  If guest tracking is in use, determine whether names are optional or required
   if (Utilities.isGuestTrackingConfigured(sess_activity_id, con) && Utilities.isGuestTrackingTbaAllowed(sess_activity_id, true, con)) {
       guestdbTbaAllowed = true;
   }

   //
   //  Get skip parm if provided
   //
   if (req.getParameter("skip") != null) {
       
      try { skip = Integer.parseInt(req.getParameter("skip")); }
      catch (Exception ignore) {}
   }
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);        // allocate a parm block


   //
   // Get all the parameters entered
   //
   course = req.getParameter("course");   //  name of course
   name = req.getParameter("name");       //  name of event
   //sid = req.getParameter("id");          //  id of event entry
   signup_id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;
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
   notes = req.getParameter("notes").trim();
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

   String hides = "no";
   if (req.getParameter("hide") != null) {

      hides = req.getParameter("hide");
   }
   
   String suppressEmails = "no";
   if (req.getParameter("suppressEmails") != null) {

      suppressEmails = req.getParameter("suppressEmails");
   }
   
   if (suppressEmails.equalsIgnoreCase("yes")) {
       email_suppressed = 1;
   } else {
       email_suppressed = 2;
   }

   //
   //  See if user wants to hide any notes from the Members
   //
   hide = 0;      // init

   if (hides.equalsIgnoreCase( "Yes" )) {

      hide = 1;
   }

   //
   //  Check C/W's for null
   //
   if (p1cw == null) p1cw = "";
   if (p2cw == null) p2cw = "";
   if (p3cw == null) p3cw = "";
   if (p4cw == null) p4cw = "";
   if (p5cw == null) p5cw = "";
   if (player1 == null) player1 = "";
   if (player2 == null) player2 = "";
   if (player3 == null) player3 = "";
   if (player4 == null) player4 = "";
   if (player5 == null) player5 = "";

   //
   //  Check if this entry is still 'in use' and still in use by this user??
   //
   //  This is necessary because the user may have gone away while holding this slot.  If the
   //  slot timed out (system timer), the slot would be marked 'not in use' and another
   //  user could pick it up.  The original holder could be trying to use it now.
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, username1, username2, username3, " +
         "username4, username5, p1cw, p2cw, p3cw, p4cw, p5cw, in_use, in_use_by, wait, " +
         "guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
         "FROM evntsup2b WHERE id = ?"); //name = ? AND  courseName = ? AND

      pstmt.clearParameters();
      //pstmt.setString(1, name);
      //pstmt.setString(2, course);
      pstmt.setInt(1, signup_id);
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

      if ((in_use == 0) || (!in_use_by.equals( user ))) {    // if entry not in use or not by this user

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
         out.println("<BR><BR>Sorry, but this event entry has been returned to the system.<BR>");
         out.println("<BR>The system timed out and released it.");
         out.println("<font size=\"2\"><br><br>");
         out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
         out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
        
      //
      //   Get the parms for this event
      //
      PreparedStatement pstmtev = con.prepareStatement (
         "SELECT * " +
         "FROM events2b " +
         "WHERE name = ? AND courseName = ?");

      pstmtev.clearParameters();        // clear the parms
      pstmtev.setString(1, name);
      pstmtev.setString(2, course);
      rs = pstmtev.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         year = rs.getInt("year");       // get date & time for email msgs
         month = rs.getInt("month");
         day = rs.getInt("day");
         etype = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         minsize = rs.getInt("minsize");
         max = rs.getInt("max");
         x = rs.getInt("x");
         xhrs = rs.getInt("xhrs");
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

      //
      //  Create time values for email msg below
      //
      act_ampm = " AM";

      if (act_hr == 0) {

         act_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (act_hr == 12) {

            act_ampm = " PM";         // change to Noon
         }
      }
      if (act_hr > 12) {

         act_hr = act_hr - 12;
         act_ampm = " PM";             // change to 12 hr clock
      }

      //
      //  convert time to hour and minutes for email msg
      //
      act_time = act_hr + ":" + Utilities.ensureDoubleDigit(act_min) + act_ampm;

   } catch (Exception e) {

      dbError(out, e);
      return;
   }

   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   if (req.getParameter("remove") != null) {

      // add entry to event log
      //Utilities.createEventLogEntry(user, event_id, signup_id, "DELETE", "Event signup deleted.", con);

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
      notes = "";
      hide = 0;
      guest_id1 = 0;
      guest_id2 = 0;
      guest_id3 = 0;
      guest_id4 = 0;
      guest_id5 = 0;

      //
      //  if this team was not on the wait list, then set an idicator so we will process the wait list below.
      //
      if (wait == 0) {
        
         checkWait = 1;
      }
      wait = 0;          // init wait in cancelled entry

      emailCan = 1;      // send email notification for Cancel Request
      sendemail = 1;

   } else {

      //
      //  Normal reservation request
      //
      //  Get the guest names specified for this club
      //
      try {
         parm.club = club;                   // set club name
         parm.course = course;               // and course name

         getClub.getParms(con, parm, sess_activity_id);        // get the club parms
        
      }
      catch (Exception ignore) {
      }


      //
      //  Check if any player names are guest names
      //
      //
      //  init string arrays
      //
      for (i=0; i < 5; i++) {
          
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
      for (i=0; i < 5; i++) {
          
          if (player[i] == null) player[i] = "";
          if (hdcp[i] == null) hdcp[i] = "";
          if (gender[i] == null) gender[i] = "";
          if (homeclub[i] == null) homeclub[i] = "";
          if (phone[i] == null) phone[i] = "";
          if (address[i] == null) address[i] = "";
          if (email[i] == null) email[i] = "";
          if (shirt[i] == null) shirt[i] = "";
          if (shoe[i] == null) shoe[i] = "";
          if (otherA1[i] == null) otherA1[i] = "";
          if (otherA2[i] == null) otherA2[i] = "";
          if (otherA3[i] == null) otherA3[i] = "";
          if (guest_idA[i] < 0) guest_idA[i] = 0;
          if (oldUserA[i] == null) oldUserA[i] = "";
      }

      // find the guests
      if (!player1.equals( "" )) {

         i = 0;
         loop1:
         while (i < parm.MAX_Guests) {

            if (player1.startsWith( parm.guest[i] )) {

               g1 = parm.guest[i];          // indicate player is a guest name and save name
               gstA[0] = player1;           // save guest value
               guests++;                    // increment number of guests this slot

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
      if (!player2.equals( "" )) {

         i = 0;
         loop2:
         while (i < parm.MAX_Guests) {

            if (player2.startsWith( parm.guest[i] )) {

               g2 = parm.guest[i];          // indicate player is a guest name and save name
               gstA[1] = player2;           // save guest value
               guests++;                    // increment number of guests this slot

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
      if (!player3.equals( "" )) {

         i = 0;
         loop3:
         while (i < parm.MAX_Guests) {

            if (player3.startsWith( parm.guest[i] )) {

               g3 = parm.guest[i];          // indicate player is a guest name and save name
               gstA[2] = player3;           // save guest value
               guests++;                    // increment number of guests this slot

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
      if (!player4.equals( "" )) {

         i = 0;
         loop4:
         while (i < parm.MAX_Guests) {

            if (player4.startsWith( parm.guest[i] )) {

               g4 = parm.guest[i];          // indicate player is a guest name and save name
               gstA[3] = player4;           // save guest value
               guests++;                    // increment number of guests this slot

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
      if (!player5.equals( "" )) {

         i = 0;
         loop5:
         while (i < parm.MAX_Guests) {

            if (player5.startsWith( parm.guest[i] )) {

               g5 = parm.guest[i];          // indicate player is a guest name and save name
               gstA[4] = player5;           // save guest value
               guests++;                    // increment number of guests this slot

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
      //  Make sure a C/W was specified for all players
      //
      if (sess_activity_id == 0 && season == 0 &&
         (((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x" )) && (p1cw.equals( "" ))) ||
          ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x" )) && (p2cw.equals( "" ))) ||
          ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x" )) && (p3cw.equals( "" ))) ||
          ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" )) && (p4cw.equals( "" ))) ||
          ((!player5.equals( "" )) && (!player5.equalsIgnoreCase( "x" )) && (p5cw.equals( "" ))))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
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
                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                    notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Make sure there are no duplicate names
      //
      if ((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x" )) && (g1.equals( "" ))) {

         if ((player1.equalsIgnoreCase( player2 )) || (player1.equalsIgnoreCase( player3 )) || (player1.equalsIgnoreCase( player4 )) || (player1.equalsIgnoreCase( player5 ))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
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
                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                    notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
         }
      }

      if ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x" )) && (g2.equals( "" ))) {

         if ((player2.equalsIgnoreCase( player3 )) || (player2.equalsIgnoreCase( player4 )) || (player2.equalsIgnoreCase( player5 ))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
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
                       guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                       notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      if ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x" )) && (g3.equals( "" ))) {

         if ((player3.equalsIgnoreCase( player4 )) || (player3.equalsIgnoreCase( player5 ))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
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
                       guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                       notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      if ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" )) && (g4.equals( "" ))) {

         if (player4.equalsIgnoreCase( player5 )) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
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
                       guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                       notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
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
         out.println("<BR><b>" + gplayer + "</b> appears to have been manually entered or " +
                 "<br>modified after selecting a different guest from the Guest Selection window.");
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
                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                 notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

         out.println("</font></center></body></html>");
         out.close();
         return;
      }


      //
      //  Parse the names to separate first, last & mi
      //  (Proshop does not verify single tokens - check for x or guest) !!!!!!!!!!!
      //
      if ((!player1.equals( "" )) && (g1.equals( "" ))) {                  // specified but not guest

         StringTokenizer tok = new StringTokenizer( player1 );     // space is the default token

         if ( tok.countTokens() > 3 ) {          // too many name fields

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname1 = tok.nextToken();
            lname1 = tok.nextToken();
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname1 = tok.nextToken();
            mi1 = tok.nextToken();
            lname1 = tok.nextToken();
         }

      }

      if ((!player2.equals( "" )) && (g2.equals( "" ))) {                  // specified but not guest

         StringTokenizer tok = new StringTokenizer( player2 );     // space is the default token

         if ( tok.countTokens() > 3 ) {          // too many name fields

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if (( tok.countTokens() == 1 ) && (!player2.equalsIgnoreCase( "X"))) {    // if not X

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname2 = tok.nextToken();
            lname2 = tok.nextToken();
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname2 = tok.nextToken();
            mi2 = tok.nextToken();
            lname2 = tok.nextToken();
         }
      }

      if ((!player3.equals( "" )) && (g3.equals( "" ))) {                  // specified but not guest

         StringTokenizer tok = new StringTokenizer( player3 );     // space is the default token

         if ( tok.countTokens() > 3 ) {          // too many name fields

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if (( tok.countTokens() == 1 ) && (!player3.equalsIgnoreCase( "X"))) {    // if not X

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname3 = tok.nextToken();
            lname3 = tok.nextToken();
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname3 = tok.nextToken();
            mi3 = tok.nextToken();
            lname3 = tok.nextToken();
         }
      }

      if ((!player4.equals( "" )) && (g4.equals( "" ))) {                  // specified but not guest

         StringTokenizer tok = new StringTokenizer( player4 );     // space is the default token

         if ( tok.countTokens() > 3 ) {          // too many name fields

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if (( tok.countTokens() == 1 ) && (!player4.equalsIgnoreCase( "X"))) {    // if not X

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname4 = tok.nextToken();
            lname4 = tok.nextToken();
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

            fname4 = tok.nextToken();
            mi4 = tok.nextToken();
            lname4 = tok.nextToken();
         }
      }

      if ((!player5.equals( "" )) && (g5.equals( "" ))) {                  // specified but not guest

         StringTokenizer tok = new StringTokenizer( player5 );     // space is the default token

         if ( tok.countTokens() > 3 ) {          // too many name fields

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if (( tok.countTokens() == 1 ) && (!player5.equalsIgnoreCase( "X"))) {    // if not X

            invData(out, player1, player2, player3, player4, player5);                        // reject
            return;
         }

         if ( tok.countTokens() == 2 ) {         // first name, last name

            fname5 = tok.nextToken();
            lname5 = tok.nextToken();
         }

         if ( tok.countTokens() == 3 ) {         // first name, mi, last name

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

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT username, g_hancap " +
            "FROM member2b " +
            "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

         if ((!fname1.equals( "" )) && (!lname1.equals( "" ))) {

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname1);
            pstmt1.setString(2, fname1);
            pstmt1.setString(3, mi1);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               user1 = rs.getString(1);
               hndcp1 = rs.getFloat(2);

               members = members + 1;        // increment # of members in request
            }
         }

         if ((!fname2.equals( "" )) && (!lname2.equals( "" ))) {

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname2);
            pstmt1.setString(2, fname2);
            pstmt1.setString(3, mi2);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               user2 = rs.getString(1);
               hndcp2 = rs.getFloat(2);

               members = members + 1;        // increment # of members in request
            }
         }

         if ((!fname3.equals( "" )) && (!lname3.equals( "" ))) {

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname3);
            pstmt1.setString(2, fname3);
            pstmt1.setString(3, mi3);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               user3 = rs.getString(1);
               hndcp3 = rs.getFloat(2);

               members = members + 1;        // increment # of members in request
            }
         }

         if ((!fname4.equals( "" )) && (!lname4.equals( "" ))) {

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname4);
            pstmt1.setString(2, fname4);
            pstmt1.setString(3, mi4);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               user4 = rs.getString(1);
               hndcp4 = rs.getFloat(2);

               members = members + 1;        // increment # of members in request
            }
         }

         if ((!fname5.equals( "" )) && (!lname5.equals( "" ))) {

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, lname5);
            pstmt1.setString(2, fname5);
            pstmt1.setString(3, mi5);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               user5 = rs.getString(1);
               hndcp5 = rs.getFloat(2);

               members = members + 1;        // increment # of members in request
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

      
      
      //
      // All checks below this point can be overridden
      //
      

      //
      // **************************************
      //  Check for Restrictions in the Event - check all members
      // **************************************
      //
      if (skip < 1) { // skip > 0

         try {

            String restPlayer = verifySlot.checkEventRests(user1, user2, user3, user4, user5, event_id, con);

            if (!restPlayer.equals( "" )) {        // if member (username) restricted from this event

               if (restPlayer.equals( user1 )) {
                  restPlayer = player1;             // get player's name
               } else {
                  if (restPlayer.equals( user2 )) {
                     restPlayer = player2;
                  } else {
                     if (restPlayer.equals( user3 )) {
                        restPlayer = player3;
                     } else {
                        if (restPlayer.equals( user4 )) {
                           restPlayer = player4;
                        } else {
                           restPlayer = player5;
                        }
                     }
                  }
               }

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Member Restricted For Event</H3><BR>");
               out.println("<BR><BR>Sorry, member " +restPlayer+ " is not allowed to participate in this event.");
               out.println("<BR><BR>Would you like to override the restriction and allow this event registration?");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");
               
               continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                            gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                            homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                            address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                            shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                            userg1, userg2, userg3, userg4, userg5,
                            guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                            notes, hides, course, name, suppressEmails, 1, false, season, rev, signup_id, event_id, out);

               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }

         }
         catch (Exception e1) {

            dbError(out, e1);                        // reject
            return;
         }

      }   // end of skip 1

      //
      // ***************************************************************
      //  Check if any members are already scheduled for this event
      // ***************************************************************
      //
      if ((!user1.equals( "" )) && (g1.equals( "" ))) {                  // specified but not guest

         try {
            PreparedStatement pstmtu = con.prepareStatement (
               "SELECT in_use FROM evntsup2b " +
               "WHERE name = ? AND courseName = ? AND id != ? AND inactive = 0 AND " +
               "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

            pstmtu.clearParameters();        // clear the parms
            pstmtu.setString(1, name);
            pstmtu.setString(2, course);
            pstmtu.setInt(3, signup_id);
            pstmtu.setString(4, user1);
            pstmtu.setString(5, user1);
            pstmtu.setString(6, user1);
            pstmtu.setString(7, user1);
            pstmtu.setString(8, user1);
            rs = pstmtu.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               error = true;
               playerName = player1;             // save player name for error msg       
            }
            pstmtu.close();
         }
         catch (Exception e1) {

            dbError(out, e1);                        // reject
            return;
         }
      }

      if (!error && !user2.equals( "" ) && g2.equals( "" )) {   // specified but not guest

         try {
            PreparedStatement pstmtu = con.prepareStatement (
               "SELECT in_use FROM evntsup2b " +
               "WHERE name = ? AND courseName = ? AND id != ? AND inactive = 0 AND " +
               "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

            pstmtu.clearParameters();        // clear the parms
            pstmtu.setString(1, name);
            pstmtu.setString(2, course);
            pstmtu.setInt(3, signup_id);
            pstmtu.setString(4, user2);
            pstmtu.setString(5, user2);
            pstmtu.setString(6, user2);
            pstmtu.setString(7, user2);
            pstmtu.setString(8, user2);
            rs = pstmtu.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               error = true;
               playerName = player2;             // save player name for error msg
            }
            pstmtu.close();
         }
         catch (Exception e1) {

            dbError(out, e1);                        // reject
            return;
         }
      }

      if (!error && !user3.equals( "" ) && g3.equals( "" )) {   // specified but not guest

         try {
            PreparedStatement pstmtu = con.prepareStatement (
               "SELECT in_use FROM evntsup2b " +
               "WHERE name = ? AND courseName = ? AND id != ? AND inactive = 0 AND " +
               "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

            pstmtu.clearParameters();        // clear the parms
            pstmtu.setString(1, name);
            pstmtu.setString(2, course);
            pstmtu.setInt(3, signup_id);
            pstmtu.setString(4, user3);
            pstmtu.setString(5, user3);
            pstmtu.setString(6, user3);
            pstmtu.setString(7, user3);
            pstmtu.setString(8, user3);
            rs = pstmtu.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               error = true;
               playerName = player3;             // save player name for error msg
            }
            pstmtu.close();
         }
         catch (Exception e1) {

            dbError(out, e1);                        // reject
            return;
         }
      }

      if (!error && !user4.equals( "" ) && g4.equals( "" )) {   // specified but not guest

         try {
            PreparedStatement pstmtu = con.prepareStatement (
               "SELECT in_use FROM evntsup2b " +
               "WHERE name = ? AND courseName = ? AND id != ? AND inactive = 0 AND " +
               "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

            pstmtu.clearParameters();        // clear the parms
            pstmtu.setString(1, name);
            pstmtu.setString(2, course);
            pstmtu.setInt(3, signup_id);
            pstmtu.setString(4, user4);
            pstmtu.setString(5, user4);
            pstmtu.setString(6, user4);
            pstmtu.setString(7, user4);
            pstmtu.setString(8, user4);
            rs = pstmtu.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               error = true;
               playerName = player4;             // save player name for error msg
            }
            pstmtu.close();
         }
         catch (Exception e1) {

            dbError(out, e1);                        // reject
            return;
         }
      }

      if (!error && !user5.equals( "" ) && g5.equals( "" )) {   // specified but not guest

         try {
            PreparedStatement pstmtu = con.prepareStatement (
               "SELECT in_use FROM evntsup2b " +
               "WHERE name = ? AND courseName = ? AND id != ? AND inactive = 0 AND " +
               "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

            pstmtu.clearParameters();        // clear the parms
            pstmtu.setString(1, name);
            pstmtu.setString(2, course);
            pstmtu.setInt(3, signup_id);
            pstmtu.setString(4, user5);
            pstmtu.setString(5, user5);
            pstmtu.setString(6, user5);
            pstmtu.setString(7, user5);
            pstmtu.setString(8, user5);
            rs = pstmtu.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               error = true;
               playerName = player5;             // save player name for error msg
            }
            pstmtu.close();
         }
         catch (Exception e1) {

            dbError(out, e1);                        // reject
            return;
         }
      }

      if (error == true) {      // if player already scheduled

         out.println(SystemUtils.HeadTitle("Member Already Scheduled - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Member Already Scheduled</H3><BR>");
         out.println("<BR><BR>Sorry, " + playerName + " is already registered for this event.<BR>");
         out.println("Remove this player and try again.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         
         returnForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                    gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                    homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                    address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                    shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5,
                    guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                    notes, hides, course, name, suppressEmails, rev, signup_id, event_id, out);

         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      // **************************************
      //  Check for max # of guests exceeded (per member)
      // **************************************
      //
      if (skip < 2) { 
         
         if (guests != 0) {      // if any guests were included

            try {
               PreparedStatement pstmtg = con.prepareStatement (
                  "SELECT guests FROM events2b " +
                  "WHERE name = ? AND courseName = ? AND activity_id = ?");

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
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
                  out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
                  out.println("event you are requesting is " + eguests + " per member.");
                  out.println("<BR>You have requested " + guests + " guests and " + members + " members.");
                  out.println("<BR><BR>Would you like to override the limit and allow this event registration?");
                  out.println("<BR><BR>");
                  out.println("<font size=\"2\">");
               
                  continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                               gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                               homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                               address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                               shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                               userg1, userg2, userg3, userg4, userg5,
                               guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                               notes, hides, course, name, suppressEmails, 2, false, season, rev, signup_id, event_id, out);

                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }

            }
            catch (Exception e5) {

               dbError(out, e5);
               return;
            }
         }      // end of if guests
      }      // end of if skip3

      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 3) {

         //
         //***********************************************************************************************
         //
         //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
         //
         //***********************************************************************************************
         //
         if (guests != 0 && members != 0) {      // if both guests and members were included

            if (g1.equals( "" )) {               // if slot 1 is not a guest

               //
               //  Both guests and members specified - determine guest owners by order
               //
               gi = 0;
               memberName = "";
               while (gi < 5) {                   // cycle thru arrays and find guests/members

                  if (!gstA[gi].equals( "" )) {

                     usergA[gi] = memberName;       // get last players username
                  } else {
                     usergA[gi] = "";               // init field
                  }
                  if (!memA[gi].equals( "" )) {

                     memberName = memA[gi];        // get players username
                  }
                  gi++;
               }
               userg1 = usergA[0];        // max of 4 guests since 1 player must be a member to get here
               userg2 = usergA[1];
               userg3 = usergA[2];
               userg4 = usergA[3];
               userg5 = usergA[4];
            }

            if (!g1.equals( "" ) || members > 1) {     // if slot 1 is a guest OR more than 1 member

               //
               //  At least one guest and one member have been specified.
               //  Prompt user to verify the order.
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");
               out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");

               if (!g1.equals( "" )) {              // if slot 1 is not a guest

                  out.println("You cannot have a guest in the first player position when one or more members are also specified.");
                  out.println("<BR><BR>");
               } else {
                  out.println("Please verify that the following order is correct:");
                  out.println("<BR><BR>");
                  out.println(player1 + " <BR>");
                  out.println(player2 + " <BR>");
                  if (!player3.equals( "" )) {
                     out.println(player3 + " <BR>");
                  }
                  if (!player4.equals( "" )) {
                     out.println(player4 + " <BR>");
                  }
                  if (!player5.equals( "" )) {
                     out.println(player5 + " <BR>");
                  }
                  out.println("<BR>Would you like to process the request as is?");
               }

               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"2\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
               out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
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
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
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

               if (!g1.equals( "" )) {              // if slot 1 is not a guest

                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

               } else {
                  out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

                  //
                  //  Return to process the players as they are
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
                  //out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
                  out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">"); // tmp
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
                  out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                  out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
                  out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
                  out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                  out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + userg1 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + userg2 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + userg3 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + userg4 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + userg5 + "\">");
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
                  out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\"></form></font>");
               }
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }

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
      }         // end of IF skip8


      //
      //  Make sure this signup has enough players as specified in the event conf
      //
      int players = 0;
      if (!player1.equals( "" )) players++;
      if (!player2.equals( "" )) players++;
      if (!player3.equals( "" )) players++;
      if (!player4.equals( "" )) players++;
      if (!player5.equals( "" )) players++;
          
      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 4) {

          if (players < minsize) {

              out.println(SystemUtils.HeadTitle("Minimum Players Not Met - Reject"));
              out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
              out.println("<hr width=\"40%\">");
              out.println("<BR><H3>Not Enough Players</H3><BR>");
              out.println("<BR><BR>The number of players (" + players + ") does not meet the required amount (" + minsize + ").");
              out.println("<BR><BR>Would you like to override this and allow the event registration?");
              out.println("<BR><BR>");
              out.println("<font size=\"2\">");
               
              continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                           gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                           homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                           address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                           shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                           userg1, userg2, userg3, userg4, userg5, 
                           guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                           notes, hides, course, name, suppressEmails, 4, false, season, rev, signup_id, event_id, out);

              out.println("</CENTER></BODY></HTML>");
              out.close();
              return;
          }
          
         //
         // **************************************
         //  Check for any Customs
         // **************************************
         //
         if (sess_activity_id == 0 && club.equals( "hazeltine" ) && name.equals( "Mens Invitational" )) {  // if Hazeltine & Invitational, check member sub-type

            String restPlayer = verifyCustom.checkHazeltineInvite(user1, user2, user3, user4, user5, con);

            if (!restPlayer.equals( "" )) {        // if member (username) restricted from this event

               if (restPlayer.equals( user1 )) {
                  restPlayer = player1;             // get player's name
               } else {
                  if (restPlayer.equals( user2 )) {
                     restPlayer = player2;
                  } else {
                     if (restPlayer.equals( user3 )) {
                        restPlayer = player3;
                     } else {
                        if (restPlayer.equals( user4 )) {
                           restPlayer = player4;
                        } else {
                           restPlayer = player5;
                        }
                     }
                  }
               }

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#CCCCAA\">");
               out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Member Restricted For Event</H3><BR>");
               out.println("<BR><BR>Sorry, member " +restPlayer+ " is not allowed to participate in this event.");
               out.println("<BR><BR>Only members flagged as Invite Priority are allowed to register.");
               out.println("<BR><BR>Would you like to override this and allow the event registration?");
               out.println("<BR><BR>");
               out.println("<font size=\"2\">");

               continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                           gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                           homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                           address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                           shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                           userg1, userg2, userg3, userg4, userg5,
                           guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                           notes, hides, course, name, suppressEmails, 4, false, season, rev, signup_id, event_id, out);

               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
         }            // end of IF hazeltine
           
      }
      

      //
      //   CC of Naples - check Associate B mships
      //
      if (club.equals("ccnaples")) {

          //boolean err = false;

          //String returnMsg = "";

          PreparedStatement pstmt = null;
          ResultSet rs3 = null;

          parmSlot parms = new parmSlot();

          // Populate the parm block
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
          parms.signup_id = signup_id;
          parms.teecurr_id = 0;

          // Get the date of this event
          try {
              pstmt = con.prepareStatement("SELECT date FROM events2b WHERE name = ?");
              pstmt.clearParameters();
              pstmt.setString(1, name);

              rs3 = pstmt.executeQuery();

              if (rs3.next()) {
                  parms.date = rs3.getInt("date");
              }

              pstmt.close();

          } catch (Exception exc) {
              //err = true;
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
              //err = true;
          }

          //
          //  CC of Naples - check for Associate B mships - restricted in season (case 1704)
          //
          if (verifyCustom.checkNaplesAssocBQuota(parms, con)) {    // check max rounds for Assoc B mships

              out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
              out.println("<BODY bgcolor=\"#CCCCAA\">");
              out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
              out.println("<hr width=\"40%\">");
              out.println("<BR><BR><H3>Member Exceeded Max Rounds</H3>" +
                          "<BR>Sorry, " +parms.player+ " has exceeded his/her max allowed rounds for this month." +
                          "<BR><BR>Please remove the player or return to the tee sheet.");
              out.println("<BR><BR>");
              out.println("<font size=\"2\">");

               continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                           gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                           homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                           address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                           shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3,
                           userg1, userg2, userg3, userg4, userg5,
                           guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                           notes, hides, course, name, suppressEmails, 4, false, season, rev, signup_id, event_id, out);

              out.println("</font></center></body></html>");
              out.close();
              return;
          }
      }        // end of CC of Naples
      


       // For Merion GC, members are limited on how many "Mens Stag Day" or "Mens Member Guest" events they can join at once, based on the current date.
       if (club.equals("merion")) {
           
           if (skip < 5) {

               int custom_event_count_1 = 0;
               int custom_event_count_2 = 0;
               int custom_event_category_id_1 = 1;    // "Mens Stag Day Events"
               int custom_event_category_id_2 = 2;    // "Mens Member Guest Events"
               int custom_selected_event_id = Utilities.getEventIdFromName(name, con);

               String event_category_name_1 = Utilities.getEventCategoryNameFromId(custom_event_category_id_1, con);
               String event_category_name_2 = Utilities.getEventCategoryNameFromId(custom_event_category_id_2, con);

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

                   if (!err_msg.equals("")) {    // check max rounds for Assoc B mships

                       out.println(SystemUtils.HeadTitle("Member Signup Limit Reached"));
                       out.println("<BODY bgcolor=\"#CCCCAA\">");
                       out.println("<CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                       out.println("<hr width=\"40%\">");
                       out.println("<BR><H3>Member Signup Limit Reached</H3><BR>");
                       out.println("<BR><BR>" + err_msg);
                       out.println("<BR><BR>Please remove this member and try again.");
                        out.println("<BR><BR>Would you like to override this and allow the event registration?");
                       out.println("<BR><BR>");
                       out.println("<font size=\"2\">");

                       continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                               gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                               homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                               address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                               shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3,
                               userg1, userg2, userg3, userg4, userg5,
                               guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                               notes, hides, course, name, suppressEmails, 30, false, season, rev, signup_id, event_id, out);

                       out.println("</font></center></body></html>");
                       out.close();
                       return;
                   }
               }
           } else if (skip == 30) {
               skip = 4;
           }
       }
   
      //
      // Notes: Handciap # and gender are two new options that are included in the slot
      //      
      
      
      //
      // If handicap numbers are required for this event then test for them
      //
      if (req_hdcp == 1 && skip < 5) {
          
          // Check to make sure any guests that are present have names included
          i = 0;
          loop1:
          while (i < 5) {

              if (!player[i].equals( "" )) {    // if player present

                 if (hdcp[i].equals("")) {      // if hdcp # not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Missing Required Data</H3>");
                    out.println("<BR><BR><b>" + player[i] + "</b> does not have a " + ((sess_activity_id == 0) ? "handicap" : "USTA") + " number specified.");
                    out.println("<BR><BR>" + ((sess_activity_id == 0) ? "Handicap" : "USTA") + " numbers are required for all players in this event.");
                    if (export_type != 0 && sess_activity_id == 0) out.println("<BR><BR><b><i>Failure to provide a handicap number will prevent you from exporting this event!</i></b>");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 5, false, season, rev, signup_id, event_id, out);

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
      if (req_gender == 1 && skip < 6) {
          
          // Check to make sure any guests that are present have a gender specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!gstA[i].equals( "" )) {      // if player present

                 if (gender[i].equals("")) {    // if gender not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Missing Required Data</H3>");
                    out.println("<BR><BR><b>" + gstA[i] + "</b> does not have a gender specified.");
                    out.println("<BR><BR>All participants are required to specify a gender for this event.");
                    if (export_type != 0) out.println("<BR><BR><b><i>Failure to specify a gender will prevent you from exporting this event!</i></b>");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");

                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 6, false, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                    
                 } // end if gender empty

              } // end if player present
              
              i++;
              
          } // end of while
      
      } // end if gender req
      
      
      //
      // If guest names are required for this event and there are guests present
      // then make sure member entered a guest's name after each guest type
      //
      if ( req_guestname == 1 && guests > 0 && skip < 7) {


            boolean gstNameError = false;

            int count2 = 0;
            count = 0;

            String gstname = "";
            String [] gName = new String [5];    // array to hold the Guest Names specified
            String [] gType = new String [5];    // array to hold the Guest Types specified

            //
            //  init string arrays
            //
            for (i=0; i < 5; i++) {

               gName[i] = "";
               gType[i] = "";
            }

            i = 0; // reset

            //
            //  Determine which player positions need to be tested.
            //  Do not check players that have already been checked.
            //
            if (!player1.equals( oldPlayer1 )) {     // if player name is new or has changed

               if (!g1.equals( "" )) {

                  gType[i] = g1;        // get guest type
                  gName[i] = player1;   // get name entered
                  i++;
               }
            }
            if (!player2.equals( oldPlayer2 )) {     // if player name is new or has changed

               if (!g2.equals( "" )) {

                  gType[i] = g2;        // get guest type
                  gName[i] = player2;   // get name entered
                  i++;
               }
            }
            if (!player3.equals( oldPlayer3 )) {     // if player name is new or has changed

               if (!g3.equals( "" )) {

                  gType[i] = g3;        // get guest type
                  gName[i] = player3;   // get name entered
                  i++;
               }
            }
            if (!player4.equals( oldPlayer4 )) {     // if player name is new or has changed

               if (!g4.equals( "" )) {

                  gType[i] = g4;        // get guest type
                  gName[i] = player4;   // get name entered
                  i++;
               }
            }
            if (!player5.equals( oldPlayer5 )) {     // if player name is new or has changed

               if (!g5.equals( "" )) {

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

               if (!gType[i].equals( "" )) {          // if guest type specified

                  gstname = gName[i];                 // get player name specified

                  if (gstname.equals( gType[i] )) {   // if matches then can't be name

                     gstNameError = true;
                     break loopg1;
                  }
                  //
                  //  Use tokens to determine the number of words in each string.
                  //  There must be at least 2 extra words in the player name.
                  //
                  StringTokenizer tok = new StringTokenizer( gstname, " " );          // delimiter is a space
                  count = tok.countTokens();                                       // number of words in player name

                  StringTokenizer tok2 = new StringTokenizer( gType[i], " " );     // guest type
                  count2 = tok2.countTokens();                                     // number of words in guest type

                  if (count > count2) {

                     count = count - count2;          // how many more words in player name than guest type

                     if (count < 2) {                 // must be at least 2

                        gstNameError = true;
                        break loopg1;
                     }

                  } else {

                     gstNameError = true;
                     break loopg1;
                  }

               } else {        // done when no guest type
                  break loopg1;
               }
               i++;
            }                  // end of while


            if ( gstNameError ) {

                out.println(SystemUtils.HeadTitle("Data Entry Error"));
                out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><H3>Invalid Guest Request</H3>");
                out.println("<BR><BR>Sorry, you must provide the full name of all guest(s) for this event.");
                out.println("<BR><BR>Please enter a space followed by the guest's name immediately after the");
                out.println("<BR>guest type in the player field.");
                out.println("<BR><BR>Would you like to override this and allow the event registration?");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");

                continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw,
                             gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5,
                             homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5,
                             address1, address2, address3, address4, address5, email1, email2, email3, email4, email5,
                             shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3,
                             userg1, userg2, userg3, userg4, userg5,
                             guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                             notes, hides, course, name, suppressEmails, 7, false, season, rev, signup_id, event_id, out);

                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }
      }



      
      //
      // Note:  The additional options below are presented on a new page - not slot
      //      
      
      
      // If we are here from slot and there is additional data to get from user then
      // before doing any other checks, let call the second page.  
      // If we've already been to the second page then proceed with the checks.
      if (req.getParameter("sp") == null && 
             (
              (ask_homeclub + ask_phone + ask_address + ask_email > 0 && guests > 0) || 
              (ask_shirtsize + ask_shoesize + ask_otherA1 + ask_otherA2 + ask_otherA3 > 0)
             )
         ) {
          
          out.println(SystemUtils.HeadTitle("Data Entry Error"));
          out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
          out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<center>");
          out.println("<BR><H3>Additional Information for Sign-up</H3>");

          extraInfoForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                        gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                        homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                        address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                        shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, 
                        player, gstA, homeclub, phone, address, email, shirt, shoe, otherA1, otherA2, otherA3, 
                        ask_homeclub + req_homeclub, ask_phone + req_phone, ask_address + req_address, ask_email + req_email, ask_shirtsize + req_shirtsize, ask_shoesize + req_shoesize, ask_otherA1 + req_otherA1, ask_otherA2 + req_otherA2, ask_otherA3 + req_otherA3, 
                        otherQ1, otherQ2, otherQ3, who_shirtsize, who_shoesize, who_otherQ1, who_otherQ2, who_otherQ3, players, guests, 
                        userg1, userg2, userg3, userg4, userg5,
                        guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                        notes, hides, course, name, suppressEmails, 7, club, rev, signup_id, event_id, out); // using highest skip value prior to this check

          out.println("</font></center></body></html>");
          out.close();
          return;
          
      }
      

      
      
      
      //
      // If home clubs are required for guests in this event then test for them
      //
      if (req_homeclub == 1 && skip < 8) {
          
          // Check to make sure any guests that are present have home clubs specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!gstA[i].equals( "" )) {      // if guest present

                 if (homeclub[i].equals("")) {  // if home club not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + player[i] + "</b> does not have a home club specified.");
                    out.println("<BR><BR>Home clubs are required for all guests in this event.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 8, true, season, rev, signup_id, event_id, out);
                    
                    out.println("</font></center></body></html>");
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
      if (req_phone == 1 && skip < 9) {
          
          // Check to make sure any guests that are present have phone numbers included
          i = 0;
          loop1:
          while (i < 5) {

              if (!gstA[i].equals( "" )) {      // if guest present

                 if (phone[i].equals("")) {     // if phone number not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + gstA[i] + "</b> does not have a phone number specified.");
                    out.println("<BR><BR>Phone numbers are required for all guests in this event.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 9, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
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
      if (req_address == 1 && skip < 10) {
          
          // Check to make sure any guests that are present have addresses included
          i = 0;
          loop1:
          while (i < 5) {

              if (!gstA[i].equals( "" )) {      // if guest present

                 if (address[i].equals("")) {   // if address not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + gstA[i] + "</b> does not have an address specified.");
                    out.println("<BR><BR>Addresses are required for all guests in this event.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 10, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
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
      if (req_email == 1 && skip < 11) {
          
          // Check to make sure any guests that are present have email address included
          i = 0;
          loop1:
          while (i < 5) {

              if (!gstA[i].equals( "" )) {      // if guest present

                 if (email[i].equals("")) {     // if email address not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + gstA[i] + "</b> does not have an email address specified.");
                    out.println("<BR><BR>Email addresses are required for all guests in this event.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 11, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                    
                 } // end if email address empty

              } // end if player present
              
              i++;
              
          } // end of while
      
      } // end if email addresses req
      
      
      
      //
      // If shirt size is required for this event then test for it
      //
      if (req_shirtsize == 1 && skip < 12) {
          
          // Check to make sure any guests that are present have a shirt size specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!player[i].equals( "" )) {    // if player present

                 if (shirt[i].equals("")) {     // if shirt size not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + player[i] + "</b> does not have a shirt size specified.");
                    out.println("<BR><BR>All participants are required to specify a shirt size for this event.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 12, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
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
      if (req_shoesize == 1 && skip < 13) {
          
          // Check to make sure any players that are present have a shoe size specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!player[i].equals( "" )) {    // if player present

                 if (shoe[i].equals("")) {      // if shoe size not specified

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + player[i] + "</b> does not have a shoe size specified.");
                    out.println("<BR><BR>All participants are required to specify a shoe size for this event.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 13, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
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
      if (req_otherA1 == 1 && skip < 14) {
          
          // Check to make sure any players that are present have a shoe size specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!player[i].equals( "" )) {    // if player present

                 if (otherA1[i].equals("")) {   // if other answer not specified
                 
                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + player[i] + "</b> is missing an answer to Question #1.");
                    //out.println("<BR><BR>All participants are required to specify an answer to Question #1.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3,
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 14, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
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
      if (req_otherA2 == 1 && skip < 15) {
          
          // Check to make sure any guests that are present have a shoe size specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!player[i].equals( "" )) {    // if player present

                 if (otherA2[i].equals("")) {   // if other answer not specified
                 
                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + player[i] + "</b> is missing an answer to Question #2.");
                    //out.println("<BR><BR>All participants are required to specify an answer to Question #2.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3,
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 15, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
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
      if (req_otherA3 == 1 && skip < 16) {
          
          // Check to make sure any guests that are present have a shoe size specified
          i = 0;
          loop1:
          while (i < 5) {

              if (!player[i].equals( "" )) {    // if player present

                 if (otherA3[i].equals("")) {   // if other answer not specified
                 
                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><H3>Additional Information for Sign-up</H3>");
                    out.println("<i><b><font color=red>Missing Required Information</font></b></i>");
                    
                    out.println("<BR><BR><b>" + player[i] + "</b> is missing an answer to Question #3.");
                    //out.println("<BR><BR>All participants are required to specify an answer to Question #3.");
                    out.println("<BR><BR>Would you like to override this and allow the event registration?");
                    out.println("<BR><BR>");
                    out.println("<font size=\"2\">");
                    
                    continueForm(player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, 
                                 gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5, 
                                 homeclub1, homeclub2, homeclub3, homeclub4, homeclub5, phone1, phone2, phone3, phone4, phone5, 
                                 address1, address2, address3, address4, address5, email1, email2, email3, email4, email5, 
                                 shirt1, shirt2, shirt3, shirt4, shirt5, shoe1, shoe2, shoe3, shoe4, shoe5, otherA1, otherA2, otherA3, 
                                 userg1, userg2, userg3, userg4, userg5,
                                 guest_id1, guest_id2, guest_id3, guest_id4, guest_id5,
                                 notes, hides, course, name, suppressEmails, 16, true, season, rev, signup_id, event_id, out);

                    out.println("</font></center></body></html>");
                    out.close();
                    return;
                    
                 } // end if other A3 empty

              } // end if player present
              
              i++;
              
          } // end of while
      
      } // end if other A3

      // If not golf, clear out all mode of transportation values.  ***WILL NEED TO HANDLE DIFFERENTLY IF ANY FUTURE FLXREZ ACTIVITIES REQUIRE THEM***
      if (sess_activity_id > 0) {
          p1cw = "";
          p2cw = "";
          p3cw = "";
          p4cw = "";
          p5cw = "";
      }
      
      
      
      
      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************
      //
      //  See if entry should be on wait list
      //
      if (wait == 0) {     // if not already on wait list
        
         if (oldPlayer1.equals( "" ) && oldPlayer2.equals( "" ) && oldPlayer3.equals( "" ) &&
             oldPlayer4.equals( "" ) && oldPlayer5.equals( "" )) {      // and new entry
            
            teams = 0;      // init # of teams registered

            try {
               //
               //   see if event is full
               //
               PreparedStatement pstmtw = con.prepareStatement (
                  "SELECT player1, player2, player3, player4, player5 " +
                  "FROM evntsup2b " +
                  "WHERE name = ? AND wait = 0 AND inactive = 0");

               pstmtw.clearParameters();        // clear the parms
               pstmtw.setString(1, name);
               rs2 = pstmtw.executeQuery();      // execute the prepared pstmt

               while (rs2.next()) {

                  wplayer1 = rs2.getString("player1");
                  wplayer2 = rs2.getString("player2");
                  wplayer3 = rs2.getString("player3");
                  wplayer4 = rs2.getString("player4");
                  wplayer5 = rs2.getString("player5");

                  if (!wplayer1.equals( "" ) || !wplayer2.equals( "" ) || !wplayer3.equals( "" ) ||
                      !wplayer4.equals( "" ) || !wplayer5.equals( "" )) {
                     
                     teams++;
                  }
                 
               }
               pstmtw.close();

               // SystemUtils.logError("DEBUG: Check Wait List for Club " +club+ ". Teams = "+teams+ ", Max = " +max+ ". Name = " +name);       // log the error
                  
            }
            catch (Exception e1) {
               SystemUtils.logError("Error in Proshop_evntSignUp.verify - Check Wait List for Club " +club+ ". Exception = " + e1.getMessage());       // log it and continue
            }
            
            if (teams >= max) {             // if event already full
              
               wait = 1;                    // put on wait list
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
      //  If players changed, then send emails
      //
      if (!player1.equals( oldPlayer1 )) {

         sendemail = 1;    // player changed - send email notification to all
      }

      if (!player2.equals( oldPlayer2 )) {

         sendemail = 1;    // player changed - send email notification to all
      }

      if (!player3.equals( oldPlayer3 )) {

         sendemail = 1;    // player changed - send email notification to all
      }

      if (!player4.equals( oldPlayer4 )) {

         sendemail = 1;    // player changed - send email notification to all
      }

      if (!player5.equals( oldPlayer5 )) {

         sendemail = 1;    // player changed - send email notification to all
      }

      //
      //   Set email type based on new or update request (cancel set above)
      //
      if ((!oldPlayer1.equals( "" )) || (!oldPlayer2.equals( "" )) || (!oldPlayer3.equals( "" )) ||
          (!oldPlayer4.equals( "" )) || (!oldPlayer5.equals( "" ))) {

         emailMod = 1;  // tee time was modified
         
         //
         //  Shift players up in case one was removed from the request (player1 must be occupied if any players exist)
         //
         if (player1.equals( "" )) {    // if empty

            if (!player2.equals( "" )) {    // if not empty

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

               if (!player3.equals( "" )) {    // if not empty

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

                  if (!player4.equals( "" )) {    // if not empty

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

                     if (!player5.equals( "" )) {    // if not empty

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
         if (player2.equals( "" )) {    // if empty

            if (!player3.equals( "" )) {    // if not empty

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

               if (!player4.equals( "" )) {    // if not empty

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

                  if (!player5.equals( "" )) {    // if not empty

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
         if (player3.equals( "" )) {    // if empty

            if (!player4.equals( "" )) {    // if not empty

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

               if (!player5.equals( "" )) {    // if not empty

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
         if (player4.equals( "" )) {    // if empty

            if (!player5.equals( "" )) {    // if not empty

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
   //  Verification complete -
   //  Update the entry in the event sign up table
   //
   try {

      PreparedStatement pstmt6 = con.prepareStatement (
         "UPDATE evntsup2b " +
         "SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, " +
             "username1 = ?, username2 = ?, username3 = ?, username4 = ?, username5 = ?, p1cw = ?, " +
             "p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
             "hndcp4 = ?, hndcp5 = ?, notes = ?, hideNotes = ?, wait = ?, " +
             "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, " +
             "gender1 = ?, gender2 = ?, gender3 = ?, gender4 = ?, gender5 = ?, " +
             "ghin1 = ?, ghin2 = ?, ghin3 = ?, ghin4 = ?, ghin5 = ?, " +
             "homeclub1 = ?, homeclub2 = ?, homeclub3 = ?, homeclub4 = ?, homeclub5 = ?, " +
             "phone1 = ?, phone2 = ?, phone3 = ?, phone4 = ?, phone5 = ?, " +
             "address1 = ?, address2 = ?, address3 = ?, address4 = ?, address5 = ?, " +
             "email1 = ?, email2 = ?, email3 = ?, email4 = ?, email5 = ?, " +
             "shirtsize1 = ?, shirtsize2 = ?, shirtsize3 = ?, shirtsize4 = ?, shirtsize5 = ?, " +
             "shoesize1 = ?, shoesize2 = ?, shoesize3 = ?, shoesize4 = ?, shoesize5 = ?," +
             "other1A1 = ?, other1A2 = ?, other1A3 = ?, other2A1 = ?, other2A2 = ?, other2A3 = ?, " +
             "other3A1 = ?, other3A2 = ?, other3A3 = ?, other4A1 = ?, other4A2 = ?, other4A3 = ?, " +
             "other5A1 = ?, other5A2 = ?, other5A3 = ?, guest_id1 = ?, guest_id2 = ?, " +
             "guest_id3 = ?, guest_id4 = ?, guest_id5 = ? " +
         "WHERE id = ?"); // name = ? AND courseName = ? AND 

      pstmt6.clearParameters();        // clear the parms
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
      pstmt6.setInt(22, hide);
      pstmt6.setInt(23, wait);
      pstmt6.setString(24, userg1);
      pstmt6.setString(25, userg2);
      pstmt6.setString(26, userg3);
      pstmt6.setString(27, userg4);
      pstmt6.setString(28, userg5);
      pstmt6.setString(29, gender1);
      pstmt6.setString(30, gender2);
      pstmt6.setString(31, gender3);
      pstmt6.setString(32, gender4);
      pstmt6.setString(33, gender5);
      pstmt6.setString(34, ghin1);
      pstmt6.setString(35, ghin2);
      pstmt6.setString(36, ghin3);
      pstmt6.setString(37, ghin4);
      pstmt6.setString(38, ghin5);
      
      pstmt6.setString(39, homeclub1);
      pstmt6.setString(40, homeclub2);
      pstmt6.setString(41, homeclub3);
      pstmt6.setString(42, homeclub4);
      pstmt6.setString(43, homeclub5);
      
      pstmt6.setString(44, phone1);
      pstmt6.setString(45, phone2);
      pstmt6.setString(46, phone3);
      pstmt6.setString(47, phone4);
      pstmt6.setString(48, phone5);
      
      pstmt6.setString(49, address1);
      pstmt6.setString(50, address2);
      pstmt6.setString(51, address3);
      pstmt6.setString(52, address4);
      pstmt6.setString(53, address5);
      
      pstmt6.setString(54, email1);
      pstmt6.setString(55, email2);
      pstmt6.setString(56, email3);
      pstmt6.setString(57, email4);
      pstmt6.setString(58, email5);
      
      pstmt6.setString(59, shirt1);
      pstmt6.setString(60, shirt2);
      pstmt6.setString(61, shirt3);
      pstmt6.setString(62, shirt4);
      pstmt6.setString(63, shirt5);

      pstmt6.setString(64, shoe1);
      pstmt6.setString(65, shoe2);
      pstmt6.setString(66, shoe3);
      pstmt6.setString(67, shoe4);
      pstmt6.setString(68, shoe5);
     
      pstmt6.setString(69, other1A1);
      pstmt6.setString(70, other1A2);
      pstmt6.setString(71, other1A3);
      pstmt6.setString(72, other2A1);
      pstmt6.setString(73, other2A2);
      pstmt6.setString(74, other2A3);
      pstmt6.setString(75, other3A1);
      pstmt6.setString(76, other3A2);
      pstmt6.setString(77, other3A3);
      pstmt6.setString(78, other4A1);
      pstmt6.setString(79, other4A2);
      pstmt6.setString(80, other4A3);
      pstmt6.setString(81, other5A1);
      pstmt6.setString(82, other5A2);
      pstmt6.setString(83, other5A3);

      pstmt6.setInt(84, guest_id1);
      pstmt6.setInt(85, guest_id2);
      pstmt6.setInt(86, guest_id3);
      pstmt6.setInt(87, guest_id4);
      pstmt6.setInt(88, guest_id5);
      
      //pstmt6.setString(28, name);
      //pstmt6.setString(29, course);
      
      pstmt6.setInt(89, signup_id);
         
      count = pstmt6.executeUpdate();      // execute the prepared stmt

      pstmt6.close();

   }
   catch (Exception e6) {

      dbError(out, e6);
      return;
   }


   String mode = "UPDATE"; // default mode
   String detail = Utilities.buildPlayerString(player1, player2, player3, player4, player5, ", ");

   // if not here to cancel the signup then add the log entry
   if (req.getParameter("remove") != null) {

       mode = "CANCEL";
       detail = "Canceled by proshop.";

   } else if (emailNew == 1) {

       mode = "CREATE";

   }

   // add entry to event log
   Utilities.createEventLogEntry(user, event_id, signup_id, mode, detail, email_suppressed, con);


   //  Attempt to add hosts for any accompanied tracked guests
   if (guest_id1 > 0 && !userg1.equals("")) Common_guestdb.addHost(guest_id1, userg1, con);
   if (guest_id2 > 0 && !userg2.equals("")) Common_guestdb.addHost(guest_id2, userg2, con);
   if (guest_id3 > 0 && !userg3.equals("")) Common_guestdb.addHost(guest_id3, userg3, con);
   if (guest_id4 > 0 && !userg4.equals("")) Common_guestdb.addHost(guest_id4, userg4, con);
   if (guest_id5 > 0 && !userg5.equals("")) Common_guestdb.addHost(guest_id5, userg5, con);

   //
   //  Build the HTML page to confirm event registration for user
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
   out.println("<Title>Proshop Event Registration Page</Title>");

   if (wait == 0) {            // if not on wait list
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?name=" + name + "&course=" + course + "\">");
   }
   out.println("</HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The event entry has been cancelled.</p>");
      
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your Event Registration has been accepted and processed.</p>");

      if (wait > 0) {            // if on wait list

         out.println("<br><br><b>Note:</b>  This team is currently on the WAIT LIST.");
      }

      if (xcount > 0 && xhrs > 0) {            // if any X's were specified

         out.println("<p>&nbsp;</p>All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the tee time.");
         out.println("<br>If not, the system will automatically remove the X.<br>");
      }
   }

   out.println("<p>&nbsp;</p></font>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();


   try {

      resp.flushBuffer();      // force the repsonse to complete

   }
   catch (Exception ignore) {
   }


   //
   //***********************************************
   //  if entry was removed then check for a wait list
   //  and move a team up if possible
   //***********************************************
   //
   //if (checkWait != 0 && !club.equals("medinahcc") && !club.equals("portlandgc")) {   // no automated wait list move ups for Medinah (Case# 1475)
   if (checkWait != 0 && !club.equals("medinahcc") && !club.equals("ballenisles")) {   // no automated wait list move ups for Medinah (Case# 1475)

      teams = 0;
      count = 0;

      try {

         //
         //   see if event is full
         //
         PreparedStatement pstmtw = con.prepareStatement (
            "SELECT player1, player2, player3, player4, player5 " +
            "FROM evntsup2b " +
            "WHERE name = ? AND wait = 0 AND inactive = 0");

         pstmtw.clearParameters();
         pstmtw.setString(1, name);
         rs2 = pstmtw.executeQuery();

         while (rs2.next()) {

            wplayer1 = rs2.getString("player1");
            wplayer2 = rs2.getString("player2");
            wplayer3 = rs2.getString("player3");
            wplayer4 = rs2.getString("player4");
            wplayer5 = rs2.getString("player5");

            if (!wplayer1.equals( "" ) || !wplayer2.equals( "" ) || !wplayer3.equals( "" ) || 
                !wplayer4.equals( "" ) || !wplayer5.equals( "" )) {
               
               teams++;               // bump number of teams if any players
            }
         }
         pstmtw.close();

         if (teams < max) {          // if room for more teams then take the oldest one off the wait list

            long wdate = 0;
            int wtime = 0;

            //
            //   get the earliest registration date on wait list
            //
            PreparedStatement stmtw1 = con.prepareStatement (
               "SELECT MIN(r_date) FROM evntsup2b " +
               "WHERE name = ? AND wait != 0 AND inactive = 0");

            stmtw1.clearParameters();
            stmtw1.setString(1, name);
            rs = stmtw1.executeQuery();

            if (rs.next()) {

               wdate = rs.getLong(1);
            }
            stmtw1.close();

            if (wdate != 0) {

               //
               //   get the earliest time on this reg date on wait list
               //
               PreparedStatement stmtw2 = con.prepareStatement (
                  "SELECT MIN(r_time) FROM evntsup2b " +
                  "WHERE name = ? AND r_date = ? AND wait != 0 AND inactive = 0");

               stmtw2.clearParameters();
               stmtw2.setString(1, name);
               stmtw2.setLong(2, wdate);
               rs = stmtw2.executeQuery();

               if (rs.next()) {

                  wtime = rs.getInt(1);
               }
               stmtw2.close();

               
               // SystemUtils.logError("DEBUG: Process Wait List. Oldest Date = " +wdate+ ", oldest Time = " +wtime);       // trace this attempt
               

               if (wtime != 0) {

                  //
                  //   get the earliest time on this reg date on wait list
                  //
                  PreparedStatement stmtw4 = con.prepareStatement (
                     "SELECT username1, username2, username3, username4, username5, player1, player2, player3, player4, player5, " +
                     "p1cw, p2cw, p3cw, p4cw, p5cw, id " +
                     "FROM evntsup2b " +
                     "WHERE name = ? AND r_date = ? AND r_time = ? AND wait != 0 AND inactive = 0");

                  stmtw4.clearParameters();
                  stmtw4.setString(1, name);
                  stmtw4.setLong(2, wdate);
                  stmtw4.setInt(3, wtime);
                  rs = stmtw4.executeQuery();

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
                     signup_id = rs.getInt("id");
                  }
                  stmtw4.close();

                  PreparedStatement pstmtw3 = con.prepareStatement (
                     "UPDATE evntsup2b SET wait = 0 " +
                     "WHERE name = ? AND id = ? AND inactive = 0");

                  pstmtw3.clearParameters();
                  pstmtw3.setString(1, name);
                  pstmtw3.setInt(2, signup_id);

                  count = pstmtw3.executeUpdate();

                  pstmtw3.close();
                  
                  if (count == 0) {
                     
                     Utilities.logError("Process Wait List for Club " +club+ ". Error removing wait list. Name = " +name+ ", signup_id = " +signup_id);
                     
                  }

               } // end if wtime != 0

            } // end if wdate != 0

         } // end if teams < max (room for more teams)

      } catch (Exception e1) {
         
         Utilities.logError("Error in Proshop_evntSignUp.verify - Process Wait List for Club " +club+ ". Exception = " + e1.getMessage());
      }

   }     // end of IF checkWait

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   //
   //  Get this today's date and the event date
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   int thisYear = cal.get(Calendar.YEAR);       
   int thisMonth = cal.get(Calendar.MONTH) +1;
   int thisDay = cal.get(Calendar.DAY_OF_MONTH);

   long today = (thisYear * 10000) + (thisMonth * 100) + thisDay;   // today's date   

   long date = (year * 10000) + (month * 100) + day;            // create event date value

   if (season == 0 && today > date) {
     
      sendemail = 0;        // do not send emails if event has already passed
   }
     
   //
   //  Send the email if required
   //
   if (sendemail != 0 && suppressEmails.equalsIgnoreCase( "no" )) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      String activity_name = "";
      try { activity_name = getActivity.getActivityName(sess_activity_id, con); }
      catch (Exception ignore) {}
      
      //
      //  Set the values in the email parm block
      //
      parme.activity_id = sess_activity_id;
      parme.club = club;
      parme.guests = guests;
      parme.type = "event";
      parme.activity_name = activity_name;
      parme.actual_activity_name = activity_name;
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

    /*   remove - they will use the new config option
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
   int signup_id  = 0;

   //
   // Get all the parameters entered
   //
   String name = req.getParameter("name");           //  name of event
   String course = req.getParameter("course");        //  name of course
   String sid = req.getParameter("id");               //  id of entry in evntsup table

   //
   //  Convert the values from string to int
   //
   try {
      signup_id = Integer.parseInt(sid);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Clear the 'in_use' flag for this entry (!!! SHOULDN'T WE ONLY CLEAR THIS IF IT STILL HAS THE IN_USE_BY FIELD SET TO THIS USER???)
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE evntsup2b SET in_use = 0 WHERE id = ?"); // name = ? AND courseName = ? AND 

      pstmt1.clearParameters();
      pstmt1.setInt(1, signup_id);
      pstmt1.executeUpdate();

      pstmt1.close();

   }
   catch (Exception ignore) {

   }

   //
   //  Prompt user to return to Proshop_events2
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
   out.println("<Title>Proshop Event Registration Page</Title>");

   out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?name=" + name + "&course=" + course + "\">");
   out.println("</HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
   out.println("<BR><BR>Thank you, the event entry has been returned to the system without changes.");
   out.println("<BR><BR>");

   out.println("<font size=\"2\">");
   out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</CENTER></BODY></HTML>");
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
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out, String p1, String p2, String p3, String p4, String p5) {


   if (p1.equals( "" )) {

      p1 = " ";             // use space instead of null
   }

   if (p2.equals( "" )) {

      p2 = " ";             // use space instead of null
   }

   if (p3.equals( "" )) {

      p3 = " ";             // use space instead of null
   }

   if (p4.equals( "" )) {

      p4 = " ";             // use space instead of null
   }

   if (p5.equals( "" )) {

      p5 = " ";             // use space instead of null
   }

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "',&nbsp;&nbsp;&nbsp;'" + p2 + "',&nbsp;&nbsp;&nbsp;'" + p3 + "',&nbsp;&nbsp;&nbsp;'" + p4 + "',&nbsp;&nbsp;&nbsp;'" + p5 + "'");
   out.println("<BR><BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
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
                         int guest_id1, int guest_id2, int guest_id3, int guest_id4, int guest_id5, 
                         String notes, String hides, String course, String name, String suppressEmails, 
                         String rev, int signup_id, int event_id, PrintWriter out) {
 
    out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
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
    out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
    out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
    out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
    out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
    out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");
    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
    out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
    //out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
    
    out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
    
    out.println("</form>");
 
 }
 
  
 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void continueForm(String player1, String player2, String player3, String player4, String player5, 
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
                         String notes, String hides, String course, String name, String suppressEmails, 
                         int skip, boolean jumpToSP, int season, String rev, int signup_id, int event_id,
                         PrintWriter out) {
 
    out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
    out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
    out.println("<input type=\"hidden\" name=\"skip\" value=\"" + skip + "\">");
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
    out.println("<input type=\"hidden\" name=\"season\" value=\"" + season + "\">");
    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
    out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
    if (jumpToSP) out.println("<input type=\"hidden\" name=\"submitForm\" value=\"\">");
    //out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
    
    out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form></font>");

    out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
    if (jumpToSP) out.println("<input type=\"hidden\" name=\"sp\" value=\"yes\">");
    out.println("<input type=\"hidden\" name=\"skip\" value=\"" + skip + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
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
    out.println("<input type=\"hidden\" name=\"season\" value=\"" + season + "\">");
    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
    out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
    out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\">");
    out.println("</form>");
 
 }
 
 
 private static void extraInfoForm(
                        String player1, String player2, String player3, String player4, String player5, 
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
                        String userg1, String userg2, String userg3, String userg4, String userg5,
                        int guest_id1, int guest_id2, int guest_id3, int guest_id4, int guest_id5,
                        String notes, String hides, String course, String name, String suppressEmails, 
                        int skip, String club, String rev, int signup_id, int event_id, PrintWriter out) {

    int i = 0;

    String tmp_color = "yellow";

    if (club.equals("demobrock") || club.equals("blackstone")) {

        tmp_color = "chartreuse";
    }

    String tmp_style = "style=\"background-color:" + tmp_color + "\"";

    out.println("<table border=1 cols=1 bgcolor=\"#F5F5DC\" cellpadding=3>");
    out.println("<tr><td width=620 align=center><font size=2>");
    out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this event registration.");
    out.println("&nbsp; If you want to return without changes, <b>do not ");
    out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Cancel Sign-up</b> option below.<br>");
    out.println("<b>Note</b>:&nbsp;&nbsp;The questions with <b>" + ((club.equals("demobrock") || club.equals("blackstone")) ? "green" : tmp_color) + " boxes are required</b> and need to be completed in order to complete your sign-up.");
    out.println("</font></td></tr>");
    out.println("</table><br>");
    
    out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" target=\"_top\">");
    
    // if we need to ask any of the 'guest only' questions
    if (guests > 0 && (ask_homeclub + ask_phone + ask_address + ask_email > 0)) {
    
        out.println("<table border=1 bgcolor=\"#F5F5DC\" align=center>");
        out.println("<tr bgcolor=\"#336633\"><td align=center>");
        out.println("<font color=white size=3><b>Additional Guest Only Information</b></font></td>");
        out.println("</tr><tr><td>");
    
        out.println("<table><tr style=\"font-size:10pt;font-weight:bold\"><td>Guests</td>");
        if (ask_homeclub > 0) out.println("<td>Home Club, State</td>");
        if (ask_phone > 0) out.println("<td>Phone</td>");
        if (ask_address > 0) out.println("<td>Mailing Address</td>");
        if (ask_email > 0) out.println("<td>Email</td>");
        out.println("</tr>");

        for (i = 0; i < players; i++) {

            out.println("<tr style=\"font-size:10pt\">");

            if (!gstA[i].equals("")) {

                out.println("<td>" + gstA[i] + "&nbsp; &nbsp;</td>");
                if (ask_homeclub > 0) out.println("<td><input type=text size=20 maxlength=50 name=homeclub" + (i + 1) + " value=\"" + homeclub[i] + "\" " + ((ask_homeclub == 2) ? tmp_style : "") + "></td>");
                if (ask_phone > 0) out.println("<td><input type=text size=11 maxlength=50 name=phone" + (i + 1) + " value=\"" + phone[i] + "\" " + ((ask_phone == 2) ? tmp_style : "") + "></td>");
                if (ask_address > 0) out.println("<td><input type=text size=24 maxlength=64 name=address" + (i + 1) + " value=\"" + address[i] + "\" " + ((ask_address == 2) ? tmp_style : "") + "></td>");
                if (ask_email > 0) out.println("<td><input type=text size=16 maxlength=64 name=email" + (i + 1) + " value=\"" + email[i] + "\" " + ((ask_email == 2) ? tmp_style : "") + "></td>");
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
        if (ask_otherA1 + ask_otherA2 + ask_otherA3 > 0) {
            out.println("<br><table align=center style=\"border: #929292 dashed 1px\"><tr><td align=center><b>Questions:</b></td></tr>");
            if (ask_otherA1 > 0 && !otherQ1.equals("")) out.println("<tr><td><font size=2>&nbsp;1. " + otherQ1 + "&nbsp;</font></td></tr>");
            if (ask_otherA2 > 0 && !otherQ2.equals("")) out.println("<tr><td><font size=2>&nbsp;2. " + otherQ2 + "&nbsp;</font></td></tr>");
            if (ask_otherA3 > 0 && !otherQ3.equals("")) out.println("<tr><td><font size=2>&nbsp;3. " + otherQ3 + "&nbsp;</font></td></tr>");
            out.println("</table><br>");
        }
        
        // header row
        out.println("<table><tr style=\"font-size:10pt;font-weight:bold\"><td>Players</td>");
        if (ask_shirtsize > 0) out.println("<td>Shirt Size</td>");
        if (ask_shoesize > 0) out.println("<td>Shoe Size</td>");
        if (ask_otherA1 > 0 && !otherQ1.equals("")) out.println("<td>Question #1</td>");
        if (ask_otherA2 > 0 && !otherQ2.equals("")) out.println("<td>Question #2</td>");
        if (ask_otherA3 > 0 && !otherQ3.equals("")) out.println("<td>Question #3</td>");
        out.println("</tr>");
        
        for (i = 0; i < players; i++) {
        
            out.println("<tr style=\"font-size:10pt\">");

            if (!player[i].equals("")) {

                out.println("<td>" + player[i] + "&nbsp; &nbsp;</td>");
                if (ask_shirtsize > 0) out.println("<td><input type=text size=6 maxlength=8 name=shirt" + (i + 1) + " value=\"" + shirt[i] + "\" " + ((ask_shirtsize == 2) ? tmp_style : "") + "></td>");
                if (ask_shoesize > 0) out.println("<td><input type=text size=6 maxlength=8 name=shoe" + (i + 1) + " value=\"" + shoe[i] + "\" " + ((ask_shoesize == 2) ? tmp_style : "") + "></td>");
                if (ask_otherA1 > 0 && !otherQ1.equals("")) out.println("<td><input type=text size=20 maxlength=50 name=other" + (i + 1) + "A1 value=\"" + otherA1[i] + "\" " + ((ask_otherA1 == 2) ? tmp_style : "") + "></td>");
                if (ask_otherA2 > 0 && !otherQ2.equals("")) out.println("<td><input type=text size=20 maxlength=50 name=other" + (i + 1) + "A2 value=\"" + otherA2[i] + "\" " + ((ask_otherA2 == 2) ? tmp_style : "") + "></td>");
                if (ask_otherA3 > 0 && !otherQ3.equals("")) out.println("<td><input type=text size=20 maxlength=50 name=other" + (i + 1) + "A3 value=\"" + otherA3[i] + "\" " + ((ask_otherA3 == 2) ? tmp_style : "") + "></td>");
            }
            
            out.println("</tr>");
        }
        
        out.println("</table>");
    
    }
    
    out.println("</td></tr>");
    out.println("</table>");
    out.println("<br>");
    
    out.println("<input type=\"hidden\" name=\"sp\" value=\"yes\">");
    
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"id\" value=\"" + signup_id + "\">");
    out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
    out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
    out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hides + "\">");
    out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
    out.println("<input type=\"hidden\" name=\"skip\" value=\"" + skip + "\">");
    //out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
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
    
    out.println("<table align=center><tr><td align=center>");
    out.println("<input type=\"submit\" value=\"Complete Sign-Up\" name=\"submitForm\" style=\"text-decoration:underline;\">");
    out.println("</form></td>");
    out.println("</tr><tr>");
    out.println("<form action=\"Proshop_evntSignUp\" method=\"post\" name=\"can\"><td align=center><br>");
    out.println("<input type=\"hidden\" name=\"id\" value=" + signup_id + ">");
    out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
    out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
    //out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
    out.println("<input type=\"submit\" value=\"Cancel Changes\" name=\"cancel\"></form>");
    out.println("</td></tr></table>");
    
 }
 
}
