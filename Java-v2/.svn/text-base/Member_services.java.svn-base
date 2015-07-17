/***************************************************************************************
 *   Member_services:  This servlet will process the 'change password' request from
 *                     Member's services page.
 *
 *
 *   called by:  member_main.htm
 *
 *   created: 12/05/2001   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 * 
 *        7/09/14   Add mobile app auth code generation
 *        5/16/14   Scioto CC (sciotocc) - Allow all members to access their old mobile credentials so they can keep using it.
 *        4/03/14   Philly Cricket Club (philcricket) - Do not allow members to change their default mode of trans (case 2401).
 *        1/16/14   Update links that were still pointing to Dining_home
 *       12/04/13   Add email options for club and member emails.
 *       12/03/13   Tonto Verde (tontoverde) - Removed the custom to hide the mobile setup options.
 *       11/11/13   Tonto Verde (tontoverde) - Hide the mobile setup options.
 *        6/06/13   Updated a couple links to make sure they're displayed in blue and underlined.
 *        2/04/13   Add the member's login count for display purposes only.
 *        1/28/13   Do not allow member to change emails or phone numbers if Flexscape-Connect Premier.
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        1/09/13   TEMP - allow only Philly Cricket users to access mobile settings from other than golf.
 *       12/12/12   Open up the mobile settings for all activities.
 *       12/05/12   Hillwood CC (hillwoodcc) - Allow members to update their emails despite using roster sync.
 *        9/06/12   Pass old phone numbers and update them if either has been changed.  This corrects a problem where members
 *                  could not remove a phone number.
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        4/05/12   Aronimink GC (aronimink) - Added custom to hide mention of notifications.
 *        3/30/12   Add a setting for the 'show more tee times' option for golf (check box in Member_select).
 *        3/08/12   Reflection Ridge GC (reflectionridgegolf) - Added to custom to require that members enter at least one email address (case 2130).
 *        2/24/12   Add mobile link and help to the mobile settings block to better assist memebers with mobile access. 
 *        1/12/12   New skin changes.
 *        9/22/11   Reverted Email Opt verbiage to indicate that the setting applies to reservations and event notifications.
 *        8/24/11   Do not include Golf settings if from Dining.
 *        7/01/11   Dallas Athletic Club (dallasathleticclub) - Added custom to require the members enter at least one email address (case 2002).
 *        5/19/11   Charlotte CC (charlottecc) - Allow members to update their email addresses since we are no longer updating them with roster sync for this club.
 *        5/17/11   Default activity selection will be hidden on the golf side if FlxRez Staging mode is turned on.
 *        4/25/11   Hawks Landing GC (hawkslandinggolfclub) - Do not display phone number fields for Hawks Landing GC.
 *        4/18/11   Hawks Landing GC (hawkslandinggolfclub) - Display custom message to members instructing them to contact the club to change their email at the club.
 *        4/05/11   Omaha CC (omahaccc) - Added sendOmahaEmail() method and call to this method whenever a member changes an email address (case 1963).
 *        3/25/11   Talbot CC (talbotcc) - Custom to require the members enter at least one email address (case 1954).
 *        3/14/11   Updated verbiage for email option to indicate that event notifications will also be affected.
 *        1/20/10   Added processing to port any applicable changes over to the dining system if the club has an organization_id.
 *        6/03/10   Allow Flexscape & NorthStar Rsync members to change their email addresses as our roster sync process does not 
 *                  process the emails received in the rsync file.
 *        2/11/10   Do not show Mobile info logged into FlxRez
 *        1/26/10   Do not show Mobile info if Mobile not supported
 *        1/14/10   Add credentials for Mobile users - username & password.
 *       10/07/09   GenRes - add tennis parms (USTA number and NTRP Rating). 
 *       10/03/09   Change any references to golf or tee times - for Activities.
 *        9/14/09   Added ability for members to specify their default activity
 *        9/10/09   Allow users that came through the ForeTees website interface (CE Bypass) to change their passwords.
 *        9/02/09   Added sendFoxDenEmail() method and call to this method whenever a member changes his/her email address(es)
 *        7/24/09   Add support for mobile devices.
 *        6/14/09   Disable changing of default transportation option for Valley Club
 *        5/19/09   Add iCal attachment options to form
 *        2/23/09   Do not allow email address changes if Roster Sync club unless overridden.
 *       11/10/08   Added tmode abbreviation to display part of select box
 *        9/02/08   Javascript compatability updates
 *        7/17/08   Allow email changes for Mesa Verde CC even though they come through MF
 *        6/26/08   changed Pro Only Tmodes check so it doesn't make an unecessary database call
 *        6/12/08   Restrict Pro-Only modes of trans options from being displayed for members (they only show up if already their default!)
 *        6/11/08   Sonnenalp - do not allow ICP and FCP modes of trans options for members (case 1452).
 *        4/10/08   Dorset FC - allow them to change email addresses.  They use MF but not Roster Sync.
 *        2/10/08   Remove AGT referrences - not used.
 *        2/10/08   Marbella - do not allow members to change their mode of trans (case 1380).       
 *        9/12/07   Make sure CE clubs use roster sync.
 *        9/07/07   Do not allow AMY CE roster sync clubs to change their emails.
 *        6/26/07   Hide course hdcp box if using GHIN, provide them a link to Member_handicaps get course hdcp
 *        6/07/07   Do not allow some CE roster sync clubs to change their emails.
 *        4/04/07   Increase the password field from 10 to 15.
 *        3/25/07   Changes for hdcp - club_num & assoc_num are now strings not integers
 *        3/19/07   Changes for allowing/disallowing changing hncp values depending on hdcpSystem specified for club
 *        1/20/07   Allow for Interlachen Spa users - do not allow them to change all options.
 *       10/20/06   Allow for AGT users - do not allow them to change all options.
 *       10/09/06   Changes for email bounce indication
 *        9/23/06   Enhancements for TLT version - Add SystemLingo support
 *        7/04/06   Minor verbiage change
 *       11/04/05   Pelicans Nest - send an email to the admin whenever a member changes their email address.
 *        9/02/05   RDP - change call to isEmailValid - only pass the email address.
 *        7/07/05   MFirst - do not allow members to change their emails.
 *        6/24/05   Blackhawk - do not allow members to change their mode of trans.
 *       11/15/04   Ver 5 - Add 2nd email address and 2 phone numbers.
 *        7/14/04   Do not show handicaps for Old Oaks.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/13/04   JAG Modifications to match the new color scheme
 *        7/18/03   Enhancements for Version 3 of the software.
 *       10/04/02   Added handicaps to parms that can be updated.
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_Server;
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;                
import java.sql.*;

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.client.SystemLingo;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.BasicSHA256;
import com.foretees.common.mobileAPI;


public class Member_services extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the initial call from member_main.htm
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
        
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   ResultSet rs = null;
   Statement stmt = null;
   PreparedStatement pstmt = null;

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) return;

   //
   //  get the club name
   //
   String user = (String)session.getAttribute("user");   // get username
   long member_id = (Long)session.getAttribute("member_id");   // get member_id
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name
   boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
 //String msubtype = (String)session.getAttribute("msubtype");       // get member's sub_type
   String msubtype = Utilities.getSessionString(req, "msubtype", "");      // get member sub-type
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String email = "";
   String email2 = "";
   String wc = "";         // trans mode name
   String wca = "";        // trans mode acronym
   String phone1 = "";
   String phone2 = "";
   String owc = "";
   String course = "";
   String ghin = "";
   String usta = "";      // Tennis USTA number
   String mobile_user = "";
   String mobile_pass = "";
   String last_name = "";

   int rsync = 0;
   int emailOpt = 0;
   int emailOpt2 = 0;
   int clubEmailOpt1 = 0;
   int clubEmailOpt2 = 0;
   int memEmailOpt1 = 0;
   int memEmailOpt2 = 0;
   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int mobile = 0;              // Mobile user
   int default_activity_id = 0;
   int foretees_mode = 0;
   int tee_sheet_jump = 0;
   int login_count = 0;

   float c_hancap = 0;
   float g_hancap = 0;
   float ntrp = 0;              // Tennis rating value
   
   boolean activity_system = false;
   boolean allowMobile = false;
   boolean dining = false;
   boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
   
   boolean clubCentral = mobileAPI.isMobileAppEnabledForClub(club, 1, con);
   boolean clubCentralStaging = false;
   if (clubCentral) clubCentralStaging = mobileAPI.isMobileAppStagingForClub(club, 1, con);  // ClubCentral app in staging mode for this club?
   
   boolean clubCentralOnly = req.getParameter("clubCentral") != null;    // if caller only wants to see the ClubCentral Activation section (from Premier site)
   

   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   

 //  boolean new_skin = (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID);
   if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
      
      new_skin = true;
      dining = true;
   }


   //
   //  See if Mobile user
   //
   try {      
      mobile = (Integer)session.getAttribute("mobile");        
   }
   catch (Exception ignore) {   
      mobile = 0;
   }
      
   
   if (mobile > 0) {        // if MOBILE user
      
      doMobile(req, out, con);
      return;
   }
   
   
   //
   //  Non-mobile users
   //
   Statement stmtc = null;

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block
   

   //
   // check which (if any) handicap system is used for this club
   //
   String hdcpSystem = "";
   String club_num = "";
   String club_assoc = "";
   
   try {

       stmt = con.createStatement();
       rs = stmt.executeQuery("SELECT hdcpSystem, rsync, foretees_mode, allow_mobile FROM club5;");
       if (rs.next()) {
           hdcpSystem = rs.getString("hdcpSystem");
           rsync = rs.getInt("rsync");
           foretees_mode = rs.getInt("foretees_mode");
           if (rs.getInt("allow_mobile") == 1) {
               allowMobile = true;
           }
       }
    
   } catch (Exception exc) {
        
       SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
       return;

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

   }
   
   if (!club.equals( "interlachenspa" ) && foretees_mode != 0) {  // skip if Interlachen Spa or if no foretees golf

      try {
         //
         //  Get the walk/cart options available for this club
         //
         //getParms.getCourse(con, parmc, course);    // course not used yet
         getParms.getCourseTrans(con, parmc);    // get all tmodes if multiple courses

      }
      catch (Exception e1) {

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 1</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Error: " + e1.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
   
   /*
   if (club.equals("tontoverde")) {
       allowMobile = false;
   }
    */

   //
   //  See if we are in timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
    
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   int activity_id = (Integer)session.getAttribute("activity_id");

   //
   //  Determine if activities are defined in system
   //
   if (getActivity.isConfigured(con) && (!getActivity.isStagingMode(con) || sess_activity_id != 0)) {
      
      activity_system = true;              // if yes
   }

   
   // 
   //  Set the appropriate language for the times to search for (i.e. Tee Times, Times, etc)
   //
   String timesText = "";
   
   if (activity_system == false) {        // if Golf only
      
      timesText = sysLingo.TEXT_tee_times;   // get text based on Tee Times or Notification System
      
   } else {
      
      timesText = "Reservation";           // generic term for members that use both or non-golf
   }
   
   if (timesText.equals("tee times")) timesText = "Reservation";     // change for new email option text below
   
   
   //
   //  Get the current info for this user
   //
   try {

      pstmt = con.prepareStatement (
             "SELECT " +
                 "m.email, m.c_hancap, m.g_hancap, m.wc, m.emailOpt, m.emailOpt2, m.clubEmailOpt1, m.clubEmailOpt2, m.memEmailOpt1, m.memEmailOpt2, m.ghin, m.email2, " +
                 "m.phone1, m.phone2, m.email_bounced, m.email2_bounced, iCal1, iCal2, default_activity_id, " +
                 "cn.club_num, ca.assoc_num, ntrp_rating, usta_num, mobile_user, mobile_pass, name_last, tee_sheet_jump, " +
                 "count " +
             "FROM member2b m " +
             "LEFT OUTER JOIN hdcp_club_num cn ON cn.hdcp_club_num_id = m.hdcp_club_num_id " +
             "LEFT OUTER JOIN hdcp_assoc_num ca ON ca.hdcp_assoc_num_id = m.hdcp_assoc_num_id " +
             "WHERE m.username = ?");
         
      pstmt.clearParameters();
      pstmt.setString(1, user);
      rs = pstmt.executeQuery();

      if ( rs.next() ) {

         email = rs.getString("email");
         c_hancap = rs.getFloat("c_hancap");
         g_hancap = rs.getFloat("g_hancap");
         wca = rs.getString("wc");
         emailOpt = rs.getInt("emailOpt");               // email notification options
         emailOpt2 = rs.getInt("emailOpt2");
         clubEmailOpt1 = rs.getInt("clubEmailOpt1");     // club emails
         clubEmailOpt2 = rs.getInt("clubEmailOpt2");
         memEmailOpt1 = rs.getInt("memEmailOpt1");       // member to member emails
         memEmailOpt2 = rs.getInt("memEmailOpt2");
         iCal1 = rs.getInt("iCal1");
         iCal2 = rs.getInt("iCal2");
         ghin = rs.getString("ghin");
         email2 = rs.getString("email2");
         phone1 = rs.getString("phone1");
         phone2 = rs.getString("phone2");
         email_bounced = rs.getInt("email_bounced");
         email2_bounced = rs.getInt("email2_bounced");
         club_num = rs.getString("club_num");
         club_assoc = rs.getString("assoc_num");
         default_activity_id = rs.getInt("default_activity_id");
         ntrp = rs.getFloat("ntrp_rating");     // get tennis data
         usta = rs.getString("usta_num");
         mobile_user = rs.getString("mobile_user");
         mobile_pass = rs.getString("mobile_pass");
         last_name = rs.getString("name_last");
         tee_sheet_jump = rs.getInt("tee_sheet_jump");
         login_count = rs.getInt("count");

      }

   } catch (Exception exc) {             // SQL Error

      out.println(SystemUtils.HeadTitle("DB Access Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H2>Database Access Error 2</H2>");
      out.println("<BR><BR>Unable to process database change at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }
   
   
   boolean allowOldMobile = false;

   if (club.equals("hillwoodcc") || club.equals("pwgolf") || club.equals("sciotocc") || club.equals("tontoverde") || club.equals("roccdallas")) {      // if club wants to keep using the old mobile

      allowOldMobile = true;
   }

         
   if (mobile_user == null) mobile_user = "";         // change to empty string if not provided
   
   if (mobile_pass == null) mobile_pass = "";         
   
   if ((mobile_user.equals("") || mobile_pass.equals("")) && !allowOldMobile) allowMobile = false;
   
   if (email.equals("")) {     // if email address not provided yet
      
      //emailOpt = 0;            // init the options
      //clubEmailOpt1 = 0;
      //memEmailOpt1 = 0;
      //iCal1 = 0;
   }
   
   if (email2.equals("")) {     // if email address not provided yet
      
      //emailOpt2 = 0;            // init the options
      //clubEmailOpt2 = 0;
      //memEmailOpt2 = 0;
      //iCal2 = 0;
   }
   

   int i = 0;
     
   if (!club.equals( "interlachenspa" ) && foretees_mode != 0) {  // skip if Interlachen Spa

      //
      //  if Piedmont Driving Club, remove 2 trans modes that are for events only
      //
      if (club.equals( "piedmont" )) {

         for (i = 0; i < parmc.tmode_limit; i++) {

            if (parmc.tmodea[i].equalsIgnoreCase( "cfc" ) || parmc.tmodea[i].equalsIgnoreCase( "wwc" )) {

               parmc.tmodea[i] = "";      // remove it
            }
         }
      }

      //
      //  Make sure the user's c/w option is still supported (pro may have changed config)
      //
      i = 0;
      loopi1:
      while (i < parmc.tmode_limit) {

         if (parmc.tmodea[i].equals( wca )) {

            wc = parmc.tmode[i];
            break loopi1;
         }
         i++;
      }
      if (i > parmc.tmode_limit-1) {       // if we went all the way without a match

         wca = parmc.tmodea[0];    // default to first option
         wc = parmc.tmode[0];
      }

      owc = wc;      // save the wc option
      
      
      //
      //  if Sonnenalp, remove 2 trans modes that are for events only
      //
      if (club.equals( "sonnenalp" )) {

         for (i = 0; i < parmc.tmode_limit; i++) {

            if (parmc.tmodea[i].equals( "ICP" ) && !wca.equals( "ICP" )) {

               parmc.tmodea[i] = "";      // remove it
            }

            if (parmc.tmodea[i].equals( "FCP" ) && !wca.equals( "FCP" )) {

               parmc.tmodea[i] = "";      // remove it
            }
         }
      }
      
      //
      //  Filter out Pro-Only MOTs
      //
      try {
          for (i = 0; i < parmc.tmode_limit; i++) {

              // If tmode is Pro-Only (tOpt# == 1) and not the member's currently set wc option, remove it
              if (parmc.tOpt[i] == 1 && !wca.equals(parmc.tmodea[i])) {
                  parmc.tmodea[i] = "";     // remove it
              }
          }
      } catch (Exception exc) {
          
          out.println(SystemUtils.HeadTitle("DB Access Error"));
          out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
          out.println("<BR><BR><H2>Database Access Error 3</H2>");
          out.println("<BR><BR>Unable to access database at this time.");
          out.println("<BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact your golf shop staff.");
          out.println("<BR><BR>");
          out.println("<a href=\"Member_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;
      }

      i = 0;
   }

    //
    //  output a html page to prompt user for changes - wait for doPost entry
    //
    //if (!dining) {      // these components are built in Dining_home for the dining system

      //
      //  Build the top of the page
      //
      Common_skin.outputHeader(club, activity_id, "Member Settings", false, out, req);
      // if user is allow to change password (not MemFirst or other web site) then include the required js
      if (caller.equals( "none" ) || caller.startsWith("foretees")) {

            // include the pass strength files
            out.println("<script type=\"text/javascript\" src=\"/" + rev + "/assets/passwordStrengthMeter/passwordStrengthMeter.js\"></script>");
            if(!rwd){
                out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"/" + rev + "/assets/passwordStrengthMeter/passwordStrengthMeter.css\">");
            }
            //if(!rwd){
                // include the pass strength local js
                out.println("<script type=\"text/javascript\">");
                out.println("jQuery(document).ready(function() {");
                out.println("	var bpos = '';");
                out.println("	var perc = 0;");
                out.println("	var minperc = 0;");
                if(!rwd){
                    out.println("	$('#password').css( {backgroundPosition: '0 0'} );");
                } else {
                    out.println("$('#oldpw').focus(function(){$(this).attr('type','password')});");
                    //out.println("$('label.oldPwrd').append('<input type=\"password\" name=\"oldp\" id=\"oldp\" maxlength=\"15\" autocomplete=\"off\">')");
                }
                out.println("	$('#newpw').keyup(function(){");
                out.println("		$('#result').html(passwordStrength($('#newpw').val(),$('#username').val(),$('#lastname').val())) ;");
                out.println("		perc = passwordStrengthPercent($('#newpw').val(),$('#username').val(),$('#lastname').val());");
                out.println("		bpos = \" $('#colorbar').css( {backgroundPosition: '0px -\" + perc + \"px' } );\"");
                out.println("       bpos = bpos + \" $('#colorbar').css( {width: '\" + (perc * 2) + \"px' } );\"");
                out.println("		eval(bpos);");
                out.println("	    $('#percent').html(\" \" + perc  + \"% \");");
                out.println("	})");
                out.println("})");
       //      out.println("	function showMore()");
       //      out.println("	{");
       //      out.println("		$('#more').slideDown()");
       //      out.println("	}");
                out.println("</script>");
            //}
      }
      out.println("</head>");
      Common_skin.outputBody(club, activity_id, out, req);
      Common_skin.outputTopNav(req, club, activity_id, out, con);
      Common_skin.outputBanner(club, activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);  
      Common_skin.outputSubNav(club, activity_id, out, con, req);
      Common_skin.outputPageStart(club, activity_id, out, req);
      Common_skin.outputBreadCrumb(club, activity_id, out, "Settings", req);
      Common_skin.outputLogo(club, activity_id, out, req);

      // out.println("<div id=\"tt1_left\">");
      // out.println("<p><strong>Manage Settings</strong></p></div>");       // Page Heading (removed - not on any other pages)

      if(!rwd){
        out.println("<div class=\"preContentFix\"></div>"); // clear the float
      }
      // Instructions for this page
      out.println("<div class=\"main_instructions pageHelp\" data-fthelptitle=\"Instructions\"><strong class=\"altTitle\">Instructions:</strong> ");          

     if (!clubCentralOnly) {
         
         out.println("Use this page to change your personal settings. Only change the fields you want to update. Click the 'Update' button to save the changes.</div>");          
         
     } else {
         
         out.println("Use this page to generate a username and password for the ClubCentral app.</div>");          
     } 
      
      
      
   //}
       

    out.println("<script type=\"text/javascript\">");
    out.println("<!-- ");
    if (caller.equals( "none" ) || caller.startsWith("foretees")) out.println("function cursor() { document.forms['setting_form'].oldpw.focus(); }");
    out.println("function resetBounceFlags() {" +
                  " var frm = document.forms['setting_form'];" +
                  " frm.resetBounceFlags.value = '1';" +
                  " frm.submit();" +
                  "}");
    out.println("// -->");
    out.println("</script>");
    
    if(!rwd){
        out.println("<br>");
    }
    // start the settings form
    out.println("<form action=\"Member_services\" method=\"post\" name=\"setting_form\" id=\"setting_form\" autocomplete=\"off\">");
    out.println("<input type=\"hidden\" name=\"resetBounceFlags\" value=\"0\">");
    out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + sess_activity_id + "\">");

    // main table that breaks the settings in to sections
    out.println("<div class=\"sub_main_tan ftMemberSetting\">");
    
    
    if (!clubCentralOnly) {
    
        if(rwd){
            out.print("<fieldset class=\"ftLoginCount standard_fieldset\"><span>Login Count:</span> <span>" +login_count+ "</span></fieldset>");
        } else {
            out.println("<table id=\"mem_settings\">");


            //
            //  Display login Count
            //
            out.println("<tr><td>"); 

                out.println("<div id=res_qs>");

                out.println("<strong>Login Count</strong> (number of times that you have logged into ForeTees): &nbsp;<strong>" +login_count+ "</strong><br><br>");

            out.println("</td></tr>"); // end this section of the main table
        }

        //
        //  Password
        //
        if (caller.equals( "none" ) || caller.startsWith("foretees")) {   // if user did not come from MemFirst or other web site

            out.println("<input name=\"username\" id=\"username\" type=\"hidden\" value=\"" + user + "\">");
            out.println("<input name=\"lastname\" id=\"lastname\" type=\"hidden\" value=\"" + last_name + "\">");

            if(rwd){
                out.println("<a name=\"password\"></a><fieldset class=\"standard_fieldset \"><legend>Change Password:</legend>");
                out.println("<div class=\"sub_instructions\">Only enter password information if you wish to change it.</div>");
                out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>Current Password:</span><input type=\"text\" name=\"oldpw\" id=\"oldpw\" maxlength=\"15\" autocomplete=\"off\"></label>");
                out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>New Password:</span><input type=\"password\" name=\"newpw\" id=\"newpw\" maxlength=\"15\" autocomplete=\"off\"></label>");
                out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>Confirm New Password:</span><input type=\"password\" name=\"newpwc\" id=\"newpwc\" maxlength=\"15\" autocomplete=\"off\"></label>");
                out.println("<label onclick=\"\" class=\"ftInputLabel\">");
                out.println("<span><span class=\"percent\" id=\"percent\">0%</span>");
                out.println(" &nbsp;&nbsp;");
                out.println(" <span class=\"result\" id=\"result\">Enter your password</span></span>");
                out.println(" <div class=\"graybar\" id=\"graybar\" style=\"height:3px\"></div>");
                out.println(" <div class=\"colorbar\" id=\"colorbar\"></div>");

                out.println("</label>");
                out.println("</fieldset>");
            }else{
                out.println("<tr><td>");
                out.println("<div id=res_qa>");
                out.println("<p><strong>Password:</strong> (Only enter password information if you wish to change it.)<p>");
                out.println("<table id=\"mem_sub_settings\">");
                out.println("<tr>");
                out.println("<td>Current Password:</td>");
                out.println("<td><input type=\"password\" name=\"oldpw\" id=\"oldpw\" size=\"20\" maxlength=\"15\" autocomplete=\"off\"></td>");
                out.println("<td></td>");
                out.println("</tr><tr>");
                out.println("<td>New Password:</td>");
                out.println("<td nowrap><input type=\"password\" name=\"newpw\" id=\"newpw\" size=\"20\" maxlength=\"15\" autocomplete=\"off\"></td>");
                out.println("<td>");

                out.println(" <div class=\"graybar\" id=\"graybar\" style=\"height:3px\"></div>");
                out.println(" <div class=\"colorbar\" id=\"colorbar\"></div>");
                out.println(" <span class=\"percent\" id=\"percent\">0%</span>");

                out.println(" &nbsp;&nbsp;");
                out.println(" <span class=\"result\" id=\"result\">Enter your password</span>");
                out.println("</td>");

                out.println("</tr><tr>");
                out.println("<td>Confirm New Password:&nbsp;</td>");
                out.println("<td><input type=\"password\" name=\"newpwc\" id=\"newpwc\" size=\"20\" maxlength=\"15\" autocomplete=\"off\"></td>");
                out.println("<td></td>");
                out.println("</tr>");
                out.println("</table>");

                out.println("</div>");

                out.println("</td></tr>"); // end this section of the main table

                out.println("<tr><td><hr width=\"75%\" align=\"center\"><BR></td></tr>"); 
            }

        }


        //
        //  Email addresses
        //

        // if Roster Sync - no email address changes allowed (except for clubs explicity listed here)
        boolean static_emails = (rsync == 1 &&
            !caller.equals("FLEXSCAPE4865") && !club.equals("mesaverdecc") &&
            !club.equals("charlottecc") && !club.equals("dorsetfc") &&
            !club.equals("bellevuecc") && !club.equals("greenacrescountryclub") && 
            !club.equals("hillwoodcc"));
        if (caller.equals("FLEXWEBFT")) static_emails = true;

        if(rwd){
            out.println("<a name=\"email\"></a><fieldset class=\"standard_fieldset ftEmailNotifications\"><legend>Email Notifications:</legend>");
            out.print("<div class=\"sub_instructions\">Indicate which communications you would like to receive under each email address.</div>");        
            if (email_bounced != 0 && !email.equals("")) {
                out.println("<div class=\"sub_instructions\"><b>Note:</b> Mail to your primary address has recently failed " +
                    "and needs to be corrected before we attempt to send you further emails.  If the email address " +
                    "is correct, then <a href=\"javascript:resetBounceFlags()\">click here</a> to reset your " +
                    "email bounce indicator.</div>");
            }
            out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>Primary Email:</span><input type=\"text\" name=\"em1\" maxlength=\"50\" value=\"" + email +"\"" + ((email_bounced!=0) ? " style=\"background-color: red\"" : "") + "" + (static_emails ? " readonly" : "") + " autocomplete=\"off\"></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((iCal1 == 1) ? "checked " : "") + "name=\"iCal1\" value=\"1\"><span>Include iCal Attachments</span></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((emailOpt == 1) ? "checked " : "") + "name=\"emOpt1\" value=\"1\"><span>"+(club.equals("aronimink")?"Event Registration":timesText)+" Notifications</span></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((clubEmailOpt1 == 1) ? "checked " : "") + "name=\"clubEmOpt1\" value=\"1\"><span>Club Communications</span></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((memEmailOpt1 == 1) ? "checked " : "") + "name=\"memEmOpt1\" value=\"1\"><span>Emails From Other Members</span></label>");
            out.print("<hr>");
            if (email2_bounced != 0 && !email2.equals("")) {
                out.println("<div class=\"sub_instructions\"><b>Note:</b> Mail to your secondary addess has recently failed " +
                    "and needs to be corrected before we attempt to send you further emails.  If the email address " +
                    "is correct, then <a href=\"javascript:resetBounceFlags()\">click here</a> to reset your " +
                    "email bounce indicator.</div>");
            }
            out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>Secondary Email:</span><input type=\"text\" name=\"em2\" maxlength=\"50\" value=\"" + email2 +"\"" + ((email_bounced!=0) ? " style=\"background-color: red\"" : "") + "" + (static_emails ? " readonly" : "") + " autocomplete=\"off\"></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((iCal2 == 1) ? "checked " : "") + "name=\"iCal2\" value=\"1\"><span>Include iCal Attachments</span></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((emailOpt2 == 1) ? "checked " : "") + "name=\"emOpt2\" value=\"1\"><span>"+(club.equals("aronimink")?"Event Registration":timesText)+" Notifications</span></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((clubEmailOpt2 == 1) ? "checked " : "") + "name=\"clubEmOpt2\" value=\"1\"><span>Club Communications</span></label>");
            out.println("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" " + ((memEmailOpt2 == 1) ? "checked " : "") + "name=\"memEmOpt2\" value=\"1\"><span>Emails From Other Members</span></label>");

            if (club.equals("hawkslandinggolfclub")) {
                out.println("<div class=\"sub_instructions\">These email addresses are used only by ForeTees to confirm tee times or for communications from other members. " +
                            "To update your email address with the club, please contact the golf shop staff.</div>");
            }

            out.print("</fieldset>");

        }else{

            out.println("<tr><td>"); // <font size=\"2\">

            out.println("<div id=res_qs>");

            out.println("<strong>Email Addresses:</strong>");

            out.println("<input type=\"hidden\" name=\"old_email\" value=\"" + email + "\" autocomplete=\"off\">");
            out.println("<input type=\"hidden\" name=\"old_email2\" value=\"" + email2 + "\" autocomplete=\"off\">");


            out.println("<p><br>&nbsp;&nbsp;&nbsp;&nbsp;Indicate which communications you would like to receive under each email address.</p>");

            // start the email table
            out.println("<table id=\"mem_sub_settings\">");

            // 
            // Allow user to update email addresses and options
            //
            out.println("<tr>");
            out.println("<td>&nbsp;</td>");
            out.println("<td><strong>Primary Email</strong></td>");
            out.println("<td><strong>Secondary Email</strong></td>");

            out.println("</tr><tr>");      
            out.println("<td>Email:</td>");
            if (static_emails) {
                out.println("<td><strong>" + email +"<strong><input type=\"hidden\" name=\"em1\" value=\"" + email + "\" autocomplete=\"off\">&nbsp;&nbsp;</td>");
            } else {
                out.println("<td><input type=\"text\" name=\"em1\" size=\"28\" maxlength=\"50\" value=\"" + email +"\"" + ((email_bounced!=0) ? " style=\"background-color: red\"" : "") + " autocomplete=\"off\">&nbsp;&nbsp;</td>");
            }
            if (static_emails) {
                out.println("<td><strong>" + email2 +"<strong><input type=\"hidden\" name=\"em2\" value=\"" + email2 + "\" autocomplete=\"off\">&nbsp;&nbsp;</td>");
            } else {
                out.println("<td><input type=\"text\" name=\"em2\" size=\"28\" maxlength=\"50\" value=\"" + email2 +"\"" + ((email2_bounced!=0) ? " style=\"background-color: red\"" : "") + " autocomplete=\"off\">&nbsp;&nbsp;</td>");
            }

            out.println("</tr><tr>");      
            out.println("<td>Include iCal Attachments:&nbsp;&nbsp;</td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((iCal1 == 1) ? "checked " : "") + "name=\"iCal1\" value=\"1\"></td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((iCal2 == 1) ? "checked " : "") + "name=\"iCal2\" value=\"1\"></td>");

            out.println("</tr><tr>");      
            if (club.equals("aronimink")) {
                out.println("<td>Receive Event Registration Notifications:&nbsp;&nbsp;</td>");  
            } else {
                out.println("<td>Receive " + timesText + " Notifications:&nbsp;&nbsp;</td>");
            }
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((emailOpt == 1) ? "checked " : "") + "name=\"emOpt1\" value=\"1\"></td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((emailOpt2 == 1) ? "checked " : "") + "name=\"emOpt2\" value=\"1\"></td>");

            out.println("</tr><tr>");      
            out.println("<td>Receive Club Communications:&nbsp;&nbsp;</td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((clubEmailOpt1 == 1) ? "checked " : "") + "name=\"clubEmOpt1\" value=\"1\"></td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((clubEmailOpt2 == 1) ? "checked " : "") + "name=\"clubEmOpt2\" value=\"1\"></td>");

            out.println("</tr><tr>");      
            out.println("<td>Receive Emails From Other Members:&nbsp;&nbsp;</td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((memEmailOpt1 == 1) ? "checked " : "") + "name=\"memEmOpt1\" value=\"1\"></td>");
            out.println("<td style=\"text-align:center;\"><input type=\"checkbox\" " + ((memEmailOpt2 == 1) ? "checked " : "") + "name=\"memEmOpt2\" value=\"1\"></td>");
            out.println("</tr>");

            if (email_bounced != 0 || email2_bounced != 0) {

                out.println("<tr><td colspan=3><br><b>Note:</b> The address above in red with an * after it has recently failed " +
                            "and needs to be corrected before we attempt to send you further emails.  If the email address " +
                            "is correct, then <a href=\"javascript:resetBounceFlags()\">click here</a> to reset your " +
                            "email bounce indicator.</td></tr>");
            }

            if (club.equals("hawkslandinggolfclub")) {

                out.println("<tr><td colspan=3><br>These email addresses are used only by ForeTees to confirm tee times or for communications from other members. " +
                            "To update your email address with the club, please contact the golf shop staff.</td></tr>");
            }

            out.println("</table>"); // end email table

            out.println("</div>");

            out.println("</td></tr>");    // end email section

            out.println("<tr><td><hr width=\"75%\" align=\"center\"><BR></td></tr>"); 
        }

        // 
        //   Phone Numbers
        //

        if (club.equals("hawkslandinggolfclub") || caller.equals("FLEXWEBFT")) {
            //  If Hawks Landing GC, do not display phone number fields.
                out.println("<input type=\"hidden\" name=\"phone1\" value=\"" +phone1+"\">");
                out.println("<input type=\"hidden\" name=\"phone2\" value=\"" +phone2+"\">");
                out.println("<input type=\"hidden\" name=\"old_phone1\" value=\"" + phone1 + "\">");
                out.println("<input type=\"hidden\" name=\"old_phone2\" value=\"" + phone2 + "\">");
        } else if(rwd){
            // Responsive
            out.println("<input type=\"hidden\" name=\"old_phone1\" value=\"" + phone1 + "\">");
            out.println("<input type=\"hidden\" name=\"old_phone2\" value=\"" + phone2 + "\">");
            out.println("<a name=\"phone\"></a><fieldset class=\"standard_fieldset ftPhoneNumbers\"><legend>Phone Numbers:</legend>");
            out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>Primary Number:</span><input type=\"text\" name=\"phone1\" maxlength=\"24\" value=\"" + phone1 +"\" autocomplete=\"off\"></label>");
                out.println("<label onclick=\"\" class=\"ftInputLabel\"><span>Alternate Number:</span><input type=\"text\" name=\"phone2\" maxlength=\"24\" value=\"" + phone2 +"\" autocomplete=\"off\"></label>");
                out.println("</fieldset>");
        } else {
            // Desktop
            out.println("<tr><td>");

        out.println("<input type=\"hidden\" name=\"old_phone1\" value=\"" + phone1 + "\" autocomplete=\"off\">");
        out.println("<input type=\"hidden\" name=\"old_phone2\" value=\"" + phone2 + "\" autocomplete=\"off\">");


                out.println("<tr><td>");
                out.println("<strong>Phone Numbers:</strong>");

            // start the email table
            out.println("<table id=\"mem_sub_settings\">");
            out.println("<tr>");
            out.println("<td>Primary Number:</td>");
            out.println("<td><input type=\"text\" name=\"phone1\" size=\"14\" maxlength=\"24\" value=\"" + phone1 +"\" autocomplete=\"off\"></td>");
            out.println("</tr><tr>");
            out.println("<td>Alternate Number:&nbsp;</td>");
            out.println("<td><input type=\"text\" name=\"phone2\" size=\"14\" maxlength=\"24\" value=\"" + phone2 +"\" autocomplete=\"off\"></td>");
            out.println("</tr></table>");

            out.println("</td></tr>");

            out.println("<tr><td><hr width=\"75%\" align=\"center\"><BR></td></tr>"); 
        }

            //
            //   Default Activity
            //
            if ( activity_system == true && !caller.equals("FLEXWEBFT")) {

                out.println("<tr><td>");
                out.println("<p>&nbsp;&nbsp;&nbsp;<strong>Default Activity:</strong>&nbsp;&nbsp;");
                out.println("<select size=\"1\" name=\"default_activity_id\" autocomplete=\"off\">");

                try {

                    // if they have foretees then give a link in to the golf system
                    if ( foretees_mode != 0 ) {

                        Common_Config.buildOption(0, "ForeTees Golf", default_activity_id, out);

                    }

                    stmt = con.createStatement();

                    // build a link to any activities they have access to
                    rs = stmt.executeQuery("SELECT * FROM activities " +
                                        "WHERE parent_id = 0 " +
                                        "ORDER BY activity_name");

                    while ( rs.next() ) {

                        Common_Config.buildOption(rs.getInt("activity_id"), rs.getString("activity_name"), default_activity_id, out);

                    }

                    stmt.close();

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { stmt.close(); }
                    catch (Exception ignore) {}

                }

                out.println("</select></p>");
                out.println("</td></tr>");
            }

            if (foretees_mode != 0 && !dining) {            // if foretees golf in system and NOT dining

                boolean showDefaultTrans = (foretees_mode != 0 && !club.equals( "blackhawk" ) && !club.equals( "marbellacc" ) && !club.equals( "interlachenspa" ) 
                        && !club.equals( "valleyclub" ) && !club.equals("philcricket") && !club.equals("mosscreek") && !club.equals("pinery"));
                boolean showSheetJump = (IS_TLT == false && sess_activity_id == 0);
                boolean showHdcp = (!club.equals( "oldoaks" ) && !club.equals( "interlachenspa" ) && foretees_mode != 0);

                boolean hasGolfOptions = (showDefaultTrans || showSheetJump);

                if(rwd && hasGolfOptions){
                    out.println("<a name=\"golf\"></a><fieldset class=\"standard_fieldset ftGolfSettings\"><legend>Golf Settings:</legend>"); 
                } else if ( activity_system == true ) {  // AND Activities - add row for group title
                // out.println("<tr><td><br><strong>Golf Options:</strong></td></tr>");  // moved below
                }


                //
                //   Mode of Trans
                //
                if (showDefaultTrans) {  // skip if Blackhawk CC or Marbella

                    if(rwd){
                        out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>Preferred Mode of Transportation:</span><select name=\"walk_cart\">");
                    } else{
                        out.println("<tr><td><strong>Golf Options:</strong></td></tr>");
                        out.println("<tr><td><br>");
                        out.println("<p>&nbsp;&nbsp;&nbsp;Default Mode of Transportation Preference:&nbsp;&nbsp;&nbsp;");
                        out.println("<select size=\"1\" name=\"walk_cart\" autocomplete=\"off\">");
                    }
                    for (i=0; i<parmc.tmode_limit; i++) {        // get all c/w options

                        if (!parmc.tmodea[i].equals( "" )) {
                            if (wca.equals( parmc.tmodea[i] )) {
                                out.print("<option selected value=" + wca + ">" + wc + " ("+wca+")</option>");
                            } else {
                                out.print("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmode[i]+ " ("+parmc.tmodea[i]+")</option>");
                            }
                        }
                    }
                    if(rwd){
                        out.println("</select></label>");
                    } else {
                        out.println("</select></p>");
                        out.println("</td></tr>");
                    }
                } else {
                    out.println("<input type=\"hidden\" name=\"walk_cart\" value=\"" + wca + "\">");
                }

                if (showSheetJump) {      // DO NOT do this for Notification systems
                        //
                        //   Tee Sheet Jump option (check box in Member_select)
                        //
                    if(rwd){
                        out.print("<label onclick=\"\" class=\"standard_button\"><input type=\"checkbox\" name=\"tee_sheet_jump\" value=\"1\"" + ((tee_sheet_jump == 1) ? " checked" : "") + "><span>Hide calendar on tee sheet</span>  <a class=\"helpButton\" href=\"#\" data-fthelp=\"hide_calendar\" title=\"How is this used?\"><span>How is this used?</span></a></label>");
                    } else {
                        out.println("<tr><td>&nbsp;</td></tr>");
                        out.println("<tr><td>&nbsp;&nbsp;&nbsp;Always position the tee sheet to show more tee times<br> &nbsp;&nbsp;&nbsp;(check box on the Make, Change or View Tee Times page)? &nbsp;");
                        out.println("<select size=\"1\" name=\"tee_sheet_jump\" autocomplete=\"off\">");
                        out.println("<option value=\"1\"" + ((tee_sheet_jump == 1) ? " selected" : "") + ">Yes</option>");
                        out.println("<option value=\"0\"" + ((tee_sheet_jump != 1) ? " selected" : "") + ">No</option>");
                        out.println("</select>");
                        out.println("</td>");
                        out.println("</tr>");
                    }
                    out.println("<input type=\"hidden\" name=\"old_tee_sheet_jump\" value=\"" + tee_sheet_jump + "\">");
                }

                if(rwd && hasGolfOptions){
                    out.print("</fieldset>");
                }

                //
                //  GHIN info
                //
                if (showHdcp) {  // skip if Old Oaks or Spa

                    boolean tmp_allow = true;
                    String tmp_hdcp = "";

                    if (hdcpSystem.equalsIgnoreCase("ghin")) tmp_allow = false;

                    if(rwd){
                        out.println("<a name=\"hdcp\"></a><fieldset class=\"standard_fieldset ftHandicapSettings\"><legend>Handicaps:</legend>"); 
                    } else {
                        out.println("<tr><td><br><hr width=\"75%\" align=\"center\"><BR></td></tr>"); 

                        out.println("<tr><td>");
                        out.println("<p><strong>Handicaps:</strong>");
                    }

                    if (!hdcpSystem.equalsIgnoreCase( "ghin" ) || Common_Server.SERVER_ID == 4) {

                        if(!rwd){
                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Course&nbsp;&nbsp;");
                        }
                        if (c_hancap > 0) {
                            tmp_hdcp = "+" + c_hancap;
                        } else {
                            if (c_hancap <= 0) {
                                c_hancap = 0 - c_hancap;                       // convert to non-negative
                            }
                            tmp_hdcp = "" + c_hancap;
                        }

                        if(rwd){
                            out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>Course:</span><input type=\"text\" name=\"c_hancap\" maxlength=\"6\" value=\"" + tmp_hdcp +"\""+(hdcpSystem.equalsIgnoreCase( "Other" )?"":" disabled=\"disabled\"")+" autocomplete=\"off\"></label>");
                        } else {
                            if (hdcpSystem.equalsIgnoreCase( "Other" )) {
                                out.println("<input type=\"text\" name=\"c_hancap\" value=\"" + tmp_hdcp + "\" size=\"6\" maxlength=\"6\" autocomplete=\"off\">");
                            } else {
                                out.println("<b>" + tmp_hdcp + "</b>");
                            }
                        }
                    }

                    if(!rwd){
                        out.println(" &nbsp;&nbsp;&nbsp;&nbsp;USGA&nbsp;&nbsp;");
                    }
                    tmp_hdcp = "";
                    if (g_hancap > 0) {
                        tmp_hdcp = "+" + g_hancap;
                    } else {
                        if (g_hancap <= 0) {
                            g_hancap = 0 - g_hancap;                       // convert to non-negative
                        }
                        tmp_hdcp = "" + g_hancap;
                    }
                    if(rwd){
                        out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>USGA:</span><input type=\"text\" name=\"g_hancap\" maxlength=\"6\" value=\"" + tmp_hdcp +"\""+(hdcpSystem.equalsIgnoreCase( "Other" )?"":" disabled=\"disabled\"")+"></label>");
                    } else {
                        if (hdcpSystem.equalsIgnoreCase( "Other" )) {
                            out.println("<input type=\"text\" name=\"g_hancap\" value=\"" + tmp_hdcp + "\" size=\"6\" maxlength=\"6\" autocomplete=\"off\">");
                        } else {
                            out.println("<b>" + tmp_hdcp + "</b>");
                        }
                    }

                    if (hdcpSystem.equalsIgnoreCase( "ghin" ) || Common_Server.SERVER_ID == 4) {

                        if(rwd){
                            out.print(" <a class=\"standard_button\" href=\"Member_handicaps?todo=view\">View course handicap</a>");
                        } else {
                            out.println("&nbsp; &nbsp; &nbsp; &nbsp;<a href=\"Member_handicaps?todo=view\">Click here to see your course handicap.</a><br>");
                        }
                    }

                    if (!ghin.equals( "" )  || Common_Server.SERVER_ID == 4) {            // if ghin # provided, display it (do not allow them to change it)

                        if(rwd){
                            out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>Your Handicap #:</span><input type=\"text\" name=\"hdcp_num\" maxlength=\"24\" value=\"" + ghin +"\" disabled=\"disabled\"></label>");
                            out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>Assigned Club Number:</span><input type=\"text\" name=\"hdcp_num\" maxlength=\"24\" value=\"" + club_num +"\" disabled=\"disabled\"></label>");
                            out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>Club Association Number:</span><input type=\"text\" name=\"hdcp_num\" maxlength=\"24\" value=\"" + club_assoc +"\" disabled=\"disabled\"></label>");
                        } else {
                            out.println("<br>&nbsp;&nbsp;&nbsp;Your Handicap # is: <b>" +ghin+ "</b>");
                            out.println("<br><br>&nbsp;&nbsp;&nbsp;Assigned Club Number: <b>" + club_num + "</b>");
                            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Assigned Club Association Number: <b>" + club_assoc + "</b>");
                        }

                    }

                    if(rwd){
                        out.print("</fieldset>"); 
                    } else {
                        out.println("</p>");
                        out.println("</td></tr>");
                    }

                }    // end of IF Old Oaks or Spa


            }       // end of IF golf  (moved - was after mobile settings below



        //  if (IS_TLT == false && allowMobile == true) {      // DO NOT do this for Notification systems
            if ((!rwd || allowOldMobile) && IS_TLT == false && allowMobile == true && (sess_activity_id == 0 || club.equals("philcricket"))) {      // TEMP - remove later (use above if)

                //
                //   Mobile settings
                //
                out.println("<tr><td><br><hr width=\"75%\" align=\"center\"><BR></td></tr>"); 

                out.println("<tr><td><br><strong>Mobile Credentials (For use when logging in from a Mobile Device):</strong></td></tr>");

                out.println("<tr><td>");

                    out.println("<div class=\"sub_instructions\">");
                    out.println("<table id=\"mem_sub_settings\">");
                    out.println("<tr><td>");
                    out.println("&nbsp;&nbsp;&nbsp;Mobile Username:&nbsp;&nbsp;");
                    out.println("</td><td>");
                    out.println("<input type=\"text\" name=\"mobile_user\" size=\"15\" maxlength=\"15\" value=\"" + mobile_user +"\">");
                    out.println("&nbsp;&nbsp;&nbsp;(It is ok to use the same credentials that you use to login from your PC.)");
                    out.println("</td></tr>");

                    if (!mobile_pass.equals( "" )) {        // if mobile password already exists - they must enter it in order to change it
                        out.println("<tr><td>");
                        out.println("&nbsp;&nbsp;&nbsp;Current Mobile Password:&nbsp;&nbsp;");
                        out.println("</td><td>");
                        out.println("<input type=\"password\" name=\"oldmobilepw\" id=\"oldmobilepw\" size=\"15\" maxlength=\"15\">");
                        out.println("&nbsp;&nbsp;&nbsp;Only enter password information if you wish to change it.");
                        out.println("</td></tr>");
                    }
                    out.println("<tr><td>");
                    out.println("&nbsp;&nbsp;&nbsp;New Mobile Password:&nbsp;&nbsp;");
                    out.println("</td><td>");
                    out.println("<input type=\"password\" name=\"newmobilepw\" size=\"15\" maxlength=\"15\">");
                    out.println("&nbsp;&nbsp;&nbsp;(4 - 15 characters)");
                    out.println("</td></tr>");

                    out.println("<tr><td>");
                    out.println("&nbsp;&nbsp;&nbsp;Confirm New Mobile Password:&nbsp;&nbsp;");            
                    out.println("</td><td>");
                    out.println("<input type=\"password\" name=\"newmobilepwc\" size=\"15\" maxlength=\"15\">");            
                    if (!mobile_pass.equals( "" )) {                                                                      // if mobile password already exists
                        if ((!email.equals("") && email_bounced == 0) || (!email2.equals("") && email2_bounced == 0)) {    //  and email address ok  
                        out.println("&nbsp;&nbsp;&nbsp;<a href=\"Login?help=yes&amp;mobile=yes&amp;user_name=" +user+ "&amp;clubname=" +club+ "\" style=\"color:blue; text-decoration:underline;\">Click Here to have your Mobile password emailed to you.</a>");
                        }
                    }
                    out.println("</td></tr>");

                    out.println("<tr><td>&nbsp;</td><td>&nbsp;</td></tr>");    // blank row for separation
                    out.println("<tr><td>");
                    out.println("&nbsp;&nbsp;&nbsp;Link to Mobile Login page:&nbsp;&nbsp;");
                    out.println("</td><td>");
                    out.println("&nbsp;&nbsp;&nbsp;<b>http://m.foretees.com/" +club+ "</b>&nbsp;&nbsp;(enter in mobile browser)");
                    out.println("</td></tr>");
                    out.println("<tr><td>");
                    out.println("&nbsp;&nbsp;&nbsp;For more information on mobile:&nbsp;&nbsp;");
                    out.println("</td><td>");
                    out.println("&nbsp;&nbsp;&nbsp;<a href=\"Common_mobile_help\" target=\"_blank\" style=\"color:blue; text-decoration:underline;\"><i>Click Here</i></a>");
                    out.println("</td></tr>");

                    out.println("</table></div>");

                out.println("</td></tr>");
            }

    }     // end of IF clubCentralOnly
    
    // 
    //*************************************************
    // Display mobile app config
    //*************************************************
    //
    if (!rwd) out.println("<tr id=\"jumpToApp\"><td><hr width=\"75%\" align=\"center\"><BR></td></tr>");   // include jump tag so page jumps here when user clicks the Generate Code button

    String authLookupCode = "", authPass = "", authUser = "";
    int club_id = Utilities.getClubId(club, con);

    if (rwd) {
        out.print("<a name=\"appconfig\"></a><fieldset class=\"standard_fieldset ftAppConfig\"><legend>Mobile Device Setup (ClubCentral App):</legend>");            
    } else {
        out.println("<tr><td><br><strong>Mobile Device Setup (ClubCentral App):</strong></td></tr>");
    }  

    boolean showActivate = false;

    if (clubCentralStaging) {    // club in Staging mode for app

        if (msubtype.equals("App Tester")) {  // only show for these members

            showActivate = true;     // ok to show activation
        }

    } else if (clubCentral) {    // if club uses app and not in staging mode

        showActivate = true;     // ok to show activation
    }

    if (showActivate || clubCentralOnly) {     // if club is using the ClubCentral app

        // new device config

        /*     // temporarily remove app login while problem in app
                
        if (!rwd) {
            out.print("<tr><td><br><p style=\"padding-left:20px;\">");
        } else {
            out.print("<span>");
        }
        
        out.print("Sorry, the ClubCentral App Login process is currently unavailable while we perform some updates.  <br><br>Please check back again soon.");
        
        if (!rwd) {
            out.print("</p></td></tr>");
        } else {
            out.print("</span><br><br>");
        }
        * 
        */
        
        // END OF TEMP MSG
        
        
        if (!rwd) {
            out.print("<tr><td><br><p style=\"padding-left:20px;\">");
        } else {
            out.print("<span>");
        }
        
        out.print("This section allows you to setup your ForeTees 'ClubCentral' App on your various mobile devices. &nbsp;<strong>Each device will require a separate "
                + "and unique password for the one time setup</strong>. &nbsp;Once you have installed the app on your mobile device, and the app prompts you to login, use "
                + "the 'Generate Password' button below to obtain your login credentials. &nbsp;Each password will be valid for 60 minutes. &nbsp;If you do not login into the app within "
                + "60 minutes, you must generate a new password.");
        
        if (!rwd) {
            out.print("</p></td></tr>");
            out.println("<tr><td><br>");
            out.println("<p style=\"padding-left:20px;\"><strong>New Device Setup:</strong></p><br>");
        } else {
            out.print("</span><br><br>");
            out.print("<span>&nbsp;&nbsp;&nbsp;<strong>New Device Setup:</strong></span><br>");
        }
        
        if (req.getParameter("generateCode") != null) {

            String[] mobileAppCredentails = mobileAPI.generateDeviceCode(user, club, con);
            authUser = mobileAppCredentails[0];
            authPass = mobileAppCredentails[1];
            Utilities.logDebug("PTS","mobileAppCredentails: created for " + user + " at " + club + " - user:" + authUser + ", pass:" + authPass);
            
        } else {

            try {

                // find an unused auth_lookup_code that's less than an hour old
                pstmt = con.prepareStatement("SELECT * FROM v5.mobile_auth WHERE member_id = ? AND club_id = ? AND active = 0 AND auth_username IS NOT NULL AND auth_password IS NOT NULL"); // AND TIMESTAMPDIFF(HOUR, date_created, NOW()) < 1;");
                pstmt.clearParameters();
                pstmt.setLong(1, member_id);
                pstmt.setInt(2, club_id);
                rs = pstmt.executeQuery();

                // if we found a record then there is an unused device authorization still available for use so display it
                if ( rs.next() ) {

                    authUser = rs.getString("auth_username");
                    authPass = rs.getString("auth_password");
                    
                }

            } catch (Exception ignore) { // any error is a fail

                out.println("<P style=\"padding-left:20px;\">ERROR: " + ignore.toString() + "</P>");

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}
            }
        }
        
        String authLookupParm = "?generateCode#jumpToApp";

        if (clubCentralOnly) authLookupParm = "?generateCode&clubCentral";
        
        if (!authPass.isEmpty()) {

            out.print("<p style=\"padding-left:40px;\">"
                    + "Mobile App Username: &nbsp;&nbsp;&nbsp;" + authUser + "<br>"
                    + "Mobile App Password: &nbsp;&nbsp;&nbsp;" + authPass.replaceAll("(.{4})(?!$)", "$1-")
                    + "</p>");
        }
        
        out.print("<p style=\"padding-left:40px;\"><input type=\"button\" value=\"Generate " + ((authPass.isEmpty()) ? "" : "New ") + "Password\" id=\"generateCode\" onclick=\"window.location.href='" +authLookupParm+ "'\"></p>");


        if ( !rwd ) {
            out.println("</td>");
            out.println("</tr>");
        }


        if (!rwd) {
            out.print("<tr><td><br><p style=\"padding-left:20px;\">When prompted by the ForeTees App, enter the login credentials displayed above. &nbsp;The letters can be entered in upper or lower case."
                    + "</p><br><p style=\"text-align:center;\"><hr width=\"35%\"></p><br>"
                    + "<p style=\"padding-left:20px;\">Your registered mobile devices will be shown below. &nbsp;<strong>If you lose or replace a mobile device</strong>, please be sure to remove it from this list by selecting the 'Deactivate' button listed next to the device below</p></td></tr>");
 
        } else {
        
            out.print("<span>When prompted by the ForeTees App, enter the login credentials displayed above. &nbsp;The letters can be entered in upper or lower case.</span>");
        }

            
        if (!clubCentralOnly) {

            // display existing device links
            if ( rwd ) {
                out.print("<span>Your registered mobile devices will be shown below. &nbsp;<strong>If you lose or replace a mobile device</strong>, please be sure to remove it from this list by selecting "
                        + "the 'Deactivate' button listed next to the device below</span><br><br>");
                out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>&nbsp;&nbsp;&nbsp;<strong>Configured Devices:</strong></span></label><br>");
            } else {
                out.println("<tr><td><br>");
                out.println("<p style=\"padding-left:20px;\"><strong>Configured Devices:</strong></p><br>");
            }

            boolean found = false;

            // handle deactivating a device
            if (req.getParameter("deactivate") != null && req.getParameter("id") != null) {

                int id = 0;
                
                try { id = Integer.parseInt( req.getParameter("id") ); } 
                catch (Exception ignore) {}
                
                if (id != 0) {
                
                    if (mobileAPI.deactivateDevice(id, member_id, club_id, con)) {
                        
                        // success
                        
                    } else {
                        
                        // failure - an actual error will generate an errorlog entry, not finding the record to delete will not.
                        
                    }
                    
                } // end id not zero
                
            } // end device deactivation

            
            //List<Map<String, String>> deviceList = new ArrayList<Map<String, String>>();
            
            try {

                // find all configured devices for this member
                pstmt = con.prepareStatement(""
                        + "SELECT *, DATE_FORMAT(date_created, '%b %d %Y %h:%i %p') AS pretty_date "
                        + "FROM v5.mobile_auth "
                        + "WHERE member_id = ? AND club_id = ? AND active = 1 AND ISNULL(auth_lookup_code) AND ISNULL(auth_username) AND ISNULL(auth_password)"
                        + "ORDER BY date_created;");
                pstmt.clearParameters();
                pstmt.setLong(1, member_id);
                pstmt.setInt(2, club_id);
                rs = pstmt.executeQuery();

                // display each device that is configured and allow them to be removed
                while ( rs.next() ) {

                    found = true;
                    
                    out.println("<p style=\"padding-left:40px;\">");
                    out.println( rs.getString("device_name") );
                    out.println( rs.getString("pretty_date") );
                    out.println("&nbsp;&nbsp;&nbsp;<input type=\"button\" value=\"Deactivate\" onclick=\"window.location.href='?deactivate&id=" + rs.getInt("id") + "#jumpToApp'\">");
                    out.println("</p>");

                }

            } catch (Exception ignore) { // any error is a fail

                out.println("<P>ERROR: " + ignore.toString() + "</P>");

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            } 

            if (!found) {
                
                out.println("<p style=\"padding-left:40px;\">No configured devices found.</p>");
            }
            
            if ( rwd ) {
                out.print("</fieldset>"); 
            } else {
                out.println("</td>");
                out.println("</tr>");
            }
        }          // end of IF clubCentralOnly

    } else {

        //
        //   Club has not purchased the app yet - inform the member in case they found the app online and want to activate it
        //
        if(rwd){

            //out.print("<span>&nbsp;&nbsp;&nbsp;Sorry, the ForeTees App (ClubCentral) is not yet available at your club.</span><br>");
            out.print("<span>&nbsp;&nbsp;&nbsp;Sorry, your club has not yet made the ClubCentral app available. &nbsp;If you have downloaded the app, " +
                        "you will not be able to activate it at this time.</span><br>");

        } else{

            out.println("<tr><td><br>");
/*
            out.println("<p style=\"padding-left:20px;\">Sorry, the ClubCentral app is not yet available. &nbsp;While it is available in the app stores, " +
                        "it is still being tested and has not been released for general use. &nbsp;We will notify your club once it is available. &nbsp;Thank you"
                    + " for your patience.</p><br>");

            */
            // USE THIS ONCE THE APP IS AVAILABLE FOR ALL CLUBS !!!!!!!!!!!
            out.println("<p style=\"padding-left:20px;\">Sorry, your club has not yet made the ClubCentral app available. &nbsp;If you have downloaded the app, " +
                        "you will not be able to activate it at this time.</p><br>");

            out.println("</td></tr>");
        }

    }      // end of IF clubCentral supported


    if (!clubCentralOnly) {


        //
        //  Add Tennis options - ******************* CHANGE THIS LATER ****************************
        //
        // if ( activity_system == true ) {  // Activities - add row for group title
        if ( club.startsWith("admiralscove")  || Common_Server.SERVER_ID == 4 ) {  // Activities - add row for group title

            if(rwd){
                out.print("<a name=\"tennis\"></a><fieldset class=\"standard_fieldset ftTennisOptions\"><legend>Tennis Options:</legend>"); 
                out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>USTA Number:</span><input type=\"text\" name=\"usta\" maxlength=\"16\" value=\"" + usta +"\" autocomplete=\"off\"></label>");
                out.print("<label onclick=\"\" class=\"ftInputLabel\"><span>NTRP Rating:</span><input type=\"text\" name=\"ntrp\" maxlength=\"12\" value=\"" + ntrp +"\" autocomplete=\"off\"></label>");
                out.print("</fieldset>"); 
            } else {

            out.println("<tr><td><BR><hr width=\"75%\" align=\"center\"><BR></td></tr>"); 

            out.println("<tr><td>");

            out.println("<strong>Tennis Options</strong></td></tr>");

            //
            //   Tennis values - USTA Number and NTRP Rating
            //
            out.println("<tr><td>");
            out.println("<p>&nbsp;&nbsp;&nbsp;USTA Number&nbsp;");
            out.println("<input type=\"text\" name=\"usta\" size=\"12\" maxlength=\"16\" value=\"" +usta+"\">");

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;NTRP Rating&nbsp;");
            out.println("<input type=\"text\" name=\"ntrp\" size=\"8\" maxlength=\"12\" value=\"" +ntrp+"\">");
            out.println("</p></td></tr>");
            }

        }    // end of IF activity system


        if(!rwd){
            out.println("</table>");                  // end of main page table
            out.println("</div>");
        }

        out.println("<input type=\"hidden\" name=\"old_wc\" value=\"" + owc + "\">");
        if(!rwd){
            out.println("<br>");
        }
        /*
        if (dining) {

                //out.println("<button class=\"btnNorm\" onclick=\"location.href='Dining_home'\">Home</button>");
                out.println("<div id=\"tt2_left\" align=\"center\">" +
                    "<input type=\"button\" value=\"Cancel\" id=\"back\" onclick=\"window.location.href='Dining_home'\">" +
                    "<input type=\"submit\" value=\"Update\" id=\"back\">" +
                    "</div>");

                out.println("</form>");

        } else {*/

                out.println("<div id=\"tt2_left\" align=\"center\">" +
                    "<input type=\"button\" value=\"Cancel\" id=\"back\" onclick=\"window.location.href='Member_announce'\">" +
                    "<input type=\"submit\" value=\"Update\" id=\"back\">" +
                    "</div>");

                out.println("</form>");
       //     }

    }       // end of IF clubCentralOnly
                
    if(rwd){
        out.print("</div>");
    } else {
        out.println("</center></div></div>"); // is this right for desktop mode??
    }
    Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       

   out.close();

 }


 //
 //******************************************************************************
 // Process the form request from member_services.htm (built by doGet above)
 //******************************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
   ResultSet rs = null;

   Member member = new Member();

   HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   con = Connect.getCon(req);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   int count = 0;
   int emailOpt = 0;
   int emailOpt2 = 0;
   int clubEmailOpt1 = 0;
   int clubEmailOpt2 = 0;
   int memEmailOpt1 = 0;
   int memEmailOpt2 = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int length = 0;
   int mobile = 0;                // mobile user
   int tee_sheet_jump = 0;
   int old_tee_sheet_jump = 0;

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name
   boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   boolean dining = false;
   
   //boolean new_skin = (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID);
   if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
      
      new_skin = true;
      dining = true;
   }

   
   //
   //  See if Mobile user
   //
   try {      
      mobile = (Integer)session.getAttribute("mobile");        
   }
   catch (Exception ignore) {   
      mobile = 0;
   }
      
   
   if (mobile > 0) {        // if MOBILE user
      
      doMobilePost(user, club, req, out, con);
      return;
   }
   
   
   
   String clubName = Utilities.getClubName(con, true);        // get the full name of this club
   

   //
   //  Normal user
   //
   String wc = "";
   String old_wc = "";
   String scourse = "";       // course hndcp
   String susga = "";         // usga hndcp
   String usta = "";
   String temp = "";
   String errMsg = "";

   float course = -99;     // default hndcp's
   float usga = -99;
   float ntrp = 0;


   //
   // Get all parameters entered
   //
   String email = req.getParameter("em1");
   String email2 = req.getParameter("em2");
   String old_email = req.getParameter("old_email");
   String old_email2 = req.getParameter("old_email2");
   String phone1 = req.getParameter("phone1");
   String phone2 = req.getParameter("phone2");
   String old_phone1 = req.getParameter("old_phone1");
   String old_phone2 = req.getParameter("old_phone2");
   String sresetBounceFlags = req.getParameter("resetBounceFlags");
   boolean resetBounceFlags = (sresetBounceFlags.equals("1"));
   
   String curr_password = "";
   String old_password = "";
   String new_password = "";
   String conf_password = "";

   String curr_mobile_pw = "";
   String old_mobile_pw = "";
   String new_mobile_pw = "";
   String conf_mobile_pw = "";
   String mobile_user = "";

   if (req.getParameter("walk_cart") != null) {         // if parm specified

      wc = req.getParameter("walk_cart");                  // walk/cart preference
   }

   if (req.getParameter("old_wc") != null) {         // if parm specified

      old_wc = req.getParameter("old_wc");                 // old walk/cart preference
   }

   if (req.getParameter("oldpw") != null) {         // if old pw specified, then process newpw

      old_password = req.getParameter("oldpw");
      new_password = req.getParameter("newpw");
      conf_password = req.getParameter("newpwc");
   }

   if (req.getParameter("mobile_user") != null) {         // if mobile username provided

      mobile_user = req.getParameter("mobile_user");
   }
   if (req.getParameter("oldmobilepw") != null) {         // if old mobile pw 

      old_mobile_pw = req.getParameter("oldmobilepw");
   }
   if (req.getParameter("newmobilepw") != null) {         // if new mobile pw 

      new_mobile_pw = req.getParameter("newmobilepw");
   }
   if (req.getParameter("newmobilepwc") != null) {         // if new mobile pw confirmed 

      conf_mobile_pw = req.getParameter("newmobilepwc");
   }

   if (req.getParameter("c_hancap") != null) {      

      scourse = req.getParameter("c_hancap");       // course hndcp
   }
   if (req.getParameter("g_hancap") != null) {

      susga = req.getParameter("g_hancap");         // usga hndcp
   }

   if (req.getParameter("usta") != null) {    

      usta = req.getParameter("usta");       // Tennis - USTA Number
   }
   
   if (req.getParameter("ntrp") != null) {

      temp = req.getParameter("ntrp");         // Tennis - NTRP Rating
   }

   if (req.getParameter("tee_sheet_jump") != null) {

       tee_sheet_jump = (req.getParameter("tee_sheet_jump").equals("1")) ? 1 : 0;
   }
   if (req.getParameter("old_tee_sheet_jump") != null) {

       old_tee_sheet_jump = (req.getParameter("old_tee_sheet_jump").equals("1")) ? 1 : 0;
   }

   
   if (req.getParameter("iCal1") != null) iCal1 = Integer.parseInt(req.getParameter("iCal1"));
   
   if (req.getParameter("iCal2") != null) iCal2 = Integer.parseInt(req.getParameter("iCal2"));
   
   if (req.getParameter("emOpt1") != null) emailOpt = Integer.parseInt(req.getParameter("emOpt1"));
   
   if (req.getParameter("emOpt2") != null) emailOpt2 = Integer.parseInt(req.getParameter("emOpt2"));
   
   if (req.getParameter("clubEmOpt1") != null) clubEmailOpt1 = Integer.parseInt(req.getParameter("clubEmOpt1"));
   
   if (req.getParameter("clubEmOpt2") != null) clubEmailOpt2 = Integer.parseInt(req.getParameter("clubEmOpt2"));
   
   if (req.getParameter("memEmOpt1") != null) memEmailOpt1 = Integer.parseInt(req.getParameter("memEmOpt1"));
   
   if (req.getParameter("memEmOpt2") != null) memEmailOpt2 = Integer.parseInt(req.getParameter("memEmOpt2"));
      
   
   int default_activity_id = 0;

   try { default_activity_id = Integer.parseInt(req.getParameter("default_activity_id")); }
   catch (Exception ignore) { }
    
   //
   //  convert numeric fields
   //
   if (!scourse.equals("")) {

      if ((scourse.startsWith("+")) || (scourse.startsWith("-"))) {

         length = scourse.length();       // get length of parm value

         if (length < 2) {
            
            errMsg = "Sorry, there is a problem with the data you entered." +
                     "<BR>The handicap must contain a numeric value." + 
                     "<BR>The '+' or '-' must be followed be a number value." + 
                     "<BR><BR>Please try again." + 
                     "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

            Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);
            
            out.close();
            return;
         }
      }

      try {

         course = Float.parseFloat(scourse);               // course handicap

      }
      catch (Exception exc) {             // SQL Error

         errMsg = "Sorry, there is a problem with the data you entered." +
                  "<BR>The handicap must contain a numeric value." + 
                  "<BR>The '+' or '-' must be followed be a number value." + 
                  "<BR><BR>Please try again." + 
                  "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

         Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

         out.close();
         return;
      }

      if ((!scourse.startsWith("+")) && (!scourse.startsWith("-"))) {

         course = 0 - course;                    // make it a negative hndcp (normal)
      }
   }

   if (!susga.equals("")) {

      if ((susga.startsWith("+")) || (susga.startsWith("-"))) {

         length = susga.length();       // get length of parm value

         if (length < 2) {

            errMsg = "Sorry, there is a problem with the data you entered." +
                     "<BR>The handicap must contain a numeric value." + 
                     "<BR>The '+' or '-' must be followed be a number value." + 
                     "<BR><BR>Please try again." + 
                     "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

            Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

            out.close();
            return;
         }
      }

      try {

         usga = Float.parseFloat(susga);                   // usga handicap

      }
      catch (Exception exc) {             // SQL Error

         errMsg = "Sorry, there is a problem with the data you entered." +
                  "<BR>The handicap must contain a numeric value." + 
                  "<BR>The '+' or '-' must be followed be a number value." + 
                  "<BR><BR>Please try again." + 
                  "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

         Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

         out.close();
         return;
      }

      if ((!susga.startsWith("+")) && (!susga.startsWith("-"))) {

         usga = 0 - usga;                       // make it a negative hndcp (normal)
      }
   }

   if (!temp.equals("")) {

      try {

         ntrp = Float.parseFloat(temp);                   // NTRP Rating

      }
      catch (Exception exc) {             // SQL Error

         errMsg = "Sorry, there is a problem with the data you entered." +
                  "<BR>NTRP Rating entry is invalid." + 
                  "<BR>The NTRP Rating must contain a numeric value." + 
                  "<BR><BR>Please try again." + 
                  "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

         Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

         out.close();
         return;
      }
   }


   //if (!club.startsWith( "demov" ) || ( club.startsWith( "demov" ) && Common_Server.SERVER_ID == 4) ) {    // do not change emailOpt if demov? site unless on dev server

      try {

         PreparedStatement stmte = con.prepareStatement (
               "UPDATE member2b SET emailOpt = ?, emailOpt2 = ? WHERE username = ?");

         stmte.clearParameters();           // clear the parms
         stmte.setInt(1, emailOpt);
         stmte.setInt(2, emailOpt2);
         stmte.setString(3, user);          // put username in statement
         count = stmte.executeUpdate();     // execute the prepared stmte

         stmte.close();

      }
      catch (Exception exc) {               // SQL Error

            out.println(SystemUtils.HeadTitle("DB Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Database Access Error 2</H2>");
            out.println("<BR><BR>Unable to process database change at this time.");
            out.println("<BR>Exception: "+ exc.getMessage());
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
      }
   //}      // end of if demo site


   int pw_length = new_password.length();               // length of new password

   if (pw_length != 0) {

      try {

         PreparedStatement stmt = con.prepareStatement (
                  "SELECT password FROM member2b WHERE username = ?");   // get member's pw...

         stmt.clearParameters();            // clear the parms
         stmt.setString(1, user);           // put username in statement
         rs = stmt.executeQuery();          // execute the prepared stmt

         if (rs.next()) {

            curr_password = rs.getString("password");
         }
         stmt.close();

      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 1</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your system administrator.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }

      //
      // Verify the passwords
      //
      if ((old_password.equalsIgnoreCase( curr_password )) && (new_password.equalsIgnoreCase(conf_password)) &&
          (pw_length > 3) && (pw_length < 16)) {


        // if (!club.startsWith( "demov" )) {        // do not change pw if demo site

            try {

               PreparedStatement stmt = con.prepareStatement (
                     "UPDATE member2b SET password = ? WHERE username = ?");

               // Set member's new pw

               stmt.clearParameters();            // clear the parms
               stmt.setString(1, new_password);   // put the password in statement
               stmt.setString(2, user);           // put username in statement
               count = stmt.executeUpdate();  // execute the prepared stmt

               stmt.close();

            }
            catch (Exception exc) {             // SQL Error

                  out.println(SystemUtils.HeadTitle("DB Access Error"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
                  out.println("<BR><BR><H2>Database Access Error 2</H2>");
                  out.println("<BR><BR>Unable to process database change at this time.");
                  out.println("<BR>Exception: "+ exc.getMessage());
                  out.println("<BR>Please try again later.");
                  out.println("<BR><BR>If problem persists, contact your golf shop staff.");
                  out.println("<BR><BR>");
                  out.println("<a href=\"Member_announce\">Return</a>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
            }
        // }      // end of if demo site

      } else {

         errMsg = "Sorry, there is a problem with the data you entered." +
                  "<BR>The Current Password must equal the Password that you used to login." + 
                  "<BR>The New Password must be at least 4 characters but no more than 15 characters." + 
                  "<BR>Avoid special characters as some are not accepted." + 
                  "<BR><BR>Please try again." + 
                  "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

         Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

         out.close();
         return;

      }
   }

   if ((club.equals("talbotcc") || club.equals("dallasathleticclub") || club.equals("reflectionridgegolf")) && email.equals("") && email2.equals("")) {

         errMsg = "Sorry, there is a problem with the data you entered." +
               "<BR>No email address was specified.  Your club has requested that an email address be required for all members." + 
               "<BR>Please return and enter at least one email address." + 
               "<BR>Contact your golf shop staff if you have any questions.<BR><BR>";

         Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

       out.close();
       return;
   }
   
   if (resetBounceFlags || !email.equalsIgnoreCase( old_email ) || !email2.equalsIgnoreCase( old_email2 )) {  // if either email address has changed

      if (!email.equals( "" )) {      // if specified

         email = email.trim();              // remove any spaces
         FeedBack feedback = (member.isEmailValid(email));
           
         if (!feedback.isPositive()) {    // if error

            String emailError = feedback.get(0);             // get error message 

            errMsg = "Sorry, there is a problem with the data you entered." +
                     "<BR>The primary email address you entered is invalid." + 
                     "<BR>" +emailError + 
                     "<BR><BR>Please try again." + 
                     "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

            Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

            out.close();
            return;
         }
      }

      if (!email2.equals( "" )) {      // if specified

         email2 = email2.trim();            // remove any spaces
         FeedBack feedback = (member.isEmailValid(email2));

         if (!feedback.isPositive()) {    // if error

            String emailError = feedback.get(0);             // get error message

            errMsg = "Sorry, there is a problem with the data you entered." +
                     "<BR>The secondary email address you entered is invalid." + 
                     "<BR>" + emailError + 
                     "<BR><BR>Please try again." + 
                     "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

            Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);

            out.close();
            return;
         }
      }

      
      
      try {

         String tmp_sql = "UPDATE member2b SET email = ?, email2 = ?";

         if (resetBounceFlags || email.equals( "" ) || (!email.equals( "" ) && !email.equalsIgnoreCase(old_email))) {
            tmp_sql += ", email_bounced = 0";
         }

         if (resetBounceFlags || email2.equals( "" ) || (!email2.equals( "" ) && !email2.equalsIgnoreCase(old_email2))) {
            tmp_sql += ", email2_bounced = 0";
         }

         tmp_sql += " WHERE username = ?";

         PreparedStatement pstmt4 = con.prepareStatement ( tmp_sql );
         pstmt4.clearParameters();

         pstmt4.setString(1, email);        // set email address
         pstmt4.setString(2, email2);        // set email address 2
         pstmt4.setString(3, user); 

         count = pstmt4.executeUpdate();    
         pstmt4.close();
            
        /*  old code
         PreparedStatement pstmt4 = con.prepareStatement (
               "UPDATE member2b SET email = ?, email2 = ? WHERE username = ?");

         pstmt4.clearParameters();            // clear the parms
         pstmt4.setString(1, email);
         pstmt4.setString(2, email2);
         pstmt4.setString(3, user);
         count = pstmt4.executeUpdate();          // execute the prepared pstmt4

         pstmt4.close();
        */
      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 3</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club administrator.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }
        
      //
      //  Email has changed - if Westchester, send an email notification to the club so they can update their records.
      //
      if (club.equals( "westchester" )) {        
  
         sendWestEmail(email, email2, user, con);
      }

      //
      //  Email has changed - if Pelicans Nest, send an email notification to the club so they can update their records.
      //
      if (club.equals( "pelicansnest" )) {

         sendPNestEmail(email, email2, user, con);
      }

      //
      //  Email has changed - if Fox Den CC, send an email notification to the club so they can update their records.
      //
      if (club.equals( "foxdencountryclub" )) {

         sendFoxDenEmail(email, email2, user, con);
      }

      //
      //  Email has changed - if Omaha CC, send an email notification to the club so they can update their records.
      //
      if (club.equals( "omahacc" )) {

         sendOmahaEmail(email, email2, user, con);
      }

   } // end of if either email address has changed


   //
   // Update iCal fields, email options, and default_activity_id for this member record
   //
   if ( email.equals("") ) {
      
      iCal1 = 0;
      clubEmailOpt1 = 0;
      memEmailOpt1 = 0;
   }
   if ( email2.equals("") ) {
      
      iCal2 = 0;
      clubEmailOpt2 = 0;
      memEmailOpt2 = 0;
   }
   
   PreparedStatement pstmt = null;

   try {

       pstmt = con.prepareStatement( "UPDATE member2b " +
                                     "SET clubEmailOpt1 = ?, clubEmailOpt2 = ?, memEmailOpt1 = ?, memEmailOpt2 = ?, iCal1 = ?, iCal2 = ?, default_activity_id = ? " +
                                     "WHERE username = ?" );
       pstmt.clearParameters();
       pstmt.setInt(1, clubEmailOpt1);
       pstmt.setInt(2, clubEmailOpt2);
       pstmt.setInt(3, memEmailOpt1);
       pstmt.setInt(4, memEmailOpt2);
       pstmt.setInt(5, iCal1);
       pstmt.setInt(6, iCal2);
       pstmt.setInt(7, default_activity_id);
       pstmt.setString(8, user);

       pstmt.executeUpdate();

   } catch (Exception exc) {

       out.println(SystemUtils.HeadTitle("DB Access Error"));
       out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
       out.println("<BR><BR><H2>Database Access Error</H2>");
       out.println("<BR><BR>We encountered an error while attempting to update your iCalendar preferences.");
       out.println("<BR>Please try again later. (" + exc.toString() + ")");
       out.println("<BR><BR>If problem persists, contact your club administrator.");
       out.println("<BR><BR>");
       out.println("<a href=\"Member_services\">Return</a>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;

   } finally {

       try { pstmt.close(); }
       catch (SQLException ignored) {}
   }


   if (!wc.equals( old_wc )) {       // if the w/c pref has changed

      try {

         PreparedStatement pstmt5 = con.prepareStatement (
               "UPDATE member2b SET wc = ? WHERE username = ?");

         pstmt5.clearParameters();                // clear the parms
         pstmt5.setString(1, wc);
         pstmt5.setString(2, user);
         count = pstmt5.executeUpdate();          // execute the prepared pstmt5

         pstmt5.close();

         session.setAttribute("wc", wc);          // save member's new walk/cart pref (for _slot)
      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 3</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club administrator.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }
   }

   if ((!phone1.equals(old_phone1)) || (!phone2.equals(old_phone2))) {   // if a phone # changed

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
               "UPDATE member2b SET phone1 = ?, phone2 = ? WHERE username = ?");

         pstmt6.clearParameters();                // clear the parms
         pstmt6.setString(1, phone1);
         pstmt6.setString(2, phone2);
         pstmt6.setString(3, user);
         count = pstmt6.executeUpdate();          // execute the prepared pstmt6

         pstmt6.close();

      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 3</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club administrator.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   if ((!susga.equals("")) && (!scourse.equals(""))) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
               "UPDATE member2b SET c_hancap = ?, g_hancap = ? WHERE username = ?");

         pstmt6.clearParameters();                // clear the parms
         pstmt6.setFloat(1, course);
         pstmt6.setFloat(2, usga);
         pstmt6.setString(3, user);
         count = pstmt6.executeUpdate();          // execute the prepared pstmt6

         pstmt6.close();
      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 3</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club administrator.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
   
   //
   //  Update tee sheet jump if it changed
   //
   if (tee_sheet_jump != old_tee_sheet_jump) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
               "UPDATE member2b SET tee_sheet_jump = ? WHERE username = ?");

         pstmt6.clearParameters();                // clear the parms
         pstmt6.setInt(1, tee_sheet_jump);
         pstmt6.setString(2, user);
         count = pstmt6.executeUpdate();          // execute the prepared pstmt6

         pstmt6.close();
      }
      catch (Exception exc) {             // SQL Error
      }
   }
   
   
   //
   //   Process MOBILE Credentials
   //
   if (!mobile_user.equals( "" )) {                  // if mobile username provided
      
         try {

            PreparedStatement stmt = con.prepareStatement (
                  "UPDATE member2b SET mobile_user = ? WHERE username = ?");

            stmt.clearParameters();            
            stmt.setString(1, mobile_user);   
            stmt.setString(2, user);          
            count = stmt.executeUpdate();  

            stmt.close();

         }
         catch (Exception exc) {             // SQL Error

               out.println(SystemUtils.HeadTitle("DB Access Error"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
               out.println("<BR><BR><H2>Data Entry Error</H2>");
               out.println("<BR><BR>Unable to process database change at this time.");
               out.println("<BR>Exception: "+ exc.getMessage());
               out.println("<BR>Please try again later.");
               out.println("<BR><BR>If problem persists, contact your golf shop staff.");
               out.println("<BR><BR>");
               out.println("<a href=\"Member_services\">Return</a>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
         }      
   }
      
   pw_length = new_mobile_pw.length();               // length of new mobile password

   if (pw_length != 0) {

      try {

         PreparedStatement stmt = con.prepareStatement (
                  "SELECT mobile_pass FROM member2b WHERE username = ?");   // get member's pw...

         stmt.clearParameters();            // clear the parms
         stmt.setString(1, user);           // put username in statement
         rs = stmt.executeQuery();          // execute the prepared stmt

         if (rs.next()) {

            curr_mobile_pw = rs.getString("mobile_pass");
         }
         stmt.close();

      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Error</H2>");
         out.println("<BR><BR>Unable to process mobile changes at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }
      
      if (curr_mobile_pw == null) curr_mobile_pw = "";        // empty string if never specified

      //
      // Verify the passwords
      //
      if ((old_mobile_pw.equalsIgnoreCase( curr_mobile_pw )) && (new_mobile_pw.equalsIgnoreCase(conf_mobile_pw)) &&
          (pw_length > 3) && (pw_length < 16)) {

            try {

               PreparedStatement stmt = con.prepareStatement (
                     "UPDATE member2b SET mobile_pass = ? WHERE username = ?");

               stmt.clearParameters();            
               stmt.setString(1, new_mobile_pw);   
               stmt.setString(2, user);          
               count = stmt.executeUpdate();  

               stmt.close();

            }
            catch (Exception exc) {             // SQL Error

                  out.println(SystemUtils.HeadTitle("DB Access Error"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
                  out.println("<BR><BR><H2>Database Error</H2>");
                  out.println("<BR><BR>Unable to process mobile password change at this time.");
                  out.println("<BR>Exception: "+ exc.getMessage());
                  out.println("<BR>Please try again later.");
                  out.println("<BR><BR>If problem persists, contact your golf shop staff.");
                  out.println("<BR><BR>");
                  out.println("<a href=\"Member_services\">Return</a>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
            }

      } else {

         errMsg = "Sorry, there is a problem with the data you entered." +
                  "<BR>The Current Mobile Password must equal the password you currently use for the mobile site." + 
                  "<BR>The New Mobile Password must be at least 4 characters but no more than 15 characters." + 
                  "<BR>Avoid special characters as some are not accepted." + 
                  "<BR><BR>Please try again." + 
                  "<BR>If problem persists, please contact your golf shop staff.<BR><BR>";

         Common_skin.outputError(club, clubName, sess_activity_id, "Data Entry Error", errMsg, "javascript:history.back(1)", out, req);
         out.close();
         return;
      }
   }          // end of Mobile password

     
   
   //
   // UPDATE TENNIS INFORMATION
   //
   if (!usta.equals("") || ntrp != 0) {

      try {

         PreparedStatement pstmt6 = con.prepareStatement (
               "UPDATE member2b SET ntrp_rating = ?, usta_num = ? WHERE username = ?");

         pstmt6.clearParameters();                // clear the parms
         pstmt6.setFloat(1, ntrp);
         pstmt6.setString(2, usta);
         pstmt6.setString(3, user);
         count = pstmt6.executeUpdate();          // execute the prepared pstmt6

         pstmt6.close();
      }
      catch (Exception exc) {             // SQL Error

         out.println(SystemUtils.HeadTitle("DB Access Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Database Access Error 3</H2>");
         out.println("<BR><BR>Unable to process database change at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact your club administrator.");
         out.println("<BR><BR>");
         out.println("<a href=\"Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

   // If organization_id is greater than 0, Dining system is in use.  Push updates to this member's record over to the dining system database
   if (Utilities.getOrganizationId(con) > 0) {
       Admin_editmem.updDiningDB(user, con);
   }
   
   //
   // Done - return.......
   //
   String title = "Member Settings Complete";

   Common_skin.outputHeader(club, sess_activity_id, title, true, out, req);     // output the page start

   out.println("<body>");
   out.println("<div id=\"wrapper_login\" align=\"center\">");
   out.println("<div id=\"title\">" +clubName+ "</div>");
   out.println("<div id=\"main_login\" align=\"center\">");
   out.println("<h1>Personal Settings</h1>");
   out.println("<div class=\"main_message\">");
   out.println("<h2>Your Personal Settings Have Been Updated</h2><br /><br />");
   out.println("<center><div class=\"sub_instructions\">");
   out.println("Thank you.");

   if (!new_password.equals( "" ) && !new_password.equals( old_password )) {

      if (caller.equals( "none" ) || caller.startsWith("foretees")) {        // if user did not come from MemFirst or other web site

         out.println("<BR><BR>You have changed your password. Be sure to use the new password next time you login.");
      } else {
         out.println("<BR><b>Notice:</b> You have changed your password.  This password is not used when you enter");
         out.println("<BR>ForeTees from your club's web site.  Refer to instructions from your club's web site for");
         out.println("<BR>changing that password.  You will only use the ForeTees password when entering");
         out.println("<BR>ForeTees directly (via the ForeTees Login Page).");
      }
   }
   out.println("<br /></div>");

   /*if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
         out.println("<form method=\"get\" action=\"Dining_home\">");
         out.println("<input type=\"hidden\" name=\"settings\" value=\"\">");
   } else {*/
         out.println("<form method=\"get\" action=\"Member_services\" autocomplete=\"off\">");
   //}
   out.println("<input id=\"returnButton\" type=\"submit\" value=\"Return\" id=\"submit\">");
   out.println("</form>");

   /*if (sess_activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
         out.println("<form method=\"get\" action=\"Dining_home\">");
   } else {*/
         out.println("<form method=\"get\" action=\"Member_announce\" autocomplete=\"off\">");
   //}
   out.println("<input type=\"submit\" value=\"Home\" id=\"submit\">");
   out.println("</form></center></div></div>");

   Common_skin.outputPageEnd(club, sess_activity_id, out, req);    // finish the page       
      
   out.close();

 }
 
 

 // ************************************************************************
 //  Mobile user - only allow them to change their mobile password
 // ************************************************************************

 private void doMobile(HttpServletRequest req, PrintWriter out, Connection con) {


   //
   //  output a html page to prompt user for changes - wait for doPost entry
   //
   out.println(SystemUtils.HeadTitleMobile("Member Services"));
   out.println(SystemUtils.BannerMobile());
      
   out.println("<form action=\"Member_services\" method=\"post\" name=\"setting_form\" id=\"setting_form\" autocomplete=\"off\">");
   out.println("<input type=\"hidden\" name=\"domobile\" value=\"yes\">");

   out.println("<div class=\"content\"><BR>");
   out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"settings\"><tr class=\"tableheader\">");
   out.println("<td><strong>");

   out.println("Use this page to change your mobile password.");
   out.println("&nbsp;&nbsp;Click on 'Submit' to process the change.</td>");
   out.println("</tr>");

   out.println("<tr class=\"tablerow\"><td>");
   out.println("<p>Current Mobile Password:&nbsp;&nbsp;");
   out.println("<input type=\"password\" name=\"oldpw\" id=\"oldpw\" size=\"8\" maxlength=\"15\">");
   out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;New Mobile Password:&nbsp;&nbsp;<input type=\"password\" name=\"newpw\" size=\"8\" maxlength=\"15\">");
   out.println("<BR>(4 - 15 characters)</p>");
   out.println("<p>Confirm New Password:&nbsp;&nbsp;<input type=\"password\" name=\"newpwc\" size=\"8\" maxlength=\"15\"></p>");
   out.println("</td></tr>");
   out.println("</table></div>");

   out.println("<div class=\"content\"><ul>");      
   out.println("<li><input type=\"submit\" value=\"Submit Changes\">");
   out.println("</form></li>");
   out.println("<li>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/mobile/member_mobile_home.html\" autocomplete=\"off\">");
   out.println("<input type=\"submit\" value=\"Cancel - Do Not Change\" style=\"text-decoration:underline;\">");
   out.println("</form></li></ul></div>");
   out.println("</body></html>");           
   out.flush();
 }
   
 
 
 // ************************************************************************
 //  Mobile user - only allow them to change their mobile password - process form post
 // ************************************************************************

 private void doMobilePost(String user, String club, HttpServletRequest req, PrintWriter out, Connection con) {

    
   ResultSet rs = null;
    
   String curr_password = "";
   String old_password = "";
   String new_password = "";
   String conf_password = "";

   if (req.getParameter("oldpw") != null) {         // if old pw specified, then process new pw

      old_password = req.getParameter("oldpw");
      new_password = req.getParameter("newpw");
      conf_password = req.getParameter("newpwc");
   }

   int pw_length = new_password.length();               // length of new password

   if (pw_length != 0) {

      try {

         PreparedStatement stmt = con.prepareStatement (
                  "SELECT mobile_pass FROM member2b WHERE username = ?");   // get member's pw...

         stmt.clearParameters();            // clear the parms
         stmt.setString(1, user);           // put username in statement
         rs = stmt.executeQuery();          // execute the prepared stmt

         if (rs.next()) {

            curr_password = rs.getString("mobile_pass");     // get current mobile password
         }
         stmt.close();

      }
      catch (Exception exc) {             // SQL Error

         SystemUtils.displayMobileError("Error accessing your account.<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
         out.close();
         return;

      }

      //
      // Verify the passwords
      //
      if ((old_password.equalsIgnoreCase( curr_password )) && (new_password.equalsIgnoreCase(conf_password)) &&
          (pw_length > 3) && (pw_length < 16)) {


            try {

               PreparedStatement stmt = con.prepareStatement (
                     "UPDATE member2b SET mobile_pass = ? WHERE username = ?");

               // Set member's new pw

               stmt.clearParameters();           
               stmt.setString(1, new_password);   // put the password in statement
               stmt.setString(2, user);           // put username in statement
               stmt.executeUpdate();  

               stmt.close();

            }
            catch (Exception exc) {             // SQL Error

                  SystemUtils.displayMobileError("Error accessing your account.<BR><BR>If problem persists, contact your golf shop staff.", "", out);    // ouput error message
                  out.close();
                  return;
            }

      } else {

         SystemUtils.displayMobileError("The data you entered is incorrect.<BR>The Current Password must equal the Password that you logged in with.<BR>The New Password must be at least 4 characters but no more than 15 characters.<BR><BR>If problem persists, contact your golf shop staff.", "servlet/Member_services", out);    // ouput error message
         out.close();
         return;

      }
   }      // end of IF new pw provided


   //
   //  output a response
   //
   out.println(SystemUtils.HeadTitleMobile("Member Services"));
   out.println(SystemUtils.BannerMobile());
      
   out.println("<div class=\"headertext\">Mobile Password Changed</div>");
   out.println("<div class=\"smheadertext\">Thank you, your password has been changed.<BR></div>");     
   out.println("<div class=\"content\"><ul>");      
   out.println("<li><a href=\"/" +rev+ "/mobile/member_mobile_home.html\">Return . . . . . . . . . . . . . . .</a></li></ul>");  // use dots to make the whole line clickable!!
   out.println("</div></body></html>");           
   out.flush();
 }
   
 
 
 // ************************************************************************
 //  Process custom to send an email notification for Westchester CC
 // ************************************************************************

 private void sendWestEmail(String email, String email2, String user, Connection con) {


   //
   //  allocate a parm block to hold the email parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Set the values in the email parm block
   //
   parme.type = "Westchester";         // type = Westchester
   parme.user = user;
   parme.player1 = email;
   parme.player2 = email2;

   //
   //  Send the email
   //
   sendEmail.sendWestEmail(parme, con);      // send an email to Westchester

 }
   
 // ************************************************************************
 //  Process custom to send an email notification for Pelicans Nest GC
 // ************************************************************************

 private void sendPNestEmail(String email, String email2, String user, Connection con) {


   //
   //  allocate a parm block to hold the email parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Set the values in the email parm block
   //
   parme.type = "PelicansNest";         // type 
   parme.user = user;
   parme.player1 = email;
   parme.player2 = email2;

   //
   //  Send the email
   //
   sendEmail.sendWestEmail(parme, con);      // send an email to Pelicans Nest (use same as Westchester)

 }

 // ************************************************************************
 //  Process custom to send an email notification for Fox Den CC
 // ************************************************************************

 private void sendFoxDenEmail(String email, String email2, String user, Connection con) {


   //
   //  allocate a parm block to hold the email parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Set the values in the email parm block
   //
   parme.type = "FoxDen";         // type
   parme.user = user;
   parme.player1 = email;
   parme.player2 = email2;

   //
   //  Send the email
   //
   sendEmail.sendWestEmail(parme, con);      // send an email to Pelicans Nest (use same as Westchester)

 }

 // ************************************************************************
 //  Process custom to send an email notification for Fox Den CC
 // ************************************************************************

 private void sendOmahaEmail(String email, String email2, String user, Connection con) {


   //
   //  allocate a parm block to hold the email parms
   //
   parmEmail parme = new parmEmail();          // allocate an Email parm block

   //
   //  Set the values in the email parm block
   //
   parme.type = "Omaha";         // type
   parme.user = user;
   parme.player1 = email;
   parme.player2 = email2;

   //
   //  Send the email
   //
   sendEmail.sendWestEmail(parme, con);      // send an email to Pelicans Nest (use same as Westchester)

 }
 
}
