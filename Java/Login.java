/***************************************************************************************     
 *   Login:  This servlet will process the initial login page's form request.
 *           It will process the 4 types of logins; support, admin, proshop and members.
 *
 *   called by:  login.jsp, mlogin.jsp and directly
 *
 *
 *   created: 11/20/2001   Bob P.
 *
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        6/29/10   Change the error message in remoteUser if a username is not provided.  Inform user that they must login to their website.
 *        6/29/10   Added stripAlpha and stripDash options for handling remote users. stripAlpha will remove a non-numeric character from
 *                  the end of the username, and stripDash will remove a dash and anything that follows it from the username
 *        5/21/10   Add club name and other info to remoteUser error message (Username not provided).
 *        5/11/10   Stone Oak CC (stoneoakcountryclub) - strip the spouse extension from the member number for Jonas.
 *        4/21/10   Mayfield Sandridge - strip the spouse extension from the member number for Jonas i/f.
 *        4/21/10   Reject remote caller if username not provided.
 *        4/05/10   If default_activity_id has been set to -999 but primary interface does NOT break out to Member_msg (no family members present),
 *                  set default_activity_id back to 0
 *        4/05/10   Added a new caller (TAHOEDONNERCA530) for Tahoe Donner for seamless interface with their site
 *        3/10/10   Change to activity parameter.  Activity_id of desired default_activity will now be passed.  If not default is passed, then the member's default is used
 *        2/16/10   If activity=tennis param passed with remote user and this user will use primary interface, store the activity id as a negative value in the session
 *        2/11/10   Updated remoteUser to look for 'activity=tennis' and if present default them into FlxRez
 *        1/26/10   Member users - check if Mobile supported (new flag in club5).
 *        1/25/10   In remoteUser - save the caller in club5 if not already set so we know which clubs are using the seamless
 *                  interface and which website they use.
 *        1/22/10   Denver CC - skip the Mobile announcement (msg002) - they don't want members to bypass website. (** Replaced by mobile check)
 *        1/21/10   Update the Help process from the Login pages, and add help for mobile users.
 *        1/15/10   Add message #2 to announce the Mobile Interface.
 *        1/14/10   Change the login for a Mobile member user to check the mobile credentials in member2b. Also,
 *                  bump counter if member mobile login and send mobile password to member if prompted.
 *       12/18/09   Reset the member message feature to display a message regarding the partner list updates.
 *       11/24/09   Allow more values in the mobile parm so we can indicate the level of support for the device.
 *       11/06/09   Remove logerror call in doGet when member enters incorrectly - flooding the log.
 *                  We can address this later - not a big deal.
 *       10/22/09   Do not display iCal prompt for members of The Reserve Club
 *       10/19/09   Remove the Monson website clubs from the filter in doGet as Kelly fixed all his links to not use Get.
 *                  Also in remoteUser add check for caller=ForeTees1298 and if so, force the semaless settings to Primary
 *                  mode so we can bypass the CE links while the lcubs uses both interfaces to test.
 *       10/05/09   Allow certain clubs' websites to access doGet because the don't know how to change it to doPost.
 *        9/28/09   Change check in remoteUser for email prompt from MF to rsync.
 *        9/28/09   Change forms that call Login from within to use post instead of get.
 *        9/21/09   Add processing doGet to allow links from CE websites (CE Bypass) that are not forms.
 *        9/16/09   Woodlands CC - leave the welcome message a little longer.
 *        8/18/09   Add users default activity_id to session block (modified 9/14/09)
 *        7/24/09   Add support for mobile devices - from Mobile Login page.
 *        7/22/09   Enable event processing from Members First
 *        7/17/09   When asking for iCal preferences upon login, only set emailOpt to 1 if user has iCal1 or iCal2 set to yes
 *        5/29/09   Pass additional information if logging in via an email dining link
 *        5/22/09   Add one-time prompting for iCalendar attachments during login
 *        5/08/09   Add new 2 minute timer call in init method.  Start with 3 minutes to offset with 
 *                  original 2 minute timer.
 *        3/05/08   Add new external login method for process logins from our email messages (processExtLogin)
 *       10/14/08   Brooklawn - prompt remote member to verify/change email address if it has bounced (case 1568) - on hold.
 *        9/29/08   Removed Temp Royal Oaks Dallas (roccdallas) changes
 *        9/22/08   Added temp login fix for Royal Oaks Dallas (roccdallas), to be removed after their site maintenence is complete
 *        8/13/08   Added new logging method to track login attemps
 *        7/17/08   Mesa Verde (MF Roster Sync) - allow remote users to add/change their email addresses.
 *        7/11/08   Added check for inactive proshop users and restrict access if inactive
 *        6/26/08   Move the clubcorp_ conversion up to doPost so we can pass the clubname test there.
 *        6/17/08   MF remoteUser - add conversion of ClubCorp club names (i.e.  clubcorp_132 = trophyclub).
 *        6/05/08   Add systemTest method (check for db connectivity and nfs access)
 *        6/03/08   Add TimerGHIN to init method (new timer for GHIN updates).
 *        6/02/08   Move member timeout value to SystemUtils.MEMBER_TIMEOUT
 *        5/28/08   Add debug output to Login when club name not found in v5 table
 *        5/19/08   Enhanced email bounced notification.  Now prompts user to enter new address and will clear their bounced indicator
 *        4/20/08   Added development logins to demo sites - member4tea any pass will login as random member
 *        4/10/08   WingedFoot - direct members to their guest quota report after login Case# 1415
 *        4/02/08   Remote Logins - add parms passed from web site to the session log entry.
 *        3/27/08   Brookings CC - Add support for their last names which may contain a trailing # sign in their last names
 *        3/03/08   Add support for checking v5.clubs.inactive flag
 *        2/07/08   Comment out called to checkName for MEMFIRST clubs in remoteUser method
 *       11/02/07   Merion - move custom code from CE area to common area (not sure why it was there to begin with).       
 *       10/14/07   MF - add eventname= parm to interface so user can select an event on their web site calendar
 *                       and go directly to our event signup (added, but not used yet). 
 *       10/02/07   Merion - strip extensions from member numbers in remoteuser (A140-1).       
 *        8/12/07   New Canaan - Add trap for failed logins to proshop
 *        7/19/07   Check new 'billable' flag in member2b.  Do not allow if not set (member is excluded).
 *        7/17/07   Add the club's Roster Sync indicator to the session for admin users.
 *        7/14/07   Add an option in remoteUser that will strip any end characters from the username.
 *        6/29/07   Add try/catches to init method when setting timers.  Log error and throw exception if one received.
 *        6/11/07   Remove BUZWEB form remote userlist.
 *                  Also, remove call to scanTee when pros log in.
 *        6/05/07   Changed remoteUser processing to get some interfaceparms from club5 instead of the web site.
 *        5/09/07   Removed building of daysArray - no longer stored in session block
 *        5/08/07   Improve the remote user failure session logging - add more info.
 *        5/01/07   Add new interface parm to strip the leading zeros from the username (stripzero=).
 *        4/19/07   Strip the leading zeros from the username for IntraClub caller (for Sunset Ridge).
 *        4/09/07   Congressional - custom for primary interface - do not include names of dependents (case #1020).
 *        4/06/07   Check new 'inact' flag in member2b.  Do not allow if inactive.
 *        4/03/07   Correct the sql statement in processEmail to prevent an email address of zero.
 *        3/28/07   Add a generic web site id for small one time web sites (seamless interface).
 *        2/15/07   Comment out the member welcome message processing as we don't need it now (save for future).
 *        2/15/07   Greeley CC - add a 'G' to the member number received from web site.
 *        2/06/07   Change index2.htm to index.htm to allow for new login pages.
 *        1/30/07   Add support for South Bay Design - Hillcrest GC in St. Paul.
 *        1/28/07   Allow member to add their email addresses, if none, on Login.
 *        1/11/07   El Niguel - change days in adv parms for Adult Females on Tues.
 *       10/20/06   Do not display tee times message if AGT member.
 *       10/16/06   Remove call to initLogins in init method as this can hang the init sequence
 *                  and therefore it can hang all processing.
 *       10/13/06   Add more information to error msg when remote user fails.
 *       10/09/06   Changes for bounced email address flagging
 *        9/12/06   Add support for Legendary Marketing - Harkers Hollow.
 *        9/12/06   Add support for Keating - Glen Oak CC.
 *        8/29/06   Add support for City Star - Colorado Springs CC.
 *        7/20/06   Add session vars for TLT system and for mobile users.
 *        7/19/06   Save timer value for X timer in init method (safety check for timers).
 *        7/12/06   Strip the leading zeros from the username for Jonas caller (for Mendakota).
 *        6/24/06   Improve some of the error messages returned to user, especially remote users.
 *        6/21/06   Added recordLoginStat method for new tracking of login statsistics
 *        6/15/06   Move call to scanTee on proshop user method so it runs in background.
 *        6/14/06   Scioto - custom days in adv for Spouse member types.
 *        6/01/06   In remoteUser, if primary=yes make sure the session gets built properly in case
 *                  there is only one member in the family - get the username and save it in the session.
 *                  Also, build and set the daysArray.
 *        5/02/06   Remove synchronized statements.
 *        5/02/06   Change calls to sessionLog to not include the pw if successful.
 *        5/01/06   Add support for Jay Van Vark caller (web site provider for Rancho Bernardo).
 *        4/25/06   Add support for Winding Oak caller (web site provider for Wayzata).
 *        4/10/06   Add support for Lightedge - Davenport CC.
 *        3/08/06   Add 2 new fields to the proshop session for member classes (mtypeOpt and mshipOpt).
 *        3/07/06   Oswego Lake (ZSmart) - trim the username to a max length of 15 chars (was 10).
 *        3/06/06   Change calls to sessionLog to include the connection.
 *        3/01/06   Strip the leading zeros from the memNum for CSG caller (for Hurstbourne).
 *        2/17/06   Add support for LogiSoft - Locust Hill CC.
 *        2/07/06   Add webid field to member2b for web site interface.  If web site interface is added
 *                  after our site is running, they can provide us with their member ids and we map them.
 *       11/09/05   Add support for MeritSoft (BuzWeb) - Providence CC.
 *       10/18/05   Add support for Cherry Hills caller (they do their own).
 *       10/06/05   Add support for Nakoma caller (web site provider for Nakoma - they do their own).
 *        9/29/05   Add support for Sedona Management Group caller (web site provider for Fairwood).
 *        9/22/05   Add a login log to track all logins.
 *        9/10/05   Medinah - allow Social Regular and Social Reg Probationary members to login.
 *        9/07/05   Oswego Lake (ZSmart) - trim the username to a max length of 10 chars (to match limit of password).
 *        9/02/05   Validate member email addresses when they login.  Post warning if invalid.
 *        7/07/05   Add support for email parms from MFirst.
 *        6/16/05   Add support for Grapevine caller (web site provider for Brooklawn).
 *        6/13/05   Add support for Flexscape caller (web site provider for Sciotto & Bishops Bay).
 *        6/13/05   Add support for ZSmart Web Marketing caller (web site provider for Oswego Lake).
 *        5/21/05   Custom for Medinah CC - If ARR member, change days in adv to 30 for all days of week.
 *        5/20/05   Medinah - do not allow dependents or social members to login.
 *        5/01/05   Add counters for logins - in SystemUtils.
 *        4/26/05   Add mtype to parms saved in the session for members.
 *        4/18/05   Add sales credentials and login, change support pw.
 *        4/10/05   Wichita - change web site providers from Gardner to JCook.
 *        4/07/05   Add mnum=yes for primary interface so web site can provide mNum instead of username.
 *        3/14/05   Add support for Gary Jonas Webs caller (web site provider for Interlachen & Meadowbrook).
 *        3/03/05   Add support for Gold Star Webs caller (web site provider for Rochester).
 *        2/21/05   Add V5 Changes page to welcome for members.
 *        2/14/05   Change SystemUtils.Connect to dbConn.Connect to allow 2 seperate servers.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/21/05   Strip the leading zeros from the memNum for Web Sites 2000 caller.
 *        1/16/05   Add support for Web Sites 2000 caller (web site provider for Ironwood).
 *        1/06/05   Add support for Hidden Valley S/W caller (web site provider for North Ridge).
 *        1/04/05   Ver 5 - allow for more than one admin login.
 *       12/15/04   Add trace for Admin and Proshop Logins from Old Oaks.
 *       11/22/04   Add support for Primary-Only logins from CE.  Only the primary member resides
 *                  in their database so we must prompt for the family member.
 *       10/28/04   Add support for FELIX caller (web site provider for White Manor).
 *       10/25/04   Add support for MEDIACURRENT caller (web site provider for Piedmont Driving Club).
 *       10/11/04   Ver 5 - save lottery support indicator in the session for proshop users.
 *        9/22/04   Add new parm from clubs' index2.htm - zipcode= (for weather link).
 *        9/16/04   Ver 5 - change getClub and getDaysInAdv from SytemUtils to common.
 *        9/03/04   Save the club's POS Type for _slot processing.
 *        5/24/04   Save the member's mode of trans preference for Member_slot.
 *        5/19/04   Change member timeout to 5 minutes (from 8) and pro from 8 hrs to 4 hrs.
 *        5/05/04   Add INTRACLUB for Old Oaks and Bellerive.  Modify no-cache settings.
 *        5/04/04   Add 'cache-control' to responses to prevent session mangling.  Some proxy
 *                  servers were caching the pages and therefore interfering with the cookies.
 *        4/27/04   Add support for VELOTEL caller (web site provider for Hazeltine).
 *        4/22/04   RDP Add custom processing for Hazeltine Natl.  Allow women 14 days in adv.
 *        4/14/04   Change member session timeouts from 10 mins to 8 mins.
 *        4/08/04   Add support for GARDNER caller (web site provider for Wichita).
 *        4/07/04   Add support for PRIVATEGOLF caller (web site provider for Fort Collins).
 *        4/07/04   Add support for CLUBESSENTIAL caller (web site provider for Wakonda).
 *        2/09/04   Add support for NEMEX caller (web site provider for Forest Hills).
 *        1/21/04   Allow for 'days in adv' parms on a per membership and per day basis.
 *        1/08/04   Add DaysAdv array processing for members - establish and save
 *                  the days in advance values to make calendar building quicker.
 *       12/31/03   Add MONSON web site caller.
 *       11/18/03   Version 3 - add hotel user logins.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/29/02   Init the connection holder field in Support's session block so new
 *                  connection is made when logging into new club.
 *
 *       12/04/02   Enhancements for Version 2 of the software.
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
import com.foretees.common.DaysAdv;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.medinahCustom;
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.StringEncrypter;
import com.foretees.common.Utilities;


public class Login extends HttpServlet {

    
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 String support = SystemUtils.support;    // class variables that never change
 String sales = SystemUtils.sales;
 String admin = SystemUtils.admin;
 String proshop = SystemUtils.proshop;
 String id = SystemUtils.id;
        
 //
 // The following password must be maintained here so we can
 //  login and initialize the databases when the site is installed.
 //
 String passwordSup = SystemUtils.passwordSup;       // password for support login...
 String passwordSales = SystemUtils.passwordSales;   // password for sales login...

 String omit = "";             // ommitted

 String iCalNotice = "<p><b>Notice:</b> We've added a new feature that allows you to receive iCalendar files along with your email notifications.&nbsp; " +
                     "You may choose to receive them at either email address using the options below, or at anytime by clicking on the Settings tab from within ForeTees.&nbsp; " +
                     "<a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>Click here for more information regarding iCalendar.</a>" +
                     "<br><br><u>BlackBerry Users:</u> If your tee time notifications from ForeTees appear to have an empty message body, you will need to disable iCal attachments to that email address.&nbsp; " +
                     "</p>";
 
 //
 //  The following is used for member messages to be displayed once when a member logs in. (******* See also Member_msg *********)
 //
 String previous_message = "msg001";      // previous message that was shown
 String latest_message = "msg002";        // message we want to show now


 
 static final int MEMBER_TIMEOUT = SystemUtils.MEMBER_TIMEOUT;
 
 

 //*****************************************************
 // Perform initialization processing when server loads
 // this servlet for the first time.
 //*****************************************************
 //
 public void init()
         throws ServletException {


   String errorMsg = "";
     
  
   //
   //  set a 2 minute system timer to check teecurr for inactive sessions
   //
   try {

      minTimer t2_timer = new minTimer();

      minTimer2 t2_timer2 = new minTimer2();      // start a 2nd timer to split the load

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting minTimer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }

   //
   //  set a 60 minute system timer to check teecurr for X's
   //
   try {

      min60Timer t4_timer = new min60Timer();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting min60Timer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }

   //
   //   set timer to make sure we keep building new sheets daily
   //
   try {

      TeeTimer t_timer = new TeeTimer();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TeeTimer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }


   //
   //   set timer to send current tee sheet to Pro Shops (2 times per day)
   //
   try {

      TsheetTimer t3_timer = new TsheetTimer();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TsheetTimer. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }

   //
   //  Set the Roster Sync Timer for tonight (4:45 AM - check for MFirst Rosters)
   //
   try {

      TimerSync sync_timer = new TimerSync();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TimerSync. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);                                       // log it and continue
   }


   //
   //  Set the GHIN Timer for tonight (3:45 AM)
   //
   try {

      TimerGHIN ghin_timer = new TimerGHIN();

   }
   catch (Exception e) {

      errorMsg = "Error in Login.init setting TimerGHIN. Exception = " + e.getMessage();
      SystemUtils.logError(errorMsg);   // log it and continue
   }


   //
   //  Set the date/time when the 60 min timer (Xtimer) should expire by next (safety check to ensure timers keep running)
   //
   Calendar cal = new GregorianCalendar();   // get todays date

   cal.add(Calendar.MINUTE,90);              // roll ahead 90 minutes to give plenty of time

   long year = cal.get(Calendar.YEAR);
   long month = cal.get(Calendar.MONTH) +1;
   long day = cal.get(Calendar.DAY_OF_MONTH);
   long hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
   long min = cal.get(Calendar.MINUTE);

   //
   //  create date & time stamp value (yyyymmddhhmm) for compares
   //
   SystemUtils.min60Time = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // save date/time stamp
   

   //
   //  Throw an exception if one received above
   //
   if (!errorMsg.equals( "" )) {     
  
      throw new ServletException("ForeTees - Error setting timers in Login Init");
   } else {
       
       SystemUtils.logError("Tomcat Startup Completed."); 
   }
     
 }


 //
 // Process external logins from our emails - currently used for dining requests and unsubscribes
 //
 public void processExtLogin(HttpServletRequest req, HttpServletResponse resp) {

   
    resp.setHeader("P3P","CP=\"NOI DSP COR NID\"");
    resp.setHeader("Pragma","no-cache");                                      // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);    // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = null;

    try {
        out = resp.getWriter();
    } catch (Exception ignore) {
        return;
    }

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;
    
    String club = "";
    String user = "";
    String mtype = "";
    String mship = "";
    String caller = "";
    String jumpTo = "";
    String name = "";
    String errMsg = "";
    String els = "";        // encoded login string
    String rls = "";        // raw (dencoded) login string
    String date = "";        // contains the date passed from the email dining link (date of booking)
    String customId = "0";   // would contain the custom messsage id from the dining link that was clicked on
    
    boolean fatalError = false;
    
    // caller indicates what feature this link is for (unsubscribe email link, dinning, ?)
    if (req.getParameter("caller") != null) {

        caller = req.getParameter("caller");
    }

    // if customId is here, lets retain pass it through
    if (caller.equals("dining")) {
        
        if (req.getParameter("customId") != null && !req.getParameter("customId").equals("")) {

            customId = req.getParameter("customId");
        }
        
        if (req.getParameter("date") != null && !req.getParameter("date").equals("")) {
            
            date = req.getParameter("date");
        }
    }

    if (req.getParameter("els") != null) {

        els = req.getParameter("els");       // get the encoded login string
        
        try {

            StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );
            rls = encrypter.decrypt( els );
        
        } catch (Exception e) {
            out.println("Decrypt ELS Error: " + e.getMessage() );
        }

        try {

            StringTokenizer tok = new StringTokenizer( rls, ":" );

            club = tok.nextToken();
            user = tok.nextToken();

        } catch (Exception exc) {

            // if we got here then the decrypted string didn't have an colon in it or was null.
            out.println("ELS Token Error: " + exc.getMessage() );

            out.println("<p>It appears the link contained in your email is not formatted properly.  Please contact support@foretees.com for assistance.</p>");

            return;
        }

    }/* else {

        //
        // TEMP - pass user & club to build ELS for testing
        //

        user = req.getParameter("user");
        club = req.getParameter("club");

        rls = club + ":" + user;

        try {

            StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );
            els = encrypter.encrypt( rls );

            out.println("Encrypted");

        } catch (Exception e) {
            out.println("Encrypt Error: " + e.getMessage() );
        }

    }
    
    out.println("els=" + els);
    out.println("rls=" + rls);
    out.println("club=" + club);
    out.println("user=" + user);    
    */

    //
    //  Make sure the club requested is not inactive
    //
    try {

        con = dbConn.Connect(rev);       // get a connection for this version level

        stmt = con.prepareStatement (
            "SELECT fullname FROM clubs WHERE clubname = ?");

        stmt.clearParameters();        // clear the parms
        stmt.setString(1, club);
        rs = stmt.executeQuery();

        if (!rs.next()) {          // if club not found in this version

            errMsg = "Error in Login.processExtLogin - club name invalid, club=" +club+ " was received for " +user;
            fatalError = true;
        }
        stmt.close();              // close the stmt

    } catch (Exception exc) {
        
        errMsg = "Error in Login.processExtLogin - Unable to Connect to Database.  Could be an invalid clubname (club=" +club+ ", user=" +user+ "). Error: " + exc.toString();
        fatalError = true;

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}
    }


    //
    // Load the JDBC Driver and connect to DB for this club
    //
    try {
        con = dbConn.Connect(club);
    } catch (Exception exc) {

        errMsg = "Login.processExtLogin - Unable to Connect to club: " +club+ "). Error: " + exc.toString();
        fatalError = true;
    }



    //
    //  Make sure the member is valid and active
    //
    try {

        stmt = con.prepareStatement (
            "SELECT CONCAT(name_first, ' ', name_last) AS fullName, m_ship, m_type FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");

        stmt.clearParameters();        // clear the parms
        stmt.setString(1, user);
        rs = stmt.executeQuery();

        if (!rs.next()) {          // if user not found

            errMsg = "Error in Login.processExtLogin - user not found or inactive: user=" +user+ ", club=" +club;
            fatalError = true;
        } else {
            name = rs.getString("fullname");
            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type
        }
        stmt.close();              // close the stmt

    } catch (Exception exc) {

        errMsg = "Error in Login.processExtLogin - Unable to verify user. (club=" +club+ ", user=" +user+ ", els=" +els+ ", rls=" +rls+ "). Error: " + exc.toString();
        fatalError = true;
        
    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}
    }


    //
    // Alert user if login processing failed
    //
    if (fatalError) {

        out.println(SystemUtils.HeadTitle("Invalid Login"));
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<p>&nbsp;</p><p>&nbsp;</p>");
        out.println("<BR><H2>Access Rejected</H2><BR>");
        out.println("<BR>Sorry, we cannot complete the connection to ForeTees due to an error.");
        out.println("<BR>Exception: " +errMsg+ "<BR>");
        out.println("<BR>Please contact support@foretees.com or try again later.  Thank you.<BR>");
        out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
        out.println("</CENTER></BODY></HTML>");
        out.close();

        SystemUtils.logError(errMsg);                           // log it

        if (con != null) {

            try { con.close(); }
            catch (Exception exp) { }
        }

        return;
        
        //SystemUtils.sessionLog("Member External Login Failed: " + rls, user, "", club, omit, con);
        //return;
    }


    //
    // IF HERE THEN LOGIN SUCCESSFUL
    //





    //
    //  Get the remote host id (for tracing client)
    //
    String remote_ip = req.getHeader("x-forwarded-for");
    if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

    // new stats logging routine
    recordLoginStat(1);

    //
    //  Trace all login attempts
    //
    SystemUtils.sessionLog("Member External Login Successful", user, "", club, omit, con);         // log it - no pw

    recordLogin(user, "", club, remote_ip, 1);

    HttpSession session = req.getSession(true);     // Create a new session object

    ConnHolder holder = new ConnHolder(con);        // create a new holder from ConnHolder class

    session.setAttribute("connect", holder);        // save DB connection holder
    //session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
    session.setAttribute("ext-user", user);         // save username as ext-user (so that verifyMem would fail)
    session.setAttribute("club", club);             // save club name
    session.setAttribute("mship", mship);           // save member's mship type
    session.setAttribute("mtype", mtype);           // save member's mtype
    session.setMaxInactiveInterval(5 * 60);         // set the timeout to 5 minutes


    // based upon caller - jump to desired page
    if (caller.equals("dining")) {

        jumpTo = "Member_dining";

    } else {

        jumpTo = "Unsubscribe"; // default

    }

    out.println("<HTML><HEAD><TITLE>Member External Login Page</TITLE>");

    //out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" + rev + "/servlet/" + jumpTo + "\">");

    out.println("</HEAD>");
    out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>Limitied Member Access Accepted</H2><BR>");
    out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

    out.println("<font size=\"3\">");
    out.println("<BR>Welcome <b>" + name );

    out.println("</b><BR><BR>");
    out.println("Please note that this session will terminate if inactive for more than <b>5</b> minutes.<BR><BR>");
    //out.println("<br><br>");

    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/" + jumpTo + "\">");

    if (caller.equals("dining")) {
        
        out.println("<input type=hidden name=\"usr\" value=\"" + user + "\">");
        out.println("<input type=hidden name=\"ext-dReq\" value=\"\">"); // caller?
        out.println("<input type=hidden name=\"dReq\">"); // caller?
        out.println("<input type=hidden name=\"date\" value=\"" + date + "\">");
        out.println("<input type=hidden name=\"caller\" value=\"email\">");
        out.println("<input type=hidden name=\"customId\" value=\"" + customId + "\">");
    }

    out.println("</font></td></tr></table>");
    out.println("<br>");
    out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</input></form></font>");
    out.println("</CENTER></BODY></HTML>");
    out.close();

 }


 //*****************************************************
 //  Process doGet - request for help
 //*****************************************************
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   // Check to see if we have an incoming external login
   //
   if (req.getParameter("extlogin") != null) {

       processExtLogin(req, resp);
       return;
   }

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   if (req.getParameter("verify") != null) {      // Paul's verify script - for testing after a bounce

      systemTest(req, out);
      return;
   }
   
   //
   //  Check if this is a request for help
   //
   if (req.getParameter("help") != null) {
     
      if (req.getParameter("browser") != null) {

         String browser = req.getParameter("browser");       // get browser selected
      
         if (browser.equals( "aol8" )) {
           
            helpAOL8(out);        // output help page
            return;
         }
         if (browser.equals( "ie6" )) {

            helpIE6(out);        // output help page
            return;
         }
      }
      
      //
      //  Help requested - Check if call from Member_services to process a request to send email with Mobile password
      //
      if (req.getParameter("mobile") != null) {         // if request for mobile pw

         doPost(req, resp);                             // call doPost processing
         return;
      }      
      

      String club = req.getParameter("clubname");       // which club request came from

      String club2 = club;                             // normally the same value

      if (req.getParameter("club2") != null) {         // if club2 specified (Greeley CC)

         club2 = req.getParameter("club2");            // get it (name of club site to return to)
      }

      
      if (req.getParameter("mobilehelp") == null) {         // if request is NOT from a Mobile login page

         //
         //  Output a page to provide help and a form to request the member's password
         //
         out.println(SystemUtils.HeadTitle("Login Help"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<br><br>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Forgot your User Name (Login Id)?<br><br>");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"230\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("The User Name is normally either the <b>Local Number</b> assigned to you and used to enter scores on the Handicap System, ");
         out.println("or it is the <b>Member Number</b> that is used for billing purposes.");
         out.println("<br><br>");
         out.println("Your password should have been provided to you by the club.  You have the ability to change this once you are logged in.");
         out.println("<br><br>");
         out.println("Please contact your club's golf professionals for further assistance.");
         out.println("</font></p>");
         out.println("<A HREF=\"/" +club2+ "/index.htm\">Back to Login</A><br><br>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<hr width=\"40%\">");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Forgot your password?<br><br>");
         out.println("Use the form on the right to have the system email your password. ");
         out.println("For this to work you must have already entered your email address in the system via the 'Settings' tab.");
         out.println("<br><br>");
         out.println("If you have not already entered your email address, please contact your club's golf professionals for assistance.");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"220\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("Enter your User Name (login id) and click on 'Send Password'. ");
         out.println("You will then receive an email containing your password.<br><br>");
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
         out.println("<b>User Name:</b><br>");
         out.println("<input type=\"text\" name=\"user_name\" size=\"15\" maxlength=\"15\">");
         out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +club+ "\">");
         out.println("<input type=\"hidden\" name=\"club2\" value=\"" +club2+ "\">");
         out.println("<input type=\"hidden\" name=\"help\" value=\"yes\">");
         out.println("<br><br>");
         out.println("<input type=\"submit\" value=\"Send Password\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</font></p>");
         out.println("<A HREF=\"/" +club2+ "/index.htm\">Back to Login</A>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<hr width=\"40%\">");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Able to login, but cannot access the tee sheets?");
         out.println("<br><br>");
         out.println("Receiving an 'Access Error' after logging in?");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"330\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("There are several issues that can cause this problem:  ");
         out.println("<br><br>");
         out.println("<b>A.</b> Your browser's security setting is set too high (blocks all cookies). ");
         out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
         out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
         out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
         out.println(" To check if this is your problem, click on your browser type: ");
         out.println("<br><br>");
         out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=ie6\" target=\"_blank\">Microsoft Internet Explorer (IE) 6.x</a>");
         out.println("<br><br>");
         out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=aol8\" target=\"_blank\">AOL 8.0 (or higher)</a>");
         out.println("<br><br>");
         out.println("If your browser is not listed above, then click on the 'Help' tab in your browser");
         out.println(" and search for 'cookie settings'.  You may also need to upgrade to a more current version");
         out.println(" (go to your browser provider's web site for information on upgrades).");
         out.println("<br><br><br>");
         out.println("<b>B.</b> It's possible that one or more of your internet files have been corrupted. ");
         out.println("These are files that your browser saves for quicker access to web sites that you frequent. ");
         out.println("To correct this problem, please try the following: ");
         out.println("<br><br>");
         out.println("For Microsoft Internet Explorer 6 <br><br>");
         out.println(" 1.  click on 'Tools'<br>");
         out.println(" 2.  select 'Internet Options'<br>");
         out.println(" 3.  click on the 'General' tab<br>");
         out.println(" 4.  click on the 'Delete Cookies' button, then select 'Ok'<br>");
         out.println(" 5.  click on the 'Delete Files' button<br>");
         out.println(" 6.  select the option to 'Delete all offline content' and then click on 'Ok'<br>");
         out.println(" 7.  click on 'Ok' at the bottom of the Internet Options box<br>");
         out.println(" 8.  try ForeTees again<br>");
         out.println("<br>");
         out.println("For AOL (from AOL browser prior to logging in)<br><br>");
         out.println(" 1.  click on 'Settings' and then 'Preferences'<br>");
         out.println(" 2.  select 'Internet Properties'<br>");
         out.println(" 3.  click on the 'General' tab<br>");
         out.println(" 4.  click on the 'Delete Cookies' button<br>");
         out.println(" 5.  click on the 'Delete Files' button<br>");
         out.println(" 6.  select the option to 'Delete all offline content' and then click on 'Ok'<br>");
         out.println(" 7.  click on 'Ok' at the bottom of the Internet Properties box<br>");
         out.println(" 8.  try ForeTees again<br>");
         out.println("<br><br>");
         out.println("<b>C.</b> You may have a Firewall installed in your computer or network. ");
         out.println("If so, check the firewall settings to make sure that it is not blocking the use of cookies. ");
         out.println("<br><br>");
         out.println("<b>D.</b> If you are using Micorsoft's Internet Explorer you may need to update it. ");
         out.println("Microsoft has recently resolved some issues involving their privacy settings. ");
         out.println("You can update your browser free of charge at <A HREF=\"http://www.microsoft.com\">microsoft.com</A>. ");
         out.println("Select 'Downloads' or 'Windows Update' under <b>'Resources'</b> on the left side of the page (this may take several minutes).");
         out.println("</font></p>");
         out.println("<A HREF=\"/" +club2+ "/index.htm\">Back to Login</A><br><br>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("<hr width=\"40%\">");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("<p><b>");
         out.println("Have other problems, want to contact ForeTees?");
         out.println("</b></p>");
         out.println("</td>");
         out.println("<td width=\"10\">&nbsp;</td>");
         out.println("<td valign=\"top\" height=\"300\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
         out.println("<td width=\"10\">&nbsp;</td>");

         out.println("<td align=\"left\" width=\"280\">");
         out.println("<p>");
         out.println("<font face=\"arial\" size=\"2\">");
         out.println("We welcome all correspondence. ");
         out.println("Please let us know if you are having problems with ForeTees, or have any suggestions ");
         out.println("on how we might improve the system.");
         out.println("<br><br>If reporting a problem with the system, please include your name, club name and a detailed description");
         out.println("of the problem.");
         out.println("<br><br>Please DO NOT send us emails regarding your tee times.  Contact your golf shop for tee time related issues.");
         out.println("<br><br>");
         out.println("To contact ForeTees, please send an email to:<br><br>");
         out.println(" <a href=\"mailto:support@foretees.com\">support@foretees.com</a>");
         out.println("<br><br>");
         out.println("If you would like to see ForeTees at another club, please send an email to:<br><br>");
         out.println(" <a href=\"mailto:sales@foretees.com\">sales@foretees.com</a><br>");
         out.println("</font></p>");
         out.println("<br><A HREF=\"/" +club2+ "/index.htm\">Back to Login</A>");
         out.println("</td></tr>");
         out.println("</table>");

         out.println("</CENTER></BODY></HTML>");
         out.close();
         
      } else {
         
         out.println(SystemUtils.HeadTitleMobile("Login Help"));
         out.println("<div class=\"headertext\"> ForeTees Mobile Help </div>");
         out.println("<div class=\"smheadertext\">");
         
         out.println("<table width=\"75%\"><tr><td>");           
         out.println("You must use your Mobile username and password to login to this site.");
         out.println("</td></tr>");           
         
         out.println("<tr><td>");           
         out.println("If you have an email address in ForeTees and forgot your Mobile password, enter your Mobile username below and " +
                     "select 'Send Password'.<BR><BR>");
         
         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
         out.println("<b>User Name:</b><br>");
         out.println("<input type=\"text\" name=\"user_name\" size=\"15\" maxlength=\"15\">");
         out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +club+ "\">");
         out.println("<input type=\"hidden\" name=\"club2\" value=\"" +club2+ "\">");
         out.println("<input type=\"hidden\" name=\"help\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"mobile\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"mobilehelp\" value=\"yes\">");
         out.println("<br><br>");
         out.println("<input type=\"submit\" value=\"Send Password\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("</td></tr>");           
         
         out.println("<tr><td>");                            
         out.println("If you do not have Mobile credentials, or" +
                     " you cannot remember your Mobile username, then you must login to ForeTees from your PC and select the 'Settings' tab.");
         out.println("</td></tr>");           
         
         out.println("<tr><td>");                                     
         out.println("If you require further assistance, then please email ForeTees at support@foretees.com.  Please include a description " +
                     "of the problem and identify your mobile device.<br>");
         out.println("<br>Thank you!");
         out.println("</td></tr></table>");           
         
         out.println("<br><br><A HREF=\"http://m.foretees.com/" +club2+ "\">Back to Login</A>");
         out.println("</div></body></html>");           
         out.close();
         
      }           // end of IF mobile help
      

   } else {      // NOT a Help request
      
      //
      //  Login attempt is not from our login page or a formal website link (posted form).
      //  Make sure that this is only from a ForeTees sponsored link.
      //
      String club = "";
      String caller = "";
      String user = "";
      String referer = "";
      
      if (req.getParameter("clubname") != null) {

         club = req.getParameter("clubname");       // which club login came from
      }
      if (req.getParameter("caller") != null) {

         caller = req.getParameter("caller");
      }
      if (req.getParameter("user_name") != null) {

         user = req.getParameter("user_name");
      }
      if (req.getHeader("referer") != null) {

         referer = req.getHeader("referer");
      }

      //
      //  Now make sure they specified a club and caller
      //
      if (club.equals("") || caller.equals("")) {  
         
         rejectCaller(out);          // reject if unknown entry
         return;
      }

       
      /*
       * 
       * No-op this for now.  CE and some other website providers have not been able to correct all their 
       * links.  Also, some members enter w/o a referer because their system strips it.  We have eliminated 
       * most of the violators and will allow these for now.
       * 
      boolean rejectLogin = true;      // default to reject this login request
         
      //
      //  Now check for any callers that do not specify a club, or are not authorized to enter here (ForeTees1298 is the CE Bypass Link!!!)
      //
      if (caller.equals("ForeTees1298") || club.equals("whitemanor") || club.equals("edgewood") || 
              club.equals("mountvernoncc") || club.equals("ridgewaygolf") || club.equals("hazeltine")) {  

         rejectLogin = false;      // these are ok for now
      }
         
      if (rejectLogin == true) {  
        
         //
         //  referrer is the url of the website that initiated the link (if null, then user bookmarked the link)
         //
         //  REMOVED - some users were coming in w/o a referer even though they did indeed come from their website - this is not worth the trouble!
         //
         if (referer.equals("") || referer == null) {  

             rejectReferer(out);          // reject if not from a website or our login page
             return;
         }
      
         String logMsg = "Login Failed - Invalid Access to Login.doGet: Caller=" +caller+ ", Club=" +club+ ", Referer=" +referer;
         SystemUtils.logError(logMsg);                           // log it

         // (allow websites to fix their links)
         //rejectCaller(out);          // reject if unknown entry 
         //return;
      }
       */
     
      doPost(req, resp);      // call doPost processing
   }
 }


 //*****************************************************
 // Perform doPost processing - someone is logging in
 //*****************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
   
    
   //
   //  Set a P3P Compact Policy Statement in the HTTP Header.
   //  This informs the client of our intended use of the persistent cookie
   //  used to manage this session.
   //
   //  Definitions:
   //
   //      ALL (access=ALL):         We will provide access to ALL information collected.
   //      NOI (access=):            Web site does not collect identified data
   //      DSP (disputes=);          We will settle any disputes.
   //      COR (remedies=):          We will 'correct' any disputes
   //      NID (non-identifiable=):  This session cookie does not collect data and cannot identify the individual person  
   //      CURa (purpose=):          The information is used to complete the activity of the service (N/A if NID)          
   //      OUR (recipient=)          Only our service will process information received in cookie (N/A if NID)
   //      STP (retention=)          Information is retained for the 'stated purpose' (N/A if NID)
   //      PUR (categories=)         We may use the informaiton used to purchase a product or service (N/A if NID)
   //
