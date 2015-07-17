/***************************************************************************************
 *   MemberTLT_slot:  This servlet will process the 'Reserve Notification' request from
 *                    the Member's Sheet page.
 *
 *
 *   Called by:  Member_sheet (doPost)
 *               self on cancel request
 *               Member_teelist (doPost)
 *
 *
 *   Created: 9/10/2006   
 *
 *   Last Updated:
 *
 *        1/10/14   Bear Creek GC (bearcreekgc) - Added custom to send te pro an email notifictaion whenever a notification is booked/modified/canceled (case 2344).
 *        4/26/12   Remove some references to 'tee times'.
 *        2/01/12   Updated for new skin
 *       10/06/11   Fixed bug in restriction checking where i was being used as an index instead of ind, causing false positives.
 *       10/20/10   Populate new parmEmail fields
 *        4/24/10   Updated moveguest Javascript function to handle the new use_guestdb value being passed to it.
 *        2/02/10   Trim the notes in verify
 *       12/09/09   When looking for events only check those that are active.
 *       12/02/09   Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *       10/04/09   Added activity isolation to the buddy list
 *        8/04/09   San Francisco GC (sfgc) - Set default Mode of Trans. to CAD for all guest types
 *        7/21/09   Winged Foot (wingedfoot) - Restrict members from having more than two unconverted notifications originated by them
 *                  on the books at any given time (case 1666).
 *        5/01/09   Always force members to list a member before guests unless unaccompanieds are ok.
 *       10/07/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *        6/18/08   Change flow for winged foot guest customs
 *        5/22/08   Lookup date for exisiting notifications if date was not passed in (tee list/calendar)
 *        4/12/08   Winged Foot - Custom guest quota per hour - no more than 9 guests per hour (case 1419).
 *        4/11/08   Fixed 5-some problem
 *        4/30/07   Winged Foot - Set a default Mode of Trans for Guests (case #1094).
 *        4/13/07   Winged Foot - Custom guest restrictions for Legacy Preferred Associates (case #1097).
 *        4/13/07   Winged Foot - Custom tee time quotas for Legacy Preferred Associates (case #1097).
 *        4/13/07   Winged Foot - Custom guest quotas - check number of guests per family per season.
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
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmNSlot;
import com.foretees.common.verifyNSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyNCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.alphaTable;
import com.foretees.common.BigDate;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.Utilities;
import com.foretees.common.formUtil;
import com.foretees.common.parmSlotPage;
import com.foretees.common.Connect;
import com.foretees.common.timeUtil;
import com.foretees.common.reqUtil;

public class MemberTLT_slot extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
   static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
   static long Hdate8 = ProcessConstants.colDay;     // Columbus Day


 //*************************************************************
 // Process the request from MemberTLT_sheet and processing below
 //*************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    doPost(req, resp);
 }

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   String temps = "";
   int contimes = 0;

   //
   //   Check for initial entry from Member_sheet and destined for Member_slotm 
   //
   //      See if more than one tee time was requested
   //
   if (req.getParameter("contimes") != null) {        // if 'consecutive tee times' count provided

      temps = req.getParameter("contimes");
      contimes = Integer.parseInt(temps);

      if (contimes > 1) {                            // if more than one tee time requested

         Member_slotm slotm = new Member_slotm();      // create an instance of Member_slotm so we can call it (static vs non-static)

         slotm.doPost(req, resp);                     // call 'doPost' method in _slotm
         return;                                      // exit
      }
   }


   //
   //  Prevent caching so all buttons are properly displayed, etc.
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your club manager.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get this session's username (to be saved in teecurr)
   //
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   String name = (String)session.getAttribute("name");          // get users full name
   String userMship = (String)session.getAttribute("mship");    // get users mship type
   String userMtype = (String)session.getAttribute("mtype");    // get users mship type
   String pcw = (String)session.getAttribute("wc");             // get users walk/cart preference
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
    boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
    String clubName = Utilities.getClubName(con, true);        // get the full name of this club
    boolean json_mode = (req.getParameter("json_mode") != null);

   //
   //  parm block to hold the tee time parms
   //
   parmNSlot nSlotParms = new parmNSlot();        // allocate a parm block
   nSlotParms.club = club;                        // save club name
   
   
    // Create fill slot page parameters we already know, fill in the rest later
    parmSlotPage slotPageParms = new parmSlotPage();

    slotPageParms.club = club;
    slotPageParms.club_name = clubName;
    slotPageParms.slot_url = "MemberTLT_slot";
    slotPageParms.notice_message = "";
    slotPageParms.slot_help_url = "../member_help_slot_instruct.htm";
    slotPageParms.slot_type = "Notification";
    slotPageParms.member_tbd_text = "Member";
    slotPageParms.page_title = "Member Notification";
    slotPageParms.bread_crumb = "Member Notification";
    slotPageParms.show_fb = true;
    slotPageParms.show_transport = true;
    slotPageParms.user = user;
    slotPageParms.mship = userMship;
    slotPageParms.zip_code = (String) session.getAttribute("zipcode");

    // Store request parameters in our slotPageParms, in case we need them for call-back later
    // This will trigger a "uses unchecked or unsafe operations" warning while compiling.  
    // Perhaps there is a better way to do this, but for now it works.
    List<String> reqNames = (ArrayList<String>) Collections.list((Enumeration<String>) req.getParameterNames());
    for (String reqName : reqNames) {
        slotPageParms.callback_map.put(reqName, req.getParameter(reqName));
    }
    slotPageParms.callback_map.put("json_mode", "true");

   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb' - a request from Member_sheet ** (NOT USED)
   //      'cancel'  - a cancel request from user via MemberTLT_slot (return with no changes)
   //      'letter'  - a request to list member names (from self)
   //      'submitForm'  - a notification request (from self)
   //      'remove'  - a 'cancel notification' request (from self - Cancel Notification)
   //      'return'  - a return from verify
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, club, con);       // process cancel request
      return;
   }

   if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session, resp);
      return;
   }

   String jump = "0";                     // jump index - default to zero (for _sheet)

   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   //
   //   Submit = 'time:fb' or 'letter'
   //
   int notify_id = 0;
   int in_use = 0;
   //int count = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
   long date = 0;
   int fb = 0;
   int x = 0;
   int xCount = 0;
   int i = 0;
   int hide = 0;
   //int nowc = 0;
   int lstate = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   
   int players_per_group = 0;
   int players = 0;
   int visible_players_per_group = 0;
   
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
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";

   String sdate = "";
   String stime = "";
   String ampm = "";
   String sfb = "";
   String notes = "";
   String hides = "";
   String msg = "";
   String slstate = "";
   String pname = "";

   //
   //  2-some indicator used for some custom requests
   //
   boolean twoSomeOnly = false;
   
   boolean skipGuests = false;

   //
   //   Flag for Cancel Tee Time button (show or not show)
   //
   boolean allowCancel = true;              // default to 'allow'

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con);

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");       //  name of the day
   String index = Utilities.getParameterString(req, "index", "995");        //  index value of day (needed by Member_sheet when returning)
   String p5 = ""; //req.getParameter("p5");        //  5-somes supported
   String course = req.getParameter("course");      //  Name of Course
   if (course == null) course = "";
   
   if (req.getParameter("fb") != null) {            // if fb was passed

      sfb = req.getParameter("fb");
   }

   if (req.getParameter("sdate") != null) {         // if date was passed in sdate

      sdate = req.getParameter("sdate");
   }

   if (req.getParameter("date") != null) {          // if date was passed in date

      sdate = req.getParameter("date");
   }

   if (req.getParameter("lstate") != null) {        // if lottery state was passed

      slstate = req.getParameter("lstate");

      if (!slstate.equals( "" )) {

         lstate = Integer.parseInt(slstate);
      }
   }

   if (req.getParameter("stime") != null) {         // if time was passed in stime

      stime = req.getParameter("stime");

   } else if (req.getParameter("time") != null) {         // if time was passed in stime

      time = reqUtil.getParameterInteger(req,"time",0);
      stime = timeUtil.get12HourTime(time);

   } else {                                         // call from Member_sheet (this else block is probably not used in this servlet)

      //
      //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
      //
      Enumeration enum1 = req.getParameterNames();  // get the parm name passed

      while (enum1.hasMoreElements()) {

         pname = (String) enum1.nextElement();          // get parm name

         if (pname.startsWith( "time" )) {

            stime = req.getParameter(pname);            //  value = time of tee time requested (hh:mm AM/PM)

            StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon

            sfb = tok.nextToken();                      // skip past 'time '
            sfb = tok.nextToken();                      // get the front/back indicator value
         }
      }
   }

   

    try {

        PreparedStatement pstmt = con.prepareStatement (
            "SELECT fives " +
            "FROM clubparm2 " +
            "WHERE courseName = ?");

        pstmt.clearParameters();
        pstmt.setString(1, course);
        rs = pstmt.executeQuery();      // execute the prepared stmt

        if (rs.next()) {
            
            if (rs.getInt(1) == 0) {
                p5 = "No";   
            } else {
                p5 = "Yes";
            }
        }
        
        pstmt.close();

    }
    catch (Exception e1) {

        SystemUtils.logError("Error looking up 5-some option.  Exception: " + e1.getMessage());
    }
   
   
   
    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();             // get todays date
    int thisYear = cal.get(Calendar.YEAR);              // get the year

    //
    // Get our notify uid if we are here to edit an existing notification, if absent set to zero to indicate new notification
    //
    String snid = req.getParameter("notifyId");
    if (snid == null) snid = "0";

    //
    //  Convert the values from string to int
    //
    try {

        notify_id = Integer.parseInt(snid);
        date = Long.parseLong(sdate);
        fb = Integer.parseInt(sfb);
    }
    catch (NumberFormatException e) {
    }

    // lookup date for this notification if date was missing (coming from tee list/calendar pages)
    if (date == 0 && notify_id > 0) {
        
        try {

            PreparedStatement pstmt = con.prepareStatement ( "" +
                "SELECT DATE_FORMAT(n.req_datetime, '%Y%m%d') AS date, "
                    + " n.req_datetime " +
                "FROM notifications " +
                "WHERE notification_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, notify_id);
            rs = pstmt.executeQuery();
/*
            if (rs.next()) {
                date = rs.getInt("date");
                sdate = ""+date;
                long restime = timeUtil.getClubUnixTimeFromDb(req, rs.getString("req_datetime"));
                int[] dateTime = timeUtil.getClubDateTime(req, restime);
                stime = timeUtil.get12HourTime(dateTime[timeUtil.TIME]);
            }
*/
        }
        catch (Exception e) {

            SystemUtils.buildDatabaseErrMsg(e.toString(), "Error looking up date for this notification.", out, false);
        }
        
    } // end if date missing for exisitng notifications
    
    
    //
    //  isolate yy, mm, dd
    //
    yy = date / 10000;
    temp = yy * 10000;
    mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    dd = mm - temp;
    mm = mm / 100;


    int start_time = 0;
    int end_time = 0;
    
     // Start configure block.  We will break out of this if we encounter an issue.  
     configure_slot:
     {
         if (req.getParameter("return") != null) {     // if this is a return from verify - time = hhmm

             try {
                 time = Integer.parseInt(stime);
             } catch (NumberFormatException e) {
             }

             //
             //  create a time string for display
             //
             hr = time / 100;
             min = time - (hr * 100);
             ampm = " AM";
             if (hr > 11) {
                 ampm = " PM";
                 if (hr > 12) {
                     hr = hr - 12;
                 }
             }
             stime = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;

         } else {

             // if we are not here to edit an existing notification
             //if (notify_id != 0) {

             out.println("<!-- stime=" + stime + " -->");
             //
             //  Parse the time parm to separate hh, mm, am/pm and convert to military time
             //  (received as 'hh:mm xx'   where xx = am or pm)
             //
             String shr = "";
             String smin = "";
             try {

                 StringTokenizer tok = new StringTokenizer(stime, ": ");     // space is the default token
                 shr = tok.nextToken();
                 smin = tok.nextToken();
                 ampm = tok.nextToken();
             } catch (NoSuchElementException e) {
                 out.println("<p><b>ERROR: parsing date: " + stime + " - " + e.toString() + "</b></p>");
             }

             //
             //  Convert the values from string to int
             //
             try {

                 hr = Integer.parseInt(shr);
                 min = Integer.parseInt(smin);
             } catch (NumberFormatException e) {
                 out.println("<p><b>ERROR: converting values - " + e.toString() + "</b></p>");
             }

             if (ampm.equalsIgnoreCase("PM") && hr != 12) {
                 hr = hr + 12;
             }

             time = hr * 100;
             time = time + min;          // military time

             // we're doing this client side, so do not need to do here for now
             if (time < start_time || time > end_time) {
                 //out.println("<p>Notifications are only being accepted between hours of " + start_time + "AM and " + end_time + "PM.</p>");
             }


             //
             // Check to see if this request can be accepted at this time, check events, restrictions, and blockers.
             //

             // check for restrictions

             //
             //  Get all restrictions for this day and user (for use when checking each tee time below)
             //

             //
             //  parm block to hold the member restrictions for this date and member
             //
             parmRest parmr = new parmRest();
             parmr.user = user;
             parmr.mship = userMship;
             parmr.mtype = userMtype;
             parmr.date = date;
             parmr.day = day_name;
             parmr.course = course;

             try {
                 getRests.getAll(con, parmr);       // get the restrictions
             } catch (Exception e) {
                 SystemUtils.buildDatabaseErrMsg(e.toString(), "Error looking for restrictions.", out, false);
             }
             int ind = 0;
             boolean allow = true;
             boolean suspend = false;

             while (ind < parmr.MAX && allow == true && !parmr.restName[ind].equals("")) {

                 if (parmr.applies[ind] == 1 && parmr.stime[ind] <= time && parmr.etime[ind] >= time) {                // matching time ?

                     // Check to make sure no suspensions apply
                     suspend = false;
                     for (int k = 0; k < parmr.MAX; k++) {

                         if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {
                             k = parmr.MAX;   // don't bother checking any more
                         } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    //tee_time falls within a suspension
                             suspend = true;
                             k = parmr.MAX;     // don't bother checking any more
                         }
                     }

                     if (!suspend) {

                         if ((parmr.courseName[ind].equals("-ALL-")) || (parmr.courseName[ind].equals(course))) {  // course ?

                             //if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb2 ))) {    // matching f/b ?

                             allow = false;                    // match found
                             //}
                         }
                     }
                 }
                 ind++;
             } // end of while

             if (!allow) {

                 if (new_skin) {
               
                     slotPageParms.page_start_button_go_back = true;
                     slotPageParms.page_start_title = "Restriction Found";
                     slotPageParms.page_start_notifications.add("A restriction was found at the same time of your requested notification."
                             + "<BR><BR>Return to the tee sheet and view a list of restrictions in place for today."
                             + "<br>Each restriction is displayed as a button that you can click on for specific details.");
                     break configure_slot;
            
                 } else {
                     out.println(SystemUtils.HeadTitle("Restriction Found"));
                     out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                     out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                     out.println("<center>");
                     out.println("<BR><BR><H3>Restriction Found</H3>");
                     out.println("<BR>A restriction was found at the same time of your requested notification.");
                     out.println("<BR><BR>Return to the tee sheet and view a list of restrictions in place for today.<br>"
                             + "Each restriction is displayed as a button that you can click on for specific details.");

                     out.println("<form action=\"MemberTLT_slot\" method=\"post\" name=\"can\">");
                     out.println("<input type=\"hidden\" name=\"notifyId\" value=" + notify_id + ">");
                     out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                     out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                     out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

                     //out.println("<br><br><form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
                     //out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                     //out.println("</form></font>");

                     out.println("<BR><BR>");

                     return;
                 }

             }


             // check for events
             try {

                 PreparedStatement pstmt = con.prepareStatement(""
                         + "SELECT * "
                         + "FROM events2b "
                         + "WHERE "
                         + "date = ? AND "
                         + "stime < ? AND "
                         + "etime > ? AND "
                         + "courseName = ? AND inactive = 0");
                 pstmt.clearParameters();
                 pstmt.setLong(1, date);
                 pstmt.setLong(2, time);
                 pstmt.setLong(3, time);
                 pstmt.setString(4, course);
                 rs = pstmt.executeQuery();

                 if (rs.next()) {

                     String tmp_sampm = "AM";
                     String tmp_eampm = "AM";
                     int tmp_shr = rs.getInt("start_hr");
                     if (tmp_shr > 12) {
                         tmp_shr -= 12;
                         tmp_sampm = "PM";
                     }
                     int tmp_ehr = rs.getInt("end_hr");
                     if (tmp_ehr > 12) {
                         tmp_ehr -= 12;
                         tmp_eampm = "PM";
                     }
                     int tmp_smin = rs.getInt("start_min");
                     int tmp_emin = rs.getInt("end_min");

                     String tmp_stime = tmp_shr + ":" + SystemUtils.ensureDoubleDigit(tmp_smin) + " " + tmp_sampm;
                     String tmp_etime = tmp_ehr + ":" + SystemUtils.ensureDoubleDigit(tmp_emin) + " " + tmp_eampm;
                     
                     if (new_skin) {

                         slotPageParms.page_start_button_go_back = true;
                         slotPageParms.page_start_title = "Conflicting Event Found";
                         slotPageParms.page_start_notifications.add("An event was found at the same time of your requested notification."
                            + "<BR><BR>The matching event is \"" + rs.getString("name") + "\" and it's from " + tmp_stime + " to " + tmp_etime + ".");
                         break configure_slot;

                     } else {

                         out.println(SystemUtils.HeadTitle("Conflicting Event Found"));
                         out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                         out.println("<center>");
                         out.println("<BR><BR><H3>Conflicting Event Found</H3>");
                         out.println("<BR>An event was found at the same time of your requested notification.");
                         out.println("<BR><BR>The matching event is \"" + rs.getString("name") + "\" and it's from " + tmp_stime + " to " + tmp_etime + ".");

                         out.println("<form action=\"MemberTLT_slot\" method=\"post\" name=\"can\">");
                         out.println("<input type=\"hidden\" name=\"notifyId\" value=" + notify_id + ">");
                         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                         out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                         out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                         out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                         out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                         out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

                         //out.println("<br><br><form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
                         //out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                         //out.println("</form></font>");

                         out.println("<BR><BR>");

                         return;
                     }

                 }

             } catch (Exception e) {

                 SystemUtils.buildDatabaseErrMsg(e.toString(), "Error looking for conflicting events.", out, false);
             }

             if (p5.equals("Yes")) {
                 // check 5-some restrictions by checking the fives2 table as set the p5 var accordingly
                 try {

                     PreparedStatement pstmt = con.prepareStatement(
                             "SELECT name "
                             + "FROM fives2 "
                             + "WHERE "
                             + "sdate < ? AND edate > ? AND "
                             + "stime < ? AND etime > ? AND "
                             + "(courseName = ? OR courseName = \"-ALL-\") AND "
                             + "(recurr = \"every day\" OR "
                             + "(DATE_FORMAT(?, \"%w\") = 1 AND recurr = \"every monday\") OR "
                             + "(DATE_FORMAT(?, \"%w\") = 2 AND recurr = \"every tuesday\") OR "
                             + "(DATE_FORMAT(?, \"%w\") = 3 AND recurr = \"every wednesday\") OR "
                             + "(DATE_FORMAT(?, \"%w\") = 4 AND recurr = \"every thursday\") OR "
                             + "(DATE_FORMAT(?, \"%w\") = 5 AND recurr = \"every friday\") OR "
                             + "((DATE_FORMAT(?, \"%w\") = 0 OR DATE_FORMAT(?, \"%w\") = 6) AND recurr = \"all weekends\") OR "
                             + "((DATE_FORMAT(?, \"%w\") != 0 AND DATE_FORMAT(?, \"%w\") != 6) AND recurr = \"all weekdays\") "
                             + ");");

                     pstmt.clearParameters();
                     pstmt.setLong(1, date);
                     pstmt.setLong(2, date);
                     pstmt.setInt(3, time);
                     pstmt.setInt(4, time);
                     pstmt.setString(5, course);
                     pstmt.setLong(6, date);
                     pstmt.setLong(7, date);
                     pstmt.setLong(8, date);
                     pstmt.setLong(9, date);
                     pstmt.setLong(10, date);
                     pstmt.setLong(11, date);
                     pstmt.setLong(12, date);
                     pstmt.setLong(13, date);
                     pstmt.setLong(14, date);
                     rs = pstmt.executeQuery();      // execute the prepared stmt

                     if (rs.next()) {
                         p5 = "No";
                     } else {
                         p5 = "Yes";
                     }

                     pstmt.close();

                 } catch (Exception e1) {

                     msg = "Error checking 5-some restrictions.  Exception: " + e1.getMessage();
                 }
             }
             //} // end if notify

         }


         // see what we are here to do
         // if letter is here OR return is here then get form data from the request object
         if ((req.getParameter("letter") != null) || (req.getParameter("return") != null)) {

             // get the player info from the parms passed
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
             notes = req.getParameter("notes");
             hides = req.getParameter("hide");

             String p9s = "";

             if (req.getParameter("p91") != null) {
                 p9s = req.getParameter("p91");
                 p91 = Integer.parseInt(p9s);
             }
             if (req.getParameter("p92") != null) {
                 p9s = req.getParameter("p92");
                 p92 = Integer.parseInt(p9s);
             }
             if (req.getParameter("p93") != null) {
                 p9s = req.getParameter("p93");
                 p93 = Integer.parseInt(p9s);
             }
             if (req.getParameter("p94") != null) {
                 p9s = req.getParameter("p94");
                 p94 = Integer.parseInt(p9s);
             }
             if (req.getParameter("p95") != null) {
                 p9s = req.getParameter("p95");
                 p95 = Integer.parseInt(p9s);
             }

             //
             //  Convert hide from string to int
             //
             hide = 0;                       // init to No
             if (!hides.equals("0")) {
                 hide = 1;
             }


         } else {


             //
             //  Get the players' names and check if this tee slot is already in use
             //
             //slotParms.day = day_name;            // save day name

             //
             //  Verify the required parms exist
             //
             //if (date == 0 || time == 0 || course == null || user.equals( "" ) || user == null) {
        /*
             if (notify_id == 0 || time == 0 || course == null || user.equals( "" ) || user == null) {
             
             msg = "Error in MemberTLT_slot - checkInUse Parms - for user " +user+ " at " +club+ ".  Date= " +date+ ", time= " +time+ ", course= " +course+ ", fb= " +fb+ ", index= " +index;   // build msg
             SystemUtils.logError(msg);
             in_use = 1;          // make like the time is busy  
             }
              */

             // see if we are here to load up an existing notification for editing
             if (notify_id != 0) {
                 try {

                     in_use = verifyNSlot.checkInUse(notify_id, user, nSlotParms, con, out);
                     slotPageParms.time_remaining = verifySlot.getSlotHoldTime(session);
                     course = nSlotParms.course;
                     day_name = nSlotParms.day;

                 } catch (Exception e) {
                     if(!new_skin){
                        out.println("<p><b>ERROR: checkInUse - " + e.toString() + "</b></p>");
                     }
                 }
             }

             if(!new_skin){
                out.println("<!-- snid=" + snid + " | notify_id=" + notify_id + " | nSlotParms.players=" + nSlotParms.players + " | nSlotParms.course = " + nSlotParms.course + " -->");
             }
             
             if (in_use != 0) {              // if time slot already in use
                 
                 if (new_skin) {

                     slotPageParms.page_start_button_go_back = true;
                     if (msg.endsWith("after connection closed.")) {
                         slotPageParms.page_start_title = "Session Timed Out";
                         slotPageParms.page_start_notifications.add("Sorry, but your session has timed out or your database connection has been lost"
                                 + "<BR><BR>Please exit ForeTees and try again.");
                         break configure_slot;
                     } else {
                         slotPageParms.page_start_title = "Notification Busy";
                         slotPageParms.page_start_notifications.add("Sorry, but this notification is currently busy."
                                 + "<BR><BR>Please select another time or try again later.");
                         break configure_slot;
                     }

                 } else {

                     out.println(SystemUtils.HeadTitle("Notification In Use Error"));
                     out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                     out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                     if (msg.endsWith("after connection closed.")) {                           // if session timed out error           
                         out.println("<CENTER><BR><BR><H2>Session Timed Out</H2>");
                         out.println("<BR><BR>Sorry, but your session has timed out or your database connection has been lost.<BR>");
                         out.println("<BR>Please exit ForeTees and try again.");
                     } else {
                         out.println("<CENTER><BR><BR><H2>Notification Busy</H2>");
                         out.println("<BR><BR>Sorry, but this notification is currently busy.<BR>");
                         out.println("<BR>Please select another time or try again later.");
                     }
                     out.println("<BR><BR>");
                     if (index.equals("999")) {       // if from Member_teelist (my tee times)

                         out.println("<font size=\"2\">");
                         out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain.htm\">");
                         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                         out.println("</form></font>");
                     } else {

                         if (index.equals("995")) {       // if from Member_teelist_list (old my tee times)

                             out.println("<font size=\"2\">");
                             out.println("<form method=\"get\" action=\"/" + rev + "/member_teemain2.htm\">");
                             out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                             out.println("</form></font>");

                         } else {

                             if (index.equals("888")) {       // if from Member_searchmem

                                 out.println("<font size=\"2\">");
                                 out.println("<form method=\"get\" action=\"/" + rev + "/member_searchmem.htm\">");
                                 out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                 out.println("</form></font>");

                             } else {                           // from tee sheet

                                 out.println("<font size=\"2\">");
                                 out.println("<form method=\"get\" action=\"/" + rev + "/member_selmain.htm\">");
                                 out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                                 out.println("</form></font>");
                             }
                         }
                     }
                     out.println("</CENTER></BODY></HTML>");
                     out.close();
                     return;
                 }
             }

             //
             //  tee time is available - get current player info
             //
             player1 = nSlotParms.player1;
             player2 = nSlotParms.player2;
             player3 = nSlotParms.player3;
             player4 = nSlotParms.player4;
             player5 = nSlotParms.player5;
             p1cw = nSlotParms.p1cw;
             p2cw = nSlotParms.p2cw;
             p3cw = nSlotParms.p3cw;
             p4cw = nSlotParms.p4cw;
             p5cw = nSlotParms.p5cw;
             notes = nSlotParms.notes;
             hide = nSlotParms.hide;
             p91 = nSlotParms.p91;
             p92 = nSlotParms.p92;
             p93 = nSlotParms.p93;
             p94 = nSlotParms.p94;
             p95 = nSlotParms.p95;
             user1 = nSlotParms.user1;
             user2 = nSlotParms.user2;
             user3 = nSlotParms.user3;
             user4 = nSlotParms.user4;
             user5 = nSlotParms.user5;

             // if there is a player in pos 5, but 5-somes are blocked, then force an allow
             if (!player5.equals("") && p5.equalsIgnoreCase("No")) {
                 p5 = "Yes";
             }

             //
             //  Now check if the tee time has changed since the member displayed the tee sheet.
             //  Get the player values passed from Member_sheet and compare against those now in the tee time.
             //
        /*
             if (!index.equals( "888" ) && !index.equals( "995" ) && !index.equals( "999" )) {  // if from Member_sheet
             
             String wasP1 = req.getParameter("wasP1");  // get the player values from tee sheet
             String wasP2 = req.getParameter("wasP2");
             String wasP3 = req.getParameter("wasP3");
             String wasP4 = req.getParameter("wasP4");
             String wasP5 = req.getParameter("wasP5");
             
             if (!wasP1.equals( player1 ) || !wasP2.equals( player2 ) || !wasP3.equals( player3 ) || 
             !wasP4.equals( player4 ) || !wasP5.equals( player5 )) {
             
             returnToMemSheet(date, time, fb, course, day_name, club, out, con);
             return;
             }
             }
              */

             /*      
             //
             //  Hacker check - if the tee time is full, then make sure this member is part of it
             //
             if (p5.equals( "Yes" )) {      // if 5-somes
             
             if (!player1.equals( "" ) && !player2.equals( "" ) &&
             !player3.equals( "" ) && !player4.equals( "" ) && !player5.equals( "" )) {      // if full
             
             if (!user1.equalsIgnoreCase( user ) && !user2.equalsIgnoreCase( user ) && !user3.equalsIgnoreCase( user ) &&
             !user4.equalsIgnoreCase( user ) && !user5.equalsIgnoreCase( user ) && !nSlotParms.orig_by.equalsIgnoreCase( user )) {   // if member not part of it
             
             returnToMemSheet(date, time, fb, course, day_name, club, out, con);           // act like its busy
             return;
             }
             }
             
             } else {
             
             if (!player1.equals( "" ) && !player2.equals( "" ) &&
             !player3.equals( "" ) && !player4.equals( "" )) {                              // if full
             
             if (!user1.equalsIgnoreCase( user ) && !user2.equalsIgnoreCase( user ) && !user3.equalsIgnoreCase( user ) &&
             !user4.equalsIgnoreCase( user ) && !nSlotParms.orig_by.equalsIgnoreCase( user )) {               // if member not part of it
             
             returnToMemSheet(date, time, fb, course, day_name, club, out, con);          // act like its busy
             return;
             }
             }
             }
              */

         } // end seeing what we are here to do











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
         //  Get the walk/cart options available  
         //
         try {

             getParms.getTmodes(con, parmc, course);
         } catch (Exception e1) {

             msg = "Get wc options. ";
             dbError(out, e1, msg);
             return;
         }

         if (!pcw.equals("")) {

             i = 0;
             loopi1:
             while (i < parmc.tmode_limit) {

                 if (parmc.tmodea[i].equals(pcw)) {

                     break loopi1;
                 }
                 i++;
             }
             if (i > parmc.tmode_limit - 1) {       // if we went all the way without a match

                 pcw = "";        // force user to specify one
             }
         }
         i = 0;

         //
         //  Set user's name as first open player to be placed in name slot for them
         //
         //  First, check if user is already included in this slot.
         //  Member_sheet already checked if slot is full and user not one of them!!
         //
         if ((!player1.equals(name)) && (!player2.equals(name)) && (!player3.equals(name)) && (!player4.equals(name)) && (!player5.equals(name))) {

             if (player1.equals("")) {

                 player1 = name;
                 p1cw = pcw;

             } else {

                 if (player2.equals("")) {

                     player2 = name;
                     p2cw = pcw;

                 } else {

                     if (player3.equals("")) {

                         player3 = name;
                         p3cw = pcw;

                     } else {

                         if (player4.equals("")) {

                             player4 = name;
                             p4cw = pcw;

                         } else {

                             if ((p5.equals("Yes")) && (player5.equals(""))) {

                                 player5 = name;
                                 p5cw = pcw;

                             }
                         }
                     }
                 }
             }
         }
         
         //
         //  Get usernames from tee time in case not already present
         //
         if (user1.equals("") || user1 == null) {

             try {

                 PreparedStatement pstmt = con.prepareStatement(
                         "SELECT username1, username2, username3, username4, username5 "
                         + "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                 pstmt.clearParameters();        // clear the parms
                 pstmt.setLong(1, date);         // put the parm in pstmt
                 pstmt.setInt(2, time);
                 pstmt.setInt(3, fb);
                 pstmt.setString(4, course);
                 rs = pstmt.executeQuery();      // execute the prepared stmt

                 if (rs.next()) {

                     user1 = rs.getString(1);
                     user2 = rs.getString(2);
                     user3 = rs.getString(3);
                     user4 = rs.getString(4);
                     user5 = rs.getString(5);
                 }

                 pstmt.close();

             } catch (Exception e1) {
                 
                 //
                 //  save message in /" +rev+ "/error.txt
                 //
                 // log it
                 msg = "Error in MemberTLT_slot for " + name + ", user " + user + " at " + club + ".  Error = " + msg;   // build msg
                 SystemUtils.logError(msg);

                 msg = "Exception Received Verifying Users.  Exception: " + e1.getMessage();
                 
                 if (new_skin) {

                     slotPageParms.page_start_button_go_back = true;
                     slotPageParms.page_start_title = "Error";
                     slotPageParms.page_start_notifications.add("An error has occurred that prevents you from continuing.  The session cookie used by the system has been corrupted.  Please return, logout and then try again."
                             + "<br><br>If this continues, please email us at support@foretees.com and include this error message.");
                     slotPageParms.page_start_notifications.add("Error: " + msg);
                     break configure_slot;

                 } else {

                     //
                     //  Error getting user field(s) - reject
                     //
                     out.println("</form></font></td></tr>");
                     out.println("</table>");
                     out.println("<br><br>");
                     out.println("<font size=\"4\"><b>Error</b></font><br>");
                     out.println("<font size=\"2\">");
                     out.println("An error has occurred that prevents you from continuing.  The session cookie<br>");
                     out.println("used by the system has been corrupted.  Please return, logout and then try again.");
                     out.println("<br><br>If this continues, please email us at support@foretees.com and include this error message.");
                     out.println("<br><br>Error: " + msg);
                     out.println("<br><br>");
                     out.println("<form action=\"MemberTLT_slot\" method=\"post\" name=\"can\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
                     out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
                     out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("<input type=\"submit\" value=\"Return\" name=\"cancel\"></form>");
                     out.println("</td></tr></table>");
                     out.println("</center></body></html>");
                     out.close();

                     return;
                 }
             }
         }

         //
         //  Determine if the 'Cancel Tee Time' button should be displayed.
         //
         allowCancel = true;             // default to Yes

         //
         //  Do not allow user to cancel the tee time if not already in it
         //
         if (!user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3)
                 && !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5) && !user.equalsIgnoreCase(nSlotParms.orig_by)) {

             allowCancel = false;
         }
         
         //
         //     Check the club db table for X and guests
         //
         try {

             getClub.getParms(con, parm);        // get the club parms
             x = parm.x;
         } catch (Exception exc) {             // SQL Error - ignore guest and x

             x = 0;
         }
         
         //
         //  Check if Guest Type table should be displayed
         //
         skipGuests = false;

     }
     
     // Set player count/size
            players_per_group = ((p5.equals("Yes")) ? 5 : 4);
            players = players_per_group;
            visible_players_per_group = players_per_group;
            if (twoSomeOnly) {
                visible_players_per_group = 2;
            } //else if (threeSomeOnly) {
             //   visible_players_per_group = 3;
            //}

        //slotPageParms.time_remaining = verifySlot.getSlotHoldTime(session);
        slotPageParms.hide_notes = hide;
        slotPageParms.show_member_tbd = (x != 0);
        slotPageParms.edit_mode = (!nSlotParms.player1.equals(""));
        slotPageParms.show_tbd = (x != 0);
        /*
        if (club.equals("congressional") && visible_players_per_group == 2) {
            slotPageParms.default_fb_value = 1; // 0 or 1
            slotPageParms.set_default_fb_value = true;
            slotPageParms.lock_fb = true;
        }
         * 
         */
        slotPageParms.allow_cancel = allowCancel;
        slotPageParms.show_member_select = true;
        slotPageParms.show_guest_types = (skipGuests == false);

        slotPageParms.player_count = players;
        slotPageParms.players_per_group = players_per_group;
        slotPageParms.visible_players_per_group = visible_players_per_group;
        slotPageParms.jump = jump;
        slotPageParms.index = index;
        //slotPageParms.day_name = day_name;

        slotPageParms.fb = fb;
        slotPageParms.slots = 1;

        slotPageParms.yy = (int) yy;
        slotPageParms.mm = (int) mm;
        slotPageParms.dd = (int) dd;

        slotPageParms.course = course;
        slotPageParms.return_course = course;
        slotPageParms.day = day_name;
        slotPageParms.stime = stime;
        slotPageParms.course_disp = course;
        slotPageParms.sdate = sdate;
        slotPageParms.date = Integer.parseInt(sdate);
        slotPageParms.time = time;
        slotPageParms.p5 = p5;
        slotPageParms.notes = notes;
        slotPageParms.name = name;
        slotPageParms.id = notify_id;

        slotPageParms.pcw = pcw; // User's default PCW

        //slotPageParms.guest_id_a = new int[]{guest_id1, guest_id2, guest_id3, guest_id4, guest_id5};
        slotPageParms.p9_a = new int[]{p91, p92, p93, p94, p95};
        //slotPageParms.time_a = slotParms.getIntArrayByName("time%");

        slotPageParms.player_a = new String[]{player1, player2, player3, player4, player5};
        slotPageParms.pcw_a = new String[]{p1cw, p2cw, p3cw, p4cw, p5cw};

        //slotPageParms.tmodes_list = nonProTmodes;  // Tmode that will be displayed
        slotPageParms.allowed_tmodes_list = Arrays.asList(parmc.tmodea);  // Tmode that will be allowed when set as defaults

        slotPageParms.course_parms = parmc;
        
        slotPageParms.options.put("mainInstructionsNoTimer",
                Arrays.asList(new String[]{"Add [options.playerPlural] to the request and click on \"[options.buttonSubmit]\" to complete the request. <a class=\"helpButton\" href=\"#\" data-fthelp=\"TLTslot_page_help\" title=\"How do I use this registration page?\"><span>How do I use this registration page?</span></a>"})
                );

        // Set players that cannot be editied on form
        /*
        slotPageParms.lock_player_a = new boolean[]{blockP1, blockP2, blockP3, blockP4, blockP5};
        // loop over the lock_player array, and set properly
        for (int i2 = 0; i2 < slotPageParms.lock_player_a.length; i2++) {
            if ((!(!slotPageParms.player_a[i2].equals("") || club.equals("pgmunl") || (club.startsWith("gaa") && !club.endsWith("class")))) && slotPageParms.lock_player_a[i2] == true) {
                slotPageParms.lock_player_a[i2] = false; // Allow editing if player is empty, and custom does not match club
            }
        }
        */
        
        // Set tranport types
        Common_slot.setDefaultTransportTypes(slotPageParms);
        // Set transport legend
        Common_slot.setTransportLegend(slotPageParms, parmc, new_skin);
        // Set transport modes
        Common_slot.setTransportModes(slotPageParms, parmc);
        // Set guest types
        Common_slot.setGuestTypes(con, slotPageParms, parm);

        // Define the fields we will include when submitting the form
        slotPageParms.slot_submit_map.put("date", "date");
        slotPageParms.slot_submit_map.put("sdate", "sdate");
        slotPageParms.slot_submit_map.put("day", "day");
        slotPageParms.slot_submit_map.put("stime", "stime");
        slotPageParms.slot_submit_map.put("time", "time");
        slotPageParms.slot_submit_map.put("fb", "fb");
        slotPageParms.slot_submit_map.put("mm", "mm");
        slotPageParms.slot_submit_map.put("yy", "yy");
        slotPageParms.slot_submit_map.put("index", "index");
        slotPageParms.slot_submit_map.put("course", "course");
        slotPageParms.slot_submit_map.put("p5", "p5");
        slotPageParms.slot_submit_map.put("jump", "jump");
        slotPageParms.slot_submit_map.put("notifyId", "id");
        slotPageParms.slot_submit_map.put("hide", "hide_notes");
        slotPageParms.slot_submit_map.put("notes", "notes");
        slotPageParms.slot_submit_map.put("player%", "player_a");
        slotPageParms.slot_submit_map.put("p9%", "p9_a");
        slotPageParms.slot_submit_map.put("p%cw", "pcw_a");
        //slotPageParms.slot_submit_map.put("guest_id%", "guest_id_a");

     if (new_skin) {
         //
         //  Build the HTML page to prompt user for names
         //
         if (json_mode) {
             out.print(Common_slot.slotJson(slotPageParms));
         } else {
             Common_slot.displaySlotPage(out, slotPageParms, req, con);
         }
         
     } else { // Old skin
         //
         //  Build the HTML page to prompt user for names
         //
         out.println("<HTML>");
         out.println("<HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web utilities/foretees2.css\" type=\"text/css\">");
         out.println("<title>Member Golf Notification Page</title>");
         //
         //*******************************************************************
         //  User clicked on a letter - submit the form for the letter
         //*******************************************************************
         //
         out.println("<script type='text/javascript'>");            // Submit the form when clicking on a letter
         out.println("<!--");
         out.println("function subletter(x) {");

//      out.println("alert(x);");
         out.println("document.forms['playerform'].letter.value = x;");         // put the letter in the parm
         out.println("document.forms['playerform'].submit();");        // submit the form
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
         out.println("<script type='text/javascript'>");            // Erase name script    (Note:  Put these in file???)  what other files use these scripts, just proshop_slot?
         out.println("<!--");

         out.println("function erasename(pPlayerPos, pCWoption) {");

         out.println("eval(\"document.forms['playerform'].\" + pPlayerPos + \".value = '';\")");           // clear the player name field
         out.println("eval(\"document.forms['playerform'].\" + pCWoption + \".selectedIndex = -1;\")");     // clear the player wc field
         out.println("}");                  // end of script function
         out.println("// -->");
         out.println("</script>");          // End of script

         //
         //*******************************************************************
         //  Erase text area - (Notes)      erasetext and movenotes
         //*******************************************************************
         //
         out.println("<script type='text/javascript'>");            // Erase text area script
         out.println("<!--");
         out.println("function erasetext(pos1) {");
         out.println("eval(\"document.forms['playerform'].\" + pos1 + \".value = '';\")");           // clear the player field
         out.println("}");                  // end of script function
         out.println("// -->");
         out.println("</script>");          // End of script

         out.println("<script type='text/javascript'>");             // Move Notes into textarea
         out.println("<!--");
         out.println("function movenotes() {");
         out.println("var oldnotes = document.forms['playerform'].oldnotes.value;");
         out.println("document.forms['playerform'].notes.value = oldnotes;");   // put notes in text area
         out.println("}");                  // end of script function
         out.println("// -->");
         out.println("</script>");          // End of script

         //
         //*********************************************************************************
         //  Move name script
         //*********************************************************************************
         //
         out.println("<script type='text/javascript'>");            // Move name script
         out.println("<!--");

         out.println("function movename(namewc) {");

         out.println("del = ':';");                               // deliminator is a colon
         out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
         out.println("var name = array[0];");
         out.println("var wc = array[1];");
         out.println("var f = document.forms['playerform'];");
         out.println("skip = 0;");

         out.println("var player1 = f.player1.value;");
         out.println("var player2 = f.player2.value;");

         if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

             out.println("var player3 = f.player3.value;");
             out.println("var player4 = f.player4.value;");

             if (p5.equals("Yes")) {
                 out.println("var player5 = f.player5.value;");
             }
         }

         out.println("if (( name != 'x') && ( name != 'X')) {");


         if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

             if (p5.equals("Yes")) {
                 out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {");
             } else {
                 out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4)) {");
             }
         } else {
             out.println("if (( name == player1) || ( name == player2)) {");
         }
         out.println("skip = 1;");
         out.println("}");
         out.println("}");

         out.println("if (skip == 0) {");

         out.println("if (player1 == '') {");                    // if player1 is empty
         out.println("f.player1.value = name;");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
         out.println("f.p1cw.value = wc;");
         out.println("}");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("f.player2.value = name;");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
         out.println("f.p2cw.value = wc;");
         out.println("}");

         if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
             out.println("} else {");

             out.println("if (player3 == '') {");                    // if player3 is empty
             out.println("f.player3.value = name;");
             out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
             out.println("f.p3cw.value = wc;");
             out.println("}");
             out.println("} else {");

             out.println("if (player4 == '') {");                    // if player4 is empty
             out.println("f.player4.value = name;");
             out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
             out.println("f.p4cw.value = wc;");
             out.println("}");

             if (p5.equals("Yes")) {
                 out.println("} else {");
                 out.println("if (player5 == '') {");                    // if player5 is empty
                 out.println("f.player5.value = name;");
                 out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
                 out.println("f.p5cw.value = wc;");
                 out.println("}");
                 out.println("}");
             }

             out.println("}");
             out.println("}");
         }                       // end of IF 2-some only time
         out.println("}");
         out.println("}");

         out.println("}");                  // end of dup name chack

         out.println("}");                  // end of script function
         out.println("// -->");
         out.println("</script>");                               // End of script

         //
         //*******************************************************************
         //  Move a Guest Name or 'X' into the tee slot
         //*******************************************************************
         //
         out.println("<script type='text/javascript'>");            // Move Guest Name script
         out.println("<!--");

         out.println("function moveguest(namewc) {");

         out.println("var f = document.forms['playerform'];");
         //out.println("var name = namewc;");

         out.println("array = namewc.split('|');"); // split string (partner_name, partner_id)
         out.println("var name = array[0];");
         out.println("var use_guestdb = array[1]");

         out.println("var defCW = '';");
         out.println("var player1 = f.player1.value;");
         out.println("var player2 = f.player2.value;");

         if (club.equals("wingedfoot") || club.equals("sfgc")) {

             out.println("defCW = 'CAD';");         // set default Mode of Trans
         }

         if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

             out.println("var player3 = f.player3.value;");
             out.println("var player4 = f.player4.value;");

             if (p5.equals("Yes")) {
                 out.println("var player5 = f.player5.value;");
             }
         }

         //  set spc to ' ' if name to move isn't an 'X'
         out.println("var spc = '';");
         out.println("if (name != 'X' && name != 'x') {");
         out.println("   spc = ' ';");
         out.println("}");

         out.println("if (player1 == '') {");                    // if player1 is empty
         out.println("f.player1.value = name + spc;");
         out.println("f.p1cw.value = defCW;");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
         out.println("f.player2.value = name + spc;");
         out.println("f.p2cw.value = defCW;");

         if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

             out.println("} else {");

             out.println("if (player3 == '') {");                    // if player3 is empty
             out.println("f.player3.value = name + spc;");
             out.println("f.p3cw.value = defCW;");
             out.println("} else {");

             out.println("if (player4 == '') {");                    // if player4 is empty
             out.println("f.player4.value = name + spc;");
             out.println("f.p4cw.value = defCW;");

             if (p5.equals("Yes")) {
                 out.println("} else {");
                 out.println("if (player5 == '') {");                    // if player5 is empty
                 out.println("f.player5.value = name + spc;");
                 out.println("f.p5cw.value = defCW;");

                 out.println("}");
             }

             out.println("}");
             out.println("}");
         }
         out.println("}");
         out.println("}");

         out.println("}");                  // end of script function
         out.println("// -->");
         out.println("</script>");                               // End of script
         //*******************************************************************************************

         out.println("</HEAD>");
         out.println("<body onLoad=\"movenotes()\" bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

         out.println("<table border=\"0\" width=\"100%\" valign=\"top\">");  // large table for whole page
         out.println("<tr><td valign=\"top\">");

         out.println("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#336633\" align=\"center\" valign=\"top\">");
         out.println("<tr><td align=\"center\" width=\"160\" bgcolor=\"#F5F5DC\">");
         out.println("<font color=\"Darkred\" size=\"3\">DO NOT USE");
         out.println("<br>Your Browser's<br>Back Button!!</font>");
//     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
         out.println("</td>");

         out.println("<td align=\"center\">");
         out.println("<font color=\"#ffffff\" size=\"5\">ForeTees Member Notification</font>");
         out.println("</font></td>");

         out.println("<td align=\"center\" width=\"160\">");
         out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
         out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
         out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " + thisYear + " All rights reserved.");
         out.println("</font></td>");
         out.println("</tr></table>");

         out.println("<table width=\"100%\" border=\"0\" align=\"center\">");          // table for main page
         out.println("<tr><td align=\"center\"><br>");

         out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"2\" color=\"Darkred\">");
         out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this notification.");
         out.println("&nbsp; If you want to return without completing a notification, <b>do not ");
         out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
         out.println("button below.");
         out.println("</font></td></tr>");
         out.println("</table>");

         out.println("<font size=\"2\" color=\"black\">");
         out.println("<br>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
         out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Time:&nbsp;&nbsp;<b>" + stime + "</b>");
         if (!course.equals("")) {
             out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
         }
         out.println("<br></font>");

         out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions and Go Back button

         out.println("<br><br>");
         out.println("<font size=\"2\" color=\"Darkred\">");
         out.println("<form action=\"MemberTLT_slot\" method=\"post\" name=\"can\">");
         out.println("<input type=\"hidden\" name=\"notifyId\" value=" + notify_id + ">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
         out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
         out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
         out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
         out.println("Return<br>w/o Changes:<br>");
         out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");

         out.println("<br><br><br><br>");
         out.println("</font><font size=\"1\" color=\"black\">");
         out.println("<a href=\"#\" onClick=\"window.open ('/" + rev + "/member_help_slot_instruct.htm', 'newwindow', config='Height=560, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("<img src=\"/" + rev + "/images/instructions.gif\" border=0>");
         out.println("<br>Click for Help</a>");

         out.println("</font></td>");

         out.println("<form action=\"MemberTLT_slot\" method=\"post\" name=\"playerform\" id=\"playerform\">");

         out.println("<input type=\"hidden\" name=\"notifyId\" value=" + notify_id + ">");

         out.println("<td align=\"center\" valign=\"top\">");

         out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" align=\"center\" width=\"370\">");  // table for player selection
         out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
         out.println("<font color=\"#ffffff\" size=\"2\">");
         out.println("<b>Add or Remove Players</b>&nbsp;&nbsp; Note: Click on Names --->");
         out.println("</font></td></tr>");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\">");

         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
         out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");

         out.println("<img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player1', 'p1cw')\" style=\"cursor:hand\">");
         out.println("1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"30\">");
         out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\" id=\"p1cw\">");

         out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
         for (i = 0; i < 16; i++) {        // get all c/w options

             if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p1cw)) {
                 out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
             }
         }
         out.println("</select>");
         if (p91 == 1) {
             out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p91\" value=\"1\">");
         } else {
             out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p91\" value=\"1\">");
         }

         out.println("<br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player2', 'p2cw')\" style=\"cursor:hand\">");
         out.println("2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"30\">");
         out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\" id=\"p2cw\">");

         out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
         for (i = 0; i < 16; i++) {

             if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p2cw)) {
                 out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
             }
         }
         out.println("</select>");
         if (p92 == 1) {
             out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p92\" value=\"1\">");
         } else {
             out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p92\" value=\"1\">");
         }

         //
         //  Custom - Piedmont DC - do not allow more than 2 players on Sat & Sun mornings
         //
         if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

             out.println("<br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player3', 'p3cw')\" style=\"cursor:hand\">");
             out.println("3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"30\">");
             out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\" id=\"p3cw\">");

             out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
             for (i = 0; i < 16; i++) {         // get all c/w options

                 if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p3cw)) {
                     out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
                 }
             }
             out.println("</select>");
             if (p93 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p93\" value=\"1\">");
             } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p93\" value=\"1\">");
             }

             out.println("<br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player4', 'p4cw')\" style=\"cursor:hand\">");
             out.println("4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"30\">");
             out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\" id=\"p4cw\">");

             out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
             for (i = 0; i < 16; i++) {       // get all c/w options

                 if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p4cw)) {
                     out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
                 }
             }
             out.println("</select>");
             if (p94 == 1) {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p94\" value=\"1\">");
             } else {
                 out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p94\" value=\"1\">");
             }

             if (p5.equals("Yes")) {

                 out.println("<br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasename('player5', 'p5cw')\" style=\"cursor:hand\">");
                 out.println("5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"30\">");
                 out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");

                 out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                 for (i = 0; i < 16; i++) {      // get all c/w options

                     if (!parmc.tmodea[i].equals("") && !parmc.tmodea[i].equals(p5cw)) {
                         out.println("<option value=\"" + parmc.tmodea[i] + "\">" + parmc.tmodea[i] + "</option>");
                     }
                 }
                 out.println("</select>");
                 if (p95 == 1) {
                     out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" checked name=\"p95\" value=\"1\">");
                 } else {
                     out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" name=\"p95\" value=\"1\">");
                 }
             } else {

                 out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                 out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
             }

         } else {

             out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
             out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
             out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
             out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
             out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
             out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
         }      // end of IF Piedmont 

         //
         //   Notes
         //
         //   Script will put any existing notes in the textarea (value= doesn't work)
         //
         out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

         if (hide != 0) {      // if proshop wants to hide the notes, do not display the text box or notes

             out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes

         } else {

             out.println("<br><br><img src=\"/" + rev + "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
             out.println("Notes to Pro:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"22\" rows=\"2\">");
             out.println("</textarea>");
         }
         out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=" + sdate + ">");
         out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
         out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
         out.println("<input type=\"hidden\" name=\"mm\" value=" + mm + ">");
         out.println("<input type=\"hidden\" name=\"yy\" value=" + yy + ">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"skip\" value=\"no\">");
         out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=" + p5 + ">");
         out.println("<input type=\"hidden\" name=\"hide\" value=" + hide + ">");
         out.println("<input type=\"hidden\" name=\"lstate\" value=" + lstate + ">");
         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");

         out.println("<br><font size=\"1\">");
         for (i = 0; i < 16; i++) {
             if (!parmc.tmodea[i].equals("")) {
                 out.println(parmc.tmodea[i] + " = " + parmc.tmode[i] + "&nbsp;&nbsp;");
             }
         }
         out.println("</font><br>");

         //
         //  Check if the 'Cancel Tee Time' button should be allowed
         //
         if (allowCancel == true) {

             out.println("<input type=submit value=\"Cancel Notification\" name=\"remove\" onclick=\"return confirm('Are you sure you want to remove this notification?')\">&nbsp;&nbsp;&nbsp;");
         }

         if (!nSlotParms.player1.equals("")) {   // if this is a change (not a new tee time)
             out.println("<input type=submit value=\"Submit Changes\" name=\"submitForm\">");
         } else {
             out.println("<input type=submit value=\"Submit\" name=\"submitForm\">");
         }
         out.println("</font></td></tr>");
         out.println("</table>");

         if (!userMship.equals("Special")) {

             out.println("<br>");
             out.println("<table border=\"1\" bgcolor=\"#FFFFFF\" align=\"left\" width=\"370\">");  // table for guest intructions
             out.println("<tr><td>");
             out.println("<font size=\"2\">");
             out.println("<b>NOTE:</b> &nbsp;");
             out.println("To add a Guest, click on one of the Guest types listed in the 'Guest Types' box to the right. ");
             out.println("Add the guest immediately after the host member. ");
             out.println("To include the name of a guest, type a space and the name after the guest type word(s) in the player box above.");
             out.println("</font></td></tr>");
             out.println("</table>");
         }

         out.println("</td>");                                // end of table and column
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
                 String mship = "";
                 String wc = "";

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
                             "SELECT name_last, name_first, name_mi, m_ship, wc FROM member2b "
                             + "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

                     stmt2.clearParameters();               // clear the parms
                     stmt2.setString(1, letter);            // put the parm in stmt
                     rs = stmt2.executeQuery();             // execute the prepared stmt

                     out.println("<tr><td align=\"left\"><font size=\"2\">");
                     out.println("<select size=\"20\" name=\"bname\" onClick=\"movename(this.value)\">"); // movename(this.form.bname.value)

                     while (rs.next()) {

                         last = rs.getString(1);
                         first = rs.getString(2);
                         mid = rs.getString(3);
                         mship = rs.getString(4);
                         wc = rs.getString(5);           // walk/cart preference

                         i = 0;
                         loopi3:
                         while (i < 16) {             // make sure wc is supported

                             if (parmc.tmodea[i].equals(wc)) {

                                 break loopi3;
                             }
                             i++;
                         }
                         if (i > 15) {       // if we went all the way without a match

                             wc = parmc.tmodea[0];    // use default option
                         }

                         i = 0;

                         if (mid.equals("")) {

                             bname = first + " " + last;
                             dname = last + ", " + first;
                         } else {

                             bname = first + " " + mid + " " + last;
                             dname = last + ", " + first + " " + mid;
                         }

                         wname = bname + ":" + wc;              // combine name:wc for script

                         out.println("<option value=\"" + wname + "\">" + dname + "</option>");

                     }

                     out.println("</select>");
                     out.println("</font></td></tr>");

                     stmt2.close();
                 } catch (Exception ignore) {
                 }

                 out.println("</table>");

             }        // end of IF Partner List or letter

         }           // not letter display

         if (letter.equals("") || letter.equals("Partner List")) {  // if no letter or Partner List request

             alphaTable.displayPartnerList(user, sess_activity_id, 0, con, out);

         }        // end of if letter display

         out.println("</td>");                                      // end of this column
         out.println("<td width=\"200\" valign=\"top\">");



         //
         //   Output the Alphabit Table for Members' Last Names
         //
         alphaTable.getTable(out, user);

         if (x != 0) {  // if X supported and NOT Forest Highlands

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

         if (skipGuests == false) {

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

                 if (xCount < 2) {

                     xCount = 2;             // set size to at least 2
                 }
                 if (xCount > 8) {

                     xCount = 8;             // set size to no more than 8 showing at once (it will scroll)
                 }
                 out.println("<tr><td align=\"left\"><font size=\"1\" face=\"Helvetica, Arial, Sans-serif\">");
                 out.println("<b>**</b> Add guests immediately<br><b>after</b> host member.<br>");
                 out.println("</font><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                 out.println("<select size=\"" + xCount + "\" name=\"xname\" onClick=\"moveguest(this.form.xname.value)\">");
                 for (i = 0; i < parm.MAX_Guests; i++) {

                     if (!parm.guest[i].equals("") && parm.gOpt[i] == 0) {   // if guest name is open for members

                         out.println("<option value=\"" + parm.guest[i] + "\">" + parm.guest[i] + "</option>");
                     }
                 }
                 out.println("</select>");
                 out.println("</font></td></tr></table>");      // end of this table 

             } else {

                 out.println("</table>");      // end the table if none specified
             }
         }            // end of IF skipGuests

         out.println("</td>");             // end of this column
         out.println("</tr>");
         out.println("</form>");     // end of playerform
         out.println("</table>");      // end of large table containing 4 smaller tables (columns)

         out.println("</font></td></tr>");
         out.println("</table>");                      // end of main page table
         //
         //  End of HTML page
         //
         out.println("</td></tr>");
         out.println("</table>");                      // end of whole page table
         out.println("</font></body></html>");
     }
     out.close();

 }  // end of doPost


 // *********************************************************
 //  Process reservation request from MemberTLT_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


    //Statement stmt = null;
    //Statement estmt = null;
    //Statement stmtN = null;
    ResultSet rs = null;
    //ResultSet rs7 = null;

    //
    //  Get this session's user name
    //
    String user = (String)session.getAttribute("user");
    //String fullName = (String)session.getAttribute("name");
    String club = (String)session.getAttribute("club");
    //String posType = (String)session.getAttribute("posType");
     boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

     Map<String, Object> result_map = new LinkedHashMap<String, Object>();
     Map<String, Object> hidden_field_map = new LinkedHashMap<String, Object>();

     Gson gson_obj = new Gson();

    //
    // Get our notify uid if we are here to edit an existing notification, if absent set to zero to indicate new notification
    //
    String snid = req.getParameter("notifyId");
    if (snid == null) snid = "0";
    int notify_id = 0;

    //
    //  Convert the values from string to int
    //
    try {

        notify_id = Integer.parseInt(snid);
    }
    catch (NumberFormatException e) {
    }

    //
    // init all variables
    //
    //int thisTime = 0;
    int time = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int fb = 0;
    //int fb2 = 0;
    //int t_fb = 0;
    int x = 0;
    int xhrs = 0;
    int calYear = 0;
    int calMonth = 0;
    int calDay = 0;
    int calHr = 0;
    int calMin = 0;
    int memNew = 0;
    int memMod = 0;
    //int i = 0;
    int ind = 0;
    int xcount = 0;
    //int year = 0;
    //int month = 0;
    //int dayNum = 0;
    //int mtimes = 0;
    int sendemail = 0;
    int emailNew = 0;
    int emailMod = 0;
    int emailCan = 0;
    //int mems = 0;
    //int players = 0;
    //int oldplayers = 0;
    //int lstate = 0;
    int gi = 0;
    int adv_time = 0;

    long temp = 0;
    long ldd = 0;
    long date = 0;
    long adv_date = 0;
    //long dateStart = 0;
    //long dateEnd = 0;

    String player = "";
    String sfb = "";
    //String sfb2 = "";
    //String course2 = "";
    //String notes = "";
    //String notes2 = "";
    //String rcourse = "";
    //String period = "";
    //String mperiod = "";
    String msg = "";
    //String plyr1 = "";
    //String plyr2 = "";
    //String plyr3 = "";
    //String plyr4 = "";
    //String plyr5 = "";
    String memberName = "";
    String p9s = "";
    String p1 = "";

    boolean error = false;
    //boolean guestError = false;
    //boolean oakskip = false;

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);

    //
    //  parm block to hold the tee time parms
    //
    parmNSlot nSlotParms = new parmNSlot();          // allocate a parm block

    nSlotParms.notify_id = notify_id;
    
    nSlotParms.hndcp1 = 99;     // init handicaps
    nSlotParms.hndcp2 = 99;
    nSlotParms.hndcp3 = 99;
    nSlotParms.hndcp4 = 99;
    nSlotParms.hndcp5 = 99;

    //
    // Get all the parameters entered
    //
    String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    String smm = req.getParameter("mm");               //  month of tee time
    String syy = req.getParameter("yy");               //  year of tee time
    String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
    sfb = req.getParameter("fb");                      //  Front/Back indicator

    nSlotParms.p5 = req.getParameter("p5");            //  5-somes supported for this slot
    nSlotParms.course = req.getParameter("course");    //  name of course
    nSlotParms.player1 = req.getParameter("player1");
    nSlotParms.player2 = req.getParameter("player2");
    nSlotParms.player3 = req.getParameter("player3");
    nSlotParms.player4 = req.getParameter("player4");
    nSlotParms.player5 = req.getParameter("player5");
    nSlotParms.p1cw = req.getParameter("p1cw");
    nSlotParms.p2cw = req.getParameter("p2cw");
    nSlotParms.p3cw = req.getParameter("p3cw");
    nSlotParms.p4cw = req.getParameter("p4cw");
    nSlotParms.p5cw = req.getParameter("p5cw");
    nSlotParms.day = req.getParameter("day");          // name of day
    nSlotParms.notes = req.getParameter("notes").trim();      // Notes
    nSlotParms.hides = req.getParameter("hide");       // Hide Notes

    //
    //  set 9-hole options
    //
    nSlotParms.p91 = 0;                       // init to 18 holes
    nSlotParms.p92 = 0;
    nSlotParms.p93 = 0;
    nSlotParms.p94 = 0;
    nSlotParms.p95 = 0;

    if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      nSlotParms.p91 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      nSlotParms.p92 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      nSlotParms.p93 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      nSlotParms.p94 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      nSlotParms.p95 = Integer.parseInt(p9s);
    }

    //
    //  Ensure that there are no null player fields
    //
    if (nSlotParms.player1 == null ) nSlotParms.player1 = "";
    if (nSlotParms.player2 == null ) nSlotParms.player2 = "";
    if (nSlotParms.player3 == null ) nSlotParms.player3 = "";
    if (nSlotParms.player4 == null ) nSlotParms.player4 = "";
    if (nSlotParms.player5 == null ) nSlotParms.player5 = "";
    
    if (nSlotParms.p1cw == null ) nSlotParms.p1cw = "";
    if (nSlotParms.p2cw == null ) nSlotParms.p2cw = "";
    if (nSlotParms.p3cw == null ) nSlotParms.p3cw = "";
    if (nSlotParms.p4cw == null ) nSlotParms.p4cw = "";
    if (nSlotParms.p5cw == null ) nSlotParms.p5cw = "";

    //
    //  Convert date & time from string to int
    //
    try {
        
        date = Long.parseLong(sdate);
        time = Integer.parseInt(stime);
        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        fb = Integer.parseInt(sfb);
    }
    catch (NumberFormatException e) {
    }

    long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

    //
    //  convert the index value from string to numeric - save both
    //
    try {
        
        ind = Integer.parseInt(index);
    } catch (NumberFormatException e) {
    }

    String jump = "0";                     // jump index - default to zero (for _sheet)

    if (req.getParameter("jump") != null) {            // if jump index provided

        jump = req.getParameter("jump");
    }

    //
    //  Get the length of Notes (max length of 254 chars)
    //
    int notesL = 0;

    if (!nSlotParms.notes.equals( "" )) {

        notesL = nSlotParms.notes.length();       // get length of notes
    }

    //
    //   use yy and mm and date to determine dd (from tee time's date)
    //
    temp = yy * 10000;
    temp = temp + (mm * 100);
    ldd = date - temp;            // get day of month from date

    dd = (int) ldd;               // convert to int

    int hr = time / 100;
    int min = time - (hr * 100);

    //
    //  put parms in Parameter Object for portability
    //
    nSlotParms.req_datetime = yy + "-" + mm + "-" + dd + " " + hr + ":" + min + ":00";
    nSlotParms.date = date;
    nSlotParms.time = time;
    nSlotParms.mm = mm;
    nSlotParms.yy = yy;
    nSlotParms.dd = dd;
    nSlotParms.fb = fb;
    nSlotParms.ind = ind;      // index value
    nSlotParms.sfb = sfb; 
    nSlotParms.jump = jump;
    nSlotParms.club = club;    // name of club

    //
    //  Determine 'ind' value in case we came from Member_teelist or _searchmem (ind = 888 or 999 or 995)
    //
    int indReal = getDaysBetween(date);            // get # of days in between today and the date


    //
    //  Check if this tee slot is still 'in use' and still in use by this user??
    //
    //  This is necessary because the user may have gone away while holding this slot.  If the
    //  slot timed out (system timer), the slot would be marked 'not in use' and another
    //  user could pick it up.  The original holder could be trying to use it now.
    //
    if (notify_id != 0) {

        // we are here to edit an existing notification
        try {

            PreparedStatement pstmt = con.prepareStatement(
                "SELECT * FROM notifications WHERE notification_id = ? AND (in_use_by = '' || in_use_by = ?)");
                //"SELECT * FROM notifications WHERE notification_id = ? AND (in_use = 0 || (in_use = 1 && in_use_by = ?))");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, notify_id);
            pstmt.setString(2, user);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                nSlotParms.req_datetime = rs.getString( "req_datetime" );
                nSlotParms.course_id = rs.getInt( "course_id" );
                //nSlotParms.fb = rs.getInt( "fb" );
                //nSlotParms.in_use = rs.getInt( "in_use" );
                nSlotParms.last_user = rs.getString( "in_use_by" );
                nSlotParms.hideNotes = rs.getInt( "hideNotes" );
                nSlotParms.notes = rs.getString( "notes" );
                nSlotParms.converted = rs.getInt( "converted" );

            }
            pstmt.close();

            pstmt = con.prepareStatement (
                "SELECT * " +
                "FROM notifications_players " +
                "WHERE notification_id = ? " +
                "ORDER BY pos");

            pstmt.clearParameters();
            pstmt.setInt(1, notify_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                nSlotParms.oldPlayer1 = rs.getString( "player_name" );
                nSlotParms.oldUser1 = rs.getString( "username" );
                nSlotParms.oldp1cw = rs.getString( "cw" );
                nSlotParms.oldp91 = rs.getInt( "9hole" );
                nSlotParms.players = 1;
            }

            if (rs.next()) {

                nSlotParms.oldPlayer2 = rs.getString( "player_name" );
                nSlotParms.oldUser2 = rs.getString( "username" );
                nSlotParms.oldp2cw = rs.getString( "cw" );
                nSlotParms.oldp92 = rs.getInt( "9hole" );
                nSlotParms.players = 2;
            }

            if (rs.next()) {

                nSlotParms.oldPlayer3 = rs.getString( "player_name" );
                nSlotParms.oldUser3 = rs.getString( "username" );
                nSlotParms.oldp3cw = rs.getString( "cw" );
                nSlotParms.oldp93 = rs.getInt( "9hole" );
                nSlotParms.players = 3;
            }

            if (rs.next()) {

                nSlotParms.oldPlayer4 = rs.getString( "player_name" );
                nSlotParms.oldUser4 = rs.getString( "username" );
                nSlotParms.oldp4cw = rs.getString( "cw" );
                nSlotParms.oldp94 = rs.getInt( "9hole" );
                nSlotParms.players = 4;
            }
                
            if (rs.next()) {

                nSlotParms.oldPlayer5 = rs.getString( "player_name" );
                nSlotParms.oldUser5 = rs.getString( "username" );
                nSlotParms.oldp5cw = rs.getString( "cw" );
                nSlotParms.oldp95 = rs.getInt( "9hole" );
                nSlotParms.players = 5;
            }
            
            pstmt.close();
            
            if (nSlotParms.orig_by.equals( "" )) {    // if originator field still empty

                nSlotParms.orig_by = user;             // set this user as the originator
            }
         
        }
        catch (Exception e) {

            msg = "Check if busy. ";
            dbError(out, e, msg);
            return;
        }

    } // end if notify_id != 0
    
    
    //
    //  If request is to 'Cancel This Res', then clear all fields for this slot
    //
    //  First, make sure user is already on tee slot or originated it for unaccompanied guests
    //
    if (req.getParameter("remove") != null) {

        try {

            PreparedStatement pstmt4 = con.prepareStatement (
                "DELETE FROM notifications WHERE notification_id = ?");

            pstmt4.clearParameters();        // clear the parms
            pstmt4.setInt(1, notify_id);
            pstmt4.executeUpdate();      // execute the prepared stmt

            pstmt4 = con.prepareStatement (
                "DELETE FROM notifications_players WHERE notification_id = ?");

            pstmt4.clearParameters();        // clear the parms
            pstmt4.setInt(1, notify_id);
            pstmt4.executeUpdate();      // execute the prepared stmt
            pstmt4.close();
            
        }
        catch (Exception e4) {

            msg = "Check user on notification. ";
            dbError(out, e4, msg);
            return;
        }

    } else {        //  not a 'Cancel Tee Time' request

        //
        //  Normal request -
        //
        //   Get the guest names and other parms specified for this club
        //
        try {
            
            getClub.getParms(con, parm);        // get the club parms
            x = parm.x;
            xhrs = parm.xhrs;                      // save for later tests
            nSlotParms.rnds = parm.rnds;
            nSlotParms.hrsbtwn = parm.hrsbtwn;
        }
        catch (Exception ignore) {
        }

        //
        //  Shift players up if any empty spots
        //
        verifyNSlot.shiftUp(nSlotParms);

        //
        //  Check if any player names are guest names
        //
        try {

            verifyNSlot.parseGuests(nSlotParms, con);
        } catch (Exception e) {
            out.println("<p><b>ERROR: parseGuests - " + e.toString() + "</b></p>");
        }

        //
        //  Reject if any player was a guest type that is not allowed for members
        //
        if (!nSlotParms.gplayer.equals( "" )) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            if (nSlotParms.hit3 == true) {                      // if error was name not specified
                out.println("<BR><BR>You must specify the name of your guest(s).");
                out.println("<BR><b>" + nSlotParms.gplayer + "</b> does not include a valid name (must be at least first & last names).");
                out.println("<BR><BR>To specify the name, click in the player box where the guest is specified, ");
                out.println("<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, ");
                out.println("<BR>use the space bar to enter a space and then type the guest's name.");
            } else {
                out.println("<BR><BR><b>" + nSlotParms.gplayer + "</b> specifies a Guest Type that is not allowed for member use.");
            }
            out.println("<BR><BR>If the Golf Shop had originally entered this guest, then it <b>must not</b> be changed.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        error = false;

        if (parm.unacompGuest == 0) {      // if unaccompanied guests not supported

            //
            //  Make sure at least 1 player contains a member
            //
            if (((nSlotParms.player1.equals( "" )) || (nSlotParms.player1.equalsIgnoreCase( "x" )) || (!nSlotParms.g1.equals( "" ))) &&
                ((nSlotParms.player2.equals( "" )) || (nSlotParms.player2.equalsIgnoreCase( "x" )) || (!nSlotParms.g2.equals( "" ))) &&
                ((nSlotParms.player3.equals( "" )) || (nSlotParms.player3.equalsIgnoreCase( "x" )) || (!nSlotParms.g3.equals( "" ))) &&
                ((nSlotParms.player4.equals( "" )) || (nSlotParms.player4.equalsIgnoreCase( "x" )) || (!nSlotParms.g4.equals( "" ))) &&
                ((nSlotParms.player5.equals( "" )) || (nSlotParms.player5.equalsIgnoreCase( "x" )) || (!nSlotParms.g5.equals( "" )))) {

            error = true;
            }

        } else {           // guests are ok

            //
            //  Make sure at least 1 player contains a member
            //
            if (((nSlotParms.player1.equals( "" )) || (nSlotParms.player1.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player2.equals( "" )) || (nSlotParms.player2.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player3.equals( "" )) || (nSlotParms.player3.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player4.equals( "" )) || (nSlotParms.player4.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player5.equals( "" )) || (nSlotParms.player5.equalsIgnoreCase( "x" )))) {

                error = true;
            }
        }

        if (error == true) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>At least one player field must contain a name.");
            out.println("<BR>If you want to cancel the notification, use the 'Cancel Notification' button under the player fields.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  Check the number of X's against max specified by proshop
        //
        xcount = 0;

        if (nSlotParms.player1.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player2.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player3.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player4.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player5.equalsIgnoreCase( "x" )) xcount++;

        if (xcount > x) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + x + ").");
            out.println("<BR>Please try again.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  At least 1 Player is present - Make sure a C/W was specified for all players
        //
        if (((!nSlotParms.player1.equals( "" )) && (!nSlotParms.player1.equalsIgnoreCase( "x" )) && (nSlotParms.p1cw.equals( "" ))) ||
            ((!nSlotParms.player2.equals( "" )) && (!nSlotParms.player2.equalsIgnoreCase( "x" )) && (nSlotParms.p2cw.equals( "" ))) ||
            ((!nSlotParms.player3.equals( "" )) && (!nSlotParms.player3.equalsIgnoreCase( "x" )) && (nSlotParms.p3cw.equals( "" ))) ||
            ((!nSlotParms.player4.equals( "" )) && (!nSlotParms.player4.equalsIgnoreCase( "x" )) && (nSlotParms.p4cw.equals( "" ))) ||
            ((!nSlotParms.player5.equals( "" )) && (!nSlotParms.player5.equalsIgnoreCase( "x" )) && (nSlotParms.p5cw.equals( "" )))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  Make sure there are no duplicate names
        //
        player = "";

        if ((!nSlotParms.player1.equals( "" )) && (!nSlotParms.player1.equalsIgnoreCase( "x" )) && (nSlotParms.g1.equals( "" ))) {

            if ((nSlotParms.player1.equalsIgnoreCase( nSlotParms.player2 )) || (nSlotParms.player1.equalsIgnoreCase( nSlotParms.player3 )) ||
                (nSlotParms.player1.equalsIgnoreCase( nSlotParms.player4 )) || (nSlotParms.player1.equalsIgnoreCase( nSlotParms.player5 ))) {

                player = nSlotParms.player1;
            }
        }

      if ((!nSlotParms.player2.equals( "" )) && (!nSlotParms.player2.equalsIgnoreCase( "x" )) && (nSlotParms.g2.equals( "" ))) {

         if ((nSlotParms.player2.equalsIgnoreCase( nSlotParms.player3 )) || (nSlotParms.player2.equalsIgnoreCase( nSlotParms.player4 )) ||
             (nSlotParms.player2.equalsIgnoreCase( nSlotParms.player5 ))) {

            player = nSlotParms.player2;
         }
      }

      if ((!nSlotParms.player3.equals( "" )) && (!nSlotParms.player3.equalsIgnoreCase( "x" )) && (nSlotParms.g3.equals( "" ))) {

         if ((nSlotParms.player3.equalsIgnoreCase( nSlotParms.player4 )) ||
             (nSlotParms.player3.equalsIgnoreCase( nSlotParms.player5 ))) {

            player = nSlotParms.player3;
         }
      }

      if ((!nSlotParms.player4.equals( "" )) && (!nSlotParms.player4.equalsIgnoreCase( "x" )) && (nSlotParms.g4.equals( "" ))) {

         if (nSlotParms.player4.equalsIgnoreCase( nSlotParms.player5 )) {

            player = nSlotParms.player4;
         }
      }

        if (!player.equals( "" )) {          // if dup name found

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  Parse the names to separate first, last & mi
        //
        try {

            error = verifyNSlot.parseNames(nSlotParms, "mem");

        }
        catch (Exception e) {
            out.println("<p><b>ERROR w/ parseNames: " + e.toString() + "</b></p>");

            error = true;
        }

        if ( error == true ) {          // if problem

            out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Invalid Data Received</H3><BR>");
            out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
            out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + nSlotParms.player1 + "',&nbsp;&nbsp;&nbsp;'");
            out.println(nSlotParms.player2 + "',&nbsp;&nbsp;&nbsp;'" + nSlotParms.player3 + "',&nbsp;&nbsp;&nbsp;'");
            out.println(nSlotParms.player4 + "',&nbsp;&nbsp;&nbsp;'" + nSlotParms.player5 + "'");
            out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
            out.println("<BR><BR>");
            out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
            out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the notification.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }


        //
        //  Get the usernames, membership types, & hndcp's for players if matching name found
        //
        try {

            verifyNSlot.getUsers(nSlotParms, con);
        }
        catch (Exception e1) {

            msg = "Check guest names. ";
            dbError(out, e1, msg);                        // reject
            return;
        }

        //
        //  Save the members' usernames for guest association
        //
        memA[0] = nSlotParms.user1;
        memA[1] = nSlotParms.user2;
        memA[2] = nSlotParms.user3;
        memA[3] = nSlotParms.user4;
        memA[4] = nSlotParms.user5;

        //
        //  Check if any of the names are invalid.  
        //
        int invalNum = 0;
        p1 = "";
        
        if (nSlotParms.inval1 != 0) {

            p1 = nSlotParms.player1;                        // reject
            invalNum = nSlotParms.inval1;
        }
        if (nSlotParms.inval2 != 0) {

            p1 = nSlotParms.player2;                        // reject
            invalNum = nSlotParms.inval2;
        }
        if (nSlotParms.inval3 != 0) {

            p1 = nSlotParms.player3;                        // reject
            invalNum = nSlotParms.inval3;
        }
        if (nSlotParms.inval4 != 0) {

            p1 = nSlotParms.player4;                        // reject
            invalNum = nSlotParms.inval4;
        }
        if (nSlotParms.inval5 != 0) {

            p1 = nSlotParms.player5;                        // reject
            invalNum = nSlotParms.inval5;
        }

      if (!p1.equals( "" )) {          // if rejected
        
         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
           
         if (invalNum == 2) {        // if incomplete member record
  
            out.println("<BR><H3>Incomplete Member Record</H3><BR>");
            out.println("<BR><BR>Sorry, a member you entered has an imcomplete member record and cannot be included at this time.<BR>");
            out.println("<BR>Member Name:&nbsp;&nbsp;&nbsp;'" + p1 + "'");
            out.println("<BR><BR>Please inform your golf professional of this error.");
            out.println("<BR><BR>You will have to remove this name from your notification.");
            out.println("<BR><BR>");

         } else {
           
            out.println("<BR><H3>Invalid Member Name Received</H3><BR>");
            out.println("<BR><BR>Sorry, a name you entered is not recognized as a valid member.<BR>");
            out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "'");
            out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
            out.println("<BR><BR>");
            out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
            out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the notification.");
            out.println("<BR><BR>");
         }

         returnToSlot(out, nSlotParms);
         return;
      }


      //
      //  If any X's requested, make sure its not too late to request an X
      //
      //    from above - x = max x's allowed, xcount = # of x's requested, xhrs = # hrs in advance to remove x's
      //
      if (xcount > 0) {       // if any x's requested in tee time

         if (xhrs != 0) {     // if club wants to remove X's

            //
            //  Set date/time values to be used to check for X's in tee sheet
            //
            //  Get today's date and then go up by 'xhrs' hours
            //
            Calendar cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

            calYear = cal.get(Calendar.YEAR);
            calMonth = cal.get(Calendar.MONTH);
            calDay = cal.get(Calendar.DAY_OF_MONTH);
            calHr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            calMin = cal.get(Calendar.MINUTE);

            calMonth = calMonth + 1;                            // month starts at zero

            adv_date = calYear * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (calMonth * 100);
            adv_date = adv_date + calDay;                    // date = yyyymmdd (for comparisons)

            adv_time = calHr * 100;                          // create time field of hhmm
            adv_time = adv_time + calMin;

            //
            //  Compare the tee time's date/time to the X deadline
            //
            if ((date < adv_date) || ((date == adv_date) && (time <= adv_time))) {

               out.println(SystemUtils.HeadTitle("Invalid Use of X - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Invalid use of the X option.</H3><BR>");
               out.println("<BR><BR>Sorry, 'X' is not allowed for this notification.<BR>");
               out.println("It is not far enough in advance to reserve a player position with an X.");
               out.println("<BR><BR>");

               returnToSlot(out, nSlotParms);
               return;
            }
         }
      }        // end of IF xcount

      //
      //************************************************************************
      //  Check any membership types for max rounds per week, month or year
      //************************************************************************
      //
      if (!nSlotParms.mship1.equals( "" ) ||
          !nSlotParms.mship2.equals( "" ) ||
          !nSlotParms.mship3.equals( "" ) ||
          !nSlotParms.mship4.equals( "" ) ||
          !nSlotParms.mship5.equals( "" )) {                // if at least one name exists then check number of rounds

         error = false;                             // init error indicator

         try {

            error = verifyNSlot.checkMaxRounds(nSlotParms, con);
         }
         catch (Exception e) {
            out.println("<p><b>ERROR: checkMaxRounds - " + e.toString() + "</b></p>");
         }

         if (error == true) {      // a member exceed the max allowed tee times per week, month or year

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
            out.println("<BR><BR>Sorry, " + nSlotParms.player + " is a " + nSlotParms.mship + " member and has exceeded the<BR>");
            out.println("maximum number of times allowed for this " + nSlotParms.period + ".");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
         }

      }  // end of mship if

      //
      // **************************************
      //  Check for max # of guests exceeded (per member)
      // **************************************
      //
      if (nSlotParms.guests != 0) {      // if any guests were included

         error = false;                             // init error indicator

         //
         //  if 1 guest and 3 members, then always ok (do not check restrictions)
         //
         if (nSlotParms.guests != 1 || nSlotParms.members < 3) {

            try {

               error = verifyNSlot.checkMaxGuests(nSlotParms, con);
            }
            catch (Exception e5) {

               msg = "Check Memberships and Guest Numbers. ";
               dbError(out, e5, msg);
               return;
            }

            if (error == true) {      // a member exceed the max allowed tee times per month

               out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
               out.println("time you are requesting is " + nSlotParms.grest_num + " per " +nSlotParms.grest_per+ ".");
               out.println("<BR><BR>Guest Restriction = " + nSlotParms.rest_name);
               out.println("<BR><BR>");

               returnToSlot(out, nSlotParms);
               return;
            }

         }

      }      // end of if guests

      //
      // *******************************************************************************
      //  Check member restrictions
      //
      //     First, find all restrictions within date & time constraints
      //     Then, find the ones for this day
      //     Then, find any for this member type or membership type (all players)
      //
      // *******************************************************************************
      //
      error = false;                             // init error indicator

      try {

         error = verifyNSlot.checkMemRests(nSlotParms, con);
      }
      catch (Exception e7) {

         msg = "Check Member Restrictions. ";

         dbError(out, e7, msg);
         return;
      }                             // end of member restriction tests

      if (error == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted</H3><BR>");
         out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is restricted from playing during this time.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + nSlotParms.rest_name + "</b><br><br>");
         out.println("Please remove this player or try a different time.<br>");
         out.println("Contact the Golf Shop if you have any questions.<br>");
         out.println("<BR><BR>");

         returnToSlot(out, nSlotParms);
         return;
      }



      //
      // *******************************************************************************
      //  Check Member Number restrictions
      //
      //     First, find all restrictions within date & time constraints
      //     Then, find the ones for this day
      //     Then, check all players' member numbers against all others in the time period
      //
      // *******************************************************************************
      //
      error = false;                             // init error indicator

      try {

         error = verifyNSlot.checkMemNum(nSlotParms, con);
      }
      catch (Exception e7) {

         msg = "Check Member Number Restrictions. ";
         dbError(out, e7, msg);
         return;
      }                             // end of member restriction tests

      if (error == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
         out.println("<BR>Sorry, ");
            if (!nSlotParms.pnum1.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum1 + "</b> ");
            }
            if (!nSlotParms.pnum2.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum2 + "</b> ");
            }
            if (!nSlotParms.pnum3.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum3 + "</b> ");
            }
            if (!nSlotParms.pnum4.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum4 + "</b> ");
            }
            if (!nSlotParms.pnum5.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum5 + "</b> ");
            }
         out.println("is/are restricted from playing during this time because the");
         out.println("<BR> number of members with the same member number has exceeded the maximum allowed.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + nSlotParms.rest_name + "</b><br><br>");
         out.println("Please remove this player(s) or try a different time.<br>");
         out.println("Contact the Golf Shop if you have any questions.<br>");
         out.println("<BR><BR>");

         returnToSlot(out, nSlotParms);
         return;
      }

      //
      //***********************************************************************************************
      //
      //    Now check if any of the players are already scheduled today (only 1 res per day)
      //
      //***********************************************************************************************
      //
      nSlotParms.hit = false;                             // init error indicator
      nSlotParms.hit2 = false;                             // init error indicator
      String tmsg = "";
      int thr = 0;
      int tmin = 0;

      try {

         verifyNSlot.checkSched(nSlotParms, con);

      }
      catch (Exception e21) {

         msg = "Check Members Already Scheduled. ";

         dbError(out, e21, msg);
         return;
      }

      if (nSlotParms.hit == true || nSlotParms.hit2 == true || nSlotParms.hit3 == true) { // if we hit on a duplicate res

         if (nSlotParms.time2 != 0) {                                  // if other time was returned
           
            thr = nSlotParms.time2 / 100;                      // set time string for message
            tmin = nSlotParms.time2 - (thr * 100);
            if (thr == 12) {
               if (tmin < 10) {
                  tmsg = thr+ ":0" +tmin+ " PM";
               } else {
                  tmsg = thr+ ":" +tmin+ " PM";
               }
            } else {
               if (thr > 12) {
                  thr = thr - 12;
                  if (tmin < 10) {
                     tmsg = thr+ ":0" +tmin+ " PM";
                  } else {
                     if (tmin < 10) {
                        tmsg = thr+ ":0" +tmin+ " PM";
                     } else {
                        tmsg = thr+ ":" +tmin+ " PM";
                     }
                  }
               } else {
                  if (tmin < 10) {
                     tmsg = thr+ ":0" +tmin+ " AM";
                  } else {
                     tmsg = thr+ ":" +tmin+ " AM";
                  }
               }
            }
            if (!nSlotParms.course2.equals( "" )) {        // if course provided
              
               tmsg = tmsg + " on the " +nSlotParms.course2+ " course";
            }
         }
         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">"); 
         out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
         if (nSlotParms.rnds > 1) {       // if multiple rounds per day supported
            if (nSlotParms.hit3 == true) {       // if rounds too close together
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is scheduled to play another round within " +nSlotParms.hrsbtwn+ " hours.<br><br>");
               out.println(nSlotParms.player + " is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            } else {
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
               out.println("A player can only be scheduled " +nSlotParms.rnds+ " times per day.<br><br>");
            }
         } else {
            if (nSlotParms.hit2 == true) {
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is part of a lottery request for this date.<br><br>");
            } else {
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            }
            out.println("A player can only be scheduled once per day.<br><br>");
         }
         out.println("Please remove this player or try a different date.<br>");
         out.println("Contact the Golf Shop if you have any questions.");
         out.println("<BR><BR>");
         out.println("If you are already scheduled for this date and would like to remove yourself<br>");
         out.println("from that notification, use the 'Go Back' button to return to the tee sheet and <br>");
         out.println("locate the time stated above, or click on the 'My Notifications' tab.");
         out.println("<BR><BR>");

         returnToSlot(out, nSlotParms);
         return;
      }

      //
      //***********************************************************************************************
      //
      //    Now check all players for 'days in advance' - based on membership types
      //
      //***********************************************************************************************
      //
      if (!nSlotParms.mship1.equals( "" ) || !nSlotParms.mship2.equals( "" ) || !nSlotParms.mship3.equals( "" ) ||
          !nSlotParms.mship4.equals( "" ) || !nSlotParms.mship5.equals( "" )) {

           try {

              //error = verifyNSlot.checkDaysAdv(nSlotParms, con);
           }
           catch (Exception e21) {

              msg = "Check Days in Advance Error. ";
              dbError(out, e21, msg);
              return;
           }

           if (error == true) {          // if we hit on a violation

              out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
              out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
              out.println("<hr width=\"40%\">");
              out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
              out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is not allowed to be part of a notification this far in advance.<br><br>");
              if (x > 0) {
                 out.println("You can use an 'X' to reserve this position until the player is allowed.<br><br>");
              } else {
                 out.println("Contact the golf shop if you wish to add this person at this time.<br><br>");
              }
              out.println("<BR><BR>");

              returnToSlot(out, nSlotParms);
              return;
           }
           
      }

      //
      //  Winged Foot - Notification Submission Quota - Check to ensure that this Winged Foot member doesn't have 2 or more notifications already
      //  submitted (only check those originated by them!) and awaiting approval/processing
      //
      if (club.equals("wingedfoot")) {

          error = verifyNCustom.checkWFNotifications(user, notify_id, con);

          if (error == true) {

              out.println(SystemUtils.HeadTitle("Notification Submission Quota Reached - Reject"));
              out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
              out.println("<hr width=\"40%\">");
              out.println("<BR><H3>Notification Submission Quota Reached</H3>");
              out.println("<BR>Your club only permits you to have up to 2 notifications on the books at any time. You are submitting a notification that would exceed this limit.<BR>" +
                      "Please contact David Zona 'dzona@wfgc.org' if you have any questions.<BR>");
              out.println("<BR><BR>");

              returnToSlot(out, nSlotParms);
              return;
          }
      }

      //
      //  Winged Foot - tee time quotas - check Legacy memberships
      //
      if (club.equals( "wingedfoot" )) {

         error = verifyNCustom.checkWFlegacy(nSlotParms, con);

         if (error == true) {

            out.println(SystemUtils.HeadTitle("Legacy Member Quota Reached - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Rounds Quota Reached</H3><BR>");
            out.println("<BR><BR>Sorry, " +nSlotParms.player+ " has already played or scheduled 2 weekend rounds this month.<BR>");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
         }
      }


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
         if (nSlotParms.guests > 0) {

            //
            //  If no members requested and Unaccompanied Guests are ok at this club
            //
            if (nSlotParms.members == 0 && parm.unacompGuest == 1) {  

               if (!nSlotParms.g1.equals( "" )) {  // if player is a guest

                  nSlotParms.userg1 = user;        // set username for guests 
               }
               if (!nSlotParms.g2.equals( "" )) {  

                  nSlotParms.userg2 = user;      
               }
               if (!nSlotParms.g3.equals( "" )) {

                  nSlotParms.userg3 = user;
               }
               if (!nSlotParms.g4.equals( "" )) {

                  nSlotParms.userg4 = user;
               }
               if (!nSlotParms.g5.equals( "" )) {

                  nSlotParms.userg5 = user;
               }
               
            } else {

               if (nSlotParms.members > 0) {     // if at least one member

                  //
                  //  Both guests and members specified (member verified above) - determine guest owners by order
                  //
                  gi = 0;
                  memberName = "";

                  while (gi < 5) {                  // cycle thru arrays and find guests/members

                     if (!nSlotParms.gstA[gi].equals( "" )) {

                        usergA[gi] = memberName;       // get last players username
                     } else {
                        usergA[gi] = "";               // init array entry
                     }
                     if (!memA[gi].equals( "" )) {

                        memberName = memA[gi];        // get players username
                     }
                     gi++;
                  }
                  nSlotParms.userg1 = usergA[0];        // set usernames for guests in teecurr
                  nSlotParms.userg2 = usergA[1];
                  nSlotParms.userg3 = usergA[2];
                  nSlotParms.userg4 = usergA[3];
                  nSlotParms.userg5 = usergA[4];
               }

               if (nSlotParms.members > 1 || !nSlotParms.g1.equals( "" )) {  // if multiple members OR slot 1 is a guest

                  //
                  //  At least one guest and 2 members have been specified, or P1 is a guest.
                  //  Prompt user to verify the order.
                  //
                  //  Only require positioning if a POS system was specified for this club (saved in Login)
                  //
                    if (new_skin) {

                        // Pull the arrays into local variable, incase we want to use them later
                        String[] player_a = nSlotParms.getPlayerArray(5);
                        String[] pcw_a = nSlotParms.getCwArray(5);
                        int[] p9_a = nSlotParms.getP9Array(5);
                        String[] userg_a = nSlotParms.getUsergArray(5);
                        //int[] guest_id_a = nSlotParms.getIntArrayByName("guest_id%");

                        // Fill that field map with values that will be used when calling back
                        hidden_field_map.put("skip8", "yes");
                        hidden_field_map.put("date", date);
                        hidden_field_map.put("time", time);
                        hidden_field_map.put("mm", mm);
                        hidden_field_map.put("yy", yy);
                        hidden_field_map.put("index", index);
                        hidden_field_map.put("p5", nSlotParms.p5);
                        hidden_field_map.put("course", nSlotParms.course);
                        //hidden_field_map.put("returnCourse", nSlotParms.returnCourse);
                        hidden_field_map.put("day", nSlotParms.day);
                        hidden_field_map.put("fb", fb);
                        hidden_field_map.put("notes", nSlotParms.notes);
                        hidden_field_map.put("hide", nSlotParms.hides);
                        hidden_field_map.put("notifyId", nSlotParms.notify_id);
                        hidden_field_map.put("jump", jump);
                        //hidden_field_map.put("displayOpt", displayOpt);
                        hidden_field_map.put("player%", player_a);
                        hidden_field_map.put("p%cw", pcw_a);
                        hidden_field_map.put("p9%", p9_a);
                        hidden_field_map.put("userg%", userg_a);
                        //hidden_field_map.put("guest_id%", guest_id_a);
                        hidden_field_map.put("submitForm", "YES - continue");

                        // Build the player list
                        String player_list_html = "<ul class=\"indented_list\">";
                        for (int i2 = 0; i2 < player_a.length; i2++) {
                            if (!player_a[i2].equals("")) {
                                player_list_html += "<li class=\"" + ((!userg_a[i2].equals("")) ? "guest_item" : "player_item") + "\">" + player_a[i2] + "</li>";
                            }
                        }
                        player_list_html += "</ul>";

                        // Fill the result map
                        result_map.put("title", "Player/Guest Association Prompt");
                        result_map.put("prompt_yes_no", true);
                        result_map.put("successful", false);
                        result_map.put("callback_map", hidden_field_map);
                        if (!nSlotParms.g1.equals("") && !nSlotParms.oldPlayer1.equals(nSlotParms.player1)) {
                            result_map.put("message_array", new String[]{
                                        "Guests must be specified <b>immediately after</b> the member they belong to.",
                                        "The first player position cannot contain a guest.",
                                        "Please correct the order of players.",
                                        "<b>This is what you requested:</b>",
                                        player_list_html,
                                        "Would you like to process the request as is?"});
                        } else {
                            result_map.put("message_array", new String[]{
                                        "Guests should be specified <b>immediately after</b> the member they belong to.",
                                        "<b>Please verify the following order:</b>",
                                        player_list_html,
                                        "Would you like to process the request as is?"});

                        }

                        // Send results as json string
                        out.print(gson_obj.toJson(result_map));

                        out.close();
                        return;

                    } else { // old skin 


                        out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
                        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

                        //
                        //  if player1 is a guest & POS & not already assigned
                        //
                        //     if (!nSlotParms.g1.equals( "" ) && !posType.equals( "" ) && !nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {
                        if (!nSlotParms.g1.equals("") && !nSlotParms.oldPlayer1.equals(nSlotParms.player1)) {

                            out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                            out.println("The first player position cannot contain a guest.  Please correct the order<br>");
                            out.println("of players.  This is what you requested:");

                        } else {

                            out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                            out.println("Please verify that the following order is correct:");
                        }
                        out.println("<BR><BR>");
                        out.println(nSlotParms.player1 + " <BR>");
                        out.println(nSlotParms.player2 + " <BR>");
                        if (!nSlotParms.player3.equals("")) {
                            out.println(nSlotParms.player3 + " <BR>");
                        }
                        if (!nSlotParms.player4.equals("")) {
                            out.println(nSlotParms.player4 + " <BR>");
                        }
                        if (!nSlotParms.player5.equals("")) {
                            out.println(nSlotParms.player5 + " <BR>");
                        }

                        //     if (nSlotParms.g1.equals( "" ) || posType.equals( "" ) || nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {
                        if (nSlotParms.g1.equals("") || nSlotParms.oldPlayer1.equals(nSlotParms.player1)) {

                            out.println("<BR>Would you like to process the request as is?");
                        }

                        //
                        //  Return to _slot to change the player order
                        //
                        out.println("<font size=\"2\">");
                        out.println("<form action=\"MemberTLT_slot\" method=\"post\" target=\"_top\">");
                        out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + nSlotParms.notify_id + "\">");
                        out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                        out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                        out.println("<input type=\"hidden\" name=\"day\" value=\"" + nSlotParms.day + "\">");
                        out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
                        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                        out.println("<input type=\"hidden\" name=\"p5\" value=\"" + nSlotParms.p5 + "\">");
                        out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                        out.println("<input type=\"hidden\" name=\"player1\" value=\"" + nSlotParms.player1 + "\">");
                        out.println("<input type=\"hidden\" name=\"player2\" value=\"" + nSlotParms.player2 + "\">");
                        out.println("<input type=\"hidden\" name=\"player3\" value=\"" + nSlotParms.player3 + "\">");
                        out.println("<input type=\"hidden\" name=\"player4\" value=\"" + nSlotParms.player4 + "\">");
                        out.println("<input type=\"hidden\" name=\"player5\" value=\"" + nSlotParms.player5 + "\">");
                        out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + nSlotParms.p1cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + nSlotParms.p2cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + nSlotParms.p3cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + nSlotParms.p4cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + nSlotParms.p5cw + "\">");
                        out.println("<input type=\"hidden\" name=\"p91\" value=\"" + nSlotParms.p91 + "\">");
                        out.println("<input type=\"hidden\" name=\"p92\" value=\"" + nSlotParms.p92 + "\">");
                        out.println("<input type=\"hidden\" name=\"p93\" value=\"" + nSlotParms.p93 + "\">");
                        out.println("<input type=\"hidden\" name=\"p94\" value=\"" + nSlotParms.p94 + "\">");
                        out.println("<input type=\"hidden\" name=\"p95\" value=\"" + nSlotParms.p95 + "\">");
                        out.println("<input type=\"hidden\" name=\"notes\" value=\"" + nSlotParms.notes + "\">");
                        out.println("<input type=\"hidden\" name=\"hide\" value=\"" + nSlotParms.hides + "\">");

                        //     if (nSlotParms.g1.equals( "" ) || posType.equals( "" ) || nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {
                        if (nSlotParms.g1.equals("") || nSlotParms.oldPlayer1.equals(nSlotParms.player1)) {

                            out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");

                        } else {
                            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                        }
                        out.println("</form></font>");

                        //      if (nSlotParms.g1.equals( "" ) || posType.equals( "" ) || nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {
                        if (nSlotParms.g1.equals("") || nSlotParms.oldPlayer1.equals(nSlotParms.player1)) {

                            //
                            //  Return to process the players as they are
                            //
                            out.println("<font size=\"2\">");
                            out.println("<form action=\"MemberTLT_slot\" method=\"post\" target=\"_top\">");
                            out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + notify_id + "\">");
                            out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
                            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + nSlotParms.player1 + "\">");
                            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + nSlotParms.player2 + "\">");
                            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + nSlotParms.player3 + "\">");
                            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + nSlotParms.player4 + "\">");
                            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + nSlotParms.player5 + "\">");
                            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + nSlotParms.p1cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + nSlotParms.p2cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + nSlotParms.p3cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + nSlotParms.p4cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + nSlotParms.p5cw + "\">");
                            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + nSlotParms.p91 + "\">");
                            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + nSlotParms.p92 + "\">");
                            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + nSlotParms.p93 + "\">");
                            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + nSlotParms.p94 + "\">");
                            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + nSlotParms.p95 + "\">");
                            out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                            out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                            out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
                            out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
                            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + nSlotParms.p5 + "\">");
                            out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
                            out.println("<input type=\"hidden\" name=\"day\" value=\"" + nSlotParms.day + "\">");
                            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + nSlotParms.notes + "\">");
                            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + nSlotParms.hides + "\">");
                            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                            out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + nSlotParms.userg1 + "\">");
                            out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + nSlotParms.userg2 + "\">");
                            out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + nSlotParms.userg3 + "\">");
                            out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + nSlotParms.userg4 + "\">");
                            out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + nSlotParms.userg5 + "\">");
                            out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
                            out.println("</form></font>");
                        }
                        out.println("</CENTER></BODY></HTML>");
                        out.close();
                        return;

                    }   // end of IF more than 1 member or guest in spot #1
                }
            }      // end of IF no members and unaccompanied guests are ok
            
         }         // end of IF any guests specified

      } else {   // skip 8 requested
          
         //
         //  User has responded to the guest association prompt - process tee time request in specified order
         //
         nSlotParms.userg1 = req.getParameter("userg1");
         nSlotParms.userg2 = req.getParameter("userg2");
         nSlotParms.userg3 = req.getParameter("userg3");
         nSlotParms.userg4 = req.getParameter("userg4");
         nSlotParms.userg5 = req.getParameter("userg5");
      }         // end of IF skip8


      //
      //***********************************************************************************************
      //
      //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
      //
      //***********************************************************************************************
      //
      if (!nSlotParms.userg1.equals( "" ) || !nSlotParms.userg2.equals( "" ) || !nSlotParms.userg3.equals( "" ) ||
          !nSlotParms.userg4.equals( "" ) || !nSlotParms.userg5.equals( "" )) {

         try {

            error = verifyNSlot.checkGuestQuota(nSlotParms, con);

         }
         catch (Exception e22) {

            msg = "Check Guest Quotas. ";

            dbError(out, e22, msg);
            return;
         }

         if (error == true) {          // if we hit on a violation

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
            out.println("<BR>Sorry, requesting <b>" + nSlotParms.player + "</b> exceeds the guest quota established by the Golf Shop.");
            out.println("<br><br>You will have to remove the guest in order to complete this request.");
            out.println("<br><br>Contact the Golf Shop if you have any questions.<br>");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
         }

         //
         //  Winged Foot - guest quotas
         //
         if (club.equals( "wingedfoot" )) {

            //
            //  West course 7:30 or later only - guest quota - no more than 9 guests allowed between x:30 and x:29 (each hour of the day).
            //
            if (time > 729 && nSlotParms.course.equals("West")) {    // quota starts at 7:30 AM

               error = verifyNCustom.checkWFguestsHour(nSlotParms, con);

               if (error == true) {

                  out.println(SystemUtils.HeadTitle("Hourly Guest Limit Reached - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Guest Quota Exceeded</H3><BR>");
                  out.println("<BR><BR>Sorry,  your request will exceed the maximum number of guests (9) for this hour.<BR>");
                  out.println("<BR>Please select a different hour of the day or contact David Zona for assistance.");
                  out.println("<BR><BR>");

                  returnToSlot(out, nSlotParms);
                  return;
               }       
            }   // end of IF Winged Foot
            
            
            //
            //  Check for total guests per family 
            //
            error = verifyNCustom.checkWFguests(nSlotParms, con);

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Guest Quota Exceeded</H3><BR>");
               out.println("<BR><BR>Sorry, " +nSlotParms.player+ " has already reached the maximum limit of guests.<BR>");
               out.println("<BR>Each membership is allowed a specified number of guests per season and per year.");
               out.println("<BR><BR>");

               returnToSlot(out, nSlotParms);
               return;
            }

            //
            //  Check for Legacy Preferred Associates mship types and guests
            //
            error = verifyNCustom.checkWFLguests(nSlotParms, con);

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Guests Restricted </H3><BR>");
               out.println("<BR><BR>Sorry, " +nSlotParms.player+ " is not allowed to have guests at this time.<BR>");
               out.println("<BR>Guests are only allowed before 11:30 AM and after 2:00 PM on Tues, Wed & Thurs.");
               out.println("<BR><BR>");

               returnToSlot(out, nSlotParms);
               return;
            }
         }

      }   // end of IF userg


      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      sendemail = 0;         // init email flags
      emailNew = 0;
      emailMod = 0;
      
      // set to show values to 2 if feature is supported and teetime is today
      GregorianCalendar cal_pci = new GregorianCalendar();    
      short tmp_pci = (
        parm.precheckin == 1 &&
        mm == (cal_pci.get(cal_pci.MONTH) + 1) &&
        dd == cal_pci.get(cal_pci.DAY_OF_MONTH) &&
        yy == cal_pci.get(cal_pci.YEAR)
       ) ? (short)2 : (short)0;
         
      //
      //  If players changed, then init the no-show flag and send emails, else use the old no-show value
      //
      if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 )) {

         nSlotParms.show1 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player2.equals( nSlotParms.oldPlayer2 )) {

         nSlotParms.show2 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player3.equals( nSlotParms.oldPlayer3 )) {

         nSlotParms.show3 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player4.equals( nSlotParms.oldPlayer4 )) {

         nSlotParms.show4 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {

         nSlotParms.show5 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      //
      //   Set email type based on new or update request (cancel set above)
      //   Also, bump stats counters for reports
      //
      if ((!nSlotParms.oldPlayer1.equals( "" )) || (!nSlotParms.oldPlayer2.equals( "" )) || (!nSlotParms.oldPlayer3.equals( "" )) ||
          (!nSlotParms.oldPlayer4.equals( "" )) || (!nSlotParms.oldPlayer5.equals( "" ))) {

         emailMod = 1;  // tee time was modified
         memMod++;      // increment number of mods

      } else {

         emailNew = 1;  // tee time is new
         memNew++;      // increment number of new tee times
      }

   }  // end of 'cancel this res' if - cancel will contain empty player fields


    int course_id = SystemUtils.getClubParmIdFromCourseName(nSlotParms.course, con);

    //
    //  Verification complete -
    //  Add or Update the notification entry in the notifications table
    //
    
    if (notify_id == 0) {

        out.println("<!-- ATTEMPTING INSERT [" + nSlotParms.req_datetime + "] -->");
        // add new notification
        try {

            // Add the notification
            PreparedStatement pstmt6 = con.prepareStatement (
                "INSERT INTO notifications " +
                "(req_datetime, course_id, notes, created_by, created_datetime) VALUES (?, ?, ?, ?, now())");
            pstmt6.clearParameters();
            pstmt6.setString(1, nSlotParms.req_datetime);
            pstmt6.setInt(2, course_id);
            pstmt6.setString(3, nSlotParms.notes);
            pstmt6.setString(4, user);
            pstmt6.executeUpdate();

            int notification_id = 0;
            pstmt6 = con.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet rsLastID = pstmt6.executeQuery();
            while (rsLastID.next()) {
                notification_id = rsLastID.getInt(1);
            }

            pstmt6.close();

            // add the players of this notifications
            if (!nSlotParms.player1.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 1)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user1);
                pstmt6.setString(3, nSlotParms.p1cw);
                pstmt6.setString(4, nSlotParms.player1);
                pstmt6.setInt(5, nSlotParms.p91);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player2.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 2)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user2);
                pstmt6.setString(3, nSlotParms.p2cw);
                pstmt6.setString(4, nSlotParms.player2);
                pstmt6.setInt(5, nSlotParms.p92);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player3.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 3)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user3);
                pstmt6.setString(3, nSlotParms.p3cw);
                pstmt6.setString(4, nSlotParms.player3);
                pstmt6.setInt(5, nSlotParms.p93);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player4.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 4)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user4);
                pstmt6.setString(3, nSlotParms.p4cw);
                pstmt6.setString(4, nSlotParms.player4);
                pstmt6.setInt(5, nSlotParms.p94);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player5.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 5)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user5);
                pstmt6.setString(3, nSlotParms.p5cw);
                pstmt6.setString(4, nSlotParms.player5);
                pstmt6.setInt(5, nSlotParms.p95);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
            
       }
       catch (Exception e6) {

          msg = "Add Notification. ";
          dbError(out, e6, msg);
          return;
       }
        
        
    } else {
        
        out.println("<!-- ATTEMPTING UPDATES [" + notify_id + "] -->");
        // update existing notification
        try {

            PreparedStatement pstmt6 = con.prepareStatement (
                "UPDATE notifications " +
                "SET " +
                    //"req_datetime = ?, " +
                    //"course_id = ?, " +
                    //"fb = ?, " +
                    "notes = ? " +
                "WHERE notification_id = ?");
            
            pstmt6.clearParameters();
            //pstmt6.setString(1, nSlotParms.req_datetime);
            //pstmt6.setInt(1, course_id);
            //pstmt6.setInt(2, nSlotParms.fb);
            pstmt6.setString(1, nSlotParms.notes);
            pstmt6.setInt(2, notify_id);
            pstmt6.executeUpdate();
            
            
            // add/update players
            updateNotificationPlayers(notify_id, nSlotParms.player1, nSlotParms.oldPlayer1, nSlotParms.user1, nSlotParms.p1cw, nSlotParms.oldp1cw, nSlotParms.p91, nSlotParms.oldp91, 1, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player2, nSlotParms.oldPlayer2, nSlotParms.user2, nSlotParms.p2cw, nSlotParms.oldp2cw, nSlotParms.p92, nSlotParms.oldp92, 2, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player3, nSlotParms.oldPlayer3, nSlotParms.user3, nSlotParms.p3cw, nSlotParms.oldp3cw, nSlotParms.p93, nSlotParms.oldp93, 3, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player4, nSlotParms.oldPlayer4, nSlotParms.user4, nSlotParms.p4cw, nSlotParms.oldp4cw, nSlotParms.p94, nSlotParms.oldp94, 4, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player5, nSlotParms.oldPlayer5, nSlotParms.user5, nSlotParms.p5cw, nSlotParms.oldp5cw, nSlotParms.p95, nSlotParms.oldp95, 5, con, out);

            // clear in_use fields
            pstmt6 = con.prepareStatement (
                "UPDATE notifications SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE notification_id = ?");

            pstmt6.clearParameters();
            pstmt6.setInt(1, notify_id);
            pstmt6.executeUpdate();
            pstmt6.close();
            
/*
            if (!nSlotParms.player1.equals("")) {
                
                out.println("<!-- UPDATING player1 [" + nSlotParms.user1 + " | " + nSlotParms.player1 + "] -->");

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                        "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 1) " + 
                    "ON DUPLICATE KEY UPDATE " +
                        "notification_id = VALUES(notification_id), " +
                        "username = VALUES(username), " +
                        "cw = VALUES(cw), " +
                        "player_name = VALUES(player_name), " +
                        "9hole = VALUES(9hole)");
                
                pstmt6.clearParameters();
                pstmt6.setInt(1, notify_id);
                pstmt6.setString(2, nSlotParms.user1);
                pstmt6.setString(3, nSlotParms.p1cw);
                pstmt6.setString(4, nSlotParms.player1);
                pstmt6.setInt(5, nSlotParms.p91);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player2.equals("")) {
                
                out.println("<!-- UPDATING player2 [" + nSlotParms.user2 + " | " + nSlotParms.player2 +  "] -->");

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                        "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 2) " + 
                    "ON DUPLICATE KEY UPDATE " +
                        "notification_id = VALUES(notification_id), " +
                        "username = VALUES(username), " +
                        "cw = VALUES(cw), " +
                        "player_name = VALUES(player_name), " +
                        "9hole = VALUES(9hole)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notify_id);
                pstmt6.setString(2, nSlotParms.user2);
                pstmt6.setString(3, nSlotParms.p2cw);
                pstmt6.setString(4, nSlotParms.player2);
                pstmt6.setInt(5, nSlotParms.p92);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (nSlotParms.player3.equals("")) {
                
                out.println("<!-- DELETING player3 -->");
                
                pstmt6 = con.prepareStatement ("DELETE FROM notifications_players WHERE notification_id = ? AND pos = ?");
                pstmt6.setInt(1, notify_id);
                pstmt6.setInt(2, 3);
                pstmt6.executeUpdate();
                pstmt6.close();
                
            } else if (!nSlotParms.player3.equals(nSlotParms.oldPlayer3)) {
                
                out.println("<!-- UPDATING player3 [" + nSlotParms.user3 +  " | " + nSlotParms.player3 + "] -->");

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                        "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 3) " +
                    "ON DUPLICATE KEY UPDATE " +
                        "notification_id = VALUES(notification_id), " +
                        "username = VALUES(username), " +
                        "cw = VALUES(cw), " +
                        "player_name = VALUES(player_name), " +
                        "9hole = VALUES(9hole)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notify_id);
                pstmt6.setString(2, nSlotParms.user3);
                pstmt6.setString(3, nSlotParms.p3cw);
                pstmt6.setString(4, nSlotParms.player3);
                pstmt6.setInt(5, nSlotParms.p93);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
            
            if (!nSlotParms.player4.equals("")) {
                
                out.println("<!-- UPDATING player4 [" + nSlotParms.user4 + " | " + nSlotParms.player4 +  "] -->");

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                        "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 4) " + 
                    "ON DUPLICATE KEY UPDATE " +
                        "notification_id = VALUES(notification_id), " +
                        "username = VALUES(username), " +
                        "cw = VALUES(cw), " +
                        "player_name = VALUES(player_name), " +
                        "9hole = VALUES(9hole)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notify_id);
                pstmt6.setString(2, nSlotParms.user4);
                pstmt6.setString(3, nSlotParms.p4cw);
                pstmt6.setString(4, nSlotParms.player4);
                pstmt6.setInt(5, nSlotParms.p94);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
            
            if (!nSlotParms.player5.equals("")) {
                
                out.println("<!-- UPDATING player5 [" + nSlotParms.user5 + " | " + nSlotParms.player5 + "] -->");

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                        "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 5) " + 
                    "ON DUPLICATE KEY UPDATE " +
                        "notification_id = VALUES(notification_id), " +
                        "username = VALUES(username), " +
                        "cw = VALUES(cw), " +
                        "player_name = VALUES(player_name), " +
                        "9hole = VALUES(9hole)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notify_id);
                pstmt6.setString(2, nSlotParms.user5);
                pstmt6.setString(3, nSlotParms.p5cw);
                pstmt6.setString(4, nSlotParms.player5);
                pstmt6.setInt(5, nSlotParms.p95);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
*/
        }
        catch (Exception e6) {

            msg = "Update Notification.";
            dbError(out, e6, msg);
            return;
        }
            
    } // end if insert or update
    
    
    
