/***************************************************************************************
 *   Proshop_waitlist_slot:  This servlet will display and process the Wait List 
 *                          registration request form from a member
 *
 *
 *   Called by:     Proshop_sheet
 *                  self on cancel request
 *
 *
 *   Created:       4/19/2008
 *
 *   Last Updated:  
 *
 *        2/20/13   Oakmont CC (oakmont) - Commented out checkOakmontGuestQuota call as it is no longer used (case 1364).
 *        1/10/13   Undo the disabling of the guest db for iPad users
 *       11/27/12   Tweak iframe resize code
 *        3/29/12   Oakmont CC (oakmont) - Updated checkOakmontGuestQuota custom call to include May as well. (case 1364).
 *       10/06/11   Modified alphaTable.nameList() calls to pass an additional parameter used for displaying inact members when modifying past tee times.
 *       10/20/10   Populate new parmEmail fields
 *        9/14/10   Added guest tracking processing
 *        9/14/10   Fixed issue where movename and moveguest scripts would not populate the 5th player slot
 *        6/24/10   Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *        2/02/10   Trim the notes in verify
 *        9/17/09   Added support for the REST_OVERRIDE limited access proshop user field to block users without access from overriding restrictions.
 *        8/04/09   San Francisco GC (sfgc) - Set default Mode of Trans. to CAD for all guest types
 *        7/30/09   Add focus setting for moveguest js method to improve efficiency and disabled return key in player positions
 *       06/15/09   Add checkbox option and processing for email suppression
 *       10/03/08   Add a space after any guest names are put in a tee time slot (case 1551).
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
import com.foretees.common.Common_skin;
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
import com.foretees.common.alphaTable;
import com.foretees.common.medinahCustom;
import com.foretees.common.congressionalCustom;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;

public class Proshop_waitlist_slot extends HttpServlet {


   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

   static long Hdate1 = ProcessConstants.memDay;     // Memorial Day
   static long Hdate2 = ProcessConstants.july4;      // 4th of July - Monday
   static long Hdate2b = ProcessConstants.july4b;    // 4th of July - other
   static long Hdate3 = ProcessConstants.laborDay;   // Labor Day
   static long Hdate7 = ProcessConstants.tgDay;      // Thanksgiving Day
   static long Hdate8 = ProcessConstants.colDay;     // Columbus Day

   
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     doPost(req, resp);
     
 }

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

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

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
    String mshipOpt = (String)session.getAttribute("mshipOpt");
    String mtypeOpt = (String)session.getAttribute("mtypeOpt");

    if (mshipOpt.equals( "" ) || mshipOpt == null) mshipOpt = "ALL";
    if (mtypeOpt.equals( "" ) || mtypeOpt == null) mtypeOpt = "ALL";

    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();        // allocate a parm block
    slotParms.club = club;                        // save club name

    //
    // Process request according to which 'submit' button was selected
    //
    //      'time:fb' - a request from Member_sheet ** (NOT USED)
    //      'cancel'  - a cancel request from user via Proshop_waitlist_slot (return with no changes)
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
    int assign = 0;
    long mm = 0;
    long dd = 0;
    long yy = 0;
    long temp = 0;
    long date = 0;
    int fb = 0;
    //int x = 0;
    //int xCount = 0;
    int i = 0;
    int hide = 0;
    //int nowc = 0;
    //int lstate = 0;
    int p91 = 0;
    int p92 = 0;
    int p93 = 0;
    int p94 = 0;
    int p95 = 0;
    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;

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
    //String msg = "";
    //String slstate = "";
    //String pname = "";

    String suppressEmails = "no";
    String last_user = "";
    String last_name = "";
    String conf = "";
    String orig_by = "";
    String orig_at = "";
    String orig_name = "";

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
    parmClub parm = new parmClub(0, con);         // golf only feature

    //
    //  parm block to hold the course parameters
    //
    parmCourse parmc = new parmCourse();          // allocate a parm block

    boolean enableAdvAssist = Utilities.enableAdvAssist(req);

    //
    // Get all the parameters entered
    //
    String day_name = req.getParameter("day");       //  name of the day
    String index = req.getParameter("index");        //  index value of day (needed by Member_sheet when returning)
    String p5 = ""; //req.getParameter("p5");        //  5-somes supported
    String course = req.getParameter("course");      //  Name of Course
    if (course == null) course = "";

    String returnCourse = req.getParameter("returnCourse");      //  Name of the returnCourse
    if (returnCourse == null) returnCourse = "";
    
    if (req.getParameter("sdate") != null) {         // if date was passed in sdate
        sdate = req.getParameter("sdate");
    }

    if (req.getParameter("date") != null) {          // if date was passed in date
        sdate = req.getParameter("date");
    }

    if (req.getParameter("assign") != null) {     // if this is to assign members to guests

        assign = 1;                               // indicate 'assign members to guest' (Unaccompanied Guests)
    }

    if (req.getParameter("suppressEmails") != null) {
        suppressEmails = req.getParameter("suppressEmails");
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
    
    String list_name = req.getParameter("name");
    if (list_name == null) list_name = "";
    
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



    // if called without a list id then go get it, get date too
    if (signup_id != 0 && wait_list_id == 0) {
        /*
        out.println("<!-- calling getListIdBySignupId with signup_id " + signup_id + " -->");
        try {
            wait_list_id = getWaitList.getListIdBySignupId(signup_id, con);
        } catch (Exception e) {
            SystemUtils.logError("Error: getListIdBySignupId - club=" + club + " " + e.toString());
        }
        */
        
        try {

            PreparedStatement pstmt = con.prepareStatement("" +
                    "SELECT wait_list_id, DATE_FORMAT(date, '%Y%m%d') AS date1 " +
                    "FROM wait_list_signups " +
                    "WHERE wait_list_signup_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, signup_id);

            rs = pstmt.executeQuery();

            if (rs.next()) { 
                
                wait_list_id = rs.getInt(1);
                date = rs.getInt(2);
            }
            
            pstmt.close();

        } catch (Exception exc) { 

            out.println("Msg=" + exc.getMessage() + ", signup_id=" + signup_id);
        }
        
    }
    
    
    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
    
    parmWL.wait_list_id = wait_list_id;
    slotParms.wait_list_id = wait_list_id;                      // store it here for returning to _slot page
    
    try {
        
        getWaitList.getParms(con, parmWL);                      // get the wait list config
        
    } catch (Exception ignore) { }
    
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
    
    int ok_stime = 0;
    int ok_etime = 0;
    int shr = 0;
    int ehr = 0;
    int smin = 0;
    int emin = 0;
    
    out.println("<!-- parmWL.max_team_size=" + parmWL.max_team_size + " -->");
    out.println("<!-- waitListId=" + wait_list_id + " -->");

    // Set p5 = "Yes" if max_team_size is 5
    if (parmWL.max_team_size > 4) {
        p5 = "Yes";
    }
    
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

                out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" name=\"can\">");
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

        if (req.getParameter("guest_id1") != null) guest_id1 = Integer.parseInt(req.getParameter("guest_id1"));
        if (req.getParameter("guest_id2") != null) guest_id2 = Integer.parseInt(req.getParameter("guest_id2"));
        if (req.getParameter("guest_id3") != null) guest_id3 = Integer.parseInt(req.getParameter("guest_id3"));
        if (req.getParameter("guest_id4") != null) guest_id4 = Integer.parseInt(req.getParameter("guest_id4"));
        if (req.getParameter("guest_id5") != null) guest_id5 = Integer.parseInt(req.getParameter("guest_id5"));

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

        if (req.getParameter("mtypeopt") != null) {

            mtypeOpt = req.getParameter("mtypeopt");
            session.setAttribute("mtypeOpt", mtypeOpt);
        }
        if (req.getParameter("mshipopt") != null) {
            mshipOpt = req.getParameter("mshipopt");
            session.setAttribute("mshipOpt", mshipOpt);
        }
        
        //
        //  Convert hide from string to int
        //
        hide = 0;                       // init to No
        if (!hides.equals( "0" )) hide = 1;


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
                
                out.println("<!-- LOADED EXISTING SIGNUP - slotParms.ok_stime=" + slotParms.ok_stime + " | slotParms.ok_etime=" + slotParms.ok_etime + " -->");
            
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
        
        out.println("<!-- wait_list_id=" + wait_list_id + " | signup_id=" + signup_id + " | slotParms.players=" + slotParms.players + " | slotParms.course = " + slotParms.course + " | slotParms.date = " + slotParms.date + " -->");

        
        if (in_use != 0) {              // if time slot already in use
      
            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
            out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
            out.println("<BR>Please select another time or try again later.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            //
            //  Prompt user to return to Proshop_sheet or Proshop_searchmem (index = 888)
            //
            if (index.equals( "888" )) {       // if originated from Proshop_main
                out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            } else {
                if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                    course = returnCourse;
                }
                out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            }
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
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
        guest_id1 = slotParms.guest_id1;
        guest_id2 = slotParms.guest_id2;
        guest_id3 = slotParms.guest_id3;
        guest_id4 = slotParms.guest_id4;
        guest_id5 = slotParms.guest_id5;

        // if there is a player in pos 5, but 5-somes are blocked, then force an allow
        if (!player5.equals("") && p5.equalsIgnoreCase("No")) p5 = "Yes";
        
   } // end seeing what we are here to do

    /*
    // date is not passed in on edits so get it from parm
    if (date == 0) {
        date = slotParms.date;
        out.println("<!-- date not passed in - getting from slotParms.date=" + date + " -->");
    } else {
        out.println("<!-- date already defined - date=" + date + " -->");
    }
    */
    
    
    //
    //  Ensure that there are no null player fields
    //
    if (player1 == null ) player1 = "";
    if (player2 == null ) player2 = "";
    if (player3 == null ) player3 = "";
    if (player4 == null ) player4 = "";
    if (player5 == null ) player5 = "";

    if (p1cw == null ) p1cw = "";
    if (p2cw == null ) p2cw = "";
    if (p3cw == null ) p3cw = "";
    if (p4cw == null ) p4cw = "";
    if (p5cw == null ) p5cw = "";

    if (last_user == null ) last_user = "";
    if (notes == null ) notes = "";
    if (orig_by == null ) orig_by = "";
    if (orig_at == null ) orig_at = "";
    if (conf == null ) conf = "";
    
    notes = notes.trim();
    
    //
    //  Get the walk/cart options available and find the originators name 
    //
    PreparedStatement pstmtc = null;

    try {

        getParms.getTmodes(con, parmc, course);

        if (!orig_by.equals( "" )) {         // if originator exists (username of person originating)

            if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating)

                orig_name = orig_by;        // if proshop, just use the username

            } else {

                //
                //  Check member table and hotel table for match
                //
                orig_name = "";        // init
              
                pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi " +
                    "FROM member2b " +
                    "WHERE username = ?");

                pstmtc.clearParameters();        // clear the parms
                pstmtc.setString(1, orig_by);

                rs = pstmtc.executeQuery();

                if (rs.next()) {
              
                   // Get the member's full name.......

                   StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                   String mi = rs.getString(3);                                // middle initial
                   if (!mi.equals( "" )) {
                      mem_name.append(" ");
                      mem_name.append(mi);
                   }
                   mem_name.append(" " + rs.getString(1));                     // last name

                   orig_name = mem_name.toString();                          // convert to one string
                }
                pstmtc.close();
              
                if (orig_name.equals( "" )) {       // if match not found - check hotel user table

                   pstmtc = con.prepareStatement (
                      "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username = ?");

                   pstmtc.clearParameters();        // clear the parms
                   pstmtc.setString(1, orig_by);

                   rs = pstmtc.executeQuery();

                   if (rs.next()) {

                      // Get the member's full name.......

                      StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                      String mi = rs.getString(3);                                // middle initial
                      if (!mi.equals( "" )) {
                         mem_name.append(" ");
                         mem_name.append(mi);
                      }
                      mem_name.append(" " + rs.getString(1));                     // last name

                      orig_name = mem_name.toString();                          // convert to one string
                   }
                   pstmtc.close();
                }
            }
        }

        if (!last_user.equals( "" )) {         // if last_user exists (username of last person to change)

            if (last_user.startsWith( "proshop" )) {  // if originator exists (username of person originating)

                last_name = last_user;        // if proshop, just use the username

            } else {

                //
                //  Check member table and hotel table for match
                //
                last_name = "";        // init

                pstmtc = con.prepareStatement (
                   "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

                pstmtc.clearParameters();        // clear the parms
                pstmtc.setString(1, last_user);

                rs = pstmtc.executeQuery();

                if (rs.next()) {

                   // Get the member's full name.......

                   StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                   String mi = rs.getString(3);                                // middle initial
                   if (!mi.equals( "" )) {
                      mem_name.append(" ");
                      mem_name.append(mi);
                   }
                   mem_name.append(" " + rs.getString(1));                     // last name

                   last_name = mem_name.toString();                          // convert to one string
                }
                pstmtc.close();

                if (last_name.equals( "" )) {       // if match not found - check hotel user table

                   pstmtc = con.prepareStatement (
                      "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username = ?");

                   pstmtc.clearParameters();        // clear the parms
                   pstmtc.setString(1, last_user);

                   rs = pstmtc.executeQuery();

                   if (rs.next()) {

                      // Get the member's full name.......

                      StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                      String mi = rs.getString(3);                                // middle initial
                      if (!mi.equals( "" )) {
                         mem_name.append(" ");
                         mem_name.append(mi);
                      }
                      mem_name.append(" " + rs.getString(1));                     // last name

                      last_name = mem_name.toString();                          // convert to one string
                   }
                   pstmtc.close();
                }
            }
        }
        
    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, please contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        if (index.equals( "888" )) {       // if originated from Proshop_main
            out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
        } else {
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                course = returnCourse;
            }
            out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        }
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }


   //
   //  Build the HTML page to prompt user for names
   //
    boolean hybrid_slot = Utilities.checkProshopHybridSlot(con);
   
     if (hybrid_slot) {
         req.setAttribute(ProcessConstants.RQA_RWD, true); // Always uses RWD
         req.setAttribute(ProcessConstants.RQA_PROSHOP_HYBRID, true); // Always force Proshop Hybrid mode
         out.print(Common_skin.getHeader(club, 0, "Member Wait List Sign-up Page<", false, req));
     } else {
         out.println("<!DOCTYPE HTML>");
         out.println("<HTML>");
         out.println("<HEAD>");
         out.println("<link rel=\"stylesheet\" href=\"/" + rev + "/web%20utilities/foretees2.css\" type=\"text/css\">");
         out.println("<script type=\"text/javascript\" src=\"/" + rev + "/web%20utilities/foretees.js\"></script>");
         out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/jquery/jquery.js\"></script>");
         out.println("<title>Member Wait List Sign-up Page<</Title>");
     }

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

   out.println("var pPlayerPos2 = pPlayerPos.replace('player', 'guest_id');");
   out.println("document.playerform[pPlayerPos2].value = '0';");
   
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

   //
   //*********************************************************************************
   //  Move name script
   //*********************************************************************************
   //
   out.println("<script type='text/javascript'>");            // Move name script
   out.println("<!--");

   out.println("function movename(namewc) {");

   out.println("var del = ':';");                               // deliminator is a colon
   out.println("var array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
   out.println("var name = array[0];");
   out.println("var wc = array[1];");
   out.println("var f = document.forms['playerform'];");
   out.println("var skip = 0;");

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
         out.println("f.guest_id1.value = '0';");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("f.p1cw.value = wc;");
         out.println("}");
         
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
         if (parmWL.max_team_size > 1) {
         out.println("f.player2.value = name;");
         out.println("f.guest_id2.value = '0';");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
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
         out.println("f.guest_id3.value = '0';");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
            out.println("f.p3cw.value = wc;");
         out.println("}");
         } else {
            out.println("alert('All players slots have been filled.');");
         }
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
         if (parmWL.max_team_size > 3) {
         out.println("f.player4.value = name;");
         out.println("f.guest_id4.value = '0';");
         out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
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
            out.println("f.guest_id5.value = '0';");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
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

   out.println("var guestid_slot;");
   out.println("var player_slot;");

   out.println("function moveguest(namewc) {");

   out.println("var f = document.forms['playerform'];");
   //out.println("var name = namewc;");0

   out.println("var array = namewc.split('|');"); // split string (partner_name, partner_id)
   out.println("var name = array[0];");

   //if (enableAdvAssist) {
       out.println("var use_guestdb = array[1];");
   //} else {
   //    out.println("var use_guestdb = 0; // force to off on iPad");
   //}

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

   // If guest tracking is turned on and in use for this guest type and at least one player slot is open, display the modal window
   out.println("if (use_guestdb == 1 && " +
           "(player1 == ''" +
           (parmWL.max_team_size > 1 ? " || player2 == ''" : "") +
           ((!twoSomeOnly && parmWL.max_team_size > 2) ? " || player3 == ''" : "") +
           ((!twoSomeOnly && parmWL.max_team_size > 3) ? " || player4 == ''" : "") +
           ((!twoSomeOnly && p5.equals("Yes") && parmWL.max_team_size > 4) ? " || player5 == ''" : "") + ")) {");
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
         out.println("f.p1cw.value = defCW;");
      out.println("} else {");

      out.println("if (player2 == '') {");                    // if player2 is empty
      if (parmWL.max_team_size > 1) {
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player2;");
            out.println("guestid_slot = f.guest_id2;");
            out.println("f.player2.value = name + spc;");
         out.println("} else {");
             out.println("f.player2.focus();");
             out.println("f.player2.value = name + spc;");
             out.println("f.player2.focus();");
         out.println("}");
         out.println("f.p2cw.value = defCW;");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
   if (twoSomeOnly == false) {               // If tee time NOT restricted to 2-somes (custom requests)

      out.println("} else {");

      out.println("if (player3 == '') {");                    // if player3 is empty
      if (parmWL.max_team_size > 2) {
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player3;");
            out.println("guestid_slot = f.guest_id3;");
            out.println("f.player3.value = name + spc;");
         out.println("} else {");
             out.println("f.player3.focus();");
             out.println("f.player3.value = name + spc;");
             out.println("f.player3.focus();");
         out.println("}");
         out.println("f.p3cw.value = defCW;");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
      out.println("} else {");

      out.println("if (player4 == '') {");                    // if player4 is empty
      if (parmWL.max_team_size > 3) {
         out.println("if (use_guestdb == 1) {");
            out.println("player_slot = f.player4;");
            out.println("guestid_slot = f.guest_id4;");
            out.println("f.player4.value = name + spc;");
         out.println("} else {");
             out.println("f.player4.focus();");
             out.println("f.player4.value = name + spc;");
             out.println("f.player4.focus();");
         out.println("}");
         out.println("f.p4cw.value = defCW;");
      } else {
         out.println("alert('All players slots have been filled.');");
      }
      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
         if (parmWL.max_team_size > 4) {
            out.println("if (use_guestdb == 1) {");
               out.println("player_slot = f.player5;");
               out.println("guestid_slot = f.guest_id5;");
               out.println("f.player5.value = name + spc;");
            out.println("} else {");
               out.println("f.player5.focus();");
               out.println("f.player5.value = name + spc;");
               out.println("f.player5.focus();");
            out.println("}");
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
   out.println("</script>");                               // End of script
   //*******************************************************************************************

   out.println("</HEAD>");
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#000000\" vlink=\"#000000\" alink=\"#000000\">");
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
     out.println("<font color=\"#ffffff\" size=\"5\">Wait List Registration</font>");
     out.println("</font></td>");

     out.println("<td align=\"center\" width=\"160\">");
     out.println("<font size=\"1\" color=\"#ffffff\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#ffffff\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#ffffff\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table width=\"100%\" border=\"0\" align=\"center\">");          // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
            out.println("<font size=\"2\" color=\"darkred\">");
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
            if (signup_id == 0) {
                out.println("<form action=\"Proshop_jump\" method=\"post\" name=\"can\">");
                out.println("<input type=\"hidden\" name=\"waitList\">");
            } else {
                out.println("<form action=\"Proshop_waitlist_slot\" method=\"POST\" name=\"can\">");
            }
            out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
            out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + list_name + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
            out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("Return<br>w/o Changes:<br>");
            out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
            out.println("<br><br>");
            out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" name=\"can\">");
            out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
            out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
            out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
            out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
            out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
            out.println("Jump to Tee Sheet:<br>");
            out.println("<input type=\"submit\" value=\"Tee Sheet\" name=\"cancel\"></form>");

         out.println("</td>");

         out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" name=\"playerform\" id=\"playerform\">");

            out.println("<input type=\"hidden\" name=\"signupId\" value=" + signup_id + ">");
            out.println("<input type=\"hidden\" name=\"waitListId\" value=" + wait_list_id + ">");
            
         out.println("<td align=\"center\" valign=\"top\">");

          out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"370\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#ffffff\" size=\"2\">");
               out.println("<b>Add or Remove Players</b>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");

               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes</b><br>");


               // Print hidden guest_id inputs
               out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + guest_id1 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + guest_id2 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + guest_id3 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + guest_id4 + "\">");
               out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + guest_id5 + "\">");

            out.println("<img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasename('player1', 'p1cw')\" style=\"cursor:hand\">");
            out.println("1:&nbsp;<input type=\"text\" id=\"player1\" name=\"player1\" value=\""+player1+"\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
            out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\" id=\"p1cw\">");

            out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
            for (i=0; i<16; i++) {        // get all c/w options

                if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                }
            }
            out.println("</select>");
            out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p91 == 1) ? "checked" : "") + " name=\"p91\" value=\"1\">");
                 
            if (parmWL.max_team_size > 1) {

                out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2', 'p2cw')\" style=\"cursor:hand\">");
                out.println("2:&nbsp;<input type=\"text\" id=\"player2\" name=\"player2\" value=\""+player2+"\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\" id=\"p2cw\">");

                out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
                for (i=0; i<16; i++) {

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
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
                out.println("3:&nbsp;<input type=\"text\" id=\"player3\" name=\"player3\" value=\""+player3+"\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\" id=\"p3cw\">");

                out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
                for (i=0; i<16; i++) {         // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw )) {
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
                out.println("4:&nbsp;<input type=\"text\" id=\"player4\" name=\"player4\" value=\""+player4+"\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\" id=\"p4cw\">");

                out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
                for (i=0; i<16; i++) {       // get all c/w options

                    if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
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
                out.println("5:&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\""+player5+"\" size=\"20\" maxlength=\"30\" onkeypress=\"return DYN_disableEnterKey(event)\">");
                out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");

                out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
                for (i=0; i<16; i++) {      // get all c/w options

                   if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
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

                  out.println("<br>");
                  out.println("<b>Contact Info / Notes</b>&nbsp; &nbsp;<br><img src=\"/" +rev+ "/images/erase.gif\" onclick=\"erasetext('notes')\" style=\"cursor:hand\">");
                  out.println("<textarea name=\"notes\" id=\"notes\" cols=\"32\" rows=\"2\">" + notes + "</textarea>");
               }
               
               out.println("<input type=\"hidden\" name=\"name\" value=\"" + list_name + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day_name + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               //out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + hide + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");

               out.println("<br><font size=\"1\">");
               for (i=0; i<16; i++) {
                  if (!parmc.tmodea[i].equals( "" )) {
                     out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
                  }
               }
               out.println("</font><br>");

               // display the Suppress Email Notifications checkbox
               out.println("Suppress email notification?:&nbsp;&nbsp; ");
               if (suppressEmails.equalsIgnoreCase( "yes" )) {
                  out.println("<input type=\"checkbox\" checked name=\"suppressEmails\" value=\"Yes\">");
               } else {
                  out.println("<input type=\"checkbox\" name=\"suppressEmails\" value=\"Yes\">");
               }
               out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\"><br><br>");


               // display the cancel button if we're here editing an existing signup'
               if (signup_id != 0) out.println("<input type=submit value=\"Cancel Sign-up\" name=\"remove\" onclick=\"return confirm('Are you sure you want to remove this wait list entry?')\">&nbsp;&nbsp;&nbsp;");

               out.println("<input type=submit value=\"" + ((!player1.equals("")) ? "Submit Changes" : "Submit") + "\" name=\"submitForm\">");

               out.println("</font></td></tr>");
               out.println("</table>");


         out.println("</td>");                                // end of table and column
         out.println("<td align=\"center\" valign=\"top\">");
         
         if (hybrid_slot) {
         
             // Use RWD member selection
             out.print("<td><div class=\"hybridSlotMemberSelect slotMemberSelect\"></div><div class=\"hybridSlotMemberOther\">");
             out.print("<div class=\"hybridSlotTbdGuestSelect\">");
             out.print("<script type=\"text/javascript\" src=\"/v5/dyn-search-20130131.js\"></script>"); // Proshop Slots seem to need this.
             //
             //   Output the List of Guests
             //
             if (parmWL.allow_guests > 0) {
                alphaTable.guestList(club, course, day_name, time, parm, false, false, 0, enableAdvAssist, out, con);
             }
             out.print("</div></td>");

         } else {

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
               out.println("<td valign=\"top\">");                        // add column for member list table


               //
               //   Output the Alphabit Table for Members' Last Names 
               //
               alphaTable.getTable(out, user);


               //
               //   Output the Mship and Mtype Options
               //
               alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);


               //
               //   Output the List of Guests if they're allowed
               //
               if (parmWL.allow_guests > 0) {
                   alphaTable.guestList(club, course, day_name, time, parm, false, false, 0, enableAdvAssist, out, con);
               }
   
         }

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
 //  Process reservation request from Proshop_waitlist_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


    Statement stmt = null;
    Statement estmt = null;
    Statement stmtN = null;
    ResultSet rs = null;
    ResultSet rs7 = null;

    //
    //  Get this session's user name
    //
    String user = (String)session.getAttribute("user");
    String fullName = (String)session.getAttribute("name");
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
    int thisTime = 0;
    int time = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int fb = 0;
    //int fb2 = 0;
    //int t_fb = 0;
    int x = 0;
    int xhrs = 0;
    //int calYear = 0;
    //int calMonth = 0;
    //int calDay = 0;
    //int calHr = 0;
    //int calMin = 0;
    //int memNew = 0;
    //int memMod = 0;
    //int i = 0;
    int ind = 0;
    //int xcount = 0;
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
    //int adv_time = 0;
    int ok_stime = 0;
    int ok_etime = 0;
    int shr = 0;
    int ehr = 0;
    int smin = 0;
    int emin = 0;
    int skip = 0;
    int thisMonth = 0;
    
    long temp = 0;
    long ldd = 0;
    long date = 0;
    //long adv_date = 0;
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
    //String p1 = "";
    String suppressEmails = "no";
    //String sponsored = "Spons";
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    if (returnCourse == null) returnCourse = "";
    String err_name = "";
    String skips = "";
    
    boolean error = false;
    //boolean guestError = false;
    boolean oakskip = false;
    boolean busy = false;

    boolean overrideAccess = SystemUtils.verifyProAccess(req, "REST_OVERRIDE", con, out);

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)
    
    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub(0, con);         // golf only feature

    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block
    
    //
    //  parm block to hold the wait list parameters
    //
    parmWaitList parmWL = new parmWaitList();                   // allocate a parm block
    
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
    //  Get skip parm if provided
    //
    if (req.getParameter("skip") != null) {

        skips = req.getParameter("skip");
        skip = Integer.parseInt(skips);
    }
    
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
    slotParms.guest_id1 = Integer.parseInt(req.getParameter("guest_id1"));
    slotParms.guest_id2 = Integer.parseInt(req.getParameter("guest_id2"));
    slotParms.guest_id3 = Integer.parseInt(req.getParameter("guest_id3"));
    slotParms.guest_id4 = Integer.parseInt(req.getParameter("guest_id4"));
    slotParms.guest_id5 = Integer.parseInt(req.getParameter("guest_id5"));
    slotParms.day = req.getParameter("day");            // name of day
    slotParms.notes = req.getParameter("notes").trim(); // Notes
    slotParms.hides = req.getParameter("hide");         // Hide Notes
    
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

    if (req.getParameter("suppressEmails") != null) {
        suppressEmails = req.getParameter("suppressEmails");
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


    //
    //  Convert date & time from string to int
    //
    try {
        
        date = Long.parseLong(sdate);
        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        time = Integer.parseInt(stime);
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
    slotParms.suppressEmails = suppressEmails;

    out.println("<!-- BEFORE: slotParms.time=" + slotParms.time + " -->");
    
    if (slotParms.time == 0) slotParms.time = slotParms.ok_stime;
    
    out.println("<!-- AFTER: slotParms.time=" + slotParms.time + " -->");
    
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
                     "  UNIX_TIMESTAMP(in_use_at) < ( UNIX_TIMESTAMP() + (6 * 60) ) " +
                     ")");
                
            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, signup_id);
            pstmt.setString(2, user);
            rs = pstmt.executeQuery();      // execute the prepared stmt
            
            if (rs.next()) {

                //slotParms.req_datetime = rs.getString( "req_datetime" );
                //slotParms.course_id = rs.getInt( "course_id" );
                slotParms.last_user = rs.getString( "in_use_by" );
                //slotParms.hideNotes = rs.getInt( "hideNotes" );
                //slotParms.notes = rs.getString( "notes" );
                slotParms.converted = rs.getInt( "converted" );

            } else {
                
                busy = true;
                
            }
            pstmt.close();

            // bomb out if we lost the signup
            if (busy) {
                
                out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
                out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<CENTER><BR><BR><H1>Wait List Entry Busy</H1>");
                out.println("<BR><BR>Sorry, but this wait list entry is currently busy.<BR>");
                out.println("<BR>Please try again later.");
                out.println("<BR><BR>");
                out.println("<font size=\"2\">");
                out.println("<form method=\"get\" action=\"Proshop_announce\">");
                out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");
                out.println("</CENTER></BODY></HTML>");
                out.close();
            }
            
            
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
                slotParms.oldguest_id1 = rs.getInt("guest_id");
                slotParms.players = 1;
            }

            if (rs.next()) {

                slotParms.oldPlayer2 = rs.getString( "player_name" );
                slotParms.oldUser2 = rs.getString( "username" );
                slotParms.oldp2cw = rs.getString( "cw" );
                slotParms.oldp92 = rs.getInt( "9hole" );
                slotParms.oldguest_id2 = rs.getInt("guest_id");
                slotParms.players = 2;
            }

            if (rs.next()) {

                slotParms.oldPlayer3 = rs.getString( "player_name" );
                slotParms.oldUser3 = rs.getString( "username" );
                slotParms.oldp3cw = rs.getString( "cw" );
                slotParms.oldp93 = rs.getInt( "9hole" );
                slotParms.oldguest_id3 = rs.getInt("guest_id");
                slotParms.players = 3;
            }

            if (rs.next()) {

                slotParms.oldPlayer4 = rs.getString( "player_name" );
                slotParms.oldUser4 = rs.getString( "username" );
                slotParms.oldp4cw = rs.getString( "cw" );
                slotParms.oldp94 = rs.getInt( "9hole" );
                slotParms.oldguest_id4 = rs.getInt("guest_id");
                slotParms.players = 4;
            }
                
            if (rs.next()) {

                slotParms.oldPlayer5 = rs.getString( "player_name" );
                slotParms.oldUser5 = rs.getString( "username" );
                slotParms.oldp5cw = rs.getString( "cw" );
                slotParms.oldp95 = rs.getInt( "9hole" );
                slotParms.oldguest_id5 = rs.getInt("guest_id");
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

        // store the signup_id for use in verification (done above now)
        //slotParms.wait_list_signup_id = signup_id;
        
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
      //  Make sure at least 1 player contains a name
      //
      if ((slotParms.player1.equals( "" )) && (slotParms.player2.equals( "" )) && (slotParms.player3.equals( "" )) && (slotParms.player4.equals( "" )) && (slotParms.player5.equals( "" ))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
         out.println("<BR>If you wish to remove all names from this slot, use the 'Cancel Tee Time' button.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         returnToSlot(out, slotParms);
         return;

      }

      //
      //  At least 1 Player field is present - Make sure a C/W was specified for all players
      //
      if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.p1cw.equals( "" ))) ||
          ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.p2cw.equals( "" ))) ||
          ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.p3cw.equals( "" ))) ||
          ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.p4cw.equals( "" ))) ||
          ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.p5cw.equals( "" )))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         returnToSlot(out, slotParms);
         return;
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
            msg = "Error parsing guests. " + e.toString();
            dbError(out, e, msg);
        }

        //
        //  Reject if any player is a guest type that uses the guest tracking system, but the guest_id is blank or doesn't match the guest name entered
        //
        if (!slotParms.gplayer.equals( "" ) && slotParms.hit4 == true) {                      // if error was name doesn't match guest_id

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR><b>" + slotParms.gplayer + "</b> appears to have been manually enetered or " +
                    "<br>modified after selecting a different guest from the Guest Selection window.");
            out.println("<BR><BR>Since this guest type uses the Guest Tracking feature, please click 'erase' ");
            out.println("<BR>next to the current guest's name, then click the desired guest type from the Guest ");
            out.println("<BR>Types list, and finally select a guest from the displayed guest selection window.");

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
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotParms);
            return;
         }
        
        
        

      //
      //  Parse the names to separate first, last & mi
      //
      try {

         error = verifySlot.parseNames(slotParms, "pro");

      }
      catch (Exception ignore) {
      }

      if ( error == true && skip == 0) {          // if problem

         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Invalid Data Received</H3><BR>");
         out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
         out.println("The player <b>" +slotParms.player+ "</b> is not a guest type, an X, or a valid member name.");

         //
         //  Return to _slot to change the player order
         //
         returnToSlot(out, slotParms, overrideAccess, "", 19);                // use unique skip id for this!!!!!!!!!!
         return;
      }

      if (skip == 19) {
        
         skip = 0;             // reset to start over !!!!!!!!!!!!
      }
        
 
      //
      //  Get the usernames, membership types and hndcp's for players if matching name found
      //
      try {

         verifySlot.getUsers(slotParms, con);

      }
      catch (Exception ignore) {
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
          out.println("<BR><BR>If you do not see this player on the wait list, it is because their signup ");
          out.println("<BR>has already been converted to a tee time.");
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
      //  Check if proshop user requested that we skip the following name test.
      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         int invalNum = 0;
         err_name = "";
           
         //
         //  Check if any of the names are invalid.  If so, ask proshop if they want to ignore the error.
         //
         if (slotParms.inval5 != 0) {

            err_name = slotParms.player5;
            invalNum = slotParms.inval5;
         }

         if (slotParms.inval4 != 0) {

            err_name = slotParms.player4;
            invalNum = slotParms.inval4;
         }

         if (slotParms.inval3 != 0) {

            err_name = slotParms.player3;
            invalNum = slotParms.inval3;
         }

         if (slotParms.inval2 != 0) {

            err_name = slotParms.player2;
            invalNum = slotParms.inval2;
         }

         if (slotParms.inval1 != 0) {

            err_name = slotParms.player1;
            invalNum = slotParms.inval1;
         }

         if (!err_name.equals( "" )) {      // invalid name received

            out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
              
            if (invalNum == 2) {        // if incomplete member record

               out.println("<BR><H3>Incomplete Member Record</H3><BR>");
               out.println("<BR><BR>Sorry, a member you entered has an imcomplete member record and cannot be included at this time.<BR>");
               out.println("<BR>Member Name:&nbsp;&nbsp;&nbsp;'" +err_name+ "'");
               out.println("<BR><BR>Please update this member's record via Admin and complete the required fields.");
               out.println("<BR><BR>You will have to remove this name from your tee time request.");
               out.println("<BR><BR>");

            } else {
  
               out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
               out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");
            }
  
            boolean allowOverride = true;

            if (invalNum == 2 || !overrideAccess) {
                allowOverride = false;
            }
            
            returnToSlot(out, slotParms, allowOverride, user, 1);
            
            return;
            
/*         
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
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
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
              
            if (invalNum == 2) {        // if incomplete member record

               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

            } else {
              
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
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
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
               out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\"></form>");
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
*/
         } // end if err_name empty
         
      } // end of skip < 1

      //
      //  Check if proshop user requested that we skip the mship test (member exceeded max and proshop
      //  wants to override the violation).
      //
      //  If this skip, or any of the following skips are set, then we've already been through these tests.
      //
      if (skip < 2) {

         //
         //************************************************************************
         //  No, normal request -
         //  Check any membership types for max rounds per week, month or year
         //************************************************************************
         //
         if ((!slotParms.mship1.equals( "" )) ||
             (!slotParms.mship2.equals( "" )) ||
             (!slotParms.mship3.equals( "" )) ||
             (!slotParms.mship4.equals( "" )) ||
             (!slotParms.mship5.equals( "" ))) {   // if at least one name exists then check number of rounds

            error = false;                             // init error indicator

            try { 
              
               error = verifySlot.checkMaxRounds(slotParms, con);

               if (error == false) {       // if ok, check for Hazeltine special processing

                  //
                  //  If Hazeltine National, then check for National Memberships - max rounds
                  //
                  if (club.equals( "hazeltine" )) {      // if Hazeltine National

                     error = verifySlot.checkNational(slotParms, con);  // check for max rounds for National mships
                  }
                  //
                  //  If Portage CC, then process any Associate Memberships (2 rounds per month, 6 per year)
                  //
                  if (club.equals( "portage" )) {      // if Portage

                     error = verifySlot.checkPortage(slotParms, con);  // check for Associate mships
                  }
                  
                  //
                  //  Custom for Eagle Creek - check to see if Social members have exceeded their # of rounds in season (Case #1284)
                  //    - and make sure Socials are accompanied by a Golf mship
                  //
                  if (club.equals( "eaglecreek" )) {

                      // now check to see if this member is Social and if so they must be accompanied by a Golf mship
                      int tmp_yy = (int)slotParms.date / 10000;         // get year
                      int tmp_sdate = (yy * 10000) + 1101;               // yyyy1101
                      int tmp_edate = ((yy + 1) * 10000) + 430;        // yyyy0430

                      //
                      //  Only check quota if tee time is within the Golf Year
                      //
                      if (slotParms.date > tmp_sdate && slotParms.date < tmp_edate) {

                          if (slotParms.mship1.equals("Social") || slotParms.mship2.equals("Social") || slotParms.mship3.equals("Social") || 
                                  slotParms.mship4.equals("Social") || slotParms.mship5.equals("Social") ) {

                              // at least one player is a Social member now check for golf mship
                              if (slotParms.mship1.equals("Golf") || slotParms.mship2.equals("Golf") || slotParms.mship3.equals("Golf") || 
                                  slotParms.mship4.equals("Golf") || slotParms.mship5.equals("Golf") ) {

                                  // ok because we found a Golf mship
                                  // but now lets see if the Social members are over their allowed limit
                                  error = verifyCustom.checkEagleCreekSocial(slotParms, con);

                                  if (error == true) {

                                      out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                                      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                                      out.println("<hr width=\"40%\">");
                                      out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
                                      out.println("<BR><BR>Sorry, " + slotParms.player + " is a Social member and has exceeded the<BR>");
                                      out.println("maximum number of tee times allowed for this season (November 1 thru April 30).");
                                      returnToSlot(out, slotParms, overrideAccess, "", 12);
                                      return;
                                  }

                              } else {
                                  
                                  // we didn't find a Golf mship so disallow
                                  out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                                  out.println("<hr width=\"40%\">");
                                  out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
                                  out.println("<BR><BR>Sorry, members with Social memberships must be accompanied by a member with<BR>");
                                  out.println("a Golf membership classification from November 1 thru April 30.");
                                  returnToSlot(out, slotParms, overrideAccess, "", 12);
                                  return;
                              } // end if golf mship check
                              
                          } // end if social mship found
                      } // end date range check   
                  } // end eaglecreek custom
                  
                  //
                  //  Custom for Mediterra CC - check to see if Sports members have exceeded their # of rounds in season (case #1262)
                  //    - keep this after all other standard verifications because it will trigger an email to pro if sports member exceed their rounds
                  //
                  if (club.equals( "mediterra" )) {

                      error = verifyCustom.checkMediterraSports(slotParms, con);

                      if (error == true) {

                          out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                          out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                          out.println("<hr width=\"40%\">");
                          out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
                          out.println("<BR><BR>Sorry, " + slotParms.player + " is a Sports member and has exceeded the<BR>");
                          out.println("maximum number of tee times allowed for this season.");
                          returnToSlot(out, slotParms, overrideAccess, "", 12);
                          return;
                      }
                  }
                  
               }
            }
            catch (Exception e2) {
               String errorMsgX = "Check for Max Rounds (Proshop_waitlist_slot): exception=" +e2;
               SystemUtils.logError(errorMsgX);        // log the error message
            }

            if (error == true) {      // a member exceed the max allowed tee times per week, month or year

               out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Warning:  " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>");
               out.println("maximum number of tee times allowed for this " + slotParms.period + ".");
               //
               //  Return to _slot to change the player order
               //
               returnToSlot(out, slotParms, overrideAccess, "", 2);
               return;
            }
         }      // end of mship if
      }         // end of skip2 if

      //
      //  Check if proshop user requested that we skip the max # of guests test
      //
      //  If this skip, or any of the following skips are set, then we've already been through these tests.
      //
      if (skip < 3) {

         //
         //************************************************************************
         //  Check for max # of guests exceeded (per Member or per Tee Time)
         //************************************************************************
         //
         if (slotParms.guests != 0) {      // if any guests were included

            error = false;                             // init error indicator

            try {

               error = verifySlot.checkMaxGuests(slotParms, con);

            }
            catch (Exception e5) {

               dbError(out, e5);
               return;
            }

            if (error == true) {      // a member exceed the max allowed guests

                  boolean allowOverride = true;

                  out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Number of Guests Exceeded Limit</H3>");
                  out.println("<BR>Sorry, the maximum number of guests allowed for the<BR>");
                  out.println("time you are requesting is " +slotParms.grest_num+ " per " +slotParms.grest_per+ ".");
                  out.println("<BR>You have requested " +slotParms.guests+ " guests and " +slotParms.members+ " members.");
                  out.println("<BR><BR>Restriction Name = " +slotParms.rest_name);

                  if (!overrideAccess || (club.equals("lakes") && !user.equalsIgnoreCase("proshop5"))) {  // if Lakes and NOT head pro
                      allowOverride = false;
                  }
                  
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, allowOverride, user, 3);
                      
                  return;
/*
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"Proshop_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
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
                  out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
                  out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
                  out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
                  out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
                  out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                  if (club.equals("lakes") && !user.equalsIgnoreCase("proshop5")) {  // if Lakes and NOT head pro
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  } else {       // not Lakes or is head pro
                     out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("</form>");

                     out.println("<form action=\"Proshop_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
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
                     out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
                     out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
                     out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
                     out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
                     out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
                     out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
                     out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                     out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                     out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                     out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                     out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                     out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                     out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                     out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\">");
                  }
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
 */
            }
              
            if (club.equals( "oakmont" )) {      // if Oakmont CC

               if (slotParms.guests > 1) {       // if 2 or more guests and Oakmont CC

                  //
                  // **********************************************************************
                  //  Oakmont - Check for max # of family guest tee times exceeded
                  // **********************************************************************
                  //
                  error = false;                             // init error indicator

                  try {

                     error = verifySlot.oakmontGuests(slotParms, con);

                  }
                  catch (Exception e5) {

                     dbError(out, e5);
                     return;
                  }

                  if (error == true) {      // a member exceed the max allowed tee times per month

                     out.println(SystemUtils.HeadTitle("Max Num Guest Tee Times Exceeded - Reject"));
                     out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");

                     out.println("<BR><H3>Number of Family Guest Tee Times Exceeded Limit For The Day</H3><BR>");
                     out.println("<BR><BR>Sorry, there are already 2 tee times with family guests<BR>");
                     out.println("scheduled for today.  You are allowed one family guest per member.");
                     //
                     //  Return to _slot to change the player order
                     //
                     returnToSlot(out, slotParms, overrideAccess, "", 3);
                     return;
                  }
               }
            }
              
            //
            //  If Green Bay, then check if more than 9 guests per hour
            //
            if (club.equals( "greenbay" )) {           // if Green Bay CC

               error = verifySlot.checkGBguests(slotParms, con);

               if (error == true) {      // more than 9 guests this hour

                  out.println(SystemUtils.HeadTitle("Max Number of Guests Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");

                  out.println("<BR><BR><H3>Maximum Number of Guests Exceeded</H3>");
                  out.println("<BR>Sorry, but there are already guests scheduled during this hour.");
                  out.println("<BR>No more than 9 guests are allowed per hour.  This request would exceed that total.");
                  out.println("<BR><BR>Please remove one or more guests, or try another time of the day.");

                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }      // end of if Green Bay CC
 
            //
            //  If Riverside G&CC & Sunday before Noon, then check if more than 12 guests total
            //
            if (club.equals( "riverside" ) && slotParms.day.equals( "Sunday" ) && slotParms.time < 1200) {  // if Riverside, Sunday & < Noon

               error = verifyCustom.checkRSguests(slotParms, con);

               if (error == true) {      // more than 12 guests before noon

                  out.println(SystemUtils.HeadTitle("Max Number of Guests Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Maximum Number of Guests Exceeded</H3>");
                  out.println("<BR>Sorry, but there are already 12 guests scheduled today.");
                  out.println("<BR>No more than 12 guests are allowed before Noon.  This request would exceed that total.");
                  out.println("<BR><BR>Please remove one or more guests, or try another time of the day.");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }      // end of if Riverside

            //
            //  If Wilmington CC then check if more than 12 guests total
            //
            if (club.equals( "wilmington" )) {

               error = verifyCustom.checkWilmingtonGuests(slotParms, con);

               if (error == true) {      // more than 12 guests

                  out.println(SystemUtils.HeadTitle("Max Number of Guests Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Maximum Number of Guests Exceeded</H3>");
                  out.println("<BR>Sorry, but there are already 12 guests scheduled today.");
                  out.println("<BR>No more than 12 guests are allowed during the selected time period.  This request would exceed that total.");
                  out.println("<BR><BR>Please remove one or more guests, or try another time of the day.");

                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }      // end of if Wilmington

            //
            //  If Merion, then check for max guest times per hour
            //
            if (club.equals( "merion" ) && slotParms.course.equals( "East" ) &&
                (slotParms.day.equals( "Saturday" ) || slotParms.day.equals( "Sunday" ))) {   // Merion, East course, and a w/e

               error = verifySlot.checkMerionG(slotParms, con);

               if (error == true) {      // max guest times exceeded this hour

                  out.println(SystemUtils.HeadTitle("Max Number of Guest Times Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Maximum Number of Guest Times Exceeded</H3>");
                  out.println("<BR>Sorry, but the maximum number of guest times are already scheduled during this hour.");
                  out.println("<BR><BR>Please try another time of the day.");
             
                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }      // end of if Merion


            //
            //  If Congressional, then check for 'Cert Jr Guest' types - must only follow a Certified Dependent
            //
            if (club.equals( "congressional" )) {           // if congressional

               error = congressionalCustom.checkCertGuests(slotParms);

               if (error == true) {      // no guests allowed

                  out.println(SystemUtils.HeadTitle("Guest Restriction - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guest Type Not Allowed</H3>");
                  out.println("<BR>Sorry, but the guest type 'Cert Jr Guest' can only follow a Certified Dependent");
                  out.println("<BR>and a dependent may only have one guest.");

                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }      // end of if Congressional


            //
            //  If Bearpath, then check for member-only time
            //
            if (club.equals( "bearpath" )) {           // if Bearpath

               error = verifySlot.checkBearpathGuests(slotParms.day, slotParms.date, slotParms.time, slotParms.ind);

               if (error == true) {      // no guests allowed

                  out.println(SystemUtils.HeadTitle("Max Number of Guest Times Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guests Not Allowed</H3>");
                  out.println("<BR>Sorry, but guests are not allowed during this time.  This is a member-only time.");
                  out.println("<BR><BR>Please try another time of the day.");
                
                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }      // end of if Bearpath

         }      // end of if guests


         //
         //  If Congressional, then check for Dependent w/o an adult
         //
         if (club.equals( "congressional" )) {           // if congressional

            error = false;

            //
            //  Check if any 'Dependent Non-Certified' mtypes are in the request
            //
            if (slotParms.mtype1.equals( "Dependent Non-Certified" ) || slotParms.mtype2.equals( "Dependent Non-Certified" ) || slotParms.mtype3.equals( "Dependent Non-Certified" ) ||
                slotParms.mtype4.equals( "Dependent Non-Certified" ) || slotParms.mtype5.equals( "Dependent Non-Certified" )) {

               error = true;     // default = error

               //
               //  Now check if any Adults
               //
               if (slotParms.mtype1.startsWith( "Primary" ) || slotParms.mtype2.startsWith( "Primary" ) || slotParms.mtype3.startsWith( "Primary" ) ||
                   slotParms.mtype4.startsWith( "Primary" ) || slotParms.mtype5.startsWith( "Primary" ) ||
                   slotParms.mtype1.startsWith( "Spouse" ) || slotParms.mtype2.startsWith( "Spouse" ) || slotParms.mtype3.startsWith( "Spouse" ) ||
                   slotParms.mtype4.startsWith( "Spouse" ) || slotParms.mtype5.startsWith( "Spouse" )) {

                  error = false;     // ok if adult included
               }
            }

            if (error == true) {      // if dependent w/o an adult

               out.println(SystemUtils.HeadTitle("Member Error - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Dependent Without An Adult</H3>");
               out.println("<BR>Sorry, but a Non-Certified Dependent is not allowed when an adult is not included.");
               out.println("<BR><BR>Please remove the dependent or add an adult.");

               returnToSlot(out, slotParms, overrideAccess, "", 3);
               return;
            }

            //
            //  Now check for special guest times (case #1075)
            //
            if (slotParms.course.equals( "Open Course" )) {    // Open Course and more than 9 days in advance

               thisTime = SystemUtils.getTime(con);               // get the current adjusted time

               int congTime = 1700;                               // default

               if (slotParms.mship1.startsWith( "Beneficiary" ) || slotParms.mship1.startsWith( "Honorar" ) || slotParms.mship1.equals( "Resident Active" ) ||
                   slotParms.mship1.equals( "Resident Twenty" )) {

                  congTime = 1500;
               }

               if (ind > 9 || (ind == 9 && thisTime < congTime)) {     // if a special guest time

                  //
                  //  Must be at least one 'Non Local Guest' in the group
                  //
                  if (slotParms.player2.startsWith( "Non Local Guest" ) || slotParms.player3.startsWith( "Non Local Guest" ) ||
                      slotParms.player4.startsWith( "Non Local Guest" ) || slotParms.player5.startsWith( "Non Local Guest" )) {

                     oakskip = true;     // ok to skip the 'days in advance' test

                     //
                     //  Make sure this member does not have too many advance times already scheduled
                     //
                     error = congressionalCustom.checkAdvTimes(slotParms, con);

                     if (error == true) {

                        out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                        out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                        out.println("<hr width=\"40%\">");
                        out.println("<BR><H3>Invalid Guest Time Request</H3><BR>");
                        out.println("<BR><BR>Sorry, " +slotParms.player+ " already has 4 advance guest times scheduled this year.<BR>");

                        returnToSlot(out, slotParms, overrideAccess, "", 3);
                        return;
                     }

                  } else {   // not a valid Guest Time

                     out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                     out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><H3>Invalid Guest Time Request</H3><BR>");
                     out.println("<BR><BR>Sorry, you must include at least one Non Local Guest in the group<BR>");
                     out.println("when making a tee time this far in advance.");

                     returnToSlot(out, slotParms, overrideAccess, "", 3);
                     return;
                  }
               }
            }
         }     // end of IF Congressional


      //
      //  If Colleton River Club, then check for Dependent w/o an adult  (Case #1291)
      //
      if (club.equals( "colletonriverclub" )) {

         //
         //  Check if any 'Dependents' mtypes are in the request
         //
         if (slotParms.mtype1.equals( "Dependents" ) || slotParms.mtype2.equals( "Dependents" ) || slotParms.mtype3.equals( "Dependents" ) ||
             slotParms.mtype4.equals( "Dependents" ) || slotParms.mtype5.equals( "Dependents" )) {

            error = true;     // default = error

            //
            //  Now check if any Adults
            //
            if (slotParms.mtype1.startsWith( "Adult" ) || slotParms.mtype2.startsWith( "Adult" ) || slotParms.mtype3.startsWith( "Adult" ) ||
                slotParms.mtype4.startsWith( "Adult" ) || slotParms.mtype5.startsWith( "Adult" )) {

               error = false;     // ok if adult included
            }
         }

         if (error == true) {      // if dependent w/o an adult

            out.println(SystemUtils.HeadTitle("Member Error - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Dependent Without An Adult</H3>");
            out.println("<BR>Sorry, but a Dependent is not allowed when an adult is not included.");

            returnToSlot(out, slotParms, overrideAccess, "", 3);
            return;
         }
      }
         
         
         //
         //  Custom for Oakmont CC
         //
         if (club.equals( "oakmont" )) {      // if Oakmont CC

            if (slotParms.ind > 14) {          // if this date is more than 14 days ahead

               //
               //  More than 14 days in advance - must have 3 guests !!
               //
               error = false;
               oakskip = true;                 // set in case we make it through here (for later)

               if (slotParms.members > 2 || slotParms.members == 0) {

                  error = true;             // must be error

               } else {

                  if (slotParms.guests < 2) {

                     error = true;             // must be error
                  }
               }

               if (error == false) {        // if ok so far

                  if (slotParms.guests == 2 && slotParms.members < 2) {

                     error = true;             // must be error
                  }
               }

               if (error == true) {        // if too many guests

                  out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                  out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Insufficient Number of Guests Specified</H3><BR>");
                  out.println("<BR><BR>Sorry, you must have 3 guests and 1 member, or 2 guests and 2 members<BR>");
                  out.println("when requesting a tee time more than 14 days in advance.");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }

            if (slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) {  // if Wednesday or Friday

               //
               // **********************************************************************
               //  Oakmont - Check for dedicated guest tee times  (Wed & Fri)
               // **********************************************************************
               //
               error = false;                             // init error indicator

               try {

                  error = verifySlot.oakmontGuestsWF(slotParms, con);

               }
               catch (Exception e5) {

                  dbError(out, e5);
                  return;
               }

               if (error == true) {      // a member exceed the max allowed tee times per month

                  out.println(SystemUtils.HeadTitle("Max Num Guest Tee Times Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");

                  out.println("<BR><H3>Insufficient Number of Guests</H3><BR>");
                  out.println("<BR><BR>Sorry, you must have 3 guests and 1 member, or 2 guests and 2 members");
                  out.println("<BR>during the selected time for this day.");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 3);
                  return;
               }
            }
         }

      }  // end of skip3 if

      //
      //   Perform this function outside of the skips so it is updated every time (updates slotParms.custom_disp fields)
      //
      if (slotParms.guests != 0) {      // if any guests were included

         //
         //  If Sonnenalp - we know we have guests so go get their rates to be displayed on the tee sheet (saved in custom_dispx)
         //
//         if (club.equals( "sonnenalp" )) {                 // if Sonnenalp
//
//            verifyCustom.addGuestRates(slotParms);         // get rates for each guest
//         }
      }


      //
      //  Check if proshop user requested that we skip the member restrictions test
      //
      //  If this skip, or any following skips are set, then we've already been through these tests.
      //
      if (skip < 4) {

         //
         // *******************************************************************************
         //  Check member restrictions
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //
         error = false;                             // init error indicator

         try {

            error = verifySlot.checkMemRests(slotParms, con);

         }
         catch (Exception e7) {

            dbError(out, e7);
            return;
         }                             // end of member restriction tests

         if (error == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b><br><br>");

            returnToSlot(out, slotParms, overrideAccess, "", 4);
            return;
         }

         //
         //  If Medinah process custom restrictions
         //
         if (club.equals( "medinahcc" )) {

            //
            // *******************************************************************************
            //  Medinah CC - Check Contingent Member Restrictions
            //
            //     on return - 'medError' contains the error code
            //
            // *******************************************************************************
            //
            int medError = medinahCustom.checkContingent(slotParms);      // go check rest's

            if (medError > 0) {          // if we hit on a restriction

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Member Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.<br><br>");

               if (medError == 1) {
                  out.println("A Family Member (8 - 11) must be accompanied by an adult.<br><br>");
               }
               if (medError == 2) {
                  out.println("A Family Member (12 & 13) must be accompanied by an adult.<br><br>");
               }
               if (medError == 3) {
                  out.println("A Family Member (14 - 16) must be accompanied by a Member.<br><br>");
               }
               if (medError == 5 || medError == 10) {
                  out.println("A Family Member (17 and Over) must be accompanied by a Member.<br><br>");
               }
               if (medError == 6) {
                  out.println("A Family Member (12 & 13) must be accompanied by a Member.<br><br>");
               }
               if (medError == 7) {
                  out.println("A Family Member (8 - 11) must be accompanied by an adult.<br><br>");
               }
               if (medError == 4 || medError == 8 || medError == 9) {
                  out.println("A Spouse must be accompanied by a Member.<br><br>");
               }
               if (medError == 11) {
                  out.println("A Family Member (14 - 16) must be accompanied by an adult.<br><br>");
               }

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
              
/*
            //
            // *******************************************************************************
            //  Medinah CC - Check for Max Advanced Reservation Rights
            //
            //     on return - 'medError' contains the number of rights used if max reached
            //
            // *******************************************************************************
            //
            try {

               medError = medinahCustom.checkARRmax(slotParms, con);

            }
            catch (Exception e7) {

               dbError(out, e7);
               return;
            }                             // end of member restriction tests

            if (medError > 0) {          // if we hit a max

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Member Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already ");

               if (medError == 1) {

                  out.println("used an Advanced Reservation Right within 14 days of this request.<br><br>");

               } else {

                  if (medError == 99) {     // if temporarily changed

                     medError = 1;          // change it back
                  }
                  out.println("used " +medError+ " Advanced Reservation Rights this season.<br><br>");
               }

               out.println("Please remove this player or try a different time.<br>");
               out.println("Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
*/              
  
         }

         //
         //  If Stanwich Club process custom restrictions
         //
         if (club.equals( "stanwichclub" )) {

            error = verifySlot.checkStanwichDependents(slotParms);     // check for Dependent w/o an Adult

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
               out.println("<BR><BR>Sorry, dependents must be accompanied by an adult for this day and time.");
               out.println("<BR><BR>All Tee Times with a Dependent must include at least 1 Adult during times specified by the golf shop.");
               out.println("<BR><BR>Please add an adult player or select a different time of the day or a different day.");

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }

         //
         //  If Castle Pines process custom restrictions
         //
         if (club.equals( "castlepines" )) {

            error = verifySlot.checkCastleDependents(slotParms);     // check for Dependent w/o an Adult

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
               out.println("<BR><BR>Sorry, dependents must be accompanied by an adult at all times.");
               out.println("<BR><BR>Please add an adult player or return to the tee sheet.");

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }

         //
         //  Cherry Hills - custom member type and membership restrictions
         //
         if (club.equals( "cherryhills" )) {

            error = verifySlot.checkCherryHills(slotParms);    // process custom restrictions

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><BR><H3>Player Not Allowed</H3>");
               out.println("<BR><BR>Sorry, one or more players are not allowed to be part of a tee time for this day and time.");
               if (slotParms.day.equals( "Monday" ) || slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) {
                  out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
               } else {
                  if (slotParms.day.equals( "Tuesday" )) {
                     if (slotParms.time > 1100) {
                        out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                     } else {
                        out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.");
                     }
                  } else {
                     if (slotParms.day.equals( "Thursday" )) {
                        if (slotParms.time > 1000) {
                           out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                        } else {
                           out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.");
                        }
                     } else {
                        if (slotParms.day.equals( "Sunday" )) {
                           if (slotParms.time > 1000) {
                              out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                           } else {
                              out.println("<BR><BR>Only Members may be included in a tee time before 10 AM on Sundays.");
                           }
                        } else {       // Saturday or Holiday
                           if (slotParms.time > 1100) {
                              out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                           } else {
                              out.println("<BR><BR>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.");
                           }
                        }
                     }
                  }
               }
               out.println("<BR><BR>Please change players or select a different day or time of day.");
               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }
           
         //
         //  If Ritz-Carlton - check for max 'Club Golf' and 'Recip' times this hour
         //
         if (club.equals( "ritzcarlton" )) {

            error = verifySlot.checkRitz(slotParms, con);

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Special Tee Time Quota Exceeded</H3>");
               out.println("<BR>Sorry, there are already 2 tee times with Club Golf members");
               out.println("<BR>or Recip guests scheduled this hour.<br><br>");
               out.println("Please select a different time of day, or change the players.<br><br>");

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }
           
         //
         //  If Skaneateles - check for Dependent Restriction
         //
         if (club.equals( "skaneateles" )) {

            error = verifySlot.checkSkaneateles(slotParms);

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Unaccompanied Dependents Not Allowed</H3>");
               out.println("<BR>Sorry, dependents must be accompanied by an adult after 4:00 PM each day.<br><br>");
               out.println("Please select a different time of day, or change the players.<br><br>");

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }

         //
         //  If Oakland Hills - check for Dependents - must be accompanied by adult (always)
         //
         if (club.equals( "oaklandhills" )) {

            error = verifySlot.checkOaklandKids(slotParms);

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Unaccompanied Dependents Not Allowed</H3>");
               out.println("<BR>Sorry, dependents must be accompanied by an adult.<br><br>");

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }


         //
         //  Bearpath - check for 'CD plus' member types
         //
         if (club.equals( "bearpath" )) {

            error = verifyCustom.checkBearpathMems(slotParms);    // process custom restriction

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
               out.println("<BR><BR>Sorry, CD Plus members are not allowed to play at this time ");
               out.println("<BR>unless accompanied by an authorized member.");

               returnToSlot(out, slotParms, overrideAccess, "", 4);
               return;
            }
         }

//         //   Custom for case 1496
//         if (club.equals( "bellemeadecc" ) && slotParms.day.equals( "Sunday" ) && slotParms.time > 759 && slotParms.time < 1251) {   // if Sunday, 8 - 12:50
//
//            error = verifyCustom.checkBelleMeadeFems(slotParms);     // check for Female w/o a Male
//
//            if (error == true) {
//
//               out.println(SystemUtils.HeadTitle("Data Entry Error"));
//               out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
//               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
//               out.println("<center>");
//               out.println("<BR><BR><BR><H3>Member Not Allowed</H3>");
//               out.println("<BR><BR>Sorry, Primary Females must be accompanied by a Primary Male between 8 AM and 12:50 PM on Sundays.");
//               out.println("<BR><BR>Please add a Primary Male member or return to the tee sheet.");
//
//               returnToSlot(out, slotParms, overrideAccess, "", 4);
//               return;
//            }
//         }

          // Custom for Oahu Case #1221
          if (club.equals( "oahucc" ) && !slotParms.mship1.startsWith("Regular") && 
                (
                  slotParms.mship1.startsWith("Intermediate") ||
                  slotParms.mship1.startsWith("Limited") || 
                  slotParms.mship1.equals("Super Senior") || 
                  slotParms.mship1.equals("SS50") ||

                  slotParms.mship2.startsWith("Intermediate") ||
                  slotParms.mship2.startsWith("Limited") || 
                  slotParms.mship2.equals("Super Senior") || 
                  slotParms.mship2.equals("SS50") ||

                  slotParms.mship3.startsWith("Intermediate") ||
                  slotParms.mship3.startsWith("Limited") || 
                  slotParms.mship3.equals("Super Senior") || 
                  slotParms.mship3.equals("SS50") ||

                  slotParms.mship4.startsWith("Intermediate") ||
                  slotParms.mship4.startsWith("Limited") || 
                  slotParms.mship4.equals("Super Senior") || 
                  slotParms.mship4.equals("SS50") ||

                  slotParms.mship5.startsWith("Intermediate") ||
                  slotParms.mship5.startsWith("Limited") || 
                  slotParms.mship5.equals("Super Senior") || 
                  slotParms.mship5.equals("SS50")
                ) ) {

              if (slotParms.day.equals( "Saturday" ) && slotParms.time > 659 && slotParms.time < 1453) {

                  out.println(SystemUtils.HeadTitle("Data Entry Error"));
                  out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center>");
                  out.println("<BR><BR><H3>Invalid Days in Advance</H3>");
                  out.println("<BR>Sorry, you must have a Regular or Regular NR member as the first player in the group for this time of day.");
                  out.println("<BR><BR>Please select a different time of day, or change the players.");

                  returnToSlot(out, slotParms, overrideAccess, "", 4);
                  return;
              }

              if (
                  (slotParms.day.equals( "Sunday" ) && slotParms.time > 629 && slotParms.time < 858)) {

                  out.println(SystemUtils.HeadTitle("Data Entry Error"));
                  out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center>");
                  out.println("<BR><BR><H3>Invalid Days in Advance</H3>");
                  out.println("<BR>Sorry, you must have a Regular or Regular NR member as the first player in the group for this time of day.");
                  out.println("<BR><BR>Please select a different time of day, or change the players.");

                  returnToSlot(out, slotParms, overrideAccess, "", 4);
                  return;
              }
          }

          // If Los Coyotes then only make sure there are at least two members or 1 w/ guest for all tee times (Case 1211)
          if (club.equals( "loscoyotes" )) {
              
              // if less than two members
              error = (slotParms.members < 2);
              // if there were less than 2 members, then lets see if one of them is a guest
              if (error) error = (slotParms.members == 1 && slotParms.guests == 0);

              if (error == true) {

                   out.println(SystemUtils.HeadTitle("Data Entry Error"));
                   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                   out.println("<hr width=\"40%\">");
                   out.println("<BR><BR><BR><H3>Invalid Number of Players</H3>");
                   out.println("<BR>Sorry, tee times are not allowed with less than two named players.");
                   out.println("<BR><BR>Please add another member or guest.<br><br>");

                  returnToSlot(out, slotParms, overrideAccess, "", 4);
                  return;
              }

          } // end Los Coyotes min players check
         
      }  // end of skip4 if


      //
      //  Check if proshop user requested that we skip the 5-some restrictions test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 5) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions
         //
         //   If 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((!slotParms.player5.equals( "" )) && (slotParms.p5rest.equals( "Yes" ))) { // if 5-somes restricted prompt user to skip test

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotParms, overrideAccess, "", 5);
            return;
         }

      }  // end of skip5 if


      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If either skip is set, then we've already been through these tests.
      //
      if (skip < 6) {

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

            dbError(out, e7);
            return;
         }                             // end of member restriction tests

         if (error == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
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
            out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
            out.println("<br><br>This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotParms, overrideAccess, "", 6);
            return;
         }

         //
         //  Hazeltine - check for consecutive singles or 2-somes
         //
         if (club.equals( "hazeltine" )) {

            if (slotParms.player3.equals( "" ) && slotParms.player4.equals( "" ) && slotParms.player5.equals( "" )) {  // if 1 or 2 players

               error = verifySlot.checkHazGrps(slotParms, con);    // process custom restriction

               if (error == true) {

                  out.println(SystemUtils.HeadTitle("Data Entry Error"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center>");
                  out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                  out.println("<BR><BR>Sorry, there is already a small group immediately before or after this time.");
                  out.println("<BR><BR>There cannot be 2 consecutive small groups during this time.");
                  out.println("<BR><BR>Please add players or select a different time of day.");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 6);
                  return;
               }
            }
         }      // end of Hazeltine
                    
      }         // end of IF skip6


      //
      //  Fort Collins 5-some checks
      //
      if (club.equals( "fortcollins" )) {

         error = false;
           
         if (!slotParms.player1.equals( slotParms.oldPlayer1 ) ||
             !slotParms.player2.equals( slotParms.oldPlayer2 ) ||
             !slotParms.player3.equals( slotParms.oldPlayer3 ) ||
             !slotParms.player4.equals( slotParms.oldPlayer4 ) ||
             !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if group not already accepted by pro

            if (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" )) {  // if 5-some

               if (slotParms.course.equals( "Greeley CC" )) {       // if Greeley CC course

                  //
                  //  5-some on Greeley course - cannot be all Fort Collins members
                  //
                  if (!slotParms.mtype1.endsWith( "Greeley" ) && !slotParms.mtype2.endsWith( "Greeley" ) && 
                      !slotParms.mtype3.endsWith( "Greeley" ) && !slotParms.mtype4.endsWith( "Greeley" ) &&
                      !slotParms.mtype5.endsWith( "Greeley" )) {

                     error = true;     // no FC members - error
                  }
              
               } else {       // Fort Collins Course

                  //
                  //  5-some on Fort Collins course - cannot be all Greeley members
                  //
                  error = true;       // assume error
                    
                  if ((!slotParms.mtype1.equals( "" ) && !slotParms.mtype1.endsWith( "Greeley" )) ||
                      (!slotParms.mtype2.equals( "" ) && !slotParms.mtype2.endsWith( "Greeley" )) ||
                      (!slotParms.mtype3.equals( "" ) && !slotParms.mtype3.endsWith( "Greeley" )) ||
                      (!slotParms.mtype4.equals( "" ) && !slotParms.mtype4.endsWith( "Greeley" )) ||
                      (!slotParms.mtype5.equals( "" ) && !slotParms.mtype5.endsWith( "Greeley" ))) {

                     error = false;     // at least one FC member - ok
                  }
               }

               if (error == true) {

                  out.println(SystemUtils.HeadTitle("Data Entry Error"));
                  out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center>");
                  out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                  out.println("<BR><BR>Sorry, 5-somes are not allowed without a member from that club.");
                  out.println("<BR><BR>Please limit the request to 4 players or include a member of the club.");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 6);
                  return;
               }
            }
         }
      }     // end of IF Fort Collins


      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 7) {

         //
         //***********************************************************************************************
         //
         //    Now check if any of the players are already scheduled today
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

            dbError(out, e21);
            return;
         }

         if (slotParms.hit == true || slotParms.hit2 == true || slotParms.hit3 == true) { // if we hit on a duplicate res

            if (slotParms.time2 != 0) {                                  // if other time was returned

               thr = slotParms.time2 / 100;                      // set time string for message
               tmin = slotParms.time2 - (thr * 100);
               
               if (thr > 12) thr -= 12;
               tmsg = thr + ":" + SystemUtils.ensureDoubleDigit(tmin) + " PM";
               
               /*
               if (thr == 12) {
                   
                   tmsg = thr + ":" + SystemUtils.ensureDoubleDigit(tmin) + " PM";
                     
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
               */
               if (!slotParms.course2.equals( "" )) {        // if course provided

                  tmsg = tmsg + " on the " +slotParms.course2+ " course";
               }
            }
            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
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

            boolean allowOverride = true;

            if (!overrideAccess || slotParms.club.equals( "lakewoodranch" )) {    // skip if lakewood ranch

                allowOverride = false;
            }
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotParms, allowOverride, "", 7);
            return;
         }
           
         //
         //   If Merion and East course, then check if any other family members are scheduled today - not allowed.
         //
         if (club.equals( "merion" ) && slotParms.course.equals( "East" )) {

            slotParms.hit = false;                             // init error indicator

            try {

               verifySlot.checkMerionSched(slotParms, con);

            }
            catch (Exception e21) {

               dbError(out, e21);
               return;
            }

            if (slotParms.hit == true) {      // if another family member is already booked today

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Member Already Scheduled</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> already has a family member scheduled to play today.<br><br>");
               out.println("Only one player per membership is allowed each day.");
               out.println("<br><br>Please remove this player or try a different date.");
               //
               //  Return to _slot to change the player order
               //
               returnToSlot(out, slotParms, overrideAccess, "", 7);
               return;
            }
              

            //
            //  Merion - Now check if more than 7 days in adv and a w/e, no more than 4 adv tee times per day
            //
            if (slotParms.ind > 7) {      // if this date is more than 7 days ahead

               if ((slotParms.day.equals( "Saturday" ) && slotParms.time > 1030) || (slotParms.day.equals( "Sunday" ) && slotParms.time > 900)) {   

                  try {

                     error = verifySlot.checkMerionWE(slotParms, con);

                  }
                  catch (Exception e21) {

                     dbError(out, e21);
                     return;
                  }

                  if (error == true) {      // if another family member is already booked today

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Advance Tee Time Limit</H3><BR>");
                     out.println("<BR>Sorry, there are already 4 advance tee times scheduled for this day.<br><br>");
                     out.println("Please try a different date.<br>");
                     //
                     //  Return to _slot to change the player order
                     //
                     returnToSlot(out, slotParms, overrideAccess, "", 7);
                     return;
                  }
               }
            }

         }  // end of IF Merion
           
      }         // end of IF skip7


      if (skip < 8) {

         //
         //***********************************************************************************************
         //
         //    Now check all players for 'days in advance' - based on membership types
         //
         //***********************************************************************************************
         //
         if (!slotParms.mship1.equals( "" ) || !slotParms.mship2.equals( "" ) || !slotParms.mship3.equals( "" ) || 
             !slotParms.mship4.equals( "" ) || !slotParms.mship5.equals( "" )) {
           
             
            // 
            // If Greenwich and less then 3 players on the listed dates/times then reject  (Case #1123)
            //
            if (club.equals( "greenwich" )) {

                error = verifySlot.checkGreenwichMinPlayers(slotParms, -1);

                if (error == true) {

                    out.println(SystemUtils.HeadTitle("Data Entry Error"));
                    out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                    out.println("<center>");
                    out.println("<BR><BR><H3>Invalid Number of Players</H3>");
                    out.println("<BR>Sorry, you are not allowed to reserve tee times with less than three players.");

                    returnToSlot(out, slotParms, overrideAccess, "", 8);
                    return;
                }
                
            } // end is Greenwich
            
             
             
            //
            //  skip if Oakmont or Congressional guest time
            //
            if (oakskip == false) {

               try {

                  error = verifySlot.checkDaysAdv(slotParms, con);

               }
               catch (Exception e21) {

                  dbError(out, e21);
                  return;
               }

               
               /*
               if (club.equals( "valleycc" ) && shortDate > 416 && shortDate < 917) {   // Ladies custom for summer season
                                 
                  if (error == true && slotParms.ind < 15 && 
                      slotParms.time > 729 && slotParms.time < 1100 && slotParms.day.equals( "Friday" ) ) {
                                    
                      // DON'T NEED TO CHECK MEMBERSHIP TYPE BECAUSE LADIES RESTRICTION WILL BLOCK ANY NON-FEMALES FROM PLAYING
                      error = false;
                  }
               }
                */
               
               
               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to be part of a tee time this far in advance.<br><br>");
                  out.println("This restriction is based on the 'Days In Advance' setting for each Membership Type.<br><br>");

                  //
                  //  Return to _slot
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 8);
                  return;
               }
            }
              
            //
            //  if Belle Haven - check for 'Elective' membership types - limited to 10 rounds per year on w/e's
            //
            if (club.equals( "bellehaven" )) {

               try {

                  error = verifySlot.checkBelleHaven(slotParms, con);

               }
               catch (Exception e22) {

                  dbError(out, e22);
                  return;
               }

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Weekend Tee Time Limit Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is an Elective member and has");
                  out.println("<BR>already played 10 times on weekends or holidays this year.<br><br>");
                  //
                  //  Return to _slot
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 8);
                  return;
               }
            }  

            //
            //  If Oakland Hills check for advance reservations
            //
            if (club.equals( "oaklandhills" )) {

               if (slotParms.ind > 5) {

                  //
                  //   check for advance times if more than 5 days in adv
                  //
                  error = verifySlot.checkOaklandAdvTime1(slotParms, con);

                  if (error == true) {          // if we hit on a violation

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Member Has Already Used An Advance Request</H3>");
                     out.println("<BR>Sorry, each membership is entitled to only one advance tee time request.<br>");
                     out.println("<BR>" +slotParms.player+ " has already used his/her advance tee time request for the season.");

                     returnToSlot(out, slotParms, overrideAccess, "", 8);
                     return;
                  }

                  error = verifySlot.checkOaklandAdvTime2(slotParms, con);

                  if (error == true) {          // if we hit on a violation

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Maximum Allowed Advanced Tee Times Exist</H3>");
                     out.println("<BR>Sorry, the maximum number of advanced tee time requests already exist on the selected date.");

                     returnToSlot(out, slotParms, overrideAccess, "", 8);
                     return;
                  }
               }
                 
               if (slotParms.ind > 7) {        // if more than 7 days in advance

                  //
                  //  Cannot have X's in tee time - members only!!
                  //
                  if (slotParms.player1.equalsIgnoreCase( "x" ) || slotParms.player2.equalsIgnoreCase( "x" ) ||
                      slotParms.player3.equalsIgnoreCase( "x" ) || slotParms.player4.equalsIgnoreCase( "x" ) ||
                      slotParms.player5.equalsIgnoreCase( "x" )) {

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Invalid Player Selection</H3>");
                     out.println("<BR>Sorry, you cannot reserve player positions with an X more than 7 days in advance.<br>");

                     returnToSlot(out, slotParms, overrideAccess, "", 8);
                     return;
                  }
               }
            }       // end of IF Oakland Hills
              
            //
            //  If CC of the Rockies & Catamount Ranch - check for max number of advance tee times
            //
            if ((club.equals( "ccrockies" ) || club.equals( "catamount" ) || club.equals( "sonnenalp" )) && slotParms.ind > 0) {       // if not today

               error = verifySlot.checkRockies(slotParms, con);

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
                  if (club.equals( "sonnenalp" )) {
                     out.println("<BR>Sorry, " +slotParms.player+ " already has 12 advance tee time requests scheduled.<br><br>");
                  } else {
                     out.println("<BR>Sorry, " +slotParms.player+ " already has 5 advance tee time requests scheduled.<br>");
                  }

                  returnToSlot(out, slotParms, overrideAccess, "", 8);
                  return;
               }
            }           // end of IF CC of the Rockies

         }
      }         // end of IF skip8

      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 9) {     // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********
         out.println("<!-- skip=" + skip + " -->");
         //
         //***********************************************************************************************
         //
         //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
         //
         //***********************************************************************************************
         //
         if (slotParms.guests != 0 && slotParms.members != 0) {      // if both guests and members were included

            if (slotParms.g1.equals( "" )) {              // if slot 1 is not a guest

               //
               //  Both guests and members specified - determine guest owners by order
               //
               gi = 0;
               memberName = "";
               while (gi < 5) {                  // cycle thru arrays and find guests/members

                  if (!slotParms.gstA[gi].equals( "" )) {

                     usergA[gi] = memberName;       // get last players username
                  } else {
                     usergA[gi] = "";               // init field
                  }
                  if (!memA[gi].equals( "" )) {

                     memberName = memA[gi];        // get players username
                  }
                  gi++;
               }
               slotParms.userg1 = usergA[0];        // max of 4 guests since 1 player must be a member to get here
               slotParms.userg2 = usergA[1];
               slotParms.userg3 = usergA[2];
               slotParms.userg4 = usergA[3];
               slotParms.userg5 = usergA[4];
            }
              
            if (!slotParms.g1.equals( "" ) || slotParms.members > 1) {  // if slot 1 is a guest OR more than 1 member

               //
               //  At least one guest and one member have been specified.
               //  Prompt user to verify the order.
               //
               //  Only require positioning if a POS system was specified for this club (saved in Login)
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

               //
               // if slot 1 is a guest & POS & not already assigned
               //
               if (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

                  out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("You cannot have a guest in the first player position when one or more members are also specified.");
                  out.println("<BR><BR>");
               } else {
                  out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("Please verify that the following order is correct:");
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
               }

               boolean allowOverride = true;
               
               if (!overrideAccess || (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 ))) {

                   allowOverride = false;

               }
               
               returnToSlot(out, slotParms, allowOverride, user, 9);
                   
               return;
               
/*
               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"Proshop_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
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
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");

               if (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

               } else {
                  out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

                  //
                  //  Return to process the players as they are
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"Proshop_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
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
                  out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
                  out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
                  out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
                  out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
                  out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
                  out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
                  out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                  out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\"></form></font>");
               }
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
*/
            }

            //
            //  At least 1 guest requested in tee time.  If Interlachen, check for a 5-some request.
            //  Guests are not allowed in any 5-some group.
            //
            if (club.equals( "interlachen" )) {      // if Interlachen

               if (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" )) {  // if 5-some

                  if (!slotParms.player1.equals( slotParms.oldPlayer1 ) ||
                      !slotParms.player1.equals( slotParms.oldPlayer2 ) ||
                      !slotParms.player1.equals( slotParms.oldPlayer3 ) ||
                      !slotParms.player1.equals( slotParms.oldPlayer4 ) ||
                      !slotParms.player1.equals( slotParms.oldPlayer5 )) {   // if group not already accepted by pro

                     out.println(SystemUtils.HeadTitle("Data Entry Error"));
                     out.println("<body>");
                     out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                     out.println("<center>");
                     out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                     out.println("<BR><BR>Sorry, guests are not allowed in a 5-some.");
                     out.println("<BR><BR>Please limit the request to 4 players or remove the guest(s).");
                     
                     returnToSlot(out, slotParms, overrideAccess, "", 9);
                     return;
                  }
               }
            }     // end of IF Interlachen


            //
            //  At least 1 guest requested in tee time.  If Fort Collins - Greeley course, check for a 5-some request.
            //  More than 1 Guest is not allowed in any 5-some group.
            //
            if (club.equals( "fortcollins" ) && slotParms.course.equals( "Greeley CC" ) && slotParms.guests > 1) { 

               if (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" )) {  // if 5-some

                  if (!slotParms.player1.equals( slotParms.oldPlayer1 ) ||
                      !slotParms.player2.equals( slotParms.oldPlayer2 ) ||
                      !slotParms.player3.equals( slotParms.oldPlayer3 ) ||
                      !slotParms.player4.equals( slotParms.oldPlayer4 ) ||
                      !slotParms.player5.equals( slotParms.oldPlayer5 )) {   // if group not already accepted by pro

                     out.println(SystemUtils.HeadTitle("Data Entry Error"));
                     out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\">");
                     out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                     out.println("<center>");
                     out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                     out.println("<BR><BR>Sorry, you may not have more than one guest in a 5-some.");
                     out.println("<BR><BR>Please limit the request to 4 players or remove the guest(s).");

                     returnToSlot(out, slotParms, overrideAccess, "", 9);
                     return;
                  }
               }
            }     // end of IF Fort Collins

         } else if (slotParms.guests > 0 && slotParms.members == 0) {

             //  Prompt user to specify associated member(s) or skip.
             //
             out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
             out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
             out.println("<hr width=\"40%\">");
             out.println("<BR><BR><H3>Unaccompanied Guests Found</H3><BR>");

             out.println("You are requesting a tee time for one or more unaccompanied guests.<br>");
             out.println("The guests on a wait list will not be associated with a member.<br><br>");

             returnToSlot(out, slotParms, overrideAccess, "", 9);
             return;
/*
            //
            //  Either all members or all guests - check for all guests (Unaccompanied Guests)
            //
            if (slotParms.guests != 0 && !club.equals( "sonnenalp")) {      // if all guests and NOT Sonnenalp

               //
               //  At least one guest and no member has been specified.
               //  Get associated member names if already assigned.
               //
               try {
                 
                  if (!slotParms.userg1.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg1);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem1 = mem_name.toString();                      // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg2.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg2);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem2 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg3.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg3);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem3 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg4.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg4);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem4 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg5.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg5);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotParms.mem5 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
               }
               catch (Exception ignore) {
               }
               
               //
               //  Prompt user to specify associated member(s) or skip.
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");
                              
               // if Hazeltine National and sponsored guests
               if ((club.equals( "hazeltine" )) &&   
                   (slotParms.player1.startsWith(sponsored) || slotParms.player2.startsWith(sponsored) ||
                    slotParms.player3.startsWith(sponsored) || slotParms.player4.startsWith(sponsored) ||
                    slotParms.player5.startsWith(sponsored))) {   

                  out.println("You are requesting a tee time for a Sponsored Group.<br>");
                  out.println("Sponsored Groups must be associated with a member.<br><br>");
                  out.println("<BR>Would you like to assign a member to the Sponsored Group?");

               } else {

                  if (slotParms.guests == 1) {      // if one guest
                     out.println("You are requesting a tee time for an unaccompanied guest.<br>");
                     out.println("The guest should be associated with a member.<br><br>");
                     out.println("<BR>Would you like to assign a member to the guest, or change the assignment?");
                  } else {
                     out.println("You are requesting a tee time for unaccompanied guests.<br>");
                     out.println("Guests should be associated with a member.<br><br>");
                     out.println("<BR>Would you like to assign a member to the guests, or change the assignments?");
                  }
               }

               //
               //  Return to _slot (doPost) to assign members
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + slotParms.wait_list_id + "\">");
               out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
               out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
               out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
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
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"mem1\" value=\"" + slotParms.mem1 + "\">");
               out.println("<input type=\"hidden\" name=\"mem2\" value=\"" + slotParms.mem2 + "\">");
               out.println("<input type=\"hidden\" name=\"mem3\" value=\"" + slotParms.mem3 + "\">");
               out.println("<input type=\"hidden\" name=\"mem4\" value=\"" + slotParms.mem4 + "\">");
               out.println("<input type=\"hidden\" name=\"mem5\" value=\"" + slotParms.mem5 + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");

               out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");  // assign member to guests

               out.println("<input type=\"submit\" value=\"Yes - Assign Member\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               //
               //  Return to process the players as they are
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
               out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + slotParms.wait_list_id + "\">");
               out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
               out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
               out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
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
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
               out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
               out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
               out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
               out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
               out.println("<input type=\"submit\" value=\"No - Continue\" name=\"submitForm\"></form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }
*/
         }      // end of IF any guests specified

      } // end skip < 9
      else if (skip == 9) {   // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

          //
          //  User has responded to the guest association prompt - process tee time request in specified order
          //
          slotParms.userg1 = req.getParameter("userg1"); 
          slotParms.userg2 = req.getParameter("userg2");
          slotParms.userg3 = req.getParameter("userg3");
          slotParms.userg4 = req.getParameter("userg4");
          slotParms.userg5 = req.getParameter("userg5");
         
      }         // end of IF skip9 
        
      //
      //  NOTE:  skip 10 is set in doPost method above when 'skip 9' processing prompts for 'Assign'.
      //         slotParms.userg1-5 are set in verifySlot when 'mem1-5' parms are passed.
      //
      if (club.equals( "hazeltine" )) {      // if Hazeltine National

         if (skip == 9 || skip == 10) {   // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

            //
            //  Member has been assigned to the Sponsored Group (unaccomp. guests)
            //
            int rcode = 0;
              
            try {

               rcode = verifySlot.checkSponsGrp(slotParms, con);  // verify Sponsored Group for Hazeltine
            }
            catch (Exception e29) {

               dbError(out, e29);
               return;
            }

            if (rcode > 0) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Sponsored Group Error - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Restriction For Sponsored Group Request</H3><BR>");
               out.println("<BR>Your request for a Sponsored Group has been rejected for the following reason:<br><br>");
               if (rcode == 1) {
                  out.println("The maximum number of Sponsored Groups have already been scheduled for this day.<br><br>");
               } else {
                  if (rcode == 2) {
                     out.println("Sponsored Groups are not allowed at this time of day.<br><br>");
                  } else {
                     out.println("Member already has 2 Sponsored Groups scheduled today.<br><br>");
                  }
               }

               //
               //  Return to _slot
               //
               returnToSlot(out, slotParms, overrideAccess, "", 11);
               return;
            }
         }
      }

      if (skip < 12) {


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
            }

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
               out.println("<BR>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established for this guest type.");
               out.println("<br><br>You will have to remove the guest in order to complete this request.");
               //
               //  Return to _slot (doPost) to assign members
               //
               returnToSlot(out, slotParms, overrideAccess, "", 12);
               return;
            }


            //
            //  Medinah Custom - check for guest quotas on Course #1 (max 12 guest per family between 6/01 - 8/31)
            //
            if (club.equals( "medinahcc" )) {

               if (slotParms.course.equals( "No 1" )) {                 // if Course #1

                  error = medinahCustom.checkNonRes1(slotParms, con);
               }

//   Removed per Mike Skully on 7/01/06
//               if (slotParms.course.equals( "No 3" )) {                 // if Course #3

//                  error = medinahCustom.checkGuests3(slotParms, con);
//               }

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already met the guest quota for June through August.");
                  out.println("<br><br>You will have to remove the guest in order to complete this request.");

                  //
                  //  Return to _slot (doPost) to assign members
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 12);
                  return;
               }
            }

            //
            //  Merion Custom - check for guest quotas on East Course
            //
            if (club.equals( "merion" ) && slotParms.course.equals( "East" )) {

               error = verifySlot.checkMerionGres(slotParms, con);

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guest Time Quota Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already met the quota for Guest Times.");
                  out.println("<br><br>You will have to remove the guest(s) in order to complete this request.");

                  //
                  //  Return to _slot (doPost) to assign members
                  //
                  returnToSlot(out, slotParms, overrideAccess, "", 12);
                  return;
               }
            }
              

            //
            //  Congressional Custom - check for guest quotas for 'Junior A' mships
            //
            if (club.equals( "congressional" )) {

               error = congressionalCustom.checkJrAGuests(slotParms);

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, Junior A members can only have one guest per member");
                  out.println("<br>on the Open Course on weekdays.");
                  out.println("<br><br>You will have to remove the extra guest(s) in order to complete this request.");

                  returnToSlot(out, slotParms, overrideAccess, "", 12);
                  return;
               }
            }


            //
            //  The CC - guest quotas
            //
            if (club.equals( "tcclub" )) {

               //
               //  Check for total guests per family if in season (4/01 - 10/31) and Main or Championship Course
               //
               if (shortDate > 400 && shortDate < 1032 &&
                   (slotParms.course.startsWith( "Main Cours" ) || slotParms.course.startsWith( "Championship Cours" ))) {

                  error = verifyCustom.checkTCCguests(slotParms, con);

                  if (error == true) {

                     out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                     out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><H3>Invalid Request</H3><BR>");
                     out.println("<BR><BR>Sorry, " +slotParms.player+ " has already reached the maximum limit of guests.<BR>");
                     out.println("<BR>Each membership is allowed 6 guests per month and 18 guests per season.");

                     returnToSlot(out, slotParms, overrideAccess, "", 12);
                     return;
                  }
               }
            }


            //
            //  Wellesley Custom - check for guest restrictions
            //
            if (club.equals( "wellesley" )) {

               int wellError = verifyCustom.wellesleyGuests(slotParms, con);

               //
               //  check for any error
               //
               if (wellError > 0) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guests Restricted for Member</H3><BR>");

                  if (wellError == 1) {          // if we hit on a violation

                     out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest.");
                  }

                  if (wellError == 2 || wellError == 3) {          // if we hit on a violation

                     out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest on this day.");
                  }

                  if (wellError == 4) {          // if we hit on a violation

                     out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to have a guest on this date.");
                  }

                  if (wellError == 5) {          // if we hit on a violation

                     out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already reached the yearly guest quota.");
                  }

                  out.println("<br><br>You will have to remove the guest(s) in order to complete this request.");

                  returnToSlot(out, slotParms, overrideAccess, "", 12);
                  return;
               }
            }        // end of Wellesley custom
            
            
            //
            //  Custom for Oakmont - 
            //      If Feb, Mar or Apr check if member already has 10 advance guest times scheduled (any time during year).
            //      If so, then reject.  Members can only reserve 10 guest times in each month, but the guest times can be any time 
            //      during the season (advance times).  After Apr they can book an unlimited number of guest times.
            //
            //      The month (01 = Jan, 02 = Feb, etc.) is saved in custom_int so we know when the tee time was booked.
            //
            /*
            if (club.equals( "oakmont" ) && slotParms.oldPlayer1.equals("")) {   // oakmont and new tee time request

               if (thisMonth > 0 && thisMonth < 6) {         // if Jan, Feb, Mar, Apr, or May (tee sheets closed in Jan, but check anyway)

                  error = verifyCustom.checkOakmontGuestQuota(slotParms, thisMonth, con);

                  if (error == true) {

                      out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                      out.println("<hr width=\"40%\">");
                      out.println("<BR><H3>Monthly Guest Quota Exceeded</H3><BR>");
                      out.println("<BR><BR>Sorry,  " +slotParms.player+ " has already scheduled the max allowed guest times this month.<BR>");
                      out.println("There is a limit to the number of advance guest rounds that can be scheduled in Feb, Mar, Apr, and May.");

                      returnToSlot(out, slotParms, overrideAccess, "", 12);
                      return;
                  } 
               }

               slotParms.custom_int = thisMonth;                // save month value for teecurr

            }          // end of IF oakmont and new tee time request 
             */
      
            
            //
            //  Custom for Baltusrol -  each member can only have 3 outstanding guest times 
            //
            if (club.equals( "baltusrolgc" )) {  

               error = verifyCustom.checkBaltusrolGuestQuota(slotParms, con);

               if (error == true) {

                   out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
                   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                   out.println("<hr width=\"40%\">");
                   out.println("<BR><H3>Guest Quota Exceeded</H3><BR>");
                   out.println("<BR><BR>Sorry,  " +slotParms.player+ " has already scheduled the max allowed guest times.<BR>");
                   out.println("There is a limit to the number of guest times (3) that can be scheduled in advance.");

                   returnToSlot(out, slotParms, overrideAccess, "", 12);
                   return;
               } 
            }          // end of IF baltusrol  
      
            
          /*          // finish this later - needs nslotParms!!
            //
            //  Winged Foot - guest quotas
            //
            if (club.equals( "wingedfoot" )) {

               //
               //  Check for total guests per family
               //
               error = verifyNCustom.checkWFguests(slotParms, con);

               if (error == true) {

                  out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Guest Quota Exceeded</H3><BR>");
                  out.println("<BR><BR>Sorry, " +nSlotParms.player+ " has already reached the maximum limit of guests.<BR>");
                  out.println("<BR>Each membership is allowed a specified number of guests per season and per year.");
                  out.println("<BR><BR>");
                  out.println("<BR>Would you like to override this restriction and allow the request?");

                  returnToSlot(out, nSlotParms, 12);
                  return;
               }
                 
               //
               //  Check for Legacy Preferred Associates mship types and guests
               //
               error = verifyNCustom.checkWFLguests(slotParms, con);

               if (error == true) {

                  out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Guests Restricted </H3><BR>");
                  out.println("<BR><BR>Sorry, " +nSlotParms.player+ " is not allowed to have guests at this time.<BR>");
                  out.println("<BR>Guests are only allowed before 11:30 AM and after 2:00 PM on Tues, Wed & Thurs.");
                  out.println("<BR><BR>");
                  out.println("<BR>Would you like to override this restriction and allow the request?");

                  returnToSlot(out, nSlotParms, 12);
                  return;
               }

               
               //
               //  Winged Foot (West course only) - guest quota - no more than 9 guests allowed between x:30 and x:29 (each hour of the day).
               //
               if (time > 729 && nSlotParms.course.equals("West")) {    // quota starts at 7:30 AM

                  error = verifyNCustom.checkWFguestsHour(slotParms, con);

                  if (error == true) {

                     out.println(SystemUtils.HeadTitle("Hourly Guest Limit Reached - Reject"));
                     out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><H3>Guest Quota Exceeded</H3><BR>");
                     out.println("<BR><BR>Sorry,  your request will exceed the maximum number of guests (9) for this hour.<BR>");
                     out.println("<BR><BR>");
                     out.println("<BR>Would you like to override this restriction and allow the request?");

                     returnToSlot(out, nSlotParms, 12);
                     return;
                  }       
               }   // end hourly guest quota
            
            } // end if Winged Foot
           */
            
            
         }   // end of IF guests

         
      } else {   // skip 12 requested?

         if (skip == 12) {

            //
            //  We must restore the guest usernames
            //
            slotParms.userg1 = req.getParameter("userg1");
            slotParms.userg2 = req.getParameter("userg2");
            slotParms.userg3 = req.getParameter("userg3");
            slotParms.userg4 = req.getParameter("userg4");
            slotParms.userg5 = req.getParameter("userg5");
         }
      }     // end of IF skip 12


      //
      //  Before we update the tee time, go check for any flags to be added to members' names for the pro tee sheet
      //
      verifySlot.checkTFlag(slotParms, con);

      

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

         emailMod = 1;  // wait list signup was modified

      } else {

         emailNew = 1;  // wait list signup is new
         
      }

   }  // end of 'cancel this res' if - cancel will contain empty player fields


    //int course_id = SystemUtils.getClubParmIdFromCourseName(slotParms.course, con);

    //
    //  Verification complete -
    //  Add or Update the wait list entry entry in the wait list tables
    //
    boolean newEntry = false;
    
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
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos, guest_id) VALUES (?, ?, ?, ?, ?, 1, ?)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user1);
                pstmt6.setString(3, slotParms.p1cw);
                pstmt6.setString(4, slotParms.player1);
                pstmt6.setInt(5, slotParms.p91);
                pstmt6.setInt(6, slotParms.guest_id1);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player2.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos, guest_id) VALUES (?, ?, ?, ?, ?, 2, ?)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user2);
                pstmt6.setString(3, slotParms.p2cw);
                pstmt6.setString(4, slotParms.player2);
                pstmt6.setInt(5, slotParms.p92);
                pstmt6.setInt(6, slotParms.guest_id2);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player3.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos, guest_id) VALUES (?, ?, ?, ?, ?, 3, ?)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user3);
                pstmt6.setString(3, slotParms.p3cw);
                pstmt6.setString(4, slotParms.player3);
                pstmt6.setInt(5, slotParms.p93);
                pstmt6.setInt(6, slotParms.guest_id3);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player4.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos, guest_id) VALUES (?, ?, ?, ?, ?, 4, ?)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user4);
                pstmt6.setString(3, slotParms.p4cw);
                pstmt6.setString(4, slotParms.player4);
                pstmt6.setInt(5, slotParms.p94);
                pstmt6.setInt(6, slotParms.guest_id4);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!slotParms.player5.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO wait_list_signups_players " +
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos, guest_id) VALUES (?, ?, ?, ?, ?, 5, ?)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, signup_id);
                pstmt6.setString(2, slotParms.user5);
                pstmt6.setString(3, slotParms.p5cw);
                pstmt6.setString(4, slotParms.player5);
                pstmt6.setInt(5, slotParms.p95);
                pstmt6.setInt(6, slotParms.guest_id5);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
            
       }
       catch (Exception e6) {

          msg = "Add Wait List Entry. ";
          dbError(out, e6, msg);
          return;
       }
        
       newEntry = true;
        
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
            updateSignUpPlayers(signup_id, slotParms.player1, slotParms.oldPlayer1, slotParms.user1, slotParms.p1cw, slotParms.oldp1cw, slotParms.p91, slotParms.oldp91, 1, slotParms.guest_id1, con, out);
            updateSignUpPlayers(signup_id, slotParms.player2, slotParms.oldPlayer2, slotParms.user2, slotParms.p2cw, slotParms.oldp2cw, slotParms.p92, slotParms.oldp92, 2, slotParms.guest_id2, con, out);
            updateSignUpPlayers(signup_id, slotParms.player3, slotParms.oldPlayer3, slotParms.user3, slotParms.p3cw, slotParms.oldp3cw, slotParms.p93, slotParms.oldp93, 3, slotParms.guest_id3, con, out);
            updateSignUpPlayers(signup_id, slotParms.player4, slotParms.oldPlayer4, slotParms.user4, slotParms.p4cw, slotParms.oldp4cw, slotParms.p94, slotParms.oldp94, 4, slotParms.guest_id4, con, out);
            updateSignUpPlayers(signup_id, slotParms.player5, slotParms.oldPlayer5, slotParms.user5, slotParms.p5cw, slotParms.oldp5cw, slotParms.p95, slotParms.oldp95, 5, slotParms.guest_id5, con, out);

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
        
        emailMod = 1;  // wait list signup was modified
            
    } // end if insert or update
    
    

   if (index.equals( "888" )) {         // if came from proshop_searchmain

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<title>Proshop Wait List Entry</title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?search=yes\">");
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");
           
      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

         if (notesL > 254) {

         out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
      }
      out.println("<p>&nbsp;</p></font>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else {                             // came from proshop_sheet

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<title>Proshop Wait List Entry</title>");
      if (newEntry) {
          out.println("<meta http-equiv=\"Refresh\" content=\"2; url=Proshop_jump?index=" + index + "&course=" + ((!returnCourse.equals( "" )) ? returnCourse : slotParms.course) + "&jump=" + slotParms.jump + "\">");
      } else {
          out.println("<meta http-equiv=\"Refresh\" content=\"2; url=Proshop_dsheet?mode=WAITLIST&name=" + parmWL.name + "&index=" + index + "&course=" + slotParms.course + "&returnCourse=" + returnCourse + "&hide=1\">");
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");

      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation has been " +
                 ((newEntry) ? "accepted and processed" : "updated successfully") + ".</p>");

      }

      out.println("<p>&nbsp;</p></font>");

      out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#8B8970\" cellpadding=\"8\">");
      if (newEntry) {
          out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      } else {
          out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      }
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + ((!returnCourse.equals( "" )) ? returnCourse : slotParms.course) + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" + slotParms.jump + ">");
      out.println("<tr><td><font size=\"2\">");
      out.println("<input type=\"submit\" value=\"Return\">");
      out.println("</font></td></tr></form></table>");
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
   //*****************************************************
   //  Send email notification if necessary and not today
   //*****************************************************
   //
   if (sendemail != 0 && suppressEmails.equalsIgnoreCase("no") && ind > 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.activity_id = 0;
      parme.club = club;
      parme.guests = slotParms.guests;
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

      parme.guest_id1 = slotParms.guest_id1;
      parme.guest_id2 = slotParms.guest_id2;
      parme.guest_id3 = slotParms.guest_id3;
      parme.guest_id4 = slotParms.guest_id4;
      parme.guest_id5 = slotParms.guest_id5;

      parme.oldguest_id1 = slotParms.oldguest_id1;
      parme.oldguest_id2 = slotParms.oldguest_id2;
      parme.oldguest_id3 = slotParms.oldguest_id3;
      parme.oldguest_id4 = slotParms.oldguest_id4;
      parme.oldguest_id5 = slotParms.oldguest_id5;

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
 //  Process cancel request (Return w/o changes) from Proshop_waitlist_slot (HTML)
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
    String index = req.getParameter("index");          //  index value of day (needed by Member_sheet when returning)
    String course = req.getParameter("course");        //  name of course (needed by Member_sheet when returning)
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to
    String day = req.getParameter("day");              //  name of the day
    String list_name = req.getParameter("name");       //  name of wait list for sending back to dsheet

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
            count = pstmt1.executeUpdate();
            pstmt1.close();

        }
        catch (Exception ignore) {
        }
    }
    
    //
    //  Prompt user to return to Proshop_sheet or Proshop_waitlist    (CHANGED 888 was return to Proshop_searchmem)
    //
    //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
    //
    if (index.equals( "888" )) {       // if originated from Proshop_main

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
      out.println("<Title>Proshop Tee Slot Page</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?search=yes\">");
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
      out.println("<BR><BR>Thank you, the signup has been returned to the system without changes.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else if (list_name != null) {
        
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
        out.println("<title>Proshop Tee Slot Page</title>");
        out.println("<meta http-equiv=\"refresh\" content=\"1; url=Proshop_dsheet?mode=WAITLIST&index=" + index + "&course=" + course + "&returnCourse=" + returnCourse + "&name=" + list_name + "\">");
        out.println("</HEAD>");
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
        out.println("<BR><BR>Thank you, the signup has been returned to the system without changes.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"Proshop_dsheet\" method=\"get\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"mode\" value=\"WAITLIST\">");
        out.println("<input type=\"hidden\" name=\"name\" value=\"" + list_name + "\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        
        out.println("</form></font>");
    //}
    //out.println("</CENTER></BODY></HTML>");
    //out.close();
        
   } else {

        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
        }
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">");
        out.println("<title>Proshop Tee Slot Page</title>");
        out.println("<meta http-equiv=\"refresh\" content=\"1; url=Proshop_jump?index=" + index + "&course=" + course + "\">");
        out.println("</HEAD>");
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
        out.println("<BR><BR>Thank you, the notification has been returned to the system without changes.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("</form></font>");
    }
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
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + msg);
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }

 
 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1) {
    dbError(out, e1, "");
 }
 
 
 // *********************************************************
 //  Update players for a wait list entry
 // *********************************************************
 
 private void updateSignUpPlayers(int signup_id, String player_name, String old_player, String username, String cw, String oldcw, int p9hole, int oldp9hole, int pos, int guest_id, Connection con, PrintWriter out) {

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
                    "(wait_list_signup_id, username, cw, player_name, 9hole, pos, guest_id) VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                    "wait_list_signup_id = VALUES(wait_list_signup_id), " +
                    "username = VALUES(username), " +
                    "cw = VALUES(cw), " +
                    "player_name = VALUES(player_name), " +
                    "9hole = VALUES(9hole), " +
                    "guest_id = VALUES(guest_id)");

            pstmt.clearParameters();
            pstmt.setInt(1, signup_id);
            pstmt.setString(2, username);
            pstmt.setString(3, cw);
            pstmt.setString(4, player_name);
            pstmt.setInt(5, p9hole);
            pstmt.setInt(6, pos);
            pstmt.setInt(7, guest_id);
            pstmt.executeUpdate();
            pstmt.close();

        } else {

            out.println("<!-- UNCHANGED player"+pos+" [" + username +  " | " + player_name + "] -->");
        }
        
    } catch (Exception e) {
        
        dbError(out, e, "Error updating player" + pos + " info for wait list entry " + signup_id + ".");
    }
 }
 
 
 // *********************************************************
 //  Return to Proshop_waitlist_slot (only option)
 // *********************************************************