//   resp.setHeader("P3P","CP=\"ALL DSP COR NID CURa OUR STP PUR\"");       // old version
   resp.setHeader("P3P","CP=\"NOI DSP COR NID\"");
     
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");                                      // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);    // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
     
   Connection con = null;                 // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   if (req.getParameter("verify") != null) {

      systemTest(req, out);
      return;
   }
   
   //
   // Get the username and password entered.........  
   //
   String username = "";
   String userpw = "";
   String club = "";
   String errMsg = "";
   
   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");       // which club login came from
   }
   if (req.getParameter("user_name") != null) {

      username = req.getParameter("user_name").trim();
   }

   //
   //   Check if member prompted for email address
   //                                                                           
   if (req.getParameter("message") != null && req.getParameter("email") != null) {

      processEmail(req, out);     // go process member 'continue' with possible email addresses
      return;
   }

   //
   //   Check if user is asking for password help
   //
   if (req.getParameter("help") != null) {
      
      boolean mobile = false;            // flag for request for mobile password   

      if (req.getParameter("mobile") != null) {

         mobile = true;        // indicate request is for mobile password (from Member_services)
      }

      userHelp(req, out, username, club, mobile);  // go process 'help' request
      return;
   }

   //
   //  normal foretees user
   //
   if (req.getParameter("password") != null) {

      userpw = req.getParameter("password").trim();
   }
      
   //
   // Make sure both were entered.......
   //
   if (req.getParameter("caller") == null && (username.equals( "" ) || userpw.equals( "" ))) {
     
     errMsg = "Username or Password not provided.";
     
     invalidLogin(errMsg, req, out, null);
     return;
   }
   
   if (username.equals( support )) {

     supportUser(req, out, userpw, username, club);   // go process 'support' user......

     return;
   }

   if (username.startsWith( sales )) {

     salesUser(req, out, userpw, username, club);   // go process 'sales' user......

     return;
   }

   //
   //  Make sure the club requested is currently running this version of ForeTees.
   //  The user may need to refresh the login page so they pull up the new page.
   //
   try {
       
      con = dbConn.Connect(rev);       // get a connection for this version level

      //
      //  If MFirst and a ClubCorp club, then we must convert the name (i.e. clubcorp_132 = trophyclubcc)
      //
      if (club.startsWith( "clubcorp_")) {       // if 'MembersFirst' and ClubCorp
     
         pstmt = con.prepareStatement (
                  "SELECT ft_name FROM clubcorp WHERE cc_name = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, club);
         rs = pstmt.executeQuery();

         if (rs.next()) {               

            club = rs.getString(1);    // get our name for this club (if not found it will fail below)
         }
         
         pstmt.close();             
      }


      pstmt = con.prepareStatement (
               "SELECT fullname, inactive FROM clubs WHERE clubname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, club);
      rs = pstmt.executeQuery();

      if (!rs.next()) {          // if club not found in this version

         out.println(SystemUtils.HeadTitle("Invalid Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p><p>&nbsp;</p>");
         out.println("<BR><H2>Login Rejected</H2><BR>");
         out.println("<BR>Your club is not yet authorized to access ForeTees.");
         out.println("<BR>The site must be completed before you can proceed.<BR>");
         out.println("<BR>Please try again later.  Thank you.<BR>");
         out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         out.println("<!-- " + club + " -->");
         out.close();
         pstmt.close();           // close the stmt
         con.close();
         return;                  // exit

      } else {

          // allow special proshop & adimn users to access inactive clubs
          if (rs.getInt("inactive") == 1 && !username.equalsIgnoreCase("proshop4tea") && !username.equalsIgnoreCase("admin4tea")) {

             out.println(SystemUtils.HeadTitle("Invalid Login"));
             out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
             out.println("<hr width=\"40%\">");
             out.println("<p>&nbsp;</p><p>&nbsp;</p>");
             out.println("<BR><H2>Site Unavailable</H2><BR>");
             out.println("<BR>Your club is no longer authorized to access ForeTees.");
             out.println("<BR>Please contact your club managment for more information.<br><br>Thank you.<BR>");
             out.println("</CENTER></BODY></HTML>");
             out.close();
             pstmt.close();           // close the stmt
             con.close();
             return;                  // exit

          }
      }

      pstmt.close();              // close the stmt
      con.close();                // close the connection
      
   }
   catch (Exception exc) {
      // Error connecting to db....
      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }


   //
   //   Check if user came from Club's Web Site provider (AMO or MembersFirst, etc.)
   //
   if (req.getParameter("caller") != null) {

      remoteUser(req, out, username, club);  // go process 'remote' user......
      return;
   }

   if (username.startsWith( admin )) {

     adminUser(req, out, userpw, username, club);   // go process 'admin' user ........
     return;
   }

   if (username.startsWith( proshop )) {

     proshopUser(req, resp, out, userpw, username, club);  // go process 'proshop' user......
     return;

   } else {

     memberUser(req, out, username, userpw, club);   // go process 'member' user.......
   }
 }

 // *********************************************************
 // Process user = SUPPORT...(DB's may not exist yet!!!).
 // *********************************************************

 private void supportUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   if (pw.equals( passwordSup )) {

     HttpSession session = req.getSession(true);  // Create a session object

     ConnHolder holder = null;                   // no con yet, get new one when needed

     session.setAttribute("connect", holder);    // clear connection holder so new one is allocated
     session.setAttribute("user", user);         // save username
     session.setAttribute("club", club);         // save club name
     session.setAttribute("caller", "none");     // save caller's name
     // set inactivity timer for this session (30 mins)
     session.setMaxInactiveInterval(30*60);

     out.println("<HTML><HEAD><Title>Support Login Page</Title>");
     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/support_main.htm\">");
     out.println("</HEAD>");
     out.println("<BODY><CENTER><p>&nbsp;</p><H2>Login Accepted</H2><BR>");
     out.println("<p>&nbsp;</p>");
     out.println("<BR>Welcome Support!");
     out.println("<br><br><font size=\"2\">");
     out.println("<form method=\"get\" action=\"/" +rev+ "/support_main.htm\">");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</input></form></font>");
     out.println("</CENTER></BODY></HTML>");
     out.close();

   } else {

     invalidLogin("Invalid Password.", req, out, null);   // process invalid login information.....
   }
 }   

 // *********************************************************
 // Process user = Sales
 // *********************************************************

 private void salesUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   if (pw.equals( passwordSales )) {

     HttpSession session = req.getSession(true);  // Create a session object

     ConnHolder holder = null;                   // no con yet, get new one when needed

     session.setAttribute("connect", holder);    // clear connection holder so new one is allocated
     session.setAttribute("user", user);         // save username
     session.setAttribute("club", club);         // save club name
     session.setAttribute("caller", "none");     // save caller's name
     // set inactivity timer for this session (30 mins)
     session.setMaxInactiveInterval(30*60);

     out.println("<HTML><HEAD><Title>Sales Login Page</Title>");
     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/sales_main.htm\">");
     out.println("</HEAD>");
     out.println("<BODY><CENTER><p>&nbsp;</p><H2>Login Accepted</H2><BR>");
     out.println("<p>&nbsp;</p>");
     out.println("<BR>Welcome Sales!");
     out.println("<br><br><font size=\"2\">");
     out.println("<form method=\"get\" action=\"/" +rev+ "/sales_main.htm\">");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</input></form></font>");
     out.println("</CENTER></BODY></HTML>");
     out.close();

   } else {

     invalidLogin("Invalid Password.", req, out, null);   // process invalid login information.....
   }
 }

 // *********************************************************
 // Process user = ADMIN....
 // *********************************************************

 private void adminUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   String errorMsg = "";

   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
       
      con = dbConn.Connect(club);

   } catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   //
   //  Get the remote host id (for tracing client)
   //
   String remote_ip = req.getHeader("x-forwarded-for");
   if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

   //
   // Check password entered against password in DB.........
   //
   try {

      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT password FROM login2 WHERE username = ?");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         if (pw.equals( rs.getString("password") )) {

            //
            //  Count the number of users logged in
            //
            countLogin("admin", con);

            // new stats logging routine
            recordLoginStat(4);
               
            //
            //  Trace all login attempts
            //
            errorMsg = "Admin Login Successful, IP=" + remote_ip + "";
            SystemUtils.sessionLog(errorMsg, user, "", club, omit, con);        // log it - no pw

            recordLogin(user, pw, club, remote_ip, 1);
            
            // Save the connection in the session block for later use.......
            
            HttpSession session = req.getSession(true);  // Create a session object
            
            ConnHolder holder = new ConnHolder(con);     // create a new holder from ConnHolder class
            
            //
            //  Get TLT indicator
            //
            int tlt = (getTLT(con)) ? 1 : 0;
            
            //
            //  Get Roster Sync indicator
            //
            int rsync = getRS(con);

            session.setAttribute("connect", holder);    // save DB connection holder
            session.setAttribute("user", user);         // save username
            session.setAttribute("club", club);         // save club name
            session.setAttribute("caller", "none");     // save caller's name
            session.setAttribute("tlt", tlt);           // timeless tees indicator
            session.setAttribute("rsync", rsync);       // Roster Sync indicator
            // set inactivity timer for this session (1 hr)
            session.setMaxInactiveInterval(60*60);
            
            out.println("<HTML><HEAD><Title>Admin Login Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/admin_welcome.htm\">");
            out.println("</HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<p>&nbsp;</p>");
            out.println("<BR><BR><H2>Login Accepted</H2><BR>");
            out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
            out.println("Welcome <b>System Administrator");
            out.println("</b>");
            out.println("</td></tr></table><br>");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/admin_welcome.htm\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

         } else {

            String errMsg = "Invalid Password. IP=" + remote_ip + "";

            invalidLogin(errMsg, req, out, con);   // process invalid login information.....

            recordLogin(user, pw, club, remote_ip, 0);

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }
         }    // end of if pw matches
      }       // end of if username found
      pstmt2.close();
   }
   catch (SQLException exc) {
      
      Connerror(out, exc, con);
      return;
   } 
 }

 // *********************************************************
 // Process user = PROSHOP....
 // *********************************************************

 private void proshopUser(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String pw, String user, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;
   String zipcode = "";
   String errorMsg = "";
   String logMsg = "";
   String tempS = "";

   int mobile = 0;              // flag for mobile user
   int activity_id = 0;         // inticator for default activity (0=golf)
   //int default_activity_id = 0;

   // Load the JDBC Driver and connect to DB.........

   try {
      con = dbConn.Connect(club);           // get connection to this club's db
   }
   catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);                       // go process connection error......
     return;
   }

   String errMsg = "Invalid Password.";

   //
   //  Get the remote host id (for tracing client)
   //
   String remote_ip = req.getHeader("x-forwarded-for");
   if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

   errorMsg = "Proshop Login Rejected - User/PW & Club = ";

   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
   }
      
   //
   //  check for mobile device flag
   //
   if (req.getParameter("mobile") != null) {

      tempS = req.getParameter("mobile");

      mobile = Integer.parseInt(tempS);       // mobile indicator (1 = basic mobile support, 2 = some javascript supported, 3 = ??, etc)
   }
   
   //
   //  Get club's POS Type for Proshop_slot processing
   //
   String posType = getPOS(con);

   //
   //  Get lottery support indicator for club (for menu processing)
   //
   String lottery = getLottery(con);

   //
   //  Get TLT indicator
   //
   int tlt = (getTLT(con)) ? 1 : 0;   
   
   // Check inactive first, if not inactive check password entered against password in DB.........
   try {
     
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT password, inact, activity_id FROM login2 WHERE username = ? ORDER BY default_entry DESC");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         if (rs.getInt("inact") != 1) {             // proshop user is not inactive
          
             if (pw.equals( rs.getString(1) )) {

                activity_id = rs.getInt(3); // this is the id for the users root activity they are working on (can only access this and below)
                 
                // temp to force proshop22 user to tennis for testing
                if (club.equals("admiralscove") && user.equals("proshop22")) activity_id = 1;

                //
                //  Count the number of users logged in
                //
                countLogin("pro", con);

                // new stats logging routine
                recordLoginStat(3);

                recordLogin(user, "", club, remote_ip, 1);
            
                //
                //  Trace all login attempts
                //
                logMsg = "Pro Login Successful";
                SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

                // Save the connection in the session block for later use.......

                HttpSession session = req.getSession(true);     // Create a session object

                ConnHolder holder = new ConnHolder(con);        // create a new holder from ConnHolder class

                session.setAttribute("connect", holder);        // save DB connection holder
                session.setAttribute("user", user);             // save username
                session.setAttribute("club", club);             // save club name
                session.setAttribute("caller", "none");         // save caller's name
                session.setAttribute("posType", posType);       // save club's POS Type
                session.setAttribute("zipcode", zipcode);       // save club's ZIP Code
                session.setAttribute("lottery", lottery);       // save club's lottery support indicator
                session.setAttribute("mtypeOpt", "ALL");        // init member classes for name list (Proshop_slot, etc.)
                session.setAttribute("mshipOpt", "ALL");
                session.setAttribute("mobile", mobile);         // mobile user
                session.setAttribute("tlt", tlt);               // timeless tees indicator
                session.setAttribute("activity_id", activity_id);  // activity indicator
                session.setMaxInactiveInterval(4*60*60);        // set inactivity timer for this session (4 hrs)

                out.println("<html><head><title>Proshop Login Page</title>");
                out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" + rev + "/" + ((mobile > 0) ? "proshop_mobile_home.htm" : "proshop_welcome.htm") + "\">");
                out.println("</head>");
                out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
                out.println("<hr width=\"40%\">");
                out.println("<p>&nbsp;</p>");
                out.println("<br><br><h2>Login Accepted</h2><br>");
                 out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\"><p>");
                 out.println("Welcome <b>Proshop</b>");
                 out.println("</p></td></tr></table><br>");
                out.println("<br><br><font size=\"2\">");
                out.println("<form method=\"get\" action=\"/" + rev + "/proshop_welcome.htm\">");
                out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                out.println("</input></form></font>");
                out.println("</center></body></html>");
                out.close();

            /*
                //
                //  6/11/07 Removed the following call to scantee to prevent bottlenecks after servers have been bounced.
                //          If we need to do this here, then we need to save the date in the club5 table on a per club basis.
                //          This current method uses one global date for all clubs and does not work well.
                //

                //
                //   Call scanTee to make sure we have current tee sheets (if it hasn't already run today)
                //
                Calendar cal = new GregorianCalendar();        // get todays date
                long yy = cal.get(Calendar.YEAR);
                long mm = cal.get(Calendar.MONTH) +1;
                long dd = cal.get(Calendar.DAY_OF_MONTH);

                long date = (yy * 10000) + (mm * 100) + dd;     // save it

                if (date != SystemUtils.scanDate) {             // if not already run today

                   try {

                      resp.flushBuffer();                   // force the repsonse to complete

                      b = SystemUtils.scanTee(con, club);   // check the tee sheets

                   }
                   catch (Exception ignore) {
                   }
                }
             */


             } else {
                 
                // pw does not match
                errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Invalid PW"; 

                errMsg = "Invalid Password.";

                invalidLogin(errMsg, req, out, con);
                
                recordLogin(user, pw, club, remote_ip, 0);
                
                if (con != null) {
                   try {
                      con.close();
                   }
                   catch (SQLException ignored) {
                   }
                }
             }                 // end of if password matches
             
         } else {
             
             errMsg = "Inactive Proshop Account.";
             
             invalidLogin(errMsg, req, out, con);   // process invalid login information
             
             recordLogin(user, pw, club, remote_ip, 0);
             
             if (con != null) {
                try {
                   con.close();       // Close the db connection........
                }
                catch (SQLException ignored) { }
             }
         }
            
      } else {
          
         // no match found in database
          
         errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Invalid Username";

         errMsg = "Invalid Username.";

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....
         
         recordLogin(user, pw, club, remote_ip, 0);

         if (con != null) {
            try {
               con.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }
      }         // end of if username found
        
      pstmt2.close();
   }
   catch (SQLException exc) {

      Connerror(out, exc, con);
      return;
   }
 }

 // *********************************************************
 // Process user = Club Member or Hotel User
 // *********************************************************

 private void memberUser(HttpServletRequest req,
                 PrintWriter out, String user, String pw, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;

   Member member = new Member();

   String errorMsg = "Member Login Rejected - User/PW & Club = ";
   String logMsg = "";
   String clubName = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String email = "";
   String email2 = "";
   String emailErr = "";
   String email2Err = "";
   String zipcode = "";
   String errMsg = "";
   String tempS = "";

   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
     
   //DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

   int hotel = 0;
   boolean error = false;           // init error indicator
   boolean allowMobile = false;
   int mobile = 0;                  // mobile user indicator
   
   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
   }

   //
   //  Check for mobile device flag
   //
   if (req.getParameter("mobile") != null) {

      tempS = req.getParameter("mobile");

      mobile = Integer.parseInt(tempS);       // mobile indicator (1 = basic mobile support, 2 = some javascript supported, 3 = ??, etc)
   }
   
       
    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?
 
    
   //
   //  Check if Mobile user and Mobile not supported
   //
    /*          // skip this for now so pros can login as a member to test the mobile
   if (mobile > 0 && allowMobile == false) {
      
      invalidLogin("Mobile not supported at your club.", req, out, con, mobile);   // process invalid login information..... 
      return;
   }
     */
    
   //
   //  Get club's POS Type for Proshop_slot processing
   //
   String posType = getPOS(con);

   int default_activity_id = 0;         // inticator for default activity (0=golf)

   //
   //  Get TLT indicator
   //
   int tlt = (getTLT(con)) ? 1 : 0;

   //
   //  Get the remote host id (for tracing client)
   //
   String remote_ip = req.getHeader("x-forwarded-for");
   if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();
   
   boolean allow_member4tea = false;
   if (
       (club.startsWith("demo") || club.startsWith("notify")) && user.equals("member4tea") || 
       (Common_Server.SERVER_ID == 4 && user.equals("member4tea"))
      ) { allow_member4tea = true; }
   
   //
   // use a prepared statement to find username (string) in the DB..
   //
   try {

      PreparedStatement pstmt = null;
       
      if ( allow_member4tea ) {
          
          // get a random member that won't get emails
          pstmt = con.prepareStatement (
             "SELECT password, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, username, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone " +
             "FROM member2b " +
             "WHERE (emailOpt = 0 OR (email = '' AND email2 = '')) AND email <> ? AND " +
                "inact = 0 AND billable = 1 " +
             "ORDER BY RAND() LIMIT 1");
          
      } else {
         
         if (mobile == 0) {                // if NOT a Mobile user
            
             pstmt = con.prepareStatement (
                "SELECT password, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone " +
                "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");

         } else {                   // Mobile user - check mobile credentials
            
             pstmt = con.prepareStatement (
                "SELECT mobile_pass, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone, username " +
                "FROM member2b WHERE mobile_user = ? AND inact = 0 AND billable = 1");
         }
      }
      // Get user's pw if there is a matching user...

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, user);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         // fake the credentials for member4tea on demo sites
         if ( allow_member4tea ) {
             user = rs.getString("username");
             pw = rs.getString("password");
         } 
          
         //
         // Check password entered against password in DB.........
         //
         if (pw.equalsIgnoreCase( rs.getString(1) )) {        // password MUST be the first field in query above !!

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
               mem_name.append(" ");
               mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string

            // Get the member's mship type and member type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type

            // Get the member's email addresses

            email = rs.getString("email");       
            email2 = rs.getString("email2");
            email_bounced = rs.getInt("email_bounced");       
            email2_bounced = rs.getInt("email2_bounced");
            iCal1 = rs.getInt("iCal1");
            iCal2 = rs.getInt("iCal2");
            default_activity_id = rs.getInt("default_activity_id");
            
            //
            //  If Mobile, then only Golf is supported !!!!!!!!!!!!!!
            //
            if (mobile > 0) {
               
               int mobile_count = rs.getInt("mobile_count");     // get # of mobile logins for this ueer
               int mobile_iphone = rs.getInt("mobile_iphone");     // get # of iphone logins for this ueer
            
               user = rs.getString("username");       // use normal username for the session
               default_activity_id = 0;               // make sure it is Golf
               
               countMobile(mobile_count, mobile_iphone, user, req, con);      // bump mobile counter and track mobile device 
            }
            

            // Get the number of visits and update it...

            int count = rs.getInt("count");       // Get count
            count++;                              // bump counter..
            
              
            //  Get wc and last message displayed at login
            wc = rs.getString("wc");          // Get w/c pref
            String message = rs.getString("message");       // last message this member viewed

            //
            //  see if we should display a message to this member
            //
            if (message.equals( latest_message )) {      // if newest message was already displayed
              
               message = "";                              // no message to send
               
            } else {
               
               if (allowMobile == true) {
                  
                  message = "msg002";      // mobile allowed - show both messages
                  
               } else {      // no mobile - only display msg001
                  
                  if (message.equals( "" )) {
                     
                     message = "msg001";
                     
                  } else {
                     
                     message = "";
                  }
               }
            }
                        

            PreparedStatement stmt = con.prepareStatement (
               "UPDATE member2b SET count = ? WHERE username = ?");

            stmt.clearParameters();          
            stmt.setInt(1, count);            // new login count 
            stmt.setString(2, user);          // username 
            stmt.executeUpdate();

            stmt.close();
            
            
            //
            //  If Medinah CC - do not allow certain members to login
            //
            if (club.equals( "medinahcc" ) && error == false) {
              
               if (mship.equals( "Social" ) || mship.startsWith( "Social Pro" ) || mtype.startsWith( "FM " ) || mtype.startsWith( "Fam Member" )) { 
  
                  error = true;     // do not allow
  
                  errMsg = "Membership Class Not Allowed.";
               }
            }  
            
            if (error == false) {
              
               //
               //  Count the number of users logged in
               //
               countLogin("mem", con);

               // new stats logging routine
               recordLoginStat(1);
            
               //
               //  Trace all login attempts
               //
               logMsg = "Member Login Successful";
               SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

               recordLogin(user, pw, club, remote_ip, 1);
            
               // Save the connection in the session block for later use.......
               HttpSession session = req.getSession(true);     // Create a new session object

               ConnHolder holder = new ConnHolder(con);    // create a new holder from ConnHolder class

               session.setAttribute("connect", holder);      // save DB connection holder
               session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
               session.setAttribute("user", user);           // save username
               session.setAttribute("name", name);           // save members full name
               session.setAttribute("club", club);           // save club name
               session.setAttribute("caller", "none");       // save caller's name
               //session.setAttribute("daysArray", daysArray); // save 'days in adv' object
               session.setAttribute("mship", mship);         // save member's mship type
               session.setAttribute("mtype", mtype);         // save member's mtype
               session.setAttribute("wc", wc);               // save member's walk/cart pref (for _slot)
               session.setAttribute("posType", posType);     // save club's POS Type
               session.setAttribute("zipcode", zipcode);     // save club's zipcode
               session.setAttribute("mobile", mobile);       // mobile user
               session.setAttribute("tlt", tlt);             // timeless tees indicator
               session.setAttribute("activity_id", default_activity_id);  // activity indicator

               //
               // set inactivity timer for this session
               //  use 10 mins to prevent user from hanging a tee slot and connection too long
               //
               if ( (club.startsWith("demo") || club.startsWith("admiralscove")) && Common_Server.SERVER_ID == 4 ) {
                   session.setMaxInactiveInterval(4*60*60);  // dev account
               } else {
                   session.setMaxInactiveInterval( MEMBER_TIMEOUT );
               }
               
               
               //
               //  Output the welcome message based on device type (Mobile or Standard)
               //
               if (mobile == 0) {        // if standard device/browser
               
                  //
                  //   Validate the email addresses
                  //
                  if (!email.equals( "" )) {                   // if specified

                     FeedBack feedback = (member.isEmailValid(email));

                     if (!feedback.isPositive()) {              // if error

                        emailErr = feedback.get(0);             // get error message
                     }
                  }
                  if (!email2.equals( "" )) {                   // if specified

                     FeedBack feedback = (member.isEmailValid(email2));

                     if (!feedback.isPositive()) {              // if error

                        email2Err = feedback.get(0);             // get error message
                     }
                  }


                  //
                  //   Determine if auto-refresh to be used
                  //
                  boolean autoRefresh = true;

                  if (count == 0 || email.equals( "" ) || email_bounced > 0 || email2_bounced > 0 || iCal1 == -1 || iCal2 == -1) {

                     autoRefresh = false;
                  }

                  if (!emailErr.equals( "" ) || !email2Err.equals( "" )) {

                     autoRefresh = false;
                  }
                  
                  
                  int delay = 1;    // default pause for welcome message
                  
                  if (club.equals("woodlandscountryclub")) {
                     
                     delay = 3;      // 3 seconds for them
                  }
                  

                  //
                  //   Output welcome page
                  //
                  out.println("<HTML><HEAD><Title>Member Login Page</Title>");

                  if (autoRefresh == true) {

                     if (message.equals( "" )) {      // if no message to display
                        if (club.equals("wingedfoot")) { 
                           //out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_searchpast?subtee=gquota\">");
                           out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/wfmember_welcome.htm\">");
                        } else {
                           out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=/" + rev + "/member_welcome.htm\">");
                        }
                     } else {
                        out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=/" +rev+ "/servlet/Member_msg\">");
                     }
                  }
                  out.println("</HEAD>");
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H2>Login Accepted</H2><BR>");
                  out.println("<table width=\"70%\" border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
                  out.println("<font size=\"3\">");
                  out.println("<BR>Welcome <b>" + name );

                  out.println("</b><BR><BR>");
                  out.println("Please note that this session will terminate if inactive for more than " + (MEMBER_TIMEOUT / 60) + " minutes.");
                  out.println("</b><BR><BR>");

                  if (count == 1) {

                     out.println("<font size=\"3\">");
                     out.println("<b>Notice:</b>  Since this is your first visit, we strongly recommend that you <b>change your password</b>.<br>");
                     out.println("To do this, select the 'Settings' tab from the navigation bar on the top of most pages.");
                  }

                  if (email_bounced == 1 || email2_bounced == 1) {        // problem with email address?

                     out.println("<h3><b>WARNING: Email bouncing!!</b></h3>");
                     out.println("We recently tried to send you an email at ");

                     if (email_bounced == 1) {

                        out.print(email);

                     } else {

                        out.print(email2);
                     }
                     out.print(" and it bounced back to us.<br>" +
                               "We've had to temporarily disable sending you any emails until you resolve this problem.");
                     out.println("<BR><BR>To correct this, update your email below, or select the 'Settings' tab from the<br>" +
                                 "navigation bar on the top of most pages and follow the insructions in the email<br>" +
                                 "section next to the word 'Important'.");
                     out.println("<br><br>If the current email is correct, simply click 'Continue' below and ForeTees will<br>" +
                             "attempt to continue using the same email.  If you would like to remove the current email<br>" +
                             "address all together, erase it from the field below and click 'Continue'.");

                     out.println("<br><br>");

                     if (autoRefresh == false) {

                         if (email.equals( "" )) {
                            out.println("Please add at least one valid email address below.");
                         } else {
                            out.println("Please verify and/or change the email address(es) below.");
                         }
                         out.print("&nbsp;&nbsp;");
                         out.print("Thank you!");
                         out.print("<br><br>");

                         out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
                         out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");
                         out.println("<input type=\"hidden\" name=\"bounced\" value=\"" + ((email_bounced == 1) ? "email" : "email2") + "\">");

                         out.println("<b>Email Address " + ((email_bounced == 1) ? "1" : "2") + ":</b>&nbsp;&nbsp;");
                         out.println("<input type=\"text\" name=\"email\" value=\"" + ((email_bounced == 1) ? email : email2) + "\" size=\"40\" maxlength=\"50\">");
                         out.println("<br><br>");

                     } else {                 

                         if (message.equals( "" )) {      // if no message to display
                            out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                         } else {
                            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
                         }
                     }

                     out.println("</font></td></tr></table>");
                     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("</input></form></font>");
                     out.println("</CENTER></BODY></HTML>");
                     out.close();
                     return;
                  }

                  if (!emailErr.equals( "" ) || !email2Err.equals( "" )) {   // problem with email address?

                     if (!emailErr.equals( "" )) {   // problem with email1 address?

                        out.println("<b>Warning:</b>  Your email address (" +email+ ") is invalid.");
                        out.println("<BR>" +emailErr);

                     } else {

                        out.println("<b>Warning:</b>  Your email address (" +email2+ ") is invalid.");
                        out.println("<BR>" +email2Err);
                     }
                     out.println("<BR><BR>To correct this, please change it below.");

                  } else if (email.equals( "" )) {

                        out.println("<br><b>Notice:</b> In order to receive email notifications and to stay informed,");
                        out.print("<br>you must maintain a current, working email address.");

                  } else if (iCal1 == -1 || iCal2 == -1) {

                      out.println(iCalNotice);

                  }

                  out.println("<br>");

                  if (autoRefresh == false) {

                     if (email.equals( "" )) {
                        out.println("Please add at least one valid email address below.");
                     } else if (iCal1 == -1 || iCal2 == -1) {
                        out.println("Please select your iCalendar preferences.");
                     } else {
                        out.println("Please verify and/or change the email address(es) below.");
                     }
                     out.print("&nbsp;&nbsp;");
                     out.print("Thank you!");
                     out.print("<br><br>");

                     out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
                     out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

                     out.println("<b>Email Address 1:</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email\" value=\"" +email+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal1\">");
                      out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");
                     out.println("<b>Email Address 2:</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email2\" value=\"" +email2+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal2\">");
                      out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");

                  } else {

                     if (message.equals( "" )) {      // if no message to display
                        out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                     } else {
                        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
                     }
                  }

                  out.println("</font></td></tr></table>");
                  out.println("<br>");
                  out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</input></form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
              
               
               } else {          

                  //
                  //  Mobile Device - go to mobile menu page
                  //
                  out.println("<HTML xmlns='http://www.w3.org/1999/xhtml'><HEAD><Title>Member Welcome Page</Title>");
                  out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/mobile/member_mobile_home.html\">");
                  out.println("<meta name=\"viewport\" id=\"viewport\" content=\"width=device-width, user-scalable=no\">");
                  out.println("<LINK REL=StyleSheet HREF=\"/" +rev+ "/mobile/style.css\" TYPE=\"text/css\" MEDIA=screen></HEAD>");
                  out.println("<BODY><CENTER>");
                  out.println("<BR><BR><div class=\"headertext\">Login Accepted<BR>");
                  out.println("<BR>Welcome " + name );                                           // Member's Name
                  out.println("<BR><BR></div>");
                  out.println("<form method=\"get\" action=\"/" + rev + "/mobile/member_mobile_home.html\">");
                  out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</input></form>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
               }                          // end of IF mobile device
               
            }              // end of IF error

         } else {          // password did not match

            error = true; // indicate an error occurred

            if (mobile == 0) {
               errMsg = "Invalid Password.";
            } else {
               errMsg = "Invalid Password for Mobile User.";
            }
            errorMsg = errorMsg + user + " " + pw + " " + club + " invalid PW (IP=" + remote_ip + ")";    // build error msg
              
         } // end of if pw matches
           
      } else {        // member username not found in member2b table

          
         // if this club supports hotel users, lets check the hotel3 table to
         // see if this username exists
          
         //
         //  Check if Hotels are supported for this club
         //
         PreparedStatement pstmth1 = con.prepareStatement (
            "SELECT clubName, hotel " +
            "FROM club5");

         pstmth1.clearParameters();         // clear the parms
         rs = pstmth1.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            clubName = rs.getString(1);       // Get club's name
            hotel = rs.getInt(2);             // Get hotel indicator
         }
         pstmth1.close();
         
         if (hotel > 0) {          // if hotels supported

            pstmth1 = con.prepareStatement (
               "SELECT password, name_last, name_first, name_mi, message " +
               "FROM hotel3 WHERE username = ?");

            // Get user's pw if there is a matching user...

            pstmth1.clearParameters();         // clear the parms
            pstmth1.setString(1, user);        // put the username field in statement
            rs = pstmth1.executeQuery();       // execute the prepared stmt

            if (rs.next()) {

               //
               // Check password entered against password in DB.........
               //
               if (pw.equals( rs.getString(1) )) {

                  //
                  //  Count the number of users logged in
                  //
                  countLogin("hotel", con);

                  recordLogin(user, pw, club, remote_ip, 1);
                  
                  //
                  //  Trace all login attempts
                  //
                  logMsg = "Hotel Login Successful";
                  SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

                  // Get the member's full name.......

                  StringBuffer mem_name = new StringBuffer(rs.getString(3));  // get first name

                  String mi = rs.getString(4);                                // middle initial
                  if (!mi.equals( omit )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  mem_name.append(" " + rs.getString(2));                     // last name

                  String name = mem_name.toString();                          // convert to one string

                  // Save the connection in the session block for later use.......
                  HttpSession session = req.getSession(true);     // Create a new session object

                  ConnHolder holder = new ConnHolder(con);    // create a new holder from ConnHolder class

                  session.setAttribute("connect", holder);    // save DB connection holder
                  session.setAttribute("sess_id", id);        // set session id for validation ("foretees")
                  session.setAttribute("user", user);         // save username
                  session.setAttribute("name", name);         // save members full name
                  session.setAttribute("club", club);         // save club name
                  session.setAttribute("caller", "none");     // save caller's name
                  session.setAttribute("zipcode", zipcode);   // save club's zipcode
                  session.setAttribute("tlt", tlt);           // timeless tees indicator
                  
                  //
                  // set inactivity timer for this session (2 hrs)
                  //
                  session.setMaxInactiveInterval(2*60*60);

                  out.println("<HTML><HEAD><Title>Hotel Login Page</Title>");
                  out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/hotel_selmain.htm\">");
                  out.println("</HEAD>");
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<p>&nbsp;</p>");
                  out.println("<BR><H2>Login Accepted</H2><BR>");
                  out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

                  out.println("<BR>Welcome to the ForeTees Tee Time Reservation system<br>for " + clubName );

                  out.println("<BR><BR>");
                  out.println("</td></tr></table>");
                  out.println("<br><br><font size=\"2\">");
                  out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_selmain.htm\">");
                  out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</input></form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();

               } else {                     // password does not match

                  error = true;             // indicate an error occurred

                  errMsg = "Invalid Password.";

                  errorMsg = errorMsg + user + " " + pw + " " + club + " invalid PW";    // build error msg

               }     // end of if hotel pw matches

            } else {                     // member & hotel username not found

               error = true;             // indicate an error occurred

               errMsg = "Invalid Username.";

               errorMsg = errorMsg + user + " " + pw + " " + club + " invalid user";   // build error msg

            }
            pstmth1.close();

         } else {                     // member username not found and hotels not supported

            error = true;             // indicate an error occurred

            errMsg = "Invalid Username.";

            errorMsg = errorMsg + user + " " + pw + " " + club + " invalid user";   // build error msg

         }     // end of if hotels supported 
         
      }        // end of if member username found

      pstmt.close();

      if (error == true) {         // if login failed   

         invalidLogin(errMsg, req, out, con, mobile);   // process invalid login information.....
         
         recordLogin(user, pw, club, remote_ip, 0);

         if (con != null) {
            try {
               con.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }
      }        // end of IF error
   }
   catch (SQLException exc) {

      Connerror(out, exc, con);
      return;
      
   }/* finally {

       if (con != null) {
          try {
             con.close();
          } catch (SQLException ignored) {}
       }
   }*/
   
 }


 // *********************************************************
 // Process user = remote web site....
 // *********************************************************

 private void remoteUser(HttpServletRequest req,
                 PrintWriter out, String user, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;
   PreparedStatement stmt = null;

   Member member = new Member();

   String lname = "";
   String username = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String zipcode = "";
   String primary = "No";
   String mNumParm = "No";
   String mNum = "";
   String oldemail1 = "";
   String oldemail2 = "";
   String email1 = "";
   String email2 = "";
   String emailErr = "";
   String email2Err = "";
   String logMsg = "";
   String mapping = "No";
   String stripZero = "No";
   String errMsg = "";
   String eventName = "";
   String courseName = "";
   String caller = "";
   String seamless_caller = "";               // caller value in club5
     
   int rsynci = 0;                      // values from club5 table for this club
   int seamless = 0;
   int primaryif = 0;
   int mnumi = 0;
   int mappingi = 0;
   int stripzeroi = 0;
   int signUp = 0;
   int stripAlpha = 0;
   int stripDash = 0;

   int default_activity_id = 0;         // inticator for default activity (0=golf)
   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
     
   boolean rsync = false;
   boolean primaryCE = false;
   boolean stripEnd = false;
   boolean fatalError = false;
   boolean allowMobile = false;

   //DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

    //
    //  Check if Mobile is allowed (for messages)
    //
    allowMobile = Utilities.checkMobileSupport (con);      //  show the Mobile messages?
 
    
   //
   //  Get the caller's id
   //
   if (req.getParameter("caller") != null) {

      caller = req.getParameter("caller");
      
   } else {
      
      errMsg = "Error in Login.remoteUser - Caller parm not provided by web site.";
      fatalError = true;
   }
    
   if (fatalError == false) {

      if (user == null || user.equals("")) {      // if username not provided

         out.println(SystemUtils.HeadTitle("Invalid Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p><p>&nbsp;</p>");
         out.println("<BR><H2>Access Rejected</H2><BR>");
         out.println("<BR>Sorry, we cannot complete the connection to ForeTees due to an interface error.");
         out.println("<BR><BR>Username or Member Number was not provided. This is often the result if you do not first");
         out.println("<BR>login to your club's website.  Please be sure to login before attempting to access ForeTees.");
         out.println("<BR><BR>If problem persists, please contact your club (and provide this information) or try again later.  Thank you.<BR>");
         out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         out.println("<BR><BR>Or <A HREF=\"javascript:history.back(1)\">Go Back</A> (if the Exit fails)");
         out.println("</CENTER></BODY></HTML>");
         out.close();

         if (con != null) {

            try {

               con.close();                // close the connection

            }
            catch (Exception exp) {
            }
         }      
         return;
      
         // errMsg = "Error in Login.remoteUser - Username not provided by web site. Club=" +club+ ", Caller=" +caller+ ".";
         // fatalError = true;
      }
   }
    
   if (fatalError == false) {

      //
      //  Make sure the club requested is currently running this version of ForeTees.
      //  The user may need to refresh the login page so they pull up the new page.
      //
      try {

         con = dbConn.Connect(rev);       // get a connection for this version level

         //
         //  Make sure club exists in our system
         //
         stmt = con.prepareStatement (
                  "SELECT fullname FROM clubs WHERE clubname = ?");

         stmt.clearParameters();        // clear the parms
         stmt.setString(1, club);
         rs = stmt.executeQuery();

         if (!rs.next()) {          // if club not found in this version

            errMsg = "Error in Login.remoteUser - club name invalid, club=" +club+ " was received from web site.";        
            fatalError = true;
         }
         stmt.close();              // close the stmt

      }
      catch (Exception exc) {
         // Error connecting to db....
         errMsg = "Error in Login.remoteUser - Unable to Connect to Database.  Could be an invalid clubname (club=" +club+ "). Error: " + exc.toString();    
         fatalError = true;
      }
   }

   if (fatalError == false) {

      //
      // Load the JDBC Driver and connect to DB for this club
      //
      try {
         con = dbConn.Connect(club);          // get a connection
      }
      catch (Exception exc) {

         errMsg = "Login.remoteUser - Unable to Connect to club: " +club+ ", User = " +user+ ". Error: " + exc.toString();
         fatalError = true;
      }
   }


   //
   //  If any of above failed, return error message to user and log error in v5 error log table
   //
   if (fatalError == true) {
      
      out.println(SystemUtils.HeadTitle("Invalid Login"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Access Rejected</H2><BR>");
      out.println("<BR>Sorry, we cannot complete the connection to ForeTees due to an interface error.");
      out.println("<BR>Exception: " +errMsg+ "<BR>");
      out.println("<BR>Please contact your club (and provide this information) or try again later.  Thank you.<BR>");
      out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      
      SystemUtils.logError(errMsg);                           // log it

      if (con != null) {

         try {

            con.close();                // close the connection

         }
         catch (Exception exp) {
         }
      }      
      return;
   }
   

      
   //
   //****************************************************
   //  Get the I/F options from club5 (set via support) 
   //****************************************************
   //
   try {

      stmt = con.prepareStatement (
               "SELECT rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero, seamless_caller, stripalpha, stripdash " +
               "FROM club5");

      stmt.clearParameters();        // clear the parms
      rs = stmt.executeQuery();

      if (rs.next()) {          // get club options 

         rsynci = rs.getInt(1);
         seamless = rs.getInt(2);
         zipcode = rs.getString(3);
         primaryif = rs.getInt(4);
         mnumi = rs.getInt(5);
         mappingi = rs.getInt(6);
         stripzeroi = rs.getInt(7);
         seamless_caller = rs.getString("seamless_caller");       // Caller value saved for this club
         stripAlpha = rs.getInt("stripalpha");
         stripDash = rs.getInt("stripdash");
      }
      stmt.close();              // close the stmt

   }
   catch (Exception exc) {

      invalidRemote("Unable to Connect to Club Database for options. Error: " + exc.getMessage(), req, out, con);
      return;
   }
   
   
   if (caller.equals("ForeTees1298")) {   // if this is a ForeTees Provided Link - CE Bypass
      
      //
      //  We provided the website link (originally added for CE websites because they charge too much), then
      //  force the settings so we can run this interface while the old CE link is still in place.
      //
      seamless = 1;
      primaryif = 1;
      mnumi = 1;
      mappingi = 1;
      stripzeroi = 1;
   }
   
   
   //
   //  Set internal 'strip' flag for specific clubs
   //
   if (club.equals( "portage" ) || club.equals("mayfieldsr") || club.equals("stoneoakcountryclub")) {

      stripEnd = true;            // strip trailing end character on username (or mNum)
   }


   //
   //**********************************************************************
   //  If the I/F options have not been set, get them from web site parms
   //**********************************************************************
   //
   if (seamless == 0) {           // if seamless not set, then others won't be so get parms passed

      //
      //  Get the club's zipcode if passed (for weather link)
      //
      if (req.getParameter("zipcode") != null) {

         zipcode = req.getParameter("zipcode");
      }

      //
      //  Get the 'primary' parm if passed (used by web site to indicate the member is primary - we must prompt)
      //
      if (req.getParameter("primary") != null) {

         primary = req.getParameter("primary");
      }

      if (req.getParameter("mnum") != null) {

         mNumParm = req.getParameter("mnum");
      }

      //
      //  See if user wants to use Mapping - maps username value to our 'webid'
      //
      if (req.getParameter("mapping") != null) {

         mapping = req.getParameter("mapping");      // get mapping parm - used to map member ids
      }

      //
      //  See if user wants to Strip Leading Zeros from the username value
      //
      if (req.getParameter("stripzero") != null) {

         stripZero = req.getParameter("stripzero");
      }
        
   } else {

      //
      //  Set parms based on club options
      //
      if (primaryif == 1) {
        
         primary = "Yes";
           
         if (caller.equals( "CLUBESSENTIAL" )) {   // CE provides their unique id in user_name - we must get mnum

            primaryCE = true;                       // indicate CE primmary i/f
            mNumParm = "Yes";                       // make sure we use mNum (use webid to get it)
         }
      }
        
      if (mnumi == 1) {

         mNumParm = "Yes";
      }

      if (mappingi == 1) {

         mapping = "Yes";
      }

      if (stripzeroi == 1) {

         stripZero = "Yes";
      }

      if (rsynci == 1) {

         rsync = true;
      }
   }


   //
   //  Strip end char off user if required
   //
   if (stripEnd == true) {
     
      if (!user.endsWith( "0" ) && !user.endsWith( "1" ) && !user.endsWith( "2" ) && !user.endsWith( "3" ) &&
          !user.endsWith( "4" ) && !user.endsWith( "5" ) && !user.endsWith( "6" ) && !user.endsWith( "7" ) &&
          !user.endsWith( "8" ) && !user.endsWith( "9" )) {

         user = stripA2( user );        // remove trailing alpha char
      }
   }

   //
   //  Special processing for MFirst
   //
   if (caller.equals( "MEMFIRST" )) {       // if 'MembersFirst'
     
      lname = req.getParameter("lname");    // get last name value that was passed
        
      username = getUser(user);             // go extract user (decrypt it)
  
      if (username.equals( "" )) {
        
         out.println(SystemUtils.HeadTitle("Connection Error - Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H2>Invalid Credentials Received</H2><BR>");
         out.println("<BR>Sorry, some required information is either missing or invalid.<BR>");
         out.println("<BR>Exception: Username Not Received");
         out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
         out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
     
         logMsg = "Remote Login Failed (MFirst) - Invalid Username: " +user;
         SystemUtils.sessionLog(logMsg, user, omit, club, omit, con);                   // log it
         return;
      }

      //
      //  Get email address(es) if supplied
      //
      if (!club.equals("mesaverdecc")) {     // if not Mesa Verde (do not update emails for Mesa Verde)

         if (req.getParameter("email1") != null) {

            email1 = req.getParameter("email1");
         }
         if (req.getParameter("email2") != null) {

            email2 = req.getParameter("email2");
         }
      }
        
      //
      //  See if request is for event signup - MF users can select event from their calendar!!
      //
      if (req.getParameter("eventname") != null) {

         eventName = req.getParameter("eventname");      // event name must match our name exactly!!!!!
      }

      if (!eventName.equals("") && eventName != null) {  // if event specified, verify name and get course name for event

         try {

            stmt = con.prepareStatement (
               "SELECT courseName, signUp " +
               "FROM events2b WHERE name = ?");

            stmt.clearParameters();       
            stmt.setString(1, eventName);       
            rs = stmt.executeQuery();       

            if (rs.next()) {

               courseName = rs.getString("courseName");     // get course name for Member_events2
               signUp = rs.getInt("signUp");                // signUp indicator for members
               
            } else {           // not found - must be invalid name from MF
               
               eventName = "";           // remove it, direct member to announce page instead
            }

            stmt.close();
            
            if (signUp == 0) {           // if members are not allowed to register for this event
               
               eventName = "";           // remove it, direct member to announce page instead
            }

         }
         catch (Exception exc) {

            eventName = "";           // remove it, direct member to announce page instead
         }  
      }
      

   } else {     // not MEMFIRST
      
      username = user;
        
      //
      //  Set Roster Sync indicator based on club name
      //
      if (club.equals( "fourbridges" )) {     // Flexscape

         rsync = true;                        
      }

      if (caller.equals( "CLUBESSENTIAL" )) {    // CE
  
         if (primaryCE == true) {       // if CE primary interface, get mNum by matching user to webid
           
            try {

               stmt = con.prepareStatement (
                  "SELECT memNum " +
                  "FROM member2b WHERE webid = ? AND inact = 0 AND billable = 1");

               stmt.clearParameters();         // clear the parms
               stmt.setString(1, user);        // put the username field in statement
               rs = stmt.executeQuery();       // execute the prepared stmt

               while (rs.next()) {

                  username = rs.getString(1);     // use mNum to get real username
               }

               stmt.close();

            }
            catch (Exception exc) {

              errMsg = "Unable to get member number for this user.";

              invalidRemote(errMsg, req, out, null);              // go process connection error......
              return;
            }  
         }
         
      }              // end of IF CE
   }
   

   //
   //  Special processing for Merion (CSG) - parse the username field to extract the mNum (primary i/f)
   //
   if (club.equals("merion")) {

      //
      //  Merion - web site is CSG - strip the "-nn" extension from the username to get the member number
      //
      StringTokenizer tok = new StringTokenizer( username, "-" );     // delimiters are dash

      if ( tok.countTokens() > 1 ) {                  // must be at least 2 (nnnn-ee)

         username = tok.nextToken();                  // get member number only
      }          
   }
   
   
   //
   //  *** IMPORTANT: DON'T USE CALLER'S LONGER THAN 24 CHARACTERS IN LENGTH! (length of club5.seamless_caller field)
   //
   //  PDG4735 = Generic Web Site ID
   //
   //  ForeTees1298 - for the CE Bypass links on CE websites !!!!!!!!!
   //
   //  South Bay Design = Hillcrest GC in St. Paul
   //  Legendary Marketing = Harkers Hollow GC
   //  Joe Keating (joetechonline.com) = Glen Oak CC
   //  City Star = Colorado Springs CC
   //  Jay Van Vark = CC of Rancho Bernardo
   //  Winding Oak = Wayzata CC
   //  Lightedge =  Davenport CC
   //  Club Systems Group (CSG7463) =  Hurstbourne CC
   //  LogiSoft (LOGISOFT7482) =  Locust Hill CC
   //  MeritSoft (BUZWEB4937) =  Providence CC (*** Removed - changed to CE 6/11/07 *****)
   //  Cherry Hills (CHCC0475) = Cherry Hills CC
   //  Nakoma (NAKOMA3273) = Fairwood
   //  Sedona Management Group (SEDONA3973) = Fairwood
   //  Flexscape (FLEXSCAPE4865) = Oswego Lake
   //  ZSmart (ZSMART3573) = Oswego Lake
   //  Jake Cook (JCOOK78439) = Wichita
   //  SLG Development (SLGDEV4673) = Pine Hills
   //  Gary Jonas (GJONAS74912) = Interlachen & Meadowbrook
   //  Gold Star Webs (GOLDSTAR) = Rochester CC
   //  Web Sites 2000 (WEBS2000) = Ironwood CC
   //  Hidden Valley = North Ridge
   //  FELIX = White Manor
   //  MediaCurrent = Piedmont Driving Club (Atlanta)
   //  Monson now = 'GCWH'
   //  Hooker = Southview CC
   //  Velotel = Hazeltine (Mark Josefson)
   //  Intraclub = Skokie, Bellerive (Paul Niebuhr 203-984-9887)
   //  TAHOEDONNERCA530 = Tahoe Donner (tahoedonner)
   //
   if (caller.equals( "AMO" ) || caller.equals( "MEMFIRST" ) || caller.equals( "GCWH" ) ||
       caller.equals( "NEMEX" ) || caller.equals( "HOOKER" ) || caller.equals( "PRIVATEGOLF" ) ||
       caller.equals( "CLUBESSENTIAL" ) || caller.equals( "JCOOK78439" ) || caller.equals( "VELOTEL" ) ||
       caller.equals( "INTRACLUB" ) || caller.equals( "MEDIACURRENT" ) || caller.equals( "FELIX" ) ||
       caller.equals( "HIDDENVALLEY" ) || caller.equals( "WEBS2000" ) || caller.equals( "GOLDSTAR" ) ||
       caller.equals( "GJONAS74912" ) || caller.equals( "SLGDEV4673" ) || caller.equals( "ZSMART3573" ) ||
       caller.equals( "FLEXSCAPE4865" ) || caller.equals( "GRAPEVINE2947" ) || caller.equals( "SEDONA3973" ) ||
       caller.equals( "NAKOMA3273" ) || caller.equals( "CHCC0475" ) || 
       caller.equals( "LOGISOFT7482" ) || caller.equals( "CSG7463" ) || caller.equals( "LIGHTEDGE" ) ||
       caller.equals( "WINDINGOAK" ) || caller.equals( "VANVARK2754" ) || caller.equals( "CITYSTAR3976" ) ||
       caller.equals( "KEATING385" ) || caller.equals( "LEGENDARY294" ) || caller.equals( "SOUTHBAY" ) ||
       caller.equals( "PDG4735" ) || caller.equals( "ForeTees1298" ) || caller.equals( "TAHOEDONNERCA530" )) {

       // || caller.equals( "BUZWEB4937" ) ||        (removed 6/11/07)

      
      //
      //   Save caller value in club5 so we can identify clubs that use this interface
      //
      if (!caller.equals( seamless_caller )) {          // if this caller is different than the caller saved in club5
         
         try {

            PreparedStatement pstmt = con.prepareStatement (
               "UPDATE club5 SET seamless_caller = ?");

            pstmt.clearParameters();           
            pstmt.setString(1, caller);             // save this caller

            pstmt.executeUpdate();  

            pstmt.close();

         }
         catch (Exception exc) {
         }
      }
      
      
      //
      //  Get club's POS Type for _slot processing
      //
      String posType = getPOS(con);

      //
      //  Get TLT indicator
      //
      int tlt = (getTLT(con)) ? 1 : 0;
         
      //
      //  Strip leading zeros in username (member #) if came from Web Sites 2000 (Ironwood CC) or CSG (Hurstbourne)
      //
      if (caller.equals( "WEBS2000" ) || caller.equals( "CSG7463" )) {     // if caller is Web Sites 2000 or CSG

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero(username);      // strip them
         }
      }

      //
      //  Strip leading zeros in username (member #) if came from St. Albans CC or Sunset Ridge
      //
      if (caller.equals( "INTRACLUB" ) && (club.equals( "stalbans" ) || club.equals( "sunsetridge" ))) {     

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }

      //
      //  Strip leading zeros in username if came from Jonas for Mendakota
      //
      if (caller.equals( "GJONAS74912" ) && club.equals( "mendakota" )) {

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }


      //
      //  Strip leading zeros in user requested it (NEW option)
      //
      if (stripZero.equalsIgnoreCase( "yes" )) {

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }

      //  If option is set, strip any non-numeric character form the end of username (e.g. 1234A -> 1234)
      if (stripAlpha == 1) {

          if (!username.endsWith("0") && !username.endsWith("1") && !username.endsWith("2") &&
              !username.endsWith("3") && !username.endsWith("4") && !username.endsWith("5") &&
              !username.endsWith("6") && !username.endsWith("7") && !username.endsWith("8") &&
              !username.endsWith("9")) {

              username = username.substring(0, username.length()-1);
          }
      }

      //  If option is set, strip dash and anything following it from username (e.g. 1234-001 -> 1234)
      if (stripDash == 1) {

          StringTokenizer dashTok = new StringTokenizer(username, "-");

          if (dashTok.countTokens() > 1) {
              username = dashTok.nextToken();
          }
      }


      //
      //  Convert username field for Bishops Bay (Flexscape)
      //
      if (caller.equals( "FLEXSCAPE4865" ) && club.equals( "bishopsbay" )) {     // if Bishops Bay CC

         username = convertFlex(username);      // convert from xxxx-00n to xxxxn
      }

      //
      //  Add a 'G' to the username field (actually member number) for Greeley CC (CSG site)
      //
      if (caller.equals( "CSG7463" ) && club.equals( "fortcollins" )) {     // Greeley shares Fort Collins site

         username = "G" + username;    // Gnnn indicates Greeley member
      }

      //
      //  Trim the username for Oswego Lake (ZSMART) - they cannot pass us the member number - they can
      //  only pass us their username, which is last name + first initial (i.e. pariseb).  We set this
      //  value in our password field and use it to locate the member number (for the primary interface).
      //
      //  Our password is defined as 15 chars max, so we must trim if more than 15.  This is a little risky
      //  because there might be more than one family with a last name that matches the first 15 chars.
      //
      if (caller.equals( "ZSMART3573" ) && club.equals( "oswegolake" )) {     // if Oswego Lake CC

         username = trimZsmart(username);      // 15 chars at max
      }

      String remote_ip = req.getHeader("x-forwarded-for");
      if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();
      
      //
      // use a prepared statement to find username (string) in the DB..
      //
      try {

         String stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                             "memNum, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id " +
                             "FROM member2b " +
                             "WHERE inact = 0 AND billable = 1 AND ";
           
         if (mNumParm.equalsIgnoreCase( "yes" )) {  // username = mNum (or webid for oswego)

            //
            //  If Oswego Lake (ZSmart), then the mNum is actually a member id that we save in our password.
            //
            if (caller.equals( "ZSMART3573" ) && club.equals( "oswegolake" )) {     // if Oswego Lake CC

               stmtString += "password = ?";

            } else {

               stmtString += "memNum = ?";
            }

         } else {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

               stmtString += "webid = ?";

            } else {
              
               stmtString += "username = ?";
            }
         }

         PreparedStatement pstmt = con.prepareStatement (stmtString);

         // Get user's pw if there is a matching user...

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, username);    // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

               username = rs.getString("username");                           // get our username
            }
              
            if (mNumParm.equalsIgnoreCase( "yes" )) {     // if mNum was supplied in username

               username = rs.getString("username");                           // get our username
            }
              
            String lastName = rs.getString("name_last");                           // get last name

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
               mem_name.append(" ");
               mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string
/*
 * REMOVED 2-7-08 - Causing a problem with a member at fairbanksranch - last name Fox
            //
            //  if MembersFirst caller, check if last name matches (only check first 4 letters)
            //
            if (caller.equals( "MEMFIRST" )) {

               lname = checkName(lname, lastName);         // verify that the last names match

               if (!lname.equals( "" )) {
                 
                  out.println(SystemUtils.HeadTitle("Connection Error - Login"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H2>Invalid Credentials Received</H2><BR>");
                  out.println("<BR>Sorry, some required information is either missing or invalid.<BR>");
                  out.println("<BR>Exception: Invalid Last Name Received " + lname);
                  out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
                  out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }
            }
*/
            // Get the member's membership type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type

            // Get the member's email addresses

            oldemail1 = rs.getString("email");
            oldemail2 = rs.getString("email2");

            email_bounced = rs.getInt("email_bounced");       
            email2_bounced = rs.getInt("email2_bounced");

            iCal1 = rs.getInt("iCal1");
            iCal2 = rs.getInt("iCal2");

            // if 'activity' is being passed in and equals 'tennis' then default the user in to FlxRez
            if (req.getParameter("activity") != null) {

                int temp_activity_id = 0;

                // Stanwich was built before the common system and passes 'tennis' instead of an activity_id
                if (club.equals("stanwichclub")) {
                    if (req.getParameter("activity").equals("tennis")) {
                        temp_activity_id = 1;
                    }
                } else {
                    temp_activity_id = Integer.parseInt(req.getParameter("activity"));
                }


                if (primary.equalsIgnoreCase("yes")) {
                    if (temp_activity_id == 0) {
                        default_activity_id = -999;
                    } else {
                        default_activity_id = temp_activity_id * -1;
                    }
                } else {
                    default_activity_id = temp_activity_id;
                }

            } else {
                default_activity_id = rs.getInt("default_activity_id");
            }

            // Set iCal values to 0 for The Reserve Club so message doesn't display
            if (club.equals("thereserveclub")) {
                iCal1 = 0;
                iCal2 = 0;
            }


            // Get the number of visits and update it...

            int count = rs.getInt("count");         // Get count
//            boolean b = rs.wasNull();       // If null, change to zero
//            if (b) {
//               count = 0;
//            }
            count++;                        // bump counter..

            //  Get wc and last message displayed at login

            wc = rs.getString("wc");                            // w/c pref
            String message = rs.getString("message");       // message
            mNum = rs.getString("memNum");                      // member #

            
            pstmt.close();       //done with statement - close it
            
            
            //
            //  see if we should display a message to this member
            //
            if (message.equals( latest_message )) {      // if newest message was already displayed
              
               message = "";                              // no message to send
               
            } else {
               
               if (allowMobile == true) {
                  
                  message = "msg002";      // mobile allowed - show both messages
                  
               } else {      // no mobile - only display msg001
                  
                  if (message.equals( "" )) {
                     
                     message = "msg001";
                     
                  } else {
                     
                     message = "";
                  }
               }
            }
                        

            //
            //  If Medinah CC - do not allow certain members to login
            //
            if (club.equals( "medinahcc" )) {

               if (mship.equals( "Social" ) || mship.startsWith( "Social Pro" ) || mtype.startsWith( "FM " ) || mtype.startsWith( "Fam Member" )) {

                  errMsg = "Membership Class Not Allowed.";

                  invalidRemote(errMsg, req, out, con);
                  return;
               }
            }

            //
            //  Trace good logins - display parms passed for verification purposes
            //
            logMsg = "Remote Login Successful: Username=" +user+ ", Primary=" +primary+ ", mNum=" +mNumParm+ ", Mapping=" +mapping+ ", IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it

            recordLogin(user, "", club, remote_ip, 1);
               
            // Save the connection in the session block for later use.......
            HttpSession session = req.getSession(true);   // Create a session object

            ConnHolder holder = new ConnHolder(con);      // create a new holder from ConnHolder class

            session.setAttribute("connect", holder);      // save DB connection holder
            session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
            session.setAttribute("user", username);       // save username
            session.setAttribute("name", name);           // save members full name
            session.setAttribute("club", club);           // save club name
            session.setAttribute("caller", caller);       // save caller's name
            session.setAttribute("mship", mship);         // save member's mship type
            session.setAttribute("mtype", mtype);         // save member's mtype
            session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
            session.setAttribute("posType", posType);     // save club's POS Type
            session.setAttribute("zipcode", zipcode);     // save club's zipcode
            session.setAttribute("tlt", tlt);             // timeless tees indicator
            session.setAttribute("mobile", 0);            // NOT mobile user
            session.setAttribute("activity_id", default_activity_id);  // activity indicator
            
            //
            // set inactivity timer for this session
            //  use 10 mins to prevent user from hanging a tee slot too long
            //
            session.setMaxInactiveInterval( MEMBER_TIMEOUT );

            //
            //  If this is the primary member and primary=yes, then we must prompt the user to see which
            //  family member this is.  Member_msg will process the reply.
            //
            if (primary.equalsIgnoreCase( "yes" ) && !mNum.equals( "" )) {

               boolean primaryDone = promptPrimary(mNum, message, club, out, con);

               if (primaryDone == true) {   // if we prompted (if more than one member)

                  return;                   // reply handled by Member_msg
               }

               // If no family members were found, change default_activity_id back from -999 to 0 and re-apply the activity_id session attribute
               if (default_activity_id == -999) {
                   default_activity_id = 0;
                   session.setAttribute("activity_id", default_activity_id);  // activity indicator
               }
            }

            //
            //  Check if email addresses should be updated (only MFirst [excluding mesaverdecc] passes this)
            //
            if (!email1.equals( "" )) {         // if email address provided by caller (MFirst)
              
               FeedBack feedback = (member.isEmailValid(email1));    // validate it

               if (!feedback.isPositive()) {    // if error
                 
                  email1 = "";                  // ignore it
                    
               } else {

                  if (email2.equals( "" ) || email2 == null) {   // if 2nd address NOT provided

                     email2 = oldemail2;           // then set it equal to current email2 so not over-written
                       
                  } else {

                     feedback = (member.isEmailValid(email2));    // validate it

                     if (!feedback.isPositive()) {    // if error

                        email2 = "";                  // ignore it
                     }
                  }
               }
            }
               
            //
            //  Update the member record if email address provided by caller, 
            //  and it's different then what is in the database 
            //  currently only MFirst is passing the email1&2 parameters with the login
            //  no changes to iCal pref - we'll assume that they want the same settings
            //  note:  the roster sync performs this functionality for all new setups
            //
            
            int tmp_updateType = 0;
            String tmp_sql = "UPDATE member2b SET count = ?";
            
            if (!email1.equals( "" ) && !email1.equalsIgnoreCase(oldemail1)) {
                tmp_updateType++;
                tmp_sql += ", email = ?, email_bounced = 0";
            }
            
            if (!email2.equals( "" ) && !email2.equalsIgnoreCase(oldemail2)) {
                tmp_updateType = tmp_updateType + 2;
                tmp_sql += ", email2 = ?, email2_bounced = 0";
            }
            
            tmp_sql += " WHERE username = ?";
            
            stmt = con.prepareStatement ( tmp_sql );
            stmt.clearParameters();
            stmt.setInt(1, count);            // put the new count in statement
           // stmt.setString(2, message);       // put in the message displayed (now done in Member_msg!!)
            
            switch (tmp_updateType) {
                
                case 0:
                    
                    stmt.setString(2, username);
                    break;
                    
                case 1:        // clear the parms
                    
                    stmt.setString(2, email1);        // set email address
                    stmt.setString(3, username); 
                    break;
                    
                case 2:        // clear the parms
                    
                    stmt.setString(2, email2);        // set email address
                    stmt.setString(3, username); 
                    break;
                    
                case 3:
                    
                    stmt.setString(2, email1);        // set email address
                    stmt.setString(3, email2);        // set email address 2
                    stmt.setString(4, username); 
                    break;
            }
            stmt.executeUpdate();
            stmt.close();
               
                       
            if (!caller.equals( "MEMFIRST" ) || club.equals("mesaverdecc")) {     // if not MFirst

               //
               //   Validate the email addresses
               //
               if (!oldemail1.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(oldemail1));

                  if (!feedback.isPositive()) {              // if error

                     emailErr = feedback.get(0);             // get error message
                  }
               }
               if (!oldemail2.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(oldemail2));

                  if (!feedback.isPositive()) {              // if error

                     email2Err = feedback.get(0);             // get error message
                  }
               }

            }

            //
            //  Count the number of users logged in
            //
            countLogin("mem", con);

            // new stats logging routine
            recordLoginStat(2);
               
            //
            //  Check if this club was upgraded from V4 to V5.  If so, offer a list of changes.
            //
//            boolean upgrade = checkUpgrade(con);


            //
            //   Determine if auto-refresh to be used
            //
            boolean autoRefresh = true;

           // if (oldemail1.equals( "" ) && (!caller.equals( "MEMFIRST" ) || club.equals("mesaverdecc"))) {  // if email not present and NOT MF
            if (oldemail1.equals( "" ) && (rsync == false || club.equals("mesaverdecc"))) {  // if email not present and NOT MF

               autoRefresh = false;                                        // do not refresh
            }

            if (!emailErr.equals( "" ) || !email2Err.equals( "" ) || iCal1 == -1 || iCal2 == -1) {

               autoRefresh = false;
            }

            //
            //   If Brooklawn and email bounced - inform member
            //
          /*   On Hold!!!
            if (club.equals("brooklawn") && (email_bounced > 0 || email2_bounced > 0)) {
                 
               out.println("<HTML><HEAD><Title>Member Login Page</Title>");
               out.println("</HEAD>");
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Member Access Accepted</H2><BR>");
               out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
               out.println("<font size=\"3\">");
               out.println("<BR>Welcome <b>" + name );

               out.println("</b><BR><BR>");
               out.println("Please note that this session will terminate if inactive for more than " + (MEMBER_TIMEOUT / 60) + " minutes.");
               out.println("</b><BR><BR>");
               out.println("<h3><b>WARNING: Email Problem!!</b></h3>");
               out.println("We recently tried to send you an email at ");

               if (email_bounced == 1) {

                  out.print(oldemail1);

               } else {

                  out.print(oldemail2);
               }
               out.print(" and it bounced back to us.<br>" +
                         "We've had to temporarily disable sending you any emails until you resolve this problem.");
               out.println("<BR><BR>To correct this, update your email below, or select the 'Settings' tab from the<br>" +
                           "navigation bar on the top of most pages and follow the insructions in the email<br>" +
                           "section next to the word 'Important'.");
               out.println("<br><br>If the current email is correct, simply click 'Continue' below and ForeTees will<br>" +
                       "attempt to continue using the same email.  If you would like to remove the current email<br>" +
                       "address all together, erase it from the field below and click 'Continue'.");

               out.println("<br><br>");
                out.println("Please verify and/or change the email address(es) below.");
                out.print("<br>NOTICE: This will only change your email address in ForeTees, NOT in the website.&nbsp;&nbsp;");
                out.print("Thank you!");
                out.print("<br><br>");

                out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
                out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");
                out.println("<input type=\"hidden\" name=\"bounced\" value=\"" + ((email_bounced == 1) ? "email" : "email2") + "\">");

                out.println("<b>Email Address " + ((email_bounced == 1) ? "1" : "2") + ":</b>&nbsp;&nbsp;");
                out.println("<input type=\"text\" name=\"email\" value=\"" + ((email_bounced == 1) ? oldemail1 : oldemail2) + "\" size=\"40\" maxlength=\"50\">");
                out.println("<br><br>");

               out.println("</font></td></tr></table>");
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</input></form></font>");
               out.println("</CENTER></BODY></HTML>");               
               out.close();

               
            } else {   // all others - not Brooklawn or not bounced
         */
                           
               int delay = 1;    // default pause for welcome message

               if (club.equals("woodlandscountryclub")) {

                  delay = 3;      // 3 seconds for them
               }
                  

               //
               //  Output the response and route to system
               //
               out.println("<HTML><HEAD><Title>Member Login Page</Title>");

               if (autoRefresh == true) {

                  if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)

                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/servlet/Member_jump?name=" +eventName+ "&course=" +courseName+ "\">");

                  } else {
                     
                     if (message.equals( "" )) {      // if no message to display
                        out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=/" + rev + "/member_welcome.htm\">");
                     } else {
                        out.println("<meta http-equiv=\"Refresh\" content=\"" +delay+ "; url=/" +rev+ "/servlet/Member_msg\">");
                     }
                  }
               }
               out.println("</HEAD>");
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Member Access Accepted</H2><BR>");
               out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

               out.println("<font size=\"3\">");
               out.println("<BR>Welcome <b>" + name );

               out.println("</b><BR><BR>");
               out.println("Please note that this session will terminate if inactive for more than " + (MEMBER_TIMEOUT / 60) + " minutes.<BR><BR>");
               out.println("<br><br>");


               if (rsync == false || club.equals("mesaverdecc")) {       // if not a roster sync club

                  if (!emailErr.equals( "" ) || !email2Err.equals( "" )) {   // problem with email address?

                      if (!emailErr.equals( "" )) {   // problem with email1 address?

                          out.println("<b>Warning:</b>  Your email address (" +oldemail1+ ") is invalid.");
                          out.println("<BR>" +emailErr);

                      } else {

                          out.println("<b>Warning:</b>  Your email address (" +oldemail2+ ") is invalid.");
                          out.println("<BR>" +email2Err);
                      }

                      out.println("<BR><BR>To correct this, please change it below.");

                  } else if (oldemail1.equals( "" )) {

                      out.println("<b>Notice:</b> In order to receive email notifications and to stay informed,");
                      out.print("<br>you must maintain a current, working email address.");
                     
                  }

                  out.println("<br><br>");

                  if (autoRefresh == false) {

                     if (oldemail1.equals( "" )) {

                         out.println("Please add at least one valid email address below.");
                     
                     } else if (iCal1 == -1 || iCal2 == -1) {

                         out.println(iCalNotice);

                     } else {

                         out.println("Please verify and/or change the email address(es) below.");
                     
                     }

                     out.print("&nbsp;&nbsp;");
                     out.print("Thank you!");
                     out.print("<br><br>");

                     out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
                     out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

                     out.println("<b>Email Address 1:</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email\" value=\"" +oldemail1+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal1\">");
                      out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");

                     out.println("<b>Email Address 2:</b>&nbsp;&nbsp;");
                     out.println("<input type=\"text\" name=\"email2\" value=\"" +oldemail2+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal2\">");
                      out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");

                  } else {

                     if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)
                           out.println("<form method=\"post\" action=\"/" + rev + "/servlet/Member_jump\">");
                           out.println("<input type=\"hidden\" name=\"name\" value=\"" +eventName+ "\">");
                           out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName+ "\">");
                     } else {
                        if (message.equals( "" )) {      // if no message to display
                           out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                        } else {
                           out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
                        }
                     }
                  }

               } else {    // Roster Sync club

                  if (!eventName.equals( "" )) {      // if event signup request (go directly to _events2)
                        out.println("<form method=\"post\" action=\"/" + rev + "/servlet/Member_jump\">");
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" +eventName+ "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" +courseName+ "\">");
                  
                  } else if (iCal1 == -1 || iCal2 == -1) {
                  
                     out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
                     out.println("<input type=\"hidden\" name=\"message\" value=\"" +message+ "\">");

                      out.println(iCalNotice);

                     out.println("<b>Email Address 1:</b>&nbsp;&nbsp;"+oldemail1);
                     out.println("<input type=\"hidden\" name=\"email\" value=\"" +oldemail1+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal1\">");
                      out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");

                     out.println("<br><br>");

                     out.println("<b>Email Address 2:</b>&nbsp;&nbsp;"+oldemail2);
                     out.println("<input type=\"hidden\" name=\"email2\" value=\"" +oldemail2+ "\" size=\"40\" maxlength=\"50\">");

                     out.println("<br>&nbsp;&nbsp;&nbsp;Receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> at this email address? ");
                     out.println("<select size=\"1\" name=\"iCal2\">");
                      out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
                      out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
                     out.println("</select>");
                  
                  } else {

                    if (message.equals( "" )) {      // if no message to display
                        out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
                     } else {
                        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
                     }
                  }

               }     // end of IF rsync

               out.println("</font></td></tr></table>");
               out.println("<br>");
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</input></form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();

           //  }   // end of IF brooklawn

         } else {                               // username not found

            pstmt.close();                      // close the statement
            
            //
            //  Trace all failed login attempts
            //
            logMsg = "Remote Login Failed - Invalid User (msg#1) IP=" + remote_ip + " ";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it twice to get all info
            
            //
            //  Trace additional information and output error reply
            //
            logMsg = "Invalid Username Received. User Id " +username+ " does not exist in the ForeTees roster.";
            invalidRemote(logMsg, req, out, con);

            recordLogin(user, "", club, remote_ip, 0);

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }

         }        // end of if username found
      }
      catch (SQLException exc) {

         errMsg = "DB Error. Exception: " +exc.getMessage();

         invalidRemote(errMsg, req, out, con);
         recordLogin(user, "", club, remote_ip, 0);
         return;
      }

   } else {    // caller is not valid

      errMsg = "Invalid Parameter Received - Web Site Id Not Allowed.";

      invalidRemote(errMsg, req, out, con);
   }
 }

 // *********************************************************
 //   Extract the username from user field (encrypted)
 // *********************************************************

 private String getUser(String user) {


   char[] ca = user.toCharArray();         // put user value in char array
   int x = 0;
   int x2 = 0;
   int i = 0;
   int i2 = 0;
   int d1 = 0;
   int d2 = 0;
   int d3 = 0;
   int d4 = 0;
   int d5 = 0;
   int d6 = 99;
   int d7 = 99;
   int d8 = 99;
   int d9 = 99;
   int d10 = 99;
   int f1 = 0;
   int f2 = 0;
   int f3 = 0;
   int f4 = 0;
   int f5 = 0;
   int f6 = 0;
   int f7 = 0;
   int f8 = 0;
   int f9 = 0;
   int f10 = 0;
   int f11 = 0;

   String sd4 = "";
   String sd5 = "";
   String sd6 = "";
   String sd7 = "";
   String sd8 = "";
   String sd9 = "";
   String sd10 = "";
     
   String sf5 = "";
   String sf6 = "";
   String sf7 = "";
   String sf8 = "";
   String sf9 = "";
   String sf10 = "";
   String sf11 = "";

   int length = user.length();

   user = "";                            // init user field

   if (length < 8 || length > 22) {
     
      return (user);                     // return null if invalid length
   }

   //
   //  user (member's local #, is encrypted - we must decrypt it)
   //
   //      X F 1 F 2 F 3 F 4 F 5 F, etc.
   //
   //          X = 10 - # of digits in local# (min of 3, max of 10)
   //          F = filler (10-((prev digit + 5)/2)) 
   //          1, 2, 3, etc. = local# digits
   //
   //   Isolate each digit -
   //
   char [] charx = new char [1];
   charx[0] = ca[0];
   String sx = new String (charx);       // get x value (# of digits)

   charx[0] = ca[1];
   String sf1 = new String (charx);       // get filler 

   charx[0] = ca[2];
   String sd1 = new String (charx);       // get local# digit #1

   charx[0] = ca[3];
   String sf2 = new String (charx);       // get filler

   charx[0] = ca[4];
   String sd2 = new String (charx);       // get local# digit #2

   charx[0] = ca[5];
   String sf3 = new String (charx);       // get filler

   charx[0] = ca[6];
   String sd3 = new String (charx);       // get local# digit #3

   charx[0] = ca[7];
   String sf4 = new String (charx);       // get filler

   if (length > 8) {

      charx[0] = ca[8];
      sd4 = new String (charx);       // get local# digit #4

      charx[0] = ca[9];
      sf5 = new String (charx);       // get filler
   }
   if (length > 10) {

      charx[0] = ca[10];
      sd5 = new String (charx);       // get local# digit #5

      charx[0] = ca[11];
      sf6 = new String (charx);       // get filler
   }
   if (length > 12) {

      charx[0] = ca[12];
      sd6 = new String (charx);       // get local# digit #6

      charx[0] = ca[13];
      sf7 = new String (charx);       // get filler
   }
   if (length > 14) {

      charx[0] = ca[14];
      sd7 = new String (charx);       // get local# digit #7

      charx[0] = ca[15];
      sf8 = new String (charx);       // get filler
   }
   if (length > 16) {

      charx[0] = ca[16];
      sd8 = new String (charx);       // get local# digit #8

      charx[0] = ca[17];
      sf9 = new String (charx);       // get filler
   }
   if (length > 18) {

      charx[0] = ca[18];
      sd9 = new String (charx);       // get local# digit #9

      charx[0] = ca[19];
      sf10 = new String (charx);       // get filler
   }
   if (length > 20) {

      charx[0] = ca[20];
      sd10 = new String (charx);       // get local# digit #10

      charx[0] = ca[21];
      sf11 = new String (charx);       // get filler
   }

   try {
      x = Integer.parseInt(sx);          // get int value of X
      f1 = Integer.parseInt(sf1);        // get filler values          
      f2 = Integer.parseInt(sf2);
      f3 = Integer.parseInt(sf3);
      f4 = Integer.parseInt(sf4);
      d1 = Integer.parseInt(sd1);          // get local# digits 
      d2 = Integer.parseInt(sd2);
      d3 = Integer.parseInt(sd3);

      if (length > 8) {
         f5 = Integer.parseInt(sf5);
         d4 = Integer.parseInt(sd4);
      }
      if (length > 10) {
         f6 = Integer.parseInt(sf6);
         d5 = Integer.parseInt(sd5);
      }
      if (length > 12) {
         d6 = Integer.parseInt(sd6);
      }
      if (length > 14) {
         d7 = Integer.parseInt(sd7);
      }
      if (length > 16) {
         d8 = Integer.parseInt(sd8);
      }
      if (length > 18) {
         d9 = Integer.parseInt(sd9);
      }
      if (length > 20) {
         d10 = Integer.parseInt(sd10);
      }

   }
   catch (NumberFormatException e) {
      return (user);                     // return null user if failed
   }

   x2 = x;                              // save x value
   x = 10 - x;                          // get actual # of digits in username
   i = i + 2;                           // bump to first digit

   char[] ca2 = new char [x];           // empty array to move x chars

   while (x > 0) {

      ca2[i2] = ca[i];                  // move digits into ca2

      i2++;
      i = i + 2;
      x = x - 1;
   }

   //
   //  Verify all digits received -- filler must equal (10 - ((prev digit + 5) / 2))
   //
   //   ****** just verify first 3 for now **********
   //
   x2 = 10 - ((x2 + 5)/2);
   d1 = 10 - ((d1 + 5)/2);
   d2 = 10 - ((d2 + 5)/2);
   d3 = 10 - ((d3 + 5)/2);
     
   if (x2 != f1) {
      return (user);                     // return null user if failed
   }
   if (d1 != f2) {
      return (user);                     // return null user if failed
   }
   if (d2 != f3) {
      return (user);                     // return null user if failed
   }
   if (d3 != f4) {
      return (user);                     // return null user if failed
   }

   return new String (ca2);             // return extracted user
 }


 // *********************************************************
 //   Verify that the last name received from MemFirst matches the last name in db
 //
 //      Only check up to 4 letters in case MemFirst's name does not
 //      match exactly what we have in our db.  Also, there could be
 //      some extraneous characters in last name.
 //
 // *********************************************************

 private String checkName(String lname, String lastName) {


   char[] ca1 = lname.toCharArray();         // put lname value in char array
   char[] ca2 = lastName.toCharArray();      // put lastName value in char array
   int recLength = lname.length(); 
   int ourLength = lastName.length();
   int move = 4;
   int i = 0;
   int i2 = 0;

   if (recLength > ourLength) {
     
      return (lname);              // error
   }
   if (recLength < 2) {

      return (lname);              // error
   }
   if (recLength < 4) {

      move = recLength;     
   }
   
   char[] rec = new char [move];           // empty arrays to move names
   char[] our = new char [move];         

   while (i < move) {

      //  do not copy spaces or underscores
      if (ca1[i2] != ' ' && ca1[i2] != '_') {
         rec[i] = ca1[i2];                  // move letters into received name
         i++;
      }
      i2++;
   }

   i = 0;
   i2 = 0;

   while (i < move) {

      //  do not copy spaces or underscores
      if (ca2[i2] != ' ' && ca2[i2] != '_') {
         our[i] = ca2[i2];                  // move letters into our name
         i++;
      }
      i2++;
   }

   lname = new String (rec);       // get new lname value
   lastName = new String (our);    // get new lastName value

   if (lname.equalsIgnoreCase( lastName )) {
     
      lname = "";                  // return null = ok
   }

   return (lname);
 }


 // *********************************************************
 // Process prompt user request - prompt for member to process
 // *********************************************************

 private boolean promptPrimary(String mNum, String message, String club, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean done = false;
   boolean addit = false;
     
   String user = "";
   String fname = "";
   String mi = "";
   String lname = "";
   String mtype = "";

   int count = 0;
     
  
   //
   // find all members with this member# to display a selection list
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT m_type " +
         "FROM member2b WHERE memNum = ? AND inact = 0 AND billable = 1");

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, mNum);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      while (rs.next()) {

         mtype = rs.getString(1);

         if (club.equals( "cherryhills" )) {       // weed out juniors if Cherry Hills CC
           
            if (!mtype.startsWith( "Junior" )) {   // if not a junior

               count++;                            // count the number of members with this member#
            }

         } else {

            if (club.equals( "congressional" )) {       // weed out Dependents if Congressional

               if (!mtype.startsWith( "Dependent" ) && !mtype.endsWith( "Dependent" )) {   // if not a dependent

                  count++;                            // count the number of members with this member#
               }

            } else {

               count++;                   // count the number of members with this member#
            }
         }
      }

      pstmt.close();

      //
      //  If more than one member, then prompt for the name
      //
      if (count > 1) {

         //
         //  Output page to prompt for real user
         //
         out.println("<HTML><HEAD><Title>Member Login Page</Title>");
         out.println("</HEAD>");
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H2>Member Identification Required</H2><BR>");
         out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

         out.println("<BR>Welcome to ForeTees");
         out.println("<font size=\"2\">");
         out.println("<BR><BR>");
         out.println("Please select your name from the following list:<BR><BR>");
           
         pstmt = con.prepareStatement (
            "SELECT username, name_last, name_first, name_mi, m_type " +
            "FROM member2b WHERE memNum = ? AND inact = 0 AND billable = 1");

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, mNum);        // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         while (rs.next()) {

            user = rs.getString(1);                         
            lname = rs.getString(2);
            fname = rs.getString(3);
            mi = rs.getString(4);
            mtype = rs.getString(5);

            addit = true;                // default to ok (use boolean in case we add more checks here)
              
            //
            // weed out juniors if Cherry Hills CC
            //
            if (club.equals( "cherryhills" ) && mtype.startsWith( "Junior" )) {  

               addit = false;                             // do not add this member

            } else {

               if (club.equals( "congressional" )) {       // weed out Dependents if Congressional

                  if (mtype.startsWith( "Dependent" ) || mtype.endsWith( "Dependent" )) {   // if a dependent

                     addit = false;                             // do not add this member
                  }
               }
            }

            if (addit == true) {

               //
               //  Build the name
               //
               StringBuffer mem_name = new StringBuffer(fname);  // get first name

               if (!mi.equals( "" )) {
                  mem_name.append(" " + mi);             // add mi
               }
               mem_name.append(" " + lname);             // add last name

               String name = mem_name.toString();        // convert to one string
               String url_name = name;
               
               // add for brookings cc, but any clubs with a trailing # in their last name will need this
               if (name.endsWith("#")) {
                   
                   url_name = name.substring(0, name.length() - 1) + "%23";
               }
               
               //
               //   Output a link with the member's name
               //
               out.println("<a href=\"/" +rev+ "/servlet/Member_msg?user=" +user+ "&name=" +url_name+ "&message=" +message+ "\" style=\"color:#336633\" alt=\"" +name+ "\">");
               out.println(name + "</a><br>");
            }
         }

         pstmt.close();

         out.println("</font>");
         out.println("</td></tr></table><br>");
         out.println("</CENTER></BODY></HTML>");
         out.close();

         done = true;        // set return indicator

      }    // end of IF count 

   }
   catch (SQLException exc) {
   }
     
   return(done);
 }


 // *********************************************************
 // Process user Help request - get password
 // *********************************************************

 private void userHelp(HttpServletRequest req,
                 PrintWriter out, String user, String club, boolean mobile) {

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String password = "";
   String email = "";
   String email2 = "";
   
   int email_bounced = 0;   

   String errMsg = "Required parameters not provided.";

   //
   //  Verify parms
   //
   if (user.equals( "" ) || club.equals( "" )) {     // if user or club not provided

      invalidLogin(errMsg, req, out, con);   // process invalid login information.....
      return;
   }

   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

     con = null;
     invalidLogin(errMsg, req, out, con);   // process invalid login information.....
     return;
   }

   String club2 = club;                             // normally the same value

   if (req.getParameter("club2") != null) {         // if club2 specified (Greeley CC)

      club2 = req.getParameter("club2");            // get it (name of club site to return to)
   }
   
   //
   //  if from Mobile Login page, then convert mobile username to normal username
   //
   if (req.getParameter("mobilehelp") != null) {
      
      try {
         
         pstmt = con.prepareStatement (
            "SELECT username " +
            "FROM member2b WHERE mobile_user = ?");

         pstmt.clearParameters();         
         pstmt.setString(1, user);        // put the mobile username field in statement
         rs = pstmt.executeQuery();       

         if (rs.next()) {

            user = rs.getString(1);     // get username
         }
      }
      catch (SQLException exc) {
      }  
   }
      

   //
   // use a prepared statement to find username (string) in the DB..
   //
   try {

      if (mobile == false) {
         
         pstmt = con.prepareStatement (
            "SELECT password, name_last, name_first, name_mi, email, email2, email_bounced " +
            "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");

      } else {   // request is for mobile password (from Member_services)
         
         pstmt = con.prepareStatement (
            "SELECT mobile_pass, name_last, name_first, name_mi, email, email2, email_bounced " +
            "FROM member2b WHERE username = ? AND inact = 0 AND billable = 1");
      }
         
         
      // Get user's pw if there is a matching user...

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, user);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         password = rs.getString(1);                                  // get password or mobile pw
         String lastName = rs.getString(2);                           // get last name
         email = rs.getString("email");                               // get email address
         email2 = rs.getString("email2");                             // get email2 address
         email_bounced = rs.getInt("email_bounced");

         // Get the member's full name.......

         StringBuffer mem_name = new StringBuffer(rs.getString(3));  // get first name

         String mi = rs.getString(4);                                // middle initial
         if (!mi.equals( omit )) {
            mem_name.append(" ");
            mem_name.append(mi);
         }
         mem_name.append(" " + rs.getString(2));                     // last name

         String name = mem_name.toString();                          // convert to one string
         
         if (email.equals( "" ) || email_bounced != 0) {
            
            email = email2;             // try email 2
         }
         

         //
         //  If email address specified, send the user his/her password
         //
         if (!password.equals( "" ) && !email.equals( "" )) {     // if password & email provided

            //
            //  allocate a parm block to hold the email parms
            //
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            //
            //  Set the values in the email parm block
            //
            parme.type = "password";         // type = tee time
            parme.name = name;
            parme.user = user;
            parme.password = password;
            parme.email = email;

            //
            //  Send the email
            //
            sendEmail.sendIt(parme, con);      // in common

            //
            //  Done - reply ok
            //
            if (req.getParameter("mobilehelp") == null) {    // if NOT Mobile user
      
               out.println(SystemUtils.HeadTitle("Help Reply"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Login Credentials Have Been Emailed</H2><BR>");
               out.println("<BR>Thank you " +name+ ". You should receive an email shortly.<BR>");
               if (mobile == false) {
                  out.println("<form method=\"get\" action=\"/" +club2+ "/index.htm\">");
               } else {
                  out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_services\">");
               }
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</input></form>");
               out.println("</CENTER></BODY></HTML>");

            } else {

               out.println(SystemUtils.HeadTitleMobile("Login Help"));
               out.println("<div class=\"headertext\"> Password Has Been Emailed </div>");
               out.println("<div class=\"smheadertext\">");

               out.println("Thank you.<BR><BR>You should receive your password via email shortly.");
               
               out.println("<br><br><A HREF=\"http://m.foretees.com/" +club2+ "\">Back to Login</A>");
               out.println("</div></body></html>");           
            }
            out.close();
            return;

         } else {  // password or email address not found

            if (req.getParameter("mobilehelp") == null) {    // if NOT Mobile user
      
               out.println(SystemUtils.HeadTitle("Help Reply"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Unable to Email Credentials</H2><BR>");
               out.println("<BR>Sorry " +name+ ". We are unable to email your password<BR>");
               out.println("because you have not provided a valid email address.<BR>");
               out.println("<BR>Please contact your club's golf professionals for assistance.<BR>");
               if (mobile == false) {
                  out.println("<form method=\"get\" action=\"/" +club2+ "/index.htm\">");
               } else {
                  out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_services\">");
               }
               out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</input></form>");
               out.println("</CENTER></BODY></HTML>");
               
            } else {

               out.println(SystemUtils.HeadTitleMobile("Login Help"));
               out.println("<div class=\"headertext\"> Unable to Send Password </div>");
               out.println("<div class=\"smheadertext\">");

               out.println("Sorry, you do not have a valid email address in ForeTees.<BR><BR>Please login with your PC and select the Settings tab to add/change your mobile credentials. ");
               
               out.println("<br><br><A HREF=\"http://m.foretees.com/" +club2+ "\">Back to Login</A>");
               out.println("</div></body></html>");           
            }
            out.close();
            return;

         }

      } else {    // no match on username

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....
      }
   }
   catch (SQLException exc) {

      invalidLogin(errMsg, req, out, con);   // process invalid login information.....
   }
 }


 // ***************************************************************
 //  countLogin
 //
 //      Track the number of users logged in for each club.
 //
 // ***************************************************************

 public static void countLogin(String type, Connection con) {


    //
    //  Keep internal counts in SystemUtils - these will reset each time tomcat is bounced
    //
    if (type.equals( "pro" ) || type.equals( "mem" )) {      // skip admin and hotel users

        if (SystemUtils.startDate.equals( "" )) {             // if first login since bounce

            SystemUtils.startDate = String.valueOf( new java.util.Date() );   // set new date & time                  
        }

        //
        //  Get the hour of day (24 hr clock)
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)

        //
        //  Increment the counter for this hour
        //
        if (type.equals( "pro" )) {   

            SystemUtils.loginCountsPro[hour]++;
        }

        if (type.equals( "mem" )) {

            SystemUtils.loginCountsMem[hour]++;
        }
    }
     
 }


 // ***************************************************************
 //  initLogins  (not used 10/16/06)
 //
 //      Reset the number of users logged in for each club.
 //
 // ***************************************************************
