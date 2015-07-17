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


public class Member_services extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 // Process the initial call from member_main.htm
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


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
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
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

   int rsync = 0;
   int emailOpt = 0;
   int email_bounced = 0;
   int email2_bounced = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int mobile = 0;              // Mobile user
   int default_activity_id = 0;
   int foretees_mode = 0;

   float c_hancap = 0;
   float g_hancap = 0;
   float ntrp = 0;              // Tennis rating value
   
   boolean activity_system = false;
   boolean allowMobile = false;


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
         getParms.getCourse(con, parmc, course);    // course not used yet

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
         out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }

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
   if ( getActivity.isConfigured(con) ) {
      
      activity_system = true;              // if yes
   }

   
   // 
   //  Set the appropriate language for the times to search for (i.e. Tee Times, Times, etc)
   //
   String timesText = "";
   
   if (activity_system == false) {        // if Golf only
      
      timesText = sysLingo.TEXT_tee_times;   // get text based on Tee Times or Notification System
      
   } else {
      
      timesText = "Reservations";           // generic term for members that use both or non-golf
   }
   
   
   //
   //  Get the current info for this user
   //
   try {

      pstmt = con.prepareStatement (
             "SELECT " +
                 "m.email, m.c_hancap, m.g_hancap, m.wc, m.emailOpt, m.ghin, m.email2, m.phone1, m.phone2, " +
                 "m.email_bounced, m.email2_bounced, iCal1, iCal2, default_activity_id, " +
                 "cn.club_num, ca.assoc_num, ntrp_rating, usta_num, mobile_user, mobile_pass " +
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
         emailOpt = rs.getInt("emailOpt");
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
         
      }

   } catch (Exception exc) {             // SQL Error

      out.println(SystemUtils.HeadTitle("DB Access Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H2>Database Access Error 2</H2>");
      out.println("<BR><BR>Unable to process database change at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }
   
   
   if (mobile_user == null) mobile_user = "";         // change to empty string if not provided
   
   if (mobile_pass == null) mobile_pass = "";         
   

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
          out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
          out.close();
          return;
      }

      i = 0;
   }

   //
   //  output a html page to prompt user for changes - wait for doPost entry
   //
   out.println(SystemUtils.HeadTitle2("Member Services"));
      out.println("<script type=\"text/javascript\">");
      out.println("<!-- ");
      if (caller.equals( "none" ) || caller.startsWith("foretees")) out.println("function cursor() { document.forms['f'].oldpw.focus(); }");
      out.println("function resetBounceFlags() {" +
              " var frm = document.forms['f'];" +
              " frm.resetBounceFlags.value = '1';" +
              " frm.submit();" +
              "}");
      
      out.println("// -->");
      out.println("</script>");
   out.println("</head>");

   out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

      out.println("<form action=\"/" +rev+ "/servlet/Member_services\" method=\"post\" target=\"bot\" name=\"f\" id=\"f\">");
         out.println("<input type=\"hidden\" name=\"resetBounceFlags\" value=\"0\">");

         out.println("<table align=\"center\" border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"10\"><tr>");
         out.println("<td align=\"center\" bgcolor=\"#336633\"><font color=\"ffffff\" size=\"2\">");

         out.println("Use this page to change your personal settings.");
         out.println("<br>Only change the fields you want to update.");
         out.println("<br>Click on 'Submit' to process the change.</font></td>");
         out.println("</tr>");

         //
         //  Password
         //
         if (caller.equals( "none" ) || caller.startsWith("foretees")) {   // if user did not come from MemFirst or other web site
            out.println("<tr><td>");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<p>&nbsp;&nbsp;&nbsp;Current Password:&nbsp;&nbsp;");
            out.println("<input type=\"password\" name=\"oldpw\" id=\"oldpw\" size=\"15\" maxlength=\"15\">");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;Only enter password information if you wish to change it.<p>");
            out.println("<p>&nbsp;&nbsp;&nbsp;New Password:&nbsp;&nbsp;<input type=\"password\" name=\"newpw\" size=\"15\" maxlength=\"15\">");
            out.println("&nbsp;&nbsp;&nbsp;(4 - 15 characters)</p>");
            out.println("<p>&nbsp;&nbsp;&nbsp;Confirm New Password:&nbsp;&nbsp;<input type=\"password\" name=\"newpwc\" size=\"15\" maxlength=\"15\"></p>");
            out.println("</font></td></tr>");
         }

         //
         //  Email addresses
         //
         out.println("<tr><td><font size=\"2\">");

         if (rsync == 1 && !club.equals("dorsetfc") && !club.equals("mesaverdecc") &&
             !caller.equals("FLEXSCAPE4865") && !club.equals("bellevuecc") && !club.equals("greenacrescountryclub")) {     // if Roster Sync - no email address changes

            out.println("<p>&nbsp;&nbsp;&nbsp;Email Addresses:&nbsp;&nbsp;");
            
            if (email.equals("") && email2.equals("")) {
                
                out.println("<i>None</i>");
            } else {
                
                out.println("Primary = " +
                        "" + ((email_bounced!=0) ? "<font color=red>" : "") + "<i>" +email+ "</i>" + ((email_bounced!=0) ? " *</font>" : ""));
                out.println("&nbsp;&nbsp;Secondary = " +
                        "" + ((email2_bounced!=0) ? "<font color=red>" : "") + "<i>" +email2+ "</i>" + ((email2_bounced!=0) ? " *</font>" : ""));
                if (email_bounced != 0 || email2_bounced != 0) 
                    out.println("<br><br>&nbsp;&nbsp;&nbsp;<b>Note:</b> The address above in red with an * after it has recently failed " +
                                "and needs to be corrected<br>&nbsp;&nbsp;&nbsp;before we attempt to send you further emails.  If the email address " +
                                "is correct, then <a href=\"javascript:resetBounceFlags()\">click here</a> to reset your" +
                                "<br>&nbsp;&nbsp;&nbsp;email bounce indicator.</i>");
            }
            
            out.println("<br><br>&nbsp;&nbsp;&nbsp;To add or change your email addresses, please refer to your club's web site.");
            out.println("<br>&nbsp;&nbsp;&nbsp;ForeTees will use the email addresses from your account on that site.");
            
            out.println("</p>");
            out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
            out.println("<input type=\"hidden\" name=\"old_email\" value=\"" + email + "\">");
            out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
            out.println("<input type=\"hidden\" name=\"old_email2\" value=\"" + email2 + "\">");

         } else {
             
            out.println("<p>&nbsp;&nbsp;&nbsp;Email Addresses:&nbsp;&nbsp;Primary&nbsp;");
            out.println("<input type=\"text\" name=\"email\" size=\"28\" maxlength=\"50\" value=\"" + email +"\"" + ((email_bounced!=0) ? " style=\"background-color: red\"" : "") + ">");
            out.println("<input type=\"hidden\" name=\"old_email\" value=\"" + email + "\">");
            out.println("&nbsp;&nbsp;Secondary&nbsp;");
            out.println("<input type=\"text\" name=\"email2\" size=\"28\" maxlength=\"50\" value=\"" + email2 +"\"" + ((email2_bounced!=0) ? " style=\"background-color: red\"" : "") + ">");
            out.println("<input type=\"hidden\" name=\"old_email2\" value=\"" + email2 + "\">");
            out.println("</p>");
            if (email_bounced != 0 || email2_bounced != 0) 
                out.println("&nbsp;&nbsp;&nbsp;<b>Important!</b> &nbsp;<i>The address above in red has recently failed and needs to be " +
                            "corrected before we attempt<br>&nbsp;&nbsp;&nbsp;to use it to send you further communications.  If the email address " +
                        "is correct, then <a href=\"javascript:resetBounceFlags()\">click here</a> to reset your" +
                        "<br>&nbsp;&nbsp;&nbsp;email bounce indicator.</i>");
            
         }
         /*
         if (email.equals("") && email2.equals("")) {
             
             out.println("<input type=hidden name=emailOpt value=No>");
             out.println("<input type=hidden name=iCal1 value=0>");
             out.println("<input type=hidden name=iCal2 value=0>");

         } else {*/
             
             out.println("<p>&nbsp;&nbsp;&nbsp;Do you wish to receive email alerts ");
             if (club.equals( "interlachenspa" )) {
                out.println("of your schedule changes?&nbsp;&nbsp;");    // Spa users
             } else {
                out.println("of your " + timesText + "?&nbsp;&nbsp;");
             }
             out.println("<select size=\"1\" name=\"emailOpt\">");
              out.println("<option" + ((emailOpt == 1) ? " selected" : "") + ">Yes</option>");
              out.println("<option" + ((emailOpt != 1) ? " selected" : "") + ">No</option>");
             out.println("</select></p>");

             out.println("&nbsp;&nbsp;&nbsp;Do you wish to receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> on your Primary Email? ");
             out.println("<select size=\"1\" name=\"iCal1\">");
              out.println("<option value=1" + ((iCal1 == 1) ? " selected" : "") + ">Yes</option>");
              out.println("<option value=0" + ((iCal1 != 1) ? " selected" : "") + ">No</option>");
             out.println("</select>");

             out.println("<br>&nbsp;&nbsp;&nbsp;Do you wish to receive <a href=\"/"+rev+"/member_help_icalendar.htm\" target=_memberHelp>iCal attachments</a> on your Secondary Email? ");
             out.println("<select size=\"1\" name=\"iCal2\">");
              out.println("<option value=1" + ((iCal2 == 1) ? " selected" : "") + ">Yes</option>");
              out.println("<option value=0" + ((iCal2 != 1) ? " selected" : "") + ">No</option>");
             out.println("</select>");

         //}
         out.println("</font></td></tr>");

         // 
         //   Phone Numbers
         //
         out.println("<tr><td><font size=\"2\">");
         out.println("<p>&nbsp;&nbsp;&nbsp;Phone Numbers:&nbsp;&nbsp;Primary&nbsp;");
         out.println("<input type=\"text\" name=\"phone1\" size=\"14\" maxlength=\"24\" value=\"" +phone1+"\">");
           
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;Alternate&nbsp;");
         out.println("<input type=\"text\" name=\"phone2\" size=\"14\" maxlength=\"24\" value=\"" +phone2+"\">");
         out.println("</p></font></td></tr>");

         
         //
         //   Default Activity
         //
         if ( activity_system == true ) {

            out.println("<tr><td><font size=\"2\">");
            out.println("<p>&nbsp;&nbsp;&nbsp;Default Activity:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"default_activity_id\">");

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
            out.println("</font></td></tr>");

         }

         if (foretees_mode != 0) {            // if foretees golf in system

            if ( activity_system == true ) {  // AND Activities - add row for group title

               out.println("<td align=\"center\" bgcolor=\"#336633\"><font color=\"ffffff\" size=\"2\">");
               out.println("Golf Options</font></td></tr>");
            }
         

            //
            //   Mode of Trans
            //
            if (!club.equals( "blackhawk" ) && !club.equals( "marbellacc" ) && !club.equals( "interlachenspa" ) && !club.equals( "valleyclub" ) && foretees_mode != 0) {  // skip if Blackhawk CC or Marbella

               out.println("<tr><td><font size=\"2\">");
               out.println("<p>&nbsp;&nbsp;&nbsp;Default Mode of Transportation Preference:&nbsp;&nbsp;");
                 out.println("<select size=\"1\" name=\"walk_cart\">");

                 for (i=0; i<16; i++) {        // get all c/w options

                    if (!parmc.tmodea[i].equals( "" )) {
                       if (wca.equals( parmc.tmodea[i] )) {
                          out.println("<option selected value=" + wca + ">" + wc + " ("+wca+")</option>");
                       } else {
                          out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmode[i]+ " ("+parmc.tmodea[i]+")</option>");
                       }
                    }
                 }
                 out.println("</select></p>");
               out.println("</font></td></tr>");
            }


            //
            //  GHIN info
            //
            if (!club.equals( "oldoaks" ) && !club.equals( "interlachenspa" ) && foretees_mode != 0) {  // skip if Old Oaks or Spa

               boolean tmp_allow = true;
               String tmp_hdcp = "";

               if (hdcpSystem.equalsIgnoreCase("ghin")) tmp_allow = false;

               out.println("<tr><td>");
                  out.println("<font size=\"2\">");
                  out.println("<p>&nbsp;&nbsp;&nbsp;Handicaps:");

                  if (!hdcpSystem.equalsIgnoreCase( "ghin" )) {

                      out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Course&nbsp;&nbsp;");

                      if (c_hancap > 0) {
                          tmp_hdcp = "+" + c_hancap;
                      } else {
                          if (c_hancap <= 0) {
                              c_hancap = 0 - c_hancap;                       // convert to non-negative
                          }
                          tmp_hdcp = "" + c_hancap;
                      }

                      if (hdcpSystem.equalsIgnoreCase( "Other" )) {
                          out.println("<input type=\"text\" name=\"c_hancap\" value=\"" + tmp_hdcp + "\" size=\"6\" maxlength=\"6\">");
                      } else {
                          out.println("<b>" + tmp_hdcp + "</b>");
                      }
                  }
                  out.println(" &nbsp;&nbsp;&nbsp;&nbsp;USGA&nbsp;&nbsp;");
                  tmp_hdcp = "";
                  if (g_hancap > 0) {
                      tmp_hdcp = "+" + g_hancap;
                  } else {
                      if (g_hancap <= 0) {
                          g_hancap = 0 - g_hancap;                       // convert to non-negative
                      }
                      tmp_hdcp = "" + g_hancap;
                  }

                  if (hdcpSystem.equalsIgnoreCase( "Other" )) {
                      out.println("<input type=\"text\" name=\"g_hancap\" value=\"" + tmp_hdcp + "\" size=\"6\" maxlength=\"6\">");
                  } else {
                      out.println("<b>" + tmp_hdcp + "</b>");
                  }

                  /*
                  if (g_hancap == -99) {
                     out.println("<input type=\"text\" name=\"g_hancap\" size=\"6\" maxlength=\"6\" " + tmp_allow + ">");
                  } else {
                     if (g_hancap > 0) {
                        out.println("<input type=\"text\" name=\"g_hancap\" value=\"+" + g_hancap + "\" size=\"6\" maxlength=\"6\" " + tmp_allow + ">");
                     } else {
                        if (g_hancap <= 0) {
                           g_hancap = 0 - g_hancap;                       // convert to non-negative
                        }
                        out.println("<input type=\"text\" name=\"g_hancap\" value=\"" + g_hancap + "\" size=\"6\" maxlength=\"6\" " + tmp_allow + ">");
                     }
                  }
                  */

                  if (hdcpSystem.equalsIgnoreCase( "ghin" )) {

                      out.println("&nbsp; &nbsp; &nbsp; &nbsp;<a href=\"/" + rev + "/servlet/Member_handicaps?todo=view\">Click here to see your course handicap.</a><br>");
                  }

                  if (!ghin.equals( "" )) {            // if ghin # provided, display it (do not allow them to change it)

                     out.println("<br>&nbsp;&nbsp;&nbsp;Your Handicap # is: <b>" +ghin+ "</b>");
                     out.println("<br><br>&nbsp;&nbsp;&nbsp;Assigned Club Number: <b>" + club_num + "</b>");
                     out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Assigned Club Association Number: <b>" + club_assoc + "</b>");
                  }

                  out.println("</p>");
               out.println("</font></td>");
               out.println("</tr>");
            }    // end of IF Old Oaks or Spa

            
            if (IS_TLT == false && allowMobile == true && sess_activity_id == 0) {      // DO NOT do this for Notification systems
            
               //
               //   Mobile settings
               //
               out.println("<td align=\"center\" bgcolor=\"#336633\"><font color=\"ffffff\" size=\"2\">");
               out.println("Mobile Credentials (For use when logging in from a Mobile Device)</font></td></tr>");

               out.println("<tr><td>");
               out.println("<font size=\"2\">");
               out.println("<p>&nbsp;&nbsp;&nbsp;Mobile Username:&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"mobile_user\" size=\"15\" maxlength=\"15\" value=\"" + mobile_user +"\">");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;(It is ok to use the same credentials that you use to login from your PC.)</p>");

               if (!mobile_pass.equals( "" )) {        // if mobile password already exists - they must enter it in order to change it
                  out.println("<p>&nbsp;&nbsp;&nbsp;Current Mobile Password:&nbsp;&nbsp;");
                  out.println("<input type=\"password\" name=\"oldmobilepw\" id=\"oldmobilepw\" size=\"15\" maxlength=\"15\">");
                  out.println("&nbsp;&nbsp;&nbsp;&nbsp;Only enter password information if you wish to change it.<p>");
               }
               out.println("<p>&nbsp;&nbsp;&nbsp;New Mobile Password:&nbsp;&nbsp;<input type=\"password\" name=\"newmobilepw\" size=\"15\" maxlength=\"15\">");
               out.println("&nbsp;&nbsp;&nbsp;(4 - 15 characters)</p>");
               out.println("<p>&nbsp;&nbsp;&nbsp;Confirm New Mobile Password:&nbsp;&nbsp;<input type=\"password\" name=\"newmobilepwc\" size=\"15\" maxlength=\"15\">");            
               if (!mobile_pass.equals( "" )) {                                                                      // if mobile password already exists
                  if ((!email.equals("") && email_bounced == 0) || (!email2.equals("") && email2_bounced == 0)) {    //  and email address ok  
                     out.println("&nbsp;&nbsp;&nbsp;<a href=\"/" +rev+ "/servlet/Login?help=yes&mobile=yes&user_name=" +user+ "&clubname=" +club+ "\">Click Here</a> to have your Mobile password emailed to you.");
                  }
               }
               out.println("</p></font></td></tr>");
            }

         }       // end of IF golf
         
         
         //
         //  Add Tennis options - ******************* CHANGE THIS LATER ****************************
         //
        // if ( activity_system == true ) {  // Activities - add row for group title
         if ( club.startsWith("admiralscove") ) {  // Activities - add row for group title

            out.println("<td align=\"center\" bgcolor=\"#336633\"><font color=\"ffffff\" size=\"2\">");
            out.println("Tennis Options</font></td></tr>");
          
         
            //
            //   Tennis values - USTA Number and NTRP Rating
            //
            out.println("<tr><td><font size=\"2\">");
            out.println("<p>&nbsp;&nbsp;&nbsp;USTA Number&nbsp;");
            out.println("<input type=\"text\" name=\"usta\" size=\"12\" maxlength=\"16\" value=\"" +usta+"\">");

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;NTRP Rating&nbsp;");
            out.println("<input type=\"text\" name=\"ntrp\" size=\"8\" maxlength=\"12\" value=\"" +ntrp+"\">");
            out.println("</p></font></td></tr>");
         }    // end of IF activity system
         

      out.println("</table>");                  // end of main page table

      out.println("<input type=\"hidden\" name=\"old_wc\" value=\"" + owc + "\">");
      out.println("<br>");
      out.println("<input type=\"submit\" value=\"Submit\">");

      out.println("</form>");

   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline;\">");
   out.println("</input></form></font>");

   out.println("</center></font></body></html>");
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

   con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact your golf shop staff.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   int count = 0;
   int emailOpt = 0;
   int iCal1 = 0;
   int iCal2 = 0;
   int length = 0;
   int mobile = 0;                // mobile user

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");      // get club name
   String caller = (String)session.getAttribute("caller");   // get caller's name
     
   
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
   
   
   
   //
   //  Normal user
   //
   String wc = "";
   String old_wc = "";
   String scourse = "";       // course hndcp
   String susga = "";         // usga hndcp
   String usta = "";
   String temp = "";

   float course = -99;     // default hndcp's
   float usga = -99;
   float ntrp = 0;


   //
   // Get all parameters entered
   //
   String email = req.getParameter("email");
   String email2 = req.getParameter("email2");
   String old_email = req.getParameter("old_email");
   String old_email2 = req.getParameter("old_email2");
   String semailOpt = req.getParameter("emailOpt");      // email option
   String phone1 = req.getParameter("phone1");
   String phone2 = req.getParameter("phone2");
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

   if (req.getParameter("iCal1") != null) {

       iCal1 = (req.getParameter("iCal1").equals("1")) ? 1 : 0;
   }
   if (req.getParameter("iCal2") != null) {

       iCal2 = (req.getParameter("iCal2").equals("1")) ? 1 : 0;
   }

   if (req.getParameter("usta") != null) {    

      usta = req.getParameter("usta");       // Tennis - USTA Number
   }
   if (req.getParameter("ntrp") != null) {

      temp = req.getParameter("ntrp");         // Tennis - NTRP Rating
   }

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

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error</H2>");
            out.println("<BR><BR>The data you entered is incorrect.");
            out.println("<BR>The handicap must contain a numeric value.");
            out.println("<BR>The '+' or '-' must be followed be a number value.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      try {

         course = Float.parseFloat(scourse);               // course handicap

      }
      catch (Exception exc) {             // SQL Error

            out.println(SystemUtils.HeadTitle("DB Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error 2</H2>");
            out.println("<BR><BR>Handicap entry is invalid.");
            out.println("<BR>The handicap must contain a numeric value.");
            out.println("<BR>The '+' or '-' must be followed be a number value.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
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

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error</H2>");
            out.println("<BR><BR>The data you entered is incorrect.");
            out.println("<BR>The handicap must contain a numeric value.");
            out.println("<BR>The '+' or '-' must be followed be a number value.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      try {

         usga = Float.parseFloat(susga);                   // usga handicap

      }
      catch (Exception exc) {             // SQL Error

            out.println(SystemUtils.HeadTitle("DB Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error 2</H2>");
            out.println("<BR><BR>Handicap entry is invalid.");
            out.println("<BR>The handicap must contain a numeric value.");
            out.println("<BR>The '+' or '-' must be followed be a number value.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
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

            out.println(SystemUtils.HeadTitle("DB Access Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error 3</H2>");
            out.println("<BR><BR>NTRP Rating entry is invalid.");
            out.println("<BR>The NTRP Rating must contain a numeric value.");
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
      }
   }

   if (semailOpt.equals( "Yes" )) {

      emailOpt = 1;

   } else {

      emailOpt = 0;
   }

   if (!club.startsWith( "demov" )) {        // do not change emailOpt if demo site

      try {

         PreparedStatement stmte = con.prepareStatement (
               "UPDATE member2b SET emailOpt = ? WHERE username = ?");

         stmte.clearParameters();           // clear the parms
         stmte.setInt(1, emailOpt);
         stmte.setString(2, user);          // put username in statement
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
            out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
      }
   }      // end of if demo site


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
         out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
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
                  out.println("<a href=\"/" +rev+ "/servlet/Member_announce\">Return</a>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
            }
        // }      // end of if demo site

      } else {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Data Entry Error</H2>");
         out.println("<BR><BR>The data you entered is incorrect.");
         out.println("<BR>The Current Password must equal the Password that you logged in with.");
         out.println("<BR>The New Password must be at least 4 characters but no more than 15 characters.");
         out.println("<BR>Avoid special characters as some are not accepted.");
         out.println("<BR><BR>Please try again.");
         out.println("<BR>If problem persists, please contact your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }
   }

   if (resetBounceFlags || !email.equalsIgnoreCase( old_email ) || !email2.equalsIgnoreCase( old_email2 )) {  // if either email address has changed

      if (!email.equals( "" )) {      // if specified

         email = email.trim();              // remove any spaces
         FeedBack feedback = (member.isEmailValid(email));
           
         if (!feedback.isPositive()) {    // if error

            String emailError = feedback.get(0);             // get error message 

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error</H2>");
            out.println("<BR><BR>The primary email address you entered is invalid.");
            out.println("<BR>" +emailError);
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }

      if (!email2.equals( "" )) {      // if specified

         email2 = email2.trim();            // remove any spaces
         FeedBack feedback = (member.isEmailValid(email2));

         if (!feedback.isPositive()) {    // if error

            String emailError = feedback.get(0);             // get error message

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H2>Data Entry Error</H2>");
            out.println("<BR><BR>The secondary email address you entered is invalid.");
            out.println("<BR>" +emailError);
            out.println("<BR><BR>Please try again.");
            out.println("<BR>If problem persists, please contact your golf shop staff.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
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
         out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
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

   } // end of if either email address has changed


   //
   // Update iCal fields and default_activity_id  for this member record
   //
   if (  email.equals("") ) iCal1 = 0;
   if ( email2.equals("") ) iCal2 = 0;
   
   PreparedStatement pstmt = null;

   try {

       pstmt = con.prepareStatement( "UPDATE member2b SET iCal1 = ?, iCal2 = ?, default_activity_id = ? WHERE username = ?" );
       pstmt.clearParameters();
       pstmt.setInt(1, iCal1);
       pstmt.setInt(2, iCal2);
       pstmt.setInt(3, default_activity_id);
       pstmt.setString(4, user);

       pstmt.executeUpdate();

   } catch (Exception exc) {

       out.println(SystemUtils.HeadTitle("DB Access Error"));
       out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
       out.println("<BR><BR><H2>Database Access Error</H2>");
       out.println("<BR><BR>We encountered an error while attempting to update your iCalendar preferences.");
       out.println("<BR>Please try again later. (" + exc.toString() + ")");
       out.println("<BR><BR>If problem persists, contact your club administrator.");
       out.println("<BR><BR>");
       out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
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
         out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }
   }

   if ((!phone1.equals("")) || (!phone2.equals(""))) {   // if a phone # specified

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
         out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
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
         out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
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
               out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
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
         out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
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
                  out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
            }

      } else {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
         out.println("<BR><BR><H2>Data Entry Error</H2>");
         out.println("<BR><BR>The data you entered is incorrect.");
         out.println("<BR>The Current Mobile Password must equal the password you currently use for the mobile site.");
         out.println("<BR>The New Mobile Password must be at least 4 characters but no more than 15 characters.");
         out.println("<BR>Avoid special characters as some are not accepted.");
         out.println("<BR><BR>Please try again.");
         out.println("<BR>If problem persists, please contact your golf shop staff.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
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
         out.println("<a href=\"/" +rev+ "/servlet/Member_services\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
   
    
   

   out.println(SystemUtils.HeadTitle("Member Settings Changed"));
   out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
   out.println("<BR><BR><H3>Your Personal Settings Have Been Changed</H3>");

   /*
   if (club.startsWith( "demov" )) {        // do not change pw if demo site

      out.println("<BR>Thank you.  For demo purposes the password has not been changed.");
      out.println("<BR><BR>");
   }
    */

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
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_services\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline;\">");
   out.println("</form>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   //
   // Done - return.......
   //
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
      
   out.println("<form action=\"/" +rev+ "/servlet/Member_services\" method=\"post\" name=\"f\" id=\"f\">");
   out.println("<input type=\"hidden\" name=\"domobile\" value=\"yes\">");

   out.println("<div class=\"content\"><BR>");
   out.println("<table cellpadding=\"0\" cellspacing=\"1\" border=\"1\" id=\"settings\"><tr class=\"tableheader\">");
   out.println("<td><strong>");

   out.println("Use this page to change your mobile password.");
   out.println("&nbsp;&nbsp;Click on 'Submit' to process the change.</td>");
   out.println("</tr>");

   out.println("<tr class=\"tablerow\"><td>");
   out.println("<p>Current Mobile Password:&nbsp;&nbsp;");
   out.println("<input type=\"password\" name=\"oldpw\" id=\"oldpw\" size=\"15\" maxlength=\"15\">");
   out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;New Mobile Password:&nbsp;&nbsp;<input type=\"password\" name=\"newpw\" size=\"15\" maxlength=\"15\">");
   out.println("<BR>(4 - 15 characters)</p>");
   out.println("<p>Confirm New Mobile Password:&nbsp;&nbsp;<input type=\"password\" name=\"newpwc\" size=\"15\" maxlength=\"15\"></p>");
   out.println("</td></tr>");
   out.println("</table></div>");

   out.println("<div class=\"content\"><ul>");      
   out.println("<li><input type=\"submit\" value=\"Submit Changes\">");
   out.println("</form></li>");
   out.println("<li>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/mobile/member_mobile_home.html\">");
   out.println("<input type=\"submit\" value=\"Cancel - Do Not Change\" style=\"text-decoration:underline;\">");
   out.println("</input></form></li></ul></div>");
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


}
