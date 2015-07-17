/***************************************************************************************
 *   Member_waitlist_slot:  This servlet will display and process the Wait List 
 *                          registration request form from a member
 *
 *
 *   Called by:     Member_wait_list
 *                  self on cancel request
 *
 *
 *   Created:       4/19/2008
 *
 *   Last Updated:  
 *
 *        7/08/10   Added ability to display member notices.  Will be treated like a normal tee time for filtering.
 *        2/02/10   Trim the notes in verify
 *       12/02/09   Call to alphaTable.displayPartnerList added to print the partner list, outdated code removed
 *       10/04/09   Added activity isolation to the buddy list
 *        8/04/09   San Francisco GC (sfgc) - Set default Mode of Trans. to CAD for all guest types
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
 *        8/13/08   Changed auto-placement of current user's name in next available slot so it takes into account the current max_team_size
 *        8/01/08   Added processing for proshop only transportation modes
 *        6/11/08   Fixed ok date bug
 *
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
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.alphaTable;
import com.foretees.common.BigDate;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyWLSlot;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmWaitList;
import com.foretees.common.getWaitList;


public class Member_waitlist_slot extends HttpServlet {


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
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


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

   if (session == null) return;

   Connection con = SystemUtils.getCon(session);            // get DB connection

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
   //String userMtype = (String)session.getAttribute("mtype");    // get users mship type
   String pcw = (String)session.getAttribute("wc");             // get users walk/cart preference
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   //
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();        // allocate a parm block
   slotParms.club = club;                        // save club name

   //
   // Process request according to which 'submit' button was selected
   //
   //      'time:fb' - a request from Member_sheet ** (NOT USED)
   //      'cancel'  - a cancel request from user via Member_waitlist_slot (return with no changes)
   //      'letter'  - a request to list member names (from self)
   //      'submitForm'  - a wait list entry request (from self)
   //      'remove'  - a 'cancel wait list entry' request (from self - Cancel Notification)
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
   int signup_id = 0;
   int wait_list_id = 0;
   int in_use = 0;
   //int count = 0;
   //int hr = 0;
   //int min = 0;
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
   //String ampm = "";
   //String sfb = "";
   String notes = "";
   String hides = "";
   String msg = "";
   //String slstate = "";
   //String pname = "";

   //
   //  2-some indicator used for some custom requests
   //
   boolean twoSomeOnly = false;

   //
   //   Flag for Cancel Tee Time button (show or not show)
   //
   boolean allowCancel = true;              // default to 'allow'

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // waitlist is not supported under FlxRez so hard code the root activity_id to a zero for now

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");       //  name of the day
   String index = req.getParameter("index");        //  index value of day (needed by Member_sheet when returning)
   String p5 = ""; //req.getParameter("p5");        //  5-somes supported
   String course = req.getParameter("course");      //  Name of Course
   if (course == null) course = "";

   if (req.getParameter("sdate") != null) {         // if date was passed in sdate

      sdate = req.getParameter("sdate");
   }

   if (req.getParameter("date") != null) {          // if date was passed in date

      sdate = req.getParameter("date");
   }
 
    String start_hr = (req.getParameter("start_hr") != null) ? req.getParameter("start_hr") : "";
    String start_min = (req.getParameter("start_min") != null) ? req.getParameter("start_min") : "";
    String start_ampm = (req.getParameter("start_ampm") != null) ? req.getParameter("start_ampm") : "";
    String end_hr = (req.getParameter("end_hr") != null) ? req.getParameter("end_hr") : "";
    String end_min = (req.getParameter("end_min") != null) ? req.getParameter("end_min") : "";
    String end_ampm = (req.getParameter("end_ampm") != null) ? req.getParameter("end_ampm") : "";
    
    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();             // get todays date
    int thisYear = cal.get(Calendar.YEAR);              // get the year

    //
    // Get our wait list entry uid if we are here to edit an existing one, 
    // else if absent set to zero to indicate new signup
    //
    String sid = req.getParameter("signupId");
    if (sid == null) sid = "0";

    String wlid = req.getParameter("waitListId");
    if (wlid == null) wlid = "0";
    
    //
    //  Convert the values from string to int
    //
    try {

        wait_list_id = Integer.parseInt(wlid);
        signup_id = Integer.parseInt(sid);
        date = Long.parseLong(sdate);
    }
    catch (NumberFormatException e) {
    }

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


    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
    
    parmWL.wait_list_id = wait_list_id;
    
    try {
        
        getWaitList.getParms(con, parmWL);                      // get the wait list config
        
    } catch (Exception ignore) { }
    
    
    
    if (parmWL.member_access != 1) {
        
        out.println(SystemUtils.HeadTitle("Restriction Found"));
        out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<br><br>");
        out.println("<p><b>This wait list is not configured for member access.<br>Please contact your golf shop for assistance.</b></p>");
        out.println("<br><br>");
        out.println("<font size=\"2\" color=\"Darkred\">");
        out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"can\">");
        out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
        out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
        out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
        out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
        out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
        out.println("Return<br>w/o Changes:<br>");
        out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
        out.println("<br><br>");
        out.println("</body></html>");
        out.close();
        return;
    }
    
    int ok_stime = 0;
    int ok_etime = 0;
    int shr = 0;
    int ehr = 0;
    int smin = 0;
    int emin = 0;
    
    String sok_stime = req.getParameter("ok_stime");    //  start of ok time (hhmm)
    String sok_etime = req.getParameter("ok_etime");    //  end of ok time (hhmm)
    
    if (sok_stime == null || sok_etime == null) {
    
        try {

            shr = Integer.parseInt(start_hr);
            smin = Integer.parseInt(start_min);
            ehr = Integer.parseInt(end_hr);
            emin = Integer.parseInt(end_min);
        }
        catch (NumberFormatException e) {
        }

        if (start_ampm.equals("PM") && shr != 12) shr += 12;
        ok_stime = (shr * 100) + smin;

        if (end_ampm.equals("PM") && ehr != 12) ehr += 12;
        ok_etime = (ehr * 100) + emin;

        out.println("<!-- FROM PARTS: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        
    } else {
        
        try {

            slotParms.ok_stime = Integer.parseInt(sok_stime);
            slotParms.ok_etime = Integer.parseInt(sok_etime);
            
        }
        catch (NumberFormatException e) {
            out.println("<!-- ERROR in ok_times: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        } finally {
            
            shr = slotParms.ok_stime / 100;
            smin = slotParms.ok_stime - (shr * 100);      
            start_ampm = "AM";
            if (shr > 11) {
                start_ampm = "PM";
                if (shr > 12) shr = shr - 12;
            }

            ehr = slotParms.ok_etime / 100;
            emin = slotParms.ok_etime - (ehr * 100);      
            end_ampm = "AM";
            if (ehr > 11) {
                end_ampm = "PM";
                if (ehr > 12) ehr = ehr - 12;
            }
        
            out.println("<!-- FROM ok_time: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        }
    }
    
/*    
    if (req.getParameter("return") != null) {     // if this is a return from verify - time = hhmm

        try {
            
            time = Integer.parseInt(stime);
        }
        catch (NumberFormatException e) {
        }

        //
        //  create a time string for display
        //
        hr = time / 100;
        min = time - (hr * 100);      
        ampm = " AM";
        if (hr > 11) {
            ampm = " PM";
            if (hr > 12) hr = hr - 12;
        }
        //stime = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;

    } else {

        // if we are not here to edit an existing wait list entry
        //if (signup_id != 0) {

            out.println("<!-- ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
            //
            //  Parse the time parm to separate hh, mm, am/pm and convert to military time
            //  (received as 'hh:mm xx'   where xx = am or pm)
            //
            String shr = "";
            String smin = "";
            try {

                StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token
                shr = tok.nextToken();
                smin = tok.nextToken();
                ampm = tok.nextToken();
            }
            catch (NoSuchElementException e) {
                out.println("<p><b>ERROR: parsing date: " + stime + " - " + e.toString() + "</b></p>");
            }

            //
            //  Convert the values from string to int
            //
            try {

                hr = Integer.parseInt(shr);
                min = Integer.parseInt(smin);
            }
            catch (NumberFormatException e) {
                out.println("<p><b>ERROR: converting hr/min values - " + e.toString() + "</b></p>");
            }

            if (ampm.equalsIgnoreCase("PM") && hr != 12) hr = hr + 12;

            time = hr * 100;
            time = time + min;          // military time

            // we're doing this client side, so do not need to do here for now
            if (time < parmWL.start_time || time > parmWL.end_time) {
            
                out.println("<p>Notifications are only being accepted between hours of " + start_time + "AM and " + end_time + "PM.</p>");
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

            while (ind < parmr.MAX && allow == true) {

                if (parmr.stime[ind] <= time && parmr.etime[ind] >= time) {                // matching time ?

                   if ((parmr.courseName[ind].equals( "-ALL-" )) || (parmr.courseName[ind].equals( course ))) {  // course ?

                      //if ((parmr.fb[ind].equals( "Both" )) || (parmr.fb[ind].equals( sfb2 ))) {    // matching f/b ?

                         allow = false;                    // match found
                      //}
                   }
                }
                ind++;
            } // end of while
            
            if (!allow) {
                
                out.println(SystemUtils.HeadTitle("Restriction Found"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<center>");
                out.println("<BR><BR><H3>Restriction Found</H3>");
                out.println("<BR>A restriction was found at the same time of your requested wait list entry.");
                out.println("<BR><BR>Return to the tee sheet and view a list of restrictions in place for today.<br>" +
                            "Each restriction is displayed as a button that you can click on for specific details.");

                out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"can\">");
                out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
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
            
        //} // end if notify

    }

*/
    
    
    
    // see what we are here to do
    // if letter is here OR return is here then get form data from the request object
    if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) || req.getParameter("memNotice") != null) {

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
        if (!hides.equals( "0" )) hide = 1;
        if (notes != null) notes = notes.trim();


        // if signup is not zero then we are here to edit an existing entry
        // load up slotParms with signup details
        if (signup_id != 0) {

            try {

                verifyWLSlot.checkInUse(signup_id, user, slotParms, con, out);

            } catch (Exception e) {
                out.println("<p><b>ERROR: checkInUse - " + e.toString() + "</b></p>");
            }

        }

    } else {
      
        // we are loading this page for the fist time
        // this could be either a new request or editing an existing
       

        // if signup is not zero then we are here to edit an existing entry
        // so we need to see if it is in use and load up slotParms with the details if not
        if (signup_id != 0) {
            
            try {

                in_use = verifyWLSlot.checkInUse(signup_id, user, slotParms, con, out);
                course = slotParms.course;
                day_name = slotParms.day;
            
            } catch (Exception e) {
                out.println("<p><b>ERROR: checkInUse - " + e.toString() + "</b></p>");
            }
            
        } else {
            
            // first page load - default times to the wait list times
            if (shr == 0) {
                
                slotParms.ok_stime = parmWL.start_time;
                slotParms.ok_etime = parmWL.end_time;
                
                out.println("<!-- INITIAL PAGE LOAD - using defaults: slotParms.ok_stime=" + slotParms.ok_stime + " | slotParms.ok_etime=" + slotParms.ok_etime + " -->");
            }
            
            slotParms.course = course;                        // save course name
        }

        
        shr = slotParms.ok_stime / 100;
        smin = slotParms.ok_stime - (shr * 100);      
        start_ampm = "AM";
        if (shr > 11) {
            start_ampm = "PM";
            if (shr > 12) shr = shr - 12;
        }

        ehr = slotParms.ok_etime / 100;
        emin = slotParms.ok_etime - (ehr * 100);      
        end_ampm = "AM";
        if (ehr > 11) {
            end_ampm = "PM";
            if (ehr > 12) ehr = ehr - 12;
        }
                
        out.println("<!-- shr=" + shr + " | smin=" + smin + " | start_ampm=" + start_ampm + " -->");
        out.println("<!-- ehr=" + ehr + " | emin=" + emin + " | end_ampm=" + end_ampm + " -->");
        
        out.println("<!-- wait_list_id=" + wait_list_id + " | signup_id=" + signup_id + " | slotParms.players=" + slotParms.players + " | slotParms.course = " + slotParms.course + " -->");

        
        if (in_use != 0) {              // if time slot already in use
      
            out.println(SystemUtils.HeadTitle("Wait List Sign-Up In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            if (msg.endsWith( "after connection closed." )) {                           // if session timed out error           
                out.println("<CENTER><BR><BR><H2>Session Timed Out</H2>");
                out.println("<BR><BR>Sorry, but your session has timed out or your database connection has been lost.<BR>");
                out.println("<BR>Please exit ForeTees and try again.");
            } else {               
                out.println("<CENTER><BR><BR><H2>Wait List Sign-Up Busy</H2>");
                out.println("<BR><BR>Sorry, but this wait list entry is currently busy.<BR>");
                out.println("<BR>Please select another time or try again later.");
            }
            out.println("<BR><BR>");
            if (index.equals( "999" )) {       // if from Member_teelist (my tee times)

                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"/" +rev+ "/member_teemain.htm\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");
            } else {

                if (index.equals( "995" )) {       // if from Member_teelist_list (old my tee times)

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

                    } else {                           // from tee sheet

                        out.println("<font size=\"2\">");
                        out.println("<form method=\"get\" action=\"/" +rev+ "/member_selmain.htm\">");
                        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                        out.println("</form></font>");
                    }
                }
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }
      
        //
        //  wait list entry is available - get current player info
        //
        player1 = slotParms.player1;
        player2 = slotParms.player2;
        player3 = slotParms.player3;
        player4 = slotParms.player4;
        player5 = slotParms.player5;
        p1cw = slotParms.p1cw;
        p2cw = slotParms.p2cw;
        p3cw = slotParms.p3cw;
        p4cw = slotParms.p4cw;
        p5cw = slotParms.p5cw;
        notes = slotParms.notes;
        hide = slotParms.hide;
        p91 = slotParms.p91;
        p92 = slotParms.p92;
        p93 = slotParms.p93;
        p94 = slotParms.p94;
        p95 = slotParms.p95;
        user1 = slotParms.user1;
        user2 = slotParms.user2;
        user3 = slotParms.user3;
        user4 = slotParms.user4;
        user5 = slotParms.user5;

        // if there is a player in pos 5, but 5-somes are blocked, then force an allow
        if (!player5.equals("") && p5.equalsIgnoreCase("No")) p5 = "Yes";

      //
      //**********************************************
      //   Check for Member Notice from Pro
      //**********************************************
      String memNotice = verifySlot.checkMemNotice(date, slotParms.ok_stime, slotParms.ok_etime, fb, course, day_name, "teetime", false, con);

      if (!memNotice.equals( "" )) {      // if message to display

          //
          //  Display the Pro's Message and then prompt the user to either accept or return to the tee sheet
          //

          out.println("<HTML><HEAD>");
          out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
          out.println("<title>Member Notice For Wait List Request</Title>");
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
          out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"can\">");
          out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
          out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
          out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
          out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
          out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
          out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
          out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
          out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
          out.println("<input type=\"submit\" value=\"NO - Return\" name=\"cancel\">");

          out.println("</form></font></td>");

          out.println("<td align=\"center\">");

          out.println("<font size=\"2\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
          out.println("</font></td>");

          out.println("<td align=\"center\">");
          out.println("<font size=\"2\">");
          out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" target=\"_top\">");
          out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + signup_id + "\">");
          out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
          out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
          out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
          out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
          out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
          out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
          out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
          out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
          out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
          out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
          out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
          out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
          out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
          out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
          out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
          out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
          out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
          out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
          out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
          out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
          out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
          out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
          out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
          out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
          out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
          out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
          out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
          out.println("<input type=\"hidden\" name=\"memNotice\" value=\"yes\">");
          if (club.equals("loscoyotes")) {
              out.println("<input type=\"submit\" value=\"AGREE - Continue\">");
          } else {
              out.println("<input type=\"submit\" value=\"YES - Continue\">");
          }
          out.println("</form></font></td></tr>");
          out.println("</table>");

          out.println("</td>");
          out.println("</tr>");
          out.println("</table>");
          out.println("</font></center></body></html>");

          out.close();
          return;
      }

    } // end seeing what we are here to do

   //
   //  Ensure that there are no null player fields
   //
   if (player1 == null ) {
      player1 = "";
   }
   if (player2 == null ) {
      player2 = "";
   }
   if (player3 == null ) {
      player3 = "";
   }
   if (player4 == null ) {
      player4 = "";
   }
   if (player5 == null ) {
      player5 = "";
   }
   if (p1cw == null ) {
      p1cw = "";
   }
   if (p2cw == null ) {
      p2cw = "";
   }
   if (p3cw == null ) {
      p3cw = "";
   }
   if (p4cw == null ) {
      p4cw = "";
   }
   if (p5cw == null ) {
      p5cw = "";
   }
   
   //
   //  Get the walk/cart options available  
   //
   try {

      getParms.getTmodes(con, parmc, course);
   }
   catch (Exception e1) {

      msg = "Get wc options. ";
      dbError(out, e1, msg);
      return;
   }

   if (!pcw.equals( "" )) {
     
      i = 0;
      loopi1:
      while (i < parmc.tmode_limit) {

         if (parmc.tmodea[i].equals( pcw )) {

            break loopi1;
         }
         i++;
      }
      if (i > parmc.tmode_limit-1) {       // if we went all the way without a match

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
   if ((!player1.equals( name )) && (!player2.equals( name )) && (!player3.equals( name )) && (!player4.equals( name )) && (!player5.equals( name ))) {

      if (player1.equals("")) {

         player1 = name;
         p1cw = pcw;

      } else {

         if (parmWL.max_team_size > 1 && player2.equals("")) {

            player2 = name;
            p2cw = pcw;

         } else {

            if (parmWL.max_team_size > 2 && player3.equals("")) {

               player3 = name;
               p3cw = pcw;

            } else {

               if (parmWL.max_team_size > 3 && player4.equals("")) {

                  player4 = name;
                  p4cw = pcw;

               } else {

                  if ((p5.equals( "Yes" )) && (parmWL.max_team_size > 4) && (player5.equals(""))) {

                     player5 = name;
                     p5cw = pcw;

                  }
               }
            }
         }
      }
   }

   //
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<title>Member Wait List Sign-up Page</title>");
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

   out.println("<script type=\"text/javascript\">");            // Erase name script    (Note:  Put these in file???)  what other files use these scripts, just proshop_slot?
   out.println("<!--");

   out.println("function erasename(pPlayerPos, pCWoption) {");
       out.println("var p = eval(\"document.forms['playerform'].\" + pPlayerPos + \";\")");
       out.println("var o = eval(\"document.forms['playerform'].\" + pCWoption + \";\")");
       out.println("p.value = '';");        // clear player field
       
       // remove any Pro-Only tmodes from the wc field
       out.println("var m=0");
       out.println("var n=0");
       out.println("var found = new Boolean(false)");
       out.println("for (m = o.length - 1; m>=0; m--) {");
           out.println("found = false;");
           out.println("for (n=0; n<nonProCount; n++) {");
               out.println("if (o.options[m].value == nonProTmodes[n]) {");
                   out.println("found = true;");
                   out.println("break;");
               out.println("}");        // end of if
           out.println("}");        // end inner for
           out.println("if (found == false) {");
               out.println("o.options[m] = null;");
           out.println("}");        // end if
       out.println("}");        // end for
       
       out.println("o.selectedIndex = -1;");        // clear WC field
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //*******************************************************************
   //  Erase text area - (Notes)      erasetext
   //*******************************************************************
   //
   out.println("<script type='text/javascript'>");            // Erase text area script
   out.println("<!--");
   out.println("function erasetext(pos1) {");
   out.println("eval(\"document.forms['playerform'].\" + pos1 + \".value = '';\")");           // clear the player field
   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script

   //
   //**********************************************************************************
   //  Add to drop down list - add options to drop down lists
   //**********************************************************************************
   //
   // add option if not already in list (means it was member's default)
   out.println("<script type=\"text/javascript\">");
   out.println("function add(e, wc) {");
   out.println("<!--");
   out.println("  var i=0;");
   out.println("  for (i=0;i<e.length;i++) {");
   out.println("    if (e.options[i].value == wc) {");
   out.println("      return;");
   out.println("    }");        // end if
   out.println("  }");      // end for
   out.println("  e.options[e.length] = new Option(wc, wc);");
   out.println("}");            // End of function add()
   out.println("// -->");
   out.println("</script>");    // End of script

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

      if (p5.equals( "Yes" )) {
         out.println("var player5 = f.player5.value;");
      }
   }

   out.println("if (( name != 'x') && ( name != 'X')) {");


   if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

      if (p5.equals( "Yes" )) {
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
            out.println("add(f.p1cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p1cw.value = wc;");
         out.println("}");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
      if (parmWL.max_team_size > 1) {
         out.println("f.player2.value = name;");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("add(f.p2cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p2cw.value = wc;");
         out.println("}");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
   if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)
      out.println("} else {");

      out.println("if (player3 == '') {");                    // if player3 is empty
      if (parmWL.max_team_size > 2) {
         out.println("f.player3.value = name;");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("add(f.p3cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p3cw.value = wc;");
         out.println("}");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
      if (parmWL.max_team_size > 3) {
         out.println("f.player4.value = name;");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("add(f.p4cw, wc);");                    // add wc option if Pro Only and player default
            out.println("f.p4cw.value = wc;");
         out.println("}");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
         if (parmWL.max_team_size > 4) {
            out.println("f.player5.value = name;");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("add(f.p5cw, wc);");                    // add wc option if Pro Only and player default
               out.println("f.p5cw.value = wc;");
            out.println("}");
         } else {
            out.println("alert('All players slots have been filled.');");
         }
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
   out.println("var name = namewc;");
   out.println("var defCW = '';");
   out.println("var player1 = f.player1.value;");
   out.println("var player2 = f.player2.value;");

   if (club.equals( "wingedfoot" ) || club.equals("sfgc")) {

      out.println("defCW = 'CAD';");         // set default Mode of Trans
   }

   if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

      out.println("var player3 = f.player3.value;");
      out.println("var player4 = f.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = f.player5.value;");
      }
   }

   //  set spc to ' ' if name to move isn't an 'X'
   out.println("var spc = '';");
   out.println("if (name != 'X' && name != 'x') {");
   out.println("   spc = ' ';");
   out.println("}");
   
      out.println("if (player1 == '') {");                    // if player1 is empty
         out.println("f.player1.focus();");                   // here for IE compat
         out.println("f.player1.value = name + spc;");
         out.println("f.player1.focus();");
         out.println("f.p1cw.value = defCW;");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
      if (parmWL.max_team_size > 1) {
         out.println("f.player2.focus();");
         out.println("f.player2.value = name + spc;");
         out.println("f.player2.focus();");
         out.println("f.p2cw.value = defCW;");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
   if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

      out.println("} else {");

      out.println("if (player3 == '') {");                    // if player3 is empty
      if (parmWL.max_team_size > 2) {
         out.println("f.player3.focus();");
         out.println("f.player3.value = name + spc;");
         out.println("f.player3.focus();");
         out.println("f.p3cw.value = defCW;");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
      if (parmWL.max_team_size > 3) {
         out.println("f.player4.focus();");
         out.println("f.player4.value = name + spc;");
         out.println("f.player4.focus();");
         out.println("f.p4cw.value = defCW;");
      } else {
         out.println("alert('All players slots have been filled.');");
      }

      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                 // if player5 is empty
         if (parmWL.max_team_size > 4) {
         out.println("f.player5.focus();");
            out.println("f.player5.value = name + spc;");
         out.println("f.player5.focus();");
            out.println("f.p5cw.value = defCW;");
         } else {
            out.println("alert('All players slots have been filled.');");
         }

         out.println("}");
       }

      out.println("}");
      out.println("}");
   }
      out.println("}");
      out.println("}");

   out.println("}");                  // end of script function
   out.println("// -->");
   out.println("</script>");          // End of script
   //*******************************************************************************************

   out.println("</HEAD>");
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   // gather list of Non-Pro-Only tmodes locally
   String[] nonProTmodes = new String[16];
   int nonProCount = 0;
   for (int j=0; j<parmc.tmode_limit; j++) {
       if (parmc.tOpt[j] == 0 && !parmc.tmodea[j].equals("")) {
           nonProTmodes[nonProCount] = parmc.tmodea[j];
           nonProCount++;
       }
   }
   // use local list to populate global array in script
   out.println("<script type=\"text/javascript\">");
   out.println("<!-- ");
   out.println("var nonProCount = " + nonProCount + ";");
   out.println("var nonProTmodes = Array()");
   for (int j=0; j<nonProCount; j++) {
       out.println("nonProTmodes[" + j + "] = \"" + nonProTmodes[j] + "\";");
   }   
   out.println("// -->");
   out.println("</script>");

   out.println("<table border=\"0\" width=\"100%\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\">");

   out.println("<table width=\"90%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#336633\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"center\" width=\"160\" bgcolor=\"#F5F5DC\">");
     out.println("<font color=\"Darkred\" size=\"3\">DO NOT USE");
     out.println("<br>Your Browser's<br>Back Button!!</font>");
//     out.println("<img src=\"/" +rev+ "/images/foretees.gif\" border=0>");
     out.println("</td>");

     out.println("<td align=\"center\">");
     out.println("<font color=\"#ffffff\" size=\"5\">Member Wait List Registration</font>");
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"160\">");
     out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table width=\"100%\" border=\"0\" align=\"center\">");          // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
            out.println("<font size=\"2\" color=\"Darkred\">");
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this wait list entry.");
            out.println("&nbsp; If you want to return without completing a wait list entry, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("button below.");
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<font size=\"2\" color=\"black\">");
      out.println("<br>Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
        //out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Time:&nbsp;&nbsp;<b>" + stime + "</b>");
        if (!course.equals( "" )) {
           out.println(" &nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>");
        }
        out.println("<br></font>");
        
      out.println("<table border=\"0\" cellpadding=\"5\" cellspacing=\"5\" align=\"center\">"); // table to contain 4 tables below

         out.println("<tr>");
         out.println("<td align=\"center\" valign=\"top\">");         // col for Instructions and Go Back button

            out.println("<br><br>");
            out.println("<font size=\"2\" color=\"Darkred\">");
            out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
            out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
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
            out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/member_help_slot_instruct.htm', 'newwindow', config='Height=560, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
            out.println("<br>Click for Help</a>");

         out.println("</font></td>");

         out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"playerform\" id=\"playerform\">");

            out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
            out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
            
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

            out.println("<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1', 'p1cw')\" style=\"cursor:hand\">");
            out.println("1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\""+player1+"\" size=\"20\" maxlength=\"30\">");
            out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\" id=\"p1cw\">");

            out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
            for (i=0; i<16; i++) {        // get all c/w options

                if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw ) && parmc.tOpt[i] == 0) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                }
            }
            out.println("</select>");
            out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p91 == 1) ? "checked" : "") + " name=\"p91\" value=\"1\">");
                 
            if (parmWL.max_team_size > 1) {

                out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2', 'p2cw')\" style=\"cursor:hand\">");
                out.println("2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\""+player2+"\" size=\"20\" maxlength=\"30\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\" id=\"p2cw\">");

                out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
                for (i=0; i<16; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw ) && parmc.tOpt[i] == 0) {
                        out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                }
                out.println("</select>");
                out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p92 == 1) ? "checked" : "") + " name=\"p92\" value=\"1\">");
            
            } else {

                out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
                out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
              
            }
            
            // we could wrap 3-5 if twosome times and max_team_size is 0 (if not set - follow course parms & customs)
            
            if (parmWL.max_team_size > 2) {

                out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player3', 'p3cw')\" style=\"cursor:hand\">");
                out.println("3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\""+player3+"\" size=\"20\" maxlength=\"30\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\" id=\"p3cw\">");

                out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
                for (i=0; i<16; i++) {         // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw ) && parmc.tOpt[i] == 0) {
                        out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                }
                out.println("</select>");
                out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p93 == 1) ? "checked" : "") + " name=\"p93\" value=\"1\">");
            
            } else {

                out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
                out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
              
            }
                 
            if (parmWL.max_team_size > 3) {
                     
                out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player4', 'p4cw')\" style=\"cursor:hand\">");
                out.println("4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\""+player4+"\" size=\"20\" maxlength=\"30\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\" id=\"p4cw\">");

                out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                for (i=0; i<16; i++) {       // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw ) && parmc.tOpt[i] == 0) {
                       out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                    }
                }
                out.println("</select>");
                out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p94 == 1) ? "checked" : "") + " name=\"p94\" value=\"1\">");
                 
            } else {

                out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
                out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
              
            }
                 
            if (parmWL.max_team_size > 4) {

                out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player5', 'p5cw')\" style=\"cursor:hand\">");
                out.println("5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\""+player5+"\" size=\"20\" maxlength=\"30\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");

                out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                for (i=0; i<16; i++) {      // get all c/w options

                   if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw ) && parmc.tOpt[i] == 0) {
                      out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                   }
                }
                out.println("</select>");
                out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p95 == 1) ? "checked" : "") + " name=\"p95\" value=\"1\">");
                
            } else {

                out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
                out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
              
            }
                 
              out.println("<br><br>");
              
              out.println("<table border=\"0\" align=center>");
              out.println("<tr><td nowrap><font size=2>");

                //Common_Config.displayStartTime(shr, smin, start_ampm, out);
                Common_Config.displayHrMinToD(shr, smin, start_ampm, "From:", "start_hr", "start_min", "start_ampm", out);
                
              out.println("</font></td></tr><tr><td nowrap><font size=2>");

                //Common_Config.displayEndTime(ehr, emin, end_ampm, out);
                Common_Config.displayHrMinToD(ehr, emin, end_ampm, "To: &nbsp; &nbsp;", "end_hr", "end_min", "end_ampm", out);
                
              out.println("</font></td></tr>");
              out.println("</table>");
      
               //
               // Notes
               //
               if (hide != 0) {      // if proshop wants to hide the notes, do not display the text box or notes

                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">"); // pass existing notes

               } else {

                  out.println("<br>Contact Info / Notes<br>");
                  out.println("<img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasetext('notes')\" style=\"cursor:hand\">");
                  out.println("<textarea name=\"notes\" id=\"notes\" cols=\"30\" rows=\"2\">" + notes + "</textarea>");
               }
              
               out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=" + sdate + ">");
               out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
               out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
               //out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
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
               for (i=0; i<16; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("</font><br>");
                 
               //
               //  Get usernames from tee time in case not already present
               //
               msg = "";        // init error message

               if (user1.equals( "" ) || user1 == null) {
                 
                  try {

                     PreparedStatement pstmt = con.prepareStatement (
                        "SELECT username1, username2, username3, username4, username5 " +
                        "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

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

                  }
                  catch (Exception e1) {

                     msg = "Exception Received Verifying Users.  Exception: " + e1.getMessage();
                  }
               }

               //
               //  Make sure the user fields are valid
               //
               if (msg.equals( "" )) {        // if no error detected above

                  //
                  //  Determine if the 'Cancel Tee Time' button should be displayed.
                  //
                  allowCancel = true;             // default to Yes

                  //
                  //  Do not allow user to cancel the tee time if not already in it
                  //
                  if (!user.equalsIgnoreCase(user1) && !user.equalsIgnoreCase(user2) && !user.equalsIgnoreCase(user3) &&
                      !user.equalsIgnoreCase(user4) && !user.equalsIgnoreCase(user5) && !user.equalsIgnoreCase(slotParms.orig_by)) {

                     allowCancel = false;
                  }

                  //
                  //  Check if the 'Cancel Tee Time' button should be allowed
                  //
                  if (allowCancel == true) {    

                     out.println("<input type=submit value=\"Cancel Sign-up\" name=\"remove\" onclick=\"return confirm('Are you sure you want to remove this wait list entry?')\">&nbsp;&nbsp;&nbsp;");
                  }
                    
                  if (!slotParms.player1.equals("")) {   // if this is a change (not a new tee time)
                     out.println("<input type=submit value=\"Submit Changes\" name=\"submitForm\">");
                  } else {
                     out.println("<input type=submit value=\"Submit\" name=\"submitForm\">");
                  }
                  out.println("</font></td></tr>");
                  out.println("</table>");
                    
                  if (!userMship.equals( "Special" )) {

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
                  out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"can\">");
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
                  
                  //
                  //  save message in /" +rev+ "/error.txt
                  //
                  msg = "Error in Member_waitlist_slot for " +name+ ", user " +user+ " at " +club+ ".  Error = " +msg;   // build msg
                  SystemUtils.logError(msg);                                   // log it
                  return;  
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
      
      if (!letter.equals( "Partner List" )) {      // if not Partner List request
      
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

            PreparedStatement stmt2 = con.prepareStatement (
                     "SELECT name_last, name_first, name_mi, m_ship, wc FROM member2b " +
                     "WHERE name_last LIKE ? ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"20\" name=\"bname\" onclick=\"movename(this.value)\">"); // movename(this.form.bname.value)

            while(rs.next()) {

              last = rs.getString(1);
              first = rs.getString(2);
              mid = rs.getString(3);
              mship = rs.getString(4);
              wc = rs.getString(5);           // walk/cart preference

              i = 0;
              loopi3:
              while (i < 16) {             // make sure wc is supported

                 if (parmc.tmodea[i].equals( wc )) {

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
         }
         catch (Exception ignore) {
         }

         out.println("</table>");

      }        // end of IF Partner List or letter

   }           // not letter display

   if (letter.equals( "" ) || letter.equals( "Partner List" )) {  // if no letter or Partner List request

       alphaTable.displayPartnerList(user, sess_activity_id, 0, con, out);

   }        // end of if letter display

   out.println("</td>");                                      // end of this column
   out.println("<td width=\"200\" valign=\"top\">");



    //
    //   Output the Alphabit Table for Members' Last Names
    //
    alphaTable.getTable(out, user);
      
    //
    //     Check the club db table for X and guests
    //
    try {
        
        getClub.getParms(con, parm);        // get the club parms
        x = parm.x;
    }
    catch (Exception exc) {             // SQL Error - ignore guest and x

        x = 0;
    }
     
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
      out.println("&nbsp;&nbsp;<a href=\"javascript:void(0)\" onclick=\"moveguest('X')\">X</a>");
      out.println("</font></td></tr></table>");      // end of this table
   }
     
   //
   //  Check if Guest Type table should be displayed
   //
   boolean skipGuests = false;
   
   

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

         if (!parm.guest[i].equals( "" ) && parm.gOpt[i] == 0) {   // count the X and guest names

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

            if (!parm.guest[i].equals( "" ) && parm.gOpt[i] == 0) {   // if guest name is open for members

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
   out.close();

 }  // end of doPost


 // *********************************************************
 //  Process reservation request from Member_waitlist_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


    ResultSet rs = null;

    //
    //  Get this session's user name
    //
    String user = (String)session.getAttribute("user");
    //String fullName = (String)session.getAttribute("name");
    String club = (String)session.getAttribute("club");
    String posType = (String)session.getAttribute("posType");

    //
    // Get our notify uid if we are here to edit an existing wait list entry, if absent set to zero to indicate new wait list entry
    //
    String suid = req.getParameter("signupId");
    if (suid == null) suid = "0";
    
    String wlid = req.getParameter("waitListId");
    if (wlid == null) wlid = "0";
    
    int signup_id = 0;
    int wait_list_id = 0;

    //
    //  Convert the values from string to int
    //
    try {

        wait_list_id = Integer.parseInt(wlid);
        signup_id = Integer.parseInt(suid);
    }
    catch (NumberFormatException e) {
    }

    //out.println("<!-- wlid=" + wlid + " | suid=" + suid + " -->");
    out.println("<!-- wait_list_id=" + wait_list_id + " | signup_id=" + signup_id + " -->");
    
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
    int ok_stime = 0;
    int ok_etime = 0;
    int shr = 0;
    int ehr = 0;
    int smin = 0;
    int emin = 0;
    
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
    boolean busy = false;

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con); // waitlist is not supported under FlxRez so hard code the root activity_id to a zero for now

    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block
    
    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
    
    
    //  parm block to hold the course parameters
    parmCourse courseParms = new parmCourse();       // allocate a parm block
    
    parmWL.wait_list_id = wait_list_id;
    
    try {
        
        getWaitList.getParms(con, parmWL);                      // get the wait list config
        
    } catch (Exception ignore) { }
    
    slotParms.signup_id = signup_id;
    slotParms.wait_list_id = wait_list_id;
    
    slotParms.hndcp1 = 99;     // init handicaps
    slotParms.hndcp2 = 99;
    slotParms.hndcp3 = 99;
    slotParms.hndcp4 = 99;
    slotParms.hndcp5 = 99;

    //
    // Get all the parameters entered
    //
    String sok_stime = req.getParameter("ok_stime");    //  start of ok time (hhmm)
    String sok_etime = req.getParameter("ok_etime");    //  end of ok time (hhmm)
    
    String start_hr = req.getParameter("start_hr");
    String start_min = req.getParameter("start_min");
    String start_ampm = req.getParameter("start_ampm");
    String end_hr = req.getParameter("end_hr");
    String end_min = req.getParameter("end_min");
    String end_ampm = req.getParameter("end_ampm");
    
    String sdate = req.getParameter("date");            //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");            //  time of tee time requested (hhmm)
    String smm = req.getParameter("mm");                //  month of tee time
    String syy = req.getParameter("yy");                //  year of tee time
    String index = req.getParameter("index");           //  day index value (needed by _sheet on return)
    sfb = req.getParameter("fb");                       //  Front/Back indicator

    slotParms.p5 = req.getParameter("p5");              //  5-somes supported for this slot
    slotParms.course = req.getParameter("course");      //  name of course
    slotParms.player1 = req.getParameter("player1");
    slotParms.player2 = req.getParameter("player2");
    slotParms.player3 = req.getParameter("player3");
    slotParms.player4 = req.getParameter("player4");
    slotParms.player5 = req.getParameter("player5");
    slotParms.p1cw = req.getParameter("p1cw");
    slotParms.p2cw = req.getParameter("p2cw");
    slotParms.p3cw = req.getParameter("p3cw");
    slotParms.p4cw = req.getParameter("p4cw");
    slotParms.p5cw = req.getParameter("p5cw");
    slotParms.day = req.getParameter("day");            // name of day
    slotParms.notes = req.getParameter("notes").trim(); // Notes
    //slotParms.hides = req.getParameter("hide");       // Hide Notes

    // hacker check - possible form manipulation
    if (parmWL.member_access != 1) {
        
        out.println(SystemUtils.HeadTitle("Restriction Found"));
        out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<br><br>");
        out.println("<p><b>This wait list is not configured for member access.<br>Please contact your golf shop for assistance.</b></p>");
        out.println("<br><br>");
        out.println("<font size=\"2\" color=\"Darkred\">");
        out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" name=\"can\">");
        out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
        out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
        out.println("<input type=\"hidden\" name=\"day\" value=" + slotParms.day + ">");
        out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
        out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
        out.println("<input type=\"hidden\" name=\"jump\" value=\"0\">");
        out.println("Return<br>w/o Changes:<br>");
        out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
        out.println("<br><br>");
        out.println("</body></html>");
        out.close();
        return;
    }
    
    //
    //  set 9-hole options
    //
    slotParms.p91 = 0;                       // init to 18 holes
    slotParms.p92 = 0;
    slotParms.p93 = 0;
    slotParms.p94 = 0;
    slotParms.p95 = 0;

    if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      slotParms.p91 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      slotParms.p92 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      slotParms.p93 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      slotParms.p94 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      slotParms.p95 = Integer.parseInt(p9s);
    }

    //
    //  Ensure that there are no null player fields
    //
    if (slotParms.player1 == null ) slotParms.player1 = "";
    if (slotParms.player2 == null ) slotParms.player2 = "";
    if (slotParms.player3 == null ) slotParms.player3 = "";
    if (slotParms.player4 == null ) slotParms.player4 = "";
    if (slotParms.player5 == null ) slotParms.player5 = "";
    
    if (slotParms.p1cw == null ) slotParms.p1cw = "";
    if (slotParms.p2cw == null ) slotParms.p2cw = "";
    if (slotParms.p3cw == null ) slotParms.p3cw = "";
    if (slotParms.p4cw == null ) slotParms.p4cw = "";
    if (slotParms.p5cw == null ) slotParms.p5cw = "";

    
    //  retrieve course parameters
    try {
        getParms.getTmodes(con, courseParms, slotParms.course);
    } catch (Exception e) { }
    
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

    
    
    
    
/*    
    boolean getFromOK = false;
    try {
        
        shr = Integer.parseInt(start_hr);
        smin = Integer.parseInt(start_min);
        ehr = Integer.parseInt(end_hr);
        emin = Integer.parseInt(end_min);
    }
    catch (NumberFormatException e) {
        
        getFromOK = true;
    
    } finally {
    
        if (start_ampm.equals("PM") && shr != 12) shr += 12;
        ok_stime = (shr * 100) + smin;

        if (end_ampm.equals("PM") && ehr != 12) ehr += 12;
        ok_etime = (ehr * 100) + emin;

        out.println("<!-- FROM FINALLY: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        
    }
    
    if (getFromOK) {
    
        try {

            ok_stime = Integer.parseInt(sok_stime);
            ok_etime = Integer.parseInt(sok_etime);
            
        }
        catch (NumberFormatException e) {
            out.println("<!-- ERROR in ok_times: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        } finally {
            out.println("<!-- getFromOK: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        }
    
    }
*/    
    
    
    //
    // First, check to see if the time range is being passed in from sok_s/etime
    // if so then we are returning here after being prompted, if not then we
    // are here from the *initial* wait list slot submission
    //
    
    
    if (sok_stime == null || sok_etime == null) {
    
        try {

            shr = Integer.parseInt(start_hr);
            smin = Integer.parseInt(start_min);
            ehr = Integer.parseInt(end_hr);
            emin = Integer.parseInt(end_min);
        }
        catch (NumberFormatException e) {
            out.println("<!-- ERROR in (verify) FROM PARTS: start_hr=" + start_hr + " | start_min=" + start_min + " | end_hr=" + end_hr + " | end_min=" + end_min + " -->");
            dbError(out, e, "ERROR in (verify) FROM PARTS");
            return;
        }

        if (start_ampm.equals("PM") && shr != 12) shr += 12;
        ok_stime = (shr * 100) + smin;

        if (end_ampm.equals("PM") && ehr != 12) ehr += 12;
        ok_etime = (ehr * 100) + emin;

        out.println("<!-- FROM PARTS (verify) initial submit: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        
    } else {
        
        try {

            ok_stime = Integer.parseInt(sok_stime);
            ok_etime = Integer.parseInt(sok_etime);
            
        }
        catch (NumberFormatException e) {
            
            out.println("<!-- ERROR in (verify) sok_times: sok_stime=" + sok_stime + " | sok_etime=" + sok_etime + " -->");
            dbError(out, e, "ERROR in (verify) sok_times");
            return;
            
        }
            
        shr = ok_stime / 100;
        smin = ok_stime - (shr * 100);      
        start_ampm = "AM";
        if (shr > 11) {
            start_ampm = "PM";
            if (shr > 12) shr = shr - 12;
        }

        ehr = ok_etime / 100;
        emin = ok_etime - (ehr * 100);
        end_ampm = "AM";
        if (ehr > 11) {
            end_ampm = "PM";
            if (ehr > 12) ehr = ehr - 12;
        }
        
        out.println("<!-- FROM (verify) ok_time: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        
    }
    
/*
    boolean getFromOK = false;
    try {
        
        shr = Integer.parseInt(start_hr);
        smin = Integer.parseInt(start_min);
        ehr = Integer.parseInt(end_hr);
        emin = Integer.parseInt(end_min);
        
    
        if (start_ampm.equals("PM") && shr != 12) shr += 12;
        ok_stime = (shr * 100) + smin;

        if (end_ampm.equals("PM") && ehr != 12) ehr += 12;
        ok_etime = (ehr * 100) + emin;

        out.println("<!-- FROM PARTS (initial submit): ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
        
    }
    catch (NumberFormatException e) {
        
        getFromOK = true;
    
    }
    
    if (getFromOK) {
    
        try {

            ok_stime = Integer.parseInt(sok_stime);
            ok_etime = Integer.parseInt(sok_etime);
            
        }
        catch (NumberFormatException e) {
            out.println("<!-- ERROR in ok_times: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
            dbError(out, e.getMessage(), "ERROR in ok_times");
            return;
        }
        
        out.println("<!-- getFromOK: ok_stime=" + ok_stime + " | ok_etime=" + ok_etime + " -->");
    
    }
*/    
    
    
    
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

    if (!slotParms.notes.equals( "" )) {

        notesL = slotParms.notes.length();       // get length of notes
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
    //slotParms.req_datetime = yy + "-" + mm + "-" + dd + " " + hr + ":" + min + ":00";
    slotParms.date = date;
    slotParms.time = time;
    slotParms.ok_stime = ok_stime;
    slotParms.ok_etime = ok_etime;
    slotParms.mm = mm;
    slotParms.yy = yy;
    slotParms.dd = dd;
    slotParms.fb = fb;
    slotParms.ind = ind;      // index value
    slotParms.sfb = sfb; 
    slotParms.jump = jump;
    slotParms.club = club;    // name of club

    //boolean timeok = verifyWLSlot.checkTimes(slopParms, parmWL);
    
    String tmp_msg = verifyWLSlot.checkReqWindow(slotParms.ok_stime, slotParms.ok_etime, parmWL.start_time, parmWL.end_time);
    
    if ( !tmp_msg.equals("") ) {

        out.println(SystemUtils.HeadTitle("Data Entry Error"));
        out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H3>Improper Time Frame</H3>");
        out.println("<BR><BR>The times you provided for the wait list are fall outside the acceptable range.");
        out.println("<BR><BR><font color=red>" + tmp_msg + "</font>");
        out.println("<BR><BR>Please try again.");
        // if new
        //out.println("<BR>If you want to cancel the wait list entry, use the 'Cancel Sign-up' button under the player fields.");
        
        // if editing
        //out.println("<BR><BR>If you want to cancel the wait list entry, use the 'Cancel Sign-up' button under the player fields.");
        out.println("<BR><BR>");

        returnToSlot(out, slotParms);
        return;
    }
    
    
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
    if (signup_id != 0) {

        // it has to either be in use by us, or not be in use (shouldn't be the case)
        // or it can be in use by someone else if their lock has expired.
        try {

            PreparedStatement pstmt = con.prepareStatement (
                "SELECT * FROM wait_list_signups " +
                "WHERE wait_list_signup_id = ? " +
                    "AND " +
                     "( (in_use_by = '' || in_use_by = ?) " +
                     " || " +
                     "  UNIX_TIMESTAMP(in_use_at) < ( UNIX_TIMESTAMP() + (6 * 30) ) " +
                     ")");
                
            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, signup_id);
            pstmt.setString(2, user);
            rs = pstmt.executeQuery();      // execute the prepared stmt
            
            if (rs.next()) {

                //slotParms.req_datetime = rs.getString( "req_datetime" );
                //slotParms.course_id = rs.getInt( "course_id" );
                slotParms.last_user = rs.getString( "in_use_by" );
                slotParms.hideNotes = rs.getInt( "hideNotes" );
                //slotParms.notes = rs.getString( "notes" );
                slotParms.converted = rs.getInt( "converted" );

            } else {
                
                busy = true;
                
            }
            pstmt.close();

            // bomb out if we lost the signup
            if (busy) 
                returnToMemSheet(0,0,0,"","","",out, con);
            
            
            pstmt = con.prepareStatement (
                "SELECT * " +
                "FROM wait_list_signups_players " +
                "WHERE wait_list_signup_id = ? " +
                "ORDER BY pos");

            pstmt.clearParameters();
            pstmt.setInt(1, signup_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                slotParms.oldPlayer1 = rs.getString( "player_name" );
                slotParms.oldUser1 = rs.getString( "username" );
                slotParms.oldp1cw = rs.getString( "cw" );
                slotParms.oldp91 = rs.getInt( "9hole" );
                slotParms.players = 1;
            }

            if (rs.next()) {

                slotParms.oldPlayer2 = rs.getString( "player_name" );
                slotParms.oldUser2 = rs.getString( "username" );
                slotParms.oldp2cw = rs.getString( "cw" );
                slotParms.oldp92 = rs.getInt( "9hole" );
                slotParms.players = 2;
            }

            if (rs.next()) {

                slotParms.oldPlayer3 = rs.getString( "player_name" );
                slotParms.oldUser3 = rs.getString( "username" );
                slotParms.oldp3cw = rs.getString( "cw" );
                slotParms.oldp93 = rs.getInt( "9hole" );
                slotParms.players = 3;
            }

            if (rs.next()) {

                slotParms.oldPlayer4 = rs.getString( "player_name" );
                slotParms.oldUser4 = rs.getString( "username" );
                slotParms.oldp4cw = rs.getString( "cw" );
                slotParms.oldp94 = rs.getInt( "9hole" );
                slotParms.players = 4;
            }
                
            if (rs.next()) {

                slotParms.oldPlayer5 = rs.getString( "player_name" );
                slotParms.oldUser5 = rs.getString( "username" );
                slotParms.oldp5cw = rs.getString( "cw" );
                slotParms.oldp95 = rs.getInt( "9hole" );
                slotParms.players = 5;
            }
            
            pstmt.close();
            
            if (slotParms.orig_by.equals( "" )) {    // if originator field still empty

                slotParms.orig_by = user;             // set this user as the originator
            }
         
        }
        catch (Exception e) {

            msg = "Check if busy. ";
            dbError(out, e, msg);
            return;
        }
        
        // store the signup_id for use in verification (doing it above)
        //slotParms.signup_id = signup_id;

    } // end if signup_id != 0
    
    
    //
    //  If request is to 'Cancel This Res', then clear all fields for this slot
    //
    //  First, make sure user is already on tee slot or originated it
    //
    if (req.getParameter("remove") != null) {

        try {

            PreparedStatement pstmt4 = con.prepareStatement (
                "DELETE FROM wait_list_signups WHERE wait_list_signup_id = ?");

            pstmt4.clearParameters();
            pstmt4.setInt(1, signup_id);
            pstmt4.executeUpdate();

            pstmt4 = con.prepareStatement (
                "DELETE FROM wait_list_signups_players WHERE wait_list_signup_id = ?");

            pstmt4.clearParameters();
            pstmt4.setInt(1, signup_id);
            pstmt4.executeUpdate();
            pstmt4.close();
            
        }
        catch (Exception e4) {

            msg = "Remove wait list signup. ";
            dbError(out, e4, msg);
            return;
        }

        emailCan = 1;      // send email notification for Cancel Request
        sendemail = 1;
        
    } else {        //  not a Cancel Entry request

        //
        //  Normal request -
        //
        //   Get the guest names and other parms specified for this club
        //
        try {
            
            getClub.getParms(con, parm);            // get the club parms
            x = parmWL.allow_x;                     // was parm.x but we are going to let the wait list override club parameters
            xhrs = parm.xhrs;                       // save for later tests
            slotParms.rnds = parm.rnds;
            slotParms.hrsbtwn = parm.hrsbtwn;
        }
        catch (Exception ignore) {
        }

        //
        //  Shift players up if any empty spots
        //
        verifySlot.shiftUp(slotParms);

        //
        //  Check if any player names are guest names
        //
        try {

            verifySlot.parseGuests(slotParms, con);
        } catch (Exception e) {
            out.println("<p><b>ERROR: parseGuests - " + e.toString() + "</b></p>");
        }

        //
        //  Reject if any player was a guest type that is not allowed for members
        //
        if (!slotParms.gplayer.equals( "" ) && !slotParms.hit4) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            if (slotParms.hit3 == true) {                      // if error was name not specified
                out.println("<BR><BR>You must specify the name of your guest(s).");
                out.println("<BR><b>" + slotParms.gplayer + "</b> does not include a valid name (must be at least first & last names).");
                out.println("<BR><BR>To specify the name, click in the player box where the guest is specified, ");
                out.println("<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, ");
                out.println("<BR>use the space bar to enter a space and then type the guest's name.");
            } else {
                out.println("<BR><BR><b>" + slotParms.gplayer + "</b> specifies a Guest Type that is not allowed for member use.");
            }
            out.println("<BR><BR>If the Golf Shop had originally entered this guest, then it <b>must not</b> be changed.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }

        error = false;

        if (parm.unacompGuest == 0) {      // if unaccompanied guests not supported

            //
            //  Make sure at least 1 player contains a member
            //
            if (((slotParms.player1.equals( "" )) || (slotParms.player1.equalsIgnoreCase( "x" )) || (!slotParms.g1.equals( "" ))) &&
                ((slotParms.player2.equals( "" )) || (slotParms.player2.equalsIgnoreCase( "x" )) || (!slotParms.g2.equals( "" ))) &&
                ((slotParms.player3.equals( "" )) || (slotParms.player3.equalsIgnoreCase( "x" )) || (!slotParms.g3.equals( "" ))) &&
                ((slotParms.player4.equals( "" )) || (slotParms.player4.equalsIgnoreCase( "x" )) || (!slotParms.g4.equals( "" ))) &&
                ((slotParms.player5.equals( "" )) || (slotParms.player5.equalsIgnoreCase( "x" )) || (!slotParms.g5.equals( "" )))) {

            error = true;
            }

        } else {           // guests are ok

            //
            //  Make sure at least 1 player contains a member
            //
            if (((slotParms.player1.equals( "" )) || (slotParms.player1.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player2.equals( "" )) || (slotParms.player2.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player3.equals( "" )) || (slotParms.player3.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player4.equals( "" )) || (slotParms.player4.equalsIgnoreCase( "x" ))) &&
                ((slotParms.player5.equals( "" )) || (slotParms.player5.equalsIgnoreCase( "x" )))) {

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
            out.println("<BR>If you want to cancel the wait list entry, use the 'Cancel Sign-up' button under the player fields.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }

        //
        //  Check the number of X's against max specified by proshop
        //
        xcount = 0;

        if (slotParms.player1.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player2.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player3.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player4.equalsIgnoreCase( "x" )) xcount++;
        if (slotParms.player5.equalsIgnoreCase( "x" )) xcount++;

        if (xcount > x) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + x + ") for this wait list.");
            out.println("<BR>Please try again.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }

        //
        //  At least 1 Player is present - Make sure a C/W was specified for all players
        //
        if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.p1cw.equals( "" ))) ||
            ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.p2cw.equals( "" ))) ||
            ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.p3cw.equals( "" ))) ||
            ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.p4cw.equals( "" ))) ||
            ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.p5cw.equals( "" )))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }

        //
        //  Make sure there are no duplicate names
        //
        player = "";

        if ((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) {

            if ((slotParms.player1.equalsIgnoreCase( slotParms.player2 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player3 )) ||
                (slotParms.player1.equalsIgnoreCase( slotParms.player4 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player5 ))) {

                player = slotParms.player1;
            }
        }

      if ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) {

         if ((slotParms.player2.equalsIgnoreCase( slotParms.player3 )) || (slotParms.player2.equalsIgnoreCase( slotParms.player4 )) ||
             (slotParms.player2.equalsIgnoreCase( slotParms.player5 ))) {

            player = slotParms.player2;
         }
      }

      if ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) {

         if ((slotParms.player3.equalsIgnoreCase( slotParms.player4 )) ||
             (slotParms.player3.equalsIgnoreCase( slotParms.player5 ))) {

            player = slotParms.player3;
         }
      }

      if ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) {

         if (slotParms.player4.equalsIgnoreCase( slotParms.player5 )) {

            player = slotParms.player4;
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

            returnToSlot(out, slotParms);
            return;
        }

        //
        //  Parse the names to separate first, last & mi
        //
        try {

            error = verifySlot.parseNames(slotParms, "mem");

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
            out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + slotParms.player1 + "',&nbsp;&nbsp;&nbsp;'");
            out.println(slotParms.player2 + "',&nbsp;&nbsp;&nbsp;'" + slotParms.player3 + "',&nbsp;&nbsp;&nbsp;'");
            out.println(slotParms.player4 + "',&nbsp;&nbsp;&nbsp;'" + slotParms.player5 + "'");
            out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
            out.println("<BR><BR>");
            out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
            out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the wait list entry.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }


        //
        //  Get the usernames, membership types, & hndcp's for players if matching name found
        //
        try {

            verifySlot.getUsers(slotParms, con);
        }
        catch (Exception e1) {

            msg = "Check guest names. ";
            dbError(out, e1, msg);                        // reject
            return;
        }
        
        
        //
        //  No players are using Pro-Only transportation modes without authorization
        //
        if (courseParms.hasProOnlyTmodes && !verifySlot.checkProOnlyMOT(slotParms, courseParms, con)) {
            
            out.println(SystemUtils.HeadTitle("Access Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Access Error</H3>");
            out.println("<BR><BR><b>'" + slotParms.player + "'</b> is not authorized to use that mode of transportation.");
            out.println("<BR><BR>Please select another mode of transportation.");
            out.println("<BR>Contact your club if you require assistance with restricted modes of transportation.");
            out.println("<BR><BR>");
            
            returnToSlot(out, slotParms);
            return;
        }
        
        //
        // Check to see if any of these players on already on the wait list
        //
        verifyWLSlot.checkPlayersAgainstList(slotParms, con, out);

        if ( !slotParms.player.equals("") ) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Member Already On Wait List</H3>");
            out.println("<BR><BR>" + slotParms.player + " is already signed up on this wait list.");
            out.println("<BR><BR>Please remove this player and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
        }
      
        //
        //  Save the members' usernames for guest association
        //
        memA[0] = slotParms.user1;
        memA[1] = slotParms.user2;
        memA[2] = slotParms.user3;
        memA[3] = slotParms.user4;
        memA[4] = slotParms.user5;

        //
        //  Check if any of the names are invalid.  
        //
        int invalNum = 0;
        p1 = "";
        
        if (slotParms.inval1 != 0) {

            p1 = slotParms.player1;                        // reject
            invalNum = slotParms.inval1;
        }
        if (slotParms.inval2 != 0) {

            p1 = slotParms.player2;                        // reject
            invalNum = slotParms.inval2;
        }
        if (slotParms.inval3 != 0) {

            p1 = slotParms.player3;                        // reject
            invalNum = slotParms.inval3;
        }
        if (slotParms.inval4 != 0) {

            p1 = slotParms.player4;                        // reject
            invalNum = slotParms.inval4;
        }
        if (slotParms.inval5 != 0) {

            p1 = slotParms.player5;                        // reject
            invalNum = slotParms.inval5;
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
            out.println("<BR><BR>You will have to remove this name from your wait list entry.");
            out.println("<BR><BR>");

         } else {
           
            out.println("<BR><H3>Invalid Member Name Received</H3><BR>");
            out.println("<BR><BR>Sorry, a name you entered is not recognized as a valid member.<BR>");
            out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "'");
            out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
            out.println("<BR><BR>");
            out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
            out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the wait list entry.");
            out.println("<BR><BR>");
         }

         returnToSlot(out, slotParms);
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
               out.println("<BR><BR>Sorry, 'X' is not allowed for this wait list signup.<BR>");
               out.println("It is not far enough in advance to reserve a player position with an X.");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms);
               return;
            }
         }
      }        // end of IF xcount

      //
      //************************************************************************
      //  Check any membership types for max rounds per week, month or year
      //************************************************************************
      //
      if (!slotParms.mship1.equals( "" ) ||
          !slotParms.mship2.equals( "" ) ||
          !slotParms.mship3.equals( "" ) ||
          !slotParms.mship4.equals( "" ) ||
          !slotParms.mship5.equals( "" )) {                // if at least one name exists then check number of rounds

         error = false;                             // init error indicator

         try {

            error = verifySlot.checkMaxRounds(slotParms, con);
         }
         catch (Exception e) {
            out.println("<p><b>ERROR: checkMaxRounds - " + e.toString() + "</b></p>");
         }

         if (error == true) {      // a member exceed the max allowed tee times per week, month or year

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
            out.println("<BR><BR>Sorry, " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>");
            out.println("maximum number of tee times allowed for this " + slotParms.period + ".");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
         }

      }  // end of mship if

      //
      // **************************************
      //  Check for max # of guests exceeded (per member)
      // **************************************
      //
      if (slotParms.guests != 0) {      // if any guests were included

         error = false;                             // init error indicator

         //
         //  if 1 guest and 3 members, then always ok (do not check restrictions)   ?????????????????????
         //
         //if (slotParms.guests != 1 || slotParms.members < 3) {
            
            if (parmWL.allow_guests < slotParms.guests) {      // a member exceed the max allowed tee times per month

               out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Sorry, the maximum number of guests allowed for this ");
               out.println("wait list is " + parmWL.allow_guests + ".");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms);
               return;
            }
            
            // if we didn't hit the max guests for the wait list, then check guest retrictions
            if (!error) {
             
                try {

                   error = verifySlot.checkMaxGuests(slotParms, con);
                }
                catch (Exception e5) {

                   msg = "Check Max Allowed Guests. ";
                   dbError(out, e5, msg);
                   return;
                }
                
            }
            
            if (error) {      // a member exceed the max allowed tee times per month

               out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
               out.println("time frame you are requesting is " + slotParms.grest_num + " per " +slotParms.grest_per+ ".");
               out.println("<BR><BR>Guest Restriction = " + slotParms.rest_name);
               out.println("<BR><BR>");

               returnToSlot(out, slotParms);
               return;
            }

         //} // end questionable guest check

      } // end if guests

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

         error = verifySlot.checkMemRests(slotParms, con);
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
         out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b><br><br>");
         out.println("Please remove this player or try a different time.<br>");
         out.println("Contact the Golf Shop if you have any questions.<br>");
         out.println("<BR><BR>");

         returnToSlot(out, slotParms);
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

         error = verifySlot.checkMemNum(slotParms, con);
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
            if (!slotParms.pnum1.equals( "" )) {
               out.println("<b>" + slotParms.pnum1 + "</b> ");
            }
            if (!slotParms.pnum2.equals( "" )) {
               out.println("<b>" + slotParms.pnum2 + "</b> ");
            }
            if (!slotParms.pnum3.equals( "" )) {
               out.println("<b>" + slotParms.pnum3 + "</b> ");
            }
            if (!slotParms.pnum4.equals( "" )) {
               out.println("<b>" + slotParms.pnum4 + "</b> ");
            }
            if (!slotParms.pnum5.equals( "" )) {
               out.println("<b>" + slotParms.pnum5 + "</b> ");
            }
         out.println("is/are restricted from playing during this time because the");
         out.println("<BR> number of members with the same member number has exceeded the maximum allowed.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b><br><br>");
         out.println("Please remove this player(s) or try a different time.<br>");
         out.println("Contact the Golf Shop if you have any questions.<br>");
         out.println("<BR><BR>");

         returnToSlot(out, slotParms);
         return;
      }

      //
      //***********************************************************************************************
      //
      //    Now check if any of the players are already scheduled today (only 1 res per day)
      //
      //***********************************************************************************************
      //
      slotParms.hit = false;                             // init error indicator
      slotParms.hit2 = false;                             // init error indicator
      String tmsg = "";
      int thr = 0;
      int tmin = 0;

      try {

         verifySlot.checkSched(slotParms, con);

      }
      catch (Exception e21) {

         msg = "Check Members Already Scheduled. ";

         dbError(out, e21, msg);
         return;
      }

      if (slotParms.hit == true || slotParms.hit2 == true || slotParms.hit3 == true) { // if we hit on a duplicate res

         if (slotParms.time2 != 0) {                                  // if other time was returned
           
            thr = slotParms.time2 / 100;                      // set time string for message
            tmin = slotParms.time2 - (thr * 100);
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
            if (!slotParms.course2.equals( "" )) {        // if course provided
              
               tmsg = tmsg + " on the " +slotParms.course2+ " course";
            }
         }
         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">"); 
         out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
         if (slotParms.rnds > 1) {       // if multiple rounds per day supported
            if (slotParms.hit3 == true) {       // if rounds too close together
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is scheduled to play another round within " +slotParms.hrsbtwn+ " hours.<br><br>");
               out.println(slotParms.player + " is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            } else {
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
               out.println("A player can only be scheduled " +slotParms.rnds+ " times per day.<br><br>");
            }
         } else {
            if (slotParms.hit2 == true) {
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is part of a lottery request for this date.<br><br>");
            } else {
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            }
            out.println("A player can only be scheduled once per day.<br><br>");
         }
         out.println("Please remove this player or try a different date.<br>");
         out.println("Contact the Golf Shop if you have any questions.");
         out.println("<BR><BR>");
         out.println("If you are already scheduled for this date and would like to remove yourself<br>");
         out.println("from that wait list entry, use the 'Go Back' button to return to the tee sheet and <br>");
         out.println("locate the time stated above, or click on the 'My Notifications' tab.");
         out.println("<BR><BR>");

         returnToSlot(out, slotParms);
         return;
      }

      //
      //***********************************************************************************************
      //
      //    Now check all players for 'days in advance' - based on membership types
      //
      //***********************************************************************************************
      //
      if (!slotParms.mship1.equals( "" ) || !slotParms.mship2.equals( "" ) || !slotParms.mship3.equals( "" ) ||
          !slotParms.mship4.equals( "" ) || !slotParms.mship5.equals( "" )) {

           try {

              //error = verifySlot.checkDaysAdv(slotParms, con);
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
              out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to be part of a tee time this far in advance.<br><br>");
              if (x > 0) {
                 out.println("You can use an 'X' to reserve this position until the player is allowed.<br><br>");
              } else {
                 out.println("Contact the golf shop if you wish to add this person at this time.<br><br>");
              }
              out.println("<BR><BR>");

              returnToSlot(out, slotParms);
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
         if (slotParms.guests > 0) {

            //
            //  If no members requested and Unaccompanied Guests are ok at this club
            //
            if (slotParms.members == 0 && parm.unacompGuest == 1) {  

               if (!slotParms.g1.equals( "" )) {  // if player is a guest

                  slotParms.userg1 = user;        // set username for guests 
               }
               if (!slotParms.g2.equals( "" )) {  

                  slotParms.userg2 = user;      
               }
               if (!slotParms.g3.equals( "" )) {

                  slotParms.userg3 = user;
               }
               if (!slotParms.g4.equals( "" )) {

                  slotParms.userg4 = user;
               }
               if (!slotParms.g5.equals( "" )) {

                  slotParms.userg5 = user;
               }
               
            } else {

               if (slotParms.members > 0) {     // if at least one member

                  //
                  //  Both guests and members specified (member verified above) - determine guest owners by order
                  //
                  gi = 0;
                  memberName = "";

                  while (gi < 5) {                  // cycle thru arrays and find guests/members

                     if (!slotParms.gstA[gi].equals( "" )) {

                        usergA[gi] = memberName;       // get last players username
                     } else {
                        usergA[gi] = "";               // init array entry
                     }
                     if (!memA[gi].equals( "" )) {

                        memberName = memA[gi];        // get players username
                     }
                     gi++;
                  }
                  slotParms.userg1 = usergA[0];        // set usernames for guests in teecurr
                  slotParms.userg2 = usergA[1];
                  slotParms.userg3 = usergA[2];
                  slotParms.userg4 = usergA[3];
                  slotParms.userg5 = usergA[4];
               }

               if (slotParms.members > 1 || !slotParms.g1.equals( "" )) {  // if multiple members OR slot 1 is a guest

                  //
                  //  At least one guest and 2 members have been specified, or P1 is a guest.
                  //  Prompt user to verify the order.
                  //
                  //  Only require positioning if a POS system was specified for this club (saved in Login)
                  //
                  out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

                  //
                  //  if player1 is a guest & POS & not already assigned
                  //
                  if (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

                     out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                     out.println("The first player position cannot contain a guest.  Please correct the order<br>");
                     out.println("of players.  This is what you requested:");

                  } else {

                     out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                     out.println("Please verify that the following order is correct:");
                  }
                  out.println("<BR><BR>");
                  out.println(slotParms.player1 + " <BR>");
                  out.println(slotParms.player2 + " <BR>");
                  if (!slotParms.player3.equals( "" )) {
                     out.println(slotParms.player3 + " <BR>");
                  }
                  if (!slotParms.player4.equals( "" )) {
                     out.println(slotParms.player4 + " <BR>");
                  }
                  if (!slotParms.player5.equals( "" )) {
                     out.println(slotParms.player5 + " <BR>");
                  }

                  if (slotParms.g1.equals( "" ) || posType.equals( "" ) || slotParms.oldPlayer1.equals( slotParms.player1 )) {

                     out.println("<BR>Would you like to process the request as is?");
                  }

                  //
                  //  Return to _slot to change the player order
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
                  out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + ok_stime + "\">");
                  out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + ok_etime + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");

                  if (slotParms.g1.equals( "" ) || posType.equals( "" ) || slotParms.oldPlayer1.equals( slotParms.player1 )) {

                     out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");

                  } else {
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                  }
                  out.println("</form></font>");

                  if (slotParms.g1.equals( "" ) || posType.equals( "" ) || slotParms.oldPlayer1.equals( slotParms.player1 )) {

                     //
                     //  Return to process the players as they are
                     //
                     out.println("<font size=\"2\">");
                     out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + signup_id + "\">");
                     out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + wait_list_id + "\">");
                     out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
                     out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                     out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                     out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                     out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                     out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                     out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                     out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                     out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                     out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                     out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                     out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + ok_stime + "\">");
                     out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + ok_etime + "\">");
                     out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
                     out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                     out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
                     out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
                     out.println("</form></font>");
                  }
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;

               }   // end of IF more than 1 member or guest in spot #1
            }      // end of IF no members and unaccompanied guests are ok
            
         }         // end of IF any guests specified

      } else {   // skip 8 requested
          
         //
         //  User has responded to the guest association prompt - process tee time request in specified order
         //
         slotParms.userg1 = req.getParameter("userg1");
         slotParms.userg2 = req.getParameter("userg2");
         slotParms.userg3 = req.getParameter("userg3");
         slotParms.userg4 = req.getParameter("userg4");
         slotParms.userg5 = req.getParameter("userg5");
      }         // end of IF skip8


      //
      //***********************************************************************************************
      //
      //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
      //
      //***********************************************************************************************
      //
      if (!slotParms.userg1.equals( "" ) || !slotParms.userg2.equals( "" ) || !slotParms.userg3.equals( "" ) ||
          !slotParms.userg4.equals( "" ) || !slotParms.userg5.equals( "" )) {

         try {

            error = verifySlot.checkGuestQuota(slotParms, con);

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
            out.println("<BR>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established by the Golf Shop.");
            out.println("<br><br>You will have to remove the guest in order to complete this request.");
            out.println("<br><br>Contact the Golf Shop if you have any questions.<br>");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms);
            return;
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
      if (!slotParms.player1.equals( slotParms.oldPlayer1 )) {

         slotParms.show1 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!slotParms.player2.equals( slotParms.oldPlayer2 )) {

         slotParms.show2 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!slotParms.player3.equals( slotParms.oldPlayer3 )) {

         slotParms.show3 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!slotParms.player4.equals( slotParms.oldPlayer4 )) {

         slotParms.show4 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!slotParms.player5.equals( slotParms.oldPlayer5 )) {

         slotParms.show5 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      //
      //   Set email type based on new or update request (cancel set above)
      //   Also, bump stats counters for reports
      //
      if ((!slotParms.oldPlayer1.equals( "" )) || (!slotParms.oldPlayer2.equals( "" )) || (!slotParms.oldPlayer3.equals( "" )) ||
          (!slotParms.oldPlayer4.equals( "" )) || (!slotParms.oldPlayer5.equals( "" ))) {

         emailMod = 1;  // tee time was modified
         memMod++;      // increment number of mods

      } else {

         emailNew = 1;  // tee time is new
         memNew++;      // increment number of new tee times
      }

   }  // end of 'cancel this res' if - cancel will contain empty player fields


    //int course_id = SystemUtils.getClubParmIdFromCourseName(slotParms.course, con);

    //
    //  Verification complete -
    //  Add or Update the wait list entry entry in the wait list tables
    //
    
    if (signup_id == 0) {

        out.println("<!-- ATTEMPTING INSERT [" + slotParms.date + ", " + slotParms.ok_stime + ", " + slotParms.ok_etime + "] -->");
        // add new wait list entry
        try {

            // Add the wait list entry
            PreparedStatement pstmt6 = con.prepareStatement (
                "INSERT INTO wait_list_signups " +
                "(wait_list_id, date, ok_stime, ok_etime, notes, created_by, created_datetime) VALUES (?, ?, ?, ?, ?, ?, now())");
            pstmt6.clearParameters();
            pstmt6.setInt(1, wait_list_id);
            pstmt6.setLong(2, slotParms.date);
            pstmt6.setInt(3, slotParms.ok_stime);
            pstmt6.setInt(4, slotParms.ok_etime);
            pstmt6.setString(5, slotParms.notes);
            pstmt6.setString(6, user);
            pstmt6.executeUpdate();

            pstmt6 = con.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet rsLastID = pstmt6.executeQuery();
            while (rsLastID.next()) {
                signup_id = rsLastID.getInt(1);
            }

            pstmt6.close();

            // add the players of this wait list entry
            if (!slotParms.player1.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 1)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user1);
                pstmt6.setString(3, slotParms.p1cw);
                pstmt6.setString(4, slotParms.player1);
                pstmt6.setInt(5, slotParms.p91);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player2.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 2)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user2);
                pstmt6.setString(3, slotParms.p2cw);
                pstmt6.setString(4, slotParms.player2);
                pstmt6.setInt(5, slotParms.p92);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player3.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 3)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user3);
                pstmt6.setString(3, slotParms.p3cw);
                pstmt6.setString(4, slotParms.player3);
                pstmt6.setInt(5, slotParms.p93);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player4.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 4)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user4);
                pstmt6.setString(3, slotParms.p4cw);
                pstmt6.setString(4, slotParms.player4);
                pstmt6.setInt(5, slotParms.p94);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player5.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 5)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user5);
                pstmt6.setString(3, slotParms.p5cw);
                pstmt6.setString(4, slotParms.player5);
                pstmt6.setInt(5, slotParms.p95);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
            
       }
       catch (Exception e6) {

          msg = "Add Wait List Entry. ";
          dbError(out, e6, msg);
          return;
       }
        
        
    } else {
        
        out.println("<!-- ATTEMPTING UPDATES [" + signup_id + ", " + slotParms.date + ", " + slotParms.ok_stime + ", " + slotParms.ok_etime + "] -->");
        // update existing entry
        try {

            PreparedStatement pstmt6 = con.prepareStatement (
                "UPDATE wait_list_signups " +
                "SET ok_stime = ?, ok_etime = ?, notes = ? " +
                "WHERE wait_list_signup_id = ?");
            
            pstmt6.setInt(1, slotParms.ok_stime);
            pstmt6.setInt(2, slotParms.ok_etime);
            pstmt6.setString(3, slotParms.notes);
            pstmt6.setInt(4, signup_id);
            pstmt6.executeUpdate();
            
            
            // add/update players
            updateSignUpPlayers(signup_id, slotParms.player1, slotParms.oldPlayer1, slotParms.user1, slotParms.p1cw, slotParms.oldp1cw, slotParms.p91, slotParms.oldp91, 1, con, out);
            updateSignUpPlayers(signup_id, slotParms.player2, slotParms.oldPlayer2, slotParms.user2, slotParms.p2cw, slotParms.oldp2cw, slotParms.p92, slotParms.oldp92, 2, con, out);
            updateSignUpPlayers(signup_id, slotParms.player3, slotParms.oldPlayer3, slotParms.user3, slotParms.p3cw, slotParms.oldp3cw, slotParms.p93, slotParms.oldp93, 3, con, out);
            updateSignUpPlayers(signup_id, slotParms.player4, slotParms.oldPlayer4, slotParms.user4, slotParms.p4cw, slotParms.oldp4cw, slotParms.p94, slotParms.oldp94, 4, con, out);
            updateSignUpPlayers(signup_id, slotParms.player5, slotParms.oldPlayer5, slotParms.user5, slotParms.p5cw, slotParms.oldp5cw, slotParms.p95, slotParms.oldp95, 5, con, out);

            // clear in_use fields
            pstmt6 = con.prepareStatement (
                "UPDATE wait_list_signups SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE wait_list_signup_id = ?");
            
            pstmt6.clearParameters();
            pstmt6.setInt(1, signup_id);
            pstmt6.executeUpdate();
            pstmt6.close();

        }
        catch (Exception e6) {

            msg = "Update Wait List Sign-up.";
            dbError(out, e6, msg);
            return;
        }
            
    } // end if insert or update
    
    
    