/*
 public static void initLogins() {


   Connection con = null;                 // init DB objects
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs2 = null;

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {
      return;
   }

   //
   //   Get each club's login count and total them
   //
   try {
      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         con = dbConn.Connect(club);         // get a connection to this club's db

         stmt = con.createStatement();           // create a statement

         stmt.executeUpdate("UPDATE club5 SET logins = 0");          // reset login count

         stmt.close();

         con.close();                           // close the connection to the club db
      }                                         // do all clubs
      stmt2.close();
      con2.close();
   }
   catch (Exception ignore) {
   }

 }
*/
 
 // ***************************************************************
 //  getTLT(Connection con)
 //
 //      Get the club's time-less tees support indicator.
 //
 // ***************************************************************

 private boolean getTLT(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    int tlt = 0;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT no_reservations FROM club5");
        if (rs.next()) tlt = rs.getInt(1);
        stmt.close();
        
    } catch (Exception ignore) {
        
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
    
    return ((tlt == 1) ? true : false);
 }
 

 // ***************************************************************
 //  getRS(Connection con)
 //
 //      Get the club's Roster Sync support indicator.
 //
 // ***************************************************************

 private int getRS(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    int rsync = 0;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT rsync FROM club5");
        if (rs.next()) rsync = rs.getInt(1);
        stmt.close();
      
    } catch (Exception ignore) {
       
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return (rsync);
 }


 // ***************************************************************
 //  getLottery
 //
 //      Get the club's Lottery Support Indicator.
 //
 // ***************************************************************

 private String getLottery(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    int lottery = 0;
    String lotteryS = "0";

    try {
        
        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT lottery FROM club5");          // get lottery flag

        if (rs.next()) lottery = rs.getInt("lottery");
        
        stmt.close();

    } catch (Exception ignore) {

    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }
     
    if (lottery > 0) {

        lotteryS = "1";        // return 'lottery supported' indicator
    }

    return(lotteryS);
 }


 // ***************************************************************
 //  getPOS
 //
 //      Get the club's POS Type.
 //
 // ***************************************************************

 private String getPOS(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    String posType = "";

    try {
        
        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT posType FROM club5");          // get pos type

        if (rs.next()) posType = rs.getString("posType");
        
        stmt.close();

    } catch (Exception ignore) {
       
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return(posType);
 }


 // *********************************************************
 //  Convert username from xxxx-00n to xxxxn
 // *********************************************************

 private final static String convertFlex( String s ) {

   String part1 = s;
   String part2 = "";
     
   int suf = 0;

   //
   //  parse the string to get the '-00n'
   //
   StringTokenizer tok = new StringTokenizer( s, "-" );

   if ( tok.countTokens() > 1 ) {

      part1 = tok.nextToken();
      part2 = tok.nextToken();

      suf = Integer.parseInt(part2);     // convert to int to get absolute number (i.e.  001 = 1)
   }

   part1 = part1 + suf;                  // combine (default = xxxx0)

   return (part1);

 } // end convertFlex


 // ********************************************************************************
 //  Trim the username if more than 15 characters - ZSmart Web - Oswego Lake CC
 // ********************************************************************************

 private final static String trimZsmart( String s ) {

   char[] ca = s.toCharArray();

   char[] ca2 = new char [15];      // new char array

   String user = s;
     
   if (ca.length > 15) {
     
      for (int i=0; i < 15; i++) {

         ca2[i] = ca[i];               // copy first 15 chars
      }

      user = new String (ca2);         // return trimmed down username
   }

   return (user);

 } // end trimZsmart


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero( String s ) {

      int memid = 0;
      String newS = "";

      //
      //  convert string to int to drop leading zeros
      //
      try {
         memid = Integer.parseInt(s);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      newS = String.valueOf( memid );      // convert back to string

      return new String (newS);

 } // end remZero


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero2( String s ) {

      int i = 0;
      int count = 0;
      String s2 = s;
      char[] ca = s.toCharArray();

      loop1:
      while (i < ca.length) {
        
         char letter = ca[i];
         if ( letter == '0' ) {        // if leading zero
            count++;                   // count them           
         } else {
            break loop1;               // else exit
         }
         i++;
      }
        
      if (count > 0) {
           
         char[] ca2 = new char [ca.length - count];      // new char array
           
         i = 0;
         while (count < ca.length) {

            char letter = ca[count];                     // set new string w/o zeros
            ca2[i] = letter;
            i++;
            count++;
         }           
         s2 = new String (ca2);
      }
      
      return (s2);

 } // end remZero2


 // *********************************************************
 //  Strip last letter from end of string
 // *********************************************************

 private final static String stripA2( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      }

      return new String (ca2);

 } // end stripA2


 // *********************************************************
 // Check if this club was upgraded from V4 to V5
 // *********************************************************

 private boolean checkUpgrade(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    boolean upgrade = false;

    try {
       
        stmt = con.createStatement();        // create a statement
        rs = stmt.executeQuery("SELECT multi FROM club2");   // does club2 exist in this system ?

        if (rs.next()) upgrade = true;

        stmt.close();

    } catch (Exception ignore) {

    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return(upgrade);
 }


 // *********************************************************
 // Connection error received - inform user to try again....
 // *********************************************************

 private void Connerror(PrintWriter out, Exception exc, Connection con) {

   out.println(SystemUtils.HeadTitle("Connection Error - Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Connection Error</H2><BR>");
   out.println("<BR>Sorry, we are unable to connect to the system database at this time.<BR>");
   out.println("<BR>Exception: " + exc.getMessage());
   out.println("<BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   if (con != null) {
      try {
         con.close();
      }
      catch (SQLException ignored) {
      }
   }
 }

 private void Connerror2(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Connection Error - Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Connection Error2</H2><BR>");
   out.println("<BR>Sorry, we are unable to connect to the system database at this time.<BR>");
   out.println("<BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Invalid Login data received - inform user to try again....
 // *********************************************************

 private void invalidLogin(String errMsg, HttpServletRequest req, PrintWriter out, Connection con) {

    
    invalidLogin(errMsg, req, out, con, 0);          // must not be a mobile user
 }
  
 private void invalidLogin(String errMsg, HttpServletRequest req, PrintWriter out, Connection con, int mobile) {

  
   String user = "";
   String club = "";
   String pw = "";
     
   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");    
   }
   if (req.getParameter("user_name") != null) {

      user = req.getParameter("user_name");
   }

   if (req.getParameter("password") != null) {

      pw = req.getParameter("password");
   }

   //
   //  Trace all login attempts
   //
   if (con != null) {
     
      String logMsg = "Login Failed - Invalid Login - Error: " +errMsg;
      SystemUtils.sessionLog(logMsg, user, pw, club, omit, con);                   // log it
   }

   if ( club.equals("newcanaan") && user.startsWith("proshop") ) {
   
       out.println(SystemUtils.HeadTitle("Invalid Login"));
       out.println("<script>window.location.href='http://216.243.184.83:8080/login.htm';</script>");
       out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
       out.println("<hr width=\"40%\">");
       out.println("<p>&nbsp;</p><p>&nbsp;</p>");
       out.println("<BR><H2>Login Rejected</H2><BR>");
       out.println("<BR>The login information you submitted was either missing or invalid.");
       out.println("<BR><BR>Error: " +errMsg);
       out.println("<BR><BR><BR>Please <A HREF=\"javascript:void(0)\">try again</A>.");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       
   } else if (mobile > 0) {

      //
      //  Mobile user
      //
      out.println(SystemUtils.HeadTitleMobile("Invalid Login"));
      out.println("<div class=\"headertext\"> Login Rejected </div>");
      out.println("<div class=\"smheadertext\">");
      out.println("The login information you submitted<BR>was either missing or invalid.");
      out.println("<BR><BR>Error: " +errMsg);
      out.println("<BR><BR>NOTE: Remember to use your MOBILE credentials.<BR>You can set or change these<BR>by logging in from your PC and<BR>clicking on the Settings tab.");
      out.println("<BR><BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
      out.println("</div></body></html>");           
      
   } else {
 
       out.println(SystemUtils.HeadTitle("Invalid Login"));
       out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
       out.println("<hr width=\"40%\">");
       out.println("<p>&nbsp;</p><p>&nbsp;</p>");
       out.println("<BR><H2>Login Rejected</H2><BR>");
       out.println("<BR>The login information you submitted was either missing or invalid.");
       out.println("<BR><BR>Error: " +errMsg);
       out.println("<BR><BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
       out.println("</CENTER></BODY></HTML>");
       out.close();
   }
   
 }

 // *********************************************************
 // Invalid call from remote user - reject
 // *********************************************************

 private void invalidRemote(String errMsg, HttpServletRequest req, PrintWriter out, Connection con) {


   String user = "";
   String club = "";
   String pw = "";
   String caller = "";
   String primary = "";
   String mNum = "";
   String mapping = "";
   String strip = "";

   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");
   }
   if (req.getParameter("user_name") != null) {

      user = req.getParameter("user_name");
   }

   if (req.getParameter("password") != null) {

      pw = req.getParameter("password");
   }

   if (req.getParameter("caller") != null) {

      caller = req.getParameter("caller");
   }

   if (req.getParameter("primary") != null) {

      primary = req.getParameter("primary");
   }

   if (req.getParameter("mnum") != null) {

      mNum = req.getParameter("mnum");
   }

   if (req.getParameter("mapping") != null) {

      mapping = req.getParameter("mapping");      // get mapping parm - used to map member ids
   }
     
   if (req.getParameter("stripzero") != null) {

      strip = req.getParameter("stripzero");   
   }

   //
   //  Trace all login attempts
   //
   if (con != null) {

      String logMsg = "Login Failed - Invalid Remote - Error: " +errMsg;
      logMsg = logMsg + " Primary=" +primary+ ", mNum=" +mNum+ ", mapping=" +mapping+ ", stripzero=" +strip+ ", ";
      SystemUtils.sessionLog(logMsg, user, pw, club, caller, con);                   // log it
        
      try {

         con.close();                // close the connection

      }
      catch (Exception exp) {
      }

   }


   out.println(SystemUtils.HeadTitle("Invalid Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<p>&nbsp;</p><p>&nbsp;</p>");
   out.println("<BR><H2>Access Rejected</H2><BR>");
   out.println("<BR>Some information provided was either missing or invalid.");
   out.println("<BR><BR>Error: " +errMsg);
   out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
   out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }


 private void rejectCaller(PrintWriter out) {
 
      out.println(SystemUtils.HeadTitle("Invalid Request"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Invalid Access</H2><BR>");
      out.println("<BR>Sorry, some required information is missing.");
      out.println("<BR>Please return to your club's website and try again, or contact your club's website administrator.  Thank you.<BR>");
      out.println("<BR><BR>");
      out.println("<form><input type=\"button\" value=\"Close\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();      
 }
 
 
 private void rejectReferer(PrintWriter out) {
 
      out.println(SystemUtils.HeadTitle("Invalid Request"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Invalid Access</H2><BR>");
      out.println("<BR>Sorry, you are not authorized to access ForeTees without going through your club's website or the ForeTees login.");
      out.println("<BR>Please return to your club's website, or contact your club's website administrator.");
      out.println("<BR><BR>If you feel you recevied this message in error, please contact ForeTees at support@foretees.com.");
      out.println("<BR>Include the name of your club, your name and your member number. Thank you.");
      out.println("<BR><BR>");
      out.println("<form><input type=\"button\" value=\"Close\" style=\"text-decoration:underline; background:#8B8970\" onClick='self.close();'></form>");
      out.println("</CENTER></BODY></HTML>");
      out.close();      
 }
 
 
 // *********************************************************
 //  Mmember 'Continue' with possible emaill addresses
 // *********************************************************

 private void processEmail(HttpServletRequest req, PrintWriter out) {

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) return;

   con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) return;

   Member member = new Member();

   String club = (String)session.getAttribute("club");   // get user's club
   String caller = (String)session.getAttribute("caller");   // get caller (mfirst, etc.)
   String user = (String)session.getAttribute("user");   // get user's username value

   String message = "";
   String email = "";
   String email2 = "";
   String bounced = "";
   String replyMsg = "You will now be directed to ForeTees.";

   int pause = 0;
   int iCal1 = 0;
   int iCal2 = 0;
     
   //
   //  Get club message and possible emails
   //
   if (req.getParameter("message") != null) {

      message = req.getParameter("message");
   }
   if (req.getParameter("email") != null) {

      email = req.getParameter("email").trim();
   }
   if (req.getParameter("email2") != null) {

      email2 = req.getParameter("email2").trim();
   }
   if (req.getParameter("bounced") != null) {
       
      bounced = req.getParameter("bounced");
   }
   if (req.getParameter("iCal1") != null) {

       iCal1 = (req.getParameter("iCal1").equals("1")) ? 1 : 0;
   }
   if (req.getParameter("iCal2") != null) {

       iCal2 = (req.getParameter("iCal2").equals("1")) ? 1 : 0;
   }
   
   email = email.trim();              // remove any spaces
   email2 = email2.trim();            // remove any spaces

   //
   //  Check if either emaill addresses were added
   //
   if (!email.equals( "" ) || !email2.equals( "" )) {      
     
      pause = 5;

      //
      //  Verify the email address(es)
      //
      if (!email.equals( "" )) {                   // if specified

         FeedBack feedback = (member.isEmailValid(email));

         if (!feedback.isPositive()) {              // if error

            email = "";                             // do not add
         }
      }
      if (!email2.equals( "" )) {                   // if specified

         FeedBack feedback = (member.isEmailValid(email2));

         if (!feedback.isPositive()) {              // if error

            email2 = "";                            // do not add
         }
      }

      if (email.equals( "" ) && email2.equals( "" )) {    // if failed

         replyMsg = "The email address you entered is not valid and has not been added to the system.<br>You will now be directed to ForeTees.";
      }
   }

   // if we're here to handle to bounced email response - let's handle that first
   if (!bounced.equals("")) {
       
       try {
           
           pstmt = con.prepareStatement("SELECT email, email2 FROM member2b WHERE username = ?");
           pstmt.clearParameters();
           pstmt.setString(1, user);
           rs = pstmt.executeQuery();
              
           if (rs.next()) {
           
               if (email.equalsIgnoreCase(rs.getString("email")) || email.equalsIgnoreCase(rs.getString("email2"))) {

                   pstmt = con.prepareStatement("UPDATE member2b SET " + bounced + "_bounced='0' WHERE username = ?");
                   pstmt.clearParameters();
                   pstmt.setString(1, user);
                   pstmt.executeUpdate();
                   
                   replyMsg = "A new email was not specified, current email is no longer flagged as bouncing.<br>" +
                           "If the email is still incorrect, it will be flagged again after the next bounced email.<br>" +
                           "You will now be directed to ForeTees.";
               } else {

                   try {

                       pstmt = con.prepareStatement("UPDATE member2b SET " + bounced + "= ?, " +
                               bounced + "_bounced='0' WHERE username = ?");
                       pstmt.clearParameters();;
                       pstmt.setString(1, email);
                       pstmt.setString(2, user);
                       pstmt.executeUpdate();

                       replyMsg = "Your email address has been changed in the system.<br>You will now be directed to ForeTees.";
                   } catch (Exception exc) {

                        SystemUtils.buildDatabaseErrMsg("DB error in Login.processEmail.", exc.toString(), out, true);
                        replyMsg = "There was a problem adding your email address to the system.<br>You will now be directed to ForeTees.";
                   }
               }
               
               // Make sure emails are set up correctly
               Support_errorlog.ensureCorrectEmailSetup(user, club, out);
           }
       } catch (Exception exc) {
           
           SystemUtils.buildDatabaseErrMsg("DB error in Login.processEmail.", exc.toString(), out, true);
           replyMsg = "There was a problem accessing your database.<br>You will now be directed to ForeTees.";
       
       } finally {

          if (rs != null) {
             try {
                rs.close();
             } catch (SQLException ignored) {}
          }

          if (pstmt != null) {
             try {
                pstmt.close();
             } catch (SQLException ignored) {}
          }
       }
       
   } else if (!email.equals( "" ) || !email2.equals( "" )) {
   
      replyMsg = "Your email address has been added to the system.<br>You will now be directed to ForeTees.";
        
      try {

         //
         //  Store the email address(es)
         //
         if (!email.equals( "" ) && !email2.equals( "" )) {    // if both provided and ok

            pstmt = con.prepareStatement( "UPDATE member2b SET " +  ((iCal1 == 1 || iCal2 == 1) ? "emailOpt = 1, " : "") + "email = ?, email2 = ?, iCal1 = ?, iCal2 = ? WHERE username = ?" );
            pstmt.clearParameters();
            pstmt.setString(1, email);
            pstmt.setString(2, email2);
            pstmt.setInt(3, iCal1);
            pstmt.setInt(4, iCal2);
            pstmt.setString(5, user);

         } else {
        
            if (!email.equals( "" )) {    // if one provided and ok

               pstmt = con.prepareStatement( "UPDATE member2b SET " +  (iCal1 == 1 ? "emailOpt = 1, " : "") + "email = ?, iCal1 = ?, iCal2 = 0 WHERE username = ?" );
               pstmt.clearParameters();
               pstmt.setString(1, email);
               pstmt.setInt(2, iCal1);
               pstmt.setString(3, user);

            } else {
                
               pstmt = con.prepareStatement( "UPDATE member2b SET " +  (iCal2 == 1 ? "emailOpt = 1, " : "") + "email2 = ?, iCal2 = ?, iCal1 = 0 WHERE username = ?" );
               pstmt.clearParameters();
               pstmt.setString(1, email2);
               pstmt.setInt(2, iCal2);
               pstmt.setString(3, user);
            }
         }
           
         pstmt.executeUpdate();
         pstmt.close();
            
      } catch (Exception exp) {
      
         SystemUtils.buildDatabaseErrMsg("DB error in Login.processEmail.", exp.toString(), out, true);
         replyMsg = "There was a problem adding your email address to the system.<br>You will now be directed to ForeTees.";
      
      } finally {

          if (pstmt != null) {
             try {
                pstmt.close();
             } catch (SQLException ignored) {}
          }
       }

   } else {

       // both email address were empty - user just skipped past and did not enter an address
       // so lets clear their iCal fields and set them to zero so we don't hit on this again during their subsequent login
       try {

           pstmt = con.prepareStatement( "UPDATE member2b SET iCal1 = 0, iCal2 = 0 WHERE username = ?" );
           pstmt.clearParameters();
           pstmt.setString(1, user);
           pstmt.executeUpdate();
           pstmt.close();

       } catch (Exception exp) {
           
           SystemUtils.buildDatabaseErrMsg("DB error #2 in Login.processEmail.", exp.toString(), out, true);
           replyMsg = "There was a problem clearing your iCal flag.<br>You will now be directed to ForeTees.";

       } finally {

          try { pstmt.close();
          } catch (SQLException ignore) {}

       }
   }

   //
   //   Output continue page
   //
   out.println("<HTML><HEAD><Title>Member Login Page 2</Title>");

   if (message.equals( "" )) {      // if no message to display
      out.println("<meta http-equiv=\"Refresh\" content=\"" +pause+ "; url=/" + rev + "/member_welcome.htm\">");
   } else {
      out.println("<meta http-equiv=\"Refresh\" content=\"" +pause+ "; url=/" +rev+ "/servlet/Member_msg\">");
   }
   out.println("</HEAD>");
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Thank You!</H2><BR>");
   out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
   out.println("<font size=\"3\">");
   out.println("<BR>" +replyMsg );

   out.println("<br><br>");
   out.println("</font></td></tr></table>");
     
   if (message.equals( "" )) {      // if no message to display
      out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
   } else {
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
   }
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   
   return;
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpAOL8(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  From the AOL Toolbar, click on 'Settings'<br>");
      out.println(" 2.  select 'Preferences'<br>");
      out.println(" 3.  select 'Internet Properties (WWW)'<br>");
      out.println(" 4.  select the 'Privacy' tab<br>");
      out.println(" 5.  Adjust the slidebar so the setting is on 'Medium High'<br>");
      out.println(" 6.  select 'Advanced'<br>");
      out.println(" 7.  Deselect the 'Override automatic cookie handling button' <br>");
      out.println(" 8.  click on 'Ok' to exit<br>");
      out.println(" 9.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpIE6(PrintWriter out) {
    
      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  click on 'Tools' on the menu bar<br>");
      out.println(" 2.  select 'Internet Options'<br>");
      out.println(" 3.  click on the 'Privacy' tab<br>");
      out.println(" 4.  Adjust the slidebar so the setting is on 'Medium High'<br>");
      out.println(" 5.  select 'Advanced'<br>");
      out.println(" 6.  Deselect 'override automatic cookie handling'<br>");
      out.println(" 7.  click on 'Ok' to save and exit<br>");
      out.println(" 8.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Increment the login counts in the Vx login_stats table
 // *********************************************************
 private void recordLoginStat(int user_type_id) {
     
    Connection con = null;
    Statement stmt = null;
    
    int server_id = Common_Server.SERVER_ID;            
    
    try {
        
        con = dbConn.Connect(rev);
        stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO login_stats (entry_date, hour, node, user_type_id, login_count) VALUES (now(), DATE_FORMAT(now(), \"%H\"), \"" + server_id + "\", \"" + user_type_id + "\", 1) ON DUPLICATE KEY UPDATE login_count = login_count + 1");
        
    } catch (Exception ignore) {
        
    } finally {
        
        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
        
        if (con != null) {
           try {
              con.close();
           } catch (SQLException ignored) {}
        }
    }
    
    con = null;
    stmt = null;
    
 }
 
 
 // *********************************************************
 // Record the detils of this login for record keeping
 // *********************************************************
 private void recordLogin(String user, String pass, String club, String ip, int success) {

    Connection con = null;
    PreparedStatement pstmt = null;
    
    try {
        
        con = dbConn.Connect(rev);
        pstmt = con.prepareStatement ("" +
                "INSERT INTO logins (club, username, password, ip, node, success, datetime) " +
                "VALUES (?, ?, ?, ?, ?, ?, now());");
        pstmt.clearParameters();
        pstmt.setString(1, club);
        pstmt.setString(2, user);
        pstmt.setString(3, pass);
        pstmt.setString(4, ip);
        pstmt.setInt(5, Common_Server.SERVER_ID);
        pstmt.setInt(6, success);
           
        pstmt.executeUpdate();
        
    } catch (Exception e) {
        
        SystemUtils.logError(e.getMessage());
        
    } finally {
        
        if (pstmt != null) {
           try {
              pstmt.close();
           } catch (SQLException ignored) {}
        }
        
        if (con != null) {
           try {
              con.close();
           } catch (SQLException ignored) {}
        }
    }
    
    pstmt = null;
    con = null;
    
 }
 
 
 private static void systemTest(HttpServletRequest req, PrintWriter out) {
     
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;
    
    int server_id = Common_Server.SERVER_ID;
    int pcount = 0;
    
    String tmp = (server_id == 4) ? "db2" : "tcnode" + server_id;
    
    try {
        
        con = dbConn.Connect("demov4");
        stmt = con.createStatement();
        rs = stmt.executeQuery("SHOW PROCESSLIST"); // SHOW STATUS LIKE 'Threads_connected'
        while ( rs.next() ) {
            
            //if ( rs.getString("Host").startsWith(tmp) ) pcount++;
            pcount++;
        }
        rs.close();
        stmt.close();
        con.close();
        
    } catch (Exception exp) { 

        pcount = -1;

    } finally {

      if (rs != null) {
         try {
            rs.close();
         } catch (SQLException ignored) {}
      }

      if (stmt != null) {
         try {
            stmt.close();
         } catch (SQLException ignored) {}
      }
      
      if (con != null) {
         try {
            con.close();
         } catch (SQLException ignored) {}
      }
    }
    
    out.print("N" + server_id + ":" + ((pcount < 1) ? "DB-FAIL" : "DB"+pcount));
    
    File f = null;
    FileReader fr = null;
    BufferedReader br = null;
    boolean failed = true; // default result fail
    
    tmp = "";
    
    try {
        
        f = new File("/home/rosters/tmp/test.txt");
        fr = new FileReader(f);
        br = new BufferedReader(fr);
        if (f.isFile()) failed = false;
        
    } catch (Exception ignore) { 
        
    } finally {

      if (br != null) {
         try {
            br.close();
         } catch (Exception ignored) {}
      }

      if (fr != null) {
         try {
            fr.close();
         } catch (Exception ignored) {}
      }
      
      if (f != null) f = null;
      
    }
    
    out.print(":" + ((!failed) ? "NFS" : "NFS-FAIL"));
    out.println("");    
 }

 
 //
 //  Mobile count method - bump counter and gather mobile device data
 //
 private static void countMobile(int mobile_count, int mobile_iphone, String user, HttpServletRequest req, Connection con) {
     
 
   PreparedStatement stmt = null;
   
   boolean iphone = false;
 
 
   //
   //  Gather the User Agent String from the request header
   //
   String ua = req.getHeader("user-agent").toLowerCase();

   if (ua.indexOf("iphone") > -1 || ua.indexOf("ipod") > -1) {

	// found an iphone or ipod
      iphone = true;

   } else if(ua.indexOf("ipad") > -1) {                   // checks for future stats !!!!!!!!!!!

	// found an iPad

   } else if(ua.indexOf("android") > -1) {

	// found an android device

   } else if(ua.indexOf("blackberry") > -1) {

	// found a blackberry device

   } else if(ua.indexOf("opera mini") > -1) {

	// found opera mini browser

   } else if(ua.indexOf("windows ce") > -1 || ua.indexOf("smartphone") > -1 || ua.indexOf("iemobile") > -1) {

	// found windows mobile device
   }

   
   
   //
   //   Increment the mobile counter for this member and update the account
   //
   mobile_count++;
   
   if (iphone == true) mobile_iphone++;     // bump iphone counter if its an iPhone
 
   try {
 
      stmt = con.prepareStatement (
         "UPDATE member2b SET mobile_count = ?, mobile_iphone = ? WHERE username = ?");

      stmt.clearParameters();          
      stmt.setInt(1, mobile_count);     // new mobile count  
      stmt.setInt(2, mobile_iphone);    // new iphone count  
      stmt.setString(3, user);          // username 
      stmt.executeUpdate();

      stmt.close();
            
   } catch (Exception ignore) { 
        
   } finally {

     if (stmt != null) {
        try {
           stmt.close();
        } catch (SQLException ignored) {}
     }      
   }
            
 }       // end of countMobile
 
 
}