/*
   //
   //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
   //
   if (nSlotParms.oldPlayer1.equals( "" ) && nSlotParms.oldPlayer2.equals( "" ) && nSlotParms.oldPlayer3.equals( "" ) &&
       nSlotParms.oldPlayer4.equals( "" ) && nSlotParms.oldPlayer5.equals( "" )) {

      //  new tee time
      SystemUtils.updateHist(date, nSlotParms.day, time, fb, nSlotParms.course, nSlotParms.player1, nSlotParms.player2, nSlotParms.player3,  
                             nSlotParms.player4, nSlotParms.player5, user, fullName, 0, con);
     
   } else {
     
      //  update tee time
      SystemUtils.updateHist(date, nSlotParms.day, time, fb, nSlotParms.course, nSlotParms.player1, nSlotParms.player2, nSlotParms.player3,
                             nSlotParms.player4, nSlotParms.player5, user, fullName, 1, con);
   }
*/

   //
   //  Build the HTML page to confirm notification for user
   //
   out.println(SystemUtils.HeadTitle("Member Tee Slot Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;The notification has been cancelled.</p>");
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;Your notification has been accepted and processed.</p>");

      if (xcount > 0 && xhrs > 0) {            // if any X's were specified 

         out.println("<p>&nbsp;</p>All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the notification.");
         out.println("<br>If not, the system will automatically remove the X.<br>");
      }

      if (notesL > 254) {

      out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
      }
   }

   out.println("<p>&nbsp;</p></font>");
   

   if (index.equals( "999" )) {         // if came from Member_teelist

      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
      out.println("</form></font>");

   } else {

      if (index.equals( "995" )) {         // if came from Member_teelist_list (old)

         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain2.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</form></font>");

      } else {

         if (index.equals( "888" )) {       // if from Member_searchmem

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/member_searchmem.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");

         } else {                                // return to Member_sheet - must rebuild frames first

            out.println("<font size=\"2\">");
            out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("</form></font>");
         }
      }
   }

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
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendemail != 0 || club.equals("bearcreekgc") || club.equals("roxiticus") || club.equals("calclub")) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.activity_id = 0;
      parme.club = club;
      parme.guests = nSlotParms.guests;
      parme.type = "tee";         // type = tee time
      parme.date = date;
      parme.time = time;
      parme.fb = fb;
      parme.mm = mm;
      parme.dd = dd;
      parme.yy = yy;

      parme.user = user;
      parme.emailNew = emailNew;
      parme.emailMod = emailMod;
      parme.emailCan = emailCan;

      parme.p91 = nSlotParms.p91;
      parme.p92 = nSlotParms.p92;
      parme.p93 = nSlotParms.p93;
      parme.p94 = nSlotParms.p94;
      parme.p95 = nSlotParms.p95;

      parme.course = nSlotParms.course;
      parme.day = nSlotParms.day;
      parme.notes = nSlotParms.notes;

      parme.player1 = nSlotParms.player1;
      parme.player2 = nSlotParms.player2;
      parme.player3 = nSlotParms.player3;
      parme.player4 = nSlotParms.player4;
      parme.player5 = nSlotParms.player5;

      parme.oldplayer1 = nSlotParms.oldPlayer1;
      parme.oldplayer2 = nSlotParms.oldPlayer2;
      parme.oldplayer3 = nSlotParms.oldPlayer3;
      parme.oldplayer4 = nSlotParms.oldPlayer4;
      parme.oldplayer5 = nSlotParms.oldPlayer5;

      parme.user1 = nSlotParms.user1;
      parme.user2 = nSlotParms.user2;
      parme.user3 = nSlotParms.user3;
      parme.user4 = nSlotParms.user4;
      parme.user5 = nSlotParms.user5;

      parme.olduser1 = nSlotParms.oldUser1;
      parme.olduser2 = nSlotParms.oldUser2;
      parme.olduser3 = nSlotParms.oldUser3;
      parme.olduser4 = nSlotParms.oldUser4;
      parme.olduser5 = nSlotParms.oldUser5;

      parme.pcw1 = nSlotParms.p1cw;
      parme.pcw2 = nSlotParms.p2cw;
      parme.pcw3 = nSlotParms.p3cw;
      parme.pcw4 = nSlotParms.p4cw;
      parme.pcw5 = nSlotParms.p5cw;

      parme.oldpcw1 = nSlotParms.oldp1cw;
      parme.oldpcw2 = nSlotParms.oldp2cw;
      parme.oldpcw3 = nSlotParms.oldp3cw;
      parme.oldpcw4 = nSlotParms.oldp4cw;
      parme.oldpcw5 = nSlotParms.oldp5cw;

      //
      //  Send the email
      //
      if (sendemail != 0) sendEmail.sendIt(parme, con);      // in common
      
      // Send custom notifications
      if (club.equals("bearcreekgc") || club.equals("roxiticus") || club.equals("calclub")) {
          
          sendEmail.sendOakmontEmail(parme, con, club);   // Bear Creek GC - Copy pro on notification whenever a member books/modifies/cancels a notification 
      }

   }     // end of IF sendemail
   
 }       // end of verify



 // ************************************************************************
 //  Get number of days between today and the date provided
 // ************************************************************************

 private int getDaysBetween(long date) {


   //
   //  break down date provided
   //
   long yy = date / 10000;                             // get year
   long mm = (date - (yy * 10000)) / 100;              // get month
   long dd = (date - (yy * 10000)) - (mm * 100);       // get day

   int month = (int)mm;
   int day = (int)dd;
   int year = (int)yy;

   //
   //  Check if this tee time is within 30 days of the current date (today)
   //
   BigDate today = BigDate.localToday();                 // get today's date
   BigDate thisdate = new BigDate(year, month, day);     // get requested date

   int ind = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between

   return(ind);

 }       // end of getDaysBetween


 // ************************************************************************
 //  Process cancel request (Return w/o changes) from MemberTLT_slot (HTML)
 // ************************************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, String club, Connection con) {


    int count = 0;
    int time  = 0;
    int fb  = 0;
    long date  = 0;

    //
    // Get all the parameters entered
    //
    String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    String sfb = req.getParameter("fb");               //  front/back indicator
    String index = Utilities.getParameterString(req, "index", "995");          //  index value of day (needed by Member_sheet when returning)
    String course = req.getParameter("course");        //  name of course (needed by Member_sheet when returning)
    String day = req.getParameter("day");              //  name of the day

    //
    // Get our notify uid if we are here to edit an existing notification, if absent set to zero to indicate new notification
    //
    String snid = req.getParameter("notifyId");
    if (snid == null) snid = "0";
    int notify_id = 0;

    //
    //  Convert the values from string to int
    //
    try {

        notify_id = Integer.parseInt(snid);
    }
    catch (NumberFormatException e) {
    }

    //
    //  Clear the 'in_use' flag for this time slot in notifications
    //
    if (notify_id != 0) {
        try {

            PreparedStatement pstmt1 = con.prepareStatement (
                "UPDATE notifications SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE notification_id = ?");

            pstmt1.clearParameters();
            pstmt1.setInt(1, notify_id);
            count = pstmt1.executeUpdate();
            pstmt1.close();

        }
        catch (Exception ignore) {
        }
    }
    //
    //  Prompt user to return to Member_sheet or Member_teelist (index = 999)
    //
    out.println("<HTML>");
    out.println("<HEAD>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
    out.println("<title>Member Tee Slot Page</title>");

    if (index.equals( "999" )) {       // if from Member_teelist

        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/member_teemain.htm\">");
        out.println("</HEAD>");
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
        out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
        out.println("</form></font>");

    } else {

        if (index.equals( "995" )) {       // if from Member_teelist_list (old)

            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/member_teemain2.htm\">");
            out.println("</HEAD>");
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
            out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
            out.println("<BR><BR>");

            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain2.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</form></font>");

        } else {

            if (index.equals( "888" )) {       // if from Member_searchmem

                out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/member_searchmem.htm\">");
                out.println("</HEAD>");
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
                out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
                out.println("<BR><BR>");

                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"/" +rev+ "/member_searchmem.htm\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");

            } else {

                out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_jump?index=" + index + "&course=" + course + "\">");
                out.println("</HEAD>");
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Return/Cancel Requested!</H3>");
                out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
                out.println("<BR><BR>");

                out.println("<font size=\"2\">");
                out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");
            } //end if 888
            
        } // end if 995
        
    } // end if 999
    
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }



 // ************************************************************************
 //  Process return to Member_sheet if tee time has changed
 // ************************************************************************

 private void returnToMemSheet(long date, int time, int fb, String course, String day, String club, PrintWriter out, Connection con) {


    //
    //  Clear the 'in_use' flag for this time slot in teecurr
    //
    try {

        PreparedStatement pstmt1 = con.prepareStatement (
            "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

        pstmt1.clearParameters();        // clear the parms
        pstmt1.setLong(1, date);         // put the parm in pstmt1
        pstmt1.setInt(2, time);
        pstmt1.setInt(3, fb);
        pstmt1.setString(4, course);
        pstmt1.executeUpdate();   

        pstmt1.close();

   }
   catch (Exception ignore) {
   }

   //
   //  Prompt user to return to Member_sheet 
   //
   out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><BR><BR><H1>Notification Slot Busy</H1>");
   out.println("<BR><BR>Sorry, but this time slot is currently busy.<BR>");
   out.println("<BR>Please select another time or try again later.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/member_selmain.htm\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Return to MemberTLT_slot
 // *********************************************************

 private void returnToSlot(PrintWriter out, parmNSlot nSlotParms) {

   //
   //  Return to _slot to change the player order
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"MemberTLT_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + nSlotParms.notify_id + "\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + nSlotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + nSlotParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + nSlotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + nSlotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + nSlotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + nSlotParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + nSlotParms.fb + "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + nSlotParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + nSlotParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + nSlotParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + nSlotParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + nSlotParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + nSlotParms.p1cw + "\">");
   out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + nSlotParms.p2cw + "\">");
   out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + nSlotParms.p3cw + "\">");
   out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + nSlotParms.p4cw + "\">");
   out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + nSlotParms.p5cw + "\">");
   out.println("<input type=\"hidden\" name=\"p91\" value=\"" + nSlotParms.p91 + "\">");
   out.println("<input type=\"hidden\" name=\"p92\" value=\"" + nSlotParms.p92 + "\">");
   out.println("<input type=\"hidden\" name=\"p93\" value=\"" + nSlotParms.p93 + "\">");
   out.println("<input type=\"hidden\" name=\"p94\" value=\"" + nSlotParms.p94 + "\">");
   out.println("<input type=\"hidden\" name=\"p95\" value=\"" + nSlotParms.p95 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + nSlotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + nSlotParms.hides + "\">");
   out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, String msg) {

    out.println(SystemUtils.HeadTitle("DB Error"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<BR><BR><H3>Database Access Error</H3>");
    out.println("<BR><BR>Unable to access the Database.");
    out.println("<BR>Please try again later.");
    out.println("<BR><BR>If problem persists, contact your club manager.");
    out.println("<BR><BR>Process: " + msg + "<br>  Exception: " + e1.getMessage());
    out.println("<BR><BR>");
    out.println("<a href=\"javascript:history.back(1)\">Return</a>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }

 
 // *********************************************************
 //  Update players for a notification
 // *********************************************************
 
 private void updateNotificationPlayers(int notify_id, String player_name, String old_player, String username, String cw, String oldcw, int p9hole, int oldp9hole, int pos, Connection con, PrintWriter out) {

    try {
        
        if ( player_name.equals("") && !old_player.equals("") ) {

            out.println("<!-- DELETING player"+pos+" | player_name=" + player_name + " | old_player=" + old_player + " -->");

            PreparedStatement pstmt = con.prepareStatement ("DELETE FROM notifications_players WHERE notification_id = ? AND pos = ?");
            pstmt.setInt(1, notify_id);
            pstmt.setInt(2, pos);
            pstmt.executeUpdate();
            pstmt.close();

        } else if (!player_name.equals(old_player) || !cw.equals(oldcw) || p9hole != oldp9hole) {

            out.println("<!-- UPDATING player"+pos+" [" + username + " | " + player_name + " | " + cw + " | " + p9hole + "] -->");

            PreparedStatement pstmt = con.prepareStatement (
                "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                    "notification_id = VALUES(notification_id), " +
                    "username = VALUES(username), " +
                    "cw = VALUES(cw), " +
                    "player_name = VALUES(player_name), " +
                    "9hole = VALUES(9hole)");

            pstmt.clearParameters();
            pstmt.setInt(1, notify_id);
            pstmt.setString(2, username);
            pstmt.setString(3, cw);
            pstmt.setString(4, player_name);
            pstmt.setInt(5, p9hole);
            pstmt.setInt(6, pos);
            pstmt.executeUpdate();
            pstmt.close();

        } else {

            out.println("<!-- UNCHANGED player"+pos+" [" + username +  " | " + player_name + "] -->");
        }
        
    } catch (Exception e) {
        
        dbError(out, e, "Error updating player" + pos + " info for notification " + notify_id + ".");
    }
 }
 
 
}