/*
 private void return1(PrintWriter out, parmSlot slotParms) {

   out.println("<font size=\"2\">");
   out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + slotParms.wait_list_id + "\">");
   out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
   out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
   out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");;
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
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
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
   out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
   out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotParms.suppressEmails + "\">");
   out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }
*/
 
 private void returnToSlot(PrintWriter out, parmSlot slotParms) {
     
     // call other method and pass default options so the user can not override
     returnToSlot(out, slotParms, false, "", 0);
     
 }
 
 
 private void returnToSlot(PrintWriter out, parmSlot slotParms, int skip) {
     
     // call other method and pass default options so the user can override
     returnToSlot(out, slotParms, true, "", skip);
     
 }
 

 // *********************************************************
 //  Return to Proshop_waitlist_slot
 // *********************************************************

 private void returnToSlot(PrintWriter out, parmSlot slotParms, boolean allowOverride, String user, int skip) {



   if (allowOverride) {
       out.println("<BR><BR>Would you like to override this and allow the player?");
   }

   out.println("<BR><BR>");

   //
   //  Prompt user for return
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + slotParms.wait_list_id + "\">");
   out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
   out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
   out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
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
   out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
   out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
   out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
   out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
   out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
   out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
   out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotParms.suppressEmails + "\">");

   if (!allowOverride) { // add check here to see if user has permission to override restrictions

      out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else {

      out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      
      out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
      
      out.println("<input type=\"hidden\" name=\"skip\" value=\"" +skip+ "\">");
      
      out.println("<input type=\"hidden\" name=\"waitListId\" value=\"" + slotParms.wait_list_id + "\">");
      out.println("<input type=\"hidden\" name=\"signupId\" value=\"" + slotParms.signup_id + "\">");
      out.println("<input type=\"hidden\" name=\"ok_stime\" value=\"" + slotParms.ok_stime + "\">");
      out.println("<input type=\"hidden\" name=\"ok_etime\" value=\"" + slotParms.ok_etime + "\">");
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
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" + slotParms.guest_id1 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" + slotParms.guest_id2 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" + slotParms.guest_id3 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" + slotParms.guest_id4 + "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" + slotParms.guest_id5 + "\">");
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
      out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
      out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
      out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
      out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotParms.suppressEmails + "\">");

      //if (skip == 12) {

         out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
         out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
         out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
         out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
         out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
      //}
      out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\"></form>");
   }
 
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Return to Proshop_waitlist_slot
 // *********************************************************
/*
 private void returnToSlot_mem(PrintWriter out, parmSlot slotParms) {

   //
   //  Return to _slot to change the player order
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"Proshop_waitlist_slot\" method=\"post\" target=\"_top\">");
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
*/
}
