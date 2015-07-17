/***************************************************************************************     
 *   Proshop_club:  This servlet will process the 'club setup' request from Proshop's
 *                  System Config page.
 *
 *
 *   called by:  proshop menu (doGet) and Proshop_club (via doPost in HTML built here)
 *
 *   created: 12/05/2002   Bob P.
 *
 *
 *   last updated:
 *
 *        6/10/10   Update link for Proshop_club_guestdb to increase the hight of the opened window.
 *        5/14/10   Updated to allow Kinsale to access the Guest Tracking system
 *        4/18/10   Updated to allow TPC clubs and Desert Forest to access the Guest Tracking system
 *        4/17/10   Changes to step2 & post methods to allow for unlimited guest types
 *        2/25/10   Added various "?" help links that were missing.
 *                  Add Mobile Access option to enable/disable member mobile access.
 *                  Add Website Url field to hold club website address (gets applied to link on ForeTees login page.
 *                  Add Guest Tracking option/config link (only viewable on demo sites).
 *       12/01/09   Add Suadi Arabia time zone for Rolling Hills (in Dhahran, Saudi Arabia).
 *       11/04/09   Change 'Rounds' to 'Reservations' in the membershhip options panel if Activity user.
 *       10/26/09   Add the sess_id when calling getClub.getParms so we get the correct guest type values.
 *       10/16/09   Replace some golf terms when the pro is logged in under an activity.
 *       10/02/09   Updates to work better with activity_ids.  Clubs with more than 1 activity configured (golf included) are no longer
 *                  able to make changes to the names of membership types (add,remove,modify) from club setup, and must use the new Admin side tool instead!
 *                  All changes other than to the name will be applied to the instance of that mship for this activity only!
 *        9/09/09   Added trimming to guest and mship values to prevent whitespace-only entries
 *        9/04/09   Change processing to grab guest types from guest5 table and mships from mship5 table instead of club5
 *        3/24/09   Add code to sync the club5 gtypes1-36 fields to guest5 table (remove guest5 entries if not in club5)
 *        3/18/09   Fixed problem with unaccompanied guest selection not getting set correctly
 *        2/19/09   Add Club Prophet Systems V3 as an option for support of the new ProShopKeeper Interface.
 *       10/17/08   Allow ClubSoft POS for all clubs.
 *        9/14/08   Removed restrictions on new features
 *        9/03/08   Add ClubSoft POS Interface vendor.
 *        9/02/08   Add POS option for Pay Now (case 1429).    
 *        9/02/08   Javascript compatability updates
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        7/01/08   Changed layout so it uses a table structure instead of freeform &nbsp formatting
 *        5/14/08   Add 'Member Cutoff' options - days and time to cutoff tee time access to members (case 1480).
 *        4/22/08   Add 'Tee Sheet Flag' option for mship types (case 1357).
 *        4/11/08   Add Revenue option for guest types to step 2 (case 1400).
 *       10/25/07   Add new club option max_originations to step 1
 *       10/17/07   Increase xhrs max from 48 to 240 (10 days).
 *        9/10/07   Add Email Password config to Step 1
 *        7/17/07   Add Arizona Time Zone.
 *        7/16/07   Add Hawaiian Time Zone.
 *        6/12/07   Allow all clubs to use GHIN.
 *        6/07/07   Change the way we check to see if the club table entry exists in step 1.
 *                  This is to allow for the entry being built in Support_cluboptions.
 *        5/01/07   Add 'days in adv mship can view tee sheets' parm to mship options.
 *                  Also, add 'defaults' page for mship options.
 *        3/19/07   Added allow members to post option
 *        3/13/07   Added hdcp setup window link when GHIN is selected
 *        1/23/07   Enhancements for TLT version - Added notify interval and hours allowed
 *        9/23/06   Enhancements for TLT version - Add SystemLingo support
 *        8/25/06   Add IBS, TAI and ClubSoft to list of POS systems.
 *        8/25/06   Increase the max allowed number of rounds per day to allow for clubs that
 *                  allow members to reserve multiple consecutive tee times in their name (for a group). 
 *        7/26/06   Adjusted maxlength of POS related text boxes
 *        7/06/06   Added Pace of Play option.
 *        4/11/06   Add CSG to list of POS systems.
 *        9/14/05   Add Abacus21 to list of POS systems.
 *        7/14/05   Add NorthStar to list of POS systems.
 *        3/01/05   Ver 5 - add pre-checkin visual notification option to club parm [Paul S]
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/05/05   Ver 5 - add Member & Proshop options for Consecutive Tee Times.
 *       12/17/04   Ver 5 - add div tags around selects for menus to work properly.
 *        9/30/04   Change the Jonas POS Options to reflect the new Jonas I/F.
 *                  Charges & Sales Tax values no longer needed.
 *        9/20/04   Ver 5 - change getClub from SystemUtils to common.
 *        9/09/04   Restructure the club parms for V5.
 *        2/05/04   Add 'Unacompanied Guests' and 'Display Handicaps' Options.
 *        2/04/04   Add support for POS systems (Jonas and Pro-ShopKeeper).
 *       12/04/03   Add support for up to 36 guest types.
 *       11/18/03   Add hotel support (Hotel=Yes/No)
 *        7/18/03   Enhancements for Version 3 of the software.
 *                  Add lottery support (Lottery=Yes/No)
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
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.getActivity;
import com.foretees.client.SystemLingo;


public class Proshop_club extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //**********************************************************
 //
 // Process the initial request from Proshop menu
 //
 //   parms passed:  none the first time called (for doGet)
 //
 //                  step2 = yes  (for Guest Types)
 //                  step3 = yes  (for Member Types)
 //                  step4 = yes  (for MShip Types)
 //                  step5 = yes  (for MShip Options)
 //
 //**********************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
   }
              
   String club = (String)session.getAttribute("club");
    
   //
   // See what activity mode we are in
   //
   int sess_activity_id = 0;

   try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
   catch (Exception ignore) { }

   //
   //  See if we are in teh timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
    
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   try {

      getClub.getParms(con, parm, sess_activity_id);          //  get the current club parms

   }
   catch (Exception e) {
      // ignore error
   }

   
/*
   // ********* temp **********

   Enumeration enum = req.getParameterNames();
     
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Proshop_club Parameters</H1>");

      out.println("<BR><BR>Query String: ");
      out.println(req.getQueryString());
      out.println();

      out.println("<BR><BR>Request Parms: ");

   while (enum.hasMoreElements()) {
     
      String name = (String) enum.nextElement();
      String values[] = req.getParameterValues(name);
      if (values != null) {
         for (int i=0; i<values.length; i++) {

            out.println("<BR><BR>" +name+ " (" +i+ "): " +values[i]);
         }
      }
   }

      out.println("<BR><BR>");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");

   // ********* temp **********
*/
  
   //
   //  Continue based on which step we are on.  These parms are broken into 4 pages to simplify the
   //  process.
   //
   if (req.getParameter("step2") != null) {

      doStep2(req, out, session, parm, con);             // process Guest Types
      return;
   }

   if (req.getParameter("step3") != null) {

      doStep3(req, out, session, parm, con);             // process Member Types
      return;
   }

   if (req.getParameter("step4") != null) {

      doStep4(req, out, session, parm, con);             // process Membership Types
      return;
   }

   if (req.getParameter("step5") != null) {

      doStep5(req, out, session, parm, con);             // process Membership Options
      return;
   }

   //
   //  Process step 1 - basic Club Options
   //

   if (sess_activity_id != 0) {

       SystemUtils.restrictProshop("", out);

   } else {

        // Define parms
       String clubName = "";        // club name
       String contact = "";         // club contact
       String website_url = "";     // club website url
       String email = "";           // contact's email
       String posType = "";         // POS system type
       String adv_zone = "";        // Time Zone

       int multi = 0;               // multiple course support = no
       int lottery = 0;             // lottery support = no
       int hotel = 0;               // hotel support = no
       int allow_mobile = 0;              // mobile access support = no;
       int dining = 0;              // dining request system = no;
       int guestdb = 0;             // guest tracking support = no
       int x = 0;                   // use 'x' (0 - 4)
       int xhrs = 0;                // # of hrs in advance to remove x's
       int emailOpt = 0;
       int rnds = 0;
       int hrsbtwn = 0;
       int forceg = 0;
       int hiden = 0;
       int constimesm = 0;
       int constimesp = 0;
       int precheckin = 0;
       int paceofplay = 0;
       int max_originations = 0;
       int unacompGuest = 0;
       int hndcpProSheet = 0;
       int hndcpProEvent = 0;
       int hndcpMemSheet = 0;
       int hndcpMemEvent = 0;

       int notify_interval = 0;
       int st_hr = 0;
       int st_min = 0;
       int et_hr = 0;
       int et_min = 0;
       int allowMemPost = 0;
       int hdcpStart_yy = 0;
       int hdcpStart_mm = 0;
       int hdcpStart_dd = 0;
       int hdcpEnd_yy = 0;
       int hdcpEnd_mm = 0;
       int hdcpEnd_dd = 0;

       int cutoffdays = 0;
       int cutofftime = 0;
       int cut_hr = 0;
       int cut_min = 0;
       int pos_paynow = 0;

       String cut_time = "";

       String st_ampm = "";
       String et_ampm = "";
       String hdcpSystem = "";
       String emailPass = "";

       //
       // Get existing parms if they exist
       //
       try {

          stmt = con.createStatement();        // create a statement

          rs = stmt.executeQuery("" +
                  "SELECT *, " +
                      "DATE_FORMAT(hdcpStartDate, '%Y') AS hdcpStart_yy, " +
                      "DATE_FORMAT(hdcpStartDate, '%c') AS hdcpStart_mm, " +
                      "DATE_FORMAT(hdcpStartDate, '%e') AS hdcpStart_dd, " +
                      "DATE_FORMAT(hdcpEndDate, '%Y') AS hdcpEnd_yy, " +
                      "DATE_FORMAT(hdcpEndDate, '%c') AS hdcpEnd_mm, " +
                      "DATE_FORMAT(hdcpEndDate, '%e') AS hdcpEnd_dd, " +
                      "DATE_FORMAT(nwindow_starttime, '%l') AS st_hr, " +
                      "DATE_FORMAT(nwindow_starttime, '%i') AS st_min, " +
                      "DATE_FORMAT(nwindow_starttime, '%p') AS st_ampm, " +
                      "DATE_FORMAT(nwindow_endtime, '%l') AS et_hr, " +
                      "DATE_FORMAT(nwindow_endtime, '%i') AS et_min, " +
                      "DATE_FORMAT(nwindow_endtime, '%p') AS et_ampm " +
                  "FROM club5 WHERE clubName != ''");

          if (rs.next()) {

             clubName = rs.getString("clubName");
             multi = rs.getInt("multi");
             website_url = rs.getString("website_url");
             lottery = rs.getInt("lottery");
             contact = rs.getString("contact");
             email = rs.getString("email");
             x = rs.getInt("x");
             xhrs = rs.getInt("xhrs");
             adv_zone = rs.getString("adv_zone");
             emailOpt = rs.getInt("emailOpt");
             hotel = rs.getInt("hotel");
             allow_mobile = rs.getInt("allow_mobile");
             dining = rs.getInt("dining");
             guestdb = rs.getInt("guestdb");
             unacompGuest = rs.getInt("unacompGuest");
             hndcpProSheet = rs.getInt("hndcpProSheet");
             hndcpProEvent = rs.getInt("hndcpProEvent");
             hndcpMemSheet = rs.getInt("hndcpMemSheet");
             hndcpMemEvent = rs.getInt("hndcpMemEvent");
             posType = rs.getString("posType");
             rnds = rs.getInt("rndsperday");
             hrsbtwn = rs.getInt("hrsbtwn");
             forceg = rs.getInt("forcegnames");
             hiden = rs.getInt("hidenames");
             constimesm = rs.getInt("constimesm");
             constimesp = rs.getInt("constimesp");
             precheckin = rs.getInt("precheckin");
             paceofplay = rs.getInt("paceofplay");
             notify_interval = rs.getInt("notify_interval");
             emailPass = rs.getString("emailPass");
             max_originations = rs.getInt("max_originations");

             if (rs.getString("nwindow_starttime") != null) {
                 st_hr = rs.getInt("st_hr");
                 st_min = rs.getInt("st_min");
                 st_ampm = rs.getString("st_ampm");
             }
             if (rs.getString("nwindow_starttime") != null) {
                 et_hr = rs.getInt("et_hr");
                 et_min = rs.getInt("et_min");
                 et_ampm = rs.getString("et_ampm");
             }

             hdcpSystem = rs.getString("hdcpSystem");
             allowMemPost = rs.getInt("allowMemPost");
             hdcpStart_yy = rs.getInt("hdcpStart_yy");
             hdcpStart_mm = rs.getInt("hdcpStart_mm");
             hdcpStart_dd = rs.getInt("hdcpStart_dd");
             hdcpEnd_yy = rs.getInt("hdcpEnd_yy");
             hdcpEnd_mm = rs.getInt("hdcpEnd_mm");
             hdcpEnd_dd = rs.getInt("hdcpEnd_dd");
             cutoffdays = rs.getInt("cutoffDays");
             cutofftime = rs.getInt("cutoffTime");
             pos_paynow = rs.getInt("pos_paynow");

          }
          stmt.close();
       }
       catch (Exception exc) {

          out.println(SystemUtils.HeadTitle("Database Error"));
          out.println("<BODY><CENTER>");
          out.println("<BR><BR><H1>Database Access Error</H1>");
          out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
          out.println("<BR>Please try again later.");
          out.println("<BR><br>Exception: " + exc.getMessage());
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
          out.println("</CENTER></BODY></HTML>");
          return;
       }

       //
       //  Build the HTML page to solicit new parms
       //
       out.println(SystemUtils.HeadTitle2("Proshop - Club Setup"));
           out.println("<script type=\"text/javascript\">");
           out.println("<!--");
           out.println("function cursor() { document.forms['f'].clubName.focus(); }");
           out.println("// -->");
           out.println("</script>");

           out.println("<script language=\"javascript\" src=\"/" +rev+ "/timeBox-scripts.js\"></script>");

        out.println("</head>");

       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
       out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

       out.println("<center>");

       out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
       out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<b>Club Setup</b><br>");
       out.println("Enter or change the club options below.<br>");
       out.println("<br>Click on the '?' for a description of each item.");
       out.println("<br>Click on 'Continue' to process the changes and go to the next page of options.");
       out.println("<br>Click on 'Done' to process the changes and exit.");
       out.println("<br>Click on 'Cancel' to exit without changes.");
       out.println("</font>");
       out.println("</td></tr></table>");

       out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<br><table border=\"2\" bgcolor=\"#F5F5DC\">");

       //***********************************************************************
       //  This is step 1 - dsiplay the first page of options
       //***********************************************************************
       //
       out.println("<form action=\"/" +rev+ "/servlet/Proshop_club\" method=\"post\" target=\"bot\" name=\"f\">");
       out.println("<input type=\"hidden\" name=\"step1\" value=\"yes\">");

       out.println("<tr>");
          out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_name.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Name of your <b>club</b>:");
             out.println("</font>");

          out.println("</td><td align=\"left\" width=\"260\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<input type=\"text\" name=\"clubName\" value=\"" + clubName + "\" size=\"25\" maxlength=\"30\"></input>");
             out.println("</font>");
          out.println("</td>");
          out.println("</tr><tr>");

          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_contact.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Name of person to <b>contact</b>:");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<input type=\"text\" name=\"contact\" value=\"" + contact + "\" size=\"25\" maxlength=\"40\"></input>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       // Website URL field
       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_website.htm', 'newwindow', 'Height=250, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">?</a>");
       out.println("</td>");
       out.println("<td align=\"right\" style=\"padding-right: 15px\">");
       out.println("<font size=\"2\">");
       out.println("<b>Club Website</b> address:");
       out.println("</font>");
       out.println("</td>");

       out.println("<td align=\"left\" style=\"padding-left: 15px\">");
       out.println("<font size=\"2\">");
       out.println("http://<input type=\"text\" name=\"website_url\" value=\"" + website_url + "\" size=\"25\" maxlength=\"255\">");
       out.println("</font>");
       out.println("</td>");
       out.println("</tr><tr>");

          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_email.htm', 'newwindow', 'Height=370, width=600, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             if (sess_activity_id == 0) {
                out.println("<b>Email address</b> for the Golf Shop:");
                out.println("<br>Do you want to receive tee sheets via email for back-up?");
             } else {
                out.println("<b>Email address</b> for the Pro Shop:");
                out.println("<br>Do you want to receive time sheets via email for back-up?");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<input type=\"text\" name=\"email\" value=\"" + email + "\" size=\"25\" maxlength=\"40\"></input>");
             out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"emailOpt\">");
             if (emailOpt == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
             } else {
               out.println("<option value=\"Yes\">Yes</option>");
             }
             if (emailOpt == 0) {
               out.println("<option selected value=\"No\">No</option>");
             } else {
               out.println("<option value=\"No\">No</option>");
             }
             out.println("</select>&nbsp; &nbsp;(We recommend 'Yes')</div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr><tr>");

       if (sess_activity_id == 0) {
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_course.htm', 'newwindow', 'Height=250, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Does your club have <b>multiple golf courses</b>?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"multi\">");
             if (multi == 0) {
               out.println("<option selected value=\"No\">No</option>");
             } else {
               out.println("<option value=\"No\">No</option>");
             }
             if (multi == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
             } else {
               out.println("<option value=\"Yes\">Yes</option>");
             }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
          out.println("</tr><tr>");
       }

       if (!IS_TLT) {
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_lott.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you wish to support a <b>lottery system</b>?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject3\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"lottery\">");
             if (lottery == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (lottery == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
        out.println("</tr><tr>");
       } else {
            out.println("<div id=\"awmobject3\"></div><input type=hidden name=lottery value=\"No\">");
       }
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_hotel.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you wish to support access from a <b>hotel</b>?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject4\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"hotel\">");
             if (hotel == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (hotel == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<a href=\"/" + rev + "/servlet/Common_mobile_help\" target=\"_blank\">?</a>");
       out.println("</td>");
       out.println("<td align=\"right\" style=\"padding-right:15px\">");
       out.println("<font size=\"2\">");
       out.println("Do you wish to allow members <b>Mobile Access</b> to the system?");
       out.println("</font>");

       out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
       out.println("<div id=\"awmobject4\">");        // allow menus to show over this box
       out.println("<select size=\"1\" name=\"allow_mobile\">");
       if (allow_mobile == 0) {
           out.println("<option selected value=\"No\">No</option>");
           out.println("<option value=\"Yes\">Yes</option>");
       } else {
           out.println("<option value=\"No\">No</option>");
           out.println("<option selected value=\"Yes\">Yes</option>");
       }
       out.println("</select></div>");
       out.println("</font>");
       out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
       out.println("<td align=\"center\">");
       out.println("<a href=\"http://www.foretees.com/newfeatures/Dining%20Request.pdf\" target=\"_blank\">");
       out.println("?</a>");
       out.println("</td><td align=\"right\" style=\"padding-right:15px\">");
       out.println("<font size=\"2\">");
       out.println("Do you wish to use the <b>dining request</b> system?");
       out.println("</font>");

       out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
       out.println("<div id=\"awmobject4\">");        // allow menus to show over this box
       out.println("<select size=\"1\" name=\"dining\">");
       if (dining == 0) {
           out.println("<option selected value=\"No\">No</option>");
           out.println("<option value=\"Yes\">Yes</option>");
       } else {
           out.println("<option value=\"No\">No</option>");
           out.println("<option selected value=\"Yes\">Yes</option>");
       }
       out.println("</select></div>");
       out.println("</font>");
       out.println("</td>");
       out.println("</tr>");

if (club.startsWith("demo") || club.startsWith("tpc") || club.equals("desertforestgolfclub") || club.equals("kinsale") || guestdb == 1) {
       out.println("<tr>");
          out.println("<td>&nbsp;</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you wish to use the <b>Guest Tracking</b> system?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject5\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"guestdb\" onchange=\"showGuestdbLink(this.options[this.selectedIndex].value)\">");
             if (guestdb == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (guestdb == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");

             out.println("<span id=cfgGuestdb style=\"visibility:" + (guestdb == 1 ? "visible" : "hidden") + "\"><a href=\"javascript: void(0)\" onclick=\"window.open ('/" +rev+ "/servlet/Proshop_club_guestdb', 'guestdbCfg', 'height=800, width=600, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no')\">Configure the guest tracking system</a></span>");

             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");
} else {
       out.println("<input type=\"hidden\" name=\"guestdb\" value=\"No\">");
}

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_unacomp.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you allow <b>Unaccompanied Guests</b>?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject5\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"unacomp\">");
             if (unacompGuest == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (unacompGuest == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_force.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
              if (sess_activity_id == 0) {
                out.println("Would you like to force members to specify <b>Guest Names</b> in their " + sysLingo.TEXT_tee_times + "?");
             } else {
                out.println("Would you like to force members to specify <b>Guest Names</b> in their reservations?");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject6\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"forceg\">");
             if (forceg == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (forceg == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_hiden.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             if (sess_activity_id == 0) {
                out.println("Would you like to <b>Hide Member Names</b> on the members' tee sheets?");
             } else {
                out.println("Would you like to <b>Hide Member Names</b> on the members' time sheets?");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject7\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"hiden\">");
             if (hiden == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (hiden == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_consm.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             if (sess_activity_id == 0) {
                out.println("How many <b>Consecutive " + sysLingo.TEXT_Tee_Times + "</b> can one Member select?");
             } else {
                out.println("How many <b>Consecutive Booking Times</b> can one Member select?");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject8\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"constimesm\">");
             if (constimesm < 2) {
                out.println("<option selected value=\"1\">1</option>");
             } else {
                out.println("<option value=\"1\">1</option>");
             }
             if (constimesm == 2) {
                out.println("<option selected value=\"2\">2</option>");
             } else {
                out.println("<option value=\"2\">2</option>");
             }
             if (constimesm == 3) {
                out.println("<option selected value=\"3\">3</option>");
             } else {
                out.println("<option value=\"3\">3</option>");
             }
             if (constimesm == 4) {
                out.println("<option selected value=\"4\">4</option>");
             } else {
                out.println("<option value=\"4\">4</option>");
             }
             if (constimesm == 5) {
                out.println("<option selected value=\"5\">5</option>");
             } else {
                out.println("<option value=\"5\">5</option>");
             }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_consp.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             if (sess_activity_id == 0) {
                out.println("How many <b>Consecutive " + sysLingo.TEXT_Tee_Times + "</b> can the Proshop select?");
             } else {
                out.println("How many <b>Consecutive Booking Times</b> can the Proshop select?");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject9\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"constimesp\">");
             if (constimesp < 2) {
                out.println("<option selected value=\"1\">1</option>");
             } else {
                out.println("<option value=\"1\">1</option>");
             }
             if (constimesp == 2) {
                out.println("<option selected value=\"2\">2</option>");
             } else {
                out.println("<option value=\"2\">2</option>");
             }
             if (constimesp == 3) {
                out.println("<option selected value=\"3\">3</option>");
             } else {
                out.println("<option value=\"3\">3</option>");
             }
             if (constimesp == 4) {
                out.println("<option selected value=\"4\">4</option>");
             } else {
                out.println("<option value=\"4\">4</option>");
             }
             if (constimesp == 5) {
                out.println("<option selected value=\"5\">5</option>");
             } else {
                out.println("<option value=\"5\">5</option>");
             }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_max_originate.htm', 'newwindow', 'Height=280, width=740, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             if (sess_activity_id == 0) {
                out.println("How many " + sysLingo.TEXT_Tee_Times + " can a member <b>originate</b> each day?<br>");
                out.println("(This value must be equal to or greater than the value specified for members Consecutive " + sysLingo.TEXT_tee_times + ")");
             } else {
                out.println("How many Reservations can a member <b>originate</b> each day?<br>");
                out.println("(This value must be equal to or greater than the value specified for members Consecutive Booking Times)");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject10\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"max_originations\">");

             if (max_originations < 1) {
                out.println("<option selected value=\"0\">Disabled</option>");
             } else {
                out.println("<option value=\"0\">Disabled</option>");
             }
             if (max_originations == 1) {
                out.println("<option selected value=\"1\">1</option>");
             } else {
                out.println("<option value=\"1\">1</option>");
             }
             if (max_originations == 2) {
                out.println("<option selected value=\"2\">2</option>");
             } else {
                out.println("<option value=\"2\">2</option>");
             }
             if (max_originations == 3) {
                out.println("<option selected value=\"3\">3</option>");
             } else {
                out.println("<option value=\"3\">3</option>");
             }
             if (max_originations == 4) {
                out.println("<option selected value=\"4\">4</option>");
             } else {
                out.println("<option value=\"4\">4</option>");
             }
             if (max_originations == 5) {
                out.println("<option selected value=\"5\">5</option>");
             } else {
                out.println("<option value=\"5\">5</option>");
             }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_precheck.htm', 'newwindow', 'Height=280, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             if (sess_activity_id == 0) {
                out.println("Would you like to use the <b>Pre-Check In</b> feature for " + sysLingo.TEXT_tee_times + " made on the current day's sheet?<br>" +
                            "Yes means you will have visual representation of " + sysLingo.TEXT_tee_times + " made for the current day during that day.");
             } else {
                out.println("Would you like to use the <b>Pre-Check In</b> feature for reservations made on the current day's sheet?<br>" +
                            "Yes means you will have visual representation of reservations made for the current day during that day.");
             }
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject11\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"precheckin\">");
             if (precheckin == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (precheckin == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       if (sess_activity_id == 0) {  // if golf
          out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_paceofplay.htm', 'newwindow', 'Height=280, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Would you like to use the <b>Pace of Play</b> feature for past and present day's tee sheets?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject12\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"paceofplay\">");
             if (paceofplay == 0) {
               out.println("<option selected value=\"No\">No</option>");
               out.println("<option value=\"Yes\">Yes</option>");
             } else {
               out.println("<option value=\"No\">No</option>");
               out.println("<option selected value=\"Yes\">Yes</option>");
             }
             out.println("</select></div>");
             out.println("</font>");
          out.println("</td>");
          out.println("</tr>");
       }

    //   if (club.startsWith("demo") || club.equals("blackhawk") || club.startsWith("notify") || club.equals("cherryhills") || club.equals("sauconvalleycc") || club.equals("philcricket")) {

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_handicap.htm', 'newwindow', 'Height=320, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Choose the <b>handicap system</b> your club uses:");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\" nowrap><font size=\"2\"><nobr>");
             out.println("<div id=\"awmobject13\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"hdcpSystem\" onchange=\"showHdcpLink(this.options[this.selectedIndex].value)\">");
             if (hdcpSystem.equalsIgnoreCase("ghin")) {
               out.println("<option selected value=\"GHIN\">GHIN</option>");
               out.println("<option value=\"Other\">Other</option>");
             } else {
               out.println("<option value=\"GHIN\">GHIN</option>");
               out.println("<option selected value=\"Other\">Other</option>");
             }
             out.println("</select>");
             out.println("</div>");

             out.println("<span id=cfgHdcp style=\"visibility:" + (hdcpSystem.equalsIgnoreCase("ghin") ? "visible" : "hidden") + "\"><a href=\"javascript: void(0)\" onclick=\"window.open ('/" +rev+ "/servlet/Proshop_club_hdcp', 'hdcpCfg', 'height=690, width=600, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no')\"\">Configure your hdcp system</a></span>");

             out.println("</font>");
          out.println("</nobr></td>");
       out.println("</tr>");


       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_handicap.htm', 'newwindow', 'Height=320, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("If your handicap system allows it, do you want members to be able to <b>post scores</b>:");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\" nowrap><font size=\"2\"><nobr>");
             out.println("<select size=\"1\" name=\"allowMemPost\">");
             if (allowMemPost == 0) {
               out.println("<option selected value=\"0\">No</option>");
               out.println("<option value=\"1\">Yes</option>");
             } else {
               out.println("<option value=\"0\">No</option>");
               out.println("<option selected value=\"1\">Yes</option>");
             }
             out.println("</select>");

             out.println("</font>");
          out.println("</nobr></td>");
       out.println("</tr>");

       /*
       } else {

           // don't give an option to select yet
           out.println("<input type=hidden name=\"hdcpSystem\" value=\"Other\">");
           out.println("<input type=hidden name=\"allowMemPost\" value=\"0\">");
           out.println("<input type=hidden name=\"hdcpStartDate\" value=\"0000-00-00 00:00:00\">");
           out.println("<input type=hidden name=\"hdcpEndtDate\" value=\"0000-00-00 00:00:00\">");

       }// end if show hdcp option
       */

       out.println("<script type=\"text/javascript\">");
       out.println("function showHdcpLink(hdcp) {");
       out.println(" elem = document.getElementById('cfgHdcp');");
       out.println(" if (hdcp=='GHIN') {");
       out.println("  elem.style.visibility='visible';");
       out.println(" } else {");
       out.println("  elem.style.visibility='hidden';");
       out.println(" }");
       out.println("}");
       out.println("function showGuestdbLink(guestdb) {");
       out.println(" elem = document.getElementById('cfgGuestdb');");
       out.println(" if (guestdb == 'Yes') {");
       out.println("  elem.style.visibility='visible';");
       out.println(" } else {");
       out.println("  elem.style.visibility='hidden';");
       out.println(" }");
       out.println("}");
       out.println("</script>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_hndcp.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you want <b>Members' Handicaps</b> displayed on:");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<select size=\"1\" name=\"hndcpPS\">");
             if (hndcpProSheet == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (hndcpProSheet == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select>");
             out.println("&nbsp;&nbsp;Proshop's Tee Sheets?<br>");
             out.println("<select size=\"1\" name=\"hndcpPE\">");
             if (hndcpProEvent == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (hndcpProEvent == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select>");
             out.println("&nbsp;&nbsp;Proshop's Event Signup Lists?<br>");
             out.println("<select size=\"1\" name=\"hndcpMS\">");
             if (hndcpMemSheet == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (hndcpMemSheet == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select>");
             out.println("&nbsp;&nbsp;Member's Tee Sheets?<br>");
             out.println("<select size=\"1\" name=\"hndcpME\">");
             if (hndcpMemEvent == 0) {
               out.println("<option selected value=\"No\">No</option>");
            } else {
               out.println("<option value=\"No\">No</option>");
            }
             if (hndcpMemEvent == 1) {
               out.println("<option selected value=\"Yes\">Yes</option>");
            } else {
               out.println("<option value=\"Yes\">Yes</option>");
            }
             out.println("</select>");
             out.println("&nbsp;&nbsp;Member's Event Signup Lists?<br>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr><tr>");

          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_pos.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("<b>Point of Sale (POS)</b> system used in your golf shop:");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<select size=\"1\" name=\"posType\">");
             if (posType.equals( "" )) {
                out.println("<option selected value=\"\">None</option>");
             } else {
                out.println("<option value=\"\">None</option>");
             }
             if (posType.equals( "Abacus21" )) {
                out.println("<option selected value=\"Abacus21\">Abacus21</option>");
             } else {
                out.println("<option value=\"Abacus21\">Abacus21</option>");
             }

             if (posType.equals( "ClubProphetV3" )) {
                out.println("<option selected value=\"ClubProphetV3\">Club Prophet Systems V3</option>");
             } else {
                out.println("<option value=\"ClubProphetV3\">Club Prophet Systems V3</option>");
             }

             if (posType.equals( "ClubSoft" )) {
                out.println("<option selected value=\"ClubSoft\">ClubSoft</option>");
             } else {
                out.println("<option value=\"ClubSoft\">ClubSoft</option>");
             }

             if (posType.equals( "ClubSystems Group" )) {
                out.println("<option selected value=\"ClubSystems Group\">ClubSystems Group</option>");
             } else {
                out.println("<option value=\"ClubSystems Group\">ClubSystems Group</option>");
             }
             if (posType.equals( "IBS" )) {
                out.println("<option selected value=\"IBS\">IBS</option>");
             } else {
                out.println("<option value=\"IBS\">IBS</option>");
             }
             if (posType.equals( "Jonas" )) {
                out.println("<option selected value=\"Jonas\">Jonas</option>");
             } else {
                out.println("<option value=\"Jonas\">Jonas</option>");
             }
             if (posType.equals( "NorthStar" )) {
                out.println("<option selected value=\"NorthStar\">NorthStar</option>");
             } else {
                out.println("<option value=\"NorthStar\">NorthStar</option>");
             }
             if (posType.equals( "Pro-ShopKeeper" )) {
                out.println("<option selected value=\"Pro-ShopKeeper\">Pro-ShopKeeper</option>");
             } else {
                out.println("<option value=\"Pro-ShopKeeper\">Pro-ShopKeeper</option>");
             }
             if (posType.equals( "TAI Club Management" )) {
                out.println("<option selected value=\"TAI Club Management\">TAI Club Management</option>");
             } else {
                out.println("<option value=\"TAI Club Management\">TAI Club Management</option>");
             }
             out.println("</select>");

             out.println("<br><select size=\"1\" name=\"pos_paynow\">");
             if (pos_paynow == 0) {
                 out.println("<option selected value=\"No\">No</option>");
             } else {
                 out.println("<option value=\"No\">No</option>");
             }
             if (pos_paynow == 1) {
                 out.println("<option selected value=\"Yes\">Yes</option>");
             } else {
                 out.println("<option value=\"Yes\">Yes</option>");
             }
             out.println("</select>");
             out.println("&nbsp;&nbsp;Use the 'Pay Now' feature?");

             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_x.htm', 'newwindow', 'Height=240, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you wish to allow members to reserve player positions using an <b>'X'</b>?");
             out.println("<br>If yes, how many X's can members specify per " + sysLingo.TEXT_tee_time + "?  (0 [zero] = NO):");
             out.println("<br>Also if yes, how many hours in advance should we delete all X's?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("X's&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"x\">");
             if (x == 0) {
               out.println("<option selected value=\"0\">0</option>");
            } else {
               out.println("<option value=\"0\">0</option>");
            }
             if (x == 1) {
               out.println("<option selected value=\"1\">1</option>");
            } else {
               out.println("<option value=\"1\">1</option>");
            }
             if (x == 2) {
               out.println("<option selected value=\"2\">2</option>");
            } else {
               out.println("<option value=\"2\">2</option>");
            }
             if (x == 3) {
               out.println("<option selected value=\"3\">3</option>");
            } else {
               out.println("<option value=\"3\">3</option>");
            }
             if (x == 4) {
               out.println("<option selected value=\"4\">4</option>");
            } else {
               out.println("<option value=\"4\">4</option>");
            }
             out.println("</select>");
             out.println("&nbsp;&nbsp;&nbsp;&nbsp;hrs&nbsp;&nbsp;");
             out.println("<input type=\"text\" name=\"xhrs\" value=\"" + xhrs + "\" size=\"3\" maxlength=\"3\">&nbsp;&nbsp;(1 - 240)");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_rounds.htm', 'newwindow', 'Height=240, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("How many times do you allow members to play per day (be part of a " + sysLingo.TEXT_tee_time + ")?");
             out.println("<br>If more than 1, how many hours do you require between " + sysLingo.TEXT_tee_times + "?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("Times&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"rnds\">");
             if (rnds == 0 || rnds == 1) {
                out.println("<option selected value=\"1\">1</option>");
             } else {
                out.println("<option value=\"1\">1</option>");
             }
             if (rnds == 2) {
                out.println("<option selected value=\"2\">2</option>");
             } else {
                out.println("<option value=\"2\">2</option>");
             }
             if (rnds == 3) {
                out.println("<option selected value=\"3\">3</option>");
             } else {
                out.println("<option value=\"3\">3</option>");
             }
             if (rnds == 4) {
                out.println("<option selected value=\"4\">4</option>");
             } else {
                out.println("<option value=\"4\">4</option>");
             }
             if (rnds == 5) {
                out.println("<option selected value=\"5\">5</option>");
             } else {
                out.println("<option value=\"5\">5</option>");
             }
             if (rnds == 6) {
                out.println("<option selected value=\"6\">6</option>");
             } else {
                out.println("<option value=\"6\">6</option>");
             }
             if (rnds == 7) {
                out.println("<option selected value=\"7\">7</option>");
             } else {
                out.println("<option value=\"7\">7</option>");
             }
             if (rnds == 8) {
                out.println("<option selected value=\"8\">8</option>");
             } else {
                out.println("<option value=\"8\">8</option>");
             }
             if (rnds == 9) {
                out.println("<option selected value=\"9\">9</option>");
             } else {
                out.println("<option value=\"9\">9</option>");
             }
             if (rnds == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             out.println("</select>");
             out.println("&nbsp;&nbsp;&nbsp;&nbsp;hrs&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"hrsbtwn\">");
             if (hrsbtwn == 0) {
                out.println("<option selected value=\"0\">0</option>");
             } else {
                out.println("<option value=\"0\">0</option>");
             }
             if (hrsbtwn == 1) {
                out.println("<option selected value=\"1\">1</option>");
             } else {
                out.println("<option value=\"1\">1</option>");
             }
             if (hrsbtwn == 2) {
                out.println("<option selected value=\"2\">2</option>");
             } else {
                out.println("<option value=\"2\">2</option>");
             }
             if (hrsbtwn == 3) {
                out.println("<option selected value=\"3\">3</option>");
             } else {
                out.println("<option value=\"3\">3</option>");
             }
             if (hrsbtwn == 4) {
                out.println("<option selected value=\"4\">4</option>");
             } else {
                out.println("<option value=\"4\">4</option>");
             }
             if (hrsbtwn == 5) {
                out.println("<option selected value=\"5\">5</option>");
             } else {
                out.println("<option value=\"5\">5</option>");
             }
             if (hrsbtwn == 6) {
                out.println("<option selected value=\"6\">6</option>");
             } else {
                out.println("<option value=\"6\">6</option>");
             }
             if (hrsbtwn == 7) {
                out.println("<option selected value=\"7\">7</option>");
             } else {
                out.println("<option value=\"7\">7</option>");
             }
             if (hrsbtwn == 8) {
                out.println("<option selected value=\"8\">8</option>");
             } else {
                out.println("<option value=\"8\">8</option>");
             }
             if (hrsbtwn == 9) {
                out.println("<option selected value=\"9\">9</option>");
             } else {
                out.println("<option value=\"9\">9</option>");
             }
             if (hrsbtwn == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             out.println("</select>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
         out.println("<td align=\"center\">");
         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_advtime.htm', 'newwindow', 'Height=180, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("?</a>");
         out.println("</td>");

         out.println("<td align=\"right\" style=\"padding-right: 15px\">");
            out.println("<font size=\"2\">&nbsp;&nbsp;");
            out.println("Time zone you are located in:");
            out.println("</font>");

         out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
              out.println("<select size=\"1\" name=\"adv_zone\">");
                if (adv_zone.equals ("Eastern")) {
                   out.println("<option selected value=\"Eastern\">Eastern</option>");
                } else {
                   out.println("<option value=\"Eastern\">Eastern</option>");
                }
                if (adv_zone.equals ("Central")) {
                   out.println("<option selected value=\"Central\">Central</option>");
                } else {
                   out.println("<option value=\"Central\">Central</option>");
                }
                if (adv_zone.equals ("Mountain")) {
                   out.println("<option selected value=\"Mountain\">Mountain</option>");
                } else {
                   out.println("<option value=\"Mountain\">Mountain</option>");
                }
                if (adv_zone.equals ("Pacific")) {
                   out.println("<option selected value=\"Pacific\">Pacific</option>");
                } else {
                   out.println("<option value=\"Pacific\">Pacific</option>");
                }
                if (adv_zone.equals ("Arizona")) {
                   out.println("<option selected value=\"Arizona\">Arizona (no DST)</option>");
                } else {
                   out.println("<option value=\"Arizona\">Arizona (no DST)</option>");
                }
                if (adv_zone.equals ("Hawaiian")) {
                   out.println("<option selected value=\"Hawaiian\">Hawaiian</option>");
                } else {
                   out.println("<option value=\"Hawaiian\">Hawaiian</option>");
                }
              
                if (club.equals( "rollinghillsgc" ) || club.startsWith("demo")) {       // if Saudi Arabia club or demo site  (max 8 chars !!!!!)
        
                   if (adv_zone.equals ("Saudi")) {
                      out.println("<option selected value=\"Saudi\">Saudi</option>");
                   } else {
                      out.println("<option value=\"Saudi\">Saudi</option>");
                   }
                }
              out.println("</select><br>");
         out.println("</font></td>");
       out.println("</tr>");

       if (IS_TLT) {

         out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_notify_interval.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("At what interval would you like notifications to be made at?");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<div id=\"awmobject14\">");        // allow menus to show over this box
             out.println("<select size=\"1\" name=\"notify_interval\">");
             out.println("<option " + ( (notify_interval == 2) ? "selected" : "") + " value=\"2\">2</option>");
             out.println("<option " + ( (notify_interval == 5) ? "selected" : "") + " value=\"5\">5</option>");
             out.println("<option " + ( (notify_interval == 10) ? "selected" : "") + " value=\"10\">10</option>");
             out.println("<option " + ( (notify_interval == 15) ? "selected" : "") + " value=\"15\">15</option>");
             out.println("<option " + ( (notify_interval == 30) ? "selected" : "") + " value=\"30\">30</option>");
             out.println("</select> minutes</div>");
             out.println("</font>");
          out.println("</td>");
       out.println("</tr>");

       out.println("<tr>");
         out.println("<td align=\"center\">");
         out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_nwindow.htm', 'newwindow', 'Height=180, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
         out.println("?</a>");
         out.println("</td>");
         out.println("<td align=\"right\" style=\"padding-right: 15px\">");
            out.println("<font size=\"2\">&nbsp;&nbsp;");
            out.println("Times you wish to allow members to make notifications for:");
            out.println("</font>");
         out.println("</td><td align=\"left\" style=\"padding-left: 15px\" nowrap><font size=\"2\">");

            out.println("<select name=st_hr>");
            for (int tmp_x = 1; tmp_x <= 12; tmp_x++) {
                out.println("<option " +
                        ( (tmp_x == st_hr) ? "selected " : "") +
                        "value=\"" + tmp_x + "\"" +
                        ">" + tmp_x + "</option>");
            }
            out.println("</select>");

            out.println("<select name=st_min>");
            for (int tmp_x = 0; tmp_x < 60; tmp_x += 5) {
                out.println("<option " +
                        ( (tmp_x == st_min) ? "selected " : "") +
                        "value=\"" + SystemUtils.ensureDoubleDigit(tmp_x) + "\">" + SystemUtils.ensureDoubleDigit(tmp_x) + "</option>");
            }
            out.println("</select>");


            out.println("<select name=st_ampm>");
            out.println("<option value=\"AM\">AM</option>");
            out.println("<option " + (st_ampm.equals("PM") ? "selected" : "") + " value=\"PM\">PM</option>");
            out.println("</select> Start Time");

            out.println("<br>&nbsp;&nbsp;");

            out.println("<select name=et_hr>");
            for (int tmp_x = 1; tmp_x <= 12; tmp_x++) {
                out.println("<option " +
                        ( (tmp_x == et_hr) ? "selected " : "") +
                        "value=\"" + tmp_x + "\"" +
                        ">" + tmp_x + "</option>");
            }
            out.println("</select>");

            out.println("<select name=et_min>");
            for (int tmp_x = 0; tmp_x < 60; tmp_x += 5) {
                out.println("<option " +
                        ( (tmp_x == et_min) ? "selected " : "") +
                        "value=\"" + SystemUtils.ensureDoubleDigit(tmp_x) + "\">" + SystemUtils.ensureDoubleDigit(tmp_x) + "</option>");
            }
            out.println("</select>");

            out.println("<select name=et_ampm>");
            out.println("<option value=\"AM\">AM");
            out.println("<option " + (et_ampm.equals("PM") ? "selected" : "") + " value=\"PM\">PM");
            out.println("</select> End Time");


       out.println("</font></td>");
       out.println("</tr>");

       } // end if TLT

       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_email_pass.htm', 'newwindow', 'Height=240, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you wish to password protect the email feature on the Proshop side?");
             out.println("<p align=center>Current Status: ");
             if (emailPass.equals("")) {
                out.print("<font color=red><b>Disabled</b></font>");
             } else {
                out.print("<font color=green><b>Enabled</b></font>");
             }
             out.println("</p>");
             out.println("</font>");

          out.println("</td><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");

          out.println("<nobr><input type=password name=currPass maxlength=16 size=14" + ((emailPass.equals("")) ? " disabled" : "") +">&nbsp; Current Password</nobr><br><br>");
          out.println("<nobr><input type=password name=newPass maxlength=16 size=14>&nbsp; New Password</nobr><br>");
          out.println("<nobr><input type=password name=newPass2 maxlength=16 size=14>&nbsp; Confirm New Password&nbsp;</nobr>");

          out.println("</font></td>");
       out.println("</tr>");


       out.println("<tr>");
          out.println("<td align=\"center\">");
          out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_mem_cutoff.htm', 'newwindow', 'Height=240, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
          out.println("?</a>");
          out.println("</td>");

          out.println("<td align=\"right\" style=\"padding-right: 15px\">");
             out.println("<font size=\"2\">");
             out.println("Do you wish to <b>cut off access to tee times</b> from members for the current day (today)?");
             out.println("</font>");
          out.println("</td><td align=\"left\" style=\"padding-left: 5px\"><font size=\"2\">");
             out.println("<div id=\"awmobject15\">");        // allow menus to show over this box
             out.println("<table><tr><td align=\"left\" style=\"padding-left: 15px\"><font size=\"2\">");
             out.println("<select size=\"1\" name=\"cutoffdays\">");
             if (cutoffdays == 99) {
               out.println("<option selected value=\"99\">No</option>");
               out.println("<option value=\"0\">Yes, Day Of</option>");
               out.println("<option value=\"1\">Yes, Day Before</option>");
             } else {
                if (cutoffdays == 0) {
                  out.println("<option value=\"99\">No</option>");
                  out.println("<option selected value=\"0\">Yes, Day Of</option>");
                  out.println("<option value=\"1\">Yes, Day Before</option>");
                } else {
                  out.println("<option value=\"99\">No</option>");
                  out.println("<option value=\"0\">Yes, Day Of</option>");
                  out.println("<option selected value=\"1\">Yes, Day Before</option>");
                }
             }
             out.println("</select>");
             out.println("&nbsp;&nbsp;&nbsp;Time:&nbsp;");

             if (cutofftime > 0) {

                cut_hr = cutofftime/100;        // get hr value
                cut_min = cutofftime - (cut_hr * 100);
             }

             cut_time = SystemUtils.getSimpleTime(cut_hr, cut_min);

             out.println("<input type=text name=cutofftime onclick=\"TB_setCaretPos(this)\" style=\"height: 22px\" size=9 value=\"" + cut_time + "\">");
             out.println("</td><td align=\"left\" style=\"padding-left: 3px\"><font size=\"2\">");
             out.println("<image src=/" + rev + "/images/up.gif onclick=\"TB_adjustTime(1, TB_box_1)\" width=17 height=8><br>");
             out.println("<image src=/" + rev + "/images/shim.gif height=2 width=1><br>");
             out.println("<image src=/" + rev + "/images/down.gif onclick=\"TB_adjustTime(-1, TB_box_1)\" width=17 height=8>");
             out.println("</td></tr></table>");

          out.println("</font></div></td>");
        out.println("</tr>");

        out.println("</table>");

        out.println("<BR><table border=\"0\">");
        out.println("<tr>");
        out.println("<td align=\"center\" width=\"150\">");
        out.println("<font size=\"1\">");
        out.println("<input type=\"submit\" value=\"Done\" name=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<br>(submit changes & exit)");
        out.println("</font></td>");
        out.println("<td align=\"center\" width=\"150\">");
        out.println("<font size=\"1\">");
        out.println("<input type=\"submit\" value=\"Continue\" name=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<br>(submit changes & next page)");
        out.println("</font></td></form>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td align=\"center\" width=\"150\">");
        out.println("<font size=\"1\">");
        out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<br>(no changes & exit)");
        out.println("</font></td></form></tr></table>");
        out.println("</center></font></body></html>");

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("var is_gecko = /gecko/i.test(navigator.userAgent);");
        out.println("var is_ie    = /MSIE/.test(navigator.userAgent);");
        out.println("var TB_box_1 = document.forms['f'].cutofftime;");
        out.println("var TB_caret_pos = 0;");
        out.println("// -->");
        out.println("</script>");
   }

 }  // end of doGet

 //
 //****************************************************************
 //
 // Process the form request from Proshop_club page displayed above
 //
 //    parms passed:  see list below
 //
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement cstmt = null;
   PreparedStatement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
   }
     
   String club = (String)session.getAttribute("club");               // get club name

   int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get activity id

   //
   //  See if we are in teh timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
    
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(sess_activity_id, con);

   try {

      getClub.getParms(con, parm, sess_activity_id);          //  get the current club parms

   }
   catch (Exception e) {
      // ignore error
   }

   //
   // Define parms passed from doGet above
   //
   String omit = "";
   String index = "";
   //String temp = "";
   String clubName = "";        // club name
   String website_url = "";     // club website url
   String contact = "";         // club contact
   String email = "";           // contact's email
   String oldClub = "";               // existing clubName value
   String adv_zone = "";
   String tmp = "";
   String posType = "";             // POS Type - None, Abacus21, Jonas, NorthStar, or Pro-ShopKeeper
   String mship1 = "";
   String period = "";
   String smdays1 = "0";        // days in advance that members can make tee times (Mon - Sun)
   String smdays2 = "0";
   String smdays3 = "0";
   String smdays4 = "0";
   String smdays5 = "0";
   String smdays6 = "0";
   String smdays7 = "0";
   String sviewdays = "0";
   String smtimes = "0";
   String advamd1 = "";        // advance time values per day of week
   String advamd2 = "";
   String advamd3 = "";
   String advamd4 = "";
   String advamd5 = "";
   String advamd6 = "";
   String advamd7 = "";
     
   int mtimes = 0;               // membership limit number (number of rounds per period)
   int mdays1 = 0;               // days in advance that members can make tee times (Mon - Sun)
   int mdays2 = 0;
   int mdays3 = 0;
   int mdays4 = 0;
   int mdays5 = 0;
   int mdays6 = 0;
   int mdays7 = 0;
   int viewdays = 0;             // days can view tee sheets
   int advhrd1 = 0;              // advance hour per mship type (Mon - Sub)
   int advhrd2 = 0;
   int advhrd3 = 0;
   int advhrd4 = 0;
   int advhrd5 = 0;
   int advhrd6 = 0;
   int advhrd7 = 0;
   int advmind1 = 0;             // advance minutes per mship type (Mon - Sub)
   int advmind2 = 0;
   int advmind3 = 0;
   int advmind4 = 0;
   int advmind5 = 0;
   int advmind6 = 0;
   int advmind7 = 0;

   //
   //  Arrays to hold the member type parms (24 types)
   //
   String [] mem = new String [parm.MAX_Mems+1];
   //
   //  Arrays to hold the membership type parms (24 types)
   //
   String [] mship = new String [parm.MAX_Mships+1];
   String [] tflag = new String [parm.MAX_Mships+1];          // Tee sheet Flags for mship types
   String [] mpos = new String [parm.MAX_Mships+1];           // POS charge classes
   String [] mposc = new String [parm.MAX_Mships+1];          // POS charge codes (18 holes)
   String [] m9posc = new String [parm.MAX_Mships+1];         // POS charge codes (9 holes)
   String [] mshipItem = new String [parm.MAX_Mships+1];      // for Jonas POS - Mship Sales Item Code
   String [] mship9Item = new String [parm.MAX_Mships+1];     // for Jonas POS - Mship Sales Item Code 9 hole

   //
   //  other variables required
   //
   int multi = 0;               // multiple course support = no
   int lottery = 0;             // lottery support = no
   int hotel = 0;               // hotel support = no
   int allow_mobile = 0;        // mobile access = yes
   int dining = 0;              // dining requst support = no
   int guestdb = 0;             // guest db support = no
   int x = 0;                   // use 'x' (0 - 4)
   int xhrs = 0;                // # of hrs in advance to remove x's
   int rnds = 0;                
   int forceg = 0;
   int hiden = 0;
   int constimesm = 0;
   int constimesp = 0;
   int hrsbtwn = 0;           
   //int adv_hr = 0;
   //int adv_min = 0;
   int unacompGuest = 0;
   int hndcpProSheet = 0;
   int hndcpProEvent = 0;
   int hndcpMemSheet = 0;
   int hndcpMemEvent = 0;
   int emailOpt = 0;
   int error = 0;
   int memChange = 0;
   int mshipChange = 0;
   //int userlock = 0;
   //int zero = 0;
   int i = 0;
   int i2 = 0;
   int precheckin = 0;
   int paceofplay = 0;
   int allowMemPost = 0;
   int max_originations = 0;
   int cutoffdays = 0;
   int cutofftime = 0;
   int pos_paynow = 0;

   //float tfloat = 0;

   boolean found = false;
   boolean exists = false;

   String [] time_part = new String [1];
   String [] time_ampm = new String [2];

   for (i = 0; i < parm.MAX_Mships+1; i++) {

      mem[i] = "";
      mship[i] = "";
      mpos[i] = "";
      mposc[i] = "";
      m9posc[i] = "";
      mshipItem[i] = "";
      mship9Item[i] = "";
   }

   //
   //  Continue based on which step we are on.  These parms are broken into 4 pages to simplify the
   //  process.
   //
   if (req.getParameter("step1") != null) {

      //
      //  This is step 1 - process the 1st page of options
      //

      //
      // Get all the parameters entered
      //
      clubName = req.getParameter("clubName");            // name of club
      clubName = clubName.trim();
      website_url = req.getParameter("website_url"); // club website url
      contact = req.getParameter("contact");              // name of contact
      email = req.getParameter("email");                  // contact's email
      String semailOpt = req.getParameter("emailOpt");    // email option
      String smulti = req.getParameter("multi");          // multi course support
      String slottery = (req.getParameter("lottery") != null) ? req.getParameter("lottery") : "";      // lottery support
      String shotel = req.getParameter("hotel");          // hotel support
      String sallow_mobile = req.getParameter("allow_mobile");  // mobile access
      String sdining = req.getParameter("dining");        // dining request support
      String sguestdb = req.getParameter("guestdb");      // guest db support
      String sx = req.getParameter("x");                  // X support
      String sxhrs = req.getParameter("xhrs");            // hours to remove X
      adv_zone = req.getParameter("adv_zone");            // time zone
      String srnds = req.getParameter("rnds");            // # of rounds per day for members
      String shrsbtwn = req.getParameter("hrsbtwn");      // hours between rounds
      String sforceg = req.getParameter("forceg");        // force guest names option
      String shiden = req.getParameter("hiden");          // hide member names option
      posType = req.getParameter("posType");              // POS maker
      String sprecheckin = req.getParameter("precheckin");// pre-checkin support option
      String spaceofplay = req.getParameter("paceofplay");// paceofplay support option
      String hdcpSystem = req.getParameter("hdcpSystem"); // hdcpSystem option
      
      String currPass = req.getParameter("currPass");     // current password
      String newPass = req.getParameter("newPass");       // new password
      String newPass2 = req.getParameter("newPass2");     // confirm new pass
      if (currPass == null) currPass = "";
      
      String stemp = req.getParameter("allowMemPost");    // allowMemPost option
      try {

         allowMemPost = Integer.parseInt(stemp);
      }
      catch (NumberFormatException e) { }
      
      stemp = req.getParameter("max_originations");       // max_originations option
      try {

         max_originations = Integer.parseInt(stemp);
      }
      catch (NumberFormatException e) { }
      
      stemp = req.getParameter("unacomp");                // Unaccompanied Guest
      
      if (stemp.equalsIgnoreCase("yes")) {
          
          unacompGuest = 1;
      }
   
      
      pos_paynow = 0;                                    // default to No
      
      if (req.getParameter("pos_paynow") != null) {

         stemp = req.getParameter("pos_paynow");         // POS Pay Now option
      
         if (stemp.equals("Yes")) {
         
            pos_paynow = 1;
         }
      }
      
      String sst_hr = req.getParameter("st_hr");
      String sst_min = req.getParameter("st_min");
      String st_ampm = req.getParameter("st_ampm");
      String set_hr = req.getParameter("et_hr");
      String set_min = req.getParameter("et_min");
      String et_ampm = req.getParameter("et_ampm");
      String sn_interval = req.getParameter("notify_interval");
      String nwindow_starttime = "";
      String nwindow_endtime = "";
      
      int st_hr = 0;
      int st_min = 0;
      int et_hr = 0;
      int et_min = 0;
      int n_interval = 0;
      
      if (IS_TLT) {
          
          if (sst_hr == null) sst_hr = "0";
          if (sst_min == null) sst_min = "0";
          st_ampm = req.getParameter("st_ampm");
          if (set_hr == null) set_hr = "0";
          if (set_min == null) set_min = "0";
          et_ampm = req.getParameter("et_ampm");
          if (sn_interval == null) sn_interval = "0";
          
          try {
             
             n_interval = Integer.parseInt(sn_interval);
             
             st_hr = Integer.parseInt(sst_hr);
             st_min = Integer.parseInt(sst_min);
             et_hr = Integer.parseInt(set_hr);
             et_min = Integer.parseInt(set_min);
             
          }
          catch (NumberFormatException e) {
              
             out.println(SystemUtils.HeadTitle("Data Entry Error"));
             out.println("<BODY><CENTER>");
             out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
             out.println("<BR><BR>Notification acceptance times seem to be invalid.");
             out.println("<BR><BR>Error:" + e.toString());
             out.println("<BR>Please try again.");
             out.println("<br><br><font size=\"2\">");
             out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
             out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
             out.println("</input></form></font>");
             out.println("</CENTER></BODY></HTML>");
             return;
         
          }
         
          if (n_interval == 0) n_interval = 10; // default
          if (st_ampm.equals("PM")) st_hr += 12;
          if (et_ampm.equals("PM")) et_hr += 12;
          if (st_hr == 24) st_hr = 0;
          if (et_hr == 24) et_hr = 0;
          
          nwindow_starttime = st_hr + ":" + st_min + ":00";
          nwindow_endtime = et_hr + ":" + et_min + ":00";
      } else {
       
          // set to mysql defaults for field type
          nwindow_starttime = "00:00:00";
          nwindow_endtime = "00:00:00";
      }
      
      
      if (stemp.equals( "Yes" )) {

         unacompGuest = 1;
      }
      stemp = req.getParameter("hndcpPS");                // Display Handicaps on Sheet
      if (stemp.equals( "Yes" )) {

         hndcpProSheet = 1;
      }
      stemp = req.getParameter("hndcpPE");                // Display Handicap on Event List
      if (stemp.equals( "Yes" )) {

         hndcpProEvent = 1;
      }
      stemp = req.getParameter("hndcpMS");                // Display Handicaps on Sheet
      if (stemp.equals( "Yes" )) {

         hndcpMemSheet = 1;
      }
      stemp = req.getParameter("hndcpME");                // Display Handicap on Event List
      if (stemp.equals( "Yes" )) {

         hndcpMemEvent = 1;
      }

      precheckin = (sprecheckin.equals("Yes")) ? 1 : 0;    // get pre-check-in option
      
      paceofplay = (spaceofplay.equals("Yes")) ? 1 : 0;    // get paceofplay option
        
      stemp = req.getParameter("constimesm");            // Get Consecutinve Tee Time values 
      
      try {
         constimesm = Integer.parseInt(stemp);           
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      stemp = req.getParameter("constimesp");           

      try {
         constimesp = Integer.parseInt(stemp);
      }
      catch (NumberFormatException e) {
         // ignore error
      }


      stemp = req.getParameter("cutoffdays");          // Cut Off Days    

      try {
         cutoffdays = Integer.parseInt(stemp);
      }
      catch (NumberFormatException e) {
         // ignore error
      }


      String cut_time = req.getParameter("cutofftime");     // Cut Off Time      

      try {
        cut_time = cut_time.trim(); // trim the time string
        
        // first lets see if the cut_time is empty, if so then we'll erase or clear this time
        if (!cut_time.equals("")) {
            
            // see if they included a space between the minutes and the ampm
            if (cut_time.indexOf(" ") == -1) {

                // no space - split is manually by its expected format
                time_ampm[0] = cut_time.substring(0, cut_time.length() - 2);
                time_ampm[1] = cut_time.substring(cut_time.length() - 2, cut_time.length());
            } else {

                // space found - use string split
                time_ampm = cut_time.split(" "); // split it into the first array
            }

            time_part = time_ampm[0].split(":"); // explode the first part into seperate time segments

            int hr = Integer.parseInt(time_part[0]);
            int min = Integer.parseInt(time_part[1]);

            if (time_ampm[1].equalsIgnoreCase("pm") && hr != 12) hr += 12;
            if (time_ampm[1].equalsIgnoreCase("am") && hr == 12) hr = 0;
            
            cutofftime = (hr * 100) + min;

        } // end if cut_time empty
        
      }
      catch (NumberFormatException e) {
         // ignore error
      }

    
      
      if (sforceg.equals( "Yes" )) {                      // Force Guest Names

         forceg = 1;
      }

      if (shiden.equals( "Yes" )) {                       // Hide Member Names

         hiden = 1;
      }

      //
      //  Filter out special characters - change from html format to real chars
      //
      clubName = SystemUtils.filter(clubName);

      //
      // Convert string values to numeric values
      //
      if (smulti.equals( "Yes" )) {
          multi = 1;
      }
      if (semailOpt.equals( "Yes" )) {
          emailOpt = 1;
      }
      if (slottery.equals( "Yes" )) {
          lottery = 1;
      }
      if (shotel.equals( "Yes" )) {
          hotel = 1;
      }
      if (sallow_mobile.equals( "Yes")) {
          allow_mobile = 1;
      }
      if (sdining.equals( "Yes" )) {
          dining = 1;
      }
      if (sguestdb.equals( "Yes" )) {
          guestdb = 1;
      }
        
      try {
         x = Integer.parseInt(sx);             // X value
         xhrs = Integer.parseInt(sxhrs);       // X hours
         rnds = Integer.parseInt(srnds);       // # rounds per day
         hrsbtwn = Integer.parseInt(shrsbtwn); // hours between rounds
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      // Verify the parameters - the following parms are required:
      //
      //       club name
      //       member type
      //       membership type
      //
      if (clubName.equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>Club Name not specified - this is required.");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if (max_originations != 0 && max_originations < constimesm) {
         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR>The value for maximum " + sysLingo.TEXT_tee_times + " a member can originate must be equal to or greater than the value specified for members Consecutive " + sysLingo.TEXT_tee_times + ".");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
      
      //
      //  if x specified (remove X's) then xhrs must be between 0 and 240
      //
      if ((x != 0) && ((xhrs < 0) || (xhrs > 240))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>The Number of Hours to Remove X's must be in the range of 0 - 240 (10 days). You entered " + xhrs + ".");
         out.println("<BR>Please change it and try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      //  If pro wants to receive emailed tee sheets, but did not specify an email addr -> error
      //
      if (emailOpt != 0 && email.equals( "")) {

         out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Input Error - Invalid Email Option Specified</H3><BR>");
         out.println("<BR><BR>Sorry, you requested that we send tee sheets via email, but you did not provide an email address.<BR>");
         out.println("<BR><BR>Please try again.<BR>");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      // retrieve the current email password
      String emailPass = "";
      try {

        cstmt = con.createStatement();
        rs = cstmt.executeQuery("SELECT emailPass FROM club5;");

        if (rs.next()) emailPass = rs.getString(1);

        rs.close();
        cstmt.close();

      } catch (Exception exp) {

        SystemUtils.buildDatabaseErrMsg("Unable to retrieve required password.", exp.toString(), out, false);
        return;
      }
      
      // flag to tell if we are to update the email password
      boolean updatePass = false;
      
      // if there is a password set and the password they provided doesn't match
      if (currPass.equals("") && newPass.equals("") && newPass2.equals("")) {
          
          // do nothing, we don't want to update if all three password fields are blank (no change)
          
      } else if (!emailPass.equals("") && !currPass.equals(emailPass)) {
          
         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Email Password Error</H3>");
         out.println("<BR>The Current Password you provided does not match the existing password that is in place.");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
         
      } else if (!newPass.equals(newPass2)) {
      
         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Email Password Error</H3>");
         out.println("<BR>New passwords do not match.&nbsp; You must provide the same password in both the 'New Password' box and the 'Confirm New Password' box.");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
         
      } else {
          
         updatePass = true;
        
      }
      

      //
      // Parms valid - add or update the club in the database
      //
      try {

         //
         //  See if the club5 table exists yet
         //
         cstmt = con.createStatement();        // create a statement

         rs = cstmt.executeQuery("SELECT clubName FROM club5");   // check for club5 table entry

         if (rs.next()) {

            // club5 DB Table already exists

            exists = true;

         } else {

            exists = false;
         }

         cstmt.close();

      }
      catch (Exception exc) {

         exists = false;
      }


      try {

         if (exists == true ) {             // create entry if club5 does not exist yet

            //
            // Parms already exist - now update them in the database
            //
            PreparedStatement pstmt = con.prepareStatement (
               "UPDATE club5 SET clubName = ?, multi = ?, lottery = ?, contact = ?, email = ?, " +
               "x = ?, xhrs = ?, adv_zone = ?, emailOpt = ?, hotel = ?, " +
               "unacompGuest = ?, hndcpProSheet = ?, hndcpProEvent = ?, hndcpMemSheet = ?, " +
               "hndcpMemEvent = ?, posType = ?, rndsperday = ?, hrsbtwn = ?, forcegnames = ?, " +
               "hidenames = ?, constimesm = ?, constimesp = ?, precheckin = ?, paceofplay = ?, hdcpSystem = ?, " +
               "nwindow_starttime = ?, nwindow_endtime = ?, notify_interval = ?, allowMemPost = ?, max_originations = ?, " +
               "cutoffDays = ?, cutoffTime = ?, pos_paynow = ?, dining = ?, guestdb = ?, " +
               "website_url = ?, allow_mobile = ? " +
               ((updatePass) ? ", emailPass = ?" : ""));

            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, clubName);
            pstmt.setInt(2, multi);
            pstmt.setInt(3, lottery);
            pstmt.setString(4, contact);
            pstmt.setString(5, email);
            pstmt.setInt(6, x);
            pstmt.setInt(7, xhrs);
            pstmt.setString(8, adv_zone);
            pstmt.setInt(9, emailOpt);
            pstmt.setInt(10, hotel);
            pstmt.setInt(11, unacompGuest);
            pstmt.setInt(12, hndcpProSheet);
            pstmt.setInt(13, hndcpProEvent);
            pstmt.setInt(14, hndcpMemSheet);
            pstmt.setInt(15, hndcpMemEvent);
            pstmt.setString(16, posType);
            pstmt.setInt(17, rnds);
            pstmt.setInt(18, hrsbtwn);
            pstmt.setInt(19, forceg);
            pstmt.setInt(20, hiden);
            pstmt.setInt(21, constimesm);
            pstmt.setInt(22, constimesp);
            pstmt.setInt(23, precheckin);
            pstmt.setInt(24, paceofplay);
            pstmt.setString(25, hdcpSystem);
            pstmt.setString(26, nwindow_starttime);
            pstmt.setString(27, nwindow_endtime);
            pstmt.setInt(28, n_interval);
            pstmt.setInt(29, allowMemPost);
            pstmt.setInt(30, max_originations);
            pstmt.setInt(31, cutoffdays);
            pstmt.setInt(32, cutofftime);
            pstmt.setInt(33, pos_paynow);
            pstmt.setInt(34, dining);
            pstmt.setInt(35, guestdb);
            pstmt.setString(36, website_url);
            pstmt.setInt(37, allow_mobile);
            if (updatePass) pstmt.setString(38, newPass);
            
            pstmt.executeUpdate();  // execute the prepared stmt

            pstmt.close();

         } else {

            //
            // Parms do not exist - insert them in the database (Init ALL parms now)
            //
            //
            //   NOTE:  see also Support_cluboptions - this now builds the initial entry at init time!!!!!!!!!!!!!!!!!!!!!!!!
            //
            //
            PreparedStatement pstmt1 = con.prepareStatement (
               "INSERT INTO club5 (clubName, multi, lottery, contact, email, " +
               "guest1, guest2, guest3, guest4, guest5, guest6, guest7, guest8, " +
               "guest9, guest10, guest11, guest12, guest13, guest14, guest15, " +
               "guest16, guest17, guest18, guest19, guest20, guest21, guest22, " +
               "guest23, guest24, guest25, guest26, guest27, guest28, guest29, " +
               "guest30, guest31, guest32, guest33, guest34, guest35, guest36, " +
               "mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
               "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
               "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24, " +
               "mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, " +
               "mship9, mship10, mship11, mship12, mship13, mship14, mship15, mship16, " +
               "mship17, mship18, mship19, mship20, mship21, mship22, mship23, mship24, " +
               "x, xhrs, adv_zone, emailOpt, lottid, hotel, userlock, " +
               "unacompGuest, hndcpProSheet, hndcpProEvent, hndcpMemSheet, hndcpMemEvent, " +
               "posType, logins, rndsperday, hrsbtwn, forcegnames, hidenames, " +
               "constimesm, constimesp, precheckin, paceofplay," +
               "nwindow_starttime, nwindow_endtime, notify_interval, " +
               "hdcpSystem, allowMemPost, lastHdcpSync, hdcpStartDate, hdcpEndDate, emailPass, max_originations, " +
               "cutoffDays, cutoffTime, pos_paynow, dining, guestdb, website_url, allow_mobile) " +
               "VALUES (?,?,?,?,?, " +
               "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +
               "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +
               "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +
               "?,?,?,?,0,?,0, " +
               "?,?,?,?,?, " +
               "?,0,?,?,?,?, " +
               "?,?,?,?,?,?,?," +
               "?,?,'0000-00-00','0000-00-00 00:00:00','0000-00-00 00:00:00'," + // hdcpSystem, allowMemPost, lastHdcpSync, hdcpStartDate, hdcpEndDate
               "?,?,?,?,?,?,?,?,?)"); // emailPass, max_originations

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, clubName);
            pstmt1.setInt(2, multi);
            pstmt1.setInt(3, lottery);
            pstmt1.setString(4, contact);
            pstmt1.setString(5, email);
            pstmt1.setString(6, omit);
            pstmt1.setString(7, omit);
            pstmt1.setString(8, omit);
            pstmt1.setString(9, omit);
            pstmt1.setString(10, omit);
            pstmt1.setString(11, omit);
            pstmt1.setString(12, omit);
            pstmt1.setString(13, omit);
            pstmt1.setString(14, omit);
            pstmt1.setString(15, omit);
            pstmt1.setString(16, omit);
            pstmt1.setString(17, omit);
            pstmt1.setString(18, omit);
            pstmt1.setString(19, omit);
            pstmt1.setString(20, omit);
            pstmt1.setString(21, omit);
            pstmt1.setString(22, omit);
            pstmt1.setString(23, omit);
            pstmt1.setString(24, omit);
            pstmt1.setString(25, omit);
            pstmt1.setString(26, omit);
            pstmt1.setString(27, omit);
            pstmt1.setString(28, omit);
            pstmt1.setString(29, omit);
            pstmt1.setString(30, omit);
            pstmt1.setString(31, omit);
            pstmt1.setString(32, omit);
            pstmt1.setString(33, omit);
            pstmt1.setString(34, omit);
            pstmt1.setString(35, omit);
            pstmt1.setString(36, omit);
            pstmt1.setString(37, omit);
            pstmt1.setString(38, omit);
            pstmt1.setString(39, omit);
            pstmt1.setString(40, omit);
            pstmt1.setString(41, omit);
            pstmt1.setString(42, omit);
            pstmt1.setString(43, omit);
            pstmt1.setString(44, omit);
            pstmt1.setString(45, omit);
            pstmt1.setString(46, omit);
            pstmt1.setString(47, omit);
            pstmt1.setString(48, omit);
            pstmt1.setString(49, omit);
            pstmt1.setString(50, omit);
            pstmt1.setString(51, omit);
            pstmt1.setString(52, omit);
            pstmt1.setString(53, omit);
            pstmt1.setString(54, omit);
            pstmt1.setString(55, omit);
            pstmt1.setString(56, omit);
            pstmt1.setString(57, omit);
            pstmt1.setString(58, omit);
            pstmt1.setString(59, omit);
            pstmt1.setString(60, omit);
            pstmt1.setString(61, omit);
            pstmt1.setString(62, omit);
            pstmt1.setString(63, omit);
            pstmt1.setString(64, omit);
            pstmt1.setString(65, omit);
            pstmt1.setString(66, omit);
            pstmt1.setString(67, omit);
            pstmt1.setString(68, omit);
            pstmt1.setString(69, omit);
            pstmt1.setString(70, omit);
            pstmt1.setString(71, omit);
            pstmt1.setString(72, omit);
            pstmt1.setString(73, omit);
            pstmt1.setString(74, omit);
            pstmt1.setString(75, omit);
            pstmt1.setString(76, omit);
            pstmt1.setString(77, omit);
            pstmt1.setString(78, omit);
            pstmt1.setString(79, omit);
            pstmt1.setString(80, omit);
            pstmt1.setString(81, omit);
            pstmt1.setString(82, omit);
            pstmt1.setString(83, omit);
            pstmt1.setString(84, omit);
            pstmt1.setString(85, omit);
            pstmt1.setString(86, omit);
            pstmt1.setString(87, omit);
            pstmt1.setString(88, omit);
            pstmt1.setString(89, omit);
            pstmt1.setInt(90, x);
            pstmt1.setInt(91, xhrs);
            pstmt1.setString(92, adv_zone);
            pstmt1.setInt(93, emailOpt);
            pstmt1.setInt(94, hotel);
            pstmt1.setInt(95, unacompGuest);
            pstmt1.setInt(96, hndcpProSheet);
            pstmt1.setInt(97, hndcpProEvent);
            pstmt1.setInt(98, hndcpMemSheet);
            pstmt1.setInt(99, hndcpMemEvent);
            pstmt1.setString(100, posType);
            pstmt1.setInt(101, rnds);
            pstmt1.setInt(102, hrsbtwn);
            pstmt1.setInt(103, forceg);
            pstmt1.setInt(104, hiden);
            pstmt1.setInt(105, constimesm);
            pstmt1.setInt(106, constimesp);
            pstmt1.setInt(107, precheckin);
            pstmt1.setInt(108, paceofplay);
            pstmt1.setString(109, nwindow_starttime);
            pstmt1.setString(110, nwindow_endtime);
            pstmt1.setInt(111, n_interval);
            pstmt1.setString(112, hdcpSystem);
            pstmt1.setInt(113, allowMemPost);
            pstmt1.setString(114, newPass);
            pstmt1.setInt(115, max_originations);
            pstmt1.setInt(116, cutoffdays);
            pstmt1.setInt(117, cutofftime);
            pstmt1.setInt(118, pos_paynow);
            pstmt1.setInt(119, dining);
            pstmt1.setInt(120, guestdb);
            pstmt1.setString(121, website_url);
            pstmt1.setInt(122, allow_mobile);

            pstmt1.executeUpdate();          // execute the prepared stmt

            pstmt1.close();
         }

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      // Database updated - done
      //
      out.println(SystemUtils.HeadTitle2("Proshop - Club Setup Step 1"));
        
      if (req.getParameter("Done") != null) {    // exit if Done specified
         out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_announce\">");
      } else {                                   // must be Continue
         out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_club?step2=yes\">");
      }
      out.println("</HEAD>");
      out.println("<BODY>");

      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

      out.println("<CENTER>");
      out.println("<BR><BR><H3>Club Options Updated - Step 1</H3>");

      if (exists == true) {      // if parms already existed

         out.println("<BR>Thank you, the club options have been changed.");

      } else {             // first time adding parms

         out.println("<BR>The club options have been set.");
      }

      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      if (req.getParameter("Done") != null) {    // exit if Done specified
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
      } else {
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_club\">");
         out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");
         out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
      }
      out.println("</CENTER></BODY></HTML>");
   
   } else {  // NOT step1

      //
      //  get the POS Type
      //
      try {
         Statement stmt9 = con.createStatement();        // create a statement

         rs = stmt9.executeQuery("SELECT posType FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            posType = rs.getString(1);
         }
         stmt9.close();
      }
      catch (Exception ignore) {
      }

   }  // end of step 1


   //
   //  Continue based on which step we are on.  These parms are broken into 5 pages to simplify the
   //  process.
   //
   if (req.getParameter("step2") != null) {

      //
      //  This is step 2 - process the 2nd page of options (guest types)
      //

      //
      //  Arrays to hold the guest parms
      //
      int guests = 0;
      if (req.getParameter("guests") != null) guests = Integer.parseInt(req.getParameter("guests"));

      String [] g = new String [guests];
      String [] revs = new String [guests];
      String [] guest = new String [guests];
      String [] gpos = new String [guests];
      String [] g9pos = new String [guests];
      String [] gstItem = new String [guests];
      String [] gst9Item = new String [guests];
      String [] guestdbs = new String [guests];
      int [] gOpt = new int [guests];
      int [] gDb = new int [guests];
      int [] gRev = new int [guests];

      //
      //  arrays to position the guest type parms
      //
      String [] guestN = new String [guests];     // array to hold the guest names
      int [] goptN = new int [guests];            // array to hold the guest options
      int [] gdbN = new int [guests];             // array to hold the guest tracking options
      int [] grevN = new int [guests];            // array to hold the guest revenue options

      int guestChange = 0;

      //
      //  init the string arrays to prevent null problems
      //
      for (i = 0; i < guests; i++) {

         g[i] = "";
         guest[i] = "";
         revs[i] = "";
         gpos[i] = "";
         g9pos[i] = "";
         gstItem[i] = "";
         gst9Item[i] = "";

         /*if (i != guests - 1)*/ guestN[i] = "";    // 1 less for this one
         goptN[i] = 0;
         grevN[i] = 0;
      }

      //
      // Get all the parameters entered
      //
      for (i = 0; i < guests; i++) {
        
         if (req.getParameter("guest" + (i + 1)) != null) {
            guest[i] = req.getParameter("guest" + (i + 1)).trim();      // guest names
         }
            
         if (req.getParameter("gOpt" + (i + 1)) != null) {
            g[i] = req.getParameter("gOpt" + (i + 1));                  // guest pro-only options
         }

         if (req.getParameter("gDb" + (i + 1)) != null) {
            guestdbs[i] = req.getParameter("gDb" + (i + 1));            // guest tracking options
         }

         if (req.getParameter("gRev" + (i + 1)) != null) {
            revs[i] = req.getParameter("gRev" + (i + 1));               // guest revenue options
         }

         if (req.getParameter("gpos" + (i + 1)) != null) {
            gpos[i] = req.getParameter("gpos" + (i + 1)).trim();        // guest POS charge codes (18 hole rates)
         }

         if (req.getParameter("g9pos" + (i + 1)) != null) {
            g9pos[i] = req.getParameter("g9pos" + (i + 1)).trim();      // guest POS charge codes (9 hole rates)
         }

         if (req.getParameter("gstItem" + (i + 1)) != null) {
            gstItem[i] = req.getParameter("gstItem" + (i + 1)).trim();
         }

         if (req.getParameter("gst9Item" + (i + 1)) != null) {
            gst9Item[i] = req.getParameter("gst9Item" + (i + 1)).trim();
         }

         if (g[i].equals( "Yes" )) {
             gOpt[i] = 1;                  // guest pro-only options
         }

         if (guestdbs[i].equals("Yes")) {
             gDb[i] = 1;                   // guest tracking options
         }

         if (revs[i].equals( "Yes" )) {
             gRev[i] = 1;                  // guest revenue options
         }

         //  copy the guest parms into arrays so they will be sequenced
         guestN[i] = guest[i];
         goptN[i] = gOpt[i];
         gdbN[i] = gDb[i];
         grevN[i] = gRev[i];

      }

      //
      //  Check to make sure that none of the guest names are subsets of the other names.
      //  This will not work in _slot when checking guest restrictions.
      //  (i.e.  'Guest' and 'Guest 2' will not work.)
      //
      error = 0;                   // init error flag

      for (i = 0; i < guests - 1; i++) {          // check each guest type (except last one)

         if (!guestN[i].equals( "" )) {

            for (i2 = i+1; i2 < guests; i2++) {          // check each guest type

               if (!guestN[i2].equals( "" ) && guestN[i2].startsWith( guestN[i] )) error = 1;      // set error

            } // end inner loop

         } // skip the empty ones

      } // end loop

      if (error != 0) {

         out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
         out.println("<BODY><CENTER><BR>");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H3>Input Error - Invalid Guest Names</H3><BR>");
         out.println("<BR><BR>Sorry, the names you entered for Guest Types will cause the system to malfunction.<BR>");
         out.println("<BR>One name cannot be a subset of any other name.");
         out.println("<BR>For example, 'Guest' and 'Guest 2' are invalid, but 'Guest 1' and 'Guest 2' are valid.");
         out.println("<BR><BR>Please try again.<BR>");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if (posType.equals( "IBS" )) {         // if IBS, check for '$' in fee values (not allowed)

         error = 0;

         for (i=0; i<guests; i++) {        // check all guest types

            if (gpos[i].startsWith( "$" ) || g9pos[i].startsWith( "$" )) {

               error = 1;
            }
         }

         if (error != 0) {    

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>The $ (dollar Sign) is not necessary and will result in an error from the IBS POS System.");
            out.println("<BR>Please remove all dollar signs from the Fee values.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }

      //
      // Parms valid - add or update the club in the database
      //
      try {

         if (!parm.clubName.equals( "" )) {          // parms exist yet??
             
            oldClub = parm.clubName;                 // save for later

            //
            //  Check if Guests, Member Types or Membership Types have changed,
            //  if so, inform the user to check restrictions, etc.
            //
            if (!parm.guest[0].equals( "" )) {    // if not the first time here

               gloop1:
               for (i = 0; i < guests; i++) {

                  // only loop over the existing # of entires and ignore the new ones for this test
                  if (i < parm.MAX_Guests) {

                      if (!guestN[i].equals( parm.guest[i])) {    // compare new guest names to previous names

                         guestChange = 1;     // indicate that a guest name has changed
                         break gloop1;
                      }
                  }
               }
            }

            //
            // Now set the guest parms for each guest type specified (different table)
            //
            for (i = 0; i < guests; i++) {

               found = false;

               if (!guestN[i].equals( "" )) {     // if guest type specified

                  stmt = con.prepareStatement (
                          "SELECT gpos FROM guest5 WHERE guest = ? AND activity_id = ?");

                  stmt.clearParameters();        // clear the parms
                  stmt.setString(1, guestN[i]);
                  stmt.setInt(2, sess_activity_id);
                  rs = stmt.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     found = true;           // guest type already exists
                  }
                  stmt.close();

                  if (found == true) {       // if found, update it, else add it

                     stmt = con.prepareStatement (
                        "UPDATE guest5 SET " +
                        "gOpt = ?, gpos = ?, g9pos = ?, gstItem = ?, gst9Item = ?, revenue = ?, use_guestdb = ? " +
                        "WHERE guest = ? AND activity_id = ?");

                     stmt.clearParameters();
                     stmt.setInt(1, goptN[i]);
                     stmt.setString(2, gpos[i]);
                     stmt.setString(3, g9pos[i]);
                     stmt.setString(4, gstItem[i]);
                     stmt.setString(5, gst9Item[i]);
                     stmt.setInt(6, grevN[i]);
                     stmt.setInt(7, gdbN[i]);

                     stmt.setString(8, guestN[i]);      // for this guest type
                     stmt.setInt(9, sess_activity_id);

                     stmt.executeUpdate();
                     stmt.close();

                  } else {                   // guest not found - add it 

                     stmt = con.prepareStatement (
                        "INSERT INTO guest5 (guest, activity_id, gOpt, " +
                        "gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                        "VALUES (?,?,?, " +
                        "?,?,?,?,?,?)");

                     stmt.clearParameters();
                     stmt.setString(1, guestN[i]);
                     stmt.setInt(2, sess_activity_id);
                     stmt.setInt(3, goptN[i]);
                     stmt.setString(4, gpos[i]);
                     stmt.setString(5, g9pos[i]);
                     stmt.setString(6, gstItem[i]);
                     stmt.setString(7, gst9Item[i]);
                     stmt.setInt(8, grevN[i]);
                     stmt.setInt(9, gdbN[i]);

                     stmt.executeUpdate();
                     stmt.close();
                  }
               }
            } // end guest loop

            // only continue if club is setup and we found at least one guest type (prevents null errors)
            if (guestN[0] != null) {

                String tmp_guests = "";

                for (i = 0; i < guests; i++) {

                    if (guestN[i] != null && !guestN[i].equals("")) {
                        tmp_guests += "\"" + guestN[i] + "\",";
                    }
                }

                // this check is probably not needed any longer with the null check above
                if (tmp_guests.length() > 0 ) {

                    tmp_guests = tmp_guests.substring(0, tmp_guests.length()-1); // trim the last comma

                    stmt = con.prepareStatement("DELETE FROM guest5 WHERE activity_id = ? AND guest NOT IN (" + tmp_guests + ")");
                    stmt.clearParameters();
                    stmt.setInt(1, sess_activity_id);

                    stmt.executeUpdate();
                    stmt.close();

                }

            }

         } else {

            //
            // Parms do not exist - ERROR
            //
            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, a sequence error has occurred and we cannot continue.");
            out.println("<BR>You must set the Club Options before the Guest Types.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><br>Exception: " + exc.toString());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle2("Proshop - Club Setup Step 2"));

      if (guestChange == 0) {    
         if (req.getParameter("Done") != null) {    // exit if Done specified
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_announce\">");
         } else if (req.getParameter("Add") != null) {    // exit if Add specified then return to guest page
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_club?step2=yes\">");
         } else {
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_club?step3=yes\">");
         }
      }
        
      out.println("</HEAD>");
      out.println("<BODY>");
        
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

      out.println("<CENTER>");
      out.println("<BR><BR><H3>Guest Type Setup Complete</H3>");
      out.println("Thank you, the guest types have been updated.");

      if (guestChange == 1) {      // if a Guest name changed

         out.println("<br><br><font size=\"4\"><b>*** Warning ***</b>");
         out.println("</font>");
         out.println("<br><br>One or more of the Guest Names have changed.");
         out.println("<br><br>You must check all existing Guest Restrictions (in System Configuration).");
         out.println("<br>Any restriction that references, or should reference, this Guest Name");
         out.println("<br>MUST be updated and re-submitted (even if you do not have to make a change).");
         out.println("<br>Failure to do this could result in an inoperative Guest Restriction.");
         out.println("<br><br>Please contact your ForeTees Representative or ForeTees Support");
         out.println("<br>if you have any questions or need assistance.");
      }

      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      if (req.getParameter("Done") != null) {    // exit if Done specified
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      } else if (req.getParameter("Add") != null) {    // exit if Add specified then return to guest page
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_club\">");
         out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");
      } else {
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_club\">");
         out.println("<input type=\"hidden\" name=\"step3\" value=\"yes\">");
      }
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");

   }  // end of step 2


   //
   //  Continue based on which step we are on.  These parms are broken into 5 pages to simplify the
   //  process.
   //
   if (req.getParameter("step3") != null) {

      //
      //  This is step 3 - process the 3rd page of options  (Member Type options)
      //

      //
      // Get all the parameters entered
      //
      for (i=1; i<parm.MAX_Mems+1; i++) {
        
         mem[i] = req.getParameter("mem" +i);         // member types (24 possible)
      }

      //
      // Verify the parameters - the following parms are required:
      //
      //       member type
      //
      if (mem[1].equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>A Member Type was not specified - required.");
         out.println("<BR><BR>There must be a Member Type in position 1.");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      // Parms valid - add or update the club in the database
      //
      try {

         if (!parm.clubName.equals( "" )) {          // parms exist yet??

            oldClub = parm.clubName;                 // save for later

            //
            //  Check if Member Types have changed,
            //  if so, inform the user to check restrictions, etc.
            //
            if (!parm.mem[0].equals( "" )) {        // if not first time here
              
               i2 = 0;                            // mem types in parm start at zero in array
               for (i=1; i<parm.MAX_Mems+1; i++) {

                  if (!mem[i].equals( parm.mem[i2] )) {

                     memChange = 1;     // indicate that a member name has changed
                  }
                  i2++;
               }
            }

            //
            // Parms already exist - now update them in the database
            //
            PreparedStatement pstmt = con.prepareStatement (
               "UPDATE club5 SET " +
               "mem1 = ?, mem2 = ?, mem3 = ?, mem4 = ?, mem5 = ?, mem6 = ?, mem7 = ?, mem8 = ?, " +
               "mem9 = ?, mem10 = ?, mem11 = ?, mem12 = ?, mem13 = ?, mem14 = ?, mem15 = ?, mem16 = ?, " +
               "mem17 = ?, mem18 = ?, mem19 = ?, mem20 = ?, mem21 = ?, mem22 = ?, mem23 = ?, mem24 = ? " +
               "WHERE clubName = ?");

            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, mem[1]);
            pstmt.setString(2, mem[2]);
            pstmt.setString(3, mem[3]);
            pstmt.setString(4, mem[4]);
            pstmt.setString(5, mem[5]);
            pstmt.setString(6, mem[6]);
            pstmt.setString(7, mem[7]);
            pstmt.setString(8, mem[8]);
            pstmt.setString(9, mem[9]);
            pstmt.setString(10, mem[10]);
            pstmt.setString(11, mem[11]);
            pstmt.setString(12, mem[12]);
            pstmt.setString(13, mem[13]);
            pstmt.setString(14, mem[14]);
            pstmt.setString(15, mem[15]);
            pstmt.setString(16, mem[16]);
            pstmt.setString(17, mem[17]);
            pstmt.setString(18, mem[18]);
            pstmt.setString(19, mem[19]);
            pstmt.setString(20, mem[20]);
            pstmt.setString(21, mem[21]);
            pstmt.setString(22, mem[22]);
            pstmt.setString(23, mem[23]);
            pstmt.setString(24, mem[24]);

            pstmt.setString(25, oldClub);

            pstmt.executeUpdate();  // execute the prepared stmt

            pstmt.close();

         } else {

            //
            // Parms do not exist - ERROR
            //
            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, a sequence error has occurred and we cannot continue.");
            out.println("<BR>You set the Club Options before you enter Member types.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle2("Proshop - Club Setup Step 3"));

      if (memChange == 0) {
         if (req.getParameter("Done") != null) {    // exit if Done specified
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_announce\">");
         } else {
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_club?step4=yes\">");
         }
      }
      out.println("</HEAD>");
      out.println("<BODY>");
        
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
        
      out.println("<CENTER>");
      out.println("<BR><BR><H3>Member Type Setup Complete</H3>");
      out.println("Thank you, the member types have been updated.");

      if (memChange == 1) {      // if a Member name changed

         out.println("<br><br><font size=\"4\"><b>*** Warning ***</b>");
         out.println("</font>");
         out.println("<br><br>One or more of the Member Types have changed.  You must check all");
         out.println("<br>existing Member Restrictions (in System Configuration).");
         out.println("<br>Any restriction that references, or should reference, this Member Type");
         out.println("<br>MUST be updated and re-submitted (even if you do not have to make a change).");
         out.println("<br>Failure to do this could result in an inoperative Member Restriction.");
         out.println("<br><br>Please contact your ForeTees Representative or ForeTees Support");
         out.println("<br>if you have any questions or need assistance.");
      }

      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      if (req.getParameter("Done") != null) {    // exit if Done specified
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      } else {
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_club\">");
         out.println("<input type=\"hidden\" name=\"step4\" value=\"yes\">");
      }
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");

   }  // end of step 3


   //
   //  Continue based on which step we are on.  These parms are broken into 5 pages to simplify the
   //  process.
   //
   if (req.getParameter("step4") != null) {

      //
      //  This is step 4 - process the 4th page of options (Mship Types)
      //

      //
      // Get all the parameters entered
      //
      for (i=0; i<parm.MAX_Mships; i++) {

         if (req.getParameter("mship" + (i + 1)) != null) {
            mship[i] = req.getParameter("mship" + (i + 1)).trim();               // membership types (24 possible)
         }
         if (req.getParameter("tflag" + (i + 1)) != null) {
            tflag[i] = req.getParameter("tflag" + (i + 1));               // tee sheet flags (24 possible)
         }
         if (req.getParameter("mpos" + (i + 1)) != null) {
            mpos[i] = req.getParameter("mpos" + (i + 1));                 // mship POS charge classes
         }
         if (req.getParameter("mposc" + (i + 1)) != null) {
            mposc[i] = req.getParameter("mposc" + (i + 1));                // mship POS charge codes
         }
         if (req.getParameter("m9posc" + (i + 1)) != null) {
            m9posc[i] = req.getParameter("m9posc" + (i + 1));              // mship POS charge codes (9 hole)
         }
         if (req.getParameter("mshipItem" + (i + 1)) != null) {
            mshipItem[i] = req.getParameter("mshipItem" + (i + 1));        // mship POS charge Items
         }
         if (req.getParameter("mship9Item" + (i + 1)) != null) {
            mship9Item[i] = req.getParameter("mship9Item" + (i + 1));        // mship POS charge Items
         }
      }

      //
      // Verify the parameters - the following parms are required:
      //
      //       member type
      //       membership type
      //
      if (mship[0].equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>A Membership Type was not specified - required.");
         out.println("<BR><BR>There must be a Membership Type in position 1.");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if (posType.equals( "IBS" )) {         // if IBS, check for '$' in fee values (not allowed)

         error = 0;

         for (i=0; i<parm.MAX_Mships; i++) {

            if (mposc[i].startsWith( "$" ) || m9posc[i].startsWith( "$" )) {

               error = 1;
            }
         }

         if (error != 0) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>The $ (dollar Sign) is not necessary and will result in an error from the IBS POS System.");
            out.println("<BR>Please remove all dollar signs from the Fee values.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }

      //
      // Parms valid - add or update the club in the database
      //
      try {

         mshipChange = 0;   // reset to default

         if (!parm.clubName.equals( "" )) {          // parms exist yet??

            oldClub = parm.clubName;                 // save for later

            //
            //  Check if Membership Types have changed,
            //  if so, inform the user to check restrictions, etc.
            //
            if (!parm.mship[0].equals( "" )) {        // if not first time here

               for (i=0; i<parm.MAX_Mships; i++) {

                  if (!mship[i].equals("") && !mship[i].equals( parm.mship[i] )) {
                     mshipChange = 1;     // indicate that a membership name has changed
                  }
               }
            }

            //
            // Now set the mship parms for each mship type specified (different table)
            //
            for (i=0; i<parm.MAX_Mships; i++) {

               found = false;
                 
               if (!mship[i].equals( "" )) {     // if mship type specified
                 
                  stmt = con.prepareStatement (
                          "SELECT days1 FROM mship5 WHERE mship = ? AND activity_id = ?");

                  stmt.clearParameters();        // clear the parms
                  stmt.setString(1, mship[i]);
                  stmt.setInt(2, sess_activity_id);
                  rs = stmt.executeQuery();      // execute the prepared stmt

                  if (rs.next()) {

                     found = true;       // mship already exists
                  }
                  stmt.close();

                  if (found == true) {       // if found, update it, else add it
                    
                     stmt = con.prepareStatement (
                        "UPDATE mship5 SET " +
                        "mpos = ?, mposc = ?, m9posc = ?, mshipItem = ?, mship9Item = ?, tflag = ? " +
                        "WHERE mship = ? AND activity_id = ?");

                     stmt.clearParameters();            // clear the parms
                     stmt.setString(1, mpos[i]);
                     stmt.setString(2, mposc[i]);
                     stmt.setString(3, m9posc[i]);
                     stmt.setString(4, mshipItem[i]);
                     stmt.setString(5, mship9Item[i]);
                     stmt.setString(6, tflag[i]);

                     stmt.setString(7, mship[i]);      // for this mship type
                     stmt.setInt(8, sess_activity_id);

                     stmt.executeUpdate();             // execute the prepared stmt
                     stmt.close();

                  } else {                   // mship not found - add it (init step 5 values) 

                     stmt = con.prepareStatement (
                        "INSERT INTO mship5 (mship, activity_id, mtimes, period, " +
                        "days1, days2, days3, days4, days5, days6, days7, " +
                        "advhrd1, advmind1, advamd1, advhrd2, advmind2, advamd2, advhrd3, advmind3, advamd3, " +
                        "advhrd4, advmind4, advamd4, advhrd5, advmind5, advamd5, " +
                        "advhrd6, advmind6, advamd6, advhrd7, advmind7, advamd7, " +
                        "mpos, mposc, m9posc, mshipItem, mship9Item, viewdays, tflag) " +
                        "VALUES (?,?,0,'', " +
                        "0,0,0,0,0,0,0, " +
                        "0,0,'',0,0,'',0,0,'', " +
                        "0,0,'',0,0,'', " +
                        "0,0,'',0,0,'', " +
                        "?,?,?,?,?,30,?)");

                     stmt.clearParameters();        // clear the parms
                     stmt.setString(1, mship[i]);
                     stmt.setInt(2, sess_activity_id);
                     stmt.setString(3, mpos[i]);
                     stmt.setString(4, mposc[i]);
                     stmt.setString(5, m9posc[i]);
                     stmt.setString(6, mshipItem[i]);
                     stmt.setString(7, mship9Item[i]);
                     stmt.setString(8, tflag[i]);

                     stmt.executeUpdate();          // execute the prepared stmt
                     stmt.close();
                  }
               }
            }

            // only contine if club is setup and we found at least one guest type (prevents null errors)
            if (mship[0] != null) {

                String tmp_mships = "";

                for (i=0; i < parm.MAX_Mships; i++) {

                    if (mship[i] != null && !mship[i].equals("")) {
                        tmp_mships += "\"" + mship[i] + "\",";
                    }
                }

                // this check is probably not needed any longer with the null check above
                if (tmp_mships.length() > 0 ) {

                    tmp_mships = tmp_mships.substring(0, tmp_mships.length()-1); // trim the last comma

                    stmt = con.prepareStatement("DELETE FROM mship5 WHERE activity_id = ? AND mship NOT IN (" + tmp_mships + ")");
                    stmt.clearParameters();
                    stmt.setInt(1, sess_activity_id);

                    stmt.executeUpdate();
                    stmt.close();

                }

            }

         } else {

            //
            // Parms do not exist - ERROR
            //
            out.println(SystemUtils.HeadTitle("Sequence Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Setup Sequence Error</H3>");
            out.println("<BR><BR>Sorry, a sequence error has occurred and we cannot continue.");
            out.println("<BR>You must set the Club Options before the Membership Types.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;

      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle2("Proshop - Club Setup Step 4"));

      if (mshipChange == 0) {
         if (req.getParameter("Done") != null) {    // exit if Done specified
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_announce\">");
         } else {
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Proshop_club?step5=yes\">");
         }
      }
      out.println("</HEAD>");
      out.println("<BODY>");
        
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

      out.println("<CENTER>");
      out.println("<BR><BR><H3>Membership Type Setup Complete</H3>");
      out.println("Thank you, the membership types have been updated.");

      // don't show the warning if there are multiple activites since they were not able to change them anyways
      if ( mshipChange == 1 && getActivity.getActivityCount(con) < 2 ) {      // if a Membership name changed

         out.println("<br><br><font size=\"4\"><b>*** Warning ***</b>");
         out.println("</font>");
         out.println("<br><br>One or more of the Membership Types have changed.  You must check all");
         out.println("<br>existing Member Restrictions (in System Configuration).");
         out.println("<br>Any restriction that references, or should reference, this Membership Type");
         out.println("<br>MUST be updated and re-submitted (even if you do not have to make a change).");
         out.println("<br>Failure to do this could result in an inoperative Member Restriction.");
         out.println("<br><br><b>Also,</b> you may have to adjust the values on the next configuration page.");
         out.println("<br><br>Please contact your ForeTees Representative or ForeTees Support");
         out.println("<br>if you have any questions or need assistance.");
      }

      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      if (req.getParameter("Done") != null) {    // exit if Done specified
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      } else {
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_club\">");
         out.println("<input type=\"hidden\" name=\"step5\" value=\"yes\">");
      }
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</CENTER></BODY></HTML>");

   }  // end of step 4


   //
   //  Continue based on which step we are on.  These parms are broken into 5 pages to simplify the
   //  process.
   //
   if (req.getParameter("step5") != null) {

      //
      //  This is step 5 - process the 5th page of options  (Membership Options)
      //
      boolean doDefault = false;

      //
      // Get all the parameters entered
      //
      if (req.getParameter("doDefault") != null) {

         doDefault = true;
           
         i = 0;             // do first mship type next
        
      } else {

         if (req.getParameter("index") != null) {

            index = req.getParameter("index");    // next mship to display

            try {
               i = Integer.parseInt(index);
            }
            catch (NumberFormatException e) {
               i = 99;    // done
            }

         } else {

            i = 1;          // first time here - start with one
         }
      }
        
      if (req.getParameter("mship") != null) {            // if mship specified

         mship1 = req.getParameter("mship");               // mship type

         if (req.getParameter("mtimes") != null) {

            smtimes = req.getParameter("mtimes");      // number of rounds per 'period' mship can play
         }
         if (req.getParameter("period") != null) {

            period = req.getParameter("period");       // period for mship times (week, month, year)
         }
         if (req.getParameter("viewdays") != null) {

            sviewdays = req.getParameter("viewdays");       // days in advance to view tee sheets
         }
         if (req.getParameter("days1") != null) {

            smdays1 = req.getParameter("days1");       // days in advance Sunday
         }
         if (req.getParameter("days2") != null) {

            smdays2 = req.getParameter("days2");       // days in advance Monday
         }
         if (req.getParameter("days3") != null) {

            smdays3 = req.getParameter("days3");       // days in advance Tuesday
         }
         if (req.getParameter("days4") != null) {

            smdays4 = req.getParameter("days4");       // days in advance Wednesday
         }
         if (req.getParameter("days5") != null) {

            smdays5 = req.getParameter("days5");       // days in advance Thursday
         }
         if (req.getParameter("days6") != null) {

            smdays6 = req.getParameter("days6");       // days in advance Friday
         }
         if (req.getParameter("days7") != null) {

            smdays7 = req.getParameter("days7");       // days in advance Saturday
         }
         //
         //  Get all the time values for the days in advance (one set per day, per memship type)
         //
         if (req.getParameter("advamd1") != null) {

            advamd1 = req.getParameter("advamd1");
         }
         if (req.getParameter("advamd2") != null) {

            advamd2 = req.getParameter("advamd2");
         }
         if (req.getParameter("advamd3") != null) {

            advamd3 = req.getParameter("advamd3");
         }
         if (req.getParameter("advamd4") != null) {

            advamd4 = req.getParameter("advamd4");
         }
         if (req.getParameter("advamd5") != null) {

            advamd5 = req.getParameter("advamd5");
         }
         if (req.getParameter("advamd6") != null) {

            advamd6 = req.getParameter("advamd6");
         }
         if (req.getParameter("advamd7") != null) {

            advamd7 = req.getParameter("advamd7");
         }
         //
         //   get the hour values
         //
         if (req.getParameter("advhrd1") != null) {

            tmp = req.getParameter("advhrd1");
            advhrd1 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advhrd2") != null) {

            tmp = req.getParameter("advhrd2");
            advhrd2 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advhrd3") != null) {

            tmp = req.getParameter("advhrd3");
            advhrd3 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advhrd4") != null) {

            tmp = req.getParameter("advhrd4");
            advhrd4 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advhrd5") != null) {

            tmp = req.getParameter("advhrd5");
            advhrd5 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advhrd6") != null) {

            tmp = req.getParameter("advhrd6");
            advhrd6 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advhrd7") != null) {

            tmp = req.getParameter("advhrd7");
            advhrd7 = Integer.parseInt(tmp);
         }
         //
         //   get the minute values
         //
         if (req.getParameter("advmind1") != null) {

            tmp = req.getParameter("advmind1");
            advmind1 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advmind2") != null) {

            tmp = req.getParameter("advmind2");
            advmind2 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advmind3") != null) {

            tmp = req.getParameter("advmind3");
            advmind3 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advmind4") != null) {

            tmp = req.getParameter("advmind4");
            advmind4 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advmind5") != null) {

            tmp = req.getParameter("advmind5");
            advmind5 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advmind6") != null) {

            tmp = req.getParameter("advmind6");
            advmind6 = Integer.parseInt(tmp);
         }
         if (req.getParameter("advmind7") != null) {

            tmp = req.getParameter("advmind7");
            advmind7 = Integer.parseInt(tmp);
         }
      }

      //
      // Convert string values to numeric values
      //
      try {

         mtimes = Integer.parseInt(smtimes);    // membership type times - max rounds
         mdays1 = Integer.parseInt(smdays1);        // days in advance
         mdays2 = Integer.parseInt(smdays2);
         mdays3 = Integer.parseInt(smdays3);
         mdays4 = Integer.parseInt(smdays4);
         mdays5 = Integer.parseInt(smdays5);
         mdays6 = Integer.parseInt(smdays6);
         mdays7 = Integer.parseInt(smdays7);
         viewdays = Integer.parseInt(sviewdays);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      // Verify the parameters - 
      //
      //        days must be less than 31
      //
      if (!club.equals( "sauconvalleycc" )) {       // do not verify for saucon (they use 365)
        
         if ((mdays1 > 365) || (mdays2 > 365) || (mdays3 > 365) || (mdays4 > 365) ||
             (mdays5 > 365) || (mdays6 > 365) || (mdays7 > 365)) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>One or more of the Days in Advance parameters are invalid.");
            out.println("<BR><BR>You cannot specify a value greater than 365.");
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }

      if (viewdays > 365) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>The View Tee Sheets value is invalid.");
         out.println("<BR><BR>You cannot specify a value greater than 365.");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if ((mdays1 > viewdays) || (mdays2 > viewdays) || (mdays3 > viewdays) || (mdays4 > viewdays) ||
          (mdays5 > viewdays) || (mdays6 > viewdays) || (mdays7 > viewdays)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>One or more of the Days in Advance values are greater than the View Tee Sheets value.");
         out.println("<BR><BR>The View Tee Sheets value must be greater than, or equal to, the largest Days in Advance value..");
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
          

      //
      // Parms valid - update the mship parms in the mship5 table (1 entry per mship type)
      //
      try {

         if (doDefault == true) {     // if setting all default values

            stmt = con.prepareStatement (
               "UPDATE mship5 SET " +
               "mtimes=?, period=?, " +
               "days1=?, days2=?, days3=?, days4=?, days5=?, days6=?, days7=?, " +
               "advhrd1=?, advmind1=?, advamd1=?, advhrd2=?, advmind2=?, advamd2=?, " +
               "advhrd3=?, advmind3=?, advamd3=?, " +
               "advhrd4=?, advmind4=?, advamd4=?, advhrd5=?, advmind5=?, advamd5=?, " +
               "advhrd6=?, advmind6=?, advamd6=?, advhrd7=?, advmind7=?, advamd7=?, viewdays = ? " +
               "WHERE activity_id = ?");

            stmt.clearParameters();            // clear the parms
            stmt.setInt(1, mtimes);
            stmt.setString(2, period);
            stmt.setInt(3, mdays1);
            stmt.setInt(4, mdays2);
            stmt.setInt(5, mdays3);
            stmt.setInt(6, mdays4);
            stmt.setInt(7, mdays5);
            stmt.setInt(8, mdays6);
            stmt.setInt(9, mdays7);
            stmt.setInt(10, advhrd1);
            stmt.setInt(11, advmind1);
            stmt.setString(12, advamd1);
            stmt.setInt(13, advhrd2);
            stmt.setInt(14, advmind2);
            stmt.setString(15, advamd2);
            stmt.setInt(16, advhrd3);
            stmt.setInt(17, advmind3);
            stmt.setString(18, advamd3);
            stmt.setInt(19, advhrd4);
            stmt.setInt(20, advmind4);
            stmt.setString(21, advamd4);
            stmt.setInt(22, advhrd5);
            stmt.setInt(23, advmind5);
            stmt.setString(24, advamd5);
            stmt.setInt(25, advhrd6);
            stmt.setInt(26, advmind6);
            stmt.setString(27, advamd6);
            stmt.setInt(28, advhrd7);
            stmt.setInt(29, advmind7);
            stmt.setString(30, advamd7);
            stmt.setInt(31, viewdays);

            stmt.setInt(32, sess_activity_id);

            stmt.executeUpdate();             // execute the prepared stmt
            stmt.close();
              
         } else {

            if (!mship1.equals( "" )) {     // if mship type specified

               stmt = con.prepareStatement (
                  "UPDATE mship5 SET " +
                  "mtimes=?, period=?, " +
                  "days1=?, days2=?, days3=?, days4=?, days5=?, days6=?, days7=?, " +
                  "advhrd1=?, advmind1=?, advamd1=?, advhrd2=?, advmind2=?, advamd2=?, " +
                  "advhrd3=?, advmind3=?, advamd3=?, " +
                  "advhrd4=?, advmind4=?, advamd4=?, advhrd5=?, advmind5=?, advamd5=?, " +
                  "advhrd6=?, advmind6=?, advamd6=?, advhrd7=?, advmind7=?, advamd7=?, viewdays = ? " +
                  "WHERE mship = ? AND activity_id = ?");

               stmt.clearParameters();            // clear the parms
               stmt.setInt(1, mtimes);
               stmt.setString(2, period);
               stmt.setInt(3, mdays1);
               stmt.setInt(4, mdays2);
               stmt.setInt(5, mdays3);
               stmt.setInt(6, mdays4);
               stmt.setInt(7, mdays5);
               stmt.setInt(8, mdays6);
               stmt.setInt(9, mdays7);
               stmt.setInt(10, advhrd1);
               stmt.setInt(11, advmind1);
               stmt.setString(12, advamd1);
               stmt.setInt(13, advhrd2);
               stmt.setInt(14, advmind2);
               stmt.setString(15, advamd2);
               stmt.setInt(16, advhrd3);
               stmt.setInt(17, advmind3);
               stmt.setString(18, advamd3);
               stmt.setInt(19, advhrd4);
               stmt.setInt(20, advmind4);
               stmt.setString(21, advamd4);
               stmt.setInt(22, advhrd5);
               stmt.setInt(23, advmind5);
               stmt.setString(24, advamd5);
               stmt.setInt(25, advhrd6);
               stmt.setInt(26, advmind6);
               stmt.setString(27, advamd6);
               stmt.setInt(28, advhrd7);
               stmt.setInt(29, advmind7);
               stmt.setString(30, advamd7);
               stmt.setInt(31, viewdays);

               stmt.setString(32, mship1);      // for this mship type
               stmt.setInt(33, sess_activity_id);

               stmt.executeUpdate();             // execute the prepared stmt
               stmt.close();
            }
         }
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, there was an error while updating the database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      // Database updated - prompt user to contniue with next mship if more to process
      //
      i++;        // next index value for next mship type
        
      out.println(SystemUtils.HeadTitle("Proshop Club Setup Complete"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
      out.println("<CENTER>");
      out.println("<BR><BR><H3>Membership Type Options Have Been Updated</H3>");
      out.println("Thank you, the club options have been updated for " +mship1+ ".");
      out.println("<BR><BR>");
      if (i < parm.MAX_Mships+1) {
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_club\" method=\"get\" target=\"bot\">");
         out.println("<input type=\"hidden\" name=\"step5\" value=\"yes\">");
         out.println("<input type=\"hidden\" name=\"index\" value=\"" +i+ "\">");
         out.println("<input type=\"submit\" value=\"Next\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form>");
         out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
      }
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");

   }  // end of step 5

 }   // end of doPost   


 // ********************************************************************
 //  Process Step 2 of doGet - Guest Types
 // ********************************************************************

 private void doStep2(HttpServletRequest req, PrintWriter out, HttpSession session, parmClub parm, Connection con)
         throws ServletException, IOException {

   Statement stmt = null;
   ResultSet rs = null;


    // Define parms
   int i = 0;
   int guestdb = 0;

   String posType = "";         // POS system type

   boolean showPOS = true;

   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   if (sess_activity_id != 0) showPOS = false;

   //
   //  arrays to hold guest type parms
   //
   ArrayList<String> guest = new ArrayList<String>();
   ArrayList<String> gstItem = new ArrayList<String>();
   ArrayList<String> gst9Item = new ArrayList<String>();
   ArrayList<String> gpos = new ArrayList<String>();
   ArrayList<String> g9pos = new ArrayList<String>();
   
   ArrayList<Integer> gOpt = new ArrayList<Integer>();
   ArrayList<Integer> gDb = new ArrayList<Integer>();
   ArrayList<Integer> gRev = new ArrayList<Integer>();
   
/*
   String [] guest = new String [parm.MAX_Guests+1];        // array to hold the guest names
   String [] gstItem = new String [parm.MAX_Guests+1];
   String [] gst9Item = new String [parm.MAX_Guests+1];
   String [] gpos = new String [parm.MAX_Guests+1];
   String [] g9pos = new String [parm.MAX_Guests+1];
   int [] gOpt = new int [parm.MAX_Guests+1];               // array to hold the guest options
   int [] gDb = new int [parm.MAX_Guests+1];                // array to hold the guestdb options
   int [] gRev = new int [parm.MAX_Guests+1];               // array to hold the revenue options

   //
   //  init the string arrays
   //
   for (i=0; i<37; i++) {
      guest[i] = "";
      gstItem[i] = "";
      gst9Item[i] = "";
      gpos[i] = "";
      g9pos[i] = "";
   }
*/


   //
   // Get existing parms if they exist
   //
   try {

      stmt = con.createStatement();

      rs = stmt.executeQuery("SELECT posType, guestdb FROM club5 WHERE clubName != ''");

      if (rs.next()) {
         posType = rs.getString("posType");
         guestdb = rs.getInt("guestdb");
      }
      stmt.close();

      //
      // Now get the guest parms for each guest type specified (different table)
      //
      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT * FROM guest5 WHERE activity_id = ? LIMIT " + parm.MAX_Guests);

      pstmt1.clearParameters();
      pstmt1.setInt(1, sess_activity_id);
      rs = pstmt1.executeQuery();

      //i = 0;

      while (rs.next()) {

          guest.add(rs.getString("guest").trim());
          gOpt.add(rs.getInt("gOpt"));
          gDb.add(rs.getInt("use_guestdb"));
          gpos.add(rs.getString("gpos"));
          g9pos.add(rs.getString("g9pos"));
          gstItem.add(rs.getString("gstItem"));
          gst9Item.add(rs.getString("gst9Item"));
          gRev.add(rs.getInt("revenue"));

/*
          guest[i] = rs.getString("guest").trim();
          gOpt[i] = rs.getInt("gOpt");
          gDb[i] = rs.getInt("use_guestdb");
          gpos[i] = rs.getString("gpos");
          g9pos[i] = rs.getString("g9pos");
          gstItem[i] = rs.getString("gstItem");
          gst9Item[i] = rs.getString("gst9Item");
          gRev[i] = rs.getInt("revenue");

          i++;
 */
      }
      pstmt1.close();
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   // Add atleast one empty entry
   //
   guest.add("");
   gOpt.add(0);
   gDb.add(0);
   gpos.add("");
   g9pos.add("");
   gstItem.add("");
   gst9Item.add("");
   gRev.add(0);
   int display_num = parm.MAX_Guests + 1;

   //
   //  Build the HTML page to solicit new parms
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Club Setup"));
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function cursor() { document.forms['f'].guest1.focus(); }");
       out.println("// -->");
       out.println("</script>");
    out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<b>Club Setup - Guest Types</b><br>");
   out.println("<br>Click on the '?' for a description of each item.");
   out.println("<br>Click on 'Continue' to process the changes and go to the next page of options.");
   out.println("<br>Click on 'Done' to process the changes and exit.");
   out.println("<br>Click on 'Cancel' to exit without changes.");
   out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

   //
   //  This is step 2 - dsiplay the 2nd page of options
   //
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_club\" method=\"post\" target=\"bot\" name=\"f\">");
   out.println("<input type=\"hidden\" name=\"step2\" value=\"yes\">");
   out.println("<tr valign=\"top\">");
      out.println("<td align=\"center\"><br><br><br>");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_guest.htm', 'newwindow', 'Height=480, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");

      out.println("<td align=\"left\" width=\"300px\"valign=\"top\">");
         out.println("<font size=\"2\"><br><br><br>");

         out.println("Enter all names you would like to use to &nbsp;&nbsp;");
         if (sess_activity_id == 0) {
            out.println("<br>specify <b>Guests</b> on the tee sheets: &nbsp;&nbsp;");
            out.println("<br><br>Select <b>'Yes'</b> for the <b>Pro Only</b> option if you want the guest name to be used " +
                    "only by Golf Shop staff. (If 'Yes', members will not be able to select this guest name.)");
         } else {
            out.println("<br>specify <b>Guests</b> on the time sheets: &nbsp;&nbsp;");
            out.println("<br><br>Select <b>'Yes'</b> for the <b>Pro Only</b> option if you want the guest name to be used " +
                    "only by Pro Shop staff. (If 'Yes', members will not be able to select this guest name.)");
         }

         if (guestdb == 1) {
             out.println("<br><br>Select <b>'Yes'</b> for the <b>Use Guest Tracking</b> option if you want the guest name to be used " +
                     "with the guest tracking feature. (If 'Yes', members will be prompted to enter guest information, or select a " +
                     "guest from those they have added previously)");
         }

         out.println("<br><br>Select <b>'Yes'</b> for the <b>Revenue</b> option if this " +
                 "guest type results in revenue (they are charged). " +
                 "This is only used for reporting purposes.");

         if (showPOS) {
             if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified and PSK

                out.println("<br><br>Specify the POS system's <b>Product Charge Code</b> for each appropriate Guest Type.");
             } else {
                if (posType.equals( "IBS" )) {

                   out.println("<br><br>Specify the <b>Sales Item Codes</b> and the associated <b>Fee (i.e. 45.50)</b> " +
                           "for the selected POS System for each appropriate Guest Type.");

                } else {
                   if (!posType.equals( "" ) && !posType.equals( "None" )) {

                      out.println("<br><br>Specify the <b>Sales Item Codes</b> " +
                              "for the selected POS System for each appropriate Guest Type.");
                   }
                }
             }
         }
         out.println("<br><br><b>Note:</b> Start with #1 and enter name values " +
                 "in order, do not leave an empty box in " +
                 "between names.");

         out.println("<br><br><b>Help:</b> Click on the ? to the left " +
                 "for more information on these fields.");
         out.println("</font>");

         /*   test this
         if (posType.equals( "Pro-ShopKeeper" )) {      // if a POS system was specified
            out.println("</td><td width=\"540\" align=\"left\"><font size=\"2\">");
         } else {
            if (posType.equals( "IBS" )) {
                  out.println("</td><td width=\"600\" align=\"left\"><font size=\"2\">");
            } else {
               if (!posType.equals( "" ) && !posType.equals( "None" )) {
                  out.println("</td><td width=\"500\" align=\"left\"><font size=\"2\">");
               } else {
                  out.println("</td><td width=\"330\" align=\"left\"><font size=\"2\">");
               }
            }
         }
         */
         out.println("</td><td align=\"left\"><font size=\"2\">");  // test this - no width option
         
         out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" style=\"font-size: 10pt\">");
         out.println("<b>");
         out.println("<tr>");
         out.println("<td></td>");
         out.println("<td align=\"center\">Guest Type</td>");
         out.println("<td align=\"center\">Pro Only?</td>");
         if (guestdb == 1) {
             out.println("<td align=\"center\">Use Guest<br>Tracking?</td>");
         }

         // Only display POS items for Golf for now
         if (showPOS) {
             
             out.println("<td align=\"center\">Revenue?</td>");

             if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified
                 out.println("<td align=\"center\">9 Hole<br>Chg Code</td>");
                 out.println("<td align=\"center\">18 Hole<br>Chg Code</td>");
             } else if (posType.equals( "IBS" )) {
                 out.println("<td align=\"center\">18 Hole<br>Item #</td>");
                 out.println("<td align=\"center\">18 Hole<br>Fee</td>");
                 out.println("<td align=\"center\">9 Hole<br>Item #</td>");
                 out.println("<td align=\"center\">9 Hole<br>Fee</td>");
             } else if (!posType.equals( "" ) && !posType.equals( "None" )) {
                 out.println("<td align=\"center\">18 Hole<br>Item #</td>");
                 out.println("<td align=\"center\">9 Hole<br>Item #</td>");
             }
         }
         
         out.println("</tr>");
         out.println("</b>");
           
         //
         //  Display all existing Guest Types
         //
         for (i=0; i < display_num; i++) {
             
             out.println("<div id=\"awmobject" + (i + 1) + "\">");
             out.println("<tr>");
             out.println("<td align=\"right\">" + (i + 1) + "</td>");

             out.println("<td><input type=\"text\" name=\"guest" + (i + 1) + "\" value=\"" + guest.get(i) + "\" size=\"15\" maxlength=\"20\"></td>");

             out.println("<td align=\"center\">");
                 out.println("<select size=\"1\" name=\"gOpt" + (i + 1) + "\">");
                 if (gOpt.get(i) == 0) {
                     out.println("<option selected value=\"No\">No</option>");
                 } else {
                     out.println("<option value=\"No\">No</option>");
                 }
                 if (gOpt.get(i) == 1) {
                     out.println("<option selected value=\"Yes\">Yes</option>");
                 } else {
                     out.println("<option value=\"Yes\">Yes</option>");
                 }
                 out.println("</select>");
             out.println("</td>");

             if (guestdb == 1) {
                 out.println("<td align=\"center\">");
                    out.println("<select size=\"1\" name=\"gDb" + (i + 1) + "\">");
                    if (gDb.get(i) == 0) {
                        out.println("<option selected value=\"No\">No</option>");
                    } else {
                        out.println("<option value=\"No\">No</option>");
                    }
                    if (gDb.get(i) == 1) {
                        out.println("<option selected value=\"Yes\">Yes</option>");
                    } else {
                        out.println("<option value=\"Yes\">Yes</option>");
                    }
                    out.println("</select>");
                 out.println("</td>");
             } else {
                 out.println("<input type=\"hidden\" name=\"gDb" + (i + 1) + "\" value=\"0\">");
             }

             // Only display POS items for Golf for now
             if (showPOS) {
                 
                 out.println("<td align=\"center\">");
                     out.println("<select size=\"1\" name=\"gRev" + (i + 1) + "\">");
                     if (gRev.get(i) == 0) {
                         out.println("<option selected value=\"No\">No</option>");
                     } else {
                         out.println("<option value=\"No\">No</option>");
                     }
                     if (gRev.get(i) == 1) {
                         out.println("<option selected value=\"Yes\">Yes</option>");
                     } else {
                         out.println("<option value=\"Yes\">Yes</option>");
                     }
                     out.println("</select>");
                 out.println("</td>");

                 if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified
                     out.println("<td><input type=\"text\" name=\"g9pos" + (i + 1) + "\" value=\"" + g9pos.get(i) + "\" size=\"15\" maxlength=\"30\"></td>");
                     out.println("<td><input type=\"text\" name=\"gpos" + (i + 1) + "\" value=\"" + gpos.get(i) + "\" size=\"15\" maxlength=\"30\"></td>");
                 } else if (posType.equals( "IBS" )) {
                     out.println("<td><input type=\"text\" name=\"gstItem" + (i + 1) + "\" value=\"" + gstItem.get(i) + "\" size=\"5\" maxlength=\"20\"></td>");
                     out.println("<td><input type=\"text\" name=\"gpos" + (i + 1) + "\" value=\"" + gpos.get(i) + "\" size=\"6\" maxlength=\"6\"></td>");
                     out.println("<td><input type=\"text\" name=\"gst9Item" + (i + 1) + "\" value=\"" + gst9Item.get(i) + "\" size=\"5\" maxlength=\"20\"></td>");
                     out.println("<td><input type=\"text\" name=\"g9pos" + (i + 1) + "\" value=\"" + g9pos.get(i) + "\" size=\"6\" maxlength=\"6\"></td>");
                 } else if (!posType.equals( "" ) && !posType.equals( "None" )) {
                     out.println("<td><input type=\"text\" name=\"gstItem" + (i + 1) + "\" value=\"" + gstItem.get(i) + "\" size=\"5\" maxlength=\"20\"></td>");
                     out.println("<td><input type=\"text\" name=\"gst9Item" + (i + 1) + "\" value=\"" + gst9Item.get(i) + "\" size=\"5\" maxlength=\"20\"></td>");
                 }
             }

             out.println("</tr>");

         } // end of while loop

         out.println("<tr><td colspan=* align=center>");
         out.println("</td></tr>");

         out.println("</div></table>");

         out.println("<center>");
         out.println("<input type=\"hidden\" name=\"guests\" value=\"" + display_num + "\">");
         out.println("<input type=\"submit\" name=\"Add\" value=\"Add / Update\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("<br><font size=\"1\">(submit changes & stay here)</font>");
         out.println("</center>");

         out.println("</font></td>");
         out.println("</tr>");
         out.println("</table>");
         
         out.println("<BR><table border=\"0\">");
         out.println("<tr>");
         out.println("<td align=\"center\" width=\"150\">");
         out.println("<font size=\"1\">");
         out.println("<input type=\"submit\" value=\"Done\" name=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("<br>(submit changes & exit)");
         out.println("</font></td>");
         out.println("<td align=\"center\" width=\"150\">");
         out.println("<font size=\"1\">");
         out.println("<input type=\"submit\" value=\"Continue\" name=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("<br>(submit changes & next page)");
         out.println("</font></td></form>");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<td align=\"center\" width=\"150\">");
         out.println("<font size=\"1\">");
         out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("<br>(no changes & exit)");
         out.println("</font></td></form></tr></table>");
         out.println("</center></font></body></html>");

 }   // end of doStep 2


 // ********************************************************************
 //  Process Step 3 of doGet - Member Types
 // ********************************************************************

 private void doStep3(HttpServletRequest req, PrintWriter out, HttpSession session, parmClub parm, Connection con)
         throws ServletException, IOException {

   Statement stmt = null;
   ResultSet rs = null;


   // Define parms
   int i = 0;
   String [] mem = new String [parm.MAX_Mems+1];

   //
   //  init the string array
   //
   for (i=0; i<parm.MAX_Mems+1; i++) {
      mem[i] = "";
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Get existing parms if they exist
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         mem[1] = rs.getString("mem1");
         mem[2] = rs.getString("mem2");
         mem[3] = rs.getString("mem3");
         mem[4] = rs.getString("mem4");
         mem[5] = rs.getString("mem5");
         mem[6] = rs.getString("mem6");
         mem[7] = rs.getString("mem7");
         mem[8] = rs.getString("mem8");
         mem[9] = rs.getString("mem9");
         mem[10] = rs.getString("mem10");
         mem[11] = rs.getString("mem11");
         mem[12] = rs.getString("mem12");
         mem[13] = rs.getString("mem13");
         mem[14] = rs.getString("mem14");
         mem[15] = rs.getString("mem15");
         mem[16] = rs.getString("mem16");
         mem[17] = rs.getString("mem17");
         mem[18] = rs.getString("mem18");
         mem[19] = rs.getString("mem19");
         mem[20] = rs.getString("mem20");
         mem[21] = rs.getString("mem21");
         mem[22] = rs.getString("mem22");
         mem[23] = rs.getString("mem23");
         mem[24] = rs.getString("mem24");
      }
      stmt.close();
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Build the HTML page to solicit new parms
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Club Setup"));
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function cursor() { document.forms['f'].mem1.focus(); }");
       out.println("// -->");
       out.println("</script>");
    out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<b>Club Setup - Member Types</b><br>");
   out.println("<br>Click on the '?' for a description of each item.");
   out.println("<br>Click on 'Continue' to process the changes and go to the next page of options.");
   out.println("<br>Click on 'Done' to process the changes and exit.");
   out.println("<br>Click on 'Cancel' to exit without changes.");
   out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<br><table border=\"2\" bgcolor=\"#F5F5DC\">");

   //
   //  This is step 3 - display the 3rd page of options
   //
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_club\" method=\"post\" target=\"bot\" name=\"f\">");
   out.println("<input type=\"hidden\" name=\"step3\" value=\"yes\">");

   out.println("<tr>");
      out.println("<td align=\"center\" valign=\"top\"><br><br><br>");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_mem.htm', 'newwindow', 'Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");

      out.println("<td align=\"right\" valign=\"top\">");
         out.println("<font size=\"2\"><br><br><br>");
         out.println("Enter all name values to use for <b>Member Types</b>: &nbsp;&nbsp;");
         out.println("<br>(Must specify at least one member type.)&nbsp;&nbsp;");
         out.println("<br><br><br><b>Note:</b> Start with #1 and enter name values in order,&nbsp;&nbsp;");
         out.println("<br>do not leave an empty box in between names.&nbsp;&nbsp;");
         out.println("</font>");

      out.println("</td><td align=\"left\" width=\"190\"><font size=\"2\">");
         out.println("&nbsp;&nbsp;1&nbsp;&nbsp;<input type=\"text\" name=\"mem1\" value=\"" + mem[1] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;2&nbsp;&nbsp;<input type=\"text\" name=\"mem2\" value=\"" + mem[2] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;3&nbsp;&nbsp;<input type=\"text\" name=\"mem3\" value=\"" + mem[3] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;4&nbsp;&nbsp;<input type=\"text\" name=\"mem4\" value=\"" + mem[4] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;5&nbsp;&nbsp;<input type=\"text\" name=\"mem5\" value=\"" + mem[5] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;6&nbsp;&nbsp;<input type=\"text\" name=\"mem6\" value=\"" + mem[6] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;7&nbsp;&nbsp;<input type=\"text\" name=\"mem7\" value=\"" + mem[7] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;8&nbsp;&nbsp;<input type=\"text\" name=\"mem8\" value=\"" + mem[8] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;&nbsp;9&nbsp;&nbsp;<input type=\"text\" name=\"mem9\" value=\"" + mem[9] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;10&nbsp;&nbsp;<input type=\"text\" name=\"mem10\" value=\"" + mem[10] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;11&nbsp;&nbsp;<input type=\"text\" name=\"mem11\" value=\"" + mem[11] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;12&nbsp;&nbsp;<input type=\"text\" name=\"mem12\" value=\"" + mem[12] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;13&nbsp;&nbsp;<input type=\"text\" name=\"mem13\" value=\"" + mem[13] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;14&nbsp;&nbsp;<input type=\"text\" name=\"mem14\" value=\"" + mem[14] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;15&nbsp;&nbsp;<input type=\"text\" name=\"mem15\" value=\"" + mem[15] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;16&nbsp;&nbsp;<input type=\"text\" name=\"mem16\" value=\"" + mem[16] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;17&nbsp;&nbsp;<input type=\"text\" name=\"mem17\" value=\"" + mem[17] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;18&nbsp;&nbsp;<input type=\"text\" name=\"mem18\" value=\"" + mem[18] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;19&nbsp;&nbsp;<input type=\"text\" name=\"mem19\" value=\"" + mem[19] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;20&nbsp;&nbsp;<input type=\"text\" name=\"mem20\" value=\"" + mem[20] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;21&nbsp;&nbsp;<input type=\"text\" name=\"mem21\" value=\"" + mem[21] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;22&nbsp;&nbsp;<input type=\"text\" name=\"mem22\" value=\"" + mem[22] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;23&nbsp;&nbsp;<input type=\"text\" name=\"mem23\" value=\"" + mem[23] + "\" size=\"20\" maxlength=\"30\">");
         out.println("<br>&nbsp;24&nbsp;&nbsp;<input type=\"text\" name=\"mem24\" value=\"" + mem[24] + "\" size=\"20\" maxlength=\"30\">");
         out.println("</font>");
      out.println("</td>");
   out.println("</tr>");
   out.println("</table>");

   out.println("<BR><table border=\"0\">");
   out.println("<tr>");
   out.println("<td align=\"center\" width=\"150\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Done\" name=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(submit changes & exit)");
   out.println("</font></td>");
   out.println("<td align=\"center\" width=\"150\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Continue\" name=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(submit changes & next page)");
   out.println("</font></td></form>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<td align=\"center\" width=\"150\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(no changes & exit)");
   out.println("</font></td></form></tr></table>");
   out.println("</center></font></body></html>");

 }   // end of doStep 3


 // ********************************************************************
 //  Process Step 4 of doGet - Membership Types
 // ********************************************************************

 private void doStep4(HttpServletRequest req, PrintWriter out, HttpSession session, parmClub parm, Connection con)
         throws ServletException, IOException {

   Statement stmt = null;
   ResultSet rs = null;


   // Define parms
   int i = 0;
   String posType = "";                   // POS system type
   boolean showPOS = true;
   boolean allowModify = true;
   String [] mship = new String [parm.MAX_Mships+1];     // membership types (golf, tennis, etc.)
   String [] mpos = new String [parm.MAX_Mships+1];      // POS Charge Classes for membership types
   String [] mposc = new String [parm.MAX_Mships+1];     // POS Charge Codes for membership types (charge per round for non-golf mems)
   String [] m9posc = new String [parm.MAX_Mships+1];
   String [] mshipItem = new String [parm.MAX_Mships+1];
   String [] mship9Item = new String [parm.MAX_Mships+1];
   String [] tflag = new String [parm.MAX_Mships+1];
     
   // Get activity_id from session
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   if (sess_activity_id != 0) showPOS = false;
   if (getActivity.getActivityCount(con) > 1) allowModify = false;
   
   //
   //  init the string arrays
   //
   for (i=0; i<parm.MAX_Mships+1; i++) {
      mship[i] = "";
      mpos[i] = "";
      m9posc[i] = "";
      mshipItem[i] = "";
      mship9Item[i] = "";
      tflag[i] = "";
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Get existing parms if they exist
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM club5 WHERE clubName != ''");

      if (rs.next()) {
         posType = rs.getString("posType");
      }
      stmt.close();

      //
      // Now get the mship parms for each mship type specified (different table)
      //
      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT * FROM mship5 WHERE activity_id = ? LIMIT " + parm.MAX_Mships);

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setInt(1, sess_activity_id);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      i = 0;
      
      while (rs.next()) {

          mship[i] = rs.getString("mship").trim();
          mpos[i] = rs.getString("mpos");
          mposc[i] = rs.getString("mposc");
          m9posc[i] = rs.getString("m9posc");
          mshipItem[i] = rs.getString("mshipItem");
          mship9Item[i] = rs.getString("mship9Item");
          tflag[i] = rs.getString("tflag");

          i++;
      }
      pstmt1.close();
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Build the HTML page to solicit new parms
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Club Setup"));
       out.println("<script type=\"text/javascript\">");
       out.println("<!--");
       out.println("function cursor() { document.forms['f'].mship1.focus(); }");
       out.println("// -->");
       out.println("</script>");
    out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<b>Club Setup - Membership Types</b><br>");
   out.println("<br>Click on the '?' for a description of each item.");
   out.println("<br>Click on 'Continue' to process the changes and go to the next page of options.");
   out.println("<br>Click on 'Done' to process the changes and exit.");
   out.println("<br>Click on 'Cancel' to exit without changes.");
   out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<br><table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

   //
   //  This is step 4 - display the 4th page of options
   //
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_club\" method=\"post\" target=\"bot\" name=\"f\">");
   out.println("<input type=\"hidden\" name=\"step4\" value=\"yes\">");

   out.println("<tr>");
      out.println("<td align=\"center\" valign=\"top\"><br><br><br>");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_mship.htm', 'newwindow', 'Height=220, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");

      out.println("<td align=\"left\" width=\"300\" valign=\"top\">");
         out.println("<font size=\"2\">");

         // If more than one activity (golf included) configured, Admin side must be used to add/remove/name-change guest types, so instruct differently based on the situation
         if (allowModify) {
             out.println("Enter all name values to use for <b>Membership Types</b>: " +
                     "(Must specify at least one membership type.)&nbsp;&nbsp;");
         } else {
             out.println("<b>*NOTE* Since this club has more than one activity configured, please log in as Admin " +
                     "to add, remove, or change the name of any Membership Types.");
             out.println("<br><br>All other options will apply ONLY to the current activity</b>");
         }

         if (sess_activity_id == 0) {
            out.println("<br><br>If you wish to <b>Flag</b> these members on the proshop tee sheet for identification " +
                    "purposes, then enter the characters(s) to be used. These characters, preceeded by a space, will " +
                    "follow the member name on the proshop tee sheet.");
         } else {
            out.println("<br><br>If you wish to <b>Flag</b> these members on the proshop time sheet for identification " +
                    "purposes, then enter the characters(s) to be used. These characters, preceeded by a space, will " +
                    "follow the member name on the proshop time sheet.");
         }

         // Only display POS items for Golf for now
         if (showPOS) {
             if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified

                out.println("<br><br>Specify the POS system's <b>Product Charge Class</b> for each Membership Type, if appropriate.");

                out.println("<br><br>Specify the POS system's <b>Charge Code</b> (for rounds played) for each Membership Type, if appropriate.");
             } else {
                if (posType.equals( "IBS" )) {
                   out.println("<br><br>Specify the <b>Sales Item Codes</b>  the associated <b>Fee (i.e. 45.50)</b> " +
                           "for each Membership Type, if they are charged.");
                } else {
                   if (!posType.equals( "" ) && !posType.equals( "None" )) {
                      out.println("<br><br>Specify the <b>Sales Item Codes</b> for Round Fees for each Membership Type, if appropriate.");
                   }
                }
             }
         }
         out.println("<br><br><br><b>Notes:");
         out.println("<br>1. </b> If you <b>add or change a Flag</b>, the flag may not show up on all reservations immediately. Wait 1 day.");
         if (allowModify) {
            out.println("<br><br><b>2. </b> Start with #1 and enter name values in order, do not leave an empty box in between names.");
            out.println("</font>");
         }
         
         //  Headings
         
         out.println("</td><td align=\"left\"><font size=\"2\">");
         out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" style=\"font-size: 10pt\">");
         out.println("<tr>");
         out.println("<td></td>");
         out.println("<td align=\"center\"><b>Membership Type</b></td>");
         out.println("<td align=\"center\"><b>Flag</b></td>");

         // Only display POS items for Golf for now
         if (showPOS) {
             if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified
                 out.println("<td align=\"center\"><b>POS<br>Chg Class</b></td>");
                 out.println("<td align=\"center\"><b>9 Hole<br>Chg Code</b></td>");
                 out.println("<td align=\"center\"><b>18 Hole<br>Chg Code</b></td>");
             } else if (posType.equals( "IBS" )) {
                 out.println("<td align=\"center\"><b>18 Hole<br>Item #</b></td>");
                 out.println("<td align=\"center\"><b>18 Hole<br>Fee</b></td>");
                 out.println("<td align=\"center\"><b>9 Hole<br>Item #</b></td>");
                 out.println("<td align=\"center\"><b>9 Hole<br>Fee</b></td>");
             } else if (!posType.equals( "" ) && !posType.equals( "None" )) {
                 out.println("<td align=\"center\"><b>18 Hole<br>Item #</b></td>");
                 out.println("<td align=\"center\"><b>9 Hole<br>Item #</b></td>");
             }
         }
         out.println("</tr>");

            
         for (i=0; i<parm.MAX_Mships; i++) {     // do all 24 mship types

             out.println("<tr>");
             out.println("<td align=\"right\">" + (i + 1) + "</td>"); // visually start these at 1 not 0

             // If club has more than 1 activity (golf included) configured, they must make mship type additions, removals, and name changes on admin side instead of here
             if (allowModify) {
                 out.println("<td align=\"left\"><input type=\"text\" name=\"mship" + (i + 1) + "\" value=\"" + mship[i] + "\" size=\"15\" maxlength=\"30\"></td>");
             } else {
                 out.println("<td align=\"left\"><input type=\"text\" disabled name=\"mship" + (i + 1) + "\" value=\"" + mship[i] + "\" size=\"15\" maxlength=\"30\"></td>");
                 out.println("<input type=\"hidden\" name=\"mship" + (i + 1) + "\" value=\"" + mship[i] + "\">");
             }

             out.println("<td align=\"left\"><input type=\"text\" name=\"tflag" + (i + 1) + "\" value=\"" + tflag[i] + "\" size=\"4\" maxlength=\"4\"></td>");

             // Only display POS items for Golf for now
             if (showPOS) {
                 if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified
                     if (mpos[i] == null) {
                         mpos[i] = "";
                     }
                     if (m9posc[i] == null) {
                         m9posc[i] = "";
                     }
                     if (mposc[i] == null) {
                         mposc[i] = "";
                     }
                     out.println("<td align=\"left\"><input type=\"text\" name=\"mpos" + (i + 1) + "\" value=\"" + mpos[i] + "\" size=\"10\" maxlength=\"20\"></td>");
                     out.println("<td align=\"left\"><input type=\"text\" name=\"m9posc" + (i + 1) + "\" value=\"" + m9posc[i] + "\" size=\"10\" maxlength=\"20\"></td>");
                     out.println("<td align=\"left\"><input type=\"text\" name=\"mposc" + (i + 1) + "\" value=\"" + mposc[i] + "\" size=\"10\" maxlength=\"20\"></td>");
                 } else if (posType.equals( "IBS" )) {
                     if (mshipItem[i] == null) {
                         mshipItem[i] = "";
                     }
                     if (mship9Item[i] == null) {
                         mship9Item[i] = "";
                     }
                     if (m9posc[i] == null) {
                         m9posc[i] = "";
                     }
                     if (mposc[i] == null) {
                         mposc[i] = "";
                     }

                     out.println("<td align=\"left\"><input type=\"text\" name=\"mshipItem" + (i + 1) + "\" value=\"" + mshipItem[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                     out.println("<td align=\"left\"><input type=\"text\" name=\"mposc" + (i + 1) + "\" value=\"" + mposc[i] + "\" size=\"6\" maxlength=\"6\"></td>");
                     out.println("<td align=\"left\"><input type=\"text\" name=\"mship9Item" + (i + 1) + "\" value=\"" + mship9Item[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                     out.println("<td align=\"left\"><input type=\"text\" name=\"m9posc" + (i + 1) + "\" value=\"" + m9posc[i] + "\" size=\"6\" maxlength=\"6\"></td>");
                 } else if (!posType.equals( "" ) && !posType.equals( "None" )) {
                     if (mshipItem[i] == null) {
                         mshipItem[i] = "";
                     }
                     if (mship9Item[i] == null) {
                         mship9Item[i] = "";
                     }
                     out.println("<td align=\"left\"><input type=\"text\" name=\"mshipItem" + (i + 1) + "\" value=\"" + mshipItem[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                     out.println("<td align=\"left\"><input type=\"text\" name=\"mship9Item" + (i + 1) + "\" value=\"" + mship9Item[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                 }
             }
             out.println("</tr>");
         }

         out.println("</table>");
         out.println("</font>");
      out.println("</td>");
   out.println("</tr>");
   out.println("</table>");

   out.println("<BR><table border=\"0\">");
   out.println("<tr>");
   out.println("<td align=\"center\" width=\"150\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Done\" name=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(submit changes & exit)");
   out.println("</font></td>");
   out.println("<td align=\"center\" width=\"150\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Continue\" name=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(submit changes & next page)");
   out.println("</font></td></form>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<td align=\"center\" width=\"150\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(no changes & exit)");
   out.println("</font></td></form></tr></table>");
   out.println("</center></font></body></html>");

 }   // end of doStep 4


 // ********************************************************************
 //  Process Step 5 of doGet - Membership Options
 // ********************************************************************

 private void doStep5(HttpServletRequest req, PrintWriter out, HttpSession session, parmClub parm, Connection con)
         throws ServletException, IOException {

   PreparedStatement pstmt1 = null;
   Statement stmt = null;
   ResultSet rs = null;


    // Define parms
   String index = "";
   String posType = "";         // POS system type
   String adv_ampm = "";
   String mship1 = "";
   String period = "";         // membership limit periods (week, month or year)
   String advamd1 = "";
   String advamd2 = "";
   String advamd3 = "";
   String advamd4 = "";
   String advamd5 = "";
   String advamd6 = "";
   String advamd7 = "";

   String [] mship = new String [parm.MAX_Mships+1];        // membership types (golf, tennis, etc.)

   int mtimes = 0;              // membership limit number (number of rounds per period)
   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //    one per day of week (Sun - Sat) and per mship type
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int viewdays = 0;
   int advhrd1 = 0;
   int advhrd2 = 0;
   int advhrd3 = 0;
   int advhrd4 = 0;
   int advhrd5 = 0;
   int advhrd6 = 0;
   int advhrd7 = 0;
   int advmind1 = 0;
   int advmind2 = 0;
   int advmind3 = 0;
   int advmind4 = 0;
   int advmind5 = 0;
   int advmind6 = 0;
   int advmind7 = 0;
   int i = 0;
   int count = 0;
     
   boolean doDefault = false;


   // Get activity_id from session
   int sess_activity_id = (Integer)session.getAttribute("activity_id");

   //
   //  init the string array
   //
   for (i=0; i<parm.MAX_Mships+1; i++) {
      mship[i] = "";
   }    

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  See if we are in the timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
    
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   //
   // Get existing mships if they exist
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * FROM club5 WHERE clubName != ''");

      if (rs.next()) {
         posType = rs.getString("posType");
      }
      stmt.close();

      pstmt1 = con.prepareStatement("SELECT mship FROM mship5 WHERE activity_id = ?");
      pstmt1.clearParameters();
      pstmt1.setInt(1, sess_activity_id);

      rs = pstmt1.executeQuery();

      i = 1;

      while (rs.next()) {
          mship[i] = rs.getString("mship");
          i++;
      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Get the next mship index to display (do one at a time)
   //
   if (req.getParameter("index") != null) {

      index = req.getParameter("index");    // next mship to display

      try {
         i = Integer.parseInt(index);
      }
      catch (NumberFormatException e) {
         i = 99;    // done
      }
        
      if (i == 0) {         // if user selected to set default values (1st time through only)
        
         doDefault = true;
      }

   } else {

      //
      // first time here - put up a list of mship types for user to select one
      //
      if (mship[3].equals( "" )) {      // if not more than 2 mship types
          
         i = 1;                         // start with 1 (do in order)
  
      } else {   // prompt for mship type to process (or request default values)
        
         //
         //  Check if this is the very first time here (initial setup).  If so, prompt for default values to save time.
         //
         try {

            pstmt1 = con.prepareStatement (
                    "SELECT COUNT(*) FROM mship5 " +
                    "WHERE days1 > 0 OR days2 > 0 OR days3 > 0 OR days4 > 0 OR days5 > 0 OR days6 > 0 OR days7 > 0");

            pstmt1.clearParameters();        // clear the parms
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               count = rs.getInt("COUNT(*)");
            }
            pstmt1.close();
         }
         catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H1>Database Access Error</H1>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Please try again later.");
            out.println("<BR><br>Exception: " + exc.getMessage());
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         if (count == 0) {      // if first time here

            out.println(SystemUtils.HeadTitle("Mship Setup"));
            out.println("<BODY>");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Membership Type Options</H3>");
            out.println("<BR><BR>To reduce the amount of time required to complete the membership options,<BR>");
            out.println("you can set default values to be used for all membership types.<BR>");
            out.println("You can then override the defaults where ever necessary.<BR>");
            out.println("<BR>Would you like to set the default values?");

            out.println("<BR><BR>");
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_club?step5=yes&index=0\" target=\"bot\">");
            out.println("Yes, set the defaults.</a>");
             
            out.println("<BR><BR>");
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_club?step5=yes&index=1\" target=\"bot\">");
            out.println("No, go to first membership type.</a>");

            out.println("<BR><BR>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<font size=\"2\">");
            out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</font>");
            out.println("</CENTER></BODY></HTML>");
            return;

         } else {    // not very first time here - present a list of mship types
           
            out.println(SystemUtils.HeadTitle("Mship Setup"));
            out.println("<BODY>");
            SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Membership Type Options</H3>");
            out.println("<BR><BR>Select the Membership Type to Process.<BR>");

            for (i=1; i<parm.MAX_Mships+1; i++) {     // 1 form for each mship type
               mship1 = mship[i];
               if (!mship1.equals( "" )) {
                  out.println("<BR>");
                  out.println("<a href=\"/" +rev+ "/servlet/Proshop_club?step5=yes&mship=" +mship1+ "&index=" +i+ "\" target=\"bot\">");
                  out.println(mship1+ "</a>");
               }
            }
            out.println("<BR><BR>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<font size=\"2\">");
            out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }
   }

   //
   // Now get the mship parms for the mship type specified by index= (different table)
   //
   mship1 = "";                      // init

   if (doDefault == false) {      // if NOT setting default values
     
      loop1:
      while (i < parm.MAX_Mships+1) {                  // make sure there is a mship type at this index value

         if (!mship[i].equals( "" )) {     // if mship type specified

            mship1 = mship[i];             // save mship for later
            break loop1;                   // exit

         } else {

            i++;
         }
      }

      if (!mship1.equals( "" )) {                    // if mship type found

         try {

            pstmt1 = con.prepareStatement (
                    "SELECT * FROM mship5 WHERE mship = ? AND activity_id = " + sess_activity_id);

            pstmt1.clearParameters();        // clear the parms
            pstmt1.setString(1, mship[i]);
            rs = pstmt1.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               mtimes = rs.getInt("mtimes");
               period = rs.getString("period");
               days1 = rs.getInt("days1");
               days2 = rs.getInt("days2");
               days3 = rs.getInt("days3");
               days4 = rs.getInt("days4");
               days5 = rs.getInt("days5");
               days6 = rs.getInt("days6");
               days7 = rs.getInt("days7");
               advhrd1 = rs.getInt("advhrd1");
               advmind1 = rs.getInt("advmind1");
               advamd1 = rs.getString("advamd1");
               advhrd2 = rs.getInt("advhrd2");
               advmind2 = rs.getInt("advmind2");
               advamd2 = rs.getString("advamd2");
               advhrd3 = rs.getInt("advhrd3");
               advmind3 = rs.getInt("advmind3");
               advamd3 = rs.getString("advamd3");
               advhrd4 = rs.getInt("advhrd4");
               advmind4 = rs.getInt("advmind4");
               advamd4 = rs.getString("advamd4");
               advhrd5 = rs.getInt("advhrd5");
               advmind5 = rs.getInt("advmind5");
               advamd5 = rs.getString("advamd5");
               advhrd6 = rs.getInt("advhrd6");
               advmind6 = rs.getInt("advmind6");
               advamd6 = rs.getString("advamd6");
               advhrd7 = rs.getInt("advhrd7");
               advmind7 = rs.getInt("advmind7");
               advamd7 = rs.getString("advamd7");
               viewdays = rs.getInt("viewdays");
            }
            pstmt1.close();
         }
         catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H1>Database Access Error</H1>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Please try again later.");
            out.println("<BR><br>Exception: " + exc.getMessage());
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

      } else {             // done with all mship types

         out.println(SystemUtils.HeadTitle("Club Setup Done"));
         out.println("<BODY>");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<CENTER>");
         out.println("<BR><BR><H3>Membership Type Options Complete</H3>");
         out.println("<BR><BR>All Membership Types Have Been Processed.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //  Build the HTML page to solicit new parms - ***** DO ONE MSHIP AT A TIME ***************
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Club Setup"));
    out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<b>Club Setup - Membership Options</b><br>");
   out.println("<br>Click on the '?' for a description of each item.");
   out.println("<br>Click on 'Continue' to process the changes or 'Cancel' to exit without changes.");
   out.println("</font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<br><table border=\"2\" bgcolor=\"#F5F5DC\">");

   //
   //  This is step 5 - display the 5th page of options
   //
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_club\" method=\"post\" target=\"bot\" name=\"f\">");
   out.println("<input type=\"hidden\" name=\"step5\" value=\"yes\">");

   if (doDefault == true) {
      out.println("<input type=\"hidden\" name=\"doDefault\" value=\"yes\">");
   }

   out.println("<tr>");
      out.println("<td align=\"center\" valign=\"top\"><br><br><br>");
      out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_club_mship2.htm', 'newwindow', 'Height=340, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      out.println("?</a>");
      out.println("</td>");

      out.println("<td align=\"right\" valign=\"top\">");
         out.println("<font size=\"3\">");
           
         if (doDefault == false) {
            out.println("<p align=\"center\"><br><b>Options For " +mship1+ "</b></p>");
         } else {
            out.println("<p align=\"center\"><br><b>Default Options For All Membership Types</b></p>");
         }
         out.println("</font><font size=\"2\">");
         if (sess_activity_id == 0) {
            out.println("Specify the number of rounds per period that these &nbsp;&nbsp;");
            out.println("<br>membership types are allowed to play golf:&nbsp;&nbsp;");
            out.println("<br>(Specify 0 [zero] for no limit.)&nbsp;&nbsp;");
            out.println("<br><br><br>&nbsp;&nbsp;How many days in advance can these members <b>view tee sheets</b>?&nbsp;&nbsp;");
            out.println("<br>&nbsp;&nbsp;(Must be greater than, or equal to, the highest 'days in advance' value below.)&nbsp;&nbsp;");
            out.println("<br><br><br>&nbsp;&nbsp;How ");
            out.println("many <b>days in advance</b> can members reserve " + sysLingo.TEXT_tee_times + " &nbsp;&nbsp;");
         } else {
            out.println("Specify the number of times per period that these &nbsp;&nbsp;");
            out.println("<br>membership types are allowed to be part of a reservation:&nbsp;&nbsp;");
            out.println("<br>(Specify 0 [zero] for no limit.)&nbsp;&nbsp;");
            out.println("<br><br><br>&nbsp;&nbsp;How many days in advance can these members <b>view time sheets</b>?&nbsp;&nbsp;");
            out.println("<br>&nbsp;&nbsp;(Must be greater than, or equal to, the highest 'days in advance' value below.)&nbsp;&nbsp;");
            out.println("<br><br><br>&nbsp;&nbsp;How ");
            out.println("many <b>days in advance</b> can members book a reservation? &nbsp;&nbsp;");
         }
         out.println("<br>&nbsp;&nbsp;for each day of the week? (must be 0 - 365):&nbsp;&nbsp;");
         out.println("<br><br>");
         out.println("And what time of the day will this advance day become available. &nbsp;&nbsp;");
         out.println("<br>(enter 0 - 59 for minutes, Midnight = 12:00 AM) &nbsp;&nbsp;");
         out.println("</font>");

         out.println("</td><td align=\"left\"><font size=\"2\">");
            if (sess_activity_id == 0) {
               out.println("<br>&nbsp;&nbsp;&nbsp;<b>Max Rounds Allowed per Period</b>");
            } else {
               out.println("<br>&nbsp;&nbsp;&nbsp;<b>Max Reservations per Period</b>");
            }
             out.println("<div id=\"awmobject1\">");        // allow menus to show over this box
             out.println("<br>&nbsp;&nbsp;&nbsp; ");
              out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + mtimes + "\" name=\"mtimes\">");
            if (sess_activity_id == 0) {
               out.println(" Rounds per ");
            } else {
               out.println(" Reservations per ");
            }
             out.println("<select size=\"1\" name=\"period\">");
             if (period.equals ("Week")) {
                out.println("<option selected value=\"Week\">Week</option>");
             } else {
                out.println("<option value=\"Week\">Week</option>");
             }
             if (period.equals ("Month")) {
                out.println("<option selected value=\"Month\">Month</option>");
             } else {
                out.println("<option value=\"Month\">Month</option>");
             }
             if (period.equals ("Year")) {
                out.println("<option selected value=\"Year\">Year</option>");
             } else {
                out.println("<option value=\"Year\">Year</option>");
             }
           out.println("</select></div>");

         //
         //    Days in Adv to View Tee Sheets
         //
         if (viewdays == 0) {
            viewdays = 30;        // set default value
         }
         if (sess_activity_id == 0) {
            out.println("<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Days To View Tee Sheets</b>");
         } else {
            out.println("<br><br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Days To View Time Sheets</b>");
         }
         out.println("<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + viewdays + "\" name=\"viewdays\">");
           out.println(" <b>View Only</b> (1 - 365 days) ");

         //
         //  Membership n (do all)
         //
         //       Sunday
         //
         out.println("<br><br><br>&nbsp;&nbsp;&nbsp;&nbsp;<b>Days In Advance Options</b>");
         out.println("<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days1 + "\" name=\"days1\">");
           out.println(" <b>Sunday</b> (days in adv) ");
            out.println("<div id=\"awmobject2\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
            out.println("<select size=\"1\" name=\"advhrd1\">");
             if (advhrd1 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd1 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd1 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd1 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd1 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd1 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd1 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd1 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd1 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd1 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd1 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd1 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind1 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind1 + " name=\"advmind1\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind1 + " name=\"advmind1\">");
              }
              out.println("&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"advamd1\">");
             if (advamd1.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd1.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

         //
         //     Monday
         //
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days2 + "\" name=\"days2\">");
           out.println(" <b>Monday</b> (days in adv) ");
            out.println("<div id=\"awmobject3\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
            out.println("<select size=\"1\" name=\"advhrd2\">");
             if (advhrd2 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd2 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd2 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd2 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd2 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd2 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd2 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd2 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd2 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd2 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd2 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd2 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind2 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind2 + " name=\"advmind2\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind2 + " name=\"advmind2\">");
              }
              out.println("&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"advamd2\">");
             if (advamd2.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd2.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

         //
         //   Tuesday
         //
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days3 + "\" name=\"days3\">");
           out.println(" <b>Tuesday</b> (days in adv) ");
             out.println("<div id=\"awmobject4\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
             out.println("<select size=\"1\" name=\"advhrd3\">");
             if (advhrd3 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd3 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd3 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd3 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd3 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd3 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd3 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd3 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd3 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd3 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd3 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd3 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind3 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind3 + " name=\"advmind3\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind3 + " name=\"advmind3\">");
              }
              out.println("&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"advamd3\">");
             if (advamd3.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd3.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

         //
         //   Wednesday
         //
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days4 + "\" name=\"days4\">");
           out.println(" <b>Wednesday</b> (days in adv) ");
             out.println("<div id=\"awmobject5\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
             out.println("<select size=\"1\" name=\"advhrd4\">");
             if (advhrd4 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd4 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd4 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd4 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd4 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd4 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd4 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd4 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd4 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd4 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd4 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd4 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind4 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind4 + " name=\"advmind4\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind4 + " name=\"advmind4\">");
              }
              out.println("&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"advamd4\">");
             if (advamd4.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd4.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

         //
         //   Thursday
         //
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days5 + "\" name=\"days5\">");
           out.println(" <b>Thursday</b> (days in adv) ");
             out.println("<div id=\"awmobject6\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
             out.println("<select size=\"1\" name=\"advhrd5\">");
             if (advhrd5 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd5 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd5 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd5 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd5 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd5 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd5 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd5 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd5 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd5 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd5 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd5 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind5 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind5 + " name=\"advmind5\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind5 + " name=\"advmind5\">");
              }
              out.println("&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"advamd5\">");
             if (advamd5.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd5.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

         //
         //   Friday
         //
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days6 + "\" name=\"days6\">");
           out.println(" <b>Friday</b> (days in adv) ");
             out.println("<div id=\"awmobject7\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
             out.println("<select size=\"1\" name=\"advhrd6\">");
             if (advhrd6 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd6 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd6 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd6 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd6 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd6 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd6 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd6 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd6 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd6 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd6 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd6 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind6 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind6 + " name=\"advmind6\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind6 + " name=\"advmind6\">");
              }
              out.println("&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"advamd6\">");
             if (advamd6.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd6.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

         //
         //    Saturday
         //
         out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
           out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"" + days7 + "\" name=\"days7\">");
           out.println(" <b>Saturday</b> (days in adv) ");
             out.println("<div id=\"awmobject8\">");        // allow menus to show over this box
           out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("hr&nbsp;");
             out.println("<select size=\"1\" name=\"advhrd7\">");
             if (advhrd7 == 1) {
                out.println("<option selected selected value=\"01\">1</option>");
             } else {
                out.println("<option value=\"01\">1</option>");
             }
             if (advhrd7 == 2) {
                out.println("<option selected value=\"02\">2</option>");
             } else {
                out.println("<option value=\"02\">2</option>");
             }
             if (advhrd7 == 3) {
                out.println("<option selected value=\"03\">3</option>");
             } else {
                out.println("<option value=\"03\">3</option>");
             }
             if (advhrd7 == 4) {
                out.println("<option selected value=\"04\">4</option>");
             } else {
                out.println("<option value=\"04\">4</option>");
             }
             if (advhrd7 == 5) {
                out.println("<option selected value=\"05\">5</option>");
             } else {
                out.println("<option value=\"05\">5</option>");
             }
             if (advhrd7 == 6) {
                out.println("<option selected value=\"06\">6</option>");
             } else {
                out.println("<option value=\"06\">6</option>");
             }
             if (advhrd7 == 7) {
                out.println("<option selected value=\"07\">7</option>");
             } else {
                out.println("<option value=\"07\">7</option>");
             }
             if (advhrd7 == 8) {
                out.println("<option selected value=\"08\">8</option>");
             } else {
                out.println("<option value=\"08\">8</option>");
             }
             if (advhrd7 == 9) {
                out.println("<option selected value=\"09\">9</option>");
             } else {
                out.println("<option value=\"09\">9</option>");
             }
             if (advhrd7 == 10) {
                out.println("<option selected value=\"10\">10</option>");
             } else {
                out.println("<option value=\"10\">10</option>");
             }
             if (advhrd7 == 11) {
                out.println("<option selected value=\"11\">11</option>");
             } else {
                out.println("<option value=\"11\">11</option>");
             }
             if (advhrd7 == 12) {
                out.println("<option selected value=\"12\">12</option>");
             } else {
                out.println("<option value=\"12\">12</option>");
             }
           out.println("</select>");
           out.println("&nbsp;&nbsp;min&nbsp;");
              if (advmind7 < 10) {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=0" + advmind7 + " name=\"advmind7\">");
              } else {
                 out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + advmind7 + " name=\"advmind7\">");
              }
              out.println("&nbsp;&nbsp;");
             out.println("<select size=\"1\" name=\"advamd7\">");
             if (advamd7.equals ("AM")) {
                out.println("<option selected value=\"AM\">AM</option>");
             } else {
                out.println("<option value=\"AM\">AM</option>");
             }
             if (advamd7.equals ("PM")) {
                out.println("<option selected value=\"PM\">PM</option>");
             } else {
                out.println("<option value=\"PM\">PM</option>");
             }
           out.println("</select></div>");

      out.println("</font>");
      out.println("</td>");
   out.println("</tr>");
   out.println("</table>");

   out.println("<BR><table border=\"0\">");
   out.println("<tr>");
   out.println("<td align=\"center\" width=\"200\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"hidden\" name=\"mship\" value=\"" +mship1+ "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" +i+ "\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(submit changes & next page)");
   out.println("</font></td></form>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<td align=\"center\" width=\"200\">");
   out.println("<font size=\"1\">");
   out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("<br>(no changes & exit)");
   out.println("</font></td></form></tr></table>");
   out.println("</center></font></body></html>");

 }   // end of doStep 5

}