/*
   //
   //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
   //
   if (slotParms.oldPlayer1.equals( "" ) && slotParms.oldPlayer2.equals( "" ) && slotParms.oldPlayer3.equals( "" ) &&
       slotParms.oldPlayer4.equals( "" ) && slotParms.oldPlayer5.equals( "" )) {

      //  new tee time
      SystemUtils.updateHist(date, slotParms.day, time, fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,  
                             slotParms.player4, slotParms.player5, user, fullName, 0, con);
     
   } else {
     
      //  update tee time
      SystemUtils.updateHist(date, slotParms.day, time, fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 1, con);
   }
*/

   //
   //  Build the HTML page to confirm wait list entry for user
   //
   out.println(SystemUtils.HeadTitle("Member Tee Slot Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;The wait list entry has been cancelled.</p>");
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;Your wait list entry has been accepted and processed.</p>");

      if (xcount > 0 && xhrs > 0) {            // if any X's were specified 

         out.println("<p>&nbsp;</p>Please note that if you receive a tee time all player positions<br>reserved by an 'X' must be filled within " + xhrs + " hours of the tee time.");
         out.println("<br>If not, the system will automatically remove the X.<br>");
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
            out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
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
   if (sendemail != 0 && ind > 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "waitlist";         // type = wait list
      parme.date = date;
      //parme.time = time;
      parme.time = ok_stime;
      parme.time2 = ok_etime;
      parme.fb = fb;
      parme.mm = mm;
      parme.dd = dd;
      parme.yy = yy;

      parme.user = user;
      parme.emailNew = emailNew;
      parme.emailMod = emailMod;
      parme.emailCan = emailCan;

      parme.p91 = slotParms.p91;
      parme.p92 = slotParms.p92;
      parme.p93 = slotParms.p93;
      parme.p94 = slotParms.p94;
      parme.p95 = slotParms.p95;

      parme.course = slotParms.course;
      parme.day = slotParms.day;
      parme.notes = slotParms.notes;

      parme.player1 = slotParms.player1;
      parme.player2 = slotParms.player2;
      parme.player3 = slotParms.player3;
      parme.player4 = slotParms.player4;
      parme.player5 = slotParms.player5;

      parme.oldplayer1 = slotParms.oldPlayer1;
      parme.oldplayer2 = slotParms.oldPlayer2;
      parme.oldplayer3 = slotParms.oldPlayer3;
      parme.oldplayer4 = slotParms.oldPlayer4;
      parme.oldplayer5 = slotParms.oldPlayer5;

      parme.user1 = slotParms.user1;
      parme.user2 = slotParms.user2;
      parme.user3 = slotParms.user3;
      parme.user4 = slotParms.user4;
      parme.user5 = slotParms.user5;

      parme.olduser1 = slotParms.oldUser1;
      parme.olduser2 = slotParms.oldUser2;
      parme.olduser3 = slotParms.oldUser3;
      parme.olduser4 = slotParms.oldUser4;
      parme.olduser5 = slotParms.oldUser5;

      parme.pcw1 = slotParms.p1cw;
      parme.pcw2 = slotParms.p2cw;
      parme.pcw3 = slotParms.p3cw;
      parme.pcw4 = slotParms.p4cw;
      parme.pcw5 = slotParms.p5cw;

      parme.oldpcw1 = slotParms.oldp1cw;
      parme.oldpcw2 = slotParms.oldp2cw;
      parme.oldpcw3 = slotParms.oldp3cw;
      parme.oldpcw4 = slotParms.oldp4cw;
      parme.oldpcw5 = slotParms.oldp5cw;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

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
 //  Process cancel request (Return w/o changes) from Member_waitlist_slot (HTML)
 // ************************************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, String club, Connection con) {


    //int count = 0;
    //int time  = 0;
    //int fb  = 0;
    //long date  = 0;

    //
    // Get all the parameters entered
    //
    //String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    //String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    //String sfb = req.getParameter("fb");               //  front/back indicator
    String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
    String course = req.getParameter("course");        //  name of course (needed by Member_sheet when returning)
    //String day = req.getParameter("day");              //  name of the day

    //
    // Get our notify uid if we are here to edit an existing wait list entry, if absent set to zero to indicate new wait list entry
    //
    String snid = req.getParameter("signupId");
    if (snid == null) snid = "0";
    int signup_id = 0;

    //
    //  Convert the values from string to int
    //
    try {

        signup_id = Integer.parseInt(snid);
    }
    catch (NumberFormatException e) {
    }

    //
    //  Clear the 'in_use' flag for this time slot in wait list table
    //
    if (signup_id != 0) {
        try {

            PreparedStatement pstmt1 = con.prepareStatement (
                "UPDATE wait_list_signups SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE wait_list_signup_id = ?");

            pstmt1.clearParameters();
            pstmt1.setInt(1, signup_id);
            pstmt1.executeUpdate();
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
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
    out.println("<title>Member Tee Slot Page</title>");

    if (index.equals( "999" )) {       // if from Member_teelist

        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/member_teemain.htm\">");
        out.println("</HEAD>");
        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
        out.println("<BR><BR>Thank you, the wait list entry has been returned to the system without changes.");
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
            out.println("<BR><BR>Thank you, the wait list entry has been returned to the system without changes.");
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
                out.println("<BR><BR>Thank you, the wait list entry has been returned to the system without changes.");
                out.println("<BR><BR>");

                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"/" +rev+ "/member_searchmem.htm\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");

            } else {

                out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_jump?index=" + index + "&course=" + course + "\">");
                out.println("</HEAD>");
                out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                out.println("<hr width=\"40%\">");
                out.println("<BR><BR><H3>Return/Cancel Requested!</H3>");
                out.println("<BR><BR>Thank you, the wait list entry has been returned to the system without changes.");
                out.println("<BR><BR>");

                out.println("<font size=\"2\">");
                out.println("<form action=\"/" +rev+ "/servlet/Member_jump\" method=\"post\" target=\"_top\">");
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

/*
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
*/
   //
   //  Prompt user to return to Member_sheet 
   //
   out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<CENTER><BR><BR><H1>Wait List Entry Busy</H1>");
   out.println("<BR><BR>Sorry, but this wait list entry is currently busy.<BR>");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/member_selmain.htm\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Return to Member_waitlist_slot
 // *********************************************************

 private void returnToSlot(PrintWriter out, parmSlot slotParms) {

   //
   //  Return to _slot to change the player order
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Member_waitlist_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
   out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + slotParms.wait_list_id + "\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
   out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
   out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
   out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
   out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
   out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
   out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
   out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
   out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
   out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
   out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
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
 //  Update players for a wait list entry
 // *********************************************************
 
 private void updateSignUpPlayers(int signup_id, String player_name, String old_player, String username, String cw, String oldcw, int p9hole, int oldp9hole, int pos, Connection con, PrintWriter out) {

    try {
        
        if ( player_name.equals("") && !old_player.equals("") ) {

            out.println("<!-- DELETING player"+pos+" | player_name=" + player_name + " | old_player=" + old_player + " -->");

            PreparedStatement pstmt = con.prepareStatement ("DELETE FROM wait_list_signups_players WHERE wait_list_signup_id = ? AND pos = ?");
            pstmt.setInt(1, signup_id);
            pstmt.setInt(2, pos);
            pstmt.executeUpdate();
            pstmt.close();

        } else if (!player_name.equals(old_player) || !cw.equals(oldcw) || p9hole != oldp9hole) {

            out.println("<!-- UPDATING player"+pos+" [" + username + " | " + player_name + " | " + cw + " | " + p9hole + "] -->");

            PreparedStatement pstmt = con.prepareStatement (
                "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                    "wait_list_signup_id = VALUES(wait_list_signup_id), " +
                    "username = VALUES(username), " +
                    "cw = VALUES(cw), " +
                    "player_name = VALUES(player_name), " +
                    "9hole = VALUES(9hole)");

            pstmt.clearParameters();
            pstmt.setInt(1, signup_id);
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
        
        dbError(out, e, "Error updating player" + pos + " info for wait list entry " + signup_id + ".");
    }
 }
 
 
}
